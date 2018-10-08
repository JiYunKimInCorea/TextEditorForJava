package com.gsoft.common.gui;

import android.graphics.Canvas;

import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.interfaces.OnTouchListener;

public class MenuWithClosable extends Menu {
	public MenuWithClosable(String name, Rectangle srcBounds, MenuType menuType, Object owner, 
    		String[] namesButtons, Size cellSpacing, boolean selectable,
    		OnTouchListener listener)
    {   
    	// owner : clientview, callee : clientView
    	super(name, srcBounds, menuType, owner, namesButtons, 
    			cellSpacing, selectable, listener);
    	countMenus = 0;
        menus = new Menu[countMenus];
        //textes = new String[namesButtons.length];
        
        //res = view.getResources();
        /*textes[0] = res.getString(R.string.menu_setting);
    	textes[1] = res.getString(R.string.menu_newstart);
    	textes[2] = res.getString(R.string.menu_pausegame);
		textes[3] = res.getString(R.string.menu_exitgame);*/
		
        InitializeAndLayout();
    }

    // 부속된 메뉴를 추가한다.
    public void InitializeAndLayout()
    {
        super.InitializeAndLayout();
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
    
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	if  (getIsOpen()==false) {
    		return false;
    	} 
    	boolean r = super.onTouch(event, scaleFactor);
    	
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    	// 영역이 아닌 곳에 터치하면 닫힌다.
	    	if (this.countMenus==0) {
		    	if (r==false) {
		    		open(false);	// 자기 것만 닫힌다.
		    		//Control.isAllDrawn = true;
		    		return true;
		    	}
		    	else return true;
	    	}
	    	else {
	    		// 부속된 메뉴 포함 영역 계산
	    		if (r==false) {
	    			this.Close();	// 부속된 메뉴 포함 닫힌다.
	    			//Control.isAllDrawn = true;
		    		return true;
		    	}
	    		else return true;
	    	}
    	}
    	
    	
    	return false;
    }
}