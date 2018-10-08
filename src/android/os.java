package android;

import java.io.File;

import android.content.Context;

public class os {
	public static class PowerManager {

		public static class WakeLock {

			public void acquire() {
				// TODO Auto-generated method stub
				
			}

			public void release() {
				// TODO Auto-generated method stub
				
			}

		}

		public static final int PARTIAL_WAKE_LOCK = 1;

		public void goToSleep(long time) {
			// TODO Auto-generated method stub
			
		}

		/** wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Lock");*/
		public WakeLock newWakeLock(int levelAndFlags, String tag) {
			// TODO Auto-generated method stub
			return new WakeLock();
		}

	}

	public static class Build {

		public static class VERSION {

			public static int SDK_INT = 18;
			
		}

	}

	public static class Process {

		public static int myPid() {
			// TODO Auto-generated method stub
			return 0;
		}

		public static void killProcess(int myPId) {
			// TODO Auto-generated method stub
			
		}

	}

	public static class Bundle {

	}

	public static class Environment {

		/** 윈도우즈의 경우 예를들어 c:\TextEditorForJava 가 되고, 
		 * 안드로이드의 경우는 /mnt/sdcard 가 된다.
		 */
		public static File getExternalStorageDirectory() {
			// TODO Auto-generated method stub
			return new File(Context.ExternalStorage_path);
		}

		public static File getRootDirectory() {
			// TODO Auto-generated method stub
			return new File(Context.Root_path);
		}
		
	}
}