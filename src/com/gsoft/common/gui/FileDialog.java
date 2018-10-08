package com.gsoft.common.gui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Random;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Environment;
import android.util.Log;

import com.gsoft.common.ColorEx;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.Compiler_gui.TextView;
import com.gsoft.common.CommonGUI_SettingsDialog;
import com.gsoft.common.IO;
import com.gsoft.common.Media;
import com.gsoft.common.Net;
import com.gsoft.common.PowerManagement;
import com.gsoft.common.CompilerHelper;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.IO.FileHelper.SizeAndCount;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Media.MediaPlayerDel;
import com.gsoft.common.Media.MediaPlayerDel.PlayListAndCurSongInfo;
import com.gsoft.common.Media.MediaRecorderDel;
import com.gsoft.common.Net.ServiceThread;
import com.gsoft.common.Net.Wifi;
import com.gsoft.common.Net.WifiThread;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.Date;
import com.gsoft.common.Util.PoolOfButton;
import com.gsoft.common.Util.Sort;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Buttons.ButtonGroup;
import com.gsoft.common.gui.Dialog.EditableDialog;
import com.gsoft.common.gui.Menu.MenuType;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.common.java.lang.Long;
import com.gsoft.common.R.R;

public class FileDialog extends EditableDialog implements OnTouchListener {
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
	Button buttonTextFormat;
	
	Button buttonOnOff;
	
	Size mButtonSize  = new Size((int)(view.getWidth()*0.35f),(int)(view.getHeight()*0.06f));
		
	
	float scaleOfTitleBarY = 0.13f;
	
	
	
	// 버튼:5개, gapX:6개, Dir버튼:나머지버튼들의 4배, gapX는 동일크기이고 나머지 버튼의 0.1, 
	// 나머지 버튼 : x
	// 5x(dir) + 3x + 0.1*x*6 = 1, 8.6x = 1, x= 1/8.6
	
	float scaleOfCategoryX = 1 / 8.6f;
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
	
	// 6는 gap개수
	float scaleOfGapY = (1-(scaleOfTitleBarY+scaleOfMenuFileListY+scaleOfDirY+
			scaleOfeditTextY+scaleOfOKButtonY)) / 6;
	
	//private boolean WasKeyboardHiddenBeforeOpen;
	private Rectangle boundsOfMenuFileList;
	OnTouchListener oldKeyboardListener;
	
	private boolean isForReadingOrSaving;
	
	/*public enum Dir {
		Context, Asset, CurDir
	};*/	
	
	public enum Category {
		Image, Text, Custom, Music, Video, All
	};
	
	
	
	//Dir dir = Dir.Context;	
	
	String curPartision = "";
	//public String curDir = File.separator;
	public String curDir = IO.FileHelper.getPartitionName() + File.separator;
	
	Category category = Category.Custom;
	private MenuWithClosable menuDir;
	MenuWithClosable menuTextFormat;
	
	static String[] partitionSymbols = IO.FileHelper.getPartitionSymbols();
	
	public static String Separator = File.separator;
	
	public static String[] namesOfMenuDir = {
		Control.res.getString(R.string.file_dir_app),
		Control.res.getString(R.string.file_dir_root), 
		"Go to project dir", "Go to SDK dir",
		"Unzip the SDK files(gsoft.zip, project.zip)"
		};
	
	private MenuWithClosable menuFunc;	
	public static String[] namesOfMenuFunc = {/*"Make Dir", "Delete", "Rename", 
		"Cut", "Copy", "Paste", "MultiSelect", "Sort", "Properties"*/
		Control.res.getString(R.string.file_func_mkdir), Control.res.getString(R.string.file_func_del),
		Control.res.getString(R.string.file_func_ren), Control.res.getString(R.string.file_func_cut),
		Control.res.getString(R.string.file_func_copy), Control.res.getString(R.string.file_func_paste),
		Control.res.getString(R.string.file_func_multiselect), Control.res.getString(R.string.file_func_sort),
		"Storage", Control.res.getString(R.string.file_func_properties)};
	
	/** Media 버튼을 눌렀을때 나오는 메뉴*/
	private MenuWithClosable menuMultimedia;	
	public static String[] namesOfMenuMultimedia = {
		/*"Send a file(s)", "Start receiver", 
		"Listen to music", "Watch video", "Record sound", "Play record", 
		"Sound Control Menu"*/
		Control.res.getString(R.string.file_media_send), Control.res.getString(R.string.file_media_receiver),
		Control.res.getString(R.string.file_media_music), Control.res.getString(R.string.file_media_video),
		"Install a package",
		Control.res.getString(R.string.file_media_record), Control.res.getString(R.string.file_media_play_record),
		Control.res.getString(R.string.file_media_sound_menu), Control.res.getString(R.string.file_media_help_menu)};
	
	/** Help 버튼을 눌렀을때 나오는 메뉴*/
	private MenuWithClosable menuHelp;
	public static String[] namesOfMenuHelp = {
		"How to play music", "How to transfer files", "How to view a java file", "How to view a class file"
	};
	
	private MenuWithClosable menuFileType;	
	public static String[] namesOfMenuFileType = {"Image", "Text", "Custom", "Music", "Video", "All"};
	
	private MenuWithClosable menuSort;	
	public static String[] namesOfMenuSort = {/*"Sort by name, ascending", "Sort by name, descending",
		"Sort by time, ascending", "Sort by time, descending"*/
		Control.res.getString(R.string.file_sort_name_ascend), Control.res.getString(R.string.file_sort_name_descend),
		Control.res.getString(R.string.file_sort_time_ascend), Control.res.getString(R.string.file_sort_time_descend)};
	
	
	public static String[] namesOfMenuTextFormat = {"UTF-8", "UTF-16", "MS-949"};
	
	public static class SortByTime {
		public long modifiedTime;
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
	
	/** load/save와 openFileDialog를 구분한다.*/
	public boolean isOpenFileDialog;
	
	PoolOfButton poolOfFileListButtons;
	Button[] fileListButtons;
	boolean isDirectoryOrFileForCutOrCopy;
	/** cut이나 copy한 파일 또는 디렉토리의 파일 리스트, 
	 * 디렉토리의 경우는 ArrayList(File), 파일의 경우는 File이다.*/
	private ArrayList fileListOfCutOrCopy; 
	//private ArrayList fileListOfCutOrCopy;
	
	/** 다중선택한 파일리스트, 파일(디렉토리)의 절대경로 스트링을 담는다.*/ 
	private ArrayListString fileListOfMultiSelect = new ArrayListString(10);
	boolean isCutOrCopy;
	
	WifiThread wifiThread;
	private ServiceThread serviceThread;
	
	//SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	//SoundPlayer soundPlayer;
	public MediaPlayerDel mediaPlayer = null;
	public boolean isFullScreen;
	private MediaRecorderDel recorder;
	private ArrayListString mPlayList;
	private PlayListAndCurSongInfo playListAndCurSongInfo;
	private Process su;
	private Process data;

	//IntegrationKeyboard keyboard = CommonGUI.keyboard;

	private static TextView textViewLogBird = CommonGUI.textViewLogBird;
	
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
	
	String[] findFiles(String[] fileList, Category category) {
		if (fileList==null) return null;
		int i;
		ArrayListString r = new ArrayListString(100);
		int count=0;
		
		try {
			for (i=0; i<fileList.length; i++) {
				String filename = fileList[i].toLowerCase();
				String absFilename = curDir + filename;
				File file = new File(absFilename);
				if (file.isDirectory()) {
					//if (count>=r.length) r = Array.Resize(r, r.length+10);
					//r[count++] = fileList[i];
					r.add(fileList[i]);
				}
			}
		}catch(Exception e) {
			Log.e("findFiles-findFile",e.toString());
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird , e);
		}
		
		if (category==Category.Image) {			
			for (i=0; i<fileList.length; i++) {
				try{					
				String filename = fileList[i].toLowerCase();
				
				String absFilename = curDir + filename;
				File file = new File(absFilename);
				if (file.isDirectory()) continue;
				
				String ext = FileHelper.getExt(filename);
				if (ext==null) continue;
				//if (ext.equals(".bmp") | ext.equals(".png") | ext.equals(".jpg") |
				//		 ext.equals(".gif")) {
				for (int k=0; k<Media.extensionOfImage.length; k++) {
    				if (ext.contains(Media.extensionOfImage[k])) {
						//if (count>=r.length) r = Array.Resize(r, r.length+10);
						//r[count++] = fileList[i];
    					r.add(fileList[i]);
						break;
    				}
				}
				}catch(Exception e) {
					e.printStackTrace();
					CompilerHelper.printStackTrace(textViewLogBird, e);
				}
			}
			//r = Array.Resize(r, count);
			//return r;
			return r.getItems();
		}
		else if (category==Category.Text) {
			for (i=0; i<fileList.length; i++) {
				String filename = fileList[i].toLowerCase();
				
				String absFilename = curDir + filename;
				File file = new File(absFilename);
				if (file.isDirectory()) continue;
				
				String ext = FileHelper.getExt(filename);
				if (ext==null) continue;
				int k;
				boolean isNotText=false;
				for (k=0; k<Media.extensionOfImage.length; k++) {
    				if (ext.contains(Media.extensionOfImage[k])) {
    					isNotText = true;
    					break;
    				}
				}
				if (isNotText) continue;
				for (k=0; k<Media.extensionOfAudio.length; k++) {
    				if (ext.contains(Media.extensionOfAudio[k])) {
    					isNotText = true;
    					break;
    				}
				}
				if (isNotText) continue;
				for (k=0; k<Media.extensionOfVideo.length; k++) {
    				if (ext.contains(Media.extensionOfVideo[k])) {
    					isNotText = true;
    					break;
    				}
				}
				if (isNotText) continue;
				/*if (ext.equals(".txt") || ext.equals(".xml") || 
						ext.equals(".htm") || ext.equals(".html") ||
						ext.equals(".h") || ext.equals(".java") || 
						ext.equals(".c") || ext.equals(".cpp") ||
						ext.equals(".sh")) {*/
					//if (count>=r.length) r = Array.Resize(r, r.length+20);
					//r[count++] = fileList[i];
				r.add(fileList[i]);
				//}
			}
			//r = Array.Resize(r, count);
			//return r;
			return r.getItems();
			
		}
		else if (category==Category.Custom) {
			for (i=0; i<fileList.length; i++) {
				String filename = fileList[i].toLowerCase();

				String absFilename = curDir + filename;
				File file = new File(absFilename);
				if (file.isDirectory()) continue;

				String ext = FileHelper.getExt(filename);
				if (ext==null) continue;
				if (ext.equals(".kjy")) {
					//if (count>=r.length) r = Array.Resize(r, r.length+10);
					//r[count++] = fileList[i];
					r.add(fileList[i]);
				}
			}
			//r = Array.Resize(r, count);
			//return r;
			return r.getItems();
			
		}
		else if (category==Category.Music) {			
			for (i=0; i<fileList.length; i++) {
				try{
				String filename = fileList[i].toLowerCase();

				String absFilename = curDir + filename;
				File file = new File(absFilename);
				if (file.isDirectory()) continue;
				
				String ext = FileHelper.getExt(filename);
				if (ext==null) continue;
				//if (ext.equals(".wav") | ext.equals(".mp3") | ext.equals(".wma")) {
				for (int k=0; k<Media.extensionOfAudio.length; k++) {
    				if (ext.contains(Media.extensionOfAudio[k])) {
						//if (count>=r.length) r = Array.Resize(r, r.length+10);
						//r[count++] = fileList[i];
    					r.add(fileList[i]);
						break;
    				}
				}
				}catch(Exception e) {
					e.printStackTrace();
					CompilerHelper.printStackTrace(textViewLogBird, e);
				}
			}
			//r = Array.Resize(r, count);
			//return r;
			return r.getItems();
		}
		else if (category==Category.Video) {			
			for (i=0; i<fileList.length; i++) {
				try{
				String filename = fileList[i].toLowerCase();

				String absFilename = curDir + filename;
				File file = new File(absFilename);
				if (file.isDirectory()) continue;
				
				String ext = FileHelper.getExt(filename);
				if (ext==null) continue;
				//if (ext.equals(".mp4") | ext.equals(".wmv") | ext.equals(".avi")) {
				for (int k=0; k<Media.extensionOfVideo.length; k++) {
    				if (ext.contains(Media.extensionOfVideo[k])) {
						//if (count>=r.length) r = Array.Resize(r, r.length+10);
						//r[count++] = fileList[i];
    					r.add(fileList[i]);
						break;
    				}
				}
				}catch(Exception e) {
					e.printStackTrace();
					CompilerHelper.printStackTrace(textViewLogBird, e);
				}
			}
			//r = Array.Resize(r, count);
			//return r;
			return r.getItems();
		}
		else if (category==Category.All) {
			for (i=0; i<fileList.length; i++) {
				try{
					String filename = fileList[i].toLowerCase();

					String absFilename = curDir + filename;
					File file = new File(absFilename);
					if (file.isDirectory()) continue;

					r.add(fileList[i]);
				}catch(Exception e) {
					e.printStackTrace();
					CompilerHelper.printStackTrace(textViewLogBird, e);
				}
			}
			//r = Array.Resize(r, count);
			//return r;
			return r.getItems();
			
		}
		
		return fileList;
	}
	
	String[] fileList(String curDir) {
		try {
			File dir = new File(curDir);
			String[] list = dir.list();
			
			return list;
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return null;
		}
	}
	
	/** FileListButtons 의 Pool을 활용하여 디렉토리를 바꿀 때마다 버튼들을 생성하지 않고 메모리를 절약한다.
	 * 즉 디렉토리를 바꾸면 버튼들을 새로 만드는 것이 아니라 pool에서 가져와서 버튼의 속성만 바꿔준다.
	 * (createFileListButtons참조)*/
	void createPoolOfFileListButtons() {
		if (poolOfFileListButtons==null) {
			poolOfFileListButtons = new PoolOfButton(50, mButtonSize);
		}
	}
	
	
	private Button[] createFileListButtons(String curDir, Category category) {
		// 윈도우즈(자바)이면 루트에서 up을 클릭할때 파티션들이 나와야한다.
		if (curDir.equals(IO.LocalComputer)) {
			buttonDir.setText(curDir);
			buttonCategory.setText(toString(Category.All));
			fileList = partitionSymbols;
			fileList = findFiles(fileList, category.All);
			return getFileListButtons(fileList);
		}
		
		buttonCategory.setText(toString(category));
		File dir = new File(curDir);
		// 현재 디렉토리의 파티션 문자를 얻는다. 그러나 뒤에 separator 가 없어진다.
		//curDir = dir.getAbsolutePath();		
		//dir = new File(curDir);
		buttonDir.setText(dir.getAbsolutePath());
		
		this.curDir = curDir;
		this.setCurDir(curDir);
		this.category = category;		
		
		
		try{
			//Log.d("createFileListButtons", curDir+", "+category);
		fileList = fileList(curDir);
		fileList = findFiles(fileList, category);
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return null;
		}
		
		return getFileListButtons(fileList);
		
	
	}
	
	Button[] getFileListButtons(String[] fileList) {
		//try {
		int buttonCount;
		if (fileList==null) buttonCount = 1;
		else if (curDir.equals(IO.LocalComputer)) {
			buttonCount = fileList.length;
		}
		else buttonCount = 1+fileList.length;
		
				
		int i;
		int buttonWidth = menuFileList.originButtonWidth;
		int buttonHeight = menuFileList.originButtonHeight;
		if (poolOfFileListButtons.list.capacity<buttonCount) {
			poolOfFileListButtons.setCapacity(buttonCount, mButtonSize);
		}
		//String debugMsg = "buttonCount:"+buttonCount;
		
		
		Button[] buttons = new Button[buttonCount];
		
		if (curDir.equals(IO.LocalComputer)==false) {
			//buttons[0] = new Button(owner, "Up", "..", Color.YELLOW, 
			//		buttonBounds, false, 255, true, 0);
			buttons[0] = (Button) poolOfFileListButtons.getItem(0);
			buttons[0].isManualOrAutoSize = true;
			buttons[0].name = "Up";
			buttons[0].bounds.x = 0;
			buttons[0].bounds.y = 0;
			buttons[0].bounds.width = buttonWidth;
			buttons[0].bounds.height = buttonHeight;			
			buttons[0].setBackColor(Color.BLUE);
			buttons[0].selectable = true;
			buttons[0].toggleable = false;
			buttons[0].isSelected = false;
			//buttons[0].changeBounds(buttons[0].bounds);
			buttons[0].setText("..");
		}
		
		int startIndex;
		
		if (curDir.equals(IO.LocalComputer)==false) {
			startIndex = 1;			
		}
		else {
			startIndex = 0;
		}
		
		int color;
		for (i=startIndex; i<buttons.length; i++) {
			File file;
			if (curDir.equals(IO.LocalComputer)==false) {
				file = new File(curDir + fileList[i-1]);
				if (file.isDirectory()) color = Color.BLUE;
				else color = Color.YELLOW;
				
				buttons[i] = (Button) poolOfFileListButtons.getItem(i);
				buttons[i].isManualOrAutoSize = true;
				buttons[i].name = fileList[i-1];
				buttons[i].bounds.x = 0;
				buttons[i].bounds.y = 0;
				buttons[i].bounds.width = buttonWidth;
				buttons[i].bounds.height = buttonHeight;				
				buttons[i].setBackColor(color);
				buttons[i].selectable = true;
				buttons[i].toggleable = false;
				buttons[i].isSelected = false;
				//buttons[i].changeBounds(buttons[i].bounds);
				buttons[i].setText(fileList[i-1]);
			}
			else {
				file = new File(fileList[i]);
				if (file.isDirectory()) color = Color.BLUE;
				else color = Color.YELLOW;
				
				buttons[i] = (Button) poolOfFileListButtons.getItem(i);
				buttons[i].isManualOrAutoSize = true;
				buttons[i].name = fileList[i];
				buttons[i].bounds.x = 0;
				buttons[i].bounds.y = 0;
				buttons[i].bounds.width = buttonWidth;
				buttons[i].bounds.height = buttonHeight;
				buttons[i].setBackColor(color);
				buttons[i].selectable = true;
				buttons[i].toggleable = false;
				buttons[i].isSelected = false;
				//buttons[i].changeBounds(buttons[i].bounds);
				buttons[i].setText(fileList[i]);
			}
			
		}
		
		ButtonGroup group = new ButtonGroup(null, buttons);
		for (i=0; i<buttons.length; i++) {
			buttons[i].setGroup(group, i);
		}
		
		return buttons;
		
	}
	
	/** curDir이 /으로 끝나지 않으면 /을 붙인다*/
	void setCurDir(String curDirectory) {
		if (curDirectory!=null) {
			if (curDirectory.equals(IO.LocalComputer)) {
				curDir = IO.LocalComputer;
			}
			else {
				String str = curDirectory.substring(curDirectory.length()-1);
				//Log.d("setCurDir", "str:"+str);
				if (str.equals(File.separator)==false) {
					curDir = curDirectory + File.separator;
				}
				else {
					curDir = curDirectory;
				}
			}
		}
		//Log.d("setCurDir", curDir);
	}
	
	public void createAndSetFileListButtons(String curDirectory, Category category) {
		Button[] fileList=null;
		
		try {
		//Log.d("createAndSetFileListButtons", "curDirectory:"+curDirectory);
		setCurDir(curDirectory);
		//Log.d("createAndSetFileListButtons", "curDir:"+curDir);
		fileList = createFileListButtons(curDir, category);
		
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
		try {
		if (menuFileList!=null/* && fileList!=null*/) {
			menuFileList.setButtons(fileList);
			//Control.setModified(true);
		}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
	}
	
	/** 영역만 잡아주고 menuFileList의 내용(Button[])은 나중에 createAndSetFileListButtons를
	 * 통해 넣어준다. 
	 * @param dir
	 * @param curDir
	 * @param category
	 */
	private void createMenuFileList(String curDir, Category category) {
		// fileDialog생성시에 dir은 null로 설정된다.
		//if (dir==null) return;
		if (category!=null) {
			Button[] fileList = createFileListButtons(curDir, category);
			if (menuFileList==null) {
				menuFileList = new MenuWithScrollBar(owner, boundsOfMenuFileList, 
						mButtonSize, 
						MenuWithScrollBar.ScrollMode.VScroll);
				menuFileList.setOnTouchListener(this);				
			}
			menuFileList.setButtons(fileList);
		}
		else {
			if (menuFileList==null) {
				menuFileList = new MenuWithScrollBar(owner, boundsOfMenuFileList, 
						mButtonSize, 
						MenuWithScrollBar.ScrollMode.VScroll);
				menuFileList.setOnTouchListener(this);				
			}
		}
		//menuFileList.isOpen = true;
	}
	
	
	
	public void changeBounds(Rectangle paramBounds) {	
		
		this.bounds = paramBounds;
		applySizingBorderOfView(bounds);
		if (isMaximized()==false) backUpBounds();
		
		heightTitleBar = (int) (bounds.height * scaleOfTitleBarY);
		
		int heightOfGap = (int)(bounds.height * scaleOfGapY);
		int widthOfGap = (int)(bounds.width * scaleOfGapX);
		
		boundsOfMenuFileList = new Rectangle(bounds.x+widthOfGap, 
				bounds.y+heightTitleBar+heightOfGap, 
				(int)(bounds.width*scaleOfMenuFileListX), (int)(bounds.height*scaleOfMenuFileListY));
		if (menuFileList!=null)
			menuFileList.changeBounds(boundsOfMenuFileList);
				
		Rectangle boundsOfEditText = new Rectangle(bounds.x+widthOfGap, 
				boundsOfMenuFileList.bottom()+heightOfGap, 
				(int)(bounds.width*scaleOfeditTextX), (int)(bounds.height*scaleOfeditTextY));
		editText.changeBounds(boundsOfEditText);
		
		
		int width = (int) (bounds.width * scaleOfDirX);
		int height = (int) (bounds.height * scaleOfDirY);
		int x = bounds.x + widthOfGap;
		int y = boundsOfEditText.bottom() + heightOfGap;
		Rectangle boundsOfDir = new Rectangle(x,y,width,height);
		
		width = (int) (bounds.width * scaleOfCategoryX);
		height = (int) (bounds.height * scaleOfCategoryY);
		x = bounds.x + widthOfGap;
		y = boundsOfEditText.bottom() + heightOfGap;		
		Rectangle boundsOfCategory = new Rectangle(boundsOfDir.right()+widthOfGap,y,
				width,height);
		Rectangle boundsOfTextFormat = new Rectangle(boundsOfCategory.right()+widthOfGap,y,
				width,height);
		
		Rectangle boundsOfFunc = new Rectangle(boundsOfTextFormat.right()+widthOfGap,y,
				width,height);
		Rectangle boundsOfMultimedia = new Rectangle(boundsOfFunc.right()+widthOfGap,y,
				width,height);
		
		width = boundsOfCategory.width;
		height = heightTitleBar;
		Rectangle boundsOfOnOff = new Rectangle(bounds.x+bounds.width-width, bounds.y,
				width,height);
		
		buttonDir.changeBounds(boundsOfDir);
		buttonCategory.changeBounds(boundsOfCategory);
		buttonTextFormat.changeBounds(boundsOfTextFormat);
		buttonFunc.changeBounds(boundsOfFunc);
		buttonMultimedia.changeBounds(boundsOfMultimedia);
		
		buttonOnOff.changeBounds(boundsOfOnOff);
				
			
		width = (int) (bounds.width * scaleOfOKButtonX);
		height = (int) (bounds.height * scaleOfOKButtonY);
		x = bounds.x + widthOfGap;
		y = boundsOfDir.bottom() + heightOfGap;
		Rectangle boundsOfButtonOK = new Rectangle(x,y,width,height);
		x = boundsOfButtonOK.right() + widthOfGap;
		Rectangle boundsOfButtonCancel = new Rectangle(x,y,width,height);
		
		((Button)controls[0]).changeBounds(boundsOfButtonOK);		
		((Button)controls[1]).changeBounds(boundsOfButtonCancel);
		
		//connectDialog.changeBounds(bounds);
	}
	
	
	
	public void addChar(String charA, boolean isNextToCursor) {
		int i;
		String[] specialKeys = IntegrationKeyboard.SpecialKeys;
		for (i=0; i<specialKeys.length; i++) {
			if (charA.equals(specialKeys[i])) {
				menuFileList.controlChar(i, charA);
				return;
			}
		}
		editText.addChar(charA/*, isNextToCursor*/);
	}
	
	public void replaceChar(String charA) {
		int i;
		String[] specialKeys = IntegrationKeyboard.SpecialKeys;
		for (i=0; i<specialKeys.length; i++) {
			if (charA.equals(specialKeys[i])) {
				menuFileList.controlChar(i, charA);
				return;
			}
		}
		editText.replaceChar(charA);
	}
	
	void createWifiDir() {
		try {
			File wifiDir = new File(Control.pathWifi);
			if (wifiDir.exists()==false) {
				wifiDir.mkdirs();
				/*boolean r = wifiDir.mkdirs();
				if (!r) {
					Control.loggingForNetwork.setText(true, "Can't make '/wifi' directory" , true);
					Control.loggingForNetwork.setHides(false);
				}*/
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
	}
	
	
	public FileDialog(/*boolean isFullScreen, boolean canSelectFileType, */Object owner, Rectangle bounds, String[] fileList) {
		super(owner, bounds);
			
		createWifiDir();
		
		try {
				        
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
		boundsOfMenuFileList = new Rectangle(bounds.x+widthOfGap, 
				bounds.y+heightTitleBar+heightOfGap, 
				(int)(bounds.width*scaleOfMenuFileListX), (int)(bounds.height*scaleOfMenuFileListY));
		createMenuFileList(null, null);
		
		createPoolOfFileListButtons();
		
		// owner속성을 this로 해야 editText.owner 속성으로 키보드에서 EditableDialog를 최대화할 수 있다.		
		Rectangle boundsOfEditText = new Rectangle(bounds.x+widthOfGap, 
				boundsOfMenuFileList.bottom()+heightOfGap, 
				(int)(bounds.width*scaleOfeditTextX), (int)(bounds.height*scaleOfeditTextY));
		editText = new EditText(false, false, this, "SaveFileEditText", boundsOfEditText, 
				boundsOfEditText.height*0.5f, true, 
				new CodeString("", Color.BLACK), 
				EditText.ScrollMode.Both, Color.WHITE);
		//oldListener = keyboard.setOnTouchListener(editText);
		
		int buttonWidth = (int) (bounds.width * scaleOfDirX);
		int buttonHeight = (int) (bounds.height * scaleOfDirY);
		int x = bounds.x + widthOfGap;
		int y = boundsOfEditText.bottom() + heightOfGap;
		Rectangle boundsOfDir = new Rectangle(x,y,buttonWidth,buttonHeight);
		
		buttonWidth = (int) (bounds.width * scaleOfCategoryX);
		buttonHeight = (int) (bounds.height * scaleOfCategoryY);
		x = bounds.x + widthOfGap;
		y = boundsOfEditText.bottom() + heightOfGap;		
		Rectangle boundsOfCategory = new Rectangle(boundsOfDir.right()+widthOfGap,y,
				buttonWidth,buttonHeight);
		
		Rectangle boundsOfTextFormat = new Rectangle(boundsOfCategory.right()+widthOfGap,y,
				buttonWidth,buttonHeight);
		
		Rectangle boundsOfFunc = new Rectangle(boundsOfTextFormat.right()+widthOfGap,y,
				buttonWidth,buttonHeight);
		
		Rectangle boundsOfMultimedia = new Rectangle(boundsOfFunc.right()+widthOfGap,y,
				buttonWidth,buttonHeight);
		
		
		
		buttonWidth = boundsOfCategory.width;
		buttonHeight = heightTitleBar;
		Rectangle boundsOfOnOff = new Rectangle(bounds.x+bounds.width-buttonWidth, bounds.y,
				buttonWidth,buttonHeight);
		
		
		buttonDir = new Button(owner, "", Control.res.getString(R.string.file_but_dir), 
				colorOfButton, boundsOfDir, false, alpha, false, 0.0f, null, Color.CYAN);
		buttonCategory = new Button(owner, "", Control.res.getString(R.string.file_but_category), 
				colorOfButton, boundsOfCategory, false, alpha, false, 0.0f, null, Color.CYAN);		
		buttonFunc = new Button(owner, "", Control.res.getString(R.string.file_but_func), 
				colorOfButton, boundsOfFunc, false, alpha, false, 0.0f, null, Color.CYAN);
		buttonMultimedia = new Button(owner, "", Control.res.getString(R.string.file_but_media), 
				colorOfButton, boundsOfMultimedia, false, alpha, false, 0.0f, null, Color.CYAN);
		buttonTextFormat = new Button(owner, "", /*Control.res.getString(R.string.file_but_media)*/"TextFormat", 
				colorOfButton, boundsOfTextFormat, false, alpha, false, 0.0f, null, Color.CYAN);
		
		buttonOnOff = new Button(owner, "", "On/Off", 
				colorOfButton, boundsOfOnOff, false, alpha, false, 0.0f, null, Color.GREEN);
		
		buttonOnOff.selectable = true;	// OnOff 버튼은 토글로 동작한다.
		buttonOnOff.toggleable = true;
		buttonOnOff.ColorSelected = Color.YELLOW;
		buttonOnOff.isSelected = true;
		
		buttonDir.setOnTouchListener(this);
		buttonCategory.setOnTouchListener(this);
		buttonFunc.setOnTouchListener(this);
		buttonMultimedia.setOnTouchListener(this);
		buttonTextFormat.setOnTouchListener(this);
		
		buttonOnOff.setOnTouchListener(this);
		
		buttonWidth = (int) (bounds.width * scaleOfOKButtonX);
		buttonHeight = (int) (bounds.height * scaleOfOKButtonY);
		x = bounds.x + widthOfGap;
		y = boundsOfDir.bottom() + heightOfGap;
		Rectangle boundsOfButtonOK = new Rectangle(x,y,buttonWidth,buttonHeight);
		x = boundsOfButtonOK.right() + widthOfGap;
		Rectangle boundsOfButtonCancel = new Rectangle(x,y,buttonWidth,buttonHeight);		
		
		controls = new Button[2];
		controls[0] = new Button(owner, NameButtonOk, Control.res.getString(R.string.OK), 
				colorOfButton, boundsOfButtonOK, false, alpha, true, 0.0f, null, Color.CYAN);
		controls[1] = new Button(owner, NameButtonCancel, Control.res.getString(R.string.cancel), 
				colorOfButton, boundsOfButtonCancel, false, alpha, true, 0.0f, null, Color.CYAN);
		// 이벤트를 이 클래스에서 직접 처리
		controls[0].setOnTouchListener(this);
		controls[1].setOnTouchListener(this);
		
		// 대화상자 이벤트를 받는 리스너 설정
		//setOnTouchListener(listener);
		
		createMenuDir();
		createMenuTextFormat();
		
		createMenuFunc();
		createMenuMultimedia();
		createMenuHelp();
		createMessageDialog();
		
		//if (canSelectFileType) {
			createMenuFileType();
		//}
			
		createMenuSort();
		
		Rectangle boundsOfConnectDialog = new Rectangle(bounds.x, bounds.y, 
				(int)(view.getWidth()*0.9f), (int)(view.getHeight()*0.4f));
		boundsOfConnectDialog.x = view.getWidth()/2 - boundsOfConnectDialog.width/2;
		connectDialog = new ConnectDialog(view, boundsOfConnectDialog);
		
		setRootPermission();
		
		if (isMaximized()==false) backUpBounds();
		
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
		}
		
	}
	
	public void setRootPermission() {
		/*ProcessBuilder builder = new ProcessBuilder();
		
		//File workingDir = view.getContext().getFilesDir();
		//builder.directory(workingDir);
		String msg = "/system/xbin/su -> ";
		builder.command("/system/xbin/su");
		builder.redirectErrorStream(true);
		if (su==null) {
			try {
				su = builder.start();
				InputStream is = su.getInputStream();
				msg += IO.readString(is, TextFormat.UTF_8);
				Control.loggingForMessageBox.setText(true, msg, false);
				Control.loggingForMessageBox.setHides(false);
			}catch (Exception e) {
				// TODO Auto-generated catch block
				Control.loggingForMessageBox.setText(true, "error:"+msg, false);
				Control.loggingForMessageBox.setHides(false);
			}
		}
		
		//String command = "sudo";
		msg += "chmod -R 775 /data -> ";
		String[] commandAndArg = {"chmod", "-R", "775", "/data"};
		builder.command(commandAndArg);
		builder.redirectErrorStream(true);
		Process process=null;
		try {
			process = builder.start();
			InputStream is = process.getInputStream();
			msg += IO.readString(is, TextFormat.UTF_8);
			Control.loggingForMessageBox.setText(true, msg, false);
			Control.loggingForMessageBox.setHides(false);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Control.loggingForMessageBox.setText(true, msg, false);
			Control.loggingForMessageBox.setHides(false);
		}
			
		msg += "chmod -R 4775 /data -> ";
		String[] commandAndArg2 = {"chmod", "-R", "4775", "/data"};
		builder.command(commandAndArg2);
		builder.redirectErrorStream(true);
		Process process2 = null;
		try {
			process2 = builder.start();
			InputStream is = process2.getInputStream();
			msg += IO.readString(is, TextFormat.UTF_8);
			Control.loggingForMessageBox.setText(true, msg, false);
			Control.loggingForMessageBox.setHides(false);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Control.loggingForMessageBox.setText(true, msg, false);
			Control.loggingForMessageBox.setHides(false);
		}				
		
		msg += "passwd root -> ";
		String[] commandAndArg3 = {"passwd", "root"};
		builder.command(commandAndArg3);
		builder.redirectErrorStream(true);
		Process process3 = null;
		try {
			process3 = builder.start();
			InputStream is = process3.getInputStream();
			msg += IO.readString(is, TextFormat.UTF_8);
			Control.loggingForMessageBox.setText(true, msg, false);
			Control.loggingForMessageBox.setHides(false);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Control.loggingForMessageBox.setText(true, msg, false);
			Control.loggingForMessageBox.setHides(false);
		}*/
				
	}
	
	public void getPermission(File file) {
		if (su==null) return;
		//if (su.exitValue()!=1) return;
		
		ProcessBuilder builder = new ProcessBuilder();
		builder.redirectErrorStream(true);
		
		builder.command("chmod", "777", file.getAbsolutePath());
		try {
			data = builder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		
	}
	
	public void closePermission() {
		if (data!=null) data.destroy();
	}
	
		
	public static PlayListAndCurSongInfo readPlayListAndCurSongInfo(InputStream is) throws Exception {
		TextFormat format = com.gsoft.common.IO.TextFormat.UTF_8;
		return MediaPlayerDel.readPlayListAndCurSongInfo(is, format);
	}
	
	/** state에 상관없이 playListAndCurSongInfo를 만든다. 
	 * mPlayList는 restorePlaylist()에서 읽어들인 리스트 혹은 listenToMusic에서 갱신된 리스트이다.*/
	public void write(OutputStream os, TextFormat format) throws Exception {
		MediaPlayerDel.write(os, mPlayList, TextFormat.UTF_8);
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
		menuTextFormat = new MenuWithClosable("MenuTextFormat", boundsMenuTextSaveFormat, 
				MenuType.Vertical, this, namesOfMenuTextFormat, new Size(3,3), true, this);
	}

	void createMenuDir() {
		try {
			if (view==null)
				view = getView(owner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return;
		}
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int x, y;
		int width=(int) (viewWidth*0.6f);
		int height=(int) (viewHeight*0.35f);
		x = viewWidth/2-width/2;
		y = viewHeight/2-height/2;
		Rectangle boundsMenuDir = new Rectangle(x,y,width,height);  
		menuDir = new MenuWithClosable("MenuDir", boundsMenuDir, 
				MenuType.Vertical, this, namesOfMenuDir, new Size(3,3), true, this);		
	}
	
	void createMenuFunc() {
		try {
			if (view==null)
				view = getView(owner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return;
		}
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int x, y;
		int width=(int) (viewWidth*0.6f);
		int height=(int) (viewHeight*0.8f);
		x = viewWidth/2-width/2;
		y = viewHeight/2-height/2;
		Rectangle boundsMenuFunc = new Rectangle(x,y,width,height);  
		menuFunc = new MenuWithClosable("MenuFunc", boundsMenuFunc, 
				MenuType.Vertical, this, namesOfMenuFunc, new Size(3,3), true, this);
		
		menuFunc.buttons[6].selectable = true;	// MultiSelect 메뉴는 토글로 동작한다.
		menuFunc.buttons[6].toggleable = true;
		menuFunc.buttons[6].ColorSelected = Color.YELLOW;
		
		/*menuFunc.buttons[9].selectable = true;	// Sound Control Menu 메뉴는 토글로 동작한다.
		menuFunc.buttons[9].toggleable = true;
		menuFunc.buttons[9].ColorSelected = Color.YELLOW;*/
	}
	
	
	void createMenuHelp() {
		try {
			if (view==null)
				view = getView(owner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return;
		}
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int x, y;
		int width=(int) (viewWidth*0.6f);
		int height=(int) (viewHeight*0.5f);
		x = viewWidth/2-width/2;
		y = viewHeight/2-height/2;
		Rectangle boundsMenuMultimedia = new Rectangle(x,y,width,height);  
		menuHelp = new MenuWithClosable("menuHelp", boundsMenuMultimedia, 
				MenuType.Vertical, this, namesOfMenuHelp, new Size(3,3), true, this);
		
	
	}
	
	void createMenuMultimedia() {
		try {
			if (view==null)
				view = getView(owner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return;
		}
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int x, y;
		int width=(int) (viewWidth*0.6f);
		int height=(int) (viewHeight*0.75f);
		x = viewWidth/2-width/2;
		y = viewHeight/2-height/2;
		Rectangle boundsMenuMultimedia = new Rectangle(x,y,width,height);  
		menuMultimedia = new MenuWithClosable("menuMultimedia", boundsMenuMultimedia, 
				MenuType.Vertical, this, namesOfMenuMultimedia, new Size(3,3), true, this);
		
		menuMultimedia.buttons[5].selectable = true;	// Record sound 메뉴는 토글로 동작한다.
		menuMultimedia.buttons[5].toggleable = true;
		menuMultimedia.buttons[5].ColorSelected = Color.YELLOW;
	
	}
	
	/** Send a File메뉴를 통해서 FileDialog에 접근하는 방법으로만 menuFileType을 생성하고 
	 * 다른 방법으로는 이것을 생성하지 않는다. 즉 다른 방법으로는 이 메서드를 호출하지 않는다.
	 */
	public void createMenuFileType() {
		if (menuFileType!=null) return; // 한 번만 생성한다.
		try {
			if (view==null)
				view = getView(owner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return;
		}
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int x, y;
		int width=(int) (viewWidth*0.6f);
		int height=(int) (viewHeight*0.6f);
		x = viewWidth/2-width/2;
		y = viewHeight/2-height/2;
		Rectangle boundsMenuFileType = new Rectangle(x,y,width,height);  
		menuFileType = new MenuWithClosable("MenuFileType", boundsMenuFileType, 
				MenuType.Vertical, this, namesOfMenuFileType, new Size(3,3), true, this);		
	}
	
	public void createMenuSort() {
		try {
			if (view==null)
				view = getView(owner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return;
		}
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int x, y;
		int width=(int) (viewWidth*0.65f);
		int height=(int) (viewHeight*0.5f);
		x = viewWidth/2-width/2;
		y = viewHeight/2-height/2;
		Rectangle boundsMenuSort = new Rectangle(x,y,width,height);  
		menuSort = new MenuWithClosable("MenuSort", boundsMenuSort, 
				MenuType.Vertical, this, namesOfMenuSort, new Size(3,3), true, this);		
	}
	
	void createMessageDialog() {
		try {
			if (view==null)
				view = getView(owner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
			return;
		}
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		int x, y;
		int width=(int) (viewWidth*0.7f);
		int height=(int) (viewHeight*0.6f);
		x = viewWidth/2-width/2;
		y = viewHeight/2-height/2;
		Rectangle boundsMessageDialog = new Rectangle(x,y,width,height);  
		messageDialog = new MessageDialog(view, boundsMessageDialog);
		messageDialog.setOnTouchListener(this);
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
	
	public void draw(Canvas canvas)
    {
		if (hides) return;
		synchronized(this) {
		try{
		if (hides==false) {
	        super.draw(canvas);
	        if (menuFileList!=null/* && menuFileList.isOpen*/)
	        	menuFileList.draw(canvas);
	        editText.draw(canvas);
	        buttonDir.draw(canvas);
	        buttonCategory.draw(canvas);
	        buttonTextFormat.draw(canvas);
	        buttonFunc.draw(canvas);
	        buttonMultimedia.draw(canvas);
	        
	        buttonOnOff.draw(canvas);
	        
	        /*if (menuDir.getIsOpen()) {
				menuDir.draw(canvas);
			}
	        if (menuFunc.getIsOpen()) {
				menuFunc.draw(canvas);
			}
	        if (menuFileType!=null && menuFileType.getIsOpen()) {
	        	menuFileType.draw(canvas);
			}
	        
	        if (mediaPlayer!=null) {
	        	mediaPlayer.draw(canvas);
			}
	        if (messageDialog.getIsOpen()) {
	        	messageDialog.draw(canvas);
			}
	        if (connectDialog.getIsOpen()) {
				connectDialog.draw(canvas);
			}*/
	        
	        // 파일 리스트를 그린 뒤에 Please wait..메시지를 자동으로 지워준다.
	     	//Control.loggingForMessageBox.setHides(true);
		}
		else {
			/*if (menuDir.getIsOpen()) {
				menuDir.draw(canvas);
			}
	        if (menuFunc.getIsOpen()) {
				menuFunc.draw(canvas);
			}
	        if (menuFileType!=null && menuFileType.getIsOpen()) {
	        	menuFileType.draw(canvas);
			}	        
	        if (mediaPlayer!=null) {
	        	mediaPlayer.draw(canvas);
			}
	        if (messageDialog.getIsOpen()) {
	        	messageDialog.draw(canvas);
			}
	        if (connectDialog.getIsOpen()) {
				connectDialog.draw(canvas);
			}*/
	        
		}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
    	}
		}
    }
	
	public void open(OnTouchListener listener, String title) {
		// 키보드의 리스너가 겹치므로 키보드의 기존 리스너를 보관하고 있다가 대화상자가 닫힐 시
		// 환원한다.
		if (!getIsOpen()) {
			this.Text = title;
			if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
				WasKeyboardHiddenBeforeOpen = keyboard .hides;
				oldKeyboardListener = keyboard.backUp();
				/*if (isForReadingOrSaving==false) {
					keyboard.setOnTouchListener(editText);
				}*/
				
				// menuFileList가 Up, Down, PgUp, PgDn키의 이벤트를 필요로 하므로
				// FileDialog가 키보드의 이벤트를 받아서 눌린 키에 따라 그 이벤트를 중개한다.
				if (isMaximized()==false) {
					changeBoundsOfKeyboard(bounds);
					keyboard.setOnTouchListener(editText);
					if (keyboard.getHides()==true) {
						keyboard.setHides(false);	// 키보드를 전면에 보이도록 한다.컨트롤스택 참조
					}
				}
			}
						
			super.open(true);
			setOnTouchListener(listener);
		}
	}
	
	/*public void setActive() {
		// 키보드의 리스너가 겹치므로 키보드의 기존 리스너를 보관하고 있다가 대화상자가 닫힐 시
		// 환원한다.
		oldListener = keyboard.setOnTouchListener(editText);
	}*/
	
	/** bounds가 바뀔 때 호출, setHides에서 호출*/
	public void backUpBounds() {
		if (prevSize!=null) prevSize.copy(bounds);
		else prevSize = new Rectangle(bounds);
	}
    
    @Override
    public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
    	boolean r=false;
    	if (event.actionCode==MotionEvent.ActionDown || event.actionCode==MotionEvent.ActionDoubleClicked) {
	    	r = super.onTouch(event, scaleFactor);
	    	if (!r) return false;
	    	r = editText.onTouch(event, scaleFactor);
	    	if (r) {
	    		if (CommonGUI_SettingsDialog.settings.EnablesScreenKeyboard) {
		    		if (isMaximized()==true) {
		    			isFullScreen = false;
			    		//canSelectFileType = false;
						isForViewing = false;
						setScaleValues();
						
						Rectangle newBounds = new Rectangle(0, 0, Control.view.getWidth(), prevSize.height);
						
						changeBounds(newBounds);
						setHides(false);
						changeBoundsOfKeyboard(newBounds);
						CommonGUI.keyboard.setHides(false);
		    		}
		    		else {
		    			changeBoundsOfKeyboard(bounds);
		    			CommonGUI.keyboard.setHides(false);
		    		}
	    		}
	    		return true;
	    	}
	    	r = buttonDir.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	r = buttonCategory.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	r = buttonTextFormat.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	r = buttonFunc.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	r = buttonMultimedia.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	r = buttonOnOff.onTouch(event, scaleFactor);
	    	if (r) return true;
	    	
	    	int i;
    		for (i=0; i<controls.length; i++) {
    			r = controls[i].onTouch(event, scaleFactor);
    			if (r) return true;
    		}
	    	
	    	if (menuFileList!=null) {
	    		r = menuFileList.onTouch(event, null);
	    		if (r) return true;
	    	}
	    	return true;
    	}
    	else 
    		return false;
    }
    
    int getUniqueRandom() {
    	Random rand = new Random();
    	boolean exist = false;
    	int num=0;
    	do {
	    	num = rand.nextInt(300);
	    	int i;
	    	if (fileList==null) break;
	    	for (i=0; i<fileList.length; i++) {
	    		if (fileList[i].equals(String.valueOf(num))) {
	    			exist = true;
	    			break;
	    		}
	    	}
    	}while(exist);
    	return num;
    }
    
    /** multiselect 가능, 절대경로 파일(디렉토리)을 대상으로 한다.*/
    boolean cutOrCopy(ArrayListString fileList) {
    	int i;
    	fileListOfCutOrCopy = new ArrayList(10);
    	for (i=0; i<fileList.count; i++) {
	    	String absFilename = fileList.getItem(i);
		    File file  = new File(absFilename);
			if (file.isDirectory()==false) {
				//fileListOfCutOrCopy = new ArrayList(5);
				fileListOfCutOrCopy.add(file);
				//isDirectoryOrFileForCutOrCopy = false;
			}
			else {
				ArrayList fileListOfCutOrCopyForDirectory;
				fileListOfCutOrCopyForDirectory = FileHelper.getFileList(absFilename);
				fileListOfCutOrCopy.add(fileListOfCutOrCopyForDirectory);
				//isDirectoryOrFileForCutOrCopy = true;
				
			}
    	}
		return true;
    }
    
    //@SuppressLint("UseValueOf")
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
	    		new Long(FileHelper.getFileSize(sdcard.getAbsolutePath())));
	    	//arrUsableSpace.add(new Long(sdcard.getUsableSpace()));
    	}catch(Exception e) {
    		e.printStackTrace();
			CompilerHelper.printStackTrace(textViewLogBird, e);
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
    	
    	CommonGUI.loggingForNetwork.setText(true, msg, false);
    	CommonGUI.loggingForNetwork.setHides(false);
    }
    
    /** multiselect 가능, 절대경로 파일(디렉토리)을 대상으로 한다.*/
    void getProperties(ArrayListString fileList) {
    	int i;
    	long size=0;
    	int fileCount=0;
    	long lastModified;
    	String msg;
    	
    	if (fileList.count==1) {
    		String absFilename = fileList.getItem(0);
		    File file  = new File(absFilename);
		    
		    if (file.isDirectory()==false) {
		    	size = file.length();
		    	fileCount = 1;
		    }
		    else {
		    	//size += FileHelper.getFileSize(absFilename, true);
		    	//fileCount += FileHelper.getFileCount(absFilename, true);
		    	SizeAndCount sizeAndCount = FileHelper.getFileSizeAndCount(absFilename);
		    	size += sizeAndCount.size;
		    	fileCount += sizeAndCount.count;
		    }
    		
    		lastModified = file.lastModified();
    		
    		//Date.setCountry(1);
			Date.addUserTime(1,9,0,1);
    		com.gsoft.common.Util.Date date = new com.gsoft.common.Util.Date(lastModified);
    		String strLastModified = date.getMonth() + "-" +
    				date.getDate() + "-" + date.getYear() + " " + 
    				date.getHour(false) + "h" + ":" + date.getMin() + "m";
    		
    		
    		/*Date date = new Date(lastModified);
    		int month = date.getMonth();
    		int day = date.getDate();
    		int year = date.getYear();
    		String strLastModified = month + "-" + day + "-" + year;*/
    		
    		/*Calendar cal = Calendar.getInstance();
    		//cal.setTimeInMillis(lastModified);
    		cal.setTimeInMillis(System.currentTimeMillis());
    		
    		int month = cal.get(Calendar.MONTH) + 1;
    		if (month%12==0) {
    			month = 12;
    		}
    		else {
    			if (month>12) {
    	    		int mok = month / 12;
    	    		month -= mok * 12;
        		}
    		}
    		String strLastModified = month + "-" +
    				cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.YEAR);*/
    		
    		
    		    		
    		msg = "Path : "+absFilename + "\n" + 
    				"Size : "+size + "\n" + 
    				"File count : "+fileCount + "\n" +  
    				"Last Modified : "+strLastModified;
    	}
    	else {
	    	for (i=0; i<fileList.count; i++) {
		    	String absFilename = fileList.getItem(i);
			    SizeAndCount sizeAndCount = FileHelper.getFileSizeAndCount(absFilename);
		    	size += sizeAndCount.size;
		    	fileCount += sizeAndCount.count;
	    	}
	    	msg = "Size : "+size + "\n" + 
	    			"File count : "+fileCount;
    	}
    	
    	CommonGUI.loggingForNetwork.setText(true, msg, false);
    	CommonGUI.loggingForNetwork.setHides(false);
    }
    
    /** 시간이 걸리는 paste작업을 스레드로 활용하여 처리한다.*/
    static class ThreadPaste extends Thread {
    	FileDialog fileDialog;
    	String curDir;
    	Category category;
    	String filename;
    	ArrayList fileListOfCutOrCopy;
    	boolean cutOrCopy;
    	
    	ThreadPaste(FileDialog fileDialog,	String curDir, Category category, String filename,
    			ArrayList fileListOfCutOrCopy, boolean cutOrCopy) {
    		this.fileDialog = fileDialog;
    		this.curDir = curDir;
    		this.category = category;
    		this.filename = filename;
    		this.fileListOfCutOrCopy = fileListOfCutOrCopy;
    		this.cutOrCopy = cutOrCopy;
    	}
    	
    	public void run() {
    		FileDialog.paste_sub(fileDialog, curDir, category, filename, fileListOfCutOrCopy, cutOrCopy);
    	}
    }
    
    /** 시간이 걸리는 paste작업을 스레드로 처리하기 위해 fileListOfCutOrCopy등의 복사작업을 한 후
     * 스레드를 생성하여 처리한다.
     * @param cutOrCopy
     * @return
     */
    boolean paste(boolean cutOrCopy) {
    	String curDir = this.curDir.substring(0, this.curDir.length());
    	Category category = this.category;
    	String filename = null;
    	if (this.filename!=null) {
    		filename = this.filename.substring(0, this.filename.length());
    	}
    	ArrayList fileList = new ArrayList(this.fileListOfCutOrCopy.count);
    	int i, j;
    	for (i=0; i<this.fileListOfCutOrCopy.count; i++) {
    		Object o = this.fileListOfCutOrCopy.getItem(i);
    		if (o instanceof ArrayList) { //디렉토리
    			ArrayList d = (ArrayList)o;
    			ArrayList directory = new ArrayList(d.count);
    			for (j=0; j<d.count; j++) {
    				File file = (File) d.getItem(j);
    				directory.add(new File(file.getAbsolutePath()));
    			}
    			fileList.add(directory);
    		}
    		else { // 파일
    			File file = (File) o;
    			fileList.add(new File(file.getAbsolutePath()));
    		}
    	}
    	ThreadPaste threadPaste = new ThreadPaste(this, curDir, category, 
    			filename, fileList, cutOrCopy);
    	threadPaste.start();
    	return true;
    }
    
  
    /** multiselect 가능, 절대경로 파일(디렉토리)을 대상으로 한다.*/
    static boolean paste_sub(FileDialog fileDialog, String curDir, Category category, 
    		String filename, ArrayList fileListOfCutOrCopy, boolean cutOrCopy) {
    	String msg="";
    	if (cutOrCopy) {
	    	if (fileListOfCutOrCopy!=null && fileListOfCutOrCopy.count>0) {
	    		int c;
	    		for (c=0; c<fileListOfCutOrCopy.count; c++) {
	    			Object item = fileListOfCutOrCopy.getItem(c);
	    			boolean isDirectoryOrFileForCut =  
	    					(item instanceof ArrayList) ? true : false;
		    		if (isDirectoryOrFileForCut) {	// cut directory
		        		String relativePath="";
		        		ArrayList list = (ArrayList)item;
		        		int i;
		        		for (i=0; i<list.count; i++) {
		        			if (i==0) {
		        				File file = (File) list.getItem(i);
		        				if (file.isDirectory()) {
		    	            		String filePath = file.getAbsolutePath();
		    	            		String fn = FileHelper.getFilename(filePath);
		    	            		relativePath = filePath.substring(filePath.indexOf(fn));
		    	            		File newFile = new File(curDir+relativePath);
				            		boolean r = file.renameTo(newFile);
				            		if (!r) {
				            			/*msg += " " + filename + " cut failed.";
				            			Control.loggingForMessageBox.setText(true, msg, false);
				    	            	Control.loggingForMessageBox.setHides(false);
				    	            	return false;*/
				            		}
		        				}
		        			}
		        			else {
			            		File file = (File) list.getItem(i);
			            		String filePath = file.getAbsolutePath();
			            		String relativeFilename = filePath.substring(filePath.indexOf(relativePath));
			            		File newFile = new File(curDir+relativeFilename);
			            		boolean r = file.renameTo(newFile);
			            		if (!r) {
			            			/*msg += " " + filename + " cut failed.";
			            			Control.loggingForMessageBox.setText(true, msg, false);
			    	            	Control.loggingForMessageBox.setHides(false);
			    	            	return false;*/
			            		}
		        			}
		        		}//for (i=0; i<list.count; i++) {
		    		} //if (isDirectoryOrFileForCut) {	// cut directory
		    		else {	// cut file
		    			File file = (File) item;
	            		String filePath = file.getAbsolutePath();
	            		String fn = FileHelper.getFilename(filePath);
	            		File newFile = new File(curDir+fn);
	            		boolean r = file.renameTo(newFile);
	            		if (!r) {
	            			/*msg += " " + filename + " cut failed.";
	            			Control.loggingForMessageBox.setText(true, msg, false);
	    	            	Control.loggingForMessageBox.setHides(false);
	    	            	return false;*/
	            		}
		    		}
		    		//createAndSetFileListButtons(curDir, category);
	    		}// for (c=0; c<fileListOfCutOrCopy.count; c++) {
	    		fileDialog.createAndSetFileListButtons(curDir, category);
	    	}//if (fileListOfCutOrCopy!=null && fileListOfCutOrCopy.count>0) {
    	}//if (cutOrCopy) {
    	else {
	    	if (fileListOfCutOrCopy!=null && fileListOfCutOrCopy.count>0) {
	    		int c;
	    		byte[] buf = new byte[1000];
	    		for (c=0; c<fileListOfCutOrCopy.count; c++) {
	    			Object item = fileListOfCutOrCopy.getItem(c);
	    			boolean isDirectoryOrFile =  
	    					(item instanceof ArrayList) ? true : false;
	    		
		    		int i;
		    		if (isDirectoryOrFile==false) {	// copy file
		    			FileInputStream inStream = null; 
	            		FileOutputStream outStream = null;
	            		BufferedInputStream binStream = null; 
	            		BufferedOutputStream boutStream = null;
	        			try {
		            		File file = (File) item;
		            		String fn = FileHelper.getFilename(file.getAbsolutePath());
		            		File newFile = new File(curDir+fn);
		            		inStream = new FileInputStream(file); 
		            		outStream = new FileOutputStream(newFile);
		            		binStream = new BufferedInputStream(inStream); 
		            		boutStream = new BufferedOutputStream(outStream);
		            		FileHelper.move(buf, binStream, boutStream);
	        			}catch(Exception e2) {
	        				msg += " " + filename + " copy failed.";
	        				CommonGUI.loggingForMessageBox.setText(true, msg, false);
	        				CommonGUI.loggingForMessageBox.setHides(false);	
	    	            	
	    	            	e2.printStackTrace();
	    					CompilerHelper.printStackTrace(textViewLogBird, e2);
	    	            	return false;
	        			}
	        			finally {
	        				FileHelper.close(inStream);
	        				FileHelper.close(outStream);
	        				FileHelper.close(binStream);
	        				FileHelper.close(boutStream);
	        			}
		    		} // copy file
		    		else {	// copy directory
		    			String relativePath="";
		    			ArrayList list = (ArrayList)item;
		    			//byte[] buf = new byte[1000];
		    			for (i=0; i<list.count; i++) {
		    				FileInputStream inStream = null; 
		            		FileOutputStream outStream = null;
		            		BufferedInputStream binStream = null; 
		            		BufferedOutputStream boutStream = null;
		            		File file = (File) list.getItem(i);
		            		try {
		        				if (i==0) {		        					
		            				if (file.isDirectory()) {
			    	            		String filePath = file.getAbsolutePath();
			    	            		String fn = FileHelper.getFilename(filePath);
			    	            		relativePath = filePath.substring(filePath.indexOf(fn));
			    	            		File newFile = new File(curDir+relativePath);
					            		boolean r2 = newFile.mkdir();
					            		if (!r2) {
					            			msg += " " + fn + " mkdir failed.";
					            			CommonGUI.loggingForMessageBox.setText(true, msg, false);
					            			CommonGUI.loggingForMessageBox.setHides(false);
					    	            	//return false;
					            		}
		            				}
		        				}
		        				else {  
				            		String filePath = file.getAbsolutePath();
				            		String relativeFilename = filePath.substring(filePath.indexOf(relativePath));
				            		File newFile = new File(curDir+relativeFilename);
				            		if (file.isDirectory()) {
				            			newFile.mkdir();
				            		}
				            		else {				            		
					            		inStream = new FileInputStream(file); 
					            		outStream = new FileOutputStream(newFile);
					            		binStream = new BufferedInputStream(inStream); 
					            		boutStream = new BufferedOutputStream(outStream);
					            		FileHelper.move(buf, binStream, boutStream);
				            		}
		        				}
		        			}catch(Exception e2) {
		        				msg += " " + filename + " copy failed.";
		        				CommonGUI.loggingForMessageBox.setText(true, msg, false);
		        				CommonGUI.loggingForMessageBox.setHides(false);
		    	            			    	            	
		    	            	e2.printStackTrace();
		    					CompilerHelper.printStackTrace(textViewLogBird, e2);
		    	            	return false;
		        			}
		        			finally {
		        				FileHelper.close(inStream);
		        				FileHelper.close(outStream);
		        				FileHelper.close(binStream);
		        				FileHelper.close(boutStream);
		        			}
		        		} // for (i=0; i<list.count; i++) {
		    			
		    		} // if (isDirectoryOrFileForCutOrCopy==true)
	    		}//for (c=0; c<fileListOfCutOrCopy.count; c++) {
	    		fileDialog.createAndSetFileListButtons(curDir, category);
	    	}//if (fileListOfCutOrCopy!=null && fileListOfCutOrCopy.count>0) {
    	} // if (!cutOrCopy)
    	CommonGUI.loggingForMessageBox.setText(true, "Paste completed", false);
    	CommonGUI.loggingForMessageBox.setHides(false);
    	Control.view.postInvalidate();
    	return true;
    } // paste
    
    void enableMultiSelect(boolean b, boolean initSelect) {
    	if (!initSelect) {
	    	if (b) {
	    		int i;
	        	for (i=0; i<menuFileList.buttons.length; i++) {
	        		menuFileList.buttons[i].toggleable = true;
	        	}
	    	}
	    	else {
	    		int i;
	        	for (i=0; i<menuFileList.buttons.length; i++) {
	        		menuFileList.buttons[i].toggleable = false;
	        	}
	    	}
    	}
    	else {
    		if (b) {
	    		int i;
	        	for (i=0; i<menuFileList.buttons.length; i++) {
	        		menuFileList.buttons[i].toggleable = true;
	        		menuFileList.buttons[i].isSelected = false;
	        	}
	    	}
	    	else {
	    		int i;
	        	for (i=0; i<menuFileList.buttons.length; i++) {
	        		menuFileList.buttons[i].toggleable = false;
	        		menuFileList.buttons[i].isSelected = false;
	        	}
	    	}
    	}
    }

    public void cancel() {
    	super.cancel();
    	// cancel버튼이 눌릴 시 리시버에 전달
    	if (listener!=null)
    		listener.onTouchEvent(this, null);
    }
    
    public boolean isPlaying() {
    	if (mediaPlayer!=null) {
    		return mediaPlayer.isPlaying();
    	}
    	if (Wifi.isRunning) {
    		return true;
    	}
    	if (this.recorder!=null && this.recorder.isRunning) {
    		return true;
    	}
    	
    	return false;
    }
    
    public void listenToMusic(ArrayListString fileListOfMultiSelect, PlayListAndCurSongInfo info) {
    	menuMultimedia.open(false);

    	if (fileListOfMultiSelect==null) return;
    	//this.mPlayList = fileListOfMultiSelect;
    	this.mPlayList = fileListOfMultiSelect.clone();
    	
    	if (fileListOfMultiSelect.count>0) {
        	cutOrCopy(fileListOfMultiSelect);
        	menuFunc.buttons[6].isSelected = false;
        	enableMultiSelect(false, false);
        	isCutOrCopy = false;
        	
        	if (isFullScreen) {
            	if (mediaPlayer==null) {
            		int x, y, w, h;
            		w = bounds.width;
            		h = buttonFunc.bounds.y;
            		x = 0;
            		y = 0;
            		Rectangle mediaBounds = new Rectangle(x,y,w,h); 
            		mediaPlayer = new MediaPlayerDel(view, mediaBounds);
            		
            		mediaPlayer.initialize(fileListOfCutOrCopy, info);
            		/*if (info!=null) {	            			
            			mediaPlayer.play(info.seekOfCurSong);
            		}*/
            		mediaPlayer.play();
            		//if (r) menuFunc.buttons[9].isSelected = true;
            		//else menuFunc.buttons[9].isSelected = false;
            	}
            	else {
            		mediaPlayer.initialize(fileListOfCutOrCopy, info);
            		/*if (info!=null) {	            			
            			mediaPlayer.play(info.seekOfCurSong);
            		}*/
            		mediaPlayer.play();
            		//if (r) menuFunc.buttons[9].isSelected = true;
            		//else menuFunc.buttons[9].isSelected = false;
            	}
        	}
        	
    	}
    	else {
    		CommonGUI.loggingForMessageBox.setText(true, "Select a file(s).", false);
    		CommonGUI.loggingForMessageBox.setHides(false);
    	}
    }
    
	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub	
		
		if (sender instanceof Button) {
			Button button = (Button)sender;
			if (button.iName==controls[0].iName)	// OK
            {
				//state = State.Normal;				
				filename = editText.getText().str;
				if (filename.equals("")) {
					if (!isFullScreen) {
						CommonGUI.loggingForMessageBox.setText(true, "Input file name.", false);
						CommonGUI.loggingForMessageBox.setHides(false);
		            	return;
					}
				}
				super.ok();
				// ok버튼이 눌릴 시 리시버에 전달
				if (listener!=null)
					listener.onTouchEvent(this, e);
				//menuFunc.buttons[9].isSelected = false;
			
            }
            else if (button.iName==controls[1].iName) // Cancel
            {
            	//state = State.Normal;
            	cancel();
            	//menuFunc.buttons[9].isSelected = false;
            }
            else if (button.iName==buttonOnOff.iName)
            {
            	if (buttonOnOff.isSelected) {
            		PowerManagement.keepScreenOn();            		
            	}
            	else {
            		PowerManagement.clearScreenOn();
            		boolean hasWakeLock = false;
            		hasWakeLock = isPlaying();
            		
            		if (!hasWakeLock) {
            			PowerManagement.releaseWakeLock();
            		}
            		else {
            			PowerManagement.getPartialWakeLock(view.getContext());
            		}
            	}
            	
            	
            }
            else if (button.iName==buttonDir.iName)
            {
            	//state = State.Normal;
            	menuDir.open(true); 
            }
            else if (button.iName==buttonTextFormat.iName)
            {
            	//state = State.Normal;
            	menuTextFormat.open(true); 
            }
            else if (button.iName==buttonCategory.iName) {
            	//state = State.Normal;
            	if (canSelectFileType) menuFileType.open(true);
            }
            else if (button.iName==buttonFunc.iName) {
            	//state = State.Normal;
            	menuFunc.open(true);
            }
            else if (button.iName==buttonMultimedia.iName) {
            	//state = State.Normal;
            	menuMultimedia.open(true);
            }
			// "Make Dir", "Delete", "Rename", "Cut", "Copy", "Paste", "Send a file", "MultiSelect", "Listen to music"
			//"Make Dir", "Delete", "Rename", 
			//"Cut", "Copy", "Paste", "MultiSelect", "Sort", "Storage", "Properties" 
            else if (button.iName==menuFunc.buttons[0].iName) { // Make Dir
            	boolean r;
            	menuFunc.open(false);
            	File file = new File(curDir+getUniqueRandom());
            	r = file.mkdir();            	
            	if (!r) 	{
            		CommonGUI.loggingForMessageBox.setText(true, "Directory not created", false);
            		CommonGUI.loggingForMessageBox.setHides(false);
            	}
            	else {
            		createAndSetFileListButtons(curDir, category);
            	}
            	
            }
            else if (button.iName==menuFunc.buttons[1].iName) { // Delete
            	state = State.Delete;
            	menuFunc.open(false);
            	if (fileListOfMultiSelect.count>0) {
            		messageDialog.setText("Delete this file(s)? ");
    				messageDialog.open();
            	}
            	else {
            		CommonGUI.loggingForMessageBox.setText(true, "Select a file(s).", false);
            		CommonGUI.loggingForMessageBox.setHides(false);
            	}
            	
            }
            else if (button.iName==menuFunc.buttons[2].iName) {	// Rename
            	state = State.Rename;
            	menuFunc.open(false);
            	if (fileListOfMultiSelect.count!=1) {
            		CommonGUI.loggingForMessageBox.setText(true, "Select only a file(folder).", false);
            		CommonGUI.loggingForMessageBox.setHides(false);
            		state = State.Normal;
            		return;
            	}
            	String absFilename = fileListOfMultiSelect.getItem(0);
				messageDialog.setText("Rename a file " + 
						FileHelper.getFilename(absFilename) + " to " + 
						editText.getText() + " ?");
				messageDialog.open();
				mAbsFilename = absFilename;
            }
            else if (button.iName==menuFunc.buttons[3].iName) {	// Cut
            	state = State.Cut;
            	menuFunc.open(false);
            	if (fileListOfMultiSelect.count>0) {
            		cutOrCopy(fileListOfMultiSelect);
                	menuFunc.buttons[6].isSelected = false;
                	enableMultiSelect(false, false);
                	isCutOrCopy = true;
            	}
            	else {
            		CommonGUI.loggingForMessageBox.setText(true, "Select a file(s).", false);
            		CommonGUI.loggingForMessageBox.setHides(false);
            	}            	
				state = State.Normal;
            }
            else if (button.iName==menuFunc.buttons[4].iName) {	// Copy
            	state = State.Copy;
            	menuFunc.open(false);
            	if (fileListOfMultiSelect.count>0) {
	            	cutOrCopy(fileListOfMultiSelect);
	            	menuFunc.buttons[6].isSelected = false;
	            	enableMultiSelect(false, false);
	            	isCutOrCopy = false;
            	}
            	else {
            		CommonGUI.loggingForMessageBox.setText(true, "Select a file(s).", false);
            		CommonGUI.loggingForMessageBox.setHides(false);
            	}
            	state = State.Normal;
            }
            else if (button.iName==menuFunc.buttons[5].iName) {	// Paste
            	menuFunc.open(false);
            	if (fileListOfCutOrCopy!=null && fileListOfCutOrCopy.count>0) {
	            	if (isCutOrCopy) paste(true);
	            	else paste(false);
	            	fileListOfMultiSelect.destroy();
	            	fileListOfCutOrCopy.reset();
            	}
            	else {
            		CommonGUI.loggingForMessageBox.setText(true, "Cut or copy a file(s).", false);
            		CommonGUI.loggingForMessageBox.setHides(false);
            	}            	
            }
            else if (button.iName==menuFunc.buttons[6].iName) {	// MultiSelect
            	menuFunc.open(false);
            	fileListOfMultiSelect.destroy();
            	if (menuFunc.buttons[6].isSelected) {
	            	state = State.MultiSelect;	            	
	            	enableMultiSelect(true, true);
            	}
            	else {
            		state = State.Normal;	            	
            		enableMultiSelect(false, true);
            	}
            }
            else if (button.iName==menuFunc.buttons[7].iName) {	// Sort
            	menuFunc.open(false);
            	menuSort.open(true);
            }
            else if (button.iName==menuFunc.buttons[8].iName) {	// Storage
            	menuFunc.open(false);
            	getStorage();
            	
            }
            else if (button.iName==menuFunc.buttons[9].iName) {	// Properties
            	menuFunc.open(false);
            	getProperties(fileListOfMultiSelect);
            	
            }
			
			
			
			
			// "Send a file", "Start receiver", 
			// "Listen to music", "Video", "Record sound", "Play record", "Sound Control Menu", "Help me"
            else if (button.iName==menuMultimedia.buttons[0].iName) {	// Send a file
            	state = State.SendFile;
            	menuMultimedia.open(false);
            	
            	if (fileListOfMultiSelect.count>0) {
	            	connectDialog.setOnTouchListener(this);
					connectDialog.open();
					this.hides = true; // 키보드가 보이도록 한다.
					keyboard.hides = false;
            	}
            	else {
            		CommonGUI.loggingForMessageBox.setText(true, "Select a file(s).", false);
            		CommonGUI.loggingForMessageBox.setHides(false);
            	}
				state = State.Normal;
            }
            else if (button.iName==menuMultimedia.buttons[1].iName) {	// Start receiver
            	menuMultimedia.open(false);
            	startReceiver();
            }            
            else if (button.iName==menuMultimedia.buttons[2].iName) {	// Listen to music
            	menuMultimedia.open(false);
            	mPlayList =  fileListOfMultiSelect;
            	this.playListAndCurSongInfo = null;
            	listenToMusic(fileListOfMultiSelect, this.playListAndCurSongInfo);
            }
            else if (button.iName==menuMultimedia.buttons[3].iName) {	// Watch video
            	menuMultimedia.open(false);
            	if (fileListOfMultiSelect!=null) {
	            	String videoFilePath = MediaPlayerDel.getVideoFile(fileListOfMultiSelect);
	            	if (videoFilePath==null) {
	            		CommonGUI.loggingForMessageBox.setText(true, "Select a video file(s).", false);
	            		CommonGUI.loggingForMessageBox.setHides(false);
	            		return;
	            	}
	            	Intent intent = new Intent();
	            	intent.setAction(Intent.ACTION_VIEW);
	            	File videoFile = new File(videoFilePath);
	            	Uri uriOfVideoFile = Uri.fromFile(videoFile);
	            	// "video/*"
	            	intent.setDataAndType(uriOfVideoFile, "video/*");
	            	Control.activity.startActivity(intent);
            	}
            	
            }
            else if (button.iName==menuMultimedia.buttons[4].iName) {	// install a package
            	menuMultimedia.open(false);
            	if (fileListOfMultiSelect!=null && fileListOfMultiSelect.count>0) {
            		String filePath = fileListOfMultiSelect.getItem(0);
            		String ext = FileHelper.getExt(filePath);
            		if (ext.equals(".apk")==false) {
            			CommonGUI.loggingForMessageBox.setText(true, "Select a package file.", false);
            			CommonGUI.loggingForMessageBox.setHides(false);
            			return;
            		}
            		
            		Intent intent = new Intent();
                	//intent.setAction(Intent.ACTION_VIEW);
                	intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
	            	File file = new File(filePath);
	            	Uri uriOfFile = Uri.fromFile(file);
	            	intent.setData(uriOfFile);
	            	Control.activity.startActivity(intent);
            		
            	}
            	
            }
            else if (button.iName==menuMultimedia.buttons[5].iName) {	// Record sound
            	//menuMultimedia.open(false);
            	if (recorder==null)
            		recorder = new MediaRecorderDel();
            	if (menuMultimedia.buttons[5].isSelected) {
            		if (recorder.isInitialized==false) {
            			recorder.initialize();
            			recorder.start();
            		}
            		else {
            			recorder.start();
            		}
            	}
            	else {
            		recorder.stop();
            	}
	            	
            	
            }
            else if (button.iName==menuMultimedia.buttons[6].iName) {	// Play record
            	menuMultimedia.open(false);
            	if (mediaPlayer==null) {
            		int x, y, w, h;
            		w = bounds.width;
            		h = buttonFunc.bounds.y;
            		x = 0;
            		y = 0;
            		Rectangle mediaBounds = new Rectangle(x,y,w,h); 
            		mediaPlayer = new MediaPlayerDel(view, mediaBounds);
            		
            		ArrayList list = new ArrayList(1);
            		list.add(new File(MediaRecorderDel.recordFile));
            		mediaPlayer.initialize(list, null);
            		//if (r) menuFunc.buttons[9].isSelected = true;
            		//else menuFunc.buttons[9].isSelected = false;
            	}
            	else {
            		ArrayList list = new ArrayList(1);
            		list.add(new File(MediaRecorderDel.recordFile));
            		mediaPlayer.initialize(list, null);
            		//if (r) menuFunc.buttons[9].isSelected = true;
            		//else menuFunc.buttons[9].isSelected = false;
            	}
            	
            } 
            else if (button.iName==menuMultimedia.buttons[7].iName) {	// Sound Control Menu            	
            	menuMultimedia.open(false);
            	if (mediaPlayer!=null && mediaPlayer.menuSoundControl!=null) {
	            	//if (menuFunc.buttons[9].isSelected)
	            		mediaPlayer.enableSoundControl(true);
	            	//else 
	            	//	mediaPlayer.enableSoundControl(false);
            	}
            	else {
            		CommonGUI.loggingForMessageBox.setText(true, "Touch the button, Listen to music", false);
            		CommonGUI.loggingForMessageBox.setHides(false);
            	}
            }
            else if (button.iName==menuMultimedia.buttons[8].iName) {	// Help me            	
            	menuMultimedia.open(false);
            	menuHelp.open(true);
            }
			
			
			// "Sort by name, ascending", "Sort by name, descending",
			// "Sort by time, ascending", "Sort by time, descending"
            else if (button.iName==menuSort.buttons[0].iName) {	// Sort by name, ascending            	
            	menuSort.open(false);
            	Sort.merge_sort(fileList, 0, fileList.length-1, true);
            	Button[] buttonsSorted = getFileListButtons(fileList);
            	if (menuFileList!=null) {
        			menuFileList.setButtons(buttonsSorted);
        		}
            }
            else if (button.iName==menuSort.buttons[1].iName) {	// Sort by name, ascending            	
            	menuSort.open(false);
            	Sort.merge_sort(fileList, 0, fileList.length-1, false);
            	Button[] buttonsSorted = getFileListButtons(fileList);
            	if (menuFileList!=null) {
        			menuFileList.setButtons(buttonsSorted);
        		}
            }
            else if (button.iName==menuSort.buttons[2].iName) {	// Sort by time, ascending            	
            	menuSort.open(false);
            	SortByTime[] arrSortByTime = new SortByTime[fileList.length];
            	int i;
            	for (i=0; i<arrSortByTime.length; i++) {
            		File file = new File(curDir+fileList[i]);
            		arrSortByTime[i] = new SortByTime(file.lastModified(), fileList[i]); 
            	}
            	Sort.merge_sort(arrSortByTime, 0, arrSortByTime.length-1, true);
            	for (i=0; i<arrSortByTime.length; i++) {
            		fileList[i] = arrSortByTime[i].filename;
            	}
            	Button[] buttonsSorted = getFileListButtons(fileList);
            	if (menuFileList!=null) {
        			menuFileList.setButtons(buttonsSorted);
        		}
            }
            else if (button.iName==menuSort.buttons[3].iName) {	// Sort by time, descending            	
            	menuSort.open(false);
            	SortByTime[] arrSortByTime = new SortByTime[fileList.length];
            	int i;
            	for (i=0; i<arrSortByTime.length; i++) {
            		File file = new File(curDir+fileList[i]);
            		arrSortByTime[i] = new SortByTime(file.lastModified(), fileList[i]); 
            	}
            	Sort.merge_sort(arrSortByTime, 0, arrSortByTime.length-1, false);
            	for (i=0; i<arrSortByTime.length; i++) {
            		fileList[i] = arrSortByTime[i].filename;
            	}
            	Button[] buttonsSorted = getFileListButtons(fileList);
            	if (menuFileList!=null) {
        			menuFileList.setButtons(buttonsSorted);
        		}
            }
			
			
            
            else {//MenuDir handler
            	
            	handleMenuDir(button);
            	
            	handleHelpMe(button);
            	
            	handleMenuFileType(button);
            	
            	
            	
            } // else {
		}//if (sender instanceof Button)
		else if (sender instanceof MenuWithScrollBar) {
			// 폴더나 파일을 사용자가 선택했을 때 호출된다.
			MenuWithScrollBar menu = (MenuWithScrollBar)sender;			
			if (state==State.Delete) {
				editText.setText(0, new CodeString(menu.selectedButtonName, editText.textColor));
				if (menu.selectedButtonName.equals("Up")==false) {
					String absFilename = curDir + menu.selectedButtonName;					
					messageDialog.setText("Delete this file? "+menu.selectedButtonName);
					messageDialog.open();
					mAbsFilename = absFilename;
				}
				else {
					state = State.Normal;
				}
			}
			else if (state==State.Rename) {
				if (menu.selectedButtonName.equals("Up")==false) {
					String absFilename = curDir + menu.selectedButtonName;
					messageDialog.setText("Rename a file " + 
							menu.selectedButtonName + " to " + 
							editText.getText() + " ?");
					messageDialog.open();
					mAbsFilename = absFilename;
				}
				else {
					state = State.Normal;
				}
			}
			else if (state==State.Normal || state==State.MultiSelect){
				editText.isSelecting = false;
				if (menu.selectedButtonName==null) { // menuFileList의 공백영역을 터치시
					editText.setText(0, new CodeString("", editText.textColor));					
					fileListOfMultiSelect.destroy();
					return;
				}
				editText.setText(0, new CodeString(menu.selectedButtonName, editText.textColor));
				String absFilename;
				if (menu.selectedButtonName.equals("Up")) {
					// 윈도우즈에서 루트일 경우 up을 클릭했을때는 
					// absFilename가 IO.LocalComputer가 된다.
					absFilename = IO.FileHelper.upDirectory(curDir);
										
					//fileListOfMultiSelect.reset();
					menuFunc.buttons[6].isSelected = false;
					//enableMultiSelect(false, false);
				}
				else {
					if (curDir.equals(IO.LocalComputer)) {
						// 윈도우즈에서 파티션들중 하나를 클릭한 경우
						// absFilename는 "c:\"가 되고 curDir을 루트로 정해준다.
						absFilename = menu.selectedButtonName;
						curDir = absFilename;
					}
					else {
						absFilename = curDir + menu.selectedButtonName;
					}
				}				
					
				boolean multiSelect = menuFunc.buttons[6].isSelected;
				if (!multiSelect) {
					try{
						File file = new File(absFilename);
						// 윈도우즈에서 루트일 경우 up을 클릭했을때는 
						// absFilename가 IO.LocalComputer가 된다.
						// 이때는 파티션들이 버튼들로 생성된다.
						if (absFilename.equals(IO.LocalComputer) || file.isDirectory()) {
							getPermission(file);
							/*boolean r = file.setReadable(true, false);
							if (r==false) {
								
							}
							r = file.setWritable(true, false);
							if (r==false) {
								
							}*/
							/*boolean r = file.setReadOnly();
							if (!r) {
								Log.e("FileDialog-onTouchEvent", "setReadOnly() failed.");
							}*/
							createAndSetFileListButtons(absFilename, category);
							state = State.Normal;
							menuFileList.buttonsOfSelect.reset();
							fileListOfMultiSelect.destroy();
							closePermission();
						}
						else {
							fileListOfMultiSelect.destroy();
							fileListOfMultiSelect.add(absFilename);
						}
					}catch(Exception e1) {
						e1.printStackTrace();
						CompilerHelper.printStackTrace(textViewLogBird, e1);
					}
				}
				else {
					if (menu.selectedButtonName.equals("Up")) {//올 수 없다.
						
					}
					else {
						fileListOfMultiSelect.add(absFilename);
					}
				}
			}
		}
		else if (sender instanceof IntegrationKeyboard) {
			// menuFileList가 Up, Down, PgUp, PgDn키의 이벤트를 필요로 하므로
			// FileDialog가 키보드의 이벤트를 받아서 눌린 키에 따라 그 이벤트를 중개한다.
			IntegrationKeyboard keyboard = (IntegrationKeyboard)sender;
			int i;
			String[] specialKeys = IntegrationKeyboard.SpecialKeys;
			for (i=0; i<specialKeys.length; i++) {
				if (keyboard.key.equals(specialKeys[i])) {
					menuFileList.controlChar(i, charA);
					return;
				}
			}
			editText.onTouchEvent(sender, e);
		}
		
		else if (sender instanceof MessageDialog) {
			MessageDialog dialog = (MessageDialog)sender;
			if (dialog.getIsOK()) {
				if (state==State.Delete) {
					CommonGUI.loggingForMessageBox.setText(true, "Deleting..", false);
					CommonGUI.loggingForMessageBox.setHides(false);
					Control.view.invalidate();
					
					ThreadDelete deleteThread = new ThreadDelete(this, fileListOfMultiSelect);
					deleteThread.start();
					
					fileListOfMultiSelect.destroy();
					//createAndSetFileListButtons(curDir, category);
					// createAndSetFileListButtons 뒤에 Button의 toggleable상태가 원상태로
					// 바뀌기 때문에 MultiSelect상태를 해제해준다.
					menuFunc.buttons[6].isSelected = false;	
					state = State.Normal;
				}
				else if (state==State.Rename) {
					File file = new File(mAbsFilename);
					String filename = editText.getText().str;
					String newFilename = curDir+filename;
					boolean success=false;
					if (filename.equals("Up") || filename.equals("") || mAbsFilename.equals(newFilename))
						success = false;
					else {
						success = file.renameTo(new File(newFilename));
					}
					if (!success) {
						CommonGUI.loggingForMessageBox.setText(true, "Rename failed.", false);
						CommonGUI.loggingForMessageBox.setHides(false);
					}
					createAndSetFileListButtons(curDir, category);
					fileListOfMultiSelect.destroy();
					// createAndSetFileListButtons 뒤에 Button의 toggleable상태가 원상태로
					// 바뀌기 때문에 MultiSelect상태를 해제해준다.
					menuFunc.buttons[6].isSelected = false;
					state = State.Normal;
				}
			}
			
			
		}
		else if (sender instanceof ConnectDialog) { // ok, cancel
			ConnectDialog dialog = (ConnectDialog)sender;
			//serverIpAddress = dialog.ipAddress;
			//doWifi();		// serverIp가 입력되면 wifi를 시작한다.
			if (dialog.ipAddress!=null) {
				wifiThread = new WifiThread(view, dialog.ipAddress, fileListOfMultiSelect);
				
				long totalFileSize=0;
				int totalFileCount=0;
				
				//setWifiState(true, " sendLargeFile started.");
				int i;
				for (i=0; i<fileListOfMultiSelect.count; i++) {
					String absFilename = fileListOfMultiSelect.getItem(i);
					totalFileSize += FileHelper.getFileSize(absFilename);
					totalFileCount += FileHelper.getFileCount(absFilename);
				}
				
				Net.setTotalFileSize(totalFileSize);
				Net.setFileCountToSend(totalFileCount);
				wifiThread.start();
				
				PowerManagement.getPartialWakeLock(view.getContext());
			}
			this.hides = false;
		}
	}
	
	
	void handleMenuFileType(Button button) {
		int i;
		if (menuFileType!=null) {
        	for (i=0; i<namesOfMenuFileType.length; i++) {
        		//if (button.name.equals(namesOfMenuDir[i])) {
        		if (button.iName==menuFileType.buttons[i].iName) {
        			switch(i) {
        			case 0: createAndSetFileListButtons(curDir, Category.Image);break;
        			case 1: createAndSetFileListButtons(curDir, Category.Text);break;
        			case 2: createAndSetFileListButtons(curDir, Category.Custom);break;
        			case 3: createAndSetFileListButtons(curDir, Category.Music);break;
        			case 4: createAndSetFileListButtons(curDir, Category.Video);break;
        			case 5: createAndSetFileListButtons(curDir, Category.All);break;
        			}
        			menuFileType.open(false);
        			break;
        		}
        	}
    	}//if (menuFileType!=null) {
	}
	
	
	void handleMenuDir(Button button) {
		int i;
    	for (i=0; i<namesOfMenuDir.length; i++) {
    		//if (button.name.equals(namesOfMenuDir[i])) {
    		if (button.iName==menuDir.buttons[i].iName) {
    			menuDir.open(false);
    			String absFilename=null;
    			switch(i) {
    			case 0: { // go to app dir
        			//File contextDir = context.getFilesDir();
        			//absFilename = contextDir.getAbsolutePath();
    				absFilename = Control.pathJaneSoft;
        			break;
    			}
    			case 1: {	// "/" directory
    				absFilename = Separator;
        			break;
    			} 
    			case 2: { // go to project dir
    				absFilename = Control.pathProjectSrc/* + Separator + "com" + 
    						Separator + "gsoft" + Separator +
    						"common" + Separator + "gui"*/;
    				break;
    			}
    			case 3: { // go to sdk dir
    				absFilename = Control.pathAndroid;
    				break;
    			}
    			case 4: {
    				CompilerHelper.decompressAndroidAndProjectSrc();            				            				        				
    				
    				
    				createAndSetFileListButtons(Control.pathJaneSoft, category);
    				
    				// CommonGUI_SettingsDialog.settingsDialog의 SDK 디렉토리의 값을
    				// 압축이 풀린 디렉토리 /mnt/sdcard/janeSoft/gsoft 로 바꾸고
    				// backup_Settings에 그 값을 저장한다.
    				absFilename = Control.pathAndroid_Final;
    				CommonGUI_SettingsDialog.settingsDialog.setEditTextDirectory(absFilename);
    				CommonGUI_SettingsDialog.settingsDialog.setSettings();
    				CommonGUI_SettingsDialog.settingsDialog.backupSettings();
    				
    				return;
    			}
    			}//switch
    			try{
    				File file = new File(absFilename);
    				if (file.exists()==false) {
    					CommonGUI.loggingForMessageBox.setText(true, 
    						"Directory not found. Go to the app directory and then load any .java file. If you do, project directory is maden.", false);
    					CommonGUI.loggingForMessageBox.setHides(false);
    					Control.view.invalidate();
    				}
    				else if (file.isDirectory()) {
    					createAndSetFileListButtons(absFilename, category);
    				}
    			}catch(Exception e1) {
    				e1.printStackTrace();
					CompilerHelper.printStackTrace(textViewLogBird, e1);
    			}
    			
    		}//if (button.iName==menuDir.buttons[i].iName) {
    	}//for (i=0; i<namesOfMenuDir.length; i++) {
	}
	
	void handleHelpMe(Button button) {
		int i;
		String filename = null;
    	for (i=0; i<namesOfMenuHelp.length; i++) {
    		//if (button.name.equals(namesOfMenuDir[i])) {
    		if (button.iName==menuHelp.buttons[i].iName) {
    			switch(i) {
    			case 0: {
    				filename = "ListenToMusic.kjy"; 
    				HelpView helpView = new HelpView(); 
    				helpView.setHelpFile(Control.pathHelpFiles + File.separator + filename);
    				helpView.setHides(false);
    				break;
    			}
    			case 1: {
    				filename = "HowToTransferFiles.kjy"; 
    				HelpView helpView = new HelpView(); 
    				helpView.setHelpFile(Control.pathHelpFiles + File.separator + filename);
    				helpView.setHides(false);
    				break;
    			}
    			case 2: {
    				filename = "HowToViewJavaFile.kjy"; 
    				HelpView helpView = new HelpView(); 
    				helpView.setHelpFile(Control.pathHelpFiles + File.separator + filename);
    				helpView.setHides(false);
    				break;
    			}
    			case 3: {
    				filename = "HowToViewClassFile.kjy"; 
    				HelpView helpView = new HelpView(); 
    				helpView.setHelpFile(Control.pathHelpFiles + File.separator + filename);
    				helpView.setHides(false);
    				break;
    			}
    			}
    		}
    	}
	}
	
	/** 시간이 걸리는 delete작업을 스레드로 활용하여 처리한다.*/
	static class ThreadDelete extends Thread {
		ArrayListString fileListOfMultiSelect;
		FileDialog fileDialog;
		
		/** 시간이 걸리는 delete작업을 스레드로 처리하기 위해 fileListOfMultiSelect 복사작업을 한 후
	     * 스레드를 생성하여 처리한다.*/
		ThreadDelete(FileDialog fileDialog, ArrayListString fileListOfMultiSelect) {
			this.fileDialog = fileDialog;
			this.fileListOfMultiSelect = fileListOfMultiSelect.clone();
		}
		public void run() {
			boolean r = false;
			int i;
			for (i=0; i<fileListOfMultiSelect.count; i++) {
				//getPermission(new File(fileListOfMultiSelect.getItem(i)));
				r = FileHelper.delete(fileListOfMultiSelect.getItem(i));
				//closePermission();
				if (!r) break;
			}
			fileDialog.createAndSetFileListButtons(fileDialog.curDir, fileDialog.category);
			if (r) {
				CommonGUI.loggingForMessageBox.setText(true, "Delete completed.", false);
				CommonGUI.loggingForMessageBox.setHides(false);
				Control.view.postInvalidate();
			}
			else {
				CommonGUI.loggingForMessageBox.setText(true, "Delete failed.", false);
				CommonGUI.loggingForMessageBox.setHides(false);
				Control.view.postInvalidate();
			}
		}
		
	}
	
	boolean startReceiver() {	
		
		WifiInfo info=null;
		info = Wifi.startWifi(view);
		short[] shortIpAddress = new short[4];
		byte[] ipAddress;
		InetAddress address=null;
		int port = 3000;
		String wifiState = "";
		String macAddress;
		
		if (info==null) {
			Wifi.setWifiStateSync(false, "Can't start receiver");
			return false;
		}
		else {
			try {
				PowerManagement.getPartialWakeLock(view.getContext());
				
				ipAddress = Wifi.getIpAddress(info.getIpAddress());
				shortIpAddress[0] = (short) (ipAddress[0]&0xff);
				shortIpAddress[1] = (short) (ipAddress[1]&0xff);
				shortIpAddress[2] = (short) (ipAddress[2]&0xff);
				shortIpAddress[3] = (short) (ipAddress[3]&0xff);
				wifiState += " ip : " + shortIpAddress[0]+"."+shortIpAddress[1]+"."+
						shortIpAddress[2]+"."+shortIpAddress[3];
				wifiState += " port : " + port;
				macAddress = info.getMacAddress();
				wifiState += " Mac : " + macAddress;
				
				address = InetAddress.getByAddress(ipAddress);
				
				Wifi.setWifiStateSync(false,wifiState);
				
				
			} catch (Exception e) {
				wifiState += "CustomView"+e.toString();
				e.printStackTrace();
				CompilerHelper.printStackTrace(textViewLogBird, e);
			}
			
			if (serviceThread!=null) killServiceThread();
			
			serviceThread = new ServiceThread(address, port);
			serviceThread.owner = view;
			serviceThread.start();
			
			return true;
		}
	}

	void killServiceThread() {
		if(serviceThread==null) return;
		serviceThread.killThread();
		serviceThread = null;
	}

	void killWifiThread() {
		if(wifiThread==null) return;
		wifiThread.killThread();
		wifiThread = null;
	}
	public void destroy() {
		if (su!=null) su.destroy();
		killWifiThread();
		killServiceThread();
		if (mediaPlayer!=null) {
			mediaPlayer.destroy();
		}
	}

	public void setPlayListAndCurSongInfo(ArrayListString fileList2,
			PlayListAndCurSongInfo playListAndCurSongInfo) {
		// TODO Auto-generated method stub
		this.fileListOfMultiSelect = fileList2;
		this.playListAndCurSongInfo = playListAndCurSongInfo;
		MediaPlayerDel.indexOfFileList_1Dim = playListAndCurSongInfo.indexOfCurSong;
		MediaPlayerDel.curPos = playListAndCurSongInfo.seekOfCurSong;
		MediaPlayerDel.state = MediaPlayerDel.stateFromString( playListAndCurSongInfo.state);
		MediaPlayerDel.allRepeated = playListAndCurSongInfo.allRepeated;
		MediaPlayerDel.isRandomOnOrOff = playListAndCurSongInfo.isRandomOnOrOff;
		
	}
}