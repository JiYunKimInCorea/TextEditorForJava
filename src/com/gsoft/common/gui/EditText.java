package com.gsoft.common.gui;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import com.gsoft.common.CompilerHelper;
import com.gsoft.common.Code.CodeChar;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Compiler_types.HighArray_CodeString;
import com.gsoft.common.Compiler_types.Language;
//import com.gsoft.common.Compiler_gui.MenuClassList;
import com.gsoft.common.ColorEx;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.Compiler_gui.TextView;
import com.gsoft.common.CommonGUI_SettingsDialog;
import com.gsoft.common.Compiler;
import com.gsoft.common.Compiler_types;
import com.gsoft.common.Compiler_types.AddCharReallyMode;
import com.gsoft.common.IO;
import com.gsoft.common.PaintEx;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util;
import com.gsoft.common.Util.Array;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListChar;
import com.gsoft.common.Util.ArrayListCodeChar;
import com.gsoft.common.Util.ArrayListCodeString;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.Math;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Control.Container;
import com.gsoft.common.gui.IntegrationKeyboard.Hangul;
import com.gsoft.common.gui.IntegrationKeyboard.Mode;
import com.gsoft.common.gui.Menu.MenuType;
import com.gsoft.common.gui.ScrollBars.HScrollBar;
import com.gsoft.common.gui.ScrollBars.VScrollBarLogical;
import com.gsoft.common.gui.SettingsDialog.Settings;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.common.R.R;

public class EditText extends Container implements OnTouchListener {
	
	public static class Edit {
		
		public static int selectColor;
		public static int foundColor;
		public static int cursorColor;
		
		public static void setBackColor(int backColor) {
			/*int textColor = ColorEx.reverseColor(backColor);
			int middleColor = ColorEx.middle(backColor, textColor);
			selectColor = ColorEx.mulElements(middleColor, 1);
			foundColor = ColorEx.mulElements(middleColor, 0.5f);
			cursorColor = ColorEx.mulElements(middleColor, 1);*/
			selectColor = ColorEx.darkerOrLighter(Color.BLUE, -100);
			foundColor = ColorEx.darkerOrLighter(Color.GREEN, -100);
			cursorColor = ColorEx.darkerOrLighter(Color.RED, -50);
		}
		
		/** 모든 EditText인스턴스들이 menuFunction를 공유한다.*/
		public static MenuWithClosable menuFunction;
		
		static final String[] Menu_Function = { 
			"Undo(Ctrl+z)", "Redo(Ctrl+y)", 
			"Copy(Ctrl+c)", "Cut(Ctrl+x)", "Paste(Ctrl+v)", 
			"Find/Replace(Ctrl+f)", 
			"Select all(Ctrl+a)",
			"Show UndoBuffer", "Show RedoBuffer"
		};
		
		
		/** 모든 EditText인스턴스들이 menuFontSize를 공유한다.*/
		public static MenuWithClosable menuFontSize;
		
		/** 모든 EditText인스턴스들이 menuFontSize를 공유한다.*/
		public static FontSizeDialog fontSizeDialog;
		
		static final String[] Menu_FontSize = { 
			"1%", "2%", "3%", "4%", "5%", "6%", "7%", "9%", "11%", "13%", Control.res.getString(R.string.font_others)
		};
		
		public static FindReplaceDialog findReplaceDialog;
		
		public static char[] find_separators = {' ', ';', ',', ':', '.', '\t', '\n', 
				'+', '-' , '*', '/', '\'', '\"', '<', '>', '=', 
				'{', '}', '(', ')', '[', ']'};
		
		public static void createFindReplaceDialog(Rectangle boundsOfEditText) {
			if (Edit.findReplaceDialog!=null) return;
			/*float w = bounds.width*0.7f;
			float h = bounds.height*0.8f;
			float x = bounds.x + bounds.width/2 - w/2;
			float y = view.getHeight()*0.15f;*/
			Rectangle bounds = new Rectangle(boundsOfEditText);
			Edit.findReplaceDialog = new FindReplaceDialog(bounds);		
		}
		
		
	}//public static class Edit {
	
	
	protected static Settings settings = CommonGUI_SettingsDialog.settings;
	
	public static boolean isTripleBuffering = settings.isTripleBuffering;
	
		
	
	
	public static String[] namesOfButtonsOfToolbar = {
		"S", "M", "FN", "O", "R/W", "U"	
	};
	
	
	
	public MenuWithAlwaysOpen toolbar;
	
	
	
	boolean hasToolbarAndMenuFontSize;
	
	public enum ScrollMode {		
		VScroll,
		Both
	}
	
	public boolean isReadOnly;
	
	/** 한 줄만 쓰이면 true, 그렇지 않으면 false*/
	private boolean isSingleLine = true;
	public void setIsSingleLine(boolean b) {
		this.isSingleLine = b;
		if (this.isSingleLine) {
			if (vScrollBar!=null) {
				vScrollBar.hides = true;
			}
			if (hScrollBar!=null) {
				hScrollBar.hides = true;
			}
			this.hasToolbarAndMenuFontSize = false;
		}
	}
	boolean getIsSingleLine() {
		return isSingleLine;
	}
	
	private CodeString text;
		
	public CodeString[] textArray;
	
	float lineHeight;
	public float fontSize;
	float descent;
	int gapX;
	
	ScrollMode scrollMode;
	VScrollBarLogical vScrollBar;
	HScrollBar hScrollBar;
	
	int vScrollBarWidth;
	int vScrollPos;
	
	public int numOfLines;
	int numOfLinesPerPage;
	//int numOfLinesInPage;
		
	int hScrollBarHeight;
	int widthOfhScrollPos;
	float maxLineWidth;
	int lineNumOfMaxWidth;
	int widthOfCharsPerPage  =1;
	//int widthOfCharsInPage;
	int widthOfTotalChars;
	int widthOfhScrollInc;
	
	boolean isCursorSeen;
	int valueOfCursorRelativeToHScroll;
	float part1OfChar;
	float part2OfChar;
	
	int rationalBoundsWidth;
	int rationalBoundsHeight;
	
	public Point cursorPos;
	int indexOfCursorInText = -1;	// 현재 입력 문자의 Text에서의 인덱스
	int numOfLinesInText;	// setTextVScroll로 수정되는 줄수, 즉 \n을 만날 때까지의 줄수
	
	boolean isBkSpThatMakeNullStr = false;
	
	
	public boolean isSelecting;
	int selectLenY;
	/** editText_Listener()를 참조한다.*/
	Point selectP1, selectP2;
	/**selectIndices 좌표 구성은 makeSelectIndices()를 참조한다.*/
	Point[] selectIndices = new Point[100];
	int selectIndicesCount = 0;
	
	/** selectStartLine,selectEndLine는 paste할 때 ActionDown이 일어날 수도 있고, 
	 * paste후에 다시 선택을 할 수도 있기 때문에 paste한 라인들을 가리키기 위해 사용한다.
	 * editText_listener(ActionDown)에서 선택라인의 정보를 저장하고 backupForUndo에서 사용한다.*/
	int selectStartLine;
	int selectEndLine;
	
	static final int Select_FirstLine = 0;
	static final int Select_MiddleLine = 1;	
	static final int Select_LastLine = 2;
	
	// 이미 선택된 텍스트를 크기변환 등 변환 후에 다시 선택을 하기 위해 사용한다.
	boolean makingSelectP1P2OutOfEvent;
	int selectP1Index, selectP2Index;
	Point selectP1Logical, selectP2Logical; // 차례로 선택된 텍스트의 앞과 뒤를 가리킨다. 
	
	int selectIndicesCountForCopy;
	boolean isCopied;
	String copiedText = "";
	
	
	//boolean isFinding;
	int findLenY;
	Point[] findIndices = new Point[100];
	int findIndicesCount = 0;
	int findIndicesCountForCopy;
	
	
	Point curFindPos = new Point(0, 0);
	boolean isFound;
	Point pointFindStart = new Point(0, 0);
	Point pointFindEnd = new Point(0, 0);	
	Point findP1 = new Point(0,0);
	Point findP2 = new Point(0,0);
	ArrayList listFindPos = new ArrayList(100);
	Point oldCursorPos = new Point(0,0);
	
	Mode keyboardMode;
	IntegrationKeyboard.Hangul.Mode hangulMode;
	
	
	public boolean isModified;
	//public String curFileName = "";
	
	boolean finishingDrawingOnce = false;
	
	
	static class UndoBuffer {
		static class Pair {
			CodeString text;
			Point cursorPos;
			String command;
			Object addedInfo;
			public boolean isSelecting;
			/** ReplaceAll에서 빈 스트링("")으로 대체된 경우 null 이 아니다.
			 * replaceAll할때에 검색된 좌표들을 백업한다.*/
			ArrayList listOfFindPos;
			/** replaceAll할때에 replace된 좌표들을 백업한다.*/
			ArrayList listOfReplacePos;
		}
		private ArrayListCodeString buffer = new ArrayListCodeString(50);
		private ArrayList arrayCursorPos = new ArrayList(50);
		private ArrayListString bufferCommand = new ArrayListString(50);
		private ArrayList arrayAddedInfo = new ArrayList(50);
		private ArrayList arrayIsSelecting = new ArrayList(50);
		private ArrayList arrayListOfFindPos = new ArrayList(50);
		private ArrayList arrayListOfReplacePos = new ArrayList(50);
		
		
		void reset() {
			buffer.reset();
			arrayCursorPos.reset();
			bufferCommand.destroy();
			arrayAddedInfo.reset();
			arrayIsSelecting.reset();
			arrayListOfFindPos.reset();
			arrayListOfReplacePos.reset();
		}
		/*void push(Point cursorPos, CodeString text) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add("");
			arrayAddedInfo.add(null);
			arrayIsSelecting.add(null);
			arrayMessage.add(null);
		}*/
		void push(Point cursorPos, CodeString text, String charA) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(null);
			arrayIsSelecting.add(null);
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, CodeString text, String charA, Object addedInfo) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(null);
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
		}
		
		void push(Point cursorPos, CodeString text, String charA, Object addedInfo, boolean isSelecting) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(new Boolean(isSelecting));
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, CodeString text, String charA, Object addedInfo, boolean isSelecting, ArrayList listFindPos) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(new Boolean(isSelecting));
			arrayListOfFindPos.add(listFindPos);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, CodeString text, String charA, Object addedInfo, boolean isSelecting, ArrayList listFindPos, ArrayList listReplacePos) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(new Boolean(isSelecting));
			arrayListOfFindPos.add(listFindPos);
			arrayListOfReplacePos.add(listReplacePos);
		}
		UndoBuffer.Pair pop() {
			UndoBuffer.Pair pair = new UndoBuffer.Pair();
			pair.text = buffer.getItem(buffer.count-1);
			pair.cursorPos = (Point)arrayCursorPos.getItem(arrayCursorPos.count-1);			
			String command = bufferCommand.getItem(bufferCommand.count-1);
			pair.command = command;
			pair.addedInfo = arrayAddedInfo.getItem(arrayAddedInfo.count-1);
			Boolean b = (Boolean) arrayIsSelecting.getItem(arrayIsSelecting.count-1);
			if (b!=null) {
				pair.isSelecting = b.booleanValue();
			}
			pair.listOfFindPos = (ArrayList) arrayListOfFindPos.getItem(arrayListOfFindPos.count-1);
			pair.listOfReplacePos = (ArrayList) arrayListOfReplacePos.getItem(arrayListOfReplacePos.count-1);
			buffer.count--;
			arrayCursorPos.count--;
			bufferCommand.count--;
			arrayAddedInfo.count--;
			arrayIsSelecting.count--;
			arrayListOfFindPos.count--;
			arrayListOfReplacePos.count--;
			return pair;
		}
	}
	
	static class RedoBuffer {
		static class Pair {
			CodeString text;
			/** 선택상태에서 백키나 delete키를 눌러서 한번에 지울경우에 사용.
			 * isSelecting이 true 일때 cursorPos는 p1이다.<br>
			 *  선택줄일때 p1은 왼쪽 위를 말하고 p2는 오른쪽 아래를 말한다.*/ 
			Point cursorPos;
			String command;
			Object addedInfo;
			/** 선택상태에서 백키나 delete키를 눌러서 한번에 지울경우에 사용*/
			public boolean isSelecting;
			/** 선택상태에서 백키나 delete키를 눌러서 한번에 지울경우에 사용.
			 * isSelecting이 true 일때 cursorPos는 p1이다.<br>
			 *  선택줄일때 p1은 왼쪽 위를 말하고 p2는 오른쪽 아래를 말한다.*/ 
			Point p2;
			/** ReplaceAll에서 빈 스트링("")으로 대체된 경우 null 이 아니다.
			 * replaceAll할때에 검색된 좌표들을 백업한다.*/
			ArrayList listOfFindPos;
			/** replaceAll할때에 replace 된 좌표들을 백업한다.*/
			ArrayList listOfReplacePos;
		}
		private ArrayListCodeString buffer = new ArrayListCodeString(50);
		private ArrayList arrayCursorPos = new ArrayList(50);
		private ArrayListString bufferCommand = new ArrayListString(50);
		private ArrayList arrayAddedInfo = new ArrayList(50);
		private ArrayList arrayIsSelecting = new ArrayList(50);
		private ArrayList arrayPointP2 = new ArrayList(50);
		private ArrayList arrayListOfFindPos = new ArrayList(50);
		private ArrayList arrayListOfReplacePos = new ArrayList(50);
		
		void reset() {
			buffer.reset();
			arrayCursorPos.reset();
			bufferCommand.destroy();
			arrayAddedInfo.reset();
			arrayIsSelecting.reset();
			arrayPointP2.reset();
			arrayListOfFindPos.reset();
			arrayListOfReplacePos.reset();
		}
		void push(Point cursorPos, CodeString text, String charA) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(null);
			arrayIsSelecting.add(null);
			arrayPointP2.add(null);
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, CodeString text, String charA, Object addedInfo) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(null);
			arrayPointP2.add(null);
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, CodeString text, String charA, Object addedInfo, boolean isSelecting, Point p2) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(new Boolean(isSelecting));
			arrayPointP2.add(p2);
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, CodeString text, String charA, Object addedInfo, boolean isSelecting, Point p2, ArrayList listFindPos) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(new Boolean(isSelecting));
			arrayPointP2.add(p2);
			arrayListOfFindPos.add(listFindPos);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, CodeString text, String charA, Object addedInfo, boolean isSelecting, Point p2, ArrayList listFindPos, ArrayList listReplacePos) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(new Boolean(isSelecting));
			arrayPointP2.add(p2);
			arrayListOfFindPos.add(listFindPos);
			arrayListOfReplacePos.add(listReplacePos);
		}
		RedoBuffer.Pair pop() {
			RedoBuffer.Pair pair = new RedoBuffer.Pair();
			pair.text = buffer.getItem(buffer.count-1);
			pair.cursorPos = (Point)arrayCursorPos.getItem(arrayCursorPos.count-1);			
			String command = bufferCommand.getItem(bufferCommand.count-1);
			pair.command = command;
			pair.addedInfo = arrayAddedInfo.getItem(arrayAddedInfo.count-1);
			Boolean b = (Boolean) arrayIsSelecting.getItem(arrayIsSelecting.count-1);
			if (b!=null) {
				pair.isSelecting = b.booleanValue();
			}
			pair.p2 = (Point) arrayPointP2.getItem(arrayPointP2.count-1);
			pair.listOfFindPos = (ArrayList) arrayListOfFindPos.getItem(arrayListOfFindPos.count-1);
			pair.listOfReplacePos = (ArrayList) arrayListOfReplacePos.getItem(arrayListOfReplacePos.count-1);
			buffer.count--;
			arrayCursorPos.count--;
			bufferCommand.count--;
			arrayAddedInfo.count--;
			arrayIsSelecting.count--;
			arrayPointP2.count--;
			arrayListOfFindPos.count--;
			arrayListOfReplacePos.count--;
			return pair;
		}
	}
	
	UndoBuffer undoBuffer = new UndoBuffer();
	RedoBuffer redoBuffer = new RedoBuffer();
	
	
	// PaintEx를 Paint대신에 생성하여 measureText에서 \n을 0으로 계산한다.
	PaintEx paint = new PaintEx();
	Paint paintOfBorder = new Paint();
	
	Context context;
	Bitmap bitmapCursor;
	
	OnTouchListener oldTouchListener;
	
	//public IntegrationKeyboard keyboard = Control.keyboard;
		
	
	static final String NewLineChar = "\n";
	static final String BackspaceChar = "%bk";
	static final String DeleteChar = "%dl";
	static final String TabChar = "\t";
	
	
	static final int MaxLineCount = 100;
	
	//public boolean isSizing;
	
	boolean isMoveActionCaptured_onlyEditText;
	
	//View view;
	
	public Rectangle totalBounds;

	private Point oldDownPoint = new Point(0,0);

	/** 에디트텍스트가 툴바를 가지고있는 경우 사이즈가 바뀔 때 툴바의 위치도 
	 * 바뀌면 true, 그렇지 않으면 false.changeBounds참조
	 */
	private boolean isDockingOfToolbarFlexiable;

	//Preprocessor preProcessor;
	
	//public boolean isJava;

	protected Language lang;

	private Rectangle boundsOfSizingBorder = new Rectangle();

	//private Compiler compiler;

	boolean isSelected;



	private IntegrationKeyboard keyboard = CommonGUI.keyboard;



	private TextView textViewLogBird = CommonGUI.textViewLogBird;

	private TextView textView;

	
	
	
	
	/*public boolean setIsProgramCode(Compiler.Language lang, String input, boolean force) {
		try {
		this.lang = lang;
		if (lang!=null) {
			//if (isModified || force) { // isModified가 true이면 새로 만든다.
				this.compiler = new Compiler();
				compiler.start2(input, lang, backColor);
				isModified = false;
				return true;
			//}
		}
		return false;
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return false;
		}
	}*/
	
	
	public void setBackColor(int backColor) {
		super.setBackColor(backColor);
		//paintOfBorder.setColor(textColor);
		paintOfBorder.setColor(/*Compiler.keywordColor*/Color.LTGRAY);
		
		/*if (compiler!=null) {
			compiler.setBackColor(backColor);
		}
		
		int j, i;
		for (j=0; j<numOfLines; j++) {
			CodeString line = textArray[j];
			for (i=0; i<line.count; i++) {
				CodeChar c = line.charAt(i); 
				if (c.color==backColor) {
					c.color = textColor;
				}
			}
		}*/
	}
	
	
	
	/*public CodeString getCompileOutput(Language lang) {
		if (compiler!=null) return compiler.strOutput;
		return null;
	}*/
	
	public void write(OutputStream os, TextFormat format) {
		String text = getText().str;
		IO.writeString(os, text, format, false, true);
		
	}
	
	public static String Read(InputStream is, TextFormat format)  throws Exception {
		String text = IO.readString(is, format);
		return text;
	}
	
	/** Java 스트림을 사용하여 파일을 읽는다.*/
	public String read(InputStream is, TextFormat format) throws Exception
	{
		isModified = true;
		undoBuffer.reset();
		redoBuffer.reset();
		return IO.readString(is, format);
	}
	
	/** Java NIO를 사용하여 파일을 읽는다.*/
	/*public String read(String path, TextFormat format) throws EncodingFormatException
	{
		isModified = true;
		undoBuffer.reset();
		redoBuffer.reset();
		return IO.readString_UsingJavaNIO(path, format);
	}*/
	
	/**hides가 false이고 editText의 현재상태가 최대화가 아니면 
	 * (키보드와 SizingBorder의 영역을 바꿔줌과 함께) 키보드를 자동으로 보여주고
	 * 현재상태가 최대화상태이면 키보드를 숨겨준다.
	 * hides가 true이면 SizingBorder를 숨긴다.*/
	public synchronized void setHides(boolean hides) {		
		super.setHides(hides);
		if (isReadOnly) return;
		if (hides==false) {
			//Control.totalBoundsOfEditText = totalBounds;
			if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
				CommonGUI.keyboard.setOnTouchListener(this);
				if (isMaximized()==false) {
					//backUpBounds();
					changeBoundsOfKeyboardAndSizingBorder(bounds);
					CommonGUI.keyboard.setHides(false);
					//Control.isMaximized = false;
				}
				else {
					keyboard .setHides(true);
					//Control.isMaximized = true;
				}
			}
		}
		else {
			if (Control.sizingBorder.getHides()==false) 
				Control.sizingBorder.setHides(true);
		}
		
	}
	
	/** 보이는 상태에서 바운드가 바뀔때는 스택아래 컨트롤들을 복원했다가 
	 * 다시 숨겨지는 컨트롤들을 결정한다.
	 * 보이지 않는 상태에서는 바운드를 바꿀 수 없다.
	 * @param paramBounds : 툴바제외 영역
	 */
	public void changeBoundsSafe(Rectangle paramBounds) {
		changeBounds(paramBounds);
		//setHides(true);
		//setHides(false);
	}
	
	/** view에서 sizingBorder가 바뀔시 호출, 
	 * 이전 바운드와 새로운 바운드간의 차이가 있을 때만 바운드가 바뀐다.
	 * @param boundsOfEditText : 툴바제외 영역*/
	public void resize(Rectangle boundsOfEditText) {
		if (Control.requiresChangingBounds(bounds, boundsOfEditText)) {
			if (isMaximized()==false) backUpBounds();
			boolean isSizingBorderHides = Control.sizingBorder.getHides();
			changeBoundsOfKeyboardAndSizingBorder(boundsOfEditText);
			changeBoundsSafe(boundsOfEditText);
			if (isSizingBorderHides==false) Control.sizingBorder.setHides(false);
		}
	}
	
	public void changeBoundsOfKeyboardAndSizingBorder(Rectangle boundsOfEditText) {
		int viewHeight = view.getHeight();
		int viewWidth = view.getWidth();
		int heightOfGap = (int)(viewHeight * vertScaleOfGap);
		int top = boundsOfEditText.bottom()+heightOfGap;
		Rectangle boundsOfIntegrationKeyboard = new Rectangle(0, top,
				(int)(viewWidth*scaleOfKeyboardX), viewHeight-top);
		if (viewHeight-top<0) {
			boundsOfIntegrationKeyboard.height = (int)(viewHeight*Control.scaleOfKeyboardY);
		}
		
		if (keyboard!=null) {
			if (Control.requiresChangingBounds(keyboard.bounds, boundsOfIntegrationKeyboard)) {
				keyboard.changeBounds(boundsOfIntegrationKeyboard);
				boundsOfSizingBorder.x = totalBounds.x;
				boundsOfSizingBorder.width = totalBounds.width;
				boundsOfSizingBorder.y = boundsOfEditText.bottom() + 1;
				boundsOfSizingBorder.height = keyboard.buttons[0].bounds.y - boundsOfSizingBorder.y;
				if (sizingBorder!=null) sizingBorder.bounds = boundsOfSizingBorder;
			}
		}
	}
	
	/** bounds가 바뀔 때 호출, setHides에서 호출*/
	public void backUpBounds() {
		//prevSize = new RectangleF(totalBounds);
		//if (prevSize!=null) prevSize.copy(totalBounds);
		//else prevSize = new RectangleF(totalBounds);
		if (prevSize!=null) prevSize.copy(totalBounds);
		else prevSize = new Rectangle(totalBounds);
		//Control.prevSizeOfEditText = prevSize;
		//prevSizeOfKeyboard.copy(keyboard.bounds);
		//if (sizingBorder!=null) prevSizeOfSizingBorder.copy(sizingBorder.bounds);		
	}
	
	public void setMaximized(boolean maxOrPrev) {
		if (maxOrPrev) {
			this.isMaximized = true;
			int viewHeight = view.getHeight();
			if (isMaximized()==false) backUpBounds();
			
			int x = bounds.x;
			int y = bounds.y;
			int w = view.getWidth();
			int h = view.getHeight();
			Rectangle bounds = new Rectangle(x,y,w,h);
			
			if (Control.sizingBorder.getHides()==false) 
				Control.sizingBorder.setHides(true);
			
			//changeBoundsSafe(bounds);
			changeBounds(bounds);
			 
			//setHides(false);
			
			if (keyboard!=null) keyboard.setIsOpen(false);
		}
		else {
			//if (keyboard!=null) keyboard.setHides(false);
			//sizingBorder.setHides(true);
			this.isMaximized = false;
			
			Rectangle newBoundsOfEditText = new Rectangle(prevSize);
			newBoundsOfEditText.x = bounds.x;
			newBoundsOfEditText.width = bounds.width;
						
			//changeBoundsSafe(newBoundsOfEditText);
			changeBounds(newBoundsOfEditText);
			
			this.changeBoundsOfKeyboardAndSizingBorder(newBoundsOfEditText);
			if (keyboard!=null) keyboard.setHides(false);
			
			//setHides(false);
			
			/*RectangleF newBoundsOfKeyboard = new RectangleF(prevSizeOfKeyboard);
			if (keyboard!=null) keyboard.changeBounds(newBoundsOfKeyboard);
			
			RectangleF newBoundsOfBorder = new RectangleF(prevSizeOfSizingBorder);
			sizingBorder.changeBounds(newBoundsOfBorder);*/
		}
	}
	
	void setDescentAndLineHeight(float fontSize) {
		this.descent = fontSize * 0.25f;
		this.lineHeight = this.fontSize + this.descent;
	}
	
	/**paramBounds:툴바를 포함한 bounds*/
	public EditText(boolean hasToolbarAndMenuFontSize, boolean isDockingOfToolbarFlexiable, Object owner, 
			String name, Rectangle paramBounds, float fontSize, 
			boolean isSingleLine, CodeString text, ScrollMode scrollMode, int backColor) {
		super();
		this.owner = owner;
		this.name = name;
		this.backColor = backColor;
		
		setBackColor(backColor);
		
		this.fontSize = fontSize;
		setDescentAndLineHeight(fontSize);
		
		this.bounds = new Rectangle(paramBounds);
		this.totalBounds = new Rectangle(paramBounds);	
		
		this.hasToolbarAndMenuFontSize = hasToolbarAndMenuFontSize;
		this.isDockingOfToolbarFlexiable = isDockingOfToolbarFlexiable;
		if (isSingleLine) {
			hasToolbarAndMenuFontSize = false;
			scrollMode = ScrollMode.Both;
		}
		if (hasToolbarAndMenuFontSize) {
			Rectangle boundsOfToolbar;
			boundsOfToolbar = new Rectangle(paramBounds.x, paramBounds.y, 
					(int)(paramBounds.width*0.1f), paramBounds.height);
			createToolbar(boundsOfToolbar);
			
			Rectangle boundsOfEditText = new Rectangle(boundsOfToolbar.right(), paramBounds.y, 
					(int)(paramBounds.width*0.9f), paramBounds.height);
			
			this.bounds.copy(boundsOfEditText);
			//this.totalBounds = paramBounds;		
		
			createMenuFontSize();
			createFontSizeDialog();
			
			createMenuFunction();
			Edit.createFindReplaceDialog(bounds);
			
			this.createTextView();
		}
		
		
		
		
		this.isSingleLine = isSingleLine;
		this.text = text;
		this.numOfLines = 1;
		//this.backColor = Color.WHITE;
		
		this.gapX = (int) (view.getWidth() * 0.02f);		
		this.cursorPos = new Point(0,0);
		
		context = view.getContext();
		
		textArray = new CodeString[MaxLineCount];
		int i;
		for (i=0; i<textArray.length; i++) {
			textArray[i] = new CodeString("", textColor);
		}
		
		paint.setTypeface(Control.typefaceDefault);
		//paint.setColor(Color.BLACK);
		paint.setTextSize(fontSize);
		paint.setStyle(Style.FILL);
		paintOfBorder.setStyle(Style.STROKE);
		if (backColor!=Color.BLACK) {
			paintOfBorder.setColor(ColorEx.darkerOrLighter(backColor, -100));
		}
		else {
			paintOfBorder.setColor(ColorEx.darkerOrLighter(backColor, 100));
		}
		
		//bitmapCursor = ContentManager.LoadBitmap(context, R.drawable.cursor);
		
		this.scrollMode = scrollMode;		
		bound(BoundMode.Create, true);
		setText(0, text);
		
		if (isMaximized()==false) backUpBounds();
		
		if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
    			isTripleBuffering)  this.drawToImage(mCanvas);
	}
	
	/** 스크롤부분과 관련된 여러 속성값들을 설정한다.*/	
	void boundAttributes(BoundMode boundMode) {
		if (scrollMode==ScrollMode.VScroll) {
			if (boundMode==BoundMode.Create) {
				this.vScrollBarWidth = ScrollBars.getScrollBarSize();
			}
			//this.vScrollPos = 0;
			rationalBoundsWidth = (int) (bounds.width - 2*gapX - vScrollBarWidth);
			rationalBoundsHeight = bounds.height;
			numOfLinesPerPage = (int)(bounds.height / lineHeight);
			//numOfLinesInPage = numOfLines - vScrollPos;
			//numOfLinesInPage = Math.min(numOfLinesPerPage, numOfLinesInPage);
		}		
		else if (scrollMode==ScrollMode.Both) {
			if (boundMode==BoundMode.Create) {
				this.vScrollBarWidth = ScrollBars.getScrollBarSize();
				this.hScrollBarHeight = ScrollBars.getScrollBarSize();
			}
			
						
			//this.vScrollPos = 0;
			widthOfhScrollInc = (int)fontSize;
			
			if (isSingleLine) {
				rationalBoundsWidth = (int) (bounds.width - 2*gapX);
				rationalBoundsHeight = bounds.height;
			}
			else {
				rationalBoundsWidth = (int) (bounds.width - 2*gapX - vScrollBarWidth);
				rationalBoundsHeight = bounds.height - hScrollBarHeight;
			}
			numOfLinesPerPage = (int)(rationalBoundsHeight / lineHeight);
			if (numOfLinesPerPage<=0) numOfLinesPerPage = 1;
			this.widthOfCharsPerPage = this.rationalBoundsWidth;
			//numOfLinesInPage = numOfLines - vScrollPos;
			//if (numOfLinesInPage<=0) numOfLinesInPage = 1;
			//numOfLinesInPage = Math.min(numOfLinesPerPage, numOfLinesInPage);
						
		}
	}
	
	enum BoundMode {
		Create,
		ChangeBounds,
		FontSize,
		ScrollMode, 
		SetText
	}
	
	/** EditText생성시, changeBounds(), 폰트 size변경시, 스크롤 Mode변경시 호출되므로 주의해야 한다.
	 * ScrollMode가  Both일 때 폰트크기변경, bounds크기 변경을 하면  setText와  cursor를 초기화할 필요가 없다.
	   ScrollMode가 VScroll일 때  bounds크기 변경을 하면  setText(세로로만 크기가 바뀌므로)와  cursor를 초기화할 필요가 없다.*/
	void bound(BoundMode boundMode, boolean initCursor) {
		if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
    			isTripleBuffering) {
			// 이미지로 그리기
			this.bitmapForRendering = 
					Bitmap.createBitmap(this.bounds.width, this.bounds.height, CommonGUI_SettingsDialog.settings.bufferedImageType);
			mCanvas = new Canvas(this.bitmapForRendering);
		}
		
		
		boundAttributes(boundMode);
		
		if (boundMode==BoundMode.Create || boundMode==BoundMode.FontSize) {
			if (isSingleLine) {
				if (scrollMode==ScrollMode.VScroll) {				
					fontSize = bounds.height * 0.45f;
					setDescentAndLineHeight(fontSize);
				}
				else {
					fontSize = bounds.height * 0.45f;
					setDescentAndLineHeight(fontSize);
				}
			}
			else {
				setDescentAndLineHeight(fontSize);
			}
		}
		
		if (initCursor) {
			vScrollPos = 0;
			widthOfhScrollPos = 0;
			cursorPos.x = 0;
			cursorPos.y = 0;
		}
		if (scrollMode==ScrollMode.VScroll) {
			
			Rectangle boundsOfVScrollBar = new Rectangle(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);
			if (vScrollBar!=null) {
				vScrollBar.changeBounds(boundsOfVScrollBar);
			}
			else {
				vScrollBar = new VScrollBarLogical
							(this, context, boundsOfVScrollBar, 
							numOfLinesPerPage,
							/*numOfLinesInPage,*/
							numOfLines, vScrollPos, 1);			
					vScrollBar.setOnTouchListener(this);
			}
			//setVScrollPos();
			setVScrollBar();
		}		
		else if (scrollMode==ScrollMode.Both) {
			if (this instanceof TextView) {
				int a;
				a=0;
				a++;
			}
			else {
				int a;
				a=0;
				a++;
			}
			
			Rectangle boundsOfVScrollBar = new Rectangle(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);
			if (vScrollBar!=null) {
				vScrollBar.changeBounds(boundsOfVScrollBar);
			}
			else {
				vScrollBar = new VScrollBarLogical
							(this, context, boundsOfVScrollBar, 
							numOfLinesPerPage,
							/*numOfLinesInPage,*/
							numOfLines, vScrollPos, 1);			
					vScrollBar.setOnTouchListener(this);
			}
			//setVScrollPos();
			setVScrollBar();
			
			Rectangle boundsOfHScrollBar = new Rectangle((int)(bounds.x+gapX), 
					bounds.y+bounds.height-hScrollBarHeight,
					this.rationalBoundsWidth, hScrollBarHeight);
			if (hScrollBar!=null) {
				hScrollBar.changeBounds(boundsOfHScrollBar);
			}
			else {
				hScrollBar = new HScrollBar(this, context, boundsOfHScrollBar);
				hScrollBar.setOnTouchListener(this);
			}
			try{
			//setHScrollPos();
			setHScrollBar();
			}catch(Exception e) {
				int a;
				a=0;
				a++;
			}
				
		}
		if (this.isSingleLine) {
			if (vScrollBar!=null) {
				vScrollBar.hides = true;
			}
			if (hScrollBar!=null) {
				hScrollBar.hides = true;
			}
			this.hasToolbarAndMenuFontSize = false;
		}		
		 
		if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
    			isTripleBuffering)  this.drawToImage(mCanvas);
	}
	
	public void changeFontSize(float fontSize/*, boolean openLog*/) {
		this.fontSize = fontSize;
		setDescentAndLineHeight(fontSize);
		//int toolbarButtonIndex = toolbar.findIndex("S");
		if (toolbar!=null) toolbar.buttons[0].setText(""+fontSize);
		paint.setTextSize(fontSize);
		
		CommonGUI.loggingForMessageBox.setText(true, "Loading...", false);
		CommonGUI.loggingForMessageBox.setHides(false);
			
		
		if (scrollMode==ScrollMode.VScroll) {
			boolean usesThread = false;
			if (usesThread) {
				SetTextThread thread = new SetTextThread(true);
				thread.start();
			}
			else {
				boolean initCursor = true;
				if (initCursor) {
					bound(BoundMode.SetText, true);
				}
				else {
					bound(BoundMode.SetText, false);
				}
				text = TextArrayToText(0, 0, 0, 0);			
				setText(0, text);
				CommonGUI.loggingForMessageBox.setHides(true);
				view.postInvalidate();
			}
		}
		else {	// Both모드일 때는 setText()를 호출할 필요가 없으므로 더 빠르다. 
			// 그리고 cursorPos도 바뀌지 않는다.
			bound(BoundMode.FontSize, false);
			
			CommonGUI.loggingForMessageBox.setHides(true);
			view.postInvalidate();
			
			//}
		}
	}
	
	/**paramBounds:툴바를 포함하지 않은 bounds*/
	public void changeBounds(Rectangle paramBounds) {
		this.bounds.copy(paramBounds);
		applySizingBorderOfView(bounds);
		paramBounds.copy(bounds);
				
		if (hasToolbarAndMenuFontSize) {
			
			Rectangle boundsOfToolbar;
			boundsOfToolbar = toolbar.bounds;
			boundsOfToolbar.y = paramBounds.y;
			boundsOfToolbar.height = paramBounds.height;
			toolbar.changeBounds(boundsOfToolbar);
			
			totalBounds.x = toolbar.bounds.x;
			totalBounds.y = toolbar.bounds.y;
			totalBounds.width = toolbar.bounds.width+this.bounds.width;
			totalBounds.height = this.bounds.height;
		}
		else {
			totalBounds.x = bounds.x;
			totalBounds.y = bounds.y;
			totalBounds.width = bounds.width;
			totalBounds.height = bounds.height;
		}
	
		//initCursorAndScrollPos();
		
		
		if (scrollMode==ScrollMode.VScroll) {
			bound(BoundMode.ChangeBounds, false);
			setText(0, this.getText());
		}
		else {
			bound(BoundMode.ChangeBounds, false);
		}
	}
	
	public CodeString getText() {
		int j;
		//String newText = "";
		ArrayListCodeChar newText = new ArrayListCodeChar(this.numOfLines*50);
		newText.setText(new CodeString("", textColor));
		for (j=0; j<numOfLines; j++) {
			//newText.insert(newText.count, textArray[j]);
			newText.concate(textArray[j]);
		}
		CodeString str = new CodeString(newText.getItems(), newText.count); 
		return str;
	}
	
	/** cursorPosY에서 n번째 newLineChar를 만날 때까지의  numOfLines를 리턴한다.*/
	protected int getNumOfLines(int cursorPosY, int numOfNewLineChar) {
		int i, j;
		int countOfNewLineChar = 0;
		if (scrollMode==ScrollMode.Both) {
			return numOfNewLineChar;
		}
		else {
			for (j=cursorPosY; j<numOfLines; j++) {
				if (textArray[j].charAt(textArray[j].length()-1).c=='\n') {
					countOfNewLineChar++;
					if (countOfNewLineChar==numOfNewLineChar) {
						return j;
					}
				}
			}
		}
		return 0;
	}
	
	/** cursorPosY에서 n번째 newLineChar를 만날 때까지의  numOfLinesInText을 리턴한다.*/
	private int getNumOfLinesInText(int cursorPosY, int cursorPosX, int numOfNewLineChar) {
		try {
		int i, j;
		String c;
		numOfLinesInText = 0;
		int countOfNewLineChar = 0;
		
		for (j=cursorPosY; j<numOfLines; j++) {
			numOfLinesInText++;
			for (i=0; i<textArray[j].length(); i++) {
				c = textArray[j].substring(i, i+1).toString();
				if (c.equals(NewLineChar)) {
					countOfNewLineChar++;
					if (countOfNewLineChar==numOfNewLineChar) {
						return numOfLinesInText;
					}
				}
			}
		}
		
		return numOfLinesInText;
		}catch(Exception e) {
			//Log.e("EditText-getNumOfLinesInTextMultiLine", e.toString());
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return -1;
		}
	}
	
		
	/** cursorPosY에서 n번째 newLineChar를 만날 때까지의  스트링을 리턴한다.*/
	private CodeString TextArrayToText(int cursorPosY, int cursorPosX, int numOfNewLineChar) {
		try {
		int i, j;
		//String newText = "";
		ArrayListCodeChar newText = new ArrayListCodeChar(100);
		int count = 0;
		char c;
		CodeChar cc;
		numOfLinesInText = 0;
		indexOfCursorInText = -1;
		int countOfNewLineChar = 0;
		
		for (j=cursorPosY; j<numOfLines; j++) {
			numOfLinesInText++;
			for (i=0; i<textArray[j].length(); i++) {
				if (j==cursorPosY && i==cursorPosX) {
					indexOfCursorInText = count;
				}
				count++;
				cc = textArray[j].charAt(i);
				c = cc.c;
				if (c=='\n') {
					countOfNewLineChar++;
					newText.add( cc );
					if (countOfNewLineChar==numOfNewLineChar)  {
						return new CodeString( newText.getItems(), newText.count );
					}
				}
				else {
					//newText += c;
					newText.add( cc );
				}
				
			}
		}
		// 커서가 텍스트바깥에 위치할 때
		if (indexOfCursorInText==-1) {
			indexOfCursorInText = count;
		}
		
		// newText의 count가 0인 빈스트링일 경우는 null이 리턴이 된다.
		return new CodeString( newText.getItems(), newText.count );
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return null;
		}
	}
	
	
	
	/** startY라인을 시작으로 numOfLines 끝까지의 스트링을 리턴한다.*/
	private CodeString TextArrayToText(int startY, int startX, int cursorPosY, int cursorPosX) {
		try {
		int i, j;
		ArrayListCodeChar newText = new ArrayListCodeChar(100); 
		int count = 0;
		int textLen;
		indexOfCursorInText = -1;
		
				
		for (j=startY; j<numOfLines; j++) {
			textLen = textArray[j].length();
			for (i=0; i<textLen; i++) {				
				newText.add( textArray[j].substring(i, i+1).charAt(0) );
				if (j==cursorPosY && i==cursorPosX) {
					indexOfCursorInText = count;
				}
				count++;
			}
		}
		// 커서가 텍스트바깥에 위치할 때
		if (indexOfCursorInText==-1) {
			indexOfCursorInText = count;
		}
		return new CodeString(newText.getItems(), newText.count);
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return null;
		}
	}
	
	/**"Left", "Right", "Up", "Down", "Home", "End", "PgUp", "PgDn"*/
	public void controlChar(int indexInSpecialKeys, String charA) {
		//if (indexInSpecialKeys < 6) {
		if (0<=indexInSpecialKeys && indexInSpecialKeys<=7)
			this.isSelecting = false;
		
		switch (indexInSpecialKeys) {
			case 0: { // "Left"
				if (cursorPos.x>0) cursorPos.x--;
				else {
					if (cursorPos.x==0 && cursorPos.y>0) {
						cursorPos.y--;
						cursorPos.x = textArray[cursorPos.y].length();
					}
				}
				break;
			}				
			case 1: { //"Right"
				if (cursorPos.x<textArray[cursorPos.y].length()) cursorPos.x++;
				else {
					if (cursorPos.x==textArray[cursorPos.y].length() && cursorPos.y<numOfLines-1) {
						cursorPos.y++;
						cursorPos.x = 0;
					}
				}
				break;
			}
			case 2: { //"Up"
				if (cursorPos.y>0) cursorPos.y--;
				if (cursorPos.x>textArray[cursorPos.y].length()) 
					cursorPos.x = textArray[cursorPos.y].length();
				if (cursorPos.x<0) cursorPos.x = 0; 
				break;
			}
			case 3: { // "Down"
				if (cursorPos.y<numOfLines-1) cursorPos.y++; 
				if (cursorPos.x>textArray[cursorPos.y].length()) 
					cursorPos.x = textArray[cursorPos.y].length();
				if (cursorPos.x<0) cursorPos.x = 0;
				break;
			}
			case 4: cursorPos.x = 0; break; //"Home"
			case 5: { // "End"
				cursorPos.x = textArray[cursorPos.y].count-1;
				if (cursorPos.x<0) cursorPos.x = 0;
				break;
			}
			case 6: { // "PgUp"
						cursorPos.y -= this.numOfLinesPerPage;
						if (cursorPos.y<0) cursorPos.y = 0;
						if (cursorPos.x>textArray[cursorPos.y].length()) 
							cursorPos.x = textArray[cursorPos.y].length();
						if (cursorPos.x<0) cursorPos.x = 0;
						break;
				
					}
			case 7: { // "PgDn"
						cursorPos.y += this.numOfLinesPerPage;
						if (cursorPos.y>=this.numOfLines) cursorPos.y = numOfLines-1;
						if (cursorPos.x>textArray[cursorPos.y].length()) 
							cursorPos.x = textArray[cursorPos.y].length();
						if (cursorPos.x<0) cursorPos.x = 0;
						break;
				
					}
		} // switch
			
			if (scrollMode==ScrollMode.VScroll) {			
				setVScrollPos();
				setVScrollBar();
			}		
			else if (scrollMode==ScrollMode.Both) {
				setVScrollPos();
				setHScrollPos();
				setVScrollBar();	
				setHScrollBar();		
			}
		/*}
		else {
			
			setVScrollBar();
		}*/
	}
	
	/** 키 입력시 호출된다.
	 * redoBuffer를 초기화시켜야 redoBuffer에 남아있는 상태에서 키를 입력하고 나서 
	 * 다시 redo 를 하면 발생하는 오류를 해결할 수 있다.*/
	public void addChar(String charA/*, boolean isNextToCursor*/) {
		//if (isSingleLine==false) {
		isModified = true;
		redoBuffer.reset();
			int i;
			String[] specialKeys = IntegrationKeyboard.SpecialKeys;
			for (i=0; i<specialKeys.length; i++) {
				if (charA.equals(specialKeys[i])) {
					controlChar(i, charA);
					return;
				}
			}
			if (charA.equals(IntegrationKeyboard.Delete)) {	// BackSpace
				charA = DeleteChar;
				
			}	// BackSpace
			else if (charA.equals(IntegrationKeyboard.Enter)) {
				charA = NewLineChar;
			}
			else if (charA.equals(IntegrationKeyboard.BackSpace)) {
				charA = BackspaceChar;
			}
			if (isSingleLine) {
				if (charA.equals(NewLineChar)) return;
			}
			addCharReally(charA/*, isNextToCursor*/);
			
		if (this.scrollMode==ScrollMode.VScroll) {
			setVScrollPos();
			setVScrollBar();
		}
		else {
			setVScrollPos();
			setHScrollPos();
			setVScrollBar();
			setHScrollBar();
		}
	}
		
	
	/** replaceAll에서 replace하는 위치들을 백업하기 위해 호출한다.*/
	void backUpForUndo_replace(String command, boolean isAll, boolean isForward, boolean isScopeAll, boolean isCaseSensitive, 
			boolean isWholeWord, Point curFindPosLocal, String textToFind, String textToReplaceWith,
			ArrayList listOfFindPos, ArrayList listOfReplacePos) {
		String addedInfo;
		if (isScopeAll==false) {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
				isWholeWord + "-" + 
				this.selectP1.x + "-" + this.selectP1.y + "-" + this.selectP2.x + "-" + this.selectP2.y;
		}
		else {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
					isWholeWord;
		}
		CodeString text = new CodeString(textToFind + "-" + textToReplaceWith, textColor);
		if (command.equals("replace")) {			
			undoBuffer.push(new Point(curFindPosLocal.x,curFindPosLocal.y), text, command, addedInfo);
		}
		else if (command.equals("replaceAll")) {
			undoBuffer.push(new Point(curFindPosLocal.x,curFindPosLocal.y), text, command, addedInfo, !isScopeAll, listOfFindPos, listOfReplacePos);
		}
	}
	
	/**replace-find 에서만 호출한다. 즉 isAll 은 false 이다. 
	 * 검색한 위치(findP1,findP2)와 대체한 위치(replacePosP1,replacePosP2) 모두를 백업한다.
	 * String addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
	 * 		isWholeWord;
	 * String text = textToFind + "-" + textToReplaceWith;
	 * cursorPos = curFindPosLocal(검색시작위치)
	 * command = "replace"/"replaceAll" */
	void backUpForUndo_replace(String command, boolean isAll, boolean isForward, boolean isScopeAll, boolean isCaseSensitive, 
			boolean isWholeWord, 
			Point findP1, Point findP2, Point replacePosP1, Point replacePosP2, 
			String textToFind, String textToReplaceWith) {
		String addedInfo;
		if (isScopeAll==false) {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
				isWholeWord + "-" + 
				this.selectP1.x + "-" + this.selectP1.y + "-" + this.selectP2.x + "-" + this.selectP2.y + "-" +
				findP1.x + "-" + findP1.y + "-" + findP2.x + "-" + findP2.y + "-" +
				replacePosP1.x + "-" + replacePosP1.y + "-" + replacePosP2.x + "-" + replacePosP2.y;
		}
		else {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
				isWholeWord + "-" +
				findP1.x + "-" + findP1.y + "-" + findP2.x + "-" + findP2.y + "-" +
				replacePosP1.x + "-" + replacePosP1.y + "-" + replacePosP2.x + "-" + replacePosP2.y;
		}
		CodeString text = new CodeString(textToFind + "-" + textToReplaceWith, textColor);
		if (command.equals("replace")) {			
			undoBuffer.push(new Point(replacePosP1.x,replacePosP1.y), text, command, addedInfo);
		}
		else if (command.equals("replaceAll")) {
			undoBuffer.push(new Point(replacePosP1.x,replacePosP1.y), text, command, addedInfo);
		}
	}
	
	/** redo를 undo한다.*/
	void backUpForUndo(String charA, RedoBuffer.Pair pair) {
		if (charA.equals("cut")) {			
			int i;
			int y;
			int startX, endX;
			String copiedText = "";
			/*for (i=0; i<selectIndicesCountForCopy; i+=2) {
				y = selectIndices[i].y;
				startX = selectIndices[i].x;
				endX = selectIndices[i+1].x;
				CodeString lineCopiedText = textArray[y].substring(startX, endX+1);
				copiedText += lineCopiedText;
			}*/
			copiedText = (String) pair.addedInfo;
			
			//int firstLine = selectIndices[0].y;
			int firstLine = pair.cursorPos.y;
			int numOfNewLineChar = getNumOfNewLineChar(copiedText) + 1;
			//int this.getNumOfLinesInText(firstLine, 0, numOfNewLineChar);
			CodeString newText = TextArrayToText(firstLine, 0, numOfNewLineChar);
			Object addedInfo = copiedText;
			undoBuffer.push(new Point(pair.cursorPos.x,firstLine), newText, charA, addedInfo);
		
		}
		else if (charA.equals("paste")) {	// paste를 undo
			CodeString textForBackup = TextArrayToText(pair.cursorPos.y, 0, 1);
			//Object addedInfo = copiedText;
			undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), textForBackup, charA, 
					pair.addedInfo);
		}
		
		else if (charA.equals(BackspaceChar) || charA.equals(NewLineChar) || charA.equals(DeleteChar))
		{
			if (charA.equals(BackspaceChar)) {
				if (pair.isSelecting) {
					
					// 선택된 줄들을 백업한다.
					CodeString textForBackup = (CodeString) pair.addedInfo;
					undoBuffer.push(new Point(pair.cursorPos.x, pair.cursorPos.y), textForBackup, charA, pair.p2, pair.isSelecting);
				}
				else {
					if (pair.cursorPos.x!=0) { // undo에서 일반적인 경우로 취급
						CodeString textForBackup = TextArrayToText(pair.cursorPos.y, 0, 1);
						undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), textForBackup, charA);
					}
					else {
						int prevLine;
						if (pair.cursorPos.y>0) {
							prevLine = pair.cursorPos.y-1;
							if (textArray[prevLine].length()>0 && textArray[prevLine].charAt(textArray[prevLine].length()-1).c=='\n') {
								// 이전라인과 현재라인 모두를 백업한다.(0열에서 '\n'이 지워지는 back키만)
								CodeString newText=null;
								newText = TextArrayToText(prevLine, pair.cursorPos.x, 2);
								undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), newText, charA);
								//setTextMultiLine(cursorPos.y, newText, -1);
							}
							else {	// scrollMode가 VScroll일 때만 
								CodeString textForBackup = TextArrayToText(prevLine, 0, 1);
								undoBuffer.push(new Point(pair.cursorPos.x, prevLine), textForBackup, charA);
							}
						}					
					}
				}
			}
			else if (charA.equals(DeleteChar)) {
				if (pair.isSelecting) {
					/*Point p1 = selectP1, p2 = selectP2;
					if (this.selectP1.y>=this.selectP2.y) {
						p1 = selectP2;
						p2 = selectP1;
					}
					// 선택된 줄들을 백업한다.
					CodeString textForBackup = TextArrayToText(p1.y, 0, p2.y-p1.y+1);
					undoBuffer.push(new Point(p1.x, p1.y), textForBackup, charA, null, isSelecting);*/
					
					// 선택된 줄들을 백업한다.
					CodeString textForBackup = (CodeString) pair.addedInfo;
					undoBuffer.push(new Point(pair.cursorPos.x, pair.cursorPos.y), textForBackup, charA, pair.p2, pair.isSelecting);
					
				}
				else {
					if (pair.cursorPos.x<textArray[pair.cursorPos.y].length()) {
						if (textArray[pair.cursorPos.y].charAt(pair.cursorPos.x).c!='\n') { // undo에서 일반적인 경우로 취급
							//undoBuffer.push(cursorPos, textArray[cursorPos.y]);
							CodeString textForBackup = TextArrayToText(pair.cursorPos.y, 0, 1);
							undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), textForBackup, charA);
						}
						else {	// 마지막열에서 '\n'이 지워지는 delete키만
							// 현재라인과 다음라인 모두를 백업한다.
							CodeString newText=null;
							newText = TextArrayToText(pair.cursorPos.y, pair.cursorPos.x, 2);
							undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), newText, charA);
						}
					}
					else {	// 지우는 문자가 '\n'이 아닌 경우, undo에서 일반적인 경우로 취급
						CodeString textForBackup = TextArrayToText(pair.cursorPos.y, 0, 1);
						undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), textForBackup, charA);
					}
				}
				
			}
			else if (charA.equals(NewLineChar)) {
				// 현재라인과 다음라인 모두를 백업한다.
				CodeString newText=null;
				newText = TextArrayToText(pair.cursorPos.y, pair.cursorPos.x, 1);
				undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), newText, charA);
			}
		}
		
		else  { // 일반적인 경우
			CodeString textForBackup = TextArrayToText(pair.cursorPos.y, 0, 1);
			undoBuffer.push(new Point(cursorPos.x,pair.cursorPos.y), textForBackup, charA);
		}
	}
	
	
	
	void backUpForUndo(String charA, boolean isReplaceChar) {
		if (charA.equals("cut")) {			
			int i;
			int y;
			int startX, endX;
			String copiedText = "";
			for (i=0; i<selectIndicesCountForCopy; i+=2) {
				y = selectIndices[i].y;
				startX = selectIndices[i].x;
				endX = selectIndices[i+1].x;
				CodeString lineCopiedText = textArray[y].substring(startX, endX+1);
				copiedText += lineCopiedText.str;
			}
			
			int firstLine = selectIndices[0].y;
			int numOfNewLineChar = getNumOfNewLineChar(copiedText) + 1;
			//int this.getNumOfLinesInText(firstLine, 0, numOfNewLineChar);
			CodeString newText = TextArrayToText(firstLine, 0, numOfNewLineChar);
			Object addedInfo = copiedText;
			undoBuffer.push(new Point(selectIndices[0].x,firstLine), newText, charA, addedInfo);
		
		}
		else if (charA.equals("paste")) {	// paste를 undo
			CodeString textForBackup = TextArrayToText(cursorPos.y, 0, 1);
			Object addedInfo = copiedText;
			undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA, 
					addedInfo);
		}
		
		else if (charA.equals(DeleteChar) || charA.equals(NewLineChar) ||
				charA.equals(BackspaceChar)) 
		{
			if (charA.equals(BackspaceChar)) {
				if (isSelecting) {
					Point p1 = selectP1, p2 = selectP2;
					if (this.selectP1.y>this.selectP2.y) { // swapping
						p1 = selectP2;
						p2 = selectP1;
					}
					else if (this.selectP1.y==this.selectP2.y) {
						if (this.selectP1.x>this.selectP2.x) {
							// swapping, y가 같을땐 x를 비교해서 작은쪽이 p1이 된다.
							p1 = selectP2;
							p2 = selectP1;
						}
					}
					// 선택된 줄들을 백업한다.
					CodeString textForBackup = TextArrayToText(p1.y, 0, p2.y-p1.y+1);
					//undoBuffer.push(new Point(p2.x, p1.y), textForBackup, charA, null, isSelecting);
					undoBuffer.push(p1, textForBackup, charA, p2, isSelecting);
				}
				else {
					if (cursorPos.x!=0) { // undo에서 일반적인 경우로 취급
						CodeString textForBackup = TextArrayToText(cursorPos.y, 0, 1);
						undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA);
					}
					else {
						int prevLine;
						if (cursorPos.y>0) {
							prevLine = cursorPos.y-1;
							if (textArray[prevLine].length()>0 && textArray[prevLine].charAt(textArray[prevLine].length()-1).c=='\n') {
								// 이전라인과 현재라인 모두를 백업한다.(0열에서 '\n'이 지워지는 back키만)
								CodeString newText=null;
								newText = TextArrayToText(prevLine, cursorPos.x, 2);
								undoBuffer.push(new Point(cursorPos.x,cursorPos.y), newText, charA);
								//setTextMultiLine(cursorPos.y, newText, -1);
							}
							else {	// scrollMode가 VScroll일 때만 
								CodeString textForBackup = TextArrayToText(prevLine, 0, 1);
								undoBuffer.push(new Point(cursorPos.x, prevLine), textForBackup, charA);
							}
						}					
					}
				}
			}
			else if (charA.equals(DeleteChar)) {
				if (isSelecting) {
					Point p1 = selectP1, p2 = selectP2;
					if (this.selectP1.y>this.selectP2.y) { // swapping
						p1 = selectP2;
						p2 = selectP1;
					}
					else if (this.selectP1.y==this.selectP2.y) {
						if (this.selectP1.x>this.selectP2.x) {
							// swapping, y가 같을땐 x를 비교해서 작은쪽이 p1이 된다.
							p1 = selectP2;
							p2 = selectP1;
						}
					}
					// 선택된 줄들을 백업한다.
					CodeString textForBackup = TextArrayToText(p1.y, 0, p2.y-p1.y+1);
					//undoBuffer.push(new Point(p2.x, p1.y), textForBackup, charA, null, isSelecting);
					undoBuffer.push(p1, textForBackup, charA, p2, isSelecting);
				}
				else {
					if (cursorPos.x<textArray[cursorPos.y].length()) {
						if (textArray[cursorPos.y].charAt(cursorPos.x).c!='\n') { // undo에서 일반적인 경우로 취급
							//undoBuffer.push(cursorPos, textArray[cursorPos.y]);
							CodeString textForBackup = TextArrayToText(cursorPos.y, 0, 1);
							undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA);
						}
						else {	// 마지막열에서 '\n'이 지워지는 delete키만
							// 현재라인과 다음라인 모두를 백업한다.
							CodeString newText=null;
							newText = TextArrayToText(cursorPos.y, cursorPos.x, 2);
							undoBuffer.push(new Point(cursorPos.x,cursorPos.y), newText, charA);
						}
					}
					else {	// 지우는 문자가 '\n'이 아닌 경우, undo에서 일반적인 경우로 취급
						CodeString textForBackup = TextArrayToText(cursorPos.y, 0, 1);
						undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA);
					}
				}
				
			}
			else if (charA.equals(NewLineChar)) {
				// 현재라인과 다음라인 모두를 백업한다.
				CodeString newText=null;
				newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);
				undoBuffer.push(new Point(cursorPos.x,cursorPos.y), newText, charA);
			}
		}
		else { // 일반적인 경우
			if (isReplaceChar) {
				CodeString textForBackup = TextArrayToText(cursorPos.y, 0, 1);
				undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA);
			}
			else {
				CodeString textForBackup = TextArrayToText(cursorPos.y, 0, 1);
				undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA);
			}
		}
	}
	
	
	protected Compiler compiler;
	/** 사용자가 마지막으로 클릭한 indexInmBuffer*/
	int indexInmBuffer = -1;
	
	/** 에디터내에서의 cursorPos를 가지고 mBuffer의 인덱스를 찾는다.*/
	int findWord(int cursorPosX, int cursorPosY) {		
		int i=0;
		int j = 0;
		HighArray_CodeString mBuffer = compiler.mBuffer;
		if (mBuffer==null) return -1;
		int lineNumber=0;
		if (lineNumber!=cursorPosY) {
			for (i=0; i<mBuffer.count; i++) {
				CodeString str = mBuffer.getItem(i);
				if (str.equals("\n")) {
					lineNumber++;
					if (lineNumber==cursorPosY) {
						break;
					}
				}
			}
		}
		
		int indexOfStartInmBuffer;
		if (i>=mBuffer.count) {
			return -1;
		}
		if (mBuffer.getItem(i).equals("\n"))
			indexOfStartInmBuffer = i+1;
		else
			indexOfStartInmBuffer = i;
		
		if (indexOfStartInmBuffer>=mBuffer.count) {
			return mBuffer.count-1;
		}
		
		int len=0;
		for (i=indexOfStartInmBuffer; i<mBuffer.count; i++) {
			CodeString str = mBuffer.getItem(i);
			len += str.length()-j;
			// cursorPosX(index)+1 : index를 길이로 바꾼다.
			if (cursorPosX <= len-1) {
				break;
			}
		}
		
		return i;
	}
	
	/** 에디터내에서의 cursorPosY를 가지고 현재 라인의 mBuffer에서의 시작과 끝 인덱스를 찾는다.*/
	Point findStartIndexAndEndIndexOfCurLine(int cursorPosY) {		
		int i=0;
		int j = 0;
		HighArray_CodeString mBuffer = compiler.mBuffer;
		if (mBuffer==null) return null;
		int lineNumber=0;
		if (lineNumber!=cursorPosY) {
			for (i=0; i<mBuffer.count; i++) {
				CodeString str = mBuffer.getItem(i);
				if (str.equals("\n")) {
					lineNumber++;
					if (lineNumber==cursorPosY) {
						break;
					}
				}
			}
		}
		
		if (i>=mBuffer.count) {
			return null;
		}
		int startIndex = i+1;
		int endIndex;
		for (j=startIndex; j<mBuffer.count; j++) {
			CodeString str = mBuffer.getItem(j);
			if (str.equals("\n")) {
				break;
			}
		}
		
		// 파일의 끝일 경우
		if (j>=mBuffer.count) endIndex = mBuffer.count-1;
		else endIndex = j-1; // 현재 라인의 끝 인덱스
				
		return new Point(startIndex, endIndex);
	}
	
	/** 에디터내에서의 cursorPos를 가지고 mBuffer에서의 라인(;)인덱스를 리턴한다.*/
	Point findLineOfCode(int cursorPosX, int cursorPosY) {
		//if (scrollMode==ScrollMode.VScroll) return -1;
		
		int i=0;
		HighArray_CodeString mBuffer = compiler.mBuffer;
		int startIndex=-1, endIndex=-1;
		int lineIndex=-1;
		
		int lineNumber=0;
		if (lineNumber!=cursorPosY) {
			for (i=0; i<mBuffer.count; i++) {
				if (mBuffer.getItem(i).equals("\n")) {
					lineNumber++;
					if (lineNumber==cursorPosY) {
						lineIndex = i;
						//startIndex = i;
						break;	
					}
				}
			}
		}
		
		startIndex = compiler.Skip(mBuffer, true, ";", 0, lineIndex);
		if (startIndex==-1) startIndex=0;
		endIndex = compiler.Skip(mBuffer, false, ";", lineIndex, mBuffer.count-1);
		if (endIndex==-1) endIndex=mBuffer.count-1;
		
				
		return new Point(startIndex, endIndex);
	}
	
	/** 사용자가 어떤 지점에 클릭을 해서 키를 추가할 경우 스트링의 이름만 바꾸는 것인가,
	 * 아니면 소스에 스트링을 추가할 것인가를 알아내서 리턴한다.
	 * @param cursorPosX
	 * @param cursorPosY
	 * @return
	 */
	AddCharReallyMode getAddCharReallyMode(int cursorPosX, int cursorPosY) {
		/*CodeChar[] curLineTextArray = textArray[cursorPosY].listCodeChar;
		char prevChar = 0;
		if (cursorPosX>0) {
			prevChar = curLineTextArray[cursorPosX-1].c;
		}
		char nextChar = 0;
		if (cursorPosX+1<textArray[cursorPosY].count) {
			nextChar = curLineTextArray[cursorPosX+1].c;
		}
		if (prevChar!=0) {
			if ((prevChar==' ' || prevChar=='\t' || prevChar=='\r' || prevChar=='\n')==false) 
				return AddCharReallyMode.General_NoAddDeleteTomBuffer;
		}
		if (nextChar!=0) {
			if ((nextChar==' ' || nextChar=='\t' || nextChar=='\r' || nextChar=='\n')==false) 
				return AddCharReallyMode.General_NoAddDeleteTomBuffer;	
		}		
		return AddCharReallyMode.General_AddTomBuffer;*/
		if (compiler!=null) {
			int indexInmBuffer = this.findWord(cursorPosX, cursorPosY);
			return compiler.getAddCharReallyMode(indexInmBuffer);
		}
		return null;
	}
	
	
		
	/** space, delete, enter키는 Hangul.mode가 None이다.
	// 그리고 isNextToCursor는 true로 설정되어 addCharVScroll로 커서다음위치에 
	// key가 추가되므로, TextArrayToTextOneLine, setTextOneLine에 의해 \n을 만날 때까지의 
	// text만 대상으로 하여 성능을 향상한다.*/	
	void addCharReally(String charA/*, boolean isNextToCursor*/) {
		int cursorPos_backup = cursorPos.x;
		try {
			
		
			backUpForUndo(charA, false);
			// redo 를 무효화한다. redoBuffer를 모두 지워야 한다. 
			// redo 를 무효로 만들지 않으면 undo-redo 시스템의 오류가 발생한다.
			redoBuffer.reset();
		
		if (charA.equals(IntegrationKeyboard.Space)) charA = " ";
		if (textArray[cursorPos.y]==null) {
			textArray[cursorPos.y] = new CodeString("", textColor);
		}
		CodeChar[] curLineTextArray = textArray[cursorPos.y].listCodeChar;
		CodeChar[] newCurLineTextArray = curLineTextArray;
		
		int cursorPosX = -1;
		int len;
		if (charA.equals(DeleteChar) || charA.equals(BackspaceChar)) {
			if (this.isSelecting) {
				Point p1 = this.selectP1;
				Point p2 = this.selectP2;
				
				if (p1.x==-1 && p2.x==-1) return;
			}
			len = 1;
		}
		else len = charA.length();
		
		
		cursorPosX = cursorPos.x + len - 1;
		
		try {
			
			newCurLineTextArray = Array.InsertNoSpaceError(new CodeString(charA, textColor).listCodeChar, 0, 
					newCurLineTextArray, cursorPosX, charA.length());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//Log.e("addCharVScrollReally-Insert", e.toString());
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
		if (this.isSelecting && 
				(charA.equals(DeleteChar) || charA.equals(BackspaceChar))) {
			this.deleteSelectedText();
		}
		else {
			textArray[cursorPos.y] = new CodeString(newCurLineTextArray, newCurLineTextArray.length);
			
			if (!charA.equals(DeleteChar) && !charA.equals(NewLineChar) &&
					!charA.equals(BackspaceChar)) {
				CodeString newText=null;
				newText = TextArrayToText(cursorPos.y, cursorPosX, 1);
				if (compiler!=null) {
					Point startAndEndIndex = this.findStartIndexAndEndIndexOfCurLine(cursorPos.y);
					AddCharReallyMode mode = getAddCharReallyMode(cursorPos.x, cursorPos.y);
					int indexInmBuffer = this.findWord(cursorPos.x, cursorPos.y);
					newText = compiler.update(startAndEndIndex, indexInmBuffer, newText, mode);
				}
				setTextMultiLine(cursorPos.y, newText, -1, 1);
				
				if (keyboardMode==Mode.Hangul && hangulMode!=Hangul.Mode.None) {
					
				}
				else {
					cursorPos.x = cursorPos_backup + 1;
				}
			}
			else {
				if (charA.equals(BackspaceChar)) {
					addBackspaceChar();
				}
				else if (charA.equals(DeleteChar)) {
					addDeleteChar();
				}
				else if (charA.equals(NewLineChar)) {
					addEnterChar(/*isNextToCursor*/);
				}
				else {
					CodeString newText = TextArrayToText(cursorPos.y, 0, cursorPos.y, cursorPosX);		
					setText(cursorPos.y, newText);
				}
			}
		}
				
		}catch(Exception e) {
			//Log.e("EditText-addCharReally", e.toString());
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
	}
	
	void addBackspaceChar() {
		if (this.isSelecting) {
			this.deleteSelectedText();
			return;
		}
		
		if (cursorPos.x!=0) { 
			int cursorPosX_backup = cursorPos.x;
			CodeString newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);		
			setTextMultiLine(cursorPos.y, newText, -1, 1);
			cursorPos.x = cursorPosX_backup-1;			
		}
		else {//if (cursorPos.x==0) { 
			int prevLine = cursorPos.y-1;
			if (prevLine>=0) {
				if (scrollMode==ScrollMode.VScroll) {
					// 지워질 라인수를 먼저 구한다. 
					int numOfLinesToDelete;
					TextArrayToText(cursorPos.y, 0, 1);
					// numOfLinesInText는 현재 라인(마지막 '\n'까지)에서 지워질 라인수이고
					// 플러스 1은 이전 라인에서 지워질 라인을 말한다.
					numOfLinesToDelete = numOfLinesInText + 1;
				
					CodeString newPrevLine;
					if (textArray[prevLine].charAt(textArray[prevLine].length()-1).c=='\n') {
						newPrevLine = deleteNewLineChar(textArray[prevLine]);
					}
					else {
						newPrevLine = textArray[prevLine].substring(0,
								textArray[prevLine].length()-1);
					}
					CodeChar[] charArrayNewPrevLine = newPrevLine.listCodeChar;
					CodeString curLineText = TextArrayToText(cursorPos.y, cursorPos.x, 1);
					// \bk를 제거한다.
					curLineText = curLineText.substring(3, curLineText.length());
					
					CodeChar[] charArrayCurLine = curLineText.listCodeChar;
					int newCursorX = newPrevLine.length();
					try {
						charArrayNewPrevLine = Array.InsertNoSpaceError(charArrayCurLine, 0, 
								charArrayNewPrevLine, newCursorX, 
								charArrayCurLine.length);
					}catch(Exception e) {
						e.printStackTrace();
						CompilerHelper.printStackTrace(textViewLogBird, e);
					}
					
					// newPrevLine은 새로운 text 가 된다.
					newPrevLine = new CodeString(charArrayNewPrevLine, charArrayNewPrevLine.length);
					setTextMultiLine(prevLine, newPrevLine, numOfLinesToDelete, 1);
					
					cursorPos.x = newCursorX;
					cursorPos.y = prevLine;
					//vScrollPos = oldVScrollPos;
					//setVScrollPos();
					//setVScrollBar();
				}//if (scrollMode==ScrollMode.VScroll) {
				else {	// 당연히 이전 줄은 newline으로 끝난다.
					int numOfLinesToDelete = 2;
					CodeString newPrevLine = deleteNewLineChar(textArray[prevLine]);
					CodeChar[] charArrayNewPrevLine = newPrevLine.listCodeChar;
					CodeString curLineText = textArray[cursorPos.y];
					// \bk를 제거한다.
					curLineText = curLineText.substring(3, curLineText.length());
					
					CodeChar[] charArrayCurLine = curLineText.listCodeChar;
					int newCursorX = newPrevLine.length();
					try {
						charArrayNewPrevLine = Array.InsertNoSpaceError(charArrayCurLine, 0, 
								charArrayNewPrevLine, newCursorX, 
								charArrayCurLine.length);
					}catch(Exception e) {
						e.printStackTrace();
						CompilerHelper.printStackTrace(textViewLogBird, e);
					}
					
					
					newPrevLine = new CodeString(charArrayNewPrevLine, charArrayNewPrevLine.length);
					setTextMultiLine(prevLine, newPrevLine, numOfLinesToDelete, 1);
					
					cursorPos.x = newCursorX;
					cursorPos.y = prevLine;
					/*setVScrollPos();
					setHScrollPos();
					setVScrollBar();
					setHScrollBar();*/
				}//ScrollMode.Both
			} // if (prevLine>=0)
			else {//첫번째 줄에서 0칼럼
				int cursorPosX_backup = cursorPos.x;
				CodeString newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);		
				setTextMultiLine(cursorPos.y, newText, -1, 1);
				cursorPos.x = cursorPosX_backup;
			}//if (prevLine<0)
			
		}//if (cursorPos.x==0) { 
	}
	
	void deleteSelectedText() {
		if (this.isSelecting) {
			Point p1 = this.selectP1;
			Point p2 = this.selectP2;
			Point temp;
			
			if (p1.x==-1 && p2.x==-1) return; 
			
			if (p1.y>p2.y) {
				temp = p1;
				p1 = p2;
				p2 = temp;				
			}
			else if (p1.y==p2.y) {
				if (p1.x>p2.x) {
					temp = p1;
					p1 = p2;
					p2 = temp;
				}
			}
			
			// p1.y 가 항상 p2.y 보다 작다.
			// p1.y와 p2.y가 같은 경우에 p1.x 가 항상 p2.x 보다 작다.
			
			int curLine = p1.y;
			int numOfLinesToDelete = this.getNumOfNewLineChar(p1, p2) + 1;
						
			CodeString headText = textArray[curLine];
			headText = headText.substring(0, p1.x);
			
			CodeString tailText = TextArrayToText(p2.y, p2.x, 1);
			if (tailText!=null && tailText.count>0)	// null도 아니고 빈스트링도 아니면		
				tailText = tailText.substring(p2.x+1, tailText.length());
			
			try {
			CodeString newText = headText.concate(tailText);
			if (numOfLinesToDelete==1 && newText.equals("") && p2.y<this.numOfLines-1) {
				newText = new CodeString("\n", textArray[curLine].listCodeChar[0].color);
			}
			else if (p2.x<textArray[p2.y].count && textArray[p2.y].charAt(p2.x).c=='\n') {
				newText = newText.concate(new CodeString("\n", textColor));
			}
			setTextMultiLine(curLine, newText, numOfLinesToDelete, 1);
			}catch(Exception e) {
				int a;
				a=0;
				a++;
				e.printStackTrace();
			}
			
			isSelecting = false;
			
			cursorPos.x = p1.x;
			cursorPos.y = p1.y;
		}
	}
	
	void addDeleteChar() {
		if (this.isSelecting) {
			
			this.deleteSelectedText();
			return;
		}
		
		int cursorPosX_backup = cursorPos.x;
		
		// cursorPos는 %dl을 가리키므로 cursorPos.x는 %를 가리킨다.
		if (cursorPos.x+3<textArray[cursorPos.y].length()) {
			// %dl문자 뒤의 문자
			CodeString curChar = textArray[cursorPos.y].substring(cursorPos.x+3,cursorPos.x+3+1);
			if (!(curChar.toString().equals("\n"))) {
				CodeString newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);		
				setTextMultiLine(cursorPos.y, newText, -1, 1);
			}
			else {	// '\n'문자 지우기
				int nextLine = cursorPos.y + 1;
				int curLine = cursorPos.y;
				if (nextLine<numOfLines) {
					if (scrollMode==ScrollMode.VScroll) {
						int numOfLinesToDelete;
						TextArrayToText(curLine, 0, 2);
						numOfLinesToDelete = numOfLinesInText;
					
						CodeString newCurLine = deleteNewLineChar(textArray[curLine]);
						// %dl를 제거한다.
						newCurLine = newCurLine.substring(0, cursorPos.x);
						CodeChar[] charArrayNewCurLine = newCurLine.listCodeChar;
						int newCursorX = newCurLine.length();
						
						CodeString nextLineText = TextArrayToText(nextLine, 0, 1);
						
						//charArrayNewCurLine = Array.Resize(charArrayNewCurLine, 
						//		charArrayNewCurLine.length/*+nextLineText.length()*/);
						CodeChar[] charArrayNextLine = nextLineText.listCodeChar;

						try {
							charArrayNewCurLine = Array.InsertNoSpaceError(charArrayNextLine, 0, 
									charArrayNewCurLine, newCursorX, 
									charArrayNextLine.length);
						}catch(Exception e) {
							e.printStackTrace();
							CompilerHelper.printStackTrace(textViewLogBird, e);
						}
						
						newCurLine = new CodeString(charArrayNewCurLine, charArrayNewCurLine.length);
						setTextMultiLine(curLine, newCurLine, numOfLinesToDelete, 1);
						
						cursorPos.x = newCursorX;
						cursorPos.y = curLine;
						
					}//if (scrollMode==ScrollMode.VScroll) {
					else {	//ScrollMode.Both
						int numOfLinesToDelete = 2;
						CodeString newCurLine = deleteNewLineChar(textArray[curLine]);
						// %dl를 제거한다.
						newCurLine = newCurLine.substring(0, cursorPos.x);
						CodeChar[] charArrayNewCurLine = newCurLine.listCodeChar;						
						int newCursorX = newCurLine.length();
						
						CodeString nextLineText = textArray[nextLine];
						
						CodeChar[] charArrayNextLine = nextLineText.listCodeChar;
						
						try {
							charArrayNewCurLine = Array.InsertNoSpaceError(charArrayNextLine, 0, 
									charArrayNewCurLine, newCursorX, 
									charArrayNextLine.length);
						}catch(Exception e) {
							e.printStackTrace();
							CompilerHelper.printStackTrace(textViewLogBird, e);
						}
						
						newCurLine = new CodeString(charArrayNewCurLine, charArrayNewCurLine.length);
						setTextMultiLine(curLine, newCurLine, numOfLinesToDelete, 1);
						
						cursorPos.x = newCursorX;
						cursorPos.y = curLine;
					}////ScrollMode.Both
				} // if (nextLine<numOfLines) {
				else {//마지막 라인
					CodeString newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);		
					setTextMultiLine(cursorPos.y, newText, -1, 1);
				}
			} // else {	// '\n'문자 지우기

		}
		else {	// if (cursorPos.x+3<textArray[cursorPos.y].length()) {
			// cursorPos는 %dl을 가리키므로 cursorPos.x는 %를 가리킨다.
			// %dl를 제거한다.
			if (scrollMode==ScrollMode.Both) {
				textArray[cursorPos.y] = textArray[cursorPos.y].substring(0, cursorPos.x);
			}
			else { // 다음 줄의 첫문자를 지운다.
				CodeString newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);		
				setTextMultiLine(cursorPos.y, newText, -1, 1);
			}
		}
		
		cursorPos.x = cursorPosX_backup;
	}
	
	
	/**엔터키를 입력하였으므로 마지막 줄수를 100줄 더 늘린다.
	NullPointerException 이나 getCursorPos()에서 에러를 발생시킬수 있으므로.*/
	void addEnterChar(/*boolean isNextToCursor*/) {
		int cursorPosX;
		/*if (isNextToCursor) cursorPosX = cursorPos.x + 1;
		else cursorPosX = cursorPos.x;*/
		cursorPosX = cursorPos.x;
		
		// 추가된 \n 을 포함하는 라인(추가된 \n 까지의 라인)과 그 다음 라인
		CodeString strCurLine = TextArrayToText(cursorPos.y, cursorPosX, 2);
		// 추가된 \n 다음의 라인
		//CodeString strRemainder = strCurLine.substring(cursorPosX+1, strCurLine.length());
		
		int curLine = cursorPos.y;
		
		// 추가된 \n 을 포함하는 라인(추가된 \n 까지의 라인)
		//CodeString newText1 = TextArrayToText(cursorPos.y, cursorPosX, 1);
		//newText1 = newText1.concate(new CodeString("\n", textColor));
		CodeString newText1 = strCurLine;
		int numOfLinesToDelete = this.getNumOfLinesInText(cursorPos.y, 0, 2);
		setTextMultiLine(cursorPos.y, newText1, numOfLinesToDelete, 1);
		
		
		
		// 엔터키를 입력하였으므로 마지막 줄수를 100줄 더 늘린다.
		// NullPointerException 이나 getCursorPos()에서 에러를 발생시킬수 있으므로.
		textArray = Array.Resize(textArray, numOfLines+100);
				
		//textArray[curLine+1] = strRemainder;
		

		
		cursorPos.y = curLine + 1;
		cursorPos.x = 0;
		
	}

	/**한글완성중일 때 이 메서드를 사용한다. 
	 * redoBuffer를 초기화시켜야 redoBuffer에 남아있는 상태에서 키를 입력하고 나서 
	 * 다시 redo 를 하면 발생하는 오류를 해결할 수 있다.*/
	public void replaceChar(String charA) {
		isModified = true;
		redoBuffer.reset();

			if (charA.equals(IntegrationKeyboard.Delete)) {	// BackSpace
				charA = DeleteChar;				
			}	// BackSpace
			else if (charA.equals(IntegrationKeyboard.Enter)) {
				charA = NewLineChar;
			}
			replaceCharReally(charA);
			
	}
	
	/** 한글이 완성중일때만 동작한다. */
	void replaceCharReally(String charA) {
		backUpForUndo(charA, true);
		// redo 를 무효화한다. redoBuffer를 모두 지워야 한다. 
		// redo 를 무효로 만들지 않으면 undo-redo 시스템의 오류가 발생한다.
		redoBuffer.reset();
		
		if (charA.equals(IntegrationKeyboard.Space)) charA = " ";
		
		CodeChar[] curLineTextArray = textArray[cursorPos.y].listCodeChar;
		try {
		curLineTextArray = Array.Delete(curLineTextArray, cursorPos.x, 1);
		}catch(Exception e) {
			int a;
			a=0;
			a++;
		}
		CodeChar[] newCurLineTextArray = curLineTextArray;
		int charALen = charA.length();
		
		if (charALen!=0) {		// charA==0이면 insert가 필요없다.	 
			try{
			newCurLineTextArray = Array.Resize(curLineTextArray, 
					curLineTextArray.length/*+charALen*/);
			newCurLineTextArray = Array.InsertNoSpaceError(new CodeString(charA, textColor).listCodeChar, 0, 
					newCurLineTextArray, cursorPos.x, charALen);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//Log.e("replaceCharVScrollReally-Insert", e.toString());
				e.printStackTrace();
				CompilerHelper.printStackTrace(textViewLogBird, e);
			}
		}
		textArray[cursorPos.y] = new CodeString(newCurLineTextArray, newCurLineTextArray.length);
		
		Point oldCursorPos=null;
		if (charALen==0) { //  도ㄹ에서 ㄹ을 ""으로 만드는 backspace키가 눌릴 때 현재 커서위치를 저장한다.
			isBkSpThatMakeNullStr = true;
			charALen = 1;	// 현재 커서를 가리키도록 한다.
			oldCursorPos = new Point(cursorPos.x, cursorPos.y);
		}
		CodeString newText=null;
		newText = TextArrayToText(cursorPos.y, cursorPos.x+charALen-1, 1);
		setTextMultiLine(cursorPos.y, newText, -1, 1);
		
		if (isBkSpThatMakeNullStr) {
			cursorPos.x = oldCursorPos.x;
			cursorPos.y = oldCursorPos.y;
			isBkSpThatMakeNullStr = false;
		}
	}
	
	
	public void initialize() {
		this.disposeTextArray();
		this.isSelecting = false;
		initCursorAndScrollPos();
		
		this.fontSize = view.getHeight() * 0.03f;
		setDescentAndLineHeight(fontSize);
		//int toolbarButtonIndex = toolbar.findIndex("S");
		if (toolbar!=null) toolbar.buttons[0].setText(""+fontSize);
		paint.setTextSize(fontSize);
		
		this.keyboardMode = IntegrationKeyboard.Mode.Math;
		
		setText(0,new CodeString("", textColor));
		setBackColor(backColor);
		undoBuffer.reset();
		redoBuffer.reset();
		//preProcessor = null;
		isModified = true;
		lang = null;
		
	}
	
	public void initCursorAndScrollPos() {
		cursorPos.x = 0;
		cursorPos.y = 0;
		
		this.boundAttributes(BoundMode.ChangeBounds);
		
		if (scrollMode==ScrollMode.VScroll) {
			vScrollPos = 0;
			vScrollBar.setVScrollBar(numOfLinesPerPage, /*numOfLinesInPage,*/ 
					numOfLines, vScrollPos, 1);
		}
		else {
			vScrollPos = 0;
			//hScrollPos = 0;
			widthOfhScrollPos = 0;
			vScrollBar.setVScrollBar(numOfLinesPerPage, /*numOfLinesInPage,*/ 
					numOfLines, vScrollPos, 1);
			hScrollBar.setHScrollBar(widthOfCharsPerPage, /*widthOfCharsInPage,*/ 
					widthOfTotalChars, widthOfhScrollPos, widthOfhScrollInc);
		}
	}
	
		
	/** startLine에서 시작하여 numOfLinesToDelete 개수 만큼 textArray에서 지우고
	 * 그 지워진 부분에 새로운 text 를 넣는다. 
	 * numOfLinesToDelete가 -1 이면 numOfLinesInText를 가지고 지워질 라인수를 산정한다.*/
	public void setTextMultiLine(int startLine, CodeString text, int numOfLinesToDelete, int numOfNewLineChar) {
		if (text==null) return;
		//isModified = true;
		if (scrollMode==ScrollMode.VScroll) {
			setTextMultiLineVScroll(startLine, text, numOfLinesToDelete);			
			setVScrollPos();
			setVScrollBar();
		}		
		else if (scrollMode==ScrollMode.Both) {
			setTextMultiLineBoth(startLine, text, numOfLinesToDelete);
			setVScrollPos();
			setHScrollPos();
			setVScrollBar();			
			setHScrollBar();
		}	
	}
	
	void disposeTextArray() {
		int i;
		for (i=0; i<textArray.length; i++) {
			if (textArray[i]!=null) {
				textArray[i].destroy();
				textArray[i] = null;
			}
		}
		//textArray = null;
	}
	
	/**startLine 이후 라인부터 text 를 추가한다.*/
	public synchronized void setText(int startLine, CodeString text) {
		try {
			if (startLine==0) {
				//this.initialize();
				this.initCursorAndScrollPos();
				this.disposeTextArray();
				this.isSelecting = false;
			}
		//synchronized (this.textArray) {
		if (text==null) text = new CodeString("", textColor);	
		if (startLine!=0) isModified = true;
		if (scrollMode==ScrollMode.VScroll) {
			setTextVScroll(startLine, text);			
			setVScrollPos();
			setVScrollBar();
		}		
		else if (scrollMode==ScrollMode.Both) {
			setTextScrollBoth(startLine, text);
			setVScrollPos();
			setHScrollPos();
			setVScrollBar();			
			setHScrollBar();
		}
		//}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
		finally {
			if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering)  this.drawToImage(mCanvas);
		}
	}
	
	
	/** 현재 입력한 문자 다음에 커서가 위치하도록 한다.
	 한글은 문자를 완성중일 때는 전진시키지 않는다. 즉 현재 완성중인 문자에 커서가 위치한다.*/
	public void setCursorPos(int lineNumber, int index, boolean isNewLine, 
			boolean isLineWidthNarrow, Point result) {
		
		if (keyboard==null) {
			result.x = 0;
			result.y = 0;
			return;
		}
		// 문자를 완성중이거나 빈스트링을 만드는 BackSpace키일때는 커서를 전진시키지 않는다.
		if ((keyboard.mode==Mode.Hangul && Hangul.mode!=Hangul.Mode.None)) {
			if (isNewLine) {
				if (!isLineWidthNarrow) {	// \n
					if (index<indexOfCursorInText) {
						result.y++;
						result.x = 0;
					}
				}
				else {	// 라인이 텍스트 너비보다 좁을 때
					// index==indexInText면 현재 입력 문자가 칸이 모잘라 아래줄에 쓰여지는 상황 
					if (index<=indexOfCursorInText) {
						result.y++;
						result.x = 0;
					}
				}
			}
			else {
				if (index<indexOfCursorInText) {
					result.x++;
				}
			}
		}
		else {
			if (isNewLine) {
				if (index<=indexOfCursorInText) {
					result.y++;
					result.x = 0;
				}
			}
			else {
				if (index<=indexOfCursorInText) {
					result.x++;
				}
			}			
		}
	}
	
	void forwardCursorX() {
		cursorPos.x++;
	}
	
	public void createMenuFontSize() {
		if (Edit.menuFontSize!=null) return;
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int x, y;
		int width=(int) (viewWidth*0.4f);
		int height=(int) (viewHeight*0.7f);
		x = viewWidth/2-width/2;
		y = viewHeight/2-height/2;
		Rectangle boundsMenuFontSize = new Rectangle(x,y,width,height);  
		Edit.menuFontSize = new MenuWithClosable("MenuFontSize", boundsMenuFontSize, 
				MenuType.Vertical, owner, Edit.Menu_FontSize, new Size(3,3), true, this);		
	}
	
	public void createMenuFunction() {
		if (Edit.menuFunction!=null) return;
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int x, y;
		int width=(int) (viewWidth*0.4f);
		int height=(int) (viewHeight*0.7f);
		x = viewWidth/2-width/2;
		y = viewHeight/2-height/2;
		Rectangle boundsMenuFunction = new Rectangle(x,y,width,height);  
		Edit.menuFunction = new MenuWithClosable("menuFunction", boundsMenuFunction, 
				MenuType.Vertical, owner, Edit.Menu_Function, new Size(3,3), true, this);		
	}
	
	
	
	void createFontSizeDialog() {
		if (Edit.fontSizeDialog!=null) return;
		int w = (int) (bounds.width*0.7f);
		int h = (int) (bounds.height*0.4f);
		int x = bounds.x + bounds.width/2 - w/2;
		int y = (int) (view.getHeight()*0.15f);
		Rectangle bounds = new Rectangle(x,y,w,h);
		Edit.fontSizeDialog = new FontSizeDialog(view, bounds);		
	}
	
	void createToolbar(Rectangle bounds) {
		toolbar = new MenuWithAlwaysOpen("menu", bounds, MenuType.Vertical, owner, 
				namesOfButtonsOfToolbar, new Size(2,2), false, this, isDockingOfToolbarFlexiable);
		toolbar.buttons[1].selectable = true;	// Mode키는 토글로 동작한다.
		toolbar.buttons[1].toggleable = true;
		toolbar.buttons[1].ColorSelected = Color.YELLOW;
		
		toolbar.buttons[4].selectable = true;	// R/W는 토글로 동작한다.
		toolbar.buttons[4].toggleable = true;
		toolbar.buttons[4].ColorSelected = Color.YELLOW;
		toolbar.open(true, false);
	}
	
	String[] setTextArrayNoSpaceError(String[] textArray, int lineNumber, String text) {
		if (lineNumber<textArray.length) {
			textArray[lineNumber] = text;
		}
		else {
			textArray = Array.Resize(textArray, lineNumber+20);
			
			textArray[lineNumber] = text;
		}
		return textArray;
	}
	
	ArrayListCodeChar[] setTextArrayNoSpaceError(ArrayListCodeChar[] textArray, int lineNumber, ArrayListCodeChar text) {
		try{
		if (lineNumber<textArray.length-1) {
			textArray[lineNumber] = text;
		}
		else {
			textArray = Array.Resize(textArray, lineNumber+20);
			
			textArray[lineNumber] = text;
		}
		return textArray;
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
		return null;
	}
	
	
	
	boolean isNextCharBkSp(CodeString text, int i) {
		int textLen = text.length();
		CodeString nextCharA;
		nextCharA = text.substring(i+1, i+2);
		if (nextCharA.toString().equals("%")) {
			if (i+3<textLen) {
				CodeString str = text.substring(i+2, i+4);
				if (str.toString().equals("bk")) {
					return true;
				}						
			}
		}
		return false;
	}
	
	boolean isCurCharBkSp(CodeString text, int i) {
		CodeString charA = text.substring(i, i+1);
		if (charA.toString().equals("%")) {
			if (i+2<text.length()) {
				CodeString str = text.substring(i+1, i+3);
				if (str.toString().equals("bk")) {
					return true;
				}
			}
		}
		return false;
	}
	
	boolean isCurCharDelete(CodeString text, int i) {
		CodeString charA = text.substring(i, i+1);
		if (charA.toString().equals("%")) {
			if (i+2<text.length()) {
				CodeString str = text.substring(i+1, i+3);
				if (str.toString().equals("dl")) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	
	
	/** 수직스크롤바만 있는 경우, 즉 수평스크롤바가 없다.
		// cursorPos.y 는 startLine과 같다.*/
		public void setTextVScroll(int startLine, CodeString text) {
			numOfLines = startLine + 1;
			
			//if (isSingleLine==false) {
				Point cursorPosLocal = new Point(0,startLine);
				
				ArrayListCodeChar textTemp1 = new ArrayListCodeChar(30);
				ArrayListCodeChar textTemp2 = new ArrayListCodeChar(30);
				ArrayListCodeChar[] textListArray = new ArrayListCodeChar[textArray.length];
				
				int i;
				float lineWidth = 0;
				
				CodeString charA;
				boolean isDeleteChar = false;
				boolean isNextCharBkSp = false;
				boolean isCurCharBkSp = false;
				int textLen = text.length();
				
				paint.setTextSize(fontSize);
				
				for (i=0; i<textLen; i++) {
					
					charA = text.substring(i, i+1);
					if (charA.toString().equals("\r")) continue;
					/*if (charA.equals("\t")) {
						String newText;
						newText = text.substring(0, i) + "    ";
						if (i+1<textLen)
							newText += text.substring(i+1, textLen);
						text = new CodeString(newText);
						textLen = text.length();
						charA = " ";
					}*/
					if (i+1<textLen) {
						isNextCharBkSp = isNextCharBkSp(text, i);
					}
					else {
						isNextCharBkSp = false;
					}
					isDeleteChar = isCurCharDelete(text, i);
					isCurCharBkSp = isCurCharBkSp(text, i);
					
					if (isDeleteChar) {
						isDeleteChar = false;
						i+=3; // d 생략
					}
					else {
						if (isNextCharBkSp) {	// 다음 문자가 bksp이면 bksp다음으로 처리를 옮긴다.
							isNextCharBkSp = false;
							i += 3;
							continue;
						}
						if (isCurCharBkSp) {	// 다음 문자가 bksp이면 bksp다음으로 처리를 옮긴다.
							isCurCharBkSp = false;
							i += 2;
							continue;
						}
						if (charA.toString().equals(NewLineChar)) {
							textTemp1.add(charA.charAt(0));
							textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, textTemp1);
							numOfLines++;
							textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, new ArrayListCodeChar(0));
							setCursorPos(startLine, i, true, false, cursorPosLocal);
							// textTemp1.reset()을 사용하지 않고 다음과 같이 한다. 줄마다 동일한 메모리 참조
							textTemp1 = new ArrayListCodeChar(30);
							textTemp2 = new ArrayListCodeChar(30);
							lineWidth = 0;
						}				
						else  {
							textTemp1.add(charA.charAt(0));
							//lineWidth = paint.measureText(new CodeString(textTemp1.getItems(), textTemp1.count).toString());
							lineWidth += paint.measureText(charA.toString());
							if (lineWidth > rationalBoundsWidth) {
								textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, textTemp2);
								numOfLines++;
								textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, new ArrayListCodeChar(0));
								setCursorPos(startLine, i, true, true, cursorPosLocal);
								// textTemp1.reset()을 사용하지 않고 다음과 같이 한다. 줄마다 동일한 메모리 참조
								textTemp1 = new ArrayListCodeChar(30);
								textTemp2 = new ArrayListCodeChar(30);
								lineWidth = 0;
								i--;	// 초과된 문자(다음줄의 첫문자)를 다시 처리
							}
							else {
								textTemp2.add(charA.charAt(0));
								textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, textTemp2);
								setCursorPos(startLine, i, false, false, cursorPosLocal);
							}
						}
					}	// isDeleteChar == false;
					
				}		// for			
				
				this.cursorPos = cursorPosLocal;
				
				textArray = Array.Resize(textArray, textListArray.length);
				for (i=startLine; i<numOfLines; i++) {
					if (textListArray[i]!=null && textListArray[i].count>0) {
						textArray[i] = new CodeString(textListArray[i].getItems(), textListArray[i].count);
					}
					else {
						textArray[i] = new CodeString("", textColor);
					}
				}
				
			//} // if (!isSingleLine)
		}
		
		public void setTextScrollBoth(int startLine, CodeString text) {
			try {
			numOfLines = startLine + 1;
			
			//if (isSingleLine==false) {			
				Point cursorPosLocal = new Point(0,startLine);
				//String textTemp1 = "";
				ArrayListCodeChar textTemp1 = new ArrayListCodeChar(30);
				ArrayListCodeChar[] textListArray = new ArrayListCodeChar[textArray.length];
				
				int i;
				
				CodeString charA;
				boolean isDeleteChar = false;
				boolean isNextCharBkSp = false;
				boolean isCurCharBkSp = false;
				int textLen = text.length();
				
				
				for (i=0; i<textLen; i++) {
					charA = text.substring(i, i+1);
					if (charA.toString().equals("\r")) continue;
					/*if (charA.equals("\t")) {
						String newText;
						newText = text.substring(0, i) + "    ";
						if (i+1<textLen)
							newText += text.substring(i+1, textLen);
						text = new CodeString(newText);
						textLen = text.length();
						charA = " ";
					}*/
					if (i+1<textLen) {
						isNextCharBkSp = isNextCharBkSp(text, i);
					}
					else {
						isNextCharBkSp = false;
					}
					isDeleteChar = isCurCharDelete(text, i);
					isCurCharBkSp = isCurCharBkSp(text, i);
					
					if (isDeleteChar) {
						isDeleteChar = false;
						i+=3; // d 생략
					}
					else {
						if (isNextCharBkSp) {	// 다음 문자가 bksp이면 bksp다음으로 처리를 옮긴다.
							isNextCharBkSp = false;
							i += 3;
							continue;
						}
						if (isCurCharBkSp) {	// 다음 문자가 bksp이면 bksp다음으로 처리를 옮긴다.
							isCurCharBkSp = false;
							i += 2;
							continue;
						}
						if (charA.toString().equals(NewLineChar)) {
							textTemp1.add(charA.charAt(0));
							textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, textTemp1);
							numOfLines++;
							textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, new ArrayListCodeChar(0));
							setCursorPos(startLine, i, true, false, cursorPosLocal);
							// textTemp1.reset()을 사용하지 않고 다음과 같이 한다. 줄마다 동일한 메모리 참조
							textTemp1 = new ArrayListCodeChar(30);
						}				
						else  {
							textTemp1.add(charA.charAt(0));
							textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, textTemp1);
							setCursorPos(startLine, i, false, false, cursorPosLocal);
							
						}
					}	// isDeleteChar == false;
					
				}		// for
				
				this.cursorPos = cursorPosLocal;
				
				textArray = Array.Resize(textArray, textListArray.length);
				for (i=startLine; i<numOfLines; i++) {
					if (textListArray[i]!=null && textListArray[i].count>0) {
						textArray[i] = new CodeString(textListArray[i].getItems(), textListArray[i].count);
					}
					else {
						textArray[i] = new CodeString("", textColor);
					}
				}
				
			//} // if (!isSingleLine)
			}catch(Exception e) {
				//Log.e("EditText-setTextScrollBoth", e.toString());
				e.printStackTrace();
				CompilerHelper.printStackTrace(textViewLogBird, e);
			}
		}
		/** paste에서 사용*/
		public void setTextMultiLineVScroll(int lineNumber, CodeString text, int numOfLinesToDelete/*, int numOfNewLineChar*/) {
			int numOfLinesLocal = 1;
			
			try {
				ArrayListCodeChar textTemp1 = new ArrayListCodeChar(30);
				ArrayListCodeChar textTemp2 = new ArrayListCodeChar(30);
				ArrayListCodeChar[] textListArrayLocal = new ArrayListCodeChar[20];// 20 lines
				
				textListArrayLocal[0] = new ArrayListCodeChar(0); // '\n'을 친 후에 새로 생긴 빈 라인을 위한 것이다.
				
				int i;
				float lineWidth = 0;
				Point cursorPosLocal = new Point(0,lineNumber);
				
				CodeString charA;
				boolean isDeleteChar = false;
				boolean isNextCharBkSp = false;
				boolean isCurCharBkSp = false;
				int textLen = text.length();
				
				int countOfNewLineChar = 0;
				
				paint.setTextSize(fontSize);
				
				for (i=0; i<textLen; i++) {
					charA = text.substring(i, i+1);
					if (charA.toString().equals("\r")) continue;
					
					if (i+1<textLen) {
						isNextCharBkSp = isNextCharBkSp(text, i);
					}
					else {
						isNextCharBkSp = false;
					}
					isDeleteChar = isCurCharDelete(text, i);
					isCurCharBkSp = isCurCharBkSp(text, i);
					
					if (isDeleteChar) {
						isDeleteChar = false;
						i+=3; // d 생략
					}
					else {
						if (isNextCharBkSp) {	// 다음 문자가 bksp이면 bksp다음으로 처리를 옮긴다.
							isNextCharBkSp = false;
							i += 3;
							continue;
						}
						if (isCurCharBkSp) {	// 다음 문자가 bksp이면 bksp다음으로 처리를 옮긴다.
							isCurCharBkSp = false;
							i += 2;
							continue;
						}
						if (charA.toString().equals(NewLineChar)) {
							textTemp1.add(charA.charAt(0));
							textListArrayLocal = setTextArrayNoSpaceError(textListArrayLocal, numOfLinesLocal-1, textTemp1);
							setCursorPos(lineNumber, i, true, false, cursorPosLocal);
							// MultiLine이므로 break를 하지 않는다.
							
							/*countOfNewLineChar++;
							if (countOfNewLineChar==numOfNewLineChar) {	// 마지막 newLineChar는 세지 않는다.
								break;
							}
							else {
								numOfLinesLocal++;
								textTemp1 = new ArrayListCodeChar(30);
								textTemp2 = new ArrayListCodeChar(30);
								lineWidth = 0;
							}*/
							numOfLinesLocal++;
							textTemp1 = new ArrayListCodeChar(30);
							textTemp2 = new ArrayListCodeChar(30);
							lineWidth = 0;
						}				
						else  {
							textTemp1.add(charA.charAt(0));
							//lineWidth = paint.measureText(new CodeString(textTemp1.getItems(), textTemp1.count).toString());
							lineWidth += paint.measureText(charA.toString());
							if (lineWidth > rationalBoundsWidth) {
								textListArrayLocal = setTextArrayNoSpaceError(textListArrayLocal, numOfLinesLocal-1, textTemp2);
								numOfLinesLocal++;
								setCursorPos(lineNumber, i, true, true, cursorPosLocal);
								textTemp1 = new ArrayListCodeChar(30);
								textTemp2 = new ArrayListCodeChar(30);
								lineWidth = 0;
								i--;	// 초과된 문자(다음줄의 첫문자)를 다시 처리
							}
							else {
								//textTemp2 += charA;
								textTemp2.add(charA.charAt(0));
								textListArrayLocal = setTextArrayNoSpaceError(textListArrayLocal, numOfLinesLocal-1, textTemp2);
								setCursorPos(lineNumber, i, false, false, cursorPosLocal);
							}
						}
					}	// isDeleteChar == false;
					
				}		// for
				
				if (numOfLinesToDelete==-1) {
					// 마지막 라인에서 엔터를 누른 경우 numOfLinesLocal는 두 줄이므로
					// '\n'으로 끝나더라도 -1을 하지 않는다.
					if (lineNumber!=numOfLines-1 &&
							text.length()>0 && text.charAt(text.length()-1).c=='\n') {
						numOfLinesLocal--;
					}
					this.numOfLines += numOfLinesLocal - numOfLinesInText;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesInText);
				}
				else {
					// 마지막 라인에서 엔터를 누른 경우 numOfLinesLocal는 두 줄이므로
					// '\n'으로 끝나더라도 -1을 하지 않는다.
					if (lineNumber!=numOfLines-1 &&
							text.length()>0 && text.charAt(text.length()-1).c=='\n') {
						numOfLinesLocal--;
					}
					this.numOfLines += numOfLinesLocal - numOfLinesToDelete;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesToDelete);
				}
				
				CodeString[] textArrayLocal = new CodeString[textListArrayLocal.length];
				for (i=0; i<textArrayLocal.length; i++) {
					if (textListArrayLocal[i]!=null && textListArrayLocal[i].count>0) {
						textArrayLocal[i] = new CodeString(textListArrayLocal[i].getItems(), textListArrayLocal[i].count);
					}
					else {
						textArrayLocal[i] = new CodeString("", textColor);
					}
				}
				textArray = Array.InsertNoSpaceError(textArrayLocal, 0, textArray, lineNumber, 
						numOfLinesLocal);
				this.cursorPos = cursorPosLocal;
				
				//textArray = Array.Resize(textArray, numOfLines+100);
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(textViewLogBird, e);
			}
		}
		/** paste에서 사용*/
		public void setTextMultiLineBoth(int lineNumber, CodeString text, int numOfLinesToDelete/*, int numOfNewLineChar*/) {
			int numOfLinesLocal = 1;
			
			try {
			//if (isSingleLine==false) {		
				ArrayListCodeChar[] textListArrayLocal = new ArrayListCodeChar[20]; // 20 lines
				
				textListArrayLocal[0] = new ArrayListCodeChar(0); // '\n'을 친 후에 새로 생긴 빈 라인을 위한 것이다.
				
				Point cursorPosLocal = new Point(0,lineNumber);
				ArrayListCodeChar textTemp1 = new ArrayListCodeChar(30);
				//ArrayListChar textTemp2 = new ArrayListChar(30);
				int i;
				
				CodeString charA;
				boolean isDeleteChar = false;
				boolean isNextCharBkSp = false;
				boolean isCurCharBkSp = false;
				int textLen = text.length();
				
				int countOfNewLineChar = 0;
				
				for (i=0; i<textLen; i++) {
					charA = text.substring(i, i+1);
					if (charA.toString().equals("\r")) continue;
					
					if (i+1<textLen) {
						isNextCharBkSp = isNextCharBkSp(text, i);
					}
					else {
						isNextCharBkSp = false;
					}
					isDeleteChar = isCurCharDelete(text, i);
					isCurCharBkSp = isCurCharBkSp(text, i);
					
					if (isDeleteChar) {
						isDeleteChar = false;
						i+=3; // d 생략
					}
					else {
						if (isNextCharBkSp) {	// 다음 문자가 bksp이면 bksp다음으로 처리를 옮긴다.
							isNextCharBkSp = false;
							i += 3;
							continue;
						}
						if (isCurCharBkSp) {	// 다음 문자가 bksp이면 bksp다음으로 처리를 옮긴다.
							isCurCharBkSp = false;
							i += 2;
							continue;
						}
						// ScrollMode가 Both이므로 딱 1줄이다.따라서 다음과 같이 한다.
						if (charA.toString().equals(NewLineChar)) {
							textTemp1.add(charA.charAt(0));
							textListArrayLocal = setTextArrayNoSpaceError(textListArrayLocal, numOfLinesLocal-1, textTemp1);
							setCursorPos(lineNumber, i, true, false, cursorPosLocal);						
							
							/*countOfNewLineChar++;
							if (countOfNewLineChar==numOfNewLineChar) {	// 마지막 newLineChar는 세지 않는다.
								break;
							}
							else {
								numOfLinesLocal++;
								textTemp1 = new ArrayListCodeChar(30);					
							}*/
							numOfLinesLocal++;
							textTemp1 = new ArrayListCodeChar(30);
						}
						else  {
							textTemp1.add(charA.charAt(0));
							textListArrayLocal = setTextArrayNoSpaceError(textListArrayLocal, numOfLinesLocal-1, textTemp1);
							setCursorPos(lineNumber, i, false, false, cursorPosLocal);
						}
					}	// isDeleteChar == false;
					
				}		// for
				
				
				if (numOfLinesToDelete==-1) {
					if (lineNumber!=numOfLines-1 &&
							text.length()>0 && text.charAt(text.length()-1).c=='\n') {
						numOfLinesLocal--;
					}
					this.numOfLines += numOfLinesLocal - numOfLinesInText;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesInText);
				}
				else {
					// 마지막 라인에서 엔터를 누른 경우 numOfLinesLocal는 두 줄이므로
					// '\n'으로 끝나더라도 -1을 하지 않는다.
					if (lineNumber!=numOfLines-1 &&
							text.length()>0 && text.charAt(text.length()-1).c=='\n') {
						numOfLinesLocal--;
					}
					this.numOfLines += numOfLinesLocal - numOfLinesToDelete;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesToDelete);
				}
				CodeString[] textArrayLocal = new CodeString[textListArrayLocal.length];
				for (i=0; i<textArrayLocal.length; i++) {
					if (textListArrayLocal[i]!=null && textListArrayLocal[i].count>0) {
						textArrayLocal[i] = new CodeString(textListArrayLocal[i].getItems(), textListArrayLocal[i].count);
					}
					else {
						textArrayLocal[i] = new CodeString("", textColor);
					}
				}
				// 원래 줄이 삭제되고 새로운 줄이 들어온다.
				//textArray = Array.Delete(textArray, lineNumber, numOfLinesInText);
				textArray = Array.InsertNoSpaceError(textArrayLocal, 0, textArray, lineNumber, 
						numOfLinesLocal);
				this.cursorPos = cursorPosLocal;
				
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(textViewLogBird, e);
			}
				
			
		}
	
	
	/** 키보드에 키를 입력할 경우 호출한다.*/
	void setVScrollPos() {
		if (isReadOnly) return;
		if (!(vScrollPos<=cursorPos.y && cursorPos.y<vScrollPos+numOfLinesPerPage)) {
			if (cursorPos.y<vScrollPos) {
				
				vScrollPos = cursorPos.y;
				//vScrollPos--;
				//if (vScrollPos<0) vScrollPos=0;
			}
			else {
				
				//vScrollPos++;
				vScrollPos = cursorPos.y - numOfLinesPerPage + 1;
			}
		}
		
		
		
		
		
	}
	
	void setHScrollPosFromMenuProblemList() {
		//if (isReadOnly) return;
		
		// 이동할 페이지의 widthOfTotalChars와 widthOfCharsPerPage을 알아야 한다.
		this.setHScrollBar();
		
		String str=null;
		paint.setTextSize(fontSize);
		if (keyboard==null) return;
		if (keyboardMode==Mode.Hangul && hangulMode!=Hangul.Mode.None) {
			str = textArray[cursorPos.y].substring(0, cursorPos.x+1).toString();
		}
		else {
			try{
			str = textArray[cursorPos.y].substring(0, cursorPos.x).toString();
			}catch(Exception e) {
				e.printStackTrace();
				int a;
				a=0;
				a++;
			}
		}
		float w = 0;
		try {
		w = paint.measureText(str);
		}catch(Exception e) {
			int a;
			a=0;
			a++;
			e.printStackTrace();
		}
	
		// 현재 페이지의 처음과 마지막을 제외한 일반적인 부분에서 적용된다.
		widthOfhScrollPos = (int) (w-2*fontSize);
		// 현재 페이지의 마지막 너비 부분에서 적용된다.
		if (widthOfhScrollPos > this.widthOfTotalChars-this.widthOfCharsPerPage) {
			widthOfhScrollPos = this.widthOfTotalChars-this.widthOfCharsPerPage;
		}
		// 현재 페이지의 처음 너비 부분에서 적용된다.
		if (widthOfhScrollPos<0) widthOfhScrollPos=0;
		
	}
	
	/** 키보드에 키를 입력할 경우 호출한다.*/
	void setHScrollPos() {
		
		if (isReadOnly) return;
		float endOfWindow = widthOfhScrollPos+widthOfCharsPerPage;
		String str = null;
		try{
		paint.setTextSize(fontSize);
		if (keyboard==null) return;
		if (keyboardMode==Mode.Hangul && hangulMode!=Hangul.Mode.None) {
			str = textArray[cursorPos.y].substring(0, cursorPos.x+1).toString();
		}
		else {
			if (cursorPos.x<=0) str = "";
			else str = textArray[cursorPos.y].substring(0, cursorPos.x).toString();
		}
		}catch(Exception e) {
			int a;
			a=0;
			a++;
		}
		try{
		float w = paint.measureText(str);
		if (!(widthOfhScrollPos<=w && w<endOfWindow)) {
			// Backspace키를 눌러 스크롤 시작범위를 넘어선 경우
			if (w < widthOfhScrollPos) {
				widthOfhScrollPos = (int) (w-widthOfCharsPerPage+fontSize);
				if (widthOfhScrollPos<0) widthOfhScrollPos=0;
			}
			// 문자키를 눌러 스크롤 끝범위를 넘어선 경우
			else if (endOfWindow <= w){
				widthOfhScrollPos = (int) (w-widthOfCharsPerPage+fontSize);
				if (widthOfhScrollPos<0) widthOfhScrollPos=0;
			}
		}
		}catch(Exception e) {
			int a;
			a=0;
			a++;
		}
		
	}
	
	public void vScrollPosToLastPage() {
		vScrollPos = numOfLines-1;
		this.cursorPos.x = 0;
		this.cursorPos.y = numOfLines-1;
		setVScrollPos();
		setVScrollBar();
		if (this.scrollMode==ScrollMode.Both) {
			widthOfhScrollPos = 0;
			setHScrollPos();
			setHScrollBar();
		}
		if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
    			isTripleBuffering)  this.drawToImage(mCanvas);
	}
	
	/** 수직 스크롤부분이 바뀔 경우 호출한다. 예를 들어 vScrollPos, bounds가 바뀔경우*/
	public void setVScrollBar() {
		
		
		numOfLinesPerPage = (int)(rationalBoundsHeight / lineHeight);
		if (numOfLinesPerPage<=0) numOfLinesPerPage = 1;
		
		//if (numOfLines>numOfLinesPerPage) {
			if (vScrollPos+numOfLinesPerPage>=numOfLines)
				vScrollPos = numOfLines - numOfLinesPerPage;
		//}
		if (vScrollPos<0) vScrollPos = 0;
		
		
		
		//isVScrolled = true;
		//vScrollBar.hides = false;
		vScrollBar.setVScrollBar(
				numOfLinesPerPage,
				numOfLines, vScrollPos, 1);	
	
	}
	
	
	/** 보이는부분(page)에서만 maxLineWidth(가장 긴 라인의 너비)를 계산한다.
	 * setHScrollBar를 호출하기 전에 setVScrollBar를 호출하여 vScrollPos와 numOfLinesInPage이
	 * 결정되어 있어야 한다.
	 * 수평 스크롤부분이 바뀔 경우 호출한다. 예를 들어 수직 스크롤시, bounds가 바뀔경우
	 * 주의사항 : 문서의 페이지의 최대 수평량을 10문자 정도 늘린다. 
	 * 왜냐하면 수평스크롤시 끝부분에서 1문자 반정도가 짤리기 때문이다.*/
	public void setHScrollBar() {		
		
		paint.setTextSize(fontSize);
		int i;
		float maxLineWidth=0, lineWidth=0;
		//for (i=0; i<numOfLines; i++) {
		int limit = Math.min(vScrollPos+this.numOfLinesPerPage, this.numOfLines);
		for (i=vScrollPos; i<limit; i++) {
			lineWidth = paint.measureText(textArray[i].toString());
			if (lineWidth>maxLineWidth) {
				lineNumOfMaxWidth = i;
				maxLineWidth = lineWidth;
			}
		}
		// 문서의 페이지의 최대 수평량을 10문자 정도 늘린다. 
		// 왜냐하면 수평스크롤시 끝부분에서 1문자 반정도가 짤리기
		// 때문이다.
		this.maxLineWidth = maxLineWidth/* + 10*fontSize*/;
		
		widthOfTotalChars = (int) this.maxLineWidth;
		widthOfCharsPerPage = (int) rationalBoundsWidth;
				
		// widthOfTotalChars을 계산한 후에 widthOfhScrollPos을 다시 계산한다. 
		// 즉, setHScrollPos 제일 마지막 부분에서 widthOfhScrollPos을 계산하는 것이 아니라 
		// 여기에서 계산한다.
		//widthOfTotalChars는 페이지 내에서의 최대 글자수
		if (widthOfhScrollPos > widthOfTotalChars - widthOfCharsPerPage)
			widthOfhScrollPos = widthOfTotalChars - widthOfCharsPerPage;		
		if (widthOfhScrollPos<0) widthOfhScrollPos = 0;
		
		
		//isHScrolled = true;
		hScrollBar.setHScrollBar(
				widthOfCharsPerPage,
				widthOfTotalChars, widthOfhScrollPos, widthOfhScrollInc);
		//hScrollBar.hides = false;
		
		
		
	}
	
	
	
	void processOnTouch(MotionEvent event, SizeF scaleFactor) {
		if (event.actionCode==MotionEvent.ActionDown) {
			if (scrollMode==ScrollMode.VScroll){
				boolean r = vScrollBar.onTouch(event, scaleFactor);
				if (r) return;
				onTouchEvent(this, event);
			}
			else if (scrollMode==ScrollMode.Both){
				boolean r = vScrollBar.onTouch(event, scaleFactor);
				if (r) return;
				r = hScrollBar.onTouch(event, scaleFactor);
				if (r) {
					return;
				}
				onTouchEvent(this, event);
			}
		}
		else if (event.actionCode==MotionEvent.ActionMove ||
				event.actionCode==MotionEvent.ActionUp) {
			if (scrollMode==ScrollMode.VScroll){
				boolean r = vScrollBar.onTouch(event, scaleFactor);
				if (r) return;
				onTouchEvent(this, event);
			}
			else if (scrollMode==ScrollMode.Both){
				boolean r = vScrollBar.onTouch(event, scaleFactor);
				if (r) return;
				r = hScrollBar.onTouch(event, scaleFactor);
				if (r) {
					return;
				}
				onTouchEvent(this, event);
			}
    	}
	}
	
	
	/** 스크롤바의 ActionCapture처리 : ActionDown(drag시작지점이 editText,scrollBar인가)을 활용하여 editText가
	 * action capture(자기영역을 벗어난 ActionMove를 자신이 가져간다)할지 안할지 결정한다.*/
	public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
		boolean r = false;		
		if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
			if (hides) return false;
			if (hasToolbarAndMenuFontSize) {
				r = toolbar.onTouch(event, scaleFactor);
		    	if (r) return true;
			}
	    	
			if (super.onTouch(event, scaleFactor)==false)     		
				return false;
	    	
			r = vScrollBar.onTouch(event, scaleFactor);
			if (r) {
				return true;
			}
			oldDownPoint.x = event.x;
			oldDownPoint.y = event.y;
			//System.currentTimeMillis();
			
			if (scrollMode==ScrollMode.Both) {
				r = hScrollBar.onTouch(event, scaleFactor);				
				if (r) {
					return true;
				}
			}
			
			
			if (this.iName==CommonGUI.textViewLogBird.iName) {
				int a;
				a=0;
				a++;
			}
			
			isSelected = true;
			
			// 통합키보드의 LangDialog의 editText만 제외하고 
			// isReadOnly(읽기모드)와는 상관없이 모든 EditText에 터치하기만하면
			// 키보드의 리스너가 되도록 한다.
			//if (CommonGUI_SettingsDialog.settings.isKeyboardEnable) {
				if (this.iName!=CommonGUI.keyboard.langDialog.editTextLang.iName) {
					CommonGUI.keyboard.setOnTouchListener(this);
					IntegrationKeyboard.EditTextOfLangDialogIsTouched = false;
				}
				else {
					IntegrationKeyboard.EditTextOfLangDialogIsTouched = true;
				}
			//}
			
			
			
			// onTouchEvent에서 커서위치를 바꾼후(vScrollPos설정) editText의 bounds를 바꾸고 키보드를 보여준다.
			// 다시 말해 순서가 중요하다.
			// 간혹 onTouchEvent에서 log메시지가 열린 후 키보드가 열림으로 인해 
			// log가 키보드에 의해 짤리는 경우가 발생한다.
						
			onTouchEvent(this, event);
			
			if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
				if (!isReadOnly) {
					
					
					if (isSingleLine==false) {
						if (isMaximized()) {
							Rectangle newBoundsOfEditText = new Rectangle(bounds.x, bounds.y, bounds.width, /*prevSize.height*/(int)(view.getHeight()*0.5f));
							changeBounds(newBoundsOfEditText);
							changeBoundsOfKeyboardAndSizingBorder(newBoundsOfEditText);
							isMaximized = false;
						}
						else {
							changeBoundsOfKeyboardAndSizingBorder(bounds);
			    		}
					}
					// isSingleLine이 true일 때도 키보드를 보여준다.
					if (CommonGUI.keyboard.getHides()==true) {
						CommonGUI.keyboard.setHides(false);
					}
				}
			}//if (CommonGUI_SettingsDialog.settings.isKeyboardEnable) {
			
			capturedControl = this;
			
			
			return true;
		}
		else if (event.actionCode==MotionEvent.ActionMove || 
				event.actionCode==MotionEvent.ActionUp) {
			if (event.actionCode==MotionEvent.ActionUp) {
				int a;
				a=0;
				a++;
			}
			// drag시작이 scrollBar이면 scrollBar가, editText라면 editText가 핸들링
			if (capturedControl==this) {
				// 영역내에서 터치를 하여 캡쳐를 하면 CustomView에서 ActionMove를 전달하여 스크롤을 하게 된다.
				// 영역검사를 하지않고 영역을 벗어나더라도 자신이 핸들링한다.
				onTouchEvent(this, event);
				return true;
			}
						
		}
		return false;
    }
	
	public void setScrollMode(ScrollMode mode) {
		scrollMode = mode;
		CommonGUI.loggingForMessageBox.setText(true, "Loading...", false);
		CommonGUI.loggingForMessageBox.setHides(false);
		SetTextThread thread = new SetTextThread(true);
		thread.start();
		
		// 모드를 바꾸면 undo버퍼를 초기화한다.
		undoBuffer.reset();
		return;
	}
	
	void toolbar_Listener(Object sender, String buttonName) {
		if (buttonName.equals("S")) {	// 툴바버튼
			Edit.menuFontSize.open(this, true);
			//menuFontSize.setOnTouchListener(this);
			return;
		}
		else if (buttonName.equals("M")) {
			ScrollMode scrollMode=this.scrollMode; 
			if (scrollMode==ScrollMode.VScroll) {
				scrollMode = ScrollMode.Both;
			}
			else {
				scrollMode = ScrollMode.VScroll;
			}
			this.setScrollMode(scrollMode);
			return;
		}
		else if (buttonName.equals("FN")) {
			Edit.menuFunction.open(this, true);
			//menuFunction.setOnTouchListener(this);
		}
		else if (buttonName.equals("O")) {
			/*if (compiler==null && Control.textViewLogBird!=null) {
				//Control.textViewLogBird.setHides(false);
				Control.textViewLogBird.open(true);
			}
			else if (compiler!=null && compiler.menuClassList!=null) {
				compiler.menuClassList.open(true);
				compiler.menuClassList.setOnTouchListener(this);
				//Compiler.menuProblemList_EditText.setOnTouchListener(this);
				compiler.menuProblemList_EditText.setOnTouchListener(this);
			}
			else if (compiler!=null && compiler.menuClassList==null) {
				Control.textViewLogBird.open(true);
			}*/
		}
		else if (buttonName.equals("U")) {
			/*if (lang!=null) {
				if (isModified) {
					//String input = getText();
					boolean hasLogMessage = false;
					//if (Control.loggingForMessageBox.getHides()) {
						Control.loggingForMessageBox.setText(true, "loading...", false);
						Control.loggingForMessageBox.setHides(false);
						hasLogMessage = true;
					//}
					ThreadSetIsProgramCode thread = new ThreadSetIsProgramCode(hasLogMessage, true);
					thread.start();
				}
				else {
					//if (preProcessor!=null) {
						compiler.menuClassList.open(true);
						compiler.menuClassList.setOnTouchListener(this);
					//}
				}
			}*/
		}
		else if (buttonName.equals("R/W")) {
			if (toolbar.buttons[4].isSelected) {
				this.isReadOnly = true;
			}
			else {
				this.isReadOnly = false;
			}
		}
	}
	
	/** ThreadSetIsJava완료시 호출*/
	/*void ThreadSetIsProgramCode_completed() {
		//if (preProcessor!=null) {		
			compiler.menuClassList.open(true);
			compiler.menuClassList.setOnTouchListener(this);
			//view.postInvalidate();
		//}
	}*/
	
	void copy() {
		if (isSelecting) {
			isCopied = true;
			int i;
			int y;
			int startX, endX;
			copiedText = "";
			for (i=0; i<selectIndicesCountForCopy; i+=2) {
				try {
					if (i==selectIndicesCountForCopy-2) {
						int a;
						a=0;
						a++;
					}
				y = selectIndices[i].y;
				startX = selectIndices[i].x;
				endX = selectIndices[i+1].x;
				CodeString lineCopiedText = textArray[y].substring(startX, endX+1);
				copiedText += lineCopiedText.str;
				}catch(Exception e) {
					int a;
					a=0;
					a++;
					e.printStackTrace();
				}
			}
			
			//if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
				Control.ClipBoardX.setData(copiedText);
			//}
		}
	}
	
	void cut() {
		if (isReadOnly) return;
		
		isModified = true;
		try {
		if (isSelecting) {
			isCopied = true;
			int i;
			int y;
			int startX, endX;
			copiedText = "";
			int[] fullLines = new int[10];
			int fullLinesCount = 0;
			
			backUpForUndo("cut", false);
			// redo 를 무효화한다. redoBuffer를 모두 지워야 한다. 
			// redo 를 무효로 만들지 않으면 undo-redo 시스템의 오류가 발생한다.
			redoBuffer.reset();
			
			for (i=0; i<selectIndicesCountForCopy; i+=2) {
				if (i==selectIndicesCountForCopy-2) {
					int a;
					a=0;
					a++;
				}
				y = selectIndices[i].y;
				startX = selectIndices[i].x;
				endX = selectIndices[i+1].x;
				CodeString lineCopiedText = textArray[y].substring(startX, endX+1);
				copiedText += lineCopiedText.str;
				
				if (textArray[y].length()==lineCopiedText.length()) { 
					// full line이면 줄 자체를 삭제
					if (!(fullLinesCount<=fullLines.length-1)) 
						fullLines = Array.Resize(fullLines, fullLines.length+10);
					fullLines[fullLinesCount++] = y;
				}
				else {
					ArrayListCodeChar list = new ArrayListCodeChar(30);
					list.setText(textArray[y]);
					list.delete(startX, endX-startX+1);
					textArray[y] = new CodeString(list.getItems(), list.count);
				}
			}
			
			// full line이면 줄 자체를 삭제
			if (fullLinesCount>0) {
				ArrayListCodeString listTextArray = new ArrayListCodeString(textArray);
				listTextArray.delete(fullLines[0], fullLinesCount);
				textArray = listTextArray.getItems();
				numOfLines -= fullLinesCount;
			}
			
			
			
			// full line을 제외한 select영역에 포함된 newline문자를 제거한다.
			CodeString newText = TextArrayToText(selectIndices[0].y, cursorPos.x, 1);
			setTextMultiLine(selectIndices[0].y, newText, -1, 1);
			
			
			Point p1 = selectP1, p2 = selectP2;
			if (this.selectP1.y>this.selectP2.y) { // swapping
				p1 = selectP2;
				p2 = selectP1;
			}
			else if (this.selectP1.y==this.selectP2.y) {
				if (this.selectP1.x>this.selectP2.x) {
					// swapping, y가 같을땐 x를 비교해서 작은쪽이 p1이 된다.
					p1 = selectP2;
					p2 = selectP1;
				}
			}
			
			Control.ClipBoardX.setData(copiedText);
			
			cursorPos.x = p1.x;
			cursorPos.y = p1.y;
			
			isSelecting = false;
		}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
	}
	
	void paste() {
		if (isReadOnly) return;
		
		//if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
			String dataClipboard = Control.ClipBoardX.getData();
			if (dataClipboard!=null) {
				isCopied = true;
				copiedText = dataClipboard;
			}
		//}
		
		if (!isCopied) return;
		
		isModified = true;
		
		backUpForUndo("paste", false);
		// redo 를 무효화한다. redoBuffer를 모두 지워야 한다. 
		// redo 를 무효로 만들지 않으면 undo-redo 시스템의 오류가 발생한다.
		redoBuffer.reset();
		
		
		
		CodeString left = textArray[cursorPos.y].substring(0, cursorPos.x);
		CodeString right = textArray[cursorPos.y].substring(cursorPos.x, 
				textArray[cursorPos.y].length());
		CodeString newCurLineText = new CodeString(left.str + copiedText + right.str, textColor);
		
		Point oldCursorPos = new Point();
		oldCursorPos.x = cursorPos.x;
		oldCursorPos.y = cursorPos.y;
		
		indexOfCursorInText = copiedText.length();
		//int numOfNewLineChar = getNumOfNewLineChar(newCurLineText);
		int numOfNewLineChar = getNumOfNewLineChar(newCurLineText) + 1;
		if (newCurLineText.charAt(newCurLineText.length()-1).c=='\n') {
			numOfNewLineChar--;
		}
		setTextMultiLine(cursorPos.y, newCurLineText, 1, numOfNewLineChar);
		
		Point relativeCursorPos = this.getRelativeCursorPos(oldCursorPos, copiedText);
		
		cursorPos.x = relativeCursorPos.x;
		cursorPos.y = relativeCursorPos.y;
		
		view.invalidate();
			
		
	}
	
	/** 선택줄일때만 undo()에서 호출한다.<br>
	 * undo에서 undo를 하기 전의 상태(redo실행결과)를 backup한다. 후에 redo에서 복원한다.*/
	void backUpForRedo(String charA, CodeString textOfBackupForUndo, Point undoPos, Object addedInfo, boolean isSelecting, Point p2) {
		if (isSelecting) {
			if (charA.equals(BackspaceChar)) {	// 0열에서 '\n'이 지워지는 back키만
				CodeString newText=null;
				newText = TextArrayToText(undoPos.y, undoPos.x, 1);
				redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA, addedInfo, true, p2);
			}
			else if (charA.equals(DeleteChar)) { // 마지막열에서 '\n'이 지워지는 delete키만
				CodeString newText=null;
				newText = TextArrayToText(undoPos.y, undoPos.x, 1);
				redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA, addedInfo, true, p2);
			}
			else if (charA.equals(NewLineChar)) {
				CodeString newText=null;
				newText = TextArrayToText(undoPos.y, undoPos.x, 1);
				backUpForRedo(charA, newText, undoPos, addedInfo);
			}
			else { // 선택을 하고 문자키를 눌렀을때
				backUpForRedo(charA, textOfBackupForUndo, undoPos, addedInfo);
			}
		}
		else {
			backUpForRedo(charA, textOfBackupForUndo, undoPos, addedInfo);
		}
	}
	
	/** replace, replaceAll에서 호출한다.
	 * undo에서 undo를 하기 전의 상태(redo실행결과)를 backup한다. 후에 redo에서 복원한다.*/
	void backUpForRedo(String charA, CodeString textOfBackupForUndo, Point undoPos, boolean isSelecting, Object addedInfo, 
			ArrayList listFindPos, ArrayList listReplacePos) {
		redoBuffer.push(undoPos, textOfBackupForUndo, charA, addedInfo, isSelecting, null, listFindPos, listReplacePos);
	}
	
	/** undo에서 undo를 하기 전의 상태(redo실행결과)를 backup한다. 후에 redo에서 복원한다.*/
	void backUpForRedo(String charA, CodeString textOfBackupForUndo, Point undoPos, Object addedInfo) {
		if (charA==null) {
			return;
		}
		if (charA.equals("cut")) {
			CodeString newText=null;
			newText = TextArrayToText(undoPos.y, undoPos.x, 1);
			int numOfNewLineChar = getNumOfNewLineChar((String)addedInfo) + 1;
			//redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA, new Integer(numOfNewLineChar));
			redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA, addedInfo);
			
		}
		else if (charA.equals("paste")) {
			String strAddedInfo = (String)addedInfo;
			// numOfNewLineChar = 선택텍스트의 newLineChar개수 + 원래의 1줄
			int numOfNewLineChar = getNumOfNewLineChar(strAddedInfo) + 1;
			CodeString newText=null;
			newText = TextArrayToText(undoPos.y, undoPos.x, numOfNewLineChar);
			redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA, addedInfo);
			
		}
		else if (charA.equals("replace") || charA.equals("replaceAll")) {
			redoBuffer.push(new Point(undoPos.x,undoPos.y), textOfBackupForUndo, charA, addedInfo);			
		}		
		else if (charA.equals(DeleteChar) || charA.equals(NewLineChar) ||
				charA.equals(BackspaceChar))
		{
			if (charA.equals(BackspaceChar)) {	// 0열에서 '\n'이 지워지는 back키만
				if (undoPos.x==0) {
					CodeString newText=null;
					if (undoPos.y==0) {
						newText = TextArrayToText(undoPos.y, undoPos.x, 1);
						redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA);
					}
					else {
						newText = TextArrayToText(undoPos.y-1, undoPos.x, 1);
						redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA);
					}
				}
				else { // 일반적인 경우
					CodeString newText=null;
					newText = TextArrayToText(undoPos.y, undoPos.x, 1);
					redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA);				
				}
			}
			else if (charA.equals(DeleteChar)) { // 마지막열에서 '\n'이 지워지는 delete키만
				CodeString newText=null;
				newText = TextArrayToText(undoPos.y, undoPos.x, 1);
				redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA);
			}
			else if (charA.equals(NewLineChar)) {
				CodeString newText=null;
				newText = TextArrayToText(undoPos.y, undoPos.x, 2);
				redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA);
			}
		}//else if (charA.equals(DeleteChar) || charA.equals(NewLineChar) ||
		//charA.equals(BackspaceChar))
		else  {	// 일반적인 경우
			CodeString newText=null;
			newText = TextArrayToText(undoPos.y, undoPos.x, 1);
			redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA);
		}
	}
	
	
	int getNumOfNewLineChar(Point p0, Point p1) {
		if (this.scrollMode==ScrollMode.Both) {
			return p1.y - p0.y;
		}
		else {
			return -1;
		}
		
	}
	
	/** text내에서 '\n'의 개수를 리턴한다.*/
	int getNumOfNewLineChar(CodeString text) {
		int i;
		int r = 0;
		for (i=0; i<text.length(); i++) {
			if (text.charAt(i).c=='\n') {
				r++;
			}
		}
		return r;
	}
	
	/** text내에서 '\n'의 개수를 리턴한다.*/
	int getNumOfNewLineChar(String text) {
		int i;
		int r = 0;
		for (i=0; i<text.length(); i++) {
			if (text.charAt(i)=='\n') {
				r++;
			}
		}
		return r;
	}
	
	/** initCursorPos에서 text를 더했을때 커서위치를 구한다.*/
	Point getRelativeCursorPos(Point initCursorPos, String text) {
		int i;
		Point r = new Point(initCursorPos.x,initCursorPos.y);
		for (i=0; i<text.length(); i++) {
			if (text.charAt(i)=='\n') {
				r.y++;
				r.x = 0;
			}
			else if (text.charAt(i)=='\r') {
			}
			else {
				r.x++;
			}
		}
		return r;
	}
	
	/** String addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
	 *		isWholeWord;
	 *	String text = textToFind + "-" + textToReplaceWith;
	 *	cursorPos = curFindPosLocal(검색시작위치)
	 *	command = "replace"/"replaceAll" */
	void undo_replace(UndoBuffer.Pair pair) {
		String command = pair.command;
		
		Point curFindPosLocal = pair.cursorPos;
		
		int index = pair.text.indexOf("-");
		String textToFind = pair.text.substring(0, index).toString();
		String textToReplaceWith = pair.text.substring(index+1).toString();
		
		String addedInfo = (String)pair.addedInfo;
		int index0 = addedInfo.indexOf("-", 0);		
		boolean isAll = Boolean.parseBoolean(addedInfo.substring(0, index0));
		
		int index1 = addedInfo.indexOf("-", index0+1);
		boolean isForward = Boolean.parseBoolean(addedInfo.substring(index0+1, index1));
		
		int index2 = addedInfo.indexOf("-", index1+1);
		boolean isScopeAll = Boolean.parseBoolean(addedInfo.substring(index1+1, index2));
		
		int index3 = addedInfo.indexOf("-", index2+1);
		boolean isCaseSensitive = Boolean.parseBoolean(addedInfo.substring(index2+1, index3));
		
		boolean isWholeWord;
		
		// replace-find(isAll==false) 시 backupForUndo_replace()에서 addedInfo의 구조
		/************************************************************************************ 
		 * if (isScopeAll==false) {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
				isWholeWord + "-" + 
				this.selectP1.x + "-" + this.selectP1.y + "-" + this.selectP2.x + "-" + this.selectP2.y + "-" +
				findP1.x + "-" + findP1.y + "-" + findP2.x + "-" + findP2.y + "-" +
				replacePosP1.x + "-" + replacePosP1.y + "-" + replacePosP2.x + "-" + replacePosP2.y;;
		}
		else {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
				isWholeWord + "-" +
				findP1.x + "-" + findP1.y + "-" + findP2.x + "-" + findP2.y + "-" +
				replacePosP1.x + "-" + replacePosP1.y + "-" + replacePosP2.x + "-" + replacePosP2.y;;
		}*****************************************************************************************/
		
		// replaceAll(isAll==true)일 경우 addedInfo의 구조
		/******************************************************************************************
		 * String addedInfo;
		if (isScopeAll==false) {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
				isWholeWord + "-" + 
				this.selectP1.x + "-" + this.selectP1.y + "-" + this.selectP2.x + "-" + this.selectP2.y;
		}
		else {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
				isWholeWord;
		}************************************************************************************************/
		
		int selectP1_x, selectP1_y, selectP2_x, selectP2_y = 0;
		int findP1_x, findP1_y, findP2_x, findP2_y = 0;
		int replacePosP1_x=0, replacePosP1_y=0, replacePosP2_x=0, replacePosP2_y=0;
		if (isScopeAll==false) {
			int index4 = addedInfo.indexOf("-", index3+1);
			isWholeWord = Boolean.parseBoolean(addedInfo.substring(index3+1, index4));
			
			int index5 = addedInfo.indexOf("-", index4+1);
			selectP1_x = Integer.parseInt(addedInfo.substring(index4+1, index5));
			
			int index6 = addedInfo.indexOf("-", index5+1);
			selectP1_y = Integer.parseInt(addedInfo.substring(index5+1, index6));
			
			int index7 = addedInfo.indexOf("-", index6+1);
			selectP2_x = Integer.parseInt(addedInfo.substring(index6+1, index7));
			
			int index8 = addedInfo.indexOf("-", index7+1);
			selectP2_y = Integer.parseInt(addedInfo.substring(index7+1));
			
			this.selectP1.x = selectP1_x;
			this.selectP1.y = selectP1_y;
			this.selectP2.x = selectP2_x;
			this.selectP2.y = selectP2_y;
			
			if (isAll==false) {// isScopeAll==false, isAll==false
				int index9 = addedInfo.indexOf("-", index8+1);
				findP1_x = Integer.parseInt(addedInfo.substring(index8+1, index9));
				
				int index10 = addedInfo.indexOf("-", index9+1);
				findP1_y = Integer.parseInt(addedInfo.substring(index9+1, index10));
				
				int index11 = addedInfo.indexOf("-", index10+1);
				findP2_x = Integer.parseInt(addedInfo.substring(index10+1, index11));				
				
				int index12 = addedInfo.indexOf("-", index11+1);
				findP2_y = Integer.parseInt(addedInfo.substring(index11+1, index12));
				
				int index13 = addedInfo.indexOf("-", index12+1);
				replacePosP1_x = Integer.parseInt(addedInfo.substring(index12+1, index13));
				
				int index14 = addedInfo.indexOf("-", index13+1);
				replacePosP1_y = Integer.parseInt(addedInfo.substring(index13+1, index14));
				
				int index15 = addedInfo.indexOf("-", index14+1);
				replacePosP2_x = Integer.parseInt(addedInfo.substring(index14+1, index15));				
				
				int index16 = addedInfo.indexOf("-", index15+1);
				replacePosP2_y = Integer.parseInt(addedInfo.substring(index15+1));
				
				this.findP1.x = findP1_x;
				this.findP1.y = findP1_y;
				this.findP2.x = findP2_x;
				this.findP2.y = findP2_y;
			}
			
		}			
		else { // isScopeAll==true
			if (isAll==false) {// isScopeAll==true, isAll==false
				int index4 = addedInfo.indexOf("-", index3+1);
				isWholeWord = Boolean.parseBoolean(addedInfo.substring(index3+1, index4));
				
				int index9 = addedInfo.indexOf("-", index4+1);
				findP1_x = Integer.parseInt(addedInfo.substring(index4+1, index9));
				
				int index10 = addedInfo.indexOf("-", index9+1);
				findP1_y = Integer.parseInt(addedInfo.substring(index9+1, index10));
				
				int index11 = addedInfo.indexOf("-", index10+1);
				findP2_x = Integer.parseInt(addedInfo.substring(index10+1, index11));				
				
				int index12 = addedInfo.indexOf("-", index11+1);
				findP2_y = Integer.parseInt(addedInfo.substring(index11+1, index12));
				
				int index13 = addedInfo.indexOf("-", index12+1);
				replacePosP1_x = Integer.parseInt(addedInfo.substring(index12+1, index13));
				
				int index14 = addedInfo.indexOf("-", index13+1);
				replacePosP1_y = Integer.parseInt(addedInfo.substring(index13+1, index14));
				
				int index15 = addedInfo.indexOf("-", index14+1);
				replacePosP2_x = Integer.parseInt(addedInfo.substring(index14+1, index15));				
				
				int index16 = addedInfo.indexOf("-", index15+1);
				replacePosP2_y = Integer.parseInt(addedInfo.substring(index15+1));
				
				this.findP1.x = findP1_x;
				this.findP1.y = findP1_y;
				this.findP2.x = findP2_x;
				this.findP2.y = findP2_y;
			}
			else {// isScopeAll==true, isAll==true
				isWholeWord = Boolean.parseBoolean(addedInfo.substring(index3+1));
			}
		}
		
		if (command.equals("replace")) { // replace-find
		
			
			// replace-find시에 대체한 위치를 textToFind로 바꾼다.
			replaceCommon(isAll, new Point(replacePosP1_x, replacePosP1_y), new Point(replacePosP2_x, replacePosP2_y), textToReplaceWith, textToFind);
		}
		else { // replaceAll
			if (!isScopeAll) isSelecting = true;
		
			
			// 일반적인 경우
			
			// 여기에서 find()를 해서 textToReplaceWith을 다시 검색하면 잘못된 것이고
			// pair.listOfReplacePos을 사용해야 한다.
			ArrayList listFindPos = pair.listOfFindPos;
			ArrayList listReplacePos = pair.listOfReplacePos;
			// 아래 for문에서 this.listFindPos가 바뀌므로 백업해두었다가
			// undoBuffer에 넣는다.
			ArrayList listBackupForReplacePos = getClone(listReplacePos);
			int count = listReplacePos.count;
			int i;
			for (i=0; i<count; i+=2) {
				Point p1 = (Point)listReplacePos.list[i];
				Point p2 = (Point)listReplacePos.list[i+1];
				replaceCommon(isAll, p1, p2, textToReplaceWith, textToFind);
				
				if (!isScopeAll) {
					changeSelectP1AndP2(true, isForward, p1, p2, textToFind);
				}
				changeListFindPos(listReplacePos, i+2, p1.y, textToReplaceWith, textToFind);
			}
			
			pair.listOfReplacePos = listBackupForReplacePos;
						
			if (!isScopeAll) {
				makeSelectIndices(true, selectP1, selectP2);
			}
			
			setVScrollPos();
			setVScrollBar();
			if (scrollMode==ScrollMode.Both) {
				setHScrollPos();
				setHScrollBar();	
			}
			
			this.isFound = false;
		}// replaceAll
	}
	
	void createTextView() {
		int viewWidth = view.getWidth(); 
		int viewHeight = view.getHeight();
		int x, y, w, h;
		
		w = (int) (viewWidth * 0.85f);
		h = (int) (viewHeight * 0.75f);
		x = viewWidth/2 - w/2;
		y = viewHeight/2 - h/2;
		Rectangle boundsOfTextView = new Rectangle(x,y,w,h);
		textView = new TextView(false, false, this, null, boundsOfTextView, 
				view.getHeight()*0.02f, false, null, ScrollMode.VScroll, Compiler.backColor);
		textView.isReadOnly = true;
	}
	
	void showUndoBuffer() {
		CodeString message = new CodeString("<UndoBuffer Stack>\ncount   buffer(the contents to change) : command(Input key)\n(bottom)\n", 
				Compiler.keywordColor);
		int i;
		for (i=0; i<undoBuffer.bufferCommand.count; i++) {	
			String buffer = undoBuffer.buffer.getItem(i).str;
			String command = undoBuffer.bufferCommand.getItem(i);
			if (command.equals(EditText.NewLineChar)) command = "Enter";
			else if (command.equals(EditText.BackspaceChar)) command = "Backspace";
			else if (command.equals(EditText.DeleteChar)) command = "Delete";
			else if (command.equals("")) command = "Char key";
			
			CodeString line = new CodeString(Util.getLineOffset(i), 
					Compiler.varUseColor);
			
			if (command.equals("cut") || command.equals("paste")) {
				line = line.concate(new CodeString(buffer + " : " + command +"(Selected)\n", 
						Compiler.textColor));
			}
			else if (command.equals("Backspace") || command.equals("Enter") ||
					command.equals("Delete")) {
				boolean isSelecting = false;
				Object s = undoBuffer.arrayIsSelecting.getItem(i);
				// s가 null이면 isSelecting은 false이다.
				if (s!=null) isSelecting = ((Boolean)s).booleanValue();
				if (isSelecting==false) {
					line = line.concate(new CodeString(buffer + " : " + command +"\n", 
							Compiler.textColor));
				}
				else {
					Point selectP1 = (Point) undoBuffer.arrayCursorPos.getItem(i);
					Point selectP2 = (Point) undoBuffer.arrayAddedInfo.getItem(i);
					String select = "(" + selectP1.x + "," + selectP1.y + ")" + ", " + 
							"(" + selectP2.x + "," + selectP2.y + ")";
					line = line.concate(new CodeString(buffer + " : " + command + " (" + select + " selected)\n", 
							Compiler.textColor));
				}				
			}//else if (command.equals("Backspace") || command.equals("Enter") ||
			// command.equals("Delete")) {
			else {	// 일반적인 문자 key 입력	
				if (command.equals("replace") || command.equals("replaceAll")) {
					int indexOfSeparator = buffer.indexOf('-');
					if (indexOfSeparator!=-1) {
						String backupText = buffer.substring(0, indexOfSeparator);
						String replaceWith = buffer.substring(indexOfSeparator+1, buffer.length());
						line = line.concate(new CodeString(backupText + " : " + command +
								"("+backupText + " with " + replaceWith+")\n", 
								Compiler.textColor));
					}
				}
				else {
					line = line.concate(new CodeString(buffer + " : " + command +"\n", 
							Compiler.textColor));
				}
			}
			message = message.concate(line);
		}
		message = message.concate(new CodeString("(top)\n", Compiler.keywordColor));
		textView.setText(0, message);
		textView.setHides(false);
	}
	
	void showRedoBuffer() {
		CodeString message = new CodeString("<RedoBuffer Stack>\ncount   buffer(the contents to change) : command(Input key)\n(bottom)\n", 
				Compiler.keywordColor);
		int i;
		for (i=0; i<redoBuffer.bufferCommand.count; i++) {	
			String buffer = redoBuffer.buffer.getItem(i).str;
			String command = redoBuffer.bufferCommand.getItem(i);
			if (command.equals(EditText.NewLineChar)) command = "Enter";
			else if (command.equals(EditText.BackspaceChar)) command = "Backspace";
			else if (command.equals(EditText.DeleteChar)) command = "Delete";
			else if (command.equals("")) command = "Char key";
			
			CodeString line = new CodeString(Util.getLineOffset(i), 
					Compiler.varUseColor);
			
			if (command.equals("cut") || command.equals("paste")) {
				line = line.concate(new CodeString(buffer + " : " + command +"(Selected)\n", 
						Compiler.textColor));
			}
			else if (command.equals("Backspace") || command.equals("Enter") ||
					command.equals("Delete")) {
				boolean isSelecting = false;
				Object s = redoBuffer.arrayIsSelecting.getItem(i);
				// s가 null이면 isSelecting은 false이다.
				if (s!=null) isSelecting = ((Boolean)s).booleanValue();
				if (isSelecting==false) {
					line = line.concate(new CodeString(buffer + " : " + command +"\n", 
							Compiler.textColor));
				}
				else {
					Point selectP1 = (Point) redoBuffer.arrayCursorPos.getItem(i);
					Point selectP2 = (Point) redoBuffer.arrayPointP2.getItem(i);
					String select = "(" + selectP1.x + "," + selectP1.y + ")" + ", " + 
							"(" + selectP2.x + "," + selectP2.y + ")";
					line = line.concate(new CodeString(buffer + " : " + command + " (" + select + " selected)\n", 
							Compiler.textColor));
				}				
			}//else if (command.equals("Backspace") || command.equals("Enter") ||
			// command.equals("Delete")) {
			else {	
				if (command.equals("replace") || command.equals("replaceAll")) {
					int indexOfSeparator = buffer.indexOf('-');
					if (indexOfSeparator!=-1) {
						String replaceWith = buffer.substring(0, indexOfSeparator);
						String backupText = buffer.substring(indexOfSeparator+1, buffer.length());
						line = line.concate(new CodeString(backupText + " : " + command +
								"("+ replaceWith + " with " + backupText+")\n", 
								Compiler.textColor));
					}
				}
				else {
					line = line.concate(new CodeString(buffer + " : " + command +"\n", 
							Compiler.textColor));
				}
			}
			message = message.concate(line);
		}
		message = message.concate(new CodeString("(top)\n", Compiler.keywordColor));
		textView.setText(0, message);
		textView.setHides(false);
	}
	
	/** back, delete, enter키 등의 조작을 무효로 한다.*/
	void undo() {
		if (isReadOnly) return;
		
		// 터치시 한글모드와 버퍼를 초기화	
		Hangul.mode = Hangul.Mode.None;
		Hangul.resetBuffer();
		
		if (undoBuffer.buffer.count>0) {
			isSelecting = false;
			
			isModified = true;
			
			UndoBuffer.Pair pair = undoBuffer.pop();
			CodeString newLineText = pair.text;
			
			String command = pair.command;
			
		
			
			if (command.equals("replace")==false && command.equals("replaceAll")==false) {
				if (pair.isSelecting==false) {
					backUpForRedo(command, newLineText, pair.cursorPos, pair.addedInfo);
				}
				else {
					backUpForRedo(command, newLineText, pair.cursorPos, newLineText, pair.isSelecting, (Point)pair.addedInfo);
				}
			}
			else {//if (command.equals("replace") || command.equals("replaceAll")) {
				
				
			}
			
			
			if (command.equals("replace") || command.equals("replaceAll")) {
				undo_replace(pair);
				//커서위치는 undo(), undo_replace(), redo(), redo_replace(), replace()에서 
				// 설정하지 않고 replaceCommon()에서 공통적으로 설정한다. 
				// 위 함수들이 공통적으로 replaceCommon()을 호출하기 때문이다.
				
				// replace, replaceAll만 여기에서 백업한다.
				backUpForRedo(command, newLineText, pair.cursorPos, pair.isSelecting, pair.addedInfo, 
						pair.listOfFindPos, pair.listOfReplacePos);
			}
			else if (command.equals("cut")) {
				int numToDelete = getNumOfLinesInText(pair.cursorPos.y, 0, 1);
				int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
				if (newLineText.length()>0 && newLineText.charAt(newLineText.length()-1).c=='\n') 
					numOfNewLineChar--;
				
				setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
				
				cursorPos = this.getRelativeCursorPos(pair.cursorPos, (String)pair.addedInfo);
				
			}
			else if (command.equals("paste")) {
				String addedInfo = (String)pair.addedInfo;
				// numOfNewLineCharToDelete = 선택텍스트의 newLineChar개수 + 원래의 1줄
				int numOfNewLineCharToDelete = getNumOfNewLineChar(addedInfo) + 1;
				int numToDelete = getNumOfLinesInText(pair.cursorPos.y, 0, numOfNewLineCharToDelete);
				int numOfNewLineChar = getNumOfNewLineChar(newLineText);
				setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
								
				cursorPos.x = pair.cursorPos.x;
				cursorPos.y = pair.cursorPos.y;
			}//paste
			else if (command.equals(BackspaceChar) || command.equals(NewLineChar) || command.equals(DeleteChar))
			{	// backspace(0열)와 delete(마지막열)는 모두 특별하게 '\n'을 제거한 경우이거나, 그렇지 않은 경우이다.
				if (command.equals(BackspaceChar)) {					
					if (pair.isSelecting) {
						int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
						if (newLineText.length()>0 && newLineText.charAt(newLineText.length()-1).c=='\n') {
							numOfNewLineChar--;
						}
						// pair.cursorPos.y는 p1의 y이다.
						setTextMultiLine(pair.cursorPos.y, newLineText, 1, numOfNewLineChar);
						
						Point p2 = (Point) pair.addedInfo;
						cursorPos.x = p2.x+1;
						cursorPos.y = p2.y;
					}
					else {
						// newLineText가 두줄이면 0열에서 back키를 누른 경우이고
						// 한줄이면 그렇지 않은 경우
						// pair.cursorPos.y-1은 이전 라인(prevLine)을 의미한다.
						if (scrollMode==ScrollMode.Both) { 
							if (pair.cursorPos.x==0) {// 2줄이 1줄로 바뀐경우이므로 1줄을 삭제하고 원래의 2줄로 바꾼다.
								int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
								if (newLineText.length()>0 && newLineText.charAt(newLineText.length()-1).c=='\n') {
									numOfNewLineChar--;
								}
								if (pair.cursorPos.y==0) {
									setTextMultiLine(pair.cursorPos.y, newLineText, 1, numOfNewLineChar);
								}
								else {
									setTextMultiLine(pair.cursorPos.y-1, newLineText, 1, numOfNewLineChar);
								}
								cursorPos.x = 0;
								cursorPos.y = pair.cursorPos.y;
							}
							else {
								int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
								if (newLineText.length()>0 && newLineText.charAt(newLineText.length()-1).c=='\n') {
									numOfNewLineChar--;
								}
								setTextMultiLine(pair.cursorPos.y, newLineText, 1, numOfNewLineChar);
								cursorPos.x = pair.cursorPos.x;
								cursorPos.y = pair.cursorPos.y;
							}
						}//if (scrollMode==ScrollMode.Both) { 
						else { 
							int numToDelete = getNumOfLinesInText(pair.cursorPos.y-1, 0, 1);
							int numOfNewLineChar = getNumOfNewLineChar(newLineText);
							setTextMultiLine(pair.cursorPos.y-1, newLineText, numToDelete, numOfNewLineChar);
						}
					}
					
				}
				else if (command.equals(DeleteChar)) {
					if (pair.isSelecting) { // Both일 경우에만
						int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
						if (newLineText.length()>0 && newLineText.charAt(newLineText.length()-1).c=='\n') {
							numOfNewLineChar--;
						}
						setTextMultiLine(pair.cursorPos.y, newLineText, 1, numOfNewLineChar);
						
						Point p2 = (Point) pair.addedInfo;
						cursorPos.x = p2.x+1;
						cursorPos.y = p2.y;
					}
					else {
						if (scrollMode==ScrollMode.Both) { // 2줄이 1줄로 바뀐경우이므로 1줄을 삭제하고 원래의 2줄로 바꾼다.
							int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
							if (newLineText.length()>0 && newLineText.charAt(newLineText.length()-1).c=='\n') {
								numOfNewLineChar--;
							}
							int cursorPosX, cursorPosY;
							if (pair.cursorPos.x<=textArray[pair.cursorPos.y].count-1 && 
									textArray[pair.cursorPos.y].charAt(pair.cursorPos.x).c!='\n') {
								cursorPosX = pair.cursorPos.x+1;
								cursorPosY = pair.cursorPos.y;
							}
							else {
								cursorPosX = pair.cursorPos.x;
								cursorPosY = pair.cursorPos.y;
							}
														
							setTextMultiLine(pair.cursorPos.y, newLineText, 1, numOfNewLineChar);
														
							cursorPos.x = cursorPosX;
							cursorPos.y = cursorPosY;
						}
						else {
							int numToDelete = getNumOfLinesInText(pair.cursorPos.y, 0, 1);
							int numOfNewLineChar = getNumOfNewLineChar(newLineText);
							setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
						}
					}
					
				}
				else if (command.equals(NewLineChar)) { // 1줄이 2줄로 바뀐경우
					if (scrollMode==ScrollMode.Both) {
						int numOfNewLineChar = getNumOfNewLineChar(newLineText);
						setTextMultiLine(pair.cursorPos.y, newLineText, 2, numOfNewLineChar);
					}
					else {
						int numToDelete = getNumOfLinesInText(pair.cursorPos.y, 0, 2);
						int numOfNewLineChar = getNumOfNewLineChar(newLineText);
						setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
					}
					cursorPos.x = pair.cursorPos.x;
					cursorPos.y = pair.cursorPos.y;
				}
			}//else if (command.equals(BackspaceChar) || command.equals(NewLineChar) || command.equals(DeleteChar))
			else {	// 일반적인 경우
				setTextMultiLine(pair.cursorPos.y, newLineText, -1, 1);
				cursorPos.x = pair.cursorPos.x;
				cursorPos.y = pair.cursorPos.y;
			}
		}//if (undoBuffer.buffer.count>0) {
	}
	
	void redo_replace(RedoBuffer.Pair pair) {
		String command = pair.command;
		
		Point curFindPosLocal = pair.cursorPos;
		
		int index = pair.text.indexOf("-");
		String textToFind = pair.text.substring(0, index).toString();
		String textToReplaceWith = pair.text.substring(index+1).toString();
		
		String addedInfo = (String)pair.addedInfo;
		int index0 = addedInfo.indexOf("-", 0);		
		boolean isAll = Boolean.parseBoolean(addedInfo.substring(0, index0));
		
		int index1 = addedInfo.indexOf("-", index0+1);
		boolean isForward = Boolean.parseBoolean(addedInfo.substring(index0+1, index1));
		
		int index2 = addedInfo.indexOf("-", index1+1);
		boolean isScopeAll = Boolean.parseBoolean(addedInfo.substring(index1+1, index2));
		
		int index3 = addedInfo.indexOf("-", index2+1);
		boolean isCaseSensitive = Boolean.parseBoolean(addedInfo.substring(index2+1, index3));
		
		boolean isWholeWord;
		
		// replace-find(isAll==false) 시 backupForUndo_replace()에서 addedInfo의 구조
		/************************************************************************************ 
		 * if (isScopeAll==false) {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
				isWholeWord + "-" + 
				this.selectP1.x + "-" + this.selectP1.y + "-" + this.selectP2.x + "-" + this.selectP2.y + "-" +
				findP1.x + "-" + findP1.y + "-" + findP2.x + "-" + findP2.y + "-" +
				replacePosP1.x + "-" + replacePosP1.y + "-" + replacePosP2.x + "-" + replacePosP2.y;;
		}
		else {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
				isWholeWord + "-" +
				findP1.x + "-" + findP1.y + "-" + findP2.x + "-" + findP2.y + "-" +
				replacePosP1.x + "-" + replacePosP1.y + "-" + replacePosP2.x + "-" + replacePosP2.y;;
		}*****************************************************************************************/
		
		// replaceAll(isAll==true)일 경우 addedInfo의 구조
		/******************************************************************************************
		 * String addedInfo;
		if (isScopeAll==false) {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
				isWholeWord + "-" + 
				this.selectP1.x + "-" + this.selectP1.y + "-" + this.selectP2.x + "-" + this.selectP2.y;
		}
		else {
			addedInfo = isAll + "-" + isForward + "-" + isScopeAll + "-" + isCaseSensitive + "-" +
				isWholeWord;
		}************************************************************************************************/
		
		
		int selectP1_x, selectP1_y, selectP2_x, selectP2_y = 0;
		int findP1_x, findP1_y, findP2_x, findP2_y = 0;
		int replacePosP1_x=0, replacePosP1_y=0, replacePosP2_x=0, replacePosP2_y=0;
		if (isScopeAll==false) {
			int index4 = addedInfo.indexOf("-", index3+1);
			isWholeWord = Boolean.parseBoolean(addedInfo.substring(index3+1, index4));
			
			int index5 = addedInfo.indexOf("-", index4+1);
			selectP1_x = Integer.parseInt(addedInfo.substring(index4+1, index5));
			
			int index6 = addedInfo.indexOf("-", index5+1);
			selectP1_y = Integer.parseInt(addedInfo.substring(index5+1, index6));
			
			int index7 = addedInfo.indexOf("-", index6+1);
			selectP2_x = Integer.parseInt(addedInfo.substring(index6+1, index7));
			
			int index8 = addedInfo.indexOf("-", index7+1);
			selectP2_y = Integer.parseInt(addedInfo.substring(index7+1));
			
			this.selectP1.x = selectP1_x;
			this.selectP1.y = selectP1_y;
			this.selectP2.x = selectP2_x;
			this.selectP2.y = selectP2_y;
			
			if (isAll==false) {// isScopeAll==false, isAll==false
				int index9 = addedInfo.indexOf("-", index8+1);
				findP1_x = Integer.parseInt(addedInfo.substring(index8+1, index9));
				
				int index10 = addedInfo.indexOf("-", index9+1);
				findP1_y = Integer.parseInt(addedInfo.substring(index9+1, index10));
				
				int index11 = addedInfo.indexOf("-", index10+1);
				findP2_x = Integer.parseInt(addedInfo.substring(index10+1, index11));				
				
				int index12 = addedInfo.indexOf("-", index11+1);
				findP2_y = Integer.parseInt(addedInfo.substring(index11+1, index12));
				
				int index13 = addedInfo.indexOf("-", index12+1);
				replacePosP1_x = Integer.parseInt(addedInfo.substring(index12+1, index13));
				
				int index14 = addedInfo.indexOf("-", index13+1);
				replacePosP1_y = Integer.parseInt(addedInfo.substring(index13+1, index14));
				
				int index15 = addedInfo.indexOf("-", index14+1);
				replacePosP2_x = Integer.parseInt(addedInfo.substring(index14+1, index15));				
				
				int index16 = addedInfo.indexOf("-", index15+1);
				replacePosP2_y = Integer.parseInt(addedInfo.substring(index15+1));
				
				this.findP1.x = findP1_x;
				this.findP1.y = findP1_y;
				this.findP2.x = findP2_x;
				this.findP2.y = findP2_y;
			}
			
		}			
		else { // isScopeAll==true
			if (isAll==false) {// isScopeAll==true, isAll==false
				int index4 = addedInfo.indexOf("-", index3+1);
				isWholeWord = Boolean.parseBoolean(addedInfo.substring(index3+1, index4));
				
				int index9 = addedInfo.indexOf("-", index4+1);
				findP1_x = Integer.parseInt(addedInfo.substring(index4+1, index9));
				
				int index10 = addedInfo.indexOf("-", index9+1);
				findP1_y = Integer.parseInt(addedInfo.substring(index9+1, index10));
				
				int index11 = addedInfo.indexOf("-", index10+1);
				findP2_x = Integer.parseInt(addedInfo.substring(index10+1, index11));				
				
				int index12 = addedInfo.indexOf("-", index11+1);
				findP2_y = Integer.parseInt(addedInfo.substring(index11+1, index12));
				
				int index13 = addedInfo.indexOf("-", index12+1);
				replacePosP1_x = Integer.parseInt(addedInfo.substring(index12+1, index13));
				
				int index14 = addedInfo.indexOf("-", index13+1);
				replacePosP1_y = Integer.parseInt(addedInfo.substring(index13+1, index14));
				
				int index15 = addedInfo.indexOf("-", index14+1);
				replacePosP2_x = Integer.parseInt(addedInfo.substring(index14+1, index15));				
				
				int index16 = addedInfo.indexOf("-", index15+1);
				replacePosP2_y = Integer.parseInt(addedInfo.substring(index15+1));
				
				this.findP1.x = findP1_x;
				this.findP1.y = findP1_y;
				this.findP2.x = findP2_x;
				this.findP2.y = findP2_y;
			}
			else {// isScopeAll==true, isAll==true
				isWholeWord = Boolean.parseBoolean(addedInfo.substring(index3+1));
			}
		}
		
		if (command.equals("replace")) { // replace-find
			Point oldFindP1 = new Point(findP1.x, findP1.y);
			Point oldFindP2 = new Point(findP2.x, findP2.y);
			
			// replace-find시에 검색한 위치를 textToReplaceWith로 바꾼다.
			// findP1, findP2는 대체한 위치가 된다.
			replaceCommon(isAll, findP1, findP2, textToFind, textToReplaceWith);
			
			backUpForUndo_replace("replace", isAll, isForward, isScopeAll, isCaseSensitive, 
					isWholeWord, 
					oldFindP1, oldFindP2, 
					new Point(replacePosP1_x, replacePosP1_y), new Point(replacePosP2_x, replacePosP2_y), 
					textToFind, textToReplaceWith);
		}
		else {//replaceAll
			
			ArrayList listFindPos = pair.listOfFindPos;
			ArrayList listReplacePos = pair.listOfReplacePos;
			// 아래 for문에서 this.listFindPos가 바뀌므로 백업해두었다가
			// undoBuffer에 넣는다.
			ArrayList listBackupForFindPos = getClone(listFindPos);
			int count = listReplacePos.count;
			int i;
			for (i=0; i<count; i+=2) {
				Point p1 = (Point)listFindPos.list[i];
				Point p2 = (Point)listFindPos.list[i+1];
				replaceCommon(isAll, p1, p2, textToFind, textToReplaceWith);
				
				if (!isScopeAll) {
					changeSelectP1AndP2(true, isForward, p1, p2, textToFind);
				}
				changeListFindPos(listFindPos, i+2, p1.y, textToFind, textToReplaceWith);
			}
			
			if (!isScopeAll) makeSelectIndices(true, selectP1, selectP2);
			
						
			backUpForUndo_replace("replaceAll", isAll, isForward, isScopeAll, isCaseSensitive, 
					isWholeWord, curFindPosLocal, textToFind, textToReplaceWith, listBackupForFindPos, listReplacePos);
			
			setVScrollPos();
			setVScrollBar();
			if (scrollMode==ScrollMode.Both) {
				setHScrollPos();
				setHScrollBar();	
			}
			this.isFound = false;
			
			
			
		}//replaceAll
		
	}
	
	/** undo를 한 뒤에 호출, back, delete, enter키의 조작 무효를 다시 실행한다.*/
	void redo() {
		if (isReadOnly) return;
		
		// 터치시 한글모드와 버퍼를 초기화	
		Hangul.mode = Hangul.Mode.None;
		Hangul.resetBuffer();
		
		if (redoBuffer.buffer.count>0) {
			isSelecting = false;
			
			isModified = true;
			RedoBuffer.Pair pair = redoBuffer.pop();
			CodeString newLineText = pair.text;
			
			String command = pair.command;
			
			
			
			if (command.equals("cut")) {
				backUpForUndo(command, pair);
				
				int numToDelete;
				//int numOfNewLineChar = ((Integer)pair.addedInfo).intValue();
				int numOfNewLineChar = getNumOfNewLineChar((String)pair.addedInfo) + 1;
				numToDelete = getNumOfLinesInText(pair.cursorPos.y, cursorPos.x, numOfNewLineChar);
				setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, 1);
				
				//Point relativeCursorPos = this.getRelativeCursorPos(pair.cursorPos, (String)pair.addedInfo);
				
				cursorPos.x = pair.cursorPos.x;
				cursorPos.y = pair.cursorPos.y;
			}
			else if (command.equals("paste")) {
				backUpForUndo(command, pair);
				
				int numToDelete;
				numToDelete = getNumOfLinesInText(pair.cursorPos.y, cursorPos.x, 1);
				//int numOfNewLineChar = getNumOfNewLineChar(newLineText);
				int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
				if (newLineText.length()>0 && newLineText.charAt(newLineText.length()-1).c=='\n') {
					numOfNewLineChar--;
				}
				setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
				
				cursorPos = this.getRelativeCursorPos(pair.cursorPos, (String)pair.addedInfo);
				
			}
			else if (command.equals("replace") || command.equals("replaceAll")) {
				
				if (command.equals("replace")) {
					
					redo_replace(pair);
				}
				else {	// replaceAll
					// replaceAll일 경우 
					// redo를 undo하기 위한 백업은 redo_replace(pair)에서 한다.
					redo_replace(pair);
				}
				
				//커서위치는 undo(), undo_replace(), redo(), redo_replace(), replace()에서 
				// 설정하지 않고 replaceCommon()에서 공통적으로 설정한다. 
				// 위 함수들이 공통적으로 replaceCommon()을 호출하기 때문이다.
				//cursorPos.x = pair.cursorPos.x;
				//cursorPos.y = pair.cursorPos.y;
			}
			else if (command.equals(BackspaceChar) || command.equals(NewLineChar) || command.equals(DeleteChar))
			{	// backspace와 delete는 모두 특별하게 '\n'을 제거한 경우이다.
				if (command.equals(BackspaceChar)) {
					if (pair.isSelecting) {
						backUpForUndo(BackspaceChar, pair);
						
						CodeString selectedText = (CodeString) pair.addedInfo; 
						int numToDelete;
						if (selectedText.count>0 && selectedText.charAt(selectedText.count-1).c=='\n') {
							numToDelete = getNumOfNewLineChar(selectedText);
						}
						else {
							numToDelete = getNumOfNewLineChar(selectedText) + 1;
						}
						int numOfNewLineChar = getNumOfNewLineChar(newLineText);
						setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
						
						cursorPos.x = pair.cursorPos.x;
						cursorPos.y = pair.cursorPos.y;
					}
					else {
						backUpForUndo(BackspaceChar, pair);
						
						int numToDelete;	// pair.cursorPos.y = undoPos.y(back키를누른위치) - 1
						if (pair.cursorPos.x==0) { // 0열에서 '\n'이 지워지는 back키만
							if (pair.cursorPos.y>0) {
								numToDelete = getNumOfLinesInText(pair.cursorPos.y-1, cursorPos.x, 2);
								int numOfNewLineChar = getNumOfNewLineChar(newLineText);
								int cursorPosX, cursorPosY;
								cursorPosX = this.textArray[pair.cursorPos.y-1].count-1;
								cursorPosY = pair.cursorPos.y-1;
								setTextMultiLine(pair.cursorPos.y-1, newLineText, numToDelete, numOfNewLineChar);
								cursorPos.x = cursorPosX;
								cursorPos.y = cursorPosY;
							}
							else {
								numToDelete = getNumOfLinesInText(pair.cursorPos.y, cursorPos.x, 1);
								int numOfNewLineChar = getNumOfNewLineChar(newLineText);
								setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
								cursorPos.x = 0;
								cursorPos.y = 0;
							}
						}
						else { // 일반적인 경우
							numToDelete = getNumOfLinesInText(pair.cursorPos.y, cursorPos.x, 1);
							int numOfNewLineChar = getNumOfNewLineChar(newLineText);
							setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
							cursorPos.x = pair.cursorPos.x - 1;
							cursorPos.y = pair.cursorPos.y;
						}
						
					}
					
				}
				else if (command.equals(DeleteChar)) {
					if (pair.isSelecting) {
						backUpForUndo(DeleteChar, pair);
						
						CodeString selectedText = (CodeString) pair.addedInfo; 
						int numToDelete;
						if (selectedText.count>0 && selectedText.charAt(selectedText.count-1).c=='\n') {
							numToDelete = getNumOfNewLineChar(selectedText);
						}
						else {
							numToDelete = getNumOfNewLineChar(selectedText) + 1;
						}
						int numOfNewLineChar = getNumOfNewLineChar(newLineText);
						setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
						
						cursorPos.x = pair.cursorPos.x;
						cursorPos.y = pair.cursorPos.y;
						
					}
					else {
						backUpForUndo(DeleteChar, pair);
						
						int numToDelete;
						if (pair.cursorPos.x==this.textArray[pair.cursorPos.y].count-1) { 
							// 마지막열에서 '\n'이 지워지는 delete키만
							numToDelete = getNumOfLinesInText(pair.cursorPos.y, cursorPos.x, 2);
						}
						else {
							numToDelete = getNumOfLinesInText(pair.cursorPos.y, cursorPos.x, 1);
						}
						int numOfNewLineChar = getNumOfNewLineChar(newLineText);
						setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
						
						cursorPos.x = pair.cursorPos.x;
						cursorPos.y = pair.cursorPos.y;
					}
					
				}
				else if (command.equals(NewLineChar)) {
					
					backUpForUndo(NewLineChar, pair);
					
					// undo로 합쳐진 1줄 또는 여러 라인의 개수를 세어 그것들을 삭제하고 newLineText으로 바꾼다.
					int numToDelete;
					numToDelete = getNumOfLinesInText(pair.cursorPos.y, cursorPos.x, 1);
					int numOfNewLineChar = getNumOfNewLineChar(newLineText);
					/*if (newLineText.charAt(newLineText.length()-1).c=='\n') {
						numOfNewLineChar--;
					}*/
					setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
					cursorPos.x = 0;
					cursorPos.y = pair.cursorPos.y+1;
				}
			}//else if (command.equals(BackspaceChar) || command.equals(NewLineChar) || command.equals(DeleteChar))
			else {	// 일반적인 경우
				
				// a를 redo한 것을 undo하기 위해 백업
				//CodeString textForBackup = TextArrayToText(pair.cursorPos.y, 0, 1);
				//undoBuffer.push(new Point(cursorPos.x,pair.cursorPos.y), textForBackup);
				
				backUpForUndo(/*"general"*/command, pair);
				
				int numToDelete;
				numToDelete = getNumOfLinesInText(pair.cursorPos.y, cursorPos.x, 1);
				setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, 1);
				cursorPos.x = pair.cursorPos.x+1;
				cursorPos.y = pair.cursorPos.y;
			}
			
			
		}
	}
	
	void openFindReplaceDialog() {
		// 모든 EditText들이 findReplaceDialog을 공유하므로 changeBounds를 해준다.			
		Rectangle newBounds = new Rectangle(totalBounds.x, totalBounds.y, totalBounds.width, totalBounds.height/2);
		Edit.findReplaceDialog.changeBounds(newBounds);
		if (isSelecting) {
			Edit.findReplaceDialog.setScope(false);
		}
		else {
			Edit.findReplaceDialog.setScope(true);
		}
		Edit.findReplaceDialog.open(this, true);
	}
	
	/** hasToolbarAndMenuFontSize이 true인 editText(즉 singleLine editText는 불가능하다)만 
	 * 호출이 가능하다.*/
	void functionMenu_Listener(Object sender, String strMenuName) {
		Edit.menuFunction.open(false);
		
		/*static final String[] Menu_Function = { 
			"Undo(Ctrl+z)", "Redo(Ctrl+y)", 
			"Copy(Ctrl+c)", "Cut(Ctrl+x)", "Paste(Ctrl+v)", 
			"Find/Replace(Ctrl+f)", 
			"Select all(Ctrl+a)",
			"Show UndoBuffer", "Show RedoBuffer"
		};*/
		
		if (strMenuName.equals("Copy(Ctrl+c)")) {
			copy();
		}
		else if (strMenuName.equals("Cut(Ctrl+x)")) {
			cut();
		}
		else if (strMenuName.equals("Paste(Ctrl+v)")) {
			paste();
		}
		else if (strMenuName.equals("Select all(Ctrl+a)")) {
			selectAll();
		}
		else if (strMenuName.equals("Find/Replace(Ctrl+f)")) {
			openFindReplaceDialog();
		}
		else if (strMenuName.equals("Undo(Ctrl+z)")) {
			undo();
		}
		else if (strMenuName.equals("Redo(Ctrl+y)")) {
			redo();
		}
		else if (strMenuName.equals("Show UndoBuffer")) {
			showUndoBuffer();
		}
		else if (strMenuName.equals("Show RedoBuffer")) {
			showRedoBuffer();
		}
	}
	
	void fontSizeMenu_Listener(Object sender, String strFontSize) {
		Edit.menuFontSize.open(false);
		if (strFontSize.equals(Edit.Menu_FontSize[Edit.Menu_FontSize.length-1])) {
			// 모든 EditText들이 findReplaceDialog을 공유하므로 changeBounds를 해준다.			
			Rectangle newBounds = new Rectangle(totalBounds.x, totalBounds.y, totalBounds.width, totalBounds.height/2);
			Edit.fontSizeDialog.changeBounds(newBounds);
			Edit.fontSizeDialog.open(this);
			//fontSizeDialog.setOnTouchListener(this);
			return;
		}
		int indexOfPercent = strFontSize.indexOf("%");
		if (indexOfPercent!=-1) {
			strFontSize = strFontSize.substring(0, indexOfPercent);
		}
		fontSize = Float.parseFloat(strFontSize) * view.getHeight() * 0.01f;
		changeFontSize(fontSize);
		
	}
	
	/** ScrollMode가  Both일 때 폰트크기변경, bounds크기 변경을 하면  setText와  cursor를 초기화할 필요가 없다.*/
	class SetTextThread extends Thread {
		boolean initCursor;
		SetTextThread(boolean initCursor) {
			this.initCursor = initCursor;
		}
		public void run() {
			if (initCursor) {
				bound(BoundMode.SetText, true);
			}
			else {
				bound(BoundMode.SetText, false);
			}
			text = TextArrayToText(0, 0, 0, 0);			
			setText(0, text);
			CommonGUI.loggingForMessageBox.setHides(true);
			view.postInvalidate();
			
		}
	}
	
	
	
	/** 스크롤 윈도우와 선택영역의 교집합을 구한다.
	 * selectIndices 좌표 구성은 makeSelectIndices()를 참조한다.*/
	Point[] getIntersectWithSelect(boolean isSelectingOrFinding) {
		try{
		if (isSelectingOrFinding) {
			if (selectLenY==2) {
				int a;
				a=0;
				a++;
			}
			Point[] r = new Point[100];
			int count=0;
			if (scrollMode==ScrollMode.VScroll) {			
				int i;
				for (i=0; i<selectIndicesCount; i+=2) {
					if (vScrollPos<=selectIndices[i].y && 
							selectIndices[i].y<vScrollPos+numOfLinesPerPage) {
						if (!(count<=r.length-2)) r = Array.Resize(r, r.length+20);
						r[count++] = selectIndices[i];
						r[count++] = selectIndices[i+1];
					}				
				}
				r = Array.Resize(r, count);
				return r;
			}
			else {
				int i;
				for (i=0; i<selectIndicesCount; i+=2) {
					if (vScrollPos<=selectIndices[i].y && 
							selectIndices[i].y<vScrollPos+numOfLinesPerPage) {
						if (!(count<=r.length-2)) {
							r = Array.Resize(r, r.length+20);
						}
						r[count++] = selectIndices[i];
						r[count++] = selectIndices[i+1];
					}				
				}
				Point[] newR = new Point[100];
				int newCount=0;
				for (i=0; i<count; i+=2) {
					//TextLine text = textArray[r[i].y].subTextLine(
					//		r[i].x, r[i+1].x+1); 
					PartOfStr str =	getStringHScroll(textArray[r[i].y]);
					if (str==null) break;
					if (str.str.length()==0) {
						// 선택영역의 첫번째와 중간라인들은 수평스크롤 영역을 벗어나더라도
						// 선택영역이 그려질 수 있도록 한다.
						if ((selectLenY>1 && r[i+1].y==Select_FirstLine) ||
							(selectLenY>1 && r[i+1].y==Select_MiddleLine)) {
							if (!(newCount<=newR.length-2)) {
								newR = Array.Resize(newR, newR.length+20);
							}
							newR[newCount++] = new Point(r[i].x,r[i].y);
							newR[newCount++] = new Point(r[i+1].x,r[i+1].y);
						}					
					}
					else {
						// 선택영역과 수평스크롤 영역의 교집합을 구한다.
						Point originP = new Point(r[i].x,r[i+1].x);
						Point newP = new Point(str.start, str.end);					
						Point intersectPoint = getIntersect(originP,newP);
						if (intersectPoint!=null) {
							if (!(newCount<=newR.length-2)) {
								newR = Array.Resize(newR, newR.length+20);
							}
							newR[newCount++] = new Point(intersectPoint.x,r[i].y);
							newR[newCount++] = new Point(intersectPoint.y,r[i+1].y);
						}
					}
				}
				newR = Array.Resize(newR, newCount);
				return newR;
			}
		}
		else { //if (isSelectingOrFinding) {
			Point[] r = new Point[100];
			int count=0;
			if (scrollMode==ScrollMode.VScroll) {			
				int i;
				for (i=0; i<findIndicesCount; i+=2) {
					if (vScrollPos<=findIndices[i].y && 
							findIndices[i].y<vScrollPos+numOfLinesPerPage) {
						if (!(count<=r.length-2)) r = Array.Resize(r, r.length+20);
						r[count++] = findIndices[i];
						r[count++] = findIndices[i+1];
					}				
				}
				r = Array.Resize(r, count);
				return r;
			}
			else {
				int i;
				for (i=0; i<findIndicesCount; i+=2) {
					if (vScrollPos<=findIndices[i].y && 
							findIndices[i].y<vScrollPos+numOfLinesPerPage) {
						if (!(count<=r.length-2)) {
							r = Array.Resize(r, r.length+20);
						}
						r[count++] = findIndices[i];
						r[count++] = findIndices[i+1];
					}				
				}
				Point[] newR = new Point[100];
				int newCount=0;
				for (i=0; i<count; i+=2) {
					//TextLine text = textArray[r[i].y].subTextLine(
					//		r[i].x, r[i+1].x+1); 
					PartOfStr str =	getStringHScroll(textArray[r[i].y]);
					if (str==null) continue;
					if (str.str.length()==0) {
						// 선택영역의 첫번째와 중간라인들은 수평스크롤 영역을 벗어나더라도
						// 선택영역이 그려질 수 있도록 한다.
						if ((findLenY>1 && r[i+1].y==Select_FirstLine) ||
							(findLenY>1 && r[i+1].y==Select_MiddleLine)) {
							if (!(newCount<=newR.length-2)) {
								newR = Array.Resize(newR, newR.length+20);
							}
							newR[newCount++] = new Point(r[i].x,r[i].y);
							newR[newCount++] = new Point(r[i+1].x,r[i+1].y);
						}					
					}
					else {
						// 선택영역과 수평스크롤 영역의 교집합을 구한다.
						Point originP = new Point(r[i].x,r[i+1].x);
						Point newP = new Point(str.start, str.end);					
						Point intersectPoint = getIntersect(originP,newP);
						if (intersectPoint!=null) {
							if (!(newCount<=newR.length-2)) {
								newR = Array.Resize(newR, newR.length+20);
							}
							newR[newCount++] = new Point(intersectPoint.x,r[i].y);
							newR[newCount++] = new Point(intersectPoint.y,r[i+1].y);
						}
					}
				}
				newR = Array.Resize(newR, newCount);
				return newR;
			}
		}
		}catch(Exception e) {
			//Log.e("getIntersectWithSelect", e.toString());
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return null;
		}
	}
	
	/** 교집합 구하기. 
	 * originP:현재줄에서 선택영역의 시작과 끝 x, newP:현재줄에서 수평스크롤 영역의 시작과 끝 x*/
	Point getIntersect(Point originP, Point newP) {
		if (newP.y<originP.x) // ---  ____	 ---:newP, ___:originP
			return null;
		else if (newP.x<originP.x && newP.y<originP.y) // --__--___ 
			return new Point(originP.x,newP.y); 
		else if (newP.x>=originP.x && newP.y<=originP.y) // _-_-_
			return new Point(newP.x,newP.y);
		else if (newP.x>=originP.x && newP.y>originP.y)  // __--__--__--
			return new Point(newP.x,originP.y);
		else if (newP.x>originP.y)	// _____   -----
			return null;
		
		if (originP.y<newP.x) // ---  ____		---:originP, ___:newP
			return null;
		else if (originP.x<newP.x && originP.y<newP.y) // --__--___ 
			return new Point(newP.x,originP.y); 
		else if (originP.x>=newP.x && originP.y<=newP.y) // _-_-_
			return new Point(originP.x,originP.y);
		else if (originP.x>=newP.x && originP.y>newP.y)  // __--__--__--
			return new Point(originP.x,newP.y);
		else if (originP.x>newP.y)	// _____   -----
			return null;
		
		return null;
		
	}
	
	/** functionMenu_listener(), 키보드 가속기(EditText의 OnTouchEvent())에서 호출*/
	void selectAll() {
		if (selectP1==null) {
			selectP1 = new Point();			
		}
		selectP1.x = 0;
		selectP1.y = 0;
		
		if (selectP2==null) {
			selectP2 = new Point();			
		}
		if (textArray[numOfLines-1].length()>0)
			selectP2.x = textArray[numOfLines-1].length()-1;
		else
			selectP2.x = 0;
		selectP2.y = numOfLines-1;
		
		makeSelectIndices(true, selectP1, selectP2);
		
		this.isSelecting = true;
	}
	
	
	
	/** EditText의 이벤트 리스너, 이후에 draw가 호출된다*/
	void editText_Listener(Object sender, MotionEvent e) {
		// scrollMode가 VScroll인지 Both인지는 상관이 없다. 
		// getCursorPos에서 처리하기 때문이다.
		try{
		paint.setTextSize(fontSize);
		
		if (e.actionCode==MotionEvent.ActionDown) {
			// 기존 선택한 텍스트 위치를 backup한다.
			if (isSelecting) {
				if (selectIndices!=null && selectIndicesCount>=2) {
					selectStartLine = selectIndices[0].y;
					selectEndLine = selectIndices[selectIndicesCount-2].y;
				}
			}
						
			isFound = false;
			getCursorPos(e);
			//setToolbarAndCurState(cursorPos);
			isSelecting = false;
			this.selectLenY = 0;
			this.selectP1 = new Point(cursorPos.x,cursorPos.y);
			selectIndicesCount = 0;
			
			
		}
		}catch(Exception e1) {
			//Log.e("EditText-OnTouchEvent ActionMove", e1.toString());
			e1.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e1);
			//return;
		}
		
		int scrollBounds = (int)(view.getHeight() * 0.03f);
		
		try {
		// 0 line   : start, end -> (startX, 0), (endX, 선택영역의 첫번째(0))
		// 1-Y line : start, end -> (startX, 1-Y), (endX, 선택영역의 중간(1))
		// Y line   : start, end -> (startX, Y), (endX, 선택영역의 마지막(2))
		if (e.actionCode==MotionEvent.ActionMove || e.actionCode==MotionEvent.ActionUp) {
			if (e.actionCode==MotionEvent.ActionUp) {
				int a;
				a=0;
				a++;
			}
			if (this.iName==CommonGUI.textViewLogBird.iName) {
				int a;
				a=0;
				a++;
			}
			getCursorPos(e);
			if (isSelecting) { // 자동 스크롤 한다.				
				// scrollMode에 상관없이 자동 스크롤 한다.
				if (e.y>bounds.bottom()-scrollBounds) {
					if (numOfLines>numOfLinesPerPage) {
						//setHeightOfVScrollPos(heightOfvScrollInc);
						vScrollPos++;
						setVScrollBar();
						Point r = cursorPos;
						selectP2 = new Point(r.x,r.y);					
						makeSelectIndices(true, selectP1, selectP2);
						return;
					}							
				}
				else if (e.y<bounds.y){
					if (numOfLines>numOfLinesPerPage) {
						//setHeightOfVScrollPos(-heightOfvScrollInc);
						//setVScrollBar(true);
						vScrollPos--;
						setVScrollBar();
						Point r = cursorPos;
						selectP2 = new Point(r.x,r.y);					
						makeSelectIndices(true, selectP1, selectP2);
						return;
					}
				}
				
				if (scrollMode==ScrollMode.Both) {
					if (e.x>bounds.right()-scrollBounds) {
						float widthOfCurLine = paint.measureText(textArray[cursorPos.y].toString());
						if (widthOfCurLine>widthOfCharsPerPage) {
							//setWidthOfHScrollPos(widthOfhScrollInc);
							widthOfhScrollPos += widthOfhScrollInc;
							setHScrollBar();
							Point r = cursorPos;
							selectP2 = new Point(r.x,r.y);					
							makeSelectIndices(true, selectP1, selectP2);
							return;
						}							
					}
					else if (e.x<bounds.x){
						float widthOfCurLine = paint.measureText(textArray[cursorPos.y].toString());
						if (widthOfCurLine>widthOfCharsPerPage) {
							//setWidthOfHScrollPos(-widthOfhScrollInc);
							//setHScrollBar(true);
							widthOfhScrollPos -= widthOfhScrollInc;
							setHScrollBar();
							Point r = cursorPos;
							selectP2 = new Point(r.x,r.y);					
							makeSelectIndices(true, selectP1, selectP2);
							return;
						}
					}
				}
			}	// if (isSelecting==true)
			Point r = cursorPos;
			if (e.actionCode==MotionEvent.ActionMove) {
				isSelecting = true;
			}
			selectP2 = new Point(r.x,r.y);					
			makeSelectIndices(true, selectP1, selectP2);
			
		} // if (e.actionCode==MotionEvent.ActionMove)
		
		
		}catch(Exception e1) {
			//Log.e("EditText-OnTouchEvent ActionMove", e1.toString());
			e1.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e1);
			return;
		}
		finally {
			// 터치시 한글모드와 버퍼를 초기화	
			Hangul.mode = Hangul.Mode.None;
			Hangul.resetBuffer();
		}
	}
	
	/**selectP1,selectP2는 논리적 좌표*/ 
	public void makeSelectIndices(boolean isSelectingOrFinding, Point selectP1, Point selectP2) {
		try{
		if (isSelectingOrFinding) {
			int i;
			selectIndicesCount = 0;
			if (selectP1==null || selectP2==null) return;
			if (selectP1.y<=selectP2.y) {
				selectLenY = selectP2.y - selectP1.y + 1;
				
				// 경계처리
				if (textArray[selectP2.y].length()<=selectP2.x) {
					if (textArray[selectP2.y].length()>0)
						selectP2.x = textArray[selectP2.y].length()-1;
					else
						selectP2.x = 0;
				}
				if (textArray[selectP1.y].length()<=selectP1.x) {
					if (textArray[selectP1.y].length()>0)
						selectP1.x = textArray[selectP1.y].length()-1;
					else 
						selectP1.x = 0;
				}
				
				if (selectLenY==1) {
					if (selectP1.x<=selectP2.x) {								
						selectIndices[selectIndicesCount++] = new Point(selectP1.x,selectP1.y);
						selectIndices[selectIndicesCount++] = new Point(selectP2.x,Select_FirstLine);
					}
					else {
						selectIndices[selectIndicesCount++] = new Point(selectP2.x,selectP2.y);
						selectIndices[selectIndicesCount++] = new Point(selectP1.x,Select_FirstLine);
					}
				}
				else {
					// 첫번째 라인
					selectIndices[selectIndicesCount++] = new Point(selectP1.x,selectP1.y);
					selectIndices[selectIndicesCount++] = new Point(textArray[selectP1.y].length()-1, 
							Select_FirstLine);
					for (i=selectP1.y+1; i<selectP2.y; i++) {
						if (!(selectIndicesCount<=selectIndices.length-2)) 
							selectIndices = Array.Resize(selectIndices, selectIndices.length+20);
						selectIndices[selectIndicesCount++] = new Point(0, i);
						selectIndices[selectIndicesCount++] = 
								new Point(textArray[i].length()-1, Select_MiddleLine);
					}
					// 마지막 라인
					if (!(selectIndicesCount<=selectIndices.length-2)) 
							selectIndices = Array.Resize(selectIndices, selectIndices.length+20);
					selectIndices[selectIndicesCount++] = new Point(0, selectP2.y);
					selectIndices[selectIndicesCount++] = new Point(selectP2.x,Select_LastLine);
				}
				selectIndicesCountForCopy = selectIndicesCount;
			}
			else {		// selectP1.y > selectP2.y
				selectLenY = selectP1.y - selectP2.y + 1;
				
				// 경계처리
				if (textArray[selectP2.y].length()<=selectP2.x) {
					if (textArray[selectP2.y].length()>0)
						selectP2.x = textArray[selectP2.y].length()-1;
					else 
						selectP2.x = 0;
				}
				if (textArray[selectP1.y].length()<=selectP1.x) {
					if (textArray[selectP1.y].length()>0)
						selectP1.x = textArray[selectP1.y].length()-1;
					else
						selectP1.x = 0;
				}
				
				// 첫번째 라인
				selectIndices[selectIndicesCount++] = new Point(selectP2.x,selectP2.y);
				selectIndices[selectIndicesCount++] = new Point(textArray[selectP2.y].length()-1, 
						Select_FirstLine);
				for (i=selectP2.y+1; i<selectP1.y; i++) {
					if (!(selectIndicesCount<=selectIndices.length-2)) 
						selectIndices = Array.Resize(selectIndices, selectIndices.length+20);
					selectIndices[selectIndicesCount++] = new Point(0, i);
					selectIndices[selectIndicesCount++] = new Point(textArray[i].length()-1, Select_MiddleLine);
				}
				// 마지막 라인
				if (!(selectIndicesCount<=selectIndices.length-2)) 
					selectIndices = Array.Resize(selectIndices, selectIndices.length+20);
				selectIndices[selectIndicesCount++] = new Point(0, selectP1.y);
				selectIndices[selectIndicesCount++] = new Point(selectP1.x,Select_LastLine);
				
				selectIndicesCountForCopy = selectIndicesCount;
			}
		}
		else {	// if (isSelecting)
			int i;
			findIndicesCount = 0;
			if (findP1==null || findP2==null) return;
			if (findP1.y<=findP2.y) {
				findLenY = findP2.y - findP1.y + 1;
				
				// 경계처리
				if (textArray[findP2.y].length()<=findP2.x) {
					if (textArray[findP2.y].length()>0)
						findP2.x = textArray[findP2.y].length()-1;
					else
						findP2.x = 0;
				}
				if (textArray[findP1.y].length()<=findP1.x) {
					if (textArray[findP1.y].length()>0)
						findP1.x = textArray[findP1.y].length()-1;
					else
						findP1.x = 0;
				}
				
				if (findLenY==1) {
					if (findP1.x<=findP2.x) {								
						findIndices[findIndicesCount++] = new Point(findP1.x,findP1.y);
						findIndices[findIndicesCount++] = new Point(findP2.x,Select_FirstLine);
					}
					else {
						findIndices[findIndicesCount++] = new Point(findP2.x,findP2.y);
						findIndices[findIndicesCount++] = new Point(findP1.x,Select_FirstLine);
					}
				}
				else {
					// 첫번째 라인
					findIndices[findIndicesCount++] = new Point(findP1.x,findP1.y);
					findIndices[findIndicesCount++] = new Point(textArray[findP1.y].length()-1, 
							Select_FirstLine);
					for (i=findP1.y+1; i<findP2.y; i++) {
						if (!(findIndicesCount<=findIndices.length-2)) 
							findIndices = Array.Resize(findIndices, findIndices.length+20);
						findIndices[findIndicesCount++] = new Point(0, i);
						findIndices[findIndicesCount++] = 
								new Point(textArray[i].length()-1, Select_MiddleLine);
					}
					// 마지막 라인
					if (!(findIndicesCount<=findIndices.length-2)) 
							findIndices = Array.Resize(findIndices, findIndices.length+20);
					findIndices[findIndicesCount++] = new Point(0, findP2.y);
					findIndices[findIndicesCount++] = new Point(findP2.x,Select_LastLine);
				}
				findIndicesCountForCopy = findIndicesCount;
			}
			else {		// findP1.y > findP2.y
				findLenY = findP1.y - findP2.y + 1;
				
				// 경계처리
				if (textArray[findP2.y].length()<=findP2.x) {
					if (textArray[findP2.y].length()>0)
						findP2.x = textArray[findP2.y].length()-1;
					else
						findP2.x = 0;
				}
				if (textArray[findP1.y].length()<=findP1.x) {
					if (textArray[findP1.y].length()>0)
						findP1.x = textArray[findP1.y].length()-1;
					else
						findP1.x = 0;
				}
				
				// 첫번째 라인
				findIndices[findIndicesCount++] = new Point(findP2.x,findP2.y);
				findIndices[findIndicesCount++] = new Point(textArray[findP2.y].length()-1, 
						Select_FirstLine);
				for (i=findP2.y+1; i<findP1.y; i++) {
					if (!(findIndicesCount<=findIndices.length-2)) 
						findIndices = Array.Resize(findIndices, findIndices.length+20);
					findIndices[findIndicesCount++] = new Point(0, i);
					findIndices[findIndicesCount++] = new Point(textArray[i].length()-1, Select_MiddleLine);
				}
				// 마지막 라인
				if (!(findIndicesCount<=findIndices.length-2)) 
					findIndices = Array.Resize(findIndices, findIndices.length+20);
				findIndices[findIndicesCount++] = new Point(0, findP1.y);
				findIndices[findIndicesCount++] = new Point(findP1.x,Select_LastLine);
				
				findIndicesCountForCopy = findIndicesCount;
			}
		}
		}catch(Exception e) {
			//Log.e("makeSelectIndices",e.toString());
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
	}
	
	
	
	
	boolean isNotEqual(boolean isCaseSensitive, char a, char b) {
		if (isCaseSensitive) {
			if (a!=b) return true;
			return false;
		}
		else {
			if ('A'<=a && a<='z' && 'A'<=b && b<='z') {	// 둘다 영문자이면
				int diff = 'a'-'A';
				if (a>b) {
					if (a==b+diff) return false;
				}
				else if (a<b) {
					if (b==a+diff) return false;
				}
				else return false;
			}
			else {
				if (a!=b) return true;
				return false;
			}
			return true;
		}
	}
	
	/** find에서 whole word시 호출*/
	boolean isAdjacentCharSeparator(Point selectP1, Point selectP2) {
		
		int i;
		boolean isLeftSeparator = false;
		if (selectP1.x>0) {
			int index = selectP1.x-1;
			for (i=0; i<Edit.find_separators.length; i++) {
				if (textArray[selectP1.y].charAt(index).c==Edit.find_separators[i]) {
					isLeftSeparator = true;
					break;
				}
			}
		}
		else {
			if (selectP1.y>0) {
				int indexY = selectP1.y-1;
				int indexX = textArray[indexY].length()-1;
				for (i=0; i<Edit.find_separators.length; i++) {
					if (textArray[indexY].charAt(indexX).c==Edit.find_separators[i]) {
						isLeftSeparator = true;
						break;
					}
				}
			}
			else {
				if (selectP1.x==0 && selectP1.y==0)
					isLeftSeparator = true;
			}
		}
		
		
		boolean isRightSeparator = false;
		if (selectP2.x+1 < textArray[selectP2.y].length()) {
			int index = selectP2.x+1;
			for (i=0; i<Edit.find_separators.length; i++) {
				if (textArray[selectP2.y].charAt(index).c==Edit.find_separators[i]) {
					isRightSeparator = true;
					break;
				}
			}
		}
		else {
			if (selectP2.y+1 < numOfLines) {
				int indexY = selectP2.y+1;
				int indexX = 0;
				for (i=0; i<Edit.find_separators.length; i++) {
					if (textArray[indexY].charAt(indexX).c==Edit.find_separators[i]) {
						isRightSeparator = true;
						break;
					}
				}
			}
			else {
				if (selectP2.y==numOfLines-1 && selectP2.x==textArray[selectP2.y].length()-1)
					isRightSeparator = true;
			}
		}
		
		if (isLeftSeparator && isRightSeparator) return true;
		else return false;
	}
	
	void deleteClone(int startLine) {
		int i;
		int[] items = new int[20];
		int countItems=0;
		boolean[] listRedunduncy = new boolean[listFindPos.count];
		int k;
		for (i=0; i<listFindPos.count; i+=2) {
			Point p = (Point)listFindPos.getItem(i);
			if (p.y==startLine) {
				
				for (k=0; k<countItems; k++) {
					if (items[k] == p.x) break;
				}
				if (k==countItems) {	// 발견되지 않았음.
					items[countItems++] = p.x;
				}
				else {
					listRedunduncy[i] = true;
					listRedunduncy[i+1] = true;
				}
			}
		}
		
		ArrayList listNewFindPos = new ArrayList(listFindPos.count);
		for (i=0; i<listRedunduncy.length; i++) {
			if (listRedunduncy[i]==false) {
				listNewFindPos.add(listFindPos.list[i]);
			}
		}
		
		this.listFindPos = listNewFindPos;
	}
	
	void findCommon(boolean isAll, boolean isForward, boolean isScopeAll, boolean isCaseSensitive, 
		boolean isWholeWord, Point curFindPosLocal, String textToFind) {
		
		int i, j;
		int k;
		int len;
		boolean bInit = true;
		int endX, endY;
		
		if (isAll) {
			listFindPos.reset();
		}
		
		boolean[] searchedLines = new boolean[pointFindEnd.y-pointFindStart.y+1];
		
		if (isForward) {
			i=curFindPosLocal.x;
			k=0;
			for (j=curFindPosLocal.y; j<numOfLines; j++) {
				len = textArray[j].length();
				
				if (searchedLines[j-pointFindStart.y]){
					// 종료조건 : curFindPosLocal.y(검색시작라인) 다음 라인을 시작하기 전에 종료, 즉 검색시작라인을 완전히 검색한다.
					if (curFindPosLocal.y!=pointFindEnd.y) {
						if (j==curFindPosLocal.y+1) {
							return;
						}
					}
					else {
						// 마지막 라인이므로 0번째 라인에서 종료
						if (j==pointFindStart.y) {
							return;
						}
					}
				}
				
				// bInit은 초기값 설정에서만 사용한다.
				if (bInit) {	// 뒤에 나오는 for 루프의 초기값 설정
					i=curFindPosLocal.x;
					bInit = false;
				}
				else {	// 뒤 루프를 한번 실행한 경우, 즉 한 줄을 검색한 경우
					if (isScopeAll) i = 0;
					else {
						if (j!=pointFindStart.y) i = 0;
						else i = pointFindStart.x;
					}
					// i==0으로 시작할 때만 검색을 다 한 것으로 예정.
					searchedLines[j-pointFindStart.y] = true; 
				}
				
				if (isScopeAll) endX = len-1;
				else {
					if (j==pointFindEnd.y) endX = pointFindEnd.x;
					else endX = len-1;
				}
				
				
				for (; i<=endX && i>=0; i++) {	// i<=len이어야 루프를 종료할 수 있다.					
					//if (i==endX) break; // 이것을 하지 않으면 무한 루프 발생
					
					if (isNotEqual(isCaseSensitive, textArray[j].charAt(i).c, 
						textToFind.charAt(k))) {
						k = 0;
					}
					else {
						if (k==0) { // 시작위치
							findP1.x = i;
							findP1.y = j;
						}
						if (k==textToFind.length()-1) {	// 찾은경우, 끝위치
							findP2.x = i;
							findP2.y = j;
							k = 0;
							
							if (!isWholeWord ) {
								makeSelectIndices(false, findP1, findP2);
								
								isFound = true;
								if (isAll) {
									Point p1 = new Point(findP1.x, findP1.y);
									Point p2 = new Point(findP2.x, findP2.y);
									listFindPos.add(p1);
									listFindPos.add(p2);
								}
								
								cursorPos.x = i+1;	// find next, +1을 하지 않으면 한 문자 찾기에서 전진을 안한다.
								cursorPos.y = j;
								
								if (isAll) {
									curFindPosLocal.x = cursorPos.x;
									curFindPosLocal.y = cursorPos.y;
								}
								// 검색한 위치로 스크롤바를 이동시킨다.
								if (!(vScrollPos<=cursorPos.y && cursorPos.y<vScrollPos+numOfLinesPerPage)) {
									vScrollPos = cursorPos.y;
									setVScrollBar();
								}							
								if (scrollMode==ScrollMode.Both) {
									setHScrollPos();
									setHScrollBar();
								}
								if (!isAll) return;
							}
							else {	// wholeWord
								if (isAdjacentCharSeparator(findP1, findP2)) {
									makeSelectIndices(false, findP1, findP2);
									
									isFound = true;
									if (isAll) {
										Point p1 = new Point(findP1.x, findP1.y);
										Point p2 = new Point(findP2.x, findP2.y);
										listFindPos.add(p1);
										listFindPos.add(p2);
									}
									
									cursorPos.x = i+1;	// find next, +1을 하지 않으면 한 문자 찾기에서 전진을 안한다.
									cursorPos.y = j;
									if (isAll) {
										curFindPosLocal.x = cursorPos.x;
										curFindPosLocal.y = cursorPos.y;
									}
									if (!(vScrollPos<=cursorPos.y && cursorPos.y<vScrollPos+numOfLinesPerPage)) {
										vScrollPos = cursorPos.y;
										setVScrollBar();
									}							
									if (scrollMode==ScrollMode.Both) {
										setHScrollPos();
										setHScrollBar();
									}
									if (!isAll) return;
								}
							}// wholeWord
						}	// if (k==textToFind.length()-1) {	// 찾은경우, 끝위치
						else if (k<textToFind.length()-1) k++;
					}//if (isNotEqual(isCaseSensitive, textArray[j].charAt(i).c, 
					//		textToFind.charAt(k))==false) {
				}//for (; i<=endX && i>=0; i++) {
				
				if (isScopeAll) endY = numOfLines-1;
				else endY = pointFindEnd.y;
				if (j==endY) {	// 다시 처음라인으로 간다.
					if (i>endX) {
						if (isScopeAll) j = -1;
						else j = pointFindStart.y-1;
						k = 0;
					}
				}
			}	// for (j=curFindPosLocal.y; j<numOfLines; j++) {
		} // if
		else {	// backward
			i=curFindPosLocal.x-1;
			k = textToFind.length()-1;
			for (j=curFindPosLocal.y; j>=0; j--) {
				len = textArray[j].length();				
				
				if (searchedLines[j-pointFindStart.y]){
					// 종료조건 : curFindPosLocal.y(검색시작라인) 이전 라인을 시작하기 전에 종료,즉 검색시작라인을 완전히 검색한다.
					if (curFindPosLocal.y!=pointFindStart.y) {
						if (j==curFindPosLocal.y-1) {
							return;
						}
					}
					else {
						// 0번째 라인이므로 마지막 라인에서 종료
						if (j==pointFindEnd.y) {
							return;
						}
					}
				}
				
				// bInit은 초기값 설정에서만 사용한다.
				if (bInit) {	// 뒤에 나오는 for 루프의 초기값 설정
					i = curFindPosLocal.x-1;
					bInit = false;
				}
				else {	// 뒤 루프를 한번 실행한 경우, 즉 한 줄을 검색한 경우
					if (isScopeAll) i = len-1;
					else {
						if (j!=pointFindEnd.y) i = len-1;
						else i = pointFindEnd.x;
					}
					
					// i==len-1으로 시작할 때만 검색을 다 한 것으로 예정.
					searchedLines[j-pointFindStart.y] = true; 
				}
				
				if (isScopeAll) endX = 0;
				else {
					if (j==pointFindStart.y) endX = pointFindStart.x;
					else endX = 0;
				}
				
				
				for (; i>=endX && i<len; i--) {	// i>=-1이어야 루프를 종료할 수 있다.
					//if (i==endX) break;  // 이것을 하지 않으면 무한 루프 발생
					
					if (isNotEqual(isCaseSensitive, textArray[j].charAt(i).c, 
						textToFind.charAt(k))) {
						k = textToFind.length()-1;
					}
					else {
						if (k==textToFind.length()-1) { // 시작위치
							findP2.x = i;
							findP2.y = j;
						}
						if (k==0) {	// 찾은경우, 끝위치
							findP1.x = i;
							findP1.y = j;
							k = textToFind.length()-1;
							
							if (!isWholeWord) {
								makeSelectIndices(false, findP1, findP2);
								//isSelecting = true;
								
								isFound = true;
								if (isAll) {
									Point p1 = new Point(findP1.x, findP1.y);
									Point p2 = new Point(findP2.x, findP2.y);
									listFindPos.add(p1);
									listFindPos.add(p2);
								}
								
								cursorPos.x = i;
								cursorPos.y = j;
								if (isAll) {
									curFindPosLocal.x = cursorPos.x;
									curFindPosLocal.y = cursorPos.y;
								}
								if (!(vScrollPos<=cursorPos.y && cursorPos.y<vScrollPos+numOfLinesPerPage)) {
									vScrollPos = cursorPos.y;
									setVScrollBar();
								}							
								if (scrollMode==ScrollMode.Both) {
									setHScrollPos();
									setHScrollBar();
								}
								if (!isAll) return;
							}
							else {
								if  (isAdjacentCharSeparator(findP1, findP2)) {
									makeSelectIndices(false, findP1, findP2);
									//isSelecting = true;
									
									isFound = true;
									if (isAll) {
										Point p1 = new Point(findP1.x, findP1.y);
										Point p2 = new Point(findP2.x, findP2.y);
										listFindPos.add(p1);
										listFindPos.add(p2);
									}
									
									cursorPos.x = i;
									cursorPos.y = j;
									if (isAll) {
										curFindPosLocal.x = cursorPos.x;
										curFindPosLocal.y = cursorPos.y;
									}
									if (!(vScrollPos<=cursorPos.y && cursorPos.y<vScrollPos+numOfLinesPerPage)) {
										vScrollPos = cursorPos.y;
										setVScrollBar();
									}							
									if (scrollMode==ScrollMode.Both) {
										setHScrollPos();
										setHScrollBar();
									}
									if (!isAll) return;
								}
							}
						}
						else if (k>0) k--;
					}
				}
				if (isScopeAll) endY = 0;
				else endY = pointFindStart.y;
				if (j==endY) {
					if (i<endX) {
						if (isScopeAll) j = numOfLines;
						else j = pointFindEnd.y+1;
						k = textToFind.length()-1;
					}
				}
			}	// for (j=curFindPosLocal.y; j<numOfLines; j++) {
		}	// else
		
		
	}
	
	String processSpecialChar(String str) {
		int i;
		ArrayListChar list = new ArrayListChar(30);
		list.setText(str);
		for (i=0; i<list.count; i++) {
			if (list.list[i]=='\\') {
				if (i+1<list.count) {
					if (list.list[i+1]=='n') { 
						list.list[i] = '\n';
						list.delete(i+1, 1);
					}
					else if (list.list[i+1]=='r') {
						list.list[i] = '\r';
						list.delete(i+1, 1);
					}
					else if(list.list[i+1]=='t') {
						list.list[i] = '\t';
						list.delete(i+1, 1);
					}
					
				}
			}
		}
		char[] arr = list.getItems();
		return new String(arr);
	}
	
	/** 항상 p2가 p1보다 크다.
	 * 커서위치는 undo(), undo_replace(), redo(), redo_replace(), replace()에서 
	 * 설정하지 않고 replaceCommon()에서 공통적으로 설정한다. 
	 * 위 함수들이 공통적으로 replaceCommon()을 호출하기 때문이다.
	 * 현재 p1과 p2가 같은 라인에서만 가능하다.*/
	void replaceCommon(boolean isAll, Point p1, Point p2, String textToFind, String textToReplaceWith) {
		//int numOfLines = p2.y - p1.y + 1;
		int numOfLines = this.getNumOfNewLineChar(textToFind)+1;
		
		if (numOfLines==1) {
			CodeString lineText = textArray[p1.y];
			ArrayListCodeChar list = new ArrayListCodeChar(30);
			list.setText(lineText);
			try {
			list.delete(p1.x, p2.x-p1.x+1);
			}catch(Exception e) {
				int a;
				a=0;
				a++;
			}
			list.insert(p1.x, new CodeString(textToReplaceWith, textColor));
			
			CodeString newLineText = new CodeString(list.getItems(), list.count);
			if (scrollMode==ScrollMode.VScroll) {
				if (!isAll) setTextMultiLine(p1.y, newLineText, 1, 1);
				else textArray[p1.y] = newLineText;
				cursorPos.x = p1.x + textToReplaceWith.length();
			}
			else {
				if (!isAll) {
					int numOfNewLines = this.getNumOfNewLineChar(newLineText)+1;
					if (newLineText.charAt(newLineText.length()-1).c=='\n') {
						numOfNewLines--;
					}
					setTextMultiLine(p1.y, newLineText, 1, numOfNewLines);
				}
				else {
					//textArray[p1.y] = newLineText;
					int numOfNewLines = this.getNumOfNewLineChar(newLineText)+1;
					if (newLineText.charAt(newLineText.length()-1).c=='\n') {
						numOfNewLines--;
					}
					setTextMultiLine(p1.y, newLineText, 1, numOfNewLines);
				}
				cursorPos = this.getRelativeCursorPos(p1, textToReplaceWith);
			}
			findP1.x = p1.x;
			findP2.x = cursorPos.x-1;
			if (!isAll) makeSelectIndices(false, findP1, findP2);
		}
		else {
			int i;
			CodeString newLineText = new CodeString("", textColor);
			for (i=0; i<numOfLines; i++) {
				if (i==0) {
					CodeString lineText1 = textArray[p1.y];
					ArrayListCodeChar list1 = new ArrayListCodeChar(30);
					list1.setText(lineText1);
					list1.delete(p1.x, lineText1.length()-1-p1.x+1);
					list1.insert(p1.x, new CodeString(textToReplaceWith, textColor));
					newLineText = newLineText.concate(new CodeString(list1.getItems(), list1.count));
				}
				else if (i==numOfLines-1) {						
					if (textToFind.length()>0 && textToFind.charAt(textToFind.length()-1)=='\n') {
						//	 12\n
						//	 3\n
						//	 456\n
						//	 789 
						//	 에서 textToFind는 2\n이고 textToReplaceWith는 2이다.
						CodeString lineText2 = textArray[p2.y+1];
						ArrayListCodeChar list2 = new ArrayListCodeChar(30);
						list2.setText(lineText2);
						newLineText = newLineText.concate(lineText2);
					}
					else {
						// 위 예에서 textToFind는 2\n3이고 textToReplaceWith는 2이다.
						CodeString lineText2 = textArray[p2.y];
						ArrayListCodeChar list2 = new ArrayListCodeChar(30);
						list2.setText(lineText2);
						list2.delete(0, p2.x+1);
						newLineText = newLineText.concate(new CodeString(list2.getItems(), list2.count));
					}
				}
				else {
				}
			}
			if (scrollMode==ScrollMode.VScroll) {
				if (!isAll) setTextMultiLine(p1.y, newLineText, numOfLines, 1);
				else textArray[p1.y] = newLineText;
				// 커서위치를 찾기 힘들므로 보류한다.
			}
			else {
				int numOfNewLines = this.getNumOfNewLineChar(newLineText)+1;
				if (newLineText.charAt(newLineText.count-1).c=='\n') {
					numOfNewLines--;
				}
				setTextMultiLine(p1.y, newLineText, numOfLines, numOfNewLines);
				
				cursorPos = this.getRelativeCursorPos(p1, newLineText.str);
			}
		}
	}
	
	
	/** replace를 하면 같은 줄의 다음 인덱스 위치들이 바뀌므로 여기에서 증감치를 더해준다.
	 * isForward에 상관없이 p2.x가 p1.x보다 크거나 같다.*/
	void changeSelectP1AndP2(boolean isAll, boolean isForward, Point p1, Point p2, String textToReplaceWith) {
		if (selectP1==null) return;
		if (selectP2==null) return;
		
		ArrayList list = new ArrayList(2);
		list.add(this.selectP1);
		list.add(this.selectP2);			
		
		int incY = p2.y - p1.y + 1;
		int numOfNewLines = getNumOfNewLineChar(textToReplaceWith) + 1;
		if (textToReplaceWith.length()>0) {
			if (textToReplaceWith.charAt(textToReplaceWith.length()-1)=='\n') {
				numOfNewLines--;
			}
		}
		incY = numOfNewLines - incY;
		
		Point temp = this.getRelativeCursorPos(new Point(0,0), 
				textToReplaceWith); // 들어오는 x, 					
		// p2.x는 나가는 x
		
		int incX = temp.x - (p2.x-p1.x+1);
		
		int i;
		for (i=0; i<list.count; i++) {
			Point p = (Point)list.getItem(i);
			if (p.y<p1.y) { // 필요없다.
				
			}					
			else if (p.y>p1.y && p.y<p2.y) { // 불가능
				p.y += incY;
				list.list[i] = p;
			}
			else if (p.y==p2.y) {
				if (p.x>p2.x) {
					p.x += incX;
					p.y += incY;
					list.list[i] = p;
				}
			}
			else if (p.y>p2.y) { // p1, p2가 바뀌는것이므로 y좌표만 생각하면 된다.
				p.y += incY;
				list.list[i] = p;
			}
		}//for (i=0; i<listFindPos.count; i++) {	
		
					
		if (!isAll) makeSelectIndices(true, selectP1, selectP2);
	}
	
	/** textToFind, textToReplaceWith는 '\n'이 없어야 한다.*/
	void changeListFindPos(ArrayList listFindPos, int startIndexInListFindPos, int curLine,
			String textToFind, String textToReplaceWith) {
		int incX = textToReplaceWith.length() - textToFind.length();
		int i;
		for (i=startIndexInListFindPos; i<listFindPos.count; i++) {
			Point p = (Point) listFindPos.getItem(i);
			if (p.y>curLine) break;
			p.x += incX;			
		}
	}
	
	/** replaceAll에서만 호출된다. 
	 * replace를 하면 같은 줄의 다음 인덱스 위치들이 바뀌므로 여기에서 증감치를 더해준다.
	 * isForward에 상관없이 p2.x가 p1.x보다 크거나 같다.
	 * ScrollMode가 Both일 때만 가능하다.
	 * @param p1 : 검색된 시작점
	 * @param p2 : 검색된 끝점*/
	void changeListFindPos(ArrayList listFindPos, boolean isForward, Point p1, Point p2, String textToReplaceWith) {
		int incY = p2.y - p1.y + 1;
		int numOfNewLines = getNumOfNewLineChar(textToReplaceWith) + 1;
		if (textToReplaceWith.length()>0 && 
				textToReplaceWith.charAt(textToReplaceWith.length()-1)=='\n') {
			numOfNewLines--;
		}
		incY = numOfNewLines - incY;
		
		Point temp = this.getRelativeCursorPos(new Point(0,0), 
				textToReplaceWith); // 들어오는 x, 					
		// p2.x는 나가는 x
		
		int incX = temp.x - (p2.x-p1.x+1);
		
		int i;
		int count = listFindPos.count;
		for (i=0; i<count; i++) {
			Point p = (Point)listFindPos.getItem(i);
			if (p.y<p1.y) { // 필요없다.						
			}
			else if (p.y>p1.y && p.y<p2.y) { // 불가능
			}
			else if (p.y==p2.y) {					
				if (p.x>p2.x) { // 같은 줄의 x축으로 다음 점
					// p.x = p.x + temp.x - (p2.x+1);
					p.x += incX;
					p.y += incY;
					listFindPos.list[i] = p;
				}
			}					
			else if (p.y>p2.y) { // 다음 줄
				p.y += incY;
				listFindPos.list[i] = p;
			}
		}//for (i=0; i<listFindPos.count; i++) {
	}
	
	/**커서위치는 undo(), undo_replace(), redo(), redo_replace(), replace()에서 
	설정하지 않고 replaceCommon()에서 공통적으로 설정한다. 
	위 함수들이 공통적으로 replaceCommon()을 호출하기 때문이다.*/
	public void replace(boolean isAll, boolean isForward, boolean isScopeAll, boolean isCaseSensitive, 
			boolean isWholeWord, Point curFindPosLocal, String textToFind, String textToReplaceWith) {
		if (isReadOnly) return;
		
		
		textToFind = processSpecialChar(textToFind);
		textToReplaceWith = processSpecialChar(textToReplaceWith);
		
		if (!isAll) {	// Replace-Find
			
			if (isFound) {
				
				curFindPosLocal.x = findP1.x;
				curFindPosLocal.y = findP1.y;
				
				Point oldFindP1 = new Point(findP1.x,findP1.y);
				Point oldFindP2 = new Point(findP2.x,findP2.y);
				replaceCommon(isAll, findP1, findP2, textToFind, textToReplaceWith);
				changeSelectP1AndP2(false, isForward, oldFindP1, oldFindP2, textToReplaceWith);
				
				
				
				backUpForUndo_replace("replace", isAll, isForward, isScopeAll, isCaseSensitive, 
						isWholeWord, oldFindP1, oldFindP2, findP1, findP2, textToFind, textToReplaceWith);
				// redo 를 무효화한다. redoBuffer를 모두 지워야 한다. 
				// redo 를 무효로 만들지 않으면 undo-redo 시스템의 오류가 발생한다.
				redoBuffer.reset();
				
				
				find(false, isForward,  isScopeAll,  isCaseSensitive, 
						isWholeWord,  curFindPosLocal, textToFind, true);
				
				//커서위치는 undo(), undo_replace(), redo(), redo_replace(), replace()에서 
				// 설정하지 않고 replaceCommon()에서 공통적으로 설정한다. 
				// 위 함수들이 공통적으로 replaceCommon()을 호출하기 때문이다.
			}
			else {
				CommonGUI.loggingForMessageBox.setHides(false);
				CommonGUI.loggingForMessageBox.setText(true, "Touch the Find next button ahead and then replace/find.", false);
			}
		}// if (!isAll)
		else {	// ReplaceAll은 scrollMode가 Both일 때만 동작한다. 검색시작위치는 0,0에서 시작되므로 listFindPos은 x, y순으로 정렬된 상태이다.
			//backUpForUndo_replace("replaceAll", isAll, isForward, isScopeAll, isCaseSensitive, 
			//		isWholeWord, curFindPosLocal, textToFind, textToReplaceWith);
			
			
			this.listFindPos = new ArrayList(50);
			// 나중에 undo를 하기위한 replace한 위치 리스트, listFindPos와 비교한다.
			ArrayList listReplacePos = new ArrayList(50);
			
			Point backupCurFindPosLocal = new Point(curFindPosLocal.x, curFindPosLocal.y);
			if (!isScopeAll) { // 선택줄에서 검색시
				curFindPosLocal = new Point(selectP1.x, selectP1.y);
			}
			else {
				curFindPosLocal = new Point(0,0);
			}
			
			find(true, isForward,  isScopeAll,  isCaseSensitive, 
				isWholeWord,  curFindPosLocal, textToFind, true);
			ArrayList list = this.listFindPos;
			// 아래 for문에서 this.listFindPos가 바뀌므로 백업해두었다가
			// undoBuffer에 넣는다.
			ArrayList listBackupForFindPos = getClone(this.listFindPos);
			int count = list.count;
						
			int i;
			for (i=0; i<count; i+=2) {
				Point p1 = (Point)list.list[i];
				Point p2 = (Point)list.list[i+1];
				replaceCommon(isAll, p1, p2, textToFind, textToReplaceWith);
				
				listReplacePos.add(new Point(findP1.x, p1.y));
				listReplacePos.add(new Point(findP2.x, p2.y));
								
				if (!isScopeAll) {
					changeSelectP1AndP2(true, isForward, p1, p2, textToReplaceWith);
				}
				changeListFindPos(list, i+2, p1.y, textToFind, textToReplaceWith);
			}
			if (!isScopeAll) { // 선택줄에서 검색시
				makeSelectIndices(true, selectP1, selectP2);
				
				Point t1 = selectP1, t2 = selectP2;
				if (this.selectP1.y>this.selectP2.y) { // swapping
					t1 = selectP2;
					t2 = selectP1;
				}
				else if (this.selectP1.y==this.selectP2.y) {
					if (this.selectP1.x>this.selectP2.x) {
						// swapping, y가 같을땐 x를 비교해서 작은쪽이 p1이 된다.
						t1 = selectP2;
						t2 = selectP1;
					}
				}
				selectP1 = t1;
				selectP2 = t2;
			}
			
			if (!isScopeAll) { // 선택줄에서 검색시
				curFindPosLocal = new Point(selectP1.x, selectP1.y);
			}
			else {
				curFindPosLocal = new Point(0,0);
			}
			
			backUpForUndo_replace("replaceAll", isAll, isForward, isScopeAll, isCaseSensitive, 
					isWholeWord, curFindPosLocal, textToFind, textToReplaceWith, listBackupForFindPos, listReplacePos);
			// redo 를 무효화한다. redoBuffer를 모두 지워야 한다. 
			// redo 를 무효로 만들지 않으면 undo-redo 시스템의 오류가 발생한다.
			redoBuffer.reset();
			
					
			setVScrollPos();
			setVScrollBar();
			if (scrollMode==ScrollMode.Both) {
				setHScrollPos();
				setHScrollBar();	
			}
			
			if (isScopeAll) { // 모든 영역에서 검색시
				isSelecting = false;
				isFound = false;
			}
			else {// 선택줄에서 검색시
				isFound = false;
			}
		}
	}
	
	/** Point[]인 ArrayList를 복제해서 리턴한다.*/
	ArrayList getClone(ArrayList listFindPos) {
		int i;
		ArrayList r = new ArrayList(listFindPos.count);
		for (i=0; i<listFindPos.count; i++) {
			Point p = (Point) listFindPos.getItem(i);
			r.add(new Point(p.x, p.y));
		}
		return r;
	}
	
	
	/** 
	 * @param isAll : 여러개를 찾을경우 true, 1개를 찾을 경우 false
	 * @param isForward
	 * @param isScopeAll : 선택라인에서 찾을 경우 false, 모든 영역에서 찾을 경우 true
	 * @param isCaseSensitive
	 * @param isWholeWord
	 * @param curFindPosLocal
	 * @param textToFind
	 * @param isCallerReplace : caller가 OnTouchEvent의 find 명령처리할 때나 
		 * 	replace()이면 true, 아니면 false, 
		 * 	사용자가 find, replace-next나 replaceAll 버튼을 클릭해서
			검색이 실패할 때만 메시지가 나오고
			undo, redo시에는 "the text not found" 메시지가 나오지 않도록 한다.
	 */
	public void find(boolean isAll, boolean isForward, boolean isScopeAll, boolean isCaseSensitive, 
		boolean isWholeWord, Point curFindPosLocal, String textToFind, boolean isCallerReplace) {
		textToFind = processSpecialChar(textToFind);
		//textToReplaceWith = processSpecialChar(textToReplaceWith);
		
		try {
		//isFinding = true;
		isFound = false;
		if (isScopeAll==false) {	// 선택영역내에서
			/*if (isSelecting==false) {	// 선택되어 있어야 한다.
				
				return;
			}
			else {*/
				if (selectP1.y == selectP2.y) {
					if (selectP1.x < selectP2.x) {
						pointFindStart.x = selectP1.x;
						pointFindStart.y = selectP1.y;
						pointFindEnd.x = selectP2.x;
						pointFindEnd.y = selectP2.y;
					}
					else {
						pointFindStart.x = selectP2.x;
						pointFindStart.y = selectP2.y;
						pointFindEnd.x = selectP1.x;
						pointFindEnd.y = selectP1.y;
					}
				}
				else {
					if (selectP1.y < selectP2.y) {
						pointFindStart.x = selectP1.x;
						pointFindStart.y = selectP1.y;
						pointFindEnd.x = selectP2.x;
						pointFindEnd.y = selectP2.y;
					}
					else {
						pointFindStart.x = selectP2.x;
						pointFindStart.y = selectP2.y;
						pointFindEnd.x = selectP1.x;
						pointFindEnd.y = selectP1.y;
					}
				}
			//}
		}
		else {//isScopeAll==true
			pointFindStart.x = 0;
			pointFindStart.y = 0;
			if (textArray[numOfLines-1].length()>0)
				pointFindEnd.x = textArray[numOfLines-1].length()-1;
			else 
				pointFindEnd.x = 0;
			pointFindEnd.y = numOfLines-1;
			
			// scope가 all이므로 시작 커서 위치를 원점으로 한다.
			//curFindPosLocal.y = 0;
			//curFindPosLocal.x = 0;
		}
		
		int startLine = curFindPosLocal.y;
		
		findCommon(isAll, isForward, isScopeAll, isCaseSensitive, 
		 	isWholeWord, curFindPosLocal, textToFind);
		
		
		 	
		if (isFound==false) {
			// 사용자가 find, replace-next나 replaceAll 버튼을 클릭해서
			// 검색이 실패할 때만 메시지가 나오고
			// undo, redo시에는 "the text not found" 메시지가 나오지 않도록 한다.
			if (isCallerReplace) {
				CommonGUI.loggingForMessageBox.setHides(false);
				CommonGUI.loggingForMessageBox.setText(true, "The text not found.", false);
			}
			this.findP1.x = -1;
			this.findP1.y = -1;
			this.findP2.x = -1;
			this.findP2.y = -1;
		}
		else {
			//listFindPos = refine(this.listFindPos);
			refineListFindPos();
		}
		
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
		
	}
	
	/** find()에서 중복된 좌표를 제거한다.*/
	void refineListFindPos() {
		int i;
		Point first = (Point) this.listFindPos.getItem(0);
		for (i=2; i<this.listFindPos.count; i+=2) {
			Point p = (Point) this.listFindPos.getItem(i);
			if (first.x==p.x && first.y==p.y) {
				listFindPos.count = i;
				break;
			}
		}
	}
	
		
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		try {
			if (sender==this) {
				callTouchListener(this, e);
				editText_Listener(sender, e);
				
				
			}
			else {
				if (sender instanceof Button) {
					int i;
					Button button = (Button)sender;
					
					for (i=0; i<namesOfButtonsOfToolbar.length; i++) {
						//if (button.name.equals(namesOfButtonsOfToolbar[i])) {
						if (button.iName==toolbar.buttons[i].iName) {
							toolbar_Listener(button, button.name);
							return;
						}
					}
					
					//"10", "14", "18", "22", "26", "30", "기타"
					for (i=0; i<Edit.Menu_FontSize.length; i++) {	// 메뉴버튼
						//if (button.name.equals(Menu_FontSize[i])) {
						if (button.iName==Edit.menuFontSize.buttons[i].iName) {	
							fontSizeMenu_Listener(button, Edit.Menu_FontSize[i]);
							return;
						} 
					}
					
					for (i=0; i<Edit.Menu_Function.length; i++) {	// 메뉴버튼
						//if (button.name.equals(Menu_FontSize[i])) {
						if (button.iName==Edit.menuFunction.buttons[i].iName) {	
							functionMenu_Listener(button, Edit.Menu_Function[i]);
							return;
						} 
					}
				}
				else if (sender instanceof VScrollBarLogical) {
					VScrollBarLogical vScrollBar = (VScrollBarLogical)sender;
					this.vScrollPos = vScrollBar.vScrollPos;
					setVScrollBar();
					// 수직 스크롤바 터치시 수평스크롤 부분도 바뀌므로 호출해야 한다.
					if (scrollMode==ScrollMode.Both) {
						setHScrollBar();
					}
				}
				else if (sender instanceof HScrollBar) {
					HScrollBar hScrollBar = (HScrollBar)sender;
					// 수평 스크롤바 터치시 수직스크롤 부분은 바뀌지 않으므로 호출하지 않는다.
					//setVScrollBar();
					this.widthOfhScrollPos = hScrollBar.widthOfScrollPos;
					setHScrollBar();
					
				}
				else if (sender instanceof FontSizeDialog) {
					fontSizeMenu_Listener(sender, Edit.fontSizeDialog.curText);
				}
				else if (sender instanceof FindReplaceDialog) {
					FindReplaceDialog dialog = (FindReplaceDialog)sender;
					if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
						CommonGUI.keyboard.setHides(true);
					}
					if (dialog.recentCommand.equals("Find")) {
						boolean isForward = dialog.buttonDirection.text.equals("Forward") ? true : false;
						boolean isScopeAll = dialog.buttonScope.text.equals("All") ? true : false;
						boolean isCaseSensitive = dialog.buttonCaseSensitive.isSelected;
						boolean isWholeWord = dialog.buttonWholeWord.isSelected;
						curFindPos.x = cursorPos.x; 
						curFindPos.y = cursorPos.y; 
						find(false, isForward, isScopeAll, isCaseSensitive, isWholeWord, 
							curFindPos, dialog.editTextFind.getText().str, true);
						
					}
					else if (dialog.recentCommand.equals("Replace-Find")) {
						boolean isForward = dialog.buttonDirection.text.equals("Forward") ? true : false;
						boolean isScopeAll = dialog.buttonScope.text.equals("All") ? true : false;
						boolean isCaseSensitive = dialog.buttonCaseSensitive.isSelected;
						boolean isWholeWord = dialog.buttonWholeWord.isSelected;
						curFindPos.x = cursorPos.x; 
						curFindPos.y = cursorPos.y; 
						replace(false, isForward, isScopeAll, isCaseSensitive, isWholeWord, curFindPos, 
							dialog.editTextFind.getText().str, dialog.editTextReplaceWith.getText().str);
					}
					else if (dialog.recentCommand.equals("ReplaceAll")) {
						// ReplaceAll은 scrollMode가 Both일 때만 동작한다.
						if (scrollMode==ScrollMode.VScroll) {
							CommonGUI.loggingForMessageBox.setHides(false);
							CommonGUI.loggingForMessageBox.setText(true, "ReplaceAll operates only in Both scrollMode.", false);
							return;
						}
						
						boolean isForward = dialog.buttonDirection.text.equals("Forward") ? true : false;
						boolean isScopeAll = dialog.buttonScope.text.equals("All") ? true : false;
						boolean isCaseSensitive = dialog.buttonCaseSensitive.isSelected;
						boolean isWholeWord = dialog.buttonWholeWord.isSelected;
						if (isScopeAll) {
							cursorPos.x = 0;
							cursorPos.y = 0;
						}
						else {
							if (isSelecting==false) return;
							if (selectP1.y == selectP2.y) {
								if (selectP1.x < selectP2.x) {
									cursorPos.x = selectP1.x;
									cursorPos.y = selectP1.y;
								}
								else {
									cursorPos.x = selectP2.x;
									cursorPos.y = selectP2.y;
								}
							}
							else {
								if (selectP1.y < selectP2.y) {
									cursorPos.x = selectP1.x;
									cursorPos.y = selectP1.y;
								}
								else {
									cursorPos.x = selectP2.x;
									cursorPos.y = selectP2.y;
								}
							}
						}
						curFindPos.x = cursorPos.x; 
						curFindPos.y = cursorPos.y; 
						replace(true, isForward, isScopeAll, isCaseSensitive, isWholeWord, curFindPos, 
							dialog.editTextFind.getText().str, dialog.editTextReplaceWith.getText().str);
					}
					else if (dialog.recentCommand.equals("Close")) {
						//curFindPos.x = 0;
						//curFindPos.y = 0;
					}
				}
			
				else if (sender instanceof IntegrationKeyboard) {
					IntegrationKeyboard keyboard = (IntegrationKeyboard)sender;
					
					// 키보드 가속기는 isReadOnly가 true이더라도 실행이 된다.
					if (IntegrationKeyboard.isCtrlPressed) {
						if (keyboard.key.equals("Z")) {
							undo();
						}
						else if (keyboard.key.equals("Y")) {
							redo();
						}
						else if (keyboard.key.equals("F")) {
							openFindReplaceDialog();
						}
						else if (keyboard.key.equals("C")) {
							copy();
						}
						else if (keyboard.key.equals("X")) {
							cut();
						}
						else if (keyboard.key.equals("V")) {
							paste();
						}
						else if (keyboard.key.equals("A")) {
							selectAll();
						}
						return;
					}//키보드 가속기
					
					
					if (isReadOnly) return;
					//if (CommonGUI_SettingsDialog.settings.isKeyboardEnable) return;
					
					
					if (listener!=null) {
						if (keyboard.key.equals(IntegrationKeyboard.Enter)) {
							listener.onTouchEvent(this, null);
							return;
						}
					}
					
					keyboardMode = keyboard.mode;
					hangulMode = Hangul.mode;
										
					if (keyboard.mode!=Mode.Hangul) {
						try {
							addChar(keyboard.key/*, false*/);
						}catch(Exception e1) {
							//Log.e("TouchEvent addChar", e1.toString());
							e1.printStackTrace();
							CompilerHelper.printStackTrace(textViewLogBird, e1);
						}
					}
					else {//if (keyboard.mode==Mode.Hangul) {
						if (Hangul.isBkSpPressed) {
							replaceChar(keyboard.key);
							return;
						}						
						
						if (Hangul.mode==Hangul.Mode.None) {
							// space, delete, enter 키는 Hangul.mode가 None이다.
							// 그리고 isNextToCursor는 true로 설정되어 커서다음위치에 
							// key가 추가된다.
							/*try {
							if (Hangul.isNextToCursor) {
								addChar(keyboard.key, true);
							}
							else {
								addChar(keyboard.key, false);
							}
							}catch(Exception e1) {
								//Log.e("addNone ", addNone.toString());
								e1.printStackTrace();
								CompilerHelper.printStackTrace(textViewLogBird, e1);
							}*/
							addChar(keyboard.key/*, false*/);
						}						
						else if (Hangul.mode==Hangul.Mode.초성 ||
								Hangul.mode==Hangul.Mode.중성) {
							/*try {
							if (Hangul.isNextToCursor) {
								addChar(keyboard.key, true);
							}
							else {
								addChar(keyboard.key, false);
							}
							}catch(Exception e1) {
								//Log.e("add초성중성", add초성중성.toString());
								e1.printStackTrace();
								CompilerHelper.printStackTrace(textViewLogBird, e1);
							}*/
							addChar(keyboard.key/*, false*/);
						}
						else {
							try {
							replaceChar(keyboard.key);
							}catch(Exception e1) {
								//Log.e("replaceChar", rep.toString());
								e1.printStackTrace();
								CompilerHelper.printStackTrace(textViewLogBird, e1);
							}
						}
						
					} // keyboard.mode==Mode.Hangul
					//Hangul.isNextToCursor = false;
				} // if (className.equals(IntegrationKeyboard))				
			}
		}catch (Exception e1) {
			e1.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e1);
		}
		finally {
			if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering)  this.drawToImage(mCanvas);
		}
		
	}
	
	public String getCurChar() {
		if (cursorPos.x<textArray[cursorPos.y].length()) {
			return textArray[cursorPos.y].substring(cursorPos.x, cursorPos.x+1).toString();
		}
		return null;
	}
	
	public void getCursorPos(MotionEvent e) {
		if (cursorPos.y<0) cursorPos.y=0;
		if (cursorPos.y>=numOfLines) cursorPos.y=numOfLines-1;
		
		paint.setTextSize(fontSize);
		
		if (scrollMode==ScrollMode.VScroll) {
			cursorPos.y = (int)((e.y - bounds.y) / /*fontSize*/lineHeight) + vScrollPos;
			if (cursorPos.y>=numOfLines) cursorPos.y = numOfLines-1;
			if (cursorPos.y<0) cursorPos.y = 0; 
			int i;
			float lineWidth = bounds.x + gapX;
			
			int lineLen = textArray[cursorPos.y].length();
			String charA=null;
			for (i=0; i<lineLen; i++) {
				charA = textArray[cursorPos.y].substring(i, i+1).toString();
				if (charA.equals(NewLineChar)) break;
				lineWidth += paint.measureText(charA);
				if (e.x<=lineWidth) {				
					break;
				}
			}
			String lastChar = charA;
			if (lineLen==0) cursorPos.x = 0;
			else {	// lineLen>0
				if (i < lineLen) {
					if (i==0) cursorPos.x = 0;
					else {	// 0<i && i<lineLen		문자열 클릭
						if (lastChar.equals(NewLineChar)) {
							// 커서는 \n을 가리킨다.
							cursorPos.x = i; 
							/*// 커서는 \r을 가리킨다.
							if (textArray[cursorPos.y].substring(i-1,i).equals("\r")) {
								cursorPos.x = i-1;
							}*/
						}
						else {
							cursorPos.x = i;
						}
					}
				}
				else {	// i==lineLen 문자열을 넘어서 클릭
					if (lastChar.equals(NewLineChar)) {
						// 커서는 \n을 가리킨다.
						cursorPos.x = (lineLen-1);
						/*// 커서는 \r을 가리킨다.
						if (textArray[cursorPos.y].substring(lineLen-2,lineLen-1).equals("\r")) {
							cursorPos.x = lineLen-2;
						}*/
					}					
					else cursorPos.x = lineLen;
				}
			}
		}
		else if (scrollMode==ScrollMode.Both) {
		
			cursorPos.y = (int)((e.y - bounds.y) / lineHeight) + vScrollPos;
			if (cursorPos.y>=numOfLines) cursorPos.y = numOfLines-1;
			if (cursorPos.y<0) cursorPos.y = 0; 
			int i;
			float eventXRelative = e.x-(bounds.x+gapX); 
			float lineWidth = 0;
			
			int lineLen = textArray[cursorPos.y].length();
			String charA=null;
			for (i=0; i<lineLen; i++) {
				charA = textArray[cursorPos.y].substring(i, i+1).toString();
				if (charA.equals(NewLineChar)) {
					break;
				}
				lineWidth += paint.measureText(charA);
				if (widthOfhScrollPos+eventXRelative <= lineWidth) {				
					break;
				}
			}
			String lastChar = charA;
			if (lineLen==0) {
				cursorPos.x = 0;
			}
			else {	// lineLen>0
				if (i < lineLen) {
					//if (i==0) cursorPos.x = 0;
					//else {	// 0<i && i<lineLen		문자열 클릭
						if (lastChar.equals(NewLineChar)) {
							// 커서는 \n을 가리킨다.
							cursorPos.x = i;
							/*// 커서는 \r을 가리킨다.
							if (textArray[cursorPos.y].substring(i-1,i).equals("\r")) {
								cursorPos.x = i-1;
							}*/
						}						
						else cursorPos.x = i;
					//}
				}
				else {	// i==lineLen 문자열을 넘어서 클릭
					/*
					 * //if (lastChar.equals(NewLineChar)) {
						//cursorPos.x = (lineLen-1);
						// 커서는 \n을 가리킨다.
						//}					
						//else
							cursorPos.x = lineLen;
							// 커서는 마지막문자의 뒤를 가리킨다.*/
					
					if (lastChar.equals(NewLineChar)) {
						// 커서는 \n을 가리킨다.
						cursorPos.x = (lineLen-1);
						/*// 커서는 \r을 가리킨다.
						if (textArray[cursorPos.y].substring(lineLen-2,lineLen-1).equals("\r")) {
							cursorPos.x = lineLen-2;
						}*/
					}					
					else cursorPos.x = lineLen;
				}
			}
		}//if (scrollMode==ScrollMode.Both)
	}
	
	public boolean endsWithNewLineChar(String lineText) {
		int lineLen = lineText.length();
		if (lineLen > 0) {
			if (lineText.substring(lineLen-1, lineLen).equals(NewLineChar)) {
				return true;
			}
			return false;
		}
		return false;		
	}
	
	/** \r\n을 제거한다. \r은 있으면 지운다.*/
	CodeString deleteNewLineChar(CodeString lineText) {
		int lineLen = lineText.length();
		CodeString newLine;
		if (lineLen > 0) {
			if (lineText.substring(lineLen-1, lineLen).toString().equals(NewLineChar)) {
				if (lineLen > 1)
					newLine = lineText.substring(0, lineLen-1);
				else 
					newLine = new CodeString("", textColor);
			}
			else {
				newLine = lineText;
			}
		}
		else {
			newLine = new CodeString("", textColor);
		}
		lineLen = newLine.length();
		if (lineLen > 0) {
			if (newLine.substring(lineLen-1, lineLen).toString().equals("\r")) {
				if (lineLen > 1)
					return newLine.substring(0, lineLen-1);
				else 
					return new CodeString("", textColor);
			}
			else {
				return newLine;
			}
		}
		else {
			return new CodeString("", textColor);
		}
	}
	
	/** \r\n을 제거한다. \r은 있으면 지운다.*/
	/*String deleteNewLineChar(String lineText) {
		int lineLen = lineText.length();
		String newLine;
		if (lineLen > 0) {
			if (lineText.substring(lineLen-1, lineLen).equals(NewLineChar)) {
				if (lineLen > 1)
					newLine = lineText.substring(0, lineLen-1);
				else 
					newLine = new String("");
			}
			else {
				newLine = lineText;
			}
		}
		else {
			newLine = "";
		}
		lineLen = newLine.length();
		if (lineLen > 0) {
			if (newLine.substring(lineLen-1, lineLen).equals("\r")) {
				if (lineLen > 1)
					return newLine.substring(0, lineLen-1);
				else 
					return new String("");
			}
			else {
				return newLine;
			}
		}
		else {
			return "";
		}
	}*/
	
	/*CodeString proceedTabChar(CodeString lineText) {
		int i;
		ArrayListCodeChar list = new ArrayListCodeChar(lineText.length()+20);
		list.setText(lineText);
		for (i=0; i<list.count; i++) {
			if (list.list[i].c=='\t') {
				list.delete(i, 1);
				list.insert(i, "    ");
			}
		}
		return new String(list.getItems());
	}*/
	
	/*String proceedTabChar(String lineText) {
		int i;
		ArrayListChar list = new ArrayListChar(lineText.length()+20);
		list.setText(lineText);
		for (i=0; i<list.count; i++) {
			if (list.list[i]=='\t') {
				list.delete(i, 1);
				list.insert(i, "    ");
			}
		}
		return new String(list.getItems());
	}*/
	
	PartOfStr getStringHScroll(CodeString str) {
		if (str==null) return null;
		try{
		int start=-1, end=-1;
		float w=0;
		part1OfChar=0;
		part2OfChar=0;
		w= widthOfhScrollPos;
		int i;
		float lineWidth=0;
		
		paint.setTextSize(fontSize);
		
		for (i=0; i<str.length(); i++) {
			CodeString charA = str.substring(i, i+1);
			float wOfCharA = paint.measureText(charA.toString()); 
			lineWidth += wOfCharA;
			if (lineWidth>w) {
				start = i;
				part2OfChar = lineWidth-w;
				part1OfChar = wOfCharA-part2OfChar;
				break;
			}
		}
		int startIndex;
		if (part1OfChar==0) {
			startIndex = start;
			lineWidth = 0;
			part2OfChar = 0;
		}
		else {
			startIndex = start+1;
			lineWidth = part2OfChar;
			end = start;
		}
		if (start!=-1) {
			for (i=startIndex; i<str.length(); i++) {
				CodeString charA = str.substring(i, i+1);
				lineWidth += paint.measureText(charA.toString());
				if (lineWidth<=rationalBoundsWidth) {
					end = i;
				}				
				else {
					end = i;
					break;
				}
			}
		}
		if (start==-1 || end==-1) {
			return new PartOfStr(new CodeString("", textColor),start,end);
		}
		else {
			return new PartOfStr(str.substring(start, end+1), start, end);
		}
		}catch(Exception e) {
			//Log.e("getStringHScroll", e.toString());
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return null;
		}
			
	}
	
	class PartOfStr {
		CodeString str;
		int start=-1;
		int end=-1;
		
		PartOfStr(CodeString str, int start, int end) {
			this.str = str;
			this.start = start;
			this.end = end;
		}
	}
	
	CodeString getStringHScrollAndCursorVisuablity(CodeString str) {
		try{
		int start=-1, end=-1;
		if (str.toString().equals("") || str.toString().equals("\n")) {			
			if (widthOfhScrollPos>=fontSize) {
				isCursorSeen = false;
				return new CodeString("", textColor);
			}
			else {
				start = 0;
				end = 0;				
			}
		}
		else {		
			PartOfStr result = getStringHScroll(str);
			start = result.start;
			end = result.end;
		}
		
				
		if (start==-1 || end==-1) {
			isCursorSeen = false;
			return new CodeString("", textColor);
		}
		else {
			int endIndex;
			if (keyboard.mode==Mode.Hangul && Hangul.mode!=Hangul.Mode.None) {
				endIndex = end;
			}
			else {
				endIndex = end+1;
			}
			//endIndex = end+1;
			if (start<=cursorPos.x && cursorPos.x<=endIndex) {
				isCursorSeen = true;
				valueOfCursorRelativeToHScroll = 0;
				return str.substring(start, cursorPos.x);
			}
			else {
				if (cursorPos.x<start) valueOfCursorRelativeToHScroll = -1;
				else valueOfCursorRelativeToHScroll = 1;
				isCursorSeen = false;
				return new CodeString("", textColor);
			}
		}
		}catch(Exception e) {
			//Log.e("getStringHScrollAndCursorVisuablity", e.toString());
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return null;
		}
	}
	
	/** CustomView의 비트맵에 컨트롤이 갖는 bitmapForRendering 비트맵(트리플 버퍼링)과 이것과 분리되어 있는 툴바 비트맵을 그린다.
     * @param canvas : CustomView의 mCanvas(내부에 비트맵을 가지므로 더블버퍼링이다.)*/
	public synchronized void draw(Canvas canvas) {
		if (this instanceof TextView) {
			int a;
			a=0;
			a++;
		}
		if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
    			isTripleBuffering) {
			Rect src = (new Rectangle(0,0,bounds.width,bounds.height)).toRect();
			
			RectF dst = this.bounds.toRectF();
			canvas.drawBitmap(this.bitmapForRendering, src, dst, paint);
			
			if (hasToolbarAndMenuFontSize) {
				toolbar.incxForBitmapRendering = (int) this.bounds.x;
				toolbar.incyForBitmapRendering = (int) this.bounds.y;
				toolbar.draw(canvas);
			}
		}
		else {
			drawCommon(canvas);
			
			if (hasToolbarAndMenuFontSize) {
				toolbar.draw(canvas);
			}
		}
	}
	
	/**isTripleBuffering이 true이면 mCanvas 안에 있는 bitmapForRendering 비트맵에 그린다. 
     * 비트멥은 원점부터 시작하므로 bounds에서 bounds.x와 bounds.y를 빼서 그려야 비트맵에 그릴 수 있다.<br>
     * isTripleBuffering이 false이면 현재 bounds에 그린다.
     *  @param canvas : isTripleBuffering이 true이면 컨트롤이 갖고 있는 mCanvas이고<br>
     *  CustomView의 mCanvas(내부에 비트맵을 가지므로 더블버퍼링이다.)
     *  */
	void drawCommon(Canvas canvas) {
		finishingDrawingOnce = true;
		
		synchronized(this) {
			try{
				try {
				if (CommonGUI.textViewLogBird!=null && this.iName==CommonGUI.textViewLogBird.iName) {
					int a;
					a=0;
					a++;
				}
				}catch(Exception e) {
					int a;
					a=0;
					a++;
					e.printStackTrace();
				}
				
				paint.setColor(backColor);
				paint.setTextSize(fontSize);
				
				canvas.save();
						
				//RectF rectBounds = bounds.toRectF();
				RectF rectBounds;
				if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
		    			isTripleBuffering) {
					rectBounds = RectangleF.toRectF(bounds, this.bounds.x , this.bounds.y);
				}
				else {
					rectBounds = bounds.toRectF();
				}
				canvas.drawRect(rectBounds, paint);
				paintOfBorder.setColor(ColorEx.reverseColor(backColor));
		    	canvas.drawRect(rectBounds, paintOfBorder);   	
		    	
		    	
		    	Rect clipRect = new Rect();
		    	clipRect.left = (int) (bounds.x+1+gapX); 
		    	clipRect.top = (int)(bounds.y+1);
		    	
		    	if (scrollMode==ScrollMode.VScroll) {
		    		clipRect.right = (int) (bounds.right()-vScrollBarWidth-gapX);
		    		clipRect.bottom = (int) (bounds.bottom());
		    	}
		    	else {
		    		if (isSingleLine) {
		    			clipRect.right = (int) (bounds.right()-gapX);
		    			clipRect.bottom = (int) (bounds.bottom());
		    		}
		    		else {
		    			clipRect.right = (int) (bounds.right()-vScrollBarWidth-gapX);
		    			clipRect.bottom = (int) (bounds.bottom()-hScrollBarHeight);
		    		}
		    	}
		    	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
		    			isTripleBuffering) {
		    		clipRect = Rectangle.toRect(clipRect, (int)this.bounds.x, (int)this.bounds.y);
		    	}
		    	if (!canvas.clipRect(clipRect, Region.Op.REPLACE)) {
		    		Log.e("editText onDraw","No clipping");
		    	}
		    	
		    	try {
					int i, j;
					float x, y, w;
					
					if (isSelecting) {					
						Point[] intersectedSelect = getIntersectWithSelect(true);
						if (intersectedSelect!=null && intersectedSelect.length!=0) {
							for (i=0; i<intersectedSelect.length; i+=2) {
								j = intersectedSelect[i].y;
								y = bounds.y + (intersectedSelect[i].y-vScrollPos) * lineHeight + descent;
								if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
						    			isTripleBuffering)  y -= this.bounds.y;
								
								//float w;
								float x2;
								float selectHeight = lineHeight;
								RectangleF dst = null;
								
								if (selectLenY==1 && intersectedSelect[i+1].y==Select_FirstLine) {
									
									CodeString cstr = textArray[j].substring(0, intersectedSelect[0].x);
									if (cstr!=null) {
										String str = cstr.str;
										x = bounds.x + gapX + paint.measureText(str);
										
										String str2;
										if (intersectedSelect[1].x+1<=textArray[j].length())
											str2 = textArray[j].substring(0, intersectedSelect[1].x+1).toString();
										else 
											str2 = textArray[j].substring(0, intersectedSelect[1].x).toString();
										x2 = bounds.x + gapX + paint.measureText(str2);
										if (scrollMode==ScrollMode.Both) {
											x -= this.widthOfhScrollPos;
											x2 -= this.widthOfhScrollPos;								
										}
										w = x2 - x;
										if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
								    			isTripleBuffering)  x -= this.bounds.x;
										dst = new RectangleF(x, y, w, selectHeight);
									}
								}
								else if (selectLenY>1 && intersectedSelect[i+1].y==Select_FirstLine) {
									
									String str = textArray[j].substring(0, intersectedSelect[0].x).toString();
									if (str!=null) {
										x = bounds.x + gapX + paint.measureText(str);
										if (scrollMode==ScrollMode.Both) {
											x -= this.widthOfhScrollPos;
											if (x<bounds.x+gapX) x = 0;
										}
										
										w = (bounds.x + bounds.width - vScrollBarWidth) - x;
										if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
								    			isTripleBuffering)  x -= this.bounds.x;
										dst = new RectangleF(x, y, w, selectHeight);
									}
								}
								else if (selectLenY>1 && intersectedSelect[i+1].y==Select_MiddleLine) {
									x = bounds.x;
									w = (bounds.x + bounds.width - vScrollBarWidth) - x;
									if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
							    			isTripleBuffering)  x -= this.bounds.x;
									dst = new RectangleF(x, y, w, selectHeight);
								}
								else if (selectLenY>1 && intersectedSelect[i+1].y==Select_LastLine) {
									String str;
									if (intersectedSelect[intersectedSelect.length-1].x+1<=textArray[j].length())
										str = textArray[j].substring(
												0, intersectedSelect[intersectedSelect.length-1].x+1).toString();
									else 
										str = textArray[j].substring(
												0, intersectedSelect[intersectedSelect.length-1].x).toString();
									if (str!=null) {
										x = bounds.x;
										w = (bounds.x + gapX + paint.measureText(str)) - x;
										if (scrollMode==ScrollMode.Both) {
											w -= this.widthOfhScrollPos;
										}
										if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
								    			isTripleBuffering)  x -= this.bounds.x;
										dst = new RectangleF(x, y, w, selectHeight);
									}
								}
								if (dst!=null) {								
									paint.setColor(Edit.selectColor);
									//paint.setAlpha(80);								 
									canvas.drawRect(dst.toRectF(), paint);
									//paint.setAlpha(255);
								}
								dst = null;
							}
						}
					}
					
					if (isFound) {
						Point[] intersectedSelect = getIntersectWithSelect(false);
						if (intersectedSelect!=null && intersectedSelect.length!=0) {
							for (i=0; i<intersectedSelect.length; i+=2) {
								j = intersectedSelect[i].y;
								y = bounds.y + (intersectedSelect[i].y-vScrollPos) * lineHeight + descent;
								if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
						    			isTripleBuffering)  y -= this.bounds.y;
								
								//float w;
								float x2;
								float findHeight = lineHeight;
								RectangleF dst = null;
								
								if (findLenY==1 && intersectedSelect[i+1].y==Select_FirstLine) {
									String str = textArray[j].substring(0, intersectedSelect[0].x).toString();
									if (str!=null) {
										x = bounds.x + gapX + paint.measureText(str);
										String str2;
										if (intersectedSelect[1].x+1<=textArray[j].length())
											str2 = textArray[j].substring(0, intersectedSelect[1].x+1).toString();
										else 
											str2 = textArray[j].substring(0, intersectedSelect[1].x).toString();
										x2 = bounds.x + gapX + paint.measureText(str2);
										if (scrollMode==ScrollMode.Both) {
											x -= this.widthOfhScrollPos;
											x2 -= this.widthOfhScrollPos;									
										}
										w = x2 - x;
										if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
								    			isTripleBuffering)  x -= this.bounds.x;
										dst = new RectangleF(x, y, w, findHeight);
									}
								}
								else if (findLenY>1 && intersectedSelect[i+1].y==Select_FirstLine) {
									String str = textArray[j].substring(0, intersectedSelect[0].x).toString();
									if (str!=null) {
										x = bounds.x + gapX + paint.measureText(str);
										if (scrollMode==ScrollMode.Both) {
											x -= this.widthOfhScrollPos;
											if (x<bounds.x+gapX) x = 0;
										}
										w = (bounds.x + bounds.width - vScrollBarWidth) - x;
										if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
								    			isTripleBuffering)  x -= this.bounds.x;
										dst = new RectangleF(x, y, w, findHeight);
									}
								}
								else if (findLenY>1 && intersectedSelect[i+1].y==Select_MiddleLine) {
									x = bounds.x;
									w = (bounds.x + bounds.width - vScrollBarWidth) - x;
									if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
							    			isTripleBuffering)  x -= this.bounds.x;
									dst = new RectangleF(x, y, w, findHeight);
								}
								else if (findLenY>1 && intersectedSelect[i+1].y==Select_LastLine) {
									String str;
									if (intersectedSelect[intersectedSelect.length-1].x+1<=textArray[j].length())
										str = textArray[j].substring(
												0, intersectedSelect[intersectedSelect.length-1].x+1).toString();
									else 
										str = textArray[j].substring(
												0, intersectedSelect[intersectedSelect.length-1].x).toString();
									if (str!=null) {
										x = bounds.x;
										w = (bounds.x + gapX + paint.measureText(str)) - x;
										if (scrollMode==ScrollMode.Both) {
											w -= this.widthOfhScrollPos;
										}
										if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
								    			isTripleBuffering)  x -= this.bounds.x;
										dst = new RectangleF(x, y, w, findHeight);
									}
								}
								if (dst!=null) {
									paint.setColor(Edit.foundColor);
									//paint.setAlpha(150);								 
									canvas.drawRect(dst.toRectF(), paint);
									//paint.setAlpha(255);
								}
								dst = null;
							}
						}
					}
					
					int lineY;
					
					paint.setTextSize(fontSize);
		    		//paint.setColor(Color.BLACK);
					paint.setColor(textColor);
		    		
		    		try{
		    		int limit = Math.min(vScrollPos+numOfLinesPerPage, numOfLines);
		    		
		    		if (scrollMode==ScrollMode.VScroll) {	    		
						for (i=vScrollPos, lineY=0; i<limit; 
								i++, lineY++) {
							x = bounds.x + gapX;
							y = bounds.y + (lineY+1) * lineHeight;
							if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
					    			isTripleBuffering) {
								x -= this.bounds.x;
								y -= this.bounds.y;
							}
							
							CodeString lineText = textArray[i]; 
							for (j=0; j<lineText.length(); j++) {
								CodeString c = lineText.substring(j, j+1);
								String charA = c.toString();
								if (charA.equals("\t")) {
									x += paint.measureText("\t");
								}
								else if (charA.equals(" ")) {
									x += paint.measureText(" ");
								}
								else if (charA.equals("\n") || charA.equals("\r")) continue;
								else {
									paint.setColor(c.charAt(0).color);								
									canvas.drawText(charA, x, y, paint);
									x += paint.measureText(charA);
								}
							}
						}
		    		}
		    		else if (scrollMode==ScrollMode.Both) {
		    			boolean debug=true;
		    			int limit2 = Math.min(vScrollPos+numOfLinesPerPage, numOfLines);
		    			for (i=vScrollPos, lineY=0; i<limit2; 
								i++, lineY++) {
		    				debug=false;
							x = bounds.x + gapX;
							y = bounds.y + (lineY+1) * lineHeight;
							
							if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
					    			isTripleBuffering) {
								x -= this.bounds.x;
								y -= this.bounds.y;
							}
							//if (y < bounds.y+bounds.height) {
								
								CodeString lineText = textArray[i];
								PartOfStr lineTextHScroll=null;
								
								lineTextHScroll = getStringHScroll(lineText);
								x -= part1OfChar;
								
								if (lineTextHScroll!=null) {															 
									for (j=0; j<lineTextHScroll.str.length(); j++) {
										CodeChar c = lineTextHScroll.str.charAt(j);
										String charA = c.toString();
										if (c.c=='\t') {
											x += paint.measureText("\t");
										}
										else if (c.c==' ') {
											x += paint.measureText(" ");
										}
										else if (c.c=='\n' || c.c=='\r') {
											continue;
										}
										else {
											
											paint.setColor(c.color);
											canvas.drawText(charA, x, y, paint);
											x += paint.measureText(charA);
										}
									}
								}
							//}					
						}
		    			if (debug) {
		    				int a;
		    				a=0;
		    				a++;
		    			}
		    		}
		    		}catch(Exception e) {
						//Log.e("EditText Draw 텍스트그리기", e.toString());
		    			e.printStackTrace();
		    			CompilerHelper.printStackTrace(textViewLogBird, e);
					}
		    		
		    		if (iName==280) {
		    			int a;
		    			a=0;
		    			a++;
		    		}
		    		
		    		
		    		// 키보드의 listener가 자신이면 커서를 그린다.
		    		if (CommonGUI.keyboard!=null && this==CommonGUI.keyboard.listener) 
		    			isCursorSeen = true;
		    		else isCursorSeen = false;
		    		
		    		// 키보드의 LangDialog의 editText가 터치되도라도 커서를 보여줘야 한다.
		    		if (IntegrationKeyboard.EditTextOfLangDialogIsTouched) {
		    			isCursorSeen = true;
		    		}
		    		
		    		// 커서가 잘못된 것일 경우 예외 코드
		    		if (isCursorSeen && vScrollPos<=cursorPos.y && 
							cursorPos.y<vScrollPos+numOfLinesPerPage) {
			    		if (cursorPos.x<0 || cursorPos.x>textArray[cursorPos.y].length()) {
			    			isCursorSeen = false;
			    		}
		    		}
		    		else {
		    			isCursorSeen = false;
		    		}
					
					
		    		try{
					if (isCursorSeen && vScrollPos<=cursorPos.y && 
							cursorPos.y<vScrollPos+numOfLinesPerPage) {
						
						String text;
						String curLine = null;
						if (cursorPos.y<this.numOfLines) {
							curLine = textArray[cursorPos.y].toString();
						}
						else {
							curLine = "";
						}
						int lineLen = curLine.length();
						
						
						String curChar=null;
						//String lastChar=null;
						if (lineLen>0) {
							if (cursorPos.x<lineLen) {	
								text = curLine.substring(0, cursorPos.x);
								x = bounds.x + gapX + paint.measureText(text);
								curChar = curLine.substring(cursorPos.x,cursorPos.x+1);
								w = paint.measureText(curChar);							
							}
							else { // if (cursorPos.x>=lineLen)	
								text = curLine.substring(0, lineLen);
								x = bounds.x + gapX + paint.measureText(text);
								curChar = "";
								w = 0;
							}
						}
						else {	// if (lineLen<=0)
							if (scrollMode==ScrollMode.Both) {
								x = bounds.x + gapX;
								w = 0;
							}
							else {
								x = bounds.x + gapX;
								w = 0;
							}
							curChar = "";
						}
						/*if (scrollMode==ScrollMode.Both) {
							if (x + w <bounds.x+gapX+widthOfhScrollPos) {
								isCursorSeen = false;
							}
							else if (x>bounds.x+gapX+widthOfhScrollPos+widthOfCharsPerPage) {
								isCursorSeen = false;
							}
							else {
								isCursorSeen = true;
							}
						}*/
						
						if (isCursorSeen) {
							if (scrollMode==ScrollMode.Both) {
								x -= widthOfhScrollPos;
							}
							y = bounds.y + (cursorPos.y-vScrollPos) * lineHeight + descent;
							drawCursor(canvas, x, y, w, curChar);
						}//if (isCursorSeen) {
					}//if (isCursorSeen && vScrollPos<=cursorPos.y && 
					
					canvas.restore();
					
					if (scrollMode==ScrollMode.Both) {
						if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
				    			isTripleBuffering) {
							vScrollBar.incxForBitmapRendering = (int) this.bounds.x;
							vScrollBar.incyForBitmapRendering = (int) this.bounds.y;
						}
						else {
							vScrollBar.incxForBitmapRendering = 0;
							vScrollBar.incyForBitmapRendering = 0;
						}
						vScrollBar.draw(canvas);
						if (hScrollBar!=null) {
							if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
					    			isTripleBuffering) {
								hScrollBar.incxForBitmapRendering = (int) this.bounds.x;
								hScrollBar.incyForBitmapRendering = (int) this.bounds.y;
							}
							else {
								hScrollBar.incxForBitmapRendering = 0;
								hScrollBar.incyForBitmapRendering = 0;
							}
							hScrollBar.draw(canvas);
						}
					}
					else {
						if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
				    			isTripleBuffering) {
							vScrollBar.incxForBitmapRendering = (int) this.bounds.x;
							vScrollBar.incyForBitmapRendering = (int) this.bounds.y;
						}
						else {
							vScrollBar.incxForBitmapRendering = 0;
							vScrollBar.incyForBitmapRendering = 0;
						}
						vScrollBar.draw(canvas);
					}
		    		}catch(Exception e) {
		    			e.printStackTrace();
		    			CompilerHelper.printStackTrace(textViewLogBird, e);
					}
		    		
		    		if (isSingleLine==false) {
			    		// 커서의 위치를 수평스크롤바 위에 그린다.
			    		float textSizeOfLoc = vScrollBar.bounds.width * 0.6f;
			    		paint.setTextSize(textSizeOfLoc);
			    		paint.setColor(Color.MAGENTA);
			    		// cursorPos는 0부터 시작이고 화면 커서라인은 1부터 시작한다.
			    		String loc = "(" + (cursorPos.x+1) + "," + (cursorPos.y+1) + ")";
			    		x = bounds.x + bounds.width/2 - paint.measureText(loc)/2;
			    		y = bounds.bottom()-textSizeOfLoc*0.3f;
			    		if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
				    			isTripleBuffering) {
				    		x -= this.bounds.x;
				    		y -= this.bounds.y;
			    		}
			    		canvas.drawText(loc, x, y, paint);
		    		}
		    		
				}catch(Exception e) {
					//Log.e("EditText draw()", e.toString());
					e.printStackTrace();
					CompilerHelper.printStackTrace(textViewLogBird, e);
				}
				
				
			}
			//}
			catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(textViewLogBird, e);
			}
			
		}//synchronized
		
	}
	
	
	void drawCursor(Canvas canvas, float x, float y, float w, String curChar) {
		if ((CommonGUI.keyboard.mode==Mode.Hangul && !(Hangul.mode==Hangul.Mode.None)) 
				||  isReadOnly) {
			if (curChar!=null && !curChar.equals("") && !curChar.equals("\n")) {
				if (Control.CurrentSystem==Control.CurrentSystemIsAndroid) {
					// 한글이 조합중이거나 읽기모드이면
					RectangleF dst = new RectangleF(x, y, w, lineHeight);
					paint.setAlpha(100);
					canvas.drawRect(dst.toRectF(), paint);
					paint.setAlpha(255);
				}
				else { // 현재 시스템이 자바이면 알파가 불가능하므로 이렇게 한다.					
					if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
			    			isTripleBuffering) {
						x -= this.bounds.x;
						y -= this.bounds.y;
					}
					RectangleF dst = new RectangleF(x, y, lineHeight*0.15f, lineHeight);
					paint.setColor(Edit.cursorColor);
					canvas.drawRect(dst.toRectF(), paint);
				}
			}//if (curChar!=null && !curChar.equals("") && !curChar.equals("\n")) {
			else { 
				// 읽기 모드인데 curChar가 \n이나 ""이더라도 커서를 보여줘야 한다.
				if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
		    			isTripleBuffering) {
					x -= this.bounds.x;
					y -= this.bounds.y;
				}
				RectangleF dst = new RectangleF(x, y, lineHeight*0.15f, lineHeight);
				paint.setColor(Edit.cursorColor);
				canvas.drawRect(dst.toRectF(), paint);
			}
		}//if ((CommonGUI.keyboard.mode==Mode.Hangul && !(Hangul.mode==Hangul.Mode.None)) 
		 //||  isReadOnly) {
		else {
			// 한글이 조합중도 아니고 읽기모드도 아닌 경우							
			if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering) {
				x -= this.bounds.x;
				y -= this.bounds.y;
			}
			RectangleF dst = new RectangleF(x, y, lineHeight*0.15f, lineHeight);
			paint.setColor(Edit.cursorColor);
			canvas.drawRect(dst.toRectF(), paint);
		}//else
	}
	
	
	/**mCanvas 안에 있는 bitmapForRendering 비트맵에 그린다. 
     * 비트멥은 원점부터 시작하므로 bounds에서 bounds.x와 bounds.y를 빼서 그려야 비트맵에 그릴 수 있다.
     *  @param canvas : 컨트롤이 갖고 있는 mCanvas
     *  */
	public synchronized void drawToImage(Canvas canvas) {
		if (canvas==null) return;
		
		drawCommon(canvas);
		
	}//draw

}