package com.gsoft.common.java.lang;

public class Character {
	/**Instances of this class represent particular subsets of the Unicode character set.*/
	static class 	Subset {
		
	}
	/**A family of character subsets representing the character blocks in the Unicode specification.*/
	static class 	UnicodeBlock {
		
	}
	/**A family of character subsets representing the character scripts defined in the Unicode Standard Annex #24: Script Names.*/
	static class 	UnicodeScript {
		
	}
	
	
	Character(char value) {
		
	}
	/**Determines the number of char values needed to represent the specified character (Unicode code point).*/
	static int	charCount(int codePoint) {
		return 0;
	}
	/**Returns the value of this Character object.*/
	char	charValue() {
		return 0;
	}
	/**Returns the code point at the given index of the char array.*/
	static int	codePointAt(char[] a, int index) {
		return 0;
	}
	/**Returns the code point at the given index of the char array, where only array elements with index less than limit can be used.*/
	static int	codePointAt(char[] a, int index, int limit) {
		return 0;	
	}
	/**Returns the code point at the given index of the CharSequence.*/
	static int	codePointAt(CharSequence seq, int index) {
		return 0;
	}
	/**Returns the code point preceding the given index of the char array.*/
	static int	codePointBefore(char[] a, int index) {
		return 0;
	}
	/**Returns the code point preceding the given index of the char array, where only array elements with index greater than or equal to start can be used.*/
	static int	codePointBefore(char[] a, int index, int start) {
		return 0;
	}
	/**Returns the code point preceding the given index of the CharSequence.*/
	static int	codePointBefore(CharSequence seq, int index) {
		return 0;
	}
	/**Returns the number of Unicode code points in a subarray of the char array argument.*/
	static int	codePointCount(char[] a, int offset, int count) {
		return 0;
	}
	/**Returns the number of Unicode code points in the text range of the specified char sequence.*/
	static int	codePointCount(CharSequence seq, int beginIndex, int endIndex) {
		return 0;
	}
	/**Compares two char values numerically.*/
	static int	compare(char x, char y) {
		return 0;
	}
	/**Compares two Character objects numerically.*/
	int	compareTo(Character anotherCharacter) {
		return 0;
	}
	/**Returns the numeric value of the character ch in the specified radix.*/
	static int	digit(char ch, int radix) {
		return 0;
	}
	/**Returns the numeric value of the specified character (Unicode code point) in the specified radix.*/
	static int	digit(int codePoint, int radix) {
		return 0;
	}
	/**Compares this object against the specified object.*/
	public boolean	equals(Object obj) {
		return false;
	}
	/**Determines the character representation for a specific digit in the specified radix.*/
	static char	forDigit(int digit, int radix) {
		return 0;
	}
	/**Returns the Unicode directionality property for the given character.*/
	static byte	getDirectionality(char ch) {
		return 0;
	}
	/**Returns the Unicode directionality property for the given character (Unicode code point).*/
	static byte	getDirectionality(int codePoint) {
		return 0;
	}
	/**Returns the Unicode name of the specified character codePoint, or null if the code point is unassigned.*/
	static java.lang.String	getName(int codePoint) {
		return null;
	}
	/**Returns the int value that the specified Unicode character represents.*/
	static int	getNumericValue(char ch) {
		return 0;
	}
	/**Returns the int value that the specified character (Unicode code point) represents.*/
	static int	getNumericValue(int codePoint) {
		return 0;
	}
	/**Returns a value indicating a character's general category.*/
	static int	getType(char ch) {
		return 0;
	}
	/**Returns a value indicating a character's general category.*/
	static int	getType(int codePoint) {
		return 0;
	}
	/**Returns a hash code for this Character; equal to the result of invoking charValue().*/
	public int	hashCode() {
		return 0;
	}
	/**Returns a hash code for a char value; compatible with Character.hashCode().*/
	static int	hashCode(char value) {
		return 0;
	}
	/**Returns the leading surrogate (a high surrogate code unit) of the surrogate pair representing the specified supplementary character (Unicode code point) in the UTF-16 encoding.*/
	static char	highSurrogate(int codePoint) {
		return 0;
	}
	/**Determines if the specified character (Unicode code point) is an alphabet.*/
	static boolean	isAlphabetic(int codePoint) {
		return false;
	}
	/**Determines whether the specified character (Unicode code point) is in the Basic Multilingual Plane (BMP).*/
	static boolean	isBmpCodePoint(int codePoint) {
		return false;
	}
	/**Determines if a character is defined in Unicode.*/
	static boolean	isDefined(char ch) {
		return false;
	}
	/**Determines if a character (Unicode code point) is defined in Unicode.*/
	static boolean	isDefined(int codePoint) {
		return false;
	}
	/**Determines if the specified character is a digit.*/
	static boolean	isDigit(char ch) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) is a digit.*/
	static boolean	isDigit(int codePoint) {
		return false;
	}
	/**Determines if the given char value is a Unicode high-surrogate code unit (also known as leading-surrogate code unit).*/
	static boolean	isHighSurrogate(char ch) {
		return false;
	}
	/**Determines if the specified character should be regarded as an ignorable character in a Java identifier or a Unicode identifier.*/
	static boolean	isIdentifierIgnorable(char ch) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) should be regarded as an ignorable character in a Java identifier or a Unicode identifier.*/
	static boolean	isIdentifierIgnorable(int codePoint) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) is a CJKV (Chinese, Japanese, Korean and Vietnamese) ideograph, as defined by the Unicode Standard.*/
	static boolean	isIdeographic(int codePoint) {
		return false;
	}
	/**Determines if the specified character is an ISO control character.*/
	static boolean	isISOControl(char ch) {
		return false;
	}
	/**Determines if the referenced character (Unicode code point) is an ISO control character.*/
	static boolean	isISOControl(int codePoint) {
		return false;
	}
	/**Determines if the specified character may be part of a Java identifier as other than the first character.*/
	static boolean	isJavaIdentifierPart(char ch) {
		return false;
	}
	/**Determines if the character (Unicode code point) may be part of a Java identifier as other than the first character.*/
	static boolean	isJavaIdentifierPart(int codePoint) {
		return false;
	}
	/**Determines if the specified character is permissible as the first character in a Java identifier.*/
	static boolean	isJavaIdentifierStart(char ch) {
		return false;
	}
	/**Determines if the character (Unicode code point) is permissible as the first character in a Java identifier.*/
	static boolean	isJavaIdentifierStart(int codePoint) {
		return false;
	}
	/**Deprecated. 
	Replaced by isJavaIdentifierStart(char).*/
	static boolean	isJavaLetter(char ch) {
		return false;
	}
	/**Deprecated. 
	Replaced by isJavaIdentifierPart(char).*/
	static boolean	isJavaLetterOrDigit(char ch) {
		return false;
	}
	/**Determines if the specified character is a letter.*/
	static boolean	isLetter(char ch) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) is a letter.*/
	static boolean	isLetter(int codePoint) {
		return false;
	}
	/**Determines if the specified character is a letter or digit.*/
	static boolean	isLetterOrDigit(char ch) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) is a letter or digit.*/
	static boolean	isLetterOrDigit(int codePoint) {
		return false;
	}
	/**Determines if the specified character is a lowercase character.*/
	static boolean	isLowerCase(char ch) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) is a lowercase character.*/
	static boolean	isLowerCase(int codePoint) {
		return false;
	}
	/**Determines if the given char value is a Unicode low-surrogate code unit (also known as trailing-surrogate code unit).*/
	static boolean	isLowSurrogate(char ch) {
		return false;
	}
	/**Determines whether the character is mirrored according to the Unicode specification.*/
	static boolean	isMirrored(char ch) {
		return false;
	}
	/**Determines whether the specified character (Unicode code point) is mirrored according to the Unicode specification.*/
	static boolean	isMirrored(int codePoint) {
		return false;
	}
	/**Deprecated. 
	Replaced by isWhitespace(char).*/
	static boolean	isSpace(char ch) {
		return false;
	}
	/**Determines if the specified character is a Unicode space character.*/
	static boolean	isSpaceChar(char ch) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) is a Unicode space character.*/
	static boolean	isSpaceChar(int codePoint) {
		return false;
	}
	/**Determines whether the specified character (Unicode code point) is in the supplementary character range.*/
	static boolean	isSupplementaryCodePoint(int codePoint) {
		return false;
	}
	/**Determines if the given char value is a Unicode surrogate code unit.*/
	static boolean	isSurrogate(char ch) {
		return false;
	}
	/**Determines whether the specified pair of char values is a valid Unicode surrogate pair.*/
	static boolean	isSurrogatePair(char high, char low) {
		return false;
	}
	/**Determines if the specified character is a titlecase character.*/
	static boolean	isTitleCase(char ch) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) is a titlecase character.*/
	static boolean	isTitleCase(int codePoint) {
		return false;
	}
	/**Determines if the specified character may be part of a Unicode identifier as other than the first character.*/
	static boolean	isUnicodeIdentifierPart(char ch) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) may be part of a Unicode identifier as other than the first character.*/
	static boolean	isUnicodeIdentifierPart(int codePoint) {
		return false;
	}
	/**Determines if the specified character is permissible as the first character in a Unicode identifier.*/
	static boolean	isUnicodeIdentifierStart(char ch) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) is permissible as the first character in a Unicode identifier.*/
	static boolean	isUnicodeIdentifierStart(int codePoint) {
		return false;
	}
	/**Determines if the specified character is an uppercase character.*/
	static boolean	isUpperCase(char ch) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) is an uppercase character.*/
	static boolean	isUpperCase(int codePoint) {
		return false;
	}
	/**Determines whether the specified code point is a valid Unicode code point value.*/
	static boolean	isValidCodePoint(int codePoint) {
		return false;
	}
	/**Determines if the specified character is white space according to Java.*/
	static boolean	isWhitespace(char ch) {
		return false;
	}
	/**Determines if the specified character (Unicode code point) is white space according to Java.*/
	static boolean	isWhitespace(int codePoint) {
		return false;
	}
		/**Returns the trailing surrogate (a low surrogate code unit) of the surrogate pair representing the specified supplementary character (Unicode code point) in the UTF-16 encoding.*/
	static char	lowSurrogate(int codePoint) {
		return 0;
	}
	/**Returns the index within the given char subarray that is offset from the given index by codePointOffset code points.*/
	static int	offsetByCodePoints(char[] a, int start, int count, int index, int codePointOffset) {
		return 0;
	}
	/**Returns the index within the given char sequence that is offset from the given index by codePointOffset code points.*/
	static int	offsetByCodePoints(CharSequence seq, int index, int codePointOffset) {
		return 0;
	}
	/**Returns the value obtained by reversing the order of the bytes in the specified char value.*/
	static char	reverseBytes(char ch) {
		return 0;
	}
	/**Converts the specified character (Unicode code point) to its UTF-16 representation stored in a char array.*/
	static char[]	toChars(int codePoint) {
		return null;
	}
	/**Converts the specified character (Unicode code point) to its UTF-16 representation.*/
	static int	toChars(int codePoint, char[] dst, int dstIndex) {
		return 0;
	}
	/**Converts the specified surrogate pair to its supplementary code point value.*/
	static int	toCodePoint(char high, char low) {
		return 0;
	}
	/**Converts the character argument to lowercase using case mapping information from the UnicodeData file.*/
	static char	toLowerCase(char ch) {
		return 0;
	}
	/**Converts the character (Unicode code point) argument to lowercase using case mapping information from the UnicodeData file.*/
	static int	toLowerCase(int codePoint) {
		return 0;
	}
	/**Returns a String object representing this Character's value.*/
	public java.lang.String	toString() {
		return null;
	}
	/**Returns a String object representing the specified char.*/
	static java.lang.String	toString(char c) {
		return null;
	}
	/**Converts the character argument to titlecase using case mapping information from the UnicodeData file.*/
	static char	toTitleCase(char ch) {
		return 0;	
	}
	/**Converts the character (Unicode code point) argument to titlecase using case mapping information from the UnicodeData file.*/
	static int	toTitleCase(int codePoint) {
		return 0;
	}
	/**Converts the character argument to uppercase using case mapping information from the UnicodeData file.*/
	static char	toUpperCase(char ch) {
		return 0;
	}
	/**Converts the character (Unicode code point) argument to uppercase using case mapping information from the UnicodeData file.*/
	static int	toUpperCase(int codePoint) {
		return 0;
	}
	/**Returns a Character instance representing the specified char value.*/
	static Character	valueOf(char c) {
		return null;
	}
	
}