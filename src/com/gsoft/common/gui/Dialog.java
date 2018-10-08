package com.gsoft.common.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Paint.Style;
import android.graphics.RectF;

import com.gsoft.common.ColorEx;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.Control.Container;
import com.gsoft.common.interfaces.OnTouchListener;

public abstract class Dialog extends Container   implements OnTouchListener
{
	
	public static class EditableControl extends Control {

		protected boolean WasKeyboardHiddenBeforeOpen;

		public EditableControl(Rectangle bounds) {			
			super();
			this.bounds = bounds;
			
		}
		
		public void changeBoundsOfKeyboard(Rectangle boundsOfEditText) {
			int viewHeight = view.getHeight();
			int viewWidth = view.getWidth();
			int heightOfGap = (int)(viewHeight * vertScaleOfGap);
			int top = boundsOfEditText.bottom()+heightOfGap;
			Rectangle boundsOfIntegrationKeyboard = new Rectangle(0, top,
					(int)(viewWidth*scaleOfKeyboardX), viewHeight-top);
			if (CommonGUI.keyboard!=null) {
				if (Control.requiresChangingBounds(CommonGUI.keyboard.bounds, boundsOfIntegrationKeyboard)) {
					CommonGUI.keyboard.changeBounds(boundsOfIntegrationKeyboard);
				}
			}
		}
		
	}

	public static class EditableDialog extends Dialog {

		
		
		protected boolean WasKeyboardHiddenBeforeOpen;

		public EditableDialog(Object owner, Rectangle bounds) {
			super(owner, bounds);
			// TODO Auto-generated constructor stub
		}
		
		public void ok() {
			OK(true);
			open(false);
			setKeyboard();
		}
		
		public void cancel() {
			OK(false);
	    	open( false);
	    	setKeyboard();
		}
		
		void setKeyboard() {
			CommonGUI.keyboard.setOnTouchListener(null);
			/*keyboard.restore();
	    	if (WasKeyboardHiddenBeforeOpen==false) { // open할 때 키보드가 보였다면 키보드를 전면에 보이게
	    		Control.keyboard.setHides(false);
	    	}
	    	else {	// open할 때 키보드가 보이지 않았다면 키보드를 숨긴다.
	    		Control.keyboard.setHides(true);
	    	}*/
		}
		
		public void changeBoundsOfKeyboard(Rectangle boundsOfEditText) {
			int viewHeight = view.getHeight();
			int viewWidth = view.getWidth();
			int heightOfGap = (int)(viewHeight * vertScaleOfGap);
			int top = boundsOfEditText.bottom()+heightOfGap;
			Rectangle boundsOfIntegrationKeyboard = new Rectangle(0, top,
					(int)(viewWidth*scaleOfKeyboardX), viewHeight-top);
			if (CommonGUI.keyboard!=null) {
				if (Control.requiresChangingBounds(CommonGUI.keyboard.bounds, boundsOfIntegrationKeyboard)) {
					CommonGUI.keyboard.changeBounds(boundsOfIntegrationKeyboard);
				}
			}
		}

		
		public void changeBounds(Rectangle paramBounds) {
			this.bounds = paramBounds;
		}
		
		
	}
		
	
	IntegrationKeyboard keyboard = CommonGUI.keyboard;
	
	protected Context context;
        
    
	//protected RectangleF bounds;
	
	//protected Size sizeButton;
	    
	protected float textSize;
	    
	protected int heightDialog;
	public boolean isTitleBarEnable = true;
	protected int heightTitleBar;
	int gap;
	    
	public String Text;  // 타이틀바의 제목
    protected Bitmap textureTitleBar;
   
    //protected int backColor = Color.CYAN;

    protected Control[] controls;
        
    protected boolean isOK;
    
    public boolean getIsOK() {
    	return isOK;
    }

    protected boolean hasFocus;

    protected boolean canDrop;
    protected boolean clicked;
    
    //protected int alpha = 255;
    
    protected int clickedButtonIndex;
    
    //Resources res;
    
    static Paint paint;
    

    public static Size ButtonSize = new Size(140, 50);

    public static int InitGap = 20;
    public static int InitHeightTitleBar = 50;
    public static int InitHeightDialog = InitHeightTitleBar + ButtonSize.height + 2 * InitGap;

    public int ColorTitleBar = Color.GREEN;
    public int ColorOfButton = Color.LTGRAY;
    public int ColorOfText = Color.BLACK;

    public static String NameButtonOk = "ButtonOk";
    public static String NameButtonCancel = "ButtonCancel";
    
    public static int InitTextSize = 24;
    
    public static boolean isDialogOpen = false;

    /**usesDrawingCache는 false이다.*/
    public Dialog(Object owner, Rectangle bounds) {
    	super();
    	this.owner = owner;
    	/*view=null;
		try {
			view = getView(owner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}*/
    	setBackColor(Color.CYAN);
    	context = view.getContext();
    	//res = context.getResources();
    	paint = new Paint();
        
        heightDialog = Dialog.InitHeightDialog;
        
        this.bounds = bounds;
        
        //Rectangle boundsRect = bounds.toRectangle();
        //setFlagAndCreateDrawingBuffer(view, false, boundsRect);
    }
    
    
   
  

    public void Focus(boolean b)
    {
        hasFocus = b;
    }

    public void OK(boolean isOK)
    {
        this.isOK = isOK;        
    }

    
    void LMouseClick(MotionEvent motionState, SizeF scaleFactor)
    {
        if (isOpen)
        {
            ProcessLMouseClick(motionState, scaleFactor);
        }
    }
    
    public void open(boolean isOpen) {
		super.open(isOpen);
		if (isOpen) {
			// 현재 오브젝트가 EditableDialog 이면 키보드 이벤트를 전달하고 
			// 그렇지 않으면 전달하지 않는다.
			if (this instanceof EditableDialog) {
				//CommonGUI.keyboard.setOnTouchListener(this);
			}
			else {
				//CommonGUI.keyboard.setOnTouchListener(null);
			}
		}
		else {
			//CommonGUI.keyboard.setOnTouchListener(null);
		}
	}
	
	public void setHides(boolean hides) {
		open(!hides);
	}
    
    public void open(OnTouchListener listener, boolean isOpen) {
    	if (isOpen) {
			setOnTouchListener(listener);
    	}
    	open(isOpen);
	}
    
    @Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	if  (isOpen==false || hides==true) {
    		return false;
    	} 
    	if (IsPointIn(new Point(event.x,event.y))==false) {
    		return false;
    	}    	
    	
    	return true;
    }
    
    
    void drawTitleBar(Canvas canvas)
    {
    	if (Text==null) return;
        Rectangle boundsTitleBar;
        boundsTitleBar = new Rectangle(bounds.x, bounds.y, bounds.width, heightTitleBar);
        paint.setStyle(Style.FILL);
        paint.setColor(ColorTitleBar);
        paint.setAlpha(alpha);
		//canvas.drawRect(boundsTitleBar.toRect(), paint);
        float rx = boundsTitleBar.width * 0.05f;
        float ry = boundsTitleBar.height * 0.15f;
        RectF rect = boundsTitleBar.toRectF();
        canvas.drawRoundRect(rect, rx, ry, paint);
        paint.setStyle(Style.STROKE);
        paint.setColor(ColorEx.darkerOrLighter(ColorTitleBar, -150));
        canvas.drawRoundRect(rect, rx, ry, paint);		
       
		paint.setTextSize(heightTitleBar*0.7f);
		float textWidth = paint.measureText(Text);		
		PointF locOfText = new PointF(boundsTitleBar.x+boundsTitleBar.width/2-textWidth/2, boundsTitleBar.y+boundsTitleBar.height-2);
		paint.setColor(textColor);
		canvas.drawText(Text, locOfText.x, locOfText.y, paint);
    }

    public void drawBackground(Canvas canvas) {
    	paint.setStyle(Style.FILL);
        paint.setColor(backColor);
        paint.setAlpha(alpha);
        if (isTitleBarEnable) {
	        RectangleF boundsExceptTitleBar = new RectangleF(bounds.x, bounds.y+heightTitleBar, 
	        		bounds.width, bounds.height-heightTitleBar);
	        float rx = boundsExceptTitleBar.width * 0.05f;
	        float ry = boundsExceptTitleBar.height * 0.05f;
	        RectF rect = boundsExceptTitleBar.toRectF();
	        canvas.drawRoundRect(rect, rx, ry, paint);
	        
	        paint.setStyle(Style.STROKE);
	        paint.setColor(ColorEx.darkerOrLighter(backColor,-150));
	        canvas.drawRoundRect(rect, rx, ry, paint);
	        //canvas.drawRect(boundsExceptTitleBar.toRect(), paint);
        }
        else {
        	float rx = bounds.width * 0.05f;
	        float ry = bounds.height * 0.05f;
	        canvas.drawRoundRect(bounds.toRectF(), rx, ry, paint);
        }
    	
    }
    
    public void draw(Canvas canvas)
    {
    	try{
    	int i;
    	if (hides) return;
        drawBackground(canvas);

        if (isTitleBarEnable) {
        	drawTitleBar(canvas);
        }
        
        if (controls==null) return;
        for (i = 0; i < controls.length; i++)
        {
        	controls[i].draw(canvas);
        }
    	}catch(Exception e) {
    		
    	}
    }
    
    public void cancel() {
    	if (CommonGUI.keyboard.getHides()==false) {
    		
    	}
    	else {
    		
    	}
    	
    }
    

    public void ProcessLMouseClick(MotionEvent motionState, SizeF scaleFactor)
    {
        int mouseX, mouseY;

        mouseX = motionState.x;
        mouseY = motionState.y;

        clickedButtonIndex = -1;
        
        if (controls==null) return;
        
        for (int i = 0; i < controls.length; i++)
        {
            if (controls[i].IsPointIn(new Point(mouseX, mouseY)))
            {
            	clickedButtonIndex = i;
                //CloseMenusChild();
                if (controls[i].name.equals(NameButtonOk))
                {
                    OK(true);
                    break;
                }
                else if (controls[i].name.equals(NameButtonCancel))
                {
                	OK(false);
                    break;
                }
            }
        }

        if (hasFocus && !IsPointIn(new Point(mouseX, mouseY)))
        {           
            /*if (Control.messageBox != null)
            {
                Control.messageBox.message = "확인이나 취소를 누르십시오";
            }*/
        }
    }

    public void ProcessLMouseDown(MotionEvent motionState, SizeF scaleFactor)
    {
        int mouseX, mouseY;

        mouseX = motionState.x;
        mouseY = motionState.y;


        for (int i = 0; i < controls.length; i++)
        {
            if (controls[i].IsPointIn(new Point(mouseX, mouseY)))
            {
                //buttons[i].SetButtonState(MyButtonState.MouseDown);
                break;
            }
        }
    }

    public void ProcessLMouseUp(MotionEvent motionState, SizeF scaleFactor)
    {
        for (int i = 0; i < controls.length; i++)
        {
            //buttons[i].SetButtonState(MyButtonState.Normal);
        }
    }

   
}