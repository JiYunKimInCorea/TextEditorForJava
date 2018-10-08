package com.gsoft.common.gui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;

import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;

public class ProgressBar extends Control {
	public int itemCount;

	public int itemPos;

    SizeF itemSize;

    //Color backColor;
    //Color itemColor;
    //Color itemSkinColor;
    
    
    float itemSkinThickness;

    boolean enable;
    
    Paint fillPaint = new Paint();

	boolean isVertOrHorz;
           
  
    public static int ItemColor = Color.GREEN;

    public synchronized void setHides(boolean hides) {
		//if (this.hides==hides) return;
		this.hides = hides;
		open(!this.hides);
	}
    
    public int getItemCount() {
	    return this.itemCount;
    }
    public int getItemPos () {
    	return this.itemPos;
    }
    public synchronized void setItemPos(int itemPos) {
    	this.itemPos = itemPos;
		if (itemPos>itemCount) this.itemPos = itemCount;
	}
    
    public synchronized void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

    public boolean getEnable () {
	    return this.enable;
    }
    public synchronized void setEnable(boolean enable) {
		this.enable = enable;
	}


    ///<summary> initProgressBarBounds = Rectangle(5,50,30,400);
    ///Game1, 22, InitProgressBarBounds, 0 </summary>
    public ProgressBar(boolean isVertOrHorz, int itemCount, Rectangle srcBounds, int itemPos) {
    	super();
    	this.isVertOrHorz = isVertOrHorz;
	    this.itemCount = itemCount;
	    this.bounds = srcBounds;
	    this.itemPos = itemPos;
	    
	    fillPaint.setStyle(Style.FILL);
	    backColor = Color.MAGENTA;
	    
	   
	    initialize();

    }
    
    public void initialize() {
    	if (isVertOrHorz) {
	    	itemSize = new SizeF(bounds.width, 
		    		((float)bounds.height)/itemCount);		   
	
		    itemSkinThickness = itemSize.width / 15;
    	}
    	else {
    		itemSize = new SizeF(((float)bounds.width)/itemCount, bounds.height);		   
	
		    itemSkinThickness = itemSize.height / 15;
    	}
    }
   
   
     
   
        
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	if (event.actionCode==MotionEvent.ActionDown) {
	    	Point point = new Point(event.x, event.y);
	    	boolean r = super.onTouch(event, null);
	    	
	    	if (r) {
	    		if (isVertOrHorz) {
		    		int v = (int)((point.y - bounds.y) / this.itemSize.height);
		    		itemPos = this.itemCount - v;
	    		}
	    		else {
	    			int v = (int)((point.x - bounds.x) / this.itemSize.width);
		    		itemPos = v + 1;
	    		}
	    		callTouchListener(this, event);
	    		//open(false);
	    		return true;
	    	}
	    	else {
	    		open(false);
	    		return true;
	    	}
    	}
    	return false;
    }
    
    

    public void drawBackground(Canvas canvas) {    	
    	fillPaint.setColor(backColor);
    	canvas.drawRect(bounds.toRectF(), fillPaint);    	
    }

    public void draw(Canvas canvas) {
    	synchronized (this) {
    	try{
    	if (hides) return;
	    drawBackground(canvas);
			
	    //int i;
	    RectangleF itemBounds;
	    fillPaint.setColor(ItemColor);
	    
	    /*for (i=1; i<=itemPos; i++) {
		    itemBounds = this.getItemBounds(i);
		    itemBounds = new RectangleF(itemBounds.x+itemSkinThickness,
		    		itemBounds.y+itemSkinThickness,itemBounds.
		    		width-2*itemSkinThickness,
		    		itemBounds.height-2*itemSkinThickness);
            canvas.drawRect(itemBounds.toRectF(), fillPaint);
	    }*/
	    if (itemPos>0) {
	    	itemBounds = this.getItemBounds(itemPos);
	    	android.graphics.RectF drawRect = new android.graphics.RectF();
	    	if (isVertOrHorz) {
	    		drawRect.left = bounds.x;
	    		drawRect.top = itemBounds.y;
	    		drawRect.right = bounds.x + bounds.width;
	    		drawRect.bottom = bounds.y + bounds.height;
	    				
	    	}
	    	else {
	    		drawRect.left = bounds.x;
	    		drawRect.top = bounds.y;
	    		drawRect.right = itemBounds.x + itemBounds.width;
	    		drawRect.bottom = bounds.y + bounds.height;
	    	}
	    	canvas.drawRect(drawRect, fillPaint);
	    }
    	}catch(Exception e) {
    		
    	}
    	}
    }

    

    RectangleF getItemBounds(int itemPos) {
	    PointF loc=null;
	    SizeF size;
	    
	    if (isVertOrHorz) {
	    	if (itemPos==0) return new RectangleF(bounds.x,bounds.y+bounds.height,0,0);
	    	loc = new PointF(bounds.x,bounds.y+bounds.height-(itemPos)*itemSize.height);
	    }
	    else {
	    	if (itemPos==0) return new RectangleF(bounds.x,bounds.y,0,0);
	    	loc = new PointF(bounds.x + (itemPos-1)*itemSize.width, bounds.y);
	    }
	    size = itemSize;
	    return new RectangleF(loc.x,loc.y,size.width,size.height);
    }
	
}