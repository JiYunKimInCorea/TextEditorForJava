package com.gsoft.common.gui;

import java.io.File;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.gsoft.common.ColorEx;
import com.gsoft.common.Compiler;
import com.gsoft.common.Events;
import com.gsoft.common.IO;
import com.gsoft.common.Media;
import com.gsoft.common.Net;
import com.gsoft.common.Sizing;
import com.gsoft.common.Util;
import com.gsoft.common.interfaces;
import com.gsoft.common.Compiler.CodeString;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.Media.MediaPlayerDel;
import com.gsoft.common.Net.WifiThread;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.PoolOfButton;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Buttons.ButtonGroup;
import com.gsoft.common.gui.Dialog.EditableDialog;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.texteditor13.R;

public class FileDialog_test extends EditableDialog implements OnTouchListener {
	//IntegrationKeyboard keyboard = Control.keyboard;
	String charA;
	
	String[] fileList;
	String[] oldFileList;
	public EditText editText;
	public String filename;
	
	public MenuWithScrollBar menuFileList;
	
	Button buttonDir;
	Button buttonCategory;
	Button buttonFunc;
	Button buttonMultimedia;
	
	Button buttonOnOff;
	
	
		
	
	float scaleOfTitleBarY = 0.13f;
	
	
	
	// 버튼:4개, gapX:5개, Dir버튼:나머지버튼들의 4배, gapX는 동일크기이고 나머지 버튼의 0.1, 
	// 나머지 버튼 : x
	// 4x(dir) + 3x + 0.1*x*5 = 1, 7.5x = 1, x= 1/7.5
	
	float scaleOfCategoryX = 1 / 7.5f;
	float scaleOfCategoryY = 0.13f;
	
	float scaleOfDirX = scaleOfCategoryX * 4;
	float scaleOfDirY = 0.13f;
	
	float scaleOfGapX = scaleOfCategoryX * 0.1f;
	
	float scaleOfMenuFileListX = 1-scaleOfGapX*2;
	float scaleOfMenuFileListY = 0.3f;
	
	float scaleOfeditTextX = 1-scaleOfGapX*2;
	float scaleOfeditTextY = 0.18f;
		
	float scaleOfOKButtonX = (1-scaleOfGapX*3) / 2;
	float scaleOfOKButtonY = 0.13f;
	
	// 5는 gap개수
	float scaleOfGapY = (1-(scaleOfTitleBarY+scaleOfMenuFileListY+scaleOfDirY+
			scaleOfeditTextY+scaleOfOKButtonY)) / 5;
	
	//private boolean WasKeyboardHiddenBeforeOpen;
	private RectangleF boundsOfMenuFileList;
	OnTouchListener oldKeyboardListener;
	
	private boolean isForReadingOrSaving;
	
	/*public enum Dir {
		Context, Asset, CurDir
	};*/	
	
	public enum Category {
		Image, Text, Custom, Music, Video, All
	};
	
	//Dir dir = Dir.Context;	
	
	public String curDir = "/";
	Category category = Category.Custom;
	public static String Separator = "/";
	
	public static String[] namesOfMenuDir = {Control.res.getString(R.string.file_dir_app),
		Control.res.getString(R.string.file_dir_root)};
	
	public static String[] namesOfMenuFunc = {/*"Make Dir", "Delete", "Rename", 
		"Cut", "Copy", "Paste", "MultiSelect", "Sort", "Properties"*/
		Control.res.getString(R.string.file_func_mkdir), Control.res.getString(R.string.file_func_del),
		Control.res.getString(R.string.file_func_ren), Control.res.getString(R.string.file_func_cut),
		Control.res.getString(R.string.file_func_copy), Control.res.getString(R.string.file_func_paste),
		Control.res.getString(R.string.file_func_multiselect), Control.res.getString(R.string.file_func_sort),
		"Storage", Control.res.getString(R.string.file_func_properties)};
	
	public static String[] namesOfMenuMultimedia = {
		/*"Send a file(s)", "Start receiver", 
		"Listen to music", "Watch video", "Record sound", "Play record", 
		"Sound Control Menu"*/
		Control.res.getString(R.string.file_media_send), Control.res.getString(R.string.file_media_receiver),
		Control.res.getString(R.string.file_media_music), Control.res.getString(R.string.file_media_video),
		"Install a package",
		Control.res.getString(R.string.file_media_record), Control.res.getString(R.string.file_media_play_record),
		Control.res.getString(R.string.file_media_sound_menu)};
	
	public static String[] namesOfMenuFileType = {"Image", "Text", "Custom", "Music", "Video", "All"};
	
	public static String[] namesOfMenuSort = {/*"Sort by name, ascending", "Sort by name, descending",
		"Sort by time, ascending", "Sort by time, descending"*/
		Control.res.getString(R.string.file_sort_name_ascend), Control.res.getString(R.string.file_sort_name_descend),
		Control.res.getString(R.string.file_sort_time_ascend), Control.res.getString(R.string.file_sort_time_descend)};
	
	static class SortByTime {
		long modifiedTime;
		String filename;
		SortByTime(long modifiedTime, String filename) {
			this.modifiedTime = modifiedTime;
			this.filename = filename;
		}
	}
	
	
	
	public enum State {
		Normal,
		Delete,
		Rename,
		Cut, Copy, Paste,
		SendFile, MultiSelect 
	}
	
	State state = State.Normal;
	
	MessageDialog messageDialog;
	
	String mAbsFilename;
	
	ConnectDialog connectDialog;
	
	public boolean canSelectFileType;
	public boolean isForViewing;
	
	PoolOfButton poolOfFileListButtons;
	Button[] fileListButtons;
	boolean isDirectoryOrFileForCutOrCopy;
	boolean isCutOrCopy;
	
	WifiThread wifiThread;
	//SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	//SoundPlayer soundPlayer;
	public MediaPlayerDel mediaPlayer = null;
	public boolean isFullScreen;
	public void setIsForReadingOrSaving(boolean isForReadingOrSaving) {
		this.isForReadingOrSaving = isForReadingOrSaving;
		if (isForReadingOrSaving) {
			editText.isReadOnly = false;
			//Text = title + " - Load";
		}
		else { 
			editText.isReadOnly = false;
			//Text = title + " - Save";
		}
	}
	
	public FileDialog_test(/*boolean isFullScreen, boolean canSelectFileType, */Object owner, RectangleF bounds, String[] fileList) {
		super(owner, bounds);
		
		//createWifiDir();
		
		try {
			/*try {
				if (view==null)
					view = getView(owner);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("FileDialog-FileDialog",e.toString());
				return;
			}*/
		//this.bounds = bounds;
			//Rectangle boundsRect = bounds.toRectangle();
	        createDrawingBuffer(view, true, null);
	        
		this.fileList = fileList;
		oldFileList = fileList;
		
		this.name = "FileDialog";
		this.isTitleBarEnable = true;
		//this.title = Control.res.getString(R.string.file_explorer_dialog); 
		this.Text = "FileExplorer";
		
		heightTitleBar = (int) (bounds.height * scaleOfTitleBarY);
		
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		int alpha = 255;
		
		int colorOfButton = ColorEx.darkerOrLighter(Color.WHITE, -100);
		
			
		// 영역만 잡아놓고 나중에 Button[]을 넣어준다.
		boundsOfMenuFileList = new RectangleF(bounds.x+widthOfGap, 
				bounds.y+heightTitleBar+heightOfGap, 
				bounds.width*scaleOfMenuFileListX, bounds.height*scaleOfMenuFileListY);
		//createMenuFileList(null, null);
		
		//createPoolOfFileListButtons();
		
				
		RectangleF boundsOfEditText = new RectangleF(bounds.x+widthOfGap, 
				boundsOfMenuFileList.bottom()+heightOfGap, 
				bounds.width*scaleOfeditTextX, bounds.height*scaleOfeditTextY);
		editText = new EditText(false, false, this, "SaveFileEditText", boundsOfEditText, 
				boundsOfEditText.height*0.5f, true, 
				new CodeString("", Color.BLACK), 
				EditText.ScrollMode.VScroll, Color.WHITE);
		//oldListener = keyboard.setOnTouchListener(editText);
		
		float buttonWidth = bounds.width * scaleOfDirX;
		float buttonHeight = bounds.height * scaleOfDirY;
		float x = bounds.x + widthOfGap;
		float y = boundsOfEditText.bottom() + heightOfGap;
		RectangleF boundsOfDir = new RectangleF(x,y,buttonWidth,buttonHeight);
		
		buttonWidth = bounds.width * scaleOfCategoryX;
		buttonHeight = bounds.height * scaleOfCategoryY;
		x = bounds.x + widthOfGap;
		y = boundsOfEditText.bottom() + heightOfGap;		
		RectangleF boundsOfCategory = new RectangleF(boundsOfDir.right()+widthOfGap,y,
				buttonWidth,buttonHeight);
		
		RectangleF boundsOfFunc = new RectangleF(boundsOfCategory.right()+widthOfGap,y,
				buttonWidth,buttonHeight);
		
		RectangleF boundsOfMultimedia = new RectangleF(boundsOfFunc.right()+widthOfGap,y,
				buttonWidth,buttonHeight);
		
		buttonWidth = boundsOfCategory.width;
		buttonHeight = heightTitleBar;
		RectangleF boundsOfOnOff = new RectangleF(bounds.x+bounds.width-buttonWidth, bounds.y,
				buttonWidth,buttonHeight);
		
		
		buttonDir = new Button(owner, "", Control.res.getString(R.string.file_but_dir), 
				colorOfButton, boundsOfDir, false, alpha, false, 0);
		buttonCategory = new Button(owner, "", Control.res.getString(R.string.file_but_category), 
				colorOfButton, boundsOfCategory, false, alpha, false, 0);		
		buttonFunc = new Button(owner, "", Control.res.getString(R.string.file_but_func), 
				colorOfButton, boundsOfFunc, false, alpha, false, 0);
		buttonMultimedia = new Button(owner, "", Control.res.getString(R.string.file_but_media), 
				colorOfButton, boundsOfMultimedia, false, alpha, false, 0);
		
		buttonOnOff = new Button(owner, "", "On/Off", 
				colorOfButton, boundsOfOnOff, false, alpha, false, 0);
		
		buttonOnOff.selectable = true;	// OnOff 버튼은 토글로 동작한다.
		buttonOnOff.toggleable = true;
		buttonOnOff.ColorSelected = Color.YELLOW;
		buttonOnOff.isSelected = true;
		
		buttonDir.setOnTouchListener(this);
		buttonCategory.setOnTouchListener(this);
		buttonFunc.setOnTouchListener(this);
		buttonMultimedia.setOnTouchListener(this);
		
		buttonOnOff.setOnTouchListener(this);
		
		buttonWidth = bounds.width * scaleOfOKButtonX;
		buttonHeight = bounds.height * scaleOfOKButtonY;
		x = bounds.x + widthOfGap;
		y = boundsOfDir.bottom() + heightOfGap;
		RectangleF boundsOfButtonOK = new RectangleF(x,y,buttonWidth,buttonHeight);
		x = boundsOfButtonOK.right() + widthOfGap;
		RectangleF boundsOfButtonCancel = new RectangleF(x,y,buttonWidth,buttonHeight);		
		
		controls = new Button[2];
		controls[0] = new Button(owner, NameButtonOk, Control.res.getString(R.string.OK), 
				colorOfButton, boundsOfButtonOK, false, alpha, true, 0);
		controls[1] = new Button(owner, NameButtonCancel, Control.res.getString(R.string.cancel), 
				colorOfButton, boundsOfButtonCancel, false, alpha, true, 0);
		// 이벤트를 이 클래스에서 직접 처리
		controls[0].setOnTouchListener(this);
		controls[1].setOnTouchListener(this);
		
		// 대화상자 이벤트를 받는 리스너 설정
		//setOnTouchListener(listener);
		
		//createMenuDir();
		
		//createMenuFunc();
		//createMenuMultimedia();
		//createMessageDialog();
		
		//if (canSelectFileType) {
			//createMenuFileType();
		//}
			
		//createMenuSort();
		
		RectangleF boundsOfConnectDialog = new RectangleF(bounds.x, bounds.y, 
				view.getWidth()*0.9f, view.getHeight()*0.4f);
		boundsOfConnectDialog.x = view.getWidth()/2 - boundsOfConnectDialog.width/2;
		connectDialog = new ConnectDialog(view, boundsOfConnectDialog);
		
		//setRootPermission();
		
		}catch(Exception e) {
			Log.e("FileDialog", e.toString());
		}
		
	}
	
	String toString(Category category) {
		String r=null;
		if (category==Category.Image) r="Image";
		else if (category==Category.Text) r="Text";
		else if (category==Category.Custom) r="Custom";
		else if (category==Category.Music) r="Music";
		else if (category==Category.Video) r="Video";
		else if (category==Category.All) r="All";
		return r;
	}
	
	private Button[] createFileListButtons(String curDir, Category category) {
		buttonCategory.setText(toString(category));
		buttonDir.setText(curDir);
		
		if (curDir==null) return null;
		this.curDir = curDir;
		this.category = category;		
		//String[] fileList = null;
		/*Context context = view.getContext(); 
		if (dir==Dir.Context) {
			fileList = context.fileList();
			fileList = findFiles(fileList, category);
			
		}
		else if (dir==Dir.Asset) {
			Resources r = context.getResources();
			AssetManager asset = r.getAssets();
			
			try {
				fileList = asset.list("files");
				fileList = findFiles(fileList, category);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("createMenuFileList", e.toString());
			}
		}
		else if (dir==Dir.CurDir) {
			if (curDir==null) return null;
			
		}*/
		try{
			//Log.d("createFileListButtons", curDir+", "+category);
		
		}catch(Exception e) {
			Log.e("createFileListButtons-findFiles", e.toString());
			return null;
		}
		
		return getFileListButtons(fileList);
		
	
	}
	
	Button[] getFileListButtons(String[] fileList) {
		//try {
		int buttonCount;
		if (fileList==null) buttonCount = 1;
		else buttonCount = 1+fileList.length;
		
				
		int i;
		float buttonWidth = menuFileList.originButtonWidth;
		float buttonHeight = menuFileList.originButtonHeight;
		if (poolOfFileListButtons.list.capacity<buttonCount) {
			poolOfFileListButtons.setCapacity(buttonCount);
		}
		//String debugMsg = "buttonCount:"+buttonCount;
		
		
		Button[] buttons = new Button[buttonCount];
		//buttons[0] = new Button(owner, "Up", "..", Color.YELLOW, 
		//		buttonBounds, false, 255, true, 0);
		buttons[0] = (Button) poolOfFileListButtons.getItem(0);
		buttons[0].isManualOrAutoSize = true;
		buttons[0].name = "Up";
		buttons[0].bounds.x = 0;
		buttons[0].bounds.y = 0;
		buttons[0].bounds.width = buttonWidth;
		buttons[0].bounds.height = buttonHeight;
		buttons[0].setText("..");
		buttons[0].setBackColor(Color.BLUE);
		buttons[0].selectable = true;
		buttons[0].toggleable = false;
		buttons[0].isSelected = false;		
		
		
		
		int color;
		for (i=1; i<buttons.length; i++) {
			File file = new File(curDir + fileList[i-1]);			
			if (file.isDirectory()) color = Color.BLUE;
			else color = Color.YELLOW;
			
			buttons[i] = (Button) poolOfFileListButtons.getItem(i);
			buttons[i].isManualOrAutoSize = true;
			buttons[i].name = fileList[i-1];
			buttons[i].bounds.x = 0;
			buttons[i].bounds.y = 0;
			buttons[i].bounds.width = buttonWidth;
			buttons[i].bounds.height = buttonHeight;
			buttons[i].setText(fileList[i-1]);
			buttons[i].setBackColor(color);
			buttons[i].selectable = true;
			buttons[i].toggleable = false;
			buttons[i].isSelected = false;
			
			
		}
		
		ButtonGroup group = new ButtonGroup(null, buttons);
		for (i=0; i<buttons.length; i++) {
			buttons[i].setGroup(group, i);
		}
		
		return buttons;
		
	}
	
	public boolean getIsForReadingOrSaving() {
		return isForReadingOrSaving;
	}
	
	/** EditRichText 크기, 또는 전체화면으로 fileDialog를 사용할 때 컨트롤들의 위치와 크기를 
	 * 바꾸기 위해 호출한다. 먼저 isFullScreen을 true,false로 설정한다.*/
	public void setScaleValues() {
		if (isFullScreen) {
			scaleOfTitleBarY = 0.06f;		
			
			
			scaleOfCategoryY = 0.06f;
					
			scaleOfDirY = 0.06f;
			
			scaleOfMenuFileListY = 0.6f;		
			
			scaleOfeditTextY = 0.08f;			
			
			scaleOfOKButtonY = 0.06f;
			
			// 5는 gap개수
			scaleOfGapY = (1-(scaleOfTitleBarY+scaleOfMenuFileListY+scaleOfDirY+
					scaleOfeditTextY+scaleOfOKButtonY)) / 5;
		}
		else {
			scaleOfTitleBarY = 0.11f;
			
						
			scaleOfCategoryX = 1 / 7.5f;
			scaleOfCategoryY = 0.11f;
			
			scaleOfDirX = scaleOfCategoryX * 4;
			scaleOfDirY = 0.11f;
			
			scaleOfGapX = scaleOfCategoryX * 0.1f;
			
			scaleOfMenuFileListX = 1-scaleOfGapX*2;
			scaleOfMenuFileListY = 0.4f;
			
			scaleOfeditTextX = 1-scaleOfGapX*2;
			scaleOfeditTextY = 0.13f;
				
			scaleOfOKButtonX = (1-scaleOfGapX*3) / 2;
			scaleOfOKButtonY = 0.11f;
			
			// 5는 gap개수
			scaleOfGapY = (1-(scaleOfTitleBarY+scaleOfMenuFileListY+scaleOfDirY+
					scaleOfeditTextY+scaleOfOKButtonY)) / 5;
		}
	}
	
	@SuppressLint("UseValueOf")
	void getStorage() {
    	ArrayList arrName = new ArrayList(5);
    	//ArrayList arrTotalSpace = new ArrayList(5);
    	//ArrayList arrUsableSpace = new ArrayList(5);
    	ArrayList arrUsedSpace = new ArrayList(5);
    	
    	// "/" partition
    	try {
	    	/*File file = Environment.getRootDirectory();
	    	arrName.add(file.getAbsolutePath());
	    	arrTotalSpace.add(new Long(file.getTotalSpace()));
	    	arrUsableSpace.add(new Long(file.getUsableSpace()));*/
	    	    	
	    	File sdcard = Environment.getExternalStorageDirectory();
	    	arrName.add(sdcard.getAbsolutePath());
	    	//arrTotalSpace.add(new Long(sdcard.getTotalSpace()));
	    	//arrUsableSpace.add(new Long(sdcard.getUsableSpace()));
	    	arrUsedSpace.add(
	    		new Long(FileHelper.getFileSize(sdcard.getAbsolutePath(),true)));
	    	//arrUsableSpace.add(new Long(sdcard.getUsableSpace()));
    	}catch(Exception e) {
    	}
    	
    	int i;
    	String msg = "";
    	long usedSpace;
    	for (i=0; i<arrName.count; i++) {
    		/*msg += arrName.getItem(i) + "\n";
    		msg += "Total Space : " + arrTotalSpace.getItem(i) + "\n";
    		usedSpace = Long.valueOf((Long)arrTotalSpace.getItem(i)) - 
    				Long.valueOf((Long)arrUsableSpace.getItem(i));
    		msg += "Used Space : " + usedSpace + "\n";
    		msg += "Usable Space : " + arrUsableSpace.getItem(i);
    		if (i!=arrName.count-1) msg += "\n\n";*/
    		msg += arrName.getItem(i) + "\n";
    		//msg += "Total Space : " + arrTotalSpace.getItem(i) + "\n";
    		usedSpace = Long.valueOf((Long)arrUsedSpace.getItem(i));
    		msg += "Used Space : " + usedSpace + "\n";
    		//msg += "Usable Space : " + arrUsableSpace.getItem(i);
    		if (i!=arrName.count-1) msg += "\n\n";
    	}
    	
    	Control.loggingForNetwork.setText(true, msg, false);
    	Control.loggingForNetwork.setHides(false);
    }
	
		
	boolean equals(String[] oldFileList, String[] fileList) {
		if (oldFileList==null) return false;
		if (oldFileList.length!=fileList.length) return false;
		int i, j;
		boolean[] arrayEqual = new boolean[oldFileList.length];
		for (i=0; i<oldFileList.length; i++) {
			for (j=0; j<fileList.length; j++) {
				if (oldFileList[i].equals(fileList[j])) {
					arrayEqual[i] = true;
					break;
				}
			}
			if (j==fileList.length) return false;
		}
		return true;
	}

	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
}