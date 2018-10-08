package com.gsoft.common.gui;

import com.gsoft.common.Font;
import com.gsoft.common.PaintEx;
import com.gsoft.common.Font.FontSortVert;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Util.Array;

import android.graphics.Canvas;
import android.graphics.Paint.Style;


public class Static extends Control
{
    float textSize;
    //Object owner;
    
    String text;
    
    boolean isTextIncludeNewLineChar;
    int[] arrNewLineCharPos;
    
    RectangleF[] arrLocAndSizeOfText = new RectangleF[20];
	int countOfArrLocAndSizeOfText;
	String[] arrSubText = new String[20];
	
	float changeValueY;
    
    PaintEx paint;
    
    public Static(Object owner, String text, int textColor, Rectangle bounds)
    {
    	super();
    	this.owner = owner;
        //this.text = text;
        this.textColor = textColor;
        this.bounds = bounds;
        //this.bounds = new RectangleF();
        hides = false;
        paint = new PaintEx();
        paint.setStyle(Style.STROKE);
        setText(text);
    }
    
    public void changeBounds(Rectangle bounds) {
    	this.bounds = bounds;
    	if (text!=null) {
    		setText(text);
    	}
    }
    
    public void setText(String text) {
    	this.text = text;
    	isTextIncludeNewLineChar = false;
    	if (text==null) return;
    	
    	int i;
		int count = 0;
		int textLen = text.length();
		for (i=0; i<textLen; i++) {
			if (text.substring(i,i+1).equals("\n")) {
				isTextIncludeNewLineChar = true;
				count++;    				
			}
		}    		
		if (isTextIncludeNewLineChar) {
			arrNewLineCharPos = new int[count];
			int index;
    		for (i=0, index=0; i<textLen; i++) {
    			if (text.substring(i,i+1).equals("\n")) {
    				arrNewLineCharPos[index++] = i;
    			}
    		}
    		if (bounds.width>bounds.height) {
    			char[] arrText = text.toCharArray();
    			for (i=0; i<arrNewLineCharPos.length; i++) {
    				arrText = Array.Delete(arrText, arrNewLineCharPos[i], 1);
    				for (int j=i+1; j<arrNewLineCharPos.length; j++) {
    					arrNewLineCharPos[j]--;
    				}
    			}
    			this.text = new String(arrText);   
    			isTextIncludeNewLineChar = false;
    		}
    	}
    	
    	if (!isTextIncludeNewLineChar) {
    		arrLocAndSizeOfText[0] = Font.getLocAndTextSize(paint, bounds, 
    				text, FontSortVert.Middle, changeValueY);
    		countOfArrLocAndSizeOfText = 1;
    		arrSubText[0] = text;
		}
		else {
			int x = bounds.x;
			int width = bounds.width;
			int height = bounds.height / (arrNewLineCharPos.length+1);
			int y = bounds.y+(0*height);    				 
			Rectangle boundsLocal = new Rectangle(x,y,width,height);
			
			int start=0, end;
			start = 0;
			end = arrNewLineCharPos[0];
			String textLocal = text.substring(start, end);
			arrLocAndSizeOfText[0] = Font.getLocAndTextSize(paint, boundsLocal, 
					textLocal, FontSortVert.Middle, changeValueY);
    		countOfArrLocAndSizeOfText = 1;
    		arrSubText[0] = textLocal;
    		
			for (i=0; i<arrNewLineCharPos.length; i++) {    				
				y = bounds.y+(i+1)*height;    				 
				boundsLocal = new Rectangle(x,y,width,height);
				if (i==arrNewLineCharPos.length-1) {
					start = arrNewLineCharPos[i]+1;
					end = text.length();
				}
				else {
					start = arrNewLineCharPos[i]+1;
					end = arrNewLineCharPos[i+1];
				}    				
				
				textLocal = text.substring(start, end);				
				arrLocAndSizeOfText[countOfArrLocAndSizeOfText] = 
					Font.getLocAndTextSize(paint, boundsLocal, 
						textLocal, FontSortVert.Middle, changeValueY);
				arrSubText[countOfArrLocAndSizeOfText++] = textLocal;
			}
		}
	}
    
    @Override
    public void draw(Canvas canvas)
    {
    	//synchronized(this) {
    	try{
    	if (hides) return;
		
		int i;
		paint.setColor(textColor);
		
		for (i=0; i<countOfArrLocAndSizeOfText; i++) {
			paint.setTextSize(arrLocAndSizeOfText[i].height);			
    		canvas.drawText(arrSubText[i], arrLocAndSizeOfText[i].x, 
    				arrLocAndSizeOfText[i].y, paint);
		}
    	}catch(Exception e) {
    		
    	}
    	//}
    }

}