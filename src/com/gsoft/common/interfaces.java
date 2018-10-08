package com.gsoft.common;

import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.SizeF;

public class interfaces {
	public interface OnTouchListener {
		abstract public void onTouchEvent(Object sender, MotionEvent e);
	}
	
	public interface Listener {
		abstract public void onEvent(Object sender);
	}
	
	public interface TimerListener {
		abstract public void onTick(Object sender);
		abstract public void onFinish(Object sender);
	}
	
	public interface Scale {
		public void scale(SizeF scaleFactor);
	}


}