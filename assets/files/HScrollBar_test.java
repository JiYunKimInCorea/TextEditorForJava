package com.gsoft.common.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.gsoft.common.Events;
import com.gsoft.common.Sizing;
import com.gsoft.common.interfaces;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.ScrollBars.RectForPage;
import com.gsoft.common.gui.ScrollBars.Thumb;
import com.gsoft.common.interfaces.OnTouchListener;

public class HScrollBar_test extends Control implements OnTouchListener {
	
	com.gsoft.common.gui.SpinControl spinControl;
	/** 스크롤막대의 왼쪽 사각형*/
	RectForPage rectForPageLeft;
	/** 스크롤막대의 오른쪽 사각형*/
	RectForPage rectForPageRight;
	/** 스크롤막대의 스크롤가능 사각형(page)*/
	Thumb thumb;
	
	float widthOfBar;
	
	int widthOfCharsPerPage;
	int widthOfCharsInPage;
	int widthOfTotalChars;
	
	int widthOfScrollPos;
	int widthOfScrollInc;
	
	int diffThumbXFromEventX;
			
	public HScrollBar_test(java.lang.Object owner, Context context, RectangleF bounds) {
		super();
		this.owner = owner;
		this.bounds = bounds;
		spinControl = new SpinControl(owner, context, bounds, true, false);
		spinControl.setOnTouchListener(this);
		
		widthOfBar = bounds.width - 2 * spinControl.boundsLeft.width;
		
		RectangleF boundsOfThumb = new RectangleF();
		thumb = new Thumb(owner, boundsOfThumb, Color.BLUE);
		thumb.setOnTouchListener(this);
		
		boolean isUpOrDown1 = this.rectForPageLeft.isUpOrDown;
		boolean isUpOrDown2 = this.rectForPageRight.isUpOrDown;
		
		RectangleF boundsOfrectForPageLeft = new RectangleF();
		rectForPageLeft = new RectForPage(owner, boundsOfrectForPageLeft, Color.CYAN, true);
		rectForPageLeft.setOnTouchListener(this);
		
		RectangleF boundsOfrectForPageRight = new RectangleF();
		rectForPageRight = new RectForPage(owner, boundsOfrectForPageRight, Color.CYAN, false);
		rectForPageRight.setOnTouchListener(this);
		
		setHScrollBar(widthOfCharsPerPage, widthOfCharsInPage, 
			widthOfTotalChars, widthOfScrollPos, widthOfScrollInc);
		
	}
	
	public void changeBounds(RectangleF bounds) {
		this.bounds = bounds;
		spinControl.changeBounds(bounds);
		widthOfBar = bounds.width - 2 * spinControl.boundsLeft.width;
		// 기존값으로 수치를 정하므로 오차가 생길 수 있다.
		setHScrollBar(widthOfCharsPerPage, widthOfCharsInPage, 
				widthOfTotalChars, widthOfScrollPos, widthOfScrollInc);
	}				
	
	/** 스크롤바의 한 페이지크기, 전체 영역, 스크롤위치(전체영역에서 현재위치), 
	 * 스크롤증감치(스크롤바의 가장 모서리부분터치시)*/
	public void setHScrollBar(int widthOfCharsPerPage, 
			int widthOfCharsInPage, 
			int widthOfTotalChars, int widthOfScrollPos, int widthOfScrollInc) {
				
		this.widthOfCharsPerPage = widthOfCharsPerPage;
		this.widthOfCharsInPage = widthOfCharsInPage;
		this.widthOfScrollPos = widthOfScrollPos;
		this.widthOfScrollInc = widthOfScrollInc;
		this.widthOfTotalChars = widthOfTotalChars;	
		
		float s=0;
		
		rectForPageLeft.bounds.x = spinControl.boundsLeft.right();
		rectForPageLeft.bounds.y = bounds.y;
		if (widthOfTotalChars!=0)
			s = ((float)widthOfScrollPos / (float)widthOfTotalChars);
		else 
			s = 0;
		rectForPageLeft.bounds.width = widthOfBar * s;			
		rectForPageLeft.bounds.height = bounds.height;
		//rectForPageLeft.isBoundsAble();
		
		thumb.bounds.x = rectForPageLeft.bounds.right();
		thumb.bounds.y = bounds.y;
		if (widthOfTotalChars!=0)
			s = (float)widthOfCharsInPage / (float)widthOfTotalChars;
		else s = 1;
		thumb.bounds.width = (s * widthOfBar);			
		thumb.bounds.height = bounds.height;	
		//thumb.isBoundsAble();
		
		rectForPageRight.bounds.x = thumb.bounds.right();
		rectForPageRight.bounds.y = bounds.y;
		if (widthOfTotalChars!=0)
			s = (float)(widthOfTotalChars-widthOfScrollPos-widthOfCharsInPage) 
				/ (float)widthOfTotalChars;
		else s = 0;
		rectForPageRight.bounds.width = (s * widthOfBar);			
		rectForPageRight.bounds.height = bounds.height;
		//rectForPageRight.isBoundsAble();
	
		
	}
	
	/** 이 컨트롤이 action을 capture하였을 경우 ActionMove이벤트를 이 컨트롤의 thumb이 처리한다.*/
	public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
		boolean r;
		if (event.actionCode==MotionEvent.ActionDown) {
			if (super.onTouch(event,scaleFactor)==false) return false;
			r = spinControl.onTouch(event, scaleFactor);
			if (r) {
	    		//capturedControl=this;
	    		return true;
	    	}
	    	r = rectForPageLeft.onTouch(event, scaleFactor);
	    	if (r) {
	    		//capturedControl=this;
	    		return true;
	    	}
	    	r = rectForPageRight.onTouch(event, scaleFactor);
	    	if (r) {
	    		//capturedControl=this;
	    		return true;
	    	}		    	
	    	r = thumb.onTouch(event, scaleFactor);
	    	if (r) {
	    		capturedControl=this;
	    		return true;
	    	}
		}
		else if (event.actionCode==MotionEvent.ActionMove) {
			if (capturedControl==this) {
				((Control)owner).modified = true;
				thumb.onTouch(event, scaleFactor);
				return true;
			}
			
		}
    	
    	return false;
    }
	
	@Override
	public void draw(Canvas canvas) {
		synchronized(this) {
		try{
		spinControl.draw(canvas);
		rectForPageLeft.draw(canvas);
		rectForPageRight.draw(canvas);
		thumb.draw(canvas);
		}catch(Exception e) {
    		
    	}
		}
	}
	
	/** event handler*/
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		
		if (sender instanceof SpinControl) {
			SpinControl spinControl = (SpinControl)sender;
			if (!spinControl.isVertOrHorz) {
				if (spinControl.isLeftClicked) {
					widthOfScrollPos-=widthOfScrollInc;
					if (widthOfScrollPos<0) widthOfScrollPos = 0;				
				}
				else if (spinControl.isRightClicked) {
					if (widthOfTotalChars>widthOfCharsPerPage) {
						widthOfScrollPos+=widthOfScrollInc;						
						if (widthOfScrollPos>widthOfTotalChars-widthOfCharsPerPage) 
							widthOfScrollPos = widthOfTotalChars-widthOfCharsPerPage;
						if (widthOfScrollPos<0) widthOfScrollPos = 0;
					}
				}
				int widthOfCharsInPage = widthOfTotalChars - widthOfScrollPos;
				widthOfCharsInPage = Math.min(widthOfCharsPerPage, widthOfCharsInPage);
				setHScrollBar(widthOfCharsPerPage, 
						widthOfCharsInPage, widthOfTotalChars, widthOfScrollPos, widthOfScrollInc);
				listener.onTouchEvent(this, e);
			}
		} // spinControl
		else if (sender instanceof RectForPage) {
			RectForPage rectForPage = (RectForPage)sender;
			if (rectForPage.isUpOrDown) {
				widthOfScrollPos -= this.widthOfCharsPerPage;
				if (widthOfScrollPos<0) widthOfScrollPos = 0;
			}
			else {
				widthOfScrollPos += this.widthOfCharsPerPage;
				//if (widthOfScrollPos<0) widthOfScrollPos = 0;
				if (widthOfScrollPos>widthOfTotalChars-widthOfCharsPerPage) 
					widthOfScrollPos = widthOfTotalChars-widthOfCharsPerPage;
				if (widthOfScrollPos<0) widthOfScrollPos = 0;
			}
			int widthOfCharsInPage = widthOfTotalChars - widthOfScrollPos;
			widthOfCharsInPage = Math.min(widthOfCharsPerPage, widthOfCharsInPage);
			setHScrollBar(widthOfCharsPerPage, 
					widthOfCharsInPage, widthOfTotalChars, widthOfScrollPos, widthOfScrollInc);
			listener.onTouchEvent(this, e);
		}
		else if (sender instanceof Thumb) {
			Thumb thumb = (Thumb)sender;
			int eventX = e.x;
			if (e.actionCode==MotionEvent.ActionDown) {
				diffThumbXFromEventX = (int) (e.x - thumb.bounds.x);
			}
			else  {
				if (eventX>spinControl.boundsRight.x) eventX = (int) spinControl.boundsRight.x;
				if (eventX<spinControl.boundsLeft.right()) eventX = (int) spinControl.boundsLeft.right();
				int newThumbX = eventX - diffThumbXFromEventX;
				newThumbX -= spinControl.boundsLeft.right();
				widthOfScrollPos = (int) (widthOfTotalChars/widthOfBar*newThumbX);
				
				if (widthOfScrollPos>widthOfTotalChars-widthOfCharsPerPage)
					widthOfScrollPos=widthOfTotalChars-widthOfCharsPerPage;
				if (widthOfScrollPos<0) widthOfScrollPos=0;
				// newThumbX : newScrollPos = rectForPageLeft.bounds.width : widthOfScrollPos
				// newScrollPos = widthOfScrollPos / rectForPageLeft.bounds.width * newThumbX
			}
			int widthOfCharsInPage = widthOfTotalChars - widthOfScrollPos;
			widthOfCharsInPage = Math.min(widthOfCharsPerPage, widthOfCharsInPage);
			setHScrollBar(widthOfCharsPerPage, 
					widthOfCharsInPage, widthOfTotalChars, widthOfScrollPos, widthOfScrollInc);
			listener.onTouchEvent(this, e);
		}
		
	}		
}
