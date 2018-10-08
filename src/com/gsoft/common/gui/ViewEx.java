package com.gsoft.common.gui;

import java.awt.Frame;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.gsoft.DataTransfer.pipe.Pipe;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.CommonGUI_SettingsDialog;
import com.gsoft.common.Font;
import com.gsoft.common.IO;
import com.gsoft.common.IO.FileHelper.LanguageAndTextFormat;
import com.gsoft.common.PowerManagement;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.Terminal;
import com.gsoft.common.Timer;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.R.R;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.EditRichText.TextLine;
import com.gsoft.common.gui.SettingsDialog.Settings;
import com.gsoft.common.interfaces.TimerListener;
import com.gsoft.common.Compiler;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

public class ViewEx extends View 
implements TimerListener, com.gsoft.common.interfaces.OnTouchListener {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int width;
	protected int height;

	protected Timer exitTimer;
	protected long tickTimeOfExitTimer = 4 * 60 * 1000;
	protected long finishTimeOfExitTimer = 4 * 60 * 1000;
	
	protected MessageDialog closeDialog;
	
	protected MessageDialog messageDialog;
	
	
	protected static Settings settings = CommonGUI_SettingsDialog.settings;
	
	Pipe.ThreadPipe mPipe;
	
	protected int backColor = Color.LTGRAY;
	
	/** 0:close, 1:minimize, 2:maximize*/
	Control[] controls = new Control[3];
	
	
	
	protected void createCloseDialog() {
		Rectangle boundsOfCloseDialog = new Rectangle(0, 0, (int)(width*0.8f), (int)(height*0.4f));
		boundsOfCloseDialog.x = (width-boundsOfCloseDialog.width) / 2;
		boundsOfCloseDialog.y = (height-boundsOfCloseDialog.height) / 2;
		
		closeDialog = new MessageDialog(this, boundsOfCloseDialog);
		//closeDialog.setOnTouchListener(this);
		closeDialog.setText("Exit this application?");
	}
	
	protected void createMessageDialog(Rectangle bounds, float fontSize, String message) {
		messageDialog = new MessageDialog(this, bounds);
		messageDialog.setFontSize(fontSize);
		//closeDialog.setOnTouchListener(this);
		messageDialog.setText(message);
	}
	
	public void writeFile(int isEditRichTextOrEditText, EditRichText editRichText, EditText editText, Terminal terminal,
			String path) {
		FileOutputStream stream=null;
		BufferedOutputStream bos=null;
		try {
			//Control.loggingForMessageBox.setHides(false);
			//Control.loggingForMessageBox.setText(true, "Saving...", false);
			String filenameExceptExt = FileHelper.getFilenameExceptExt(path);				
			if (isEditRichTextOrEditText==0) {
				path = filenameExceptExt + ".kjy";
			}
			/*else {
				filename = filenameExceptExt + ".txt";
			}*/
			String ext = FileHelper.getExt(path);
			
			File file = new File(path);
			//file.setWritable(true, false);//-->NoSuchMethod Exception
			stream = new FileOutputStream(file);
			bos = new BufferedOutputStream(stream/*, IO.DefaultBufferSize*/);
			
			TextFormat format = TextFormat.UTF_8;
			if (ext.equals(".java")) {
				Menu menuTextSaveFormat = CommonGUI.fileDialog.menuTextFormat;
				format = TextFormat.UTF_8;
		    	if (menuTextSaveFormat.buttons[0].isSelected) {
		    		format = TextFormat.UTF_8;
		    	}
		    	else if (menuTextSaveFormat.buttons[1].isSelected) {
		    		//format = TextFormat.UTF_16;
		    		format = TextFormat.UTF_8;
		    	}
		    	else if (menuTextSaveFormat.buttons[2].isSelected) {
		    		format = TextFormat.MS949_Korean;
		    	}
			}
			else if (ext.equals(".htm") || ext.equals(".html")) {
				format = TextFormat.UTF_8;
			}
			else {
				if (ext.equals(".kjy")) {
					format = TextFormat.UTF_16;
				}
				else if (ext.equals(".txt")) {
					if (CommonGUI_SettingsDialog.settings.textSaveFormat==0) {
						format = TextFormat.UTF_8;
					}
					else if (CommonGUI_SettingsDialog.settings.textSaveFormat==1) {
						//format = TextFormat.UTF_16;
						format = TextFormat.UTF_8;
					}
					else {
						format = TextFormat.MS949_Korean;
					}
				}
			}
			
			if (isEditRichTextOrEditText==0) {
				editRichText.write(bos, format);
			}
			else if (isEditRichTextOrEditText==1) {
				editText.write(bos, format);
			}
			else if (isEditRichTextOrEditText==2) {
				terminal.editText.write(bos, format);
			}
			bos.close();
			stream.close();
			//Control.setModified(true);
			CommonGUI.loggingForMessageBox.setText(true, "Save complete", false);
			CommonGUI.loggingForMessageBox.setHides(false);
			//postInvalidate();
			//Control.view.postInvalidate();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.e("Save Error", e1.toString());
			CommonGUI.loggingForMessageBox.setHides(false);
			CommonGUI.loggingForMessageBox.setText(true, Control.res.getString(R.string.save_error_read_only), false);					
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.e("Save Error", e1.toString());
			CommonGUI.loggingForMessageBox.setHides(false);
			CommonGUI.loggingForMessageBox.setText(true, Control.res.getString(R.string.save_error), false);
		}
		finally {
			FileHelper.close(bos);
			FileHelper.close(stream);
		}
	}
	
	
	/** 스레드에서 이 함수를 호출해야 Control.view.postInvalidate();이 제대로 작동할 수 있다.*/
	public void readFile(int isEditRichTextOrEditText, EditRichText editRichText, EditText_Compiler editText, 
			String path) {
		
		String filenameExceptExt;
		String ext;	
		
		filenameExceptExt = FileHelper.getFilenameExceptExt(path);
		ext = FileHelper.getExt(path);
		path = filenameExceptExt + ext;
		
		if (isEditRichTextOrEditText==0) {
			if (ext.equals(".kjy")==false) {
				CommonGUI.loggingForMessageBox.setText(true, "Can't open the file extension.", false);
				CommonGUI.loggingForMessageBox.setHides(false);
				Control.view.postInvalidate();
				return;
			}
		}
		
		
		TextFormat format = null;
		com.gsoft.common.Compiler_types.Language lang = null;
		
		LanguageAndTextFormat languageAndTextFormat = 
				FileHelper.getLanguageAndTextFormat(path);
		if (languageAndTextFormat!=null) {
			lang = languageAndTextFormat.lang;
			format = languageAndTextFormat.format;
		}
		
		else {	// 일반 .txt 는 format 이 null 이다.
			if (isEditRichTextOrEditText==0) {
				if (ext.equals(".kjy")==false) {
					CommonGUI.loggingForMessageBox.setText(true, "Can't open the file extension.", false);
					CommonGUI.loggingForMessageBox.setHides(false);
					Control.view.postInvalidate();
					return;
				}
				else {
					format = TextFormat.UTF_16;
				}
			}
			else if (isEditRichTextOrEditText==1) {
				if (ext.equals(".kjy")) {
					CommonGUI.loggingForMessageBox.setText(true, "Can't open the file extension.", false);
					CommonGUI.loggingForMessageBox.setHides(false);
					Control.view.postInvalidate();
					return;
				}
				format = null;
			}	
		}
		
		if (ext.equals(".txt") || ext.equals(".java")) {
			CommonGUI_SettingsDialog.settingsDialog.enablesMenuTextFormat = true;
		}
		else {
			CommonGUI_SettingsDialog.settingsDialog.enablesMenuTextFormat = false;
		}
		
		
		boolean r = false;
		if (format!=null) {
			r = load(isEditRichTextOrEditText, editRichText, editText,
					path, format, lang);
			if (!r) CommonGUI_SettingsDialog.settingsDialog.setTextSaveFormat(-1);
			if (r) {
				CommonGUI.loggingForMessageBox.setText(true, "Read complete", false);
				CommonGUI.loggingForMessageBox.setHides(false);
				Control.view.postInvalidate();
				return;
			}
		}
		else {
			r = load(isEditRichTextOrEditText, editRichText, editText,
					path, TextFormat.UTF_8, lang);
			if (r) {
				CommonGUI_SettingsDialog.settingsDialog.setTextSaveFormat(0);
				CommonGUI.loggingForMessageBox.setText(true, "Read complete", false);
				CommonGUI.loggingForMessageBox.setHides(false);
				Control.view.postInvalidate();
				return;
			}
			if (!r) {
				r = load(isEditRichTextOrEditText, editRichText, editText,
						path, TextFormat.MS949_Korean, lang);
			}
			if (r) {
				CommonGUI_SettingsDialog.settingsDialog.setTextSaveFormat(2);
				CommonGUI.loggingForMessageBox.setText(true, "Read complete", false);
				CommonGUI.loggingForMessageBox.setHides(false);
				Control.view.postInvalidate();
				return;
			}
			if (!r) {
				r = load(isEditRichTextOrEditText, editRichText, editText,
						path, TextFormat.UTF_16, lang);
			}
			if (r) {
				CommonGUI_SettingsDialog.settingsDialog.setTextSaveFormat(1);
				CommonGUI.loggingForMessageBox.setText(true, "Read complete", false);
				CommonGUI.loggingForMessageBox.setHides(false);
				Control.view.postInvalidate();
				return;
			}
			if (!r) CommonGUI_SettingsDialog.settingsDialog.setTextSaveFormat(-1);
		}
	}
	
	public boolean load(int isEditRichTextOrEditText, EditRichText editRichText, EditText_Compiler editText,
			String path, TextFormat format, com.gsoft.common.Compiler_types.Language lang) {
		/*if (isEditRichTextOrEditText==1) {			
			if (lang==Language.Java) {
				if ((Compiler.androidAlreadyExists()==false || Compiler.projectSrcAlreadyExists()==false)) {
					Compiler.decompressAndroidAndProjectSrc();
				}
			}
		}*/
		
		FileInputStream stream=null;
		BufferedInputStream bis=null;
		
		//long fileSize = FileHelper.getFileSize(path, true);
		
		try {
			//stream = new FileInputStream(path);
			//bis = new BufferedInputStream(stream);
			if (isEditRichTextOrEditText==0) {
				stream = new FileInputStream(path);
				bis = new BufferedInputStream(stream);
				TextLine text = editRichText.read(bis, format);
				bis.close();
				stream.close();
				//editRichText.initCursorAndScrollPos();
				editRichText.initialize();
				editRichText.setText(0, text);
				editRichText.isModified = false;
				
				if (text==null || text.count==0) {
					File file = new File(path);
					if (file.length()!=0) return false;
				}
			}
			else if (isEditRichTextOrEditText==1) {
				
				//String text = editText.read(bis, format);
				
				editText.initialize();
				boolean result=false;
				if (lang!=null) {		// .java, .htm, .class 등			
					result = editText.setIsProgramCode(lang, path, format);
					if (result) editText.setText(0, editText.getCompileOutput(lang));
				}
				else { // .txt 등
					//result = editText.setIsProgramCode(lang, path);
					stream = new FileInputStream(path);
					bis = new BufferedInputStream(stream);
					editText.setBackColor(CommonGUI_SettingsDialog.settings.selectedColor[0]);
					String text = EditText.Read(bis, format);
					editText.setText(0, new CodeString(text, editText.textColor));
				}
								
				editText.isModified = false;
				
				/*if (text==null || text.length()==0) {
					File file = new File(path);
					if (file.length()!=0) return false;
				}*/
			}
			
			
			CommonGUI.loggingForMessageBox.setHides(true);
			postInvalidate();
			return true;
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			Log.e("Load Error", e1.toString());
			
			CommonGUI.loggingForMessageBox.setText(true, /*Control.res.getString(R.string.load_error_file_not_found)*/e1.toString(), false);
			CommonGUI.loggingForMessageBox.setHides(false);
			return false;
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.e("Load Error", e1.toString());
			
			CommonGUI.loggingForMessageBox.setText(true, /*Control.res.getString(R.string.load_error)*/e1.toString(), false);
			CommonGUI.loggingForMessageBox.setHides(false);
			return false;
		}
		catch (OutOfMemoryError e1) {
			Log.e("Load Error", e1.toString());
			
			CommonGUI.loggingForMessageBox.setText(true, Control.res.getString(R.string.outof_memory_error), false);
			CommonGUI.loggingForMessageBox.setHides(false);
			return false;
		}
		finally {
			FileHelper.close(bis);
			FileHelper.close(stream);
			postInvalidate();
		}
	}
	
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		int i=Control.controlStack.count-1;
		//for (i=Control.controlStack.count-1; i>=0; i--)
		if (i>=0) {
			Control control = Control.controlStack.getItem(i); 
			if (control instanceof Dialog) {
				Dialog dialog = (Dialog)control;
				dialog.cancel();
			}
			else if (control instanceof Menu) {
				Menu menu = (Menu)control;
				menu.open(null, false);
			}
			else { // progressBar, log 등
				control.setHides(true);
			}
		}
		else  {
			closeDialog.open(this, true);
		}
		
	}

	public ViewEx(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		Control.res = context.getResources();
		
		//exitTimer = new Timer(10000000, this, tickTimeOfExitTimer, finishTimeOfExitTimer);
		//exitTimer.setSyncTime(tickTimeOfExitTimer, tickTimeOfExitTimer);
		
		
		createBackupFile();
		
		PowerManagement.getPartialWakeLock(getContext());
		
		Font.loadHangul(context);
		
		
		
	}
	
	/** view에서 size가 정해지면 호출된다.(view.onDraw)*/
	public void sized() {
		
		
		createColorDialog(Control.view);
		SettingsDialog.createSettingsDialog(Control.view);
		Compiler.createTextViewLogBird();
		
		if (settings==null) {
			settings = CommonGUI_SettingsDialog.settings;
		}
		
		int selectedColor;
		if (settings!=null) {
			selectedColor = settings.selectedColor[0];
			CommonGUI_SettingsDialog.settingsDialog.setSelectedColorOfEditText(selectedColor);
			CommonGUI_SettingsDialog.settingsDialog.setSelectedColorOfKeyboard(settings.selectedColor[1]);
			Control.pathAndroid = settings.pathAndroid;
			Control.pathAndroid = Control.pathAndroid.replace('/', File.separatorChar);
			CommonGUI_SettingsDialog.settingsDialog.editTextDirectory.setText(0, new CodeString(Control.pathAndroid+File.separator,Color.BLACK));
		}
	}
	
	void createColorDialog(View view) {
		if (CommonGUI.colorDialog!=null) return;
		int width = view.getWidth();
		int height = view.getHeight();
		int w = (int) (width*0.7f);
		int h = (int) (height*0.8f);
		int x = width/2 - w/2;
		int y = height/2 - h/2;
		Rectangle bounds = new Rectangle(x,y,w,h);
		CommonGUI.colorDialog = new ColorDialog(view, bounds);
		//Control.colorDialog.setOnTouchListener(this);
	}
	
	
	
	public void pause() {
		
	}
	
	/** 종료시 finish()를 호출했으면 1을 저장하고,
	 *  아니면 0을 backup_IsFinishingWhenExitingPrevly에 저장한다.*/
	protected void backupIsFinishingWhenExitingPrevly() {
		Context context = Control.activity.getApplicationContext();
		FileOutputStream stream=null;
		boolean r = false;
		String absFilename=null;
		try {
			File contextDir = context.getFilesDir();
			absFilename = Control.pathGSoftFiles + File.separator + "backup_IsFinishingWhenExitingPrevly";
			stream = new FileOutputStream(absFilename);
			int v = Control.activity.isFinishing() ? 1 : 0;
			IO.writeInt(stream, v, true);
			if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
				IO.writeInt(stream, this.width, true);
				IO.writeInt(stream, this.height, true);
			}
			r = true;
		} catch (FileNotFoundException e) {
			r= false;
		}
		catch (Exception e) {
			r = false;
		}
		finally {
			FileHelper.close(stream);
			if (!r) {
				if (absFilename!=null) {
					File file = new File(absFilename);
					file.delete();
				}
			}
		}
	}
	
	/** 이전에 종료시 finish()를 호출했으면 1을 로드하고,
	 *  아니면 0을 로드한다. backup_IsFinishingWhenExitingPrevly에서 로드한다.*/
	public static int restoreIsFinishingWhenExitingPrevly() {
		Context context = Control.activity.getApplicationContext();
		FileInputStream stream=null;
		String absFilename=null;
		boolean r = false;
		try {
			//File contextDir = context.getFilesDir();
			absFilename = Control.pathGSoftFiles + File.separator + "backup_IsFinishingWhenExitingPrevly";
			stream = new FileInputStream(absFilename);
			int isFinishingWhenExitingPrevly = IO.readInt(stream, true);
			if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
				int width = IO.readInt(stream, true);
				if (width!=0) CommonGUI_SettingsDialog.settings.viewWidth = width;
				int height = IO.readInt(stream, true);
				if (height!=0) CommonGUI_SettingsDialog.settings.viewHeight = height;
			}
			return isFinishingWhenExitingPrevly;
		} 
		catch (Exception e) {
			//settings = new Settings();
			return -1;
		}
		finally {
			FileHelper.close(stream);
			if (!r) {
				if (absFilename!=null) {
					//File file = new File(absFilename);
					//file.delete();
				}
			}
		}
	}
	
	/*void moveAssetEtcFilesToEtc() {
		Context context = getContext();
		Resources r = context.getResources();
		AssetManager asset = r.getAssets();
		
		String[] list=null;
		try {
			list = asset.list("etc");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("moveEtcFilesToSystem", e.toString());
		}
		int i;
		InputStream inputStream=null;
		FileOutputStream outputStream=null;
		BufferedOutputStream os=null;
		File etcDir = new File("/system/etc");
		
		for (i=0; i<list.length; i++) {
			try {				
				String absFilename = etcDir.getAbsolutePath() + File.separator + list[i];
				File file = new File(absFilename);
				if (file.exists()) continue;
				inputStream = asset.open("etc/"+list[i]);
				//outputStream = context.openFileOutput(list[i], Context.MODE_PRIVATE);
				outputStream = new FileOutputStream(file);
				//os = new BufferedOutputStream(outputStream, fileLen);
				
				FileHelper.move(inputStream, os);
				
		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				FileHelper.close(inputStream);
				FileHelper.close(outputStream);
				FileHelper.close(os);
				inputStream=null;
				outputStream=null;
				os=null;
			}
		}
	}*/
	
	
	void moveFiles(String directoryName, File outputFile) {
		Context context = getContext();
		Resources r = context.getResources();
		AssetManager asset = r.getAssets();
		
		String relativePath;
		int indexIndirectoryName = new String("files").length()+1;
		relativePath = directoryName.substring(indexIndirectoryName, directoryName.length());
		String[] list=null;
		try {
			list = asset.list(directoryName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("moveImagesToAppPackage", e.toString());
		}
		int i;
		InputStream inputStream=null;
		FileOutputStream outputStream=null;
		BufferedOutputStream os=null;
	
		byte[] buf = new byte[1000];
		
		for (i=0; i<list.length; i++) {
			try {				
				String absFilename = outputFile.getAbsolutePath() + File.separator + 
						relativePath + File.separator + list[i];
				File file = new File(absFilename);
				
				if (file.isDirectory()) {
					file.mkdir();
					this.moveFiles("files"+File.separator+relativePath + File.separator + list[i], file);
					continue;
				}
				if (file.exists()) continue;
				
				inputStream = asset.open(directoryName+File.separator+list[i]);
				
				outputStream = new FileOutputStream(file);
				
				FileHelper.move(buf, inputStream, outputStream);				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				FileHelper.close(inputStream);
				FileHelper.close(outputStream);
				FileHelper.close(os);
				inputStream=null;
				outputStream=null;
				os=null;
			}
		}
	}
	
	void moveFilesToContextAppPackage() {
		Context context = getContext();
		Resources r = context.getResources();
		AssetManager asset = r.getAssets();
		
		String[] list=null;
		try {
			list = asset.list("files");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("moveImagesToAppPackage", e.toString());
		}
		int i;
		InputStream inputStream=null;
		FileOutputStream outputStream=null;
		BufferedOutputStream os=null;
		File contextDir = getContext().getFilesDir();
	
		byte[] buf = new byte[1000];
		
		for (i=0; i<list.length; i++) {
			try {				
				String absFilename = contextDir.getAbsolutePath() + File.separator + list[i];
				File file = new File(absFilename);
				
				if (file.isDirectory()) {
					file.mkdir();
					this.moveFiles("files"+File.separator+list[i], contextDir);
					continue;
				}
				if (file.exists()) continue;
				
				inputStream = asset.open("files"+File.separator+list[i]);
				
				outputStream = new FileOutputStream(file);
				
				FileHelper.move(buf, inputStream, outputStream);				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				FileHelper.close(inputStream);
				FileHelper.close(outputStream);
				FileHelper.close(os);
				inputStream=null;
				outputStream=null;
				os=null;
			}
		}
		
	}
	
	/**Control.pathGSoftFiles에 backup_settings, backup_EditRichText.kjy, backup_EditText.txt,
	 * backup_playlist.txt, backup_text.kjy 등의 파일이 있는지 확인한다.
	 * @return
	 */
	public static boolean backupFilesExists() {
		/*File sdCardDir = new File(Control.pathGSoftFiles+File.separator+"backup_settings");
		if (sdCardDir.exists()) return true;
		else return false;*/
		
		File sdCardDir = new File(Control.pathGSoftFiles);
		String[] fileList = sdCardDir.list();
		if (fileList!=null && fileList.length==56) return true;
		else return false;
	}
	
	/** Control.pathHelpFiles에 "ListenToMusic.kjy" 파일이 있는지 확인한다.*/
	public static boolean backupHelpFilesExists() {
		/*File sdCardDir = new File(Control.pathHelpFiles+File.separator+"ListenToMusic.kjy");
		if (sdCardDir.exists()) return true;
		else return false;*/
		
		File sdCardDir = new File(Control.pathHelpFiles);
		String[] fileList = sdCardDir.list();
		if (fileList!=null && fileList.length==4) return true;
		else return false;
		
	}
	
	/** Control.DownloadedImageDirPath 에 "Func.jpg" 파일이 있는지 확인한다.*/
	public static boolean backupDownloadedImageExists() {
		/*File sdCardDir = new File(Control.DownloadedImageDirPath+File.separator+"Func.jpg");
		if (sdCardDir.exists()) return true;
		else return false;*/
		
		File sdCardDir = new File(Control.DownloadedImageDirPath);
		String[] fileList = sdCardDir.list();
		if (fileList!=null && fileList.length==24) return true;
		else return false;
		
	}
	
	
	/** asset 에 있는 files 들을 /mnt/sdcard/gsoft-files 로 복사한다.*/
	public static void moveFilesToSDCard() {
		Context context = Control.activity.getApplicationContext();
		Resources r = context.getResources();
		AssetManager asset = r.getAssets();
		
		String[] list=null;
		try {
			list = asset.list("files");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("moveImagesToAppPackage", e.toString());
		}
		if (list==null) return;
		int i;
		InputStream inputStream=null;
		FileOutputStream outputStream=null;
		BufferedOutputStream os=null;
		File sdCardDir = new File(Control.pathGSoftFiles);
		if (sdCardDir.exists()==false) {
			sdCardDir.mkdirs();
		}
		
		byte[] buf = new byte[1000];
		
		for (i=0; i<list.length; i++) {
			try {
				String absFilename = sdCardDir.getAbsolutePath() + File.separator + list[i];
				File file = new File(absFilename);				
				
				if (file.exists()) continue;
				
				inputStream = asset.open("files"+File.separator+list[i]);
				
				file.createNewFile();
				outputStream = new FileOutputStream(file);
				
				FileHelper.move(buf, inputStream, outputStream);				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				FileHelper.close(inputStream);
				FileHelper.close(outputStream);
				FileHelper.close(os);
				inputStream=null;
				outputStream=null;
				os=null;
			}
		}//for
		
	}
	
	
	/** asset 에 있는 help_files 들을 /mnt/sdcard/janeSoft/help_files 로 복사한다.*/
	public static void moveHelpFilesToSDCard() {
		Context context = Control.activity.getApplicationContext();
		Resources r = context.getResources();
		AssetManager asset = r.getAssets();
		
		String[] list=null;
		try {
			list = asset.list("help_files");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("moveHelpFilesToSDCard", e.toString());
		}
		if (list==null) return;
		int i;
		InputStream inputStream=null;
		FileOutputStream outputStream=null;
		BufferedOutputStream os=null;
		File sdCardDir = new File(Control.pathHelpFiles);
		if (sdCardDir.exists()==false) {
			sdCardDir.mkdirs();
		}
		
		byte[] buf = new byte[1000];
		
		for (i=0; i<list.length; i++) {
			try {
				String absFilename = sdCardDir.getAbsolutePath() + File.separator + list[i];
				File file = new File(absFilename);				
				
				if (file.exists()) continue;
				
				inputStream = asset.open("help_files"+File.separator+list[i]);
				
				file.createNewFile();
				outputStream = new FileOutputStream(file);
				
				FileHelper.move(buf, inputStream, outputStream);				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				FileHelper.close(inputStream);
				FileHelper.close(outputStream);
				FileHelper.close(os);
				inputStream=null;
				outputStream=null;
				os=null;
			}
		}//for
		
	}
	
	
	/** asset 에 있는 DownloadedImage 들을 /mnt/sdcard/janeSoft/DownloadedImage 로 복사한다.*/
	public static void moveDownloadedImagesToSDCard() {
		Context context = Control.activity.getApplicationContext();
		Resources r = context.getResources();
		AssetManager asset = r.getAssets();
		
		String[] list=null;
		try {
			list = asset.list("DownloadedImage");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("moveDownloadedImagesToSDCard", e.toString());
		}
		if (list==null) return;
		int i;
		InputStream inputStream=null;
		FileOutputStream outputStream=null;
		BufferedOutputStream os=null;
		File sdCardDir = new File(Control.DownloadedImageDirPath);
		if (sdCardDir.exists()==false) {
			sdCardDir.mkdirs();
		}
		
		byte[] buf = new byte[1000];
		
		for (i=0; i<list.length; i++) {
			try {
				String absFilename = sdCardDir.getAbsolutePath() + File.separator + list[i];
				File file = new File(absFilename);				
				
				if (file.exists()) continue;
				
				inputStream = asset.open("DownloadedImage"+File.separator+list[i]);
				
				file.createNewFile();
				outputStream = new FileOutputStream(file);
				
				FileHelper.move(buf, inputStream, outputStream);				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				FileHelper.close(inputStream);
				FileHelper.close(outputStream);
				FileHelper.close(os);
				inputStream=null;
				outputStream=null;
				os=null;
			}
		}//for
		
	}
	
	boolean isFileExistInContextAppPackage(String path) {
		return false;
	}
	
	boolean isAndroidLibExistInContextAppPackage() {
		File contextDir = getContext().getFilesDir();
		String absFilename = contextDir.getAbsolutePath() + File.separator + "android.jar";
		File file = new File(absFilename);
		if (file.exists()) return true;
		return false;
	}
	
	void extractAndroidJar() {
		File contextDir = getContext().getFilesDir();
		String jarPath = contextDir.getAbsolutePath() + File.separator + "android.jar";
		
		try {
			JarFile jarFile = new JarFile(jarPath, false);
			Enumeration<JarEntry> e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				String name = entry.getName();
				InputStream is = jarFile.getInputStream(entry);
				
			}
			jarFile.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	void moveAndroidLibraryToContextAppPackage() {
		//if (isAndroidLibExistInContextAppPackage()) return;
		
		Context context = getContext();
		Resources r = context.getResources();
		AssetManager asset = r.getAssets();
		
		String[] list=null;
		try {
			list = asset.list("lib");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("moveAndroidLibraryToContextAppPackage", e.toString());
		}
		int i;
		InputStream inputStream=null;
		FileOutputStream outputStream=null;
		BufferedOutputStream os=null;
		File contextDir = getContext().getFilesDir();
		
		byte[] buf = new byte[1000];
		
		for (i=0; i<list.length; i++) {
			try {				
				String absFilename = contextDir.getAbsolutePath() + File.separator + list[i];
				//String absFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + list[i];
				File file = new File(absFilename);
				//if (file.exists()) continue;
				
				inputStream = asset.open("lib"+File.separator+list[i]);
				outputStream = new FileOutputStream(file);
				
				FileHelper.move(buf, inputStream, outputStream);
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				FileHelper.close(inputStream);
				FileHelper.close(outputStream);
				FileHelper.close(os);
				inputStream=null;
				outputStream=null;
				os=null;
			}
		}
		
	}
	
	/** View의 initControls()의 슈퍼 메서드이다.*/
	protected void initControls() throws Exception {
		if (width==0 || height==0) throw new Exception("뷰의 너비와 높이가 정해지지 않았음");
		ScrollBars.setScrollBarScale(this);
		createCloseDialog();
	}
	
	/** View의 initControls() 이후에 호출되는 슈퍼 메서드이다.*/
	public void afterInitControls() {
		int gap = 5;
		int alpha = 255;
		int w = (int) (this.width * 0.03f);
		int h = w;
		int x = (this.width - w - gap);
		int y = gap;
		
		Rectangle boundsClose = new Rectangle(x, y, w, h);
		Button closeButton = new Button(this, "Close", "x", 
				Color.RED, boundsClose, false, alpha, true, 0.0f, null, Color.LTGRAY);
		closeButton.setBackColor(Color.RED);
		closeButton.setOnTouchListener(this);
		closeButton.setHides(false);
		this.controls[0] = closeButton;
		
		x = (boundsClose.x - w - gap);
		Rectangle boundsMinimize = new Rectangle(x, y, w, h);
		Button minimizeButton = new Button(this, "Minimize", "_", 
				Color.RED, boundsMinimize, false, alpha, true, 0.0f, null, Color.LTGRAY);
		minimizeButton.setBackColor(Color.RED);
		minimizeButton.setOnTouchListener(this);
		minimizeButton.setHides(false);
		this.controls[1] = minimizeButton;
		
		if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
			if (Control.isMasterOrSlave==false) {
				// slave 에서 수신을 위한 소켓의 포트번호
				int portNum = Pipe.countOfCreatedProcesses + Pipe.StartPortNum;
										
				CommonGUI.loggingForNetwork.setText(true, "Listening to port "+portNum, false);
				CommonGUI.loggingForNetwork.setHides(false);
				Control.view.invalidate();
				
				mPipe = new Pipe.ThreadPipe();
				mPipe.start();
			}
		}
	}
	
	protected void deleteBackupFile() {
		File contextDir = getContext().getFilesDir();
		String absFilename = contextDir.getAbsolutePath() + File.separator + "backup_text.kjy";
		File backupFile = new File(absFilename);
		backupFile.delete();
	}
	
	/** backup_text.kjy이 존재하지 않으면 생성한다.*/
	protected void createBackupFile() {
		File contextDir = getContext().getFilesDir();
		String absFilename = contextDir.getAbsolutePath() + File.separator + "backup_text.kjy";
		File backupFile = new File(absFilename);
		if (backupFile.exists()==false) {
			FileOutputStream stream=null;
			try {
				stream = new FileOutputStream(backupFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
			}finally {
				FileHelper.close(stream);
			}
		}	
		
	}
	
	
	
	public void destroy () {
		if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
			
			if (Control.isMasterOrSlave) {
				Pipe.destroyMainProcess();
				
			}			
			
			if (Control.isMasterOrSlave==false) {
				Pipe.destroySubProcess();
				if (mPipe!=null) {
					mPipe.destroy();
				}
			}
		}
	}

	@Override
	public void onTick(Object sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinish(Object sender) {
		// TODO Auto-generated method stub
		
	}
	
	public void onDraw(Canvas canvas) {
		// 자바에서 배경지우기 추가
		//canvas.g.setColor(java.awt.Color.LIGHT_GRAY);
		//canvas.g.fillRect(0, 0, width, height);
		RectF rectF = new RectF();
		rectF.left = 0;
		rectF.top = 0;
		rectF.right = width;
		rectF.bottom = height;
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(backColor);
		canvas.drawRect(rectF, paint);
	}
	
	public void afterOnDraw(Canvas canvas) {
		/*int i;
		for (i=0; i<this.controls.length; i++) {
			Control control = controls[i];
			if (control!=null) {
				control.draw(canvas);
			}
		}*/
	}
	
	protected void resizeView() {
		int gap = 5;
		Button closeButton = null;
		Button minimizeButton;
		if (controls[0]!=null) {
			closeButton = (Button) controls[0];
			Rectangle newBounds = closeButton.bounds;
			newBounds.x = width - newBounds.width - gap;
			closeButton.changeBounds(newBounds);
		}
		if (controls[1]!=null) {
			minimizeButton = (Button) controls[1];
			Rectangle newBounds = minimizeButton.bounds;
			newBounds.x = closeButton.bounds.x - newBounds.width - gap;
			minimizeButton.changeBounds(newBounds);
		}
	}
	

	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		if (sender instanceof Button) {
			Button button = (Button) sender;
			if (controls[0]!=null && button.iName==this.controls[0].iName) {
				Control.exit(false);
			}
			else if (controls[1]!=null && button.iName==this.controls[1].iName) {
				this.setState(Frame.ICONIFIED);
			}
		}
	}

}