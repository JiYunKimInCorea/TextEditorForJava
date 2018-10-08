package com.gsoft.common;

public class JniNatives {
	static {
		try {
			System.loadLibrary("Native_test");
		}catch(UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}
	//public static native void delete();
	public static native int addVals(int a, int b);
	
}