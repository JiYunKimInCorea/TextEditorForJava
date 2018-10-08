package com.gsoft.common.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.gsoft.common.ColorEx;
import com.gsoft.common.ContentManager;
import com.gsoft.common.Events;
import com.gsoft.common.Font;
import com.gsoft.common.PaintEx;
import com.gsoft.common.Sizing;
import com.gsoft.common.Util;
import com.gsoft.common.Font.FontSortVert;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util.Array;
import com.gsoft.common.Util.ArrayList;

import android.view.View;

public class Buttons {
	
	public static class TreeNodeButton extends Button {

		public TreeNodeButton(Object owner, String name, String text,
				int backColor, RectangleF srcBounds, boolean selectable,
				int alpha, boolean bRoundRect, float changeValueY) {
			super(owner, name, text, backColor, srcBounds, selectable, alpha, bRoundRect,
					changeValueY);
			// TODO Auto-generated constructor stub
		}

		/** Buttons[]*/
		ArrayList listOfChildButtons;
		
	}
	
	public static class ButtonGroup {
		/**버튼이 속해있는 그룹 내 버튼들의 인덱스들*/ 
	    public Byte[] indicesOfButtonsInGroup;
	    /**그룹 내 버튼들*/
		public Button[] buttons;
		public int indexOfSelectedButton;
		
		public ButtonGroup(Byte[] indicesOfButtonsInGroup, Button[] buttons) {
			this.indicesOfButtonsInGroup = indicesOfButtonsInGroup;
			this.buttons = buttons;
		}
	}
	
	public static class Button extends Control 
	{
		enum ButtonState
		{    
		    MouseDown,
		    Normal,
		    MouseOver
		}
	    
	    Bitmap texture;
	    ButtonState buttonState;
	    
	    boolean isTextOrImage;
	    float textSize;
	    
	    public boolean selectable;
	    public boolean isSelected;
	    
	    public boolean toggleable;
	            
	    //Object owner;
	    
	    /**text를 읽을 수는 있지만 쓸때는 setText를 사용한다*/
	    public String text;
	    float changeValueY;
	    boolean isTextIncludeNewLineChar;
	    int[] arrNewLineCharPos;
	    
	    public boolean bRoundRect;
	    
	    private ButtonGroup buttonGroup;
	    private int indexOfButtonInGroup;
		
		MotionEvent motionEvent;
		
		RectangleF[] arrLocAndSizeOfText = new RectangleF[20];
		int countOfArrLocAndSizeOfText;
		String[] arrSubText = new String[20];
	    
	    //2버튼들의 높이=50*6+28(버튼간격)*7=500,    왼쪽버튼의 top : 501-160=340/2=170+50=220
	    public static final Rectangle InitMenuButtonBounds = new Rectangle(0, 50+28+50*0, 40, 50);
	    public static final Rectangle InitLineButtonBounds = new Rectangle(0, 50+28*2+50, 40, 50);
	    
	    // public static Rectangle InitProgressBarBackgroundBounds = new Rectangle(877, 50, 40, 501);
	    // 3버튼들의 높이=30*4+100(버튼간격)*3=420,    왼쪽버튼의 top : 501-420=80/2=40+50=105
	    public static final Rectangle InitLeftButtonBounds = new Rectangle(0, 50+28*3+50*2, 40, 50); 
	    public static final Rectangle InitRightButtonBounds = new Rectangle(0, 50+28*4+50*3,40,50);
	    public static final Rectangle InitSpaceButtonBounds = new Rectangle(0, 50+28*5+50*4,40,50);
	    public static final Rectangle InitEnterButtonBounds = new Rectangle(0, 50+28*6+50*5,40,50);
	   
	    
	    public static int LeftButton = 0;
	    public static int RightButton = 1;
	    public static int SpaceButton = 2;
	    public static int EnterButton = 3;
	    public static int MenuButton = 4;
	    public static int LineButton = 5;
	    
	    public PaintEx paint = new PaintEx();
	    public Paint paintOfImageButton = new Paint();
	    public Paint paintOfBorder = new Paint();
	
	    //public static Texture2D TextureIsSelected;
	    public int ColorSelected = Color.MAGENTA;
	    public static int ColorMouseDowned = Color.YELLOW;
	    public static int BorderColor = Color.YELLOW;
	    
	    public int textColor;
	    
	    public void setBackColor(int backColor) {
	    	this.backColor = backColor;
	    	textColor = ColorEx.reverseColor(backColor);
	    	ColorSelected = ColorEx.darkerOrLighter(backColor, 0.3f);
	    }
	    
	    public boolean isManualOrAutoSize;
	
	   
	    public void setGroup(ButtonGroup buttonGroup, int indexOfButtonInGroup) {
	    	this.buttonGroup = buttonGroup;
	    	this.indexOfButtonInGroup = indexOfButtonInGroup;
	    }
	       
	
	    // 이미지 버튼
	    public Button(Object owner, String name, RectangleF srcBounds, int resid, 
	    		boolean selectable,	int alpha)
	    {
	    	super();
	    	this.owner = owner;
	        this.name = name;
	        //this.srcBounds = srcBounds;
	        this.bounds = new RectangleF(srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height);
	        buttonState = ButtonState.Normal;
	        hides = false;        
	        this.selectable = selectable;        
	        //paint.setStyle(Style.FILL);
	        paintOfImageButton.setStyle(Style.FILL);
	        paintOfImageButton.setAlpha(alpha);
	        texture = ContentManager.LoadBitmap(((View)owner).getContext(), resid);
	        isTextOrImage = false;
	        this.alpha = alpha;
	        paintOfBorder.setStyle(Style.STROKE);
	        paintOfBorder.setColor(ColorEx.darkerOrLighter(Color.LTGRAY, -100));        
	    }   
	    
	    // 텍스트 버튼
	    public Button(Object owner, String name, String text, int backColor, RectangleF srcBounds, 
	    		boolean selectable, int alpha, boolean bRoundRect, float changeValueY)
	    {
	    	super();
	    	this.owner = owner;
	        this.name = name;
	        this.bounds = new RectangleF(srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height);
	        
	        paint.setTypeface(Control.typefaceDefault);
	        
	        this.text = text;
	        setText(text);
	        setBackColor(backColor);
	        //this.srcBounds = srcBounds;
	        
	        buttonState = ButtonState.Normal;
	        hides = false;
	        this.selectable = selectable;
	        
	        paint.setStyle(Style.FILL);
	        isTextOrImage = true;
	        this.alpha = alpha;
	        this.bRoundRect = bRoundRect;
	        paintOfBorder.setStyle(Style.STROKE);
	        paintOfBorder.setColor(ColorEx.darkerOrLighter(backColor, -100));
	        this.changeValueY = changeValueY;
	    }
	    
	    public void changeBounds(RectangleF bounds) {
	    	this.bounds = bounds;
	    	if (text!=null) {
	    		setText(text);
	    	}
	    }
	    
	    public void changeBoundsFast(RectangleF bounds) {
	    	this.bounds = bounds;
	    }
	    
	    /*public void scale(SizeF scaleFactor) {
	    	bounds.x = (srcBounds.x * scaleFactor.width);
	    	bounds.y = (srcBounds.y * scaleFactor.height);
	    	bounds.width = (srcBounds.width * scaleFactor.width);
	    	bounds.height = (srcBounds.height * scaleFactor.height);
	    	if (isTextOrImage==false) {
	    		texture = Bitmap.createScaledBitmap(texture, (int)bounds.width, (int)bounds.height, false);
	    	}
	    	else {
	    		
	    	}
	    }*/
	    
	    /** 버튼은 세가지 상태를 갖는다.
	     * - toggleable상태 : toggleable, selectable 모두 true이다. toggle한다. 
	     * 그룹에 영향을 받지 않고 자신의 선택 상태만 반전한다.
	     * - selectable상태 : 그룹에 속해있는 다른 버튼의 select상태를 초기화하면서 
	     * 자신의 선택상태를 true로 바꾼다.
	     * - default상태 : 위에 있는 모든 기능을 하지 않고 listener만 호출한다.
	     */
	    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
	    	boolean r = super.onTouch(event, scaleFactor);
	    	if (!r) return false;
	    	
	    	if (event.actionCode==MotionEvent.ActionDown) {
	    		SetButtonState(ButtonState.MouseDown);
	    		if (toggleable) {
	    			Toggle();
	    		}
	    		else if (selectable) {
	    			// 그룹에 속해 있으면 다른 버튼들의 선택상태를 초기화하고 자신의 선택상태를
	    			// true로 바꾼다.
	    			if (buttonGroup!=null) {
	    				/*int i, indexOfButton;
	    				for (i=0; i<buttonGroup.indicesOfButtonsInGroup.length; i++) {
	    					indexOfButton = buttonGroup.indicesOfButtonsInGroup[i];
	    					if (buttonGroup.buttons[indexOfButton].selectable) {
	    						buttonGroup.buttons[indexOfButton].Select(false);
	    					}
	    				}*/
	    				if (buttonGroup.buttons[buttonGroup.indexOfSelectedButton].selectable) {
    						buttonGroup.buttons[buttonGroup.indexOfSelectedButton].Select(false);
    						buttonGroup.indexOfSelectedButton = indexOfButtonInGroup;
    					}
	    			}
	    			Select(true);
	    		}
	    		callTouchListener(this, event);
	    	}
	    	else if (event.actionCode==MotionEvent.ActionMove) {
	    		SetButtonState(ButtonState.MouseOver);
	    	}
	    	else if (event.actionCode==MotionEvent.ActionUp) {
	    		SetButtonState(ButtonState.Normal);
	    	}   	
	    	
	    	
	    	return true;
	    }
	        
	    
	    
	    public void SetButtonState(ButtonState myButtonState)
	    {
	        buttonState = myButtonState;
	    }
	
	    public void Select(boolean s)
	    {
	    	if (!toggleable) {
		        if (selectable)
		        {
		            isSelected = s;
		        }
	    	}
	    }
	    
	    public void Toggle() {
	    	if (toggleable && selectable) {
	    		isSelected = !isSelected;
	    	}
	    }
	    
	    public void setText(String paramText) {
	    	text = paramText;
	    	isTextIncludeNewLineChar = false;
	    	if (text==null || text.equals("")) return;
	    	
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
	    			text = new String(arrText);   
	    			isTextIncludeNewLineChar = false;
	    		}
	    	}
	    	
	    	if (!isTextIncludeNewLineChar) {
	    		if (!isManualOrAutoSize) {
		    		arrLocAndSizeOfText[0] = Font.getLocAndTextSize(paint, bounds, 
		    				text, FontSortVert.Middle, changeValueY);
		    		countOfArrLocAndSizeOfText = 1;
		    		arrSubText[0] = text;
	    		}
	    		else {
	    			arrLocAndSizeOfText[0] = Font.getLocAndTextSizeManual(paint, bounds, 
		    				text, FontSortVert.Middle, changeValueY);
		    		countOfArrLocAndSizeOfText = 1;
		    		arrSubText[0] = text.substring(0,(int)arrLocAndSizeOfText[0].height);
	    		}
			}
			else {
				float x = bounds.x;
				float width = bounds.width;
				float height = bounds.height / (arrNewLineCharPos.length+1);
				float y = bounds.y+(0*height);    				 
				RectangleF boundsLocal = new RectangleF(x,y,width,height);
				
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
					boundsLocal = new RectangleF(x,y,width,height);
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
	    
	    public void draw(Canvas canvas)
	    {
	    	synchronized(this) {
	    	try {
	    	if (hides) return;
	    	if (isTextOrImage==false) {	// 이미지버튼    		
		        if (!isSelected)
		        {	        	
		        	paintOfImageButton.setColor(Color.WHITE);
		        	paintOfImageButton.setAlpha(alpha);
		        	Rectangle src = new Rectangle(0,0,texture.getWidth(),texture.getHeight());
		        	RectangleF dst = this.bounds;
		        	canvas.drawBitmap(texture, src.toRect(), dst.toRectF(), paintOfImageButton);
		        	
		        	canvas.drawRect(bounds.toRectF(), paintOfBorder);
		        }
		        else
		        {	        	
		        	paintOfImageButton.setColor(ColorSelected);
		        	paintOfImageButton.setAlpha(alpha);
		        	Rectangle src = new Rectangle(0,0,texture.getWidth(),texture.getHeight());
		        	RectangleF dst = this.bounds;
		        	canvas.drawBitmap(texture, src.toRect(), dst.toRectF(), paintOfImageButton);
		        	
		        	canvas.drawRect(bounds.toRectF(), paintOfBorder);
		        }
	    	}
	    	else {		// 텍스트 버튼
	    		if (!isSelected)
		        {
	    			paint.setColor(backColor);
	    			if (bRoundRect==false) {
	    				canvas.drawRect(bounds.toRectF(), paint);    				
	    	        	canvas.drawRect(bounds.toRectF(), paintOfBorder);
	    			}
	    			else {
	    				float rx = bounds.width * 0.1f;
	    		        float ry = rx;
	    		        canvas.drawRoundRect(bounds.toRectF(), rx, ry, paint);    		        
	    	        	canvas.drawRoundRect(bounds.toRectF(), rx, ry, paintOfBorder);
	    			}
		        }
	    		else {    			
	    			paint.setColor(ColorSelected);
	    			if (bRoundRect==false) {
	    				canvas.drawRect(bounds.toRectF(), paint);    				
	    	        	canvas.drawRect(bounds.toRectF(), paintOfBorder);
	    			}
	    			else {
	    				float rx = bounds.width * 0.1f;
	    		        float ry = rx;
	    		        canvas.drawRoundRect(bounds.toRectF(), rx, ry, paint);    		        
	    	        	canvas.drawRoundRect(bounds.toRectF(), rx, ry, paintOfBorder);
	    			}
	    			
	    		}
	    		
	    		
				paint.setColor(textColor);
	    		for (int i=0; i<countOfArrLocAndSizeOfText; i++) {
	    			paint.setTextSize(arrLocAndSizeOfText[i].width);
	        		canvas.drawText(arrSubText[i], arrLocAndSizeOfText[i].x, 
	        				arrLocAndSizeOfText[i].y, paint);
	    		}
	    		
	    	}// 텍스트버튼
	    	}catch(Exception e) {
	    		
	    	}
	    	}
	    	
	    		
	    }	// draw
	    
	    public void draw(Canvas canvas, float x, float y)
	    {
	    	synchronized(this) {
	    	try {
	    	if (hides) return;
	    	if (isTextOrImage==false) {	// 이미지버튼    		
		        if (!isSelected)
		        {	        	
		        	paintOfImageButton.setColor(Color.WHITE);
		        	paintOfImageButton.setAlpha(alpha);
		        	Rectangle src = new Rectangle(0,0,texture.getWidth(),texture.getHeight());
		        	RectangleF dst = this.bounds;
		        	canvas.drawBitmap(texture, src.toRect(), dst.toRectF(), paintOfImageButton);
		        	
		        	canvas.drawRect(bounds.toRectF(), paintOfBorder);
		        }
		        else
		        {	        	
		        	paintOfImageButton.setColor(ColorSelected);
		        	paintOfImageButton.setAlpha(alpha);
		        	Rectangle src = new Rectangle(0,0,texture.getWidth(),texture.getHeight());
		        	RectangleF dst = this.bounds;
		        	canvas.drawBitmap(texture, src.toRect(), dst.toRectF(), paintOfImageButton);
		        	
		        	canvas.drawRect(bounds.toRectF(), paintOfBorder);
		        }
	    	}
	    	else {		// 텍스트 버튼
	    		if (!isSelected)
		        {
	    			paint.setColor(backColor);
	    			if (bRoundRect==false) {
	    				RectangleF dst = this.bounds;
	    				canvas.drawRect(dst.toRectF(), paint);    				
	    	        	canvas.drawRect(dst.toRectF(), paintOfBorder);
	    			}
	    			else {
	    				float rx = bounds.width * 0.1f;
	    		        float ry = rx;
	    		        RectangleF dst = this.bounds;
	    		        canvas.drawRoundRect(dst.toRectF(), rx, ry, paint);    		        
	    	        	canvas.drawRoundRect(dst.toRectF(), rx, ry, paintOfBorder);
	    			}
		        }
	    		else {    			
	    			paint.setColor(ColorSelected);
	    			if (bRoundRect==false) {
	    				RectangleF dst = this.bounds;
	    				canvas.drawRect(dst.toRectF(), paint);    				
	    	        	canvas.drawRect(dst.toRectF(), paintOfBorder);
	    			}
	    			else {
	    				float rx = bounds.width * 0.1f;
	    		        float ry = rx;
	    		        RectangleF dst = this.bounds;
	    		        canvas.drawRoundRect(dst.toRectF(), rx, ry, paint);    		        
	    	        	canvas.drawRoundRect(dst.toRectF(), rx, ry, paintOfBorder);
	    			}
	    			
	    		}
	    		
	    		
				paint.setColor(textColor);
	    		for (int i=0; i<countOfArrLocAndSizeOfText; i++) {
	    			paint.setTextSize(arrLocAndSizeOfText[i].width);
	        		canvas.drawText(arrSubText[i], x+arrLocAndSizeOfText[i].x, 
	        				y+arrLocAndSizeOfText[i].y, paint);
	    		}
	    		
	    	}// 텍스트버튼
	    	}catch(Exception e) {
	    		
	    	}
	    	}
	    	
	    		
	    }	// draw
	    
	    
	}
}
