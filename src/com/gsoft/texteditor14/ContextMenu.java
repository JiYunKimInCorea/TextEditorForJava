package com.gsoft.texteditor14;

import android.graphics.Canvas;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.Control;
import com.gsoft.common.gui.Menu;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.texteditor14.R;

public class ContextMenu extends Menu
{
	public static String[] textsOfButtons = {
		/*"New Document", "Load", "Save", "Sizable", "Maximize/PrevSize", "EditRichText/EditText",
		"Open file explorer", "Show arrow keys", 
		"About program and programer", "Close"*/
		Control.res.getString(R.string.context_menu_0), Control.res.getString(R.string.context_menu_1),
		Control.res.getString(R.string.context_menu_2), Control.res.getString(R.string.context_menu_3),
		Control.res.getString(R.string.context_menu_4), "EditRichText/EditText/Terminal", 
		Control.res.getString(R.string.context_menu_5),
		Control.res.getString(R.string.context_menu_6), Control.res.getString(R.string.context_menu_7),
		"Current Time", "Terminal", "Settings",
		Control.res.getString(R.string.context_menu_8)
		
	};
	
	//public static Byte[] indicesOfButtonsInGroup = {0,1,2,4,5,6,7,8,9};
	
	//public static int indexOfSelectedButton=-1;
		
    public ContextMenu(String name, Rectangle srcBounds, MenuType menuType, Object owner, 
    		String[] namesButtons, Size cellSpacing, boolean selectable,
    		OnTouchListener listener)
    { 	
        
    	// owner : clientview, callee : clientView
    	super(name, srcBounds, menuType, owner, namesButtons, 
    			cellSpacing, selectable, listener);
    	countMenus = 0;
        menus = new Menu[countMenus];
        //textes = new String[namesButtons.length];
        
        //res = ((CustomView)owner).getResources();
        /*textes[0] = res.getString(R.string.menu_setting);
    	textes[1] = res.getString(R.string.menu_newstart);
    	textes[2] = res.getString(R.string.menu_pausegame);
		textes[3] = res.getString(R.string.menu_exitgame);*/
		
        InitializeAndLayout();
        
        
		
		
    }

    // 0õȠ0 ß0���
    public void InitializeAndLayout()
    {
        super.InitializeAndLayout();
    }
    
    /*public void scale(SizeF scaleFactor) {
    	super.scale(scaleFactor);
    	
    }*/
    
    @Override
    public void draw(Canvas canvas) {
		
		super.draw(canvas);
    	
    	
    }
    
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	if  (getIsOpen()==false) {
    		return false;
    	} 
    	
    	boolean r= super.onTouch(event, scaleFactor);
    	if (!r) {
    		this.Close();
    		return true;
    	}
    	else {
    		return true;
    	}
    	
    	/*if (event.actionCode==MotionEvent.ActionDown) {
    		ProcessLMouseClick(event, scaleFactor);
    	}
    	else if (event.actionCode==MotionEvent.ActionDoubleClicked) {
    		
    	}
    	else if (event.actionCode==MotionEvent.ActionUp) {
    		
    	}
    	return true;*/
    }

    public void ProcessLMouseClick(MotionEvent motionState, SizeF scaleFactor)
    {
        super.ProcessLMouseClick(motionState, scaleFactor);       
    }
}