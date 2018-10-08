package com.gsoft.common.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.view.View;
import com.gsoft.common.ColorEx;
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

public class Control_test {
	public static ControlStack controlStack = new ControlStack(20);
	
	public static class Container extends Control {
	}
	
	Object owner;
	public static View view;
    public RectangleF bounds;
    
    
    int backColor = Color.WHITE;
    public int textColor = Color.BLACK;
    
    public void setBackColor(int color) {
		backColor = color;
		textColor = ColorEx.reverseColor(backColor);
	}
    
    int alpha = 255;
	
    public String name;
	
	public int iName;	
	public static int countOfControls;
	
	//public boolean isMoveActionCaptured;
	public static Control capturedControl;
	
	
	public OnTouchListener listener;
	
	
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
	
	boolean isOpen;
	
	/** View의 mCanvas와 함께 사용되어 그리기 위한 buffer역할을 한다.*/
	public static Bitmap drawingCache;
	
	/** PartDrawing(부분그리기)메커니즘을 위해 true로 설정한다. 아니면 false*/
	public static boolean usesDrawingCache = false;
	
	/** drawingCache(PartDrawing)에 그리기 위한 작업공간이다. open()을 통해 열리기 전 화면을 
	 * backup하고 restore한다. 메뉴, 다이얼로그 등은 각자 갖고 있어야 한다.*/
	public int[] oldPixels;
	
	
	public static String pathAndroid = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "gsoft";
	
	
	
	public static ViewEx viewEx;
	
	public static Resources res;
	//private ArrayListInt indicesOfControlsToHide;
	private ArrayListInt iNamesOfControlsToHide;
	
	public static boolean isMaximized;
	
	
	/*public static void showMessageDelayed(String text) {
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
	}*/
	
	public static void showMessageToFile(String text) {
		FileOutputStream outputStream=null;
		try{
		
		File contextDir = view.getContext().getFilesDir();
		
		String absFilename = contextDir.getAbsolutePath() + File.separator + "ExitReason.txt";
		//String absFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + list[i];
		File file = new File(absFilename);
		
		outputStream = new FileOutputStream(file);
		IO.writeString(outputStream, text, TextFormat.UTF_16, false, true);
		}catch(Exception e) {
			
		}
		finally {
			FileHelper.close(outputStream);
		}
	}
		
		
	/*public void write(OutputStream os, TextFormat format) throws Exception {
		try {
			String className = getClass().getName();
			IO.writeString(os, className, format);
		}catch(Exception e) {
			throw e;
		}
		//IO.writeString(os, ";");
	}*/
	
	/*public static void showLogForMessageBox(boolean addOrReplace, String text) {
		if (Control.loggingForMessageBox!=null) Control.loggingForMessageBox.setText(!addOrReplace, text, false);
		if (Control.loggingForMessageBox!=null) Control.loggingForMessageBox.setHides(false);
		if (Control.view!=null) Control.view.invalidate();
	}*/
	
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
        		Control_test.controlStack.deleteItem(iName);
        		iNamesOfControlsToHide = findControlsToHide(bounds);
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
        		}
        	}
        	else {
        		
        		Control.controlStack.deleteItem(iName);
        		
        	}
        }
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
		}
		return r;
	}

}