package com.gsoft.common.gui;

import com.gsoft.common.ColorEx;
import com.gsoft.common.Events;
import com.gsoft.common.Sizing;
import com.gsoft.common.interfaces;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.interfaces.OnTouchListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class ScrollBars {
	public static float VScrollBarWidthScale = 0.065f;
	public static float HScrollBarHeightScale = 0.04f;
	
	/** viewEx의 initControls에서 호출된다.*/
	public static void setScrollBarScale(View view) {
		int width = view.getWidth();
		int height = view.getHeight();
		if (height>width) {
			VScrollBarWidthScale = 0.065f;
			HScrollBarHeightScale = 0.04f;
		}
		else {
			VScrollBarWidthScale = 0.04f;
			HScrollBarHeightScale = 0.06f;
		}
	}
	
	/** 스크롤막대의 사각형(pageUp, pageDown, pageLeft, pageRight)*/
	static class RectForPage extends Control {
		Paint paint = new Paint();
		Paint paintOfBorder = new Paint();
		int backColor;
		/** up이면 true, down이면 false*/
		boolean isUpOrDown;
		
		public RectForPage(Object owner, RectangleF bounds, int backColor,
				boolean isUpOrDown) {
			super();
			this.bounds = bounds;
			//isBoundsAble();
			this.owner = owner;
			this.backColor = backColor;
			this.isUpOrDown = isUpOrDown;
			paint.setStyle(Style.FILL);
			paintOfBorder.setStyle(Style.STROKE);
			paint.setColor(backColor);
			paintOfBorder.setColor(ColorEx.darkerOrLighter(backColor, 0.5f));
		}
		
		public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
	    	if (event.actionCode==MotionEvent.ActionDown) {
				if (super.onTouch(event,scaleFactor)==false) return false;
	    		callTouchListener(this, event);
	    		return true;
	    	}	
	    	
	    	return false;
	    }
		
		public void draw(Canvas canvas) {
			//if (boundsEnable){
				canvas.drawRect(bounds.toRectF(), paint);    		        
	        	canvas.drawRect(bounds.toRectF(), paintOfBorder);
			//}
		}
	}
	
	static class Thumb extends Control {
		Paint paint = new Paint();
		Paint paintOfBorder = new Paint();
		int backColor;
		boolean dragAndDrop;
		
		public Thumb(Object owner, RectangleF bounds, int backColor) {
			super();
			this.bounds = bounds;
			//isBoundsAble();
			this.owner = owner;
			this.backColor = backColor;
			paint.setStyle(Style.FILL);
			paintOfBorder.setStyle(Style.STROKE);
			paint.setColor(backColor);
			paintOfBorder.setColor(ColorEx.darkerOrLighter(backColor, 0.5f));
		}
		
		
		public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
			/*if (isMoveActionCaptured && event.actionCode==MotionEvent.ActionMove) {
				callTouchListener(this, event);
				return true;
			}*/
			if (event.actionCode==MotionEvent.ActionMove) {
				callTouchListener(this, event);
				return true;
			}
			if (event.actionCode==MotionEvent.ActionDown) {
				if (super.onTouch(event,scaleFactor)==false) return false;
	    		callTouchListener(this, event);
	    		return true;
	    	}    	
	    	return false;
	    }
		
		public void draw(Canvas canvas) {
			//if (boundsEnable){
		        canvas.drawRect(bounds.toRectF(), paint);    		        
	        	canvas.drawRect(bounds.toRectF(), paintOfBorder);
			//}
		}
		
	}
	
	static public class VScrollBarLogical extends Control implements OnTouchListener {
		
		SpinControl spinControl;
		RectForPage rectForPageUp;
		RectForPage rectForPageDown;
		Thumb thumb;
		
		float heightOfBar;
		
		int numOfLinesPerPage;
		int numOfLinesInPage;
		int numOfLines;	
		int vScrollPos;
		int vScrollInc;
		
		int diffThumbYFromEventY;
				
		public VScrollBarLogical(Object owner, Context context, RectangleF bounds, 
				int numOfLinesPerPage, int numOfLinesInPage, 
				int numOfLines, int vScrollPos, int vScrollInc) {
			super();
			
			this.owner = owner;
			this.bounds = bounds;
			//this.lineHeight = lineHeight;
			spinControl = new SpinControl(owner, context, bounds, true, true);
			spinControl.setOnTouchListener(this);
			
			heightOfBar = bounds.height - 2 * spinControl.boundsUp.height;
					
			this.numOfLinesPerPage = numOfLinesPerPage;
			this.numOfLinesInPage = numOfLinesInPage;
			this.vScrollPos = vScrollPos;
			this.vScrollInc = vScrollInc;
			this.numOfLines = numOfLines;	
			
			//if (heightOfBar!=0) {
				RectangleF boundsOfThumb = new RectangleF();
				thumb = new Thumb(owner, boundsOfThumb, Color.BLUE);
				thumb.setOnTouchListener(this);
				
				RectangleF boundsOfrectForPageUp = new RectangleF();
				rectForPageUp = new RectForPage(owner, boundsOfrectForPageUp, Color.CYAN, true);
				rectForPageUp.setOnTouchListener(this);
				
				RectangleF boundsOfrectForPageDown = new RectangleF();
				rectForPageDown = new RectForPage(owner, boundsOfrectForPageDown, Color.CYAN, false);
				rectForPageDown.setOnTouchListener(this);
			//}
				
			setVScrollBar(
				numOfLinesPerPage,
				numOfLinesInPage,
				numOfLines, vScrollPos, vScrollInc);	
			
		}
		
		public void changeBounds(RectangleF bounds) {
			this.bounds = bounds;
			spinControl.changeBounds(bounds);
			heightOfBar = bounds.height - 2 * spinControl.boundsUp.height;
			setVScrollBar(numOfLinesPerPage, numOfLinesInPage, 
					numOfLines, vScrollPos, vScrollInc);
		}
		
		public void setVScrollBar(int numOfLinesPerPage, int numOfLinesInPage, 
				int numOfLines, int vScrollPos, int vScrollInc) {
					
			this.numOfLinesPerPage = numOfLinesPerPage;
			this.numOfLinesInPage = numOfLinesInPage;
			this.vScrollPos = vScrollPos;
			this.vScrollInc = vScrollInc;
			this.numOfLines = numOfLines;	
			
			//if (heightOfBar!=0) {
				rectForPageUp.bounds.x	= bounds.x;
				rectForPageUp.bounds.y = spinControl.boundsUp.bottom();
				rectForPageUp.bounds.width = bounds.width;
				float s = ((float)vScrollPos / (float)numOfLines);
				if (s<0 || s>1)
					try {
						throw new Exception("ScrollBar error");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				rectForPageUp.bounds.height = heightOfBar * s;
				
				//rectForPageUp.isBoundsAble();
				
				thumb.bounds.x = bounds.x;
				thumb.bounds.y = spinControl.boundsUp.bottom() + rectForPageUp.bounds.height;
				thumb.bounds.width = bounds.width;
				s = (float)numOfLinesInPage / (float)numOfLines;
				thumb.bounds.height = (int) (s * heightOfBar);
				if (s<0 || s>1)
					try {
						throw new Exception("ScrollBar error");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				//thumb.isBoundsAble();
				
				rectForPageDown.bounds.x = bounds.x;
				rectForPageDown.bounds.y = thumb.bounds.bottom();
				rectForPageDown.bounds.width = bounds.width;
				s = (float)(numOfLines-vScrollPos-numOfLinesInPage) / (float)numOfLines;
				rectForPageDown.bounds.height = (int) (s * heightOfBar);
				if (s<0 || s>1)
					try {
						throw new Exception("ScrollBar error");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				//rectForPageDown.isBoundsAble();
			//}
			
		}
		
				
		public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
			/*if (isMoveActionCaptured && event.actionCode==MotionEvent.ActionMove) {
				thumb.onTouch(event, scaleFactor);
		    	return true;
			}*/
			
			boolean r;
			if (event.actionCode==MotionEvent.ActionDown) {
				if (super.onTouch(event,scaleFactor)==false) return false;
				r = spinControl.onTouch(event, scaleFactor);
				if (r) {
		    		//capturedControl=this;		    		
		    		return true;
		    	}
		    	r = rectForPageUp.onTouch(event, scaleFactor);
		    	if (r) {
		    		//capturedControl=this;
		    		return true;
		    	}
		    	r = rectForPageDown.onTouch(event, scaleFactor);
		    	if (r) {
		    		//capturedControl=this;
		    		return true;
		    	}		    	
		    	r = thumb.onTouch(event, scaleFactor);
		    	if (r) {
		    		capturedControl=this;
		    		return true;
		    	}
			}
			else if (event.actionCode==MotionEvent.ActionMove) {
				if (capturedControl==this) {
					//((Control)owner).modified = true;
					thumb.onTouch(event, scaleFactor);
					return true;
				}
				
			}
	    	
	    	return false;
	    }
		
		@Override
		public void draw(Canvas canvas) {
			if (hides) return;
			synchronized(this) {
			try{
			spinControl.draw(canvas);
			rectForPageUp.draw(canvas);
			rectForPageDown.draw(canvas);
			thumb.draw(canvas);
			}catch(Exception e) {
	    		
	    	}
			}
		}

		@Override
		public void onTouchEvent(Object sender, MotionEvent e) {
			// TODO Auto-generated method stub
			
			if (sender instanceof SpinControl) {
				SpinControl spinControl = (SpinControl)sender;
				if (spinControl.isVertOrHorz) {
					if (spinControl.isUpClicked) {
						vScrollPos-=vScrollInc;
						if (vScrollPos<0) vScrollPos = 0;
					}
					else if (spinControl.isDownClicked) {						
							vScrollPos+=vScrollInc;
							/*if (numOfLines>numOfLinesPerPage) {
								if (vScrollPos>numOfLines-numOfLinesPerPage) 
									vScrollPos = numOfLines-numOfLinesPerPage;
							}*/
							if (vScrollPos+numOfLinesInPage>=numOfLines)
								vScrollPos = numOfLines - numOfLinesInPage;
							if (vScrollPos<0) vScrollPos = 0;
					}
					int numOfLinesInPage = numOfLines - vScrollPos;
					numOfLinesInPage = Math.min(numOfLinesPerPage, numOfLinesInPage);
					setVScrollBar(numOfLinesPerPage, numOfLinesInPage, 
							numOfLines, vScrollPos, vScrollInc);
					listener.onTouchEvent(this, e);
				}				
				
			} // spinControl
			else if (sender instanceof RectForPage) {
				RectForPage rectForPage = (RectForPage)sender;
				if (rectForPage.isUpOrDown) {
					vScrollPos -= this.numOfLinesPerPage;
					if (vScrollPos<0) vScrollPos = 0;
				}
				else {
					vScrollPos += this.numOfLinesPerPage;
					if (vScrollPos+numOfLinesInPage>=numOfLines)
						vScrollPos = numOfLines - numOfLinesInPage;
					if (vScrollPos<0) vScrollPos = 0;
				}
				int numOfLinesInPage = numOfLines - vScrollPos;
				numOfLinesInPage = Math.min(numOfLinesPerPage, numOfLinesInPage);
				setVScrollBar(numOfLinesPerPage, 
						numOfLinesInPage, numOfLines, vScrollPos, vScrollInc);
				listener.onTouchEvent(this, e);
			}
			else if (sender instanceof Thumb) {
				Thumb thumb = (Thumb)sender;
				if (e.actionCode==MotionEvent.ActionDown) {
					diffThumbYFromEventY = (int) (e.y - thumb.bounds.y);
				}
				else {
					int eventY = e.y;
					if (eventY>spinControl.boundsDown.y) eventY = (int) spinControl.boundsDown.y;
					if (eventY<spinControl.boundsUp.bottom()) eventY = (int) spinControl.boundsUp.bottom();
					int newThumbY = eventY - diffThumbYFromEventY;
					newThumbY -= spinControl.boundsUp.bottom();
					// newScrollPos : newThumbY = numOfLines : heightOfBar
					vScrollPos = (int) (numOfLines/heightOfBar*newThumbY);
					
					if (vScrollPos+numOfLinesInPage>=numOfLines)
						vScrollPos = numOfLines - numOfLinesInPage;
					if (vScrollPos<0) vScrollPos=0;
					// newThumbY : newScrollPos = rectForPageUp.bounds.height : vScrollPos
					// newScrollPos = vScrollPos / rectForPageUp.bounds.height * newThumbY
				}
				int numOfLinesInPage = numOfLines - vScrollPos;
				numOfLinesInPage = Math.min(numOfLinesPerPage, numOfLinesInPage);
				setVScrollBar(numOfLinesPerPage, 
						numOfLinesInPage, numOfLines, vScrollPos, vScrollInc);
				listener.onTouchEvent(this, e);
			}
			
		}
	}
	
	static public class VScrollBar extends Control implements OnTouchListener {
				
		SpinControl spinControl;
		RectForPage rectForPageUp;
		RectForPage rectForPageDown;
		Thumb thumb;
		
		float heightOfBar;
		
		int heightOfLinesPerPage;
		int heightOfLinesInPage;
		int heightOfLines;	
		int heightOfvScrollPos;
		int heightOfvScrollInc;
		
		
		int diffThumbYFromEventY;
				
		public VScrollBar(Object owner, Context context, RectangleF bounds) {
			super();
			this.owner = owner;
			this.bounds = bounds;
			//this.lineHeight = lineHeight;
			spinControl = new SpinControl(owner, context, bounds, true, true);
			spinControl.setOnTouchListener(this);
			
			heightOfBar = bounds.height - 2 * spinControl.boundsUp.height;
					
			RectangleF boundsOfThumb = new RectangleF();
			thumb = new Thumb(owner, boundsOfThumb, Color.BLUE);
			thumb.setOnTouchListener(this);
			
			RectangleF boundsOfrectForPageUp = new RectangleF();
			rectForPageUp = new RectForPage(owner, boundsOfrectForPageUp, Color.CYAN, true);
			rectForPageUp.setOnTouchListener(this);
			
			RectangleF boundsOfrectForPageDown = new RectangleF();
			rectForPageDown = new RectForPage(owner, boundsOfrectForPageDown, Color.CYAN, false);
			rectForPageDown.setOnTouchListener(this);
			
			setVScrollBar(heightOfLinesPerPage, heightOfLinesInPage, 
				heightOfLines, heightOfvScrollPos, heightOfvScrollInc);
			
		}
		
		public void changeBounds(RectangleF bounds) {
			this.bounds = bounds;
			spinControl.changeBounds(bounds);
			heightOfBar = bounds.height - 2 * spinControl.boundsUp.height;
			// 기존값으로 수치를 정하므로 오차가 생길 수 있다.
			setVScrollBar(heightOfLinesPerPage, heightOfLinesInPage, 
				heightOfLines, heightOfvScrollPos, heightOfvScrollInc);
		}
				
		public void setVScrollBar(int heightOfLinesPerPage, int heightOfLinesInPage, 
				int heightOfLines, int heightOfvScrollPos, int heightOfvScrollInc) {
					
			this.heightOfLinesPerPage = heightOfLinesPerPage;
			this.heightOfLinesInPage = heightOfLinesInPage;
			this.heightOfvScrollPos = heightOfvScrollPos;
			this.heightOfvScrollInc = heightOfvScrollInc;
			this.heightOfLines = heightOfLines;	
			
			rectForPageUp.bounds.x	= bounds.x;
			rectForPageUp.bounds.y = spinControl.boundsUp.bottom();
			rectForPageUp.bounds.width = bounds.width;
			float s = ((float)heightOfvScrollPos / (float)heightOfLines);
			rectForPageUp.bounds.height = heightOfBar * s;
			//rectForPageUp.isBoundsAble();
			
			thumb.bounds.x = bounds.x;
			thumb.bounds.y = spinControl.boundsUp.bottom() + rectForPageUp.bounds.height;
			thumb.bounds.width = bounds.width;
			s = (float)heightOfLinesInPage / (float)heightOfLines;
			thumb.bounds.height = (int) (s * heightOfBar);
			//thumb.isBoundsAble();
			
			rectForPageDown.bounds.x = bounds.x;
			rectForPageDown.bounds.y = thumb.bounds.bottom();
			rectForPageDown.bounds.width = bounds.width;
			s = (float)(heightOfLines-heightOfvScrollPos-heightOfLinesInPage) / (float)heightOfLines;
			rectForPageDown.bounds.height = (int) (s * heightOfBar);
			//rectForPageDown.isBoundsAble();
			
		}
				
		public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
			/*if (isMoveActionCaptured && event.actionCode==MotionEvent.ActionMove) {
				thumb.onTouch(event, scaleFactor);
				return true;
			}*/
			boolean r;
			if (event.actionCode==MotionEvent.ActionDown) {
				if (super.onTouch(event,scaleFactor)==false) return false;				
		    	r = spinControl.onTouch(event, scaleFactor);
		    	if (r) {
		    		//capturedControl=this;
		    		return true;
		    	}
		    	r = rectForPageUp.onTouch(event, scaleFactor);
		    	if (r) {
		    		//capturedControl=this;
		    		return true;
		    	}
		    	r = rectForPageDown.onTouch(event, scaleFactor);
		    	if (r) {
		    		//capturedControl=this;
		    		return true;
		    	}		    	
		    	r = thumb.onTouch(event, scaleFactor);
		    	if (r) {
		    		capturedControl=this;
		    		return true;
		    	}
			}
			else if (event.actionCode==MotionEvent.ActionMove) {
				if (capturedControl==this) {
					//((Control)owner).modified = true;
					thumb.onTouch(event, scaleFactor);
					return true;
				}
				
			}
	    	return false;
	    }
		
		@Override
		public void draw(Canvas canvas) {
			if (hides) return;
			synchronized(this) {
			try{
			spinControl.draw(canvas);
			rectForPageUp.draw(canvas);
			rectForPageDown.draw(canvas);
			thumb.draw(canvas);
			}catch(Exception e) {
	    		
	    	}
			}
		}

		@Override
		public void onTouchEvent(Object sender, MotionEvent e) {
			// TODO Auto-generated method stub
			
			if (sender instanceof SpinControl) {
				SpinControl spinControl = (SpinControl)sender;
				if (spinControl.isVertOrHorz) {
					if (spinControl.isUpClicked) {
						heightOfvScrollPos-=heightOfvScrollInc;
						if (heightOfvScrollPos<0) heightOfvScrollPos = 0;
					}
					else if (spinControl.isDownClicked) {						
							heightOfvScrollPos+=heightOfvScrollInc;
							/*if (heightOfLines>heightOfLinesPerPage) {
								if (heightOfvScrollPos>heightOfLines-heightOfLinesPerPage) 
									heightOfvScrollPos = heightOfLines-heightOfLinesPerPage;
							}*/
							if (heightOfvScrollPos+heightOfLinesInPage>=heightOfLines)
								heightOfvScrollPos = heightOfLines - heightOfLinesInPage;
							if (heightOfvScrollPos<0) heightOfvScrollPos = 0;
					}
					int heightOfLinesInPage = heightOfLines - heightOfvScrollPos;
					heightOfLinesInPage = Math.min(heightOfLinesPerPage, heightOfLinesInPage);
					setVScrollBar(heightOfLinesPerPage, heightOfLinesInPage, 
							heightOfLines, heightOfvScrollPos, heightOfvScrollInc);
					listener.onTouchEvent(this, e);
				}				
				
			} // spinControl
			else if (sender instanceof RectForPage) {
				RectForPage rectForPage = (RectForPage)sender;
				if (rectForPage.isUpOrDown) {
					heightOfvScrollPos -= this.heightOfLinesPerPage;
					if (heightOfvScrollPos<0) heightOfvScrollPos = 0;
				}
				else {
					heightOfvScrollPos += this.heightOfLinesPerPage;
					/*if (heightOfLines>heightOfLinesPerPage) {
						if (heightOfvScrollPos>heightOfLines-heightOfLinesPerPage) 
							heightOfvScrollPos = heightOfLines-heightOfLinesPerPage;
					}*/
					if (heightOfvScrollPos+heightOfLinesInPage>=heightOfLines)
						heightOfvScrollPos = heightOfLines - heightOfLinesInPage;
					if (heightOfvScrollPos<0) heightOfvScrollPos = 0;
				}
				int heightOfLinesInPage = heightOfLines - heightOfvScrollPos;
				heightOfLinesInPage = Math.min(heightOfLinesPerPage, heightOfLinesInPage);
				setVScrollBar(heightOfLinesPerPage, 
						heightOfLinesInPage, heightOfLines, heightOfvScrollPos, heightOfvScrollInc);
				listener.onTouchEvent(this, e);
			}
			else if (sender instanceof Thumb) {
				Thumb thumb = (Thumb)sender;
				if (e.actionCode==MotionEvent.ActionDown) {
					diffThumbYFromEventY = (int) (e.y - thumb.bounds.y);
				}
				else {
					int eventY = e.y;
					if (eventY>spinControl.boundsDown.y) eventY = (int) spinControl.boundsDown.y;
					if (eventY<spinControl.boundsUp.bottom()) eventY = (int) spinControl.boundsUp.bottom();
					int newThumbY = eventY - diffThumbYFromEventY;
					newThumbY -= spinControl.boundsUp.bottom();
					// newScrollPos : newThumbY = heightOfLines : heightOfBar
					heightOfvScrollPos = (int) (heightOfLines/heightOfBar*newThumbY);
					
					/*if (heightOfLines>heightOfLinesPerPage) {
						if (heightOfvScrollPos>heightOfLines-heightOfLinesPerPage)
							heightOfvScrollPos=heightOfLines-heightOfLinesPerPage;
					}*/
					if (heightOfvScrollPos+heightOfLinesInPage>=heightOfLines)
						heightOfvScrollPos = heightOfLines - heightOfLinesInPage;
					if (heightOfvScrollPos<0) heightOfvScrollPos=0;
					// newThumbY : newScrollPos = rectForPageUp.bounds.height : heightOfvScrollPos
					// newScrollPos = heightOfvScrollPos / rectForPageUp.bounds.height * newThumbY
				}
				int heightOfLinesInPage = heightOfLines - heightOfvScrollPos;
				heightOfLinesInPage = Math.min(heightOfLinesPerPage, heightOfLinesInPage);
				setVScrollBar(heightOfLinesPerPage, 
						heightOfLinesInPage, heightOfLines, heightOfvScrollPos, heightOfvScrollInc);
				listener.onTouchEvent(this, e);
			}
			
		}
	}
	
static public class HScrollBar extends Control implements OnTouchListener {
		
		SpinControl spinControl;
		/** 스크롤막대의 왼쪽 사각형*/
		RectForPage rectForPageLeft;
		/** 스크롤막대의 오른쪽 사각형*/
		RectForPage rectForPageRight;
		/** 스크롤막대의 스크롤가능 사각형(page)*/
		Thumb thumb;
		
		float widthOfBar;
		
		int widthOfCharsPerPage;
		int widthOfCharsInPage;
		int widthOfTotalChars;
		
		int widthOfScrollPos;
		int widthOfScrollInc;
		
		int diffThumbXFromEventX;
				
		public HScrollBar(Object owner, Context context, RectangleF bounds) {
			super();
			this.owner = owner;
			this.bounds = bounds;
			spinControl = new SpinControl(owner, context, bounds, true, false);
			spinControl.setOnTouchListener(this);
			
			widthOfBar = bounds.width - 2 * spinControl.boundsLeft.width;
			
			RectangleF boundsOfThumb = new RectangleF();
			thumb = new Thumb(owner, boundsOfThumb, Color.BLUE);
			thumb.setOnTouchListener(this);
			
			RectangleF boundsOfrectForPageLeft = new RectangleF();
			rectForPageLeft = new RectForPage(owner, boundsOfrectForPageLeft, Color.CYAN, true);
			rectForPageLeft.setOnTouchListener(this);
			
			RectangleF boundsOfrectForPageRight = new RectangleF();
			rectForPageRight = new RectForPage(owner, boundsOfrectForPageRight, Color.CYAN, false);
			rectForPageRight.setOnTouchListener(this);
			
			setHScrollBar(widthOfCharsPerPage, widthOfCharsInPage, 
				widthOfTotalChars, widthOfScrollPos, widthOfScrollInc);
			
		}
		
		public void changeBounds(RectangleF bounds) {
			this.bounds = bounds;
			spinControl.changeBounds(bounds);
			widthOfBar = bounds.width - 2 * spinControl.boundsLeft.width;
			// 기존값으로 수치를 정하므로 오차가 생길 수 있다.
			setHScrollBar(widthOfCharsPerPage, widthOfCharsInPage, 
					widthOfTotalChars, widthOfScrollPos, widthOfScrollInc);
		}				
		
		/** 스크롤바의 한 페이지크기, 전체 영역, 스크롤위치(전체영역에서 현재위치), 
		 * 스크롤증감치(스크롤바의 가장 모서리부분터치시)*/
		public void setHScrollBar(int widthOfCharsPerPage, 
				int widthOfCharsInPage, 
				int widthOfTotalChars, int widthOfScrollPos, int widthOfScrollInc) {
					
			this.widthOfCharsPerPage = widthOfCharsPerPage;
			this.widthOfCharsInPage = widthOfCharsInPage;
			this.widthOfScrollPos = widthOfScrollPos;
			this.widthOfScrollInc = widthOfScrollInc;
			this.widthOfTotalChars = widthOfTotalChars;	
			
			float s=0;
			
			rectForPageLeft.bounds.x = spinControl.boundsLeft.right();
			rectForPageLeft.bounds.y = bounds.y;
			if (widthOfTotalChars!=0)
				s = ((float)widthOfScrollPos / (float)widthOfTotalChars);
			else 
				s = 0;
			rectForPageLeft.bounds.width = widthOfBar * s;			
			rectForPageLeft.bounds.height = bounds.height;
			//rectForPageLeft.isBoundsAble();
			
			thumb.bounds.x = rectForPageLeft.bounds.right();
			thumb.bounds.y = bounds.y;
			if (widthOfTotalChars!=0)
				s = (float)widthOfCharsInPage / (float)widthOfTotalChars;
			else s = 1;
			thumb.bounds.width = (s * widthOfBar);			
			thumb.bounds.height = bounds.height;	
			//thumb.isBoundsAble();
			
			rectForPageRight.bounds.x = thumb.bounds.right();
			rectForPageRight.bounds.y = bounds.y;
			if (widthOfTotalChars!=0)
				s = (float)(widthOfTotalChars-widthOfScrollPos-widthOfCharsInPage) 
					/ (float)widthOfTotalChars;
			else s = 0;
			rectForPageRight.bounds.width = (s * widthOfBar);			
			rectForPageRight.bounds.height = bounds.height;
			//rectForPageRight.isBoundsAble();
		
			
		}
		
		/** 이 컨트롤이 action을 capture하였을 경우 ActionMove이벤트를 이 컨트롤의 thumb이 처리한다.*/
		public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
			boolean r;
			if (event.actionCode==MotionEvent.ActionDown) {
				if (super.onTouch(event,scaleFactor)==false) return false;
				r = spinControl.onTouch(event, scaleFactor);
				if (r) {
		    		//capturedControl=this;
		    		return true;
		    	}
		    	r = rectForPageLeft.onTouch(event, scaleFactor);
		    	if (r) {
		    		//capturedControl=this;
		    		return true;
		    	}
		    	r = rectForPageRight.onTouch(event, scaleFactor);
		    	if (r) {
		    		//capturedControl=this;
		    		return true;
		    	}		    	
		    	r = thumb.onTouch(event, scaleFactor);
		    	if (r) {
		    		capturedControl=this;
		    		return true;
		    	}
			}
			else if (event.actionCode==MotionEvent.ActionMove) {
				if (capturedControl==this) {
					((Control)owner).modified = true;
					thumb.onTouch(event, scaleFactor);
					return true;
				}
				
			}
	    	
	    	return false;
	    }
		
		@Override
		public void draw(Canvas canvas) {
			if (hides) return;
			synchronized(this) {
			try{
			spinControl.draw(canvas);
			rectForPageLeft.draw(canvas);
			rectForPageRight.draw(canvas);
			thumb.draw(canvas);
			}catch(Exception e) {
	    		
	    	}
			}
		}
		
		/** event handler*/
		public void onTouchEvent(Object sender, MotionEvent e) {
			// TODO Auto-generated method stub
			
			if (sender instanceof SpinControl) {
				SpinControl spinControl = (SpinControl)sender;
				if (!spinControl.isVertOrHorz) {
					if (spinControl.isLeftClicked) {
						widthOfScrollPos-=widthOfScrollInc;
						if (widthOfScrollPos<0) widthOfScrollPos = 0;				
					}
					else if (spinControl.isRightClicked) {
						
							widthOfScrollPos+=widthOfScrollInc;
							/*if (widthOfTotalChars>widthOfCharsPerPage) {
								if (widthOfScrollPos>widthOfTotalChars-widthOfCharsPerPage) 
									widthOfScrollPos = widthOfTotalChars-widthOfCharsPerPage;
							}*/
							if (widthOfScrollPos+widthOfCharsInPage>=widthOfTotalChars)
								widthOfScrollPos = widthOfTotalChars - widthOfCharsInPage;
							if (widthOfScrollPos<0) widthOfScrollPos = 0;
					}
					int widthOfCharsInPage = widthOfTotalChars - widthOfScrollPos;
					widthOfCharsInPage = Math.min(widthOfCharsPerPage, widthOfCharsInPage);
					setHScrollBar(widthOfCharsPerPage, 
							widthOfCharsInPage, widthOfTotalChars, widthOfScrollPos, widthOfScrollInc);
					listener.onTouchEvent(this, e);
				}
			} // spinControl
			else if (sender instanceof RectForPage) {
				RectForPage rectForPage = (RectForPage)sender;
				if (rectForPage.isUpOrDown) {
					widthOfScrollPos -= this.widthOfCharsPerPage;
					if (widthOfScrollPos<0) widthOfScrollPos = 0;
				}
				else {
					widthOfScrollPos += this.widthOfCharsPerPage;
					//if (widthOfScrollPos<0) widthOfScrollPos = 0;
					/*if (widthOfTotalChars>widthOfCharsPerPage) {
						if (widthOfScrollPos>widthOfTotalChars-widthOfCharsPerPage) {
							widthOfScrollPos = widthOfTotalChars-widthOfCharsPerPage;
						}
					}*/
					if (widthOfScrollPos+widthOfCharsInPage>=widthOfTotalChars)
						widthOfScrollPos = widthOfTotalChars - widthOfCharsInPage;
					if (widthOfScrollPos<0) widthOfScrollPos = 0;
				}
				int widthOfCharsInPage = widthOfTotalChars - widthOfScrollPos;
				widthOfCharsInPage = Math.min(widthOfCharsPerPage, widthOfCharsInPage);
				setHScrollBar(widthOfCharsPerPage, 
						widthOfCharsInPage, widthOfTotalChars, widthOfScrollPos, widthOfScrollInc);
				listener.onTouchEvent(this, e);
			}
			else if (sender instanceof Thumb) {
				Thumb thumb = (Thumb)sender;
				int eventX = e.x;
				if (e.actionCode==MotionEvent.ActionDown) {
					diffThumbXFromEventX = (int) (e.x - thumb.bounds.x);
				}
				else  {
					if (eventX>spinControl.boundsRight.x) eventX = (int) spinControl.boundsRight.x;
					if (eventX<spinControl.boundsLeft.right()) eventX = (int) spinControl.boundsLeft.right();
					int newThumbX = eventX - diffThumbXFromEventX;
					newThumbX -= spinControl.boundsLeft.right();
					widthOfScrollPos = (int) (widthOfTotalChars/widthOfBar*newThumbX);
					
					/*if (widthOfTotalChars>widthOfCharsPerPage) {
						if (widthOfScrollPos>widthOfTotalChars-widthOfCharsPerPage)
							widthOfScrollPos=widthOfTotalChars-widthOfCharsPerPage;
					}*/
					if (widthOfScrollPos+widthOfCharsInPage>=widthOfTotalChars)
						widthOfScrollPos = widthOfTotalChars - widthOfCharsInPage;
					if (widthOfScrollPos<0) widthOfScrollPos=0;
					// newThumbX : newScrollPos = rectForPageLeft.bounds.width : widthOfScrollPos
					// newScrollPos = widthOfScrollPos / rectForPageLeft.bounds.width * newThumbX
				}
				int widthOfCharsInPage = widthOfTotalChars - widthOfScrollPos;
				widthOfCharsInPage = Math.min(widthOfCharsPerPage, widthOfCharsInPage);
				setHScrollBar(widthOfCharsPerPage, 
						widthOfCharsInPage, widthOfTotalChars, widthOfScrollPos, widthOfScrollInc);
				listener.onTouchEvent(this, e);
			}
			
		}		
	}


}
