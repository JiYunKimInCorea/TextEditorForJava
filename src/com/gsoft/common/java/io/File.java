package com.gsoft.common.java.io;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URL;

public class File {
	/**The system-dependent path-separator character, represented as a string for convenience.*/
	static String	pathSeparator;
	/**The system-dependent path-separator character.*/
	static char	pathSeparatorChar;
	/**The system-dependent default name-separator character, represented as a string for convenience.*/
	static String	separator;
	/**The system-dependent default name-separator character.*/
	static char	separatorChar;
	
	/**Creates a new File instance from a parent abstract pathname and a child pathname string.*/
	File(File parent, String child) {
		
	}
	/**Creates a new File instance by converting the given pathname string into an abstract pathname.*/
	File(String pathname) {
		
	}
	/**Creates a new File instance from a parent pathname string and a child pathname string.*/
	File(String parent, String child) {
		
	}
	/**Creates a new File instance by converting the given file: URI into an abstract pathname.*/
	File(URI uri) {
		
	}
	
	/**Tests whether the application can execute the file denoted by this abstract pathname.*/
	boolean	canExecute() {
		return false;
		
	}
	/**Tests whether the application can read the file denoted by this abstract pathname.*/
	boolean	canRead() {
		return false;
		
	}
	/**Tests whether the application can modify the file denoted by this abstract pathname.*/
	boolean	canWrite() {
		return false;
		
	}
	/**Compares two abstract pathnames lexicographically.*/
	int	compareTo(File pathname) {
		return 0;
		
	}
	/**Atomically creates a new, empty file named by this abstract pathname if and only if a file with this name does not yet exist.*/
	boolean	createNewFile() {
		return false;
		
	}
	/**Creates an empty file in the default temporary-file directory, using the given prefix and suffix to generate its name.*/
	static File	createTempFile(String prefix, String suffix) {
		return null;
		
	}
	/**Creates a new empty file in the specified directory, using the given prefix and suffix strings to generate its name.*/
	static File	createTempFile(String prefix, String suffix, File directory) {
		return directory;
		
	}
	/**Deletes the file or directory denoted by this abstract pathname.*/
	boolean	delete() {
		return false;
		
	}
	/**Requests that the file or directory denoted by this abstract pathname be deleted when the virtual machine terminates.*/
	void	deleteOnExit() {
		
	}
	/**Tests this abstract pathname for equality with the given object.*/
	public boolean	equals(Object obj) {
		return false;
		
	}
	/**Tests whether the file or directory denoted by this abstract pathname exists.*/
	boolean	exists() {
		return false;
		
	}
	/**Returns the absolute form of this abstract pathname.*/
	File	getAbsoluteFile() {
		return null;
		
	}
	/**Returns the absolute pathname string of this abstract pathname.*/
	String	getAbsolutePath() {
		return null;
		
	}
	/**Returns the canonical form of this abstract pathname.*/
	File	getCanonicalFile() {
		return null;
		
	}
	/**Returns the canonical pathname string of this abstract pathname.*/
	String	getCanonicalPath() {
		return null;
		
	}
	/**Returns the number of unallocated bytes in the partition named by this abstract path name.*/
	long	getFreeSpace() {
		return 0;
		
	}
	/**Returns the name of the file or directory denoted by this abstract pathname.*/
	String	getName() {
		return null;
		
	}
	/**Returns the pathname string of this abstract pathname's parent, or null if this pathname does not name a parent directory.*/
	String	getParent() {
		return null;
		
	}
	/**Returns the abstract pathname of this abstract pathname's parent, or null if this pathname does not name a parent directory.*/
	File	getParentFile() {
		return null;
		
	}
		/**Converts this abstract pathname into a pathname string.*/
	String	getPath() {
		return null;
		
	}
	/**Returns the size of the partition named by this abstract pathname.*/
	long	getTotalSpace() {
		return 0;
		
	}
	/**Returns the number of bytes available to this virtual machine on the partition named by this abstract pathname.*/
	long	getUsableSpace() {
		return 0;
		
	}
	/**Computes a hash code for this abstract pathname.*/
	public int	hashCode() {
		return 0;
		
	}
	/**Tests whether this abstract pathname is absolute.*/
	boolean	isAbsolute() {
		return false;
		
	}
	/**Tests whether the file denoted by this abstract pathname is a directory.*/
	boolean	isDirectory() {
		return false;
		
	}
	/**Tests whether the file denoted by this abstract pathname is a normal file.*/
	boolean	isFile() {
		return false;
		
	}
	/**Tests whether the file named by this abstract pathname is a hidden file.*/
	boolean	isHidden() {
		return false;
		
	}
	/**Returns the time that the file denoted by this abstract pathname was last modified.*/
	long	lastModified() {
		return 0;
		
	}
	/**Returns the length of the file denoted by this abstract pathname.*/
	long	length() {
		return 0;
		
	}
	/**Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname.*/
	String[]	list() {
		return null;
		
	}
	/**Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname that satisfy the specified filter.*/
	String[]	list(FilenameFilter filter) {
		return null;
		
	}
	/**Returns an array of abstract pathnames denoting the files in the directory denoted by this abstract pathname.*/
	File[]	listFiles() {
		return null;
		
	}
	/**Returns an array of abstract pathnames denoting the files and directories in the directory denoted by this abstract pathname that satisfy the specified filter.*/
	File[]	listFiles(FileFilter filter) {
		return null;
		
	}
	/**Returns an array of abstract pathnames denoting the files and directories in the directory denoted by this abstract pathname that satisfy the specified filter.*/
	File[]	listFiles(FilenameFilter filter) {
		return null;
		
	}
	/**List the available filesystem roots.*/
	static File[]	listRoots() {
		return null;
		
	}
	/**Creates the directory named by this abstract pathname.*/
	boolean	mkdir() {
		return false;
		
	}
	/**Creates the directory named by this abstract pathname, including any necessary but nonexistent parent directories.*/
	boolean	mkdirs() {
		return false;
		
	}
	/**Renames the file denoted by this abstract pathname.*/
	boolean	renameTo(File dest) {
		return false;
		
	}
	/**A convenience method to set the owner's execute permission for this abstract pathname.*/
	boolean	setExecutable(boolean executable) {
		return executable;
		
	}
	/**Sets the owner's or everybody's execute permission for this abstract pathname.*/
	boolean	setExecutable(boolean executable, boolean ownerOnly) {
		return ownerOnly;
		
	}
	/**Sets the last-modified time of the file or directory named by this abstract pathname.*/
	boolean	setLastModified(long time) {
		return false;
		
	}
	/**A convenience method to set the owner's read permission for this abstract pathname.*/
	boolean	setReadable(boolean readable) {
		return readable;
		
	}
	/**Sets the owner's or everybody's read permission for this abstract pathname.*/
	boolean	setReadable(boolean readable, boolean ownerOnly) {
		return ownerOnly;
		
	}
	/**Marks the file or directory named by this abstract pathname so that only read operations are allowed.*/
	boolean	setReadOnly() {
		return false;
		
	}
	/**A convenience method to set the owner's write permission for this abstract pathname.*/
	boolean	setWritable(boolean writable) {
		return writable;
		
	}
	/**Sets the owner's or everybody's write permission for this abstract pathname.*/
	boolean	setWritable(boolean writable, boolean ownerOnly) {
		return ownerOnly;
		
	}
	/**Returns a java.nio.file.Path object constructed from the this abstract path.*/
	/*Path	toPath() {
		return null;
		
	}*/
	/**Returns the pathname string of this abstract pathname.*/
	public String	toString() {
		return null;
		
	}
	/**Constructs a file: URI that represents this abstract pathname.*/
	URI	toURI() {
		return null;
		
	}
	/**Deprecated. 
	This method does not automatically escape characters that are illegal in URLs. 
	It is recommended that new code convert an abstract pathname into a URL by first converting it into a URI, via the toURI method, 
	and then converting the URI into a URL via the URI.toURL method.*/
	URL	toURL() {
		return null;
		
	}
	
	
	
}