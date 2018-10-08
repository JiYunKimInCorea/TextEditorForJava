package com.gsoft.common;

import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ContentManager {
	/*static public Bitmap LoadBitmap(Context context, String filename) {
		try {
			File path = context.getFilesDir();
			return BitmapFactory.decodeFile(path.getAbsolutePath()+filename);
		}catch(Exception e) {
			Log.e("LoadBitmap", e.toString());
			return null;
		}
		
	}*/
	
	static public com.gsoft.common.Bitmap LoadBitmap(AssetManager asset, InputStream is) {
		try {
			return new com.gsoft.common.Bitmap(is);
		}catch(Exception e) {
			Log.e("LoadBitmap", e.toString());
			return null;
		}
	}
	
	static public Bitmap LoadBitmap(Context context, InputStream iStream) throws Exception {
		Bitmap r = null;
		try {
			//File path = context.getFilesDir();
			r = BitmapFactory.decodeStream(iStream);
			iStream.close();
		}catch (OutOfMemoryError e) {
			iStream.close();
			throw e;
		}
		catch(Exception e) {
			iStream.close();
			throw e;
		}
		return r;
		
	}
	
	static public Bitmap LoadBitmap(Context context, int id) {
		Resources resources = context.getResources();
		
		//BitmapFactory.Options.
		Bitmap bitmap = null;
		try {
			//Drawable drawable = resources.getDrawable(id);
			bitmap = BitmapFactory.decodeResource(resources, id);
		}catch(Exception e) {
		}
		return bitmap;
	}
	
	static public Drawable getDrawable(Context context, int id) {
		Resources resources = context.getResources();
		return resources.getDrawable(id);
		
	}
	
	static void changeColor(Bitmap bitmap, int srcColor, int dstColor) {
		if (bitmap.isMutable()==false) {
			return;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int i, j;
		for (j=0; j<height; j++) {
			for (i=0; i<width; i++) {
				if (bitmap.getPixel(i, j)==srcColor) {
					bitmap.setPixel(i, j, dstColor);
				}
			}
		}
		
	}
}