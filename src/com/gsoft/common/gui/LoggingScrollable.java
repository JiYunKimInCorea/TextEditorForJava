package com.gsoft.common.gui;

//import java.awt.Graphics;
//import java.awt.image.BufferedImage;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

import com.gsoft.common.ColorEx;
import com.gsoft.common.Util.Math;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Util.Array;
import com.gsoft.common.Util.ArrayListChar;
import com.gsoft.common.gui.EditText.ScrollMode;
import com.gsoft.common.Sizing.Rectangle;


import com.gsoft.common.Sizing.SizeF;

public class LoggingScrollable extends Control {
	int vScrollPos;
	
	public int numOfLines;
	int numOfLinesPerPage;
	int numOfLinesInPage;
	
	int rationalBoundsWidth;
	int rationalBoundsHeight;
	int gapX;
	float fontSize;
	float lineHeight;
	
	Rectangle realBounds = new Rectangle();
	
	String text;
	
	String[] textArray;
	
	ScrollMode scrollMode;
	View view;
	
	private Paint paint;
	
	public boolean isTransparent = false;
	
	public int backColor;
	public int textColor;

	//private BufferedImage mImage2;

	//private Canvas mCanvas2;

	public synchronized void setHides(boolean hides) {
		/*if (hides==false) {
			if (this.hides==false) {	// 이미 열려 있으면
				open(false);	// 스택에서 제거
				open(true);		// 다시 연다.
			}
			else {
				open(true);
			}
		}
		else {
			if (this.hides==false) {	// 열려 있으면 닫는다.
				open(false);
			}
		}*/
		//if (this.hides==hides) return;
		this.hides = hides;
		open(!this.hides);
	}
		
	public void changeBounds(Rectangle bounds) {
		this.bounds = bounds;
		
		bound();
	}
	
	void bound() {
		if (scrollMode==ScrollMode.VScroll) {
			rationalBoundsWidth = bounds.width - 2*gapX;
			rationalBoundsHeight = bounds.height;
			numOfLinesPerPage = (int)(bounds.height / lineHeight);
			numOfLinesInPage = numOfLines - vScrollPos;
			numOfLinesInPage = Math.min(numOfLinesPerPage, numOfLinesInPage);			
					
			
			if (text!=null)	setText(true, text, isTransparent);
		}
	}
	
	public LoggingScrollable(Object owner, Rectangle bounds, String text,
			ScrollMode scrollMode, float fontSize) {
		super();
		this.bounds = bounds;
		this.text = text;
		this.scrollMode = scrollMode;
		paint = new Paint();
		paint.setStyle(Style.FILL);
		gapX = 5;
		this.fontSize = fontSize;
		float descent = this.fontSize * 0.25f;
		this.lineHeight = this.fontSize + descent;	
		paint.setTextSize(fontSize);
		hides = true;
		backColor = ColorEx.darkerOrLighter(Color.BLUE, 0.5f);
		setBackColor(backColor);
		
		try {
			view = getView(owner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return;
		}
		textArray = new String[100];
		
		if (scrollMode==ScrollMode.VScroll) {
			this.vScrollPos = 0;
			rationalBoundsWidth = bounds.width - 2*gapX;
			rationalBoundsHeight = bounds.height;
			numOfLinesPerPage = (int)(bounds.height / lineHeight);
			numOfLinesInPage = numOfLines - vScrollPos;
			numOfLinesInPage = Math.min(numOfLinesPerPage, numOfLinesInPage);
					
			
			if (text!=null)	
				setText(true, text, isTransparent);
		}
		
	}
	
	public void setVScrollPos() {
		if (numOfLines > numOfLinesPerPage) {
			vScrollPos = numOfLines - numOfLinesPerPage;
		}
		else vScrollPos = 0;
		numOfLinesInPage = numOfLines - vScrollPos;
		numOfLinesInPage = Math.min(numOfLinesPerPage, numOfLinesInPage);
	
	}
		
		
	public synchronized void setText(boolean replaceOrAdd, String text, boolean isTransparent) {
		if (text==null || text.equals("")) {
			numOfLines = 0;
			return;
		}
		if (replaceOrAdd) this.text = text;
		else this.text += text;
		
		this.isTransparent = isTransparent;
		numOfLines = 0 + 1;
		
		ArrayListChar textTemp1 = new ArrayListChar(30);
		ArrayListChar textTemp2 = new ArrayListChar(30);
		ArrayListChar[] textListArray = new ArrayListChar[textArray.length];
			
		int i;
		float lineWidth;
		
		String charA;
		int textLen = text.length();
		
		paint.setTextSize(this.fontSize);
		
		/*for (i=0; i<textLen; i++) {
			charA = text.substring(i,i+1);
			textTemp1.add(charA.charAt(0));
			lineWidth = paint.measureText(new String(textTemp1.getItems()));
			if (charA.equals("\n")) {
				textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, textTemp1);
				numOfLines++;			
				// textTemp1.reset()을 사용하지 않고 다음과 같이 한다. 줄마다 동일한 메모리 참조
				textTemp1 = new ArrayListChar(30);
				textTemp2 = new ArrayListChar(30);
			}
			else {
				if (lineWidth > rationalBoundsWidth) {
					textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, textTemp2);
					numOfLines++;
					// textTemp1.reset()을 사용하지 않고 다음과 같이 한다. 줄마다 동일한 메모리 참조
					textTemp1 = new ArrayListChar(30);
					textTemp2 = new ArrayListChar(30);
					i--;	// 초과된 문자를 다시 처리
				}
				else {
					textTemp2.add(charA.charAt(0));
					textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, textTemp2);
				}
			}
			
		}		// for*/
		
		lineWidth = 0;
		
		for (i=0; i<textLen; i++) {
			charA = text.substring(i,i+1);
			textTemp1.add(charA.charAt(0));
			lineWidth += paint.measureText(charA);
			if (charA.equals("\n")) {
				textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, textTemp1);
				numOfLines++;			
				// textTemp1.reset()을 사용하지 않고 다음과 같이 한다. 줄마다 동일한 메모리 참조
				textTemp1 = new ArrayListChar(30);
				textTemp2 = new ArrayListChar(30);
				lineWidth = 0;
			}
			else {
				if (lineWidth > rationalBoundsWidth) {
					textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, textTemp2);
					numOfLines++;
					// textTemp1.reset()을 사용하지 않고 다음과 같이 한다. 줄마다 동일한 메모리 참조
					textTemp1 = new ArrayListChar(30);
					textTemp2 = new ArrayListChar(30);
					i--;	// 초과된 문자를 다시 처리
					lineWidth = 0;
				}
				else {
					textTemp2.add(charA.charAt(0));
					textListArray = setTextArrayNoSpaceError(textListArray, numOfLines-1, textTemp2);
				}
			}
			
		}	
		
				
		textArray = Array.Resize(textArray, textListArray.length);
		for (i=0; i<textArray.length; i++) {
				if (textListArray[i]!=null && textListArray[i].count>0) {
					textArray[i] = new String(textListArray[i].getItems());
				}
				else if (textListArray[i]==null) {
					//textArray[i] = "";
					break;
				}
				else {
					textArray[i] = "";
				}
		}
		
		/*if (this.numOfLines>1) {
			float m = paint.measureText(textArray[0]);
			if (rationalBoundsWidth - m > paint.measureText("A")) {
				setText(replaceOrAdd, text, isTransparent);
			}
		}*/
		
		/*if (textArray[0].charAt(textArray[0].length()-1)=='s') {
			try {
			throw new Exception("error");
			}catch(Exception e) {				
				e.printStackTrace();
				setText(replaceOrAdd, text, isTransparent);
				//CompilerHelper.printStackTrace(CmmonGUI, e)
			}
		}*/
		
		setVScrollPos();
		
		setRealBounds();

	}
	
	/** bounds(처음에 설정되어 바뀌지 않음)가 아니라 
	 * 실제 draw에서 그려지는 bounds를 말한다.*/
	void setRealBounds() {
		realBounds.x = (int) bounds.x;
		realBounds.y = (int) bounds.y;
		realBounds.width = (int) (bounds.width);
		realBounds.height = (int) (lineHeight * numOfLinesInPage + fontSize * 0.4f);
	}
	
	public void open(boolean isOpen) {
		Rectangle boundsOfBackup = bounds;
		bounds = realBounds;
		super.open(isOpen);
		bounds = boundsOfBackup;
		
	}
	
	String[] setTextArrayNoSpaceError(String[] textArray, int lineNumber, String text) {
		if (lineNumber<textArray.length) {
			textArray[lineNumber] = text;
		}
		else {
			textArray = Array.Resize(textArray, lineNumber+20);
			
			textArray[lineNumber] = text;
		}
		return textArray;
	}
	
	ArrayListChar[] setTextArrayNoSpaceError(ArrayListChar[] textArray, int lineNumber, ArrayListChar text) {
		if (lineNumber<textArray.length) {
			textArray[lineNumber] = text;
		}
		else {
			textArray = Array.Resize(textArray, lineNumber+20);
			
			textArray[lineNumber] = text;
		}
		return textArray;
	}
	
	public void setBackColor(int color) {
		backColor = color;
		textColor = ColorEx.reverseColor(backColor);
	}
	
	public void setTextColor(int color) {
		textColor = color;
		backColor = ColorEx.reverseColor(textColor);
	}
	
	public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	if  (getIsOpen()==false) {
    		return false;
    	} 
    	super.onTouch(event, scaleFactor);
    	
    	if (event.actionCode==MotionEvent.ActionDown) {
    		open(false);	// 영역에 상관없이 닫힌다.
    		return true;
    	}
    	return false;
	}
	
	/** 자바에서만*/
	/*public void paint(Graphics g) {
		//Paint.g = g;
		//Canvas canvas = new Canvas(g);
		if (mImage2==null) {
			mImage2 = new BufferedImage((int)bounds.width,(int)bounds.height,BufferedImage.TYPE_INT_ARGB); 
			Graphics gForImage = mImage2.getGraphics();
			Paint.g = gForImage;
			mCanvas2 = new Canvas(gForImage);
		}
		onDraw(mCanvas2);
		g.drawImage(mImage2, 0, 0, null);
		//g.dispose();
	}*/
	
	
	public synchronized void draw(Canvas canvas) {
		synchronized(this) {
		try{
		if (hides) return;
		
		if (!isTransparent) {
			/*Rect rect = new Rect();
			rect.left = (int) bounds.x;
			rect.top = (int) bounds.y;
			rect.right = (int) (bounds.x + bounds.width);
			rect.bottom = (int) (bounds.y + lineHeight * numOfLinesInPage + fontSize * 0.4f);*/
			paint.setStyle(Style.FILL);
			paint.setColor(backColor);
			canvas.drawRect(realBounds.toRectF(), paint);
		}
		
    	int i, j;
    	float x, y;
    	if (scrollMode==ScrollMode.VScroll) {
    		paint.setColor(textColor);
    		paint.setTextSize(fontSize);
    		paint.setStyle(Style.STROKE);
			for (i=vScrollPos; i<vScrollPos+numOfLinesInPage; i++) {
				x = bounds.x + gapX;
				y = bounds.y + (i-vScrollPos+1) * lineHeight;
				//String lineText = deleteNewLineChar(textArray[i]);							
				//canvas.drawText(lineText, x, y, paint);
				
				String lineText = textArray[i]; 
				for (j=0; j<lineText.length(); j++) {
					String charA = lineText.substring(j, j+1);
					if (charA.equals("\t")) {
						x += paint.measureText("\t");
					}
					else if (charA.equals("\n")) continue;
					else {
						canvas.drawText(charA, x, y, paint);
						x += paint.measureText(charA);
					}
				}
			}
		}
		}catch(Exception e) {
    		
    	}
		}
    	
	}
	
	String deleteNewLineChar(String lineText) {
		int lineLen = lineText.length();
		if (lineLen > 0) {
			if (lineText.substring(lineLen-1, lineLen).equals("\n")) {
				if (lineLen > 1)
					return lineText.substring(0, lineLen-1);
				else 
					return new String("");
			}
			else {
				return lineText;
			}
		}
		else {
			return "";
		}
	}

}