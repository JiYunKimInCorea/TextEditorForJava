package android;

import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import com.gsoft.common.gui.Control;

import android.content.Context;

public class view {
	public static class KeyCharacterMap {

		public static KeyCharacterMap load(int deviceId) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static class MotionEvent {
		int x;
		int y;
		int action;
		int eventTime;
		int downTime;

		public static final int ACTION_MOVE = 0;
		public static final int ACTION_DOWN = 1;
		public static final int ACTION_DOUBLE_CLICKED = 2;
		public static final int ACTION_UP = 3;
		
		public MotionEvent(int x, int y, int action, int eventTime) {
			this.x = x;
			this.y = y;
			this.action = action;
			this.eventTime = eventTime;
		}

		public int getAction() {
			// TODO Auto-generated method stub
			return action;
		}

		public int getX() {
			// TODO Auto-generated method stub
			return x;
		}

		public int getY() {
			// TODO Auto-generated method stub
			return y;
		}

		public int getEventTime() {
			// TODO Auto-generated method stub
			return eventTime;
		}

		public int getDownTime() {
			// TODO Auto-generated method stub
			return downTime;
		}

	}

	public static class WindowManager {

		public static class LayoutParams {

			public static final int FLAG_KEEP_SCREEN_ON = 128;
			
		}

	}

	public static class KeyEvent {
		int action;
		int keyCode;
		public java.awt.event.KeyEvent awtKeyEvent;

		public static final int ACTION_DOWN = 0;

		public static final int KEYCODE_BACK = 4;
		public static final int KEYCODE_ESCAPE = 111;

		public static final int KEYCODE_PAGE_UP = 92;

		public static final int KEYCODE_PAGE_DOWN = 93;

		public static final int KEYCODE_SHIFT_LEFT = 59;

		public static final int KEYCODE_SHIFT_RIGHT = 60;
		public static final int KEYCODE_ENTER = 66;
		public static final int KEYCODE_DEL = 67;
		public static final int KEYCODE_DPAD_LEFT = 0;
		public static final int KEYCODE_DPAD_RIGHT = 0;
		public static final int KEYCODE_DPAD_UP = 0;
		public static final int KEYCODE_DPAD_DOWN = 0;
		public static final int KEYCODE_HOME = 0;
		public static final int KEYCODE_ENDCALL = 0;
		public static final int KEYCODE_Z = 0;
		public static final int KEYCODE_Y = 0;
		public static final int KEYCODE_F = 0;
		public static final int KEYCODE_C = 0;
		public static final int KEYCODE_X = 0;
		public static final int KEYCODE_V = 0;
		public static final int KEYCODE_A = 0;

		public KeyEvent(java.awt.event.KeyEvent e) {
			// TODO Auto-generated constructor stub
			action = ACTION_DOWN;
			awtKeyEvent = e;
			if (e.getKeyCode()==java.awt.event.KeyEvent.VK_BACK_SPACE)
				keyCode = KEYCODE_BACK;
			else if (e.getKeyCode()==java.awt.event.KeyEvent.VK_ESCAPE)
				keyCode = KEYCODE_ESCAPE;
			else if (e.getKeyCode()==java.awt.event.KeyEvent.VK_PAGE_UP)
				keyCode = KEYCODE_PAGE_UP;
			else if (e.getKeyCode()==java.awt.event.KeyEvent.VK_PAGE_DOWN)
				keyCode = KEYCODE_PAGE_DOWN;
			else if (e.getKeyCode()==java.awt.event.KeyEvent.VK_SHIFT)
				keyCode = KEYCODE_SHIFT_LEFT;
			else if (e.getKeyCode()==java.awt.event.KeyEvent.VK_ENTER)
				keyCode = KEYCODE_ENTER;
			else if (e.getKeyCode()==java.awt.event.KeyEvent.VK_DELETE)
				keyCode = KEYCODE_DEL;
			else keyCode = -1;
		}

		public int getAction() {
			// TODO Auto-generated method stub
			return action;
		}

		public int getDeviceId() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getMetaState() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getUnicodeChar(int metaState) {
			// TODO Auto-generated method stub
			char c = this.awtKeyEvent.getKeyChar();
			return c;
		}

		public boolean isAltPressed() {
			// TODO Auto-generated method stub
			boolean b = this.awtKeyEvent.isAltDown();
			return b;
		}

		public boolean isShiftPressed() {
			// TODO Auto-generated method stub
			boolean b = this.awtKeyEvent.isShiftDown();
			return b;
		}

		public boolean isCtrlPressed() {
			// TODO Auto-generated method stub
			return false;
		}

	}
	
	public static class View extends JFrame implements WindowListener, KeyListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Context context;
		public Insets insets;
		
		public static interface OnTouchListener extends MouseListener, MouseMotionListener {
			public boolean onTouch(View v, MotionEvent event);
		}
		
		public View(Context context) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.addWindowListener(this);
			this.setUndecorated(true);
			this.addKeyListener(this);
			
		}
		
		public void setSize(int width, int height) {
			
			super.setSize(width, height);
			insets = this.getInsets();
		}
		
		public void postInvalidate() {
			// TODO Auto-generated method stub
			invalidate();
		}
		

		/** MouseListener와 MouseMotionListener는 매개변수의 onTouchListener이다.*/
		public void setOnTouchListener(OnTouchListener onTouchListener) {
			// TODO Auto-generated method stub
			this.addMouseListener(onTouchListener);
			this.addMouseMotionListener(onTouchListener);
		}

		public int getHeight() {
			// TODO Auto-generated method stub
			return getBounds().height;
		}

		public void invalidate() {
			// TODO Auto-generated method stub
			//super.invalidate();
			this.repaint();
			//this.paintAll(Paint.g);
			//this.setVisible(true);
		}

		public Context getContext() {
			// TODO Auto-generated method stub
			return context;
		}

		public int getWidth() {
			// TODO Auto-generated method stub
			return getBounds().width;
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			insets = this.getInsets();
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub
			//Control.activity.finish();
			System.exit(0);
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			// TODO Auto-generated method stub
			//Control.activity.finish();
			Control.exit(false);
			//System.exit(0);
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub
			insets = this.getInsets();
		}

		@Override
		public void keyPressed(java.awt.event.KeyEvent e) {
			// TODO Auto-generated method stub
			KeyEvent event = new KeyEvent(e); 
			Control.activity.onKeyDown(event.keyCode, event);
		}

		@Override
		public void keyReleased(java.awt.event.KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(java.awt.event.KeyEvent e) {
			// TODO Auto-generated method stub
			//KeyEvent event = new KeyEvent(e); 
			//Control.activity.onKeyDown(event.keyCode, event);
		}
		
	}
	
	public static class Window {

		public void setFlags(int flagKeepScreenOn, int flagKeepScreenOn2) {
			// TODO Auto-generated method stub
			
		}

		public void clearFlags(int flagKeepScreenOn) {
			// TODO Auto-generated method stub
			
		}
		
	}
}