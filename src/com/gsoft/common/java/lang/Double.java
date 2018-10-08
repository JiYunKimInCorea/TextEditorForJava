package com.gsoft.common.java.lang;

public class Double {
	double value;
	public Double(double value) {
		// TODO Auto-generated constructor stub
		this.value = value;
	}
	/**Returns the double value of this Double object.*/
	public double doubleValue() {
		return value;
	}

	/**Returns a new double initialized to the value represented by the specified String, as performed by the valueOf method of class Double.*/
	public static double parseDouble(String s)
            throws NumberFormatException {
		return 0;
	}

	
	public static double valueOf(Double number) {
		return number.value;
	}
}