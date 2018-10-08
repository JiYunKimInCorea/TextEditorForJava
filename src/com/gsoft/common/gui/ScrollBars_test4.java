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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

/** test*/
@SuppressWarnings("unchecked")
public class ScrollBars_test4 {
	
	com.gsoft.common.gui.Buttons.Button[] arr = new com.gsoft.common.gui.Buttons.Button[3];
	Object o = null;
	com.gsoft.common.gui.Buttons.Button button = (com.gsoft.common.gui.Buttons.Button)o;
	boolean instanced = o instanceof com.gsoft.common.gui.Buttons.Button;
	
	byte b = (byte) (28 * 9);
	char c = (char) (280000 * 10<<2);
	
	class AAA {
	}

	class BBB extends AAA {
	}

	interface AAI {
	}

	interface BBI {
	}

	interface CCI extends AAI, BBI {
	}

	class AAC extends AAA implements AAI, BBI {
	}
	
	class BBC implements AAI, BBI, CCI {
		
	}

	
	/** test2*/
	@SuppressWarnings("unchecked")
	enum ButtonState
	{  
		/** MouseDown*/
	    MouseDown,
	    // 윈도우즈에서 파티션들중 하나를 클릭한 경우
	 	// absFilename는 "c:\"가 되고 curDir을 루트로 정해준다.
	    Normal,
	    MouseOver
	}
	
	/**str*/
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
    
    /** ja_mo_code*/
    static char[] ja_mo_code = 
		{	
			/*new Mapping('0', 0),
			new Mapping('ㄱ', 0xA4A1),   new Mapping('ㅠ', 0xA4D0),    new Mapping('ㅡ', 0xA4D1),    
			new Mapping('ㅢ', 0xA4D2), 
			new Mapping('ㅣ', 0xA4D3)*/
		};
    
    /**toObjectArray()*/
    java.lang.Object[] toObjectArray() {
    	Object[] r = {new Integer(1), new Short((short) 2)};
    	return r;
    }
    
    /**aaa*/
    static int aaa(int a, int b) {
    	Class c = (new ScrollBars_test4().toObjectArray())[0].getClass();
    	return a+b;
    }
    
    int i = 10;
    int i2;
    //byte b = 3 + i; // int를 byte로 캐스트해야함
    byte b2 = (byte) (3 + 10 * (i=3-(i2=2))); // 3 + 10 i 3 i2 2 = - = * +
    
    /**indicesOfButtonsInGroup*/
    java.lang.Byte[] indicesOfButtonsInGroup = {0+127,1,2,3,4,5,6,7,8};
    
    int[][] colors = {
    		{Color.BLACK+255, Color.WHITE-aaa(2, 3), Color.RED+aaa(4, aaa(2, 3))/20}, 
    		{Color.YELLOW,Color.BLUE/*, Color.GREEN*/}
	};
    
    /** color2*/
    int[][][] colors2 = {
    		{/*{Color.BLACK, Color.WHITE, Color.RED},*/ 
    		 {Color.YELLOW,Color.BLUE, Color.GREEN}},
    		 
    		 {{Color.RED, Color.WHITE/*, Color.BLACK*/}, 
        	  {Color.GREEN,Color.BLUE, Color.YELLOW}}
	};//2면 2행 3열
    
    /**staticMemberVarOfScrollBars_test4*/
    static int staticMemberVarOfScrollBars_test4 = 10;
    /**nonStaticMemberVarOfScrollBars_test4*/
    int nonStaticMemberVarOfScrollBars_test4 = 10;
    
    
    /** 윈도우즈에서만 가능하다.
	 * @return 콜론을 뺀 c 나 d 등*/
	public static java.lang.String[] getPartitionSymbols() {
		ArrayListString r = new ArrayListString(10); 
		for (char c='a'; c<='z'; c++) {
			char[] arr = {c};
			String path = new String(arr);
			File file = new File(path+":"+File.separator);
			if (file.exists()) {
				String partition = file.getAbsolutePath();
				r.add(partition);
			}
		}
		return r.getItems();
	}
    
	/**writeCharKSC*/
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
    
    /** getDirectory()*/
    @SuppressWarnings("unchecked")
    public static java.lang.String getDirectory(String path)
    {
    	// 윈도우즈에서 파티션들중 하나를 클릭한 경우
		// absFilename는 "c:\"가 되고 curDir을 루트로 정해준다. 'a  'b
		String absFilename = "abc";
		
		String abc = "abc" + 3;
		String abc1 = abc + 3;
		String abc2 = abc + 3.5f;
		String abc3 = 3 + "abc";
		
		int ab=0;
		ab |= 3+2;
		int ba=ab+(ab=2)+3;
		
		ba=ab+(ab+=2)+3;
		
		ba=ab-(ab-=2.3f+2)*3;
		ba=ab-(ab-=(int)(2.3f+2))*3;

		ba=ab-((ab%=2)*3+2);
		ba=ab-((ab*=2.0f+2*3)*3+2);
		ba=ab-(((ab/=2+2.3f*3)*3+2)*2-2+(ba=0)+3);
		
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
    
    /** CheckParenthesis*/
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
		//boolean r = ScrollBars.HScrollBar.isMaximized;
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
			staticMemberVarOfScrollBars_test4 = 2;
			//nonStaticMemberVarOfScrollBars_test4 = 2; // static함수에서는 static이 아닌 멤버접근 불가능
			//a = 1; // static함수에서는 static이 아닌 멤버접근 불가능
			
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
			i += 1.0f + (char) f;
			i -= (char) f + 1;
			
			boolean bool = i>f==true;
			boolean bool2 = false==true!=i<=f;
			i = i<f && f>0 ? aaa(2,3)+1 : 0;
			
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
		
		/**RectForPage*/
		public RectForPage(Object owner, RectangleF bounds, int backColor,
				boolean isUpOrDown) {
			super();
			staticMemberVarOfScrollBars_test4 = 2;
			//nonStaticMemberVarOfScrollBars_test4 = 2; // 부모클래스의 static이 아닌 멤버변수를 접근 못한다.
			
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
	
	class A {
		void a() {
			// A클래스가 static이 아니므로 상위클래스의 static과 static이 아닌 멤버를 접근가능하다.
			staticMemberVarOfScrollBars_test4 = 5;  
		    nonStaticMemberVarOfScrollBars_test4 = 3;
			view = null;
		}
		
		// static도 top레벨도 아닌 클래스에서 static멤버를 정의할 수 없다.
		/*static int b=2;
		static void b() {
			
		}*/
	}
	
	/**HScrollBar*/
	static public class HScrollBar extends Control implements OnTouchListener {
		
		SpinControl spinControl;
		/** 스크롤막대의 왼쪽 사각형*/
		RectForPage rectForPageLeft;
		
		float widthOfBar;
		
		int widthOfCharsPerPage;
		
		
		/** HScrollBar*/
		public HScrollBar(Object owner, Context context, RectangleF bounds) {
			super();
			view = null;
			
			widthOfBar = bounds.width - 10 * spinControl.boundsLeft.width;
			
			boolean isUpOrDown1 = this.rectForPageLeft.isUpOrDown;
			
			boolean isTest = RectForPage.isUpOrDown_test;
			RectForPage.test((rectForPageLeft), 3);
		
		}
		
		/*public void changeBounds(RectangleF bounds) {
			this.bounds = bounds;
			spinControl.changeBounds(bounds);
			widthOfBar = bounds.width - 2 * spinControl.boundsLeft.width;
		}*/				
			
		/**draw()*/
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