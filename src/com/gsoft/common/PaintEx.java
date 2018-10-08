package com.gsoft.common;

import com.gsoft.common.gui.EditRichText.TextLine;

import android.graphics.Paint;
import android.graphics.Typeface;

public class PaintEx extends Paint {
	public PaintEx() {
		super();
	}
	public PaintEx(Paint paint) {
		super();
		setColor(paint.getColor());
		setStyle(paint.getStyle());
		setTextSize(paint.getTextSize());
	}
	
	
	
	
	@Override
	public float measureText(String text) {
		
		/*float spaceWidth = this.getTextSize() * 0.6f;
		float tabWidth = spaceWidth * 3;
		
		int i;
		char c;
		float r = super.measureText(text);
		
		for (i=0; i<text.length(); i++) {
			c = text.charAt(i);
			//if (c=='\n' || c=='\r') continue;
			if (c=='\t') {
				r -= super.measureText("\t");
				r += tabWidth;
			}
			else if (c==' ') {
				r -= super.measureText(" ");
				r += spaceWidth;
			}
			else if (c=='\r') {
				r -= super.measureText("\r");
			}
			else if (c=='\n') {
				r -= super.measureText("\n");
			}
		}
		return r;*/
		
		float spaceWidth = this.getTextSize() * 0.5f;
		float tabWidth = spaceWidth * 3;
		
		int i;
		char c;
		float r = 0;
		
		if (text==null) return 0.0f;
		
		for (i=0; i<text.length(); i++) {
			c = text.charAt(i);
			//if (c=='\n' || c=='\r') continue;
			if (c=='\t') {
				r += tabWidth;
			}
			else if (c==' ') {
				r += spaceWidth;
			}
			else if (c=='\r') {
				continue;
			}
			else if (c=='\n') {
				continue;
			}
			else {
				char[] arg = {c};
				String cstr = new String(arg);
				r += super.measureText(cstr);
			}
		}
		return r;
		
		
	}
	
	public float measureText(TextLine text) {
		float oldTextSize = getTextSize();
		Typeface oldTypeface = getTypeface();
		int i;
		float r = 0;
		for (i=0; i<text.count; i++) {
			if (text.characters[i].bitmap==null) {
				setTextSize(text.characters[i].size);
				setTypeface(text.characters[i].typeface);
				/*if (text.characters[i].charA!=0 && text.characters[i].charA!='\n') {
					char[] arg = {text.characters[i].charA};
					r += super.measureText(new String(arg));
				}*/
				char[] arg = {text.characters[i].charA};
				r += measureText(new String(arg));
			}
			else {
				r += text.characters[i].bitmap.getWidth();
			}
		}
		this.setTextSize(oldTextSize);
		this.setTypeface(oldTypeface);
		return r;
	}
	
	
}