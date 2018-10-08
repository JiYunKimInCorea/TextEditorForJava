package com.gsoft.common.gui;

import android.graphics.Canvas;

import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.interfaces.OnTouchListener;

public class MenuWithAlwaysOpen extends Menu {
	boolean isDockingOfToolbarFlexiable;
	
	
	
    public MenuWithAlwaysOpen(String name, Rectangle srcBounds, MenuType menuType, Object owner, 
    		String[] namesButtons, Size cellSpacing, boolean selectable,
    		OnTouchListener listener, boolean isDockingOfToolbarFlexiable)
    {   
    	// owner : clientview, callee : clientView
    	super(name, srcBounds, menuType, owner, namesButtons, 
    			cellSpacing, selectable, listener);
    	this.isDockingOfToolbarFlexiable = isDockingOfToolbarFlexiable;
    	countMenus = 0;
        menus = new Menu[countMenus];
        //textes = new String[namesButtons.length];
        
        //res = view.getResources();
        
        /*textes[0] = res.getString(R.string.menu_setting);
    	textes[1] = res.getString(R.string.menu_newstart);
    	textes[2] = res.getString(R.string.menu_pausegame);
		textes[3] = res.getString(R.string.menu_exitgame);*/
		
        InitializeAndLayout();
        
        //usesDrawingCache = false;
    }

    // 0õȠ0 ß0���
    public void InitializeAndLayout()
    {
        super.InitializeAndLayout();
    }
    
    public void open(boolean isOpen, boolean usesDrawingCache)
    {
        //setIsOpen(isOpen);
    	if (usesDrawingCache) {
    		super.open(isOpen);
    	}
    	else {
    		setIsOpen(isOpen);
    	}
    }
    
    /*public void scale(SizeF scaleFactor) {
    	super.scale(scaleFactor);
    }*/
    
    @Override
    public void draw(Canvas canvas) {
    	if (hides) return;
    	synchronized(this) {
    	try{
		super.draw(canvas);
    	}catch(Exception e) {
    		
    	}
    	}
    }
    
    @Override
    public void changeBounds(Rectangle bounds) {
    	if (menuType == MenuType.Vertical) {
    		if (this.isDockingOfToolbarFlexiable==false) {
	    		int i;
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
    			super.changeBounds(bounds);
    		}
        }
    	else {
    		
    	}
    }
    
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	//if  (isOpen==false) {
    	//	return false;
    	//}
    	
    	boolean r = super.onTouch(event, scaleFactor);
    	
    	/*if (this.countMenus==0) {
	    	if (IsPointIn(new Point(event.x,event.y), false)==false) {
	    		//isOpen = false;
	    		return false;
	    	}
    	}
    	else {
    		if (IsPointIn(new Point(event.x,event.y), true)==false) {
	    		//isOpen = false;
	    		return false;
	    	}
    	}*/
    	
    	
    	
    	/*if (event.actionCode==MotionEvent.ActionDown) {
    		ProcessLMouseClick(event, scaleFactor);
    	}
    	else if (event.actionCode==MotionEvent.ActionDoubleClicked) {
    		
    	}
    	else if (event.actionCode==MotionEvent.ActionUp) {
    		
    	}*/
    	return r;
    }
}