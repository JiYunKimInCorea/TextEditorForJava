package com.gsoft.common.java.lang;

import java.nio.charset.Charset;
import java.util.Locale;

public class String implements java.lang.CharSequence {
	/**Initializes a newly created String object so that it represents an empty character sequence.*/
	String() {}
	/**Constructs a new String by decoding the specified array of bytes using the platform's default charset.*/
	String(byte[] bytes) {}
	/**Constructs a new String by decoding the specified array of bytes using the specified charset.*/
	String(byte[] bytes, Charset charset){
		
	}
	/**Deprecated.This method does not properly convert bytes into characters. As of JDK 1.1, the preferred way to do this is via the String constructors that take a Charset, charset name, or that use the platform's default charset.*/
	String(byte[] ascii, int hibyte){
		
	}
	 
	/**Constructs a new String by decoding the specified subarray of bytes using the platform's default charset.*/
	String(byte[] bytes, int offset, int length) {
		
	}
	/**Constructs a new String by decoding the specified subarray of bytes using the specified charset.*/
	String(byte[] bytes, int offset, int length, Charset charset){
		
	}
	/**Deprecated. 
	This method does not properly convert bytes into characters. As of JDK 1.1, the preferred way to do this is via the String constructors that take a Charset, charset name, or that use the platform's default charset.*/
	String(byte[] ascii, int hibyte, int offset, int count){
		
	}
	/**Constructs a new String by decoding the specified subarray of bytes using the specified charset.*/
	String(byte[] bytes, int offset, int length, String charsetName) {
		
	}
	/**Constructs a new String by decoding the specified array of bytes using the specified charset.*/
	String(byte[] bytes, String charsetName) {
		
	}
	/**Allocates a new String so that it represents the sequence of characters currently contained in the character array argument.*/
	String(char[] value) {
		
	}
	/**Allocates a new String that contains characters from a subarray of the character array argument.*/
	String(char[] value, int offset, int count) {
		
	}
	/**Allocates a new String that contains characters from a subarray of the Unicode code point array argument.*/
	String(int[] codePoints, int offset, int count) {
		
	}
	/**Initializes a newly created String object so that it represents the same sequence of characters as the argument; in other words, the newly created string is a copy of the argument string.*/
	String(String original){
		
	}
	/**Allocates a new string that contains the sequence of characters currently contained in the string buffer argument.*/
	String(StringBuffer buffer) {
		
	}
	/**Allocates a new string that contains the sequence of characters currently contained in the string builder argument.*/
	String(StringBuilder builder) {
		
	}
	

	/** Returns the char value at the specified index.*/
	public char	charAt(int index) {
		return 0;
		
	}
	/** Returns the character (Unicode code point) at the specified index.*/
	int	codePointAt(int index) {
		return index;
		
	}
	/**Returns the character (Unicode code point) before the specified index.*/
	int	codePointBefore(int index) {
		return index;
		
	}
	/**Returns the number of Unicode code points in the specified text range of this java.lang.String.*/
	int	codePointCount(int beginIndex, int endIndex) {
		return endIndex;
		
	}
	/**Compares two strings lexicographically.*/
	public int	compareTo(java.lang.String anotherString) {
		return 0;
	}
	/**Compares two strings lexicographically, ignoring case differences.*/
	int	compareToIgnoreCase(java.lang.String str) {
		return 0;
	}
	/**Concatenates the specified string to the end of this string.*/
	java.lang.String	concat(java.lang.String str) {
		return null;
	}
	/**Returns true if and only if this string contains the specified sequence of char values.*/
	boolean	contains(java.lang.CharSequence s) {
		return false;
	}
	/**Compares this string to the specified java.lang.CharSequence.*/
	boolean	contentEquals(java.lang.CharSequence cs) {
		return false;
	}
	/**Compares this string to the specified StringBuffer.*/
	boolean	contentEquals(StringBuffer sb) {
		return false;
	}
	/**Equivalent to valueOf(char[]).*/
	static java.lang.String	copyValueOf(char[] data) {
		return null;
	}
	/**Equivalent to valueOf(char[], int, int).*/
	static java.lang.String	copyValueOf(char[] data, int offset, int count) {
		return null;
	}
	/**Tests if this string ends with the specified suffix.*/
	boolean	endsWith(java.lang.String suffix) {
		return false;
	}
	/**Compares this string to the specified object.*/
	public boolean	equals(Object anObject) {
		return false;
	}
	/**Compares this java.lang.String to another java.lang.String, ignoring case considerations.*/
	boolean	equalsIgnoreCase(java.lang.String anotherString) {
		return false;
	}
	/**Returns a formatted string using the specified locale, format string, and arguments.*/
	static java.lang.String	format(Locale l, java.lang.String format, Object... args) {
		return null;
	}
	/**Returns a formatted string using the specified format string and arguments.*/
	static java.lang.String	format(java.lang.String format, Object... args) {
		return null;
	}
	/**Encodes this java.lang.String into a sequence of bytes using the platform's default charset, storing the result into a new byte array.*/
	byte[]	getBytes() {
		return null;
	}
	/**Encodes this java.lang.String into a sequence of bytes using the given charset, storing the result into a new byte array.*/
	byte[]	getBytes(Charset charset) {
		return null;
	}
	/**Deprecated.
	 * This method does not properly convert characters into bytes. As of JDK 1.1, the preferred way to do this is via the getBytes() method, which uses the platform's default charset.*/
	void	getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) {
		
	}
	 
	/**Encodes this java.lang.String into a sequence of bytes using the named charset, storing the result into a new byte array.*/
	byte[]	getBytes(java.lang.String charsetName) {
		return null;
	}
	/**Copies characters from this string into the destination character array.*/
	void	getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		
	}
	/**Returns a hash code for this string.*/
	public int	hashCode() {
		return 0;
	}
	/**Returns the index within this string of the first occurrence of the specified character.*/
	int	indexOf(int ch) {
		return 0;
	}
	/**Returns the index within this string of the first occurrence of the specified character, starting the search at the specified index.*/
	int	indexOf(int ch, int fromIndex) {
		return 0;
	}
	/**Returns the index within this string of the first occurrence of the specified substring.*/
	int	indexOf(java.lang.String str) {
		return 0;
	}
	/**Returns the index within this string of the first occurrence of the specified substring, starting at the specified index.*/
	int	indexOf(java.lang.String str, int fromIndex) {
		return 0;
	}
	/**Returns a canonical representation for the string object.*/
	java.lang.String	intern() {
		return null;
	}
	/**Returns true if, and only if, length() is 0.*/
	boolean	isEmpty() {
		return false;
	}
	/**Returns a new java.lang.String composed of copies of the java.lang.CharSequence elements joined together with a copy of the specified delimiter.*/
	static java.lang.String	join(java.lang.CharSequence delimiter, java.lang.CharSequence... elements) {
		return null;
	}
	/**Returns a new java.lang.String composed of copies of the java.lang.CharSequence elements joined together with a copy of the specified delimiter.*/
	static java.lang.String	join(java.lang.CharSequence delimiter, Iterable<? extends java.lang.CharSequence> elements) {
		return null;
	}
	/**Returns the index within this string of the last occurrence of the specified character.*/
	int	lastIndexOf(int ch) {
		return 0;
	}
	/**Returns the index within this string of the last occurrence of the specified character, searching backward starting at the specified index.*/
	int	lastIndexOf(int ch, int fromIndex) {
		return 0;
	}
	/**Returns the index within this string of the last occurrence of the specified substring.*/
	int	lastIndexOf(java.lang.String str) {
		return 0;
	}
	/**Returns the index within this string of the last occurrence of the specified substring, searching backward starting at the specified index.*/
	int	lastIndexOf(java.lang.String str, int fromIndex) {
		return 0;
	}
	/**Returns the length of this string.*/
	public int	length() {
		return 0;
	}
	/**Tells whether or not this string matches the given regular expression.*/
	boolean	matches(java.lang.String regex) {
		return false;
	}
	/**Returns the index within this java.lang.String that is offset from the given index by codePointOffset code points.*/
	int	offsetByCodePoints(int index, int codePointOffset) {
		return 0;
	}
	/**Tests if two string regions are equal.*/
	boolean	regionMatches(boolean ignoreCase, int toffset, java.lang.String other, int ooffset, int len) {
		return false;
	}
	/**Tests if two string regions are equal.*/
	boolean	regionMatches(int toffset, java.lang.String other, int ooffset, int len) {
		return false;
	}
	/**Returns a string resulting from replacing all occurrences of oldChar in this string with newChar.*/
	java.lang.String	replace(char oldChar, char newChar) {
		return null;
	}
	/**Replaces each substring of this string that matches the literal target sequence with the specified literal replacement sequence.*/
	java.lang.String	replace(java.lang.CharSequence target, java.lang.CharSequence replacement) {
		return null;
	}
	/**Replaces each substring of this string that matches the given regular expression with the given replacement.*/
	java.lang.String	replaceAll(java.lang.String regex, java.lang.String replacement) {
		return null;
	}
	/**Replaces the first substring of this string that matches the given regular expression with the given replacement.*/
	java.lang.String	replaceFirst(java.lang.String regex, java.lang.String replacement) {
		return null;
	}
	/**Splits this string around matches of the given regular expression.*/
	java.lang.String[]	split(java.lang.String regex) {
		return null;
	}
	/**Splits this string around matches of the given regular expression.*/
	java.lang.String[]	split(java.lang.String regex, int limit) {
		return null;
	}
	/**Tests if this string starts with the specified prefix.*/
	boolean	startsWith(java.lang.String prefix) {
		return false;
	}
	/**Tests if the substring of this string beginning at the specified index starts with the specified prefix.*/
	boolean	startsWith(java.lang.String prefix, int toffset) {
		return false;
	}
	/**Returns a character sequence that is a subsequence of this sequence.*/
	public java.lang.CharSequence	subSequence(int beginIndex, int endIndex) {
		return null;
	}
	/**Returns a string that is a substring of this string.*/
	java.lang.String	substring(int beginIndex) {
		return null;
	}
	/**Returns a string that is a substring of this string.*/
	java.lang.String	substring(int beginIndex, int endIndex) {
		return null;
	}
	/**Converts this string to a new character array.*/
	char[]	toCharArray() {
		return null;
	}
	/**Converts all of the characters in this java.lang.String to lower case using the rules of the default locale.*/
	java.lang.String	toLowerCase() {
		return null;
	}
	/**Converts all of the characters in this java.lang.String to lower case using the rules of the given Locale.*/
	java.lang.String	toLowerCase(Locale locale) {
		return null;
	}
	/**This object (which is already a string!) is itself returned.*/
	public java.lang.String	toString() {
		return null;
	}
	/**Converts all of the characters in this java.lang.String to upper case using the rules of the default locale.*/
	java.lang.String	toUpperCase() {
		return null;
	}
	/**Converts all of the characters in this java.lang.String to upper case using the rules of the given Locale.*/
	java.lang.String	toUpperCase(Locale locale) {
		return null;
	}
	/**Returns a string whose value is this string, with any leading and trailing whitespace removed.*/
	java.lang.String	trim() {
		return null;
	}
	/**Returns the string representation of the boolean argument.*/
	static java.lang.String	valueOf(boolean b) {
		return null;
	}
	/**Returns the string representation of the char argument.*/
	static java.lang.String	valueOf(char c) {
		return null;
	}
	/**Returns the string representation of the char array argument.*/
	static java.lang.String	valueOf(char[] data) {
		return null;
	}
	/**Returns the string representation of a specific subarray of the char array argument.*/
	static java.lang.String	valueOf(char[] data, int offset, int count) {
		return null;
	}
	/**Returns the string representation of the double argument.*/
	static java.lang.String	valueOf(double d) {
		return null;
	}
	/**Returns the string representation of the float argument.*/
	static java.lang.String	valueOf(float f) {
		return null;
	}
	/**Returns the string representation of the int argument.*/
	static java.lang.String	valueOf(int i) {
		return null;
	}
	/**Returns the string representation of the long argument.*/
	static java.lang.String	valueOf(long l) {
		return null;
	}
	
	static java.lang.String	valueOf(Object obj) {
		return null;
	}
}//class String 