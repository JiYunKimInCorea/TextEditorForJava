package com.gsoft.common;

import com.gsoft.common.ByteCode_Types.ByteCodeInstruction;
import com.gsoft.common.ByteCode_Types.CONSTANT_Class_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Double_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Field_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Float_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Long_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Method_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_Integer_info;
import com.gsoft.common.ByteCode_Types.CONSTANT_String_info;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Compiler.ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub;
import com.gsoft.common.Compiler_types.AccessModifier.AccessPermission;
import com.gsoft.common.Compiler_types.Block;
import com.gsoft.common.Compiler_types.CategoryOfControls;
import com.gsoft.common.Compiler_types.FindArrayInitializerParams;
import com.gsoft.common.Compiler_types.FindClassParams;
import com.gsoft.common.Compiler_types.FindControlBlockParams;
import com.gsoft.common.Compiler_types.FindFuncCallParam;
import com.gsoft.common.Compiler_types.FindFunctionParams;
import com.gsoft.common.Compiler_types.FindIncrementStatementParams;
import com.gsoft.common.Compiler_types.FindSpecialBlockParams;
import com.gsoft.common.Compiler_types.FindSpecialStatementParams;
import com.gsoft.common.Compiler_types.FindStatementParams;
import com.gsoft.common.Compiler_types.FindVarParams;
import com.gsoft.common.Compiler_types.FindVarUseParams;
import com.gsoft.common.Compiler_types.FindAssignStatementParams;
import com.gsoft.common.Compiler_types.HighArrayCharForByteCode;
import com.gsoft.common.Util.HighArray;
import com.gsoft.common.Compiler_types.TypeCast;
import com.gsoft.common.PostFixConverter.CodeStringEx;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListChar;
import com.gsoft.common.Util.ArrayListIReset;
import com.gsoft.common.Util.ArrayListInt;
import com.gsoft.common.Util.Hashtable2_String;
import com.gsoft.common.Compiler_types.HighArray_CodeString;

public class ByteCodeGenerator {

	Compiler compiler;
	
	/** 가장 바깥(inner 가 아닌) 클래스의 리스트*/
	ArrayListIReset mlistOfClass;
	
	
	static Hashtable2_String hashTableInstructionSet;
	
	/** 코드에서 외부클래스의 필드나 메서드를 참조할 때 
	 * 참조하는 외부클래스의 풀이름과 필드나 메서드의 이름과 타입 디스크립터가 들어간다. 
	 * CONSTANT_Method_info, CONSTANT_Field_info 가 들어간다.*/
	//ArrayList listOfConstantTable_OuterRef = new ArrayList(50);
	/**CONSTANT_String_info, CONSTANT_Integer_info, CONSTANT_Float_info 등이 들어간다.*/
	//ArrayList listOfConstantTable_Constant = new ArrayList(50);
	
	/** 바이트코드를 생성하는 클래스의 ConstantTable*/
	HighArray<Object> listOfConstantTable = new HighArray<Object>(100);

	//CodeStringEx backupToken;
	//private ArrayList backupParents;
	
	
	/** 해시테이블을 생성해서 instructionSet 을 테이블에 넣는다. 한번만 초기화 가능*/
	static {
		if (hashTableInstructionSet==null) {
			hashTableInstructionSet = new Hashtable2_String(50, 5);
			int i;
			for (i=0; i<ByteCode_Types.instructionSet.length; i++) {
				ByteCodeInstruction instruction = ByteCode_Types.instructionSet[i];
				hashTableInstructionSet.input(instruction.mnemonic, instruction);
			}
		}
	}
	
	/** @param mlistOfClass : 가장 바깥(inner 가 아닌) 클래스의 리스트*/
	ByteCodeGenerator(Compiler compiler, ArrayListIReset mlistOfClass) {
		this.compiler = compiler;
		this.mlistOfClass = mlistOfClass;
		
		
	}
	
	
	
	
	
	
	/** func 의 파라미터 리스트와 리턴타입으로
	 *  ([Lcom/gsoft/common/Util$ArrayList;I)[[Lcom/gsoft/common/ColorEx; 
	 * 와 같은 디스크립터를 리턴한다.*/
	String getDescriptor(FindFunctionParams func) {
		if (func.name.equals("getIpAddress")) {
			int a;
			a=0;
			a++;
		}
		ArrayListChar descriptorArr = new ArrayListChar(50);
		
		// func 의 파라미터들에 대해서 디스크립터를 생성한다.
		descriptorArr.add('(');
		int i;
		for (i=0; i<func.listOfFuncArgs.count; i++) {
			FindVarParams param = (FindVarParams) func.listOfFuncArgs.getItem(i);
			ByteCode_Types.getDescriptor(param.typeName, descriptorArr);
		}
		descriptorArr.add(')');
		
		// func 의 리턴타입에 대해서 디스크립터를 생성한다.
		if (func.isConstructor) {
			ByteCode_Types.getDescriptor("void", descriptorArr);
		}
		else {
			ByteCode_Types.getDescriptor(func.returnType, descriptorArr);
		}
		return new String(descriptorArr.getItems());
	}
	
	/** [[Lcom/gsoft/common/ColorEx; 이와 같은 클래스의 디스크립터를 얻는다.*/
	String getDescriptorOfThis(FindClassParams classParams) {
		ArrayListChar result = new ArrayListChar(30);		
		ByteCode_Types.getDescriptor(classParams.name, result);
		result.add(" this");
		return new String(result.getItems());
	}
	
	/** [[Lcom/gsoft/common/ColorEx; 이와 같은 클래스의 디스크립터를 얻는다.*/
	String getDescriptor(FindClassParams classParams) {
		ArrayListChar result = new ArrayListChar(30);		
		ByteCode_Types.getDescriptor(classParams.name, result);
		return new String(result.getItems());
	}
	
	/** [[Lcom/gsoft/common/ColorEx; 혹은 I 이와 같은 필드의 디스크립터를 얻는다.*/
	String getDescriptor(FindVarParams var) {
		ArrayListChar result = new ArrayListChar(30);
		ByteCode_Types.getDescriptor(var.typeName, result);
		return new String(result.getItems());
	}
	
	
	/** varUse 가 참조하는 memberDecl 을 가지고 CONSTANT_Class_info 을 만들어서 
	 * varUse.constant_info 에 설정하고 
	 * listOfConstantTable 에 CONSTANT_Class_info 을 넣는다.
	 * @param varUse
	 */
	void makeCONSTANT_Class_infoAndPutItIntolistOfConstantTable(FindVarUseParams varUse) {
		if (varUse.memberDecl instanceof FindClassParams) {
			String className = ((FindClassParams)varUse.memberDecl).name;
			CONSTANT_Class_info classInfo = new CONSTANT_Class_info(className, compiler);
			varUse.constant_info = classInfo;
			this.listOfConstantTable.add(classInfo);
		}
	}
	
	
	
	/** varUse 가 참조하는 varDecl 을 가지고 CONSTANT_Field_info 을 만들어서 
	 * varUse.constant_info 에 설정하고 
	 * listOfConstantTable 에 CONSTANT_Field_info 을 넣는다.
	 * @param varUse
	 */
	void makeCONSTANT_Field_infoAndPutItIntolistOfConstantTable(FindVarUseParams varUse) {
		FindClassParams parentClass = (FindClassParams) varUse.varDecl.parent;
		String descriptor = this.getDescriptor(varUse.varDecl);
		CONSTANT_Field_info field = new CONSTANT_Field_info(parentClass.name, 
				varUse.originName, descriptor, compiler);
		varUse.constant_info = field;
		this.listOfConstantTable.add(field);
	}
	
	/** varUse 가 참조하는 funcDecl 을 가지고 CONSTANT_Method_info 을 만들어서 
	 * varUse.constant_info 에 설정하고 
	 * listOfConstantTable 에 CONSTANT_Method_info 을 넣는다.
	 * @param varUse
	 */
	void makeCONSTANT_Method_infoAndPutItIntolistOfConstantTable(FindVarUseParams varUse) {
		//if (varUse.funcDecl==null) return;
		FindClassParams parentClass = null;
		try {
		parentClass = (FindClassParams) varUse.funcDecl.parent;
		}catch(Exception e) {
			e.printStackTrace();
			int a;
			a=0;
			a++;
		}
		String descriptor = this.getDescriptor(varUse.funcDecl);
		CONSTANT_Method_info method = new CONSTANT_Method_info(parentClass.name, 
				varUse.funcDecl.name, descriptor, compiler);
		varUse.constant_info = method;
		this.listOfConstantTable.add(method);
	}
	
	/** varUse 가 참조하는 funcDecl 을 가지고 CONSTANT_Method_info 을 만들어서 
	 * varUse.constant_info 에 설정하고 
	 * listOfConstantTable 에 CONSTANT_Method_info 을 넣는다.
	 * @param varUse
	 */
	void makeCONSTANT_String_infoAndPutItIntolistOfConstantTable(FindVarUseParams varUse) {		
		CONSTANT_String_info str = new CONSTANT_String_info(varUse.name);
		varUse.constant_info = str;
		this.listOfConstantTable.add(str);
	}
	
	/** varUse 가 참조하는 숫자상수 을 가지고 CONSTANT_Integer_info 등을 만들어서 
	 * varUse.constant_info 에 설정하고 
	 * listOfConstantTable 에 CONSTANT_Integer_info 등을 넣는다.
	 * @param varUse
	 * @param numberType : 숫자가 아니면 0을 리턴, char이면 1, short이면 2,
	 * integer이면 3을 리턴, long이면 4를, float이면 5를, double이면 6, byte이면 7을 리턴한다.
	 * 16진수인 경우 CompilerHelper.IsHexa(str)을 참조한다.
	 * 
	 * byte나 short의 경우에는 bipush( byte 를 스택에 push), 
	 * sipush( short 를 스택에 push)를 써야 하므로
	 *  constant table 에 넣지 않는다.
	 */
	void makeCONSTANT_Number_infoAndPutItIntolistOfConstantTable(FindVarUseParams varUse, int numberType) {
		String varUseName = varUse.name;
		// str이 숫자이면 'f', 'd', 'l"과 같은 접미사를 제거한 스트링을 리턴한다.
		if (varUseName.length()>0) {
			char chLast = varUseName.charAt(varUseName.length()-1);
			if (('A'<=chLast && chLast<='Z') || ('a'<=chLast && chLast<='z')) {
				varUseName = varUseName.substring(0, varUseName.length()-1);
			}
		}
		switch (numberType) {
		case 1:  break; // char
		case 2:  break; // short
		case 3: //int			
			int value = Integer.parseInt(varUseName);
			CONSTANT_Integer_info integer = new CONSTANT_Integer_info(value);
			varUse.constant_info = integer;
			this.listOfConstantTable.add(integer); break;
		case 4: //long
			long valueLong = Long.parseLong(varUseName);
			CONSTANT_Long_info longValue = new CONSTANT_Long_info(valueLong);
			varUse.constant_info = longValue;
			this.listOfConstantTable.add(longValue); break;
		case 5: //float
			float valueFloat = Float.parseFloat(varUseName);
			CONSTANT_Float_info f = new CONSTANT_Float_info(valueFloat);
			varUse.constant_info = f;
			this.listOfConstantTable.add(f); break;
		case 6: // double
			double valueDouble = Double.parseDouble(varUseName);
			CONSTANT_Double_info d = new CONSTANT_Double_info(valueDouble);
			varUse.constant_info = d;
			this.listOfConstantTable.add(d); break;
		case 7: // byte 
			break;
		}
	}
	
	/** 
	 * Constant table 에 들어가는 CONSTANT_Field_info 나 CONSTANT_Method_info 를
	 * 만들어서 Constant table(listOfConstantTable) 에 넣는다.
	 * true, false, byte, short 는 constant table 에 넣지 않는다. 
	 * 이것들은 sipush, bipush 를 사용하여 스택에 직접 넣는다.<br>
	 * <br><br>
	 * 추가된 타입과 변수, 함수호출은 <br>
	 * 1. 배열초기화에서 사용되는 지역변수와 <br>
	 * 2. Compiler.putTryCatchShieldToSynchronizedOrFunction()에서 함수나 synchronized블록에서
	 * 예외가 발생할 경우 호출함수에 발생한 예외를 던지는 기능을 만들기 위해 추가된 try-catch블록에서
	 * 사용되는 java.lang.Exception e 지역변수가 있다.<br>
	 * 3. CompilerHelper.makeFuncCallToDefaultConstructorOfSuperClass()에서 생성자에서 super클래스의
	 * 생성자를 호출하는 문장이 없으면 super클래스의 디폴트 생성자를 호출하는 문장을 자동으로 만들어주기 위해
	 * FindIndependentFuncCallParam(독립적인 함수 호출문)과 varUseSuper(super())를 자동으로 만들어줘야 한다.<br>
	 * 4. 그리고 CompilerHelper.makeDefaultConstructor()와 CompilerHelper.makeStaticConstructor()에서 
	 * 인스턴스 필드와 스태틱 필드를 초기화하기 위해 자동으로 만들어주는 생성자들이 있다.<br>
	 * Compiler.FindAllClassesAndItsMembers2()의 마지막 부분을 참조한다.
	 * @param token
	 */
	void makeCONSTANT_info(FindVarUseParams varUse) {
		if (varUse.index()==11179) {
			int a;
			a=0;
			a++;
		}
		
		if (varUse.varDecl!=null) {
			FindVarParams varDecl = varUse.varDecl;
			boolean isMemberOrLocal = varDecl.isMemberOrLocal;
			if (isMemberOrLocal) { 
				if (varDecl.accessModifier!=null && varDecl.accessModifier.isStatic) {
					// static 필드를 참조, getstatic, putstatic
					makeCONSTANT_Field_infoAndPutItIntolistOfConstantTable(varUse);
				}
				else {
					if (varDecl.isThis || varDecl.isSuper) {
						// varUse 가 this, super, 아니면 클래스이름일 경우
						return;						
					}
					// this 등이 아닌 경우
					// getfield, putfield
					makeCONSTANT_Field_infoAndPutItIntolistOfConstantTable(varUse);
				}
			}//if (isMemberOrLocal) {
			else {
				// 로컬 변수 참조
				
				
			}
		}//if (varUse.varDecl!=null) {
		else if (varUse.funcDecl!=null) {
			// invokespecial(멤버함수), invokestatic(static 함수 호출), 
			// invokevirtual(부모클래스의 멤버함수로 자식클래스의 멤버함수를 호출)
			makeCONSTANT_Method_infoAndPutItIntolistOfConstantTable(varUse);
		}
		else if (varUse.memberDecl!=null) {
			// 타입캐스트, instanceof 타입, new 타입[3];
			if (varUse.index()==2284) {
				int a;
				a=0;
				a++;
			}
			boolean makes = false;
			if (varUse.typeCast!=null) makes = true;
			int indexPrev = compiler.getFullNameIndex0(compiler.mBuffer, true, varUse.index());
			indexPrev = compiler.SkipBlank(compiler.mBuffer, true, 0, indexPrev-1);
			CodeString prev = compiler.mBuffer.getItem(indexPrev);
			if (prev.equals("instanceof") || (prev.equals("new") && varUse.isArrayElement))
				makes = true;
			if (makes) {
				this.makeCONSTANT_Class_infoAndPutItIntolistOfConstantTable(varUse);
			}
		}
		else { // 숫자상수, 문자상수
			CodeString constant = compiler.mBuffer.getItem(varUse.index());
			if (constant.equals("30l")) {
				int a;
				a=0;
				a++;
			}
			if (constant.equals("true") || constant.equals("false")) { 
				// bipush( byte 를 스택에 push)를 써야 하므로 constant table 에 넣지 않는다.
				return;
			}
			
			int numberType = CompilerHelper.IsNumber2(constant);
			if (numberType!=0) {
				if (numberType==7) {
					// bipush( byte 를 스택에 push)를 써야 하므로 constant table 에 넣지 않는다.
					return;
				}
				else if (numberType==1 || numberType==2) {
					// sipush( short 를 스택에 push)를 써야 하므로 constant table 에 넣지 않는다.
					return;
				}
				
				this.makeCONSTANT_Number_infoAndPutItIntolistOfConstantTable(varUse, numberType);
			}//if (numberType!=0) {
			
			// 숫자가 아닐 경우
			if (CompilerHelper.IsConstant(constant)) {
				
				// 문자 상수
				char c = constant.charAt(0).c;
				if (c=='"')	{ // 문자열(스트링)
					this.makeCONSTANT_String_infoAndPutItIntolistOfConstantTable(varUse);
				}
				else if (c=='\'') { // 'a'와 같은 문자상수
					
				}
				
			}
		}//상수 일 경우
	}
	
	/** this.func() 혹은 func() 과 같은 멤버함수 호출일 경우
		invokespecial, invokevirtual 를 하기 전에 먼저 this 를 load 한다.
		static 함수 호출 일경우 invokestatic 을, 
		인스턴스 변수 호출일 경우 invokespecial, invokevirtual을 한다.*/
	void printFuncCall(FindVarUseParams varUse, HighArrayCharForByteCode result) {
		boolean isStatic = false;
		if (varUse.funcDecl.accessModifier!=null && varUse.funcDecl.accessModifier.isStatic)
			isStatic = true;
		if (isStatic) {
			result.add("invokestatic "+varUse.constant_info+"\n");
		}
		else { // invokespecial, invokevirtual
			boolean invokespecial = false;
			// 인스턴스 초기화 메서드를 호출하는 경우
			if (varUse.funcDecl.isConstructor) invokespecial = true;
			// private메서드를 호출하는 경우
			if (varUse.funcDecl.accessModifier!=null && 
				varUse.funcDecl.accessModifier.accessPermission==AccessPermission.Private) 
				invokespecial = true;
			// superclass의 메서드를 호출하는 경우에는  invokespecial이다.
			FindClassParams classInvokingFunc = varUse.classToDefineThisVarUse;
			FindClassParams classDefiningFunc = (FindClassParams) varUse.funcDecl.parent;
			if (FindClassParams.isARelation(compiler, classDefiningFunc, classInvokingFunc) &&
					classInvokingFunc.name.equals(classDefiningFunc.name)==false)
				invokespecial = true;
			
			if (invokespecial) {
				result.add("invokespecial "+varUse.constant_info+"\n");
				return;
			}
			
			boolean invokeinterface = false;
			FindClassParams parent = (FindClassParams) varUse.funcDecl.parent;
			if (parent.isInterface) {
				invokeinterface = true;
			}
			if (varUse.funcDecl.accessModifier!=null && varUse.funcDecl.accessModifier.isAbstract)
				invokeinterface = true;
			if (invokeinterface) {
				result.add("invokeinterface "+varUse.constant_info+"\n");
				return;
			}
			
			
			result.add("invokevirtual "+varUse.constant_info+"\n");
		}
	}
	
	/*void printTypeFullNameAfterOperation(FindExpressionParams expression, int j, 
			HighArrayCharForByteCode result) {		
		CodeStringEx[] postfix = expression.postfix;
		CodeStringEx operator = postfix[postfix.length-1];
		if (operator.indicesInSrc.list[0]==1883) {
			int a;
			a=0;
			a++;
		}
		String curType = postfix[j].typeFullName;
		if (CompilerHelper.IsOperator(curType)) return;
		
		printTypeCast_sub(curType, operator.typeFullName, result);
	}*/
	
	/**수식을 연산할때 operand나 operator(수식의 임시연산결과)가 묵시적 타입캐스트가 되는 것을 출력한다.
	 * 명시적 타입캐스트도 출력한다. 실제적으로 타입캐스트를 바이트코드로 변환한다. 
	 * checkcast는 타입캐스트가 object일 경우 */
	void printTypeCast_sub(String oldType, String curType, HighArrayCharForByteCode result) {
		if (oldType==null || curType==null) return;
		if (oldType.equals(curType)) return;
		
		if (CompilerHelper.IsDefaultType(oldType)==false && 
				CompilerHelper.IsDefaultType(curType)==false) {
			result.add("checkcast " +curType+"\n");
		}
		
		if (oldType.equals("byte") || oldType.equals("short") || oldType.equals("char")) {
			if (curType.equals("int")) {
				// 넣기만 하면 되므로 아무 일도 하지 않는다.
			}
			else if (curType.equals("long")) {
				result.add("i2l"+"\n");
			}
			else if (curType.equals("float")) {
				result.add("i2f"+"\n");
			}
			else if (curType.equals("doulbe")) {
				result.add("i2d"+"\n");
			}
		}
		else if (oldType.equals("int")) {
			if (curType.equals("byte")) {
				result.add("i2b"+"\n");
			}
			else if (curType.equals("char")) {
				result.add("i2c"+"\n");
			}
			else if (curType.equals("short")) {
				result.add("i2s"+"\n");
			}
			else if (curType.equals("long")) {
				result.add("i2l"+"\n");
			}
			else if (curType.equals("float")) {
				result.add("i2f"+"\n");
			}
			else if (curType.equals("doulbe")) {
				result.add("i2d"+"\n");
			}
		}
		else if (oldType.equals("long")) {
			if (curType.equals("byte")) {
				result.add("l2i"+"\n");
				result.add("i2b"+"\n");
			}
			else if (curType.equals("short")) {
				result.add("l2i"+"\n");
				result.add("i2s"+"\n");
			}
			else if (curType.equals("char")) {
				result.add("l2i"+"\n");
				result.add("i2c"+"\n");
			}
			else if (curType.equals("int")) {
				result.add("l2i"+"\n");
			}
			else if (curType.equals("float")) {
				result.add("l2f"+"\n");
			}
			else if (curType.equals("doulbe")) {
				result.add("l2d"+"\n");
			}
		}
		else if (oldType.equals("float")) {
			if (curType.equals("byte")) {
				result.add("f2i"+"\n");
				result.add("i2b"+"\n");
			}
			else if (curType.equals("short")) {
				result.add("f2i"+"\n");
				result.add("i2s"+"\n");
			}
			else if (curType.equals("char")) {
				result.add("f2i"+"\n");
				result.add("i2c"+"\n");
			}
			else if (curType.equals("int")) {
				result.add("f2i"+"\n");
			}
			else if (curType.equals("long")) {
				result.add("f2l"+"\n");
			}
			else if (curType.equals("doulbe")) {
				result.add("f2d"+"\n");
			}
		}
		else if (oldType.equals("double")) {
			if (curType.equals("byte")) {
				result.add("d2i"+"\n");
				result.add("i2b"+"\n");
			}
			else if (curType.equals("short")) {
				result.add("d2i"+"\n");
				result.add("i2s"+"\n");
			}
			else if (curType.equals("char")) {
				result.add("d2i"+"\n");
				result.add("i2c"+"\n");
			}
			else if (curType.equals("int")) {
				result.add("d2i"+"\n");
			}
			else if (curType.equals("long")) {
				result.add("d2l"+"\n");
			}
			else if (curType.equals("float")) {
				result.add("d2f"+"\n");
			}
		}
	}
	
	
	
	/** 작은 정수값이 아닌 constant table 에 들어가는 
	 * 더 큰 정수, 실수, 스트링 상수, 문자상수를 출력한다.*/
	void printConstant(FindVarUseParams varUse, HighArrayCharForByteCode result) {
		if (varUse.constant_info==null) return;
		
		// String, int or float : ldc_w
		// double or long : ldc2_w
		if (varUse.constant_info instanceof CONSTANT_String_info) {//ldc_w
			FindClassParams stringClass = CompilerHelper.loadClass(compiler, "java.lang.String");
			
			ArrayListChar message = new ArrayListChar(30);
			message.add("new ");
			message.add(getDescriptor(stringClass));
			message.add("\n");
			result.add(new String(message.getItems()));
			result.add("dup"+"\n");
			
			CONSTANT_String_info str = (CONSTANT_String_info) varUse.constant_info;
			result.add("ldc_w "+str+"\n");
			
			FindFunctionParams constructorOfStringClass = this.getConstructor(stringClass, 7);
			result.add("invokespecial "+constructorOfStringClass+"\n");
			
			
		}//if (varUse.constant_info instanceof CONSTANT_String_info) {//ldc_w
		else if (varUse.constant_info instanceof CONSTANT_Integer_info) {//ldc_w
			CONSTANT_Integer_info i = (CONSTANT_Integer_info) varUse.constant_info;
			result.add("ldc_w "+i+"\n");
		}//else if (varUse.constant_info instanceof CONSTANT_Integer_info) {//ldc_w
		else if (varUse.constant_info instanceof CONSTANT_Float_info) {//ldc_w
			CONSTANT_Float_info f = (CONSTANT_Float_info) varUse.constant_info;
			result.add("ldc_w "+f+"\n");
		}//else if (varUse.constant_info instanceof CONSTANT_Float_info) {//ldc_w
		else if (varUse.constant_info instanceof CONSTANT_Double_info) {//ldc2_w
			CONSTANT_Double_info d = (CONSTANT_Double_info) varUse.constant_info;
			result.add("ldc2_w "+d+"\n");
		}//else if (varUse.constant_info instanceof CONSTANT_Double_info) {//ldc2_w
		else if (varUse.constant_info instanceof CONSTANT_Long_info) {//ldc2_w
			CONSTANT_Long_info l = (CONSTANT_Long_info) varUse.constant_info;
			result.add("ldc2_w "+l+"\n");
		}//else if (varUse.constant_info instanceof CONSTANT_Long_info) {//ldc2_w
	}
	
	/** 다차원배열을 포함한 배열오브젝트 생성시 호출*/
	void printNewArray(FindVarUseParams varUse, HighArrayCharForByteCode result) {
		int dimension = CompilerHelper.getArrayDimension(compiler, varUse.name);
		if (dimension>1) {
			String typeName = null;
			if (CompilerHelper.IsDefaultType(varUse.originName)) {
				typeName = varUse.originName;
				result.add("multianewarray "+typeName+"\n");
			}
			else if (varUse.varDecl!=null) {
				typeName = ((FindClassParams)varUse.varDecl.parent).name;
				result.add("multianewarray "+typeName+"\n");
			}
			else if (varUse.memberDecl!=null) {
				typeName = ((FindClassParams)varUse.memberDecl).name;
				result.add("multianewarray "+typeName+"\n");
			}
		}
		else {
			String typeName = null;
			if (CompilerHelper.IsDefaultType(varUse.originName)) {
				typeName = varUse.originName;
				result.add("newarray "+typeName+"\n");
			}
			else if (varUse.varDecl!=null) {
				typeName = ((FindClassParams)varUse.varDecl.parent).name;
				result.add("anewarray "+typeName+"\n");
			}
			else if (varUse.memberDecl!=null) {
				typeName = ((FindClassParams)varUse.memberDecl).name;
				result.add("anewarray "+typeName+"\n");
			}
		}
	}
	
	void printArrayInitializer_newarray_NoBottom(int arrayLength, 
			int depth, FindVarUseParams varUse, HighArrayCharForByteCode result) {
		// array length print
		printArrayLength(arrayLength, depth, varUse, result);
		result.add("anewarray"+"\n");
	}
	
	
	void printArrayLength(int arrayLength, int depth, FindVarUseParams varUse, HighArrayCharForByteCode result) {
		int numberType = CompilerHelper.IsNumber2(String.valueOf(arrayLength));
		if (arrayLength<6) {
			switch (arrayLength) {
			case 0:	result.add("iconst_0"+"\n"); break;
			case 1:	result.add("iconst_1"+"\n"); break;
			case 2:	result.add("iconst_2"+"\n"); break;
			case 3:	result.add("iconst_3"+"\n"); break;
			case 4:	result.add("iconst_4"+"\n"); break;
			case 5:	result.add("iconst_5"+"\n"); break;
			}
			//result.add("iconst "+arrayLength+"\n");
			return;
		}
		if (numberType==7) {
			result.add("bipush "+arrayLength+"\n");
		}
		else if (numberType==1 || numberType==2) {
			result.add("sipush "+arrayLength+"\n");
		}
		else if (numberType==3) { // 배열초기화를 위한 임시 지역변수 
			FindVarParams var = getLocalVarForArrayInit_arrayLength(depth, varUse);
			result.add("iload "+var.toString()+"\n");
		}
		else if (numberType==4) {
			
		}
	}
	
	void printArrayInitializer_newarray_bottom(int arrayLength, int depth, FindVarUseParams varUse, String typeName, HighArrayCharForByteCode result) {
		// array length 출력
		printArrayLength(arrayLength, depth, varUse, result);
		
		if (CompilerHelper.IsDefaultType(typeName)) {
			result.add("newarray "+typeName+"\n");
		}
		else {
			result.add("anewarray "+typeName+"\n");
		}
	}
	
	/** maxIndex에 따라 작은 정수(byte, short)의 숫자 타입별로 num을 출력한다.*/
	void printSmallIntegerNumber(int num, int maxIndex, HighArrayCharForByteCode result) {
		int numberType = CompilerHelper.IsNumber2(String.valueOf(maxIndex));
		if (maxIndex<6) {
			switch (maxIndex) {
			case 0:	result.add("iconst_0"+"\n"); break;
			case 1:	result.add("iconst_1"+"\n"); break;
			case 2:	result.add("iconst_2"+"\n"); break;
			case 3:	result.add("iconst_3"+"\n"); break;
			case 4:	result.add("iconst_4"+"\n"); break;
			case 5:	result.add("iconst_5"+"\n"); break;
			}
			//result.add("iconst "+num+"\n");
			return;
		}
		if (numberType==7) {
			result.add("bipush "+num+"\n");
		}
		else if (numberType==1 || numberType==2) {
			result.add("sipush "+num+"\n");
		}
	}
	
	/**배열초기화에서 short범위를 넘어서는 인덱스를 위한 지역변수를 얻어온다.
	 * makeLocalVarsForArrayInit를 참조한다. 
	 * @param varUse : 배열초기화문의 lValue*/
	FindVarParams getLocalVarForArrayInit(int depth, FindVarUseParams varUse) {
		Object r = this.getFunctionOrClassToDefineLocalVarsForArrayInit(varUse);
		if (r==null) return null;
		if (r instanceof FindClassParams) {
			FindClassParams c = (FindClassParams) r;
			FindVarParams var = (FindVarParams) c.listOfLocalVarsForArrayInit.getItem(depth);
			return var;
		}
		else if (r instanceof FindFunctionParams) {
			FindFunctionParams func = (FindFunctionParams) r;
			FindVarParams var = (FindVarParams) func.listOfLocalVarsForArrayInit.getItem(depth);
			return var;
		}
		return null;
	}
	
	/**배열초기화에서 short범위를 넘어서는 array length를 위한 지역변수를 얻어온다.
	 * makeLocalVarsForArrayInit를 참조한다.  
	 * @param varUse : 배열초기화문의 lValue*/
	FindVarParams getLocalVarForArrayInit_arrayLength(int depth, FindVarUseParams varUse) {
		Object r = this.getFunctionOrClassToDefineLocalVarsForArrayInit(varUse);
		if (r==null) return null;
		if (r instanceof FindClassParams) {
			FindClassParams c = (FindClassParams) r;
			FindVarParams var = (FindVarParams) c.listOfLocalVarsForArrayInit_arrayLength.getItem(depth);
			return var;
		}
		else if (r instanceof FindFunctionParams) {
			FindFunctionParams func = (FindFunctionParams) r;
			FindVarParams var = (FindVarParams) func.listOfLocalVarsForArrayInit_arrayLength.getItem(depth);
			return var;
		}
		return null;
	}
	
	/** @param varUse : 배열초기화문의 lValue*/
	void printArrayInitializer_index(int index, int maxIndex, int depth, FindVarUseParams varUse, HighArrayCharForByteCode result){
		int numberType = CompilerHelper.IsNumber2(String.valueOf(maxIndex));
		if (numberType==0) {
			
		}
		else if (numberType==3) { // 배열초기화를 위한 임시 지역변수 
			FindVarParams var = getLocalVarForArrayInit(depth, varUse);
			result.add("iload "+var.toString()+"\n");
		}
		else if (numberType==4) {
			
		}
		else { // byte, char, short
			printSmallIntegerNumber(index, maxIndex, result);
		}
	}
	
	void printArrayInitializer_inc(int maxIndex, FindArrayInitializerParams topArray, 
			FindArrayInitializerParams curArray, HighArrayCharForByteCode result) {
		int numberType = CompilerHelper.IsNumber2(String.valueOf(maxIndex));
		if (numberType==3) { // 배열초기화를 위한 임시 지역변수 
			FindVarParams var = this.getLocalVarForArrayInit(curArray.depth, topArray.varUse);
			result.add("iinc "+var+"\n");
		}
		else if (numberType==4) {
			// ladd를 사용해야 한다.
		}
	}
	
	void printArrayInitializer_store_NoBottom(HighArrayCharForByteCode result) {
		result.add("aastore"+"\n");
	}
	
	void printArrayInitializer_store_bottom(FindFuncCallParam funcCall, HighArrayCharForByteCode result) {
		if (funcCall.typeFullName==null) {
			
			return;
		}
		String elementTypeName = funcCall.typeFullName.str;
		if (elementTypeName.equals("byte")) {
			result.add("bastore "+"\n");
		}
		else if (elementTypeName.equals("char")) {
			result.add("castore "+"\n");
		}
		else if (elementTypeName.equals("short")) {
			result.add("sastore "+"\n");
		}
		else if (elementTypeName.equals("int")) {
			result.add("iastore "+"\n");
		}
		else if (elementTypeName.equals("long")) {
			result.add("lastore "+"\n");
		}
		else if (elementTypeName.equals("float")) {
			result.add("fastore "+"\n");
		}
		else if (elementTypeName.equals("double")) {
			result.add("dastore "+"\n");
		}
		else {
			result.add("aastore "+"\n");
		}
	}
	
	/** a = i[0][0]; 에서 varUse는 i 이다. 
	 * 여기에서 마지막 첨자가 아닐 경우에는 aaload 를 출력하고 
	 * 마지막 첨자를 처리할 경우에는 iaload, caload 등을 배열원소 타입 별로 출력한다.*/
	void printArrayElement(FindVarUseParams varUse, HighArrayCharForByteCode result, 
			int curDimension, int dimensionOfArray, boolean loadsOrStores) {
		if (loadsOrStores) {
			if (varUse.isArrayElement) {
				// int[] func() {}   int i = func()[3]; 여기에서 varUse는 func이다.
				// 즉 varUse는 변수도 될 수 있고 리턴타입이 있는 함수호출도 될 수 있다.
				String typeName = varUse.varDecl!=null ? varUse.varDecl.typeName : varUse.funcDecl.returnType;
				String elementTypeName = CompilerHelper.getArrayElementType(typeName);
				if (curDimension<dimensionOfArray-1) { 
					// a = i[0][1]; 에서 i[0]는 0번째 행을 말한다.
					result.add("aaload "+"\n");
				}
				else {
					if (elementTypeName.equals("byte")) {
						result.add("baload "+"\n");
					}
					else if (elementTypeName.equals("char")) {
						result.add("caload "+"\n");
					}
					else if (elementTypeName.equals("short")) {
						result.add("saload "+"\n");
					}
					else if (elementTypeName.equals("int")) {
						result.add("iaload "+"\n");
					}
					else if (elementTypeName.equals("long")) {
						result.add("laload "+"\n");
					}
					else if (elementTypeName.equals("float")) {
						result.add("faload "+"\n");
					}
					else if (elementTypeName.equals("double")) {
						result.add("daload "+"\n");
					}
					else {
						result.add("aaload "+"\n");
					}
				}// else
				/*if (varUse.fullnameTypeCast!=null) {
					try {
					printTypeCast(varUse, elementTypeName, varUse.fullnameTypeCast, result);
					}catch(Exception e) {
						int a;
						a=0;
						a++;
					}
				}*/
			}//if (varUse.isArrayElement) {
		}//if (loadsOrStores==true)
		else {
			if (varUse.isArrayElement) {
				// store시에 func의 returnType의 배열원소에 값이 저장될 수 없다.
				String elementTypeName = CompilerHelper.getArrayElementType(varUse.varDecl.typeName);
				if (curDimension<dimensionOfArray-1) { 
					// i[0][1] = a; 에서 i[0]는 0번째 행을 말한다.
					result.add("aastore "+"\n");
				}
				else {
					if (elementTypeName.equals("byte")) {
						result.add("bastore "+"\n");
					}
					else if (elementTypeName.equals("char")) {
						result.add("castore "+"\n");
					}
					else if (elementTypeName.equals("short")) {
						result.add("sastore "+"\n");
					}
					else if (elementTypeName.equals("int")) {
						result.add("iastore "+"\n");
					}
					else if (elementTypeName.equals("long")) {
						result.add("lastore "+"\n");
					}
					else if (elementTypeName.equals("float")) {
						result.add("fastore "+"\n");
					}
					else if (elementTypeName.equals("double")) {
						result.add("dastore "+"\n");
					}
					else {
						result.add("aastore "+"\n");
					}
				}// else
				//varUse.hasPrintedCode = true;
			}//if (varUse.isArrayElement) {
		}
	}
	
	/**object 가 this 이고 getfield 일 경우 this 를 load 한다. 예를들어 aload this; getfield memberVar;  
	 * @param loadOrStore : true일 경우 getfield, getstatic, false일 경우 putstatic, putfield*/
	void printMemberVarUse(FindVarUseParams varUse, HighArrayCharForByteCode result, boolean loadOrStore) {
		if (varUse.index()==376) {
			int a;
			a=0;
			a++;
		}
		boolean isStatic = false;
		if (varUse.varDecl==null) return;
		if (varUse.index()==571) {
			int a;
			a=0;
			a++;
		}
		if (varUse.varDecl.accessModifier!=null && varUse.varDecl.accessModifier.isStatic)
			isStatic = true;
		if (loadOrStore) { // getfield, getstatic
			if (isStatic) {
				result.add("getstatic "+varUse.constant_info+"\n");
			}
			else {
				// this.varName 혹은 varName 과 같은 멤버변수일 경우
				// getfield 를 하기 전에 먼저 this 를 load 한다.
				if (varUse.parent==null && varUse.isUsingThisOrSuper) {					
					FindClassParams parentClass = (FindClassParams)varUse.classToDefineThisVarUse;
					result.add("aload "+this.getDescriptorOfThis(parentClass)+"\n");
				}
				if (varUse.name.equals("length") && 
					((FindClassParams)varUse.varDecl.parent).name.equals("com.gsoft.common.Array")) {
					// arr.length의 경우 length는 "com.gsoft.common.Array"에 정의되어 있다.					
					result.add("arraylength"+"\n");
				}
				else {
					// 일반적인 경우
					result.add("getfield "+varUse.constant_info+"\n");
				}
			}
		}//if (loadOrStore) { // getfield, getstatic
		else {
			if (isStatic) {
				result.add("putstatic "+varUse.constant_info+"\n");
			}
			else {
				result.add("putfield "+varUse.constant_info+"\n");
			}
		}
	}
	
	/** 작은 정수 상수 bipush, sipush 를 말한다.*/
	void printSmallIntegerConstant(FindVarUseParams varUse, HighArrayCharForByteCode result) {
		CodeString constant = compiler.mBuffer.getItem(varUse.index());
		if (constant.equals("true")) {
			result.add("bipush "+"1"+"\n");
			return;
		}
		else if (constant.equals("false")) {
			result.add("bipush "+"0"+"\n");
			return;
		}
		
		int numberType = CompilerHelper.IsNumber2(constant);
		if (numberType==7) { //byte
			result.add("bipush "+constant+"\n");
		}
		else if (numberType==1 || numberType==2) {//char, short
			result.add("sipush "+constant+"\n");
		}
		
	}
	
	/** @param loadOrStore : true일 경우 load, false일 경우 store*/
	void printLocalVar(FindVarUseParams varUse, HighArrayCharForByteCode result, boolean loadOrStore) {
		if (varUse.varDecl==null) return;
		// int a = (int)f + 2; 에서 타입 캐스트 int 가 varUse일 경우
		if (CompilerHelper.IsDefaultType(varUse.name)) return;
		String typeName = varUse.varDecl.typeName;
		
		if (loadOrStore) {
			if (typeName.equals("boolean") || typeName.equals("byte") || 
					typeName.equals("char") || typeName.equals("short") ||
					typeName.equals("int")) {
				result.add("iload "+varUse.varDecl+"\n");
				//printMakingValueClassWithValue(2, 0, varUse, result);
			}
			else if (typeName.equals("long")) {
				result.add("lload "+varUse.varDecl+"\n");
				//printMakingValueClassWithValue(2, 3, varUse, result);
			}
			else if (typeName.equals("float")) {
				result.add("fload "+varUse.varDecl+"\n");
				//printMakingValueClassWithValue(2, 1, varUse, result);
			}
			else if (typeName.equals("double")) {
				result.add("dload "+varUse.varDecl+"\n");
				//printMakingValueClassWithValue(2, 2, varUse, result);
			}
			else { // 오브젝트 지역변수
				result.add("aload "+varUse.varDecl+"\n");
			}
		}
		else {
			try {
			if (typeName.equals("boolean") || typeName.equals("byte") || 
					typeName.equals("char") || typeName.equals("short") ||
					typeName.equals("int")) {
				result.add("istore "+varUse.varDecl+"\n");
			}
			else if (typeName.equals("long")) {
				result.add("lstore "+varUse.varDecl+"\n");
			}
			else if (typeName.equals("float")) {
				result.add("fstore "+varUse.varDecl+"\n");
			}
			else if (typeName.equals("double")) {
				result.add("dstore "+varUse.varDecl+"\n");
			}
			else { // 오브젝트 지역변수
				result.add("astore "+varUse.varDecl+"\n");
			}
			}catch(Exception e) {
				e.printStackTrace();
				int a;
				a=0;
				a++;
			}
		}
	}
	
	/** +, -, *, /, mod 등의 산술연산자*/
	void printOperator(CodeStringEx operator, HighArrayCharForByteCode result) {
		if ((operator.equals("+") || operator.equals("-")) && operator.isPlusOrMinusForOne) {
			if (operator.equals("-")) {
				// x가 정수형일 경우 -x = ~x + 1 이다.
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("ineg"+"\n");
						result.add("iconst_1"+"\n");
						result.add("iadd"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("lneg"+"\n");
						result.add("lconst_1"+"\n");
						result.add("ladd"+"\n");
					}
					else if (operator.typeFullName.equals("float")) {
						// postfix에서는 -0.4f의 경우 0.4f, - 이렇게 구성이 된다.
						result.add("iconst_m1"+"\n");
						result.add("i2f"+"\n");
						result.add("fmul"+"\n");
					}
					else if (operator.typeFullName.equals("double")) {
						result.add("iconst_m1"+"\n");
						result.add("i2d"+"\n");
						result.add("dmul"+"\n");
					}
				}
			}
		}//if ((operator.equals("+") || operator.equals("-")) && operator.isPlusOrMinusForOne) {
		else if ((operator.equals("+") || operator.equals("-") || 
				operator.equals("*") || operator.equals("/") || operator.equals("%")) 
				&& operator.isPlusOrMinusForOne==false) { //이항산술연산자		
			if (operator.equals("+")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("iadd"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("ladd"+"\n");
					}
					else if (operator.typeFullName.equals("float")) {
						result.add("fadd"+"\n");
					}
					else if (operator.typeFullName.equals("double")) {
						result.add("dadd"+"\n");
					}
					else if (operator.typeFullName.equals("java.lang.String")) {
						this.printConcat(operator, result);
					}//else if (operator.typeFullName.equals("java.lang.String")) {
				}
			}//else if (operator.equals("+")) {
			else if (operator.equals("-")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("isub"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("lsub"+"\n");
					}
					else if (operator.typeFullName.equals("float")) {
						result.add("fsub"+"\n");
					}
					else if (operator.typeFullName.equals("double")) {
						result.add("dsub"+"\n");
					}
				}
			}
			else if (operator.equals("*")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("imul"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("lmul"+"\n");
					}
					else if (operator.typeFullName.equals("float")) {
						result.add("fmul"+"\n");
					}
					else if (operator.typeFullName.equals("double")) {
						result.add("dmul"+"\n");
					}
				}
			}
			else if (operator.equals("/")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("idiv"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("ldiv"+"\n");
					}
					else if (operator.typeFullName.equals("float")) {
						result.add("fdiv"+"\n");
					}
					else if (operator.typeFullName.equals("double")) {
						result.add("ddiv"+"\n");
					}
				}
			}
			else if (operator.equals("%")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("irem"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("lrem"+"\n");
					}
					else if (operator.typeFullName.equals("float")) {
						result.add("frem"+"\n");
					}
					else if (operator.typeFullName.equals("double")) {
						result.add("drem"+"\n");
					}
				}
			}
		}//이항산술연산자
		
		else if (operator.equals("<<") || operator.equals(">>") || operator.equals(">>>") ) {
			if (operator.equals("<<")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("ishl"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("lshl"+"\n");
					}
				}
			}
			else if (operator.equals(">>")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("ishr"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("lshr"+"\n");
					}
				}
			}
			else if (operator.equals(">>>")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("iushr"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("lushr"+"\n");
					}
				}
			}
		}// 시프트 연산자
		
		else if (operator.equals("&") || operator.equals("|") || operator.equals("^") ||
				operator.equals("~") ) {
			if (operator.equals("&")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("iand"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("land"+"\n");
					}
				}
			}
			else if (operator.equals("|")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("ior"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("lor"+"\n");
					}
				}
			}
			else if (operator.equals("^")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("ixor"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("lxor"+"\n");
					}
				}
			}
			else if (operator.equals("~")) {
				if (operator.typeFullName!=null) {
					if (operator.typeFullName.equals("byte") || operator.typeFullName.equals("char") ||
						operator.typeFullName.equals("short") || operator.typeFullName.equals("int")) {
						result.add("ineg"+"\n");
					}
					else if (operator.typeFullName.equals("long")) {
						result.add("lneg"+"\n");
					}
					else if (operator.typeFullName.equals("float")) {
						result.add("fneg"+"\n");
					}
					else if (operator.typeFullName.equals("double")) {
						result.add("dneg"+"\n");
					}
				}
			}
		}// 비트 논리 연산
		
		/*else if (operator.equals("=")) {
			// lValue 출력
			CodeStringEx leftOperand = operator.affectsLeft;
			int indexOfVarUseInMBuffer = compiler.getFullNameIndex(compiler.mBuffer, false, leftOperand.indicesInSrc.getItem(0));
			String varUseName = compiler.mBuffer.getItem(indexOfVarUseInMBuffer).str;
			FindVarUseParams varUse = compiler.getVarUseWithIndex(compiler.mlistOfAllVarUsesHashed, varUseName, indexOfVarUseInMBuffer).r;
			if (varUse.isArrayElement==false) {
				if (varUse.constant_info!=null) { 
					//멤버변수에 저장, putfield, putstatic
					this.printMemberVarUse(varUse, result, false);
				}
				else {//지역변수에 저장, istore, astore 등
					this.printLocalVar(varUse, result, false);
				}
			}
			else { 
				// 배열에 저장
				// arr[0] = i; 에서 varUse는 arr이다. 
				// aastore, iastore 등은 스택상태가 arrRef, index(첨자), value(rValue) 이어야 한다.
				// 따라서 arrRef, index(첨자)는 미리 스택에 로드되어 있어야 하고 rValue를 계산한 후에
				// value가 스택에 로드되면 aastore 등을 출력한다.
				int curDimension = CompilerHelper.getArrayDimension(compiler, varUse.name);
				this.printArrayElement(varUse, result, curDimension, curDimension, false);
			}
		}//대입 연산자*/
		
		else if (operator.equals("instanceof")) {
			FindVarUseParams varUse = (FindVarUseParams) operator.affectsRight.listOfVarUses.
					getItem(operator.affectsRight.listOfVarUses.count-1);
			result.add("instanceof "+ /*varUse.memberDecl*/varUse.constant_info + "\n");
		}
	}
	
	/** printOperator()의 '+'스트링 연결연산자의 오퍼랜드 타입에 따라 그 결과를 출력한다.
	 * @param operator : operator의 타입(opeator.typeFullName)은 java.lang.String이다.*/
	void printConcat(CodeStringEx operator, HighArrayCharForByteCode result) {
		FindFunctionParams concatFunc = null;
		FindClassParams stringClass = CompilerHelper.loadClass(compiler, "java.lang.String");
		concatFunc = this.getConcatFunctionInStringClass(stringClass);
		
		// 두번째 오퍼랜드가 String클래스이거나 그것의 서브클래스일 경우
		// String str = str1+str2;
		// String str = str1+obj; obj는 toString()을 갖고있는 클래스이다.
		// 두번째 오퍼랜드가 toString()을 갖고 있는 클래스일 경우
		// 두번째 오퍼랜드의 toString() 메서드를 호출하고 String의 concat()를 호출한다.
					
		result.add("invokevirtual "+concatFunc+"\n");
	}
	
	FindFunctionParams getConcatFunctionInStringClass(FindClassParams classParams) {
		int i, j;
		for (i=0; i<classParams.listOfFunctionParams.count; i++) {
			FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
			if (func.name.equals("concat")) {
				if (func.listOfFuncArgs.count==1) {
					FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(0);
					if (var.typeName.equals("java.lang.String")) {
						return func;
					}
				}
			}
		}
		return null;
	}
	
	FindFunctionParams getToStringFunction(FindClassParams classParams) {
		int i, j;
		for (i=0; i<classParams.listOfFunctionParams.count; i++) {
			FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
			if (func.name.equals("toString")) {
				if (func.listOfFuncArgs.count==0) return func;
			}
		}
		for (i=0; i<classParams.listOfFunctionParamsInherited.count; i++) {
			FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParamsInherited.getItem(i);
			if (func.name.equals("toString")) {
				if (func.listOfFuncArgs.count==0) return func;
			}
		}
		return null;
	}
	
	
	/** @param type : 0-Byte, 1-Char, 2-Short, 3-Integer, 4-Long, 5-Float, 6-Double, 7-String*/
	FindFunctionParams getConstructor(FindClassParams classParams, int type) {
		int i;
		if (type==0) { // Byte
			for (i=0; i<classParams.listOfFunctionParams.count; i++) {
				FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
				if (func.isConstructor) {
					if (func.listOfFuncArgs.count==1) {
						FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(0);
						if (var.typeName.equals("byte")) {
							return func;
						}
					}
				}
			}
		}
		else if (type==1) { //Char
			for (i=0; i<classParams.listOfFunctionParams.count; i++) {
				FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
				if (func.isConstructor) {
					if (func.listOfFuncArgs.count==1) {
						FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(0);
						if (var.typeName.equals("char")) {
							return func;
						}
					}
				}
			}
		}
		else if (type==2) { //Short
			for (i=0; i<classParams.listOfFunctionParams.count; i++) {
				FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
				if (func.isConstructor) {
					if (func.listOfFuncArgs.count==1) {
						FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(0);
						if (var.typeName.equals("short")) {
							return func;
						}
					}
				}
			}
		}
		else if (type==3) { //Integer
			for (i=0; i<classParams.listOfFunctionParams.count; i++) {
				FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
				if (func.isConstructor) {
					if (func.listOfFuncArgs.count==1) {
						FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(0);
						if (var.typeName.equals("int")) {
							return func;
						}
					}
				}
			}
		}
		else if (type==4) { //Long
			for (i=0; i<classParams.listOfFunctionParams.count; i++) {
				FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
				if (func.isConstructor) {
					if (func.listOfFuncArgs.count==1) {
						FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(0);
						if (var.typeName.equals("long")) {
							return func;
						}
					}
				}
			}
		}
		else if (type==5) { //Float
			for (i=0; i<classParams.listOfFunctionParams.count; i++) {
				FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
				if (func.isConstructor) {
					if (func.listOfFuncArgs.count==1) {
						FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(0);
						if (var.typeName.equals("float")) {
							return func;
						}
					}
				}
			}
		}
		else if (type==6) { //Double
			for (i=0; i<classParams.listOfFunctionParams.count; i++) {
				FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
				if (func.isConstructor) {
					if (func.listOfFuncArgs.count==1) {
						FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(0);
						if (var.typeName.equals("double")) {
							return func;
						}
					}
				}
			}
		}
		else if (type==7) { //String
			for (i=0; i<classParams.listOfFunctionParams.count; i++) {
				FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(i);
				if (func.isConstructor) {
					if (func.listOfFuncArgs.count==1) {
						FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(0);
						if (var.typeName.equals("byte[]")) {
							return func;
						}
					}
				}
			}
		}
		return null;
	}
	
	
	
	/** 사용자가 함수호출, lValue, 배열원소 등의 varUse를 클릭했을때 
	 * varUse 앞에 있는 일련의 object 들을 출력하기 위해 호출한다. 
	 * @param varUse
	 * @param result
	 * @param isForShowingOrNot : 이 함수는 보여주기 용이 아니더라도 호출이 되기 때문에 
	 * 실제로 컴파일을 할때는 false로 해서 호출해야 한다. 
	 * 그렇지 않으면 varUse 앞에 있는 일련의 object들이 중복되어서 출력된다.
	 * 
	 */
	void findParentAndPrintThem(FindVarUseParams varUse, HighArrayCharForByteCode result, boolean isForShowingOrNot) {
		if (isForShowingOrNot==false) return;
		//if (varUse.parent!=null && varUse.parent.hasPrintedCode) return;
		
		if (varUse.parent==null && varUse.isUsingThisOrSuper) {
			FindClassParams parentClass = (FindClassParams)varUse.classToDefineThisVarUse;
			result.add("aload "+this.getDescriptorOfThis(parentClass)+"\n");
			return;
		}
		// object1.object2.object3.var = i + 2; 이와 같은 경우에
		// object1.object2.object3 들을 object1을 스택에 놓고 getfield 를 해야 한다.
		
		// varUse 의 parent가 null이 될때까지 parent를 찾는다.
		// object1 을 찾는다.
		FindVarUseParams parent = varUse;
		while (true) {
			if (parent.parent==null) break;
			parent = parent.parent;				
		}
		
		// object1.object2.object3 들을 object1을 스택에 놓고 getfield 를 해야 한다.
		// var 는 LValue의 마지막 부분에서 putfield 를 통해 rValue 값을 넣는다.
		FindVarUseParams childLValue = parent;
		while (true) {
			if (childLValue.child==null) break;
			// object.var = i+2; 에서 childLValue 는 object 이다.
			this.traverseExpressionTree(compiler.mBuffer, childLValue, childLValue.index(), result);
			childLValue = childLValue.child;				
		}
	}
	
	FindStatementParams findFindStatementParamsInmlistOfControlBlocks(int indexInmBuffer) {
		int j;
		FindControlBlockParams controlBlock = null;
		for (j=0; j<compiler.mlistOfAllControlBlocks.count; j++) {
			controlBlock = (FindControlBlockParams) compiler.mlistOfAllControlBlocks.getItem(j);
			if (controlBlock.nameIndex()==indexInmBuffer) return controlBlock;
		}
		return null;
	}
	
	/** varUse를 포함하는 함수 또는 클래스 내에서 varUse를 갖는 최소의 문장을 찾아 리턴한다.
	 * 클래스인 경우 멤버변수 초기화 문장을 찾는다.*/
	FindStatementParams findFindStatementParams(FindVarUseParams varUse) {
		FindFunctionParams func = varUse.funcToDefineThisVarUse;
		FindClassParams classParams = varUse.classToDefineThisVarUse;
		int i;
		if (func!=null) {
			for (i=0; i<func.listOfStatements.count; i++) {
				FindStatementParams statement = (FindStatementParams) func.listOfStatements.getItem(i);
				if (statement instanceof FindVarParams) continue;
				int startIndex = statement.startIndex();
				int endIndex = statement.endIndex();
				if (startIndex<=varUse.index() && varUse.index()<=endIndex) {
					if (statement instanceof FindControlBlockParams) {
						FindControlBlockParams controlBlock = (FindControlBlockParams) statement;
						// 제어구조의 괄호내에 있는 varUse를 클릭했을때
						if (controlBlock.indexOfLeftParenthesis()<=varUse.index() && varUse.index()<=controlBlock.indexOfRightParenthesis()) {							
							return controlBlock;
						}
						// 제어구조내에서 문장찾기
						FindStatementParams r = findFindStatementParams(varUse, controlBlock);
						return r;
					}
					else {
						return statement;
					}
				}
				
			}
		}//if (func!=null) {
		else { // 클래스에 있는 멤버변수 초기화문
			for (i=0; i<classParams.listOfStatements.count; i++) {
				FindStatementParams statement = (FindStatementParams) classParams.listOfStatements.getItem(i);
				if (statement instanceof FindVarParams) continue;
				int startIndex = statement.startIndex();
				int endIndex = statement.endIndex();
				if (startIndex<=varUse.index() && varUse.index()<=endIndex) {
					if (statement instanceof FindClassParams) {
						continue;
					}
					else if (statement instanceof FindFunctionParams) {
						continue;
					}
					else {
						return statement;
					}
				}
				
			}
		}
		
		return null;
	}
	
	/** varUse를 포함하는 제어구조 내에서 varUse를 갖는 최소의 문장을 찾아 리턴한다.*/
	FindStatementParams findFindStatementParams(FindVarUseParams varUse, FindControlBlockParams controlBlock) {
		int i;
		if (controlBlock.indexOfLeftParenthesis()==31700) {
			int a;
			a=0;
			a++;
		}
		for (i=0; i<controlBlock.listOfStatements.count; i++) {
			FindStatementParams statement = (FindStatementParams) controlBlock.listOfStatements.getItem(i);
			if (statement instanceof FindVarParams) continue;
			int startIndex = statement.startIndex();
			int endIndex = statement.endIndex();
			
			if (startIndex<=varUse.index() && varUse.index()<=endIndex) {
				if (statement instanceof FindControlBlockParams) {
					FindControlBlockParams child = (FindControlBlockParams) statement;
					if (child.indexOfLeftParenthesis()<=varUse.index() && varUse.index()<=child.indexOfRightParenthesis())
						return child;
					return findFindStatementParams(varUse, child);
				}
				else {
					return statement;
				}
			}
		}
		return null;
	}
	
	/**if-elseif-else 구조에서 해당 if, else if, else블록이 끝나면 true, 아니면 false를 리턴한다. 
	 * @param controlBlock : if-elseif-else 구조에서 해당 if, else if, else블록*/
	boolean if_elseEnds(FindControlBlockParams controlBlock) {
		if (controlBlock.catOfControls==null) return true;
		if (controlBlock.catOfControls.category==CategoryOfControls.Control_else)
		{
			return true;
		}
		
		// else가 아니면
		Block parentBlock = controlBlock.parent;
		int i;
		FindStatementParams statement = (FindStatementParams) parentBlock.listOfControlBlocks.getItem(
				controlBlock.indexInListOfControlBlocksOfParent);
		int j;
		if (statement!=null) {
			if (controlBlock.catOfControls.category==CategoryOfControls.Control_if ||
					controlBlock.catOfControls.category==CategoryOfControls.Control_elseif)
			{
				
				if (parentBlock.listOfControlBlocks.count>controlBlock.indexInListOfControlBlocksOfParent+1) {
					FindControlBlockParams control = (FindControlBlockParams) parentBlock.listOfControlBlocks.getItem(
							controlBlock.indexInListOfControlBlocksOfParent+1);
					if (control.catOfControls==null) return true;//try, catch 등
					
					if ((control.catOfControls!=null && 
							(control.catOfControls.category==CategoryOfControls.Control_elseif ||
						control.catOfControls.category==CategoryOfControls.Control_else))) {
						// 다음 제어구조가 else if이거나 else이면
						return false;
					}
					else { // 다른 if나 아니면 다른 while, for등의 제어블록이 올 경우
						return true;
					}
				}
				else {
					// parentBlock에서 마지막 블록일 경우
					return true;
				}
			}
			
		}
		return true;
	}
	
	/*void copy(HighArrayCharForByteCode result, HighArrayCharForByteCode result) {
		int i;
		try {
		int len = result.getCount();
		for (i=0; i<len; i++) {
			result.add(result.getItem(i));
		}
		}catch(Exception e) {
			int a;
			a=0;
			a++;
			e.printStackTrace();
		}
	}*/
	
	
	void printWhile(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {
		int i;
		// condition
		result.add("// condition of "+controlBlock+"\n");
		
		//HighArrayCharForByteCode result = new HighArrayCharForByteCode(100); 
		this.printCondition(controlBlock, result);
		//this.copy(result, result);
		
		result.add("// exit_goto_w of "+"\n");
		result.add("goto_w "+"// exit of "+controlBlock+"\n");
		
		// run
		result.add("// run of "+controlBlock+"\n");
		for (i=0; i<controlBlock.listOfStatements.count; i++) {
			FindStatementParams statement = (FindStatementParams) controlBlock.listOfStatements.getItem(i);
			this.printFindStatementParams(statement, result);
		}
		
		
		result.add("goto_w "+"// go to condition of "+controlBlock+"\n");
		result.add("// exit of "+controlBlock+"\n");
	}
	
	void printSwitch(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {
		int low, high;
		int i;
		int[] arrValues = new int[controlBlock.listOfControlBlocks.count];
		int count = 0;
		for (i=0; i<controlBlock.listOfControlBlocks.count; i++) {
			FindControlBlockParams caseBlock = 
					(FindControlBlockParams) controlBlock.listOfControlBlocks.getItem(i);
			if (compiler.mBuffer.getItem(caseBlock.nameIndex()).equals("default")==false) {
				CodeString strValue = compiler.getFullName(compiler.mBuffer, 
						caseBlock.indexOfLeftParenthesis(), caseBlock.indexOfRightParenthesis()-1);
				int value = Integer.parseInt(strValue.str);
				arrValues[count++] = value;
			}
		}
		if (count>0) {
			low = arrValues[0];
			high = arrValues[0];
			for (i=1; i<count; i++) {
				int value = arrValues[i];
				if (low<value) low = value;
				else if (high>value) high = value;
			}
		}
		
		
	}
	
	void printDoWhile(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {
		int i;
		
		// run
		result.add("// run of "+controlBlock+"\n");
		for (i=0; i<controlBlock.listOfStatements.count; i++) {
			FindStatementParams statement = (FindStatementParams) controlBlock.listOfStatements.getItem(i);
			this.printFindStatementParams(statement, result);
		}
		
		// condition
		result.add("// condition of "+controlBlock+"\n");
		
		//HighArrayCharForByteCode result = new HighArrayCharForByteCode(100); 
		this.printCondition(controlBlock, result);
		//this.copy(result, result);
		
		result.add("// exit_goto_w of "+controlBlock+"\n");
		result.add("goto_w "+"// exit of "+controlBlock+"\n");
				
		result.add("// exit of "+controlBlock+"\n");
	}
	
	
	void printFor(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {		
		int indexOfSemicolon1 = controlBlock.funcCall.startIndex();
		indexOfSemicolon1 = compiler.SkipBlank(compiler.mBuffer, true, 0, indexOfSemicolon1-1);
		
		int indexOfSemicolon2 = controlBlock.funcCall.endIndex();
		indexOfSemicolon2 = compiler.SkipBlank(compiler.mBuffer, false, indexOfSemicolon2+1, compiler.mBuffer.count-1);
		
		if (indexOfSemicolon1==1655) {
			int a;
			a=0;
			a++;
		}
		int i;
		// 초기화 문장을 실행한다.
		for (i=0; i<controlBlock.listOfStatementsInParenthesis.count; i++) {
			FindStatementParams statement = (FindStatementParams) controlBlock.listOfStatementsInParenthesis.getItem(i);
			if (statement.endIndex()<=indexOfSemicolon1) {
				if (statement instanceof FindAssignStatementParams) {
					FindAssignStatementParams assign = (FindAssignStatementParams) statement;
					assign.rValue.typeFullName = compiler.getTypeOfExpression(compiler.mBuffer, assign.rValue);
				}
				this.printFindStatementParams(statement, result);
			}
		}
		
		// condition
		result.add("// condition of "+controlBlock+"\n");
		
		//HighArrayCharForByteCode result = new HighArrayCharForByteCode(100); 
		this.printCondition(controlBlock, result);
		//this.copy(result, result);
		
		result.add("// exit_goto_w of "+controlBlock+"\n");
		result.add("goto_w "+"// exit of "+controlBlock+"\n");
		
		// run
		result.add("// run of "+controlBlock+"\n");
		for (i=0; i<controlBlock.listOfStatements.count; i++) {
			FindStatementParams statement = (FindStatementParams) controlBlock.listOfStatements.getItem(i);
			this.printFindStatementParams(statement, result);
		}
		
		result.add("// increments of "+controlBlock+"\n");
		
		// for루프 괄호안 뒷 증감문
		for (i=0; i<controlBlock.listOfStatementsInParenthesis.count; i++) {
			FindStatementParams statement = (FindStatementParams) controlBlock.listOfStatementsInParenthesis.getItem(i);
			if (statement.startIndex()>=indexOfSemicolon2) {
				this.printFindStatementParams(statement, result);
			}
		}
		
		result.add("goto_w "+"// go to condition of "+controlBlock+"\n");
		result.add("// exit of "+controlBlock+"\n");
	}
	
	void printSynchronized(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {
		if (controlBlock.nameIndex()==201) {
			int a;
			a=0;
			a++;
		}
		result.add("// synchronized starts"+"\n");
		
		int startIndex = compiler.getIndexInmListOfAllVarUses(
				compiler.mBuffer, compiler.mlistOfAllVarUses, 0, 
				controlBlock.indexOfLeftParenthesis(), true);
		int endIndex = compiler.getIndexInmListOfAllVarUses(
				compiler.mBuffer, compiler.mlistOfAllVarUses, startIndex, 
				controlBlock.indexOfRightParenthesis(), false);
		traverse(startIndex, endIndex, result);
		
		result.add("monitorenter "+"\n");		
		this.printControlBlockBody(controlBlock, result);
		result.add("monitorexit "+"\n");
		
		result.add("// synchronized ends"+"\n");
	}
	
	CodeString getNameOfControlBlock(FindControlBlockParams controlBlock) {
		CodeString name = null;
		if (controlBlock.nameIndex()!=-1) {
			// 일반적인 controlBlock의 nameIndex는 null이 아니다
			name = compiler.mBuffer.getItem(controlBlock.nameIndex());
		}
		else if (controlBlock instanceof FindSpecialBlockParams) {
			// 가짜 try-catch블록
			FindSpecialBlockParams synchronizedBlock = (FindSpecialBlockParams) controlBlock;
			if (synchronizedBlock.specialBlockType==FindSpecialBlockParams.SpecialBlockType_try) {
				name = new CodeString("try", Compiler.textColor);
			}
			else if (synchronizedBlock.specialBlockType==FindSpecialBlockParams.SpecialBlockType_catch) {
				name = new CodeString("catch", Compiler.textColor);
			}
		}
		return name;
	}
	
	
	FindControlBlockParams[] getCatchBlocksOfTry(Block parentBlock, int indexInlistOfControlBlocksOfParent) {
		int i;
		ArrayList r = new ArrayList(5);
		int len = parentBlock.listOfControlBlocks.count;
		// try-catch-finally의 끝을 찾는다.
		for (i=indexInlistOfControlBlocksOfParent+1; i<len; i++) {
			FindControlBlockParams control = (FindControlBlockParams) parentBlock.listOfControlBlocks.getItem(i);
			CodeString name = this.getNameOfControlBlock(control);
			if (name.equals("catch")) {
				r.add(control);
			}
			else if (name.equals("finally")) {
				break;
			}
			else { // catch, finally를 제외한 controlBlock이 나오면 
				break;
			}
		}//for (i=indexInParent+1; i<len; i++) {
		FindControlBlockParams[] result = new FindControlBlockParams[r.count];
		for (i=0; i<r.count; i++) {
			result[i] = (FindControlBlockParams) r.getItem(i);
		}
		return result;
	}
	
	/** @return : 0 - catch:not exist, finally:not exist<br>
	 *  1 - catch:exist, finally:not exist<br> 
	 * 2 - catch:not exist, finally:exist<br>
	 *  3 - catch:exist, finally:exist   
	 * @param parentBlock
	 * @param indexInlistOfControlBlocksOfParent
	 */
	int catchOrFinallyExists(Block parentBlock, int indexInlistOfControlBlocksOfParent) {
		int i;
		boolean catchExists=false, finallyExists=false;
		int len = parentBlock.listOfControlBlocks.count;
		// try-catch-finally의 끝을 찾는다.
		for (i=indexInlistOfControlBlocksOfParent+1; i<len; i++) {
			FindControlBlockParams control = (FindControlBlockParams) parentBlock.listOfControlBlocks.getItem(i);
			CodeString name = this.getNameOfControlBlock(control);
			
			if (name.equals("catch")) {
				catchExists = true;
			}
			else if (name.equals("finally")) {
				finallyExists = true;
				break;
			}
			else { // catch, finally를 제외한 controlBlock이 나오면 
				break;
			}
		}//for (i=indexInParent+1; i<len; i++) {
		if (catchExists==false && finallyExists==false) return 0;
		else if (catchExists==true && finallyExists==false) return 1;
		else if (catchExists==false && finallyExists==true) return 2;
		else return 3;
	}
	
	
	void printTry(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {
		result.add("// try starts"+"\n");
		
		if (controlBlock.parent instanceof FindFunctionParams && controlBlock.isFake) {
			FindFunctionParams constructor = (FindFunctionParams) controlBlock.parent;
			if (constructor.isConstructor &&
					CompilerHelper.hasFuncCallToConstructorOfSuperClass(compiler, constructor)==false) {
				// super()을 출력해야 한다.
				FindVarUseParams varUseSuper = new FindVarUseParams(null);
				varUseSuper.index = null
						/*IndexForHighArray.indexRelative(varUseSuper, compiler.mBuffer, constructor.findBlockParams.startIndex()+1)*/;
				varUseSuper.name = "super";
				varUseSuper.originName = "super";
				if (varUseSuper.listOfFuncCallParams==null) {
					varUseSuper.listOfFuncCallParams = new ArrayListIReset(1);
				}
				varUseSuper.isFake = true;
				FindClassParams parentClass = (FindClassParams) constructor.parent;
				varUseSuper.classToDefineThisVarUse = parentClass;
				varUseSuper.funcToDefineThisVarUse = constructor;
				
				varUseSuper.funcDecl = compiler.getFunction(compiler.mBuffer, parentClass, varUseSuper, -1);
				
				this.makeCONSTANT_Method_infoAndPutItIntolistOfConstantTable(varUseSuper);
				
				this.traverseFuncCall(compiler.mBuffer, varUseSuper, -1, result);
			}
		}
		
		this.printControlBlockBody(controlBlock, result);
		
		int indexInlistOfControlBlocksOfParent = controlBlock.indexInListOfControlBlocksOfParent;
		int catchFinallyExists = this.catchOrFinallyExists(controlBlock.parent, indexInlistOfControlBlocksOfParent);
		
		// 예외는 발생하지 않았다.
		if (catchFinallyExists==2 || catchFinallyExists==3) {
			// 다음에 finally가 있을 경우
			result.add("goto_w "+"// finally"+"\n");
		}
		else {
			// 다음에 finally가 없이 catch만 있을 경우
			result.add("goto_w "+"// exit of try-catch-finally"+"\n");
		}
		
		result.add("// try ends"+"\n");
	}
	
	/** try-catch-finally의 끝은 catch이거나 finally가 된다.
	 * 발생한 예외를 catch의 괄호안에 있는 예외선언에 저장한다.
	 * try-catch-finally의 끝이면 exit of try-catch-finally을 출력한다.*/
	void printCatch(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {
		// 예외 선언을 찾는다.
		/*int startIndex = controlBlock.indexOfLeftParenthesis();
		int endIndex = controlBlock.indexOfRightParenthesis();
		
		Block parent = compiler.getParent(controlBlock);
		FindVarParams var = null;
		if (parent instanceof FindFunctionParams) {
			FindFunctionParams func = (FindFunctionParams) parent;
			int i;			
			for (i=0; i<func.listOfVariableParams.count; i++) {
				var = (FindVarParams) func.listOfVariableParams.getItem(i);
				if (startIndex<=var.startIndex() && var.endIndex()<=endIndex) {
					break;
				}
			}
			if (i==func.listOfVariableParams.count) { // not found
				var = null;
			}
		}*/
		
		result.add("// catch starts"+"\n");
		
		FindVarParams var = 
				(FindVarParams) controlBlock.listOfStatementsInParenthesis.getItem(0);
		
		// 발생한 예외를 catch의 괄호안에 있는 예외선언에 저장한다.
		result.add("astore "+var+"\n");
		
		this.printControlBlockBody(controlBlock, result);
		
		int indexInlistOfControlBlocksOfParent = controlBlock.indexInListOfControlBlocksOfParent;
		int catchFinallyExists = this.catchOrFinallyExists(controlBlock.parent, indexInlistOfControlBlocksOfParent);
		
		if (catchFinallyExists==2 || catchFinallyExists==3) { 
			// 다음에 finally가 있을 경우
			result.add("goto_w "+"// finally"+"\n");
			result.add("// catch ends"+"\n");
		}
		else if (catchFinallyExists==0) { 
			// 다음에 catch, finally가 없는 경우, 즉 현재 catch로 끝날 경우
			result.add("// catch ends"+"\n");
			result.add("// exit of try-catch-finally"+"\n");
		}
		else { // 1번의 경우, 다음에 catch가 또 있을때
			result.add("goto_w "+"// exit of try-catch-finally"+"\n");
			result.add("// catch ends"+"\n");
		}
		
		
	}
	
	/** try-catch-finally의 끝은 catch이거나 finally가 된다.
	 * try-catch-finally의 끝이면 exit of try-catch-finally을 출력한다.*/
	void printFinally(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {
		result.add("// finally starts"+"\n");
		this.printControlBlockBody(controlBlock, result);
		result.add("// finally ends"+"\n");
		result.add("// exit of try-catch-finally"+"\n");
	}
	
	/** 전체 try_catch_finally를 한번에 출력한다.*/
	void printTry_catch_finally(FindControlBlockParams tryBlock, HighArrayCharForByteCode result) {
		Block parentBlock = tryBlock.parent;
		int i;
		int indexEnd = parentBlock.listOfControlBlocks.count-1;
		for (i=tryBlock.indexInListOfControlBlocksOfParent+1; i<parentBlock.listOfControlBlocks.count; i++) {
			FindControlBlockParams controlBlock = (FindControlBlockParams) parentBlock.listOfControlBlocks.getItem(i);
			// 다음 controlBlock이 없는 try, catch문이거나 
			if ( controlBlock.catOfControls!=null) {
				indexEnd = i-1;
				break;
			}
			CodeString keyword = getNameOfControlBlock(controlBlock);
			
			// 다음 controlBlock이 catch나 finally가 아니면
			if ((keyword.equals("catch") || keyword.equals("finally"))==false) {
				indexEnd = i-1;
				break;
			}
			else {
				FindSpecialBlockParams catchOrFinally = (FindSpecialBlockParams) controlBlock;
				catchOrFinally.tryBlockConnected = (FindSpecialBlockParams) tryBlock;				
			}
		}		
		
		for (i=tryBlock.indexInListOfControlBlocksOfParent; i<=indexEnd; i++) {
			FindControlBlockParams controlBlock = (FindControlBlockParams) parentBlock.listOfControlBlocks.getItem(i);
			
			CodeString keyword = getNameOfControlBlock(controlBlock);
			
			if (keyword.equals("try")) {
				printTry(controlBlock, result);
			}
			else if (keyword.equals("catch")) {
				printCatch(controlBlock, result);
			}
			else if (keyword.equals("finally")) {
				printFinally(controlBlock, result);
			}
		}
	}
	
	
	/** 전체 If_ElseIf_Else를 한번에 출력한다.*/
	void printIf_ElseIf_Else(FindControlBlockParams ifBlock, HighArrayCharForByteCode result) {
		Block parentBlock = ifBlock.parent;
		int i;
		int indexEnd = parentBlock.listOfControlBlocks.count-1;
		for (i=ifBlock.indexInListOfControlBlocksOfParent+1; i<parentBlock.listOfControlBlocks.count; i++) {
			FindControlBlockParams controlBlock = (FindControlBlockParams) parentBlock.listOfControlBlocks.getItem(i);
			// 다음 controlBlock이 없는 try, catch문이거나 
			if ( controlBlock.catOfControls==null) {
				indexEnd = i-1;
				break;
			}
			// 다음 controlBlock이 else if나 else가 아니면
			if ((controlBlock.catOfControls.category==CategoryOfControls.Control_elseif || 
				controlBlock.catOfControls.category==CategoryOfControls.Control_else)==false) {
				indexEnd = i-1;
				break;
			}
		}
		
		//HighArrayCharForByteCode result = null;
		for (i=ifBlock.indexInListOfControlBlocksOfParent; i<=indexEnd; i++) {
			FindControlBlockParams controlBlock = (FindControlBlockParams) parentBlock.listOfControlBlocks.getItem(i);
			
			if (controlBlock.catOfControls.category==CategoryOfControls.Control_if) {
				//result = new HighArrayCharForByteCode(100);
				//result.resizeInc = 300;
				
				printIfControlBlock(controlBlock, result);				
				if (if_elseEnds(controlBlock)) {
					// if문의 끝나는 영역
					result.add("// exit"+" of "+controlBlock+"\n");
					result.add("// exit of if-elseif-else"+"\n");
					/*copy(result, result);
					if (result!=null) {
						result.destroy();
					}*/
				}
				else { // if-elseif-else에서 if나 else if구조가 끝났을 때
					result.add("goto_w "+"// exit of if-elseif-else"+"\n");
					// if문의 끝나는 영역
					result.add("// exit"+" of "+controlBlock+"\n");
				}
			}
			else if (controlBlock.catOfControls.category==CategoryOfControls.Control_elseif) {
				printIfControlBlock(controlBlock, result);
				if (if_elseEnds(controlBlock)) {
					// else if문의 끝나는 영역
					result.add("// exit"+" of "+controlBlock+"\n");
					result.add("// exit of if-elseif-else"+"\n");
					/*copy(result, result);
					if (result!=null) {
						//result.count = 0;
						result.destroy();
					}*/
				}
				else {// if-elseif-else에서 if나 else if구조가 끝났을 때
					result.add("goto_w "+"// exit of if-elseif-else"+"\n");
					// else if문의 끝나는 영역
					result.add("// exit"+" of "+controlBlock+"\n");
				}
			}
			else if (controlBlock.catOfControls.category==CategoryOfControls.Control_else) {
				printElseControlBlock(controlBlock, result);
				if (if_elseEnds(controlBlock)) {
					result.add("// exit of if-elseif-else"+"\n");
					/*copy(result, result);
					if (result!=null) {
						//result.count = 0;
						result.destroy();
					}*/
				}
				else {// if-elseif-else에서 if나 else if구조가 끝났을 때
					result.add("goto_w "+"// exit of if-elseif-else"+"\n");
				}
			}
		}//for (i=ifBlock.indexInListOfControlBlocksOfParent; i<=indexEnd; i++) {
		
	}
	
	
	/** main 함수 역할한다.*/
	public void generate() {
		HighArrayCharForByteCode result = new HighArrayCharForByteCode(500, true);
		int i;
		for (i=0; i<mlistOfClass.count; i++) {
			FindClassParams classParams = (FindClassParams) mlistOfClass.getItem(i);
			printClass(classParams, result);
		}
	}
	
	
	/** 클래스 단위로 부모/자식클래스들을 순회하며 재귀적 호출한다.*/
	void printClass(FindClassParams classParams, HighArrayCharForByteCode result) {
		if (classParams==null) return;
		
		int i;
		if (classParams.childClasses!=null) {	// 자식 클래스부터 바이트코드를 생성한다.	
			for (i=0; i<classParams.childClasses.count; i++) {
				FindClassParams child = (FindClassParams) classParams.childClasses.getItem(i);
				printClass(child, result);
			}
		}
		
		// 가장 아래 자식클래스이거나 자식클래스들의 코드를 모두 생성한 부모클래스가 된다.
		// 바이트코드를 생성한다.
		
	
		int j;
		for (j=0; j<classParams.listOfFunctionParams.count; j++) {
			FindFunctionParams func = (FindFunctionParams) classParams.listOfFunctionParams.getItem(j);
			printFunction(func, result);
		}
	}
	
	/** 부모클래스에 생성자가 없을 경우
	 * 자식클래스의 생성자에서 부모클래스의 생성자를 호출하는 super()가 없으면
	 * 자동으로 만들어 호출한다.
	 * @param func
	 * @param result
	 */
	void makeFuncCallToConstructorOfSuperClass(FindFunctionParams func, 
			HighArrayCharForByteCode result) {
		if (func.isConstructor) {
			int i;
			if (CompilerHelper.hasFuncCallToConstructorOfSuperClass(compiler, func)==false) { 
				FindVarUseParams varUseSuper = new FindVarUseParams(null);
				varUseSuper.name = "super";
				varUseSuper.originName = "super";
				if (varUseSuper.listOfFuncCallParams==null) {
					varUseSuper.listOfFuncCallParams = new ArrayListIReset(1);
				}
				varUseSuper.classToDefineThisVarUse = (FindClassParams) func.parent;
				varUseSuper.funcToDefineThisVarUse = func;
				FindClassParams classParams = (FindClassParams) func.parent;
				FindClassParams parentClass = classParams.classToExtend;
				FindFunctionParams defaultConstructorOfParentClass = null;
				if (func.isConstructorThatInitializesStaticFields==false) {
					// instance constructor
					for (i=0; i<parentClass.listOfFunctionParams.count; i++) {
						FindFunctionParams funcOfParent = (FindFunctionParams) parentClass.listOfFunctionParams.getItem(i);
						if (funcOfParent.isConstructor && funcOfParent.isConstructorThatInitializesStaticFields==false) {
							if (funcOfParent.listOfFuncArgs==null || funcOfParent.listOfFuncArgs.count==0) {
								if (funcOfParent.accessModifier!=null && funcOfParent.accessModifier.isStatic==false) {
									defaultConstructorOfParentClass = funcOfParent;
									break;
								}
							}
						}
					}//for (i=0; i<parentClass.listOfFunctionParams.count; i++) {
				}
				else {
					// static constructor
					for (i=0; i<parentClass.listOfFunctionParams.count; i++) {
						FindFunctionParams funcOfParent = (FindFunctionParams) parentClass.listOfFunctionParams.getItem(i);
						if (funcOfParent.isConstructor && funcOfParent.isConstructorThatInitializesStaticFields==false) {
							if (funcOfParent.listOfFuncArgs==null || funcOfParent.listOfFuncArgs.count==0) {
								if (funcOfParent.accessModifier!=null && funcOfParent.accessModifier.isStatic) {
									defaultConstructorOfParentClass = funcOfParent;
									break;
								}
							}
						}
					}//for (i=0; i<parentClass.listOfFunctionParams.count; i++) {
				}
				varUseSuper.funcDecl = defaultConstructorOfParentClass;
				this.traverseFuncCall(compiler.mBuffer, varUseSuper, -1, result);
			}
		}
	}
	
	
	void printFunction(FindFunctionParams func, HighArrayCharForByteCode result) {
		int i;
		
		if (func.accessModifier!=null && func.accessModifier.isSynchronized) {
			result.add("// synchronized starts"+"\n");
			
			FindClassParams parentClass = (FindClassParams)func.parent;
			result.add("aload "+this.getDescriptorOfThis(parentClass)+"\n");
			
			result.add("monitorenter"+"\n");
		}
		
		/*if (func.isConstructor) {
			this.makeFuncCallToConstructorOfSuperClass(func, result);
		}*/
		
		for (i=0; i<func.listOfStatements.count; i++) {
			FindStatementParams statement = (FindStatementParams) func.listOfStatements.getItem(i);
			this.printFindStatementParams(statement, result);
		}
		
		if (func.accessModifier!=null && func.accessModifier.isSynchronized) {
			result.add("monitorexit"+"\n");
		}
		
		if (func.returnType==null || func.returnType.equals("") || func.returnType.equals("void")) {
			result.add("return"+"\n");
		}
		
		if (func.accessModifier!=null && func.accessModifier.isSynchronized) {
			result.add("// synchronized ends"+"\n");
		}
	}
	
	/** statement의 바이트코드를 출력한다.
	 * @param statement : FindAssignStatementParams, FindIndependentFuncCallParams 등의 최소의 문장이다.
	 * try 등을 포함한 제어구조(if, else)가 아니다.*/
	void printFindStatementParams(FindStatementParams statement, HighArrayCharForByteCode result) {
		if (statement==null) return;
		
		if (statement.includesInc) {
			this.printStatementIncludingInc(statement, result);
			return;
		}
		
		if (statement instanceof FindFunctionParams) {
			FindFunctionParams func = (FindFunctionParams) statement;
			this.printFunction(func, result);
		}
		else if (statement instanceof FindControlBlockParams) {
			FindControlBlockParams controlBlock = (FindControlBlockParams) statement;
			if (controlBlock.catOfControls!=null) {
				if (controlBlock.catOfControls.category==CategoryOfControls.Control_if) {
					printIf_ElseIf_Else(controlBlock, result);
				}
				else if (controlBlock.catOfControls.category==CategoryOfControls.Control_elseif) {
					// if문을 처음 만날때 연결된 elseif나 else도 모두 출력한다.
					return;
				}
				else if (controlBlock.catOfControls.category==CategoryOfControls.Control_else) {
					// if문을 처음 만날때 연결된 elseif나 else도 모두 출력한다.
					return;
				}
				else if (controlBlock.catOfControls.category==CategoryOfControls.Control_for) {
					this.printFor(controlBlock, result);
				}
				else if (controlBlock.catOfControls.category==CategoryOfControls.Control_while) {
					this.printWhile(controlBlock, result);
				}
				else if (controlBlock.catOfControls.category==CategoryOfControls.Control_dowhile) {
					this.printDoWhile(controlBlock, result);
				}
				else if (controlBlock.catOfControls.category==CategoryOfControls.Control_switch) {
					this.printSwitch(controlBlock, result);
				}
				/*else if (controlBlock.catOfControls.category==CategoryOfControls.Control_case) {
					this.printCase(controlBlock, result);
				}*/
			}//if (controlBlock.catOfControls!=null) {
			else {
				// try, catch 등
				CodeString keyword = getNameOfControlBlock(controlBlock);
				if (keyword.equals("try")) {
					// this.printTry(controlBlock, result);
					// try문을 처음 만날때 연결된 catch나 finally도 모두 출력한다.
					this.printTry_catch_finally(controlBlock, result);
				}
				else if (keyword.equals("catch")) {
					//this.printCatch(controlBlock, result);
					// try문을 처음 만날때 연결된 catch나 finally도 모두 출력한다.
					return;
				}
				else if (keyword.equals("finally")) {
					//this.printFinally(controlBlock, result);
					// try문을 처음 만날때 연결된 catch나 finally도 모두 출력한다.
					return;
				}
				else if (keyword.equals("synchronized")) {
					this.printSynchronized(controlBlock, result);
				}
			}// try, catch 등
		}//if (statement instanceof FindControlBlockParams) {
		else if (statement instanceof FindSpecialStatementParams) {
			FindSpecialStatementParams specialStatement = (FindSpecialStatementParams) statement;
			CodeString keyword = null;
			if (specialStatement.kewordIndex()!=-1) {
				keyword = compiler.mBuffer.getItem(specialStatement.kewordIndex());
			}
			else { // 가짜 try-catch블록의 가짜 throw문
				keyword = new CodeString("throw", Compiler.textColor);
			}
			if (keyword.equals("throw")) {
				printThrow(specialStatement, result);
			}
			else if (keyword.equals("return")) {
				printReturn(specialStatement, result);
			}
			else {
				
				if (specialStatement.funcCall!=null) {
					this.traverseChild(specialStatement.funcCall, result);
				}
				printSpecialStatement(specialStatement, result);
			}
		}
		else if (statement instanceof FindIncrementStatementParams) {
			
			FindIncrementStatementParams inc = (FindIncrementStatementParams) statement;
			FindVarUseParams lValue = inc.lValue;
			String typeName = null;
			
			if (lValue.index()==394) {
				int a;
				a=0;
				a++;
			}
			
			typeName = lValue.varDecl.typeName;
					
			if (typeName.equals("int") && lValue.isLocal) {
				// 증감문장이 로컬변수이고 타입이 int일 때만 iinc를 사용한다.
				printIncrementStatement(inc, result);
				
			}
			else {
				FindAssignStatementParams assign = inc.toFindAssignStatementParams();
				
				Compiler compilerBackup = this.compiler;
				this.compiler = assign.rValue.compiler;
				
				this.printFindStatementParams(assign, result);
				
				this.compiler = compilerBackup;				
			}
			
		}//else if (statement instanceof FindIncrementStatementParams) {
		else if (statement instanceof FindSpecialStatementParams) {
			FindSpecialStatementParams specialStatement = (FindSpecialStatementParams) statement;
			
		}//else if (statement instanceof FindSpecialStatementParams) {
		else { // 대입문, 함수호출문 등
			if (statement instanceof FindVarParams) return;
			
			int startIndex = compiler.getIndexInmListOfAllVarUses(
					compiler.mBuffer, compiler.mlistOfAllVarUses, 0, statement.startIndex(), true);
			int endIndex = compiler.getIndexInmListOfAllVarUses(
					compiler.mBuffer, compiler.mlistOfAllVarUses, 0, statement.endIndex(), false);
			traverse(startIndex, endIndex, result);
		}
	}
	
	
	
	/** f(++iarr[++j]); 이와 같은 증감문들을 해결한다.*/
	void printVarUseIncludingInc(FindVarUseParams varUse, HighArrayCharForByteCode result, int typeOfInc) {
		if (varUse.index()==326) {
			int a;
			a=0;
			a++;
		}
		
		int startIndexInmBuffer=-1;
		int endIndexInmBuffer=-1;
		boolean checksChildInc = true;
		
		if (varUse.isArrayElement) {
			ArrayListIReset arr = varUse.listOfArrayElementParams;
			if (arr.count>0) {
				FindFuncCallParam funcCallStart = (FindFuncCallParam) arr.getItem(0);
				startIndexInmBuffer = funcCallStart.startIndex();
				FindFuncCallParam funcCallEnd = (FindFuncCallParam) arr.getItem(arr.count-1);
				endIndexInmBuffer = funcCallEnd.endIndex();
			}
		}
		if (varUse.isForVarOrForFunc==false) {
			ArrayListIReset arr = varUse.listOfFuncCallParams;
			if (arr.count>0) {
				FindFuncCallParam funcCallStart = (FindFuncCallParam) arr.getItem(0);
				startIndexInmBuffer = funcCallStart.startIndex();
				FindFuncCallParam funcCallEnd = (FindFuncCallParam) arr.getItem(arr.count-1);
				endIndexInmBuffer = funcCallEnd.endIndex();
			}
		}
		
		if (startIndexInmBuffer<0) {
			checksChildInc = false;
		}
		
		if (endIndexInmBuffer<0) {
			checksChildInc = false;
		}
		
		if (checksChildInc) {
			int startIndex = compiler.getIndexInmListOfAllVarUses(
					compiler.mBuffer, compiler.mlistOfAllVarUses, 0, startIndexInmBuffer, true);
			int endIndex = compiler.getIndexInmListOfAllVarUses(
					compiler.mBuffer, compiler.mlistOfAllVarUses, 0, endIndexInmBuffer, false);
			
			int i;
			for (i=startIndex; i<=endIndex; i++) {
				FindVarUseParams v = (FindVarUseParams) compiler.mlistOfAllVarUses.getItem(i);
				if (v.inc!=null) {
					// 자식 inc부터 출력
					printVarUseIncludingInc(v, result, typeOfInc);
				}
			}
		}
		
		// 현재 varUse의 증감문을 출력한다.
		if (typeOfInc==0 || typeOfInc==2) {
			if (varUse.inc.type==0 || varUse.inc.type==2) {
				this.printFindStatementParams(varUse.inc, result);
			}
		}
		else if (typeOfInc==1 || typeOfInc==3) {
			if (varUse.inc.type==1 || varUse.inc.type==3) {
				this.printFindStatementParams(varUse.inc, result);
			}
		}
	}
	
	/** f(++iarr[++j]); 이와 같은 증감문들을 해결한다.*/
	void printStatementIncludingInc(FindStatementParams statement, HighArrayCharForByteCode result) {
		if (statement.startIndex()>=387 && statement.startIndex()<389) {
			int a;
			a=0;
			a++;
		}
		
		int startIndex = compiler.getIndexInmListOfAllVarUses(
				compiler.mBuffer, compiler.mlistOfAllVarUses, 0, statement.startIndex(), true);
		int endIndex = compiler.getIndexInmListOfAllVarUses(
				compiler.mBuffer, compiler.mlistOfAllVarUses, 0, statement.endIndex(), false);
		
		int i;
		// ++iarr[++j] 를 해결한다.
		for (i=startIndex; i<=endIndex; i++) {
			FindVarUseParams varUse = (FindVarUseParams) compiler.mlistOfAllVarUses.getItem(i);
			if (varUse.inc!=null) {
				printVarUseIncludingInc(varUse, result, 0);
				i = this.jump(varUse, i);
			}
		}
		
		// f(++iarr[++j]); 문장을 출력한다.
		statement.includesInc = false;
		this.printFindStatementParams(statement, result);
		statement.includesInc = true;
		
		// iarr[j++]++ 를 해결한다.
		for (i=startIndex; i<=endIndex; i++) {
			FindVarUseParams varUse = (FindVarUseParams) compiler.mlistOfAllVarUses.getItem(i);
			if (varUse.inc!=null) {
				printVarUseIncludingInc(varUse, result, 1);
				i = this.jump(varUse, i);
			}
		}
	}
	
	
	/** 사용자가 조건문(if문을 제외한 else if에 대해서만 호출한다.)안의 varUse를 클릭했을때 
	 * statement의 바이트코드를 출력한다.
	 * 보여주기용이므로 printFindStatementParams()와 구별한다. 실제로 컴파일을 할 때는
	 * printFindStatementParams()을 써야 한다.
	 * @param statement : FindAssignStatementParams, FindIndependentFuncCallParams 등의 최소의 문장이다.
	 * try 등을 포함한 제어구조(if, else)가 아니다.*/
	void printFindStatementParams_findNode(FindStatementParams statement, HighArrayCharForByteCode result) {
		if (statement==null) return;
		
		if (statement.includesInc) {
			printFindStatementParams(statement, result);
			return;
		}
		
		//HighArrayCharForByteCode result = null;
		if (statement instanceof FindControlBlockParams) {
			FindControlBlockParams controlBlock = (FindControlBlockParams) statement;
			
			if (controlBlock.catOfControls==null) {
				// try, catch, synchronized 등
				CodeString keyword = compiler.mBuffer.getItem(controlBlock.nameIndex());
				if (keyword.equals("try")) {
					//this.printTry(controlBlock, result);
					this.printFindStatementParams(controlBlock, result);
				}
				else if (keyword.equals("catch")) {
					this.printCatch(controlBlock, result);
				}
				else if (keyword.equals("finally")) {
					this.printFinally(controlBlock, result);
				}
				else { // synchronized 등
					this.printFindStatementParams(controlBlock, result);
				}
			}//if (controlBlock.catOfControls==null) {
			else {
				if (controlBlock.catOfControls.category==CategoryOfControls.Control_if) {
					//result = new HighArrayCharForByteCode(100);
					//result.resizeInc = 300;				
					printIfControlBlock(controlBlock, result);				
					if (if_elseEnds(controlBlock)) {
						// if문의 끝나는 영역
						result.add("// exit"+" of "+controlBlock+"\n");
						result.add("// exit of if-elseif-else"+"\n");
					}
					else { // if-elseif-else에서 if나 else if구조가 끝났을 때
						result.add("goto_w "+"// exit of if-elseif-else"+"\n");
						// if문의 끝나는 영역
						result.add("// exit"+" of "+controlBlock+"\n");
					}
					/*copy(result, result);
					if (result!=null) {
						//result.count = 0;
						result.destroy();
					}*/
				}
				else if (controlBlock.catOfControls.category==CategoryOfControls.Control_elseif) {
					//result = new HighArrayCharForByteCode(100);
					//result.resizeInc = 300;
					printIfControlBlock(controlBlock, result);
					if (if_elseEnds(controlBlock)) {
						// else if문의 끝나는 영역
						result.add("// exit"+" of "+controlBlock+"\n");
						result.add("// exit of if-elseif-else"+"\n");					
					}
					else {// if-elseif-else에서 if나 else if구조가 끝났을 때
						result.add("goto_w "+"// exit of if-elseif-else"+"\n");
						// else if문의 끝나는 영역
						result.add("// exit"+" of "+controlBlock+"\n");
					}
					/*copy(result, result);
					if (result!=null) {
						//result.count = 0;
						result.destroy();
					}*/
				}
				else if (controlBlock.catOfControls.category==CategoryOfControls.Control_else) {
					//result = new HighArrayCharForByteCode(100);
					//result.resizeInc = 300;
					printElseControlBlock(controlBlock, result);
					if (if_elseEnds(controlBlock)) {
						result.add("// exit of if-elseif-else"+"\n");					
					}
					else {// if-elseif-else에서 if나 else if구조가 끝났을 때
						result.add("goto_w "+"// exit of if-elseif-else"+"\n");
					}
					/*copy(result, result);
					if (result!=null) {
						//result.count = 0;
						result.destroy();
					}*/
				}
				else { // if, else if, else 등을 제외한 제어구조, for, while 등
					printFindStatementParams(statement, result);
				}
			}// controlBlock.catOfControls!=null
		}//if (statement instanceof FindControlBlockParams) {
		else if (statement instanceof FindSpecialStatementParams) {
			printFindStatementParams(statement, result);
		}
		else {
			printFindStatementParams(statement, result);
		}
	}
	
	/**@param startIndex : mlistOfAllVarUses에서의 시작 인덱스
	 * @param endIndex : mlistOfAllVarUses에서의 끝 인덱스*/
	void traverse(int startIndex, int endIndex, HighArrayCharForByteCode result) {
		int i;
		for (i=startIndex; i<=endIndex; i++) {
			FindVarUseParams varUse = (FindVarUseParams) compiler.mlistOfAllVarUses.getItem(i);
			if (varUse.index()==388) {
				int a;
				a=0;
				a++;				
			}
			if (varUse.rValue!=null) { // lValue
				this.traverseExpressionTree(compiler.mBuffer, varUse, varUse.index(), result);
				i = jump(varUse, i);
			}
			else if (varUse.funcDecl!=null) {
				this.traverseExpressionTree(compiler.mBuffer, varUse, varUse.index(), result);
				i = jump(varUse, i);
			}
			else if (varUse.isArrayElement) {
				this.traverseExpressionTree(compiler.mBuffer, varUse, varUse.index(), result);
				i = jump(varUse, i);
			}
			else if (varUse.typeCast!=null /*&& varUse.typeCast.affectsExpression*/) {
				this.traverseExpressionTree(compiler.mBuffer, varUse, varUse.index(), result);
				i = jump(varUse, i);
			}
			else {
				this.traverseExpressionTree(compiler.mBuffer, varUse, varUse.index(), result);
			}
		}
	}
	
	
	
	void printControlBlockBody(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {
		int i;
		// 제어블록내에 있는 문장들을 수행한다.
		for (i=0; i<controlBlock.listOfStatements.count; i++) {
			FindStatementParams statement = (FindStatementParams) controlBlock.listOfStatements.getItem(i);
			this.printFindStatementParams(statement, result);
		}
	}
	
	void printElseControlBlock(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {
		printControlBlockBody(controlBlock, result);
	}
	
	
	/** 조건문의 끝나는 지점과 if 문장 영역(run영역) 사이에  exit_goto_w 영역을 두어서
		조건문에서 exit_goto_w 를 하게 되면 이 영역으로 오게 되어 goto_w 명령으로 exit 하게 된다.
		바이트코드 if문의 64K 한계를 극복하게 된다.<br>		
		만약에 조건을 만족하지 못하면 exit_goto_w 영역을 거쳐서 if 블록을 빠져나가에 되어
		다음 else if블록을 실행할 수도 있다.*/
	void printIfControlBlock(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {
		
		printCondition(controlBlock, result);
		
		// 조건문의 끝나는 지점과 if 문장 영역(run영역) 사이에  exit_goto_w 영역을 두어서
		// 조건문에서 exit_goto_w 를 하게 되면 이 영역으로 오게 되어 goto_w 명령으로 exit 하게 된다.
		// 바이트코드 if문의 64K 한계를 극복하게 된다.
		
		// 만약에 조건을 만족하지 못하면 exit_goto_w 영역을 거쳐서 if 블록을 빠져나가에 되어
		// 다음 else if블록을 실행할 수도 있다.
		result.add("// exit_goto_w of "+controlBlock+"\n");
		result.add("goto_w "+"// exit"+" of "+controlBlock+"\n");
		
		result.add("// run of "+controlBlock+"\n");
		
		printControlBlockBody(controlBlock, result);
		
		
		
		
		
	}
	
	
	
	
	
	/** true && false 에서 true*/
	static final byte And_Left = 1;
	/** true && false 에서 false*/
	static final byte And_Right = 2;
	/** true || false 에서 true*/
	static final byte Or_Left = 3;
	/** true || false 에서 false*/
	static final byte Or_Right = 4;
	static final byte Not_Left = 5;
	static final byte Not_Right = 6;
	
	
	static class AffectedBy {
		CodeStringEx operator;
		byte howToLink;
		
		AffectedBy(CodeStringEx operator, byte howToLink) {
			this.operator = operator;
			this.howToLink = howToLink;
		}
		
		public String toString() {
			switch (howToLink) {
			case 1: return "And_Left";
			case 2: return "And_Right";
			case 3: return "Or_Left";
			case 4: return "Or_Right";
			case 5: return "Not_Left";
			}
			return "";
		}
	}
	
	
	/** parent가 &&일 경우 왼쪽이면 And_Left, 오른쪽이면 And_Right,
	 * parent가 ||일 경우 왼쪽이면 Or_Left, 오른쪽이면 Or_Right,
	 * parent가 !일 경우 왼쪽이면 Not_Left, 오른쪽이면 Not_Right,
	 * @param token
	 * @return
	 */
	ArrayList getListOfParentsOfOperators(CodeStringEx token) {
		CodeStringEx parent = token;
		ArrayList r = new ArrayList(5); 
		while(true) {			
			if (parent==null) {
				return r;
			}
			if (parent.affectedBy==null) {
				return r;
			}
			if (parent.affectedBy.equals("&&")) {
				if (parent.affectedBy_left!=null) {
					r.add(new AffectedBy(parent.affectedBy,(byte)And_Left));
				}
				else if (parent.affectedBy_right!=null) {
					r.add(new AffectedBy(parent.affectedBy,(byte)And_Right));
				}
			}
			else if (parent.affectedBy.equals("||")) {
				if (parent.affectedBy_left!=null) {
					r.add(new AffectedBy(parent.affectedBy,(byte)Or_Left));
				}
				else if (parent.affectedBy_right!=null) {
					r.add(new AffectedBy(parent.affectedBy,(byte)Or_Right));
				}
			}
			else if (parent.affectedBy.equals("!")) {
				if (parent.affectedBy_left!=null) {
					r.add(new AffectedBy(parent.affectedBy,(byte)Not_Left));
				}
				else if (parent.affectedBy_right!=null) {
					r.add(new AffectedBy(parent.affectedBy,(byte)Not_Right));
				}
			}
			parent = parent.affectedBy;
		}
	}
	
	
	CodeStringEx findTokenAfterOperator(CodeStringEx[] postfix, CodeStringEx operator) {
		int i;
		for (i=0; i<postfix.length; i++) {
			CodeStringEx token = postfix[i];
			if (token.indicesInSrc.list[0]>operator.indicesInSrc.list[0]) {
				return token;
			}
		}
		return null;
	}
	
	int hasNOT(ArrayList parents, int startIndex) {
		int i;
		for (i=startIndex; i<parents.count; i++) {
			AffectedBy affectedBy = (AffectedBy) parents.list[i];
			if (affectedBy.howToLink==Not_Left) {
				return i;
			}
		}
		return -1;
	}
	
	
	static class OR {
		int index;
		AffectedBy or;
		OR(int index, AffectedBy or) {
			this.index = index;
			this.or = or;
		}
	}
	
	static class AND {
		int index;
		AffectedBy and;
		AND(int index, AffectedBy and) {
			this.index = index;
			this.and = and;
		}
	}
	
	
	/** 어떤 토큰의 오퍼레이터 parents 리스트에서 startIndex 이후의 '||'를 왼쪽으로 연결하는
	 * 첫번째 '||'를 찾아서 그것의 다음에 있는 토큰을 리턴한다.
	 * @param parents : if (true && false && true || true)에서 첫번째 true의 parents리스트는
	 * &&, &&, ||가 된다.<br>
	 *  if (true && false || true && true)에서 첫번째 true의 parents리스트는 
	 *  &&, ||가 된다.
	 * 
	 * @param startIndex : if (true &&(0) false ||(1) true)에서 괄호안의 인덱스이다. 
	 * @param postfix : 조건문의 포스트픽스 수식
	 * @return
	 */
	OR hasOR(ArrayList parents, int startIndex, CodeStringEx[] postfix) {
		int i;
		for (i=startIndex; i<parents.count; i++) {
			AffectedBy affectedBy = (AffectedBy) parents.list[i];
			
			if (affectedBy.operator.equals("||")) {
				OR r = new OR(i, affectedBy);
				return r;
			}
		}
		return null;
	}
	
	/** 어떤 토큰의 오퍼레이터 parents 리스트에서 startIndex 이후의 '&&'를 왼쪽으로 연결하는
	 * 첫번째 '&&'를 찾아서 그것의 다음에 있는 토큰을 리턴한다.
	 * @param parents : if (true && false && true || true)에서 첫번째 true의 parents리스트는
	 * &&, &&, ||가 된다.<br>
	 *  if (true && false || true && true)에서 첫번째 true의 parents리스트는 
	 *  &&, ||가 된다.
	 * 
	 * @param startIndex : if (true &&(0) false ||(1) true)에서 괄호안의 인덱스이다. 
	 * @param postfix : 조건문의 포스트픽스 수식
	 * @return
	 */
	AND hasAND(ArrayList parents, int startIndex, CodeStringEx[] postfix) {
		int i;
		for (i=startIndex; i<parents.count; i++) {
			AffectedBy affectedBy = (AffectedBy) parents.list[i];
			
			if (affectedBy.operator.equals("&&")) {
				AND r = new AND(i, affectedBy);
				return r;
			}
		}
		return null;
	}
	
	
			
	/**            or
	 *      and          or
	 *   or   and     and   H  
	 * A  B   C or    F G
	 *         D  E 
	 *         
	 *  (A or B) and C and (D or E)  or  F and G or H
	 *  <br>
	 *  first에서 Or_Left, Or_Right 2개의 경우의 수
		second에서 And_Left, And_Right, Or_Left, Or_Right 4개의 경우의 수
		따라서 논리연산자가 2개 이상 있을 때 8개의 경우의 수가 있다.
	 * @param token : 관계 연산자
	 * @param curType
	 * @param result
	 */
	
	
	
	/**            or
	 *      and          or
	 *   or   and     and   H  
	 * A  B   C or    F G
	 *         D  E 
	 *         
	 *  (A or B) and C and (D or E)  or  F and G or H
	 *  <br>
	 *  first에서 And_Left, And_Right 2개의 경우의 수
		second에서 And_Left, And_Right, Or_Left, Or_Right 4개의 경우의 수
		따라서 논리연산자가 2개 이상 있을 때 8개의 경우의 수가 있다.
	 * @param token : 관계 연산자
	 * @param curType
	 * @param result
	 */
	
	
	/** and를 왼쪽으로 연결하면서 or를 오른쪽으로 연결하는 경우에 호출
	 * @param operator : ||, Or_Right */
	void print_And_Left_Or_Right(AffectedBy operator, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		ArrayList parents = this.getListOfParentsOfOperators(operator.operator);
		
		if (parents==null || parents.count==0) {
			// if (!true ||(1) (!true &&(0) !true)) 에서 두번째 true
			result.add("ifle "+"// exit_goto_w"+"\n"); // put false
			//result.add("iconst_1"+"\n");
			return;	
		}
		
		AffectedBy parentOfOR = (AffectedBy) parents.getItem(0);
		
		if (parentOfOR.operator.equals("!")) {
			// if (!(2)(!true ||(1) (!true &&(0) !true))) 에서 두번째 true
			this.branchToNOT(parentOfOR.operator, 1, result);
		}
		else if (parentOfOR.operator.equals("||")) {
			if (parentOfOR.howToLink==Or_Left) {
				// if ((!true ||(1) !true &&(0) !true) ||(2) false) 두번째 true
				CodeStringEx nextToken = this.findTokenAfterOperator(postfix, parentOfOR.operator);
				String index = "("+nextToken.indicesInSrc.list[0]+")";
				result.add("ifle "+"// go to or"+", "+nextToken+index+"\n"); // put false
				//result.add("iconst_1"+"\n");				
				nextToken.printsORComment = true;
				nextToken.trueOrFalse = false;
			}
			else {
				// if (false ||(2) (!true ||(1) !true &&(0) !true)) 두번째 true
				print_And_Left_Or_Right(parentOfOR, result, postfix);
			}				
		}
		else if (parentOfOR.operator.equals("&&")) {
			if (parentOfOR.howToLink==And_Left) {
				// if ((!true ||(1) !true &&(0) !true) &&(2) false) 두번째 true
				print_And_Left(parentOfOR.operator, result, postfix);
			}
			else {
				// if (true &&(2) (!true ||(1) !true &&(0) !true)) 세번째 true
				print_And_Left_And_Right(parentOfOR, result, postfix);
			}
		}
	}
	
	/** and를 왼쪽으로 연결하면서 and를 오른쪽으로 연결하는 경우에 호출
	 * @param operator : &&, And_Right */
	void print_And_Left_And_Right(AffectedBy operator, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		ArrayList parents = this.getListOfParentsOfOperators(operator.operator);
		
		if (parents==null || parents.count==0) {
			// if ((!true &&(1) (!true &&(0) !true))) 두번째 true
			result.add("ifle "+"// exit_goto_w"+"\n"); // put false
			return;	
		}
		
		AffectedBy parentOfAND = (AffectedBy) parents.getItem(0);
		
		if (parentOfAND.operator.equals("!")) {
			// if (!(2)(!true &&(1) (!true &&(0) !true))) 두번째 true
			this.branchToNOT(parentOfAND.operator, 1, result);
		}
		else if (parentOfAND.operator.equals("||")) {
			if (parentOfAND.howToLink==Or_Left) {
				// if ((!true &&(1) !true &&(0) !true) ||(2) false) 두번째 true
				CodeStringEx nextToken = this.findTokenAfterOperator(postfix, parentOfAND.operator);
				String index = "("+nextToken.indicesInSrc.list[0]+")";
				result.add("ifle "+"// go to or"+", "+nextToken+index+"\n"); // put false
				nextToken.printsORComment = true;
				nextToken.trueOrFalse = false;
			}
			else {
				// if (false ||(2) (!true &&(1) !true &&(0) !true)) 두번째 true
				print_And_Left_Or_Right(parentOfAND, result, postfix);
			}				
		}
		else if (parentOfAND.operator.equals("&&")) {
			if (parentOfAND.howToLink==And_Left) {
				// if ((!true &&(1) !true &&(0) !true) &&(2) false) 두번째 true
				print_And_Left(parentOfAND.operator, result, postfix);
			}
			else {
				// if (true &&(2) (!true &&(1) !true &&(0) !true)) 세번째 true
				print_And_Left_And_Right(parentOfAND, result, postfix);
			}
		}
	}
	
	
	/**ifle "+"// go to not 과 같이 not 연산자로 분기한다.
	 * @param mode : gt(0), le(1)*/
	void branchToNOT(CodeStringEx not, int mode, HighArrayCharForByteCode result) {
		if (mode==1) {
			String index = "("+not.indicesInSrc.list[0]+")";
			not.trueOrFalse = false;
			result.add("ifle "+"// go to not and put false"+", "+not+index+"\n"); // put false
			not.printsNOTComment = true;			
		}
		else if (mode==0) {
			// not으로 분기한다.
			String index = "("+not.indicesInSrc.list[0]+")";
			// not+1은 not 주석 뒤에 iconst_1(true)로 분기한다는 뜻이다. 
			// not은 not 주석 뒤에 iconst_0(false)로 분기한다는 뜻이다.
			result.add("ifgt "+"// go to not and put true"+", "+not+index+"\n"); // put true
			not.printsNOTComment = true;
			not.trueOrFalse = true;
		}
	}
	
	/** A && B에서 A를 처리한 후 호출
	 * @param operator : &&를 왼쪽으로 연결할때 &&*/
	void print_And_Left(CodeStringEx operator, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		if (operator.indicesInSrc.list[0]==3382) {
			int a;
			a=0;
			a++;
		}
		ArrayList parents = this.getListOfParentsOfOperators(operator);
		OR or = this.hasOR(parents, 0, postfix);
		
		int indexOfNot = this.hasNOT(parents, 0);
		CodeStringEx not = null;
		if (indexOfNot>=0) {
			not = ((AffectedBy)parents.getItem(indexOfNot)).operator;
		}
		
		if (or==null) {
			if (not==null) {
				result.add("ifle "+"// exit_goto_w"+"\n");
				return;
			}
			else {
				// not으로 분기한다.
				this.branchToNOT(not, 1, result);
			}
		}
		else if (or.or.howToLink==Or_Left){
			if (not==null || or.index<indexOfNot) {
				// if ((false &&(0) true) ||(1) false)에서 첫번째 false
				CodeStringEx nextToken = this.findTokenAfterOperator(postfix, or.or.operator);
				String index = "("+nextToken.indicesInSrc.list[0]+")";
				result.add("ifle "+"// go to or"+", "+nextToken+index+"\n"); // put false
				nextToken.printsORComment = true;
				nextToken.trueOrFalse = false;
				operator.tokenAfterOR = nextToken;
				return;
			}
			else {
				// if (!(1)(false &&(0) true) ||(2) false)에서 첫번째 false
				// not으로 분기한다.
				this.branchToNOT(not, 1, result);
			}
		}
		else if (or.or.howToLink==Or_Right){
			if (not==null || or.index<indexOfNot) {
				// if (false ||(1) (false &&(0) true))에서 두번째 false
				this.print_And_Left_Or_Right(or.or, result, postfix);
			}
			else {
				// if (false ||(2) !(1)(false &&(0) true))에서 두번째 false
				// not으로 분기한다.
				this.branchToNOT(not, 1, result);
			}
		}
	}
	
	void printAND(CodeStringEx operator, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		if (operator.indicesInSrc.list[0]==2447) {
			int a;
			a=0;
			a++;
		}
		// 왼쪽 오퍼랜드부터 호출
		callBoolean(operator.affectsLeft, result, postfix);
		
		print_And_Left(operator, result, postfix);
		
		callBoolean(operator.affectsRight, result, postfix);
		//result.add("iand"+"\n");
	}
	
	
	/** or를 왼쪽으로 연결하면서 or를 오른쪽으로 연결하는 경우에 호출
	 * @param operator : ||, Or_Right */
	void print_Or_Left_Or_Right(AffectedBy operator, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		ArrayList parents = this.getListOfParentsOfOperators(operator.operator);
		
		if (parents==null || parents.count==0) {
			// if ((!true ||(1) (!true ||(0) !true))) 두번째 true
			result.add("ifgt "+"// run"+"\n");
			return;	
		}
		
		AffectedBy parentOfOR = (AffectedBy) parents.getItem(0);
		
		if (parentOfOR.operator.equals("!")) {
			// if ( !(2)( (!true ||(1) (!true ||(0) !true) ) ) ) 두번째 true
			this.branchToNOT(parentOfOR.operator, 0, result);
		}
		else if (parentOfOR.operator.equals("||")) {
			if (parentOfOR.howToLink==Or_Left) {
				// if ((!true ||(1) (!true ||(0) !true)) ||(2) false) 두번째 true
				// print_Or_Left()에서 hasAND()가 호출되므로 and로 점프하거나
				// run으로 점프하게된다.
				print_Or_Left(parentOfOR.operator, result, postfix);
			}
			else {
				// if (false ||(2) (!true ||(1) (!true ||(0) !true))) 두번째 true
				// print_Or_Left()에서 hasAND()가 호출되므로 and로 점프하거나
				// run으로 점프하게된다.
				print_Or_Left_Or_Right(parentOfOR, result, postfix);
			}				
		}
		else if (parentOfOR.operator.equals("&&")) {
			if (parentOfOR.howToLink==And_Left) {
				// if ((!true ||(1) (!true ||(0) !true)) &&(2) false) 두번째 true
				//print_And_Left(parentOfOR.operator, result, postfix);
				CodeStringEx nextToken = this.findTokenAfterOperator(postfix, parentOfOR.operator);
				String index = "("+nextToken.indicesInSrc.list[0]+")";
				result.add("ifgt "+"// go to and"+", "+nextToken+index+"\n"); // put true
				nextToken.printsANDComment = true;
				nextToken.trueOrFalse = true;
			}
			else {
				// if (true &&(2) (!true ||(1) (!true ||(0) !true))) 세번째 true
				print_Or_Left_And_Right(parentOfOR, result, postfix);
			}
		}
	}
	
	/** or를 왼쪽으로 연결하면서 and를 오른쪽으로 연결하는 경우에 호출
	 * @param operator : &&, And_Right */
	void print_Or_Left_And_Right(AffectedBy operator, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		ArrayList parents = this.getListOfParentsOfOperators(operator.operator);
		
		if (parents==null || parents.count==0) {
			// if ((!true &&(1) (!true ||(0) !true))) 두번째 true
			result.add("ifgt "+"// run"+"\n");
			return;
		}
		
		AffectedBy parentOfAND = (AffectedBy) parents.getItem(0);
		
		if (parentOfAND.operator.equals("!")) {
			// if (!(2)( (!true &&(1) (!true ||(0) !true) ) ) ) 두번째 true
			
			this.branchToNOT(parentOfAND.operator, 0, result);
		}
		else if (parentOfAND.operator.equals("||")) {
			if (parentOfAND.howToLink==Or_Left) {
				// if ((!true &&(1) (!true ||(0) !true)) ||(2) false) 두번째 true
				this.print_Or_Left(parentOfAND.operator, result, postfix);
			}
			else {
				// if (false ||(2) (!true &&(1) (!true ||(0) !true)) 두번째 true
				print_Or_Left_Or_Right(parentOfAND, result, postfix);
			}				
		}
		else if (parentOfAND.operator.equals("&&")) {
			if (parentOfAND.howToLink==And_Left) {
				// if ((!true &&(1) (!true ||(0) !true)) &&(2) false) 두번째 true
				//print_And_Left(parentOfAND.operator, result, postfix);
				CodeStringEx nextToken = this.findTokenAfterOperator(postfix, parentOfAND.operator);
				String index = "("+nextToken.indicesInSrc.list[0]+")";
				result.add("ifgt "+"// go to and"+", "+nextToken+index+"\n"); // put true
				nextToken.printsANDComment = true;
				nextToken.trueOrFalse = true;
				
			}
			else {
				// if (true &&(2) (!true &&(1) (!true ||(0) !true))) 세번째 true
				print_Or_Left_And_Right(parentOfAND, result, postfix);
			}
			
		}
	}
	
	/** A || B에서 A를 처리한 후 호출
	 * @param operator : ||를 왼쪽으로 연결할때 ||*/
	void print_Or_Left(CodeStringEx operator, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		
		ArrayList parents = this.getListOfParentsOfOperators(operator);
		AND and = this.hasAND(parents, 0, postfix);
		int indexOfNot = this.hasNOT(parents, 0);
		CodeStringEx not = null;
		if (indexOfNot>=0) {
			not = ((AffectedBy)parents.getItem(indexOfNot)).operator;
		}
		if (and==null) {
			if (not==null) {
				result.add("ifgt "+"// run"+"\n");
			}
			else {
				this.branchToNOT(not, 0, result);
			}
			
		}
		else if (and.and.howToLink==And_Left){
			if (not==null || and.index<indexOfNot) {
				CodeStringEx nextToken = this.findTokenAfterOperator(postfix, and.and.operator);
				String index = "("+nextToken.indicesInSrc.list[0]+")";
				result.add("ifgt "+"// go to and"+", "+nextToken+index+"\n"); // put true
				nextToken.printsANDComment = true;
				nextToken.trueOrFalse = true;
				operator.tokenAfterAND = nextToken;
			}
			else {
				// not으로 분기한다.
				// if ( !(1)(true ||(0) false) &&(2) false )
				this.branchToNOT(not, 0, result);
			}
		}
		else if (and.and.howToLink==And_Right){
			if (not==null || and.index<indexOfNot) {
				// if ((!true &&(1) (!true ||(0) !true)) ||(2) false) 두번째 true			
				// if (false ||(2) (!true &&(1) (!true ||(0) !true)) 두번째 true
				print_Or_Left_And_Right(and.and, result, postfix);
			}
			else {
				// not으로 분기한다.
				// if ((!true &&(2) !(1)(!true ||(0) !true)) ||(3) false) 두번째 true
				this.branchToNOT(not, 0, result);
			}
		}
	}
	
	void printOR(CodeStringEx operator, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		if (operator.indicesInSrc.list[0]==2044) {
			int a;
			a=0;
			a++;
		}
		// 왼쪽 오퍼랜드부터 호출
		callBoolean(operator.affectsLeft, result, postfix);
		
		print_Or_Left(operator, result, postfix);
		
		callBoolean(operator.affectsRight, result, postfix);
		//result.add("ior"+"\n");
	}
	
	/** not이 바로 다음에 올 경우에만 호출한다.*/
	void justBeforeNot(CodeStringEx not, HighArrayCharForByteCode result) {
		String index = "("+not.indicesInSrc.list[0]+")";
		result.add("ifle "+"// iconst_0"+"\n"); 
		result.add("iconst_1"+"\n");
		result.add("goto "+"ixor"+", "+not+index+"\n"); // ixor으로 점프한다.
		result.add("iconst_0"+"\n");
		result.add("goto "+"ixor"+", "+not+index+"\n"); // ixor으로 점프한다.
	}
	
	void printNOT(CodeStringEx operator, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		if (operator.indicesInSrc.list[0]==2047) {
			int a;
			a=0;
			a++;
		}
		// 왼쪽 오퍼랜드부터 호출
		callBoolean(operator.affectsLeft, result, postfix);
		
		if (operator.printsNOTComment) {
			justBeforeNot(operator, result);
		}
		
		if (operator.printsNOTComment) {
			// 해당 // not, 분기될 경우 스택에 넣을 false, true순으로 값이 정의되어 있다.
			String index = "("+operator.indicesInSrc.list[0]+")";
			result.add("// not"+index+"\n");
			result.add("iconst_0"+"\n");	// false
			result.add("goto "+"ixor"+"\n"); // ixor로 점프
			result.add("iconst_1"+"\n");	// true
			
		}
		
		
		result.add("ixor 1"+"\n");
	}
	
	/** 논리연산자, 관계연산자를 제외한 모든 연산자*/
	void printOperatorInCondition(CodeStringEx token, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		if (token.indicesInSrc.list[0]==2282) {
			int a;
			a=0;
			a++;
		}
		if (token.affectsLeft!=null) {
			callBoolean(token.affectsLeft, result, postfix);
		}
		if (token.equals("instanceof")==false) { 
			// token이 instanceof일 경우 affectsRight를 대상으로 callBoolean()을 호출하지 않는다.
			if (token.affectsRight!=null) {
				callBoolean(token.affectsRight, result, postfix);
			}
		}
		
		this.printOperator(token, result);
	}
	
	void printRelationOperator(CodeStringEx token, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		callBoolean(token.affectsLeft, result, postfix);
		callBoolean(token.affectsRight, result, postfix);
		// 스택에 관계식의 결과값을 넣어준다.
		String typeFullName = token.affectsLeft.typeFullNameAfterOperation==null ? 
				token.affectsLeft.typeFullName : token.affectsLeft.typeFullNameAfterOperation;
		
		if (typeFullName.equals("boolean")) {
			result.add("isub"+"\n");
		}
		else if (typeFullName.equals("byte") || typeFullName.equals("char") ||
				typeFullName.equals("short") || typeFullName.equals("int")) {
			result.add("isub"+"\n");
		}
		else if (typeFullName.equals("long")) {
			result.add("lsub"+"\n");
		}
		else if (typeFullName.equals("float")) {
			result.add("fsub"+"\n");
		}
		else if (typeFullName.equals("double")) {
			result.add("dsub"+"\n");
		}
		
		if (token.equals(">")) {
			result.add("ifgt "+"// iconst_1"+"\n");
			result.add("iconst_0"+"\n");
			result.add("goto "+"1"+"\n");
			result.add("iconst_1"+"\n");
		}
		else if (token.equals("<")) {
			result.add("iflt "+"// iconst_1"+"\n");
			result.add("iconst_0"+"\n");
			result.add("goto "+"1"+"\n");
			result.add("iconst_1"+"\n");
		}
		else if (token.equals(">=")) {
			result.add("ifge "+"// iconst_1"+"\n");
			result.add("iconst_0"+"\n");
			result.add("goto "+"1"+"\n");
			result.add("iconst_1"+"\n");
		}
		else if (token.equals("<=")) {
			result.add("ifle "+"// iconst_1"+"\n");
			result.add("iconst_0"+"\n");
			result.add("goto "+"1"+"\n");
			result.add("iconst_1"+"\n");			
		}
		else if (token.equals("==")) {
			result.add("ifeq "+"// iconst_1"+"\n");
			result.add("iconst_0"+"\n");
			result.add("goto "+"1"+"\n");
			result.add("iconst_1"+"\n");
		}
		else if (token.equals("!=")) {
			result.add("ifne "+"// iconst_1"+"\n");
			result.add("iconst_0"+"\n");
			result.add("goto "+"1"+"\n");
			result.add("iconst_1"+"\n");
		}
	}
	
	void callBoolean(CodeStringEx token, HighArrayCharForByteCode result, CodeStringEx[] postfix) {
		if (token==null) return;
		if (token==null || token.indicesInSrc==null) {
			int a;
			a=0;
			a++;
		}
		if (token!=null && token.indicesInSrc!=null && token.indicesInSrc.list[0]==241) {
			int a;
			a=0;
			a++;
		}
		if (token.equals("||")) {
			printOR(token, result, postfix);
		}
		else if (token.equals("&&")) {
			printAND(token, result, postfix);
		}
		else if (token.equals("!")) {
			printNOT(token, result, postfix);
			
			
		}
		else {
			if (CompilerHelper.IsRelationOperator(token.str)) {
				printRelationOperator(token, result, postfix);
			}
			else if (CompilerHelper.IsOperator(token.str)) {
				printOperatorInCondition(token, result, postfix);
			}
			else { // 상수나 변수일 경우
				if (token.printsORComment) {
					String index = "("+token.indicesInSrc.list[0]+")";
					result.add("// or"+index+"\n");
				}
				if (token.printsANDComment) {
					String index = "("+token.indicesInSrc.list[0]+")";
					result.add("// and"+index+"\n");
				}
				
				int startIndex = compiler.getIndexInmListOfAllVarUses(
						compiler.mBuffer, compiler.mlistOfAllVarUses, 0, 
						token.indicesInSrc.getItem(0)-1, true);
				int endIndex = compiler.getIndexInmListOfAllVarUses(
						compiler.mBuffer, compiler.mlistOfAllVarUses, startIndex, 
						token.indicesInSrc.getItem(token.indicesInSrc.count-1)+1, false);
				traverse(startIndex, endIndex, result);
				
				if (token.equals("true")) {
				}
				else if (token.equals("false")) {
				}
				else if (CompilerHelper.IsConstant(token)) {
					
				}				
				else { // 변수
					
				}
				// if (i+2<f) 에서 '+'의 typeFullNameAfterOperation는 float가 된다. 
				// 따라서 iadd를 출력한 후에 i2f를 출력한다.
				if (token.typeFullNameAfterOperation!=null) {
					this.printTypeCast_sub(token.typeFullName, 
							token.typeFullNameAfterOperation, result);
				}
			}//상수나 변수일 경우
		}
	}
	
	
	
	/** 제어블록에 있는 조건식의 바이트코드를 출력한다.*/
	void printCondition(FindControlBlockParams controlBlock, HighArrayCharForByteCode result) {
		
		FindFuncCallParam funcCall = controlBlock.funcCall;
		CodeStringEx[] postfix = funcCall.expression.postfix;
		
		CodeStringEx lastToken = postfix[postfix.length-1];
		callBoolean(lastToken, result, postfix);
		
		result.add("ifgt "+"// run"+"\n");
	}
	
	void printIncrementStatement(FindIncrementStatementParams statement, HighArrayCharForByteCode result) {
		FindVarUseParams lValue = statement.lValue;
		String typeName = lValue.varDecl.typeName;
				
		if (typeName.equals("int") && lValue.isLocal) {
			// 지역변수이고 타입이 int일 때만
			if (statement.type==0 || statement.type==1) {
				result.add("iinc "+lValue.varDecl+" 1"+"\n");
			}
			else {
				result.add("iinc "+lValue.varDecl+" -1"+"\n");
			}
		}
		else {		
			if (statement.type==0 || statement.type==1) {
				if (typeName.equals("int")) {
					result.add("iconst_1"+"\n");
					result.add("iadd"+"\n");
					result.add("istore "+lValue.varDecl+"\n");
				}
				else if (typeName.equals("long")) {
					result.add("lconst 1"+"\n");
					result.add("ladd"+"\n");
					result.add("lstore "+lValue.varDecl+"\n");
				}
				else if (typeName.equals("float")) {
					result.add("fconst 1"+"\n");
					result.add("fadd"+"\n");
					result.add("fstore "+lValue.varDecl+"\n");
				}
				else if (typeName.equals("double")) {
					result.add("dconst 1"+"\n");
					result.add("dadd"+"\n");
					result.add("dstore "+lValue.varDecl+"\n");
				}
			}
		}
	}
	
	/** return문을 포함하는 synchronized가 중첩되거나 그렇지 않을 경우 synchronized의 개수를 세어 리턴한다.
	 * return문이 synchronized에 포함되지 않으면 0을 리턴한다.*/  
	int getCountOfSynchronized(FindSpecialStatementParams statement) {
		if (statement.kewordIndex()==1656) {
			int a;
			a=0;
			a++;
		}
		int count=0;
		FindStatementParams parent = statement.parent;
		while (true) {			
			if (parent==null) return count;
			if (parent instanceof FindFunctionParams) {
				FindFunctionParams func = (FindFunctionParams) parent;
				if (func.accessModifier!=null && func.accessModifier.isSynchronized) 
					count++;
				else return count;
			}
			if (parent instanceof FindClassParams) {
				return count;
			}
			if (parent instanceof FindControlBlockParams) {
				FindControlBlockParams controlBlock = (FindControlBlockParams) parent;
				// controlBlock.nameIndex()==-1일 경우 가짜 try-catch블록
				if (controlBlock.nameIndex()!=-1 &&
						compiler.mBuffer.getItem(controlBlock.nameIndex()).equals("synchronized"))
					count++;
			}
			parent = parent.parent;
		}
	}
	
	
	/**throw가 던지는 예외가 catch되기 전에( throw가 던지는 예외타입이 catch블록 리스트에 있을 때)
	   synchronized의 개수를 세어 오브젝트 잠금을 몇 번 해제를 해야 하는지를 얻는다.
		found는 catch블록을 만나서 던진 예외가 catch되면 true가 된다.<br>
		
		 catch-synchronized-synchronized-throw e 스택 상에서 
		 catch를 만나기 전에 synchronized가 두번 있으므로 결과는 2가 된다.<br>
		
		 synchronized-synchronized-catch-throw e 스택 상에서 
		 synchronized를 만나기 전에 catch가 되므로 결과는 0가 된다. 
		 이 때는 모니터를 해제할 필요가 없다.*/
	int getCountOfSynchronizedBeforeCatch(FindSpecialStatementParams statement, String typeNameOfExceptionOfThrow) {
		if (statement.kewordIndex()==1656) {
			int a;
			a=0;
			a++;
		}
		
		// throw가 던지는 예외가 catch되기 전에 
		// synchronized의 개수를 세어 오브젝트 잠금을 몇 번 해제를 해야 하는지를 얻는다.
		// found는 catch블록을 만나서 던진 예외가 catch되면 true가 된다.
		
		// catch-synchronized-synchronized-throw e 스택 상에서 
		// catch를 만나기 전에 synchronized가 두번 있으므로 결과는 2가 된다.
		
		// synchronized-synchronized-catch-throw e 스택 상에서 
		// synchronized를 만나기 전에 catch가 되므로 결과는 0가 된다. 
		// 이 때는 모니터를 해제할 필요가 없다.
		boolean found = false;
		Block block = statement.parent;
		//FindControlBlockParams synchronizedBlock = null;
		int count = 0;
		while (true) {
			FindControlBlockParams tryBlock = null;
			FindControlBlockParams[] listOfCatchBlocks = null;
					
			
			if (block==null) break;
			else if (block instanceof FindControlBlockParams) {
				FindControlBlockParams control = (FindControlBlockParams) block;
				if (control.catOfControls==null) {
					CodeString name = getNameOfControlBlock(control);
					if (name.equals("try")) {
						tryBlock = control;
					}
					else if (name.equals("synchronized")) {
						// throw가 던지는 예외가 catch되기 전에 
						// synchronized의 개수를 세어 오브젝트 잠금을 몇 번 해제를 해야 하는지를 얻는다.
						count++;
					}
					else {
						block = block.parent;
						continue;
					}
				}
				else {
					block = block.parent;
					continue;
				}
			}//else if (block instanceof FindControlBlockParams) {
			else if (block instanceof FindFunctionParams) {
				FindFunctionParams func = (FindFunctionParams) block;
				if (func.accessModifier!=null && func.accessModifier.isSynchronized) {
					// throw가 던지는 예외가 catch되기 전에 
					// synchronized의 개수를 세어 오브젝트 잠금을 몇 번 해제를 해야 하는지를 얻는다.
					count++;
				}
				break;
			}
			else {
				block = block.parent;
				continue;
			}
			
			if (tryBlock!=null) {
				listOfCatchBlocks = this.getCatchBlocksOfTry(tryBlock.parent, tryBlock.indexInListOfControlBlocksOfParent);
			}
			
			
			int i;
			// found는 throw가 던지는 예외타입이 
			// catch블록 리스트에 있을 때 true가 된다.
			if (listOfCatchBlocks!=null) {
				for (i=0; i<listOfCatchBlocks.length; i++) {
					FindControlBlockParams catchBlock = listOfCatchBlocks[i];
					FindVarParams var = (FindVarParams) catchBlock.listOfStatementsInParenthesis.getItem(0);
					if (var.typeName.equals(typeNameOfExceptionOfThrow)) {
						found = true;
						break;
					}
				}
			}
			if (found) break;
			
			if (block!=null) {
				block = block.parent;
			}
		}//while (true) {
		
		return count;
	}
	
	
	void printSpecialStatement(FindSpecialStatementParams statement, HighArrayCharForByteCode result) {
		if (statement.kewordIndex()==2408) {
			int a;
			a=0;
			a++;
		}
		String keyword = compiler.mBuffer.getItem(statement.kewordIndex()).str;
		if (keyword.equals("return")) {
			// printReturn()에서 처리
		}
		else if (keyword.equals("continue")) {
			FindControlBlockParams controlBlock = 
					(FindControlBlockParams) this.getParentIterationBlock(statement.parent);
			if (controlBlock!=null) {
				if (controlBlock.catOfControls.category==CategoryOfControls.Control_for) {
					result.add("goto_w "+"// go to increments of "+controlBlock+"\n");
				}
				else { // while, do while
					result.add("goto_w "+"// go to condition of "+controlBlock+"\n");
				}
			}
		}
		else if (keyword.equals("break")) {
			FindControlBlockParams controlBlock = 
					(FindControlBlockParams) this.getParentIterationBlock(statement.parent);
			if (controlBlock!=null) {
				if (controlBlock.catOfControls.category==CategoryOfControls.Control_for) {
					result.add("goto_w "+"// exit of for loop "+controlBlock+"\n");
				}
				else if (controlBlock.catOfControls.category==CategoryOfControls.Control_while) {
					result.add("goto_w "+"// exit of while loop "+controlBlock+"\n");
				}
				else if (controlBlock.catOfControls.category==CategoryOfControls.Control_dowhile) {
					result.add("goto_w "+"// exit of dowhile loop "+controlBlock+"\n");
				}
				else if (controlBlock.catOfControls.category==CategoryOfControls.Control_switch) {
					result.add("goto_w "+"// exit of switch "+controlBlock+"\n");
				}
			}
		}//else if (keyword.equals("break")) {
		else if (keyword.equals("throw")) {
			// printThrow()에서 처리
		}
		
	}
	
	void printReturn(FindSpecialStatementParams statement, HighArrayCharForByteCode result) {
		// return; 에서와 같이 값을 리턴하지 않는 경우 statement.funcCall은 null이므로
		// 이 경우에는 return만 출력한다.
		// 리턴할 값을 스택에 넣는다.
		// return문에 수식이 있을 경우 수식을 출력한다.
		if (statement.funcCall!=null && statement.funcCall.typeFullName!=null) {
			this.traverseChild(statement.funcCall, result);
		}
		
		// synchronized의 개수를 세어 리턴을 하기 전에 오브젝트 잠금을 해제한다.
		int countOfSynchronized = this.getCountOfSynchronized(statement);
		
		if (countOfSynchronized>0) {
			// return문에 수식이 있을 경우 모니터가 해제되지 않은 상태에서 수식을 처리하기 위해
			// 다음과 같이 임시변수와 임시변수참조를 만든다.
			FindVarParams tempLocalVar = null;
			FindVarUseParams tempLocal = null;
			
			if (statement.funcCall!=null && statement.funcCall.typeFullName!=null) {
				tempLocalVar = new FindVarParams(compiler, -1, -1);
				tempLocalVar.typeName = statement.funcCall.typeFullName.str;
				tempLocal = new FindVarUseParams(null);
				tempLocal.varDecl = tempLocalVar;
				this.printLocalVar(tempLocal, result, false);
			}
			
			int i;
			for (i=0; i<countOfSynchronized; i++) {
				result.add("monitorexit"+"\n");
			}
			
			// 저장된 임시지역변수를 다시 로드한다.
			if (statement.funcCall!=null && statement.funcCall.typeFullName!=null) {
				this.printLocalVar(tempLocal, result, true);
			}
		}//if (countOfSynchronized>0) {
		
		// statement.funcCall.typeFullName이 null이거나 ""이거나 "void"이면 return으로 한다.			
		if (statement.funcCall!=null && statement.funcCall.typeFullName!=null) {
			String typeName = statement.funcCall.typeFullName.str;
			if (typeName.equals("boolean") ||
				typeName.equals("byte") || typeName.equals("char") || typeName.equals("short") ||
				typeName.equals("int")) {
				result.add("ireturn"+"\n");
			}
			else if (typeName.equals("float")) {
				result.add("freturn"+"\n");
			}
			else if (typeName.equals("double")) {
				result.add("dreturn"+"\n");
			}
			else if (typeName.equals("void") || typeName.equals("")) { // void형
				result.add("return"+"\n");
			}
			else {
				result.add("areturn"+"\n");
			}
		}
		else { // void 형
			result.add("return"+"\n");
		}
	}
	
	void printThrow(FindSpecialStatementParams statement, HighArrayCharForByteCode result) {
		if (statement.kewordIndex()==1739) {
			int a;
			a=0;
			a++;
		}
		
		String typeNameOfException = null;
		if (statement.funcCall==null && statement.isFake) { // 가짜 try-catch블록의 가짜 throw문
			typeNameOfException = "java.lang.Exception";
		}
		else {
			if (statement.funcCall.typeFullName==null) return;
			typeNameOfException = statement.funcCall.typeFullName.str;
		}
		
		// synchronized의 개수를 세어 리턴을 하기 전에 오브젝트 잠금을 해제를 해야 하는지 참고한다.
		// found는 synchronized블록을 만나기 전에 
		// throw가 던지는 예외타입이 catch블록 리스트에 있을 때 true가 된다.
		/*boolean found = false;
		Block block = statement.parent;
		FindControlBlockParams synchronizedBlock = null;
		while (true) {
			FindControlBlockParams tryBlock = null;
			FindControlBlockParams[] listOfCatchBlocks = null;
			
			//Block block = this.getParentTryBlock(statement.parent);
			//if (block!=null) tryBlock = (FindControlBlockParams) block;			
			
			if (block==null) break;
			else if (block instanceof FindControlBlockParams) {
				FindControlBlockParams control = (FindControlBlockParams) block;
				if (control.catOfControls==null) {
					CodeString name = getNameOfControlBlock(control);
					if (name.equals("try")) {
						tryBlock = control;
					}
					else if (name.equals("synchronized")) {
						// found는 synchronized블록을 만나기 전에 
						// throw가 던지는 예외타입이 catch블록 리스트에 있을 때 true가 된다.
						// found가 false일때 모니터를 해제해야 한다.
						// found가 true일때는 모니터를 해제할 필요가 없다.
						synchronizedBlock = control;
						break;
					}
					else {
						block = block.parent;
						continue;
					}
				}
				else {
					block = block.parent;
					continue;
				}
			}//else if (block instanceof FindControlBlockParams) {
			else {
				block = block.parent;
				continue;
			}
			
			if (tryBlock!=null) {
				listOfCatchBlocks = this.getCatchBlocksOfTry(tryBlock.parent, tryBlock.indexInListOfControlBlocksOfParent);
			}
			
			
			int i;
			// found는 synchronized블록을 만나기 전에 throw가 던지는 예외타입이 
			// catch블록 리스트에 있을 때 true가 된다.
			if (listOfCatchBlocks!=null) {
				for (i=0; i<listOfCatchBlocks.length; i++) {
					FindControlBlockParams catchBlock = listOfCatchBlocks[i];
					FindVarParams var = (FindVarParams) catchBlock.listOfStatementsInParenthesis.getItem(0);
					if (var.typeName.equals(typeNameOfException)) {
						found = true;
						break;
					}
				}
			}
			if (found) break;
			
			if (block!=null) {
				block = block.parent;
			}
		}//while (true) {
		
		if (found==false) {
			// synchronized블록을 만나기 전에 throw에서 던지는 예외가 catch되지 않으므로
			// synchronized의 개수를 세어 synchronized블록을 빠져나오기 전에 오브젝트 잠금을 해제한다.
			int countOfSynchronized = this.getCountOfSynchronizedBeforeCatch(synchronizedBlock, typeNameOfException);
			int k;
			for (k=0; k<countOfSynchronized; k++) {
				result.add("monitorexit"+"\n");
			}
		}
		else {
			// 던지는 예외가 같은 메서드 내에서 catch블록에 의해 잡힐 경우
			// throw문이 synchronized블록 안에 있더라도 catch를 한 이후에 잠금을 해제한다.
		}*/
		
		int countOfSynchronized = this.getCountOfSynchronizedBeforeCatch(statement, typeNameOfException);
		int k;
		for (k=0; k<countOfSynchronized; k++) {
			result.add("monitorexit"+"\n");
		}
		
		// 던질 예외를 스택에 넣는다.
		if (statement.funcCall!=null) {
			this.traverseChild(statement.funcCall, result);
		}
		
		if (statement.kewordIndex()!=-1) {
			result.add("athrow"+"\n");
		}
		else {
			// 가짜 try-catch블록의 가짜 throw문일 경우 가짜 catch블록 안에 있게 되고
			// 가짜 catch블록에 선언된 예외를 던져야 한다.
			FindVarParams exception = 
					(FindVarParams) statement.parent.listOfVariableParams.getItem(0);
			FindVarUseParams varUseException = new FindVarUseParams(null);
			varUseException.varDecl = exception;
			this.printLocalVar(varUseException, result, true);
			result.add("athrow"+"\n");
			
		}
	}
	
	/** block을 처음으로 감싸는 반복 블록을 얻는다. 
	 * 예를들어 continue, break에서 getParentIterationBlock를 호출하면 
	 * 이 블럭을 처음으로 감싸는 반복블록(for, while, dowhile)를 얻는다.*/
	public Block getParentIterationBlock(Block block) {
		while (true) {
			if (block instanceof FindControlBlockParams) {
				FindControlBlockParams control = (FindControlBlockParams) block;
				if (control.catOfControls!=null) {
					if (control.catOfControls.category==CategoryOfControls.Control_for) 
						return control;
					if (control.catOfControls.category==CategoryOfControls.Control_while) 
						return control;
					if (control.catOfControls.category==CategoryOfControls.Control_dowhile) 
						return control;
					if (control.catOfControls.category==CategoryOfControls.Control_switch) 
						return control;
				}
			}
			if (block!=null) {
				block = block.parent;
			}
			else {
				return null;
			}
		}
	}
	
	/** block을 처음으로 감싸는 try 블록을 얻는다. 
	 * 예를들어 throw e;에서 getParentTryBlock를 호출하면 
	 * 이 블럭을 처음으로 감싸는 try블록를 얻는다.*/
	public Block getParentTryBlock(Block block) {
		while (true) {
			if (block instanceof FindControlBlockParams) {
				FindControlBlockParams control = (FindControlBlockParams) block;
				if (control.catOfControls==null) {
					CodeString name = getNameOfControlBlock(control);
					if (name.equals("try"))
						return control;
				}
			}
			if (block!=null) {
				block = block.parent;
			}
			else {
				return null;
			}
		}
	}
	
	/** varUse가 lValue, 함수호출, 배열원소, 아니면 수식 타입캐스트이면 그 다음으로 점프하는 
	 * compiler.mlistOfAllVarUses 상의 인덱스를 리턴한다.
	 * @param varUse
	 * @param indexInmlistOfAllVarUses
	 * @return
	 */
	int jump(FindVarUseParams varUse, int indexInmlistOfAllVarUses) {
		if (varUse.rValue!=null) {
			int endIndex = compiler.getIndexInmListOfAllVarUses(
					compiler.mBuffer, compiler.mlistOfAllVarUses, indexInmlistOfAllVarUses, varUse.rValue.endIndex(), false);
			return endIndex;
		}
		if (varUse.funcDecl!=null) {
			int leftParenthesis = 
				compiler.SkipBlank(compiler.mBuffer, false, varUse.index()+1, compiler.mBuffer.count-1);
			int rightParenthesis =
				CompilerHelper.CheckParenthesis(compiler.mBuffer, "(", ")", leftParenthesis, compiler.mBuffer.count-1, false);
			
			int endIndex = compiler.getIndexInmListOfAllVarUses(
					compiler.mBuffer, compiler.mlistOfAllVarUses, indexInmlistOfAllVarUses, rightParenthesis, false);
			return endIndex;			
		}
		if (varUse.isArrayElement) {
			int leftParenthesis = 
				compiler.SkipBlank(compiler.mBuffer, false, varUse.index()+1, compiler.mBuffer.count-1);
			int rightParenthesis =
				CompilerHelper.CheckParenthesis(compiler.mBuffer, "[", "]", leftParenthesis, compiler.mBuffer.count-1, false);
			
			int endIndex = compiler.getIndexInmListOfAllVarUses(
					compiler.mBuffer, compiler.mlistOfAllVarUses, indexInmlistOfAllVarUses, rightParenthesis, false);
			return endIndex;			
		}
		if (varUse.typeCast!=null/* && varUse.typeCast.affectsExpression*/) {
			TypeCast typeCast = varUse.typeCast;			
			//return typeCast.endIndexToAffect_mlistOfAllVarUses;
			
			// 타입캐스트되는 varUser가 함수호출, lValue, 배열원소일 경우 이미 수식을 출력했으므로
			// 다시 점프한다.
			FindVarUseParams varUseTypeCasted = 
					compiler.mlistOfAllVarUses.getItem(typeCast.endIndexToAffect_mlistOfAllVarUses);
			int r = this.jump(varUseTypeCasted, typeCast.endIndexToAffect_mlistOfAllVarUses);
			return r;
		}
		return indexInmlistOfAllVarUses;
	}
	
	/**int[][][] colors2 = {<br>
	{//a0<br>
		{Color.BLACK, Color.WHITE, Color.RED}, //a00<br>
		{Color.YELLOW,Color.BLUE, Color.GREEN} //a01<br>
	},
	 
	{//a1<br>
		{Color.BLACK, Color.WHITE, Color.RED}, //a10<br>
	    {Color.YELLOW,Color.BLUE, Color.GREEN} //a11<br>
	}
	};//2면 2행 3열<br>
	colors2->a0->a00->Color.BLACK, Color.WHITE, Color.RED <br>
		   ->a01->Color.YELLOW,Color.BLUE, Color.GREEN <br>
	->a1->a10->Color.BLACK, Color.WHITE, Color.RED <br>
	   ->a11->Color.YELLOW,Color.BLUE, Color.GREEN <br>
	깊이 :0    1    2*/
	public void traverseArrayIntializer(HighArray_CodeString src, FindArrayInitializerParams topArray, 
			FindArrayInitializerParams array, HighArrayCharForByteCode result) {
		if (array.listOfFindArrayInitializerParams.count>0) { // 중첩된 배열
			int i;
			this.printArrayInitializer_newarray_NoBottom(
					CompilerHelper.getMaxArrayLengthInCurDepth(topArray, array), 
					array.depth, topArray.varUse, result);
			
			for (i=0; i<array.listOfFindArrayInitializerParams.count; i++) {
				result.add("dup"+"\n");
				try {
					printArrayInitializer_index(i, array.listOfFindFuncCallParam.count, 
							array.depth, topArray.varUse, result);
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				FindArrayInitializerParams child = (FindArrayInitializerParams) array.listOfFindArrayInitializerParams.getItem(i);
				traverseArrayIntializer(src, topArray, child, result);
				
				printArrayInitializer_store_NoBottom(result);
				
				printArrayInitializer_inc(CompilerHelper.getMaxArrayLengthInCurDepth(topArray, array), 
						topArray, array, result);
			}//for (i=0; i<array.listOfFindArrayInitializerParams.count; i++) {
		}
		else { // 수식, 가장 하위 노드
			// 최하위차원
			
			this.printArrayInitializer_newarray_bottom(
					CompilerHelper.getMaxArrayLengthInCurDepth(topArray, array), 
					array.depth, topArray.varUse,
					CompilerHelper.getArrayElementType(topArray.typeFullName), result);
			
			int i, j, k;
			for (i=0; i<array.listOfFindFuncCallParam.count; i++) {
				result.add("dup"+"\n");
				try {
					printArrayInitializer_index(i, CompilerHelper.getMaxArrayLengthInCurDepth(topArray, array),
							array.depth, topArray.varUse, result);
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				FindFuncCallParam funcCallParam = (FindFuncCallParam) array.listOfFindFuncCallParam.getItem(i);
								
				if (funcCallParam.expression.postfix!=null) {					
					
					// 수식트리에서 상위 노드의 포스트픽스
					// f2(1+2) 3 +
					
					// 1 2 +
					// 수식트리에서 자식노드들을 방문하기위해 재귀적호출
					
					this.traverseChild(funcCallParam, result);
					
					/*for (j=0; j<funcCallParam.expression.postfix.length; j++) {
						CodeStringEx token = funcCallParam.expression.postfix[j];
						for (k=0; k<token.listOfVarUses.count; k++) {
							FindVarUseParams child = null;
							try {
							child = (FindVarUseParams) token.listOfVarUses.getItem(k);
							}catch(Exception e) {
								int a;
								a=0;
								a++;
							}
							if (child!=null) {
								// child를 varUse로 recursive call
								traverseExpressionTree(src, child, child.index(), result);
								
								k = getIndex(token, child, k);
							}//if (child!=null) {
						}//for (k=0; k<token.listOfVarUses.count; k++) {
					}//for (j=0; j<funcCallParam.expression.postfix.length; j++) {
					*/
					
					printArrayInitializer_store_bottom(funcCallParam, result);
					
					printArrayInitializer_inc(CompilerHelper.getMaxArrayLengthInCurDepth(topArray, array), 
							topArray, array, result);
					
				}//if (funcCallParam.expression.postfix!=null) {				
				
			}//for (i=0; i<array.listOfFindFuncCallParam.count; i++) {
			
			
		}// else
	
	}
	
	/**배열초기화에서 short범위를 넘어서는 인덱스와, array length를 위한 지역변수를 얻어오기 위해서
	 * varUse를 정의하는 클래스나 함수를 얻어온다.
	 * getLocalVarForArrayInit()와 getLocalVarForArrayInit_arrayLength()를 참조한다. 
	 * @param varUse : 배열초기화문의 lValue*/
	Object getFunctionOrClassToDefineLocalVarsForArrayInit(FindVarUseParams varUse) {
		if (varUse.classToDefineThisVarUse!=null) {
			return varUse.classToDefineThisVarUse;
		}
		else if (varUse.funcToDefineThisVarUse!=null) {
			FindFunctionParams func = varUse.funcToDefineThisVarUse;
			return func;
		}
		return null;		
	}
	
	/** 배열초기화에서 topArray의 listOfLength를 확인하여 array length가 short범위를 넘어서면
	 * index와 array length를 위한 지역변수들을 만든다. 깊이별로 조직해야 하므로 어떤 깊이에서
	 * length가 short범위를 넘지 않으면 null을 넣는다. FindArrayInitializerParams를 참조한다.
	 * @param topArray : 최상위 array, lValue가 가르키는 array
	 */
	void makeLocalVarsForArrayInit(FindArrayInitializerParams topArray) {
		ArrayListInt listOfLength = topArray.listOfLength;
		int i;
		for (i=0; i<listOfLength.count; i++) {
			int length = listOfLength.getItem(i);
			int numberType = CompilerHelper.IsNumber2(String.valueOf(length));
			if (numberType==3 || numberType==4) {
				FindVarParams var = new FindVarParams(compiler, "int", "___varForArrayInit_"+i);
				FindVarParams var2 = new FindVarParams(compiler, "int", "___varForArrayInit_arrayLength_"+i);
				var.isFake = true;
				var2.isFake = true;
				Object r = this.getFunctionOrClassToDefineLocalVarsForArrayInit(topArray.varUse);
				if (r instanceof FindClassParams) {
					FindClassParams classParams = (FindClassParams) r;
					classParams.listOfLocalVarsForArrayInit.add(var);
					classParams.listOfLocalVarsForArrayInit_arrayLength.add(var2);
				}
				else if (r instanceof FindFunctionParams) {
					FindFunctionParams func = (FindFunctionParams) r;
					func.listOfLocalVarsForArrayInit.add(var);
					func.listOfLocalVarsForArrayInit_arrayLength.add(var2);
				}
			}
			else { // short 범위를 넘어서지 않으면
				Object r = this.getFunctionOrClassToDefineLocalVarsForArrayInit(topArray.varUse);
				if (r instanceof FindClassParams) {
					FindClassParams classParams = (FindClassParams) r;
					classParams.listOfLocalVarsForArrayInit.add(null);
					classParams.listOfLocalVarsForArrayInit_arrayLength.add(null);
				}
				else if (r instanceof FindFunctionParams) {
					FindFunctionParams func = (FindFunctionParams) r;
					func.listOfLocalVarsForArrayInit.add(null);
					func.listOfLocalVarsForArrayInit_arrayLength.add(null);
				}
			}
		}//for (i=0; i<listOfLength.count; i++) {
	}
	
	/**복합 대입연산자, 
	   a+=2; 여기에서 a로드, 2로드(traverseChild()), add까지 한다.*/
	void traverseCompositiveEqualOperator(HighArray_CodeString src, FindVarUseParams varUse, HighArrayCharForByteCode result) {
		if (varUse.index()==1932) {
			int a;
			a=0;
			a++;
		}
		
		int endIndexOfLValue = compiler.getFullNameIndex2(src, varUse.index());
		
		int startIndexOfOperator = compiler.SkipBlank(src, false, endIndexOfLValue+1, src.count-1);
		int endIndexOfOperator = compiler.IsLValue(src, varUse);
		
		String operator = compiler.getFullName(src, startIndexOfOperator, endIndexOfOperator).str;
		String operatorExceptEqual = null;
		CodeStringEx operatorEx = null;
		
		CodeStringEx tokenOfLValue = null;
		CodeStringEx tokenOfRValue = null;
		
		CodeStringEx typeOfRValue = null;
		
		// 복합 대입연산자("+=", "-=", "*=" 등)에 대해서만
		// int ab=0;
		// int ba=ab+(ab+=2)+3; 여기에서 두번째 ab
		// ba=ab-(ab-=2.3f+2)*3; 여기에서 두번째 ab

		int startIndexOfLValue = compiler.getFullNameIndex(src, true, varUse.index());
		int startIndexOfLValueInmListOfAllVarUses = compiler.getIndexInmListOfAllVarUses(
				compiler.mBuffer, compiler.mlistOfAllVarUses, 0, 
				startIndexOfLValue, true);
		int endIndexOfLValueInmListOfAllVarUses = compiler.getIndexInmListOfAllVarUses(
				compiler.mBuffer, compiler.mlistOfAllVarUses, startIndexOfLValueInmListOfAllVarUses, 
				endIndexOfLValue, false);
		
		
		
		
		////////////////  lValue를 로드한다. start /////////////////////////
		
		// 중첩된 대입연산자의 lValue를 다시 스택에 로드해야 한다.
		// 밑에 있는 traverseExpressionTree()에서 lValue의 rValue를 null로 안하면
		// traverseLValue()가 호출되어 결과가 틀리게 나오기 때문에
		// 위에서 저장된 lValue를 로드만 해야 하기 때문에 rValue를 null로 만들어 준 후
		// 아래와 같이 token에 대해서 traverseExpressionTree()를 호출하여
		// 저장된 lValue를 스택에 로드한다.
		FindFuncCallParam backupRValue = varUse.rValue;
		varUse.rValue = null;
				
		int k;
		for (k=startIndexOfLValueInmListOfAllVarUses; k<=endIndexOfLValueInmListOfAllVarUses; k++) {
			// 오퍼랜드 하나에 있는 varUse들에 대해서 재귀적 호출을 한다.
			FindVarUseParams child = compiler.mlistOfAllVarUses.getItem(k);
			if (child!=null) {
				traverseExpressionTree(src, child, child.index(), result);
				//k = getIndex(token, child, k);
				k = this.jump(child, k);
				
			}//if (child!=null) {
		}//for (k=0; k<token.listOfVarUses.count; k++) {
		varUse.rValue = backupRValue;
						
		
		CodeStringEx typeOfLValue;
		ReturnOfgetTypeOfVarUseOrFuncCallOfFullName2_sub r = 
				compiler.getTypeOfVarUseOrFuncCallOfFullName(src, startIndexOfLValue);
		typeOfLValue = r.typeFullName;
		
		typeOfRValue = new CodeStringEx(varUse.rValue.typeFullName.str);
		
		// 복합 대입연산자("+=", "-=", "*=" 등)에서 "="을 제외한 연산자를 구한다.
		operatorExceptEqual = operator.substring(0, operator.length()-1);
		operatorEx = new CodeStringEx(operatorExceptEqual);
		operatorEx.typeFullName = varUse.rValue.typeFullName.str;
		operatorEx.isPlusOrMinusForOne = false;
		
		FindFuncCallParam funcCall = new FindFuncCallParam(compiler, startIndexOfLValue, varUse.rValue.endIndex());
		ArrayListIReset listOfTypes = new ArrayListIReset(2);
		listOfTypes.add(typeOfLValue);
		listOfTypes.add(typeOfRValue);
		tokenOfLValue = new CodeStringEx("LValue");
		tokenOfRValue = new CodeStringEx("RValue");
		tokenOfLValue.typeFullName = typeOfLValue.str;
		tokenOfRValue.typeFullName = typeOfRValue.str;
		typeOfLValue.operandOrOperator = tokenOfLValue;
		typeOfRValue.operandOrOperator = tokenOfRValue;
		// int ab=0;
		// ba=ab-(ab-=2.3f+2)*3; 여기에서 두번째 ab
		// 두번째 ab와 2.3f+2에 "-"연산을 할때 오퍼랜드들의 
		// 묵시적 타입캐스팅(typeFullNameAfterOperation)을 정한다.
		// 두번째 ab는 int이고 rValue의 타입은 float이므로 "-"연산을 할때 두번째 ab의 타입을 float로 바꿔야 한다.
		CodeStringEx typeOfOperator = 
				CompilerHelper.getTypeOfOperator(compiler, funcCall, listOfTypes, operatorEx);
		if (typeOfOperator!=null)
			operatorEx.typeFullName = typeOfOperator.str;
		
		
		// int ab=0;
		// ba=ab-(ab-=2.3f+2)*3; 여기에서 두번째 ab
		// 두번째 ab는 int이고 rValue의 타입은 float이므로 "-"연산을 할때 두번째 ab의 타입을 float로 바꿔야 한다.
		if (typeOfLValue!=null && tokenOfLValue!=null && tokenOfLValue.typeFullNameAfterOperation!=null) {
			this.printTypeCast_sub(typeOfLValue.str, 
					tokenOfLValue.typeFullNameAfterOperation, result);
		}
		//////////////// lValue를 로드한다. end /////////////////////////
		
		
		//////////////// rValue를 로드한다. start ///////////////////////
		FindFuncCallParam funcCallOfRValue = varUse.rValue;
		traverseChild(funcCallOfRValue, result);
		
				
		if (typeOfRValue!=null && tokenOfRValue!=null && tokenOfRValue.typeFullNameAfterOperation!=null) {
			// int ab=0;
			// ba=ab-(ab-=2+2)*3; 여기에서 두번째 ab
			// 두번째 ab는 int이고 rValue의 타입은 byte이므로 "-"연산을 할때 rValue의 타입을 int로 바꿔야 한다.
			this.printTypeCast_sub(typeOfRValue.str, 
				tokenOfRValue.typeFullNameAfterOperation, result);
		}
		////////////////rValue를 로드한다. end ///////////////////////
		
		// 복합 대입연산자("+=", "-=", "*=" 등)에서 "="을 제외한 이항 연산자를 출력한다.			
		this.printOperator(operatorEx, result);
	}
	
	
	void traverseLValue(HighArray_CodeString src, FindVarUseParams varUse, int indexInmBuffer, HighArrayCharForByteCode result) {		
		if (indexInmBuffer==2012) {
			int a;
			a=0;
			a++;
		}
		int indexOfEqual = compiler.IsLValue(src, varUse);
		
		int startIndex = compiler.getIndexInmListOfAllVarUses(src, compiler.mlistOfAllVarUses, 0, indexOfEqual+1, true);
		int endIndex=-1;	
		
		
		if (varUse.arrayInitializer==null) {
			if (varUse.rValue.expression.postfix==null) {
				varUse.rValue.typeFullName = 
						compiler.getTypeOfExpression(compiler.mBuffer, varUse.rValue);
				if (varUse.rValue.expression.postfix==null) {
					return;
				}
			}
			
			startIndex = compiler.getIndexInmListOfAllVarUses(src, compiler.mlistOfAllVarUses, 0, varUse.rValue.startIndex(), true);
			endIndex = compiler.getIndexInmListOfAllVarUses(src, compiler.mlistOfAllVarUses, 0, varUse.rValue.endIndex(), false);
		}
		
		// this가 앞에 붙으면 이 함수의 가장 밑에서 처리한다.
		if (varUse.isArrayElement==false) {
			// 배열원소에 저장할 때는 traverseArrayElement()에서 출력하므로
			// 배열이 아닌 일반 멤버변수에 저장하는 putfield 일 때만 this를 출력한다.
			if (varUse.parent==null && varUse.isUsingThisOrSuper) {	
				// putfield 일때만
				// var(var는 인스턴스변수) = i + 2; 이와 같은 경우 다음과 같이
				// 가장 앞에서 rValue 를 계산하기 전에 this 를 load 해야 한다.
				// aload this
				// iload i
				// bipush 2
				// iadd
				// putfield var
				FindClassParams parentClass = (FindClassParams)varUse.classToDefineThisVarUse;
				result.add("aload "+this.getDescriptorOfThis(parentClass)+"\n");
			}
		}
		
		if (varUse.arrayInitializer!=null) {			
			this.makeLocalVarsForArrayInit(varUse.arrayInitializer);
			traverseArrayIntializer(src, 
						varUse.arrayInitializer, varUse.arrayInitializer, result);
			//return;
		}
		else {
			if (varUse.index()==1932) {
				int a;
				a=0;
				a++;
			}
			
			int endIndexOfLValue = compiler.getFullNameIndex2(src, varUse.index());
			
			int startIndexOfOperator = compiler.SkipBlank(src, false, endIndexOfLValue+1, src.count-1);
			int endIndexOfOperator = compiler.IsLValue(src, varUse);
			
			String operator = compiler.getFullName(src, startIndexOfOperator, endIndexOfOperator).str;
		
			if (operator.length()==1) {	 // 일반적인 = 연산자
				FindFuncCallParam funcCall = varUse.rValue;
				traverseChild(funcCall, result);
			}
			else {
				// 복합 대입연산자  
				// a+=2; 여기에서 a로드, 2로드(traverseChild()), add까지 한다.
				this.traverseCompositiveEqualOperator(src, varUse, result);
			}
		}//배열초기화가 아니면

		// lValue 출력
		
		if (varUse.isArrayElement==false) {
			if (varUse.constant_info!=null) { 
				//멤버변수에 저장, putfield, putstatic
				this.printMemberVarUse(varUse, result, false);
			}
			else {//지역변수에 저장, istore, astore 등
				this.printLocalVar(varUse, result, false);
			}
		}
		else { 
			// 배열에 저장
			// arr[0] = i; 에서 varUse는 arr이다. 
			// aastore, iastore 등은 스택상태가 arrRef, index(첨자), value(rValue) 이어야 한다.
			// 따라서 arrRef, index(첨자)는 미리 스택에 로드되어 있어야 하고 rValue를 계산한 후에
			// value가 스택에 로드되면 aastore 등을 출력한다.
			int curDimension = CompilerHelper.getArrayDimension(compiler, varUse.name);
			this.printArrayElement(varUse, result, curDimension, curDimension, false);
		}

	}
	
	/** 함수호출 파라미터가 스트링 상수이고 함수선언 파라미터가 
		java.lang.String 혹은 그것의 서브클래스일 경우
		호출파라미터로부터 String클래스 혹은 서브 클래스를 생성한다.*/
	/*void printFirstProcessInStringConstant(CodeString parameter, String typeNameOfParameter,
			FindClassParams stringClass, FindClassParams childClass, HighArrayCharForByteCode result) {
		if (CompilerHelper.IsConstant(parameter) && 
				typeNameOfParameter.equals("java.lang.String") &&
				FindClassParams.isARelation(compiler, stringClass, childClass)) {
			// 함수호출 파라미터가 스트링 상수이고 함수선언 파라미터가 
			// java.lang.String 혹은 그것의 서브클래스일 경우
			// 호출파라미터로부터 String클래스 혹은 서브 클래스를 생성한다.
			ArrayListChar message = new ArrayListChar(30);
			message.add("new ");
			message.add(getDescriptor(childClass));
			message.add("\n");
			result.add(new String(message.getItems()));
			result.add("dup"+"\n");
		}
	}*/
	
	/** 함수호출 파라미터가 스트링 상수이고 함수선언 파라미터가 
	java.lang.String 혹은 그것의 서브클래스일 경우
	호출파라미터로부터 String클래스 혹은 서브 클래스를 생성한다.*/
	/*boolean printSecondProcessInStringConstant(CodeString parameter, String typeNameOfParameter, 
			FindClassParams stringClass, FindClassParams childClass, HighArrayCharForByteCode result) {
		if (CompilerHelper.IsConstant(parameter) && 
				typeNameOfParameter.equals("java.lang.String") &&
				FindClassParams.isARelation(compiler, stringClass, childClass)) {
			// 함수호출 파라미터가 스트링 상수이고 함수선언 파라미터가 
			// java.lang.String 혹은 그것의 서브클래스일 경우
			// 호출파라미터로부터 String클래스 혹은 서브 클래스를 생성한다.
			FindFunctionParams constructor = this.getConstructor(childClass, 7);
			result.add("invokevirtual "+constructor+"\n");
			return true;
		}
		return false;
	}*/
	
	static class ReturnOfprintFirstProcessInConcatOperator {
		FindClassParams valueClass; 
		FindFunctionParams constructorOfValueClass;
		
		ReturnOfprintFirstProcessInConcatOperator(FindClassParams valueClass, 
				FindFunctionParams constructorOfValueClass) {
			this.valueClass = valueClass;
			this.constructorOfValueClass = constructorOfValueClass; 
		}
	}
	
	/** token이 '+'의 첫번째 오퍼랜드이고 타입이 java.lang.String이거나 서브클래스이고 스트링 상수이면
	 * java.lang.String오브젝트를 생성하고 
	 * token이 '+'의 두번째 오퍼랜드이고 타입이 java.lang.String이거나 서브클래스이고 스트링 상수이면
	 * 마찬가지로 java.lang.String오브젝트를 생성하고 
	 * token이 '+'의 두번째 오퍼랜드이고 타입이 디폴트타입이면 그 타입에 따라 value 클래스를 생성한다.*/
	ReturnOfprintFirstProcessInConcatOperator printFirstProcessInConcatOperator(CodeStringEx token, HighArrayCharForByteCode result) {
		FindClassParams valueClass = null;
		FindFunctionParams constructorOfValueClass = null;
		if (token.affectedBy!=null && token.affectedBy.equals("+")) {
			if (token.affectedBy_left!=null) {
				if (CompilerHelper.IsDefaultType(token.typeFullName)) {
					// token이 int, char와 같은 디폴트타입이고 '+'의 두번째 오퍼랜드이면
					// String str = "abc"+256; 에서 varUse는 256이다.
					// String str = str1 +256; 에서 varUse는 256이다.
					CodeStringEx secondOperand = token.affectedBy.affectsRight;
					if (secondOperand.typeFullName.equals("java.lang.String")) {
						// 첫번째 오퍼랜드가 String클래스이면
						if (token.typeFullName.equals("byte") || token.typeFullName.equals("char") ||
							token.typeFullName.equals("short") || token.typeFullName.equals("int")) {
							valueClass = CompilerHelper.loadClass(compiler, "java.lang.Integer");
							constructorOfValueClass = this.getConstructor(valueClass, 3);
						}
						else if (token.typeFullName.equals("float")) {
							valueClass = CompilerHelper.loadClass(compiler, "java.lang.Float");
							constructorOfValueClass = this.getConstructor(valueClass, 5);
						}
						else if (token.typeFullName.equals("double")) {
							valueClass = CompilerHelper.loadClass(compiler, "java.lang.Double");
							constructorOfValueClass = this.getConstructor(valueClass, 6);
						}
						else if (token.typeFullName.equals("long")) {
							valueClass = CompilerHelper.loadClass(compiler, "java.lang.Long");
							constructorOfValueClass = this.getConstructor(valueClass, 4);
						}
						ArrayListChar message = new ArrayListChar(30);
						message.add("new ");
						try {
						message.add(getDescriptor(valueClass));
						}catch(Exception e) {
							e.printStackTrace();
							int a;
							a=0;
							a++;
						}
						message.add("\n");
						result.add(new String(message.getItems()));
						result.add("dup"+"\n");
					}//if (secondOperand.equals("java.lang.String")) {
				}//if (CompilerHelper.IsDefaultType(token.typeFullName)) {
			}//if (token.affectedBy_left!=null) {
			else if (token.affectedBy_right!=null) {
				if (CompilerHelper.IsDefaultType(token.typeFullName)) {
					// token이 int, char와 같은 디폴트타입이고 '+'의 두번째 오퍼랜드이면
					// String str = "abc"+256; 에서 varUse는 256이다.
					// String str = str1 +256; 에서 varUse는 256이다.
					CodeStringEx firstOperand = token.affectedBy.affectsLeft;
					if (firstOperand.typeFullName.equals("java.lang.String")) {
						// 첫번째 오퍼랜드가 String클래스이면
						if (token.typeFullName.equals("byte") || token.typeFullName.equals("char") ||
							token.typeFullName.equals("short") || token.typeFullName.equals("int")) {
							valueClass = CompilerHelper.loadClass(compiler, "java.lang.Integer");
							constructorOfValueClass = this.getConstructor(valueClass, 3);
						}
						else if (token.typeFullName.equals("float")) {
							valueClass = CompilerHelper.loadClass(compiler, "java.lang.Float");
							constructorOfValueClass = this.getConstructor(valueClass, 5);
						}
						else if (token.typeFullName.equals("double")) {
							valueClass = CompilerHelper.loadClass(compiler, "java.lang.Double");
							constructorOfValueClass = this.getConstructor(valueClass, 6);
						}
						else if (token.typeFullName.equals("long")) {
							valueClass = CompilerHelper.loadClass(compiler, "java.lang.Long");
							constructorOfValueClass = this.getConstructor(valueClass, 4);
						}
						ArrayListChar message = new ArrayListChar(30);
						message.add("new ");
						try {
						message.add(getDescriptor(valueClass));
						}catch(Exception e) {
							e.printStackTrace();
							int a;
							a=0;
							a++;
						}
						message.add("\n");
						result.add(new String(message.getItems()));
						result.add("dup"+"\n");
					}//if (firstOperand.equals("java.lang.String")) {
				}//else if (CompilerHelper.IsDefaultType(token.typeFullName)) {
				
			}//else if (token.affectedBy_right!=null) {
		}//if (token.affectedBy!=null && token.affectedBy.equals("+")) {
		return new ReturnOfprintFirstProcessInConcatOperator(valueClass, constructorOfValueClass);
	}
	
	/** token이 '+'의 첫번째 오퍼랜드이고 타입이 java.lang.String이거나 서브클래스이고 스트링 상수이면
	 * 스택에 올려진 상수로부터 java.lang.String오브젝트를 생성하고 
	 * token이 '+'의 두번째 오퍼랜드이고 타입이 java.lang.String이거나 서브클래스이고 스트링 상수이면
	 * 마찬가지로 스택에 올려진 상수로부터 java.lang.String오브젝트를 생성하고 
	 * token이 '+'의 두번째 오퍼랜드이고 타입이 디폴트타입이면 그 타입에 따라 
	 * 스택에 올려진 값(상수 또는 변수)으로부터 value 클래스를 생성한다.*/
	void printSecondProcessInConcatOperator(CodeStringEx token, 
			FindFunctionParams constructorOfValueClass,
			FindClassParams classHavingToStringFunc,
			HighArrayCharForByteCode result) {
		if (token.affectedBy!=null && token.affectedBy.equals("+")) {
			if (token.affectedBy_left!=null) { 
				if (CompilerHelper.IsDefaultType(token.typeFullName)) {
					// token이 int, char와 같은 디폴트타입이고 '+'의 두번째 오퍼랜드이면
					// String str = "abc"+256; 에서 varUse는 256이다.
					// String str = str1 +256; 에서 varUse는 256이다.						
					CodeStringEx secondOperand = token.affectedBy.affectsRight;
					if (secondOperand.typeFullName.equals("java.lang.String")) {
						// 첫번째 오퍼랜드가 String클래스이거나 그것의 서브클래스일 경우에만
						// Integer 클래스의 Integer(int i) 생성자를 호출한다.
						// Float 클래스의 Float(float f) 생성자를 호출한다. 기타 등등
						result.add("invokevirtual "+constructorOfValueClass+"\n");
						// 여기에서 스택에는 초기화된 Value클래스의 오브젝트가 들어있게 된다.
						
						FindFunctionParams toStringFunc = this.getToStringFunction(classHavingToStringFunc);
						result.add("invokevirtual "+toStringFunc+"\n");
					}
				}
			}//if (token.affectedBy_left!=null) {
			else if (token.affectedBy_right!=null) { 
				if (CompilerHelper.IsDefaultType(token.typeFullName)) {
					// token이 int, char와 같은 디폴트타입이고 '+'의 두번째 오퍼랜드이면
					// String str = "abc"+256; 에서 varUse는 256이다.
					// String str = str1 +256; 에서 varUse는 256이다.						
					CodeStringEx firstOperand = token.affectedBy.affectsLeft;
					if (firstOperand.typeFullName.equals("java.lang.String")) {
						// 첫번째 오퍼랜드가 String클래스이거나 그것의 서브클래스일 경우에만
						// Integer 클래스의 Integer(int i) 생성자를 호출한다.
						// Float 클래스의 Float(float f) 생성자를 호출한다. 기타 등등
						result.add("invokevirtual "+constructorOfValueClass+"\n");
						// 여기에서 스택에는 초기화된 Value클래스의 오브젝트가 들어있게 된다.
						FindFunctionParams toStringFunc = this.getToStringFunction(classHavingToStringFunc);
						result.add("invokevirtual "+toStringFunc+"\n");
					}
				}
			}//else if (token.affectedBy_right!=null) {
			// 여기에서 스택에는 초기화된 String오브젝트와 Value클래스의 오브젝트가 들어있게 된다.
		}//if (token.affectedBy!=null && token.affectedBy.equals("+")) {
	}
	
	/** 수식에 있는 오퍼랜드에 대해서 재귀적 호출을 한다. 묵시적 타입캐스트도 해결한다.*/
	void traverseChild(FindFuncCallParam funcCall, HighArrayCharForByteCode result) {
		int j, k;
		HighArray_CodeString src = compiler.mBuffer;
		
		FindClassParams valueClass = null;
		FindFunctionParams constructorOfValueClass = null;
				
		if (funcCall.expression.postfix==null) return;
		
		for (j=0; j<funcCall.expression.postfix.length; j++) {
			CodeStringEx token = funcCall.expression.postfix[j]; // 오퍼랜드
			if (token.indicesInSrc.getItem(0)==1932) {
				int a;
				a=0;
				a++;
			}
			valueClass = null;
			constructorOfValueClass = null;
			
			// 수식에 있는 스트링 연결연산자를 처리한다.
			ReturnOfprintFirstProcessInConcatOperator r = 
					this.printFirstProcessInConcatOperator(token, result);
			if (r!=null) {
				valueClass = r.valueClass;
				constructorOfValueClass = r.constructorOfValueClass;
			}
			
			
			for (k=0; k<token.listOfVarUses.count; k++) {
				// 오퍼랜드 하나에 있는 varUse들에 대해서 재귀적 호출을 한다.
				FindVarUseParams child = null;
				child = (FindVarUseParams) token.listOfVarUses.getItem(k);
				if (child!=null) {
					traverseExpressionTree(src, child, child.index(), result);
					k = getIndex(token, child, k);
				}//if (child!=null) {
			}//for (k=0; k<token.listOfVarUses.count; k++) {
			
			// 수식 안에 또다른 대입연산자를 포함할 경우를 처리한다.
			int index = loadLValueInNestedEqualOperator(src, funcCall, token, result);
			if (index!=-1) j = index;
			
			// 수식에 있는 스트링 연결연산자를 처리한다.
			printSecondProcessInConcatOperator(token, constructorOfValueClass, valueClass, result);
			
			
			if (CompilerHelper.IsOperator(token)) {
				// token이 스트링 연결 연산자 +일 경우
				// String str = "abc"+256; 에서 varUse는 256이다.
				// String str = str1 +256; 에서 varUse는 256이다.
				// 스트링을 연결한다.
				this.printOperator(token, result);
			}
			if (token.typeFullNameAfterOperation!=null) {
				this.printTypeCast_sub(token.typeFullName, 
						token.typeFullNameAfterOperation, result);
			}
		}//for (j=0; j<funcCall.expression.postfix.length; j++) {
	}
	
	
	/** a = 1+(a=2)+3;에서 token은 두번째 a이다.<br>
	 * 중첩된 대입연산자의 lValue를 다시 스택에 로드해야 한다.
		밑에 있는 traverseExpressionTree()에서 lValue의 rValue를 null로 안하면
		traverseLValue()가 호출되어 결과가 틀리게 나오기 때문에
		위에서 저장된 lValue를 로드만 해야 하기 때문에 rValue를 null로 만들어 준 후
		아래와 같이 token에 대해서 traverseExpressionTree()를 호출하여
		저장된 lValue를 스택에 로드한다. 
	 * @param token : a = 1+(a=2)+3;에서 token은 두번째 a이다
	 * @param funcCall : 첫번째 a의 rValue
	 * @return : 중첩된 대입연산자를 포함할 경우 첫번째 a의 rValue의 포스트픽스 상에서 두번째 =의 위치를 리턴하고
	 *  	중첩된 대입연산자를 포함하지 않으면 -1을 리턴한다.*/
	int loadLValueInNestedEqualOperator(HighArray_CodeString src, FindFuncCallParam funcCall, CodeStringEx token, HighArrayCharForByteCode result) {
		// a = 1+(a=2)+3;에서 token은 두번째 a이다.
		int j = -1;
		ReturnOfgetIndexWhenContainingEqual r = getIndexWhenContainingEqual(token, funcCall);
		if (r!=null && r.index>=0) {
			// 중첩된 대입연산자의 lValue를 다시 스택에 로드해야 한다.
			// 밑에 있는 traverseExpressionTree()에서 lValue의 rValue를 null로 안하면
			// traverseLValue()가 호출되어 결과가 틀리게 나오기 때문에
			// 위에서 저장된 lValue를 로드만 해야 하기 때문에 rValue를 null로 만들어 준 후
			// 아래와 같이 token에 대해서 traverseExpressionTree()를 호출하여
			// 저장된 lValue를 스택에 로드한다.
			FindFuncCallParam backupRValue = r.lValue.rValue;
			r.lValue.rValue = null;
			
			// 토큰 두번째 a에 대해서
			int k;
			for (k=0; k<token.listOfVarUses.count; k++) {
				// 오퍼랜드 하나에 있는 varUse들에 대해서 재귀적 호출을 한다.
				FindVarUseParams child = null;
				child = (FindVarUseParams) token.listOfVarUses.getItem(k);
				if (child!=null) {
					traverseExpressionTree(src, child, child.index(), result);
					k = getIndex(token, child, k);									
					
				}//if (child!=null) {
			}//for (k=0; k<token.listOfVarUses.count; k++) {
			r.lValue.rValue = backupRValue;
			
			// 중첩된 대입연산자가 포함된 경우에만 대입연산자 위치로 점프한다.
			j = r.index;
		}//if (r.index>=0) {
		return j;
	}
	
	/** 명시적 수식 타입캐스트를 해결한다.*/
	void traverseTypeCast(HighArray_CodeString src, FindVarUseParams varUse, int indexInmBuffer, HighArrayCharForByteCode result) {
		FindFuncCallParam funcCallParam = varUse.typeCast.funcCall;
		
		if (varUse.index()==518) {
			int a;
			a=0;
			a++;
		}
		
		int j, k;
		if (funcCallParam.expression!=null) {
			if (funcCallParam.expression.postfix!=null) {					
				
				// 수식트리에서 상위 노드의 포스트픽스
				// f2(1+2) 3 +
				
				// 1 2 +
				// 수식트리에서 자식노드들을 방문하기위해 재귀적호출
				traverseChild(funcCallParam, result);
				
			}//if (funcCallParam.expression.postfix!=null) {
			
		} //if (funcCallParam.expression!=null) {
		// 타입 캐스트 varUse 출력
		//if (varUse.memberDecl!=null) return;
		/*if (varUse.constant_info!=null) {
			try {
			if (varUse.index()==3087) {
				int a;
				a=0;
				a++;
			}
			String str = varUse.constant_info.toString();
			if (str!=null) {
				result.add(str+"\n");
			}
			}catch(Exception e) {
				e.printStackTrace();
				int a;
				a=0;
				a++;
			}
		}*/
		// int i = (int)(f + 2); 에서 타입캐스트 int 가 varUse
		// 이런 경우 f+2를 처리한 뒤에 타입캐스트 int 를 처리한다.
		//if (varUse.typeCast.affectsExpression) {
		if (varUse.typeCast.funcCall!=null) {
			printTypeCast(varUse, funcCallParam.typeFullNameBeforeTypeCast, 
					varUse.typeCast.name, result);
			
		}
	}
	
	/** 타입캐스트를 출력한다.*/
	void printTypeCast(FindVarUseParams varUse, String oldType, String curType, HighArrayCharForByteCode result) {
		if (varUse.typeCast!=null/* && varUse.typeCast.affectsExpression*/) {
			// 수식 타입캐스트, (int)(f+2)
			if (CompilerHelper.IsDefaultType(varUse.typeCast.name) && 
					CompilerHelper.IsDefaultType(oldType)) {
				printTypeCast_sub(oldType, curType, result);
			}
			else {
				if (CompilerHelper.IsDefaultType(varUse.typeCast.name)==false &&
						CompilerHelper.IsDefaultType(oldType)) {
				
				}
				else if (CompilerHelper.IsDefaultType(varUse.typeCast.name)==false) {
					// object이면
					//result.add("checkcast " +varUse.typeCast.name+"\n");
					printTypeCast_sub(oldType, varUse.typeCast.name, result);
				}
			}
		}
		//else if (varUse.fullnameTypeCast!=null) {
		/*else if (varUse.typeCastedByVarUse!=null) {
			// (int)f 와 같은 타입캐스트
			String fullnameTypeCast = FindVarUseParams.getFullnameTypeCast(varUse.typeCastedByVarUse);
			if (CompilerHelper.IsDefaultType(fullnameTypeCast) && 
					CompilerHelper.IsDefaultType(oldType)) {
				printTypeCast_sub(oldType, curType, result);
			}
			else {
				if (CompilerHelper.IsDefaultType(fullnameTypeCast)==false &&
						CompilerHelper.IsDefaultType(oldType)) {
				
				}
				else if (CompilerHelper.IsDefaultType(fullnameTypeCast)==false) {
					// object이면
					//result.add("checkcast " +varUse.fullnameTypeCast+"\n");
					printTypeCast_sub(oldType, fullnameTypeCast, result);
				}
			}
		}*/
	}
	
	void traverseArrayElement(HighArray_CodeString src, FindVarUseParams varUse, int indexInmBuffer, HighArrayCharForByteCode result) {
		if (varUse.index()==855) {
			int a;
			a=0;
			a++;
		}
		// varUse가 배열원소이면서 동시에 배열이 object 의 멤버이거나 아니면 지역변수 일 경우
		// arrayref를 먼저 출력하고 다음에 첨자를 출력한다.
		if (varUse.constant_info!=null) { 
			this.printMemberVarUse(varUse, result, true);
		}
		else { // 작은 정수나 로컬 변수 등
			// 로컬(지역)변수
			printLocalVar(varUse, result, true);
		}		
		
		int dimension = CompilerHelper.getArrayDimension(compiler, varUse.name);
		
		if (varUse.listOfArrayElementParams==null) return;
		
		int i;
		
		// 첨자 출력
		for (i=0; i<varUse.listOfArrayElementParams.count; i++) {
			FindFuncCallParam funcCallParam = 
					(FindFuncCallParam) varUse.listOfArrayElementParams.getItem(i);
							
			if (funcCallParam.expression!=null) {
				if (funcCallParam.expression.postfix!=null) {					
					
					// 1 2 +
					// 수식트리에서 자식노드들을 방문하기위해 재귀적호출
					traverseChild(funcCallParam, result);
					
				}//if (funcCallParam.expression.postfix!=null) {
				
			} //if (funcCallParam.expression!=null) {
			if (i!=varUse.listOfArrayElementParams.count-1) {
				// 마지막 첨자 처리가 아니면 aaload 를 출력한다.
				if (varUse.rValue==null) { //load
					if ((CompilerHelper.IsDefaultType(varUse.originName) ||
							varUse.memberDecl!=null)==false) {
						// 배열원소가 타입이 아니면, 즉 배열오브젝트 생성이 아니면
						printArrayElement(varUse, result, i, dimension, true);
					}
				}
			}
		} //for (i=0; i<varUse.listOfFuncCallParams.count; i++) {
		// 배열원소 varUse 출력
		// 마지막 첨자일 경우에는 iaload, caload 등 배열원소타입대로 출력한다.
		if (varUse.index()==1082) {
			int a;
			a=0;
			a++;
		}
		if (varUse.constant_info!=null) {// 멤버변수 등
			if (varUse.isArrayElement) {
				if (varUse.rValue==null) {//load
					if (varUse.varDecl!=null && varUse.varDecl.typeName!=null) {
						printArrayElement(varUse, result, i, dimension, true);
					}
					else {
						if (CompilerHelper.IsDefaultType(varUse.originName) ||
								varUse.memberDecl!=null) {
							// int[] arr = new int[10];과 같은 배열오브젝트 생성
							printNewArray(varUse, result);
						}
						else if (varUse.funcDecl!=null) {
							// 배열원소인 동시에 함수호출인 경우
							// int[] func() {}  int i = func()[3]; 여기에서 varUse는 func이다.
							printArrayElement(varUse, result, i, dimension, true);
						}
					}
				}
			}
		}//if (varUse.constant_info!=null) {// 멤버변수 등
		else {//지역변수 array
			if (varUse.isArrayElement) {
				if (varUse.rValue==null) {//load
					if (varUse.varDecl!=null && varUse.varDecl.typeName!=null) {
						printArrayElement(varUse, result, i, dimension, true);
					}
					else {
						if (CompilerHelper.IsDefaultType(varUse.originName) ||
								varUse.memberDecl!=null) {
							// int[] arr = new int[10];과 같은 배열오브젝트 생성
							printNewArray(varUse, result);
						}
						else if (varUse.funcDecl!=null) {
							// 배열원소인 동시에 함수호출인 경우
							// int[] func() {}  int i = func()[3]; 여기에서 varUse는 func이다.
							printArrayElement(varUse, result, i, dimension, true);
						}
					}
				}
			}//if (varUse.isArrayElement) {
		}//지역변수 array
		
		//if (varUse.fullnameTypeCast!=null) {
		/*if (varUse.typeCastedByVarUse!=null) {
			this.printTypeCastOfVarUse(varUse, result);
		}*/
	}
	
	void traverseFuncCall(HighArray_CodeString src, FindVarUseParams varUse, int indexInmBuffer, HighArrayCharForByteCode result) {
		if (varUse.index()==538) {
			int a;
			a=0;
			a++;
		}
		if (varUse.funcDecl==null) return;
		
		// this가 앞에 붙으면 이 함수의 가장 밑에서 처리한다.
		// this.func() 혹은 func() 과 같은 멤버함수 호출일 경우
		// invokespecial, invokevirtual 를 하기 전에 먼저 this 를 load 한다.
		if (varUse.parent==null && varUse.isUsingThisOrSuper) {	
			FindClassParams parentClass = (FindClassParams)varUse.classToDefineThisVarUse;
			result.add("aload "+getDescriptorOfThis(parentClass)+"\n");
		}
		
		if (varUse.funcDecl.isConstructor && 
				varUse.funcDecl.isConstructorThatInitializesStaticFields==false) {
			FindClassParams parentClass = null;
			if (varUse.originName.equals("super")==false) {
				parentClass = (FindClassParams) varUse.funcDecl.parent;
				
				ArrayListChar message = new ArrayListChar(30);
				message.add("new ");
				message.add(getDescriptor(parentClass));
				message.add("\n");
				result.add(new String(message.getItems()));
				result.add("dup"+"\n");
			}
			else {
				// public ColorDialog(View owner, Rectangle bounds) {
				// 		super(owner, bounds);
				// }
				// 여기에서 varUse는 super이다.
				// super()는 다음과 같이 컴파일된다.
				// 0     aload Lcom/gsoft/common/gui/ColorDialog; this
				// 2     aload android.view.View owner
				// 4     checkcast java.lang.Object
				// 7     aload com.gsoft.common.Sizing.Rectangle bounds
				// 9     invokespecial com.gsoft.common.gui.Dialog::void Dialog(java/lang/Object, com/gsoft/common/Sizing$Rectangle)


				parentClass = varUse.classToDefineThisVarUse;
				result.add("aload "+getDescriptorOfThis(parentClass)+"\n");
			}
			
		}
		
		int i, j, k;
		
		// f1(f2(1+2)+3)
		for (i=0; i<varUse.listOfFuncCallParams.count; i++) {
			// 파라미터 한개
			FindFuncCallParam funcCallParam = 
					(FindFuncCallParam) varUse.listOfFuncCallParams.getItem(i);
			
			if (funcCallParam.typeFullName==null) continue;
			
			FindVarParams var = (FindVarParams)varUse.funcDecl.listOfFuncArgs.getItem(i);
		
							
			if (funcCallParam.expression!=null) {
				if (funcCallParam.expression.postfix!=null) {
					// 1 2 +
					// 수식트리에서 자식노드들을 방문하기위해 재귀적호출
					traverseChild(funcCallParam, result);					
				}//if (funcCallParam.expression.postfix!=null) {				
			} //if (funcCallParam.expression!=null) {
			
			// 호출 파라미터가 오브젝트일 경우 함수 선언의 파라미터의 타입으로 캐스팅될 수 있는지 확인한다.
			this.printTypeCast_sub(funcCallParam.typeFullName.str, var.typeName, result);
		} //for (i=0; i<varUse.listOfFuncCallParams.count; i++) {
		
		// 함수호출 varUse 출력
		if (varUse.constant_info!=null) { // 메서드 출력 invokestatic, invokevirtual 등
			this.printFuncCall(varUse, result);
		}
		
		
		// (Long)arrUsedSpace.getItem(i)에서 
		// varUse는 getItem이고 fullnameTypeCast는 java.lang.Long이 된다.
		//if (varUse.fullnameTypeCast!=null) {
		/*if (varUse.typeCastedByVarUse!=null) {
			printTypeCastOfVarUse(varUse, result);
		}*/
		
		
	}
	
	
	
	/** 수식 트리를 순회한다.
	 * @param src
	 * @param varUse : 처음 호출시 사용자가 터치한 varUse, 아니면 자식노드의 varUse
	 * @param indexInmBuffer : 처음 호출시 사용자가 터치한 varUse의 mBuffer에서의 인덱스,
	 * , 아니면 자식노드의 varUse의 mBuffer에서의 인덱스
	 * @return : 수식트리를 순회하면서 함수 파라미터(FindFuncCallParam)에 있는 수식의 포스트픽스 표현들의 리스트
	 */
	public void traverseExpressionTree(HighArray_CodeString src, FindVarUseParams varUse, int indexInmBuffer, HighArrayCharForByteCode result) {
		
		if (varUse.index()==855) {
			int a;
			a=0;
			a++;
		}
		
		
		boolean isLValue = false, isFuncCall = false, isArrayElement = false, isTypeCast = false;
		
		int indexOfEqual = -1;
		if (varUse.rValue!=null) {
			indexOfEqual = compiler.IsLValue(src, varUse);
		}
		
		if (indexOfEqual!=-1) { // varUse가 LValue이면
			isLValue = true;
		}//if (IsLValue(src, varUse)!=-1) { // varUse가 LValue이면
		
		if (varUse.funcDecl!=null) {
			isFuncCall = true;
		} // if (varUse.funcDecl!=null) {
		
		
		if (varUse.isArrayElement) { // 배열첨자 처리
			isArrayElement = true;
		}//else if (varUse.isArrayElement) {
		
		if (varUse.typeCast!=null && varUse.typeCast.funcCall!=null) { // (타입)(수식)인 경우 varUse는 타입
			isTypeCast = true;
		}//else if (varUse.typeCast!=null) {
		
		if (isLValue && isArrayElement) {
			// 배열에 저장
			// arr[0] = i; 에서 varUse는 arr이다. 
			// aastore, iastore 등은 스택상태가 arrRef, index(첨자), value(rValue) 이어야 한다.
			// 따라서 arrRef, index(첨자)는 미리 스택에 로드되어 있어야 하고 rValue를 계산한 후에
			// value가 스택에 로드되면 aastore 등을 출력한다.
			traverseArrayElement(src, varUse, varUse.index(), result);
			traverseLValue(src, varUse, varUse.index(), result);
		}
		else if (isFuncCall && isArrayElement) {
			// 함수호출이면서 배열원소일 경우
			// 예를들어 func(p1,p2)[0] 와 같은 경우
			traverseFuncCall(src, varUse, varUse.index(), result);
			traverseArrayElement(src, varUse, varUse.index(), result);
		}
		else if (isLValue) {// int i = a+3;
			traverseLValue(src, varUse, varUse.index(), result);
		}
		else if (isFuncCall) {
			traverseFuncCall(src, varUse, varUse.index(), result);
		}
		else if (isArrayElement) {
			traverseArrayElement(src, varUse, varUse.index(), result);
		}
		else if (isTypeCast) { // 수식 타입캐스트
			traverseTypeCast(src, varUse, varUse.index(), result);
		}
		else {	// 원자적 성질을 갖는다.
			printVarConstantOrTypecast(varUse, result);
		}
		
	} // traverseExpressionTree
	
	
	/** varUse 가 lValue도 아니고 배열원소도 아니고 수식 타입캐스트도 아니면
		따라서 가장 작은 수식에서 오퍼랜드 하나이거나 str.length 에서 str, length를 말한다.
		getfield, getstatic, putfield, putstatic 와 같은 멤버변수 혹은 스트링, 정수, 실수 등의 상수,
		로컬(지역)변수, (int)f 와 같은 타입캐스트,
		명시적 수식타입캐스트가 아닌 명시적 타입캐스트를 해결한다.*/
	void printVarConstantOrTypecast(FindVarUseParams varUse, HighArrayCharForByteCode result) {
		// 일반변수(배열이나 함수호출이 아닌), 일반변수, 상수 등
		if (varUse.memberDecl!=null) return;
		
		// varUse가 this, super이면 super.alpha = 10;에서 super
		if (varUse.varDecl!=null && (varUse.varDecl.isThis || varUse.varDecl.isSuper)) {
			FindClassParams parentClass = (FindClassParams)varUse.classToDefineThisVarUse;
			result.add("aload "+this.getDescriptorOfThis(parentClass)+"\n");
			return;
		}
		
		if (varUse.constant_info!=null) { 
			// getfield, getstatic, putfield, putstatic, 혹은 스트링, 정수, 실수 등의 상수
			CodeString constant = compiler.mBuffer.getItem(varUse.index());
			if (CompilerHelper.IsConstant(constant)) {
				this.printConstant(varUse, result);
			}
			else {
				this.printMemberVarUse(varUse, result, true);
			}
		}
		else { // 작은 정수나 로컬 변수 등
			// true, false 이거나 작은 정수들은 bipush, sipush 를 쓰므로 
			// varUse.constant_info가 null 이다.
			CodeString constant = compiler.mBuffer.getItem(varUse.index());
			if (constant.equals("null")) {
				result.add("aconst_null"+"\n");				
			}
			else if (CompilerHelper.IsConstant(constant)) {
				this.printSmallIntegerConstant(varUse, result);
			}
			else {			
				// 로컬(지역)변수, 일반상수, load
				printLocalVar(varUse, result, true);
			}
		}
		
		// int i = (int)f + 2; (수식 타입캐스트가 아닌)에서  varUse 는 f 가 된다.
		//printTypeCastOfVarUse(varUse, result);
	}
	
	/** int i = (int)f + 2; (수식 타입캐스트가 아닌)에서  varUse 는 f 가 된다.
	 * (Long)arrUsedSpace.getItem(i)에서 
	   varUse는 getItem이고 fullnameTypeCast는 java.lang.Long이 된다.*/
	/*void printTypeCastOfVarUse(FindVarUseParams varUse, HighArrayCharForByteCode result) {
		//if (varUse.fullnameTypeCast!=null) {
		if (varUse.typeCastedByVarUse!=null) {
			CodeString constant = compiler.mBuffer.getItem(varUse.index());
			if (CompilerHelper.IsConstant(constant)) {
				if (constant.equals("true") || constant.equals("false")) {
					
				}
				else { 
					String fullnameTypeCast = FindVarUseParams.getFullnameTypeCast(varUse.typeCastedByVarUse);
					int numberType = CompilerHelper.IsNumber2(constant);
					if (numberType==0) {
						// 문자 상수
						char c = constant.charAt(0).c;
						if (c=='"') {
							printTypeCast(varUse, "java.lang.String", fullnameTypeCast, result); 
							return;
						}
						else if (c=='\'') {
							printTypeCast(varUse, "char", fullnameTypeCast, result); 
							return;
						}
					}
					// 숫자 상수
					switch (numberType) {
					case 0:
					case 1: printTypeCast(varUse, "char", fullnameTypeCast, result); 
					return;
					case 2: printTypeCast(varUse, "short", fullnameTypeCast, result); 
					return;
					case 3: printTypeCast(varUse, "integer", fullnameTypeCast, result); 
					return;
					case 4: printTypeCast(varUse, "long", fullnameTypeCast, result); 
					return;
					case 5: printTypeCast(varUse, "float", fullnameTypeCast, result); 
					return;
					case 6: printTypeCast(varUse, "double", fullnameTypeCast, result); 
					return;
					case 7: printTypeCast(varUse, "byte", fullnameTypeCast, result); 
					return;
					}
				}
			}//if (CompilerHelper.IsConstant(constant)) {
			else {
				// 변수일 경우
				String fullnameTypeCast = FindVarUseParams.getFullnameTypeCast(varUse.typeCastedByVarUse);
				if (varUse.varDecl!=null) {
					printTypeCast(varUse, varUse.varDecl.typeName, fullnameTypeCast, result); return;
				}
				else if (varUse.funcDecl!=null) {
					String returnType = varUse.funcDecl.returnType;
					printTypeCast(varUse, returnType, fullnameTypeCast, result); return;
				}
				else if (varUse.memberDecl!=null) {
					FindClassParams classParams = (FindClassParams) varUse.memberDecl;
					printTypeCast(varUse, classParams.name, fullnameTypeCast, result); return;
				}
			}
		}//if (varUse.fullnameTypeCast!=null) {
	}*/
	
	/** compiler.mlistOfAllVarUses 을 검색하며 하나씩 
	 * varUse.memberDecl 이 null 인 것(null이 아니면 패키지나 내부클래스이므로)을 대상으로 하여
	 * constant table 을 만든다.
	 */
	public void makeConstantTable() {
		int i;
		int len = compiler.mlistOfAllVarUses.getCount();
		for (i=0; i<len; i++) {
			FindVarUseParams varUse = (FindVarUseParams) compiler.mlistOfAllVarUses.getItem(i);
			if (varUse.index()==11179) {
				int a;
				a=0;
				a++;
			}
			if (varUse.funcDecl!=null) {
				int a;
				a=0;
				a++;
			}
			if (varUse.originName.equals("getContext")) {
				int a;
				a=0;
				a++;
			}
			//if (varUse.memberDecl!=null) continue;
			
			this.makeCONSTANT_info(varUse);
		}
		
		int a;
		a=0;
		a++;
	}
	
	
	static class ReturnOfgetIndexWhenContainingEqual {
		int index;
		FindVarUseParams lValue;
		ReturnOfgetIndexWhenContainingEqual(int index, FindVarUseParams lValue) {
			this.index = index;
			this.lValue = lValue;
		}
	}
	
	/** int i = (i=2)+1; f(a=2, 3);
		int i = 1 + (i=2);에서  lValue는 i이다. 
		중첩된 대입연산자의 경우 중첩된 할당문을 출력했으므로 중복 출력을 막기위해서 
		인덱스 j를 증가시켜야 한다.
		@return : 포스트픽스 상에서 중첩된 대입 연산자 위치로 점프하기 위한 index를 리턴한다. 
		문장안에 대입문을 포함하는 경우 index는 0이상이고 
		그렇지 않은 일반적인 경우에는 -1을 리턴한다.*/
	ReturnOfgetIndexWhenContainingEqual getIndexWhenContainingEqual(CodeStringEx token, FindFuncCallParam funcCall) {
		int j = -1;
		// int i = 1 + (i=2);에서  lValue는 i이다. 
		// 중첩된 대입연산자의 경우 인덱스 j를 증가시켜야 한다.
		int indexOfTokenInmBuffer = token.indicesInSrc.getItem(0);
		int indexOfVarUse = compiler.getFullNameIndex(compiler.mBuffer, false, indexOfTokenInmBuffer);
		String varUseName = compiler.mBuffer.getItem(indexOfVarUse).str;
		FindVarUseParams r = 
			compiler.getVarUseWithIndex(compiler.mlistOfAllVarUsesHashed, varUseName, indexOfVarUse);
		
		FindVarUseParams lValue = r;
		if (lValue==null) {
			return null;
		}
		int indexOfEqual = compiler.IsLValue(compiler.mBuffer, lValue);
		// 포스트픽스상에서 =의 인덱스와 같은 토큰을 찾는다.
		if (indexOfEqual>0) {
			int m;
			boolean found = false;
			CodeStringEx[] postfixArr = funcCall.expression.postfix;
			for (m=0; m<postfixArr.length; m++) {
				CodeStringEx tokenInPostfix = postfixArr[m];
				int n;
				// 대입연산자는 =, +=, -=, *=, /=, %= 등이 될 수 있다.
				for (n=0; n<tokenInPostfix.indicesInSrc.count; n++) {
					int indexOfTokenInPostfix = tokenInPostfix.indicesInSrc.getItem(n);
					if (indexOfTokenInPostfix==indexOfEqual) {
						found = true;
						break;
					}
				}
				if (found) break;
			}
			if (m<postfixArr.length) {
				// 중첩된 대입 연산자 위치로 점프한다.
				j = m;
			}
		}//if (indexOfEqual>0) {
		return new ReturnOfgetIndexWhenContainingEqual(j, lValue);
	}
	
	
	/** findNode_varUse_makeString_postfix에서 호출한다.
	 * 수식이 중복되어 처리될수있으므로 인덱스값을 child의 마지막 위치 다음으로 바꿔준다.*/
	int getIndex(CodeStringEx token, FindVarUseParams child, int k) {
		boolean isFuncCallAndArrayElement = false;
		if (child.funcDecl!=null && child.isArrayElement)
			isFuncCallAndArrayElement = true;
		
		/*if (child.rValue!=null) { // 대입문인 경우, a=1+(a=2);에서 child는 두번째 a이다.
			FindFuncCallParam funcCall = child.rValue;
			int endIndexOfFuncCall = funcCall.endIndex()/*+1*/;
			//k = compiler.getIndexInmListOfAllVarUses2(token.listOfVarUses, k, endIndexOfFuncCall, /*true*/false);
			//k--;
		//}*/
		
		if (child.funcDecl!=null) {
			if (child.listOfFuncCallParams.count>0) {
				FindFuncCallParam funcCall = (FindFuncCallParam) 
					child.listOfFuncCallParams.getItem(child.listOfFuncCallParams.count-1);
				int endIndexOfFuncCall = funcCall.endIndex()/*+1*/;
				k = compiler.getIndexInmListOfAllVarUses2(token.listOfVarUses, k, endIndexOfFuncCall, /*true*/false);
				//k--;				
			}
		}
		if (child.isArrayElement) {
			try {
			FindFuncCallParam funcCall = (FindFuncCallParam) 
					child.listOfArrayElementParams.getItem(child.listOfArrayElementParams.count-1);
			int endIndexOfFuncCall = funcCall.endIndex()/*+1*/;
			k = compiler.getIndexInmListOfAllVarUses2(token.listOfVarUses, k, endIndexOfFuncCall, /*true*/false);
			//k--;
			}catch(Exception e) {
				e.printStackTrace();
				int a;
				a=0;
				a++;
			}
		}
		if (child.typeCast!=null && child.typeCast.funcCall!=null) {
			FindFuncCallParam funcCall = (FindFuncCallParam) child.typeCast.funcCall;
			int endIndexOfFuncCall = funcCall.endIndex()/*+1*/;
			k = compiler.getIndexInmListOfAllVarUses2(token.listOfVarUses, k, endIndexOfFuncCall, /*true*/false);
			//k--;
		}
		if (isFuncCallAndArrayElement && child.listOfFuncCallParams.count>0) k++;
		return k;
	}
	
}