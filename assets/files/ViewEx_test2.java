package com.gsoft.common.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.gsoft.common.Font;
import com.gsoft.common.PowerManagement;
import com.gsoft.common.Timer;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Compiler;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import com.gsoft.common.gui.FontSizeDialog_test;

public class ViewEx_test2 extends android.view.View {
	FontSizeDialog_test dialogfont;

	protected int width;
	protected int height;

	protected Timer exitTimer;
	protected long tickTimeOfExitTimer = 4 * 60 * 1000;
	
	protected MessageDialog closeDialog;
	
	protected MessageDialog messageDialog;
	
	int f(int a) {
		return a;
	}
	
	protected com.gsoft.common.gui.Dialog createCloseDialog(com.gsoft.common.gui.Dialog d) 
			throws java.lang.Exception {
		com.gsoft.common.gui.FontSizeDialog_test fontDialog = null;
		dialogfont.open(false);
		dialogfont.open(null);
		dialogfont.cancel();
		RectangleF r = dialogfont.boundsExceptTitleBar;
		//RectangleF boundsOfCloseDialog = new RectangleF(0, 0, width*0.8f, height*0.4f);
		RectangleF boundsOfCloseDialog = new RectangleF(0.0f, 0.0f, width*0.8f, height*0.4f);
		f(2+3);
		f(f(2+3)+3);
		int a = f(f(f(1+2)+3)+4)+5;
		boundsOfCloseDialog.x = (width-boundsOfCloseDialog.width) / 2;
		boundsOfCloseDialog.y = (height-boundsOfCloseDialog.height) / 2;
		
		closeDialog = new MessageDialog(this, boundsOfCloseDialog);
		closeDialog.setText("Exit this application?");
		return closeDialog;
	}
	
	
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		int i=Control.controlStack.count-1;
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
		
	}

	public ViewEx_test2(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		Control.res = context.getResources();
				
		PowerManagement.getPartialWakeLock(getContext());
		
		Font.loadHangul(context);
		
		moveFilesToContextAppPackage();
		
	
	}
	
	/** view에서 size가 정해지면 호출된다.(view.onDraw)*/
	public void sized() {
		Compiler.createTextViewLogBird();
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
		File contextDir = getContext().getFilesDir();
				
		for (i=0; i<list.length; i++) {
			try {				
				String absFilename = contextDir.getAbsolutePath() + File.separator + list[i];
				File file = new File(absFilename);
				if (file.exists()) continue;
				
				inputStream = asset.open("files/"+list[i]);
				
				outputStream = new FileOutputStream(file);
				
				FileHelper.move(inputStream, outputStream);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				FileHelper.close(inputStream);				
			}
		}
		
	}
		
	void extractAndroidJar() {
		File contextDir = getContext().getFilesDir();
		String jarPath = contextDir.getAbsolutePath() + File.separator + "android.jar";
		
		try {
			JarFile jarFile = new JarFile(jarPath, false);
			Enumeration<JarEntry> e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				InputStream is = jarFile.getInputStream(entry);
				
			}
			jarFile.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	

}
