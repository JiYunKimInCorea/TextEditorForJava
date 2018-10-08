package com.gsoft.common.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import com.gsoft.common.ColorEx;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.CommonGUI_SettingsDialog;
import com.gsoft.common.Compiler;
import com.gsoft.common.ContentManager;
import com.gsoft.common.Font;
import com.gsoft.common.IO;
import com.gsoft.common.PaintEx;
import com.gsoft.common.CompilerHelper;
import com.gsoft.common.Util;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Compiler_gui.TextView;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Font.FontFamily;
import com.gsoft.common.Font.FontSortVert;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util.Array;
import com.gsoft.common.Util.ArrayListChar;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.ArrayListTextLine;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.Math;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Control.Container;
import com.gsoft.common.gui.EditText.Edit;
import com.gsoft.common.gui.IntegrationKeyboard.Hangul;
import com.gsoft.common.gui.IntegrationKeyboard.Mode;
import com.gsoft.common.gui.Menu.MenuType;
import com.gsoft.common.gui.ScrollBars.HScrollBar;
import com.gsoft.common.gui.ScrollBars.VScrollBar;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.common.R.R;

public class EditRichText extends Container implements OnTouchListener {
	
	public static String[] namesOfButtonsOfToolbar = {
		"S", "F", "C", "B", "U", "P", "FN", "R/W"
	};
	
	public MenuWithAlwaysOpen toolbar;
	
		
	
	public enum ScrollMode {
		VScroll,
		Both
	}
	
	public static class Character {
		public Bitmap bitmap;
		String filename;	// context에 저장된 파일이름
		
		public char charA;
		public float size;
		private int charColor = Color.BLACK;
		
		public void setCharColor(int color) {
			this.charColor = color;
		}
		boolean isUnderLine;
		boolean isBold;
		boolean isItalic;
		public Typeface typeface = Control.typefaceDefault;		
		String typefaceName;
		//FontFamily fontFamily = Font.fromString(typefaceName);
		
		public void write(OutputStream os, TextFormat format) {			
			if (filename==null) {
				IO.writeString(os, "null", format, true, true);
				IO.writeChar(os, charA, format, true);
				IO.writeFloat(os, size);
				IO.writeInt(os, charColor, true);
				IO.writeBoolean(os, isUnderLine);
				IO.writeBoolean(os, isBold);
				IO.writeBoolean(os, isItalic);
				if (typefaceName==null) IO.writeString(os, "null", format, true, true);
				else IO.writeString(os, typefaceName, format, true, true);
				
			}
			else {
				IO.writeString(os, filename, format, true, true);
				if (bitmap!=null) {
					/*if (Control.CurrentSystem.equals(Control.CurrentSystemIsAndroid)) {
						bitmap.compress(Bitmap.CompressFormat.PNG, 70, os);
					}
					else {*/
						try {
							String imagePath = Control.DownloadedImageDirPath;
							File dir = new File(imagePath);
							dir.mkdir();
							File imageFile = new File(imagePath + File.separator + filename); 
							FileOutputStream out = new FileOutputStream(imageFile);
							bitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
							out.close();
						}catch(Exception e) {
							e.printStackTrace();
						}
					//}
				}
				//IO.writeFloat(os, size);
				
			}
			
			
		}
		
		public static Character read(InputStream is, TextFormat format) {
			try{
			Character c = new Character();
			c.filename = IO.readString(is, format);			
			if (c.filename.equals("null")) c.filename = null;
			
			if (c.filename==null) {
				c.charA = IO.readCharUTF16(is, format, true);
				c.size = IO.readFloat(is);
				c.charColor = IO.readInt(is, true);
				c.isUnderLine = IO.readBoolean(is);
				c.isBold = IO.readBoolean(is);
				c.isItalic = IO.readBoolean(is);
				c.typefaceName = IO.readString(is, format);
				if (c.typefaceName.equals("null")) {
					c.typefaceName = "Default";
					c.typeface = Control.typefaceDefault;
				}
				else {
					FontFamily family = Font.fromString(c.typefaceName);
					if (c.isBold && c.isItalic)
						c.typeface = Font.getTypeface(family, true, true);
					else if (c.isBold)
						c.typeface = Font.getTypeface(family, true, false);
					else if (c.isItalic)
						c.typeface = Font.getTypeface(family, false, true);
					else
						c.typeface = Font.getTypeface(family, false, false);
					
				}
			}
			else {
				try {
					/*if (Control.CurrentSystem.equals(Control.CurrentSystemIsAndroid)) {
						c.bitmap = BitmapFactory.decodeStream(is);
						c.size = c.bitmap.getHeight();
					}
					else {*/
						String imagePath = Control.DownloadedImageDirPath;
						File imageFile = new File(imagePath + File.separator + c.filename); 
						FileInputStream in = new FileInputStream(imageFile);
						c.bitmap = BitmapFactory.decodeStream(in);
						c.size = c.bitmap.getHeight();
						in.close();
					//}
				}catch(OutOfMemoryError e) {
					throw e;
				}
			}
			
			return c;
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				return null;
			}
		}
		
		// read, write 목적
		public Character() {
			//typeface = Control.typefaceDefault;
		}
		
		public Character(Bitmap bitmap, String filename) {
			this.bitmap = bitmap;
			this.filename = filename;
			size = bitmap.getHeight();
		}
		
		public Character(char charA, float size) {
			this.charA = charA;
			this.size = size;
		}
		
		public Character(Character c) {
			if (c.bitmap==null) {
				this.charA = c.charA;
				this.size = c.size;
				this.charColor = c.charColor;
				this.isUnderLine = c.isUnderLine;
				this.isBold = c.isBold;
				this.isItalic = c.isItalic;
				this.typeface = c.typeface;
				this.typefaceName = c.typefaceName;
			}
			else {
				this.bitmap = c.bitmap;
				this.filename = c.filename;
				this.size = c.size;
			}
		}
		
		public void allocate(Character c) {
			if (c.bitmap==null) {
				this.charA = c.charA;
				this.size = c.size;
				this.charColor = c.charColor;
				this.isUnderLine = c.isUnderLine;
				this.isBold = c.isBold;
				this.isItalic = c.isItalic;
				this.typeface = c.typeface;
				this.typefaceName = c.typefaceName;
			}
			else {
				this.bitmap = c.bitmap;
				this.filename = c.filename;
				this.size = c.size;
			}
		}
		
		public String toString() {
			char[] arg = {charA};
			String r = new String(arg);
			return r;
		}
		
		public boolean equals(Character c) {
			if (bitmap==null) {
				if (charA==c.charA && size==c.size)
					return true;
				return false;
			}
			else {
				return false;
			}
		}
		
		public boolean equals(char c) {
			if (charA==c)
				return true;
			return false;
		}
	}
	
	public static class TextLine {
		public Character[] characters;
		public int count;
		float lineHeight;
		float maxFontSize;
		//boolean maxFontSizeIsImage;
		
		float descent;
		float leading;
		
		static float physicalDescentRate = 0.28f;
		//static float logicalDescentRate = 0.25f;
		static float leadingRate = 0.05f;
		static float physicalDescentRate_Image = 0.1f;
		static float leadingRate_Image = 0.01f;
		static final int MaxCharCount = 100;
		
		public void write(OutputStream os, TextFormat format) {
			int i;
			IO.writeInt(os, count, true);
			IO.writeFloat(os, lineHeight);
			IO.writeFloat(os, maxFontSize);
			for (i=0; i<count; i++) {
				characters[i].write(os, format);
			}
			
		}
		
		public static TextLine read(InputStream is, TextFormat format) {
			int i;
			TextLine textLine = new TextLine();			
			
			textLine.count = IO.readInt(is, true);
			textLine.lineHeight = IO.readFloat(is);
			textLine.maxFontSize = IO.readFloat(is);
			textLine.characters = new Character[textLine.count];  
			for (i=0; i<textLine.count; i++) {
				try {
					textLine.characters[i] = Character.read(is, format);
				}catch(OutOfMemoryError e) {
					throw e;
				}
			}
			
			return textLine;			
		}
		
		// read, write 목적
		public TextLine() {
			
		}
		
		public TextLine(Character[] characters, int count) {
			this.characters = characters;
			this.count = count;
			setLineHeight(30);
		}
		
		public TextLine(Character[] characters) {
			this.characters = characters;
			this.count = characters.length;
			setLineHeight(30);
		}
		
		public TextLine(int count, float size) {
			this.characters = new Character[count];
			int i;
			for (i=0; i<count; i++) {
				characters[i] = new Character((char)0, size);
			}
			this.count = count;
			//this.maxFontSize = size;
			setLineHeight(size);
		}
		
		public TextLine(int count) {
			this.characters = new Character[count];
			int i;
			for (i=0; i<count; i++) {
				characters[i] = new Character((char)0, 0);
			}
			this.count = count;
			setLineHeight(30);
		}
		
		public TextLine(String c, float size) {
			int i;
			if (c==null) c="";
			characters = new Character[c.length()];
			for (i=0; i<c.length(); i++) {
				characters[i] = new Character(c.charAt(i), size);
			}
			this.count = c.length();
			this.maxFontSize = size;
			setLineHeight(size);
		}
		
		public void reset() {
			count = 0;
		}
		
		/*public TextLine(char[] c, float size) {
			int i;
			if (c==null) c= new char[0];
			characters = new Character[c.length];
			for (i=0; i<c.length; i++) {
				characters[i] = new Character(c[i], size);
			}
			this.count = c.length;
			this.maxFontSize = size;
			setLineHeight(size);
		}*/
			
		public char[] toCharArray() {
			int i;
			char[] r = new char[count];
			for (i=0; i<count; i++) {
				r[i] = this.characters[i].charA;
			}
			return r;
		}
		
		public Character[] toCharacterArray() {
			int i;
			Character[] r = new Character[count];
			for (i=0; i<count; i++) {
				r[i] = new Character(this.characters[i]);
			}
			return r;
		}
	
		
		public String toString() {
			char[] charArray = this.toCharArray();
			return new String(charArray);
		}
		
		/**start포함, end미포함, 현재 스트링이 빈스트링일 경우 빈스트링 리턴*/
		public TextLine subTextLine(int start , int end) {
			//if (end-start<0) return null;	// 길이
			//if (start<0 || end<0) return null;
			if (this.count<=0) return new TextLine("", maxFontSize);
			TextLine r = new TextLine(end-start, maxFontSize);
			for (int i=start; i<end; i++) {
				r.characters[i-start] = this.characters[i];
			}
			r.setLineHeight(30);
			return r;
		}
		
		public boolean equals(String str) {
			if (this.count!=str.length()) return false;
			for (int i=0; i<str.length(); i++) {
				if (characters[i].charA!=str.charAt(i))
					return false;
			}
			return true;
		}
		
		public boolean equals(TextLine str) {
			if (this.count!=str.count) return false;
			for (int i=0; i<str.count; i++) {
				if (characters[i].equals(str.characters[i])==false)
					return false;
			}
			return true;
		}
		
		void setLineHeight(float sizeWhenCountIsZero/*OrWhenElementIsOne*/) {
			if (count==0) {
				maxFontSize = sizeWhenCountIsZero;
				this.descent = maxFontSize*physicalDescentRate;
				this.leading = maxFontSize*leadingRate;
				this.lineHeight = maxFontSize + descent + leading;
				return;
			}
			int i;				
			maxFontSize = 0;
			this.descent = 0;
			this.leading = 0;
			float descent=0;
			float leading=0;
			for (i=0; i<count; i++) {
				float charSize = characters[i].size;
				if (characters[i].bitmap==null) {
					descent = charSize*physicalDescentRate;
					leading = charSize*leadingRate;
					if (this.descent<descent) this.descent = descent;
					if (this.leading<leading) this.leading = leading;
				}
				if (charSize>maxFontSize) {
					maxFontSize = charSize;						
				}
			}
			lineHeight = maxFontSize + this.descent + this.leading;
		}
		
		void add(Character c) {
			if (count==0) {
				characters = Array.Resize(characters, count+1);
			}
			else {
				if (count>=characters.length)
					characters = Array.Resize(characters, count+10);
			}
			//characters[count] = new Character(c);
			characters[count] = c;
			setLineHeight(c.size);
			count++;
		}
		
		/**startIndex포함, endIndex미포함*/
		public static float measureHeight(TextLine[] textArray, int startIndex, int endIndex) {
			int i;
			float r=0;
			for (i=startIndex; i<endIndex; i++)  {
				r += textArray[i].lineHeight;
			}
			return r;
		}
		
		public void resize(int len) {
			this.characters = Array.Resize(characters, len);
			if (count>len) count = len;			
		}
		
		public void insert(Character[] src, int srcIndex, int destIndex, int len) {
			try {
				characters = Array.InsertNoSpaceError(src, 0, characters, destIndex, len);
				count+=len;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}			
		}
		
		public void insert(int destIndex, TextLine str) {
			try {
				characters = Array.InsertNoSpaceError(str.characters, 0, characters, destIndex, str.count);
				count+=str.count;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
		}
		
		public void delete(int startIndex, int len) {
			this.characters = Array.Delete(this.characters, 
					startIndex, len);
			count-=len;
		}
				
	}
	
	public boolean isReadOnly;
	
	public boolean isSingleLine = true;
	
	private String text;	
	TextLine textLine;
		
	TextLine[] textArray;
	
	//float lineHeight;
	
	public float fontSize;
	float initFontSize;
	//float maxFontSize;
	
	float descent;
	float descentRate;
	float leading;
	float leadingRate;
	int gapX;
	
	ScrollMode scrollMode;
	VScrollBar vScrollBar;
	HScrollBar hScrollBar;
	
	public int numOfLines;
	
	int vScrollBarWidth;
		
	int vScrollPos;
	int numOfLinesPerPage;
	//int numOfLinesInPage;
	
	float partOfCharY;  // y축으로 폰트의 잘리는 부분의 크기
	
	int heightOfvScrollInc = 10;
	int heightOfvScrollPos;
	int heightOfLines;
	int heightOfLinesPerPage;
	//int heightOfLinesInPage;
		
	int hScrollBarHeight;
	
	int widthOfhScrollPos;
	float maxLineWidth;
	int lineNumOfMaxWidth;
	int widthOfCharsPerPage  =1;
	//int widthOfCharsInPage;
	int widthOfTotalChars;
	int widthOfhScrollInc = 10;
	
	boolean isCursorSeen;
	int valueOfCursorRelativeToHScroll;
	
	int rationalBoundsWidth;
	int rationalBoundsHeight;
	
	Point cursorPos;
	int indexOfCursorInText = -1;	// 현재 입력 문자의 Text에서의 인덱스
	int numOfLinesInText;	// setTextVScroll로 수정되는 줄수, 즉 \n을 만날 때까지의 줄수
	
	boolean isBkSpThatMakeNullStr = false;
	
	public boolean isSelecting;
	int selectLenY;
	Point selectP1 = new Point(0,0);
	Point selectP2 = new Point(0,0);
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
	TextLine copiedText = new TextLine(30); 
	
	
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
	
	//public boolean isActionCaptured_editText; 
	
	
	
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
	
	
	static final int MaxLineCount = 100;
		
	
	
	static final String[] Menu_Typeface = {
		"Default", "SansSerif", "Serif"
	};
	
	MenuWithClosable menuTypeface;
	
	//Typeface typeface = Typeface.DEFAULT;
	Typeface typeface = Control.typefaceDefault;
	
	
	//Font.Style style = Font.Style.Normal;
	
	/** DEFAULT, SANSSERIF, SERIF, MONOSPACE만 가능, isBold, isItalic은 따로*/
	String typefaceName = Menu_Typeface[0];
	
	boolean isBold;
	boolean isUnderline;					
	boolean isItalic;
	
	int curColor;
	
	public boolean isSizing;
	
	// 툴바와 스크롤바를 제외한 영역에서 일어난 MoveAction Capture
	boolean isMoveActionCaptured_onlyEditText;

	private FontSizeDialog fontSizeDialog;
	
	//View view;

	private ColorDialog colorDialog = CommonGUI.colorDialog;

	private FileDialog fileDialog = CommonGUI.fileDialog;

	String errorMessage;
	
	/** isChangeBounds이 true이면 changeBounds동작중임을 나타낸다.*/
	boolean isChangeBounds;

	public Rectangle totalBounds;
	
	public boolean isModified;
	//public String curFileName = "";
	
	Mode keyboardMode;
	IntegrationKeyboard.Hangul.Mode hangulMode;
	
	
	
	/*static class UndoBuffer {
		static class Pair {
			TextLine text;
			Point cursorPos;
			String command;
			Object addedInfo;
		}
		private ArrayListTextLine buffer = new ArrayListTextLine(50);
		private ArrayList arrayCursorPos = new ArrayList(50);
		private ArrayListString bufferCommand = new ArrayListString(50);
		private ArrayList arrayAddedInfo = new ArrayList(50);
		
		void reset() {
			buffer.reset();
			arrayCursorPos.reset();
			bufferCommand.reset();
			arrayAddedInfo.reset();
		}
		void push(Point cursorPos, TextLine text) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add("");
			arrayAddedInfo.add(null);
		}
		void push(Point cursorPos, TextLine text, String charA) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(null);
		}
		void push(Point cursorPos, TextLine text, String charA, Object addedInfo) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
		}
		Pair pop() {
			Pair pair = new Pair();
			pair.text = buffer.getItem(buffer.count-1);
			pair.cursorPos = (Point)arrayCursorPos.getItem(arrayCursorPos.count-1);			
			String command = bufferCommand.getItem(bufferCommand.count-1);
			pair.command = command;
			pair.addedInfo = arrayAddedInfo.getItem(arrayAddedInfo.count-1);
			buffer.count--;
			arrayCursorPos.count--;
			bufferCommand.count--;
			arrayAddedInfo.count--;
			return pair;
		}
	}*/
	
	static class UndoBuffer {
		static class Pair {
			TextLine text;
			Point cursorPos;
			String command;
			Object addedInfo;
			public boolean isSelecting;
			/** ReplaceAll에서 빈 스트링("")으로 대체된 경우 null 이 아니다.
			 * replaceAll할때에 검색된 좌표들을 백업한다.*/
			ArrayList listOfFindPos;
			/** replaceAll할때에 대체된 좌표들을 백업한다.*/
			ArrayList listOfReplacePos;
		}
		private ArrayListTextLine buffer = new ArrayListTextLine(50);
		private ArrayList arrayCursorPos = new ArrayList(50);
		private ArrayListString bufferCommand = new ArrayListString(50);
		private ArrayList arrayAddedInfo = new ArrayList(50);
		private ArrayList arrayIsSelecting = new ArrayList(50);
		private ArrayList arrayListOfFindPos = new ArrayList(50);
		private ArrayList arrayListOfReplacePos = new ArrayList(50);
		
		
		void reset() {
			buffer.reset2();
			arrayCursorPos.reset();
			bufferCommand.destroy();
			arrayAddedInfo.reset();
			arrayIsSelecting.reset();
			arrayListOfFindPos.reset();
			arrayListOfReplacePos.reset();
		}
		/*void push(Point cursorPos, TextLine text) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add("");
			arrayAddedInfo.add(null);
			arrayIsSelecting.add(null);
		}*/
		void push(Point cursorPos, TextLine text, String charA) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(null);
			arrayIsSelecting.add(null);
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, TextLine text, String charA, Object addedInfo) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(null);
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, TextLine text, String charA, Object addedInfo, ArrayList listOfFindPos) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(null);
			arrayListOfFindPos.add(listOfFindPos);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, TextLine text, String charA, Object addedInfo, ArrayList listOfFindPos, ArrayList listOfReplacePos) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(null);
			arrayListOfFindPos.add(listOfFindPos);
			arrayListOfReplacePos.add(listOfReplacePos);
		}
		void push(Point cursorPos, TextLine text, String charA, Object addedInfo, boolean isSelecting) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(new Boolean(isSelecting));
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
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
			try {
			pair.listOfReplacePos = (ArrayList) arrayListOfReplacePos.getItem(arrayListOfReplacePos.count-1);
			}catch(Exception e) {
				int a;
				a=0;
				a++;
				e.printStackTrace();
			}
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
			TextLine text;
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
		private ArrayListTextLine buffer = new ArrayListTextLine(50);
		private ArrayList arrayCursorPos = new ArrayList(50);
		private ArrayListString bufferCommand = new ArrayListString(50);
		private ArrayList arrayAddedInfo = new ArrayList(50);
		private ArrayList arrayIsSelecting = new ArrayList(50);
		private ArrayList arrayPointP2 = new ArrayList(50);
		private ArrayList arrayListOfFindPos = new ArrayList(50);
		private ArrayList arrayListOfReplacePos = new ArrayList(50);
		
		void reset() {
			buffer.reset2();
			arrayCursorPos.reset();
			bufferCommand.destroy();
			arrayAddedInfo.reset();
			arrayIsSelecting.reset();
			arrayPointP2.reset();
			arrayListOfFindPos.reset();
			arrayListOfReplacePos.reset();
		}
		
		void push(Point cursorPos, TextLine text, String charA) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(null);
			arrayIsSelecting.add(null);
			arrayPointP2.add(null);
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, TextLine text, String charA, Object addedInfo) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(null);
			arrayPointP2.add(null);
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, TextLine text, String charA, Object addedInfo, boolean isSelecting, Point p2) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(new Boolean(isSelecting));
			arrayPointP2.add(p2);
			arrayListOfFindPos.add(null);
			arrayListOfReplacePos.add(null);
		}
		
		void push(Point cursorPos, TextLine text, String charA, Object addedInfo, boolean isSelecting, Point p2, ArrayList listFindPos) {
			buffer.add(text);
			arrayCursorPos.add(cursorPos);
			bufferCommand.add(charA);
			arrayAddedInfo.add(addedInfo);
			arrayIsSelecting.add(new Boolean(isSelecting));
			arrayPointP2.add(p2);
			arrayListOfFindPos.add(listFindPos);
			arrayListOfReplacePos.add(null);
		}
		void push(Point cursorPos, TextLine text, String charA, Object addedInfo, boolean isSelecting, Point p2, ArrayList listFindPos, ArrayList listReplacePos) {
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

	private Rectangle boundsOfSizingBorder = new Rectangle();

	private TextView textView;

	
	
	/*private SetTextThread setTextThread;
	
	private boolean running;
	public synchronized void setRunning(boolean b) {
		// TODO Auto-generated method stub
		running = b;
	}
	
	public boolean modified;	
	public synchronized void setModified(boolean b) {
		// TODO Auto-generated method stub
		modified = b;
	}
	
	private ArrayList queueOfSetText = new ArrayList(20);
	
	class ArgOfSetText {
		boolean isAllOrOneLine;
		int startLine;
		TextLine argText;
		
		ArgOfSetText(boolean isAllOrOneLine, int startLine, TextLine argText) {
			this.isAllOrOneLine = isAllOrOneLine;
			this.startLine = startLine;
			this.argText = argText;
		}
	}*/
	
	
	
	public void write(OutputStream os, TextFormat format) {
		//oos = new ObjectOutputStream(os);
		TextLine text = this.TextArrayToText(0,  0,  0, 0);
		text.write(os, format);
		
	}
	
	public static TextLine Read(InputStream is, TextFormat format) {
		//BufferedInputStream bis = new BufferedInputStream(is, IO.DefaultBufferSize);
		TextLine r=null;
		try {
			r = TextLine.read(is, format);
		}catch(OutOfMemoryError e) {
			throw e;
		}
		//is.close();
		return r;
	}
	
	public TextLine read(InputStream is, TextFormat format) {
		//BufferedInputStream bis = new BufferedInputStream(is, IO.DefaultBufferSize);
		TextLine r=null;
		try {
			r = TextLine.read(is, format);
		}catch(OutOfMemoryError e) {
			throw e;
		}
		//is.close();
		return r;
	}
	
	void setHeightOfLines() {
		int i;
		float h=0;
		for (i=0; i<numOfLines; i++) {
			h += textArray[i].lineHeight;			
		}
		this.heightOfLines = (int) h;
	}
	/**스크롤영역(heightOfvScrollPos, heightOfvScrollPos+heightOfLinesPerPage)의 edge부분에 걸치는
	 * 줄도 포함한다.
	 * @param heightOfvScrollPos
	 * @param heightOfLinesPerPage
	 */
	void setNumOfLinesPerPage(float heightOfvScrollPos, float heightOfLinesPerPage) {
		float h=0;
		int i;
		int start=-1, end=-1;
		for (i=0; i<numOfLines; i++) {
			h += textArray[i].lineHeight;
			if (h>heightOfvScrollPos) {
				start = i;
				break;
			}
		}
		if (start==-1) {	// 빈 텍스트
			numOfLinesPerPage = 0;
			return;
		}
		if (h>heightOfvScrollPos+heightOfLinesPerPage) {	// start라인이 매우 큰 라인일 경우
			numOfLinesPerPage = 1;
			return;
		}
		for (i=start+1; i<numOfLines; i++) {
			h += textArray[i].lineHeight;
			if (h>heightOfvScrollPos+heightOfLinesPerPage) {
				end = i;
				break;
			}
		}
		
		if (end==-1) {	// 텍스트는 있으나 내용이 작을 때
			numOfLinesPerPage = (i-1)-start+1;
		}
		else {
			numOfLinesPerPage = end-start+1;
		}
	}
	
	void createFontSizeDialog(View view) {
		int w = (int) (bounds.width*0.7f);
		int h = (int) (bounds.height*0.4f);
		int x = bounds.x + bounds.width/2 - w/2;
		int y = bounds.y + bounds.height/2 - h/2;
		Rectangle bounds = new Rectangle(x,y,w,h);
		fontSizeDialog = new FontSizeDialog(view, bounds);		
	}
	
	
	
	void createToolbar(Rectangle bounds) {
		toolbar = new MenuWithAlwaysOpen("menu", bounds, MenuType.Vertical, owner, 
				namesOfButtonsOfToolbar, new Size(2,2), false, this, false);
		toolbar.buttons[3].selectable = true;	// Bold키는 토글로 동작한다.
		toolbar.buttons[3].toggleable = true;
		toolbar.buttons[3].ColorSelected = Color.YELLOW;
		toolbar.buttons[4].selectable = true;	// Underline키는 토글로 동작한다.
		toolbar.buttons[4].toggleable = true;
		toolbar.buttons[4].ColorSelected = Color.YELLOW;
		toolbar.buttons[5].selectable = true;	// Italic키는 토글로 동작한다.
		toolbar.buttons[5].toggleable = true;
		toolbar.buttons[5].ColorSelected = Color.YELLOW;
		toolbar.buttons[7].selectable = true;	// R/W는 토글로 동작한다.
		toolbar.buttons[7].toggleable = true;
		toolbar.buttons[7].ColorSelected = Color.YELLOW;
		toolbar.open(true, false);
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
	
		
	void createMenuTypeface() {
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int x, y;
		int width=(int) (viewWidth*0.5f);
		int height=(int) (viewHeight*0.3f);
		x = viewWidth/2-width/2;
		y = viewHeight/2-height/2;
		Rectangle boundsMenuTypeface = new Rectangle(x,y,width,height);  
		menuTypeface = new MenuWithClosable("MenuTypeface", boundsMenuTypeface, 
				MenuType.Vertical, owner, Menu_Typeface, new Size(3,3), true, this);		
	}
	
	/*void createFileDialog() {
		float w = bounds.width;
		float h = bounds.height;
		float x = bounds.x + bounds.width/2 - w/2;
		float y = bounds.y + bounds.height/2 - h/2;
		RectangleF boundsOfFileDialog = new RectangleF(x, y, w, h);
		fileDialog = new FileDialog(false, false, this, boundsOfFileDialog, null,
				keyboard);		
	}*/
		
	public void setBackColor(int backColor) {
		super.setBackColor(backColor);
		curColor = textColor;
		int j, i;
		for (j=0; j<numOfLines; j++) {
			TextLine line = textArray[j];
			for (i=0; i<line.count; i++) {
				Character c = line.characters[i]; 
				if (ColorEx.isSameColor(c.charColor,backColor)) {
					c.setCharColor(textColor);
				}
			}
		}
		//this.drawToImage(mCanvas);
	}
	
	/**hides가 false이고 editText의 현재상태가 최대화가 아니면 
	 * (키보드와 SizingBorder의 영역을 바꿔줌과 함께) 키보드를 자동으로 보여주고
	 * 현재상태가 최대화상태이면 키보드를 숨겨준다.
	 * hides가 true이면 SizingBorder를 숨긴다.*/
	public synchronized void setHides(boolean hides) {		
		super.setHides(hides);
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
					CommonGUI.keyboard.setHides(true);
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
	
	void changeBoundsOfKeyboardAndSizingBorder(Rectangle boundsOfEditText) {
		int viewHeight = view.getHeight();
		int viewWidth = view.getWidth();
		int heightOfGap = (int)(viewHeight * vertScaleOfGap);
		int top = boundsOfEditText.bottom()+heightOfGap;
		Rectangle boundsOfIntegrationKeyboard = new Rectangle(0, top,
				(int)(viewWidth*scaleOfKeyboardX), viewHeight-top);
		if (viewHeight-top<0) {
			boundsOfIntegrationKeyboard.height = (int)(viewHeight*Control.scaleOfKeyboardY);
		}
		if (CommonGUI.keyboard!=null) {
			if (Control.requiresChangingBounds(CommonGUI.keyboard.bounds, boundsOfIntegrationKeyboard)) {
				CommonGUI.keyboard.changeBounds(boundsOfIntegrationKeyboard);
				boundsOfSizingBorder.x = totalBounds.x;
				boundsOfSizingBorder.width = totalBounds.width;
				boundsOfSizingBorder.y = boundsOfEditText.bottom() + 1;
				boundsOfSizingBorder.height = CommonGUI.keyboard.buttons[0].bounds.y - boundsOfSizingBorder.y;
				sizingBorder.bounds = boundsOfSizingBorder;
			}
		}
	}
	
	/** bounds가 바뀔 때 호출, setHides에서 호출*/
	public void backUpBounds() {
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
			
			//changeBoundsSafe(bounds);
			changeBounds(bounds);
			
			if (CommonGUI.keyboard!=null) CommonGUI.keyboard.setIsOpen(false);
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
			if (CommonGUI.keyboard!=null) CommonGUI.keyboard.setHides(false);
			
			//setHides(false);
			
			/*RectangleF newBoundsOfKeyboard = new RectangleF(prevSizeOfKeyboard);
			if (keyboard!=null) keyboard.changeBounds(newBoundsOfKeyboard);
			
			RectangleF newBoundsOfBorder = new RectangleF(prevSizeOfSizingBorder);
			sizingBorder.changeBounds(newBoundsOfBorder);*/
		}
	}
	
	public EditRichText(Object owner, String name, Rectangle paramBounds, 
			float fontSize, 
			boolean isSingleLine, String text, ScrollMode scrollMode) {
		super();
		this.owner = owner;
		this.name = name;
		
		setBackColor(Color.WHITE);
		curColor = textColor;
		
		descentRate = TextLine.physicalDescentRate;
		leadingRate = TextLine.leadingRate;
		
		this.fontSize = fontSize;
		this.descent = fontSize * descentRate;
		this.initFontSize = fontSize;
		//this.lineHeight = this.fontSize + this.descent;
		
		this.isSingleLine = isSingleLine;
		this.text = text;
		
		Rectangle boundsOfToolbar = new Rectangle(paramBounds.x, paramBounds.y, 
				(int)(paramBounds.width*0.1f), paramBounds.height); 
		Rectangle boundsOfEditRichText = new Rectangle(boundsOfToolbar.right(), paramBounds.y, 
				(int)(paramBounds.width*0.9f), paramBounds.height);
		createToolbar(boundsOfToolbar);
		this.bounds = boundsOfEditRichText;
		this.totalBounds = new Rectangle();
		this.totalBounds.copy(paramBounds);
		
		this.numOfLines = 1;
		//this.backColor = Color.WHITE;
		
		//this.gapX = maxFontSize;
		this.gapX = 10;
		this.cursorPos = new Point(0,0);
		
		context = view.getContext();
		
		
		createMenuFontSize();
		createMenuTypeface();
		createFontSizeDialog(view);
		//createColorDialog(view);
		//createFileDialog();
		
		createMenuFunction();
		
		this.createTextView();
		
		textArray = new TextLine[MaxLineCount];
		int i;
		for (i=0; i<textArray.length; i++) {
			textArray[i] = new TextLine(0,initFontSize);
		}
		
		paint.setTypeface(typeface);
		paint.setColor(Color.BLACK);
		paint.setTextSize(fontSize);
		paint.setStyle(Style.FILL);
		paintOfBorder.setStyle(Style.STROKE);
		paintOfBorder.setColor(ColorEx.darkerOrLighter(backColor, -100));
		
		bitmapCursor = ContentManager.LoadBitmap(context, R.drawable.cursor);
		
		this.scrollMode = scrollMode;
		
		bound(BoundMode.Create, true);
		
		/*if (scrollMode==ScrollMode.VScroll) {
			this.vScrollBarWidth = view.getWidth() * ScrollBars.VScrollBarWidthScale;
			this.heightOfvScrollPos = 0;
			rationalBoundsWidth = this.bounds.width - 2*gapX - vScrollBarWidth;
			rationalBoundsHeight = this.bounds.height;
			
			heightOfLinesPerPage = (int) rationalBoundsHeight;
			
			RectangleF boundsOfVScrollBar = new RectangleF(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);
						
			vScrollBar = new VScrollBar(this, context, boundsOfVScrollBar);
			vScrollBar.setOnTouchListener(this);
			
			
		}		
		else if (scrollMode==ScrollMode.Both) {
			this.vScrollBarWidth = view.getWidth() * ScrollBars.VScrollBarWidthScale;
			this.heightOfvScrollPos = 0;
			rationalBoundsWidth = this.bounds.width - 2*gapX - vScrollBarWidth;
			this.hScrollBarHeight = view.getHeight() * ScrollBars.HScrollBarHeightScale;
			this.widthOfhScrollPos = 0;
			rationalBoundsHeight = this.bounds.height - hScrollBarHeight;
			
			heightOfLinesPerPage = (int) rationalBoundsHeight;
			widthOfCharsPerPage = (int) rationalBoundsWidth;
						
			RectangleF boundsOfVScrollBar = new RectangleF(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);			
			vScrollBar = new VScrollBar(this, context, boundsOfVScrollBar);
			vScrollBar.setOnTouchListener(this);
			
			RectangleF boundsOfHScrollBar = new RectangleF(bounds.x+gapX, 
					bounds.y+bounds.height-hScrollBarHeight,
					this.rationalBoundsWidth, hScrollBarHeight);
			
			hScrollBar = new HScrollBar(this, context, boundsOfHScrollBar);
			hScrollBar.setOnTouchListener(this);
			
					
		}*/
		
		
		
		this.textLine = new TextLine(text, fontSize);
		setText(0, textLine);
		
		//this.mDrawingThread = new Thread(this);
		//this.mDrawingThread.start();
		
		if (isMaximized()==false) backUpBounds();
		
		
	}
	
	/** 스크롤부분과 관련된 여러 속성값들을 설정한다.*/	
	void boundAttributes(BoundMode boundMode) {
		if (scrollMode==ScrollMode.VScroll) {
			if (boundMode==BoundMode.Create) {
				this.vScrollBarWidth = ScrollBars.getScrollBarSize();
			}
			
			rationalBoundsWidth = bounds.width - 2*gapX - vScrollBarWidth;
			rationalBoundsHeight = bounds.height;
			heightOfLinesPerPage = (int) rationalBoundsHeight;
			setNumOfLinesPerPage(heightOfvScrollPos, heightOfLinesPerPage);
		}		
		else if (scrollMode==ScrollMode.Both) {
			if (boundMode==BoundMode.Create) {
				this.vScrollBarWidth = ScrollBars.getScrollBarSize();
				this.hScrollBarHeight = ScrollBars.getScrollBarSize();
			}
			
			if (this.vScrollBarWidth>this.hScrollBarHeight) {
				this.vScrollBarWidth = this.hScrollBarHeight;
			}
			else {
				this.hScrollBarHeight = this.vScrollBarWidth;
			}
			
			rationalBoundsWidth = bounds.width - 2*gapX - vScrollBarWidth;
			rationalBoundsHeight = bounds.height - hScrollBarHeight;
			heightOfLinesPerPage = (int) rationalBoundsHeight;  
			setNumOfLinesPerPage(heightOfvScrollPos, heightOfLinesPerPage);
						
		}
	}
	
	/** EditRichText생성시, changeBounds(), 폰트 size변경시, 스크롤 Mode변경시 호출되므로 주의해야 한다.
	 * ScrollMode가  Both일 때 폰트크기변경, bounds크기 변경을 하면  setText와  cursor를 초기화할 필요가 없다.
	   ScrollMode가 VScroll일 때  bounds크기 변경을 하면  setText(세로로만 크기가 바뀌므로)와  cursor를 초기화할 필요가 없다.*/
	void bound(BoundMode boundMode, boolean initCursor) {
		boundAttributes(boundMode);
		if (initCursor) {
			vScrollPos = 0;
			heightOfvScrollPos = 0;
			widthOfhScrollPos = 0;
			cursorPos.x = 0;
			cursorPos.y = 0;
		}
		if (scrollMode==ScrollMode.VScroll) {
			/*rationalBoundsWidth = bounds.width - 2*gapX - vScrollBarWidth;
			rationalBoundsHeight = bounds.height;
			heightOfLinesPerPage = (int) rationalBoundsHeight;
			setNumOfLinesPerPage(heightOfvScrollPos, heightOfLinesPerPage);
			numOfLinesInPage = numOfLines - vScrollPos;
			numOfLinesInPage = Math.min(numOfLinesPerPage, numOfLinesInPage);
			
			RectangleF boundsOfVScrollBar = new RectangleF(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);
			vScrollBar.changeBounds(boundsOfVScrollBar);
			
			TextLine textLine = TextArrayToText(0, 0, cursorPos.y, cursorPos.x);
			setText(0, textLine);
			
			setToolbarAndCurState(cursorPos);*/
			Rectangle boundsOfVScrollBar = new Rectangle(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);
			if (vScrollBar!=null) vScrollBar.changeBounds(boundsOfVScrollBar);
			else {
				vScrollBar = new VScrollBar(this, context, boundsOfVScrollBar);
				vScrollBar.setOnTouchListener(this);
			}
			setVScrollPos();
			setVScrollBar(true);
			setToolbarAndCurState(cursorPos);
		}		
		else if (scrollMode==ScrollMode.Both) {
			/*rationalBoundsWidth = bounds.width - 2*gapX - vScrollBarWidth;
			rationalBoundsHeight = bounds.height - hScrollBarHeight;
			heightOfLinesPerPage = (int) rationalBoundsHeight;  
			setNumOfLinesPerPage(heightOfvScrollPos, heightOfLinesPerPage);
			numOfLinesInPage = numOfLines - vScrollPos;
			numOfLinesInPage = Math.min(numOfLinesPerPage, numOfLinesInPage);
						
			RectangleF boundsOfVScrollBar = new RectangleF(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);
			vScrollBar.changeBounds(boundsOfVScrollBar);
			
			RectangleF boundsOfHScrollBar = new RectangleF(bounds.x+gapX, 
					bounds.y+bounds.height-hScrollBarHeight,
					this.rationalBoundsWidth, hScrollBarHeight);
			hScrollBar.changeBounds(boundsOfHScrollBar);
			
			TextLine textLine = TextArrayToText(0, 0, cursorPos.y, cursorPos.x);
			setText(0, textLine);
			
			setToolbarAndCurState(cursorPos);*/
			
			Rectangle boundsOfVScrollBar = new Rectangle(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);
			if (vScrollBar!=null) vScrollBar.changeBounds(boundsOfVScrollBar);
			else {
				vScrollBar = new VScrollBar(this, context, boundsOfVScrollBar);
				vScrollBar.setOnTouchListener(this);
			}
			setVScrollPos();
			setVScrollBar(true);
			
			Rectangle boundsOfHScrollBar = new Rectangle(bounds.x+gapX, 
					bounds.y+bounds.height-hScrollBarHeight,
					this.rationalBoundsWidth, hScrollBarHeight);
			if (hScrollBar!=null) hScrollBar.changeBounds(boundsOfHScrollBar);
			else {
				hScrollBar = new HScrollBar(this, context, boundsOfHScrollBar);
				hScrollBar.setOnTouchListener(this);
			}
			
			setHScrollPos();
			setHScrollBar(true);
			
			setToolbarAndCurState(cursorPos);
		}
	}
	
	enum BoundMode {
		Create,
		ChangeBounds,
		FontSize,
		ScrollMode, 
		SetText
	}
	
	/** paramBounds : 툴바를 포함하지 않은 bounds*/
	public void changeBounds(Rectangle paramBounds) {		
		this.bounds.copy(paramBounds);
		applySizingBorderOfView(bounds);
		paramBounds.copy(bounds);
		/*if (keyboard.mode==Mode.Hangul && Hangul.mode!=Hangul.Mode.None)
			this.textLine = TextArrayToText(0, 0, cursorPos.y, cursorPos.x);
		else
			this.textLine = TextArrayToText(0, 0, cursorPos.y, cursorPos.x-1);*/
		
		Rectangle boundsOfToolbar = toolbar.bounds;
		boundsOfToolbar.height = paramBounds.height;
		toolbar.changeBounds(boundsOfToolbar);
		
		totalBounds.x = toolbar.bounds.x;
		totalBounds.y = toolbar.bounds.y;
		totalBounds.width = toolbar.bounds.width+this.bounds.width;
		totalBounds.height = this.bounds.height;
		
		//fontSizeDialog.changeBounds(this.bounds);
		
		//Point backupCursorPos = cursorPos;
		if (scrollMode==ScrollMode.VScroll) {
			bound(BoundMode.ChangeBounds, false);			
			TextLine text = this.TextArrayToText(0, 0, cursorPos.y, cursorPos.x);
			setText(0, text);
		}
		else {
			bound(BoundMode.ChangeBounds, false);
		}
		
		//cursorPos = backupCursorPos;
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
			TextLine text = TextArrayToText(0, 0, 0, 0);			
			setText(0, text);
			CommonGUI.loggingForMessageBox.setHides(true);
			view.postInvalidate();
			
		}
	}
	
	public String getText() {
		int j;
		String newText = "";
		for (j=0; j<numOfLines; j++) {
			newText += textArray[j];
		}
		return newText;
	}
	
	/** cursorPosY에서 n번째 newLineChar를 만날 때까지의  numOfLinesInText을 리턴한다.*/
	private int getNumOfLinesInText(int cursorPosY, int cursorPosX, int numOfNewLineChar) {
		try {
		int i, j;
		char c;
		numOfLinesInText = 0;
		int countOfNewLineChar = 0;
		
		for (j=cursorPosY; j<numOfLines; j++) {
			numOfLinesInText++;
			for (i=0; i<textArray[j].count; i++) {
				c = textArray[j].characters[i].charA;
				if (c=='\n') {
					countOfNewLineChar++;
					if (countOfNewLineChar==numOfNewLineChar) {
						return numOfLinesInText;
					}
				}
			}
		}
		return numOfLinesInText;
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return -1;
		}
	}
	
		
	/** cursorPosY에서 n번째 newLineChar를 만날 때까지의  스트링을 리턴한다.*/
	private TextLine TextArrayToText(int cursorPosY, int cursorPosX, int numOfNewLineChar) {
		try {
		int i, j;
		TextLine newText = new TextLine(0,0);
		int count = 0;
		char c;
		numOfLinesInText = 0;
		indexOfCursorInText = -1;
		int countOfNewLineChar = 0;
		
		for (j=cursorPosY; j<numOfLines; j++) {
			numOfLinesInText++;
			for (i=0; i<textArray[j].count; i++) {
				if (j==cursorPosY && i==cursorPosX) {
					indexOfCursorInText = count;
				}
				count++;
				c = textArray[j].characters[i].charA;
				if (c=='\n') {
					countOfNewLineChar++;
					newText.add(textArray[j].characters[i]);
					if (countOfNewLineChar==numOfNewLineChar)  {
						return newText;
					}
				}
				else {
					newText.add(textArray[j].characters[i]);
				}
				
			}
		}
		// 커서가 텍스트바깥에 위치할 때
		if (indexOfCursorInText==-1) {
			indexOfCursorInText = count;
		}
		return newText;
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return null;
		}
	}
		
	/*private TextLine TextArrayToTextOneLine(int cursorPosY, int cursorPosX) {
		int i, j;
		TextLine newText = new TextLine(0,0);
		int count = 0;
		Character c;
		numOfLinesInText = 0;
		indexOfCursorInText = -1;
		for (j=cursorPosY; j<numOfLines; j++) {
			numOfLinesInText++;
			for (i=0; i<textArray[j].count; i++) {
				if (j==cursorPosY && i==cursorPosX) {
					indexOfCursorInText = count;
				}
				count++;
				//c = textArray[j].substring(i, i+1);
				c = textArray[j].characters[i];
				if (c.equals('\n')) {
					newText.add(c);
					return newText;
				}
				else {
					newText.add(c);
				}
				
			}
		}
		// 커서가 텍스트바깥에 위치할 때
		if (indexOfCursorInText==-1) {
			indexOfCursorInText = count-1;
		}
		return newText;		
	}*/
	
	private TextLine TextArrayToText(int startY, int startX, int cursorPosY, int cursorPosX) {
		int i, j;
		TextLine newText = new TextLine(0,0);
		int count = 0;
		int textLen;
		indexOfCursorInText = -1;
				
		Point selectP1=null, selectP2=null;
		if (makingSelectP1P2OutOfEvent) {
			selectP1 = new Point(selectIndices[0].x,selectIndices[0].y);
			int lastIndex = selectIndicesCount-2;
			selectP2 = new Point(selectIndices[lastIndex+1].x,selectIndices[lastIndex].y);
		}
		
		for (j=startY; j<numOfLines; j++) {
			textLen = textArray[j].count;
			for (i=0; i<textLen; i++) {				
				newText.add(textArray[j].characters[i]);
				if (j==cursorPosY && i==cursorPosX) {
					indexOfCursorInText = count;
				}
				if (makingSelectP1P2OutOfEvent) {
					if (j==selectP1.y && i==selectP1.x) {
						selectP1Index = count;
					}
					if (j==selectP2.y && i==selectP2.x) {
						selectP2Index = count;
					}
				}
				count++;
			}
		}
		// 커서가 텍스트바깥에 위치할 때
		if (indexOfCursorInText==-1) {
			indexOfCursorInText = count;
		}
		return newText;		
	}
	
	/**"Left", "Right", "Up", "Down", "Home", "End", "PgUp", "PgDn"*/
	public void controlChar(int indexInSpecialKeys, String charA) {
		if (0<=indexInSpecialKeys && indexInSpecialKeys<=7)
			this.isSelecting = false;
		
		switch (indexInSpecialKeys) {
			case 0: {
				if (cursorPos.x>0) cursorPos.x--;
				else {
					if (cursorPos.x==0 && cursorPos.y>0) {
						cursorPos.y--;
						cursorPos.x = textArray[cursorPos.y].count;
					}
				}
				break;
			}				
			case 1: {
				if (cursorPos.x<textArray[cursorPos.y].count) cursorPos.x++;
				else {
					if (cursorPos.x==textArray[cursorPos.y].count && cursorPos.y<numOfLines-1) {
						cursorPos.y++;
						cursorPos.x = 0;
					}
				}
				break;
			}
			case 2: {
				if (cursorPos.y>0) cursorPos.y--;
				if (cursorPos.x>textArray[cursorPos.y].count) 
					cursorPos.x = textArray[cursorPos.y].count;
				if (cursorPos.x<0) cursorPos.x = 0; 
				break;
			}
			case 3: {
				if (cursorPos.y<numOfLines-1) cursorPos.y++; 
				if (cursorPos.x>textArray[cursorPos.y].count) 
					cursorPos.x = textArray[cursorPos.y].count;
				if (cursorPos.x<0) cursorPos.x = 0;
				break;
			}
			case 4: cursorPos.x = 0; break;//"Home"
			case 5: {// "End"
				cursorPos.x = textArray[cursorPos.y].count-1;
				if (cursorPos.x<0) cursorPos.x = 0;
				break;
			}
			case 6: { // "PgUp"
						cursorPos.y -= this.numOfLinesPerPage;
						if (cursorPos.y<0) cursorPos.y = 0;
						if (cursorPos.x>textArray[cursorPos.y].count) 
							cursorPos.x = textArray[cursorPos.y].count;
						if (cursorPos.x<0) cursorPos.x = 0;
						break;
				
					}
			case 7: { // "PgDn"
						cursorPos.y += this.numOfLinesPerPage;
						if (cursorPos.y>=this.numOfLines) cursorPos.y = numOfLines-1;
						if (cursorPos.x>textArray[cursorPos.y].count) 
							cursorPos.x = textArray[cursorPos.y].count;
						if (cursorPos.x<0) cursorPos.x = 0;
						break;
				
					}
		}//switch
			setToolbarAndCurState(cursorPos);
			
			if (scrollMode==ScrollMode.VScroll) {			
				setVScrollPos();
				setVScrollBar(true);
			}		
			else if (scrollMode==ScrollMode.Both) {
				setVScrollPos();
				setHScrollPos();
				setVScrollBar(true);			
				setHScrollBar(true);				
			}
		/*}
		else {
			switch (indexInSpecialKeys) {
			case 6: {
					heightOfvScrollPos -= heightOfLinesPerPage;
					if (heightOfvScrollPos<0) heightOfvScrollPos = 0;
					setVScrollPos(heightOfvScrollPos);
					break;
				}				
			case 7: if (heightOfLines>heightOfLinesPerPage) {			
						heightOfvScrollPos += heightOfLinesPerPage;
						if (heightOfvScrollPos>heightOfLines-heightOfLinesPerPage) 
							heightOfvScrollPos = heightOfLines-heightOfLinesPerPage;
						setVScrollPos(heightOfvScrollPos);
						break;
					}
			}
			//setVScrollPos(heightOfvScrollPos);
			setVScrollBar(true);
		}*/
	}
	
	/** 키 입력시 호출된다.
	 * redoBuffer를 초기화시켜야 redoBuffer에 남아있는 상태에서 키를 입력하고 나서 
	 * 다시 redo 를 하면 발생하는 오류를 해결할 수 있다.*/
	public void addChar(String charA/*, boolean isNextToCursor*/) {
		//isBackSpacePressed = false;
		isModified = true;
		redoBuffer.reset();
		
		if (charA==null || charA.equals("")) return;
		if (isSingleLine==false) {
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
			addCharReally(charA/*, isNextToCursor*/);
			
			if (this.scrollMode==ScrollMode.VScroll) {
				setVScrollPos();
				setVScrollBar(true);
			}
			else {
				setVScrollPos();
				setHScrollPos();
				setVScrollBar(true);
				setHScrollBar(true);
			}
			
			//setVScrollBar();
		}
		else {	// singleLine
			if (!charA.equals(NewLineChar)) {
				text += charA;
			}
		}
	}
	
	/** replace, replaceAll에서 호출한다.
	 * undo에서 undo를 하기 전의 상태(redo실행결과)를 backup한다. 후에 redo에서 복원한다.*/
	void backUpForRedo(String charA, TextLine textOfBackupForUndo, Point undoPos, boolean isSelecting, Object addedInfo, 
			ArrayList listFindPos, ArrayList listReplacePos) {
		redoBuffer.push(undoPos, textOfBackupForUndo, charA, addedInfo, isSelecting, null, listFindPos, listReplacePos);
	}
	
	/** 선택줄일때만 undo()에서 호출한다.<br>
	 * undo에서 undo를 하기 전의 상태(redo실행결과)를 backup한다. 후에 redo에서 복원한다.*/
	void backUpForRedo(String charA, TextLine textOfBackupForUndo, Point undoPos, Object addedInfo, boolean isSelecting, Point p2) {
		if (isSelecting) {
			if (charA.equals(BackspaceChar)) {	// 0열에서 '\n'이 지워지는 back키만
				TextLine newText=null;
				newText = TextArrayToText(undoPos.y, undoPos.x, 1);
				redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA, addedInfo, true, p2);
			}
			else if (charA.equals(DeleteChar)) { // 마지막열에서 '\n'이 지워지는 delete키만
				TextLine newText=null;
				newText = TextArrayToText(undoPos.y, undoPos.x, 1);
				redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA, addedInfo, true, p2);
			}
			else if (charA.equals(NewLineChar)) {
				TextLine newText=null;
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
	
	
	/** undo에서 undo를 하기 전의 상태(redo실행결과)를 backup한다. 후에 redo에서 복원한다.*/
	void backUpForRedo(String charA, TextLine textOfBackupForUndo, Point undoPos, Object addedInfo) {
		if (charA==null) return;
		
		if (charA.equals("cut")) {
			TextLine newText=null;
			newText = TextArrayToText(undoPos.y, undoPos.x, 1);
			int numOfNewLineChar = getNumOfNewLineChar((TextLine)addedInfo) + 1;
			redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA, addedInfo);
			
		}
		else if (charA.equals("paste")) {
			TextLine strAddedInfo = (TextLine)addedInfo;
			// numOfNewLineChar = 선택텍스트의 newLineChar개수 + 원래의 1줄
			int numOfNewLineChar = getNumOfNewLineChar(strAddedInfo) + 1;
			TextLine newText=null;
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
					TextLine newText=null;
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
					TextLine newText=null;
					newText = TextArrayToText(undoPos.y, undoPos.x, 1);
					redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA);				
				}
			}
			else if (charA.equals(DeleteChar)) { // 마지막열에서 '\n'이 지워지는 delete키만
				TextLine newText=null;
				newText = TextArrayToText(undoPos.y, undoPos.x, 1);
				redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA);
			}
			else if (charA.equals(NewLineChar)) {
				TextLine newText=null;
				newText = TextArrayToText(undoPos.y, undoPos.x, 2);
				redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA);
			}
		}//else if (charA.equals(DeleteChar) || charA.equals(NewLineChar) ||
		//charA.equals(BackspaceChar))
		
		else  {	// 일반적인 경우
			TextLine newText=null;
			newText = TextArrayToText(undoPos.y, undoPos.x, 1);
			redoBuffer.push(new Point(undoPos.x,undoPos.y), newText, charA);
		}
	}
	
	/** text내에서 '\n'의 개수를 리턴한다.*/
	int getNumOfNewLineChar(TextLine text) {
		int i;
		int r = 0;
		for (i=0; i<text.count; i++) {
			if (text.characters[i].charA=='\n') {
				r++;
			}
		}
		return r;
	}
	
	/** initCursorPos에서 text를 더했을때 커서위치를 구한다.*/
	Point getRelativeCursorPos(Point initCursorPos, TextLine text) {
		int i;
		Point r = new Point(initCursorPos.x,initCursorPos.y);
		for (i=0; i<text.count; i++) {
			if (text.characters[i].charA=='\n') {
				r.y++;
				r.x = 0;
			}
			else if (text.characters[i].charA=='\r') {
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
		
		int index = pair.text.toString().indexOf("-");
		String textToFind = pair.text.subTextLine(0, index).toString();
		String textToReplaceWith = pair.text.subTextLine(index+1, pair.text.count).toString();
		
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
		
		if (command.equals("replace")) {			
			
			// replace-find시에 대체한 위치를 textToFind로 바꾼다.
			replaceCommon(isAll, new Point(replacePosP1_x, replacePosP1_y), 
					new Point(replacePosP2_x, replacePosP2_y), 
					textToReplaceWith, textToFind);
		}
		else {
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
			setVScrollBar(true);
			if (scrollMode==ScrollMode.Both) {
				setHScrollPos();
				setHScrollBar(true);	
			}
			
			this.isFound = false;
		}//replaceAll
	}
	
	void createTextView() {
		int viewWidth = view.getWidth(); 
		int viewHeight = view.getHeight();
		int x, y, w, h;
		
		w = (int) (viewWidth * 0.8f);
		h = (int) (viewHeight * 0.5f);
		x = viewWidth/2 - w/2;
		y = viewHeight/2 - h/2;
		Rectangle boundsOfTextView = new Rectangle(x,y,w,h);
		//public TextView(boolean hasToolbarAndMenuFontSize,
		//		boolean isDockingOfToolbarFlexiable, Object owner, String name,
		//		Rectangle paramBounds, float fontSize, boolean isSingleLine,
		//		CodeString text, ScrollMode scrollMode, int backColor) {
		textView = new TextView(false, false, this, null, boundsOfTextView, 
				view.getHeight()*0.03f, false, null, EditText.ScrollMode.VScroll, Compiler.backColor);
		textView.isReadOnly = true;
	}
	
	
	void showUndoBuffer() {
		CodeString message = new CodeString("<UndoBuffer Stack>\ncount   buffer(the contents to change) : command(Input key)\n(bottom)\n", 
				Compiler.keywordColor);
		int i;
		for (i=0; i<undoBuffer.bufferCommand.count; i++) {	
			String buffer = new String(undoBuffer.buffer.getItem(i).toCharArray());
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
			String buffer = new String(redoBuffer.buffer.getItem(i).toCharArray());
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
		
		if (undoBuffer.buffer.count>0) {
			isModified = true;
			
			isSelecting = false;
			
			UndoBuffer.Pair pair = undoBuffer.pop();
			TextLine newLineText = pair.text;
			
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
				// 커서위치는 undo(), undo_replace(), redo(), redo_replace(), replace()에서 
				// 설정하지 않고 replaceCommon()에서 공통적으로 설정한다. 
				// 위 함수들이 공통적으로 replaceCommon()을 호출하기 때문이다.
				
				// replace, replaceAll만 여기에서 백업한다.
				backUpForRedo(command, newLineText, pair.cursorPos, pair.isSelecting, pair.addedInfo, 
						pair.listOfFindPos, pair.listOfReplacePos);
			}
			else if (command.equals("cut")) {
				int numToDelete = getNumOfLinesInText(pair.cursorPos.y, 0, 1);
				int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
				if (newLineText.count>0 && newLineText.characters[newLineText.count-1].charA=='\n') numOfNewLineChar--;
				
				setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
				Point relativeCursorPos = this.getRelativeCursorPos(pair.cursorPos, (TextLine)pair.addedInfo);
				cursorPos.x = relativeCursorPos.x;
				cursorPos.y = relativeCursorPos.y;
			}
			else if (command.equals("paste")) {
				TextLine addedInfo = (TextLine)pair.addedInfo;
				// numOfNewLineCharToDelete = 선택텍스트의 newLineChar개수 + 원래의 1줄
				int numOfNewLineCharToDelete = getNumOfNewLineChar(addedInfo) + 1;
				int numToDelete = getNumOfLinesInText(pair.cursorPos.y, 0, numOfNewLineCharToDelete);
				int numOfNewLineChar = getNumOfNewLineChar(newLineText);
				setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
				cursorPos.x = pair.cursorPos.x;
				cursorPos.y = pair.cursorPos.y;
			}
			else if (command.equals(BackspaceChar) || command.equals(NewLineChar) || command.equals(DeleteChar))
			{
				// backspace(0열)와 delete(마지막열)는 모두 특별하게 '\n'을 제거한 경우이거나, 그렇지 않은 경우이다.
				if (command.equals(BackspaceChar)) {
					if (pair.isSelecting) {
						int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
						if (newLineText.count>0 && newLineText.characters[newLineText.count-1].charA=='\n') {
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
						if (scrollMode==ScrollMode.Both) { // 2줄이 1줄로 바뀐경우이므로 1줄을 삭제하고 원래의 2줄로 바꾼다.
							if (pair.cursorPos.x==0) {// 2줄이 1줄로 바뀐경우이므로 1줄을 삭제하고 원래의 2줄로 바꾼다.
								int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
								if (newLineText.count>0 && newLineText.characters[newLineText.count-1].charA=='\n') {
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
								if (newLineText.count>0 && newLineText.characters[newLineText.count-1].charA=='\n') {
									numOfNewLineChar--;
								}
								setTextMultiLine(pair.cursorPos.y, newLineText, 1, numOfNewLineChar);
								cursorPos.x = pair.cursorPos.x;
								cursorPos.y = pair.cursorPos.y;
							}
						}
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
						if (newLineText.count>0 && newLineText.characters[newLineText.count-1].charA=='\n') {
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
							if (newLineText.count>0 && newLineText.characters[newLineText.count-1].charA=='\n') {
								numOfNewLineChar--;
							}
							
							int cursorPosX, cursorPosY;
							if (pair.cursorPos.x<=textArray[pair.cursorPos.y].count-1 && 
								textArray[pair.cursorPos.y].characters[pair.cursorPos.x].charA!='\n') {
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
		}
	}
	
	void redo_replace(RedoBuffer.Pair pair) {
		String command = pair.command;
		
		Point curFindPosLocal = pair.cursorPos;
		
		int index = pair.text.toString().indexOf("-");
		String textToFind = pair.text.subTextLine(0, index).toString();
		String textToReplaceWith = pair.text.subTextLine(index+1, pair.text.count).toString();
		
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
		
		if (command.equals("replace")) {
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
					changeSelectP1AndP2(true, isForward, p1, p2, textToReplaceWith);
				}
				
				changeListFindPos(listFindPos, i+2, p1.y, textToFind, textToReplaceWith);
				
			}
			if (!isScopeAll) makeSelectIndices(true, selectP1, selectP2);
			
			backUpForUndo_replace("replaceAll", isAll, isForward, isScopeAll, isCaseSensitive, 
					isWholeWord, curFindPosLocal, textToFind, textToReplaceWith, listBackupForFindPos, listReplacePos);
			
			setVScrollPos();
			setVScrollBar(true);
			if (scrollMode==ScrollMode.Both) {
				setHScrollPos();
				setHScrollBar(true);	
			}
			this.isFound = false;
		}//replaceAll
		
	}
	
	/** undo를 한 뒤에 호출, back, delete, enter키의 조작 무효를 다시 실행한다.*/
	void redo() {
		if (isReadOnly) return;
		
		if (redoBuffer.buffer.count>0) {
			isSelecting = false;
			
			isModified = true;
			RedoBuffer.Pair pair = redoBuffer.pop();
			TextLine newLineText = pair.text;
			
			String command = pair.command;
			
			if (command.equals("cut")) {
				backUpForUndo(command, pair);
				
				int numToDelete;
				//int numOfNewLineChar = ((Integer)pair.addedInfo).intValue();
				int numOfNewLineChar = getNumOfNewLineChar((TextLine)pair.addedInfo) + 1;
				numToDelete = getNumOfLinesInText(pair.cursorPos.y, cursorPos.x, numOfNewLineChar);
				setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, 1);
				cursorPos.x = pair.cursorPos.x;
				cursorPos.y = pair.cursorPos.y;
			}
			else if (command.equals("paste")) {
				backUpForUndo(command, pair);
				
				int numToDelete;
				numToDelete = getNumOfLinesInText(pair.cursorPos.y, cursorPos.x, 1);
				int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
				if (newLineText.count>0 && newLineText.characters[newLineText.count-1].charA=='\n') {
					numOfNewLineChar--;
				}
				setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
				
				Point relativeCursorPos = this.getRelativeCursorPos(pair.cursorPos, (TextLine)pair.addedInfo);
				cursorPos.x = relativeCursorPos.x;
				cursorPos.y = relativeCursorPos.y;				
			}
			else if (command.equals("replace") || command.equals("replaceAll")) {
				if (command.equals("replace")) {
					redo_replace(pair);
				}
				else {	// replaceAll
					// replaceAll일 경우 
					// redo 를 undo 하기 위한 백업은 redo_replace(pair)에서 한다.
					redo_replace(pair);
				}
								
				//커서위치는 undo(), undo_replace(), redo(), redo_replace(), replace()에서 
				// 설정하지 않고 replaceCommon()에서 공통적으로 설정한다. 
				// 위 함수들이 공통적으로 replaceCommon()을 호출하기 때문이다.
				//cursorPos.x = pair.cursorPos.x;
				//cursorPos.y = pair.cursorPos.y;
			}
			
			else if (command.equals(BackspaceChar) || command.equals(NewLineChar) || command.equals(DeleteChar))
			{
				// backspace와 delete는 모두 특별하게 '\n'을 제거한 경우이다.
				if (command.equals(BackspaceChar)) { // 0열에서 '\n'이 지워지는 back키만
					if (pair.isSelecting) {
						backUpForUndo(BackspaceChar, pair);
						
						TextLine selectedText = (TextLine) pair.addedInfo; 
						int numToDelete;
						if (selectedText.count>0 && selectedText.characters[selectedText.count-1].charA=='\n') {
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
				else if (command.equals(DeleteChar)) { // 마지막열에서 '\n'이 지워지는 delete키만
					if (pair.isSelecting) {
						backUpForUndo(DeleteChar, pair);
						
						TextLine selectedText = (TextLine) pair.addedInfo; 
						int numToDelete;
						if (selectedText.count>0 && selectedText.characters[selectedText.count-1].charA=='\n') {
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
					int numOfNewLineChar = getNumOfNewLineChar(newLineText) + 1;
					/*if (newLineText.characters[newLineText.count-1].charA=='\n') {
						numOfNewLineChar--;
					}*/
					setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, numOfNewLineChar);
					cursorPos.x = 0;
					cursorPos.y = pair.cursorPos.y+1;
					
				}
				
			}//else if (command.equals(BackspaceChar) || command.equals(NewLineChar) || command.equals(DeleteChar))
			
			else {		// 일반적인 경우
				// a를 redo한 것을 undo하기 위해 백업
				backUpForUndo(command, pair);
				
				int numToDelete;
				numToDelete = getNumOfLinesInText(pair.cursorPos.y, cursorPos.x, 1);
				setTextMultiLine(pair.cursorPos.y, newLineText, numToDelete, 1);
				cursorPos.x = pair.cursorPos.x+1;
				cursorPos.y = pair.cursorPos.y;
			}
			
			
			
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
		String text = textToFind + "-" + textToReplaceWith;
		if (command.equals("replace")) {			
			undoBuffer.push(new Point(curFindPosLocal.x,curFindPosLocal.y), new TextLine(text,fontSize), command, addedInfo);
		}
		else if (command.equals("replaceAll")) {
			undoBuffer.push(new Point(curFindPosLocal.x,curFindPosLocal.y), new TextLine(text,fontSize), command, addedInfo, listOfFindPos, listOfReplacePos);
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
		TextLine text = new TextLine(textToFind + "-" + textToReplaceWith, fontSize);
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
			TextLine copiedText = new TextLine();
			
			copiedText = (TextLine) pair.addedInfo;
			
			//int firstLine = selectIndices[0].y;
			int firstLine = pair.cursorPos.y;
			int numOfNewLineChar = getNumOfNewLineChar(copiedText) + 1;
			//int this.getNumOfLinesInText(firstLine, 0, numOfNewLineChar);
			TextLine newText = TextArrayToText(firstLine, 0, numOfNewLineChar);
			Object addedInfo = copiedText;
			undoBuffer.push(new Point(pair.cursorPos.x,firstLine), newText, charA, addedInfo);
		
		}
		else if (charA.equals("paste")) {	// paste를 undo
			TextLine textForBackup = TextArrayToText(pair.cursorPos.y, 0, 1);
			//Object addedInfo = copiedText;
			undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), textForBackup, charA, 
					pair.addedInfo);
		}
		
		else if (charA.equals(BackspaceChar) || charA.equals(NewLineChar) || charA.equals(DeleteChar))
		{
			if (charA.equals(BackspaceChar)) {
				if (pair.isSelecting) {
					
					// 선택된 줄들을 백업한다.
					TextLine textForBackup = (TextLine) pair.addedInfo;
					undoBuffer.push(new Point(pair.cursorPos.x, pair.cursorPos.y), textForBackup, charA, pair.p2, pair.isSelecting);
				}
				else {
					if (pair.cursorPos.x!=0) { // undo에서 일반적인 경우로 취급
						TextLine textForBackup = TextArrayToText(pair.cursorPos.y, 0, 1);
						undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), textForBackup, charA);
					}
					else {
						int prevLine;
						if (pair.cursorPos.y>0) {
							prevLine = pair.cursorPos.y-1;
							if (textArray[prevLine].count>0 && textArray[prevLine].characters[textArray[prevLine].count-1].charA=='\n') {
								// 이전라인과 현재라인 모두를 백업한다.(0열에서 '\n'이 지워지는 back키만)
								TextLine newText=null;
								newText = TextArrayToText(prevLine, pair.cursorPos.x, 2);
								undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), newText, charA);
								//setTextMultiLine(cursorPos.y, newText, -1);
							}
							else {	// scrollMode가 VScroll일 때만 
								TextLine textForBackup = TextArrayToText(prevLine, 0, 1);
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
					TextLine textForBackup = (TextLine) pair.addedInfo;
					undoBuffer.push(new Point(pair.cursorPos.x, pair.cursorPos.y), textForBackup, charA, pair.p2, pair.isSelecting);
					
				}
				else {
					if (pair.cursorPos.x<textArray[pair.cursorPos.y].count) {
						if (textArray[pair.cursorPos.y].characters[pair.cursorPos.x].charA!='\n') { // undo에서 일반적인 경우로 취급
							//undoBuffer.push(cursorPos, textArray[cursorPos.y]);
							TextLine textForBackup = TextArrayToText(pair.cursorPos.y, 0, 1);
							undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), textForBackup, charA);
						}
						else {	// 마지막열에서 '\n'이 지워지는 delete키만
							// 현재라인과 다음라인 모두를 백업한다.
							TextLine newText=null;
							newText = TextArrayToText(pair.cursorPos.y, pair.cursorPos.x, 2);
							undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), newText, charA);
						}
					}
					else {	// 지우는 문자가 '\n'이 아닌 경우, undo에서 일반적인 경우로 취급
						TextLine textForBackup = TextArrayToText(pair.cursorPos.y, 0, 1);
						undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), textForBackup, charA);
					}
				}
			}
			else if (charA.equals(NewLineChar)) {
				// 현재라인과 다음라인 모두를 백업한다.
				TextLine newText=null;
				newText = TextArrayToText(pair.cursorPos.y, pair.cursorPos.x, 1);
				undoBuffer.push(new Point(pair.cursorPos.x,pair.cursorPos.y), newText, charA);
			}
		}//else if (charA.equals(BackspaceChar) || charA.equals(NewLineChar) || charA.equals(DeleteChar))
		
		else  { // 일반적인 경우
			TextLine textForBackup = TextArrayToText(pair.cursorPos.y, 0, 1);
			undoBuffer.push(new Point(cursorPos.x,pair.cursorPos.y), textForBackup, charA);
		}
	}
	
	/** 사용자가 키를 조작하기 전의 상태를 backup한다. 
	 * undoBuffer의 cursorPos는 다음과 같다. 
	 * cut:(0,selectIndices[0].y),  
	 * 0열 back:(cursorPos.x,cursorPos.y),  
	 * 마지막열 delete:(cursorPos.x,cursorPos.y), 
	 * 일반적인경우:(cursorPos.x,cursorPos.y)*/
	void backUpForUndo(String charA, boolean isReplaceChar) {
		if (charA.equals("cut")) {
						
			int i;
			int y;
			int startX, endX;
			TextLine copiedText = new TextLine(0);
			for (i=0; i<selectIndicesCountForCopy; i+=2) {
				y = selectIndices[i].y;
				startX = selectIndices[i].x;
				endX = selectIndices[i+1].x;
				TextLine lineCopiedText = textArray[y].subTextLine(startX, endX+1);
				copiedText.insert(lineCopiedText.toCharacterArray(),0,
					copiedText.count,lineCopiedText.count);
			}
			
			int firstLine = selectIndices[0].y;
			int numOfNewLineChar = getNumOfNewLineChar(copiedText) + 1;
			//int this.getNumOfLinesInText(firstLine, 0, numOfNewLineChar);
			TextLine newText = TextArrayToText(firstLine, 0, numOfNewLineChar);
			Object addedInfo = copiedText;
			undoBuffer.push(new Point(selectIndices[0].x,firstLine), newText, charA, addedInfo);
			//Log.d("backUpForUndo", "newText:"+newText);
		
		}
		else if (charA.equals("paste")) {	// paste를 undo
			TextLine textForBackup = TextArrayToText(cursorPos.y, 0, 1);
			Object addedInfo = copiedText;
			undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA, 
					addedInfo);
		}
		
		else if (charA.equals(DeleteChar) || charA.equals(NewLineChar) ||
				charA.equals(BackspaceChar)) {
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
					TextLine textForBackup = TextArrayToText(p1.y, 0, p2.y-p1.y+1);
					//undoBuffer.push(new Point(p1.x, p2.y), textForBackup);
					//undoBuffer.push(new Point(p1.x, p1.y), textForBackup, charA, null, isSelecting);
					undoBuffer.push(p1, textForBackup, charA, p2, isSelecting);
				}
				else {
					if (cursorPos.x!=0) { // undo에서 일반적인 경우로 취급
						TextLine textForBackup = TextArrayToText(cursorPos.y, 0, 1);
						undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA);
					}
					else {
						int prevLine;
						if (cursorPos.y>0) {
							prevLine = cursorPos.y-1;
							if (textArray[prevLine].count>0 && textArray[prevLine].characters[textArray[prevLine].count-1].charA=='\n') {
								// 이전라인과 현재라인 모두를 백업한다.(0열에서 '\n'이 지워지는 back키만)
								TextLine newText=null;
								newText = TextArrayToText(prevLine, cursorPos.x, 2);
								undoBuffer.push(new Point(cursorPos.x,cursorPos.y), newText, charA);
								//setTextMultiLine(cursorPos.y, newText, -1);
							}
							else {	// scrollMode가 VScroll일 때만 
								TextLine textForBackup = TextArrayToText(prevLine, 0, 1);
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
					TextLine textForBackup = TextArrayToText(p1.y, 0, p2.y-p1.y+1);
					//undoBuffer.push(new Point(p1.x, p2.y), textForBackup);
					//undoBuffer.push(new Point(p1.x, p1.y), textForBackup, charA, null, isSelecting);
					undoBuffer.push(p1, textForBackup, charA, p2, isSelecting);
				}
				else {
					if (cursorPos.x<textArray[cursorPos.y].count) {
						if (textArray[cursorPos.y].characters[cursorPos.x].charA!='\n') { // undo에서 일반적인 경우로 취급
							//undoBuffer.push(cursorPos, textArray[cursorPos.y]);
							TextLine textForBackup = TextArrayToText(cursorPos.y, 0, 1);
							undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA);
						}
						else {	// 마지막열에서 '\n'이 지워지는 delete키만
							// 현재라인과 다음라인 모두를 백업한다.
							TextLine newText=null;
							newText = TextArrayToText(cursorPos.y, cursorPos.x, 2);
							undoBuffer.push(new Point(cursorPos.x,cursorPos.y), newText, charA);
						}
					}
					else {	// 지우는 문자가 '\n'이 아닌 경우, undo에서 일반적인 경우로 취급
						TextLine textForBackup = TextArrayToText(cursorPos.y, 0, 1);
						undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA);
					}
				}
			}
			else if (charA.equals(NewLineChar)) {
				// 현재라인과 다음라인 모두를 백업한다.
				TextLine newText=null;
				newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);
				undoBuffer.push(new Point(cursorPos.x,cursorPos.y), newText, charA);
			}
		}//else if (charA.equals(DeleteChar) || charA.equals(NewLineChar) ||
		//charA.equals(BackspaceChar)) 
		/*else  {
			TextLine textForBackup = TextArrayToText(cursorPos.y, 0, 1);
			undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup);
		}*/
		else {// 일반적인 경우
			if (isReplaceChar) {
				TextLine textForBackup = TextArrayToText(cursorPos.y, 0, 1);
				undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA);
			}
			else {
				TextLine textForBackup = TextArrayToText(cursorPos.y, 0, 1);
				undoBuffer.push(new Point(cursorPos.x,cursorPos.y), textForBackup, charA);
			}
		}
	}
	
	/** space, delete, enter 키는 Hangul.mode가 None이다.
	 그리고 isNextToCursor는 true로 설정되어 addChar로 커서다음위치에 
	 key가 추가된다. 이 메서드는 일반문자(영어, 숫자, 특수문자등)를 대상으로 한다.
	 TextArrayToTextOneLine, setTextOneLine에 의해 \n을 만날 때까지의 
	 text만 대상으로 하여 성능을 향상한다.*/
	void addCharReally(String charA/*, boolean isNextToCursor*/) {
		isModified = true;
		
		backUpForUndo(charA, false);
		// redo 를 무효화한다. redoBuffer를 모두 지워야 한다. 
		// redo 를 무효로 만들지 않으면 undo-redo 시스템의 오류가 발생한다.
		redoBuffer.reset();
		
		if (charA.equals(IntegrationKeyboard.Space)) charA = " ";
		if (textArray[cursorPos.y]==null) {
			textArray[cursorPos.y] = new TextLine(0,initFontSize);
		}
		
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
		
		/*if (!isNextToCursor) {			
			cursorPosX = cursorPos.x + len - 1;
		}
		
		else {
			//키보드 모드가 한글인 상태에서 'ㄱ', 'ㅏ'를 순서대로 눌러 '가'를 완성한 후 
			// 엔터키를 칠 경우 cursorPos.x는 '가'를 가르키므로 엔터키가 '가'다음에 오도록 한다.
			if (this.keyboardMode==IntegrationKeyboard.Mode.Hangul) {
				cursorPosX = cursorPos.x + len;
			}
			else {
				cursorPosX = cursorPos.x + len - 1;
			}
		}*/
		cursorPosX = cursorPos.x + len - 1;
		
				
		if (this.isSelecting && 
				(charA.equals(DeleteChar) || charA.equals(BackspaceChar))) {
			this.deleteSelectedText();
		}
		else {
		
			try {		 
			textArray[cursorPos.y].resize(textArray[cursorPos.y].count+charA.length());
			TextLine charATextLine = (new TextLine(charA, fontSize));
			int i;
			for (i=0; i<charATextLine.count; i++) {
				charATextLine.characters[i].typeface = typeface;
				charATextLine.characters[i].typefaceName = typefaceName;
				charATextLine.characters[i].isUnderLine = isUnderline;
				charATextLine.characters[i].isBold = isBold;
				charATextLine.characters[i].isItalic = isItalic;
				charATextLine.characters[i].setCharColor(curColor);
			}
			Character[] src = charATextLine.characters;
			//textArray[cursorPos.y].insert(src, 0, cursorPosX, charA.length());
			textArray[cursorPos.y].insert(src, 0, cursorPosX, charA.length());
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
			
			if (!charA.equals(DeleteChar) && !charA.equals(NewLineChar) &&
					!charA.equals(BackspaceChar)) {
				TextLine newText=null;
				newText = TextArrayToText(cursorPos.y, cursorPosX, 1);
				setTextMultiLine(cursorPos.y, newText, -1, 1);			
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
					TextLine newText = TextArrayToText(cursorPos.y, 0, cursorPos.y, cursorPosX);		
					setText(cursorPos.y, newText);
				}
			}
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
			int numOfLinesToDelete = getNumOfNewLineChar(p1, p2) + 1;
			
			TextLine headText = textArray[curLine];
			headText = headText.subTextLine(0, p1.x);
			int oldHeadTextLen = headText.count;
			
			TextLine tailText = TextArrayToText(p2.y, p2.x, 1);
			if (tailText!=null && tailText.count>0) // null도 아니고 빈스트링도 아니면
				tailText = tailText.subTextLine(p2.x+1, tailText.count);
			
			
			// headText에 tailText를 연결한다.
			headText.resize(headText.count+tailText.count);
			headText.insert(tailText.characters, 0, oldHeadTextLen, tailText.count);
			
			if (numOfLinesToDelete==1 && headText.equals("") && p2.y<this.numOfLines-1) {
				headText = new TextLine("\n", textArray[curLine].characters[0].size);
			}
			else if (textArray[selectP1.y].characters[selectP1.x].charA=='\n' && 
					textArray[selectP2.y].characters[selectP2.x].charA=='\n') {
				headText.insert(headText.count, new TextLine("\n", initFontSize));
			}
			
			setTextMultiLine(curLine, headText, numOfLinesToDelete, 1);
			
			isSelecting = false;
			
			cursorPos.x = p1.x;
			cursorPos.y = p1.y;
		}
	}
	
	void addDeleteChar() {
		isModified = true;
		// cursorPos는 %dl을 가리키므로 cursorPos.x는 %를 가리킨다.
		if (cursorPos.x+3<textArray[cursorPos.y].count) {
			// %dl문자 뒤의 문자
			TextLine curChar = textArray[cursorPos.y].subTextLine(cursorPos.x+3,cursorPos.x+3+1);
			if (!(curChar.equals("\n"))) {
				TextLine newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);		
				setTextMultiLine(cursorPos.y, newText, -1, 1);
			}
			else {
				int nextLine = cursorPos.y + 1;
				int curLine = cursorPos.y;
				if (nextLine<numOfLines) {
					if (scrollMode==ScrollMode.VScroll) {
						int numOfLinesToDelete;
						TextArrayToText(curLine, 0, 2);
						numOfLinesToDelete = numOfLinesInText + 1;
						
					
						TextLine newCurLine = deleteNewLineChar(textArray[curLine]);
						// %dl를 제거한다.
						newCurLine = newCurLine.subTextLine(0, cursorPos.x);
						Character[] charArrayNewCurLine = newCurLine.toCharacterArray();
						int newCursorX = newCurLine.count;
						
						TextLine nextLineText = TextArrayToText(nextLine, 0, 1);
						
						//charArrayNewCurLine = Array.Resize(charArrayNewCurLine, 
						//		charArrayNewCurLine.length+nextLineText.count);
						Character[] charArrayNextLine = nextLineText.toCharacterArray();

						try {
							charArrayNewCurLine = Array.Insert(charArrayNextLine, 0, 
									charArrayNewCurLine, newCursorX, 
									charArrayNextLine.length);
						}catch(Exception e) {
							e.printStackTrace();
							CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
						}
						
						int oldVScrollPos = vScrollPos;
						
												
						newCurLine = new TextLine(charArrayNewCurLine);
						setTextMultiLine(curLine, newCurLine, numOfLinesToDelete, 1);
						
						cursorPos.x = newCursorX;
						cursorPos.y = curLine;
						vScrollPos = oldVScrollPos;
						setVScrollPos();
						setVScrollBar(true);
					}
					else {	
						int numOfLinesToDelete = 2;
						TextLine newCurLine = deleteNewLineChar(textArray[curLine]);
						// %dl를 제거한다.
						newCurLine = newCurLine.subTextLine(0, cursorPos.x);
						Character[] charArrayNewCurLine = newCurLine.toCharacterArray();						
						int newCursorX = newCurLine.count;
						
						TextLine nextLineText = textArray[nextLine];
						
						//charArrayNewCurLine = Array.Resize(charArrayNewCurLine, 
						//		charArrayNewCurLine.length+nextLineText.count);
						Character[] charArrayNextLine = nextLineText.toCharacterArray();
						
						try {
							charArrayNewCurLine = Array.Insert(charArrayNextLine, 0, 
									charArrayNewCurLine, newCursorX, 
									charArrayNextLine.length);
						}catch(Exception e) {
							e.printStackTrace();
							CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
						}
						
						int oldHScrollPos = widthOfhScrollPos;
						int oldVScrollPos = vScrollPos;
						
												
						newCurLine = new TextLine(charArrayNewCurLine);
						setTextMultiLine(curLine, newCurLine, numOfLinesToDelete, 1);
						
						cursorPos.x = newCursorX;
						cursorPos.y = curLine;
						widthOfhScrollPos = oldHScrollPos;
						vScrollPos = oldVScrollPos;
						setVScrollPos();
						setHScrollPos();
						setVScrollBar(true);
						setHScrollBar(true);
					}
				} // if (nextLine<numOfLines) {
				else {
					TextLine newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);		
					setTextMultiLine(cursorPos.y, newText, -1, 1);
				}
			} // else

		}
		else {	// if (cursorPos.x+3<textArray[cursorPos.y].length()) {
			// cursorPos는 %dl을 가리키므로 cursorPos.x는 %를 가리킨다.
			// %dl를 제거한다.
			if (scrollMode==ScrollMode.Both) {
				textArray[cursorPos.y] = textArray[cursorPos.y].subTextLine(0, cursorPos.x);
			}
			else { // 다음 줄의 첫문자를 지운다.
				TextLine newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);		
				setTextMultiLine(cursorPos.y, newText, -1, 1);
			}
		}
	}

	/**엔터키를 입력하였으므로 마지막 줄수를 100줄 더 늘린다.
		NullPointerException 이나 getCursorPos()에서 에러를 발생시킬수 있으므로.*/
	void addEnterChar(/*boolean isNextToCursor*/) {
		isModified = true;
		
		int cursorPosX;
		/*if (isNextToCursor) cursorPosX = cursorPos.x + 1;
		else cursorPosX = cursorPos.x;*/
		cursorPosX = cursorPos.x;
		
		TextLine strCurLine = TextArrayToText(cursorPos.y, cursorPosX, 2);
		//TextLine strRemainder = strCurLine.subTextLine(cursorPos.x+1, strCurLine.count);
		
		int curLine = cursorPos.y;
		
		//TextLine newText1 = TextArrayToText(cursorPos.y, cursorPos.x, 1);
		//newText1.insert(newText1.count, new TextLine("\n", initFontSize));
		
		TextLine newText1 = strCurLine;
		int numOfLinesToDelete = this.getNumOfLinesInText(cursorPos.y, 0, 2);
		setTextMultiLine(cursorPos.y, newText1, numOfLinesToDelete, 1);
		
		
		// 엔터키를 입력하였으므로 마지막 줄수를 100줄 더 늘린다.
		// NullPointerException 이나 getCursorPos()에서 에러를 발생시킬수 있으므로.
		textArray = Array.Resize(textArray, numOfLines+100);
				
		//textArray[curLine+1] = strRemainder;

		
		cursorPos.y = curLine + 1;
		cursorPos.x = 0;
		
	}
	
	void addBackspaceChar() {
		isModified = true;
		
		if (this.isSelecting) {
			this.deleteSelectedText();
			return;
		}
		
		if (cursorPos.x!=0) { 
			TextLine newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);		
			setTextMultiLine(cursorPos.y, newText, -1, 1);
			
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
				
					TextLine newPrevLine;
					if (textArray[prevLine].characters[textArray[prevLine].count-1].charA=='\n') {
						newPrevLine = deleteNewLineChar(textArray[prevLine]);
					}
					else {
						newPrevLine = textArray[prevLine].subTextLine(0,
								textArray[prevLine].count-1);
					}
					Character[] charArrayNewPrevLine = newPrevLine.toCharacterArray();
					TextLine curLineText = TextArrayToText(cursorPos.y, cursorPos.x, 1);
					// \bk를 제거한다.
					curLineText = curLineText.subTextLine(3, curLineText.count);
					
					Character[] charArrayCurLine = curLineText.toCharacterArray();
					int newCursorX = newPrevLine.count;
					try {
						charArrayNewPrevLine = Array.Insert(charArrayCurLine, 0, 
								charArrayNewPrevLine, newCursorX, 
								charArrayCurLine.length);
					}catch(Exception e) {
						e.printStackTrace();
						CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
					}
										
					// newPrevLine은 새로운 text 가 된다.					
					newPrevLine = new TextLine(charArrayNewPrevLine);
					setTextMultiLine(prevLine, newPrevLine, numOfLinesToDelete, 1);
										
					cursorPos.x = newCursorX;
					cursorPos.y = prevLine;
					
					//setVScrollPos();
					//setVScrollBar(true);
				}//if (scrollMode==ScrollMode.VScroll) {
				else {	// 당연히 이전 줄은 newline으로 끝난다.
					int numOfLinesToDelete = 2;
					TextLine newPrevLine = deleteNewLineChar(textArray[prevLine]);
					Character[] charArrayNewPrevLine = newPrevLine.toCharacterArray();
					TextLine curLineText = textArray[cursorPos.y];
					// \bk를 제거한다.
					curLineText = curLineText.subTextLine(3, curLineText.count);
					//charArrayNewPrevLine = Array.Resize(charArrayNewPrevLine, 
					//		charArrayNewPrevLine.length+curLineText.count);
					Character[] charArrayCurLine = curLineText.toCharacterArray();
					int newCursorX = newPrevLine.count;
					try {
						charArrayNewPrevLine = Array.Insert(charArrayCurLine, 0, 
								charArrayNewPrevLine, newCursorX, 
								charArrayCurLine.length);
					}catch(Exception e) {
						e.printStackTrace();
						CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
					}
					
					newPrevLine = new TextLine(charArrayNewPrevLine);
					setTextMultiLine(prevLine, newPrevLine, numOfLinesToDelete, 1);
					
					cursorPos.x = newCursorX;
					cursorPos.y = prevLine;
					//setVScrollPos();
					//setHScrollPos();
					//setVScrollBar(true);
					//setHScrollBar(true);
				}//ScrollMode.Both
			} // if (prevLine>=0)
			else {
				TextLine newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);		
				setTextMultiLine(cursorPos.y, newText, -1, 1);
			}//if (prevLine<0)
			
		}//if (cursorPos.x==0) { 
	}

	
	/**한글완성중일 때 이 메서드를 사용한다. 
	 * redoBuffer를 초기화시켜야 redoBuffer에 남아있는 상태에서 키를 입력하고 나서 
	 * 다시 redo 를 하면 발생하는 오류를 해결할 수 있다.*/
	public void replaceChar(String charA) {
		redoBuffer.reset();
		
		//if (charA==null || charA.equals("")) return;
		if (charA.equals(IntegrationKeyboard.Delete)) {	// BackSpace
			charA = DeleteChar;				
		}	// BackSpace
		else if (charA.equals(IntegrationKeyboard.Enter)) {
			charA = NewLineChar;
		}
		replaceCharReally(charA);		
	}
	
	/**한글이 완성중일때만 동작한다. TextArrayToTextOneLine, setTextOneLine에 의해 \n을 만날 때까지의 
	 text만 대상으로 하여 성능을 향상한다.*/	
	void replaceCharReally(String charA) {
		backUpForUndo(charA, true);
		// redo 를 무효화한다. redoBuffer를 모두 지워야 한다. 
		// redo 를 무효로 만들지 않으면 undo-redo 시스템의 오류가 발생한다.
		redoBuffer.reset();
		
		if (charA.equals(IntegrationKeyboard.Space)) charA = " ";
		
		try {
		textArray[cursorPos.y].delete(cursorPos.x, 1);
		}catch(Exception e) {
			int a;
			a=0;
			a++;
		}
		textArray[cursorPos.y].setLineHeight(30);
		
		int charALen = charA.length();
		
		try {
		if (charALen!=0) {		// charA==0이면 insert가 필요없다.
			textArray[cursorPos.y].resize(textArray[cursorPos.y].count+charA.length());
			TextLine charATextLine = (new TextLine(charA, fontSize));
			int i;
			for (i=0; i<charATextLine.count; i++) {
				charATextLine.characters[i].typeface = typeface;
				charATextLine.characters[i].typefaceName = typefaceName;
				charATextLine.characters[i].isUnderLine = isUnderline;
				charATextLine.characters[i].isBold = isBold;
				charATextLine.characters[i].isItalic = isItalic;
				charATextLine.characters[i].setCharColor(curColor);
			}
			Character[] src = charATextLine.characters;
			textArray[cursorPos.y].insert(src, 0, cursorPos.x, charA.length());
			//textArray[cursorPos.y].setLineHeight(30);
		}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
		Point oldCursorPos=null;
		if (charALen==0) {	//  도ㄹ에서 ㄹ을 ""으로 만드는 backspace키가 눌릴 때 현재 커서위치를 저장한다.
			isBkSpThatMakeNullStr = true;
			charALen = 1;	// 현재 커서를 가리키도록 한다.
			oldCursorPos = new Point(cursorPos.x, cursorPos.y);
			
		}
		TextLine newText=null;
		newText = TextArrayToText(cursorPos.y, cursorPos.x+charALen-1, 1);
		setTextMultiLine(cursorPos.y, newText, -1, 1);
		
		if (isBkSpThatMakeNullStr) {
			cursorPos.x = oldCursorPos.x;
			cursorPos.y = oldCursorPos.y;
			isBkSpThatMakeNullStr = false;
		}
		
		
	}	
	
	public void initialize() {
		initCursorAndScrollPos();
		this.keyboardMode = IntegrationKeyboard.Mode.Math;
		TextLine text = new TextLine(0,0);
		setText(0,text);
		setToolbarAndCurState(cursorPos);
		setBackColor(backColor);
		this.descent = fontSize * descentRate;
		isSelecting = false;
		undoBuffer.reset();
		redoBuffer.reset();
	}
	
	public void initCursorAndScrollPos() {
		cursorPos.x = 0;
		cursorPos.y = 0;
		if (scrollMode==ScrollMode.VScroll) {
			vScrollPos = 0;
			vScrollBar.setVScrollBar(heightOfLinesPerPage,
					
					heightOfLines, heightOfvScrollPos, heightOfvScrollInc);
		}
		else {
			vScrollPos = 0;
			//hScrollPos = 0;
			widthOfhScrollPos = 0;
			vScrollBar.setVScrollBar(heightOfLinesPerPage,
					
					heightOfLines, heightOfvScrollPos, heightOfvScrollInc);
			hScrollBar.setHScrollBar(widthOfCharsPerPage, 
					widthOfTotalChars, widthOfhScrollPos, widthOfhScrollInc);
		}
		setToolbarAndCurState(cursorPos);
	}
	
		
	
	/** startLine부터 textArray를 만든다.
	// 처음 생성자에서 addChar, replaceChar를 거치지 않고 직접 setText를 호출시 indexInText가
	// -1이므로 커서위치는 바뀌지 않는다. 따라서 scrollPos도 바뀌지 않는다.*/
	public synchronized void setText(int startLine, TextLine text) {
		if (text==null) return;
		if (startLine!=0) isModified = true;
		if (scrollMode==ScrollMode.VScroll) {
			setTextVScroll(startLine, text);			
			setVScrollPos();
			setVScrollBar(true);
		}		
		else if (scrollMode==ScrollMode.Both) {
			setTextScrollBoth(startLine, text);
			setVScrollPos();
			setHScrollPos();
			setVScrollBar(true);			
			setHScrollBar(true);
		}
		//queueOfSetText.add(new ArgOfSetText(true, startLine,text));
		//setModified(true);
		
	}
	
		
	/** startLine에서 시작하여 numOfLinesToDelete 개수 만큼 textArray에서 지우고
	 * 그 지워진 부분에 새로운 text 를 넣는다. 
	 * numOfLinesToDelete가 -1 이면 numOfLinesInText를 가지고 지워질 라인수를 산정한다.*/
	public void setTextMultiLine(int startLine, TextLine text, int numOfLinesToDelete, int numOfNewLineChar) {
		isModified = true;
		if (scrollMode==ScrollMode.VScroll) {
			setTextMultiLineVScroll(startLine, text, numOfLinesToDelete);
			setVScrollPos();
			setVScrollBar(true);
		}		
		else if (scrollMode==ScrollMode.Both) {
			try {
			setTextMultiLineBoth(startLine, text, numOfLinesToDelete);
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
			
			setVScrollPos();
			setHScrollPos();
			setVScrollBar(true);			
			setHScrollBar(true);
			
		}
	}
	
	/** 현재 입력한 문자 다음에 커서가 위치하도록 한다.
	 한글은 문자를 완성중일 때는 전진시키지 않는다. 즉 현재 완성중인 문자에 커서가 위치한다.*/
	public void setCursorPos(int lineNumber, int index, boolean isNewLine, 
			boolean isLineWidthNarrow, Point result) {
		// 문자를 완성중이거나 빈스트링을 만드는 BackSpace키일때는 커서를 전진시키지 않는다.
		if ((CommonGUI.keyboard.mode==Mode.Hangul && Hangul.mode!=Hangul.Mode.None)) {
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
					// BkSp는 한글이 아닐 때는 일반 문자로 동작한다.
					//if (!(keyboard.mode==Mode.Hangul && 
					//		Hangul.mode==Hangul.Mode.None && 
					//		Hangul.isBkSpPressed)) {
					//	result.y++;
					//	result.x = 0;
					//}
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
	
	TextLine[] setTextArrayNoSpaceError(TextLine[] textArray, int lineNumber, TextLine text) {
		if (lineNumber<textArray.length) {
			textArray[lineNumber] = text;
		}
		else {
			textArray = Array.Resize(textArray, lineNumber+10);
			textArray[lineNumber] = text;
		}
		return textArray;
			
	}
	
	boolean isNextCharBkSp(TextLine text, int i) {
		int textLen = text.count;
		TextLine nextCharA;
		nextCharA = text.subTextLine(i+1, i+2);
		if (nextCharA.equals("%")) {
			if (i+3<textLen) {
				TextLine str = text.subTextLine(i+2, i+4);
				if (str.equals("bk")) {
					return true;
				}						
			}
		}
		return false;
	}
	
	boolean isCurCharBkSp(TextLine text, int i) {
		TextLine charA = text.subTextLine(i, i+1);
		if (charA.equals("%")) {
			if (i+2<text.count) {
				TextLine str = text.subTextLine(i+1, i+3);
				if (str.equals("bk")) {
					return true;
				}
			}
		}
		return false;
	}
	
	boolean isCurCharDelete(TextLine text, int i) {
		TextLine charA = text.subTextLine(i, i+1);
		if (charA.equals("%")) {
			if (i+2<text.count) {
				TextLine str = text.subTextLine(i+1, i+3);
				if (str.equals("dl")) {
					return true;
				}
			}
		}
		return false;
	}
	
	// 수직스크롤바만 있는 경우, 즉 수평스크롤바가 없다.
	// cursorPos.y 는 startLine과 같다.
	public void setTextVScroll(int startLine, TextLine text) {
		if (text==null) return;
		numOfLines = startLine + 1;
		
		if (isSingleLine==false) {
			Point cursorPosLocal = new Point(0,startLine);
			
			int i;
			float lineWidth = 0;
			
			for (i=startLine; i<textArray.length; i++) {
				textArray[i] = new TextLine(0,initFontSize);
			}
			TextLine textTemp1 = new TextLine(0,0);
			TextLine textTemp2 = new TextLine(0,0);
			
			TextLine charA;
			boolean isDeleteChar = false;
			boolean isNextCharBkSp = false;
			boolean isCurCharBkSp = false;
			int textLen = text.count;			
			int selectIndexX=0, selectIndexY=0;
			for (i=0; i<textLen; i++) {
				if (makingSelectP1P2OutOfEvent) {
					if (i==selectP1Index) {
						selectP1Logical = new Point(selectIndexX,startLine+selectIndexY);
					}
					if (i==selectP2Index) {
						selectP2Logical = new Point(selectIndexX,startLine+selectIndexY);
					}
				}
				charA = text.subTextLine(i, i+1);
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
					if (charA.equals(NewLineChar)) {
						textTemp1.add(charA.characters[0]);
						textArray = setTextArrayNoSpaceError(textArray, numOfLines-1, textTemp1);
						numOfLines++;
						textArray = setTextArrayNoSpaceError(textArray, numOfLines-1, new TextLine(0,0));
						selectIndexY++;
						selectIndexX = 0;
						setCursorPos(startLine, i, true, false, cursorPosLocal);
						textTemp1 = new TextLine(0,0);
						textTemp2 = new TextLine(0,0);
						lineWidth = 0;
						// newline문자의 다음 줄을 newline문자의 size로 바꾼다.
						textArray[numOfLines-1].setLineHeight(charA.characters[0].size);
						
					}
					else  {
						textTemp1.add(charA.characters[0]);
						//lineWidth = paint.measureText(textTemp1);
						lineWidth += paint.measureText(charA);
						if (lineWidth > rationalBoundsWidth) {
							textArray = setTextArrayNoSpaceError(textArray, numOfLines-1, textTemp2);
							numOfLines++;
							textArray = setTextArrayNoSpaceError(textArray, numOfLines-1, new TextLine(0,0));
							selectIndexY++;
							selectIndexX = 0;
							setCursorPos(startLine, i, true, true, cursorPosLocal);
							textTemp1 = new TextLine(0,0);
							textTemp2 = new TextLine(0,0);
							lineWidth = 0;
							i--;	// 초과된 문자(다음줄의 첫문자)를 다시 처리
						}
						else {
							textTemp2.add(charA.characters[0]);
							//textArray[numOfLines-1].add(charA.characters[0]);
							textArray = setTextArrayNoSpaceError(textArray, numOfLines-1, textTemp2);
							setCursorPos(startLine, i, false, false, cursorPosLocal);
							selectIndexX++;
						}
					}
				}	// isDeleteChar == false;
				
			}		// for			
			
			for (i=startLine; i<numOfLines; i++) {
				if (textArray[i].count!=0) {
					textArray[i].setLineHeight(30);
				}
			}
			this.cursorPos = cursorPosLocal;
			
			//textArray = Array.Resize(textArray, numOfLines+100);
			
		} // if (!isSingleLine)
	}
	
	/** newline문자의 다음 줄을 newline문자의 size로 바꾼다.*/
	public void setTextScrollBoth(int startLine, TextLine text) {
		try{
		if (text==null) return;
		numOfLines = startLine + 1;
		
		if (isSingleLine==false) {			
			Point cursorPosLocal = new Point(0,startLine);
			TextLine textTemp1 = new TextLine(0,0);
			int i;
			for (i=startLine; i<textArray.length; i++) {
				textArray[i] = new TextLine(0,initFontSize);
			}
			
			TextLine charA;
			boolean isDeleteChar = false;
			boolean isNextCharBkSp = false;
			boolean isCurCharBkSp = false;
			int textLen = text.count;
			
			for (i=0; i<textLen; i++) {
				charA = text.subTextLine(i, i+1);
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
					if (charA.equals(NewLineChar)) {						
						textTemp1.add(charA.characters[0]);
						textArray = setTextArrayNoSpaceError(textArray, numOfLines-1, textTemp1);
						numOfLines++;
						textArray = setTextArrayNoSpaceError(textArray, numOfLines-1, new TextLine(0,0));
						setCursorPos(startLine, i, true, false, cursorPosLocal);
						textTemp1 = new TextLine(0,0);
						// newline문자의 다음 줄을 newline문자의 size로 바꾼다.
						textArray[numOfLines-1].setLineHeight(charA.characters[0].size);						
					}				
					else  {
						textTemp1.add(charA.characters[0]);
						textArray = setTextArrayNoSpaceError(textArray, numOfLines-1, textTemp1);
						setCursorPos(startLine, i, false, false, cursorPosLocal);
					}
				}	// isDeleteChar == false;
				
			}		// for
			
			this.cursorPos = cursorPosLocal;
			for (i=startLine; i<numOfLines; i++) {
				if (textArray[i].count!=0) {
					textArray[i].setLineHeight(30);
				}
			}
			//textArray = Array.Resize(textArray, numOfLines+100);
			
		} // if (!isSingleLine)
		}catch(Exception e) {
			
		}
	}
	
	/*public void setTextOneLineVScroll(int lineNumber, TextLine text, int numOfLinesToDelete) {
		if (text==null) return;
		int numOfLinesLocal = 1;
		
		if (isSingleLine==false) {
			TextLine[] textArrayLocal = new TextLine[10];
			Point cursorPosLocal = new Point(0,lineNumber);
			TextLine textTemp1 = new TextLine(0,0);
			TextLine textTemp2 = new TextLine(0,0);
			int i;
			float lineWidth;
			for (i=0; i<textArrayLocal.length; i++) {
				textArrayLocal[i] = new TextLine(0,initFontSize);
			}
			
			TextLine charA;
			boolean isDeleteChar = false;
			boolean isNextCharBkSp = false;
			boolean isCurCharBkSp = false;
			int textLen = text.count;
			
			for (i=0; i<textLen; i++) {
				charA = text.subTextLine(i, i+1);
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
					if (charA.equals(NewLineChar)) {
						textTemp1.add(charA.characters[0]);
						//textArrayLocal[numOfLinesLocal-1] = textTemp1;
						textArrayLocal = setTextArrayNoSpaceError(textArrayLocal, numOfLinesLocal-1, textTemp1);
						setCursorPos(lineNumber, i, true, false, cursorPosLocal);
						break;
					}				
					else  {
						textTemp1.add(charA.characters[0]);
						lineWidth = paint.measureText(textTemp1);
						if (lineWidth > rationalBoundsWidth) {
							//textArrayLocal[numOfLinesLocal-1] = textTemp2;
							textArrayLocal = setTextArrayNoSpaceError(textArrayLocal, numOfLinesLocal-1, textTemp2);
							numOfLinesLocal++;
							textArrayLocal = setTextArrayNoSpaceError(textArrayLocal, numOfLinesLocal-1, new TextLine(0,0));
							setCursorPos(lineNumber, i, true, true, cursorPosLocal);
							textTemp1 = new TextLine(0,0);
							textTemp2 = new TextLine(0,0);
							i--;	// 초과된 문자(다음줄의 첫문자)를 다시 처리
						}
						else {
							textTemp2.add(charA.characters[0]);
							//textArrayLocal[numOfLinesLocal-1] = textTemp2;
							textArray = setTextArrayNoSpaceError(textArray, numOfLinesLocal-1, textTemp2);
							setCursorPos(lineNumber, i, false, false, cursorPosLocal);
						}
					}
				}	// isDeleteChar == false;
				
			}		// for
			for (i=0; i<numOfLinesLocal; i++) {
				if (textArrayLocal[i].count!=0) {
					textArrayLocal[i].setLineHeight(30);
				}
			}
			try {
				//this.numOfLines += numOfLinesLocal - numOfLinesInText;
				//textArray = Array.Delete(textArray, lineNumber, numOfLinesInText);
				if (numOfLinesToDelete==-1) {
					this.numOfLines += numOfLinesLocal - numOfLinesInText;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesInText);
				}
				else {
					this.numOfLines += numOfLinesLocal - numOfLinesToDelete;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesToDelete);
				}
				textArray = Array.InsertNoSpaceError(textArrayLocal, 0, textArray, lineNumber, 
						numOfLinesLocal);
			}catch(Exception e) {
				Log.e("setTextOneLineVScroll-delete,insert", e.toString());
			}
			
			//setVScrollBar();
			
			this.cursorPos = cursorPosLocal;
			
		} // if (!isSingleLine)
	}*/
	
	
	/*public void setTextOneLineBoth(int lineNumber, TextLine text, int numOfLinesToDelete) {
		if (text==null) return;
		int numOfLinesLocal = 1;
		
		if (isSingleLine==false) {		
			TextLine[] textArrayLocal = new TextLine[1];
			Point cursorPosLocal = new Point(0,lineNumber);
			TextLine textTemp1 = new TextLine(0,0);
			int i;
			
			for (i=0; i<textArrayLocal.length; i++) {
				textArrayLocal[i] = new TextLine(0,initFontSize);
			}
			
			TextLine charA;
			boolean isDeleteChar = false;
			boolean isNextCharBkSp = false;
			boolean isCurCharBkSp = false;
			int textLen = text.count;
			
			for (i=0; i<textLen; i++) {
				charA = text.subTextLine(i, i+1);
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
					if (charA.equals(NewLineChar)) {
						textTemp1.add(charA.characters[0]);
						textArrayLocal[numOfLinesLocal-1] = textTemp1;
						//numOfLinesLocal++;
						setCursorPos(lineNumber, i, true, false, cursorPosLocal);
						//textTemp1 = "";
						//textTemp2 = "";
						break;
					}				
					else  {
						textTemp1.add(charA.characters[0]);
						textArrayLocal[numOfLinesLocal-1].add(charA.characters[0]);
						setCursorPos(lineNumber, i, false, false, cursorPosLocal);
					}
				}	// isDeleteChar == false;
				
			}		// for
			for (i=0; i<numOfLinesLocal; i++) {
				if (textArrayLocal[i].count!=0) {
					textArrayLocal[i].setLineHeight(30);
				}
			}
			
			//this.numOfLines += numOfLinesLocal - numOfLinesInText;
			try {
				//textArray = Array.Delete(textArray, lineNumber, numOfLinesInText);
				if (numOfLinesToDelete==-1) {
					this.numOfLines += numOfLinesLocal - numOfLinesInText;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesInText);
				}
				else {
					this.numOfLines += numOfLinesLocal - numOfLinesToDelete;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesToDelete);
				}
				textArray = Array.InsertNoSpaceError(textArrayLocal, 0, textArray, lineNumber, 
						numOfLinesLocal);
			}catch(Exception e) {
				Log.e("Insert", e.toString());
			}
			
			this.cursorPos = cursorPosLocal;
			
		} // if (!isSingleLine)
	}*/
	
	
	/** paste에서 사용*/
	public void setTextMultiLineVScroll(int lineNumber, TextLine text, int numOfLinesToDelete) {
		if (text==null) return;
		int numOfLinesLocal = 1;
		
		if (isSingleLine==false) {
			TextLine[] textArrayLocal = new TextLine[20];
			Point cursorPosLocal = new Point(0,lineNumber);
			TextLine textTemp1 = new TextLine(0,0);
			TextLine textTemp2 = new TextLine(0,0);
			int i;
			float lineWidth = 0;
			textArrayLocal[0] = new TextLine(0,initFontSize); // '\n'을 친 후에 새로 생긴 빈 라인을 위한 것이다.
			
			TextLine charA;
			boolean isDeleteChar = false;
			boolean isNextCharBkSp = false;
			boolean isCurCharBkSp = false;
			int textLen = text.count;
			
			int countOfNewLineChar = 0;
			
			for (i=0; i<textLen; i++) {
				charA = text.subTextLine(i, i+1);
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
					if (charA.equals(NewLineChar)) {
						textTemp1.add(charA.characters[0]);
						textArrayLocal = setTextArrayNoSpaceError(textArrayLocal, numOfLinesLocal-1, textTemp1);
						setCursorPos(lineNumber, i, true, false, cursorPosLocal);						
						
						numOfLinesLocal++;
						textTemp1 = new TextLine(0,0);
						textTemp2 = new TextLine(0,0);
						lineWidth = 0;
					}				
					else  {
						textTemp1.add(charA.characters[0]);
						//lineWidth = paint.measureText(textTemp1);
						lineWidth += paint.measureText(charA);
						if (lineWidth > rationalBoundsWidth) {
							textArrayLocal = setTextArrayNoSpaceError(textArrayLocal, numOfLinesLocal-1, textTemp2);
							numOfLinesLocal++;
							textArrayLocal = setTextArrayNoSpaceError(textArrayLocal, numOfLinesLocal-1, new TextLine(0,0));
							setCursorPos(lineNumber, i, true, true, cursorPosLocal);
							textTemp1 = new TextLine(0,0);
							textTemp2 = new TextLine(0,0);
							lineWidth = 0;
							i--;	// 초과된 문자(다음줄의 첫문자)를 다시 처리
						}
						else {
							textTemp2.add(charA.characters[0]);
							textArrayLocal = setTextArrayNoSpaceError(textArrayLocal, numOfLinesLocal-1, textTemp2);
							setCursorPos(lineNumber, i, false, false, cursorPosLocal);
						}
					}
				}	// isDeleteChar == false;
				
			}		// for
			
						
			for (i=0; i<numOfLinesLocal; i++) {
				if (textArrayLocal[i]!=null && textArrayLocal[i].count!=0) {
					textArrayLocal[i].setLineHeight(30);
				}
			}
			try {
				if (numOfLinesToDelete==-1) {
					// 마지막 라인에서 엔터를 누른 경우 numOfLinesLocal는 두 줄이므로
					// '\n'으로 끝나더라도 -1을 하지 않는다.
					if (lineNumber!=numOfLines-1 &&
							text.count>0 && text.characters[text.count-1].charA=='\n') {
						numOfLinesLocal--;
					}
					this.numOfLines += numOfLinesLocal - numOfLinesInText;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesInText);
				}
				else {
					// 마지막 라인에서 엔터를 누른 경우 numOfLinesLocal는 두 줄이므로
					// '\n'으로 끝나더라도 -1을 하지 않는다.
					if (lineNumber!=numOfLines-1 &&
							text.count>0 && text.characters[text.count-1].charA=='\n') {
						numOfLinesLocal--;
					}
					this.numOfLines += numOfLinesLocal - numOfLinesToDelete;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesToDelete);
				}
				textArray = Array.InsertNoSpaceError(textArrayLocal, 0, textArray, lineNumber, 
						numOfLinesLocal);
				//textArray = Array.Resize(textArray, numOfLines+100);
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
			
			//setVScrollBar();
			
			this.cursorPos = cursorPosLocal;
			
		} // if (!isSingleLine)
	}
	/** paste에서 사용*/
	public void setTextMultiLineBoth(int lineNumber, TextLine text, int numOfLinesToDelete) {
		if (text==null) return;
		int numOfLinesLocal = 1;
		
		if (isSingleLine==false) {		
			TextLine[] textArrayLocal = new TextLine[20];
			Point cursorPosLocal = new Point(0,lineNumber);
			TextLine textTemp1 = new TextLine(0,0);
			int i;
			
			/*for (i=0; i<textArrayLocal.length; i++) {
				textArrayLocal[i] = new TextLine(0,initFontSize);
			}*/
			textArrayLocal[0] = new TextLine(0,initFontSize); // '\n'을 친 후에 새로 생긴 빈 라인을 위한 것이다.
			
			TextLine charA;
			boolean isDeleteChar = false;
			boolean isNextCharBkSp = false;
			boolean isCurCharBkSp = false;
			int textLen = text.count;
			
			
			for (i=0; i<textLen; i++) {
				charA = text.subTextLine(i, i+1);
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
					if (charA.equals(NewLineChar)) {
						//textTemp1 += "\n";
						textTemp1.add(charA.characters[0]);
						//textArrayLocal[numOfLinesLocal-1] = new String(textTemp1.getItems());
						textArrayLocal = setTextArrayNoSpaceError(
							textArrayLocal, numOfLinesLocal-1, textTemp1);
						setCursorPos(lineNumber, i, true, false, cursorPosLocal);						
						
						
						numOfLinesLocal++;
						textTemp1 = new TextLine(0,0);
					}				
					else  {
						textTemp1.add(charA.characters[0]);
						//textArrayLocal[numOfLinesLocal-1].add(charA.characters[0]);
						textArrayLocal = setTextArrayNoSpaceError(textArrayLocal, numOfLinesLocal-1, textTemp1);
						setCursorPos(lineNumber, i, false, false, cursorPosLocal);
					}
				}	// isDeleteChar == false;
				
			}		// for
			
			
			for (i=0; i<numOfLinesLocal; i++) {
				if (textArrayLocal[i]!=null && textArrayLocal[i].count!=0) {
					textArrayLocal[i].setLineHeight(30);
				}
			}
			
			try {
				//textArray = Array.Delete(textArray, lineNumber, numOfLinesInText);
				if (numOfLinesToDelete==-1) {
					// 마지막 라인에서 엔터를 누른 경우 numOfLinesLocal는 두 줄이므로
					// '\n'으로 끝나더라도 -1을 하지 않는다.
					if (lineNumber!=numOfLines-1 &&
							text.count>0 && text.characters[text.count-1].charA=='\n') {
						numOfLinesLocal--;
					}
					this.numOfLines += numOfLinesLocal - numOfLinesInText;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesInText);
				}
				else {
					// 마지막 라인에서 엔터를 누른 경우 numOfLinesLocal는 두 줄이므로
					// '\n'으로 끝나더라도 -1을 하지 않는다.
					if (lineNumber!=numOfLines-1 &&
							text.count>0 && text.characters[text.count-1].charA=='\n') {
						numOfLinesLocal--;
					}
					this.numOfLines += numOfLinesLocal - numOfLinesToDelete;
					textArray = Array.Delete(textArray, lineNumber, numOfLinesToDelete);
				}
				
								
				textArray = Array.InsertNoSpaceError(textArrayLocal, 0, textArray, lineNumber, 
						numOfLinesLocal);
				
				for (i=0; i<numOfLines; i++) {
					if (textArray[i]==null) {
						textArray[i] = new TextLine("", initFontSize);
					}
					else if (textArray[i].count!=0) {
						textArray[i].setLineHeight(30);
					}
				}
				//textArray = Array.Resize(textArray, numOfLines+100);
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
			
			this.cursorPos = cursorPosLocal;
			
		} // if (!isSingleLine)
	}
	
	void setHeightOfVScrollPos(int inc) {
		heightOfvScrollPos += inc;
		if (heightOfvScrollPos>heightOfLines-heightOfLinesPerPage) {
			heightOfvScrollPos = heightOfLines-heightOfLinesPerPage;
		}
		if (heightOfvScrollPos<0) heightOfvScrollPos = 0;
		setVScrollPos(heightOfvScrollPos);
	}
	
	void setWidthOfHScrollPos(int inc) {
		widthOfhScrollPos += inc;
		if (widthOfhScrollPos>widthOfTotalChars-widthOfCharsPerPage) {
			widthOfhScrollPos = widthOfTotalChars-widthOfCharsPerPage;
		}
		if (widthOfhScrollPos<0) widthOfhScrollPos = 0;
	}
	
	/** 이 메서드는 스크롤 위치(heightOfvScrollPos)가 바뀌면 vScrollPos가 바뀌어야 하므로 
	* 텍스트 내용이 바뀌거나(setVScrollPos()) 스크롤 버튼을 누르면(onTouchEvent) 호출된다.*/ 
	void setVScrollPos(float heightOfvScrollPos) {
		float h=0;
		int i;
		partOfCharY = 0;
		for (i=0; i<numOfLines; i++) {
			h += textArray[i].lineHeight;
			if (h>heightOfvScrollPos) {
				vScrollPos = i;
				partOfCharY = textArray[i].lineHeight - (h-heightOfvScrollPos);
				break;
			}
		}
	}
	
	/**  커서 위치(cursorPos)에 따라 자동 스크롤한다. 문자키를 눌러서 자동스크롤하여 그 다음 문자를 누르기 편하기위해 사용한다.
	 * setVScrollPos와 setHScrollPos, setVScrollBar, setHScrollBar는 텍스트 내용이 바뀔 때마다 
	 * setText, setTextOneLine에서 호출된다.커서위치는 setText, setTextOneLine에 있는 setCursorPos에서
	 * indexInText로 새로운 cursorPos가 결정된다. indexInText는 TextArrayToText에서 기존 cursorPos를 기반으로
	 * 결정된다. 
	 * 문자가 화면크기보다 큰 경우에도 스크롤할 수 있다.*/
	void setVScrollPos() {
		if (isReadOnly) return;
		
		float heightOfCursorPosY=0;
		int i;
		float topOfCursor, bottomOfCursor;
		try{
		for (i=0; i<=cursorPos.y; i++) {
			heightOfCursorPosY += textArray[i].lineHeight;
		}
		}catch(Exception e) {
			int a;
			a=0;
			a++;
		}
		
		bottomOfCursor = heightOfCursorPosY;
		descent = textArray[cursorPos.y].descent;
		leading = textArray[cursorPos.y].leading;
		topOfCursor = heightOfCursorPosY - textArray[cursorPos.y].maxFontSize - descent;
		
		if (!(heightOfvScrollPos<=topOfCursor && 
				bottomOfCursor<heightOfvScrollPos+heightOfLinesPerPage)) {
			if (heightOfvScrollPos>topOfCursor) {
				heightOfvScrollPos =  (int) topOfCursor;
			}
			else {
				heightOfvScrollPos = (int) (bottomOfCursor - heightOfLinesPerPage);
			}
			
		}
		
		
		
		
		
		//float leading = initFontSize * leadingRate;		// 현재 fontSize
		//descent = textArray[cursorPos.y].descent;
		/*if (textArray[cursorPos.y].lineHeight<heightOfLinesPerPage) {
			bottomOfCursor = heightOfCursorPosY;
			descent = textArray[cursorPos.y].descent;
			leading = textArray[cursorPos.y].leading;
			topOfCursor = heightOfCursorPosY - textArray[cursorPos.y].maxFontSize - descent;			
		}
		else {
			bottomOfCursor = heightOfCursorPosY;			
			descent = initFontSize * descentRate;
			leading = initFontSize * leadingRate;
			topOfCursor = heightOfCursorPosY - initFontSize - descent;
		}
		
		
		if (!(heightOfvScrollPos<=topOfCursor && 
				bottomOfCursor<heightOfvScrollPos+heightOfLinesPerPage)) {*/
			/*if (topOfCursor<heightOfvScrollPos) {				
				heightOfvScrollPos -= heightOfvScrollPos-topOfCursor;
				if (heightOfvScrollPos<0) heightOfvScrollPos=0;
			}
			else if (bottomOfCursor>=heightOfvScrollPos+heightOfLinesPerPage){
				heightOfvScrollPos += bottomOfCursor - (heightOfvScrollPos+heightOfLinesPerPage);
			}*/
			/*if (bottomOfCursor<heightOfvScrollPos) {
				heightOfvScrollPos = (int) topOfCursor;				
			}
			else if (topOfCursor>heightOfvScrollPos+heightOfLinesPerPage){
				heightOfvScrollPos = (int) topOfCursor;
			}*/
			/*heightOfvScrollPos = (int) (topOfCursor-leading);
			if (heightOfvScrollPos<0) heightOfvScrollPos = 0;
			
		}*/
		
		
	}
	
	/** 키보드에 키를 입력할 경우 호출한다.
	 * 문자가 화면크기보다 큰 경우에도 스크롤할 수 있다.*/
	void setHScrollPos() {
		if (isReadOnly) return;
		try {
			float endOfWindow = widthOfhScrollPos+widthOfCharsPerPage;
			TextLine str;
			// 한글과 기타 다른 언어의 커서위치가 다르기 때문에 다음과 같이 한다.
			if (keyboardMode==Mode.Hangul && hangulMode!=Hangul.Mode.None) {
			//if (keyboard.mode==Mode.Hangul && Hangul.mode!=Hangul.Mode.None) {
				str = textArray[cursorPos.y].subTextLine(0, cursorPos.x);
			}
			else {
				if (cursorPos.x<=0) str = new TextLine("", fontSize);
				else str = textArray[cursorPos.y].subTextLine(0, cursorPos.x);
			}
			// 커서가 가리키는 문자의 폭을 측정한다.
			TextLine cursorChar;
			if (cursorPos.x<textArray[cursorPos.y].count)
				cursorChar = textArray[cursorPos.y].subTextLine(cursorPos.x, cursorPos.x+1);
			else 
				cursorChar = new TextLine("3", initFontSize);
			float cursorCharWidth = paint.measureText(cursorChar);
			
			float cursorStart = paint.measureText(str);
			float cursorEnd;
			if (cursorPos.x<textArray[cursorPos.y].count) {
				if (textArray[cursorPos.y].characters[cursorPos.x].size>heightOfLinesPerPage) {
					cursorEnd = cursorStart+initFontSize;
				}
				else {
					cursorEnd = cursorStart+cursorCharWidth;
				}
			}
			else {
				cursorEnd = cursorStart+initFontSize;
			}
			
			// 커서가 보이도록 스크롤한다.
			if (!(widthOfhScrollPos<=cursorStart && cursorEnd<endOfWindow)) {
				// Backspace키를 눌러 스크롤 시작범위를 넘어선 경우
				/*if (cursorEnd < widthOfhScrollPos) {
					widthOfhScrollPos = (int) (cursorEnd-widthOfCharsPerPage);
					if (widthOfhScrollPos<0) widthOfhScrollPos=0;
				}
				// 문자키를 눌러 스크롤 끝범위를 넘어선 경우
				else if (cursorStart >= endOfWindow){
					widthOfhScrollPos = (int) (cursorEnd-widthOfCharsPerPage);
					if (widthOfhScrollPos<0) widthOfhScrollPos=0;
				}
				else {
					widthOfhScrollPos = (int) (cursorEnd-widthOfCharsPerPage);
					if (widthOfhScrollPos<0) widthOfhScrollPos=0;
				}*/
				widthOfhScrollPos = (int) (cursorEnd-widthOfCharsPerPage+30);
				if (widthOfhScrollPos<0) widthOfhScrollPos=0;
			}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
		
		
	}
	
	/** 수직 스크롤부분이 바뀔 경우 호출한다. 예를 들어 vScrollPos, bounds가 바뀔경우*/
	public void setVScrollBar(boolean requiresSetVScrollBar) {
		setHeightOfLines();
		heightOfLinesPerPage = (int) rationalBoundsHeight;
		
		/*if (heightOfLines > heightOfLinesPerPage) {
			// heightOfLines 을 계산한 후에 heightOfvScrollPos 다시 계산한다. 
			// 즉, setHScrollPos 제일 마지막 부분에서 heightOfvScrollPos 을 계산하는 것이 아니라 
			// 여기에서 계산한다.
			// heightOfvScrollPos이  heightOfLines-heightOfLinesPerPage 보다 크지못하도록 한다.
			if (heightOfvScrollPos>heightOfLines-heightOfLinesPerPage){
				heightOfvScrollPos = heightOfLines-heightOfLinesPerPage;
			}
		}*/
		//if (heightOfLines>heightOfLinesPerPage) {
			if (heightOfvScrollPos+heightOfLinesPerPage>=heightOfLines)
				heightOfvScrollPos = heightOfLines - heightOfLinesPerPage;
		//}
		if (heightOfvScrollPos<0) heightOfvScrollPos = 0;
		setVScrollPos(heightOfvScrollPos);
		
				
		setNumOfLinesPerPage(heightOfvScrollPos, heightOfLinesPerPage);
				
		//Log.d("setVScrollBar", "numOfLinesInPage:"+numOfLinesInPage+" numOfLinesPerPage:"+numOfLinesPerPage);
				
		//vScrollBar.hides = false;
		if (requiresSetVScrollBar) {
			vScrollBar.setVScrollBar(
					heightOfLinesPerPage,
					heightOfLines, heightOfvScrollPos, heightOfvScrollInc);
		}
	}
	
	/** 수평 스크롤부분이 바뀔 경우 호출한다. 예를 들어 hScrollPos, bounds가 바뀔경우*/
	public void setHScrollBar(boolean requiresSetHScrollBar) {
		
		int i;
		float maxLineWidth=0, lineWidth=0;
		for (i=0; i<numOfLines; i++) {
			lineWidth = paint.measureText(textArray[i]);
			if (lineWidth>maxLineWidth) {
				lineNumOfMaxWidth = i;
				maxLineWidth = lineWidth;
			}
		}
		this.maxLineWidth = maxLineWidth;
		
		
		widthOfTotalChars = (int) this.maxLineWidth;				
		widthOfCharsPerPage = (int) rationalBoundsWidth;
				
		// widthOfTotalChars을 계산한 후에 widthOfhScrollPos을 다시 계산한다. 
		// 즉, setHScrollPos 제일 마지막 부분에서 widthOfhScrollPos을 계산하는 것이 아니라 
		// 여기에서 계산한다.
		//widthOfTotalChars는 페이지 내에서의 최대 글자수
		//if (widthOfTotalChars>widthOfCharsPerPage) {
			if (widthOfhScrollPos+widthOfCharsPerPage>=widthOfTotalChars)
				widthOfhScrollPos = widthOfTotalChars - widthOfCharsPerPage;
		//}
		
		if (widthOfhScrollPos<0) widthOfhScrollPos = 0;
		
		
		
		//isHScrolled = true;
		if (requiresSetHScrollBar) {
			hScrollBar.setHScrollBar(
					widthOfCharsPerPage,
					widthOfTotalChars, widthOfhScrollPos, widthOfhScrollInc);
		}
		//hScrollBar.hides = false;	
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
				if (textArray[selectP1.y].characters[index].charA==Edit.find_separators[i]) {
					isLeftSeparator = true;
					break;
				}
			}
		}
		else {
			if (selectP1.y>0) {
				int indexY = selectP1.y-1;
				int indexX = textArray[indexY].count-1;
				for (i=0; i<Edit.find_separators.length; i++) {
					if (textArray[indexY].characters[indexX].charA==Edit.find_separators[i]) {
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
		if (selectP2.x+1 < textArray[selectP2.y].count) {
			int index = selectP2.x+1;
			for (i=0; i<Edit.find_separators.length; i++) {
				if (textArray[selectP2.y].characters[index].charA==Edit.find_separators[i]) {
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
					if (textArray[indexY].characters[indexX].charA==Edit.find_separators[i]) {
						isRightSeparator = true;
						break;
					}
				}
			}
			else {
				if (selectP2.y==numOfLines-1 && selectP2.x==textArray[selectP2.y].count-1)
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
				len = textArray[j].count;
				
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
					
					if (isNotEqual(isCaseSensitive, textArray[j].characters[i].charA, 
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
								//if (!isAll) isSelecting = true;
								
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
								// 검색위치로 스크롤바를 이동시킨다.
								if (!(vScrollPos<=cursorPos.y && cursorPos.y<vScrollPos+numOfLinesPerPage)) {
									vScrollPos = cursorPos.y;
									setVScrollPos();
									setVScrollBar(true);
								}							
								if (scrollMode==ScrollMode.Both) {
									setHScrollPos();
									setHScrollBar(true);
								}
								if (!isAll) return;
							}
							else {	// wholeWord
								if (isAdjacentCharSeparator(findP1, findP2)) {
									makeSelectIndices(false, findP1, findP2);
									//if (!isAll) isSelecting = true;
									
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
										setVScrollPos();
										setVScrollBar(true);
									}							
									if (scrollMode==ScrollMode.Both) {
										setHScrollPos();
										setHScrollBar(true);
									}
									if (!isAll) return;
								}
							}
						}	// if (k==textToFind.length()-1) {	// 찾은경우, 끝위치
						else if (k<textToFind.length()-1) k++;
					}
				}
				
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
				len = textArray[j].count;				
				
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
					
					if (isNotEqual(isCaseSensitive, textArray[j].characters[i].charA, 
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
								//if (!isAll) isSelecting = true;
								
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
									setVScrollPos();
									setVScrollBar(true);
								}							
								if (scrollMode==ScrollMode.Both) {
									setHScrollPos();
									setHScrollBar(true);
								}
								if (!isAll) return;
							}
							else {
								if  (isAdjacentCharSeparator(findP1, findP2)) {
									makeSelectIndices(false, findP1, findP2);
									//if (!isAll) isSelecting = true;
									
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
										setVScrollPos();
										setVScrollBar(true);
									}							
									if (scrollMode==ScrollMode.Both) {
										setHScrollPos();
										setHScrollBar(true);
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
			
	
	/*void findCommon(boolean isForward, boolean isScopeAll, boolean isCaseSensitive, 
		boolean isWholeWord, Point curFindPosLocal, String textToFind) {
		

		int i, j;
		int k;
		int len;
		boolean bInit = true;
		int endX, endY;
		
		boolean[] searchedLines = new boolean[pointFindEnd.y-pointFindStart.y+1];
		
		if (isForward) {
			i=curFindPosLocal.x;
			k=0;
			for (j=curFindPosLocal.y; j<numOfLines; j++) {
				len = textArray[j].count;
				
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
					
					if (isNotEqual(isCaseSensitive, textArray[j].characters[i].charA, 
						textToFind.charAt(k))) {
						k = 0;
					}
					else {
						if (k==0) { // 시작위치
							selectP1.x = i;
							selectP1.y = j;
						}
						if (k==textToFind.length()-1) {	// 찾은경우, 끝위치
							selectP2.x = i;
							selectP2.y = j;
							
							if (!isWholeWord ) {
								makeSelectIndices( selectP1, selectP2);
								isSelecting = true;
								
								isFound = true;
								
								cursorPos.x = i+1;	// find next, +1을 하지 않으면 한 문자 찾기에서 전진을 안한다.
								cursorPos.y = j;
								if (!(vScrollPos<=cursorPos.y && cursorPos.y<vScrollPos+numOfLinesPerPage)) {
									vScrollPos = cursorPos.y;
									setVScrollPos();
									setVScrollBar(true);
								}							
								if (scrollMode==ScrollMode.Both) {
									setHScrollPos();
									setHScrollBar(true);
								}
								return;
							}
							else {
								if (isAdjacentCharSeparator(selectP1, selectP2)) {
									makeSelectIndices( selectP1, selectP2);
									isSelecting = true;
									
									isFound = true;
									
									cursorPos.x = i+1;	// find next, +1을 하지 않으면 한 문자 찾기에서 전진을 안한다.
									cursorPos.y = j;
									if (!(vScrollPos<=cursorPos.y && cursorPos.y<vScrollPos+numOfLinesPerPage)) {
										vScrollPos = cursorPos.y;
										setVScrollPos();
										setVScrollBar(true);
									}							
									if (scrollMode==ScrollMode.Both) {
										setHScrollPos();
										setHScrollBar(true);
									}
									return;
								}
							}
						}	// if (k==textToFind.length()-1) {	// 찾은경우, 끝위치
						if (k<textToFind.length()-1) k++;
					}
				}
				
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
				len = textArray[j].count;				
				
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
					
					if (isNotEqual(isCaseSensitive, textArray[j].characters[i].charA, 
						textToFind.charAt(k))) {
						k = textToFind.length()-1;
					}
					else {
						if (k==textToFind.length()-1) { // 시작위치
							selectP2.x = i;
							selectP2.y = j;
						}
						if (k==0) {	// 찾은경우, 끝위치
							selectP1.x = i;
							selectP1.y = j;
							
							if (!isWholeWord) {
								makeSelectIndices( selectP1, selectP2);
								isSelecting = true;
								
								isFound = true;
								
								cursorPos.x = i;
								cursorPos.y = j;
								if (!(vScrollPos<=cursorPos.y && cursorPos.y<vScrollPos+numOfLinesPerPage)) {
									vScrollPos = cursorPos.y;
									setVScrollPos();
									setVScrollBar(true);
								}							
								if (scrollMode==ScrollMode.Both) {
									setHScrollPos();
									setHScrollBar(true);
								}
								return;
							}
							else {
								if  (isAdjacentCharSeparator(selectP1, selectP2)) {
									makeSelectIndices( selectP1, selectP2);
									isSelecting = true;
									
									isFound = true;
									
									cursorPos.x = i;
									cursorPos.y = j;
									if (!(vScrollPos<=cursorPos.y && cursorPos.y<vScrollPos+numOfLinesPerPage)) {
										setVScrollPos();
										setVScrollBar(true);
									}							
									if (scrollMode==ScrollMode.Both) {
										setHScrollPos();
										setHScrollBar(true);
									}
									return;
								}
							}
						}
						if (k>0) k--;
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
	}*/
	
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
	 * 위 함수들이 공통적으로 replaceCommon()을 호출하기 때문이다.*/
	void replaceCommon(boolean isAll, Point p1, Point p2, String textToFind, String textToReplaceWith) {
		//int numOfLines = p2.y - p1.y + 1;
		int numOfLines = this.getNumOfNewLineChar(new TextLine(textToFind,fontSize))+1;
		
		if (numOfLines==1) {
			TextLine lineText = textArray[p1.y];
			//ArrayListCharacter list = new ArrayListChar(30);
			//list.setText(lineText);
			lineText.delete(p1.x, p2.x-p1.x+1);
			lineText.insert(p1.x, new TextLine(textToReplaceWith, fontSize));
			
			//TextLine newLineText = new String(list.getItems());
			if (scrollMode==ScrollMode.VScroll) {
				if (!isAll) setTextMultiLine(p1.y, lineText, 1, 1);
				else textArray[p1.y] = lineText;
				cursorPos.x = p1.x + textToReplaceWith.length();
			}
			else {
			
				
				if (!isAll) {
					int numOfNewLines = this.getNumOfNewLineChar(lineText)+1;
					if (lineText.characters[lineText.count-1].charA=='\n') {
						numOfNewLines--;
					}
					setTextMultiLine(p1.y, lineText, 1, numOfNewLines);
				}
				else {
					//textArray[p1.y] = newLineText;
					int numOfNewLines = this.getNumOfNewLineChar(lineText)+1;
					if (lineText.characters[lineText.count-1].charA=='\n') {
						numOfNewLines--;
					}
					setTextMultiLine(p1.y, lineText, 1, numOfNewLines);
				}
				cursorPos = getRelativeCursorPos(p1, 
						new TextLine(textToReplaceWith, fontSize));
			}
			
			findP1.x = p1.x;
			findP2.x = cursorPos.x-1;
			if (!isAll) makeSelectIndices(false, findP1, findP2);
		}//if (numOfLines==1) {
		else {
			int i;
			TextLine newLineText=null;
			for (i=0; i<numOfLines; i++) {
				if (i==0) {
					TextLine lineText1 = textArray[p1.y];
					lineText1.delete(p1.x, lineText1.count-1-p1.x+1);
					lineText1.insert(p1.x, new TextLine(textToReplaceWith, fontSize));
					newLineText = lineText1;
				}
				else if (i==numOfLines-1) {	
					if (textToFind.length()>0 && textToFind.charAt(textToFind.length()-1)=='\n') {
						//	 12\n
						//	 3\n
						//	 456\n
						//	 789 
						//	 에서 textToFind는 2\n이고 textToReplaceWith는 2이다.
						TextLine lineText2 = textArray[p2.y+1];
						newLineText.insert(newLineText.count, lineText2);
					}
					else {
						// 위 예에서 textToFind는 2\n3이고 textToReplaceWith는 2이다.
						TextLine lineText2 = textArray[p2.y];
						lineText2.delete(0, p2.x+1);
						newLineText.insert(newLineText.count,  lineText2);
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
				if (newLineText.characters[newLineText.count-1].charA=='\n') {
					numOfNewLines--;
				}
				setTextMultiLine(p1.y, newLineText, numOfLines, numOfNewLines);
				
				cursorPos = this.getRelativeCursorPos(p1, newLineText);
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
		TextLine textLineTextToReplaceWith = new TextLine(textToReplaceWith, textColor);
		int numOfNewLines = getNumOfNewLineChar(textLineTextToReplaceWith) + 1;
		if (textToReplaceWith.length()>0 && 
				textToReplaceWith.charAt(textToReplaceWith.length()-1)=='\n') {
			numOfNewLines--;
		}
		incY = numOfNewLines - incY;
		
		Point temp = this.getRelativeCursorPos(new Point(0,0), 
				textLineTextToReplaceWith); // 들어오는 x, 					
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
		TextLine textLineTextToReplaceWith = new TextLine(textToReplaceWith, textColor);
		int numOfNewLines = getNumOfNewLineChar(textLineTextToReplaceWith) + 1;
		if (textToReplaceWith.length()>0 && 
				textToReplaceWith.charAt(textToReplaceWith.length()-1)=='\n') {
			numOfNewLines--;
		}
		incY = numOfNewLines - incY;
		
		Point temp = this.getRelativeCursorPos(new Point(0,0), 
				textLineTextToReplaceWith); // 들어오는 x, 					
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
				if (p.x>p2.x) {// 같은 줄의 x축으로 다음 점
					
					// p.x = p.x + temp.x - (p2.x+1);
					p.x += incX;
					p.y += incY;
					listFindPos.list[i] = p;
				}
			}					
			else if (p.y>p2.y) {// 다음 줄
				p.y += incY;
				listFindPos.list[i] = p;
			}
		}//for (i=0; i<listFindPos.count; i++) {
	}
	
	public void replace(boolean isAll, boolean isForward, boolean isScopeAll, boolean isCaseSensitive, 
			boolean isWholeWord, Point curFindPosLocal, String textToFind, String textToReplaceWith) {
		if (isReadOnly) return;
		
		
		textToFind = processSpecialChar(textToFind);
		textToReplaceWith = processSpecialChar(textToReplaceWith);
		
		isModified = true;
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
				CommonGUI.loggingForMessageBox.setText(true, "Touch the Find next.", false);
			}
		}// if (!isAll)
		else {	// ReplaceAll은 scrollMode가 Both일 때만 동작한다. 검색시작위치는 0,0에서 시작되므로 listFindPos은 x, y순으로 정렬된 상태이다.
			
			
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
			setVScrollBar(true);
			if (scrollMode==ScrollMode.Both) {
				setHScrollPos();
				setHScrollBar(true);	
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
		try {
		//isFinding = true;
		isFound = false;
		//isScopeAll = true;
		
		if (isScopeAll==false) {
			if (isSelecting==false) {	// 선택되어 있어야 한다.
				
				return;
			}
			else {
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
			}
		}
		else {
			pointFindStart.x = 0;
			pointFindStart.y = 0;
			if (textArray[numOfLines-1].count>0)
				pointFindEnd.x = textArray[numOfLines-1].count-1;
			else 
				pointFindEnd.x = 0;
			pointFindEnd.y = numOfLines-1;
			
			// scope가 all이므로 시작 커서 위치를 원점으로 한다.
			//curFindPosLocal.y = 0;
			//curFindPosLocal.x = 0;
		}
		//isSelecting = false;
		
		int startLine = curFindPosLocal.y;
		
		findCommon(isAll, isForward, isScopeAll, isCaseSensitive, 
		 	isWholeWord, curFindPosLocal, textToFind);
		 	
		if (isFound==false) {
			if (isCallerReplace) {
				CommonGUI.loggingForMessageBox.setHides(false);
				CommonGUI.loggingForMessageBox.setText(true, "The text not found.", false);
			}
		}
		else {
			//listFindPos = refine(this.listFindPos);
			refineListFindPos();
		}
		
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
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
	
	
	/** 스크롤바와 EditRichText선택영역의 MoveActionCapture처리 : 
	 * ActionDown(drag시작지점이 editRichText,scrollBar인가)을 활용하여 editRichText가
	 * action capture(자기영역을 벗어난 ActionMove를 자신이 가져간다)할지 안할지 결정한다.*/
	public boolean onTouch(MotionEvent event, SizeF scaleFactor) {		
		boolean r = false;		
		if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
			if (hides) return false;
			r = toolbar.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	
			if (super.onTouch(event, scaleFactor)==false)     		
				return false;	    	
	    	
			r = vScrollBar.onTouch(event, scaleFactor);
			if (r) return true;
			if (scrollMode==ScrollMode.Both) {
				r = hScrollBar.onTouch(event, scaleFactor);
				if (r) return true;
			}
			
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
			
			capturedControl = this;
			onTouchEvent(this, event);
			
			// onTouchEvent에서 커서위치를 바꾼후(vScrollPos설정) editText의 bounds를 바꾸고 키보드를 보여준다.
			// 다시 말해 순서가 중요하다.
			if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
				if (!isReadOnly) {				
					if (isSingleLine==false) {
						if (isMaximized()) {
							Rectangle newBoundsOfEditText = new Rectangle(bounds.x, bounds.y, bounds.width, (int)(view.getHeight()*0.5f));
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
			}
			return true;
		}
		else if (event.actionCode==MotionEvent.ActionMove || 
				event.actionCode==MotionEvent.ActionUp) {
			/*if (fontSizeDialog.getIsOpen()) {
				return true;
			}
			if (fileDialog.getIsOpen()) {
				return true;
			}*/
			
			
			// drag시작이 scrollBar이면 scrollBar가, editText라면 editText가 핸들링
			
			if (capturedControl==this) {
				// 영역내에서 터치를 하여 캡쳐를 하면 CustomView에서 ActionMove를 전달하여 스크롤을 하게 된다.
				// 영역검사를 하지않고 영역을 벗어나더라도 자신이 핸들링한다.
				modified = true;
				onTouchEvent(this, event);
				return true;
			}
			
		}
		return false;
    }
	
	
	
	/**selectP1,selectP2는 논리적 좌표*/ 
	void makeSelectIndices(boolean isSelectingOrFinding, Point selectP1, Point selectP2) {
		try{
			if (isSelectingOrFinding) {
				int i;
				selectIndicesCount = 0;
				if (selectP1==null || selectP2==null) return;
				if (selectP1.y<=selectP2.y) {
					selectLenY = selectP2.y - selectP1.y + 1;
					
					// 경계처리
					if (textArray[selectP2.y].count<=selectP2.x) {
						if (textArray[selectP2.y].count>0)
							selectP2.x = textArray[selectP2.y].count-1;
						else 
							selectP2.x = 0;
					}
					if (textArray[selectP1.y].count<=selectP1.x) {
						if (textArray[selectP1.y].count>0)
							selectP1.x = textArray[selectP1.y].count-1;
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
						selectIndices[selectIndicesCount++] = new Point(textArray[selectP1.y].count-1, 
								Select_FirstLine);
						for (i=selectP1.y+1; i<selectP2.y; i++) {
							if (!(selectIndicesCount<=selectIndices.length-2)) 
								selectIndices = Array.Resize(selectIndices, selectIndices.length+20);
							selectIndices[selectIndicesCount++] = new Point(0, i);
							selectIndices[selectIndicesCount++] = 
									new Point(textArray[i].count-1, Select_MiddleLine);
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
					if (textArray[selectP2.y].count<=selectP2.x) {
						if (textArray[selectP2.y].count>0)
							selectP2.x = textArray[selectP2.y].count-1;
						else 
							selectP2.x = 0;
					}
					if (textArray[selectP1.y].count<=selectP1.x) {
						if (textArray[selectP1.y].count>0)
							selectP1.x = textArray[selectP1.y].count-1;
						else
							selectP1.x = 0;
					}
					
					// 첫번째 라인
					selectIndices[selectIndicesCount++] = new Point(selectP2.x,selectP2.y);
					selectIndices[selectIndicesCount++] = new Point(textArray[selectP2.y].count-1, 
							Select_FirstLine);
					for (i=selectP2.y+1; i<selectP1.y; i++) {
						if (!(selectIndicesCount<=selectIndices.length-2)) 
							selectIndices = Array.Resize(selectIndices, selectIndices.length+20);
						selectIndices[selectIndicesCount++] = new Point(0, i);
						selectIndices[selectIndicesCount++] = new Point(textArray[i].count-1, Select_MiddleLine);
					}
					// 마지막 라인
					if (!(selectIndicesCount<=selectIndices.length-2)) 
						selectIndices = Array.Resize(selectIndices, selectIndices.length+20);
					selectIndices[selectIndicesCount++] = new Point(0, selectP1.y);
					selectIndices[selectIndicesCount++] = new Point(selectP1.x,Select_LastLine);
					
					selectIndicesCountForCopy = selectIndicesCount;
				}
			}
			else {	// if (isSelectingOrFinding)
				int i;
				findIndicesCount = 0;
				if (findP1==null || findP2==null) return;
				if (findP1.y<=findP2.y) {
					findLenY = findP2.y - findP1.y + 1;
					
					// 경계처리
					if (textArray[findP2.y].count<=findP2.x) {
						if (textArray[findP2.y].count>0)
							findP2.x = textArray[findP2.y].count-1;
						else
							findP2.x = 0;
					}
					if (textArray[findP1.y].count<=findP1.x) {
						if (textArray[findP1.y].count>0)
							findP1.x = textArray[findP1.y].count-1;
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
						findIndices[findIndicesCount++] = new Point(textArray[findP1.y].count-1, 
								Select_FirstLine);
						for (i=findP1.y+1; i<findP2.y; i++) {
							if (!(findIndicesCount<=findIndices.length-2)) 
								findIndices = Array.Resize(findIndices, findIndices.length+20);
							findIndices[findIndicesCount++] = new Point(0, i);
							findIndices[findIndicesCount++] = 
									new Point(textArray[i].count-1, Select_MiddleLine);
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
					if (textArray[findP2.y].count<=findP2.x) {
						if (textArray[findP2.y].count>0)
							findP2.x = textArray[findP2.y].count-1;
						else
							findP2.x = 0;
					}
					if (textArray[findP1.y].count<=findP1.x) {
						if (textArray[findP1.y].count>0)
							findP1.x = textArray[findP1.y].count-1;
						else
							findP1.x = 0;
					}
					
					// 첫번째 라인
					findIndices[findIndicesCount++] = new Point(findP2.x,findP2.y);
					findIndices[findIndicesCount++] = new Point(textArray[findP2.y].count-1, 
							Select_FirstLine);
					for (i=findP2.y+1; i<findP1.y; i++) {
						if (!(findIndicesCount<=findIndices.length-2)) 
							findIndices = Array.Resize(findIndices, findIndices.length+20);
						findIndices[findIndicesCount++] = new Point(0, i);
						findIndices[findIndicesCount++] = new Point(textArray[i].count-1, Select_MiddleLine);
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
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
	}
	
	void copy() {
		if (isSelecting) {
			isCopied = true;
			int i;
			int y;
			int startX, endX;
			copiedText.reset();
			for (i=0; i<selectIndicesCountForCopy; i+=2) {
				y = selectIndices[i].y;
				startX = selectIndices[i].x;
				endX = selectIndices[i+1].x;
				TextLine lineCopiedText = textArray[y].subTextLine(startX, endX+1);
				copiedText.insert(lineCopiedText.toCharacterArray(),0,
					copiedText.count,lineCopiedText.count);
			}
			
			//if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
				Control.ClipBoardX.setData(copiedText.toString());
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
			copiedText.reset();
			int[] fullLines = new int[10];
			int fullLinesCount = 0;
				
			backUpForUndo("cut", false);
			// redo 를 무효화한다. redoBuffer를 모두 지워야 한다. 
			// redo 를 무효로 만들지 않으면 undo-redo 시스템의 오류가 발생한다.
			redoBuffer.reset();
			
			for (i=0; i<selectIndicesCountForCopy; i+=2) {
				y = selectIndices[i].y;
				startX = selectIndices[i].x;
				endX = selectIndices[i+1].x;
				TextLine lineCopiedText = textArray[y].subTextLine(startX, endX+1);
				copiedText.insert(lineCopiedText.toCharacterArray(),0,
					copiedText.count,lineCopiedText.count);
				
				if (textArray[y].count==lineCopiedText.count) { 
					// full line이면 줄 자체를 삭제
					if (!(fullLinesCount<=fullLines.length-1)) 
						fullLines = Array.Resize(fullLines, fullLines.length+10);
					fullLines[fullLinesCount++] = y;
				}
				else {
					TextLine line = new TextLine(textArray[y].toCharacterArray());
					//list.setText(textArray[y]);
					line.delete(startX, endX-startX+1);
					textArray[y] = line;
				}
			}
			
			// full line이면 줄 자체를 삭제
			if (fullLinesCount>0) {
				ArrayListTextLine listTextArray = new ArrayListTextLine(textArray);
				listTextArray.delete(fullLines[0], fullLinesCount);
				textArray = listTextArray.getItems();
				numOfLines -= fullLinesCount;
			}
			
			// full line을 제외한 select영역에 포함된 newline문자를 제거한다.
			TextLine newText = TextArrayToText(selectIndices[0].y, cursorPos.x, 1);
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
			
			Control.ClipBoardX.setData(copiedText.toString());
			
			cursorPos.x = p1.x;
			cursorPos.y = p1.y;
			
			isSelecting = false;
		}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
	}
	
	void paste() {
		if (isReadOnly) return;
		
		//if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
			String dataClipboard = Control.ClipBoardX.getData();
			TextLine data = new TextLine(dataClipboard, initFontSize);
			if (dataClipboard!=null) {
				isCopied = true;
				copiedText = data;
			}
		//}
		
		if (!isCopied) return;
		
		isModified = true;
		
		backUpForUndo("paste", false);
		// redo 를 무효화한다. redoBuffer를 모두 지워야 한다. 
		// redo 를 무효로 만들지 않으면 undo-redo 시스템의 오류가 발생한다.
		redoBuffer.reset();
		
		TextLine left = textArray[cursorPos.y].subTextLine(0, cursorPos.x);
		TextLine right = textArray[cursorPos.y].subTextLine(cursorPos.x, 
				textArray[cursorPos.y].count);
		// newCurLineText = left + copiedText + right
		left.insert(copiedText.toCharacterArray(),0,left.count,copiedText.count);
		left.insert(right.toCharacterArray(),0,left.count,right.count);
		
		Point oldCursorPos = new Point();
		oldCursorPos.x = cursorPos.x;
		oldCursorPos.y = cursorPos.y;
		
		indexOfCursorInText = copiedText.count;
		/*int numOfNewLineChar = getNumOfNewLineChar(left);
		setTextMultiLine(cursorPos.y, left, 1, numOfNewLineChar);
		
		cursorPos.x = oldCursorPos.x;
		cursorPos.y = oldCursorPos.y;*/
		
		int numOfNewLineChar = getNumOfNewLineChar(left) + 1;
		if (left.characters[left.count-1].charA=='\n') {
			numOfNewLineChar--;
		}
		setTextMultiLine(cursorPos.y, left, 1, numOfNewLineChar);
		
		Point relativeCursorPos = this.getRelativeCursorPos(oldCursorPos, copiedText);
		
		cursorPos.x = relativeCursorPos.x;
		cursorPos.y = relativeCursorPos.y;
		
		view.invalidate();
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
		if (textArray[numOfLines-1].count>0) {
			selectP2.x = textArray[numOfLines-1].count-1;
		}
		else {
			selectP2.x = 0;
		}
		selectP2.y = numOfLines-1;
		
		makeSelectIndices(true, selectP1, selectP2);
		
		this.isSelecting = true;
	}
	
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
	
	
		/*if (strMenuName.equals("Copy")) {
			copy();
		}
		else if (strMenuName.equals("Cut")) {
			cut();
		}
		else if (strMenuName.equals("Paste")) {
			paste();
		}
		else if (strMenuName.equals("Find/Replace")) {
			// 모든 EditText들이 findReplaceDialog을 공유하므로 changeBounds를 해준다.			
			Rectangle newBounds = new Rectangle(totalBounds.x, totalBounds.y, totalBounds.width, totalBounds.height);
			Edit.findReplaceDialog.changeBounds(newBounds);
			if (isSelecting) {
				Edit.findReplaceDialog.setScope(false);
			}
			else {
				Edit.findReplaceDialog.setScope(true);
			}
			Edit.findReplaceDialog.open(this, true);
		}
		else if (strMenuName.equals("Undo")) {
			undo();
		}
		else if (strMenuName.equals("Redo")) {
			redo();
		}
		else if (strMenuName.equals("Show UndoBuffer")) {
			showUndoBuffer();
		}
		else if (strMenuName.equals("Show RedoBuffer")) {
			showRedoBuffer();
		}*/
	}
	
	void toolbar_Listener(Object sender, String buttonName) {
		if (buttonName.equals("S")) {	// 툴바버튼
			Edit.menuFontSize.open(this, true);
			//return;
		}
		else if (buttonName.equals("F")) {
			menuTypeface.open(true);
			//return;
		}
		else if (buttonName.equals("C")) {
			colorDialog.open(this, true);
		}
		else if (buttonName.equals("B") || buttonName.equals("I")) {
			FontFamily family = Font.fromString(typefaceName);
			if (isBold && isItalic)
				typeface = Font.getTypeface(family, true, true);
			else if (isBold)
				typeface = Font.getTypeface(family, true, false);
			else if (isItalic)
				typeface = Font.getTypeface(family, false, true);
			else
				typeface = Font.getTypeface(family, false, false);
			if (isSelecting==false) {							
				return;
			}
			else {
				int k, j;
				for (k=0; k<selectIndicesCount; k+=2) {
					int start = selectIndices[k].x;
					int end = selectIndices[k+1].x;
					int count = textArray[selectIndices[k].y].count;
					for (j=start; j<=end && j<count; j++) {
						textArray[selectIndices[k].y].characters[j].typeface = 
								typeface;
						textArray[selectIndices[k].y].characters[j].typefaceName = 
								typefaceName;
						textArray[selectIndices[k].y].characters[j].isBold = 
								isBold;
						textArray[selectIndices[k].y].characters[j].isItalic = 
								isItalic;
					}								
				}
				realign();
			}
		}
		else if (buttonName.equals("U")) {
			if (isSelecting) {
				int k, j;
				for (k=0; k<selectIndicesCount; k+=2) {
					int start = selectIndices[k].x;
					int end = selectIndices[k+1].x;
					int count = textArray[selectIndices[k].y].count;
					for (j=start; j<=end && j<count; j++) {
						textArray[selectIndices[k].y].characters[j].isUnderLine = 
								isUnderline;
					}								
				}
				realign();
			}
		}
		else if (buttonName.equals("P")) {
			fileDialog = CommonGUI.fileDialog;
			if (fileDialog.getIsOpen()) {
				CommonGUI.loggingForMessageBox.setText(true, "File explorer already opens", false);
				CommonGUI.loggingForMessageBox.setHides(false);
				return;
			}
			
			if (isMaximized==false) {
				fileDialog.isFullScreen = false;					
			}
			else {
				fileDialog.isFullScreen = true;
			}
			fileDialog.canSelectFileType = false;
			fileDialog.isForViewing = false;
			fileDialog.setScaleValues();
			Rectangle newBoundsOfEditText = new Rectangle(bounds);
			fileDialog.changeBounds(newBoundsOfEditText);
			
			fileDialog.createAndSetFileListButtons(fileDialog.curDir,
					FileDialog.Category.Image);
			//fileDialog.setOnTouchListener(this);
			fileDialog.setIsForReadingOrSaving(true);
			fileDialog.open(this, "FileExplorer - Load");
			
		}
		else if (buttonName.equals("FN")) {
			Edit.menuFunction.open(this, true);
		}
		else if (buttonName.equals("R/W")) {
			if (toolbar.buttons[7].isSelected) {
				this.isReadOnly = true;
			}
			else {
				this.isReadOnly = false;
			}
		}
		//bound(BoundMode.ScrollMode, false);
	}
	
	void typefaceMenu_Listener(Object sender, String strTypeface) {
		typefaceName = strTypeface;
		
		menuTypeface.open(false);
		if (isSelecting) {
			int k, j;
			boolean isBold, isItalic;
			for (k=0; k<selectIndicesCount; k+=2) {
				int start = selectIndices[k].x;
				int end = selectIndices[k+1].x;
				int count = textArray[selectIndices[k].y].count;
				for (j=start; j<=end && j<count; j++) {					
					//Typeface typeface;
					isBold = textArray[selectIndices[k].y].characters[j].isBold;
					isItalic = textArray[selectIndices[k].y].characters[j].isItalic;
					/*if (isBold && isItalic) 
						typeface = Font.getTypeface(typefaceName+"_Bold_Italic");
					else if (isBold)
						typeface = Font.getTypeface(typefaceName+"_Bold");
					else if (isItalic)
						typeface = Font.getTypeface(typefaceName+"_Italic");
					else
						typeface = Font.getTypeface(typefaceName+"_Normal");*/
					FontFamily family = Font.fromString(typefaceName);
					if (isBold && isItalic)
						typeface = Font.getTypeface(family, true, true);
					else if (isBold)
						typeface = Font.getTypeface(family, true, false);
					else if (isItalic)
						typeface = Font.getTypeface(family, false, true);
					else
						typeface = Font.getTypeface(family, false, false);
					/*if (isBold) typeface = Font.getTypeface(null, true, false);
					else typeface = Font.getTypeface(null, false, false);*/
					textArray[selectIndices[k].y].characters[j].typeface = 
							typeface;
					textArray[selectIndices[k].y].characters[j].typefaceName = 
							typefaceName;
					
				}
			}
			realign();
		}
	}
	
	void colorDialog_Listener(Object sender, int color) {
		if (isSelecting==false) {
			//fontSize = Float.parseFloat(strFontSize);
			//int toolbarButtonIndex = toolbar.findIndex("S");
			//toolbar.buttons[toolbarButtonIndex].setText(""+fontSize);
			curColor = color;
			toolbar.buttons[2].backColor = curColor;			
		}
		else {
			int k, j;
			//fontSize = Float.parseFloat(strFontSize);
			//int toolbarButtonIndex = toolbar.findIndex("S");
			//toolbar.buttons[toolbarButtonIndex].setText(""+fontSize);
			curColor = color;
			toolbar.buttons[2].backColor = curColor;
			for (k=0; k<selectIndicesCount; k+=2) {
				int start = selectIndices[k].x;
				int end = selectIndices[k+1].x;
				int count = textArray[selectIndices[k].y].count;
				for (j=start; j<=end && j<count; j++) {
					textArray[selectIndices[k].y].characters[j].setCharColor(curColor);
				}
			}
			// 폰트, 폰트크기 등이 바뀐 경우 다시 줄을 맞춘다. 
			realign();
		}
	}
	
	void fontSizeMenu_Listener(Object sender, String strFontSize) {
				
		Edit.menuFontSize.open(null, false);
		if (strFontSize.equals(Edit.Menu_FontSize[Edit.Menu_FontSize.length-1])) {
			// 모든 EditText들이 findReplaceDialog을 공유하므로 changeBounds를 해준다.			
			Rectangle newBounds = new Rectangle(totalBounds.x, totalBounds.y, totalBounds.width, totalBounds.height/2);
			Edit.fontSizeDialog.changeBounds(newBounds);
			fontSizeDialog.open(this);
			//fontSizeDialog.setOnTouchListener(this);
			return;
		}
		if (isSelecting==false) {
			int indexOfPercent = strFontSize.indexOf("%");
			if (indexOfPercent!=-1) {
				strFontSize = strFontSize.substring(0, indexOfPercent);
			}
			strFontSize = strFontSize.substring(0, indexOfPercent);
			fontSize = Float.parseFloat(strFontSize) * view.getHeight() * 0.01f;
			
			//int toolbarButtonIndex = toolbar.findIndex("S");
			toolbar.buttons[0].setText(""+fontSize);
			if (cursorPos.x<textArray[cursorPos.y].count) {
				if (textArray[cursorPos.y].characters[cursorPos.x].bitmap==null) 
					textArray[cursorPos.y].characters[cursorPos.x].size = fontSize;
			}
			else {
				
			}
			// 폰트, 폰트크기 등이 바뀐 경우 다시 줄을 맞춘다. 
			realign();
		}
		else {
			int k, j;
			
			int indexOfPercent = strFontSize.indexOf("%");
			if (indexOfPercent!=-1) {
				strFontSize = strFontSize.substring(0, indexOfPercent);
			}
			strFontSize = strFontSize.substring(0, indexOfPercent);
			fontSize = Float.parseFloat(strFontSize) * view.getHeight() * 0.01f;
			
			toolbar.buttons[0].setText(""+fontSize);
			for (k=0; k<selectIndicesCount; k+=2) {
				int start = selectIndices[k].x;
				int end = selectIndices[k+1].x;
				int count = textArray[selectIndices[k].y].count;
				for (j=start; j<=end && j<count; j++) {
					if (textArray[selectIndices[k].y].characters[j].bitmap==null) 
						textArray[selectIndices[k].y].characters[j].size = fontSize;						
				}
			}
			// 폰트, 폰트크기 등이 바뀐 경우 다시 줄을 맞춘다. 
			realign();
		}
	}
	
	void fileDialog_Listener(Object sender, String filename) {		
		if (fileDialog.category==FileDialog.Category.Image) {			
			Bitmap bitmap = null;
			FileInputStream stream=null;
			BufferedInputStream bis=null;
			boolean error = false;
			try {
				stream = new FileInputStream(fileDialog.curDir+filename);
				int bufferSize = (int) (FileHelper.getFileSize(fileDialog.curDir+filename)*IO.DefaultBufferSizeParam);
				bis = new BufferedInputStream(stream, bufferSize);
				bitmap = ContentManager.LoadBitmap(context, bis);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				Log.e("Load Error", e1.toString());
				CommonGUI.loggingForMessageBox.setHides(false);
				CommonGUI.loggingForMessageBox.setText(true, Control.res.getString(R.string.load_error_file_not_found), false);
				error = true;
			}
			catch (OutOfMemoryError e1) {
				Log.e("Load Error", e1.toString());
				CommonGUI.loggingForMessageBox.setHides(false);
				CommonGUI.loggingForMessageBox.setText(true, Control.res.getString(R.string.outof_memory_error), false);
				error = true;
			}
			catch (Exception e1) {
				// TODO Auto-generated catch block
				Log.e("Load Error", e1.toString());
				CommonGUI.loggingForMessageBox.setHides(false);
				CommonGUI.loggingForMessageBox.setText(true, Control.res.getString(R.string.load_error), false);
				e1.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
				error = true;
			}
			finally {
				FileHelper.close(bis);
				FileHelper.close(stream);
				if (error) return;
			}
			
			Character charA = new Character(bitmap, fileDialog.filename);
			Character[] chars = {charA};
			
			try {		 
			textArray[cursorPos.y].resize(textArray[cursorPos.y].count+1);
			TextLine charATextLine = (new TextLine(chars,chars.length));
			
			Character[] src = charATextLine.characters;
			textArray[cursorPos.y].insert(src, 0, cursorPos.x, chars.length);
			//textArray[cursorPos.y].setLineHeight(30);
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
			
			TextLine newText=null;
			newText = TextArrayToText(cursorPos.y, cursorPos.x, 1);
			setTextMultiLine(cursorPos.y, newText, -1, 1);
		}
	}
	
	void realign() {
		int k;
		if (scrollMode==ScrollMode.VScroll) {
			makingSelectP1P2OutOfEvent = true;
			TextLine newText = TextArrayToText(selectIndices[0].y, 0, cursorPos.y, cursorPos.x);
			setText(selectIndices[0].y, newText);			
		}
		else {
			if (isSelecting==false) {
				textArray[cursorPos.y].setLineHeight(30);
			}
			else {
				// Both인 경우 textArray가 바뀌지 않으므로
				for (k=0; k<selectIndicesCount; k+=2) {
					textArray[selectIndices[k].y].setLineHeight(30);
				}
			}
			setVScrollPos();
			setHScrollPos();
			setVScrollBar(true);
			setHScrollBar(true);
		}
	}
	
	/** EditRichText의 이벤트 리스너, 이후에 draw가 호출된다*/
	void editRichText_Listener(Object sender, MotionEvent e) {
		// scrollMode가 VScroll인지 Both인지는 상관이 없다. 
		// getCursorPos에서 처리하기 때문이다.
		if (e.actionCode==MotionEvent.ActionDown) {
			// 기존 선택한 텍스트 위치를 backup한다.
			if (isSelecting) {
				selectStartLine = selectIndices[0].y;
				selectEndLine = selectIndices[selectIndicesCount-2].y;
			}
			
			cursorPos = getCharPos(e);
			setToolbarAndCurState(cursorPos);
			isSelecting = false;
			isFound = false;
			this.selectLenY = 0;
			this.selectP1 = new Point(cursorPos.x,cursorPos.y);
			selectIndicesCount = 0; 
		}
		
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
			Point charPos = getCharPos(e);
			if (isSelecting) { // 자동 스크롤 한다.				
				// scrollMode에 상관없이 자동 스크롤 한다.
				if (e.y>bounds.bottom()/*-hScrollBarHeight*/) {
					if (heightOfLines>heightOfLinesPerPage) {
						setHeightOfVScrollPos(heightOfvScrollInc);						
						setVScrollBar(true);
						Point r = charPos;
						selectP2 = new Point(r.x,r.y);					
						makeSelectIndices(true, selectP1, selectP2);
						return;
					}							
				}
				else if (e.y<bounds.y){
					if (heightOfLines>heightOfLinesPerPage) {
						setHeightOfVScrollPos(-heightOfvScrollInc);
						setVScrollBar(true);
						Point r = charPos;
						selectP2 = new Point(r.x,r.y);					
						makeSelectIndices(true, selectP1, selectP2);
						return;
					}
				}
				
				if (scrollMode==ScrollMode.Both) {
					if (e.x>bounds.right()-vScrollBarWidth) {
						float widthOfCurLine = paint.measureText(textArray[charPos.y]);
						if (widthOfCurLine>widthOfCharsPerPage) {
							setWidthOfHScrollPos(widthOfhScrollInc);
							setHScrollBar(true);
							Point r = charPos;
							selectP2 = new Point(r.x,r.y);					
							makeSelectIndices(true, selectP1, selectP2);
							return;
						}							
					}
					else if (e.x<bounds.x){
						float widthOfCurLine = paint.measureText(textArray[charPos.y]);
						if (widthOfCurLine>widthOfCharsPerPage) {
							setWidthOfHScrollPos(-widthOfhScrollInc);
							setHScrollBar(true);
							Point r = charPos;
							selectP2 = new Point(r.x,r.y);					
							makeSelectIndices(true, selectP1, selectP2);
							return;
						}
					}
				}
			}	// if (isSelecting==true)
			Point r = charPos;
			if (e.actionCode==MotionEvent.ActionMove) {
				isSelecting = true;
			}
			selectP2 = new Point(r.x,r.y);					
			makeSelectIndices(true, selectP1, selectP2);
			
		} // if (e.actionCode==MotionEvent.ActionMove)
		
		
		}catch(Exception e1) {
			e1.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
			return;
		}
		finally {
			// 터치시 한글모드와 버퍼를 초기화	
			Hangul.mode = Hangul.Mode.None;
			Hangul.resetBuffer();
		}
	}
	
	void integrationKeyboard_Listener(Object sender, MotionEvent e) {
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
		
		
		
		keyboardMode = keyboard.mode;
		hangulMode = Hangul.mode;
		
		if (keyboard.mode!=Mode.Hangul) {
			try {
				addChar(keyboard.key/*, false*/);
			}catch(Exception e1) {
				e1.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
			}
		}
		else {
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
					e1.printStackTrace();
					CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
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
					e1.printStackTrace();
					CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
				}*/
				addChar(keyboard.key/*, false*/);
			}
			else {
				try {
				replaceChar(keyboard.key);
				}catch(Exception e1) {
					e1.printStackTrace();
					CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
				}
			}
			
		} // keyboard.mode==Mode.Hangul
		//Hangul.isNextToCursor = false;
	}
	
	
	/** find()에서 listFindPos이 처음과 마지막에서 중복되는 좌표를 제거한다.*/ 
	ArrayList refine(ArrayList listFindPos) {
		int i;
		Point first1, first2;
		Point last1, last2;
		if (listFindPos.count==2) {
			first1 = (Point) listFindPos.getItem(0);
			first2 = (Point) listFindPos.getItem(1);
		}
		else if (listFindPos.count>2) {
			int count = listFindPos.count;
			first1 = (Point) listFindPos.getItem(0);
			first2 = (Point) listFindPos.getItem(1);
			last1 = (Point) listFindPos.getItem(count-2);
			last2 = (Point) listFindPos.getItem(count-1);
			if (first1.x==last1.x && first1.y==last1.y && 
					first2.x==last2.x && first2.y==last2.y) {
				listFindPos.count-=2;
			}
		}
		return listFindPos;
		
	}
	
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		errorMessage = null;
		try {
			if (sender==this) {
				editRichText_Listener(sender, e);
				return;
			}
			else { // if (sender!=this)
								
				if (sender instanceof Button) {
					Button button = (Button)sender;
					
					isBold = toolbar.buttons[3].isSelected;
					isUnderline = toolbar.buttons[4].isSelected;					
					isItalic = toolbar.buttons[5].isSelected;
					int i;
					// "S", "F", "C", "B", "U", "I", "P"
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
					
					// "Default", "SansSerif", "Serif"
					for (i=0; i<Menu_Typeface.length; i++) {	// 메뉴버튼
						//if (button.name.equals(Menu_Typeface[i])) {
						if (button.iName==menuTypeface.buttons[i].iName) {
							typefaceMenu_Listener(button, Menu_Typeface[i]);
							return;
						} // if
					}
					
					for (i=0; i<Edit.Menu_Function.length; i++) {	// 메뉴버튼
						//if (button.name.equals(Menu_FontSize[i])) {
						if (button.iName==Edit.menuFunction.buttons[i].iName) {	
							functionMenu_Listener(button, Edit.Menu_Function[i]);
							return;
						} 
					}
					
				}
				else if (sender instanceof VScrollBar) {
					VScrollBar vScrollBar = (VScrollBar)sender;
					this.heightOfvScrollPos = vScrollBar.heightOfvScrollPos;
					// setVScrollPos()가 아니라 setVScrollPos(heightOfvScrollPos)을 사용한다.
					setVScrollPos(heightOfvScrollPos);
					setVScrollBar(false);
					// 수직 스크롤바 터치시 수평스크롤 부분도 바뀌므로 호출해야 한다.
					if (scrollMode==ScrollMode.Both) {
						setHScrollBar(false);
					}
				}
				else if (sender instanceof HScrollBar) {
					HScrollBar hScrollBar = (HScrollBar)sender;
					this.widthOfhScrollPos = hScrollBar.widthOfScrollPos;
					setHScrollBar(false);
					// 수평 스크롤바 터치시 수직스크롤 부분은 바뀌지 않으므로 호출하지 않는다.
					//setVScrollPos(heightOfvScrollPos);
					//setVScrollBar(false);
				}
				else if (sender instanceof IntegrationKeyboard) {
					integrationKeyboard_Listener(sender, e);
				} // if (className.equals(IntegrationKeyboard))
				else if (sender instanceof FontSizeDialog) {
					fontSizeMenu_Listener(sender, fontSizeDialog.curText);
				}
				else if (sender instanceof FindReplaceDialog) {
					FindReplaceDialog dialog = (FindReplaceDialog)sender;
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
				else if (sender instanceof ColorDialog) {
					colorDialog_Listener(sender, colorDialog.selectedColor);
				}
				else if (sender instanceof FileDialog) {
					FileDialog dialog = (FileDialog)sender;
					if (dialog.getIsOK()==true) {
						if (dialog.iName!=fileDialog.iName) return;
						fileDialog_Listener(sender, fileDialog.filename);
					}
				}
			}
		}catch (Exception e1) {
			e1.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
		}
		
	}
	
	public TextLine getCurChar() {
		if (cursorPos.x<textArray[cursorPos.y].count) {
			return textArray[cursorPos.y].subTextLine(cursorPos.x, cursorPos.x+1);
		}
		return null;
	}
	
	public Point getCharPos(MotionEvent e) {
		if (cursorPos.y<0) cursorPos.y=0;
		if (cursorPos.y>=numOfLines) cursorPos.y=numOfLines-1;
		
		Point r = new Point();
		try{
		if (scrollMode==ScrollMode.VScroll) {
			int i;			
			float h = -partOfCharY;
			float eventY = e.y - bounds.y;
			r.y = numOfLines-1;
			for (i=vScrollPos; i<numOfLines; i++) {
				h += textArray[i].lineHeight;
				if (eventY <= h) {
					r.y = i;
					break;				
				}
			}
			if (r.y>=numOfLines) r.y = numOfLines-1;
			
			
			float lineWidth = bounds.x + gapX;
			
			int lineLen = textArray[r.y].count;
			TextLine charA=null;
			for (i=0; i<lineLen; i++) {
				charA = textArray[r.y].subTextLine(i, i+1);
				if (charA.equals(NewLineChar)) break;
				lineWidth += paint.measureText(charA);
				if (e.x<=lineWidth) {				
					break;
				}
			}
			TextLine lastChar = charA;
			if (lineLen==0) r.x = 0;
			else {	// lineLen>0
				if (i < lineLen) {
					if (i==0) r.x = 0;
					else {	// 0<i && i<lineLen		문자열 클릭
						if (lastChar.equals(NewLineChar)) r.x = i; 
						// 커서는 \n을 가리킨다.
						else r.x = i;
					}
				}
				else {	// i==lineLen 문자열을 넘어서 클릭
					if (lastChar.equals(NewLineChar)) r.x = (lineLen-1);
					// 커서는 \n을 가리킨다.
					else r.x = lineLen;
				}
			}
			
			
		}
		else if (scrollMode==ScrollMode.Both) {		
			int i;			
			float h = -partOfCharY;
			r.y = numOfLines-1;
			float eventY = e.y - bounds.y;
			for (i=vScrollPos; i<numOfLines; i++) {
				h += textArray[i].lineHeight;
				if (eventY <= h) {
					r.y = i;
					break;				
				}
			}
			if (r.y>=numOfLines) r.y = numOfLines-1;
			if (r.y<0) r.y = 0; 			
			
			float eventXRelative = e.x-(bounds.x+gapX); 
			float lineWidth = 0;
			
			int lineLen = textArray[r.y].count;
			TextLine charA=null;
			for (i=0; i<lineLen; i++) {
				charA = textArray[r.y].subTextLine(i, i+1);
				if (charA.equals(NewLineChar)) {
					break;
				}
				lineWidth += paint.measureText(charA);
				if (widthOfhScrollPos+eventXRelative <= lineWidth) {				
					break;
				}
			}
			TextLine lastChar = charA;
			if (lineLen==0) {
				r.x = 0;
			}
			else {	// lineLen>0
				if (i < lineLen) {
					//if (i==0) r.x = 0;
					//else {	// 0<i && i<lineLen		문자열 클릭
						if (lastChar.equals(NewLineChar)) {
							// 커서는 \n을 가리킨다.
							r.x = i; 
						}						
						else r.x = i;
					//}
				}
				else {	// i==lineLen 문자열을 넘어서 클릭
					r.x = lineLen;
					// 커서는 마지막문자의 뒤를 가리킨다.
				}
			}
			
		}//if (scrollMode==ScrollMode.Both)
		}catch(Exception e1) {
			e1.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
			return null;
		}
		return r;
	}
	
	/**fontSize를 커서의 문자크기로 set한다. 
	 * 툴바와 현재상태를 현재 커서가 가리키는 문자의 상태로 바꾼다. 이후에 addChar, replaceChar에서 그 툴바상태로 문자를 입력한다.*/
	void setToolbarAndCurState(Point cursorPos) {
		/*int color;
		boolean isUnderline;
		Typeface typeface;
		String typefaceName;
		boolean isBold;
		boolean isItalic;
		float fontSize;*/
		if (textArray[cursorPos.y].count==0) {
			fontSize = initFontSize;
			curColor = textColor;
			isUnderline = false;
			typeface = Control.typefaceDefault;
			typefaceName = Menu_Typeface[0];
			isBold = false;
			isItalic = false;
		}
		else if (textArray[cursorPos.y].count>cursorPos.x) {
			Character c = textArray[cursorPos.y].characters[cursorPos.x];
			fontSize = c.size;
			curColor = c.charColor;
			isUnderline = c.isUnderLine;
			isBold = c.isBold;
			isItalic = c.isItalic;
			typeface = c.typeface;
			typefaceName = c.typefaceName;
			
			/*toolbar.buttons[2].backColor = curColor;
			toolbar.buttons[3].Select(isBold);
			toolbar.buttons[4].Select(isUnderline);
			toolbar.buttons[5].Select(isItalic);*/
		}
		else {
			// 그 줄의 마지막 문자의 size로 fontSize를 정한다.
			Character c = textArray[cursorPos.y].characters[textArray[cursorPos.y].count-1];
			fontSize = c.size;
			curColor = c.charColor;
			isUnderline = c.isUnderLine;
			isBold = c.isBold;
			isItalic = c.isItalic;
			typeface = c.typeface;
			typefaceName = c.typefaceName;
			
		}
		toolbar.buttons[0].setText(""+fontSize);
		toolbar.buttons[2].backColor = curColor;
		toolbar.buttons[3].isSelected = isBold;
		toolbar.buttons[4].isSelected = isUnderline;
		toolbar.buttons[5].isSelected = isItalic;
	}
	
	public boolean endsWithNewLineChar(TextLine lineText) {
		int lineLen = lineText.count;
		if (lineLen > 0) {
			if (lineText.subTextLine(lineLen-1, lineLen).equals(NewLineChar)) {
				return true;
			}
			return false;
		}
		return false;		
	}
	
	TextLine deleteNewLineChar(TextLine lineText) {
		int lineLen = lineText.count;
		if (lineLen > 0) {
			if (lineText.subTextLine(lineLen-1, lineLen).equals(NewLineChar)) {
				if (lineLen > 1)
					return lineText.subTextLine(0, lineLen-1);
				else 
					return new TextLine(0,0);
			}
			else {
				return lineText;
			}
		}
		else {
			return new TextLine(0,0);
		}
	}
	
	PartOfStr getStringHScroll(TextLine str) {
		try{
		int start=-1, end=-1;
		float w=0;
		float part1OfChar=0;
		float part2OfChar=0;
		w= widthOfhScrollPos;
		int i;
		float lineWidth=0;
		for (i=0; i<str.count; i++) {
			TextLine charA = str.subTextLine(i, i+1);
			float wOfCharA = paint.measureText(charA); 
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
		}
		else {
			startIndex = start+1;
			lineWidth = part2OfChar;
			end = start;
		}
		if (start!=-1) {
			for (i=startIndex; i<str.count; i++) {
				TextLine charA = str.subTextLine(i, i+1);
				lineWidth += paint.measureText(charA);
				if (lineWidth<=rationalBoundsWidth) {
					end = i;
				}				
				else { // 넘은 문자 포함
					end = i;
					break;
				}
			}
		}
		if (start==-1 || end==-1) {
			return new PartOfStr(new TextLine(0,0),start,end,
					part1OfChar,part2OfChar);
		}
		else {
			return new PartOfStr(str.subTextLine(start, end+1),start,end,
					part1OfChar,part2OfChar);
		}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return null;
		}
			
	}
	
	class PartOfStr {
		TextLine str;
		int start=-1;
		int end=-1;
		float part1OfChar=0;
		float part2OfChar=0;
		
		PartOfStr(TextLine str, int start, int end, 
				float part1OfChar, float part2OfChar) {
			this.str = str;
			this.start = start;
			this.end = end;
			this.part1OfChar=part1OfChar;
			this.part2OfChar=part2OfChar;
		}
	}
	
	/** 스크롤 윈도우와 선택영역의 교집합을 구한다.*/
	Point[] getIntersectWithSelect(boolean isSelectingOrFinding) {
		try{
		if (isSelectingOrFinding) {
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
					if (str.str.count==0) {
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
		else {
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
					if (str.str.count==0) {
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
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
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
	
	PartOfStr getStringHScrollAndCursorVisuablity(TextLine str) {
		try{
		int start=-1, end=-1;
		float part1OfChar=0;
		float part2OfChar=0;		
		
		if (str.equals("") || str.equals("\n")) {			
			if (widthOfhScrollPos>=fontSize) {
				isCursorSeen = false;
				return new PartOfStr(new TextLine(0,0),start,end,
						part1OfChar,part2OfChar);
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
			part1OfChar=result.part1OfChar;
			part2OfChar=result.part2OfChar;
		}
		
		if (start==-1 || end==-1) {
			isCursorSeen = false;
			return new PartOfStr(new TextLine(0,0),start,end,
					part1OfChar,part2OfChar);
		}
		else {
			int endIndex;
			if (CommonGUI.keyboard.mode==Mode.Hangul && Hangul.mode!=Hangul.Mode.None) {
				endIndex = end;
			}
			else {
				endIndex = end+1;
			}
			//endIndex = end+1;
			if (start<=cursorPos.x && cursorPos.x<=endIndex) {
				isCursorSeen = true;
				valueOfCursorRelativeToHScroll = 0;
				return new PartOfStr(str.subTextLine(start, cursorPos.x),start,end,
						part1OfChar,part2OfChar);
				// return str.subTextLine(start, cursorPos.x);
			}
			else {
				if (cursorPos.x<start) valueOfCursorRelativeToHScroll = -1;
				else valueOfCursorRelativeToHScroll = 1;
				isCursorSeen = false;
				//return new TextLine(0,0);
				return new PartOfStr(new TextLine(0,0),start,end,
						part1OfChar,part2OfChar);
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return null;
		}
	}
	
	void drawErrorMessage(Canvas canvas) {
		float x, y;
		paint.setColor(Color.RED);
		float w = paint.measureText(errorMessage);
		x = bounds.x + bounds.width/2 - w/2;
		y = bounds.y + bounds.height/2 - paint.getTextSize()/2;
		canvas.drawText(errorMessage, x, y, paint);
	}
	
	/*@Override
	public void run() {
		while (!mExit) {
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				if (mStartDrawing) {
					draw(mCanvas);
					mStartDrawing = false;
				}
			}		
		}
	}
	
	public void startDrawing(Canvas canvas) {		
		mStartDrawing = true;
		this.mCanvas = canvas;
		this.mDrawingThread.interrupt();
	}*/
	
	/** 실행순서에서 제일 마지막에 그린다.
	 * 실행순서 : constructor->(add or replace Char)->setText or setTextOneLine->
	 * setVScrollBar or setHScrollBar->draw*/
	public synchronized void draw(Canvas canvas) {
		synchronized(this) {
		try{
		if (hides) return;
				
		//if (!isReadOnly) backColor = Color.WHITE;
		//else backColor = Color.LTGRAY;
		
		canvas.save();
		
		paint.setColor(backColor);
		//paint.setTypeface(typeface);
				
		canvas.drawRect(bounds.toRectF(), paint);		
    	canvas.drawRect(bounds.toRectF(), paintOfBorder);
    	
    	    	
    	Rect clipRect = new Rect();
    	clipRect.left = (int) (bounds.x+gapX); 
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
    	if (!canvas.clipRect(clipRect, Region.Op.REPLACE)) {
    		Log.e("editRichText onDraw","No clipping");
    	}
    	
		if (isSingleLine) {
			RectangleF locAndSizeOfText = Font.getLocAndTextSize(paint, bounds, 
    				text, FontSortVert.Middle, 0);
    		paint.setTextSize(locAndSizeOfText.height);
    		paint.setColor(textColor);
    		//paint.setAlpha(60);
    		canvas.drawText(text, locAndSizeOfText.x, locAndSizeOfText.y, paint);
    		//paint.setAlpha(255);
		}
		else {
			int i, j;
			float x, y, y1, y2;
			
			try{				
				if (isSelecting) {
					if (scrollMode==ScrollMode.VScroll && makingSelectP1P2OutOfEvent) {
						makeSelectIndices(true, selectP1Logical,selectP2Logical);
						makingSelectP1P2OutOfEvent = false;
					}
					Point[] intersectedSelect = getIntersectWithSelect(true);
					if (intersectedSelect!=null && intersectedSelect.length!=0) {
						y1 = bounds.y + TextLine.measureHeight(textArray, vScrollPos, intersectedSelect[0].y);
						y1 -= partOfCharY;
						for (i=0; i<intersectedSelect.length; i+=2) {
							j = intersectedSelect[i].y;
							y1 += TextLine.measureHeight(textArray, j, j+1);
							float charSize;
							float selectHeight;
							RectangleF dst = null;
							float x2;
							charSize = textArray[j].maxFontSize;
							y2 = y1 - textArray[j].descent; // baseline
							selectHeight = charSize + textArray[j].descent + textArray[j].leading;
							y = y2 - (charSize+textArray[j].leading);
							
							float w;
							if (selectLenY==1 && intersectedSelect[i+1].y==Select_FirstLine) {									
								TextLine str = textArray[j].subTextLine(0, intersectedSelect[0].x);
								if (str!=null) {
									x = bounds.x + gapX + paint.measureText(str);
									TextLine str2;
									if (intersectedSelect[1].x+1<=textArray[j].count)
										str2 = textArray[j].subTextLine(0, intersectedSelect[1].x+1);
									else 
										str2 = textArray[j].subTextLine(0, intersectedSelect[1].x);
									x2 = bounds.x + gapX + paint.measureText(str2);
									if (scrollMode==ScrollMode.Both) {
										x -= this.widthOfhScrollPos;
										x2 -= this.widthOfhScrollPos;									
									}
									w = x2 - x;
									dst = new RectangleF(x, y, w, selectHeight);
								}
							}
							else if (selectLenY>1 && intersectedSelect[i+1].y==Select_FirstLine) {
								TextLine str = null;
								if (intersectedSelect[0].x>=0)
									str = textArray[j].subTextLine(0, intersectedSelect[0].x);
								if (str!=null) {
									x = bounds.x + gapX + paint.measureText(str);
									if (scrollMode==ScrollMode.Both) {
										x -= this.widthOfhScrollPos;
										if (x<bounds.x+gapX) x = 0;
									}
									w = (bounds.x + bounds.width - vScrollBarWidth) - x;
									dst = new RectangleF(x, y, w, selectHeight);
								}
							}
							else if (selectLenY>1 && intersectedSelect[i+1].y==Select_MiddleLine) {
								x = bounds.x;
								w = (bounds.x + bounds.width - vScrollBarWidth) - x;
								dst = new RectangleF(x, y, w, selectHeight);
							}
							else if (selectLenY>1 && intersectedSelect[i+1].y==Select_LastLine) {
								TextLine str;
								if (intersectedSelect[intersectedSelect.length-1].x+1<=textArray[j].count)
									str = textArray[j].subTextLine(
											0, intersectedSelect[intersectedSelect.length-1].x+1);
								else 
									str = textArray[j].subTextLine(
											0, intersectedSelect[intersectedSelect.length-1].x);
								if (str!=null) {
									x = bounds.x;
									w = (bounds.x + gapX + paint.measureText(str)) - x;
									if (scrollMode==ScrollMode.Both) {
										w -= this.widthOfhScrollPos;
									}
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
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
			try {
			if (isFound) {
				/*if (scrollMode==ScrollMode.VScroll && makingSelectP1P2OutOfEvent) {
					makeSelectIndices(selectP1Logical,selectP2Logical);
					makingSelectP1P2OutOfEvent = false;
				}*/
				Point[] intersectedSelect = getIntersectWithSelect(false);
				if (intersectedSelect!=null && intersectedSelect.length!=0) {
					y1 = bounds.y + TextLine.measureHeight(textArray, vScrollPos, intersectedSelect[0].y);
					y1 -= partOfCharY;
					for (i=0; i<intersectedSelect.length; i+=2) {
						j = intersectedSelect[i].y;
						y1 += TextLine.measureHeight(textArray, j, j+1);
						float charSize;
						float findHeight;
						RectangleF dst = null;
						float x2;
						charSize = textArray[j].maxFontSize;
						y2 = y1 - textArray[j].descent; // baseline
						findHeight = charSize + textArray[j].descent + textArray[j].leading;
						y = y2 - (charSize+textArray[j].leading);
						
						float w;
						if (findLenY==1 && intersectedSelect[i+1].y==Select_FirstLine) {									
							TextLine str = textArray[j].subTextLine(0, intersectedSelect[0].x);
							if (str!=null) {
								x = bounds.x + gapX + paint.measureText(str);
								TextLine str2;
								if (intersectedSelect[1].x+1<=textArray[j].count)
									str2 = textArray[j].subTextLine(0, intersectedSelect[1].x+1);
								else 
									str2 = textArray[j].subTextLine(0, intersectedSelect[1].x);
								x2 = bounds.x + gapX + paint.measureText(str2);
								if (scrollMode==ScrollMode.Both) {
									x -= this.widthOfhScrollPos;
									x2 -= this.widthOfhScrollPos;									
								}
								w = x2 - x;
								dst = new RectangleF(x, y, w, findHeight);
							}
						}
						else if (findLenY>1 && intersectedSelect[i+1].y==Select_FirstLine) {
							TextLine str = textArray[j].subTextLine(0, intersectedSelect[0].x);
							if (str!=null) {
								x = bounds.x + gapX + paint.measureText(str);
								if (scrollMode==ScrollMode.Both) {
									x -= this.widthOfhScrollPos;
									if (x<bounds.x+gapX) x = 0;
								}
								w = (bounds.x + bounds.width - vScrollBarWidth) - x;
								dst = new RectangleF(x, y, w, findHeight);
							}
						}
						else if (findLenY>1 && intersectedSelect[i+1].y==Select_MiddleLine) {
							x = bounds.x;
							w = (bounds.x + bounds.width - vScrollBarWidth) - x;
							dst = new RectangleF(x, y, w, findHeight);
						}
						else if (findLenY>1 && intersectedSelect[i+1].y==Select_LastLine) {
							TextLine str;
							if (intersectedSelect[intersectedSelect.length-1].x+1<=textArray[j].count)
								str = textArray[j].subTextLine(
										0, intersectedSelect[intersectedSelect.length-1].x+1);
							else 
								str = textArray[j].subTextLine(
										0, intersectedSelect[intersectedSelect.length-1].x);
							if (str!=null) {
								x = bounds.x;
								w = (bounds.x + gapX + paint.measureText(str)) - x;
								if (scrollMode==ScrollMode.Both) {
									w -= this.widthOfhScrollPos;
								}
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
			}// if (isFound)
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
			
			try {
				//int i, j;
				//float x=0, y=0, y1=0, y2=0;
				
				//paint.setTextSize(fontSize);
	    		paint.setColor(textColor);
	    		//paint.setAlpha(60);
	    		
	    		if (scrollMode==ScrollMode.VScroll) {
	    			y1 = bounds.y;
	    			y1 -= partOfCharY;
	    			float oldTextSize = paint.getTextSize();
	    			int oldColor = paint.getColor();
	    			Typeface oldTypeface = paint.getTypeface();
	    			int limit = Math.min(vScrollPos+numOfLinesPerPage, this.numOfLines);
					for (i=vScrollPos; i<limit; i++) {
						x = bounds.x + gapX;
						//y = bounds.y + (lineY+1) * lineHeight;
						y1 += textArray[i].lineHeight;						
						y2 = y1 - textArray[i].descent; // baseline
						//if (y1 < bounds.y+bounds.height) {
							TextLine lineText = deleteNewLineChar(textArray[i]);
							for (j=0; j<lineText.count; j++) {
								if (lineText.characters[j].bitmap==null) {
									paint.setTextSize(lineText.characters[j].size);
									paint.setTypeface(lineText.characters[j].typeface);
									paint.setColor(lineText.characters[j].charColor);
									if (lineText.characters[j].isUnderLine)
										paint.setUnderlineText(true);
									else paint.setUnderlineText(false);
									String c = lineText.characters[j].toString();
									//paint.setColor(lineText.characters[j].color);
									canvas.drawText(c, x, y2, paint);
									x += paint.measureText(c);
								}
								else {
									Bitmap bitmap = lineText.characters[j].bitmap;
									canvas.drawBitmap(bitmap, x, (y1+y2)/2-lineText.characters[j].size, paint);
									x += bitmap.getWidth();
								}
							}
						//}					
					}
					paint.setTextSize(oldTextSize);
					paint.setTypeface(oldTypeface);
	    			paint.setColor(oldColor);
	    		}
	    		else if (scrollMode==ScrollMode.Both) {
	    			y1 = bounds.y;
	    			y1 -= partOfCharY;
	    			float oldTextSize = paint.getTextSize();
	    			int oldColor = paint.getColor();
	    			Typeface oldTypeface = paint.getTypeface();
	    			int limit = Math.min(vScrollPos+numOfLinesPerPage, this.numOfLines);
	    			for (i=vScrollPos; i<limit; i++) {
						x = bounds.x + gapX;
						y1 += textArray[i].lineHeight;
						y2 = y1 - textArray[i].descent;	// baseline
						//if (y1 < bounds.y+bounds.height) {
							TextLine lineText=null;
							lineText = deleteNewLineChar(textArray[i]);
							if ( !lineText.equals(new TextLine(0,0)) ) {
								PartOfStr lineTextHScroll=null;
								lineTextHScroll = getStringHScroll(lineText);
								x -= lineTextHScroll.part1OfChar;
								try {
									for (j=0; j<lineTextHScroll.str.count; j++) {
										if (lineTextHScroll.str.characters[j].bitmap==null) {
											paint.setColor(lineTextHScroll.str.characters[j].charColor);
											paint.setTextSize(lineTextHScroll.str.characters[j].size);
											paint.setTypeface(lineTextHScroll.str.characters[j].typeface);
											if (lineTextHScroll.str.characters[j].isUnderLine)
												paint.setUnderlineText(true);
											else paint.setUnderlineText(false);
											String c = lineTextHScroll.str.characters[j].toString();								
											canvas.drawText(c, x, y2, paint);
											x += paint.measureText(c);
										}
										else {
											Bitmap bitmap = lineTextHScroll.str.characters[j].bitmap;
											//canvas.drawBitmap(bitmap, x, (y1+y2)/2-lineTextHScroll.str.characters[j].size, paint);
											canvas.drawBitmap(bitmap, x, y2-lineTextHScroll.str.characters[j].size, paint);
											x += bitmap.getWidth();
										}
									}
								
									// 문자의 잘리는 부분을 backColor로 칠하여 지운다.
									/*x = bounds.x+gapX-lineTextHScroll.part1OfChar;
									paint.setColor(this.backColor);
									canvas.drawRect(new RectF(x,y1-lineTextHScroll.str.lineHeight+1,
											x+lineTextHScroll.part1OfChar,y1+lineTextHScroll.str.lineHeight), paint);
									paint.setColor(Color.BLACK);*/
								}catch(Exception e) {
									e.printStackTrace();
									CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
								}
							}
							
						//}//if (y1 < bounds.y+bounds.height) {					
					}
	    			paint.setTextSize(oldTextSize);
	    			paint.setTypeface(oldTypeface);
	    			paint.setColor(oldColor);
	    		}
	    		//paint.setAlpha(255);
	    		
	    		
				
	    		try{
				float w;
				x = 0;
				
				// 읽기모드와는 상관없이 키보드의 listener가 자신이면 커서를 그린다.
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
		    		if (cursorPos.x<0 || cursorPos.x>textArray[cursorPos.y].count) {
		    			isCursorSeen = false;
		    		}
	    		}
	    		else {
	    			isCursorSeen = false;
	    		}
	    		
	    		
				if (isCursorSeen && vScrollPos<=cursorPos.y && 
						cursorPos.y<vScrollPos+numOfLinesPerPage) {
					
					TextLine text;
					TextLine curLine = textArray[cursorPos.y];
					int lineLen = curLine.count;
					
					isCursorSeen = true;
					TextLine curChar=null;
					if (lineLen>0) {
						// 문자열 바깥을 클릭하면 getCharPos에서 마지막 문자가 \n인지 아닌지에 따라
						// 커서위치를 정해주므로 여기에서는 고려할 필요가 없고, 커서위치에 커서를 
						// 그려주기만 하면 된다.
						if (scrollMode==ScrollMode.Both) {
							PartOfStr textHScroll = getStringHScrollAndCursorVisuablity(curLine);
							if (isCursorSeen) {
								x = bounds.x + gapX + paint.measureText(textHScroll.str);
								x -= textHScroll.part1OfChar;
							}
						}
						else {
							text = curLine.subTextLine(0, cursorPos.x);
							float w1 = paint.measureText(text);
							x = bounds.x + gapX + w1;
						}
						if (cursorPos.x<lineLen) {
							curChar = curLine.subTextLine(cursorPos.x,cursorPos.x+1);
							w = paint.measureText(curChar);
						}
						else { // if (cursorPos.x>=lineLen) 문자열 바깥
							curChar = new TextLine(0,0);
							w = 0;							
						}						
					}
					else {	// if (lineLen<=0)
						if (scrollMode==ScrollMode.Both) {
							PartOfStr textHScroll = getStringHScrollAndCursorVisuablity(new TextLine(0,0));
							x = bounds.x + gapX + paint.measureText(textHScroll.str);
							w = 0;
						}
						else {
							x = bounds.x + gapX;
							w = 0;
						}
						curChar = new TextLine(0,0);
					}
					
					if (isCursorSeen) {
						y1 = bounds.y;
						y1 -= partOfCharY;
						y1 += TextLine.measureHeight(textArray, vScrollPos, cursorPos.y+1);
						float cursorHeight;
						y2 = y1 - textArray[cursorPos.y].descent;	// baseline
						
						if (cursorPos.x<textArray[cursorPos.y].count) {
							if (textArray[cursorPos.y].characters[cursorPos.x].bitmap==null) {
								descent = fontSize*descentRate;
								leading = fontSize*leadingRate;								
							}
							else {
								descent = textArray[cursorPos.y].descent;
								leading = textArray[cursorPos.y].leading;
							}
						}
						else {
							descent = fontSize*descentRate;
							leading = fontSize*leadingRate;							
						}
						cursorHeight = fontSize + descent + leading;
						y = y2 - (fontSize+leading);
						
						//y = y2 - (fontSize+leading);
						drawCursor(canvas, x, y, y1, w, cursorHeight, curChar);
					}
				}
	    		}catch(Exception e) {
	    			e.printStackTrace();
	    			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				}
	    		
	    		
	    		/*if (!canvas.clipRect(originBounds, Region.Op.REPLACE)) {
	    			Log.e("editRichText onDraw", "No clip release");
	    		}*/
	    		
	    		canvas.restore();
	    		
				
				if (scrollMode==ScrollMode.Both) {
					vScrollBar.draw(canvas);
					hScrollBar.draw(canvas);
				}
				else {
					vScrollBar.draw(canvas);
				}
				
				toolbar.draw(canvas);
				
				
					    		
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
		}
		
		
		}catch(Exception e) {
    		
    	}
		}
		
	}// draw
	
	
	void drawCursor(Canvas canvas, float x, float y, float y1, float w, float cursorHeight,
			TextLine curChar) {
		if ((CommonGUI.keyboard.mode==Mode.Hangul && !(Hangul.mode==Hangul.Mode.None)) 
				||  isReadOnly) {
			// 한글이 조합중이거나 읽기모드인 경우
			if (curChar!=null && !curChar.equals("") && !curChar.equals("\n")) {
				if (Control.CurrentSystem==Control.CurrentSystemIsAndroid) {
					RectangleF dst = new RectangleF(x, y, w, cursorHeight);
					paint.setColor(Color.LTGRAY);
					paint.setAlpha(100);
					canvas.drawRect(dst.toRectF(), paint);
					paint.setAlpha(255);
				}
				else {// 현재 시스템이 자바이면 알파가 불가능하므로 이렇게 한다.
					RectangleF dst;
					if (cursorHeight>heightOfLinesPerPage) {
						y = y1-initFontSize;
						dst = new RectangleF(x, y, 5, initFontSize);
					}
					else {
						dst = new RectangleF(x, y, 5, cursorHeight);
					}
					paint.setColor(Edit.cursorColor);
					canvas.drawRect(dst.toRectF(), paint);
				}
			}//if (curChar!=null && !curChar.equals("") && !curChar.equals("\n")) {
			else {
				// 읽기 모드인데 curChar가 \n이나 ""이더라도 커서를 보여줘야 한다.
				RectangleF dst;
				if (cursorHeight>heightOfLinesPerPage) {
					y = y1-initFontSize;
					dst = new RectangleF(x, y, 5, initFontSize);
				}
				else {
					dst = new RectangleF(x, y, 5, cursorHeight);
				}
				paint.setColor(Edit.cursorColor);
				canvas.drawRect(dst.toRectF(), paint);
			}
		}//if ((CommonGUI.keyboard.mode==Mode.Hangul && !(Hangul.mode==Hangul.Mode.None)) 
		 //		||  isReadOnly) {
		else {// 한글이 조합중도 아니고 읽기모드도 아닌 경우
			RectangleF dst;
			if (cursorHeight>heightOfLinesPerPage) {
				y = y1-initFontSize;
				dst = new RectangleF(x, y, 5, initFontSize);
			}
			else {
				dst = new RectangleF(x, y, 5, cursorHeight);
			}
			paint.setColor(Edit.cursorColor);
			canvas.drawRect(dst.toRectF(), paint);
		}
	}

}