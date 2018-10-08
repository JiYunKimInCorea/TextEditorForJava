package com.gsoft.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Color;

import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Compiler_types.AccessModifier;
import com.gsoft.common.Compiler_types.AccessModifier.AccessPermission;
import com.gsoft.common.Compiler_types.FindFunctionParams;
import com.gsoft.common.Compiler_types.FindVarParams;
import com.gsoft.common.Compiler_types.HighArray_CodeString;
import com.gsoft.common.Compiler_types.IReset;
import com.gsoft.common.Compiler_types.Language;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListChar;
import com.gsoft.common.Util.ArrayListIReset;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.Hashtable2_Object;
import com.gsoft.common.Util.HighArrayForReading_char;
import com.gsoft.common.gui.Control;

public class ByteCode_Types {
	
	static Hashtable2_Object hashTableInstructionSet;
	
	
	/**
	 * @param accessFlags
	 * @param type
	 * @return
	 */
	static AccessModifier toAccessModifier(short accessFlags, ClassFieldMethod type) {
		AccessModifier modifier = new AccessModifier(null, -1, -1);
		if (type==ClassFieldMethod.Class) {
			/*Flag Name Value Interpretation 
			ACC_PUBLIC 0x0001 Declared public; may be accessed from outside its package. 
			ACC_FINAL 0x0010 Declared final; no subclasses allowed. 
			ACC_SUPER 0x0020 Treat superclass methods specially when invoked by the invokespecial instruction. 
			ACC_INTERFACE 0x0200 Is an interface, not a class. 
			ACC_ABSTRACT 0x0400 Declared abstract; must not be instantiated. 
			ACC_SYNTHETIC 0x1000 Declared synthetic; Not present in the source code. 
			ACC_ANNOTATION 0x2000 Declared as an annotation type. 
			ACC_ENUM 0x4000 Declared as an enum type.*/
			if ((accessFlags & 0x0001)==0x0001)
				modifier.accessPermission = AccessPermission.Public;
			if ((accessFlags & 0x0002)==0x0002)
				modifier.accessPermission = AccessPermission.Private;
			if ((accessFlags & 0x0004)==0x0004)
				modifier.accessPermission = AccessPermission.Protected;
			if ((accessFlags & 0x0008)==0x0008)
				modifier.isStatic = true;
			if ((accessFlags & 0x0010)==0x0010)
				modifier.isFinal = true;
			if ((accessFlags & 0x0020)==0x0020)
				modifier.isSuper = true;
			if ((accessFlags & 0x0200)==0x0200)
				modifier.isInterface = true;
			if ((accessFlags & 0x0400)==0x0400)
				modifier.isAbstract = true;
			
			// class에서 사용시에는 enum타입, 필드에서 사용시는 enum의 원소이다.
			if ((accessFlags & 0x4000)==0x4000)
				modifier.isEnum = true;
		}
		else if (type==ClassFieldMethod.Field) {
			 /*Flag Name 		Value Interpretation 
			 * ACC_PUBLIC 		0x0001 Declared public; may be accessed from outside its package. 
			 * ACC_PRIVATE 		0x0002 Declared private; usable only within the defining class. 
			 * ACC_PROTECTED 	0x0004 Declared protected; may be accessed within subclasses. 
			 * ACC_STATIC 		0x0008 Declared static. 
			 * ACC_FINAL	 	0x0010 Declared final; no further assignment after initialization. 
			 * ACC_VOLATILE 	0x0040 Declared volatile; cannot be cached. 
			 * ACC_TRANSIENT 	0x0080 Declaredtransient; not written or read by a persistent object manager. 
			 * ACC_SYNTHETIC 	0x1000 Declared synthetic; Not present in the source code. 
			 * ACC_ENUM 		0x4000 Declared as an element of an enum.*/
			if ((accessFlags & 0x0001)==0x0001)
				modifier.accessPermission = AccessPermission.Public;
			if ((accessFlags & 0x0002)==0x0002)
				modifier.accessPermission = AccessPermission.Private;
			if ((accessFlags & 0x0004)==0x0004)
				modifier.accessPermission = AccessPermission.Protected;
			
		
			if ((accessFlags & 0x0008)==0x0008)
				modifier.isStatic = true;
			if ((accessFlags & 0x0010)==0x0010)
				modifier.isFinal = true;
			if ((accessFlags & 0x4000)==0x4000)
				modifier.isEnum = true;
		}
		else if (type==ClassFieldMethod.Method) {
			if ((accessFlags & 0x0001)==0x0001)
				modifier.accessPermission = AccessPermission.Public;
			if ((accessFlags & 0x0002)==0x0002)
				modifier.accessPermission = AccessPermission.Private;
			if ((accessFlags & 0x0004)==0x0004)
				modifier.accessPermission = AccessPermission.Protected;
			
		
			if ((accessFlags & 0x0008)==0x0008)
				modifier.isStatic = true;
			if ((accessFlags & 0x0010)==0x0010)
				modifier.isFinal = true;
			if ((accessFlags & 0x0020)==0x0020)
				modifier.isSynchronized = true;
			if ((accessFlags & 0x0100)==0x0100)
				modifier.isNative = true;
			if ((accessFlags & 0x0400)==0x0400)
				modifier.isAbstract = true;
		}
		return modifier;
		
		
	}
	
	enum ClassFieldMethod {
		Class,
		Field,
		Method
	}
	
	/** attribute StackMapTable*/
	static class Stack_map {		
		short attribute_name_index;
		int attribute_length;
		short number_of_entries;
		//stack_map_frame entries[number_of_entries];
	}
	
	static class Attribute_Info implements IReset {
		/** attribute_name_index와 attribute_length는 모든 속성의 공통적인 속성이다.*/
		short attribute_name_index;	
		/** attribute_name_index와 attribute_length는 모든 속성의 공통적인 속성이다.*/
		int attribute_length;
		String attribute_name;
		byte[] info; // attribute_length
		
		/** SourceFile Attribute 일 경우 0이 아니다.*/
		short sourcefile_index;
		/** SourceFile Attribute 일 경우 null 이 아니다.*/
		String sourcefile_name;
		
		/** InnerClasses Attribute 일 경우 0이 아니다.*/
		short number_of_classes;
		/** InnerClasses Attribute 일 경우 null 이 아니다.*/
		Class_Info[] classes; // number_of_classes
		
		/** Code Attribute 일 경우 null 이 아니다.*/
		Code_attribute codeAttribute;
		/** LineNumberTable Attribute 일 경우 null 이 아니다.*/
		LineNumberTable_attribute lineNumberTableAttribute;
		/** LocalVariableTable Attribute 일 경우 null 이 아니다.*/
		LocalVariableTable_attribute localVarTableAttribute;
		StackMapTable_attribute stackMapTableAttribute;
		
		/** Method_info의 attribute, 
		 * Attribute 가 Exceptions Attribute 일 경우 null 이 아니다.*/
		Exceptions_attribute exceptionsAttribute;
		
		
		
		static Attribute_Info read(PathClassLoader owner, InputStream is, ArrayList constantTable
				, boolean IsLittleEndian) throws IOException {
			Attribute_Info r = new Attribute_Info();
			r.attribute_name_index = IO.readShort(is, IsLittleEndian);
			r.attribute_name = ((CONSTANT_Utf8_info) constantTable.getItem(r.attribute_name_index)).str;
			
			if (r.attribute_name.equals("SourceFile")) {
				r.attribute_length = IO.readInt(is, IsLittleEndian);
				if (owner.readsCode==false) {
					is.skip(r.attribute_length);
				}
				else {		
					r.sourcefile_index = IO.readShort(is, IsLittleEndian);
					r.sourcefile_name = ((CONSTANT_Utf8_info) constantTable.getItem(r.sourcefile_index)).str;
				}
			}
			else if (r.attribute_name.equals("Exceptions")) {
				r.attribute_length = IO.readInt(is, IsLittleEndian);
				if (owner.readsCode==false) {
					is.skip(r.attribute_length);
				}
				else {		
					r.exceptionsAttribute = Exceptions_attribute.read(is, IsLittleEndian);
					r.exceptionsAttribute.attribute_name_index = r.attribute_name_index;
					r.exceptionsAttribute.attribute_length = r.attribute_length;
				}
			}
			else if (r.attribute_name.equals("InnerClasses")) {
				r.attribute_length = IO.readInt(is, IsLittleEndian);
				if (owner.readsCode==false) {
					is.skip(r.attribute_length);
				}
				else {		
					r.number_of_classes = IO.readShort(is, IsLittleEndian);
					r.classes = new Class_Info[r.number_of_classes];
					int i;
					CONSTANT_Class_info inner_class_info, outer_class_info = null;
					for (i=0; i<r.classes.length; i++) {
						r.classes[i] = new Class_Info();
						r.classes[i].inner_class_info_index = IO.readShort(is, IsLittleEndian);
						inner_class_info = ((CONSTANT_Class_info)constantTable.getItem(r.classes[i].inner_class_info_index));
						r.classes[i].innerClassName = ((CONSTANT_Utf8_info) constantTable.getItem(inner_class_info.name_index)).str;
						
						r.classes[i].outer_class_info_index = IO.readShort(is, IsLittleEndian);
						try {
						outer_class_info = ((CONSTANT_Class_info)constantTable.getItem(r.classes[i].outer_class_info_index));
						}catch(Exception e) {
							e.printStackTrace();
							int a;
							a=0;
							a++;
						}
						r.classes[i].outerClassName = ((CONSTANT_Utf8_info) constantTable.getItem(outer_class_info.name_index)).str;
						
						r.classes[i].inner_name_index = IO.readShort(is, IsLittleEndian);
						r.classes[i].simpleInnerName = ((CONSTANT_Utf8_info) constantTable.getItem(r.classes[i].inner_name_index)).str;
						
						r.classes[i].inner_class_access_flags = IO.readShort(is, IsLittleEndian);
						r.classes[i].accessModifier = ByteCode_Types.toAccessModifier(r.classes[i].inner_class_access_flags, ClassFieldMethod.Class);
					}
				}
				
				/*r.attribute_length = IO.readInt(is, IsLittleEndian);
				r.info = new byte[r.attribute_length];
				is.read(r.info);*/
			}
			// Code_Attribute는 Method_Info의 속성이다.
			else if (r.attribute_name.equals("Code")) {
				r.attribute_length = IO.readInt(is, IsLittleEndian);
				
				//r.info = new byte[r.attribute_length];
				//is.read(r.info);
				if (owner.readsCode==false) {
					is.skip(r.attribute_length);
				}
				else {				
					// r.attribute_length 만큼 읽어들인다.
					r.codeAttribute = Code_attribute.toCode_attribute(owner, is, 
							constantTable, IsLittleEndian);
					r.codeAttribute.attribute_name_index = r.attribute_name_index;
					r.codeAttribute.attribute_length = r.attribute_length;
				}
			}
			
			// LineNumberTable, LocalVariableTable, StackMapTable 은 
			// Code_Attribute의 속성이다.
			else if (r.attribute_name.equals("StackMapTable")) {
				r.attribute_length = IO.readInt(is, IsLittleEndian);
				
				if (owner.readsCode==false) {
					is.skip(r.attribute_length);
				}
				else {	
					// SimpleTest1의 test()의 info : [0, 1, -3, 0, 6, 1, 1]
					// r.attribute_length 만큼 읽어들인다.
					//r.info = new byte[r.attribute_length];
					//is.read(r.info);
					
					r.stackMapTableAttribute = StackMapTable_attribute.toStackMapTable_attribute(owner, is, 
							constantTable, IsLittleEndian);
					r.stackMapTableAttribute.attribute_name_index = r.attribute_name_index;
					r.stackMapTableAttribute.attribute_length = r.attribute_length;
				}
			}
			else if (r.attribute_name.equals("LineNumberTable")) {
				r.attribute_length = IO.readInt(is, IsLittleEndian);
				
				if (owner.readsCode==false) {
					is.skip(r.attribute_length);
				}
				else {				
					// r.attribute_length 만큼 읽어들인다.
					r.lineNumberTableAttribute = LineNumberTable_attribute.toLineNumberTable_attribute(
							is, constantTable, IsLittleEndian);
					r.lineNumberTableAttribute.attribute_name_index = r.attribute_name_index;
					r.lineNumberTableAttribute.attribute_length = r.attribute_length;
				}
			}
			else if (r.attribute_name.equals("LocalVariableTable")) {
				r.attribute_length = IO.readInt(is, IsLittleEndian);
				
				if (owner.readsCode==false) {
					is.skip(r.attribute_length);
				}
				else {				
					// r.attribute_length 만큼 읽어들인다.
					r.localVarTableAttribute = LocalVariableTable_attribute.toLocalVariableTable_attribute(
							is, constantTable, IsLittleEndian);
					r.localVarTableAttribute.attribute_name_index = r.attribute_name_index;
					r.localVarTableAttribute.attribute_length = r.attribute_length;
				}
			}
			else {
				r.attribute_length = IO.readInt(is, IsLittleEndian);
				if (owner.readsCode==false) {
					is.skip(r.attribute_length);
				}
				else {		
					r.info = new byte[r.attribute_length];
					is.read(r.info);
				}
			}
			return r;
			
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.classes!=null) {
				int i;
				for (i=0; i<this.classes.length; i++) {
					this.classes[i].destroy();
					this.classes[i] = null;
				}
				this.classes = null;
			}
			if (this.info!=null) {
				this.info = null;
			}
			this.attribute_name = null;
			if (codeAttribute!=null) {
				codeAttribute.destroy();
				codeAttribute = null;
			}
			if (lineNumberTableAttribute!=null) {
				lineNumberTableAttribute.destroy();
				lineNumberTableAttribute = null;
			}
			if (localVarTableAttribute!=null) {
				localVarTableAttribute.destroy();
				localVarTableAttribute = null;
			}
			if (exceptionsAttribute!=null) {
				exceptionsAttribute.destroy();
				exceptionsAttribute = null;
			}
			if (stackMapTableAttribute!=null) {
				stackMapTableAttribute.destroy();
				stackMapTableAttribute = null;
			}
		}
	}
	
	/** 내부클래스를 표현하는 클래스, 
	 * InnerClasses Attribute를 갖을 경우 내부클래스 하나를 표현하기 위해 사용한다.
	 * Attribute_Info의 read()의 InnerClasses부분을 참조한다.*/
	static class Class_Info implements IReset {
		/** 0이 아니라면 constant pool 내 이 인덱스의 엔트리는 CONSTANT_Class_info이고,
		 * 0이면 내부클래스가 아니다.	 */
		short inner_class_info_index;
		String innerClassName;
		/** 0이 아니라면 constant pool 내 이 인덱스의 엔트리는 CONSTANT_Class_info이고,
		 * 0이면 외부클래스가 없다.	 */
		short outer_class_info_index;
		String outerClassName;
		/** innerClassName의 짧은 이름을 갖는 CONSTANT_Utf8_info 를 가리키는 인덱스*/
		short inner_name_index;
		/** innerClassName 의 short 이름*/
		String simpleInnerName;
		short inner_class_access_flags;
		AccessModifier accessModifier;
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			innerClassName = null;
			outerClassName = null;
			simpleInnerName = null;
			if (accessModifier!=null) {
				accessModifier.destroy();
				accessModifier = null;
			}
		}
	}
	
	
	
	static class CONSTANT_String_info { 
		byte tag; 
		short string_index; // CONSTANT_Utf8_info의 constantTable 내 인덱스
		ArrayList constantPool;
		String str;
		/** 바이트코드를 생성할 때 필요하다.*/
		CONSTANT_String_info(String str) {
			this.str = str;
		}
		CONSTANT_String_info(byte tag, short string_index, ArrayList constantPool) {
			this.tag = tag;
			this.string_index = string_index;
			this.constantPool = constantPool;
		}
		public String toString() {
			if (constantPool!=null) {
				String str = ((CONSTANT_Utf8_info) constantPool.getItem(string_index)).str;
				return str;
			}
			else return str;
		}
	}
	
	static class CONSTANT_Class_info { 
		byte tag; //CONSTANT_Class (7)
		short name_index; // CONSTANT_Utf8_info의 constantTable 내 인덱스
		ArrayList constantPool;
		String className;
		Compiler compiler;
		
		CONSTANT_Class_info(byte tag, short name_index, ArrayList constantPool) {
			this.tag = tag;
			this.name_index = name_index;
			this.constantPool = constantPool;
		}
		/** 바이트코드를 생성할 때 필요하다.*/
		CONSTANT_Class_info(String className, Compiler compiler) {
			this.className = className;
			this.compiler = compiler;
		}
		public String toString() {
			if (constantPool!=null) {
				className = ((CONSTANT_Utf8_info) constantPool.getItem(name_index)).str;
				return className;
			}
			else return className;
		}
	}
	
	static class CONSTANT_Long_info { 
		byte tag; 
		long l;
		
		/** 바이트코드를 생성할 때 필요*/
		CONSTANT_Long_info(long l) {
			this.l = l;
		}
		
		CONSTANT_Long_info(byte tag, long l) {
			this.tag = tag;
			this.l = l;
		}
		public String toString() {
			return String.valueOf(l);
		}
	}
	
	static class CONSTANT_Double_info { 
		byte tag; 
		double d;
		
		/** 바이트코드를 생성할 때 필요*/
		CONSTANT_Double_info(double d) {
			this.d = d;
		}
		
		CONSTANT_Double_info(byte tag, double d) {
			this.tag = tag;
			this.d = d;
		}
		public String toString() {
			return String.valueOf(d);
		}
	}
	
	static class CONSTANT_Float_info { 
		byte tag; 
		float f; //IEEE 754 floating point single format
		
		/** 바이트코드를 생성할 때 필요*/
		CONSTANT_Float_info(float f) {
			this.f = f;
		}
		
		CONSTANT_Float_info(byte tag, float f) {
			this.tag = tag;
			this.f = f;
		}
		public String toString() {
			return String.valueOf(f);
		}
	}
	
	static class CONSTANT_Integer_info { 
		byte tag; 
		int integer; // big-endian (high byte first) order
		
		/** 바이트코드를 생성할 때 필요*/
		CONSTANT_Integer_info(int integer) {
			this.integer = integer;
		}
		
		CONSTANT_Integer_info(byte tag, int integer) {
			this.tag = tag;
			this.integer = integer;
		}
		public String toString() {
			return String.valueOf(integer);
		}
	}
	
	static class CONSTANT_Utf8_info { 
		byte tag; 
		short length; // 바이트수
		String str; //utf-8,  not null-terminated
		
		CONSTANT_Utf8_info(byte tag, short length, String str) {
			this.tag = tag;
			this.length = length;
			this.str = str;
		}
		public String toString() {
			return str;
		}
	}
	
	public static class ByteCodeInstruction {
		public String mnemonic;
		/** 해시테이블의 키가 된다.*/
		public short opcodeHexa;
		/**"lookupswitch" (4개이상), "tableswitch" (4개이상), "wide" (3개나 5개) 은 
		 * 가변 인덱스를 갖는다.*/
		public short numOfOtherBytes;
		/** numOfOtherBytes 개의 다른 바이트들을 갖는다. 
		 * Code_Attribute 의 toString()에서 호출한다.*/
		public ArrayList indices;
		public String messageOfIndices;
		public String message;
		public boolean hasError;
		
		/** tableswitch의 경우 패딩의 개수는 원래 0-3까지 4개이나 더 많을 수도 있다.*/
		public short countOfPadding;
		
		ByteCodeInstruction(String mnemonic, short opcodeHexa, short numOfOtherBytes) {
			this.mnemonic = mnemonic;
			this.opcodeHexa = opcodeHexa;
			this.numOfOtherBytes = numOfOtherBytes;
			if (numOfOtherBytes!=0) {
				indices = new ArrayList(numOfOtherBytes);
			}
		}
		
		ByteCodeInstruction(String mnemonic, short opcodeHexa, short numOfOtherBytes, String message) {
			this.mnemonic = mnemonic;
			this.opcodeHexa = opcodeHexa;
			this.numOfOtherBytes = numOfOtherBytes;
			if (numOfOtherBytes!=0) {
				indices = new ArrayList(numOfOtherBytes);
			}
			this.message = message;
		}
		
		/** instruction 을 복사하는 생성자, 
		 * 예를들어 goto 문은 쓰임에 따라 인덱스가 달라지기 때문에 
		 * indices 를 새로 만들어야 한다.*/
		ByteCodeInstruction(ByteCodeInstruction instruction) {
			this.mnemonic = instruction.mnemonic;
			this.opcodeHexa = instruction.opcodeHexa;
			this.numOfOtherBytes = instruction.numOfOtherBytes;
			if (numOfOtherBytes!=0) {
				indices = new ArrayList(numOfOtherBytes);
			}
			this.message = instruction.message;
		}
		
		/**"lookupswitch" (4개이상), "tableswitch" (4개이상), "wide" (3개나 5개) 은 
		 * 가변 인덱스를 갖는다. Code_attribute의 toCodeString()에서 인덱스 개수가 결정된다.<br>
		 * 
		 * tableswitch<br>
		   4+: [0-3 bytes padding],<br> 
		   case의 조건들 : defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4,<br> 
		   jump offsets : lowbyte1, lowbyte2, lowbyte3, lowbyte4, highbyte1, highbyte2, highbyte3, highbyte4,<br> 
			...<br>
				
			switch (i) {<br>
			      case 0:  return  0;<br>
			      case 1:  return  1;<br>
			      case 2:  return  2;<br>
			      default: return -1;<br>
			}<br>
				<br>
		
			Type Description 
			u1   tableswitch opcode = 0xAA (170) 
			-    0-3 bytes of padding ... 
			s4   default_offset 
			s4   <low> 
			s4   <low> + N - 1 
			s4   offset_1 
			s4   offset_2 
			... 
			... 
			s4   offset_N 
			* */	
		boolean hasVariableIndices() {
			if (mnemonic.equals("lookupswitch") ||  mnemonic.equals("tableswitch") || 
					mnemonic.equals("wide")) return true;
			return false;
		}
		
		
		
		/**"lookupswitch" (4개이상), "tableswitch" (4개이상), "wide" (3개나 5개) 은 
		 * 가변 인덱스임을 주의한다.
		 * instruction 의 인덱스 정보에 에러가 있으면 
		 * instruction 의 color 를 Compiler.keywordColor로 바꾼다.*/
		public CodeString toCodeString() {
			CodeString r = null;
			int textColor = Compiler.textColor;
			if (this.hasError) textColor = Compiler.keywordColor;
			if (mnemonic.equals("invokeinterface")) {
				r = new CodeString(mnemonic, Compiler.funcUseColor);
			}
			else {
				r = new CodeString(mnemonic, textColor);
			}
			String otherBytes = "";
			for (int i=0; i<numOfOtherBytes; i++) {
				Object o = indices.getItem(i);
				if (o instanceof Short) {
					Short otherByte = (Short)o;
					otherBytes += " #" + otherByte;
				}
				else if (o instanceof Integer) {
					Integer otherByte = (Integer)o;
					otherBytes += " #" + otherByte;
				}				
			}
			r = r.concate(new CodeString(otherBytes,textColor));
			return r;
		}
		
		/**"lookupswitch" (4개이상), "tableswitch" (4개이상), "wide" (3개나 5개) 은 
		 * 가변 인덱스임을 주의한다.*/
		int getLenOfIndices() {
			return this.numOfOtherBytes;
		}
	}
	
	/** no name cb-fd
		"lookupswitch" (4개이상), "tableswitch" (4개이상), "wide" (3개나 5개) 은 가변 인덱스이다.*/
	public static ByteCodeInstruction[] instructionSet = 
	{
		new ByteCodeInstruction("aaload", (short)0x32, (short)0, "arrayref, index → value\nload onto the stack a reference from an array"),
		new ByteCodeInstruction("aastore", (short)0x53, (short)0, "arrayref, index, value →\nstore into a reference in an array"),
		new ByteCodeInstruction("aconst_null", (short)0x01, (short)0, "→ null\npush a null reference onto the stack"),
		
		new ByteCodeInstruction("aload", (short)0x19, (short)1, "→ objectref\nload a reference onto the stack from a local variable #index"),
		new ByteCodeInstruction("aload_0", (short)0x2a, (short)0, "→ objectref\nload a reference onto the stack from local variable 0"),
		new ByteCodeInstruction("aload_1", (short)0x2b, (short)0, "→ objectref\nload a reference onto the stack from local variable 1"),
		new ByteCodeInstruction("aload_2", (short)0x2c, (short)0, "→ objectref\nload a reference onto the stack from local variable 2"),
		new ByteCodeInstruction("aload_3", (short)0x2d, (short)0, "→ objectref\nload a reference onto the stack from local variable 3"),
		
		new ByteCodeInstruction("anewarray", (short)0xbd, (short)2, "count → arrayref\ncreate a new array of references of length count and component type identified by the class reference index (indexbyte1 << 8 + indexbyte2) in the constant pool"),
		new ByteCodeInstruction("areturn", (short)0xb0, (short)0, "objectref → [empty]\nreturn a reference from a method"),
		new ByteCodeInstruction("arraylength", (short)0xbe, (short)0, "arrayref → length\nget the length of an array"),
		
		new ByteCodeInstruction("astore", (short)0x3a, (short)1, "objectref →\nstore a reference into a local variable #index"),
		new ByteCodeInstruction("astore_0", (short)0x4b, (short)0, "objectref →\nstore a reference into a local variable 0"),
		new ByteCodeInstruction("astore_1", (short)0x4c, (short)0, "objectref →\nstore a reference into a local variable 1"),
		new ByteCodeInstruction("astore_2", (short)0x4d, (short)0, "objectref →\nstore a reference into a local variable 2"),
		new ByteCodeInstruction("astore_3", (short)0x4e, (short)0, "objectref →\nstore a reference into a local variable 3"),
		
		new ByteCodeInstruction("athrow", (short)0xbf, (short)0, "objectref → [empty], objectref\nthrows an error or exception (notice that the rest of the stack is cleared, leaving only a reference to the Throwable)"),
		
		new ByteCodeInstruction("baload", (short)0x33, (short)0, "arrayref, index → value\nload a byte or Boolean value from an array"),
		new ByteCodeInstruction("bastore", (short)0x54, (short)0, "arrayref, index, value →\nstore a byte or Boolean value into an array"),
		new ByteCodeInstruction("bipush", (short)0x10, (short)1, "→ value\npush a byte onto the stack as an integer value"),
		new ByteCodeInstruction("breakpoint", (short)0xca, (short)0, "reserved for breakpoints in Java debuggers; should not appear in any class file"),
		
		new ByteCodeInstruction("caload", (short)0x34, (short)0, "arrayref, index → value\nload a char from an array"),
		new ByteCodeInstruction("castore", (short)0x55, (short)0, "arrayref, index, value →\nstore a char into an array"),
		new ByteCodeInstruction("checkcast", (short)0xc0, (short)2, "objectref → objectref\nchecks whether an objectref is of a certain type, the class reference of which is in the constant pool at index (indexbyte1 << 8 + indexbyte2)"),
		
		
		new ByteCodeInstruction("d2f", (short)0x90, (short)0, "value → result\nconvert a double to a float"),
		new ByteCodeInstruction("d2i", (short)0x8e, (short)0, "value → result\nconvert a double to an int"),
		new ByteCodeInstruction("d2l", (short)0x8f, (short)0, "value → result\nconvert a double to a long"),
		new ByteCodeInstruction("dadd", (short)0x63, (short)0, "value1, value2 → result\nadd two doubles"),
		
		new ByteCodeInstruction("daload", (short)0x31, (short)0, "arrayref, index → value\nload a double from an array"),
		new ByteCodeInstruction("dastore", (short)0x52, (short)0, "arrayref, index, value →\nstore a double into an array"),
		new ByteCodeInstruction("dcmpg", (short)0x98, (short)0, "value1, value2 → result\ncompare two doubles"),
		new ByteCodeInstruction("dcmpl", (short)0x97, (short)0, "value1, value2 → result\ncompare two doubles"),
		
		new ByteCodeInstruction("dconst_0", (short)0x0e, (short)0, "→ 0.0\npush the constant 0.0 onto the stack"),
		new ByteCodeInstruction("dconst_1", (short)0x0f, (short)0, "→ 1.0\npush the constant 1.0 onto the stack"),
		
		new ByteCodeInstruction("ddiv", (short)0x6f, (short)0, "value1, value2 → result\ndivide two doubles"),
		
		new ByteCodeInstruction("dload", (short)0x18, (short)1, "→ value\nload a double value from a local variable #index"),
		new ByteCodeInstruction("dload_0", (short)0x26, (short)0, "→ value\nload a double value from a local variable 0"),
		new ByteCodeInstruction("dload_1", (short)0x27, (short)0, "→ value\nload a double value from a local variable 1"),
		new ByteCodeInstruction("dload_2", (short)0x28, (short)0, "→ value\nload a double value from a local variable 2"),
		new ByteCodeInstruction("dload_3", (short)0x29, (short)0, "→ value\nload a double value from a local variable 3"),
		
		new ByteCodeInstruction("dmul", (short)0x6b, (short)0, "value1, value2 → result\nmultiply two doubles"),
		new ByteCodeInstruction("dneg", (short)0x77, (short)0, "value → result\nnegate a double"),
		new ByteCodeInstruction("drem", (short)0x73, (short)0, "value1, value2 → result\nget the remainder from a division between two doubles"),
		new ByteCodeInstruction("dreturn", (short)0xaf, (short)0, "value → [empty]\nreturn a double from a method"),
		
		new ByteCodeInstruction("dstore", (short)0x39, (short)1, "value →\nstore a double value into a local variable #index"),
		new ByteCodeInstruction("dstore_0", (short)0x47, (short)0, "value →\nstore a double value into a local variable 0"),
		new ByteCodeInstruction("dstore_1", (short)0x48, (short)0, "value →\nstore a double value into a local variable 1"),
		new ByteCodeInstruction("dstore_2", (short)0x49, (short)0, "value →\nstore a double value into a local variable 2"),
		new ByteCodeInstruction("dstore_3", (short)0x4a, (short)0, "value →\nstore a double value into a local variable 3"),
		
		new ByteCodeInstruction("dsub", (short)0x67, (short)0, "value1, value2 → result\nsubtract a double from another"),
		
		new ByteCodeInstruction("dup", (short)0x59, (short)0, "value → value, value\nduplicate the value on top of the stack"),
		new ByteCodeInstruction("dup_x1", (short)0x5a, (short)0, "value2, value1 → value1, value2, value1\ninsert a copy of the top value into the stack two values from the top. value1 and value2 must not be of the type double or long."),
		new ByteCodeInstruction("dup_x2", (short)0x5b, (short)0, "value3, value2, value1 → value1, value3, value2, value1\ninsert a copy of the top value into the stack two (if value2 is double or long it takes up the entry of value3, too) or three values (if value2 is neither double nor long) from the top"),
		new ByteCodeInstruction("dup2", (short)0x5c, (short)0, "{value2, value1} → {value2, value1}, {value2, value1}\nduplicate top two stack words (two values, if value1 is not double nor long; a single value, if value1 is double or long)"),
		new ByteCodeInstruction("dup2_x1", (short)0x5d, (short)0, "value3, {value2, value1} → {value2, value1}, value3, {value2, value1}\nduplicate two words and insert beneath third word (see explanation above)"),
		new ByteCodeInstruction("dup2_x2", (short)0x5e, (short)0, "{value4, value3}, {value2, value1} → {value2, value1}, {value4, value3}, {value2, value1}\nduplicate two words and insert beneath fourth word"),
		
		new ByteCodeInstruction("f2d", (short)0x8d, (short)0, "value → result\nconvert a float to a double"),
		new ByteCodeInstruction("f2i", (short)0x8b, (short)0, "value → result\nconvert a float to an int"),
		new ByteCodeInstruction("f2l", (short)0x8c, (short)0, "value → result\nconvert a float to a long"),
		
		new ByteCodeInstruction("fadd", (short)0x62, (short)0, "value1, value2 → result\nadd two floats"),
		new ByteCodeInstruction("faload", (short)0x30, (short)0, "arrayref, index → value\nload a float from an array"),
		new ByteCodeInstruction("fastore", (short)0x51, (short)0, "arrayref, index, value →\nstore a float in an array"),
		new ByteCodeInstruction("fcmpg", (short)0x96, (short)0, "value1, value2 → result\ncompare two floats"),
		new ByteCodeInstruction("fcmpl", (short)0x95, (short)0, "value1, value2 → result\ncompare two floats"),
		
		new ByteCodeInstruction("fconst_0", (short)0x0b, (short)0, "→ 0.0f\npush 0.0f on the stack"),
		new ByteCodeInstruction("fconst_1", (short)0x0c, (short)0, "→ 1.0f\npush 1.0f on the stack"),
		new ByteCodeInstruction("fconst_2", (short)0x0d, (short)0, "→ 2.0f\npush 2.0f on the stack"),
		
		new ByteCodeInstruction("fdiv", (short)0x6e, (short)0, "value1, value2 → result\ndivide two floats"),
		
		new ByteCodeInstruction("fload", (short)0x17, (short)1, "→ value\nload a float value from a local variable #index"),
		new ByteCodeInstruction("fload_0", (short)0x22, (short)0, "→ value\nload a float value from a local variable 0"),
		new ByteCodeInstruction("fload_1", (short)0x23, (short)0, "→ value\nload a float value from a local variable 1"),
		new ByteCodeInstruction("fload_2", (short)0x24, (short)0, "→ value\nload a float value from a local variable 2"),
		new ByteCodeInstruction("fload_3", (short)0x25, (short)0, "→ value\nload a float value from a local variable 3"),
		
		new ByteCodeInstruction("fmul", (short)0x6a, (short)0, "value1, value2 → result\nmultiply two floats"),
		new ByteCodeInstruction("fneg", (short)0x76, (short)0, "value → result\nnegate a float"),
		new ByteCodeInstruction("frem", (short)0x72, (short)0, "value1, value2 → result\nget the remainder from a division between two floats"),
		new ByteCodeInstruction("freturn", (short)0xae, (short)0, "value → [empty]\nreturn a float"),
		
		new ByteCodeInstruction("fstore", (short)0x38, (short)1, "value →\nstore a float value into a local variable #index"),
		new ByteCodeInstruction("fstore_0", (short)0x43, (short)0, "value →\nstore a float value into a local variable 0"),
		new ByteCodeInstruction("fstore_1", (short)0x44, (short)0, "value →\nstore a float value into a local variable 1"),
		new ByteCodeInstruction("fstore_2", (short)0x45, (short)0, "value →\nstore a float value into a local variable 2"),
		new ByteCodeInstruction("fstore_3", (short)0x46, (short)0, "value →\nstore a float value into a local variable 3"),
		
		new ByteCodeInstruction("fsub", (short)0x66, (short)0, "value1, value2 → result\nsubtract two floats"),
		
		new ByteCodeInstruction("getfield", (short)0xb4, (short)2, "objectref → value\nget a field value of an object objectref, where the field is identified by field reference in the constant pool index (index1 << 8 + index2)"),
		new ByteCodeInstruction("getstatic", (short)0xb2, (short)2, "→ value\nget a static field value of a class, where the field is identified by field reference in the constant pool index (index1 << 8 + index2)"),
		new ByteCodeInstruction("goto", (short)0xa7, (short)2, "goes to another instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("goto_w", (short)0xc8, (short)4, "goes to another instruction at branchoffset (signed int constructed from unsigned bytes branchbyte1 << 24 + branchbyte2 << 16 + branchbyte3 << 8 + branchbyte4)"),
		
		new ByteCodeInstruction("i2b", (short)0x91, (short)0, "value → result\nconvert an int into a byte"),
		new ByteCodeInstruction("i2c", (short)0x92, (short)0, "value → result\nconvert an int into a character"),
		new ByteCodeInstruction("i2d", (short)0x87, (short)0, "value → result\nconvert an int into a double"),
		new ByteCodeInstruction("i2f", (short)0x86, (short)0, "value → result\nconvert an int into a float"),
		new ByteCodeInstruction("i2l", (short)0x85, (short)0, "value → result\nconvert an int into a long"),
		new ByteCodeInstruction("i2s", (short)0x93, (short)0, "value → result\nconvert an int into a short"),
		
		new ByteCodeInstruction("iadd", (short)0x60, (short)0, "value1, value2 → result\nadd two ints"),
		new ByteCodeInstruction("iaload", (short)0x2e, (short)0, "arrayref, index → value\nload an int from an array"),
		new ByteCodeInstruction("iand", (short)0x7e, (short)0, "value1, value2 → result\nperform a bitwise and on two integers"),
		new ByteCodeInstruction("iastore", (short)0x4f, (short)0, "arrayref, index, value → \nstore an int into an array"),
		
		new ByteCodeInstruction("iconst_m1", (short)0x02, (short)0, "→ -1\nload the int value -1 onto the stack"),
		new ByteCodeInstruction("iconst_0", (short)0x03, (short)0, "→ 0\nload the int value 0 onto the stack"),
		new ByteCodeInstruction("iconst_1", (short)0x04, (short)0, "→ 1\nload the int value 1 onto the stack"),
		new ByteCodeInstruction("iconst_2", (short)0x05, (short)0, "→ 2\nload the int value 2 onto the stack"),
		new ByteCodeInstruction("iconst_3", (short)0x06, (short)0, "→ 3\nload the int value 3 onto the stack"),
		new ByteCodeInstruction("iconst_4", (short)0x07, (short)0, "→ 4\nload the int value 4 onto the stack"),
		new ByteCodeInstruction("iconst_5", (short)0x08, (short)0, "→ 5\nload the int value 5 onto the stack"),
		
		new ByteCodeInstruction("idiv", (short)0x6c, (short)0, "value1, value2 → result\ndivide two integers"),
		
		new ByteCodeInstruction("if_acmpeq", (short)0xa5, (short)2, "value1, value2 →\nif references are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("if_acmpne", (short)0xa6, (short)2, "value1, value2 →\nif references are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("if_icmpeq", (short)0x9f, (short)2, "value1, value2 →\nif ints are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("if_icmpge", (short)0xa2, (short)2, "value1, value2 →\nif value1 is greater than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("if_icmpgt", (short)0xa3, (short)2, "value1, value2 →\nif value1 is greater than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("if_icmple", (short)0xa4, (short)2, "value1, value2 →\nif value1 is less than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("if_icmplt", (short)0xa1, (short)2, "value1, value2 →\nif value1 is less than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("if_icmpne", (short)0xa0, (short)2, "value1, value2 →\nif ints are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		
		new ByteCodeInstruction("ifeq", (short)0x99, (short)2, "value →\nif value is 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("ifge", (short)0x9c, (short)2, "value →\nif value is greater than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("ifgt", (short)0x9d, (short)2, "value →\nif value is greater than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("ifle", (short)0x9e, (short)2, "value →\nif value is less than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("iflt", (short)0x9b, (short)2, "value →\nif value is less than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("ifne", (short)0x9a, (short)2, "value →\nif value is not 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("ifnonnull", (short)0xc7, (short)2, "value →\nif value is not null, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		new ByteCodeInstruction("ifnull", (short)0xc6, (short)2, "value →\nif value is null, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)"),
		
		new ByteCodeInstruction("iinc", (short)0x84, (short)2, "[No change]\nincrement local variable #index by signed byte const"),
		
		new ByteCodeInstruction("iload", (short)0x15, (short)1, "→ value\nload an int value from a local variable #index"),
		new ByteCodeInstruction("iload_0", (short)0x1a, (short)0, "→ value\nload an int value from a local variable 0"),
		new ByteCodeInstruction("iload_1", (short)0x1b, (short)0, "→ value\nload an int value from a local variable 1"),
		new ByteCodeInstruction("iload_2", (short)0x1c, (short)0, "→ value\nload an int value from a local variable 2"),
		new ByteCodeInstruction("iload_3", (short)0x1d, (short)0, "→ value\nload an int value from a local variable 3"),
		
		new ByteCodeInstruction("impdep1", (short)0xfe, (short)0, "reserved for implementation-dependent operations within debuggers; should not appear in any class file"),
		new ByteCodeInstruction("impdep2", (short)0xff, (short)0, "reserved for implementation-dependent operations within debuggers; should not appear in any class file"),
		
		new ByteCodeInstruction("imul", (short)0x68, (short)0, "value1, value2 → result\nmultiply two integers"),
		new ByteCodeInstruction("ineg", (short)0x74, (short)0, "value → result\nnegate int"),
		
		new ByteCodeInstruction("instanceof", (short)0xc1, (short)2, "objectref → result\ndetermines if an object objectref is of a given type, identified by class reference index in constant pool (indexbyte1 << 8 + indexbyte2)"),
		
		new ByteCodeInstruction("invokedynamic", (short)0xba, (short)4, "[arg1, [arg2 ...]] →\ninvokes a dynamic method identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)"),
		new ByteCodeInstruction("invokeinterface", (short)0xb9, (short)4, "objectref, [arg1, arg2, ...] →\ninvokes an interface method on object objectref, where the interface method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)"),
		new ByteCodeInstruction("invokespecial", (short)0xb7, (short)2, "objectref, [arg1, arg2, ...] →\ninvoke instance method on object objectref, where the method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)"),
		new ByteCodeInstruction("invokestatic", (short)0xb8, (short)2, "[arg1, arg2, ...] →\ninvoke a static method, where the method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)"),
		new ByteCodeInstruction("invokevirtual", (short)0xb6, (short)2, "objectref, [arg1, arg2, ...] →\ninvoke virtual method on object objectref, where the method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)"),
		
	
		new ByteCodeInstruction("ior", (short)0x80, (short)0, "value1, value2 → result\nbitwise int or"),
		new ByteCodeInstruction("irem", (short)0x70, (short)0, "value1, value2 → result\nlogical int remainder"),
		new ByteCodeInstruction("ireturn", (short)0xac, (short)0, "value → [empty]\nreturn an integer from a method"),
		new ByteCodeInstruction("ishl", (short)0x78, (short)0, "value1, value2 → result\nint shift left"),
		new ByteCodeInstruction("ishr", (short)0x7a, (short)0, "value1, value2 → result\nint arithmetic shift right"),
		
		new ByteCodeInstruction("istore", (short)0x36, (short)1, "value →\nstore int value into variable #index"),
		new ByteCodeInstruction("istore_0", (short)0x3b, (short)0, "value →\nstore int value into variable 0"),
		new ByteCodeInstruction("istore_1", (short)0x3c, (short)0, "value →\nstore int value into variable 1"),
		new ByteCodeInstruction("istore_2", (short)0x3d, (short)0, "value →\nstore int value into variable 2"),
		new ByteCodeInstruction("istore_3", (short)0x3e, (short)0, "value →\nstore int value into variable 3"),
		
		new ByteCodeInstruction("isub", (short)0x64, (short)0, "value1, value2 → result\nint subtract"),
		
		new ByteCodeInstruction("iushr", (short)0x7c, (short)0, "value1, value2 → result\nint logical shift right"),
		new ByteCodeInstruction("ixor", (short)0x82, (short)0, "value1, value2 → result\nint xor"),
		
		new ByteCodeInstruction("jsr", (short)0xa8, (short)2, "→ address\njump to subroutine at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2) and place the return address on the stack"),
		new ByteCodeInstruction("jsr_w", (short)0xc9, (short)4, "→ address\njump to subroutine at branchoffset (signed int constructed from unsigned bytes branchbyte1 << 24 + branchbyte2 << 16 + branchbyte3 << 8 + branchbyte4) and place the return address on the stack"),
		
		new ByteCodeInstruction("l2d", (short)0x8a, (short)0, "value → result\nconvert a long to a double"),
		new ByteCodeInstruction("l2f", (short)0x89, (short)0, "value → result\nconvert a long to a float"),
		new ByteCodeInstruction("l2i", (short)0x88, (short)0, "value → result\nconvert a long to a int"),
		
		new ByteCodeInstruction("ladd", (short)0x61, (short)0, "value1, value2 → result\nadd two longs"),
		new ByteCodeInstruction("laload", (short)0x2f, (short)0, "arrayref, index → value\nload a long from an array"),
		new ByteCodeInstruction("land", (short)0x7f, (short)0, "value1, value2 → result\nbitwise and of two longs"),
		new ByteCodeInstruction("lastore", (short)0x50, (short)0, "arrayref, index, value →\nstore a long to an array"),		
		new ByteCodeInstruction("lcmp", (short)0x94, (short)0, "value1, value2 → result\ncompare two longs values"),
		
		new ByteCodeInstruction("lconst_0", (short)0x09, (short)0, "→ 0L\npush the long 0 onto the stack"),
		new ByteCodeInstruction("lconst_1", (short)0x0a, (short)0, "→ 1L\npush the long 1 onto the stack"),
		
		new ByteCodeInstruction("ldc", (short)0x12, (short)1, "→ value\npush a constant #index from a constant pool (String, int or float) onto the stack"),
		new ByteCodeInstruction("ldc_w", (short)0x13, (short)2, "→ value\npush a constant #index from a constant pool (String, int or float) onto the stack (wide index is constructed as indexbyte1 << 8 + indexbyte2)"),
		new ByteCodeInstruction("ldc2_w", (short)0x14, (short)2, "→ value\npush a constant #index from a constant pool (double or long) onto the stack (wide index is constructed as indexbyte1 << 8 + indexbyte2)"),
		
		new ByteCodeInstruction("ldiv", (short)0x6d, (short)0, "value1, value2 → result\ndivide two longs"),
		
		new ByteCodeInstruction("lstore", (short)0x16, (short)1, "→ value\nload a long value from a local variable #index"),
		new ByteCodeInstruction("lstore_0", (short)0x1e, (short)0, "→ value\nload a long value from a local variable 0"),
		new ByteCodeInstruction("lstore_1", (short)0x1f, (short)0, "→ value\nload a long value from a local variable 1"),
		new ByteCodeInstruction("lstore_2", (short)0x20, (short)0, "→ value\nload a long value from a local variable 2"),
		new ByteCodeInstruction("lstore_3", (short)0x21, (short)0, "→ value\nload a long value from a local variable 3"),
		
		new ByteCodeInstruction("lmul", (short)0x69, (short)0, "value1, value2 → result\nmultiply two longs"),
		new ByteCodeInstruction("lneg", (short)0x75, (short)0, "value → result\nnegate a long"),
		
		new ByteCodeInstruction("lookupswitch", (short)0xab, (short)4, "key →\na target address is looked up from a table using a key and execution continues from the instruction at that address"),
		
		new ByteCodeInstruction("lor", (short)0x81, (short)0, "value1, value2 → result\nbitwise or of two longs"),
		new ByteCodeInstruction("lrem", (short)0x71, (short)0, "value1, value2 → result\nremainder of division of two longs"),
		new ByteCodeInstruction("lreturn", (short)0xad, (short)0, "value → [empty]\nreturn a long value"),
		new ByteCodeInstruction("lshl", (short)0x79, (short)0, "value1, value2 → result\nbitwise shift left of a long value1 by int value2 positions"),
		new ByteCodeInstruction("lshr", (short)0x7b, (short)0, "value1, value2 → result\nbitwise shift right of a long value1 by int value2 positions"),
		
		new ByteCodeInstruction("lstore", (short)0x37, (short)1, "value →\nstore a long value in a local variable #index"),
		new ByteCodeInstruction("lstore_0", (short)0x3f, (short)0, "value →\nstore a long value in a local variable 0"),
		new ByteCodeInstruction("lstore_1", (short)0x40, (short)0, "value →\nstore a long value in a local variable 1"),
		new ByteCodeInstruction("lstore_2", (short)0x41, (short)0, "value →\nstore a long value in a local variable 2"),
		new ByteCodeInstruction("lstore_3", (short)0x42, (short)0, "value →\nstore a long value in a local variable 3"),
		
		new ByteCodeInstruction("lsub", (short)0x65, (short)0, "value1, value2 → result\nsubtract two longs"),
		new ByteCodeInstruction("lushr", (short)0x7d, (short)0, "value1, value2 → result\nbitwise shift right of a long value1 by int value2 positions, unsigned"),
		new ByteCodeInstruction("lxor", (short)0x83, (short)0, "value1, value2 → result\nbitwise exclusive or of two longs"),
		
		new ByteCodeInstruction("monitorenter", (short)0xc2, (short)0, "objectref →\nenter monitor for object (grab the lock - start of synchronized() section)"),
		new ByteCodeInstruction("monitorexit", (short)0xc3, (short)0, "objectref →\nexit monitor for object (release the lock - end of synchronized() section)"),
		new ByteCodeInstruction("multianewarray", (short)0xc5, (short)3, "count1, [count2,...] → arrayref\ncreate a new array of dimensions dimensions with elements of type identified by class reference in constant pool index (indexbyte1 << 8 + indexbyte2); the sizes of each dimension is identified by count1, [count2, etc.]"),
		
		new ByteCodeInstruction("new", (short)0xbb, (short)2, "→ objectref\ncreate new object of type identified by class reference in constant pool index (indexbyte1 << 8 + indexbyte2)"),
		new ByteCodeInstruction("newarray", (short)0xbc, (short)1, "count → arrayref\ncreate new array with count elements of primitive type identified by atype"),
		new ByteCodeInstruction("nop", (short)0x00, (short)0, "[No change]\nperform no operation"),
		
		new ByteCodeInstruction("pop", (short)0x57, (short)0, "value →\ndiscard the top value on the stack"),
		new ByteCodeInstruction("pop2", (short)0x58, (short)0, "{value2, value1} →\ndiscard the top two values on the stack (or one value, if it is a double or long)"),
		new ByteCodeInstruction("putfield", (short)0xb5, (short)2, "objectref, value →\nset field to value in an object objectref, where the field is identified by a field reference index in constant pool (indexbyte1 << 8 + indexbyte2)"),
		new ByteCodeInstruction("putstatic", (short)0xb3, (short)2, "value →\nset static field to value in a class, where the field is identified by a field reference index in constant pool (indexbyte1 << 8 + indexbyte2)"),
		
		new ByteCodeInstruction("ret", (short)0xa9, (short)1, "[No change]\ncontinue execution from address taken from a local variable #index (the asymmetry with jsr is intentional)"),
		new ByteCodeInstruction("return", (short)0xb1, (short)0, "→ [empty]\nreturn void from method"),
		
		new ByteCodeInstruction("saload", (short)0x35, (short)0, "arrayref, index → value\nload short from array"),
		new ByteCodeInstruction("sastore", (short)0x56, (short)0, "arrayref, index, value →\nstore short to array"),
		new ByteCodeInstruction("sipush", (short)0x11, (short)2, "→ value\npush a short onto the stack"), 
		new ByteCodeInstruction("swap", (short)0x5f, (short)0, "value2, value1 → value1, value2\nswaps two top words on the stack (note that value1 and value2 must not be double or long)"),		
		
		new ByteCodeInstruction("tableswitch", (short)0xaa, (short)4, "index →\ncontinue execution from an address in the table at offset index"),
		new ByteCodeInstruction("wide", (short)0xc4, (short)3, "[same as for corresponding instructions]\nexecute opcode, where opcode is either iload, fload, aload, lstore, dload, lstore, fstore, astore, lstore, dstore, or ret, but assume the index is 16 bit; or execute iinc, where the index is 16 bits and the constant to increment by is a signed 16 bit short"),
		
	}; // ByteCodeInstruction[] instructionSet
	
	
	static class CONSTANT_Field_info implements IReset {
		short classRef;
		short nameAndTypeDescRef;
		String parent;
		String name;
		String typeDesc;
		ArrayList constantPool;
		PathClassLoader loader;
		private Compiler compiler;
		
		CONSTANT_Field_info(short classRef, short nameAndTypeDescRef, 
				ArrayList constantPool, PathClassLoader loader) {
			this.classRef = classRef;
			this.nameAndTypeDescRef = nameAndTypeDescRef;
			this.constantPool = constantPool;
			this.loader = loader;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		/** 바이트코드를 생성할 때 필요하다.*/
		CONSTANT_Field_info(String parent, String name, String typeDesc, Compiler compiler) {
			this.parent = parent;
			this.name = name;
			this.typeDesc = typeDesc;
			this.compiler = compiler;
		}
		
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			this.parent = null;
			this.name = null;
			this.typeDesc = null;
		}
		
		public String toString() {
			if (constantPool!=null) {
				CONSTANT_NameAndTypeDesc_info ntd = (CONSTANT_NameAndTypeDesc_info) constantPool.getItem(nameAndTypeDescRef);
				//ntd.toString();
				this.name = ((CONSTANT_Utf8_info) constantPool.getItem(ntd.nameRef)).str;
				this.typeDesc = ((CONSTANT_Utf8_info) constantPool.getItem(ntd.typeDescRef)).str;
				int parentNameIndex = ((CONSTANT_Class_info)constantPool.getItem(classRef)).name_index;
				this.parent = ((CONSTANT_Utf8_info)constantPool.getItem(parentNameIndex)).str;
				
				Field_Info field_Info = new Field_Info();
				field_Info.name = name;
				field_Info.descriptor = typeDesc;
				FindVarParams var = Field_Info.toFindVarParams(loader.compiler, field_Info, null, false);
				
				//var.fieldName = parent + "." + var.fieldName;
				return parent + "::" + var.toString();
			}
			else {
				Field_Info field_Info = new Field_Info();
				field_Info.name = name;
				field_Info.descriptor = typeDesc;
				FindVarParams var = Field_Info.toFindVarParams(compiler, field_Info, null, false);
				
				//var.fieldName = parent + "." + var.fieldName;
				return parent + "::" + var.toString();
			}
		}
	}
	
	static class CONSTANT_Method_info implements IReset {
		short classRef; // CONSTANT_Class_info 을 가리키는 constant table 내 인덱스
		short nameAndTypeDescRef;
		String parent;
		String name;
		String typeDesc;
		ArrayList constantPool;
		PathClassLoader loader;
		Compiler compiler;
		
		CONSTANT_Method_info(short classRef, short nameAndTypeDescRef, ArrayList constantPool, PathClassLoader loader) {
			this.classRef = classRef;
			this.nameAndTypeDescRef = nameAndTypeDescRef;
			this.constantPool = constantPool;
			this.loader = loader;
			
			/*this.parent = (String) constantPool.getItem(classRef);
			NameAndTypeDesc nameAndTypeDesc = 
					(NameAndTypeDesc) constantPool.getItem(nameAndTypeDescRef);
			this.name = (String) constantPool.getItem(nameAndTypeDesc.nameRef);
			this.typeDesc = (String) constantPool.getItem(nameAndTypeDesc.typeDescRef);*/
		}

		public CONSTANT_Method_info(String parentClassName, String funcName,
				String typeDescriptor, Compiler compiler) {
			// TODO Auto-generated constructor stub
			this.parent = parentClassName;
			this.name = funcName;
			this.typeDesc = typeDescriptor;
			this.compiler = compiler;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			this.parent = null;
			this.name = null;
			this.typeDesc = null;
		}
		
		public String toString() {
			if (constantPool!=null) {
				CONSTANT_NameAndTypeDesc_info ntd = (CONSTANT_NameAndTypeDesc_info) constantPool.getItem(nameAndTypeDescRef);
				//ntd.toString();
				this.name = ((CONSTANT_Utf8_info) constantPool.getItem(ntd.nameRef)).str;
				this.typeDesc = ((CONSTANT_Utf8_info) constantPool.getItem(ntd.typeDescRef)).str;
				int parentNameIndex = ((CONSTANT_Class_info)constantPool.getItem(classRef)).name_index;
				this.parent = ((CONSTANT_Utf8_info)constantPool.getItem(parentNameIndex)).str;
				
				Method_Info method_Info = new Method_Info();
				method_Info.name = name;
				method_Info.descriptor = typeDesc;
				
				FindFunctionParams func = Method_Info.toFindFunctionParams(loader.compiler, loader, 
						method_Info, loader.typeNameInTemplatePair, true, parent, false);
				
				//func.name = parent + "." + func.name;
				return parent + "::" + func.toString();
			}
			else {
				Method_Info method_Info = new Method_Info();
				method_Info.name = name;
				method_Info.descriptor = typeDesc;
				
				String typeNameInTemplatePair = null;
				
				FindFunctionParams func = Method_Info.toFindFunctionParams(compiler, loader, 
						method_Info, typeNameInTemplatePair, true, parent, false);
				
				//func.name = parent + "." + func.name;
				return parent + "::" + func.toString();
			}
		}
		
		
	}
	
	
	
	static class CONSTANT_InterfaceMethod_info implements IReset {
		short classRef;
		short nameAndTypeDescRef;
		String parent;
		String name;
		String typeDesc;
		ArrayList constantPool;
		PathClassLoader loader;
		
		CONSTANT_InterfaceMethod_info(PathClassLoader loader, ArrayList constantPool, short classRef, short nameAndTypeDescRef) {
			this.classRef = classRef;
			this.nameAndTypeDescRef = nameAndTypeDescRef;
			this.constantPool = constantPool;
			this.loader = loader;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			this.parent = null;
			this.name = null;
			this.typeDesc = null;
		}
				
		public String toString() {
			CONSTANT_NameAndTypeDesc_info ntd = (CONSTANT_NameAndTypeDesc_info) constantPool.getItem(nameAndTypeDescRef);
			//ntd.toString();
			this.name = ((CONSTANT_Utf8_info) constantPool.getItem(ntd.nameRef)).str;
			this.typeDesc = ((CONSTANT_Utf8_info) constantPool.getItem(ntd.typeDescRef)).str;
			int parentNameIndex = ((CONSTANT_Class_info)constantPool.getItem(classRef)).name_index;
			this.parent = ((CONSTANT_Utf8_info)constantPool.getItem(parentNameIndex)).str;
			
			Method_Info method_Info = new Method_Info();
			method_Info.name = name;
			method_Info.descriptor = typeDesc;
			
			//String parentDot = CompilerHelper.setPathSeparatorToDot(parent);
			
			FindFunctionParams func = Method_Info.toFindFunctionParams(loader.compiler, loader, 
					method_Info, loader.typeNameInTemplatePair, true, parent, false);
			
			func.name = parent + "." + func.name;
			return func.toString();
		}
	}
	
	static class CONSTANT_NameAndTypeDesc_info {
		short nameRef;
		short typeDescRef;
		ArrayList constantPool;
		String name;
		String typeDesc;
		
		CONSTANT_NameAndTypeDesc_info(short nameRef, short typeDescRef, ArrayList constantPool) {
			this.nameRef = nameRef;
			this.typeDescRef = typeDescRef;
			this.constantPool = constantPool;
			
		}
		/** name과 typeDesc가 정해진다.*/
		public String toString() {
			this.name = ((CONSTANT_Utf8_info) constantPool.getItem(nameRef)).str;
			this.typeDesc = ((CONSTANT_Utf8_info) constantPool.getItem(typeDescRef)).str;
			return name + ":" + typeDesc;
		}
	}
	
	
	/** 바이트코드 생성시 필요하다.
	 * typeName 으로부터 디스크립터를 얻는다.
	 * typeName이 오브젝트인 경우 Lcom/gsoft/common/Util$ArrayList; 와 같이 만든다.
	 * typeName이 배열인 경우 디스크립터 앞에 '['을 차원에 맞춰서 붙인다.*/
	static void getDescriptor(String typeName, ArrayListChar result) {
		int dimension = CompilerHelper.getArrayDimension(null, typeName);
		if (dimension!=0) {
			typeName = CompilerHelper.getArrayElementType(typeName);
		}
		typeName = CompilerHelper.getTemplateOriginalType(typeName);
		int i;
		for (i=0; i<dimension; i++) {
			result.add('[');
		}
		char c;
		if (typeName.equals("void")) result.add('V');
		else if (typeName.equals("byte")) result.add('B');
		else if (typeName.equals("char")) result.add('C');
		else if (typeName.equals("double")) result.add('D');
		else if (typeName.equals("float")) result.add('F');
		else if (typeName.equals("int")) result.add('I');
		else if (typeName.equals("long")) result.add('J');
		else if (typeName.equals("short")) result.add('S');
		else if (typeName.equals("boolean")) result.add('Z');
		else {
			result.add('L');
			
			getClassFilePath(typeName, result);
			result.add(';');
			
		}
	}
	
	/** typeName 인 com.gsoft.common.Util.ArrayList 을 
	 * 디렉토리 경로 형태인 com/gsoft/common/Util$ArrayList 로 바꾼다.
	 * 바이트코드를 만드는 것이므로 '/'으로 바꿔준다.*/
	static void getClassFilePath(String typeName, ArrayListChar result) {
		typeName = CompilerHelper.getArrayElementType(typeName);
		typeName = CompilerHelper.getTemplateOriginalType(typeName);
		
		if (CompilerHelper.IsDefaultType(typeName)) return;
		
		if (typeName.equals("java.lang.String")) {
			int a;
			a=0;
			a++;
		}
		
		typeName = typeName.replace('.', File.separatorChar);
		String path = Control.pathAndroid + File.separator + typeName;
		String classFilePath = CompilerHelper.fixClassPath(path);
		
		if (classFilePath!=null) {
			int dirLen = (Control.pathAndroid + File.separator).length();
			String r = classFilePath.substring(dirLen, classFilePath.length());
			int indexClass = r.indexOf(".class");
			if (indexClass!=-1) {
				r = r.substring(0, indexClass);
			}
			// 바이트코드를 만드는 것이므로 '/'으로 바꿔준다.
			r = r.replace('\\', '/');
			result.add(r);
			// classFilePath 를 얻었으므로 리턴한다.
			// classFilePath 를 얻지 못하면 소스패스를 확인한다.
			return;
		}
		
		//if (classFilePath==null) return;
		String sourcePath = null;
		
		if (classFilePath==null) {
			sourcePath = Control.pathProjectSrc + File.separator + typeName;
			sourcePath = CompilerHelper.getSourceFilePath(sourcePath);
			if (sourcePath==null) return;
			// java.lang.String의 소스파일은 없으므로 직접 만든 소스파일을 제공해야 한다.
			// 직접 만든 소스파일의 위치를 찾는다.
			//classFilePath = CompilerHelper.getSourceFilePathAddingComGsoftCommon(typeName);
			
			//if (classFilePath==null) return;
		}
		
		// com.gsoft.common.Util.ArrayList
		
		HighArray_CodeString stringArr1 = 
			new StringTokenizer().ConvertToStringArray2(new CodeString(typeName,Color.BLACK), 20, Language.Java);
		
		// Control.pathProjectSrc + File.separator + com\gsoft\common\Util + ".java"
		HighArray_CodeString stringArr2;
		stringArr2 = new StringTokenizer().ConvertToStringArray2(new CodeString(sourcePath,Color.BLACK), 20, Language.Java);
		
		int i;
		// Util
		for (i=stringArr2.count-1; i>=0; i--) {
			CodeString str = stringArr2.getItem(i);
			if (CompilerHelper.IsSeparator(str)) continue;
			if (str.equals("java") || str.equals("class")) continue;
			break;				
		}
		
		CodeString lastStr = null;
		try {
		lastStr = stringArr2.getItem(i); // Util
		}catch(Exception e) {
			int a;
			a=0;
			a++;
			e.printStackTrace();
		}
		int j;
		for (j=stringArr1.count-1; j>=0; j--) {
			CodeString str = stringArr1.getItem(j);
			if (CompilerHelper.IsSeparator(str)) continue;
			try {
			if (str.equals(lastStr.str)) {
				break;
			}
			}catch(Exception e) {
				int a;
				a=0;
				a++;
				e.printStackTrace();
			}
		}
		int lastIndex = j;
	
		for (j=0; j<=lastIndex; j++) { // separator 를 디렉토리 경로 문자로 변경
			CodeString str = stringArr1.getItem(j);
			if (CompilerHelper.IsSeparator(str)!=true) {
				result.add(str.str);
			}
			else {
				result.add('/'); // 자바 클래스 파일 경로 문자
			}
		}
		
		for (j=lastIndex+1; j<stringArr1.count; j++) { // separator 를 내부클래스 구분 문자($)로 변경
			CodeString str = stringArr1.getItem(j);
			if (CompilerHelper.IsSeparator(str)!=true) {
				result.add(str.str);
			}
			else {
				result.add('$'); // 내부클래스 구분 문자($)
			}
		}
		
	
	}
	
	
	static class Field_Info implements IReset {
		short access_flags;
		/** 필드 이름을 표현하는 CONSTANT_Utf8_info 의 constant table 내 인덱스*/
		short name_index;
		/** 필드 디스크립터를 표현하는 CONSTANT_Utf8_info 의 constant table 내 인덱스*/
		short descriptor_index;
		short attributes_count;
		Attribute_Info[] attributes;
		
		String name;
		String descriptor;
		AccessModifier accessModifier;
		
		static Field_Info read(PathClassLoader owner, InputStream is, ArrayList constantTable, boolean IsLittleEndian) throws IOException, Exception {
			try {
			Field_Info r = new Field_Info();
			r.access_flags = IO.readShort(is, IsLittleEndian);
			r.accessModifier = toAccessModifier(r.access_flags, ClassFieldMethod.Field);
			r.name_index = IO.readShort(is, IsLittleEndian);
			
			
			r.name = ((CONSTANT_Utf8_info) constantTable.getItem(r.name_index)).str;
			
			
			
			r.descriptor_index = IO.readShort(is, IsLittleEndian);
			r.descriptor = ((CONSTANT_Utf8_info) constantTable.getItem(r.descriptor_index)).str;
			
			r.attributes_count = IO.readShort(is, IsLittleEndian);
			r.attributes = new Attribute_Info[r.attributes_count];
			int i;
			for (i=0; i<r.attributes_count; i++) {
				r.attributes[i] = Attribute_Info.read(owner, is, constantTable, IsLittleEndian);
			}
			
			return r;
			}catch(Exception e) {
				e.printStackTrace();				
				int a;
				a=0;
				a++;
				throw e;
			}
			
		}
		
		/** indexC 는 c의 fieldDescriptor상에서의 인덱스를 말한다.*/
		private static String getType(String fieldDescriptor, char c, int indexC, boolean setsSeparatorToDot) {
			String typeName=null;
			if (c=='B') typeName = "byte";				
			else if (c=='C') typeName = "char";
			else if (c=='D') typeName = "double";
			else if (c=='F') typeName = "float";
			else if (c=='I') typeName = "int";
			else if (c=='J') typeName = "long";
			else if (c=='S') typeName = "short";
			else if (c=='Z') typeName = "boolean";
			else if (c=='L') {
				typeName = fieldDescriptor.substring(indexC+1, fieldDescriptor.length()-1);
				if (setsSeparatorToDot) {
					typeName = typeName.replace('/', '.');
					typeName = typeName.replace('$', '.');
				}
			}
			return typeName;
		}
		
		public static String getType(Field_Info field, boolean setsSeparatorToDot) {
			String typeName=null;
			if (field.descriptor!=null) {
				char c = field.descriptor.charAt(0);				
				if (c=='[') { // array
					int i;
					int dimension=1;
					for (i=1; i<field.descriptor.length(); i++) {
						c = field.descriptor.charAt(i);
						if (c=='[') {
							dimension++;
						}
						else break;
					}
					int j;
					String arrDimension = "";
					for (j=0; j<dimension; j++) {
						arrDimension = arrDimension + "[]";
					}
					// array의 원소 타입
					char t = field.descriptor.charAt(i);
					String elementType = getType(field.descriptor, t, i, setsSeparatorToDot);
					typeName = elementType + arrDimension;
					
				}
				else {
					typeName = getType(field.descriptor, c, 0, setsSeparatorToDot);
				}
			}
			return typeName;
		}
		
		/** @param typeNameInTemplatePair : 클래스가 템플릿 클래스일 경우 <>안에 있는 바꿔야 할 풀 타입 이름이다.
		 * @param setsSeparatorToDot : path separator 나 '$'등 디렉토리 관련정보를 '.'으로 바꾸려면 true,
		 * 그렇지 않으면 false*/
		static FindVarParams toFindVarParams(Compiler compiler, Field_Info field, String typeNameInTemplatePair,
				boolean setsSeparatorToDot) {
			FindVarParams var=null;
			//var = FindVarParams(field.name)
			String typeName;
			String fieldName;
			fieldName = field.name;
			if (fieldName.equals("controls")) {
				int a;
				a=0;
				a++;
			}
			typeName = getType(field, setsSeparatorToDot);
			var = new FindVarParams(compiler, typeName, fieldName);
			if (typeNameInTemplatePair!=null) {
				//T data 에서 T는 바이트코드에서 java.lang.Object이다.
				if (var.typeName.equals("java.lang.Object")) {
					var.typeName = typeNameInTemplatePair;
				}
			}
			var.accessModifier = field.accessModifier;
			var.isMemberOrLocal = true;
			return var;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.accessModifier!=null) {
				this.accessModifier.destroy();
				this.accessModifier = null;
			}
			if (this.attributes!=null) {
				int i;
				for (i=0; i<this.attributes_count; i++) {
					this.attributes[i].destroy();
					this.attributes[i] = null;
				}
			}
			this.name = null;
			this.descriptor = null;
		}
	}
	
	static class Method_Info implements IReset {
		short access_flags;
		/** 메서드 이름을 표현하는 CONSTANT_Utf8_info 의 constant table 내 인덱스*/
		short name_index;
		/** 메서드 디스크립터를 표현하는 CONSTANT_Utf8_info 의 constant table 내 인덱스*/
		short descriptor_index;
		short attributes_count;
		/** code, exception, local attribute 등*/
		Attribute_Info[] attributes;
		
		String name;
		String descriptor;
		AccessModifier accessModifier;
		
		static Method_Info read(PathClassLoader owner, InputStream is, ArrayList constantTable, boolean IsLittleEndian) throws IOException, Exception {
			try {
			Method_Info r = new Method_Info();
			r.access_flags = IO.readShort(is, IsLittleEndian);
			r.accessModifier = toAccessModifier(r.access_flags, ClassFieldMethod.Method);
			r.name_index = IO.readShort(is, IsLittleEndian);
			r.name = ((CONSTANT_Utf8_info) constantTable.getItem(r.name_index)).str;
			//r.parent = owner.name;
			
			if (r.name.equals("<clinit>") || r.name.equals("<init>")) {
				int a;
				a=0;
				a++;
			}
			
			r.descriptor_index = IO.readShort(is, IsLittleEndian);
			// ProcessBuilder의 command()함수의 디스크립터는 다음과 같다.
			// ()Ljava/util/List;
			// ([Ljava/lang/String;)Ljava/lang/ProcessBuilder;
			// (Ljava/util/List;)Ljava/lang/ProcessBuilder;
			
			r.descriptor = ((CONSTANT_Utf8_info) constantTable.getItem(r.descriptor_index)).str;
			
			
			r.attributes_count = IO.readShort(is, IsLittleEndian);
			r.attributes = new Attribute_Info[r.attributes_count];
			int i;
			for (i=0; i<r.attributes_count; i++) {
				r.attributes[i] = Attribute_Info.read(owner, is, constantTable, IsLittleEndian);
			}
			//is.skip(r.attributes_count);
			return r;
			
			}catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
			
		}
		
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.accessModifier!=null) {
				this.accessModifier.destroy();
				this.accessModifier = null;
			}
			if (this.attributes!=null) {
				int i;
				for (i=0; i<this.attributes_count; i++) {
					this.attributes[i].destroy();
					this.attributes[i] = null;
				}
			}
			this.name = null;
			this.descriptor = null;
		}
		
		
		
		private static String getType(String descriptor, char c, int index, int arrayDimension,
				boolean setsSeparatorToDot) {
			String typeName=null;
			if (c=='V') typeName = "void";
			else if (c=='B') typeName = "byte";				
			else if (c=='C') typeName = "char";
			else if (c=='D') typeName = "double";
			else if (c=='F') typeName = "float";
			else if (c=='I') typeName = "int";
			else if (c=='J') typeName = "long";
			else if (c=='S') typeName = "short";
			else if (c=='Z') typeName = "boolean";
			else if (c=='L') {
				int i;
				for (i=index+1; i<descriptor.length(); i++) {
					if (descriptor.charAt(i)==';') break;
				}
				
				typeName = descriptor.substring(index+1, i);
				if (setsSeparatorToDot) {
					typeName = typeName.replace('/', '.');
					typeName = typeName.replace('$', '.');
				}
			}
			int i;
			for (i=0; i<arrayDimension; i++) {
				typeName = typeName + "[]";
			}
			return typeName;
		}
		
		static int getSemicolonIndex(String descriptor, int startIndex) {
			int i;
			int count = descriptor.length();
			for (i=startIndex; i<count; i++) {
				char c = descriptor.charAt(i);
				if (c==';') return i;
			}
			return -1;
		}
		
		
		
		static String[] getTypes(Method_Info method, boolean setsSeparatorToDot) {
			ArrayListString list = new ArrayListString(10); 
			String[] r = null;
			if (method.name.equals("getItems")) {
				int a;
				a=0;
				a++;
			}
			// (파라미터디스크립터*)리턴디스크립터
			if (method.descriptor!=null) {
				int i;
				String descriptor = method.descriptor;
				int dimensionArray = 0;
				// 파라미터 타입들
				int len = descriptor.length();
				for (i=0; i<len; i++) {
					char c = descriptor.charAt(i);
					if (c=='(') continue;
					else if (c==')') {
						i++;
						break;
					}
					else if (c=='[') {
						dimensionArray++;
					}
					else if (c=='V' || c=='B' || c=='C' || c=='D' || c=='F' || c=='I' || c=='J' || c=='S' || c=='Z') {						
						String type = getType(descriptor, c, i, dimensionArray, setsSeparatorToDot);
						list.add(type);
						dimensionArray = 0;
					}
					else if (c=='L') {						
						String type = getType(descriptor, c, i, dimensionArray, setsSeparatorToDot);
						list.add(type);
						//i += type.length();
						i = getSemicolonIndex(descriptor, i);
						dimensionArray = 0;
					}
				}
				// 리턴 타입들
				for (;i<descriptor.length(); i++) {
					char c = descriptor.charAt(i);
					if (c=='[') {
						dimensionArray++;
					}
					else if (c=='V' || c=='B' || c=='C' || c=='D' || c=='F' || c=='I' || c=='J' || c=='S' || c=='Z') {						
						String type = getType(descriptor, c, i, dimensionArray, setsSeparatorToDot);
						list.add(type);
						dimensionArray = 0;
					}
					else if (c=='L') {						
						String type = getType(descriptor, c, i, dimensionArray, setsSeparatorToDot);
						list.add(type);
						//i += type.length();
						i = getSemicolonIndex(descriptor, i);
						dimensionArray = 0;
					}
				}
				
				r = list.getItems();
			}
			return r;
		}
		
		
		/** @param typeNameInTemplatePair : 클래스가 템플릿 클래스일 경우 <>안에 있는 바꿔야 할 풀 타입 이름이다.
		 * @param changesInitToConstructorName : init 메서드 이름을 클래스 이름으로 바꾸는지 여부
		 * @param setsSeparatorToDot : path separator 나 '$'등 디렉토리 관련정보를 '.'으로 바꾸려면 true,
		 * 그렇지 않으면 false*/
		static FindFunctionParams toFindFunctionParams(Compiler compiler, PathClassLoader loader, 
				Method_Info method, String typeNameInTemplatePair, 
				boolean changesInitToConstructorName, String parent, boolean setsSeparatorToDot) {
			FindFunctionParams func=null;
			//var = FindVarParams(field.name)
			String[] listOfTypes;
			String methodName;
			methodName = method.name;
			
			if (method.name.equals("test")) {
				int a;
				a=0;
				a++;
			}
			
			listOfTypes = getTypes(method, setsSeparatorToDot);
			func = new FindFunctionParams(compiler, methodName);
			func.returnType = listOfTypes[listOfTypes.length-1];
			func.method_Info = method;
			
			
			if (method.name.equals("<init>") || method.name.equals("<clinit>")) {
				if (changesInitToConstructorName) {
					method.name = compiler.getShortName(parent);
				}
				func.name = method.name;
				if (func.name.equals("Control")) {
					int a;
					a=0;
					a++;
				}
				// stack = new Stack<Block>();에서 Stack<Block>의 fullNameIncludingTemplateExceptArray는
				// com.gsoft.common.Util.Stack<com.gsoft.common.Compiler_types.Block>이다.
				if (loader==null) {// 바이트코드를 만들때
					func.returnType = parent + "." + func.name;
				}
				else {  // 바이트코드를 읽을때
					func.returnType = loader.fullNameIncludingTemplateExceptArray;
				}
				func.isConstructor = true;
				if (method.accessModifier!=null && method.accessModifier.isStatic) {
					func.isConstructorThatInitializesStaticFields = true;
				}
			}
			
			if (typeNameInTemplatePair!=null) {
				//T Get()에서 T는 바이트코드에서 java.lang.Object이다.
				if (func.returnType.equals("java.lang.Object")) {
					func.returnType = typeNameInTemplatePair;
				}
			}
			
			int i;
			func.listOfFuncArgs = new ArrayListIReset(5);
			for (i=0; i<listOfTypes.length-1; i++) {
				String typeName = listOfTypes[i];
				FindVarParams param = new FindVarParams(compiler, typeName, null);
				//void Push(T data)에서 T는 바이트코드에서 java.lang.Object이다.
				if (typeNameInTemplatePair!=null) {
					if (param.typeName.equals("java.lang.Object")) {
						param.typeName = typeNameInTemplatePair;
					}
				}
				func.listOfFuncArgs.add(param);
			}
			func.accessModifier = method.accessModifier;
			return func;
		}
	}
	
	/** method_info의 attribute이다.*/
	static class Exceptions_attribute implements IReset {
		/** attribute_name_index, attribute_length 이 처음 6바이트를 제외해야 한다.
		 *  Attribute_Info에서 이미 읽었다.*/
		short attribute_name_index;
		/** attribute_name_index, attribute_length 이 처음 6바이트를 제외해야 한다.
		 *  Attribute_Info에서 이미 읽었다.*/
		int attribute_length;
		/**exception_index_table의 entry 개수*/
		short number_of_exceptions;
		/** 각각의 값은 constant table로의 인덱스이다. 
		 * 인덱스가 가리키는 constant table내 값은 CONSTANT_Class_info이다.
		 * CONSTANT_Class_info는 메서드가 throw하는 클래스 타입이다. 예외 타입이다.
		 */
		short[] exception_index_table;
		
		public static Exceptions_attribute read(InputStream is, boolean IsLittleEndian) {
			Exceptions_attribute r = new Exceptions_attribute();
			//r.attribute_name_index = IO.readShort(is, IsLittleEndian);
			//r.attribute_length = IO.readInt(is, IsLittleEndian);
			r.number_of_exceptions = IO.readShort(is, IsLittleEndian);
			r.exception_index_table = new short[r.number_of_exceptions];
			int i;
			for (i=0; i<r.number_of_exceptions; i++) {
				r.exception_index_table[i] = IO.readShort(is, IsLittleEndian);
			}
			return r;
		}

		public void destroy() {
			// TODO Auto-generated method stub
			exception_index_table = null;
		}
	}
	
	static class Exception_Entry implements IReset {
		/** 예외핸들러가 활동(try)중인 code 배열안 범위, [start_pc(포함), end_pc(불포함)), opcode 의 인덱스, 
		 * nested 될 수 있다. end_pc보다 항상 작다.*/
		short start_pc;
		/** 예외핸들러가 활동(try)중인 code 배열안 범위, [start_pc(포함), end_pc(불포함)), opcode 의 인덱스나 code 배열의 길이,
		 * nested 될 수 있다. start_pc보다 항상 크다.*/
		short end_pc;
		/** 예외핸들러의 시작, opcode 의 인덱스*/
		short handler_pc;
		/** catch_type 이 0이 아니면 constant table 내 CONSTANT_Class_info의 인덱스,
		 * 이것은 catch 해야 할 예외 클래스 타입이다.
		 * catch_type 이 0이면 모든 예외에 대해서 실행되는 finally 구문이 된다.
		 */
		short catch_type;
		
		public static Exception_Entry read(InputStream is, boolean IsLittleEndian) {
			Exception_Entry r = new Exception_Entry();
			r.start_pc = IO.readShort(is, IsLittleEndian);
			r.end_pc = IO.readShort(is, IsLittleEndian);
			r.handler_pc = IO.readShort(is, IsLittleEndian);
			r.catch_type = IO.readShort(is, IsLittleEndian);
			return r;
		}

		public void destroy() {
			// TODO Auto-generated method stub
			
		}
	}
	
	/** local_variable_table의 엔트리, 
	 * 각 엔트리는 어떤 지역변수가 값을 갖고있는 Code_Attribute 내 code 배열안에서 인덱스 범위를 말한다.*/
	static class LocalVariableTable_Entry implements IReset { 
		/**  주어진 지역변수는 Code_Attribute 의 code array 안 
		 * [start_pc, start_pc+length] 인덱스 구간내에서 값을 갖는다. 
		 * start_pc 는 code 배열로의 opcode 인덱스이다.
		 */
		short start_pc;
		/**  주어진 지역변수는 Code_Attribute 의 code array 안 
		 * [start_pc, start_pc+length] 인덱스 구간내에서 값을 갖는다. 
		 * start_pc+length 는 code 배열로의 opcode 인덱스이거나 code 배열의 끝 다음 인덱스이다.
		 */
		short length;
		/** CONSTANT_Utf8_info 의 constant table 내 인덱스,  지역변수의 unqualified name */
		short name_index;
		/** CONSTANT_Utf8_info 의 constant table 내 인덱스, 
		 * 지역변수의 타입을 표현하는 디스크립터*/
		short descriptor_index;
		/** 현재 스택프레임내 지역변수 array 내 어떤 지역변수의 인덱스, 
		 * long 이나 double 의 경우 index, index+1을 말한다.*/
		short index;
		
		/** 지역변수의 unqualified name*/
		String name;
		/** 지역변수의 타입, I, Ljava/lang/Object; 등*/
		String descriptor;
		
		public static LocalVariableTable_Entry read(InputStream is, ArrayList constantTable, boolean IsLittleEndian) {
			LocalVariableTable_Entry r = new LocalVariableTable_Entry();
			r.start_pc = IO.readShort(is, IsLittleEndian);
			r.length = IO.readShort(is, IsLittleEndian);
			r.name_index = IO.readShort(is, IsLittleEndian);
			r.descriptor_index = IO.readShort(is, IsLittleEndian);
			r.index = IO.readShort(is, IsLittleEndian);
			r.name = ((CONSTANT_Utf8_info)constantTable.getItem(r.name_index)).str;
			r.descriptor = ((CONSTANT_Utf8_info)constantTable.getItem(r.descriptor_index)).str;
			return r;
		}

		public void destroy() {
			// TODO Auto-generated method stub
			this.name = null;
			this.descriptor = null;
		}
	}
	
	
	static class LocalVariableTable_attribute implements IReset {
		/** attribute_name_index, attribute_length 이 처음 6바이트를 제외해야 한다.
		 *  Attribute_Info에서 이미 읽었다.*/
		short attribute_name_index;
		/** attribute_name_index, attribute_length 이 처음 6바이트를 제외해야 한다.
		 *  Attribute_Info에서 이미 읽었다.*/
		int attribute_length;
		/** local_variable_table 의 엔트리 개수*/
		short local_variable_table_length;
		/**local_variable_table_length 개 만큼 할당*/
		LocalVariableTable_Entry[] local_variable_table;
		
		/** Attribute_Info 의 attribute_length 만큼 읽어들인다.*/
		public static LocalVariableTable_attribute toLocalVariableTable_attribute(InputStream is, 
				ArrayList constantTable, boolean IsLittleEndian) throws IOException {
			LocalVariableTable_attribute r = new LocalVariableTable_attribute();
			// Attribute_info 에서 이미 읽었으므로 읽지 않는다.
			//r.attribute_name_index = IO.readShort(is);
			//r.attribute_length = IO.readInt(is);
			
			r.local_variable_table_length = IO.readShort(is, IsLittleEndian);
			r.local_variable_table = new LocalVariableTable_Entry[r.local_variable_table_length];
			int i;
			for (i=0; i<r.local_variable_table_length; i++) {
				r.local_variable_table[i] = LocalVariableTable_Entry.read(is, constantTable, IsLittleEndian);
			}
			return r;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.local_variable_table!=null) {
				int i;
				for (i=0; i<this.local_variable_table_length; i++) {
					this.local_variable_table[i].destroy();
					this.local_variable_table[i] = null;
				}
				this.local_variable_table = null;
			}
		}
	}
	
	static class Verification_Type_info {
		/** 0이면 ITEM_Top, 1이면 ITEM_Integer, 2이면 ITEM_Float,
		 * 3이면 ITEM_Double, 4이면 ITEM_Long, 5이면 ITEM_Null,
		 * 6이면 ITEM_UninitializedThis, 7이면 ITEM_Object, 
		 * 8이면 ITEM_Uninitialized */

		short tagShort;
		short cpool_index; // ITEM_Object이면 의미가 있다.
		short offset;	// ITEM_Uninitialized이면 의미가 있다.
		
		public static Verification_Type_info read(InputStream is,
				ArrayList constantTable, boolean isLittleEndian) {
			Verification_Type_info r = new Verification_Type_info();
			byte tag = IO.readByte(is); 
			byte[] arr = {tag, 0};
			r.tagShort = IO.toShort(arr, true);
			if (r.tagShort==7) { // ITEM_Object
				r.cpool_index = IO.readShort(is, isLittleEndian);
			}
			else if (r.tagShort==8) { // ITEM_Uninitialized
				r.offset = IO.readShort(is, isLittleEndian);
			}
			else { // 더이상 작업할 필요가 없다.
				
			}
			return r;
		}
		
	}
	
	static class StackMapTable_Entry implements IReset {
		/** [0-63]이면 same_frame, [64, 127]이면 same_locals_1_stack_item_frame,
		 * [128-246]이면 보류, 247이면 same_locals_1_stack_item_frame_extended,
		 *   [248-250]이면 chop_frame,  251이면 same_frame_extended, 
		 *  [252-254]이면 append, 255이면 full_frame 이다.
		 */
		short frame_type;
		/** same_locals_1_stack_item_frame, same_locals_1_stack_item_frame_extended 이면 
		 * null 이 아님, 원소개수는 1개이다.*/
		Verification_Type_info[] stack; 
		/** same_locals_1_stack_item_frame_extended, chop_frame, same_frame_extended,
		 * append_frame 이면 
		 * 0이 아님*/
		short offset_delta;
		
		/** append_frame 이면 null 이 아님,
		 * verification_type_info locals[frame_type -251]*/
		Verification_Type_info[] locals;
		/**full_frame 이면 0 이 아님*/
		short number_of_locals;
		/**full_frame 이면 null 이 아님, 
		 * verification_type_info locals[number_of_locals];*/
		Verification_Type_info[] locals_FullFrame;
		/**full_frame 이면 0 이 아님*/
		short number_of_stack_items;
		/**full_frame 이면 null 이 아님, 
		 * verification_type_info stack[number_of_stack_items]*/
		Verification_Type_info[] stack_FullFrame;
		
		
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
		}

		public static StackMapTable_Entry read(InputStream is,
				ArrayList constantTable, boolean isLittleEndian) {
			// TODO Auto-generated method stub
			StackMapTable_Entry entry = new StackMapTable_Entry();
			byte tag = IO.readByte(is);
			byte[] arr = {tag, 0};
			entry.frame_type = IO.toShort(arr, true);
			
			if (0<=entry.frame_type && entry.frame_type<=63) {// SAME
				// 더이상 작업할 필요가 없다.
				//same_frame {
				//	u1 frame_type = SAME;/* 0-63 */
				//}
			}
			else if (64<=entry.frame_type && entry.frame_type<=127) {//SAME_LOCALS_1_STACK_ITEM
				//same_locals_1_stack_item_frame {
				//	u1 frame_type = SAME_LOCALS_1_STACK_ITEM;/* 64-127 */
				//	verification_type_info stack[1];
				//}
				entry.stack = new Verification_Type_info[1];
				entry.stack[0] = Verification_Type_info.read(is, constantTable, isLittleEndian);
			}
			else if (128<=entry.frame_type && entry.frame_type<=246) {//reserved
				
			}
			else if (entry.frame_type==247) {//SAME_LOCALS_1_STACK_ITEM_EXTENDED
				//same_locals_1_stack_item_frame_extended {
				//	u1 frame_type = SAME_LOCALS_1_STACK_ITEM_EXTENDED;/* 247 */
				//	u2 offset_delta;
				//	verification_type_info stack[1];
				//}
				entry.offset_delta = IO.readShort(is, isLittleEndian);
				entry.stack = new Verification_Type_info[1];
				entry.stack[0] = Verification_Type_info.read(is, constantTable, isLittleEndian);
			}
			else if (248<=entry.frame_type && entry.frame_type<=250) {//CHOP
				//chop_frame {
				//	u1 frame_type=CHOP; /* 248-250 */
				//	u2 offset_delta;
				//}
				entry.offset_delta = IO.readShort(is, isLittleEndian);
			}
			else if (entry.frame_type==251) {//SAME_FRAME_EXTENDED
				//same_frame_extended {
				//	u1 frame_type = SAME_FRAME_EXTENDED;/* 251*/
				//	u2 offset_delta;
				//}
				entry.offset_delta = IO.readShort(is, isLittleEndian);
			}
			else if (252<=entry.frame_type && entry.frame_type<=254) {//APPEND
				//append_frame {
				//	u1 frame_type = APPEND; /* 252-254 */
				//	u2 offset_delta;
				//	verification_type_info locals[frame_type -251];
				//}
				entry.offset_delta = IO.readShort(is, isLittleEndian);
				entry.locals = new Verification_Type_info[entry.frame_type-251];
				int i;
				for (i=0; i<entry.locals.length; i++) {
					entry.locals[i] = Verification_Type_info.read(is, constantTable, isLittleEndian);
				}
			}
			else if (entry.frame_type==255) {//FULL_FRAME
				//full_frame {
				//	u1 frame_type = FULL_FRAME; /* 255 */
				//	u2 offset_delta;
				//	u2 number_of_locals;
				//	verification_type_info locals[number_of_locals];
				//	u2 number_of_stack_items;
				//	verification_type_info stack[number_of_stack_items];
				//}
				entry.offset_delta = IO.readShort(is, isLittleEndian);
				
				entry.number_of_locals = IO.readShort(is, isLittleEndian);
				entry.locals_FullFrame = new Verification_Type_info[entry.number_of_locals];
				int i;
				for (i=0; i<entry.locals_FullFrame.length; i++) {
					entry.locals_FullFrame[i] = Verification_Type_info.read(is, constantTable, isLittleEndian);
				}
				
				entry.number_of_stack_items = IO.readShort(is, isLittleEndian);
				entry.stack_FullFrame = new Verification_Type_info[entry.number_of_stack_items];
				//int i;
				for (i=0; i<entry.stack_FullFrame.length; i++) {
					entry.stack_FullFrame[i] = Verification_Type_info.read(is, constantTable, isLittleEndian);
				}
			}
			return entry;
		}
		
	}
	
	
	static class StackMapTable_attribute implements IReset {
		/** attribute_name_index, attribute_length 이 처음 6바이트를 제외해야 한다.
		 *  Attribute_Info에서 이미 읽었다.*/
		short attribute_name_index;
		/** attribute_name_index, attribute_length 이 처음 6바이트를 제외해야 한다.
		 *  Attribute_Info에서 이미 읽었다.*/
		int attribute_length;
		/** stackMapTable 의 엔트리 개수*/
		short  number_of_entries;
		/** number_of_entries 개 만큼 할당*/
		StackMapTable_Entry[] stackMapTable;
		
		/** Attribute_Info 의 attribute_length 만큼 읽어들인다.*/
		public static StackMapTable_attribute toStackMapTable_attribute(PathClassLoader loader, InputStream is, 
				ArrayList constantTable, boolean IsLittleEndian) throws IOException {
			StackMapTable_attribute r = new StackMapTable_attribute();
			// Attribute_info 에서 이미 읽었으므로 읽지 않는다.
			//r.attribute_name_index = IO.readShort(is);
			//r.attribute_length = IO.readInt(is);
			
			r.number_of_entries = IO.readShort(is, IsLittleEndian);
			r.stackMapTable = new StackMapTable_Entry[r.number_of_entries];
			int i;
			for (i=0; i<r.number_of_entries; i++) {
				r.stackMapTable[i] = StackMapTable_Entry.read(is, constantTable, IsLittleEndian);
			}
			return r;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.stackMapTable!=null) {
				int i;
				for (i=0; i<this.number_of_entries; i++) {
					this.stackMapTable[i].destroy();
					this.stackMapTable[i] = null;
				}
				this.stackMapTable = null;
			}
		}
	}
	
	/** line_number_table 의 엔트리*/
	static class LineNumber_Entry implements IReset {
		/** Code_Attribute의 code 배열안 인덱스, 소스파일에서 새로운 라인이 시작되었다.
		 * code_length 보다 작아야 한다.
		 */
		short start_pc;
		/** start_pc에 대응하는 소스파일의 새로운 라인 번호*/
		short line_number;
		public static LineNumber_Entry read(InputStream is, boolean IsLittleEndian) {
			LineNumber_Entry r = new LineNumber_Entry();
			r.start_pc = IO.readShort(is, IsLittleEndian);
			r.line_number = IO.readShort(is, IsLittleEndian);
			return r;
		}
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
		}
	}
	
	/** 디버거에 의해 사용된다. 
	 * Code_Attribute의 code 배열의 어떤 부분이 소스파일의 어떤 라인을 표현하는지를 말한다.
	 * @author kjy
	 *
	 */
	static class LineNumberTable_attribute implements IReset {
		/** attribute_name_index, attribute_length 이 처음 6바이트를 제외해야 한다.
		 *  Attribute_Info에서 이미 읽었다.*/
		short attribute_name_index;
		/** attribute_name_index, attribute_length 이 처음 6바이트를 제외해야 한다.
		 *  Attribute_Info에서 이미 읽었다.*/
		int attribute_length;
		/** line_number_table 내 엔트리 개수*/
		short line_number_table_length;
		/** 각 엔트리는 code array 의 어떤 부분이 소스파일의 어떤 라인과 대응하는지를 말한다.*/
		LineNumber_Entry[] line_number_table;
		
		/** Attribute_Info 의 attribute_length 만큼 읽어들인다.*/
		public static LineNumberTable_attribute toLineNumberTable_attribute(InputStream is, 
				ArrayList constantTable, boolean IsLittleEndian) throws IOException {
			LineNumberTable_attribute r = new LineNumberTable_attribute();
			// Attribute_info 에서 이미 읽었으므로 읽지 않는다.
			//r.attribute_name_index = IO.readShort(is);
			//r.attribute_length = IO.readInt(is);
			
			r.line_number_table_length = IO.readShort(is, IsLittleEndian);
			r.line_number_table = new LineNumber_Entry[r.line_number_table_length];
			int i;
			for (i=0; i<r.line_number_table_length; i++) {
				r.line_number_table[i] = LineNumber_Entry.read(is, IsLittleEndian);
			}
			return r;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.line_number_table!=null) {
				int i;
				for (i=0; i<this.line_number_table_length; i++) {
					this.line_number_table[i].destroy();
					this.line_number_table[i] = null;
				}
			}
		}
	}
	
	/** Attribute_Info의 한 종류(attribute_name이 Code 이면), 
	 * Method_Info일 경우 attribute 이다.*/
	static class Code_attribute implements IReset {
		/** attribute_name_index, attribute_length 이 처음 6바이트를 제외해야 한다.
		 *  Attribute_Info에서 이미 읽었다.*/
		short attribute_name_index;
		/** attribute_name_index, attribute_length 이 처음 6바이트를 제외해야 한다.
		 *  Attribute_Info에서 이미 읽었다.*/
		int attribute_length; 
		/**  operand stack 의 최대깊이*/
		short max_stack;
		/** 지역변수 array 에서 메서드 아큐먼트를 포함한 지역변수의 개수,
		 * long, double 의 경우 최대 가능한 인덱스는 max_locals-2가 되고,
		 * 다른 타입은 max_locals-1이 된다.*/
		short max_locals;
		int code_length; 
		/** 실제 code*/
		byte[] code;//code_length
		/** 예외 테이블의 엔트리 개수*/
		short exception_table_length;
		Exception_Entry[] exception_table;//exception_table_length
		short attributes_count;
		/** LineNumberTable ,LocalVariableTable,
		 * attributes which contain debugging information,
		as well as the StackMapTable attribute 등을 포함한다. */
		Attribute_Info[] attributes;//attributes_count,
		PathClassLoader pathClassLoader;
		
		/** getLineNumberTableEntry()에서 설정되고, 캐시된다.*/
		LineNumberTable_attribute lineNumberTable = null;
		/** getLineNumberTableEntry()에서 사용되고 캐시된다. lineNumberTable의 검색 시작 인덱스*/
		int indexOfStartOfLineNumberTable;
		
		/** getLocalVariableTableEntry()에서 설정되고, 캐시된다.*/
		LocalVariableTable_attribute localVariableTable = null;
		
		
		/** Attribute_info 의 attribute_length 만큼 읽어들인다.*/
		public static Code_attribute toCode_attribute(PathClassLoader owner, InputStream is, 
				ArrayList constantTable, boolean IsLittleEndian) throws IOException {
			Code_attribute r = new Code_attribute();
			r.pathClassLoader = owner;
			//r.attribute_name_index = IO.readShort(is);
			//r.attribute_length = IO.readInt(is);
			
			// int test(int arg1, int arg2) {
			//		int r = arg1 + arg2;
			// 		return r;
			// }
			// 위 함수의 경우 다음 속성들의 값은 속성 위 주석값과 같다.
		
			// 3
			r.max_stack = IO.readShort(is, IsLittleEndian);
			// 1
			r.max_locals = IO.readShort(is, IsLittleEndian);
			// 10
			r.code_length = IO.readInt(is, IsLittleEndian);
			
			if (r.code_length==9) {
				int a;
				a=0;
				a++;
				
				
			}
			
			//static int test(int arg1, int arg2) { // implicit frame
			//	int r = arg1 + arg2;
			//	return r;
			//}
			// 이 소스코드의 바이트 코드는 다음과 같다.
			
			// [26(1a:iload_0), 27(1b:iload_1), 96(60:iadd), 61(3d:lstore_2), 28(1c:iload_2), -84(ac:ireturn)]
			//String opcode1 = IO.toHexa((byte)26);
			//String opcode3 = IO.toHexa((byte)96);
			//String opcode4 = IO.toHexa((byte)61);
			//String opcode5 = IO.toHexa((byte)28);
			//String opcode6 = IO.toHexa((byte)-84);
			
			
			
			//static int test2(Integer arg1, int arg2) { 
			//	int r = arg1.intValue() + arg2;
			//	return r;
			//}
			
			//[42(2a:aload_0), -74(b6:invokevirtual), 0(b6의 인덱스), 22(b6의 인덱스), 27(1b:iload_1), 96(60:iadd), 61(3d:lstore_2), 28(1c:iload_2), -84(ac:ireturn)]
			/*String opcode1 = IO.toHexa((byte)42);
			String opcode3 = IO.toHexa((byte)-74);
			Method method = (Method) constantTable.getItem((0<<8)+22);
			Object className = constantTable.getItem(method.classRef);
			NameAndTypeDesc nameAndTypeDescRef = (NameAndTypeDesc) constantTable.getItem(method.nameAndTypeDescRef);
			String methodName = ((CONSTANT_Utf8_info) constantTable.getItem(nameAndTypeDescRef.nameRef)).str;
			String typeDesc = ((CONSTANT_Utf8_info) constantTable.getItem(nameAndTypeDescRef.typeDesc)).str;*/
			
			
			r.code = new byte[r.code_length];
			is.read(r.code);
			
			// 0
			r.exception_table_length = IO.readShort(is, IsLittleEndian);
			r.exception_table = new Exception_Entry[r.exception_table_length];
			int i;
			for (i=0; i<r.exception_table_length; i++) {
				r.exception_table[i] = Exception_Entry.read(is, IsLittleEndian);
			}
			
			// 2
			r.attributes_count = IO.readShort(is, IsLittleEndian);
			// LineNumberTable, LocalVariableTable
			r.attributes = new Attribute_Info[r.attributes_count];
			for (i=0; i<r.attributes_count; i++) {
				r.attributes[i] = Attribute_Info.read(owner, is, constantTable, IsLittleEndian);
			}
			
			return r;
			
		}
		
		/** instruction 의 인덱스, 즉 otherByte로 LocalVariableTable_Entry를 얻는다.*/
		LocalVariableTable_Entry getLocalVariableTableEntry(int indexOfInstruction) {
			if (this.attributes==null) return null;
			int i;
			
			if (localVariableTable==null) {
				for (i=0; i<this.attributes_count; i++) {
					Attribute_Info info = this.attributes[i];
					if (info.attribute_name.equals("LocalVariableTable")) {
						localVariableTable = info.localVarTableAttribute;
						break;
					}
				}
			}
			if (localVariableTable!=null) {				
				if (indexOfInstruction<localVariableTable.local_variable_table.length)
					return localVariableTable.local_variable_table[indexOfInstruction];
				else 
					return null;
			}
			return null;
		}
		
		/** code array의 인덱스로 Exception_Entry(예외핸들러)를 찾는다.
		 * @param type : 0-start_pc, 1-end_pc, 2-handler_pc*/
		Exception_Entry[] getExceptionEntry(int indexInCodeArray, int type) {
			if (exception_table!=null) {
				int j;
				ArrayList r = new ArrayList(5);
				if (type==0) {
					for (j=0; j<exception_table.length; j++) {
						Exception_Entry entry = exception_table[j];
						if (entry.start_pc==indexInCodeArray) {
							r.add(entry);
						}
					}
				}//if (type==0) {
				else if (type==1) {
					for (j=0; j<exception_table.length; j++) {
						Exception_Entry entry = exception_table[j];
						if (entry.end_pc==indexInCodeArray) {
							r.add(entry);
						}
					}
				}//if (type==1) {
				else if (type==2) {
					for (j=0; j<exception_table.length; j++) {
						Exception_Entry entry = exception_table[j];
						if (entry.handler_pc==indexInCodeArray) {
							r.add(entry);
						}
					}
				}//if (type==2) {
				Exception_Entry[] result = new Exception_Entry[r.count]; 
				int i;
				for (i=0; i<r.count; i++) {
					result[i] = (Exception_Entry) r.getItem(i);
				}
				return result;
			}//if (exception_table!=null) {
			return null;
		}
		
		LineNumber_Entry getLineNumberEntry(int indexInCodeArray) {
			if (this.attributes==null) return null;
			int i;
			
			if (this.lineNumberTable==null) {
				for (i=0; i<this.attributes_count; i++) {
					Attribute_Info info = this.attributes[i];
					if (info.attribute_name.equals("LineNumberTable")) {
						lineNumberTable = info.lineNumberTableAttribute;
						break;
					}
				}
			}
			if (lineNumberTable!=null) {
				int j;
				j = this.indexOfStartOfLineNumberTable;
				for (; j<lineNumberTable.line_number_table_length; j++) {
					LineNumber_Entry entry = lineNumberTable.line_number_table[j];
					LineNumber_Entry nextEntry = null;
					if (j<lineNumberTable.line_number_table_length-1)
						nextEntry = lineNumberTable.line_number_table[j+1];
					if (nextEntry==null && entry.start_pc<=indexInCodeArray) {
						indexOfStartOfLineNumberTable = j;
						return entry;
					}
					if (entry.start_pc<=indexInCodeArray && 
							(nextEntry!=null && indexInCodeArray<nextEntry.start_pc)) {
						indexOfStartOfLineNumberTable = j;
						return entry;
					}
				}
				indexOfStartOfLineNumberTable = j;
			}
			return null;
		}
		
		/** instruction 인덱스의 지역변수 정보나 참조 이름, 분기주소 등의 메시지를 가져온다.*/
		CodeString getMessageOfIndices(ByteCodeInstruction instruction, ArrayList constantPool, 
				boolean IsLittleEndian)  throws Exception {
			
			CodeString r= new CodeString("", Compiler.varUseColor);
			
			switch (instruction.opcodeHexa) {
			case 0x19 : {//aload의 지역변수정보
				Short otherByte = (Short) instruction.indices.getItem(0);
				r = getMessageOfIndices_sub_LocalVar(otherByte);
			break;      }
			case 0x2a : {//aload_0의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(0);				
			break;      }
			case 0x2b : {//aload_1의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(1);
			break;      }
			case 0x2c : {//aload_2의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(2);
			break;      }
			case 0x2d : {//aload_3의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(3);				
			break;      }
			case 0xbd : {//anewarray 의 타입정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Class, IsLittleEndian);
			break;      }
			
			case 0x3a : {//astore의 지역변수정보
				Short otherByte = (Short) instruction.indices.getItem(0);
				r = getMessageOfIndices_sub_LocalVar(otherByte);		
			break;      }
			case 0x4b : {//astore_0의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(0);				
			break;      }
			case 0x4c : {//astore_1의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(1);				
			break;      }
			case 0x4d : {//astore_2의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(2);				
			break;      }
			case 0x4e : {//astore_3의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(3);				
			break;      }
			
			case 0x10 : {//bipush의 상수 정보
				r = new CodeString(" (", Compiler.varUseColor);
				Short otherByte = (Short) instruction.indices.getItem(0);
				r = r.concate(new CodeString(" "+otherByte, Compiler.varUseColor));
				r = r.concate(new CodeString(" )", Compiler.varUseColor));				
			break;      }
			
			case 0xc0 : {//checkcast의 타입캐스트 정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Class, IsLittleEndian);
			break;      }
			
			case 0x18 : {//dload의 지역변수정보
				Short otherByte = (Short) instruction.indices.getItem(0);
				r = getMessageOfIndices_sub_LocalVar(otherByte);		
			break;      }
			case 0x26 : {//dload_0의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(0);				
			break;      }
			case 0x27 : {//dload_1의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(1);				
			break;      }
			case 0x28 : {//dload_2의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(2);				
			break;      }
			case 0x29 : {//dload_3의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(3);				
			break;      }
			
			case 0x39 : {//dstore의 지역변수정보
				Short otherByte = (Short) instruction.indices.getItem(0);
				r = getMessageOfIndices_sub_LocalVar(otherByte);		
			break;      }
			case 0x47 : {//dstore_0의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(0);				
			break;      }
			case 0x48 : {//dstore_1의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(1);				
			break;      }
			case 0x49 : {//dstore_2의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(2);				
			break;      }
			case 0x4a : {//dstore_3의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(3);				
			break;      }
			
			
			case 0x17 : {//fload의 지역변수정보
				Short otherByte = (Short) instruction.indices.getItem(0);
				r = getMessageOfIndices_sub_LocalVar(otherByte);		
			break;      }
			case 0x22 : {//fload_0의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(0);				
			break;      }
			case 0x23 : {//fload_1의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(1);				
			break;      }
			case 0x24 : {//fload_2의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(2);				
			break;      }
			case 0x25 : {//fload_3의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(3);				
			break;      }
			
			case 0x38 : {//fstore의 지역변수정보
				Short otherByte = (Short) instruction.indices.getItem(0);
				r = getMessageOfIndices_sub_LocalVar(otherByte);		
			break;      }
			case 0x43 : {//fstore_0의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(0);				
			break;      }
			case 0x44 : {//fstore_1의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(1);				
			break;      }
			case 0x45 : {//fstore_2의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(2);				
			break;      }
			case 0x46 : {//fstore_3의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(3);				
			break;      }
			
			// getfield 부터
			case 0xb4 : {//getfield 의 필드 정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Field, IsLittleEndian);
			break;      }
			case 0xb2 : {//getstatic 의 필드 정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Field, IsLittleEndian);
			break;      }
			case 0xa7 : {//goto 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0xc8 : {//goto_w 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, true, IsLittleEndian);
			break;      }
			
			case 0xa5 : {//if_acmpeq 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0xa6 : {//if_acmpne 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0x9f : {//if_icmpeq 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0xa2 : {//if_icmpge 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0xa3 : {//if_icmpgt 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0xa4 : {//if_icmple 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0xa1 : {//if_icmplt 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0xa0 : {//if_icmpne 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			// 0과 비교
			case 0x99 : {//ifeq 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0x9c : {//ifge 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0x9d : {//ifgt 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0x9e : {//ifle 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0x9b : {//iflt 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0x9a : {//ifne 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0xc7 : {//ifnonnull 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0xc6 : {//ifnull 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			
			case 0x84 : {//iinc 의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar((Short)instruction.indices.getItem(0));				
			break;      }
			
			case 0x15 : {//iload 의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar((Short)instruction.indices.getItem(0));				
			break;      }
			case 0x1a : {//iload_0의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(0);				
			break;      }
			case 0x1b : {//iload_1의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(1);				
			break;      }
			case 0x1c : {//iload_2의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(2);				
			break;      }
			case 0x1d : {//iload_3의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(3);				
			break;      }
			
			case 0xc1 : {//instanceof 의 클래스 타입 정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Class, IsLittleEndian);
			break;      }
			case 0xba : {//invokedynamic 의 메서드 정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Method, IsLittleEndian);
			break;      }
			case 0xb9 : {//invokeinterface 의 메서드 정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.InterfaceMethod, IsLittleEndian);
			break;      }
			case 0xb7 : {//invokespecial 의 메서드 정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Method, IsLittleEndian);
			break;      }
			case 0xb8 : {//invokestatic 의 메서드 정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Method, IsLittleEndian);
			break;      }
			case 0xb6 : {//invokevirtual 의 메서드 정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Method, IsLittleEndian);
			break;      }
			
			case 0x36 : {//lstore 의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar((Short)instruction.indices.getItem(0));			
			break;      }
			case 0x3b : {//lstore_0의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(0);				
			break;      }
			case 0x3c : {//lstore_1의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(1);				
			break;      }
			case 0x3d : {//lstore_2의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(2);				
			break;      }
			case 0x3e : {//lstore_3의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(3);				
			break;      }
			
			case 0xa8 : {//jsr(jump to subroutine) 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			case 0xc9 : {//jsr_w(jump to subroutine) 의 분기 오프셋 정보
				r = getMessageOfIndices_sub_BranchOffset(instruction, true, IsLittleEndian);
			break;      }
			
			
			case 0x12 : {//ldc 의 constant pool 상수 정보
				r = this.getMessageOfIndices_sub_Constant(instruction, constantPool, false, IsLittleEndian);
			break;      }
			case 0x13 : {//ldc_w 의 constant pool 상수 정보
				r = this.getMessageOfIndices_sub_Constant(instruction, constantPool, true, IsLittleEndian);
			break;      }
			case 0x14 : {//ldc2_w 의 constant pool 상수 정보
				r = this.getMessageOfIndices_sub_Constant(instruction, constantPool, true, IsLittleEndian);
			break;      }
			
			case 0x16 : {//lstore 의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar((Short)instruction.indices.getItem(0));				
			break;      }
			case 0x1e : {//lstore_0의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(0);				
			break;      }
			case 0x1f : {//lstore_1의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(1);				
			break;      }
			case 0x20 : {//lstore_2의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(2);				
			break;      }
			case 0x21 : {//lstore_3의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(3);				
			break;      }
			
			// lookupswitch 부터
			case 0xab : {//loopupswitch 의 key 와 분기오프셋 정보
				r = getMessageOfIndices_sub_KeysAndBranchOffset(instruction);				
			break;      }
			
			case 0x37 : {//lstore 의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar((Short)instruction.indices.getItem(0));				
			break;      }
			case 0x3f : {//lstore_0의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(0);				
			break;      }
			case 0x40 : {//lstore_1의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(1);				
			break;      }
			case 0x41 : {//lstore_2의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(2);				
			break;      }
			case 0x42 : {//lstore_3의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar(3);				
			break;      }
			
			case 0xc5 : {//multianewarray 의 지역변수정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Class, IsLittleEndian);				
			break;      }
			
			case 0xbb : {//new 의 지역변수정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Class, IsLittleEndian);
			break;      }
			case 0xbc : {//newarray 의 지역변수정보
				r = getMessageOfIndices_sub_PrimitiveType(instruction);
			break;      }
			
			case 0xb5 : {//putfield 의 지역변수정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Field, IsLittleEndian);
			break;      }
			case 0xb3 : {//putstatic 의 지역변수정보
				r = getMessageOfIndices_sub_ClassType_Field_Method(instruction, constantPool, 
						CONSTANT_Info_Type.Field, IsLittleEndian);
			break;      }
			case 0xa9 : {//ret 의 지역변수정보
				r = getMessageOfIndices_sub_LocalVar((Integer)instruction.indices.getItem(0));
			break;      }
			
			case 0x11 : {//sipush 의 상수 정보
				r = this.getMessageOfIndices_sub_BranchOffset(instruction, false, IsLittleEndian);
			break;      }
			
			case 0xaa : {//tableswitch 의 키와 분기 주소 정보
				r = this.getMessageOfIndices_sub_KeysAndBranchOffset_Extended(instruction, IsLittleEndian);
			break;      }
			
			case 0xc4 : {//wide 의 분기 주소 정보
				r = getMessageOfIndices_sub_wide(instruction, IsLittleEndian);
			break;      }
			}
			
			return r;
		}
		
		/** tableswitch */
		CodeString getMessageOfIndices_sub_KeysAndBranchOffset_Extended (
				ByteCodeInstruction instruction, boolean IsLittleEndian)  throws Exception {
			
			int index;
			ArrayList indices = instruction.indices;
			int i;
			String message = "";
			String messageOfPrefix = "";
			// 0-3 바이트는 padding 바이트
			// key
			// key의 개수는 다음과 같다.
			int numOfPrefixes = 3;
			int limitOfPrefixes = instruction.countOfPadding + numOfPrefixes;
			
			i = instruction.countOfPadding;
			Integer defaultOffset = (Integer) indices.getItem(i);
			messageOfPrefix += " Default Offset : " + defaultOffset.toString();
			
			i++;
			Integer lowInt = (Integer) indices.getItem(i);
			int low = lowInt.intValue();
			messageOfPrefix += " Low : " + lowInt.toString();
			
			i++;
			Integer highInt = (Integer) indices.getItem(i);
			int high = highInt.intValue();
			messageOfPrefix += " High : " + highInt.toString();
			
					
			String branchOffset = " Offset :";
			int limitOfOffsets = instruction.indices.count;
			int k;
			for (i=limitOfPrefixes, k=low; i<limitOfOffsets; i++, k++) {
				branchOffset += "(";
				branchOffset += k + ", ";
				Integer offset = (Integer) indices.getItem(i);
				branchOffset += offset.toString();
				branchOffset += ")  ";
			}
			
			message = messageOfPrefix + branchOffset;
			
			CodeString r = new CodeString(" (", Compiler.varUseColor);
			r = r.concate(new CodeString(" " +message, Compiler.varUseColor));
			r = r.concate(new CodeString(" )", Compiler.varUseColor));
			return r;
		}
		
		/** lookupswitch */
		CodeString getMessageOfIndices_sub_KeysAndBranchOffset (ByteCodeInstruction instruction)  throws Exception {
			
			ArrayList indices = instruction.indices;
			int i;
			String message = "";
			String messageOfDefaultOffsetAndCountOfItems = "";
			// 0-3 바이트는 padding 바이트
			
			Integer defaultOffset = (Integer) indices.getItem(instruction.countOfPadding);
			messageOfDefaultOffsetAndCountOfItems += "Default Offset : " + defaultOffset.toString() + "  ";
			
			int indexOfCountOfItems = instruction.countOfPadding+1;
			Integer countOfItems = (Integer) indices.getItem(indexOfCountOfItems);
			messageOfDefaultOffsetAndCountOfItems += "count of items : " + countOfItems.toString() + "  ";
			
			int indexOfKeysAndOffsets = indexOfCountOfItems+1;
			String branchOffset = ", Keys and Offsets : ";
			int limitOfLoop = indices.count;
			
			for (i=indexOfKeysAndOffsets; i<limitOfLoop; i+=2) {
				branchOffset += "(";
				Integer key = (Integer) indices.getItem(i);
				branchOffset += key.toString();
				
				Integer offset = (Integer) indices.getItem(i);
				branchOffset += ", " + offset.toString();
				branchOffset += ") ";
			}
			
			message = messageOfDefaultOffsetAndCountOfItems + branchOffset;
			
			CodeString r = new CodeString(" (", Compiler.varUseColor);
			r = r.concate(new CodeString(" " +message, Compiler.varUseColor));
			r = r.concate(new CodeString(" )", Compiler.varUseColor));
			return r;
		}
		
		/** s1은 첫번째 바이트, s2는 두번째 바이트 이고 리턴값은 (첫번째바이트 << 8) + 두번째바이트*/
		int toInt(short s1, short s2, boolean IsLittleEndian)  throws Exception {
			//boolean endianBackup = IO.IsLittleEndian;
			try {
				//IO.IsLittleEndian = true;
				//short index = (short) (( (s1 & 0xff) << 8 ) | (s2 & 0xff));
				byte[] buf = {(byte) (s2 & 0xff), (byte) (s1 & 0xff), 0, 0};
				//index = IO.toShort(buf);
				return IO.toInt(buf, true);
			}catch(Exception e) {
				throw e;
			}finally {
				//IO.IsLittleEndian = endianBackup;
			}
		}
		
		/** s1은 첫번째 바이트, s2는 두번째 바이트, s3은 세번째 바이트, s4는 네번째 바이트 이고 
		 * 리턴값은 (첫번째바이트 << 24) + (두번째바이트 << 16) + (세번째바이트 << 8) + (네번째바이트 << 0)*/
		int toInt(short s1, short s2, short s3, short s4, boolean IsLittleEndian) 
				 throws Exception {
			//boolean endianBackup = IO.IsLittleEndian;
			try {
				//IO.IsLittleEndian = true;
				byte[] buf = {(byte) (s4 & 0xff), (byte) (s3 & 0xff), 
						(byte) (s2 & 0xff), (byte) (s1 & 0xff)};
				return IO.toInt(buf, true);
			}catch(Exception e) {
				throw e;
			}finally {
				//IO.IsLittleEndian = endianBackup;
			}
		}
		
		
		/** isWide가 false 이면 (첫번째바이트 << 8) + 두번째바이트*/
		CodeString getMessageOfIndices_sub_BranchOffset (
				ByteCodeInstruction instruction, boolean isWide, boolean IsLittleEndian)
						 throws Exception {
			
			int index;
			if (isWide==false) {
				ArrayList indices = instruction.indices;
				index = toInt((Short)indices.getItem(0), (Short)indices.getItem(1), true);
			}
			else {
				ArrayList indices = instruction.indices;
				index = toInt((Short)indices.getItem(0), (Short)indices.getItem(1), 
						(Short)indices.getItem(2), (Short)indices.getItem(3), true);
			}
			
			String branchOffset = String.valueOf(index);
			
			CodeString r = new CodeString(" (", Compiler.varUseColor);
			r = r.concate(new CodeString(" " +branchOffset, Compiler.varUseColor));
			r = r.concate(new CodeString(" )", Compiler.varUseColor));
			return r;
		}
		
		/** isWide가 false 이면 (첫번째바이트 << 8) + 두번째바이트*/
		CodeString getMessageOfIndices_sub_wide (ByteCodeInstruction instruction, boolean IsLittleEndian) 
				 throws Exception {
			
			int index;
			int count;
			
			ArrayList indices = instruction.indices;
			short opcode = (Short) indices.getItem(0);
			ByteCodeInstruction instructionInHash = 
					(ByteCodeInstruction) hashTableInstructionSet.getData(opcode);
			String mnemonic = " " + instructionInHash.mnemonic;
			String message = mnemonic;
			
			if (indices.count==3) {
				index = toInt((Short)indices.getItem(1), (Short)indices.getItem(2), true);
				message += " " + index;
			}
			else if (indices.count==5) {
				index = toInt((Short)indices.getItem(1), (Short)indices.getItem(2), true);
				count = toInt((Short)indices.getItem(3), (Short)indices.getItem(4), true);
				message += " " + index + " " + count;
			}
			
			CodeString r = new CodeString(" (", Compiler.varUseColor);
			r = r.concate(new CodeString(" " +message, Compiler.varUseColor));
			r = r.concate(new CodeString(" )", Compiler.varUseColor));
			return r;
		}
		
		/** 인덱스로 constant pool 을 검색한다.*/
		CodeString getMessageOfIndices_sub_Constant (ByteCodeInstruction instruction, 
				ArrayList constantPool, boolean isWide, boolean IsLittleEndian) 
						 throws Exception {
			ArrayList indices = instruction.indices;
			int index;
			if (isWide) {
				index = toInt((Short)indices.getItem(0), (Short)indices.getItem(1), true);
			}
			else {
				index = (Short) indices.getItem(0);
			}
			
			if (index==34) {
				int a;
				a=0;
				a++;
			}
			String message = "";
			Object item = constantPool.getItem(index);
			if (item instanceof CONSTANT_String_info) {
				int stringIndex = ((CONSTANT_String_info)item).string_index;
				message = ((CONSTANT_Utf8_info) constantPool.getItem(stringIndex)).str;
			}
			else if (item instanceof CONSTANT_Integer_info) {
				int value = ((CONSTANT_Integer_info)item).integer;
				byte[] buf = IO.toBytes(value, false);
				value = IO.toInt(buf, false);
				message = String.valueOf(value);
			}
			else if (item instanceof CONSTANT_Float_info) {
				float value = ((CONSTANT_Float_info)item).f;
				message = String.valueOf(value);
			}
			else if (item instanceof CONSTANT_Long_info) {
				long value = ((CONSTANT_Long_info)item).l;
				message = String.valueOf(value);
			}
			else if (item instanceof CONSTANT_Double_info) {
				double value = ((CONSTANT_Double_info)item).d;
				message = String.valueOf(value);
			}
			
						
			CodeString r = new CodeString(" (", Compiler.varUseColor);
			r = r.concate(new CodeString(" " +message, Compiler.varUseColor));
			r = r.concate(new CodeString(" )", Compiler.varUseColor));
			return r;
		}
		
		
		enum CONSTANT_Info_Type {
			Class,
			Field,
			Method,
			InterfaceMethod
		}
		
		/** newarray */
		CodeString getMessageOfIndices_sub_PrimitiveType(ByteCodeInstruction instruction) {
			ArrayList indices = instruction.indices;
			short s1 = (Short) indices.getItem(0);
			String message = " " + s1;
			
			CodeString r = new CodeString(" (", Compiler.varUseColor);
			r = r.concate(new CodeString(" " +message, Compiler.varUseColor));
			r = r.concate(new CodeString(" )", Compiler.varUseColor));
			return r;
		}
		
		/** anewarray, checkcast 등 에서 타입을 필요로할때나 필드, 메서드 레퍼런스를 얻을때 호출*/
		CodeString getMessageOfIndices_sub_ClassType_Field_Method (ByteCodeInstruction instruction, 
				ArrayList constantPool, CONSTANT_Info_Type infoType, boolean IsLittleEndian) 
						 throws Exception {
			/*ArrayList indices = instruction.indices;
			short s1 = (short) indices.getItem(0);
			short s2 = (short) indices.getItem(1);
			//short index = (short) (( (s1 & 0xff) << 8 ) | (s2 & 0xff));
			byte[] buf = {(byte) (s2 & 0xff), (byte) (s1 & 0xff)};
			short index = IO.toShort(buf);*/
			ArrayList indices = instruction.indices;
			int index = toInt((Short)indices.getItem(0), (Short)indices.getItem(1), true);
			
			String message = "";
			if (infoType==CONSTANT_Info_Type.Class) {
				CONSTANT_Class_info classInfo = 
						(CONSTANT_Class_info) constantPool.getItem(index);
				message = ((CONSTANT_Utf8_info) constantPool.getItem(classInfo.name_index)).str;
				//message = CompilerHelper.setPathSeparatorToDot(message);
			}
			else if (infoType==CONSTANT_Info_Type.Field) {
				CONSTANT_Field_info fieldInfo = 
						(CONSTANT_Field_info) constantPool.getItem(index);
				message = fieldInfo.toString();
			}
			else if (infoType==CONSTANT_Info_Type.Method) {
				CONSTANT_Method_info method = (CONSTANT_Method_info) constantPool.getItem(index);
				
				message = method.toString();
			}
			else if (infoType==CONSTANT_Info_Type.InterfaceMethod) {
				CONSTANT_InterfaceMethod_info method = (CONSTANT_InterfaceMethod_info) constantPool.getItem(index);				
				message = method.toString();
			}
			
			CodeString r = new CodeString(" (", Compiler.varUseColor);
			r = r.concate(new CodeString(" " +message, Compiler.varUseColor));
			r = r.concate(new CodeString(" )", Compiler.varUseColor));
			return r;
		}
		
		CodeString getMessageOfIndices_sub_LocalVar(int indexOfLocalVarInStackFrame) {
			CodeString r = new CodeString(" (", Compiler.varUseColor);
			LocalVariableTable_Entry localVar = 
					getLocalVariableTableEntry(indexOfLocalVarInStackFrame);
			if (localVar==null) { // error
				return null;
			}
			
			Field_Info field_Info = new Field_Info();
			field_Info.name = localVar.name;
			field_Info.descriptor = localVar.descriptor;
			FindVarParams var = Field_Info.toFindVarParams(pathClassLoader.compiler, field_Info, null, false);
			var.fieldName = var.fieldName;
			String message = var.toString();
			
			r = r.concate(new CodeString(" " +message, Compiler.varUseColor));
			r = r.concate(new CodeString(" )", Compiler.varUseColor));
			return r;
		}
		
		
		/**"lookupswitch" (4개이상), "tableswitch" (4개이상), "wide" (3개나 5개) 은 
		 * 가변 인덱스를 갖는다. Code_attribute의 toCodeString()에서 인덱스 개수가 결정된다.<br>
		 * 
		 * tableswitch<br>
		   4+: [0-3 bytes padding],<br> 
		   case의 조건들 : defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4,<br> 
		   jump offsets : lowbyte1, lowbyte2, lowbyte3, lowbyte4, highbyte1, highbyte2, highbyte3, highbyte4,<br> 
			...<br>
				
			switch (i) {<br>
			      case 0:  return  0;<br>
			      case 1:  return  1;<br>
			      case 2:  return  2;<br>
			      default: return -1;<br>
			}<br>
				<br>
		
			Type Description <br>
			u1   tableswitch opcode = 0xAA (170)<br> 
			-    0-3 bytes of padding ... <br>
			s4   default_offset <br>
			s4   <low> <br>
			s4   <low> + N - 1<br> 
			s4   offset_1 <br>
			s4   offset_2 <br>
			... <br>
			... <br>
			s4   offset_N <br> 
		 * @throws Exception <br>
			* */
		static int processTableSwitch(int i, byte[] code, ByteCodeInstruction instruction) throws Exception {
			int indexInstruction = i;
			
			int indexPadding;
			for (indexPadding=i+1; indexPadding<code.length; indexPadding++) {
				if (code[indexPadding]!=0) break;
			}
			
			// i+1은 패딩바이트의 시작인덱스
			int indexOfDefaultOffset = indexPadding;
			
			short countOfPadding = (short) ((indexPadding-1)-(i+1)+1);
			instruction.countOfPadding = countOfPadding;
			int k;
			for (k=0; k<countOfPadding; k++) {
				instruction.indices.add(new Short((short)0));
			}
			
			byte[] buffer = new byte[4];
			Util.copy(code, indexOfDefaultOffset, buffer, 0, buffer.length);
			int defaultOffset = i + IO.toInt(buffer, true);
			instruction.indices.add(defaultOffset);
			
			int indexOfLow = indexOfDefaultOffset + 4;
			Util.copy(code, indexOfLow, buffer, 0, buffer.length);
			int low = IO.toInt(buffer, true);
			instruction.indices.add(low);
			
			int indexOfHigh = indexOfLow + 4;
			Util.copy(code, indexOfHigh, buffer, 0, buffer.length);
			int high = IO.toInt(buffer, true);
			instruction.indices.add(high);
			
			int countOfLabels = high-low+1;
			//int countOfOffsets = countOfLabels+1; 
			int[] arrLabel = new int[countOfLabels];
			
			int m;
			int indexOfOffset = indexOfHigh + 4;
			
			for (m=0; m<countOfLabels; m++, indexOfOffset+=4) {
				Util.copy(code, indexOfOffset, buffer, 0, buffer.length);
				int offset = i + IO.toInt(buffer, true);
				arrLabel[m] = offset;
				instruction.indices.add(arrLabel[m]);
			}
			
			
			// indexPadding은 다음 명령어의 시작인덱스이거나 배열의 끝
			// defaultAddress + low + high + countOfLabels
			instruction.numOfOtherBytes = (short) (countOfPadding+4*(1+2+countOfLabels));
			if (instruction.numOfOtherBytes<0) {
				throw new Exception("can't read "+instruction);
			}
			
			i = indexInstruction;
			return i;
		}
		
		/**
		 * Type Description <br>
			u1  lookupswitch opcode = 0xAB (171) <br>
			-   ...0-3 bytes of padding ... <br>
			s4  default_offset <br>
			s4  n <br>
			s4  key_1 <br>
			s4  offset_1 <br>
			s4  key_2 <br>
			s4  offset_2 <br>
			... <br>
			... <br>
			s4  key_n <br>
			s4  offset_n <br>
		 * @param i
		 * @param code
		 * @param instruction
		 * @return
		 * @throws Exception
		 */
		static int processLookupSwitch(int i, byte[] code, ByteCodeInstruction instruction) throws Exception {
			int indexInstruction = i;
			
			int indexPadding;
			for (indexPadding=i+1; indexPadding<code.length; indexPadding++) {
				if (code[indexPadding]!=0) break;
			}
			
			// i+1은 패딩바이트의 시작인덱스
			int indexOfDefaultOffset = indexPadding;
			
			short countOfPadding = (short) ((indexPadding-1)-(i+1)+1);
			instruction.countOfPadding = countOfPadding;
			int k;
			for (k=0; k<countOfPadding; k++) {
				instruction.indices.add(new Short((short)0));
			}
			
			byte[] buffer = new byte[4];
			Util.copy(code, indexOfDefaultOffset, buffer, 0, buffer.length);
			int defaultOffset = i + IO.toInt(buffer, true);
			instruction.indices.add(defaultOffset);
			
			int indexOfCountOfItems = indexOfDefaultOffset + 4;
			Util.copy(code, indexOfCountOfItems, buffer, 0, buffer.length);
			int countOfItems = IO.toInt(buffer, true);
			instruction.indices.add(countOfItems);
			
			if (countOfItems<0) {
				return -1;
			}
			
			
			int[] arrKeys = new int[countOfItems];
			int[] arrLabel = new int[countOfItems];
			
			int m;
			int indexOfKeys = indexOfCountOfItems + 4;
			for (m=0; m<countOfItems; m++, indexOfKeys+=8) {
				Util.copy(code, indexOfKeys, buffer, 0, buffer.length);
				int key = IO.toInt(buffer, true);
				arrKeys[m] = key;
				instruction.indices.add(arrLabel[m]);
				
				int indexOfOffset = indexOfKeys + 4;
				Util.copy(code, indexOfOffset, buffer, 0, buffer.length);
				int offset = IO.toInt(buffer, true);
				arrLabel[m] = offset;
				instruction.indices.add(arrLabel[m]);
			}
			
			
			// indexPadding은 다음 명령어의 시작인덱스이거나 배열의 끝
			// defaultAddress + countOfItems + 2*(key+offset)
			instruction.numOfOtherBytes = (short) (countOfPadding+4*(1+1)+4*2*countOfItems);
			if (instruction.numOfOtherBytes<0) {
				throw new Exception("can't read "+instruction);
			}
			
			i = indexInstruction;
			return i;
		}
		
		
		static int processVariableInstruction(int i, byte[] code, ByteCodeInstruction instruction) throws Exception {
			if (instruction.mnemonic.equals("tableswitch")) {
				return processTableSwitch(i, code, instruction);
			}
			else if (instruction.mnemonic.equals("lookupswitch")) {
				return processLookupSwitch(i, code, instruction);
			}
			else {
				
			}
			return i;
		}
		
		/** 바이트코드를 읽을 경우 화면에 보여주기위해 Object.toString()을 override 한다.
		 * code 배열의 opcode와 인덱스들을 스트링으로 보여준다.*/
		public CodeString toCodeString(ArrayList constantPool, boolean IsLittleEndian) 
				 throws Exception {
			CodeString r = new CodeString("",Compiler.textColor);
			//boolean endianBackup = IO.IsLittleEndian;
			int i, j;
			int len = code.length;
			int indexOfExceptionTable = 0;
			
			for (i=0; i<len; i++) {
				CompilerHelper.showMessage(true, "i : " + i);
				
				if (i==278) {
					int a;
					a=0;
					a++;
				}
				
				byte opcodeByte = code[i];
				byte[] arr = {opcodeByte, 0};
				short key = IO.toShort(arr, true);
				ByteCodeInstruction instructionInHash = 
					(ByteCodeInstruction) hashTableInstructionSet.getData(key);
				ByteCodeInstruction instruction = new ByteCodeInstruction(instructionInHash);
				if (instruction.mnemonic.equals("if_icmpne")) {
					int a;
					a=0;
					a++;
				}
				

				if (instruction.hasVariableIndices() ) {
					
					i = processVariableInstruction(i, code, instruction);
					if (i<0) {
						return r;	
					}
					
				}//if (instruction.hasVariableIndices() ) {
				else { // 인덱스 개수가 고정개이거나 0개인 경우
					int k;
					// j 는 현재 opcode 의 다음 opcode 의 인덱스이거나 배열의 끝+1
					j = (i+1) + instruction.getLenOfIndices();
					
					for (k=i+1; k<j; k++) {
						byte b = code[k];
						byte[] arr4 = {b, 0};
						//boolean backupEndian = IO.IsLittleEndian;
						//IO.IsLittleEndian = true;
						Short otherByte2 = IO.toShort(arr4, true);
						//IO.IsLittleEndian = backupEndian;
						if (instruction.mnemonic.equals("goto")) {
							int a;
							a=0;
							a++;
						}
						instruction.indices.add(otherByte2);
					}
				}//else { // 인덱스 개수가 고정개이거나 0개인 경우
				
				if (instruction.mnemonic.equals("tableswitch")) {
					int a;
					a=0;
					a++;
				}
				
				// 먼저 instruction의 인덱스에 대한 정보를 확인하여
				// 에러가 있을 경우 instruction에 에러가 있다고 전해준다. 
				// Compiler.keywordColor로 instruction의 색깔을 바꾼다.
				// (instruction.toCodeString()을 참조)
				CodeString message = this.getMessageOfIndices(instruction, constantPool, IsLittleEndian);
				if (message==null) {
					instruction.hasError = true;
					message = new CodeString("Error instruction", Compiler.textColor);
				}
				else {
					instruction.hasError = false;
				}
				
				if (this.exception_table!=null && exception_table.length>0) {
					CodeString exceptionMessage = printExceptionEntry(i, constantPool);
					message = message.concate(exceptionMessage);
				}//if (this.exception_table!=null) {
				
				// 여기에서 i 는 instruction 의 opcode 의 오프셋
				r = r.concate(new CodeString(Util.getLineOffset(i) + " ",Compiler.textColor));
				r = r.concate(instruction.toCodeString());
				r = r.concate(new CodeString("; ",Compiler.textColor));
				
				
				
				r = r.concate(new CodeString(" [", Compiler.textColor));
				LineNumber_Entry entry = this.getLineNumberEntry(i);
				try {
				r = r.concate(new CodeString(entry.line_number+"", Compiler.keywordColor));
				}catch(Exception e) {
					e.printStackTrace();
					int a;
					a=0;
					a++;
				}
				r = r.concate(new CodeString("] //",Compiler.textColor));
				
				if (r.count==960) {
					int a;
					a=0;
					a++;
				}
				
				
				
				r = r.concate(message);
				
				
				r = r.concate(new CodeString("\n",Compiler.textColor));
				
				i += instruction.getLenOfIndices();
				
				//IO.IsLittleEndian = endianBackup;
				
				
			}//for (i=0; i<code.length; i++) {
			return r;
		}
		
		CodeString printExceptionEntry(int indexOfCodeArray, ArrayList constantPool) {
			int p;
			CodeString message = new CodeString("", Compiler.varUseColor); 
			for (p=0; p<=2; p++) {
				Exception_Entry[] entries = this.getExceptionEntry(indexOfCodeArray, p);
				if (entries!=null && entries.length>0) {
					int m;
					for (m=0; m<entries.length; m++) {
						Exception_Entry entry = entries[m];
						CodeString catchType = null;
						CONSTANT_Class_info classInfo = null;
						if (entry.catch_type==0) {
							classInfo = null;
						}
						else {
							classInfo = (CONSTANT_Class_info) constantPool.getItem(entry.catch_type);
						}
						if (classInfo==null) catchType = new CodeString("finally-handler:", Compiler.varUseColor);
						else catchType = new CodeString(classInfo.toString()+"-handler:", Compiler.varUseColor);
						catchType = catchType.concate(new CodeString(String.valueOf(entry.handler_pc)+", ",Compiler.varUseColor));
						
						if (p==0) { // start_pc, nested 될 수 있다. end_pc보다 항상 작다
							CodeString startOfTry = new CodeString(" Try starts and exception type is ", Compiler.varUseColor);
							startOfTry = startOfTry.concate(catchType);
							message = message.concate(startOfTry);
						}
						else if (p==1) { // end_pc, nested 될 수 있다. start_pc보다 항상 크다
							CodeString endOfTry = new CodeString(" Try ends in prev instruction and exception type is ", Compiler.varUseColor);
							endOfTry = endOfTry.concate(catchType);
							message = message.concate(endOfTry);
						}
						else if (p==2) { // handler_pc
							CodeString startOfExceptionHandler = new CodeString(" Start Of exception handler ", Compiler.varUseColor);
							startOfExceptionHandler = startOfExceptionHandler.concate(catchType);
							message = message.concate(startOfExceptionHandler);
						}								
					}//for (m=0; m<entries.length; m++) {
					return message; // start_pc는 end_pc보다 항상 작으므로 그대로 리턴한다.  
				}//if (entries!=null && entries.length>0) {
			}//for (p=0; p<=2; p++) {
			return message;
		}
		
		

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.attributes!=null) {
				int i;
				for (i=0; i<this.attributes_count; i++) {
					this.attributes[i].destroy();
					this.attributes[i] = null;
				}
				this.attributes = null;
			}
			if (this.exception_table!=null) {
				int i;
				for (i=0; i<this.exception_table_length; i++) {
					this.exception_table[i].destroy();
					this.exception_table[i] = null;
				}
				this.exception_table = null;
			}
			this.code = null;
		}
	}//Code_attribute
	
	
}