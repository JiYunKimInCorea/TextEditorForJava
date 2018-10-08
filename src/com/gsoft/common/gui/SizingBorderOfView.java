package com.gsoft.common.gui;

import android.graphics.Canvas;
import android.graphics.Point;

import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.interfaces.OnTouchListener;

public class SizingBorderOfView extends SizingBorder {

	public static int thickness;
	public ResizeViewOrientation orientation;
	
	static {
		if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
			thickness = 15;
		}
		else {
			thickness = 5;
		}
	}
	
	public SizingBorderOfView(Object owner, Rectangle bounds,
			OnTouchListener listener) {
		super(owner, bounds, listener);
		// TODO Auto-generated constructor stub
	}
	
	public ResizeViewOrientation IsPointIn2(Point point)
    {
		// top
        if (bounds.x <= point.x && point.x <= bounds.x + bounds.width &&
            bounds.y <= point.y && point.y <= bounds.y + thickness)
            return new ResizeViewOrientation(ResizeViewOrientation.TOP);
        
        // left
        if (bounds.x <= point.x && point.x <= bounds.x + thickness &&
            bounds.y <= point.y && point.y <= bounds.y + bounds.height)
            return new ResizeViewOrientation(ResizeViewOrientation.LEFT);
        
        // right
        if (bounds.x + bounds.width - thickness <= point.x && point.x <= bounds.x + bounds.width &&
            bounds.y <= point.y && point.y <= bounds.y + bounds.height)
            return new ResizeViewOrientation(ResizeViewOrientation.RIGHT);
        
        // bottom
        if (bounds.x <= point.x && point.x <= bounds.x + bounds.width &&
            bounds.y +bounds.height - thickness <= point.y && point.y <= bounds.y + bounds.height)
            return new ResizeViewOrientation(ResizeViewOrientation.BOTTOM);
        return null;
    }
	
	public static class ResizeViewOrientation {
		public static byte TOP = 1;
		public static byte LEFT = 2;
		public static byte RIGHT = 3;
		public static byte BOTTOM = 4;
		
		public static Point TopLeftAbsolute = new Point(0,0);
		
		public static Point RightBottomAbsolute = new Point(0,0);
		
				
				
		public byte orientation;
		
		
		public ResizeViewOrientation(byte orientation) {
			this.orientation = orientation;
		}
	}
	
		
	public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
		// TODO Auto-generated method stub
		if (hides) return false;
		if (event.actionCode==MotionEvent.ActionDown) {
			Point point = new Point(event.x, event.y);
			orientation = IsPointIn2(point);
			if (orientation==null) {
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
	
	public void draw(Canvas canvas) {
		
	}

}