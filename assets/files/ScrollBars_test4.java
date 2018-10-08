package com.gsoft.common.gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util.*;
import com.gsoft.common.*;
//import com.gsoft.common.Util.Stack;
import com.gsoft.common.interfaces.OnTouchListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

/** test*/
public class ScrollBars_test4 {
	/** test2*/
	enum ButtonState
	{  
		/** MouseDown*/
	    MouseDown,
	    // 윈도우즈에서 파티션들중 하나를 클릭한 경우
	 	// absFilename는 "c:\"가 되고 curDir을 루트로 정해준다.
	    Normal,
	    MouseOver
	}
	
	String str = "video/*";
        
    ButtonState buttonState;
    
    /*public static String[] SpecialKeys = {
		"@", "\\", "'", "\'", "\r", "PgUp", "@"
	};
    
    public static class Hangul {
		static char[] chars_shiftNotPressed = {
			'\\', '"', '\"', '\n', 'ㅅ',  'ㅢ', 
			'ㅝ', '\'', '.'
		};
    }*/
    
    static void writeCharKSC(OutputStream bos, char value) {
		if (0<value && value<=127) {
			byte[] buf = new byte[1];
			try {
				buf[0] = (byte)(value & 0xff);
				bos.write(buf, 0, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		else {
			int code = 0;
			byte[] buf = new byte[2];
			try {
				if (code!=0) {
					buf[0] = (byte)((code & 0xff00) >>> 8);
					buf[1] = (byte)((code & 0xff));
					bos.write(buf, 0, 2);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}
    
    public static String getDirectory(String path)
    {
    	// 윈도우즈에서 파티션들중 하나를 클릭한 경우
		// absFilename는 "c:\"가 되고 curDir을 루트로 정해준다. 'a  'b
		String absFilename = "abc";
		
    	String s = "/*";
    	char c = '\n';
    	String s3 = "*/";
    	char c2 = '\\';
    	char c3 = '\'';
    	String s4 = "'";
        int i;
        char[] name = new char[path.length()];
        path.getChars(0, name.length, name, 0);
        for (i = name.length - 1; i >= 0; i--)
        {
            if (name[i] == File.separator.charAt(0)/* || name[i]=='\\'*/)
            {
                break;
            }
        }
        if (i==-1) return "";
        return path.substring(0, i);
    }
    
    @SuppressWarnings("unchecked")
	int[][] CheckParenthesis(CodeString[] buffer) throws Exception
	{
		@SuppressWarnings(
				"rawtypes")
		//Stack<Object> s, stack;
		Stack stack;
		stack = new Stack();
		//Button[][] tempTable2 = new Button[100][ (int)( 2+(int)1 ) ];
		String[][] tempTable3 = new String[100][ (int)( 2+(int)1 ) ];
		//Stack<Character.Subset>[][] tempTable5 = new Stack<Character.Subset>[100][ (int)( 2+(int)1 ) ];
		Character.Subset[][] tempTable4 = new Character.Subset[100][ (int)( 2+(int)1 ) ];
		int[][] tempTable = new int[100][ (int)( 2+(int)1 ) ];
		for (int i=0, k=0, j=0; i<(long)buffer.length; i++) {
			if ( ((java.lang.Object)buffer[i]).equals("(") ) {
				stack.Push(buffer[(int)i+0]);
				stack.Push( (Object)(buffer[(int)i+0]) );
				tempTable[k++][++k] = i;
			}
		}
		boolean r = ScrollBars.HScrollBar.isMaximized;
		ButtonState bs = ButtonState.MouseDown;
		CommonGUI.colorDialog.IsPointIn(null);
		
		Thread.sleep(1000);
		
		Character.Subset s;
		Character.Subset[] s2;
		
		Stack<Stack<Character.Subset>> stack2 = new Stack(); 
		Stack<Character.Subset> stack3 = null;
		stack2.Push(stack3);
		
		Stack<Stack<Character.Subset>>[] stack4 = null;
		stack4[0].Push(stack3);
		
		LangDialog d = new LangDialog(null, null); 
		
		try {
			JarFile jarFile = new JarFile("", false);
			Enumeration<java.util.jar.JarEntry> e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				InputStream is = jarFile.getInputStream(entry);
				
			}
			jarFile.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
    
	public static float VScrollBarWidthScale = -0.065f;
	
	public SizeF[] controls = new SizeF[5+1];
	
	
	
	/** a*/
	int a=-1+2;
	static int staticVar = 3;
	public static View view;
	
	public static void a(int b) {
		staticVar = 5;
		setScrollBarScale(null);
	}
	
	/** viewEx의 initControls에서 호출된다.*/
	public static void setScrollBarScale(View view) {
		VScrollBarWidthScale = 0.065f;
	}
	
	/** 스크롤막대의 사각형(pageUp, pageDown, pageLeft, pageRight)*/
	public static class RectForPage extends Control {
		Paint paint = new Paint();
		int backColor;
		/** up이면 true, down이면 false*/
		boolean isUpOrDown;
		static boolean isUpOrDown_test;
		int a=-1;
		
		protected android.view.View view; 
		
		static RectForPage test(RectForPage r, int b) {
			ScrollBars_test4.view = null;
			int a=0;
			a++;
			Control c = null;
			RectForPage rect = (RectForPage)c; // 실행시 ClassCastException
			Control control = (Control)rect;
			Control control2 = rect;
			float f;
			int i = 0;
			f = i; // 초기화안하면 에러
			//i = f; // 실수를 정수에 넣을때 타입캐스트 필요
			i += (char) f + 1.0f; // 실수를 정수에 넣을때 타입캐스트 필요
			i -= (char) f + 1;
			
			boolean bool = i>f==true;
			boolean bool2 = false==true!=i<=f;
			i = i<f ? 1 : 0;
			
			char ch = 0;
			i = ch; // 초기화안하면 에러
			i = ch + 1;
			ch %= (char) (ch + 1); // char로 캐스팅 필요
			f = ch;
			ch = (char) i; // char 타입캐스트해야함
			long l = 0;
			f = l;
			return r;
		}
		
		public RectForPage(Object owner, RectangleF bounds, int backColor,
				boolean isUpOrDown) {
			super();
			this.view = null;
			int c = (int) ((int)bounds.height*(int)0.3f);
						
			paint.setStyle(Style.FILL);
			String className = getClass().getName();
			test(test(this, this.alpha), ((this)).backColor + (2+5)*3);
			
			a(2);
			//(new RectForPage(owner, bounds, backColor, isUpOrDown)).restore();
			test( this, ((new RectForPage(owner, bounds, (backColor)+((1)-2)*2, isUpOrDown))).a+(2+1+3)*(1+0) );
			a=-3+4+5;
			
			switch (1) {
			case 0:
			case 1:
			}
			switch (1) {
			case 2: switch(2) {
					case 4:
					case 5:
					}
			case 3:if (1>2) {
				
				   }
				   else {
						
				   }
			}
			
			if (a>1) if (a>2) a=0;
			
			while (a>1) 
				if (a>2) {} else if (a==1) {} else {}
			
			//int a;
			if (a>0) while(a==0) {
				int a;
				a=0;
			}
			
			float s = 0;
			if (s<0 || s>1)
				try {
					throw new Exception("ScrollBar error");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {					
				}
		}
		
		/*public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
	    	if (event.actionCode==MotionEvent.ActionDown) {
	    		a=0;
	    		int a;
				if (super.onTouch(event,scaleFactor)==false) {
					a= this.a + 2;
					return false;
				}
	    		return true;
	    	}	
	    	
	    	return false;
	    }*/
		
	}
	
	static class A {
		void a() {
			view = null;
		}
	}
	
		
	static public class HScrollBar extends Control implements OnTouchListener {
		
		SpinControl spinControl;
		/** 스크롤막대의 왼쪽 사각형*/
		RectForPage rectForPageLeft;
		
		float widthOfBar;
		
		int widthOfCharsPerPage;
		
		
				
		public HScrollBar(Object owner, Context context, RectangleF bounds) {
			super();
			view = null;
			
			widthOfBar = bounds.width - 10 * spinControl.boundsLeft.width;
			
			boolean isUpOrDown1 = this.rectForPageLeft.isUpOrDown;
			
			boolean isTest = RectForPage.isUpOrDown_test;
			RectForPage.test(rectForPageLeft, 3);
		
		}
		
		/*public void changeBounds(RectangleF bounds) {
			this.bounds = bounds;
			spinControl.changeBounds(bounds);
			widthOfBar = bounds.width - 2 * spinControl.boundsLeft.width;
		}*/				
			
		
		@Override
		public void draw(Canvas canvas) {
			synchronized(this) {
			try{
			spinControl.draw(canvas);
			rectForPageLeft.draw(canvas);
			}catch(Exception e) {
	    		
	    	}
			}
		}

		@Override
		public void onTouchEvent(Object sender, MotionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

}