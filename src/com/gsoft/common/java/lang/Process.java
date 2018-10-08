package com.gsoft.common.java.lang;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public abstract class Process {
	/**Kills the subprocess.*/
	abstract void	destroy();
	/**Kills the subprocess.*/
	java.lang.Process	destroyForcibly() {
		return null;
		
	}
	/**Returns the exit value for the subprocess.*/
	abstract int	exitValue();
	/**Returns the input stream connected to the error output of the subprocess.*/
	abstract InputStream	getErrorStream();
	/**Returns the input stream connected to the normal output of the subprocess.*/
	abstract InputStream	getInputStream();
	/**Returns the output stream connected to the normal input of the subprocess.*/
	abstract OutputStream	getOutputStream();
	/**Tests whether the subprocess represented by this Process is alive.*/
	boolean	isAlive() {
		return false;
		
	}
	/**Causes the current thread to wait, if necessary, until the process represented by this Process object has terminated.*/
	abstract int	waitFor();
	/**Causes the current thread to wait, if necessary, until the subprocess represented by this Process object has terminated, or the specified waiting time elapses.*/
	boolean	waitFor(long timeout, TimeUnit unit) {
		return false;
		
	}
	
}