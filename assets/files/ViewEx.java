package com.gsoft.common.gui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.gsoft.common.Font;
import com.gsoft.common.PowerManagement;
import com.gsoft.common.Timer;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.interfaces.TimerListener;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;

public class ViewEx extends View 
implements TimerListener, com.gsoft.common.interfaces.OnTouchListener {
	
	protected int width;
	protected int height;

	protected Timer exitTimer;
	protected long tickTimeOfExitTimer = 4 * 60 * 1000;
	protected long finishTimeOfExitTimer = 4 * 60 * 1000;
	
	protected MessageDialog closeDialog;
	
	protected MessageDialog messageDialog;
	
	protected void createCloseDialog() {
		RectangleF boundsOfCloseDialog = new RectangleF(0, 0, width*0.8f, height*0.4f);
		boundsOfCloseDialog.x = (width-boundsOfCloseDialog.width) / 2;
		boundsOfCloseDialog.y = (height-boundsOfCloseDialog.height) / 2;
		
		closeDialog = new MessageDialog(this, boundsOfCloseDialog);
		//closeDialog.setOnTouchListener(this);
		closeDialog.setText("Exit this application?");
	}
	
	protected void createMessageDialog(RectangleF bounds, float fontSize, String message) {
		messageDialog = new MessageDialog(this, bounds);
		messageDialog.setFontSize(fontSize);
		//closeDialog.setOnTouchListener(this);
		messageDialog.setText(message);
	}
	
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		int i=Control.controlStack.count-1;
		//for (i=Control.controlStack.count-1; i>=0; i--)
		if (i>=1) {
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
		
		moveFilesToContextAppPackage();
		
		//moveAndroidLibraryToContextAppPackage();
		
		//extractAndroidJar();
		
		//moveAssetEtcFilesToEtc();
	}
	
	/** view에서 size가 정해지면 호출된다.(view.onDraw)*/
	public void sized() {
		createColorDialog(Control.view);
		createSettingsDialog(Control.view);
	}
	
	void createColorDialog(View view) {
		if (Control.colorDialog!=null) return;
		float width = view.getWidth();
		float height = view.getHeight();
		float w = width*0.7f;
		float h = height*0.8f;
		float x = width/2 - w/2;
		float y = height/2 - h/2;
		RectangleF bounds = new RectangleF(x,y,w,h);
		Control.colorDialog = new ColorDialog(view, bounds);
		//Control.colorDialog.setOnTouchListener(this);
	}
	
	void createSettingsDialog(View view) {
		if (Control.settingsDialog!=null) return;
		float width = view.getWidth();
		float height = view.getHeight();
		float w = width*0.8f;
		float h = height*0.8f;
		float x = width/2 - w/2;
		float y = height/2 - h/2;
		RectangleF bounds = new RectangleF(x,y,w,h);
		Control.settingsDialog = new SettingsDialog(view, bounds);
		//Control.colorDialog.setOnTouchListener(this);
	}
	
	public void pause() {
		
	}
	
	void moveAssetEtcFilesToEtc() {
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
		//String curDir;
		//curDir = System.getProperty("user.dir");
		
		for (i=0; i<list.length; i++) {
			try {				
				String absFilename = contextDir.getAbsolutePath() + File.separator + list[i];
				File file = new File(absFilename);
				//File fileToCopy = new File("C:\\Users\\k\\Documents\\eclipse_workspace\\TextEditor\\assets\\files\\"+list[i]);
				/*if (file.exists()) {
					long file1 = file.lastModified();
					long file2 = fileToCopy.lastModified();
					if (file1 > file2) continue;
				}*/
				if (file.exists()) continue;
				
				inputStream = asset.open("files/"+list[i]);
				//inputStream.
				
				//outputStream = context.openFileOutput(list[i], Context.MODE_PRIVATE);
				outputStream = new FileOutputStream(file);
				//os = new BufferedOutputStream(outputStream, fileLen);
				
				FileHelper.move(inputStream, outputStream);
				
				/*try {
					do {
						int readed;
						readed = inputStream.read(buf, 0, buf.length);
						if (readed<=0) {
								break;
						}
						outputStream.write(buf,0,readed);
						
					}while(true);
				}catch(Exception e) {
					e.printStackTrace();
				}*/
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
		
		for (i=0; i<list.length; i++) {
			try {				
				String absFilename = contextDir.getAbsolutePath() + File.separator + list[i];
				//String absFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + list[i];
				File file = new File(absFilename);
				
				inputStream = asset.open("lib/"+list[i]);
				outputStream = new FileOutputStream(file);
				
				FileHelper.move(inputStream, outputStream);
				
				
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
	
	protected void initControls() throws Exception {
		if (width==0 || height==0) throw new Exception("뷰의 너비와 높이가 정해지지 않았음");
		ScrollBars.setScrollBarScale(this);
		createCloseDialog();
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
		
	}

	@Override
	public void onTick(Object sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinish(Object sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
