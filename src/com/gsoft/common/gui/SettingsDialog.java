package com.gsoft.common.gui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import com.gsoft.common.ColorEx;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.CommonGUI_SettingsDialog;
import com.gsoft.common.IO;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util.BufferByte;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.EditText.Edit;
import com.gsoft.common.gui.EditText.ScrollMode;
import com.gsoft.common.gui.Menu.MenuType;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.common.R.R;

/**ViewEx의 createSettingsDialog(View view)에서 생성되어 
 * CommonGUI_SettingsDialog.settingsDialog에 저장된다.*/
public class SettingsDialog extends Dialog  implements OnTouchListener {
	static int[] colors = {Color.WHITE, Color.RED
	};
	static String[] namesOfColorButtons = {"White", "Red"
	};
	static String[] textesOfColorButtons = {"EditText Color", "Keyboard Color"
	};
	
	float scaleOfGapX = 0.05f;
	
	float scaleOfTitleBar = 0.1f;
	
	// static과 button
	float scaleOfColorButtonsX = (1-scaleOfGapX*3) / 2;
	float scaleOfColorButtonsY = 0.07f;
	
	float scaleOfOKButtonX = (1-scaleOfGapX*3) / 2;
	float scaleOfOKButtonY = 0.07f;
	
	float scaleOfstaticEditTextDirectory = 0.07f;
	
	float scaleOfbuttonTextSaveFormatY = 0.07f;
	
	Static staticButtonTextSaveFormat;
	
	Button buttonTextSaveFormat;
	
	Static staticIsTripleBuffering;
	
	Button buttonIsTripleBuffering;
	
	float scaleOfbuttonIsTripleBuffering = 0.07f;
	
	Static staticEditTextDirectory;
	
	/** 짝수번째는 Static, 홀수번째는 Button*/
	Control[] controlsOfSettings = new Control[namesOfColorButtons.length*2];
	
	ColorDialog colorDialog = CommonGUI.colorDialog;
	//public int[] selectedColor = {Color.WHITE, Color.RED};
	
	
	int indexOfSelectedButton;
	
	/** 뒤에 '/'이 붙는것을 주의한다. 예를 들어 /sdcard/adroid/ */
	public EditText editTextDirectory;	
	Button buttonFileExplorer;
	
	float scaleOfstaticTextSaveFormatX = 0.4f;
	
	float scaleOfEditTextDirectoryX = 0.7f;
	float scaleOfButtonFileExplorerX =  1 - (scaleOfEditTextDirectoryX+scaleOfGapX*3);
	
	// scaleOfstaticEditTextDirectory을 합친 크기, 나중에 실제 높이를 계산할때는 
	// scaleOfstaticEditTextDirectory을 빼준다.
	float scaleOfEditTextDirectoryY = 0.11f + scaleOfstaticEditTextDirectory;
	
	float scaleOfButtonPageControllerY = 0.05f;
	
	float scaleOfButtonPageControllerX = 0.15f;
	
	
	// 나누는 수는 gap 개수
	float scaleOfGapY = (1-(scaleOfTitleBar + scaleOfColorButtonsY*2  +	scaleOfEditTextDirectoryY + 
			scaleOfOKButtonY + scaleOfbuttonTextSaveFormatY + scaleOfbuttonIsTripleBuffering + scaleOfButtonPageControllerY)) / 8;
	
	
	Button buttonPageController;
	
	public static String[] namesOfMenuTextFormat = {"UTF-8", "UTF-16", "MS-949"};
	
	Menu menuTextFormat;
	boolean enablesMenuTextFormat;
	
	/** 텍스트저장형식 utf-8:0, utf-16:1, ms-949(ksc):2이다.*/
	//public int textSaveFormat = 0;
	
	Panel panelControls;
	
	//boolean isTripleBuffering;
	
	/** 클래스캐시를 사용하는지 여부*/
	float scaleOfStaticUsesClassCacheX = this.scaleOfstaticTextSaveFormatX;
	float scaleOfButtonUsesClassCacheX = this.scaleOfstaticTextSaveFormatX;
	float scaleOfUsesClassCacheY = this.scaleOfbuttonTextSaveFormatY;
	
	/** child 프로세스를 사용해서 자바문서를 열면 true(MDI), 
	 * 그게아니라 메인프로세스에서 열면 false(SDI)*/
	float scaleOfStaticUsesChildCompilerProcessX = this.scaleOfstaticTextSaveFormatX;
	float scaleOfButtonUsesChildCompilerProcessX = this.scaleOfstaticTextSaveFormatX;
	float scaleOfUsesChildCompilerProcessY = this.scaleOfbuttonTextSaveFormatY;
	
	
	/** 자주 사용하는 클래스 파일들을 스레드를 사용하여 미리 로드하는지 여부*/
	float scaleOfStaticLoadsClassesFrequentlyUsedAdvancelyX = this.scaleOfstaticTextSaveFormatX;
	float scaleOfButtonLoadsClassesFrequentlyUsedAdvancelyX = this.scaleOfstaticTextSaveFormatX;
	float scaleOfLoadsClassesFrequentlyUsedAdvancelyY = this.scaleOfbuttonTextSaveFormatY;
	
	float scaleOfStaticEnablesScreenKeyboardX = scaleOfStaticLoadsClassesFrequentlyUsedAdvancelyX;
	float scaleOfButtonEnablesScreenKeyboardX = scaleOfButtonLoadsClassesFrequentlyUsedAdvancelyX;
	float scaleOfEnablesScreenKeyboard = scaleOfLoadsClassesFrequentlyUsedAdvancelyY;
	
	float scaleOfStaticEnablesUnzipLibraryX = scaleOfStaticLoadsClassesFrequentlyUsedAdvancelyX;
	float scaleOfButtonEnablesUnzipLibraryX = scaleOfButtonLoadsClassesFrequentlyUsedAdvancelyX;
	float scaleOfEnablesUnzipLibrary = scaleOfLoadsClassesFrequentlyUsedAdvancelyY;
	
	
	float scaleOfGapXBetweenPageControllers = (1-(scaleOfButtonPageControllerX*3)) / 4;
	
	private Button buttonPageController2;
	private Static staticUsesClassCache;
	private Button buttonUsesClassCache;
	private Static staticUsesChildCompilerProcess;
	private Button buttonUsesChildCompilerProcess;
	private Static staticLoadsClassesFrequentlyUsedAdvancely;
	private Button buttonLoadsClassesFrequentlyUsedAdvancely;
	private Static staticEnablesScreenKeyboard;
	private Button buttonEnablesScreenKeyboard;
	private Static staticEnablesUnzipLibrary;
	private Button buttonEnablesUnzipLibrary;
	private Panel panelControls2;
	
	
	private Button buttonPageController3;
	private Static staticShowsCopyRight;
	public Button buttonShowsCopyRight;
	private Panel panelControls3;
	
	
	void setEnablesShowsCopyRight(boolean showsCopyRight) {
		this.buttonShowsCopyRight.Select(showsCopyRight);
		if (showsCopyRight) this.buttonShowsCopyRight.setText("enabled");
		else this.buttonShowsCopyRight.setText("disabled");
	}
	
	void setIsTripleBuffering(boolean isTripleBuffering) {
		this.buttonIsTripleBuffering.Select(isTripleBuffering);
		if (isTripleBuffering) this.buttonIsTripleBuffering.setText("enabled");
		else this.buttonIsTripleBuffering.setText("disabled");
	}
	
	void setUsesClassCache(boolean usesClassCache) {
		this.buttonUsesClassCache.Select(usesClassCache);
		if (usesClassCache) this.buttonUsesClassCache.setText("enabled");
		else this.buttonUsesClassCache.setText("disabled");
	}
	
	void setUsesChildCompilerProcess(boolean usesChildProcess) {
		//this.usesChildCompilerProcess = usesChildProcess;
		this.buttonUsesChildCompilerProcess.Select(usesChildProcess);
		if (usesChildProcess) this.buttonUsesChildCompilerProcess.setText("enabled");
		else this.buttonUsesChildCompilerProcess.setText("disabled");
	}
	
	void setLoadsClassesFrequentlyUsedAdvancely(boolean loadsClassesFrequentlyUsedAdvancely) {
		//this.loadsClassesFrequentlyUsedAdvancely = loadsClassesFrequentlyUsedAdvancely;
		this.buttonLoadsClassesFrequentlyUsedAdvancely.Select(loadsClassesFrequentlyUsedAdvancely);
		if (loadsClassesFrequentlyUsedAdvancely) this.buttonLoadsClassesFrequentlyUsedAdvancely.setText("enabled");
		else this.buttonLoadsClassesFrequentlyUsedAdvancely.setText("disabled");
	}
	
	void setEnablesScreenKeyboard(boolean enablesScreenKeyboard) {
		//this.loadsClassesFrequentlyUsedAdvancely = loadsClassesFrequentlyUsedAdvancely;
		this.buttonEnablesScreenKeyboard.Select(enablesScreenKeyboard);
		if (enablesScreenKeyboard) this.buttonEnablesScreenKeyboard.setText("enabled");
		else this.buttonEnablesScreenKeyboard.setText("disabled");
	}
	
	void setEnablesUnzipLibrary(boolean enablesUnzipLibrary) {
		//this.loadsClassesFrequentlyUsedAdvancely = loadsClassesFrequentlyUsedAdvancely;
		this.buttonEnablesUnzipLibrary.Select(enablesUnzipLibrary);
		if (enablesUnzipLibrary) this.buttonEnablesUnzipLibrary.setText("enabled");
		else this.buttonEnablesUnzipLibrary.setText("disabled");
	}
	
	
	/**ViewEx의 sized()에서 createSettingsDialog(View view)가 호출되어 생성되고 
	 * CommonGUI_SettingsDialog.settingsDialog에 저장된다.*/
	public static SettingsDialog createSettingsDialog(View view) {
		if (CommonGUI_SettingsDialog.settingsDialog!=null) 
			return CommonGUI_SettingsDialog.settingsDialog;
		int width = view.getWidth();
		int height = view.getHeight();
		int w = (int) (width*0.8f);
		int h = (int) (height*0.8f);
		int x = width/2 - w/2;
		int y = height/2 - h/2;
		Rectangle bounds = new Rectangle(x,y,w,h);
		CommonGUI_SettingsDialog.settingsDialog = new SettingsDialog(view, bounds);
		//Control.colorDialog.setOnTouchListener(this);
		CommonGUI_SettingsDialog.settingsDialog.setIsTripleBuffering(CommonGUI_SettingsDialog.settings.isTripleBuffering);
		return CommonGUI_SettingsDialog.settingsDialog;
	}
	
	
	public static class Settings {
		/** 0:backColor, 1:Keyboard Color*/
		public int[] selectedColor = {Color.WHITE, Color.RED};
		/** 디폴트 패스*/
		public String pathAndroid = Control.pathAndroid;
		/** 이전 종료시 finish()를 안하고 종료했으면 0, 했으면 1*/
		public int isFinishingWhenExitingPrevly = 0;
		
		public boolean isTripleBuffering = false;
		
		/** isTripleBuffering이 true이고 윈도우즈에서만 가능하다.*/
		public Config bufferedImageType = Config.RGB_565;
		
		/** usesClassCache가 false이면 클래스 캐시를 지워서 클래스와 소스 등의 자원을 해제한다.*/
		public boolean usesClassCache = false;
		
		/** child 프로세스를 사용해서 자바문서를 열면 true(MDI), 
		 * 그게아니라 메인프로세스에서 열면 false(SDI)*/
		public boolean usesChildCompilerProcess = false;
		
		/** 자주 사용하는 클래스 파일들을 스레드를 사용하여 미리 로드하는지 여부*/
		public boolean loadsClassesFrequentlyUsedAdvancely = false;
		public int textSaveFormat;
		
		/** backup_settings 파일에 없을 경우 디폴트는 800, 700이다. 안드로이드에서는 참조안한다. 윈도우에서만*/
		public int viewWidth = 800;
		/** backup_settings 파일에 없을 경우 디폴트는 800, 700이다.안드로이드에서는 참조안한다. 윈도우에서만*/
		public int viewHeight = 700;
		
		/** 화면 키보드가 가능한지 여부*/
		public boolean EnablesScreenKeyboard = false;
		
		/** 프로그램 시작시 gsoft.zip, project.zip 등을 압축해제할 것인지를 결정한다.*/
		public boolean EnablesUnzipLibrary = true;
		
		public boolean EnablesShowsCopyRight = true;
		
		public Settings() {
			/*if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
				EnablesScreenKeyboard = false;
			}
			else {//안드로이드
				EnablesScreenKeyboard = true;
			}*/
		}
	}
	
	
	void createMenuTextFormat() {
		
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int x, y;
		int width=(int) (viewWidth*0.6f);
		int height=(int) (viewHeight*0.3f);
		x = viewWidth/2-width/2;
		y = viewHeight/2-height/2;
		Rectangle boundsMenuTextSaveFormat = new Rectangle(x,y,width,height);  
		menuTextFormat = new MenuWithClosable("MenuTextSaveFormat", boundsMenuTextSaveFormat, 
				MenuType.Vertical, this, namesOfMenuTextFormat, new Size(3,3), true, this);
	}
	

	public SettingsDialog(Object owner, Rectangle bounds) {
		super(owner, bounds);
		// TODO Auto-generated constructor stub
		this.bounds = bounds;
		heightTitleBar = (int) (bounds.height*scaleOfTitleBar);
		
		
		this.isTitleBarEnable = true;
		this.Text = "Settings";
		
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		int alpha = 255;
		
		this.backColor = Color.CYAN;
		setBackColor(backColor);
		
		Edit.setBackColor(Color.WHITE);
		
		int colorOfButton = ColorEx.darkerOrLighter(Color.WHITE, -100);
		
		int widthOfGapBetweenButtonPageControllers = 
				(int) (bounds.width * this.scaleOfGapXBetweenPageControllers);
		int x, y, width, height;
		
		
		/////////////////////////// Page 1 시작 /////////////////////////
		
		width = (int) (bounds.width * scaleOfButtonPageControllerX);
		height = (int) (bounds.height * scaleOfButtonPageControllerY);
		x = bounds.x + widthOfGapBetweenButtonPageControllers;
		y = bounds.y + heightTitleBar + heightOfGap;
		
		buttonPageController  = new Button(owner, "PageSet 1", "PageSet 1", 
				Color.DKGRAY, new Rectangle(x,y,width,height), false, alpha, true, 0.0f, null, Color.CYAN);
		buttonPageController.setBackColor(Color.DKGRAY);
		buttonPageController.setOnTouchListener(this);
		
		
		
		width = (int) (bounds.width * scaleOfColorButtonsX);
		height = (int) (bounds.height * scaleOfColorButtonsY);
		x = bounds.x;
		y = buttonPageController.bounds.bottom();
		
		
		Rectangle boundsOfColorButtons=null;
		
		int i, j, k=0;
		for (j=0; j<namesOfColorButtons.length; j++) {
			y += heightOfGap;
			x = bounds.x; 
			for (i=0; i<2; i++) {				
				x += widthOfGap;
				boundsOfColorButtons = new Rectangle(x, y,width,height);
				//k = j*1+i;
				if (i%2==0) {
					Static text = new Static(owner, textesOfColorButtons[j], textColor, boundsOfColorButtons);
					controlsOfSettings[k] = text;
				}
				else {
					Button button  = new Button(owner, namesOfColorButtons[j], namesOfColorButtons[j], 
							colors[j], boundsOfColorButtons, false, alpha, true, 0.0f, null, Color.CYAN);
					controlsOfSettings[k] = button;
					button.ColorSelected = Color.DKGRAY;				
					button.setOnTouchListener(this);
				}
				k++;
				x += width;
			}
			y += height;
		}
		
		int widthOfGapOfTextSaveFormat = (int) ((1 - 2 * scaleOfstaticTextSaveFormatX) * bounds.width / 3);
		
		width = (int) (bounds.width * this.scaleOfstaticTextSaveFormatX);
		height = (int) (bounds.height * this.scaleOfbuttonTextSaveFormatY);
		x = bounds.x + widthOfGapOfTextSaveFormat;
		y = boundsOfColorButtons.bottom() + heightOfGap;
		Rectangle boundsOfstaticButtonTextSaveFormat = new Rectangle(x,y,width,height);
		
		this.staticButtonTextSaveFormat = new Static(owner, "Text Save Format", textColor, boundsOfstaticButtonTextSaveFormat);
		
		
		width = (int) (bounds.width * this.scaleOfstaticTextSaveFormatX);
		height = (int) (bounds.height * this.scaleOfbuttonTextSaveFormatY);
		x = boundsOfstaticButtonTextSaveFormat.right() + widthOfGapOfTextSaveFormat;
		y = boundsOfColorButtons.bottom() + heightOfGap;
		Rectangle boundsOfButtonTextSaveFormat = new Rectangle(x,y,width,height);
		
		this.buttonTextSaveFormat  = new Button(owner, "textSaveFormat", "textSaveFormat", 
				Color.WHITE, boundsOfButtonTextSaveFormat, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonTextSaveFormat.setOnTouchListener(this);
		
		this.createMenuTextFormat();
		this.menuTextFormat.setOnTouchListener(this);
		
		
		
		x = bounds.x + widthOfGapOfTextSaveFormat;
		y = boundsOfstaticButtonTextSaveFormat.bottom() + heightOfGap;
		Rectangle boundsOfstaticIsTripleBuffering = new Rectangle(x,y,width,height);
		
		this.staticIsTripleBuffering = new Static(owner, "IsTripleBuffering", textColor, boundsOfstaticIsTripleBuffering);
		
		
		x = boundsOfstaticIsTripleBuffering.right() + widthOfGapOfTextSaveFormat;
		Rectangle boundsOfButtonIsTripleBuffering = new Rectangle(x,y,width,height);
		
		this.buttonIsTripleBuffering  = new Button(owner, "IsTripleBuffering", "enabled", 
				Color.WHITE, boundsOfButtonIsTripleBuffering, true, alpha, true, 0.0f, null, Color.CYAN);
		buttonIsTripleBuffering.toggleable = true;
		buttonIsTripleBuffering.setOnTouchListener(this);
		
		
		
		
		width = (int) (bounds.width * this.scaleOfEditTextDirectoryX);
		height = (int) (bounds.height * this.scaleOfstaticEditTextDirectory);
		x = bounds.x + widthOfGap;
		y = boundsOfstaticIsTripleBuffering.bottom() + heightOfGap;
		Rectangle boundsOfstaticEditTextDirectory = new Rectangle(x,y,width,height);
		
		this.staticEditTextDirectory = new Static(owner, "SDK directory", textColor, boundsOfstaticEditTextDirectory);
		
		
		// staticEditTextDirectory의 영역 바로 아래에 boundsOfEditTextDirectory의 영역을 잡는다.
		width = (int) (bounds.width * this.scaleOfEditTextDirectoryX);
		// 영역의 크기를 scaleOfEditTextDirectoryY의 크기에서 scaleOfstaticEditTextDirectory의 크기를 뺀
		// 크기로 설정한다. 참고) scaleOfGapY을 계산할때 gapY의 개수를 4개로 설정한것과 비교한다.
		height = (int) (bounds.height * (this.scaleOfEditTextDirectoryY-this.scaleOfstaticEditTextDirectory));
		x = bounds.x + widthOfGap;
		y = boundsOfstaticEditTextDirectory.bottom()/* + heightOfGap*/;
		Rectangle boundsOfEditTextDirectory = new Rectangle(x,y,width,height);
		
		width = (int) (bounds.width * this.scaleOfButtonFileExplorerX);
		height = boundsOfEditTextDirectory.height;
		x = boundsOfEditTextDirectory.right() + widthOfGap;
		Rectangle boundsOfButtonFileExplorer = new Rectangle(x,y,width,height);
		
		/*EditText(boolean hasToolbarAndMenuFontSize, boolean isDockingOfToolbarFlexiable, Object owner, 
				String name, RectangleF paramBounds, float fontSize, 
				boolean isSingleLine, CodeString text, ScrollMode scrollMode, int backColor) {*/
		this.editTextDirectory = new EditText(false, false, this, "Directory", boundsOfEditTextDirectory, 
				boundsOfEditTextDirectory.height*0.5f, true, 
				new CodeString("",Color.BLACK), ScrollMode.VScroll, Color.WHITE);
		editTextDirectory.isReadOnly = true;
		
		this.buttonFileExplorer = new Button(owner, "Explorer", "Explorer", 
				colorOfButton, boundsOfButtonFileExplorer, false, alpha, true, 0.0f, null, Color.CYAN);
		buttonFileExplorer.setOnTouchListener(this);
		
		
		
		
		// 이제까지의 컨트롤들을 담는 패널을 만든다.
		x = bounds.x;
		y = this.buttonPageController.bounds.bottom();
		width = bounds.width;
		height = boundsOfEditTextDirectory.bottom() - y;
		
		panelControls = new Panel(new Rectangle(x,y,width,height));
		for (k=0; k<controlsOfSettings.length; k++) {
			panelControls.add(controlsOfSettings[k]);
		}
		panelControls.add(staticButtonTextSaveFormat);
		panelControls.add(buttonTextSaveFormat);
		// 메뉴는 setHides(false)를 하면 스택에 등록되어 자동적으로 그려지므로
		// 넣지 않는다.
		//panelControls.add(menuTextSaveFormat);
		panelControls.add(staticIsTripleBuffering);
		panelControls.add(buttonIsTripleBuffering);
		panelControls.add(staticEditTextDirectory);
		panelControls.add(editTextDirectory);
		panelControls.add(buttonFileExplorer);
		
		/////////////////////////// Page 1 끝 /////////////////////////
		
		
		/////////////////////////// Page 2 시작 /////////////////////////
		
		width = (int) (bounds.width * scaleOfButtonPageControllerX);
		height = (int) (bounds.height * scaleOfButtonPageControllerY);
		x = buttonPageController.bounds.right() + widthOfGapBetweenButtonPageControllers;
		y = bounds.y + heightTitleBar + heightOfGap;
		
		buttonPageController2  = new Button(owner, "PageSet 2", "PageSet 2", 
				Color.DKGRAY, new Rectangle(x,y,width,height), false, alpha, true, 0.0f, null, Color.CYAN);
		buttonPageController2.setBackColor(Color.DKGRAY);
		buttonPageController2.setOnTouchListener(this);
		
		
		width = (int) (bounds.width * this.scaleOfStaticUsesClassCacheX);
		height = (int) (bounds.height * this.scaleOfUsesClassCacheY);
		x = bounds.x + widthOfGapOfTextSaveFormat;
		y = buttonPageController2.bounds.bottom() + heightOfGap;
		
		this.staticUsesClassCache = new Static(owner, "Uses Class Cache", textColor, new Rectangle(x,y,width,height));
		
		
		width = (int) (bounds.width * this.scaleOfstaticTextSaveFormatX);
		height = (int) (bounds.height * this.scaleOfbuttonTextSaveFormatY);
		x = staticUsesClassCache.bounds.right() + widthOfGapOfTextSaveFormat;
		
		this.buttonUsesClassCache  = new Button(owner, "UsesClassCache", "UsesClassCache", 
				Color.WHITE, new Rectangle(x,y,width,height), true, alpha, true, 0.0f, null, Color.CYAN);
		buttonUsesClassCache.toggleable = true;
		buttonUsesClassCache.setOnTouchListener(this);
		
		
		
		
		x = bounds.x + widthOfGapOfTextSaveFormat;
		width = (int) (bounds.width * this.scaleOfStaticUsesChildCompilerProcessX);
		height = (int) (bounds.height * this.scaleOfUsesChildCompilerProcessY);
		y = staticUsesClassCache.bounds.bottom() + heightOfGap;		
		this.staticUsesChildCompilerProcess = new Static(owner, "Uses child process", textColor, new Rectangle(x,y,width,height));
		
		
		x = staticUsesChildCompilerProcess.bounds.right() + widthOfGapOfTextSaveFormat;		
		this.buttonUsesChildCompilerProcess  = new Button(owner, "Uses child process", "Uses child process", 
				Color.WHITE, new Rectangle(x,y,width,height), true, alpha, true, 0.0f, null, Color.CYAN);
		buttonUsesChildCompilerProcess.toggleable = true;
		buttonUsesChildCompilerProcess.setOnTouchListener(this);
		
		
		x = bounds.x + widthOfGapOfTextSaveFormat;
		width = (int) (bounds.width * this.scaleOfStaticLoadsClassesFrequentlyUsedAdvancelyX);
		height = (int) (bounds.height * this.scaleOfLoadsClassesFrequentlyUsedAdvancelyY);
		y = staticUsesChildCompilerProcess.bounds.bottom() + heightOfGap;		
		this.staticLoadsClassesFrequentlyUsedAdvancely = new Static(owner, "Loads classes advancely", textColor, new Rectangle(x,y,width,height));
		
		
		x = staticLoadsClassesFrequentlyUsedAdvancely.bounds.right() + widthOfGapOfTextSaveFormat;		
		this.buttonLoadsClassesFrequentlyUsedAdvancely  = new Button(owner, "LoadsClassesFrequentlyUsedAdvancely", "LoadsClassesFrequentlyUsedAdvancely", 
				Color.WHITE, new Rectangle(x,y,width,height), true, alpha, true, 0.0f, null, Color.CYAN);
		buttonLoadsClassesFrequentlyUsedAdvancely.toggleable = true;
		buttonLoadsClassesFrequentlyUsedAdvancely.setOnTouchListener(this);
		
		
		
		x = bounds.x + widthOfGapOfTextSaveFormat;
		width = (int) (bounds.width * this.scaleOfStaticEnablesScreenKeyboardX);
		height = (int) (bounds.height * this.scaleOfEnablesScreenKeyboard);
		y = staticLoadsClassesFrequentlyUsedAdvancely.bounds.bottom() + heightOfGap;		
		this.staticEnablesScreenKeyboard = new Static(owner, "Enables screen keyboard", textColor, new Rectangle(x,y,width,height));
		
		
		x = staticEnablesScreenKeyboard.bounds.right() + widthOfGapOfTextSaveFormat;		
		this.buttonEnablesScreenKeyboard  = new Button(owner, "EnablesScreenKeyboard", "EnablesScreenKeyboard", 
				Color.WHITE, new Rectangle(x,y,width,height), true, alpha, true, 0.0f, null, Color.CYAN);
		buttonEnablesScreenKeyboard.toggleable = true;
		buttonEnablesScreenKeyboard.setOnTouchListener(this);
		
		
		x = bounds.x + widthOfGapOfTextSaveFormat;
		width = (int) (bounds.width * this.scaleOfStaticEnablesUnzipLibraryX);
		height = (int) (bounds.height * this.scaleOfEnablesUnzipLibrary);
		y = staticEnablesScreenKeyboard.bounds.bottom() + heightOfGap;		
		this.staticEnablesUnzipLibrary = new Static(owner, "Enables Unzipping library", textColor, new Rectangle(x,y,width,height));
		
		
		x = staticEnablesUnzipLibrary.bounds.right() + widthOfGapOfTextSaveFormat;		
		this.buttonEnablesUnzipLibrary  = new Button(owner, "EnablesUnzipLibrary", "EnablesUnzipLibrary", 
				Color.WHITE, new Rectangle(x,y,width,height), true, alpha, true, 0.0f, null, Color.CYAN);
		buttonEnablesUnzipLibrary.toggleable = true;
		buttonEnablesUnzipLibrary.setOnTouchListener(this);
		
		
		// 이제까지의 컨트롤들을 담는 패널을 만든다.
		x = bounds.x;
		y = this.buttonPageController2.bounds.bottom();
		width = bounds.width;
		height = boundsOfEditTextDirectory.bottom() - y;
		
		panelControls2 = new Panel(new Rectangle(x,y,width,height));
		
		panelControls2.add(staticUsesClassCache);
		panelControls2.add(buttonUsesClassCache);
		panelControls2.add(this.staticUsesChildCompilerProcess);
		panelControls2.add(this.buttonUsesChildCompilerProcess);
		panelControls2.add(this.staticLoadsClassesFrequentlyUsedAdvancely);
		panelControls2.add(this.buttonLoadsClassesFrequentlyUsedAdvancely);
		panelControls2.add(this.staticEnablesScreenKeyboard);
		panelControls2.add(this.buttonEnablesScreenKeyboard);
		panelControls2.add(this.staticEnablesUnzipLibrary);
		panelControls2.add(this.buttonEnablesUnzipLibrary);
		
		panelControls2.setIsOpen(false);
		
		
		/////////////////////////// Page 2 끝 /////////////////////////
		
		
		/////////////////////////// Page 3 시작 /////////////////////////
		
		
		width = (int) (bounds.width * scaleOfButtonPageControllerX);
		height = (int) (bounds.height * scaleOfButtonPageControllerY);
		x = buttonPageController2.bounds.right() + widthOfGapBetweenButtonPageControllers;
		y = bounds.y + heightTitleBar + heightOfGap;
		
		buttonPageController3  = new Button(owner, "PageSet 3", "PageSet 3", 
				Color.DKGRAY, new Rectangle(x,y,width,height), false, alpha, true, 0.0f, null, Color.CYAN);
		buttonPageController3.setBackColor(Color.DKGRAY);
		buttonPageController3.setOnTouchListener(this);
		
		
		width = (int) (bounds.width * this.scaleOfStaticUsesClassCacheX);
		height = (int) (bounds.height * this.scaleOfUsesClassCacheY);
		x = bounds.x + widthOfGapOfTextSaveFormat;
		y = buttonPageController3.bounds.bottom() + heightOfGap;
		
		this.staticShowsCopyRight = new Static(owner, "Shows CopyRight", textColor, new Rectangle(x,y,width,height));
		
		
		width = (int) (bounds.width * this.scaleOfstaticTextSaveFormatX);
		height = (int) (bounds.height * this.scaleOfbuttonTextSaveFormatY);
		x = staticShowsCopyRight.bounds.right() + widthOfGapOfTextSaveFormat;
		
		this.buttonShowsCopyRight  = new Button(owner, "ShowsCopyRight", "ShowsCopyRight", 
				Color.WHITE, new Rectangle(x,y,width,height), true, alpha, true, 0.0f, null, Color.CYAN);
		buttonShowsCopyRight.toggleable = true;
		buttonShowsCopyRight.setOnTouchListener(this);
		
		this.setEnablesShowsCopyRight(false/*true*/);
		
		
		// 이제까지의 컨트롤들을 담는 패널을 만든다.
		x = bounds.x;
		y = this.buttonPageController3.bounds.bottom();
		width = bounds.width;
		height = boundsOfEditTextDirectory.bottom() - y;
		
		panelControls3 = new Panel(new Rectangle(x,y,width,height));
		
		panelControls3.add(staticShowsCopyRight);
		panelControls3.add(buttonShowsCopyRight);
		
		panelControls3.setIsOpen(false);
		
		
		/////////////////////////// Page 3 끝 /////////////////////////
		
		
				
		width = (int) (bounds.width * scaleOfOKButtonX);
		height = (int) (bounds.height * scaleOfOKButtonY);
		x = bounds.x + widthOfGap;
		y = boundsOfEditTextDirectory.bottom() + heightOfGap;
		Rectangle boundsOfButtonOK = new Rectangle(x,y,width,height);
		x = boundsOfButtonOK.right() + widthOfGap;
		Rectangle boundsOfButtonCancel = new Rectangle(x,y,width,height);
		
		controls = new Button[2];
		controls[0] = new Button(owner, NameButtonOk, Control.res.getString(R.string.OK), 
				colorOfButton, boundsOfButtonOK, false, alpha, true, 0.0f, null, Color.CYAN);
		controls[1] = new Button(owner, NameButtonCancel, Control.res.getString(R.string.cancel), 
				colorOfButton, boundsOfButtonCancel, false, alpha, true, 0.0f, null, Color.CYAN);
		// 이벤트를 이 클래스에서 직접 처리
		controls[0].setOnTouchListener(this);
		controls[1].setOnTouchListener(this);
	}
	
	/*public void open() {
		super.open(true);		
	}*/
	
	
	public void draw(Canvas canvas)
    {
		if (hides) return;
		synchronized(this) {
			try {
		        super.draw(canvas);
		        /*int i;
		        for (i=0; i<controlsOfSettings.length; i++){
		        	controlsOfSettings[i].draw(canvas);
				}
		        this.staticButtonTextSaveFormat.draw(canvas);
		        this.buttonTextSaveFormat.draw(canvas);
		        
		        staticIsTripleBuffering.draw(canvas);
		        buttonIsTripleBuffering.draw(canvas);
		        
		        staticEditTextDirectory.draw(canvas);
		        this.editTextDirectory.draw(canvas);
		        this.buttonFileExplorer.draw(canvas);*/
		        
		        this.buttonPageController.draw(canvas);
		        this.buttonPageController2.draw(canvas);
		        this.buttonPageController3.draw(canvas);
		        this.panelControls.draw(canvas);
		        this.panelControls2.draw(canvas);
		        this.panelControls3.draw(canvas);
			}catch(Exception e) { 	}
    	}
    }
	
	public void open(OnTouchListener listener, boolean isOpen) {
		// slave 로 동작할때는 settings를 바꾸지 못한다.
		if (isOpen) {
			if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
				if (Control.isMasterOrSlave==false) return;
			}
		}
		super.open(isOpen);
		if (isOpen) {
			boolean isTripleBuffering = CommonGUI_SettingsDialog.settings.isTripleBuffering;
			boolean usesClassCache = CommonGUI_SettingsDialog.settings.usesClassCache;
			boolean usesChildCompilerProcess = CommonGUI_SettingsDialog.settings.usesChildCompilerProcess;
			boolean loadsClassesFrequentlyUsedAdvancely = CommonGUI_SettingsDialog.settings.loadsClassesFrequentlyUsedAdvancely;
			boolean enablesScreenKeyboard = CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard;
			boolean enablesUnzipLibrary = CommonGUI_SettingsDialog.settings.EnablesUnzipLibrary;
			
			this.setIsTripleBuffering(isTripleBuffering);
			this.setUsesClassCache(usesClassCache);
			this.setUsesChildCompilerProcess(usesChildCompilerProcess);
			this.setLoadsClassesFrequentlyUsedAdvancely(loadsClassesFrequentlyUsedAdvancely);
			this.setEnablesScreenKeyboard(enablesScreenKeyboard);
			this.setEnablesUnzipLibrary(enablesUnzipLibrary);
			
		}
	}
	
	public static Settings restoreSettings() {
		Context context = Control.activity.getApplicationContext();
		FileInputStream stream=null;
		BufferedInputStream bis=null;
		String absFilename=null;
		Settings settings = null;
		boolean r = false;
		try {
			//File contextDir = context.getFilesDir();
			absFilename = Control.pathGSoftFiles + File.separator + "backup_settings";
			stream = new FileInputStream(absFilename);
			bis = new BufferedInputStream(stream);
			settings = SettingsDialog.load(bis);
			
			r= true;
		} 
		catch (Exception e) {
			settings = new Settings();
			r=false;
		}
		finally {
			Control.pathAndroid = settings.pathAndroid;
			FileHelper.close(bis);
			FileHelper.close(stream);
			if (!r) {
				if (absFilename!=null) {
					//File file = new File(absFilename);
					//file.delete();
				}
			}
		}
		return settings;
	}
	
	
	
	/** 로드시에는 pathAndroid의 끝에 '/'이 없다.(저장시 끝에 '/'이 없으므로)*/
	public static Settings load(InputStream is) {
		Settings settings = new Settings();
		settings.selectedColor[0] = IO.readInt(is, true);
		if (settings.selectedColor[0]==0) {
			settings.selectedColor[0] = Color.WHITE;
		}
		settings.selectedColor[1] = IO.readInt(is, true);
		if (settings.selectedColor[1]==0) {
			settings.selectedColor[1] = Color.RED;
		}
		//settings.isFinishingWhenExitingPrev = IO.readInt(is);
		try {
			BufferByte bufferByte = IO.readUntilNull(is);
			
			//settings.pathAndroid = IO.readString(is, TextFormat.UTF_8);
			settings.pathAndroid = IO.readStringIncludingNull(bufferByte, TextFormat.UTF_8, true);
			if (settings.pathAndroid==null || settings.pathAndroid.equals("")) {
				settings.pathAndroid = Control.pathAndroid;
			}
			String pathAndroid = settings.pathAndroid;
			if (pathAndroid.length()>1 && pathAndroid.charAt(pathAndroid.length()-1)==File.separator.charAt(0)) {
				settings.pathAndroid = pathAndroid.substring(0, pathAndroid.length()-1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			settings.pathAndroid = Control.pathAndroid;
			e.printStackTrace();
		}
		settings.isTripleBuffering = IO.readBoolean(is);
		settings.usesClassCache = IO.readBoolean(is);
		settings.usesChildCompilerProcess = IO.readBoolean(is);
		
		settings.loadsClassesFrequentlyUsedAdvancely = IO.readBoolean(is);
		settings.EnablesScreenKeyboard = IO.readBoolean(is);
		settings.EnablesUnzipLibrary = IO.readBoolean(is);
		
		return settings;
	}
	
	/** CommonGUI_SettingsDialog.settings 를 저장한다.
	 * 저장시에는 pathAndroid의 끝에 '/'이 없도록 저장한다.*/
	public void save(OutputStream os) {
		Settings settings = CommonGUI_SettingsDialog.settings;
		if (settings.selectedColor[0]==0) {
			settings.selectedColor[0] = Color.WHITE;
		}
		IO.writeInt(os, settings.selectedColor[0], true);
		if (settings.selectedColor[1]==0) {
			settings.selectedColor[1] = Color.WHITE;
		}
		IO.writeInt(os, settings.selectedColor[1], true);
		
		//int v = Control.activity.isFinishing() ? 1 : 0;
		//IO.writeInt(os, v);
		
		String pathAndroid = settings.pathAndroid;
		// 마지막 "/"을 제거한다.
		if (pathAndroid.length()>1 && pathAndroid.charAt(pathAndroid.length()-1)==File.separator.charAt(0)) {
			pathAndroid = pathAndroid.substring(0, pathAndroid.length()-1);
		}
		IO.writeString(os, pathAndroid, TextFormat.UTF_8, true, true);
		
		IO.writeBoolean(os, settings.isTripleBuffering);
		IO.writeBoolean(os, settings.usesClassCache);
		IO.writeBoolean(os, settings.usesChildCompilerProcess);
		
		IO.writeBoolean(os, settings.loadsClassesFrequentlyUsedAdvancely);
		IO.writeBoolean(os, settings.EnablesScreenKeyboard);
		IO.writeBoolean(os, settings.EnablesUnzipLibrary);
		
	}
	
	public void setSelectedColorOfEditText(int selectedColorOfEditText) {
		//this.selectedColor[0] = selectedColorOfEditText;
		Button button = (Button)controlsOfSettings[1]; 
		button.setText(ColorEx.toString(selectedColorOfEditText));
		button.setBackColor(selectedColorOfEditText);
	}
	
	public void setSelectedColorOfKeyboard(int selectedColor) {
		//this.selectedColor[1] = selectedColor;
		Button button = (Button)controlsOfSettings[3]; 
		button.setText(ColorEx.toString(selectedColor));
		button.setBackColor(selectedColor);
	}
		
    
    @Override
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r;
    	int i;
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    	r = super.onTouch(event, scaleFactor);
	    	if (!r) return false;
	    	for (i=0; i<controls.length; i++) {
	    		r = controls[i].onTouch(event, scaleFactor);
	    		if (r) return true;
	    	}
	        /*for (i=0; i<controlsOfSettings.length; i++) {
	        	r = controlsOfSettings[i].onTouch(event, scaleFactor);
	        	if (r) return true;
	        }
	        r = this.buttonTextSaveFormat.onTouch(event, scaleFactor);
	        if (r) return true;
	        r = this.buttonIsTripleBuffering.onTouch(event, scaleFactor);
	        if (r) return true;
	        r = this.editTextDirectory.onTouch(event, scaleFactor);
	        if (r) return true;
	        r = this.buttonFileExplorer.onTouch(event, scaleFactor);
	        if (r) return true;
	        return true;*/
	    	
	    	r = this.buttonPageController.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	
	    	r = this.buttonPageController2.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	
	    	r = this.buttonPageController3.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	
	    	r = this.panelControls.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	
	    	r = this.panelControls2.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	
	    	r = this.panelControls3.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	
	    	return true;
    	}
    	return false;
    }   
    
    public void cancel() {
		OK(false);
		open(false);
    }
    
    /** @param textSaveFormat : -1이면 error, 0이면 utf-8, 1이면 utf-16, 2이면 ms-949*/
    public void setTextSaveFormat(int textFormat) {
    	//this.textSaveFormat = textSaveFormat;
    	if (textFormat==-1) {
    		this.buttonTextSaveFormat.setText("load error");
    	}
    	else {
    		this.buttonTextSaveFormat.setText(namesOfMenuTextFormat[textFormat]);
    	}
    }
    
    public void setEditTextDirectory(String pathAndroid) {
    	this.editTextDirectory.setText(0, new CodeString(pathAndroid, editTextDirectory.textColor));
    }
    
    /** 대화상자에서 OK 버튼을 누르면 호출된다. 
     * SettingsDialog에 있는 컨트롤들의 값으로
     * CommonGUI_SettingsDialog.settings을 설정한다.*/
    public void setSettings() {
    	Settings settings = CommonGUI_SettingsDialog.settings;
		Button colorButton1 = (Button)this.controlsOfSettings[1];
		Button colorButton3 = (Button)this.controlsOfSettings[3];
		settings.selectedColor[0] = colorButton1.backColor;
		settings.selectedColor[1] = colorButton3.backColor;				
						
		settings.pathAndroid = this.editTextDirectory.getText().str;
		settings.isTripleBuffering = this.buttonIsTripleBuffering.isSelected;
		settings.usesClassCache = this.buttonUsesClassCache.isSelected;
		settings.usesChildCompilerProcess = this.buttonUsesChildCompilerProcess.isSelected;
		settings.loadsClassesFrequentlyUsedAdvancely = this.buttonLoadsClassesFrequentlyUsedAdvancely.isSelected;
		settings.EnablesScreenKeyboard = this.buttonEnablesScreenKeyboard.isSelected;
		settings.EnablesUnzipLibrary = this.buttonEnablesUnzipLibrary.isSelected;
		
		// 안드로이드의 경우 isTripleBuffering, usesChildCompilerProcess 등은 false로 한다.
		if (Control.CurrentSystem.equals(Control.CurrentSystemIsAndroid)) {
			settings.isTripleBuffering = false;
			settings.usesClassCache = false;
			settings.usesChildCompilerProcess = false;
		}
    }
    
    
    void saveFile(String filename, TextFormat textFormat) {
    	int i;
    	EditText_Compiler editText = null;
    	for (i=Control.controlStack.count-1; i>=0; i--) {
    		Control control = Control.controlStack.getItem(i);
    		if (control instanceof EditText_Compiler) {
    			editText = (EditText_Compiler) control;
    			break;
    		}
    	}
    	
    	FileOutputStream outputStream = null;
    	BufferedOutputStream output = null;
    	
    	try {
			outputStream = new FileOutputStream(filename);
			output = new BufferedOutputStream(outputStream);
			String str = editText.getText().str;
	    	IO.writeString(output, str, textFormat, false, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	finally {
    		if (output!=null) {
				try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (outputStream!=null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	
    }
    

	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		if (sender instanceof Button) {
			Button button = (Button)sender;
			int i;
			// button들에 대해서만, 즉 static은 아님
			for (i=1; i<controlsOfSettings.length; i+=2) {
				//if (button.name.equals(namesOfColorButtons[i])) {
				if (button.iName==controlsOfSettings[i].iName) {
					indexOfSelectedButton = i;
					CommonGUI.colorDialog.open(this, true);					
					//selectedColor[i/2] = Control.colorDialog.selectedColor;
					return;
				}
			}
			if (button.iName==controls[0].iName) // OK
            { 
				this.setSettings();
				
				// settings 를 여기에서 저장한다. 
				// slave 로 동작할때는 settings 를 저장을 못하게 한다.
				this.backupSettings();
				
				String filename = CommonGUI.fileDialog.curDir+CommonGUI.fileDialog.filename;
		    	TextFormat textFormat = null;
		    	if (menuTextFormat.buttons[0].isSelected) {
		    		textFormat = TextFormat.UTF_8;
		    	}
		    	else if (menuTextFormat.buttons[1].isSelected) {
		    		textFormat = TextFormat.UTF_16;
		    	}
		    	else if (menuTextFormat.buttons[2].isSelected) {
		    		textFormat = TextFormat.MS949_Korean;
		    	}
		    	if (textFormat!=null) {
		    		this.saveFile(filename, textFormat);
		    	}
				
				OK(true);
            	open(false);
            	callTouchListener(this, null);
            	
            	ThreadAutoTerminating thread = new ThreadAutoTerminating(this);
            	thread.start();
            	
            	
            }
            else if (button.iName==controls[1].iName) // Cancel
            {
            	cancel();
            }
            else if (button.iName==this.buttonFileExplorer.iName) {
            	CommonGUI.fileDialog.isFullScreen = true;
            	CommonGUI.fileDialog.canSelectFileType = false;
            	CommonGUI.fileDialog.isForViewing = true;
            	CommonGUI.fileDialog.setScaleValues();
            	CommonGUI.fileDialog.changeBounds(new Rectangle(0,0,view.getWidth(),view.getHeight()));
            	CommonGUI.fileDialog.createAndSetFileListButtons(CommonGUI.fileDialog.curDir, FileDialog.Category.All);
				//integrationKeyboard.setHides(true);
            	CommonGUI.fileDialog.open(this, "Set SDK Directory");
            	CommonGUI.fileDialog.setOnTouchListener(this);
            }
            else if (button.iName==this.buttonTextSaveFormat.iName) {
            	if (enablesMenuTextFormat) {
            		this.menuTextFormat.open(true);
            	}
            }
            else if (button.iName==this.buttonIsTripleBuffering.iName) {
            	this.setIsTripleBuffering(button.isSelected);
            }
            else if (button.iName==this.buttonPageController.iName) {
            	this.panelControls.setIsOpen(true);
            	this.panelControls2.setIsOpen(false);            	
            	this.panelControls3.setIsOpen(false);
            }
            else if (button.iName==this.buttonPageController2.iName) {
            	this.panelControls.setIsOpen(false);
            	this.panelControls2.setIsOpen(true);
            	this.panelControls3.setIsOpen(false);
            }
            else if (button.iName==this.buttonPageController3.iName) {
            	this.panelControls.setIsOpen(false);
            	this.panelControls2.setIsOpen(false);
            	this.panelControls3.setIsOpen(true);
            }
            else if (button.iName==this.buttonUsesClassCache.iName) {
            	this.setUsesClassCache(button.isSelected);
            }
            else if (button.iName==this.buttonUsesChildCompilerProcess.iName) {
            	this.setUsesChildCompilerProcess(button.isSelected);
            }
            else if (button.iName==this.buttonLoadsClassesFrequentlyUsedAdvancely.iName) {
            	this.setLoadsClassesFrequentlyUsedAdvancely(button.isSelected);
            }
            else if (button.iName==this.buttonEnablesScreenKeyboard.iName) {
            	this.setEnablesScreenKeyboard(button.isSelected);
            }
            else if (button.iName==this.buttonEnablesUnzipLibrary.iName) {
            	this.setEnablesUnzipLibrary(button.isSelected);
            }
            else if (button.iName==this.buttonShowsCopyRight.iName) {
            	this.setEnablesShowsCopyRight(button.isSelected);
            }
            else {
            	if (button.iName==this.menuTextFormat.buttons[0].iName) { // UTF-8
            		this.setTextSaveFormat(0);
            		menuTextFormat.open(false);
            	}
            	else if (button.iName==this.menuTextFormat.buttons[1].iName) { // UTF-16
            		this.setTextSaveFormat(1);
            		menuTextFormat.open(false);
            		
            	}
            	else if (button.iName==this.menuTextFormat.buttons[2].iName) { // MS-949
            		this.setTextSaveFormat(2);
            		menuTextFormat.open(false);
            	}
            }
		}//if (sender instanceof Button) {
		else if (sender instanceof ColorDialog) {
			ColorDialog colorDialog = (ColorDialog)sender;
			//selectedColor[indexOfSelectedButton/2] = colorDialog.selectedColor;
			String strSelectedColor = CommonGUI.colorDialog.strSelectedColor; 
			Button button = (Button)controlsOfSettings[indexOfSelectedButton];
			button.setBackColor(colorDialog.selectedColor);
			button.setText(strSelectedColor);
			
		}
		else if (sender instanceof FileDialog) {
			FileDialog fileDialog = (FileDialog)sender;
			if (fileDialog.isOK) {
				String dir = fileDialog.buttonDir.text;
				this.editTextDirectory.setText(0, new CodeString(dir,Color.BLACK));
			}
		}
		
	}
	
	/** CommonGUI_SettingsDialog.settings 를 저장한다.
	 * 저장시에는 pathAndroid의 끝에 '/'이 없도록 저장한다.*/
	public void backupSettings() {
		Context context = view.getContext();
		FileOutputStream stream=null;
		BufferedOutputStream bos=null;
		boolean r = false;
		String absFilename=null;
		try {
			//File contextDir = context.getFilesDir();
			absFilename = Control.pathGSoftFiles + File.separator + "backup_settings";
			stream = new FileOutputStream(absFilename);
			bos = new BufferedOutputStream(stream/*, IO.DefaultBufferSize*/);
			CommonGUI_SettingsDialog.settingsDialog.save(bos);
			
			//IO.writeInt(bos, v);
			r = true;
		} catch (FileNotFoundException e) {
			r= false;
		}
		catch (Exception e) {
			r = false;
		}
		finally {
			FileHelper.close(bos);
			FileHelper.close(stream);
			if (!r) {
				if (absFilename!=null) {
					File file = new File(absFilename);
					file.delete();
				}
			}
		}
	}
	
	static class ThreadAutoTerminating extends Thread {
		SettingsDialog owner;
		ThreadAutoTerminating(SettingsDialog owner) {
			this.owner = owner;
		}
		public void run() {
			CommonGUI.loggingForMessageBox.setText(true, "Terminating autoly after 3 seconds..",  false);
        	CommonGUI.loggingForMessageBox.setHides(false);
        	Control.view.postInvalidate();
        	
        	try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        	
        	
        	Control.exit(false);
		}
	}

}