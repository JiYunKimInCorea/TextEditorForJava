package com.gsoft.texteditor14;

import java.io.File;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;

import com.gsoft.DataTransfer.pipe.Pipe;
import com.gsoft.common.CommonGUI;
import com.gsoft.common.CommonGUI_SettingsDialog;
//import com.gsoft.common.Notification;
import com.gsoft.common.PowerManagement;
import com.gsoft.common.gui.Control;
import com.gsoft.common.hardware.HardwareKeyboard;

public class MainActivity extends Activity  {
	CustomView mView;
	
	String title;
	
	

	
	/*static long factorial(long n) {
		if (n==0 || n==1) return 1;
		long r;
		r = n * factorial(n-1);
		return r;
	}
	
	static long sum(int n) {
		int i;
		long r = 0;
		for (i=0; i<=n; i++) {
			r += i;
		}
		return r;
	}*/

	
	public static void main(String[] args) {
		
		/*long oldTime = System.currentTimeMillis();
		for (int i=0; i<1000; i++) {
			factorial(3000);
			sum(1000000);
		}
		long curTime = System.currentTimeMillis();
		long diff = curTime - oldTime;*/
		
		
		// 윈도우즈와 우분투와 같은 데스크탑에서만 사용한다.
		String workingDir = System.getProperty("user.dir");
		if (File.separator.equals("\\")) {
			System.setProperty("user.dir", "C:\\GSoft\\TextEditorForJava\\TextEditorForJava");
		}
		else {
			File usrDir = new File("/usr");
			if (usrDir.exists()) { // 리눅스
				System.setProperty("user.dir", "/home/TextEditorForJava/TextEditorForJava");
			}
			else {
				// 안드로이드
				
			}
		}
		workingDir = System.getProperty("user.dir");
		
		
		MainActivity activity = new MainActivity();
		Control.activity = activity;
		Control.CurrentSystem = "JAVA";
		
		
		
		
		
		boolean putCount = false;
		Map<String, String> map = System.getenv();
		String countOfCreatedProcesses =  map.get("countOfCreatedProcesses");
		if (countOfCreatedProcesses!=null) {
			Pipe.countOfCreatedProcesses = Integer.parseInt(countOfCreatedProcesses);
			putCount = true;
		}
		
		if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
			//if (args.length>0) { // TextEditorForJava_sub
			if (putCount) {
				Control.isMasterOrSlave = false;
				Control.numOfCurProcess = Pipe.countOfCreatedProcesses;
				boolean success = false;
				try {
					//Pipe.countOfCreatedProcesses = Integer.parseInt(args[args.length-1]);
					success = true;
				}catch(Exception e) {
					
				}
				if (success == false) {
					
				}
			}
			else {// TextEditorForJava
				Control.isMasterOrSlave = true;
			}
		}
		
		Control.window = activity.getWindow(); 
		PowerManagement.keepScreenOn();
		
		Context context = activity.getApplicationContext();
		
		try {
		activity.mView = new CustomView(activity,context);
		Pipe.listener = activity.mView;		
		activity.mView.restoreContents();
		activity.mView.setSize(CommonGUI_SettingsDialog.settings.viewWidth, 
				CommonGUI_SettingsDialog.settings.viewHeight);
		
		activity.setContentView(activity.mView);
		
		
		}catch (Exception e) {
        	e.printStackTrace();
        }
		
	}
	

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction()==KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
	            // When the user center presses, let them pick a contact.
	        	mView.onBackPressed();
	        	//mView.closeDialog.open();
	        	//Control.setModified(true);
	        	mView.invalidate();
	            return true;
	        }
	       		
			HardwareKeyboard hardwareKeyboard = new HardwareKeyboard(keyCode, event);
			CommonGUI.keyboard.onTouchEvent(hardwareKeyboard, null);
			mView.invalidate();
			return true;
		}
		return false;
    }
		
	
	/**app이 실행을 멈추고 실행을 재개할 때 호출된다.*/
	protected void onResume() {
		super.onResume();
		mView.resume();		
	}

	/**app이 실행을 멈출 때 호출된다. 예를 들어 home버튼 눌릴 시*/
    protected void onPause() {
    	super.onPause();
    	//Control.exit(true);
    	mView.pause();    	
    }
   
    
    public void destroy() {
    	String appName = this.getClass().getPackage().getName();
    	try{
    		
    	if (isFinishing()==false) {  // 여기서 finish()를 안하고 종료하므로 backupText()에 의한 저장을 해야 한다.
			mView.destroyExceptNotify();
			super.onDestroy();
			int myPId = android.os.Process.myPid();
			android.os.Process.killProcess(myPId);
			System.exit(0);
					
			//String appName = this.getClass().getPackage().getName();
			//Process process = Runtime.getRuntime().exec(appName, null, null);
			android.os.Process p = new android.os.Process();
			
		}
		else {	// finish called.
			if (Control.isDestroyedExceptNotify) {
				mView.destroyExceptNotify();
				super.onDestroy();
				System.exit(0);
				
				int myPId = android.os.Process.myPid();
				android.os.Process.killProcess(myPId);
				
			}
			else {
				mView.destroy();
				super.onDestroy();
				System.exit(0);
				
				int myPId = android.os.Process.myPid();
				android.os.Process.killProcess(myPId);
				
			}
		}
    	
    	}catch(Exception e) {
    		e.printStackTrace();
    		Control.showMessageToFile((new com.gsoft.common.Util.StackTracer(e)).getMessage().str);
    	}
    }
  
	
	protected void onDestroy() {
		destroy();
		
	}

}