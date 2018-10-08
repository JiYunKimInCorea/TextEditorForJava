package com.gsoft.common.gui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Buttons.ButtonGroup;
import com.gsoft.common.interfaces.OnTouchListener;

public abstract class Menu extends Control
{
	public static enum MenuType
	{
	    Vertical,
	    Horizontal
	}

	//public String name;
	
	//RectangleF srcBounds;
	//RectangleF bounds;
	
    //protected Object owner;
    //protected Object callee;

    public MenuType menuType;

    public Button[] buttons;
    public int countButtons;

    protected String[] namesButtons;
    protected String[] textes;
    
    protected Menu[] menus;
    protected int countMenus;

    //private boolean isOpen;

    // gap(버튼사이의 간격)에서 버튼을 띄우는 공간(여백)
    protected int cellspaceHorz;
    protected int cellspaceVert;

    public boolean selectable;
    
    public static int ButtonBackColor = Color.GREEN;
    
    Paint fillPaint;
    
    //int alpha = 255;
    
    protected int clickedButtonIndex;
    
    //protected Resources res;
    
    //public OnTouchListener listener;
    
    //int[] oldPixels;
    
    int incxForBitmapRendering;
	int incyForBitmapRendering;
    
    
   
    

    public static Size ButtonSize = new Size(150, 50);

    protected Menu()
    {
    }

    public Menu(String name, Rectangle srcBounds, MenuType menuType, 
    		Object owner, String[] namesButtons, 
    		Size cellSpacing, boolean selectable, OnTouchListener listener)
    {
    	super();
    	//usesDrawingCache = true;
        this.name = name;
        //this.srcBounds = srcBounds;
        this.bounds = new Rectangle(srcBounds.x, srcBounds.y, srcBounds.width, srcBounds.height);
        this.menuType = menuType;
        this.owner = owner;
        this.countButtons = namesButtons.length;
        this.namesButtons = namesButtons;
        this.textes = namesButtons;
        this.cellspaceHorz = cellSpacing.width;
        this.cellspaceVert = cellSpacing.height;
        this.selectable = selectable;
        fillPaint = new Paint();
        fillPaint.setStyle(Style.FILL);
       

        //InitializeAndLayout();
        this.listener = listener;
    }

    public void InitializeAndLayout()
    {
        if (menuType == MenuType.Vertical)
        {
        	int height = bounds.height;
        	int gap = height / countButtons;     // 버튼사이의 간격
        	int heightButton = gap - 2 * cellspaceVert;
        	int widthButton = bounds.width - 2 * cellspaceHorz;
        	int offsetX = bounds.x + cellspaceHorz;
        	int offsetY = bounds.y;

            int i;
            buttons = new Button[countButtons];
        
            // 버튼 텍스처 로드와 버튼 생성
            for (i = 0; i < countButtons; i++)
            {              
                Rectangle srcBoundsButton = new Rectangle(offsetX, offsetY + i * gap + cellspaceVert, widthButton, heightButton);
                buttons[i] = new Button(this, namesButtons[i], textes[i], ButtonBackColor, srcBoundsButton, 
                		selectable, 255, false, 0.0f, null, Color.LTGRAY);
                buttons[i].setOnTouchListener(listener);
            }
        }
        else
        {
        	int width = bounds.width;
        	int gap = width / countButtons;     // 버튼사이의 간격
        	int widthButton = gap - 2 * cellspaceHorz;
        	int heightButton = bounds.height - 2 * cellspaceVert;
        	int offsetX = bounds.x;
        	int offsetY = bounds.y + cellspaceVert;

            int i;
            buttons = new Button[countButtons];
           
            // 버튼 텍스처 로드와 버튼 생성
            for (i = 0; i < countButtons; i++)
            {
                Rectangle srcBoundsButton = new Rectangle(offsetX + i * gap + cellspaceHorz, offsetY, widthButton, heightButton);
                buttons[i] = new Button(this, namesButtons[i], textes[i], ButtonBackColor, srcBoundsButton, selectable, 
                		255, false, 0.0f, null, Color.LTGRAY);
                buttons[i].setOnTouchListener(listener);
            }
        }
        
        ButtonGroup group = new ButtonGroup(null, buttons);
		int i;
		for (i=0; i<buttons.length; i++) {
			buttons[i].setGroup(group, i);
		}
    }
    
    public void selectAll(boolean isSelected){
    	int i;
    	for (i=0; i<this.buttons.length; i++) {
    		this.buttons[i].Select(isSelected);
    	}
    }
    
    public Button findByName(String name) {
    	int i;
    	name = name.toLowerCase();
    	for (i=0; i<this.buttons.length; i++) {
    		String buttonName = this.buttons[i].name;
    		buttonName = buttonName.toLowerCase();
    		if (buttonName.contains(name)) return buttons[i];
    	}
    	return null;
    }
    
    public void changeBounds(Rectangle bounds) {
    	/*if (menuType == MenuType.Vertical) {
    		int i;
    		int h=0;
    		for (i=0; i<countButtons; i++) {
    			if (buttons[i].bounds.bottom()>bounds.bottom()) {
    				break;
    			}
    			else {
    				buttons[i].hides = false;
    			}
    		}
    		int j;
    		for (j=i; j<countButtons; j++) {
    			buttons[j].hides = true;
    		}
        }
    	else {
    		
    	}*/
    	this.bounds = bounds;
    	
    	if (menuType == MenuType.Vertical)
        {
    		int height = bounds.height;
    		int gap = height / countButtons;     // 버튼사이의 간격
    		int heightButton = gap - 2 * cellspaceVert;
    		int widthButton = bounds.width - 2 * cellspaceHorz;
    		int offsetX = bounds.x + cellspaceHorz;
    		int offsetY = bounds.y;

            int i;
        
            // 버튼 텍스처 로드와 버튼 생성
            for (i = 0; i < countButtons; i++)
            {              
                Rectangle srcBoundsButton = new Rectangle(offsetX, offsetY + i * gap + cellspaceVert, widthButton, heightButton);
                buttons[i].changeBounds(srcBoundsButton);
            }
        }
        else
        {
        	int width = bounds.width;
        	int gap = width / countButtons;     // 버튼사이의 간격
        	int widthButton = gap - 2 * cellspaceHorz;
        	int heightButton = bounds.height - 2 * cellspaceVert;
        	int offsetX = bounds.x;
        	int offsetY = bounds.y + cellspaceVert;

            int i;
            buttons = new Button[countButtons];
           
            // 버튼 텍스처 로드와 버튼 생성
            for (i = 0; i < countButtons; i++)
            {
                Rectangle srcBoundsButton = new Rectangle(offsetX + i * gap + cellspaceHorz, offsetY, widthButton, heightButton);
                buttons[i].changeBounds(srcBoundsButton);
            }
        }
    }
    
    /*public void scale(SizeF scaleFactor) {
    	int i;
    	this.bounds = new RectangleF((srcBounds.x*scaleFactor.width), (srcBounds.y*scaleFactor.height), 
    			(srcBounds.width*scaleFactor.width), (srcBounds.height*scaleFactor.height));
    	for (i = 0; i < countButtons; i++)
        {
    		buttons[i].scale(scaleFactor);        
        }
    	for (i = 0; i < countMenus; i++)
        {
    		this.menus[i].scale(scaleFactor);
        }    	
    }*/


    public void drawBackground(Canvas canvas, int color) {
    	fillPaint.setColor(color);
    	fillPaint.setAlpha(alpha);
    	//RectF dst = RectangleF.toRectF(bounds, incxForBitmapRendering, incyForBitmapRendering);
    	//canvas.drawRect(dst, fillPaint);
    	canvas.drawRect(bounds.toRectF(), fillPaint);
    }
    
    
    
    public void draw(Canvas canvas)
    {
    	try{
    	if (hides) return;
    	
        drawBackground(canvas, Color.LTGRAY);

        int i;
        // 실행시에 메뉴버튼의 텍스트가 바뀌는 효과를 낸다.
        for (i = 0; i < countButtons; i++)
        {
        	//buttons[i].incxForBitmapRendering = incxForBitmapRendering;
        	//buttons[i].incyForBitmapRendering = incyForBitmapRendering;
        	buttons[i].text = textes[i];
            buttons[i].draw(canvas);
        }

        // 부속된 메뉴들
        for (i=0; i<countMenus; i++) 
        {
            if (menus[i] != null && menus[i].isOpen)
            {
                menus[i].draw(canvas);
            }
        }
    	}catch(Exception e) {
    		
    	}
    }
    
    public boolean IsPointIn(Point point, boolean includesChildren)
    {
        if (bounds.x <= point.x && point.x <= bounds.x + bounds.width - 1 &&
            bounds.y <= point.y && point.y <= bounds.y + bounds.height - 1) {
            return true;
        }
        else {
        	if (!includesChildren) return false;
        	else {
	        	int i;
	        	for (i=0; i<countMenus; i++) 
	            {	        		
	                if (menus[i] != null && menus[i].isOpen)
	                {
	                	if (menus[i].IsPointIn(point, includesChildren)) {
	                		return true;
	                	}
	                }
	            }
	        	return false;
        	}
        	
        }
        //return r;
    }

    // 부속된 메뉴를 포함해서 모두 닫는다.
    public void Close()
    {
        open(false);
        // 	부속된 메뉴들
        for (int i=0; i<countMenus; i++) 
        {
            if (menus[i] != null && menus[i].isOpen)
            {
                menus[i].Close();
            }
        }
    }

    public void InitializeButtons()
    {
        for (int i = 0; i < countButtons; i++)
        {
            buttons[i].SetButtonState(Button.ButtonState.Normal);
        }
    }

    public void DeselectButtons()
    {
        for (int i = 0; i < countButtons; i++)
        {
            buttons[i].Select(false);
        }
    }

    // 부속된 메뉴의 버튼도 포함해서 버튼을 선택한다.
    public void SelectButton(String nameButton)
    {
        for (int i = 0; i < countButtons; i++)
        {
            if (buttons[i].name.equals(nameButton))
            {
                buttons[i].Select(true);
                return;
            }
        }

        for (int i = 0; i < countMenus; i++)
        {
            menus[i].SelectButton(nameButton);
        }
    }
    
    @Override
    public void setOnTouchListener(OnTouchListener listener) {
		this.listener = listener;
		for (int i = 0; i < countButtons; i++)
        {
			buttons[i].setOnTouchListener(listener);
        }
    }
    
    public void open(OnTouchListener listener, boolean isOpen) {
    	if (isOpen) {
			setOnTouchListener(listener);
    	}
    	super.open(isOpen);
	}
    
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r = super.onTouch(event, scaleFactor);
    	if (!r) return false;
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
    		if (this.IsPointIn(new Point(event.x, event.y))) {
    			this.selectAll(false);
    		}
	    	for (int i = 0; i < countButtons; i++)
	        {
	    		r = buttons[i].onTouch(event, scaleFactor);
	    		if (r) return true;
	        }
    	}
    	return false;
    }
    
    public int findIndex(String buttonName) {
    	int i;
    	for (i=0; i<countButtons; i++) {
    		if (buttons[i].name.equals(buttonName))
    			return i;
    	}
    	return -1;
    }

    public void ProcessLMouseUp(MotionEvent motionState, SizeF scaleFactor)
    {
        InitializeButtons();
        for (int i = 0; i < countMenus; i++)
        {
            if (menus[i] != null && menus[i].isOpen)
            {
                menus[i].ProcessLMouseUp(motionState, scaleFactor);
            }
        }
    }

    public void ProcessLMouseDown(MotionEvent motionState, SizeF scaleFactor)
    {
        int mouseX, mouseY;

        mouseX = motionState.x;
        mouseY = motionState.y;

        
        for (int i = 0; i < countButtons; i++)
        {
            if (buttons[i].IsPointIn(new Point(mouseX, mouseY)))
            {                    
                buttons[i].SetButtonState(Button.ButtonState.MouseDown);
                break;
            }
        }
        for (int i = 0; i < countMenus; i++)
        {
            if (menus[i] != null && menus[i].isOpen)
            {
                menus[i].ProcessLMouseDown(motionState, scaleFactor);
            }
        }
    }

    public void ProcessLMouseClick(MotionEvent motionState, SizeF scaleFactor)
    {
    	//if (this.isOpen==false) return;
    	int mouseX, mouseY;

        mouseX = motionState.x;
        mouseY = motionState.y;
        
        clickedButtonIndex = -1;
        
        for (int i = 0; i < countButtons; i++)
        {
            if (buttons[i].IsPointIn(new Point(mouseX, mouseY)))
            { 
                DeselectButtons();
                buttons[i].Select(true);
                clickedButtonIndex = i;
                break;
            }
        }
        for (int i = 0; i < countMenus; i++)
        {
            if (menus[i] != null && menus[i].isOpen)
            {
                menus[i].ProcessLMouseClick(motionState, scaleFactor);
            }
        }
    }
    
    protected void CloseMenu(Menu menu)
    {
        
    }

    // 재귀적 호출, 자식 메뉴들을 모두 닫는다.
    public void CloseMenusChild()
    {
        int i;
        for (i = 0; i < countMenus; i++)
        {
            if (menus[i] != null)
            {
                menus[i].CloseMenusChild();
                menus[i].Close();
            }
        }

    }

    public void InitializeAll()
    {
        int i;
        for (i = 0; i < buttons.length; i++)
        {
            if (buttons[i] != null)
            {
                buttons[i].Select(false);
            }
        }
        for (i = 0; i < countMenus; i++)
        {
            if (menus[i] != null)
            {
                menus[i].InitializeAll();
            }
        }
    }
}