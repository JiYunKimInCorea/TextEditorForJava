package com.gsoft.common.gui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import com.gsoft.common.ColorEx;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.common.R.R;

public class MessageDialog extends Dialog implements OnTouchListener {

	RectangleF boundsExceptTitleBar;
	
	float scaleOfGapX = 0.05f;
	
	float scaleOfTitleBar = 0.15f;
	
	float scaleOfbuttonRefreshX = 0.2f;
	float scaleOfbuttonRefreshY = 0.1f;
	
	float scaleOfeditTextX = 0.8f;
	float scaleOfeditTextY = 0.4f + scaleOfbuttonRefreshY;	
	
	float scaleOfOKButtonX = (1-scaleOfGapX*3) / 2;
	float scaleOfOKButtonY = 0.1f;
	
	public EditText editText;
	
	Button buttonRefresh;
	
	//boolean WasKeyboardHiddenBeforeOpen;
	
	OnTouchListener oldKeyboardListener;
	
	// 3은 gap개수
	float scaleOfGapY = ((1-scaleOfTitleBar)-(scaleOfeditTextY+scaleOfOKButtonY)) / 3;

	public void changeBounds(Rectangle bounds) {
		this.bounds = bounds;
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		
		int width = (int) (bounds.width * scaleOfbuttonRefreshX);
		int height = (int) (bounds.height * scaleOfbuttonRefreshY);
		int x = bounds.x + bounds.width/2 - width/2;
		int y = bounds.y + heightTitleBar + heightOfGap;
		Rectangle boundsOfbuttonRefresh = new Rectangle(x,y,width,height);
		buttonRefresh = new Button(owner, "Refresh", "Refresh", 
				Color.GREEN, boundsOfbuttonRefresh, true, alpha, true, 0.0f, null, Color.CYAN);
		
		// editText를 buttonRefresh의 바로 밑에 만든다. 
		width = (int) (bounds.width * scaleOfeditTextX);
		height = (int) (bounds.height * (scaleOfeditTextY-scaleOfbuttonRefreshY));
		x = bounds.x + bounds.width/2 - width/2;
		y = boundsOfbuttonRefresh.bottom()/* + heightOfGap*/;
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
	
	public MessageDialog(View owner, Rectangle bounds) {
		super(owner, bounds);
		// TODO Auto-generated constructor stub
		this.bounds = bounds;
		
		heightTitleBar = (int) (bounds.height*scaleOfTitleBar);
		
		
		this.isTitleBarEnable = true;
		this.Text = Control.res.getString(R.string.message_dialog);
		
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		int alpha = 255;
		
		int colorOfButton = ColorEx.darkerOrLighter(Color.WHITE, -100);
		
		
		int width = (int) (bounds.width * scaleOfbuttonRefreshX);
		int height = (int) (bounds.height * scaleOfbuttonRefreshY);
		int x = bounds.x + bounds.width/2 - width/2;
		int y = bounds.y + heightTitleBar + heightOfGap;		
		Rectangle boundsOfbuttonRefresh = new Rectangle(x,y,width,height);
		
		buttonRefresh = new Button(owner, "Refresh", "Refresh", 
				Color.GREEN, boundsOfbuttonRefresh, true, alpha, true, 0.0f, null, Color.CYAN);
		buttonRefresh.setOnTouchListener(this);
		
		// editText를 buttonRefresh의 바로 밑에 만든다. 
		width = (int) (bounds.width * scaleOfeditTextX);
		height = (int) (bounds.height * (scaleOfeditTextY-scaleOfbuttonRefreshY));
		x = bounds.x + bounds.width/2 - width/2;
		y = boundsOfbuttonRefresh.bottom()/* + heightOfGap*/;				
		Rectangle boundsOfEditText = new Rectangle(x, y,width,height);
		
		float fontSize = boundsOfEditText.height * 0.25f;
		editText = new EditText(false, false, this, "EditText", boundsOfEditText, fontSize, 
				false, new CodeString("", Color.BLACK), 
				EditText.ScrollMode.Both, Color.WHITE);
		editText.isReadOnly = true;
				
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
	}
	
	public void setText(String text) {
		editText.initCursorAndScrollPos();
		editText.setText(0, new CodeString(text, editText.textColor));
		
	}
	
	public void open() {
		if (!getIsOpen()) {
			/*WasKeyboardHiddenBeforeOpen = keyboard.hides;
			keyboard.setOnTouchListener(editText);
			keyboard.hides = false;*/
			super.open(true);
		}
	}
	
	
	public void draw(Canvas canvas)
    {
		if (hides) return;
		synchronized(this) {
		try{
        super.draw(canvas);
        buttonRefresh.draw(canvas);
        editText.draw(canvas);
		}catch(Exception e) {
    		
    	}
		}
    }
	
	public void setFontSize(float fontSize) {
		editText.changeFontSize(fontSize);
	}
		
    
    @Override
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r;
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    	r = super.onTouch(event, scaleFactor);
	    	if (!r) return false;
	    	//if (!r) return true;
	    	r = buttonRefresh.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	
	    	r = editText.onTouch(event, scaleFactor);
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
    	open(false);
		//this.keyboard.hides = WasKeyboardHiddenBeforeOpen;
		//keyboard.setOnTouchListener(oldKeyboardListener);
    }
    
	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		
		if (sender instanceof Button) {
			Button button = (Button)sender;
			if (button.iName==buttonRefresh.iName) {
				// MessageDialog가 여러개이므로 viewEx.messageDialog 에만 적용한다.
				if (this.iName==Control.viewEx.messageDialog.iName) {
					String message = Control.res.getString(R.string.about_program);
					editText.initCursorAndScrollPos();
					editText.setText(0, new CodeString(message,Color.BLACK));
				}
			}
			else if (button.iName==controls[0].iName) // OK
            {
				open(false);
				//this.keyboard.hides = WasKeyboardHiddenBeforeOpen;
				//keyboard.setOnTouchListener(oldKeyboardListener);
				OK(true);
				if (listener!=null) listener.onTouchEvent(this, e);
            }
			else if (button.iName==controls[1].iName) { // Cancel
				cancel();
			}
		}		
	}

}