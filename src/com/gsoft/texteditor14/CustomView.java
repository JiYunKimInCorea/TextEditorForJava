package com.gsoft.texteditor14;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
//import android.view.SurfaceHolder;
import android.view.View;

import com.gsoft.DataTransfer.pipe.Pipe;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Media.MediaPlayerDel.PlayListAndCurSongInfo;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.CommonGUI_SettingsDialog;
import com.gsoft.common.CompilerHelper;
//import com.gsoft.common.Notification;
import com.gsoft.common.PowerManagement;
import com.gsoft.common.ColorEx;
import com.gsoft.common.IO;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Terminal;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.ControlStack;
import com.gsoft.common.Util.PoolOfButton;
import com.gsoft.common.gui.Control;
import com.gsoft.common.gui.EditRichText;
import com.gsoft.common.gui.EditText;
import com.gsoft.common.gui.EditText_Compiler;
import com.gsoft.common.gui.FileDialog;
import com.gsoft.common.gui.IntegrationKeyboard;
import com.gsoft.common.gui.LoggingScrollable;
import com.gsoft.common.gui.MenuWithScrollBar;
import com.gsoft.common.gui.MessageDialog;
import com.gsoft.common.gui.SettingsDialog;
import com.gsoft.common.gui.SizingBorder;
import com.gsoft.common.gui.SizingBorderOfView;
import com.gsoft.common.gui.SizingBorderOfView.ResizeViewOrientation;
import com.gsoft.common.gui.ViewEx;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.EditRichText.TextLine;
import com.gsoft.common.gui.IntegrationKeyboard.Mode;
import com.gsoft.common.gui.Menu.MenuType;
import com.gsoft.common.gui.SettingsDialog.Settings;
import com.gsoft.common.R.R;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CustomView extends ViewEx 
	implements 	com.gsoft.common.interfaces.OnTouchListener
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long handlingTimeOfTouch;
	long time1OfTouch;
	long time2OfTouch;
	boolean isSaved;
	
	BufferedImage mImage2;
	Canvas mCanvas2;
	Graphics mGraphics2;
	
	ReadFileThread readFileThread;
	WriteFileThread writeFileThread;
	
	
	
	
	
	class CustomView1_OnTouchListener implements OnTouchListener {
		long oldActionTime;
		boolean isClicked;
		CustomView owner;
		Point oldMovePoint;
				
		public CustomView1_OnTouchListener(CustomView owner) {
			this.owner = owner;			
		}
		
		
		public boolean onTouch(View v, MotionEvent event) {
			//Control.modified = true;
			isSaved = false;
			int actionCode = event.getAction();
			int myActionCode = 0;
			if (actionCode==MotionEvent.ACTION_MOVE) {
				myActionCode = com.gsoft.common.Events.MotionEvent.ActionMove;
			}
			else if (actionCode==MotionEvent.ACTION_DOWN) {
				myActionCode = com.gsoft.common.Events.MotionEvent.ActionDown;
			}
			else if (actionCode==MotionEvent.ACTION_DOUBLE_CLICKED) {
				myActionCode = com.gsoft.common.Events.MotionEvent.ActionDoubleClicked;
			}
			else 
				myActionCode = com.gsoft.common.Events.MotionEvent.ActionUp;						
			
			try {
				if (myActionCode==com.gsoft.common.Events.MotionEvent.ActionDown ||
						myActionCode==com.gsoft.common.Events.MotionEvent.ActionDoubleClicked ) {
					com.gsoft.common.Events.MotionEvent myEvent = 
							new com.gsoft.common.Events.MotionEvent(myActionCode, (int)event.getX(), (int)event.getY());
					oldMovePoint = new Point(myEvent.x,myEvent.y);
					if (!sized) return false;
					
					java.awt.Point loc = owner.getLocation();
					Dimension size = owner.getSize();
					SizingBorderOfView.ResizeViewOrientation.TopLeftAbsolute.x = loc.x;
					SizingBorderOfView.ResizeViewOrientation.TopLeftAbsolute.y = loc.y;
					SizingBorderOfView.ResizeViewOrientation.RightBottomAbsolute.x = loc.x + size.width -1;
					SizingBorderOfView.ResizeViewOrientation.RightBottomAbsolute.y = loc.y + size.height -1;
					
					if (myActionCode==com.gsoft.common.Events.MotionEvent.ActionDoubleClicked) {
						int a;
						a=0;
						a++;
					}
					
					boolean r=false;	
					int i;
					ControlStack controlStack = Control.controlStack;
					
					//synchronized (Control.controlStack) {
					Control[] controls = controlStack.getItems();
					if (controls.length>0) {
						Control control = controls[controls.length-1];
						if (control instanceof com.gsoft.common.gui.Dialog) {
							if (control instanceof com.gsoft.common.gui.Dialog.EditableDialog==false) {
								if (control.IsPointIn(oldMovePoint)==false /*&& 
										integrationKeyboard.IsPointIn(oldMovePoint)==false*/) {
									CommonGUI.loggingForMessageBox.setText(true, "Touch the OK or Cancel button", false);
									CommonGUI.loggingForMessageBox.setHides(false);
									invalidate();
									return true;
								}
							}
						}
					}
					for (i=controls.length-1; i>=0; i--) {
						Control control = controls[i];
						if (control instanceof FileDialog) {
							int a;
							a=0;
							a++;
						}
						if (control!=null) {
							time1OfTouch = System.currentTimeMillis();
							if (exitTimer!=null) exitTimer.startTimer();
							r = control.onTouch(myEvent, null);
							if (exitTimer!=null) exitTimer.cancelTimer();
							if (r) {							
								break;
							}
						}
					}
					
					if (!r) {
						r = buttonMenu.onTouch(myEvent, null);
						
						if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
							// 윈도우즈(자바)에서만 윈도우 이동을 위해 캡쳐한다.
							// else if (myActionCode==com.gsoft.common.Events.MotionEvent.ActionMove)의
							// owner.setLocation(p.x+myEvent.x, p.y+myEvent.y);를 참조한다.
							if (r) {
								Control.capturedControl = buttonMenu;
							}
						}
						
						if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess &&
								Control.isMasterOrSlave) {
							if (!r) r = buttonLogBird.onTouch(myEvent, null);
							if (!r) r = buttonProcessClose.onTouch(myEvent, null);
						}
					}
					//}
					
					invalidate();
										
					/** editRichText, keyboard(내부에 editText가 있기 때문), sizingBorder는 
					// ActionDown에 연이은 AcitionMove를 받기 위해 여기서 true를 리턴해야 한다.*/
					return r;
				}
				
				else if (myActionCode==com.gsoft.common.Events.MotionEvent.ActionMove ||
						myActionCode==com.gsoft.common.Events.MotionEvent.ActionUp) {
					
					com.gsoft.common.Events.MotionEvent myEvent = 
							new com.gsoft.common.Events.MotionEvent(
							myActionCode, (int)event.getX(), (int)event.getY());					
					
					if (!sized) return false;
					
					//if (event.getEventTime()-event.getDownTime()<100) return false;
					
					boolean r=false;
					
					
					
					if (Control.capturedControl!=null) {
						if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
							// 윈도우즈(자바)에서만 윈도우 이동
							// if (myActionCode==com.gsoft.common.Events.MotionEvent.ActionDown) 의
							// buttonMenu를 캡쳐한 것을 참조한다.
							if (Control.capturedControl.iName==buttonMenu.iName) {
								java.awt.Point p = owner.getLocation();
								// oldMovePoint는 mouseDragged 시작시에 최초로 정해지고
								// 드래그가 계속될때는 변하지 않는다.
								int incX = myEvent.x-oldMovePoint.x;
								int incY = myEvent.y-oldMovePoint.y;
								
								owner.setLocation(p.x+incX, p.y+incY);
														
							}
						}
						r = Control.capturedControl.onTouch(myEvent, null);
						if (r) {
							owner.invalidate();
						}
					}
					
					if (myActionCode==com.gsoft.common.Events.MotionEvent.ActionUp) {
						Control.capturedControl = null;
					}
					
					//owner.invalidate();
					
					//Control.setModified(true);
					
					return r;
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			return true;
			
		}
		
		int oldActionEvent = -1;


		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			/*int a;
			a=0;
			a++;
			
			if (e.getWhen()-this.oldActionTime<1000) {
				MotionEvent me2 = new MotionEvent(
						e.getX()-owner.insets.left, 
						e.getY()-owner.insets.top, 
						MotionEvent.ACTION_DOUBLE_CLICKED, (int)e.getWhen());
				this.onTouch(owner, me2);
			}
			else {
				//int x, int y, int action, int eventTime
				MotionEvent me = new MotionEvent(
						e.getX()-owner.insets.left, 
						e.getY()-owner.insets.top, 
						MotionEvent.ACTION_DOWN, (int)e.getWhen());
				this.onTouch(owner, me);
				oldActionEvent = MotionEvent.ACTION_DOWN;
			}
			
			this.oldActionTime = e.getWhen();*/
		}


		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			int a;
			a=0;
			a++;
			
			// 더블클릭할 경우 처음 클릭 이벤트로 한번 ACTION_DOWN이 발생하고 
			// 두번째 클릭 이벤트에 의해 ACTION_DOUBLE_CLICKED 이 발생하게 된다.
			if (e.getWhen()-this.oldActionTime<500) {
				MotionEvent me2 = new MotionEvent(
						e.getX()-owner.insets.left, 
						e.getY()-owner.insets.top, 
						MotionEvent.ACTION_DOUBLE_CLICKED, (int)e.getWhen());
				this.onTouch(owner, me2);
			}
			else {
				//int x, int y, int action, int eventTime
				MotionEvent me = new MotionEvent(
						e.getX()-owner.insets.left, 
						e.getY()-owner.insets.top, 
						MotionEvent.ACTION_DOWN, (int)e.getWhen());
				this.onTouch(owner, me);
				oldActionEvent = MotionEvent.ACTION_DOWN;
			}
			
			this.oldActionTime = e.getWhen();
		}


		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			MotionEvent me = new MotionEvent(
					e.getX()-owner.insets.left, 
					e.getY()-owner.insets.top, 
					MotionEvent.ACTION_UP, (int)e.getWhen());
			this.onTouch(owner, me);
		}


		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			if (oldActionEvent!=MotionEvent.ACTION_MOVE) {
				mouseClicked(e);
			}
			MotionEvent me = new MotionEvent(
					e.getX(), 
					e.getY(), 
					MotionEvent.ACTION_MOVE, (int)e.getWhen());
			this.onTouch(owner, me);
			oldActionEvent = MotionEvent.ACTION_MOVE; 
		}


		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			/*MotionEvent me = new MotionEvent(
					e.getX(), 
					e.getY(), 
					MotionEvent.ACTION_MOVE, (int)e.getWhen());
			this.onTouch(owner, me);*/
			oldActionEvent = -1;
		}		
		
	}

	

	//private static final int REQUEST_ENABLE_BT = 1;
	
	float vertScaleOfGap = Control.vertScaleOfGap;
	float horzScaleOfGap = Control.horzScaleOfGap;
	
	boolean sized = false;
	
	float scaleOfMenuX = 1.0f - horzScaleOfGap * 2;
	float scaleOfMenuY = 0.055f;
	
	float scaleOfEditTextX = 1.0f - horzScaleOfGap * 2;
	float scaleOfEditTextY = 0.5f;
	
	float scaleOfKeyboardX = Control.scaleOfKeyboardX;
	float scaleOfKeyboardY = 1.0f - (vertScaleOfGap+scaleOfMenuY+
			vertScaleOfGap+scaleOfEditTextY+vertScaleOfGap);
	
	
	String name = "CustomView";
	//int width;
	//int height;
	
	Button buttonMenu;
	EditRichText editRichText;
	
	IntegrationKeyboard integrationKeyboard;
	
	SizingBorder sizingBorder;
	Paint paint = new Paint();
	
	ContextMenu menu;
	
	FileDialog fileDialog;
	String filename;
	
	
	
	
	private Rectangle boundsOfButtonMenu;
	
	/** EditRichText는 toolbar 영역을 제외한 너비 즉 editRichText.bounds와 같고, 
	 * EditText도 역시 toolbar 영역을 제외한 너비 즉 editText.bounds와 같다.*/
	private Rectangle boundsOfEditText;
	/** EditRichText는 toolbar 영역을 제외한 너비 즉 editRichText.bounds와 같고, 
	 * EditText도 역시 toolbar 영역을 제외한 너비 즉 editText.bounds와 같다.*/
	private Rectangle boundsOfEditRichText;
	private Rectangle boundsOfIntegrationKeyboard;
	private Rectangle boundsOfMenu;
	private Rectangle boundsOfSizingBorder;
	private Rectangle boundsOfLoggingForMessageBox;
	private Rectangle boundsOfLoggingForNetwork;
	private int heightOfGap;
	private int widthOfGap;
	private Rectangle boundsOfFileDialog;
	Rectangle boundsOfsizingBorderOfView;
	Rectangle boundsOfTerminal;
	
	/** editRichText:0, editText:1, terminal:2*/
	int isEditRichTextOrEditText = 1;
	
	
	
	
	/** backupContents와 restoreContents에서 읽고 쓰는 text
	 * savedText는 editRichText, strSavedText는 editText를 대상으로 한다.*/
	TextLine savedText;
	private String strSavedText;
	
	private EditText_Compiler editText;
	//private boolean isMaximized;
	
	
	
	
	
	//SurfaceHolder mHolder;
	Canvas mCanvas;
	public synchronized void setRunning(boolean b) {
	}
	private Thread renderThread;
	
	
	
	//ControlInfo controlInfo;
	
	int refreshTime = 100;
	private String[] mPlayList;
	private PlayListAndCurSongInfo playListAndCurSongInfo;
	
	private Terminal terminal;
	//private SettingsDialog settingsDialog;
	
	
	SizingBorderOfView sizingBorderOfView;
	
	/** 매스터로 동작할 경우에만 사용*/
	EditText editTextDocumentPath;
	
	/** 매스터로 동작할 경우에만 사용, menuTabList의 버튼 사이즈로 사용*/
	Size mButtonSize;
	
	/** 매스터로 동작할 경우에만 사용*/
	MenuWithScrollBar menuTabList;
	
	/** 매스터로 동작할 경우에만 사용, menuTabList의 버튼으로 사용*/
	PoolOfButton poolOfTabListButtons;
	
	/** 이전에 종료시 finish()를 호출했으면 1을 로드하고,
	 *  아니면 0을 로드한다. backup_IsFinishingWhenExitingPrevly에서 로드한다.*/
	private int isFinishingWhenExitingPrev;
	private Button buttonLogBird;
	private Button buttonProcessClose;
	
	
	
	
	
	
	public CustomView(MainActivity activity, Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setOnTouchListener(new CustomView1_OnTouchListener(this));
		
		Control.view = this;
		Control.viewEx = this;
		
		this.setTitle("Java Editor");
		
		
		
		//Control.canFinish = false;
		
			
		/*mHolder = getHolder();
		mHolder.addCallback(this);*/
		
		/*long l = 1456789012L;
		byte[] b =IO.toBytes(l);
		long l2 = IO.toLong(b);
		
		long l3 = 6456789012L;
		byte[] b2 =IO.toBytes(l3);
		long l4 = IO.toLong(b2);
		
		long l5 = 10456789012L;
		byte[] b3 =IO.toBytes(l5);
		long l6 = IO.toLong(b3);
		
		int i = 123456;
		byte[] b4 = IO.toBytes(i);
		int i2 = IO.toInt(b4);
		
		
		char c1 = '가';
		short code1 = (short)c1;		
		int iCode1 = ~(code1+1);
		byte[] buf1 = IO.toBytes(code1);
		buf1[0] = (byte)(0xac & 0xff);
		buf1[1] = (byte)(0x00 & 0xff);
		String s1 = new String(buf1);
		
		
		char c2 = (char)44032;
		char[] a2 = {c2};
		String s2 = new String(a2);
		
		byte[] buf3 = new byte[2];
		buf3[0] = (byte)(0x00 & 0xff);
		buf3[1] = (byte)(0xac & 0xff);
		int i3 = IO.toInt(buf3);
		char c3 = IO.toChar(buf3);
		char[] a3 = {c3};
		String s3 = new String(a3);*/
		
		
		//byte[] buf1 = new String("가").getBytes();
		//byte[] buf2 = new String("나").getBytes();
		
		/*byte[] buf3 = new String("다").getBytes();
		byte[] buf4 = new String("라").getBytes();
		
		char[] indices = {128, 0x07bf, 0x0900, 0xfffd};
		
		int i;
		for (i=0; i<indices.length; i++) {
			char c = indices[i];
			char[] cc = {c};
		
			String s = new String(cc);
			byte[] buf5 = s.getBytes();
			if (buf5.length==4) {
				break;
			}
		}
		
		char c = 0x0900;
		char[] cc = {c};
	
		String s = new String(cc);
		byte[] buf6 = s.getBytes();
		
		// 1byte : 0이상 127이하
		// 2bytes : 128이상(2바이트널)-0x07bf(Thaana) : (-,-)
		//		(-62,-128)			(-34,-65)
		// 3bytes : 0x0900이상(Devanagari)-0xfffd(Specials)
		//		  (-32,-92,-128)			(-17,-65,-67)*/
		
		
		
		//Font.loadHangul(getContext());
		
	}
		
	
	

	void changeBounds(int width, int height) {
		if (sized==false) {
			heightOfGap = (int)(height * vertScaleOfGap);
			widthOfGap = (int)(width * horzScaleOfGap);
		}
		
	
		if (boundsOfButtonMenu==null)
			boundsOfButtonMenu = new Rectangle(insets.left+widthOfGap, insets.top+heightOfGap, 
				(int)(width*scaleOfMenuX), (int)(height*scaleOfMenuY));
		else {
			boundsOfButtonMenu.width = width - 2 * widthOfGap;
		}
		//if (buttonMenu!=null) buttonMenu.changeBounds(boundsOfButtonMenu);
		
		if (boundsOfEditText==null)
			boundsOfEditText = new Rectangle(insets.left+widthOfGap, 
				boundsOfButtonMenu.bottom()+heightOfGap, 
				(int)(width*scaleOfEditTextX), (int)(height*scaleOfEditTextY));
		else {
			boundsOfEditText.x = editText.bounds.x;
			boundsOfEditText.y = editText.bounds.y;
			boundsOfEditText.width = width - 2 * widthOfGap - editText.toolbar.bounds.width;
			if (this.editText.isMaximized()) {
				boundsOfEditText.height = height - editText.bounds.y;
			}
			else {
				boundsOfEditText.height = (int)(height*scaleOfEditTextY);
			}
		}
		//if (editRichText!=null) editRichText.changeBounds(boundsOfEditText);
		
		if (boundsOfEditRichText==null)
			boundsOfEditRichText = new Rectangle(boundsOfEditText);
		else {
			boundsOfEditRichText.x = editRichText.bounds.x;
			boundsOfEditRichText.y = editRichText.bounds.y;
			boundsOfEditRichText.width = width - 2 * widthOfGap - editRichText.toolbar.bounds.width;
			if (this.editRichText.isMaximized()) {
				boundsOfEditRichText.height = height - editRichText.bounds.y;
			}
			else {
				boundsOfEditRichText.height = (int)(height*scaleOfEditTextY);
			}
		}
		
		if (boundsOfTerminal==null)
			boundsOfTerminal = new Rectangle(insets.left+widthOfGap, 
				boundsOfButtonMenu.bottom()+heightOfGap, 
				(int)(width*scaleOfEditTextX), (int)(height*scaleOfEditTextY));
		else {
			if (terminal!=null) {
				boundsOfTerminal.x = terminal.editText.bounds.x;
				boundsOfTerminal.y = terminal.editText.bounds.y;
				boundsOfTerminal.width = width - 2 * widthOfGap - terminal.editText.toolbar.bounds.width;
				if (terminal.editText.isMaximized()) {
					boundsOfTerminal.height = height - terminal.editText.bounds.y;
				}
				else {
					boundsOfTerminal.height = (int)(height*scaleOfEditTextY);
				}
			}
		}
		
		//Control.prevSizeTotalConstant = new RectangleF(boundsOfEditText); 
		int x, y, w, h;
		
		if (boundsOfIntegrationKeyboard==null) {
			boundsOfIntegrationKeyboard = new Rectangle(insets.left, 
				boundsOfEditText.bottom() + heightOfGap,
				(int)(width*scaleOfKeyboardX), (int)(height*scaleOfKeyboardY));
		}
		else {
			boundsOfIntegrationKeyboard.x = 0;
			boundsOfIntegrationKeyboard.y = boundsOfEditText.bottom() + heightOfGap;
			boundsOfIntegrationKeyboard.width = width;
			boundsOfIntegrationKeyboard.height = (int)(height*scaleOfKeyboardY);
			if (boundsOfIntegrationKeyboard.height<0) {
				int a;
				a=0;
				a++;
			}
		}
		//if (integrationKeyboard!=null) integrationKeyboard.changeBounds(boundsOfIntegrationKeyboard);
		
		if (width>1000) {
			int a;
			a=0;
			a++;
		}
		
		// ContextMenu
		
		w = (int) (width * 0.7f);
		h = (int) (height * 0.95f);
		x = insets.left+ width / 2 - w / 2;
		y = insets.top + height / 2 - h / 2;
		if (boundsOfMenu==null) boundsOfMenu = new Rectangle(x, y, w, h);
		else {
			boundsOfMenu.x = x;
			boundsOfMenu.y = y;
			boundsOfMenu.width = w;
			boundsOfMenu.height = h;
		}
		//if (menu!=null) menu.changeBounds(boundsOfMenu);
		
		
		if (boundsOfFileDialog==null) {
			w = width;
			h = boundsOfEditText.height;
			x = insets.left;
			y = boundsOfEditText.y;
			boundsOfFileDialog = new Rectangle(x, y, w, h);
		}
		else {
			boundsOfFileDialog.x = 0;
			boundsOfFileDialog.y = 0;
			boundsOfFileDialog.width = width;
			boundsOfFileDialog.height = height;
		}
		//if (fileDialog!=null) fileDialog.changeBounds(boundsOfFileDialog);
		
						
		Rectangle boundsOfEditText2 = boundsOfEditText;
		if (boundsOfSizingBorder==null) boundsOfSizingBorder = new Rectangle(boundsOfEditText2.x, boundsOfEditText2.bottom()+1, 
				boundsOfEditText2.width+1, 3);
		//boundsOfSizingBorder.height = integrationKeyboard.buttons[0].bounds.y - boundsOfSizingBorder.y;
		//if (sizingBorder!=null) sizingBorder.changeBounds(boundsOfSizingBorder);
			
		
		if (boundsOfLoggingForMessageBox==null) boundsOfLoggingForMessageBox = new Rectangle(insets.left, insets.top, (int)(width*0.9f), (int)(height*0.75f));
		else {
			boundsOfLoggingForMessageBox.width = (int)(width*0.9f);
			boundsOfLoggingForMessageBox.height = (int)(height*0.75f);
		}
		boundsOfLoggingForMessageBox.x = (width-boundsOfLoggingForMessageBox.width) / 2;
		boundsOfLoggingForMessageBox.y = (height-boundsOfLoggingForMessageBox.height) / 2;
		
		if (boundsOfLoggingForNetwork==null) boundsOfLoggingForNetwork = new Rectangle(insets.left, insets.top, (int)(width*0.9f), (int)(height*0.85f));
		else {
			boundsOfLoggingForNetwork.width = (int)(width*0.9f);
			boundsOfLoggingForNetwork.height = (int)(height*0.85f);
		}
		boundsOfLoggingForNetwork.x = (width-boundsOfLoggingForNetwork.width) / 2;
		boundsOfLoggingForNetwork.y = height-boundsOfLoggingForNetwork.height;
		//if (Control.loggingForNetwork!=null) Control.loggingForNetwork.changeBounds(boundsOfLoggingForNetwork);
		
		
		/*if (boundsOfTerminal==null)
			boundsOfTerminal = new Rectangle(widthOfGap, 
				boundsOfButtonMenu.bottom()+heightOfGap, 
				(int)(width*scaleOfEditTextX), (int)(height*scaleOfEditTextY));
		else {
			//boundsOfTerminal.width = width - 2 * widthOfGap;
			boundsOfTerminal.x = 0;
			boundsOfTerminal.y = 0;
			boundsOfTerminal.width = width;
			boundsOfTerminal.height = height;
		}*/
		
		if (boundsOfsizingBorderOfView==null)
			boundsOfsizingBorderOfView = new Rectangle(0, 0, Control.view.getWidth(), Control.view.getHeight());
		else {
			boundsOfsizingBorderOfView.x = 0;
			boundsOfsizingBorderOfView.y = 0;
			boundsOfsizingBorderOfView.width = width;
			boundsOfsizingBorderOfView.height = height;
		}
	}
	
	
		
	protected void initControls() throws Exception {
		try {
		super.initControls();
		
		this.backColor = Color.LTGRAY;
		
		changeBounds(this.width, this.height);
		
		/*if (settings==null) {
			settings = CommonGUI_SettingsDialog.settings;
		}
		
		int selectedColor;
		if (settings!=null) {
			selectedColor = settings.selectedColor[0];
			CommonGUI_SettingsDialog.settingsDialog.setSelectedColorOfEditText(selectedColor);
			CommonGUI_SettingsDialog.settingsDialog.setSelectedColorOfKeyboard(settings.selectedColor[1]);
			Control.pathAndroid = settings.pathAndroid;
			Control.pathAndroid = Control.pathAndroid.replace('/', File.separatorChar);
			CommonGUI_SettingsDialog.settingsDialog.editTextDirectory.setText(0, new CodeString(Control.pathAndroid+File.separator,Color.BLACK));
		}*/
		
		
		
		sizingBorderOfView = new SizingBorderOfView(this, boundsOfsizingBorderOfView, this);
		sizingBorderOfView.setHides(false);
		
		
		int alpha = 255;
		int colorOfButton;
		
				
		colorOfButton = ColorEx.darkerOrLighter(Color.WHITE, -30);
		buttonMenu = new Button(this, "ButtonMenu", "Menu", 
				colorOfButton, boundsOfButtonMenu, false, alpha, true, 0.0f, null, Color.LTGRAY);
		buttonMenu.setOnTouchListener(this);
		buttonMenu.setIsOpen(true);
		//Control.controlStack.add(buttonMenu);
				
		try {
		colorOfButton = settings.selectedColor[1];
		}catch(Exception e) {
			int a;
			a=0;
			a++;
		}
		
		
		integrationKeyboard = new IntegrationKeyboard(this, boundsOfIntegrationKeyboard, Mode.Math, 
				widthOfGap, heightOfGap, colorOfButton, alpha);		
		//Control.controlStack.add(integrationKeyboard);
		CommonGUI.keyboard = integrationKeyboard;
		//Control.prevSizeOfKeyboardConstant = new RectangleF(boundsOfIntegrationKeyboard); 
		
		//controlInfo.listOfControlsInContainer.add(integrationKeyboard);
		
		
		
		String text = null/*"가나다라마바사아자차카타파하\nㄱㄴㄷㅂㅅㅇㅈㅊㅋㅌㅍㅎ"*/;
		float fontSize = height * 0.05f;
		
		
			//restoreContents()에서 읽어들인 텍스트를 설정한다.
			editRichText = new EditRichText(this, "EditText", boundsOfEditRichText, fontSize, false, 
					text, EditRichText.ScrollMode.Both);
			editRichText.setBackColor(settings.selectedColor[0]);
			
			if (isFinishingWhenExitingPrev==0) {
				if (savedText!=null) editRichText.setText(0, savedText);
				else editRichText.setText(0, null);
			}
			
			//restoreContents()에서 읽어들인 텍스트를 설정한다.
			editText = new EditText_Compiler(true, false, this, "EditText", boundsOfEditText, 
					fontSize, false, new CodeString(text, Color.BLACK), 
					EditText.ScrollMode.Both, Color.WHITE);
			editText.setBackColor(settings.selectedColor[0]);
			
			if (isFinishingWhenExitingPrev==0) {
				if (strSavedText!=null) editText.setText(0, new CodeString(strSavedText,editText.textColor));
				else editText.setText(0, null);
			}
			
			
			
			//Control.prevSizeConstant = new RectangleF(editText.bounds); 
			//Control.prevSizeTotalConstant = new RectangleF(editText.totalBounds);
			
			//isEditRichTextOrEditText = getIsEditRichTextOrEditText_Correct();
			isEditRichTextOrEditText = 1;
			
			if (isEditRichTextOrEditText==0) {				
				editRichText.setHides(false);
				integrationKeyboard.setOnTouchListener(editRichText);
				
			}
			else if (isEditRichTextOrEditText==1){
				editText.setHides(false);
				integrationKeyboard.setOnTouchListener(editText);
				
			}
			else {				
				isEditRichTextOrEditText = 2;
				if (terminal==null) {
					int color = CommonGUI_SettingsDialog.settings.selectedColor[0];
					terminal = new Terminal(boundsOfTerminal, color);
					terminal.setHides(false);
					integrationKeyboard.setOnTouchListener(terminal);
				}
			}
			if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
				integrationKeyboard.setHides(false);
			}
		
		menu = new ContextMenu("menu", boundsOfMenu, MenuType.Vertical, this, 
				ContextMenu.textsOfButtons, new Size(5,5), true, this);
		
		menu.buttons[4].selectable = true;	// Maximize/PrevSize 메뉴는 토글로 동작한다.
		menu.buttons[4].toggleable = true;
		menu.buttons[4].ColorSelected = Color.YELLOW;
		
		menu.buttons[3].selectable = true;	// Sizable 메뉴는 토글로 동작한다.
		menu.buttons[3].toggleable = true;
		menu.buttons[3].ColorSelected = Color.YELLOW;
		
		menu.buttons[5].selectable = true;	// EditRichText/EditText 메뉴는 토글로 동작한다.
		menu.buttons[5].toggleable = true;
		menu.buttons[5].ColorSelected = Color.YELLOW;
		
		//controlInfo.listOfControlsInContainer.add(menu);
				
		
		paint.setStyle(Style.FILL);
		paint.setColor(Color.MAGENTA);
		
		
		//boundsOfSizingBorder = new RectangleF(boundsOfEditText2.x, boundsOfEditText2.bottom()+1, 
		//		boundsOfEditText2.width, 3);
		boundsOfSizingBorder.height = integrationKeyboard.buttons[0].bounds.y - boundsOfSizingBorder.y;
		
		sizingBorder = new SizingBorder(this, boundsOfSizingBorder, this);
		//Control.controlStack.add(sizingBorder);
		//boundsOfSizingBorder.height = integrationKeyboard.buttons[0].bounds.y - boundsOfSizingBorder.y; 
		//sizingBorder.setBoundsDrawing(boundsOfSizingBorder);
		Control.sizingBorder = sizingBorder;
		
		//backUpBounds();
		
		
		CommonGUI.loggingForNetwork = new LoggingScrollable(this, boundsOfLoggingForNetwork, 
				"", EditText.ScrollMode.VScroll, height*0.035f);
		
		// 텍스트색상을 남색으로 설정
		CommonGUI.loggingForNetwork.setTextColor(ColorEx.darkerOrLighter(Color.BLUE, 0.5f));
		
		CommonGUI.loggingForMessageBox = new LoggingScrollable(this, boundsOfLoggingForMessageBox, 
				"", EditText.ScrollMode.VScroll, height*0.035f);
		
		
		createFileDialog();
		CommonGUI.fileDialog = fileDialog;
		
		
		editRichText.setMaximized(true);
		editText.setMaximized(true);
		
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		/*Intent loadIntent = Control.activity.getIntent(); 
		if (loadIntent!=null) {
			Uri uri = loadIntent.getData();
			if (uri!=null) {
				String filenameLoaded = uri.getPath();
				readFile(filenameLoaded);
				int count;
				count = Control.controlStack.count;
			}
		}*/
		
		// master 일 경우에만 실행되는 sub 프로세스들 관리 화면
		if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
			if (Control.isMasterOrSlave) {
				this.editRichText.setHides(true);
				this.editText.setHides(true);
				this.integrationKeyboard.setHides(true);
				this.sizingBorder.setHides(true);
				
				float scaleOfDocumentPathY = 0.1f;
				float scaleOfButtonLogBirdY = 0.07f;
				float scaleOfTabListY = 1 - (scaleOfMenuY + scaleOfDocumentPathY + 
						scaleOfButtonLogBirdY + 5 * vertScaleOfGap);
				
				
				// 영역만 잡아놓고 나중에 Button[]을 넣어준다.
				Rectangle boundsOfTabList = new Rectangle(widthOfGap, 
						this.buttonMenu.bounds.bottom()+heightOfGap, 
						(int)(this.width-2*widthOfGap), (int)(this.height*scaleOfTabListY));
				createTabList(boundsOfTabList);
				
				
				Rectangle boundsOfDocumentPath = new Rectangle(widthOfGap, 
						boundsOfTabList.bottom()+heightOfGap, 
						(int)(this.width-2*widthOfGap), (int)(this.height*scaleOfDocumentPathY));
				editTextDocumentPath = new EditText(false, false, this, "DocumentPath", boundsOfDocumentPath, 
						boundsOfDocumentPath.height*0.5f, true, 
						new CodeString("", Color.BLACK), 
						EditText.ScrollMode.Both, Color.WHITE);
				editTextDocumentPath.isReadOnly = true;
				editTextDocumentPath.setHides(false);
				
				int x, y, w, h, gapX;
				w = (int) (width * 1/(5.0f));
				gapX = w;
				h = (int) (height * scaleOfButtonLogBirdY);
				x = gapX;
				y = editTextDocumentPath.bounds.bottom() + heightOfGap;
				Rectangle boundsOfLogBird = new Rectangle(x,y,w,h); 
				buttonLogBird = new Button(this, "", "LogBird", Color.LTGRAY, 
						boundsOfLogBird, false, 255, true, 0.0f, null, Color.CYAN);
				buttonLogBird.setOnTouchListener(this);
				
				//w = (int) (bounds.width * 1/(7.0f));
				//h = heightOfButton;
				x = boundsOfLogBird.right() + gapX;
				Rectangle boundsOfProcessClose = new Rectangle(x,y,w,h); 
				buttonProcessClose = new Button(this, "", "Process Close", Color.LTGRAY, 
						boundsOfProcessClose, false, 255, true, 0.0f, null, Color.CYAN);
				buttonProcessClose.setOnTouchListener(this);
			}
		}//if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
		
		super.afterInitControls();
		
		CommonGUI_SettingsDialog.showsCopyRight();
		
	}
	
	/** 영역만 잡아주고 menuFileList의 내용(Button[])은 나중에 createAndSetFileListButtons를
	 * 통해 넣어준다.
	 */
	private void createTabList(Rectangle boundsOfMenuTabList) {
		if (menuTabList==null) {
			mButtonSize  = new Size((int)(this.getWidth()*0.35f),(int)(this.getHeight()*0.06f));
			
			menuTabList = new MenuWithScrollBar(this, boundsOfMenuTabList, 
					mButtonSize, 
					MenuWithScrollBar.ScrollMode.VScroll);
			menuTabList.setOnTouchListener(this);
			menuTabList.setHides(false);
			
			createPoolOfTabListButtons();
		}
	}
	
	Button[] getFileListButtons(String[] fileList) {
		//try {
		int buttonCount;
		int i;
		buttonCount = 0;
		
		for (i=0; i<fileList.length; i++) {
			if (fileList[i]!=null) buttonCount++;
		}
		
		int buttonWidth = menuTabList.originButtonWidth;
		int buttonHeight = menuTabList.originButtonHeight;
		if (poolOfTabListButtons.list.capacity<buttonCount) {
			poolOfTabListButtons.setCapacity(buttonCount, mButtonSize);
		}
		//String debugMsg = "buttonCount:"+buttonCount;
		
		
		Button[] buttons = new Button[buttonCount];
		
		int k=0;
		for (i=0; i<fileList.length; i++) {
			if (fileList[i]==null) continue;
			
			String path = fileList[i];
			String shortPath = FileHelper.getFilename(path);
			buttons[k] = (Button) poolOfTabListButtons.getItem(k);
			buttons[k].owner = this.menuTabList;
			buttons[k].isManualOrAutoSize = false;
			buttons[k].name = shortPath;
			buttons[k].bounds.x = 0;
			buttons[k].bounds.y = 0;
			buttons[k].bounds.width = buttonWidth;
			buttons[k].bounds.height = buttonHeight;			
			buttons[k].setBackColor(Color.BLUE);
			buttons[k].selectable = false;
			buttons[k].toggleable = false;
			buttons[k].isSelected = false;
			//buttons[k].changeBounds(buttons[k].bounds);
			buttons[k].setText(shortPath);
			// Pipe.listOfPathOfOpenProcesses, Pipe.listOfSockets, 
			// Pipe.listOfSocketStream의 인덱스이다.
			buttons[k].addedInfo = String.valueOf(i);
			buttons[k].setOnTouchListener(this);
			k++;
		}
		return buttons;
	}
	
	/** TabListButtons 의 Pool을 활용하여 디렉토리를 바꿀 때마다 버튼들을 생성하지 않고 메모리를 절약한다.
	 * 즉 디렉토리를 바꾸면 버튼들을 새로 만드는 것이 아니라 pool에서 가져와서 버튼의 속성만 바꿔준다.
	 * (createFileListButtons참조)*/
	void createPoolOfTabListButtons() {
		if (poolOfTabListButtons==null) {
			poolOfTabListButtons = new PoolOfButton(10, mButtonSize);
		}
	}
	
	
	
		
	void createFileDialog() {
		try {
		fileDialog = new FileDialog(this, boundsOfFileDialog, null);
		//fileDialog.setOnTouchListener(this);
		
		if (playListAndCurSongInfo==null) return;
		if (this.playListAndCurSongInfo.state.equals("Play_")) {
			if (mPlayList!=null && mPlayList.length>0) {
				fileDialog.isFullScreen = true;
				fileDialog.canSelectFileType = true;
				fileDialog.setScaleValues();
				fileDialog.changeBounds(new Rectangle(0,0,getWidth(),getHeight()));
				
				fileDialog.createAndSetFileListButtons(fileDialog.curDir, FileDialog.Category.All);
				integrationKeyboard.setHides(true);
				
				fileDialog.open(this, "FileExplorer");
				
				ArrayListString fileList = new ArrayListString(mPlayList.length);
				int i;
				for (i=0; i<mPlayList.length; i++) {
					fileList.add(mPlayList[i]);
				}
				// 음악을 곧바로 재생한다.
				fileDialog.listenToMusic(fileList, this.playListAndCurSongInfo);
			}
		}
		else {
			if (mPlayList!=null && mPlayList.length>0) {
				ArrayListString fileList = new ArrayListString(mPlayList.length);
				int i;
				for (i=0; i<mPlayList.length; i++) {
					fileList.add(mPlayList[i]);
				}
				// 음악을 곧바로 재생하지 않고 나중에 재생할 수 있다.
				fileDialog.setPlayListAndCurSongInfo(fileList, this.playListAndCurSongInfo);
			}
			
		}
		}catch(Exception e) {
			Log.e("createFileDialog", e.toString());
			e.printStackTrace();
		}
	}
	
	
	
	void changeBounds(Rectangle boundsOfEditText) {
		int heightOfGap = (int)(height * vertScaleOfGap);
		int top = boundsOfEditText.bottom()+heightOfGap;
		Rectangle boundsOfIntegrationKeyboard = new Rectangle(0, top,
				(int)(width*scaleOfKeyboardX), height-top);
		integrationKeyboard.changeBounds(boundsOfIntegrationKeyboard);
		
		boundsOfSizingBorder.y = boundsOfEditText.bottom() + 1;
		boundsOfSizingBorder.height = integrationKeyboard.buttons[0].bounds.y - boundsOfSizingBorder.y;
		sizingBorder.bounds = boundsOfSizingBorder;
		
	}
	
	public void setSize(int width, int height) {
		
		super.setSize(width, height);
		this.width = width;
		this.height = height;
		createBufferedImageAndCanvas(width, height);
	}
	
	void createBufferedImageAndCanvas(int width, int height) {
		// 더블 버퍼링
		mImage2 = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB); 
		Graphics gForImage = mImage2.getGraphics();
		Paint.g = gForImage;
		mCanvas2 = new Canvas(gForImage);
	}
	
	/** 자바에서만 더블 버퍼링*/
	public void paint(Graphics g) {
		if (mImage2==null) {
			createBufferedImageAndCanvas(width, height);			
		}
		// mImage2위에 그린다.
		onDraw(mCanvas2);
		// 그려진 mImage2를 g에 그린다.
		g.drawImage(mImage2, 0, 0, null);
		
		/*if (mGraphics2==null) mGraphics2 = g.create();
		Paint.g = mGraphics2;
		if (mCanvas2==null) mCanvas2 = new Canvas(mGraphics2);
		onDraw(mCanvas2);*/
	}
	
	public void onDraw(Canvas canvas) {	
		if (!sized) {
			width = getWidth();
			height = getHeight();
			
			try {
				
				Control.viewEx.sized();
				initControls();
				
				sized = true;
				
				//canvas.setBitmap(mBitmap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CompilerHelper.printStackTrace(new File(Control.ErrorLogFilePath), e);
			}			
		}
		super.onDraw(canvas);
		try {
			int i;
			
			
			
			buttonMenu.draw(canvas);
			if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess &&
					Control.isMasterOrSlave) {
				buttonLogBird.draw(canvas);
				buttonProcessClose.draw(canvas);
			}
			
			//synchronized (Control.controlStack) {
				for (i=0; i<Control.controlStack.count; i++) {
					if (Control.controlStack.count>2) {
						int a;
						a=0;
						a++;
					}
					Control control;
					try {
					control = Control.controlStack.getItem(i);
					if (control!=null) {
						control.draw(canvas);
					}
					}catch(Exception e) {
						//e.printStackTrace();
					}
				}
			//}
			
		//this.validate();
				
				super.afterOnDraw(canvas);
							
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(new File(Control.ErrorLogFilePath), e);
		}
	}
	
	public void resume() {
		//restoreContents();
		//Control.showLogForMessageBox(true, "Resume()");
		time1OfTouch = 0;
		if (fileDialog!=null && fileDialog.mediaPlayer!=null) {
			fileDialog.mediaPlayer.resume();
		}
		//exitTimer.startTimer();
		
		//setRunning(true);
		//Control.setModified(true);
		/*renderThread = new Thread(this);
		renderThread.start();*/
		
		/*if (fileExplorerDialog!=null && fileExplorerDialog.mediaPlayer!=null) {
			fileExplorerDialog.mediaPlayer.resume();
		}*/
		
		
	}
	
	@Override
	public void pause() {
		time1OfTouch = 0;
		if (exitTimer!=null) exitTimer.cancelTimer();
		//this.backupContents();
		
	}
	
		
	void drawLogging(Canvas canvas) {
		if (CommonGUI.loggingForNetwork.getHides()==false) {
			CommonGUI.loggingForNetwork.draw(canvas);
		}
		if (CommonGUI.loggingForMessageBox.getHides()==false) {
			CommonGUI.loggingForMessageBox.draw(canvas);
		}
	}
		
	/*public boolean onTouch(com.gsoft.common.Events.MotionEvent event, SizeF scaleFactor) {
    	//if (hides==true || IsPointIn(new Point(event.x, event.y))==false) return false;
    	
    	if (event.actionCode==com.gsoft.common.Events.MotionEvent.ActionDown) {
    	}
    	else if (event.actionCode==com.gsoft.common.Events.MotionEvent.ActionMove) {
    	}
    	else if (event.actionCode==com.gsoft.common.Events.MotionEvent.ActionUp) {
    	}
    	
    	onTouchEvent(this, event);
    	
    	return true;
    }*/
	
	
	public void backupContents() {
		//IO.IsLittleEndian = true;
		backupIsFinishingWhenExitingPrevly();
		if (Control.activity.isFinishing()==false) {
	    	//if (this.editRichText.isModified || this.editText.isModified) {
				backupText();
	    	//}		
		}
    	
		backupPlaylist();
		isSaved = true;
	}
	
	void backupText() {
		//IO.IsLittleEndian = true;
		Context context = getContext();
		FileOutputStream stream=null;
		BufferedOutputStream bos=null;
		boolean r = false;
		String absFilename=null;
		TextFormat format = TextFormat.UTF_16;
		try {
			File contextDir = context.getFilesDir();
			absFilename = Control.pathGSoftFiles + File.separator + "backup_EditRichText.kjy";
			stream = new FileOutputStream(absFilename);
			//int bufferSize = (int) (FileHelper.getFileSize(absFilename, true)*IO.DefaultBufferSizeParam);
			bos = new BufferedOutputStream(stream);				
			editRichText.write(bos, format);
			bos.flush();
			r = true;
			//}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//r= false;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			//r = false;
		}
		finally {
			FileHelper.close(bos);
			FileHelper.close(stream);
			if (!r) {
				if (absFilename!=null) {
					File file = new File(absFilename);
					file.delete();
				}
			}
		}
		
		try {
			File contextDir = context.getFilesDir();
			absFilename = Control.pathGSoftFiles + File.separator + "backup_EditText.txt";
			stream = new FileOutputStream(absFilename);
			bos = new BufferedOutputStream(stream/*, IO.DefaultBufferSize*/);
			editText.write(bos, format);
			bos.flush();
			r = true;
			//}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//r= false;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			//r = false;
		}
		finally {
			FileHelper.close(bos);
			FileHelper.close(stream);
			if (!r) {
				if (absFilename!=null) {
					File file = new File(absFilename);
					file.delete();
				}
			}
		}
		
		
	}
	
	/** 음악을 재생중일 때만 playList를 덮어쓰고 
	 * 재생중이 아니면 state만 저장하고 playlist는 저장하지 않는다.*/
	void backupPlaylist() {
		Context context = getContext();
		FileOutputStream stream=null;
		BufferedOutputStream bos=null;
		boolean r = false;
		String absFilename=null;
		try {
			File contextDir = context.getFilesDir();
			absFilename = Control.pathGSoftFiles + File.separator + "backup_playlist";
			
			stream = new FileOutputStream(absFilename);
			bos = new BufferedOutputStream(stream/*, IO.DefaultBufferSize*/);
			writePlaylistAndCurSongInfo(bos, TextFormat.UTF_16);
			r = true;
		} catch (FileNotFoundException e) {
			r= false;
		}
		catch (Exception e) {
			r = false;
		}
		finally {
			FileHelper.close(bos);
			FileHelper.close(stream);
			if (!r) {
				if (absFilename!=null) {
					File file = new File(absFilename);
					file.delete();
				}
			}
		}
	}
	
	
	
	void backupFilenameLoaded() {
		Context context = getContext();
		FileOutputStream stream=null;
		BufferedOutputStream bos=null;
		boolean r = false;
		String absFilename=null;
		try {
			File contextDir = context.getFilesDir();
			absFilename = Control.pathGSoftFiles + File.separator + "backup_filename_loaded.txt";
			stream = new FileOutputStream(absFilename);
			bos = new BufferedOutputStream(stream/*, IO.DefaultBufferSize*/);
			IO.writeString(bos, filename, TextFormat.UTF_16, false, true);
			r = true;
		} catch (FileNotFoundException e) {
			r= false;
		}
		catch (Exception e) {
			r = false;
		}
		finally {
			FileHelper.close(bos);
			FileHelper.close(stream);
			if (!r) {
				if (absFilename!=null) {
					File file = new File(absFilename);
					file.delete();
				}
			}
		}
	}
	
	void writePlaylistAndCurSongInfo(OutputStream os, TextFormat format) {
		try {
			fileDialog.write(os, format);
		}catch(Exception e) {
			
		}
	}
	
	
	public void restoreText() {
		Context context = getContext();
		FileInputStream stream=null;
		BufferedInputStream bis=null;
		String absFilename=null;
		boolean r = false;
		File file = null;
		File contextDir = context.getFilesDir();
		TextFormat format = TextFormat.UTF_16;
		try {
			//stream = context.openFileInput("backup_file.kjy");
			
			//if (this.isEditRichTextOrEditText) {
				absFilename = Control.pathGSoftFiles + File.separator + "backup_EditRichText.kjy";
				file = new File(absFilename);
				if (file.exists()) {
					stream = new FileInputStream(absFilename);
					int bufferSize = (int) (FileHelper.getFileSize(absFilename)*IO.DefaultBufferSizeParam);
					bis = new BufferedInputStream(stream/*, bufferSize*/);
					TextLine text = EditRichText.Read(bis, format);
					this.savedText = text;
					
				}
				r = true;
		} 
		catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			this.savedText = null;
			//this.strSavedText = null;
			//r=false;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			this.savedText = null;
			//this.strSavedText = null;
			//r=false;
		}
		finally {
			FileHelper.close(bis);
			FileHelper.close(stream);
			if (!r) {
				if (file!=null) file.delete();
			}
		}
		
		try {
			absFilename = Control.pathGSoftFiles + File.separator + "backup_EditText.txt";
			file = new File(absFilename);
			if (file.exists()) {
				stream = new FileInputStream(absFilename);
				int bufferSize = (int) (FileHelper.getFileSize(absFilename)*IO.DefaultBufferSizeParam);
				bis = new BufferedInputStream(stream/*, bufferSize*/);
				String text = EditText.Read(bis, format);
				this.strSavedText = text;
				
			}
			r = true;
		}
		catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			this.savedText = null;
			//this.strSavedText = null;
			//r=false;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			this.savedText = null;
			//this.strSavedText = null;
			//r=false;
		}
		finally {
			FileHelper.close(bis);
			FileHelper.close(stream);
			if (!r) {
				if (file!=null) file.delete();
			}
		}
		
	}
	
	public void restorePlaylist() {
		Context context = getContext();
		FileInputStream stream=null;
		BufferedInputStream bis=null;
		String absFilename=null;
		boolean r = false;
		try {
			File contextDir = context.getFilesDir();
			absFilename = Control.pathGSoftFiles + File.separator + "backup_playlist";
			stream = new FileInputStream(absFilename);
			int bufferSize = (int) (FileHelper.getFileSize(absFilename)*IO.DefaultBufferSizeParam);
			bis = new BufferedInputStream(stream/*, bufferSize*/);
			readPlayListAndCurSongInfo(bis);
			r= true;
		} catch (FileNotFoundException e) {
			r=false;
		}
		catch (Exception e) {
			r=false;
		}
		finally {
			FileHelper.close(bis);
			FileHelper.close(stream);
			if (!r) {
				if (absFilename!=null) {
					File file = new File(absFilename);
					file.delete();
				}
			}
		}
	}
	
	
	
	public void restoreContents() {
		//settings 는 CommonGUI_SettingsDialog의 static 블럭에서 읽어들인다.
		isFinishingWhenExitingPrev = super.restoreIsFinishingWhenExitingPrevly();
		if (isFinishingWhenExitingPrev==0) {
			restoreText();
		}
		restorePlaylist();
		
	}
	
	void readPlayListAndCurSongInfo(InputStream is) {
		try {
			this.playListAndCurSongInfo = FileDialog.readPlayListAndCurSongInfo(is);
			
			mPlayList = playListAndCurSongInfo.playList;
		}catch(Exception e) {
			
		}
	}

	
	@Override
	public void onTouchEvent(Object sender, com.gsoft.common.Events.MotionEvent e) {
		// TODO Auto-generated method stub		
		super.onTouchEvent(sender, e);
		
		if (sender instanceof Button) {
			Button button = (Button)sender;
			if (button.iName==buttonMenu.iName) {
				if (isMaximized()) {
					menu.buttons[4].isSelected = true;
				}
				else {
					menu.buttons[4].isSelected = false;
				}
				menu.open(this, true);
			}
			// "New Document", "Load", "Save", "Minimize", "Maximize", "PrevSize", "Connect to server", "Close"
			else if (button.iName==menu.buttons[0].iName) {	// new document
				//isEditRichTextOrEditText = getIsEditRichTextOrEditText_Correct();
				if (isEditRichTextOrEditText==0) {
					editRichText.initialize();
				}
				else if (isEditRichTextOrEditText==1) {
					editText.initialize();
				}
				else {
					if (terminal!=null) terminal.editText.initialize();
				}
				menu.open(this, false);
			}
			else if (button.iName==menu.buttons[1].iName) { // load
				//isEditRichTextOrEditText = getIsEditRichTextOrEditText_Correct();
				// 키보드의 리스너가 겹치므로 키보드의 기존 리스너를 보관하고 있다가 
				// 대화상자가 닫힐 시 환원한다.
				if (fileDialog.getIsOpen()) {
					CommonGUI.loggingForMessageBox.setText(true, "File explorer already opens", false);
					CommonGUI.loggingForMessageBox.setHides(false);
					return;
				}
				
				if (isEditRichTextOrEditText==2) { // 터미널에서는 load를 할 수 없다.
					return;
				}
				/*if (Control.isMaximized==false) {
					fileDialog.isFullScreen = false;					
				}
				else {
					fileDialog.isFullScreen = true;
				}*/
				fileDialog.isFullScreen = true;
				fileDialog.canSelectFileType = false;
				fileDialog.isForViewing = true;
				fileDialog.isOpenFileDialog = false;
				fileDialog.setScaleValues();
				fileDialog.changeBounds(new Rectangle(0,0,getWidth(),getHeight()));
				
				if (isEditRichTextOrEditText==0) {
					fileDialog.createAndSetFileListButtons(fileDialog.curDir, FileDialog.Category.Custom);
				}
				else if (isEditRichTextOrEditText==1) {
					fileDialog.createAndSetFileListButtons(fileDialog.curDir, FileDialog.Category.Text);
				}
				fileDialog.setIsForReadingOrSaving(true);
				fileDialog.open(this, "FileExplorer - Load");
				menu.open(this, false);
				
				CommonGUI_SettingsDialog.showsCopyRight();
				
				/*editRichText.setHides(true);
				editText.setHides(true);
				if (terminal!=null) terminal.setHides(true);*/
			}
			else if (button.iName==menu.buttons[2].iName) { //save
				//isEditRichTextOrEditText = getIsEditRichTextOrEditText_Correct();
				if (fileDialog.getIsOpen()) {
					CommonGUI.loggingForMessageBox.setText(true, "File explorer already opens", false);
					CommonGUI.loggingForMessageBox.setHides(false);
					return;
				}
				// 키보드의 리스너가 겹치므로 키보드의 기존 리스너를 보관하고 있다가 
				// 대화상자가 닫힐 시 환원한다.
				/*if (Control.isMaximized==false) {
					fileDialog.isFullScreen = false;					
				}
				else {
					fileDialog.isFullScreen = true;
				}*/
				fileDialog.isFullScreen = true;
				fileDialog.canSelectFileType = false;
				fileDialog.isForViewing = false;
				fileDialog.isOpenFileDialog = false;
				fileDialog.setScaleValues();
				//RectangleF newBoundsOfEditText = new RectangleF(Control.totalBoundsOfEditText);
				fileDialog.changeBounds(new Rectangle(0,0,getWidth(),getHeight()));
				
				if (this.isEditRichTextOrEditText==0) {
					fileDialog.createAndSetFileListButtons(fileDialog.curDir, FileDialog.Category.Custom);
				}
				else if (isEditRichTextOrEditText==1) {
					fileDialog.createAndSetFileListButtons(fileDialog.curDir, FileDialog.Category.Text);
				}
				else if (isEditRichTextOrEditText==2) {
					fileDialog.createAndSetFileListButtons(fileDialog.curDir, FileDialog.Category.Text);
				}
				fileDialog.setIsForReadingOrSaving(false);
				fileDialog.open(this, "FileExplorer - Save");
				menu.open(this, false);
				
				CommonGUI_SettingsDialog.showsCopyRight();
				
				/*editRichText.setHides(true);
				editText.setHides(true);
				if (terminal!=null) terminal.setHides(true);*/
			}
			else if (button.iName==menu.buttons[3].iName) { // Sizable
				menu.open(this, false);
				if (isMaximized()) return;
				if (button.isSelected==false)
					sizingBorder.setHides(true);
				else
					sizingBorder.setHides(false);
			}
			else if (button.iName==menu.buttons[4].iName) { // maximize/prevSize
				menu.open(this, false);
				//isEditRichTextOrEditText = getIsEditRichTextOrEditText_Correct();
				CommonGUI.loggingForMessageBox.setText(true, "Loading...", false);
				CommonGUI.loggingForMessageBox.setHides(false);
				//MaxPrevSizeThread thread = new MaxPrevSizeThread();
				//thread.start();
				setMaximized();
				invalidate();
			}
			else if (button.iName==menu.buttons[5].iName) { // isEditRichTextOrEditText
				menu.open(this, false);
				//isEditRichTextOrEditText = getIsEditRichTextOrEditText_Correct();
				if (isEditRichTextOrEditText==0) {
					isEditRichTextOrEditText = 1;
				}
				else if (isEditRichTextOrEditText==1) {
					if (terminal!=null) {
						isEditRichTextOrEditText = 2;
					}
					else {
						isEditRichTextOrEditText = 0;
					}
				}
				else {
					isEditRichTextOrEditText = 0;
				}
				if (isEditRichTextOrEditText==0) {					
					/*RectangleF newBounds = new RectangleF(boundsOfEditText);
					newBounds.x = editRichText.bounds.x;
					newBounds.width = editRichText.bounds.width;
					editRichText.changeBounds(newBounds);*/
					editRichText.setHides(false);
					editText.setHides(true);
					if (terminal!=null) terminal.setHides(true);
					
				}
				else if (isEditRichTextOrEditText==1) {
					/*RectangleF newBounds = new RectangleF(boundsOfEditText);
					newBounds.x = editText.bounds.x;
					newBounds.width = editText.bounds.width;
					editText.changeBounds(newBounds);*/
					editText.setHides(false);
					editRichText.setHides(true);
					if (terminal!=null) terminal.setHides(true);
				}
				else if (isEditRichTextOrEditText==2) {
					if (terminal==null) return;
					/*RectangleF newBounds = new RectangleF(boundsOfEditText);
					newBounds.x = terminal.editText.bounds.x;
					newBounds.width = terminal.editText.bounds.width;
					terminal.editText.changeBounds(newBounds);*/
					terminal.setHides(false);
					editRichText.setHides(true);
					editText.setHides(true);
				}
				
			}
			else if (button.iName==menu.buttons[6].iName) { // open file explorer				
				menu.open(this, false);
				if (fileDialog.getIsOpen()) {
					CommonGUI.loggingForMessageBox.setText(true, "File explorer already opens", false);
					CommonGUI.loggingForMessageBox.setHides(false);
					return;
				}
				// 키보드의 리스너가 겹치므로 키보드의 기존 리스너를 보관하고 있다가 
				// 대화상자가 닫힐 시 환원한다.
				//fileExplorerDialog.createAndSetFileListButtons(fileExplorerDialog.curDir, FileDialog.Category.All);				
				//fileExplorerDialog.open(this);
				fileDialog.isFullScreen = true;
				fileDialog.canSelectFileType = true;
				fileDialog.isForViewing = true;
				fileDialog.isOpenFileDialog = true;
				fileDialog.setIsForReadingOrSaving(true);
				fileDialog.setScaleValues();
				fileDialog.changeBounds(new Rectangle(0,0,getWidth(),getHeight()));
				fileDialog.createAndSetFileListButtons(fileDialog.curDir, FileDialog.Category.All);
				//integrationKeyboard.setHides(true);
				fileDialog.open(this, "FileExplorer");
				
				
				CommonGUI_SettingsDialog.showsCopyRight();
				
				
			}
			else if (button.iName==menu.buttons[7].iName) { // Show arrow keys
				menu.open(this, false);
				// 한 칸 전 Mode로 파라미터를 설정해서 특수문자 키보드로 바꿔 준다.
				integrationKeyboard.process자판(Mode.Eng);
			}
			else if (button.iName==menu.buttons[8].iName) { // about programmer
				menu.open(this, false);
				/*String text = Control.res.getString(R.string.about_program);
				Control.loggingForMessageBox.setText(true, text, false);
				Control.loggingForMessageBox.setHides(false);*/
				if (messageDialog==null) {
					String message = Control.res.getString(R.string.about_program);
					Rectangle boundsOfDialog = new Rectangle(0, 0, (int)(width*0.9f), (int)(height*0.7f));
					boundsOfDialog.x = (width-boundsOfDialog.width) / 2;
					boundsOfDialog.y = (height-boundsOfDialog.height) / 2;
					createMessageDialog(boundsOfDialog, height * 0.04f, message);
					//messageDialog.editText.setScrollMode(EditText.ScrollMode.Both);
				}
				messageDialog.open(this, true);
				CommonGUI.loggingForMessageBox.setHides(true);
			}
			else if (button.iName==menu.buttons[9].iName) { // current time
				menu.open(this, false);
				
				String dateTime = com.gsoft.common.Util.Date.getCurDateTime(true);
				String text = dateTime;
				CommonGUI.loggingForMessageBox.setText(true, text, false);
				CommonGUI.loggingForMessageBox.setHides(false);
			}
			else if (button.iName==menu.buttons[10].iName) { // terminal
				menu.open(this, false);
				Rectangle bounds = new Rectangle(widthOfGap, 
						boundsOfButtonMenu.bottom()+heightOfGap, 
						(int)(width*scaleOfEditTextX), (int)(height*scaleOfEditTextY));
				isEditRichTextOrEditText = 2;
				if (terminal==null) {
					int selectedColor = CommonGUI_SettingsDialog.settings.selectedColor[0];
					terminal = new Terminal(bounds, selectedColor);
				}
				/*editRichText.setHides(true);
				editText.setHides(true);*/
				terminal.setHides(false);
				
			}
			else if (button.iName==menu.buttons[11].iName) { // Settings
				menu.open(this, false);
				CommonGUI_SettingsDialog.settingsDialog.open(this, true);
			}
			else if (button.iName==menu.buttons[12].iName) { // 실제로 종료한다.	// close
				close();
			}
			else if (buttonProcessClose!=null && button.iName==buttonProcessClose.iName) {
				if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
					if (Control.isMasterOrSlave) {
						Button selectedButton = this.menuTabList.selectedButton;
						if (selectedButton==null) return;
						// sub 프로세스를 죽인다.
						int numOfSubProcess = Integer.parseInt((String)selectedButton.addedInfo);
						Pipe.destroySubProcess(numOfSubProcess);
						
						
						
						// menuTabList 를 갱신한다.
						String[] fileList = Pipe.listOfPathOfOpenProcesses.getItems();
						Button[] buttonPaths = this.getFileListButtons(fileList);
						this.menuTabList.setButtons(buttonPaths);
					}
				}
			}
			else if (buttonLogBird!=null && button.iName==buttonLogBird.iName) {
				if (CommonGUI.textViewLogBird!=null) {
					CommonGUI.textViewLogBird.setHides(false);
					CommonGUI.textViewLogBird.vScrollPosToLastPage();
				}
			}
			
		}	// button
		else if (sender instanceof FileDialog) { 
			fileDialog_eventHandler(sender);
		}	
		else if (Control.capturedControl==this.sizingBorderOfView && 
				e.actionCode==com.gsoft.common.Events.MotionEvent.ActionMove) {
			if (sender instanceof SizingBorderOfView) {				
				resizeView(e.x, e.y, sizingBorderOfView.orientation);
			}
		}
		else if (sender instanceof SizingBorder) {
			if (e.actionCode==com.gsoft.common.Events.MotionEvent.ActionMove){
				//isEditRichTextOrEditText = getIsEditRichTextOrEditText_Correct();
				if (this.isEditRichTextOrEditText==0) {
					Rectangle bounds = editRichText.bounds;
					int heightOfEditText = e.y-1-bounds.y;				
					int viewHeight = getHeight();
					float heightOfKeyboard = viewHeight - e.y;
					float minHeight = viewHeight*0.2f;
					if (heightOfEditText > minHeight && heightOfKeyboard > minHeight) {
						
						sizingBorder.bounds.y = e.y;
						
						Rectangle newBoundsOfEditText = new Rectangle(bounds);
						newBoundsOfEditText.height = heightOfEditText;
						
						editRichText.resize(newBoundsOfEditText);
						
						//Control.prevSizeConstant = new RectangleF(editRichText.bounds);
						//Control.prevSizeOfKeyboardConstant = integrationKeyboard.bounds;
					}
				}
				else if (isEditRichTextOrEditText==1) {
					Rectangle bounds = editText.bounds;
					int heightOfEditText = e.y-1-bounds.y;
					int viewHeight = getHeight();
					float heightOfKeyboard = viewHeight - e.y;
					float minHeight = viewHeight*0.2f;
					if (heightOfEditText > minHeight && heightOfKeyboard > minHeight) {
												
						sizingBorder.bounds.y = e.y;
						
						Rectangle newBoundsOfEditText = new Rectangle(bounds);
						newBoundsOfEditText.height = heightOfEditText;
						
						editText.resize(newBoundsOfEditText);
						
						//Control.prevSizeConstant = new RectangleF(editText.bounds);
						//Control.prevSizeOfKeyboardConstant = integrationKeyboard.bounds;
					}
					
				}
			}
		
		}
		else if (sender instanceof SettingsDialog) {	// SettingsDialog의 OK버튼
			Settings settings = CommonGUI_SettingsDialog.settings;
			int selectedColor = settings.selectedColor[0];			
			editText.setBackColor(selectedColor);
			editRichText.setBackColor(selectedColor);
			if (terminal!=null) terminal.editText.setBackColor(selectedColor);
			
			selectedColor = settings.selectedColor[1];
			this.integrationKeyboard.setBackColor(selectedColor);
			
			String pathAndroid = settings.pathAndroid;
			Control.pathAndroid = pathAndroid;
			invalidate();
		}
		else if (sender instanceof MessageDialog) {	// CloseDialog의 OK버튼
			MessageDialog dialog = (MessageDialog)sender;
			if (dialog.iName==closeDialog.iName) {
				close();
			}
		}
		else if (sender instanceof Pipe.ThreadPipeForReceivingOfMain) {
			String[] fileList = Pipe.listOfPathOfOpenProcesses.getItems();
			Button[] buttonPaths = this.getFileListButtons(fileList);
			this.menuTabList.setButtons(buttonPaths);
		}
		else if (sender instanceof MenuWithScrollBar) {
			// master 에서 sub 프로세스를 가르키는 버튼을 클릭할 때
			// focus 를 주기를 원하는 하나의 sub 프로세스를 제외한 모든 sub 프로세스를 back 으로 바꾸고 
			// 하나의 sub 프로세스에만 focus 를 준다.
			MenuWithScrollBar menu = (MenuWithScrollBar) sender;
			if (menu.iName==this.menuTabList.iName) {
				Button button = menu.selectedButton;
				if (e.actionCode==MotionEvent.ACTION_DOWN) {
					if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
						if (Control.isMasterOrSlave) {
							int numOfSubProcess;
							if (button!=null) {
								String num = (String) button.addedInfo;
								numOfSubProcess = Integer.parseInt(num);
								Pipe.toBackAllSubProcessExceptOne(numOfSubProcess);
								Pipe.focusSubProcess(numOfSubProcess);
								//Control.view.toBack();
								String path = Pipe.listOfPathOfOpenProcesses.getItem(numOfSubProcess);
								editTextDocumentPath.setText(0, new CodeString(path,editTextDocumentPath.textColor));
								editTextDocumentPath.isSelecting = false;
								editTextDocumentPath.initCursorAndScrollPos();
								this.invalidate();
							}
						}
					}
				}
				
			}
		}//else if (sender instanceof MenuWithScrollBar) {
		
	}
	
	void fileDialog_eventHandler(Object sender) {
		// load, save, fileExplorer
		FileDialog dialog = (FileDialog)sender;
		
		if (dialog.isFullScreen) { // open FileExplorer				
			if ((dialog.isOpenFileDialog)
					&& dialog.getIsOK()==true) {
				CommonGUI.loggingForMessageBox.setText(true, "Please use the load/save button.", false);
				CommonGUI.loggingForMessageBox.setHides(false);
				return;
			}
		}
		// load, save
		if (dialog.getIsOK()==false) return;
		if (dialog.iName!=fileDialog.iName) return;
		filename = dialog.filename;
		//isEditRichTextOrEditText = getIsEditRichTextOrEditText_Correct();
		
		if (dialog.getIsForReadingOrSaving()==false) {
			// 터치시 OnTouchListener로 리턴한 후 invalidate가 호출되므로
			// invalidate를 호출할 필요가 없다.
			CommonGUI.loggingForMessageBox.setText(true, "Saving...", false);
			CommonGUI.loggingForMessageBox.setHides(false);
			if (writeFileThread==null) {
				writeFileThread = new WriteFileThread(this);
				writeFileThread.start();
			}
			else {
				if (writeFileThread.isAlive()) writeFileThread.interrupt();
				writeFileThread = new WriteFileThread(this);
				writeFileThread.start();
			}
			
		}
		else {
			// 터치시 OnTouchListener로 리턴한 후 invalidate가 호출되므로
			// invalidate를 호출할 필요가 없다.
			//boolean hasSomeText = false;
			if (isEditRichTextOrEditText==0) {
				editRichText.initialize();
			}
			else if (isEditRichTextOrEditText==1) {
				editText.initialize();
			}
			
			
			
			CommonGUI.loggingForMessageBox.setText(true, "Loading...", false);
			CommonGUI.loggingForMessageBox.setHides(false);
			if (readFileThread==null) {
				readFileThread = new ReadFileThread(this);
				readFileThread.start();
			}
			else {
				if (readFileThread.isAlive()) readFileThread.interrupt();
				readFileThread = new ReadFileThread(this);
				readFileThread.start();
			}
			
			try {
				if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
					if (Control.isMasterOrSlave) {
						readFileThread.join();
						String[] fileList = Pipe.listOfPathOfOpenProcesses.getItems();
						if (fileList.length>3) {
							int a;
							a=0;
							a++;
						}
						if (fileList.length<1) return;
						Button[] buttonPaths = this.getFileListButtons(fileList);
						this.menuTabList.setButtons(buttonPaths);
						this.editTextDocumentPath.initCursorAndScrollPos();
						this.editTextDocumentPath.isSelecting = false;
						this.editTextDocumentPath.setText(0, new CodeString(fileList[fileList.length-1], editTextDocumentPath.textColor));
					}
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}			
	}
	
	
	
	/** ms 윈도우에서만 가능한 기능이다.*/
	void resizeView(int x, int y, ResizeViewOrientation orientation) {
		
		int posx = 0, posy = 0, w = 0, h = 0;		
		
		if (orientation.orientation==ResizeViewOrientation.RIGHT) {
			posx = getLocation().x;
			posy = getLocation().y;
			w = (posx + x) - ResizeViewOrientation.TopLeftAbsolute.x + 1;
			h = height;
		}
		else if (orientation.orientation==ResizeViewOrientation.BOTTOM) {
			posx = getLocation().x;
			posy = getLocation().y;
			h = (posy + y) - ResizeViewOrientation.TopLeftAbsolute.y + 1;
			w = width;
		}
		else if (orientation.orientation==ResizeViewOrientation.LEFT) {
			posx = getLocation().x + x; // 절대좌표에서  바뀌는 윈도우의 left 좌표
			posy = getLocation().y;
			w = ResizeViewOrientation.RightBottomAbsolute.x - posx + 1;
			h = height;
		}
		else if (orientation.orientation==ResizeViewOrientation.TOP) {
			posx = getLocation().x;
			posy = getLocation().y + y; // 절대좌표에서  바뀌는 윈도우의 Top 좌표
			h = ResizeViewOrientation.RightBottomAbsolute.y - posy + 1;
			w = width;
		}
		
		int MinimumViewWidth = 
				(int) (CommonGUI_SettingsDialog.settings.viewWidth * 0.5f);
		int MinimumViewHeight = 
				(int) (CommonGUI_SettingsDialog.settings.viewHeight * 0.5f);
		
		if (w<MinimumViewWidth || h<MinimumViewHeight)
			return;
		
		width = w;
		height = h;
		createBufferedImageAndCanvas(width, height);
		this.setBounds(posx, posy, w, h);
		
				
		this.changeBounds(this.width, this.height);
		
		buttonMenu.changeBounds(boundsOfButtonMenu);
		editText.changeBounds(boundsOfEditText);
		editRichText.changeBounds(boundsOfEditRichText);
		if (terminal!=null) {
			terminal.editText.changeBounds(this.boundsOfTerminal);
		}
		
		
		this.sizingBorderOfView.changeBounds(this.boundsOfsizingBorderOfView);
		
		this.menu.changeBounds(this.boundsOfMenu);
		if (CommonGUI.loggingForMessageBox!=null) 
			CommonGUI.loggingForMessageBox.changeBounds(this.boundsOfLoggingForMessageBox);
		if (CommonGUI.loggingForNetwork!=null) 
			CommonGUI.loggingForNetwork.changeBounds(this.boundsOfLoggingForNetwork);
		if (CommonGUI.keyboard!=null) 
			CommonGUI.keyboard.changeBounds(this.boundsOfIntegrationKeyboard);
		if (CommonGUI.fileDialog!=null) 
			CommonGUI.fileDialog.changeBounds(this.boundsOfFileDialog);
		
		super.resizeView();
	}
	
	boolean isMaximized() {
		if (isEditRichTextOrEditText==0) {
			if (editRichText.isMaximized) return true;
			return false;
		}
		else if (isEditRichTextOrEditText==1) {
			if (editText.isMaximized) return true;
			return false;
		}
		else if (isEditRichTextOrEditText==2) {
			if (terminal!=null && terminal.editText.isMaximized) return true;
			return false;
		}
		return false;
	}
	
	void setMaximized() {
		if (isEditRichTextOrEditText==0) {
			if (menu.buttons[4].isSelected) {
				if (isMaximized()) {
					CommonGUI.loggingForMessageBox.setHides(true);
					//postInvalidate();
					return;
				}
				//Control.isMaximized = true;
				
				
				editRichText.setMaximized(true);
			}
			else { // prevsize
				if (isMaximized()==false) {
					CommonGUI.loggingForMessageBox.setHides(true);
					//postInvalidate();
					return;
				}
				//Control.isMaximized = false;
				
				editRichText.setMaximized(false);
			}
		}
		else if (isEditRichTextOrEditText==1) {	// EditText
			if (menu.buttons[4].isSelected) {
				if (isMaximized()) {
					CommonGUI.loggingForMessageBox.setHides(true);
					//postInvalidate();
					return;
				}
				//Control.isMaximized = true;
				
				
				
				editText.setMaximized(true);
			}
			else { // prevsize
				if (isMaximized()==false) {
					CommonGUI.loggingForMessageBox.setHides(true);
					//postInvalidate();
					return;
				}
				//Control.isMaximized = false;
				
				editText.setMaximized(false);
			}
			
		}
		else if (isEditRichTextOrEditText==2) {	// Terminal
			if (terminal==null) return;
			if (menu.buttons[4].isSelected) {
				if (isMaximized()) {
					CommonGUI.loggingForMessageBox.setHides(true);
					//postInvalidate();
					return;
				}
				//Control.isMaximized = true;
				
				terminal.editText.setMaximized(true);
			}
			else { // prevsize
				if (isMaximized()==false) {
					CommonGUI.loggingForMessageBox.setHides(true);
					//postInvalidate();
					return;
				}
				//Control.isMaximized = false;
				
				terminal.editText.setMaximized(false);
			}
			
		}
		CommonGUI.loggingForMessageBox.setHides(true);
		//postInvalidate();
	}
	
	static class MaxPrevSizeThread extends Thread {
		
		CustomView owner;
		MaxPrevSizeThread(CustomView owner) {
			this.owner = owner;
		}
		
		public void run() {
			owner.setMaximized();
			//postInvalidate();
			Control.view.postInvalidate();
		}// run()
	}
	
	static class WriteFileThread extends Thread {
		CustomView owner;
		WriteFileThread(CustomView owner) {
			this.owner = owner;
		}
		
		public void run() {
			try {
			owner.writeFile(owner.isEditRichTextOrEditText, owner.editRichText, owner.editText, 
					owner.terminal, owner.fileDialog.curDir+owner.filename);
			Control.view.postInvalidate();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	static class ReadFileThread extends Thread {
		CustomView owner;
		ReadFileThread(CustomView owner) {
			this.owner = owner;
		}
		public void run() {
			try {
				owner.readFile(owner.isEditRichTextOrEditText, owner.editRichText, owner.editText, 
					owner.fileDialog.curDir+owner.filename);
			if (owner.isEditRichTextOrEditText==0) {
				owner.editRichText.initCursorAndScrollPos();
				CommonGUI.keyboard.setOnTouchListener(owner.editRichText);
			}
			else if (owner.isEditRichTextOrEditText==1) {
				owner.editText.initCursorAndScrollPos();
				CommonGUI.keyboard.setOnTouchListener(owner.editText);
			}
			Control.view.postInvalidate();
			}catch(Exception e) {
				e.printStackTrace();
			}
			
				
		}//run
	}	// ReadFileThread
	
	void close() {
		/*if (this.isEditRichTextOrEditText==0) {
			editRichText.initialize();
		}
		else if (isEditRichTextOrEditText==1) {
			editText.initialize();
		}*/
		Control.exit(false);
		//destroy();
	}
	
	void killRenderThread() {
		if(renderThread==null) return;
		renderThread.interrupt();
	}
	
	public void destroy() {
		super.destroy();
		backupContents();
		killRenderThread();
		
		if (fileDialog!=null) {
			if (fileDialog.isPlaying()==false) {
				PowerManagement.releaseWakeLock();
			}
			
			fileDialog.destroy();
			fileDialog = null;
		}
		
		// 자바로 바꿀때 주석추가
		//Notification.cancelNotify();
		
		if (exitTimer!=null) {
			exitTimer.killTimer();
			exitTimer = null;
		}
		
		if (readFileThread!=null && readFileThread.isAlive()) {
			readFileThread.interrupt();
		}
		
		if (writeFileThread!=null && writeFileThread.isAlive()) {
			writeFileThread.interrupt();
		}
		
		//pause();
	}
	
	public void destroyExceptNotify () {
		/*if (Control.textOfNotification==null) {
			Notification.notifyAppUpdate(Control.activity, " is stopped." );
		 }
		else {
			Notification.notifyAppUpdate(Control.activity, Control.textOfNotification );
		}*/
		//killServiceThread();
		destroy();
		
	}	


	@Override
	public void onTick(Object sender) {
		// TODO Auto-generated method stub
		/*if (time1OfTouch!=0) {
			time2OfTouch = System.currentTimeMillis();
			handlingTimeOfTouch = time2OfTouch - time1OfTouch;
			if (handlingTimeOfTouch>3000) {	
				exitTimer.cancelTimer();
				//closeDialog.open(this, true);	// 이렇게 하면 그려지지 않는다.
				Control.textOfNotification = " was destroyed because of no response.";
				Control.exit(true);				
			}
		}*/
	}


	@Override
	public void onFinish(Object sender) {
		// TODO Auto-generated method stub
				
	}


	
}