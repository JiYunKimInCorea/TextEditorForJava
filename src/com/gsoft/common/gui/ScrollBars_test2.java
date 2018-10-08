package com.gsoft.common.gui;

import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.interfaces.OnTouchListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class ScrollBars_test2 {
	public static float VScrollBarWidthScale = 0.065f;
	public static float HScrollBarHeightScale = 0.04f;
	
	public static void a(int b) {
		float f=50;
		int i=0;
		// fcmpl,ifle           
		if (f>i) {
			// 위 4개의 if가 성립하면 수행된다.
			i = 3;
		}
		//fcmpg,ifge 
		if (30.0f<i) {
			i = 1;
		}
		//fcmpl,ifne
		if (f==i) {
			i = 2;
		}
		//fcmpl,iflt
		if (3>=f) {
			i = 3;
		}
		i = 4;
		
		if (f>i && 30.0f<i) {
			i = 5;
		}
		
		// fcmpl은 두 실수 f1, f2를 비교해서 뺀 결과는 f1<f2일 경우 0초과가 된다.
		// fcmpl은 두 실수 f1, f2를 비교해서 뺀 결과는 f1>f2일 경우 0이하가 된다.
		// fcmpl은 두 실수 f1, f2를 비교해서 뺀 결과는 f1==f2일 경우 0이 된다.
		
		// fcmpg은 두 실수 f1, f2를 비교해서 뺀 결과는 f1>f2일 경우 0초과가 된다.
		// fcmpg은 두 실수 f1, f2를 비교해서 뺀 결과는 f1<f2일 경우 0이하가 된다.
		// fcmpg은 두 실수 f1, f2를 비교해서 뺀 결과는 f1==f2일 경우 0이 된다.
	}
	
	/** viewEx의 initControls에서 호출된다.*/
	public static void setScrollBarScale(View view) {
		VScrollBarWidthScale = 0.065f;
		HScrollBarHeightScale = 0.04f;
		int i=0;
		if (i<0) {
			
		}
	}
	
	/** 스크롤막대의 사각형(pageUp, pageDown, pageLeft, pageRight)*/
	static class RectForPage extends Control {
		Paint paint = new Paint();
		int backColor;
		/** up이면 true, down이면 false*/
		boolean isUpOrDown;
		static boolean isUpOrDown_test;
		
		static void test(RectForPage r) {
			
		}
		
		public RectForPage(Object owner, Rectangle bounds, int backColor,
				boolean isUpOrDown) {
			super();
			this.owner = owner;
			this.backColor = backColor;
			paint.setStyle(Style.FILL);
			a(2);
			RectForPage r = new RectForPage(owner, bounds, backColor, isUpOrDown);
			test(r);
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
			canvas.drawRect(bounds.toRectF(), paint);
		}
	}
	
		
	static public class HScrollBar extends Control implements OnTouchListener {
		
		SpinControl spinControl;
		/** 스크롤막대의 왼쪽 사각형*/
		RectForPage rectForPageLeft;
		
		float widthOfBar;
		
		int widthOfCharsPerPage;
		
		
				
		public HScrollBar(Object owner, Context context, Rectangle bounds) {
			super();
			this.owner = owner;
			spinControl = new SpinControl(owner, context, bounds, true, false);
			
			widthOfBar = bounds.width - 2 * spinControl.boundsLeft.width;
			
			
			boolean isUpOrDown1 = this.rectForPageLeft.isUpOrDown;
			
			boolean isTest = RectForPage.isUpOrDown_test;
			RectForPage.test(rectForPageLeft);
			
			Rectangle boundsOfrectForPageLeft = new Rectangle();
			rectForPageLeft = new RectForPage(owner, boundsOfrectForPageLeft, Color.CYAN, true);
			rectForPageLeft.setOnTouchListener(this);
			
		
			
		}
		
		public void changeBounds(Rectangle bounds) {
			this.bounds = bounds;
			spinControl.changeBounds(bounds);
			widthOfBar = bounds.width - 2 * spinControl.boundsLeft.width;
		}				
		
		/** 스크롤바의 한 페이지크기, 전체 영역, 스크롤위치(전체영역에서 현재위치), 
		 * 스크롤증감치(스크롤바의 가장 모서리부분터치시)*/
		public void setHScrollBar(int widthOfCharsPerPage, 
				int widthOfCharsInPage, 
				int widthOfTotalChars, int widthOfScrollPos, int widthOfScrollInc) {
					
			this.widthOfCharsPerPage = widthOfCharsPerPage;
			
			float s=0;
			
			rectForPageLeft.bounds.x = spinControl.boundsLeft.right();
			rectForPageLeft.bounds.y = bounds.y;
			if (widthOfTotalChars!=0)
				s = ((float)widthOfScrollPos / (float)widthOfTotalChars);
			else 
				s = 0;
		}
		
		/** 이 컨트롤이 action을 capture하였을 경우 ActionMove이벤트를 이 컨트롤의 thumb이 처리한다.*/
		public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
			boolean r;
			if (event.actionCode==MotionEvent.ActionDown) {
				if (super.onTouch(event,scaleFactor)==false) return false;
				
		    	r = rectForPageLeft.onTouch(event, scaleFactor);
		    	if (r) {
		    		//capturedControl=this;
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

		@Override
		public void onTouchEvent(Object sender, MotionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

}