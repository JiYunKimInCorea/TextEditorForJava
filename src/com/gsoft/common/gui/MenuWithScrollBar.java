package com.gsoft.common.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.Log;

import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util.Array;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Control.Container;
import com.gsoft.common.gui.ScrollBars.HScrollBar;
import com.gsoft.common.gui.ScrollBars.VScrollBarLogical;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.common.Util.Math;
import com.gsoft.texteditor14.CustomView;




public class MenuWithScrollBar extends Container implements OnTouchListener {
		
	public enum ScrollMode {
		VScroll,
		Both
	}
	
	public class ButtonLine {
		Button[] buttons;
		int count;
		public ButtonLine() {
			buttons = new Button[10];
		}
		public void add(Button button) {
			buttons[count++] = button;
		}
		public float measureWidth() {
			float r=0;
			int i;
			for (i=0; i<count; i++) {
				r += buttons[i].bounds.width;
			}
			return r;
		}
	}
	
	VScrollBarLogical vScrollBar;
	HScrollBar hScrollBar;
	
	int vScrollBarWidth;
	int vScrollPos;
	
	public int numOfLines;
	int numOfLinesPerPage;
	//int numOfLinesInPage;
	
	int rationalBoundsWidth;
	int rationalBoundsHeight;
	int gapX;
	int lineHeight;
	
	public int originButtonWidth;
	public int originButtonHeight;
	
	Button[] buttons;
	
	ButtonLine[] linesOfButtons;
	
	ScrollMode scrollMode;
	//View view;
	
	public String selectedButtonName;
	ArrayList buttonsOfSelect = new ArrayList(10);
	
	public Button selectedButton;
	
	private Paint paint;
	
	long oldMoveTime;
	private Point oldDownPoint = new Point(0,0);
	
	/** MenuClassList에서 바운드가 바뀔때 사용, 
	 * buttonSize가 originButtonWidth, originButtonHeight가 된다.*/
	public void changeBounds(Rectangle bounds, Size buttonSize) {
		this.bounds = bounds;
		this.originButtonWidth = buttonSize.width;
		this.originButtonHeight = buttonSize.height;
		if (buttons!=null) {
			for (int i=0; i<buttons.length; i++) {
				Button button = buttons[i];
				button.changeBounds(new Rectangle(0,0,originButtonWidth,originButtonHeight));
			}
		}
		bound();
	}
	
	public void changeBounds(Rectangle bounds) {
		this.bounds = bounds;
		//this.originButtonWidth = buttonSize.width;
		//this.originButtonHeight = buttonSize.height;
		bound();
	}
	
	void bound() {
		if (scrollMode==ScrollMode.VScroll) {
			rationalBoundsWidth = bounds.width - 2*gapX - vScrollBarWidth;
			rationalBoundsHeight = bounds.height;
			
			lineHeight = originButtonHeight;			
			
			Rectangle boundsOfVScrollBar = new Rectangle(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);
			vScrollBar.changeBounds(boundsOfVScrollBar);
			
			if (buttons!=null)
				setButtons(buttons);
			
			numOfLinesPerPage = (int)(bounds.height / lineHeight/* + 1*/);		
			
			
			setVScrollBar();
			//if (text!=null)	
			
		}
	}
	
	public MenuWithScrollBar(Object owner, Rectangle bounds, Size buttonSize,
			ScrollMode scrollMode) {
		this.bounds = bounds;
		this.scrollMode = scrollMode;
		paint = new Paint();
		paint.setStyle(Style.FILL);
		gapX = 5;
		
				
		backColor = Color.CYAN;
		setBackColor(backColor);
		
		Context conbuttons = view.getContext();
				
		originButtonWidth = buttonSize.width;
		originButtonHeight = buttonSize.height;
		
		lineHeight = originButtonHeight;		
		
		linesOfButtons = new ButtonLine[50];
		
		if (scrollMode==ScrollMode.VScroll) {
			this.vScrollBarWidth = ScrollBars.getScrollBarSize();
			this.vScrollPos = 0;
			rationalBoundsWidth = bounds.width - 2*gapX - vScrollBarWidth;
			rationalBoundsHeight = bounds.height;
			numOfLinesPerPage = (int)(bounds.height / lineHeight)/*+1*/;
			
			Rectangle boundsOfVScrollBar = new Rectangle(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);
						
			vScrollBar = new VScrollBarLogical
					(this, conbuttons, boundsOfVScrollBar, 
					numOfLinesPerPage,
					numOfLines, vScrollPos, 1);			
			vScrollBar.setOnTouchListener(this);			
		}
		
	}
	
	public void setButtons(Button[] buttons) {
		if (scrollMode==ScrollMode.VScroll) {
			int i;
			vScrollPos = 0;
			this.buttons = buttons;
			if (buttons==null) return;
			this.buttonsOfSelect.reset();
			
			for (i=0; i<buttons.length; i++) {
				buttons[i].setOnTouchListener(this);
			}
			setButtonsVScroll(buttons);
			
			
			
			setVScrollBar();
			
			this.setButtonsBounds();
						
		}		
		else if (scrollMode==ScrollMode.Both) {
		}	
	}
	
	public void setVScrollBar() {
		if (vScrollPos<0) vScrollPos = 0;
		if (numOfLines > numOfLinesPerPage) { 
			// vScrollPos numOfLines-numOfLinesPerPage 보다 크지못하도록 한다.
			if (vScrollPos>numOfLines-numOfLinesPerPage){
				vScrollPos = numOfLines-numOfLinesPerPage;
			}
		}
				
		vScrollBar.hides = false;
		vScrollBar.setVScrollBar(
				numOfLinesPerPage,
				numOfLines, vScrollPos, 1);	
	
	}
	
	public void setButtonsVScroll(Button[] buttons) {
		try {
		numOfLines = 0 + 1;
		
		if (buttons!=null && buttons.length>0 && buttons[0].bounds.width > rationalBoundsWidth) {
			throw new Exception("buttonSize larger than rationalBoundsWidth");
		}
				
		ButtonLine buttonsTemp1 = new ButtonLine();
		ButtonLine buttonsTemp2 = new ButtonLine();
		int i;
		float lineWidth;
		
		Button button;
		int buttonsLen = buttons.length;
		
		for (i=0; i<buttonsLen; i++) {
			button = buttons[i];		
			buttonsTemp1.add(button);
			lineWidth = buttonsTemp1.measureWidth();
			if (lineWidth > rationalBoundsWidth) {
				setTextArrayNoSpaceError(numOfLines-1, buttonsTemp2);
				numOfLines++;
				buttonsTemp1 = new ButtonLine();
				buttonsTemp2 = new ButtonLine();
				i--;	// 초과된 버튼(다음줄의 첫버튼)를 다시 처리
			}
			else {
				buttonsTemp2.add(buttons[i]);
				setTextArrayNoSpaceError(numOfLines-1, buttonsTemp2);
				//linesOfButtons[numOfLines-1].add(button);
			}
			
		}		// for
		}catch(Exception e) {
			//Control.loggingForMessageBox.setText(false, "MenuWithScrollBar:"+e.toString(),false);
			//Control.loggingForMessageBox.setHides(false);
		}

	}
	
	void setTextArrayNoSpaceError(int lineNumber, ButtonLine buttonLine) {
		try{
		if (lineNumber<linesOfButtons.length) {
			linesOfButtons[lineNumber] = buttonLine;
		}
		else {
			/*do {
				linesOfButtons = Array.Resize(linesOfButtons, linesOfButtons.length+10);
			}while(lineNumber>=linesOfButtons.length);*/
			linesOfButtons = Array.Resize(linesOfButtons, linesOfButtons.length*2);
			
			linesOfButtons[lineNumber] = buttonLine;
		}
		}catch(Exception e) {
			//Control.loggingForMessageBox.setText(true, "MenuWithScrollBar:"+e.toString(),false);
			//Control.loggingForMessageBox.setHides(false);
		}
	}
	
	/** 버튼들의 바운드를 정해준다.*/
	public void setButtonsBounds() {
		//if (hides) return;
		
    	if (buttons==null || buttons.length==0) return;
    	
    	    	
    	int i;
    	int x, y;
    	int j;
    	if (scrollMode==ScrollMode.VScroll) {
    		int limit = Math.min(vScrollPos+numOfLinesPerPage, this.numOfLines);
			for (i=vScrollPos; i<limit; i++) {
				x = bounds.x + gapX;
				y = bounds.y + (i-vScrollPos+1) * lineHeight;
				ButtonLine buttonLine = linesOfButtons[i];	
				if (buttonLine==null) {
					int a;
					a=0;
					a++;
				}
				for (j=0; j<buttonLine.count; j++) {
					Rectangle newButtonBounds = new Rectangle(x, y-lineHeight, 
							originButtonWidth/**0.9f*/, 
							originButtonHeight/**0.9f*/);
					buttonLine.buttons[j].changeBoundsFast(newButtonBounds);
					x += originButtonWidth;
				}			
			}
		}
	}
	
	public void draw(Canvas canvas) {
		synchronized(this) {
		try {
		if (hides) return;
		
		paint.setColor(backColor);				
		canvas.drawRect(bounds.toRectF(), paint);
    	if (buttons==null || buttons.length==0) return;
    	
    	    	
    	int i;
    	int x, y;
    	int j;
    	
    	if (owner!=null &&
    			this.owner.getClass().getSimpleName().equals("MenuClassList")) {
    		int a;
    		a=0;
    		a++;
    	}
    	if (scrollMode==ScrollMode.VScroll) {
    		int limit = Math.min(vScrollPos+numOfLinesPerPage, this.numOfLines);
			for (i=vScrollPos; i<limit; i++) {
				x = bounds.x + gapX;
				y = bounds.y + (i-vScrollPos+1) * lineHeight;
				//if (y < bounds.y+bounds.height) {
					ButtonLine buttonLine = linesOfButtons[i];							
					for (j=0; j<buttonLine.count; j++) {
						/*Rectangle newButtonBounds = new Rectangle(x, y-lineHeight, 
								originButtonWidth, 
								originButtonHeight);
						buttonLine.buttons[j].changeBoundsFast(newButtonBounds);*/
						Rectangle newButtonBounds = buttonLine.buttons[j].bounds;
						buttonLine.buttons[j].draw(canvas, newButtonBounds.x, newButtonBounds.y);
						x += originButtonWidth;
					}
				//}					
			}
		}
    
    	if (scrollMode==ScrollMode.Both) {
			vScrollBar.draw(canvas);
			hScrollBar.draw(canvas);
		}
		else {
			vScrollBar.draw(canvas);
		}
		}catch(Exception e) {
			Log.e("MenuWithScrollBar-draw", e.toString());
		}
		}
	}
	
	/**"Left", "Right", "Up", "Down", "Home", "End", "PgUp", "PgDn"*/
	public void controlChar(int indexInSpecialKeys, String charA) {
		switch (indexInSpecialKeys) {
		case 2: {
			if (vScrollPos>0) vScrollPos--; 
			break;
		}
		case 3: {
			if (vScrollPos<numOfLines-numOfLinesPerPage) vScrollPos++;
			break;
		}
		case 6: {
				vScrollPos -= numOfLinesPerPage;
				if (vScrollPos<0) vScrollPos = 0;
				break;
			}				
		case 7: if (numOfLines>numOfLinesPerPage) {			
					vScrollPos += numOfLinesPerPage;
					if (vScrollPos>numOfLines-numOfLinesPerPage) 
						vScrollPos = numOfLines-numOfLinesPerPage;
					break;
				}
		}
		//setVScrollPos(heightOfvScrollPos);
		setVScrollBar();
	}
	
	@Override
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r=false;
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    	r = super.onTouch(event, scaleFactor);
	    	if (!r) return false;
	    	if (buttons==null || buttons.length==0) return false;
	    	int i, j;
	    	if (scrollMode==ScrollMode.VScroll) {
	    		boolean r1=false;
				r1 = vScrollBar.onTouch(event, null);
				if (r1) {
					//isMoveActionCaptured = true;
					//vScrollBar.setActionCapture(true);
					return true;
				}
				//isMoveActionCaptured = false;
				//vScrollBar.setActionCapture(false);
				
				if (this.owner instanceof CustomView) {
					int a;
					a=0;
					a++;
				}
								
				
	    		// 한 번 그린 뒤에(draw에서 vScrollPos에 따라 Button들의 영역을 다시 계산한다)
	    		// 터치를 하므로 다음과 같이 하는 것이 가능하다. 다시 계산된 버튼들의 영역을 바탕으로 터치 이벤트를 전달한다.
				int limit = Math.min(vScrollPos+numOfLinesPerPage, this.numOfLines);
				for (i=vScrollPos; i<limit; i++) {
					//if (y < bounds.y+bounds.height) {
						ButtonLine buttonLine = linesOfButtons[i];						
						for (j=0; j<buttonLine.count; j++) {
							r = buttonLine.buttons[j].onTouch(event, null);
							if (r) {
								if (buttonLine.buttons[j].isSelected) {
									buttonsOfSelect.add(buttonLine.buttons[j]);
								}
								return true;
							}
						}
					//}
				}
				
				// MenuScrollable영역안에서 스크롤바와 버튼들을 제외한 공백영역을 터치시
				// 선택된 버튼을 원래대로 초기화한다.
				for (i=0; i<buttonsOfSelect.count; i++) {
					Button button = (Button)buttonsOfSelect.getItem(i);
					button.Select(false);
				}
				buttonsOfSelect.reset();
				
				capturedControl = this;
				
				onTouchEvent(this, event);
				
				oldDownPoint.x = event.x;
				oldDownPoint.y = event.y;
				oldMoveTime = System.currentTimeMillis();
				
	    	}//if (scrollMode==ScrollMode.VScroll) {
	    	
	    	return true;
    	}
    	else if (event.actionCode==MotionEvent.ActionMove) {
    		if (this==Control.capturedControl) {
    			// 영역내에서 터치를 하여 캡쳐를 하면 CustomView에서 ActionMove를 전달하여 스크롤을 하게 된다.
    			// 영역검사를 하지않고 영역을 벗어나더라도 자신이 핸들링한다.
    			onTouchEvent(this, event);
				return true;
			}
    	}
    	return false;
    }
	
	/*public void releaseActionCapture() {
		isMoveActionCaptured = false;
		vScrollBar.setActionCapture(false);
	}*/
	
	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub	
				
		if (sender instanceof Button) { // 버튼 터치시 호출된다.
			Button button = (Button)sender;
			int i;
			for (i=0; i<buttons.length; i++) {
				if (button.iName==buttons[i].iName) {
					selectedButtonName = button.name;
					selectedButton = button;
					listener.onTouchEvent(this, e); // FileDialog 등
					return;
				}
			}
		}
		else if (sender instanceof VScrollBarLogical) {
			VScrollBarLogical vScrollBar = (VScrollBarLogical)sender;
			this.vScrollPos = vScrollBar.vScrollPos;			
			setVScrollBar();
			setButtonsBounds();
		}
		else if (sender instanceof MenuWithScrollBar) { 
			// MenuWithScrollBar의 버튼제외영역을 터치하거나 스크롤시 호출 
			if (e.actionCode==MotionEvent.ActionDown) {
				selectedButtonName = null;
				selectedButton = null;
				listener.onTouchEvent(this, e);
			}
			else if (e.actionCode==MotionEvent.ActionMove) {
				long curMoveTime = System.currentTimeMillis();
				if (curMoveTime-oldMoveTime<500) {	// 느리게 스크롤하는 효과
					//oldMoveTime = curMoveTime; 
					return;
				}				
				oldMoveTime = curMoveTime;
				
				boolean r = false;				
				
				if (e.y>bounds.bottom()) {
					if (numOfLines > numOfLinesPerPage) { 
						vScrollPos++;
						setVScrollBar();
						r = true;
					}						
				}
				else if (e.y<bounds.y){
					if (vScrollPos>0) { 
						vScrollPos--;
						setVScrollBar();
						r = true;
					}						
				}
				if (r==false) {
					boolean u = false;
					int diff = 10;
					if (e.y-oldDownPoint.y>diff) {
						if (numOfLines > numOfLinesPerPage) { 
							vScrollPos++;
							setVScrollBar();
							u = true;
						}							
					}
					else if (e.y-oldDownPoint.y<-diff) {
						if (vScrollPos>0) {
							vScrollPos--;						
							setVScrollBar();
							u = true;
						}							
					}
					
					if (u) {
						oldDownPoint.x = e.x;
						oldDownPoint.y = e.y;
					}
				}//if (r==false)
				this.setButtonsBounds();
			}//else if (e.actionCode==MotionEvent.ActionMove) {
		}//else if (sender instanceof MenuWithScrollBar) { 
		
	}//onTouchEvent()
}