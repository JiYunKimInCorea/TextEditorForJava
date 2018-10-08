package com.gsoft.common.gui;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;

import com.gsoft.DataTransfer.pipe.Pipe;
import com.gsoft.common.ByteCode_Types;
import com.gsoft.common.Code.CodeChar;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.CommonGUI_SettingsDialog;
import com.gsoft.common.Compiler_types;
import com.gsoft.common.IO;
import com.gsoft.common.Compiler_types.Language;
import com.gsoft.common.Compiler_gui.MenuClassList;
import com.gsoft.common.Compiler_gui.MenuProblemList;
import com.gsoft.common.Compiler_gui.MenuProblemList_EditText;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.ReturnOfReadString;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Compiler;
import com.gsoft.common.CompilerHelper;
import com.gsoft.common.Compiler_types.HighArray_CodeString;
import com.gsoft.common.gui.Buttons.Button;

public class EditText_Compiler extends EditText {
	
	
	
	
	
	public EditText_Compiler(boolean hasToolbarAndMenuFontSize,
			boolean isDockingOfToolbarFlexiable, Object owner, String name,
			Rectangle paramBounds, float fontSize, boolean isSingleLine,
			CodeString text, EditText.ScrollMode scrollMode, int backColor) {
		super(hasToolbarAndMenuFontSize, isDockingOfToolbarFlexiable, owner, name,
				paramBounds, fontSize, isSingleLine, text, scrollMode, backColor);
		// TODO Auto-generated constructor stub
		CommonGUI.editText_compiler = this;
	}
	
	/** resetDocument()를 호출하여 문서가 바뀔때마다 메모리를 해제한다.*/
	public void initialize() {
		resetDocument();
		super.initialize();
	}
	
	public void changeBounds(Rectangle newBounds) {
		super.changeBounds(newBounds);
		if (compiler!=null) {
			compiler.changeBounds();
		}
	}
	
	@Override
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r=false;
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    	r = super.onTouch(event, scaleFactor);
	    	if (!r) {
	    		return false;
	    	}
	    	else return true;
    	}
    	else if (event.actionCode==MotionEvent.ActionMove || event.actionCode==MotionEvent.ActionUp) {
    		if (event.actionCode==MotionEvent.ActionUp) {
    		}
    		
			// drag시작이 scrollBar이면 scrollBar가, editText라면 editText가 핸들링
			if (capturedControl==this) {// 영역검사를 하지않고 영역을 벗어나더라도 자신이 핸들링한다.
				if (event.actionCode==MotionEvent.ActionMove) {
					if (this.compiler!=null) {
		    			Compiler.textViewExpressionTreeAndMessage.setHides(true);
		    		}
				}
				onTouchEvent(this, event);
				return true;
			}
			
						
		}
    	return false;
    }
	
	void toolbar_Listener(Object sender, String buttonName) {
		if (buttonName.equals("S")) {	// 툴바버튼
			EditText.Edit.menuFontSize.open(this, true);
			//menuFontSize.setOnTouchListener(this);
			return;
		}
		else if (buttonName.equals("M")) {
			EditText.ScrollMode scrollMode=this.scrollMode; 
			if (scrollMode==EditText.ScrollMode.VScroll) {
				scrollMode = EditText.ScrollMode.Both;
			}
			else {
				scrollMode = EditText.ScrollMode.VScroll;
			}
			this.setScrollMode(scrollMode);
			return;
		}
		else if (buttonName.equals("FN")) {
			EditText.Edit.menuFunction.open(this, true);
			//menuFunction.setOnTouchListener(this);
		}
		else if (buttonName.equals("O")) {
			if (compiler==null && CommonGUI.textViewLogBird!=null) {
				//Control.textViewLogBird.setHides(false);
				CommonGUI.textViewLogBird.open(true);
			}
			else if (compiler!=null && compiler.menuClassList!=null) {
				compiler.menuClassList.open(true);
				compiler.menuClassList.setOnTouchListener(this);
				//Compiler.menuProblemList_EditText.setOnTouchListener(this);
				if (compiler.menuProblemList_EditText!=null) {
					compiler.menuProblemList_EditText.setOnTouchListener(this);
				}
			}
			else if (compiler!=null && compiler.menuClassList==null) {
				CommonGUI.textViewLogBird.open(true);
			}
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
	
	/** .class 파일에서 커서 좌표의 워드를 찾는다.*/
	CodeString findWordInClassFile(int cursorPosX, int cursorPosY) {		
		CodeString line = this.textArray[cursorPosY];
		int left, right;
		int i;
		for (i=cursorPosX; i>=0; i--) {
			CodeChar c = line.charAt(i);
			if (c.c==' ' || c.c=='\b' || c.c=='\t' || c.c=='\r' || c.c=='\n') break;
		}
		//if (i<0) i = 0;
		left = i;
		
		for (i=cursorPosX; i<line.count; i++) {
			CodeChar c = line.charAt(i);
			if (c.c==' ' || c.c=='\b' || c.c=='\t' || c.c=='\r' || c.c=='\n') break;
		}
		//if (i>=line.count) i = line.count-1;
		right = i;
		
		CodeString r = null;
		if (left!=right) {
			r = line.substring(left+1, right);
			if (r.count>0 && r.charAt(r.count-1).c==';')
				r = r.substring(0, r.count-1);
		}
		return r;
	}
	
	
	
	
	
	public CodeString getCompileOutput(Language lang) {
		if (compiler!=null) return compiler.strOutput;
		return null;
	}
	
	
	public void setBackColor(int backColor) {
		super.setBackColor(backColor);
		
		if (compiler!=null) {
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
		}
		if (EditText.isTripleBuffering) this.drawToImage(mCanvas);
	}
	
	/** 문서가 바뀔때마다 compiler 가 사용하는 메모리를 해제한다. 
	 * Compiler.start2() 호출시 settings.usesClassCache 가 false 이면 클래스 캐시를 모두 해제한다.
	 * 클래스 캐시가 모두 해제될때 compiler 가 사용하는 메모리도 모두 해제된다.*/
	void resetDocument() {
		// 문서가 바뀔때마다 compiler 가 사용하는 메모리를 해제한다.
		if (CommonGUI_SettingsDialog.settings.usesClassCache==false) {
			if (this.compiler!=null) {
				this.compiler.destroy();
				this.compiler = null;
				System.gc();
			}
		}
		
		if (this.textArray!=null) {
			int i;
			for (i=0; i<textArray.length; i++) {
				if (textArray[i]!=null) {
					textArray[i].destroy();
					textArray[i] = new CodeString("", Compiler.textColor);
				}
			}
		}
	}
	
	
	/** 문서가 바뀔때마다 compiler 가 사용하는 메모리를 해제한다. 
	 * Compiler.start2() 호출시 settings.usesClassCache 가 false 이면 클래스 캐시를 모두 해제한다.
	 * 클래스 캐시가 모두 해제될때 compiler 가 사용하는 메모리도 모두 해제된다.*/
	public boolean setIsProgramCode(Compiler_types.Language lang, String filename, TextFormat format) {
		
		FileInputStream stream = null;
		BufferedInputStream bis = null;
		try {
			this.lang = lang;
			if (lang!=null) {
				
				if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
					if (Control.isMasterOrSlave==false) {
						//"rename"-numOfProcess(Control.numOfCurProcess)-바뀐 filePath
						// 순으로 보내진다.
						Pipe.renameProcess(Control.numOfCurProcess, filename);
						
						String input = null;
						if (lang==Compiler_types.Language.Java) {
							input = IO.readString(bis, format);
						}
						else {
							stream = new FileInputStream(filename);
							bis = new BufferedInputStream(stream);
							
							if (lang==Compiler_types.Language.Html)
								input = IO.readString(bis, format);
							else if (lang==Compiler_types.Language.Class) {
								input = IO.readString(bis, TextFormat.UTF_8);
							}
							else return false;
						}
						
						if (input==null) return false;
						
						this.compiler = new Compiler();
						compiler.start2(input, lang, backColor, filename);
						isModified = false;
						return true;
					}
					else {
						Pipe.createCompilerProcess(filename);
						return true;
					}
				}//if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
				else {
					
					
					String input = null;
					if (lang==Compiler_types.Language.Java) {
						//format = TextFormat.MS949_Korean; 
						
						//input = IO.readString(bis, format);
						ReturnOfReadString r = IO.readString(filename);
						input = r.result;
						format = r.textFormat;
					}
					else {
						stream = new FileInputStream(filename);
						bis = new BufferedInputStream(stream);
						
						if (lang==Compiler_types.Language.Html)
							input = IO.readString(bis, format);
						else if (lang==Compiler_types.Language.Class) {
							input = IO.readString(bis, TextFormat.UTF_8);
						}
						else return false;
					}
					
					if (input==null) return false;
					
					FileDialog fileDialog = CommonGUI.fileDialog;
					fileDialog.menuTextFormat.selectAll(false);
					if (format==TextFormat.UTF_8) {
						Button button = fileDialog.menuTextFormat.findByName("UTF-8");
						button.Select(true);
					}
					else if (format==TextFormat.UTF_16) {
						Button button = fileDialog.menuTextFormat.findByName("UTF-16");
						button.Select(true);
					}
					else if (format==TextFormat.MS949_Korean) {
						Button button = fileDialog.menuTextFormat.findByName("MS-949");
						button.Select(true);
					}
					
					this.compiler = new Compiler();
					compiler.start2(input, lang, backColor, filename);
					isModified = false;
					return true;
				}
			}
			return false;
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return false;
		}finally {
			if (bis!=null)
				try {
					bis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (stream!=null)
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
	}
	
	
	
	
	/** EditText의 이벤트 리스너, 이후에 draw가 호출된다*/
	void editText_Listener(Object sender, MotionEvent e) {
		try {
		super.editText_Listener(sender, e);
		
		if (e.actionCode==MotionEvent.ActionDown) {
			if (lang==Language.Java || lang==Language.C) {
				//
				if (compiler==null) return;				
				HighArray_CodeString mBuffer = compiler.mBuffer;
				indexInmBuffer = findWord(cursorPos.x,cursorPos.y);
				if (indexInmBuffer!=-1) {
					CodeString str = mBuffer.getItem(indexInmBuffer);
					CodeString message = null;
					if (str!=null) {
						if (str.equals("{") || str.equals("}")) {
							message = compiler.findParenthesis(mBuffer, indexInmBuffer);
						}
						else {
							message = compiler.findNode(mBuffer, indexInmBuffer);
						}
						if (message!=null && message.count!=0) {														
							Compiler.textViewExpressionTreeAndMessage.initCursorAndScrollPos();
							Compiler.textViewExpressionTreeAndMessage.setText(0, message);
							Compiler.textViewExpressionTreeAndMessage.setHides(false);							
						}
					}
				}
			}//if (lang==Language.Java || lang==Language.C) {
			else if (lang==Language.Class) {
				//CodeString word = findWordInClassFile(cursorPos.x, cursorPos.y);
				CodeString word = null;
				if (compiler==null) return;				
				HighArray_CodeString mBuffer = compiler.mBuffer;
				int indexInmBuffer = findWord(cursorPos.x,cursorPos.y);
				if (indexInmBuffer!=-1) {
					word = mBuffer.getItem(indexInmBuffer);
				}
				if (word!=null) {
					int i;
					boolean found = false;
					ByteCode_Types.ByteCodeInstruction instruction = null;
					for (i=0; i<ByteCode_Types.instructionSet.length; i++) {
						instruction = ByteCode_Types.instructionSet[i];
						if (instruction.mnemonic.equals(word.str)) {
							found = true;
							break;
						}
					}
					if (found) {
						Compiler.textViewExpressionTreeAndMessage.initCursorAndScrollPos();
						Compiler.textViewExpressionTreeAndMessage.setText(0, new CodeString(instruction.message, Compiler.textColor));
						Compiler.textViewExpressionTreeAndMessage.setHides(false);
					}
				}
			}
		}
		}catch(Exception e1) {
			//Log.e("EditText-OnTouchEvent ActionMove", e1.toString());
			e1.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e1);
			//return;
		}
		
		// scrollMode가 VScroll인지 Both인지는 상관이 없다. 
		// getCursorPos에서 처리하기 때문이다.
		/*try{
		paint.setTextSize(fontSize);
		
		if (e.actionCode==MotionEvent.ActionDown) {
			// 기존 선택한 텍스트 위치를 backup한다.
			if (isSelecting) {
				selectStartLine = selectIndices[0].y;
				selectEndLine = selectIndices[selectIndicesCount-2].y;
			}
						
			isFound = false;
			getCursorPos(e);
			//setToolbarAndCurState(cursorPos);
			isSelecting = false;
			this.selectLenY = 0;
			this.selectP1 = new Point(cursorPos.x,cursorPos.y);
			selectIndicesCount = 0;
			
			//if (preProcessor!=null) {
			if (lang==Language.Java || lang==Language.C) {
				int indexInmBuffer = -1;
				HighArray_CodeString mBuffer = compiler.mBuffer;
				indexInmBuffer = findWord(cursorPos.x,cursorPos.y);
				if (indexInmBuffer!=-1) {
					CodeString str = mBuffer.getItem(indexInmBuffer);
					CodeString message = null;
					if (str!=null) {
						if (str.equals("{") || str.equals("}")) {
							message = compiler.findParenthesis(mBuffer, indexInmBuffer);
						}
						else {
							message = compiler.findNode(mBuffer, indexInmBuffer);
						}
						if (message!=null && message.count!=0) {
														
							Compiler.textViewExpressionTreeAndMessage.initCursorAndScrollPos();
							Compiler.textViewExpressionTreeAndMessage.setText(0, message);
							Compiler.textViewExpressionTreeAndMessage.setHides(false);
							
						}
					}
				}
			}
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
		if (e.actionCode==MotionEvent.ActionMove) {
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
			isSelecting = true;
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
		}*/
	}
	
	public synchronized void draw(Canvas canvas) {
		super.draw(canvas);
		
		RectF rectBounds = bounds.toRectF();
		paintOfBorder.setColor(Compiler.keywordColor);
    	canvas.drawRect(rectBounds, paintOfBorder); 
	}
	
	
	public void onTouchEvent(Object sender, MotionEvent e) {
		super.onTouchEvent(sender, e);
		
		if (e==null) {
			// Compiler의 MenuProblemList_EditText의 오류EditText을 터치시 호출
			if (sender instanceof MenuProblemList_EditText) {
				MenuProblemList_EditText list = (MenuProblemList_EditText)sender;
				int countOfNewLineChars = list.countOfNewLineChars;
				list.open(false);
				
				cursorPos.x = 0;
				if (scrollMode==EditText.ScrollMode.VScroll) {
					cursorPos.y = getNumOfLines(0, countOfNewLineChars) + 1;
				}
				else {
					cursorPos.y = countOfNewLineChars;
					cursorPos.x = list.countOfCol;
				}
				
				vScrollPos = cursorPos.y;
				setVScrollPos();
				setVScrollBar();
				if (scrollMode==EditText.ScrollMode.Both) {
					//widthOfhScrollPos = cursorPos.x;
					setHScrollPosFromMenuProblemList();
					setHScrollBar();
					com.gsoft.common.Compiler_types.Error selectedError = list.selectedError;
					CommonGUI.loggingForMessageBox.setText(true, selectedError.msg, false);
					CommonGUI.loggingForMessageBox.open(true);
				}
				
			}
			else if (sender instanceof MenuClassList) {
				MenuClassList classList = (MenuClassList)sender;
				int countOfNewLineChars = classList.countOfNewLineChars;
				classList.open(false);
				
				cursorPos.x = 0;
				if (scrollMode==EditText.ScrollMode.VScroll) {
					cursorPos.y = getNumOfLines(0, countOfNewLineChars) + 1;
				}
				else {
					cursorPos.y = countOfNewLineChars;
				}
				vScrollPos = cursorPos.y;
				setVScrollPos();
				setVScrollBar();
				if (scrollMode==EditText.ScrollMode.Both) {
					widthOfhScrollPos = 0;
					setHScrollPos();
					setHScrollBar();
				}
				
			}
			// Compiler의 MenuProblemList의 오류버튼을 터치시 호출
			else if (sender instanceof MenuProblemList) {
				MenuProblemList list = (MenuProblemList)sender;
				int countOfNewLineChars = list.countOfNewLineChars;
				list.open(false);
				
				cursorPos.x = 0;
				if (scrollMode==EditText.ScrollMode.VScroll) {
					cursorPos.y = getNumOfLines(0, countOfNewLineChars) + 1;
				}
				else {
					cursorPos.y = countOfNewLineChars;
				}
				vScrollPos = cursorPos.y;
				setVScrollPos();
				setVScrollBar();
				if (scrollMode==EditText.ScrollMode.Both) {
					widthOfhScrollPos = 0;
					setHScrollPos();
					setHScrollBar();
				}
				
			}
			if (EditText.isTripleBuffering) this.drawToImage(mCanvas);
		}
	}
}