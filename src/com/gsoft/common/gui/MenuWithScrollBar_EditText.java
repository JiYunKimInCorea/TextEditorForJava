package com.gsoft.common.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.Log;

import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util.Array;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.gui.EditText;
import com.gsoft.common.gui.Control.Container;
import com.gsoft.common.gui.ScrollBars.HScrollBar;
import com.gsoft.common.gui.ScrollBars.VScrollBarLogical;
import com.gsoft.common.interfaces.OnTouchListener;




public class MenuWithScrollBar_EditText extends Container implements OnTouchListener {
		
	public enum ScrollMode {
		VScroll,
		Both
	}
	
	public class EditTextLine {
		EditText[] editTexts;
		int count;
		public EditTextLine() {
			editTexts = new EditText[10];
		}
		public void add(EditText editText) {
			editTexts[count++] = editText;
		}
		public float measureWidth() {
			float r=0;
			int i;
			for (i=0; i<count; i++) {
				r += editTexts[i].bounds.width;
			}
			return r;
		}
	}
	
	VScrollBarLogical vScrollBar;
	HScrollBar hScrollBar;
	
	int vScrollBarWidth;
	int vScrollPos;
	
	public int numOfLines;
	int numOfLinesPerPage;
	//int numOfLinesInPage;
	
	int rationalBoundsWidth;
	int rationalBoundsHeight;
	int gapX;
	int lineHeight;
	
	public int originEditTextWidth;
	public int originEditTextHeight;
	
	EditText[] editTexts;
	
	EditTextLine[] linesOfEditTexts;
	
	ScrollMode scrollMode;
	//View view;
	
	public String selectedEditTextName;
	ArrayList editTextsOfSelect = new ArrayList(10);
	
	public EditText selectedEditText;
	
	private Paint paint;
	
	long oldMoveTime;
	private Point oldDownPoint = new Point(0,0);
	
	/** MenuProblemList_EditText에서 바운드가 바뀔때 사용*/
	public void changeBounds(Rectangle bounds, Size editTextSize) {
		this.bounds = bounds;
		originEditTextWidth = editTextSize.width;
		originEditTextHeight = editTextSize.height;
		/*for (int i=0; i<this.editTexts.length; i++) {
			EditText editText = editTexts[i];
			editText.changeBounds(new Rectangle(0,0,originEditTextWidth,originEditTextHeight));
		}*/
		bound();
	}
	
	public void changeBounds(Rectangle bounds) {
		this.bounds = bounds;
		
		bound();
	}
	
	void bound() {
		if (scrollMode==ScrollMode.VScroll) {
			rationalBoundsWidth = bounds.width - 2*gapX - vScrollBarWidth;
			rationalBoundsHeight = bounds.height;
			
			Rectangle boundsOfVScrollBar = new Rectangle(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);
			vScrollBar.changeBounds(boundsOfVScrollBar);
			
			if (editTexts!=null)
				setEditTexts(editTexts);
			
			numOfLinesPerPage = (int)(bounds.height / lineHeight/* + 1*/);
			
			
			setVScrollBar();
			//if (text!=null)	
			
		}
	}
	
	public MenuWithScrollBar_EditText(Object owner, Rectangle bounds, Size editTextSize,
			ScrollMode scrollMode) {
		this.bounds = bounds;
		this.scrollMode = scrollMode;
		paint = new Paint();
		paint.setStyle(Style.FILL);
		gapX = 5;
		
		/*try {
			view = getView(owner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return;
		}*/
		
		backColor = Color.CYAN;
		setBackColor(backColor);
		
		Context coneditTexts = view.getContext();
				
		lineHeight = editTextSize.height;
		originEditTextWidth = editTextSize.width;
		originEditTextHeight = editTextSize.height;
		
		linesOfEditTexts = new EditTextLine[50];
		
		if (scrollMode==ScrollMode.VScroll) {
			this.vScrollBarWidth = ScrollBars.getScrollBarSize();
			this.vScrollPos = 0;
			rationalBoundsWidth = bounds.width - 2*gapX - vScrollBarWidth;
			rationalBoundsHeight = bounds.height;
			numOfLinesPerPage = (int)(bounds.height / lineHeight)/*+1*/;
			
			Rectangle boundsOfVScrollBar = new Rectangle(bounds.x+bounds.width-vScrollBarWidth,
					bounds.y, vScrollBarWidth, this.rationalBoundsHeight);
						
			vScrollBar = new VScrollBarLogical
					(this, coneditTexts, boundsOfVScrollBar, 
					numOfLinesPerPage,
					numOfLines, vScrollPos, 1);			
			vScrollBar.setOnTouchListener(this);			
		}
		
	}
	
	public void setEditTexts(EditText[] editTexts) {
		if (scrollMode==ScrollMode.VScroll) {
			int i;
			vScrollPos = 0;
			this.editTexts = editTexts;
			if (editTexts==null) return;
			for (i=0; i<editTexts.length; i++) {
				editTexts[i].setOnTouchListener(this);
			}
			setEditTextsVScroll(editTexts);
			
			setVScrollBar();
			
			setEditTextsBounds();
						
		}		
		else if (scrollMode==ScrollMode.Both) {
		}	
	}
	
	public void setVScrollBar() {
		if (vScrollPos<0) vScrollPos = 0;
		if (numOfLines > numOfLinesPerPage) { 
			// vScrollPos numOfLines-numOfLinesPerPage 보다 크지못하도록 한다.
			if (vScrollPos>numOfLines-numOfLinesPerPage){
				vScrollPos = numOfLines-numOfLinesPerPage;
			}
		}
				
		vScrollBar.hides = false;
		vScrollBar.setVScrollBar(
				numOfLinesPerPage,
				numOfLines, vScrollPos, 1);	
	
	}
	
	public void setEditTextsVScroll(EditText[] editTexts) {
		try {
		numOfLines = 0 + 1;
		
		if (editTexts!=null && editTexts.length>0 && editTexts[0].bounds.width > rationalBoundsWidth) {
			throw new Exception("EditTextSize larger than rationalBoundsWidth");
		}
				
		EditTextLine editTextsTemp1 = new EditTextLine();
		EditTextLine editTextsTemp2 = new EditTextLine();
		int i;
		float lineWidth;
		
		EditText editText;
		int editTextsLen = editTexts.length;
		
		for (i=0; i<editTextsLen; i++) {
			editText = editTexts[i];		
			editTextsTemp1.add(editText);
			lineWidth = editTextsTemp1.measureWidth();
			if (lineWidth > rationalBoundsWidth) {
				setTextArrayNoSpaceError(numOfLines-1, editTextsTemp2);
				numOfLines++;
				editTextsTemp1 = new EditTextLine();
				editTextsTemp2 = new EditTextLine();
				i--;	// 초과된 버튼(다음줄의 첫버튼)를 다시 처리
			}
			else {
				editTextsTemp2.add(editTexts[i]);
				setTextArrayNoSpaceError(numOfLines-1, editTextsTemp2);
				//linesOfEditTexts[numOfLines-1].add(editText);
			}
			
		}		// for
		}catch(Exception e) {
			//Control.loggingForMessageBox.setText(false, "MenuWithScrollBar:"+e.toString(),false);
			//Control.loggingForMessageBox.setHides(false);
		}

	}
	
	void setTextArrayNoSpaceError(int lineNumber, EditTextLine editTextLine) {
		try{
		if (lineNumber<linesOfEditTexts.length) {
			linesOfEditTexts[lineNumber] = editTextLine;
		}
		else {
			/*do {
				linesOfEditTexts = Array.Resize(linesOfEditTexts, linesOfEditTexts.length+10);
			}while(lineNumber>=linesOfEditTexts.length);*/
			linesOfEditTexts = Array.Resize(linesOfEditTexts, linesOfEditTexts.length*2);
			
			linesOfEditTexts[lineNumber] = editTextLine;
		}
		}catch(Exception e) {
			//Control.loggingForMessageBox.setText(true, "MenuWithScrollBar:"+e.toString(),false);
			//Control.loggingForMessageBox.setHides(false);
		}
	}
	
	/** MenuWithScrollBar_EditText에 넣어진 EditText들의 bounds 를 결정한다. 
	 * 따라서 EditText의 changeBounds()를 호출하여 bounds 를 바꾼다. 
	 * 트리플버퍼링일 경우 bounds 를 바꾸면 비트맵을 바꾸고 그 비트맵에 그리기 위해
	 * drawToImage를 호출하여 다시 그린다. 
	 * invalidate()가 되면 draw()에서 다시 그려진 비트맵으로 블록이동(blt) 한다.*/
	public void setEditTextsBounds() {
		synchronized(this) {
		try {
    	if (editTexts==null || editTexts.length==0) return;
    	
    	    	
    	int i;
    	int x, y;
    	int j;
    	if (scrollMode==ScrollMode.VScroll) {
    		int limit = Math.min(vScrollPos+numOfLinesPerPage, this.numOfLines);
			for (i=vScrollPos; i<limit; i++) {
				x = bounds.x + gapX;
				y = bounds.y + (i-vScrollPos+1) * lineHeight;
				//if (y < bounds.y+bounds.height) {
					EditTextLine editTextLine = linesOfEditTexts[i];					
					for (j=0; j<editTextLine.count; j++) {
						Rectangle newEditTextBounds = new Rectangle(x, y-lineHeight, 
								originEditTextWidth/**0.9f*/, 
								originEditTextHeight/**0.9f*/);
						if (editTextLine.editTexts[j].iName==629) {
							int a;
							a=0;
							a++;
						}
						editTextLine.editTexts[j].changeBounds(newEditTextBounds);
						x += originEditTextWidth;
					}
				//}					
			}
		}
    
    	
		}catch(Exception e) {
			Log.e("MenuWithScrollBar-setEditTextsBounds()", e.toString());
		}
		}
	}
	
	public void draw(Canvas canvas) {
		synchronized(this) {
		try {
		if (hides) return;
		
		paint.setColor(backColor);				
		canvas.drawRect(bounds.toRectF(), paint);
    	if (editTexts==null || editTexts.length==0) return;
    	
    	    	
    	int i;
    	int x, y;
    	int j;
    	if (scrollMode==ScrollMode.VScroll) {
    		int limit = Math.min(vScrollPos+numOfLinesPerPage, this.numOfLines);
			for (i=vScrollPos; i<limit; i++) {
				x = bounds.x + gapX;
				y = bounds.y + (i-vScrollPos+1) * lineHeight;
				//if (y < bounds.y+bounds.height) {
					EditTextLine editTextLine = linesOfEditTexts[i];					
					for (j=0; j<editTextLine.count; j++) {
						/*Rectangle newEditTextBounds = new Rectangle(x, y-lineHeight, 
								originEditTextWidth, 
								originEditTextHeight);
						if (editTextLine.editTexts[j].iName==629) {
							int a;
							a=0;
							a++;
						}
						editTextLine.editTexts[j].changeBounds(newEditTextBounds);*/
						//editTextLine.editTexts[j].draw(canvas, newEditTextBounds.x, newEditTextBounds.y);
						editTextLine.editTexts[j].draw(canvas);
						x += originEditTextWidth;
					}
				//}					
			}
		}
    
    	if (scrollMode==ScrollMode.Both) {
			vScrollBar.draw(canvas);
			hScrollBar.draw(canvas);
		}
		else {
			vScrollBar.draw(canvas);
		}
		}catch(Exception e) {
			Log.e("MenuWithScrollBar-draw", e.toString());
		}
		}
	}
	
	/**"Left", "Right", "Up", "Down", "Home", "End", "PgUp", "PgDn"*/
	public void controlChar(int indexInSpecialKeys, String charA) {
		switch (indexInSpecialKeys) {
		case 2: {
			if (vScrollPos>0) vScrollPos--; 
			break;
		}
		case 3: {
			if (vScrollPos<numOfLines-numOfLinesPerPage) vScrollPos++;
			break;
		}
		case 6: {
				vScrollPos -= numOfLinesPerPage;
				if (vScrollPos<0) vScrollPos = 0;
				break;
			}				
		case 7: if (numOfLines>numOfLinesPerPage) {			
					vScrollPos += numOfLinesPerPage;
					if (vScrollPos>numOfLines-numOfLinesPerPage) 
						vScrollPos = numOfLines-numOfLinesPerPage;
					break;
				}
		}
		//setVScrollPos(heightOfvScrollPos);
		setVScrollBar();
	}
	
	@Override
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r=false;
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    	r = super.onTouch(event, scaleFactor);
	    	if (!r) return false;
	    	if (editTexts==null || editTexts.length==0) return false;
	    	int i, j;
	    	if (scrollMode==ScrollMode.VScroll) {
	    		boolean r1=false;
				r1 = vScrollBar.onTouch(event, null);
				if (r1) {
					return true;
				}				
								
				
	    		// 한 번 그린 뒤에(draw에서 vScrollPos에 따라 EditText들의 영역을 다시 계산한다)
	    		// 터치를 하므로 다음과 같이 하는 것이 가능하다. 
				// 다시 계산된 버튼들의 영역을 바탕으로 터치 이벤트를 전달한다.
				int limit = Math.min(vScrollPos+numOfLinesPerPage, this.numOfLines);
				for (i=vScrollPos; i<limit; i++) {
					//if (y < bounds.y+bounds.height) {
						EditTextLine editTextLine = linesOfEditTexts[i];						
						for (j=0; j<editTextLine.count; j++) {
							r = editTextLine.editTexts[j].onTouch(event, null);
							if (r) {
								if (editTextLine.editTexts[j].isSelected) {
									editTextsOfSelect.add(editTextLine.editTexts[j]);
								}
								return true;
							}
						}
					//}
				}
				
				// MenuScrollable영역안에서 스크롤바와 버튼들을 제외한 공백영역을 터치시
				// 선택된 버튼을 원래대로 초기화한다.
				for (i=0; i<editTextsOfSelect.count; i++) {
					EditText editText = (EditText)editTextsOfSelect.getItem(i);
					editText.isSelected = false;
				}
				editTextsOfSelect.reset();
				
				capturedControl = this;
				
				onTouchEvent(this, event);
				
				oldDownPoint.x = event.x;
				oldDownPoint.y = event.y;
				oldMoveTime = System.currentTimeMillis();
				
	    	}
	    	
	    	return true;
    	}
    	else if (event.actionCode==MotionEvent.ActionMove) {
    		if (this==Control.capturedControl) {// 영역검사를 하지않고 영역을 벗어나더라도 자신이 핸들링한다.
    			onTouchEvent(this, event);
				return true;
			}
    	}
    	return false;
    }
		
	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub	
		
		if (sender instanceof EditText) {
			EditText editText = (EditText)sender;
			int i;
			for (i=0; i<editTexts.length; i++) {
				if (editText.iName==editTexts[i].iName) {
					selectedEditTextName = editText.name;
					selectedEditText = editText;
					listener.onTouchEvent(this, e);
					return;
				}
			}
		}
		else if (sender instanceof VScrollBarLogical) {
			VScrollBarLogical vScrollBar = (VScrollBarLogical)sender;
			this.vScrollPos = vScrollBar.vScrollPos;
			setVScrollBar();
			setEditTextsBounds();
		}
		else if (sender instanceof MenuWithScrollBar_EditText) {
			if (e.actionCode==MotionEvent.ActionDown) {
				selectedEditTextName = null;
				selectedEditText = null;
				listener.onTouchEvent(this, e);
			}
			else if (e.actionCode==MotionEvent.ActionMove) {
				long curMoveTime = System.currentTimeMillis();
				if (curMoveTime-oldMoveTime<500) {	// 느리게 스크롤하는 효과
					//oldMoveTime = curMoveTime; 
					return;
				}				
				oldMoveTime = curMoveTime;
				
				boolean r = false;				
				
				if (e.y>bounds.bottom()) {
					if (numOfLines > numOfLinesPerPage) { 
						vScrollPos++;
						setVScrollBar();
						r = true;
					}						
				}
				else if (e.y<bounds.y){
					if (vScrollPos>0) { 
						vScrollPos--;
						setVScrollBar();
						r = true;
					}						
				}
				if (r==false) {
					boolean u = false;
					int diff = 10;
					if (e.y-oldDownPoint.y>diff) {
						if (numOfLines > numOfLinesPerPage) { 
							vScrollPos++;
							setVScrollBar();
							u = true;
						}							
					}
					else if (e.y-oldDownPoint.y<-diff) {
						if (vScrollPos>0) {
							vScrollPos--;						
							setVScrollBar();
							u = true;
						}							
					}
					
					if (u) {
						oldDownPoint.x = e.x;
						oldDownPoint.y = e.y;
					}
				}//if (r==false) {
			}//else if (e.actionCode==MotionEvent.ActionMove) {
			this.setEditTextsBounds();
		}//else if (sender instanceof MenuWithScrollBar_EditText) {
		
	}//onTouchEvent()
}