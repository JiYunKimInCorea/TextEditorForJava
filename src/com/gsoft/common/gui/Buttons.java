package com.gsoft.common.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.RectF;

import com.gsoft.common.ColorEx;
import com.gsoft.common.CommonGUI_SettingsDialog;
import com.gsoft.common.ContentManager;
import com.gsoft.common.Font;
import com.gsoft.common.PaintEx;
import com.gsoft.common.Font.FontSortVert;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util.Array;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.gui.SettingsDialog.Settings;

import android.view.View;

public class Buttons {
	
	public static class TreeNodeButton extends Button {

		public TreeNodeButton(Object owner, String name, String text,
				int backColor, Rectangle srcBounds, boolean selectable,
				int alpha, boolean bRoundRect, float changeValueY, Object addedInfo, int colorOfPlaceOfButtonLying) {
			super(owner, name, text, backColor, srcBounds, selectable, alpha, bRoundRect,
					changeValueY, addedInfo, colorOfPlaceOfButtonLying);
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
		
		protected static Settings settings = CommonGUI_SettingsDialog.settings;
		
		public static boolean isTripleBuffering = settings.isTripleBuffering;
		
	    
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
		
		/** 버튼의 텍스트 외에 부가정보를 갖을수있다.*/
		public Object addedInfo;
		
		int incxForBitmapRendering;
		int incyForBitmapRendering;
	    
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
	    	//ColorSelected = Color.DKGRAY;
	    	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering) {
	    		this.drawToImage(mCanvas);
	    	}
	    }
	    
	    public boolean isManualOrAutoSize;
	    
	    int colorOfPlaceOfButtonLying;

		private Rectangle oldBounds = new Rectangle(0,0,0,0);
	
	   
	    public void setGroup(ButtonGroup buttonGroup, int indexOfButtonInGroup) {
	    	this.buttonGroup = buttonGroup;
	    	this.indexOfButtonInGroup = indexOfButtonInGroup;
	    }
	       
	
	    // 이미지 버튼
	    public Button(Object owner, String name, Rectangle srcBounds, int resid, 
	    		boolean selectable,	int alpha)
	    {
	    	super();
	    	this.owner = owner;
	        this.name = name;
	        //this.srcBounds = srcBounds;
	        this.bounds = new Rectangle(srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height);
	        
	        if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering) {
	        	 // 이미지로 그리기
		        this.bitmapForRendering = 
	 					Bitmap.createBitmap(this.bounds.width, this.bounds.height, CommonGUI_SettingsDialog.settings.bufferedImageType);
	 			mCanvas = new Canvas(this.bitmapForRendering);
	        }
	     			
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
	        
	        if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering) {
		        	drawToImage(mCanvas);
		        }
	    }   
	    
	    // 텍스트 버튼
	    public Button(Object owner, String name, String text, int backColor, Rectangle srcBounds, 
	    		boolean selectable, int alpha, boolean bRoundRect, float changeValueY, Object addedInfo, int colorOfPlaceOfButtonLying)
	    {
	    	super();
	    	this.owner = owner;
	        this.name = name;
	        this.bounds = new Rectangle(srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height);
	        
	        if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering) {
			        // 이미지로 그리기
					this.bitmapForRendering = 
							Bitmap.createBitmap(this.bounds.width, this.bounds.height, CommonGUI_SettingsDialog.settings.bufferedImageType);
					mCanvas = new Canvas(this.bitmapForRendering);
		        }
	        
	        paint.setTypeface(Control.typefaceDefault);
	        
	        this.text = text;
	        
	        setBackColor(backColor);
	        //this.srcBounds = srcBounds;
	        
	        buttonState = ButtonState.Normal;
	        hides = false;
	        this.selectable = selectable;
	        
	        paint.setStyle(Style.FILL);
	        isTextOrImage = true;
	        this.alpha = alpha;
	        
	        this.bRoundRect = bRoundRect;
	        if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering) {
	        	this.bRoundRect = false;
	        }
	        paintOfBorder.setStyle(Style.STROKE);
	        paintOfBorder.setColor(ColorEx.darkerOrLighter(backColor, -100));
	        this.changeValueY = changeValueY;
	        this.addedInfo = addedInfo;
	        
	        setText(text);
	        
	        if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering) {
		        drawToImage(mCanvas);
		    }
	        
	        this.colorOfPlaceOfButtonLying = colorOfPlaceOfButtonLying;
	    }
	    
	    public void changeBounds(Rectangle bounds) {
	    	if (this.oldBounds.equals(bounds)) {
	    		if (text!=null) {
		    		setText(text);
		    	}
	    		return;
	    	}
	    	oldBounds.copy(bounds);
	    	this.bounds = bounds;
	    	
	    	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering) {
			    	// 이미지로 그리기
			    	this.bitmapForRendering = 
							Bitmap.createBitmap(this.bounds.width, this.bounds.height, CommonGUI_SettingsDialog.settings.bufferedImageType);
					mCanvas = new Canvas(this.bitmapForRendering);
		    	}
	    				
	    	if (text!=null) {
	    		setText(text);
	    	}
	    }
	    
	    public void changeBoundsFast(Rectangle bounds) {
	    	this.bounds = bounds;
	    	
	    	// 이미지로 그리기
	    	/*this.bitmapForRendering = 
					Bitmap.createBitmap((int)this.bounds.width+1, (int)this.bounds.height+1, null);
			mCanvas = new Canvas(this.bitmapForRendering);*/
	    	
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
	    	
	    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
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
	    	
	    	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering) {
		    		drawToImage(mCanvas);
		    	}
	    	
	    	return true;
	    }
	        
	    
	    
	    public void SetButtonState(ButtonState myButtonState)
	    {
	        buttonState = myButtonState;
	    }
	
	    public void Select(boolean s)
	    {
	    	//if (!toggleable) {
		        if (selectable)
		        {
		        	if (this.text.equals("desktop.ini")) {
		        		int a;
		        		a=0;
		        		a++;
		        	}
		            isSelected = s;
		            
		            if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
			    			isTripleBuffering) {
			            	drawToImage(mCanvas);
			            }
		        }
	    	//}
	    }
	    
	    public void Toggle() {
	    	if (toggleable && selectable) {
	    		isSelected = !isSelected;
	    		if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
		    			isTripleBuffering) {
		    			drawToImage(mCanvas);
		    		}
	    	}
	    }
	    
	    public synchronized void setText(String paramText) {
	    	try{
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
	    		if (text.equals("desktop.ini")) {
	    			int a;
	    			a=0;
	    			a++;
	    		}
	    		if (!isManualOrAutoSize) {
		    		arrLocAndSizeOfText[0] = Font.getLocAndTextSize(paint, bounds, 
		    				text, FontSortVert.Middle, changeValueY);
		    		// 원점에서 상대적인 좌표이다.
		    		arrLocAndSizeOfText[0].x -= bounds.x;
		    		arrLocAndSizeOfText[0].y -= bounds.y;
		    		
		    		countOfArrLocAndSizeOfText = 1;
		    		arrSubText[0] = text;
	    		}
	    		else {
	    			arrLocAndSizeOfText[0] = Font.getLocAndTextSizeManual(paint, bounds, 
		    				text, FontSortVert.Middle, changeValueY);
	    			// 원점에서 상대적인 좌표이다.
		    		arrLocAndSizeOfText[0].x -= bounds.x;
		    		arrLocAndSizeOfText[0].y -= bounds.y;
		    		
		    		countOfArrLocAndSizeOfText = 1;
		    		arrSubText[0] = text.substring(0,(int)arrLocAndSizeOfText[0].height);
	    		}
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
				// 원점에서 상대적인 좌표이다.
	    		arrLocAndSizeOfText[0].x -= bounds.x;
	    		arrLocAndSizeOfText[0].y -= bounds.y;
	    		
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
					// 원점에서 상대적인 좌표이다.
		    		arrLocAndSizeOfText[countOfArrLocAndSizeOfText].x -= bounds.x;
		    		arrLocAndSizeOfText[countOfArrLocAndSizeOfText].y -= bounds.y;
		    		
					arrSubText[countOfArrLocAndSizeOfText++] = textLocal;
				}
			}
	    	}
	    	finally {
	    		if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
		    			isTripleBuffering) {
		    			drawToImage(mCanvas);
		    		}
	    	}
		}
	    
	    /** CustomView의 비트맵에 컨트롤이 갖는 bitmapForRendering 비트맵(트리플 버퍼링)을 그린다.
	     * @param canvas : CustomView의 mCanvas(내부에 비트맵을 가지므로 더블버퍼링이다.)*/
	    public synchronized void draw(Canvas canvas) {
	    	if (this.iName==134) {
	    		int a;
	    		a=0;
	    		a++;
	    	}
	    	
	    	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering) {
		    	Rect src = (new Rectangle(0,0,bounds.width,bounds.height)).toRect();
				
				RectF dst = this.bounds.toRectF();
				canvas.drawBitmap(this.bitmapForRendering, src, dst, paint);
	    	}
	    	else {
	    		drawCommon(canvas);
	    	}
		}
	    
	    /**isTripleBuffering이 true이면 mCanvas 안에 있는 bitmapForRendering 비트맵에 그린다. 
	     * 비트멥은 원점부터 시작하므로 bounds에서 bounds.x와 bounds.y를 빼서 그려야 비트맵에 그릴 수 있다.<br>
	     * isTripleBuffering이 false이면 현재 bounds에 그린다.
	     *  @param canvas : isTripleBuffering이 true이면 컨트롤이 갖고 있는 mCanvas이고<br>
	     *  CustomView의 mCanvas(내부에 비트맵을 가지므로 더블버퍼링이다.)
	     *  */
	    public synchronized void drawCommon(Canvas canvas) {
	    	if (this.iName==134) {
	    		int a;
	    		a=0;
	    		a++;
	    	}
	    	synchronized(this) {
	    	try {
	    	if (hides) return;
	    	if (isTextOrImage==false) {	// 이미지버튼    		
		        if (!isSelected)
		        {	        	
		        	paintOfImageButton.setColor(Color.WHITE);
		        	paintOfImageButton.setAlpha(alpha);
		        	Rectangle src = new Rectangle(0,0,texture.getWidth(),texture.getHeight());
		        	//RectangleF dst = this.bounds;
		        	RectF dst;
		        	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
		        			isTripleBuffering) {
		        		dst = RectangleF.toRectF(bounds, bounds.x, bounds.y);
		        	}
		        	else {
		        		dst = this.bounds.toRectF();
		        	}
		        	canvas.drawBitmap(texture, src.toRect(), dst, paintOfImageButton);
		        	
		        	canvas.drawRect(dst, paintOfBorder);
		        }
		        else
		        {	        	
		        	paintOfImageButton.setColor(ColorSelected);
		        	paintOfImageButton.setAlpha(alpha);
		        	Rectangle src = new Rectangle(0,0,texture.getWidth(),texture.getHeight());
		        	//RectangleF dst = this.bounds;
		        	//RectF dst = RectangleF.toRectF(bounds, bounds.x, bounds.y);
		        	RectF dst;
		        	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
		        			isTripleBuffering) {
		        		dst = RectangleF.toRectF(bounds, bounds.x, bounds.y);
		        	}
		        	else {
		        		dst = this.bounds.toRectF();
		        	}
		        	canvas.drawBitmap(texture, src.toRect(), dst, paintOfImageButton);
		        	
		        	canvas.drawRect(dst, paintOfBorder);
		        }
	    	}
	    	else {		// 텍스트 버튼
	    		if (!isSelected)
		        {
	    			paint.setColor(backColor);
	    			if (bRoundRect==false) {
	    				//RectF dst = RectangleF.toRectF(bounds, bounds.x, bounds.y);
	    				RectF dst;
			        	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
			        			isTripleBuffering) {
			        		dst = RectangleF.toRectF(bounds, bounds.x, bounds.y);
			        	}
			        	else {
			        		dst = this.bounds.toRectF();
			        	}
			        	
	    				canvas.drawRect(dst, paint);    				
	    	        	canvas.drawRect(dst, paintOfBorder);
	    			}
	    			else {
	    				float rx = bounds.width * 0.1f;
	    		        float ry = rx;
	    		        //RectF dst = RectangleF.toRectF(bounds, bounds.x, bounds.y);
	    		        RectF dst;
			        	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
			        			isTripleBuffering) {
			        		dst = RectangleF.toRectF(bounds, bounds.x, bounds.y);
			        	}
			        	else {
			        		dst = this.bounds.toRectF();
			        	}
			        	//paint.setColor(colorOfPlaceOfButtonLying);
			        	//canvas.drawRect(dst, paint); // 버튼 뒤 배경컬러
			        	
			        	paint.setColor(backColor);
	    		        canvas.drawRoundRect(dst, rx, ry, paint);    		        
	    	        	canvas.drawRoundRect(dst, rx, ry, paintOfBorder);
	    			}
		        }
	    		else {    			
	    			paint.setColor(ColorSelected);
	    			if (bRoundRect==false) {
	    				//RectF dst = RectangleF.toRectF(bounds, bounds.x, bounds.y);
	    				RectF dst;
			        	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
			        			isTripleBuffering) {
			        		dst = RectangleF.toRectF(bounds, bounds.x, bounds.y);
			        	}
			        	else {
			        		dst = this.bounds.toRectF();
			        	}
	    				canvas.drawRect(dst, paint);    				
	    	        	canvas.drawRect(dst, paintOfBorder);
	    	        	
	    	        	
	    			}
	    			else {
	    				float rx = bounds.width * 0.1f;
	    		        float ry = rx;
	    		        //RectF dst = RectangleF.toRectF(bounds, bounds.x, bounds.y);
	    		        RectF dst;
			        	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
			        			isTripleBuffering) {
			        		dst = RectangleF.toRectF(bounds, bounds.x, bounds.y);
			        	}
			        	else {
			        		dst = this.bounds.toRectF();
			        	}
			        	//paint.setColor(colorOfPlaceOfButtonLying);
			        	//canvas.drawRect(dst, paint); // 버튼 뒤 배경컬러
			        	
			        	paint.setColor(ColorSelected);
	    		        canvas.drawRoundRect(dst, rx, ry, paint);    		        
	    	        	canvas.drawRoundRect(dst, rx, ry, paintOfBorder);
	    	        	
	    			}
	    		}	    		
	    		
				paint.setColor(textColor);
	    		for (int i=0; i<countOfArrLocAndSizeOfText; i++) {
	    			paint.setTextSize(arrLocAndSizeOfText[i].width);
	    			if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    					isTripleBuffering) {
		    			// arrLocAndSizeOfText[i].x, arrLocAndSizeOfText[i].y 에는
		    			// 원점에서 상대적인 좌표가 들어있으므로 바꿀 필요가 없다.	    			
		        		canvas.drawText(arrSubText[i], arrLocAndSizeOfText[i].x, 
		        				arrLocAndSizeOfText[i].y, paint);
	    			}
	    			else {
	    				canvas.drawText(arrSubText[i], arrLocAndSizeOfText[i].x+bounds.x, 
		        				arrLocAndSizeOfText[i].y+bounds.y, paint);
	    			}
	    		}
	    		
	    	}// 텍스트버튼
	    	}catch(Exception e) {
	    		
	    	}
	    	}
	    	
	    }
	    
	    /**mCanvas 안에 있는 bitmapForRendering 비트맵에 그린다. 
	     * 비트멥은 원점부터 시작하므로 bounds에서 bounds.x와 bounds.y를 빼서 그려야 비트맵에 그릴 수 있다.
	     *  @param canvas : 컨트롤이 갖고 있는 mCanvas*/
	    public synchronized void drawToImage(Canvas canvas)
	    {
	    	drawCommon(canvas);
	    		
	    }	// draw
	    
	    
	    /** MenuWithScrollBar의 draw()에서 호출된다.
	     * isTripleBuffering이 true 이면 CustomView의 비트맵에 컨트롤이 갖는 bitmapForRendering 비트맵(트리플 버퍼링)을 그린다.<br>
	     * isTripleBuffering이 false 이면 CustomView의 비트맵에 컨트롤의 bounds 에 그린다.
	     * @param canvas : CustomView의 mCanvas(내부에 비트맵을 가지므로 더블버퍼링이다.)*/
	    public synchronized void draw(Canvas canvas, int x, int y)
	    {
	    	if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava) &&
	    			isTripleBuffering) {
		    	Rect src = (new Rectangle(0,0,bounds.width,bounds.height)).toRect();
				
				RectF dst = Rectangle.toRectF(this.bounds.toRect(), -this.bounds.x + x, -this.bounds.y + y);
				canvas.drawBitmap(this.bitmapForRendering, src, dst, paint);
	    	}
	    	else {
	    		draw(canvas, (float)x, (float)y);
	    	}
			
	    		
	    }	// draw
	    
	    
	    /** MenuWithScrollBar의 draw()에서 호출된다.
	     * CustomView의 비트맵에 컨트롤의 bounds 에 그린다.
	     * @param canvas : CustomView의 mCanvas(내부에 비트맵을 가지므로 더블버퍼링이다.)*/ 
	    public synchronized void draw(Canvas canvas, float x, float y)
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
		        	Rectangle dst = this.bounds;
		        	canvas.drawBitmap(texture, src.toRect(), dst.toRectF(), paintOfImageButton);
		        	
		        	canvas.drawRect(bounds.toRectF(), paintOfBorder);
		        }
		        else
		        {	        	
		        	paintOfImageButton.setColor(ColorSelected);
		        	paintOfImageButton.setAlpha(alpha);
		        	Rectangle src = new Rectangle(0,0,texture.getWidth(),texture.getHeight());
		        	Rectangle dst = this.bounds;
		        	canvas.drawBitmap(texture, src.toRect(), dst.toRectF(), paintOfImageButton);
		        	
		        	canvas.drawRect(bounds.toRectF(), paintOfBorder);
		        }
	    	}
	    	else {		// 텍스트 버튼
	    		if (!isSelected)
		        {
	    			paint.setColor(backColor);
	    			if (bRoundRect==false) {
	    				Rectangle dst = this.bounds;
	    				canvas.drawRect(dst.toRectF(), paint);    				
	    	        	canvas.drawRect(dst.toRectF(), paintOfBorder);
	    			}
	    			else {
	    				float rx = bounds.width * 0.1f;
	    		        float ry = rx;
	    		        Rectangle dst = this.bounds;
	    		        canvas.drawRoundRect(dst.toRectF(), rx, ry, paint);    		        
	    	        	canvas.drawRoundRect(dst.toRectF(), rx, ry, paintOfBorder);
	    			}
		        }
	    		else {    			
	    			paint.setColor(ColorSelected);
	    			if (bRoundRect==false) {
	    				Rectangle dst = this.bounds;
	    				canvas.drawRect(dst.toRectF(), paint);    				
	    	        	canvas.drawRect(dst.toRectF(), paintOfBorder);
	    			}
	    			else {
	    				float rx = bounds.width * 0.1f;
	    		        float ry = rx;
	    		        Rectangle dst = this.bounds;
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