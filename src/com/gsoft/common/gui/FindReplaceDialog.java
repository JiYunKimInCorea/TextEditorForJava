package com.gsoft.common.gui;

import com.gsoft.common.ColorEx;
import com.gsoft.common.CommonGUI_SettingsDialog;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Dialog.EditableDialog;
import com.gsoft.common.interfaces.OnTouchListener;

import android.graphics.Canvas;
import android.graphics.Color;


public class FindReplaceDialog  extends EditableDialog implements OnTouchListener {

	//boolean isMaximized;
	
	RectangleF boundsExceptTitleBar;
	
	float scaleOfGapX = 0.02f;
	
	float scaleOfTitleBar = 0.15f;
	
	float scaleOfeditTextFindX = 0.4f;
	float scaleOfeditTextFindY = 0.17f;
	
	float scaleOfeditTextReplaceWidthX = 0.4f;
	float scaleOfeditTextReplaceWidthY = 0.17f;
	
	float scaleOfOptionButtonX = (1-scaleOfGapX*5) / 4;
	float scaleOfOptionButtonY = 0.15f;
	
	float scaleOfCommandButtonX = (1-scaleOfGapX*5) / 4;
	float scaleOfCommandButtonY = 0.15f;
	
	Static staticFind;
	Static staticReplaceWith;
	
	EditText editTextFind;
	EditText editTextReplaceWith;
	
	// option 4개
	Button buttonDirection;
	Button buttonScope;
	
	Button buttonCaseSensitive;
	Button buttonWholeWord;
	
	// command 4개
	Button buttonFind;
	Button buttonReplace;
	Button buttonReplaceAll;
	Button buttonClose;
	
		
	String errorMessage;
	
	//boolean WasKeyboardHiddenBeforeOpen;
	
	OnTouchListener oldKeyboardListener;
	
	// 5은 gap개수
	float scaleOfGapY = (1-(scaleOfTitleBar+scaleOfeditTextFindY+
		scaleOfeditTextFindY+scaleOfOptionButtonY+scaleOfCommandButtonY)) / 5;

	//private IntegrationKeyboard keyboard = CommonGUI.keyboard;

	public String curText;
	
	public String recentCommand;
	
	/*public void setIsMaximized(boolean isMax) {
		this.isMaximized = isMax;
		if (!isMax) {
			scaleOfGapX = 0.05f;
	
			scaleOfTitleBar = 0.15f;
			
			scaleOfeditTextFindX = 0.5f;
			scaleOfeditTextFindY = 0.25f;
			
			scaleOfeditTextReplaceWidthX = 0.5f;
			scaleOfeditTextReplaceWidthY = 0.25f;
			
			scaleOfOKButtonX = (1-scaleOfGapX*3) / 2;
			scaleOfOKButtonY = 0.17f;
						
			// 4은 gap개수
			scaleOfGapY = (1-(scaleOfTitleBar+scaleOfeditTextFindY+
				scaleOfeditTextFindY+scaleOfOKButtonY)) / 4;

		}
		else {
		}
	}*/
	
	public void changeBounds(Rectangle bounds) {
		this.bounds = bounds;
		if (isMaximized()==false) backUpBounds();
		
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		
		int width = (int) (bounds.width * scaleOfeditTextFindX);
		int height = (int) (bounds.height * scaleOfeditTextFindY);
		int x = bounds.x + widthOfGap;
		int y = bounds.y + heightTitleBar + heightOfGap;
		
		Rectangle boundsOfStaticFind = new Rectangle(x,y,width,height);
		staticFind.changeBounds(boundsOfStaticFind);
		
		x = boundsOfStaticFind.right() + widthOfGap;
		Rectangle boundsOfEditTextFind = new Rectangle(x,y,width,height);
		editTextFind.changeBounds(boundsOfEditTextFind);
		editTextFind.setText(0, editTextFind.getText());
		
		x = bounds.x + widthOfGap;
		y = boundsOfEditTextFind.bottom() + heightOfGap;
		Rectangle boundsOfStaticReplaceWith = new Rectangle(x,y,width,height);
		staticReplaceWith.changeBounds(boundsOfStaticReplaceWith);
		
		x = boundsOfStaticReplaceWith.right() + widthOfGap;
		Rectangle boundsOfEditTextReplaceWith = new Rectangle(x,y,width,height);
		editTextReplaceWith.changeBounds(boundsOfEditTextReplaceWith);
		editTextReplaceWith.setText(0, editTextReplaceWith.getText());
				
				
		width = (int) (bounds.width * scaleOfOptionButtonX);
		height = (int) (bounds.height * scaleOfOptionButtonY);
		x = bounds.x + widthOfGap;
		y = boundsOfEditTextReplaceWith.bottom() + heightOfGap;
		Rectangle boundsOfbuttonDirection = new Rectangle(x,y,width,height);
		
		x = boundsOfbuttonDirection.right() + widthOfGap;
		Rectangle boundsOfbuttonScope = new Rectangle(x,y,width,height);
		
		x = boundsOfbuttonScope.right() + widthOfGap;
		Rectangle boundsOfbuttonCaseSensitive = new Rectangle(x,y,width,height);
		
		x = boundsOfbuttonCaseSensitive.right() + widthOfGap;
		Rectangle boundsOfbuttonWholeWord = new Rectangle(x,y,width,height);
		
		
		buttonDirection.changeBounds(boundsOfbuttonDirection);
		buttonScope.changeBounds(boundsOfbuttonScope);
		buttonCaseSensitive.changeBounds(boundsOfbuttonCaseSensitive);
		buttonWholeWord.changeBounds(boundsOfbuttonWholeWord);
		
		
		// command 3개
		//Button buttonFind;
		//Button buttonReplace;
		//Button buttonReplaceAll;
		//Button buttonClose;
		
		width = (int) (bounds.width * scaleOfCommandButtonX);
		height = (int) (bounds.height * scaleOfCommandButtonY);
		x = bounds.x + widthOfGap;
		y = boundsOfbuttonWholeWord.bottom() + heightOfGap;
		Rectangle boundsOfbuttonFind = new Rectangle(x,y,width,height);
		
		x = boundsOfbuttonFind.right() + widthOfGap;
		Rectangle boundsOfbuttonReplace = new Rectangle(x,y,width,height);
		
		x = boundsOfbuttonReplace.right() + widthOfGap;
		Rectangle boundsOfbuttonReplaceAll = new Rectangle(x,y,width,height);
		
		x = boundsOfbuttonReplaceAll.right() + widthOfGap;
		Rectangle boundsOfbuttonClose = new Rectangle(x,y,width,height);
		
		buttonFind.changeBounds(boundsOfbuttonFind);
		buttonReplace.changeBounds(boundsOfbuttonReplace);
		buttonReplaceAll.changeBounds(boundsOfbuttonReplaceAll);
		buttonClose.changeBounds(boundsOfbuttonClose);
		
	}
	
	public FindReplaceDialog(Rectangle bounds) {
		super(Control.view, bounds);
		// TODO Auto-generated constructor stub
		this.bounds = bounds;
		heightTitleBar = (int) (bounds.height*scaleOfTitleBar);
	
		
		this.isTitleBarEnable = true;
		//this.Text = Control.res.getString(R.string.font_size_dialog);
		this.Text = "Find/Replace";
		
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		int alpha = 255;
		
		int colorOfButton = ColorEx.darkerOrLighter(Color.WHITE, -100);
		
		int width = (int)(bounds.width * scaleOfeditTextFindX);
		int height = (int)(bounds.height * scaleOfeditTextFindY);
		int x = bounds.x + widthOfGap;
		int y = bounds.y + heightTitleBar + heightOfGap;
		
		Rectangle boundsOfStaticFind = new Rectangle(x,y,width,height);
		staticFind = new Static(owner, "Find", textColor, boundsOfStaticFind);
		
		// owner속성을 this로 해야 editText.owner 속성으로 키보드에서 EditableDialog를 최대화할 수 있다.
		x = boundsOfStaticFind.right() + widthOfGap;
		Rectangle boundsOfEditTextFind = new Rectangle(x,y,width,height);
		editTextFind = new EditText(false, false, this, "EditText", boundsOfEditTextFind, boundsOfEditTextFind.height*0.6f, 
				true, new CodeString("", Color.BLACK), 
				EditText.ScrollMode.VScroll, Color.WHITE);
		
		
		
		x = bounds.x + widthOfGap;
		y = boundsOfEditTextFind.bottom() + heightOfGap;
		Rectangle boundsOfStaticReplaceWith = new Rectangle(x,y,width,height);
		staticReplaceWith = new Static(owner, "ReplaceWith", textColor, boundsOfStaticReplaceWith);
		
		// owner속성을 this로 해야 editText.owner 속성으로 키보드에서 EditableDialog를 최대화할 수 있다.
		x = boundsOfStaticReplaceWith.right() + widthOfGap;
		Rectangle boundsOfEditTextReplaceWith = new Rectangle(x,y,width,height);
		editTextReplaceWith = new EditText(false, false, this, "EditText", boundsOfEditTextReplaceWith, boundsOfEditTextReplaceWith.height*0.6f, 
				true, new CodeString("", Color.BLACK), 
				EditText.ScrollMode.VScroll, Color.WHITE);
		
		
		// option 4개
		//Button buttonDirection;
		//Button buttonScope;
		
		//Button buttonCaseSensitive;
		//Button buttonWholeWord;
		
		// command 3개
		//Button buttonFind;
		//Button buttonReplace;
		//Button buttonReplaceAll;
			
				
		width = (int) (bounds.width * scaleOfOptionButtonX);
		height = (int) (bounds.height * scaleOfOptionButtonY);
		x = bounds.x + widthOfGap;
		y = boundsOfEditTextReplaceWith.bottom() + heightOfGap;
		Rectangle boundsOfbuttonDirection = new Rectangle(x,y,width,height);
		
		x = boundsOfbuttonDirection.right() + widthOfGap;
		Rectangle boundsOfbuttonScope = new Rectangle(x,y,width,height);
		
		x = (int) (boundsOfbuttonScope.right() + widthOfGap);
		Rectangle boundsOfbuttonCaseSensitive = new Rectangle(x,y,width,height);
		
		x = (int) (boundsOfbuttonCaseSensitive.right() + widthOfGap);
		Rectangle boundsOfbuttonWholeWord = new Rectangle(x,y,width,height);
		
		
		buttonDirection = new Button(owner, "Direction", "Forward", 
				colorOfButton, boundsOfbuttonDirection, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonScope = new Button(owner, "Scope", "All", 
				colorOfButton, boundsOfbuttonScope, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonCaseSensitive = new Button(owner, "Case", "Case sensitive", 
				colorOfButton, boundsOfbuttonCaseSensitive, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonWholeWord = new Button(owner, "WholeWord", "Find by whole word", 
				colorOfButton, boundsOfbuttonWholeWord, false, alpha, true, 0.0f, null, Color.CYAN);
				
		buttonCaseSensitive.selectable = true;	// buttonCaseSensitive는 토글로 동작한다.
		buttonCaseSensitive.toggleable = true;
		buttonCaseSensitive.ColorSelected = Color.YELLOW;
		buttonCaseSensitive.isSelected = true;
		
		buttonWholeWord.selectable = true;	// buttonWholeWord는 토글로 동작한다.
		buttonWholeWord.toggleable = true;
		buttonWholeWord.ColorSelected = Color.YELLOW;
		buttonWholeWord.isSelected = false;
		
		
		
		// command 3개
		//Button buttonFind;
		//Button buttonReplace;
		//Button buttonReplaceAll;
		
		width = (int) (bounds.width * scaleOfCommandButtonX);
		height = (int) (bounds.height * scaleOfCommandButtonY);
		x = bounds.x + widthOfGap;
		y = boundsOfbuttonWholeWord.bottom() + heightOfGap;
		Rectangle boundsOfbuttonFind = new Rectangle(x,y,width,height);
		
		x = boundsOfbuttonFind.right() + widthOfGap;
		Rectangle boundsOfbuttonReplace = new Rectangle(x,y,width,height);
		
		x = boundsOfbuttonReplace.right() + widthOfGap;
		Rectangle boundsOfbuttonReplaceAll = new Rectangle(x,y,width,height);
		
		x = boundsOfbuttonReplaceAll.right() + widthOfGap;
		Rectangle boundsOfbuttonClose = new Rectangle(x,y,width,height);
		
		buttonFind = new Button(owner, "Find", "Find Next", 
				colorOfButton, boundsOfbuttonFind, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonReplace = new Button(owner, "Replace-Find", "Replace-Find", 
				colorOfButton, boundsOfbuttonReplace, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonReplaceAll = new Button(owner, "ReplaceAll", "ReplaceAll", 
				colorOfButton, boundsOfbuttonReplaceAll, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonClose = new Button(owner, "Close", "Close", 
				colorOfButton, boundsOfbuttonClose, false, alpha, true, 0.0f, null, Color.CYAN);		
		// 이벤트를 이 클래스에서 직접 처리
		buttonFind.setOnTouchListener(this);
		buttonReplace.setOnTouchListener(this);
		buttonReplaceAll.setOnTouchListener(this);
		buttonClose.setOnTouchListener(this);
		
		if (isMaximized()==false) backUpBounds();
	}
	
	@Override
	public void open(OnTouchListener listener, boolean isOpen) {
		if (isOpen) {
			setOnTouchListener(listener);
			errorMessage = null;
			if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
				if (keyboard!=null) {
					WasKeyboardHiddenBeforeOpen = keyboard.hides;
					oldKeyboardListener = keyboard.backUp();
					keyboard.setOnTouchListener(editTextFind);
					if (keyboard.getHides()==true) {
						keyboard.setHides(false);	// 키보드를 전면에 보이도록 한다.컨트롤스택 참조
					}
				}
			}
			super.open(true);
		}
		else {
			super.open(false);
		}
	}
	
	public void setHides(boolean hides) {
		open(!hides);
	}

	void drawErrorMessage(Canvas canvas) {
		float x, y;
		float w = paint.measureText(errorMessage);
		x = bounds.x + bounds.width/2 - w/2;
		y = bounds.y + bounds.height/2 - paint.getTextSize()/2;
		canvas.drawText(errorMessage, x, y, paint);
	}
	
	public void draw(Canvas canvas)
    {
		if (hides) return;
		synchronized(this) {
		try{
        super.draw(canvas);
        staticFind.draw(canvas);
        staticReplaceWith.draw(canvas);
        
        editTextFind.draw(canvas);
        editTextReplaceWith.draw(canvas);
        
        buttonDirection.draw(canvas);
		buttonScope.draw(canvas);		
		buttonCaseSensitive.draw(canvas);
		buttonWholeWord.draw(canvas);
		
		buttonFind.draw(canvas);
		buttonReplace.draw(canvas);		
		buttonReplaceAll.draw(canvas);
		buttonClose.draw(canvas);
		
        if (errorMessage!=null) drawErrorMessage(canvas);
		}catch(Exception e) {
    		
    	}
		}
    }
	
	public void setScope(boolean isAll) {
		if (isAll) {
			buttonScope.setText("All");
		}
		else {
			buttonScope.setText("Selected lines");
		}
	}
		
    
    @Override
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r;
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    	r = super.onTouch(event, scaleFactor);
	    	if (!r) return false;
	    	r = editTextFind.onTouch(event, scaleFactor);
			if (r) {
				if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
					if (isMaximized()) {
						Rectangle newBounds = new Rectangle(/*bounds.x, bounds.y,*/0, 0, bounds.width, /*prevSize.height*/(int)(view.getHeight()*0.5f));
						changeBounds(newBounds);
						setHides(false);
						changeBoundsOfKeyboard(bounds);
						keyboard.setHides(false);
					}
		    		else {
		    			changeBoundsOfKeyboard(bounds);
		    			keyboard.setHides(false);
		    		}
				}
				return true;
			}
			
			r = editTextReplaceWith.onTouch(event, scaleFactor);
			if (r) {
				if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
					if (isMaximized()) {
						Rectangle newBounds = new Rectangle(/*bounds.x, bounds.y,*/0,0, bounds.width, /*prevSize.height*/(int)(view.getHeight()*0.5f));
						changeBounds(newBounds);
						setHides(false);
						changeBoundsOfKeyboard(bounds);
						keyboard.setHides(false);
					}
		    		else {
		    			changeBoundsOfKeyboard(bounds);
		    			keyboard.setHides(false);
		    		}
				}
				return true;
			}
			
			r = buttonDirection.onTouch(event, scaleFactor);
			if (r) {
				if (buttonDirection.text.equals("Forward")) {
					buttonDirection.setText("Backward");
				}
				else {
					buttonDirection.setText("Forward");
				}
				return true;
			}
			r = buttonScope.onTouch(event, scaleFactor);
			if (r) {
				if (buttonScope.text.equals("All")) {
					buttonScope.setText("Selected lines");
				}
				else {
					buttonScope.setText("All");
				}
				return true;
			}
			r = buttonCaseSensitive.onTouch(event, scaleFactor);
			if (r) return true;
			r = buttonWholeWord.onTouch(event, scaleFactor);
			if (r) return true;
			
			r = buttonFind.onTouch(event, scaleFactor);
			if (r) return true;
			r = buttonReplace.onTouch(event, scaleFactor);
			if (r) return true;
			r = buttonReplaceAll.onTouch(event, scaleFactor);
			if (r) return true;
			r = buttonClose.onTouch(event, scaleFactor);
			if (r) return true;
				
			/*int i;
    		for (i=0; i<controls.length; i++) {
    			r = controls[i].onTouch(event, scaleFactor);
    			if (r) return true;
    		}*/
	    	return true;
    	}
    	else return false;
    }   
    
    public void cancel() {
    	super.cancel();
    }
    
    
    
	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		
		
		if (sender instanceof Button) {
			Button button = (Button)sender;
			if (button.iName==buttonFind.iName)	// find
            {
            	recentCommand = "Find";
				curText = editTextFind.getText().str;
				if (curText==null || curText.equals("")) {
					CommonGUI.loggingForMessageBox.setHides(false);
					CommonGUI.loggingForMessageBox.setText(true, "Input text.", false);
					return;
				}
									
				super.ok();
				
				callTouchListener(this,null);
				CommonGUI.keyboard.listener = this.listener;
            }
            else if (button.iName==buttonReplace.iName)	// Replace
            {
            	recentCommand = "Replace-Find";
            	callTouchListener(this,null);
            	cancel();
            	CommonGUI.keyboard.listener = this.listener;
            }			
            else if (button.iName==buttonReplaceAll.iName)	// ReplaceAll
            {
            	recentCommand = "ReplaceAll";
            	callTouchListener(this,null);
            	cancel();
            	CommonGUI.keyboard.listener = this.listener;
            }
            else if (button.iName==buttonClose.iName)	// Close
            {
            	//editTextFind.setText(0,new CodeString("", editTextFind.textColor));
            	//editTextReplaceWith.setText(0,new CodeString("", editTextReplaceWith.textColor));
            	recentCommand = "Close";
            	callTouchListener(this,null);
            	cancel();
            	CommonGUI.keyboard.listener = this.listener;
            }
		}	// if (sender instanceof Button) {
	}


}