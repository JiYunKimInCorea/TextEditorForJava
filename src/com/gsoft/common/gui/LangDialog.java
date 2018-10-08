package com.gsoft.common.gui;

import com.gsoft.common.ColorEx;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.common.R.R;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;


public class LangDialog extends Dialog implements OnTouchListener {
	enum Mode {
		Hiragana,
		Katakana,
		Radical,		// 한자의 부수
		Hanja,
		Latin2
	}
	Mode mode;
	//IntegrationKeyboard keyboard = Control.keyboard;
	String charA;
		
	String curChar; // 커서의 현재 선택된 문자
	
	public EditText editTextLang;
	Button buttonHiragana;
	Button buttonKatakana;
	Button buttonRadical;
	Button buttonLatin2;
	
	float scaleOfGapX = 0.02f;
	float scaleOfGapY = 0.02f;
	float scaleOfeditTextLangX = 1-scaleOfGapX*2;
	float scaleOfeditTextLangY = 0.7f;
		
	float scaleOfLangButtonX = (1-scaleOfGapX*5) / 4;
	float scaleOfLangButtonY = (1-scaleOfeditTextLangY-scaleOfGapY*4) / 2;
	
	float scaleOfOKButtonX = (1-scaleOfGapX*3) / 2;
	float scaleOfOKButtonY = scaleOfLangButtonY;
	
	String textHiragana;
	String textKatakana;
	String textRadical;
	String[] textHanja;
	String textLatin2;
	private IntegrationKeyboard keyboard = CommonGUI.keyboard;

	public LangDialog(View owner, Rectangle bounds) {
		super(owner, bounds);
		// TODO Auto-generated constructor stub
		this.bounds = bounds;
		
		this.name = "LangDialog";
		this.isTitleBarEnable = false;
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		int alpha = 255;
		
		
		int colorOfButton = ColorEx.darkerOrLighter(Color.WHITE, -100);
		
		int buttonWidth = (int) (bounds.width * scaleOfLangButtonX);
		int buttonHeight = (int) (bounds.height * scaleOfLangButtonY);
		int x = bounds.x + widthOfGap;
		int y = bounds.y + heightOfGap;
		Rectangle boundsOfLangButton = new Rectangle(x,y,buttonWidth,buttonHeight);
		buttonHiragana = new Button(owner, "Hiragana", "Hiragana", 
					colorOfButton, boundsOfLangButton, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonHiragana.setOnTouchListener(this);
		
		x = boundsOfLangButton.right() + widthOfGap;
		boundsOfLangButton.x = x;
		buttonKatakana = new Button(owner, "Katakana", "Katakana", 
					colorOfButton, boundsOfLangButton, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonKatakana.setOnTouchListener(this);
		
		x = boundsOfLangButton.right() + widthOfGap;
		boundsOfLangButton.x = x;
		buttonRadical = new Button(owner, "Radical", Control.res.getString(R.string.lang_chinese), 
					colorOfButton, boundsOfLangButton, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonRadical.setOnTouchListener(this);
		
		x = boundsOfLangButton.right() + widthOfGap;
		boundsOfLangButton.x = x;
		buttonLatin2 = new Button(owner, "Latin2", "Latin2", 
					colorOfButton, boundsOfLangButton, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonLatin2.setOnTouchListener(this);
		
		Rectangle boundsOfEditText = new Rectangle(bounds.x+widthOfGap, 
				boundsOfLangButton.bottom()+heightOfGap, 
				(int)(bounds.width*scaleOfeditTextLangX), (int)(bounds.height*scaleOfeditTextLangY));
		editTextLang = new EditText(true, true, owner, "EditTextLang", boundsOfEditText, 
				boundsOfEditText.height*0.22f, false, 
				new CodeString("", Color.BLACK), 
				EditText.ScrollMode.Both, Color.WHITE);
		editTextLang.setIsSingleLine(false);
		editTextLang.isReadOnly = true;
				
		buttonWidth = (int) (bounds.width * scaleOfOKButtonX);
		buttonHeight = (int) (bounds.height * scaleOfOKButtonY);
		x = bounds.x + widthOfGap;
		y = (int) (boundsOfEditText.bottom() + heightOfGap);
		Rectangle boundsOfButtonOK = new Rectangle(x,y,buttonWidth,buttonHeight);
		x = (int) (boundsOfButtonOK.right() + widthOfGap);
		Rectangle boundsOfButtonCancel = new Rectangle(x,y,buttonWidth,buttonHeight);
		
		controls = new Button[2];
		controls[0] = new Button(owner, NameButtonOk, Control.res.getString(R.string.OK), 
				colorOfButton, boundsOfButtonOK, false, alpha, true, 0.0f, null, Color.CYAN);
		controls[1] = new Button(owner, NameButtonCancel, Control.res.getString(R.string.cancel), 
				colorOfButton, boundsOfButtonCancel, false, alpha, true, 0.0f, null, Color.CYAN);
		// 이벤트를 이 클래스에서 직접 처리
		controls[0].setOnTouchListener(this);
		controls[1].setOnTouchListener(this);
		
		textHanja = new String[IntegrationKeyboard.부수.length];
		
	}
	
	public void changeBounds(Rectangle bounds) {
		this.bounds = bounds;
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		
		int buttonWidth = (int) (bounds.width * scaleOfLangButtonX);
		int buttonHeight = (int) (bounds.height * scaleOfLangButtonY);
		int x = bounds.x + widthOfGap;
		int y = bounds.y + heightOfGap;
		Rectangle boundsOfLangButton = new Rectangle(x,y,buttonWidth,buttonHeight);
		buttonHiragana.changeBounds(boundsOfLangButton);
		
		x = boundsOfLangButton.right() + widthOfGap;
		boundsOfLangButton = new Rectangle(x,y,buttonWidth,buttonHeight);
		buttonKatakana.changeBounds(boundsOfLangButton);
		
		x = boundsOfLangButton.right() + widthOfGap;
		boundsOfLangButton = new Rectangle(x,y,buttonWidth,buttonHeight);
		buttonRadical.changeBounds(boundsOfLangButton);
		
		x = boundsOfLangButton.right() + widthOfGap;
		boundsOfLangButton = new Rectangle(x,y,buttonWidth,buttonHeight);
		buttonLatin2.changeBounds(boundsOfLangButton);
		
		Rectangle boundsOfEditText = new Rectangle(bounds.x+widthOfGap, 
				boundsOfLangButton.bottom()+heightOfGap, 
				(int)(bounds.width*scaleOfeditTextLangX), (int)(bounds.height*scaleOfeditTextLangY));
		boundsOfEditText.x = editTextLang.bounds.x;
		//boundsOfEditText.width = editTextLang.bounds.width;
		editTextLang.changeBounds(boundsOfEditText);
				
		buttonWidth = (int) (bounds.width * scaleOfOKButtonX);
		buttonHeight = (int) (bounds.height * scaleOfOKButtonY);
		x = bounds.x + widthOfGap;
		y = boundsOfEditText.bottom() + heightOfGap;
		Rectangle boundsOfButtonOK = new Rectangle(x,y,buttonWidth,buttonHeight);		
		((Button)controls[0]).changeBounds(boundsOfButtonOK);
		
		x = boundsOfButtonOK.right() + widthOfGap;
		Rectangle boundsOfButtonCancel = new Rectangle(x,y,buttonWidth,buttonHeight);
		((Button)controls[1]).changeBounds(boundsOfButtonCancel);
	}
	
	public void open() {
		//editTextLang.backUp();
		super.open(true);
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
		char[] text;
		char i;
		char start, end;
				
		if (mode==Mode.Hiragana) {
			if (textHiragana==null) {
				start = (char) IntegrationKeyboard.startHiragana;
				end = (char) IntegrationKeyboard.endHiragana;
				int count=0, len = end-start+1;
				text = new char[len+len/16];
				int index;
				for (i=start, index=0; i<=end; i++, index++) {
					text[index] = i;
					if (++count==16) {
						count = 0;
						text[++index] = '\n';
					}
				}
				editTextLang.initialize();
				editTextLang.setText(0, new CodeString(textHiragana = new String(text), editTextLang.textColor));
				editTextLang.initCursorAndScrollPos();
			}
			else {
				editTextLang.initialize();
				editTextLang.setText(0, new CodeString(textHiragana, editTextLang.textColor));
				editTextLang.initCursorAndScrollPos();
			}
		}
		else if (mode==Mode.Katakana) {
			if (textKatakana==null) {
				start = (char) IntegrationKeyboard.startKatakana;
				end = (char) IntegrationKeyboard.endKatakana;
				int count=0, len = end-start+1;
				text = new char[len+len/16];
				int index;
				for (i=start, index=0; i<=end; i++, index++) {
					text[index] = i;
					if (++count==16) {
						count = 0;
						text[++index] = '\n';
					}
				}
				editTextLang.initialize();
				editTextLang.setText(0, new CodeString(textKatakana = new String(text), editTextLang.textColor));
				editTextLang.initCursorAndScrollPos();
			}
			else {
				editTextLang.initialize();
				editTextLang.setText(0, new CodeString(textKatakana, editTextLang.textColor));
				editTextLang.initCursorAndScrollPos();
			}
		} 
		else if (mode==Mode.Radical) {
			if (textRadical==null) {
				editTextLang.initialize();
				editTextLang.setText(0, new CodeString(textRadical = new String(IntegrationKeyboard.부수), editTextLang.textColor));
				editTextLang.initCursorAndScrollPos();
			}
			else {
				editTextLang.initialize();
				editTextLang.setText(0, new CodeString(textRadical, editTextLang.textColor));
				editTextLang.initCursorAndScrollPos();
			}
		}
		else if (mode==Mode.Hanja) {
			if (curChar!=null) {
				int indexOf부수 = find(IntegrationKeyboard.부수, 
						curChar.toCharArray()[0]);
				if (indexOf부수==-1) return;
				if (textHanja[indexOf부수]==null) {
					if (indexOf부수<IntegrationKeyboard.부수.length-1) {
						start = IntegrationKeyboard.부수[indexOf부수];
						end = IntegrationKeyboard.부수[indexOf부수+1];
					}
					else if (indexOf부수==IntegrationKeyboard.부수.length-1) {					
						start = IntegrationKeyboard.부수[indexOf부수];
						end = (char) (IntegrationKeyboard.endOf부수+1);
					}
					else {
						start = 0;
						end = 0;
					}
					if (start!=0 && end!=0) {
						int count=0, len = end-start;
						int index;
						text = new char[len+len/16];
						for (i=start, index=0; i<end; i++, index++) {
							text[index] = i;
							if (++count==16) {
								count = 0;
								text[++index] = '\n';
							}
						}
						editTextLang.initialize();
						textHanja[indexOf부수] = new String(text);
						editTextLang.setText(0, new CodeString(textHanja[indexOf부수], editTextLang.textColor));
						editTextLang.initCursorAndScrollPos();
					}
				}
				else {
					editTextLang.initialize();
					editTextLang.setText(0, new CodeString(textHanja[indexOf부수], editTextLang.textColor));
					editTextLang.initCursorAndScrollPos();
				}
			}
			
		}
		else if (mode==Mode.Latin2) {
			if (textLatin2==null) {
				start = (char) IntegrationKeyboard.startLatin2;
				end = (char) IntegrationKeyboard.endLatin2;
				int count=0, len = end-start+1;
				text = new char[len+len/16];
				int index;
				for (i=start, index=0; i<=end; i++, index++) {
					text[index] = i;
					if (++count==16) {
						count = 0;
						text[++index] = '\n';
					}
				}
				editTextLang.initialize();
				editTextLang.setText(0, new CodeString(textLatin2 = new String(text), editTextLang.textColor));
				editTextLang.initCursorAndScrollPos();
			}
			else {
				editTextLang.initialize();
				editTextLang.setText(0, new CodeString(textLatin2, editTextLang.textColor));
				editTextLang.initCursorAndScrollPos();
			}
		}
	}
	
	int find(char[] arr, char c) {
		int i;
		for (i=0; i<arr.length; i++) {
			if (arr[i]==c) return i;
		}
		return -1;
	}
	
	public void draw(Canvas canvas)
    {
		if (hides) return;
		synchronized(this) {
        super.draw(canvas);
        buttonHiragana.draw(canvas);
        buttonKatakana.draw(canvas);
        buttonRadical.draw(canvas);
        buttonLatin2.draw(canvas);
        editTextLang.draw(canvas);
		}
    }
		
    
    @Override
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r = false;
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    	r= super.onTouch(event, scaleFactor);
	    	if (!r) {
	    		return false;
	    	}
	    	r = buttonHiragana.onTouch(event, scaleFactor);
	    	if (!r) r = buttonKatakana.onTouch(event, scaleFactor);
	    	if (!r) r = buttonRadical.onTouch(event, scaleFactor);
	    	if (!r) r = buttonLatin2.onTouch(event, scaleFactor);
	    	if (!r) {
	    		r = editTextLang.onTouch(event, scaleFactor);
	    	}
	    	int i;
    		for (i=0; i<controls.length; i++) {
    			r = controls[i].onTouch(event, scaleFactor);
    			if (r) return true;
    		}
	    	return true;
    	}
    	/*else if (event.actionCode==MotionEvent.ActionMove && editTextLang==capturedControl) {
    		editTextLang.onTouch(event, scaleFactor);
    		return true;
    	}*/
    	return true;
    }   
    
    public void cancel() {
    	OK(false);
    	open(false);
    	keyboard.hides = false;
    }
    
	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		
		try{
		if (sender instanceof Button) {
			Button button = (Button)sender;
			if (button.iName==controls[0].iName) // OK
            {             
				if (mode!=Mode.Radical) {
					curChar = editTextLang.getCurChar();
					if (curChar!=null && curChar.equals("\n")==false) {
						// 문자 입력일때만 call back으로 호출한다.
						keyboard .onTouchEvent(this, null);						
					}
										
					OK(true);
					open(false);
					keyboard.hides = false;					
				}
				else {
					curChar = editTextLang.getCurChar();
					setMode(Mode.Hanja);					
					
				}
				//editTextLang.restore();				
            }
            else if (button.iName==controls[1].iName) // Cancel
            {
            	cancel();
            	//editTextLang.restore();
            }
            else if (button.iName==buttonHiragana.iName)
            {
            	setMode(LangDialog.Mode.Hiragana);            
            }
            else if (button.iName==buttonKatakana.iName)
            {
            	setMode(LangDialog.Mode.Katakana);
            }
            else if (button.iName==buttonRadical.iName)
            {
            	setMode(LangDialog.Mode.Radical);            
            }
            else if (button.iName==buttonLatin2.iName)
            {
            	setMode(LangDialog.Mode.Latin2);            
            }
			
		}
		}catch(Exception e1) {
    		
    	}
	}

}