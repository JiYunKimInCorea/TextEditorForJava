package com.gsoft.common;

import java.io.File;
import java.io.InputStream;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;

import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.Control;
import com.gsoft.common.gui.EditText;
import com.gsoft.common.gui.IntegrationKeyboard;
import com.gsoft.common.gui.EditText.ScrollMode;
import com.gsoft.common.interfaces.OnTouchListener;

public class Terminal extends Control implements OnTouchListener {
	public EditText editText;
	
	Runtime runtime = Runtime.getRuntime();
	File workingDir = Environment.getRootDirectory();

	private int responseColor = ColorEx.darkerOrLighter(Color.GREEN, -100);

	private IntegrationKeyboard keyboard = CommonGUI.keyboard;
	
	
	public Terminal(Rectangle bounds, int backColor) {
		super();
		this.bounds = bounds;
		this.hides = true;
		super.setBackColor(backColor);
		CodeString text = new CodeString("-------------------------\nTerminal started.\n-------------------------\n$\n$", textColor);
		float fontSize = Control.view.getHeight()*0.035f;
		
		editText = new EditText(true, false, this, "terminal", bounds, 
				fontSize, false, text, ScrollMode.Both, backColor);
		editText.cursorPos.y = 4;
		editText.cursorPos.x = 1;
		editText.setOnTouchListener(this);
		
	}
	
	public void changeBounds(Rectangle paramBounds) {
		bounds = paramBounds;
		applySizingBorderOfView(bounds);
		editText.changeBounds(
			new Rectangle(editText.bounds.x, paramBounds.y, paramBounds.width-editText.bounds.x, paramBounds.height));
	}
	
	/**hides가 false이고 editText의 현재상태가 최대화가 아니면 키보드를 자동으로 보여준다.*/
	public synchronized void setHides(boolean hides) {		
		try {
			super.setHides(hides);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (hides==false) {
			editText.setIsOpen(!hides);
			keyboard .setOnTouchListener(editText);
			if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
				if (isMaximized()==false) {
					editText.changeBoundsOfKeyboardAndSizingBorder(editText.bounds);
					keyboard.setHides(false);
					//Control.isMaximized = false;
				}
				else {
					//Control.isMaximized = true;
				}
			}
		}
		else {
			editText.setIsOpen(!hides);
			if (Control.sizingBorder.getHides()==false)
				try {
					Control.sizingBorder.setHides(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
	
	public void setBackColor(int backColor) {
		super.setBackColor(backColor);
		responseColor = ColorEx.darkerOrLighter(Color.GREEN, -100);
	}
	
	
	/** bounds가 editText와 같고 editText자체에서 크기가 바뀌므로 
	 * super.onTouch를 호출하지 않는다. 
	 * 즉 editText의 bounds만 바뀌고 Terminal의 bounds는 바뀌지 않기 때문이다.*/
	public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r=false;
    	if (event.actionCode==MotionEvent.ActionDown) {
	    	//r = super.onTouch(event, scaleFactor);
	    	//if (!r) return false;
	    	r = editText.onTouch(event, scaleFactor);
	    	if (r) {
	    		this.bounds = editText.totalBounds;
	    		return true;
	    	}
	    	else return false;
    	}
    	else 
    		return false;
    }
	
	public void draw(Canvas canvas)
    {
		synchronized(this) {
		if (hides==false) {
	        super.draw(canvas);
	        editText.draw(canvas);
		}
		}
    }
	
	public void onTouchEvent(Object sender, MotionEvent e) {
		if (sender instanceof EditText) {
			int i;
			EditText editText = (EditText)sender;
			if (e!=null) return;
			if (CommonGUI.keyboard.key!=null && CommonGUI.keyboard.key.equals(IntegrationKeyboard.Enter)) {
				// Enter는 아직 들어가지 않는다.
				String line = editText.textArray[editText.cursorPos.y].toString();
				if (line.charAt(0)=='$') {
					line = line.substring(1, line.length());
				}
				else {
					line = line.substring(0, line.length());
				}
				//Compiler.Preprocessor.ConvertToStringArray(new CodeString(line));
				//ArrayListCodeString mBuffer = Compiler.Preprocessor.mBuffer;
				
				String msg;
				try {
					Process process = runtime.exec(line, null, workingDir);
					InputStream is = process.getInputStream();
					msg = IO.readString(is, TextFormat.UTF_8);
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					msg = "error\n";
				}
				editText.textArray[editText.cursorPos.y].concate(new CodeString("\n", editText.textColor));
				
				CodeString codeMsg = new CodeString(msg, editText.textColor);
				codeMsg.setColor(responseColor);
				editText.setText(editText.cursorPos.y+1, codeMsg);
				editText.cursorPos.x = 0;
				editText.cursorPos.y = editText.numOfLines-1;
				editText.setText(editText.cursorPos.y, new CodeString("$", editText.textColor));
				editText.cursorPos.x = 1;
				editText.cursorPos.y = editText.numOfLines-1;
				
				
			}
		}
	}
}