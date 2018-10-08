package com.gsoft.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.gsoft.common.Compiler_types.*;
import com.gsoft.common.ByteCode_Types.Attribute_Info;
import com.gsoft.common.ByteCode_Types.ByteCodeInstruction;
import com.gsoft.common.ByteCode_Types.CONSTANT_Class_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Double_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Field_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Float_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Integer_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_InterfaceMethod_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Long_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Method_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_NameAndTypeDesc_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_String_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Utf8_info;
import com.gsoft.common.ByteCode_Types.ClassFieldMethod;
import com.gsoft.common.ByteCode_Types.Code_attribute;
import com.gsoft.common.ByteCode_Types.Exceptions_attribute;
import com.gsoft.common.ByteCode_Types.Field_Info;
import com.gsoft.common.ByteCode_Types.Method_Info;
import com.gsoft.common.Code.CodeChar;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Compiler_types.FindFunctionParams;
import com.gsoft.common.Compiler_types.FindVarParams;
import com.gsoft.common.Compiler_types.LoadWayOfFindClassParams;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListChar;
import com.gsoft.common.Util.ArrayListCodeChar;
import com.gsoft.common.Util.ArrayListCodeString;
import com.gsoft.common.Util.ArrayListIReset;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.BufferByte;
import com.gsoft.common.Util.Hashtable2_Object;
import com.gsoft.common.Util.HighArrayForReading_CodeChar;
import com.gsoft.common.Compiler_types.HighArray_CodeString;
import com.gsoft.common.gui.Control;

public class PathClassLoader extends ClassLoader implements IReset {
	String absFilename;
	
	String name;
	String superName;
	ArrayListString interfaceNames = new ArrayListString(5);
	
	ArrayList constantTable;
	
		
	short accessFlags;
	AccessModifier accessModifier;
	
	
	
	short[] interfaces;
	Field_Info[] fields_info;
	Method_Info[] methods_info;
	Attribute_Info[] attributes_info;
	//InnerClasses_attribute innerclasses_info;
	
	/** output, 로드된 클래스*/
	FindClassParams classParams;
	Compiler compiler;
	
	/** FindClassParams[]*/
	ArrayListIReset listOfInnerClasses;
	private boolean loadsInnerClass = false;

	/** 클래스가 템플릿 클래스일 경우 <>안에 있는 바꿔야 할 풀 타입 이름이다.*/
	String typeNameInTemplatePair;
	/** 클래스가 템플릿 클래스일 경우 템플릿 타입 이름을 포함하고 배열은 포함하지 않은 클래스 풀 이름이다.*/
	String fullNameIncludingTemplateExceptArray;

	/** code array 와 Code_Attribute의 속성 
	 * 즉, LineNumberTable, LocalVariableTable, StackMapTable들을 
	 * 읽으면 true, 그렇지 않으면 false
	 */
	public boolean readsCode = false;

	HighArray_CodeString mBuffer;

	/** big-endian 을 디폴트로 한다.*/
	private boolean IsLittleEndian = false;
	
	/** 클래스 파일 로드 실패 리스트*/
	static ArrayListString listOfClassesToFailLoading = new ArrayListString(10);
	
		
	
	
	/** 해시테이블을 생성해서 instructionSet 을 테이블에 넣는다. 한번만 초기화 가능*/
	static {
		ByteCode_Types.hashTableInstructionSet = new Hashtable2_Object(100, 5);
		int i;
		for (i=0; i<ByteCode_Types.instructionSet.length; i++) {
			ByteCodeInstruction instruction = ByteCode_Types.instructionSet[i];
			ByteCode_Types.hashTableInstructionSet.input(instruction.opcodeHexa, instruction);
		}
	}
	
	
	
	
	/** class 와 field, method 와 code array 를 출력하는 스트링을 리턴한다.*/
	/*CodeString getText_1() {
		try {
			int initMaxArrayLen = 1000;
			if (classParams.listOfFunctionParams.count<10) initMaxArrayLen = 100;
			else if (classParams.listOfFunctionParams.count<50) initMaxArrayLen = 1000;
			else initMaxArrayLen = 10000;
			ArrayListCodeChar r = new ArrayListCodeChar(initMaxArrayLen);
			
		r.concate(new CodeString(classParams.toString() + " {\n", Compiler.textColor));
		
		int i, j;
		for (i=0; i<classParams.listOfVariableParams.count; i++) {
			FindVarParams var = (FindVarParams) classParams.listOfVariableParams.getItem(i);
			r.concate(new CodeString("\t" + var.toString() + ";\n", Compiler.textColor));
		}
		for (i=0; i<classParams.listOfFunctionParams.count; i++) {
			CompilerHelper.showMessage(true, "i : "+i);
			if (i==2) {
				int a;
				a=0;
				a++;
			}
			FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
			r.concate(new CodeString("\t" + func.toString() + " {\n", Compiler.textColor));
			// code 를 출력한다.
			Code_attribute codeAttribute = null;
			for (j=0; j<func.method_Info.attributes_count; j++) {
				Attribute_Info attribute = func.method_Info.attributes[j];
				if (attribute.codeAttribute!=null) {
					codeAttribute = attribute.codeAttribute;
					break;
				}
			}
			try {
			CodeString code = codeAttribute.toCodeString(this.constantTable, IsLittleEndian );
			r.concate(code);
			}catch(Exception e) {
				e.printStackTrace();
				int a;
				a=0;
				a++;
			}
			r.concate(new CodeString("\t" + "}\n", Compiler.textColor));
		}
		// 클래스의 블록 끝
		r.concate(new CodeString("}\n", Compiler.textColor));
		
		//CodeString newR = new CodeString(r,Compiler.textColor);
		
		Compiler compiler = new Compiler();
		CodeChar[] codeChars = r.getItems();
		CodeString ret = new CodeString(codeChars, codeChars.length);
		mBuffer = compiler.ConvertToStringArray2(ret, 1000);
		//compiler.reset();
		compiler = null;
		return ret;
		}catch(Exception e) {
			return null;
		}
	}*/
	
	
	/** class 와 field, method 와 code array 를 출력하는 스트링을 리턴한다.*/
	CodeString getText() {
		try {
			HighArrayForReading_CodeChar r = new HighArrayForReading_CodeChar(1000);
						
		r.concate(new CodeString(classParams.toString() + " {\n", Compiler.textColor));
		
		int i, j;
		for (i=0; i<classParams.listOfVariableParams.count; i++) {
			FindVarParams var = (FindVarParams) classParams.listOfVariableParams.getItem(i);
			r.concate(new CodeString("\t" + var.toString() + ";\n", Compiler.textColor));
		}
		for (i=0; i<classParams.listOfFunctionParams.count; i++) {
			CompilerHelper.showMessage(true, "i : "+i);
			if (i==6) {
				int a;
				a=0;
				a++;
			}
			FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
			r.concate(new CodeString("\t" + func.toString() + " {\n", Compiler.textColor));
			// code 를 출력한다.
			Code_attribute codeAttribute = null;
			for (j=0; j<func.method_Info.attributes_count; j++) {
				if (func.name.equals("main")) {
					int a;
					a=0;
					a++;
				}
				Attribute_Info attribute = func.method_Info.attributes[j];
				if (attribute.codeAttribute!=null) {
					codeAttribute = attribute.codeAttribute;
					break;
				}
			}
			try {
			CodeString code = codeAttribute.toCodeString(this.constantTable, IsLittleEndian );
			r.concate(code);
			}catch(Exception e) {
				e.printStackTrace();
				int a;
				a=0;
				a++;
			}
			r.concate(new CodeString("\t" + "}\n", Compiler.textColor));
		}//for (i=0; i<classParams.listOfFunctionParams.count; i++) {
		// 클래스의 블록 끝
		r.concate(new CodeString("}\n", Compiler.textColor));
		
		
		
		
		CodeChar[] codeChars = r.toArray();
		CodeString ret = new CodeString(codeChars, codeChars.length);
		mBuffer = new StringTokenizer().ConvertToStringArray2(ret, 1000, Language.Java);
		
		//compiler.reset();
		compiler = null;
		return ret;
		}catch(Exception e) {
			return null;
		}
	}
	
	
	
	void loadInnerClass() throws Exception {
		listOfInnerClasses = new ArrayListIReset(5);
		int i, j;
		for (i=0; i<attributes_info.length; i++) {
			if (attributes_info[i].classes!=null) {
				for (j=0; j<attributes_info[i].classes.length; j++) {
					String innerClassName = attributes_info[i].classes[j].innerClassName;
					String innerClassName2 = innerClassName.replace('/', '.');
					innerClassName2 = innerClassName2.replace('$', '.');
					if (innerClassName2.equals(this.name)) continue;
					
					//String innerClassNamePath = Control.pathAndroid + "/" + innerClassName + ".class";
					
					String innerClassNamePath;
					innerClassNamePath = innerClassName2.replace('.', '/'); 
					innerClassNamePath = Control.pathAndroid + "/" + innerClassNamePath + ".class";
					
					
					String path = CompilerHelper.fixClassPath(innerClassNamePath);
					
					if (path!=null) innerClassNamePath = path;
					else continue;
					 
					try{
						// 내부클래스 역시 mlistOfAllClasses에 존재하지 않을때만 로드하고 등록
						if (CompilerHelper.exists(Compiler.mlistOfAllClassesHashed, innerClassName2)==false) {
							PathClassLoader classLoader = new PathClassLoader(innerClassNamePath, null, null, readsCode);
							if (classLoader.classParams!=null && classLoader.classParams.name!=null) {
								listOfInnerClasses.add(classLoader.classParams);
								Compiler.mlistOfAllClasses.add(classLoader.classParams);
							}
						}
						else {
							listOfInnerClasses.add(
								CompilerHelper.getFindClassParams(Compiler.mlistOfAllClassesHashed, 
										innerClassName2));
						}
					}
					catch(Exception e) {
						e.printStackTrace();
						throw e;
					}
				}
			}
		}
	}
	
	/**전에 로드를 실패한 적이 있다면 다시 로드하지 않는다. 
	 * PathClassLoader에서 클래스 파일 로드 실패 리스트에 등록한다.
	 * @param classPath : 디렉토리상 풀 경로, 확장자 포함*/
	static boolean failedLoadingAlready(String classPath) {
		int i;
		// 전에 로드를 실패한 적이 있다면 다시 로드하지 않는다.
		for (i=0; i<listOfClassesToFailLoading.count; i++) {
			if (listOfClassesToFailLoading.getItem(i).equals(classPath))
				return true;
		}
		return false;
	}
	
	/** 클래스 파일을 로드한다. 로드가 실패하면  listOfClassesToFailLoading에 등록되어
	 * boolean failedLoadingAlready(String classPath) 이 함수를 통해 이미 로드가 실패했는지를 확인한다.
	 * @param classPath : 디렉토리상 풀 경로, 확장자 포함*/
	public PathClassLoader(String classPath, String typeNameInTemplatePair, String fullNameIncludingTemplateExceptArray, boolean readsCode) {
		//Context context = Control.activity.getApplicationContext();		
		this.compiler = new Compiler();
		this.compiler.filename = classPath;
		FileInputStream stream=null;
		BufferedInputStream bis=null;
		absFilename=null;
		this.typeNameInTemplatePair = typeNameInTemplatePair;
		this.fullNameIncludingTemplateExceptArray = fullNameIncludingTemplateExceptArray;
		this.readsCode = readsCode;
		boolean r = false;
		
	
		
		try {
			//File contextDir = context.getFilesDir();
			absFilename = classPath;
			String shortName = FileHelper.getFilename(absFilename);
			if (shortName.equals("interfaces$Listener.class")) {
				int a;
				a=0;
				a++;
			}
			if (shortName.equals("Util$Stack.class")) {
				int a;
				a=0;
				a++;
			}
			stream = new FileInputStream(absFilename);
			int bufferSize = (int) (FileHelper.getFileSize(absFilename)*IO.DefaultBufferSizeParam);
			bis = new BufferedInputStream(stream, bufferSize);
			String filename = FileHelper.getFilename(classPath); 
			if (filename.equals("Timer.class")) {
				int a;
				a=0;
				a++;
			}
			if (filename.equals("Util$ArrayListCodeString.class")) {
				int a;
				a=0;
				a++;
			}
			try {
				readClassFile(bis);
			}catch(Exception e) {
				//e.printStackTrace();
				listOfClassesToFailLoading.add(classPath);
				throw new Exception("Can't read the class file "+ filename + " : " + e.getMessage());
			}
			
			//readPlayListAndCurSongInfo(bis);
			
			r= true;
		} catch (StackOverflowError sfe) {
			Log.e("can't load class file", classPath);
			CompilerHelper.printMessage(CommonGUI.textViewLogBird, "can't load class file : " + classPath);
			r = false;
		} catch (FileNotFoundException e) {
			if (classPath.contains("mode.class")) {
				int a;
				a=0;
				a++;
			}
			Log.e("can't load class file", classPath);
			CompilerHelper.printMessage(CommonGUI.textViewLogBird, "can't load class file : " + classPath);
			r=false;
		}
		catch (OutOfMemoryError ome) {
			Log.e("can't load class file", classPath);
			CompilerHelper.printMessage(CommonGUI.textViewLogBird, "can't load class file : " + classPath);
			r = false;
			ome.printStackTrace();
			System.gc();
		}
		catch (Exception e) {
			Log.e("can't load class file", classPath);
			CompilerHelper.printMessage(CommonGUI.textViewLogBird, "can't load class file : " + classPath + "\n");
			
			/*String fullNameExceptTemplate = CompilerHelper.getTemplateOriginalType(this.fullNameIncludingTemplateExceptArray);
			String fullNameSlash = fullNameExceptTemplate.replace('.', File.separatorChar);
			String path = Control.pathProjectSrc + File.separator + fullNameSlash;
			
			String srcPath = CompilerHelper.getSourceFilePath(path);
			srcPath += ".java";
			
			int indexOfJavaLang = fullNameExceptTemplate.indexOf("java.lang");
			
			Log.e("can't load class file", classPath);
			if (indexOfJavaLang!=0) {
				CompilerHelper.printMessage(CommonGUI.textViewLogBird, "can't load class file : " + classPath + 
						"\nTrys to read its source file : " + srcPath);
			}
			else {
				// java.lang 패키지 안에 있는 클래스들에 대해서는 srcPath를 생략한다.
				// (null.java로 나옴)
				CompilerHelper.printMessage(CommonGUI.textViewLogBird, "can't load class file : " + classPath + "\n");
			}*/
			r=false;
			//e.printStackTrace();
			//CompilerHelper.printStackTrace(Control.textViewLogBird, e);
		}
		finally {
			//IO.IsLittleEndian = true;
			FileHelper.close(bis);
			FileHelper.close(stream);
			if (!r) {
				if (absFilename!=null) {
					//File file = new File(absFilename);
					//file.delete();
				}
			}
		}
	}
	
	
	
	/** inner class는 outer class에서 읽는다.
	 * @throws Exception */
	void readClassFile(InputStream is) throws Exception {
		try {
		// magic number
		
		String shortName = FileHelper.getFilename(absFilename);
		if (shortName.equals("ProcessBuilder.class")) {
			int a;
			a=0;
			a++;
		}
		else if (shortName.equals("Util$Date.class")) {
			int a;
			a=0;
			a++;
		}
		else if (shortName.equals("Util$PoolOfButton.class")) {
			int a;
			a=0;
			a++;
		}
		
		int i, j;
		byte[] bufMagicNumber = new byte[4];
		//try {
			is.read(bufMagicNumber);
		/*} catch (IOException e) {
			e.printStackTrace();
			CompilerHelper.printStackTrace(Control.textViewLogBird, e);
		}*/
		
		//boolean isLittleEndian = IO.IsLittleEndian;
		//boolean IsLittleEndian = false; // big-endian
		
		short[] bufVersion = new short[2];
		bufVersion[0] = IO.readShort(is, IsLittleEndian);
		bufVersion[1] = IO.readShort(is, IsLittleEndian);
		
		// constant table 을 읽는다.
		short countOfConstantPool = IO.readShort(is, IsLittleEndian);
		byte[] tag = new byte[1];
		
		
		
		ArrayList nameAndTypeDescs = new ArrayList(5);
		
		
		// countOfConstantPool의 값은 constantTable의 엔트리 개수보다 1개 더 많다.
		// constantTable의 인덱스는 0보다 크고(1이상) countOfConstantPool보다 작다(countOfConstantPool-1).
		
		/*ClassFile { 
			u4 magic; // 0xCAFEBABE
			u2 minor_version; 
			u2 major_version; 
			u2 constant_pool_count; //constant_pool의 엔트리수보다 하나 더 많다.
			cp_info constant_pool[constant_pool_count-1]; //constant_pool의 인덱스는 0보다 크고 constant_pool_count보다 하나 작다.
			u2 access_flags; 
			u2 this_class; // CONSTANT_Class_info을 가르키는 constant_pool의 인덱스
			u2 super_class; 
			u2 interfaces_count; 
			u2 interfaces[interfaces_count]; 
			u2 fields_count; 
			field_info fields[fields_count]; 
			u2 methods_count; 
			method_info methods[methods_count]; 
			u2 attributes_count; 
			attribute_info attributes[attributes_count]; 
		}*/
		
		/*cp_info { 
			u1 tag; //Constant pool tags
			u1 info[]; //태그값에 따라 다양하다.
		}*/

		constantTable = new ArrayList(countOfConstantPool);
		
		constantTable.add(new Integer(0)); // constant table 인덱스가 1부터 시작하기 때문이다.
		
		
		/*Constant pool tags
		 * Constant Type Value
		  CONSTANT_Class 	7 
		CONSTANT_Fieldref 	9 
		CONSTANT_Methodref 	10 
		CONSTANT_InterfaceMethodref 11 
		CONSTANT_String 	8 
		CONSTANT_Integer 	3 
		CONSTANT_Float	 	4 
		CONSTANT_Long 		5 
		CONSTANT_Double 	6 
		CONSTANT_NameAndType 12 
		CONSTANT_Utf8 		1
		*/
		
			//i = 2; // constant table 인덱스가 1부터 시작하기 때문이다.
		// countOfConstantPool의 값은 constantTable의 엔트리 개수보다 1개 더 많다.
		// constantTable의 인덱스는 0보다 크고 countOfConstantPool보다 작다.
			for (i=1; i<countOfConstantPool; i++) {
				if (compiler.filename.contains("String")) {
					if (i==countOfConstantPool-3) {
						int a;
						a=0;
						a++;
					}
				}
				if (i==countOfConstantPool-3) {
					int a;
					a=0;
					a++;
				}
				else if (i==countOfConstantPool-2) {
					int a;
					a=0;
					a++;
				}
				
					is.read(tag);
					//tag[0] = IO.readByte(is);
					
					if (tag[0]==0) {
						throw new IOException(this.absFilename + " load error" + " (invalid tag : "+tag[0] + 
							" countOfConstantPool :" + countOfConstantPool + " i :"+i+")");
					}
				
				switch(tag[0]) {
				case 1: // CONSTANT_Utf8_info
					/*CONSTANT_Utf8_info { 
						u1 tag; 
						u2 length; // 바이트수
						u1 bytes[length]; //utf-8,  not null-terminated
					}*/
					short numOfBytes = IO.readShort(is, IsLittleEndian);
					byte[] strBuf = new byte[numOfBytes];
					is.read(strBuf);
					BufferByte bufferbyte = new BufferByte(strBuf);
					String str = IO.readStringUTF8(null, bufferbyte);
					CONSTANT_Utf8_info utf8Info = new CONSTANT_Utf8_info(tag[0], numOfBytes, str);
					constantTable.add(utf8Info);
					break;
				case 3: // integer
					/*CONSTANT_Integer_info { 
						u1 tag; 
						u4 bytes; // big-endian (high byte first) order
					}*/
					//boolean backupEndian = IO.IsLittleEndian;
					//IO.IsLittleEndian = false;
					int number = IO.readInt(is, IsLittleEndian);
					//IO.IsLittleEndian = backupEndian;
					Integer integer = new Integer(number);
					CONSTANT_Integer_info integerInfo = new CONSTANT_Integer_info(tag[0], integer);
					constantTable.add(integerInfo);
					break;
				case 4: // float
					/*CONSTANT_Float_info { 
						u1 tag; 
						u4 bytes; //IEEE 754 floating point single format
					}*/
					float fNumber = IO.readFloat(is);
					Float float0 = new Float(fNumber);
					CONSTANT_Float_info floatInfo = new CONSTANT_Float_info(tag[0], float0);
					constantTable.add(floatInfo);
					break;
				case 5: // long
					/*CONSTANT_Long_info { 
						u1 tag; 
						u4 high_bytes; //8bytes
						u4 low_bytes; 
					}*/
					long lNumber = IO.readLong(is, IsLittleEndian);
					Long long0 = new Long(lNumber);
					CONSTANT_Long_info longInfo = new CONSTANT_Long_info(tag[0], long0);
					constantTable.add(longInfo);
					break;
				case 6: // double
					/*CONSTANT_Double_info { 
						u1 tag; 
						u4 high_bytes; //8bytes
						u4 low_bytes; 
					}*/
					double dNumber = IO.readDouble(is);
					Double double0 = new Double(dNumber);
					CONSTANT_Double_info doubleInfo = new CONSTANT_Double_info(tag[0], double0);
					constantTable.add(doubleInfo);
					break;			
				case 7: // class reference : full name 스트링을 가르키는 인덱스
					/*CONSTANT_Class_info { 
						u1 tag; //CONSTANT_Class (7)
						u2 name_index; // CONSTANT_Utf8_info를 가르키는 constantTable의 인덱스
					}*/
					short classRef = IO.readShort(is, IsLittleEndian);
					CONSTANT_Class_info classInfo = new CONSTANT_Class_info(tag[0], classRef, this.constantTable);
					constantTable.add(classInfo); //CONSTANT_Class_info의 name_index가 들어간다.
					break;
				case 8: // string reference
					/*CONSTANT_String_info { 
						u1 tag; 
						u2 string_index; 
					}*/

					short stringRef = IO.readShort(is, IsLittleEndian);
					Short short0 = new Short(stringRef);
					CONSTANT_String_info stringInfo = new CONSTANT_String_info(tag[0], stringRef, this.constantTable);
					constantTable.add(stringInfo);//CONSTANT_String_info의 string_index가 들어간다.
					break;
				case 9: // field reference
					/*CONSTANT_Fieldref_info { 
						u1 tag; // CONSTANT_Fieldref (9). 
						u2 class_index; // CONSTANT_Class_info을 가르키는 constantTable의 인덱스
						u2 name_and_type_index; //CONSTANT_NameAndType_info 을 가르키는 constantTable의 인덱스
					}*/
					short classRef0 = IO.readShort(is, IsLittleEndian);
					short nameTypeDescRef0 = IO.readShort(is, IsLittleEndian);
					CONSTANT_Field_info field = new CONSTANT_Field_info(classRef0, nameTypeDescRef0, this.constantTable, this);
					//CONSTANT_Fieldref_info의 class_index와 name_and_type_index을 갖는 Field가 들어간다.
					constantTable.add(field);
					
					break;
				case 10: // method reference
					/*CONSTANT_Methodref_info { 
						u1 tag; //CONSTANT_Methodref (10). 
						u2 class_index; 
						u2 name_and_type_index; 
					}*/
					short classRef1 = IO.readShort(is, IsLittleEndian);
					short nameTypeDescRef1 = IO.readShort(is, IsLittleEndian);
					CONSTANT_Method_info method = new CONSTANT_Method_info(classRef1, nameTypeDescRef1, this.constantTable, this);
					//CONSTANT_Methodref_info의 class_index와 name_and_type_index을 갖는 Method가 들어간다.
					constantTable.add(method);
					break;
				case 11: // interface method reference
					/*CONSTANT_InterfaceMethodref_info { 
						u1 tag; // CONSTANT_InterfaceMethodref (11)
						u2 class_index; 
						u2 name_and_type_index; 
					}*/

					short classRef2 = IO.readShort(is, IsLittleEndian);
					short nameTypeDescRef2 = IO.readShort(is, IsLittleEndian);
					CONSTANT_InterfaceMethod_info interfaceMethod = new CONSTANT_InterfaceMethod_info(
							this, this.constantTable, classRef2, nameTypeDescRef2);
					constantTable.add(interfaceMethod);
					break;
					
				case 12: // name and type descriptor
					/*CONSTANT_NameAndType_info { 
						u1 tag; 
						u2 name_index; //CONSTANT_Utf8_info을 가르키는 constantTable의 인덱스
						u2 descriptor_index; //CONSTANT_Utf8_info을 가르키는 constantTable의 인덱스
					}*/

					short nameRef = IO.readShort(is, IsLittleEndian);
					short typeDesc = IO.readShort(is, IsLittleEndian);
					CONSTANT_NameAndTypeDesc_info nameAndTypeDesc = new CONSTANT_NameAndTypeDesc_info(nameRef, typeDesc, this.constantTable);
					nameAndTypeDescs.add(nameAndTypeDesc);
					constantTable.add(nameAndTypeDesc);
					break;
				
				}
			} //for (i=1; i<countOfConstantPool; i++) {
			
		// SimpleTest2 의 Constant_ pool 아이템 갯수
		// 클래스 1개 : Constant_Class_info 1개 + Constant_utf8_info 1개 = 2
		// 필드 0개 : 0 * (Constant_utf8_info(name+type) 2개) = 0
		// 메서드 4개 : 4 * (Constant_utf8_info(name+type) 2개) = 8
		// 정수 상수 : 0
		// 스트링 상수 : 0
		// 지역변수 5개 : 5 * Constant_utf8_info 2개 = 10
		// 필드 참조 0개 : 0 * (Constant_Field_info 1개 + (Constant_Class_info 1개 + Constant_utf8_info 1개) + Constant_NameAndTypeDesc_info 1개 + (Constant_utf8_info 2개))
		//				= 0 * 6 = 0
		// 메서드 참조 4개 : 0 * (Constant_Method_info 1개 + (Constant_Class_info 1개 + Constant_utf8_info 1개) + Constant_NameAndTypeDesc_info 1개 + (Constant_utf8_info 2개))
		//				= 4 * 6 = 24
		// this : 1
		// Method와 Code Attribute : "Code", "LineNumberTable", "LocalVariableTable"
		//					= 3
		// Attribute : "SourceFile"(2개), "InnerClasses"(3개)
			
			
			// 디버그용
			/* CONSTANT_Class_info--com/gsoft/texteditor14/SimpleTest1$SimpleTest2  
			CONSTANT_Utf8_info--com/gsoft/texteditor14/SimpleTest1$SimpleTest2  
			
			// 디폴트 생성자에서 상속클래스 Object의 static 생성자(<init>)을 호출
			CONSTANT_Class_info--java/lang/Object  
			CONSTANT_Utf8_info--java/lang/Object  
			CONSTANT_Utf8_info--<init>  
			CONSTANT_Utf8_info--()V 			  
			CONSTANT_Method_info--java/lang/Object.Object()  
			CONSTANT_NameAndTypeDesc_info--<init>:()V 
			
			
			// 메서드와 Code Attribute 이름 3개
			CONSTANT_Utf8_info--Code 
			CONSTANT_Utf8_info--LineNumberTable  
			CONSTANT_Utf8_info--LocalVariableTable 
			
			// this 와 this 의 타입
			CONSTANT_Utf8_info--this  
			CONSTANT_Utf8_info--Lcom/gsoft/texteditor14/SimpleTest1$SimpleTest2; 
			
			
			// int test(int arg1, int arg2) 의 시그너쳐와 아규먼트의 타입과 이름
			CONSTANT_Utf8_info--test  
			CONSTANT_Utf8_info--(II)I  
			CONSTANT_Utf8_info--I  
			CONSTANT_Utf8_info--arg1 			
			CONSTANT_Utf8_info--arg2 
			
			// 중복성이 제거된 지역변수 이름과 타입			
			CONSTANT_Utf8_info--r 
			
			
			// int test2(Integer arg1, int arg2) 의 시그너쳐와 아규먼트의 타입과 이름
			CONSTANT_Utf8_info--test2  
			CONSTANT_Utf8_info--(Ljava/lang/Integer;I)I 
			CONSTANT_Utf8_info--Ljava/lang/Integer; 
			
			 
			// arg1.intValue() 메서드 참조(호출)
			CONSTANT_Method_info--int java/lang/Integer.intValue()  
			CONSTANT_Class_info--java/lang/Integer  
			CONSTANT_Utf8_info--java/lang/Integer  
			CONSTANT_NameAndTypeDesc_info--intValue:()I  
			CONSTANT_Utf8_info--intValue  
			CONSTANT_Utf8_info--()I 
			
			 
			// int test(String[] args) 의 시그너쳐와 아규먼트의 타입과 이름			
			CONSTANT_Utf8_info--([Ljava/lang/String;)I 
			CONSTANT_Utf8_info--[Ljava/lang/String; 
			CONSTANT_Utf8_info--args
			
			
			
			
			
			// SimpleTest2 test = new SimpleTest2();
			// 지역변수 SimpleTest2 test 도 중복이므로 생략
			// SimpleTest2()의 CONSTANT_Class_info와 메서드 이름과 type에 관한 CONSTANT_Utf8_info 생략
			CONSTANT_Method_info--com/gsoft/texteditor14/SimpleTest1$SimpleTest2.SimpleTest2() 
			
			 
			 // test(1, 2) 와 test(11, 300) this 메서드 참조(호출)
			 // CONSTANT_Class_info와 메서드 이름과 type에 관한 CONSTANT_Utf8_info 생략
			CONSTANT_Method_info--int com/gsoft/texteditor14/SimpleTest1$SimpleTest2.test(int, int)  
			CONSTANT_NameAndTypeDesc_info--test:(II)I
			
			  
			  
			
			
			// 중복성이 제거된 지역변수 이름과 타입
			CONSTANT_Utf8_info--r2
			
			 
			// SourceFile Attribute
			CONSTANT_Utf8_info--SourceFile  
			CONSTANT_Utf8_info--SimpleTest1.java  
			
			// InnerClasses Attribute
			CONSTANT_Utf8_info--InnerClasses  
			CONSTANT_Class_info--com/gsoft/texteditor14/SimpleTest1  
			CONSTANT_Utf8_info--com/gsoft/texteditor14/SimpleTest1  
			CONSTANT_Utf8_info--SimpleTest2  */
			
			String typesAndContents = this.getTypesAndContents(constantTable);
			
		
			if (FileHelper.getFilename(compiler.filename).contains("Dialog.class")) {
				int a;
				a=0;
				a++;
			}
		
			accessFlags = IO.readShort(is, IsLittleEndian);
			accessModifier = ByteCode_Types.toAccessModifier(accessFlags, ClassFieldMethod.Class);
			
			// CONSTANT_Class_info을 가르키는 constant_pool의 인덱스
			/*CONSTANT_Class_info { 
				u1 tag; 
				u2 name_index; 
			}*/
			short thisRef = IO.readShort(is, IsLittleEndian);
			if (0<thisRef && thisRef<constantTable.count) {
				short thisClassRef = ((CONSTANT_Class_info)constantTable.getItem(thisRef)).name_index;
				
				this.name = ((CONSTANT_Utf8_info) constantTable.getItem(thisClassRef)).str;
				this.name = this.name.replace('/', '.');
				this.name = this.name.replace('$', '.');
				
			}
			
			// CONSTANT_Class_info을 가르키는 constant_pool의 인덱스
			/*CONSTANT_Class_info { 
				u1 tag; 
				u2 name_index; 
			}*/
			short superRef = IO.readShort(is, IsLittleEndian);
			if (0<superRef && superRef<constantTable.count) {
				short superClassRef = ((CONSTANT_Class_info)constantTable.getItem(superRef)).name_index;
				
				this.superName = ((CONSTANT_Utf8_info) constantTable.getItem(superClassRef)).str;
				this.superName = this.superName.replace('/', '.');
				this.superName = this.superName.replace('$', '.');
				
			}
			
			short interfaces_count = IO.readShort(is, IsLittleEndian);
			interfaces = new short[interfaces_count];
			for (i=0; i<interfaces_count; i++) {
				interfaces[i] = IO.readShort(is, IsLittleEndian);
				short ref = ((CONSTANT_Class_info) constantTable.getItem(interfaces[i])).name_index;
				String name = ((CONSTANT_Utf8_info) constantTable.getItem(ref)).str;
				name = name.replace('/', '.');
				name = name.replace('$', '.');
				this.interfaceNames.add( name );
			}
			
			/*field_info { 
				u2 access_flags; 
				u2 name_index; //CONSTANT_Utf8_info을 가르키는 constantTable의 인덱스
				u2 descriptor_index; //CONSTANT_Utf8_info을 가르키는 constantTable의 인덱스
				u2 attributes_count; 
				attribute_info attributes[attributes_count];
			}*/

			short fields_count = IO.readShort(is, IsLittleEndian);
			fields_info = new Field_Info[fields_count];
			for (i=0; i<fields_count; i++) {
				fields_info[i] = Field_Info.read(this, is, this.constantTable, IsLittleEndian); 
			}
			
			short methods_count = IO.readShort(is, IsLittleEndian);
			methods_info = new Method_Info[methods_count];
			String className = compiler.getShortName(this.name);
			if (className.equals("Array")) {
				int a;
				a=0;
				a++;
			}
			for (i=0; i<methods_count; i++) {
				try {
					if (i==3) {
						int a;
						a=0;
						a++;
					}
				methods_info[i] = Method_Info.read(this, is, this.constantTable, IsLittleEndian );
				}catch(Exception e) {
					int a;
					a=0;
					a++;
					throw e;
				}
				
				if (methods_info[i].name!=null && methods_info[i].name.equals("getClass")) {
					int a;
					a=0;
					a++;
				}
			}
			
			short attributes_count = IO.readShort(is, IsLittleEndian);
			attributes_info = new Attribute_Info[attributes_count];
			for (i=0; i<attributes_count; i++) {
				attributes_info[i] = Attribute_Info.read(this, is, this.constantTable, IsLittleEndian);
			}
			
			//innerclasses_info = InnerClasses_attribute.read(is, this.constantTable);
			
			if (loadsInnerClass ) {
				loadInnerClass();
			}
			
			int a;
			a=0;
			a++;
			
			
		
		
		
		
		classParams = toFindClassParams();
		
		
		
		//IO.IsLittleEndian = isLittleEndian;
		
		
		/*getMembers(constantTable, 
				classReferences, classReferences_values, stringReferences_values, stringReferences, 
			fieldReferences, methodReferences, interfaceMethodReferences,
			newFieldReferences, newMethodReferences, newInterfaceMethodReferences);*/
		
		}catch (Exception e) {
			int a;
			a=0;
			a++;
			//e.printStackTrace();
			//CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			CompilerHelper.printMessage(CommonGUI.textViewLogBird, e.getMessage());
			throw e;
		}
		
	}
	
	String getTypesAndContents(ArrayList constantPool) {
		int i;
		ArrayListChar r = new ArrayListChar(10000);
		for (i=0; i<constantPool.count; i++) {
			String type = constantPool.getItem(i).getClass().getName();
			String shortType = CompilerHelper.getShortName(type);
			String content = constantPool.getItem(i).toString();
			r.add(shortType + "--" + content + "  ");
		}
		char[] rc = r.getItems();
		return new String(rc);
	}
	
	FindClassParams toFindClassParams() {
		try {
		FindClassParams c = new FindClassParams(this.compiler);
		
		if (this.name.contains("Dialog")) {
			int a;
			a=0;
			a++;
		}
		if (this.name!=null) c.name = this.name;
		//else c.name = (String) constantTable.getItem(2);
		if (this.superName!=null) c.classNameToExtend = this.superName;
		//else c.classNameToExtend = (String) constantTable.getItem(4);
		
		if (c.name!=null) {
			c.name = c.name.replace('/', '.');
			c.name = c.name.replace('$', '.');
		}
		// 클래스가 템플릿 클래스일 경우 템플릿 이름을 포함하는 풀 타입 이름이다.
		if (fullNameIncludingTemplateExceptArray!=null) {
			c.name = fullNameIncludingTemplateExceptArray;
		}
		
		if (c.classNameToExtend!=null) {
			c.classNameToExtend = c.classNameToExtend.replace('/', '.');
			c.classNameToExtend = c.classNameToExtend.replace('$', '.');
		}
				
		int i;
		if (c.interfaceNamesToImplement==null) {
			c.interfaceNamesToImplement = new ArrayListString(5);
		}
		for (i=0; i<this.interfaceNames.count; i++) {
			c.interfaceNamesToImplement.add(this.interfaceNames.getItem(i));
		}
		
		ArrayListIReset listOfVars = new ArrayListIReset(fields_info.length);
		for (i=0; i<fields_info.length; i++) {
			FindVarParams var = Field_Info.toFindVarParams(compiler, fields_info[i], typeNameInTemplatePair, true);
			if (var.fieldName.equals("list")) {
				int a;
				a=0;
				a++;
			}
			var.parent = c;
			listOfVars.add(var);
			
		}
		ArrayListIReset listOfFuncs = new ArrayListIReset(methods_info.length);
		for (i=0; i<methods_info.length; i++) {
			FindFunctionParams func = Method_Info.toFindFunctionParams(compiler, this, 
					methods_info[i], typeNameInTemplatePair, true, this.name, true);
			if (func.name.equals("Control")) {
				int a;
				a=0;
				a++;
			}
			func.parent = c;
			listOfFuncs.add(func);
		}
		
		
		c.listOfVariableParams = listOfVars;
		c.listOfFunctionParams = listOfFuncs;
		
		c.childClasses = this.listOfInnerClasses;
		
		c.loadWayOfFindClassParams = LoadWayOfFindClassParams.ByteCode;
		
		c.accessModifier = this.accessModifier;
		c.isInterface = this.accessModifier.isInterface;
		
		if (compiler.getShortName(c.name).equals("Dialog")) {
			int a;
			a=0;
			a++;
		}
		
	
		CompilerHelper.makeNoneStaticDefaultConstructorIfNoneStaticDefaultConstructorNotExist(compiler, c);
		
		if (CompilerHelper.requiresStaticConstructor(c)) {
			CompilerHelper.makeStaticDefaultConstructorIfStaticDefaultConstructorNotExist(compiler, c);
		}
		
		this.classParams = c;
		return c;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
		
	
	
	

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		this.absFilename = null;
		if (this.accessModifier!=null) {
			this.accessModifier.destroy();
			this.accessModifier = null;
		}
		if (this.attributes_info!=null) {
			int i;
			for (i=0; i<this.attributes_info.length; i++) {
				if (attributes_info[i]!=null) {
					this.attributes_info[i].destroy();
					this.attributes_info[i] = null;
				}
			}
			this.attributes_info = null;
		}
		if (this.classParams!=null) {
			this.classParams.destroy();
			this.classParams = null;
		}
		if (this.compiler!=null) {
			this.compiler.destroy();
			this.compiler = null;
		}
		if (this.constantTable!=null) {
			this.constantTable.reset();
			this.constantTable = null;
		}
		this.fullNameIncludingTemplateExceptArray = null;
		if (this.fields_info!=null) {
			int i;
			for (i=0; i<this.fields_info.length; i++) {
				this.fields_info[i].destroy();
				this.fields_info[i] = null;
			}
			this.fields_info = null;
		}
		this.interfaces = null;
		if (this.interfaceNames!=null) {
			this.interfaceNames.destroy();
			this.interfaceNames = null;
		}
		if (this.listOfInnerClasses!=null) {
			this.listOfInnerClasses.reset();
			this.listOfInnerClasses = null;
		}
		if (this.mBuffer!=null) {
			this.mBuffer.destroy();
			this.mBuffer = null;
		}
		if (this.methods_info!=null) {
			int i;
			for (i=0; i<this.methods_info.length; i++) {
				try {
				this.methods_info[i].destroy();
				}catch(Exception e) {
					int a;
					a=0;
					a++;
					e.printStackTrace();
				}
				this.methods_info[i] = null;
			}
			this.methods_info = null;
		}
		this.name = null;
		this.superName = null;
		this.typeNameInTemplatePair = null;
	}
	
		
}