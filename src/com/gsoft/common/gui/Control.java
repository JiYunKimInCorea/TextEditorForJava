package com.gsoft.common.gui;

import java.io.File;
import java.io.FileOutputStream;

import com.gsoft.common.ColorEx;
import com.gsoft.common.CommonGUI_SettingsDialog;
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
import android.graphics.Typeface;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.graphics.Bitmap;


public abstract class Control  {
	Bitmap bitmapForRendering;
	Canvas mCanvas;
		
	public static ControlStack controlStack = new ControlStack(20);
		
	public static class Container extends Control {
	}
	
	public Object owner;
	
    public Rectangle bounds;
    
    public static Window window;
	
	public static Activity activity;
	
	public static ViewEx viewEx;
	
	public static View view;
	
	public static Resources res;
	
	/** 현재 프로세스가 매스터로 동작하면 true, slave 으로 동작하면 false, 
	 * MainActivity에서 main()의 args 개수를 확인하여 매스터인지 slave 인지를 구분한다.*/
	public static boolean isMasterOrSlave;
	
	/** isMasterOrSlave==false일 경우 sub 프로세스의 일련번호, 
	 * 메인프로세스의 listOfProcesses와 listOfPathOfOpenProcesses등의 인덱스이다.
	 * 메인프로세스의 createCompilerProcess()에서 서브프로세스가 생성될때 환경변수를 통해
	 * 쓰여지면 서브프로세스의 MainActivity의 main()에서 읽혀진다. 
	 * Pipe.countOfCreatedProcesses와 값이 같게 된다.
	 */
	public static int numOfCurProcess;
	
	
	/** 비정상 종료시 에러를 출력한다.*/
	public static String ErrorLogFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() 
			+ File.separator + "ErrorLogFile.txt";
	
    
    
	
	public static com.gsoft.common.IO.TextFormat NetworkStringFormat = com.gsoft.common.IO.TextFormat.UTF_8;
	
	/** 클립보드*/
	public static com.gsoft.DataTransfer.ClipBoardX ClipBoardX = new com.gsoft.DataTransfer.ClipBoardX(); 
    
    /** 보이는 상태에서 바운드가 바뀔때는 스택아래 컨트롤들을 복원했다가 
	 * 다시 숨겨지는 컨트롤들을 결정한다.
	 * 보이지 않는 상태에서는 바운드를 바꿀 수 없다.
	 * @param paramBounds : 툴바제외 영역
	 */
	/*public void changeBoundsSafe(Rectangle paramBounds) {
		boolean oldHides = getHides();
		if (oldHides==false) {
			setHides(true);
			changeBounds(paramBounds);			
			setHides(false);
		}
	}*/
	
	/**컨트롤이 최대화되어있는지 확인*/
    public boolean isMaximized(RectangleF bounds) {
    	int viewHeight = view.getHeight();
    	if (bounds.bottom()+5>viewHeight && bounds.height>viewHeight*0.7f) return true;
    	return false;
    }
    
    /**컨트롤이 최대화되어있는지 확인*/
    public boolean isMaximized() {
    	if (this.isMaximized) return true;
    	int viewHeight = view.getHeight();
    	if (bounds.bottom()+SizingBorderOfView.thickness+5>viewHeight && bounds.height>viewHeight*0.7f) return true;
    	return false;
    }
    
    /** bounds 영역에 SizingBorderOfView(resize 영역)를 고려한 영역을 적용해서 리턴한다.*/
    public Rectangle applySizingBorderOfView(Rectangle bounds) {
    	if (view!=null) {
	    	if (bounds.x < SizingBorderOfView.thickness) {
	    		bounds.x = SizingBorderOfView.thickness;
	    	}
	    	if (bounds.y < SizingBorderOfView.thickness) {
	    		bounds.y = SizingBorderOfView.thickness;
	    	}
			if (bounds.x+bounds.width >= view.getWidth()-SizingBorderOfView.thickness) {
				bounds.width = view.getWidth()-SizingBorderOfView.thickness - bounds.x;
			}
			if (bounds.y<view.getHeight()) {
				if (bounds.y+bounds.height >= view.getHeight()-SizingBorderOfView.thickness) {
					bounds.height = view.getHeight()-SizingBorderOfView.thickness - bounds.y;
				}
			}
			else {
				
			}
			return bounds;
		}
	    return null;
    }
    
    /** 현재 시스템이 자바이면 "JAVA", 안드로이드면 "ANDROID"*/
    public static String CurrentSystem;
    public static String CurrentSystemIsJava = "JAVA"; 
    public static String CurrentSystemIsAndroid = "ANDROID";
    
    public static float vertScaleOfGap = 0.015f;
    public static float horzScaleOfGap = 0.02f;
    public static float scaleOfKeyboardX = 1.0f;
    public static float scaleOfKeyboardY = 0.4f;
   
    
    protected int backColor = Color.WHITE;
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
	
	
	public static Control capturedControl;
	
	
	public OnTouchListener listener;
	private OnTouchListener oldTouchListener;	
	
	
	
	
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
    
	/** 스택과 상관없이 hides, isOpen 상태만 바꾼다.*/
    public void setIsOpen(boolean isOpen) {
    	this.isOpen = isOpen;
    	this.hides = !isOpen;
    }
    
    	
	public boolean modified = true;
	
	
	
	
	/** /mnt/sdcard/janeSoft 를 말한다.*/
	public static String pathJaneSoft = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "janeSoft";
	
	/** 디폴트 패스, 안드로이드 라이브러리(SDK)의 path, 즉 /mnt/sdcard/janeSoft/gsoft 를 말한다.
	 * pathAndroid는 어디서이든 (예를들면 SettingsDialog.restoreSettings()) 바뀔 수 있다는 것을 주의한다.*/
	public static String pathAndroid = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "janeSoft" + File.separator + "gsoft";
	
	/** pathAndroid는 어디서이든 (예를들면 SettingsDialog.restoreSettings()) 바뀔 수 있기 때문에
	 * final 로 선언된 pathAndroid 가 필요하다./mnt/sdcard/janeSoft/gsoft
	 */
	public static final String pathAndroid_Final = pathAndroid;
	
	/** 디폴트 패스, 안드로이드 라이브러리의 path, 즉 /mnt/sdcard/janeSoft/gsoft-files 를 말한다.*/
	public static String pathGSoftFiles = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "janeSoft" + File.separator + "gsoft-files";
	
	/** 프로젝트소스의 path, 즉 /mnt/sdcard/janeSoft/project 를 말한다.*/
	public static String pathProjectSrc = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "janeSoft" + File.separator + "project";
	
	/** wifi 디렉토리의 path, 즉 /mnt/sdcard/janeSoft/wifi 를 말한다.*/
	public static String pathWifi = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "janeSoft" + File.separator + "wifi";
	
	/** help 디렉토리의 path, 즉 /mnt/sdcard/janeSoft/help 를 말한다.*/
	public static String pathHelpFiles = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "janeSoft" + File.separator + "help";
	
	
	/** EditRichText에서 삽입된 image 파일들의 패스, 
	 * 윈도우즈에서는 이미지가 삽입될 경우 이미지 다음에 있는 문자들을 잘못 읽는다.(스트림이상)
	 * DownloadedImage 디렉토리의 path, 즉 /mnt/sdcard/janeSoft/DownloadedImage 를 말한다.*/
	public static String DownloadedImageDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "janeSoft" 
			+ File.separator + "DownloadedImage";
	
	public static String pipePath = System.getProperties().getProperty("user.dir") + File.separator + "pipe";
	
	
	/** 제일 먼저 시작한다.*/
	static {
		
		File fileJaneSoft = new File(pathJaneSoft);
		fileJaneSoft.mkdirs();
		
		
	}
	
	
	public static boolean findEqualDir = false;
	
	public static boolean isDestroyedExceptNotify=false;
	public static String textOfNotification;
	
	
	/** Control마다 사용, 인스턴스*/
	public Rectangle prevSize;
	public boolean isMaximized;
	
	public static SizingBorder sizingBorder;
	//public static SizingBorderOfView sizingBorderOfView;
	
	public void backUpBounds() {
		prevSize = new Rectangle(bounds);
	}
	
	
	
	public static void showMessageToFile(String text) {
		FileOutputStream outputStream=null;
		try{
		
		File contextDir = view.getContext().getFilesDir();
		
		String absFilename = contextDir.getAbsolutePath() + File.separator + "ExitReason.txt";
		File file = new File(absFilename);
		
		outputStream = new FileOutputStream(file);
		IO.writeString(outputStream, text, TextFormat.UTF_16, false, true);
		}catch(Exception e) {
			
		}
		finally {
			FileHelper.close(outputStream);
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
        if (isOpen) {
    		if (this instanceof EditText) {
    		}
    		Control.controlStack.deleteItem(iName);
    		Control.controlStack.add(this);
    		        	}
    	else {
    		if (this instanceof EditText) {
    		}
    		//iNamesOfControlsToHide = findControlsToHide(bounds);
    		
    		Control.controlStack.deleteItem(iName);
    	}
    }
	
	
	public Control() {
		iName = countOfControls;
		countOfControls++;
	}
	
	
	/** 프로그램이 종료될때 호출된다. android.view.View.windowClosing()에서 호출되거나
	 * SettingsDialog.ThreadAutoTerminating 에서도 호출된다. 
	 * 프로그램이 사용하는 리소스들(네트워크스레드,타이머스레드 등), 알림을 해제하고 
	 * SettingsDialog 를 저장하고 종료한다.*/
	public static void exit(boolean argIsDestroyedExceptNotify) {
		isDestroyedExceptNotify = argIsDestroyedExceptNotify;
		if (ClipBoardX!=null) ClipBoardX.destroy(); 
		
		//CommonGUI_SettingsDialog.settingsDialog.setSettings();
		CommonGUI_SettingsDialog.settingsDialog.backupSettings();
		
		if (activity!=null) activity.finish();
		
		
	}
	
		
	public Container getContainer(Object owner) throws Exception {
		Object o = owner;
		int count = 0;
		
		do {
			try {
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
	public static boolean compareBounds(Rectangle bounds1, Rectangle bounds2) {
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
	public ArrayListInt findControlsToHide(Rectangle ownBounds) {
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
	
	public static boolean requiresChangingBounds(Rectangle bounds1, Rectangle bounds2) {
		int diff = 5;
		if (bounds2.height<0) return false;
		if (bounds2.width<0) return false;
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
	
	public void changeBounds(Rectangle paramBounds) {
		
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