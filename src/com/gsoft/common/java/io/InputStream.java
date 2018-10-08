package com.gsoft.common.java.io;

import java.io.Closeable;

public abstract class InputStream implements Closeable {
	/**Returns an estimate of the number of bytes that can be read (or skipped over) from this input stream without blocking by the next invocation of a method for this input stream.*/
	int	available() {
		return 0;
		
	}
	/**Closes this input stream and releases any system resources associated with the stream.*/
	public void	close() {
		
	}
	/**Marks the current position in this input stream.*/
	void	mark(int readlimit) {
		
	}
	/**Tests if this input stream supports the mark and reset methods.*/
	boolean	markSupported() {
		return false;
		
	}
	/**Reads the next byte of data from the input stream.*/
	abstract int	read();
	
	/**Reads some number of bytes from the input stream and stores them into the buffer array b.*/
	int	read(byte[] b) {
		return 0;			
	}
	/**Reads up to len bytes of data from the input stream into an array of bytes.*/
	int	read(byte[] b, int off, int len) {
		return len;			
	}
	/**Repositions this stream to the position at the time the mark method was last called on this input stream.*/
	void	reset() {
		
	}
	/**Skips over and discards n bytes of data from this input stream.*/
	long	skip(long n) {
		return n;
	}
	
}