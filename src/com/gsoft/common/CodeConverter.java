package com.gsoft.common;

import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.IO.ReturnOfReadString;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.Util.ArrayList;
import java.io.File;

public class CodeConverter {
	public static String getRemainder(File file, String curDir) {
		String absFileName = file.getAbsolutePath();
		int i;
		for (i=0; i<curDir.length(); i++) {
			char c = curDir.charAt(i);
			char c2 = absFileName.charAt(i);
			if (c!=c2) break;
		}
		String r = "";
		if (i+1<absFileName.length()) {
			r = absFileName.substring(i+1, absFileName.length());
		}
		return r;
	}
	
	
	
	
	public static void convertToUTF8(String srcPath, String destPath) {
		ArrayList list = IO.FileHelper.getFileList(srcPath);
		int i;
		if (list.count==0) return;
		//String curDir = ((File)list.getItem(0)).getAbsolutePath();
		
		if (destPath.charAt(destPath.length()-1)==File.separatorChar) {
			destPath = destPath.substring(0, destPath.length()-1);
		}
		
		String curDir = srcPath;
		for (i=0; i<list.count; i++) {
			File file = (File) list.getItem(i);
			String remainder = getRemainder(file, curDir);
			String destFileName = destPath + File.separator + remainder;
			File destFile = new File(destFileName);
			if (file.isDirectory()) {
				destFile.mkdir();
			}
			else { 
				if (FileHelper.getExt(file.getAbsolutePath()).equals(".java")==false) {
					continue;
				}
				ReturnOfReadString input = IO.readString(file.getAbsolutePath());
				if (input.result!=null) {
					IO.writeString(destFileName, input.result, TextFormat.UTF_8, false, true);
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String srcPath = "D:\\kjy\\eclipse_workspace\\TextEditorForJava\\TextEditorForJava\\assets\\files";
		String destPath = "D:\\kjy\\eclipse_workspace\\TextEditorForJava\\TextEditorForJava\\assets\\files2";
		
		CodeConverter.convertToUTF8(srcPath, destPath);
		
	}

}