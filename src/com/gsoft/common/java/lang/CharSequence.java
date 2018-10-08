package com.gsoft.common.java.lang;

public interface CharSequence {
	/**Returns the char value at the specified index.*/
	char	charAt(int index);
	/**Returns a stream of int zero-extending the char values from this sequence.*/
	//java.util.Stream.IntStream	chars();
	/**Returns a stream of code point values from this sequence.*/
	//java.util.Stream.IntStream	codePoints();
	/**Returns the length of this character sequence.*/
	int	length();
	/**Returns a CharSequence that is a subsequence of this sequence.*/
	CharSequence	subSequence(int start, int end);
	/**Returns a string containing the characters in this sequence in the same order as this sequence.*/
	java.lang.String	toString();
	
}