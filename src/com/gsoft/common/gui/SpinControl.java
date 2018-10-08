package com.gsoft.common.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;

import com.gsoft.common.ContentManager;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.R.R;

public class SpinControl extends Control 
{
	//Rectangle srcBounds;
	//Rectangle bounds;
	//Object owner;
    Paint paint;
    int alpha = 255;
    
    Rectangle boundsUp;
    Rectangle boundsDown;
    
    public boolean isUpClicked;
    public boolean isDownClicked;
    
    Bitmap textureUp;
    Bitmap textureDown;
    
    public boolean isVertOrHorz;
    
    public boolean isSeparate;
    
    Rectangle boundsLeft;
    Rectangle boundsRight;
    
    public boolean isLeftClicked;
    public boolean isRightClicked;
    
    Bitmap textureLeft;
    Bitmap textureRight;
    
    int incxForBitmapRendering;
	int incyForBitmapRendering;
	
	public SpinControl(Object owner, Context context, Rectangle srcBounds, 
			boolean isSeparate, boolean isVertOrHorz) {
		super();
		this.owner = owner;
		//this.srcBounds = srcBounds;
		this.bounds = new Rectangle(srcBounds.x,srcBounds.y,srcBounds.width,srcBounds.height);		
		this.isVertOrHorz = isVertOrHorz;
		this.isSeparate = isSeparate;
				
		if (isVertOrHorz) {
			if (!isSeparate) {
				boundsUp = new Rectangle(bounds.x, bounds.y, 
						bounds.width, bounds.height/2);
		    	boundsDown = new Rectangle(bounds.x, boundsUp.bottom(), 
		    			bounds.width, bounds.height/2);
			}
			else {
				//float h = bounds.height * 0.4f;
				int h = (int) (Control.view.getHeight()*0.045f);
				if (2*h<bounds.height) {
					boundsUp = new Rectangle(bounds.x, bounds.y, 
							bounds.width, h);
			    	boundsDown = new Rectangle(bounds.x, bounds.y+bounds.height-h, 
			    			bounds.width, h);			
				}
				else {
					boundsUp = new Rectangle(bounds.x, bounds.y, 
							bounds.width, bounds.height/2);
			    	boundsDown = new Rectangle(bounds.x, boundsUp.bottom(), 
			    			bounds.width, bounds.height/2);
				}
			}
	    	    	
	    	textureUp = ContentManager.LoadBitmap(context, R.drawable.spin_up);
	    	textureDown = ContentManager.LoadBitmap(context, R.drawable.spin_down);
		}
		else {
			if (!isSeparate) {
				boundsLeft = new Rectangle(bounds.x, bounds.y, 
						bounds.width/2, bounds.height);
		    	boundsRight = new Rectangle(boundsLeft.right(), bounds.y, 
		    			bounds.width/2, bounds.height);
			}
			else {
				//float w = bounds.width * 0.1f;
				int w = (int) (Control.view.getHeight()*0.045f);
				boundsLeft = new Rectangle(bounds.x, bounds.y, 
						w, bounds.height);
		    	boundsRight = new Rectangle(bounds.right()-w, bounds.y, 
		    			w, bounds.height);			
			}
	    	    	
	    	textureLeft = ContentManager.LoadBitmap(context, R.drawable.spin_left);
	    	textureRight = ContentManager.LoadBitmap(context, R.drawable.spin_right);
			
		}
    	
    	paint = new Paint();
    	paint.setStyle(Style.FILL);
    	
    	
	}
	
	public void changeBounds(Rectangle bounds) {
		this.bounds = bounds;
		if (isVertOrHorz) {
			if (!isSeparate) {
				boundsUp.x = bounds.x;
				boundsUp.y = bounds.y; 
				boundsUp.width = bounds.width;
				boundsUp.height = bounds.height/2;
				
		    	boundsDown.x = bounds.x;
		    	boundsDown.y = boundsUp.bottom(); 
		    	boundsDown.width = bounds.width;
		    	boundsDown.height = bounds.height/2;
			}
			else {
				//float h = bounds.height * 0.4f;
				int h = (int) (Control.view.getHeight()*0.045f);
				if (2*h<bounds.height) {
					boundsUp.x = bounds.x; 
					boundsUp.y = bounds.y; 
					boundsUp.width = bounds.width; 
					boundsUp.height = h;
					
			    	boundsDown.x = bounds.x; 
			    	boundsDown.y = bounds.y+bounds.height-h; 
			    	boundsDown.width = bounds.width; 
			    	boundsDown.height =	h;
				}
				else {
					boundsUp.x = bounds.x; 
					boundsUp.y = bounds.y; 
					boundsUp.width = bounds.width; 
					boundsUp.height = bounds.height/2;
					
			    	boundsDown.x = bounds.x; 
			    	boundsDown.y = boundsUp.bottom(); 
			    	boundsDown.width = bounds.width; 
			    	boundsDown.height =	bounds.height/2;
				}
			}
		}
		else {
			if (!isSeparate) {
				boundsLeft.x = bounds.x; 
				boundsLeft.y = bounds.y; 
				boundsLeft.width = bounds.width/2; 
				boundsLeft.height =	bounds.height;
				
		    	boundsRight.x = boundsLeft.right(); 
		    	boundsRight.y = bounds.y;
		    	boundsRight.width =	bounds.width/2; 
		    	boundsRight.height = bounds.height;
			}
			else {
				//float w = bounds.width * 0.1f;
				int w = (int) (Control.view.getHeight()*0.045f);
				boundsLeft.x = bounds.x; 
				boundsLeft.y = bounds.y; 
				boundsLeft.width = w; 
				boundsLeft.height = bounds.height;
				
		    	boundsRight.x = bounds.right()-w; 
		    	boundsRight.y =	bounds.y; 
		    	boundsRight.width =	w; 
		    	boundsRight.height = bounds.height;			
			}
			
		}

	}
	
	/*public void scale(SizeF scaleFactor) {
    	bounds.x = (int) (srcBounds.x * scaleFactor.width);
    	bounds.y = (int) (srcBounds.y * scaleFactor.height);
    	bounds.width = (int) (srcBounds.width * scaleFactor.width);
    	bounds.height = (int) (srcBounds.height * scaleFactor.height);
    	
    	boundsUp = new RectangleF(bounds.x, bounds.y, bounds.width, bounds.height/2);
    	boundsDown = new RectangleF(bounds.x, boundsUp.bottom(), bounds.width, bounds.height/2);
    }*/
	
	/*public void setOnTouchListener(OnTouchListener listener) {
    	this.receiver = listener;
    }*/
	
	@Override
	public boolean IsPointIn(Point point)
    {
		if (isVertOrHorz) {
			if (boundsUp.x <= point.x && point.x <= boundsUp.x + boundsUp.width  &&
		        boundsUp.y <= point.y && point.y <= boundsUp.y + boundsUp.height ) {
				isUpClicked = true;
				isDownClicked = false;
		        return true;
			}
			
			if (boundsDown.x <= point.x && point.x <= boundsDown.x + boundsDown.width  &&
			    boundsDown.y <= point.y && point.y <= boundsDown.y + boundsDown.height ) {
				isUpClicked = false;
				isDownClicked = true;
			    return true;
			}
			isUpClicked = false;
			isDownClicked = false;
		}
		else {
			if (boundsLeft.x <= point.x && point.x <= boundsLeft.x + boundsLeft.width  &&
				boundsLeft.y <= point.y && point.y <= boundsLeft.y + boundsLeft.height ) {
				isLeftClicked = true;
				isRightClicked = false;
		        return true;
			}
			
			if (boundsRight.x <= point.x && point.x <= boundsRight.x + boundsRight.width  &&
			    boundsRight.y <= point.y && point.y <= boundsRight.y + boundsRight.height ) {
				isLeftClicked = false;
				isRightClicked = true;
			    return true;
			}
			isLeftClicked = false;
			isRightClicked = false;
			
		}
        return false;
    }
    
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	if (IsPointIn(new Point(event.x, event.y))==false) return false;
    	
    	if (event.actionCode==MotionEvent.ActionDown) {
    		callTouchListener(this, event);
    		
    	}
    	else if (event.actionCode==MotionEvent.ActionDoubleClicked) {
    		callTouchListener(this, event);
    		//callTouchListener(this, event);    		
    	}
    	else if (event.actionCode==MotionEvent.ActionMove) {
    		
    	}
    	else if (event.actionCode==MotionEvent.ActionUp) {
    		
    	}   	
    	
    	return true;
    }
    
    public void draw(Canvas canvas)
    {
    	synchronized(this) {
    	try{
    	paint.setAlpha(alpha);
    	if (this.isVertOrHorz) {
	    	Rectangle srcUp = new Rectangle(0,0,textureUp.getWidth(),textureUp.getHeight());
	    	RectF dst = RectangleF.toRectF(boundsUp, this.incxForBitmapRendering, this.incyForBitmapRendering);
	    	canvas.drawBitmap(textureUp, srcUp.toRect(), dst, paint);
	    	
	    	Rectangle srcDown = new Rectangle(0,0,textureDown.getWidth(),textureDown.getHeight());
	    	dst = RectangleF.toRectF(boundsDown, this.incxForBitmapRendering, this.incyForBitmapRendering);
	    	canvas.drawBitmap(textureDown, srcDown.toRect(), dst, paint);
    	}
    	else {
    		Rectangle srcLeft = new Rectangle(0,0,textureLeft.getWidth(),textureLeft.getHeight());
    		RectF dst = RectangleF.toRectF(boundsLeft, this.incxForBitmapRendering, this.incyForBitmapRendering);
	    	canvas.drawBitmap(textureLeft, srcLeft.toRect(), dst, paint);
	    	
	    	Rectangle srcRight = new Rectangle(0,0,textureRight.getWidth(),textureRight.getHeight());
	    	dst = RectangleF.toRectF(boundsRight, this.incxForBitmapRendering, this.incyForBitmapRendering);
	    	canvas.drawBitmap(textureRight, srcRight.toRect(), dst, paint);
    		
    	}
    	}catch(Exception e) {
    		
    	}
    	}
    }
    
    	
	
}