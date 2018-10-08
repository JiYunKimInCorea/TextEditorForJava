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
import com.gsoft.common.R.R;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class FontSizeDialog extends EditableDialog implements OnTouchListener {

	RectangleF boundsExceptTitleBar;
	
	float scaleOfGapX = 0.05f;
	
	float scaleOfTitleBar = 0.15f;
	
	float scaleOfeditTextX = 0.5f;
	float scaleOfeditTextY = 0.4f;
	
	float scaleOfOKButtonX = (1-scaleOfGapX*3) / 2;
	float scaleOfOKButtonY = 0.2f;
	
	EditText editText;
		
	String errorMessage;
	
	//boolean WasKeyboardHiddenBeforeOpen;
	
	OnTouchListener oldKeyboardListener;
	
	// 3은 gap개수
	float scaleOfGapY = (1-(scaleOfTitleBar+scaleOfeditTextY+scaleOfOKButtonY)) / 3;

	//private IntegrationKeyboard keyboard = CommonGUI.keyboard;

	public String curText;
	
	public void changeBounds(Rectangle bounds) {	
		
		this.bounds = bounds;
		if (isMaximized()==false) backUpBounds();
		
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		
		int width = (int) (bounds.width * scaleOfeditTextX);
		int height = (int) (bounds.height * scaleOfeditTextY);
		int x = bounds.x + bounds.width/2 - width/2;
		int y = bounds.y + heightTitleBar + heightOfGap;
		Rectangle boundsOfEditText = new Rectangle(x,y,width,height);
		editText.changeBounds(boundsOfEditText);
				
				
		width = (int) (bounds.width * scaleOfOKButtonX);
		height = (int) (bounds.height * scaleOfOKButtonY);
		x = bounds.x + widthOfGap;
		y = boundsOfEditText.bottom() + heightOfGap;
		Rectangle boundsOfButtonOK = new Rectangle(x,y,width,height);
		x = boundsOfButtonOK.right() + widthOfGap;
		Rectangle boundsOfButtonCancel = new Rectangle(x,y,width,height);
		
		((Button)controls[0]).changeBounds(boundsOfButtonOK);		
		((Button)controls[1]).changeBounds(boundsOfButtonCancel);
	}
	
	public FontSizeDialog(View owner, Rectangle bounds) {
		super(owner, bounds);
		// TODO Auto-generated constructor stub
		this.bounds = bounds;
		heightTitleBar = (int) (bounds.height*scaleOfTitleBar);
		
		
		this.isTitleBarEnable = true;
		this.Text = Control.res.getString(R.string.font_size_dialog);
		
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		int alpha = 255;
		
		int colorOfButton = ColorEx.darkerOrLighter(Color.WHITE, -100);
		
		int width = (int) (bounds.width * scaleOfeditTextX);
		int height = (int) (bounds.height * scaleOfeditTextY);
		int x = bounds.x + bounds.width/2 - width/2;
		int y = bounds.y + heightTitleBar + heightOfGap;
		
		// owner속성을 this로 해야 editText.owner 속성으로 키보드에서 EditableDialog를 최대화할 수 있다.
		Rectangle boundsOfEditText = new Rectangle(x, y,width,height);
		editText = new EditText(false, false, this, "EditText", boundsOfEditText, boundsOfEditText.height*0.6f, 
				true, new CodeString("3", Color.BLACK), 
				EditText.ScrollMode.VScroll, Color.WHITE);
				
		width = (int) (bounds.width * scaleOfOKButtonX);
		height = (int) (bounds.height * scaleOfOKButtonY);
		x = bounds.x + widthOfGap;
		y = boundsOfEditText.bottom() + heightOfGap;
		Rectangle boundsOfButtonOK = new Rectangle(x,y,width,height);
		x = boundsOfButtonOK.right() + widthOfGap;
		Rectangle boundsOfButtonCancel = new Rectangle(x,y,width,height);
		
		controls = new Button[2];
		controls[0] = new Button(owner, NameButtonOk, Control.res.getString(R.string.OK), 
				colorOfButton, boundsOfButtonOK, false, alpha, true, 0.0f, null, Color.CYAN);
		controls[1] = new Button(owner, NameButtonCancel, Control.res.getString(R.string.cancel), 
				colorOfButton, boundsOfButtonCancel, false, alpha, true, 0.0f, null, Color.CYAN);
		// 이벤트를 이 클래스에서 직접 처리
		controls[0].setOnTouchListener(this);
		controls[1].setOnTouchListener(this);
		
		if (isMaximized()==false) backUpBounds();
	}
	
	public void open(OnTouchListener listener) {
		setOnTouchListener(listener);
		errorMessage = null;
		if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
			if (keyboard!=null) {
				WasKeyboardHiddenBeforeOpen = keyboard.getHides();
				oldKeyboardListener = keyboard.backUp();
				keyboard.setOnTouchListener(editText);
				if (keyboard.getHides()==true) {
					keyboard.setHides(false);	// 키보드를 전면에 보이도록 한다.컨트롤스택 참조
				}
			}
		}
		super.open(true);
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
        editText.draw(canvas);
        if (errorMessage!=null) drawErrorMessage(canvas);
		}catch(Exception e) {
    		
    	}
		}
    }
	
	/** bounds가 바뀔 때 호출, setHides에서 호출*/
	public void backUpBounds() {
		if (prevSize!=null) prevSize.copy(bounds);
		else prevSize = new Rectangle(bounds);
	}
    
    @Override
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r;
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    	r = super.onTouch(event, scaleFactor);
	    	if (!r) return false;
	    	r = editText.onTouch(event, scaleFactor);
			if (r) {
				if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
					if (isMaximized()) {
						//RectangleF newBounds = new RectangleF(bounds.x, bounds.y, 
						//		bounds.width, Control.view.getHeight()-Control.keyboard.bounds.height);
						Rectangle newBounds = new Rectangle(bounds.x, bounds.y, bounds.width, prevSize.height);
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
				errorMessage = null;
				return true;
			}
			int i;
    		for (i=0; i<controls.length; i++) {
    			r = controls[i].onTouch(event, scaleFactor);
    			if (r) return true;
    		}
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
			if (button.iName==controls[0].iName)	// OK
            {             
				curText = editText.getText().str;
				float fontSize;
				try {
					fontSize = Float.parseFloat(curText);
					if (fontSize<1 || fontSize>60) {
						CommonGUI.loggingForMessageBox.setHides(false);
						CommonGUI.loggingForMessageBox.setText(true, "Only number(1<=fontSize<=60) is allowed", false);
						return;
					}
					// 숫자 입력일때만 call back으로 호출한다.
					if (listener!=null)
						listener.onTouchEvent(this, null);
				}
				catch(Exception e1) {
					CommonGUI.loggingForMessageBox.setHides(false);
					CommonGUI.loggingForMessageBox.setText(true, "Only number is allowed", false);
					return;
				}
					
									
				super.ok();				
            }
            else if (button.iName==controls[1].iName)	// Cancel
            {
            	cancel();
            }			
		}		
	}


}