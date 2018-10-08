package com.gsoft.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import com.gsoft.common.Util.Array;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListChar;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.BufferByte;
import com.gsoft.common.gui.Control;
import com.gsoft.common.Encoding.KSC5601;
import com.gsoft.common.Encoding.EncodingFormatException;

import android.util.Log;

public class IO {
	//public static String Separator = "/";
	//public static char CSeparator = '/';
	
	/** 디폴트로 true이므로 little endian이 기본이다. 만약에 big endian을 쓰고 싶다면 
	 * 빅엔디안으로 바꾸기전에 IsLittleEndian의 값을 백업을 해놓고 빅엔디안으로 read/write를 한후에 
	 * 다시 백업한 값으로 복원을 해야한다. 왜냐하면 다른 곳에서도 파일입출력을 하기 위해서이다.
	 */
	public static boolean IsLittleEndian = true;
	//public static boolean IsUTF16 = false;
	
	public enum TextFormat {
		UTF_8,
		UTF_16,
		KSC
	}
	public static TextFormat textFormat = TextFormat.UTF_8;
	
	
	
	public static int DefaultBufferSize = 10000;
	
		
	public static class FileHelper {
		static ArrayListString directoryForPath = new ArrayListString(100);
		static String[] mDirsToAvoid = {File.separator+"proc", File.separator+"proc"+File.separator,
			File.separator+"sys", File.separator+"sys"+File.separator};
		
		/*public static void reset() {
			directoryForPath.reset();
		}*/
		
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
		
		public static boolean isDirectoryFound(String absFilename) {
			int i;
			for (i=0; i<mDirsToAvoid.length; i++) {
				if (absFilename.equals(mDirsToAvoid[i])) {
					return true;
				}
			}
			if (Control.findEqualDir) {
				for (i=0; i<directoryForPath.count; i++) {
					try {
						File paramFile = new File(absFilename);
						File file = new File(directoryForPath.getItem(i));
						if (equals(paramFile, file)) return true;
					}catch(Exception e) {
						
					}
				}
			}
			return false;
		}
		
		public static ArrayList getFileList(String absFilename, boolean firstCall) {
			if (firstCall) {
				directoryForPath.reset();
			}
			
			ArrayList result = new ArrayList(10);
			
			File file = new File(absFilename); 
			if (file.isDirectory()) {
				boolean directoryFound = isDirectoryFound(absFilename);
				if (directoryFound) {
					return new ArrayList(0);
				}
				directoryForPath.add(absFilename);
				result.add(file); // 디렉토리
				
				String[] fileList = file.list();
				if (fileList!=null && fileList.length>0) {
					int i;				
					for (i=0; i<fileList.length; i++) {
						ArrayList r;
						if (isSeparator( absFilename.charAt(absFilename.length()-1) )==false)
							r = FileHelper.getFileList(absFilename+File.separator+fileList[i], false);
						else 
							r = FileHelper.getFileList(absFilename+fileList[i], false);
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
		
		public static SizeAndCount getFileSizeAndCount(String absFilename, boolean firstCall) {
			if (firstCall) {
				directoryForPath.reset();
			}
			SizeAndCount sizeAndCount = null;
			long size = 0;
			int count = 0;
			File file = new File(absFilename); 
			if (file.isDirectory()) {
				boolean directoryFound = isDirectoryFound(absFilename);
				if (directoryFound) {
					return new SizeAndCount(0,0);
				}
				directoryForPath.add(absFilename);
				String[] fileList = file.list();
				if (fileList!=null && fileList.length>0) {
					int i;				
					for (i=0; i<fileList.length; i++) {
						SizeAndCount sc = null;
						if (isSeparator( absFilename.charAt(absFilename.length()-1) )==false)
							sc = FileHelper.getFileSizeAndCount(absFilename+File.separator+fileList[i], false);
						else 
							sc = FileHelper.getFileSizeAndCount(absFilename+fileList[i], false);
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
		
		public static long getFileSize(String absFilename, boolean firstCall) {
			if (firstCall) {
				directoryForPath.reset();
			}
			long r=0;
			File file = new File(absFilename); 
			if (file.isDirectory()) {
				boolean directoryFound = isDirectoryFound(absFilename);
				if (directoryFound) {
					return 0;
				}
				directoryForPath.add(absFilename);
				String[] fileList = file.list();
				if (fileList!=null && fileList.length>0) {
					int i;				
					for (i=0; i<fileList.length; i++) {
						if (isSeparator( absFilename.charAt(absFilename.length()-1) )==false)
							r += FileHelper.getFileSize(absFilename+File.separator+fileList[i], false);
						else 
							r += FileHelper.getFileSize(absFilename+fileList[i], false);
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
		
		public static int getFileCount(String absFilename, boolean firstCall) {
			if (firstCall) {
				directoryForPath.reset();
			}
			int r=0;
			File file = new File(absFilename); 
			if (file.isDirectory()) {
				boolean directoryFound = isDirectoryFound(absFilename);
				if (directoryFound) {
					return 0;
				}
				directoryForPath.add(absFilename);
				
				String[] fileList = file.list();
				if (fileList!=null && fileList.length>0) {
					int i;				
					for (i=0; i<fileList.length; i++) {
						if (isSeparator( absFilename.charAt(absFilename.length()-1) )==false)
							r += FileHelper.getFileCount(absFilename+File.separator+fileList[i], false);
						else 
							r += FileHelper.getFileCount(absFilename+fileList[i], false);
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
						if (!r) return false;
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
		
		/** 리턴값은 /을 포함한다.*/
		public static String upDirectory(String filename) {
			int i;
			if (filename.equals(File.separator)) return File.separator;
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
		public static void move(InputStream input, OutputStream output) 
				throws IOException {			
			byte[] buf = new byte[1000];
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
	}
	
	public static byte[] toBytes(short s) {
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
	public static char toChar(byte[] buf) {
		byte temp = buf[0];
		buf[0] = buf[1];
		buf[1] = temp;
		int i = toInt(buf);
		return (char)i;
		
	}
	
	public static short toShort(byte[] buf) {
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
	
	public static byte[] toBytes(long i) {
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
	
	public static long toLong(byte[] data) {
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
	
	
	public static byte[] toBytes(int i) {
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
	
	public static int toInt(byte[] data) {
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
	
	
	public static long readLong(InputStream bis) {
		byte[] buf = new byte[8];
		try {
			bis.read(buf, 0, 8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IO-readLong",e.toString());
		}
		long r = 0;
		r = toLong(buf);
		return r;
	}
	
	public static void writeLong(OutputStream bos, long value) {
		byte[] buf;
		buf = toBytes(value);
		
		try {
			bos.write(buf, 0, buf.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IO-writeLong",e.toString());
		}
	}
	
	public static int readInt(InputStream bis) {
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
	
	public static void writeInt(OutputStream bos, int value) {
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
	
	public static short readShort(InputStream bis) {
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
	
	public static void writeShort(OutputStream bos, short value) {
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
	2바이트 이상으로 표시된 문자의 경우, 첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.*/
	public static char readChar(InputStream bis, TextFormat format) {
		byte[] buf = new byte[1];
		byte[] bufHangul = new byte[3];
		int bufHanguleCount=0;
		
		int count=0;
		//ArrayListChar r = new ArrayListChar(100);  
		//boolean isHangulMode=false;
		int mode = 1;
		while (true) {
			try {
				count = bis.read(buf, 0, 1);
				if (count<0 || buf[0]==0) {					
					break;
				}
								
				if (mode==1) {
					if (0<buf[0] && buf[0]<=127) {	// 1바이트문자, basic latin 0이상 127이하
						return (char)buf[0];
					}
					else {
						byte msb = (byte)((buf[0] & 0xf0) >>> 4);
						if (12<=msb && msb<=13) {	// 2바이트 문자
							mode = 2;
							//bufHanguleCount = 0;
						}
						else {
							if (msb==14) {	// 3바이트문자
								mode = 3;
								//bufHanguleCount = 0;
							}
						}
						bufHangul[bufHanguleCount++] = buf[0];
					}
				}
				else {
					bufHangul[bufHanguleCount++] = buf[0];
					if (mode==3) {
						if (bufHanguleCount==3) {					
							String str = new String(bufHangul, 0, 3);
							return str.charAt(0);
							//mode = 1;
							//bufHanguleCount = 0;
						}
					}
					else {
						String str = new String(bufHangul, 0, 2);
						return str.charAt(0);
						//mode = 1;
						//bufHanguleCount = 0;
					}
				}			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				mode = 1;
				bufHanguleCount = 0;
			}
		}
		return 0;
	}

	/**-1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	-2바이트 이상으로 표시된 문자의 경우, 
	첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 
	예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	-첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.*/
	public static void writeChar(OutputStream bos, char value, TextFormat format) {
		if (format==TextFormat.UTF_8) {
			char[] cBuf = {value};
			byte[] buf = (new String(cBuf)).getBytes();
			//byte[] paddingBuf = {1};
			try {
				if (buf.length==1) {// 영어, 기타
					bos.write(buf);
				}
				else if (buf.length==2) {
					bos.write(buf, 0, 2);
					//bos.write(paddingBuf, 0, 1);
				}
				else if (buf.length==3) {	// 한글
					bos.write(buf, 0, 3);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		else if (format==TextFormat.KSC) {
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
	}
	
	/**-1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	-2바이트 이상으로 표시된 문자의 경우, 
	첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 
	예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	-첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.*/
	public static void writeChar(BufferByte buffer, char c, TextFormat format) {
		char[] cBuf = {c};
		byte[] buf = (new String(cBuf)).getBytes();
		//byte[] paddingBuf = {1};
		if (buf.length==1) {	// 영어, 기타
			Array.Copy(buf, 0, buffer.buffer, buffer.offset, buf.length);
			buffer.offset += buf.length;
		}
		else if (buf.length==2) {
			Array.Copy(buf, 0, buffer.buffer, buffer.offset, buf.length);
			//Array.Copy(paddingBuf, 0, buffer.buffer, buffer.offset, 1);
			buffer.offset += 2;
		}
		else if (buf.length==3) { // 한글
			Array.Copy(buf, 0, buffer.buffer, buffer.offset, buf.length);
			buffer.offset += buf.length;
		}
	}
	
	/**영어:1바이트, 한글:3바이트 형식(널문자는 1바이트)으로 변환하여 
	 * ByteBuffer의 offset을 전진시키면서 그 버퍼에 쓴다.
	 * 1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	2바이트 이상으로 표시된 문자의 경우, 첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.*/
	public static void writeString(BufferByte buffer, String str, TextFormat format) {
		str += "\0";
		char[] buf = new char[str.length()];
		str.getChars(0, str.length(), buf, 0);
		int i;
		for (i=0; i<buf.length; i++) {
			if (buf[i]=='\n' && (i>0 && buf[i-1]!='\r')) {
				writeChar(buffer, '\r', format);
			}
			writeChar(buffer, buf[i], format);
		}
		
	}
	
	
	
	/**널문자(한글은 3바이트 나머지는 1바이트)포함 널문자를 만날 때까지의 스트링을 읽어 
	 * buffer의 offset을 전진시킨다.
	 * 1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	2바이트 이상으로 표시된 문자의 경우, 첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.*/
	public static String readString(BufferByte buffer, TextFormat format) throws EncodingFormatException {
		if (format==TextFormat.UTF_8) {
			return readStringUTF8(null, buffer);
		}
		else {
			return readStringUTF16(null, buffer);
		}
		
	}
	
	private static String readStringUTF16(InputStream bis, BufferByte buffer) {
		
		byte[] buf = new byte[2];
		
		int count=0;
		ArrayListChar r = new ArrayListChar(100);
		//int mode = 1;
		while (true) {
			try {
				if (buffer==null) {
					count = bis.read(buf, 0, 2);
					char ch = IO.toChar(buf);
					if (ch=='\\') ch = '/';
					r.add(ch);
					if (count<0) {					
						break;
					}
				}
				else {
					buf[0] = buffer.buffer[buffer.offset];
					buffer.offset++;
					buf[1] = buffer.buffer[buffer.offset];
					buffer.offset++;
					char ch = IO.toChar(buf);
					if (ch=='\\') ch = '/';
					r.add(ch);
					if (buffer.offset>=buffer.buffer.length) {
						break;
					}
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		} // while
		return new String(r.getItems());
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
	private static String readStringUTF8(InputStream bis, BufferByte buffer) throws EncodingFormatException {
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
					if (buf[0]=='\\') buf[0] = '/';
				}
				else {
					buf[0] = buffer.buffer[buffer.offset];
					buffer.offset++;
					if (buffer.offset>=buffer.buffer.length || buf[0]==0) {
						break;
					}
					if (buf[0]=='\\') buf[0] = '/';
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
							String str = new String(bufHangul, 0, 3);
							r.add(str.charAt(0));
							mode = 1;
							bufHanguleCount = 0;
						}
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
				throw new EncodingFormatException();
			}
		} // while
		
		/*try {
			if (bis.available()!=0) {
				if (r.count==0) throw new EncodingFormatException();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}*/
		return new String(r.getItems());
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
	
	public static String readStringKSC(InputStream bis) throws EncodingFormatException
	{
		byte[] buf = new byte[1];
		byte[] bufHangul = new byte[2];
		int bufHanguleCount=0;
		int lsi;
		int msBits8;
		
		int count=0;
		ArrayListChar r = new ArrayListChar(100);
		int mode = 1;			
		
		while (true) {
			try {
				count = bis.read(buf,0,1);
				if (count<0) {
					break;
				}
				
				if (mode==1) {
					if (0<buf[0] && buf[0]<=127) {	// 1바이트문자, basic latin 0이상 127이하
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
								if (lsi>=KSC5601.ja_mo_code.length) throw new EncodingFormatException();
								
								r.add(KSC5601.ja_mo_code[lsi].utf8Code);
								mode = 1;
								bufHanguleCount = 0;
								
							}
							else {
								//msi = KSC5601.findMSI(bufHangul[0]);
								lsi = KSC5601.findLSI(bufHangul[0], bufHangul[1]);
								if (lsi>=KSC5601.wansung_code.length) throw new EncodingFormatException();
								r.add(KSC5601.wansung_code[lsi].utf8Code);
								mode = 1;
								bufHanguleCount = 0;
							}
						}
					}
				}
			}catch(EncodingFormatException e) {
				throw e;
			}
			catch(Exception e) {
				mode = 1;
				bufHanguleCount = 0;
			}
		}
		return new String(r.getItems());
	}
	
	public static String readString(InputStream bis, TextFormat format)  throws EncodingFormatException {
		if (format==TextFormat.UTF_8) {
			return readStringUTF8(bis, null);
		}
		else if (format==TextFormat.UTF_16) {
			return readStringUTF16(bis, null);
		}
		else {
			return readStringKSC(bis);
		}
	}
	
	public static int getByteLen(char value) {
		char[] cBuf = {value};
		byte[] buf = (new String(cBuf)).getBytes();
		if (buf.length==1) {// 영어, 기타
			return 1;
		}
		else if (buf.length==2) {
			return 3;
		}
		else if (buf.length==3) {	// 한글
			return 3;
		}
		return 0;
	}
	
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
	
	/**1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	2바이트 이상으로 표시된 문자의 경우, 첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.*/
	public static void writeString(OutputStream os, String value, TextFormat format) {
		value += "\0";
		char[] buf = new char[value.length()];
		value.getChars(0, value.length(), buf, 0);
		int i;
		for (i=0; i<buf.length; i++) {
			// '\n'앞에 '\r'을 추가한다.
			if (buf[i]=='\n' && (i>0 && buf[i-1]!='\r')) {
				writeChar(os, '\r', format);
			}
			writeChar(os, buf[i], format);
		}
	}
	
	public static float readFloat(InputStream bis) {
		float r = readInt(bis);
		return r;
	}
	
	public static void writeFloat(OutputStream bos, float value) {
		int v = (int) value;
		writeInt(bos, v);
	}
	
	public static double readDouble(InputStream bis) {
		double r = readLong(bis);
		return r;
	}
	
	public static void writeDouble(OutputStream bos, double value) {
		long v = (long) value;
		writeLong(bos, v);
	}
	
}
