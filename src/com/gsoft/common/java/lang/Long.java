package com.gsoft.common.java.lang;

public class Long {
	long value;
	public Long(long value) {
		// TODO Auto-generated constructor stub
		this.value = value;
	}
	/**Returns the long value of this Long object.*/
	public long longValue() {
		return value;
	}

	/**Returns a new long initialized to the value represented by the specified String, as performed by the valueOf method of class Long.*/
	public static long parseLong(String s)
            throws NumberFormatException {
		return 0;
	}
	public static long valueOf(Long number) {
		return number.value;
	}
}