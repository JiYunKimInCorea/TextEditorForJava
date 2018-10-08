package com.gsoft.common;

import android.graphics.Rect;
import android.graphics.RectF;

public class Sizing {
	public static class Rectangle {
		public int x;
		public int y;
		public int width;
		public int height;
		
		public Rectangle() {
		}
		
		public Rectangle(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public Rectangle(Rectangle bounds) {
			// TODO Auto-generated constructor stub
			x = bounds.x;
			y = bounds.y;
			width = bounds.width;
			height = bounds.height;
		}
		
		public void copy(Rectangle bounds) {
			x = bounds.x;
			y = bounds.y;
			width = bounds.width;
			height = bounds.height;
		}
		
		public int bottom() {
			return y+height-1;
		}
		public int right() {
			return x+width-1; 
		}
		public Rectangle toRectangle() {
			return new Rectangle(x, y, width, height);		
		}
		public Rect toRect() {
			Rect rect = new Rect();
			rect.left = x;
			rect.top = y;
			rect.right = x+width;
			rect.bottom = y+height;
			return rect;		
		}
		
		public RectF toRectF() {
			RectF rect = new RectF();
			rect.left = x;
			rect.top = y;
			rect.right = x+width+1;
			rect.bottom = y+height+1;
			return rect;		
		}
		
		public static RectF toRectF(Rect r, int incx, int incy) {
			RectF rect = new RectF();
			rect.left = r.left - incx;
			rect.top = r.top - incy;
			rect.right = r.right - incx;
			rect.bottom = r.bottom - incy;		
			return rect;
		}
		
		public static Rect toRect(Rect r, int incx, int incy) {
			Rect rect = new Rect();
			rect.left = r.left - incx;
			rect.top = r.top - incy;
			rect.right = r.right - incx;
			rect.bottom = r.bottom - incy;		
			return rect;
		}
		
		// top = bottom-height+1
		public static int toTop(int bottom, int height) {
			return bottom-height+1;
		}
		// left = right-width+1
		public static int toLeft(int right, int width) {
			return right-width+1;
		}
		
		public boolean equals(Rectangle rect) {
			if (this.x!=rect.x) return false;
			if (this.y!=rect.y) return false;
			if (this.width!=rect.width) return false;
			if (this.height!=rect.height) return false;
			return true;
		}
	}
	
	/*public static class XRectF extends RectF {
		float width;
		float height;
		public XRectF(float x, float y, float right, float bottom) {
			left = x;
			top = y;
			this.right = right;
			this.bottom = bottom;
			width = right - left;
			height = bottom - top;
		}
	}*/
	
	public static class RectangleF {
		public float x;
		public float y;
		public float width;
		public float height;
		
		//public RectF rectF;
		
		public RectangleF() {
		}
		
		public RectangleF(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public RectangleF(RectangleF bounds) {
			// TODO Auto-generated constructor stub
			x = bounds.x;
			y = bounds.y;
			width = bounds.width;
			height = bounds.height;
		}
		
		public void copy(RectangleF bounds) {
			x = bounds.x;
			y = bounds.y;
			width = bounds.width;
			height = bounds.height;
		}
		
		
		public float bottom() {
			return y+height;
		}
		public float right() {
			return x+width; 
		}
		public RectF toRectF() {
			RectF rectF = new RectF();
			rectF.left = x;
			rectF.top = y;
			rectF.right = x+width;
			rectF.bottom = y+height;		
			return rectF;
		}
		/** 비트맵 렌더링을 하기 위하여 원래 RectangleF r에 incx, incy를 빼서 새로운 RectF를 얻는다.
		 * 예를들어 EditText의 bitmapForRendering은 원점부터 시작하기 때문에 스크롤바, 툴바 등은 
		 * draw() 할때에 EditText의 totalBounds.x와 totalBounds.y 값을 갖는 
		 * incxForBitampRendering, incyForBitampRendering 두 속성을 이용하여 
		 * EditText의 bitmapForRendering에 알맞게 드로잉을 하게 된다.*/
		public static RectF toRectF(RectangleF r, float incx, float incy) {
			RectF rectF = new RectF();
			rectF.left = r.x - incx;
			rectF.top = r.y - incy;
			rectF.right = r.x + r.width - incx;
			rectF.bottom = r.y + r.height - incy;		
			return rectF;
		}
		
		/** 비트맵 렌더링을 하기 위하여 원래 RectangleF r에 incx, incy를 빼서 새로운 RectF를 얻는다.
		 * 예를들어 EditText의 bitmapForRendering은 원점부터 시작하기 때문에 스크롤바, 툴바 등은 
		 * draw() 할때에 EditText의 totalBounds.x와 totalBounds.y 값을 갖는 
		 * incxForBitampRendering, incyForBitampRendering 두 속성을 이용하여 
		 * EditText의 bitmapForRendering에 알맞게 드로잉을 하게 된다.*/
		public static RectF toRectF(Rectangle r, int incx, int incy) {
			RectF rectF = new RectF();
			rectF.left = r.x - incx;
			rectF.top = r.y - incy;
			rectF.right = r.x + r.width - incx -1;
			rectF.bottom = r.y + r.height - incy -1;		
			return rectF;
		}
		
		public Rectangle toRectangle() {
			return new Rectangle((int)x, (int)y, (int)width, (int)height);		
		}
		
		// top = bottom-height
		public static float toTop(float bottom, float height) {
			return bottom-height;
		}
		// left = right-width
		public static float toLeft(float right, float width) {
			return right-width;
		}

		public Rect toRect() {
			// TODO Auto-generated method stub
			return new Rect((int)x, (int)y, (int)(x+width), (int)(y+height));
		}
	}
	
	
	public static class Size {
		public int width;
		public int height;
		public Size(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}
	
	public static class SizeF {
		public float width;
		public float height;
		public SizeF(float width, float height) {
			this.width = width;
			this.height = height;
		}
		public float width(){
			return width;
		}
		public float height(){
			return height;
		}
	}



}