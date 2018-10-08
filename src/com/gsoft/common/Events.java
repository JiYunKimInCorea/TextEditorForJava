package com.gsoft.common;

public class Events {
	public static enum Keys {
		NumPad2, NumPad4, NumPad6,
		NumPad5,
		Enter,
		BackSpace,
		Delete
	}

	public static class MotionEvent {
		public static final int ActionDown = 1;
		public static final int ActionMove = 2;
		public static final int ActionUp = 3;
		public static final int ActionDoubleClicked = 4;
		
		public int x;
		public int y;
		public int actionCode;
		
		public MotionEvent(int actionCode, int x, int y) {
			this.actionCode = actionCode;
			this.x = x;
			this.y = y;
			
		}
	}
	
	public static class KeyEvent {
		public static int KEYCODE_LEFT = 1;
		public static int KEYCODE_RIGHT = 2;
		public static int KEYCODE_SPACE = 3;
		public static int KEYCODE_DELETE = 4;
		public static int KEYCODE_ENTER = 5;
		public static int KEYCODE_MENU = 6;
		public static int KEYCODE_LINE = 7;
		
		int keyCode = 0;
		
		public KeyEvent(int keyCode) {
			this.keyCode = keyCode;
		}
		public boolean IsKeyDown(int keyCode) {
			if (this.keyCode==keyCode)
				return true;
			return false;		
		}
	}


}