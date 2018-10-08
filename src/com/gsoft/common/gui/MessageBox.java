package com.gsoft.common.gui;

import com.gsoft.common.Sizing.Rectangle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.view.View;

public class MessageBox extends Control
{
    public String message;
    public boolean isOpen;
    int colorBackground;   
    Paint paint = new Paint();
    
    float textSize;

   
    public MessageBox(View owner, Rectangle bounds, float textSize)
    {
    	super();
    	this.owner = owner;
    	paint.setStyle(Style.STROKE);
    	colorBackground = Color.TRANSPARENT;
    	this.bounds = bounds;
    	this.textSize = textSize;
        
    }    
   
    
    public void drawBackground(Canvas canvas) {
    	canvas.drawColor(colorBackground);
    }

    public void draw(Canvas canvas)
    {
    	try{
    	if (!isOpen) return;
    	if (message == null || message.equals("")) return;
        drawBackground(canvas);        
        
        paint.setColor(Color.RED);
        paint.setTextSize(textSize);
        int messageWidth = (int)paint.measureText(message, 0, message.length()-1);
    	Point locText = new Point();
    	locText.x = (int) (bounds.x + bounds.width/2 - messageWidth/2);
    	locText.y = (int) (bounds.y + bounds.height/2 - textSize/2);
        
    	
        canvas.drawText(message, locText.x, locText.y, paint);
    	}catch(Exception e) {
    		
    	}
    }
}