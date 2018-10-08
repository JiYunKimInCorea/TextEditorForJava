package com.gsoft.common;

import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.gui.Control;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.Bitmap;
import android.os.Build;


public class Font {
	enum FontSortHorz {
		Left, Middle, Right		
	}
	
	public enum FontSortVert {
		Top, Middle, Bottom		
	}
	
	public enum FontFamily {
		Default,
		SansSerif,
		Serif,
		Monospace
		
	}
	
	public static String toString(FontFamily family) {
		if (family==null) return null;
		if (family==FontFamily.Default) return "Default";
		else if (family==FontFamily.SansSerif) return "SansSerif";
		else if (family==FontFamily.Serif) return "Serif";
		else if (family==FontFamily.Monospace) return "Monospace";
		return null;
	}
	
	public static FontFamily fromString(String familyName) {
		if (familyName==null) return null;
		if (familyName.equals("Default")) return FontFamily.Default;
		else if (familyName.equals("SansSerif")) return FontFamily.SansSerif;
		else if (familyName.equals("Serif")) return FontFamily.Serif;
		else if (familyName.equals("Monospace")) return FontFamily.Monospace;
		return null;
	}
	
	enum Style {
		Normal,
		Bold,
		Italic,
		BoldItalic
	}
	
	static Bitmap HangulFont1;
	
	static Typeface default_normal;
	static Typeface sansSerif_normal;
	static Typeface serif_normal;
	
	static Typeface default_bold;
	static Typeface sansSerif_bold;
	static Typeface serif_bold;
	
	static Typeface default_italic;
	static Typeface sansSerif_italic;
	static Typeface serif_italic;
	
	static Typeface default_bold_italic;
	static Typeface sansSerif_bold_italic;
	static Typeface serif_bold_italic;
	
	public static Typeface getTypeface(FontFamily family, boolean isBold, boolean isItalic) {
		int version = Build.VERSION.SDK_INT;
		if (version<17) {
			if (family==FontFamily.Default && !isBold && !isItalic) {
				if (default_normal!=null) return default_normal;
				return Typeface.DEFAULT;
			}
			else if (family==FontFamily.SansSerif && !isBold && !isItalic) {
				if (sansSerif_normal!=null) return sansSerif_normal;
				return Typeface.SANS_SERIF;
			}
			else if (family==FontFamily.Serif && !isBold && !isItalic) {
				if (serif_normal!=null) return serif_normal;
				return Typeface.SERIF;
			}
			else if (family==FontFamily.Default && isBold && !isItalic) {
				if (default_bold!=null) return default_bold;
				default_bold = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
				return default_bold;
			}
			else if (family==FontFamily.SansSerif && isBold && !isItalic) {
				if (sansSerif_bold!=null) return sansSerif_bold;
				sansSerif_bold = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
				return sansSerif_bold;
			}
			else if (family==FontFamily.Serif && isBold && !isItalic) {
				if (serif_bold!=null) return serif_bold;
				serif_bold = Typeface.create(Typeface.SERIF, Typeface.BOLD);
				return serif_bold;
			}
			else if (family==FontFamily.Default && !isBold && isItalic) {
				if (default_italic!=null) return default_italic;
				default_italic = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC);
				return default_italic;
			}
			else if (family==FontFamily.SansSerif && !isBold && isItalic) {
				if (sansSerif_italic!=null) return sansSerif_italic;
				sansSerif_italic = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC);
				return sansSerif_italic;
			}
			else if (family==FontFamily.Serif && !isBold && isItalic) {
				if (serif_italic!=null) return serif_italic;
				serif_italic = Typeface.create(Typeface.SERIF, Typeface.ITALIC);
				return serif_italic;
			}
			else if (family==FontFamily.Default && isBold && isItalic) {
				if (default_bold_italic!=null) return default_bold_italic;
				default_bold_italic = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
				return default_bold_italic;
			}
			else if (family==FontFamily.SansSerif && isBold && isItalic) {
				if (sansSerif_bold_italic!=null) return sansSerif_bold_italic;
				sansSerif_bold_italic = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
				return sansSerif_bold_italic;
			}
			else if (family==FontFamily.Serif && isBold && isItalic) {
				if (serif_bold_italic!=null) return serif_bold_italic;
				serif_bold_italic = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC);
				return serif_bold_italic;
			}
			
			return Typeface.DEFAULT;
		}
		else {
			/*/if (!isBold)
				return Control.typeface;
			else 
				return Control.typefaceBold;*/
			return Control.typefaceDefault;
		}
	}
	
	
	public static void loadHangul(Context context) {
		//HangulFont1 = ContentManager.LoadBitmap(context, R.drawable.font_sansserif_bold_12_44032_45207);
		int version = Build.VERSION.SDK_INT;
		if (version>=17) {
			AssetManager assets = context.getAssets();
			Typeface typeface = Typeface.createFromAsset(assets, "fonts/DroidSans.ttf");
			Control.typefaceDefault = typeface;
			//default_normal = typeface;
		}
		else {
			Control.typefaceDefault = Typeface.DEFAULT;
			//default_normal = Typeface.DEFAULT;
		}
		
		/*typeface = Typeface.createFromAsset(assets, "fonts/DroidSans-Bold.ttf");
		Control.typefaceBold = typeface;*/
	}
	
	public static RectangleF getLocAndTextSize(PaintEx paint, Rectangle bounds, String text, 
			FontSortVert sortVert, float changeValueY) {
		float fontSize;    		
		float textWidth;
		float descent;
		@SuppressWarnings("unused")
		float leading;
		
		fontSize = bounds.height * 0.7f;
				
		do {
			fontSize -= fontSize * 0.15f;
			paint.setTextSize(fontSize);    		
			textWidth = paint.measureText(text);			
		}
		while (textWidth>=bounds.width);
		
		descent = fontSize * 0.15f;
		leading = fontSize * 0.25f;
		
		
		
		if (sortVert==FontSortVert.Top) {
			PointF locOfText = new PointF(bounds.x+bounds.width/2-textWidth/2, 
					bounds.y + fontSize - /*leading*/descent);
			return new RectangleF(locOfText.x, locOfText.y, fontSize, fontSize);			
		}
		else if (sortVert==FontSortVert.Middle) {
			PointF locOfText = new PointF(bounds.x+bounds.width/2-textWidth/2, 
					bounds.y + bounds.height - descent - (bounds.height-fontSize)/2);
			return new RectangleF(locOfText.x, locOfText.y, fontSize, fontSize);
		}
		else {
			PointF locOfText = new PointF(bounds.x+bounds.width/2-textWidth/2, 
					bounds.y + bounds.height - descent);
			return new RectangleF(locOfText.x, locOfText.y, fontSize, fontSize);
		}
		
	}
	
	public static RectangleF getLocAndTextSizeManual(PaintEx paint, Rectangle bounds, String text, 
			FontSortVert sortVert, float changeValueY) {
		float fontSize;    		
		float textWidth = 0;
		float descent;
		@SuppressWarnings("unused")
		float leading;
		
		fontSize = bounds.height * 0.45f;
		int textLenSeen;
		float charWidth;
		
		paint.setTextSize(fontSize);
		int i;
		for (i=0; i<text.length(); i++)
		{
			String str = text.substring(i,i+1);
			charWidth = paint.measureText(str);
			textWidth += charWidth;
			if (textWidth>bounds.width) {
				textWidth -= charWidth;
				break;
			}
		}
		textLenSeen = i;
		
		descent = fontSize * 0.15f;
		leading = fontSize * 0.25f;
		
		
		
		if (sortVert==FontSortVert.Top) {
			PointF locOfText = new PointF(bounds.x+bounds.width/2-textWidth/2, 
					bounds.y + fontSize - /*leading*/descent);
			return new RectangleF(locOfText.x, locOfText.y, fontSize, textLenSeen);			
		}
		else if (sortVert==FontSortVert.Middle) {
			PointF locOfText = new PointF(bounds.x+bounds.width/2-textWidth/2, 
					bounds.y + bounds.height - descent - (bounds.height-fontSize)/2);
			return new RectangleF(locOfText.x, locOfText.y, fontSize, textLenSeen);
		}
		else {
			PointF locOfText = new PointF(bounds.x+bounds.width/2-textWidth/2, 
					bounds.y + bounds.height - descent);
			return new RectangleF(locOfText.x, locOfText.y, fontSize, textLenSeen);
		}
		
	}
}