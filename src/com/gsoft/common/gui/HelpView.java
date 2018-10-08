package com.gsoft.common.gui;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Canvas;
import android.graphics.Color;

import com.gsoft.common.CommonGUI_SettingsDialog;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.EditRichText.TextLine;


public class HelpView extends Control {
	EditRichText editRichText;
	
	public HelpView() {
		int x = (int) (Control.view.getWidth()*0.1f);
		int y = (int) (Control.view.getHeight()*0.1f);
		int w = (int) (Control.view.getWidth()*0.8f);
		int h = (int) (Control.view.getHeight()*0.8f);
		
		Rectangle boundsOfEditRichText = new Rectangle(x,y,w,h);
		float fontSize = Control.view.getHeight()*0.07f;
		editRichText = new EditRichText(this, "EditText", boundsOfEditRichText, fontSize, false, 
				"", EditRichText.ScrollMode.Both);		
		editRichText.isReadOnly = true;
	}
	
	@Override
	public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
	   	boolean r;
	   	r = editRichText.onTouch(event, scaleFactor);
	   	if (!r) {
	   		// 외부영역을 터치하면 닫히도록 한다.
	   		this.setHides(true);
	   		return false;
	   	}
	   	return true;
	}
	
	public void setHelpFile(String pathHelpFile) {
		FileInputStream stream = null;
		BufferedInputStream bis = null;
		try {
			stream = new FileInputStream(pathHelpFile);
			bis = new BufferedInputStream(stream);
			editRichText.initialize();
			TextLine text = editRichText.read(bis, TextFormat.UTF_16);
			editRichText.setText(0, text);
			editRichText.setBackColor(CommonGUI_SettingsDialog.settings.selectedColor[0]);
		}catch(Exception e) {
			
		}
		finally {
			if (bis!=null)
				try {
					bis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (stream!=null)
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public void draw(Canvas canvas)
    {
		if (hides) return;
		editRichText.draw(canvas);
    }
}