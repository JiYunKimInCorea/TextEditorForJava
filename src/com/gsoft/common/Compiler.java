package com.gsoft.common;

import java.io.File;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Code.CodeStringType;
import com.gsoft.common.Compiler_types.Error;
import com.gsoft.common.Compiler_types.*;
import com.gsoft.common.Compiler_types.AccessModifier.AccessPermission;
import com.gsoft.common.Compiler_gui.MenuClassList;
import com.gsoft.common.Compiler_gui.MenuProblemList_EditText;
import com.gsoft.common.Compiler_gui.TextView;

import com.gsoft.common.ByteCode_Types.CONSTANT_Field_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Float_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Integer_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_String_info;
import com.gsoft.common.Compiler_types.AccessModifier;
import com.gsoft.common.Compiler_types.AddCharReallyMode;
import com.gsoft.common.Compiler_types.Block;
import com.gsoft.common.Compiler_types.CategoryOfControls;
import com.gsoft.common.Compiler_types.FindArrayInitializerParams;
import com.gsoft.common.Compiler_types.FindAssignStatementParams;
import com.gsoft.common.Compiler_types.FindClassParams;
import com.gsoft.common.Compiler_types.FindControlBlockParams;
import com.gsoft.common.Compiler_types.FindFuncCallParam;
import com.gsoft.common.Compiler_types.FindFunctionParams;
import com.gsoft.common.Compiler_types.FindSpecialBlockParams;
import com.gsoft.common.Compiler_types.FindSpecialStatementParams;
import com.gsoft.common.Compiler_types.FindStatementParams;
import com.gsoft.common.Compiler_types.FindVarParams;
import com.gsoft.common.Compiler_types.FindVarUseParams;
import com.gsoft.common.Util.HighArray;
import com.gsoft.common.Compiler_types.IReset;
import com.gsoft.common.Compiler_types.IndexForHighArray;
import com.gsoft.common.Compiler_types.Template;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.PostFixConverter.CodeStringEx;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListIReset;
import com.gsoft.common.Util.ArrayListChar;
import com.gsoft.common.Util.ArrayListInt;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.ArrayListCodeString;
import com.gsoft.common.Util.ArrayList_FindClassParams;
import com.gsoft.common.Util.Hashtable2;
import com.gsoft.common.Util.Hashtable_FullClassName;
import com.gsoft.common.Compiler_types.HighArray_CodeString;
import com.gsoft.common.Util.HighArray_char;
import com.gsoft.common.Util.ObjectPool;
import com.gsoft.common.Util.Stack;
import com.gsoft.common.gui.Control;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.EditText;
import com.gsoft.common.gui.EditText.ScrollMode;

public class Compiler {
	
	static boolean androidJavaLibAlreadyLoaded = false;
	static boolean gsoftAlreadyLoaded = false;
	static boolean projectAlreadyLoaded = false;
	
	
	static int StringArrayLimit = 200;
	
	public static int backColor = Color.WHITE;
	public static int textColor = Color.BLACK;
	public static int keywordColor = Color.MAGENTA;
	public static int varUseColor = ColorEx.darkerOrLighter(Color.YELLOW, -80);
	static int funcUseColor = Color.CYAN;
	static int memberDeclColor = Color.BLUE;
	static int commentColor = ColorEx.darkerOrLighter(Color.GREEN, -50);
	static int docuCommentColor = Color.RED;
	
	/** 프로그램 시작시 맨 처음 실행된다.*/
	/*static {
		if ((CompilerHelper.androidJavaLibAlreadyExists()==false || CompilerHelper.projectSrcAlreadyExists()==false)) {
			CompilerHelper.decompressAndroidAndProjectSrc();
		}
	}*/
	
	
	
	static String[] KeywordsOfHtml = {
		"html", "head", "body", "title", "link",
		
		"a", "address", "applet", "area", "audio", "b", "br", 
		"caption", "div", "col", "dt", "hr", "form", "iframe",
		"img", "input", "label", "li", "menu", "meta", "object",
		"option", "p", "script", "select", "strong", "style",
		"table", "td", "textarea", "th", "tr", "u", "var", "video"
	};
	
	static String[] KeywordsOfC = {
		// Modifiers
        "const",

        "null", "false", "true", 

        "switch", "break", "for", "return", "if", "do", "else", "in", 
        "try", "case", "goto", "continue", "while", 
        "throw", "finally", "catch", "foreach", "default",                                    

        "struct", "include", "new", "this", "operator",  

        "sizeof", // 타입 정보

        
	};
	
	static String[] KeywordsOfCSharp = {
		// Modifiers
        "private", "protected", "public", "final", "volatile", "sealed", "internal", 
        "static", "readonly", "unsafe", "extern", "abstract", "virtual", 
        "event", "const", "override", "default",

        "null", "false", "true", 

        "switch", "break", "for", "return", "if", "do", "else", "in", 
        "try", "case", "goto", "continue", "while", 
        "throw", "finally", "catch", "foreach", "default",                                    

        "struct", "explicit", "class", "ref", "enum", "namespace", 
        "implicit", "using", "new", "delegate", "interface", 
        "base", "this", "operator", "params", "super", 

        "typeof", "sizeof", "is", "as", // 타입 정보

        "checked", "unchecked", // 오버플로 예외 컨트롤

        "out", "fixed", "lock", "stackalloc"
	};
	
	static String[] KeywordsOfJava = {   
			
			/*"float", "int", "uint", "char", "short", "long", "void", "ulong", 
            "byte", "bool", "sbyte", "decimal", "object", "ushort", "string", 
            "double", "boolean",
            
            */
		
		
        // Modifiers
        "private", "protected", "public", "final", "volatile",  
        "static", "extern", "abstract",  
        "const", "default",

        "null", "false", "true", 

        "switch", "break", "for", "return", "if", "do", "else",  
        "try", "case", "goto", "continue", "while", 
        "throw", "throws", "finally", "catch", "foreach", "synchronized",                                    

        "class", "enum", "package", "import",  
        "new", "interface", "extends", "implements",
        "super", "this",  

        "typeof", "sizeof", "as", "instanceof"// 타입 정보
        
        
        
        /*"float", "int", "char", "short", "long", "void",  
        "byte", "double", "boolean"*/
        
        
	};
	
		
	static String[] AccessModifiersOfC = {		 
        "const"
	};
	
	static String[] AccessModifiersOfCSharp = {
		"private", "protected", "public", "final", "volatile", "sealed", "internal", 
        "static", "readonly", "unsafe", "extern", "abstract", "virtual", 
        "event", "const", "override", "default"
	};
	
	static String[] AccessModifiersOfJava = {
		/*"private", "protected", "public", "final", "volatile", "sealed", "internal", 
        "static", "readonly", "unsafe", "extern", "abstract", "virtual", 
        "event", "const", "override", "default"*/
        
        "private", "protected", "public", "final", "volatile",  
        "static", "extern", "abstract",  
        "const", "default", "synchronized"
	};
	
	static String[] TypesOfC = {
		"float", "int", "uint", "char", "short", "long", "void", "ulong", 
        "byte", "bool", "sbyte", "decimal", "object", "ushort", "string", 
        "double", "boolean"
	};
	
	static String[] TypesOfCSharp = {
		"float", "int", "uint", "char", "short", "long", "void", "ulong", 
        "byte", "bool", "sbyte", "decimal", "object", "ushort", "string", 
        "double", "boolean"
	};
	
	static String[] TypesOfJava = {
		/*"float", "int", "uint", "char", "short", "long", "void", "ulong", 
        "byte", "bool", "sbyte", "decimal", "object", "ushort", "string", 
        "double", "boolean"*/
		
		"float", "int", "char", "short", "long", "void",  
        "byte", "double", "boolean"/*, "Object", "String"*/
	};
	
	/** loadJavaLangPackage()에서 읽어들인다.*/
	static String[] TypesOfDefaultLibraryOfJava; /*= {
		"Object", "Class", "Exception",
		"String", "Boolean", "Integer", "Short", "Long", "Float", "Double"
	};*/
	
	/** ArrayListString[], 
	 * Character$Subset.class인 경우 item(ArrayListString)에는 Character, Subset순으로 들어간다.*/
	static ArrayListIReset TypesOfDefaultLibraryOfJava2 = new ArrayListIReset(20);
	
	/** 배열원소는 ArrayListString, 즉 ArrayListString[], 
	 * 파일이름만 들어가므로 전체 디렉토리이름은 
	 * Control.pathAndroid + File.separator + mlistOfImportedClassesStar[i] + 해당파일이름 이다.
	 * 예를들어 해당파일이름이 HashTable이고 mlistOfImportedClassesStar[i]가 com.gsoft.common.Util일 경우
	 * 전체 파일명은 /sdcard/gsoft/com/gsoft/common/Util/HashTable 이 된다.*/
	ArrayListIReset TypesOfImportStarOfJava = new ArrayListIReset(10);
	
	/** .class 파일을 load 할 경우 null 이 아님.*/
	public PathClassLoader mLoader;
	
	
    	

	
	void findAnnotation(HighArray_CodeString src) {
		int i, j;
		int indexAlpha;
		int indexID;
		int startIndexOfAnnotation=-1;
		int endIndexOfAnnotation=-1;
		for (i=0; i<src.count; i++) {
			CodeString str = src.getItem(i);
			if (CompilerHelper.IsBlank(str) || CompilerHelper.IsComment(str)) continue;
			else if (str.equals("@")) {
				indexAlpha = i;
				j = SkipBlank(src, false, i+1, src.count-1);
				if (j==src.count) return;
				
				str = src.getItem(j);
				if (IsIdentifier(str)) {
					startIndexOfAnnotation = i;
					indexID = j;
					src.getItem(indexAlpha).setType(CodeStringType.Annotation);
					str.setType(CodeStringType.Annotation);
					j = SkipBlank(src, false, j+1, src.count-1);
					if (j==src.count) return;
					
					str = src.getItem(j);					
					if (str.equals("(")) {
						int rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", j, src.count-1, false);
						if (rightPair!=-1)	{
							int k;
							for (k=j; k<=rightPair; k++) {
								src.getItem(k).setType(CodeStringType.Annotation);
							}
							i = rightPair;
							endIndexOfAnnotation = rightPair;
						}
						else {
							i = src.count;
							// error
						}
					}
					else {
						i = j-1;
						endIndexOfAnnotation = i;
					}
					mlistOfAnnotations.add(new Annotation(this, startIndexOfAnnotation, endIndexOfAnnotation));
				}
				else {
					i = j - 1;
				}
				
			}//else if (str.equals("@")) {
			
		}//for (i=0; i<src.count; i++) {
		
	}
	
	
	public void changeBounds() {
		if (menuClassList!=null) {
			View view = Control.view;
			int viewWidth = view.getWidth();
			int viewHeight = view.getHeight();
			
			//Size buttonSize = new Size((int)(viewWidth*0.7f),(int)(viewHeight*0.06f));
			
			// 영역만 잡아놓고 나중에 Button[]을 넣어준다.
			int width = (int) (viewWidth * 0.9f);
			int height = (int) (viewHeight * 0.8f);
			int x = viewWidth / 2 - width / 2;
			int y = viewHeight / 2 - height / 2;
			menuClassList.changeBounds(new Rectangle(x,y,width,height));
		}
		
		if (menuProblemList_EditText!=null) {
			View view = Control.view;
			int viewWidth = view.getWidth();
			int viewHeight = view.getHeight();
						
			// 영역만 잡아놓고 나중에 Button[]을 넣어준다.
			int width = (int) (viewWidth * 0.9f);
			int height = (int) (viewHeight * 0.6f);
			int x = viewWidth / 2 - width / 2;
			int y = viewHeight / 2 - height / 2;
			Rectangle boundsOfMenuProblemList = new Rectangle(x,y,width,height);
			menuProblemList_EditText.changeBounds(boundsOfMenuProblemList);
		}
		
		if (CommonGUI.textViewLogBird!=null) {
			int x, y, w, h;
			int viewWidth = Control.view.getWidth(); 
			int viewHeight = Control.view.getHeight();
			w = (int) (viewWidth * 0.8f);
			h = (int) (viewHeight * 0.8f);
			x = viewWidth/2 - w/2;
			y = viewHeight/2 - h/2;
			
			Rectangle boundsOfTextView = new Rectangle(x,y,w,h);
			CommonGUI.textViewLogBird.changeBounds(boundsOfTextView);
		}
		
		if (textViewExpressionTreeAndMessage!=null) {
			int x, y, w, h;
			int viewWidth = Control.view.getWidth(); 
			int viewHeight = Control.view.getHeight();
			w = (int) (viewWidth * 0.8f);
			h = (int) (viewHeight * 0.6f);
			x = viewWidth/2 - w/2;
			y = viewHeight/2 - h/2;
			
			Rectangle boundsOfTextView = new Rectangle(x,y,w,h);
			textViewExpressionTreeAndMessage.changeBounds(boundsOfTextView);
		}
		
		
		
		
	}
			
	
	public void createMenuClassList() {
		if (menuClassList!=null) return; 
		View view = Control.view;
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		
		
		
		// 영역만 잡아놓고 나중에 Button[]을 넣어준다.
		int width = (int) (viewWidth * 0.9f);
		int height = (int) (viewHeight * 0.8f);
		int x = viewWidth / 2 - width / 2;
		int y = viewHeight / 2 - height / 2;
		Rectangle boundsOfMenuClassAndMemberList = new Rectangle(x,y,width,height);
		
		menuClassList = new MenuClassList(this, boundsOfMenuClassAndMemberList);
	}
	
	
	
	public void createMenuProblemList_EditText() {
		if (menuProblemList_EditText!=null) return; 
		View view = Control.view;
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
					
		// 영역만 잡아놓고 나중에 Button[]을 넣어준다.
		int width = (int) (viewWidth * 0.9f);
		int height = (int) (viewHeight * 0.6f);
		int x = viewWidth / 2 - width / 2;
		int y = viewHeight / 2 - height / 2;
		Rectangle boundsOfMenuProblemList = new Rectangle(x,y,width,height);
		
		
		
		menuProblemList_EditText = new MenuProblemList_EditText(this, boundsOfMenuProblemList);
		menuProblemList_EditText.setBackColor(Color.GREEN);
	}
	
	public static void createTextViewLogBird() {
		if (CommonGUI.textViewLogBird!=null) return; 
		int x, y, w, h;
		int viewWidth = Control.view.getWidth(); 
		int viewHeight = Control.view.getHeight();
		w = (int) (viewWidth * 0.8f);
		h = (int) (viewHeight * 0.6f);
		x = viewWidth/2 - w/2;
		y = viewHeight/2 - h/2;
		
		Rectangle boundsOfTextView = new Rectangle(x,y,w,h);
		CommonGUI.textViewLogBird = new TextView(false, false, null, null, boundsOfTextView, 
				Control.view.getHeight()*0.03f, false, null, ScrollMode.Both, Compiler.backColor);
		CommonGUI.textViewLogBird.isReadOnly = true;
		CommonGUI.textViewLogBird.isSelecting = true;
		Compiler.textViewLogBird = CommonGUI.textViewLogBird;
	}
	
	public static void createTextViewExpressionTree() {
		if (textViewExpressionTreeAndMessage!=null) return; 
		int x, y, w, h;
		int viewWidth = Control.view.getWidth(); 
		int viewHeight = Control.view.getHeight();
		w = (int) (viewWidth * 0.8f);
		h = (int) (viewHeight * 0.65f);
		x = viewWidth/2 - w/2;
		y = viewHeight/2 - h/2;
		
		Rectangle boundsOfTextView = new Rectangle(x,y,w,h);
		textViewExpressionTreeAndMessage = new TextView(false, false, null, null, boundsOfTextView, 
				Control.view.getHeight()*0.028f, false, null, ScrollMode.VScroll, Compiler.backColor);
		textViewExpressionTreeAndMessage.isReadOnly = true;
	}
	
	public void setBackColor(int color) {
		/*int oldBackColor = backColor;
		int oldTextColor = textColor;
		int oldKeywordColor = keywordColor;
		int oldVarUseColor = varUseColor;
		int oldFuncUseColor = funcUseColor;
		int oldMemberDeclColor = memberDeclColor;
		int oldCommentColor = commentColor;
		int oldDocuCommentColor = docuCommentColor; */
		
		if (color==Color.WHITE) {
			backColor = Color.WHITE;
			textColor = Color.BLACK;
			keywordColor = Color.MAGENTA;
			varUseColor = Color.RED;
			funcUseColor = Color.CYAN;
			memberDeclColor = Color.BLUE;
			commentColor = Color.GREEN;
			docuCommentColor = ColorEx.darkerOrLighter(Color.GREEN, -100);
		}
		else if (color==Color.BLACK) {
			backColor = Color.BLACK;
			textColor = Color.WHITE;
			keywordColor = Color.MAGENTA;
			varUseColor = Color.YELLOW;
			funcUseColor = Color.CYAN;
			memberDeclColor = Color.BLUE;
			commentColor = Color.GREEN;
			docuCommentColor = ColorEx.darkerOrLighter(Color.GREEN, -100);
		}
		else if (color==Color.RED) {
			backColor = Color.RED;
			textColor = Color.CYAN; // CYAN
			keywordColor = Color.LTGRAY;
			varUseColor = Color.YELLOW;
			funcUseColor = Color.CYAN;
			memberDeclColor = Color.BLUE;
			commentColor = ColorEx.DKGRAY;
			docuCommentColor = Color.GREEN;
		}
		else if (color==Color.YELLOW) {
			backColor = Color.YELLOW;
			textColor = Color.BLUE;
			keywordColor = Color.MAGENTA;
			varUseColor = Color.BLACK;
			funcUseColor = Color.CYAN;
			memberDeclColor = Color.BLUE;
			commentColor = ColorEx.DKGRAY;
			docuCommentColor = ColorEx.GREEN;
		}
		else if (color==Color.BLUE) {
			backColor = Color.BLUE;
			textColor = Color.GREEN;
			keywordColor = Color.MAGENTA;
			varUseColor = Color.WHITE;
			funcUseColor = Color.CYAN;
			memberDeclColor = Color.BLACK;
			commentColor = Color.LTGRAY;
			docuCommentColor = Color.YELLOW;
		}
		else if (color==Color.GREEN) {
			backColor = Color.GREEN;
			textColor = Color.MAGENTA;	// MAGENTA
			keywordColor = Color.YELLOW;
			varUseColor = Color.WHITE;
			funcUseColor = Color.CYAN;
			memberDeclColor = Color.BLACK;
			commentColor = Color.LTGRAY;
			docuCommentColor = Color.RED;
		}
		else if (color==Color.MAGENTA) {
			backColor = Color.MAGENTA;
			textColor = Color.GREEN;	// GREEN
			keywordColor = Color.BLACK;
			varUseColor = Color.WHITE;
			funcUseColor = Color.CYAN;
			memberDeclColor = Color.RED;
			commentColor = Color.LTGRAY;
			docuCommentColor = Color.BLUE;
		}
		else if (color==Color.CYAN) {
			backColor = Color.CYAN;
			textColor = Color.MAGENTA;	// MAGENTA
			keywordColor = Color.GREEN;
			varUseColor = Color.WHITE;
			funcUseColor = Color.BLUE;
			memberDeclColor = Color.BLACK;
			commentColor = Color.LTGRAY;
			docuCommentColor = Color.RED;
		}
		else if (color==Color.LTGRAY) {
			backColor = Color.LTGRAY;
			textColor = Color.BLACK;
			keywordColor = Color.RED;
			varUseColor = Color.WHITE;
			funcUseColor = Color.MAGENTA;
			memberDeclColor = Color.CYAN;
			commentColor = Color.GREEN;
			docuCommentColor = Color.BLUE;
		}
		
		if (menuClassList!=null) {
			if (menuClassList.textView!=null) {
				menuClassList.textView.setBackColor(backColor);
			}
		}
		
		if (mBuffer==null) return;
		
		int i;
		//int codeStringColor;
		CodeString codeString;
		byte type;
		for (i=0; i<mBuffer.count; i++) {
			codeString = mBuffer.getItem(i);
			//codeStringColor = codeString.charAt(0).color;
			type = codeString.charAt(0).type;
			/*if (codeStringColor==oldBackColor) {
				codeString.setColor(backColor);
			}
			else */if (type==0 || type==CodeStringType.Text) {
				codeString.setColor(textColor);
			}
			else if (type==CodeStringType.Keyword) {
				codeString.setColor(keywordColor);
			}
			else if (type==CodeStringType.MemberVarUse) {
				codeString.setColor(varUseColor);
			}
			else if (type==CodeStringType.FuncUse) {
				codeString.setColor(funcUseColor);
			}
			else if (type==CodeStringType.MemberVarDecl) {
				codeString.setColor(memberDeclColor);
			}
			else if (type==CodeStringType.Comment) {
				codeString.setColor(commentColor);
			}
			else if (type==CodeStringType.DocuComment) {
				codeString.setColor(docuCommentColor);
			}
		}
		
		if (textViewExpressionTreeAndMessage!=null) {
			textViewExpressionTreeAndMessage.setBackColor(backColor);
		}
		
		
		
	}
	
   
    
    

    
	
	
	
	
	
	
	
	
	public MenuClassList menuClassList;
	
	// compiler마다 공유이므로
	//public static MenuProblemList menuProblemList;
	/** compiler마다 공유이므로 static이어야하나 현재는 tab문제와 src연결문제로 non-static,
	 * 다중문서를 열기 위해서는 Compiler클래스들의 리스트를 갖고 있으면 된다.*/
	public MenuProblemList_EditText menuProblemList_EditText;
	
	/** LogBird를 위한 textView*/
	static TextView textViewLogBird;
	
	/** 수식트리를 위한 textView*/
	public static TextView textViewExpressionTreeAndMessage;
	
	
	
	
	Language language;
	
	
	boolean mIsCompileAll = false;
	

	//public ArrayListCodeString mBuffer;
	public HighArray_CodeString mBuffer;
	
	CodeString strInput;
	public CodeString strOutput;
	
	
	public String filename;
	
	
	static ArrayListIReset errors = new ArrayListIReset(30);
	
	
	
		
	String[] Types;
	String[] Keywords;
	String[] AccessModifiers;
	
	String[] TypesOfDefaultLibrary;
	
	
	
	/** 가장 바깥(inner가 아닌) 클래스의 리스트*/
	ArrayListIReset mlistOfClass = new ArrayListIReset(10);
	
	/**FindClassParams[], 
	 * 현재 소스파일에서 정의된 클래스나 import된 클래스, 이 소스파일에서
	 * 읽어들인 클래스들이 등록된 클래스들의 집합, 
	 * 나중에 현재 소스파일이 아니라 전체 프로젝트 단위의 클래스집합으로 바꿀것이다.*/
	static ArrayList_FindClassParams mlistOfAllClasses = new ArrayList_FindClassParams(20);
	/** mlistOfAllClasses의 해시리스트*/
	static Hashtable_FullClassName mlistOfAllClassesHashed  = new Hashtable_FullClassName(50,20);
	
	/** CompilerHelper.loadClassFromSrc_onlyInterface()에서 이미 로드한 소스파일의 리스트*/
	static ArrayListString mlistOfLoadClassFromSrc_onlyInterface = new ArrayListString(10);
	
	/**CompilerHelper.loadClassFromSrc_onlyInterface()에서 이미 로드한 소스파일인지를 확인한다.*/
	static boolean AlreadyLoadedClassFromSrc_onlyInterface(String srcPath) {
		int i;
		for (i=0; i<mlistOfLoadClassFromSrc_onlyInterface.count; i++) {
			String path = mlistOfLoadClassFromSrc_onlyInterface.getItem(i);
			if (path.equals(srcPath)) {
				return true;
			}
		}
		return false;
	}
	
	/** CompilerHelper.loadClassFromSrc_onlyInterface()에서 이미 로드를 시도했지만 실패한 소스파일의 리스트*/
	static ArrayListString mlistOfLoadClassFromSrc_onlyInterface_failed = new ArrayListString(10);
	
	/**CompilerHelper.loadClassFromSrc_onlyInterface()에서 이미 로드한 소스파일인지를 확인한다.*/
	static boolean AlreadyLoadedClassFromSrc_onlyInterface_failed(String srcPath) {
		int i;
		for (i=0; i<mlistOfLoadClassFromSrc_onlyInterface_failed.count; i++) {
			String path = mlistOfLoadClassFromSrc_onlyInterface_failed.getItem(i);
			if (path.equals(srcPath)) {
				return true;
			}
		}
		return false;
	}
	
	/** 파일에서 정의하는 모든 클래스들*/
	ArrayListIReset mlistOfAllDefinedClasses = new ArrayListIReset(5);
	
	/** Short name을 사용할수 있는 클래스들, 
	 * import된 클래스들, 파일에서 정의하는 클래스들, findClassUsing에서 사용한다. 
	 * 참고로 같은 패키지내 클래스들은 findMemberUsesUsingNamespace_sub에서 로드를 하기 때문에 
	 * 여기에는 없다.*/
	//ArrayListIReset mlistOfAllClassesThatCanUseShortName = new ArrayListIReset(10);
	
	/**FindFunctionParams[]*/
	ArrayListIReset mlistOfAllFunctions = new ArrayListIReset(50);
	/**FindVarParams[]*/
	private HighArray<FindVarParams> mlistOfAllLocalVarDeclarations = new HighArray<FindVarParams>(100);
	/**FindVarParams[]*/
	private HighArray<FindVarParams> mlistOfAllMemberVarDeclarations = new HighArray<FindVarParams>(100);
	/**FindVarUseParams[]*/
	HighArray<FindVarUseParams> mlistOfAllVarUses = new HighArray<FindVarUseParams>(200);
	Hashtable2 mlistOfAllVarUsesHashed;
	/**FindVarUseParams[]*/
	//private ArrayListIReset mlistOfAllVarUsesForVar = new ArrayListIReset(600);
	/**FindVarUseParams[]*/
	//private ArrayListIReset mlistOfAllVarUsesForFunc = new ArrayListIReset(200);
	//private ArrayListIReset mlistOfAllFuncCalls = new ArrayListIReset(50);
	/**FindControlBlockParams[]*/
	ArrayListIReset mlistOfAllControlBlocks = new ArrayListIReset(200);
	/**TypeCast[], 파일안의 모든 타입캐스트 리스트*/
	ArrayListIReset mlistOfAllTypeCasts = new ArrayListIReset(20);
	/** 같은 패키지내 클래스 리스트*/
	String[] mSamePackageClasses;
	/** 같은 패키지내 파일 리스트, 그러므로 디렉토리명을 제외한 확장자 포함 파일이름만 가진다*/
	String[] mListOfFileOfSamePackage;
	
	/**FindArrayIntializerParams[], 파일안의 모든 배열초기화문 리스트*/
	ArrayListIReset mlistOfAllArrayIntializers = new ArrayListIReset(20);
	
	/**DocuComment[]*/
	//ArrayListIReset mlistOfDocuComment = new ArrayListIReset(50);
	
	/**String[]*/	
	private ArrayListString mlistOfImportedClasses = new ArrayListString(50);
	
	/**String[], import com.gsoft.common.Util.*;이와 같은 경우*/	
	private ArrayListString mlistOfImportedClassesStar = new ArrayListString(50);
	
	
	
	/**FindStatementParams[], 변수사용 다음에 '='이 오는 할당문, 
	 * 조건문, 반복문에 있는 할당문을 포함한 모든 할당문*/
	private ArrayListIReset mlistOfAllAssignStatements = new ArrayListIReset(200);
	
	/** FindAssignStatementParams[], 변수사용 다음에 '='이 오는 할당문, 
	 * 그러나 조건문, 반복문에 할당문이 있을수 있으므로 그것을 제외한다.*/
	ArrayListIReset mlistOfAssignStatements = new ArrayListIReset(100);
	
	/** FindAssignStatementParams[],
	 * 예를들어 한 문장이 f1();	f1(f2());	this.f1();	ref.f1();	f1().f2().f3() 이런 함수호출문들을 말한다.
	 * 독립적인 함수호출문*/
	//ArrayListIReset mlistOfIndependentFuncCall = new ArrayListIReset(100);
	
	/** FindSpecialStatementParams[]*/
	ArrayListIReset mlistOfSpecialStatement = new ArrayListIReset(60);
	
	/** 최상위 템플릿만 모아놓은 리스트, Template[]*/
	ArrayListIReset mlistOfAllTemplates = new ArrayListIReset(10);
	
	/** FindExpressionParams[]*/
	//ArrayListIReset mlistOfExpressions = new ArrayListIReset(100);
	
	/** FindVarUseParams타입, listOfAllVarUses에서 검색하여 함수호출의 VarUse만 모아놓은 리스트, 
	 * findAllFunctionCall에서 리스트를 만든다. 
	 * 수식에 들어가는 함수호출을 찾기 쉽게 하기 위함이다.*/
	//ArrayListIReset mlistOfVarUsesOfFunctionCalls = new ArrayListIReset(100);
	
	/** FindBlockParams[] : '{':startIndex(), '}':endIndex, 
	 * CheckParenthesis의 인덱스 정보를 저장한다.*/
	ArrayListIReset mlistOfBlocks = new ArrayListIReset(200);
	
	//ArrayListInt mlistOfIndexOfMiddlePair = new ArrayListInt(200); 
	
	/** FindSmallBlockParams[] : '(':startIndex(), ')':endIndex, 
	 * CheckParenthesis의 인덱스 정보를 저장한다.*/
	//ArrayListIReset mlistOfSmallBlocks = new ArrayListIReset(200);
	
	/** FindLargeBlockParams[] : '[':startIndex(), ']':endIndex, 
	 * CheckParenthesis의 인덱스 정보를 저장한다.*/
	//ArrayListIReset mlistOfLargeBlocks = new ArrayListIReset(150);
	
	/** fullname constructor의 타입의 리스트, Constructor[]*/
	ArrayListIReset mlistOfAllConstructor = new ArrayListIReset(20);
	
	
	ArrayListIReset mlistOfSynchronizedBlocks = new ArrayListIReset(5);
	
	
	/** 파일상의 Annotation들의 리스트, 문장의 리스트를 만들때 사용, Annotation[],
	 * mlistOfFindStatementParams을 참조*/
	ArrayListIReset mlistOfAnnotations = new ArrayListIReset(30);
	
	/** 파일상의 ImportStatement들의 리스트, 문장의 리스트를 만들때 사용, ImportStatement[],
	 * mlistOfFindStatementParams을 참조*/
	ArrayListIReset mlistOfImportStatements = new ArrayListIReset(50);
	
	/** 파일상의 PackageStatement들의 리스트, 문장의 리스트를 만들때 사용, PackageStatement[],
	 * mlistOfFindStatementParams을 참조*/
	ArrayListIReset mlistOfPackageStatements = new ArrayListIReset(2);
	
	
	/** 파일상의 FindStatementParams들의 리스트, 문장의 리스트를 만들때 사용, FindStatementParam[],
	 * package, import, class, 함수, 제어블록, 변수선언, 주석, 애노테이션 등도 
	 * 모두 문장들(FindStatementParams)이므로 
	 * 파일을 문장들의 리스트로 만들수있다.*/
	ArrayListIReset mlistOfFindStatementParams = new ArrayListIReset(1000);
	
	
	
	
	
	
	FindPackageParams[] mlistOfPackages;
	Package2[] mlistOfPackages2;
	
	private String packageName = "";
	
	
	
	//boolean mergesComment = true;
	/** Point[], Point는 주석의 시작과 끝을 말한다.*/
	//ArrayListIReset listOfComments = new ArrayListIReset(100); 
	
	/** checkParenthesisAll에서 괄호에러를 검출하면 hasPairError가 true로 설정되어
	 * 함수, 제어블록 등은 각자 자신이 괄호 쌍을 찾게 된다. 
	 * checkParenthesisAll에서 괄호에러를 검출하지 못하면 
	 * 클래스, 함수, 제어블록 등은 괄호 인덱스 캐시(listOfBlocks)에서 괄호 인덱스를 찾는다. 
	 */
	//private boolean hasPairError;
	//private boolean hasRightPairError;
	
	ByteCodeGenerator codeGen;
	
	/** 파일상의 주석, 다큐주석들의 리스트, 문장의 리스트를 만들때 사용, Comment[], 
	 * mlistOfFindStatementParams을 참조, StringTokenizer에서 만든다.*/
	ArrayListIReset mlistOfComments;
	
	
	
	
	
	
	/** CheckParenthesis의 인덱스정보를 저장한 FindBlockParams[]에서 block의 endIndex를 찾는다.*/
	FindBlockParams findBlock(ArrayListIReset listOfBlocks, int startIndexOfBlock) {
		int i;
		for (i=0; i<listOfBlocks.count; i++) {
			FindBlockParams block = (FindBlockParams) listOfBlocks.getItem(i);
			if (block.startIndex()==startIndexOfBlock) {
				return block;
			}
		}
		return null;
	}
	
	
	
	
	/** CheckParenthesis의 인덱스정보를 저장한 FindSmallBlockParams[]에서 
	 * isReverse가 false 이면 small block의 endIndex를 찾는다. 없으면 -1을 리턴,
	 * isReverse가 true  이면 small block의 startIndex()를 찾는다. 없으면 -1을 리턴,*/
	int findEndIndexOfSmallBlock(ArrayListIReset listOfSmallBlocks, int startIndexOfSmallBlock, boolean isReverse) {
		int i;
		if (isReverse==false) {
			for (i=0; i<listOfSmallBlocks.count; i++) {
				FindSmallBlockParams block = (FindSmallBlockParams) listOfSmallBlocks.getItem(i);
				if (block.startIndex()==startIndexOfSmallBlock) {
					return block.endIndex();
				}
			}
			return -1;
		}
		else {
			for (i=0; i<listOfSmallBlocks.count; i++) {
				FindSmallBlockParams block = (FindSmallBlockParams) listOfSmallBlocks.getItem(i);
				if (block.endIndex()==startIndexOfSmallBlock) {
					return block.startIndex();
				}
			}
			return -1;
		}
	}
	
	
	
	
	int Find(HighArray_CodeString src, String str, int startIndex, int endIndex)
    {
		try{
        int i;
        int resultIndex = -1;
        for (i = startIndex; i <= endIndex; i++)
        {
        	CodeString cstr = src.getItem(i);
        	if (CompilerHelper.IsComment(cstr)) continue;
            if (cstr.equals(str))
            {
                resultIndex = i;
                break;
            }
        }
        return resultIndex;
		}catch(Exception e) {
			int a;
			a=0;
			a++;
			return -1;
		}
    }
	
	
	
	
    
    public boolean IsAccessModifier(CodeString c) {
    	int i;
    	for (i=0; i<AccessModifiers.length; i++) {
    		if (c.equals(AccessModifiers[i])) return true;
    	}
    	return false;    	
    }
    public boolean IsAccessModifier(String c) {
    	int i;
    	for (i=0; i<AccessModifiers.length; i++) {
    		if (c.equals(AccessModifiers[i])) return true;
    	}
    	return false;    	
    }
    
    public boolean IsKeyword(CodeString c) {
    	int i;
    	for (i=0; i<Keywords.length; i++) {
    		if (c.equals(Keywords[i])) return true;
    	}
    	return false;    	
    }
    
    public boolean IsDefaultType(CodeString c) {
    	int i;
    	if (Types==null) return false;
    	for (i=0; i<Types.length; i++) {
    		if (c.equals(Types[i])) return true;
    	}
    	return false;
    }
    
    /** int, char, float등 기본타입*/
    public boolean IsDefaultType(String c) {
    	int i;
    	if (Types==null) return false;
    	for (i=0; i<Types.length; i++) {
    		if (c.equals(Types[i])) return true;
    	}
    	return false;
    }
    
    public boolean IsTypeOfImportStar(CodeString c) {
    	int i, j;
    	if (TypesOfImportStarOfJava==null) return false;
    	
    	for (i=0; i<TypesOfImportStarOfJava.count; i++) {
    		ArrayListString types = (ArrayListString) TypesOfImportStarOfJava.getItem(i);
    		for (j=0; j<types.count; j++) {
    			if (c.equals(types.list[i])) return true;
    		}
    	}
    	return false;
    }
    
    /** Integer, Char, Float등 java.lang패키지에 있는 클래스들
     * @param c : short이든 full이름이든 상관안함, 예를들어 Integer나 java.lang.Integer등
     * @return
     */
    public boolean IsTypeOfDefaultLibrary(CodeString c) {
    	/*int i;
    	if (TypesOfDefaultLibrary==null) return false;
    	for (i=0; i<TypesOfDefaultLibrary.length; i++) {
    		if (c.equals(TypesOfDefaultLibrary[i])) return true;
    	}
    	return false;*/
    	if (c.str.contains(".")==false) { // short name
	    	int i;
	    	if (TypesOfDefaultLibrary==null) return false;
	    	/*for (i=0; i<Types.length; i++) {
	    		if (c.equals(Types[i])) return true;    		
	    	}*/
	    	for (i=0; i<TypesOfDefaultLibrary.length; i++) {
	    		if (c.equals(TypesOfDefaultLibrary[i])) return true;
	    	}
	    	return false;
    	}
    	else {
    		int i;
	    	if (TypesOfDefaultLibrary==null) return false;
	    	/*for (i=0; i<Types.length; i++) {
	    		if (c.equals(Types[i])) return true;    		
	    	}*/
	    	for (i=0; i<TypesOfDefaultLibrary.length; i++) {
	    		if (c.equals("java.lang."+TypesOfDefaultLibrary[i])) return true;
	    	}
	    	return false;
    		
    	}
    }
    
    public boolean IsTypeOfDefinedClass(String c) {
    	if (c.contains(".")==false) {
	    	int i;
	    	if (mlistOfAllDefinedClasses==null) return false;
	    	for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
	    		FindClassParams classP = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
	    		String name = getShortName(classP.name);
	    		if (name.equals(c)) return true;
	    	}
	    	return false;
    	}
    	else {
    		int i;
    		if (mlistOfAllDefinedClasses==null) return false;
	    	for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
	    		FindClassParams classP = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
	    		String name = classP.name;
	    		if (name.equals(c)) return true;
	    	}
	    	return false;
    		
    	}
    }
    
    /** 같은 파일에 정의된 클래스의 이름이 확실하게 fullname으로 정해진 이후에 호출되어야 한다. 
     * FindAllClassesAndItsMembers2_sub()을 호출한 이후 클래스 이름을 fullname으로 정하고 클래스 캐시에 등록한 이후를 말한다.
     * (FindAllClassesAndItsMembers2()와 start_onlyInterface()함수를 참조한다.)
     * short이름일 경우 mlistOfAllDefinedClass에 중복된 클래스를 검색하면 null을 리턴한다. 
     * 예를들어 com.gsoft.common.EditText.UndoBuffer.Pair와 com.gsoft.common.EditText.RedoBuffer.Pair가 중복되고
     * c가 Pair이면 null을 리턴한다.*/
    public FindClassParams getTypeOfDefinedClass(String c) {
    	if (c.equals("Pair")) {
    		int a;
    		a=0;
    		a++;
    	}
    	if (c.contains(".")==false) {
	    	int i;
	    	if (mlistOfAllDefinedClasses==null) return null;
	    	ArrayListInt listOfIndices = new ArrayListInt(2);
	    	for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
	    		FindClassParams classP = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
	    		if (classP.name==null) continue;
	    		String name = getShortName(classP.name);
	    		if (name.equals(c)) {
	    			//if (classP.startIndex()<indexOfSrc && indexOfSrc<classP.endIndex()) {
	    				listOfIndices.add(i);
	    			//}
	    		}
	    	}
	    	if (listOfIndices.count==1) {
	    		return (FindClassParams) mlistOfAllDefinedClasses.getItem(listOfIndices.getItem(0));
	    	}
	    	else if (listOfIndices.count>1) { // 이름이 같은 클래스들이 여러개 있는 경우
	    		return null;
	    	}//else if (listOfIndices.count>1) { // 이름이 같은 클래스들이 여러개 있는 경우
	    	else {
	    		return null;
	    	}
	    	
    	}
    	else {
    		int i;
    		if (mlistOfAllDefinedClasses==null) return null;
	    	for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
	    		FindClassParams classP = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
	    		if (classP.name==null) continue;
	    		String name = classP.name;
	    		if (name.equals(c)) return classP;
	    	}
	    	return null;
    		
    	}
    }
    
    /** 같은 파일에 정의된 클래스의 이름이 확실하게 fullname으로 정해진 이후에 호출되어야 한다. 
     * FindAllClassesAndItsMembers2_sub()을 호출한 이후 클래스 이름을 fullname으로 정하고 클래스 캐시에 등록한 이후를 말한다.
     * (FindAllClassesAndItsMembers2()와 start_onlyInterface()함수를 참조한다.)<br>
     * short이름일 경우 mlistOfAllDefinedClass에 중복된 클래스를 검색하면 클래스 내부에서 사용되는 경우에는 가장 안쪽 클래스를 리턴하고,
     * 클래스 외부에서 사용되는 경우에는 클래스가 정의된 이후와  indexOfSrc의 거리를 따져서 가장 가까운 거리의 클래스를 리턴한다.
     * 예를들어 com.gsoft.common.EditText.UndoBuffer.Pair가 정의되고 Pair가 사용되면 UndoBuffer.Pair를
     * com.gsoft.common.EditText.RedoBuffer.Pair가 가 정의되고 Pair가 사용되면 RedoBuffer.Pair를 리턴한다.
     * 
     * @param indexOfSrc : c가 short name일 경우에만 의미가 있다. c의 소스상에서의 인덱스, 비슷하게 정해주면 된다.*/
    public FindClassParams getTypeOfDefinedClass(String c, int indexOfSrc) {
    	if (c.equals("Pair")) {
    		int a;
    		a=0;
    		a++;
    	}
    	if (indexOfSrc==4810) {
    		int a;
    		a=0;
    		a++;
    	}
    	if (c.contains(".")==false) {
	    	int i;
	    	if (mlistOfAllDefinedClasses==null) return null;
	    	ArrayListInt listOfIndices = new ArrayListInt(2);
	    	for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
	    		FindClassParams classP = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
	    		if (classP.name==null) continue;
	    		String name = getShortName(classP.name);
	    		if (name.equals(c)) {
	    			//if (classP.startIndex()<indexOfSrc && indexOfSrc<classP.endIndex()) {
	    				listOfIndices.add(i);
	    			//}
	    		}
	    	}
	    	if (listOfIndices.count==1) {
	    		return (FindClassParams) mlistOfAllDefinedClasses.getItem(listOfIndices.getItem(0));
	    	}
	    	else if (listOfIndices.count>1) { // 이름이 같은 클래스들이 여러개 있는 경우
	    		ArrayListInt listOfIndices2 = new ArrayListInt(1); //클래스 내부에서 사용되는 경우
	    		ArrayListInt listOfLength2 = new ArrayListInt(1);
	    		ArrayListInt listOfIndices3 = new ArrayListInt(1); //클래스가 정의된 이후에서 사용되는 경우
	    		ArrayListInt listOfLength3 = new ArrayListInt(1); // 클래스가 정의된 이후와 indexOfSrc이 떨어진 거리
	    		int index;
		    	for (i=0; i<listOfIndices.count; i++) {
		    		index = listOfIndices.getItem(i);
		    		FindClassParams classP = (FindClassParams) mlistOfAllDefinedClasses.getItem(index);
		    		if (classP.name==null) continue;
		    		if (classP.startIndex()<indexOfSrc && indexOfSrc<classP.endIndex()) {
		    			//클래스 내부에서 사용되는 경우
		    			listOfIndices2.add(index);
	    				listOfLength2.add(classP.endIndex()-classP.startIndex()+1);
		    		}
		    		else if (indexOfSrc>classP.endIndex()) {
		    			// 클래스가 정의된 이후에서 사용되는 경우
		    			listOfIndices3.add(index);
		    			listOfLength3.add(indexOfSrc-classP.endIndex()+1);
		    		}
		    	}
		    	if (listOfIndices2.count==1) {//이름이 같은 클래스들이 서로 포함하지 않는경우
		    		return (FindClassParams) mlistOfAllDefinedClasses.getItem(listOfIndices2.getItem(0));
		    	}
		    	else if (listOfIndices2.count>1) {//이름이 같은 클래스들이 서로 포함하는경우
		    		int len;
		    		int minLen = listOfLength2.getItem(0);
		    		int indexOfMinLen = 0;
		    		for (i=1; i<listOfLength2.count; i++) {
			    		len = listOfLength2.getItem(i);
			    		if (minLen>len) {
			    			minLen = len;
			    			indexOfMinLen = i;
			    		}
		    		}
		    		return (FindClassParams) mlistOfAllDefinedClasses.getItem(listOfIndices2.getItem(indexOfMinLen));
		    	}
		    	else {// 이름이 같은 클래스들이 여러개 있으나 그 클래스들내에서 사용되지 않는 경우	
		    		if (listOfLength3.count>0) {
			    		int len;
			    		int minLen = listOfLength3.getItem(0);
			    		int indexOfMinLen = 0;
			    		// 클래스가 정의된 이후의 인덱스와 indexOfSrc의 인덱스들의 저장된 listOfLength3에서 최소값을 찾는다.
			    		for (i=1; i<listOfLength3.count; i++) {
			    			len = listOfLength3.getItem(i);
				    		if (minLen>len) {
				    			minLen = len;
				    			indexOfMinLen = i;
				    		}
			    		}
			    		return (FindClassParams) mlistOfAllDefinedClasses.getItem(listOfIndices3.getItem(indexOfMinLen));
		    		}
		    		else {
		    			// Pair가 사용되고 그 이후에 UndoBuffer.Pair, RedoBuffer.Pair가 정의되면 null을 리턴한다.
		    			return null;
		    		}
		    	}
	    	}//else if (listOfIndices.count>1) { // 이름이 같은 클래스들이 여러개 있는 경우
	    	else {
	    		// shortname을 가진 클래스가 없는 경우
	    		return null;
	    	}
	    	
    	}
    	else {
    		int i;
    		if (mlistOfAllDefinedClasses==null) return null;
	    	for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
	    		FindClassParams classP = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
	    		if (classP.name==null) continue;
	    		String name = classP.name;
	    		if (name.equals(c)) return classP;
	    	}
	    	return null;
    		
    	}
    }
    
    
    /** java.lang 패키지안의 클래스를 검색한다.
     * @param c : */
    public boolean IsTypeOfDefaultLibrary(String c) {
    	if (c.contains(".")==false) { // short name
	    	int i;
	    	if (TypesOfDefaultLibrary==null) return false;
	    	for (i=0; i<TypesOfDefaultLibrary.length; i++) {
	    		if (c.equals(TypesOfDefaultLibrary[i])) return true;
	    	}
	    	return false;
    	}
    	else { // full name
    		int i;
	    	if (TypesOfDefaultLibrary==null) return false;
	    	for (i=0; i<TypesOfDefaultLibrary.length; i++) {
	    		if (c.equals("java.lang."+TypesOfDefaultLibrary[i])) return true;
	    	}
	    	return false;
    		
    	}
    }
    
    /** import com.gsoft.common.Util.*; Stack<Character.Subset> s;에서 Stack을 결정한다.*/
    public String getTypeOfImportStar(String shortName) {
    	int i, j;
    	if (TypesOfImportStarOfJava==null) return null;
    	
    	for (i=0; i<TypesOfImportStarOfJava.count; i++) {
    		ArrayListString types = (ArrayListString) TypesOfImportStarOfJava.getItem(i);
    		for (j=0; j<types.count; j++) {
    			if (shortName.equals(types.list[j])) {
    				String path = mlistOfImportedClassesStar.list[i]+"."+types.list[j];
    				return path;
    			}
    		}
    	}
    	return null;
    }
    
    /** shortName으로 같은 패키지내 클래스들에서 검색하여 
     * 일치하는 일반 namespace와 같은(a.b.c 등) full name을 리턴한다.*/
    public String getTypeOfPackageLibrary(String shortName) {
    	if (mListOfFileOfSamePackage==null) {
	    	File file = new File(Control.pathAndroid+File.separator+packageName.replace('.', File.separatorChar));
	    	mListOfFileOfSamePackage = file.list();
    	}
    	if (mListOfFileOfSamePackage==null) return null;
    	int i;
    	for (i=0; i<mListOfFileOfSamePackage.length; i++) {
    		String str = FileHelper.getFilenameExceptExt(mListOfFileOfSamePackage[i]);
    		if (str.equals(shortName)) 
    			return packageName + "." + shortName;
    	}
    	return null;
    	
    }
    
    /** name(shortName, fullName둘다 가능)으로 import문들에서 검색하여 일치하는 full name을 리턴한다.*/
    public String getTypeOfImportLibrary(String name) {
    	int i, j;
    	if (name.contains(".")==false) {
	    	for (i=0; i<mlistOfImportedClasses.count; i++) {
	    		String importedLibrary = mlistOfImportedClasses.getItem(i);
	    		String shortName = this.getShortName(importedLibrary);
	    		if (shortName.equals(name)) return importedLibrary;
	    	}
    	}
    	else {
    		for (i=0; i<mlistOfImportedClasses.count; i++) {
	    		String importedLibrary = mlistOfImportedClasses.getItem(i);
	    		if (importedLibrary.equals(name)) return importedLibrary;	    		
    		}
    	}
    	return null;
    	
    }
    
    static class ReturnOfIsArrayType {
    	boolean isTemplateCheck;
    	int r;
    	ReturnOfIsArrayType(boolean isTemplateCheck, int r) {
    		this.isTemplateCheck = isTemplateCheck;
        	this.r = r;
    	}
    }
    
    /** isReverse가 true이면 index부터 역으로 배열인지를 확인하고, false이면 index부터 순방향으로 배열인지를
     * 확인한다.
     * @return 배열이 맞으면 역방향일 경우 type시작의 인덱스, 순방향일 경우 ]의 인덱스, 배열이 아니면 -1 
     */
    public ReturnOfIsArrayType IsArrayType(HighArray_CodeString src, boolean isReverse, int index) {
    	if (index<0) return new ReturnOfIsArrayType(false, -1);
    	if (isReverse) {
    		int i = index;
    		
    		
    		if (src.getItem(i).equals("]")==false) return new ReturnOfIsArrayType(false, -1);
    		do {
    			int indexLeftPair = CompilerHelper.CheckParenthesis(src, "[", "]", 0, i, true);
    			if (indexLeftPair==-1) {
    				//errors.add(new Error(this, i, i, "invalid array declaration"));
    				return new ReturnOfIsArrayType(false, -1);
    			}
    			i = SkipBlank(src, true, 0, indexLeftPair-1);
    			if (i<=-1) return new ReturnOfIsArrayType(false, -1);
    			if (src.getItem(i).equals("]")==false) break;
    		}while(true);
    		
    		// 템플릿 배열일 경우 Template개체를 만들기 위해 현재 인덱스를 리턴해야 한다.
    		if (src.getItem(i).equals(">")) {
    			return new ReturnOfIsArrayType(true, i);
    		}
    		
    		i = this.SkipBlank(src, true, 0, i);
    		
    		CodeString str = src.getItem(i);
    		/*if (IsDefaultType(str)==true || IsUserDefinedType(str)==true ||
    		//		IsIdentifier(str)) return i;	// 타입이어야 하나 일단 id도 가능
    		if (IsDefaultType(str)==true || IsIdentifier(str)) return i;	// 타입이어야 하나 일단 id도 가능
    		if (IsTypeOfDefaultLibrary(str)==true) return i;*/
    		int fullnameIndex = getFullNameIndex0(src, true, i);
    		// 가장 왼쪽의 애노테이션이나 주석은 제외한다.
    		/*for (int k=fullnameIndex; k<i; k++) {
    			CodeString token = src.getItem(k);
    			if (CompilerHelper.IsAnnotation(token)) continue;
    			else if (CompilerHelper.IsBlank(token)) continue;
    			else {
    				fullnameIndex = k;
    				break;
    			}
    		}*/
    		return new ReturnOfIsArrayType(false, fullnameIndex);
    	}//if (isReverse) {
    	else {
    		index = this.SkipBlank(src, false, index, src.count-1);
    		
    		CodeString str = src.getItem(index);
    		// 타입이어야 하나 일단 id도 가능
    		/*if (IsDefaultType(str)==false && IsTypeOfDefaultLibrary(str)==false &&
    	    		IsIdentifier(str)==false) return -1;	// 타입이어야 하나 일단 id도 가능
    		*/
    		int fullnameIndex = getFullNameIndex0(src, false, index);
    		if (fullnameIndex==-1) return new ReturnOfIsArrayType(false, -1);
    		index = fullnameIndex;
    		
    		int i = SkipBlank(src, false, index+1, src.count-1);
    		if (i>=src.count) return new ReturnOfIsArrayType(false, -1);
    		
    		if (src.getItem(i).equals("[")==false) 
    			return new ReturnOfIsArrayType(false, -1);
    		
    		
    		
    		do {
    			int indexRightPair = CompilerHelper.CheckParenthesis(src, "[", "]", i, src.count-1, false);
    			if (indexRightPair==-1) {
    				//errors.add(new Error(this, i, i, "invalid array declaration"));
    				return new ReturnOfIsArrayType(false, -1);
    			}
    			i = SkipBlank(src, false, indexRightPair+1, src.count-1);
    			if (i>=src.count || src.getItem(i).equals("[")==false) 
    				return new ReturnOfIsArrayType(false, i);
    		}while(true);
    		
    		
    		
    	}
    	//return -1;
    	
    }
    
    
    public boolean IsUserDefinedType(CodeString c) {
    	int i;
    	// user-defined type
    	for (i=0; i<mlistOfImportedClasses.count; i++) {
    		String typeUserDefined = mlistOfImportedClasses.getItem(i);
    		if (c.toString().contains(".")==false) {
    			int indexOftypeUserDefined = typeUserDefined.indexOf(c.toString());
    			if (indexOftypeUserDefined==typeUserDefined.length()-c.toString().length()) {
    				return true;
    			}
    		}
    		else {
    			String type = c.toString();
    			if (type.equals(typeUserDefined)) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
   
    
    /** index부터 시작하여 역방향으로 검사하여 타입이름의 시작인덱스를 리턴한다. 중첩된 템플릿을 검사하기 위하여 재귀적 호출을 한다.
     * @return : 중첩된 템플릿일 경우 가장 바깥 템플릿*/
    public Template isTemplate(HighArray_CodeString src, int index) {
    	if (index<0) return null;
    	CodeString strIndex = src.getItem(index);
    	if (strIndex.equals(">")==false) return null;
    	int indexLeftPair = CompilerHelper.CheckParenthesis(src, "<", ">", 0, index, true);
    	if (indexLeftPair==-1) {
    		return null;
    	}
    	//중첩된 템플릿을 검사하기 위하여 재귀적 호출을 한다.
    	Template childTemplate = new Template(this);
    	int startIndexOfType1 = IsType(src, true, index-1, childTemplate);
    	if (startIndexOfType1==-1) return null;
    	startIndexOfType1 = SkipBlank(src, false, startIndexOfType1, index-1);
    	if (startIndexOfType1==index) return null;
    	
    	int startIndexOfType2 = SkipBlank(src, false, indexLeftPair+1, index-1);
    	if (startIndexOfType2==index) return null;
    	
    	if (startIndexOfType1!=startIndexOfType2) return null;
    	
    	int indexTypeName = indexLeftPair-1;
    	indexTypeName = this.SkipBlank(src, true, 0, indexTypeName);
    	indexTypeName = getFullNameIndex0(src, true, indexTypeName);
    	indexTypeName = SkipBlank(src, false, indexTypeName, indexLeftPair-1);
    	if (indexTypeName==indexLeftPair) return null;
    	
    	if (childTemplate.found) {
    		Template r = new Template(this, indexTypeName, indexLeftPair, index, startIndexOfType1);
    		r.child = childTemplate;
    		//childTemplate.parent = r;
    		return r;
    	}
    	else {
    		Template r = new Template(this, indexTypeName, indexLeftPair, index, startIndexOfType1);
    		//mlistOfAllTemplates.add(r);
    		return r;
    	}
    }
    
    /** type이 아니면 -1을 리턴, type이면 index를 리턴, 
     * array이면 역방향일 경우 배열의 시작인덱스를, 순방향일 경우 ]의 인덱스를 리턴
     * 포인터이면 역방향일 경우 타입의 시작인덱스를 리턴
     * @param template : result(out), 템플릿이 중첩될 경우 가장 바깥 템플릿, 즉 parent
     * @param index : isReverse가 true일경우 타입의 endIndex(), isReverse가 false일경우 타입의 startIndex()*/
    public int IsType(HighArray_CodeString src, boolean isReverse, int index, Template template) {
    	if (index<0) return -1;
    	
    	if (isReverse && language==Language.C && src.getItem(index).equals("*")) {
    		// 일단 타입을 확인하지 않는다.차후에
    		int i = index;
    		while (i>=0) {
	    		i = SkipBlank(src, isReverse, 0, i-1);
	    		if (i==-1) return -1;
	    		CodeString str = src.getItem(i);
	    		if (IsIdentifier(str)) return i;	    		
	    		else if (str.equals("*")) continue;
	    		else {
	    			return -1;
	    		}
    		}
    	}
    	
    	ReturnOfIsArrayType isArray = IsArrayType(src, isReverse, index); 
    	int indexArrayType = isArray.r;
    	if (isArray.isTemplateCheck==false) { // 일반배열이면 리턴
    		if (indexArrayType!=-1) return indexArrayType;
    	}
    	
    	
    	int indexTemplate = -1;
    	//indexTemplate = isTemplate(src, index);
    	Template template2 = null;
    	if (isArray.isTemplateCheck==false) { // 템플릿인지 확인한다.
    		template2 = isTemplate(src, index);
    	}
    	else { // 템플릿 배열인지 확인한다.
    		template2 = isTemplate(src, indexArrayType);
    	}
    	if (template2!=null) {
    		template.found = true;
    		template.copy(template2);
    		indexTemplate = template.indexTypeName();
    	}
    	if (indexTemplate!=-1) {
    		return indexTemplate;
    	}
    	
    	if (isReverse==false) {
    		index = this.SkipBlank(src, false, index, src.count-1);
    	}
    	else {
    		index = this.SkipBlank(src, true, 0, index);
    	}
    	    	
    	CodeString c = src.getItem(index);
    	//if (CompilerHelper.IsBlank(c) || CompilerHelper.IsComment(c)) return -1;
    	if (CompilerHelper.IsSeparator(c)) return -1;
    	if (IsKeyword(c)) return -1;
    	
    	
    	if (CompilerHelper.IsConstant(c)) return -1;
    			
    	// int, char 등의 내장 타입인 경우
    	if (IsDefaultType(c)) return index;
    	
    	// Object, Integer, Char 등
    	//if (IsTypeOfDefaultLibrary(c)) return index;
    	
    	
    	
    	// com.gsoft.common.Rectangle
    	int indexFullName = getFullNameIndex0(src, isReverse, index);
    	if (indexFullName!=-1) {
    		//indexFullName = SkipBlank(src, false, indexFullName, index);
    		return indexFullName;
    	}
    	
    	
    	if (IsUserDefinedType(c)) return index;
    	
    	// 일단 타입을 확인하지 않는다.차후에 
    	if (IsIdentifier(c)) return index;
    	
    	return -1;    	
    }
    
    
    
    public boolean IsIdentifier(CodeString c) {
    	if (c.equals("this")) return true;
    	if (c.equals("super")) return true;
    	if (CompilerHelper.IsSeparator(c)) return false;
    	if (IsKeyword(c)) return false;
    	//if (IsType(c)) return false;
    	int i;
    	for (i=0; i<Types.length; i++) {
    		if (c.equals(Types[i])) return false;
    	}
    	//if (IsTypeOfDefaultLibrary(c)) return false;
    	
    	
    	char ch = c.toString().charAt(0);
    	if (('0'<=ch && ch<='9') || (ch=='-' || ch=='+')) return false;
    	return true;
    }
    
    /**start2()와 start_onlyInterface()에서 호출된다.
	 * @param compiler : CompilerHelper의 loadClassFromSrc_onlyInterface()에서 
	 * compiler.start_onlyInterface(...) 이렇게 호출이 되고  start_onlyInterface()에서
	 * loadImportStar(...)호출을 하면
	 * loadImportStar()내부의 this나 this가 없는 
	 * Compiler클래스의 멤버 사용은 compiler(호출오브젝트)의 멤버사용이므로 없어도 되는 것이다.
	 * start2()에서 호출이 되어도 마찬가지이다.
	 * @param src : compiler.mBuffer와 같다.*/
    public void loadImportStar() {
    	//if (TypesOfImportStarOfJava!=null) return;
    	
    	//String pathAndroid = Control.pathAndroid.replace('.', File.separatorChar);
    	int i, j;
    	for (i=0; i<mlistOfImportedClassesStar.count; i++) {
    		TypesOfImportStarOfJava.add( new ArrayListString(20) );
    		
    		ArrayListString listNew = (ArrayListString) TypesOfImportStarOfJava.list[i]; 
    		
    		String dir = mlistOfImportedClassesStar.getItem(i);
    		dir = dir.replace('.', File.separatorChar);
    		
    		String classPath; 
			classPath = Control.pathAndroid + File.separator + dir + ".class";			
			
			String path = CompilerHelper.fixClassPath(classPath);
			
			if (path!=null) { // com/gsoft/common/Util.class 는 존재하는 파일이다.			
				String directory = CompilerHelper.getDirectory(Control.pathAndroid + File.separator + dir);
				File file = new File(directory);
				String[] list = file.list();
				String filename = FileHelper.getFilename(classPath);
				String filenameExceptExt = FileHelper.getFilenameExceptExt(filename);
				
				for (j=0; j<list.length; j++) {
					int index = list[j].indexOf(filenameExceptExt);
					if (index!=-1) { // 내부클래스를 찾아야 한다.
						String str = list[j].substring(index+filenameExceptExt.length()+1, list[j].length());
						str = FileHelper.getFilenameExceptExt(str);
						listNew.add(str);
					}
				}
				
				continue;				
			}
    		
    		// dir = com/gsoft/common 이 된다.
    		File starFile = new File(Control.pathAndroid+File.separator+dir);
    		String[] list = starFile.list();
    		//ArrayListString listNew = (ArrayListString) TypesOfImportStarOfJava.list[i];
    		if (list==null) return;
    		
    		for (j=0; j<list.length; j++) {
    			int indexDollar = list[j].indexOf("$");
    			// '$'를 포함하지않으면, 즉 내부클래스는 제외
    			if (indexDollar==-1) {
    				int indexDot = list[j].indexOf(".");
    				// 확장자는 제외한다. 따라서 HashTable.class일 경우 HashTable만 listNew에 들어간다.
    				if (indexDot!=-1) {
    					String str = list[j].substring(0, indexDot);
    					listNew.add(str);
    				}
    			}
    		} // for j
    	} // for i
    	
    	
    }
   
    
    public static void loadJavaLangPackage() {
    	if (TypesOfDefaultLibraryOfJava!=null) return;
    	
    	File javaLangFile = new File(Control.pathAndroid+File.separator+"java"+File.separator+"lang");
		String[] list = javaLangFile.list();
		ArrayListString listNew = new ArrayListString(50); 
		int i;
		if (list==null) {
			return;
		}
		for (i=0; i<list.length; i++) {
			int indexDollar = list[i].indexOf("$");
			if (indexDollar==-1) {
				int indexDot = list[i].indexOf(".");
				if (indexDot!=-1) {
					String str = list[i].substring(0, indexDot);
					listNew.add(str);
				}
			}
			else { // $가 있는경우, 즉 내부클래스인 경우
				// Character$Subset.class인 경우 item에는 Character, Subset순으로 들어간다.
				/*ArrayListString item = new ArrayListString(2);
				String filename = FileHelper.getFilename(list[i]);
				String token;
				do {
					int indexDollar2 = filename.indexOf("$");
					if (indexDollar2==-1) {
						item.add(filename);
						break;
					}
					else {
						token = filename.substring(0, indexDollar2);
						item.add(token);
						filename = filename.substring(indexDollar2+1, filename.length());
					}
				}while(true);*/
				
				
				int indexDot = list[i].indexOf(".");
				String name = list[i].replace('$', '.');
				if (indexDot!=-1) {
					String str = name.substring(0, indexDot);
					listNew.add(str);
				}
				
				//TypesOfDefaultLibraryOfJava2.add(item);
			}
		}
		TypesOfDefaultLibraryOfJava = listNew.getItems();
    }
   
    
    public void setLanguage(Language lang) {
    	language = lang;
    	if (lang==Language.Java) {
    		loadJavaLangPackage();
    		Types = TypesOfJava;
    		TypesOfDefaultLibrary = TypesOfDefaultLibraryOfJava;  
    		Keywords = KeywordsOfJava;
    		AccessModifiers = AccessModifiersOfJava; 
    	}
    	else if (lang==Language.CSharp) {
    		Types = TypesOfCSharp;
    		Keywords = KeywordsOfCSharp;
    		AccessModifiers = AccessModifiersOfCSharp; 
    	}
    	else if (lang==Language.C) {
    		Types = TypesOfC;
    		Keywords = KeywordsOfC;
    		AccessModifiers = AccessModifiersOfC; 
    	}
    	else if (lang==Language.Html) {
    		Types = null;
    		Keywords = KeywordsOfHtml;
    		AccessModifiers = null;
    	}
    }
    
    /** blank와 애노테이션과 일반주석만을 skip, 
     * reverse가 false이면 startIndex()부터 endIndex()까지 인덱스를 증가시키면서 검색, 못 찾으면 endIndex()+1
	 *  reverse가 true이면 endIndex()부터 startIndex()까지 인덱스를 감소시키면서 검색, 못 찾으면 startIndex()-1*/
	int SkipOnlyBlankAndAnnotationAndRegularComment(HighArray_CodeString src, boolean reverse, int startIndex, int endIndex)
    {
        int i;
        int resultIndex = -1;
        if (!reverse)
        {
            for (i = startIndex; i <= endIndex; i++)
            {
            	CodeString cstr = src.getItem(i);
                if (CompilerHelper.IsBlank(cstr)) continue;
                else if (CompilerHelper.IsAnnotation(cstr)) continue;
                else if (CompilerHelper.IsRegularComment(cstr)) continue;
                else break;
            }
            /*if (i <= endIndex+1) resultIndex = i;
            else resultIndex = endIndex();*/
            resultIndex = i;
        }
        else
        {
            for (i = endIndex; i >= startIndex; i--)
            {
            	CodeString cstr = src.getItem(i);
                if (CompilerHelper.IsBlank(cstr)) continue;
                else if (CompilerHelper.IsAnnotation(cstr)) continue;
                else if (CompilerHelper.IsRegularComment(cstr)) continue;
                else break;
            }
            /*if (i >= startIndex-1) resultIndex = i;
            else resultIndex = startIndex();*/
            resultIndex = i;
        }
        return resultIndex;
    }
    
    /** blank만을 skip, 즉 \n, \b, \r등만을 스킵하고 주석은 스킵하지 않는다.
     * reverse가 false이면 startIndex()부터 endIndex()까지 인덱스를 증가시키면서 검색, 못 찾으면 endIndex()+1
	 *  reverse가 true이면 endIndex()부터 startIndex()까지 인덱스를 감소시키면서 검색, 못 찾으면 startIndex()-1*/
	int SkipOnlyBlank(HighArray_CodeString src, boolean reverse, int startIndex, int endIndex)
    {
        int i;
        int resultIndex = -1;
        if (!reverse)
        {
            for (i = startIndex; i <= endIndex; i++)
            {
            	CodeString cstr = src.getItem(i);
                if (CompilerHelper.IsBlank(cstr)) continue;
                else break;
            }
            /*if (i <= endIndex+1) resultIndex = i;
            else resultIndex = endIndex();*/
            resultIndex = i;
        }
        else
        {
            for (i = endIndex; i >= startIndex; i--)
            {
            	CodeString cstr = src.getItem(i);
                if (CompilerHelper.IsBlank(cstr)) continue;
                else break;
            }
            /*if (i >= startIndex-1) resultIndex = i;
            else resultIndex = startIndex();*/
            resultIndex = i;
        }
        return resultIndex;
    }

	
	
	
	/** Expression를 skip.
     *  reverse가 false이면 startIndex()부터 endIndex()까지 인덱스를 증가시키면서 검색, 못 찾으면 endIndex()+1
	 *  reverse가 true이면 endIndex()부터 startIndex()까지 인덱스를 감소시키면서 검색, 못 찾으면 startIndex()-1*/
	int SkipExpression(HighArray_CodeString src, boolean reverse, int startIndex, int endIndex)
    {
        int i;
        int resultIndex = -1;
        if (!reverse)
        {
            for (i = startIndex; i <= endIndex; i++)
            {
            	CodeString cstr = src.getItem(i);
            	if (CompilerHelper.IsComment(cstr)) continue;
                if (CompilerHelper.IsBlank(cstr)) continue;
                if (IsIdentifier(cstr)) continue;
                if (CompilerHelper.IsConstant(cstr)) continue;
                if (CompilerHelper.IsOperator(cstr)) continue;
                if (cstr.equals("(")) {
                	int rightPair;
                	rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", i, endIndex, false);
                	if (rightPair==-1) i = endIndex;
                	else i = rightPair;
                	continue;
                }
                break;
            }
            resultIndex = i;
        }
        else
        {
            for (i = endIndex; i >= startIndex; i--)
            {
            	CodeString cstr = src.getItem(i);
            	if (CompilerHelper.IsComment(cstr)) continue;
                if (CompilerHelper.IsBlank(cstr)) continue;
                if (IsIdentifier(cstr)) continue;
                if (CompilerHelper.IsConstant(cstr)) continue;
                if (CompilerHelper.IsOperator(cstr)) continue;
                if (cstr.equals(")")) {
                	int leftPair;
                	leftPair = CompilerHelper.CheckParenthesis(src, "(", ")", startIndex, i, true);
                	if (leftPair==-1) i = startIndex;
                	else i = leftPair;
                	continue;
                }
                break;
            }
            resultIndex = i;
        }
        return resultIndex;
    }
	
    /** blank나 comment를 skip.
     *  reverse가 false이면 startIndex()부터 endIndex()까지 인덱스를 증가시키면서 검색, 
     *  blank나 comment가 아닌 인덱스를 리턴, 못 찾으면 endIndex()+1
	 *  reverse가 true이면 endIndex()부터 startIndex()까지 인덱스를 감소시키면서 검색, 
	 *  blank나 comment가 아닌 인덱스를 리턴, 못 찾으면 startIndex()-1*/
	int SkipBlank(HighArray_CodeString src, boolean reverse, int startIndex, int endIndex)
    {
        int i;
        int resultIndex = -1;
        if (!reverse)
        {
            for (i = startIndex; i <= endIndex; i++)
            {
            	CodeString cstr = src.getItem(i);
            	if (CompilerHelper.IsComment(cstr)) continue;
                if (CompilerHelper.IsBlank(cstr)) continue;
                break;
            }
            resultIndex = i;
        }
        else
        {
            for (i = endIndex; i >= startIndex; i--)
            {
            	try{
            	CodeString cstr = src.getItem(i);
            	if (CompilerHelper.IsComment(cstr)) continue;
                if (CompilerHelper.IsBlank(cstr)) continue;
                break;
            	}catch(Exception e) {
            		int a;
            		a=0;
            		a++;
            	}
            }
            resultIndex = i;
        }
        return resultIndex;
    }
	
	boolean Contains(CodeString target, ArrayListCodeString listOfStopStrings) {
		int i;
		for (i=0; i<listOfStopStrings.count; i++) {
			if (target.equals(listOfStopStrings.getItem(i))) {
				return true;
			}
		}		 
		return false;
	}
	
	/** reverse가 false이면 startIndex()부터 endIndex()까지 인덱스를 증가시키면서 검색, 못 찾으면 endIndex()+1
	 *  reverse가 true이면 endIndex()부터 startIndex()까지 인덱스를 감소시키면서 검색, 못 찾으면 startIndex()-1
	 *  @param stringStopped : 발견해서 멈추게 한 문자열*/
	int Skip(HighArray_CodeString src, boolean reverse, ArrayListCodeString listOfStopStrings, 
			int startIndex, int endIndex, CodeString stringStopped)
    {
        int i;
        int resultIndex = -1;
        if (!reverse)
        {
            for (i = startIndex; i <= endIndex; i++)
            {
            	CodeString cstr = src.getItem(i);
            	if (CompilerHelper.IsComment(cstr)) continue;
                if (Contains(cstr, listOfStopStrings))  {
                	stringStopped.str = cstr.str; 
                	break;
                }
            }
            resultIndex = i;
        }
        else
        {
            for (i = endIndex; i >= startIndex; i--)
            {
            	CodeString cstr = src.getItem(i);
            	if (CompilerHelper.IsComment(cstr)) continue;
                if (Contains(cstr, listOfStopStrings)) {
                	stringStopped.str = cstr.str;
                	break;
                }
            }
            resultIndex = i;
        }
        return resultIndex;
    }
	
	/** reverse가 false이면 startIndex()부터 endIndex()까지 인덱스를 증가시키면서 검색, 못 찾으면 endIndex()+1
	 *  reverse가 true이면 endIndex()부터 startIndex()까지 인덱스를 감소시키면서 검색, 못 찾으면 startIndex()-1
	 *  주석이나 애노테이션은 skip한다.*/
	public int Skip(HighArray_CodeString src, boolean reverse, String stopChar, int startIndex, int endIndex)
    {
        int i;
        int resultIndex = -1;
        if (!reverse)
        {
            for (i = startIndex; i <= endIndex; i++)
            {
            	CodeString cstr = src.getItem(i);
            	if (CompilerHelper.IsComment(cstr) || CompilerHelper.IsAnnotation(cstr)) continue;
                if (cstr.equals(stopChar)) break;
            }
            resultIndex = i;
        }
        else
        {
            for (i = endIndex; i >= startIndex; i--)
            {
            	CodeString cstr = src.getItem(i);
            	if (CompilerHelper.IsComment(cstr) || CompilerHelper.IsAnnotation(cstr)) continue;
                if (cstr.equals(stopChar)) break;
            }
            resultIndex = i;
        }
        return resultIndex;
    }
	
	
	
	/** 클래스 하나에 있는 내부클래스들을 대상으로 하며, 클래스들이 트리구조이므로 recursive call한다.*/
	public void RegisterClasses(HighArray_CodeString src, FindClassParams findClassParams, 
			String classPath) {
		int i;
		if (findClassParams==null) return;
		
		classPath += src.getItem(findClassParams.classNameIndex()).toString();
		mlistOfImportedClasses.add(new String(classPath));
		
		if (findClassParams.childClasses!=null) {
			classPath += ".";
			for (i=0; i<findClassParams.childClasses.count; i++) {
				FindClassParams classParams = (FindClassParams) findClassParams.childClasses.getItem(i);
				RegisterClasses(src, classParams, classPath);
			}
		}		
	}
	
	/** full name 하나를 검색한다. 에러도 넣어준다.
	 * extends일 경우는 implements나 {의 인덱스를 리턴,
	 * implements 일 경우는 ,나 {의 인덱스를 리턴 
	 * @param extendsOrImplementsOrThrows = "extends", "implements", "throws"
	 * @param classParams = "extends", "implements"일 경우 null이 아님 
	 * @param functionParams = "throws"일 경우 null이 아님
	 * @param startIndex()
	 */
	int getFullNames(HighArray_CodeString src, String extendsOrImplementsOrThrows,
			FindClassParams classParams, FindFunctionParams functionParams, 
			int startIndex) {
		
		int index = SkipBlank(src, false, startIndex, src.count-1);
        if (index==src.count) {
        	return -1;
        }
		if (extendsOrImplementsOrThrows.equals("extends")) {
			ArrayListCodeString fullName = getFullName(src, index);
			String strFullName = convertFullNameToString(fullName);
	        classParams.classNameToExtend = strFullName;
	        
	        index += fullName.count;
	        index = SkipBlank(src, false, index, src.count-1);
	        if (index==src.count) {
	        	return index;
	        }
	        
	        CodeString cstr = src.getItem(index);
	        if (cstr.equals("implements")) {
	        	return index;
	        }
	        else if (cstr.equals("{")){
	        	return index;	        	
	        }
	        else {
	        	errors.add(new Error(this, index, index, "invalid extends"));
	        	return -1;
	        }
		}
		else if (extendsOrImplementsOrThrows.equals("implements")) {
			ArrayListCodeString fullName = getFullName(src, index);
			String strFullName = convertFullNameToString(fullName);
			if (classParams.interfaceNamesToImplement==null) {
				classParams.interfaceNamesToImplement = new ArrayListString(2);
				classParams.interfaceNamesToImplement.add(strFullName);
			}
			else {
				classParams.interfaceNamesToImplement.add(strFullName);
			}
	        
	        index += fullName.count;
	        index = SkipBlank(src, false, index, src.count-1);
	        if (index==src.count) {
	        	return index;
	        }
	        
	        CodeString cstr = src.getItem(index);
	        if (cstr.equals(",")) {
	        	return index;
	        }
	        else if (cstr.equals("{")){
	        	return index;	        	
	        }
	        else {
	        	errors.add(new Error(this, index, index, "invalid implements"));
	        	return -1;
	        }
		}
		else if (extendsOrImplementsOrThrows.equals("throws")) {
			ArrayListCodeString fullName = getFullName(src, index);
			String strFullName = convertFullNameToString(fullName);
			if (functionParams.exceptionNamesToThrow==null) {
				functionParams.exceptionNamesToThrow = new ArrayListString(2);
				functionParams.exceptionNamesToThrow.add(strFullName);
			}
			else {
				functionParams.exceptionNamesToThrow.add(strFullName);
			}
	        
	        index += fullName.count;
	        index = SkipBlank(src, false, index, src.count-1);
	        if (index==src.count) {
	        	return index;
	        }
	        
	        CodeString cstr = src.getItem(index);
	        if (cstr.equals(",")) {
	        	return index;
	        }
	        else if (cstr.equals("{")){
	        	return index;	        	
	        }
	        else {
	        	errors.add(new Error(this, index, index, "invalid throws in function"));
	        	return -1;
	        }
		}
		return -1;
	}
	
	String convertFullNameToString(ArrayListCodeString fullName) {
		int i;
		CodeString r = new CodeString("", textColor);
		for (i=0; i<fullName.count; i++) {
			CodeString cs = fullName.getItem(i);
			r = r.concate(cs);
		}
		return r.str;
	}
	
	
	
	
	/** listOfClasses에서 name을 갖는 클래스를 검색한다.
	 * name은 fullname이든 short name이든 상관은 없으나 
	 * short name일 경우 틀린 classParams가 검색될 위험성이 있다. 
	 * 현재 short name으로 이 함수를 호출하는 경우는 
	 * 같은 파일 내에서 정의된 클래스에 대해서만 한다. */
	public FindClassParams getFindClassParams(Hashtable_FullClassName listOfClassesHashed, 
			ArrayList_FindClassParams listOfClasses, String name) {
		if (name==null) return null;
		if (name.contains(".")) {  // 풀네임이면
			return listOfClassesHashed.getData(name);
		}
		else {  // short name
			/*int i;
			for (i=0; i<listOfClasses.count; i++) {
				FindClassParams classParam = (FindClassParams)listOfClasses.getItem(i);
				String className = classParam.name;
				int j;
				for (j=className.length()-1; j>=0; j--) {
					if (className.charAt(j)=='.') break;
				}
				className = className.substring(j+1, className.length());
				if (className.equals(name))
					return classParam;
				
			}*/
			return null;
		}
		//return null;
	}
	
	
	
	
		
	
	/** Control.pathAndroid의 디렉토리구조를 FindPackageParams으로 만들어 mlistOfPackages에 등록한다.*/
	void loadLibraries2(Compiler compiler) {
		File parentFile = new File(Control.pathAndroid);
		String[] list = parentFile.list();
		if (list==null) return;
		int i;
		ArrayListIReset listPackages = new ArrayListIReset(10); 
		for (i=0; i<list.length; i++) {
			File packageFile = new File(Control.pathAndroid + File.separator + list[i]);
			if (packageFile.isDirectory()) {
				String packageName = FileHelper.getFilename(list[i]);
				String parentFullName = "";
				String[] listChildren = packageFile.list(); 
				FindPackageParams p = new FindPackageParams(packageName, parentFullName, listChildren);
				listPackages.add(p);
			}
		}
		mlistOfPackages = new FindPackageParams[listPackages.count];
		for (i=0; i<mlistOfPackages.length; i++) {
			mlistOfPackages[i] = (FindPackageParams) listPackages.getItem(i);
		}
	}
	
	/** Control.pathAndroid에 지정된 라이브러리를 통째로 읽어 트리를 만든다.*/
	/*void loadLibraries(Compiler compiler) {
		try{
		Package2.countLoaded = 0;
		String pathAndroid = Control.pathAndroid;
		Package2 init = new Package2(compiler, pathAndroid);
		int i;
		mlistOfPackages2 = new Package2[init.listOfChildPackages.count];
		for (i=0; i<init.listOfChildPackages.count; i++) {
			mlistOfPackages2[i] = (Package2) init.listOfChildPackages.getItem(i);
		}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			//showMessage(true, (new StackTracer(e)).toString());
		}
	}*/
	
	
	
	
	
	public String getFullNameType3(HighArray_CodeString src, String name, ArrayListIReset listOfAllClasses) {
		if (name==null) {
			return null;
		}
		if (listOfAllClasses==null) return null;
		
		if (IsDefaultType(name)) {
			return name;
		}
		if (name.contains(".")==false) {
			int i;
			for (i=0; i<listOfAllClasses.count; i++) {
				FindClassParams c = (FindClassParams) listOfAllClasses.getItem(i);
				if (this.getShortName(c.name).equals(name)) return c.name;
			}
			return null;
		}
		else {
			int i;
			for (i=0; i<listOfAllClasses.count; i++) {
				FindClassParams c = (FindClassParams) listOfAllClasses.getItem(i);
				if (c.name.equals(name)) return c.name;
			}
			return null;
		}
	}
	
	
	/** name으로 클래스 찾기, getFullNameType()과 다른 점은 getFullNameType()은 타입에 배열이나 템틀릿기호가 붙을수 있는데,  
	 * getFullNameType2는 배열이나 템플릿기호가 없는 순수한 클래스이름이다.
	 * full name이 아니고 import라이브러리타입, 라이브러리 타입(Object, Class, Integer등)도 아니면 
	 * 패키지 이름을 붙인 full name을 리턴한다.
	 * full name이 아니고 내장타입(int, char등), 라이브러리 타입이면 그대로 리턴한다.
	 * @param name : short이름 full이름 구분 안 함
	 * @param indexOfSrc : name의 소스에서의 index, 비슷하게 정해주면 된다.
	 * @return : 1. int, char등 기본타입, 2. import클래스의 full이름, 
	 * 3. 동일 패키지내 클래스의 풀이름, 4. 자신이 정의하는 클래스*/
	public String getFullNameType2(HighArray_CodeString src, String name, int indexOfSrc/*, ArrayListIReset listOfAllClasses*/) {
		if (name==null) {
			return null;
		}
		
		if (IsDefaultType(name)) {
			return name;
		}
		if (name.contains(".")==false) {
			
			String typeNameFull = null;
			String varUseName = name;
			if (typeNameFull==null) {
				typeNameFull = this.getTypeOfImportLibrary(varUseName);
			}
			
			// import com.gsoft.common.Util.*; Stack<Character.Subset> s;에서 Stack을 결정한다.
			if (typeNameFull==null) {
				typeNameFull = getTypeOfImportStar(varUseName);
			}
			
			if (typeNameFull==null) {
				FindClassParams c = getTypeOfDefinedClass(varUseName, indexOfSrc);
				if (c!=null) {
					typeNameFull = c.name;
				}
			}
			
			// com.gsoft.common.gui패키지에서 LangDialog를 import하지않고 
			// LangDialog d = new LangDialog()에서 LangDialog를 결정한다.
			if (typeNameFull==null) {
				typeNameFull = getTypeOfPackageLibrary(varUseName);
			}
			
			// java.lang패키지를 import하지 않고 Thread.sleep(1000); 에서 Thread를 결정한다.
			if (typeNameFull==null) {
				if (IsTypeOfDefaultLibrary(varUseName)) {
					typeNameFull = "java.lang." + varUseName;
				}
				
			}
			return typeNameFull;
		}
		return name;
		
	}
	
	
	/** 주의사항 : 자신이 정의하는 클래스만 가능하다. 다시말해 외부클래스에는 적용할수없다.
	 * 해당 클래스의 full name을 얻을때 호출한다. 리턴값에 "."이 뒤에 붙는것을 주의한다.
	 * 완벽한 full name을 얻기 위해 리턴값에서 packageName + "." 을 앞에 붙인다. */
	public String getFullNameExceptPackageName(HighArray_CodeString src, FindClassParams findClassParams) {
		if (findClassParams==null) return null;
		if (findClassParams.classNameIndex()==-1) return null;
		String className = "";
		FindClassParams classParams = findClassParams;
		while (classParams!=null) {
			if (classParams.classNameIndex()==-1) { // C의 경우는 -1이다.
				break;
			}
			className = src.getItem(classParams.classNameIndex()).toString() + "." + className;
			classParams = (FindClassParams) classParams.parent;
		}
		return className;
	}
	
	/** 템플릿이 중첩될 경우 가장 알맞은 템플릿을 리턴한다.*/
	Template getMostSuitableTemplate(ArrayListIReset listOfTemplates, int startIndex, int endIndex) {
		int i;
		for (i=0; i<listOfTemplates.count; i++) {
			Template t = (Template) listOfTemplates.getItem(i);
			Template r = getMostSuitableTemplate_sub(t, startIndex, endIndex);
			if (r!=null) return r;
		}
		return null;
	}
	
	/** 템플릿이 중첩될 경우 가장 알맞은 템플릿을 리턴한다.*/
	Template getMostSuitableTemplate_sub(Template template, int startIndex, int endIndex) {
		if (template==null) return null;
		
		// 템플릿이 중첩될 경우 가장 알맞은 템플릿을 리턴한다.
		if (template.child!=null) {
			Template t = getMostSuitableTemplate_sub(template.child, startIndex, endIndex);
			if (t!=null) return t;
		}
		
		if (template.indexTypeName()<=startIndex && endIndex<=template.indexRightPair()) {
			return template;
		}
		return null;
	}
	
	/** 템플릿이 중첩될 경우 가장 알맞은 템플릿에 fullClassName을 준다.*/
	boolean modifyTemplateName(Template template, String fullClassName, int indexVarUse) {
		if (template==null) return false;
		
		
		// 템플릿이 중첩될 경우 가장 알맞은 템플릿에 fullClassName을 준다.
		if (template.child!=null) {
			boolean r = modifyTemplateName( template.child, fullClassName, indexVarUse);
			if (r) return r;
		}
		
		if (template.indexTypeName()<=indexVarUse && indexVarUse<=template.indexLeftPair()) {				
			template.typeName = fullClassName;
			return true;
		}
		else if (template.indexTypeNameToChange()<=indexVarUse) {
			template.typeNameToChange = fullClassName;
			return true;
		}
		
		return false;
	}
	
	/** template으로부터 fullname을 얻는다. 
	 * 템플릿이 중첩되더라도 자식 템플릿을 포함하여 fullname을 얻는다.
	 * FindClassesFromTypeDecls(), confirmTypeName()에서 var.getType()이나 func.getReturnType()을 통해서
	 * getFullNameType()이 호출되어 Template의 typeName과 typeNameToChange의 풀이름이 설정된다.
	 * Template의 typeName과 typeNameToChange의 풀이름이 정확하지 않을때는 
	 * (예를들어, Stack<Character.Subset> s;와 같은 경우) 
	 * modifyTemplateName()를 통해서 올바른 풀이름으로 변경된다.*/
	String getFullNameFromTemplate(HighArray_CodeString src, Template template) {
		if (template==null) return null;
		
		String r = template.typeName;
		String child = null;
		if (template.child!=null) {
			child = getFullNameFromTemplate(src, template.child);
		}
		
		if (child!=null) {
			r += "<" + child + ">";
		}
		else {
			r += "<" + template.typeNameToChange + ">";
		}
		return r;
		
	}
	
	
	/** 타입이름을 fullname으로 확실하게 정의해 준다. 
	 * 예를들어 Character.Subset은 var.getType()이나 func.getReturnType()-
	 * 즉 getFullNameType()에서 fullname으로 정의할수 없으므로
	 * (Short이름 클래스(Character)에 내부클래스(Subset)가 있을 경우 full이름을 얻을수없다.) 
	 * findMemberUsesUsingNamespace_sub()을 거쳐서 fullname으로 확실하게 정의할 수 있을때 
	 * confirmTypeName()호출하여 해당 변수나 함수의 typeName이나 returnType을 확실하게 정의해 준다.
	 * 타입이 사용되는 곳은 클래스에서 상속클래스, 상속인터페이스, 지역변수, 멤버변수, 
	 * 함수의 리턴타입, constructor에서, 그리고  
	 * 템플릿에서 템플릿기호내부의 타입과 기호외부의 타입 등이므로 모두 확실하게 정의해 줘야 한다.
	 * 템플릿은 변수선언안에 들어가있으므로 지역변수, 멤버변수 처리시 해결된다.
	 * @param varUse : Character.Subset에서 Subset의 varUse*/
	public String confirmTypeName(HighArray_CodeString src, FindVarUseParams varUse) {
		String fullClassName = null;
		if (varUse.memberDecl instanceof FindClassParams) {
			FindClassParams classParams = (FindClassParams) varUse.memberDecl;
			fullClassName = classParams.name;
		}
		if (fullClassName==null) return null;
		
		int i;
		// 지역변수의 타입을 정확한 fullname으로 바꿔준다.
		int len = mlistOfAllLocalVarDeclarations.getCount();
		for (i=0; i<len; i++) {
			FindVarParams var = (FindVarParams) mlistOfAllLocalVarDeclarations.getItem(i);
			if (var.typeStartIndex()<=varUse.index() && varUse.index()<=var.typeEndIndex()) {
		
				String fullname = getFullNameType(this, var.typeStartIndex(), var.typeEndIndex());
				var.typeName = fullname;
				return var.typeName;
				
			}
			else if (varUse.index()<var.typeStartIndex()) break;
		}
		// 멤버변수의 타입을 정확한 fullname으로 바꿔준다.
		int len2 = mlistOfAllMemberVarDeclarations.getCount();
		for (i=0; i<len2; i++) {
			FindVarParams var = (FindVarParams) mlistOfAllMemberVarDeclarations.getItem(i);
			if (var.typeStartIndex()<=varUse.index() && varUse.index()<=var.typeEndIndex()) {
				if ( var.typeStartIndex()==506 && var.typeEndIndex()==515) {
					int a;
					a=0;
					a++;
				}
				/*String fullname = var.getType(src, var.typeStartIndex(), var.typeEndIndex());
				int dimension = CompilerHelper.getArrayDimension(this, fullname);
				var.typeName = CompilerHelper.getArrayType(fullClassName, dimension);
				return;*/
				String fullname = getFullNameType(this, var.typeStartIndex(), var.typeEndIndex());
				var.typeName = fullname;
				return var.typeName;
			}
			else if (varUse.index()<var.typeStartIndex()) break;
		}
		// 함수의 리턴타입을 정확한 fullname으로 바꿔준다.
		for (i=0; i<mlistOfAllFunctions.count; i++) {
			FindFunctionParams func = (FindFunctionParams) mlistOfAllFunctions.getItem(i);
			if (func.returnTypeStartIndex()<=varUse.index() && varUse.index()<=func.returnTypeEndIndex()) {
				/*String fullname = func.getReturnType(src, func.returnTypeStartIndex, func.returnTypeEndIndex);
				int dimension = CompilerHelper.getArrayDimension(this, fullname);
				func.returnType = CompilerHelper.getArrayType(fullClassName, dimension);
				return;*/
				String fullname = getFullNameType(this, func.returnTypeStartIndex(), func.returnTypeEndIndex());
				func.returnType = fullname;
				return func.returnType;
			}
			else if (varUse.index()<func.returnTypeStartIndex()) break;
		}
		// 상속클래스이름을 정확한 fullname으로 바꿔준다.
		for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
			FindClassParams c = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
			if (c.startIndexOfClassNameToExtend()<=varUse.index() && varUse.index()<=c.endIndexOfClassNameToExtend()) {
				/*String fullname = getFullNameType(this, c.startIndexOfClassNameToExtend, c.endIndexOfClassNameToExtend);
				int dimension = CompilerHelper.getArrayDimension(this, fullname);
				c.classNameToExtend = CompilerHelper.getArrayType(fullClassName, dimension);
				return;*/
				String fullname = getFullNameType(this, c.startIndexOfClassNameToExtend(), c.endIndexOfClassNameToExtend());
				//int dimension = CompilerHelper.getArrayDimension(this, fullname);
				//c.classNameToExtend = CompilerHelper.getArrayType(fullClassName, dimension);
				c.classNameToExtend = fullname;
				return c.classNameToExtend;
			}
			//else if (varUse.index()<c.returnTypeStartIndex) break;
		}
		//구현인터페이스이름을 정확한 fullname으로 바꿔준다.
		for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
			FindClassParams c = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
			if (c.listOfStartIndexOfInterfaceNamesToImplement!=null) {
				for (int j=0; j<c.listOfStartIndexOfInterfaceNamesToImplement.count; j++) {
					IndexForHighArray startIndex = (IndexForHighArray) c.listOfStartIndexOfInterfaceNamesToImplement.getItem(j);
					IndexForHighArray endIndex = (IndexForHighArray) c.listOfEndIndexOfInterfaceNamesToImplement.getItem(j);
					if (startIndex.index()<=varUse.index() && varUse.index()<=endIndex.index()) {
						String fullname = getFullNameType(this, startIndex.index(), endIndex.index());
						/*int dimension = CompilerHelper.getArrayDimension(this, fullname);
						c.interfaceNamesToImplement.list[j] = CompilerHelper.getArrayType(fullClassName, dimension);
						return;*/
						c.interfaceNamesToImplement.list[j] = fullname;
						return c.interfaceNamesToImplement.list[j];
					}
				}
			}
		}
		

		return null;
		
	}
	
	
	
	
	/** FindVarParams.getType(), FindFunctionParams.getReturnType()에서 호출된다. 
	 * 위 함수들에서는 typeName, returnType멤버의 캐시를 이용하므로  
	 * typeName, returnType이 정확한 풀이름을 갖고있을때는
	 * getFullNameType()을 이용하지 말고 var.getType()과 func.getReturnType()을 이용해야 한다.
	 * 
	 * FindClass()함수에서 이 함수를 호출하면 아직 mListOfAllDefinedClass에 파일에 정의된 클래스들이 모두 등록되지 않은 상태이므로
	 * 같은 파일에 정의된 클래스를 상속하는 경우나 인터페이스를 구현하는 경우 fullname이 틀릴수 있다. 
	 * 또한 같은 파일에 정의된 클래스의 이름이 확실하게 fullname으로 정해진 이후에 호출되어야 한다. 
     * FindAllClassesAndItsMembers2_sub()을 호출한 이후 클래스 이름을 fullname으로 정하고 클래스 캐시에 등록한 이후를 말한다.
     * (FindAllClassesAndItsMembers2()와 start_onlyInterface()함수를 참조한다.)
	 * 따라서 FindAllClassesAndItsMembers2_sub()을 호출한 이후에 getFullNameType()을 호출해야 한다.
	 * 예를들어 FindClassesFromTypeDecls()는 FindAllClassesAndItsMembers2_sub()을 호출한 이후에 호출된다.
	 * 
	 * 
	   
	 * 
	 * 변수타입의 full name을 얻을때 호출한다. 
	 * 단순히 startIndex(), endIndex()사이의 공백, 주석을 제외한 문자열을 연결한다.
	 * 
	 * 연결한 스트링의 첫번째 부분으로
	 * full name이 아니고 import라이브러리타입, 라이브러리 타입(Object, Class, Integer등)도 아니면 
	 * 패키지 이름을 붙인 full name을 리턴한다.
	 * full name이 아니고 내장타입(int, char등), 라이브러리 타입이면 그대로 리턴한다.
	 * @param compiler : compiler의 mBuffer와 startIndex(), endIndex()가 일치해야 한다. 
	 * 즉 startIndex(), endIndex()는 올바른 소스 파일에서의 인덱스여야 한다.
	 * @param src
	 * @param startIndex() : 포함
	 * @param endIndex() : 포함, int getFullNameIndex(HighArray_CodeString src, int startIndex)을 통해 알아낸다.
	 * @return : full name, 내장타입(int, char등), import라이브러리타입, 자바라이브러리타입(Object, Class, Integer등)
	 */
	public String getFullNameType(Compiler compiler, int startIndex, int endIndex) {
		//HighArray_CodeString src = compiler.mBuffer;
		if (startIndex==2717) {
			int a;
			a=0;
			a++;
		}
		HighArray_CodeString src = compiler.mBuffer;
		int i;
		String str = "";
		int indexOfHead=startIndex; // fullname에서 첫번째 부분의 인덱스
		boolean isHead = true;
		for (i=startIndex; i<=endIndex; i++) {
			try{
			CodeString s = src.getItem(i);
			if (CompilerHelper.IsBlank(s) || CompilerHelper.IsComment(s)) {
				continue;
			}
			else if (s.equals(".")) {
				str += s.str;
				isHead = false;
			}
			else if (compiler.IsIdentifier(s)) {
				str += s.str;
				if (isHead) {
					indexOfHead = i;
					isHead = false;
				}
			}
			else {//배열, 템플릿 기호등
				str += s.str;
			}
			}catch(Exception e) {
				int a;
				a=0;
				a++;	
			}
		}
		if (str.equals("Pair")) {
			int a;
			a=0;
			a++;
		}
		if (str.contains("Character")) {
			int a;
			a=0;
			a++;
		}
		boolean isArray = false;
		int dimensionOfArray = 0;
		
		dimensionOfArray = CompilerHelper.getArrayDimension(compiler, str);
		if (dimensionOfArray!=0) {
			isArray = true;
			str = str.substring(0, str.length()-2*dimensionOfArray);
		}
		
		// 템플릿이나 템플릿 배열일 경우, 예를들어 Stack<String> s; 혹은 Stack<String>[] s;
		boolean isTemplate = false;
		int indexTemplateLeftPair = str.indexOf("<");
		int indexTemplateRightPair = -1;
		Template template = null;
		String typeNameToChange = null;
		
		String strTemplate = "";
		if (indexTemplateLeftPair!=-1) {			
			int indexTemplateLeftPairInmBuffer = compiler.Skip(src, false, "<", startIndex, endIndex);
			int indexTemplateRightPairInmBuffer = 
					CompilerHelper.CheckParenthesis(src, "<", ">", indexTemplateLeftPairInmBuffer, endIndex, false);
			
			indexTemplateRightPair = 
				CompilerHelper.CheckParenthesis(str, "<", ">", indexTemplateLeftPair, str.length()-1, false);
			isTemplate = true;
			if (indexTemplateRightPair!=-1) {
				// Stack<Object>에서 <Object>부분을 말한다. 
				//strTemplate = str.substring(indexTemplateLeftPair, indexTemplateRightPair+1);
				typeNameToChange = 
						compiler.getFullNameType(compiler, indexTemplateLeftPairInmBuffer+1, indexTemplateRightPairInmBuffer-1);
				strTemplate = 
						"<"+typeNameToChange+">";
				// 위에서 Stack부분
				str = str.substring(0, indexTemplateLeftPair);
				
				int s = compiler.SkipBlank(src, false, indexTemplateLeftPairInmBuffer, indexTemplateRightPairInmBuffer);
				int e = compiler.SkipBlank(src, true, indexTemplateLeftPairInmBuffer, indexTemplateRightPairInmBuffer);
				
				template = compiler.getMostSuitableTemplate(mlistOfAllTemplates, s, e);
				if (template!=null) {
					template.typeName = str;
					template.typeNameToChange = typeNameToChange;
				}
				
			}
		}
		
		String oldFullName = str;		
		
		String remainder=null;
		
		int indexOfDot = str.indexOf('.');
		if (indexOfDot!=-1) {
			// Compiler_types.Language lang;에서 str은 Compiler_types이고 remainder는 Language이다.
			remainder = str.substring(indexOfDot+1, str.length());
			str = str.substring(0, indexOfDot);
		}		
		
		if (compiler.IsDefaultType(str)) {
			if (isArray==false) {
				if (isTemplate) {
					return null;					
				}
				// 템플릿이 아니면
				if (remainder==null) return str;
				else {
					return null;
				}
			}
			else {
				if (isTemplate) {
					return null;
				}
				// 템플릿이 아니면
				if (remainder==null) return CompilerHelper.getArrayType(str, dimensionOfArray);
				else {
					return null;
				}
			}
		}
		
		// import java.lang.Character; Character.Subset s; 에서 
		// str은 Character이고 remainder는 Subset이다.
			String importedLibrary = compiler.getTypeOfImportLibrary(str);
			if (importedLibrary!=null) {
				if (isArray==false) {
					if (isTemplate) {
						if (template!=null) {
							if (remainder==null) template.typeName = importedLibrary;
							else template.typeName = importedLibrary + "." + remainder;
							template.typeNameToChange = typeNameToChange;
						}
						if (remainder==null) {
							//return importedLibrary+strTemplate;
							boolean exists = this.checkTypeNameInFileList(compiler, importedLibrary);
							if (exists) {
								return importedLibrary + strTemplate;
							}
							else return null;
						}
						else {
							String name = importedLibrary + "." + remainder;
							boolean exists = this.checkTypeNameInFileList(compiler, name);
							if (exists) {
								return name + strTemplate;
							}
							else return null;
						}
					}
					if (remainder==null) {
						//return importedLibrary;
						boolean exists = this.checkTypeNameInFileList(compiler, importedLibrary);
						if (exists) {
							return importedLibrary;
						}
						else return null;
					}
					else {
						String name = importedLibrary + "." + remainder;
						boolean exists = this.checkTypeNameInFileList(compiler, name);
						if (exists) {
							return importedLibrary + "." + remainder;
						}
						else return null;
					}
				}
				else {
					if (isTemplate) {
						if (template!=null) {
							if (remainder==null) template.typeName = importedLibrary;
							else template.typeName = importedLibrary+"."+remainder;
							template.typeNameToChange = typeNameToChange;
						}
						if (remainder==null) {
							//return CompilerHelper.getArrayType(importedLibrary+strTemplate, dimensionOfArray);
							boolean exists = this.checkTypeNameInFileList(compiler, importedLibrary);
							if (exists) {
								return CompilerHelper.getArrayType(importedLibrary+strTemplate, dimensionOfArray);
							}
							else return null;
						}
						else {
							String name = importedLibrary + "." + remainder;
							boolean exists = this.checkTypeNameInFileList(compiler, name);
							if (exists) {
								return CompilerHelper.getArrayType(name+strTemplate, dimensionOfArray);
							}
							else return null;
						}
					}
					if (remainder==null) {
						//return CompilerHelper.getArrayType(importedLibrary, dimensionOfArray);
						boolean exists = this.checkTypeNameInFileList(compiler, importedLibrary);
						if (exists) {
							return CompilerHelper.getArrayType(importedLibrary, dimensionOfArray);
						}
						else return null;
					}
					else {
						String name = importedLibrary + "." + remainder;
						boolean exists = this.checkTypeNameInFileList(compiler, name);
						if (exists) {
							return CompilerHelper.getArrayType(name, dimensionOfArray);
						}
						else return null;
					}
				}
			}
		//}
			
			String starClass = compiler.getTypeOfImportStar(str);
			if (starClass!=null) {
				if (isArray==false) {
					if (isTemplate) {
						if (template!=null) {
							if (remainder==null) template.typeName = starClass;
							else template.typeName = starClass+"."+remainder;
							template.typeNameToChange = typeNameToChange;
						}
						if (remainder==null) {
							// return starClass+strTemplate;
							boolean exists = this.checkTypeNameInFileList(compiler, starClass);
							if (exists) {
								return starClass+strTemplate;
							}
							else return null;
						}
						else  {
							String name = starClass + "." + remainder;
							boolean exists = this.checkTypeNameInFileList(compiler, name);
							if (exists) {
								return name+strTemplate;
							}
							else return null;
						}
					}
					if (remainder==null) {
						//return starClass;
						boolean exists = this.checkTypeNameInFileList(compiler, starClass);
						if (exists) {
							return starClass;
						}
						else return null;
					}
					else  {
						String name = starClass + "." + remainder;
						boolean exists = this.checkTypeNameInFileList(compiler, name);
						if (exists) {
							return name;
						}
						else return null;
					}
				}
				else {
					if (isTemplate) {
						if (template!=null) {
							if (remainder==null) template.typeName = starClass;
							else  template.typeName = starClass+"."+remainder;
							template.typeNameToChange = typeNameToChange;
						}
						if (remainder==null) {
							//return CompilerHelper.getArrayType(starClass+strTemplate, dimensionOfArray);
							boolean exists = this.checkTypeNameInFileList(compiler, starClass);
							if (exists) {
								return CompilerHelper.getArrayType(starClass+strTemplate, dimensionOfArray);
							}
							else return null;
						}
						else {
							String name = starClass + "." + remainder;
							boolean exists = this.checkTypeNameInFileList(compiler, name);
							if (exists) {
								return CompilerHelper.getArrayType(name+strTemplate, dimensionOfArray);
							}
							else return null;
						}
					}
					if (remainder==null) {
						//return CompilerHelper.getArrayType(starClass, dimensionOfArray);
						boolean exists = this.checkTypeNameInFileList(compiler, starClass);
						if (exists) {
							return CompilerHelper.getArrayType(starClass, dimensionOfArray);
						}
						else return null;
					}
					else  {
						String name = starClass + "." + remainder;
						boolean exists = this.checkTypeNameInFileList(compiler, name);
						if (exists) {
							return CompilerHelper.getArrayType(name, dimensionOfArray);
						}
						else return null;
					}
				}
			}
			
			FindClassParams classParams = compiler.getTypeOfDefinedClass(str, indexOfHead);
			if (classParams!=null) {
				String fullname = classParams.name;
				if (isArray==false) {
					if (isTemplate) { 
						if (template!=null) {
							if (remainder==null) template.typeName = fullname;
							else template.typeName = fullname+"."+remainder;
							template.typeNameToChange = typeNameToChange;
						}
						if (remainder==null) {
							//return fullname+strTemplate;
							boolean exists = this.checkTypeNameInFileList(compiler, fullname);
							if (exists) {
								return fullname+strTemplate;
							}
							else return null;
						}
						else  {
							String name = fullname + "." + remainder;
							boolean exists = this.checkTypeNameInFileList(compiler, name);
							if (exists) {
								return name+strTemplate;
							}
							else return null;
						}
					}
					if (remainder==null) {
						//return fullname;
						boolean exists = this.checkTypeNameInFileList(compiler, fullname);
						if (exists) {
							return fullname;
						}
						else return null;
					}
					else {
						String name = fullname + "." + remainder;
						boolean exists = this.checkTypeNameInFileList(compiler, name);
						if (exists) {
							return name;
						}
						else return null;
					}
				}
				else {
					if (isTemplate) {
						if (template!=null) {
							if (remainder==null) template.typeName = fullname;
							else  template.typeName = fullname+"."+remainder;
							template.typeNameToChange = typeNameToChange;
						}
						if (remainder==null) {
							//return CompilerHelper.getArrayType(fullname+strTemplate, dimensionOfArray);
							boolean exists = this.checkTypeNameInFileList(compiler, fullname);
							if (exists) {
								return CompilerHelper.getArrayType(fullname+strTemplate, dimensionOfArray);
							}
							else return null;
						}
						else {
							String name = fullname + "." + remainder;
							boolean exists = this.checkTypeNameInFileList(compiler, name);
							if (exists) {
								return CompilerHelper.getArrayType(name+strTemplate, dimensionOfArray);
							}
							else return null;
						}
					}
					if (remainder==null) {
						//return CompilerHelper.getArrayType(fullname, dimensionOfArray);
						boolean exists = this.checkTypeNameInFileList(compiler, fullname);
						if (exists) {
							return CompilerHelper.getArrayType(fullname, dimensionOfArray);
						}
						else return null;
					}
					else {
						String name = fullname + "." + remainder;
						boolean exists = this.checkTypeNameInFileList(compiler, name);
						if (exists) {
							return CompilerHelper.getArrayType(name, dimensionOfArray);
						}
						else return null;
					}
				}
			}
		
		//if (str.contains(".")==false) {	
			String packageClass = compiler.getTypeOfPackageLibrary(str);
			if (packageClass!=null) {
				if (isArray==false) {
					if (isTemplate) {
						if (template!=null) {
							if (remainder==null) template.typeName = packageClass;
							else template.typeName = packageClass+"."+remainder;
							template.typeNameToChange = typeNameToChange;
						}
						if (remainder==null) {
							//return packageClass+strTemplate;
							boolean exists = this.checkTypeNameInFileList(compiler, packageClass);
							if (exists) {
								return packageClass+strTemplate;
							}
							else return null;
						}
						else {
							String name = packageClass + "." + remainder;
							boolean exists = this.checkTypeNameInFileList(compiler, name);
							if (exists) {
								return name+strTemplate;
							}
							else return null;
						}
					}
					if (remainder==null) {
						//return packageClass;
						boolean exists = this.checkTypeNameInFileList(compiler, packageClass);
						if (exists) {
							return packageClass;
						}
						else return null;
					}
					else  {
						String name = packageClass + "." + remainder;
						boolean exists = this.checkTypeNameInFileList(compiler, name);
						if (exists) {
							return name;
						}
						else return null;
					}
				}
				else {
					if (isTemplate) {
						if (template!=null) {
							if (remainder==null) template.typeName = packageClass;
							else  template.typeName = packageClass+"."+remainder;
							template.typeNameToChange = typeNameToChange;
						}
						if (remainder==null) {
							//return CompilerHelper.getArrayType(packageClass+strTemplate, dimensionOfArray);
							boolean exists = this.checkTypeNameInFileList(compiler, packageClass);
							if (exists) {
								return CompilerHelper.getArrayType(packageClass+strTemplate, dimensionOfArray);
							}
							else return null;
						}
						else {
							String name = packageClass + "." + remainder;
							boolean exists = this.checkTypeNameInFileList(compiler, name);
							if (exists) {
								return CompilerHelper.getArrayType(name+strTemplate, dimensionOfArray);
							}
							else return null;
						}
					}
					if (remainder==null) {
						// return CompilerHelper.getArrayType(packageClass, dimensionOfArray);
						boolean exists = this.checkTypeNameInFileList(compiler, packageClass);
						if (exists) {
							return CompilerHelper.getArrayType(packageClass, dimensionOfArray);
						}
						else return null;
					}
					else {
						String name = packageClass + "." + remainder;
						boolean exists = this.checkTypeNameInFileList(compiler, name);
						if (exists) {
							return CompilerHelper.getArrayType(name, dimensionOfArray);
						}
						else return null;
					}
				}
			}
		//}
		
				
		//if (str.contains(".")==false) {
			if (compiler.IsTypeOfDefaultLibrary(str)) {
				if (isArray==false) {
					if (isTemplate) {
						if (template!=null) {
							if (remainder==null) template.typeName = "java.lang."+str;
							else template.typeName = "java.lang."+str+"."+remainder;
							template.typeNameToChange = typeNameToChange;
						}
						if (remainder==null) {
							// return "java.lang."+str+strTemplate;
							boolean exists = this.checkTypeNameInFileList(compiler, "java.lang."+str);
							if (exists) {
								return "java.lang."+str+strTemplate;
							}
							else return null;
						}
						else  {
							String name = "java.lang."+str + "." + remainder;
							boolean exists = this.checkTypeNameInFileList(compiler, name);
							if (exists) {
								return name+strTemplate;
							}
							else return null;
						}
					}
					if (remainder==null) {
						//return "java.lang."+str;
						boolean exists = this.checkTypeNameInFileList(compiler, "java.lang."+str);
						if (exists) {
							return "java.lang."+str;
						}
						else return null;
					}
					else  {
						String name = "java.lang."+str + "." + remainder;
						boolean exists = this.checkTypeNameInFileList(compiler, name);
						if (exists) {
							return name;
						}
						else return null;
					}
				}
				else {
					if (isTemplate) {
						if (template!=null) {
							if (remainder==null) template.typeName = "java.lang."+str;
							else  template.typeName = "java.lang."+str+"."+remainder;
							template.typeNameToChange = typeNameToChange;
						}
						if (remainder==null) {
							//return CompilerHelper.getArrayType("java.lang."+str+strTemplate, dimensionOfArray);
							boolean exists = this.checkTypeNameInFileList(compiler, "java.lang."+str);
							if (exists) {
								return CompilerHelper.getArrayType("java.lang."+str+strTemplate, dimensionOfArray);
							}
							else return null;
						}
						else  {
							String name = "java.lang."+str + "." + remainder;
							boolean exists = this.checkTypeNameInFileList(compiler, name);
							if (exists) {
								return CompilerHelper.getArrayType(name+strTemplate, dimensionOfArray);
							}
							else return null;
						}
					}
					if (remainder==null) {
						//return CompilerHelper.getArrayType("java.lang."+str, dimensionOfArray);
						boolean exists = this.checkTypeNameInFileList(compiler, "java.lang."+str);
						if (exists) {
							return CompilerHelper.getArrayType("java.lang."+str, dimensionOfArray);
						}
						else return null;
					}
					else {
						String name = "java.lang."+str + "." + remainder;
						boolean exists = this.checkTypeNameInFileList(compiler, name);
						if (exists) {
							return CompilerHelper.getArrayType(name, dimensionOfArray);
						}
						else return null;
					}
				}
			}
		
		
		if (isArray==false) {
			if (isTemplate) {
				if (template!=null) {
					if (remainder==null) template.typeName = str;
					else template.typeName = str+"."+remainder;
					template.typeNameToChange = typeNameToChange;
				}
				if (remainder==null) {
					// return str+strTemplate;
					boolean exists = this.checkTypeNameInFileList(compiler, str);
					if (exists) {
						return str+strTemplate;
					}
					else return null;
				}
				else {
					String name = str + "." + remainder;
					boolean exists = this.checkTypeNameInFileList(compiler, name);
					if (exists) {
						return name+strTemplate;
					}
					else return null;
				}
			}
			if (remainder==null) {
				//return str;
				boolean exists = this.checkTypeNameInFileList(compiler, str);
				if (exists) {
					return str;
				}
				else return null;
			}
			else {
				String name = str + "." + remainder;
				boolean exists = this.checkTypeNameInFileList(compiler, name);
				if (exists) {
					return name;
				}
				else return null;
			}
		}
		else {
			if (isTemplate) {
				if (template!=null) {
					if (remainder==null) template.typeName = str;
					else template.typeName = str+"."+remainder;
					template.typeNameToChange = typeNameToChange;
				}
				if (remainder==null) {
					//return CompilerHelper.getArrayType(str+strTemplate, dimensionOfArray);
					boolean exists = this.checkTypeNameInFileList(compiler, str);
					if (exists) {
						return CompilerHelper.getArrayType(str+strTemplate, dimensionOfArray);
					}
					else return null;
				}
				else {
					String name = str + "." + remainder;
					boolean exists = this.checkTypeNameInFileList(compiler, name);
					if (exists) {
						return CompilerHelper.getArrayType(name+strTemplate, dimensionOfArray);
					}
					else return null;
				}
			}
			if (remainder==null) {
				boolean exists = this.checkTypeNameInFileList(compiler, str);
				if (exists) {
					return CompilerHelper.getArrayType(str, dimensionOfArray);
				}
				else return null;
			}
			else  {
				String name = str + "." + remainder;
				boolean exists = this.checkTypeNameInFileList(compiler, name);
				if (exists) {
					return CompilerHelper.getArrayType(name, dimensionOfArray);
				}
				else return null;
			}
		}
		
		/*int indexOfDot = str.indexOf('.');
		if (indexOfDot!=-1) {
			// java.util.jar.JarFile
			// FindClassParams fullNameClass = CompilerHelper.loadClass(compiler, str);
			// if (fullNameClass!=null) {
			boolean exists = this.checkTypeNameInFileList(compiler, oldFullName);
			if (exists) {
				if (isArray==false) {
					if (isTemplate) {
						if (template!=null) {
							template.typeName = str;
							template.typeNameToChange = typeNameToChange;
						}
						return str+strTemplate;
					}
					return str;
				}
				else {
					if (isTemplate) {
						if (template!=null) {
							template.typeName = str;
							template.typeNameToChange = typeNameToChange;
						}
						return CompilerHelper.getArrayType(str+strTemplate, dimensionOfArray);
					}
					return CompilerHelper.getArrayType(str, dimensionOfArray);
				}
			}
		}*/
			
		
		//return null;
	}
	
	/** startIndex()부터 endIndex()까지 공백, 주석 등을 제외하여 각 스트링을 연결한 결과 스트링을 리턴한다.*/
	public CodeString getFullName(HighArray_CodeString src, int startIndex, int endIndex) {
		int j;
		CodeString r = new CodeString("", Compiler.textColor);
		for (j=startIndex; j<=endIndex; j++) {
			CodeString item = src.getItem(j);
			if (CompilerHelper.IsBlank(item) || CompilerHelper.IsComment(item)) continue;
			else r = r.concate(item);
		}
		return r;
	}
	
		
	
	/** startIndex()부터 '.'이 아닌 구분자(공백, ';', ','등)를 만날 때까지 full name을 리턴한다.
	 * startIndex()가 공백과 같은 구분자이면 빈스트링을 리턴한다.*/
	public ArrayListCodeString getFullName(HighArray_CodeString src, int startIndex) {
		int j;
		ArrayListCodeString type = new ArrayListCodeString(10);
		for (j=startIndex; j<src.count; j++) {
			CodeString item = src.getItem(j);
			if (CompilerHelper.IsBlank(item) || CompilerHelper.IsComment(item)) continue;
			else if (item.equals(".")) {
				int prevIndex = SkipBlank(src, true, startIndex, j-1);
				if (prevIndex==startIndex-1) break;
				CodeString prev = src.getItem(prevIndex);
				// . 이전에는 id가 와야 한다.
				if ( (IsIdentifier(prev))==false ) {
					errors.add(new Error(this, prevIndex,prevIndex,"Invalid identifier, invalid token. : "+prev));
					break;
				}
				int nextIndex = SkipBlank(src, false, j+1, src.count-1);
				if (nextIndex==src.count) break;
				CodeString next = src.getItem(nextIndex);
				// . 다음에는 id가 와야 한다.
				if ( (IsIdentifier(next))==false ) {
					errors.add(new Error(this, nextIndex,nextIndex,"Invalid identifier, invalid token. : "+next));
					break;
				}
				type.add(item);
			}			
			else if (CompilerHelper.IsSeparator(item)) { // .이 아닌 구분자이면
				break;
			}
			else if (IsKeyword(item)) { // .이 아닌 키워드이면
				errors.add(new Error(this, j,j,"Invalid identifier, invalid token : "+item));
				break;
			}
			else { // id
				type.add(item);
			}
			
		}
		return type;
	}
	
	/** startIndex()부터 '.'이 아닌 구분자(공백이 아닌 ';', ',', 연산자 등) 
	 * 혹은 "extends, implements"를 만날 때까지 full name의 마지막 인덱스(구분자 등의 index-1)를 리턴한다.
	 * 'a[i+1]'등의 배열원소에서 ]다음의 인덱스(구분자를 제외한 공백등은 포함가능한 인덱스), 
	 * 'a(i+1)'등의 함수호출에서 )다음의 인덱스(구분자를 제외한 공백등은 포함가능한 인덱스)를 리턴, 
	 * 이것이 getFullNameIndex와 다른 점이다. PostFixConverter에서 a(i+1)등을 합칠때 사용한다.
	 * 
	 *  <br><br>startIndex는 fullname에 속하는 인덱스여야 하고 
	 * "."으로 연결되지 않는 공백은 구분자에 속한다. 
	 * 그러므로 "."으로 연결되지 않은 공백을 만나면 isReverse가 false일 때는 공백 인덱스-1을 리턴하고
	 * true일 때는 공백인덱스+1을 리턴한다.*/
	public int getFullNameIndex2(HighArray_CodeString src, int startIndex) {
		int i;
		int nextIndex;
		CodeString next;
		//if (isReverse==false) {
		 	//startIndex = SkipBlank(src, false, startIndex, src.count-1);
			for (i=startIndex; i<src.count; ) {
				CodeString item = src.getItem(i);
				if (CompilerHelper.IsBlank(item)) {
					int blankProcess = this.processBlankInGetFullNameIndex(src, false, i);
					if (blankProcess==-1) return i-1;
					else {
						i = blankProcess;
						// "."부터 시작
						i--;
						continue;
					}
				}
				else if (CompilerHelper.IsComment(item)) {
					i++;
					continue;
				}
				
				else if (CompilerHelper.IsNumber2(item)!=0 || IsIdentifier(item)) { 
					nextIndex = SkipBlank(src, false, i+1, src.count-1);
					if (nextIndex==src.count) {
						return nextIndex;
					}
					next = src.getItem(nextIndex);
					if (next.equals(".")) {
						i = nextIndex;
						continue;
					}
					else if (next.equals("(")) { // 함수호출
						//int indexOfEndOfSmallBlock = Compiler.findEndIndexOfSmallBlock(listOfSmallBlocks, nextIndex);
						int indexOfEndOfSmallBlock = 
								CompilerHelper.CheckParenthesis(src, "(", ")", nextIndex, src.count-1, false);
						if (indexOfEndOfSmallBlock!=-1) {
							i = indexOfEndOfSmallBlock+1;						
							continue;
						}
						else {
							return nextIndex;
						}
					}
					else if (next.equals("[")) { // 배열원소
						while (true) {
							int j = CompilerHelper.CheckParenthesis(src, "[", "]", nextIndex, src.count-1, false);
							if (j!=-1) {
								i = j+1;
								nextIndex = this.SkipBlank(src, false, j+1, src.count-1);
								if (nextIndex==src.count) return src.count-1; 
								if (src.getItem(nextIndex).equals("[")==false) {
									i = nextIndex;
									break;								
								}
								continue;
							}
							else {
								return nextIndex;
							}
						}
					}
					else {// ), ]등
						//return nextIndex;
						return i;
					}
				}
				else if (item.equals(".")) {
					nextIndex = SkipBlank(src, false, i+1, src.count-1);
					if (nextIndex==src.count) {
						return nextIndex-1;
					}
					next = src.getItem(nextIndex);
					if (IsIdentifier(next)) {
						i = nextIndex;
					}
					else {
						return i;
					}
				}
				else { // 키워드, 연산자와 같은 구분자 등
					return i-1;
				}
			} // for i
		
		return i;
		
	}
	
	
	
	
	/** 타입캐스트시 무엇을 타입캐스트해야하는지 알아내기 위해 호출 
	 * isReverse가 false이고 startIndex가 a의 인덱스일 경우 (int)a.b().c.d 에서 d의 인덱스를 리턴한다. 
	 * (A)a[i]에서 a의 인덱스를 리턴한다. (A)a(p1, p2)에서 a의 인덱스를 리턴한다.  
	 * ((java.lang.Object)buffer[i]).equals("(")에서 startIndex가 buffer의 인덱스일 경우 buffer의 인덱스를 리턴한다.
	 * isReverse가 true이고 startIndex가 d의 인덱스일 경우 a.b().c.d 에서 a의 인덱스를 리턴한다.
	 * 못찾으면 -1을 리턴, varUse의 인덱스나 -1을 리턴하므로 마지막 부분에 공백은 포함 안 한다.*/
	public int getFullNameIndex3(HighArray_CodeString src, /*boolean isReverse,*/ int startIndex) {
		int i;
		int j;
		
		//if (isReverse==false) {
			int startIndexInmListOfAllVarUses;		
			startIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, this.mlistOfAllVarUses, 
					0, startIndex, true);
			if (startIndexInmListOfAllVarUses==-1) return -1;
			
			boolean isInterrupted = false;
			int len = mlistOfAllVarUses.getCount();
			for (i=startIndexInmListOfAllVarUses; i<len; ) {
				FindVarUseParams varUse = (FindVarUseParams) mlistOfAllVarUses.getItem(i);
				/*if (varUse.typeCast!=null) { // 타입캐스트문은 건너뛴다. (int)i에서 startIndex가 (일 경우
					i = getIndexInmListOfAllVarUses(src, this.mlistOfAllVarUses, 
							startIndexInmListOfAllVarUses, varUse.typeCast.rightPair, true);
					continue;					
				}*/
				if (varUse.child==null) return varUse.index();
				
				// ((java.lang.Object)buffer[i]).equals("(") 여기에서 buffer를 리턴하도록 한다.
				for (j=varUse.index()+1; j<varUse.child.index(); j++) {
					j = SkipBlank(src, false, j, src.count-1);
					CodeString item = src.getItem(j);
					if (item.equals("[")) {
						int rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", j, src.count-1, false);
						if (rightPair==-1) j = src.count;
						else j = rightPair;
					}
					/*else if (item.equals("(")) {
						boolean isJump = false;
						int indexID = SkipBlank(src, true, startIndex, j-1);
						if (indexID!=startIndex-1 && IsIdentifier(src.getItem(indexID))) {
							if (indexID==varUse.index()) {
								if (varUse.child==null) return varUse.index();
								else {
									isJump = true;
								}
							}
							else isJump = true;
							//return indexID;
						}
						else { // ((new RectForPage(owner, bounds, backColor, isUpOrDown))).a
							isJump = true;
						}
						if (isJump) {
							int rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", j, src.count-1, false);
							if (rightPair==-1) j = src.count;
							else j = rightPair;
						}
					}*/
					else if (item.equals(")")) {
						isInterrupted = true;
						break;
					}
					else {
					}
				}
				if (isInterrupted) return varUse.index();
				i = getIndexInmListOfAllVarUses(src, this.mlistOfAllVarUses, 
						startIndexInmListOfAllVarUses, varUse.child.index(), true);
				if (i==-1) return -1;
			}
		
		return -1;
		
	}
	
	/** startIndex부터 '.'이 아닌 구분자(공백이 아닌 ';', ',', 연산자 등), 다큐주석의 이전 인덱스를 리턴한다. 
	 * 혹은 "extends, implements"를 만날 때까지 full name의 마지막 인덱스(구분자 등의 index-1)를 리턴한다.
	 * a.b().c.d에서 d의 인덱스를 리턴한다. a(i).b에서 b의 인덱스를 리턴, b[i]에서 b의 인덱스를 리턴, a.b()에서 b를 리턴
	 * (new RectForPage(owner, bounds, backColor, isUpOrDown)).a 에서 a를 리턴 */
	public int getFullNameIndexWithPostfix(HighArray_CodeString src, CodeStringEx token, boolean isReverse, int startIndex) {		
		int j;
		if (startIndex==448) {
			int a;
			a=0;
			a++;
		}
		if (isReverse==false) {
			for (j=startIndex; j<src.count; j++) {
				CodeString item = null;
				try{
				item = src.getItem(j);
				}catch(Exception e) {
					e.printStackTrace();
					int a;
					a=0;
				}
				if (CompilerHelper.IsBlank(item)) continue;
				else if (CompilerHelper.IsDocuComment(item)) return j-1;
				else if (CompilerHelper.IsAnnotation(item)) return j-1;
				else if (CompilerHelper.IsComment(item)) continue;
				else if (item.equals(".")) {
				}
				else if (item.equals("[")) {
					// a[i].b일 경우 첨자를 건너뛴다. a[i+1]의 경우 a를 리턴, a.b[i]에서 b를 리턴
					// new int[10][10]에서 startIndex는 int를 가리킬때 int의 인덱스를 리턴
					int indexID = SkipBlank(src, true, startIndex, j-1);
					if (indexID<0) {
						int rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", j, src.count-1, false);
						if (rightPair==-1) j = src.count;
						else j = rightPair;
						continue;
					}
					CodeString id = src.getItem(indexID);
					if (indexID!=startIndex-1 && (IsIdentifier(id) || IsDefaultType(id))) {
						 
						//FindVarUseParams varUse = this.getVarUseWithIndex(mlistOfAllVarUses, 0, indexID).r;
						FindVarUseParams varUse = getVarUseWithIndex(mlistOfAllVarUsesHashed, id.str, /*indexID*/this.toSrcIndex(mBuffer, token, indexID));
						if (varUse.child==null)	return indexID;
						else j = toPostfixIndex(src, token, varUse.child.index())-1; 
					}
					
					else { // ((new RectForPage(owner, bounds, backColor, isUpOrDown))).a
						int rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", j, src.count-1, false);
						if (rightPair==-1) j = src.count;
						else j = rightPair;
					}
				}
				else if (item.equals("(")) { 
					// a(i).b일 경우 괄호를 건너뛴다. a(i+1)의 경우 a를 리턴, a.b(i)에서 b를 리턴
					int indexID = SkipBlank(src, true, startIndex, j-1);
					if (indexID<0) {
						int rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", j, src.count-1, false);
						if (rightPair==-1) j = src.count;
						else j = rightPair;
						continue;
					}
					CodeString id = src.getItem(indexID);
					if (indexID!=startIndex-1 && IsIdentifier(id)) {
						//FindVarUseParams varUse = this.getVarUseWithIndex(mlistOfAllVarUses, 0, indexID).r;
						try {
						FindVarUseParams varUse = getVarUseWithIndex(mlistOfAllVarUsesHashed, id.str, /*indexID*/this.toSrcIndex(mBuffer, token, indexID));
						if (varUse.child==null)	return indexID;
						else j = toPostfixIndex(src, token, varUse.child.index())-1; 
						}catch(Exception e) {
							e.printStackTrace();
							int a;
							a=0;
							a++;
						}
					}
					else { // ((new RectForPage(owner, bounds, backColor, isUpOrDown))).a, (int)1
						int rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", j, src.count-1, false);
						if (rightPair==-1) j = src.count;
						else j = rightPair;
					}
				}
				else if (item.equals("this") || item.equals("super") || item.equals("null") || 
						item.equals("true") || item.equals("false")) continue;
				
				
				else if (CompilerHelper.IsSeparator(item)) { // '.', 공백이 아닌 구분자이면
					return j-1;
				}
				
				else if (IsKeyword(item)) {
					return j-1;
				}
				// new int[10][10]에서 item이 int일때 return하면 안된다.
				/*else if (IsDefaultType(item)) { 
					return j-1;
				}*/
				else {
				}
			}			
			return j;
		}
		else {
			for (j=startIndex; j>=0; j--) {
				CodeString item = src.getItem(j);
				if (CompilerHelper.IsBlank(item)) continue;
				else if (CompilerHelper.IsDocuComment(item)) return j+1;
				else if (CompilerHelper.IsAnnotation(item)) return j+1;
				else if (CompilerHelper.IsComment(item)) continue;
				
				else if (item.equals(".")) {
				}
				else if (item.equals("]")) {
					int leftPair = CompilerHelper.CheckParenthesis(src, "[", "]", 0, j, true);
					if (leftPair==-1) j = 0;
					else j = leftPair;
				}
				else if (item.equals(")")) {
					int leftPair = CompilerHelper.CheckParenthesis(src, "(", ")", 0, j, true);
					if (leftPair==-1) {
						return -1;
					}
					
					int indexID = SkipBlank(src, true, 0, leftPair-1);
					if (indexID!=-1 && IsIdentifier(src.getItem(indexID))) {
						return indexID;
					}
				}
				else if (item.equals("this") || item.equals("super") || item.equals("null") || 
						item.equals("true") || item.equals("false")) continue;
				
				
				
				else if (CompilerHelper.IsSeparator(item)) { // '.', 공백이 아닌 구분자이면
					//return j-1;
					return j+1;
				}
				//else if (item.equals("extends") || item.equals("implements")) {
				else if (IsKeyword(item)) {
					return j+1;
				}
				else if (IsDefaultType(item)) {
					return j+1;
				}
				else {
				}
			}			
			return j;
		}
	}
	
	/** blank가 separator역할을 하면 -1을 리턴하고 
	 * 그렇지않고 "."과 함께 연결된 역할을 하면 "."의 인덱스를 리턴한다.
	 * fullName내에 "]"와 ")"같은 배열이나 함수호출이 올 수 있다.*/
	int processBlankInGetFullNameIndex(HighArray_CodeString src, boolean isReverse, int indexOfBlank) {
		int j = indexOfBlank;
		if (isReverse==false) {
			int prevIndex = this.SkipBlank(src, true, 0, j);
			CodeString prev = src.getItem(prevIndex);
			if (this.IsIdentifier(prev) || prev.equals("]")  || prev.equals(")")) {
				int nextIndex = this.SkipBlank(src, false, j, src.count-1);
				if (src.getItem(nextIndex).equals(".")) {
					j = nextIndex;
					return j;
				}
				else {//fullname의 끝
					return -1;
				}
			}
			else if (prev.equals(".")) {
				int nextIndex = this.SkipBlank(src, false, j, src.count-1);
				if (this.IsIdentifier(src.getItem(nextIndex))) {
					j = nextIndex;
					return j;
				}
				else {//fullname의 끝
					return -1;
				}
			}
		}//if (isReverse==false) {
		else {
			int nextIndex = this.SkipBlank(src, false, j, src.count-1);
			CodeString next = src.getItem(nextIndex);
			if (this.IsIdentifier(next)) {
				int prevIndex = this.SkipBlank(src, true, 0, j);
				if (src.getItem(prevIndex).equals(".")) {
					j = prevIndex;
					return j;
				}
				else { //fullname의 끝
					return -1;
				}
			}
			else if (next.equals(".")) {
				int prevIndex = this.SkipBlank(src, true, 0, j);
				CodeString prev = src.getItem(prevIndex);
				if (this.IsIdentifier(prev) || prev.equals("]") || prev.equals(")")) {
					j = prevIndex;
					return j;
				}
				else {//fullname의 끝
					return -1;
				}
			}
		}//if (isReverse) {
		return -1;
	}
	
	
	/** blank가 separator역할을 하면 -1을 리턴하고 
	 * 그렇지않고 "."과 함께 연결된 역할을 하면 "."의 인덱스를 리턴한다.
	 * fullName내에 "]"와 ")"같은 배열이나 함수호출이 올 수 없다.*/
	int processBlankInGetFullNameIndex_OnlyType(HighArray_CodeString src, boolean isReverse, int indexOfBlank) {
		int j = indexOfBlank;
		if (isReverse==false) {
			int prevIndex = this.SkipBlank(src, true, 0, j);
			CodeString prev = src.getItem(prevIndex);
			if (this.IsIdentifier(prev)) {
				int nextIndex = this.SkipBlank(src, false, j, src.count-1);
				if (src.getItem(nextIndex).equals(".")) {
					j = nextIndex;
					return j;
				}
				else {//fullname의 끝
					return -1;
				}
			}
			else if (prev.equals(".")) {
				int nextIndex = this.SkipBlank(src, false, j, src.count-1);
				if (this.IsIdentifier(src.getItem(nextIndex))) {
					j = nextIndex;
					return j;
				}
				else {//fullname의 끝
					return -1;
				}
			}
			else {
				// "]", ")"와 같은 배열이나 함수호출이 오면 리턴
				return -1;
			}
		}//if (isReverse==false) {
		else {
			int nextIndex = this.SkipBlank(src, false, j, src.count-1);
			CodeString next = src.getItem(nextIndex);
			if (this.IsIdentifier(next)) {
				int prevIndex = this.SkipBlank(src, true, 0, j);
				if (src.getItem(prevIndex).equals(".")) {
					j = prevIndex;
					return j;
				}
				else { //fullname의 끝
					return -1;
				}
			}
			else if (next.equals(".")) {
				int prevIndex = this.SkipBlank(src, true, 0, j);
				CodeString prev = src.getItem(prevIndex);
				if (this.IsIdentifier(prev)) {
					j = prevIndex;
					return j;
				}
				else {//fullname의 끝, "]", ")"와 같은 배열이나 함수호출이 오면 리턴
					return -1;
				}
			}
		}//if (isReverse) {
		return -1;
	}
	
	
	/** startIndex부터 '.'이 아닌 구분자(공백이 아닌 ';', ',', 연산자 등), 다큐주석이나 애노테이션의 이전 인덱스를 리턴한다. 
	 * 혹은 "extends, implements"를 만날 때까지 full name의 마지막 인덱스(구분자 등의 index-1)를 리턴한다.
	 * a.b().c.d에서 d의 인덱스를 리턴한다. a(i).b에서 b의 인덱스를 리턴, b[i]에서 b의 인덱스를 리턴, a.b()에서 b를 리턴
	 * (new RectForPage(owner, bounds, backColor, isUpOrDown)).a 에서 a를 리턴 
	 *  
	 *  <br><br>startIndex는 fullname에 속하는 인덱스여야 하고 
	 * "."으로 연결되지 않는 공백은 구분자에 속한다. 
	 * 그러므로 "."으로 연결되지 않은 공백을 만나면 isReverse가 false일 때는 공백 인덱스-1을 리턴하고
	 * true일 때는 공백인덱스+1을 리턴한다.*/
	public int getFullNameIndex(HighArray_CodeString src, boolean isReverse, int startIndex) {		
		int j;
		if (startIndex>=1500) {
			int a;
			a=0;
			a++;
		}
		
		if (isReverse==false) {
			//startIndex = this.SkipBlank(src, isReverse, startIndex, src.count-1);
			for (j=startIndex; j<src.count; j++) {
				CodeString item = src.getItem(j);
				if (CompilerHelper.IsBlank(item)) {
					int blankProcess = this.processBlankInGetFullNameIndex(src, isReverse, j);
					if (blankProcess==-1) return j-1;
					else j = blankProcess;
				}
				else if (CompilerHelper.IsDocuComment(item)) {
					continue;
					//return j-1;
				}
				else if (CompilerHelper.IsAnnotation(item)) {
					continue;
					//return j-1;
				}
				else if (CompilerHelper.IsComment(item)) continue;
				else if (item.equals(".")) {
				}
				else if (item.equals("[")) {
					// a[i].b일 경우 첨자를 건너뛴다. a[i+1]의 경우 a를 리턴, a.b[i]에서 b를 리턴
					// new int[10][10]에서 startIndex는 int를 가리킬때 int의 인덱스를 리턴
					int indexID = SkipBlank(src, true, startIndex, j-1);
					if (indexID<0) {
						int rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", j, src.count-1, false);
						if (rightPair==-1) j = src.count;
						else j = rightPair;
						continue;
					}
					CodeString id = src.getItem(indexID);
					if (indexID!=startIndex-1 && (IsIdentifier(id) || IsDefaultType(id))) {
						 
						//FindVarUseParams varUse = this.getVarUseWithIndex(mlistOfAllVarUses, 0, indexID).r;
						FindVarUseParams varUse = getVarUseWithIndex(mlistOfAllVarUsesHashed, id.str, indexID);
						if (varUse.child==null)	return indexID;
						else j = varUse.child.index()-1; 
					}
					
					else { // ((new RectForPage(owner, bounds, backColor, isUpOrDown))).a
						int rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", j, src.count-1, false);
						if (rightPair==-1) j = src.count;
						else j = rightPair;
					}
				}
				else if (item.equals("(")) { 
					// a(i).b일 경우 괄호를 건너뛴다. a(i+1)의 경우 a를 리턴, a.b(i)에서 b를 리턴
					int indexID = SkipBlank(src, true, startIndex, j-1);
					if (indexID<0) {
						int rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", j, src.count-1, false);
						if (rightPair==-1) j = src.count;
						else j = rightPair;
						continue;
					}
					CodeString id = src.getItem(indexID);
					if (indexID!=startIndex-1 && IsIdentifier(id)) {
						//FindVarUseParams varUse = this.getVarUseWithIndex(mlistOfAllVarUses, 0, indexID).r;
						FindVarUseParams varUse = getVarUseWithIndex(mlistOfAllVarUsesHashed, id.str, indexID);
						if (varUse.child==null)	return indexID;
						else j = varUse.child.index()-1; 
					}
					else { // ((new RectForPage(owner, bounds, backColor, isUpOrDown))).a, (int)1
						int rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", j, src.count-1, false);
						if (rightPair==-1) j = src.count;
						else j = rightPair;
					}
				}
				else if (item.equals("<")) { 
					Template t = null;
					int rightPairIndex = CompilerHelper.CheckParenthesis(src, "<", ">", j, src.count-1, false);
					if (rightPairIndex!=-1) {
						t = isTemplate(src, rightPairIndex);
						if (t==null) {
							return j-1;
						}
						else {
							j = rightPairIndex;
						}
					}
				}
				else if (item.equals("this") || item.equals("super") || item.equals("null") || 
						item.equals("true") || item.equals("false")) continue;
				
				
				else if (CompilerHelper.IsSeparator(item)) { // '.', 공백이 아닌 구분자이면
					return j-1;
				}
				
				else if (item.equals("new")) {
					continue;
				}
				
				else if (IsKeyword(item)) {
					return j-1;
				}
				// new int[10][10]에서 item이 int일때 return하면 안된다.
				//else if (IsDefaultType(item)) { 
				//	return j-1;
				//}
				else {
				}
			}			
			return j;
		}
		else {
			//startIndex = this.SkipBlank(src, isReverse, 0, startIndex);
			for (j=startIndex; j>=0; j--) {
				CodeString item = src.getItem(j);
				if (CompilerHelper.IsBlank(item)) {
					int blankProcess = this.processBlankInGetFullNameIndex(src, isReverse, j);
					if (blankProcess==-1) return j+1;
					else j = blankProcess;
					//continue;
				}
				else if (CompilerHelper.IsDocuComment(item)) {
					//return j+1;
					continue;
				}
				else if (CompilerHelper.IsAnnotation(item)) {
					//return j+1;
					continue;
				}
				else if (CompilerHelper.IsComment(item)) continue;
				
				else if (item.equals(".")) {
				}
				else if (item.equals("]")) {
					int leftPair = CompilerHelper.CheckParenthesis(src, "[", "]", 0, j, true);
					if (leftPair==-1) j = 0;
					else j = leftPair;
				}
				else if (item.equals(")")) {
					int leftPair = CompilerHelper.CheckParenthesis(src, "(", ")", 0, j, true);
					if (leftPair==-1) {
						return -1;
					}
					
					int indexID = SkipBlank(src, true, 0, leftPair-1);
					if (indexID!=-1 && IsIdentifier(src.getItem(indexID))) {
						return indexID;
					}
				}
				else if (item.equals(">")) { 
					Template t = null;
					int leftPairIndex = CompilerHelper.CheckParenthesis(src, "<", ">", 0, j, true);
					if (leftPairIndex!=-1) {
						t = isTemplate(src, j);
						if (t==null) {
							return j+1;
						}
						else {
							j = leftPairIndex;
						}
					}
				}
				else if (item.equals("this") || item.equals("super") || item.equals("null") || 
						item.equals("true") || item.equals("false")) continue;
				
				
				
				else if (CompilerHelper.IsSeparator(item)) { // '.', 공백이 아닌 구분자이면
					//return j-1;
					return j+1;
				}
				//else if (item.equals("extends") || item.equals("implements")) {
				else if (item.equals("new")) {
					continue;
				}
				else if (IsKeyword(item)) {
					return j+1;
				}
				else if (IsDefaultType(item)) {
					return j+1;
				}
				else {
				}
			}			
			return j;
		}
	}
	
	/** 독립적인 함수호출문 문장을 찾을 때 사용한다. 
	 * isReverse가 false 일때 구분자등의 인덱스-1을 리턴하고,
	 * isReverse가 true 일때 구분자등의 인덱스+1을 리턴한다.
	 * 
	 * <br><br>startIndex는 fullname에 속하는 인덱스여야 하고 
	 * "."으로 연결되지 않는 공백은 구분자에 속한다. 
	 * 그러므로 "."으로 연결되지 않은 공백을 만나면 isReverse가 false일 때는 공백 인덱스-1을 리턴하고
	 * true일 때는 공백인덱스+1을 리턴한다.*/
	public int getFullNameIndex5(HighArray_CodeString src, boolean isReverse, int startIndex) {		
		int j;
		if (startIndex==448) {
			int a;
			a=0;
			a++;
		}
		if (isReverse==false) {
			for (j=startIndex; j<src.count; j++) {
				CodeString item = src.getItem(j);
				if (CompilerHelper.IsBlank(item)) {
					//continue;
					int blankProcess = this.processBlankInGetFullNameIndex(src, isReverse, j);
					if (blankProcess==-1) return j-1;
					else j = blankProcess;
				}
				else if (CompilerHelper.IsDocuComment(item)) {
					//return j-1;
					continue;
				}
				else if (CompilerHelper.IsAnnotation(item)) {
					//return j-1;
					continue;
				}
				else if (CompilerHelper.IsComment(item)) continue;
				else if (item.equals(".")) {
				}
				else if (item.equals("[")) {
					// a[i].b일 경우 첨자를 건너뛴다. a[i+1]의 경우 a를 리턴, a.b[i]에서 b를 리턴
					// new int[10][10]에서 startIndex는 int를 가리킬때 int의 인덱스를 리턴
					int rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", j, src.count-1, false);
					if (rightPair==-1) j = src.count;
					else j = rightPair;
				}
				else if (item.equals("(")) { 
					// a(i).b일 경우 괄호를 건너뛴다. a(i+1)의 경우 a를 리턴, a.b(i)에서 b를 리턴
					int rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", j, src.count-1, false);
					if (rightPair==-1) j = src.count;
					else j = rightPair;
				}
				else if (item.equals("this") || item.equals("super") || item.equals("null") || 
						item.equals("true") || item.equals("false")) continue;
				
				
				else if (CompilerHelper.IsSeparator(item)) { // '.', 공백이 아닌 구분자이면
					return j-1;
				}
				
				else if (IsKeyword(item)) {
					return j-1;
				}
				
				else {
				}
			}			
			return j;
		}
		else {
			for (j=startIndex; j>=0; j--) {
				CodeString item = src.getItem(j);
				if (CompilerHelper.IsBlank(item)) {
					//continue;
					int blankProcess = this.processBlankInGetFullNameIndex(src, isReverse, j);
					if (blankProcess==-1) return j+1;
					else j = blankProcess;
				}
				//else if (CompilerHelper.IsDocuComment(item)) return j+1;
				//else if (CompilerHelper.IsAnnotation(item)) return j+1;
				else if (CompilerHelper.IsComment(item)) continue;
				
				else if (item.equals(".")) {
				}
				else if (item.equals("]")) {
					int leftPair = CompilerHelper.CheckParenthesis(src, "[", "]", 0, j, true);
					if (leftPair==-1) j = 0;
					else j = leftPair;
				}
				else if (item.equals(")")) {
					int leftPair = CompilerHelper.CheckParenthesis(src, "(", ")", 0, j, true);
					if (leftPair==-1) j = 0;
					else j = leftPair;
				}
				else if (item.equals("this") || item.equals("super") || item.equals("null") || 
						item.equals("true") || item.equals("false")) continue;
				
				
				
				else if (CompilerHelper.IsSeparator(item)) { // '.', 공백이 아닌 구분자이면
					//return j-1;
					return j+1;
				}
				//else if (item.equals("extends") || item.equals("implements")) {
				else if (IsKeyword(item)) {
					return j+1;
				}
				else if (IsDefaultType(item)) {
					return j+1;
				}
				else {
				}
			}			
			return j;
		}
	}
	
	/**start2()와 start_onlyInterface()에서 호출된다.
	 * @param compiler : CompilerHelper의 loadClassFromSrc_onlyInterface()에서 
	 * compiler.start_onlyInterface(...) 이렇게 호출이 되고  start_onlyInterface()에서
	 * RegisterPackageName(...)호출을 하면
	 * RegisterPackageName()내부의 this나 this가 없는 
	 * Compiler클래스의 멤버 사용은 compiler(호출오브젝트)의 멤버사용이므로 없어도 되는 것이다.
	 * start2()에서 호출이 되어도 마찬가지이다.
	 * @param src : compiler.mBuffer와 같다.*/
	public void RegisterPackageName(HighArray_CodeString src) {
		int i;
		int indexOfStartOfPackage = -1;
		int indexOfEndOfPackage = -1;
		for (i=0; i<src.count; i++) {
			CodeString str = src.getItem(i);
			if (CompilerHelper.IsBlank(str) || CompilerHelper.IsComment(str)) {
				continue;
			}
			else if (str.equals("package")) {
				indexOfStartOfPackage = i;
				int path = SkipBlank(src, false, i+1, src.count-1);
				int j;				
				if (path==src.count) {
					errors.add(new Error(this, i,i,"Invalid package"));
					return;
				}
				String type = "";
				for (j=path; j<src.count; j++) {
					CodeString item = src.getItem(j);
					if (CompilerHelper.IsBlank(item) || CompilerHelper.IsComment(item)) {
						i = j;
						continue;
					}
					else if (item.equals(";")) {
						indexOfEndOfPackage = j-1;
						mlistOfPackageStatements.add(new PackageStatement(this, indexOfStartOfPackage, indexOfEndOfPackage));
						
						packageName = type;
						if (packageName.equals("") || packageName.charAt(packageName.length()-1)=='.') {
							errors.add(new Error(this, i,j,"Invalid package name"));
						}						
						i = j;
						// for j 종료
						break;
					}
					
					else if (item.equals(".")) {
						type += item;
						i = j;
					}
					else if (CompilerHelper.IsSeparator(item)) {
						errors.add(new Error(this, j,j,"Invalid package name, invalid separator. : "+item));
						i = j;
					}
					else if (IsIdentifier(item)){
						type += item;
						int nextIndex = SkipBlank(src, false, j+1, src.count-1);
						CodeString next = src.getItem(nextIndex);
						// id 다음에는 .이나 ;이 와야 한다.
						if ( (next.equals(".") || next.equals(";"))==false ) {
							errors.add(new Error(this, nextIndex,nextIndex,"Invalid import, invalid token. : "+next));
							i = nextIndex;
							break;
						}
						i = j;
					}
					else { // 키워드 등등
						errors.add(new Error(this, j,j,"Invalid package name, invalid token : "+item));
						i = j;
						break;
					}
				} //for (j=path; j<src.count; j++) {
				//i = j - 1;
				//i = j;
			} //if (str.equals("package")) {
			else if (str.equals("import") || IsAccessModifier(str) || str.equals("class")) {
				if (packageName==null || packageName.equals("")) {
					errors.add(new Error(this, i,i,"Invalid package, ';' not exists."));
				}
				break;
			}
			else {
				errors.add(new Error(this, i,i,"Invalid package, invalid token : "+str));
			}
			
		}// for i
	}
	
	/** import문에서 사용되는 네임스페이스와 같이 a.b.c.d와 같은 것만 가능. 
	 * reverse가 true일 경우 '.'을 제외한 구분자의 인덱스+1을 리턴한다. 
	 * 다큐먼트 주석(일반주석을 제외한)이나 애노테이션은 구분자와 같이 인덱스+1을 리턴한다.<br>
	 * reverse가 false일 경우 '.'을 제외한 구분자의 인덱스-1을 리턴한다.
	 * 두 경우 공통으로 에러시(구분자를 못만났으면) -1을 리턴
	 * 
	 *  <br><br>startIndex는 fullname에 속하는 인덱스여야 하고 
	 * "."으로 연결되지 않는 공백은 구분자에 속한다. 
	 * 그러므로 "."으로 연결되지 않은 공백을 만나면 isReverse가 false일 때는 공백 인덱스-1을 리턴하고
	 * true일 때는 공백인덱스+1을 리턴한다.
	 * 
	 * @param startIndex : reverse가 true일 경우 인덱스를 감소시키며 타입을 찾는다. 
	 * reverse가 false일 경우 인덱스를 증가시키며 타입을 찾는다.
	 * */
	public int getFullNameIndex0(HighArray_CodeString src, boolean reverse, int startIndex) {
		if (reverse==true) {
			int j;
			for (j=startIndex; j>=0; j--) {
				CodeString item = src.getItem(j);
				if (CompilerHelper.IsBlank(item)) {
					//continue;
					int blankProcess = this.processBlankInGetFullNameIndex_OnlyType(src, reverse, j);
					if (blankProcess==-1) return j+1;
					else j = blankProcess;
				}
				
				else if (CompilerHelper.IsDocuComment(item) || CompilerHelper.IsAnnotation(item)) {
					//return j+1;
					continue;
				}
				else if (CompilerHelper.IsComment(item)) continue;
				else if (item.equals(".")) continue;
				else if (item.equals(">")) { 
					Template t = null;
					int leftPairIndex = CompilerHelper.CheckParenthesis(src, "<", ">", 0, j, true);
					if (leftPairIndex!=-1) {
						t = isTemplate(src, j);
						if (t==null) {
							return j+1;
						}
						else {
							j = leftPairIndex;
						}
					}
				}
				else if (CompilerHelper.IsSeparator(item)) {
					return j+1;
				}
				else if (IsKeyword(item)) {
					return j+1;
				}
				else continue;
			}
			if (j<0) return 0; //Stack<Block>에서 startIndex가 0일 경우 j는 -1이 되므로 0을 리턴한다.
			return -1;
		}//if (reverse==true) {
		else {
			
			int j;
			for (j=startIndex; j<src.count; j++) {
				CodeString item = src.getItem(j);
				if (CompilerHelper.IsBlank(item)) {
					int blankProcess = this.processBlankInGetFullNameIndex_OnlyType(src, reverse, j);
					if (blankProcess==-1) return j-1;
					else j = blankProcess;
				}
				else if (CompilerHelper.IsComment(item)) {
					continue;
				}
				else if (item.equals(".")) continue;
				else if (item.equals("<")) { 
					Template t = null;
					int rightPairIndex = CompilerHelper.CheckParenthesis(src, "<", ">", j, src.count-1, false);
					if (rightPairIndex!=-1) {
						t = isTemplate(src, rightPairIndex);
						if (t==null) {
							return j-1;
						}
						else {
							j = rightPairIndex;
						}
					}
				}
				else if (CompilerHelper.IsSeparator(item)) {
					return j-1;
				}
				else if (IsKeyword(item)) {
					return j-1;
				}
				else continue;
			}
			return -1;
		}
	}
	
	/**import문에서 들어오는 class들을 mlistOfImportedClasses에 추가한다.
	 * start2()와 start_onlyInterface()에서 호출된다.
	 * @param compiler : CompilerHelper의 loadClassFromSrc_onlyInterface()에서 
	 * compiler.start_onlyInterface(...) 이렇게 호출이 되고  start_onlyInterface()에서
	 * RegisterImportedClasses(...)호출을 하면
	 * RegisterImportedClasses()내부의 this나 this가 없는 
	 * Compiler클래스의 멤버 사용은 compiler(호출오브젝트)의 멤버사용이므로 없어도 되는 것이다.
	 * start2()에서 호출이 되어도 마찬가지이다.
	 * @param src : compiler.mBuffer와 같다.*/
	public void RegisterImportedClasses(HighArray_CodeString src) {
		int i;
		mlistOfImportedClasses.destroy();
		String importName = null;
		int indexOfStartOfImport = -1;
		int indexOfEndOfImport = -1;
		
		for (i=0; i<src.count; i++) {
			CodeString str = src.getItem(i);
			if (CompilerHelper.IsBlank(str) || CompilerHelper.IsComment(str)) continue;
			else if (str.equals("package")) {
				int semicolonIndex = Skip(src, false, ";", i, src.count-1);
				if (semicolonIndex==src.count) {
					
				}
				i = semicolonIndex;
			}
			else if (str.equals("import")) {
				indexOfStartOfImport = i;
				int path = SkipBlank(src, false, i+1, src.count-1);
				int j;				
				if (path==src.count) {
					/*try {
						throw new Exception("Invalid import");
					} catch (Exception e) {
					}*/
					errors.add(new Error(this, i,i,"Invalid import"));
					return;
				}
				importName = "";
				for (j=path; j<src.count; j++) {
					CodeString item = src.getItem(j);
					if (CompilerHelper.IsBlank(item) || CompilerHelper.IsComment(item)) {
						i = j;
						continue;
					}
					else if (item.equals(";")) {
						// importName이 빈스트링이거나 .으로 끝나는 경우는 잘못된 것이다.
						if (importName.equals("") || importName.charAt(importName.length()-1)=='.') {
							errors.add(new Error(this, j-1,j-1,"Invalid import"));
							i = j;
							break;
						}
						indexOfEndOfImport = j-1;
						mlistOfImportStatements.add(new ImportStatement(this, indexOfStartOfImport, indexOfEndOfImport));
						mlistOfImportedClasses.add(importName);
						i = j;
						break; // for j 종료
					}
					
					else if (item.equals(".")) {
						importName += item;
						i = j;
					}
					else if (item.equals("*")) {
						int nextIndex = SkipBlank(src, false, j+1, src.count-1);
						CodeString next = src.getItem(nextIndex);
						// *는 임포트문의 마지막이어야 한다.
						if ((next.equals(";"))==false) {
							errors.add(new Error(this, nextIndex,nextIndex,"Invalid import, invalid star. : "+item));
							i = nextIndex; //세미콜론 다음부터
							break;
						}
						
						String importNameExceptStar;
						if (importName.charAt(importName.length()-1)=='.') {
							importNameExceptStar = importName.substring(0, importName.length()-1);
						}
						else {
							importNameExceptStar = importName.substring(0, importName.length());
						}
						
						indexOfEndOfImport = nextIndex-1;
						mlistOfImportStatements.add(new ImportStatement(this, indexOfStartOfImport, indexOfEndOfImport));
						
						mlistOfImportedClassesStar.add(importNameExceptStar);
						i = nextIndex; //세미콜론 다음부터
						break;
					}
					else if (CompilerHelper.IsSeparator(item)) {
						errors.add(new Error(this, j,j,"Invalid import, invalid separator. : "+item));
						i = j;
						break;
					}
					
					else if (IsIdentifier(item)) {
						int nextIndex = SkipBlank(src, false, j+1, src.count-1);
						CodeString next = src.getItem(nextIndex);
						// id 다음에는 .이나 ;이 와야 한다.
						if ((next.equals(".") || next.equals(";") || next.equals("*"))==false) {
							errors.add(new Error(this, nextIndex,nextIndex,"Invalid import, invalid token. : "+next));
							i = nextIndex;
							break;
						}
						importName += item;
						i = j;
					}
					else { // 키워드 등등
						errors.add(new Error(this, i,i,"Invalid import, invalid token : "+item));
						i = j;
						break;
					}
				} //for (j=path; j<src.count; j++) {
				//i = j - 1;
				//i = j;
			} //if (str.equals("import")) {	
			else if (str.equals("class") || IsAccessModifier(str)) {
				if (importName==null || importName.equals("")) {
					//errors.add(new Error(this, i,i,"Invalid package, ';' not exists."));
				}
				// for i 종료
				break;
			}
			else {
				errors.add(new Error(this, i,i,"Invalid import, invalid token : "+str));
			}
			
		}//for (i=0; i<src.count; i++) {
	}
	
	/** 조건문, 반복문 등 제어블록 조건 등의 소괄호 '(', ')'안에 할당문이 들어가 있는지를 확인한다.*/
	public boolean IsInParenthesisOfControlBlock(FindClassParams classParams, 
			FindAssignStatementParams assignStatement, FindVarUseParams varUse) 
	{	
		if (varUse.index()==7758) {
			int a;
			a=0;
			a++;
		}
		FindFunctionParams func = varUse.funcToDefineThisVarUse;
		if (func==null) return false;
		int i;
		if (func.startIndex()<assignStatement.startIndex() && 
				assignStatement.endIndex()<func.endIndex()) {
			ArrayListIReset listOfControlBlocks = func.listOfControlBlocks;
			if (listOfControlBlocks==null) return false;
			for (i=0; i<listOfControlBlocks.count; i++) {
				FindControlBlockParams controlBlock = (FindControlBlockParams)listOfControlBlocks.getItem(i);
				if (controlBlock.startIndex()<=assignStatement.startIndex() && 
						assignStatement.endIndex()<=controlBlock.endIndex()) {
					boolean r = this.IsInParenthesisOfControlBlock(controlBlock, assignStatement);
					if (r) return true;
				}
				if (controlBlock.indexOfLeftParenthesis()<=assignStatement.startIndex() && 
						assignStatement.endIndex()<=controlBlock.indexOfRightParenthesis())
					return true;
			}
		}
		return false;
		
	}
	
	/** 조건문, 반복문 등 제어블록 조건 등의 소괄호 '(', ')'안에 수식이 들어가 있는지를 확인한다.*/
	public boolean IsInParenthesisOfControlBlock(FindControlBlockParams controlBlock, FindAssignStatementParams assignStatement) 
	{
		if (controlBlock.listOfControlBlocks==null) return false;
		int i;
		for (i=0; i<controlBlock.listOfControlBlocks.count; i++) {
			FindControlBlockParams child = (FindControlBlockParams)controlBlock.listOfControlBlocks.getItem(i);
			if (child.startIndex()<=assignStatement.startIndex() && 
					assignStatement.endIndex()<=child.endIndex()) {
				// child 제어블록부터 호출
				boolean r = this.IsInParenthesisOfControlBlock(child, assignStatement);
				if (r) return true;
			}
			if (child.indexOfLeftParenthesis()<=assignStatement.startIndex() && 
					assignStatement.endIndex()<=child.indexOfRightParenthesis())
				return true;
		}
		return false;
		
	}
	
	
	/** f();   f1().f2();   f().a;-->이것은 error   
	 * f1(f2());     m.f1().f2();    m1.m2.f1().f2();
	 * return f(); 이것은 제외한다.
	 * 이와같은 독립적인 함수호출문장을 찾는다.*/
	public void FindIndependentFuncCall(HighArray_CodeString src, FindVarUseParams varUse, 
			FindIndependentFuncCallParams result) {
		int index;
		index = varUse.index();
		
		String varUseName = src.getItem(index).toString();
		
		if (index==1778) {
			int a;
			a=0;
			a++;
		}
		
		if (varUseName.equals("a")) {
			int a;
			a=0;
			a++;
		}
		
		int i;
		int startIndex=varUse.index();
		int endIndex;
		
		startIndex = this.getFullNameIndex5(src, true, startIndex);
		
		
		int indexOfStart = this.SkipBlank(src, true, 0, startIndex-1);
		if (indexOfStart==-1) return;
		
		
		CodeString start = src.getItem(indexOfStart);
		if ((start.equals(";") || start.equals("{") || start.equals("}") || 
				start.equals(")"))==false) {
			// 대입연산자나 타입이 오면 리턴한다.
			return;
		}
		
		endIndex = this.getFullNameIndex5(src, false, varUse.index());
		endIndex = SkipBlank(src, false, endIndex+1, src.count-1);
		
		
		CodeString end = src.getItem(endIndex);
		if (end.equals(";")) {
		}
		else {
			return;
		}
	
		
		result.startIndex = IndexForHighArray.indexRelative(result, src, startIndex);
		result.endIndex = IndexForHighArray.indexRelative(result, src, endIndex);
		result.found = true;
		
		
	}
	
	/** 해당클래스에서 lValue에 해당하는 변수선언 var를 찾는다.*/
	FindVarParams getVarDecl(HighArray_CodeString src, FindClassParams classParams, FindVarUseParams lValue) {
		int i, j;
		// 먼저 지역변수인지 검사
		for (i=0; i<classParams.listOfFunctionParams.count; i++) {
			FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
			if (func.startIndex()<lValue.index() && lValue.index()<func.endIndex()) {
				for (j=0; j<func.listOfVariableParams.count; j++) {
					FindVarParams var = (FindVarParams) func.listOfVariableParams.getItem(j);
					if (var.varNameIndex()==-1) continue;
					if (src.getItem(var.varNameIndex()).equals(src.getItem(lValue.index())))
						return var;
				}				
			}
			else if (lValue.index()<func.startIndex()) break;
		}
		// 멤버변수인지 검사
		for (i=0; i<classParams.listOfVariableParams.count; i++) {
			FindVarParams var = (FindVarParams) classParams.listOfVariableParams.getItem(i);
			if (var.varNameIndex()==-1) continue;
			if (src.getItem(var.varNameIndex()).equals(src.getItem(lValue.index())))
				return var;
		}
		return null;
		
	}
	
	
	
	/** 파일에서 정의한 함수들을 해당 클래스의 listOfStatements에 넣는다. 재귀적 호출
	 * @param FindClassParams classParams : 처음호출시는 최상위클래스, 재귀적호출시에는 해당클래스*/
	void inputClassesAndFunctionsToListOfStatementsOfSuitableClass_sub(HighArray_CodeString src, FindClassParams classParams, 
			FindFunctionParams statement) {
		int i;
		for (i=0; i<classParams.childClasses.count; i++) {
			FindClassParams c = (FindClassParams) classParams.childClasses.getItem(i);
			inputClassesAndFunctionsToListOfStatementsOfSuitableClass_sub(src, c, statement);
			if (statement.found) {
				return;
			}
		}
		
		if (classParams.startIndex()<statement.startIndex() && statement.endIndex()<classParams.endIndex()) {
			statement.found = true;
			classParams.listOfStatements.add(statement);
		}
	}
	
	/** 파일에서 정의한 클래스들을 해당 클래스의 listOfStatements에 넣는다. 재귀적 호출
	 * @param FindClassParams classParams : 처음호출시는 최상위클래스, 재귀적호출시에는 해당클래스*/
	void inputClassesAndFunctionsToListOfStatementsOfSuitableClass_sub(HighArray_CodeString src, FindClassParams classParams, 
			FindClassParams statement) {
		int i;
		for (i=0; i<classParams.childClasses.count; i++) {
			FindClassParams c = (FindClassParams) classParams.childClasses.getItem(i);
			inputClassesAndFunctionsToListOfStatementsOfSuitableClass_sub(src, c, statement);
			if (statement.found) {
				return;
			}
		}
		
		if (classParams.name.equals(statement.name)) return;
		else {
			if (classParams.startIndex()<statement.startIndex() && statement.endIndex()<classParams.endIndex()) {
				statement.found = true;
				classParams.listOfStatements.add(statement);
			}
		}
		
	}
	
	/** 클래스의 문장리스트를 만들때 클래스에 들어가지 못한 문장들을 파일상의 문장리스트(mlistOfFindStatementParams)에 넣는다.*/
	public void inputPackageAndImportAndSoOnToListOfStatementsOfFile(HighArray_CodeString src) {
		int i;
		for (i=0; i<mlistOfPackageStatements.count; i++) {
			FindStatementParams c = (FindStatementParams) mlistOfPackageStatements.getItem(i);
			mlistOfFindStatementParams.add(c);
		}
		for (i=0; i<mlistOfImportStatements.count; i++) {
			FindStatementParams c = (FindStatementParams) mlistOfImportStatements.getItem(i);
			mlistOfFindStatementParams.add(c);
		}
		for (i=0; i<mlistOfClass.count; i++) {
			FindStatementParams c = (FindStatementParams) mlistOfClass.getItem(i);
			mlistOfFindStatementParams.add(c);
		}
		for (i=0; i<this.mlistOfComments.count; i++) {
			FindStatementParams c = (FindStatementParams) mlistOfComments.getItem(i);
			if (c.found==false) {
				mlistOfFindStatementParams.add(c);
			}
		}
		
		for (i=0; i<mlistOfAnnotations.count; i++) {
			FindStatementParams c = (FindStatementParams) mlistOfAnnotations.getItem(i);
			if (c.found==false) {
				mlistOfFindStatementParams.add(c);
			}
		}
		
		ArrayListIReset result = new ArrayListIReset(mlistOfFindStatementParams.count);
		SortByIndex(mlistOfFindStatementParams, result);
		
		// 문장과 문장사이에서 나머지 문장들을 찾고 넣는다.
		mlistOfFindStatementParams = findAndSoOnStatements(src, 0, result, src.count-1);
	}
	
	
	/** 문장과 문장사이에서 나머지 문장들을 찾고 넣는다.
	 * @param input : startIndex()에 의해 미리 정렬되어 있어야 한다.
	 * @return : 이 함수의 처리 결과를 말한다.*/
	ArrayListIReset findAndSoOnStatements(HighArray_CodeString src, int startIndex, ArrayListIReset input, int endIndex) {
		if (input.count==0) {
			ArrayListIReset result = new ArrayListIReset(1);
			result.add(new AndSoOnStatement(this, startIndex, endIndex));
			return result;
		}
		int i;
		ArrayListIReset result = new ArrayListIReset(input.count*2);
		FindStatementParams s0 = (FindStatementParams) input.getItem(0);
		result.add(s0);
		if (0<s0.startIndex()) {
			result.add(new AndSoOnStatement(this, startIndex, s0.startIndex()-1)); 
		}
		for (i=0; i<input.count; i++) {
			FindStatementParams s = (FindStatementParams) input.getItem(i);
			if (s.endIndex()==1721) {
				int a;
				a=0;
				a++;
			}
			if (i>0) {				
				result.add(s);
			}
			if (i<input.count-1) { // 문장과 문장 사이
				FindStatementParams s1 = (FindStatementParams) input.getItem(i+1);
				if (s.endIndex()<s1.startIndex()) {
					result.add(new AndSoOnStatement(this, s.endIndex()+1, s1.startIndex()-1));
				}
			}
			else if (i==input.count-1) { // 마지막 문장과 src의 끝 사이
				if (s.endIndex()+1<=src.count-1) {
					result.add(new AndSoOnStatement(this, s.endIndex()+1, endIndex));
				}
			}
		}
		
		ArrayListIReset result2 = new ArrayListIReset(result.count);
		SortByIndex(result, result2);
		return result2;
	}
	
	/** 파일에서 정의한 주석과 Annotation들을 해당 클래스의 listOfStatements에 넣는다.
	 * 참고로 파일에서 정의한 클래스들과 함수들은 inputClassesAndFunctionsToListOfStatementsOfSuitableClass()에서, 
	 * 변수선언들은 FindAllClassesAndItsMembers2_sub()에서 넣는다.
	 * @param FindClassParams classParams : 최상위클래스*/
	/*public void inputCommentsAndAnnotationsToListOfStatementsOfSuitableClass(HighArray_CodeString src, FindClassParams classParams) {
		int i;
		for (i=0; i<mlistOfComments.count; i++) {
			Comment c = (Comment) mlistOfComments.getItem(i);
			c.found = false;
			this.inputStatementToSuitableBlock_caller(src, classParams, c);
		}
		
		for (i=0; i<mlistOfAnnotations.count; i++) {
			Annotation a = (Annotation) mlistOfAnnotations.getItem(i);
			a.found = false;
			this.inputStatementToSuitableBlock_caller(src, classParams, a);
		}
	}*/
	
	/** 파일에서 정의한 클래스들과 함수들을 해당 클래스의 listOfStatements에 넣는다.
	 * 참고로 주석은 inputCommentsToListOfStatementsOfSuitableClass()에서, 
	 * 변수선언들은 FindAllClassesAndItsMembers2_sub()에서 넣는다.
	 * @param FindClassParams classParams : 최상위클래스*/
	public void inputClassesAndFunctionsToListOfStatementsOfSuitableClass(HighArray_CodeString src, FindClassParams classParams) {
		int i;
		for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
			FindClassParams c = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
			if (c.name.equals(classParams.name)) continue;
			c.found = false;
			inputClassesAndFunctionsToListOfStatementsOfSuitableClass_sub(src, classParams, c);
		}
		
		for (i=0; i<mlistOfAllFunctions.count; i++) {
			FindFunctionParams f = (FindFunctionParams) mlistOfAllFunctions.getItem(i);
			f.found = false;
			inputClassesAndFunctionsToListOfStatementsOfSuitableClass_sub(src, classParams, f);
		}
	}
	
	
	
	
	/** mlistOfAllArrayIntializers에서 lValue의 varUse로 해당 배열초기화문을 찾는다.*/
	FindArrayInitializerParams getFindArrayInitializerParams(HighArray_CodeString src, FindVarUseParams varUse) {
		int i;
		for (i=0; i<this.mlistOfAllArrayIntializers.count; i++) {
			FindArrayInitializerParams arr = (FindArrayInitializerParams) mlistOfAllArrayIntializers.getItem(i);
			if (arr.nameIndex()==varUse.index()) return arr;
		}
		return null;
	}
	
	
	/** 현재 varUse가 배열초기화문의 변수가 아니면 false를 리턴한다.*/
	int isArrayInitializer(HighArray_CodeString src, FindVarUseParams varUse) {
		int indexEqual = SkipBlank(src, false, varUse.index()+1, src.count-1);           
        if (indexEqual==src.count) return -1;        
        CodeString equal = src.getItem(indexEqual);
        if (equal.equals("=")==false) return -1;
        
        int indexLeftPair = SkipBlank(src, false, indexEqual+1, src.count-1);
        if (indexLeftPair==src.count) return -1;
        CodeString leftPair = src.getItem(indexLeftPair);
        
        if (leftPair.equals("{")) {
        	int rightPair = CompilerHelper.CheckParenthesis(src, "{", "}", indexLeftPair, src.count-1, false);
        	return rightPair;
        }
        return -1;
        
	}
	
	/** a=++i + 2;, a=i++;, a=1+--i;, a=i--+2;
	 * i++; 의 경우에 startIndex()는 문장구분자+1, endIndex()는 ;을 포함하는 인덱스를 갖는다.*/
	public FindIncrementStatementParams findIndependentIncrementStatement(HighArray_CodeString src, FindIncrementStatementParams inc) {
		if (inc!=null) {
			int startIndex = this.SkipBlank(src, true, 0, inc.startIndex()-1);
			int endIndex = this.SkipBlank(src, false, inc.endIndex()+1, src.count-1);
			CodeString start = src.getItem(startIndex);
			CodeString end = src.getItem(endIndex);
			if (start.equals("{") || start.equals("}") || start.equals(";") ) {
				if (end.equals(";") || end.equals(")")) {
					inc.startIndex = IndexForHighArray.indexRelative(inc, src, startIndex+1);
					inc.endIndex = IndexForHighArray.indexRelative(inc, src, endIndex);
					return inc;
				}
			}			
		}
		return null;
	}
	
		
	/**i=++i+1; f(++i);와 같은 증감문을 문장 statement에서 찾아서 varUse에 연결하고 해당 문장의
	 * includesInc를 true로 만든다. */
	public void findIncrementStatementInStatement(HighArray_CodeString src, FindStatementParams statement) {
		int i;
		int startIndexInmBuffer = statement.startIndex();
		int endIndexInmBuffer = statement.endIndex();
		
		if (statement.startIndex()>=387 && statement.startIndex()<389) {
			int a;
			a=0;
			a++;
		}
		
		for (i=startIndexInmBuffer; i<=endIndexInmBuffer; i++) {
			CodeString str = src.getItem(i);
			if (str.equals("+") || str.equals("-")) {
				int nextIndex = this.SkipBlank(src, false, i+1, src.count-1);
				if (nextIndex>=src.count) {
					i++;
					continue;
				}
				CodeString next = src.getItem(nextIndex);
				if (next.equals(str.str)) { // ++, --
					int indexID = this.SkipBlank(src, false, nextIndex+1, src.count-1);
					CodeString id = src.getItem(indexID);
					if (IsIdentifier(id)) {
						int leftPair = SkipBlank(src, false, indexID+1, src.count-1);
						CodeString leftPairStr = src.getItem(leftPair);
						if (leftPairStr.equals("(")) { // ++i()
							this.errors.add(new Error(this, i, leftPair, "invalid "+str+next));
						}
						else {
							if (leftPairStr.equals("[")) {
								// ++a[b[++k+1]]
								// 배열을 붙여도 안 붙여도 된다.
							}
							// ++i, --i
							statement.includesInc = true;						
							FindIncrementStatementParams inc = new FindIncrementStatementParams(this, i, indexID); 
							FindVarUseParams varUse = this.getVarUseWithIndex(this.mlistOfAllVarUsesHashed, id.str, indexID);
							if (varUse!=null) {
								varUse.inc = inc;
								inc.lValue = varUse;
								if (str.equals("+")) inc.type = 0;
								else inc.type = 2;
							}
						}
					}// ++i, --i
					else {
						// i++, i--
						// i는 첫번째 +의 인덱스
						int indexID2 = this.SkipBlank(src, true, 0, i-1);
						CodeString id2 = src.getItem(indexID2);
						if (IsIdentifier(id2)) { // i++, i--
							statement.includesInc = true;
							FindIncrementStatementParams inc = new FindIncrementStatementParams(this, indexID2, nextIndex); 
							FindVarUseParams varUse = this.getVarUseWithIndex(this.mlistOfAllVarUsesHashed, id2.str, indexID2);
							if (varUse!=null) {
								varUse.inc = inc;
								inc.lValue = varUse;
								if (str.equals("+")) inc.type = 1;
								else inc.type = 3;
							}
						}
						else if (id2.equals("]")) { // a[k++]++
							int leftPair = CompilerHelper.CheckParenthesis(src, "[", "]", 0, indexID2, true);
							int indexID3 = this.SkipBlank(src, true, 0, leftPair-1);
							CodeString id3 = src.getItem(indexID3);
							if (IsIdentifier(id3)) {
								statement.includesInc = true;
								FindIncrementStatementParams inc = new FindIncrementStatementParams(this, indexID3, nextIndex); 
								FindVarUseParams varUse = this.getVarUseWithIndex(this.mlistOfAllVarUsesHashed, id3.str, indexID3);
								if (varUse!=null) {
									varUse.inc = inc;
									inc.lValue = varUse;
									if (str.equals("+")) inc.type = 1;
									else inc.type = 3;
								}
							}
						}//else if (id2.equals("]")) { // a[k++]++
						else if (id2.equals(")")) {
							int leftPair = CompilerHelper.CheckParenthesis(src, "(", ")", 0, indexID2, true);
							int indexID3 = this.SkipBlank(src, true, 0, leftPair-1);
							CodeString id3 = src.getItem(indexID3);
							if (IsIdentifier(id3)) { // a()++
								this.errors.add(new Error(this, i, indexID2, "invalid "+str+next));
							}
						}
						else { // 상수++, ++상수
							this.errors.add(new Error(this, i, indexID2, "invalid "+str+next));
						}
					} // i++, i--, a[k++]++
				}//if (next.equals(str.str)) {
			}//if (str.equals("+") || str.equals("-")) {
		}//for (i=startIndexInmBuffer; i<=endIndexInmBuffer; i++) {
	
	}
	
	
	/** ++i, i++, --i, i--
	 * i++; 의 경우에 startIndex()는 구분자+1, endIndex()는 ;을 포함하는 인덱스를 갖는다.*/
	public FindIncrementStatementParams findIndependentIncrementStatement_sub(HighArray_CodeString src, FindVarUseParams varUse) {
		if (varUse.index()==330) {
			int a;
			a=0;
			a++;
		}
		FindIncrementStatementParams r = null;
		int startIndex, endIndex;
		
		startIndex = this.getFullNameIndex5(src, true, varUse.index());
		if (startIndex<0) {
			return null;
		}
		startIndex = this.SkipBlank(src, true, 0, startIndex-1);
		if (startIndex<0) {
			return null;
		}
		
		endIndex = this.getFullNameIndex5(src, false, varUse.index());
		if (endIndex>src.count-1) {
			return null;
		}
		endIndex = this.SkipBlank(src, false, endIndex+1, src.count-1);
		if (endIndex>src.count-1) {
			return null;
		}
		
		// 여기까지 하면 startIndex()와 endIndex()는 모두 구분자의 인덱스가 된다.
		
		CodeString operator = src.getItem(startIndex);
		if (operator.equals("+")) {
			startIndex = this.SkipBlank(src, true, 0, startIndex-1);
			if (startIndex<0) {
				return null;
			}
			CodeString operator2 = src.getItem(startIndex);
			if (operator2.equals("+")) {
				r = new FindIncrementStatementParams(this, startIndex, endIndex-1);
				r.lValue = varUse;
				r.type = 0; // ++i
				return r;
			}
		}
		else if (operator.equals("-")) {
			startIndex = this.SkipBlank(src, true, 0, startIndex-1);
			if (startIndex<0) {
				return null;
			}
			CodeString operator2 = src.getItem(startIndex);
			if (operator2.equals("-")) {
				r = new FindIncrementStatementParams(this, startIndex, endIndex-1);
				r.lValue = varUse;
				r.type = 2; // --i
				return r;
			}
		}
		
		
		startIndex = this.getFullNameIndex5(src, true, varUse.index());
		if (startIndex<0) {
			return null;
		}
		startIndex = this.SkipBlank(src, true, 0, startIndex-1);
		if (startIndex<0) {
			return null;
		}
		
		endIndex = this.getFullNameIndex5(src, false, varUse.index());
		if (endIndex>src.count-1) {
			return null;
		}
		endIndex = this.SkipBlank(src, false, endIndex+1, src.count-1);
		if (endIndex>src.count-1) {
			return null;
		}
		
		// 여기까지 하면 startIndex()와 endIndex()는 모두 구분자의 인덱스가 된다.
		
		operator = src.getItem(endIndex);
		if (operator.equals("+")) {
			endIndex = this.SkipBlank(src, false, endIndex+1, src.count-1);
			if (endIndex>src.count-1) {
				return null;
			}
			CodeString operator2 = src.getItem(endIndex);
			if (operator2.equals("+")) {
				r = new FindIncrementStatementParams(this, startIndex+1, endIndex);
				r.lValue = varUse;
				r.type = 1; // i++
				return r;
			}
		}
		else if (operator.equals("-")) {
			endIndex = this.SkipBlank(src, false, endIndex+1, src.count-1);
			if (endIndex>src.count-1) {
				return null;
			}
			CodeString operator2 = src.getItem(endIndex);
			if (operator2.equals("-")) {
				r = new FindIncrementStatementParams(this, startIndex+1, endIndex);
				r.lValue = varUse;
				r.type = 3; // i--
				return r;
			}
		}
		
		return r;
	}
	
	
	/** varUse 다음에 '=', '+=', '-=', '*=', '/=', '%=' 가 있으면 lValue라고 판단한다. 
	 * @return '='의 인덱스를 리턴, lValue가 아니면 -1을 리턴*/
	public int IsLValue(HighArray_CodeString src, FindVarUseParams varUse) {
		if (varUse.index()==1932) {
			int a;
			a=0;
			a++;
		}
		if (varUse.originName.equals("b")) {
			int a;
			a=0;
			a++;
		}
		else if (varUse.name.equals("name[i]")) {
			int a;
			a=0;
			a++;
		}
		int i1, i2;
		int endIndex = src.count-1;
		// 공백 스킵
        i1 = SkipBlank(src, false, varUse.index()+1, endIndex);           
        if (i1==endIndex+1) return -1;        
        CodeString str1=null;
        try {
        str1 = src.getItem(i1);
        }catch(Exception e) {
        	int a;
        	a=0;
        	a++;
        	e.printStackTrace();
        }
        
        //  공백 스킵
        i2 = SkipBlank(src, false, i1+1, endIndex);           
        if (i2==endIndex+1) return -1;
        CodeString str2 = src.getItem(i2);
        
        CodeString id = src.getItem(varUse.index());
        int i;
        if (IsIdentifier(id)) {
        	// a[0][1] = 2;
        	if (str1.equals("[")) {
				for (i=i1; i<src.count; i++) {
					CodeString cstr = src.getItem(i);
					if (CompilerHelper.IsBlank(cstr) || 
							CompilerHelper.IsComment(cstr)) continue;
					else if (cstr.equals("[")) {
						int rightPair = CompilerHelper.
								CheckParenthesis(src, "[", "]", i, src.count-1, false);
						if (rightPair!=-1) {
							i = rightPair;
						}
					}
					else break;
				}
				i1 = i;
				str1 = src.getItem(i1);
				
				i2 = SkipBlank(src, false, i1+1, endIndex);           
			    if (i2==endIndex+1) return -1;
			    str2 = src.getItem(i2);
        	}
        	
        	// +=, -=, *=, /=, %=, |=, &= 등인 경우
        	if (CompilerHelper.IsAssignOperator(str1)) {        		
        		// <<=, >>=을 제외한 대입연산자들
        		if (str2.equals("=")) {
        			return i2;
        		}
        	}
        	else if (str1.equals(">")) {
    			if (str2.equals(">")) {
    				int i3 = SkipBlank(src, false, i2+1, endIndex);           
           		    if (i3==endIndex+1) return -1;
           		    CodeString str3 = src.getItem(i3);
           		    if (str3.equals("=")) return i3; // ">>="
           		    else return -1;
    			}
    			else  {
    				return -1;
    			}
    		}
    		else if (str1.equals("<")) {
    			if (str2.equals("<")) {
    				int i3 = SkipBlank(src, false, i2+1, endIndex);           
   	    		    if (i3==endIndex+1) return -1;
   	    		    CodeString str3 = src.getItem(i3);
   	    		    if (str3.equals("=")) return i3; // "<<="
   	    		    else return -1;
    			}
    			else {
    				return -1;
    			}
    		}
        	// =+, =-, =*, =/등은 에러, a=+1; a=-1;
        	else if (str1.equals("=")) {
        		if (str2.equals("=")) return -1;
        		else if (CompilerHelper.IsOperator(str2)) {
        			if (str2.equals("+") || str2.equals("-")) {
        				// 일단 =뒤의 +,-는 부호 operator로 간주하고 LValue로 리턴한다.
        				return i1;
        			}
        			else {
	        			//return i1;
	        			//Compiler.errors.add(new Error(this, i1,i2,"invalid compostive operator"));
	        			return -1;
        			}
        		}
        		else { // 일반적인 할당문의 =의 인덱스를 리턴한다.
        			return i1;
        		}
        	}
        }//if (IsIdentifier(id)) {
        return -1;
		
	}
	
	/** kewordIndex의 인덱스를 startIndex(), ';'의 인덱스를 endIndex()로 하는 
	 * 할당문을 리턴한다.
	 * result의 kewordIndex를 가지고 완전한 문장을 찾는다.
	 * @param result : in, out*/  
	public FindSpecialStatementParams FindSpecialStatement(HighArray_CodeString src, int keywordIndex) {
		int i1, i2;
		int endIndex = src.count-1;
		
        i1 = Skip(src, false, ";", keywordIndex+1, endIndex);
        if (i1==endIndex+1) {
        	return null;
        }
        
        /*i2 = Skip(src, true, ";", 0, result.kewordIndex-1);
        if (i2==-1) {
        	result.found = false;
        	return;
        }*/
        
        FindSpecialStatementParams result = new FindSpecialStatementParams(this, -1, -1, keywordIndex); 
        result.startIndex = IndexForHighArray.indexRelative(result, src, result.kewordIndex());
        result.endIndex = IndexForHighArray.indexRelative(result, src, i1);
        result.found = true;
        
        return result;
		
	}
	
	/** lValue의 인덱스를 startIndex(), ';'의 인덱스를 endIndex()로 하는 
	 * 할당문을 리턴한다.
	 * @param indexOfEquals : 등호의 인덱스*/  
	public void FindAssignStatement(HighArray_CodeString src, 
			FindAssignStatementParams result, FindVarUseParams lValue, 
			int indexOfEquals) {
		if (lValue.index()==1272) {
			int a;
			a=0;
			a++;
		}

		
		
		String operator = "=";
		int index = SkipBlank(src, true, 0, indexOfEquals-1);
		if (CompilerHelper.IsAssignOperator(src.getItem(index))) {
			operator = src.getItem(index) + "=";
		}
		
		if (lValue.name.equals("i") && operator.equals("+=")) {
			int a;
			a=0;
			a++;
		}
		
		int endIndexOfArr = this.isArrayInitializer(src, lValue);
		
		FindExpressionParams expression = null;
		expression = new FindExpressionParams(this, -1, -1);
		FindRValue(src, expression, lValue, indexOfEquals);
        if (expression.found==false) return;
        
        // 할당문의 시작 인덱스를 찾는다.
        int startIndex;
        if (lValue.parent==null) {
        	// A.B.C.D var = i; 에서 var.index
        	startIndex = this.getFullNameIndex5(src, true, lValue.index());
        }
        else {
        	// a.b.c.d = i에서 d.index-1
        	startIndex = this.getFullNameIndex5(src, true, lValue.index());
        }
        if (startIndex<0) {
        	errors.add(new Error(this, 0, 0, "The start of sentence reachs that of file."));
        	return;
        }
        
        
        result.startIndex = IndexForHighArray.indexRelative(result, src, startIndex);
       
        // 문장 구분자를 포함한 인덱스를 끝 인덱스로 한다.
        int endIndex = SkipBlank(src, false, expression.endIndex()+1, src.count-1);
        
        if (endIndex==246) {
        	int a;
        	a=0;
        	a++;
        }
        
        result.endIndex = IndexForHighArray.indexRelative(result, src, endIndex);
        result.lValue = lValue;
        result.indexOfEqual = IndexForHighArray.indexRelative(result, src, indexOfEquals);
        result.operator = operator;
        result.rValue = new FindFuncCallParam(this, expression);
        result.found = true;
        
        lValue.rValue = result.rValue;
		
	}
	
	/** lValue와 '=' 다음의 인덱스를 startIndex(), ';' 이전의 인덱스를 endIndex()로 하는 수식을 리턴한다.
	 * 배열초기화문일 경우 rValue의 시작과 끝 인덱스는 {, }의 인덱스가 된다.
	 * @param indexOfEquals : '=', '+=', '-=', '*=', '/=', '%='에서 =의 인덱스,
	 * */  
	public void FindRValue(HighArray_CodeString src, FindExpressionParams result, 
			FindVarUseParams lValue, int indexOfEquals) {
		if (lValue.index()==655) {
			int a;
			a=0;
			a++;
		}
		
		int i1 = indexOfEquals;
		int endIndex = src.count-1;
		
        
        // i1는 =의 인덱스
        result.startIndex = IndexForHighArray.indexRelative(result, src, SkipBlank(src, false, i1+1, endIndex));
        
        if (src.getItem(result.startIndex()).equals("{")) { 
        	// 배열초기화문일 경우 rValue의 시작과 끝 인덱스는 {, }의 인덱스가 된다.
        	result.endIndex = IndexForHighArray.indexRelative(result, src,  CompilerHelper.CheckParenthesis(src, "{", "}", result.startIndex(), src.count-1, false));
        	result.found = true;
        	return;
        }
        
        int i = result.startIndex();        
        while (i<endIndex) {
        	CodeString str = src.getItem(i);
        	if (CompilerHelper.IsBlank(str) || CompilerHelper.IsComment(str)) {
        		i++;
        	}
        	else if (IsIdentifier(str) || IsDefaultType(str)) {
		        i++;
        	}
        	else if (str.equals("[")) { // 배열원소
	        	int rightPair = 
		        		CompilerHelper.CheckParenthesis(src, "[", "]", i, endIndex, false);
	        	if (rightPair==-1) {
	        		i = endIndex;
	        		break;
	        	}
	        	i = rightPair;		        	
	        	i++;
		    }
        	else if (str.equals("(")) { // 함수호출, 타입캐스트
        		int rightPair = 
		        		CompilerHelper.CheckParenthesis(src, "(", ")", i, endIndex, false);
        		if (rightPair==-1) {
	        		i = endIndex;
	        		break;
	        	}
	        	i = rightPair;		        	
	        	i++;
        	}
	        else if (str.equals(",")) break;
	        else if (str.equals(";")) break;
	        else if (str.equals(")")) break; // for (i=0; i<3; i+=2) 에서 i+=2의 경우
	        else {
	        	i++;
	        }
        	
        }
        
        if (i<endIndex && 
        		(src.getItem(i).equals(",") || src.getItem(i).equals(";")  || src.getItem(i).equals(")")) ) {
	        result.found = true;
	        result.endIndex = IndexForHighArray.indexRelative(result, src, i-1);
        }
        else {
        	result.found = false;
        }
        
       
		
	}
	
	
	
	
	
	
	
	/** 외부소스파일을 링크하여 인터페이스만 얻을때 호출한다. 
	 * 이럴 경우 main함수 역할을 한다.
	 * loadClassFromSrc_onlyInterface()에서 호출한다.
	 * @param compiler : CompilerHelper의 loadClassFromSrc_onlyInterface()에서 
	 * compiler.start_onlyInterface(...) 이렇게 호출이 되므로 
	 * start_onlyInterface()내부의 this나 this가 없는 
	 * Compiler클래스의 멤버 사용은 compiler(호출오브젝트)의 멤버사용이므로 없어도 되는 것이다.
	 * 
	 * addsNewly가 true일 경우 즉 getSourceFilePath()에서 소스파일을 찾지 못해서
	 * getSourceFilePathAddingComGsoftCommon()으로 소스파일을 찾게 된 경우
	 * fullName에서 com.gsoft.common을 제거한 이름을 새로운 fullName으로 한다.
	 * 즉 새로이 제공된 소스파일에 있는 클래스로 클래스파일을 읽기 실패한 클래스를 대체하게 된다.
	 * fullName이 com.gsoft.common.java.lang.String일 경우 새로운 fullName은 java.lang.String이 된다.
	 *  @param fullNameIncludingTemplateExceptArray : 클래스가 템플릿 클래스일 경우 템플릿 타입 이름을 포함하고 배열은 포함하지 않은 클래스 풀 이름이다.
	 *  */
	public void start_onlyInterface(Compiler compiler, String input, Language lang, String fullNameIncludingTemplateExceptArray, String filename, boolean addsNewly) {
		//errors.reset();
		
		compiler.filename = filename;
		setLanguage(lang);
		
		String fullname = fullNameIncludingTemplateExceptArray;
		
		// 클래스가 템플릿 클래스일 경우 템플릿 타입 이름을 포함하고 배열은 포함하지 않은 클래스 풀 이름이다.
		fullname = CompilerHelper.getArrayElementType(fullname);
		
			
		
				
		try {
			
			
			strInput = new CodeString(input, textColor);
			
			
			mBuffer = new StringTokenizer().ConvertToStringArray2(strInput, 10000, language);
			mBuffer.name = filename;
			
			try {
			findAnnotation(mBuffer);
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
		
		
			packageName = "";
			mlistOfClass.reset2();
			
			RegisterPackageName(mBuffer);
			
			RegisterImportedClasses(mBuffer);
			
			loadImportStar();
			//FindAllClassesAndItsMembers2(mBuffer, 0, mBuffer.count-1, mlistOfClass, lang);
			
			try {
				mlistOfAllVarUsesHashed = new Hashtable2(mBuffer, 50, 20);
					
				FindAllClassesAndItsMembers2_sub(0, mBuffer.count-1, mlistOfClass, ModeAllOrUpdate.All);
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
			
			for (int i=0; i<mlistOfAllDefinedClasses.count; i++) {
				FindClassParams classParams = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
				classParams.loadWayOfFindClassParams = LoadWayOfFindClassParams.Start_OnlyInterface; 
			}
			
			
			int i, j;		
			try {
			// 외부라이브러리처럼 클래스의 name을 fullname으로 정해준다.
			// mlistOfAllClasses이 static이므로 mlistOfAllDefinedClasses안의 클래스들을 대상으로 한다.
			// 즉, 여러 파일을 load할 경우 src가 달라지는 문제가 발생할 수 있다.
			for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
				FindClassParams c = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
				if (c.name==null) {
					String fullName;
					if (c.isEventHandlerClass==false) {
						String fullNameExceptPackageName1 = getFullNameExceptPackageName(mBuffer, c);
						String fullNameExceptPackageName2 = fullNameExceptPackageName1.substring(0, fullNameExceptPackageName1.length()-1);
						fullName = packageName + "." + fullNameExceptPackageName2;
						// addsNewly가 true일 경우 즉 getSourceFilePath()에서 소스파일을 찾지 못해서
						// getSourceFilePathAddingComGsoftCommon()으로 소스파일을 찾게 된 경우
						// fullName에서 com.gsoft.common을 제거한 이름을 새로운 fullName으로 한다.
						// 즉 새로이 제공된 소스파일에 있는 클래스로 클래스파일을 읽기 실패한 클래스를
						// 대체하게 된다.
						// fullName이 com.gsoft.common.java.lang.String일 경우 
						// 새로운 fullName은 java.lang.String이 된다.
						if (addsNewly) {
							int len = new String(FindClassParams.prefixInCaseOfAddsNewly).length();
							int founded = fullName.indexOf(FindClassParams.prefixInCaseOfAddsNewly);
							if (founded!=-1) {
								fullName = fullName.substring(len, fullName.length());
								c.addsNewly = true;
							}
						}
					}
					else {  // event Handler 클래스
						fullName = getFullNameType(this, c.startIndexOfEventHandlerName(), c.endIndexOfEventHandlerName()) + "_EventHandler";
					}
					c.name = fullName;
					CompilerHelper.makeNoneStaticDefaultConstructorIfConstructorNotExist(compiler, c);
				}
				
				if (compiler.getShortName(c.name).equals("ArrayListCodeString")) {
					int a;
					a=0;
					a++;
				}
				// 여기서 mlistOfAllClasses에 넣어준다. 
				// import한 기존 클래스가 아니라 새로 읽어들인 클래스로 바꿔준다.
				boolean found = false;
				for (j=0; j<mlistOfAllClasses.count; j++) {
					FindClassParams classParams = (FindClassParams) mlistOfAllClasses.getItem(j);
					
					if (classParams.name.equals(c.name)) {
						if (classParams.loadWayOfFindClassParams==null ||
							classParams.loadWayOfFindClassParams==LoadWayOfFindClassParams.None ||
							classParams.loadWayOfFindClassParams==LoadWayOfFindClassParams.ByteCode) {
							mlistOfAllClasses.list[j] = c;
							mlistOfAllClassesHashed.replace(c);
							found = true;
							//break;
						}
					}
				}//for (j=0; j<mlistOfAllClasses.count; j++) {
				
				if (!found) {				
					mlistOfAllClasses.add(c);
					mlistOfAllClassesHashed.input(c);
				}
			}//for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
			
			// 같은 파일에 정의된 클래스들의 이름이 fullname으로 확실하게 정한 이후에 상속한다.
			for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
				FindClassParams c = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
				// 파일에서 정의하는 클래스만 상속, 외부 클래스들은 loadClass에서 상속한다. 
				FindMembersInherited(c);
			}
			
			//defineTypeName_OnlyInterface(compiler, compiler.mBuffer);
			ArrayListString result = new ArrayListString(50);
			result.resizeInc = 200;
			this.FindClassesFromTypeDecls(compiler, result);
			
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
	}
	
	
	
	/** 모든 메모리 자원들을 해제한다.*/
	public void destroy() {
		if (this.mBuffer!=null) {
			this.mBuffer.destroy();
			this.mBuffer = null;
		}
		if (this.mlistOfAllArrayIntializers!=null) {
			this.mlistOfAllArrayIntializers.reset();
			this.mlistOfAllArrayIntializers = null;
		}
		if (this.mlistOfAllAssignStatements!=null) {
			this.mlistOfAllAssignStatements.reset();
			this.mlistOfAllAssignStatements = null;
		}
		if (this.mlistOfAssignStatements!=null) {
			this.mlistOfAssignStatements.reset();
			this.mlistOfAssignStatements = null;
		}
		/*if (this.mlistOfAllClassesThatCanUseShortName!=null) {
			this.mlistOfAllClassesThatCanUseShortName.reset();
			this.mlistOfAllClassesThatCanUseShortName = null;
		}*/
		if (this.mlistOfAllConstructor!=null) {
			this.mlistOfAllConstructor.reset();
			this.mlistOfAllConstructor = null;
		}
		if (this.mlistOfAllControlBlocks!=null) {
			this.mlistOfAllControlBlocks.reset();
			this.mlistOfAllControlBlocks = null;
		}
		if (this.mlistOfAllDefinedClasses!=null) {
			this.mlistOfAllDefinedClasses.reset();
			this.mlistOfAllDefinedClasses = null;
		}
		if (this.mlistOfAllFunctions!=null) {
			this.mlistOfAllFunctions.reset();
			this.mlistOfAllFunctions = null;
		}
		if (this.mlistOfAllLocalVarDeclarations!=null) {
			this.mlistOfAllLocalVarDeclarations.destroy();
			this.mlistOfAllLocalVarDeclarations = null;
		}
		if (this.mlistOfAllMemberVarDeclarations!=null) {
			this.mlistOfAllMemberVarDeclarations.destroy();
			this.mlistOfAllMemberVarDeclarations = null;
		}
		if (this.mlistOfAllTemplates!=null) {
			this.mlistOfAllTemplates.reset();
			this.mlistOfAllTemplates = null;
		}
		if (this.mlistOfAllTypeCasts!=null) {
			this.mlistOfAllTypeCasts.reset();
			this.mlistOfAllTypeCasts = null;
		}
		if (this.mlistOfAllVarUses!=null) {
			this.mlistOfAllVarUses.destroy();
			this.mlistOfAllVarUses = null;
		}
		if (this.mlistOfAllVarUsesHashed!=null) {
			this.mlistOfAllVarUsesHashed.reset();
			this.mlistOfAllVarUsesHashed = null;
		}
		if (this.mlistOfAnnotations!=null) {
			this.mlistOfAnnotations.reset();
			this.mlistOfAnnotations = null;
		}
		if (this.mlistOfBlocks!=null) {
			this.mlistOfBlocks.reset();
			this.mlistOfBlocks = null;
		}
		if (this.mlistOfClass!=null) {
			this.mlistOfClass.reset();
			this.mlistOfClass = null;
		}
		
		if (this.mListOfFileOfSamePackage!=null) {
			int i;
			for (i=0; i<mListOfFileOfSamePackage.length; i++) {
				mListOfFileOfSamePackage[i] = null;
			}
			this.mListOfFileOfSamePackage = null;
		}
		if (this.mlistOfFindStatementParams!=null) {
			this.mlistOfFindStatementParams.reset();
			this.mlistOfFindStatementParams = null;
		}
		if (this.mlistOfImportedClasses!=null) {
			this.mlistOfImportedClasses.destroy();
			this.mlistOfImportedClasses = null;
		}
		if (this.mlistOfImportedClassesStar!=null) {
			this.mlistOfImportedClassesStar.destroy();
			this.mlistOfImportedClassesStar = null;
		}
		if (this.mlistOfImportStatements!=null) {
			this.mlistOfImportStatements.reset();
			this.mlistOfImportStatements = null;
		}
		/*if (this.mlistOfIndependentFuncCall!=null) {
			this.mlistOfIndependentFuncCall.reset();
			this.mlistOfIndependentFuncCall = null;
		}*/
		if (this.mlistOfPackages!=null) {
			int i;
			for (i=0; i<mlistOfPackages.length; i++) {
				mlistOfPackages[i].destroy();
				mlistOfPackages[i] = null;
			}
			this.mlistOfPackages = null;
		}
		if (this.mlistOfPackages2!=null) {
			int i;
			for (i=0; i<mlistOfPackages2.length; i++) {
				mlistOfPackages2[i].destroy();
				mlistOfPackages2[i] = null;
			}
			this.mlistOfPackages2 = null;
		}
		if (this.mlistOfPackageStatements!=null) {
			this.mlistOfPackageStatements.reset();
			this.mlistOfPackageStatements = null;
		}
		if (this.mlistOfSpecialStatement!=null) {
			this.mlistOfSpecialStatement.reset();
			this.mlistOfSpecialStatement = null;
		}
		this.AccessModifiers = null;
		this.filename = null;
		this.Keywords = null;
		this.language = null;
		this.Types = null;
		// TypesOfDefaultLibraryOfJava은 static 이고
		// setLanguage()에서 loadJavaLangPackage()에서 읽어들인다.
		
		if (this.TypesOfImportStarOfJava!=null) {
			TypesOfImportStarOfJava.reset();
			TypesOfImportStarOfJava = null;
		}
		if (this.mSamePackageClasses!=null) {
			int i;
			for (i=0; i<this.mSamePackageClasses.length; i++) {
				this.mSamePackageClasses[i] = null;
			}
			mSamePackageClasses = null;
		}
		this.packageName = null;
		
		if (this.mLoader!=null) {
			this.mLoader.destroy();
			this.mLoader = null;
		}
		
	} // reset();
	
	
	/** CommonGUI_SettingsDialog.settings.usesClassCache 이 false 이면 
	 * 클래스 캐시와 캐시에 들어있는 FindClassParams와 Compiler 등을 제거한다.*/
	public static void resetClassCache() {
		if (CommonGUI_SettingsDialog.settings.usesClassCache==false) {			
			mlistOfAllClasses.reset();
			mlistOfAllClassesHashed.reset();
			System.gc();
		}		
	}
	
	/** Compiler의 main함수 역할을 한다.*/
	public void start2(String input, Language lang, int backColor, String filename) {
		
		int k1 = 1^2 + 2; // k = 5;
		int k2 = ~k1;  // k2 = -6
		// 00000101 11111010(~의 결과:1의보수) 000000110(2의보수:6) 따라서 -6
		// 11111011(2의보수:-5) 000000101(2의보수:5) 따라서 5 
		// 2의보수를 2의보수를 취하면 원래 값이 나온다.
		int k3 = 2^2; // k3 = 0
		int k4 = 3^1; // k4 = 2
		int k5 = 4^2; // k5 = 6
		errors.reset2();
				
		this.filename = filename;
		setBackColor(backColor);
		
		setLanguage(lang);
		
		CommonGUI.textViewLogBird.initialize();
		
		if (CommonGUI_SettingsDialog.settings.usesClassCache==false) {
			Compiler.mlistOfLoadClassFromSrc_onlyInterface.destroy();
			Compiler.mlistOfLoadClassFromSrc_onlyInterface_failed.destroy();
			PathClassLoader.listOfClassesToFailLoading.destroy();
			resetClassCache();
		}
		
		
		try {
			createMenuClassList();
			createTextViewExpressionTree();

			
			packageName = "";
			mlistOfClass.reset2();
			
			if (lang==Language.Html) {
				strInput = new CodeString(input, textColor);
				mBuffer = new StringTokenizer().ConvertToStringArray2(strInput, 10000, lang);
				changeKeywordAndConstantColor(mBuffer);
				menuClassList.setButtons(new Button[0]);
				strOutput = strInput;
				return;
			}
			else if (lang==Language.Class) { // 클래스 파일 로드
				mLoader = new PathClassLoader(filename, null, null, true);
				if (mLoader!=null && mLoader.classParams!=null) {
					strOutput = mLoader.getText();
					mBuffer = mLoader.mBuffer;
					/** listOfClass가 1보다 큰 경우(즉 파일하나에 중첩되지 않은 클래스가 여러 개 있으면, 
					 * 루트라 이름붙인다.) 루트에서는  parentClassParams=null, curClassParams=null이다. 
					 * 1인 경우는 parentClassParams=첫번째 클래스, curClassParams=첫번째클래스이다.*/
					Button[] list;
					menuClassList.setContainerClassAndSrc(mBuffer, 
							mLoader.classParams);
					mlistOfClass.add(mLoader.classParams);
					//list = menuClassList.getMenuListButtons(this, mBuffer, mLoader.classParams);
					list = menuClassList.getMenuListButtons(mBuffer, mlistOfClass);
					menuClassList.setButtons(list);
					return;
				}
			}
			
			if (CommonGUI_SettingsDialog.settings.loadsClassesFrequentlyUsedAdvancely) {
				CompilerHelper.startThreadReadingJavaClassesUsingWell();
			}
			
		
			createMenuProblemList_EditText();
			
			
			
			CompilerHelper.showMessage(true, "Loading class files and analyzing....");
						
						
			
			strInput = new CodeString(input, textColor);
			
			StringTokenizer tokenizer = new StringTokenizer();
			mBuffer = tokenizer.ConvertToStringArray2(strInput, 10000, language);			
			mBuffer.name = filename;
			this.mlistOfComments = tokenizer.mlistOfComments;
			
			try {
				findAnnotation(mBuffer);
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
		
			
        
			
			FindAllClassesAndItsMembers2(mBuffer, 0, mBuffer.count-1, mlistOfClass, lang, ModeAllOrUpdate.All);
			
			
			strOutput = strInput;
			
			
			
							
			/** listOfClass가 1보다 큰 경우(즉 파일하나에 중첩되지 않은 클래스가 여러 개 있으면, 
			 * 루트라 이름붙인다.) 루트에서는  parentClassParams=null, curClassParams=null이다. 
			 * 1인 경우는 parentClassParams=첫번째 클래스, curClassParams=첫번째클래스이다.*/
			Button[] list;
			/*if (hasPairError) {
				menuClassList.setContainerClassAndSrc(mBuffer, null);
				list = menuClassList.getMenuListButtons(this, mBuffer, (FindClassParams)null);
			}
			else {*/
				/*if (mlistOfClass.count>1) {
					menuClassList.setContainerClassAndSrc(mBuffer, null);
					list = menuClassList.getMenuListButtons(mBuffer, mlistOfClass);					
				}
				else {
					menuClassList.setContainerClassAndSrc(mBuffer, 
							(FindClassParams)mlistOfClass.getItem(0));
					list = menuClassList.getMenuListButtons(this, mBuffer, 
							(FindClassParams)mlistOfClass.getItem(0));					
				}*/
			menuClassList.setContainerClassAndSrc(mBuffer, null);
			list = menuClassList.getMenuListButtons(mBuffer, mlistOfClass);
			//}
			menuClassList.setButtons(list);				
			
			
			EditText[] problemList = menuProblemList_EditText.getMenuListEditTexts(mBuffer, errors);
			menuProblemList_EditText.setEditTexts(problemList);
			
			codeGen = new ByteCodeGenerator(this, mlistOfClass);
			codeGen.makeConstantTable();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block				
			//showMessage(true, e.toString());
			//return null;
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
	}
	
	
	
	public void InputVarUsesToHashtable(HighArray_CodeString src, 
			ArrayListIReset input, ArrayListIReset result) 
	{
		
	}
	
	/** input(FindStatementParams[])를 입력, result(FindStatementParams[])를 출력으로
	 * 하여 문장들의 startIndex()를 기준으로 문장들을 정렬한다.
	 */
	public void SortByIndex(ArrayListIReset input, ArrayListIReset result) {
		if (input.count<=0) return;
		int i, j;
		int indexMin=0;
		FindStatementParams minStatement = (FindStatementParams)input.getItem(0);
		int min = minStatement.startIndex();
		FindStatementParams statement = null;
		
		for (j=0; j<input.count; j++) {
			statement = (FindStatementParams)input.getItem(j);
			result.add(statement);			
		}
		
		for (j=0; j<result.count; j++) {
			// 가정한 최소값
			minStatement = (FindStatementParams)result.getItem(j);
			min = minStatement.startIndex();
			indexMin = j;
			
			for (i=j; i<result.count; i++) { // 구간에서 실제 최소값 찾기
				statement = (FindStatementParams)result.getItem(i);
				int cur = statement.startIndex();
				if (min > cur) {
					minStatement = statement;
					min = cur;
					indexMin = i;
				}
			}
			
			// 가정한 최소값과 실제 최소값의 자리를 바꾸기
			if (j!=indexMin) {
				IReset temp = result.list[j];
				result.list[j] = result.list[indexMin];
				result.list[indexMin] = temp;
				
			}
			
		}
		
		
	}
	
	public void SortByName(HighArray_CodeString src, ArrayListIReset input, ArrayListIReset result) {
		int i, j;
		int indexMin=0;
		FindVarUseParams minVarUse = (FindVarUseParams)input.getItem(0);
		String min = src.getItem(minVarUse.index()).str;
		FindVarUseParams varUse = null;
		
		for (j=0; j<input.count; j++) {
			varUse = (FindVarUseParams)input.getItem(j);
			result.add(varUse);			
		}
		
		for (j=0; j<result.count; j++) {
			// 가정한 최소값
			minVarUse = (FindVarUseParams)result.getItem(j);
			min = src.getItem(minVarUse.index()).str;
			indexMin = j;
			
			for (i=j; i<result.count; i++) { // 구간에서 실제 최소값 찾기
				varUse = (FindVarUseParams)result.getItem(i);
				String str = src.getItem(varUse.index()).str;
				if (min.compareTo(str)>0) {
					minVarUse = varUse;
					min = str;
					indexMin = i;
				}
			}
			
			// 가정한 최소값과 실제 최소값의 자리를 바꾸기
			if (j!=indexMin) {
				IReset temp = result.list[j];
				result.list[j] = result.list[indexMin];
				result.list[indexMin] = temp;
				
			}
			
		}
		
		// 테스트 코드
		for (j=0; j<result.count; j++) {
			varUse = (FindVarUseParams)result.getItem(j);
			String str = src.getItem(varUse.index()).str;
			if (str.equals("VScrollBarWidthScale")) {
				int a;
				a=0;
				a++;
			}
		}
	}
	
	public ArrayList FindByNameInHashtable(HighArray_CodeString src, Hashtable2 hashtable, String name) {
		if (name.equals("textArray")) {
			int a;
			a=0;
			a++;
		}
		if (hashtable==null) return null;
		ArrayList r = hashtable.getData(name);
		return r;
		
	}
	
	public void FindByName(HighArray_CodeString src, ArrayListIReset listOfVarUses, String name, ArrayListIReset result) {
		int i;
		int n = listOfVarUses.count;
		int l = 0;
		int h = listOfVarUses.count-1;
		i = l + n/2;
		//CodeString str = src.getItem(i)
		boolean found = false;
		if (name.equals("VScrollBarWidthScale")) {
			int a;
			a=0;
			a++;
		}
		int r;
		while (l<=h) {
			FindVarUseParams varUse = (FindVarUseParams) listOfVarUses.getItem(i);
			String str = src.getItem(varUse.index()).str;
			r = name.compareTo(str);
			if (r==0) {
				//result.add(varUse);
				found = true;
				break;
			}
			else if (r<0) {
				h = i-1;
			}
			else {
				l = i+1;
			}
			i = l + (h-l+1)/2;
				
		}
		
		int k;		
		if (found) {
			int start=i, end=i;
			for (k=i; k>=0; k--) {
				FindVarUseParams varUse = (FindVarUseParams) listOfVarUses.getItem(k);
				String str = src.getItem(varUse.index()).str;
				if (str.equals(name)==false) {
					start = k+1;
					break;
				}
			}
			
			for (k=i; k<listOfVarUses.count; k++) {
				FindVarUseParams varUse = (FindVarUseParams) listOfVarUses.getItem(k);
				String str = src.getItem(varUse.index()).str;
				if (str.equals(name)==false) {
					end = k-1;
					break;
				}
			}
			
			for (k=start; k<=end; k++) {
				FindVarUseParams varUse = (FindVarUseParams) listOfVarUses.getItem(k);
				result.add(varUse);
			}
		}
		
	}
	
	/** statement가 가짜 try-catch블록의 try이면 true를 리턴한다.*/
	boolean isTry_CatchShield(FindStatementParams statement) {
		if (statement instanceof FindSpecialBlockParams) {
			FindSpecialBlockParams block = (FindSpecialBlockParams) statement;
			if (block.nameIndex==null && 
				block.specialBlockType==FindSpecialBlockParams.SpecialBlockType_try) {
				// 가짜 try-catch블록
				return block.isFake;
			}
		}
		return false;
		
	}
	
	
	/** synchronized안이나 메서드 안에서 가짜 try-catch블록을 만든다.
	가짜 try-catch블록이란 try-catch가 없는 메서드내에서 예외를 호출함수로 던져주거나 
	try-catch가 없는 synchronized블록에서 예외가 발생할 경우 모니터를 해제하기 위한 것이다.
	현재는 try-catch가 있더라도 가짜 try-catch블록을 만든다.
	(그래도 문제가 안되기 때문에)
	이 경우 Try와 Catch블록은 FindControlBlockParams.nameIndex==null이 되고 
	 * throw문은 FindSpecialStatementParams.kewordIndex==null이 된다.
	 * 이 메서드의 내용을 참조한다.*/
	void putTryCatchShieldToSynchronizedOrFunction(Block synchronizedOrFunction) {
		
		
		if (synchronizedOrFunction instanceof FindSpecialBlockParams) {
			FindSpecialBlockParams specialBlock = (FindSpecialBlockParams) synchronizedOrFunction;
			if (specialBlock.nameIndex()==201) {
				int a;
				a=0;
				a++;
			}
		}
		else if (synchronizedOrFunction instanceof FindFunctionParams) {
			FindFunctionParams func = (FindFunctionParams) synchronizedOrFunction;
			if (func.functionNameIndex()==28) {
				int a;
				a=0;
				a++;
			}
		}
		ArrayListIReset originalControlBlocks = synchronizedOrFunction.listOfControlBlocks;
		ArrayListIReset originalSpecialBlocks = synchronizedOrFunction.listOfSpecialBlocks;
		ArrayListIReset originalStatements = synchronizedOrFunction.listOfStatements;
		
		FindSpecialBlockParams tryBlock = new FindSpecialBlockParams(this, 
				FindSpecialBlockParams.SpecialBlockType_try, 
				synchronizedOrFunction.startIndex()+1, synchronizedOrFunction.endIndex()-1);
		tryBlock.parent = synchronizedOrFunction;
		tryBlock.isFake = true;
		
		int i;
		for (i=0; i<originalControlBlocks.count; i++) {
			FindStatementParams statement = (FindStatementParams) originalControlBlocks.getItem(i);
			statement.parent = tryBlock;
			tryBlock.listOfControlBlocks.add(statement);
		}
		originalControlBlocks.count = 0;
		
		for (i=0; i<originalSpecialBlocks.count; i++) {
			FindStatementParams statement = (FindStatementParams) originalSpecialBlocks.getItem(i);
			statement.parent = tryBlock;
			tryBlock.listOfSpecialBlocks.add(statement);
		}
		originalSpecialBlocks.count = 0;
		
		for (i=0; i<originalStatements.count; i++) {
			FindStatementParams statement = (FindStatementParams) originalStatements.getItem(i);
			statement.parent = tryBlock;
			tryBlock.listOfStatements.add(originalStatements.getItem(i));
		}
		originalStatements.count = 0;
		
		
		FindSpecialBlockParams catchBlock = new FindSpecialBlockParams(this, 
				FindSpecialBlockParams.SpecialBlockType_catch, 
				synchronizedOrFunction.startIndex()+1, synchronizedOrFunction.endIndex()-1);
		catchBlock.parent = synchronizedOrFunction;
		catchBlock.isFake = true;
		
		FindVarParams var = new FindVarParams(this, -1, -1);
		var.typeName = "java.lang.Exception";
		var.fieldName = "e";
		var.isMemberOrLocal = false;
		var.isFake = true;
		catchBlock.listOfStatementsInParenthesis.add(var);
		catchBlock.listOfVariableParams.add(var);
		
		FindSpecialStatementParams throwStatement = new FindSpecialStatementParams(this, 
				catchBlock.startIndex()+1, catchBlock.endIndex()-1, -1);
		throwStatement.parent = catchBlock;
		throwStatement.isFake = true;
		catchBlock.listOfStatements.add(throwStatement);
		
		Block function = this.getParent(catchBlock);
		function.listOfVariableParams.add(var);
		
		
		tryBlock.indexInListOfControlBlocksOfParent = 0;
		synchronizedOrFunction.listOfControlBlocks.add(tryBlock);
		synchronizedOrFunction.listOfStatements.add(tryBlock);
		
		catchBlock.indexInListOfControlBlocksOfParent = 1;
		synchronizedOrFunction.listOfControlBlocks.add(catchBlock);
		synchronizedOrFunction.listOfStatements.add(catchBlock);
		
		 
	}
	
	
	
		
	/** 클래스, 함수, 제어구조 등의 블록구조를 스택을 써서 확인한다.
	 * start2()와 start_onlyInterface()에서 호출된다.
	 * @param compiler : CompilerHelper의 loadClassFromSrc_onlyInterface()에서 
	 * compiler.start_onlyInterface(...) 이렇게 호출이 되고  start_onlyInterface()에서
	 * FindAllClassesAndItsMembers2_sub(...)호출을 하면
	 * FindAllClassesAndItsMembers2_sub()내부의 this나 this가 없는 
	 * Compiler클래스의 멤버 사용은 compiler(호출오브젝트)의 멤버사용이므로 없어도 되는 것이다.
	 * start2()에서 호출이 되어도 마찬가지이다.
	 * @param result : 최상위 클래스 리스트
	 * @param resultForListOfAllClasses : 모든 클래스 리스트
	 */
	public void FindAllClassesAndItsMembers2_sub(int startIndex, int endIndex, 
			ArrayListIReset result, ModeAllOrUpdate modeAllOrUpdate) {
		HighArray_CodeString src = this.mBuffer;
		int i;
		Stack<Block> stack = new Stack<Block>();
		Block top = null;
		OldTypeIndex oldTypeIndex = null;
		
		
		// 문장 찾고 넣기에서 대입문
		int startIndexOfAssign=-1, endIndexOfAssign=-1;	
		int startIndexOfIndependentFuncCall=-1, endIndexOfIndependentFuncCall=-1;
		boolean isAssignOrIndependentFuncCall = false;
		
				
		for (i=startIndex; i<=endIndex; ) {
			try{
			CodeString str = src.getItem(i);
			Block currentBlock = stack.Get();
			
			if (i==1535) {
				int a;
				a=0;
				a++;
			}
			if (i==329 || i==330) {
				int a;
				a=0;
				a++;
			}
			
			if (CompilerHelper.IsBlank(str) || CompilerHelper.IsComment(str))  {
				i++;
				continue;
			}
			// 배열 초기화문  char[] a = {'a', 'b'};
			else if (str.equals("=")) {
				int prevIndex = SkipBlank(src, true, 0, i-1);
				if (prevIndex==-1) {
					i++;
					continue;
				}
				int nameIndex;
				CodeString varName = src.getItem(prevIndex);
				if (this.IsIdentifier(varName)==false) {
					i++;
					continue;
				}
				nameIndex = prevIndex;
				prevIndex = SkipBlank(src, true, 0, prevIndex-1);
				if (prevIndex==-1) {
					i++;
					continue;
				}
				CodeString rightPair = src.getItem(prevIndex);
				if (rightPair.equals("]")==false) {
					i++;
					continue;
				}
				int nextIndex = SkipBlank(src, false, i+1, src.count-1);
				if (nextIndex==src.count) {
					i++;
					continue;
				}
				CodeString leftPair = src.getItem(nextIndex);
				if (leftPair.equals("{")) {
					int indexRightPair = CompilerHelper.CheckParenthesis(src, "{", "}", nextIndex, src.count-1, false);
					if (indexRightPair!=-1) {
						FindArrayInitializerParams arr = new FindArrayInitializerParams(this, nameIndex, nextIndex, indexRightPair);
						FindBlockParams blockParams = new FindBlockParams(this, nextIndex, indexRightPair);
						arr.findBlockParams = blockParams;
						//mlistOfAllArrayIntializers.add(arr)// 맨처음 배열 초기화문을 등록한다.
						putArrayIntializer(arr, modeAllOrUpdate);// 맨처음 배열 초기화문을 등록한다.
						Block parentBlock = stack.Get();
						arr.parent = parentBlock; // parent는 클래스나 함수, 제어블록 등이 될수 있다.
						stack.Push(arr);
						
						// 배열초기화를 lValue에 연결한다.
						FindVarUseParams varUseLValue = 
								(FindVarUseParams) mlistOfAllVarUses.getItem(mlistOfAllVarUses.getCount()-1);
						varUseLValue.arrayInitializer = arr;
						
						// 배열초기화를 대입문에 연결한다.
						FindAssignStatementParams assignStatement = 
								(FindAssignStatementParams) mlistOfAllAssignStatements.getItem(mlistOfAllAssignStatements.count-1);
						assignStatement.arrayInitializer = arr;
						
						// nextIndex는 { 의 인덱스
						i = nextIndex+1;
						continue;
					}
					else {
						i++;
						continue;
					}
				}
				else {
					i++;
					continue;
				}
			}
			// 배열 초기화문  char[][] a = {{'a', 'a'}, {'b', 'b'}}; 에서 처음 {}안의 중첩된 {'a','a'}를 처리한다.
			else if (str.equals("{") && currentBlock instanceof FindArrayInitializerParams) {
				int indexRightPair = CompilerHelper.CheckParenthesis(src, "{", "}", i, src.count-1, false);
				if (indexRightPair!=-1) {
					FindArrayInitializerParams arr = new FindArrayInitializerParams(this, -1, i, indexRightPair);
					FindBlockParams blockParams = new FindBlockParams(this, i, indexRightPair);
					arr.findBlockParams = blockParams;
					// 중첩된 배열초기화는 mlistOfAllArrayIntializers에 등록을 하지 않는다.
					//mlistOfAllArrayIntializers.add(arr);
					
					Block parentBlock = stack.Get();
					arr.parent = parentBlock; // parent는 FindArrayInitializerParams이다.
					stack.Push(arr);
					
					// 상위 배열초기화문에 등록한다.
					FindArrayInitializerParams parentArr = (FindArrayInitializerParams) parentBlock;
					//parentArr.listOfFindArrayInitializerParams.add(arr)
					this.putArrayInitializerToParent(arr, parentArr, modeAllOrUpdate);
					i = i+1;
					continue;
				}
				else {
					i++;
					continue;
				}
			}
			else if (str.equals("new")) {
				//i++;
				//continue;
				Constructor c = new Constructor(this);
				c.indexOfNew = IndexForHighArray.indexRelative(c, src, i);
				if (i==83450){
					int a;
					a=0;
					a++;
				}
				
				int startIndexNew = i+1;
				int indexTemp = this.SkipBlank(src, false, startIndexNew, src.count-1);
				int endIndexNew = getFullNameIndex2(src, indexTemp);
				int leftPairIndex = SkipBlank(src, false, endIndexNew+1, src.count-1);
				
				Template t = null;
				// stack = new Stack<Block>();에서 템플릿을 등록하고 올바른 endIndexNew을 정한다.
				if (src.getItem(leftPairIndex).equals("<")) {
					int rightPairIndex = CompilerHelper.CheckParenthesis(src, "<", ">", leftPairIndex, src.count-1, false);
					if (rightPairIndex!=-1) {
						t = isTemplate(src, rightPairIndex);
						if (t!=null) {
							c.template = t;
							c.endIndexExceptPair = IndexForHighArray.indexRelative(c, src, t.indexRightPair());
							//this.mlistOfAllTemplates.add(t);
							this.putTempate(t, modeAllOrUpdate);
							int leftPairIndexOfFuncCall = SkipBlank(src, false, rightPairIndex+1, src.count-1);
							if (src.getItem(leftPairIndexOfFuncCall).equals("(")) {
								int rightPairIndexOfFuncCall = CompilerHelper.CheckParenthesis(src, "(", ")", leftPairIndexOfFuncCall, src.count-1, false);
								if (rightPairIndexOfFuncCall!=-1) {
									endIndexNew = rightPairIndexOfFuncCall;
								}
							}
						}
					}
				}//if (src.getItem(leftPairIndex).equals("<")) {
				
				
				c.startIndex = IndexForHighArray.indexRelative(c, src, startIndexNew);
				c.endIndex = IndexForHighArray.indexRelative(c, src, endIndexNew);
				String fullname = getFullName(src, c.startIndex(), c.endIndex()).str;
				int dimension = CompilerHelper.getArrayDimension(this, fullname);
				c.dimension = dimension;
				
				int startIndexNew2 = i+1;				
				c.startIndexExceptPair = IndexForHighArray.indexRelative(c, src, startIndexNew2);
				
				if (t==null) {
					int endIndexNew2 = i+1;
					endIndexNew2 = this.SkipBlank(src, false, endIndexNew2, src.count-1);
					endIndexNew2 = getFullNameIndex0(src, false, endIndexNew2);
					c.endIndexExceptPair = IndexForHighArray.indexRelative(c, src, endIndexNew2);
				}
				
				
				//mlistOfAllConstructor.add(c)
				this.putConstructor(c, modeAllOrUpdate);
				
				if (i==325) {
					int a;
					a=0;
					a++;
				}
				
				int indexOfSeparator = SkipBlank(src, false, endIndexNew, src.count-1);
				CodeString separator = src.getItem(indexOfSeparator);
				if (separator.equals("{")) {//setOnTouchLisener(new View.OnTouchListener() {});
					// 이벤트 핸들러 클래스를 찾아야 한다.
					FindClassParams eventClass = new FindClassParams(this);
					String handlerName = fullname + "(..){..}";
					FindEventHandlerClass(src, eventClass, c.startIndexExceptPair(), c.endIndexExceptPair(), 
							indexOfSeparator, handlerName);
					if (eventClass.findBlockParams!=null && eventClass.findBlockParams.startIndex()!=-1) {
												
						//mlistOfAllDefinedClasses.add(eventClass)
						putFindClassParams(eventClass, modeAllOrUpdate);
						eventClass.allocateListOfVarUses(modeAllOrUpdate);
						
						FindClassParams parentClass=null;
						Block curBlock = stack.Get();
						curBlock = getParent(curBlock);
						if (curBlock instanceof FindFunctionParams) {
							FindFunctionParams func = (FindFunctionParams) curBlock;
							parentClass = (FindClassParams) func.parent;
						}
						else if (curBlock instanceof FindClassParams) {
							parentClass = (FindClassParams) curBlock;
						}
						
						if (parentClass!=null) {
							//parentClass.childClasses.add(eventClass)
							putClassEnumInterfaceToParent(eventClass, parentClass.childClasses, modeAllOrUpdate);
							eventClass.parent = parentClass;
						}
						
						stack.Push(eventClass);
						
						//i = eventClass.findBlockParams.startIndex() + 1;
						i++;
						continue;
					}				
					else {
						i++;
						continue;
					}
				}
				else { //일반적인 constructor
					i++;
					continue;
				}
			}
			else if (str.equals("enum")) {
				FindClassParams parentClass=null;
				top = stack.Get();
				if (top instanceof FindClassParams) {
					parentClass = (FindClassParams)top;
				}
				FindClassParams enumParams = new FindClassParams(this);
				FindClass(src, enumParams, i, src.count-1, true);
				if (enumParams.findBlockParams!=null && enumParams.findBlockParams.startIndex()!=-1) {					
					/*if (stack.len==0) { // 최상위 클래스만 등록
						result.add(enumParams)
					}*/
					putClassEnumInterfaceToResult(enumParams, result, stack, modeAllOrUpdate);
					stack.Push(enumParams);
					//mlistOfAllDefinedClasses.add(enumParams);
					this.putFindClassParams(enumParams, modeAllOrUpdate);
					
					if (parentClass!=null) {
						//parentClass.childClasses.add(enumParams);
						this.putClassEnumInterfaceToParent(enumParams, parentClass.childClasses, modeAllOrUpdate);
						enumParams.parent = parentClass;
					}
					i = enumParams.findBlockParams.startIndex() + 1;
					continue;
				}
				else {
					i++;
					continue;
				}
			}
			else if (str.equals("interface")) {
				FindClassParams parentClass=null;
				top = stack.Get();
				if (top instanceof FindClassParams) {
					parentClass = (FindClassParams)top;
				}
				FindClassParams interfaceParams = new FindClassParams(this);
				FindInterface(src, interfaceParams, i, src.count-1);
				if (interfaceParams.findBlockParams!=null && interfaceParams.findBlockParams.startIndex()!=-1) {
					/*if (stack.len==0) { // 최상위 클래스만 등록
						result.add(interfaceParams);
					}*/
					putClassEnumInterfaceToResult(interfaceParams, result, stack, modeAllOrUpdate);
					stack.Push(interfaceParams);
					//mlistOfAllDefinedClasses.add(interfaceParams);
					this.putFindClassParams(interfaceParams, modeAllOrUpdate);
					interfaceParams.allocateListOfVarUses(modeAllOrUpdate);
					
					if (parentClass!=null) {
						//parentClass.childClasses.add(interfaceParams);
						this.putClassEnumInterfaceToParent(interfaceParams, parentClass.childClasses, modeAllOrUpdate);
						interfaceParams.parent = parentClass;
					}
					if (interfaceParams.indexOfExtends()!=-1) {
						i = interfaceParams.indexOfExtends() + 1;
					}
					else {
						i = interfaceParams.findBlockParams.startIndex() + 1;
					}
					continue;
				}
				else {
					i++;
					continue;
				}
			}
			else if (str.equals("class")) {
				if (i==1025) {
					int a;
					a=0;
					a++;
				}
				FindClassParams parentClass=null;
				FindClassParams classParams;
				top = stack.Get();
				if (top instanceof FindClassParams) {
					parentClass = (FindClassParams)top;
				}
				classParams = new FindClassParams(this);
				FindClass(src, classParams, i, src.count-1, false);
				if (classParams.findBlockParams!=null && classParams.findBlockParams.startIndex()!=-1) {
					
					if (classParams.findBlockParams.blockName.equals("DocuComment")) {
						int a;
						a=0;
						a++;
					}
					
					/*if (stack.len==0) { // 최상위 클래스만 등록
						result.add(classParams);
					}*/
					putClassEnumInterfaceToResult(classParams, result, stack, modeAllOrUpdate);
					stack.Push(classParams);
					//mlistOfAllDefinedClasses.add(classParams);
					this.putFindClassParams(classParams, modeAllOrUpdate);
					classParams.allocateListOfVarUses(modeAllOrUpdate);
					
					if (parentClass!=null) {
						//parentClass.childClasses.add(classParams);
						this.putClassEnumInterfaceToParent(classParams, parentClass.childClasses, modeAllOrUpdate);
						classParams.parent = parentClass;
					}
					//i = classParams.findBlockParams.startIndex() + 1;
					if (classParams.indexOfExtends()!=-1) {
						i = classParams.indexOfExtends() + 1;
						continue;
					}
					else if (classParams.indexOfImplements()!=-1) {
						i = classParams.indexOfImplements() + 1;
						continue;
					}
					else {
						i = classParams.findBlockParams.startIndex() + 1;
						continue;
					}
				}				
				else {
					i++;
					continue;
				}
			}
			/*else if (str.equals("extends")) {
				i++;
				continue;
			}
			else if (str.equals("implements")) {
				i++;
				continue;
			}*/
			else if (str.equals("return") || str.equals("continue") || str.equals("break") || 
					str.equals("throw")) {
				FindSpecialStatementParams specialStatement = FindSpecialStatement(src, i);
				if (specialStatement==null) {
					i++;
					continue;
				}
				
				Block parent = stack.Get();
				specialStatement.parent = parent;
				// return, continue, break문등의 SpecialStatements일 경우
				//parent.listOfStatements.add(specialStatement)
				putStatementToParent(specialStatement, parent, modeAllOrUpdate);
				//mlistOfSpecialStatement.add(specialStatement)
				putFindSpecialStatementParams(specialStatement, modeAllOrUpdate);
				i++;
				continue;
			}
			
			// 변수이름 찾기, 상수일 경우도 포함된 이유는 varUse를 찾기 위해서이다.
			else if (IsIdentifier(str) || CompilerHelper.IsConstant(str) ||
	        		IsDefaultType(str) /*||
	        		str.equals("return") || str.equals("continue") || str.equals("break")*/)   
			{ // 식별자, 상수, 타입(타입캐스트), 특수문이면
				if (i==1580) {
					int a;
					a=0;
					a++;
				}
				if (str.equals("return")) {
					int a;
					a=0;
					a++;
				}
				else if (str.equals("CustomView_test")) {
					int a;
					a=0;
					a++;
				}
				else if (str.equals("Object")) {
					int a;
					a=0;
					a++;
				}
				try {
					top = stack.Get();
				}catch(Exception e) {
					int a;
					a=0;
					a++;
				}
				if (i==1365) {
					int a;
					a=0;
					a++;
				}
				
				// break문을 만나고 스택의 top이 case문이면 caseBlock.breakExistsWhenCase을 true로 설정한다.
				// 이후에 다음 case문을 만나면 이전 case문의 끝을 정해준다.(이 함수의 case를 참조한다.)
				if (str.equals("break")) {
					FindControlBlockParams caseBlock = null;
					if (top!=null && top instanceof FindControlBlockParams) {
						caseBlock = (FindControlBlockParams) top;
						if (caseBlock.catOfControls!=null && caseBlock.catOfControls.category==CategoryOfControls.Control_case)
							caseBlock.breakExistsWhenCase = true;
					}
				}
				
				
				// 함수 정의문의 함수이름은 continue
				boolean isFunctionDecl = CheckBody(src, i);
				if (isFunctionDecl) {
					i++;
					continue;
				}
				
				FindVarParams var = null;
				FindVarUseParams varUse = null;
				ReturnOfFindVarDecl returnOfFindVarDecl;
				returnOfFindVarDecl = FindVarDeclarationsAndVarUses(src, i, oldTypeIndex, modeAllOrUpdate);
				if (returnOfFindVarDecl==null) oldTypeIndex = null;
				else {
					oldTypeIndex = returnOfFindVarDecl.oldTypeIndex;
					var = returnOfFindVarDecl.var;
					varUse = returnOfFindVarDecl.varUse;
				}
				
				if (top==null || top instanceof Block==false) {
					i++;
					continue;
				}
				if (var==null && varUse==null) {
					i++;
					continue;
				}
				
				Block parent = top;
				
				if (var!=null) {
					var.fieldName = src.getItem(var.varNameIndex()).str;
					if (var.varNameIndex()==1561) {
						int a;
						a=0;
						a++;
					}
					if (var.typeEndIndex()==1364) {
						int a;
						a=0;
						a++;
					}
					
					
					
					// 변수선언을 처음으로 감싸는 함수나 클래스를 변수선언의 parent로 삼는다.
					Block parentBlock = getParent(parent);
					//var.parent = parentBlock;
					var.parent = parent;
					boolean r = CheckVarScope(src, var);
					if (r) {
						var.startIndexOfScope = IndexForHighArray.indexRelative(var, src, var.varNameIndex());
						
						if (parentBlock instanceof FindClassParams) {
							// 변수선언을 처음으로 감싸는 함수나 클래스의 listOfVariableParams에 넣어준다.
							//parentBlock.listOfVariableParams.add(var)
							putVarToParent(var, parentBlock, modeAllOrUpdate);
							//mlistOfAllMemberVarDeclarations.add(var)
							putFindVarParams(var, modeAllOrUpdate, false);
							var.isMemberOrLocal = true;
							// 클래스의 listOfStatements에 문장으로 넣는다.
							parent.listOfStatements.add(var);
							
						}
						else if (parentBlock instanceof FindFunctionParams) {
							// 변수선언을 처음으로 감싸는 함수나 클래스의 listOfVariableParams에 넣어준다.
							//parentBlock.listOfVariableParams.add(var);
							putVarToParent(var, parentBlock, modeAllOrUpdate);
							
							if (parent instanceof FindControlBlockParams || 
									parent instanceof FindSpecialBlockParams) {
								// 2016.10.26 추가 
								// 제어블록에도 들어가야 '}' 처리시 endIndexOfScope가 정확해진다.
								//parent.listOfVariableParams.add(var);
								putVarToParent(var, parent, modeAllOrUpdate);
							}
							FindFunctionParams func = (FindFunctionParams) parentBlock;
							// 함수 파라미터에 선언된 지역변수선언은 제외한다. 
							if (func.indexOfLeftParenthesis()<=var.varNameIndex() && 
									var.varNameIndex()<=func.indexOfRightParenthesis()) {
								//func.listOfFuncArgs.add(var)
								putVarToFunctionArg(var, func, modeAllOrUpdate);
								//mlistOfAllLocalVarDeclarations.add(var);
								putFindVarParams(var, modeAllOrUpdate, true);
								var.isMemberOrLocal = false;
							}
							else {
								//mlistOfAllLocalVarDeclarations.add(var)
								putFindVarParams(var, modeAllOrUpdate, true);
								var.isMemberOrLocal = false;
								// 함수의 listOfStatements에 문장으로 넣는다. 함수파라미터는 제외한다.
								//parent.listOfStatements.add(var);
								boolean inInParenthesis = false;
								if (parent instanceof FindControlBlockParams) {
									FindControlBlockParams controlBlock = (FindControlBlockParams) parent;
									if (controlBlock.indexOfLeftParenthesis()<=var.startIndex() &&
										var.endIndex()<=controlBlock.indexOfRightParenthesis()) {
										inInParenthesis = true;
										controlBlock.listOfStatementsInParenthesis.add(var);
									}
								}
								if (inInParenthesis==false) {
									this.putStatementToParent(var, parent, modeAllOrUpdate);
								}
							}
						}
					}
				}
				
				if (varUse!=null) {					
					// varUse가 enumElement인 경우 FindVarParams로 바꿔서 enum타입에 넣어준다.
					if (top instanceof FindClassParams) {
						FindClassParams parentEnum = (FindClassParams)top;
						if (parentEnum.isEnum) {
							FindVarParams enumElement = new FindVarParams(this, varUse.index(), true, parentEnum);
							enumElement.fieldName = src.getItem(varUse.index()).str;
							String typeFullname = packageName + "." + getFullNameExceptPackageName(src, parentEnum);
							typeFullname = typeFullname.substring(0, typeFullname.length()-1);
							enumElement.typeName = typeFullname;
							
							// enum 변수에는  accessModifier가 없으므로 다음과 같이 public, static으로 설정한다.
							enumElement.accessModifier = new AccessModifier(this, -1, -1);
							enumElement.accessModifier.accessPermission = AccessPermission.Public;
							enumElement.accessModifier.isStatic = true;
							
							//parentEnum.listOfVariableParams.add(enumElement);
							this.putVarToParent(enumElement, parentEnum, modeAllOrUpdate);
							// 클래스의 listOfStatements에 문장으로 넣는다.
							//parentEnum.listOfStatements.add(enumElement);
							this.putStatementToParent(enumElement, parentEnum, modeAllOrUpdate);
							
							int docuIndex = varUse.index()-1;
		                	int indexOfDocuEnd = SkipOnlyBlank(src, true, 0, docuIndex); // 공백 스킵
		                	DocuComment docu = 
		                			FindDocuComment(src, /*false, null, findVarParams,*/ 0, indexOfDocuEnd);
		                	enumElement.docuComment = docu;
		                	
							i = varUse.index()+1;
							varUse.isEnumElement = true;
							continue;
						}
					}
					
					 
					Block parentBlock = getParent(parent);
					if (parentBlock instanceof FindClassParams) {
						FindClassParams parentClass = (FindClassParams)parentBlock;
						varUse.classToDefineThisVarUse = parentClass;
						/*try {
						parentClass.listOfAllVarUses.add(varUse)
						}catch(Exception e) {
							int a;
							a=0;
							a++;
						}
						if (varUse.isForVarOrForFunc) parentClass.listOfAllVarUsesForVar.add(varUse);
						else parentClass.listOfAllVarUsesForFunc.add(varUse);*/
						putVarUseToClass(varUse, parentClass, modeAllOrUpdate);
					}
					else if (parentBlock instanceof FindFunctionParams) {
						FindFunctionParams parentFunc = (FindFunctionParams)parentBlock;
						varUse.funcToDefineThisVarUse = parentFunc;
						/*parentFunc.listOfAllVarUses.add(varUse);
						if (varUse.isForVarOrForFunc) parentFunc.listOfAllVarUsesForVar.add(varUse);
						else parentFunc.listOfAllVarUsesForFunc.add(varUse);*/
						putVarUseToFunction(varUse, parentFunc, modeAllOrUpdate);
						
												
						FindClassParams parentClass = (FindClassParams)parentFunc.parent;
						if (parentClass==null) {
							int a;
							a=0;
							a++;
						}
						if (parentClass!=null && parentClass.isEnum==false) {							
							// 해당 클래스의 listOfAllVarUses에도 넣어준다.
							varUse.classToDefineThisVarUse = parentClass;
							/*parentClass.listOfAllVarUses.add(varUse);
							if (varUse.isForVarOrForFunc) parentClass.listOfAllVarUsesForVar.add(varUse);
							else parentClass.listOfAllVarUsesForFunc.add(varUse);
							*/
							putVarUseToClass(varUse, parentClass, modeAllOrUpdate);
						}
						
					}//else if
				
				}//if (varUse.index()!=-1)
				
				if (varUse!=null) {
					if (varUse.index()==316) {
						int a;
						a=0;
						a++;
					}
					
					// 변수사용 다음에 '='이 오는 수식을 찾는다. 
					// 그러나 조건문, 반복문의 조건에 할당문이 있을수 있으므로 그것을 제외한다.
					int indexOfEquals = IsLValue(src, varUse); 
					if (indexOfEquals>0) {
						
						
						// 배열초기화문이면 여기에선 제외하고 FindAllClassesAndItsMembers2_sub()의 배열초기화문을 처리할때 넣는다.
						//boolean isArrayIntitializer = isArrayInitializer(src, varUse);
						//if (isArrayIntitializer) continue;
						
						FindAssignStatementParams assignStatement = new FindAssignStatementParams(this, -1, -1);
						FindAssignStatement(src, assignStatement, varUse, indexOfEquals);
						
						boolean isArrayIntitializer = false;
						if (assignStatement.found) {
							this.putFindAssignStatementParamsTomlistOfAllAssignStatements(assignStatement, modeAllOrUpdate);
							
							// a = 1 + f1().f2(); 에서 할당문이므로 f1(), f2()는 건너뛴다. 
							// a = 1 + (a2 = 2); 에서 a2는 건너뛴다.
							if (isAssignOrIndependentFuncCall &&
									startIndexOfAssign<=varUse.index() && varUse.index()<=endIndexOfAssign) {
								assignStatement.isIndependent = false;
							}
							// f1 ( f2() );  여기에서 f2()는 건너뛴다.
							// f (a=3, b); 에서 a는 건너뛴다.
							else if (isAssignOrIndependentFuncCall==false &&
									startIndexOfIndependentFuncCall<=varUse.index() && 
									varUse.index()<=endIndexOfIndependentFuncCall) {
								assignStatement.isIndependent = false;
							}
							Block parentBlock = parent;
							if (parentBlock instanceof FindControlBlockParams) {
								FindControlBlockParams controlBlock = (FindControlBlockParams) parentBlock;
								if (controlBlock.indexOfLeftParenthesis()<varUse.index() && 
										varUse.index()<controlBlock.indexOfRightParenthesis()) {
									// 제어블록의 괄호안에 있는 할당문은 listOfStatementsInParenthesis에 넣는다.
									assignStatement.isIndependent = false;
									if (controlBlock.listOfStatementsInParenthesis==null) {
										controlBlock.listOfStatementsInParenthesis = new ArrayListIReset(5);										
									}
									//controlBlock.listOfStatementsInParenthesis.add(assignStatement)
									putStatementToParenthesisOfParent(assignStatement, controlBlock, modeAllOrUpdate);
									assignStatement.parent = controlBlock;
								}
							}
							// 독립적인 할당문만 리스트에 등록하고 블록에 넣는다.
							if (assignStatement.isIndependent) {
								//mlistOfAllAssignStatements.add(assignStatement)
								putFindAssignStatementParams(assignStatement, modeAllOrUpdate);
								
								startIndexOfAssign = assignStatement.startIndex();
								endIndexOfAssign = assignStatement.endIndex();
								
								assignStatement.found = false;
								
								//parentBlock.listOfStatements.add(assignStatement);
								this.putStatementToParent(assignStatement, parentBlock, modeAllOrUpdate);
								
								assignStatement.parent = parentBlock;
								
								//inputStatementToSuitableBlock(src, classParams, assignStatement);
								isAssignOrIndependentFuncCall = true;
								
								//this.findIncrementStatements(src, assignStatement);
							}
						}//if (assignStatement.found) {
					}//if (indexOfEquals>0) {
					
					else if (varUse.isForVarOrForFunc==false) { // 독립적인 함수호출문 찾기
						boolean isIndependent = true;
						
						// a = 1 + f1().f2(); 에서 할당문이므로 f1(), f2()는 건너뛴다. 
						if (isAssignOrIndependentFuncCall &&
								startIndexOfAssign<=varUse.index() && varUse.index()<=endIndexOfAssign) {
							isIndependent = false;
						}
						// f1 ( f2() );  여기에서 f2()는 건너뛴다.
						else if (isAssignOrIndependentFuncCall==false &&
								startIndexOfIndependentFuncCall<=varUse.index() && varUse.index()<=endIndexOfIndependentFuncCall) {
							isIndependent = false;
						}
						
						FindIndependentFuncCallParams independentFuncCall = new FindIndependentFuncCallParams(this, -1, -1);
						FindIndependentFuncCall(src, varUse, independentFuncCall);
						
						if (independentFuncCall.found) {
							Block parentBlock = parent;
							if (parentBlock instanceof FindControlBlockParams) {
								FindControlBlockParams controlBlock = (FindControlBlockParams) parentBlock;
								if (controlBlock.indexOfLeftParenthesis()<varUse.index() && 
										varUse.index()<controlBlock.indexOfRightParenthesis()) {
									// 제어블록의 괄호안에 있는 listOfStatementsInParenthesis에 넣는다.
									isIndependent = false;
									if (controlBlock.listOfStatementsInParenthesis==null) {
										controlBlock.listOfStatementsInParenthesis = new ArrayListIReset(5);										
									}
									//controlBlock.listOfStatementsInParenthesis.add(independentFuncCall);
									this.putStatementToParenthesisOfParent(independentFuncCall, controlBlock, modeAllOrUpdate);
									independentFuncCall.parent = controlBlock;
								}
							}
							// 독립적인 함수호출문만
							if (isIndependent) {							
								startIndexOfIndependentFuncCall = independentFuncCall.startIndex();
								endIndexOfIndependentFuncCall = independentFuncCall.endIndex();
								
								//parentBlock.listOfStatements.add(independentFuncCall);
								this.putStatementToParent(independentFuncCall, parentBlock, modeAllOrUpdate);
								independentFuncCall.parent = parentBlock;
								//inputStatementToSuitableBlock(src, classParams, independentFuncCall);
								isAssignOrIndependentFuncCall = false;
								
								//this.findIncrementStatements(src, independentFuncCall);
							}
						}
					}//else if (varUse.isForVarOrForFunc==false) { // 독립적인 함수호출문 찾기
				}//if (varUse.index()!=-1)
				
				
				if (varUse!=null) {
					if (varUse.index()==900) {
						int a;
						a=0;
						a++;
					}
					
					FindIncrementStatementParams inc = this.findIndependentIncrementStatement_sub(src, varUse);
					
					Block parentBlock = parent;
					
					// ++i;,i++;와 같은 독립적인 증감문을 찾아서 해당 블록의 listOfStatements에 넣는다.
					FindIncrementStatementParams independentInc = this.findIndependentIncrementStatement(src, inc);
					boolean isIndependent = true;
					if (independentInc!=null) {						
						if (parentBlock instanceof FindControlBlockParams) {
							FindControlBlockParams controlBlock = (FindControlBlockParams) parentBlock;
							if (controlBlock.indexOfLeftParenthesis()<varUse.index() && 
									varUse.index()<controlBlock.indexOfRightParenthesis()) {
								// 제어블록의 괄호안에 있는 listOfStatementsInParenthesis에 넣는다.
								isIndependent = false;
								if (controlBlock.listOfStatementsInParenthesis==null) {
									controlBlock.listOfStatementsInParenthesis = new ArrayListIReset(5);										
								}
								//controlBlock.listOfStatementsInParenthesis.add(independentInc);
								this.putStatementToParenthesisOfParent(independentInc, controlBlock, modeAllOrUpdate);
								independentInc.parent = controlBlock; 
							}
						}
						// 독립적인 증감문만
						if (isIndependent) {							
							//parentBlock.listOfStatements.add(independentInc);
							this.putStatementToParent(independentInc, parentBlock, modeAllOrUpdate);
							independentInc.parent = parentBlock; 
						}
					}//if (independentInc!=null) {	
					
				}//if (varUse.index()!=-1) {
				
				if (var!=null) {
					if (src.getItem(var.endIndex()).equals("=")) {
						//for (char c='a'; c<='z'; c++) {
						//char[] arr = {c}; 여기에서 var는 arr이고 endIndex()는 =을 가리킨다.						
						// arr은 배열초기화문장인데 다음 }을 만날때 for블록이 pop될수가 있다.
						// 아래와 같이 i = var.endIndex();을 다음인덱스로 해줘야 배열초기화 처리를 할 수가있다.
						i = var.endIndex();
					}
					else {
						i = var.endIndex() + 1;
					}
					continue;
				}
				i++;
				continue;
			} // else if (IsIdentifier(str)) {
			
			
			
			else if (str.equals("static")) { // static 블럭
				top = stack.Get();
				FindFunctionParams function = new FindFunctionParams(this, -1, -1);
				if (top instanceof FindClassParams) {
					FindClassParams parent = (FindClassParams)top;
					//FindFunction(src, parent, function, i)
					findStaticBlock(src, parent, function, i, modeAllOrUpdate);
				}
				
				if (function.found) {	// 함수 정의
					function.name = function.findBlockParams.blockName;
					function.returnType = "void";
					
					function.isConstructor = true;
					function.isConstructorThatInitializesStaticFields = true;
					
					//mlistOfAllFunctions.add(function)
					putFindFunctionParams(function, modeAllOrUpdate);
					if (top instanceof FindClassParams) {
						FindClassParams parent = (FindClassParams)top;
						//parent.listOfFunctionParams.add(function)
						putFunctionToParent(function, parent, modeAllOrUpdate);
						
						function.parent = parent;
						parent.listOfConstructor.add(function);
					}
					else {
						errors.add(new Error(this, i, i, "invalid static block."));
					}
					
					stack.Push(function);
					function.allocateListOfVarUses(modeAllOrUpdate);
					//i = function.findBlockParams.startIndex() + 1;
					i++;
					continue;
				}
				else { //  함수호출, 수식 등
					i++;
					continue;
				}
				
			}
			
			else if (str.equals("(")) {
				if (i==330) {
					int a;
					a=0;
					a++;
				}
				top = stack.Get();
				FindFunctionParams function = new FindFunctionParams(this, -1, -1);
				if (top instanceof FindClassParams) {
					FindClassParams parent = (FindClassParams)top;
					FindFunction(src, parent, function, i);
				}
				
				if (function.found) {	// 함수 정의
					function.name = src.getItem(function.functionNameIndex()).str;
					if (src.getItem(function.functionNameIndex()).equals("onTouch")) {
						int a;
						a=0;
						a++;
					}
					//mlistOfAllFunctions.add(function);
					putFindFunctionParams(function, modeAllOrUpdate);
					if (top instanceof FindClassParams) {						
						FindClassParams parent = (FindClassParams)top;
						//parent.listOfFunctionParams.add(function);
						this.putFunctionToParent(function, parent, modeAllOrUpdate);
						function.parent = parent;
						
						if (function.isConstructor) {
							parent.listOfConstructor.add(function);
						}
					}
					else {
						errors.add(new Error(this, function.startIndex(), function.startIndex(), "invalid function"));
					}
					
					stack.Push(function);
					function.allocateListOfVarUses(modeAllOrUpdate);
					//i = function.findBlockParams.startIndex() + 1;
					i++;
					continue;
				}
				else { //  함수호출, 수식 등
					i++;
					continue;
				}
			}
			else if (str.equals("synchronized") || str.equals("try") || str.equals("catch") || str.equals("finally")) {
				if (str.equals("finally")) {
					int a;
					a=0;
					a++;
				}
				if (i==10439) {
					int a;
					a=0;
					a++;
				}
				top = stack.Get();
				FindSpecialBlockParams block = new FindSpecialBlockParams(this, 0, -1, -1);
				FindSpecialBlock(src, block, i);
				if (block.found) {					
					if (top!=null && top instanceof FindClassParams==false) {
						Block parent = top;
						//parent.listOfControlBlocks.add(block)
						putControlBlockToParent(block, parent, modeAllOrUpdate);
						//parent.listOfStatements.add(block);
						this.putStatementToParent(block, parent, modeAllOrUpdate);
						this.putFindControlBlockParams(block, modeAllOrUpdate);
						if (src.getItem(block.nameIndex()).equals("synchronized")) {
							putSynchronizedBlockTomlistOfSynchronizedBlocks(block, modeAllOrUpdate);
						}
						block.parent = parent;
						block.indexInListOfControlBlocksOfParent = parent.listOfControlBlocks.count-1;						
					}
					else {
						errors.add(new Error(this, i, i, "invalid "+str));
					}
					stack.Push(block);
					i = block.nameIndex() + 1;
					continue;
				}
				else { // synchronized 함수 등
					i++;
					continue;
				}
			}
			else if (str.equals("if") || str.equals("while") || str.equals("for") || 
					str.equals("else")  || str.equals("do") || 
					str.equals("switch") || str.equals("case") || str.equals("default")) {
				if (i==8985) {
					int a;
					a=0;
					a++;
				}
				if (str.equals("case")) {
					int a;
					a=0;
					a++;
				}
				top = stack.Get();
				FindControlBlockParams control = new FindControlBlockParams(this, null, -1, -1);
				FindControlBlock(src, i, control, modeAllOrUpdate);
				if (control.found) {
					if (str.equals("case") && 
							control.findBlockParams!=null && control.findBlockParams.blockName.equals("2")) {
						int a;
						a=0;
						a++;
					}
					if (control.isBlock==false) {
						
					}
					if (str.equals("else")) {
						int a;
						a=0;
						a++;
					}
					boolean isPreviousCase = false;
					FindControlBlockParams caseBlock = null;					
					if (top!=null && top instanceof FindControlBlockParams) {
						caseBlock = (FindControlBlockParams) top;
						// case문 다음에 case문이 오면 이전 case문을 스택에서 pop해준다.
						if (caseBlock.catOfControls!=null && caseBlock.catOfControls.category==CategoryOfControls.Control_case) {
							if (control.catOfControls!=null && control.catOfControls.category==CategoryOfControls.Control_case) {
								isPreviousCase = true;
							}
						}
					}
					// 다음 case문을 만날 경우는 이전 case문을 스택에서 pop해준다.
					if (isPreviousCase) {
						// 이전 case문의 끝을 정해준다.
						caseBlock.endIndex = IndexForHighArray.indexRelative(caseBlock, src, i-1);
						caseBlock.findBlockParams.endIndex = IndexForHighArray.indexRelative(caseBlock.findBlockParams, src, i-1);
						stack.Pop(); // 이전 case문을 pop
						top = stack.Get();
					}
					
					if (top!=null && top instanceof FindClassParams==false) {
						Block parent = top;
						//parent.listOfControlBlocks.add(control);
						this.putControlBlockToParent(control, parent, modeAllOrUpdate);
						//parent.listOfStatements.add(control);
						this.putStatementToParent(control, parent, modeAllOrUpdate);
						control.parent = parent;
						control.indexInListOfControlBlocksOfParent = parent.listOfControlBlocks.count-1;
					}
					else {
						errors.add(new Error(this, i, i, "invalid "+str));
						
					}
					stack.Push(control);
					
					//mlistOfAllControlBlocks.add(control)
					putFindControlBlockParams(control, modeAllOrUpdate);
					
					
					if (control.catOfControls!=null) {
						if (control.catOfControls.category!=CategoryOfControls.Control_elseif) {
							i = control.nameIndex() + 1;
						}
						else {
							i = control.indexOfLeftParenthesis() + 1;
						}
					}
					continue;
				}//if (control.found) {
				else {
					errors.add(new Error(this, i, i, "invalid "+str));
					i = control.nameIndex() + 1;
					continue;
				}
			}//else if (str.equals("if") || str.equals("while") || str.equals("for") || 
			//str.equals("else")  || str.equals("do") || 
			//str.equals("switch") || str.equals("case")) {
			
			else if (str.equals(")")) { 
				if (i==903) {
					int a;
					a=0;
					a++;
				}
				// 제어구조의 오른쪽 괄호, 제어구조의 조건 안에 있는 문장들에 포함된 증감문들을 찾는다.
				Block block = stack.Get();
				if (block instanceof FindControlBlockParams) {
					FindControlBlockParams controlBlock = (FindControlBlockParams) block;
					int leftPairIndex = CompilerHelper.CheckParenthesis(src, "(", ")", 0, i, true);
					int keywordIndex = this.SkipBlank(src, true, 0, leftPairIndex-1);
					CodeString keyword = src.getItem(keywordIndex);
					if (keyword.equals("for") || keyword.equals("while") || keyword.equals("if") || 
							keyword.equals("switch")) { // else if , do while 포함
						if (controlBlock!=null && controlBlock.listOfStatementsInParenthesis!=null && controlBlock.listOfStatementsInParenthesis.count>0) {
							int j;
							for (j=controlBlock.listOfStatementsInParenthesis.count-1; j>=0; j--) {
								FindStatementParams statement = 
									(FindStatementParams) controlBlock.listOfStatementsInParenthesis.getItem(j);
								if (statement instanceof FindAssignStatementParams ||
										statement instanceof FindIndependentFuncCallParams) {
									this.findIncrementStatementInStatement(src, statement);
								}
							}
						}
					}//if (keyword.equals("for") || keyword.equals("while") || keyword.equals("if") || keyword.equals("switch")) { // else if , do while 포함
				}//if (block instanceof FindControlBlockParams) {
				i++;
				continue;
			}//else if (str.equals(")")) { // 제어구조의 오른쪽 괄호
					
			else if (str.equals(";")) {
				Block curBlock = stack.Get();
				
				FindControlBlockParams controlBlock = null;
				boolean isInParenthesisOfControlBlock = false; 
				if (curBlock instanceof FindControlBlockParams) {
					controlBlock = (FindControlBlockParams) curBlock;
					int leftPairIndex = CompilerHelper.CheckParenthesis(src, "(", ")", 0, i, true);
					int keywordIndex = this.SkipBlank(src, true, 0, leftPairIndex-1);
					CodeString keyword = src.getItem(keywordIndex);
					if (keyword.equals("for") || keyword.equals("while") || keyword.equals("if") || 
							keyword.equals("switch")) { // else if , do while 포함
						isInParenthesisOfControlBlock = true;
					}
				}
				
				if (isInParenthesisOfControlBlock==false) {
					// 일반적인 경우 ';'을 만나면 문장안에서 증감문들을 찾는다.
					if (curBlock!=null && curBlock.listOfStatements.count>0) {
						FindStatementParams statement = 
							(FindStatementParams) curBlock.listOfStatements.getItem(curBlock.listOfStatements.count-1);
						if (statement instanceof FindAssignStatementParams ||
								statement instanceof FindIndependentFuncCallParams) {
							this.findIncrementStatementInStatement(src, statement);
						}
					}
				}
				else {
					// for 루프의 괄호안에서 ';'을 만나면 문장안에서 증감문들을 찾는다.
					if (controlBlock!=null && controlBlock.listOfStatementsInParenthesis!=null && controlBlock.listOfStatementsInParenthesis.count>0) {
						FindStatementParams statement = 
							(FindStatementParams) controlBlock.listOfStatementsInParenthesis.getItem(controlBlock.listOfStatementsInParenthesis.count-1);
						if (statement instanceof FindAssignStatementParams ||
								statement instanceof FindIndependentFuncCallParams) {
							this.findIncrementStatementInStatement(src, statement);
						}
					}
				}
				
				
				// 함수가 인터페이스의 메소드 선언이면 스택에서 pop을 해야 한다.
				if (curBlock instanceof FindFunctionParams) {
					FindFunctionParams func = (FindFunctionParams)curBlock;
					if (func.isInterfaceMethod) {
						stack.Pop();
						func.endIndex = IndexForHighArray.indexRelative(func, src, i-1 );
						i++;
						continue;
					}
					else {
						
					}
					
				}
				// if (a>1) if (a>2) a=0;
				// 위의 경우와 같은 제어단문에 제어단문이 섞여 있을때 위쪽 제어단문의 endIndex()를
				// 정해주고 스택에서 빼줘야 한다.
				boolean isCase = false;
				while (true) {
					Block topBlock = stack.Get();
					isCase = false;
					if (topBlock instanceof FindControlBlockParams) {
						FindControlBlockParams shortControl = (FindControlBlockParams)topBlock;
						if (shortControl.catOfControls!=null && shortControl.catOfControls.category==CategoryOfControls.Control_else &&
								shortControl.isBlock==false) {
							int a;
							a=0;
							a++;
						}
						
						if (shortControl.catOfControls!=null && shortControl.catOfControls.category==CategoryOfControls.Control_case) {
							if (shortControl.isBlock==false) {
								isCase = true;
								break;
							}
						}
						if (shortControl.isBlock==false && isCase==false) {
							Block block = stack.Pop();
							if (block!=null) {
								block.endIndex = IndexForHighArray.indexRelative(block, src, i);
							}
							//i++;
							continue;
						}
						else { // 제어블록
							break; 
						}
					}
					else { // 함수 등
						break;
					}
				}
				i++;
				continue;
			}
			/*else if (str.equals(")")) {
				Block block = stack.Get();
				if (block instanceof FunctionCall) {
					FunctionCall funcCall = (FunctionCall)block;
					if (funcCall.indexOfRightPair==i) { // 함수호출의 마지막 )
						stack.Pop();
					}
					i++;
					continue;
				}
				else {
					i++;
					continue;
				}
			}*/
			else if (str.equals("}")) {
				if (i==1535) {
					int a;
					a=0;
					a++;
				}
					//Block block = stack.Pop();
				// char[] a = {'a','b}; 이와 같은 배열을 처리할 수 없기 때문에 Block인지를 확인하여
				// Block이 아니면 skip하고 Block일때만 pop한다.
					Block block = stack.Get();
					if (block instanceof FindControlBlockParams) {
						FindControlBlockParams b = (FindControlBlockParams) block;
						if (b.catOfControls!=null && b.catOfControls.category==CategoryOfControls.Control_case) {
							int a;
							a=0;
							a++;
						}
					}
					if (block instanceof FindClassParams) {
						if (block.findBlockParams!=null && block.findBlockParams.blockName.equals("Edit")) {
							int a;
							a=0;
							a++;
						}
						if (block.findBlockParams!=null && block.findBlockParams.blockName.equals("DocuComment")) {
							int a;
							a=0;
							a++;
						}
					}
					
					/*if (block instanceof Block) {
						block = stack.Pop();
					}
					else {
						i++;
						continue;
					}*/
					
					// switch() {
					// case 0: a=1;
					// case 1: a=2;
					// }
					boolean isCaseAndSwitchEnds = false;
					if (block instanceof FindControlBlockParams) {
						FindControlBlockParams b = (FindControlBlockParams) block;
						if (b.catOfControls!=null && b.catOfControls.category==CategoryOfControls.Control_case) {
							if (b.isBlock==false) { // 괄호없는 case문을 만나면
								isCaseAndSwitchEnds = true;
								stack.Pop(); // case문을 pop
								block = stack.Pop(); // switch문을 pop				
							}
						}
					}
					/*if (isCaseAndSwitchEnds==false) {
						if (block instanceof Block) {
							block = stack.Pop();
							if (block instanceof FindArrayInitializerParams) {
								i++;
								continue;
							}
						}
						else {
							i++;
							continue;
						}
					}*/
					if (isCaseAndSwitchEnds==false) {						
						if (block instanceof Block) {
							// block이 배열초기화문이면 스택에서 block을 빼내고 continue.
							// 배열초기화문은 원래 블록이 아니므로.
							if (block instanceof FindArrayInitializerParams) {						
								block = stack.Pop();
								i++;
								continue;
							}
							else {
								block = stack.Pop();
							}
						}
						else { // '}'이 블록이 아니면 continue.
							i++;
							continue;
						}
					}
					
					// block의 endIndex()와 block에서 정의된 변수들의 마지막 scope를 정해준다.
					if (block!=null && block.findBlockParams!=null) {
						FindControlBlockParams controlBlock=null;
						if (block instanceof FindControlBlockParams) {							
							controlBlock = (FindControlBlockParams)block;
						}
						// do_while루프일때는 이미 checkControlBody()에서 정했다.
						if ((block instanceof FindControlBlockParams)==false || 
								controlBlock.catOfControls==null  || // try, catch, finally, synchronized 블록
								(controlBlock.catOfControls!=null && controlBlock.catOfControls.category!=CategoryOfControls.Control_dowhile)) {
							block.findBlockParams.endIndex = IndexForHighArray.indexRelative(block.findBlockParams, src, i);
							block.endIndex = IndexForHighArray.indexRelative(block, src, i );
						}
						int k;
						for (k=0; k<block.listOfVariableParams.count; k++) {
							FindVarParams var = (FindVarParams) block.listOfVariableParams.getItem(k);
							if (var.endIndexOfScope()==-1) {
								var.endIndexOfScope = IndexForHighArray.indexRelative(var, src, block.endIndex()-1);
							}
						}
						
						// if (a>0) while(a==0) {
						//			int a;
						//			a=0;
						//			} 
						// 위의 경우와 같은 제어단문에 제어복문이 섞여 있을때 제어단문의 endIndex()를
						// 정해주고 스택에서 빼줘야 한다.
						// if 						while
						// 	 if {} 						try{}
						//   else if {} 				catch{}
						//   else {}					finally{}
						
						
						//if (a>0) while(a==0) {
						//	int a;
						//	a=0;
						//}
						while (true) {
							Block topBlock = stack.Get();
							if (topBlock instanceof FindControlBlockParams) {
								FindControlBlockParams shortControl = (FindControlBlockParams)topBlock;
								if (shortControl.isBlock==false) { // 제어단문
									// 현재블록이 if, else if등 또는 try, catch등이고 
									// 상위블록이 제어단문일경우 스택에서 빼면 안된다.
									//while (a>1) 
									//	if (a>2) {} else if (a==1) {} else {}
									// 이와 같은 경우 while을 빼면 안된다.
									if (block  instanceof FindControlBlockParams) {
										FindControlBlockParams danglingBlock = (FindControlBlockParams) block;
										if ((danglingBlock.catOfControls!=null && danglingBlock.catOfControls.category==CategoryOfControls.Control_if) ||
											(danglingBlock.catOfControls!=null && danglingBlock.catOfControls.category==CategoryOfControls.Control_elseif)) {
											int nextIndex = SkipBlank(src, false, i+1, src.count-1);
											CodeString next = src.getItem(nextIndex);
											if (next.equals("else")) {
												break;
											}
										}
									}
									if (block  instanceof FindSpecialBlockParams) {
										FindSpecialBlockParams danglingBlock = (FindSpecialBlockParams) block;
										if (danglingBlock.toString().equals("try") || 
												danglingBlock.toString().equals("catch")) {
											int nextIndex = SkipBlank(src, false, i+1, src.count-1);
											CodeString next = src.getItem(nextIndex);
											if (next.equals("catch") || next.equals("finally")) {
												break;
											}
										}
									}
									// 제어단문을 스택에서 빼낸다.
									Block sblock = stack.Pop();
									if (sblock!=null) {
										sblock.endIndex = IndexForHighArray.indexRelative(sblock, src, i );
									}
									//i++;
									continue;
								}//if (shortControl.isBlock==false) { // 제어단문
								else { // 제어블록
									break; 
								}
							}//if (topBlock instanceof FindControlBlockParams) {
							else { // 함수 등
								break;
							}
						}//while (true) {
					}
					else {
						errors.add(new Error(this, i,i,"invalid '}'"));
					}
			//	}
				i++;
				continue;
			}
			else {
				i++;
			}
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				
			}
		}//for (i=0; i<src.count; ) {
	}
	
	/** block을 처음으로 감싸는 클래스나 함수를 얻는다. 
	 * 예를들어 제어블럭에서 getParent를 호출하면 이 블럭을 처음으로 감싸는 함수를 얻는다.*/
	public Block getParent(Block block) {
		while (true) {
			if (block instanceof FindFunctionParams) return block;
			if (block instanceof FindClassParams) return block;
			if (block!=null) {
				block = block.parent;
			}
			else {
				return null;
			}
		}
	}
	
	/** parent와 child간에 parent관계가 있는지 조사한다.*/
	public boolean isParent(Block parent, Block child, Block limit) {
		if (parent==null) return false;
		while (true) {
			if (child==null) return false;
			if (child==parent) return true;
			if (child!=null) {
				child = child.parent;
			}
		}
	}
	
	/** varUse들을 해당클래스의 listOfAllVarUsesForVar, listOfAllVarUsesForFunc해시테이블에 넣는다.*/
	public void InputVarUsesToHashtableOfClass(HighArray_CodeString src, FindClassParams classParams) {
		int i;
		int j;
		for (i=0; i<2; i++) {			
			HighArray<FindVarUseParams> listOfAllVarUses = null;
			if (i==0) listOfAllVarUses = classParams.listOfAllVarUsesForVar;
			else listOfAllVarUses = classParams.listOfAllVarUsesForFunc;
			if (listOfAllVarUses==null) return;
			
			Hashtable2 hashtable = new Hashtable2(src, 50, 20);
			int len = listOfAllVarUses.getCount();
			for (j=0; j<len; j++) {
				FindVarUseParams varUse = (FindVarUseParams) listOfAllVarUses.getItem(j); 
				if (src.getItem(varUse.index()).equals("super")) {
					int a;
					a=0;
					a++;
				}
				hashtable.input(varUse);
			}
			
			if (i==0) classParams.listOfAllVarUsesForVarHashed = hashtable;
			else classParams.listOfAllVarUsesForFuncHashed = hashtable;
		}
	}
	
	/** this, super와 varUse들을 해당클래스의 해시테이블에 넣는다.
	 * @param classParams : 처음 호출시는 최상위 클래스, 재귀적 호출시는 해당클래스*/
	public void inputThisAndSuperToClassAndInputVarUsesToHashtable(HighArray_CodeString src, 
			FindClassParams classParams, ModeAllOrUpdate modeAllOrUpdate) {
		int i;
		if (classParams==null) return;
		if (classParams.isEnum) return;
		if (classParams.isInterface) return;
		
		if (classParams.childClasses!=null) {
			FindVarParams varThis = new FindVarParams(this, true, false, false, classParams);
			FindVarParams varSuper = new FindVarParams(this, false, false, true, classParams);
			AccessModifier accessModifierThis = new AccessModifier(this, -1, -1);
			AccessModifier accessModifierSuper = new AccessModifier(this, -1, -1);
			varThis.accessModifier = accessModifierThis;
			varSuper.accessModifier = accessModifierSuper;
			
			//classParams.listOfVariableParams.add(varThis); // this
			this.putVarToParent(varThis, classParams, modeAllOrUpdate);
			//classParams.listOfVariableParams.add(varSuper); // super
			this.putVarToParent(varSuper, classParams, modeAllOrUpdate);
			//this.mlistOfAllMemberVarDeclarations.add(varThis);
			this.putFindVarParams(varThis, modeAllOrUpdate, false);
			//this.mlistOfAllMemberVarDeclarations.add(varSuper);
			this.putFindVarParams(varSuper, modeAllOrUpdate, false);
			InputVarUsesToHashtableOfClass(src, classParams);
			
			
			for (i=0; i<classParams.childClasses.count; i++) {
				FindClassParams child = (FindClassParams) classParams.childClasses.getItem(i);
				inputThisAndSuperToClassAndInputVarUsesToHashtable(src, child, modeAllOrUpdate);
			}
		}
		else {
			FindVarParams varThis = new FindVarParams(this, true, false, false, classParams);
			FindVarParams varSuper = new FindVarParams(this, false, false, true, classParams);
			//classParams.listOfVariableParams.add(varThis); // this
			this.putVarToParent(varThis, classParams, modeAllOrUpdate);
			//classParams.listOfVariableParams.add(varSuper); // super
			this.putVarToParent(varSuper, classParams, modeAllOrUpdate);
			//this.mlistOfAllMemberVarDeclarations.add(varThis);
			this.putFindVarParams(varThis, modeAllOrUpdate, false);
			//this.mlistOfAllMemberVarDeclarations.add(varSuper);
			this.putFindVarParams(varSuper, modeAllOrUpdate, false);
			//Compiler.SortByName(src, classParams.listOfAllVarUsesForVar, classParams.listOfAllVarUsesForVarSortedByName);
			InputVarUsesToHashtableOfClass(src, classParams);
		}
		
	}
	
	boolean hasInHeritted(FindClassParams classParams, String className) {
		if (classParams.classNameToExtend.equals(className)) return true;
		if (classParams.classToExtend!=null) {
			// 상속관계에서 이미 상속을 했는지 확인한다.
			boolean r = hasInHeritted(classParams.classToExtend, className);
			return r;
		}
		return false;
	}
	
	/** childClass의 parentClass를 찾아 accessPermission을 확인하고 멤버를 상속한다. 
	 * 즉 childClass의 멤버에 추가한다.<br>
	 * //if (childClass.hasInherited) return; 
	 * 이 주석을 풀면 ConnectDialog, Control, ConnectDialog을 Start2상태로 순서대로 읽을때 주석을 제대로 못읽을 수가 있다.
	 * 왜냐하면 ConnectDialog의 parent인 EditableDialog가 hasInherited가 true인 상태여서 Control을 
	 * 다시 읽어 클래스 캐시를 갱신하였더라도 그대로 리턴을 해버려서 
	 * Control이 주석이 없는 바이트코드 상태이고  주석이 있는 Start2상태가 아니기 때문이다.*/
	void FindMembersInherited(FindClassParams childClass) {
		if (this.getShortName(childClass.name).equals("String")) {
			int a;
			a=0;
			a++;
		}
		if (childClass.name.equals("java.lang.Object") || childClass.name.equals("java.lang.object")) {
			childClass.hasInherited = true;
			return;
		}
		
		// 주석을 풀면 ConnectDialog, Control, ConnectDialog을 Start2상태로 순서대로 읽을때 주석을 제대로 못읽을 수가 있다.
		// 왜냐하면 ConnectDialog의 parent인 EditableDialog가 hasInherited가 true인 상태여서 Control을 
		// 다시 읽어 클래스 캐시를 갱신하였더라도 그대로 리턴을 해버려서 
		// Control이 주석이 없는 바이트코드 상태이고  주석이 있는 Start2상태가 아니기 때문이다.
		if (childClass.hasInherited) return;
		
		childClass.hasInherited = true;
		
		
		String parentClassName;
		int loopCount = 0;
		ArrayListString listOfparentClassName = new ArrayListString(2);
		if (childClass.isInterface==false) {
			if (childClass.classNameToExtend==null) {
				childClass.classNameToExtend = "java.lang.Object";
			}
			parentClassName = childClass.classNameToExtend;
			listOfparentClassName.add(parentClassName);
			loopCount = 1;
			if (childClass.interfaceNamesToImplement!=null) {
				loopCount += childClass.interfaceNamesToImplement.count;
				for (int i=0; i<childClass.interfaceNamesToImplement.count; i++) {
					listOfparentClassName.add(
							childClass.interfaceNamesToImplement.getItem(i));
				}
			}
		}
		else {//인터페이스
			if (childClass.classNameToExtend==null) {
				childClass.classNameToExtend = "java.lang.Object";
			}
			if (childClass.interfaceNamesToImplement==null) {
				loopCount = 1;
				listOfparentClassName.add(childClass.classNameToExtend);
			}
			else {
				loopCount = childClass.interfaceNamesToImplement.count + 1;
				listOfparentClassName.add(childClass.classNameToExtend);
				for (int i=0; i<childClass.interfaceNamesToImplement.count; i++) {
					listOfparentClassName.add(
							childClass.interfaceNamesToImplement.getItem(i));
				}
			}
		}
		
		if (childClass.listOfVarParamsInherited==null) {
			childClass.listOfVarParamsInherited = new ArrayListIReset(15);
		}
		else {
			childClass.listOfVarParamsInherited.reset2();
		}
		if (childClass.listOfFunctionParamsInherited==null) {
			childClass.listOfFunctionParamsInherited = new ArrayListIReset(10);
		}
		else {
			childClass.listOfFunctionParamsInherited.reset2();
		}
		
		int k;
		for (k=0; k<loopCount; k++) {
			parentClassName = listOfparentClassName.getItem(k);
			
			// parentClassName이 shortname일수도 있으므로 getFullNameType2()를 써야한다.
			// 왜냐하면 FindAllClassesAndItsMembers2_sub()에서 findClass(), findInterface()의 
			// getFullNameType()호출은 파일 내 정의 클래스들이 아직 읽혀지지 않을수도 있기 때문이다.
			parentClassName = this.getFullNameType2(mBuffer, parentClassName, childClass.classNameIndex());
			childClass.hasInherited = true; 
			
			//while (parentClassName!=null && parentClassName.equals("")==false) {
			if (parentClassName!=null && parentClassName.equals("")==false) {
				
				
				// recursive 호출이므로 java.lang.Object클래스부터 상속이 된다.
				if (parentClassName.equals("com.gsoft.common.gui.Control")) {
					int a;
					a=0;
					a++;
				}
				// loadClass()함수가 재귀적으로 FindMembersInherited()을 호출하므로
				// 상속관계의 가장 위인 java.lang.Object부터 상속이 된다.
				FindClassParams parentClass = CompilerHelper.loadClass(this, parentClassName);
				int i, j=0;
				if (parentClass!=null) {
					if (this.getShortName(parentClass.name).equals("Control") && 
							parentClass.loadWayOfFindClassParams==LoadWayOfFindClassParams.Start2) {
						int a;
						a=0;
						a++;
					}
					
					
					if (parentClass.name.equals(childClass.classNameToExtend) ||
							parentClass.name.equals("java.lang.object") ||
							parentClass.name.equals("java.lang.Object")) {
						// parentClass가 class일 경우
						//if (childClass.classToExtend==null) { 
							// 한번 초기화만 가능, 그렇지 않으면 java.lang.Object가 되어버린다.
							childClass.classToExtend = parentClass;
							if (this.getShortName(childClass.classNameToExtend).equals("EditableDialog")) {
								int a;
								a=0;
								a++;
							}
						//}
					}
					else {
						// parentClass가 인터페이스일 경우
						if (childClass.interfacesToImplement==null) {
							childClass.interfacesToImplement = new ArrayList(3);								
						}
						childClass.interfacesToImplement.add(parentClass);
					}
					
					
					// parentClass 에 정의되어 있고 childClass에도 중복해서 정의된 변수도
					// 모두 상속된다.
					ArrayListIReset vars = null;				
					for (j=0; j<2; j++) {
						if (j==0) vars = parentClass.listOfVariableParams;
						else vars = parentClass.listOfVarParamsInherited;
						if (vars==null) continue;
						
						for (i=0; i<vars.count; i++) {
							FindVarParams var = (FindVarParams) vars.getItem(i);
							if (CompilerHelper.hasInherittedMember(childClass, var, null))
								continue;
							if (var.fieldName!=null && var.fieldName.equals("bounds")) {
								int a;
								a=0;
								a++;
							}
							if (var.isThis || var.isSuper) continue;
							if (var.accessModifier==null) {
								childClass.listOfVarParamsInherited.add(var);
							}
							else {
								// final 멤버도 상속이 된다.
								//if (var.accessModifier.isFinal) continue;
								if (var.accessModifier.accessPermission==null) {
									childClass.listOfVarParamsInherited.add(var);
								}
								if (var.accessModifier.accessPermission!=null && 
									var.accessModifier.accessPermission!=AccessPermission.Private) {
									childClass.listOfVarParamsInherited.add(var);
								}
							}
						}//for (i=0; i<vars.count; i++) {
					}//for (j=0; j<2; j++) {
					
					ArrayListIReset funcs;
					for (j=0; j<2; j++) {
						// 부모클래스의 메서드부터 상속하고 
						// 부모클래스가 상속받은 메서드를 그 다음에 상속한다.
						if (j==0) funcs = parentClass.listOfFunctionParams;
						else funcs = parentClass.listOfFunctionParamsInherited;
						if (funcs==null) continue;
						
						for (i=0; i<funcs.count; i++) {
							FindFunctionParams func = (FindFunctionParams) funcs.getItem(i);
							if (func.name!=null && func.name.equals("getClass")) {
								int a;
								a=0;
								a++;
							}
							if (CompilerHelper.hasInherittedMember(childClass, null, func))
								continue;
							if (func.accessModifier==null) {
								childClass.listOfFunctionParamsInherited.add(func);
							}
							else {
								// final 멤버도 상속이 된다.
								//if (func.accessModifier.isFinal) continue;								
								
								FindFunctionParams funcOfChild = childClass.hasFunc(func);
								if (funcOfChild!=null) {
									funcOfChild.overridedFindFunctionParams = func;
								}
								if (func.accessModifier.isAbstract) {
									// 현재 클래스가 클래스이고 추상 메서드를 상속할 경우 childClass는 그 메서드를 구현하고 있어야 한다.
									// 현재 클래스가 인터페이스이면 에러를 발생시키지 않는다.
									if (funcOfChild==null && childClass.isInterface==false) {
										errors.add(new Error(childClass.compiler, childClass.classNameIndex(), childClass.classNameIndex(), 
												childClass.name + " class don't have a method " + func.name + "()" + " of " + parentClass.name + ". You must implement the abstract method."));
									}
								}
								if (func.accessModifier.accessPermission==null) {
									childClass.listOfFunctionParamsInherited.add(func);
								}
								if (func.accessModifier.accessPermission!=null && 
									func.accessModifier.accessPermission!=AccessPermission.Private) {
									childClass.listOfFunctionParamsInherited.add(func);
								}
							}
						}//for (i=0; i<funcs.count; i++) {
					}//for (j=0; j<2; j++) {
					
				}//if (parentClass!=null) {
			}//if (parentClassName!=null && parentClassName.equals("")==false) {
		}//for k
		
		
	}
	
	
	/** class Stack<T> {<br>
	 * 		T Data;<br>
	 *  	void push(T data) {<br>
	 *  	}<br>
	 * }<br>
	 * 에서 변수 Data와 data는 getFullNameType()에서 null을 리턴하므로
	 * 그것의 템플릿 타입이름인 T를 리턴한다.
	 * @param var
	 * @return
	 */
	String isTemplateType(FindVarParams var) {
		Object parent = var.parent;
		String typeName = this.getFullName(this.mBuffer, var.typeStartIndex(), var.typeEndIndex()).str;
		while(true) {
			if (parent==null) return null;
			if (parent instanceof FindClassParams) {
				FindClassParams c = (FindClassParams) parent;
				if (c.template!=null) {
					if (c.template.typeNameToChange!=null) {
						if (c.template.typeNameToChange.equals(typeName)) 
							return typeName;
					}
				}
				parent = c.parent;
			}
			else if (parent instanceof FindFunctionParams) {
				FindFunctionParams f = (FindFunctionParams) parent;
				parent = f.parent;
			}
		}
	}
	
	
	/** 모든 타입선언문들에서 타입이름이 short name만을 대상으로 하여
	 * (full name은 findMemberUsesUsingNameSpace()에서 클래스를 로드하므로) 
	 * import된 클래스인지, 파일에서 정의하고 있는 클래스인지, 같은 패키지 클래스인지를 검사하여 
	 * result로 리턴한다. 또한 int, char 등과 같은 타입들도 제외한다.(getFullnameType()을 호출하여) 
	 * Integer, Char, Exception 등과 같은 java.lang안의 클래스들도 여기서 찾아낼수 있다. 
	 * 여기에서 타입이 결정이 안된 클래스들은 다음에서 정해진다. 타입선언시 내부클래스가 있는 경우이다. 
	 * java.lang안의 클래스들이나 같은 패키지내 클래스들이나 import java.util.*에서 타입선언시 내부클래스가 있는 경우이다.
	 * Character.Subset s;과 같은 java.lang안의 내부클래스들은 findMemberUsesUsingNamespace_sub을 거쳐서 confirmTypeName에서 정해진다.
	 * 같은 패키지내 클래스들은 findMemberUsesUsingNamespace_sub에서 정해진다.<br>
	 * 대체적인 풀타입이름이 정해진다. 자신이 정의하는 클래스의 풀네임도 여기서 정해진다.
	 * (FindAllClassesAndItsMembers2_sub()호출이후이므로)
	 * @param src
	 * @param listOfMemberVarDecls : 파일안 모든 멤버변수선언리스트
	 * @param listOfLocalVarDecls : 파일안 모든 지역변수선언리스트
	 * @param result : 배열을 제외한 클래스 풀 이름
	 */
	public void FindClassesFromTypeDecls(Compiler compiler, ArrayListString result) {
		int i, j;
		boolean isClassInFile = false;
		
		// 상속클래스이름을 정확한 fullname으로 바꿔준다.
		for (i=0; i<compiler.mlistOfAllDefinedClasses.count; i++) {
			FindClassParams c = (FindClassParams) compiler.mlistOfAllDefinedClasses.getItem(i);
			String fullname;
			if (c.startIndexOfClassNameToExtend()==-1 && c.endIndexOfClassNameToExtend()==-1)
				fullname = "java.lang.Object";
			else 
				fullname = getFullNameType(compiler, c.startIndexOfClassNameToExtend(), c.endIndexOfClassNameToExtend());
			c.classNameToExtend = fullname;
		}
		
		//구현인터페이스이름을 정확한 fullname으로 바꿔준다.
		for (i=0; i<compiler.mlistOfAllDefinedClasses.count; i++) {
			FindClassParams c = (FindClassParams) compiler.mlistOfAllDefinedClasses.getItem(i);
			if (c.listOfStartIndexOfInterfaceNamesToImplement!=null) {
				for (j=0; j<c.listOfStartIndexOfInterfaceNamesToImplement.count; j++) {					
					IndexForHighArray startIndex = (IndexForHighArray) c.listOfStartIndexOfInterfaceNamesToImplement.getItem(j);
					IndexForHighArray endIndex = (IndexForHighArray) c.listOfEndIndexOfInterfaceNamesToImplement.getItem(j);
					String fullname = getFullNameType(compiler, startIndex.index(), endIndex.index());
					c.interfaceNamesToImplement.list[j] = fullname;
				}
			}
		}
		
		int len = compiler.mlistOfAllMemberVarDeclarations.getCount();
		for (i=0; i<len; i++) {
			isClassInFile = false;
			FindVarParams var = (FindVarParams) compiler.mlistOfAllMemberVarDeclarations.getItem(i);
			if (var.varNameIndex()==10279) {
				int a;
				a=0;
				a++;
			}
			if (var.isSuper) {
				int a;
				a=0;
				a++;
			}
			if (var.fieldName.equals("stack2")) {
				int a;
				a=0;
				a++;
			}
			else if (var.fieldName.equals("textColor")) {
				int a;
				a=0;
				a++;
			}
			String typeName;
			if (var.isThis) {
				typeName = ((FindClassParams)var.parent).name;
			}
			else if (var.isSuper) {
				typeName = ((FindClassParams)var.parent).classNameToExtend;
			}
			else {
				//typeName = var.getType(src, var.typeStartIndex(), var.typeEndIndex());
				typeName = getFullNameType(compiler, var.typeStartIndex(), var.typeEndIndex());
			}
			if (typeName==null) {
				String templateTypeName = isTemplateType(var);
				typeName = templateTypeName;
			}
			var.typeName = typeName;
			if (typeName==null) {
				continue;
			}
			if (typeName.equals("java.lang.Exception")) {
				int a;
				a=0;
				a++;
			}
			//result.add(typeName);
			String typeName2 = CompilerHelper.getArrayElementType(typeName);
			if (typeName.equals(typeName2)==false) {
				result.add(typeName2);
			}
			else {
				result.add(typeName);
			}
		}
		
		int len2 = compiler.mlistOfAllLocalVarDeclarations.getCount();
		for (i=0; i<len2; i++) {
			isClassInFile = false;
			FindVarParams var = (FindVarParams) compiler.mlistOfAllLocalVarDeclarations.getItem(i);
			if (var.fieldName.equals("stack")) {
				int a;
				a=0;
				a++;
			}
			else if (var.fieldName.equals("scrollMode")) {
				int a;
				a=0;
				a++;
			}
			if (var.varNameIndex()==2721) {
				int a;
				a=0;
				a++;
			}
			//String typeName = var.getType(src, var.typeStartIndex(), var.typeEndIndex());
			String typeName = getFullNameType(compiler, var.typeStartIndex(), var.typeEndIndex());
			var.typeName = typeName;
			if (typeName==null) {
				int a;
				a=0;
				a++;
				continue;
			}
			if (typeName.equals("com.gsoft.common.Compiler.CodeString[]")) {
				int a;
				a=0;
				a++;
			}
			else if (typeName.equals("android.graphics.Canvas")) {
				int a;
				a=0;
				a++;
			}
			//result.add(typeName);
			try{
			String typeName2 = CompilerHelper.getArrayElementType(typeName);
			if (typeName.equals(typeName2)==false) {
				result.add(typeName2);
			}
			else {
				result.add(typeName);
			}
			}catch(Exception e) {
				int a;
				a=0;
				a++;
			}
		}
		
		for (i=0; i<compiler.mlistOfAllFunctions.count; i++) {
			isClassInFile = false;
			FindFunctionParams func = (FindFunctionParams) compiler.mlistOfAllFunctions.getItem(i);
			//String typeName = func.getReturnType(src, func.returnTypeStartIndex, func.returnTypeEndIndex);
			if (func.name.equals("File")) {
				int a;
				a=0;
				a++;
			}
			String typeName = getFullNameType(compiler, func.returnTypeStartIndex(), func.returnTypeEndIndex());
			if (typeName!=null && typeName.contains("EditRichText.Character")) {
				int a;
				a=0;
				a++;
			}
			if (func.returnTypeStartIndex()==-1 && func.returnTypeEndIndex()==-1) {
				// 생성자의 경우는 이미 정해져 있다.
				if (func.isConstructor) {
					// addsNewly가 true일 경우 즉 getSourceFilePath()에서 소스파일을 찾지 못해서
					// getSourceFilePathAddingComGsoftCommon()으로 소스파일을 찾게 된 경우
					// fullName에서 com.gsoft.common을 제거한 이름을 새로운 fullName으로 한다.
					// 즉 새로이 제공된 소스파일에 있는 클래스로 클래스파일을 읽기 실패한 클래스를
					// 대체하게 된다.
					// fullName이 com.gsoft.common.java.lang.String일 경우 
					// 새로운 fullName은 java.lang.String이 된다.
					// 여기에선 생성자의 리턴 타입 이름을 원래 이름으로 바꿔 준다.
					if (((FindClassParams)func.parent).addsNewly) {
						int len4 = new String(FindClassParams.prefixInCaseOfAddsNewly).length();
						int founded = func.returnType.indexOf(FindClassParams.prefixInCaseOfAddsNewly);
						if (founded!=-1) {
							func.returnType = func.returnType.substring(len4, func.returnType.length());
							
						}
					}
					typeName = func.returnType;
					// 생성자이므로 반환값이 없다. 그러므로 continue;
					// continue;
				}//if (func.isConstructor) {
				else if (func.isStaticBlock) {
					// static 블록이므로 반환값이 없다. 그러므로 continue;
					continue;
				}
			}//if (func.returnTypeStartIndex==-1 && func.returnTypeEndIndex==-1) {
			else {
				func.returnType = typeName;
			}
			if (typeName==null) {
				int a;
				a=0;
				a++;
				continue;
			}
			if (this.getShortName(typeName).equals("Pair")) {
				int a;
				a=0;
				a++;
			}
			if (typeName.equals("com.gsoft.common.Compiler.CodeString[]")) {
				int a;
				a=0;
				a++;
			}
			//result.add(typeName);
			String typeName2 = null;
			try {
			typeName2 = CompilerHelper.getArrayElementType(typeName);
			}catch(Exception e) {
				int a;
				a=0;
				a++;
				e.printStackTrace();
			}
			
			if (typeName.equals(typeName2)==false) {
				result.add(typeName2);
			}
			else {
				result.add(typeName);
			}
		}
		
		for (i=0; i<compiler.mlistOfAllConstructor.count; i++) {
			isClassInFile = false;
			Constructor constructor = (Constructor) compiler.mlistOfAllConstructor.getItem(i);
			if (constructor.indexOfNew()==83450) {
				int a;
				a=0;
				a++;
			}
			//String typeName = constructor.getType(src, constructor.startIndexExceptPair, constructor.endIndexExceptPair);
			String typeName = getFullNameType(compiler, constructor.startIndexExceptPair(), constructor.endIndexExceptPair());
			constructor.fullname = typeName;
			if (typeName==null) {
				int a;
				a=0;
				a++;
				continue;
			}
			if (this.getShortName(typeName).equals("Pair")) {
				int a;
				a=0;
				a++;
			}
			if (typeName.equals("com.gsoft.common.Compiler.CodeString[]")) {
				int a;
				a=0;
				a++;
			}
			//result.add(typeName);
			String typeName2 = CompilerHelper.getArrayElementType(typeName);
			if (typeName.equals(typeName2)==false) {
				result.add(typeName2);
			}
			else {
				result.add(typeName);
			}
		}
	}
	
	public void FindAllClassesAndItsMembers2(HighArray_CodeString src, int startIndex, int endIndex, 
			ArrayListIReset result, Language lang, ModeAllOrUpdate modeAllOrUpdate) {
		try{
			
			if (lang==Language.Java) {
				this.loadLibraries2(this);
				
				RegisterPackageName(src);
				
				RegisterImportedClasses(src);
				
				loadImportStar();
				
			}
			
			
		try {
		mlistOfAllVarUsesHashed = new Hashtable2(src, 50, 20);
		
		CommonGUI.loggingForMessageBox.setText(true, "FindAllClassesAndItsMembers2_sub()...", false);
		CommonGUI.loggingForMessageBox.setHides(false);
		Control.view.postInvalidate();
		
		if (FileHelper.getFilename(filename).equals("ConnectDialog.java")) {
			int a;
			a=0;
			a++;
		}
				
		FindAllClassesAndItsMembers2_sub(startIndex, endIndex, result, ModeAllOrUpdate.All);
				
		
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
		
		for (int i=0; i<mlistOfAllDefinedClasses.count; i++) {
			FindClassParams classParams = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
			classParams.loadWayOfFindClassParams = LoadWayOfFindClassParams.Start2; 
		}
		
		int i, j;		
		try {
		// 외부라이브러리처럼 클래스의 name을 fullname으로 정해준다.
		// mlistOfAllClasses이 static이므로 mlistOfAllDefinedClasses안의 클래스들을 대상으로 한다.
		// 즉, 여러 파일을 load할 경우 src가 달라지는 문제가 발생할 수 있다.
		for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
			FindClassParams c = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
			if (c.name==null) {
				String fullName;
				if (c.isEventHandlerClass==false) {
					String fullNameExceptPackageName1 = getFullNameExceptPackageName(src, c);
					String fullNameExceptPackageName2 = fullNameExceptPackageName1.substring(0, fullNameExceptPackageName1.length()-1);
					fullName = packageName + "." + fullNameExceptPackageName2;
				}
				else {
					fullName = getFullNameType(this, c.startIndexOfEventHandlerName(), c.endIndexOfEventHandlerName()) + "_EventHandler";
				}
				c.name = fullName;
				CompilerHelper.makeNoneStaticDefaultConstructorIfConstructorNotExist(this, c);
				//mlistOfAllClassesThatCanUseShortName.add(c);
			}
			// 여기서 mlistOfAllClasses에 넣어준다. 
			// import한 기존 클래스가 아니라 새로 읽어들인 클래스로 바꿔준다.
			boolean found = false;
			for (j=0; j<mlistOfAllClasses.count; j++) {
				FindClassParams classParams = (FindClassParams) mlistOfAllClasses.getItem(j);
				if (classParams.name.equals(c.name)) {
					/*if (classParams.loadWayOfFindClassParams==LoadWayOfFindClassParams.None ||
						classParams.loadWayOfFindClassParams==LoadWayOfFindClassParams.ByteCode ||
						classParams.loadWayOfFindClassParams==LoadWayOfFindClassParams.Start_OnlyClass ||
						classParams.loadWayOfFindClassParams==LoadWayOfFindClassParams.Start_OnlyInterface) {
						mlistOfAllClasses.list[j] = c;
						mlistOfAllClassesHashed.replace(c);
						found = true;
						//break;
					}*/
					mlistOfAllClasses.list[j] = c;
					mlistOfAllClassesHashed.replace(c);
					found = true;
				}
			}
			
			if (!found) {				
				mlistOfAllClasses.add(c);
				mlistOfAllClassesHashed.input(c);
			}
			
			
		}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
		
		
		CommonGUI.loggingForMessageBox.setText(true, "FindClassesFromTypeDecls()", false);
		CommonGUI.loggingForMessageBox.setHides(false);
		Control.view.postInvalidate();
		
		// 상속을 하기전에 변수, 메서드 등의 타입을 확실히 한다. 
		// TreeNodeButton은 Button을 상속하는데 Button은 TreeNodeButton 다음에 정의되므로
		// FindMembersInherited()에서 Button을 loadClass()할때 Button은 캐시에 등록되어 있고
		// 그것의 멤버들을 상속하여 중복 변수, 중복 메서드를 체크 할때(CompilerHelper.hasInherittedMember()) 
		// FindClassesFromTypeDecls()가 FindMembersInherited() 다음에 오면 아직 타입이 정의되어
		// 있지 않으므로 NullPointerException이 발생한다.
		ArrayListString namesOfClassesFromTypeDecls = new ArrayListString(10);
		FindClassesFromTypeDecls(this, namesOfClassesFromTypeDecls);
		
		
		
		// 같은 파일에 정의된 클래스들의 이름이 fullname으로 확실하게 정한 이후에 상속한다.
		for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
			FindClassParams classParams = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
			if (this.getShortName(classParams.name).equals("OnTouchListener2")) {
				int a;
				a=0;
				a++;
			}
			FindMembersInherited(classParams);
			if (classParams.hasVarInheritted("textColor")) {
				int a;
				a=0;
				a++;
			}
		}
		
		if (lang != Language.C) { // c의 경우는 this를 넣지 않는다.
			for (i=0; i<mlistOfClass.count; i++) {
				FindClassParams classParams = (FindClassParams) mlistOfClass.getItem(i);
				//if (classParams.isEnum) continue;
				inputThisAndSuperToClassAndInputVarUsesToHashtable(src, classParams, modeAllOrUpdate);
			}
		}
		
		
		
		findTypeCasts(src, this.mlistOfAllVarUses);
		
		mSamePackageClasses = CompilerHelper.getSamePackageClasses(packageName, true);
		
		
		
		
		// 먼저 테스트를 하기 위해 아래부분과 중복되어 있다.
		//findMemberUsesUsingNamespace(src, mlistOfAllVarUses);
		//findMemberUsesUsingNamespace_library(src, mlistOfAllVarUses);
		
		
		
		findAllVarUsingOfTemplate(src);
		
		
		
		CommonGUI.loggingForMessageBox.setText(true, "findAllVarUsingAndChangeMemberDeclColor_caller_Local()", false);
		CommonGUI.loggingForMessageBox.setHides(false);
		Control.view.postInvalidate();
		
		findAllLocalVarUsing_caller_Local(src, 
				mlistOfAllMemberVarDeclarations, mlistOfAllLocalVarDeclarations);
		
		
		
		
		
		
		CommonGUI.loggingForMessageBox.setText(true, "findMemberUsesUsingNamespace()", false);
		CommonGUI.loggingForMessageBox.setHides(false);
		Control.view.postInvalidate();
		//Thread.sleep(3000);
		
		
		// import하지 않고 com.gsoft.common.FileHelper.getFileName()호출을 하는 경우와 같이
		// 이런 경우는 import문에서 클래스가 로드되는 것이 아니라 호출시 로드가 된다.
		try {
			findMemberUsesUsingNamespace(src, mlistOfAllVarUses);
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
		
		
		CommonGUI.loggingForMessageBox.setText(true, "findTypeOfAssignments()", false);
		CommonGUI.loggingForMessageBox.setHides(false);
		Control.view.postInvalidate();
		
		findTypeOfAssignments(src);
		
		findTypeOfSpecialStatements(this);
		
		findTypeOfConditionsAndConvertConditionsToPostfix(src);
		
		// 모든 클래스에 static필드를 초기화하는 생성자를 만든다.
		for (i=0; i<this.mlistOfAllDefinedClasses.count; i++) {
			FindClassParams classParams = (FindClassParams) this.mlistOfAllDefinedClasses.getItem(i);
			if (CompilerHelper.requiresStaticConstructor(classParams)) {
				CompilerHelper.makeStaticDefaultConstructorIfStaticDefaultConstructorNotExist(this, classParams);
				
			}
		}
		
		for (i=0; i<this.mlistOfSynchronizedBlocks.count; i++) {
			FindControlBlockParams synchronizedBlock = (FindControlBlockParams) mlistOfSynchronizedBlocks.getItem(i);
			if (synchronizedBlock.listOfStatements.count>0) {
				FindStatementParams statement = (FindStatementParams) synchronizedBlock.listOfStatements.getItem(0);
				// 가짜 try-catch블록을 이미 넣었으면 continue
				if (isTry_CatchShield(statement)) continue;
			}
			this.putTryCatchShieldToSynchronizedOrFunction(synchronizedBlock);
		}
		
		putAssignsToConstructorOfAllDefinedClasses();
		
		
		for (i=0; i<this.mlistOfAllFunctions.count; i++) {
			FindFunctionParams func = (FindFunctionParams) mlistOfAllFunctions.getItem(i);
			/*if (func.isConstructor) {
				if (func.name.equals("Menu")) {
					int a;
					a=0;
					a++;
				}
				CompilerHelper.makeFuncCallToDefaultConstructorOfSuperClass(this, func);				
			}*/
			if (func.listOfStatements.count>0) {
				FindStatementParams statement = (FindStatementParams) func.listOfStatements.getItem(0);
				// 가짜 try-catch블록을 이미 넣었으면 continue
				if (isTry_CatchShield(statement)) continue;
			}
			this.putTryCatchShieldToSynchronizedOrFunction(func);
		}
		
		
		changeKeywordAndConstantColor(src);
		
		changeVarUseColor(src, mlistOfClass);
		
		checkControlBlocks(src);
		
		
		checkVarUse(src);
		
		checkLocalVar(src, mlistOfAllFunctions);
		
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
		
		
	}
	
	/** mlistOfAllDefinedClasses에서 클래스의 멤버 초기화문을 찾아서 
	 * 그 클래스의 생성자의 앞부분에 넣는다.
	 * static멤버 초기화 부분은 따로 넣는다.*/
	void putAssignsToConstructorOfAllDefinedClasses() {
		int i;
		for (i=0; i<this.mlistOfAllDefinedClasses.count; i++) {
			try {
			FindClassParams classParams = (FindClassParams) this.mlistOfAllDefinedClasses.getItem(i);
			
			ArrayListIReset listOfAssignsOfClass_instance = new ArrayListIReset(classParams.listOfStatements.count);
			listOfAssignsOfClass_instance.resizeInc = 100;
			ArrayListIReset listOfAssignsOfClass_static = new ArrayListIReset(classParams.listOfStatements.count);
			listOfAssignsOfClass_static.resizeInc = 50;
			
			ArrayListIReset listOfAssignsOfClass_instance_backup = null;
			ArrayListIReset listOfAssignsOfClass_static_backup = null;
			
			// 클래스의 초기화 문장들을 넣는다.
			for (int k=0; k<classParams.listOfStatements.count; k++) {
				FindStatementParams statement = (FindStatementParams) classParams.listOfStatements.getItem(k);
				if (statement instanceof FindAssignStatementParams) {
					FindAssignStatementParams assign = (FindAssignStatementParams) statement;
					FindVarParams var = assign.lValue.varDecl;
					if (var.accessModifier!=null && var.accessModifier.isStatic) {
						listOfAssignsOfClass_static.add(assign);
					}
					else {
						listOfAssignsOfClass_instance.add(assign);
					}
				}
			}
			listOfAssignsOfClass_instance_backup = this.getClone(listOfAssignsOfClass_instance);
			listOfAssignsOfClass_static_backup =  this.getClone(listOfAssignsOfClass_static);
			listOfAssignsOfClass_instance_backup.resizeInc = 100;
			listOfAssignsOfClass_static_backup.resizeInc = 50;
			
			for (int m=0; m<classParams.listOfConstructor.count; m++) {
				FindFunctionParams func = (FindFunctionParams) classParams.listOfConstructor.getItem(m);
				if (func.isConstructor) {
					if (func.functionNameIndex()==660) {
						int a;
						a=0;
						a++;
					}
					if (func.isConstructorThatInitializesStaticFields==false) {
						int n = 0;
						// super()를 가장 처음에 넣는다.
						if (CompilerHelper.hasFuncCallToConstructorOfSuperClass(this, func)) {
							listOfAssignsOfClass_instance.insert(0, func.listOfStatements.getItem(0));
							n = 1;
						}
						// 생성자의 원래 문장들을 그 다음에 넣는다.
						for (; n<func.listOfStatements.count; n++) {
							FindStatementParams s = (FindStatementParams) func.listOfStatements.getItem(n);
							if (s instanceof FindIndependentFuncCallParams) {
								
							}
							listOfAssignsOfClass_instance.add(s);
						}
						func.listOfStatements = listOfAssignsOfClass_instance;
					}
					else { 
						// static constuctor
						for (int n=0; n<func.listOfStatements.count; n++) {
							listOfAssignsOfClass_static.add(func.listOfStatements.getItem(n));
						}
						func.listOfStatements = listOfAssignsOfClass_static;
					}
				}
				listOfAssignsOfClass_instance = listOfAssignsOfClass_instance_backup;
				listOfAssignsOfClass_static = listOfAssignsOfClass_static_backup;
			}//for (int m=0; m<classParams.listOfConstructor.count; m++) {
			
			}catch(Exception e) {
				e.printStackTrace();
				
			}
		}//for (i=0; i<this.mlistOfAllDefinedClasses.count; i++) {
	}
	
	ArrayListIReset getClone(ArrayListIReset arr) {
		int i;
		ArrayListIReset r = new ArrayListIReset(arr.count);
		for (i=0; i<arr.count; i++) {
			r.add(arr.getItem(i));
		}
		return r;
	}
	
	/** findTypeOfConditionsAndConvertConditionsToPostfix()에서 호출하여 for loop의 조건문의 인덱스를 리턴한다.
	 * @return : 시작인덱스는 Point.x, 끝인덱스는 Point.y*/
	Point getConditionIndicesOfForLoop(HighArray_CodeString src, int indexOfLeftParenthesis, int indexOfRightParenthesis) {
		int i;
		int startIndex = -1, endIndex = -1;
		
		startIndex = Skip(src, false, ";", indexOfLeftParenthesis+1, indexOfRightParenthesis-1);
		if (startIndex==indexOfRightParenthesis) {
			errors.add(new Error(this, indexOfLeftParenthesis, indexOfRightParenthesis, "Condition of for loop not valid."));
			return null;
		}
		startIndex++;
		
		endIndex = Skip(src, false, ";", startIndex, indexOfRightParenthesis-1);
		if (endIndex==indexOfRightParenthesis) {
			errors.add(new Error(this, indexOfLeftParenthesis, indexOfRightParenthesis, "Condition of for loop not valid."));
			return null;
		}
		endIndex--;	
		
		Point r = new Point();
		r.x = startIndex;
		r.y = endIndex;
		return r;
	}
	
	
	boolean isBoolean(String typeFullName) {
		if (typeFullName==null) return false;
		if (typeFullName.length()!=7) return false;
		
		if (typeFullName.charAt(0)!='b') return false;
		if (typeFullName.charAt(1)!='o') return false;
		if (typeFullName.charAt(2)!='o') return false;
		if (typeFullName.charAt(3)!='l') return false;
		if (typeFullName.charAt(4)!='e') return false;
		if (typeFullName.charAt(5)!='a') return false;
		if (typeFullName.charAt(6)!='n') return false;
		
		return true;
	}
		
	/** if, while, for 등의 조건문을 POSTFIX로 변환하고 타입을 결정한다*/
	void findTypeOfConditionsAndConvertConditionsToPostfix(HighArray_CodeString src) {
		int i;
		for (i=0; i<mlistOfAllControlBlocks.count; i++) {
			try {
			FindControlBlockParams controlBlock = 
					(FindControlBlockParams) mlistOfAllControlBlocks.getItem(i);
			if (controlBlock.catOfControls==null) continue;
			
			if (controlBlock.catOfControls.category==CategoryOfControls.Control_for) {
				Point p = getConditionIndicesOfForLoop(src, controlBlock.indexOfLeftParenthesis(), controlBlock.indexOfRightParenthesis());
				if (p!=null) {
					FindFuncCallParam funcCall = new FindFuncCallParam(this, p.x, p.y);
					funcCall.typeFullName = this.getTypeOfExpression(src, funcCall);
					controlBlock.funcCall = funcCall;
					if (funcCall.typeFullName==null || funcCall.typeFullName.equals("boolean")==false) {
						errors.add(new Error(this, controlBlock.nameIndex(), controlBlock.nameIndex(), "Condition of control block must be boolean. : " + funcCall.typeFullName));
					}
				}
			}
			else if (controlBlock.catOfControls.category==CategoryOfControls.Control_switch) { // switch문의 경우
				FindFuncCallParam funcCall = new FindFuncCallParam(this, controlBlock.indexOfLeftParenthesis()+1, controlBlock.indexOfRightParenthesis()-1);
				funcCall.typeFullName = this.getTypeOfExpression(src, funcCall);
				controlBlock.funcCall = funcCall;
				if (funcCall.typeFullName!=null && (funcCall.typeFullName.equals("int") || funcCall.typeFullName.equals("char") ||
						funcCall.typeFullName.equals("byte") || funcCall.typeFullName.equals("short") || funcCall.typeFullName.equals("long"))) {
				}
				else {
					errors.add(new Error(this, controlBlock.nameIndex(), controlBlock.nameIndex(), "Condition of control block must be int. : " + funcCall.typeFullName));
				}
			}
			else if (controlBlock.catOfControls.category==CategoryOfControls.Control_case) { // case문의 경우
				FindFuncCallParam funcCall = new FindFuncCallParam(this, controlBlock.indexOfLeftParenthesis(), controlBlock.indexOfRightParenthesis()-1);
				funcCall.typeFullName = this.getTypeOfExpression(src, funcCall);
				controlBlock.funcCall = funcCall;
				if (funcCall.typeFullName!=null && (funcCall.typeFullName.equals("int") || funcCall.typeFullName.equals("char") ||
						funcCall.typeFullName.equals("byte") || funcCall.typeFullName.equals("short") || funcCall.typeFullName.equals("long"))) {
				}
				else {
					errors.add(new Error(this, controlBlock.nameIndex(), controlBlock.nameIndex(), "Condition of control block must be int. : " + funcCall.typeFullName));
				}
			}
			else if (controlBlock.catOfControls.category==CategoryOfControls.Control_else) { // else문의 경우
				
			}
			else if (controlBlock.catOfControls.category!=CategoryOfControls.Control_case) {
				if (controlBlock.nameIndex()==2245) {
					int a;
					a=0;
					a++;
				}
				FindFuncCallParam funcCall = new FindFuncCallParam(this, controlBlock.indexOfLeftParenthesis()+1, controlBlock.indexOfRightParenthesis()-1);
				funcCall.typeFullName = this.getTypeOfExpression(src, funcCall);
				controlBlock.funcCall = funcCall;
				if (funcCall.typeFullName==null || funcCall.typeFullName.equals("boolean")==false) {
					errors.add(new Error(this, controlBlock.nameIndex(), controlBlock.nameIndex(), "Condition of control block must be boolean. : " + funcCall.typeFullName));
				}
			}
			}catch (Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
			
		}
	}
	
	/** return expression; 과 같이 expression의 타입을 찾아서 
	 * FindSpecialStatementParams.funcCall에 넣는다.*/
	void findTypeOfSpecialStatements(Compiler compiler) {
		int i;
		for (i=0; i<this.mlistOfSpecialStatement.count; i++) {
			FindSpecialStatementParams statement = (FindSpecialStatementParams) mlistOfSpecialStatement.getItem(i);
			String keyword = compiler.mBuffer.getItem(statement.kewordIndex()).str; 
			if (keyword.equals("return") || keyword.equals("throw")) {
				if (statement.funcCall==null) {
					int startIndex = statement.kewordIndex()+1;
					int endIndex = statement.endIndex()-1;
					if (startIndex<=endIndex) {
						FindFuncCallParam funcCall = new FindFuncCallParam(compiler, startIndex, endIndex);
						funcCall.typeFullName = 
								compiler.getTypeOfExpression(compiler.mBuffer, funcCall);
						statement.funcCall = funcCall;
					}
				}
			}//if (keyword.equals("return") || keyword.equals("throw")) {
		}
	}
	
	void getTypeOfAssignment(HighArray_CodeString src, FindAssignStatementParams assign, AddCharReallyMode mode) {
		try {
		if (assign.lValue.index()==5382) {
			int a;
			a=0;
			a++;
		}
		FindFuncCallParam rValue = assign.rValue;
		
		String fullNameOfLValue = null;
		FindVarParams var = assign.lValue.varDecl;
		if (var==null) {
			int a;
			a=0;
			a++;
			return;
		}
		fullNameOfLValue = var.typeName;
		rValue.funcName = fullNameOfLValue; 
		
		if (fullNameOfLValue==null)
			fullNameOfLValue = var.getType(src, var.typeStartIndex(), var.typeEndIndex());
		if (fullNameOfLValue==null) return;
		
		if (assign.lValue.isArrayElement) {
			// colorButtons[k] = new Button(owner);에서 lValue는 colorButtons[k]이고 
			// 이것의 타입은 Control[]이다.
			fullNameOfLValue = CompilerHelper.getArrayElementType(fullNameOfLValue);
		}
		
		
		if (assign.arrayInitializer==null) {			
			CodeStringEx typeFullName = null;
			try{
				// rValue의 타입을 정한다.
			typeFullName = getTypeOfExpression(src, rValue);
			if (typeFullName!=null)  {
				assign.typeFullNameOfRValue = typeFullName.str;				
				rValue.typeFullName = typeFullName;
			}
			
			int startOfFindFuncCallParam = this.SkipBlank(src, false, assign.startIndex(), src.count-1);
			//int startOfFindFuncCallParam = this.getFullNameIndex(src, true, assign.lValue.index());
			int endOfFindFuncCallParam = assign.endIndex();
			if (CompilerHelper.IsSeparator(src.getItem(endOfFindFuncCallParam))) {
				endOfFindFuncCallParam = this.SkipBlank(src, true, 0, endOfFindFuncCallParam-1);
			}
			
			// 대입연산자들(=,+=,-=등)을 포함하여 타입을 정한다.
			FindFuncCallParam funcCallParam = new FindFuncCallParam(this, startOfFindFuncCallParam, endOfFindFuncCallParam);
			CodeStringEx csFullNameOfLValue = new CodeStringEx(fullNameOfLValue);
			csFullNameOfLValue.typeFullName = fullNameOfLValue;
			
			ArrayListIReset listOfTypes = new ArrayListIReset(2);				
			listOfTypes.add(csFullNameOfLValue);
			csFullNameOfLValue.operandOrOperator = csFullNameOfLValue;
			listOfTypes.add(typeFullName);
			if (typeFullName!=null) {
				typeFullName.operandOrOperator = typeFullName;
			}
			CodeStringEx csOperator = new CodeStringEx(assign.operator);
			
			CodeStringEx type = CompilerHelper.getTypeOfOperator(this, funcCallParam, listOfTypes, csOperator);
			funcCallParam.typeFullName = type;
			//this.getTypeOfExpression(src, funcCallParam);
			
			assign.funcCallParam = funcCallParam;
			
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				Log.e("error", "lValue name : "+assign.lValue.name);
				CompilerHelper.printMessage(CommonGUI.textViewLogBird, "lValue name : "+assign.lValue.name);
				int a;
				a=0;
				a++;
				
			}
		}
		else { // 할당문이 배열초기화문이면
			findTypesOfExpressionsOfArrayInitializer(src, assign, mode);
		}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			
		}
	}
	
	/** mlistOfAllAssignStatements에 대해서 모든 할당문들의 타입을 체크한다.*/
	void findTypeOfAssignments(HighArray_CodeString src) {
		int i;
		for (i=0; i<mlistOfAllAssignStatements.count; i++) {
			if (i==35) {
				int a;
				a=0;
				a++;
			}
			FindAssignStatementParams assign = 
				(FindAssignStatementParams) mlistOfAllAssignStatements.getItem(i);
			getTypeOfAssignment(src, assign, null);
		}
	}
	
	/** 처음 호출시에는 최상위 배열초기화문(FindArrayInitializerParams)이고, 
	 * 재귀적 호출시에는 원래 배열안의 중첩된 배열에 대해서 호출한다. 
	 * 배열초기화문의 차원수(dimension)와 원소 타입과 행과 열들의 길이(length)도 정해준다.<br>
	 * int[][] colors = {
    		{Color.BLACK, Color.WHITE, Color.RED}, 
    		{Color.YELLOW,Color.BLUE, Color.GREEN}
	}; <br>
	이 경우 2차원이고 2행 3열이다. */
	void findTypesOfExpressionsOfArrayInitializer(HighArray_CodeString src, FindAssignStatementParams assign, AddCharReallyMode mode) {
		
		FindVarParams var = assign.lValue.varDecl;
		assign.arrayInitializer.varUse = assign.lValue;
		assign.arrayInitializer.typeFullName = var.typeName;
		int dimension = CompilerHelper.getArrayDimension(this, var.typeName);
		String arrayOriginalType = CompilerHelper.getArrayElementType(var.typeName);
		
		//assign.arrayInitializer.dimension = 0; // 여러번 호출될 경우를 대비해서 초기화해준다.		
		findTypesOfExpressionsOfArrayInitializer_sub(src, assign.arrayInitializer, assign.arrayInitializer, arrayOriginalType, mode);
		
		assign.arrayInitializer.dimension = 0; // 여러번 호출될 경우를 대비해서 초기화해준다.
		if (assign.arrayInitializer.listOfLength!=null) assign.arrayInitializer.listOfLength.reset2();
		//getDimensionAndLengthOfArrayInitializer(src, assign.arrayInitializer, assign.arrayInitializer);
		getDimensionOfArrayInitializer(src, assign.arrayInitializer, assign.arrayInitializer);
		getLengthOfArrayInitializer(src, assign.arrayInitializer, assign.arrayInitializer);
		
				
		if (dimension!=assign.arrayInitializer.dimension) {
			errors.add(new Error(this, assign.lValue.index(), assign.lValue.index(), "invalid array dimension : "+dimension+"-"+assign.arrayInitializer.dimension));
		}
	}
	
	/** 배열초기화문의 길이를 정해준다.
	 * 트리의 모든 노드를 순회하면서 현재 배열의 길이가 최상위 노드에 있는 같은 깊이의 현재 길이보다 크면 바꿔주면서 
	 * 모든 깊이의 길이를 올바르게 정해준다.<br> 
	 * int[][][] colors2 = {<br>
    		{//a0<br>
	    		{Color.BLACK, Color.WHITE}, //a00<br>
	    		{Color.YELLOW,Color.BLUE, Color.GREEN} //a01<br>
    		},
    		 
    		{//a1<br>
        	    {Color.YELLOW,Color.BLUE, Color.GREEN} //a11<br>
        	}
	};//2면 2행 3열<br>
	colors2->a0->a00->Color.BLACK, Color.WHITE <br>
			   ->a01->Color.YELLOW,Color.BLUE, Color.GREEN <br>
	 	   ->a1->a11->Color.YELLOW,Color.BLUE, Color.GREEN <br>
	 	   @param upmost : 배열의 최상위노드
	 	   @param array : 처음 호출시는 최상위노드이고 재귀적호출시에는 해당 배열 */
	void getLengthOfArrayInitializer(HighArray_CodeString src, FindArrayInitializerParams upmost, FindArrayInitializerParams array) {
		if (array.listOfFindArrayInitializerParams.count>0) { // 자식 배열초기화문이 있으면
			if (upmost.listOfLength==null) {
				upmost.listOfLength = new ArrayListInt(upmost.dimension);
				upmost.listOfLength.count = upmost.dimension;
			}
			if (upmost.listOfLength.list[array.depth] < array.listOfFindArrayInitializerParams.count) {
				upmost.listOfLength.list[array.depth] = array.listOfFindArrayInitializerParams.count;
			}
			int i;
			for (i=0; i<array.listOfFindArrayInitializerParams.count; i++) {
				FindArrayInitializerParams child = (FindArrayInitializerParams) array.listOfFindArrayInitializerParams.getItem(i);
				getLengthOfArrayInitializer(src, upmost, child);
			}			
		}
		else { // 수식들의 배열
			if (upmost.listOfLength==null) {
				upmost.listOfLength = new ArrayListInt(upmost.dimension);
				upmost.listOfLength.count = upmost.dimension;
			}
			if (upmost.listOfLength.list[array.depth] < array.listOfFindFuncCallParam.count) {
				upmost.listOfLength.list[array.depth] = array.listOfFindFuncCallParam.count;
			}
		}	
	}
	
	/** 배열초기화문의 차원을 정해서 최상위 노드에 그 값을 저장한다.
	 * @param upmost : 배열의 최상위노드
	 * @param array : 처음 호출시는 최상위노드이고 재귀적호출시에는 해당 배열*/
	void getDimensionOfArrayInitializer(HighArray_CodeString src, FindArrayInitializerParams upmost, FindArrayInitializerParams array) {
		if (array.listOfFindArrayInitializerParams.count>0) { // 자식 배열초기화문이 있으면
			upmost.dimension++;
			
			FindArrayInitializerParams child = (FindArrayInitializerParams) array.listOfFindArrayInitializerParams.getItem(0);
			getDimensionOfArrayInitializer(src, upmost, child);
		}
		else {
			upmost.dimension++;
		}
	}
	
	/** 현재 배열의 깊이를 리턴한다. 최상위 배열초기화문의 노드의 깊이는 0이다.*/
	int getDepth(FindArrayInitializerParams array) {
		int depth = 0;
		while (array.parent!=null) {			
			if (array.parent instanceof FindArrayInitializerParams) {
				array = (FindArrayInitializerParams) array.parent;
				depth++;
			}
			else break;
		}
		return depth;
	}
	
	
	/** findTypesOfExpressionsOfArrayInitializer()의 sub이다.<br>
	 * int[][] colors = {<br>
    		{Color.BLACK, Color.WHITE, Color.RED}, //a0[]<br>
    		{Color.YELLOW,Color.BLUE, Color.GREEN} //a1[]<br>
	};<br>
	colors->a0->Color.BLACK, Color.WHITE, Color.RED <br>
	 	  ->a1->Color.YELLOW,Color.BLUE, Color.GREEN<br>
	 	  <br>
	int[][][] colors2 = {<br>
    		{//a0<br>
	    		{Color.BLACK, Color.WHITE}, //a00<br>
	    		{Color.YELLOW,Color.BLUE, Color.GREEN} //a01<br>
    		},
    		 
    		{//a1<br>
        	    {Color.YELLOW,Color.BLUE, Color.GREEN} //a11<br>
        	}
	};//2면 2행 3열<br>
	colors2->a0->a00->Color.BLACK, Color.WHITE <br>
			   ->a01->Color.YELLOW,Color.BLUE, Color.GREEN <br>
	 	   ->a1->a11->Color.YELLOW,Color.BLUE, Color.GREEN <br>
	 	   @param upmost : 배열의 최상위노드
	 	   @param array : 처음 호출시는 최상위노드이고 재귀적호출시에는 해당 배열 
	 	   @param arrayOriginalType : int[][]일 경우 int*/
	public void findTypesOfExpressionsOfArrayInitializer_sub(
			HighArray_CodeString src, FindArrayInitializerParams upmost, 
			FindArrayInitializerParams array, String arrayOriginalType, AddCharReallyMode mode) {
		if (array.listOfFindArrayInitializerParams.count>0) { // 자식 배열초기화문이 있으면
			array.depth = getDepth(array);
			int i;
			// 자식 배열들을 재귀적 호출한다.
			for (i=0; i<array.listOfFindArrayInitializerParams.count; i++) {
				FindArrayInitializerParams child = (FindArrayInitializerParams) array.listOfFindArrayInitializerParams.getItem(i);
				child.index = i;
				findTypesOfExpressionsOfArrayInitializer_sub(src, upmost, child, arrayOriginalType, mode);
			}			
		}
		else { // 수식들의 배열
			// 현재 배열의 깊이를 알아낸다.
			array.depth = getDepth(array);
			
			ArrayListIReset listOfExpressions = null;
			if (array.listOfFindFuncCallParam==null || array.listOfFindFuncCallParam.count==0) {
				// 현재 배열의 수식들의 개수를 인덱스들을 알아낸다.
				listOfExpressions = 
					findFuncCallParams(src, array.indexOfLeftParenthesis(), array.indexOfRightParenthesis(), null);
			}
			else {
				// 배열초기화문에서 funcCallParam 하나만 바뀌는 경우
				listOfExpressions = array.listOfFindFuncCallParam;
				int i;
				for (i=0; i<listOfExpressions.count; i++) {				
					FindFuncCallParam expression = (FindFuncCallParam) listOfExpressions.getItem(i);
					if (expression.startIndex()==546) {
						int a;
						a=0;
						a++;
					}
					CodeStringEx typeFullName = expression.typeFullName;
					if (TypeCast.isCompatibleType(this, new CodeStringEx(arrayOriginalType), typeFullName, 1, expression)==false) {
						if (typeFullName==null) {
							errors.add(
								new Error(this, expression.startIndex(), expression.endIndex(), 
									"invalid array initialization. : " + arrayOriginalType + "-" + null));
						}
						else {
							errors.add(
								new Error(this, expression.startIndex(), expression.endIndex(), 
									"invalid array initialization. : " + arrayOriginalType + "-" + typeFullName.str));
						}
					}
					
				}//for (i=0; i<listOfExpressions.count; i++) {
				return;
			}
			
			int i;
			// 현재 배열의 수식들을 포스트픽스로 바꾸고 그 타입을 정하여 현재 배열에 그것들을 넣어주고 
			// 그 타입이 틀린 타입이면 에러를 출력한다.
			for (i=0; i<listOfExpressions.count; i++) {				
				FindFuncCallParam expression = (FindFuncCallParam) listOfExpressions.getItem(i);
				if (expression.startIndex()==546) {
					int a;
					a=0;
					a++;
				}
				CodeStringEx typeFullName = this.getTypeOfExpression(src, expression);
				array.listOfFindFuncCallParam.add(expression);
				if (TypeCast.isCompatibleType(this, new CodeStringEx(arrayOriginalType), typeFullName, 1, expression)==false) {
					if (typeFullName==null) {
						errors.add(
							new Error(this, expression.startIndex(), expression.endIndex(), 
								"invalid array initialization. : " + arrayOriginalType + "-" + null));
					}
					else {
						errors.add(
							new Error(this, expression.startIndex(), expression.endIndex(), 
								"invalid array initialization. : " + arrayOriginalType + "-" + typeFullName.str));
					}
				}
				
			}//for (i=0; i<listOfExpressions.count; i++) {	
		}
	}
	
	
	/** var의 scope를 확인한다.*/
	public boolean CheckVarScope(HighArray_CodeString src, FindVarParams var) {
		int i;
		if (var.isThis || var.isSuper) return false;
		CodeString varName = src.getItem(var.varNameIndex());
		if (varName.equals("0")) {
			int a;
			a=0;
			a++;
		}
		
		Object parent = var.parent;		
		Block parentBlock = getParent((Block)parent);
		// 변수가 제어블럭 포함하여 함수 내에 정의된 경우,  parent블록에 이미 같은 이름의 변수가 선언되어 있는지 조사한다.
		if (parentBlock instanceof FindFunctionParams) {
			for (i=0; i<parentBlock.listOfVariableParams.count; i++) {
				FindVarParams varInList = (FindVarParams)parentBlock.listOfVariableParams.getItem(i);			
				CodeString varNameInList = src.getItem(varInList.varNameIndex());
				if (varName.equals(varNameInList)) {
					boolean isParent = isParent((Block)(varInList.parent), (Block)parent, parentBlock);
					if (isParent) {
						errors.add(new Error(this, var.varNameIndex(), var.varNameIndex(), "var name is duplicated. : " + varName));
						return false;
					}
				}
			}
		}
		// 변수가 클래스에 정의된 경우 parent블록에 이미 같은 이름의 변수가 선언되어 있어도 허용된다.
		
		return true;
		
		
	}
	
	/** 지역변수에 access modifier(public, private등)이 있는지 확인한다.*/
	private void checkLocalVar(HighArray_CodeString src,
			ArrayListIReset listOfAllFunctions) {		
		// TODO Auto-generated method stub
		int i, j;
		boolean showError;
		for (i=0; i<listOfAllFunctions.count; i++) {			
			FindFunctionParams func = (FindFunctionParams)listOfAllFunctions.getItem(i);
			ArrayListIReset listOfLocalVars = func.listOfVariableParams;
			
			for (j=0; j<listOfLocalVars.count; j++) {
				FindVarParams localVar = (FindVarParams)listOfLocalVars.getItem(j);
				showError = hasAccessModifierExceptFinal(localVar, null); 
				if (showError) {
					errors.add(new Error(this, func.functionNameIndex(), func.functionNameIndex(), 
							"invalid access modifier"));
				}
			} // for j			
			
		}		
	}




	void checkControlBlocks(HighArray_CodeString src) {
		int i;
		for (i=0; i<mlistOfAllControlBlocks.count; i++) {
			FindControlBlockParams control =  (FindControlBlockParams) mlistOfAllControlBlocks.getItem(i);
			if (control.parent==null) {
				errors.add(new Error(this, control.startIndex(), control.startIndex(), "invalid "+control.catOfControls.toString()));
			}
		}
	}
	
	
	
	/**클래스의 AndSoOnStatements들을 찾고 넣고 정렬한다. 
	 * 클래스내부의 자식클래스, 함수, 초기화문장들도 포함한다.
	 * @param classParams : 처음 호출시 최상위 클래스, 재귀적 호출시는 해당 클래스*/
	void findAndSoOnStatementsOfClass(HighArray_CodeString src, FindClassParams classParams) {
		int i;
		for (i=0; i<classParams.childClasses.count; i++) {
			FindClassParams child = (FindClassParams) classParams.childClasses.getItem(i);
			// 자식 클래스부터 AndSoOnStatements들을 찾고 넣고 정렬한다. 재귀적 호출
			findAndSoOnStatementsOfClass(src, child);
		}
		
		// 자신의 문장리스트를 AndSoOnStatements들을 찾고 넣고 정렬한다.
		
		classParams.listOfStatements = findAndSoOnStatements(src, classParams.findBlockParams.startIndex()+1, 
				classParams.listOfStatements, classParams.findBlockParams.endIndex()-1);
				
		for (i=0; i<classParams.listOfFunctionParams.count; i++) {
			FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
			findAndSoOnStatementsOfFunction(src, func);
		}
		
	}
	
	/** 제어블록의 AndSoOnStatements들을 찾고 넣고 정렬한다. 재귀적 호출*/
	void findAndSoOnStatementsOfControlBlock(HighArray_CodeString src, FindControlBlockParams controlBlock) {
		int i;
		for (i=0; i<controlBlock.listOfStatements.count; i++) {
			FindStatementParams s = (FindStatementParams) controlBlock.listOfStatements.getItem(i);
			if (s instanceof FindControlBlockParams) {
				FindControlBlockParams c = (FindControlBlockParams) s;
				// 자식 제어블록부터 AndSoOnStatements들을 찾고 넣고 정렬한다.
				findAndSoOnStatementsOfControlBlock(src, c);
			}
		}
		
		// 자신의 문장리스트를 AndSoOnStatements들을 찾고 넣고 정렬한다.
		if (controlBlock.isBlock==false
				/*controlBlock.findBlockParams==null || controlBlock.findBlockParams.startIndex()==-1 || controlBlock.findBlockParams.endIndex()==-1*/) {
			if (controlBlock.catOfControls.category==CategoryOfControls.Control_if) {
				int a;
				a=0;
				a++;
			}
			// 제어블록이 단문이면
			controlBlock.listOfStatements = findAndSoOnStatements(src, controlBlock.indexOfRightParenthesis()+1,
					controlBlock.listOfStatements, controlBlock.endIndex()-1);
		}
		else { // 제어블록이 복문이면
			controlBlock.listOfStatements = findAndSoOnStatements(src, controlBlock.findBlockParams.startIndex()+1,
					controlBlock.listOfStatements, controlBlock.findBlockParams.endIndex()-1);
		}
	}
	
	/** 함수의 AndSoOnStatements들을 찾고 넣고 정렬한다. 함수내부의 제어블록도 포함한다.*/
	void findAndSoOnStatementsOfFunction(HighArray_CodeString src, FindFunctionParams func) {
		if (func.isConstructor==true && func.findBlockParams==null) {
			// 컴파일러가 만드는 생성자이면 리턴한다.
			return;
		}
		
		int i;		
		for (i=0; i<func.listOfStatements.count; i++) {
			FindStatementParams s = (FindStatementParams) func.listOfStatements.getItem(i);
			if (s instanceof FindControlBlockParams) {
				FindControlBlockParams c = (FindControlBlockParams) s;
				findAndSoOnStatementsOfControlBlock(src, c);
			}
		}
		
		// 아규먼트는 여기에서 하지 않는다.
		func.listOfStatements = findAndSoOnStatements(src, /*func.indexOfLeftParenthesis+1*/func.findBlockParams.startIndex()+1, 
				func.listOfStatements, /*func.indexOfRightParenthesis-1*/func.findBlockParams.endIndex()-1);
	}
	
	
	
	
	
	
	void makeScope(FindFunctionParams func, FindVarParams var) {
		if (func.listOfControlBlocks!=null) {
			int i;
			boolean r;
			for (i=0; i<func.listOfControlBlocks.count; i++) {
				FindControlBlockParams block = (FindControlBlockParams)func.listOfControlBlocks.getItem(i);
				r = isInScope(block, var);
				if (r) return;
			}
		}
		if (func.startIndex()<var.startIndex() && var.endIndex()<func.endIndex()) {
			var.startIndexOfScope = IndexForHighArray.indexRelative(var, this.mBuffer, var.startIndex());
			var.endIndexOfScope = IndexForHighArray.indexRelative(var, this.mBuffer, func.endIndex());
		}
		
	}
	
	/** 지역변수의 scope를 결정하기 위한 recursive 호출이다.*/
	private boolean isInScope(FindControlBlockParams block, FindVarParams var) {
		if (block.listOfControlBlocks==null) return false;  // 가장 끝 제어블록의 자식 제어블록리스트는 null이다.
		int i;
		boolean r;
		// 자신부터가 아니라 자식 제어블록부터 검사한다.
		for (i=0; i<block.listOfControlBlocks.count; i++) {
			FindControlBlockParams childBlock = (FindControlBlockParams)block.listOfControlBlocks.getItem(i);
			r = isInScope(childBlock, var);
			if (r) return true; 
			// 자식 제어블록내에서 선언되었으므로 자신의 것은 아니지만 
			// scope가 결정이 되었다는 의미이다.
		}
		if (block.startIndex()<var.startIndex() && var.endIndex()<block.endIndex()) {
			var.startIndexOfScope = IndexForHighArray.indexRelative(var, this.mBuffer, var.startIndex());
			var.endIndexOfScope = IndexForHighArray.indexRelative(var, this.mBuffer, block.findBlockParams.endIndex());
			return true;
		}
		return false;
	}
	
	
	
	void changeKeywordAndConstantColor(HighArray_CodeString src) {
		int i;
		if (language!=Language.Html) {
			for (i=0; i<src.count; i++) {
				CodeString str = src.getItem(i);
				if (CompilerHelper.IsComment(str)) continue;
				if (IsKeyword(str) || IsDefaultType(str)) {
					str.setColor(keywordColor);
					str.setType(CodeStringType.Keyword);
				}
				else if (CompilerHelper.IsConstant(str)) {
					str.setColor(keywordColor);
					str.setType(CodeStringType.Constant);
				}
			}
		}
		else {	// html
			for (i=0; i<src.count; i++) {
				CodeString str = src.getItem(i);
				if (CompilerHelper.IsComment(str)) continue;
				if (IsKeyword(str) || IsDefaultType(str)) {
					int leftParent = SkipBlank(src, true, 0, i-1);
					if (leftParent==-1) continue;
					CodeString left = src.getItem(leftParent); 
					if ((left.equals("<") || left.equals("/"))==false) continue;
					
					int rightParent = SkipBlank(src, false, i+1, src.count-1);
					if (rightParent==src.count) continue;
					//CodeString right = src.getItem(rightParent); 
					//if (right.equals(">")==false) continue;
					
					str.setColor(keywordColor);
					str.setType(CodeStringType.Keyword);
				}
			}
		}
	}
	
	/** @param listOfClass : 최상위 클래스 리스트*/
	void changeVarUseColor(HighArray_CodeString src, ArrayListIReset listOfClass) {
		
		int i;
		for (i=0; i<listOfClass.count; i++) {
			FindClassParams classParams = (FindClassParams)listOfClass.getItem(i);
			changeVarUseColor_sub(src, classParams);
		}
	}
	
	/** 파일에서 사용된 모든 varUse 색깔을 바꾼다. 또한 자식(inner)클래스도 포함한다.
	 * @param classParams : 최상위 클래스*/
	void changeVarUseColor_sub(HighArray_CodeString src, FindClassParams classParams) {
		int i;
		if (classParams.listOfAllVarUses!=null) {
			int len = classParams.listOfAllVarUses.getCount();
			for (i=0; i<len; i++) {
				FindVarUseParams varUse = (FindVarUseParams)classParams.listOfAllVarUses.getItem(i);
				if (varUse.isLocal==false && varUse.varDecl!=null) { // 멤버변수의 사용만 색깔을 바꾼다.
					src.getItem(varUse.index()).setColor(varUseColor);
					src.getItem(varUse.index()).setType(CodeStringType.MemberVarUse);
				}
				else if (varUse.memberDecl!=null) {
					src.getItem(varUse.index()).setColor(varUseColor);
					src.getItem(varUse.index()).setType(CodeStringType.MemberVarUse);
				}
				else if (varUse.funcDecl!=null) { // 함수호출
					src.getItem(varUse.index()).setColor(funcUseColor);
					src.getItem(varUse.index()).setType(CodeStringType.FuncUse);
				}
			}
		}
		
		if (classParams.childClasses!=null) {
			for (i=0; i<classParams.childClasses.count; i++) {
				FindClassParams child = (FindClassParams) classParams.childClasses.getItem(i);
				changeVarUseColor_sub(src, child);
			}
			
		}
	}
	
	/** 중괄호를 사용자가 터치시 나타나는 툴팁을 위한 호출이다.*/
	public CodeString findParenthesis(HighArray_CodeString src, int indexInmBuffer) {
		int i;
		FindBlockParams block=null;
		for (i=0; i<mlistOfBlocks.count; i++) {
			block = (FindBlockParams)mlistOfBlocks.getItem(i);
			if (block.startIndex()==indexInmBuffer) {
				if (block.endIndex()!=-1) {
					if (block.categoryOfBlock!=null)
						return new CodeString(indexInmBuffer + " " + block.categoryOfBlock.toString()+" "+block.blockName + " starts",textColor);
					else return new CodeString(indexInmBuffer + " " + "EndIndex:" + block.endIndex() + " starts",textColor);
				}
				else {
					return new CodeString(indexInmBuffer + " " + "Not pair",textColor);
				}
			}
			else if (block.endIndex()==indexInmBuffer) {
				if (block.categoryOfBlock!=null)
					return new CodeString(indexInmBuffer + " " + block.categoryOfBlock.toString()+" "+block.blockName + " ends",textColor);
				else return new CodeString(indexInmBuffer + " " + "StartIndex:" + block.startIndex() + " ends",textColor);
			} 
		}
		return null;
	}
	
	public void checkVarUse(HighArray_CodeString src) {
		int i;
		FindVarUseParams varUse=null;
		int len = mlistOfAllVarUses.getCount();
		for (i=0; i<len; i++) {
			varUse = (FindVarUseParams)mlistOfAllVarUses.getItem(i);
			if (varUse.index()==599) {
				int a;
				a=0;
				a++;
			}
			if ( CompilerHelper.IsConstant( src.getItem(varUse.index()) ) ) continue;
			if (varUse.varDecl==null && varUse.funcDecl==null && varUse.memberDecl==null) {
				if (varUse.isEnumElement) continue; // varUse가 enum 원소일 경우에도 제외한다.
				//if (varUse.typeCast!=null) continue; // varUse가 타입캐스트일 경우에도 제외한다.(int)a에서 int
				if (IsDefaultType(varUse.originName)) continue; // int, char 등일 경우
				
				errors.add(new Error(this, varUse.index(), varUse.index(), 
						"invalid var use : " + src.getItem(varUse.index())));
			}
		}
	}
	
	
	
	/** 타입선언문에서 변수 타입이나 변수를 또는 클래스 선언문, 변수사용, 함수선언, 제어블록의 키워드부분을 
	 * 사용자가 터치시 나타나는 툴팁을 위한 호출이다.*/
	public CodeString findNode(HighArray_CodeString src, int indexInmBuffer) {
		try{
		CodeString r = findNode_sub(src, mlistOfClass, indexInmBuffer);
		if (r==null) {
			r = findNode_sub(src, null, indexInmBuffer);			
		}
		if (r==null) {
			r = new CodeString(indexInmBuffer+"\n", Compiler.textColor);
		}
		else {
			CodeString str = new CodeString(indexInmBuffer+"\n", Compiler.textColor);
			r = str.concate(r);
		}
		Error err = getError(indexInmBuffer);
		CodeString result;
		if (err!=null) {
			result = new CodeString("(Error)"+err.msg+"\n", Compiler.keywordColor);
		}
		else result = new CodeString("", textColor);
		
		if (r!=null) result = result.concate(r);		
		
		return result;
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
		return null;
		
	}
	
	public CodeString findNode_sub(HighArray_CodeString src, ArrayListIReset listOfClasses, 
			int indexInmBuffer) {
		int i;
		CodeString r=null;
		if (listOfClasses==null) {
			r = findNode_sub_sub(src, null, indexInmBuffer);
			return r;
		}
		for (i=0; i<listOfClasses.count; i++) {
			FindClassParams classParams = (FindClassParams) listOfClasses.getItem(i);
			r = findNode_sub_sub(src, classParams, indexInmBuffer);
			if (r!=null) return r;
		}
		return r;
		
	}
	
	
	/** 변수타입선언문인지 변수사용(또는 함수호출)인지 확인한다. 
	 * 또한 함수정의문인지 클래스선언문, 또는 제어블럭(if, for블록등의 키워드)인지 확인한다.*/
	Object findNode_NodeType(HighArray_CodeString src, FindClassParams classParams, int indexInmBuffer) {
		int i;
		if (classParams==null) {
			int len = mlistOfAllVarUses.getCount();
			for (i=0; i<len; i++) {
				FindVarUseParams varUse = (FindVarUseParams) mlistOfAllVarUses.getItem(i);
				if (varUse.index()==indexInmBuffer) return varUse;
			}
			return null;
		}
		
		// 클래스 정의문이나 enum 정의문인지 확인
		if (classParams.classNameIndex()==indexInmBuffer) {
			return classParams;
		}
		
		int j;
		
		
		for (i=0; i<this.mlistOfAllControlBlocks.count; i++) {
			FindControlBlockParams controlBlock = (FindControlBlockParams) mlistOfAllControlBlocks.getItem(i);
			if (controlBlock.nameIndex()==indexInmBuffer) {
				return controlBlock;
			}
		}
		
		// 변수사용인지 확인한다.
		FindVarUseParams r=null;
		boolean isVarUseOrFuncCall;
		int nextIndex = SkipBlank(src, false, indexInmBuffer+1, src.count-1);
		if (nextIndex==src.count) return null;
		
		if (src.getItem(nextIndex).equals("(")) {
			isVarUseOrFuncCall = false;
		}
		else {
			isVarUseOrFuncCall = true;
		}
		
		// 변수사용이 가장 하위 자식 클래스에 등록이 되어 있기 때문이다.
		if (isVarUseOrFuncCall) {	// 변수참조 해시테이블에서 찾기
			String dataName = src.getItem(indexInmBuffer).str;
			if (classParams.listOfAllVarUsesForVarHashed!=null) {
				r = classParams.listOfAllVarUsesForVarHashed.getData(dataName, indexInmBuffer);
				if (r!=null) {
					return r;
				}
			}
		}
		else {	// 함수호출 리스트에서 찾기
			if (classParams.listOfAllVarUsesForFunc!=null) {
				int len = classParams.listOfAllVarUsesForFunc.getCount();
				for (j=0; j<len; j++) {
					FindVarUseParams varUse = (FindVarUseParams)classParams.listOfAllVarUsesForFunc.getItem(j);
					if (src.getItem(varUse.index()).equals("a")) {
						int a;
						a=0;
						a++;
					}
					if (varUse.index()==indexInmBuffer) {
						return varUse;
					}
					else if (indexInmBuffer<varUse.index()) {
						break;
					}
				}
			}
			
		}
		
		if (classParams.listOfVariableParams!=null) {		
			FindVarParams var=null;			
			// 멤버변수선언인지 확인
			ArrayListIReset listOfVarDeclaratons = classParams.listOfVariableParams; 
			for (i=0; i<listOfVarDeclaratons.count; i++) {
				var = (FindVarParams)listOfVarDeclaratons.getItem(i);
				if (var.typeEndIndex()==1364) {
					int a;
					a=0;
					a++;
				}
				if (var.isEnumElement && var.varNameIndex()==indexInmBuffer) {	
					// enum 변수를 터치시
					return var;
				}
				if (var.startIndex()==-1 || var.endIndex()==-1) continue;			
				if (var.typeStartIndex()==-1 || var.typeEndIndex()==-1) continue;
				if (var.typeStartIndex()<=indexInmBuffer && indexInmBuffer<=var.typeEndIndex()) {
					return var;
				}
				if (var.varNameIndex()==indexInmBuffer) {		// 변수를 터치시
					return var;
				}
			}
		}
		
		if (classParams.listOfFunctionParams!=null) {
			// 지역변수선언인지 확인		
			ArrayListIReset listOfFuncs = classParams.listOfFunctionParams; 
			FindFunctionParams func = null;
			boolean found = false;
			for (j=0; j<listOfFuncs.count; j++) {
				func = (FindFunctionParams) listOfFuncs.getItem(j);
				if (func.startIndex()<indexInmBuffer && indexInmBuffer<func.endIndex()) {
					found = true;
					break;
				}					
			}
			if (found) {
				ArrayListIReset listOfLocalVars = func.listOfVariableParams;
				for (j=0; j<listOfLocalVars.count; j++) {
					FindVarParams var = (FindVarParams) listOfLocalVars.getItem(j);
					if (var.typeEndIndex()==1364) {
						int a;
						a=0;
						a++;
					}
					if (var.startIndex()==-1 || var.endIndex()==-1) continue;
					if (var.typeStartIndex()==-1 || var.typeEndIndex()==-1) continue;
					if (var.typeStartIndex()<=indexInmBuffer && indexInmBuffer<=var.typeEndIndex()) {
						return var;
					}
					if (var.varNameIndex()==indexInmBuffer) {		// 변수를 터치시
						return var;
					}
				}
			}
		}
		
		
		
		
		
		if (classParams.listOfFunctionParams!=null) {
			// 함수인지 확인
			ArrayListIReset listOfFuncs = classParams.listOfFunctionParams;		
			boolean found = false;
			for (j=0; j<listOfFuncs.count; j++) {
				FindFunctionParams func = (FindFunctionParams) listOfFuncs.getItem(j);
				if (func.startIndex()<=indexInmBuffer && indexInmBuffer<=func.endIndex()) {
					if (func.returnTypeStartIndex()<=indexInmBuffer && indexInmBuffer<=func.returnTypeEndIndex()) {
						return func;
					}
					if (func.functionNameIndex()==indexInmBuffer) {
						return func;
					}
				}					
			}
		}
		
		
		return null;
	}
	
	/** 제어블록의 키워드부분을 사용자가 터치시 호출이 된다.*/
	public CodeString findNode_controlBlock_makeString(HighArray_CodeString src, FindControlBlockParams controlBlock, int indexInmBuffer) {		
		if (controlBlock!=null) {
			CodeString r = null;
			if (controlBlock.funcCall==null) {
				
			}
			else {
				r = new CodeString("", textColor);
				int j;
				if (controlBlock.funcCall.expression==null) return null;
				for (j=0; j<controlBlock.funcCall.expression.postfix.length; j++) {
					CodeStringEx token = controlBlock.funcCall.expression.postfix[j];
					r = getPostfixPrint(src, r, token);				
				}
			}
			return r;
		}
		return null;
	}
	
	/** 함수 정의의 머릿부분을 사용자가 터치시 호출이 된다.*/
	public CodeString findNode_func_makeString(HighArray_CodeString src, FindFunctionParams func, int indexInmBuffer) {		
		if (func!=null) {
			// func.functionNameIndex()==-1인 경우는 MenuClassList에서 상속 함수선언을 터치한 경우이다.
			if (func.functionNameIndex()==indexInmBuffer || func.functionNameIndex()==-1) {
				int k;
				String docuComment = null;
				if (func.docuComment!=null && func.docuComment.str!=null) {
					docuComment = func.docuComment.str;
				}
				
				String className = "";
				if (func.parent!=null) {
					FindClassParams classP = (FindClassParams)func.parent;
					className = classP.name + ".";
				}
				
				String funcName;
				funcName = func.name;
				String returnTypeOfFunc;
				returnTypeOfFunc = func.getReturnType(src, func.returnTypeStartIndex(), func.returnTypeEndIndex());
				String args = "";
				for (k=0; k<func.listOfFuncArgs.count; k++) {
					FindVarParams arg = (FindVarParams)func.listOfFuncArgs.getItem(k);
					String typeName = arg.getType(src, arg.typeStartIndex(), arg.typeEndIndex());
					if (arg.varNameIndex()!=-1) {
						args += typeName + " " + src.getItem(arg.varNameIndex()).toString();
					}
					else {
						args += typeName;
					}
					if (k!=func.listOfFuncArgs.count-1) {
						args += ", ";
					}
				}
				
				boolean isStatic = func.accessModifier.isStatic;
				boolean isConstructor = func.isConstructor;
				
				String message = "";
				if (func.overridedFindFunctionParams!=null) {
					message = "Overrides " + func.overridedFindFunctionParams.name + "() in " + 
							((FindClassParams)func.overridedFindFunctionParams.parent).name + "\n\n";
				}
				/*if (isStatic) {
					if (isConstructor) message += "S, C : ";
					else message += "S : ";
				}
				else {
					if (isConstructor) message += "C : ";
					else message += "";
				}*/
				if (isConstructor) message += "Constructor ";
				message += func.accessModifier.toString();
								
								
				if (docuComment!=null)
					return new CodeString(docuComment + "\n" + message + returnTypeOfFunc + " " + className + funcName + "(" + args + ")", textColor);
				else 
					return new CodeString(message + returnTypeOfFunc + " " + className + funcName + "(" + args + ")", textColor);
			}
			// 함수선언의 리턴타입을 터치시
			else if (func.returnTypeStartIndex()<=indexInmBuffer && indexInmBuffer<=func.returnTypeEndIndex()) {
				String docuComment = null;
				CodeString r;
				FindClassParams typeClassParams = CompilerHelper.loadClass(this, func.returnType);
				if (typeClassParams!=null && typeClassParams.docuComment!=null && typeClassParams.docuComment.str!=null) {
					docuComment = typeClassParams.docuComment.str;
				}
				if (docuComment!=null) {
					r = new CodeString(docuComment + "\n" + func.getReturnType(src, func.returnTypeStartIndex(), func.returnTypeEndIndex()), textColor);
				}
				else {
					r = new CodeString(func.getReturnType(src, func.returnTypeStartIndex(), func.returnTypeEndIndex()), textColor);
				}
				return r;
			}
		}
		return null;
	}
	
	
	
	/** findNode_varUse_makeString_postfix()에서 호출한다. 포스트픽스의 해당 토큰을 출력한다.
	 * @param r : input*/
	CodeString getPostfixPrint(HighArray_CodeString src, CodeString r, CodeStringEx token) {
		if (CompilerHelper.IsOperator(token)) {
			if (token.isPlusOrMinusForOne==false) {
				r = r.concate(new CodeString(token.str, varUseColor));
				r = r.concate(new CodeString(":" + token.typeFullName + "  ", textColor));
			}
			else {
				r = r.concate(new CodeString(token.str, keywordColor));
				r = r.concate(new CodeString(":" + token.typeFullName + "  ", textColor));
			}
		}
		else {
			r = r.concate(new CodeString(token.str, varUseColor));
			r = r.concate(new CodeString(":" + token.typeFullName + "  ", textColor));
		}
		return r;
	}
	
	static class ResultOfFindNode_arrayIntializer_makeString_postfix {
		CodeString r;
		ResultOfFindNode_arrayIntializer_makeString_postfix(CodeString r) {
			this.r = r;
		}
	}
	
	
	/**int[][][] colors2 = {<br>
			{//a0<br>
				{Color.BLACK, Color.WHITE, Color.RED}, //a00<br>
				{Color.YELLOW,Color.BLUE, Color.GREEN} //a01<br>
			},
			 
			{//a1<br>
				{Color.BLACK, Color.WHITE, Color.RED}, //a10<br>
			    {Color.YELLOW,Color.BLUE, Color.GREEN} //a11<br>
			}
		};//2면 2행 3열<br>
colors2->a0->a00->Color.BLACK, Color.WHITE, Color.RED <br>
	   	   ->a01->Color.YELLOW,Color.BLUE, Color.GREEN <br>
	   ->a1->a10->Color.BLACK, Color.WHITE, Color.RED <br>
	       ->a11->Color.YELLOW,Color.BLUE, Color.GREEN <br>
 깊이 :0    1    2*/
	public void findNode_arrayIntializer_makeString_postfix(HighArray_CodeString src, FindArrayInitializerParams topArray, FindArrayInitializerParams array, ResultOfFindNode_arrayIntializer_makeString_postfix r) {
		if (array.listOfFindArrayInitializerParams.count>0) { // 중첩된 배열
			int i;
			for (i=0; i<array.listOfFindArrayInitializerParams.count; i++) {
				FindArrayInitializerParams child = (FindArrayInitializerParams) array.listOfFindArrayInitializerParams.getItem(i);
				findNode_arrayIntializer_makeString_postfix(src, topArray, child, r);
			}
		}
		else { // 수식, 가장 하위 노드
			CodeString indexOfArray = new CodeString("",textColor);
			ArrayListInt indices = array.getIndicesOfParentArrayIncludingOwnIndex(array);
			for (int m=indices.count-1; m>=0; m--) {
				int index = indices.getItem(m);
				indexOfArray = indexOfArray.concate(new CodeString("[",textColor));
				String strIndex = String.valueOf(index);
				CodeString cstrIndex = new CodeString(strIndex,textColor);
				indexOfArray = indexOfArray.concate(cstrIndex).concate(new CodeString("]",textColor));
			}
			String strIndexOfArray = indexOfArray.toString();
			
			int i, j, k;
			CodeString result = new CodeString("", Color.BLACK);
			CodeString indexOfArray2;
			for (i=0; i<array.listOfFindFuncCallParam.count; i++) {
				FindFuncCallParam funcCallParam = (FindFuncCallParam) array.listOfFindFuncCallParam.getItem(i);
				indexOfArray2 = new CodeString(strIndexOfArray + "["+i+"]", textColor);
				result = result.concate(indexOfArray2);
				
				if (funcCallParam.expression.postfix!=null) {					
					
					// 수식트리에서 상위 노드의 포스트픽스
					// f2(1+2) 3 +
					for (j=0; j<funcCallParam.expression.postfix.length; j++) {						
						CodeStringEx token = funcCallParam.expression.postfix[j];
						result = getPostfixPrint(src, result, token);
					}
										
					CodeString r2= new CodeString("",varUseColor);
					// 1 2 +
					// 수식트리에서 자식노드들을 방문하기위해 재귀적호출
					for (j=0; j<funcCallParam.expression.postfix.length; j++) {
						CodeStringEx token = funcCallParam.expression.postfix[j];
						for (k=0; k<token.listOfVarUses.count; k++) {
							FindVarUseParams child = null;
							try {
							child = (FindVarUseParams) token.listOfVarUses.getItem(k);
							}catch(Exception e) {
								int a;
								a=0;
								a++;
							}
							CodeString childStr;
							if (child!=null) {
								// child를 varUse로 recursive call
								childStr = 
									findNode_varUse_makeString_postfix(src, child, child.index());
								if (childStr!=null) {
									r2 = r2.concate(childStr).concate(new CodeString("  ",textColor));
								}
								
								k = getIndex(token, child, k);
							}//if (child!=null) {
						}//for (k=0; k<token.listOfVarUses.count; k++) {
					}//for (j=0; j<funcCallParam.expression.postfix.length; j++) {
					if (r2.equals("")==false) {
						result = result.concate(new CodeString(" --> ",textColor));
						result = result.concate(r2);
					}
						
					//r = r.concat("\n");
					result = result.concate(new CodeString("\n",textColor));
				}//if (funcCallParam.expression.postfix!=null) {
			}//for (i=0; i<array.listOfFindFuncCallParam.count; i++) {
			
			r.r = r.r.concate(result);
		}// else
		
	}
	
	/** 수식 트리를 순회한다.
	 * @param src
	 * @param varUse : 처음 호출시 사용자가 터치한 varUse, 아니면 자식노드의 varUse
	 * @param indexInmBuffer : 처음 호출시 사용자가 터치한 varUse의 mBuffer에서의 인덱스,
	 * , 아니면 자식노드의 varUse의 mBuffer에서의 인덱스
	 * @return : 수식트리를 순회하면서 함수 파라미터(FindFuncCallParam)에 있는 수식의 포스트픽스 표현들의 리스트
	 */
	public CodeString findNode_varUse_makeString_postfix(HighArray_CodeString src, FindVarUseParams varUse, int indexInmBuffer) {
		int i, j, k;
		CodeString r = new CodeString("",varUseColor);
		if (varUse.index()==5248) {
			int a;
			a=0;
			a++;
		}
		int indexOfEqual = IsLValue(src, varUse);
		
		if (indexOfEqual!=-1) { // varUse가 LValue이면
			
			int startIndex = getIndexInmListOfAllVarUses(src, mlistOfAllVarUses, 0, indexOfEqual+1, true);
			int endIndex=-1;
			
			
			
			CodeString r2= new CodeString("",varUseColor);
			
			
			if (varUse.arrayInitializer!=null) {
				ResultOfFindNode_arrayIntializer_makeString_postfix result = new ResultOfFindNode_arrayIntializer_makeString_postfix(r);
				//CodeString ret = 
					findNode_arrayIntializer_makeString_postfix(src, 
							varUse.arrayInitializer, varUse.arrayInitializer, result);
				return result.r;
			}
			
			if (varUse.rValue.expression.postfix==null) return r;
			
			startIndex = getIndexInmListOfAllVarUses(src, mlistOfAllVarUses, 0, varUse.rValue.startIndex(), true);
			endIndex = getIndexInmListOfAllVarUses(src, mlistOfAllVarUses, 0, varUse.rValue.endIndex(), false);
			
			
			// 수식트리에서 상위 노드의 포스트픽스를 출력한다.
			for (j=0; j<varUse.rValue.expression.postfix.length; j++) {
				CodeStringEx token = varUse.rValue.expression.postfix[j];
				r = getPostfixPrint(src, r, token);				
			}
			if (varUse.rValue.typeFullName!=null && varUse.rValue.typeFullName.value!=null) {
				r = r.concate(new CodeString("(value = "+varUse.rValue.typeFullName.value+")", Compiler.textColor));
			}
			
			for (k=startIndex; k<=endIndex; k++) {
				if (k==35) {
					int a;
					a=0;
					a++;
				}
				FindVarUseParams child = (FindVarUseParams) mlistOfAllVarUses.getItem(k);
								
				CodeString childStr;
				r2= new CodeString("",varUseColor);
				
				// 수식트리에서 자식노드들을 방문하기위해 재귀적호출
				childStr = findNode_varUse_makeString_postfix(src, child, child.index());
				if (childStr!=null) {
					r2 = r2.concate(childStr).concate(new CodeString("  ",textColor));
				}
				
				// 중복되어 출력될수있으므로 인덱스값을 처리한 위치 다음으로 바꿔준다.
				boolean isFuncCallAndArrayElement = false;
				if (child.funcDecl!=null && child.isArrayElement)
					isFuncCallAndArrayElement = true;
				
				if (child.funcDecl!=null) {
					if (child.listOfFuncCallParams.count>0) {
						FindFuncCallParam funcCall = (FindFuncCallParam) 
							child.listOfFuncCallParams.getItem(child.listOfFuncCallParams.count-1);
						int endIndexOfFuncCall = funcCall.endIndex()+1;
						k = getIndexInmListOfAllVarUses2(mlistOfAllVarUses, k, endIndexOfFuncCall, true);
						k--;
					}
				}
				if (child.isArrayElement) {
					FindFuncCallParam funcCall = (FindFuncCallParam) 
							child.listOfArrayElementParams.getItem(child.listOfArrayElementParams.count-1);
					int endIndexOfFuncCall = funcCall.endIndex()+1;
					k = getIndexInmListOfAllVarUses2(mlistOfAllVarUses, k, endIndexOfFuncCall, true);
					k--;										
				}
				if (child.typeCast!=null && child.typeCast.funcCall!=null) {
					FindFuncCallParam funcCall = (FindFuncCallParam) child.typeCast.funcCall;
					int endIndexOfFuncCall = funcCall.endIndex()+1;
					k = getIndexInmListOfAllVarUses2(mlistOfAllVarUses, k, endIndexOfFuncCall, true);
					k--;
				}
				if (isFuncCallAndArrayElement && child.listOfFuncCallParams.count>0) k++;
				
				if (r2.equals("")==false) {
					r = r.concate(new CodeString(" --> ",textColor));
					r = r.concate(r2);
				}				
				
				r = r.concate(new CodeString("\n",textColor));
			}
		}//if (IsLValue(src, varUse)!=-1) { // varUse가 LValue이면
		
		if (varUse.funcDecl!=null) {
			// f1(f2(1+2)+3)
			for (i=0; i<varUse.listOfFuncCallParams.count; i++) {
				// 파라미터 한개
				FindFuncCallParam funcCallParam = 
						(FindFuncCallParam) varUse.listOfFuncCallParams.getItem(i);
								
				if (funcCallParam.expression!=null) {
					if (funcCallParam.expression.postfix!=null) {					
						
						// 수식트리에서 상위 노드의 포스트픽스
						// f2(1+2) 3 +
						for (j=0; j<funcCallParam.expression.postfix.length; j++) {
							
							CodeStringEx token = funcCallParam.expression.postfix[j];
							r = getPostfixPrint(src, r, token);
						}
						if (funcCallParam.typeFullName.value!=null) {
							r = r.concate(new CodeString("(value = "+funcCallParam.typeFullName.value+")", Compiler.textColor));
						}
											
						CodeString r2= new CodeString("",varUseColor);
						// 1 2 +
						// 수식트리에서 자식노드들을 방문하기위해 재귀적호출
						for (j=0; j<funcCallParam.expression.postfix.length; j++) {
							CodeStringEx token = funcCallParam.expression.postfix[j];
							for (k=0; k<token.listOfVarUses.count; k++) {
								FindVarUseParams child = null;
								try {
								child = (FindVarUseParams) token.listOfVarUses.getItem(k);
								}catch(Exception e) {
									int a;
									a=0;
									a++;
								}
								CodeString childStr;
								if (child!=null) {
									// child를 varUse로 recursive call
									childStr = 
										findNode_varUse_makeString_postfix(src, child, child.index());
									if (childStr!=null) {
										r2 = r2.concate(childStr).concate(new CodeString("  ",textColor));
									}
									
									k = getIndex(token, child, k);
								}//if (child!=null) {
							}//for (k=0; k<token.listOfVarUses.count; k++) {
						}//for (j=0; j<funcCallParam.expression.postfix.length; j++) {
						if (r2.equals("")==false) {
							r = r.concate(new CodeString(" --> ",textColor));
							r = r.concate(r2);
						}
							
						//r = r.concat("\n");
						r = r.concate(new CodeString("\n",textColor));
					}//if (funcCallParam.expression.postfix!=null) {
					
				} //if (funcCallParam.expression!=null) {
			} //for (i=0; i<varUse.listOfFuncCallParams.count; i++) {
			//return r;
		} // if (varUse.funcDecl!=null) {
		
		
		if (varUse.isArrayElement) { // 배열첨자 처리
			if (varUse.listOfArrayElementParams==null) return r;
			for (i=0; i<varUse.listOfArrayElementParams.count; i++) {
				FindFuncCallParam funcCallParam = 
						(FindFuncCallParam) varUse.listOfArrayElementParams.getItem(i);
								
				if (funcCallParam.expression!=null) {
					if (funcCallParam.expression.postfix!=null) {					
						
						// 수식트리에서 상위 노드의 포스트픽스
						// f2(1+2) 3 +
						for (j=0; j<funcCallParam.expression.postfix.length; j++) {
							CodeStringEx token = funcCallParam.expression.postfix[j];
							r = getPostfixPrint(src, r, token);
						}
						if (funcCallParam.typeFullName.value!=null) {
							r = r.concate(new CodeString("(value = "+funcCallParam.typeFullName.value+")", Compiler.textColor));
						}
											
						CodeString r2= new CodeString("",varUseColor);
						// 1 2 +
						// 수식트리에서 자식노드들을 방문하기위해 재귀적호출
						for (j=0; j<funcCallParam.expression.postfix.length; j++) {
							CodeStringEx token = funcCallParam.expression.postfix[j];
							for (k=0; k<token.listOfVarUses.count; k++) {
								FindVarUseParams child = 
										(FindVarUseParams) token.listOfVarUses.getItem(k);
								CodeString childStr;
								if (child!=null) {
									// child를 varUse로 recursive call 
									childStr = 
										findNode_varUse_makeString_postfix(src, child, child.index());
									if (childStr!=null) {
										r2 = r2.concate(childStr).concate(new CodeString("  ",textColor));
									}
									
									k = getIndex(token, child, k);
								}//if (child!=null) {
							}
						}
						if (r2.equals("")==false) {
							r = r.concate(new CodeString(" --> ",textColor));
							r = r.concate(r2);
						}
						r = r.concate(new CodeString("\n",textColor));
					}//if (funcCallParam.expression.postfix!=null) {
					
				} //if (funcCallParam.expression!=null) {
			} //for (i=0; i<varUse.listOfFuncCallParams.count; i++) {
			//return r;
		}//else if (varUse.isArrayElement) {
		
		if (varUse.typeCast!=null && varUse.typeCast.funcCall!=null) { // (타입)(수식)인 경우 varUse는 타입
			//String r = "";
			//CodeString r = new CodeString("",varUseColor);
			FindFuncCallParam funcCallParam = varUse.typeCast.funcCall;
						
			if (funcCallParam.expression!=null) {
				if (funcCallParam.expression.postfix!=null) {					
					
					// 수식트리에서 상위 노드의 포스트픽스
					// f2(1+2) 3 +
					for (j=0; j<funcCallParam.expression.postfix.length; j++) {
						CodeStringEx token = funcCallParam.expression.postfix[j];
						r = getPostfixPrint(src, r, token);
					}
					if (funcCallParam.typeFullName.value!=null) {
						r = r.concate(new CodeString("(value = "+funcCallParam.typeFullName.value+")", Compiler.textColor));
					}
										
					//String r2="";
					CodeString r2= new CodeString("",varUseColor);
					// 1 2 +
					// 수식트리에서 자식노드들을 방문하기위해 재귀적호출
					for (j=0; j<funcCallParam.expression.postfix.length; j++) {
						CodeStringEx token = funcCallParam.expression.postfix[j];
						for (k=0; k<token.listOfVarUses.count; k++) {
							FindVarUseParams child = 
									(FindVarUseParams) token.listOfVarUses.getItem(k);
							CodeString childStr;
							if (child!=null) {
								// child를 varUse로 recursive call 
								childStr = 
									findNode_varUse_makeString_postfix(src, child, child.index());
								if (childStr!=null) {
									r2 = r2.concate(childStr).concate(new CodeString("  ",textColor));
								}
								
								k = getIndex(token, child, k);
							}//if (child!=null) {
						}
					}
					if (r2.equals("")==false) {
						r = r.concate(new CodeString(" --> ",textColor));
						r = r.concate(r2);
					}
					r = r.concate(new CodeString("\n",textColor));
				}//if (funcCallParam.expression.postfix!=null) {
				
			} //if (funcCallParam.expression!=null) {
			
		}//else if (varUse.typeCast!=null) {
		
		if (r.equals("") && indexOfEqual==-1) { // 일반변수(배열이나 함수호출이 아닌), 상수 등
			return null;
		}
		
		return r;
		
	} // findNode_varUse_makeString_postfix
	
	/** findNode_varUse_makeString_postfix에서 호출한다.
	 * 수식이 중복되어 처리될수있으므로 인덱스값을 child의 마지막 위치 다음으로 바꿔준다.*/
	int getIndex(CodeStringEx token, FindVarUseParams child, int k) {
		boolean isFuncCallAndArrayElement = false;
		if (child.funcDecl!=null && child.isArrayElement)
			isFuncCallAndArrayElement = true;
		
		if (child.funcDecl!=null) {
			if (child.listOfFuncCallParams.count>0) {
				FindFuncCallParam funcCall = (FindFuncCallParam) 
					child.listOfFuncCallParams.getItem(child.listOfFuncCallParams.count-1);
				int endIndexOfFuncCall = funcCall.endIndex()+1;
				k = getIndexInmListOfAllVarUses2(token.listOfVarUses, k, endIndexOfFuncCall, true);
				k--;
			}
		}
		if (child.isArrayElement) {
			FindFuncCallParam funcCall = (FindFuncCallParam) 
					child.listOfArrayElementParams.getItem(child.listOfArrayElementParams.count-1);
			int endIndexOfFuncCall = funcCall.endIndex()+1;
			k = getIndexInmListOfAllVarUses2(token.listOfVarUses, k, endIndexOfFuncCall, true);
			k--;										
		}
		if (child.typeCast!=null && child.typeCast.funcCall!=null) {
			FindFuncCallParam funcCall = (FindFuncCallParam) child.typeCast.funcCall;
			int endIndexOfFuncCall = funcCall.endIndex()+1;
			k = getIndexInmListOfAllVarUses2(token.listOfVarUses, k, endIndexOfFuncCall, true);
			k--;
		}
		if (isFuncCallAndArrayElement && child.listOfFuncCallParams.count>0) k++;
		return k;
	}
	
	public CodeString findNode_varUse_makeString(HighArray_CodeString src, FindVarUseParams varUse, int indexInmBuffer) {
		if (src.getItem(varUse.index()).equals("RectForPage")) {
			int a;
			a=0;
			a++;
		}
		if (varUse.memberDecl!=null) {
			if (src.getItem(varUse.index()).equals("HScrollBar")) {
				int a;
				a=0;
				a++;
			}
			 
			if (varUse.memberDecl instanceof FindClassParams) { // 멤버클래스
				FindClassParams classDecl = (FindClassParams) varUse.memberDecl;
				//if (classDecl.parent!=null) { // 클래스를 포함하는 클래스
					String docuComment = null;
					if (classDecl.docuComment!=null && classDecl.docuComment.str!=null) {
						docuComment = classDecl.docuComment.str;
					}
					String typeName;
					
					FindClassParams classP = (FindClassParams)classDecl.parent;
					String classFullNameDot = "";
					typeName = classDecl.name;
					
					
					/*String fullnameTypeCast = FindVarUseParams.getFullnameTypeCast(varUse.typeCastedByVarUse);
					if (fullnameTypeCast!=null) {
						typeName += "(" + fullnameTypeCast + ")"; 
					}*/
					
					
					String message = classDecl.accessModifier.toString();
					
					if (docuComment!=null)
						return new CodeString(docuComment + "\n" + message + typeName,textColor);
					else
						return new CodeString(message + typeName,textColor);
				//}
			}	// if (varUse.memberDecl instanceof FindClassParams) { // 멤버클래스
			else if (varUse.memberDecl instanceof FindPackageParams) { // 패키지
				FindPackageParams packageDecl = (FindPackageParams) varUse.memberDecl;
				//if (packageDecl.parent!=null) { // 클래스를 포함하는 클래스
					
					String typeName;
					typeName = packageDecl.getFullName(0);
					typeName = "P : " + typeName;
					return new CodeString(typeName,textColor);
				//}
			}	// else if (varUse.memberDecl instanceof FindPackageParams) { // 패키지	
		} // if (varUse.memberDecl!=null) {
		else if (varUse.varDecl!=null) {
			if (src.getItem(varUse.index()).equals("isMaximized")) {
				int a;
				a=0;
				a++;
			}
			FindVarParams varDecl = varUse.varDecl; 
			if (varUse.isLocal==false) { // 멤버변수		
				if (varDecl.parent!=null) { // 변수선언을 포함하는 클래스
					String docuComment = null;
					if (varDecl.docuComment!=null && varDecl.docuComment.str!=null) {
						docuComment = varDecl.docuComment.str;
					}
					// 클래스이름뒤에 "."이 붙게됨을 주의한다.
					FindClassParams classP = (FindClassParams)varDecl.parent; 
					String classFullNameDot = "";
					classFullNameDot = classP.name + ".";
					
					String typeName = null;
					String varName;
					if (varDecl.isThis==false  && varDecl.isSuper==false) { // 외부 클래스
						typeName = varDecl.typeName;
					}
					else {
						if (varDecl.isThis==false && varDecl.isSuper==false)
							typeName = varDecl.getType(src, varDecl.typeStartIndex(), varDecl.typeEndIndex());
						else if (varDecl.isThis) {
							typeName = classP.name;
						}
						else if (varDecl.isSuper) {
							typeName = classP.classNameToExtend;
						}
					}
					
					/*String fullnameTypeCast = FindVarUseParams.getFullnameTypeCast(varUse.typeCastedByVarUse);
					if (fullnameTypeCast!=null) {
						typeName += "(" + fullnameTypeCast + ")";
					}*/
					if (varDecl.isThis==false && varDecl.isSuper==false) {// 외부 클래스
						varName = varDecl.fieldName;
					}
					else if (varDecl.isThis)
						varName = "this";
					else if (varDecl.isSuper)
						varName = "super";
					else varName = typeName;
					
					boolean isStatic=false;
					if (varDecl.isThis==false && varDecl.accessModifier!=null)
						isStatic = varDecl.accessModifier.isStatic;
					else if (varDecl.isThis || varDecl.isSuper)
						isStatic = false;
					
					/*String message;
					if (varDecl.isClass) {
						message = isStatic ? "S : " : "";
					}
					else {
						message = isStatic ? "S,M : " : "M : ";
					}*/
					String message = varDecl.accessModifier.toString() + "Member Variable ";
					
					if (docuComment!=null)
						return new CodeString(docuComment + "\n" + message + typeName + " " + classFullNameDot + varName,textColor);
					else
						return new CodeString(message + typeName + " " + classFullNameDot + varName, textColor);
				}
			}
			else { // 지역변수
				if (varDecl.parent!=null) {						
					FindFunctionParams func = (FindFunctionParams)getParent((Block)varDecl.parent); 
					String funcName = ((FindClassParams)func.parent).name + "." + func.name;
					String returnTypeOfFunc;
					returnTypeOfFunc = func.getReturnType(src, 
							func.returnTypeStartIndex(), func.returnTypeEndIndex());
					String typeName = varDecl.getType(src, varDecl.typeStartIndex(), varDecl.typeEndIndex());
					
					/*String fullnameTypeCast = FindVarUseParams.getFullnameTypeCast(varUse.typeCastedByVarUse);
					if (fullnameTypeCast!=null) {
						typeName += "(" + fullnameTypeCast + ")";
					}*/
					String varName = src.getItem(varDecl.varNameIndex()).toString();
					String scope = "(scope : " + varDecl.startIndexOfScope + ", " + varDecl.endIndexOfScope + ")";
					return new CodeString("Local Variable " + typeName + " " + varName + scope + " - " + returnTypeOfFunc + " " + funcName + "()", textColor);
				}
			}
		} // if (varUse.varDecl!=null) {
		else if (varUse.funcDecl!=null) { // function call
			FindFunctionParams func = varUse.funcDecl;
			String fileName = func.compiler.filename;
			int k;
			String docuComment = null;
			if (func.docuComment!=null && func.docuComment.str!=null) {
				docuComment = func.docuComment.str;
			}
			
			String className = "";
			if (func.parent!=null) {
				FindClassParams classP = (FindClassParams)func.parent;
				className = classP.name + ".";
			}
			
			String funcName;
			funcName = func.name;
			String returnTypeOfFunc;
			returnTypeOfFunc = func.getReturnType(func.compiler.mBuffer, 
					func.returnTypeStartIndex(), func.returnTypeEndIndex());
			
			/*String fullnameTypeCast = FindVarUseParams.getFullnameTypeCast(varUse.typeCastedByVarUse);
			if (fullnameTypeCast!=null) {
				returnTypeOfFunc += "(" + fullnameTypeCast + ")";
			}*/
			
			boolean isOuterFile = false;
			String args = "";
			for (k=0; k<func.listOfFuncArgs.count; k++) {
				FindVarParams arg = (FindVarParams)func.listOfFuncArgs.getItem(k);
				if (arg.fieldName==null) {// 외부 클래스
					String typeName = arg.typeName;
					args += typeName;
					isOuterFile = true;
				}
				else {
					String typeName = arg.getType(src, arg.typeStartIndex(), arg.typeEndIndex());
					args += typeName + " " + arg.fieldName;
				}
				if (k!=func.listOfFuncArgs.count-1) {
					args += ", ";
				}
			}
			
			boolean isStatic = func.accessModifier.isStatic;
			//String message = isStatic ? "S : " : "";
			String message = func.accessModifier.toString();
			
			String packageNameDot;
			if (packageName.equals("")) { // C의 경우
				packageNameDot = "";
			}
			else {
				packageNameDot = packageName + ".";
			}
			
			if (docuComment!=null)
				return new CodeString(fileName + "\n" + docuComment + "\n" + message + returnTypeOfFunc + " " + className + funcName + "(" + args + ")", textColor);
			else 
				return new CodeString(fileName + "\n" + message + returnTypeOfFunc + " " + className + funcName + "(" + args + ")", textColor);
		} // else if (varUse.funcDecl!=null) { // function call
		else {
			String varUseName = src.getItem(varUse.index()).str;
			CodeString varUseName2 = new CodeString(varUseName,textColor);
			if (IsDefaultType(varUseName)) { // a = new int[5+1]; 에서 int
				return varUseName2;
			}
			else {
				int numberType = CompilerHelper.IsNumber2(varUseName2);
				if (numberType!=0) {
					CodeString type = new CodeString(" : " + 
							CompilerHelper.getNumberString(numberType),keywordColor);
					
					return varUseName2.concate(type);
				}				
			}
		}
		
		
		// varUse가 함수호출일 경우 함수에 바인딩이 안되면 파라미터들과 그것의 타입 리스트를 출력한다.
		if (varUse.listOfFuncCallParams!=null && varUse.listOfFuncCallParams.count>0 &&
			varUse.funcDecl==null) {
			int i;
			HighArray_char r = new HighArray_char(20);
			r.add("func decl not bound. Check a parameter list.\n\n");
			for (i=0; i<varUse.listOfFuncCallParams.count; i++) {
				FindFuncCallParam funcCall = (FindFuncCallParam) varUse.listOfFuncCallParams.getItem(i);
				String str = this.getFullName(src, funcCall.startIndex(), funcCall.endIndex()).str;
				r.add(str);
				r.add(" : ");
				r.add(funcCall.typeFullName.str);
				r.add('\n');
			}
			return new CodeString(r.getItems(), Compiler.textColor);
		}
	
		return null;
	}
	
	/** 클래스 정의나 enum 정의의 머릿부분을 사용자가 터치시 호출이 된다.*/
	public CodeString findNode_class_makeString(HighArray_CodeString src, FindClassParams classP, int indexInmBuffer) {
		String r;
		
		if (classP.classNameIndex()==indexInmBuffer) {		// 클래스 정의문에서 클래스이름을 터치시
			CodeString className = src.getItem(indexInmBuffer);
			String docuComment = null;
			if (classP.docuComment!=null && classP.docuComment.str!=null) {
				docuComment = classP.docuComment.str;
			}
			boolean isStatic = false;
			/*if (classP.accessModifier!=null)
					isStatic = classP.accessModifier.isStatic;					
			String message = isStatic ? "S" : "";*/
			String message = classP.accessModifier.toString();
			if (classP.isEnum==false) { // class
				if (message.length()>0) {
					message = message + ", Class : ";
				}
				else {
					message = "Class : ";
				}
			}
			else { // enum
				if (message.length()>0) {
					message = message + ", Enum : ";
				}
				else {
					message = "Enum : ";
				}
			}
			
			String classNameStr = getFullNameExceptPackageName(src, classP  );
			classNameStr = classNameStr.substring(0, classNameStr.length()-1);
			if (docuComment!=null)
				return new CodeString(docuComment + "\n" + message + packageName + "." + classNameStr, textColor);
			else
				return new CodeString(message + packageName + "." + classNameStr, textColor);
		}//if (classP.classNameIndex==indexInmBuffer) {		// 변수를 터치시
		return null;
	}
	
	/** 사용자가 변수선언을 터치시 호출된다.*/
	public CodeString findNode_var_makeString(HighArray_CodeString src, FindVarParams var, int indexInmBuffer) {
		CodeString r;
		// var.varNameIndex()==-1인 경우는 MenuClassList에서 상속 변수선언을 터치한 경우이다.
		if (var.varNameIndex()==indexInmBuffer || var.varNameIndex()==-1) {		// 변수를 터치시
			CodeString varName = new CodeString(var.fieldName,textColor);
			if (var.isMemberOrLocal || var.isEnumElement) {
				String docuComment = null;
				if (var.docuComment!=null && var.docuComment.str!=null) {
					docuComment = var.docuComment.str;
				}
				boolean isStatic = false;
				/*if (var.accessModifier!=null)
						isStatic = var.accessModifier.isStatic;					
				String message = isStatic ? "S,M : " : "M : ";*/
				String message = var.accessModifier.toString();
				message += "Member Variable ";
				String typeName = "";
				//if (var.typeStartIndex()!=-1 && var.typeEndIndex()!=-1)
				//	typeName = var.getType(src, var.typeStartIndex(), var.typeEndIndex());
				typeName = var.typeName;
				String className;
				if (var.varNameIndex()!=-1) {
					className = getFullNameExceptPackageName(src, (FindClassParams)getParent( (Block)(var.parent) ) );
					if (docuComment!=null)
						return new CodeString(docuComment + "\n" + message + typeName + " " + packageName + "." + className + varName, textColor);
					else
						return new CodeString(message + typeName + " " + packageName + "." + className + varName, textColor);
				}
				else {
					className = ((FindClassParams)var.parent).name;
					if (docuComment!=null)
						return new CodeString(docuComment + "\n" + message + typeName + " " + className + "." + varName, textColor);
					else
						return new CodeString(message + typeName + " " + className + "." + varName, textColor);
				}
				
				
			}
			else if (var.isMemberOrLocal==false) {
				FindFunctionParams func = (FindFunctionParams)getParent( (Block)(var.parent) ); 
				String funcName = ((FindClassParams)func.parent).name + "." + src.getItem(func.functionNameIndex()).toString();
				String returnTypeOfFunc;
				if (func.returnTypeEndIndex()<0) {
					returnTypeOfFunc = "";
				}
				else {
					returnTypeOfFunc = func.getReturnType(src, 
							func.returnTypeStartIndex(), func.returnTypeEndIndex());
				}
				String typeName = var.getType(src, var.typeStartIndex(), var.typeEndIndex());
				
				String scope = "(scope : " + var.startIndexOfScope + ", " + var.endIndexOfScope + ")";
				
				return new CodeString("Local Variable " + typeName + " " + varName + scope + " - " + returnTypeOfFunc + " " + funcName + "()", textColor);
				
			}
			
			
		}//if (var.varNameIndex()==indexInmBuffer) {		// 변수를 터치시
		// 변수선언의 타입을 터치시
		else if (var.typeStartIndex()<=indexInmBuffer && indexInmBuffer<=var.typeEndIndex()) {
			String docuComment = null;
			FindClassParams typeClassParams = CompilerHelper.loadClass(this, var.typeName);
			if (typeClassParams!=null && typeClassParams.docuComment!=null && typeClassParams.docuComment.str!=null) {
				docuComment = typeClassParams.docuComment.str;
			}
			if (docuComment!=null) {
				r = new CodeString(docuComment + "\n" + var.getType(src, var.typeStartIndex(), var.typeEndIndex()), textColor);
			}
			else {
				r = new CodeString(var.getType(src, var.typeStartIndex(), var.typeEndIndex()), textColor);
			}
			return r;
		}
		
		return null;
	}
	
	Error getError(int indexInmBuffer) {
		int i;
		for (i=0; i<errors.count; i++) {
			Error err = (Error) errors.getItem(i);
			if (err.startIndex()<=indexInmBuffer && indexInmBuffer<=err.endIndex()) {
				return err;
			}
		}
		return null;		
	}
	
	/** 타입선언문에서 변수 타입이나 변수를 사용자가 터치시 나타나는 툴팁을 위한 호출의 sub_sub이다.*/
	public CodeString findNode_sub_sub(HighArray_CodeString src, FindClassParams classParams, int indexInmBuffer) {
		if (classParams==null) {
			Object node=null;
			node = findNode_NodeType(src, classParams, indexInmBuffer);			
			if (node instanceof FindVarUseParams) {
				FindVarUseParams varUse = (FindVarUseParams) node;
				
				CodeString r = null;			
				
				CodeString r1 =  findNode_varUse_makeString_postfix(src, varUse, indexInmBuffer);
				// 수식트리의 방문표시를 다시 초기화를 해야한다.
				//findNode_varUse_makeString_postfix_reset(src, varUse, indexInmBuffer);
				
				if (r1!=null) {
					r = r1.concate(new CodeString("\n",textColor));
					CodeString r2 = findNode_varUse_makeString(src, varUse, indexInmBuffer);
					if (r2!=null) {
						r = r.concate(r2);
					}
				}
				else {
					CodeString r2 = findNode_varUse_makeString(src, varUse, indexInmBuffer);
					r = r2;
				}
				return r;
			} 
		}//if (classParams==null) {
		else if (classParams.startIndex()<=indexInmBuffer && indexInmBuffer<=classParams.endIndex()) {
			CodeString r=null;
			int i;
			if (classParams.childClasses!=null) {
				for (i=0; i<classParams.childClasses.count; i++) {
					FindClassParams child = (FindClassParams) classParams.childClasses.getItem(i);
					r = findNode_sub_sub(src, child, indexInmBuffer);
					if (r!=null) return r;
				}
			}
			
			Object node=null;
			node = findNode_NodeType(src, classParams, indexInmBuffer);
			
			if (node==null) {
				
			}//if (node==null) {
			
			if (node instanceof FindClassParams) {
				FindClassParams classP = (FindClassParams)node;
				return findNode_class_makeString(src, classP, indexInmBuffer);
			}// if (node instanceof FindVarParams) {
			
			else if (node instanceof FindVarUseParams) {
				FindVarUseParams varUse = (FindVarUseParams) node;
				CodeString result = null;
				
				// varUse를 포함하는 최소의 문장을 찾는다.
				FindStatementParams statement = codeGen.findFindStatementParams(varUse);
				HighArrayCharForByteCode byteCodeResult = new HighArrayCharForByteCode(500);
				//byteCodeResult.resizeInc = 500;
				if (statement instanceof FindControlBlockParams) {
					FindControlBlockParams controlBlock = (FindControlBlockParams) statement;
					if (controlBlock.catOfControls!=null) {
						if (controlBlock.catOfControls.category==CategoryOfControls.Control_if) {
							codeGen.printFindStatementParams(statement, byteCodeResult);
						}
						else { // if문을 제외한 다른 제어구조
							codeGen.printFindStatementParams_findNode(statement, byteCodeResult);
						}
					}
					else {
						// try, catch 등
						
					}
				}
				else {				
					codeGen.printFindStatementParams_findNode(statement, byteCodeResult);
				}
				
				byteCodeResult.add('\n');
				CodeString byteCode = new CodeString(new String(byteCodeResult.getItems()), textColor);
						
				CodeString r1 =  findNode_varUse_makeString_postfix(src, varUse, indexInmBuffer);
				// 수식트리의 방문표시를 다시 초기화를 해야한다.
				//findNode_varUse_makeString_postfix_reset(src, varUse, indexInmBuffer);
								
				if (r1!=null) {
					result = r1.concate(new CodeString("\n",textColor));
					
					/*HighArrayCharForByteCode byteCodeResult = new ArrayListChar(500);
					byteCodeResult.resizeInc = byteCodeResult.count;
					codeGen.traverseExpressionTree(mBuffer, varUse, varUse.index(), byteCodeResult);
					result = result.concate(
							new CodeString(new String(byteCodeResult.getItems()), Compiler.textColor));*/
					
					CodeString r2 = findNode_varUse_makeString(src, varUse, indexInmBuffer);
					if (r2!=null) {
						result = result.concate(r2);
					}
				}
				else {
					CodeString r2 = findNode_varUse_makeString(src, varUse, indexInmBuffer);
					result = r2;
				}
				
				if (result!=null) {
					result = byteCode.concate(result);
				}
				else {
					result = byteCode;
				}
				
				return result;
			}//else if (node instanceof FindVarUseParams) {
			else if (node instanceof FindVarParams) {
				FindVarParams var = (FindVarParams)node;
				return findNode_var_makeString(src, var, indexInmBuffer);
			}// if (node instanceof FindVarParams) {
			
			else if (node instanceof FindFunctionParams) {
				CodeString result = null;
				FindFunctionParams func = (FindFunctionParams) node;
				result = findNode_func_makeString(src, func, indexInmBuffer);
				
				HighArrayCharForByteCode byteCodeResult = null;
				CodeString byteCode = null;
				
				byteCodeResult = new HighArrayCharForByteCode(1000);
				//byteCodeResult.resizeInc = 800;
				codeGen.printFindStatementParams(func, byteCodeResult);
				byteCodeResult.add('\n');
				
				byteCode = new CodeString(new String(byteCodeResult.getItems()), textColor);
				if (result!=null) byteCode = byteCode.concate(result);
				
				result = byteCode;
				
				return result;
			}//else if (node instanceof FindFunctionParams) {
			else if (node instanceof FindControlBlockParams) {
				FindControlBlockParams controlBlock = (FindControlBlockParams) node;
				CodeString str = src.getItem(indexInmBuffer);
				CodeString result = null;
				
				HighArrayCharForByteCode byteCodeResult = null;
				CodeString byteCode = null;
				if (controlBlock.catOfControls==null) {
					if (str.equals("try")) {
						byteCodeResult = new HighArrayCharForByteCode(500);
						//byteCodeResult.resizeInc = 500;
						codeGen.printFindStatementParams(controlBlock, byteCodeResult);
					}
					else if (str.equals("catch") || str.equals("finally")) { // if문을 제외한 다른 제어구조
						byteCodeResult = new HighArrayCharForByteCode(200);
						//byteCodeResult.resizeInc = 200;
						codeGen.printFindStatementParams_findNode(controlBlock, byteCodeResult);
						
					}
					else if (str.equals("synchronized")) {
						byteCodeResult = new HighArrayCharForByteCode(500);
						//byteCodeResult.resizeInc = 500;
						codeGen.printFindStatementParams(controlBlock, byteCodeResult);
					}
					if (byteCodeResult!=null) {
						byteCodeResult.add('\n');
						byteCode = new CodeString(new String(byteCodeResult.getItems()), textColor);
						result = byteCode;
					}
				}
				
				CodeString makeString = findNode_controlBlock_makeString(src, controlBlock, indexInmBuffer);
				if (result!=null) {
					if (makeString!=null) {
						result = result.concate(makeString);
					}
				}
				else {
					result = makeString;
				}
				
				
				return result;
			}
			
		}//else if (classParams.startIndex()<=indexInmBuffer/* && indexInmBuffer<=classParams.endIndex()*/) { 
		return null;
		
	}
	
	
	
	
	
	
	ArrayListIReset getAllVarUses(HighArray_CodeString src, ArrayListIReset listOfAllVarUses, int startIndex, int endIndex) {
		int i;
		ArrayListIReset listOfVarUses = new ArrayListIReset(10);
		listOfVarUses.resizeInc = 10;
		for (i=0; i<listOfAllVarUses.count; i++) {
			FindVarUseParams varUse = (FindVarUseParams)listOfAllVarUses.getItem(i);
			if (startIndex<=varUse.index() && varUse.index()<=endIndex) {
				listOfVarUses.add(varUse);
			}
			else if (varUse.index()>endIndex) {
				break;
			}
		}
		return listOfVarUses;
	}
	
	String getNameSpaceElement(String path, int index) {
		int i;
		int[] indicesDots = new int[20];
		int k=0;
		for (i=0; i<path.length(); i++) {
			if (path.charAt(i)=='.') {
				indicesDots[k] = i;
				k++;
			}
		}
		
		String r=null;
		if (indicesDots.length>0) {
			if (1<=index && index<k) {
				r = path.substring(indicesDots[index-1]+1, indicesDots[index]);
			}
			else if (0==index) {
				r = path.substring(0, indicesDots[0]);
			}
			else if (index==k) {
				r = path.substring(indicesDots[k-1]+1, path.length());
			}
			else return null;			
		}
		else {
			r = path;
		}
		return r;
					
	}
	
	
	/** 꼭대기(com.gsoft.common의 com)에 있는 FindPackageParams를 리턴한다.*/
	Object findChildMember2(HighArray_CodeString src, String elementName) {
		int i;
		for (i=0; i<mlistOfPackages.length; i++) {
			String name = mlistOfPackages[i].packageName;
			if (elementName.equals(name)) {
				return mlistOfPackages[i];
			}
		}
		return null;
	}
	
		
	
	/** 패키지나 클래스, 즉 parent의 멤버를 이름(elementName)으로 찾는다.
	 * @param  elementName : 찾아야할 멤버 이름
	 * @param parent : FindPackageParams, FindClassParams
	 * @return Object : 멤버들, FindPackageParams, FindClassParams, FindVarParams, FindFunctionParams*/
	Object findChildMember_sub2(HighArray_CodeString src, FindVarUseParams childVarUse, Object parent, int indexInmListOfAllVarUses) {
		int i;
		String elementName = childVarUse.originName;
		if (parent instanceof FindPackageParams) {
			FindPackageParams p = (FindPackageParams)parent;
			for (i=0; i<p.listOfChildrenNames.length; i++) {
				String childName = p.listOfChildrenNames[i];
				if (childName.equals("IO.class")) {
					int a;
					a=0;
					a++;
				}
				String name = FileHelper.getFilenameExceptExt(childName);
				if (elementName.equals(name)) {
					String parentFullName =  
							((FindPackageParams) parent).getFullName(1);
					String ownFullName = parentFullName + File.separator + childName;
					File file = new File(Control.pathAndroid + File.separator + ownFullName);
					if (file.isDirectory()) { // 디렉토리이면 패키지를 생성하고 리턴
						String[] listOfChildrenNames = file.list();					
						FindPackageParams packageChild = new FindPackageParams(childName, parentFullName, listOfChildrenNames); 
						return packageChild;
					}
					else { // 디렉토리가 아닌 파일, .class파일일 경우 클래스를 읽어들인다.
						String ext = FileHelper.getExt(ownFullName);
						if (ext.equals(".class") || ownFullName.contains("$")==false) {
							String fullClassName = ownFullName.replace(File.separatorChar, '.');
							fullClassName = FileHelper.getFilenameExceptExt(fullClassName);
							FindClassParams classParams = CompilerHelper.loadClass(this, fullClassName);
							
							return classParams;
						}
					}
				} //if
			} // for
		}
		else if (parent instanceof FindClassParams) {
			FindClassParams p = (FindClassParams)parent;
			// 한 클래스의 모든 내부 클래스를 읽는게 아니라 필요한 내부 클래스만 읽는다.
			if (p.childClasses==null) {
				boolean hasInnerClass = 
						CompilerHelper.hasInnerClass(p.name, elementName, true);
				if (hasInnerClass) {
					String typeNameFull = p.name + "." + elementName;
					FindClassParams child = CompilerHelper.loadClass(this, typeNameFull);
					return child;
				}
			}
			else if (p.childClasses!=null) {
				for (i=0; i<p.childClasses.count; i++) {
					FindClassParams child = (FindClassParams)p.childClasses.getItem(i);
					String name = getShortName(child.name);
					if (elementName.equals(name)) {
						return child;
					}
				}
			}
			if (p.listOfVariableParams!=null) {
				for (i=0; i<p.listOfVariableParams.count; i++) {
					FindVarParams child = (FindVarParams)p.listOfVariableParams.getItem(i);
					String name = getShortName(child.fieldName);
					if (elementName.equals(name)) {
						return child;
					}
				}
			}
			if (p.listOfVarParamsInherited!=null) {
				for (i=0; i<p.listOfVarParamsInherited.count; i++) {
					FindVarParams child = (FindVarParams)p.listOfVarParamsInherited.getItem(i);
					String name = getShortName(child.fieldName);
					if (elementName.equals(name)) {
						return child;
					}
				}
			}
			if (p.listOfFunctionParams!=null) {
				for (i=0; i<p.listOfFunctionParams.count; i++) {
					try{
					FindFunctionParams child = (FindFunctionParams)p.listOfFunctionParams.getItem(i);
					String name = getShortName(child.name);
					if (elementName.equals(name)) {
						
						child = getFunction(src, p, childVarUse, indexInmListOfAllVarUses);
						
						return child;
					}
					}catch(Exception e) {
						int a;
						a=0;
						a++;
					}
				}
			}
			if (p.listOfFunctionParamsInherited!=null) {
				for (i=0; i<p.listOfFunctionParamsInherited.count; i++) {
					try{
					FindFunctionParams child = (FindFunctionParams)p.listOfFunctionParamsInherited.getItem(i);
					String name = getShortName(child.name);
					if (elementName.equals(name)) {
						
						child = getFunction(src, p, childVarUse, indexInmListOfAllVarUses);
						
						return child;
					}
					}catch(Exception e) {
						int a;
						a=0;
						a++;
					}
				}
			}
		}
		
		return null;
	}
	
	
	
	/** loadLibraries에서 로드한 패키지, 클래스로 구성된 트리에서 
	 * Package2, FindVarParams, FindFunctionParams, FindClassParams를 찾는다. 
	 * @param src
	 * @param path : java.lang.String과 같이 full name으로 구성된다.
	 * @return : Package2(com, gsoft등), FindVarParams(필드), FindFunctionParams(메서드), FindClassParams(내부클래스)
	 */
	Object findChildMember(HighArray_CodeString src, String path) {
		int i;
		if (path.contains(".")==false) {
			for (i=0; i<this.mlistOfPackages2.length; i++) {
				String name = mlistOfPackages2[i].name;
				if (path.equals(name)) {
					return mlistOfPackages2[i];
				}
			}
		}
		else {
			Object p = null;
			for (i=0; i<this.mlistOfPackages2.length; i++) {
				String name = mlistOfPackages2[i].name;
				if (path.equals(name)) {
					p = mlistOfPackages2[i];
					break;
				}
			}
			
			int index;
			index = FileHelper.getCount(path, '.');			
			
			for (i=0; i<index; i++) {
				String element = getNameSpaceElement(path, index);
				p = findChildMember_sub(src, element, p);
				if (p==null) return null;
			}
			return p;
			
		}
		return null;
	}
	
	/** 패키지나 클래스, 즉 parent의 멤버를 이름(elementName)으로 찾는다.
	 * @param  elementName : 찾아야할 멤버 이름
	 * @param parent : FindPackageParams, FindClassParams
	 * @return Object : 멤버들, FindPackageParams, FindClassParams, FindVarParams, FindFunctionParams*/
	Object findChildMember_sub(HighArray_CodeString src, String elementName, Object parent) {
		int i;
		if (parent instanceof Package2) {
			Package2 p = (Package2)parent;
			for (i=0; i<p.listOfChildPackages.count; i++) {
				Package2 child = (Package2)p.listOfChildPackages.getItem(i);
				String name = child.name;
				if (elementName.equals(name)) {
					return child;
				}
			}
			for (i=0; i<p.listOfClasses.count; i++) {
				FindClassParams child = (FindClassParams)p.listOfClasses.getItem(i);
				String name = getShortName(child.name);
				if (elementName.equals(name)) {
					return child;
				}
			}
		}
		else if (parent instanceof FindClassParams) {
			FindClassParams p = (FindClassParams)parent;
			for (i=0; i<p.childClasses.count; i++) {
				FindClassParams child = (FindClassParams)p.childClasses.getItem(i);
				String name = getShortName(child.name);
				if (elementName.equals(name)) {
					return child;
				}
			}
			for (i=0; i<p.listOfVariableParams.count; i++) {
				FindVarParams child = (FindVarParams)p.listOfVariableParams.getItem(i);
				String name = getShortName(child.fieldName);
				if (elementName.equals(name)) {
					return child;
				}
			}
			for (i=0; i<p.listOfFunctionParams.count; i++) {
				FindFunctionParams child = (FindFunctionParams)p.listOfFunctionParams.getItem(i);
				String name = getShortName(child.name);
				if (elementName.equals(name)) {
					return child;
				}
			}
			
		}
		
		return null;
	}
	
	
	
	/*void findMemberUsesUsingNamespace_library(HighArray_CodeString src, HighArray<FindVarUseParams> listOfAllVarUses) {
		int i, j, index;
		// 이 파일에서 정의되지 않은 패키지(FindPackageParams)의
		// 타입을 정의한다.
		if (mlistOfPackages!=null) {
			int len = listOfAllVarUses.getCount();
			for (i=0; i<len; i++) {
				FindVarUseParams varUse = listOfAllVarUses.getItem(i);
				
				index = varUse.index();
				String varUseName = src.getItem(index).toString();
				
				if (varUseName.equals("com")) {
					int a;
					a=0;
					a++;
				}
				
				if (varUse.parent!=null) continue;
				
				for (j=0; j<mlistOfPackages.length; j++) {
					String packageName = mlistOfPackages[j].packageName;
					if (varUseName.equals("Subset")) {
						int a;
						a=0;
						a++;
					}
					if (varUseName.equals(packageName)) {
						//String[] children = findChildPackages(src, Control.pathAndroid+File.separator+packageName); 
						//varUse.memberDecl = new FindPackageParams(packageName, "", children);
						varUse.memberDecl = this.findChildMember2(src, packageName);
						break;
					}
				}
			}
		}
	}*/
	
	/** varUse가 com, android, java와 같은 꼭대기에 있는 이름일 경우 varUse의 memberDecl에 연결한다.*/ 
	void findMemberUsesUsingNamespace_library2(HighArray_CodeString src, FindVarUseParams varUse) {
		int i, j, index;
		// 이 파일에서 정의되지 않은 패키지(FindPackageParams)의
		// 타입을 정의한다.
		if (mlistOfPackages!=null) {
			index = varUse.index();
			String varUseName = src.getItem(index).toString();
			
			if (varUseName.equals("com")) {
				int a;
				a=0;
				a++;
			}
			
			if (varUse.parent!=null) return;
			
			for (j=0; j<mlistOfPackages.length; j++) {
				String packageName = mlistOfPackages[j].packageName;
				if (varUseName.equals("Subset")) {
					int a;
					a=0;
					a++;
				}
				if (varUseName.equals(packageName)) {
					//String[] children = findChildPackages(src, Control.pathAndroid+File.separator+packageName); 
					//varUse.memberDecl = new FindPackageParams(packageName, "", children);
					varUse.memberDecl = this.findChildMember2(src, packageName);
					break;
				}
			}
		}
	}
	
	
	/** 외부에서 멤버를 이용한 변수(배열원소포함)나 함수의 타입을 결정한다. a.b.c나 a.b.c()등과 같이, 
	 * 또한 타입캐스트문도 여기에서 해결한다. getTypeOfExpression()등에서 호출
	 * @param startIndex() :  src에서의 검색 시작 인덱스
	 * @param endIndex() :  src에서의 검색 끝 인덱스 */ 
	void findMemberUsesUsingNamespace_sub(HighArray_CodeString src, 
			int startIndex, int endIndex) {
		int endIndexInmListOfAllVarUses;
		int startIndexInmListOfAllVarUses;
		int startIndexInmBuffer;
		
		startIndexInmBuffer = SkipBlank(src, false, startIndex, endIndex);
		startIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, mlistOfAllVarUses, 
				0, startIndexInmBuffer, true);
		endIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, mlistOfAllVarUses, 
				startIndexInmListOfAllVarUses, endIndex, false);
		if (endIndexInmListOfAllVarUses!=-1) {
			// 함수호출의 매개변수당 한번씩 recursive call
			findMemberUsesUsingNamespace_sub(src, mlistOfAllVarUses, 
				startIndexInmListOfAllVarUses, endIndexInmListOfAllVarUses);
		}
	}
	
	/** 외부에서 멤버를 이용한 변수(배열원소포함)나 함수의 타입을 결정한다. a.b.c나 a.b.c()등과 같이, 
	 * 또한 타입캐스트문도 여기에서 해결한다. getTypeOfExpression()등에서 호출한다.
	 * 타입선언시 내부클래스가 있는 경우에 fullname타입 결정과 클래스로드를 한다. 
	 *	타입선언시 shortname만 있는 경우는 getFullNameType, getFullNameType2에서 모두 찾는다.
	 *	예를들어 java.lang패키지에 있는 클래스 Character.Subset s;에서 Characterd와 Subset는 여기서 로드된다. 
	 * 또한 같은 패키지내 클래스와 import java.util.*;와 같이 *를 섞어서 import하는 경우에 내부클래스가 있는 경우 여기서 해야 한다. 
	 * @param listOfAllVarUses : mlistOfAllVarUses
	 * @param startIndex() :  mlistOfAllVarUses에서의 검색 시작 인덱스
	 * @param endIndex() :  mlistOfAllVarUses에서의 검색 끝 인덱스 */ 
	void findMemberUsesUsingNamespace_sub(HighArray_CodeString src, HighArray<FindVarUseParams> listOfAllVarUses, 
			int startIndex, int endIndex) 
	{
		int i, j;
		int index, prevIndex;
		boolean isConstructor = false;
		FindVarUseParams child;
		TypeCast typeCast=null;
		int startIndexToTypeCast=-1, endIndexToTypeCast=-1;
		for (i=startIndex; i<=endIndex; i++) {
			try {
				/*CommonGUI.loggingForMessageBox.setText(true, i + " completed.", false);
				CommonGUI.loggingForMessageBox.setHides(false);
				Control.view.postInvalidate();*/
				//Thread.sleep(1000);
				if (i==16831) {  // index = 12456
					int a;
					a=0;
					a++;
				}
				
			FindVarUseParams varUse = (FindVarUseParams)listOfAllVarUses.getItem(i);
			
			
			index = varUse.index();
			String varUseName = src.getItem(index).toString();
			
			if (index==865) {
				int a;
				a=0;
				a++;
			}
			
			if (varUse.child!=null && varUse.child.index()==809) {
				int a;
				a=0;
				a++;
			}
			
			// com, android, java와 같은 꼭대기 이름을 memberDecl에 연결한다.
			if (varUse.varDecl==null && varUse.funcDecl==null && varUse.memberDecl==null) {
				this.findMemberUsesUsingNamespace_library2(src, varUse);
			}
			
			
						
			if (varUse.typeCast!=null) {
				if (varUseName.equals("RectForPage")) {
					int a;
					a=0;
					a++;
				}
				if (varUseName.equals("Object")) {
					int a;
					a=0;
					a++;
				}
				typeCast = varUse.typeCast; 
				startIndexToTypeCast = varUse.typeCast.startIndexToAffect_mlistOfAllVarUses;
				endIndexToTypeCast = varUse.typeCast.endIndexToAffect_mlistOfAllVarUses;
				
				// (int)( 2+(int)1 ) 이와같은 (타입)(수식)일 경우
				//if (typeCast.affectsExpression) {
					if (typeCast.funcCall==null) {
						FindFuncCallParam funcCall = new FindFuncCallParam(this, typeCast.startIndexToAffect(), typeCast.endIndexToAffect());
						funcCall.funcName = typeCast.name;
						// getTypeOfExpression에서 findMemberUsesUsingNamespace_sub을 다시 recursive call해서 
						// 타입캐스트 (int)1의 타입을 결정하고 2+(int)1의 타입을 결정한 후 funcCall에 넣는다.
						CodeStringEx type = getTypeOfExpression(src, funcCall);
						
						if (TypeCast.isCompatibleType(this, new CodeStringEx(funcCall.funcName), type, 0, funcCall)==false) {
							errors.add(new Error(this, typeCast.startIndex(), typeCast.startIndex(), 
									"invalid type cast. : " + funcCall.funcName + "-" + type));
						}
						else {
							funcCall.typeFullNameBeforeTypeCast = type.str;
							funcCall.typeFullName = new CodeStringEx(typeCast.name);
							typeCast.funcCall = funcCall;
						}						
					}
					// 타입캐스트를 해결하였으므로 초기화
					//typeCast = null;
				//}				
			}
			
			
			int indexFullName = this.getFullNameIndex0(src, true, index-1);
			prevIndex = SkipBlank(src, true, 0, indexFullName-1);
			if (prevIndex!=-1) {
				if (src.getItem(prevIndex).equals("new")) {
					isConstructor = true;
				}
				else isConstructor = false;
			}
			
			if (varUseName.equals("MessageDialog")) {
				int a;
				a=0;
				a++;
			}
			
			if (varUseName.equals("stack")) {
				int a;
				a=0;
				a++;
			}
			else if (varUseName.equals("com")) {
				int a;
				a=0;
				a++;
			}
			
			child = varUse.child;
			
			// child가 이미 정해진 경우는 continue
			if (child!=null && (child.varDecl!=null || child.funcDecl!=null)) continue;
			
			if (varUseName.equals("Compiler")) {
				int a;
				a=0;
				a++;
			}
			
			// 이 파일에서 정의되지 않은 외부 라이브러리
			// (android.graphics.Paint, com.gsoft.common와 같은)의 
			// 패키지(FindPackageParams)와 클래스와 그 멤버변수의
			// 타입을 정의한다.
			if (varUse.memberDecl!=null) {
				if (varUseName.equals("Character")) {
					int a;
					a=0;
					a++;
				}
				if (varUse.child!=null) {
					//FindVarUseParams child = varUse.child;
					CodeString strChild = src.getItem(child.index());
					if (child.memberDecl==null) {
						// 이 함수의 마지막부분 boolean isInnerClassIn = 
						// CompilerHelper.hasInnerClass(classParam.name, varUse.child.originName, true);에서 
						// 미리 memberDecl이 정해질 수 있다.
						child.memberDecl = this.findChildMember_sub2(src, child, varUse.memberDecl, i+1);
						if (child.memberDecl!=null) {
							if (child.memberDecl instanceof FindVarParams) {
								child.varDecl = (FindVarParams) child.memberDecl;
								child.memberDecl = null;
							}
							// static 함수일 경우는 여기에서 처리하지 않고 다음으로 넘긴다.
							else if (child.memberDecl instanceof FindFunctionParams) {
								child.funcDecl = (FindFunctionParams) child.memberDecl;
								child.memberDecl = null;
							}
							// ClipBoardX = new com.gsoft.DataTransfer.ClipBoardX(); 에서
							// varUse는 DataTransfer이고 child는 ClipBoardX이다. 
							// 이런 경우는 이 함수의 가장 아랫부분에서 정의한다.
							// static class
							else if (child.memberDecl instanceof FindClassParams) {
								if (child.isForVarOrForFunc==false) {
									FindClassParams classP = (FindClassParams) child.memberDecl;
									child.funcDecl = getFunction(src, classP, varUse.child, i+1);
									child.memberDecl = null;
								}
							}
							continue;
						}
					}//if (child.memberDecl==null) {
				}
			}//if (varUse.memberDecl!=null) {
			
			// child가 이미 정해진 경우는 continue
			if (varUse.child!=null && varUse.child.memberDecl!=null) continue;
			
			// this이면 continue		
			//if (varUse.varDecl!=null && varUse.varDecl.isThis) continue;
			
						
			
			//found = false;
			
			// varUse의 타입에 따라 클래스를 로드한다.
			
			String typeName;
			String typeNameFull = null;
			FindClassParams classParam=null;
			boolean isThisOrSuper = false;
			
			// 멤버변수나 멤버함수의 varUse로 가정한다.
			if (varUse.parent==null || (src.getItem(varUse.parent.index()).equals("this") || src.getItem(varUse.parent.index()).equals("super")))
				isThisOrSuper = true;
			
			// varUse는 this, super
			if ((varUseName.equals("this") || varUseName.equals("super")) && 
					varUse.isForVarOrForFunc && varUse.varDecl==null) {
				if (varUseName.equals("super")) {
					int a;
					a=0;
					a++;
				}
				varUse.varDecl = this.getMemberVar(src, varUse.classToDefineThisVarUse, varUse);
				if (varUse.varDecl==null) continue;
				typeNameFull = varUse.varDecl.typeName;			
				classParam = CompilerHelper.loadClass(this, typeNameFull);
				if (classParam==null) continue;				
				/*if (varUse.child!=null && varUse.varDecl!=null) {  // new A().b에서 b를 찾기위해 다시 처리
					i--;
					continue;
				}*/
			}
			
			// 동일클래스의 멤버변수, varUse의 parent는 null이거나 this, super이어야 한다.
			else if (isConstructor==false && isThisOrSuper && varUse.varDecl==null && varUse.isForVarOrForFunc) {
				if (varUse.index()==7564) {
					int a;
					a=0;
					a++;
				}
				if (varUse.classToDefineThisVarUse!=null) {
					varUse.varDecl = getMemberVar(src, varUse.classToDefineThisVarUse, varUse);
				}
				if (varUse.varDecl!=null) {
					try {
					typeNameFull = varUse.varDecl.typeName;
					}catch(Exception e) {
						int a;
						a=0;
						a++;
						e.printStackTrace();
					}
					classParam = CompilerHelper.loadClass(this, typeNameFull);
					if (classParam==null) continue;				
					/*if (varUse.child!=null && varUse.varDecl!=null) {  // new A().b에서 b를 찾기위해 다시 처리
						i--;
						continue;
					}*/
				}
			}
			
			// super(a,b);이와 같은 경우.
			else if ((varUseName.equals("super") && varUse.isForVarOrForFunc==false) && varUse.funcDecl==null) {
				if (varUse.index()==10136) {
					int a;
					a=0;
					a++;
				}
				classParam = varUse.classToDefineThisVarUse/*.classToExtend*/;
				if (classParam==null) continue;
				varUse.funcDecl = getFunction(src, classParam, varUse, i);
				if (varUse.funcDecl==null) continue;				
				/*if (varUse.child!=null && varUse.funcDecl!=null) {  // new A().b에서 b를 찾기위해 다시 처리
					i--;
					continue;
				}*/
			}
			
			// 동일클래스의 멤버함수, varUse의 parent는 this, super이어야 한다.
			// func1(func2()); 에서 func1, func2 모두 멤버함수이고 func1이 func2로 앞서는 경우를 해결할수있다.
			else if (isConstructor==false && isThisOrSuper && varUse.funcDecl==null && varUse.isForVarOrForFunc==false) {
				if (varUse.index()==7564) {
					int a;
					a=0;
					a++;
				}
				varUse.funcDecl = getMemberFunction(src, varUse);
				if (varUse.funcDecl!=null) {
					try {
					typeNameFull = varUse.funcDecl.returnType;
					}catch(Exception e) {
						int a;
						a=0;
						a++;
						e.printStackTrace();
					}
					classParam = CompilerHelper.loadClass(this, typeNameFull);
					if (varUse.funcDecl==null) continue;				
					/*if (varUse.child!=null && varUse.funcDecl!=null) {  // new A().b에서 b를 찾기위해 다시 처리
						i--;
						continue;
					}*/
				}
			}
			
			
			
			else if (varUse.memberDecl!=null) {
				if (varUse.memberDecl instanceof FindClassParams) {					
					classParam = (FindClassParams) varUse.memberDecl;
					typeNameFull = classParam.name;
					if (varUse.isArrayElement) {
						if (varUse.listOfArrayElementParams==null) {
							findArraySubscription(src, varUse, i, listOfAllVarUses);
						}
					}
				}
			}
			
			// a = new int[5+1]; b = new String[5+1]; 에서 int와 String
			else if (isConstructor && varUse.varDecl==null && varUse.isArrayElement && varUse.funcDecl==null) {
				//typeNameFull = getFullTypeNameOfArrayElement(src, varUse, i, listOfAllVarUses, true);
				//classParam = getFindClassParams(mlistOfAllClassesHashed, mlistOfAllClasses, typeNameFull);
				if (varUse.listOfArrayElementParams==null) {
					findArraySubscription(src, varUse, i, listOfAllVarUses);
				}
				// 타입검사는 findTypeOfAssignments()에서 처리
			}
			
			else if (isConstructor && varUse.isForVarOrForFunc==false && varUse.funcDecl==null) 
			{ // 외부 라이브러리의 생성자 호출 등
				// varUse가 상수이면 continue;
				if (CompilerHelper.IsConstant(src.getItem(index))) continue;
				typeName = varUseName;
				/*if (varUse.template==null) {
					typeNameFull = getFullNameType2(src, typeName, varUse.index());
				}
				else {
					if (varUse.template.typeName!=null && varUse.template.typeNameToChange!=null) {
						typeNameFull = varUse.template.typeName + "<" + varUse.template.typeNameToChange + ">";
					}
				}*/
				if (varUse.index()==1128) {
					int a;
					a=0;
					a++;
				}
				int startIndexOfFullName = this.getFullNameIndex0(src, true, varUse.index());
				int endIndexOfFullName = this.getFullNameIndex0(src, false, varUse.index());
				typeNameFull = this.getFullNameType(this, startIndexOfFullName, endIndexOfFullName);
				
				classParam = CompilerHelper.loadClass(this, typeNameFull);
				/*if (i==endIndexToTypeCast) { // (B) (new A())
					typeNameFull = typeCast.name;
					varUse.typeCastedByVarUse = typeCast.varUseTypeCasting;
					classParam = CompilerHelper.loadClass(this, typeNameFull);
				}*/
				if (classParam==null) continue;
				
				varUse.funcDecl = getFunction(src, classParam, varUse, i);
				if (varUse.funcDecl==null) continue;
				/*if (varUse.child!=null && varUse.funcDecl!=null) {  // new A().b에서 b를 찾기위해 다시 처리
					i--;
					continue;
				}*/
				
			}
			
			// varUse.varDecl이 널이 아니고 child가 있는 
			// 변수사용의 타입인 클래스의 멤버선언리스트
			/*else*/ if (varUse.varDecl!=null) { // varUse가 변수인경우
				
				if (varUseName.equals("stack")) {
					int a;
					a=0;
					a++;
				}
				if (varUse.varDecl.typeName!=null) { 
					if (varUse.isArrayElement==false) {
						typeName = varUse.varDecl.typeName;
						if (CompilerHelper.getArrayDimension(this, typeName)!=0) { 
							// 타입이 배열일 경우, buffer.length에서 buffer의 타입
							typeName = "com.gsoft.common.Array";
						}
						if (typeName!=null) {
							typeNameFull = getFullNameType2(src, typeName, varUse.varDecl.typeStartIndex());
							classParam = CompilerHelper.loadClass(this, typeNameFull);
						}
					}
					else {
						// ((Button)(controls[0])).changeBounds(boundsOfButtonOK); 
						// 여기에서 controls가 배열원소이므로 이것의 타입인 Control을 구한다.
						typeNameFull = getFullTypeNameOfArrayElement(src, varUse, i, listOfAllVarUses, true);
						classParam = CompilerHelper.loadClass(this, typeNameFull);
					}
				}	
			} // else if (varUse.varDecl!=null) { // varUse가 변수인경우
			else if (varUse.funcDecl!=null) {// varUse가 함수인경우
				if (varUse.funcDecl.returnType!=null) {
					typeNameFull = varUse.funcDecl.returnType;
				}
				
				if (varUse.isArrayElement) {
					// varUse가 함수호출이자 배열원소인 경우
					// char c = first.toCharArray()[0]; 아니면
					//Class c = (new ScrollBars_test4().toObjectArray())[0].getClass(); 에서
					//varUse는 toObjectArray이다.
					if (varUse.listOfArrayElementParams==null) {
						findArraySubscription(src, varUse, i, listOfAllVarUses);
					}
					int dimension1 = CompilerHelper.getArrayDimension(this, varUse.name);
					int dimension2 = CompilerHelper.getArrayDimension(this, typeNameFull);
					if (dimension1==dimension2) {
						typeNameFull = CompilerHelper.getArrayElementType(typeNameFull);
					}
					else {
						typeNameFull = null;
						errors.add(new Error(this, varUse.index(), varUse.index(), "Array dimension is invalid."));
					}
				}
				else {
					
				}
				classParam = getFindClassParams(mlistOfAllClassesHashed, mlistOfAllClasses, typeNameFull);
				if (classParam==null) {
					classParam = CompilerHelper.loadClass(this, typeNameFull);
				}
			}
			
			// 타입선언시 내부클래스가 있는 경우에 fullname타입 결정과 클래스로드를 한다. 
			//	타입선언시 shortname만 있는 경우는 getFullNameType, getFullNameType2에서 모두 찾는다.
			//	예를들어 java.lang패키지에 있는 클래스 Character.Subset s;에서 Character는 여기서 로드된다. 
			// 또한 같은 패키지내 클래스와 import java.util.*;와 같이 *를 섞어서 import하는 경우에 내부클래스가 있는 경우 여기서 해야 한다.
			if (varUse.parent==null && varUse.varDecl==null && varUse.funcDecl==null && varUse.memberDecl==null && 
					CompilerHelper.IsConstant(src.getItem(varUse.index()))==false) {
				if (varUseName.equals("Pair")) {
					int a;
					a=0;
					a++;
				}
				
				typeNameFull = getFullNameType2(src, varUseName, varUse.index());
				
				classParam = CompilerHelper.loadClass(this, typeNameFull);
				
				if (varUseName.equals("Subset")) {
					int a;
					a=0;
					a++;
				}
				varUse.memberDecl = classParam;
				// s = new String[10]; 에서 String은 여기에서 타입이 정해지기때문에 다시 처리
				// i = new int[10]; 에서 int는 클래스가 없기 때문에 다시 처리하지 않는다.
				if (varUse.isArrayElement) {
					if (IsDefaultType(src.getItem(varUse.index()))==false && classParam!=null) {
						if (varUse.child!=null) {
							i--;
							continue;
						}
					}
				}
			}//if (varUse.parent==null && varUse.varDecl==null && varUse.funcDecl==null && varUse.memberDecl==null) {
			
			// 타입캐스트인데 (타입캐스트)(수식)이 아닌 경우, 
			// ((java.lang.Object)buffer[i]).equals("("), (int)a
			// (long)buffer.length
			if (i==endIndexToTypeCast && (typeCast!=null && typeCast.affectsExpression==false)) {
				// RectForPage p = (RectForPage)c; 여기서 c는 Control인데 varUse는 c 일때이다.
				if (src.getItem(varUse.index()).equals("0.3f")) {
					int a;
					a=0;
					a++;
				}
				
				if (typeCast.name!=null) {
					if (typeNameFull==null) {
						ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub typeFull = getTypeOfVarUseOrFuncCallOfFullName(src, varUse.index());
						typeNameFull = typeFull.typeFullName.str;
					}
					if (TypeCast.isCompatibleType(this, new CodeStringEx(typeCast.name), new CodeStringEx(typeNameFull), 0, null)==false) {
						errors.add(new Error(this, typeCast.startIndex(), typeCast.startIndex(), 
								"invalid type cast. : " + typeCast.name + "-" + typeNameFull));
					}
					else {
						typeNameFull = typeCast.name;
						classParam = getFindClassParams(mlistOfAllClassesHashed, mlistOfAllClasses, typeNameFull);
					}
				}
				// 타입캐스트를 해결하였으므로 초기화
				typeCast = null;
			}
			
			
			if (typeNameFull!=null && classParam==null) {
				classParam = CompilerHelper.loadClass(this, typeNameFull);
			}
			
			
			if (classParam==null) continue;
			
			// varUse의 로드된 클래스 멤버중에서 child의 변수타입이나 함수타입을 결정한다.
			// com.gsoft.common.ViewEx.moveFilesToSDCard()와 같은 경우는 이 함수의 위 부분에서 처리하고
			// ViewEx.moveFilesToSDCard()와 같은 경우는 여기에서 처리한다.
			if (varUse.child!=null) {
				if (varUse.child.memberDecl!=null || varUse.child.varDecl!=null || 
					varUse.child.funcDecl!=null) continue;
				
				if (varUse.child.originName.equals("getLocation")) {
					int a;
					a=0;
					a++;
				}
				
				if (varUse.child.isForVarOrForFunc) {
					// 타입선언시 내부클래스가 있는 경우에 fullname타입 결정과 클래스로드를 한다. 
					//	타입선언시 shortname만 있는 경우는 getFullNameType, getFullNameType2에서 모두 찾는다.
					//	예를들어 java.lang패키지에 있는 클래스 Character.Subset s;에서 Subset는 여기서 로드된다. 
					// 또한 같은 패키지내 클래스와 import java.util.*;와 같이 *를 섞어서 import하는 경우에 내부클래스가 있는 경우 여기서 해야 한다.
					boolean isInnerClassIn = 
							CompilerHelper.hasInnerClass(classParam.name, varUse.child.originName, true);
					if (isInnerClassIn) {
						if (varUseName.equals("Character")) {
							int a;
							a=0;
							a++;
						}
						if (varUse.memberDecl==null) continue;
						typeNameFull = classParam.name + "." + varUse.child.originName;
						varUse.child.memberDecl = CompilerHelper.loadClass(this, typeNameFull);
						/*if (varUse.child.child==null) {
							confirmTypeName(src, varUse.child);
						}*/
					}
					else {
						varUse.child.varDecl = getMemberVar(src, classParam, varUse.child);
					}
				}
				else {
					// 내부클래스의 생성자 호출 이외의 경우
					varUse.child.funcDecl = getFunction(src, classParam, varUse.child, i+1);
					if (varUse.child.funcDecl!=null) continue;
					
					// 내부클래스의 생성자 호출인지 확인한다.
					// pair = new UndoBuffer.Pair();에서 varUse.child는 Pair이다.
					if (CompilerHelper.IsConstant(src.getItem(varUse.child.index()))) continue;
					typeName = varUse.child.originName;
					typeNameFull = classParam.name + "." + typeName;
					
					
					// CustomView 에서 owner는 타입이 CustomView 일때
					// Point p = owner.getLocation(); 에서
					// varUse는 owner이고 varUse의 child는 getLocation()이다.
					// getLocation()는 java.awt.Component의 멤버함수인데
					// java.awt.Component를 읽을수 없으므로 위 getFunction()에서
					// getLocation()을 찾을 수 없다. 
					// 그래서 여기서 필요없는 loadClass()를 하지 않도록 
					// 내부클래스의 생성자 호출인지 확인한다.
					if (varUse.memberDecl==null) continue;
					
					// pair = new UndoBuffer.Pair();에서 varUse.child는 Pair이다.
					// Pair클래스를 찾는다.
					boolean isInnerClassIn = 
							CompilerHelper.hasInnerClass(classParam.name, varUse.child.originName, true);
					if (isInnerClassIn==false) continue;
					
					classParam = CompilerHelper.loadClass(this, typeNameFull);
					/*if (i==endIndexToTypeCast) { // (B) (new A())
						typeNameFull = typeCast.name;
						//varUse.typeCastedByVarUse = typeCast.varUseTypeCasting;
						classParam = CompilerHelper.loadClass(this, typeNameFull);
					}*/
					if (classParam==null) continue;
					
					// pair = new UndoBuffer.Pair();에서 varUse.child는 Pair이다.
					// Pair클래스의 생성자 Pair()을 찾는다.
					varUse.child.funcDecl = getFunction(src, classParam, varUse.child, i+1);
					/*if (varUse.child.child!=null && varUse.child.funcDecl!=null) {  
						// new A().b에서 b를 찾기위해 다시 처리
						i--;
						continue;
					}*/
					
					int a;
					a=0;
					a++;
				}
				
			}
			
			//if (found) continue;
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
		}//for (i=0; i<listOfAllVarUses.count; i++) {
	}
	
	/** 다른 소스파일에서 인터페이스의 풀네임타입을 얻을때 호출한다.
	 *  Character.Subset에서 Character는 java.lang.Character이므로 여기에 Subset을 연결하여
	 *  java.lang.Character.Subset을 리턴한다. static이 아닌 getTypeOfImportLibrary() 등을 사용하므로
	 *  compiler 파라미터를 주의한다.
	 * @param name : Character.Subset과 같은 이름, 아니면 com.gsoft.common.Compiler와 같은 이름.
	 * @return
	 */
	String getTypeName_full(Compiler compiler, int typeStartIndex, int typeEndIndex) {
		
		return compiler.getFullNameType(compiler, typeStartIndex, typeEndIndex);
	}
	
	/** 클래스캐시를 확인하고 없으면 클래스 파일리스트를 검색해서 정확한 fullname인지를 확인한다. 
	 * 소스 파일 리스트에서도 확인한다. 파일리스트에 없으면 실제로 로드를 해본다(로드를 안해볼수도 있으므로 주의한다).*/
	boolean checkTypeNameInFileList(Compiler compiler, String fullname) {
		if (fullname==null) return false;
		
		if (fullname.equals("Compiler_types.Language")) {
			int a;
			a=0;
			a++;
		}
		
		// 배열과 템플릿을 제외한 풀 이름
		fullname = CompilerHelper.getArrayElementType(fullname);
		fullname = CompilerHelper.getTemplateOriginalType(fullname);
		
		if (fullname==null) return false;
		
		if (IsDefaultType(fullname)) return true;
		
		// 클래스 캐시를 확인한다.
		FindClassParams classParamsCashed = CompilerHelper.getFindClassParams(Compiler.mlistOfAllClassesHashed, fullname);
		if (classParamsCashed!=null) return true;
		
		String slashedFullname = fullname.replace('.', File.separator.charAt(0));
		
		String classPath = Control.pathAndroid + File.separator + slashedFullname;
		
		classPath += ".class";
		String fixedClassPath = CompilerHelper.fixClassPath(classPath);
		if (fixedClassPath!=null) {
			return true;
		}
				
		
		// 소스파일에서 로드를 시도한다.
		FindClassParams classParams = CompilerHelper.loadClassFromSrc_onlyInterface(fullname);
		if (classParams!=null) {
			return true;
		}
		return false;
		
	}
	
	
	
	
	
	/** varUse의 listOfArrayElementParams을 찾는다. 즉 varUse가 배열원소일 경우 배열원소 첨자들을 구한다.
	 * 
	 * @param src
	 * @param varUse : new SizeF[5+1]에서 SizeF, buffer[i].equals("a")에서 buffer 등
	 * @param i : varUse의 listOfAllVarUses에서의 인덱스
	 * @param listOfAllVarUses : 파일안에서 모든 varUse들의 리스트, mlistOfAllVarUses
	 */
	void findArraySubscription(HighArray_CodeString src, FindVarUseParams varUse, 
			int i, HighArray<FindVarUseParams> listOfAllVarUses) {
		// 배열 원소일 경우,
		// 배열원소 첨자들을 구하고 첨자 각각의 타입을 결정한다. 또한 첨자 내 수식을 postfix로 바꾸기도 한다.
		if (src.getItem(varUse.index()).equals("buffer")) {
			int a;
			a=0;
			a++;
		}
		varUse.listOfArrayElementParams = findArrayElementParams(src, varUse, i, listOfAllVarUses);
		int k;
		for (k=0; k<varUse.listOfArrayElementParams.count; k++) {
			FindFuncCallParam params = (FindFuncCallParam) varUse.listOfArrayElementParams.getItem(k);
			if (params.typeFullName==null) {
				CodeStringEx typeNameArrParam = getTypeOfExpression(src, params);
				params.typeFullName = typeNameArrParam;
			}
			if (params.typeFullName!=null && params.typeFullName.str!=null) {
				params.classParams = getFindClassParams(mlistOfAllClassesHashed, Compiler.mlistOfAllClasses, params.typeFullName.str);
			}
			if (params.classParams==null) {
				int a;
				a=0;
				a++;
			}
		}
		
	}
	
		
	/** varUse.isArrayElement의 값은 반드시 true이다, 즉 buffer[i].equals()와 같이 배열원소이다.
	 * @param i : varUse의 listOfAllVarUses에서의 인덱스
	 * @return : 배열 원소의 타입*/
	String getFullTypeNameOfArrayElement(HighArray_CodeString src, FindVarUseParams varUse, 
			int i, HighArray<FindVarUseParams> listOfAllVarUses, boolean isOuterLibrary) {
		String typeName, typeNameFull = null;
		// 배열 원소일 경우,
		// 배열원소 첨자들을 구하고 첨자 각각의 타입을 결정한다. 또한 첨자 내 수식을 postfix로 바꾸기도 한다.
		varUse.listOfArrayElementParams = findArrayElementParams(src, varUse, i, listOfAllVarUses);
		int k;
		for (k=0; k<varUse.listOfArrayElementParams.count; k++) {
			FindFuncCallParam params = (FindFuncCallParam) varUse.listOfArrayElementParams.getItem(k);
			if (params.typeFullName==null) {
				CodeStringEx typeNameArrParam = getTypeOfExpression(src, params);
				params.typeFullName = typeNameArrParam;
			}
			params.classParams = getFindClassParams(mlistOfAllClassesHashed, Compiler.mlistOfAllClasses, params.typeFullName.str);
			if (params.classParams==null) {
				int a;
				a=0;
				a++;
			}
		}
		
		//배열의 차원을 확인해야 한다. 여기에선 배열 원소의 타입을 구한다.
		// buffer[i].equals()에서 buffer[i]의 타입은 CodeString이다.
		if (isOuterLibrary==false) {
			typeName =
				varUse.varDecl.getType(src, varUse.varDecl.typeStartIndex(), varUse.varDecl.typeEndIndex());
		}
		else {
			typeName = varUse.varDecl.typeName;
		}
		int dimensionOfType = CompilerHelper.getArrayDimension(this, typeName);
		int dimensionOfElement = CompilerHelper.getArrayDimension(this, varUse.name);
		
		if (dimensionOfType==dimensionOfElement) {
			typeName = CompilerHelper.getArrayElementType(typeName);
			int indexOfTemplateLeftPair = typeName.indexOf('<');
			if (indexOfTemplateLeftPair!=-1) {
				typeName = typeName.substring(0, indexOfTemplateLeftPair);
			}
			if (typeName!=null) {
				typeNameFull = getFullNameType2(src, typeName, varUse.varDecl.typeStartIndex());
				//classParam = getFindClassParams(src, packageName, mlistOfAllClasses, typeNameFull);
			}
		}
		
		return typeNameFull;
	}
	
		
	/** 네임스페이스의 꼭대기 다음에 나오는 멤버의 타입을 확인한다.
	 * 네임스페이스를 사용하여 멤버가 사용되는 경우를 체크한다.
	 * import하지 않고 com.gsoft.common.FileHelper.getFileName()호출을 하는 경우와 같이
		이런 경우는 import문에서 클래스가 로드되는 것이 아니라 호출시 로드가 된다.
	 * @param listOfAllVarUses : 파일 단위 FindVarUseParams*/
	void findMemberUsesUsingNamespace(HighArray_CodeString src, HighArray<FindVarUseParams> listOfAllVarUses) {		
		try {
		
		//findMemberUsesUsingNamespace_library(src, listOfAllVarUses);
		
		findMemberUsesUsingNamespace_sub(src, listOfAllVarUses, 0, listOfAllVarUses.getCount()-1);
		
		
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
	}
	
	/** 해당 클래스내에(상속된 멤버변수포함) varUse라는 내부클래스가 있으면 리턴한다. */
	FindClassParams getMemberClass(HighArray_CodeString src, FindClassParams classParam, FindVarUseParams varUse) {
		if (varUse==null) return null;
		
		return null;
	}
	
	/** 해당 클래스내에(상속된 멤버변수포함) varUse라는 멤버변수가 있으면 리턴한다. */
	FindVarParams getMemberVar(HighArray_CodeString src, FindClassParams classParam, FindVarUseParams varUse) {
		if (varUse==null) return null;
		if (classParam==null) {
			int a;
			a=0;
			a++;
			return null;
		}
		int loopCount = 0;
		ArrayListIReset listOfAllVarDeclarations=null;
		int j;
		
		String childVarUseName = src.getItem(varUse.index()).toString();
		if (varUse.index()==5107) {
			int a;
			a=0;
			a++;
		}
		
		
		for (; loopCount<2; loopCount++) {
			if (loopCount==0) listOfAllVarDeclarations = classParam.listOfVariableParams;
			else listOfAllVarDeclarations = classParam.listOfVarParamsInherited;
			if (listOfAllVarDeclarations==null) continue;
			
			
			for (j=0; j<listOfAllVarDeclarations.count; j++) {
				FindVarParams varParams = (FindVarParams)listOfAllVarDeclarations.getItem(j);
				if (varParams.parent instanceof FindFunctionParams) continue;	// 지역변수
				
				String varName;
				
				//if (varParams.isThis || varParams.isSuper) continue;
				varName = varParams.fieldName;
				try {
				if (varName.equals(childVarUseName)) {
					// var = 0;
					// this.var=0; super.var=0;
					FindVarUseParams parentOfVarUse = varUse.parent; 
					if (parentOfVarUse==null || 
							(parentOfVarUse.name.equals("this") || parentOfVarUse.name.equals("super")) ) {
						// 동일 클래스내 멤버변수 참조
						this.findAllMemberVarUsing_sub_sub(varUse, varParams);
						if (varUse.varDecl!=null) return varParams;
						continue;
					}
					return varParams;
				} // if
				}catch(Exception e) {
					int a;
					a=0;
					a++;
				}
			} // for (j=0; j<listOfAllVarDeclarations.count; j++) {
		} // for (; loopCount<2; loopCount++) {
		return null;	
	}
	
	
	/**func1(func2()); 에서 func1, func2 모두 멤버함수이고 func1이 func2로 앞서는 경우를 해결할수있다.*/
	FindFunctionParams getMemberFunction(HighArray_CodeString src, FindVarUseParams varUse) {
		FindClassParams classToDefineThisVarUse = varUse.classToDefineThisVarUse;
		
		
		/*while (classToDefineThisVarUse!=null) {
			FindFunctionParams func = 
					getFunction(src, classToDefineThisVarUse, varUse, varUse.index());
			if (func!=null) return func;
			classToDefineThisVarUse = (FindClassParams) classToDefineThisVarUse.parent;
		}*/
		FindFunctionParams func = 
				getFunction(src, classToDefineThisVarUse, varUse, varUse.index());
		if (func!=null) return func;
		return null;		
	}
	
	
	/** 해당 클래스내에(상속된 멤버함수포함) varUse라는 멤버함수가 있으면 리턴한다. 
	 * varUse가 함수호출일 경우 파라미터가 네임스페이스가 있는 함수일 수도 있으므로 
	 * 파라미터부터 타입 검사(다시 파라미터가 함수호출일 수도 있으므로 recursive call)를 한 후에 
	 * 함수호출의 선언을 결정한다. 
	 * @param indexInmListOfAllVarUses : varUse의 mListOfAllVarUses에서의 인덱스*/
	FindFunctionParams getFunction(HighArray_CodeString src, FindClassParams classParam, 
			FindVarUseParams varUse, int indexInmListOfAllVarUses) {
		if (varUse==null) return null;
		
		int loopCount=0;
		ArrayListIReset listOfFuncDeclarations;
		int index, indexOfNext = -1;
		int indexOfStartOfSmallBlock, indexOfEndOfSmallBlock;
		ArrayListIReset listOfFuncCallParams=null;
		int i, j;
		
		String varUseName = varUse.name;
		if (varUseName.equals("null")) {
			int a;
			a=0;
			a++;
		}
		
		if (varUse.index()==83452) {
			int a;
			a=0;
			a++;
		}
		
		
		if (varUse.isFake==false) {
			index = varUse.index();
			indexOfNext = SkipBlank(src, false, index+1, src.count-1);
			if (indexOfNext!=src.count) {
				if (varUse.template==null) {
					if (src.getItem(indexOfNext).equals("(")==false) 
						return null;
				}
				else {
					indexOfNext = SkipBlank(src, false, varUse.template.indexRightPair()+1, src.count-1);
				}
			}
			
			// 함수호출의 VarUse를 모아놓은 리스트에 넣는다. 수식에 들어가는 함수호출을 찾기 쉽게 하기 위함이다.
			//mlistOfVarUsesOfFunctionCalls.add(varUse);		
			
			indexOfStartOfSmallBlock = indexOfNext;				
			indexOfEndOfSmallBlock = CompilerHelper.CheckParenthesis(src, "(", ")", indexOfStartOfSmallBlock, src.count-1, false);
			if (indexOfEndOfSmallBlock==-1) {
				errors.add(new Error(this, indexOfStartOfSmallBlock, indexOfStartOfSmallBlock, "not pair"));
			}
			else {
				if (varUse.listOfFuncCallParams==null) {
					listOfFuncCallParams = findFuncCallParams(src, 
							indexOfStartOfSmallBlock, indexOfEndOfSmallBlock, varUse);
					// 함수호출 파라미터의 타입을 listOfFuncCallParams의 각각에 넣어줘야 한다.				
					boolean success = setParamsTypeOfListOfFuncCallParams(src, listOfFuncCallParams);
					if (success) {
						varUse.listOfFuncCallParams = listOfFuncCallParams;
					}
				}
				else {
					listOfFuncCallParams = varUse.listOfFuncCallParams;
				}
			
			}
		}//if (varUse.isFake==false) {
		else { // 가짜 super();
			listOfFuncCallParams = varUse.listOfFuncCallParams;
		}
		
		if (varUseName.equals("super")) {
			// super(xxx);
			varUseName = this.getShortName(classParam.classNameToExtend);
		}
		
		int lenOfWayOfCheck = 1;
		
		loopCount=0;
		
		ArrayList listResult = new ArrayList(5);
		
		if (varUse.name.equals("super")) {
			loopCount=1;
		}
		if (varUse.parent!=null && varUse.parent.name.equals("super")) {
			// super.func()에서 varUse는 func이다.
			// 부모클래스에서 먼저 찾아야 하므로 현재 클래스의 부모클래스의 메서드부터 찾도록 정의한다.
			loopCount=1;
		}	
		
		// 첫번째는 자신이 정의한 함수부터
		// 두번째는 상속한 함수를 대상으로 한다.
		//String varUseName = varUse.originName;
		for (; loopCount<2; loopCount++) {
			if (loopCount==0) listOfFuncDeclarations = classParam.listOfFunctionParams;
			else listOfFuncDeclarations = classParam.listOfFunctionParamsInherited;
			if (listOfFuncDeclarations==null) continue;
			
			
			for (j=0; j<listOfFuncDeclarations.count; j++) {
				FindFunctionParams funcParams = (FindFunctionParams)listOfFuncDeclarations.getItem(j);
				
				String funcName = funcParams.name;
				
				// 함수이름부터 일치하는지 검사한다.
				if (funcName.equals(varUseName)==false) continue;
				
				// 파라미터 개수가 일치하지 않으면 타입검사를 안함
				//if (funcParams.listOfFuncArgs.count!=listOfFuncCallParams.count) continue;
				
				if (varUse.index()==3180) {
					int a;
					a=0;
					a++;
				}
				if (varUse.isFake && varUse.name.equals("super")) {
					int a;
					a=0;
					a++;
				}
				
				if (funcParams.equals(src, varUseName, listOfFuncCallParams, varUse)) { 
					// 매개변수 검사를 하였음.
					if (varUse.index()==538) {
						int a;
						a=0;
						a++;
					}
					String classNameDefiningFunc = ((FindClassParams)funcParams.parent).name;
					String classNameDefiningVarUse = varUse.classToDefineThisVarUse.name;
					
					
					FindVarUseParams parentOfVarUse = varUse.parent;
					if (varUse.name.equals("super")) {
						// 자식클래스의 생성자에서 부모클래스의 생성자 호출, super(xxx);
						this.findMemberFunctionCall_sub_sub(varUse, funcParams);
						if (varUse.funcDecl!=null) return funcParams;
						continue;
					}
					else {
						// XXX = new File(); 여기에서 varUse는 File, 
						// 이것은 동일클래스나 부모클래스의 멤버함수 호출이 될 수 없다.
						// method();
						// this.method(); super.method();
						if (funcParams.isConstructor==false && 
								(parentOfVarUse==null || 
								(parentOfVarUse.name.equals("this") || parentOfVarUse.name.equals("super"))) ) {							
							// 동일 클래스내 멤버함수나 부모클래스의 멤버함수 호출
							this.findMemberFunctionCall_sub_sub(varUse, funcParams);
							if (varUse.funcDecl!=null) return funcParams;
							continue;
							//return null;
						}
					}
					
					if (varUse.name.equals("super")) {
						
						errors.add(new Error(this, varUse.index(), varUse.index(), "invalid super"));
					}
					else {
						//return funcParams;
						listResult.add(funcParams);
					}						
				} // if
			} // for (j=0; j<listOfFuncDeclarations.count; j++) {
			
		} // for (; loopCount<2; loopCount++) {
		
		if (listResult.count==0) {
			return null;
		}
		else if (listResult.count==1) {
			return (FindFunctionParams) listResult.getItem(0);
		}
		else { // 2개 이상이 매핑될 경우
			if (varUse.index()==709) {
				int a;
				a=0;
				a++;
			}
			if (this.hasConstant(varUse)) {
				FindFunctionParams oldFunc = (FindFunctionParams) listResult.getItem(0);
				String parentClassNameOld = ((FindClassParams)oldFunc.parent).name;
				for (i=1; i<listResult.count; i++) {
					FindFunctionParams func = (FindFunctionParams) listResult.getItem(i);
					String parentClassName = ((FindClassParams)func.parent).name;
					if (parentClassNameOld.equals(parentClassName)==false)
						return oldFunc; // 함수 오버라이딩
					else { // 함수 오버로딩
						break;
					}
				}
				
				for (i=0; i<varUse.listOfFuncCallParams.count; i++) {
					FindFuncCallParam funcCall = (FindFuncCallParam) varUse.listOfFuncCallParams.getItem(i);					
					int numberType = CompilerHelper.IsNumber2(getFullName(src, funcCall.startIndex(), funcCall.endIndex()));
					if (numberType!=0) {
						listResult = getFilteredFunctionList(listResult, i, numberType);
						if (listResult==null) return null;
					}
					else {
						// 숫자상수가 아닌 경우
					}
				}
				
				if (listResult.count==0) {
					return null;
				}
				else if (listResult.count==1) {
					return (FindFunctionParams) listResult.getItem(0);
				}
				else {
					
				}
					
			}//함수 호출에 숫자상수를 포함할 경우
			else {
				// 정확한 타입이름들을 갖는 함수를 리턴한다.				
				for (i=0; i<listResult.count; i++) {
					FindFunctionParams func = (FindFunctionParams) listResult.getItem(i);
					if (func.listOfFuncArgs.count!=varUse.listOfFuncCallParams.count)
						continue;
					boolean hasSameTypeName = true;					
					for (j=0; j<func.listOfFuncArgs.count; j++) {
						FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(j);
						FindFuncCallParam funcCall = (FindFuncCallParam) varUse.listOfFuncCallParams.getItem(j);
						
						if (funcCall.typeFullName.equals(var.typeName)==false) {
							hasSameTypeName = false;
							break;
						}
					}
					if (hasSameTypeName) return func;
				}
				
				FindFunctionParams oldFunc = (FindFunctionParams) listResult.getItem(0);
				String parentClassNameOld = ((FindClassParams)oldFunc.parent).name;
				for (i=1; i<listResult.count; i++) {
					FindFunctionParams func = (FindFunctionParams) listResult.getItem(i);
					String parentClassName = ((FindClassParams)func.parent).name;
					if (parentClassNameOld.equals(parentClassName)==false)
						return oldFunc; // 함수 오버라이딩
				}
			}//함수 호출에 상수를 포함하지 않을 경우
			
			
		}// 2개 이상이 매핑될 경우
		
		
		return null;
	}
	
	/** getFunction()에서 srcFuncList가 여러개의 함수들을 갖고 있고, 
	 * 즉 함수호출이 여러 개의 함수에 매핑이 되고
	 *  함수호출파라미터들 중에 숫자상수를 포함할 경우 getFunction()에서 호출한다.
	 * @param srcFuncList
	 * @param indexArgs : 함수호출 파라미터들 중에 몇 번째 파라미터인가
	 * @param numberType : CompilerHelper.IsNumber2()의 리턴값, 숫자상수
	 * @return
	 */
	ArrayList getFilteredFunctionList(ArrayList srcFuncList, int indexArgs, int numberType) {
		ArrayList r = new ArrayList(srcFuncList.count); 
		switch (numberType) {
		case 7: 
		case 1:
		case 2:
		case 3:
		case 4: 
		{
			int i;
			for (i=0; i<srcFuncList.count; i++) {
				FindFunctionParams func = (FindFunctionParams) srcFuncList.getItem(i);
				FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(indexArgs);
				String typeName = var.typeName;
				if ((typeName.equals("float") || typeName.equals("double"))==false) {
					r.add(func);
				}
			}
			return r;
		}
		case 5:
		case 6:
		{
			int i;
			for (i=0; i<srcFuncList.count; i++) {
				FindFunctionParams func = (FindFunctionParams) srcFuncList.getItem(i);
				FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(indexArgs);
				String typeName = var.typeName;
				if (typeName.equals("float") || typeName.equals("double")) {
					r.add(func);
				}
			}
			return r;
		}
		case 0:
		{
			return null;
		}
			
		}
		return null;
	}
	
	
	boolean hasConstant(FindVarUseParams varUse) {
		int i;
		for (i=0; i<varUse.listOfFuncCallParams.count; i++) {
			FindFuncCallParam funcCall = (FindFuncCallParam) varUse.listOfFuncCallParams.getItem(i);
			CodeString str = this.getFullName(this.mBuffer, funcCall.startIndex(), funcCall.endIndex());
			if (CompilerHelper.IsNumber2(str)!=0)
				return true;
		}
		return false;
	}
	
	/** classParams(클래스)내에 varUse란 이름을 갖는 변수 또는 함수가 있는지 검사한다.*/
	boolean hasVarOrFunc(HighArray_CodeString src, FindClassParams classParams, String varUse, boolean isVarOrFunc) {
		int i;
		if (isVarOrFunc) {
			for (i=0; i<classParams.listOfVariableParams.count; i++) {
				FindVarParams var = (FindVarParams)classParams.listOfVariableParams.getItem(i);
				if (var.isThis==false && var.isSuper==false) { 
					if (src.getItem(var.varNameIndex()).equals(varUse)) return true;
				}
				else {
					if (varUse.equals("this")) return true;
					if (varUse.equals("super")) return true;
					return true;
				}
			}
		}
		else {
			for (i=0; i<classParams.listOfFunctionParams.count; i++) {
				FindFunctionParams func = (FindFunctionParams)classParams.listOfFunctionParams.getItem(i);
				if (src.getItem(func.functionNameIndex()).equals(varUse)) return true;
			}
		}
		return false;
	}
	
		
		
	
	/** 해당 클래스 내에서(findMemberVarUsingOutofClass 대조)
	 *  변수선언(var)(멤버,지역)으로 해당클래스의 변수사용리스트(FindClassParams.hashtableOfAllVarUsesForVarHashedByName)에서 모두 찾는다.
	 *  
	 *  멤버변수선언과 멤버변수사용은 full name을 얻어서 비교한다.
	 *  지역변수선언은 full name이 아니므로 varName이고, 
		지역변수사용은 멤버변수와 겹칠수 있으므로 full name을 얻어서 비교한다.
		
	 * @param classParams : 해당 클래스 (변수를 선언한 클래스), inputVarToSuitableClassOrFunc에서 호출되므로.
	 * @param isLocal : true이면 멤버변수찾기(functionParams==null), false이면 로컬변수찾기(classParams==null)
	 * @param var : 변수선언 */	
	void findAllLocalVarUsing_sub(HighArray_CodeString src, 
			FindClassParams classParams, FindFunctionParams functionParams,   
			FindVarParams var, /*ArrayListIReset listOfAllVarUses,*/ boolean isLocal, String varName) {
		if (classParams!=null && classParams.name.equals("com.gsoft.common.gui.Button") && varName.equals("bounds")) {
			int a;
			a=0;
			a++;
		}
		if (classParams!=null && classParams.name.equals("com.gsoft.common.gui.EditText") && varName.equals("RedoBuffer")) {
			int a;
			a=0;
			a++;
		}
		if (classParams!=null && classParams.name.equals("com.gsoft.common.gui.EditText") && varName.equals("selectP2")) {
			int a;
			a=0;
			a++;
		}
		// 현재클래스는 현재 파일에 정의된 것이고 상속클래스는 현재 파일에 정의되지 않을수도 있으므로
		// 상속클래스의 필드를 찾을때는 src와 var.varNameIndex()이 불일치할 수도 있으므로 
		// var.varNameIndex()을 쓸때는 주의해야 하고 var.fieldName을 사용해야 한다.
		// varUse.index()는 상관이 없다.
		try {
			int i;
			//String varName = src.getItem(var.varNameIndex()).toString();
			
			
			//for (i=0; i<classParams.listOfAllVarUses.count; i++) {
			int len = functionParams.listOfAllVarUsesForVar.getCount();
			for (i=0; i<len; i++) {
				FindVarUseParams varUse = (FindVarUseParams)functionParams.listOfAllVarUsesForVar.getItem(i);
				int index = varUse.index();
				String varUseName = varUse.originName;
				//String varUseName = varUse.name;
				if (varUseName.equals("buffer[i]")) {
					int debug;
					debug=0;
					debug++;
				}
				Block block = (Block) var.parent;
				//if (block.startIndex()<index && index<block.endIndex()) { // 변수의 scope가 맞아야 한다.
				if (var.startIndexOfScope()<=index && index<=var.endIndexOfScope()) { // 변수의 scope가 맞아야 한다.
					// 지역변수선언은 full name이 아니므로 varName이고, 
					// 지역변수사용은 멤버변수와 겹칠수 있으므로 full name을 얻어서 비교한다.
					//String varUseFullName = getFullName(src, classParams, varUse, true);
					if (varName.equals(varUseName)) { // 변수선언과 이름이 같고
					//if (varName.equals(varUseFullName)) { // 변수선언과 이름이 같고
						if (varName.equals("s")) {
							int a;
							a=0;
							a++;
						}
						int dot = SkipBlank(src, true, 0, index-1);
						if (dot>=0 && src.getItem(dot).equals(".")) {
							int parent = SkipBlank(src, true, 0, dot-1);
							if (parent>=0 && src.getItem(parent).equals("this")) {
								int a;
								a=0;
								a++;									
							}
						}
						else { // .이 없어야 한다.
							//if (var.startIndex()OfScope<=varUse.index() && varUse.index()<=var.endIndex()OfScope) {
								// 지역변수의 scope검사
								varUse.isLocal = true;
								varUse.varDecl = var;
								var.listOfVarUses.add(varUse);
							//}
						}
						if (varUseName.equals("widthOfCharsInPage")) {
							int a;
							a=0;
							a++;
						}
						
					}//if (varName.equals(varUseName)) { // 변수선언과 이름이 같고
				}//if (functionParams.startIndex()<index && index<functionParams.endIndex()) {
				else if (index > functionParams.endIndex()) {
					break;
				}
			}//for (i=0; i<classParams.listOfAllVarUses.count; i++) {
		} catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
	}
	
	
	void findAllMemberVarUsing_sub_sub(FindVarUseParams varUse, FindVarParams var) {
		String varUseName = varUse.name;
		if (varUse.parent==null) { // varName 
			boolean isStaticFunc = false;
			FindFunctionParams func = varUse.funcToDefineThisVarUse;
			if (func!=null && func.accessModifier!=null && func.accessModifier.isStatic)
				isStaticFunc = true;
			if (isStaticFunc) { // 변수사용이 사용된 장소가 함수이고 static함수일 경우
				if (var.accessModifier!=null && var.accessModifier.isStatic) {
					// static 함수내에서 static 멤버변수 참조
					varUse.varDecl = var;
					var.listOfVarUses.add(varUse);
				}
				else {
					String varFullName = ((FindClassParams)var.parent).name + "." + var.fieldName;
					// static 함수내에서는 상속클래스나 내포하는 클래스, 자신의 클래스의 static 멤버만 접근 가능하다.
					errors.add(new Error(this, varUse.index(), varUse.index(), varFullName + " is not static."));
				}
			}
			else {// 인스턴스 함수내에서 변수 참조
				if (var.accessModifier!=null && var.accessModifier.isStatic==false) {
					varUse.isUsingThisOrSuper = true;
				}
				varUse.varDecl = var;
				var.listOfVarUses.add(varUse);
			}
		}//if (varUse.parent==null) { // varName 
		else {
			//if (varUse.parent.isThis) { // this.varName
			if (this.mBuffer.getItem(varUse.parent.index()).equals("this")) { // this.varName
				if (varUse.parent.parent!=null) {
					errors.add(new Error(this, varUse.parent.parent.index(),varUse.parent.parent.index(),"invalid this."));
				}
				else {
					if (var.accessModifier!=null && var.accessModifier.isStatic) {
						String parentShortName = CompilerHelper.getShortName(((FindClassParams)var.parent).name);
						errors.add(new Error(this, varUse.index(), varUse.index(), 
							varUseName + " is a static member variable. So use the "+parentShortName));
					}
					else {
						varUse.isUsingThisOrSuper = true;
						varUse.varDecl = var;
						var.listOfVarUses.add(varUse);
					}
				}
			}
			else if (this.mBuffer.getItem(varUse.parent.index()).equals("super")) { // super.varName
				String classNameDefiningVar = ((FindClassParams)var.parent).name;
				String classNameDefiningVarUse = varUse.classToDefineThisVarUse.name;
				
				FindVarUseParams parentOfSuper = varUse.parent.parent;
				if (parentOfSuper!=null) {
					// super 앞에는 다른 토큰이 있어서는 안된다.
					errors.add(new Error(this, parentOfSuper.index(), parentOfSuper.index(), "invalid super."));
				}
				else {
					if (classNameDefiningVar.equals(classNameDefiningVarUse)) {
						// 동일 클래스내 함수호출이지만 super키워드가 있으므로 매핑이 안되고 continue;
						//errors.add(new Error(this, varUse.parent.index(), varUse.parent.index(), "invalid super. Use this."));
					}
					else {
						if (var.accessModifier!=null && var.accessModifier.isStatic) {
							//errors.add(new Error(this, varUse.index(), varUse.index(), varUseName + " is static."));
							String parentShortName = CompilerHelper.getShortName(((FindClassParams)var.parent).name);
							errors.add(new Error(this, varUse.index(), varUse.index(), 
								varUseName + " is a static member variable. So use the "+parentShortName+", not super."));
						}
						else {
							varUse.isUsingThisOrSuper = true;
							varUse.varDecl = var;
							var.listOfVarUses.add(varUse);
						}
					}
				}
			}
			else {	// varUse.parent가 널이 아니고 this가 아닌경우
				/*if (var.isClass==false && var.isThis==false && 
						var.varNameIndex()!=-1 && src.getItem(var.varNameIndex()).equals("a")) {
					int debug;
					debug=0;
					debug++;
				}
				if (var.accessModifier!=null && var.accessModifier.isStatic && varUse.parent==null) { 
					varUse.varDecl = var;
					var.listOfVarUses.add(varUse);	
				}*/
				
			} // else { varUse.parent가 널이 아니고 this가 아닌경우
		}//else if (varUse.parent!=null) {  
	}
	
	
		
	
	void findMemberFunctionCall_sub_sub(FindVarUseParams varUse, FindFunctionParams func) {
		String varUseName = varUse.name;
		if (varUse.index()==10136) {
			int a;
			a=0;
			a++;
		}
		// method()
		if (varUse.parent==null) { 
			boolean isStaticFunc = false;
			FindFunctionParams parentFunc = varUse.funcToDefineThisVarUse;
			if (parentFunc!=null && parentFunc.accessModifier!=null && parentFunc.accessModifier.isStatic)
				isStaticFunc = true;
			if (isStaticFunc) { // 변수사용이 사용된 장소가 함수이고 static함수일 경우
				if (varUse.name.equals("super")) {
					if (parentFunc.isConstructor) {
						// 자식클래스의 static 생성자에서 부모클래스의 static 생성자 호출
						if (func.accessModifier!=null && func.accessModifier.isStatic) {
							varUse.funcDecl = func;
						}
					}
					else {
						errors.add(new Error(this, varUse.index(), varUse.index(), "invalid super()"));
					}
				}//if (varUse.name.equals("super")) {
				else {
					if (func.accessModifier!=null && func.accessModifier.isStatic) {
						// static 함수내에서 다른 static 함수를 호출
						varUse.funcDecl = func;
					}
					else {
						// static 함수내에서는 상속클래스나 내포하는 클래스, 자신의 클래스의 static 멤버만 접근 가능하다.
						if (func.isConstructor==false) {
							// Character클래스내에서 static인 read()함수내에서 Character()생성자를 호출한 경우
							String funcFullName = ((FindClassParams)func.parent).name + "." + func.name;
							errors.add(new Error(this, varUse.index(), varUse.index(), funcFullName + "() is not static."));
						}
					}
				}
			}//static 함수
			else {//일반 인스턴스함수
				if (varUse.name.equals("super")) {
					if (parentFunc.isConstructor) {
						// 자식클래스의 not-static 생성자에서 부모클래스의 not-static 생성자 호출
						if (func.accessModifier!=null && func.accessModifier.isStatic==false) {
							varUse.funcDecl = func;
						}
					}
					else {
						errors.add(new Error(this, varUse.index(), varUse.index(), "invalid super()"));
					}
				}//if (varUse.name.equals("super")) {
				else {
					if (func.accessModifier!=null && func.accessModifier.isStatic==false) {
						if (func.isConstructor==false) {
							varUse.isUsingThisOrSuper = true;
						}
					}
					varUse.funcDecl = func;
				}
			}							
			
		}//if (varUse.parent==null) {
		else { 
			// this.method()
			//if (varUse.parent.isThis) {	// this를 이용한 동일 클래스내 멤버함수 호출
			if (this.mBuffer.getItem(varUse.parent.index()).equals("this")) {	// this를 이용한 동일 클래스내 멤버함수 호출
				FindClassParams classParams = (FindClassParams) func.parent;
				
				FindVarUseParams parentOfThis = varUse.parent.parent;
				if (parentOfThis!=null) {
					// this 앞에는 다른 토큰이 있어서는 안된다.
					errors.add(new Error(this, parentOfThis.index(), parentOfThis.index(), "invalid this."));
				}
				else {
					if (func.accessModifier!=null && func.accessModifier.isStatic) {
						//errors.add(new Error(this, varUse.index(), varUse.index(), varUseName + "() is static."));
						String parentShortName = CompilerHelper.getShortName(((FindClassParams)func.parent).name);
						errors.add(new Error(this, varUse.index(), varUse.index(), 
							varUseName + "() is a static member function. So use the "+parentShortName+", not super."));
					}
					else {
						varUse.isUsingThisOrSuper = true;
						varUse.funcDecl = func;
					}
				}
			}
			// super.method();
			else if (this.mBuffer.getItem(varUse.parent.index()).equals("super")) {	// super를 이용한 상속 클래스내 멤버함수 호출
				FindClassParams classParams = (FindClassParams) func.parent;
				
				String classNameDefiningFunc = classParams.name;
				String classNameDefiningVarUse = varUse.classToDefineThisVarUse.name;
				
				FindVarUseParams parentOfSuper = varUse.parent.parent;
				if (parentOfSuper!=null) {
					// super 앞에는 다른 토큰이 있어서는 안된다.
					errors.add(new Error(this, parentOfSuper.index(), parentOfSuper.index(), "invalid super."));
				}
				else {
					if (classNameDefiningFunc.equals(classNameDefiningVarUse)) {
						// 동일 클래스내 함수호출이지만 super키워드가 있으므로 continue;
						//errors.add(new Error(this, varUse.parent.index, varUse.parent.index, "invalid super. Use this."));
					}
					else {
						if (func.accessModifier!=null && func.accessModifier.isStatic) {
							//errors.add(new Error(this, varUse.index(), varUse.index(), varUseName + "() is static."));
							String parentShortName = CompilerHelper.getShortName(((FindClassParams)func.parent).name);
							errors.add(new Error(this, varUse.index(), varUse.index(), 
								varUseName + "() is a static member function. So use the "+parentShortName+", not super."));
						}
						else {
							varUse.isUsingThisOrSuper = true;
							varUse.funcDecl = func;
						}
					}
				}
			}//else if (src.getItem(varUse.parent.index).equals("super"))
			
			
			
		} //if (varUse.parent!=null)
	}
	
	/** 네임스페이스에서 꼭대기 변수를 찾는다.*/
	void findAllLocalVarUsing_caller_Local(HighArray_CodeString src, 
			HighArray<FindVarParams> listOfAllMemberVarDeclarations, HighArray<FindVarParams> listOfAllLocalVarDeclarations) {
		int i;
		int len = listOfAllLocalVarDeclarations.getCount();
		for (i=0; i<len; i++) {
			FindVarParams var = (FindVarParams) listOfAllLocalVarDeclarations.getItem(i);
			if (var.varNameIndex()!=-1 && src.getItem(var.varNameIndex()).equals("backColor")) {
				int a;
				a=0;
				a++;
			}
			Block block = getParent((Block)var.parent);
			if (block instanceof FindFunctionParams) {
				//var.isMemberOrLocal = false; // 로컬변수
				//findAllVarUsingAndChangeMemberDeclColor(src, null, (FindFunctionParams)block, var, true);
				this.findAllLocalVarUsing_sub(src, null, (FindFunctionParams)block, var, true, var.fieldName);
			}
		}
		
		
	}
	
	
	
	/** fullName에 템플릿에 배열기호가 포함될경우 
	 * shortName은 템플릿, 배열기호를 포함한 마지막 '.', '/', '\\', '$' 이후 이름을 리턴한다.
	 * '.', '/', '\\', '$'  이 없으면 원래 fullName을 리턴한다.
	 * @param fullName
	 * @return
	 */
	String getShortName(String fullName) {
		if (fullName==null) return null;
		int i;
		for (i=fullName.length()-1; i>=0; i--) {
			if (fullName.charAt(i)=='.') break;
			else if (fullName.charAt(i)=='/') break;
			else if (fullName.charAt(i)=='\\') break;
			else if (fullName.charAt(i)=='$') break;
		}
		return fullName.substring(i+1, fullName.length());
	}
	
	/** applyTypeNameToChangeToTemplateClass()의 sub이다.
	 * @param compiler : 현재 ***.java의 compiler
	 * @param classParams : 해당 클래스, 재귀적 호출
	 * @param typeNameToChange : class Stack {}이 템플릿 클래스 T일 경우 typeNameToChange는 T이다.
	 * @param typeNameInTemplatePair : Stack<Block> stack; 에서 typeNameInTemplatePair는 com.gsoft.common.Compiler_types.Block이 된다.
	 */
	public static void applyTypeNameToChangeToTemplateClass_sub(Compiler compiler, FindClassParams classParams,
			String typeNameToChange, String typeNameInTemplatePair) {
		ArrayListIReset listOfMemberVars = classParams.listOfVariableParams;
		ArrayListIReset listOfFuncs = classParams.listOfFunctionParams;
		int i, j, k;		
		
		for (j=0; j<listOfMemberVars.count; j++) {
			FindVarParams var = (FindVarParams) listOfMemberVars.getItem(j);
			if (var.typeName!=null && var.typeName.equals(typeNameToChange)) {
				var.typeName = typeNameInTemplatePair;
			}
		}
		
		for (j=0; j<listOfFuncs.count; j++) {
			FindFunctionParams func = (FindFunctionParams) listOfFuncs.getItem(j);
			if (func.returnType!=null && func.returnType.equals(typeNameToChange)) {
				func.returnType = typeNameInTemplatePair;
			}
			
			ArrayListIReset listOfFuncArgs = func.listOfFuncArgs;
			for (i=0; i<listOfFuncArgs.count; i++) {
				FindVarParams var = (FindVarParams) listOfFuncArgs.getItem(i);
				if (var.isThis || var.isSuper) continue;
				if (var.typeName!=null && var.typeName.equals(typeNameToChange)) {
					var.typeName = typeNameInTemplatePair;
				}
			}
			
			ArrayListIReset listOfLocalVars = func.listOfVariableParams;
			for (i=0; i<listOfLocalVars.count; i++) {
				FindVarParams var = (FindVarParams) listOfLocalVars.getItem(i);
				if (var.isThis || var.isSuper) continue;
				if (var.typeName!=null && var.typeName.equals(typeNameToChange)) {
					var.typeName = typeNameInTemplatePair;
				}
			}
		}//for (i=0; i<listOfFuncs.count; i++) {
	}
	
	/** class Stack {}이 템플릿 클래스 T일 경우 이 클래스를 CompilerHelper.loadClassFromSrc_onlyInterface()으로
	 * 읽어야 한다면(바이트코드를 읽지않고 바이트코드는 PathClassLoader클래스에서 템플릿을 처리한다.) 템플릿 타입을 처리하기 위해
	 * 이 함수를 호출해야 한다. 예를 들어  Stack<Block> stack;에서 Stack<Block>을 만나면 loadClass()가 호출이 되는데
	 * 바이트코드가 error가 날경우 loadClassFromSrc_onlyInterface()에서 호출을 해야 한다.
	 * @param compiler
	 * @param classParams
	 * @param fullNameIncludingTemplate : Stack<Block> stack; 에서 com.gsoft.common.Util.Stack<com.gsoft.common.Compiler_types.Block>이다.
	 * 
	 */
	public static void applyTypeNameToChangeToTemplateClass(Compiler compiler, FindClassParams classParams, 
			String fullNameIncludingTemplate) {
		if (classParams==null) return;
		if (classParams.template==null) return;
		
		
		// class Stack {}이 템플릿 클래스 T일 경우 typeNameToChange는 T이다.
		String typeNameToChange = classParams.template.typeNameToChange;
		
		// Stack<Block> stack; 에서 typeNameInTemplatePair는 com.gsoft.common.Compiler_types.Block이 된다.
		String typeNameInTemplatePair = CompilerHelper.getTemplateTypeInPair(fullNameIncludingTemplate);
		
		
		classParams.name = fullNameIncludingTemplate;
		
		int i;
		// 내부 클래스에 대해 호출
		for (i=0; i<classParams.childClasses.count; i++) {
			FindClassParams child = (FindClassParams) classParams.childClasses.getItem(i);
			applyTypeNameToChangeToTemplateClass_sub(compiler, child, typeNameToChange, typeNameInTemplatePair);
		}
		
		// 현재 클래스에 대해 호출
		applyTypeNameToChangeToTemplateClass_sub(compiler, classParams, typeNameToChange, typeNameInTemplatePair);
		
		
	}//void applyTypeNameToChangeToTemplateClass()
	
	/** 파일에서 정의하는 클래스가 템플릿일 경우 가상의 변수선언을 만들어서 findTemplateVarUses()을
	 * 호출하여 클래스안의 변수선언들, 함수선언들에서 varUse들을 찾아서 가상의 변수선언에 연결한다. 
	 * 예를들어 class Stack {}이 템플릿 T일 경우 클래스안의 T들을 찾는다.
	 * @param src
	 */
	void findAllVarUsingOfTemplate(HighArray_CodeString src) {
		int i;
		for (i=0; i<mlistOfAllDefinedClasses.count; i++) {
			FindClassParams c = (FindClassParams) mlistOfAllDefinedClasses.getItem(i);
			if (c.template!=null) {
				FindVarParams var = new FindVarParams(this, c.template, c);
				findTemplateVarUses(src, c, var);
			}
		}
	}
	
	/** findTemplateVarUses_sub()의 재귀적 호출*/
	void findTemplateVarUses(HighArray_CodeString src, FindClassParams classParams, FindVarParams templateVar) {
		int i;
		ArrayListIReset listOfChildClasses = classParams.childClasses;
		for (i=0; i<listOfChildClasses.count; i++) {
			FindClassParams child = (FindClassParams) listOfChildClasses.getItem(i);
			findTemplateVarUses_sub(src, child, templateVar);
		}
		findTemplateVarUses_sub(src, classParams, templateVar);
	}
	
	/** class Stack {}에서 Stack이 템플릿클래스이면 템플릿 변수를 만들어서
	 *  클래스의 변수선언들, 함수선언들의 타입들에 있는 varUse들의 varDecl을 템플릿 변수에 연결시킨다.
	 * @param src
	 * @param classParams : 해당 클래스
	 * @param templateVar : 템플릿 변수(FindVarParams.templateOfClass가 null이 아니다.)
	 */
	void findTemplateVarUses_sub(HighArray_CodeString src, FindClassParams classParams, FindVarParams templateVar) {
		ArrayListIReset listOfMemberVars = classParams.listOfVariableParams;
		ArrayListIReset listOfFuncs = classParams.listOfFunctionParams;
		int i, j, k;
		int typeStartIndex, typeEndIndex;
		int endIndexInmListOfAllVarUses;
		int startIndexInmListOfAllVarUses;
		
		for (i=0; i<listOfMemberVars.count; i++) {
			FindVarParams var = (FindVarParams) listOfMemberVars.getItem(i);
			if (var.isThis || var.isSuper) continue;
			typeStartIndex = var.typeStartIndex();
			typeEndIndex = var.typeEndIndex();
						
			startIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, classParams.listOfAllVarUsesForVar, 
					0, typeStartIndex, true);
			endIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, classParams.listOfAllVarUsesForVar, 
					startIndexInmListOfAllVarUses, typeEndIndex, false);
			
			for (j=startIndexInmListOfAllVarUses; j<=endIndexInmListOfAllVarUses; j++) {
				FindVarUseParams varUse = (FindVarUseParams) classParams.listOfAllVarUsesForVar.getItem(j);
				if (varUse.originName.equals(templateVar.fieldName)) {
					varUse.varDecl = templateVar;
				}
			}
		}//for (i=0; i<listOfMemberVars.count; i++) {
		
		for (k=0; k<listOfFuncs.count; k++) {
			FindFunctionParams func = (FindFunctionParams) listOfFuncs.getItem(k);
			
			if (func.returnType!=null) {
				typeStartIndex = func.returnTypeStartIndex();
				typeEndIndex = func.returnTypeEndIndex();
				if (typeStartIndex!=-1 && typeEndIndex!=-1) {							
					startIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, this.mlistOfAllVarUses, 
							0, typeStartIndex, true);
					endIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, this.mlistOfAllVarUses, 
							startIndexInmListOfAllVarUses, typeEndIndex, false);
					
					for (j=startIndexInmListOfAllVarUses; j<=endIndexInmListOfAllVarUses; j++) {
						FindVarUseParams varUse = (FindVarUseParams) this.mlistOfAllVarUses.getItem(j);
						if (varUse.originName.equals(templateVar.fieldName)) {
							varUse.varDecl = templateVar;
						}
					}
				}
			}//if (func.returnType!=null) {
			
			ArrayListIReset listOfFuncArgs = func.listOfFuncArgs;
			for (i=0; i<listOfFuncArgs.count; i++) {
				FindVarParams var = (FindVarParams) listOfFuncArgs.getItem(i);
				if (var.isThis || var.isSuper) continue;
				typeStartIndex = var.typeStartIndex();
				typeEndIndex = var.typeEndIndex();
							
				startIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, func.listOfAllVarUsesForVar, 
						0, typeStartIndex, true);
				endIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, func.listOfAllVarUsesForVar, 
						startIndexInmListOfAllVarUses, typeEndIndex, false);
				
				for (j=startIndexInmListOfAllVarUses; j<=endIndexInmListOfAllVarUses; j++) {
					FindVarUseParams varUse = (FindVarUseParams) func.listOfAllVarUsesForVar.getItem(j);
					if (varUse.originName.equals(templateVar.fieldName)) {
						varUse.varDecl = templateVar;
					}
				}
			}//for (i=0; i<listOfFuncArgs.count; i++) {
			
			ArrayListIReset listOfLocalVars = func.listOfVariableParams;
			for (i=0; i<listOfLocalVars.count; i++) {
				FindVarParams var = (FindVarParams) listOfLocalVars.getItem(i);
				if (var.isThis || var.isSuper) continue;
				typeStartIndex = var.typeStartIndex();
				typeEndIndex = var.typeEndIndex();
							
				startIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, func.listOfAllVarUsesForVar, 
						0, typeStartIndex, true);
				endIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, func.listOfAllVarUsesForVar, 
						startIndexInmListOfAllVarUses, typeEndIndex, false);
				
				for (j=startIndexInmListOfAllVarUses; j<=endIndexInmListOfAllVarUses; j++) {
					FindVarUseParams varUse = (FindVarUseParams) func.listOfAllVarUsesForVar.getItem(j);
					if (varUse.originName.equals(templateVar.fieldName)) {
						varUse.varDecl = templateVar;
					}
				}
			}//for (i=0; i<listOfFuncArgs.count; i++) {
		}//for (k=0; k<listOfFuncs.count; k++) {
	}
				

	
	
	
	
	/** synchronized, try, catch, finally 등의 블록을 찾는다.
	 * startIndex : 접근지정자(static, public등) 
	 *  endIndex : block의 끝, 즉 '}' */
	public void FindSpecialBlock(HighArray_CodeString src,  
			FindSpecialBlockParams result, int keywordIndex)
	{
		CodeString str = src.getItem(keywordIndex);
		int i;
		int leftPair, rightPair;
		int blockStart;
		if (str.equals("synchronized")) {			
			leftPair = SkipBlank(src, false, keywordIndex + 1, src.count-1);
			if (src.getItem(leftPair).equals("(")) {
				result.indexOfLeftParenthesis = IndexForHighArray.indexRelative(result, src, leftPair);
				//rightPair = Skip(src, false, ")", leftPair + 1, src.count-1);
				rightPair =	        		
						CompilerHelper.CheckParenthesis(src, "(", ")", result.indexOfLeftParenthesis(), src.count-1, false);
				if (rightPair==-1) {
					errors.add( new Error(this, rightPair,rightPair,"')' not exist.") );
					return;
				}
				if (src.getItem(rightPair).equals(")")) {
					result.indexOfRightParenthesis = IndexForHighArray.indexRelative(result, src, rightPair);
					blockStart = SkipBlank(src, false, rightPair + 1, src.count-1);
					if (src.getItem(blockStart).equals("{")) {
						FindBlockParams blockParams = new FindBlockParams(this, blockStart, -1); 
						result.findBlockParams = blockParams;
						result.findBlockParams.categoryOfBlock = new CategoryOfBlock(CategoryOfBlock.Synchronized,null);
						// 조건문을 연결
						result.findBlockParams.blockName = "";
		            	if (result.indexOfLeftParenthesis()!=-1 && result.indexOfRightParenthesis()!=-1) {
			            	ArrayListChar list = new ArrayListChar(50);
			            	for (i=result.indexOfLeftParenthesis(); i<=result.indexOfRightParenthesis(); i++) {
			            		list.add(src.getItem(i).str);
			            	}
			            	result.findBlockParams.blockName = new String(list.getItems());
		            	}
						mlistOfBlocks.add(blockParams);
						result.startIndex = IndexForHighArray.indexRelative(result, src, keywordIndex);
						result.found = true;
						result.nameIndex = IndexForHighArray.indexRelative(result, src, keywordIndex);
						result.specialBlockType = FindSpecialBlockParams.SpecialBlockType_synchronized;
						result.isBlock = true;
						return;
					}
				}
			}
		}
		else if (str.equals("catch")) {
			leftPair = SkipBlank(src, false, keywordIndex + 1, src.count-1);
			if (src.getItem(leftPair).equals("(")) {
				result.indexOfLeftParenthesis = IndexForHighArray.indexRelative(result, src, leftPair);
				//rightPair = Skip(src, false, ")", leftPair + 1, src.count-1);
				rightPair =	        		
						CompilerHelper.CheckParenthesis(src, "(", ")", result.indexOfLeftParenthesis(), src.count-1, false);
				if (rightPair==-1) {
					errors.add( new Error(this, rightPair,rightPair,"')' not exist.") );
					return;
				}
				if (src.getItem(rightPair).equals(")")) {
					result.indexOfRightParenthesis = IndexForHighArray.indexRelative(result, src, rightPair);
					blockStart = SkipBlank(src, false, rightPair + 1, src.count-1);
					if (src.getItem(blockStart).equals("{")) {
						FindBlockParams blockParams = new FindBlockParams(this, blockStart, -1); 
						result.findBlockParams = blockParams;
						result.findBlockParams.categoryOfBlock = new CategoryOfBlock(CategoryOfBlock.Catch,null);
						
						// 조건문을 연결
						result.findBlockParams.blockName = "";
		            	if (result.indexOfLeftParenthesis()!=-1 && result.indexOfRightParenthesis()!=-1) {
			            	ArrayListChar list = new ArrayListChar(50);
			            	for (i=result.indexOfLeftParenthesis(); i<=result.indexOfRightParenthesis(); i++) {
			            		list.add(src.getItem(i).str);
			            	}
			            	result.findBlockParams.blockName = new String(list.getItems());
		            	}
		            	
						mlistOfBlocks.add(blockParams);
						result.found = true;
						result.startIndex = IndexForHighArray.indexRelative(result, src, keywordIndex);
						result.nameIndex = IndexForHighArray.indexRelative(result, src, keywordIndex);
						result.specialBlockType = FindSpecialBlockParams.SpecialBlockType_catch;
						result.isBlock = true;
						return;
					}
				}
			}
		}
		else if (str.equals("try") || str.equals("finally")) {
			blockStart = SkipBlank(src, false, keywordIndex + 1, src.count-1);
			if (src.getItem(blockStart).equals("{")) {
				FindBlockParams blockParams = new FindBlockParams(this, blockStart, -1); 
				result.findBlockParams = blockParams;
				mlistOfBlocks.add(blockParams);
				result.found = true;
				result.startIndex = IndexForHighArray.indexRelative(result, src, keywordIndex);
				result.nameIndex = IndexForHighArray.indexRelative(result, src, keywordIndex);
				if (str.equals("try")) {
					result.specialBlockType = FindSpecialBlockParams.SpecialBlockType_try;
					result.findBlockParams.categoryOfBlock = new CategoryOfBlock(CategoryOfBlock.Try,null);
					result.findBlockParams.blockName = "";
				}
				else if (str.equals("finally")) {
					result.specialBlockType = FindSpecialBlockParams.SpecialBlockType_finally;
					result.findBlockParams.categoryOfBlock = new CategoryOfBlock(CategoryOfBlock.Finally,null);
					result.findBlockParams.blockName = "";
				}
				result.isBlock = true;
				return;
			}
			else {
				errors.add(new Error(this, blockStart, blockStart, "'{' required."));
			}
		}
		
	}
	
	
	
	/** startIndex : 접근지정자(static, public등) 
	 *  endIndex : block의 끝, 즉 '}' */
	public void FindEnum(HighArray_CodeString src,  
			FindClassParams findEnumParams, int startIndex, int endIndex)
	{
        int i;
        for (i = startIndex; i <= endIndex; )
        {        	
        	findEnumParams.classIndex = IndexForHighArray.indexRelative(findEnumParams, src, Find(src, "enum", i, endIndex));

            if (findEnumParams.classIndex() == -1) {
				errors.add(new Error(this, endIndex, endIndex, "enum keyword not exists"));
            	return;
            }

            // 공백 스킵                
            i = SkipBlank(src, false, findEnumParams.classIndex() + 1, endIndex);
            if (i==endIndex+1) break;
            
            CodeString str = src.getItem(i);
            
            
            // 클래스 이름 찾기
            if (IsIdentifier(str)  && !CompilerHelper.IsComment(str))   // 식별자 이면
            {            	
                findEnumParams.classNameIndex = IndexForHighArray.indexRelative(findEnumParams, src, i);

                // 공백 스킵
                i = SkipBlank(src, false, i + 1, endIndex);
                if (i==endIndex+1) break;

                               
                int indexOfNextOfEnumName = i;
                CodeString nextOfEnumName = src.getItem(indexOfNextOfEnumName);
                
                if (str.equals("HScrollBar")) {
                	int a;
                	a=0;
                	a++;
                }
                
               
             
                int startParenthesis = -1;
                int endParenthesis = -1;
                FindBlockParams findBlockParams = new FindBlockParams(this, startParenthesis, endParenthesis);
                                
                
                if (src.getItem(indexOfNextOfEnumName).equals("{")==false) {
                	errors.add(new Error(this, endIndex, endIndex, "invalid enum."));
                }
                else {
                	startParenthesis = indexOfNextOfEnumName;
                }
                findBlockParams.startIndex = IndexForHighArray.indexRelative(findBlockParams, src, startParenthesis);
                
                //AccessModifier r = FindAccessModifier(src, 0, findEnumParams.classIndex() - 1, 
                //		findEnumParams.accessModifier);
                ReturnOfFindAccessModifier r = FindAccessModifier(src, 0, findEnumParams.classIndex() - 1, findEnumParams.accessModifier);
                int k = r.r;
                
                int docuIndex;
                if (findEnumParams.accessModifier.found) docuIndex = k-1;
            	else {
            		docuIndex = findEnumParams.classIndex()-1;
            	}
            	//int indexOfDocuEnd = SkipOnlyBlank(src, true, 0, docuIndex); // 공백 스킵 
            	int indexOfDocuEnd = SkipOnlyBlankAndAnnotationAndRegularComment(src, true, 0, docuIndex); // 공백 스킵
            	DocuComment docu = 
            			FindDocuComment(src, /*true, findFunctionParams, null,*/ 0, indexOfDocuEnd);
            	findEnumParams.docuComment = docu;
            	
            	
            	findEnumParams.startIndex = IndexForHighArray.indexRelative(findEnumParams, src, k);
            	findEnumParams.startIndex = IndexForHighArray.indexRelative(findEnumParams, src, 
            			getIndexWhickRemoveLeftBlankAndCommentAndAnnotation(src, r));
                //findEnumParams.endIndex = findBlockParams.endIndex();
                findEnumParams.found = true;
                findEnumParams.findBlockParams = findBlockParams;
                findEnumParams.findBlockParams.blockName = src.getItem(findEnumParams.classNameIndex()).str;
                
                findBlockParams.categoryOfBlock = new CategoryOfBlock(CategoryOfBlock.Enum, null);
                mlistOfBlocks.add(findBlockParams);
                
                return;

            }
            else
            {
            	errors.add(new Error(this, i, i, "Identifier not exists behind enum keyword."));
            }
        }   // for
    }
	
	
	
	public void FindEventHandlerClass(HighArray_CodeString src,  
			FindClassParams findClassParams, int startIndexOfName, int endIndexOfName, 
			int indexOfSeparator, String fullNameOfClass)
	{
		
        int i;
        i = indexOfSeparator;

        int startParenthesis = indexOfSeparator;
        int endParenthesis = -1;
        FindBlockParams findBlockParams = new FindBlockParams(this, startParenthesis, endParenthesis);
                        
       
        findBlockParams.startIndex = IndexForHighArray.indexRelative(findBlockParams, src, startParenthesis);
        findBlockParams.blockName = fullNameOfClass;
                   	
        findClassParams.classNameIndex = IndexForHighArray.indexRelative(findClassParams, src, SkipBlank(src, true, 0, endIndexOfName));
    	findClassParams.startIndex = IndexForHighArray.indexRelative(findClassParams, src, startIndexOfName);
    	findClassParams.startIndexOfEventHandlerName = IndexForHighArray.indexRelative(findClassParams, src, startIndexOfName);
    	findClassParams.endIndexOfEventHandlerName = IndexForHighArray.indexRelative(findClassParams, src, endIndexOfName);
        findClassParams.found = true;
        findClassParams.findBlockParams = findBlockParams;
        findClassParams.findBlockParams.blockName = fullNameOfClass;
        
        if (findClassParams.classNameToExtend==null) {
        	findClassParams.classNameToExtend = "java.lang.object";
        }
        
        if (findClassParams.interfacesToImplement==null) {
        	findClassParams.interfacesToImplement = new ArrayList(1);
        	findClassParams.interfacesToImplement.add(fullNameOfClass);
        }
        
        findBlockParams.categoryOfBlock = new CategoryOfBlock(CategoryOfBlock.Class, null);
        mlistOfBlocks.add(findBlockParams);
        
        findClassParams.isEventHandlerClass = true;
        
        return;
    }
	
	
	/** startIndex : 접근지정자(static, public등) 
	 *  endIndex : block의 끝, 즉 '}' */
	public void FindClass(HighArray_CodeString src,  
			FindClassParams findClassParams, int startIndex, int endIndex, boolean isEnum)
	{
		if (isEnum) {
			FindEnum(src, findClassParams, startIndex, endIndex);
			findClassParams.isEnum = true;
			return;
		}
        int i;
        for (i = startIndex; i <= endIndex; )
        {        	
        	findClassParams.classIndex = IndexForHighArray.indexRelative(findClassParams, src, Find(src, "class", i, endIndex));

            if (findClassParams.classIndex() == -1) {
				errors.add(new Error(this, endIndex, endIndex, "class keyword not exists"));
            	return;
            }

            // 공백 스킵                
            i = SkipBlank(src, false, findClassParams.classIndex() + 1, endIndex);
            if (i==endIndex+1) break;
            
            CodeString str = src.getItem(i);

            int indexOfExtendsOrImplementsOrCommaOrLeftPair = -1;
            
            // 클래스 이름 찾기
            if (IsIdentifier(str)  && !CompilerHelper.IsComment(str))   // 식별자 이면
            {            	
                findClassParams.classNameIndex = IndexForHighArray.indexRelative(findClassParams, src, i);

                // 공백 스킵
                i = SkipBlank(src, false, i + 1, endIndex);
                if (i==endIndex+1) break;
                
                int indexOfNextOfClassName = i;
                
                Template t = null;
                int leftPairIndex = i;
				// class Stack <T> {} 에서 템플릿을 등록하고 올바른 endIndexNew을 정한다.
				if (src.getItem(leftPairIndex).equals("<")) {
					int rightPairIndex = CompilerHelper.CheckParenthesis(src, "<", ">", leftPairIndex, src.count-1, false);
					if (rightPairIndex!=-1) {
						t = isTemplate(src, rightPairIndex);
						if (t!=null) {
							String typeNameToChange = getFullName(src, leftPairIndex+1, rightPairIndex-1).str;
							t.typeNameToChange = typeNameToChange;
							findClassParams.template = t;
							this.mlistOfAllTemplates.add(t);
							indexOfNextOfClassName = this.SkipBlank(src, false, rightPairIndex+1, src.count-1);
						}
					}
				}//if (src.getItem(leftPairIndex).equals("<")) {
				                
               
                CodeString nextOfClassName = src.getItem(indexOfNextOfClassName);
                
                indexOfExtendsOrImplementsOrCommaOrLeftPair = indexOfNextOfClassName;
                
                
                
                if (str.equals("HScrollBar")) {
                	int a;
                	a=0;
                	a++;
                }
                
                int indexOfExtendsOrImplementsOrCommaOrLeftPairOld;
                if (nextOfClassName.equals("extends") || nextOfClassName.equals("implements")) {
                	int j;
                	if (nextOfClassName.equals("extends")) {
                		findClassParams.indexOfExtends = IndexForHighArray.indexRelative(findClassParams, src, indexOfNextOfClassName);
                		indexOfExtendsOrImplementsOrCommaOrLeftPair = SkipBlank(src, false, indexOfNextOfClassName + 1, endIndex);
                        if (indexOfExtendsOrImplementsOrCommaOrLeftPair==endIndex+1) break;
                       
                        indexOfExtendsOrImplementsOrCommaOrLeftPairOld = indexOfExtendsOrImplementsOrCommaOrLeftPair;
                        int indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1 = getFullNameIndex0(src, false, indexOfExtendsOrImplementsOrCommaOrLeftPairOld);
                        indexOfExtendsOrImplementsOrCommaOrLeftPair = this.SkipBlank(src, false, indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1+1, src.count-1);
                       
                        if (indexOfExtendsOrImplementsOrCommaOrLeftPairOld <= 
                        		indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1) {
                        	try{
                        		// 같은 파일에 정의된 클래스를 상속하는 경우 fullname이 틀릴수 있다.
                        	findClassParams.classNameToExtend = getFullNameType(this, indexOfExtendsOrImplementsOrCommaOrLeftPairOld, indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1);
                        	}catch(Exception e) {
                        		int a;
                        		a=0;
                        		a++;
                        	}
                        	// 상속 클래스의 시작과 끝 인덱스
                        	findClassParams.startIndexOfClassNameToExtend = IndexForHighArray.indexRelative(findClassParams, src, indexOfExtendsOrImplementsOrCommaOrLeftPairOld);
                        	findClassParams.endIndexOfClassNameToExtend = IndexForHighArray.indexRelative(findClassParams, src, indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1);
                        	//템플릿이면 찾아서 등록한다.
                        	Template template = new Template(this); 
                        	IsType(src, true, findClassParams.endIndexOfClassNameToExtend(), template);
                        	if (template.found) {
                        		mlistOfAllTemplates.add(template);
                        		findClassParams.templateForExtending = template;
                        	}
                        	CodeString codeStr = src.getItem(indexOfExtendsOrImplementsOrCommaOrLeftPair);
                        	
                        	indexOfNextOfClassName = indexOfExtendsOrImplementsOrCommaOrLeftPair;
                        	
                        	if (codeStr.equals("{")) {
                        		nextOfClassName = codeStr;
                        		//i = indexOfExtendsOrImplementsOrCommaOrLeftPair;
                        	}
                        	else if (codeStr.equals("implements")) {
                        		findClassParams.indexOfImplements = IndexForHighArray.indexRelative(findClassParams, src, indexOfExtendsOrImplementsOrCommaOrLeftPair);
                        		nextOfClassName = new CodeString("implements", textColor);
                        		
                        		//indexOfExtendsOrImplementsOrCommaOrLeftPair++;
                       
                        	}                        	
                        	else { // '{'이 없을 때
                        		//indexOfExtendsOrImplementsOrCommaOrLeftPair = indexOfNextOfClassName;
                        		Compiler.errors.add(new Error(this, indexOfExtendsOrImplementsOrCommaOrLeftPair, 
                        				indexOfExtendsOrImplementsOrCommaOrLeftPair, "\"{\" not exist"));
                        	}
                        	
                        }
                         
                	} // if (nextOfClassName.equals("extends")) {
                	
                	if (nextOfClassName.equals("implements")) {
                		findClassParams.indexOfImplements = IndexForHighArray.indexRelative(findClassParams, src, indexOfNextOfClassName);
                		
                		int indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1;
                		if (findClassParams.classNameToExtend==null) {
                			indexOfExtendsOrImplementsOrCommaOrLeftPair = indexOfNextOfClassName;
                		}
                		if (indexOfNextOfClassName==1919) {
                			int a;
                			a=0;
                			a++;
                		}
                		while (true) {
                			indexOfExtendsOrImplementsOrCommaOrLeftPair = SkipBlank(src, false, indexOfExtendsOrImplementsOrCommaOrLeftPair+1, endIndex);
	                        if (indexOfExtendsOrImplementsOrCommaOrLeftPair==endIndex+1) break;
	                        
	                        indexOfExtendsOrImplementsOrCommaOrLeftPairOld = indexOfExtendsOrImplementsOrCommaOrLeftPair;
	                        indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1 = getFullNameIndex0(src, false, indexOfExtendsOrImplementsOrCommaOrLeftPair);
	                        //indexOfExtendsOrImplementsOrCommaOrLeftPair = indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1+1;
	                        indexOfExtendsOrImplementsOrCommaOrLeftPair = this.SkipBlank(src, false, indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1+1, src.count-1);
	                        
	                        if (indexOfExtendsOrImplementsOrCommaOrLeftPairOld <= 
	                        		indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1) {
	                        	if (findClassParams.interfaceNamesToImplement==null) {
	                        		findClassParams.interfaceNamesToImplement = new ArrayListString(5);
	                        	}
	                        	// 같은 파일에 정의된 클래스를 구현하는 경우 fullname이 틀릴수 있다.
	                        	String fullname = getFullNameType(this, indexOfExtendsOrImplementsOrCommaOrLeftPairOld, indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1); 
	                        	findClassParams.interfaceNamesToImplement.add(fullname);
	                        	// 구현인터페이스들의 시작과 끝 인덱스
	                        	if (findClassParams.listOfStartIndexOfInterfaceNamesToImplement==null) {
	                        		findClassParams.listOfStartIndexOfInterfaceNamesToImplement = new ArrayList(2);
	                        		findClassParams.listOfEndIndexOfInterfaceNamesToImplement = new ArrayList(2);
	                        	}
	                        	IndexForHighArray startIndexOfInteface = IndexForHighArray.indexRelative(findClassParams, src, indexOfExtendsOrImplementsOrCommaOrLeftPairOld);
	                        	IndexForHighArray endIndexOfInteface = IndexForHighArray.indexRelative(findClassParams, src, indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1);
	                        	findClassParams.listOfStartIndexOfInterfaceNamesToImplement.add(startIndexOfInteface);
	                        	findClassParams.listOfEndIndexOfInterfaceNamesToImplement.add(endIndexOfInteface);
	                        	//템플릿이면 찾아서 등록한다.
	                        	Template template = new Template(this); 
	                        	IsType(src, true, indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1, template);
	                        	if (findClassParams.listOfTemplatesForImplementing==null) {
	                        		findClassParams.listOfTemplatesForImplementing = new ArrayListIReset(2);
	                        	}
	                        	if (template.found) {
	                        		mlistOfAllTemplates.add(template);
	                        		findClassParams.listOfTemplatesForImplementing.add(template);
	                        	}
	                        	CodeString codeStr = src.getItem(indexOfExtendsOrImplementsOrCommaOrLeftPair);
	                        	if (codeStr.equals("{")) {
	                        		//i = indexOfExtendsOrImplementsOrCommaOrLeftPair;
	                        		break;
	                        	}
	                        	else if (codeStr.equals(",")) {
	                        		//indexOfExtendsOrImplementsOrCommaOrLeftPair++;
	                        	}
	                        	else {
	                        		Compiler.errors.add(new Error(this, indexOfExtendsOrImplementsOrCommaOrLeftPair, 
	                        				indexOfExtendsOrImplementsOrCommaOrLeftPair, "\"{\" not exist"));
	                        		break;
	                        	}
	                        }
	                        else { // '{'이 없을 때
                            	//i = indexOfNextOfClassName;
	                        	break;
	                        }
                		}//while (true) {
                		
                	}//if (nextOfClassName.equals("implements")) {
                	else if (nextOfClassName.equals("{")) { // extends ClassName { 에서 {
                		
                	}
                	else {// extends ClassName 에서 { 이 없는 경우
                		errors.add(new Error(this, i,i,"invalid class"));
                	}
                } // if (nextOfClassName.equals("extends") || nextOfClassName.equals("implements")) {
                else {
                	indexOfExtendsOrImplementsOrCommaOrLeftPair = SkipBlank(src, false, indexOfNextOfClassName, endIndex);
                }
                i = indexOfExtendsOrImplementsOrCommaOrLeftPair;
                indexOfNextOfClassName = indexOfExtendsOrImplementsOrCommaOrLeftPair;

                int startParenthesis = -1;
                int endParenthesis = -1;
                FindBlockParams findBlockParams = new FindBlockParams(this, startParenthesis, endParenthesis);
                     
                CodeString nextOfClassName2 = src.getItem(indexOfNextOfClassName); 
                
                if (nextOfClassName2.equals("{")==false) {
                	//startParenthesis = Skip(src, false, "{", indexOfNextOfClassName, endIndex);
                	errors.add(new Error(this, indexOfNextOfClassName, indexOfNextOfClassName, 
                			"\"{\" not exist"));
                }
                else {
                	startParenthesis = indexOfNextOfClassName;
                }
                if (startParenthesis==endIndex+1) {
                	errors.add(new Error(this, endIndex, endIndex, "invalid class."));
                	return;
                }
                findBlockParams.startIndex = IndexForHighArray.indexRelative(findBlockParams, src, startParenthesis);
                findBlockParams.blockName = src.getItem(findClassParams.classNameIndex()).str;
                ReturnOfFindAccessModifier r = FindAccessModifier(src, 0, findClassParams.classIndex() - 1, findClassParams.accessModifier);
                int k = r.r;
                
                int docuIndex;
            	if (findClassParams.accessModifier.found) docuIndex = k-1;
            	else {
            		docuIndex = findClassParams.classIndex()-1;
            	}
            	int indexOfDocuEnd = SkipOnlyBlank(src, true, 0, docuIndex); // 공백 스킵            	
            	DocuComment docu = 
            			FindDocuComment(src, /*true, findFunctionParams, null,*/ 0, indexOfDocuEnd);
            	findClassParams.docuComment = docu;
            	
            	
            	//findClassParams.startIndex = IndexForHighArray.indexRelative(findClassParams, src, k);
            	findClassParams.startIndex = IndexForHighArray.indexRelative(findClassParams, src, 
            			getIndexWhickRemoveLeftBlankAndCommentAndAnnotation(src, r));
                findClassParams.endIndex = findBlockParams.endIndex;
                findClassParams.found = true;
                findClassParams.findBlockParams = findBlockParams;
                findClassParams.findBlockParams.blockName = src.getItem(findClassParams.classNameIndex()).str;
                
                if (findClassParams.classNameToExtend==null) {
                	findClassParams.classNameToExtend = "java.lang.object";
                }
                
                findBlockParams.categoryOfBlock = new CategoryOfBlock(CategoryOfBlock.Class, null);
                mlistOfBlocks.add(findBlockParams);
                
                return;

            }
            else
            {
            	errors.add(new Error(this, i, i, "Identifier not exists behind class keyword."));
            }
        }   // for

      
    }
	
	/** startIndex : 접근지정자(static, public등) 
	 *   */
	public void FindInterface(HighArray_CodeString src,  
			FindClassParams findInterfaceParams, int startIndex, int endIndex)
	{
        int i;
        for (i = startIndex; i <= endIndex; )
        {        	
        	findInterfaceParams.classIndex = IndexForHighArray.indexRelative(findInterfaceParams, src, Find(src, "interface", i, endIndex));

            if (findInterfaceParams.classIndex() == -1) {
				errors.add(new Error(this, endIndex, endIndex, "interface keyword not exists"));
            	return;
            }
            
            findInterfaceParams.isInterface = true;

            // 공백 스킵                
            i = SkipBlank(src, false, findInterfaceParams.classIndex() + 1, endIndex);
            if (i==endIndex+1) break;
            
            CodeString str = src.getItem(i);

            int indexOfExtendsOrImplementsOrCommaOrLeftPair = -1;
            
            // 클래스 이름 찾기
            if (IsIdentifier(str)  && !CompilerHelper.IsComment(str))   // 식별자 이면
            {            	
                findInterfaceParams.classNameIndex = IndexForHighArray.indexRelative(findInterfaceParams, src, i);

                // 공백 스킵
                i = SkipBlank(src, false, i + 1, endIndex);
                if (i==endIndex+1) break;
                
                Template t = null;
                int leftPairIndex = i;
				// class OnListener <T> {} 에서 템플릿을 등록하고 올바른 endIndexNew을 정한다.
				if (src.getItem(leftPairIndex).equals("<")) {
					int rightPairIndex = CompilerHelper.CheckParenthesis(src, "<", ">", leftPairIndex, src.count-1, false);
					if (rightPairIndex!=-1) {
						t = isTemplate(src, rightPairIndex);
						if (t!=null) {
							String typeNameToChange = getFullName(src, leftPairIndex+1, rightPairIndex-1).str;
							t.typeNameToChange = typeNameToChange;
							findInterfaceParams.template = t;
							this.mlistOfAllTemplates.add(t);
						}
					}
				}
				
                
                int indexOfNextOfClassName = i;
                CodeString nextOfClassName = src.getItem(indexOfNextOfClassName);
                
                if (str.equals("HScrollBar")) {
                	int a;
                	a=0;
                	a++;
                }
                
                indexOfExtendsOrImplementsOrCommaOrLeftPair = i;
                
                int indexOfExtendsOrImplementsOrCommaOrLeftPairOld;
                if (nextOfClassName.equals("extends")) { 
                	findInterfaceParams.indexOfExtends = IndexForHighArray.indexRelative(findInterfaceParams, src, indexOfNextOfClassName);
            		int indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1;
            		indexOfExtendsOrImplementsOrCommaOrLeftPair = indexOfNextOfClassName;
            		while (true) {
            			indexOfExtendsOrImplementsOrCommaOrLeftPair = SkipBlank(src, false, indexOfExtendsOrImplementsOrCommaOrLeftPair+1, endIndex);
                        if (indexOfExtendsOrImplementsOrCommaOrLeftPair==endIndex+1) break;
                        indexOfExtendsOrImplementsOrCommaOrLeftPairOld = indexOfExtendsOrImplementsOrCommaOrLeftPair;
                        indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1 = getFullNameIndex0(src, false, indexOfExtendsOrImplementsOrCommaOrLeftPairOld);
                        //indexOfExtendsOrImplementsOrCommaOrLeftPair = indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1+1;
                        indexOfExtendsOrImplementsOrCommaOrLeftPair = this.SkipBlank(src, false, indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1+1, src.count-1);
                        
                        if (indexOfExtendsOrImplementsOrCommaOrLeftPairOld <=
                        		indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1) {
                        	if (findInterfaceParams.interfaceNamesToImplement==null) {
                        		findInterfaceParams.interfaceNamesToImplement = new ArrayListString(5);
                        	}
                        	// 같은 파일에 정의된 클래스를 구현하는 경우 fullname이 틀릴수 있다.
                        	String fullname = getFullNameType(this, indexOfExtendsOrImplementsOrCommaOrLeftPairOld, indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1); 
                        	findInterfaceParams.interfaceNamesToImplement.add(fullname);
                        	// 구현인터페이스들의 시작과 끝 인덱스
                        	if (findInterfaceParams.listOfStartIndexOfInterfaceNamesToImplement==null) {
                        		findInterfaceParams.listOfStartIndexOfInterfaceNamesToImplement = new ArrayList(2);
                        		findInterfaceParams.listOfEndIndexOfInterfaceNamesToImplement = new ArrayList(2);
                        	}
                        	IndexForHighArray startIndexOfInteface = IndexForHighArray.indexRelative(findInterfaceParams, src, indexOfExtendsOrImplementsOrCommaOrLeftPairOld);
                        	IndexForHighArray endIndexOfInteface = IndexForHighArray.indexRelative(findInterfaceParams, src, indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1);
                        	findInterfaceParams.listOfStartIndexOfInterfaceNamesToImplement.add(startIndexOfInteface);
                        	findInterfaceParams.listOfEndIndexOfInterfaceNamesToImplement.add(endIndexOfInteface);
                        	//템플릿이면 찾아서 등록한다.
                        	Template template = new Template(this); 
                        	IsType(src, true, indexOfExtendsOrImplementsOrCommaOrLeftPairMinus1, template);
                        	if (findInterfaceParams.listOfTemplatesForImplementing==null) {
                        		findInterfaceParams.listOfTemplatesForImplementing = new ArrayListIReset(2);
                        	}
                        	if (template.found) {
                        		mlistOfAllTemplates.add(template);
                        		findInterfaceParams.listOfTemplatesForImplementing.add(template);
                        	}
                        	
                        	CodeString codeStr = src.getItem(indexOfExtendsOrImplementsOrCommaOrLeftPair);
                        	indexOfNextOfClassName = indexOfExtendsOrImplementsOrCommaOrLeftPair;
                        	if (codeStr.equals("{")) {
                        		//i = indexOfImplementsOrImplementsOrCommaOrLeftPair;
                        		break;
                        	}
                        	else if (codeStr.equals(",")) {
                        		//indexOfExtendsOrImplementsOrCommaOrLeftPair++;
                        	}
                        	else {
                        		errors.add(new Error(this, indexOfNextOfClassName, indexOfNextOfClassName, "\"{\" or \",\" not exist"));
                        		break;
                        		
                        	}
                        }
                        else { // '{'이 없을 때
                        	//i = indexOfNextOfClassName;
                        	break;
                        }
            		}//while (true) {
            		
            	}//if (nextOfClassName.equals("extends")) {  
            	else if (nextOfClassName.equals("{")) {
            		
            	}
            	else {
            		errors.add(new Error(this, i,i,"invalid class"));
            	}
                
                
                
                /*else {
                	indexOfExtendsOrImplementsOrCommaOrLeftPair = SkipBlank(src, false, indexOfNextOfClassName, endIndex);
                }*/
                i = indexOfExtendsOrImplementsOrCommaOrLeftPair;

                int startParenthesis = -1;
                int endParenthesis = -1;
                FindBlockParams findBlockParams = new FindBlockParams(this, startParenthesis, endParenthesis);
                                
                
                if (src.getItem(indexOfNextOfClassName).equals("{")==false) {
                	errors.add(new Error(this, indexOfNextOfClassName, indexOfNextOfClassName, "\"{\" not exist"));
                	//startParenthesis = Skip(src, false, "{", indexOfNextOfClassName, endIndex);
                }
                else {
                	startParenthesis = indexOfNextOfClassName;
                }
                if (startParenthesis==endIndex+1) {
                	errors.add(new Error(this, endIndex, endIndex, "invalid class."));
                	return;
                }
                findBlockParams.startIndex = IndexForHighArray.indexRelative(findBlockParams, src, startParenthesis);
                findBlockParams.blockName = src.getItem(findInterfaceParams.classNameIndex()).str;
                ReturnOfFindAccessModifier r = FindAccessModifier(src, 0, findInterfaceParams.classIndex() - 1, findInterfaceParams.accessModifier);
                 
                int k = r.r;
                
                int docuIndex;
            	if (findInterfaceParams.accessModifier.found) docuIndex = k-1;
            	else {
            		docuIndex = findInterfaceParams.classIndex()-1;
            	}
            	int indexOfDocuEnd = SkipOnlyBlank(src, true, 0, docuIndex); // 공백 스킵            	
            	DocuComment docu = 
            			FindDocuComment(src, /*true, findFunctionParams, null,*/ 0, indexOfDocuEnd);
            	findInterfaceParams.docuComment = docu;
            	
            	
            	findInterfaceParams.startIndex = IndexForHighArray.indexRelative(findInterfaceParams, src, k);
            	findInterfaceParams.startIndex = IndexForHighArray.indexRelative(findInterfaceParams, src, 
            			getIndexWhickRemoveLeftBlankAndCommentAndAnnotation(src, r));
                //findInterfaceParams.endIndex = findBlockParams.endIndex;
                findInterfaceParams.found = true;
                findInterfaceParams.findBlockParams = findBlockParams;
                findInterfaceParams.findBlockParams.blockName = src.getItem(findInterfaceParams.classNameIndex()).str;
                
               
                findBlockParams.categoryOfBlock = new CategoryOfBlock(CategoryOfBlock.Interface, null);
                mlistOfBlocks.add(findBlockParams);
                
                return;

            }
            else
            {
            	errors.add(new Error(this, i, i, "Identifier not exists behind class keyword."));
            }
        }   // for

      
    }
	
	static class ReturnOfFindAccessModifier implements IReset {
		int r;
		/** 0:접근지정자가 아닌 첫번째 인덱스, 1:startIndex-1, 2:다큐주석의 인덱스+1*/
		int type;
		
		/** @param type 0:접근지정자가 아닌 첫번째 인덱스, 1:startIndex-1, 2:다큐주석의 인덱스+1*/
		ReturnOfFindAccessModifier(int r, int type) {
			this.r = r;
			this.type = type;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
		}
	}
	
	/** endIndex부터 역순으로 접근지정자들을 찾는다. 
	 * 반환값:0:접근지정자가 아닌 첫번째 인덱스, 또는 1:startIndex-1, 또는 2:다큐주석의 인덱스+1*/
	ReturnOfFindAccessModifier FindAccessModifier(HighArray_CodeString src, int startIndex, int endIndex, AccessModifier accessModifier) {
		if (accessModifier!=null) 
			accessModifier.reset();
		
		int k;
        // 접근지정자
        for (k = endIndex; k >= startIndex; k--)
        {
        	int index = SkipOnlyBlank(src, true, startIndex, k);
        	if (index==startIndex-1) {
        		return new ReturnOfFindAccessModifier(index, 1);
        		
        	}
        	k = index;
        	CodeString cstr = src.getItem(index);
        	if (!CompilerHelper.IsDocuComment(cstr)) {
        		if (CompilerHelper.IsRegularComment(cstr)) continue;
        		if (CompilerHelper.IsAnnotation(cstr)) continue;
	        	if (index<0 || IsAccessModifier(cstr)==false) {
	        		//break; // 접근지정자가 아닌 인덱스
	        		if (k+1<=endIndex) {
		        		accessModifier.startIndex = IndexForHighArray.indexRelative(accessModifier, src, k+1);
		        		accessModifier.endIndex = IndexForHighArray.indexRelative(accessModifier, src, endIndex);
	        		}
	        		return new ReturnOfFindAccessModifier(k, 0);
	        	}
	        	else {
	        		if (accessModifier==null) {
	        			// 변수나 메서드, 클래스에 붙는 accessModifier는 null일 수 없다.
	        			accessModifier = new AccessModifier(this, -1, -1);
	        		}
	        		if (cstr.equals("static")) {
	        			accessModifier.isStatic = true;
	        			accessModifier.found = true;
	        		}
	        		else if (cstr.equals("synchronized")) {
	        			accessModifier.isSynchronized = true;
	        			accessModifier.found = true;
	        		}
	        		else if (cstr.equals("abstract")) {
	        			accessModifier.isAbstract = true;
	        			accessModifier.found = true;
	        		}
	        		else if (cstr.equals("final")) {
	        			accessModifier.isFinal = true;
	        			accessModifier.found = true;
	        		}
	        		else if (cstr.equals("public")) {
	        			accessModifier.accessPermission = AccessModifier.AccessPermission.Public;
	        			accessModifier.found = true;
	        		}
	        		else if (cstr.equals("private")) {
	        			accessModifier.accessPermission = AccessModifier.AccessPermission.Private;
	        			accessModifier.found = true;
	        		}
	        		else if (cstr.equals("protected")) {
	        			accessModifier.accessPermission = AccessModifier.AccessPermission.Protected;
	        			accessModifier.found = true;
	        		}
	        		else if (cstr.equals("default")) {
	        			accessModifier.accessPermission = AccessModifier.AccessPermission.Default;
	        			accessModifier.found = true;
	        		}
	        	}
        	}
        	else {
        		//return k+1;//다큐주석의 인덱스+1
        		return new ReturnOfFindAccessModifier(k+1, 2);
        	}
        }
        //return k;//접근지정자가 아닌 첫번째 인덱스, 또는 startIndex-1
        return new ReturnOfFindAccessModifier(k, 1);
	}
	

	
	
	/** mlistOfAllVarUses(listOfAllVarUses)에서 모든 타입 캐스트문(TypeCast)을 찾아 
	 * mlistOfAllTypeCasts에 저장한다.*/
	void findTypeCasts(HighArray_CodeString src, HighArray<FindVarUseParams> listOfAllVarUses) {
		boolean isTypeCast = false;
		TypeCast typeCast = null;
		int i;
		int typeIndex;
		CodeString strType = null;
		int len = listOfAllVarUses.getCount();
		for (i=0; i<len; i++) {
			isTypeCast = false;
			FindVarUseParams varUse = (FindVarUseParams) listOfAllVarUses.getItem(i);
			if (varUse.index()==669) {
				int a;
				a=0;
				a++;
			}
			int nextIndex = this.SkipBlank(src, false, varUse.index()+1, src.count-1);
			CodeString nextStr = src.getItem(nextIndex);
			if (nextStr.equals(")")==false) continue;
			
			typeIndex = this.getFullNameIndex0(src, true, varUse.index());
			typeIndex = SkipBlank(src, true, 0, typeIndex-1);
			if (typeIndex==-1) {
				errors.add(new Error(this, varUse.index(), varUse.index(), "the start of file"));
				continue;
			}
			strType = src.getItem(typeIndex);
			
			if (strType.equals("(")) {
				int leftParentOfTypeCast, rightParentOfTypeCast;
				int startIndexToAffect=-1, endIndexToAffect=-1; 
				int startIndexToAffect_mlistOfAllVarUses=-1, endIndexToAffect_mlistOfAllVarUses=-1;
				boolean affectsExpression = false;
				leftParentOfTypeCast = typeIndex;
				
				
				rightParentOfTypeCast = CompilerHelper.CheckParenthesis(src, "(", ")", leftParentOfTypeCast, src.count-1, false);
				if (rightParentOfTypeCast!=-1) {
					Template template = new Template(this); 
					int typeIndex2 = IsType(src, false, leftParentOfTypeCast+1, template);
					if (typeIndex2!=-1) {
						int tempRightPair = SkipBlank(src, false, typeIndex2+1, src.count-1);
						if (tempRightPair==rightParentOfTypeCast) { // 괄호안은 타입캐스트문
							if (typeIndex2==58728) {
								int a;
								a=0;
								a++;
							}
							//if (isScopeAll) i = 0;에서 typeIndex2는 isScopeAll이고, keyword는 if이다.
							int keywordIndex = SkipBlank(src, true, 0, leftParentOfTypeCast-1);
							CodeString keyword = src.getItem(keywordIndex);
							if (this.IsKeyword(keyword)) continue;
							
							int startIndexOfID = SkipBlank(src, false, rightParentOfTypeCast+1, src.count-1);
							CodeString id = src.getItem(startIndexOfID);
							if (id.equals("buffer")) {
								int a;
								a=0;
								a++;
							}
							if ( IsIdentifier(id) || CompilerHelper.IsConstant(id)) { // (타입)id, (타입)a.b.c+d
								// ((java.lang.Object)buffer[i]).equals("("),   (long)buffer.length
								isTypeCast = true;
								startIndexToAffect = startIndexOfID;
								//endIndexToAffect = startIndexToAffect;
								endIndexToAffect = getFullNameIndex3(src, startIndexOfID);
								startIndexToAffect_mlistOfAllVarUses = getIndexInmListOfAllVarUses(src, 
										this.mlistOfAllVarUses,	0, startIndexToAffect, true);
								endIndexToAffect_mlistOfAllVarUses = getIndexInmListOfAllVarUses(src, 
										this.mlistOfAllVarUses,	startIndexToAffect_mlistOfAllVarUses, 
										endIndexToAffect, false);
								affectsExpression = false;
								
							}
							else if (id.equals("(")) { // (타입)(수식)
								isTypeCast = true;
								startIndexToAffect = startIndexOfID+1;
								endIndexToAffect = CompilerHelper.CheckParenthesis(src, "(", ")", startIndexOfID, src.count-1, false);
								if (endIndexToAffect==-1) {
									errors.add(new Error(this, startIndexOfID, startIndexOfID, "( not paired."));
								}
								else endIndexToAffect--;
								startIndexToAffect_mlistOfAllVarUses = getIndexInmListOfAllVarUses(src, 
										this.mlistOfAllVarUses,	0, startIndexToAffect, true);
								endIndexToAffect_mlistOfAllVarUses = getIndexInmListOfAllVarUses(src, 
										this.mlistOfAllVarUses,	startIndexToAffect_mlistOfAllVarUses, 
										endIndexToAffect+1, false);
								affectsExpression = true;
							}
							else {
							}
							if (isTypeCast) {
								typeCast = new TypeCast(this, leftParentOfTypeCast+1, typeIndex2, 
										startIndexToAffect, endIndexToAffect);
								typeCast.name = getFullNameType(this, leftParentOfTypeCast+1, typeIndex2);
								typeCast.startIndexToAffect_mlistOfAllVarUses = startIndexToAffect_mlistOfAllVarUses;
								typeCast.endIndexToAffect_mlistOfAllVarUses = endIndexToAffect_mlistOfAllVarUses;
								typeCast.indexOfLeftPair = IndexForHighArray.indexRelative(typeCast, src, leftParentOfTypeCast);
								typeCast.indexOfRightPair = IndexForHighArray.indexRelative(typeCast, src, rightParentOfTypeCast);
								typeCast.affectsExpression = affectsExpression;
								
								int indexOfVarUseTypeCasting = this.SkipBlank(src, false, leftParentOfTypeCast+1, src.count-1);
								indexOfVarUseTypeCasting = this.getFullNameIndex0(src, false, indexOfVarUseTypeCasting);
								indexOfVarUseTypeCasting = this.SkipBlank(src, true, 0, indexOfVarUseTypeCasting);
								String varUseName = src.getItem(indexOfVarUseTypeCasting).str;
								typeCast.varUseTypeCasting = this.getVarUseWithIndex(this.mlistOfAllVarUsesHashed, varUseName, indexOfVarUseTypeCasting);
								
								varUse.typeCast = typeCast;
								if (template.found) {
									mlistOfAllTemplates.add(template);
									typeCast.template = template;
								}								
								this.mlistOfAllTypeCasts.add(typeCast);
							}
						}//if (tempRightPair==rightParentOfTypeCast) { // 괄호안은 타입캐스트문
						else {
						}
					}//if (typeIndex!=-1) {
					else {
					}
				} // if (rightParentOfTypeCast!=-1) {
				else {
					errors.add(new Error(this, leftParentOfTypeCast, leftParentOfTypeCast, "( not paired."));
				}
			} // if (strType.equals("(")) {
		} // for (i=0; i<listOfAllVarUses.count; i++) {
	}
	
	
	void putTempate(Template template, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfAllTemplates.add(template);
		}
		
	}
	
	void putFindClassParams(FindClassParams classParams, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfAllDefinedClasses.add(classParams);
		}
	}
	
	void putConstructor(Constructor constructor, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfAllConstructor.add(constructor);
		}
	}
	
	void putFindVarParams(FindVarParams var, ModeAllOrUpdate modeAllOrUpdate, boolean isLocal) {
		if (isLocal==false) {
			if (modeAllOrUpdate==ModeAllOrUpdate.All) {
				mlistOfAllMemberVarDeclarations.add(var);
			}
		}
		else {
			if (modeAllOrUpdate==ModeAllOrUpdate.All) {
				mlistOfAllLocalVarDeclarations.add(var);
			}
		}
	}
	
	/** 독립적인 할당문 뿐만 아니라 모든 할당문, 
	 * 예를들어 a=2+(a=3)+2; 혹은 f(a=2, 3);과 같은 문장에 포함된 대입문들도 대상이 된다. 
	 * 모든 할당문들을 mlistOfAllAssignStatements에 넣는다.*/
	void putFindAssignStatementParamsTomlistOfAllAssignStatements(FindAssignStatementParams assignStatement, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfAllAssignStatements.add(assignStatement);
		}
	}
	
	/** 독립적인 할당문만을 mlistOfAssignStatements에 넣는다.*/
	void putFindAssignStatementParams(FindAssignStatementParams assignStatement, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfAssignStatements.add(assignStatement);
		}
	}
	
	void putVarUse(FindVarUseParams varUse, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfAllVarUses.add(varUse);
			mlistOfAllVarUsesHashed.input(varUse);
		}
	}
	
	void putSynchronizedBlockTomlistOfSynchronizedBlocks(FindControlBlockParams control, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfSynchronizedBlocks.add(control);
		}
	}
	
	void putFindControlBlockParams(FindControlBlockParams control, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfAllControlBlocks.add(control);
		}
	}
	
	void putFindFunctionParams(FindFunctionParams function, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfAllFunctions.add(function);
		}
	}
	
	void putFindSpecialStatementParams(FindSpecialStatementParams specialStatement, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfSpecialStatement.add(specialStatement);
		}
	}
	
	void putFindBlockParams(FindBlockParams findBlockParams, ModeAllOrUpdate modeAllOrUpdate) {		
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfBlocks.add(findBlockParams);
		}
	}
	
	void putArrayIntializer(FindArrayInitializerParams arrayIntializer, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			mlistOfAllArrayIntializers.add(arrayIntializer);
		}
	}
	
	void putVarToParent(FindVarParams var, Block parent, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			parent.listOfVariableParams.add(var);
		}
	}
	
	void putVarToFunctionArg(FindVarParams var, FindFunctionParams func, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			func.listOfFuncArgs.add(var);
		}
	}
	
	void putArrayInitializerToParent(FindArrayInitializerParams arr, FindArrayInitializerParams parent, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			parent.listOfFindArrayInitializerParams.add(arr);
		}
	}
	
	void putVarUseToClass(FindVarUseParams varUse, FindClassParams parentClass, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			parentClass.listOfAllVarUses.add(varUse);
			if (varUse.isForVarOrForFunc) parentClass.listOfAllVarUsesForVar.add(varUse);
			else parentClass.listOfAllVarUsesForFunc.add(varUse);
		}
	}
	
	void putVarUseToFunction(FindVarUseParams varUse, FindFunctionParams parentFunc, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			parentFunc.listOfAllVarUses.add(varUse);
			if (varUse.isForVarOrForFunc) parentFunc.listOfAllVarUsesForVar.add(varUse);
			else parentFunc.listOfAllVarUsesForFunc.add(varUse);
		}
	}
	
	void putStatementToParenthesisOfParent(FindStatementParams statement, FindControlBlockParams parent, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			parent.listOfStatementsInParenthesis.add(statement);
		}
	}
	
	void putStatementToParent(FindStatementParams statement, Block parent, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			parent.listOfStatements.add(statement);
		}
	}
	
	void putControlBlockToParent(FindControlBlockParams block, Block parent, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			parent.listOfControlBlocks.add(block);
		}
	}
	
	void putClassEnumInterfaceToResult(FindClassParams classParams, ArrayListIReset result, Stack stack, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			if (stack.len==0) { // 최상위 클래스만 등록
				result.add(classParams);
			}
		}
	}
	
	void putFunctionToParent(FindFunctionParams function, FindClassParams parent, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			parent.listOfFunctionParams.add(function);
		}
	}
	
	void putClassEnumInterfaceToParent(FindClassParams eventClass, ArrayListIReset parentClass, ModeAllOrUpdate modeAllOrUpdate) {
		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
			parentClass.add(eventClass);
		}
	}
	
		
	
	/** 변수선언 외에 변수사용도 찾는다. 
	 * 변수선언의 경우 endIndex는 세미콜론의 인덱스이다.
	 * @param findVarParams : out 
	 * @param index : if (IsIdentifier(cstr) || CompilerHelper.IsConstant(cstr) ||
        		IsDefaultType(cstr))   { // 식별자, 상수, 타입(타입캐스트)의 인덱스
	 * @param oldTypeIndex : int a, b;의 경우처럼 타입선언 b를 찾을때 이전 호출에서 ,로 끝날경우 
	 * int 타입의 인덱스
	 * @return OldTypeIndex : int a, b;의 경우처럼 타입선언 a를 찾은후 ,로 끝나는 경우 
	 * 다음 호출을 위해서 int 타입의 인덱스를 리턴한다.*/
	ReturnOfFindVarDecl FindVarDeclarationsAndVarUses(HighArray_CodeString src,
			int index, OldTypeIndex oldTypeIndex, ModeAllOrUpdate modeAllOrUpdate)
	{
		try{
		boolean isCommaPreviously = false;
		if (oldTypeIndex!=null) isCommaPreviously = true;

        
        if (10279<=index && index<=10300) {
        	int a;
        	a=0;
        	a++;
        }
        
        if (index==380) {
        	int a;
        	a=0;
        	a++;
        }
        
        CodeString cstr = src.getItem(index);
        
        if (cstr.equals("toCharArray")) {
        	int a;
        	a=0;
        	a++;
        }
       
      
        IndexForHighArray varNameIndex = null;
        IndexForHighArray typeStartIndex = null;
        IndexForHighArray typeEndIndex = null;

        // 변수 이름 찾기
        // cstr이 상수일 경우는 varUse를 찾기 위해서이다.
        //if (IsIdentifier(cstr) || CompilerHelper.IsConstant(cstr) ||
        //		IsDefaultType(cstr))   { // 식별자, 상수, 타입(타입캐스트)이면
        	
        	
        	varNameIndex = IndexForHighArray.indexRelative(null, src, index);
        	int typeIndex = SkipBlank(src, true, 0, index - 1);
        	// 다음 줄의 주석을 제거하면 변수선언 = 변수사용 일 때 변수사용의 확인이 안 된다.
        	//if (type==startIndex-1) continue;
        	
        	Template template = null;
        	
        	CodeString strType = null;        	
        	if (typeIndex>=0) {
        		strType = src.getItem(typeIndex);
        	}
			if (language==Language.C && strType.equals("*")) {
				typeStartIndex = IndexForHighArray.indexRelative(null, src, SkipBlank(src, true, 0, typeIndex - 1));
				if (typeStartIndex!=null) {
					if (IsIdentifier(src.getItem(typeStartIndex.index()))) {
						return null;	// 이항연산 id * id
					}
				}
				else {
				}
			}
			else { // 타입이거나 식별자이면 타입으로 인식한다.
				if (typeIndex==1364) {
					int a;
					a=0;
					a++;
				}
				
				//findVarParams.typeStartIndex = IsType(src, true, typeIndex);
				if (typeIndex>=0) {
					template = new Template(this);				
					typeStartIndex = IndexForHighArray.indexRelative(null, src, IsType(src, true, typeIndex, template));
					if (template.found) {
						//mlistOfAllTemplates.add(template);
						this.putTempate(template, modeAllOrUpdate);
					}
				}
				
				if (typeStartIndex!=null && typeStartIndex.index()==555) {
					int a;
					a=0;
					a++;
				}
				
				if (typeStartIndex==null) {
					if (strType!=null && IsIdentifier(strType)) {
						typeStartIndex = IndexForHighArray.indexRelative(null, src, typeIndex);
					}
					else {
						//continue;
					}
				}
				else {
					// 함수파라미터에서 int a, int b 와 같은 경우 두번째 int는 원래대로 처리
					isCommaPreviously = false;
				}
			}
			
			
			// int a=0, b=1과 같이 varName이 0일 경우는 타입이 아니고 수식이므로 isCommaPreviously = false로 한다.
			if (oldTypeIndex!=null && 
					oldTypeIndex.startIndexOfExpression()<=varNameIndex.index() && 
					varNameIndex.index()<=oldTypeIndex.endIndexOfExpression())
				isCommaPreviously = false;
			
			// 함수파라미터에서 int a, RectForPage b 와 같은 경우 두번째 RectForPage는 리턴
			int nextIndex2 = SkipBlank(src, false, varNameIndex.index()+1, src.count-1);
			if (nextIndex2==src.count) return null;
			
			//Character.Subset a;에서 Subset일 경우 strType은 '.'이다. 이때는 리턴을 안하고 varUse로 등록한다.
			// RedoBuffer.Pair p;에서 RedoBuffer나 Pair일 경우 strType이나 next는 '.'이다. 이때는 리턴을 안하고 varUse로 등록한다.
			CodeString next = src.getItem(nextIndex2);
			if ((strType!=null && strType.equals(".")) || next.equals("."))  {
				isCommaPreviously = false;
			}
			else {			
				// Point[] p;에서 Point일 경우 varUse로 등록한다.
				if (next.equals("[")) {
					int rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", nextIndex2, src.count-1, false);
					if (rightPair==src.count) return null;
					nextIndex2 = SkipBlank(src, false, rightPair+1, src.count-1);
					next = src.getItem(nextIndex2);
				}
				
				// Point p;에서 Point일 경우 varUse로 등록한다.
				if (IsIdentifier(next)) {
					isCommaPreviously = false;
				}
			}
			
			
			//if (strType.equals(",")) isCommaPreviously = false;
			
			//Template template = null;
        	
        	if (typeStartIndex!=null && typeStartIndex.index() != -1 || isCommaPreviously) { // 타입이면
        		// 변수선언으로 인정한다.
        		int k = 0;
        		ReturnOfFindAccessModifier r=null;
        		FindVarParams var;
        		AccessModifier accessModifier;
        		if (isCommaPreviously) {
        			typeStartIndex = oldTypeIndex.startIndex;
        			typeEndIndex = oldTypeIndex.endIndex;
        			accessModifier = oldTypeIndex.accessModifier;
        			template = oldTypeIndex.template;
        			
        			var = new FindVarParams(this, -1, -1);
        			var.typeStartIndex = typeStartIndex;
        			var.typeStartIndex.owner = var;
        			var.typeEndIndex = typeEndIndex;
        			var.typeEndIndex.owner = var;
        			var.varNameIndex = varNameIndex;
        			var.varNameIndex.owner = var;
        			var.accessModifier = accessModifier;
        			var.template = template;
        		}
        		else {
	        		typeEndIndex = IndexForHighArray.indexRelative(null, src, typeIndex);
	        		r = FindAccessModifier(src, 0, typeStartIndex.index() - 1, 
	        				accessModifier = new AccessModifier(this, -1, -1));
	        		k = r.r;
	        		
	        		var = new FindVarParams(this, -1, -1);
        			var.typeStartIndex = typeStartIndex;
        			var.typeStartIndex.owner = var;
        			var.typeEndIndex = typeEndIndex;
        			var.typeEndIndex.owner = var;
        			var.varNameIndex = varNameIndex;
        			var.varNameIndex.owner = var;
        			var.accessModifier = accessModifier;
        			var.template = template;
        		}
        		
        		index = SkipBlank(src, false, index+1, src.count-1);
        		if (index!=src.count) {
            		CodeString separator = src.getItem(index);
            		if (separator.equals(";") || separator.equals("=") || // 멤버 혹은 지역변수
            				separator.equals(",") || separator.equals(")")) { // 지역변수            			
            			if (cstr.equals("sized")) {
            				int a;
            				a = 0;
            				a++;
            			}
            			boolean hasAccess = hasAccessModifier(var, null);
            			if (isCommaPreviously==false) {
		            		if (hasAccess) var.startIndex = IndexForHighArray.indexRelative(var, src, k);
		        			else var.startIndex = var.typeStartIndex;
		            		var.startIndex = IndexForHighArray.indexRelative(var, src, getIndexWhickRemoveLeftBlankAndCommentAndAnnotation(src, r));
            			}
            			else { // int i=0, j=0, k=0에서 j와 k의 startIndex는 varNameIndex()이다.
            				var.startIndex = var.varNameIndex;
            			}
	            		// 변수의 endIndex()를 separator까지로 한다.
	            		var.endIndex = IndexForHighArray.indexRelative(var, src, index );
	            		var.found = true;
	            		if (template.found) {
	            			var.template = template;
	            		}
	            		
	            		int docuIndex;
	                	if (hasAccess) docuIndex = k;
	                	else docuIndex = var.typeStartIndex() - 1;
	                	//int indexOfDocuEnd = SkipOnlyBlank(src, true, 0, docuIndex); // 공백 스킵
	                	int indexOfDocuEnd = SkipOnlyBlankAndAnnotationAndRegularComment(src, true, 0, docuIndex); // 공백 스킵
	                	
	                	DocuComment docu = 
	                			FindDocuComment(src, /*false, null, findVarParams,*/ 0, indexOfDocuEnd);
	                	var.docuComment = docu; 
	                	
	                	int nextIndexOfExpression = -1;
	                	int startIndexOfExpression = -1;
	            		int endIndexOfExpression = -1;
	            		
	            		FindVarUseParams varUse = null;
	                	if (separator.equals("=")) {	// 변수사용으로도 등록한다. int a = 5;
	                		varUse = new FindVarUseParams(null); 
	                		varUse.index = IndexForHighArray.indexRelative(varUse, src, var.varNameIndex());
	                		varUse.isForVarOrForFunc = true;
	                		varUse.name = src.getItem(varUse.index()).str;
	                		varUse.originName = varUse.name;	                		
	                		//mlistOfAllVarUses.add(varUse);
	                		//mlistOfAllVarUsesHashed.input(varUse);
	                		this.putVarUse(varUse, modeAllOrUpdate);
	                		
	                		nextIndexOfExpression = SkipExpression(src, false, index+1, src.count-1);
	                		startIndexOfExpression = index+1;
	                		
	                		if (nextIndexOfExpression!=src.count) {
	                			separator = src.getItem(nextIndexOfExpression);
	                			endIndexOfExpression = nextIndexOfExpression-1;
	                		}
	                	}
	                	
	                	if (separator.equals(",")) { // int a, b 또는 int a=0, b=1;
	                		//OldTypeIndex(Compiler compiler, int startIndex, int endIndex, int startIndexOfExpression, int endIndexOfExpression,
	                		//		Template template, AccessModifier accessModifier)
	                		OldTypeIndex oldTypeIndex2 = new OldTypeIndex(this, var.typeStartIndex(), var.typeEndIndex(),
                    				startIndexOfExpression, endIndexOfExpression, template, var.accessModifier);
	                		
	                		return new ReturnOfFindVarDecl(oldTypeIndex2, var, varUse);
	                	}
	                	
	                	// separator가 =나 ,이 아닌 ;나 )일 경우는 null을 리턴
		                //return null;
	                	return new ReturnOfFindVarDecl(null, var, varUse);
            		}
        		} // if (i!=endIndex+1) {
        		
        	} // if (findVarParams.typeStartIndex != -1) { // 타입이면
        	
        	else if (strType==null ||
        			CompilerHelper.IsSeparator(strType) || 
        			strType.equals("new") || strType.equals("else") || 
        			strType.equals("throw") || strType.equals("package") || strType.equals("import") ||
        			strType.equals("throws") ||
        			strType.equals("return") || strType.equals("continue") || strType.equals("break") ||
        			strType.equals("extends") || strType.equals("implements") ||
        			strType.equals("instanceof") ||
        			IsAccessModifier(strType) ) 
        	{ // 변수의 사용, if (findVarParams.typeStartIndex != -1) {
        		
        		CodeString varName = src.getItem(varNameIndex.index());
        		if (varName.equals("long")) {
        			int debug;
        			debug=0;
        			debug++;
        			
        		}
        		if (index==329) {
        			int a;
        			a=0;
        			a++;
        		}
        		//if (this.IsKeyword(varName)) return null;
        		
        		FindVarUseParams findVarUseParams = new FindVarUseParams(null);
        		
        		int nextIndex = 
        				this.findVarUseName(src, findVarUseParams, index);
        		
        		if (nextIndex==-1) return null;
        		
        		
        		//mlistOfAllVarUses.add(findVarUseParams);
        		//if (mlistOfAllVarUsesHashed!=null)
        		//	mlistOfAllVarUsesHashed.input(findVarUseParams);
        		this.putVarUse(findVarUseParams, modeAllOrUpdate);
        		findVarUseParams.originName = src.getItem(findVarUseParams.index()).str;
        		
        		if (strType!=null && strType.equals(".")) {
        			if (varName.equals("changeBounds")) {
            			int debug;
            			debug=0;
            			debug++;
            			
            		}
        			
	        		int foundParent = this.findParentOfVarUse(src, typeIndex, findVarUseParams);
	        		if (foundParent==-1) return new ReturnOfFindVarDecl(null, null, findVarUseParams);
	        		else if (foundParent==0) {
	        			
	        		}
        		}  // if (strType.equals(".")) { 
        		
        		boolean isExpressionElement = false;
        		if (oldTypeIndex!=null && oldTypeIndex.hasExpression() &&
    					oldTypeIndex.startIndexOfExpression()<=varNameIndex.index() && 
    					varNameIndex.index()<=oldTypeIndex.endIndexOfExpression())
        			isExpressionElement = true;
        		
        		// int a=0, b=1과 같이 varName이 0일 경우
        		// nextIndex 는 변수이름의 공백등을 제외한 다음 인덱스
        		if (src.getItem(nextIndex).equals(",")) { // 다음이 ,이면 수식의 인덱스들을 초기화
    				if (oldTypeIndex!=null) {
    					oldTypeIndex.startIndexOfExpression = null;
    					oldTypeIndex.endIndexOfExpression = null;
    				}
    			}
        		
        		if (isExpressionElement) {
        			return new ReturnOfFindVarDecl(oldTypeIndex, null, findVarUseParams);
        		}
        		
        		return new ReturnOfFindVarDecl(oldTypeIndex, null, findVarUseParams);
        		
        	} // else if (IsSeparator(strType) || strType.equals("return") || strType.equals("else") || strType.equals("throw"))
       // } // if (IsIdentifier(cstr))   { // 식별자 이면
        
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
		}
		return null;
	}
	
	int findVarUseName(HighArray_CodeString src, FindVarUseParams findVarUseParams, int index) {
		boolean isVarOrFuncCall = true;
		// index는 변수이름의 인덱스
		int nextIndex = SkipBlank(src, false, index+1, src.count-1);
		boolean isArrayElement=false;
		int startIndexArray=-1, endIndexArray=-1;
		
		int nextIndexBackup = nextIndex;
		if (nextIndex!=src.count) {
			
			CodeString string = src.getItem(nextIndex);
			
			if (string.equals("<")) {
				// stack = new Stack<Block>();에서 nextIndex은 템플릿의 왼쪽 <이 된다.
				Template t = CompilerHelper.getTemplate(this, nextIndex, 0);
				if (t!=null) {
					findVarUseParams.template = t;
					// 위의 예에서 nextIndex는 (의 인덱스가 된다.
					nextIndex = SkipBlank(src, false, t.indexRightPair()+1, src.count-1);
					//if (nextIndex==src.count) return null;
					string = src.getItem(nextIndex);
				}
			}
			if (string.equals("[")) {
				// 배열타입선언인지 아니면 배열원소사용인지 확인한다.
				int rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", nextIndex, src.count-1, false);
				if (rightPair==-1) return -1;
				startIndexArray = nextIndex;
				
				while(true) {
					nextIndex = SkipBlank(src, false, rightPair+1, src.count-1);
					if (nextIndex==src.count) return -1;
					if (src.getItem(nextIndex).equals("[")==false) {
						endIndexArray = nextIndex-1;
						break;
					}
					rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", nextIndex, src.count-1, false);
					if (rightPair==-1) break;
				}
				// 배열 원소 참조인지 아니면 배열타입선언인지 확인한다.
				isArrayElement = CompilerHelper.isArrayElement(this, startIndexArray, endIndexArray);
				
			}
			else if (string.equals("(")) { // 함수 호출, 함수 정의
				isVarOrFuncCall = false;
				int rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", nextIndex, src.count-1, false);
				if (rightPair==-1) {
					
				}
				nextIndex = rightPair;
				nextIndex = SkipBlank(src, false, nextIndex+1, src.count-1);
				
				//Class c = (new ScrollBars_test4().toObjectArray())[0].getClass();에서
				// varUse는 toObjectArray이다.
				while (true) {
    				CodeString string2 = src.getItem(nextIndex);
        			if (string2.equals("[")) {
        				// 배열타입선언인지 아니면 배열원소사용인지 확인한다.
        				int rightPair2 = CompilerHelper.CheckParenthesis(src, "[", "]", nextIndex, src.count-1, false);
        				if (rightPair2==-1) return -1;
        				startIndexArray = nextIndex;
        				
        				while(true) {
        					nextIndex = SkipBlank(src, false, rightPair2+1, src.count-1);
        					if (nextIndex==src.count) return -1;
        					if (src.getItem(nextIndex).equals("[")==false) {
        						endIndexArray = nextIndex-1;
        						break;
        					}
        					rightPair2 = CompilerHelper.CheckParenthesis(src, "[", "]", nextIndex, src.count-1, false);
        					if (rightPair2==-1) break;
        				}
        				// 배열 원소 참조인지 아니면 배열타입선언인지 확인한다.
        				isArrayElement = CompilerHelper.isArrayElement(this, startIndexArray, endIndexArray);
        				break;
        			}
        			else if (string2.equals("(")) { // 함수 호출, 함수 정의
        				isVarOrFuncCall = false;
        				int rightPair2 = CompilerHelper.CheckParenthesis(src, "(", ")", nextIndex, src.count-1, false);
    					if (rightPair2==-1) break;
    					nextIndex = rightPair2;
        			}
        			else if (string2.equals(")")) { // fake 괄호
        				
        			}
        			else {
        				break;
        			}
        			nextIndex = SkipBlank(src, false, nextIndex+1, src.count-1);
    			}//while (true) {
			}//else if (string.equals("(")) { // 함수 호출, 함수 정의
			
		}//if (nextIndex!=src.count) {
		
		nextIndex = nextIndexBackup;
		
		            		
		findVarUseParams.index = IndexForHighArray.indexRelative(findVarUseParams, src, index);
		findVarUseParams.isForVarOrForFunc = isVarOrFuncCall;
		findVarUseParams.isArrayElement = isArrayElement;
		if (!isArrayElement) {
			findVarUseParams.name = src.getItem(findVarUseParams.index()).str;
		}
		else { // varUse가 배열원소일 경우 buffer[i]에서 varUse의 index는 buffer를 가리키고
			// name만 buffer[i]를 갖게된다. 참고로 varUse들은 buffer, [, i, ] 이다.
			int arrIndex;
			findVarUseParams.name = src.getItem(findVarUseParams.index()).str;
			for (arrIndex=startIndexArray; arrIndex<=endIndexArray; arrIndex++) {
				CodeString str = src.getItem(arrIndex);
				if (CompilerHelper.IsBlank(str) || CompilerHelper.IsComment(str)) continue;
				findVarUseParams.name = findVarUseParams.name.concat(str.str);
			}
		}
		return nextIndex;
	}
	
	boolean hasAccessModifier(FindVarParams var, FindFunctionParams func) {
		if (var!=null) {
			if (var.accessModifier!=null) {
				if (var.accessModifier.isFinal) return true;
				if (var.accessModifier.isStatic) return true;
				if (var.accessModifier.accessPermission!=null) return true;
			}
			else {
				return false;
			}
		}
		else if (func!=null) {
			if (func.accessModifier!=null) {
				if (func.accessModifier.isFinal) return true;
				if (func.accessModifier.isStatic) return true;
				if (func.accessModifier.accessPermission!=null) return true;
			}
			else {
				return false;
			}
		}
		return false;
	}
	
	boolean hasAccessModifierExceptFinal(FindVarParams var, FindFunctionParams func) {
		if (var!=null) {
			if (var.accessModifier!=null) {
				if (var.accessModifier.isFinal) return false;
				if (var.accessModifier.isStatic) return true;
				if (var.accessModifier.accessPermission!=null) return true;
			}
			else {
				return false;
			}
		}
		else if (func!=null) {
			if (func.accessModifier!=null) {
				if (func.accessModifier.isFinal) return true;
				if (func.accessModifier.isStatic) return true;
				if (func.accessModifier.accessPermission!=null) return true;
			}
			else {
				return false;
			}
		}
		return false;
	}
	
	/** FindVarDeclarationsAndVarUses호출에서 typeIndex가 '.'일 경우 findVarUseParams의 parent를 찾는다. 
	 * 에러가 있으면 -1를 리턴, 못찾으면 0을 리턴, 찾으면 1을 리턴*/
	int findParentOfVarUse(HighArray_CodeString src, int typeIndex, FindVarUseParams findVarUseParams) {
		if (findVarUseParams.index()==561) {
			int a;
			a=0;
			a++;
		}
		int parentIndex =  SkipBlank(src, true, 0, typeIndex - 1);
		boolean found = false;
		if (parentIndex!=-1) {
			CodeString parent = src.getItem(parentIndex);
			int m;
			if (parent.equals("]")) { 
				// parent 가 배열원소일 경우, buffer[i].equals()에서 equals의 parent는 buffer이다.
				int leftPair = CompilerHelper.CheckParenthesis(src, "[", "]", 0, parentIndex, true);
				if (leftPair==-1) {
					errors.add(new Error(this, parentIndex,parentIndex,"] not paired."));
					return -1;
				}
				int indexParent = this.SkipBlank(src, true, 0, leftPair-1);
				// (new ScrollBars_test4().toObjectArray())[0].getClass();
				while (true) { 
					CodeString str = src.getItem(indexParent);
					if (str.equals(")")) {
						int lp = CompilerHelper.CheckParenthesis(src, "(", ")", 0, indexParent, true);
						if (lp==-1) {
							
						}
						else {
							int idIndex = this.SkipBlank(src, true, 0, lp-1);
							CodeString id = src.getItem(idIndex);
							if (IsIdentifier(id)==false) {// fake괄호 제거
							}
							else { // 함수호출, 위에서 toObjectArray
								indexParent = idIndex;
								break;
							}
						}
					}
					else {
						break;
					}
					indexParent = this.SkipBlank(src, true, 0, indexParent-1);
				}//while (true) { 
				for (m=mlistOfAllVarUses.getCount()-1; m>=0; m--) {
					FindVarUseParams use = (FindVarUseParams)mlistOfAllVarUses.getItem(m);
					if (use.index()==indexParent) {
						findVarUseParams.parent = use;
						use.child = findVarUseParams;
						found = true;
						break;
					}
				}
			}
			else if (parent.equals(")")==false) {  // 이전 varUse가 일반변수일 경우
				for (m=mlistOfAllVarUses.getCount()-1; m>=0; m--) {
					FindVarUseParams use = (FindVarUseParams)mlistOfAllVarUses.getItem(m);
					if (use.index()==parentIndex) {
						findVarUseParams.parent = use;
						use.child = findVarUseParams;
						found = true;
						break;
					}
				}
			}
			else if (parent.equals(")")) {
				int leftPair;
				int backupIndex = parentIndex;
				
				
				int backup1 = backupIndex;
				while ( src.getItem(parentIndex).equals(")") ) {
					leftPair = CompilerHelper.CheckParenthesis(src, "(", ")", 0, parentIndex, true);
					if (leftPair!=-1) {
						parentIndex = SkipBlank(src, true, 0, leftPair - 1);
						if (parentIndex!=-1) {
							CodeString parent2 = src.getItem(parentIndex);
							if ( IsIdentifier( parent2 ) ) { 
								// 이전 varUse가 괄호로 싸여진 함수호출일 경우
    							for (m=mlistOfAllVarUses.getCount()-1; m>=0; m--) {
    	        					FindVarUseParams use = (FindVarUseParams)mlistOfAllVarUses.getItem(m);
    	        					if (use.index()==parentIndex) {
    	        						findVarUseParams.parent = use;
    	        						use.child = findVarUseParams;
    	        						found = true;
    	        						break;
    	        					}
    	        				}
    							if (found) break;
							}
							else {
								parentIndex = SkipBlank(src, true, 0, backup1 - 1);
								backup1 = parentIndex;
							}
						}
						else {
							break;
						}
					} // if (leftPair!=-1) {
					else {
						break;
					}
				} // while(true)*/
							
				if (found==false) {
					leftPair = CompilerHelper.CheckParenthesis(src, "(", ")", 0, backupIndex, true);
					if (leftPair!=-1) {
						parentIndex = SkipBlank(src, true, 0, leftPair - 1);
						if (parentIndex!=-1) {
							CodeString parent2 = src.getItem(parentIndex);
							if ( IsIdentifier( parent2 ) ) { 
								// 이전 varUse가 함수호출일 경우
    							for (m=mlistOfAllVarUses.getCount()-1; m>=0; m--) {
    	        					FindVarUseParams use = (FindVarUseParams)mlistOfAllVarUses.getItem(m);
    	        					if (use.index()==parentIndex) {
    	        						findVarUseParams.parent = use;
    	        						use.child = findVarUseParams;
    	        						found = true;
    	        						break;
    	        					}
    	        				}
							}
						}
					}
				}
				
				if (found==false) {
					int backup2 = backupIndex;
					// ((Button)(controls[0])).changeBounds(boundsOfButtonOK);
					while (true) {
						parentIndex = SkipBlank(src, true, 0, backup2 - 1);
						if (parentIndex!=-1) {
							CodeString parent2 = src.getItem(parentIndex);
							if ( IsIdentifier( parent2 ) ) { 
								// 이전 varUse가 괄호에 싸인 일반변수일 경우
    							for (m=mlistOfAllVarUses.getCount()-1; m>=0; m--) {
    	        					FindVarUseParams use = (FindVarUseParams)mlistOfAllVarUses.getItem(m);
    	        					if (use.index()==parentIndex) {
    	        						findVarUseParams.parent = use;
    	        						use.child = findVarUseParams;
    	        						found = true;
    	        						break;
    	        					}
    	        				}
    							if (found) break;
							}
							else if (parent2.equals("]")) { 
	        					// 건너뛰기
	        					leftPair = CompilerHelper.CheckParenthesis(src, "[", "]", 0, parentIndex, true);
	        					if (leftPair==-1) {
	        						errors.add(new Error(this, parentIndex,parentIndex,"] not paired."));
	        						return -1;
	        					}
	        					backup2 = leftPair;
	        				}
							else if (parent2.equals(")")) {
								backup2 = parentIndex;
							}
							else {
								errors.add(new Error(this, parentIndex, parentIndex, "invalid token : "+parent2));
								return -1;
							}
						}
						else { // if (parentIndex==-1) {
							return -1;
						}
					} // while (true) {
				} // if (found==false) {
			} // else if (parent.equals(")")) {
		} // if (parentIndex!=-1) {
		if (found) return 1;
		return 0;
	}
	
	boolean CheckControlBody_case(HighArray_CodeString src, 
			CategoryOfControls category, FindControlBlockParams findControlParams, 
			int startIndex, int endIndex, int i, ModeAllOrUpdate modeAllOrUpdate) {
		if (i==8985) {
			int a;
			a=0;
			a++;
		}
		if (category==null) return false;
		
		findControlParams.nameIndex = IndexForHighArray.indexRelative(findControlParams, src, i);		
		findControlParams.catOfControls = category;
		findControlParams.startIndex = IndexForHighArray.indexRelative(findControlParams, src, i);
		
		boolean noError=true;
		int indexOfNext = 0;
		
		if (category.category==CategoryOfControls.Control_case) {
			findControlParams.indexOfLeftParenthesis = IndexForHighArray.indexRelative(findControlParams, src, i+1);	        
	        
	        findControlParams.indexOfRightParenthesis =	        		
	        		IndexForHighArray.indexRelative(findControlParams, src, Skip(src, false, ":", i+1, endIndex));
	        
	        if (findControlParams.indexOfRightParenthesis() == endIndex+1) {
				try {
					throw new Exception(":가 없습니다.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					return false;
				} 
	        }
	        //findControlParams.indexOfRightParenthesis = Skip(src, false, ")", findControlParams.indexOfLeftParenthesis+1, endIndex);
	        
	        // if, else if, while등은 ) 다음부터, case는 ":" 다음
	        indexOfNext = findControlParams.indexOfRightParenthesis() + 1;
		}
		
		if (noError) {
        	int separator = SkipBlank(src, false, indexOfNext, endIndex); // 공백 스킵
        	if (separator==endIndex+1)
        		return false;
        	
        	int startParenthesis = separator;
            int endParenthesis = -1;
        			
        	CodeString leftParent = src.getItem(separator);
        	if (leftParent.equals("{")==false) { // block이 아닌 제어문이거나 error
        		// error
        		//errors.add(new Error(this, separator, separator, packageName));
        		int nextCaseIndex = Skip(src, false, "case", i+1, endIndex);
        		endParenthesis = nextCaseIndex-1;        		
        		//findControlParams.endIndex = endParenthesis;
        		findControlParams.isBlock = false;
        	}
        	else {	// block
        		endParenthesis = CompilerHelper.CheckParenthesis(src, "{", "}", startParenthesis, src.count-1, false);
        		//findControlParams.endIndex = endParenthesis;
        		findControlParams.isBlock = true;
        	}
        	
        	FindBlockParams findBlockParams = new FindBlockParams(this, startParenthesis, endParenthesis);
        	
        	
        	findControlParams.found = true;
        	findBlockParams.categoryOfBlock = new CategoryOfBlock(0, category);
        	findControlParams.findBlockParams = findBlockParams;
        	
        	// 조건문을 연결
        	// findControlParams.indexOfRightParenthesis는 case문의 경우 ':'이다.
        	if (findControlParams.indexOfLeftParenthesis()!=-1 && findControlParams.indexOfRightParenthesis()!=-1) {
            	ArrayListChar list = new ArrayListChar(50);
            	int start = findControlParams.indexOfLeftParenthesis();
            	int end = findControlParams.indexOfRightParenthesis();
            	for (i=start; i<end; i++) {
            		CodeString str = src.getItem(i);
            		if (CompilerHelper.IsBlank(str)) continue;
            		list.add(str.str);
            	}
            	findControlParams.findBlockParams.blockName = new String(list.getItems());
        	}
        	else {
        		findControlParams.findBlockParams.blockName = "";
        	}
        	
        	if (findControlParams.isBlock) {
        		//mlistOfBlocks.add(findBlockParams)
        		putFindBlockParams(findBlockParams, modeAllOrUpdate);
        	}
        	return true;
            
        }
        return false;
	}
	
	/** i(nameIndex)부터 startIndex, endIndex사이에서 ()와 {}이 제대로 있는지를 확인한다.
	 * ) 다음에 { 이 아니면 block이 아닌 제어문이거나 error이고, 
	 * { 이면 쌍이 되는 }을 찾아 block임을 확인한다.
	 * @param i : if, else, else if, while 등의 index*/
	boolean CheckControlBody(HighArray_CodeString src, 
			CategoryOfControls category, FindControlBlockParams findControlParams, 
			int startIndex, int endIndex, int i, ModeAllOrUpdate modeAllOrUpdate) {
		if (category==null) return false;
		
		if (category.category==CategoryOfControls.Control_case) {
			return CheckControlBody_case(src, category, findControlParams, startIndex, endIndex, i, modeAllOrUpdate);
		}
		
		findControlParams.nameIndex = IndexForHighArray.indexRelative(findControlParams, src, i);		
		findControlParams.catOfControls = category;
		findControlParams.startIndex = IndexForHighArray.indexRelative(findControlParams, src, i);
		
		boolean noError=true;
		int indexOfNext;
		int indexOfEndOfdowhile=-1;
        
		if (category.category==CategoryOfControls.Control_dowhile) {
			// else는 else 다음부터
			indexOfNext = i+1;
			
			int indexOfWhile = -1;
			int leftPair = -1;
			int rightPair = -1;
			int semicolon = -1;
			
			int indexOfMiddleLeftPair = SkipBlank(src, false, i+1, endIndex);
			int indexOfMiddleRightPair = CompilerHelper.CheckParenthesis(src, "{", "}", indexOfMiddleLeftPair, endIndex, false);
			
			
			//while (true) {
				indexOfWhile = SkipBlank(src, false, indexOfMiddleRightPair+1, endIndex);
				leftPair = SkipBlank(src, false, indexOfWhile+1, endIndex);
				
				if (src.getItem(leftPair).equals("(")==false) {
					try {
						throw new Exception("(가 없습니다.");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						return false;
					} 
		        }
			
				rightPair =
						CompilerHelper.CheckParenthesis(src, "(", ")", leftPair, endIndex, false);
				semicolon = SkipBlank(src, false, rightPair+1, endIndex);
				
				CodeString strSemicolon = src.getItem(semicolon); 
				if (strSemicolon.equals(";")) {
					findControlParams.indexOfLeftParenthesis = IndexForHighArray.indexRelative(findControlParams, src, leftPair);
					findControlParams.indexOfRightParenthesis = IndexForHighArray.indexRelative(findControlParams, src, rightPair);
					findControlParams.endIndex = IndexForHighArray.indexRelative(findControlParams, src, semicolon);
					indexOfEndOfdowhile = semicolon;
					// findBlockParams은 아랫부분에서 만들어 endIndex를 넣는다.
				}
			//}
			
	        
	       
		}
		else if (category.category!=CategoryOfControls.Control_else) {
			if (category.category!=CategoryOfControls.Control_else && 
					category.category!=CategoryOfControls.Control_elseif) {
				// if, for, while 등
				findControlParams.indexOfLeftParenthesis = IndexForHighArray.indexRelative(findControlParams, src, this.SkipBlank(src, false, i+1, endIndex));
			}
			else if (category.category==CategoryOfControls.Control_elseif) {
				int indexOfIf = this.SkipBlank(src, false, i+1, endIndex);
				findControlParams.indexOfLeftParenthesis = IndexForHighArray.indexRelative(findControlParams, src, this.SkipBlank(src, false, indexOfIf+1, endIndex));
			}
			//findControlParams.indexOfLeftParenthesis = Skip(src, false, "(", i+1, endIndex);
	        if (findControlParams.indexOfLeftParenthesis() == endIndex+1) {
				try {
					throw new Exception("(가 없습니다.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					return false;
				} 
	        }
	        
	        
	        
	        
	        findControlParams.indexOfRightParenthesis =	 IndexForHighArray.indexRelative(findControlParams, src,        		
	        		CompilerHelper.CheckParenthesis(src, "(", ")", findControlParams.indexOfLeftParenthesis(), endIndex, false));
	        //findControlParams.indexOfRightParenthesis = Skip(src, false, ")", findControlParams.indexOfLeftParenthesis+1, endIndex);
	        
	        // if, else if, while등은 ) 다음부터
	        indexOfNext = findControlParams.indexOfRightParenthesis() + 1;
		}
		else {
			// else는 else 다음부터
			indexOfNext = i+1;
		}
        
		if (noError) {
        	int separator = SkipBlank(src, false, indexOfNext, endIndex); // 공백 스킵
        	if (separator==endIndex+1)
        		return false;
        	
        	int startParenthesis = separator;
            int endParenthesis = -1;
        			
        	CodeString leftParent = src.getItem(startParenthesis);
        	if (leftParent.equals("{")==false) { // block이 아닌 제어문이거나 error
        		// error
        		//errors.add(new Error(this, separator, separator, packageName));
        		findControlParams.isBlock = false;
        		findControlParams.found = true;
        		return true;
        		    		
        	}
        	else {	// block
        		
        		findControlParams.isBlock = true;
        		FindBlockParams findBlockParams = new FindBlockParams(this, startParenthesis, endParenthesis);
            	if (category.category!=CategoryOfControls.Control_dowhile) {
            		endParenthesis = CompilerHelper.CheckParenthesis(src, "{", "}", startParenthesis, src.count-1, false);
            		findControlParams.endIndex = IndexForHighArray.indexRelative(findControlParams, src, endParenthesis);
            		findBlockParams.endIndex = IndexForHighArray.indexRelative(findBlockParams, src, endParenthesis);
            	}
            	else { // do while
            		findBlockParams.startIndex = IndexForHighArray.indexRelative(findBlockParams, src, i);	        		
            		findBlockParams.endIndex = IndexForHighArray.indexRelative(findBlockParams, src, indexOfEndOfdowhile);
            		findControlParams.startIndex = IndexForHighArray.indexRelative(findControlParams, src, i);
            		findControlParams.endIndex = IndexForHighArray.indexRelative(findControlParams, src, indexOfEndOfdowhile);
            	}
            	findControlParams.found = true;
            	findBlockParams.categoryOfBlock = new CategoryOfBlock(0, category);
            	findControlParams.findBlockParams = findBlockParams;
            	
            	// 조건문을 연결
            	if (findControlParams.indexOfLeftParenthesis()!=-1 && findControlParams.indexOfRightParenthesis()!=-1) {
	            	ArrayListChar list = new ArrayListChar(50);
	            	int start = findControlParams.indexOfLeftParenthesis();
	            	int end = findControlParams.indexOfRightParenthesis();
	            	for (i=start; i<=end; i++) {
	            		list.add(src.getItem(i).str);
	            	}
	            	findControlParams.findBlockParams.blockName = new String(list.getItems());
            	}
            	else {
            		findControlParams.findBlockParams.blockName = "";
            	}
            	
            	//mlistOfBlocks.add(findBlockParams);
            	this.putFindBlockParams(findBlockParams, modeAllOrUpdate);
            	return true;
        		
        	}
            
        }
        return false;
	}
	
	/** i(functionNameIndex)부터 ()와 {}이 제대로 있는지를 확인한다. 함수정의인지를 확인한다.*/
	boolean CheckBody(HighArray_CodeString src, int i) {
		
		int functionNameIndex = i;
		
		int startIndexOfName = getFullNameIndex0(src, true, i);
		int indexOfnew = SkipBlank(src, true, 0, startIndexOfName);
		if (indexOfnew>=0 && src.getItem(indexOfnew).equals("new")) {
			return false;
		}
        
        int indexOfLeftParenthesis = SkipBlank(src, false, i+1, src.count-1);
        if (indexOfLeftParenthesis == src.count) {
        	return false;
        }
        if (src.getItem(indexOfLeftParenthesis).equals("(")==false) return false;
               
        //findFunctionParams.indexOfRightParenthesis = Skip(src, false, ")", findFunctionParams.indexOfLeftParenthesis+1, endIndex);
        int indexOfRightParenthesis =	        		
        		CompilerHelper.CheckParenthesis(src, "(", ")", indexOfLeftParenthesis, src.count-1, false);
        if (indexOfRightParenthesis == -1) {
        	return false;
        }
        int separator = SkipBlank(src, false, indexOfRightParenthesis + 1, src.count-1); // 공백 스킵
    	int indexOfSeparator=separator;
    	if (separator==src.count) {
    		return false;        		
    	}
    	//if (showsError==false) return false;
		CodeString strSeparator = src.getItem(separator);
		int index=separator;
		if (strSeparator.equals("throws")) {
    		while (true) {
        		index = SkipBlank(src, false, index+1, src.count-1);
                if (index==src.count) break;
                index = this.getFullNameIndex0(src, false, index);
                //index = getFullNames(src, "throws", null, findFunctionParams, index);
                if (index!=-1) {
                	index = this.SkipBlank(src, false, index+1, src.count-1);
                	CodeString codeStr = src.getItem(index);
                	if (codeStr.equals("{")) {
                		i = index;
                		break;
                	}
                	else if (codeStr.equals(",")) {
                	}
                	else {
                		errors.add(new Error(this, index, index, "\"{\" or \",\" not exist"));
                		break;
                	}
                }
                else {	// '{'이 없을 때
                	index = separator;
                	break;
                }
    		}
    	}
		indexOfSeparator = index;
		
		strSeparator = src.getItem(indexOfSeparator);
		if (strSeparator.equals("{")==false) {
			return false;
		}
    	
    	
    	// 반드시 ) 다음에는 { 이다.	
        return true;

	}
	
	/** i(functionNameIndex())부터 startIndex, endIndex사이에서 ()와 {}이 제대로 있는지를 확인한다.
	 * @param checkType : 0(함수,클래스), 1(생성자)이면 블록 시작({)이 없는 에러를 확인하고(함수, 클래스, 생성자 정의), 
     * 		2이면 시작괄호에러를 확인하지 않는다.*/
	boolean CheckBody(HighArray_CodeString src, FindFunctionParams findFunctionParams, 
			int startIndex, int endIndex, int i) {
		findFunctionParams.functionNameIndex = IndexForHighArray.indexRelative(findFunctionParams, src, i);
		
		if (i==2073) {
			int a;
			a=0;
			a++;
		}
        
        //i = SkipBlank(src, false, i + 1, endIndex); // 공백 스킵
        
        findFunctionParams.indexOfLeftParenthesis = IndexForHighArray.indexRelative(findFunctionParams, src, SkipBlank(src, false, i + 1, endIndex));
        if (findFunctionParams.indexOfLeftParenthesis() == endIndex+1) {
			//errors.add(new Error(this, findFunctionParams.indexOfLeftParenthesis, findFunctionParams.indexOfLeftParenthesis, "( not exists."));
			return false;
        }
        if (src.getItem(findFunctionParams.indexOfLeftParenthesis()).equals("(")==false) {
        	//errors.add(new Error(this, findFunctionParams.indexOfLeftParenthesis, findFunctionParams.indexOfLeftParenthesis, "( not exists."));
			return false;
        }
               
        //findFunctionParams.indexOfRightParenthesis = Skip(src, false, ")", findFunctionParams.indexOfLeftParenthesis+1, endIndex);
        findFunctionParams.indexOfRightParenthesis = IndexForHighArray.indexRelative(findFunctionParams, src, 	        		
        		CompilerHelper.CheckParenthesis(src, "(", ")", findFunctionParams.indexOfLeftParenthesis(), endIndex, false));
        if (findFunctionParams.indexOfRightParenthesis() == -1) {
        	errors.add(new Error(this, findFunctionParams.indexOfRightParenthesis(), findFunctionParams.indexOfRightParenthesis(), ") not exists."));
			return false;
        }
        else {
        	
        	
        	int separator = SkipBlank(src, false, findFunctionParams.indexOfRightParenthesis() + 1, endIndex); // 공백 스킵
        	int indexOfSeparator=separator;
        	if (separator==endIndex+1) {
        		return false;        		
        	}
        	else {
        		//if (showsError==false) return false;
        		CodeString strSeparator = src.getItem(separator);
        		int index=separator;
        		if (strSeparator.equals("throws")) {
            		while (true) {
                		index = SkipBlank(src, false, index+1, endIndex);
                        if (index==endIndex+1) break;
                        index = getFullNames(src, "throws", null, findFunctionParams, index);
                        if (index!=-1) {
                        	CodeString codeStr = src.getItem(index);
                        	if (codeStr.equals("{")) {
                        		i = index;
                        		break;
                        	}
                        	else if (codeStr.equals(",")) {
                        	}
                        	else if (codeStr.equals(";")) {
                        		break;
                        	}
                        }
                        else {	// '{'이 없을 때
                        	index = separator;
                        	break;
                        }
            		}
            	}
        		indexOfSeparator = index;
        		
        		strSeparator = src.getItem(indexOfSeparator);
        		if (strSeparator.equals(";")) {
        			if (findFunctionParams.hasReturnType()) {// 함수foward선언
        				findFunctionParams.isInterfaceMethod = true;
        			}
        			else { // 함수호출
        				return false;
        			}
        		}
        		else if (strSeparator.equals("{")==false) {// 함수호출
        			return false;
        		}
        		
        		// 함수 정의
        	}
        	
        	
        	// 반드시 ) 다음에는 { 이다.
        	int startParenthesis = indexOfSeparator;
            int endParenthesis = -1;
            FindBlockParams findBlockParams = new FindBlockParams(this, startParenthesis, endParenthesis);
            
            int k = -1;
            ReturnOfFindAccessModifier r;
            
            if (findFunctionParams.hasReturnType()) { // 일반함수
            	r = FindAccessModifier(src, startIndex, findFunctionParams.returnTypeStartIndex() - 1, findFunctionParams.accessModifier);
            }
            else { // 생성자
            	r = FindAccessModifier(src, startIndex, findFunctionParams.functionNameIndex() - 1, findFunctionParams.accessModifier);
            }
            k = r.r;
            
            
            boolean hasAccess = hasAccessModifier(null, findFunctionParams);
            if (findBlockParams.startIndex()!=-1/* && findBlockParams.endIndex!=-1*/) {
            	if (hasAccess) findFunctionParams.startIndex = IndexForHighArray.indexRelative(findFunctionParams, src, k);
            	//else findFunctionParams.startIndex = -1;
            	else findFunctionParams.startIndex = IndexForHighArray.indexRelative(findFunctionParams, src, k);
            	findFunctionParams.startIndex = IndexForHighArray.indexRelative(findFunctionParams, src, 
            			this.getIndexWhickRemoveLeftBlankAndCommentAndAnnotation(src, r));
            	//findFunctionParams.endIndex = findBlockParams.endIndex;
            	findFunctionParams.found = true;
            	findFunctionParams.findBlockParams = findBlockParams;
            	findFunctionParams.findBlockParams.blockName = src.getItem(findFunctionParams.functionNameIndex()).str;
            	
            	findBlockParams.categoryOfBlock = new CategoryOfBlock(CategoryOfBlock.Function, null);
            	mlistOfBlocks.add(findBlockParams);
            	
            	
            	int docuIndex;
            	if (hasAccess) docuIndex = k;
            	else {
            		if (findFunctionParams.hasReturnType()) { // 일반함수
            			docuIndex = findFunctionParams.returnTypeStartIndex() - 1;
            		}
            		else { //생성자
            			docuIndex = findFunctionParams.functionNameIndex() - 1;
            		}
            	}
            	int indexOfDocuEnd = SkipOnlyBlankAndAnnotationAndRegularComment(src, true, startIndex, docuIndex); // 공백 스킵
            	DocuComment docu = 
            			FindDocuComment(src, /*true, findFunctionParams, null,*/ 0, indexOfDocuEnd);
            	findFunctionParams.docuComment = docu;
            	
            	return true;
            }
        }   
        return false;

	}
	
	/** index부터 검색하여 다큐주석이 아닌 일반주석, 애노테이션을 제거한 인덱스를 리턴한다.*/
	int getIndexWhickRemoveLeftBlankAndCommentAndAnnotation(HighArray_CodeString src, ReturnOfFindAccessModifier r) {
		int i;
		int index;
		index = r.r;
		if (index<0) return index;
		if (r.type==0) {
			index = r.r+1;
		}
		for (i=index; i<src.count; i++) {
			CodeString str=null;
			try{
			str = src.getItem(i);
			}catch(Exception e) {
				e.printStackTrace();
				int a;
				a=0;
				a++;
			}
			if (CompilerHelper.IsRegularComment(str)) continue;
			else if (CompilerHelper.IsAnnotation(str)) continue;
			else if (CompilerHelper.IsBlank(str)) continue;
			else return i;
		}
		return index;
	}
	
	
	
	/** Compiler.getVarUseWithIndex의 리턴타입, 
	 * getVarUseWithIndex의 startIndex()를 활용하면 검색을 효율적으로 할 수 있다.*/
	static class GetVarUseWithIndexReturnType {
		FindVarUseParams r;
		/** r이 null이 아닌경우 mlistOfAllVarUses상에서의 인덱스*/
		int indexInlistOfAllVarUses;
		GetVarUseWithIndexReturnType(FindVarUseParams varUse, int indexInlistOfAllVarUses) {
			this.r = varUse;
			this.indexInlistOfAllVarUses = indexInlistOfAllVarUses;
		}
	}
	
	/** @param startIndex() : listOfAllVarUses 상에서 검색 시작 인덱스*/
	GetVarUseWithIndexReturnType getVarUseWithIndex(HighArray<FindVarUseParams> listOfAllVarUses, int startIndex, int indexOfVarUseInMBuffer) {
		int i;
		int len = listOfAllVarUses.getCount();
		for (i=startIndex; i<len; i++) {
			FindVarUseParams varUse = (FindVarUseParams)listOfAllVarUses.getItem(i);
			if (varUse.index()==indexOfVarUseInMBuffer) {
				return new GetVarUseWithIndexReturnType(varUse, i);
			}
			else if (varUse.index()>indexOfVarUseInMBuffer) return null;
		}
		return null;
	}
	
	/** mlistOfAllVarUses의 해시리스트에서 varUseName과 indexOfVarUseInMBuffer으로 해당 varUse를 검색한다.
	 * @return : varUse가 검색되지않으면 null을 리턴, 그렇지 않으면 varUse 리턴 */
	FindVarUseParams getVarUseWithIndex(Hashtable2 hashListOfAllVarUses, String varUseName, int indexOfVarUseInMBuffer) {
		int i;
		FindVarUseParams varUse = hashListOfAllVarUses.getData(varUseName, indexOfVarUseInMBuffer);
		if (varUse==null) return null;
		return varUse;
	}
	
	CodeStringEx getTypeOfExpression(HighArray_CodeString src, FindExpressionParams expression) {
		FindFuncCallParam funcCall = 
			new FindFuncCallParam(this, expression.startIndex(), expression.endIndex());
		CodeStringEx type = getTypeOfExpression(src, funcCall);
		return type;
	}
	
	ArrayListCodeString toArrayListCodeString(CodeStringEx[] arr) {
		ArrayListCodeString r = new ArrayListCodeString(arr.length);
		for (int i=0; i<r.count; i++) {
			r.add(arr[i]);
		}
		return r;
	}
	
	/** setOnTouchListener(new View.OnTouchListener() {}); 에서 View.OnTouchListener()는 
		 * 이벤트 핸들러 클래스이므로 {의 인덱스를 리턴한다.*/
	int isEventHandlerClass(HighArray_CodeString src, FindFuncCallParam funcCall) {
		int i;
		for (i=funcCall.startIndex(); i<=funcCall.endIndex(); i++) {
			CodeString str = src.getItem(i);
			if (CompilerHelper.IsBlank(str) || CompilerHelper.IsComment(str)) continue;
			if (str.equals("{")) {
				return i;
			}
		}
		return -1;
	}
	
	/** 함수호출(또는 배열첨자)의 파라미터 하나를 대상으로 startIndex()와 endIndex 안에서 
	 * 수식(덧셈 등의 변수사용, 함수호출)의 type을 결정한다. 또한 수식을 postfix형식으로 변환한다.
	 * startIndex(), endIndex는 수식의 범위를 말하고 범위안에 포함된다. 
	 * 수식의 범위안에 네임스페이스와 함수, 배열원소등이 포함되어 있어도 된다.
	 * 
	 * 예를들어,
	 * 이 함수호출파라미터(funcCall)는 함수호출varUse의 listOfFuncCallParams의 원소이거나(f(a,b,c)에서 a,b,c), 
	 * 배열원소varUse의 listOfArrayElementParams의 원소이거나(buffer[i][j]에서 i, j),
	 * (타입)(수식)에서 타입VarUse의 TypeCast의 funcCall(FindFuncCallParam) 이거나,
	 * 할당문(FindAssignStatementParams)에서 rValue(FindFuncCallParam)를 말한다.*/
	CodeStringEx getTypeOfExpression(HighArray_CodeString src/*, int startIndex, int endIndex*/, 
			FindFuncCallParam funcCall) {
		//if (funcCall.typeFullName!=null) return funcCall.typeFullName; 
		try{
		if (funcCall.startIndex()>=379 && funcCall.startIndex()<381) {
			int a;
			a=0;
			a++;
		}
		
		if (funcCall.typeFullName!=null) 
			return funcCall.typeFullName;
			
		int indexOfSeparator = isEventHandlerClass(src, funcCall);
		if (indexOfSeparator!=-1) {
			funcCall.endIndex = IndexForHighArray.indexRelative(funcCall, src, indexOfSeparator-1);
		}
		if (funcCall.funcName!=null && funcCall.funcName.equals("CodeString") && 
				funcCall.startIndex()==2465 || funcCall.startIndex()==2466) {
			int a;
			a=0;
			a++;
		}
		else if (funcCall.funcName!=null && funcCall.funcName.equals("tempTable")) {
			int a;
			a=0;
			a++;
		}
		else if (funcCall.funcName!=null && funcCall.funcName.equals("boolean")) {
			int a;
			a=0;
			a++;
		}
		if (funcCall.startIndex()>funcCall.endIndex()) return new CodeStringEx("void");
		
		int endIndexInmListOfAllVarUses;
		int startIndexInmListOfAllVarUses;
		int startIndexInmBuffer;
		
		startIndexInmBuffer = SkipBlank(src, false, funcCall.startIndex(), funcCall.endIndex());
		startIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, this.mlistOfAllVarUses, 
				0, startIndexInmBuffer, true);
		endIndexInmListOfAllVarUses = getIndexInmListOfAllVarUses(src, this.mlistOfAllVarUses, 
				startIndexInmListOfAllVarUses, funcCall.endIndex(), false);
		if (endIndexInmListOfAllVarUses!=-1) {
			// 함수호출의 매개변수당 한번씩 recursive call
			try {
			this.findMemberUsesUsingNamespace_sub(src, mlistOfAllVarUses, startIndexInmListOfAllVarUses, endIndexInmListOfAllVarUses);
			}catch(Exception e) {
				int a;
				a=0;
				a++;
				e.printStackTrace();
			}
		}
		
		int i;
		if (funcCall.startIndex()==1128) {
			int a;
			a=0;
			a++;
		}
		
		if (funcCall.funcName!=null && funcCall.funcName.equals("int")) {
			int a;
			a=0;
			a++;
		}
		if (funcCall.funcName!=null && funcCall.funcName.equals("String")) {
			int a;
			a=0;
			a++;
		}
		
		CodeStringEx[] postfix = null;
		if (funcCall.expression==null || funcCall.expression.postfix==null) {
			FindExpressionParams expression = funcCall.expression;
			if (funcCall.expression==null) {
				expression = new FindExpressionParams(this, funcCall.startIndex(), funcCall.endIndex());
			}
			// funcCall.expression이 null이 아니면 다시 생성하지 않는다.
			if (562<=expression.startIndex() && expression.startIndex()<=563) {
				int a;
				a=0;
				a++;
			}
			PostFixConverter converter = new PostFixConverter(this, src, expression);
			postfix = converter.Convert();
			expression.postfix = postfix;
			funcCall.expression = expression;
		}
		
		
		//ArrayListString listOfTypes = new ArrayListString(10);
		
		/** 수식 스택*/
		ArrayListIReset listOfTypes = new ArrayListIReset(10);
		
		CodeStringEx type = null;
		if (postfix==null) {
			int a;
			a=0;
			a++;
			
			return null;
		}
		
		
		for (i=0; i<postfix.length; i++) {
			CodeStringEx str = postfix[i];
			
			HighArray_CodeString src2 = new HighArray_CodeString(1);
			src2.add(str);
			
			if (str==null) {
				int a;
				a=0;
				a++;
			}
			if (str.equals("this.alpha")) {
				int a;
				a=0;
				a++;
			}
			if (CompilerHelper.IsOperator(str)==false && str.equals("new")==false ){
				// 변환된 postfix 수식에서 operand 하나의 타입을 얻는다.
				// k++, a[k++]++일 경우
				CodeString token = src.getItem(str.indicesInSrc.getItem(0));
				FindVarUseParams varUse = null;
				CodeStringEx strType = null;
				ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub returnOfgetTypeOfVarUseOrFuncCallOfFullName2 = null;
				if (CompilerHelper.IsOperator(token)==false ) {					
					returnOfgetTypeOfVarUseOrFuncCallOfFullName2 = getTypeOfVarUseOrFuncCallOfFullName2(src, src2, str.indicesInSrc.getItem(0));
					if (returnOfgetTypeOfVarUseOrFuncCallOfFullName2==null)
						strType = null;
					else strType = returnOfgetTypeOfVarUseOrFuncCallOfFullName2.typeFullName;
					type = strType;
					if (returnOfgetTypeOfVarUseOrFuncCallOfFullName2!=null) {
						if (returnOfgetTypeOfVarUseOrFuncCallOfFullName2.varUse!=null) {
							returnOfgetTypeOfVarUseOrFuncCallOfFullName2.varUse.tokenInPostfixWhenVarUseIsConstant = str;
						}
					}
					if (CompilerHelper.IsNumber2(str)!=0) { // 숫자 상수이면
						type.value = str.str;
						type.typeFullName = strType.str;
					}
				}//if (CompilerHelper.IsOperator(token)==false ) {
				else { // ++k, ++a[k++]
					int indexInToken = 2;
					CodeString tokenLast = src.getItem(str.indicesInSrc.getItem(indexInToken));
					if (CompilerHelper.IsOperator(tokenLast)==false ) {
						returnOfgetTypeOfVarUseOrFuncCallOfFullName2 = getTypeOfVarUseOrFuncCallOfFullName2(src, src2, str.indicesInSrc.getItem(indexInToken));
						if (returnOfgetTypeOfVarUseOrFuncCallOfFullName2==null)
							strType = null;
						else strType = returnOfgetTypeOfVarUseOrFuncCallOfFullName2.typeFullName;
						type = strType;
						if (returnOfgetTypeOfVarUseOrFuncCallOfFullName2!=null) {
							if (returnOfgetTypeOfVarUseOrFuncCallOfFullName2.varUse!=null) {
								returnOfgetTypeOfVarUseOrFuncCallOfFullName2.varUse.tokenInPostfixWhenVarUseIsConstant = str;
							}
						}
						if (CompilerHelper.IsNumber2(str)!=0) { // 숫자 상수이면
							type.value = str.str;
							type.typeFullName = strType.str;
						}
					}
				}
				if (type!=null) {
					str.typeFullName = type.str;
					type.operandOrOperator = str;
				}
				
				// 스택에 넣는다.	
				// getTypeOfOperator()에서 str의 typeFullNameAfterOperation의 값이 바뀌게 된다.
				listOfTypes.add(type);
			}
			// 1  2.5f  +  3  +   --> 첫번째 +를 만날시 float가 getTypeOfOperator에서 리턴되고,
			// listOfTypes에는 첫두개의 오퍼랜드가 삭제되어 float가 저장된다. 
			// 3을 만나면 float  int이 되고,
			// 두번째 +를 만나면 마지막이 float int이기 때문에 float가 getTypeOfOperator에서 리턴되고,
			// 최종적으로 listOfTypes에는 두개의 오퍼랜드가 삭제되어 빈상태가 된다.
			// 마지막 float가 수식의 타입으로 리턴된다.
			else if (CompilerHelper.IsOperator(str)) { // 이항 연산자일 경우에만
				if (str.indicesInSrc.count>0 && str.indicesInSrc.list[0]==5398) {
					int a;
					a=0;
					a++;
				}
				
				type = CompilerHelper.getTypeOfOperator(this, funcCall, listOfTypes, str);
				if (type==null) {
					str.typeFullName = null;					
				}
				else {
					// operator의 타입을 정한다.
					str.typeFullName = type.str;
					// operator인 str에 저장된 value를 type에 넣어 스택에서 옳게 계산되도록 한다.
					type.value = str.value;
					
					type.operandOrOperator = str;
				}
				// 스택에서 오퍼랜드들을 빼고 연산 결과를 넣는다.
				// getTypeOfOperator()에서 str의 typeFullNameAfterOperation의 값이 바뀌게 된다.
				try {
				listOfTypes.add(type);
				}catch(Exception e) {
					e.printStackTrace();
					int a;
					a=0;
					a++;
				}
			}
		}//for (i=0; i<postfix.length; i++) {
		
		// 최종적으로 type 하나가 스택에 남는다.
		// funcCall이 상수값들인 경우 계산된 최종값은 type의 value에 저장되어 있다.
		if (type==null) funcCall.typeFullName = null;
		else funcCall.typeFullName = type;
		
		return type;
		
		
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			String message="";
			for (int m=funcCall.startIndex(); m<=funcCall.endIndex(); m++) {
				message += src.getItem(m);
			}
			Log.e("error", "getTypeOfExpression :" + message);
			CompilerHelper.printMessage(CommonGUI.textViewLogBird, "getTypeOfExpression :" + message);
			return null;
		}
		
		
	}
	
	
	
	
	
	
	
	
	/** full name이든 아니든 startIndex()에서 시작하는 
	 * 변수사용의 type, 혹은 함수의 타입을 리턴한다.
	 * 예를 들어 full name일 경우 hScrollBar.rectForPage.draw(); 여기에서 draw의 리턴타입를 말한다.
	 * a.b().c일 경우 c의 타입을 리턴한다. a.b()일 경우 b의 리턴타입을 리턴한다.
	 * buffer[i+1]일 경우 이것의 타입인 com.gsoft.common.Compiler.CodeString을 리턴한다.
	 * (int)f와 같이 varUse f가 타입캐스트된 경우 변환된 int를 리턴  
	 * full name이 아니면 해당 변수사용 혹은 함수의 타입을 말한다.
	 * 변수사용이 아니면 null을 리턴한다. 
	 * full name이 아니면 패키지이름을 추가한 full name을 리턴한다.
	 * @return : boolean, 숫자(int, float), void, 문자(String, char), 오브젝트일경우 fullname*/
	ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub getTypeOfVarUseOrFuncCallOfFullName(HighArray_CodeString src, int startIndex) {
		if (startIndex==3353) {
			int a;
			a=0;
			a++;
		}
		
		
		int endIndexOfFullName;
		//endIndexOfFullName = SkipBlank(src, false, startIndex(), src.count-1);
		endIndexOfFullName = getFullNameIndex(src, false, startIndex);
		if (endIndexOfFullName<startIndex) {
			errors.add(new Error(this, startIndex, startIndex, "invalid function call : invalid parameter"));
			return null;
		}
		endIndexOfFullName = SkipBlank(src, true, 0, endIndexOfFullName);
		if (endIndexOfFullName<0) return null;
		
		return this.getTypeOfVarUseOrFuncCallOfFullName2_sub(src, startIndex, endIndexOfFullName);
		
	}
	
	/** 포스트픽스로 변환된 하나의 토큰을 원래의 일련의 스트링(원래의 소스상의 인덱스를 갖기위해 타입은 CodeStringEx를 갖는다)으로 변환한다.*/
	HighArray_CodeString toOriginalArrayListCodeString(HighArray_CodeString src, CodeStringEx token) {
		int count = token.indicesInSrc.count;
		HighArray_CodeString r = new HighArray_CodeString(count);
		int i;
		for (i=0; i<count; i++) {
			ArrayListInt indicesOfSrc = new ArrayListInt(1);
			indicesOfSrc.add(token.indicesInSrc.getItem(i));
			String str = src.getItem(token.indicesInSrc.getItem(i)).str;
			CodeStringEx string = new CodeStringEx(str, textColor, indicesOfSrc, null);
			r.add(string);
		}
		return r;
	}
	
	
	/** 포스트픽스로 변환된 하나의 토큰에서 indexOfPostfix을 원래 소스상의 인덱스로 변환한다.
	 * @param indexOfPostfix : 포스트픽스로 변환된 하나의 토큰에서의 인덱스*/
	int  toSrcIndex(HighArray_CodeString src, CodeStringEx token, int indexOfPostfix) {
		if (token.arrayListCodeStringForToken.count<=indexOfPostfix) {
			int indexOfLast = token.indicesInSrc.getItem(token.indicesInSrc.count-1);
			return indexOfLast;
		}
		else {
			int i;
			try {
			CodeStringEx strInToken = 
					(CodeStringEx) token.arrayListCodeStringForToken.getItem(indexOfPostfix);			
			return strInToken.indicesInSrc.getItem(0);
			}catch(Exception e) {
				e.printStackTrace();
				int a;
				a=0;
				a++;
			}
			return -1;
		}
	}
	
	/** 포스트픽스로 변환된 하나의 토큰에서 소스상의 인덱스를 포스트픽스의 토큰상의 인덱스로 변환한다.
	 * @param indexOfSrc : 소스에서의 인덱스*/
	int  toPostfixIndex(HighArray_CodeString src, CodeStringEx token, int indexOfSrc) {
		int i;
		for (i=0; i<token.indicesInSrc.count; i++) {
			int index = token.indicesInSrc.getItem(i);
			if (index==indexOfSrc) return i;
		}
		return -1;
	}
	
	static class ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub {
		CodeStringEx typeFullName;
		FindVarUseParams varUse;
		ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub(CodeStringEx typeFullName, 
				FindVarUseParams varUse) {
			this.typeFullName = typeFullName;
			this.varUse = varUse;
		}
	}
	
	ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub getTypeOfVarUseOrFuncCallOfFullName2_sub(HighArray_CodeString src, int startIndex, int endIndexOfFullName) {
		if (endIndexOfFullName==360) {
			int a;
			a=0;
			a++;
		}
		String varUseName = src.getItem(endIndexOfFullName).str;
		FindVarUseParams varUse = getVarUseWithIndex(mlistOfAllVarUsesHashed, varUseName, endIndexOfFullName);
		
		
		CodeStringEx result = null;
		String strResult = null;
			
		
		CodeString strOfEndIndexOfFullName = src.getItem(endIndexOfFullName);
		//strOfEndIndexOfFullName.setType(src.getItem(startIndex()).charAt(0).type);
		
		if (strOfEndIndexOfFullName.equals("true") || strOfEndIndexOfFullName.equals("false")) {
			strResult = "boolean";
		}
		else if (strOfEndIndexOfFullName.equals("null")) {
			strResult = "null";
		}
		
		else { // 숫자나 인용문(스트링), 인용문자(문자상수)
			if (strOfEndIndexOfFullName.equals("10")) {
				int a;
				a=0;
				a++;
			}
			else if (strOfEndIndexOfFullName.equals("-1l")) {
				int a;
				a=0;
				a++;
			}
			int number = CompilerHelper.IsNumber2(strOfEndIndexOfFullName);
			/*if (r==0) {
				errors.add(new Error(this, endIndexOfFullName, endIndexOfFullName, "invalid number"));
			}*/
			
			if (number==1) {
				strResult = "char";
			}
			else if (number==2) {
				strResult = "short";
			}
			else if (number==3) {
				strResult = "int";
			}
			else if (number==4) {
				strResult = "long";
			}
			else if (number==5) {
				strResult = "float";
			}
			else if (number==6) {
				strResult = "double";
			}
			else if (number==7) {
				strResult = "byte";
			}
			
			if (strOfEndIndexOfFullName==null || strOfEndIndexOfFullName.equals("")) 
			{
				strResult = "void";
			}
			else if (CompilerHelper.IsConstant(strOfEndIndexOfFullName)) {
				// 문자 상수
				char c = strOfEndIndexOfFullName.charAt(0).c;
				if (c=='"')	{
					strResult = "java.lang.String";
				}
				else if (c=='\'') {
					strResult = "char";
				}
			}
		}
		
		//(Object)(buffer[(int)i+0]+2) 이와 같은 수식 타입캐스트일 경우
		if (src.getItem(startIndex).equals("(")) {
			int indexOfRightPair = CompilerHelper.CheckParenthesis(src, "(", ")", startIndex, src.count-1, false);
			int indexOfvarUseTypeCast = this.SkipBlank(src, true, 0, indexOfRightPair-1);
			String varUseNameStr = src.getItem(indexOfvarUseTypeCast).str;
			FindVarUseParams varUseTypeCast = 
				this.getVarUseWithIndex(mlistOfAllVarUsesHashed, varUseNameStr, indexOfvarUseTypeCast);
			
			if (varUseTypeCast!=null && varUseTypeCast.typeCast!=null && 
					/*varUseTypeCast.typeCast.affectsExpression &&*/ 
					varUseTypeCast.typeCast.endIndexToAffect()!=-1 &&
					varUseTypeCast.typeCast.funcCall!=null) {
				int indexRightPair = 
					SkipBlank(src, false, varUseTypeCast.typeCast.endIndexToAffect()+1, src.count-1);
				//if (indexRightPair==endIndexOfFullName) {				
					strResult = varUseTypeCast.typeCast.funcCall.typeFullName.str;
					
					return new ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub(new CodeStringEx(strResult), varUse);
				//}
			}
		}
		
		
		// hScrollBar.rectForPage.draw(); 여기에서 draw를 말한다.
		//GetVarUseWithIndexReturnType r = getVarUseWithIndex(mlistOfAllVarUses, 0, endIndexOfFullName);
		
		if (varUse!=null) { // 위와 같은 수식 타입 캐스트가 아닌 경우
			if (src.getItem(varUse.index()).equals("bounds")) {
				int a;
				a=0;
				a++;
			}
			// (int)f와 같이 varUse f가 타입캐스트된 경우 변환된 int를 리턴  
			//if (varUse.fullnameTypeCast!=null) {
			/*if (varUse.typeCastedByVarUse!=null) {
				String fullnameTypeCast = FindVarUseParams.getFullnameTypeCast(varUse.typeCastedByVarUse);
				return new ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub(new CodeStringEx(fullnameTypeCast), varUse);
			}*/
			
			if (varUse.isArrayElement) {
				// array 오브젝트 생성
				boolean isConstructor = false;
				int prevIndex = SkipBlank(src, true, 0, varUse.index()-1);
				if (prevIndex!=-1) {
					if (src.getItem(prevIndex).equals("new")) {
						isConstructor = true;
					}
					else isConstructor = false;
				}
				if (isConstructor) {	
					// a = new int[10][10]인 경우
					int arrayDimension = CompilerHelper.getArrayDimension(this, varUse.name); 
					if (arrayDimension!=0) {
						String type = CompilerHelper.getArrayElementType(varUse.name);
						String fullnameType = getFullNameType2(src, type, varUse.index());
						String returnType = CompilerHelper.getArrayType(fullnameType, arrayDimension);
						
						strResult = returnType;	
						return new ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub(new CodeStringEx(strResult), varUse);
						
					}
				}
				else { // a = new Character.Subset[10][10]에서 varUse는 Subset을 가리킨다.
					if (varUse.memberDecl!=null) {
						String typeName = getFullNameType(this, startIndex, endIndexOfFullName);
						int arrayDimension = CompilerHelper.getArrayDimension(this, varUse.name); 
						if (arrayDimension!=0) {
							String returnType = CompilerHelper.getArrayType(typeName, arrayDimension);
							strResult = returnType;
							return new ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub(new CodeStringEx(strResult), varUse);
						}
					}
				}
				
			}//if (varUse.isArrayElement) {
			if (varUse.varDecl!=null) {
				FindVarParams var = varUse.varDecl; 
				if (var.isThis) { // return this;
					FindClassParams classParams = (FindClassParams) var.parent;
					String fullname = getFullNameExceptPackageName(src, classParams);
					fullname = fullname.substring(0, fullname.length()-1);
					strResult = packageName + "." + fullname;
					
				}
				else if (var.isSuper) { // return super;
					String typeName = ((FindClassParams)var.parent).classNameToExtend;
					result =  new CodeStringEx(typeName);
					
				}
				
				else if (var.typeName!=null) {
					//String fullname = var.getType(src, var.typeStartIndex(), var.typeEndIndex());
					String fullname = var.typeName;
					if (varUse.isArrayElement) { 
						// buffer[i]와 같은 경우, 타입은 CodeString[]이므로 CodeString을 리턴
						strResult = CompilerHelper.getTypeOfArrayElement(this, fullname, varUse);
					}
					else {
						// mBuffer와 같은 경우, 타입은 CodeString[]이므로 CodeString[]을 리턴
						strResult = fullname;
					}
					
				}
				else { // 외부 라이브러리
					strResult = var.typeName;
					
				}
			}
			else if (varUse.funcDecl!=null) {
				FindFunctionParams func = varUse.funcDecl;
				if (varUse.isArrayElement) {
					// int[] f() {} 이고 
					// int v = f()[0] + 1;
					// varUse는 f()[0] 이다.
					int dimension1 = CompilerHelper.getArrayDimension(this, varUse.name);
					int dimension2 = CompilerHelper.getArrayDimension(this, func.returnType);
					if (dimension1==dimension2) {
						strResult = CompilerHelper.getArrayElementType(func.returnType);
						
					}
					else {
						errors.add(new Error(this, varUse.index(), varUse.index(), "Array dimension is not valid."));
					}
				}
				else {
					strResult = func.returnType;					
				}
			}//else if (varUse.funcDecl!=null) {
			else if (varUse.memberDecl!=null) {
				FindClassParams c = (FindClassParams) varUse.memberDecl;
				strResult = c.name;
			}
		}
		if (strResult==null) return null;
		else return new ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub(new CodeStringEx(strResult), varUse);
	}
	
	/**@param src : 원래 소스
	 * @param srcOfPostfix : 포스트픽스상의 하나의 토큰, {토큰}인 개수가 1개인 배열이다.
	 * @param startIndex : 소스상에서의 토큰의 시작 인덱스
	 * @return : fullname 타입
	 */
	ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub getTypeOfVarUseOrFuncCallOfFullName2(HighArray_CodeString src, HighArray_CodeString srcOfPostfix, int startIndex) {
		if (startIndex>=217590 && startIndex<=217592) {
			int a;
			a=0;
			a++;
		}
		if (startIndex==3854) {
			int a;
			a=0;
			a++;
		}
		if (src.getItem(startIndex).equals("int")) {
			int a;
			a=0;
			a++;
		}
		else if (src.getItem(startIndex).equals("2")) {
			int a;
			a=0;
			a++;
		}
		
		CodeStringEx token = (CodeStringEx)srcOfPostfix.getItem(0);
		if (token.arrayListCodeStringForToken==null) {
			token.arrayListCodeStringForToken = toOriginalArrayListCodeString(src, token);
		}
		
		int startIndexForPostfix = this.toPostfixIndex(src, token, startIndex);
		int endIndexOfFullName, endIndexOfFullNameForPostfix;
		endIndexOfFullNameForPostfix = getFullNameIndexWithPostfix(token.arrayListCodeStringForToken, token, false, startIndexForPostfix);
		if (endIndexOfFullNameForPostfix<0) return null;
		
		endIndexOfFullName = toSrcIndex(src, token, endIndexOfFullNameForPostfix);
		if (endIndexOfFullName<startIndex) {
			errors.add(new Error(this, startIndex, startIndex, "invalid function call : invalid parameter"));
			return null;
		}
		endIndexOfFullName = SkipBlank(src, true, 0, endIndexOfFullName);
		if (endIndexOfFullName<0) return null;
		
		
		return 
			getTypeOfVarUseOrFuncCallOfFullName2_sub(src, startIndex, endIndexOfFullName);
	}
	
	/** mlistOfAllVarUses에서 시작인덱스(0)에서부터 검색을 시작하여 
	 * varUse의 mlistOfAllVarUses에서의 인덱스를 리턴한다.
	 */
	int getIndexInmListOfAllVarUses(HighArray_CodeString src, ArrayListIReset mlistOfAllVarUses, FindVarUseParams varUse) {
		int i;
		for (i=0; i<mlistOfAllVarUses.count; i++) {
			FindVarUseParams v = (FindVarUseParams) mlistOfAllVarUses.getItem(i);
			if (v.index()==varUse.index()) return i;
		}
		return -1;
	}
	
	/** mlistOfAllVarUses에서 시작인덱스(startIindexInmListOfAllVarUses)에서부터 검색을 시작하여 
	 * indexInmBuffer을 만날 때까지 mlistOfAllVarUses의 마지막 인덱스를 리턴한다. 못찾으면 -1을 리턴
	 * @param isStartOrEnd : true일때, 즉 start일때는 varUse.index()>=indexInmBuffer 조건이 최초로 성립할때이고,
	 * false일때, 즉 end일때는 varUse.index()==indexInmBuffer시는 현재varUse의 인덱스, 
	 * 그렇지않으면 이전varUse의 인덱스를 리턴한다.*/
	int getIndexInmListOfAllVarUses(HighArray_CodeString src, HighArray<FindVarUseParams> mlistOfAllVarUses, 
			int startIindexInmListOfAllVarUses, 
			int indexInmBuffer, boolean isStartOrEnd) {
		int i;
		FindVarUseParams varUse = null;
		int len = mlistOfAllVarUses.getCount();
		for (i=startIindexInmListOfAllVarUses; i<len; i++) {
			varUse = (FindVarUseParams) mlistOfAllVarUses.getItem(i);
			if (varUse.index()>=indexInmBuffer) break;
		}
		if (i<len) {
			if (isStartOrEnd) return i;
			else {
				if (i==startIindexInmListOfAllVarUses) return i;
				else if (varUse.index()==indexInmBuffer) return i;
				return i-1;
			}
		}
		else if (i==len) {
			//indexInmBuffer이 mlistOfAllVarUses내의 마지막 varUse의 인덱스보다 큰 경우
			if (isStartOrEnd) return i-1;
			else return i-1;
		}
		
		return -1;
	}
	
	/** listOfAllVarUses에서 시작인덱스(startIindexInListOfAllVarUses)에서부터 검색을 시작하여 
	 * indexInBuffer을 만날 때까지 listOfAllVarUses의 마지막 인덱스를 리턴한다. 
	 * 못찾으면 listOfAllVarUses.count을 리턴
	 * @param isStartOrEnd : true일때, 즉 start일때는 varUse.index()>=indexInBuffer 조건이 최초로 성립할때이고,
	 * false일때, 즉 end일때는 varUse.index()==indexInBuffer시는 현재varUse의 인덱스, 
	 * 그렇지않으면 이전varUse의 인덱스를 리턴한다.*/
	int getIndexInmListOfAllVarUses2(HighArray<FindVarUseParams> listOfAllVarUses, 
			int startIindexInListOfAllVarUses, 
			int indexInBuffer, boolean isStartOrEnd) {
		int i;
		FindVarUseParams varUse = null;
		int len = listOfAllVarUses.getCount();
		for (i=startIindexInListOfAllVarUses; i<len; i++) {
			varUse = (FindVarUseParams) listOfAllVarUses.getItem(i);
			if (varUse==null) continue;
			if (varUse.index()>=indexInBuffer) break;
		}
		if (i<len) {
			if (isStartOrEnd) return i;
			else {
				if (i==startIindexInListOfAllVarUses) return i;
				else if (varUse.index()==indexInBuffer) return i;
				return i-1;
			}
		}
		
		return i;
	}
	
	
	/** listOfAllVarUses에서 시작인덱스(startIindexInListOfAllVarUses)에서부터 검색을 시작하여 
	 * indexInBuffer을 만날 때까지 listOfAllVarUses의 마지막 인덱스를 리턴한다. 
	 * 못찾으면 listOfAllVarUses.count을 리턴
	 * @param isStartOrEnd : true일때, 즉 start일때는 varUse.index()>=indexInBuffer 조건이 최초로 성립할때이고,
	 * false일때, 즉 end일때는 varUse.index()==indexInBuffer시는 현재varUse의 인덱스, 
	 * 그렇지않으면 즉 varUse.index()>indexInBuffer일 때는 이전varUse의 인덱스를 리턴한다.*/
	int getIndexInmListOfAllVarUses2(ArrayListIReset listOfAllVarUses, 
			int startIindexInListOfAllVarUses, 
			int indexInBuffer, boolean isStartOrEnd) {
		int i;
		FindVarUseParams varUse = null;
		for (i=startIindexInListOfAllVarUses; i<listOfAllVarUses.count; i++) {
			varUse = (FindVarUseParams) listOfAllVarUses.getItem(i);
			if (varUse==null) continue;
			if (varUse.index()>=indexInBuffer) break;
		}
		if (i<listOfAllVarUses.count) {
			if (isStartOrEnd) return i;
			else {
				if (i==startIindexInListOfAllVarUses) return i;
				else if (varUse.index()==indexInBuffer) return i;
				return i-1;
			}
		}
		
		return i;
	}
	
	/** 함수호출의 파라미터가 함수, 덧셈, 뺄셈 등 operator일 경우, full name를 포함한 
	 * 모든 경우에 파라미터의 타입을 정해준다. 파라미터가 네임스페이스가 있는 함수일 수도 있으므로 
	 * 함수호출의 매개변수당 한번씩 findMemberUsesUsingNamespace_sub 을 recursive call 한다.
	 * @param src
	 * @param listOfFuncCallParams : ','로 구분된 파라미터 리스트, FindFuncCallParam[]
	 * @param indexInmListOfAllVarUses : varUse(해당함수이름)의 mListOfAllVarUses에서의 인덱스
	 */
	boolean setParamsTypeOfListOfFuncCallParams(HighArray_CodeString src, 
			ArrayListIReset listOfFuncCallParams) {
		if (listOfFuncCallParams==null) return false;
		int i;
		CodeStringEx typeName = null;
		/*int endIndexInmListOfAllVarUses;
		int startIndexInmListOfAllVarUses;
		int startIndexInmBuffer;*/
		
		for (i=0; i<listOfFuncCallParams.count; i++) {
			FindFuncCallParam funcCallParam = (FindFuncCallParam)listOfFuncCallParams.getItem(i);
			if (funcCallParam.startIndex()==30399) {
				int a;
				a=0;
				a++;
			}
			if (funcCallParam.funcName.equals("FindExpression")) {
				int a;
				a=0;
				a++;
			}
			if (funcCallParam.typeFullName==null) {
				typeName = getTypeOfExpression(src, funcCallParam);
				if (typeName!=null && (typeName.str==null || typeName.str.equals(""))) {
					funcCallParam.typeFullName = null;
					return false;
				}
				else {
					funcCallParam.typeFullName = typeName;
				}
			}
			//if (typeName!=null) {
			if (typeName!=null && (typeName.str==null || typeName.str.equals(""))) {
				funcCallParam.classParams = getFindClassParams(Compiler.mlistOfAllClassesHashed, Compiler.mlistOfAllClasses, typeName.str);
			}
		}
		return true;
		
	}
	
	/** findFuncCallParams의 sub로서 startIndex()와 endIndex영역안에 함수호출이나 타입캐스트문 혹은 수식 괄호가 있으면 
	 * 그을 건너뛴 인덱스를 리턴한다.
	 * @param endIndex : 함수호출의 )의 인덱스-1*/
	int findFuncCallParams_sub2(HighArray_CodeString src, int startIndex, int endIndex) {
		int i;
		int indexOfLeftPair, indexOfRightPair;
		
		for (i=startIndex; i<=endIndex; i++) {
			CodeString str = src.getItem(i);
			if (CompilerHelper.IsComment(str) || CompilerHelper.IsBlank(str) ) continue;
			else if (str.equals("(")) {
				int rightPair;
				rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", i, endIndex, false);
				if (rightPair==-1) {
					errors.add(new Error(this, i, i, ") not exists"));
				}
				else i = rightPair;
				//continue;
			}
			else if (str.equals(",")) {
				return i-1;
			}
		}
		return endIndex;
	}
	
	/** findFuncCallParams의 sub로서 영역안에 함수호출이 있으면 
	 * 함수호출을 건너뛴 인덱스를 리턴한다.*/
	int findFuncCallParams_sub(HighArray_CodeString src, int startIndex, int endIndex) {
		int i;
		int indexOfLeftPair, indexOfRightPair;
		
		for (i=startIndex; i<=endIndex; i++) {
			CodeString str = src.getItem(i);
			if (CompilerHelper.IsComment(str) || CompilerHelper.IsBlank(str)) continue;
			if (IsIdentifier(str)) { // 영역안에 함수호출이 있는지 조사
				indexOfLeftPair = SkipBlank(src, false, i, endIndex-1);
				if (indexOfLeftPair!=endIndex) {
					CodeString leftPair = src.getItem(indexOfLeftPair);
					if (leftPair.equals("(")) {
						indexOfRightPair = CompilerHelper.CheckParenthesis(src, "(", ")", indexOfLeftPair, endIndex-1, false);
						if (indexOfRightPair!=-1) { // 영역안에 함수호출이 있는 경우
							i = indexOfRightPair;
							return i;	// 함수호출을 스킵한 인덱스, )의 인덱스
						}
						else { // 함수호출은 있으나 )이 없는 경우
							return startIndex;
						}
					}
					else { // 식별자는 있으나 (이 없는 경우
						return startIndex;
					}
				}
				else { // 식별자는 있으나 모두 공백이나 주석인 경우
					return startIndex;
				}
			}	
		}
		return startIndex;
	}
	
	/** []로 구분되는 배열원소의 파라미터들을 찾는다. 예를들어 a[i][j+1]에서 'i', 'j+1'각각을 말한다.
	 * @param curVarUse : a[i][j]에서 varUse 'a'를 말한다.
	 * @param indexOfCurVarUse : listOfAllVarUses 에서 'a'의 인덱스
	 * @return ArrayListIReset : FindFuncCallParam[], FindFuncCallParam의 시작과 끝인덱스에서 '[', ']'는 포함하지 않는다.
	 *   	파라미터가 없을 경우에는 null이 아닌 count가 0인 것을 리턴*/
	ArrayListIReset findArrayElementParams(HighArray_CodeString src, FindVarUseParams curVarUse, int indexOfCurVarUse, 
			HighArray<FindVarUseParams> listOfAllVarUses) {
		int i;
		ArrayListIReset result = new ArrayListIReset(5);
		result.resizeInc = 10;
		FindFuncCallParam funcCallParam=null;
		
		int dimensionOfElement = CompilerHelper.getArrayDimension(this, curVarUse.name);
		int countDimension = 0;
		
		for (i=curVarUse.index()+1; i<src.count; i++) {
			CodeString str = src.getItem(i);
			// 배열원소 안에 또다른 배열원소가 있을수 있으므로 [가 있으면 짝이 맞는 ]으로 점프한다.
			if (str.equals("[")) {
				funcCallParam = new FindFuncCallParam(this, i+1, -1);
				funcCallParam.funcName = src.getItem(curVarUse.index()).str;
				int rightPair;
				rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", i, src.count, false);
				if (rightPair==-1) errors.add(new Error(this, i, i, "] not exists"));
				else {
					i = rightPair;
					funcCallParam.endIndex = IndexForHighArray.indexRelative(funcCallParam, src, i-1);
					result.add(funcCallParam);
					countDimension++;
					if (dimensionOfElement==countDimension) break;
				}
			}
		}
		return result;
	}
	
	/** ','로 구분되는 함수 호출의 파라미터들을 괄호안에서 찾는다.
	 * @param indexOfStartOfSmallBlock : 함수호출의 시작 '('
	 * @param indexOfEndOfSmallBlock : 함수호출의 마지막 ')'
	 * @param varUse : 함수호출의 varUse
	 * @return ArrayListIReset : FindFuncCallParam[], 
	 *   	파라미터가 없을 경우에는 null이 아닌 count가 0인 것을 리턴*/
	ArrayListIReset findFuncCallParams(HighArray_CodeString src, 
			int indexOfStartOfSmallBlock, int indexOfEndOfSmallBlock, FindVarUseParams varUse) {
		int i;
		ArrayListIReset result = new ArrayListIReset(5);
		result.resizeInc = 10;
		FindFuncCallParam funcCallParam=null;
		
		// characters[i] = new Character((char)0, size);
		// test(test(this, this.alpha), ((this)).backColor + (2+5)*3);
		// test( this, ((new RectForPage(owner, bounds, (backColor)+((1)-2)*2, isUpOrDown))).a+(2+1+3)*(1+0) );
		for (i=indexOfStartOfSmallBlock+1; i<indexOfEndOfSmallBlock; i++) {
			CodeString str = src.getItem(i);
			if (i==indexOfStartOfSmallBlock+1) {
				funcCallParam = new FindFuncCallParam(this, i, -1);
				if (varUse!=null) funcCallParam.funcName = src.getItem(varUse.index()).str;
				if (funcCallParam.funcName!=null && funcCallParam.funcName.equals("test")) {
					int a;
					a=0;
					a++;
				}
				i = findFuncCallParams_sub2(src, i, indexOfEndOfSmallBlock-1);
			}
			else if (i>indexOfStartOfSmallBlock+1 && str.equals(",")) {
				if (funcCallParam==null) {
					int a;
					a=0;
					a++;
				}
				funcCallParam.endIndex = IndexForHighArray.indexRelative(funcCallParam, src, i-1);
				result.add(funcCallParam);
				
				funcCallParam = new FindFuncCallParam(this, i+1, -1);
				if (varUse!=null) funcCallParam.funcName = src.getItem(varUse.index()).str;
				i = findFuncCallParams_sub2(src, i+1, indexOfEndOfSmallBlock-1);
			}
			if (i==indexOfEndOfSmallBlock-1) {
				if (funcCallParam!=null) {
					funcCallParam.endIndex = IndexForHighArray.indexRelative(funcCallParam, src, i);
					result.add(funcCallParam);
				}
			}
		}
		// 파라미터가 없는데도 불구하고 빈 파라미터 하나가 생길수도 있기때문에 그런 파라미터를 제거한다.
		if (result.count==1) {
			boolean isBlank = true;
			for (i=funcCallParam.startIndex(); i<=funcCallParam.endIndex(); i++) {
				CodeString str = src.getItem(i);
				if (CompilerHelper.IsBlank(str) || CompilerHelper.IsComment(str)) continue;
				else {
					isBlank = false;
					break;
				}
			}
			if (isBlank) result.reset2();
		}
		return result;
		
    	
	}
	
	/** isFuncComment가 true이면 함수의 다큐를 찾으므로  findVarParams는 null이고, 
	 * false이면 멤버변수의 다큐를 찾으므로 findFunctionParams는 null이다.
	 * endIndex는 주석의 마지막 인덱스이다. endIndex를 감소시키며 startIndex()까지 찾는다.*/
	DocuComment FindDocuComment(HighArray_CodeString src, 
			/*boolean isFuncComment, FindFunctionParams findFunctionParams, FindVarParams findVarParams,*/ 
			int startIndex, int endIndex) {
		if (endIndex<0) return null;
		
		
		int loop;
    	int startOfDocu=-1, endOfDocu=-1;
    	CodeString item = src.getItem(endIndex);
    	if (item.charAt(0).type==CodeStringType.DocuComment) {
    		endOfDocu = endIndex;
    	}
    	else return null;
    	
    	DocuComment docu = null;
    	for (loop=endIndex-1; loop>=startIndex; loop--) {
    		item = src.getItem(loop); 
    		if (item.charAt(0).type!=CodeStringType.DocuComment) {
    			startOfDocu = loop+1;
    			docu = new DocuComment(src, startOfDocu, endOfDocu);
    			return docu;
    		}
    	}
    	return null;
	}
	
	void FindControlBlock(HighArray_CodeString src, int keywordIndex, 
			FindControlBlockParams result, ModeAllOrUpdate modeAllOrUpdate) {
		int i = keywordIndex;
		int startIndex = 0;
		int endIndex = src.count-1;
		result.found = false;
		
		CodeString cstr = src.getItem(i);
		if (i==31493) {
			int a;
			a=0;
			a++;
		}
        CategoryOfControls category = null;
        
        if (cstr.equals("if")) category = new CategoryOfControls(CategoryOfControls.Control_if); 
        else if (cstr.equals("else")) {
        	int next = SkipBlank(src, false, i+1, endIndex);           
            if (next==endIndex+1) return;
            CodeString strNext = src.getItem(next); 
            if (strNext.equals("if")) {
            	category = new CategoryOfControls(CategoryOfControls.Control_elseif);
            }
            else if (strNext.equals("{")) {
            	category = new CategoryOfControls(CategoryOfControls.Control_else);
            }
            else {	// id, 기타 등등, 단문 else
            	category = new CategoryOfControls(CategoryOfControls.Control_else);
            }
        	
        }
        else if (cstr.equals("for")) category = new CategoryOfControls(CategoryOfControls.Control_for);
        else if (cstr.equals("while"))  {
        	category = new CategoryOfControls(CategoryOfControls.Control_while);
        }
        else if (cstr.equals("switch"))  category = new CategoryOfControls(CategoryOfControls.Control_switch);
        else if (cstr.equals("do"))  {
        	category = new CategoryOfControls(CategoryOfControls.Control_dowhile);
        }
        else if (cstr.equals("case") || cstr.equals("default"))  {
        	category = new CategoryOfControls(CategoryOfControls.Control_case);
        }
        
        if (category!=null) {
        	boolean r = CheckControlBody(src, category, result, startIndex, endIndex, i, modeAllOrUpdate);
        	if (r) {
        		result.catOfControls = category;
        		//result.startIndex() = i;
        		//result.endIndex = result.blockParams.endIndex;
        		//result.found = true;
        		
        		//Log.d(category.toString(), "start:"+result.startIndex()+" end:"+result.endIndex);
        		return;
        	}
        }
		
	}
	
	
	boolean findStaticBlock(HighArray_CodeString src, FindClassParams parent, FindFunctionParams findFunctionParams, int staticIndex, ModeAllOrUpdate modeAllOrUpdate) {
		int endIndex = src.count-1;
		int separator = SkipBlank(src, false, staticIndex + 1, endIndex); // 공백 스킵
    	int indexOfSeparator=separator;
    	int i;
    	if (separator==endIndex+1) {
    		return false;        		
    	}
    	else {
    		findFunctionParams.functionNameIndex = IndexForHighArray.indexRelative(findFunctionParams, src, staticIndex);
    		
    		//if (showsError==false) return false;
    		CodeString strSeparator = src.getItem(separator);
    		int index=separator;
    		if (strSeparator.equals("throws")) {
        		while (true) {
            		index = SkipBlank(src, false, index+1, endIndex);
                    if (index==endIndex+1) break;
                    index = getFullNames(src, "throws", null, findFunctionParams, index);
                    if (index!=-1) {
                    	CodeString codeStr = src.getItem(index);
                    	if (codeStr.equals("{")) {
                    		i = index;
                    		break;
                    	}
                    	else if (codeStr.equals(",")) {
                    	} 
                    }
                    else {	// '{'이 없을 때
                    	index = separator;
                    	break;
                    }
        		}
        	}
    		indexOfSeparator = index;
    		
    		strSeparator = src.getItem(indexOfSeparator);
    		if (strSeparator.equals("{")==false) {
    			//errors.add(new Error(this, indexOfSeparator, indexOfSeparator, "{ not exists"));
    			return false;
    		}
    	}
    	
    	
    	// 반드시 ) 다음에는 { 이다.
    	int startParenthesis = indexOfSeparator;
        int endParenthesis = -1;
        FindBlockParams findBlockParams = new FindBlockParams(this, startParenthesis, endParenthesis);
        
       
        if (findBlockParams.startIndex()!=-1/* && findBlockParams.endIndex()!=-1*/) {
        	findFunctionParams.startIndex = IndexForHighArray.indexRelative(findFunctionParams, src, staticIndex);
        	findFunctionParams.found = true;
        	findFunctionParams.isStaticBlock = true;
        	findFunctionParams.findBlockParams = findBlockParams;
        	findFunctionParams.findBlockParams.blockName = "static init block";
        	
        	findBlockParams.categoryOfBlock = new CategoryOfBlock(CategoryOfBlock.Function, null);
        	//mlistOfBlocks.add(findBlockParams);
        	this.putFindBlockParams(findBlockParams, modeAllOrUpdate);
        	return true;
        }
		return false;
	}
	
	void FindFunction(HighArray_CodeString src, FindClassParams parent, FindFunctionParams findFunctionParams, 
			int indexOfLeftParenthesis)
	{
        int i;

        findFunctionParams.returnTypeStartIndex = null;
        findFunctionParams.returnTypeEndIndex = null;
        findFunctionParams.functionNameIndex = null;
        findFunctionParams.found = false;

        if (indexOfLeftParenthesis==-1) return;
        
        i = SkipBlank(src, true, 0, indexOfLeftParenthesis - 1);
        if (i==-1) return;
        	
        CodeString cstr = src.getItem(i);
        
        if (indexOfLeftParenthesis==2074) {
        	int a;
        	a=0;
        	a++;
        }
        
        if (i==847) {
        	int a;
        	a=0;
        	a++;
        }
        
        
        // 함수 이름 찾기
        if (IsIdentifier(cstr))   // 식별자 이면
        {
        	if (cstr.equals("RectForPage")) {
        		int a;
        		a=0;
        		a++;
        	}
        	else if (cstr.equals("CheckParenthesis")) {
        		int a;
        		a=0;
        		a++;
        	}
            
        	CodeString returnType=null;
        	// i는 functionNameIndex()이다.
        	findFunctionParams.returnTypeEndIndex = IndexForHighArray.indexRelative(findFunctionParams, src, SkipBlank(src, true, 0, i - 1));
        	
        	Template template = null;
        	
        	if (findFunctionParams.returnTypeEndIndex() != -1) {        			
    			returnType = src.getItem(findFunctionParams.returnTypeEndIndex());
    			if (returnType.equals("new")) { // constructor
    				return;
    			}
    			else if (returnType.equals(".")) { // 함수의 이름은 하나의 스트링이고 "."을 포함해서는 안된다.
    				return;
    			}
    			
    			if (language==Language.C && returnType.equals("*")) {	// 포인터타입인지 확인
    				
    				findFunctionParams.returnTypeStartIndex = IndexForHighArray.indexRelative(findFunctionParams, src, 
    						IsType(src, true, findFunctionParams.returnTypeEndIndex(), template));
    			}
    			else {
    				template = new Template(this);
    				findFunctionParams.returnTypeStartIndex = IndexForHighArray.indexRelative(findFunctionParams, src,    						
    						IsType(src, true, findFunctionParams.returnTypeEndIndex(), template));
    				if (template.found) {
    					mlistOfAllTemplates.add(template);
    					findFunctionParams.template = template;
    				}
    				
    			}
    		}
        	
        	if (findFunctionParams.returnTypeStartIndex()!=-1 && findFunctionParams.returnTypeEndIndex()!=-1) {
        		
        	}
        	else {
        		
        	}
        	
        	boolean r = false;
        	
        	r = CheckBody(src, findFunctionParams, 0, src.count-1, i);
        	            	
        	if (r) {
        		if (findFunctionParams.isInterfaceMethod) {
        			if (findFunctionParams.accessModifier!=null && findFunctionParams.accessModifier.isFinal) {
        				errors.add(new Error(this, findFunctionParams.functionNameIndex(), findFunctionParams.functionNameIndex(), 
        						"Interface has an final method."));
        			}
        			
        			if (findFunctionParams.accessModifier==null) {
        				AccessModifier accessModifier = new AccessModifier(this, -1, -1);
        				accessModifier.accessPermission = AccessModifier.AccessPermission.Public;
        				accessModifier.isAbstract = true;
        				findFunctionParams.accessModifier = accessModifier;
        			}
        		}
        		
        		if (findFunctionParams.returnTypeEndIndex()!=-1 && 
                    	( IsIdentifier(returnType) || findFunctionParams.returnTypeStartIndex()!=-1 ) )
                    	// 일반함수찾기 : 주석이 아니고 식별자나 타입, 포인터이면
        		{
        			if (findFunctionParams.startIndex()==-1) {
        				if (findFunctionParams.returnTypeStartIndex()!=-1) {
        					findFunctionParams.startIndex = IndexForHighArray.indexRelative(findFunctionParams, src, findFunctionParams.returnTypeStartIndex());
        				}
        			}
        			findFunctionParams.name = src.getItem(findFunctionParams.functionNameIndex()).str;
        			//findFunctionParams.getReturnType(src, 
        			//		findFunctionParams.returnTypeStartIndex, findFunctionParams.returnTypeEndIndex);
        			findFunctionParams.isConstructor = false;
        			if (findFunctionParams.returnTypeStartIndex()!=-1 && findFunctionParams.returnTypeEndIndex()!=-1) {
	        			findFunctionParams.returnType = 
	        				getFullNameType(this, findFunctionParams.returnTypeStartIndex(), 
	        					findFunctionParams.returnTypeEndIndex());
        			}
        			//i = findFunctionParams.endIndex() + 1;
        		}
        		else { // constructor
        			if (findFunctionParams.isInterfaceMethod) {
        				errors.add(new Error(this, findFunctionParams.functionNameIndex(), findFunctionParams.functionNameIndex(), 
        						"Interface has an invalid method."));
        			}
        			findFunctionParams.returnTypeEndIndex = null;
        			String name;
        			if (parent.classNameIndex()!=-1) {
        				name = src.getItem(parent.classNameIndex()).str;
        			}
        			else {
        				name = this.getShortName(parent.name);
        			}
        			findFunctionParams.isConstructor = true;
            		if (cstr.equals(name)==false) {
            			errors.add(new Error(this, findFunctionParams.functionNameIndex(), findFunctionParams.functionNameIndex(), 
            					"Class name not equals with constructor name."));
            		}
            		
            		findFunctionParams.name = src.getItem(findFunctionParams.functionNameIndex()).str;
            		if (parent.loadWayOfFindClassParams!=LoadWayOfFindClassParams.ByteCode) {
	            		String fullname = packageName + "." + getFullNameExceptPackageName(src, parent);
	            		fullname = fullname.substring(0, fullname.length()-1);
	        			findFunctionParams.returnType = fullname;
            		}
            		else { // ByteCode에서 읽을 경우
            			findFunctionParams.returnType = parent.name;
            		}
        			
        			/*if ( src.getItem(findClassParams.classNameIndex).equals( 
        					src.getItem(findFunctionParams.functionNameIndex()).toString()) ) 
        			{ // 클래스이름과 생성자이름이 같아야한다.
        				try {
							throw new Exception("Class name not equals with constructor name.");
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
        				if (findFunctionParams.startIndex()==-1) 
            				findFunctionParams.startIndex() = i;
        			}
        			//i = findFunctionParams.endIndex() + 1;
        			if (findFunctionParams.startIndex()==-1) 
        				findFunctionParams.startIndex() = i;*/
        		}
        		findFunctionParams.name = src.getItem(findFunctionParams.functionNameIndex()).str;
        		return;
        	}
            else	// if (r)
            {
            	// 함수 호출
                //i++;
            	i = indexOfLeftParenthesis + 1;
            } // 리턴타입 확인           	
        
        }	// 함수이름찾기
        else
        {
            //i++;
        	i = indexOfLeftParenthesis + 1;
        }   // 구분자
    }
	
	/** 자신의 class와 child class들에서 function이 이미 존재하는지 찾는다.*/
	public boolean Exists(FindClassParams findClassParams, int functionNameIndex) {
		int i;
		if (findClassParams==null) return false;
		ArrayListIReset list = findClassParams.listOfFunctionParams;
		for (i=0; i<list.count; i++) {
			FindFunctionParams fp = (FindFunctionParams) list.getItem(i);
			if (fp.functionNameIndex()==functionNameIndex) return true;
		}
		if (findClassParams.childClasses!=null) {
			for (i=0; i<findClassParams.childClasses.count; i++) {
				boolean r = Exists((FindClassParams)(findClassParams.childClasses.getItem(i)), 
						functionNameIndex);
				if (r) return true;
			}
		}
		return false;
	}
	
	public AddCharReallyMode getAddCharReallyMode(int indexInmBuffer) {
		HighArray_CodeString src = this.mBuffer;
		
		CodeString cur = src.getItem(indexInmBuffer);
		if (CompilerHelper.IsComment(cur)) return null;
		
		CodeString prev = null;
		if (indexInmBuffer-1>=0)
			prev = src.getItem(indexInmBuffer-1);
		CodeString next = null;
		if (indexInmBuffer+1<this.mBuffer.count)
			next = src.getItem(indexInmBuffer+1);
		
		if ((cur.equals(" ") || cur.equals("\t") || cur.equals("\r") || cur.equals("\n"))==false) 
			return AddCharReallyMode.General_NoAddDeleteTomBuffer;
		
		if (prev!=null) {
			if ((prev.equals(" ") || prev.equals("\t") || prev.equals("\r") || prev.equals("\n"))==false) 
				return AddCharReallyMode.General_NoAddDeleteTomBuffer;
		}
		if (next!=null) {
			if ((next.equals(" ") || next.equals("\t") || next.equals("\r") || next.equals("\n"))==false) 
				return AddCharReallyMode.General_NoAddDeleteTomBuffer;	
		}		
		return AddCharReallyMode.General_AddTomBuffer;
	}
	
	/** 증감 컴파일러의 진입점*/
	public CodeString update(Point startAndEndIndex, int indexInmBuffer, CodeString newText, AddCharReallyMode mode) {
		try {
		if (mode==AddCharReallyMode.General_NoAddDeleteTomBuffer) {
			return update_addCharReally(startAndEndIndex, indexInmBuffer, newText, mode);
		}
		return null;
		}catch(Exception e) {
			e.printStackTrace();
			int a;
			a=0;
			a++;
		}
		return null;
	}
	
	public void update_addCharReally_compare(Point startAndEndIndex, int indexInmBuffer, 
			ArrayList listOfIndexForHighArray, HighArray_CodeString updateStrings, 
			AddCharReallyMode mode) {
		HighArray_CodeString src = this.mBuffer;
		if (mode==AddCharReallyMode.General_NoAddDeleteTomBuffer) {
			int startIndex = startAndEndIndex.x;
			int endIndex = startAndEndIndex.y;
			// assign문장이 삭제된 한줄보다 더 길 경우 나중에 처리하기 위해 백업한다.
			FindAssignStatementParams assignBackup = null;
			int i;
			int len = listOfIndexForHighArray.count;
			for (i=0; i<len; i++) {
				IndexForHighArray index = (IndexForHighArray) listOfIndexForHighArray.getItem(i);
				Object owner = index.owner;
				if (index.index()==380) {
					int a;
					a=0;
					a++;
				}
				if (owner instanceof FindVarUseParams) {
					FindVarUseParams varUse = (FindVarUseParams) owner;
					if (varUse.index()!=indexInmBuffer) continue;
					
					int indexInUpdateStrings = varUse.index()-startIndex;
					CodeString str = updateStrings.getItem(indexInUpdateStrings);
					if (varUse.originName.equals(str.str)) {
						
					}
					else {
						src.setCodeString(startIndex+indexInUpdateStrings, str);
						// 먼저 소스를 바꾸고 varUse.name을 다시 설정한다.
						this.findVarUseName(mBuffer, varUse, index.index());
						varUse.originName = str.str;
						if (varUse.varDecl!=null && varUse.varDecl.varNameIndex()!=varUse.index()) {
							// float scaleOfGapX = 0.05f;에서 scaleOfGapX이 scaleOfGapX2으로 바뀌는 경우에는
							// varUse.varDecl을 바꾸지 않는다.
							varUse.varDecl = null;
							//errors.add(new Error(this, varUse.index(), varUse.index(), "invalid varUse : "+varUse.name));
						}
						if (varUse.memberDecl!=null) {
							varUse.memberDecl = null;
							//errors.add(new Error(this, varUse.index(), varUse.index(), "invalid varUse : "+varUse.name));
						}
						if (varUse.funcDecl!=null) {
							varUse.funcDecl = null;
							//errors.add(new Error(this, varUse.index(), varUse.index(), "invalid varUse : "+varUse.name));
						}
						
						if (varUse.constant_info instanceof CONSTANT_Field_info) {
							// float scaleOfGapX = 0.05f;에서 scaleOfGapX가 scaleOfGapX2로 바뀌는 경우 
							CONSTANT_Field_info fieldInfo = (CONSTANT_Field_info) varUse.constant_info;
							fieldInfo.setName(str.str);
						}
						else if (varUse.constant_info instanceof CONSTANT_Float_info) {
							// float scaleOfGapX = 0.05f;에서 0.05f가 0.15f로 바뀌는 경우
							CONSTANT_Float_info floatInfo = (CONSTANT_Float_info) varUse.constant_info;
							floatInfo.f = Float.parseFloat(str.str);
						}
						else if (varUse.constant_info instanceof CONSTANT_Integer_info) {
							CONSTANT_Integer_info intInfo = (CONSTANT_Integer_info) varUse.constant_info;
							intInfo.integer = Integer.parseInt(str.str);
						}
						else if (varUse.constant_info instanceof CONSTANT_String_info) {
							CONSTANT_String_info strInfo = (CONSTANT_String_info) varUse.constant_info;
							strInfo.str = str.str;
						}
					}
					
				}//if (owner instanceof FindVarUseParams) {
				else if (owner instanceof AccessModifier) {
					AccessModifier accessModifier = (AccessModifier) owner;
					
					if (accessModifier.startIndex()<=indexInmBuffer && indexInmBuffer<=accessModifier.endIndex()) {
						int indexInUpdateStrings = indexInmBuffer-startIndex;
						CodeString str = updateStrings.getItem(indexInUpdateStrings);
						src.setCodeString(startIndex+indexInUpdateStrings, str);
						
						this.FindAccessModifier(src, accessModifier.startIndex()-1, 
								accessModifier.endIndex()+1, accessModifier);
					}
				}
				else if (owner instanceof FindVarParams) {
					FindVarParams var = (FindVarParams) owner;
					if (indexInmBuffer==var.varNameIndex()) {
						int indexInUpdateStrings = var.varNameIndex()-startIndex;
						CodeString str = updateStrings.getItem(indexInUpdateStrings);
						if (var.fieldName.equals(str.str)) {
							
						}
						else {
							var.fieldName = str.str;
							src.setCodeString(startIndex+indexInUpdateStrings, str);
						}
					}
					else if (var.typeStartIndex()<=indexInmBuffer && indexInmBuffer<=var.typeEndIndex()) {
						HighArray_CodeString backup = this.mBuffer;
						this.mBuffer = updateStrings;
						String typeName = this.getFullNameType(this, var.typeStartIndex()-startIndex, 
								var.typeEndIndex()-startIndex);
						this.mBuffer = backup;
						
						if (typeName!=null && typeName.equals("")) typeName = null;
						var.typeName = typeName;
						
						int indexInUpdateStrings = indexInmBuffer-startIndex;
						CodeString str = updateStrings.getItem(indexInUpdateStrings);
						src.setCodeString(startIndex+indexInUpdateStrings, str);
						FindClassParams classParams = CompilerHelper.loadClass(this, typeName);
						if (classParams!=null) {
							
						}
					}
					
				}//else if (owner instanceof FindVarParams) {
				
				else if (owner instanceof FindFuncCallParam) {					
					FindFuncCallParam funcCall = (FindFuncCallParam) owner;					
					if (index.index()==190) {
						int a;
						a=0;
						a++;
					}
					if (funcCall.startIndex()<=indexInmBuffer && indexInmBuffer<=funcCall.endIndex()) {
						funcCall.typeFullName = null;
						if (funcCall.expression!=null) {
							// postfix를 다시 만든다.
							funcCall.expression.postfix = null;
						}
						
						int indexInUpdateStrings = indexInmBuffer-startIndex;
						CodeString str = updateStrings.getItem(indexInUpdateStrings);
						src.setCodeString(startIndex+indexInUpdateStrings, str);
						funcCall.typeFullName = this.getTypeOfExpression(src, funcCall);
					}
				}//else if (owner instanceof FindFuncCallParam) {
				else if (owner instanceof FindAssignStatementParams) {					
					FindAssignStatementParams assign = (FindAssignStatementParams) owner;
					if (assign.endIndex()>endIndex) {
						// 삭제된 줄보다 대입문이 더 길 경우 assignBackup에 저장을 하고 루프가 끝난후 다시 처리한다.
						// assign의 startIndex, endIndex가 그 내부에 있는 varUse들보다 먼저 등록이 되기 때문이다.
						assignBackup = assign;
						continue;
					}
					if (index.index()==assign.endIndex()) {				
						int indexInUpdateStrings = indexInmBuffer-startIndex;
						CodeString str = updateStrings.getItem(indexInUpdateStrings);
						src.setCodeString(startIndex+indexInUpdateStrings, str);
						this.getTypeOfAssignment(src, assign, mode);
					}
				}//else if (owner instanceof FindAssignStatementParams) {
				else if (owner instanceof FindFunctionParams) {
					FindFunctionParams func = (FindFunctionParams) owner;
					if (func.endIndex()==1523) {
						int a;
						a=0;
						a++;
					}
					if (indexInmBuffer==func.functionNameIndex()) {
						int indexInUpdateStrings = indexInmBuffer-startIndex;
						CodeString str = updateStrings.getItem(indexInUpdateStrings);
						src.setCodeString(startIndex+indexInUpdateStrings, str);
						
						func.name = str.str;
					}
					else if (func.returnTypeStartIndex()<=indexInmBuffer && indexInmBuffer<=func.returnTypeEndIndex()) {
						HighArray_CodeString backup = this.mBuffer;
						this.mBuffer = updateStrings;
						String typeName = this.getFullNameType(this, func.returnTypeStartIndex()-startIndex, 
								func.returnTypeEndIndex()-startIndex);
						this.mBuffer = backup;
					
						FindClassParams classParams = CompilerHelper.loadClass(this, typeName);
						if (classParams!=null) {
							
						}
					}
				}//else if (owner instanceof FindFunctionParams) {
				else if (owner instanceof FindClassParams) {
					FindClassParams classParams = (FindClassParams) owner;
					if (classParams.endIndex()==1523) {
						int a;
						a=0;
						a++;
					}
					if (indexInmBuffer==classParams.classNameIndex()) {
						int indexInUpdateStrings = indexInmBuffer-startIndex;
						CodeString str = updateStrings.getItem(indexInUpdateStrings);
						src.setCodeString(startIndex+indexInUpdateStrings, str);
						
						classParams.name = str.str;
					}
					else if (classParams.startIndexOfClassNameToExtend()<=indexInmBuffer && indexInmBuffer<=classParams.endIndexOfClassNameToExtend()) {
						HighArray_CodeString backup = this.mBuffer;
						this.mBuffer = updateStrings;
						String typeName = this.getFullNameType(this, classParams.startIndexOfClassNameToExtend()-startIndex, 
								classParams.endIndexOfClassNameToExtend()-startIndex);
						this.mBuffer = backup;
					
						FindClassParams newClassParams = CompilerHelper.loadClass(this, typeName);
						if (newClassParams!=null) {
							
						}
					}
					
				}//else if (owner instanceof FindClassParams) {
			}//for (i=0; i<len; i++) {
			if (assignBackup!=null) {
				int indexInUpdateStrings = indexInmBuffer-startIndex;
				CodeString str = updateStrings.getItem(indexInUpdateStrings);
				src.setCodeString(startIndex+indexInUpdateStrings, str);
				this.getTypeOfAssignment(src, assignBackup, mode);
			}
		}//if (mode==AddCharReallyMode.General) {
	}
	
	
	
	/** editText에서 addCharReally()가 호출되면 이 함수를 호출한다.
	 * @param startAndEndIndex : mBuffer상의 절대 인덱스의 시작(x)과 끝(y) 인덱스*/
	public CodeString update_addCharReally(Point startAndEndIndex, int indexInmBuffer, CodeString newText, AddCharReallyMode mode) {
		HighArray_CodeString src = this.mBuffer;
		
		if (mode==null) {
			// 주석일 경우
			int startIndex = startAndEndIndex.x;
			int endIndex = startAndEndIndex.y;
			src.deleteDataRelated_undelete(startIndex, endIndex);
			ArrayList listOfIndexForHighArray = src.listOfIndexForHighArray;
			
			StringTokenizer tokenizer = new StringTokenizer();
			HighArray_CodeString updateStrings = 
					tokenizer.ConvertToStringArray2(newText, 60, Language.Java);
		}
		// mBuffer에 있는 스트링 하나에만 변화가 있을 경우, 즉 mBuffer에 스트링이 추가되거나 삭제되지 않는 경우
		else if (mode==AddCharReallyMode.General_NoAddDeleteTomBuffer) {
			int startIndex = startAndEndIndex.x;
			int endIndex = startAndEndIndex.y;
			src.deleteDataRelated_undelete(startIndex, endIndex);
			ArrayList listOfIndexForHighArray = src.listOfIndexForHighArray;
			
			StringTokenizer tokenizer = new StringTokenizer();
			HighArray_CodeString updateStrings = 
					tokenizer.ConvertToStringArray2(newText, 60, Language.Java);
			
			this.update_addCharReally_compare(startAndEndIndex, indexInmBuffer, listOfIndexForHighArray, updateStrings, mode);
			
			/*ArrayListIReset result = new ArrayListIReset(1);
			this.mBuffer = updateStrings;
			this.FindAllClassesAndItsMembers2_sub(0, updateStrings.count-1, result, ModeAllOrUpdate.Update);
			
			this.mBuffer = src;*/
				
			
			return newText;
		}
		
		return newText;
	}
	
	
	/** 괄호에러가 있으면 CheckParenthesis을 사용하고
	 * (BASIC언어, end function, end if, end class 등), 
	 * 괄호에러가 없으면 CheckParenthesisAll을 호출
	 * (C언어, class, function, if 등의 {, }이 모두 같다.)한 후의 캐시(listOfBlocks등)를 사용한다. 
	 * @param errors : Error[], 멤버변수로 호출
	 * @return : 괄호에러가 있으면 true, 없으면 false
	 */
	boolean hasPairError(ArrayListIReset errors) {
		int i;
		for (i=0; i<errors.count; i++) {
			Error error = (Error)errors.getItem(i);
			if (error.errorNum==Error.Error_MiddlePair || error.errorNum==Error.Error_SmallPair ||
					error.errorNum==Error.Error_LargePair ) {
				return true;
			}
		}
		return false;
	}

		    
    
    
    /** <summary>처음부터 끝까지 '{,(,['와 '},),]'을 스택에 넣고 빼며 쌍이 맞는지 확인한다. 
    * 괄호에러가 있을 때, block단위로 확인하지 않으므로 정확한 pair를 찾을 수 없지만
    * 괄호에러가 있다는 것은 알 수 있다. 
    * 괄호에러가 없을 때에 한해, listOfBlocks등 캐시를 사용하므로 더욱 빠르게 괄호의 쌍을
    * 알 수 있다.</summary>*/
    public void CheckParenthesisAll(HighArray_CodeString src, 
    		ArrayListIReset listOfBlocks, ArrayListIReset listOfSmallBlocks, ArrayListIReset listOfLargeBlocks) 
    {
        int i;
        listOfBlocks.reset2();
        listOfSmallBlocks.reset2();
        listOfLargeBlocks.reset2();
        //mlistOfIndexOfMiddlePair.reset();
        
        Stack<FindBlockParams> stack = new Stack<FindBlockParams>();
        Stack<FindSmallBlockParams> stackSmall = new Stack<FindSmallBlockParams>();
        Stack<FindLargeBlockParams> stackLarge = new Stack<FindLargeBlockParams>();        
        
        for (i = 0; i < src.count; i++)
        {
        	CodeString str = src.getItem(i);
        	if (CompilerHelper.IsComment(str)) continue;
            if (str.equals("{"))
            {
            	//mlistOfIndexOfMiddlePair.add(i);
            	FindBlockParams block = new FindBlockParams(this, i, -1); 
                stack.Push(block);
                listOfBlocks.add(block);
            }
            else if (str.equals("}"))
            {
            	//mlistOfIndexOfMiddlePair.add(i);
            	if (stack.len==2) { 
            		// 최상위 class블록(1)안에서 두번째 블록(함수 혹은 클래스)의 닫는 } 
            		int a;
            		a=0;
            		a++;
            	}
                //try
            	if (stack.len>0)
                {
            		FindBlockParams block = stack.Pop();
            		block.endIndex = IndexForHighArray.indexRelative(block, src, i);
                }
                //catch (Exception e)
            	else
                {
                	//throw new Exception("}가 {보다 많습니다");
                    //errors.add(new Error(this, i, i, "The num of '}' larger than '{'", Error.Error_MiddlePair));
            		//hasPairError = true;
                }
            }
            
            
            else if (str.equals("("))
            {
            	FindSmallBlockParams  block = new FindSmallBlockParams(this, i, -1); 
            	stackSmall.Push(block);
                listOfSmallBlocks.add(block);
            }
            else if (str.equals(")"))
            {	
                //try
            	if (stackSmall.len>0)
                {
                	FindSmallBlockParams  block = stackSmall.Pop();
                	block.endIndex = IndexForHighArray.indexRelative(block, src, i);
                }
                //catch (Exception e)
            	else
                {
                	//throw new Exception("}가 {보다 많습니다");
                    //errors.add(new Error(this, i, i, "The num of ')' larger than '('", Error.Error_SmallPair));
            		//hasPairError = true;
                }
            }
            
            else if (str.equals("["))
            {
            	FindLargeBlockParams  block = new FindLargeBlockParams(this, i, -1); 
            	stackLarge.Push(block);
                listOfLargeBlocks.add(block);
            }
            else if (str.equals("]"))
            {	
                //try
            	if (stackLarge.len>0)
                {
                	FindLargeBlockParams  block = stackLarge.Pop();
                	block.endIndex = IndexForHighArray.indexRelative(block, src, i);
                }
                //catch (Exception e)
            	else
                {
                	//throw new Exception("}가 {보다 많습니다");
                    //errors.add(new Error(this, i, i, "The num of ']' larger than '['", Error.Error_LargePair));
            		//hasPairError = true;
                }
            }
        }
        
        int j;
		for (j=0; j<listOfBlocks.count; j++) {
            FindBlockParams block = (FindBlockParams)listOfBlocks.getItem(j);
            if (block.endIndex()==-1) {
            	
            }
            else {
            	src.getItem(block.startIndex()).setColor(Compiler.keywordColor);
            	src.getItem(block.startIndex()).setType(CodeStringType.Parenthesis);
            	src.getItem(block.endIndex()).setColor(Compiler.keywordColor);
            	src.getItem(block.endIndex()).setType(CodeStringType.Parenthesis);
            }
        }
    

        //if (!stack.IsEmpty())
        //if (i==src.count) {
	        if (stack.len>0)
	        {	        	
	        	//errors.add(new Error(this, i-1, i-1, "The num of '{' larger than '}'", Error.Error_MiddlePair));
	        	//hasPairError = true;
	        }
	        
	        if (stackSmall.len>0)
	        {
	        	errors.add(new Error(this, i-1, i-1, "The num of '(' larger than ')'", Error.Error_SmallPair));
	        	//hasPairError = true;
	        }
	        
	        if (stackLarge.len>0)
	        {
	        	errors.add(new Error(this, i-1, i-1, "The num of '[' larger than ']'", Error.Error_LargePair));
	        }
        //}
		
		
    }

}