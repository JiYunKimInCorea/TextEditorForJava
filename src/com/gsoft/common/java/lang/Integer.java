package com.gsoft.common.java.lang;

public class Integer {
	/**The number of bytes used to represent a int value in two's complement binary form.*
	static int	BYTES;
	/**A constant holding the maximum value an int can have, 231-1.*/
	static int	MAX_VALUE;
	/**A constant holding the minimum value an int can have, -231.*/
	static int	MIN_VALUE;
	/**The number of bits used to represent an int value in two's complement binary form.*/
	static int	SIZE;
	/**Constructs a newly allocated Integer object that represents the specified int value.*/
	public Integer(int value) {
		
	}
	/**Constructs a newly allocated Integer object that represents the int value indicated by the java.lang.String parameter.*/
	public Integer(java.lang.String s) {
		
	}
	/**Returns the number of one-bits in the two's complement binary representation of the specified int value.*/
	static int	bitCount(int i) {
		return 0;
	}
	/**Returns the value of this Integer as a byte after a narrowing primitive conversion.*/
	byte	byteValue() {
		return 0;
	}
	/**Compares two int values numerically.*/
	static int	compare(int x, int y) {
		return 0;
	}
	/**Compares two Integer objects numerically.*/
	int	compareTo(Integer anotherInteger) {
		return 0;
	}
	/**Compares two int values numerically treating the values as unsigned.*/
	static int	compareUnsigned(int x, int y) {
		return 0;
	}
	/**Decodes a java.lang.String into an Integer.*/
	static java.lang.Integer	decode(java.lang.String nm) {
		return null;
	}
	/**Returns the unsigned quotient of dividing the first argument by the second where each argument and the result is interpreted as an unsigned value.*/
	static int	divideUnsigned(int dividend, int divisor) {
		return 0;
	}
	/**Returns the value of this Integer as a double after a widening primitive conversion.*/
	double	doubleValue() {
		return 0;
	}
	/**Compares this object to the specified object.*/
	public boolean	equals(Object obj) {
		return false;
	}
	/**Returns the value of this Integer as a float after a widening primitive conversion.*/
	float	floatValue() {
		return 0;
	}
	/**Determines the integer value of the system property with the specified name.*/
	static java.lang.Integer	getInteger(java.lang.String nm) {
		return null;
	}
	/**Determines the integer value of the system property with the specified name.*/
	static Integer	getInteger(java.lang.String nm, int val) {
		return null;
	}
	/**Returns the integer value of the system property with the specified name.*/
	static Integer	getInteger(java.lang.String nm, Integer val) {
		return null;
	}
	/**Returns a hash code for this Integer.*/
	public int	hashCode() {
		return 0;
	}
	/**Returns a hash code for a int value; compatible with Integer.hashCode().*/
	static int	hashCode(int value) {
		return 0;
	}
	/**Returns an int value with at most a single one-bit, in the position of the highest-order ("leftmost") one-bit in the specified int value.*/
	static int	highestOneBit(int i) {
		return 0;
	}
	/**Returns the value of this Integer as an int.*/
	int	intValue() {
		return 0;	
	}
	/**Returns the value of this Integer as a long after a widening primitive conversion.*/
	long	longValue() {
		return 0;
	}
	/**Returns an int value with at most a single one-bit, in the position of the lowest-order ("rightmost") one-bit in the specified int value.*/
	static int	lowestOneBit(int i) {
		return 0;
	}
	/**Returns the greater of two int values as if by calling Math.max.*/
	static int	max(int a, int b) {
		return 0;
	}
	/**Returns the smaller of two int values as if by calling Math.min.*/
	static int	min(int a, int b) {
		return 0;
	}
	/**Returns the number of zero bits preceding the highest-order ("leftmost") one-bit in the two's complement binary representation of the specified int value.*/
	static int	numberOfLeadingZeros(int i) {
		return 0;
	}
	/**Returns the number of zero bits following the lowest-order ("rightmost") one-bit in the two's complement binary representation of the specified int value.*/
	static int	numberOfTrailingZeros(int i) {
		return 0;
	}
	/**Parses the string argument as a signed decimal integer.*/
	static int	parseInt(java.lang.String s) {
		return 0;
	}
	/**Parses the string argument as a signed integer in the radix specified by the second argument.*/
	static int	parseInt(java.lang.String s, int radix) {
		return 0;
	}
	/**Parses the string argument as an unsigned decimal integer.*/
	static int	parseUnsignedInt(java.lang.String s) {
		return 0;
	}
	/**Parses the string argument as an unsigned integer in the radix specified by the second argument.*/
	static int	parseUnsignedInt(java.lang.String s, int radix) {
		return 0;
	}
	/**Returns the unsigned remainder from dividing the first argument by the second where each argument and the result is interpreted as an unsigned value.*/
	static int	remainderUnsigned(int dividend, int divisor) {
		return 0;
	}
	/**Returns the value obtained by reversing the order of the bits in the two's complement binary representation of the specified int value.*/
	static int	reverse(int i) {
		return 0;
	}
	/**Returns the value obtained by reversing the order of the bytes in the two's complement representation of the specified int value.*/
	static int	reverseBytes(int i) {
		return 0;
	}
	/**Returns the value obtained by rotating the two's complement binary representation of the specified int value left by the specified number of bits.*/
	static int	rotateLeft(int i, int distance) {
		return 0;
	}
	/**Returns the value obtained by rotating the two's complement binary representation of the specified int value right by the specified number of bits.*/
	static int	rotateRight(int i, int distance) {
		return 0;
	}
	/**Returns the value of this Integer as a short after a narrowing primitive conversion.*/
	short	shortValue() {
		return 0;
	}
	/**Returns the signum function of the specified int value.*/
	static int	signum(int i) {
		return 0;
	}
	/**Adds two integers together as per the + operator.*/
	static int	sum(int a, int b) {
		return 0;
	}
	/**Returns a string representation of the integer argument as an unsigned integer in base 2.*/
	static java.lang.String	toBinaryString(int i) {
		return null;
	}
	/**Returns a string representation of the integer argument as an unsigned integer in base 16.*/
	static java.lang.String	toHexString(int i) {
		return null;
	}
	/**Returns a string representation of the integer argument as an unsigned integer in base 8.*/
	static java.lang.String	toOctalString(int i) {
		return null;
	}
	/**Returns a java.lang.String object representing this Integer's value.*/
	public java.lang.String	toString() {
		return null;
	}
	/**Returns a java.lang.String object representing the specified integer.*/
	static java.lang.String	toString(int i) {
		return null;
	}
	/**Returns a string representation of the first argument in the radix specified by the second argument.*/
	static java.lang.String	toString(int i, int radix) {
		return null;
	}
	/**Converts the argument to a long by an unsigned conversion.*/
	static long	toUnsignedLong(int x) {
		return 0;
	}
	/**Returns a string representation of the argument as an unsigned decimal value.*/
	static java.lang.String	toUnsignedString(int i) {
		return null;
	}
	/**Returns a string representation of the first argument as an unsigned integer value in the radix specified by the second argument.*/
	static java.lang.String	toUnsignedString(int i, int radix) {
		return null;
	}
	/**Returns an Integer instance representing the specified int value.*/
	static java.lang.Integer	valueOf(int i) {
		return null;
	}
	/**Returns an Integer object holding the value of the specified java.lang.String.*/
	static java.lang.Integer	valueOf(java.lang.String s) {
		return null;
	}
	/**Returns an Integer object holding the value extracted from the specified java.lang.String when parsed with the radix given by the second argument.*/
	static java.lang.Integer	valueOf(java.lang.String s, int radix) {
		return null;
	}
	
	
}