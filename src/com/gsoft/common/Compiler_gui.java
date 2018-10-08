package com.gsoft.common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;

import com.gsoft.common.Code.CodeChar;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Compiler_types.*;
import com.gsoft.common.Compiler_types.Error;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util.ArrayListIReset;
import com.gsoft.common.Util.ArrayListCodeChar;
import com.gsoft.common.Util.ArrayListCodeString;
import com.gsoft.common.Compiler_types.HighArray_CodeString;
import com.gsoft.common.Util.PoolOfButton;
import com.gsoft.common.Util.PoolOfEditText;
import com.gsoft.common.gui.Control;
import com.gsoft.common.gui.EditText;
import com.gsoft.common.gui.MenuWithScrollBar;
import com.gsoft.common.gui.MenuWithScrollBar_EditText;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Buttons.ButtonGroup;
import com.gsoft.common.gui.Control.Container;
import com.gsoft.common.gui.EditText.ScrollMode;
import com.gsoft.common.interfaces.OnTouchListener;

public class Compiler_gui {
	public static class MenuClassList extends Container implements OnTouchListener 
	{
		MenuWithScrollBar menuClassAndMemberList;
		private PoolOfButton poolOfFileListButtons;
		Size buttonSize;
		EditText editTextPath;
		//private FindClassParams parentClassParams;
		private HighArray_CodeString src;
		private FindClassParams curClassParams;
		
		public int countOfNewLineChars;
		private ArrayListIReset listOfClass;
		
		Button buttonProblemList;
		
		public TextView textView;
		
		Compiler compiler;
		private Button buttonLogBird;
		
		Button buttonShowsExtends;
		private boolean showsExtendsMember;
		
		
		public void changeBounds(Rectangle bounds) {
			this.bounds = bounds;
			changeBounds_MenuListAndEditTextPath(bounds);
			
			int heightOfButton =  (int) (menuClassAndMemberList.bounds.height * 0.1f);
			
			int x, y, w, h, gapX;
			w = (int) (bounds.width * 1/(7.0f));
			gapX = w;
			h = heightOfButton;
			x = bounds.x + w;
			y = menuClassAndMemberList.bounds.bottom() - h;
			Rectangle boundsOfProblem = new Rectangle(x,y,w,h); 
			buttonProblemList.changeBounds(boundsOfProblem);
			
			//w = (int) (bounds.width * 1/(7.0f));
			//h = heightOfButton;
			x = boundsOfProblem.right() + gapX;
			y = menuClassAndMemberList.bounds.bottom() - h;
			Rectangle boundsOfLogBird = new Rectangle(x,y,w,h); 
			buttonLogBird.changeBounds(boundsOfLogBird);
			
			//w = (int) (bounds.width * 1/(7.0f));
			//h = heightOfButton;
			x = boundsOfLogBird.right() + gapX;
			y = menuClassAndMemberList.bounds.bottom() - h;
			Rectangle boundsOfShowsExtends = new Rectangle(x,y,w,h); 
			buttonShowsExtends.changeBounds(boundsOfShowsExtends);
			
			
			int viewWidth = view.getWidth(); 
			int viewHeight = view.getHeight();
			w = (int) (viewWidth * 0.8f);
			h = (int) (viewHeight * 0.5f);
			x = viewWidth/2 - w/2;
			y = viewHeight/2 - h/2;
			Rectangle boundsOfTextView = new Rectangle(x,y,w,h);
			textView.changeBounds(boundsOfTextView);
		}
		
		
		public MenuClassList(Compiler compiler, Rectangle bounds/*, Size buttonSize*/) {
			this.compiler = compiler;
			this.bounds = bounds;
			
			this.buttonSize = new Size((int)(bounds.width*0.7f),(int)(bounds.height*0.06f));
			createMenuListAndEditTextPath();
			createPoolOfFileListButtons();
			
			//Rectangle boundsOfmenuClassAndMemberList = new Rectangle(this.menuClassAndMemberList.bounds);
			int heightOfButton =  (int) (menuClassAndMemberList.bounds.height * 0.1f);
			
			int x, y, w, h, gapX;
			w = (int) (bounds.width * 1/(7.0f));
			gapX = w;
			h = heightOfButton;
			x = bounds.x + w;
			y = menuClassAndMemberList.bounds.bottom() - h;
			Rectangle boundsOfProblem = new Rectangle(x,y,w,h); 
			buttonProblemList = new Button(this, "", "Problems", Color.LTGRAY, 
					boundsOfProblem, false, 255, true, 0, null, Color.CYAN);
			buttonProblemList.setOnTouchListener(this);
			
			//w = (int) (bounds.width * 1/(7.0f));
			//h = heightOfButton;
			x = boundsOfProblem.right() + gapX;
			y = menuClassAndMemberList.bounds.bottom() - h;
			Rectangle boundsOfLogBird = new Rectangle(x,y,w,h); 
			buttonLogBird = new Button(this, "", "LogBird", Color.LTGRAY, 
					boundsOfLogBird, false, 255, true, 0, null, Color.CYAN);
			buttonLogBird.setOnTouchListener(this);
			
			//w = (int) (bounds.width * 1/(7.0f));
			//h = heightOfButton;
			x = boundsOfLogBird.right() + gapX;
			y = menuClassAndMemberList.bounds.bottom() - h;
			Rectangle boundsOfShowsExtends = new Rectangle(x,y,w,h); 
			buttonShowsExtends = new Button(this, "", "ShowsExtends", Color.LTGRAY, 
					boundsOfShowsExtends, false, 255, true, 0, null, Color.CYAN);
			buttonShowsExtends.selectable = true;
			buttonShowsExtends.toggleable = true;
			buttonShowsExtends.setOnTouchListener(this);
			
			
			int viewWidth = view.getWidth(); 
			int viewHeight = view.getHeight();
			w = (int) (viewWidth * 0.8f);
			h = (int) (viewHeight * 0.5f);
			x = viewWidth/2 - w/2;
			y = viewHeight/2 - h/2;
			Rectangle boundsOfTextView = new Rectangle(x,y,w,h);
			textView = new TextView(false, false, this, null, boundsOfTextView, 
					view.getHeight()*0.03f, false, null, ScrollMode.VScroll, Compiler.backColor);
			textView.isReadOnly = true;
			
		
		}
		
		void changeBounds_MenuListAndEditTextPath(Rectangle newBounds) {
			this.buttonSize.width = (int) (view.getWidth()*0.7f);
			this.buttonSize.height = (int)(view.getHeight()*0.06f);
			
			Rectangle boundsOfmenuClassAndMemberList = new Rectangle(newBounds);
			int heightOfEditTextPath =  (int) (newBounds.height * 0.1f);
			boundsOfmenuClassAndMemberList.height = (newBounds.height - heightOfEditTextPath);
			if (menuClassAndMemberList!=null) {
				menuClassAndMemberList.changeBounds(boundsOfmenuClassAndMemberList, buttonSize);				
			}
			if (editTextPath!=null) {
				Rectangle boundsOfDocumentPath = new Rectangle(newBounds.x, 
						menuClassAndMemberList.bounds.bottom(), 
						(newBounds.width), heightOfEditTextPath);
				editTextPath.changeBounds(boundsOfDocumentPath);
			}
		}
		
		/** 영역만 잡아주고 menuClassAndMemberList 내용(Button[])은 나중에 createAndSetFileListButtons를
		 * 통해 넣어준다. 
		 * @param dir
		 * @param curDir
		 * @param category
		 */
		private void createMenuListAndEditTextPath() {
			Rectangle boundsOfmenuClassAndMemberList = new Rectangle(bounds);
			int heightOfEditTextPath =  (int) (bounds.height * 0.1f);
			boundsOfmenuClassAndMemberList.height = (bounds.height - heightOfEditTextPath);
			if (menuClassAndMemberList==null) {
				
				menuClassAndMemberList = new MenuWithScrollBar(this, boundsOfmenuClassAndMemberList, 
						buttonSize, 
						MenuWithScrollBar.ScrollMode.VScroll);
				menuClassAndMemberList.setOnTouchListener(this);				
			}
			if (editTextPath==null) {
				Rectangle boundsOfDocumentPath = new Rectangle(bounds.x, 
						menuClassAndMemberList.bounds.bottom(), 
						(this.bounds.width), heightOfEditTextPath);
				editTextPath = new EditText(false, false, this, "DocumentPath", boundsOfDocumentPath, 
						boundsOfDocumentPath.height*0.5f, true, 
						new CodeString("", Color.BLACK), 
						EditText.ScrollMode.Both, Color.WHITE);
				editTextPath.isReadOnly = true;
				editTextPath.setText(0, new CodeString(compiler.filename,editTextPath.textColor));
				
			}
		}
		
		public void setBackColor(int backColor) {
			menuClassAndMemberList.setBackColor(backColor);
		}
		
		/** FileListButtons 의 Pool을 활용하여 디렉토리를 바꿀 때마다 버튼들을 생성하지 않고 메모리를 절약한다.
		 * 즉 디렉토리를 바꾸면 버튼들을 새로 만드는 것이 아니라 pool에서 가져와서 버튼의 속성만 바꿔준다.
		 * (createFileListButtons참조)*/
		void createPoolOfFileListButtons() {
			if (poolOfFileListButtons==null) {
				poolOfFileListButtons = new PoolOfButton(50, buttonSize);
			}
		}
		
		void setContainerClassAndSrc(HighArray_CodeString src, FindClassParams containerClassParams) {
			this.src = src;
			//parentClassParams = containerClassParams;
			this.curClassParams = containerClassParams;
		}
		
		/** 클래스 여러 개를 Button[]으로 리턴한다.*/
		Button[] getMenuListButtons(HighArray_CodeString src2, ArrayListIReset listOfClass) {
			//try {	
			//this.curClassParams = classParams;
			this.listOfClass = listOfClass;
			if (listOfClass==null) return null;
			
			int countOfClasses = listOfClass.count;
			int buttonCount = countOfClasses + 1;
						
			int i;
			int buttonWidth = menuClassAndMemberList.originButtonWidth;
			int buttonHeight = menuClassAndMemberList.originButtonHeight;
			if (poolOfFileListButtons.list.capacity < buttonCount) {
				poolOfFileListButtons.setCapacity(buttonCount, buttonSize);
			}
			
			Button[] buttons = new Button[buttonCount];
			
			int color;	
			
			color = Color.BLUE;
			buttons[0] = (Button) poolOfFileListButtons.getItem(0);
			buttons[0].name = "Back";
			buttons[0].bounds.x = 0;
			buttons[0].bounds.y = 0;
			buttons[0].bounds.width = buttonWidth;
			buttons[0].bounds.height = buttonHeight;
			buttons[0].changeBounds(buttons[0].bounds);
			buttons[0].setBackColor(color);
			buttons[0].setText(buttons[0].name);
			
			int buttonIndex = 1; 
			for (i=0; i<listOfClass.count; i++, buttonIndex++) {
				color = Color.BLUE;
				buttons[buttonIndex] = (Button) poolOfFileListButtons.getItem(buttonIndex);
				buttons[buttonIndex].name = ((FindClassParams)listOfClass.getItem(i)).name;
				buttons[buttonIndex].bounds.x = 0;
				buttons[buttonIndex].bounds.y = 0;
				buttons[buttonIndex].bounds.width = buttonWidth;
				buttons[buttonIndex].bounds.height = buttonHeight;
				buttons[buttonIndex].changeBounds(buttons[buttonIndex].bounds);
				buttons[buttonIndex].setBackColor(color);
				buttons[buttonIndex].setText(buttons[buttonIndex].name);
				buttons[buttonIndex].addedInfo = (FindClassParams)listOfClass.getItem(i);
			}
						
			ButtonGroup group = new ButtonGroup(null, buttons);
			for (i=0; i<buttonCount; i++) {
				buttons[i].setGroup(group, i);
			}
			
			return buttons;
			
		}
		
		/** 클래스 하나에 있는 내부클래스들과 함수와 변수들을 Button[]으로 리턴한다.*/
		Button[] getMenuListButtons(Compiler compiler, HighArray_CodeString src2, FindClassParams classParams) {
			try {	
			this.curClassParams = classParams;
			
			int buttonCount = 1;
			int countOfChildClasses=0;
			if (classParams!=null) {
				countOfChildClasses = 
					(classParams.childClasses==null ? 0 : classParams.childClasses.count);
			
				buttonCount += (countOfChildClasses +  
						classParams.listOfVariableParams.count + 
						classParams.listOfFunctionParams.count);
								
				if (showsExtendsMember) {
					buttonCount += classParams.listOfVarParamsInherited==null ? 0 : classParams.listOfVarParamsInherited.count;
					buttonCount += classParams.listOfFunctionParamsInherited==null ? 0 : classParams.listOfFunctionParamsInherited.count;
				}
			}
			else {
				buttonCount += compiler.mlistOfAllFunctions.count;
			}
			
			int i;
			int buttonWidth = menuClassAndMemberList.originButtonWidth;
			int buttonHeight = menuClassAndMemberList.originButtonHeight;
			if (poolOfFileListButtons.list.capacity < buttonCount) {
				poolOfFileListButtons.setCapacity(buttonCount, buttonSize);
			}
			
			Button[] buttons = new Button[buttonCount];
			
			int color;	
			
			color = Color.BLUE;
			buttons[0] = (Button) poolOfFileListButtons.getItem(0);
			buttons[0].name = "Back";
			buttons[0].bounds.x = 0;
			buttons[0].bounds.y = 0;
			buttons[0].bounds.width = buttonWidth;
			buttons[0].bounds.height = buttonHeight;
			buttons[0].changeBounds(buttons[0].bounds);
			buttons[0].setBackColor(color);
			buttons[0].setText(buttons[0].name);
			
			int buttonIndex = 1;
			if (classParams!=null) {
				ArrayListIReset childClasses = classParams.childClasses; 
				if (childClasses!=null) {				
					for (i=0; i<childClasses.count; i++, buttonIndex++) {
						color = Color.BLUE;
						buttons[buttonIndex] = (Button) poolOfFileListButtons.getItem(buttonIndex);
						buttons[buttonIndex].name = ((FindClassParams)childClasses.getItem(i)).name;
						buttons[buttonIndex].bounds.x = 0;
						buttons[buttonIndex].bounds.y = 0;
						buttons[buttonIndex].bounds.width = buttonWidth;
						buttons[buttonIndex].bounds.height = buttonHeight;
						buttons[buttonIndex].changeBounds(buttons[buttonIndex].bounds);
						buttons[buttonIndex].setBackColor(color);
						buttons[buttonIndex].addedInfo = childClasses.getItem(i);
						buttons[buttonIndex].setText(buttons[buttonIndex].name);
					}
				}
				int k;
				int loopCount;
				if (this.showsExtendsMember) loopCount = 2;
				else loopCount = 1;
				for (k=0; k<loopCount; k++) {
					ArrayListIReset listFunctions;
					if (k==0) listFunctions = classParams.listOfFunctionParams;
					else  listFunctions = classParams.listOfFunctionParamsInherited;
					if (listFunctions!=null) {				
						for (i=0; i<listFunctions.count; i++, buttonIndex++) {
							color = Color.YELLOW;
							
							FindFunctionParams function = (FindFunctionParams)listFunctions.getItem(i);
							
							buttons[buttonIndex] = (Button) poolOfFileListButtons.getItem(buttonIndex);
							
							boolean isStatic = false;
							if (function.accessModifier!=null) isStatic = function.accessModifier.isStatic;
							String message = "";
							if (isStatic) message = "S:";
							
							if (function.isConstructor) message += "C:";
							
							if (k==1) {
								message += compiler.getShortName(((FindClassParams)function.parent).name) + " - ";
							}
							
							String name = null;
							if (function.hasReturnType()) {
								name = message + function.returnType + " " + function.name + "()";
							}
							else {
								if (function.isStaticBlock) name = message + function.name;
								else {
									name = message + function.name + "()";
								}
							}
							buttons[buttonIndex].name = name;
							buttons[buttonIndex].bounds.x = 0;
							buttons[buttonIndex].bounds.y = 0;
							buttons[buttonIndex].bounds.width = buttonWidth;
							buttons[buttonIndex].bounds.height = buttonHeight;
							buttons[buttonIndex].changeBounds(buttons[buttonIndex].bounds);
							String text = name;							
							buttons[buttonIndex].setBackColor(color);
							buttons[buttonIndex].addedInfo = function;
							buttons[buttonIndex].setText(text);
						}
					} // if (listFunctions!=null) {	
					
					ArrayListIReset listVars;
					if (k==0) listVars = classParams.listOfVariableParams;
					else  listVars = classParams.listOfVarParamsInherited;
					if (listVars!=null) {				
						for (i=0; i<listVars.count; i++, buttonIndex++) {						
							color = Color.LTGRAY;
							
							FindVarParams var = (FindVarParams)listVars.getItem(i);
							
							buttons[buttonIndex] = (Button) poolOfFileListButtons.getItem(buttonIndex);
							
							boolean isStatic = false;
							if (var.accessModifier!=null) isStatic = var.accessModifier.isStatic;
							String message = "";
							if (isStatic) message = "S:";
							
							if (k==1) {
								message += compiler.getShortName(((FindClassParams)var.parent).name) + " - ";
							}
							
							String name = null;
							if (var.isThis) {
								name = message + ((FindClassParams)var.parent).name + " " + "this";
							}
							else if (var.isSuper) {
								name = message + ((FindClassParams)var.parent).name + " " + "super";
							}
							else {
								name = message + var.typeName + " " + var.fieldName;
							}
							buttons[buttonIndex].name = name;
							buttons[buttonIndex].bounds.x = 0;
							buttons[buttonIndex].bounds.y = 0;
							buttons[buttonIndex].bounds.width = buttonWidth;
							buttons[buttonIndex].bounds.height = buttonHeight;
							buttons[buttonIndex].changeBounds(buttons[buttonIndex].bounds);
							buttons[buttonIndex].setBackColor(color);
							buttons[buttonIndex].addedInfo = var;
							String text = name;
							buttons[buttonIndex].setText(text);
						}
					} // if (listVars!=null) {
				}// for k
			} // if (classParams!=null) {
			else {
				ArrayListIReset listFunctions = compiler.mlistOfAllFunctions;
				if (listFunctions!=null) {				
					for (i=0; i<listFunctions.count; i++, buttonIndex++) {
						color = Color.YELLOW;
						
						FindFunctionParams function = (FindFunctionParams)listFunctions.getItem(i);
						
						buttons[buttonIndex] = (Button) poolOfFileListButtons.getItem(buttonIndex);
						
						boolean isStatic = false;
						if (function.accessModifier!=null) isStatic = function.accessModifier.isStatic;
						String message = "";
						if (isStatic) message = "S:";
						
						if (function.isConstructor) message += "C:";
						
						String name;
						if (function.hasReturnType()) {
							name = message + function.returnType + 
									" " + function.name + "()";
						}
						else {
							name = message + function.name + "()";
						}
						buttons[buttonIndex].name = name;
						buttons[buttonIndex].bounds.x = 0;
						buttons[buttonIndex].bounds.y = 0;
						buttons[buttonIndex].bounds.width = buttonWidth;
						buttons[buttonIndex].bounds.height = buttonHeight;
						buttons[buttonIndex].changeBounds(buttons[buttonIndex].bounds);
						buttons[buttonIndex].setBackColor(color);
						buttons[buttonIndex].addedInfo = function;
						String text = name;
						buttons[buttonIndex].setText(text);
					}
				} // if (listFunctions!=null) {
			}
			
			ButtonGroup group = new ButtonGroup(null, buttons);
			for (i=0; i<buttonCount; i++) {
				buttons[i].setGroup(group, i);
			}
			
			return buttons;
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				
				return null;
			}
		}
		
		@Override
	    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
	    	boolean r=false;
	    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
		    	r = super.onTouch(event, scaleFactor);
		    	if (textView!=null && textView.getHides()==false && 
		    			textView.onTouch(event, scaleFactor)==false) {
		    		textView.open(false);
		    	}
		    	if (!r) {
		    		open(false);	// 영역에 상관없이 닫힌다.
		    		return true;
		    	}
		    	
		    	r = buttonProblemList.onTouch(event, null);
		    	if (r) return true;
		    	
		    	r = buttonLogBird.onTouch(event, null);
		    	if (r) return true;
		    	
		    	r = buttonShowsExtends.onTouch(event, null);
		    	if (r) return true;
		    	
		    	if (menuClassAndMemberList!=null) {
		    		r = menuClassAndMemberList.onTouch(event, null);
		    		if (r) return true;
		    	}
		    	
		    	r = editTextPath.onTouch(event, null);
		    	if (r) return true;
		    	return true;
	    	}
	    	else 
	    		return false;
	    }
		
		/** src에서 nameIndex까지의 "\n"의 개수를 센다. 
		 * 주석안의 "\n"은 세고 인용부호안의 "\n"은 세지 않는다.*/
		int getCountOfNewLineChars(HighArray_CodeString src2, int nameIndex) {
			int i;
			int count = 0;
			//boolean isRefSymbol = false;
			//boolean containsNewLine = false;
			for (i=0; i<nameIndex; i++) {
				CodeString str = src2.getItem(i);
				CodeChar c = str.charAt(0);
				//isRefSymbol = (c.c=='\"');
				//if (isRefSymbol) continue;
				//containsNewLine = src2.getItem(i).toString().contains("\n");
				//if (containsNewLine) count++;
				if (c.c=='\n') count++;
			}
			return count;
		}

		/** listOfClass가 1보다 큰 경우(즉 파일하나에 중첩되지 않은 클래스가 여러 개 있으면, 루트라 이름붙인다.)
		 * 루트에서는  parentClassParams=null, curClassParams=null이다. 
		 * 1인 경우는 parentClassParams=첫번째 클래스, curClassParams=첫번째클래스이다.*/
		@Override		
		public void onTouchEvent(Object sender, MotionEvent e) {
			// TODO Auto-generated method stub
			try {
			if (sender instanceof Button) {
				Button button = (Button)sender;
				if (button.iName==buttonProblemList.iName) {
					//Compiler.menuProblemList_EditText.open(true);
					if (compiler.menuProblemList_EditText!=null)
						compiler.menuProblemList_EditText.open(true);
				}
				else if (button.iName==buttonLogBird.iName) {
					//compiler.menuProblemList.open(true);
					if (CommonGUI.textViewLogBird!=null)
						CommonGUI.textViewLogBird.setHides(false);
				}
				else if (button.iName==buttonShowsExtends.iName) {
					//compiler.menuProblemList.open(true);
					if (buttonShowsExtends.isSelected) this.showsExtendsMember = true;
					else this.showsExtendsMember = false;
					Button[] buttons;
					if (curClassParams!=null) {
						buttons = getMenuListButtons(compiler, src, curClassParams);
					}
					else {
						buttons = getMenuListButtons(src, listOfClass);
					}
					setButtons(buttons);
				}
			}
			else if (sender instanceof MenuWithScrollBar) {
							
				
				MenuWithScrollBar menu = (MenuWithScrollBar)sender;
				String nameOfButton = menu.selectedButtonName;
				
				if (nameOfButton==null) return;
				
				Object addedInfo = menu.selectedButton.addedInfo;
				
				if (nameOfButton.equals("Back")==true) {
					Button[] buttons;
					if (curClassParams==null) { // 현재는 파일
						CommonGUI.loggingForMessageBox.setText(true, "root class", false);
						CommonGUI.loggingForMessageBox.setHides(false);
						buttons = getMenuListButtons(compiler.mBuffer, listOfClass);
					}
					else {						
						FindClassParams parentClassParams = (FindClassParams)curClassParams.parent;
						if (parentClassParams!=null) { // parent클래스로 올라간다.
							buttons = getMenuListButtons(compiler, compiler.mBuffer, parentClassParams);
							curClassParams = parentClassParams;							
						}
						else { // 파일로 올라간다.
							buttons = getMenuListButtons(compiler.mBuffer, listOfClass);
							curClassParams = null; // 루트이므로 
						}
					}
					setButtons(buttons);
				}//back
				else {
					
					if (addedInfo instanceof FindClassParams) { // class
						FindClassParams childClass;
						/*if (curClassParams!=null) {
							childClass = findChildClass(curClassParams, nameOfButton);
						}
						else {
							childClass = findChildClass(listOfClass, nameOfButton);
						}*/
						childClass = (FindClassParams) addedInfo;
						if (childClass!=null) {
							//this.parentClassParams = curClassParams;
							this.curClassParams = childClass;
							Button[] buttons = getMenuListButtons(compiler, compiler.mBuffer, curClassParams);
							setButtons(buttons);
						}
					}
					else if (addedInfo instanceof FindFunctionParams) {
						FindFunctionParams func = (FindFunctionParams) addedInfo;
						if (e.actionCode==MotionEvent.ActionDown) {//메시지를 보여준다.
							CodeString message;
							if (func.isStaticBlock) {
								message = new CodeString(func.name, textColor);
							}
							else {
								message = compiler.findNode_func_makeString(compiler.mBuffer, func, func.functionNameIndex());
							}
							CommonGUI.loggingForMessageBox.setText(true, message.str, false);
							CommonGUI.loggingForMessageBox.setHides(false);
						}//if (e.actionCode==MotionEvent.ActionDown) {
						else if (e.actionCode==MotionEvent.ActionDoubleClicked) {//위치를 이동한다.
							showFunctionControlStructure(func);
						}//else if (e.actionCode==MotionEvent.ActionDoubleClicked) {
						
					}//else if (addedInfo instanceof FindFunctionParams) {
					else if (addedInfo instanceof FindVarParams) {
						FindVarParams var = (FindVarParams) addedInfo;
						if (e.actionCode==MotionEvent.ActionDown) {//메시지를 보여준다.
							CodeString message;
							/*if (func.isStaticBlock) {
								message = new CodeString(func.name, textColor);
							}
							else {
								message = compiler.findNode_func_makeString(compiler.mBuffer, func, func.functionNameIndex);
							}*/
							message = compiler.findNode_var_makeString(compiler.mBuffer, var, var.varNameIndex());
							CommonGUI.loggingForMessageBox.setText(true, message.str, false);
							CommonGUI.loggingForMessageBox.setHides(false);
						}//if (e.actionCode==MotionEvent.ActionDown) {
						else if (e.actionCode==MotionEvent.ActionDoubleClicked) {//위치를 이동한다.
							String varName;
							varName = var.fieldName;
							if (varName.equals("this")) return;
							FindClassParams parent = (FindClassParams) var.parent;
							
							if (parent.loadWayOfFindClassParams==LoadWayOfFindClassParams.ByteCode) {
								HighArray_CodeString mBuffer = compiler.mBuffer;
								compiler.setLanguage(Language.Java);
								// 바이트코드(클래스파일)일 경우 func 의 funcNameIndex는 -1이므로
								// mBuffer에서 func의 name으로 검색하여 일치하면 func의 args들을 찾아
								// 일치하는지 확인하여 funcNameIndex를 찾는다.
								int i;
								for (i=0; i<mBuffer.count; i++) {
									CodeString str = mBuffer.getItem(i);
									if (str.equals(varName)) {
										FindVarParams findVarParams = null;
										ReturnOfFindVarDecl r = compiler.FindVarDeclarationsAndVarUses(mBuffer, i, null, null);
										if (r!=null) {
											findVarParams = r.var;
										}
										if (findVarParams!=null && findVarParams.found) {
											findVarParams.typeName = compiler.getFullName(mBuffer, 
													findVarParams.typeStartIndex(), findVarParams.typeEndIndex()).str;
											findVarParams.fieldName = mBuffer.getItem(findVarParams.varNameIndex()).str;
											if (var.equals(findVarParams)) {
												var.varNameIndex = findVarParams.varNameIndex;
												break;
											}
										}
									}
								}//for (i=0; i<mBuffer.count; i++) {
								int indexOfVarName = var.varNameIndex();								
								countOfNewLineChars = getCountOfNewLineChars(compiler.mBuffer, indexOfVarName);
								// call listener
								callTouchListener(this, null);
							}//if (parent.loadWayOfFindClassParams==LoadWayOfFindClassParams.ByteCode) {
							else {
								int indexOfVarName = var.varNameIndex();
								
								countOfNewLineChars = getCountOfNewLineChars(compiler.mBuffer, indexOfVarName);
								
								// call listener
								callTouchListener(this, null);
							}
						}
					}
					
						
				}// back==false
				
			}//else if (sender instanceof MenuWithScrollBar) {
			}catch(Exception e1) {
				e1.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
			}
			
		}//onTouchEvent()
		
		
		void showFunctionControlStructure(FindFunctionParams func) {
			FindClassParams c = (FindClassParams)func.parent;							
			if (c.loadWayOfFindClassParams==LoadWayOfFindClassParams.ByteCode) {
				int i;
				compiler.setLanguage(Language.Java);
				HighArray_CodeString mBuffer = compiler.mBuffer;
				// 바이트코드(클래스파일)일 경우 func 의 funcNameIndex는 -1이므로
				// mBuffer에서 func의 name으로 검색하여 일치하면 func의 args들을 찾아
				// 일치하는지 확인하여 funcNameIndex를 찾는다.
				for (i=0; i<mBuffer.count; i++) {
					CodeString str = mBuffer.getItem(i);
					if (str.equals(func.name)) {
						FindFunctionParams func2 = new FindFunctionParams(compiler, -1, -1);
						compiler.FindFunction(mBuffer, c, func2, i+1);
						if (func2.found==true) {
							if (func.name.equals(func2.name)) {
								// i는 func의 name의 인덱스이고, i+1은 '('의 인덱스가 된다.
								int startIndex = i+1+1;
								int endIndex = CompilerHelper.CheckParenthesis(mBuffer, "(", ")", i+1, mBuffer.count-1, false)-1;
								int j;
								FindVarUseParams funcCall = new FindVarUseParams(IndexForHighArray.indexRelative(null, compiler.mBuffer, i));
								ArrayListIReset result = compiler.findFuncCallParams(mBuffer, startIndex-1, endIndex+1, funcCall);
								for (j=0; j<result.count; j++) {
									FindFuncCallParam param = (FindFuncCallParam) result.getItem(j);
									FindVarParams var = new FindVarParams(compiler, param.startIndex(), param.endIndex());
									var.typeName = compiler.getFullName(mBuffer, param.startIndex(), param.endIndex()).str;
									func2.listOfFuncArgs.add(var);
								}
								if (func.equals(func2)) {
									func.functionNameIndex = func2.functionNameIndex;
									break;
								}
							}
						}//if (func2.found==true) {
					}//if (str.equals("(")) {
				}//for (i=0; i<mBuffer.count; i++) {
				
				countOfNewLineChars = getCountOfNewLineChars(compiler.mBuffer, func.functionNameIndex());
				
				// call listener, editText내의 해당 함수로 이동한다.
				callTouchListener(this, null);
				
				
			}//if (c.loadWayOfFindClassParams==LoadWayOfFindClassParams.ByteCode) {
			else {
				if (func.isConstructor) {
					int indexOfFunctionName = func.functionNameIndex();
					
					countOfNewLineChars = getCountOfNewLineChars(compiler.mBuffer, indexOfFunctionName);
					
					// call listener, editText내의 해당 함수로 이동한다.
					callTouchListener(this, null);
					
					// 함수 내 제어블록(문)을 보여준다.
					FindFunctionParams function = func;
					ArrayListCodeChar r = getControlBlocksMessage(compiler.mBuffer, function, 0);
					
					CodeString message = null;
					if (r!=null) message = new CodeString(r.getItems(), r.count);
					textView.initCursorAndScrollPos();
					textView.setText(0, message);
					textView.setHides(false);
				}//if (func.isConstructor) {
				else {
					int indexOfFunctionName = func.functionNameIndex();
					
					countOfNewLineChars = getCountOfNewLineChars(compiler.mBuffer, indexOfFunctionName);
					
					// call listener, editText내의 해당 함수로 이동한다.
					callTouchListener(this, null);
					
					// 함수 내 제어블록(문)을 보여준다.
					FindFunctionParams function = func;
					ArrayListCodeChar r = getControlBlocksMessage(compiler.mBuffer, function, 0);
					
					CodeString message = null;
					if (r!=null) message = new CodeString(r.getItems(), r.count);
					textView.initCursorAndScrollPos();
					textView.setText(0, message);
					textView.setHides(false);
				}//if (func.isConstructor==false) {
			}
		}
		
		int findIndex(Compiler compiler, FindClassParams curClass, String name, boolean isFunction) {
			if (/*!compiler.hasPairError && */curClass==null) return -1;
			
				if (isFunction) {
					if (curClass.listOfFunctionParams==null) return -1;
					int i;
					for (i=0; i<curClass.listOfFunctionParams.count; i++) {
						FindFunctionParams function = (FindFunctionParams)curClass.listOfFunctionParams.getItem(i);
						if (function.functionNameIndex()==-1) continue;
						CodeString functionName = compiler.mBuffer.getItem(function.functionNameIndex());
						if (functionName.equals(name)) {
							return function.functionNameIndex();
						}
					}
				}
				else {	// 변수
					if (curClass.listOfVariableParams==null) return -1;
					int i;
					for (i=0; i<curClass.listOfVariableParams.count; i++) {
						FindVarParams var = (FindVarParams)curClass.listOfVariableParams.getItem(i);
						if (var.isThis) continue;
						if (var.isSuper) continue;
						if (var.varNameIndex()==-1) continue;
						CodeString varName = compiler.mBuffer.getItem(var.varNameIndex());
						if (varName.equals(name)) {
							return var.varNameIndex();
						}
					}				
				}
			//}
			return -1;
		}
		
		FindFunctionParams findFunctionParams(FindClassParams curClass, String name) {
			if (/*!compiler.hasPairError && */curClass==null) return null;
			
				if (curClass.listOfFunctionParams==null) return null;
				int i;
				for (i=0; i<curClass.listOfFunctionParams.count; i++) {
					FindFunctionParams function = (FindFunctionParams)curClass.listOfFunctionParams.getItem(i);
					if (function.functionNameIndex()==-1) return null;
					CodeString functionName = src.getItem(function.functionNameIndex());
					if (functionName.equals(name)) {
						return function;
					}
				}
			//}
			return null;
		}
		
		/** 재귀적 호출, 일:다 관계, 
		 * 함수 내의 모든 제어블록(문)들을 (자식 제어블록(문)을 포함하여) 검색한다.
		 * @param listOfControls : 처음 호출시에는 함수의 listOfControlBlocks를 이용하여 호출
		 * @return : 함수내의 모든 제어블록(문), FindControlBlockParams[] 
		 */
		public ArrayListIReset getControlBlocks(ArrayListIReset listOfControls) {
			if (listOfControls==null) return null;
			ArrayListIReset r = new ArrayListIReset(10);
			r.resizeInc = 10;
			int k;
			int i;
			for (k=0; k<listOfControls.count; k++) {
				FindControlBlockParams control = (FindControlBlockParams)listOfControls.getItem(k);
				r.add(control);
				
				// child 제어블럭(문)
				ArrayListIReset temp = getControlBlocks(control.listOfControlBlocks);
				if (temp==null) continue;
				for (i=0; i<temp.count; i++) {
					r.add(temp.getItem(i));
				}
			}
			return r;
		}
		
		
		/** 재귀적 호출, 일:다 관계, 
		 * 함수 내의 모든 제어블록(문)들을 (자식 제어블록(문)을 포함하여) 검색한다.
		 * @param listOfControls : 처음 호출시에는 함수의 listOfControlBlocks를 이용하여 호출
		 * @param countOfSpace : 들여쓰기 개수
		 * @return : 함수내의 모든 제어블록(문), FindControlBlockParams[] 
		 */
		public ArrayListCodeChar getControlBlocksMessage(HighArray_CodeString src, 
				Block block, int countOfSpace) {
			//if (listOfControls==null) return null;
			ArrayListCodeChar r = new ArrayListCodeChar(200);
			int k;
			int i;
			ArrayListIReset listOfControls = block.listOfControlBlocks;
			//ArrayListIReset listOfSpecialBlocks = block.listOfSpecialBlocks;
			ArrayListIReset list = new ArrayListIReset(listOfControls.count/*+listOfSpecialBlocks.count*/);
						
			for (i=0; i<listOfControls.count; i++) {
				list.add(listOfControls.getItem(i));
			}
			/*for (i=0; i<listOfSpecialBlocks.count; i++) {
				list.add(listOfSpecialBlocks.getItem(i));
			}*/
			ArrayListIReset newList = new ArrayListIReset(list.count);
			compiler.SortByIndex(list, newList);
			
			for (k=0; k<newList.count; k++) {
				Object o = newList.getItem(k);
				if (o instanceof FindControlBlockParams) {
					FindControlBlockParams control = (FindControlBlockParams)o;
					if (control.catOfControls!=null) {
						ArrayListCodeChar message = toCodeString(src, control, countOfSpace); 
						r.concate( new CodeString(message.getItems(), message.count) );
						//r.add(control);
						
						// child 제어블럭(문)을 위한 재귀적호출, 들여쓰기를 위해 2를 증가
						ArrayListCodeChar temp = getControlBlocksMessage(src, control, countOfSpace+2);
						if (temp==null) continue;
						for (i=0; i<temp.count; i++) {
							r.add(temp.getItem(i));
						}
					}
					else {
						FindSpecialBlockParams control2 = (FindSpecialBlockParams)o;
						ArrayListCodeChar message = toCodeString(src, control2, countOfSpace); 
						r.concate( new CodeString(message.getItems(), message.count) );
						//r.add(control);
						
						// child 제어블럭(문)을 위한 재귀적호출, 들여쓰기를 위해 2를 증가
						ArrayListCodeChar temp = getControlBlocksMessage(src, control2, countOfSpace+2);
						if (temp==null) continue;
						for (i=0; i<temp.count; i++) {
							r.add(temp.getItem(i));
						}
					}
				}
			}
			return r;
		}
		
		
		
		/** @param listOfControls : 일렬로 늘어진 FindControlBlockParams[]*/
		public ArrayListCodeChar toCodeString(HighArray_CodeString src, ArrayListIReset listOfControls) {
			int i;
			ArrayListCodeChar r = new ArrayListCodeChar(100); 
			for (i=0; i<listOfControls.count; i++) {
				//String message = null;
				FindControlBlockParams control = (FindControlBlockParams)listOfControls.getItem(i);
				if (control!=null) {
					r.concate( new CodeString((new CategoryOfControls(control.catOfControls.category)).toString(), Compiler.textColor) );
					if (control.indexOfLeftParenthesis()>=0 && control.indexOfRightParenthesis()>=0) {
						CodeString[] condition = src.substring(control.indexOfLeftParenthesis(), 
								control.indexOfRightParenthesis()-control.indexOfLeftParenthesis()+1);
						int j;
						for (j=0; j<condition.length; j++) {
							r.concate(condition[j]);
						}
					}
					r.concate(new CodeString("\n", textColor));
				}
			}
			return r;
		}
		
		/** 해당 제어블록(문)에 대한 메시지를 리턴한다.
		 * @param control : FindControlBlockParams
		 * @param countOfSpace : 들여쓰기 개수*/
		public ArrayListCodeChar toCodeString(HighArray_CodeString src, FindControlBlockParams control, int countOfSpace) {
			if (control!=null) {
				ArrayListCodeChar r = new ArrayListCodeChar(50);
				int i;
				for (i=0; i<countOfSpace; i++) {
					r.add(new CodeChar('-', Compiler.textColor));
				}
				boolean isDefault = false;
				if (control.catOfControls!=null && 
						control.catOfControls.category==CategoryOfControls.Control_case) {
					if (src.getItem(control.nameIndex()).equals("default"))
						isDefault = true;
				}
				if (isDefault==false) {
					r.concate( new CodeString(new CategoryOfControls(control.catOfControls.category).toString(), Compiler.keywordColor) );
				}
				else {
					r.concate(new CodeString("default", Compiler.keywordColor));
				}
				if (control.indexOfLeftParenthesis()>=0 && control.indexOfRightParenthesis()>=0) {
					CodeString[] condition = src.substring(control.indexOfLeftParenthesis(), 
							control.indexOfRightParenthesis()-control.indexOfLeftParenthesis()+1);
					int j;
					for (j=0; j<condition.length; j++) {
						r.concate(condition[j]);
					}
				}
				r.concate(new CodeString("\n", Compiler.textColor));
				return r;
			}
			return null;
		}
		
		/** 해당 제어블록(문)에 대한 메시지를 리턴한다.
		 * @param control : FindControlBlockParams
		 * @param countOfSpace : 들여쓰기 개수*/
		public ArrayListCodeChar toCodeString(HighArray_CodeString src, FindSpecialBlockParams control, int countOfSpace) {
			if (control!=null) {
				ArrayListCodeChar r = new ArrayListCodeChar(50);
				int i;
				for (i=0; i<countOfSpace; i++) {
					r.add(new CodeChar('-', Compiler.textColor));
				}
				r.concate( new CodeString(control.toString(), Compiler.keywordColor) );
				if (control.indexOfLeftParenthesis()>=0 && control.indexOfRightParenthesis()>=0) {
					CodeString[] condition = src.substring(control.indexOfLeftParenthesis(), 
							control.indexOfRightParenthesis()-control.indexOfLeftParenthesis()+1);
					int j;
					for (j=0; j<condition.length; j++) {
						r.concate(condition[j]);
					}
				}
				r.concate(new CodeString("\n", Compiler.textColor));
				return r;
			}
			return null;
		}
		
		
		
		FindClassParams findChildClass(FindClassParams curClass, String className) {
			if (curClass==null) return null;
			if (curClass.childClasses==null) return null;
			int i;			
			for (i=0; i<curClass.childClasses.count; i++) {
				FindClassParams item = (FindClassParams)curClass.childClasses.getItem(i);
				CodeString itemName = src.getItem(item.classNameIndex());
				if (itemName.equals(className)) {
					return item;
				}
			}
			return null;
			
		}
		
		FindClassParams findChildClass(ArrayListIReset listOfClass, String className) {
			if (listOfClass==null) return null;
						
			int i;			
			for (i=0; i<listOfClass.count; i++) {
				FindClassParams item = (FindClassParams)listOfClass.getItem(i);
				CodeString itemName = src.getItem(item.classNameIndex());
				if (itemName.equals(className)) {
					return item;
				}
			}
			return null;
			
		}
		
		@Override
		public void draw(Canvas canvas) {
			menuClassAndMemberList.draw(canvas);
			buttonProblemList.draw(canvas);
			buttonLogBird.draw(canvas);
			buttonShowsExtends.draw(canvas);
			editTextPath.draw(canvas);
		}

		public void setButtons(Button[] list) {
			// TODO Auto-generated method stub
			menuClassAndMemberList.setButtons(list);
		}
		
	}
	
	
	
	public static class MenuProblemList extends Container implements OnTouchListener 
	{
		MenuWithScrollBar menuProblemList;
		private PoolOfButton poolOfFileListButtons;
		Size buttonSize;
		public int countOfNewLineChars;
		
		Compiler compiler;
		
		public MenuProblemList(Compiler compiler, Rectangle bounds, Size buttonSize) {
			this.compiler = compiler;
			this.bounds = bounds;
			this.buttonSize = buttonSize;
			createMenuList();
			createPoolOfFileListButtons();
		}
		
		public void setBackColor(int backColor) {
			menuProblemList.setBackColor(backColor);
		}
		
		/** 영역만 잡아주고 menuClassAndMemberList 내용(Button[])은 나중에 createAndSetFileListButtons를
		 * 통해 넣어준다. 
		 * @param dir
		 * @param curDir
		 * @param category
		 */
		private void createMenuList() {
			if (menuProblemList==null) {
				menuProblemList = new MenuWithScrollBar(this, bounds, 
						buttonSize, 
						MenuWithScrollBar.ScrollMode.VScroll);
				menuProblemList.setBackColor(backColor);
				menuProblemList.setOnTouchListener(this); // MenuWithScrollbar의 listener				
			}
		}
		
		/** FileListButtons 의 Pool을 활용하여 디렉토리를 바꿀 때마다 버튼들을 생성하지 않고 메모리를 절약한다.
		 * 즉 디렉토리를 바꾸면 버튼들을 새로 만드는 것이 아니라 pool에서 가져와서 버튼의 속성만 바꿔준다.
		 * (createFileListButtons참조)*/
		void createPoolOfFileListButtons() {
			if (poolOfFileListButtons==null) {
				poolOfFileListButtons = new PoolOfButton(50, buttonSize);
			}
		}
		
		/*void setSrcAndErrors(HighArray_CodeString src, ArrayListIReset errors) {
		}*/
		
		/** errors를 Button[]으로 리턴한다.*/
		Button[] getMenuListButtons(HighArray_CodeString src2, ArrayListIReset errors) {
			if (errors==null) return null;
			
			int buttonCount = errors.count + 1;
						
			int i;
			int buttonWidth = menuProblemList.originButtonWidth;
			int buttonHeight = menuProblemList.originButtonHeight;
			if (poolOfFileListButtons.list.capacity < buttonCount) {
				poolOfFileListButtons.setCapacity(buttonCount, buttonSize);
			}
			
			Button[] buttons = new Button[buttonCount];
			
			int color;
			
			String textOfButton0;
			if (errors.count==0) {
				textOfButton0 = "No error";
			}
			else {
				textOfButton0 = "Back";
			}
			
			color = Color.BLUE;
			buttons[0] = (Button) poolOfFileListButtons.getItem(0);
			buttons[0].name = textOfButton0;
			buttons[0].bounds.x = 0;
			buttons[0].bounds.y = 0;
			buttons[0].bounds.width = buttonWidth;
			buttons[0].bounds.height = buttonHeight;
			buttons[0].setText(buttons[0].name);
			buttons[0].setBackColor(color);
			
			int buttonIndex = 1; 
			for (i=0; i<errors.count; i++, buttonIndex++) {
				color = Color.BLUE;
				buttons[buttonIndex] = (Button) poolOfFileListButtons.getItem(buttonIndex);
				Error error = (Error)errors.getItem(i);
				buttons[buttonIndex].name = Integer.toString(i); // errors에서의 index
				buttons[buttonIndex].bounds.x = 0;
				buttons[buttonIndex].bounds.y = 0;
				buttons[buttonIndex].bounds.width = buttonWidth;
				buttons[buttonIndex].bounds.height = buttonHeight;
				buttons[buttonIndex].setText(error.msg);
				buttons[buttonIndex].setBackColor(color);
			}
			
			ButtonGroup group = new ButtonGroup(null, buttons);
			for (i=0; i<buttonCount; i++) {
				buttons[i].setGroup(group, i);
			}
			
			return buttons;
			
		}
		
				
		@Override
	    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
	    	boolean r=false;
	    	if (event.actionCode==MotionEvent.ActionDown) {
		    	r = super.onTouch(event, scaleFactor);
		    	if (!r) {
		    		open(false);	// 영역에 상관없이 닫힌다.
		    		return true;
		    	}
		    	
		    	if (menuProblemList!=null) {
		    		r = menuProblemList.onTouch(event, null);
		    		if (r) return true;
		    	}
		    	return true;
	    	}
	    	else 
	    		return false;
	    }
		
		/** src에서 nameIndex까지의 "\n"의 개수를 센다. 
		 * 주석안의 "\n"은 세고 인용부호안의 "\n"은 세지 않는다.*/
		int getCountOfNewLineChars(HighArray_CodeString src2, int nameIndex) {
			int i;
			int count = 0;
			for (i=0; i<nameIndex; i++) {
				CodeString str = src2.getItem(i);
				CodeChar c = str.charAt(0);
				if (c.c=='\n') count++;
			}
			return count;
		}

		/** listOfClass가 1보다 큰 경우(즉 파일하나에 중첩되지 않은 클래스가 여러 개 있으면, 루트라 이름붙인다.)
		 * 루트에서는  parentClassParams=null, curClassParams=null이다. 
		 * 1인 경우는 parentClassParams=첫번째 클래스, curClassParams=첫번째클래스이다.*/
		@Override		
		public void onTouchEvent(Object sender, MotionEvent e) {
			// TODO Auto-generated method stub
			try {
			if (sender instanceof MenuWithScrollBar) {
				MenuWithScrollBar menu = (MenuWithScrollBar)sender;
				String nameOfButton = menu.selectedButtonName;
				if (nameOfButton==null) return;
				if (nameOfButton.equals("Back") || nameOfButton.equals("No error")) {					
					setHides(true);
				}
				else {
					if (menu.selectedButton==null) return;
					compiler.menuClassList.open(false);
					
					Button selectedButton = menu.selectedButton;
					int indexOfErrors = Integer.parseInt(selectedButton.name);
					int index = ((Error)compiler.errors.getItem(indexOfErrors)).startIndex();
					countOfNewLineChars = getCountOfNewLineChars(compiler.mBuffer, index);
					
					// call listener
					callTouchListener(this, null);
				}
			}
			}catch(Exception e1) {
				e1.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
			}
			
		}
		
				
		@Override
		public void draw(Canvas canvas) {
			menuProblemList.draw(canvas);
		}

		public void setButtons(Button[] list) {
			// TODO Auto-generated method stub
			menuProblemList.setButtons(list);
		}
		
	}
	
	public static class MenuProblemList_EditText extends Container implements OnTouchListener 
	{
		MenuWithScrollBar_EditText menuProblemList;
		private PoolOfEditText poolOfFileListEditTexts;
		Size buttonSize;
		public int countOfNewLineChars;
		public int countOfCol;
		
		Compiler compiler;
		public Error selectedError;
		
		public MenuProblemList_EditText(Compiler compiler, Rectangle bounds/*, Size buttonSize*/) {
			this.compiler = compiler;
			this.bounds = bounds;
			this.buttonSize = new Size((int)(view.getWidth()*0.75f),(int)(view.getHeight()*0.1f));
			//this.buttonSize = buttonSize;
			createMenuList();
			createPoolOfFileListEditTexts();
		}
		
		public void setBackColor(int backColor) {
			menuProblemList.setBackColor(backColor);
		}
		
		public void changeBounds(Rectangle newBounds) {
			this.bounds = newBounds;
			this.buttonSize.width = (int)(view.getWidth()*0.75f);
			this.buttonSize.height = (int)(view.getHeight()*0.1f);
			if (menuProblemList!=null) {
				menuProblemList.changeBounds(newBounds, buttonSize);
			}
		}
		
		/** 영역만 잡아주고 menuClassAndMemberList 내용(Button[])은 나중에 createAndSetFileListButtons를
		 * 통해 넣어준다. 
		 * @param dir
		 * @param curDir
		 * @param category
		 */
		private void createMenuList() {
			if (menuProblemList==null) {
				menuProblemList = new MenuWithScrollBar_EditText(this, bounds, 
						buttonSize, 
						MenuWithScrollBar_EditText.ScrollMode.VScroll);
				menuProblemList.setBackColor(backColor);
				menuProblemList.setOnTouchListener(this); // MenuWithScrollbar의 listener				
			}
		}
		
		/** FileListButtons 의 Pool을 활용하여 디렉토리를 바꿀 때마다 버튼들을 생성하지 않고 메모리를 절약한다.
		 * 즉 디렉토리를 바꾸면 버튼들을 새로 만드는 것이 아니라 pool에서 가져와서 버튼의 속성만 바꿔준다.
		 * (createFileListButtons참조)*/
		void createPoolOfFileListEditTexts() {
			if (poolOfFileListEditTexts==null) {
				poolOfFileListEditTexts = new PoolOfEditText(50);
			}
		}
		
		/*void setSrcAndErrors(HighArray_CodeString src, ArrayListIReset errors) {
		}*/
		
		/** errors를 EditText[]으로 리턴한다.*/
		EditText[] getMenuListEditTexts(HighArray_CodeString src2, ArrayListIReset errors) {
			if (errors==null) return null;
			
			int editTextCount = errors.count + 1;
						
			int i;
			int editTextWidth = menuProblemList.originEditTextWidth;
			int editTextHeight = menuProblemList.originEditTextHeight;
			if (poolOfFileListEditTexts.list.capacity < editTextCount) {
				poolOfFileListEditTexts.setCapacity(editTextCount);
			}
			
			EditText[] editTexts = new EditText[editTextCount];
			
			int color;
			
			String textOfEditText0;
			if (errors.count==0) {
				textOfEditText0 = "No error";
			}
			else {
				textOfEditText0 = "Back";
			}
			
			color = Color.BLUE;
			editTexts[0] = (EditText) poolOfFileListEditTexts.getItem(0);
			editTexts[0].name = textOfEditText0;			
			editTexts[0].bounds.x = 0;
			editTexts[0].bounds.y = 0;
			editTexts[0].bounds.width = editTextWidth;
			editTexts[0].bounds.height = editTextHeight;
			editTexts[0].setBackColor(color);
			editTexts[0].isReadOnly = true;
			editTexts[0].setIsSingleLine(true);
			//editTexts[0].changeBounds(editTexts[0].bounds);
			editTexts[0].setText(0, new CodeString(editTexts[0].name,editTexts[0].textColor));			
			
			
			
			int editTextIndex = 1; 
			for (i=0; i<errors.count; i++, editTextIndex++) {
				color = Color.WHITE;
				editTexts[editTextIndex] = (EditText) poolOfFileListEditTexts.getItem(editTextIndex);
				Error error = (Error)errors.getItem(i);
				editTexts[editTextIndex].name = Integer.toString(i); // errors에서의 index
				editTexts[editTextIndex].bounds.x = 0;
				editTexts[editTextIndex].bounds.y = 0;
				editTexts[editTextIndex].bounds.width = editTextWidth;
				editTexts[editTextIndex].bounds.height = editTextHeight;
				editTexts[editTextIndex].setBackColor(color);
				editTexts[editTextIndex].isReadOnly = true;
				editTexts[editTextIndex].setIsSingleLine(true);
				//editTexts[editTextIndex].changeBounds(editTexts[editTextIndex].bounds);
				editTexts[editTextIndex].setText(0, new CodeString(error.msg,editTexts[editTextIndex].textColor));				
				
			}
			
			/*EditTextGroup group = new EditTextGroup(null, editTexts);
			for (i=0; i<editTextCount; i++) {
				editTexts[i].setGroup(group, i);
			}*/
			
			return editTexts;
			
		}
		
				
		@Override
	    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
	    	boolean r=false;
	    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    		if (event.actionCode==MotionEvent.ActionDoubleClicked) {
	    			int a;
	    			a=0;
	    			a++;
	    		}
		    	r = super.onTouch(event, scaleFactor);
		    	if (!r) {
		    		open(false);	// 영역에 상관없이 닫힌다.
		    		return true;
		    	}
		    	
		    	if (menuProblemList!=null) {
		    		r = menuProblemList.onTouch(event, null);
		    		if (r) return true;
		    	}
		    	return true;
	    	}
	    	else 
	    		return false;
	    }
		
		/** src에서 nameIndex까지의 "\n"의 개수를 센다. 
		 * 주석안의 "\n"은 세고 인용부호안의 "\n"은 세지 않는다.*/
		int getCountOfNewLineChars(HighArray_CodeString src2, int nameIndex) {
			int i;
			int count = 0;
			for (i=0; i<nameIndex; i++) {
				CodeString str = src2.getItem(i);
				CodeChar c = str.charAt(0);
				if (c.c=='\n') count++;
			}
			return count;
		}
		
		/** src에서 nameIndex를 갖는 토큰의 EditText상에서의 줄과 열번호를 알아낸다. 
		 * 시작시는 isStartOrEnd가 true, 끝날때는 false이다.*/
		Point getPoint(HighArray_CodeString src2, int nameIndex, boolean isStartOrEnd) {
			if (nameIndex<0) {
				return null;
			}
			int i;
			int row = 0; 
			int col = 0;
			int indexInSrc2OfRow = 0;
			int indexInSrc2OfCol = 0;
			for (i=0; i<nameIndex; i++) {
				CodeString str = src2.getItem(i);
				if (str==null) {
					int a;
					a=0;
					a++;
				}
				CodeChar c = str.charAt(0);
				col++;
				if (c.c=='\n') {
					indexInSrc2OfRow = i+1;
					row++;
					col = 0;
				}
			}
			if (row==1984) {
				int a;
				a=0;
				a++;
			}
			for (i=indexInSrc2OfRow; i<nameIndex; i++) {
				int len = src2.getItem(i).str.length();
				indexInSrc2OfCol += len;
			} 
			if (isStartOrEnd) {
				return new Point(indexInSrc2OfCol, row);
			}
			else {
				return new Point(indexInSrc2OfCol+src2.getItem(nameIndex).str.length()-1, row);
			}
		}

		/** listOfClass가 1보다 큰 경우(즉 파일하나에 중첩되지 않은 클래스가 여러 개 있으면, 루트라 이름붙인다.)
		 * 루트에서는  parentClassParams=null, curClassParams=null이다. 
		 * 1인 경우는 parentClassParams=첫번째 클래스, curClassParams=첫번째클래스이다.*/
		@Override		
		public void onTouchEvent(Object sender, MotionEvent e) {
			// TODO Auto-generated method stub
			try {
			if (sender instanceof MenuWithScrollBar_EditText) {
				MenuWithScrollBar_EditText menu = (MenuWithScrollBar_EditText)sender;
				String nameOfEditText = menu.selectedEditTextName;
				if (nameOfEditText==null) return;
				if (nameOfEditText.equals("Back") || nameOfEditText.equals("No error")) {					
					setHides(true);
				}
				else {
					if (e.actionCode==MotionEvent.ActionDown || e.actionCode==MotionEvent.ActionMove) return;
					if (e.actionCode==MotionEvent.ActionDoubleClicked) {
						int a;
						a=0;
						a++;
					}
					if (menu.selectedEditText==null) return;
					compiler.menuClassList.open(false);
					
					EditText selectedEditText = menu.selectedEditText;
					int indexOfErrors = Integer.parseInt(selectedEditText.name);
					Error error = (Error)Compiler.errors.getItem(indexOfErrors);
					int indexStart = error.startIndex();
					int indexEnd = error.endIndex();
					Compiler document = error.compiler;
					if (indexStart==-1 || indexEnd==-1) {
						CommonGUI.loggingForMessageBox.setText(true, "Can't open the file " + document.filename, false);
						CommonGUI.loggingForMessageBox.setHides(false);
						Control.view.invalidate();
						return;
					}
					if (indexEnd==1984) {
						int a;
						a=0;
						a++;
					}
					Point p1 = getPoint(document.mBuffer, indexStart, true);
					Point p2 = getPoint(document.mBuffer, indexEnd, false);
					
					if (p1.y==154) {
						int a;
						a=0;
						a++;
					}
					
					countOfNewLineChars = p1.y;
					countOfCol = /*p2.x*/p1.x;
					this.selectedError = error;
					//countOfNewLineChars = getCountOfNewLineChars(compiler.mBuffer, indexStart);
					if (listener!=null && listener instanceof EditText) {
						EditText editText = (EditText) listener;
						editText.makeSelectIndices(true, p1, p2);
						editText.isSelecting = true;
					}
					
					// call listener
					callTouchListener(this, null);
				}
			}
			}catch(Exception e1) {
				e1.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
			}
			
		}
		
				
		@Override
		public void draw(Canvas canvas) {
			menuProblemList.draw(canvas);
		}

		public void setEditTexts(EditText[] list) {
			// TODO Auto-generated method stub
			menuProblemList.setEditTexts(list);
		}
		
	}
	
	
	
	/** bounds 외부를 터치하면 닫히는 EditText*/
	public static class TextView extends EditText {

		public TextView(boolean hasToolbarAndMenuFontSize,
				boolean isDockingOfToolbarFlexiable, Object owner, String name,
				Rectangle paramBounds, float fontSize, boolean isSingleLine,
				CodeString text, ScrollMode scrollMode, int backColor) {
			super(hasToolbarAndMenuFontSize, isDockingOfToolbarFlexiable, owner, name,
					paramBounds, fontSize, isSingleLine, text, scrollMode, backColor);
			// TODO Auto-generated constructor stub
		}
		
		@Override
	    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
	    	boolean r=false;
	    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
		    	r = super.onTouch(event, scaleFactor);
		    	if (!r) {
		    		open(false);	// 영역에 상관없이 닫힌다.
		    		return true;
		    	}
		    	else return true;
	    	}
	    	else if (event.actionCode==MotionEvent.ActionMove) {
	    		r = super.onTouch(event, scaleFactor);
	    		if (r) return true;
	    		return false;
	    	}
	    	else 
	    		return false;
	    }
				
	}
}