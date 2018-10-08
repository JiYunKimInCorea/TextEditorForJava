package com.gsoft.texteditor14;

import java.awt.event.MouseEvent;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

import com.gsoft.common.Util.ControlStack;
import com.gsoft.common.gui.Control;
import com.gsoft.common.gui.EditRichText;
import com.gsoft.common.gui.ViewEx;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.texteditor14.CustomView;

public class CustomView_test extends ViewEx 
	implements 	com.gsoft.common.interfaces.OnTouchListener
{	
	public interface OnTouchListener2 extends  com.gsoft.common.interfaces.Listener, 
	com.gsoft.common.interfaces.TimerListener{
		abstract public void onTouchEvent(Object sender, MotionEvent e);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomView_test(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}


	long handlingTimeOfTouch;
	long time1OfTouch;
	long time2OfTouch;
	static boolean isSaved;
	
	public static void a() {
		
	}
	
	
	
	static class CustomView1_OnTouchListener implements OnTouchListener {
		long oldActionTime;
		boolean isClicked;
		CustomView owner;
		Point oldMovePoint;
		static int a=0; 
				
		public CustomView1_OnTouchListener(CustomView owner) {
			this.owner = owner;
			a();
		}
		
		
		public boolean onTouch(View v, MotionEvent event) {
			//Control.modified = true;
			CustomView1_OnTouchListener.a = 0;
			isSaved = false;
			int actionCode = event.getAction();
			int myActionCode = 0;
			if (actionCode==MotionEvent.ACTION_MOVE) {
				myActionCode = com.gsoft.common.Events.MotionEvent.ActionMove;
			}
			else 
				myActionCode = 0;						
			
			try {
				if (myActionCode==com.gsoft.common.Events.MotionEvent.ActionDown) {
					com.gsoft.common.Events.MotionEvent myEvent = 
							new com.gsoft.common.Events.MotionEvent(myActionCode, (int)event.getX(), (int)event.getY());
					oldMovePoint = new Point(myEvent.x,myEvent.y);
					if (!sized) return false;
					
					boolean r=false;	
					int i;
					ControlStack controlStack = Control.controlStack;
					
					//synchronized (Control.controlStack) {
					Control[] controls = controlStack.getItems();
					if (controls.length>0) {
						Control control = controls[controls.length-1];
						if (control instanceof com.gsoft.common.gui.Dialog) {
							
						}
					}
										
					/** editRichText, keyboard(내부에 editText가 있기 때문), sizingBorder는 
					// ActionDown에 연이은 AcitionMove를 받기 위해 여기서 true를 리턴해야 한다.*/
					return r;
				}
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			return true;
			
		}


		@Override
		/** 자바로 바꿀때 추가*/
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		/** 자바로 바꿀때 추가*/
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		/** 자바로 바꿀때 추가*/
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		/** 자바로 바꿀때 추가*/
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		/** 자바로 바꿀때 추가*/
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		/** 자바로 바꿀때 추가*/
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		/** 자바로 바꿀때 추가*/
		public void mouseMoved(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}		
		
	}

	

	
	static boolean sized = false;
	
	float scaleOfMenuX = 1.0f - 2;
	float scaleOfMenuY = 0.055f;
	
	
	//int height;
	
	Button buttonMenu;
	EditRichText editRichText;
	
	
	Paint paint = new Paint();
	

	
	/** editRichText:0, editText:1, terminal:2*/
	int isEditRichTextOrEditText = 1;
	
	
	
	@Override
	public void onFinish(Object sender) {
		// TODO Auto-generated method stub
				
	}


	
}