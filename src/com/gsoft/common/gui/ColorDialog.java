package com.gsoft.common.gui;

import com.gsoft.common.ColorEx;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Buttons.ButtonGroup;
import com.gsoft.common.interfaces.OnTouchListener;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import com.gsoft.common.R.R;

public class ColorDialog extends Dialog  implements OnTouchListener {
	int[] colors = {Color.BLACK, Color.WHITE, Color.RED, Color.YELLOW,
			Color.BLUE, Color.GREEN, Color.MAGENTA, Color.CYAN, 
			Color.LTGRAY
	};
	String[] namesOfColorButtons = {"Black", "White", "Red", "Yellow",
			"Blue", "Green", "Magenta", "Cyan",
			"LtGray"
	};
	
	Byte[] indicesOfButtonsInGroup = {0,1,2,3,4,5,6,7,8};
	
	Button[] colorButtons = new Button[colors.length];
	int selectedColor;
	String strSelectedColor;
	
	float scaleOfGapX = 0.05f;
	
	float scaleOfTitleBar = 0.1f;
	
	float scaleOfColorButtonsX = (1-scaleOfGapX*4) / 3;
	float scaleOfColorButtonsY = 0.15f;
	
	float scaleOfOKButtonX = (1-scaleOfGapX*3) / 2;
	float scaleOfOKButtonY = 0.15f;
	
	// 3은 gap개수
	float scaleOfGapY = (1-(scaleOfTitleBar + scaleOfColorButtonsY * 3 + scaleOfOKButtonY)) / 5;
	
	

	public ColorDialog(View owner, Rectangle bounds) {
		super(owner, bounds);
		// TODO Auto-generated constructor stub
		
		this.bounds = bounds;
		heightTitleBar = (int) (bounds.height*scaleOfTitleBar);
	
		
		this.isTitleBarEnable = true;
		this.Text = Control.res.getString(R.string.color_dialog);
		
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		int alpha = 255;
		
		this.backColor = ColorEx.darkerOrLighter(Color.YELLOW, -80);
		
		int colorOfButton = ColorEx.darkerOrLighter(Color.WHITE, -100);
		
		int width = (int) (bounds.width * scaleOfColorButtonsX);
		int height = (int) (bounds.height * scaleOfColorButtonsY);
		int x = bounds.x;
		int y = bounds.y + heightTitleBar;
		Rectangle boundsOfColorButtons=null;
		
		ButtonGroup group = new ButtonGroup(indicesOfButtonsInGroup, colorButtons);
		
		int i, j, k;
		for (j=0; j<3; j++) {
			y += heightOfGap;
			x = bounds.x; 
			for (i=0; i<3; i++) {
				x += widthOfGap;
				boundsOfColorButtons = new Rectangle(x, y,width,height);
				k = j*3+i;
				colorButtons[k] = new Button(owner, namesOfColorButtons[k], namesOfColorButtons[k], 
						colors[k], boundsOfColorButtons, true, alpha, true, 0.0f, null, this.backColor);
				colorButtons[k].ColorSelected = Color.DKGRAY;				
				colorButtons[k].setOnTouchListener(this);								
				colorButtons[k].setGroup(group, k);
				x += width;
			}
			y += height;
		}
		
				
		width = (int) (bounds.width * scaleOfOKButtonX);
		height = (int) (bounds.height * scaleOfOKButtonY);
		x = bounds.x + widthOfGap;
		y = boundsOfColorButtons.bottom() + heightOfGap;
		Rectangle boundsOfButtonOK = new Rectangle(x,y,width,height);
		x = boundsOfButtonOK.right() + widthOfGap;
		Rectangle boundsOfButtonCancel = new Rectangle(x,y,width,height);
		
		controls = new Button[2];
		controls[0] = new Button(owner, NameButtonOk, Control.res.getString(R.string.OK), 
				colorOfButton, boundsOfButtonOK, false, alpha, true, 0.0f, null, this.backColor);
		controls[1] = new Button(owner, NameButtonCancel, Control.res.getString(R.string.cancel), 
				colorOfButton, boundsOfButtonCancel, false, alpha, true, 0.0f, null, this.backColor);
		// 이벤트를 이 클래스에서 직접 처리
		controls[0].setOnTouchListener(this);
		controls[1].setOnTouchListener(this);
	}
	
	/*public void open(OnTouchListener listener) {
		this.listener = listener;
		super.open(true);		
	}*/
	
	
	public synchronized void draw(Canvas canvas)
    {
		if (hides) return;
		synchronized(this) {
			try {
		        super.draw(canvas);
		        int i;
		        for (i=0; i<colorButtons.length; i++){
		        	colorButtons[i].draw(canvas);
				}
			}catch(Exception e) { 	}
    	}
    }
		
    
    @Override
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r;
    	int i;
    	
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
    		r = super.onTouch(event, scaleFactor);
        	if (!r) return false;
        	
	    	for (i=0; i<controls.length; i++) {
	    		r = controls[i].onTouch(event, scaleFactor);
	    		if (r) return true;
	    	}
	        for (i=0; i<colorButtons.length; i++) {
	        	r = colorButtons[i].onTouch(event, scaleFactor);
	        	if (r) {
	        		return true;
	        	}
	        }
    	}
    	else if (event.actionCode==MotionEvent.ActionMove && capturedControl==this) {
    		//액션캡쳐는 CustomView와 협동하여 처리한다.
    		return false;
    	}
    	return false;
    }   
    
    public void cancel() {
		OK(false);
		open(false);
    }
    
	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		
		if (sender instanceof Button) {
			Button button = (Button)sender;
			int i;
			for (i=0; i<namesOfColorButtons.length; i++) {
				//if (button.name.equals(namesOfColorButtons[i])) {
				if (button.iName==colorButtons[i].iName) {
					selectedColor = colors[i];
					strSelectedColor = namesOfColorButtons[i];
					return;
				}
			}
			if (button.iName==controls[0].iName) // OK
            {   
				OK(false);
            	open(false);
				callTouchListener(this, null);			
            }
            else if (button.iName==controls[1].iName) // Cancel
            {
            	cancel();
            }			
		}		
	}


}