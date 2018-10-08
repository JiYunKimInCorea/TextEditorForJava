package com.gsoft.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class CanvasEx  {
	
	
	public static void drawText(Canvas canvas, String text, float x, float y, PaintEx paint) {
		int i;
		int len = text.length();
		float xLocal=x, yLocal=y;
		
		Rect src = new Rect();
		RectF dst = new RectF();
		float textSize = paint.getTextSize();
		float top = textSize * 0.82f;
		int bitmapX, bitmapY, bitmapW=21, bitmapH=19, bitmapIndex;
		Bitmap bitmapChar = null;
		for (i=0; i<len; i++) {
			char c = text.charAt(i);
			char[] arr = {c};
			String strC = new String(arr);
			if ('가'<=c && c<='낗') {
				bitmapIndex = c-'가';
				bitmapX = (bitmapIndex % 16) * bitmapW;
				bitmapY = (bitmapIndex / 16) * bitmapH;
				bitmapChar = Bitmap.createBitmap(Font.HangulFont1, bitmapX, bitmapY, bitmapW, bitmapH);
				
				src.left = 0; src.top = 0;
				src.right = bitmapW; src.bottom = bitmapH;
				dst.left = xLocal; dst.top = yLocal - top;
				dst.right = dst.left + textSize;
				dst.bottom = dst.top + textSize;
				canvas.drawBitmap(bitmapChar, null, dst, paint);
				
				
				//yLocal += textSize;
			}
			else {				
				canvas.drawText(strC, xLocal, yLocal, paint);
			}
			xLocal += paint.measureText(strC);
		}
		
	}

}