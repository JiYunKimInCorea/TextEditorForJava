package com.gsoft.common.gui;

import com.gsoft.common.ColorEx;
import com.gsoft.common.Events;
import com.gsoft.common.Sizing;
import com.gsoft.common.interfaces;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.interfaces.OnTouchListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class ScrollBars_test3 {
	public static float VScrollBarWidthScale = 0.065f;
	public static float HScrollBarHeightScale = 0.04f;
	
	public static void a() {
		
	}
	
	/** viewEx의 initControls에서 호출된다.*/
	public static void setScrollBarScale(View view) {
		int width = view.getWidth();
		int height = view.getHeight();
		if (height>width) {
			VScrollBarWidthScale = 0.065f;
			HScrollBarHeightScale = 0.04f;
		}
		else {
			VScrollBarWidthScale = 0.04f;
			HScrollBarHeightScale = 0.06f;
		}
	}
	
	/** 스크롤막대의 사각형(pageUp, pageDown, pageLeft, pageRight)*/
	static class RectForPage extends Control {
		Paint paint = new Paint();
		Paint paintOfBorder = new Paint();
		int backColor;
		/** up이면 true, down이면 false*/
		boolean isUpOrDown;
		static boolean isUpOrDown_test;
		
		static void test() {
			
		}
		
		public RectForPage(Object owner, RectangleF bounds, int backColor,
				boolean isUpOrDown) {
			super();
			this.bounds = bounds;
			//isBoundsAble();
			this.owner = owner;
			this.backColor = backColor;
			this.isUpOrDown = isUpOrDown;
			paint.setStyle(Style.FILL);
			paintOfBorder.setStyle(Style.STROKE);
			paint.setColor(backColor);
			paintOfBorder.setColor(ColorEx.darkerOrLighter(backColor, 0.5f));
			a();
		}
		
		public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
	    	if (event.actionCode==MotionEvent.ActionDown) {
				if (super.onTouch(event,scaleFactor)==false) return false;
	    		callTouchListener(this, event);
	    		return true;
	    	}	
	    	
	    	return false;
	    }
		
		public void draw(Canvas canvas) {
			//if (boundsEnable){
				canvas.drawRect(bounds.toRectF(), paint);    		        
	        	canvas.drawRect(bounds.toRectF(), paintOfBorder);
			//}
		}
	}
	
	
	
	
	static public class HScrollBar extends Control implements OnTouchListener {
		
		SpinControl spinControl;
		/** 스크롤막대의 왼쪽 사각형*/
		RectForPage rectForPageLeft;
		
		float widthOfBar;
		
		int widthOfCharsPerPage;
		int widthOfCharsInPage;
		int widthOfTotalChars;
		
		int widthOfScrollPos;
		int widthOfScrollInc;
		
		int diffThumbXFromEventX;
				
		public HScrollBar(Object owner, Context context, RectangleF bounds) {
			super();
			this.owner = owner;
			this.bounds = bounds;
			spinControl = new SpinControl(owner, context, bounds, true, false);
			spinControl.setOnTouchListener(this);
			
			widthOfBar = bounds.width - 2 * spinControl.boundsLeft.width;
			
						
			boolean isUpOrDown1 = this.rectForPageLeft.isUpOrDown;
			
			boolean isTest = RectForPage.isUpOrDown_test;
			RectForPage.test();
			
			RectangleF boundsOfrectForPageLeft = new RectangleF();
			rectForPageLeft = new RectForPage(owner, boundsOfrectForPageLeft, Color.CYAN, true);
			rectForPageLeft.setOnTouchListener(this);
			
		
			
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
		    
			}
			else if (event.actionCode==MotionEvent.ActionMove) {
				if (capturedControl==this) {
					((Control)owner).modified = true;
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
			}catch(Exception e) {
	    		
	    	}
			}
		}
		
		/** event handler*/
		public void onTouchEvent(Object sender, MotionEvent e) {
			// TODO Auto-generated method stub
			
			if (sender instanceof RectForPage) {
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
			
			
		}		
	}

}
