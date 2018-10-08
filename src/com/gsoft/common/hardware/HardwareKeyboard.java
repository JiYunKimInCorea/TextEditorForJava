package com.gsoft.common.hardware;

import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.gsoft.common.CommonGUI;
import com.gsoft.common.gui.Control;
import com.gsoft.common.gui.IntegrationKeyboard;
import com.gsoft.common.gui.IntegrationKeyboard.Mode;

public class HardwareKeyboard {
	int keyCode;
	KeyEvent event;
	public static boolean isEnglishKeyboard = true;
	
	static KeyCharacterMap keyCharacterMap;
	public String key;
	
	static char[] buf = new char[2];
	static int count = 0;
	
	/** 윈도우즈와 안드로이드 키보드 처리가 다르기 때문에 구별한다.
	 * Control.CurrentSystem이 "JAVA"이면 makeKeyForJava()을 호출,
	 * Control.CurrentSystem이 "ANDROID"이면 makeKeyForAndroid()을 호출한다.*/
	private void makeKeyForAndroid(int keyCode, KeyEvent event) {
		int metaState = event.getMetaState();
		//char c = (char) keyCharacterMap.get(keyCode, metaState);
		int unicode = event.getUnicodeChar(metaState); 
		char c = (char)unicode;
		
		boolean isShiftPressed = event.isShiftPressed();
		boolean isAltPressed = event.isAltPressed();
		boolean isCtrlPressed = event.isCtrlPressed();
		
		// 0(keyCode==59, 65)
		if (keyCode==67) {
			key = "BkSp";
			count = 0;
		}
		else if (c=='\n') {
			key = IntegrationKeyboard.Enter;
			count = 0;
		}
		else if (isShiftPressed && keyCode==KeyEvent.KEYCODE_SHIFT_LEFT || keyCode==KeyEvent.KEYCODE_SHIFT_RIGHT) {
			key = "Shift";
			count = 0;
		}
		else if (c=='%') {
			buf[count++] = c;
			char[] arg = {c};
			key = new String(arg);
		}
		else if (c=='h') {				
			if (isEnglishKeyboard) {
				char[] arg = {c};
				key = new String(arg);
			}
			else {
				key = convertToHangul(c);
			}
			if (count>0 && buf[count-1]=='%') {
				isEnglishKeyboard = !isEnglishKeyboard;
				if (isEnglishKeyboard) {
					CommonGUI.keyboard.mode = Mode.Eng;
				}
				else {
					CommonGUI.keyboard.mode = Mode.Hangul;
				}
				//process자판(Control.keyboard.mode);
			}
			count = 0;
		}
		else if (keyCode==KeyEvent.KEYCODE_PAGE_UP) {
			key = "PgUp";
			count = 0;
		}
		else if (keyCode==KeyEvent.KEYCODE_PAGE_DOWN) {
			key = "PgDn";
			count = 0;
		}
		else {
			count = 0;
			if (keyCode==KeyEvent.KEYCODE_DPAD_LEFT) {
				key = "Left";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT) {
				key = "Right";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (keyCode==KeyEvent.KEYCODE_DPAD_UP) {
				key = "Up";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN) {
				key = "Down";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (keyCode==KeyEvent.KEYCODE_HOME) {
				key = "Home";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (keyCode==KeyEvent.KEYCODE_ENDCALL) {
				key = "End";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (unicode==65535 && !isShiftPressed) { // Window 키, 한자키 등
				key = IntegrationKeyboard.Shift;
				count = 0;
			}
			else {
				if (isCtrlPressed) { // 키보드 가속기
					IntegrationKeyboard.isCtrlPressed = true;
					if (keyCode==KeyEvent.KEYCODE_Z) {//undo
						key = new String("Z");
					}
					else if (keyCode==KeyEvent.KEYCODE_Y) {//redo
						key = new String("Y");
					}
					else if (keyCode==KeyEvent.KEYCODE_F) {//find/replace
						key = new String("F");
					}
					else if (keyCode==KeyEvent.KEYCODE_C) {//copy
						key = new String("C");
					}
					else if (keyCode==KeyEvent.KEYCODE_X) {//cut
						key = new String("X");
					}
					else if (keyCode==KeyEvent.KEYCODE_V) {//paste
						key = new String("V");
					}
					else if (keyCode==KeyEvent.KEYCODE_A) {//select all
						key = new String("A");
					}
				}//if (isCtrlPressed) { // 키보드 가속기
				else if (isAltPressed) { // 키보드 가속기
					IntegrationKeyboard.isAltPressed = true;
				}
				else { // 일반적인 키
					if (isEnglishKeyboard) {
						char[] arg = {c};
						key = new String(arg);
					}
					else {
						key = convertToHangul(c);
					}
				}// 일반적인 키
			}//else
			/*count = 0;
			if (isEnglishKeyboard) {
				char[] arg = {c};
				key = new String(arg);
			}
			else {
				key = convertToHangul(c);
			}*/
		}//else
	}
	
	
	
	/** 윈도우즈와 안드로이드 키보드 처리가 다르기 때문에 구별한다.
	 * Control.CurrentSystem이 "JAVA"이면 makeKeyForJava()을 호출,
	 * Control.CurrentSystem이 "ANDROID"이면 makeKeyForAndroid()을 호출한다.*/
	private void makeKeyForJava(int keyCode, KeyEvent event) {
		
		IntegrationKeyboard.isCtrlPressed = false;
		IntegrationKeyboard.isAltPressed = false;
		
		int metaState = event.getMetaState();
		//char c = (char) keyCharacterMap.get(keyCode, metaState);
		int unicode = event.getUnicodeChar(metaState); 
		char c = (char)unicode;
		boolean isShiftPressed = event.isShiftPressed();
		boolean isAltPressed = event.isAltPressed();
		boolean isCtrlPressed = event.awtKeyEvent.isControlDown();
		
		if (c=='1') {
			int a;
			a=0;
			a++;
		}
		
		int awtKeyCode = event.awtKeyEvent.getKeyCode();
		
		// 0(keyCode==59, 65)
		if (keyCode==KeyEvent.KEYCODE_BACK) {
			key = IntegrationKeyboard.BackSpace;
			count = 0;
		}
		else if (keyCode==KeyEvent.KEYCODE_DEL) {
			key = IntegrationKeyboard.Delete;
			count = 0;
		}
		else if (c=='\n' || keyCode==KeyEvent.KEYCODE_ENTER) {
			key = IntegrationKeyboard.Enter;
			count = 0;
		}
		
		else if (isShiftPressed && awtKeyCode==java.awt.event.KeyEvent.VK_SPACE) {
			isEnglishKeyboard = !isEnglishKeyboard;
			
			if (isEnglishKeyboard) { // 한글모드에서 영문모드로 바뀌면
				if (IntegrationKeyboard.Hangul.mode != 
						IntegrationKeyboard.Hangul.Mode.None) { 
					// 한글이 조합중일때만 커서를 전진
					IntegrationKeyboard.Hangul.forwardCursor();
				}
				IntegrationKeyboard.Hangul.resetBuffer();
				IntegrationKeyboard.Hangul.mode = IntegrationKeyboard.Hangul.Mode.None;
				
				CommonGUI.keyboard.mode = Mode.Eng;
			}
			else { // 영문모드에서 한글모드로 바뀌면
				IntegrationKeyboard.Hangul.resetBuffer();
				CommonGUI.keyboard.mode = Mode.Hangul;
			}
			count = 0;
			
			key = IntegrationKeyboard.Shift;
			
			//IntegrationKeyboard.Hangul.mode = IntegrationKeyboard.Hangul.Mode.None;
			//IntegrationKeyboard.Hangul.isNextToCursor = true;
		}
		
		else if (unicode==65535 && isShiftPressed) {
			key = IntegrationKeyboard.Shift;
			count = 0;
		}
		
		
		
		
		else if (c=='%') {
			count = 0;
			buf[count++] = c;
			char[] arg = {c};
			key = new String(arg);
		}
		else if (c=='h') {				
			if (isEnglishKeyboard) {
				char[] arg = {c};
				key = new String(arg);
			}
			else {
				key = convertToHangul(c);
			}
			if (count>0 && buf[count-1]=='%') {
				isEnglishKeyboard = !isEnglishKeyboard;
				if (isEnglishKeyboard) {
					CommonGUI.keyboard.mode = Mode.Eng;
				}
				else {
					CommonGUI.keyboard.mode = Mode.Hangul;
				}				
				//process자판(Control.keyboard.mode);
			}
			count = 0;
		}
		else if (keyCode==KeyEvent.KEYCODE_PAGE_UP) {
			key = "PgUp";  // IntegrationKeyboard.SpecialKeys를 참조한다.
			count = 0;
		}
		else if (keyCode==KeyEvent.KEYCODE_PAGE_DOWN) {
			key = "PgDn";// IntegrationKeyboard.SpecialKeys를 참조한다.
			count = 0;
		}
		else {
			count = 0;
			awtKeyCode = event.awtKeyEvent.getKeyCode();
			if (awtKeyCode==java.awt.event.KeyEvent.VK_LEFT) {
				key = "Left";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (awtKeyCode==java.awt.event.KeyEvent.VK_RIGHT) {
				key = "Right";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (awtKeyCode==java.awt.event.KeyEvent.VK_UP) {
				key = "Up";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (awtKeyCode==java.awt.event.KeyEvent.VK_DOWN) {
				key = "Down";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (awtKeyCode==java.awt.event.KeyEvent.VK_HOME) {
				key = "Home";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (awtKeyCode==java.awt.event.KeyEvent.VK_END) {
				key = "End";// IntegrationKeyboard.SpecialKeys를 참조한다.
			}
			else if (unicode==65535 && !isShiftPressed) { // Window 키, 한자키 등
				key = IntegrationKeyboard.Shift;
				count = 0;
			}
			else {
				if (isCtrlPressed) { // 키보드 가속기
					IntegrationKeyboard.isCtrlPressed = true;
					if (awtKeyCode==java.awt.event.KeyEvent.VK_Z) {//undo
						key = new String("Z");
					}
					else if (awtKeyCode==java.awt.event.KeyEvent.VK_Y) {//redo
						key = new String("Y");
					}
					else if (awtKeyCode==java.awt.event.KeyEvent.VK_F) {//find/replace
						key = new String("F");
					}
					else if (awtKeyCode==java.awt.event.KeyEvent.VK_C) {//copy
						key = new String("C");
					}
					else if (awtKeyCode==java.awt.event.KeyEvent.VK_X) {//cut
						key = new String("X");
					}
					else if (awtKeyCode==java.awt.event.KeyEvent.VK_V) {//paste
						key = new String("V");
					}
					else if (awtKeyCode==java.awt.event.KeyEvent.VK_A) {//select all
						key = new String("A");
					}
				}//if (isCtrlPressed) { // 키보드 가속기
				else if (isAltPressed) { // 키보드 가속기
					IntegrationKeyboard.isAltPressed = true;
				}
				else { // 일반적인 키
					if (isEnglishKeyboard) {
						char[] arg = {c};
						key = new String(arg);
					}
					else {
						key = convertToHangul(c);
					}
				}// 일반적인 키
			}//else
		}//else
	}
	
	/** key 를 만든다.*/
	public HardwareKeyboard(int keyCode, KeyEvent event) {
		this.keyCode = keyCode;
		this.event = event;
		
		if (CommonGUI.keyboard.getHides()==false) {
			CommonGUI.keyboard.setHides(true);
		}
		
		int deviceId = event.getDeviceId();
		if (keyCharacterMap==null) {
			keyCharacterMap = KeyCharacterMap.load(deviceId);
		}
		
		if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
			this.makeKeyForJava(keyCode, event);
		}
		else if (Control.CurrentSystem.equals(Control.CurrentSystemIsAndroid)) {
			this.makeKeyForAndroid(keyCode, event);
		} 
		else {
			this.makeKeyForJava(keyCode, event);
		}
	}
	
	String convertToHangul(char c) {
		switch (c) {
		case 'a': return "ㅁ";
		case 'b': return "ㅠ";
		case 'c': return "ㅊ";
		case 'd': return "ㅇ";
		case 'e': return "ㄷ";
		case 'f': return "ㄹ";
		case 'g': return "ㅎ";
		case 'h': return "ㅗ";
		case 'i': return "ㅑ";
		case 'j': return "ㅓ";
		case 'k': return "ㅏ";
		case 'l': return "ㅣ";
		case 'm': return "ㅡ";
		case 'n': return "ㅜ";
		case 'o': return "ㅐ";
		case 'p': return "ㅔ";
		case 'q': return "ㅂ";
		case 'r': return "ㄱ";
		case 's': return "ㄴ";
		case 't': return "ㅅ";
		case 'u': return "ㅕ";
		case 'v': return "ㅍ";
		case 'w': return "ㅈ";
		case 'x': return "ㅌ";
		case 'y': return "ㅛ";
		case 'z': return "ㅋ";
		
		case 'A': return "ㅁ";
		case 'B': return "ㅠ";
		case 'C': return "ㅁ";
		case 'D': return "ㅇ";
		case 'E': return "ㄸ";
		case 'F': return "ㄹ";
		case 'G': return "ㅎ";
		case 'H': return "ㅗ";
		case 'I': return "ㅑ";
		case 'J': return "ㅓ";
		case 'K': return "ㅏ";
		case 'L': return "ㅣ";
		case 'M': return "ㅡ";
		case 'N': return "ㅜ";
		case 'O': return "ㅒ";
		case 'P': return "ㅖ";
		case 'Q': return "ㅃ";
		case 'R': return "ㄲ";
		case 'S': return "ㄴ";
		case 'T': return "ㅆ";
		case 'U': return "ㅕ";
		case 'V': return "ㅍ";
		case 'W': return "ㅉ";
		case 'X': return "ㅌ";
		case 'Y': return "ㅛ";
		case 'Z': return "ㅋ";			
		}
		char[] r = {c};
		return new String(r);
	}
	
}