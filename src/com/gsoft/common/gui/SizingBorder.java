package com.gsoft.common.gui;

import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.interfaces.OnTouchListener;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class SizingBorder extends Control {
	Paint paint = new Paint();
	int color=Color.BLACK;
	
	/*public synchronized void setHides(boolean hides) {
		this.hides = hides;
		//if (!hides) {
			modified = true;
		//}
	}*/	
	
	public SizingBorder(Object owner, Rectangle bounds, OnTouchListener listener) {
		super();
		this.owner = owner;
		this.bounds = bounds;
		this.listener = listener;
		this.hides = true;
		paint.setStyle(Style.FILL);
		
	}
	/*public void setBoundsDrawing(RectangleF originBounds) {
		boundsDrawing = new RectF(originBounds.x, originBounds.y, 
				originBounds.x+originBounds.width, originBounds.y+3);
	}*/
	public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
		// TODO Auto-generated method stub
		if (hides) return false;
		if (event.actionCode==MotionEvent.ActionDown) {
			if (super.onTouch(event, scaleFactor)==false) {
				return false;
			}
			capturedControl = this;
	    	  
	    	callTouchListener(this, event);
	    	return true;
		}
		else if (event.actionCode==MotionEvent.ActionMove && capturedControl==this) {
			callTouchListener(this, event);
			return true;
    	}    	
    	return false;
	}
	public void changeBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	public boolean IsPointIn(Point point)
    {
        if (bounds.x <= point.x && point.x <= bounds.x + bounds.width &&
            bounds.y <= point.y && point.y <= bounds.y + bounds.height)
            return true;
        return false;
    }
	public void draw(Canvas canvas) {
		synchronized(this) {
		try{
		//if (hides) return;
		//paint.setColor(Color.WHITE);
		//RectF boundsRect = this.bounds.toRectF();
		//canvas.drawRect(boundsRect, paint);
		
		if (!hides) {
			paint.setColor(color);
			RectF boundsRect = this.bounds.toRectF();
			boundsRect.bottom = boundsRect.top + 3;
			canvas.drawRect(boundsRect,paint);
		}
		}catch(Exception e) {
    		
    	}
		}
		
	}

}