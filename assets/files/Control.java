package com.gsoft.common.gui;
// %h가나다라마바사
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.gsoft.common.ColorEx;
import com.gsoft.common.Compiler.TextView;
import com.gsoft.common.IO;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util.ArrayListInt;
import com.gsoft.common.Util.ControlStack;
import com.gsoft.common.interfaces.OnTouchListener;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.graphics.Bitmap;


public abstract class Control  {
		
	public static ControlStack controlStack = new ControlStack(20);
		
	public static class Container extends Control {
	}
	
	Object owner;
	public static View view;
	/** Compiler의 로그를 위한 editText*/
	public static TextView textViewLogBird;
    public RectangleF bounds;
    
    /** 보이는 상태에서 바운드가 바뀔때는 스택아래 컨트롤들을 복원했다가 
	 * 다시 숨겨지는 컨트롤들을 결정한다.
	 * 보이지 않는 상태에서는 바운드를 바꿀 수 없다.
	 * @param paramBounds : 툴바제외 영역
	 */
	public void changeBoundsSafe(RectangleF paramBounds) {
		boolean oldHides = getHides();
		if (oldHides==false) {
			setHides(true);
			changeBounds(paramBounds);			
			setHides(false);
		}
	}
	
	/**컨트롤이 최대화되어있는지 확인*/
    public boolean isMaximized(RectangleF bounds) {
    	int viewHeight = view.getHeight();
    	if (bounds.bottom()+5>viewHeight && bounds.height>viewHeight*0.7f) return true;
    	return false;
    }
    
    /**컨트롤이 최대화되어있는지 확인*/
    public boolean isMaximized() {
    	int viewHeight = view.getHeight();
    	if (bounds.bottom()+5>viewHeight && bounds.height>viewHeight*0.7f) return true;
    	return false;
    }
    
    public static float vertScaleOfGap = 0.015f;
    public static float horzScaleOfGap = 0.02f;
    public static float scaleOfKeyboardX = 1.0f;
    
    /** Edit(Rich)Text의 bounds(툴바제외), Maximize/PrevSize에서 bounds를 복원할때 사용*/
    //public static RectangleF prevSizeConstant;
    /** Edit(Rich)Text의 bounds(툴바포함), fileDialog의 onTouch에서 bounds를 복원할때 사용*/
    //public static RectangleF prevSizeTotalConstant;
    //public static RectangleF prevSizeOfKeyboardConstant;
    
    int backColor = Color.WHITE;
    public int textColor = Color.BLACK;
    
    public void setBackColor(int color) {
		backColor = color;
		textColor = ColorEx.reverseColor(backColor);
	}
	
	public void setTextColor(int color) {
		textColor = color;
		backColor = ColorEx.reverseColor(textColor);
	}
    
    int alpha = 255;
	
    public String name;
	
	public int iName;	
	public static int countOfControls;
	
	//public boolean isMoveActionCaptured;
	public static Control capturedControl;
	
	
	public OnTouchListener listener;
	private OnTouchListener oldTouchListener;
	//public boolean boundsEnable;
	
	
	public static LoggingScrollable loggingForMessageBox;
	
	public static LoggingScrollable loggingForNetwork;
	
	
	public static Typeface typefaceDefault;
	public static Typeface typefaceBold;
	
	
	protected boolean hides;
	
	/** setHides(true)는 control stack을 모두 뒤져서 현재 컨트롤을 hides상태로 만든다. 다시 말해 컨트롤스택에 
	 * 있는 현재 컨트롤의 레퍼런스를 모두 삭제(deleteItem)한다.
	 * setHides(false)는  컨트롤 스택에 있는 기존 컨트롤의 레퍼런스들을 모두 삭제한 후에 새로이 컨트롤의 레퍼런스를
	 * 추가하고 hides상태를 false로 만든다. 
	 * 이 컨트롤이 열릴 경우 스택 아래에 있는 이미 숨겨진 컨트롤은 제외한 영역이 포함되는 모든 컨트롤들을 숨긴다.
	 * 이 컨트롤이 닫힐 경우 열릴 때 닫힌 모든 컨트롤들을 다시 연다.
	 * @param hides
	 */
	public synchronized void setHides(boolean hides) {
		//if (this.hides==hides) return;
		this.hides = hides;
		open(!this.hides);
	}
		
	public synchronized boolean getHides() {
		return hides;
	}
	
	
	boolean isOpen;
	
	public boolean getIsOpen() {
    	return isOpen;
    }
    
    public void setIsOpen(boolean isOpen) {
    	this.isOpen = isOpen;
    	this.hides = !isOpen;
    }
    
    	
	public boolean modified = true;
	
	//public static boolean isAllDrawn = false;
	
	/** View의 mCanvas와 함께 사용되어 그리기 위한 buffer역할을 한다.*/
	public static Bitmap drawingCache;
	
	/** PartDrawing(부분그리기)메커니즘을 위해 true로 설정한다. 아니면 false*/
	public static boolean usesDrawingCache = false;
	
	/** drawingCache(PartDrawing)에 그리기 위한 작업공간이다. open()을 통해 열리기 전 화면을 
	 * backup하고 restore한다. 메뉴, 다이얼로그 등은 각자 갖고 있어야 한다.*/
	public int[] oldPixels;
	
	
	public static String pathAndroid = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "gsoft";
	
	
	
	/*public static boolean modified;
	public synchronized static void setModified(boolean b) {
		// TODO Auto-generated method stub
		modified = b;
	}*/
	
	/** 공유 dialog들*/
	public static FileDialog fileDialog;
	
	public static ColorDialog colorDialog;
	
	public static SettingsDialog settingsDialog;
	//public static FileDialog fileExplorerDialog;
	
	public static Window window;
	
	public static Activity activity;
	
	public static ViewEx viewEx;
	
	public static Resources res;
	public static boolean findEqualDir = false;
	
	public static boolean isDestroyedExceptNotify=false;
	public static String textOfNotification;
	
	public static IntegrationKeyboard keyboard;
	
	/** edit(Rich)Text의 bounds를 복원할 때 사용*/
	//public static RectangleF prevSizeOfEditText;
	/** fileDialog의 bounds를 결정할때 사용*/
	//public static RectangleF totalBoundsOfEditText;
	/** Control마다 사용, 인스턴스*/
	public RectangleF prevSize;
	//private ArrayListInt indicesOfControlsToHide;
	private ArrayListInt iNamesOfControlsToHide;
	
	public static boolean isMaximized;
	
	public static SizingBorder sizingBorder;
	
	public void backUpBounds() {
		prevSize = new RectangleF(bounds);
	}
	
	
	
	public static void showMessageDelayed(String text) {
		if (Control.loggingForMessageBox!=null) {
			Control.loggingForMessageBox.setText(true, text, false);
			Control.loggingForMessageBox.setHides(false);
			if (view!=null) view.invalidate();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public static void showMessageToFile(String text) {
		FileOutputStream outputStream=null;
		try{
		
		File contextDir = view.getContext().getFilesDir();
		
		String absFilename = contextDir.getAbsolutePath() + File.separator + "ExitReason.txt";
		//String absFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + list[i];
		File file = new File(absFilename);
		
		outputStream = new FileOutputStream(file);
		IO.writeString(outputStream, text, TextFormat.UTF_8);
		}catch(Exception e) {
			
		}
		finally {
			FileHelper.close(outputStream);
		}
	}
		
		
	public void write(OutputStream os, TextFormat format) throws Exception {
		try {
			String className = getClass().getName();
			IO.writeString(os, className, format);
		}catch(Exception e) {
			throw e;
		}
		//IO.writeString(os, ";");
	}
	
	public static void showLogForMessageBox(boolean addOrReplace, String text) {
		if (Control.loggingForMessageBox!=null) Control.loggingForMessageBox.setText(!addOrReplace, text, false);
		if (Control.loggingForMessageBox!=null) Control.loggingForMessageBox.setHides(false);
		if (Control.view!=null) Control.view.invalidate();
	}
	
	 /** FileDialog는 changeBound를 적용해도 되도록 View의 크기로 drawingBuffer의 크기를 설정하도록
	  * isFullScreen을 true로 설정하고 메뉴나 다른 Dialog들은 bounds을 설정한다.*/
	public void createDrawingBuffer(View view, 
			boolean isFullScreen, Rectangle bounds) {
		if (usesDrawingCache) {
			if (isFullScreen) {
				oldPixels = new int[(view.getWidth()+2) * (view.getHeight()+2)];
			}
			else {
				oldPixels = new int[(bounds.width+2) * (bounds.height+2)];
			}
		}
		
	}
	
	/** open(true)는  컨트롤 스택에 있는 기존 컨트롤의 레퍼런스들을 모두 삭제한 후에 새로이 컨트롤의 레퍼런스를
	 * 추가하고 hides상태를 false, isOpen을 true로 만든다.
	 * open(false)는 control stack을 모두 뒤져서 현재 컨트롤을 hides상태를 true, isOpen을 false로 만든다. 
	 * 다시 말해 컨트롤스택에 있는 현재 컨트롤의 레퍼런스를 모두 삭제(deleteItem)한다.
	 * 이 컨트롤이 열릴 경우 스택 아래에 있는 이미 숨겨진 컨트롤은 제외한 영역이 포함되는 모든 컨트롤들을 숨긴다.
	 * 이 컨트롤이 닫힐 경우 열릴 때 닫힌 모든 컨트롤들을 다시 연다.  
	 * @param hides
	 */
	public void open(boolean isOpen)
    {
		//if (this.isOpen==isOpen) return;
        this.isOpen = isOpen;
        this.hides = !isOpen;
        if (usesDrawingCache) {
        }
        else {
        	if (isOpen) {
        		if (this instanceof EditText) {
        			int a;
        			a=0;
        			a++;
        		}
        		Control.controlStack.deleteItem(iName);
        		Control.controlStack.add(this);
        		
        		// 이 컨트롤이 열릴 경우 스택 아래에 있는 이미 숨겨진 컨트롤은 제외한
        		// 영역이 포함되는 모든 컨트롤들을 숨긴다.
        		/*iNamesOfControlsToHide = findControlsToHide(bounds);
        		if (iNamesOfControlsToHide.count>0) {
        			int i;
        			for (i=0; i<iNamesOfControlsToHide.count; i++) {
        				int iName = iNamesOfControlsToHide.getItem(i);
        				Control control = Control.controlStack.findItem(iName);
        				if (control!=null) {
	        				control.hides = true;
	        				control.isOpen = false;
        				}
        			}
        		}*/
        	}
        	else {
        		if (this instanceof EditText) {
        			int a;
        			a=0;
        			a++;
        		}
        		//iNamesOfControlsToHide = findControlsToHide(bounds);
        		
        		Control.controlStack.deleteItem(iName);
        		
        		// 이 컨트롤이 닫힐 경우 열릴 때 닫힌 모든 컨트롤들을 다시 연다.
        		/*if (iNamesOfControlsToHide!=null && iNamesOfControlsToHide.count>0) {
        			int i;
        			for (i=0; i<iNamesOfControlsToHide.count; i++) {
        				int iName = iNamesOfControlsToHide.getItem(i);
        				Control control = Control.controlStack.findItem(iName);
        				if (control!=null) {
	        				control.hides = false;
	        				control.isOpen = true;
        				}
        			}
        		}*/
        	}
        }
    }
	
	
	public Control() {
		iName = countOfControls;
		countOfControls++;
	}
	
	
	/** 프로그램이 사용하는 리소스들(네트워크스레드,타이머스레드 등), 알림을 해제하고 종료한다.*/
	public static void exit(boolean argIsDestroyedExceptNotify) {
		isDestroyedExceptNotify = argIsDestroyedExceptNotify;
		if (activity!=null) activity.finish();
		
	}
	
	public Container getContainer(Object owner) throws Exception {
		Object o = owner;
		int count = 0;
		
		do {
			try {
				//Class<? extends Object> c = o.getClass();
				//String packageName = c.getPackage().getName();
				if (o instanceof android.view.View)
					return null;
				else if (o instanceof Container)
					return (Container)o;
				else {
					Control control = (Control)o;
					o = control.owner;
				}
			}catch(Exception e) {
				throw e;
			}
		}while(count<100);
		throw new Exception("Owner Container not found");
	}
	    
	public View getView(Object owner) throws Exception {
		Object o = owner;
		int count = 0;
		
		do {
			try {
				//Class<? extends Object> c = o.getClass();
				//String packageName = c.getPackage().getName();
				if (o instanceof android.view.View)
					return (View)o;
				else {
					Control control = (Control)o;
					o = control.owner;
				}
			}catch(Exception e) {
				throw e;
			}
		}while(count<100);
		throw new Exception("Owner View not found");
	}
		
    public boolean IsPointIn(Point point)
    {
    	//if (bounds.width<=0 || bounds.height<=0) return false;
        if (bounds.x <= point.x && point.x <= bounds.x + bounds.width &&
            bounds.y <= point.y && point.y <= bounds.y + bounds.height)
            return true;
        return false;
    }

	public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
		// TODO Auto-generated method stub
		if (hides==true || IsPointIn(new Point(event.x, event.y))==false) 
			return false;
		//modified = true;
		// 모든 컨토롤들이 ActionCapture를 하지 않는 것으로 가정한다.
		if (event.actionCode==MotionEvent.ActionDown) {
			capturedControl = null;
		}
		return true;
	}
	
	
	/** 이벤트를 필요로 하는 여러 개의 리스너가 있을 경우(예를들어 IntegrationKeyboard를 필요로 하는
	 * editText가 여러 개 있는 경우), 이벤트를 필요로 할 때 backUp을 호출하여 
	 * 기존 리스너를 보관하고 있다가 나중에 환원(restore)한다.*/
	public OnTouchListener backUp() {		
		oldTouchListener = listener;
		return oldTouchListener;
	}
	
	/** 이벤트를 필요로 하는 여러 개의 리스너가 있을 경우 이벤트가 필요없을 때 보관된 기존 리스너를 환원한다.*/
	public void restore() {
		listener = this.oldTouchListener;
		
	}
	
	/** bounds1이 bounds2를 포함하면 true, 그렇지않으면 false*/
	public static boolean compareBounds(RectangleF bounds1, RectangleF bounds2) {
		int diff = 2;
		if (bounds1.width>=bounds2.width-diff && bounds1.height>=bounds2.height-diff) {			
			if (bounds1.x-diff > bounds2.x+1) return false;
			if (bounds1.y-diff > bounds2.y+1) return false;
			if (bounds1.right()+diff < bounds2.right()) return false;
			if (bounds1.bottom()+diff < bounds2.bottom()) return false;
			return true;
		}
		return false;
	}
	
	/** 이 컨트롤이 열릴 때 숨겨져야할(영역이 포함되고 이미 숨겨지지않은 컨트롤) 
	 * 컨트롤들을 찾는다.*/
	public ArrayListInt findControlsToHide(RectangleF ownBounds) {
		ArrayListInt r = new ArrayListInt(10);
		int i;
		int indexOfThis=-1;	// 호출하는 control의 스택내에서의 인덱스
		for (i=Control.controlStack.count-1; i>=0; i--) {
			Control control = Control.controlStack.getItem(i); 
			if (iName==control.iName) {
				indexOfThis = i;
				break;
			}
		}
		// 숨겨질 control들을 찾는다.
		for (i=0; i<=indexOfThis-1; i++) {
			Control control = Control.controlStack.getItem(i);
			if (control.hides==true) continue; // 이미 숨은 컨트롤은 제외
			if (control.iName==iName) continue;
			if (compareBounds(ownBounds, control.bounds)) {
				r.add(control.iName);
			}
		}
		return r;
	}
	
	public static boolean requiresChangingBounds(RectangleF bounds1, RectangleF bounds2) {
		int diff = 5;
		if (bounds1.x > bounds2.x) {
			if (bounds1.x-bounds2.x>=diff) return true;
		}
		else {
			if (bounds2.x-bounds1.x>=diff) return true;
		}
		if (bounds1.y > bounds2.y) {
			if (bounds1.y-bounds2.y>=diff) return true;
		}
		else {
			if (bounds2.y-bounds1.y>=diff) return true;
		}
		if (bounds1.width > bounds2.width) {
			if (bounds1.width-bounds2.width>=diff) return true;
		}
		else {
			if (bounds2.width-bounds1.width>=diff) return true;
		}
		if (bounds1.height > bounds2.height) {
			if (bounds1.height-bounds2.height>=diff) return true;
		}
		else {
			if (bounds2.height-bounds1.height>=diff) return true;
		}
		return false;
		
	}
	
	public void changeBounds(RectangleF paramBounds) {
		
	}
	
	public void draw(Canvas canvas){
		
	}
	
	public void setOnTouchListener(OnTouchListener listener) {
		this.listener = listener;
    }
	
	/** 리스너가 있으면 이벤트전달*/
	public void callTouchListener(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		if (listener!=null)
			listener.onTouchEvent(sender, e);
		
	}
	
}


