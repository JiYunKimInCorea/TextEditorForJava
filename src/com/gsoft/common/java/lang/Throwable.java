package com.gsoft.common.java.lang;

import java.io.PrintStream;
import java.io.PrintWriter;

public class Throwable {
	/**Constructs a new throwable with null as its detail message.*/
	protected Throwable() {
		
	}
	/**Constructs a new throwable with the specified detail message.*/
	protected Throwable(java.lang.String message) {
		
	}
	/**Constructs a new throwable with the specified detail message and cause.*/
	protected Throwable(java.lang.String message, java.lang.Throwable cause) {
		
	}
	/**Constructs a new throwable with the specified detail message, cause, suppression enabled or disabled, and writable stack trace enabled or disabled.*/
	protected Throwable(java.lang.String message, java.lang.Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		
	}
	/**Constructs a new throwable with the specified cause and a detail message of (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).*/
	protected Throwable(java.lang.Throwable cause) {
		
	}
	/**Appends the specified exception to the exceptions that were suppressed in order to deliver this exception.*/
	void	addSuppressed(java.lang.Throwable exception) {
		
	}
	/**Fills in the execution stack trace.*/
	java.lang.Throwable	fillInStackTrace() {
		return null;
	}
	/**Returns the cause of this throwable or null if the cause is nonexistent or unknown.*/
	java.lang.Throwable	getCause() {
		return null;
	}
	/**Creates a localized description of this throwable.*/
	java.lang.String	getLocalizedMessage() {
		return null;
	}
	/**Returns the detail message string of this throwable.*/
	java.lang.String	getMessage() {
		return null;
	}
	/**Provides programmatic access to the stack trace information printed by printStackTrace().*/
	StackTraceElement[]	getStackTrace() {
		return null;
	}
	/**Returns an array containing all of the exceptions that were suppressed, typically by the try-with-resources statement, in order to deliver this exception.*/
	java.lang.Throwable[]	getSuppressed() {
		return null;
	}
	/**Initializes the cause of this throwable to the specified value.*/
	java.lang.Throwable	initCause(java.lang.Throwable cause) {
		return null;
	}
	/**Prints this throwable and its backtrace to the standard error stream.*/
	void	printStackTrace() {
		
	}
	/**Prints this throwable and its backtrace to the specified print stream.*/
	void	printStackTrace(PrintStream s) {
		
	}
	/**Prints this throwable and its backtrace to the specified print writer.*/
	void	printStackTrace(PrintWriter s) {
		
	}
	/**Sets the stack trace elements that will be returned by getStackTrace() and printed by printStackTrace() and related methods.*/
	void	setStackTrace(StackTraceElement[] stackTrace) {
		
	}
	/**Returns a short description of this throwable.*/
	public java.lang.String	toString() {
		return null;
	}
	
	
}