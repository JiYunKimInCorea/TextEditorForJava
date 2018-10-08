package com.gsoft.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;

import com.gsoft.DataTransfer.pipe.Pipe;
import com.gsoft.common.Code.CodeChar;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Code.CodeStringType;
import com.gsoft.common.Compiler_types.AccessModifier;
import com.gsoft.common.Compiler_types.Error;
import com.gsoft.common.Compiler_types.FindArrayInitializerParams;
import com.gsoft.common.Compiler_types.FindClassParams;
import com.gsoft.common.Compiler_types.FindControlBlockParams;
import com.gsoft.common.Compiler_types.FindSpecialBlockParams;
import com.gsoft.common.Compiler_types.FindFuncCallParam;
import com.gsoft.common.Compiler_types.FindFunctionParams;
import com.gsoft.common.Compiler_types.FindIndependentFuncCallParams;
import com.gsoft.common.Compiler_types.FindStatementParams;
import com.gsoft.common.Compiler_types.FindVarParams;
import com.gsoft.common.Compiler_types.FindVarUseParams;
import com.gsoft.common.Compiler_types.IndexForHighArray;
import com.gsoft.common.Compiler_types.Template;
import com.gsoft.common.Compiler_types.TypeCast;
import com.gsoft.common.Compiler_types.AccessModifier.AccessPermission;
import com.gsoft.common.Compiler_gui.TextView;
import com.gsoft.common.Encoding.EncodingFormatException;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.IO.TextFormat;
import com.gsoft.common.PostFixConverter.CodeStringEx;
import com.gsoft.common.Util.Array;
import com.gsoft.common.Util.ArrayListCodeString;
import com.gsoft.common.Util.ArrayListIReset;
import com.gsoft.common.Util.ArrayListInt;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.Hashtable_FullClassName;
import com.gsoft.common.Compiler_types.HighArray_CodeString;
import com.gsoft.common.Util.Stack;
import com.gsoft.common.Util.StackTracer;
import com.gsoft.common.gui.Control;

public class CompilerHelper {
	
	public static String curPackageName;
	public static String[] glistOfFilesInPackage;
	
	
	/** blank나 comment(comment는 찾지 않음)를 skip.
     *  reverse가 false이면 startIndex()부터 endIndex()까지 인덱스를 증가시키면서 검색, 못 찾으면 endIndex()+1
	 *  reverse가 true이면 endIndex()부터 startIndex()까지 인덱스를 감소시키면서 검색, 못 찾으면 startIndex()-1*/
	public static int SkipBlank(CodeString src, boolean reverse, int startIndex, int endIndex)
    {
        int i;
        int resultIndex = -1;
        if (!reverse)
        {
            for (i = startIndex; i <= endIndex; i++)
            {
            	CodeChar c = src.charAt(i);
            	if (CompilerHelper.IsComment(c)) continue;
                if (CompilerHelper.IsBlank(c.c)) continue;
                break;
            }
            resultIndex = i;
        }
        else
        {
            for (i = endIndex; i >= startIndex; i--)
            {
            	CodeChar c = src.charAt(i);
            	if (CompilerHelper.IsComment(c)) continue;
                if (CompilerHelper.IsBlank(c.c)) continue;
                break;
            }
            /*if (i >= startIndex) resultIndex = i;
            else resultIndex = startIndex();*/
            resultIndex = i;
        }
        return resultIndex;
    }
	
	static public void showMessage(boolean replaceOrAdd, String message) {
		try{
			CommonGUI.loggingForMessageBox.setText(replaceOrAdd, message, false);
			CommonGUI.loggingForMessageBox.setHides(false);
			Control.view.postInvalidate();
		}catch(Exception e) {
			
		}
		
	}
	
	
	
	/**  Compiler.projectAlreadyLoaded이 true이면 true를 리턴 그렇지 않으면 false를 리턴한다.
	 * 프로그램 시작시에는 false이므로 무조건 false를 리턴한다.
	 * decompressAndroidAndProjectSrc_sub()에서 압축을 풀면 Compiler.projectAlreadyLoaded은 true가 된다.
	 * */
	public synchronized static boolean projectSrcAlreadyExists() {
		if (Compiler.projectAlreadyLoaded) return true;
		else return false;
	}
	
	/**  Compiler.gsoftAlreadyLoaded이 true이면 true를 리턴 그렇지 않으면 false를 리턴한다. 
	 * 프로그램 시작시에는 false이므로 무조건 false를 리턴한다. 
	 * decompressAndroidAndProjectSrc_sub()에서 압축을 풀면 Compiler.gsoftAlreadyLoaded은 true가 된다.
	 * */
	public synchronized static boolean gsoftAlreadyExists() {
		if (Compiler.gsoftAlreadyLoaded) return true;
		else return false;
	}
	
	/**  /mnt/sdcard/janeSoft/gsoft/android이 존재하면 true를 리턴 그렇지 않으면 false를 리턴한다.*/
	public synchronized static boolean androidJavaLibAlreadyExists() {
		
		// /sdcard/gsoft/android
		//String destFilename =  Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "gsoft" + File.separator + "android";
		String destFilename = Control.pathAndroid_Final + File.separator + "android";
		File file = new File(destFilename);
		if (file.exists()) return true;
		else return false;
	}
	
	
	static class ThreadDecompressAndroidAndProjectSrc extends Thread {
		public void run() {
			
			decompressAndroidAndProjectSrc_sub();          				            				        				
			
		}
	}
	
	public static void decompressAndroidAndProjectSrc_sub() {
		boolean success = true;
		
			//  /sdcard/gsoft 의 파일들이 올바르지 않으면 /sdcard/gsoft과 /sdcard/gsoft.zip을 모두 지우고
			// 새로 압축을 푼다.
			//boolean r = IO.FileHelper.testFileWork();
		//if (androidJavaLibAlreadyExists()==false) {
		
		
		// 압축이 실패할 수도 있으므로 디폴트로 바꿔놓는다.
		Control.pathAndroid = Control.pathAndroid_Final;
		CommonGUI_SettingsDialog.settings.pathAndroid = Control.pathAndroid_Final;
			
			// /sdcard/janeSoft/gsoft
			String destFilename =  Control.pathAndroid_Final;
			showMessage(true, "Deleting " + destFilename + ".. Please don't close and wait..");
			FileHelper.delete(destFilename);
			// /sdcard/gsoft.zip
			destFilename =  Control.pathJaneSoft + File.separator + "gsoft.zip";
			FileHelper.delete(destFilename);
			success = decompressAndroidJavaLib();
			
			if (success==false) {
				//Compiler.errors.add(new Error(compiler, 0,0,"Can't load android library."));
			}
			else {
				Compiler.androidJavaLibAlreadyLoaded = true;
				showMessage(false, "Decompress completed.");
				CommonGUI.loggingForMessageBox.setHides(true);
			}
		//}
		
		// 프로그램시작시에는 /sdcard/janeSoft/project/com을 지우고 다시 설치한다.
		//if (projectSrcAlreadyExists()==false) {
			
			// /sdcard/janeSoft/project/com
			destFilename =  Control.pathProjectSrc + File.separator + "com";
			showMessage(true, "Deleting " + destFilename + ".. Please don't close and wait..");
			FileHelper.delete(destFilename);
			// /sdcard/janeSoft/project.zip
			destFilename =  Control.pathJaneSoft + File.separator + "project.zip";
			FileHelper.delete(destFilename);
			success = decompressProjectSrc();
			
			if (success==false) {
				//Compiler.errors.add(new Error(compiler, 0,0,"Can't load project files."));
			}
			else {
				Compiler.projectAlreadyLoaded = true;
				showMessage(false, "Decompress completed.");
				CommonGUI.loggingForMessageBox.setHides(true);
			}
		//}			
	
	}
	
	/** 스레드를 사용하여 압축을 해제할지를 결정하여 압축을 해제한다.
	 * slave 로 동작할 때는 압축을 해제하지 않는다.*/
	public static void decompressAndroidAndProjectSrc() {
		if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
			if (Control.isMasterOrSlave) {
				ThreadDecompressAndroidAndProjectSrc thread = new ThreadDecompressAndroidAndProjectSrc();
				thread.start();
			}
		}
		else {
			ThreadDecompressAndroidAndProjectSrc thread = new ThreadDecompressAndroidAndProjectSrc();
			thread.start();
		}
		
	}
	
	
	/** 윈도우즈의 경우 asset\lib\project.zip 파일을 c:\TextEditorForJava\janeSoft\project.zip 에 옮겨서
	 * c:\TextEditorForJava\janeSoft\project 에 압축을 해제하고 c:\TextEditorForJava\janeSoft\project.zip 은 지운다.
	 * @return 압축해제가 성공하면 true 를 리턴한다.
	 */
	public static boolean decompressProjectSrc() {
		OutputStream os = null;
		InputStream is = null;
		
		byte[] buf = new byte[1000];
		
		String srcFilename = null;
		try{
			
			
			Context context = Control.view.getContext();
			AssetManager asset = context.getAssets();
			
			// asset에 있는 "gsoft.zip" 파일을 sdcard(전체경로:/sdcard/janeSoft/project.zip)에 옮긴다.
			is = asset.open("lib"+File.separator+"project.zip");
			
			srcFilename = Control.pathJaneSoft + File.separator + "project.zip";
			File srcFile = new File(srcFilename);
			
			try {
				os = new FileOutputStream(srcFile);
				FileHelper.move(buf, is, os);
				}catch(Exception e) {
					return false;
				}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return false;
		}
		finally {
			FileHelper.close(is);
			FileHelper.close(os);
		}
		
		try {
			
			String destFilename =  Control.pathJaneSoft + File.separator + "project";
			
			// sdcard에 옮겨진 jar파일을 압축을 푼다.
			showMessage(true, "Decompressing project.zip.. Please don't close and wait..");
			boolean r = com.gsoft.common.Util.JarFile.decompress(srcFilename, destFilename);
			// /sdcard/janeSoft/project.zip 파일을 지운다.
			FileHelper.delete(srcFilename);
			
			if (r) {
				Control.pathProjectSrc = destFilename;
				//Control.settingsDialog.editTextDirectory.setText(0, 
				//		new CodeString(Control.pathAndroid+File.separator,Color.BLACK));
				return true;
			}
			else {
				showMessage(true, "Failed decompressing project.zip....");
				Thread.sleep(4000);
				return false;
			}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return false;
		}
		finally {
			FileHelper.close(is);
			FileHelper.close(os);
		}
	}
	
	/**asset에 있는 gsoft.zip을 /sdcard/janeSoft/gsoft.zip에 옮겨서 
	 * /sdcard/janeSoft/gsoft에 압축을 해제한다.
	 * 윈도우즈의 경우 asset\lib\gsoft.zip 파일을 c:\TextEditorForJava\janeSoft\gsoft.zip 에 옮겨서
	 * c:\TextEditorForJava\janeSoft\gsoft 에 압축을 해제하고 c:\TextEditorForJava\janeSoft\gsoft.zip 은 지운다.
	 * 압축이 성공적으로 풀리면 CommonGUI_SettingsDialog.settings.pathAndroid가 
	 * c:\TextEditorForJava\janeSoft\gsoft 으로 바뀌게 된다.
	 * */
	public static boolean decompressAndroidJavaLib() {
		OutputStream os = null;
		InputStream is = null;
		
		byte[] buf = new byte[1000];
		
		String srcFilename = null;
		try{
			Context context = Control.view.getContext();
			AssetManager asset = context.getAssets();
			
			// asset에 있는 "AndroidJavaLib.zip" 파일을 sdcard(전체경로:/sdcard/gsoft.zip)에 옮긴다.
			is = asset.open("lib"+File.separator+"gsoft.zip");
			
			srcFilename = Control.pathJaneSoft + File.separator + "gsoft.zip";
			File srcFile = new File(srcFilename);
			
			try {
				os = new FileOutputStream(srcFile);
				FileHelper.move(buf, is, os);
				}catch(Exception e) {
					return false;
				}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return false;
		}
		finally {
			FileHelper.close(is);
			FileHelper.close(os);
		}
		
		try {
			
			String destFilename =  Control.pathAndroid_Final;
					
			
			// sdcard에 옮겨진 jar파일을 압축을 푼다.
			showMessage(true, "Decompressing AndroidJavaLib.zip.. Please don't close and wait..");
			boolean r = com.gsoft.common.Util.JarFile.decompress(srcFilename, destFilename);
			// 옮겨진 /sdcard/AndroidJavaLib.zip 파일은 지운다.
			FileHelper.delete(srcFilename);
			
			if (r) {
				Control.pathAndroid = destFilename;
				if (CommonGUI_SettingsDialog.settingsDialog!=null) {
					CommonGUI_SettingsDialog.settingsDialog.editTextDirectory.setText(0, 				
						new CodeString(Control.pathAndroid+File.separator,Color.BLACK));
				}
				CommonGUI_SettingsDialog.settings.pathAndroid = destFilename;
				return true;
			}
			else {
				showMessage(true, "Failed decompressing AndroidJavaLib.zip....");
				Thread.sleep(4000);
				return false;
			}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return false;
		}
		finally {
			FileHelper.close(is);
			FileHelper.close(os);
		}
	}
	
	
	/**asset에 있는 gsoft.zip을 /sdcard/janeSoft/gsoft.zip에 옮겨서 /sdcard/janeSoft/gsoft에 압축을 해제한다.
	 * 윈도우즈의 경우 asset\lib\gsoft.zip 파일을 c:\TextEditorForJava\janeSoft\gsoft.zip 에 옮겨서
	 * c:\TextEditorForJava\janeSoft\gsoft 에 압축을 해제하고 c:\TextEditorForJava\janeSoft\gsoft.zip 은 지운다.
	 * @return 압축해제가 성공하면 true 를 리턴한다.
	 */
	public static boolean decompressGSoft() {
		OutputStream os = null;
		InputStream is = null;
		
		byte[] buf = new byte[1000];
		
		String srcFilename = null;
		try{
			Context context = Control.view.getContext();
			AssetManager asset = context.getAssets();
			
			// asset에 있는 "gsoft.zip" 파일을 sdcard(전체경로:/sdcard/gsoft.zip)에 옮긴다.
			is = asset.open("lib"+File.separator+"gsoft.zip");
			
			srcFilename = Control.pathJaneSoft + File.separator + "gsoft.zip";
			File srcFile = new File(srcFilename);
			
			try {
				os = new FileOutputStream(srcFile);
				FileHelper.move(buf, is, os);
				}catch(Exception e) {
					return false;
				}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return false;
		}
		finally {
			FileHelper.close(is);
			FileHelper.close(os);
		}
		
		try {
			
			// /sdcard/janeSoft/gsoft 에 압축을 해제한다.
			String destFilename1 =  Control.pathJaneSoft + File.separator + "gsoft";
			
			//String destFilename2 =  destFilename1 + File.separator + "com" + File.separator + "gsoft";
			
						
			
			// sdcard 에 옮겨진 zip 파일을 압축을 푼다.
			showMessage(true, "Decompressing gsoft.zip.. Please don't close and wait..");
			boolean r = com.gsoft.common.Util.JarFile.decompress(srcFilename, destFilename1);
			// /sdcard/gsoft.zip 파일을 지운다.
			FileHelper.delete(srcFilename);
			
			if (r) {
				Control.pathAndroid = destFilename1;
				if (CommonGUI_SettingsDialog.settingsDialog!=null) {
					CommonGUI_SettingsDialog.settingsDialog.editTextDirectory.setText(0, 
						new CodeString(Control.pathAndroid+File.separator,Color.BLACK));
				}
				return true;
			}
			else {
				showMessage(true, "Failed decompressing gsoft.zip....");
				Thread.sleep(4000);
				return false;
			}
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return false;
		}
		finally {
			FileHelper.close(is);
			FileHelper.close(os);
		}
	}
	
	
	public static CodeString findComment(CodeString input, int textColor) throws Exception
    {
        int i, j;
        char[] result = new char[input.length()];
        int[] resultColors = new int[input.length()];
        byte[] resultTypes = new byte[input.length()]; 
        int count = 0;
        
        for (i = 0; i < input.length(); )
        {
        	// "/*"
            if (input.charAt(i).c == '/' && 
            		(i + 1 < input.length() && input.charAt(i + 1).c == '*'))
            {	
            	int startOfComment = i;
            	int endOfComment = input.length()-1;
            	boolean isDocuComment = false;            	
            	                	
                for (j = i+2; j < input.length(); j++)
                {
                	if (input.charAt(j).c == '*' && 
                			(j + 1 < input.length() && input.charAt(j + 1).c == '/'))
                    {
                		endOfComment = j + 1;
                		break;
                    }
                }
                
                // "/**" 다큐먼트 주석,  /**/는 아님
                if (j<input.length() && i+2<input.length() && input.charAt(i+2).c=='*' && 
                		i+3<input.length() && input.charAt(i+3).c!='/') { 
                	isDocuComment = true;
                }
                
                int k;
                if (isDocuComment==false) {
                    for (k=startOfComment; k<=endOfComment; k++) {
                    	result[count] = input.charAt(k).c;
                        resultColors[count] = Compiler.commentColor;
                        resultTypes[count++] = CodeStringType.Comment;
                    }
                }
                else {
                	int a;
                	a=0;
                	for (k=startOfComment; k<=endOfComment; k++) {
                    	result[count] = input.charAt(k).c;
                        resultColors[count] = Compiler.docuCommentColor;
                        resultTypes[count++] = CodeStringType.DocuComment;
                    }
                }
                i = endOfComment + 1;
                
               
            }
        	// "//"
            else if (input.charAt(i).c == '/' && 
            		(i + 1 < input.length() && input.charAt(i + 1).c == '/'))
            {
            	for (j = i; j < input.length(); j++)
                {
                	result[count] = input.charAt(j).c;
                    resultColors[count] = Compiler.commentColor;
                    resultTypes[count++] = CodeStringType.Comment;
                    i = j+1;
                    
                    if (input.charAt(j).c == '\r' && 
                    		(j + 1 < input.length() && input.charAt(j + 1).c == '\n')) {
                    	// 마지막 '\n'넣기
                    	result[count] = input.charAt(j+1).c;
                        resultColors[count] = Compiler.commentColor;
                        resultTypes[count++] = CodeStringType.Comment;
                        i = j+2;
                        //listOfComments.add(new Point(startOfComment, endOfComment));
                    	break;
                    }
                    else if (input.charAt(j).c == '\n') { // 이미 넣었음
                    	//listOfComments.add(new Point(startOfComment, endOfComment));
                    	break;
                    }
                    
                }
            }
                        
            else	// 주석이 아닌 경우
            {
            	result[count] = input.charAt(i).c;
                resultColors[count] = textColor;
                resultTypes[count++] = CodeStringType.Text;
                i++;
            }
        }
        result = Array.Resize(result, count);
        resultColors = Array.Resize(resultColors, count);
        resultTypes = Array.Resize(resultTypes, count);
        return new CodeString(result, resultColors, resultTypes);
    }
	
	
	
	public static ArrayListCodeString findComment(ArrayListCodeString src, int textColor) throws Exception
    {
        int i, j;
        ArrayListCodeString r = new ArrayListCodeString(src.count); 
        int count = 0;
        //listOfComments.resizeInc = 100;
        //listOfComments.reset();
        
        for (i = 0; i < src.count; )
        {
        	// "/*"
            if (CompilerHelper.IsConstant(src.getItem(i))==false && src.getItem(i).equals("/") && 
            		(i + 1 < src.count && CompilerHelper.IsConstant(src.getItem(i+1))==false && src.getItem(i + 1).equals("*")))
            {	
            	int startOfComment = i;
            	int endOfComment = src.count-1;
            	boolean isDocuComment = false;            	
            	                	
                for (j = i+2; j < src.count; j++)
                {
                	if (CompilerHelper.IsConstant(src.getItem(j))==false && src.getItem(j).equals("*") && 
                			(j + 1 < src.count && CompilerHelper.IsConstant(src.getItem(j+1))==false && src.getItem(j + 1).equals("/")))
                    {
                		endOfComment = j + 1;
                		break;
                    }
                }
                
                // "/**" 다큐먼트 주석,  /**/는 아님
                if (j<src.count && i+2<src.count && CompilerHelper.IsConstant(src.getItem(i+2))==false && src.getItem(i+2).equals("*") && 
                		i+3<src.count && CompilerHelper.IsConstant(src.getItem(i+3))==false && src.getItem(i+3).equals("/")==false) { 
                	isDocuComment = true;
                }
                
                int k;
                if (isDocuComment==false) {
                    for (k=startOfComment; k<=endOfComment; k++) {
                    	/*result[count] = src.getItem(k).c;
                        resultColors[count] = Compiler.commentColor;
                        resultTypes[count++] = CodeStringType.Comment;*/
                    	CodeString str = src.getItem(k);
                    	str.setType(CodeStringType.Comment);
                    	str.setColor(Compiler.commentColor);
                    	r.add(str);
                    }
                }
                else {
                	int a;
                	a=0;
                	for (k=startOfComment; k<=endOfComment; k++) {
                    	/*result[count] = src.getItem(k).c;
                        resultColors[count] = Compiler.docuCommentColor;
                        resultTypes[count++] = CodeStringType.DocuComment;*/
                		CodeString str = src.getItem(k);
                    	str.setType(CodeStringType.DocuComment);
                    	str.setColor(Compiler.docuCommentColor);
                    	r.add(str);
                    }
                }
                i = endOfComment + 1;
                
               
            }
        	// "//"
            else if (CompilerHelper.IsConstant(src.getItem(i))==false && src.getItem(i).equals("/") && 
            		(i + 1 < src.count && CompilerHelper.IsConstant(src.getItem(i+1))==false && src.getItem(i + 1).equals("/")))
            {
            	for (j = i; j < src.count; j++)
                {
                	/*result[count] = src.getItem(j).c;
                    resultColors[count] = Compiler.commentColor;
                    resultTypes[count++] = CodeStringType.Comment;*/
            		if (src.getItem(j).equals("파티션들중")) {
            			int a;
            			a=0;
            			a++;
            		}
            		CodeString str = src.getItem(j);
                	str.setType(CodeStringType.Comment);
                	str.setColor(Compiler.commentColor);
                	r.add(str);
                    i = j+1;
                    
                    if (src.getItem(j).equals("\r") && 
                    		(j + 1 < src.count && src.getItem(j + 1).equals("\n"))) {
                    	// 마지막 '\n'넣기
                    	/*result[count] = src.getItem(j+1).c;
                        resultColors[count] = Compiler.commentColor;
                        resultTypes[count++] = CodeStringType.Comment;*/
                    	CodeString str2 = src.getItem(j+1);
                    	str2.setType(CodeStringType.Comment);
                    	str.setColor(Compiler.commentColor);
                    	r.add(str2);
                        i = j+2;
                        //listOfComments.add(new Point(startOfComment, endOfComment));
                    	break;
                    }
                    else if (src.getItem(j).equals("\n")) { // 이미 넣었음
                    	//listOfComments.add(new Point(startOfComment, endOfComment));
                    	break;
                    }
                    
                }
            }
                        
            else	// 주석이 아닌 경우
            {
            	if (CompilerHelper.IsConstant(src.getItem(i))) {
            		/*result[count] = src.getItem(i).c;
	                resultColors[count] = Compiler.keywordColor;
	                resultTypes[count++] = CodeStringType.Constant;*/
            		CodeString str = src.getItem(i);
                	str.setType(CodeStringType.Constant);
                	str.setColor(Compiler.keywordColor);
                	r.add(str);
	                i++;
            	}
            	else {
	            	/*result[count] = src.getItem(i).c;
	                resultColors[count] = textColor;
	                resultTypes[count++] = CodeStringType.Text;*/
            		CodeString str = src.getItem(i);
                	str.setType(CodeStringType.Text);
                	str.setColor(Compiler.textColor);
                	r.add(str);
	                i++;
            	}
            }
        }
        /*result = Array.Resize(result, count);
        resultColors = Array.Resize(resultColors, count);
        resultTypes = Array.Resize(resultTypes, count);
        return new CodeString(result, resultColors, resultTypes);*/
        return r;
    }
	
	/** 디렉토리에서 parentClassNameFull의 childClassNameShort라는 이름의 
	 * 내부(inner) 클래스가 존재하는지 확인한다.
	 * @param parentClassNameFull : java.lang.String, com.gsoft.common.gui.Control 과 같은 이름
	 * @param childClassNameShort : String, Control과 같은 짧은 이름
	 * @param usesCache : glistOfFilesInPackage에 저장된 파일리스트가 packageName에 있는 파일리스트와 동일한 것이라면
	 * usesCache이 true일때 새로 리스트를 얻는게 아니라 glistOfFilesInPackage이 캐시로서 사용되고 
	 * usesCache이 false이면 glistOfFilesInPackage을 새로 만든다.
	 * */
	public static boolean hasInnerClass(String parentClassNameFull, String childClassNameShort, 
			boolean usesCache) {
		
		
		String[] listPackage = null;
		//packageName : com.gsoft.common.gui 와 같은 이름, parentClassNameFull에서 패키지의 이름이어야 한다.
		// 즉 com.gsoft.common.Compiler.CodeString에서 packageName은 com.gsoft.common이다. 
		String packageName = getPackageName(parentClassNameFull);
		if (packageName==null) return false;
		
		if (usesCache && glistOfFilesInPackage!=null && packageName.equals(curPackageName)) {
			listPackage = glistOfFilesInPackage;
		}
		else {
			String pathPackage =  packageName.replace(".", File.separator);
			String path = Control.pathAndroid + File.separator + pathPackage;
			File dirPackage = new File(path);
			listPackage = dirPackage.list();
			curPackageName = packageName;
			glistOfFilesInPackage = listPackage;
		}
		
		int i;
		/*for (i=0; i<packageName.length(); i++) {
			if (packageName.charAt(i)!=parentClassNameFull.charAt(i)) break;
		}*/
		
		// 패키지이름을 제외한 parentClassName
		// parent가 com.gsoft.common.gui.Buttons.Button, child가 ButtonState일 경우
		// parentClassNameExceptPackageName는 Buttons.Button-> Buttons$Button
		// childClassNameWithDollar는 Buttons$Button$ButtonState.class 가 된다.
		String parentClassNameExceptPackageName = 
			parentClassNameFull.substring(packageName.length()+1, parentClassNameFull.length());
		parentClassNameExceptPackageName = parentClassNameExceptPackageName.replace(".", "$");
		
		// 디렉토리안에 있는 파일 이름
		String childClassNameWithDollar = parentClassNameExceptPackageName + "$" +  childClassNameShort + 
				".class";
		
		for (i=0; i<listPackage.length; i++) {
			if (listPackage[i].equals(childClassNameWithDollar)) return true;
		}
		
		return false;
	}
	
	
	static FindClassParams loadClassFromSrc(String fullName) {
		FileInputStream stream=null;
		BufferedInputStream bis=null;
		TextFormat format = TextFormat.MS949_Korean;
		com.gsoft.common.Compiler_types.Language lang = com.gsoft.common.Compiler_types.Language.Java;
		
		
		try {
			if (CompilerHelper.exists(Compiler.mlistOfAllClassesHashed,fullName)==false) {
				CommonGUI.loggingForMessageBox.setText(true, "loadClassFromSrc()-"+fullName, false);
				CommonGUI.loggingForMessageBox.setHides(false);
				Control.view.postInvalidate();
				
				Compiler compiler = new Compiler();
				String fullNameSlash = fullName.replace('.', File.separatorChar);
				String path = Control.pathProjectSrc + File.separator + fullNameSlash;
				
				String srcPath = CompilerHelper.getSourceFilePath(path);
				if (srcPath==null) {
					int a;
					a=0;
					a++;
					CompilerHelper.printMessage(CommonGUI.textViewLogBird, "can't find its source file : " + fullName);
				}
				srcPath += ".java";
				
				stream = new FileInputStream(srcPath);
				int bufferSize = (int) (FileHelper.getFileSize(srcPath)*IO.DefaultBufferSizeParam);
				bis = new BufferedInputStream(stream, bufferSize);
				
				String input = IO.readString(bis, format);
				//String input = IO.readString_UsingJavaNIO(srcPath, format);
				
				compiler.start2(input, lang, Compiler.backColor, srcPath);
				
				int i;
				for (i=0; i<compiler.mlistOfAllDefinedClasses.count; i++) {
					FindClassParams c = (FindClassParams) compiler.mlistOfAllDefinedClasses.getItem(i);
					if (c.name.equals(fullName)) return c;
				}
				FindClassParams r = (FindClassParams) compiler.mlistOfClass.list[0];
				return r;
			}
			else {
				FindClassParams r = CompilerHelper.getFindClassParams(Compiler.mlistOfAllClassesHashed, fullName);
				return r;
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			Log.e("Load Error", e1.toString());
		}
		catch (EncodingFormatException e1) {
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.e("Load Error", e1.toString());
		}
		catch (OutOfMemoryError e1) {
			Log.e("Load Error", e1.toString());
		}
		finally {
			FileHelper.close(bis);
			FileHelper.close(stream);
		}
		return null;
	}
	
	/** fullName을 가지는 클래스가 캐시에 등록되어 있는지를 먼저 확인하고 없으면
	 * 자바(.java) 소스 파일을 읽어서 클래스와 그것의 필드와 메소드들의 타입을 정하고 클래스 캐시에 등록하고 상속도 하여
	 * 그 클래스의 FindClassParams을 리턴한다.
	 * @param fullClassNameIncludingTemplateExceptArray : 클래스가 템플릿 클래스일 경우 템플릿 타입 이름을 포함하고 배열은 포함하지 않은 클래스 풀 이름이다.
	 * 
	 * @return
	 */
	static FindClassParams loadClassFromSrc_onlyInterface(String fullNameIncludingTemplateExceptArray) {
		FileInputStream stream=null;
		BufferedInputStream bis=null;
		TextFormat format = TextFormat.MS949_Korean;
		com.gsoft.common.Compiler_types.Language lang = com.gsoft.common.Compiler_types.Language.Java;
		
		String fullName = fullNameIncludingTemplateExceptArray;
		if (fullName.equals("EditRichText.Character")) {
			int a;
			a=0;
			a++;
		}
		
		//클래스가 템플릿 클래스일 경우 템플릿 타입 이름을 포함하고 배열은 포함하지 않은 클래스 풀 이름이다.
		fullName = CompilerHelper.getArrayElementType(fullName);
		
		try {
			FindClassParams classParams = CompilerHelper.getFindClassParams(Compiler.mlistOfAllClassesHashed, fullName);
			
			if (classParams==null) {
				
				String fullNameExceptTemplate = CompilerHelper.getTemplateOriginalType(fullName);
				
				if (CompilerHelper.IsDefaultType(fullNameExceptTemplate)) {
					return null;
				}
				
				
				
				Compiler compiler = new Compiler();
				String fullNameSlash = fullNameExceptTemplate.replace('.', File.separatorChar);
				String path = Control.pathProjectSrc + File.separator + fullNameSlash;
				if (path.contains("EncodingFormatException")) {
					int a;
					a=0;
					a++;
				}
				String srcPath = CompilerHelper.getSourceFilePath(path);
				boolean addsNewly = false;
				
				if (srcPath==null) {
					// java.lang.String의 소스파일은 없으므로 직접 만든 소스파일을 제공해야 한다.
					// 직접 만든 소스파일의 위치를 찾는다.
					srcPath = CompilerHelper.getSourceFilePathAddingComGsoftCommon(fullNameExceptTemplate);
					
					String fullNameSlash2 = fullNameExceptTemplate.replace('.', File.separatorChar);
					String pathAddingComGsoftCommon = Control.pathProjectSrc + File.separator + "com" + File.separator + "gsoft" + File.separator +
							"common" + File.separator + fullNameSlash2 + ".java";
					
					if (Compiler.AlreadyLoadedClassFromSrc_onlyInterface(pathAddingComGsoftCommon)) {
						// 클래스 캐시에 있는지는 이미 확인했으므로 null 을 리턴한다.
						// java.lang.Float는 클래스 파일 로드가 실패하고 
						// com.gsoft.common.java.lang 패키지 소스파일에도 정의되어 있지도 않으므로
						// java.lang.Float이 매번 필요할 때마다 소스파일을 로드할 필요가 없이 
						// 이미 로드한 소스파일 리스트에서 그것을 확인한다.
						return null;
					}
					if (Compiler.AlreadyLoadedClassFromSrc_onlyInterface_failed(pathAddingComGsoftCommon)) {
						// 클래스 캐시에 있는지는 이미 확인했으므로 null 을 리턴한다.
						// java.lang.Short는 클래스 파일 로드가 실패하고 
						// com.gsoft.common.java.lang 소스파일에도 정의되어 있지도 않으므로
						// java.lang.Short 이 매번 필요할 때마다 소스파일을 로드를 시도할 필요가 없이 
						// 이미 로드한 소스파일 실패 리스트에서 그것을 확인한다.
						return null;
					}
					if (srcPath==null) {
						int a;
						a=0;
						a++;
						// 소스파일을 로드가 실패했으므로 경로를 추가한다.
						Compiler.mlistOfLoadClassFromSrc_onlyInterface_failed.add(pathAddingComGsoftCommon);
						Log.e("can't find its source file : ", fullName);
						CompilerHelper.printMessage(CommonGUI.textViewLogBird, "can't find its source file : " + fullName);
						return null;
					}
					addsNewly = true;
				}
				srcPath += ".java";
			
				Log.e("Trys to read its source file : ", srcPath);
				CompilerHelper.printMessage(CommonGUI.textViewLogBird, "Trys to read its source file : " + srcPath);
				
				
				CommonGUI.loggingForMessageBox.setText(true, "loadClassFromSrc_onlyInterface()-"+fullName, false);
				CommonGUI.loggingForMessageBox.setHides(false);
				Control.view.postInvalidate();
				
				//compiler.filename = srcPath;
				
				stream = new FileInputStream(srcPath);
				bis = new BufferedInputStream(stream);
				
				String input = IO.readString(bis, format);
				//String input = IO.readString_UsingJavaNIO(srcPath, format);
				
				compiler.start_onlyInterface(compiler, input, lang, fullName, srcPath, addsNewly);
				// 소스파일을 로드했으므로 경로를 추가한다.
				Compiler.mlistOfLoadClassFromSrc_onlyInterface.add(srcPath);
				
				// 리소스를 해제한다.
				input = null;
				
				int i;
				for (i=0; i<compiler.mlistOfAllDefinedClasses.count; i++) {
					FindClassParams c = (FindClassParams) compiler.mlistOfAllDefinedClasses.getItem(i);
					if (c.name.equals(fullNameExceptTemplate)) {
						if (c.template!=null) {
							Compiler.applyTypeNameToChangeToTemplateClass(compiler, c, fullName);
							return c;
						}
						return c;
					}
				}
				return null;
				//FindClassParams r = (FindClassParams) compiler.mlistOfClass.list[0]; 
				//return r;
			}
			else {		// 캐시에 있으면		
				return classParams;
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			Log.e("Load Error", e1.toString());
		}
		catch (EncodingFormatException e1) {
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.e("Load Error", e1.toString());
		}
		catch (OutOfMemoryError e1) {
			Log.e("Load Error", e1.toString());
		}
		finally {
			FileHelper.close(bis);
			FileHelper.close(stream);
		}
		return null;
	}
	
	/** Compiler의 checkTypeNameInFileList()에서 파일리스트에 없을때 실제로 그 클래스가 존재하는지를 알기위해 호출이 된다.*/
	/*static FindClassParams loadClassFromSrc_onlyClass(String fullName) {
		FileInputStream stream=null;
		BufferedInputStream bis=null;
		TextFormat format = TextFormat.KSC;
		com.gsoft.common.Compiler.Language lang = com.gsoft.common.Compiler.Language.Java;
		
		
		try {
			// 클래스 캐시를 확인한다.
			FindClassParams classParams = CompilerHelper.getFindClassParams(Compiler.mlistOfAllClassesHashed, fullName);
			
			if (classParams==null) { //클래스캐시에 없으면
				CommonGUI.loggingForMessageBox.setText(true, "loadClassFromSrc_onlyClass()-"+fullName, false);
				CommonGUI.loggingForMessageBox.setHides(false);
				Control.view.postInvalidate();
				
				Compiler compiler = new Compiler();
				String fullNameSlash = fullName.replace('.', File.separatorChar);
				String path = Control.pathProjectSrc + File.separator + fullNameSlash;
				
				String srcPath = CompilerHelper.getSourceFilePath(path);
				srcPath += ".java";
				
				if (compiler.getShortName(fullName).equals("Array")) {
					int a;
					a=0;
					a++;
				}
				
				stream = new FileInputStream(srcPath);
				//int bufferSize = (int) (FileHelper.getFileSize(path, true)*IO.DefaultBufferSizeParam);
				bis = new BufferedInputStream(stream);
				
				String input = IO.readString(bis, format);
				//String input = IO.readString_UsingJavaNIO(srcPath, format);
				
				compiler.start_onlyClass(compiler, input, lang, fullName);
				
				int i;
				for (i=0; i<compiler.mlistOfAllDefinedClasses.count; i++) {
					FindClassParams c = (FindClassParams) compiler.mlistOfAllDefinedClasses.getItem(i);
					if (c.name.equals(fullName)) return c;
				}
				FindClassParams r = (FindClassParams) compiler.mlistOfClass.list[0]; 
				return r;
			}
			else {// 캐시에 있으면
				 
				return classParams;
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			Log.e("Load Error", e1.toString());
		}
		catch (EncodingFormatException e1) {
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.e("Load Error", e1.toString());
		}
		catch (OutOfMemoryError e1) {
			Log.e("Load Error", e1.toString());
		}
		finally {
			FileHelper.close(bis);
			FileHelper.close(stream);
		}
		return null;
	}*/
	
	/**@param  packageName : package com.gsoft.common.gui 문장에서 패키지
	 * @return : java.lang.String, com.gsoft.common.gui.Control 과 같은 이름
	 *  @param usesCache : glistOfFilesInPackage에 저장된 파일리스트가 packageName에 있는 파일리스트와 동일한 것이라면
	 * usesCache이 true일때 새로 리스트를 얻는게 아니라 glistOfFilesInPackage이 캐시로서 사용되고 
	 * usesCache이 false이면 glistOfFilesInPackage을 새로 만든다.*/
	public static String[] getSamePackageClasses(String packageName, boolean usesCache) {
		String pathPackage =  packageName.replace(".", File.separator);
		String path = Control.pathAndroid + File.separator + pathPackage;
		File dirPackage = new File(path);
		String[] listPackage = null;
		
		try{
		
		
		if (usesCache && glistOfFilesInPackage!=null && packageName.equals(curPackageName)) {
			listPackage = glistOfFilesInPackage;
		}
		else {				
			listPackage = dirPackage.list();
			curPackageName = packageName;
			glistOfFilesInPackage = listPackage; 
		}
		
		if (listPackage==null) return null;
		
		ArrayListString r = new ArrayListString(listPackage.length);
		int i;
		for (i=0; i<listPackage.length; i++) {
			String str = listPackage[i];
			if (str.contains("$")==false) {
				str = packageName + "." + str;
				int indexDotClass = str.indexOf(".class");
				if (indexDotClass!=-1) {
					str = str.substring(0, indexDotClass);
					r.add(str);
				}
			}
		}
		return r.getItems();
		}catch(Exception e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return null;
		}
	}
	
	/** varUse가 타입캐스트를 해야하는지 확인한다. 타입캐스트를 해야하면 full name 타입을 리턴한다.
	 * (long)buffer.length에서 varUse가 buffer일 경우 타입캐스트를 해서는 안된다.
	 	((java.lang.Object)buffer[i]).equals("(")에서 varUse가 buffer일 경우 타입캐스트를 해야한다.
		(int)i+0 에서 varUse가 i일 경우 타입캐스트를 해야한다.
		(int)(a+(char)b)+c 는 여기에서 해결할 수 없다.*/
	public static String mustTypeCast(Compiler compiler, HighArray_CodeString src, FindVarUseParams varUse) {
		boolean r = false;
		int i;
		int rightPair;
		// (long)buffer.length에서 varUse가 buffer일 경우 타입캐스트를 해서는 안된다.
		// ((java.lang.Object)buffer[i]).equals("(")에서 varUse가 buffer일 경우 타입캐스트를 해야한다.
		// (int)i+0 에서 varUse가 i일 경우 타입캐스트를 해야한다.
		for (i=varUse.index()+1; i<src.count; i++) {
			CodeString str = src.getItem(i);
			if (CompilerHelper.IsBlank(str) || CompilerHelper.IsComment(str)) continue;
			else if (str.equals("(")) {
				rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", i, src.count-1, false);
				if (rightPair!=-1) i = rightPair;
			}
			else if (str.equals("[")) {
				rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", i, src.count-1, false);
				if (rightPair!=-1) i = rightPair;
			}
			else if (str.equals(")")) {
				r = true;
				break;
			}
			else if (CompilerHelper.IsOperator(str)) { // 공백과 주석을 제외한 구분자
				r = true;
				break;
			}
			else if (str.equals(";") || str.equals(",")) {
				r = true;
				break;
			}
			else {  // '.'
				break;
			}
		}
		if (r==false) return null;
		r = false;
		
		int leftParent, rightParent;
		int typeIndex = -1;
		
		rightParent = compiler.SkipBlank(src, true, 0, varUse.index()-1);
		
		CodeString str = src.getItem(rightParent);
		
		if (str.equals(")")) {
			leftParent = CompilerHelper.CheckParenthesis(src, "(", ")", 0, rightParent, true);
			if (leftParent!=-1) {
				typeIndex = compiler.IsType(src, true, rightParent-1, null);
				if (typeIndex!=-1) {
					int tempLeftPair = compiler.SkipBlank(src, true, 0, typeIndex-1);
					if (tempLeftPair==leftParent) { // 괄호안은 타입캐스트문
						if ( compiler.IsIdentifier( src.getItem(varUse.index()) ) ) { // (타입)id
							r = true;
						}
						/*else if (src.getItem(rightParent+1).equals("(")) { // (타입)(수식)
							int rightParent2 = CompilerHelper.CheckParenthesis(src, "(", ")", rightParent+1, src.count-1, false);
							continue;
						}*/
					}
				}
			}
		}
		if (r) {
			return compiler.getFullNameType(compiler, typeIndex, rightParent-1);
		}
		return null;
		
	}
	
	/**파일에 출력한다.*/
	public static void printStackTrace(File file, Exception e) {
		if (file==null) return;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			CodeString stackTraceMessage = (new StackTracer(e)).getMessage();
			IO.writeString(bos, stackTraceMessage.str, TextFormat.UTF_8, false, true);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			if (fos!=null)
				try {
					fos.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
	}
	/**LogBird에 출력한다.*/
	public static void printStackTrace(TextView tv, Exception e) {
		//boolean isSlave = false;
		if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
			if (Control.isMasterOrSlave==false) {
				//isSlave = true;
				Socket socket = Pipe.socketToMain_client;
				try {
					OutputStream outputStream = socket.getOutputStream();
					//"logWrite"-numOfProcess(Control.numOfCurProcess)-log message
					// 순으로 보내진다.
					IO.writeString(outputStream, "logWrite", TextFormat.UTF_8, true, true);
					int numOfProcess = Control.numOfCurProcess;
					IO.writeString(outputStream, numOfProcess+"", TextFormat.UTF_8, true, true);
					CodeString stackTraceMessage = (new StackTracer(e)).getMessage();
					IO.writeString(outputStream, stackTraceMessage.str, TextFormat.UTF_8, true, true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		if (tv==null) return;
		CodeString stackTraceMessage = (new StackTracer(e)).getMessage();
		tv.setText(tv.numOfLines, stackTraceMessage);
		tv.vScrollPosToLastPage();
		
	}
	/**LogBird에 출력한다.*/
	/*public static void printLog(TextView tv, String message) {
		if (tv==null) return;
		CodeString str = new CodeString(message, Compiler.textColor);
		tv.setText(tv.numOfLines, str);
		tv.vScrollPosToLastPage();
	}*/
	/**LogBird에 출력한다.*/
	public static void printMessage(TextView tv, String message) {
		//boolean isSlave = false;
		if (CommonGUI_SettingsDialog.settings.usesChildCompilerProcess) {
			if (Control.isMasterOrSlave==false) {
				//isSlave = true;
				Socket socket = Pipe.socketToMain_client;
				try {
					OutputStream outputStream = socket.getOutputStream();
					//"logWrite"-numOfProcess(Control.numOfCurProcess)-log message
					// 순으로 보내진다.
					IO.writeString(outputStream, "logWrite", TextFormat.UTF_8, true, true);
					int numOfProcess = Control.numOfCurProcess;
					IO.writeString(outputStream, numOfProcess+"", TextFormat.UTF_8, true, true);
					IO.writeString(outputStream, message, TextFormat.UTF_8, true, true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}
		//if (isSlave==false) {
			if (tv==null) return;
			CodeString strMessage = new CodeString(message, Color.RED);
			if (strMessage.str.contains("getClass")) {
				int a;
				a=0;
				a++;
			}
			tv.setText(tv.numOfLines, strMessage);
			tv.vScrollPosToLastPage();
			Control.view.invalidate();
		//}
	}
	
	static boolean isFileExist(String path) {
		File file = new File(path);
		if (file.exists()) return true;
		else return false;
		
	}
	
	/** classParams 에 static 필드가 있으면 그것들을 초기화하기 위해 
	 * static 디폴트 생성자가 존재하지 않으면
	 * static 생성자를 만든다.
	 * @param classParams
	 */
	public static void makeStaticDefaultConstructorIfStaticDefaultConstructorNotExist(Compiler compiler, FindClassParams classParams) {
		if (classParams.isEnum) return;
		
		if (staticDefaultConstructorExists(compiler, classParams)==false) {
			makeNoneStaticDefaultConstructor(compiler, classParams);
		}
		// 이미 만들었으면 리턴
		//if (classParams.staticConstructorThatCompilerMakes!=null) 
		//	return classParams.staticConstructorThatCompilerMakes;
		
		
	}

	/** classParams에 static 생성자가 아닌 인스턴스 생성자가 존재하는지를 확인한후 없으면 
	 * 인스턴스 생성자를 만든다.*/
	public static void makeNoneStaticDefaultConstructorIfNoneStaticDefaultConstructorNotExist(Compiler compiler, FindClassParams classParams) {
		if (classParams.isEnum) return;
		
		if (noneStaticDefaultConstructorExists(compiler, classParams)==false) {
			makeNoneStaticDefaultConstructor(compiler, classParams);
		}
	}
	
	public static void makeNoneStaticDefaultConstructorIfConstructorNotExist(Compiler compiler, FindClassParams classParams) {
		if (classParams.isEnum) return;
		// void setOnTouchListener(new View.onTouchListener() {});이런 경우를 주의한다.
		//if (classParams.isInterface) return;
		
		
		if (constructorExists(compiler, classParams)==false) {
			makeNoneStaticDefaultConstructor(compiler, classParams);
		}
	}
	
	/** classParams에 static 생성자가 아닌 인스턴스 생성자가 존재하는지를 확인한다.*/
	public static boolean noneStaticDefaultConstructorExists(Compiler compiler, FindClassParams classParams) {
		if (classParams.isEnum) return false;
		int i;
		String shortName = compiler.getShortName(classParams.name);
		for (i=0; i<classParams.listOfFunctionParams.count; i++) {
			FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
			if (func.listOfFuncArgs.count!=0) continue;
			if (func.accessModifier!=null && func.accessModifier.isStatic) continue;
			if (func.name.equals(shortName)) return true;
		}
		return false;
	}
	
	/** classParams에 static 디폴트 생성자가 존재하는지를 확인한다.*/
	public static boolean staticDefaultConstructorExists(Compiler compiler, FindClassParams classParams) {
		if (classParams.isEnum) return false;
		int i;
		String shortName = compiler.getShortName(classParams.name);
		for (i=0; i<classParams.listOfFunctionParams.count; i++) {
			FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
			if (func.listOfFuncArgs.count!=0) continue;
			if (func.accessModifier!=null && func.accessModifier.isStatic==false) continue;
			if (func.name.equals(shortName)) return true;
		}
		return false;
	}
	
	public static boolean constructorExists(Compiler compiler, FindClassParams classParams) {
		if (classParams.isEnum) return false;
		int i;
		String shortName = compiler.getShortName(classParams.name);
		for (i=0; i<classParams.listOfFunctionParams.count; i++) {
			FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
			if (func.name.equals(shortName)) return true;
		}
		return false;
	}
	
	public static void makeStaticDefaultConstructor(Compiler compiler, FindClassParams classParams) {
		FindFunctionParams func = new FindFunctionParams(classParams.compiler, "<clinit>");
		func.accessModifier = new AccessModifier(classParams.compiler, -1, -1);
		func.accessModifier.isStatic = true;
		func.isConstructor = true;
		func.isConstructorThatInitializesStaticFields = true;
		func.parent = classParams;
		//classParams.staticConstructorThatCompilerMakes = func;
		func.isFake = true;
		
		classParams.listOfFunctionParams.add(func);
		compiler.mlistOfAllFunctions.add(func);
	}
	
	/** classParams에 디폴트 생성자를 만든다.*/
	public static void makeNoneStaticDefaultConstructor(Compiler compiler, FindClassParams classParams) {
		if (classParams.isEnum) return;
		FindFunctionParams func=null;
		String fullname = classParams.name;
		String name = compiler.getShortName(fullname);
		func = new FindFunctionParams(compiler, name);
		func.returnType = fullname;
		func.parent = classParams;
		func.isConstructor = true;
		int i;
		if (func.listOfFuncArgs==null) {
			func.listOfFuncArgs = new ArrayListIReset(0);
		}
		if (func.accessModifier==null){
			func.accessModifier = new AccessModifier(compiler, -1, -1);
		}
		func.accessModifier.accessPermission = AccessPermission.Public;
		func.isFake = true;
		classParams.listOfFunctionParams.add(func);
		compiler.mlistOfAllFunctions.add(func);
	}
	
	/**classParams 에 static 필드가 있는지를 확인하여 static 생성자를 필요로하는지를 물어본다.*/
	public static boolean requiresStaticConstructor(FindClassParams classParams) {
		int i;
		for (i=0; i<classParams.listOfVariableParams.count; i++) {
			FindVarParams var = (FindVarParams) classParams.listOfVariableParams.getItem(i);
			if (var.accessModifier!=null && var.accessModifier.isStatic) return true;
		}
		return false;
	}
	
	
	
	/**public ColorDialog(View owner, Rectangle bounds) {<br>
		super(owner, bounds);<br>
	}<br>
	constructor에서 super() 또는 super(xxx) 호출이 있는지를 확인한다.
	가짜 try-block이 들어간 뒤에 이 함수를 호출하면 super()을 찾을 수 없다.
	따라서 이 함수를 가짜 try-block이 들어가기 전에 호출해야 정상적인 결과를 얻을 수 있다.
	*/
	public static boolean hasFuncCallToConstructorOfSuperClass(Compiler compiler, 
			FindFunctionParams constructor) {
		FindStatementParams statement = null;
		if (constructor.listOfStatements.count>0) {
			statement = (FindStatementParams) constructor.listOfStatements.getItem(0);
		}
		else {
			return false;
		}
		
		if (statement.isFake==false) {
			int indexSuper = 
					compiler.SkipBlank(compiler.mBuffer, false, statement.startIndex(), compiler.mBuffer.count-1);
			if (compiler.mBuffer.getItem(indexSuper).equals("super")==false)
				return false;
			//int indexInListOfVarUses = 
					//compiler.getIndexInmListOfAllVarUses2(constructor.listOfAllVarUsesForFunc, 0, indexSuper, true);
			FindVarUseParams varUseSuper =
					compiler.getVarUseWithIndex(((FindClassParams)constructor.parent).listOfAllVarUsesForFuncHashed, 
							"super", indexSuper);
			//FindVarUseParams varUseSuper = constructor.listOfAllVarUsesForFunc.getItem(indexInListOfVarUses);
			try{
			if (varUseSuper!=null && /*varUseSuper.name!=null && varUseSuper.name.equals("super") &&*/ 
				varUseSuper.listOfFuncCallParams!=null && varUseSuper.listOfFuncCallParams.count>=0)
				return true;
			}catch(Exception e) {
				e.printStackTrace();
				int a;
				a=0;
				a++;
			}
		}
		else {
			// statement는 super();이다.
			if (statement instanceof FindIndependentFuncCallParams)
				return true;
		}
		return false;
	}
	
	
	
	static int indexOf(String str, int startIndex, char c) {
		int i;
		for (i=startIndex; i<str.length(); i++) {
			if (str.charAt(i)==c) {
				return i;
			}
		}
		return -1;
	}
	
	static int indexOf(String str, int startIndex, char c, boolean reverse) {
		if (reverse) {
			int i;
			for (i=startIndex; i>=0; i--) {
				if (str.charAt(i)==c) {
					return i;
				}
			}
			return -1;
		}
		return -1;
	}
	
	/** <summary>startIndex부터 endIndex까지 
     * '{,(,['(charOfLeftPair)와 '},),]'(charOfRightPair)을 스택에 넣고 빼며 쌍이 맞는지 확인한다. 
    * block단위로 확인해야 정확한 pair를 찾을 수 있다. 
    * 다시말해 괄호에러를 고칠 수 있다. 처음 만난 괄호의 쌍의 인덱스를 리턴한다. 
    * reverse가 false이면 startIndex부터 endIndex까지 인덱스를 증가시키면서 검색, 못 찾으면 -1을 리턴
	 *  reverse가 true이면 endIndex부터 startIndex까지 인덱스를 감소시키면서 검색, 못 찾으면 -1을 리턴
	 *  </summary>
	 *  @param startIndex : isReverse가 false일 경우 charOfLeftPair의 인덱스여야 한다.
	 *  그래야 괄호의 쌍이 맞는다.
	 *  @param endIndex :	isReverse가 true일 경우 charOfRightPair의 인덱스여야 한다. 
	 *  그래야 괄호의 쌍이 맞는다.*/
    public static int CheckParenthesis(HighArray_CodeString src, 
    		String charOfLeftPair, String charOfRightPair, 
    		int startIndex, int endIndex, boolean isReverse) 
    {
        int i;
        
        Stack<String> stack = new Stack<String>();
        if (isReverse==false) {
        	if (startIndex<0) return -1;
	        for (i = startIndex; i <= endIndex; i++)
	        {
	        	CodeString str = src.getItem(i);
	        	if (CompilerHelper.IsComment(str)) continue;
	        	if (CompilerHelper.IsConstant(str)) continue;
	            if (str.equals(charOfLeftPair))
	            { 
	                stack.Push(charOfLeftPair);
	            }
	            else if (str.equals(charOfRightPair))
	            {
	            	if (stack.len==2) { 
	            		// 최상위 class블록(1)안에서 두번째 블록(함수 혹은 클래스)의 닫는 } 
	            		int a;
	            		a=0;
	            		a++;
	            	}
	            	if (stack.len>0)
	                {
	            		stack.Pop();
	            		if (stack.len==0) {
	            			return i;
	            		}
	                }
	            }            
	        } // for
        }
        else {
        	for (i = endIndex; i >= startIndex; i--)
	        {
	        	CodeString str = src.getItem(i);
	        	if (CompilerHelper.IsComment(str)) continue;
	        	if (CompilerHelper.IsConstant(str)) continue;
	            if (str.equals(charOfRightPair))
	            { 
	                stack.Push(charOfRightPair);
	            }
	            else if (str.equals(charOfLeftPair))
	            {
	            	if (stack.len==2) { 
	            		// 최상위 class블록(1)안에서 두번째 블록(함수 혹은 클래스)의 닫는 } 
	            		int a;
	            		a=0;
	            		a++;
	            	}
	            	if (stack.len>0)
	                {
	            		stack.Pop();
	            		if (stack.len==0) {
	            			return i;
	            		}
	                }
	            }            
	        } // for
        }
        
        return -1;
    }
    
    /** <summary>startIndex부터 endIndex까지 
     * '{,(,['(charOfLeftPair)와 '},),]'(charOfRightPair)을 스택에 넣고 빼며 쌍이 맞는지 확인한다. 
    * block단위로 확인해야 정확한 pair를 찾을 수 있다. 
    * 다시말해 괄호에러를 고칠 수 있다. 처음 만난 괄호의 쌍의 인덱스를 리턴한다. 
    * reverse가 false이면 startIndex부터 endIndex까지 인덱스를 증가시키면서 검색, 못 찾으면 -1을 리턴
	 *  reverse가 true이면 endIndex부터 startIndex까지 인덱스를 감소시키면서 검색, 못 찾으면 -1을 리턴
	 *  주의사항 : 스트링에 주석이 있어서는 안된다.
	 *  </summary>*/
    public static int CheckParenthesis(String str, 
    		String charOfLeftPair, String charOfRightPair, 
    		int startIndex, int endIndex, boolean isReverse) 
    {
        int i;
        
        Stack<String> stack = new Stack<String>();
        if (isReverse==false) {
	        for (i = startIndex; i <= endIndex; i++)
	        {
	        	char c = str.charAt(i);
	        	//if (CompilerHelper.IsComment(c)) continue;
	        	//if (CompilerHelper.IsBlank(c.c)) continue;
	            if (c==charOfLeftPair.charAt(0))
	            { 
	                stack.Push(charOfLeftPair);
	            }
	            else if (c==charOfRightPair.charAt(0))
	            {
	            	if (stack.len==2) { 
	            		// 최상위 class블록(1)안에서 두번째 블록(함수 혹은 클래스)의 닫는 } 
	            		int a;
	            		a=0;
	            		a++;
	            	}
	            	if (stack.len>0)
	                {
	            		stack.Pop();
	            		if (stack.len==0) {
	            			return i;
	            		}
	                }
	            }            
	        } // for
        }
        else {
        	for (i = endIndex; i >= startIndex; i--)
	        {
	        	char c = str.charAt(i);
	        	//if (CompilerHelper.IsComment(c)) continue;
	        	//if (CompilerHelper.IsBlank(c.c)) continue;
	            if (c==charOfRightPair.charAt(0))
	            { 
	                stack.Push(charOfRightPair);
	            }
	            else if (c==charOfLeftPair.charAt(0))
	            {
	            	if (stack.len==2) { 
	            		// 최상위 class블록(1)안에서 두번째 블록(함수 혹은 클래스)의 닫는 } 
	            		int a;
	            		a=0;
	            		a++;
	            	}
	            	if (stack.len>0)
	                {
	            		stack.Pop();
	            		if (stack.len==0) {
	            			return i;
	            		}
	                }
	            }            
	        } // for
        }
        
        return -1;
    }
    
    /** str이 int[], buffer[i]등일 경우 int, buffer의 인덱스를 리턴한다. 틀린 배열일 경우 -1을 리턴*/
    public static int getArrayNameIndex(Compiler compiler, HighArray_CodeString src, int index) {
		int r = -1;
		int i;
		int leftPair;
		if (index<0) return -1;
		if (src.getItem(index).equals("]")==false) return -1;
		for (i=index; i>=0; i--) {
			if (src.getItem(i).equals("]")) {
				leftPair = CheckParenthesis(src, "[", "]", 0, i, true);
				if (leftPair!=-1) {
					i = leftPair;
				}
				else {
					Compiler.errors.add(new Error(compiler, i, i, "] not paired."));
					return -1;
				}
			}
			else break;
		}
		r = i;
		return r;
	}
    
    /** 배열원소인지 아니면 배열타입선언인지 확인한다. 
     * @param startIndexArraySubscription : [의 인덱스
     * @param endIndexArraySubscription : ]의 인덱스 이후 공백이나 주석 포함가능*/
    public static boolean isArrayElement(Compiler compiler, int startIndexArraySubscription, 
    		int endIndexArraySubscription) {
    	for (int i=startIndexArraySubscription; i<=endIndexArraySubscription; i++) {
    		CodeString str = compiler.mBuffer.getItem(i);
    		if (IsBlank(str) || IsComment(str)) continue;
    		else if (compiler.IsIdentifier(str) || CompilerHelper.IsNumber2(str)!=0) return true;
    		else if (IsConstant(str)) return true;
    	}
		return false;
    	
    	
    }
    
    /** int[] a;에서 b=a[1];에서 fullnameOfArrayType이 int[]이고 varUse가 a[1]이면 int를 리턴한다.
     * 틀리면 errors에 에러를 출력한다.*/
    public static String getTypeOfArrayElement(Compiler compiler, String fullnameOfArrayType, FindVarUseParams varUse) {
    	int dimension1 = CompilerHelper.getArrayDimension(compiler, fullnameOfArrayType);
    	int dimension2 = CompilerHelper.getArrayDimension(compiler, varUse.name);
    	if (dimension1!=dimension2) {
    		Compiler.errors.add(new Error(compiler, varUse.index(), varUse.index(), "Array dimension invalid. : "+fullnameOfArrayType+"-"+varUse.name));
    	}
    	else {
    		String r = CompilerHelper.getArrayElementType(fullnameOfArrayType);
    		return r;
    	}
		return null;
    	
    }
    
    public static int getMaxArrayLengthInCurDepth(FindArrayInitializerParams topArray, 
			FindArrayInitializerParams array) {
    	ArrayListInt listOfLength = topArray.listOfLength;
    	int curDepth = array.depth;
    	return listOfLength.getItem(curDepth);
    	
    }
	
	/** str이 int[], buffer[i]일 경우 차원 1을 리턴한다. 배열이 아니면 0을 리턴*/
	public static int getArrayDimension(Compiler compiler, String str) {
		int dimensionOfArray = 0;
		int i;
		int leftPair;
		if (str==null) {
			int a;
			a=0;
			a++;
		}
		if (str.length()<1) return 0;
		if (str.charAt(str.length()-1)!=']') return 0;
		for (i=str.length()-1; i>=0; i--) {
			if (str.charAt(i)==']') {
				leftPair = CheckParenthesis(str, "[", "]", 0, i, true);
				if (leftPair!=-1) {
					i = leftPair;
				}
				else {
					Compiler.errors.add(new Error(compiler, i, i, "] not paired."));
					return 0;
				}
				dimensionOfArray++;
			}
			else break;
		}
		return dimensionOfArray;
	}
	
	/** str이 int[]일 경우 첨자를 뺀 원소 타입 int를 리턴한다. str은 타입이름이어야 한다. 
	 * int[10][10]와 같은 배열원소는 int를 리턴한다. str에 주석이나 애노테이션등이 있어서는 안된다.*/
	public static String getArrayElementType(String str) {
		if (str.length()<=0) return null;
		if (str==null) return null;
		int indexRightPair = str.length()-1;
		int indexLefttPair;
		int typeStartIndex=0, typeEndIndex;
		int index = indexRightPair;
		char c;
		while (true) {
			if (index==-1) {
				int a;
				a=0;
				a++;
			}
			c = str.charAt(index);
			if (c==']') {
				indexLefttPair = CheckParenthesis(str, "[", "]", 0, indexRightPair, true);
				if (indexLefttPair==-1) { // error
					return null;
				}
				index = indexLefttPair-1;
				indexRightPair = index;
			}
			else {
				typeEndIndex = index;
				break;
			}
		}
		String type = str.substring(typeStartIndex, typeEndIndex+1);
		return type;
		
		/* while (true) {
			if (str.contains("[]")) {
				str = str.substring(0, str.length()-2); // 마지막 []을 제거
			}
			else break;
		}
		return str;*/
	}
	
	/** str은 타입이름이어야 한다. str이 int이고 dimension이 1일 경우 int[]을 리턴한다.*/
	public static String getArrayType(String str, int dimension) {
		int i;
		String r = str;
		for (i=0; i<dimension; i++) {
			r += "[]";
		}
		return r;
	}
	
	/*public static String getTemplateOriginalType(ArrayListCodeString src, String template, 
			int startIndex, int endIndex) {
		boolean isTemplate = false;
		int indexTemplateLeftPair = template.indexOf("<");
		int indexTemplateRightPair = -1;
		
		String strTemplate = "";
		if (indexTemplateLeftPair!=-1) {
			int indexTemplateLeftPairInmBuffer = Skip(src, false, "<", startIndex, endIndex);
			int indexTemplateRightPairInmBuffer = 
					CompilerHelper.CheckParenthesis(src, "<", ">", indexTemplateLeftPairInmBuffer, endIndex, false);
			
			indexTemplateRightPair = 
				CompilerHelper.CheckParenthesis(template, "<", ">", indexTemplateLeftPair, template.length()-1, false);
			isTemplate = true;
			if (indexTemplateRightPair!=-1) {
				// Stack<Object>에서 <Object>부분을 말한다. 
				//strTemplate = str.substring(indexTemplateLeftPair, indexTemplateRightPair+1);
				strTemplate = 
						"<"+getFullNameType(src, indexTemplateLeftPairInmBuffer+1, indexTemplateRightPairInmBuffer-1)+">";
				// 위에서 Stack부분
				template = template.substring(0, indexTemplateLeftPair);
				
			}
		}
		
		int i;
		String r = typeName + templateStr;
		return r;
	}*/
	
	/** mlistOfAllTemplates에서 leftPair로 템플릿을 찾아 리턴한다. rightPair는 아무 값이나 넣어도 된다.*/
	public static Template getTemplate(Compiler compiler, int leftPair, int rightPair) {
		ArrayListIReset list = compiler.mlistOfAllTemplates;
		int i;
		for (i=0; i<list.count; i++) {
			Template t = (Template) list.getItem(i);
			if (t.indexLeftPair()==leftPair/* && t.indexRightPair==rightPair*/)
				return t;
		}
		return null;
	}
	
	/** fullname이 템플릿이면 템플릿 괄호 안에 있는 타입을 리턴한다.*/
	public static String getTemplateTypeInPair(String fullname) {
		int indexTemplateLeftPair = fullname.indexOf("<");
		
		String r = null;
		if (indexTemplateLeftPair!=-1) {			
			int indexTemplateRightPair = 
					CompilerHelper.CheckParenthesis(fullname, "<", ">", 
							indexTemplateLeftPair, fullname.length()-1, false);
			if (indexTemplateRightPair==-1) return null;
			else {
				r = fullname.substring(indexTemplateLeftPair+1, indexTemplateRightPair);
				return r;
			}
		}
		return fullname;
	}
	
	/** fullname이 템플릿이면 템플릿을 제거한 원래 타입이름을 리턴한다.*/
	public static String getTemplateOriginalType(String fullname) {
		if (fullname==null) {
			int a;
			a=0;
			a++;
			return null;
		}
		int indexTemplateLeftPair = fullname.indexOf("<");
		
		String r = null;
		if (indexTemplateLeftPair!=-1) {
			r = fullname.substring(0, indexTemplateLeftPair);
			return r;
		}
		return fullname;
	}
	
	public static String getTemplateType(String typeName, String templateStr) {
		int i;
		String r = typeName + templateStr;
		return r;
	}
	
	/**@param fullClassName : java.lang.String과 같은 이름*/
	static String getPackageName(String fullClassName) {
		String pathPackage =  fullClassName.replace(".", File.separator);
		int indexSeparator=0;
		String path = pathPackage;
		String r = null;
		
		while (true) {
			indexSeparator = indexOf(pathPackage, indexSeparator+1, File.separatorChar);
			if (indexSeparator==-1) {
				break;
			}
			path = pathPackage.substring(0, indexSeparator);
			String fullPath = Control.pathAndroid + File.separator + path;
			File dirPackage = new File(fullPath);
			if (dirPackage.exists()==false) {
				break;
			}
			else {
				r = path;
			}
		}
		if (r!=null) {
			r = r.replace(File.separator, ".");
		}
		return r;
	}
	
	/** str의 디렉토리구분자는 File.separatorChar이다.*/
	static boolean isFile(String str) {
		File file = new File(str);
		if (file.isFile()) return true;
		return false;
	}
	
	/** fullNameExceptTemplate 라는 소스파일을 찾을 수 없으면 
	 * "com.gsoft.common."을 fullNameExceptTemplate 앞에 붙여서 소스 파일이 
	 * 존재하는지 확인한다.
	 * @param fullNameExceptTemplate : 예를들어 java.lang.String
	 * @return
	 */
	static String getSourceFilePathAddingComGsoftCommon(String fullNameExceptTemplate) {
		String fullNameSlash = fullNameExceptTemplate.replace('.', File.separatorChar);
		String path = Control.pathProjectSrc + File.separator + "com" + File.separator + "gsoft" + File.separator +
				"common" + File.separator + fullNameSlash;
		
		String r = getSourceFilePath(path);
		return r;
	}
	
	/** /mnt/sdcard/project_src/com/gsoft/common/gui/control/container에서 
	 * 소스파일부분은 /mnt/sdcard/project_src/com/gsoft/common/gui/control이다. 
	 * path에서 뒤에 확장자는 붙이지 않는다.*/ 
	static String getSourceFilePath(String path) {
		// /mnt/sdcard/project_src/com/gsoft/common/gui를 얻는다.
		String dir = getDirectory(path);
		if (dir==null) return null;
		int indexStart = dir.length()+1; ///mnt/sdcard/project_src/com/gsoft/common/gui/의 길이
		int indexSrcFile = path.indexOf(File.separator, indexStart);
		String r;
		if (indexSrcFile!=-1) {			
			r = path.substring(0, indexSrcFile);
			//  /mnt/sdcard/project_src/com/gsoft/common/gui/control
		}
		else {
			r = path;
		}
		
		String rIncludeingJava = r + ".java";
		
		if (isFileExist(rIncludeingJava)) {
			return r; 
		}
		// /mnt/sdcard/project/com/gsoft/common/Encoding/EncodingFormatException.java는 존재하지 않는다.
		
		int separator = r.length();
		String javaPath, javaPathExceptExt;
		// 만약에 /mnt/sdcard/project/com/gsoft/common/Encoding/EncodingFormatException/InnerClass2가
		// /mnt/sdcard/project/com/gsoft/common/Encoding.java라는 소스 파일이 있다면 
		// 루프를 통해서 그 소스파일이 존재하는지를 확인해야 한다.
		while (true) {
			separator = indexOf(r, separator-1, File.separatorChar, true);
			if (separator==-1) return null;
			javaPathExceptExt = r.substring(0, separator);
			javaPath = javaPathExceptExt + ".java";
			
			if (isFileExist(javaPath)) {
				return javaPathExceptExt; 
			}
			
		}
		
		
	}
	
	/** str의 디렉토리구분자는 File.separatorChar이다.*/
	static boolean isDirectory(String str) {
		File file = new File(str);
		if (file.isDirectory()) return true;
		return false;
	}
	
	/** /mnt/sdcard/gsoft/com/gsoft/common/Util에서 디렉토리부분은 /mnt/sdcard/gsoft/com/gsoft/common이다.*/ 
	static String getDirectory(String path) {
		String originalPath = new String(path);
		int separator=0;
		int separatorOld=0;
		boolean r=false;
		
		r = isDirectory(path);
		if (r) {
			return path;
		}
		
		while (separator!=-1) {
			separator = indexOf(originalPath, separator, File.separatorChar);
			if (separator==-1) {
				r = isDirectory(originalPath);
				if (r) return originalPath;
				else {
					String dir = originalPath.substring(0, separatorOld);
					return dir;
				}
			}
			path = originalPath.substring(0, separator+1);
			r = isDirectory(path);
			if (r) {
				separatorOld = separator;
			}
			else {
				String dir = originalPath.substring(0, separatorOld);
				return dir;
			}
			separator++;
		}
		return null;
	}
	
	/**리턴시에는 ".class"가 붙는다. 
	 * @param classPath : 디렉토리가 포함된 파일이름이므로 확장자가 아닌 
	 * 디렉토리 구분자는 File.separatorChar이므로 확장자 외에 '.'이 있어서는 안된다. 
	 * 내부클래스를 표시하는 '$'이 있을수는 있다. ".class"가 붙어선 안된다.
	 * */
	static String fixClassPath(String classPath) {
		classPath = FileHelper.getFilenameExceptExt(classPath);
		
		String path = classPath;
		int separator=0;
		int separatorOld=0;
		boolean r=false;
		
		while (separator!=-1) {
			separator = indexOf(classPath, separator, File.separatorChar);
			if (separator==-1) {
				path = classPath + ".class";
				r = isFileExist(path);
				break;
			}
			
			path = classPath.substring(0, separator+1);
			
			r = isFileExist(path);
			
			if (r) {
				String remainder=null;
				try {
				remainder = classPath.substring(separatorOld+1, classPath.length());
				
				remainder = remainder.replace(File.separatorChar, '$');
				path = classPath.substring(0, separatorOld+1) + remainder + ".class";
				}catch(Exception e) {
					e.printStackTrace();
					CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				}
				boolean r2 = isFileExist(path);
				if (r2)	break;
			}
			if (r==false) {
				String remainder=null;
				try {
				remainder = classPath.substring(separatorOld+1, classPath.length());
				
				remainder = remainder.replace(File.separatorChar, '$');
				path = classPath.substring(0, separatorOld+1) + remainder + ".class";
				}catch(Exception e) {
					e.printStackTrace();
					CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
				}
				r = isFileExist(path);
				break;
			}
			
			separatorOld = separator;
			separator++;
		}
		if (r)  return path;
		else return null;
	}
	
	/** 자주 사용하는 java 패키지의 클래스들을 모아 놓은 배열(Compiler_types.glistOfJavaClassesForThread)에
	 * 있는 클래스들을 스레드(ThreadReadingJavaClassesUsingWell)를 활용하여 loadClass()로 읽어 캐시에
	 * 등록하여 성능을 높인다.
	 */
	public static void startThreadReadingJavaClassesUsingWell() {
		ThreadReadingJavaClassesUsingWell thread = new ThreadReadingJavaClassesUsingWell();
		thread.start();
	}
	
	/** 자주 사용하는 java 패키지의 클래스들을 모아 놓은 배열(Compiler_types.glistOfJavaClassesForThread)에
	 * 있는 클래스들을 스레드(ThreadReadingJavaClassesUsingWell)를 활용하여 loadClass()로 읽어 캐시에
	 * 등록하여 성능을 높인다.
	 */
	public static class ThreadReadingJavaClassesUsingWell extends Thread {
		public void run() {
			int i;
			Compiler compiler = new Compiler();
			for (i=0; i<Compiler_types.glistOfJavaClassesForThread.length; i++) {
				String fullName = Compiler_types.glistOfJavaClassesForThread[i];
				loadClass(compiler, fullName);
			}
		}
	}
	
	
	/** 변수나 메서드를 이미 상속하였으면 true, 그게 아니면 false를 리턴한다.
	 * findMembersInherited() 에서 상속을 하다보면 중복해서 상속을 할 경우도 있으므로
	 * (특히 java.lang.Object의 경우) 그것을 피하기 위해 호출한다.
	 * */
	public static 
	boolean hasInherittedMember(FindClassParams classParams, FindVarParams var, FindFunctionParams func) {
		if (var!=null) {
			boolean r = classParams.hasVarInheritted(var);
			return r;
		}
		if (func!=null) {
			boolean r = classParams.hasFuncInheritted(func);
			return r;
		}
		return false;
	}
	
	
		
	
	/** inner class를 함께 읽을수도 있고 아닐수도 있다.
	 * compiler.mlistOfAllClasses에 이미 로드된 클래스가 있다면 그것을 리턴하고,
	 * 없다면 그 클래스를 클래스파일에서 로드하고 compiler.mlistOfAllClasses에 등록하고 리턴한다.
	 * 또한 상속까지 한다.(외부 클래스들은 loadClass에서 상속, 파일에서 정의하는 클래스들은 따로 상속을 해야한다.)
	 * 클래스 로드가 실패하면 소스파일에서 로드한다(loadClassFromSrc_onlyInterface 참조). 
	 * @param compiler : 어떤 compiler도 가능하다.
	 * @param fullName : java.lang.String과 같은 이름
	 * @param fullNameIncludingTemplateAndArray : 클래스가 템플릿 클래스일 경우 템플릿 타입 이름을 포함하고 배열도 포함하는 클래스 풀 이름이다.*/
	public static FindClassParams loadClass(Compiler compiler, String fullNameIncludingTemplateAndArray) {
		String fullName = fullNameIncludingTemplateAndArray;
		if (fullName==null || fullName.equals("")) return null;
		
		String typeNameInTemplatePair = null;
		if (fullName.contains("<")) {
			int a;
			a=0;
			a++;
			typeNameInTemplatePair = CompilerHelper.getTemplateTypeInPair(fullName);
		}
		
		//클래스가 템플릿 클래스일 경우 템플릿 타입 이름을 포함하고 배열은 포함하지 않은 클래스 풀 이름이다.
		try {
		fullName = CompilerHelper.getArrayElementType(fullName);
		}catch(Exception e) {
			int a;
			a=0;
			a++;
			e.printStackTrace();
		}
		
		if (fullName.equals("java.lang.Number")) {
			int a;
			a=0;
			a++;
		}
		if (compiler.getShortName(fullName).equals("PoolOfButton")) {
			int a;
			a=0;
			a++;
		}
		if (compiler.getShortName(fullName).equals("float")) {
			int a;
			a=0;
			a++;
		}
		/*if (fullName.contains("Stack") && fullName.contains("<")) {
			int a;
			a=0;
			a++;			
			FindClassParams r = CompilerHelper.loadClassFromSrc_onlyInterface(fullName);
			Compiler.applyTypeNameToChangeToTemplateClass(compiler, r, fullName);
			if (r==null) return null;
		}*/
		if (compiler.IsDefaultType(fullName)) {
			return null;
		}
		try {
			//if (CompilerHelper.exists(Compiler.mlistOfAllClasses,fullName)==false) {
			// 클래스 캐시를 확인한다.
			FindClassParams classParams = CompilerHelper.getFindClassParams(Compiler.mlistOfAllClassesHashed, fullName);
			
			if (classParams==null) { // 캐시에 없으면
				String classPath;
				String fullNameExceptTemplate = CompilerHelper.getTemplateOriginalType(fullName);
				classPath = fullNameExceptTemplate.replace('.', File.separatorChar); 
				classPath = Control.pathAndroid + File.separator + classPath + ".class";
				
				if (fullNameExceptTemplate.equals("com.gsoft.common.Encoding.EncodingFormatException")) {
					int a;
					a=0;
					a++;
				}
				
				String path = fixClassPath(classPath);
				
				if (path!=null) classPath = path;
				else {
					// 클래스 패스가 잘못되어 소스 로드를 시도할 때
					// (예를들어 라이브러리가 설치되지 않거나 라이브러리 경로가 틀린 경우)
					// 소스를 로드하기 전에 소스 로드가 이미 실패했다면 다시 로드하지 않는다.
					if (PathClassLoader.failedLoadingAlready(classPath)) {
						return null;
					}
					// 클래스패스가 잘못되었으면 소스를 로드한다.
					FindClassParams r = CompilerHelper.loadClassFromSrc_onlyInterface(fullName);
					if (r==null) {
						// 소스 로드가 실패하면 클래스 로드 실패 리스트에 등록하여
						// 다시 로드 하지 않는다.
						//PathClassLoader.listOfClassesToFailLoading.add(classPath);
						return null;
					}
				}
				
				// 클래스 파일 로드가 이미 실패했다면
				if (PathClassLoader.failedLoadingAlready(classPath)) {
					// 실패하면 소스를 로드한다.
					FindClassParams r = CompilerHelper.loadClassFromSrc_onlyInterface(fullName);
					if (r==null) return null;
					return null;
				}
				
				// 클래스 파일을 로드한다.
				// 클래스 파일 로드가 실패하면 실패 리스트(failedLoadingAlready())에 등록한다.
				PathClassLoader loader = new PathClassLoader(classPath, typeNameInTemplatePair, fullName, false);
				if (loader!=null && loader.classParams!=null) {
					Compiler.mlistOfAllClasses.add(loader.classParams);
					Compiler.mlistOfAllClassesHashed.input(loader.classParams);
					if (!loader.classParams.hasInherited) {
						compiler.FindMembersInherited(loader.classParams);
					}
					return loader.classParams;
				}
				
				// 실패하면 소스를 로드한다.
				String shortName = compiler.getShortName(fullNameExceptTemplate);
				if (shortName.equals("IO")) {
					int a;
					a=0;
					a++;
				}
				FindClassParams r = CompilerHelper.loadClassFromSrc_onlyInterface(fullName);
				if (r==null) return null;
				
				return r;
			}
			else { // 캐시에 있으면
				if (typeNameInTemplatePair!=null) {
					classParams.changeToTemplate(typeNameInTemplatePair);
				}
				//compiler.FindClassesFromTypeDecls(compiler, result)
				classParams.compiler.FindMembersInherited(classParams);
				return classParams;
			}
			
		}catch(Exception e) {
			try{
			Log.e("error", e.getMessage());
			}catch(Exception e2) {}
			//e.printStackTrace();
			//CompilerHelper.printStackTrace(textViewLogBird, e);
		}
		return null;
	}
	
	
	/*public static boolean exists(ArrayList listOfClasses, String fullClassName) {
		int i;
		for (i=0; i<listOfClasses.count; i++) {
			FindClassParams item = (FindClassParams) listOfClasses.getItem(i);
			if (item.name!=null) {
				if (item.name.equals(fullClassName)) return true;
			}
		}
		return false;
	}*/
	
	public static boolean exists(Hashtable_FullClassName listOfClassesHashed, String fullClassName) {
		FindClassParams item = listOfClassesHashed.getData(fullClassName);
		if (item!=null) return true;
		return false;
	}
	
	/*public static FindClassParams getFindClassParams(ArrayList listOfClasses, String fullClassName) {
		int i;
		for (i=0; i<listOfClasses.count; i++) {
			FindClassParams item = (FindClassParams) listOfClasses.getItem(i);
			if (item.name!=null) {
				if (item.name.equals(fullClassName)) return item;
			}
		}
		return null;
	}*/
	
	/** @param fullClassNameIncludingTemplateExceptArray : 클래스가 템플릿 클래스일 경우 템플릿 타입 이름을 포함하고 배열은 포함하지 않은 클래스 풀 이름이다.*/
	public static synchronized 
	FindClassParams getFindClassParams(Hashtable_FullClassName listOfClassesHashed, String fullClassNameIncludingTemplateExceptArray) {
		synchronized(listOfClassesHashed) {
			FindClassParams item = listOfClassesHashed.getData(fullClassNameIncludingTemplateExceptArray);
			return item;
		}
	}
	
	/** fullName에 템플릿에 배열기호가 포함될경우 
	 * shortName은 템플릿, 배열기호를 포함한 마지막 '.', '/', '\\', '$' 이후 이름을 리턴한다.
	 * '.', '/', '\\', '$'  이 없으면 원래 fullName을 리턴한다.
	 * @param fullName
	 * @return
	 */
	public static String getShortName(String fullName) {
		if (fullName==null) return null;
		int i;
		for (i=fullName.length()-1; i>=0; i--) {
			if (fullName.charAt(i)=='.') break;
			else if (fullName.charAt(i)=='/') break;
			else if (fullName.charAt(i)=='\\') break;
			else if (fullName.charAt(i)=='$') break;
		}
		return fullName.substring(i+1, fullName.length());
	}
	
	/** 자바 타입만 가능하다. int, char, float등 기본타입*/
    public static boolean IsDefaultType(String c) {
    	if (c==null) return false;
    	int i;
    	if (Compiler.TypesOfJava==null) return false;
    	for (i=0; i<Compiler.TypesOfJava.length; i++) {
    		if (c.equals(Compiler.TypesOfJava[i])) return true;
    	}
    	return false;
    }
	
	/** 주석, 다큐주석, 애노테이션인지를 확인한다.*/
	public static boolean IsComment(CodeChar c) {
		if (c.type==CodeStringType.Comment ||
				c.type==CodeStringType.DocuComment ||
				c.type==CodeStringType.Annotation) return true;
		return false;
	}
	
	/** 주석, 다큐주석, 애노테이션이면 true, 그렇지않으면 false*/
	public static boolean IsComment(CodeString str) {
		if (str==null || str.count<=0) {
			int a;
			a=0;
			a++;
		}
		CodeChar c = str.charAt(0); 
		if (c.type==CodeStringType.Comment ||
				c.type==CodeStringType.DocuComment ||
				c.type==CodeStringType.Annotation) return true;
		return false;
	}
	
	/** 다큐주석*/
	public static boolean IsRegularComment(CodeString str) {
		CodeChar c = str.charAt(0); 
		if (c.type==CodeStringType.Comment) return true;
		return false;
	}
	
	/** 다큐주석*/
	public static boolean IsDocuComment(CodeString str) {
		CodeChar c = str.charAt(0); 
		if (c.type==CodeStringType.DocuComment) return true;
		return false;
	}
	
	/** 애노테이션인지를 확인한다.*/
	public static boolean IsAnnotation(CodeString str) {
		CodeChar c = str.charAt(0); 
		if (c.type==CodeStringType.Annotation) return true;
		return false;
	}
	
	/** 상수(인용문, 숫자, 불리안 등)인지를 확인한다. 여기서 인용문은 "abc", 'a'를 말한다. 
	 * 숫자는 정수, 부동소수점을 말한다. 또한 null을 포함한다.*/
	public static boolean IsConstant(CodeString str) {
		if (str.equals("null")) return true;
		if (str.equals("true") || str.equals("false")) return true;
		CodeChar c = str.charAt(0); 
		if (c.type==CodeStringType.Constant) return true;
	
		if (str.equals("0xff")) {
			int a;
			a=0;
			a++;
		}
		int isNumber = IsNumber2(str);
		if (isNumber!=0) {
			return true;
		}
		return false;
	}
	
	/*public static boolean IsNumber(CodeString str) {
		int i;
		char ch;
		int lenOfStr = str.length();
		if (lenOfStr>1) {
			// 첫번째 문자
			ch = str.charAt(0).c;
			if ((('0'<=ch && ch<='9') || ch=='-' || ch=='+')==false) {
				return false;
			}
			
			// 중간들
			boolean isDotFound = false;
			// -.3f(x), +.2f(x)
			if (str.charAt(1).c=='.' && 
					(str.charAt(0).c=='-' || str.charAt(0).c=='+') ) return false;
			for (i=1; i<lenOfStr-1; i++) {			
				ch = str.charAt(i).c;
				if (isDotFound && ch=='.') return false; // 소수점이 두개 이상인경우
				if (ch=='.') {					
					isDotFound = true;
				}
				if (ch=='-' || ch=='+') return false;
				if ((('0'<=ch && ch<='9') || ch=='.')==false) {					
					return false;
				}
			}
		
			// 마지막 문자
			ch = str.charAt(lenOfStr-1).c;
			if (str.str.contains(".")) { // 실수
				if ((('0'<=ch && ch<='9') || ch=='f' || ch=='d')==false) {
					return false;
				}
			}
			else { // 정수
				if ((('0'<=ch && ch<='9') || ch=='c' || ch=='i' || ch=='s' || ch=='l')==false) {
					return false;
				}
			}
		}
		else if (lenOfStr==1) {
			ch = str.charAt(0).c;
			if (('0'<=ch && ch<='9')==false) return false;
			else return true;
		}
		else {
			return false;
		}
		
		int posOfDot = str.indexOf(".");
		if (posOfDot==-1) return true;
		if (posOfDot==0 || posOfDot>=str.length()-1)
			return false;
		else return true;
	}*/
	
	/** 16진수가 아니면 0을 리턴, 16진수 char이면 1, 16진수 short이면 2,
	 * 16진수 integer이면 3을 리턴, 16진수 long이면 4를, 
	 * 16진수 float이면 5를, 16진수 double이면 6를 리턴한다.
	 * 16진수 byte이면 7을 리턴한다.*/
	public static int IsHexa(CodeString str) {
		return IsHexa(str.str);
	}
	
	
	/** 16진수가 아니면 0을 리턴, 16진수 char이면 1, 16진수 short이면 2,
	 * 16진수 integer이면 3을 리턴, 16진수 long이면 4를, 
	 * 16진수 float이면 5를, 16진수 double이면 6를 리턴한다.
	 * 16진수 byte이면 7을 리턴한다.*/
	public static int IsHexa(String str) {
		int i;
		char ch1, ch2, ch;
		int lenOfStr = str.length();
		
		if (lenOfStr>2) {
			// 첫번째 문자
			ch1 = str.charAt(0);
			ch2 = str.charAt(1);
			// 16진수인 경우 +, -의 부호가 들어가서는 안된다. 반드시 0x로 시작해야 한다.
			if ((ch1=='0' && (ch2=='x' || ch2=='X'))==false) return 0;
			
			/*for (i=2; i<lenOfStr-1; i++) {
				ch = str.charAt(i).c;
				if ( (('0'<=ch && ch<='9') || 
					 ('a'<=ch && ch<='f') || ('A'<=ch && ch<='F'))==false )
					return false;
			}*/
			
			
			// 중간들
			boolean isDotFound = false;
			// 0x.0011
			if (str.charAt(2)=='.') return 0;
			for (i=2; i<lenOfStr-1; i++) {			
				ch = str.charAt(i);
				if (isDotFound && ch=='.') return 0; // 소수점이 두개 이상인경우
				if (ch=='.') {					
					isDotFound = true;
				}
				if (ch=='-' || ch=='+') return 0;
				if ( (('0'<=ch && ch<='9') || 
						 ('a'<=ch && ch<='f') || ('A'<=ch && ch<='F'))==false ) {					
					return 0;
				}
			}
			
			// 마지막 문자
			/*ch = str.charAt(lenOfStr-1).c;
			if (str.str.contains(".")) { // 실수
				if ((('0'<=ch && ch<='9') || ch=='f' || ch=='d')==false) {
					return true;
				}
			}
			else { // 정수
				if ((('0'<=ch && ch<='9') || (ch=='c' || ch=='s' || ch=='i' || ch=='l'))==false) {
					return true;
				}
			}*/
			
			// 마지막 문자
			ch = str.charAt(lenOfStr-1);
			if (str.contains(".")) { // 실수
				// 16진수의 경우 접미사 f는 사용할수 없다.
				if ( (('0'<=ch && ch<='9') || (('a'<=ch && ch<='f') || ('A'<=ch && ch<='F')) || 
						(ch=='d' || ch=='D'))==false ) {
					return 0;
				}
			}
			else { // 정수
				if ( (('0'<=ch && ch<='9')  || (('a'<=ch && ch<='f') || ('A'<=ch && ch<='F')) ||
					(ch=='c' || ch=='s' || ch=='i' || ch=='l' || ch=='C' || ch=='S' || ch=='I' || ch=='L'))==false ) {
					return 0;
				}
			}
		}
		
		else {
			return 0;
		}
		
		int posOfDot = str.indexOf(".");
		if (posOfDot==-1) {// 정수
			if (ch=='c' || ch=='C') return 1;//char
			else if (ch=='s' || ch=='S') return 2;//short
			else if (ch=='i' || ch=='I') return 3;//int
			else if (ch=='l' || ch=='L') return 4;//long
			//else return 3; //접미사가 없으면 int
			else { // 접미사가 없으면
				//int integer = Integer.parseInt(str.str);
				/*try {
					integerLong = IO.hexaToLong(str.str);
					if (Byte.MIN_VALUE<=integerLong && integerLong<Byte.MAX_VALUE) return 7;
					else if (Short.MIN_VALUE<=integerLong && integerLong<Short.MAX_VALUE) return 2;
					else if (Integer.MIN_VALUE<=integerLong && integerLong<Integer.MAX_VALUE) return 3;
					else return 4; // long
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				}*/
				return IO.isNumber2OfHexa(str);
				
			}
		}
		if (posOfDot==0 || posOfDot>=str.length()-1)
			return 0;
		else { //실수
			//if (ch=='f') return 5;//float
			//else 
			// 16진수의 경우 접미사 f는 사용할수 없다.
			if (ch=='d' || ch=='D') return 6;//double
				
			// 소수점은 있으나 접미사가 없으면
			float f = Float.parseFloat(str);
			if (Float.MIN_VALUE<=f && f<Float.MAX_VALUE) return 5;
			return 6;
		}
	}
	
	/** 0이 아니면 not number을 리턴, 1이면 char를, 2이면 short를,
	 * 3이면 integer를 리턴, 4이면 long을, 5이면 float를, 6이면 double을 리턴한다. 
	 * 7이면 byte를 리턴한다.*/
	public static String getNumberString(int numberType) {
		switch(numberType) {
		case 0 : return "not number";
		case 1 : return "char";
		case 2 : return "short";
		case 3 : return "integer";
		case 4 : return "long";
		case 5 : return "float";
		case 6 : return "double";
		case 7 : return "byte";
		}
		return null;
	}
	
	/** str이 숫자이면 'f', 'd', 'l"과 같은 접미사를 제거한 스트링을 리턴한다.*/
	public static String getNumberRemovingLastFix(String str) {		
		if (str.length()>0) {
			char chLast = str.charAt(str.length()-1);
			if (('A'<=chLast && chLast<='Z') || ('a'<=chLast && chLast<='z')) {
				int r = IsNumber2(str);
				if (r!=0) {
					String result = str.substring(0, str.length()-1);
					return result;
				}
			}
		}
		return str;
	}
	
	
	/** 숫자가 아니면 0을 리턴, char이면 1, short이면 2,
	 * integer이면 3을 리턴, long이면 4를, float이면 5를, double이면 6를 리턴한다. 
	 * byte이면 7을 리턴한다.
	 * 16진수인 경우 CompilerHelper.IsHexa(str)을 참조한다.*/
	public static int IsNumber2(CodeString str) {
		return IsNumber2(str.str);
	}
	
	

	
	/** 숫자가 아니면 0을 리턴, char이면 1, short이면 2,
	 * integer이면 3을 리턴, long이면 4를, float이면 5를, double이면 6를 리턴한다. 
	 * byte이면 7을 리턴한다.
	 * 16진수인 경우 CompilerHelper.IsHexa(str)을 참조한다.*/
	public static int IsNumber2(String str) {
		if (str.equals("127")) {
			int a;
			a=0;
			a++;
		}
		// 16진수인 경우 
		int hexa = CompilerHelper.IsHexa(str);
		if (hexa!=0) return hexa;
		
		int i;
		char ch;
		int lenOfStr = str.length();
		if (lenOfStr>1) {
			// 첫번째 문자
			ch = str.charAt(0);
			if ((('0'<=ch && ch<='9') || ch=='-' || ch=='+')==false) {
				return 0;
			}
			
			// 중간들
			boolean isDotFound = false;
			// -.3f(x), +.2f(x)
			if (str.charAt(1)=='.' && 
					(str.charAt(0)=='-' || str.charAt(0)=='+') ) return 0;
			for (i=1; i<lenOfStr-1; i++) {			
				ch = str.charAt(i);
				if (isDotFound && ch=='.') return 0; // 소수점이 두개 이상인경우
				if (ch=='.') {					
					isDotFound = true;
				}
				if (ch=='-' || ch=='+') return 0;
				if ((('0'<=ch && ch<='9') || ch=='.')==false) {					
					return 0;
				}
			}
		
			// 마지막 문자
			ch = str.charAt(lenOfStr-1);
			if (str.contains(".")) { // 실수
				if ( (('0'<=ch && ch<='9') || 
						(ch=='f' || ch=='d' || ch=='F' || ch=='D') )==false) {
					return 0;
				}
			}
			else { // 정수
				if ( (('0'<=ch && ch<='9') || 
					(ch=='c' || ch=='s' || ch=='i' || ch=='l' || ch=='C' || ch=='S' || ch=='I' || ch=='L'))==false ) {
					return 0;
				}
			}
		}
		else if (lenOfStr==1) {
			ch = str.charAt(0);
			if (('0'<=ch && ch<='9')==false) return 0;
			else return 7; // byte
		}
		else {
			return 0;
		}
		
				
		int posOfDot = str.indexOf(".");
		if (posOfDot==-1) {// 정수
			if (ch=='c' || ch=='C') return 1;//char
			else if (ch=='s' || ch=='S') return 2;//short
			else if (ch=='i' || ch=='I') return 3;//int
			else if (ch=='l' || ch=='L') return 4;//long
			else { // 접미사가 없으면
				int integer = Integer.parseInt(str);
				if (IO.BYTE_MIN_VALUE<=integer && integer<=IO.BYTE_MAX_VALUE) return 7;
				else if (IO.SHORT_MIN_VALUE<=integer && integer<=IO.SHORT_MAX_VALUE) return 2;
				else if (IO.INTEGER_MIN_VALUE<=integer && integer<=IO.INTEGER_MAX_VALUE) return 3;
				else return 4; // long
			}
		}
		if (posOfDot==0 || posOfDot>=str.length()-1)
			return 0;
		else { //실수
			if (ch=='f' || ch=='F') return 5;//float
			else if (ch=='d' || ch=='D') return 6;//double
			
			// 소수점은 있으나 접미사가 없으면
			float f = Float.parseFloat(str);
			if (Float.MIN_VALUE<=f && f<Float.MAX_VALUE) return 5;
			else return 6; // double
		}
	}

	
	public static boolean IsBlank(char c)
    {
        if (c == ' ' || c == '\t' || c == '\r' || c == '\n') return true;
        return false;
    }

	/** " ", "\t", "\r", "\n" */
    public static boolean IsBlank(CodeString c)
    {
        if (c.equals(" ") || c.equals("\t") || c.equals("\r") || c.equals("\n")) return true;
        return false;
    }
    
    /** 공백을 포함한 구분자*/
    public static boolean IsSeparator(char c)
    {    	
        if (c == '[' || c == ']' || c == '{' || c == '}' || c == '(' || c == ')' ||
            c == '=' || c == ';' || c == ':' || c == ',' || c == '.' || c == '@' ||
            c == '\\')
            return true;
        if (IsOperator(c)) return true;
        if (CompilerHelper.IsBlank(c)) return true;
        return false;
    }

    /** 공백을 포함한 구분자*/
    public static boolean IsSeparator(CodeString c)
    {
    	//if (IsSeparator(c.charAt(0).c)) return true;
        if (c.equals("[") || c.equals("]") || c.equals("{") || c.equals("}") || c.equals("(") || c.equals(")") ||
           c.equals("=") || c.equals(";") || c.equals(":") || c.equals(",") || c.equals(".") || c.equals("@") ||
           c.equals("\\"))
            return true;
        if (IsOperator(c)) return true;
        if (CompilerHelper.IsBlank(c)) return true;
        return false;
    }
    
    public static boolean isInteger(String typeName) {
    	if (typeName.equals("boolean") || typeName.equals("byte") || typeName.equals("char") ||
				typeName.equals("short") || typeName.equals("int"))
    		return true;
    	return false;
    }
    
    /** c == '+' || c == '-' || c == '*' || c == '/' || c == '<' || c == '>' || c == '~' ||
        c == '|' || c == '&' || c == '!' || c == '%' || c == '=' || c == '^'
            참고로 '='는 주의한다. "=="*/
    public static boolean IsOperator(char c)
    {
        if (c == '+' || c == '-' || c == '*' || c == '/' || c == '<' || c == '>' || c == '~' ||
            c == '|' || c == '&' || c == '!' || c == '%' || c == '=' || c == '^' || 
            c == '?' || c == ':') return true;
        return false;
    }
    
    /** 길이가 오직 1만 허용되는 연산자들, c.equals("~") || c.equals("!") || c.equals("^") || 
	        c.equals("?") || c.equals(":") || c.equals("instanceof")*/
    public static boolean IsOperatorLen1(CodeString c) {
    	if (c.equals("~") || c.equals("!") || c.equals("^") || 
	        c.equals("?") || c.equals(":") || c.equals("instanceof")) return true;
    	return false;
    }
    
    public static boolean IsCompositiveOperator(CodeString op1, CodeString op2, CodeString op3) {
    	if (op1.equals("<") || op2.equals("<") || op3.equals("<")) {
    		return true;
    	}
    	if (op1.equals(">") || op2.equals(">") || op3.equals(">")) {
    		return true;
    	}
    	return false;
    }
    
    public static boolean IsCompositiveOperator(CodeString op1, CodeString op2) {
    	if (op2.equals("=")) {
    		// 사칙연산
    		if (op1.equals("+") || op1.equals("-") || op1.equals("*") || op1.equals("/") ||
    			op1.equals("%") || 
    			op1.equals("|") || op1.equals("&") || op1.equals("^") || op1.equals("~")) {
        		return true;
        	}
    		// 관계연산
    		if (op1.equals("!") || op1.equals("=") || op1.equals("<") || op1.equals(">"))
    			return true;
    		return false;
    	}
    	if (op1.equals("<")) {
    		// shift연산
    		if (op2.equals("<")) return true;
    		// 관계연산
    		if (op2.equals("=")) return true;
    		return false;
    	}
    	else if (op1.equals(">")) {
    		// shift연산
    		if (op2.equals(">")) return true;
    		// 관계연산
    		if (op2.equals("=")) return true;
    		return false;
    	}
    	else if (op1.equals("&")) {
    		// 논리연산
    		if (op2.equals("&")) return true;
    		return false;
    	}
    	else if (op1.equals("|")) {
    		// 논리연산
    		if (op2.equals("|")) return true;
    		return false;
    	}
    	return false;
    }
    
    /** >, <, ==, !=, >=, <=*/
    public static boolean IsRelationOperator(String str) {
    	if (str.equals(">") || str.equals("<") || str.equals("==") || str.equals("!=") ||
    			str.equals(">=") || str.equals("<="))
    		return true;
    	return false;
    }

    /** c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("<") || c.equals(">") || c.equals("~") ||
        c.equals("|") || c.equals("&") || c.equals("!") || c.equals("%") || c.equals("=") || c.equals("^")
            참고로 '='는 주의한다. "=="*/
    public static boolean IsOperator(CodeString c)
    {
    	/*int len = c.length();
    	if (len==2) {
	    	if (IsOperator(c.charAt(0).c)) {
	    		if (IsOperator(c.charAt(1).c)) {
	    			return true; // "<=" 이런경우 "<"이 연산자이므로 true
	    		}
	    		else return false; //-1이면 연산자가 아니다.
	    	}
	    	else return false;
    	}
    	else if (len==3) {
	    	if (IsOperator(c.charAt(0).c)) {
	    		if (IsOperator(c.charAt(1).c)) {
	    			if (IsOperator(c.charAt(2).c)) {
	    				return true; // "<=" 이런경우 "<"이 연산자이므로 true
	    			}
	    			else return false;
	    		}
	    		else return false;
	    	}
	    	else return false;
    	}
    	else if (len==1) {
	        if (c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("<") || c.equals(">") || c.equals("~") ||
	            c.equals("|") || c.equals("&") || c.equals("!") || c.equals("%") || c.equals("=") || c.equals("^") || 
	            c.equals("?") || c.equals(":")) return true;
    	}
    	else {
    		if (c.equals("instanceof")) return true;
    		return false;
    	}
    	
        return false;*/
    	
    	return IsOperator(c.str);
    }
    
    /** c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("<") || c.equals(">") || c.equals("~") ||
    c.equals("|") || c.equals("&") || c.equals("!") || c.equals("%") || c.equals("=") || c.equals("^")
        참고로 '='는 주의한다. "=="*/
public static boolean IsOperator(String c)
{
	int len = c.length();
	if (len==2) {
    	if (IsOperator(c.charAt(0))) {
    		if (IsOperator(c.charAt(1))) {
    			return true; // "<=" 이런경우 "<"이 연산자이므로 true
    		}
    		else return false; //-1이면 연산자가 아니다.
    	}
    	else return false;
	}
	else if (len==3) {
    	if (IsOperator(c.charAt(0))) {
    		if (IsOperator(c.charAt(1))) {
    			if (IsOperator(c.charAt(2))) {
    				return true; // "<=" 이런경우 "<"이 연산자이므로 true
    			}
    			else return false;
    		}
    		else return false;
    	}
    	else return false;
	}
	else if (len==1) {
        if (c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("<") || c.equals(">") || c.equals("~") ||
            c.equals("|") || c.equals("&") || c.equals("!") || c.equals("%") || c.equals("=") || c.equals("^") || 
            c.equals("?") || c.equals(":")) return true;
	}
	else {
		if (c.equals("instanceof")) return true;
		return false;
	}
	
    return false;
}
    
    /**c.equals("!") || c.equals("~")*/
    public static boolean IsOperatorForOne(CodeString c)
    {
        if (c.equals("!") || c.equals("~")) return true;
        return false;
    }
    
    /**c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || 
            c.equals("|") || c.equals("&") || c.equals("%")  || c.equals("^") 
            뒤에 "="이 붙는다. 참고로 <는 "<<="을 말하고 >는 ">>="을 말하고 ^는 "^="(거듭제곱)을 말한다.
            IsLValue()를 참고한다.*/
    public static boolean IsAssignOperator(CodeString c)
    {
        if (c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || 
            c.equals("|") || c.equals("&") || c.equals("%")  || c.equals("^")) return true;
        return false;
    }
    
    /** +=, -=, *=, /=, |=, &=, %=, ^=, <<=, >>= 을 말한다.
     * "+="는 op1이 +이고, op2는 =이 된다. op3는 null일 수 있다.*/
    public static boolean IsAssignOperator(CodeString op1, CodeString op2, CodeString op3) {
    	if (IsAssignOperator(op1) && op2.equals("=")) {
    		return true;
    	}
    	if (op1.equals("<") && op2.equals("<") && op3.equals("="))
    		return true;
    	if (op1.equals(">") && op2.equals(">") && op3.equals("="))
    		return true;
    	return false;
    }
    
    
    /** 1  2.5f  +  3  +   --> 첫번째 +를 만날시 float가 getTypeOfOperator에서 리턴되고,
	listOfTypes에는 첫두개의 오퍼랜드가 삭제되어 float가 저장된다. 
	3을 만나면 float  int이 되고,
	두번째 +를 만나면 마지막이 float int이기 때문에 float가 getTypeOfOperator에서 리턴되고,
	최종적으로 listOfTypes에는 두개의 오퍼랜드가 삭제되어 빈상태가 된다.
	마지막 float가 수식의 타입으로 리턴된다.
	@param listOfTypes : 오퍼랜드만 저장된 스택이다.오퍼레이터는 여기에 없다.*/
public static 
	CodeStringEx getTypeOfOperator(Compiler compiler, FindFuncCallParam funcCall, ArrayListIReset listOfTypes, CodeStringEx operator) {
		int startIndex = funcCall.startIndex();
		int endIndex = funcCall.endIndex();
		int i;
		
		if (operator!=null && operator.indicesInSrc!=null && 
				operator.indicesInSrc.count>0 && operator.indicesInSrc.list[0]==5384) {
			int a;
			a=0;
			a++;
		}
		
		// 일단 이렇게 한다. 연산자가 들어있는 수식의 타입처리
		CodeStringEx oldType = null, curType;
		
		if (2471<=startIndex && endIndex<=2494) {
			int a;
			a=0;
			a++;
		}
		
		try{
		if (listOfTypes.count==1) {
			if (listOfTypes.getItem(listOfTypes.count-1)==null) {
				listOfTypes.count -= 1;
				return new CodeStringEx("null");
			}
			if (listOfTypes.getItem(listOfTypes.count-1).equals("null")) {
				listOfTypes.count -= 1;
				return new CodeStringEx("null");
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
			int a;
			a=0;
			a++;
		}
		
		// 단항연산자, int i4 = -((-1)+2) + (~(-1)+(~1)); 
		// 여기에서 -((-1)+2)의 -를 말한다. -1은 어휘분석기에서 하나의 스트링으로 합쳐진다.
		if ((operator.equals("+")  && operator.isPlusOrMinusForOne) || 
			(operator.equals("-") && operator.isPlusOrMinusForOne)) {
			if (listOfTypes.count>0) {
				oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-1);				
				if (oldType==null) {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+"null"));
					listOfTypes.count -= 1;
					return new CodeStringEx("null");
				}
				oldType.operandOrOperator.affectedBy_left = operator;
				oldType.operandOrOperator.affectedBy = operator;
				operator.affectsLeft = oldType.operandOrOperator;
				if (oldType.equals("byte")) {
					allowsOperator(oldType, null, operator);					
				}
				else if (oldType.equals("char") || oldType.equals("int") || oldType.equals("short") ||
						oldType.equals("long")) {
					allowsOperator(oldType, null, operator);
				}
				else if (oldType.equals("float") || oldType.equals("double")) {
					allowsOperator(oldType, null, operator);
				}
				else {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType));
					listOfTypes.count -= 1;
					return new CodeStringEx("null");
				}
				// 스택에서 마지막 1개의 오퍼랜드를 빼낸다.
				listOfTypes.count -= 1;
				
				// oldType을 operator의 타입으로 바꾼다.
				if (operator.value!=null) {
					oldType.setStrAndTypeFullName(operator.typeFullName, operator.typeFullName);
				}
				return oldType;
			}
		}
		
		if (operator.equals("?")) {
			if (listOfTypes.count>0) {
				oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-1);
				if (oldType==null) {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+"null"));
					listOfTypes.count -= 1;
					return new CodeStringEx("null");
				}
				oldType.operandOrOperator.affectedBy_left = operator;
				oldType.operandOrOperator.affectedBy = operator;
				operator.affectsLeft = oldType.operandOrOperator;
				if (oldType.equals("boolean")) {
				}
				else {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType));
					listOfTypes.count -= 1;
					return new CodeStringEx("null");
				}
				// 스택에서 마지막 1개의 오퍼랜드를 빼낸다.
				listOfTypes.count -= 1;
				
				// oldType을 operator의 타입으로 바꾼다.
				if (operator.value!=null) {
					oldType.setStrAndTypeFullName(operator.typeFullName, operator.typeFullName);
				}
				return oldType;
			}
		}
		
		else if (operator.equals("!")) {
			if (listOfTypes.count>0) {
				oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-1);
				if (oldType==null) {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+"null"));
					listOfTypes.count -= 1;
					return new CodeStringEx("null");
				}
				oldType.operandOrOperator.affectedBy_left = operator;
				oldType.operandOrOperator.affectedBy = operator;
				operator.affectsLeft = oldType.operandOrOperator;
				if (oldType.equals("boolean")) {
				}
				else {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType));
					listOfTypes.count -= 1;
					return new CodeStringEx("null");
				}
				// 스택에서 마지막 1개의 오퍼랜드를 빼낸다.
				listOfTypes.count -= 1;
				
				// oldType을 operator의 타입으로 바꾼다.
				if (operator.value!=null) {
					oldType.setStrAndTypeFullName(operator.typeFullName, operator.typeFullName);
				}
				return oldType;
			}
		}
		
		else if (operator.equals("~")) {
			if (listOfTypes.count>0) {
				oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-1);
				if (oldType==null) {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+"null"));
					listOfTypes.count -= 1;
					return new CodeStringEx("null");
				}
				oldType.operandOrOperator.affectedBy_left = operator;
				oldType.operandOrOperator.affectedBy = operator;
				operator.affectsLeft = oldType.operandOrOperator;
				if (oldType.equals("byte") || oldType.equals("char") || oldType.equals("int") || oldType.equals("short") ||
					oldType.equals("long")) {
					allowsOperator(oldType, null, operator);
				}
				else { // float, double등은 에러
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType));
					listOfTypes.count -= 1;
					return new CodeStringEx("null");
				}
				// 스택에서 마지막 1개의 오퍼랜드를 빼낸다.
				listOfTypes.count -= 1;
				
				// oldType을 operator의 타입으로 바꾼다.
				if (operator.value!=null) {
					oldType.setStrAndTypeFullName(operator.typeFullName, operator.typeFullName);
				}
				return oldType;
			}
		}
		
		if (operator.equals(":")) {
			if (listOfTypes.count>1) {
				oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-2);
				if (oldType==null) {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+"null"));
					listOfTypes.count -= 2;
					return new CodeStringEx("null");
				}
				oldType.operandOrOperator.affectedBy_left = operator;
				oldType.operandOrOperator.affectedBy = operator;
				operator.affectsLeft = oldType.operandOrOperator;
				
				for (i=listOfTypes.count-1; i<listOfTypes.count; i++) {					
					curType = (CodeStringEx) listOfTypes.getItem(i);
					if (curType==null) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, 
								"invalid expression : ("+operator.str+") "+oldType+", "+"null"));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					curType.operandOrOperator.affectedBy_right = operator;
					curType.operandOrOperator.affectedBy = operator;
					operator.affectsRight = curType.operandOrOperator;
					if (oldType.equals("byte")) {
						if (curType.equals("byte") || curType.equals("char") || 
							curType.equals("short") || curType.equals("int") || curType.equals("long") || 
							curType.equals("float") || curType.equals("double")) {
							// oldType을 curType으로 바꾼다.
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("char") || oldType.equals("short")) {
						if (curType.equals("byte")) {
							
						}
						else if (curType.equals("char") || 
							curType.equals("short") || curType.equals("int") || curType.equals("long") || 
							curType.equals("float") || curType.equals("double")) {
							// oldType을 curType으로 바꾼다.
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("int")) {
						if (curType.equals("byte") || curType.equals("char") || curType.equals("short")) {
							
						}
						else if (curType.equals("int") || curType.equals("long") || 
							curType.equals("float") || curType.equals("double")) {
							// oldType을 curType으로 바꾼다.
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("long")) {
						if (curType.equals("byte") || curType.equals("char") ||
							curType.equals("short") || curType.equals("int")) {
							
						}
						else if (curType.equals("long") || 
							curType.equals("float") || curType.equals("double")) {
							// oldType을 curType으로 바꾼다.
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("float")) {
						if (curType.equals("byte") || curType.equals("char") ||
							curType.equals("short") || curType.equals("int")) {
							
						}
						else if (curType.equals("long")) {
							
						}
						else if (curType.equals("float") || curType.equals("double")) {
							// oldType을 curType으로 바꾼다.
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("double")) {
						if (curType.equals("byte") || curType.equals("char") ||
							curType.equals("short") || curType.equals("int") || curType.equals("long") ||
							curType.equals("float") || curType.equals("double")) {
							
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else {
						if (TypeCast.isCompatibleType(compiler, oldType, curType, 0, null)==false) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							
						}
					}
				}//for
				// 스택에서 마지막 3개의 오퍼랜드를 빼낸다.
				listOfTypes.count -= 3;
				
				// oldType을 operator의 타입으로 바꾼다.
				if (operator.value!=null) {
					oldType.setStrAndTypeFullName(operator.typeFullName, operator.typeFullName);
				}
				return oldType;
			}//if (listOfTypes.count>1) {
		}//if (operator.equals(":")) {
		
		// +, -, *, /, %, 는 float-float가 가능하다.boolean-boolean불가능
		else if ((operator.equals("+") && operator.isPlusOrMinusForOne==false) || 
			(operator.equals("-") && operator.isPlusOrMinusForOne==false) || 
			operator.equals("*") || operator.equals("/") ||	operator.equals("%")) {
			if (listOfTypes.count>1) {
				oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-2);
				if (oldType==null) {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+"null"));
					listOfTypes.count -= 2;
					return new CodeStringEx("null");
				}
				oldType.operandOrOperator.affectedBy_left = operator;
				oldType.operandOrOperator.affectedBy = operator;
				operator.affectsLeft = oldType.operandOrOperator;
				for (i=listOfTypes.count-1; i<listOfTypes.count; i++) {
					curType = (CodeStringEx) listOfTypes.getItem(i);
					if (curType==null) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, 
								"invalid expression : ("+operator.str+") "+oldType+", "+"null"));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					curType.operandOrOperator.affectedBy_right = operator;
					curType.operandOrOperator.affectedBy = operator;
					operator.affectsRight = curType.operandOrOperator;
					if (oldType.equals("boolean")) {
						if (curType.equals("boolean")) {							
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							if (operator.equals("+")) {//'+'연산만 가능
								Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
								listOfTypes.count -= 2;
								return new CodeStringEx("null");
							}
							else {
								Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
								listOfTypes.count -= 2;
								return new CodeStringEx("null");
							}
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("void")) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					else if (oldType.equals("byte")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte")) {
							allowsOperator(oldType, curType, operator);
						}
						else if (curType.equals("int")) {
							allowsOperator(oldType, curType, operator);
							// oldType을 curType으로 바꾼다.
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
						}
						else if (curType.equals("short")) {
							allowsOperator(oldType, curType, operator);
							// oldType을 curType으로 바꾼다.
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
						}
						else if (curType.equals("long")) {
							// 이전 오퍼랜드는 long으로 바뀐다.
							allowsOperator(oldType, curType, operator);
							// oldType을 curType으로 바꾼다.
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
						}
						else if (curType.equals("float") || curType.equals("double")) {
							// 이전 오퍼랜드는 float나 double으로 바뀐다.
							allowsOperator(oldType, curType, operator);
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
						}
						else if (curType.equals("char")) {
							// 현재 오퍼랜드는 int로 바뀐다.
							allowsOperator(oldType, curType, operator);
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
						}
						else if (curType.equals("java.lang.String")) {
							// 이전 오퍼랜드는 String으로 바뀐다.
							if (operator.equals("+")) {
								oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							}
							else {
								Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
								listOfTypes.count -= 2;
								return new CodeStringEx("null");
							}
							//return curType;
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("byte")) {
					else if (oldType.equals("int") || oldType.equals("short") || oldType.equals("long")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte")) {
							allowsOperator(oldType, curType, operator);
							curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
						}
						else if (curType.equals("int")) {
							boolean b = allowsOperator(oldType, curType, operator);
							if (oldType.equals("short")) {
								oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
								oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
							}
							else if (oldType.equals("long")) {
								// curType이 oldType으로 바뀐다.
								curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
							}
						}
						else if (curType.equals("short")) {
							// 현재 오퍼랜드는 int로 바뀐다.
							allowsOperator(oldType, curType, operator);
							if (oldType.equals("int")) {
								curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
							}
							else if (oldType.equals("long")) {
								curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
							}
						}
						else if (curType.equals("long")) {
							// 이전 오퍼랜드는 long으로 바뀐다.
							allowsOperator(oldType, curType, operator);
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
						}
						else if (curType.equals("float") || curType.equals("double")) {
							// 이전 오퍼랜드는 float나 double으로 바뀐다.					
							allowsOperator(oldType, curType, operator);
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
						}
						else if (curType.equals("char")) {
							// 현재 오퍼랜드는 int로 바뀐다.
							allowsOperator(oldType, curType, operator);
							curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
						}
						else if (curType.equals("java.lang.String")) {
							if (operator.equals("+")) {
								oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							}
							else {
								Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
								listOfTypes.count -= 2;
								return new CodeStringEx("null");
							}
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("int")) {
					else if (oldType.equals("float")) {
						if (operator.indicesInSrc!=null && operator.indicesInSrc.getItem(0)==1970) {
							int a;
							a=0;
							a++;
						}
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte") || curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							// 현재 오퍼랜드는 float로 바뀐다.
							allowsOperator(oldType, curType, operator);
							curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
						}
						else if (curType.equals("float")) {
							allowsOperator(oldType, curType, operator);
						}
						else if (curType.equals("double")) {
							// 이전 오퍼랜드는 double으로 바뀐다.
							allowsOperator(oldType, curType, operator);
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
						}
						else if (curType.equals("char")) {
							allowsOperator(oldType, curType, operator);
							curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
						}
						else if (curType.equals("java.lang.String")) {
							if (operator.equals("+")) {
								oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							}
							else {
								Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
								listOfTypes.count -= 2;
								return new CodeStringEx("null");
							}
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("float")) {
					else if (oldType.equals("double")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte") || curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							// 현재 오퍼랜드는 double로 바뀐다.
							allowsOperator(oldType, curType, operator);
							curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
						}
						else if (curType.equals("float")) {
							// 현재 오퍼랜드는 double로 바뀐다.
							//FindVarUseParams v = (FindVarUseParams) listOfVarUses.list[i];
							//v.fullnameTypeCastByOperator = oldType;
							allowsOperator(oldType, curType, operator);
							curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
						}
						else if (curType.equals("double")) {
							allowsOperator(oldType, curType, operator);
						}
						else if (curType.equals("char")) {
							allowsOperator(oldType, curType, operator);
							curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
						}
						else if (curType.equals("java.lang.String")) {
							if (operator.equals("+")) {
								oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							}
							else {
								Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
								listOfTypes.count -= 2;
								return new CodeStringEx("null");
							}
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("double")) {
					else if (oldType.equals("char")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte")) {
							allowsOperator(oldType, curType, operator);
							curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							// 이전 오퍼랜드는 현재 타입으로 바뀐다.
							//FindVarUseParams v = (FindVarUseParams) listOfVarUses.list[i-1];
							//v.fullnameTypeCastByOperator = curType;
							allowsOperator(oldType, curType, operator);
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;							
						}
						else if (curType.equals("float") || curType.equals("double")) {
							// 이전 오퍼랜드는 현재 타입으로 바뀐다.
							allowsOperator(oldType, curType, operator);
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
						}
						else if (curType.equals("char")) {
							allowsOperator(oldType, curType, operator);
						}
						else if (curType.equals("java.lang.String")) {
							if (operator.equals("+")) {
								oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							}
							else {
								Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
								listOfTypes.count -= 2;
								return new CodeStringEx("null");
							}
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("char")) {
					else if (oldType.equals("java.lang.String")) {
						// 현재 오퍼랜드는 String으로 바뀐다.
						if (operator.equals("+")) {
							if (compiler.IsDefaultType(curType)) {
								curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
								listOfTypes.count -= 2;
								return oldType;
							}
							else if (curType.equals("java.lang.String")) {
								listOfTypes.count -= 2;
								return oldType;
							}
							else {
								// curType이 String이 아닌 다른 object일 경우
								CodeStringEx fullnameTypeCast1 = new CodeStringEx("java.lang.String");
								CodeStringEx fullnameTarget1 = curType;
								boolean isCompatible1 = TypeCast.isCompatibleType(compiler, fullnameTypeCast1, fullnameTarget1, 1, null);
								if (isCompatible1) {
									// curType이 java.lang.String의 서브클래스일 경우
									//curType.typeFullNameAfterOperation = oldType.str;
									listOfTypes.count -= 2;
									return oldType;
								}
								else {
									// curType의 toString()함수가 호출되어야 한다.
									if (toStringFuncExists(compiler, curType.str)) {
										//curType.typeFullNameAfterOperation = oldType.str;
										listOfTypes.count -= 2;
										return oldType;								
									}
									// curType의 toString()함수가 없으면 error를 발생시킨다.
									Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
									listOfTypes.count -= 2;
									return new CodeStringEx("null");
								}
							}
						}//+이면
						else { // +가 아니면
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("java.lang.String")) {
					else {//oldType은 java.lang.String이 아닌 object이다.
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("float") || curType.equals("double")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("char")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							//return curType;
							CodeStringEx fullnameTypeCast1 = new CodeStringEx("java.lang.String");
							CodeStringEx fullnameTarget1 = oldType;
							boolean isCompatible1 = TypeCast.isCompatibleType(compiler, fullnameTypeCast1, fullnameTarget1, 1, null);
							if (isCompatible1) {
								// oldType이 String의 서브 클래스일 경우
								if (operator.equals("+")) {
									listOfTypes.count -= 2;
									return curType;
								}
								else {
									Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
									listOfTypes.count -= 2;
									return new CodeStringEx("null");		
								}
							}
							else {
								// oldType의 toString()함수가 호출되어야 한다.
								if (toStringFuncExists(compiler, oldType.str)) {
									//curType.typeFullNameAfterOperation = oldType.str;
									if (operator.equals("+")) {
										listOfTypes.count -= 2;
										return curType;
									}
									else {
										Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
										listOfTypes.count -= 2;
										return new CodeStringEx("null");
									}
								}
								// oldType이 toString()이 없으면								
								Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
								listOfTypes.count -= 2;
								return new CodeStringEx("null");
							}
						}//else if (curType.equals("java.lang.String")) {
						else { // oldType, curType 모두 java.lang.String이 아닌 경우
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}//// oldType, curType 모두 java.lang.String이 아닌 경우
					}//else {//oldType은 java.lang.String이 아닌 object이다.
				} // for (i=1; i<listOfTypes.count; i++) {
				
				// 스택에서 마지막 2개의 오퍼랜드를 빼낸다.
				listOfTypes.count -= 2;
				//listOfVarUses.count -= 2;
				
				// oldType을 operator의 타입으로 바꾼다.
				if (operator.value!=null) {
					oldType.setStrAndTypeFullName(operator.typeFullName, operator.typeFullName);
				}
				return oldType;
			} //if (listOfTypes.count==2) {
		} // if (operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/") || operator.equals("%") {
		
		// >, <, >=, <= 는 float-float가 가능하다.boolean-boolean불가능
		if (operator.equals(">") || operator.equals("<") || operator.equals(">=") || operator.equals("<=")) {
			if (listOfTypes.count>1) {
				oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-2);
				if (oldType==null) {
					listOfTypes.count -= 2;
					return new CodeStringEx("null");
				}
				oldType.operandOrOperator.affectedBy_left = operator;
				oldType.operandOrOperator.affectedBy = operator;
				operator.affectsLeft = oldType.operandOrOperator;
				for (i=listOfTypes.count-1; i<listOfTypes.count; i++) {
					curType = (CodeStringEx) listOfTypes.getItem(i);
					if (curType==null) {
						int a;
						a=0;
						a++;
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					curType.operandOrOperator.affectedBy_right = operator;
					curType.operandOrOperator.affectedBy = operator;
					operator.affectsRight = curType.operandOrOperator;
					if (oldType.equals("boolean")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("void")) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					else if (oldType.equals("byte") || oldType.equals("char") || oldType.equals("short")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte") || curType.equals("char") || curType.equals("short")) {
							oldType.operandOrOperator.typeFullNameAfterOperation = "int";
							curType.operandOrOperator.typeFullNameAfterOperation = "int";
							oldType.str = "boolean";
						}
						else if (curType.equals("int")) {
							//oldType = new CodeStringEx("boolean");
							oldType.operandOrOperator.typeFullNameAfterOperation = "int";
							oldType.str = "boolean";
						}
						else if (curType.equals("long")) {
							// 바이트코드에서 int 비교밖에 없으므로 oldType, curType 모두 int로 바꾼다.
							oldType.operandOrOperator.typeFullNameAfterOperation = "long";
							oldType.str = "boolean";
						}
						else if (curType.equals("float")) {
							// 바이트코드에서 int 비교밖에 없으므로 oldType, curType 모두 int로 바꾼다.
							oldType.operandOrOperator.typeFullNameAfterOperation = "float";
							oldType.str = "boolean";
						}
						else if (curType.equals("double")) {
							oldType.operandOrOperator.typeFullNameAfterOperation = "double";
							oldType.str = "boolean";
						}
						
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("byte") || oldType.equals("char") || oldType.equals("short")) {
					else if (oldType.equals("int")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte") || curType.equals("char") || curType.equals("short")) {
							// 바이트코드에서 int 비교밖에 없으므로 oldType, curType 모두 int로 바꾼다. 
							curType.operandOrOperator.typeFullNameAfterOperation = "int";
							oldType.str = "boolean";
						}
						else if (curType.equals("int")) {
							oldType.str = "boolean";
						}
						else if (curType.equals("long")) {
							oldType.operandOrOperator.typeFullNameAfterOperation = "long";
							oldType.str = "boolean";
						}
						else if (curType.equals("float")) {
							//oldType = new CodeStringEx("boolean");
							oldType.operandOrOperator.typeFullNameAfterOperation = "float";
							oldType.str = "boolean";
						}
						else if (curType.equals("double")) {
							oldType.operandOrOperator.typeFullNameAfterOperation = "double";
							oldType.str = "boolean";
						}
						
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("int")) {
					else if (oldType.equals("long")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte") || curType.equals("char") || curType.equals("short")) {
							// 바이트코드에서 int 비교밖에 없으므로 oldType, curType 모두 int로 바꾼다.
							curType.operandOrOperator.typeFullNameAfterOperation = "long";
							oldType.str = "boolean";
						}
						else if (curType.equals("int")) {
							curType.operandOrOperator.typeFullNameAfterOperation = "long";
							oldType.str = "boolean";
						}
						else if (curType.equals("long")) {
							oldType.str = "boolean";
						}
						else if (curType.equals("float")) {
							//oldType = new CodeStringEx("boolean");
							oldType.operandOrOperator.typeFullNameAfterOperation = "float";
							oldType.str = "boolean";
						}
						else if (curType.equals("double")) {
							oldType.operandOrOperator.typeFullNameAfterOperation = "double";
							oldType.str = "boolean";
						}
						
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("long")) {
					else if (oldType.equals("float")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte") || curType.equals("char")|| curType.equals("short")) {
							curType.operandOrOperator.typeFullNameAfterOperation = "float";
							oldType.str = "boolean";
						}
						else if (curType.equals("int")) {
							curType.operandOrOperator.typeFullNameAfterOperation = "float";
							oldType.str = "boolean";
						}
						else if (curType.equals("long")) {
							curType.operandOrOperator.typeFullNameAfterOperation = "float";
							oldType.str = "boolean";
							//oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("float")) {
							oldType.str = "boolean";
							//oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("double")) {
							oldType.operandOrOperator.typeFullNameAfterOperation = "double";
							oldType.str = "boolean";
							//oldType = new CodeStringEx("boolean");
						}
						
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("float")) {
					else if (oldType.equals("double")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte") || curType.equals("char")|| curType.equals("short")) {
							curType.operandOrOperator.typeFullNameAfterOperation = "double";
							oldType.str = "boolean";
						}
						else if (curType.equals("int")) {
							curType.operandOrOperator.typeFullNameAfterOperation = "double";
							oldType.str = "boolean";
						}
						else if (curType.equals("long")) {
							curType.operandOrOperator.typeFullNameAfterOperation = "double";
							oldType.str = "boolean";
						}
						else if (curType.equals("float")) {
							curType.operandOrOperator.typeFullNameAfterOperation = "double";
							oldType.str = "boolean";
							//oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("double")) {
							oldType.str = "boolean";
							//oldType = new CodeStringEx("boolean");
						}
						
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("double")) {
					
					else if (oldType.equals("java.lang.String")) {
						// 현재 오퍼랜드는 String으로 바뀐다.
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					else {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("float") || curType.equals("double")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("char")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
				} // for (i=1; i<listOfTypes.count; i++) {
				
				// 스택에서 마지막 2개의 오퍼랜드를 빼낸다.
				listOfTypes.count -= 2;
				//listOfVarUses.count -= 2;
				
				return oldType;
			} //if (listOfTypes.count==2) {
		} // if (operator.equals("<") || operator.equals(">") || operator.equals("<=") || operator.equals(">=")) {
		
		// ^, <<, >>, |, &, ~(정수만, 단항) 는 정수-정수만 가능하다.
		if (operator.equals("<<") || operator.equals(">>") || operator.equals("<<<") || operator.equals(">>>") ||
			operator.equals("&") || operator.equals("|") || operator.equals("^")) {
			if (operator.equals("|")) {
				int a;
				a=0;
				a++;
			}
			if (listOfTypes.count>1) {
				oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-2);
				if (oldType==null) {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+"null"));
					listOfTypes.count -= 2;
					return new CodeStringEx("null");
				}
				oldType.operandOrOperator.affectedBy_left = operator;
				oldType.operandOrOperator.affectedBy = operator;
				operator.affectsLeft = oldType.operandOrOperator;
				for (i=listOfTypes.count-1; i<listOfTypes.count; i++) {
					curType = (CodeStringEx) listOfTypes.getItem(i);
					if (curType==null) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, 
								"invalid expression : ("+operator.str+") "+oldType+", "+"null"));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					curType.operandOrOperator.affectedBy_right = operator;
					curType.operandOrOperator.affectedBy = operator;
					operator.affectsRight = curType.operandOrOperator;
					if (oldType.equals("boolean")) {
						if (curType.equals("boolean")) {							
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("void")) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					else if (oldType.equals("int") || oldType.equals("short") || oldType.equals("long")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte")) {
							allowsOperator(oldType, curType, operator);
							// curType이 oldType으로 바뀐다.
							curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
						}
						else if (curType.equals("int")) {
							allowsOperator(oldType, curType, operator);
							if (oldType.equals("short")) {
								// oldType이 curType으로 바뀐다.
								oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
								oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
							}
							else if (oldType.equals("long")) {
								// curType이 oldType으로 바뀐다.
								curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
							}
						}
						else if (curType.equals("short")) {
							// 현재 오퍼랜드는 int로 바뀐다.
							allowsOperator(oldType, curType, operator);
							if (oldType.equals("int") || oldType.equals("long")) {
								// curType이 oldType으로 바뀐다.
								curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
							}
						}
						else if (curType.equals("long")) {
							// 이전 오퍼랜드는 long으로 바뀐다.							
							allowsOperator(oldType, curType, operator);
							if (oldType.equals("long")==false) {
								oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
								oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
							}
						}
						else if (curType.equals("float") || curType.equals("double")) {
							// 이전 오퍼랜드는 float나 double으로 바뀐다.
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("char")) {
							// 현재 오퍼랜드는 int로 바뀐다.
							allowsOperator(oldType, curType, operator);
							curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
						}
						else if (curType.equals("java.lang.String")) {
							// 이전 오퍼랜드는 String으로 바뀐다.
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("int") || oldType.equals("short") || oldType.equals("long")) {
					else if (oldType.equals("float") || oldType.equals("float")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte") || curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							// 현재 오퍼랜드는 float로 바뀐다.
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("float")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("double")) {
							// 이전 오퍼랜드는 double으로 바뀐다.
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("char")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {							
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("float") || oldType.equals("float")) {
					
					else if (oldType.equals("byte") || oldType.equals("char")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							// 이전 오퍼랜드는 현재 타입으로 바뀐다.
							allowsOperator(oldType, curType, operator);
							oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
							oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
						}
						else if (curType.equals("float") || curType.equals("double")) {
							// 이전 오퍼랜드는 현재 타입으로 바뀐다.
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte")) {
							allowsOperator(oldType, curType, operator);
							if (oldType.equals("char")) {
								curType.operandOrOperator.typeFullNameAfterOperation = oldType.str;
							}
						}
						else if (curType.equals("char")) {
							allowsOperator(oldType, curType, operator);
							if (oldType.equals("byte")) {
								oldType.setStrAndTypeFullName(curType.str, curType.typeFullName);
								oldType.operandOrOperator.typeFullNameAfterOperation = curType.str;
							}
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("java.lang.String")) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					else {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("float") || curType.equals("double")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("char")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
				} // for (i=1; i<listOfTypes.count; i++) {
				
				// 스택에서 마지막 2개의 오퍼랜드를 빼낸다.
				listOfTypes.count -= 2;
				//listOfVarUses.count -= 2;
				
				// oldType을 operator의 타입으로 바꾼다.
				if (operator.value!=null) {
					oldType.setStrAndTypeFullName(operator.typeFullName, operator.typeFullName);
				}
				return oldType;
			} //if (listOfTypes.count==2) {
		}//if (operator.equals("^") || operator.equals("<<") || operator.equals(">>") || 
		//operator.equals("&") || operator.equals("|")) {
		
		// &&, ||, !(단항) float-float가 불가능하다.boolean-boolean만 가능하다.
		if (operator.equals("&&") || operator.equals("||")) {
			if (listOfTypes.count>1) {
				oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-2);
				if (oldType==null) {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+"null"));
					listOfTypes.count -= 2;
					return new CodeStringEx("null");
				}
				oldType.operandOrOperator.affectedBy_left = operator;
				oldType.operandOrOperator.affectedBy = operator;
				operator.affectsLeft = oldType.operandOrOperator;
				for (i=listOfTypes.count-1; i<listOfTypes.count; i++) {
					curType = (CodeStringEx) listOfTypes.getItem(i);
					if (curType==null) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, 
								"invalid expression : ("+operator.str+") "+oldType+", "+"null"));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					curType.operandOrOperator.affectedBy_right = operator;
					curType.operandOrOperator.affectedBy = operator;
					operator.affectsRight = curType.operandOrOperator;
					if (oldType.equals("boolean")) {
						if (curType.equals("boolean")) {
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("void")) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					else if (oldType.equals("int")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("short")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("long")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("float") || curType.equals("double")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("char")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("float")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("float")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("double")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("char")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("double")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("float")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("double")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("char")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("byte") || oldType.equals("char")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("float") || curType.equals("double")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("char")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("java.lang.String")) {
						// 현재 오퍼랜드는 String으로 바뀐다.
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					else {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("float") || curType.equals("double")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (oldType.equals("byte") || curType.equals("char")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
				} // for (i=1; i<listOfTypes.count; i++) {
				
				// 스택에서 마지막 2개의 오퍼랜드를 빼낸다.
				listOfTypes.count -= 2;
				//listOfVarUses.count -= 2;
				
				return oldType;
			} //if (listOfTypes.count==2) {
		}//if (operator.equals("&&") || operator.equals("||")) {
		
		
		if (operator.equals("==") || operator.equals("!=")) {
			if (listOfTypes.count>1) {
				oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-2);
				if (oldType==null) {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+"null"));
					listOfTypes.count -= 2;
					return new CodeStringEx("null");
				}
				oldType.operandOrOperator.affectedBy_left = operator;
				oldType.operandOrOperator.affectedBy = operator;
				operator.affectsLeft = oldType.operandOrOperator;
				for (i=listOfTypes.count-1; i<listOfTypes.count; i++) {
					curType = (CodeStringEx) listOfTypes.getItem(i);
					if (curType==null) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, 
								"invalid expression : ("+operator.str+") "+oldType+", "+"null"));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					curType.operandOrOperator.affectedBy_right = operator;
					curType.operandOrOperator.affectedBy = operator;
					operator.affectsRight = curType.operandOrOperator;
					if (oldType.equals("boolean")) {
						if (curType.equals("boolean")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("void")) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					else if (oldType.equals("int") || oldType.equals("short") || oldType.equals("long")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("short")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("long")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("float") || curType.equals("double")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("byte") || curType.equals("char")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("int")) {
					else if (oldType.equals("float")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("float")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("double")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("byte") || curType.equals("char")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("float")) {
					else if (oldType.equals("double")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("float")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("double")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("byte") || curType.equals("char")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}//else if (oldType.equals("double")) {
					else if (oldType.equals("byte") || oldType.equals("char")) {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("float") || curType.equals("double")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("byte") || curType.equals("char")) {
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("java.lang.String")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}
					else if (oldType.equals("java.lang.String")) {
						// 현재 오퍼랜드는 String으로 바뀐다.
						//FindVarUseParams v = (FindVarUseParams) listOfVarUses.list[i];
						//v.fullnameTypeCastByOperator = oldType;
						//return oldType;
						if (curType.equals("null")) { // if (str==null) 에서 스택에는 str, "null", ==
							oldType = new CodeStringEx("boolean");
						}
						else if (curType.equals("java.lang.String")) { // if (str1==str2) 에서 스택에는 str1, str2, ==
							oldType = new CodeStringEx("boolean");
						}
						else if (compiler.IsDefaultType(curType)) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else { // if (str==obj)
							boolean b = TypeCast.isCompatibleType(compiler, oldType, curType, 0, null);
							if (b==false) {
								Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
								listOfTypes.count -= 2;
								return new CodeStringEx("null");
							}
						}
					}
					else {
						if (curType.equals("boolean")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("void")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("int") || curType.equals("short") || curType.equals("long")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("float") || curType.equals("double")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("byte") || curType.equals("char")) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else if (curType.equals("java.lang.String")) {
							boolean b = TypeCast.isCompatibleType(compiler, oldType, curType, 0, null);
							if (b==false) {
								Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
								listOfTypes.count -= 2;
								return new CodeStringEx("null");
							}
							else {
								oldType = new CodeStringEx("boolean");
							}
						}
						else {
							//Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							boolean b = TypeCast.isCompatibleType(compiler, oldType, curType, 0, null);
							if (b==false) {
								Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
								listOfTypes.count -= 2;
								return new CodeStringEx("null");
							}
							else {
								oldType = new CodeStringEx("boolean");
							}
						}
					}
				} // for (i=1; i<listOfTypes.count; i++) {
				
				// 스택에서 마지막 2개의 오퍼랜드를 빼낸다.
				listOfTypes.count -= 2;
				//listOfVarUses.count -= 2;
				
				return oldType;
			} //if (listOfTypes.count==2) {
		}//if (operator.equals("==") || operator.equals("!=")) {
		
		
		else if (operator.equals("instanceof")) {
			if (listOfTypes.count>1) {
				oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-2);
				if (oldType==null) {
					Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+"null"));
					listOfTypes.count -= 2;
					return new CodeStringEx("null");
				}
				oldType.operandOrOperator.affectedBy_left = operator;
				oldType.operandOrOperator.affectedBy = operator;
				operator.affectsLeft = oldType.operandOrOperator;
				for (i=listOfTypes.count-1; i<listOfTypes.count; i++) {
					curType = (CodeStringEx) listOfTypes.getItem(i);
					if (curType==null) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, 
								"invalid expression : ("+operator.str+") "+oldType+", "+"null"));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					curType.operandOrOperator.affectedBy_right = operator;
					curType.operandOrOperator.affectedBy = operator;
					operator.affectsRight = curType.operandOrOperator;
					if (compiler.IsDefaultType(oldType)) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					else {
						//else if (sender instanceof MenuProblemList) 
						boolean b = TypeCast.isCompatibleType(compiler, oldType, curType, 1, null);
						if (b==false) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						else {
							oldType = new CodeStringEx("boolean");
						}
					}
				}// for (i=1; i<listOfTypes.count; i++) {
				
				// 스택에서 마지막 2개의 오퍼랜드를 빼낸다.
				listOfTypes.count -= 2;
				//listOfVarUses.count -= 2;
				
				return oldType;
			} //if (listOfTypes.count>1) {
		}//if (operator.equals("instanceof")) {
		
		else if (operator.str.contains("=")) { // =, +=, -= 등
		//else if (CompilerHelper.IsAssignOperator(op1, op2, op3))
			if (operator.indicesInSrc!=null && operator.indicesInSrc.getItem(0)==1950) {
				int a;
				a=0;
				a++;
			}
			
			if (operator.count>1) {
				boolean operatorError = false;
				CodeStringEx op1 = new CodeStringEx(""+operator.str.charAt(0));
				CodeStringEx op2 = null;
				if (op1.equals("<")) {
					
					if (operator.str.length()!=3) operatorError = true;
					else if (operator.str.charAt(1)!='<') operatorError = true;
					if (operatorError) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, 
								"invalid operator : ("+operator.str+") "));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					else {// '<<='
						op1 = new CodeStringEx("<<");
					}
				}
				if (operatorError==false) {
					/*// "<<=", "+=", "-=" 등의 복합 대입연산자의 경우
					CodeStringEx type = getTypeOfOperator(compiler, funcCall, listOfTypes, op1);
					return type;*/
				}
			}//if (operator.count>1) {
			//else { // '='
				if (listOfTypes.count>1) {
					oldType = (CodeStringEx) listOfTypes.getItem(listOfTypes.count-2);
					if (oldType==null) {
						Compiler.errors.add(new Error(compiler, startIndex, endIndex, "invalid expression : ("+operator.str+") "+"null"));
						listOfTypes.count -= 2;
						return new CodeStringEx("null");
					}
					oldType.operandOrOperator.affectedBy_left = operator;
					oldType.operandOrOperator.affectedBy = operator;
					operator.affectsLeft = oldType.operandOrOperator;
					for (i=listOfTypes.count-1; i<listOfTypes.count; i++) {
						curType = (CodeStringEx) listOfTypes.getItem(i);
						if (curType==null || (curType!=null && curType.equals(""))) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, 
								"invalid expression : ("+operator.str+") "+oldType+", "+"null"));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
						curType.operandOrOperator.affectedBy_right = operator;
						curType.operandOrOperator.affectedBy = operator;
						operator.affectsRight = curType.operandOrOperator;
						if (TypeCast.isCompatibleType(compiler, oldType, curType, 1, null)==false) {
							Compiler.errors.add(new Error(compiler, startIndex, endIndex, 
									"invalid expression : ("+operator.str+") "+oldType+", "+curType));
							listOfTypes.count -= 2;
							return new CodeStringEx("null");
						}
					}// for (i=1; i<listOfTypes.count; i++) {
					
					// 스택에서 마지막 2개의 오퍼랜드를 빼낸다.
					listOfTypes.count -= 2;
					//listOfVarUses.count -= 2;
					
					return oldType;
				} //if (listOfTypes.count>1) {
			//}
		}//else if (operator.str.contains("=")) { // =, +=, -= 등
		
		
		Compiler.errors.add(new Error(compiler, startIndex, endIndex, 
				"invalid operator : ("+operator.str+") "));
		return new CodeStringEx("null");
	}//getTypeOfOperator()


/**value의 크기와 범위에 따라 그 타입을 리턴한다.*/
public static String getType(long value) {
	if (Byte.MIN_VALUE<=value && value<=Byte.MAX_VALUE) return "byte";
	else if (Short.MIN_VALUE<=value && value<=Short.MAX_VALUE) return "short";
	else if (Integer.MIN_VALUE<=value && value<=Integer.MAX_VALUE) return "int";
	else if (Long.MIN_VALUE<=value && value<=Long.MAX_VALUE) return "long";
	
	return "long";
}


/** 숫자 상수에 대해서 실제로 연산을 하여 그 값을 operator의 value에 저장한다. 
 * number1, number2가 숫자상수가 아니면 false를 리턴한다.
 * 계산된 값은 operator의 value에 저장된다.*/
public static 
	boolean allowsOperator(CodeStringEx number1, CodeStringEx number2, CodeStringEx operator) {
		if (number1==null) return false;
		if (number1.str==null) return false;
		
		try {
		if ((number1.str.equals("float") || number1.str.equals("double"))==false) {
			if ((operator.equals("+")  && operator.isPlusOrMinusForOne) || 
					(operator.equals("-") && operator.isPlusOrMinusForOne)) {
				try {
					int i = Integer.parseInt(number1.value);
					String strValue = operator.str + number1.value;
					int r = Integer.parseInt(strValue);
					operator.value = String.valueOf(r);
					return true;
				}catch(Exception e) {
					return false;
				}
			} // 단항연산자
			else if ((operator.equals("+") && operator.isPlusOrMinusForOne==false) || 
					(operator.equals("-") && operator.isPlusOrMinusForOne==false) || 
					operator.equals("*") || operator.equals("/") ||	operator.equals("%")) {
				int i1;
				try {
					i1 = Integer.parseInt(number1.value);
				}catch(Exception e) {
					return false;
				}	
				if (operator.equals("+")) {
					try {
						int i2 = Integer.parseInt(number2.value);
						String strValue = String.valueOf(i1+i2);
						int r = Integer.parseInt(strValue);
						operator.value = String.valueOf(r);
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				else if (operator.equals("-")) {
					try {
						int i2 = Integer.parseInt(number2.value);
						String strValue = String.valueOf(i1-i2);
						int r = Integer.parseInt(strValue);
						operator.value = String.valueOf(r);
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				else if (operator.equals("*")) {
					try {
						int i2 = Integer.parseInt(number2.value);
						String strValue = String.valueOf(i1*i2);
						int r = Integer.parseInt(strValue);
						operator.value = String.valueOf(r);
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				else if (operator.equals("/")) {
					try {
						int i2 = Integer.parseInt(number2.value);
						String strValue = String.valueOf(i1/i2);
						int r = Integer.parseInt(strValue);
						operator.value = String.valueOf(r);
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				
			}//else if ((operator.equals("+") && operator.isPlusOrMinusForOne==false) || 
			//(operator.equals("-") && operator.isPlusOrMinusForOne==false) || 
			//operator.equals("*") || operator.equals("/") ||	operator.equals("%")) {
			else if (operator.equals("^") || 
					operator.equals("<<") || operator.equals(">>") || operator.equals("<<<") || operator.equals(">>>") ||
					operator.equals("&") || operator.equals("|")) {
				int i1;
				try {
					i1 = Integer.parseInt(number1.value);
				}catch(Exception e) {
					return false;
				}	
				if (operator.equals("^")) {
					try {
						int i2 = Integer.parseInt(number2.value);
						String strValue = String.valueOf(i1^i2);
						int r = Integer.parseInt(strValue);
						operator.value = String.valueOf(r);
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				else if (operator.equals("<<")) {
					try {
						int i2 = Integer.parseInt(number2.value);
						String strValue = String.valueOf(i1<<i2);
						int r = Integer.parseInt(strValue);
						operator.value = String.valueOf(r);
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				else if (operator.equals(">>")) {
					try {
						int i2 = Integer.parseInt(number2.value);
						String strValue = String.valueOf(i1>>i2);
						int r = Integer.parseInt(strValue);
						operator.value = String.valueOf(r);
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				else if (operator.equals("&")) {
					try {
						int i2 = Integer.parseInt(number2.value);
						String strValue = String.valueOf(i1&i2);
						int r = Integer.parseInt(strValue);
						operator.value = String.valueOf(r);
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				else if (operator.equals("|")) {
					try {
						int i2 = Integer.parseInt(number2.value);
						String strValue = String.valueOf(i1|i2);
						int r = Integer.parseInt(strValue);
						operator.value = String.valueOf(r);
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				
			}//else if (operator.equals("^") || 
			//operator.equals("<<") || operator.equals(">>") || operator.equals("<<<") || operator.equals(">>>") ||
			//operator.equals("&") || operator.equals("|")) {
		}
		else { // float, double
			if ((operator.equals("+")  && operator.isPlusOrMinusForOne) || 
					(operator.equals("-") && operator.isPlusOrMinusForOne)) {
				try {
					float i = Float.parseFloat(number1.value);
					String strValue = operator.str + number1.value;
					float r = Float.parseFloat(strValue);
					operator.value = String.valueOf(r);
					return true;
				}catch(Exception e) {
					return false;
				}
			} // 단항연산자
			else if ((operator.equals("+") && operator.isPlusOrMinusForOne==false) || 
					(operator.equals("-") && operator.isPlusOrMinusForOne==false) || 
					operator.equals("*") || operator.equals("/") ||	operator.equals("%")) {
				float f1;
				try {
					f1 = Float.parseFloat(number1.value);
				}catch(Exception e) {
					return false;
				}	
				if (operator.equals("+")) {
					try {
						float f2 = Float.parseFloat(number2.value);
						String strValue = String.valueOf(f1+f2);
						operator.value = strValue;
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				else if (operator.equals("-")) {
					try {
						float f2 = Float.parseFloat(number2.value);
						String strValue = String.valueOf(f1-f2);
						operator.value = strValue;
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				else if (operator.equals("*")) {
					try {
						float f2 = Float.parseFloat(number2.value);
						String strValue = String.valueOf(f1*f2);
						operator.value = strValue;
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				else if (operator.equals("/")) {
					try {
						float f2 = Float.parseFloat(number2.value);
						String strValue = String.valueOf(f1/f2);
						operator.value = strValue;
						return true;							
					}catch(Exception e) {
						return false;
					}
				}
				
			}//else if ((operator.equals("+") && operator.isPlusOrMinusForOne==false) || 
			//(operator.equals("-") && operator.isPlusOrMinusForOne==false) || 
			//operator.equals("*") || operator.equals("/") ||	operator.equals("%")) {
			else if (operator.equals("^")) {
				return false;
			}
				
		}
		}finally {
			if (operator.value!=null) {
				if (operator.value.contains(".")==false) {
					long value = Long.parseLong(operator.value);
					operator.typeFullName = getType(value);
				}
				else {
					if (number1!=null && number1.typeFullName!=null && 
							number2!=null && number2.typeFullName!=null) {
						// float, double
						if (number1.typeFullName.equals("double") || number2.typeFullName.equals("double")) {
							operator.typeFullName = "double";
						}
						else {
							operator.typeFullName = "float";
						}
					}
					else {
						operator.typeFullName = "float";
					}
				}
			}
		}
		return false;
	}//allowsOperator()


	/** fullname을 가진 클래스내에 String toString()함수가 존재하는지 확인한다.*/
	public static boolean toStringFuncExists(Compiler compiler, String fullname) {
		if (fullname==null || fullname.equals("")) return false;
		FindClassParams c = CompilerHelper.loadClass(compiler, fullname);
		if (c==null) return false;
		int i;
		for (i=0; i<c.listOfFunctionParams.count; i++) {
			FindFunctionParams func = (FindFunctionParams) c.listOfFunctionParams.getItem(i);
			if (func.name.equals("toString")) {
				if (func.returnType==null) continue;
				if (func.returnType.equals("java.lang.String")) return true;
			}
		}
		for (i=0; i<c.listOfFunctionParamsInherited.count; i++) {
			FindFunctionParams func = (FindFunctionParams) c.listOfFunctionParamsInherited.getItem(i);
			if (func.name.equals("toString")) {
				if (func.returnType==null) continue;
				if (func.returnType.equals("java.lang.String")) return true;
			}
		}
		return false;
	}


}