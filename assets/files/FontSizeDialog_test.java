package com.gsoft.common.gui;

import com.gsoft.common.ColorEx;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Dialog.EditableDialog;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.common.Compiler.CodeString;
import com.gsoft.common.R;

import android.graphics.Color;
import android.view.View;

public class FontSizeDialog_test extends EditableDialog implements OnTouchListener {

	int[] oldPixels = new int[(view.getWidth()+2) * (view.getHeight()+2)];
	
	RectangleF boundsExceptTitleBar;
	
	float scaleOfGapX = 0.05f;
	
	float scaleOfTitleBar = 0.15f;
	
	float scaleOfeditTextX = 0.5f;
	float scaleOfeditTextY = 0.4f;
	
	float scaleOfOKButtonX = (1-scaleOfGapX*3) / 2;
	float scaleOfOKButtonY = 0.2f;
	
	EditText editText;
		
	String errorMessage;
	
	
	OnTouchListener oldKeyboardListener;
	
	// 3은 gap개수
	float scaleOfGapY = (1-(scaleOfTitleBar+scaleOfeditTextY+scaleOfOKButtonY)) / 3;

	private IntegrationKeyboard keyboard = Control.keyboard;

	public String curText;
	
	public void changeBounds(RectangleF bounds) {
		this.bounds = bounds;
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		
		float width = bounds.width * scaleOfeditTextX;
		float height = bounds.height * scaleOfeditTextY;
		float x = bounds.x + bounds.width/2 - width/2;
		float y = bounds.y + heightTitleBar + heightOfGap;
		
		RectangleF boundsOfButtonOK = new RectangleF(x,y,width,height);
		x = boundsOfButtonOK.right() + widthOfGap;
		RectangleF boundsOfButtonCancel = new RectangleF(x,y,width,height);
		
		((Button)(controls[0])).changeBounds(boundsOfButtonOK);
		((Button)controls[1]).changeBounds(boundsOfButtonCancel);
	}
	
	public FontSizeDialog_test(View owner, RectangleF bounds) {
		super(owner, bounds);
		// TODO Auto-generated constructor stub
		this.bounds = bounds;
		heightTitleBar = (int) ((int)bounds.height*scaleOfTitleBar);
		
		createDrawingBuffer(view, true, null);
		
		this.isTitleBarEnable = true;
		this.Text = Control.res.getString(R.string.font_size_dialog);
		
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		int alpha = 255;
		
		int colorOfButton = ColorEx.darkerOrLighter(Color.WHITE, -100);
		
		float width = bounds.width * scaleOfeditTextX;
		float height = bounds.height * scaleOfeditTextY;
		float x = bounds.x + bounds.width/2 - width/2;
		float y = bounds.y + heightTitleBar + heightOfGap;
		
		// owner속성을 this로 해야 editText.owner 속성으로 키보드에서 EditableDialog를 최대화할 수 있다.
		RectangleF boundsOfEditText = new RectangleF(x, y,width,height);
		editText = new EditText(false, false, this, "EditText", boundsOfEditText, boundsOfEditText.height*0.6f, 
				true, new CodeString("30", Color.BLACK), 
				EditText.ScrollMode.VScroll, Color.WHITE);
		
		RectangleF boundsOfButtonOK = new RectangleF(x,y,width,height);
		x = boundsOfButtonOK.right() + widthOfGap;
		RectangleF boundsOfButtonCancel = new RectangleF(x,y,width,height);
		
		controls = new Button[2];
		controls[0] = new Button(owner, NameButtonOk, Control.res.getString(R.string.OK), 
				colorOfButton, boundsOfButtonOK, false, alpha, true, 0);
		controls[1] = new Button(owner, NameButtonCancel, Control.res.getString(R.string.cancel), 
				colorOfButton, boundsOfButtonCancel, false, alpha, true, 0);
		// 이벤트를 이 클래스에서 직접 처리
		(controls[0]).setOnTouchListener(this);
		controls[1].setOnTouchListener(this);
	}
	
	/** open*/
	public void open(OnTouchListener listener) {
		setOnTouchListener(listener);
		errorMessage = null;
		if (keyboard!=null) {
			WasKeyboardHiddenBeforeOpen = keyboard.getHides();
			oldKeyboardListener = keyboard.backUp();
			keyboard.setOnTouchListener(editText);
			if (keyboard.getHides()==true) {
				keyboard.setHides(false);	// 키보드를 전면에 보이도록 한다.컨트롤스택 참조
			}
		}
		super.open(true);
	}

			
    
    @Override
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r;
    	if (event.actionCode==MotionEvent.ActionDown) {
	    	r = super.onTouch(event, scaleFactor);
	    	if (!r) return false;
	    	r = editText.onTouch(event, scaleFactor);
			if (r) {
				if (isMaximized()) {
					RectangleF newBounds = new RectangleF(bounds.x, bounds.y, 
							bounds.width, Control.view.getHeight()-Control.keyboard.bounds.height);
					changeBounds(newBounds);
					setHides(false);
					changeBoundsOfKeyboard(bounds);
					keyboard.setHides(false);
				}
	    		else {
	    			changeBoundsOfKeyboard(bounds);
	    			keyboard.setHides(false);
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
    
    /** cancel*/
    public void cancel() {
    	super.cancel();
    }
    
	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		
		
	}


}
