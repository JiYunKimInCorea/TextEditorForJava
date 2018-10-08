package com.gsoft.common.gui;

import android.graphics.Canvas;
import android.graphics.Color;

import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util.ArrayList;

public class Panel extends Control {
	/** Control[]*/
	ArrayList controls;
	
	public Panel(Rectangle bounds) {
		this.bounds = bounds;
		controls = new ArrayList(10);
		this.backColor = Color.LTGRAY;
	}
	
	public void changeBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public void add(Control control) {
		controls.add(control);
	}
	
	public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r;
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    	r = super.onTouch(event, scaleFactor);
	    	if (!r) return false;
	    	int i;
	    	for (i=0; i<controls.count; i++) {
	    		Control control = (Control) controls.getItem(i);
	    		if (control!=null) {
	    			r = control.onTouch(event, scaleFactor);
	    			if (r) return true;
	    		}
	    	}
    	}
    	return false;
	}
	
	public void draw(Canvas canvas)
    {
		if (hides) return;
		synchronized(this) {
		try{
        super.draw(canvas);
        
        int i;
    	for (i=0; i<controls.count; i++) {
    		Control control = (Control) controls.getItem(i);
    		if (control!=null) {
    			control.draw(canvas);
    		}
    	}
		}catch(Exception e) {
    		
    	}
		}
    }
}