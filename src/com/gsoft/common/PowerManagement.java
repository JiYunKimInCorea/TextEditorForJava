package com.gsoft.common;

import com.gsoft.common.gui.Control;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.WindowManager;

public class PowerManagement {
	static PowerManager powerManager;
	public static boolean isScreenOn;
	public static WakeLock wakeLock;
	public static void keepScreenOn() {
		Control.window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		isScreenOn = true;
	}
	
	public static void clearScreenOn() {
		Control.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		isScreenOn = false;
	}
	
	public static void goToSleep(Context context, long time) {
		if (powerManager==null) {
			powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		}
		powerManager.goToSleep(time);
	}
	
	public static void getPartialWakeLock(Context context) {
		if (powerManager==null) {
			powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		}
		if (wakeLock==null) {
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Lock");
			wakeLock.acquire();
		}
	}
	
	public static void releaseWakeLock() {
		if (wakeLock!=null) {
			wakeLock.release();
			wakeLock = null;
		}
	}
}