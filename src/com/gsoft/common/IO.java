package com.gsoft.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;

import com.gsoft.common.Charset.Codeset;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Util.Array;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListByte;
import com.gsoft.common.Util.ArrayListChar;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.BufferByte;
import com.gsoft.common.Util.HighArray;
import com.gsoft.common.Util.HighArrayForReading_char;
import com.gsoft.common.Util.HighArray_byte;
import com.gsoft.common.encoding.MS949.MS949;
import com.gsoft.common.encoding.MS949.MS949.Item;
import com.gsoft.common.encoding.MS949.MS949.Language;
import com.gsoft.common.gui.Control;
import com.gsoft.common.Encoding.KSC5601;
//import com.gsoft.common.Encoding.EncodingFormatException;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

public class IO {
	
	public static byte BYTE_MIN_VALUE = -16*8; //-2^7
	public static byte BYTE_MAX_VALUE = (byte) -(BYTE_MIN_VALUE+1);//-BYTE_MIN_VALUE-1
	
	public static short SHORT_MIN_VALUE = -16*16*16*8;//-2^15 = -16^3*8 = -16*16*16*8;
	public static short SHORT_MAX_VALUE = (short) - (SHORT_MIN_VALUE+1); //2^15-1;
	
	public static char CHAR_MIN_VALUE = 0;
	public static char CHAR_MAX_VALUE = 16*16*16*16-1; //2^16-1;
	
	public static int INTEGER_MIN_VALUE = -16*16*16*8*16*16*16*16;   //-2^31 = -2^15 * 2^16;
	public static int INTEGER_MAX_VALUE =  -(INTEGER_MIN_VALUE+1); // 2^31-1;
	
	public static long LONG_MIN_VALUE = -16*16*16*8*16*16*16*16 * 16*16*16*8*16*16*16*16 * 2; //-2^63 = -2^31 * 2^31 * 2;
	public static long LONG_MAX_VALUE = -(LONG_MIN_VALUE+1); //2^63-1;
	
	
	
	
	public static MS949 g_MS949;
	
	public static String LocalComputer = "LocalComputer"; 
	
	/** 리틀 엔디언이면 true이고 빅 엔디언이면 false이다.<br>
	 * 디폴트로 true이므로 little endian이 기본이다. 만약에 big endian을 쓰고 싶다면 
	 * 빅엔디안으로 바꾸기전에 IsLittleEndian의 값을 백업을 해놓고 빅엔디안으로 read/write를 한후에 
	 * 다시 백업한 값으로 복원을 해야한다. 왜냐하면 다른 곳에서도 파일입출력을 하기 위해서이다.
	 */
	//public static boolean IsLittleEndian = true;
	
	
	public enum TextFormat {
		UTF_8,
		UTF_16,
		MS949_Korean
	}
	public static TextFormat textFormat = TextFormat.UTF_16;
	
	
	
	//public static int DefaultBufferSize = 10000000;
	public static int DefaultBufferSizeParam = 3;
	
		
	public static class FileHelper {
		
		/** 윈도우즈의 경우에는 c:, d: 등을 리턴하고 안드로이드의 경우에는 ""을 리턴한다.*/
		public static String getPartitionName() {
			String curDir = System.getProperties().getProperty("user.dir");
			if (curDir.length()>1 && curDir.charAt(1)==':') {
				return curDir.substring(0, 2);
			}
			return ""; 
		}
		
		public static class LanguageAndTextFormat {
			public com.gsoft.common.Compiler_types.Language lang;
			public TextFormat format;
			
			public LanguageAndTextFormat(com.gsoft.common.Compiler_types.Language lang, TextFormat format) {
				this.lang = lang;
				this.format = format;
			}
		}
		
		/** 파일 확장자별로 language 와 textFormat 을 가져온다. 
		 * 확장자 txt 등은 null 을 리턴한다.*/
		public static LanguageAndTextFormat getLanguageAndTextFormat(String path) {
			String filenameExceptExt;
			String ext;	
			
			filenameExceptExt = FileHelper.getFilenameExceptExt(path);
			ext = FileHelper.getExt(path);
			path = filenameExceptExt + ext;
			
				
			
			TextFormat format = null;
			com.gsoft.common.Compiler_types.Language lang = null;
			
			if (ext.equals(".java")) {
				format = TextFormat.UTF_8;
				lang = com.gsoft.common.Compiler_types.Language.Java;
				CommonGUI_SettingsDialog.settingsDialog.setTextSaveFormat(2);
			}
			else if (ext.equals(".c") || ext.equals(".cpp")) {
				format = TextFormat.UTF_8;
				lang = com.gsoft.common.Compiler_types.Language.C;
				CommonGUI_SettingsDialog.settingsDialog.setTextSaveFormat(2);
			}
			else if (ext.equals(".htm") || ext.equals(".html")) {
				format = TextFormat.UTF_8;
				lang = com.gsoft.common.Compiler_types.Language.Html;
				CommonGUI_SettingsDialog.settingsDialog.setTextSaveFormat(0);
			}
			else if (ext.equals(".sh")) {
				format = TextFormat.UTF_8;
				CommonGUI_SettingsDialog.settingsDialog.setTextSaveFormat(0);
			}
			else if (ext.equals(".class")) {
				format = TextFormat.UTF_8;
				lang = com.gsoft.common.Compiler_types.Language.Class;
				CommonGUI_SettingsDialog.settingsDialog.setTextSaveFormat(0);
			}
			else {
				return null;
			}
			/*else if (ext.equals(".txt")) {
				format = TextFormat.UTF_8;
			}*/
			
			return new LanguageAndTextFormat(lang, format);
			
		}
		
		
		public static int getCount(String path, char c) {
			int i;
			char[] buf = path.toCharArray();
			int count=0;
			for (i=0; i<buf.length; i++) {
				if (buf[i]==c) count++;
			}
			return count;
		}
		
		public static boolean isSeparator(String str)
        {
            if (str.equals(File.separator)/* || str.equals("\\")*/)
                return true;
            return false;
        }
		
		public static boolean isSeparator(char c)
        {
            if (c==File.separator.charAt(0)/* || c=='\\'*/)
                return true;
            return false;
        }
		
		private static boolean equals(String[] strs1, String[] strs2) {
			if (strs1.length!=strs2.length) return false;
			int i, j;
			boolean found;
			for (i=0; i<strs1.length; i++) {
				found=false;
				for (j=0; j<strs2.length; j++) {
					if (strs1[i].equals(strs2[i])) {
						found = true;
						break;
					}
				}
				if (!found) return false;
			}
			return true;
		}
		
		public static boolean equals(File file1, File file2) {
			String name1 = file1.getName();
			String name2 = file2.getName();
			if (name1.equals(name2)==false) return false;
			
			long len1 = file1.length();
			long len2 = file2.length();
			if (len1!=len2) return false;
			
			long time1 = file1.lastModified();
			long time2 = file2.lastModified();
			if (time1!=time2) return false;
					
			
			boolean isDir1 = file1.isDirectory();
			boolean isDir2 = file2.isDirectory();
			if (isDir1!=isDir2) return false;
			if (isDir1 && isDir2) {
				String[] fileList1 = file1.list();
				String[] fileList2 = file2.list();
				if (equals(fileList1, fileList2)==false) return false;
			}
			return true;
		}
		
		
		/** 파일 작업이 가능한지 테스트해본다.*/
		public static boolean testFileWork() {
			OutputStream os = null;
			InputStream is = null;
			byte[] buf = new byte[1000];
			
			try {			
				Context context = Control.view.getContext();
				AssetManager asset = context.getAssets();
				is = asset.open("lib/Test.txt");
				String srcFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Test.txt";
				File srcFile = new File(srcFilename);
				
				try {
					os = new FileOutputStream(srcFile);
					FileHelper.move(buf, is, os);
				}catch(Exception e) {
					return false;
				}
			}catch(Exception e) {
				return false;
			}
			finally {
				FileHelper.close(is);
				FileHelper.close(os);
			}
			return true;
		}
		
		public static ArrayList getFileList(String absFilename) {
			
			ArrayList result = new ArrayList(10);
			
			File file = new File(absFilename); 
			if (file.isDirectory()) {
				result.add(file); // 디렉토리
				
				String[] fileList = file.list();
				if (fileList!=null && fileList.length>0) {
					int i;				
					for (i=0; i<fileList.length; i++) {
						ArrayList r;
						if (isSeparator( absFilename.charAt(absFilename.length()-1) )==false)
							r = FileHelper.getFileList(absFilename+File.separator+fileList[i]);
						else 
							r = FileHelper.getFileList(absFilename+fileList[i]);
						int j;
						for (j=0; j<r.count; j++) {
							result.add(r.getItem(j));
						}
						if (result.count>=Integer.MAX_VALUE/100) {
							return result;
						}
					}
				}				
				
				return result;
			}
			else {
				result.add(file);
				return result;
			}
		}
		
		public static class SizeAndCount {
			public long size;
			public int count;
			public SizeAndCount() {
				
			}
			public SizeAndCount(long size, int count) {
				this.size = size;
				this.count = count;
			}
		}
		
		public static SizeAndCount getFileSizeAndCount(String absFilename) {
			SizeAndCount sizeAndCount = null;
			long size = 0;
			int count = 0;
			File file = new File(absFilename); 
			if (file.isDirectory()) {
				String[] fileList = file.list();
				if (fileList!=null && fileList.length>0) {
					int i;				
					for (i=0; i<fileList.length; i++) {
						SizeAndCount sc = null;
						if (isSeparator( absFilename.charAt(absFilename.length()-1) )==false)
							sc = FileHelper.getFileSizeAndCount(absFilename+File.separator+fileList[i]);
						else 
							sc = FileHelper.getFileSizeAndCount(absFilename+fileList[i]);
						if (sc!=null) {
							size += sc.size;
							count += sc.count;
						}
						//return r;
					}
				}
				
				size += 0; // 디렉토리
				count++;
				sizeAndCount = new SizeAndCount(size,count);
				return sizeAndCount;
			}
			else {
				size += file.length();
				count++;
				sizeAndCount = new SizeAndCount(size,count);
				return sizeAndCount;
			}
		}
		
		/** 디렉토리일 경우 아래에 있는 파일이나 디렉토리 모두 센다.*/
		public static long getFileSize(String absFilename) {
			/*if (firstCall) {
				directoryForPath.reset();
			}*/
			long r=0;
			File file = new File(absFilename); 
			if (file.isDirectory()) {
				/*boolean directoryFound = isDirectoryFound(absFilename);
				if (directoryFound) {
					return 0;
				}
				directoryForPath.add(absFilename);*/
				String[] fileList = file.list();
				if (fileList!=null && fileList.length>0) {
					int i;				
					for (i=0; i<fileList.length; i++) {
						if (isSeparator( absFilename.charAt(absFilename.length()-1) )==false)
							r += FileHelper.getFileSize(absFilename+File.separator+fileList[i]);
						else 
							r += FileHelper.getFileSize(absFilename+fileList[i]);
						//return r;
					}
				}
				
				r += 0; // 디렉토리
				return r;
			}
			else {
				r += file.length();
				return r;
			}
		}
		
		public static int getFileCountExceptDirectory(String absFilename) {
			
			int r=0;
			File file = new File(absFilename); 
			if (file.isDirectory()) {
				String[] fileList = file.list();
				if (fileList!=null && fileList.length>0) {
					int i;				
					for (i=0; i<fileList.length; i++) {
						if (isSeparator( absFilename.charAt(absFilename.length()-1) )==false)
							r += FileHelper.getFileCountExceptDirectory(absFilename+File.separator+fileList[i]);
						else 
							r += FileHelper.getFileCountExceptDirectory(absFilename+fileList[i]);
						//return r;
					}
				}
				
				//r += 1; // 디렉토리
				return r;
			}
			else {
				r += 1;
				return r;
			}
		}
		
		/** 디렉토리일 경우 아래에 있는 파일이나 디렉토리 모두 센다.*/
		public static int getFileCount(String absFilename) {
			/*if (firstCall) {
				directoryForPath.reset();
			}*/
			int r=0;
			File file = new File(absFilename); 
			if (file.isDirectory()) {
				/*boolean directoryFound = isDirectoryFound(absFilename);
				if (directoryFound) {
					return 0;
				}
				directoryForPath.add(absFilename);*/
				
				String[] fileList = file.list();
				if (fileList!=null && fileList.length>0) {
					int i;				
					for (i=0; i<fileList.length; i++) {
						if (isSeparator( absFilename.charAt(absFilename.length()-1) )==false)
							r += FileHelper.getFileCount(absFilename+File.separator+fileList[i]);
						else 
							r += FileHelper.getFileCount(absFilename+fileList[i]);
						//return r;
					}
				}
				
				r += 1; // 디렉토리
				return r;
			}
			else {
				r += 1;
				return r;
			}
		}
		
		/** 디렉토리일 경우 아래에 있는 파일이나 디렉토리 모두 지운다.*/
		public static boolean delete(String absFilename) {
			boolean r=false;
			File file = new File(absFilename); 
			if (file.isDirectory()) {
				String[] fileList = file.list();
				if (fileList!=null && fileList.length>0) {
					int i;				
					for (i=0; i<fileList.length; i++) {
						if (isSeparator( absFilename.charAt(absFilename.length()-1) )==false)
							r = FileHelper.delete(absFilename+File.separator+fileList[i]);
						else 
							r = FileHelper.delete(absFilename+fileList[i]);
						//if (!r) return false;
					}
				}
				
				r = file.delete(); // 빈 디렉토리
				if (!r) return false;
			}
			else {
				r = file.delete();
				if (!r) return false;
			}
			return true;
		}
		
		/** 윈도우즈에서만 가능하다.
		 * @return 콜론을 뺀 c 나 d 등*/
		public static String[] getPartitionSymbols() {
			ArrayListString r = new ArrayListString(10); 
			for (char c='a'; c<='z'; c++) {
				char[] arr = {c};
				String path = new String(arr);
				File file = new File(path+":"+File.separator);
				if (file.exists()) {
					String partition = file.getAbsolutePath();
					r.add(partition);
				}
			}
			return r.getItems();
		}
		
		/** 윈도우즈에서만 가능하다.
		 * @return 콜론을 뺀 c 나 d 등*/
		public static String getPartition(String absFilename) {
			int indexColon = absFilename.indexOf(':');
			String r = absFilename.substring(0, indexColon);
			return r;
		}
		
		public static boolean isRoot(String filename) {
			if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
				int indexColon = filename.indexOf(':');
				if (indexColon!=-1) {
					// "c:"나 "c:\"
					if (filename.length()==indexColon+1 || filename.length()==indexColon+2)
						return true;
				}
				else {
					// "\"
					if (filename.equals(File.separator)) return true;
				}
				return false;
			}
			else {
				if (filename.equals(File.separator)) return true;
				return false;
			}
		}
		
		/** 리턴값은 /을 포함한다. 
		 * 윈도우즈(자바)이면 루트이더라도 파티션이 있을수 있으므로 매개변수가 루트이면
		 * IO.LocalComputer을 리턴하고 리눅스(안드로이드)이면 루트를 리턴한다.*/
		public static String upDirectory(String filename) {
			int i;
			// 루트상태에서 up을 클릭한 경우
			if (isRoot(filename)) {
				if (Control.CurrentSystem.equals(Control.CurrentSystemIsJava)) {
					// 파티션들이 나와야하므로
					return IO.LocalComputer;
				}
				return File.separator;
			}
			for (i=filename.length()-2; i>=0; i--) {
				if (filename.charAt(i)==File.separator.charAt(0)/* || filename.charAt(i)=='\\'*/) {
					break;
				}
			}
			return filename.substring(0,i+1);
		}
		
		/** '.'을 포함하여 확장자를 리턴한다.*/
		public static String getExt(String filename)
        {
            int i;
            char[] name = new char[filename.length()];
            filename.getChars(0, name.length, name, 0);
            for (i = name.length - 1; i >= 0; i--)
            {
                if (name[i] == '.')
                {
                    break;
                }
            }
            if (i==-1) return "";
            String ext = filename.substring(i);
            return ext;
        }

		public static String getFilenameExceptExt(String filename)
        {
            int i;
            char[] name = new char[filename.length()];
            filename.getChars(0, name.length, name, 0);
            for (i = name.length - 1; i >= 0; i--)
            {
                if (name[i] == '.')
                {
                    break;
                }
            }
            if (i==-1) return filename;
            return filename.substring(0, i);
        }
		
		/** path 에서 디렉토리를 추출해서 리턴한다.*/
		public static String getDirectory(String path)
        {
            int i;
            char[] name = new char[path.length()];
            path.getChars(0, name.length, name, 0);
            for (i = name.length - 1; i >= 0; i--)
            {
                if (name[i] == File.separator.charAt(0)/* || name[i]=='\\'*/)
                {
                    break;
                }
            }
            if (i==-1) return "";
            return path.substring(0, i);
        }
		
		public static String getFilename(String path)
        {
            int i;
            char[] name = new char[path.length()];
            path.getChars(0, name.length, name, 0);
            for (i = name.length - 1; i >= 0; i--)
            {
                if (name[i] == File.separator.charAt(0)/* || name[i]=='\\'*/)
                {
                    break;
                }
            }
            if (i==-1) return path;
            return path.substring(i+1, name.length);
        }
		
		
		public static void close(InputStream stream) {
			try {
				if (stream!=null) stream.close();
			}
			catch(Exception e) {}			
		}
		public static void close(OutputStream stream) {
			try {
				if (stream!=null) stream.close();
			}
			catch(Exception e) {}			
		}
		public static void move(byte[] buf, InputStream input, OutputStream output) 
				throws IOException {			
			
			try {
				do {
					int readed;
					readed = input.read(buf, 0, buf.length);
					if (readed<0) {
							break;
					}
					output.write(buf,0,readed);
					
				}while(true);
			}catch(IOException e) {
				throw e;
			}
		}
		
		public static void move(byte[] buf, String srcPath, String destPath) 
				throws IOException {			
			
			try {
				FileInputStream inputStream = new FileInputStream(srcPath);
				FileOutputStream outputStream = new FileOutputStream(destPath);
				BufferedInputStream input = new BufferedInputStream(inputStream);
				BufferedOutputStream output = new BufferedOutputStream(outputStream);
				
				FileHelper.move(buf, input, output);
			}catch(IOException e) {
				throw e;
			}
		}
	}
	
	public static byte[] toBytes(short s, boolean IsLittleEndian) {
		byte[] r = new byte[2];
		if (IsLittleEndian) {
			r[1] = (byte) (s>>>8);
			r[0] = (byte) (s);
		}
		else {
			r[0] = (byte) (s>>>8);
			r[1] = (byte) (s);
		}
		return r;
	}
	
	/**2bytes의 버퍼를 char(UTF-16)으로 바꾼다.*/
	public static char toChar(byte[] buf, boolean IsLittleEndian) {
		/*byte temp = buf[0];
		buf[0] = buf[1];
		buf[1] = temp;
		int i = toInt(buf);
		return (char)i;*/
		short s = IO.toShort(buf, IsLittleEndian);
		char r = (char)s;
		return r;		
	}
	
	public static short toShort(byte[] buf, boolean IsLittleEndian) {
		short r=0;
		if (IsLittleEndian) {	
			short two = (short) (((short)buf[1])<<8 & 0xff00);
			short one = (short) (((short)buf[0])    & 0x00ff);
			r = (short) (two | one);
		}
		else {		
			// big endian
			short one = (short) (((short)buf[0])<<8 & 0xff00);
			short two = (short) (((short)buf[1])    & 0x00ff);
			r = (short) (one | two);
		}
		return r;
		
	}
	
	public static byte[] toBytes(long i, boolean IsLittleEndian) {
		byte[] r = new byte[8];
		if (IsLittleEndian) {
			r[7] = (byte) ((i>>>56) & 0xff);
			r[6] = (byte) ((i>>>48) & 0xff);
			r[5] = (byte) ((i>>>40) & 0xff);
			r[4] = (byte) ((i>>>32) & 0xff);
			r[3] = (byte) ((i>>>24) & 0xff);
			r[2] = (byte) ((i>>>16) & 0xff);
			r[1] = (byte) ((i>>>8) & 0xff);
			r[0] = (byte) (i & 0xff);
		}
		else {
			r[0] = (byte) (i>>>56);
			r[1] = (byte) (i>>>48);
			r[2] = (byte) (i>>>40);
			r[3] = (byte) (i>>>32);
			r[4] = (byte) (i>>>24);
			r[5] = (byte) (i>>>16);
			r[6] = (byte) (i>>>8);
			r[7] = (byte) (i);
		}
		return r;
	}
	
	public static long toLong(byte[] data, boolean IsLittleEndian) {
		long r=0;
		if (IsLittleEndian) {
			long eight = 	(long)((((long)data[7])<<56) & 0xff00000000000000L);
			long seven = 	(((long)data[6])<<48) & 0x00ff000000000000L;
			long six = 		(((long)data[5])<<40) & 0x0000ff0000000000L;
			long five = 	(((long)data[4])<<32) & 0x000000ff00000000L;
			long four = 	(((long)data[3])<<24) & 0x00000000ff000000L;
			long three = 	(((long)data[2])<<16) & 0x0000000000ff0000L;
			long two = 		(((long)data[1])<<8 ) & 0x000000000000ff00L;
			long one = 		(((long)data[0])    ) & 0x00000000000000ffL;
			/*r = (long) (eight & 0xff00000000000000l | seven & 0x00ff000000000000l | six & 0x0000ff0000000000l | five & 0x000000ff00000000l | 
					four & 0xff000000 | three & 0xff0000 | two & 0xff00 | one & 0xff);*/
			r = (long) (eight | seven | six | five | 
					four | three | two | one );
		}
		else {		
			// big endian
			long one = 		(long)(((long)data[0])<<56 & 0xff00000000000000L);
			long two = 		((long)data[1])<<48 & 0x00ff000000000000L;
			long three = 	((long)data[2])<<40 & 0x0000ff0000000000L;
			long four = 	((long)data[3])<<32 & 0x000000ff00000000L;
			long five = 	((long)data[4])<<24 & 0x00000000ff000000L;
			long six = 		((long)data[5])<<16 & 0x0000000000ff0000L;
			long seven = 	((long)data[6])<<8  & 0x000000000000ff00L;
			long eight = 	((long)data[7])     & 0x00000000000000ffL;
			r = (long) (one | two | three | four | 
					five | six | seven | eight);
		}
		return r;
	}
	
	
	public static byte[] toBytes(int i, boolean IsLittleEndian) {
		byte[] r = new byte[4];
		if (IsLittleEndian) {
			r[3] = (byte) (i>>>24);
			r[2] = (byte) (i>>>16);
			r[1] = (byte) (i>>>8);
			r[0] = (byte) (i);
		}
		else {
			r[0] = (byte) (i>>>24);
			r[1] = (byte) (i>>>16);
			r[2] = (byte) (i>>>8);
			r[3] = (byte) (i);
		}
		return r;
	}
	
	static int getHexaValue(char c) throws Exception {
		switch(c) {
		case '0': return 0;
		case '1': return 1;
		case '2': return 2;
		case '3': return 3;
		case '4': return 4;
		case '5': return 5;
		case '6': return 6;
		case '7': return 7;
		case '8': return 8;
		case '9': return 9;
		case 'a': return 10;
		case 'A': return 10;
		case 'b': return 11;
		case 'B': return 11;
		case 'c': return 12;
		case 'C': return 12;
		case 'd': return 13;
		case 'D': return 13;
		case 'e': return 14;
		case 'E': return 14;
		case 'f': return 15;
		case 'F': return 15;
		}
		throw new Exception("c is not hexa value.");
	}
	
	static String getHexa(byte value) {
		switch(value) {
		case 0: return "0";
		case 1: return "1";
		case 2: return "2";
		case 3: return "3";
		case 4: return "4";
		case 5: return "5";
		case 6: return "6";
		case 7: return "7";
		case 8: return "8";
		case 9: return "9";
		case 10: return "A";
		case 11: return "B";
		case 12: return "C";
		case 13: return "D";
		case 14: return "E";
		case 15: return "F";
		}
		return null;
	}
	
	/** value를 Hexa값을 갖는 스트링으로 리턴한다. 예를 들어 value가 10이면 0A를 리턴한다.*/
	public static String toHexa(byte value) {
		byte b1 = (byte) ((value & 0xf0) >> 4);
		byte b0 = (byte) (value & 0x0f);
		return ""+getHexa(b1)+getHexa(b0);
	}
	
	/** 0x로 시작되는 hexa 값의 정수 타입을 리턴한다.
	 * byte(7), short(2), char(2), int(3), long(4)
	 * 16 진수값에 부호가 있어서는 안된다.*/
	public static int isNumber2OfHexa(String hexaValue) {
		int len = hexaValue.length();
		long r=0;
		if (len>2) {
			char c0 = hexaValue.charAt(0);
			char c1 = hexaValue.charAt(1);
			if (c0=='0' && (c1=='x' || c1=='X')) { // 16진수
				int remainder = len - 2;
				// byte 의 경우 8bit이므로 16진수 2개의 자리수가 된다.
				// char, short 의 경우 16bit이므로 16진수 4개의 자리수가 된다.
				// int 의 경우 32bit이므로 16진수 8개의 자리수가 된다.
				if (remainder<=2) return 7; // byte
				else if (remainder<=4) return 2; // short
				else if (remainder<=8) return 3; // int
				else if (remainder<=16) return 4; // long

				return 0;
			}
			return 0;
		}
		return 0;
	}
	
	/** 스트링 정수값을 int 값으로 변환한다. 스트링 정수값에는 16진수를 포함한다.
	 * 16 진수값에 부호가 있어서는 안된다.*/
	public static int toInt(String value) throws Exception {
		int number = CompilerHelper.IsNumber2(new CodeString(value,0));
		if ((number==2 || number==3 || number==7)!=false) 
			throw new Exception("Value is not integer.");
		int len = value.length();
		int r=0;
		
		if (len>2) {
			char c0 = value.charAt(0);
			char c1 = value.charAt(1);
			if (c0=='0' && (c1=='x' || c1=='X')) { // 16진수
				int i;				
				for (i=len-1; i>=2; i--) {
					char c = value.charAt(i);
					int v = getHexaValue(c);
					int inc = v * 16 ^ (len-1-i);
					r += inc;
				}
				return r;
			}
			else { // 1234 등
				r = Integer.parseInt(value);
				return r;
			}
		}
		else { // 0, 1 등
			r = Integer.parseInt(value);
			return r;
		}	
	}//toInt()
	
	public static int toInt(byte[] data, boolean IsLittleEndian) {
		int r=0;
		if (data.length==4) {
			if (IsLittleEndian) {		
				int four =  (int)(((int)data[3])<<24 & 0xff000000);
				int three = (int)(((int)data[2])<<16 & 0x00ff0000);
				int two =   (int)(((int)data[1])<<8  & 0x0000ff00);
				int one =   (int)(((int)data[0])     & 0x000000ff);
				//r = (int) (four & 0xff000000 | three & 0xff0000 | two & 0xff00 | one & 0xff);
				r = (int) (four | three | two | one );
			}
			else {		
				// big endian
				int one =   (int)(((int)data[0])<<24 & 0xff000000);
				int two =   (int)(((int)data[1])<<16 & 0x00ff0000);
				int three = (int)(((int)data[2])<<8  & 0x0000ff00);
				int four =  (int)(((int)data[3])     & 0x000000ff);
				//r = (int) (one & 0xff000000 | two & 0xff0000 | three & 0xff00 | four & 0xff);
				r = (int) (one | two | three | four);
			}
		}
		else if (data.length==2) {
			if (IsLittleEndian) {
				int two = ((int)data[1])<<8 & 0xff00;
				int one = ((int)data[0])    & 0x00ff;
				r = (int) (two | one);
			}
			else {		
				// big endian
				int one = ((int)data[0])<<8 & 0xff00;
				int two = ((int)data[1])    & 0x00ff;
				r = (int) (one | two);
			}
		}
		return r;
	}
	
	/** 8바이트를 읽는다.*/
	public static long readLong(InputStream bis, boolean IsLittleEndian) {
		byte[] buf = new byte[8];
		try {
			bis.read(buf, 0, buf.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IO-readLong",e.toString());
		}
		long r = 0;
		r = toLong(buf, IsLittleEndian);
		//r = toInt(buf, IsLittleEndian);
		return r;
	}
	
	public static void writeLong(OutputStream bos, long value, boolean IsLittleEndian) {
		byte[] buf;
		buf = toBytes(value, IsLittleEndian);
		
		try {
			bos.write(buf, 0, buf.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IO-writeLong",e.toString());
		}
	}
	
	public static int readInt(InputStream bis, boolean IsLittleEndian) {
		byte[] buf = new byte[4];
		try {
			bis.read(buf, 0, 4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IO-readInt",e.toString());
		}
		int r = 0;
		
		if (IsLittleEndian) {		
			int four = ((int)buf[3])<<24;
			int three = ((int)buf[2])<<16;
			int two = ((int)buf[1])<<8;
			int one = ((int)buf[0]);
			r = (int) (four & 0xff000000 | three & 0xff0000 | two & 0xff00 | one & 0xff);
		}
		else {		
			// big endian
			int one = ((int)buf[0])<<24;
			int two = ((int)buf[1])<<16;
			int three = ((int)buf[2])<<8;
			int four = ((int)buf[3]);
			r = (int) (one & 0xff000000 | two & 0xff0000 | three & 0xff00 | four & 0xff);
		}
		return r;
	}
	
	public static void writeInt(OutputStream bos, int value, boolean IsLittleEndian) {
		byte[] buf = new byte[4];
		
		if (IsLittleEndian) {
			buf[3] = (byte) (value>>>24);
			buf[2] = (byte) (value>>>16);
			buf[1] = (byte) (value>>>8);
			buf[0] = (byte) (value);
		}
		else {
			buf[0] = (byte) (value>>>24);
			buf[1] = (byte) (value>>>16);
			buf[2] = (byte) (value>>>8);
			buf[3] = (byte) (value);
		}
		
		try {
			bos.write(buf, 0, 4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IO-writeInt",e.toString());
		}
	}
	
	public static short readShort(InputStream bis, boolean IsLittleEndian) {
		byte[] buf = new byte[2];
		try {
			bis.read(buf, 0, 2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IO-readInt",e.toString());
		}
		short r = 0;
		
		if (IsLittleEndian) {
			short two = (short)((buf[1])<<8);
			short one = (short)buf[0];
			r = (short) (two & 0xff00 | one & 0xff);
		}
		else {		
			// big endian
			short one = (short)(buf[0]<<8);
			short two = (short)buf[1];
			r = (short) (one & 0xff00 | two & 0xff);
		}
		return r;
	}
	
	public static void writeShort(OutputStream bos, short value, boolean IsLittleEndian) {
		byte[] buf = new byte[2];
		
		if (IsLittleEndian) {
			buf[1] = (byte) (value>>>8);
			buf[0] = (byte) (value);
		}
		else {
			buf[0] = (byte) (value>>>8);
			buf[1] = (byte) (value);
		}
		
		try {
			bos.write(buf, 0, 2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IO-writeInt",e.toString());
		}
	}
	
	public static byte readByte(InputStream bis) {
		byte[] buf = new byte[1];
		try {
			bis.read(buf, 0, buf.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IO-readByte",e.toString());
		}
		return buf[0];
	}
	
	
	public static boolean readBoolean(InputStream bis) {
		byte[] buf = new byte[1];
		try {
			bis.read(buf, 0, buf.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IO-readBoolean",e.toString());
		}
		if (buf[0]==0) return false;
		else return true;
	}
	
	public static void writeBoolean(OutputStream bos, boolean value) {
		byte[] buf = new byte[1];
				
		if (value==false) buf[0] = 0;
		else buf[0] = 1;		
		
		try {
			bos.write(buf, 0, buf.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IO-writeBoolean",e.toString());
		}
	}
	
	
	/**1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	2바이트 이상으로 표시된 문자의 경우, 
	첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 
	예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.*/
	public static char readCharUTF16(InputStream bis, TextFormat format, boolean IsLittleEndian) {
		int count=0;
		int codePoint = 0;
		byte[] buf = new byte[2];
		while (true) {
			try {
				count = bis.read(buf, 0, buf.length);					
				if (count<0) break;
				if (buf[0]==0 && buf[1]==0)  {
					int a;
					a=0;
					a++;
					break;
				}
				codePoint = IO.toInt(buf, IsLittleEndian);
				char[] arrChar = Character.toChars(codePoint);
				return arrChar[0];
			} catch (Exception e) {
				break;
			}
		}
		return 1;
			
		
					
	}
	
	
	
	public static void writeChar(OutputStream bos, char value, TextFormat format, boolean IsLittleEndian) {
		if (format==TextFormat.UTF_8) {
			writeCharUTF8(bos, value, IsLittleEndian);
		}
		else if (format==TextFormat.UTF_16) {
			writeCharUTF16(bos, value, IsLittleEndian);
		}
		else if (format==TextFormat.MS949_Korean) {
			//writeCharKSC(bos, value);
			writeCharMS949_korean(bos, value, /*IsLittleEndian*/false);
		}
	}

	
	static void writeCharUTF8(OutputStream bos, char value, boolean IsLittleEndian) {
			
		byte[] buf = com.gsoft.common.Charset.endcode(Codeset.UTF_8, value);
		try {
			bos.write(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** buffer에 유니코드 문자 c를 쓰고 offset을 전진시킨다.*/
	static void writeCharUTF8(BufferByte buffer, char c, boolean IsLittleEndian) {
		byte[] buf = com.gsoft.common.Charset.endcode(Codeset.UTF_8, c);
		Array.Copy(buf, 0, buffer.buffer, buffer.offset, buf.length);
		buffer.offset += buf.length;
	}
	
	static void writeCharUTF16(BufferByte buffer, char c, boolean IsLittleEndian) {
		// 지금 현재는 utf-16만
		char[] cBuf = {c};
		String str = new String(cBuf);
		int codePoint = str.codePointAt(0);
		short s = (short) (codePoint & 0x0000ffff);
		byte[] buf = IO.toBytes(s, IsLittleEndian);
		Array.Copy(buf, 0, buffer.buffer, buffer.offset, buf.length);
		buffer.offset += 2;
	}
	
	static void writeCharUTF16(OutputStream bos, char value, boolean IsLittleEndian) {
		char[] cBuf = {value};
		String str = new String(cBuf);
		int codePoint = str.codePointAt(0);
		short s = (short) (codePoint & 0x0000ffff);
		IO.writeShort(bos, s, IsLittleEndian);
	}
	
	static void writeCharKSC(OutputStream bos, char value) {
		if (0<value && value<=127) {
			byte[] buf = new byte[1];
			try {
				buf[0] = (byte)(value & 0xff);
				bos.write(buf, 0, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		else {
			int code = KSC5601.findKSC(value);
			byte[] buf = new byte[2];
			try {
				if (code!=0) {
					buf[0] = (byte)((code & 0xff00) >>> 8);
					buf[1] = (byte)((code & 0xff));
					bos.write(buf, 0, 2);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}
	
	/** readStringMS949_korean() 을 참조한다. IsLittleEndian 는 false 이어야 한다.*/
	static void writeCharMS949_korean(OutputStream bos, char value, boolean IsLittleEndian) {
		MS949.loadForWriting(Language.KOREAN);
		boolean oldEndian = IsLittleEndian;
		//IO.IsLittleEndian = false;
		
		try {
			if (0<=value && value<=127) {
				byte[] buf = new byte[1];
				try {
					buf[0] = (byte)(value & 0xff);
					bos.write(buf, 0, 1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}
			else {
				int code;
				MS949.Item item = (Item) MS949.tableOfUnicodeToMS949_korean.getData(value, true);
				if (item==null) {
					int a;
					a=0;
					a++;
				}
				code = item.ms949Code;
				short sCode = (short)code;
				IO.writeShort(bos, sCode, IsLittleEndian);
			}
		}finally {
			//IO.IsLittleEndian = oldEndian;
		}
	}
	
	
	/**-1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	-2바이트 이상으로 표시된 문자의 경우, 
	첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 
	예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	-첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.
	
	가는 -80 -95, 80은 2로 나누면 40-0, 20-00, 10-000, 5-0000, 2-10000, 01010000, 이것을 2의보수로 바꾸면 10110000 
	95는 2로 나누면 42-1, 21-01, 10-101, 5-0101, 2-10101, 01010101이 된다. 2의보수로 바꾸면 10101011이 된다.
	
	일본어의 아는 -86 -94*/
	/*public static void writeCharForJava(OutputStream bos, char value, TextFormat format) {
		if (format==TextFormat.UTF_8) {
			writeCharUTF8(bos, value);
		}
		else if (format==TextFormat.UTF_16) {
			
			writeCharUTF16(bos, value);
						
		}
		else if (format==TextFormat.KSC) {
			writeCharKSC(bos, value);
		}
	}*/
	
	
	
	
	

	
	
	/**영어:1바이트, 한글:3바이트 형식(널문자는 1바이트)으로 변환하여 
	 * ByteBuffer의 offset을 전진시키면서 그 버퍼에 쓴다.
	 * 1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	2바이트 이상으로 표시된 문자의 경우, 첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.<br>
	 buffer가 null이 아니면 안드로이드이든 자바이든 자바방식으로 통일하여 
			안드로이드와 자바가 통신이 가능하도록 한다.*/
	public static void writeString(BufferByte buffer, String str, TextFormat format, boolean IsLittleEndian) {
		// buffer가 null이 아닐 경우 스트링은 null(두바이트 0)로 끝나야 하므로 
		// buffer에 쓸때도 null로 끝내야 한다. readStringUTF16()을 참조한다.
		if (str==null) str = "\0";
		else str += "\0";
		char[] buf = new char[str.length()];
		str.getChars(0, str.length(), buf, 0);
		int i;
		
		// buffer가 null이 아니면 안드로이드이든 자바이든 자바방식으로 통일하여 
		// 안드로이드와 자바가 통신이 가능하도록 한다.
		for (i=0; i<buf.length; i++) {
			// \n 만 있는 경우는 \r\n 을 출력한다.
			if (buf[i]=='\n' && (i>0 && buf[i-1]!='\r')) {
				writeCharUTF8(buffer, '\r', IsLittleEndian);
				//writeCharForAndroid(buffer, '\r', format);
			}
			writeCharUTF8(buffer, buf[i], IsLittleEndian);
			//writeCharForAndroid(buffer, buf[i], format);
		}	
		
	}
	
	
	
	/**널문자(한글은 3바이트 나머지는 1바이트)포함 널문자를 만날 때까지의 스트링을 읽어 
	 * buffer의 offset을 전진시킨다.
	 * 1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	2바이트 이상으로 표시된 문자의 경우, 첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.<br>
	 buffer가 null이 아니면 안드로이드이든 자바이든 자바방식으로 통일하여 
			안드로이드와 자바가 통신이 가능하도록 한다.*/
	public static String readStringIncludingNull(BufferByte buffer, TextFormat format, boolean IsLittleEndian) throws Exception {
		
		
		// return readStringUTF16(null, buffer, IsLittleEndian);
		
		return IO.readStringUTF8(null, buffer);
	}
	
	
	/**-1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	-2바이트 이상으로 표시된 문자의 경우, 
	첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 
	예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	-첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.
	예를 들어, 
	가(44032, AC00) =-22, -80, -128 =
		첫바이트 : 	  1110(14)   1010(A)   
		두번째바이트 : 10(나머지바이트상위2비트) 11   00(C) 00    
		세번째바이트 : 10(나머지바이트상위2비트) 00(0)   0000(0)
		
	-22 : 22의 2의보수 11 0, 5 10, 2 110, 00010110  
		 따라서 1110(14) 1010(A)
	-80 : 80의 2의보수 40 0, 20 00, 10 000, 5 0000, 2 10000, 01010000, 
		 따라서 10 1100(C) 00
	-128 : 128의 2의보수 64 0, 32 00, 16 000, 8 0000, 4 00000, 2 000000 10000000 
		 따라서 10 00(0) 0000(0)
		 
	가 : char c = 0xac00;*/
	//private static String readStringUTF8ForAndroid(InputStream bis, BufferByte buffer) throws Exception {
	/*public static String readString2UTF8(InputStream bis, BufferByte buffer) throws Exception {
		
		byte[] testBuf = new String("위").getBytes();
		byte[] buf = new byte[1];
		byte[] bufHangul = new byte[3]; // 건강할건[-27, -127, -91] 건강할강[-27, -70, -73] 일본어[-29, -125, -101]
		int bufHanguleCount=0;
		byte[] bufLatin = new byte[2];
		
		int count=0;
		ArrayListChar r = new ArrayListChar(100);
		int mode = 1;
		while (true) {
			try {
				if (buffer==null) {
					count = bis.read(buf, 0, 1);
					if (count<0) {					
						break;
					}
					//if (buf[0]=='\\') buf[0] = '/';
				}
				else {
					// buffer.offset-2일 경우에는 문자를 얻어야 하고 그보다 크면 루프가 종료된다.
					if (buffer.offset>=buffer.buffer.length) {
						break;
					}
					buf[0] = buffer.buffer[buffer.offset];
					buffer.offset++;
					if (buffer.offset==26) {
						int a;
						a=0;
						a++;
					}
					
					if (buf[0]==0) break;
					
					//if (buf[0]=='\\') buf[0] = '/';
				}
								
				if (mode==1) {
					if (0<buf[0] && buf[0]<=127) {	// 1바이트문자, basic latin 0이상 127이하
						r.add((char)buf[0]);
					}
					else {
						byte msb = (byte)((buf[0] & 0xf0) >>> 4);
						if (12<=msb && msb<=13) {	// 2바이트 문자
							mode = 2;
							bufHanguleCount = 0;
						}
						else {
							if (msb==14) {	// 3바이트문자
								mode = 3;
								bufHanguleCount = 0;
							}
						}
						bufHangul[bufHanguleCount++] = buf[0];
					}
				}
				else {
					bufHangul[bufHanguleCount++] = buf[0];
					if (mode==3) {
						if (bufHanguleCount==3) {					
							//String str = new String(bufHangul, 0, 3); 
							char ch = com.gsoft.common.Charset.decode(Codeset.UTF_8, bufHangul);
							r.add(ch);
							mode = 1;
							bufHanguleCount = 0;
						}
					}
					else if (mode==2) {
						// LatinSupplement[-61, -87, 0] 
						
						// [-61, -87]
						// 61은 30 1, 15 01, 7 101, 3 1101, 00111101 따라서 -61은 11000011 (c3)
						// 87은 43 1, 21 11, 10 111, 5 0111, 2 10111, 01010111 따라서 10101001 (a9) 
						// 따라서 e9
						
						// [-61, -88]
						// 88은 01011000 따라서 10101000 
						// 따라서 e8
						
						// [-61, -79]
						// 61은 30 1, 15 01, 7 101, 3 1101, 00111101 따라서 -61은 11000011 (c3)
						// 79는 39 1, 19 11, 9 111, 4 1111, 01001111 따라서 -79는 10110001    
						// 따라서 f1
						
						
						
						
						// LatinExtendA 
						// [-17, -69, -65] 이후에 LatinExtendA 두 바이트 문자를 넣는다. 
						
						// 0x0100[-60, -128] 
						// 60은 00111100 따라서 -60은 1100(12) 0100
						// 128은 10000000 따라서 -128은 10000000
						
						// 0x0101[-60, -127]
						// 127은 01111111 따라서 -127은 10000001
						
						// 0x0202[-60, -126]
						
						// 0x017E [-59, -66]
						// 59는 00111011 따라서 -59는 1100(12) 0101
						// 66은 33 0, 16 10, 8 010, 4 0010, 2 00010, 01000010 따라서 -66은 10 111110
						// 첫바이트의 마지막 2비트 01과 두번째 바이트의 마지막 6비트 111110을 합치면 126이 되고,
						// 첫바이트의 마지막 3번째 비트 0x0100 + 126을 더하면 0x017E이 된다.
						
						bufLatin[0] = bufHangul[0]; 
						bufLatin[1] = bufHangul[1];
						char ch = com.gsoft.common.Charset.decode(Codeset.UTF_8, bufLatin);
						r.add(ch);
						mode = 1;
						bufHanguleCount = 0;
						
						char test = 0xe9;
						test = 0x0100; 
						test = 0xe8;
						test = 0xf1;
						test = 0xc8;
						test = 0xc7;
						test = 0xc9;
					}
					else {
						String str = new String(bufHangul, 0, 2);
						r.add(str.charAt(0));
						mode = 1;
						bufHanguleCount = 0;
					}
				}			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//mode = 1;
				//bufHanguleCount = 0;
				throw new Exception();
			}
		} // while
		
	
		String ret = new String(r.getItems());
		r.reset();
		return ret;
	}*/
	
	
	
	/** inputStream 에서 null을 만날때까지의 바이트 스트림을 읽는다.*/
	public static BufferByte readUntilNull(InputStream is) {
		byte[] buf = new byte[1];
		ArrayListByte r = new ArrayListByte(100);
		while (true) {
			try {
				int count = is.read(buf, 0, 1);
				if (count<0 || buf[0]==0) {					
					break;
				}
				r.add(buf[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new BufferByte(r.list);
		
	}
	
	
	
	/**-1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	-2바이트 이상으로 표시된 문자의 경우, 
	첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 
	예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	-첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.
	예를 들어, 
	가(44032, AC00) =-22, -80, -128 =
		첫바이트 : 	  1110(14)   1010(A)   
		두번째바이트 : 10(나머지바이트상위2비트) 11   00(C) 00    
		세번째바이트 : 10(나머지바이트상위2비트) 00(0)   0000(0)
		
	-22 : 22의 2의보수 11 0, 5 10, 2 110, 00010110  
		 따라서 1110(14) 1010(A)
	-80 : 80의 2의보수 40 0, 20 00, 10 000, 5 0000, 2 10000, 01010000, 
		 따라서 10 1100(C) 00
	-128 : 128의 2의보수 64 0, 32 00, 16 000, 8 0000, 4 00000, 2 000000 10000000 
		 따라서 10 00(0) 0000(0)
		 
	가 : char c = 0xac00;*/
public static String readStringUTF8(InputStream bis, BufferByte buffer) throws Exception {
		
		//byte[] testBuf = new String("위").getBytes();
		byte[] buf = new byte[1];
		byte[] bufHangul = new byte[3]; // 건강할건[-27, -127, -91] 건강할강[-27, -70, -73] 일본어[-29, -125, -101]
		int bufHanguleCount=0;
		byte[] bufLatin = new byte[2];
		
		int count=0;
		HighArrayForReading_char r = new HighArrayForReading_char(100);
		int mode = 1;
		while (true) {
			try {
				if (buffer==null) {
					count = bis.read(buf, 0, 1);
					if (count<0 || buf[0]==0) {					
						break;
					}
					//if (buf[0]=='\\') buf[0] = '/';
				}
				else {
					// buffer.offset-1일 경우에는 문자를 얻어야 하고 그보다 크면 루프가 종료된다.
					if (buffer.offset>=buffer.buffer.length/* || buf[0]==0*/) {
						break;
					}
					
					buf[0] = buffer.buffer[buffer.offset];
					buffer.offset++;
					
					if (buf[0]==0) break; // null 까지 읽는다.
					
					//if (buf[0]=='\\') buf[0] = '/';
				}
								
				if (mode==1) {
					if (0<buf[0] && buf[0]<=127) {	// 1바이트문자, basic latin 0이상 127이하
						r.add((char)buf[0]);
					}
					else {
						byte msb = (byte)((buf[0] & 0xf0) >>> 4);
						if (12<=msb && msb<=13) {	// 2바이트 문자
							mode = 2;
							bufHanguleCount = 0;
						}
						else {
							if (msb==14) {	// 3바이트문자
								mode = 3;
								bufHanguleCount = 0;
							}
						}
						bufHangul[bufHanguleCount++] = buf[0];
					}
				}
				else {
					bufHangul[bufHanguleCount++] = buf[0];
					if (mode==3) {
						if (bufHanguleCount==3) {					
							//String str = new String(bufHangul, 0, 3); 
							char ch = com.gsoft.common.Charset.decode(Codeset.UTF_8, bufHangul);
							r.add(ch);
							mode = 1;
							bufHanguleCount = 0;
						}
					}
					else if (mode==2) {
						// LatinSupplement[-61, -87, 0] 
						
						// [-61, -87]
						// 61은 30 1, 15 01, 7 101, 3 1101, 00111101 따라서 -61은 11000011 (c3)
						// 87은 43 1, 21 11, 10 111, 5 0111, 2 10111, 01010111 따라서 10101001 (a9) 
						// 따라서 e9
						
						// [-61, -88]
						// 88은 01011000 따라서 10101000 
						// 따라서 e8
						
						// [-61, -79]
						// 61은 30 1, 15 01, 7 101, 3 1101, 00111101 따라서 -61은 11000011 (c3)
						// 79는 39 1, 19 11, 9 111, 4 1111, 01001111 따라서 -79는 10110001    
						// 따라서 f1
						
						
						
						
						// LatinExtendA 
						// [-17, -69, -65] 이후에 LatinExtendA 두 바이트 문자를 넣는다. 
						
						// 0x0100[-60, -128] 
						// 60은 00111100 따라서 -60은 1100(12) 0100
						// 128은 10000000 따라서 -128은 10000000
						
						// 0x0101[-60, -127]
						// 127은 01111111 따라서 -127은 10000001
						
						// 0x0202[-60, -126]
						
						// 0x017E [-59, -66]
						// 59는 00111011 따라서 -59는 1100(12) 0101
						// 66은 33 0, 16 10, 8 010, 4 0010, 2 00010, 01000010 따라서 -66은 10 111110
						// 첫바이트의 마지막 2비트 01과 두번째 바이트의 마지막 6비트 111110을 합치면 126이 되고,
						// 첫바이트의 마지막 3번째 비트 0x0100 + 126을 더하면 0x017E이 된다.
						
						bufLatin[0] = bufHangul[0]; 
						bufLatin[1] = bufHangul[1];
						char ch = com.gsoft.common.Charset.decode(Codeset.UTF_8, bufLatin);
						r.add(ch);
						mode = 1;
						bufHanguleCount = 0;
						
						char test = 0xe9;
						test = 0x0100; 
						test = 0xe8;
						test = 0xf1;
						test = 0xc8;
						test = 0xc7;
						test = 0xc9;
					}//mode==2
					else {
						String str = new String(bufHangul, 0, 2);
						r.add(str.charAt(0));
						mode = 1;
						bufHanguleCount = 0;
					}
				}			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//mode = 1;
				//bufHanguleCount = 0;
				throw new Exception();
			}
		} // while
		
		
		String ret = new String(r.toArray());
		r.destroy();
		return ret;
	}
	
	/**-1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	-2바이트 이상으로 표시된 문자의 경우, 
	첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 
	예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	-첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.
	예를 들어, 
	가(44032, AC00) =-22, -80, -128 =
		첫바이트 : 	  1110(14)   1010(A)   
		두번째바이트 : 10(나머지바이트상위2비트) 11   00(C) 00    
		세번째바이트 : 10(나머지바이트상위2비트) 00(0)   0000(0)*/
	/*private static String readStringUTF8ForJava(InputStream bis, BufferByte buffer) throws Exception {
		// 가=-95(msb:10) -80(msb:11),  나=-86(msb:10) -77(msb:11)
		byte[] testBuf = {-80, -95};
		String strTest = new String(testBuf);
		
		byte[] buf = new byte[1];
		byte[] bufHangul = new byte[3];
		int bufHanguleCount=0;
		
		int count=0;
		ArrayListChar r = new ArrayListChar(100);
		int mode = 1;
		while (true) {
			try {
				if (buffer==null) {
					count = bis.read(buf, 0, 1);
					if (count<0 || buf[0]==0) {					
						break;
					}
					//if (buf[0]=='\\') buf[0] = '/';
				}
				else {
					buf[0] = buffer.buffer[buffer.offset];
					buffer.offset++;
					if (buffer.offset>=buffer.buffer.length || buf[0]==0) {
						break;
					}
					//if (buf[0]=='\\') buf[0] = '/';
				}
								
				if (mode==1) {
					if (0<buf[0] && buf[0]<=127) {	// 1바이트문자, basic latin 0이상 127이하
						r.add((char)buf[0]);
					}
					else {// 가=-95(msb:10) -80(msb:11),  나=-86(msb:10) -77(msb:11)
						byte msb = (byte)((buf[0] & 0xf0) >>> 4);
						if (msb==11) {	// 2바이트 문자
							mode = 2;
							bufHanguleCount = 0;
						}
						else {
							
						}
						bufHangul[bufHanguleCount++] = buf[0];
					}
				}
				else {
					bufHangul[bufHanguleCount++] = buf[0];
					
					if (mode==2) {
						String str = new String(bufHangul, 0, 2);
						r.add(str.charAt(0));
						mode = 1;
						bufHanguleCount = 0;
					}
					else {
						
					}
				}			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//mode = 1;
				//bufHanguleCount = 0;
				throw new Exception();
			}
		} // while
		
		
		return new String(r.getItems());
	}*/
		
	
	/** buffer가 null이 아닐 경우 스트링은 null(두바이트 0)로 끝나야 하므로 
	 * buffer에 쓸때도 null로 끝내야 한다.
	 * writeString(BufferByte buffer, String str, TextFormat format)을 참조한다.*/
	private static String readStringUTF16(InputStream bis, BufferByte buffer, boolean IsLittleEndian) throws Exception {
		
		int count=0;
		ArrayListChar r = new ArrayListChar(100);
		int mode = 1;
		int codePoint = 0;
		byte[] buf = new byte[2];
		while (true) {
			try {
				if (buffer==null) {
					count = bis.read(buf, 0, buf.length);					
					if (count<0) break;
					if (buf[0]==0 && buf[1]==0)  {
						int a;
						a=0;
						a++;
						break;
					}
					codePoint = IO.toInt(buf, IsLittleEndian);
					char[] arrChar = Character.toChars(codePoint);
					r.add(arrChar[0]);
				}
				else {
					// buffer.offset-2일 경우에는 문자를 얻어야 하고 그보다 크면 루프가 종료된다.
					if (buffer.offset>=buffer.buffer.length/* || (buf[0]==0 && buf[1]==0)*/) {
						break;
					}
					
					buf[0] = buffer.buffer[buffer.offset];
					buf[1] = buffer.buffer[buffer.offset+1];
					buffer.offset += 2;
					
					if (buf[0]==0 && buf[1]==0) break;
					
					
					
					//if (buf[0]=='\\') buf[0] = '/';
					codePoint = IO.toInt(buf, IsLittleEndian);
					char[] arrChar = Character.toChars(codePoint);
					r.add(arrChar[0]);
					
					
					
				}
			} catch (Exception e) {
				break;
			}
		}
		String ret = new String(r.getItems());
		r.reset();
		return ret;
	}
	
	/*public static String readStringKSC(InputStream bis) {
		Charset charset = Charset.forName("MS949");
		//byte[] buffer = new byte[1000];
		ByteBuffer buffer = ByteBuffer.allocate(100000);
		try {
			bis.read(buffer.array(), 0, buffer.capacity());
			//String r = new String(buffer, charset);
			CharBuffer charBuffer = charset.decode(buffer);
			//char[] arrChars = charBuffer.array();
			//String r = new String(arrChars);
			//return r;
			return charBuffer.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}*/
	
	
	public static String readStringKSC_UsingReader(InputStream bis) {
		BufferedReader in
		   = new BufferedReader(new InputStreamReader(bis));
		ArrayListChar r = new ArrayListChar(10000);
		r.resizeInc = 10000;
		char[] buf = new char[10000];
		while (true) {
			try {
				int count = in.read(buf);
				if (count<0) {
					break;
				}
				for (int i=0; i<count; i++) {
					r.add(buf[i]);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		char[] rStr = r.getItems();
		String ret = new String(rStr);
		r.reset();
		
		return ret;
	}
	
	
	
	/** 해시테이블을 기반으로 하여 MS949 코드 한글(12160자)과 영문을 읽어 유니코드 스트링으로 리턴한다.<br>
	 * MS949코드 0x8141은 한글로 갂인데 만약에 little endian이라면 하위바이트 0x41(65) 이것은 
		ascii코드와 충돌을 하므로 big endian이어야 한다. 그리고 ms949 한글의 상위바이트는 0x81 이상이므로
		ascii코드와 충돌을 하지 않는다.*/
	public static String readStringMS949_korean(InputStream bis, boolean IsLittleEndian) throws Exception
	{
		MS949.loadForReading(Language.KOREAN);
		
		//boolean oldEndian = IO.IsLittleEndian;
		// MS949코드 0x8141은 한글로 갂인데 만약에 little endian이라면 하위바이트 0x41(65) 이것은 
		// ascii코드와 충돌을 하므로 big endian이어야 한다. 그리고 한글의 상위바이트는 0x81 이상이므로
		// ascii코드와 충돌을 하지 않는다.
		//IO.IsLittleEndian = false;
		
		byte[] buf = new byte[1];
		byte[] bufHangul = new byte[2];
		int bufHanguleCount=0;
		int lsi;
		int msBits8;
		
		int count=0;
		//ArrayListChar r = new ArrayListChar(100);
		HighArrayForReading_char r = new HighArrayForReading_char(1000); 
		int mode = 1;
		
		try {		
			while (true) {
				count = bis.read(buf,0,1);
				if (count<0) {
					break;
				}
				
				if (r.data.count==8) {
					ArrayListChar list =  (ArrayListChar)r.data.list[7];
					if (list.count==240) {
						char c = (char) -127;
						int a;
						a=0;
						a++;
					}
				}
				
				if (mode==1) {
					if (0<=buf[0] && buf[0]<=127) {	// 1바이트문자, basic latin 0이상 127이하
						r.add((char)buf[0]);
					}
					else { // buf[0]가 0x81이면
						mode = 3;
						bufHanguleCount = 0;
						bufHangul[bufHanguleCount++] = buf[0];
					}						
				}
				else {
					bufHangul[bufHanguleCount++] = buf[0];
					if (mode==3) {
						if (bufHanguleCount==2) {
							short s_ms949Code = IO.toShort(bufHangul, false);
							char ms949Code = (char)s_ms949Code;
														
							MS949.Item item = 
								(Item) MS949.tableOfMS949ToUnicode_korean.getData(ms949Code, false);
							if (item==null) {
								int a;
								a=0;
								a++;
										
							}
							char unicode = (char) (item.unicode & 0xffff);
							r.add(unicode);
							
							mode = 1;
							bufHanguleCount = 0;
						}
					}
				}
			}//while
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {		
			//IO.IsLittleEndian = oldEndian;
		}
		
		char[] arr = r.toArray();
		String ret = new String(arr);
		r.destroy();
		
		return ret;
	}
	
	public static String readStringKSC(InputStream bis) throws Exception
	{
		byte[] buf = new byte[1];
		byte[] bufHangul = new byte[2];
		int bufHanguleCount=0;
		int lsi;
		int msBits8;
		
		int count=0;
		//ArrayListChar r = new ArrayListChar(100);
		HighArrayForReading_char r = new HighArrayForReading_char(1000); 
		int mode = 1;
		
				
		while (true) {
			try {
				count = bis.read(buf,0,1);
				if (count<0) {
					break;
				}
				
				if (r.data.count==8) {
					ArrayListChar list =  (ArrayListChar)r.data.list[7];
					if (list.count==240) {
						char c = (char) -127;
						int a;
						a=0;
						a++;
					}
				}
				
				if (mode==1) {
					if (0<=buf[0] && buf[0]<=127) {	// 1바이트문자, basic latin 0이상 127이하
						r.add((char)buf[0]);
					}
					else {
						mode = 3;
						bufHanguleCount = 0;
						bufHangul[bufHanguleCount++] = buf[0];
					}						
				}
				else {
					bufHangul[bufHanguleCount++] = buf[0];
					if (mode==3) {
						if (bufHanguleCount==2) {
							msBits8 = (int)(bufHangul[0] & 0xff);
							if (msBits8 ==0xA4) {
								lsi = KSC5601.findLSI_jamo(bufHangul[1]);
								if (lsi>=KSC5601.ja_mo_code.length) throw new Exception();
								
								r.add(KSC5601.ja_mo_code[lsi].utf8Code);
								mode = 1;
								bufHanguleCount = 0;
								
							}
							else {
								//msi = KSC5601.findMSI(bufHangul[0]);
								lsi = KSC5601.findLSI(bufHangul[0], bufHangul[1]);
								if (lsi>=KSC5601.wansung_code.length) throw new Exception();
								r.add(KSC5601.wansung_code[lsi].utf8Code);
								mode = 1;
								bufHanguleCount = 0;
							}
						}
					}
				}
			}catch(Exception e) {
				throw e;
			}
			/*catch(Exception e) {
				mode = 1;
				bufHanguleCount = 0;
			}*/
		}
		
		char[] arr = r.toArray();
		String ret = new String(arr);
		
		r.destroy();
		return ret;
	}
	
	/** Java 스트림을 사용하여 파일을 읽는다.*/
	public static String readString(InputStream bis, TextFormat format)  throws Exception {
		if (format==TextFormat.UTF_8) {
			return readStringUTF8(bis, null);
		}
		else if (format==TextFormat.UTF_16) {
			return readStringUTF16(bis, null, true);
		}
		else {
			return readStringMS949_korean(bis, false);
			//return readStringKSC(bis);
			//return readStringKSC_UsingReader(bis);
		}
	}
	
	/** TextFormat에 상관없이 파일을 읽는다.*/
	/*public static String readString(InputStream inputStream)  {
				
		try {
			return readStringUTF8(inputStream, null);
		}catch(Exception e) {
			try {
				return readStringMS949_korean(inputStream, false);
			}
			catch(Exception e1) {
				try {					
					return readStringUTF16(inputStream, null, true);
				} catch (Exception e2) {
				}
			}
		}
		return null;
	}*/
	
	public static class ReturnOfReadString {
		public String result;
		public TextFormat textFormat;
		ReturnOfReadString(String result, TextFormat textFormat) {
			this.result = result;
			this.textFormat = textFormat;
		}
	}
	
	/** TextFormat에 상관없이 파일을 읽는다.*/
	public static ReturnOfReadString readString(String filePath)  {		
		FileInputStream inputStream = null;
		BufferedInputStream bis = null;
		
		int i=0;
		for (i=0; i<3; i++) {				
			try {
				inputStream = new FileInputStream(filePath);
				bis = new BufferedInputStream(inputStream);
				if (i==0) {					
					String r = readStringUTF8(bis, null);
					return new ReturnOfReadString(r, TextFormat.UTF_8);
				}
				else if (i==1) {
					String r = readStringMS949_korean(bis, false);
					return new ReturnOfReadString(r, TextFormat.MS949_Korean);
				}
				else {
					String r = readStringUTF16(bis, null, true);
					return new ReturnOfReadString(r, TextFormat.UTF_16);
				}
			}catch(Exception e) {
				try {
					
				}
				catch(Exception e1) {
					try {					
						
					} catch (Exception e2) {
					}
				}
			}finally {
				if (bis!=null) {
					try {
						bis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (inputStream!=null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	/** Java NIO를 사용하여 파일을 읽는다.*/
	/*public static String readString_UsingJavaNIO(String path, TextFormat format)  throws Exception {		
		if (format==TextFormat.KSC) {			
			return readStringKSC_UsingJavaNIO(path);
			//return readStringKSC_UsingReader(bis);
		}
		else {
			return null;
		}
	}*/
	
	/** value의 UTF8코드로 인코딩된 바이트 길이를 리턴한다.
	 * Net의 sendFile()에서 호출*/
	public static int getByteLen(char value) {		
		if (value=='\0') return 1;
		byte[] arrCh = Charset.endcode(Codeset.UTF_8, value);
		return arrCh.length;
	}
	
	/** value의 UTF8코드로 인코딩된 바이트 길이를 리턴한다.
	 * Net의 sendFile()에서 호출*/
	public static int getByteLen(String value) {
		int r=0;
		value += "\0";
		char[] buf = new char[value.length()];
		value.getChars(0, value.length(), buf, 0);
		int i;
		for (i=0; i<buf.length; i++) {
			r += getByteLen(buf[i]);
		}
		return r;
	}
	
	public static void writeStringMS949_Korean(OutputStream os, String value, boolean addsNull, boolean IsLittleEndian) throws IOException {
		if (value==null) return;
		
		MS949.loadForWriting(Language.KOREAN);
		
		int i;
		byte[] buf = new byte[1];
		
		for (i=0; i<value.length(); i++) {
			char c = value.charAt(i);
			
			try {
				if (0<c && c<=127) {
					// '\n' 만 있는 경우 앞에 '\r'을 추가한다.
					if (c=='\n' && (i>0 && value.charAt(i-1)!='\r')) {
						try {
							buf[0] = (byte)('\r' & 0xff);
							os.write(buf, 0, 1);
						} catch (IOException e) {
							// TODO Auto-generated catch block
						}
					}
					
					try {
						buf[0] = (byte)(c & 0xff);
						os.write(buf, 0, 1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				}
				else {
					int code;
					MS949.Item item = (Item) MS949.tableOfUnicodeToMS949_korean.getData(c, true);
					code = item.ms949Code;
					short sCode = (short)code;
					IO.writeShort(os, sCode, IsLittleEndian);
				}
			}finally {
				//IO.IsLittleEndian = oldEndian;
			}
		
		}//for (i=0; i<value.length(); i++) {
		if (addsNull) {
			try {
				buf[0] = (byte)('\n' & 0xff);
				os.write(buf, 0, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		
	}
	
	public static void writeStringUTF8(OutputStream os, String value, boolean addsNull, boolean IsLittleEndian) throws IOException {
		if (value==null) return;
		
		HighArray_byte r = new HighArray_byte(300); 
		int i;
		for (i=0; i<value.length(); i++) {
			char c = value.charAt(i);
			// '\n' 만 있는 경우 앞에 '\r'을 추가한다.
			if (c=='\n' && (i>0 && value.charAt(i-1)!='\r')) {
				byte[] ret = com.gsoft.common.Charset.endcode(Codeset.UTF_8, '\r');
				r.add(ret);
			}
			byte[] buf = com.gsoft.common.Charset.endcode(Codeset.UTF_8, c);
			r.add(buf);
		}
		if (addsNull) {
			byte[] buf = com.gsoft.common.Charset.endcode(Codeset.UTF_8, '\0');
			r.add(buf);
		}
		for (i=0; i<r.data.count; i++) {
			ArrayListByte item = (ArrayListByte) r.data.list[i];
			os.write(item.list, 0, item.count);
			//startIndex += item.count;
		}
		
	}
	
	
	
	
	/**1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	2바이트 이상으로 표시된 문자의 경우, 첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.
	@param addsNull : 스트링 마지막에 널문자를 추가하려면 true, 아니면 false.<br> 
	.txt 파일에 들어가는 스트링은 하나이므로 널문자 추가가 필요없으나, <br>
	스트링이 여러개나 정수, boolean, 스트링등이 같이 섞어서 들어가는 파일에는 
	스트링 구분을 위해 널문자 추가가 필요하다.
	 **/
	public static void writeString(OutputStream os, String value, TextFormat format, boolean addsNull, boolean IsLittleEndian) {
		//value += "\0";
		if (value==null) return;
		
		/*if (format==TextFormat.UTF_8) {
			try {
				IO.writeStringUTF8(os, value, addsNull, IsLittleEndian);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (format==TextFormat.MS949_Korean) {
			try {
				IO.writeStringMS949_Korean(os, value, addsNull, IsLittleEndian);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {		
			char[] buf = new char[value.length()+1];		
			value.getChars(0, value.length(), buf, 0);
			if (addsNull) buf[buf.length-1] = '\0';
			int i;
			for (i=0; i<buf.length; i++) {
				if (buf[i]=='\0') {
					int a;
					a=0;
					a++;
				}
				// '\n' 만 있는 경우 앞에 '\r'을 추가한다.
				if (buf[i]=='\n' && (i>0 && buf[i-1]!='\r')) {
					writeChar(os, '\r', format, IsLittleEndian);
				}
				writeChar(os, buf[i], format, IsLittleEndian);
			}
		}*/
		
		char[] buf = null;
		if (addsNull) {
			buf = new char[value.length()+1];		
		}
		else {
			buf = new char[value.length()];
		}
		
		value.getChars(0, value.length(), buf, 0);
		if (addsNull) buf[buf.length-1] = '\0';
		int i;
		for (i=0; i<buf.length; i++) {
			if (buf[i]=='\0') {
				int a;
				a=0;
				a++;
			}
			// '\n' 만 있는 경우 앞에 '\r'을 추가한다.
			if (buf[i]=='\n' && (i>0 && buf[i-1]!='\r')) {
				writeChar(os, '\r', format, IsLittleEndian);
			}
			writeChar(os, buf[i], format, IsLittleEndian);
		}
	}
	
	
	/**1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	2바이트 이상으로 표시된 문자의 경우, 첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.
	@param addsNull : 스트링 마지막에 널문자를 추가하려면 true, 아니면 false.<br> 
	.txt 파일에 들어가는 스트링은 하나이므로 널문자 추가가 필요없으나, <br>
	스트링이 여러개나 정수, boolean, 스트링등이 같이 섞어서 들어가는 파일에는 
	스트링 구분을 위해 널문자 추가가 필요하다.
	 **/
	public static void writeString(String filename, String value, TextFormat format, boolean addsNull, boolean IsLittleEndian) {
		//value += "\0";
		if (value==null) return;
		
		/*if (format==TextFormat.UTF_8) {
			try {
				IO.writeStringUTF8(os, value, addsNull, IsLittleEndian);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (format==TextFormat.MS949_Korean) {
			try {
				IO.writeStringMS949_Korean(os, value, addsNull, IsLittleEndian);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {		
			char[] buf = new char[value.length()+1];		
			value.getChars(0, value.length(), buf, 0);
			if (addsNull) buf[buf.length-1] = '\0';
			int i;
			for (i=0; i<buf.length; i++) {
				if (buf[i]=='\0') {
					int a;
					a=0;
					a++;
				}
				// '\n' 만 있는 경우 앞에 '\r'을 추가한다.
				if (buf[i]=='\n' && (i>0 && buf[i-1]!='\r')) {
					writeChar(os, '\r', format, IsLittleEndian);
				}
				writeChar(os, buf[i], format, IsLittleEndian);
			}
		}*/
		
		FileOutputStream fos = null;
		BufferedOutputStream os = null;
		
		try {
			fos = new FileOutputStream(filename);
			os = new BufferedOutputStream(fos);
			
			char[] buf = null;
			if (addsNull) {
				buf = new char[value.length()+1];		
			}
			else {
				buf = new char[value.length()];
			}
			value.getChars(0, value.length(), buf, 0);
			if (addsNull) buf[buf.length-1] = '\0';
			int i;
			for (i=0; i<buf.length; i++) {
				if (buf[i]=='\0') {
					int a;
					a=0;
					a++;
				}
				// '\n' 만 있는 경우 앞에 '\r'을 추가한다.
				if (buf[i]=='\n' && (i>0 && buf[i-1]!='\r')) {
					writeChar(os, '\r', format, IsLittleEndian);
				}
				writeChar(os, buf[i], format, IsLittleEndian);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (os!=null) {
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	
	
	public static float readFloat(InputStream bis) {
		float r = readInt(bis, true);
		return r;
	}
	
	public static void writeFloat(OutputStream bos, float value) {
		int v = (int) value;
		writeInt(bos, v, true);
	}
	
	/** 8바이트를 읽는다.*/
	public static double readDouble(InputStream bis) {
		double r = readLong(bis, true);
		return r;
	}
	
	public static void writeDouble(OutputStream bos, double value) {
		long v = (long) value;
		writeLong(bos, v, true);
	}
	
}