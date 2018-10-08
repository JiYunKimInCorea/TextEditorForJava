package com.gsoft.common;

import android.graphics.Color;

public class ColorEx extends Color {
	public static final int RedBlue = 
			Color.rgb( Color.red(Color.RED)+Color.red(Color.BLUE),
					Color.green(Color.RED)+Color.green(Color.BLUE),
					Color.blue(Color.RED)+Color.blue(Color.BLUE) );	// magenta
	public static final int RedGreen = 
			Color.rgb( Color.red(Color.RED)+Color.red(Color.GREEN),
					Color.green(Color.RED)+Color.green(Color.GREEN),
					Color.blue(Color.RED)+Color.blue(Color.GREEN) );	// yellow
	public static final int GreenBlue = 
			Color.rgb( Color.red(Color.GREEN)+Color.red(Color.BLUE),
					Color.green(Color.GREEN)+Color.green(Color.BLUE),
					Color.blue(Color.GREEN)+Color.blue(Color.BLUE) );	// cyon
	
		
		
	public static int add(int c1, int c2) {
		int r, g, b;
		r = Color.red(c1)+Color.red(c2);
		g = Color.green(c1)+Color.green(c2);
		b = Color.blue(c1)+Color.blue(c2);
		if (r>255) r=255;
		if (g>255) g=255;
		if (b>255) b=255;
		if (r<0) r=0;
		if (g<0) g=0;
		if (b<0) b=0;
		return Color.rgb(r,g,b);
	}
	
	public static int middle(int c1, int c2) {
		int r, g, b;
		r = (Color.red(c1)+Color.red(c2)) / 2;
		g = (Color.green(c1)+Color.green(c2)) / 2;
		b = (Color.blue(c1)+Color.blue(c2)) / 2;
		if (r>255) r=255;
		if (g>255) g=255;
		if (b>255) b=255;
		if (r<0) r=0;
		if (g<0) g=0;
		if (b<0) b=0;
		return Color.rgb(r,g,b);
	}
	
	/** 성분단위 합*/
	public static int addElements(int c1, int inc) {
		int r, g, b;
		r = Color.red(c1)+inc;
		g = Color.green(c1)+inc;
		b = Color.blue(c1)+inc;
		if (r>255) r=255;
		if (g>255) g=255;
		if (b>255) b=255;
		if (r<0) r=0;
		if (g<0) g=0;
		if (b<0) b=0;
		return Color.rgb(r,g,b);
	}
	
	/** 성분단위 곱*/
	public static int mulElements(int c1, float scale) {
		int r, g, b;
		r = (int) (Color.red(c1)*scale);
		g = (int) (Color.green(c1)*scale);
		b = (int) (Color.blue(c1)*scale);
		if (r>255) r=255;
		if (g>255) g=255;
		if (b>255) b=255;
		if (r<0) r=0;
		if (g<0) g=0;
		if (b<0) b=0;
		return Color.rgb(r,g,b);
	}
	
	public static String toString(int color) {
		if (color==Color.WHITE) return "White";
		else if (color==Color.BLACK) return "Black";
		else if (color==Color.RED) return "Red";
		else if (color==Color.GREEN) return "Green";
		else if (color==Color.BLUE) return "Blue";
		else if (color==Color.LTGRAY) return "LtGray";
		else if (color==Color.DKGRAY) return "DkGray";
		else if (color==Color.MAGENTA) return "Magenta";
		else if (color==Color.CYAN) return "Cyan";
		else if (color==Color.GREEN) return "Green";
		else if (color==Color.YELLOW) return "Yellow";
		else if (color==Color.GRAY) return "Gray";
		else return "Others";
	}
	
	public static boolean isSameColor(int color0, int color1) {
		int r0, g0, b0;
		r0 = Color.red(color0);
		g0 = Color.green(color0);
		b0 = Color.blue(color0);
		
		int r1, g1, b1;
		r1 = Color.red(color1);
		g1 = Color.green(color1);
		b1 = Color.blue(color1);
		
		int diff = 50;
		boolean rSame, gSame, bSame;
		if (r0>=r1) {
			if (r0-r1<diff) rSame = true;
			else rSame = false;
		}
		else {
			if (r1-r0<diff) rSame = true;
			else rSame = false; 
		}
		if (g0>=g1) {
			if (g0-g1<diff) gSame = true;
			else gSame = false;
		}
		else {
			if (g1-g0<diff) gSame = true;
			else gSame = false; 
		}
		
		if (b0>=b1) {
			if (b0-b1<diff) bSame = true;
			else bSame = false;
		}
		else {
			if (b1-b0<diff) bSame = true;
			else bSame = false; 
		}
		
		if (rSame && gSame && bSame) return true;
		return false;
	}
		
	public static int reverseColor(int color) {
		int oldR, oldG, oldB;
		oldR = Color.red(color);
		oldG = Color.green(color);
		oldB = Color.blue(color);
		int r = 255-oldR;
		int g = 255-oldG;
		int b = 255-oldB;
		
		// 기존색과 새로운색간의 차이를 두어 색이 구별되도록 한다.
		int diff = 127;
		if (r>=oldR) {
			if (r-oldR<diff) r = oldR+diff;
		}
		else {
			if (oldR-r<diff) r = oldR-diff; 
		}
		if (g>=oldG) {
			if (g-oldG<diff) g = oldG+diff;
		}
		else {
			if (oldG-g<diff) g = oldG-diff; 
		}
		if (b>=oldB) {
			if (b-oldB<diff) b = oldB+diff;
		}
		else {
			if (oldB-b<diff) b = oldB-diff; 
		}
		
		if (r>255) r=255;
		if (g>255) g=255;
		if (b>255) b=255;
		if (r<0) r=0;
		if (g<0) g=0;
		if (b<0) b=0;
		/*int min = 128 - diff;
		int max = 128 + diff;
		if (min<r && r<max && min<g && g<max && min<b && b<max)
			return Color.rgb(0, 0, 0);*/
		return Color.rgb(r, g, b);
	}
	
	public static int darkerOrLighter(int color, float scale) {
		if (scale<0) return Color.BLACK;
		
		int r, g, b;
		int oldR, oldG, oldB;
		oldR = Color.red(color);
		oldG = Color.green(color);
		oldB = Color.blue(color);
		r = oldR;
		g = oldG;
		b = oldB;
		if (oldR!=0) r = (int) (oldR*scale);
		if (oldG!=0) g = (int) (oldG*scale);
		if (oldB!=0) b = (int) (oldB*scale);
		if (r>255) r=255;
		if (g>255) g=255;
		if (b>255) b=255;
		if (r<0) r=0;
		if (g<0) g=0;
		if (b<0) b=0;
		return Color.rgb(r, g, b);
	}
	
	public static int darkerOrLighter(int color, int inc) {
		int r, g, b;
		int oldR, oldG, oldB;
		oldR = Color.red(color);
		oldG = Color.green(color);
		oldB = Color.blue(color);
		r = oldR;
		g = oldG;
		b = oldB;
		if (oldR!=0) r = (oldR+inc);
		if (oldG!=0) g = (oldG+inc);
		if (oldB!=0) b = (oldB+inc);
		if (r>255) r=255;
		if (g>255) g=255;
		if (b>255) b=255;
		if (r<0) r=0;
		if (g<0) g=0;
		if (b<0) b=0;
		return Color.rgb(r, g, b);
		
	}
	
}