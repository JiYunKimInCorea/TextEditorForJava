package com.gsoft.common;

import com.gsoft.common.R.R;
import com.gsoft.common.gui.Control;
import com.gsoft.common.gui.SettingsDialog;
import com.gsoft.common.gui.SettingsDialog.Settings;
import com.gsoft.common.gui.ViewEx;

public class CommonGUI_SettingsDialog {
	/**ViewEx의 sized()에서 createSettingsDialog(View view)가 호출되어 생성되고 
	 * CommonGUI_SettingsDialog.settingsDialog에 저장된다.*/
	public static SettingsDialog settingsDialog;
	
	/** Button, EditText 등의 isTripleBuffering(static)속성은 여기를 참조한다.
	 * ViewEx의 settings는 여기를 참조한다. 가장 먼저 실행되는 곳이다.*/
	public static Settings settings;
	
	/** 프로그램 시작시 맨처음 실행된다.*/
	static {
		//SettingsDialog.createSettingsDialog(view);
		
		//프로그램이 설치되어 처음으로 실행될 때 호출된다.
		if (ViewEx.backupFilesExists()==false) {
			ViewEx.moveFilesToSDCard();
		}
		
		if (ViewEx.backupHelpFilesExists()==false) {
			ViewEx.moveHelpFilesToSDCard();
		}
		
		if (ViewEx.backupDownloadedImageExists()==false) {
			ViewEx.moveDownloadedImagesToSDCard();
		}
		
		if (settings==null) {
			settings = SettingsDialog.restoreSettings();
		}
		
		// 디폴트 세팅
		if (settings==null) {
			settings = new Settings();
		}
		
		if (settings.EnablesUnzipLibrary) {
			CompilerHelper.decompressAndroidAndProjectSrc();
		}
	}
	
	/**SettingsDialog.buttonShowsCopyRight의 선택상태에 따라 저작권 메시지를 보여준다.*/
	public static void showsCopyRight() {
		if (CommonGUI_SettingsDialog.settingsDialog.buttonShowsCopyRight.isSelected) {
			String copyRight = Control.res.getString(R.string.copy_right);
			CommonGUI.loggingForMessageBox.setText(true, copyRight, false);
			CommonGUI.loggingForMessageBox.setHides(false);
		}
	}
}