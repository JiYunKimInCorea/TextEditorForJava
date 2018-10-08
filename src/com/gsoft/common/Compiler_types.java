package com.gsoft.common;

import java.io.File;

import android.util.Log;

import com.gsoft.common.ByteCode_Types.ByteCodeInstruction;
import com.gsoft.common.ByteCode_Types.Method_Info;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Code.CodeStringType;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.PostFixConverter.CodeStringEx;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListByte;
import com.gsoft.common.Util.ArrayListIReset;
import com.gsoft.common.Util.ArrayListChar;
import com.gsoft.common.Util.ArrayListCodeString;
import com.gsoft.common.Util.ArrayListInt;
import com.gsoft.common.Util.ArrayListString;
import com.gsoft.common.Util.Hashtable2;
import com.gsoft.common.Util.HighArray;
import com.gsoft.common.Util.HighArray_char;
import com.gsoft.common.Util.ObjectPool;

public class Compiler_types {
	
	public enum ModeAllOrUpdate {
		All,
		Update
	}
	
	static class ReturnOfFindVarDecl {
		OldTypeIndex oldTypeIndex;
		FindVarParams var;
		FindVarUseParams varUse;
		
		ReturnOfFindVarDecl(OldTypeIndex oldTypeIndex, FindVarParams var, FindVarUseParams varUse) {
			this.oldTypeIndex = oldTypeIndex;
			this.var = var;
			this.varUse = varUse;
		}
	}
	
	public static class OldTypeIndex {
		IndexForHighArray startIndex;
		IndexForHighArray endIndex;
		IndexForHighArray startIndexOfExpression;
		IndexForHighArray endIndexOfExpression;
		Template template;
		AccessModifier accessModifier;
		Compiler compiler;
		
		public OldTypeIndex(Compiler compiler, int startIndex, int endIndex, int startIndexOfExpression, int endIndexOfExpression,
				Template template, AccessModifier accessModifier) {
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			this.startIndexOfExpression = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndexOfExpression);
			this.endIndexOfExpression = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndexOfExpression);
			this.compiler = compiler;
			this.template = template;
			this.accessModifier = accessModifier;
		}
		
		public void setOldTypeIndex() {
			
		}
		
		boolean hasExpression() {
			if (startIndexOfExpression()!=-1 && endIndexOfExpression()!=-1) return true;
			return false;
		}
		
		int startIndex() {
			if (startIndex==null) return -1;
			return startIndex.index();
		}
		
		int endIndex() {
			if (endIndex==null) return -1;
			return endIndex.index();
		}
		
		int startIndexOfExpression() {
			if (startIndexOfExpression==null) return -1;
			return startIndexOfExpression.index();
		}
		int endIndexOfExpression() {
			if (endIndexOfExpression==null) return -1;
			return endIndexOfExpression.index();
		}
	}
	
	/** HighArray_CodeString내에서 스트링 아이템을 가리키는 (인덱스, 인덱스)쌍이다.*/
	public static class IndexForHighArray implements IReset {
		/** HighArray_CodeString 내에 있는 array의 번호, 이것은 data의 인덱스이다.*/
		int arrayNumber = -1;
		/** HighArray_CodeString 내에 있는 array의 스트링을 가리키는 인덱스*/
		int offset = -1;
		
		Object owner;
		HighArray_CodeString highArray;
		
		
		public void destroy() {
			arrayNumber = -1;
			offset = -1;
			owner = null;
			highArray = null;
		}
		
		public void reset() {
			arrayNumber = -1;
			offset = -1;
		}
		
		/** 인덱스가 생성될 때 highArray의 dataRelated의 해당 파티션(arrayNumber)에 생성된 인덱스를 넣는다.
		 * @param owner : null이면 dataRelated에 넣지 않는다.*/
		public IndexForHighArray(Object owner, HighArray_CodeString highArray, int arrayNumber, int offset) {
			if (owner instanceof FindExpressionParams && arrayNumber==0 && offset==380) {
				int a;
				a=0;
				a++;
			}
			this.arrayNumber = arrayNumber;
			this.offset = offset;
			this.highArray = highArray;
			this.owner = owner;
			
			if (owner!=null) { 
				this.highArray.addToDataRelated(this);
			}
		}
		
		/**상대인덱스(arrayNumber,offset)에서 절대 인덱스를 리턴한다.*/
		public int index() {
			return highArray.index(arrayNumber, offset);
		}
		
		/*public void setIndex(int index) {
			highArray.indexRelative(owner, index, this);
		}*/
		
		/**절대 인덱스에서 상대인덱스(arrayNumber,offset)를 리턴한다.*/
		public static IndexForHighArray indexRelative(Object owner, HighArray_CodeString highArray, int index) {
			if (highArray==null) return null;
			if (index<0) return null;
			return highArray.indexRelative(owner, index);
		}
		
		public String toString() {
			String r = String.valueOf(index()) + "-";
			Class classOfowner = owner.getClass();
			r += classOfowner.getName();
			return r;
		}
	}
	
	public static class HighArrayCharForByteCode extends com.gsoft.common.Util.HighArray_char {
		ArrayListByte codeArray;
		boolean modeCreateOrShow;
		
		int indexOfByteCode;
		
		/**@param modeCreateOrShow : byte배열에 출력할 때는 true, 그렇지 않고 보여주기 위해
		 * char배열에 출력할때는 false이다.
		 * @param arrayLimit
		 */
		public HighArrayCharForByteCode(int arrayLimit, boolean modeCreateOrShow) {
			super(arrayLimit);
			this.modeCreateOrShow = modeCreateOrShow;
			if (modeCreateOrShow) {
				codeArray = new ArrayListByte(1000);
				codeArray.resizeInc = 1000;
			}
		}
		
		/**modeCreateOrShow는 기본적으로 false가 되므로 byte배열에 출력하는 것이 아니라
		 *  보여주기 위해 char배열에 출력한다.
		 * @param arrayLimit
		 */
		public HighArrayCharForByteCode(int arrayLimit) {
			super(arrayLimit);
			modeCreateOrShow = false;
		}
		
		public void add(String code) {
			int i;
			String strInstruciton = code;
			if (code.charAt(0)!='/') {
				strInstruciton = Util.getLineOffset(indexOfByteCode) + " " + code;
			}
			else {
				strInstruciton = "           " + code;
			}
			super.add(strInstruciton);
			
			if (code.charAt(0)!='/') {
				String instructionName = null;
				for (i=0; i<code.length(); i++) {
					char ch = code.charAt(i);
					if (ch==' ' || ch=='\n') break;
				}
				instructionName = code.substring(0, i);
				ByteCodeInstruction instruction = 
					(ByteCodeInstruction) ByteCodeGenerator.hashTableInstructionSet.getData(instructionName);
				if (instruction==null) {
					/*if (instructionName.equals("iconst")) {
						this.indexOfByteCode++;
					}
					else {
						this.indexOfByteCode++;
					}*/
					int a;
					a=0;
					a++;
				}
				if (instruction.hasVariableIndices() ) {
					
				}
				else {
					this.indexOfByteCode += 1 + instruction.getLenOfIndices();
				}
			}//if (code.charAt(0)!='/') {
			
		}
		
	}
	

	
	
	
	/** 아주 큰 배열을 가변크기(처음 로딩시에는 고정크기 arrayLimit를 갖지만 
	 * 수정을 하면 가변크기가 된다.)의 작은 배열 여러개로 나눈 array이다.*/
	public static class HighArray_CodeString implements IReset {
		int arrayLimit;
		
		/**ArrayListCodeString[]*/
		ArrayList data;
		
		/**data에 있는 array에 관련된 데이터들을 넣는다. 
		 * 즉 소스가 있는 data의 해당 array가 
		 * 바뀌면 변경 되어야 할 인덱스들을 갖는다. data의 count의 크기를 갖는다. 
		 * ArrayList[]*/
		ArrayList dataRelated;
		
		/** data 아이템, ArrayListCodeString[]의 개수를 누적시킨 int[]이다. 
		 * 소스의 수정이 있을때마다 자동으로 바뀐다.*/
		ArrayListInt dataSumCount;
		
		public int count;

		public String name;
		
		int lengthOfItemOfDataRelated = 100;

		int resizeInc;
		
		/** 소스의 어떤 부분이 제거가 되면(deleteDataRelated()) 이 풀로 
		 * 관계된 IndexForHighArray들이 옮겨진다.*/
		ArrayList listOfIndexForHighArray;
		
		
		
		/** @param arrayLimit : 작은 배열의 limit*/
		public HighArray_CodeString (int arrayLimit) {
			this.arrayLimit = arrayLimit;
			data = new ArrayList(10);
			dataRelated = new ArrayList(10);
			dataSumCount = new ArrayListInt(10);
			resizeInc = arrayLimit;
			listOfIndexForHighArray = new ArrayList(arrayLimit);
		}
		
		/*public int getLastArrayNumber() {
			return data.count-1;
		}
		
		public int getOffset() {
			ArrayListCodeString arr = (ArrayListCodeString) data.getItem(data.count-1);
			return arr.count-1;
		}
		
		public IndexForHighArray getLastIndexForHighArray() {
			IndexForHighArray index = null;
			int arrNum = data.count-1;
			ArrayListCodeString arr = (ArrayListCodeString) data.getItem(data.count-1);
			int offset = arr.count-1;
			index = new IndexForHighArray(this, this, arrNum, offset);
			return index;
		}*/
		
		/** 소스변경이 쉽도록 index를 dataRelated의 해당 array에 등록해준다.
		 * 소스변경이 있으면 소스가 있는 해당 array와 관련된 dataRelated의 해당 array에 등록된
		 * index들을 바꿔준다.*/
		public void addToDataRelated(IndexForHighArray index) {
			if (index==null) return;
			if (index.arrayNumber==-1) return;
			ArrayList arr = (ArrayList) dataRelated.getItem(index.arrayNumber);
			arr.add(index);
		}
		
		/** 상대인덱스(arrayNumber,offset)를 절대인덱스로 바꾼다.*/
		public int index(int arrayNumber, int offset) {
			int i;
			int index = 0;
			// 이전 array까지의 개수들의 합
			for (i=0; i<arrayNumber; i++) {
				ArrayListCodeString arr = (ArrayListCodeString) data.getItem(i);
				index += arr.count;
			}
			index += offset;
			return index;
		}
		
		public String toString() {
			int i, j;
			CodeString result = new CodeString("", Compiler.textColor);
			int dataLen = data.count;
			for (i=0; i<dataLen; i++) {
				ArrayListCodeString arr = (ArrayListCodeString) data.getItem(i);
				int arrLen = arr.count;
				for (j=0; j<arrLen; j++) {					
					CodeString str = arr.getItem(j);
					if (str!=null) {
						result = result.concate(str);
					}					
				}
			}
			return result.str;
		}
		
		/** 절대인덱스 index에 있는 스트링을 str로 바꾼다.*/
		public void setCodeString(int index, CodeString str) {
			IndexForHighArray indexRelative = indexRelative(null, index);
			ArrayListCodeString arr = (ArrayListCodeString) data.getItem(indexRelative.arrayNumber);
			arr.setCodeString(indexRelative.offset, str);
		}
		
		/** 절대인덱스를 상대인덱스(arrayNumber,offset)로 바꾼다.
		 * owner를 null로 하면 dataRelated에 등록되지 않고 조회만 할 수 있다.*/
		public IndexForHighArray indexRelative(Object owner, int index) {
			if (index<0) return null;
			int indexOfData = -1;
			int i;
			int len = 0;   // 현재 array까지의 개수들의 합
			int oldLen = 0;// 이전 array까지의 개수들의 합
			int dataLen = data.count;
			for (i=0; i<dataLen; i++) {
				ArrayListCodeString arr = (ArrayListCodeString) data.getItem(i);
				oldLen = len;
				len += arr.count;
				if (index<len) {
					indexOfData = i;
					break;
				}
			}
			if (index>=len) {
				return null; // ArrayIndexOUtOfBoundsException
			}
			
			int indexInArray = index - oldLen; // offset
			return new IndexForHighArray(owner, this, indexOfData, indexInArray);
		}
		
		
		/** 절대인덱스를 상대인덱스(arrayNumber,offset)로 바꾼다.*/
		public void indexRelative(Object owner, int index, IndexForHighArray result) {
			if (index<0) return;
			int indexOfData = -1;
			int i;
			int len = 0;    // 현재 array까지의 개수들의 합
			int oldLen = 0; // 이전 array까지의 개수들의 합
			int dataLen = data.count;
			for (i=0; i<dataLen; i++) {
				ArrayListCodeString arr = (ArrayListCodeString) data.getItem(i);
				oldLen = len;
				len += arr.count;
				if (index<len) {
					indexOfData = i;
					break;
				}
			}
			if (index>=len) {
				return; // ArrayIndexOUtOfBoundsException
			}
			
			int indexInArray = index - oldLen; // offset
			result.arrayNumber = indexOfData;
			result.offset = indexInArray;
			result.owner = owner;
		}
		
		/** 모든 자원을 해제한다.*/
		public void destroy() {
			int j;
			int dataLen = data.list.length;
			for (j=0; j<dataLen; j++) {
				ArrayListCodeString list = (ArrayListCodeString) data.getItem(j);
				if (list!=null) list.reset();
				list = null;
			}
			data.reset();
		}
		
		public int getCount() {
			// 마지막 item을 얻는다.
			ArrayListCodeString arrItem = (ArrayListCodeString) data.getItem(data.count-1);
			//int len = arrayLimit * (data.count-1) + arrItem.count;
			int i;
			int dataLen = data.count-1;
			int len = 0;
			for (i=0; i<dataLen; i++) {
				ArrayListCodeString arr = (ArrayListCodeString) data.getItem(i);
				len += arr.count;
			}
			len += arrItem.count;
			return len;
		}
				
		public CodeString getItem(int index) {
			//int indexOfData = index / arrayLimit;
			//int indexInArray = index % arrayLimit;
			
			IndexForHighArray indexForHighArray = this.indexRelative(null, index);
			
			ArrayListCodeString arrItem = (ArrayListCodeString) data.getItem(indexForHighArray.arrayNumber);
			return arrItem.getItem(indexForHighArray.offset);
		}
		
		public void add(CodeString c) {
			if (data.count==0) {
				ArrayListCodeString newItem = new ArrayListCodeString(arrayLimit);
				newItem.resizeInc = this.resizeInc;
				data.add(newItem);
				ArrayList newItemOfDataRelated = new ArrayList(lengthOfItemOfDataRelated);
				newItemOfDataRelated.resizeInc = this.resizeInc;
				dataRelated.add(newItemOfDataRelated);
			}
			
			ArrayListCodeString item;
			
			// 마지막 item을 얻는다.
			ArrayListCodeString arrItem = (ArrayListCodeString) data.getItem(data.count-1);
			if (arrItem.count>=arrayLimit) { // 새로운 item을 생성해서 넣는다.
				ArrayListCodeString newItem = new ArrayListCodeString(arrayLimit);
				newItem.resizeInc = this.resizeInc;
				data.add(newItem);
				item = newItem;
				ArrayList newItemOfDataRelated = new ArrayList(lengthOfItemOfDataRelated);
				newItemOfDataRelated.resizeInc = this.resizeInc;
				dataRelated.add(newItemOfDataRelated);
			}
			else {
				item = arrItem;
			}
			
			item.add(c);
			count++;
		}
		
		
		public CodeString[] toArray() {
			if (data.count<=0) {
				return new CodeString[0];
			}
			
			int i, j;
			// 마지막 item을 얻는다.
			//ArrayListCodeString arrItem = (ArrayListCodeString) data.getItem(data.count-1);
			//int len = arrayLimit * (data.count-1) + arrItem.count;
			int len = this.getCount();
			
			CodeString[] r = new CodeString[len];
			int k=0;
			
			for (i=0; i<data.count; i++) {
				ArrayListCodeString item = (ArrayListCodeString) data.getItem(i);
				for (j=0; j<item.count; j++) {
					r[k] =  item.getItem(j);
					k++;
				}
			}			
			return r;
		}

		public CodeString[] getItems() {
			// TODO Auto-generated method stub
			return this.toArray();
		}

		public CodeString[] substring(int start, int len) {
			HighArray_CodeString r = new HighArray_CodeString(len);
			int i;
			for (i=start; i<start+len; i++) {
				r.add(this.getItem(i));
			}
			return r.getItems();
		}
		
		/** startIndex와 endIndex를 상대인덱스로 고쳐서 그 사이에 있는 인덱스들을 모두 지우고
		 * poolOfIndexForHighArray에 넣는다.
		 * @param startIndex : 절대 인덱스, 소스상 인덱스의 시작
		 * @param endIndex : 절대 인덱스, 소스상 인덱스의 끝
		 */
		void deleteDataRelated_undelete(int startIndex, int endIndex) {
			IndexForHighArray start = this.indexRelative(null, startIndex);
			IndexForHighArray end = this.indexRelative(null, endIndex);
			
			listOfIndexForHighArray.count = 0;
			
			if (start.arrayNumber==end.arrayNumber) {
				ArrayList arr = (ArrayList) this.dataRelated.getItem(start.arrayNumber);
				int i;
				for (i=0; i<=arr.count; i++) {
					IndexForHighArray index = (IndexForHighArray) arr.getItem(i);
					if (index!=null && (start.offset<=index.offset && index.offset<=end.offset)) { 
						//index.destroy();
						//index.reset();
						listOfIndexForHighArray.add(index);
						//arr.list[i] = null;
					}
				}			
				//arr.delete(startIndex, end.offset-start.offset+1);
			}
			else {
				
			}
		}


		public void update_addCharReally(int startIndex, int endIndex, AddCharReallyMode mode) {
			// TODO Auto-generated method stub
			
		}
		
	}//public static class HighArray_CodeString {
	
	/**사용자가 어떤 지점에 클릭을 해서 키를 추가할 경우 스트링의 이름만 바꾸는 것인가,
	 * 아니면 소스에 스트링을 추가할 것인가를 알아내서 리턴한다.*/
	public enum AddCharReallyMode {
		/**소스내 어떤 스트링의 이름만 바꾼다*/
		General_NoAddDeleteTomBuffer,
		/**소스에 스트링을 추가한다.*/
		General_AddTomBuffer
	}
	
	public static interface IReset {
		public abstract void destroy();
	}
	
	/** startIndex와 endIndex가 둘다 -1이면 디폴트 AccessModifier(AccessModifier가 없는)가 된다.*/
	public static class AccessModifier implements IReset {
		public enum AccessPermission {
			Public,
			Private,
			Protected,
			Default
			
		}
		/** enum 형 AccessPermission을 스트링으로 변환한다.*/
		public String toString(AccessPermission accessPermission) {
			if (accessPermission==AccessPermission.Public) return "public";
			else if (accessPermission==AccessPermission.Private) return "private";
			else if (accessPermission==AccessPermission.Protected) return "protected";
			else return "";
		}
		AccessPermission accessPermission;
		
		boolean isStatic;
		boolean isFinal;
		boolean isSuper;
		boolean isInterface;
		boolean isAbstract;
		boolean isEnum;
		
		boolean isSynchronized;
		boolean isNative;
		
		boolean found;
		
		
		IndexForHighArray startIndex;
		IndexForHighArray endIndex;
		
		Compiler compiler;
		
		void reset() {
			accessPermission = null;
			
			isStatic = false;
			isFinal = false;
			isSuper = false;
			isInterface = false;
			isAbstract = false;
			isEnum = false;
			
			isSynchronized = false;
			isNative = false;
			
			found = false;
		}
		
		
		AccessModifier(Compiler compiler, int startIndex, int endIndex) {
			isStatic = false;
			isFinal = false;		 
			accessPermission = null;
			
			this.compiler = compiler;
			if (compiler!=null) {
				this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
				this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			}
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (startIndex!=null) {
				startIndex.destroy();
				startIndex = null;
			}
			if (endIndex!=null) {
				endIndex.destroy();
				endIndex = null;
			}
		}
		
		/** 반환값이 ""이 아니면 스트링의 마지막에 공백이 붙는다.*/
		public String toString() {
			String r;
			if (accessPermission!=null)
				r = toString(accessPermission) + " ";
			else r = "default ";
			
			if (isStatic) r += "static ";
			if (isFinal) r += "final ";
			if (isSynchronized) r += "synchronized ";
			return r;
		}

		public int startIndex() {
			if (startIndex==null) return -1;
			return startIndex.index();
		}
		
		public int endIndex() {
			if (endIndex==null) return -1;
			return endIndex.index();
		}
	}
	
	
	
	
	
	public static class Block extends FindStatementParams {
		/*int startIndex;
		int endIndex;
        boolean found;*/
		//Block parent;
		FindBlockParams findBlockParams;
        
		/**변수 선언들*/
        ArrayListIReset listOfVariableParams = new ArrayListIReset(10);
    	
    	/**제어블럭이거나 제어문들*/
        ArrayListIReset listOfControlBlocks = new ArrayListIReset(2);
        
        /**synchronized, try, catch, finally 등*/
        ArrayListIReset listOfSpecialBlocks = new ArrayListIReset(2);
        
        
        /** 함수안의 문장들의 리스트, 할당문, 제어문, 함수호출문(Void) 등의 순서적 나열
         * 예를들어 ColorDialog의 문장리스트는 다음과 같다.<br>
         * [com.gsoft.common.Compiler_types$AndSoOnStatement@97d66a, 
         * int[] colors, 
         * com.gsoft.common.Compiler_types$FindAssignStatementParams@7dea47, 
         * com.gsoft.common.Compiler_types$AndSoOnStatement@9ff430, 
         * java.lang.String[] namesOfColorButtons, 
         * com.gsoft.common.Compiler_types$FindAssignStatementParams@15888f7, 
         * com.gsoft.common.Compiler_types$AndSoOnStatement@1884f64, 
         * java.lang.Byte[] indicesOfButtonsInGroup, 
         * com.gsoft.common.Compiler_types$FindAssignStatementParams@146877, 
         * com.gsoft.common.Compiler_types$AndSoOnStatement@1d6733c, 
         * com.gsoft.common.gui.Buttons.Button[] colorButtons,
         * com.gsoft.common.Compiler_types$FindAssignStatementParams@1bee467, 
         * com.gsoft.common.Compiler_types$AndSoOnStatement@18567b6, 
         * com.gsoft.common.Compiler_types$Comment@15c057c, 
         * com.gsoft.common.Compiler_types$AndSoOnStatement@6c4a08, 
         * float scaleOfGapY, 
         * com.gsoft.common.Compiler_types$FindAssignStatementParams@1707c77, 
         * com.gsoft.common.Compiler_types$AndSoOnStatement@1fba4dd,
         * public void cancel(), 
         * com.gsoft.common.Compiler_types$AndSoOnStatement@61eae, 
         * com.gsoft.common.Compiler_types$Annotation@1c85b94, 
         * com.gsoft.common.Compiler_types$AndSoOnStatement@f459a4, 
         * public void onTouchEvent(java.lang.Object, com.gsoft.common.Events.MotionEvent), 
         * com.gsoft.common.Compiler_types$AndSoOnStatement@293404]*/
		ArrayListIReset listOfStatements = new ArrayListIReset(20);
		
		
		
		/*Block(Compiler compiler, int startIndex, int endIndex) {
			super(this, compiler, startIndex, endIndex);
		}*/
		
		
		
	}
	
	/** com.gsoft.common에서 com, gsoft, common 각각을 말한다.*/
	public static class FindPackageParams implements IReset {
		/** short name*/
		String packageName;
		/** java/lang/String과 같은 풀네임, 완벽한 path는 아님*/
		String packageParentFullName;
		String[] listOfChildrenNames;
		
		/** package ***.***; 이 들어있는 해당 문서파일*/
		Compiler compiler;
		
		
		public String toString() {
			int i;
			String r = packageName;
			return r;
		}
		
		/** @param packageName : short name
		 * @param parentFullName : java/lang/String과 같은 풀네임, 완벽한 path는 아님*/ 
		FindPackageParams(String packageName, String parentFullName, String[] listOfChildrenNames) {
			this.packageName = packageName;
			this.packageParentFullName = parentFullName;
			this.listOfChildrenNames = listOfChildrenNames; 
		}
		
		boolean contains(String name) {
			int i;
			for (i=0; i<listOfChildrenNames.length; i++) {
				if (listOfChildrenNames[i].equals(name)) return true;
			}
			return false;
		}
		
		/** separatorType이 0이면 '.', 1이면 File.separatorChar이다.*/
		String getFullName(int separatorType) {
			String r;
			if (separatorType==0) {
				r = packageParentFullName + "." + packageName;
				r = r.replace(File.separatorChar, '.');
				if (r.charAt(0)=='.') {
					r = r.substring(1, r.length());
				}
				if (r.charAt(r.length()-1)=='.') {
					r = r.substring(0, r.length()-1);
				}
			}
			else {
				r = packageParentFullName + File.separator + packageName;
				r = r.replace('.', File.separatorChar);
				if (r.charAt(0)==File.separatorChar) {
					r = r.substring(1, r.length());
				}
				if (r.charAt(r.length()-1)==File.separatorChar) {
					r = r.substring(0, r.length()-1);
				}
			}
			return r;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			packageName = null;
			packageParentFullName = null;
			listOfChildrenNames = null;
		}
	}
	
	
	
	
	public static enum LoadWayOfFindClassParams {
		None,
		ByteCode,
		Start2,
		Start_OnlyInterface,
		Start_OnlyClass
	}
	
	
	
	public static class FindClassParams extends Block  implements IReset {
		//Compiler compiler;
		
		LoadWayOfFindClassParams loadWayOfFindClassParams;
		
		/** setOnTouchListener(new View.OnTouchListener() {}); 에서 View.OnTouchListener()는 
		 * 이벤트 핸들러 클래스이므로 true 이다. 일반적인 클래스는 false 이다. */
		boolean isEventHandlerClass;
		
		/**addsNewly가 true 일 경우 즉 CompilerHelper.loadClassFromSrc_onlyInterface()에서 
		 * getSourceFilePath()이 소스파일을 찾지 못해서
		   getSourceFilePathAddingComGsoftCommon()으로 소스파일을 찾게 된 경우
		   fullName에서 com.gsoft.common을 제거한 이름을 새로운 fullName으로 한다.
		     즉 새로이 제공된 소스파일에 있는 클래스로 클래스파일을 읽기 실패한 클래스를	대체하게 된다.
		   새로 제공된 소스파일의 클래스이름은 com.gsoft.common.java.lang.String일 경우 
		     원래 클래스 이름은 java.lang.String이 된다.*/
		boolean addsNewly;
		
		/** addsNewly가 true일 경우 즉 CompilerHelper.loadClassFromSrc_onlyInterface()에서 
		 * getSourceFilePath()이 소스파일을 찾지 못해서
		   getSourceFilePathAddingComGsoftCommon()으로 소스파일을 찾게 된 경우
		   새로 제공된 소스파일의 클래스이름은 com.gsoft.common.java.lang.String일 경우 
		     원래 클래스 이름은 java.lang.String이 된다.
		   추가로 붙여지는 접두어*/
		static String prefixInCaseOfAddsNewly = "com.gsoft.common.";
		
		/** isEventHandlerClass이 true 일 때만*/
		IndexForHighArray startIndexOfEventHandlerName = null;
		/** isEventHandlerClass이 true 일 때만*/
		IndexForHighArray endIndexOfEventHandlerName = null;
		
		int startIndexOfEventHandlerName() {
			if (startIndexOfEventHandlerName==null) return -1;
			return startIndexOfEventHandlerName.index();
		}
		
		int endIndexOfEventHandlerName() {
			if (endIndexOfEventHandlerName==null) return -1;
			return endIndexOfEventHandlerName.index();
		}
		
		
		/** enum 형일 경우 true*/
		boolean isEnum;
		
		/** interface 형일 경우 true*/
		boolean isInterface;
		
		/** full name, 파일에서 정의한 클래스도 외부라이브러리클래스와 동일
		 * (FindAllClassesAndItsMembers2에서 정의)*/
		String name;
		
		//boolean isPackage;
		
		DocuComment docuComment;
		AccessModifier accessModifier;
		
        IndexForHighArray classIndex=null;
        IndexForHighArray classNameIndex=null;
        
        int classIndex() {
        	if (classIndex==null) return -1;
        	return classIndex.index();
        }
        
        int classNameIndex() {
        	if (classNameIndex==null) return -1;
        	return classNameIndex.index();
        }
        
        ArrayListIReset listOfFunctionParams = new ArrayListIReset(10);
        
        /**상속을 이미 했으면 true, 아니면 false*/
        boolean hasInherited;
        ArrayListIReset listOfVarParamsInherited;
        ArrayListIReset listOfFunctionParamsInherited;
        
        
        
        /**FindVarUseParams[]*/
        HighArray<FindVarUseParams> listOfAllVarUses/* = new ArrayListIReset(200)*/;
    	
    	/**FindVarUseParams[]*/
    	HighArray<FindVarUseParams> listOfAllVarUsesForVar/* = new ArrayListIReset(100)*/;
    	
    	
    	
    	/**FindVarUseParams[]*/
    	Hashtable2 listOfAllVarUsesForVarHashed;
    	
    	 /**FindVarUseParams[]*/
    	HighArray<FindVarUseParams> listOfAllVarUsesForFunc/* = new ArrayListIReset(50)*/;
    	Hashtable2 listOfAllVarUsesForFuncHashed;
        
        /** FindClassParams[]*/
        ArrayListIReset childClasses;
        
        /** FindEnumParams[]*/
        //ArrayListIReset childEnums;
        
        /** extends 키워드의 인덱스, 없으면 -1*/
        IndexForHighArray indexOfExtends = null;
        /** implements 키워드의 인덱스, 없으면 -1*/
        IndexForHighArray indexOfImplements = null;
       
        /**확장클래스의 풀이름*/
        String classNameToExtend;
        /**확장클래스이름의 시작과 끝 인덱스*/
        IndexForHighArray startIndexOfClassNameToExtend = null;
        /**확장클래스이름의 시작과 끝 인덱스*/
        IndexForHighArray endIndexOfClassNameToExtend = null;
        
        /**구현인터페이스들의 풀이름들의 리스트, 인터페이스일 경우 확장 인터페이스들의 풀이름 리스트*/
        ArrayListString interfaceNamesToImplement;
        /**구현인터페이스 이름들의 시작과 끝 인덱스들의 리스트*, 인터페이스일 경우 확장 인터페이스들의 시작과 끝 인덱스들의 리스트,
         * IndexForHighArray[]*/
        ArrayList listOfStartIndexOfInterfaceNamesToImplement;
        /**구현인터페이스 이름들의 시작과 끝 인덱스들의 리스트, 인터페이스일 경우 확장 인터페이스들의 시작과 끝 인덱스들의 리스트
         * IndexForHighArray[]*/
        ArrayList listOfEndIndexOfInterfaceNamesToImplement;
        
        FindClassParams classToExtend;
        /** FindClassParams[] 상속할 클래스들의 리스트*/
        ArrayListIReset listOfClassesToExtend;
        /**인터페이스일 경우 확장 인터페이스들의 풀이름 리스트*/
        ArrayList interfacesToImplement;
        
        /** 제어블럭안의 문장들의 리스트, 할당문, 제어문, 함수호출문 등의 순서적 나열*/
		//ArrayListIReset listOfStatements = new ArrayListIReset(10);
		
		/**FindSpecialStatementParams[], return, break 등*/
    	//ArrayListIReset listOfSpecialStatements = new ArrayListIReset(10);
    	
    	/** 현재 클래스가 템플릿일 경우 null이 아니다. class Stack<T> {}에서 <T>를 말한다.*/
    	public Template template;
    	/** 확장클래스가 템플릿일경우 null이 아님*/
		public Template templateForExtending;
		/** 구현인터페이스들이 템플릿일경우 null이 아님, Template[]*/
		public ArrayListIReset listOfTemplatesForImplementing;

		/** reset()가 호출되면 true로 설정된다.*/
		private boolean disposed;
		
		/** 바이트코드에서 클래스파일을 읽거나 바이트코드를 생성할때
		 *  static 필드들을 초기화하기 위해 컴파일러가 새로 만든 생성자이면 null 이 아니고, 
		 *  그렇지 않으면 null 이다. */
		//FindFunctionParams staticConstructorThatCompilerMakes = null;
		
		
		/** 배열초기화에서 short 범위를 넘어서는 인덱스를 사용하기 위한 지역변수들의 리스트*/ 
		ArrayListIReset listOfLocalVarsForArrayInit = new ArrayListIReset(5);
		
		/** 배열초기화에서 short 범위를 넘어서는 array length를 사용하기 위한 지역변수들의 리스트*/ 
		ArrayListIReset listOfLocalVarsForArrayInit_arrayLength = new ArrayListIReset(5);
		
		ArrayListIReset listOfConstructor = new ArrayListIReset(5);
		
    	
    	/** 클래스, 인터페이스를 찾았을때 호출, FindAllClassesAndItsMembers2_sub() 참조*/
    	void allocateListOfVarUses(ModeAllOrUpdate modeAllOrUpdate) {
    		if (modeAllOrUpdate==ModeAllOrUpdate.All) {
	    		if (listOfAllVarUses==null) listOfAllVarUses = new HighArray<FindVarUseParams>(200);
	    		listOfAllVarUses.resizeInc = 200;
	        
	    		if (listOfAllVarUsesForVar==null) listOfAllVarUsesForVar = new HighArray<FindVarUseParams>(100);
	        	listOfAllVarUsesForVar.resizeInc = 100;
	        	
	        	if (listOfAllVarUsesForFunc==null) listOfAllVarUsesForFunc = new HighArray<FindVarUseParams>(50);
	        	listOfAllVarUsesForFunc.resizeInc = 50;
    		}
    	}
    	
        
        FindClassParams(Compiler compiler) {
        	//super(compiler, -1, -1);
        	this.compiler = compiler;
			this.startIndex = null;
			this.endIndex = null;
			
        	this.accessModifier = new AccessModifier(compiler, -1, -1);
        	this.childClasses = new ArrayListIReset(10);
        	listOfFunctionParams.resizeInc = 20;
        	listOfVariableParams.resizeInc = 50;
        }
        
        FindClassParams(Compiler compiler, int startIndex,	int endIndex) {
        	//super(this, compiler, startIndex, endIndex);
        	this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			
        	this.accessModifier = new AccessModifier(compiler, -1, -1);
        	this.childClasses = new ArrayListIReset(10);
        	
        	listOfFunctionParams.resizeInc = 20;
        	listOfVariableParams.resizeInc = 50;
        }
        
        public String toString() {
        	ArrayListChar r = new ArrayListChar(100);
			if (this.accessModifier!=null) {
				r.add(accessModifier.toString());
			}
        	
        	if (isEnum) {
        		r.add("enum ");
        		r.add(this.name);
        	}
        	else if (isInterface) {
        		r.add("interface ");
        		r.add(this.name);
        	}
        	else {
        		r.add("class ");
        		r.add(this.name);
        	}
        	
        	if (this.classNameToExtend!=null) {
        		r.add(" extends ");
        		r.add(this.classNameToExtend);
        	}
        	
        	if (this.interfaceNamesToImplement!=null && this.interfaceNamesToImplement.count>0) {
        		int i;
        		r.add(" implements ");
        		r.add(this.interfaceNamesToImplement.getItem(0)); // 맨 처음 이름
        		for (i=1; i<this.interfaceNamesToImplement.count; i++) { // 두번째부터 마지막까지
        			r.add(", ");
        			r.add(this.interfaceNamesToImplement.getItem(i));        			
        		}
        	}
        	return new String(r.getItems());
        }
        
        boolean hasChildClass(String fullName) {
        	if (fullName==null) return false;
        	int i;
        	for (i=0; i<this.childClasses.count; i++) {
        		FindClassParams c = (FindClassParams) childClasses.getItem(i);
        		if (c.name!=null && c.name.equals(fullName)) return true;
        	}
    		return false;
        }
        
        /** listOfVariableParams에서 var와 동일한 변수를 갖고있는지 확인한다.*/
        boolean hasVar(FindVarParams var) {
        	int i;
        	for (i=0; i<listOfVariableParams.count; i++) {
        		FindVarParams v = (FindVarParams) listOfVariableParams.getItem(i);
        		if (v.equals(var)) return true;
        	}
        	return false;
        }
        
        /** listOfFunctionParams에서 func 와 동일한 함수를 갖고있는지 확인한다.
         * func 와 동일한 함수를 리턴한다.*/
        FindFunctionParams hasFunc(FindFunctionParams func) {
        	int i;
        	for (i=0; i<listOfFunctionParams.count; i++) {
        		FindFunctionParams f = (FindFunctionParams) listOfFunctionParams.getItem(i);
        		try {
            		if (f.equals(func)) 
            			return f;
            		}catch(Exception e) {
            			e.printStackTrace();
            		}
        	}
        	return null;
        }
        
        /** listOfVarParamsInherited에서 var 를 이미 상속했는지 확인한다.*/
        boolean hasVarInheritted(FindVarParams var) {
        	int i;
        	for (i=0; i<listOfVarParamsInherited.count; i++) {
        		FindVarParams v = (FindVarParams) listOfVarParamsInherited.getItem(i);
        		if (v.equals(var)) return true;
        	}
        	return false;
        }
        /** listOfVarParamsInherited에서 fieldName 를 가진 변수를 이미 상속했는지 확인한다.*/
        boolean hasVarInheritted(String fieldName) {
        	int i;
        	for (i=0; i<listOfVarParamsInherited.count; i++) {
        		FindVarParams v = (FindVarParams) listOfVarParamsInherited.getItem(i);
        		if (v.fieldName==null) {
        			continue;
        		}
        		if (v.fieldName.equals(fieldName)) return true;
        	}
        	return false;
        }
        /** listOfFunctionParamsInherited에서 func 를 이미 상속했는지 확인한다.*/
        boolean hasFuncInheritted(FindFunctionParams func) {
        	int i;
        	for (i=0; i<listOfFunctionParamsInherited.count; i++) {
        		FindFunctionParams f = (FindFunctionParams) listOfFunctionParamsInherited.getItem(i);
        		try {
        		if (f.equals(func)) 
        			return true;
        		}catch(Exception e) {
        			e.printStackTrace();
        		}
        	}
        	return false;
        }
        
        /** 모든 메모리 자원들을 해제한다.
         * 클래스의 메모리가 해제될때 compiler(자바문서파일을 표현)가 사용하는 메모리도 모두 해제된다.*/
        public void destroy() {
        	if (this.disposed) return;
        	this.disposed = true;
        	
        	if (this.listOfAllVarUses!=null) {
        		this.listOfAllVarUses.destroy();
        		this.listOfAllVarUses = null;
        	}        	
        	if (this.listOfAllVarUsesForFunc!=null) {
        		this.listOfAllVarUsesForFunc.destroy();
        		this.listOfAllVarUsesForFunc = null;
        	} 
        	if (this.listOfAllVarUsesForVar!=null) {
        		this.listOfAllVarUsesForVar.destroy();
        		this.listOfAllVarUsesForVar = null;
        	} 
        	if (this.listOfClassesToExtend!=null) {
        		this.listOfClassesToExtend.reset();
        		this.listOfClassesToExtend = null;
        	} 
        	if (this.listOfControlBlocks!=null) {
        		this.listOfControlBlocks.reset();
        		this.listOfControlBlocks = null;
        	}
        	/*if (this.staticConstructorThatCompilerMakes!=null) {
        		this.staticConstructorThatCompilerMakes.destroy();
        		this.staticConstructorThatCompilerMakes = null;
        	}*/
        	if (this.compiler!=null) {
        		this.compiler.destroy();
        		this.compiler = null;
        	}
        	
        }
        
        public void changeToTemplate(String typeNameInTemplatePair) {
        	int i;
        	for (i=0; i<this.listOfVariableParams.count; i++) {
        		FindVarParams var = (FindVarParams) listOfVariableParams.getItem(i);
        		if (var.typeName!=null && var.typeName.equals("java.lang.Object")) {
        			var.typeName = typeNameInTemplatePair;
        		}
        	}
        	
        	for (i=0; i<this.listOfFunctionParams.count; i++) {
        		FindFunctionParams func = (FindFunctionParams) listOfFunctionParams.getItem(i);
        		if (func.returnType!=null && func.returnType.equals("java.lang.Object")) {
        			func.returnType = typeNameInTemplatePair;
        		}
        		int j;
        		for (j=0; j<func.listOfFuncArgs.count; j++) {
        			FindVarParams var = (FindVarParams) func.listOfFuncArgs.getItem(j);
            		if (var.typeName!=null && var.typeName.equals("java.lang.Object")) {
            			var.typeName = typeNameInTemplatePair;
            		}
        		}
        	}
        }
        
        /** parent 와 child 관계가  is a 관계인지를 조사한다. 상속관계, Implement관계 모두 조사
         * parent는 OnTouchListener이고, child는 EditText_Compiler일 경우
         * child의 상속클래스를 찾으면서 클래스 이름이 같거나 구현 인터페이스의 이름이 같은지를 확인한다.*/
        static boolean isARelation(Compiler compiler, FindClassParams parent, FindClassParams child) {
                	
        	if (child==null || parent==null) return false;
        	
        	if (child.name.equals(parent.name)) return true;
        	if (parent.name.equals("java.lang.Object")) return true;
        	
        	//FindClassParams backupChild = child;
        	
        	// parent는 OnTouchListener이고, child는 EditText_Compiler일 경우
        	// child의 상속클래스를 찾으면서 클래스 이름이 같거나 구현 인터페이스의 이름이 같은지를 확인한다.
        	while (true) {
        		if (child.name.equals("java.lang.Object")) break;
        		
    			// 클래스 이름이 같은지를 확인한다.
    			if (child.name.equals(parent.name)) return true;
    			
    			// 그 클래스가 인터페이스를 구현하고 있는지를 확인한다.
    			if (child.interfaceNamesToImplement!=null) {            	
	            	int i;
	            	for (i=0; i<child.interfaceNamesToImplement.count; i++) {
	            		String interfaceName = child.interfaceNamesToImplement.getItem(i);
	            		if (interfaceName.equals(parent.name)) return true;
	            	}
    			}
    			
    			if (child.classToExtend==null) {
    				child.classToExtend = CompilerHelper.loadClass(compiler, child.classNameToExtend);
    			}
    			child = child.classToExtend;
    			if (child==null) return false;
        	}
        	
        	        	
        	return false;
        }

		public int indexOfExtends() {
			// TODO Auto-generated method stub
			if (indexOfExtends==null) return -1;
			return indexOfExtends.index();
		}
		
		public int indexOfImplements() {
			// TODO Auto-generated method stub
			if (indexOfImplements==null) return -1;
			return indexOfImplements.index();
		}
		
		public int startIndexOfClassNameToExtend() {
			// TODO Auto-generated method stub
			if (startIndexOfClassNameToExtend==null) return -1;
			return startIndexOfClassNameToExtend.index();
		}
		
		public int endIndexOfClassNameToExtend() {
			// TODO Auto-generated method stub
			if (endIndexOfClassNameToExtend==null) return -1;
			return endIndexOfClassNameToExtend.index();
		}
	}
	
	
	public static class FindFunctionParams  extends Block  implements IReset {
		/**함수가 인터페이스 메서드 선언이면 true, 일반 메서드이면 false, 
		 * 예를들어 interface OnTouchListener의 boolean OnTouch();는 true*/
		boolean isInterfaceMethod;
		
		/**FindClassesFromTypeDecls에서 returnType(fullname)이 정의되고
		 * (자신이 파일내에 정의하는 클래스를 찾기위하여), 생성자의 경우는 findFunction에서 정의된다.
		 * fieldName은 FindAllClassesAndItsMembers2_sub에서 정해진다. 
		 * 외부클래스는 loadClass에서 정해진다.*/
		String name;
		/**FindClassesFromTypeDecls에서 returnType(fullname)이 정의되고
		 * (자신이 파일내에 정의하는 클래스를 찾기위하여), 생성자의 경우는 findFunction에서 정의된다.
		 * fieldName은 FindAllClassesAndItsMembers2_sub에서 정해진다.  
		 * 외부클래스는 loadClass에서 정해진다.*/
		String returnType;
		/**외부라이브러리면 필요*/
		//String[] listOfParamTypes;
		DocuComment docuComment;
		AccessModifier accessModifier;
		
		/** 소스상에서 함수 이름의 인덱스를 말한다. static 초기화블록의 경우는 static 의 인덱스이다.*/
        IndexForHighArray functionNameIndex=null;
        
        IndexForHighArray returnTypeStartIndex=null;
        IndexForHighArray returnTypeEndIndex=null;
        
        IndexForHighArray indexOfLeftParenthesis=null;
        IndexForHighArray indexOfRightParenthesis=null;
        
        /** 리턴 타입의 findClassParams*/
        //FindClassParams typeClassParams;
        
        /**FindVarParams[], 함수의 매개변수들, 
         * 또한 외부클래스일 경우는 주석과 fieldName이 없으나 자신이 정의한 클래스는 주석과 fieldName이 있다.*/
        ArrayListIReset listOfFuncArgs = new ArrayListIReset(10); 
		
		
		/**FindVarUseParams[]*/
        HighArray<FindVarUseParams> listOfAllVarUses/* = new ArrayListIReset(50)*/;
    	
    	 /**FindVarUseParams[]*/
    	HighArray<FindVarUseParams> listOfAllVarUsesForVar/* = new ArrayListIReset(30)*/;
    	
    	 /**FindVarUseParams[]*/
    	HighArray<FindVarUseParams> listOfAllVarUsesForFunc/* = new ArrayListIReset(10)*/;
		
        
        ArrayListString exceptionNamesToThrow;
		public boolean isConstructor;
		
		/** static 초기화 블럭인지 유무, static 블럭은 리턴타입, 함수이름, 인자 등이 없다.*/
		public boolean isStaticBlock;
		
		//Compiler compiler;
		/** 리턴타입이 템플릿일 경우 null 이 아님*/
		public Template template;
		
		/** 바이트코드에서 클래스파일을 읽을때 null 이 아님*/
		public Method_Info method_Info;
		
		/** 바이트코드를 생성할때
		 *  static 필드들을 초기화하기 위해 컴파일러가 새로 만든 생성자이면 true, 
		 *  그렇지 않으면 false */
		boolean isConstructorThatInitializesStaticFields = false;
		
		
		
		/** 이 메서드가 상속클래스의 메서드를 override 할 경우 
		 * 상속클래스의 override 된 virtual 메서드,
		 * 다시말해서 상속클래스의 메서드는 virtual 메서드가 되고 
		 * 자식클래스의 메서드는 override 메서드가 된다.*/
		public FindFunctionParams overridedFindFunctionParams;
		
		
		/** 배열초기화에서 short 범위를 넘어서는 인덱스를 사용하기 위한 지역변수들의 리스트*/ 
		ArrayListIReset listOfLocalVarsForArrayInit = new ArrayListIReset(5);
		
		/** 배열초기화에서 short 범위를 넘어서는 array length를 사용하기 위한 지역변수들의 리스트*/ 
		ArrayListIReset listOfLocalVarsForArrayInit_arrayLength = new ArrayListIReset(5);
		
		int returnTypeStartIndex() {
			if (returnTypeStartIndex==null) return -1;
			return returnTypeStartIndex.index();
		}
		int returnTypeEndIndex() {
			if (returnTypeEndIndex==null) return -1;
			return returnTypeEndIndex.index();
		}
		int indexOfLeftParenthesis() {
			if (indexOfLeftParenthesis==null) return -1;
			return indexOfLeftParenthesis.index();
		}
		int indexOfRightParenthesis() {
			if (indexOfRightParenthesis==null) return -1;
			return indexOfRightParenthesis.index();
		}
      
        public boolean hasReturnType() {
        	if (returnTypeStartIndex()!=-1 && returnTypeEndIndex()!=-1) return true;
        	//if (this.returnType!=null) return true;
        	return false;
        }
        
        /** 호출시 returnType이 null이 아니면 그것을 리턴, null이면 소스를 뒤져서 returnType을 설정하고 리턴*/
        String getReturnType(HighArray_CodeString src, int returnTypeStartIndex, int returnTypeEndIndex) {
        	if (returnType!=null) return returnType;
        	else {
	       
        		returnType = compiler.getFullNameType(compiler, returnTypeStartIndex, returnTypeEndIndex);
	        	//if (returnType==null) returnType = "";
	        	return returnType;
        	}
        }
        
        /**외부라이브러리면 필요*/
        FindFunctionParams(Compiler compiler, String funcName) {
        	//super(compiler, -1, -1);
        	this.compiler = compiler;
			this.startIndex = null;
			this.endIndex = null;
			
        	this.name = funcName;
        	
        }
        
        int functionNameIndex() {
        	if (functionNameIndex==null) return -1;
        	return this.functionNameIndex.index();
        }
        
        /** 함수를 찾았을때 호출, FindAllClassesAndItsMembers2_sub() 참조*/
        void allocateListOfVarUses(ModeAllOrUpdate modeAllOrUpdate) {
        	if (modeAllOrUpdate==ModeAllOrUpdate.All) {
	    		listOfAllVarUses = new HighArray<FindVarUseParams>(50);
	        
	        	listOfAllVarUsesForVar = new HighArray<FindVarUseParams>(30);
	        	
	        	listOfAllVarUsesForFunc = new HighArray<FindVarUseParams>(10);
        	}
    	}
        
        FindFunctionParams(Compiler compiler, int startIndex, int endIndex) {
        	//super(compiler, startIndex, endIndex);
        	this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			
        	this.compiler = compiler;
        	this.accessModifier = new AccessModifier(compiler, -1, -1);
        	this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
        	this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
        	//this.findBlockParams = new FindBlockParams(-1, -1);
        	listOfFuncArgs.resizeInc = 10;
        	listOfVariableParams.resizeInc = 100;
        	
        }
        
        public String toString() {
        	ArrayListChar r = new ArrayListChar(100);
        	
        	String message = "";
			if (this.accessModifier!=null) {
				message = accessModifier.toString();
			}
			r.add(message);
			
			String funcName;
			funcName = this.name;
			String returnTypeOfFunc = "";
			if ((this.isConstructor || this.isStaticBlock)==false)
				returnTypeOfFunc = this.returnType + " ";
			r.add(returnTypeOfFunc);
			
			r.add(funcName);
			
			int k;
			r.add("(");
			for (k=0; k<this.listOfFuncArgs.count; k++) {
				FindVarParams arg = (FindVarParams)this.listOfFuncArgs.getItem(k);
				String typeName = arg.typeName;
				r.add(typeName);
				if (arg.fieldName!=null) {
					r.add(" "+arg.fieldName);
				}
				if (k!=this.listOfFuncArgs.count-1) {
					r.add(", ");
				}
			}
			r.add(")");
			
			
			//return message + returnTypeOfFunc + funcName + "(" + args + ")";
			return new String(r.getItems());
    	}
        
        boolean hasEqualItemOfFuncArgs(FindVarParams item) {
        	int i;
        	FindVarParams var;
        	for (i=0; i<listOfFuncArgs.count; i++) {
        		var = (FindVarParams)listOfFuncArgs.getItem(i);
        		if (var.equals(item)) return true;
        	}
        	return false;
        }
        
        /** 함수 이름이 일치하고 파라미터 하나씩 full name으로 타입이름을 확인한다.
         * @param funcName : varUseName*/
        boolean equals(FindFunctionParams func) {
        	try {
        		String funcName = func.name;
        		ArrayListIReset listOfFuncArgsOfFunc = func.listOfFuncArgs;
        		
	        	if (funcName==null) return false;
	        	//if (listOfFuncCallParams==null) return false;
	        	
	        	String ownFuncName;
	        	ownFuncName = this.name;
	        	try {
	        	if (ownFuncName.equals(funcName)==false) return false;
	        	}catch(Exception e) {
	        		int a;
	        		a=0;
	        		a++;
	        		e.printStackTrace();
	        		
	        	}
	        	
	        	if (funcName.equals("command")) {
	        		int a;
	        		a=0;
	        		a++;
	        	}
	        	
	        	FindVarParams var;
	        	FindVarParams varOfFunc;
	        	int i;
	        	String typeName1 = null; // 함수선언 파라미터의 타입
	        	
	        		
	        	// 파라미터 개수가 일치하지 않으면 타입검사를 안함
	        	if (this.listOfFuncArgs.count!=listOfFuncArgsOfFunc.count) return false;
	        	
        	
	        	for (i=0; i<listOfFuncArgs.count; i++) {
	        		var = (FindVarParams)listOfFuncArgs.getItem(i);
	        		
	        		try{
	        		//typeName1 = var.getType(src, var.typeStartIndex, var.typeEndIndex);
	        			typeName1 = var.getType(this.compiler.mBuffer, var.typeStartIndex(), var.typeEndIndex());
	        		}catch(Exception e) {
	        			Log.e(e.getMessage(), "func name:"+ownFuncName+", var name:"+var.fieldName);
	        		}
	        		varOfFunc = (FindVarParams)listOfFuncArgsOfFunc.getItem(i);
	        		if (var.fieldName==null || varOfFunc.fieldName==null) {
	        			// 어떤 클래스를 바이트코드에서 읽으면 fieldName은 null 이다.
	        			if (var.typeName.equals(varOfFunc.typeName)==false) return false;
	        		}
	        		//if (var.equals(varOfFunc)==false) return false;
	        		
	        		
	        	}//for (i=0; i<listOfFuncArgs.count; i++) {
	        	
	        	if (this.isConstructor && func.isConstructor) {
	        		if (this.accessModifier!=null && func.accessModifier!=null) {
		        		if (this.accessModifier.isStatic!=func.accessModifier.isStatic)
		        			return false;
	        		}
	        	}
	        	
	        	return true;
        	}catch(Exception e) {
        		e.printStackTrace();
        		CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
        		return false;
        	}
        	
        }
        
        /** 함수 이름이 일치하고 파라미터 하나씩 full name으로 타입이름을 확인하고 또한 is a 관계인지를 확인한다.
         * @param funcName : varUseName
         * @param wayOfCheck : 0이면 타입이름이 완전히 일치해야만 true, 1이면 호환성이 있으면 true*/
        boolean equals(HighArray_CodeString src, String funcName, ArrayListIReset listOfFuncCallParams, FindVarUseParams varUse) {
        	try {
	        	if (funcName==null) return false;
	        	
	        	if (varUse.index()==865) {
	        		int a;
	        		a=0;
	        		a++;
	        	}
	        	
	        	
	        	if (listOfFuncCallParams.count>0) {
	        		FindFuncCallParam funcCall = (FindFuncCallParam) listOfFuncCallParams.getItem(0);
	        		if (funcCall.startIndex()==1814) {
	        			int a;
	        			a=0;
	        			a++;
	        		}
	        	}
	        	
	        	String ownFuncName;
	        	ownFuncName = this.name;
	        	if (ownFuncName.equals(funcName)==false) return false;
	        	
	        	if (funcName.equals("darkerOrLighter")) {
	        		int a;
	        		a=0;
	        		a++;
	        	}
	        	
	        	FindVarParams var;
	        	FindFuncCallParam funcCallParam;
	        	int i;
	        	String typeName1 = null; // 함수선언 파라미터의 타입
	        	String typeName2;		// 함수호출 파라미터의 타입
	        	
	        	
	        	if (listOfFuncArgs.count==1) {
	        		var = (FindVarParams)listOfFuncArgs.getItem(0);
	        		typeName1 = var.getType(this.compiler.mBuffer, var.typeStartIndex(), var.typeEndIndex());
	        		if (typeName1.equals("java.lang.String[]")) {
	        			// ProcessBuilder의 command()함수는 스트링 여러개를 인자로 받는 
	        			// java.util.List와 String[]를 파라미터로 갖고있다.
	        			// 여기서는 String[]와 일치하는지 확인한다.
	        			boolean isEqual = true;
	        			for (i=0; i<listOfFuncCallParams.count; i++) {
	        				funcCallParam = (FindFuncCallParam)listOfFuncCallParams.getItem(i);
	        				if (funcCallParam.typeFullName==null) return false;
	    	        		typeName2 = funcCallParam.typeFullName.str;
	    	        		String value = funcCallParam.typeFullName.value;
		        			boolean isFullnameTargetConstant = false;
	    	        		if (value!=null) {
	    	        			isFullnameTargetConstant = true;
	    	        		}
	    	        		// String과 호환되는지 확인한다.
	    	        		boolean isCompatible = TypeCast.isCompatibleType(compiler, new CodeStringEx("java.lang.String"), new CodeStringEx(typeName2), 2, null, isFullnameTargetConstant);
		        			if (isCompatible==false) {
		        				isEqual = false;
		        				break;
		        			}
	        			}
	        			if (isEqual) return true;
	        		}
	        	}
	        		
	        	// 파라미터 개수가 일치하지 않으면 타입검사를 안함
	        	if (this.listOfFuncArgs.count!=listOfFuncCallParams.count) return false;
	        	
	        	
	        	
        	
	        	for (i=0; i<listOfFuncArgs.count; i++) {
	        		var = (FindVarParams)listOfFuncArgs.getItem(i);
	        		
	        		try{
	        		//typeName1 = var.getType(src, var.typeStartIndex, var.typeEndIndex);
	        			typeName1 = var.getType(this.compiler.mBuffer, var.typeStartIndex(), var.typeEndIndex());
	        		}catch(Exception e) {
	        			Log.e(e.getMessage(), "func name:"+ownFuncName+", var name:"+var.fieldName);
	        		}
	        		funcCallParam = (FindFuncCallParam)listOfFuncCallParams.getItem(i);
	        		if (funcCallParam==null || funcCallParam.typeFullName==null)
	        			typeName2 = null;
	        		else typeName2 = funcCallParam.typeFullName.str;
	        		
	        		if (typeName2==null) return false;
	        		
	        		// 함수호출 파라미터가 null인 경우 타입검사를 안함, menu.open(null, false);
	        		if (typeName2.equals("null")) {
	        			int a;
	        			a=0;
	        			a++;
	        			continue;
	        		}
	        		
	        		if (typeName1==null) {
	        			int a;
	        			a=0;
	        			a++;
	        		}
	        		String value = funcCallParam.typeFullName.value;
        			boolean isFullnameTargetConstant = false;
	        		if (value!=null) {
	        			// 상수들의 계산된 값이면 타입 이름이 일치하는 경우뿐만 아니라
	        			// 호환되는 타입일 경우도 확인한다.
	        			isFullnameTargetConstant = true;
	        			boolean isCompatible = TypeCast.isCompatibleType(compiler, new CodeStringEx(typeName1), new CodeStringEx(typeName2), 2, funcCallParam, isFullnameTargetConstant);
	        			if (isCompatible==false) return false;
	        		}
	        		else {
	        			/*if (CompilerHelper.IsDefaultType(typeName2)==false) {
	        				isFullnameTargetConstant = false;
    	        			boolean isCompatible = TypeCast.isCompatibleType(compiler, new CodeStringEx(typeName1), new CodeStringEx(typeName2), 2, funcCallParam, isFullnameTargetConstant);
    	        			if (isCompatible==false) return false;
	        			}
	        			else {
    	        			// 상수들의 계산된 값이 아닌 일반적인 경우에는 타입 이름이 일치하여야만 한다.
    	        			if (typeName1.equals(typeName2)==false) return false;
	        			}*/
	        			isFullnameTargetConstant = false;
	        			boolean isCompatible = TypeCast.isCompatibleType(compiler, new CodeStringEx(typeName1), new CodeStringEx(typeName2), 2, funcCallParam, isFullnameTargetConstant);
	        			if (isCompatible==false) return false;
	        			
	        		}
	        	}//for (i=0; i<listOfFuncArgs.count; i++) {
	        	
	        	if (this.isConstructor && varUse.name.equals("super")) {
	        		AccessModifier accessModifier = ((FindFunctionParams)varUse.funcToDefineThisVarUse).accessModifier;
	        		if (this.accessModifier!=null && accessModifier!=null) {
		        		if (this.accessModifier.isStatic!=accessModifier.isStatic)
		        			return false;
	        		}
	        	}
	        	
	        	return true;
        	}catch(Exception e) {
        		e.printStackTrace();
        		CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
        		return false;
        	}
        	
        }

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.accessModifier!=null) {
				this.accessModifier.destroy();
				accessModifier = null;
			}
			if (this.docuComment!=null) {
				this.docuComment.destroy();
				docuComment = null;
			}
			if (this.exceptionNamesToThrow!=null) {
				exceptionNamesToThrow.destroy();
				exceptionNamesToThrow = null;
			}
			if (this.findBlockParams!=null) {
				this.findBlockParams.destroy();
				findBlockParams = null;
			}
			if (this.listOfControlBlocks!=null) {
				this.listOfControlBlocks.reset();
				listOfControlBlocks = null;
			}
			if (this.listOfFuncArgs!=null) {
				this.listOfFuncArgs.reset();
				listOfFuncArgs = null;
			}
			if (this.listOfAllVarUses!=null) {
				this.listOfAllVarUses.destroy();
				listOfAllVarUses = null;
			}
			if (this.listOfAllVarUsesForFunc!=null) {
				this.listOfAllVarUsesForFunc.destroy();
				listOfAllVarUsesForFunc = null;
			}
			if (this.listOfAllVarUsesForVar!=null) {
				this.listOfAllVarUsesForVar.destroy();
				listOfAllVarUsesForVar = null;
			}
			if (this.listOfSpecialBlocks!=null) {
				this.listOfSpecialBlocks.reset();
				listOfSpecialBlocks = null;
			}
			if (this.listOfStatements!=null) {
				this.listOfStatements.reset();
				listOfStatements = null;
			}
			if (this.listOfVariableParams!=null) {
				this.listOfVariableParams.reset();
				listOfVariableParams = null;
			}
			if (this.template!=null) {
				this.template.destroy();
				template = null;
			}
			if (this.method_Info!=null) {
				this.method_Info.destroy();
				method_Info = null;
			}
			this.name = null;
			this.returnType = null;
			this.parent = null;
			this.compiler = null;
		}
	}
	
	public static class Template  implements IReset {
		/** 템플릿 타입이름의 인덱스, com.gsoft.common.util.Stack'<'java.lang.String'>' s;에서 com의 인덱스이다.*/
		IndexForHighArray indexTypeName;
		String typeName;
		/** <의 인덱스*/
		IndexForHighArray indexLeftPair;
		/** >의 인덱스*/
		IndexForHighArray indexRightPair;
		/** 괄호안에 있는 바꿔야할 타입의 인덱스, com.gsoft.common.util.Stack'<'java.lang.String'>' s;에서 java의 인덱스이다.*/
		IndexForHighArray indexTypeNameToChange;
		String typeNameToChange;
		
		int indexTypeName() {
			if (indexTypeName==null) return -1;
			return indexTypeName.index();
		}
		
		int indexLeftPair() {
			if (indexLeftPair==null) return -1;
			return indexLeftPair.index();
		}
		
		int indexRightPair() {
			if (indexRightPair==null) return -1;
			return indexRightPair.index();
		}
		
		int indexTypeNameToChange() {
			if (indexTypeNameToChange==null) return -1;
			return indexTypeNameToChange.index();
		}
		
		Template child;
		
		boolean found;
		//public Template parent;
		
		Compiler compiler;
		
		/** @param indexTypeName : 템플릿 타입이름의 인덱스, 
		 * com.gsoft.common.util.Stack'<'java.lang.String'>' s;에서 com의 인덱스이다.
		* @param indexLeftPair : <의 인덱스
		* @param indexRightPair : >의 인덱스
		* @param indexTypeNameToChange : 괄호안에 있는 바꿔야할 타입의 인덱스, 
		* com.gsoft.common.util.Stack'<'java.lang.String'>' s;에서 java의 인덱스이다.*/
		Template(Compiler compiler, int indexTypeName, int indexLeftPair, 
				int indexRightPair, int indexTypeNameToChange) {
			this.compiler = compiler;
			this.indexTypeName = IndexForHighArray.indexRelative(this, compiler.mBuffer, indexTypeName);
			this.indexLeftPair = IndexForHighArray.indexRelative(this, compiler.mBuffer, indexLeftPair);
			this.indexRightPair = IndexForHighArray.indexRelative(this, compiler.mBuffer, indexRightPair);
			this.indexTypeNameToChange = IndexForHighArray.indexRelative(this, compiler.mBuffer, indexTypeNameToChange);
		}
		
		Template(Compiler compiler) {
			this.compiler = compiler;
			this.indexTypeName = null;
			this.indexLeftPair = null;
			this.indexRightPair = null;
			this.indexTypeNameToChange = null;
		}
		
		void copy(Template template) {
			this.indexTypeName = template.indexTypeName;
			this.indexLeftPair = template.indexLeftPair;
			this.indexRightPair = template.indexRightPair;
			this.indexTypeNameToChange = template.indexTypeNameToChange;
			this.child = template.child;
			this.typeName = template.typeName;
			this.typeNameToChange = template.typeNameToChange;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.child!=null) {
				this.child.destroy();
				this.child = null;
			}
			if (this.typeName!=null) this.typeName = null;
			if (this.typeNameToChange!=null) this.typeNameToChange = null;
		}
		
		
	}
	
	/** 변수선언*/
	public static class FindVarParams extends FindStatementParams  implements IReset {
		/**FindClassesFromTypeDecls에서 typeName(fullname)이 정의되고
		 * (자신이 파일내에 정의하는 클래스를 찾기위하여) 
		 * fieldName은 FindAllClassesAndItsMembers2_sub에서 정해진다. 
		 * 외부클래스는 loadClass에서 정해진다. */
		String typeName;
		/**FindClassesFromTypeDecls에서 typeName(fullname)이 정의되고
		 * (자신이 파일내에 정의하는 클래스를 찾기위하여) 
		 * fieldName은 FindAllClassesAndItsMembers2_sub에서 정해진다. 
		 * enum 변수일 경우는 FindAllClassesAndItsMembers2_sub의 varUse를 다룰때 정의된다. 
		 * 또한 외부클래스일 경우는 fieldName이 없으나 자신이 정의한 클래스는 fieldName이 있다. 
		 * 외부클래스는 loadClass에서 정해진다.*/
		String fieldName;
		
		DocuComment docuComment;
		AccessModifier accessModifier;
		//int startIndex = -1;
		//int endIndex = -1;
        //boolean found;
        
       
        
		IndexForHighArray typeStartIndex=null;
		IndexForHighArray typeEndIndex=null;
        /** 변수 타입의 findClassParams*/
        //FindClassParams typeClassParams;
        
        IndexForHighArray varNameIndex=null;
        Object parent = null;
        HighArray<FindVarUseParams> listOfVarUses = new HighArray<FindVarUseParams>(10); // class 또는 함수 내에서 변수의 사용
        
        boolean isMemberOrLocal;
        /** 변수선언시부터*/
        IndexForHighArray startIndexOfScope = null;
        /** 변수가 선언된 블록의 '}' 바로 전까지*/
        IndexForHighArray endIndexOfScope = null;
        
        int startIndexOfScope() {
        	if (startIndexOfScope==null) return -1;
        	return startIndexOfScope.index();
        }
        
        int endIndexOfScope() {
        	if (endIndexOfScope==null) return -1;
        	return endIndexOfScope.index();
        }
        
        boolean isThis;
        //boolean isClass;
        boolean isSuper;
        /** class stack {} 에 붙는 화살표안 괄호안의 템플릿을 말한다.*/
        Template templateOfClass;
        
        /** enum 변수일 경우 true*/
        boolean isEnumElement;
        
        /** 타입이 템플릿이면 널이 아님*/
        Template template;
        
        //Compiler compiler;
        
        /** 바이트코드에서 클래스파일을 읽을때 null 이 아님*/
		public ByteCode_Types.Field_Info field_Info;
		
		
        
        /** full name으로 리턴, 
         * 호출시 typeName이 null이 아니면 그것을 리턴, null이면 소스를 뒤져서 typeName을 설정하고 리턴*/
        String getType(HighArray_CodeString src, int typeStartIndex, int typeEndIndex) {
        	if (this.typeName!=null) {        		
        		return typeName;
        	}
        	else {
        		//if (typeStartIndex==-1 || typeEndIndex==-1) return "";
        		//this.typeName = compiler.getFullNameType(src, typeStartIndex, typeEndIndex);
        		this.typeName = compiler.getFullNameType(compiler, typeStartIndex, typeEndIndex);
        		return typeName;
        	}
        }
        
        public int typeStartIndex() {
        	if (typeStartIndex==null) return -1;
        	return this.typeStartIndex.index();
		}
        
        public int typeEndIndex() {
        	if (typeEndIndex==null) return -1;
        	return this.typeEndIndex.index();
		}

		int varNameIndex() {
        	if (varNameIndex==null) return -1;
        	return this.varNameIndex.index();
        }
        
        /** class Stack {}에서 클래스가 템플릿일 경우 VarUse들을 쉽게 찾기 위해 가상으로 만든 타입선언이다.
         * fieldName은 templateOfClass.typeNameToChange이 된다.*/
        FindVarParams(Compiler compiler, Template templateOfClass, FindClassParams parent) {
        	//super(compiler, -1, -1);
        	this.compiler = compiler;
			this.startIndex = null;
			this.endIndex = null;
			
        	this.templateOfClass = templateOfClass;
        	this.fieldName = templateOfClass.typeNameToChange;
        	this.parent = parent;
        }
        
        /**외부라이브러리일때 필요, 함수인자일경우 fieldName에 null을 넣어준다.*/
        FindVarParams(Compiler compiler, String typename, String fieldName) {
        	//super(compiler, -1, -1);
        	this.compiler = compiler;
			this.startIndex = null;
			this.endIndex = null;
			
        	this.typeName = typename;
        	this.fieldName = fieldName;
        }
        
        FindVarParams(Compiler compiler, int startIndex, int endIndex) {
        	//super(compiler, startIndex, endIndex);
        	this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			
        	accessModifier = null;
        }
        
        FindVarParams(Compiler compiler, boolean isThis, boolean isClass, boolean isSuper, FindClassParams parentClassParams) {
        	//super(compiler, -1, -1);
        	this.compiler = compiler;
			this.startIndex = null;
			this.endIndex = null;
			
        	this.isThis = isThis;
        	//this.isClass = isClass;
        	this.isSuper = isSuper;
        	this.parent = parentClassParams;
        	this.isMemberOrLocal = true;
        	accessModifier = null;
        	listOfVarUses.resizeInc = 30;
        	this.typeName = parentClassParams.name;
        	if (isSuper) {
        		this.typeName = parentClassParams.classNameToExtend;
        	}
        	
        	if (isThis) this.fieldName = "this";
        	if (isSuper) this.fieldName = "super";
        	if (isClass) {
        		this.fieldName = compiler.getShortName(parentClassParams.name);
        	}
        	
        }
        
        /** EnumElement를 만들때 호출*/
        FindVarParams(Compiler compiler, int varNameIndex, boolean isEnumElement, FindClassParams parentEnumParams) {
        	//super(compiler, -1, -1);
        	this.compiler = compiler;
			this.startIndex = null;
			this.endIndex = null;
			
        	this.isEnumElement = isEnumElement;
        	this.varNameIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, varNameIndex);
        	this.parent = parentEnumParams;
        	this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, varNameIndex);
        	this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, varNameIndex);
        	accessModifier = null;
        	listOfVarUses.resizeInc = 10;
        }
        
        
    	public String toString() {
    		ArrayListChar r = new ArrayListChar(50);
    		String message = "";
    		if (this.isMemberOrLocal) {
				if (this.accessModifier!=null) {
					message = accessModifier.toString();
				}
    		}
			r.add(message);
			r.add(typeName);
			r.add(" ");
			if (this.fieldName!=null) {
				r.add(this.fieldName);
			}
			else {
				r.add("null");
			}
			    		
    		//r = message + this.typeName + " " + this.fieldName;
    		return new String(r.getItems());
    	}
        
        
        boolean equals(FindVarParams var) {
        	
        	if (var.isThis || var.isSuper) return false;
        	if (this.isThis || this.isSuper) return false;
        	/*if (this.fieldName==null && var.fieldName==null) {
        		if (this.typeName.equals(var.typeName)==false) return false;
        	}
        	if (this.fieldName==null) {
        		
        	}*/
        	if (this.fieldName.equals(var.fieldName)==false) return false;
        	
        	if (this.typeName==null) {
        		int a;
        		a=0;
        		a++;
        	}
        	if (this.typeName.equals(var.typeName)==false) return false;
        	return true;
        }

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.accessModifier!=null) {
				this.accessModifier.destroy();
				this.accessModifier = null;
			}
			if (this.docuComment!=null) {
				this.docuComment.destroy();
				this.docuComment = null;
			}
			this.fieldName = null;
			
			if (this.listOfVarUses!=null) {
				this.listOfVarUses.destroy();
				this.listOfVarUses = null;
			}
			this.parent = null;
			if (this.template!=null) {
				this.template.destroy();
				this.template = null;
			}
			if (this.templateOfClass!=null) {
				this.templateOfClass.destroy();
				this.templateOfClass = null;
			}
			//this.typeClassParams = null;
			
			if (this.field_Info!=null) {
				this.field_Info.destroy();
				this.field_Info = null;
			}
		}
	}
	
	/**(타입)id 이와 같은 타입캐스트를 표현한다.*/
	public static class TypeCast  implements IReset {
		String name;
		/**(타입)id 에서 타입의 시작과 끝, 괄호 불포함*/
		IndexForHighArray startIndex;
		/**(타입)id 에서 타입의 시작과 끝, 괄호 불포함*/
		IndexForHighArray endIndex;
		
		/** 타입캐스트문의 괄호 인덱스*/
		IndexForHighArray indexOfLeftPair;
		/** 타입캐스트문의 괄호 인덱스*/
		IndexForHighArray indexOfRightPair;
		
		/**(타입)id 는 false, (타입)(수식)은 true*/
		boolean affectsExpression;
		
		/**(타입)(수식)일 경우에만 null 이 아님*/
		FindFuncCallParam funcCall;
		
		/**(타입)id 에서 id 의 시작과 끝, (타입)(수식) 에서 괄호 불포함, (타입)a.b.c 에서 a 를 말한다.*/
		IndexForHighArray startIndexToAffect;
		/**(타입)id 에서 id 의 시작과 끝, (타입)(수식) 에서 괄호 불포함, (타입)a.b.c 에서 c 를 말한다.*/
		IndexForHighArray endIndexToAffect;
		
		/** (com.gsoft.common.gui.Buttons.Button)control 여기에서 Button을 말한다.*/
		FindVarUseParams varUseTypeCasting;
		
		int startIndexToAffect() {
			if (startIndexToAffect==null) return -1;
			return startIndexToAffect.index();
		}
		
		int endIndexToAffect() {
			if (endIndexToAffect==null) return -1;
			return endIndexToAffect.index();
		}
		
		int indexOfLeftPair() {
			if (indexOfLeftPair==null) return -1;
			return indexOfLeftPair.index();
		}
		
		int indexOfRightPair() {
			if (indexOfRightPair==null) return -1;
			return indexOfRightPair.index();
		}
		
		/**(타입)id 에서 id 의 시작과 끝, (타입)(수식) 에서 id의 mlistOfAllVarUses에서의 시작 인덱스*/
		int startIndexToAffect_mlistOfAllVarUses;
		/**(타입)id 에서 id 의 시작과 끝, (타입)(수식) 에서 id의 mlistOfAllVarUses에서의 끝 인덱스*/
		int endIndexToAffect_mlistOfAllVarUses;
		/** 타입캐스트가 템플릿일 경우 null이 아님*/
		public Template template;
		
		Compiler compiler;
		
		
		/** 매개변수 각각은 괄호 불포함*/
		TypeCast(Compiler compiler, int startIndex, int endIndex, int startIndexToAffect, int endIndexToAffect) {
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			this.startIndexToAffect = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndexToAffect);
			this.endIndexToAffect = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndexToAffect);
			
			
		}
		
		/** (fullnameTypeCast)fullnameTarget, fullnameTypeCast = fullnameTarget; 
		 * @param isTypeCastOrAssignOrFuncCall : 타입캐스트할경우는 0, 할당시일경우는 1, 함수호출시는 2, 
		 * 2016-07-20 업데이트로 함수호출시에도 할당시와 같은 규칙을 갖는다.*/
		public static boolean isCompatibleType(Compiler compiler, 
				CodeStringEx fullnameTypeCast, CodeStringEx fullnameTarget,
				int isTypeCastOrAssignOrFuncCall,  
				FindFuncCallParam expression) {
			return isCompatibleType(compiler, 
					fullnameTypeCast, fullnameTarget,
					isTypeCastOrAssignOrFuncCall,  
					expression, false);
		}
		
		/** (fullnameTypeCast)fullnameTarget, fullnameTypeCast = fullnameTarget; 
		 * @param isTypeCastOrAssignOrFuncCall : 타입캐스트할경우는 0, 할당시일경우는 1, 함수호출시는 2, 
		 * 2016-07-20 업데이트로 함수호출시에도 할당시와 같은 규칙을 갖는다.*/
		public static boolean isCompatibleType(Compiler compiler, 
				CodeStringEx fullnameTypeCast, CodeStringEx fullnameTarget,
				int isTypeCastOrAssignOrFuncCall,  
				FindFuncCallParam expression, boolean isFullnameTargetConstant) {
			if (fullnameTypeCast.equals("android.view.View") && fullnameTarget.equals("null")) {
				int a;
				a=0;
				a++;
			}
			//if (isTypeCastOrAssignOrFuncCall==2) isTypeCastOrAssignOrFuncCall = 1;
			
			if (fullnameTypeCast==null || fullnameTarget==null) return false;
			if (fullnameTarget.equals("")) return false;
			if (fullnameTypeCast.equals(fullnameTarget)) return true;
			if (fullnameTypeCast.equals("java.lang.Object")) {
				if (CompilerHelper.IsDefaultType(fullnameTarget.str)) return false;
				return true;
			}
			
			if (fullnameTarget.equals("byte")) {
				if (fullnameTypeCast.equals("byte")){
					return true;
				}
				// i = c; (int)c
				else if (fullnameTypeCast.equals("char")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					// char c = b; 가능
					else if (isTypeCastOrAssignOrFuncCall==1) return true;
					else return true;
				}
				else if (fullnameTypeCast.equals("int") || fullnameTypeCast.equals("short") ||
					fullnameTypeCast.equals("long")) 
				{
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // i = b; s = b; l = b;
					}
					else {
						// f(100); void f(int i) {} 에서 
						// 100은 byte이므로 함수 f()에 바인딩이 안 되지만 
						// 원래 100은 int여서 함수 f()에 바인딩이 되었기 때문에 허용한다.
						// 그러나 byte b = 0; f(b); 에서는 f()에 바인딩이 되지 않는다.
						if (isFullnameTargetConstant) return true;
						return true;
					}
				}
				else if (fullnameTypeCast.equals("float") || fullnameTypeCast.equals("double")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true; 
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast; 
						return true; // f = b;는 가능하다.
					}
					else return true;
				}
				else if (fullnameTypeCast.equals("java.lang.Byte")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true; 
					// java.lang.Byte B = b; 
					else if (isTypeCastOrAssignOrFuncCall==1) return true;
					// f(java.lang.Byte B) 에 f(b) 호출 가능
					else return true;
				}
				//else return false;
			}//if (fullnameTarget.equals("byte")) {
			else if (fullnameTarget.equals("char")) {
				if (fullnameTypeCast.equals("byte")){
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else return false;
				}
				// i = c; (int)c
				else if (fullnameTypeCast.equals("char")) {
					return true;
				}
				else if (fullnameTypeCast.equals("int") || fullnameTypeCast.equals("short") ||
					fullnameTypeCast.equals("long")) 
				{
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // i = c; s = c; l = c;
					}
					else return true;					 
				}
				else if (fullnameTypeCast.equals("float") || fullnameTypeCast.equals("double")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true; 
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast; 
						return true; // f = c;는 가능하다.
					}
					else return true;
				}
				else if (fullnameTypeCast.equals("java.lang.Char")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true; 
					// java.lang.Char C = c; 
					else if (isTypeCastOrAssignOrFuncCall==1) return true;
					// f(java.lang.Char B) 에 f(c) 호출 가능
					else return true;
				}
				//else return false;
			}//if (fullnameTarget.equals("char")) {
			else if (fullnameTarget.equals("int")) {
				if (fullnameTypeCast.equals("byte") || fullnameTypeCast.equals("java.lang.Byte")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else {
						//byte r2 = Byte.parseByte("255");//에러발생
						try {
							Byte.parseByte(fullnameTarget.value);							
							return true;
						}catch(Exception e) {
							return false;
						}
					}
				}
				else if (fullnameTypeCast.equals("char")) {				
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else {
						//byte r2 = Byte.parseByte("255");//에러발생
						try {
							int max = Character.MAX_CODE_POINT;
							int min = Character.MIN_CODE_POINT;
							//int target = Integer.parseInt(fullnameTarget.value);
							int target = IO.toInt(fullnameTarget.value);
							if (min<=target && target<=max)	return true;
							else return false;
						}catch(Exception e) {
							return false;
						}
					}
				}
				else if (fullnameTypeCast.equals("short")) {				
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else {
						//byte r2 = Byte.parseByte("255");//에러발생
						try {
							Short.parseShort(fullnameTarget.value);							
							return true;
						}catch(Exception e) {
							return false;
						}
					}
				}
				
				else if (fullnameTypeCast.equals("int")) {
					return true;
				}
				else if (fullnameTypeCast.equals("long")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // l = i;
					}
					else {
						//상수일 때는 허용한다.
						if (isFullnameTargetConstant) return true;
						return false;
					}
				}
				else if (fullnameTypeCast.equals("float")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // f = i;
					}
					else return true;
				}
				else if (fullnameTypeCast.equals("double")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // d = i;
					}
					else return true;
				}
				else if (fullnameTypeCast.equals("java.lang.Integer")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true; 
					// java.lang.Integer I = i; 
					else if (isTypeCastOrAssignOrFuncCall==1) return true;
					// f(java.lang.Integer B) 에 f(i) 호출 가능
					else return true;
				}
				//else return false;
			}//else if (fullnameTarget.equals("int")) {
			else if (fullnameTarget.equals("short")) {
				if (fullnameTypeCast.equals("byte")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//byte r2 = Byte.parseByte("255");//에러발생
						try {
							Byte.parseByte(fullnameTarget.value);							
							return true;
						}catch(Exception e) {
							return false;
						}
					}
					else return false;
				}
				else if (fullnameTypeCast.equals("char")){
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else {
						//byte r2 = Byte.parseByte("255");//에러발생
						try {
							int max = Character.MAX_CODE_POINT;
							int min = Character.MIN_CODE_POINT;
							//int target = Integer.parseInt(fullnameTarget.value);
							int target = IO.toInt(fullnameTarget.value);
							if (min<=target && target<=max)	return true;
							else return false;
						}catch(Exception e) {
							return false;
						}
						//char c = s; 은 가능하다.
						//return true;
						
					}
				}
				else if (fullnameTypeCast.equals("int")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // i = s;
					}
					else {
						// f(256); void f(int i) {} 에서 
						// 256은 short이므로 함수 f()에 바인딩이 안 되지만 
						// 원래 256은 int여서 함수 f()에 바인딩이 되었기 때문에 허용한다.
						// 그러나 short s = 0; f(s); 에서는 f()에 바인딩이 되지 않는다.
						if (isFullnameTargetConstant) return true;
						return true;
					}
				}
				else if (fullnameTypeCast.equals("short")) {
					return true;
				}
				else if (fullnameTypeCast.equals("long")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // l = s;
					}
					else {
						//상수일 때는 허용한다.
						if (isFullnameTargetConstant) return true;
						return true;
					}
				}
				else if (fullnameTypeCast.equals("float")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // f = s;
					}
					else return true;					
				}
				else if (fullnameTypeCast.equals("double")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // d = s;
					}
					else return true;
				}
				else if (fullnameTypeCast.equals("java.lang.Short")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true; 
					// java.lang.Short S = s; 
					else if (isTypeCastOrAssignOrFuncCall==1) return true;
					// f(java.lang.Short S) 에 f(s) 호출 가능
					else return true;
				}
				//else return false;
			}//else if (fullnameTarget.equals("short")) {
			else if (fullnameTarget.equals("long")) {
				if (fullnameTypeCast.equals("byte")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else {
						//byte r2 = Byte.parseByte("255");//에러발생
						try {
							Byte.parseByte(fullnameTarget.value);							
							return true;
						}catch(Exception e) {
							return false;
						}
					}
				}
				else if (fullnameTypeCast.equals("char")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else {
						//byte r2 = Byte.parseByte("255");//에러발생
						try {
							int max = Character.MAX_CODE_POINT;
							int min = Character.MIN_CODE_POINT;
							//int target = Integer.parseInt(fullnameTarget.value);
							int target = IO.toInt(fullnameTarget.value);
							if (min<=target && target<=max)	return true;
							else return false;
						}catch(Exception e) {
							return false;
						}
					}
				}
				else if (fullnameTypeCast.equals("int")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else {
						//byte r2 = Byte.parseByte("255");//에러발생
						try {
							Integer.parseInt(fullnameTarget.value);							
							return true;
						}catch(Exception e) {
							return false;
						}
					}
				}
				else if (fullnameTypeCast.equals("short")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else {
						//byte r2 = Byte.parseByte("255");//에러발생
						try {
							Short.parseShort(fullnameTarget.value);							
							return true;
						}catch(Exception e) {
							return false;
						}
					}
				}
				else if (fullnameTypeCast.equals("long")) {
					return true;
				}
				else if (fullnameTypeCast.equals("float")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // f = l;
					}
					else return false;
				}
				else if (fullnameTypeCast.equals("double")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // d = l;
					}
					else return false;
				}
				else if (fullnameTypeCast.equals("java.lang.Long")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true; 
					// java.lang.Long L = l; 
					else if (isTypeCastOrAssignOrFuncCall==1) return true;
					// f(java.lang.Long L) 에 f(l) 호출 가능
					else return true;
				}
				//else return false;
			} //else if (fullnameTarget.equals("long")) {
			
			else if (fullnameTarget.equals("float")) {
				if (fullnameTypeCast.equals("float")) {
					return true;					
				}
				else if (fullnameTypeCast.equals("double")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else if (isTypeCastOrAssignOrFuncCall==1) {
						//if (expression!=null) expression.typeFullNameByOperator = fullnameTypeCast;
						return true; // d = f;
					}
					else return true;
				}
				else if (fullnameTypeCast.equals("byte") || fullnameTypeCast.equals("char")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else return false;
				}
				else if (fullnameTypeCast.equals("int")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else return false;
				}
				else if (fullnameTypeCast.equals("short")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else return false;
				}
				else if (fullnameTypeCast.equals("long")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else return false;
				}
				else if (fullnameTypeCast.equals("java.lang.Float")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true; 
					// java.lang.Float F = f; 
					else if (isTypeCastOrAssignOrFuncCall==1) return true;
					// f(java.lang.Float F) 에 f(f) 호출 가능
					else return true;
				}
				//else return false;
			}//else if (fullnameTarget.equals("float")) {
			else if (fullnameTarget.equals("double")) {
				if (fullnameTypeCast.equals("float")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else return false;				
				}
				else if (fullnameTypeCast.equals("double")) {
					return true;
				}
				else if (fullnameTypeCast.equals("byte") || fullnameTypeCast.equals("char")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else return false;
				}
				else if (fullnameTypeCast.equals("int")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else return false;
				}
				else if (fullnameTypeCast.equals("short")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else return false;
				}
				else if (fullnameTypeCast.equals("long")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true;
					else return false;
				}
				else if (fullnameTypeCast.equals("java.lang.Double")) {
					if (isTypeCastOrAssignOrFuncCall==0) return true; 
					// java.lang.Double D = d; 
					else if (isTypeCastOrAssignOrFuncCall==1) return true;
					// f(java.lang.Double d) 에 f(d) 호출 가능
					else return true;
				}
				//else return false;
			}//else if (fullnameTarget.equals("double")) {
			else if (fullnameTarget.equals("java.lang.Byte")) {
				if (isTypeCastOrAssignOrFuncCall==0) return true; 
				// java.lang.Byte B; b = B; 
				else if (isTypeCastOrAssignOrFuncCall==1) return true;
				// f(byte b) 에 f(B) 호출 가능
				else return true;
			}
			else if (fullnameTarget.equals("java.lang.Char")) {
				if (isTypeCastOrAssignOrFuncCall==0) return true; 
				// java.lang.Char C; c = C; 
				else if (isTypeCastOrAssignOrFuncCall==1) return true;
				// f(char c) 에 f(C) 호출 가능
				else return true;
			}
			else if (fullnameTarget.equals("java.lang.Short")) {
				if (isTypeCastOrAssignOrFuncCall==0) return true; 
				// java.lang.Short S; s = S; 
				else if (isTypeCastOrAssignOrFuncCall==1) return true;
				// f(short s) 에 f(S) 호출 가능
				else return true;
			}
			else if (fullnameTarget.equals("java.lang.Integer")) {
				if (isTypeCastOrAssignOrFuncCall==0) return true; 
				// java.lang.Integer I; i = I; 
				else if (isTypeCastOrAssignOrFuncCall==1) return true;
				// f(int i) 에 f(I) 호출 가능
				else return true;
			}
			else if (fullnameTarget.equals("java.lang.Long")) {
				if (isTypeCastOrAssignOrFuncCall==0) return true; 
				// java.lang.Long L; l = L; 
				else if (isTypeCastOrAssignOrFuncCall==1) return true;
				// f(long l) 에 f(L) 호출 가능
				else return true;
			}
			else if (fullnameTarget.equals("java.lang.Float")) {
				if (isTypeCastOrAssignOrFuncCall==0) return true; 
				// java.lang.Float F; f = F; 
				else if (isTypeCastOrAssignOrFuncCall==1) return true;
				// f(float f) 에 f(F) 호출 가능
				else return true;
			}
			else if (fullnameTarget.equals("java.lang.Double")) {
				if (isTypeCastOrAssignOrFuncCall==0) return true; 
				// java.lang.Double D; d = D; 
				else if (isTypeCastOrAssignOrFuncCall==1) return true;
				// f(double d) 에 f(D) 호출 가능
				else return true;
			}
			
			
			// byte[] arr = null;인것처럼 배열차원이 달라도 할당문 타입검사를 피한다.
			if (fullnameTarget.equals("null")) return true;
			
			
			int dimension1 = CompilerHelper.getArrayDimension(compiler, fullnameTarget.str);
			int dimension2 = CompilerHelper.getArrayDimension(compiler, fullnameTypeCast.str);
			
			if (dimension1!=dimension2) return false;
			
			String strfullnameTarget = CompilerHelper.getArrayElementType(fullnameTarget.str);
			String strfullnameTypeCast = CompilerHelper.getArrayElementType(fullnameTypeCast.str);
			
			if (compiler.getShortName(strfullnameTarget).equals("Button") &&
					compiler.getShortName(strfullnameTypeCast).equals("Control")	) {
				int a;
				a=0;
				a++;
			}
			
			
			
			FindClassParams typeCast = compiler.getFindClassParams(Compiler.mlistOfAllClassesHashed, Compiler.mlistOfAllClasses, strfullnameTypeCast);
			if (typeCast==null) typeCast = CompilerHelper.loadClass(compiler, strfullnameTypeCast);
			
			FindClassParams	target = compiler.getFindClassParams(Compiler.mlistOfAllClassesHashed, Compiler.mlistOfAllClasses, strfullnameTarget);
			if (target==null) target = CompilerHelper.loadClass(compiler, strfullnameTarget);
			
			
			
						
			if (typeCast==null || target==null) return false;
			
			if (typeCast==null || target==null || typeCast.name==null || target.name==null) {
				int a;
				a=0;
				a++;
			}
			
			
			
			//(Control)rect이나 control=rect는 true를 리턴한다.
			//(RectForPage)c이나  rect=control는 false를 리턴한다.
			if (FindClassParams.isARelation(compiler, typeCast, target)){
				if (isTypeCastOrAssignOrFuncCall==0) { 
					return true;
				}
				else return true; // typeCast = target;
			}
			
			//(Control)rect이나 control=rect는 false를 리턴한다.
			//(RectForPage)c이나  rect=control는 true를 리턴한다.
			if (FindClassParams.isARelation(compiler, target, typeCast)){
				if (isTypeCastOrAssignOrFuncCall==0) { // typeCast = target;
					return true;
				}
				else return false;
			}
			
			return false;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.funcCall!=null) {
				this.funcCall.destroy();
				this.funcCall = null;
			}
			if (this.template!=null) {
				this.template.destroy();
				this.template = null;
			}
			this.name = null;
		}

		public int startIndex() {
			// TODO Auto-generated method stub
			if (startIndex==null) return -1;
			return startIndex.index();
		}
		
		public int endIndex() {
			// TODO Auto-generated method stub
			if (endIndex==null) return -1;
			return endIndex.index();
		}
	}
	
	/** 생성자를 표현한다.*/
	public static class Constructor implements IReset {
		/** 배열일 경우 배열기호도 포함한다.*/
		String fullname;
		/**new의 시작과 끝, new불포함, 배열이나 함수의 괄호 포함한 인덱스이다.*/
		IndexForHighArray startIndex;
		/**new의 시작과 끝, new불포함, 배열이나 함수의 괄호 포함한 인덱스이다.*/
		IndexForHighArray endIndex;
		/**new의 시작과 끝, new불포함, 배열이나 함수의 괄호 포함하지않은 인덱스이다.*/
		IndexForHighArray startIndexExceptPair;
		/**new의 시작과 끝, new불포함, 배열이나 함수의 괄호 포함하지않은 인덱스이다.*/
		IndexForHighArray endIndexExceptPair;
		IndexForHighArray indexOfNew;
		Compiler compiler;
		public int dimension;
		/** stack=new Stack<Block>();에서 template은 null이 아니다.*/
		public Template template;
		
		Constructor(Compiler compiler) {
			this.compiler = compiler;
		}
		
		public int startIndex() {
			if (startIndex==null) return -1;
			return startIndex.index();
		}
		
		public int endIndex() {
			if (endIndex==null) return -1;
			return endIndex.index();
		}
		
		public int startIndexExceptPair() {
			if (startIndexExceptPair==null) return -1;
			return startIndexExceptPair.index();
		}
		
		public int endIndexExceptPair() {
			if (endIndexExceptPair==null) return -1;
			return endIndexExceptPair.index();
		}
		
		public int indexOfNew() {
			if (indexOfNew==null) return -1;
			return indexOfNew.index();
		}
		
		/** 호출시 fullname이 null이 아니면 그것을 리턴, null이면 소스를 뒤져서 fullname을 설정하고 리턴
		 * new int[10]; 이면 int[]을 리턴한다.*/
        String getType(HighArray_CodeString src, int typeStartIndex, int typeEndIndex) {
        	if (fullname!=null) return fullname;
        	else {
	        	/*if (returnTypeStartIndex==-1 || returnTypeEndIndex==-1) {	        		
	        		return "";
	        	}*/
	        	//returnType = compiler.getFullNameType(src, returnTypeStartIndex, returnTypeEndIndex);
        		fullname = compiler.getFullNameType(compiler, typeStartIndex, typeEndIndex);
        		fullname = CompilerHelper.getArrayType(fullname, dimension);
	        	//if (returnType==null) returnType = "";
	        	return fullname;
        	}
        }

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			this.fullname = null;
			if (this.template!=null) {
				this.template.destroy();
				this.template = null;
			}
		}
	}
	
	/** 변수의 사용, 
	 * FindVarParams varDecl(varUse의 해당 변수선언), FindFunctionParams funcDecl(varUse의 해당 함수선언), 
	 * Object memberDecl(varUse의 해당 패키지등),
	 * ArrayListIReset listOfArrayElementParams(buffer[i][j]에서 varUse는 buffer이고 파라미터들은 i,j를 말한다), 
	 * ArrayListIReset listOfFuncCallParams(func(a,b,c)에서 varUse는 func이고 파라미터들은 a,b,c를 말한다),
	 * FindVarUseParams parent(멤버한정연산자(.)에 따른 parent.varUse에 parent);
	 * FindVarUseParams child(멤버한정연산자(.)에 따른 varUse.child에 child),
	 * TypeCast typeCast((int)a에서 int, 현재 varUse가 타입캐스트문일 경우 null이 아님) 등
	 * 중요한 멤버들이 있으므로 주의한다.*/
	public static class FindVarUseParams  implements IReset {
		//static int count;
		//int number;
		/** buffer[i]와 같은 경우 "buffer[i]", buffer(i)와 같은 경우는 "buffer", "i"*/
		String name;
		/**name과 다르게 원래 이름*/
		String originName;
		IndexForHighArray index = null;
		boolean isLocal;
		FindVarParams varDecl=null;
		FindFunctionParams funcDecl=null;
		
		/** this.varUse 혹은 super.varUse 이런 경우는 true, 아니면 false*/
		boolean isUsingThisOrSuper;
		
		/** buffer[i]와 같은 경우 true, 아니면 false*/
		boolean isArrayElement;
		/**FindFuncCallParam[], 처음엔 null, isArrayElement이 true일 경우에만 new*/
		ArrayListIReset listOfArrayElementParams;
		
		
		/** FindPackageParams 또는 FindClassParams, FindVarParams, FindFunctionParams*/  
		Object memberDecl;
		
		boolean isForVarOrForFunc;
		
		/** FindFuncCallParam[], 함수호출파라미터, funcDecl은 null이 아니다.*/
		ArrayListIReset listOfFuncCallParams;
		
		//boolean isThis;
		///** 개체 자신을 가리키는 포인터*/
		//FindClassParams _this;
		
		/** 멤버한정연산자(.)에 따른 parent.varUse에 parent*/
		FindVarUseParams parent;
		/** 멤버한정연산자(.)에 따른 varUse.child에 child*/
		FindVarUseParams child;
		
		boolean isEnumElement;
		
		/** (int)a에서 int, 현재 varUse가 타입캐스트문일 경우 null이 아님*/
		TypeCast typeCast;
		
		/** stack = new Stack<Block>(); 에서 Stack의 varUse는  template이 null이 아니게 된다.*/
		Template template;
		
		/** 이전 타입캐스트문에 의해 타입캐스트될 경우 fullname으로 갖는다.*/
		//FindVarUseParams typeCastedByVarUse;
		//String fullnameTypeCast;
		
		/**varUse가 lValue이면 null이 아니다.*/
		FindFuncCallParam rValue;
		
		/**varUse가 배열초기화문이면 null이 아니다.*/
		FindArrayInitializerParams arrayInitializer;
		
		/** static함수에서 nonStatic변수를 참조하는가를 쉽게 알기 위해 사용한다.*/
		FindFunctionParams funcToDefineThisVarUse;
		
		/** static클래스나 static이 아닌 클래스에서 부모클래스의 변수를 어떻게 참조하는가를 쉽게 알기 위해 사용한다.*/
		FindClassParams classToDefineThisVarUse;
		
		/**바이트코드를 생성할때 필요하다. 
		 * Constant table 에 들어가는 CONSTANT_Field_info 이거나
		 * CONSTANT_Method_info 이면 null 이 아니다.
		 */
		Object constant_info;
		
		/** 바이트코드 생성시에 사용자가 lValue, 함수호출, 배열첨자 등의 varUse를 클릭했을때 
		 * 나오는 화면을 위한 것인데 이미 print한 varUse이면 true, 아니면 false이다.
		 * 실제로 컴파일할 때는 필요가 없다. 
		 * ByteCodeGenerator.findParentAndPrintCode()에서 사용한다.
		 */
		//boolean hasPrintedCode;
		
		/** ++i, i++, --i, i--*/
		FindIncrementStatementParams inc;
		
		/** 현재 varUse가 문자열, 숫자 상수일때 postfix상에서의 token을 갖는다.
		 * ByteCoeGenerator에서 문자열 연결연산자 '+'를 출력할 때(printConstant()) 사용한다.*/
		CodeStringEx tokenInPostfixWhenVarUseIsConstant;
		
		/** ByteCodeGenerator에서 가상으로 만들어진 것이면 true*/
		boolean isFake;
		
		public String toString() {
			return this.name;
		}
		
		FindVarUseParams(IndexForHighArray index) {
			this.index = index;
			//number = count++;
		}
		
		protected FindVarUseParams clone() {
			FindVarUseParams r = new FindVarUseParams(null);
			r.arrayInitializer = this.arrayInitializer;
			r.child = this.child;
			r.classToDefineThisVarUse = this.classToDefineThisVarUse;
			r.constant_info = this.constant_info;
			//r.typeCastedByVarUse = this.typeCastedByVarUse;
			r.funcDecl = this.funcDecl;
			r.funcToDefineThisVarUse = this.funcToDefineThisVarUse;
			r.index = this.index;
			r.isArrayElement = this.isArrayElement;
			r.isEnumElement = this.isEnumElement;
			r.isForVarOrForFunc = this.isForVarOrForFunc;
			r.isLocal = this.isLocal;
			r.isUsingThisOrSuper = this.isUsingThisOrSuper;
			r.listOfArrayElementParams = this.listOfArrayElementParams;
			r.listOfFuncCallParams = this.listOfFuncCallParams;
			r.memberDecl = this.memberDecl;
			r.name = this.name;
			r.originName = this.originName;
			r.parent = this.parent;
			r.rValue = this.rValue;
			r.template = this.template;
			r.typeCast = this.typeCast;
			r.varDecl = this.varDecl;
			r.inc = this.inc;
			
			return r;
		}
		
		public void destroy() {
			if (this.listOfArrayElementParams!=null) {
				this.listOfArrayElementParams.reset();
				this.listOfArrayElementParams = null;
			}
			if (this.listOfFuncCallParams!=null) {
				this.listOfFuncCallParams.reset();
				this.listOfFuncCallParams = null;
			}
			if (this.arrayInitializer!=null) {
				this.arrayInitializer.destroy();
				this.arrayInitializer = null;
			}
			if (this.child!=null) {
				this.child.destroy();
				this.child = null;
			}
			if (this.rValue!=null) {
				this.rValue.destroy();
				this.rValue = null;
			}
			if (this.template!=null) {
				this.template.destroy();
				this.template = null;
			}
			if (this.typeCast!=null) {
				this.typeCast.destroy();
				this.typeCast = null;
			}
			this.classToDefineThisVarUse = null;
			//this.typeCastedByVarUse = null;
			this.funcDecl = null;
			this.varDecl = null;
			this.funcToDefineThisVarUse = null;
			this.memberDecl = null;
			this.name = null;
			this.originName = null;
			this.parent = null;			
					
			if (this.inc!=null) {
				this.inc.destroy();
			}
		}
		
		/** String fullnameTypeCast가 FindVarUseParams typeCastedByVarUse으로 바뀌면서
		 * 예전의 fullnameTypeCast을 대체하기 위한 메서드이다.
		 * @param typeCastedByVarUse
		 * @return
		 */
		public static String getFullnameTypeCast(FindVarUseParams typeCastedByVarUse) {
			if (typeCastedByVarUse==null) return null;
			if (typeCastedByVarUse.constant_info!=null) {
				// ByteCodeGenerator에서 사용
				return typeCastedByVarUse.constant_info.toString();
			}
			if (typeCastedByVarUse.typeCast!=null)
				return typeCastedByVarUse.typeCast.name;
			return null;
			
		}

		/** 상대인덱스(arrayNumber,offset)에서 절대 index를 리턴한다.*/
		public int index() {
			// TODO Auto-generated method stub
			if (index==null) return -1;
			return this.index.index();
		}
		
		
	}
	
	
	
	/** 함수호출의 파라미터 하나 혹은 배열첨자 하나, 할당문의 rValue, 타입캐스트, 즉 (타입)수식  
	 * 안에 있는 수식을 표현한다.*/
	public static class FindFuncCallParam implements IReset {
		/** 함수호출이름 또는 배열이름*/
		String funcName;
		/** '('는 불포함 */
		IndexForHighArray startIndex;
		/** ',', ')'는 불포함이지만 주석 또는 공백은 포함될수있다. 구분자의 인덱스-1*/
		IndexForHighArray endIndex;
		
		/** 함수호출 파라미터, 배열첨자, 타입캐스트의 수식 등의 
		 * getTypeOfExpression에서 처리된 수식의 fullname 타입을 갖는다.*/
		CodeStringEx typeFullName;
		
		/** =에 의해 묵시적으로 타입캐스트되는 경우 null이 아님*/
		//String typeFullNameByOperator;
		
		/** type이 오브젝트일 경우 FindClassParams*/
		FindClassParams classParams;
		
		/**postfix로 변환된 수식*/
		//CodeStringEx[] postfix;
		
		/** 수식*/
		FindExpressionParams expression;
		
		/** 수식을 트리순회시 방문했으면 true, 아니면 false*/
		//boolean isVisited;
		
		/** FindFuncCallParam이 들어있는 해당 문서 파일*/
		Compiler compiler;
		
		/** 수식 타입캐스트에서 타입캐스트하기전 수식의 타입 이름, 
		 * 반면 FindFuncCallParam.typeFullName은 타입캐스트된후의 타입이름이다.*/
		public String typeFullNameBeforeTypeCast;
		
		public FindFuncCallParam clone() {
			FindFuncCallParam r = new FindFuncCallParam(compiler, -1, -1);
			r.classParams = this.classParams;
			r.compiler = this.compiler;
			r.endIndex = this.endIndex;
			r.expression = this.expression;
			r.funcName = this.funcName;
			r.startIndex = this.startIndex;
			r.typeFullName = this.typeFullName;
			r.typeFullNameBeforeTypeCast = this.typeFullNameBeforeTypeCast;
			return r;
		}
		
		public String toString() {
			int i;
			String r = "";
			HighArray_CodeString mBuffer = compiler.mBuffer;
			for (i=startIndex(); i<=endIndex(); i++) {
				CodeString str = mBuffer.getItem(i);
				if (CompilerHelper.IsComment(str)) continue;
				else if (CompilerHelper.IsAnnotation(str)) continue;
				else r += str.str;
			}
			return r;
		}
				
		FindFuncCallParam(Compiler compiler, int startIndex, int endIndex) {
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			this.compiler = compiler;
		}
		FindFuncCallParam(Compiler compiler, FindExpressionParams expression) {
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, expression.startIndex());
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, expression.endIndex());
			this.expression = expression;
			this.compiler = compiler;
		}
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.classParams!=null) {
				this.classParams.destroy();
				this.classParams = null;
			}
			if (this.expression!=null) {
				this.expression.destroy();
				this.expression = null;
			}
			if (this.typeFullName!=null) {
				this.typeFullName.reset();
				this.typeFullName = null;
			}
			this.funcName = null;
		}

		public int startIndex() {
			// TODO Auto-generated method stub
			if (startIndex==null) return -1;
			return startIndex.index();
		}

		public int endIndex() {
			// TODO Auto-generated method stub
			if (endIndex==null) return -1;
			return endIndex.index();
		}
	}
	
	/** {, }으로 이루어지는 블록을 말한다.*/
	public static class FindBlockParams implements IReset {
		IndexForHighArray startIndex;
		IndexForHighArray endIndex;
		Compiler compiler;
		String blockName;
		CategoryOfBlock categoryOfBlock;
		
		FindBlockParams(Compiler compiler, int startIndex, int endIndex) {
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			this.compiler = compiler;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			this.blockName = null;
			if (this.categoryOfBlock!=null) {
				this.categoryOfBlock.destroy();
				this.categoryOfBlock = null;
			}
		}

		public int startIndex() {
			// TODO Auto-generated method stub
			if (startIndex==null) return -1;
			return startIndex.index();
		}
		
		public int endIndex() {
			// TODO Auto-generated method stub
			if (endIndex==null) return -1;
			return endIndex.index();
		}
	}
	
	/** (, )으로 이루어지는 작은 블록을 말한다.*/
	public static class FindSmallBlockParams  implements IReset {
		IndexForHighArray startIndex;
		IndexForHighArray endIndex;
		Compiler compiler;
		
		FindSmallBlockParams(Compiler compiler, int startIndex, int endIndex) {
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			this.compiler = compiler;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
		}

		public int startIndex() {
			// TODO Auto-generated method stub
			if (startIndex==null) return -1;
			return startIndex.index();
		}
		
		public int endIndex() {
			// TODO Auto-generated method stub
			if (endIndex==null) return -1;
			return endIndex.index();
		}
	}
	
	/** [, ]으로 이루어지는 배열 블록을 말한다.*/
	public static class FindLargeBlockParams  implements IReset {
		IndexForHighArray startIndex;
		IndexForHighArray endIndex;
		Compiler compiler;
		
		FindLargeBlockParams(Compiler compiler, int startIndex, int endIndex) {
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			this.compiler = compiler;
		}

		public int startIndex() {
			// TODO Auto-generated method stub
			if (startIndex==null) return -1;
			return startIndex.index();
		}
		
		public int endIndex() {
			// TODO Auto-generated method stub
			if (endIndex==null) return -1;
			return endIndex.index();
		}
		
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static class CategoryOfBlock implements IReset {
		CategoryOfControls categoryOfControl;
		/** 0이면 제어블록, 1이면 class, 2이면 funciton, 
		 * 0일때는 categoryOfControl을 확인한다.*/
		int typeOfBlock;
		
		static int ControlBlock = 0;
		static int Class = 1;
		static int Function = 2;
		static int Enum = 3;
		static int Try = 4;
		static int Catch = 5;
		static int Finally = 6;
		static int Synchronized = 7;
		static int Interface = 8;
		
		
		public CategoryOfBlock(int typeOfBlock, CategoryOfControls categoryOfControl) {
			this.typeOfBlock = typeOfBlock;
			if (typeOfBlock==0) {
				this.categoryOfControl = categoryOfControl;
			}
		}
		
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.categoryOfControl!=null) {
				this.categoryOfControl.destroy();
				this.categoryOfControl = null;
			}
		}

		public String toString() {
			if (typeOfBlock==0) {
				if (categoryOfControl!=null) {
					return categoryOfControl.toString();
				}
			}
			else if (typeOfBlock==1) {
				return "class";
			}
			else if (typeOfBlock==2) {
				return "function";
			}
			else if (typeOfBlock==3) {
				return "enum";
			}
			else if (typeOfBlock==4) {
				return "try";
			}
			else if (typeOfBlock==5) {
				return "catch";
			}
			else if (typeOfBlock==6) {
				return "finally";
			}
			else if (typeOfBlock==7) {
				return "synchronized";
			}
			else if (typeOfBlock==8) {
				return "interface";
			}
			return null;
		}
	}
	
	public static class CategoryOfControls implements IReset {
		int category;
		
		static int Control_if = 1;
		static int Control_elseif = 2;
		static int Control_else = 3;
		static int Control_switch = 4;
		
		static int Control_for = 5;
		static int Control_while = 6;
		static int Control_dowhile = 7;
		
		static int Control_case = 8;
		
		CategoryOfControls(int category) {
			this.category = category;
		}
		
		public void destroy() {
			// TODO Auto-generated method stub
			
		}

		public String toString() {
			if (category==0) return null;
			if (category==CategoryOfControls.Control_if) return "if";
			else if (category==CategoryOfControls.Control_elseif) return "else if";
			else if (category==CategoryOfControls.Control_else) return "else";
			else if (category==CategoryOfControls.Control_switch) return "switch";
			else if (category==CategoryOfControls.Control_while) return "while";
			else if (category==CategoryOfControls.Control_for) return "for";
			else if (category==CategoryOfControls.Control_dowhile) return "do while";
			else if (category==CategoryOfControls.Control_case) return "case";
			return null;
		}
	}
	
	/** 제어 블럭(if, else, while등)*/
	public static class FindControlBlockParams /*extends FindStatementParams*/  extends Block implements IReset {
		CategoryOfControls catOfControls;
		
		/**nameIndex==null이면 synchronized안이나 메서드 안에 있는 
		 * 가짜 try-catch블록의 nameIndex이다.
		 * 가짜 try-catch블록이란 try-catch가 없는 메서드내에서 예외를 호출함수로 던져주거나 
		 * try-catch가 없는 synchronized블록에서 예외가 발생할 경우 모니터를 해제하기 위한 것이다.
		 * 현재는 try-catch가 있더라도 가짜 try-catch블록을 만든다.
		 * (그래도 문제가 안되기 때문에)
		 * Compiler.putTryCatchShieldToSynchronized()를 참조한다.*/
		IndexForHighArray nameIndex=null;
		/** case문의 경우 case 다음 인덱스*/
		IndexForHighArray indexOfLeftParenthesis=null;
        /** case문의 경우 case 다음 ':'의 인덱스*/
		IndexForHighArray indexOfRightParenthesis=null;
		
		/**nameIndex==null이면 synchronized안이나 메서드 안에 있는 
		 * 가짜 try-catch블록의 nameIndex이다.
		 * 가짜 try-catch블록이란 try-catch가 없는 메서드내에서 예외를 호출함수로 던져주거나 
		 * try-catch가 없는 synchronized블록에서 예외가 발생할 경우 모니터를 해제하기 위한 것이다.
		 * 현재는 try-catch가 있더라도 가짜 try-catch블록을 만든다.
		 * (그래도 문제가 안되기 때문에)
		 * Compiler.putTryCatchShieldToSynchronized()를 참조한다.*/
		int nameIndex() {
			if (nameIndex==null) return -1;
			return nameIndex.index();
		}
		
		int indexOfLeftParenthesis() {
			if (indexOfLeftParenthesis==null) return -1;
			return indexOfLeftParenthesis.index();
		}
		
		int indexOfRightParenthesis() {
			if (indexOfRightParenthesis==null) return -1;
			return indexOfRightParenthesis.index();
		}
        
        boolean isBlock;
        
        /** case문일때 break문이 존재하는지 유무*/
        boolean breakExistsWhenCase;
		
		
				
		/** 조건문의 수식과 그것의 postfix*/
		FindFuncCallParam funcCall;
		
	
		
		/** 이 블록이 들어있는 parent 블록의 listOfControlBlocks에서의 인덱스*,
		 * Compiler.FindAllClassesAndItsMembers2_sub()에서 정해진다./ 
		 */
		int indexInListOfControlBlocksOfParent;
		
		
		ArrayListIReset listOfStatementsInParenthesis = new ArrayListIReset(3);
		
		
		
		/** 제어블록의 괄호안에 있는 문장 리스트*/
		//ArrayListIReset listOfStatementsInParenthesis;
		
		public String toString() {
			int i;			
			HighArray_CodeString mBuffer = compiler.mBuffer;
			String r;
			if (this.catOfControls!=null) {
				if (this.catOfControls.category==CategoryOfControls.Control_elseif) {
					r = "else if";
				}
				else {
					r = mBuffer.getItem(nameIndex()).str;
				}
			}
			else {
				r = mBuffer.getItem(nameIndex()).str;
			}
			if (indexOfLeftParenthesis()!=-1) {
				r += " ";
			}
			
			int start = indexOfLeftParenthesis();
			int end = indexOfRightParenthesis();
			for (i=start; i<=end; i++) {
				CodeString str = mBuffer.getItem(i);
				if (CompilerHelper.IsComment(str)) continue;
				else if (CompilerHelper.IsAnnotation(str)) continue;
				else r += str.str;
			}
			return r;
		}
		
    	
    	FindControlBlockParams(Compiler compiler, CategoryOfControls catOfControls, int startIndex, int endIndex) {
    		/*try {
    		super(compiler, startIndex, endIndex);
    		}catch(Exception e) {
    			
    		}*/
    		this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			
			this.catOfControls = catOfControls;
			this.compiler = compiler;
			//this.findBlockParams = new FindBlockParams(-1, -1);
		}
    	
    	public void reset() {
    		if (this.catOfControls!=null) {
    			this.catOfControls.destroy();
    			this.catOfControls = null;
    		}
    		if (funcCall!=null) {
    			funcCall.destroy();
    			funcCall = null;
    		}
    	}
	}
	
	/** 특별한 블럭(synchronized, try, catch, finally등)
	 * FindControlBlockParams를 상속하므로 제어블록의 일종이다. 
	 * 이것은 문장들을 적절한 제어블록에 넣을때(Compiler.inputStatementToSuitableBlock_caller() 참조)
	 * try, catch 등의 블록도 같이 포함해서 넣기 위한 것이다.
	 * 제어블록이지만 catOfControls는 null이 된다.*/
	public static class FindSpecialBlockParams extends /*Block*/FindControlBlockParams {
		int specialBlockType;
		/*int startIndex;
		int endIndex;
		boolean found;*/
		
		//IndexForHighArray nameIndex=null;
		
		/** catch나 finally블록에서 연결된 try블록을 말한다.*/
		FindSpecialBlockParams tryBlockConnected;
        
        public static final int SpecialBlockType_synchronized = 1;
        public static final int SpecialBlockType_try = 2;
        public static final int SpecialBlockType_catch = 3;
        public static final int SpecialBlockType_finally = 4;
              
     		
    	
		FindSpecialBlockParams(Compiler compiler, int specialBlockType, int startIndex, int endIndex) {
			super(compiler, null, startIndex, endIndex);
			this.specialBlockType = specialBlockType;			
			//this.findBlockParams = new FindBlockParams(-1, -1);
		}
		
		public String toString() {
			if (specialBlockType==1) return "synchronized";
			else if (specialBlockType==2) return "try";
			else if (specialBlockType==3) return "catch";
			else if (specialBlockType==4) return "finally";
			return null;
		}
	}
	
	
	/** 배열 초기화문*/
	public static class FindArrayInitializerParams extends Block  implements IReset {
		/*int startIndex;
		int endIndex;
		boolean found;*/
		
		/** 배열초기화문의 변수(varUse)의 인덱스, int[] a = {1, 2};에서 a의 인덱스를 말한다.*/ 
		IndexForHighArray nameIndex=null;
		/** int[] a = {1, 2};에서 {의 인덱스을 말한다.*/
        IndexForHighArray indexOfLeftParenthesis=null;
        /** int[] a = {1, 2};에서 }의 인덱스을 말한다.*/
        IndexForHighArray indexOfRightParenthesis=null;
        
        /** int[] a = {1, 2};에서 a의 인덱스을 말한다.*/
        FindVarUseParams varUse;
        
        /** int[] a = {1, 2};에서 int[]을 말한다.*/
        String typeFullName;
        
        /** int[][] a = {{1}, {2}};에서 dimension은 2이다. 최상위 노드에 있는 값만 정확하다.*/
        int dimension;
        /** 배열초기화문의 노드의 깊이, 0부터 시작<br>
         * int[][][] colors2 = {<br>
	    		{//a0<br>
		    		{Color.BLACK, Color.WHITE}, //a00<br>
		    		{Color.YELLOW,Color.BLUE, Color.GREEN} //a01<br>
	    		},
	    		 
	    		{//a1<br>
	        	    {Color.YELLOW,Color.BLUE, Color.GREEN} //a11<br>
	        	}
			};//2면 2행 3열<br>
			colors2->a0->a00->Color.BLACK, Color.WHITE <br>
					   ->a01->Color.YELLOW,Color.BLUE, Color.GREEN <br>
			 	   ->a1->a11->Color.YELLOW,Color.BLUE, Color.GREEN <br>
	       깊이   :   0	  1	  2 	<br>
	       모든 노드에 저장된다.*/
        int depth;
        /** int[][] a = {{1}, {2}};에서 listOfLength은 행(2), 열(1)이다. <br>
         * int[][][] a = {{{1}, {2}},{{1}, {2}}};에서 listOfLength은 면(2), 행(2), 열(1)이다.<br>
         * 최상위 노드에 있는 값만 정확하다.*/
        ArrayListInt listOfLength;
        /** 배열에서의 인덱스, 배열초기화문의 가장 밑에 있는 수식들의 리스트일 경우는 -1이다.*/
        int index = -1;
        
        
        public int nameIndex() {
        	if (nameIndex==null) return -1;
        	return nameIndex.index();
        }
        
        public int indexOfLeftParenthesis() {
        	if (indexOfLeftParenthesis==null) return -1;
        	return indexOfLeftParenthesis.index();
        }
        
        public int indexOfRightParenthesis() {
        	if (indexOfRightParenthesis==null) return -1;
        	return indexOfRightParenthesis.index();
        }
        
        /**FindArrayInitializerParams[], 배열안에 수식이 아니라 또다른 배열의 리스트이면 count가 0이 아니다. <br>
         * int[][] arr = {{1,2},{3,4}}; 에서 {1,2},{3,4}를 의미한다.*/
        ArrayListIReset listOfFindArrayInitializerParams = new ArrayListIReset(10);
        
        /**FindFuncCallParam[], 배열안에 또다른 배열이 아니라 수식들의 리스트이면  count가 0이 아니다.<br>
         * int[][] arr = {{1,2},{3,4}}; 에서 1,2와 3,4를 의미한다.*/
        ArrayListIReset listOfFindFuncCallParam = new ArrayListIReset(10);
        
        /** 배열초기화문이 들어있는 해당 문서파일*/
        //Compiler compiler;
        
        public String toString() {
			int i;
			String r = "";
			HighArray_CodeString mBuffer = compiler.mBuffer;
			
			int start = indexOfLeftParenthesis();
			int end = indexOfRightParenthesis();
			for (i=start; i<=end; i++) {
				CodeString str = mBuffer.getItem(i);
				if (CompilerHelper.IsBlank(str)) continue;
				else if (CompilerHelper.IsComment(str)) continue;
				else r += str.str;
			}
			return r;
		}
        
        
        public FindArrayInitializerParams(Compiler compiler, 
        		int nameIndex, int indexOfLeftParenthesis, int indexOfRightParenthesis) {
        	//super(compiler, -1, -1);
        	this.compiler = compiler;
			this.startIndex = null;
			this.endIndex = null;
			
        	this.nameIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, indexOfLeftParenthesis);
            this.indexOfLeftParenthesis = IndexForHighArray.indexRelative(this, compiler.mBuffer, indexOfLeftParenthesis);
            this.indexOfRightParenthesis = IndexForHighArray.indexRelative(this, compiler.mBuffer, indexOfRightParenthesis);
            this.compiler = compiler;
        }
        
        /**배열에서 어떤 원소의 차원별 인덱스들을 리턴한다. 
         * 예를들어 int[][] arr = {{1,2,3},{4,5,6},{7,8,9}}; 
         * 여기에서 원소 5의 결과값은 1과 1이 된다. 원소 2는 1과 0이 된다.*/
        public ArrayListInt getIndicesOfParentArrayIncludingOwnIndex(FindArrayInitializerParams array) {
        	ArrayListInt r = new ArrayListInt(5);
        	if (array.index==-1) return r;
        	r.add(array.index);
        	while (array.parent!=null) {
        		if (array.parent instanceof FindArrayInitializerParams){
        			FindArrayInitializerParams parent = (FindArrayInitializerParams) array.parent;
        			if (parent.index==-1) break; // 최상위 노드에선 index가 -1이다.
        			r.add(parent.index);
        			array = (FindArrayInitializerParams) array.parent;
        		}
        		else {
        			break;
        		}        		
        	}
        	return r;
        }

		@Override
		public void destroy() {
			super.destroy();
			// TODO Auto-generated method stub
			if (this.findBlockParams!=null) {
				this.findBlockParams.destroy();
				this.findBlockParams = null;
			}
			if (this.listOfFindArrayInitializerParams!=null) {
				this.listOfFindArrayInitializerParams.reset();
				this.listOfFindArrayInitializerParams = null;
			}
			if (this.listOfFindFuncCallParam!=null) {
				this.listOfFindFuncCallParam.reset();
				this.listOfFindFuncCallParam = null;
			}
			if (this.listOfLength!=null) {
				this.listOfLength.reset2();
				this.listOfLength = null;
			}
			this.typeFullName = null;
			this.varUse = null;
		}
	}
	
	
	public static class FindStatementParams  implements IReset {
		IndexForHighArray startIndex;
		IndexForHighArray endIndex;
		boolean found;
		Compiler compiler;
		
		Block parent;
		
		/**int i = ++j + 1; f(++k); 과 같이 문장에 증감문이 들어간 경우 true이다.*/
		boolean includesInc;
		

		/** ByteCodeGenerator에서 가상으로 만들어진 것이면 true*/
		boolean isFake;
		
		/*FindStatementParams(Object owner, Compiler compiler, int startIndex, int endIndex) {
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(owner, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(owner, compiler.mBuffer, endIndex);
		}*/
		
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (startIndex!=null) {
				startIndex.destroy();
				startIndex = null;
			}
			if (endIndex!=null) {
				endIndex.destroy();
				endIndex = null;
			}
			/*if (compiler!=null) {
				compiler.destroy();
				compiler = null;
			}*/
			
		}
		
		public int startIndex() {
			if (startIndex==null) return -1;
			return startIndex.index();
		}
		
		public int endIndex() {
			if (endIndex==null) return -1;
			return endIndex.index();
		}
		
		public String toString() {
			int i;
			String r = "";
			if (compiler==null) return r;
			HighArray_CodeString mBuffer = compiler.mBuffer;
			int start = startIndex();
			int end = endIndex();
			for (i=start; i<=end; i++) {
				CodeString str = mBuffer.getItem(i);
				if (CompilerHelper.IsComment(str)) continue;
				else if (CompilerHelper.IsAnnotation(str)) continue;
				else r += str.str;
			}
			return r;
		}
	}
	
	/** 증감문, i++, ++i 등,
	 * i++; 의 경우에 startIndex는 구분자+1, endIndex는 ;을 포함하는 인덱스를 갖는다.*/
	public static class FindIncrementStatementParams extends FindStatementParams  implements IReset 
	{
		public FindVarUseParams lValue;
		/** ++i(0), i++(1), --i(2), i--(3)*/
		int type;
		//Compiler compiler;
		
		public String toString() {
			int i;
			String r = "";
			HighArray_CodeString mBuffer = compiler.mBuffer;
			int start = startIndex();
			int end = endIndex();
			for (i=start; i<=end; i++) {
				CodeString str = mBuffer.getItem(i);
				if (CompilerHelper.IsComment(str)) continue;
				else if (CompilerHelper.IsAnnotation(str)) continue;
				else r += str.str;
			}
			return r;
		}
		
		public FindIncrementStatementParams(Compiler compiler, int startIndex, int endIndex) {
			//super(compiler, startIndex, endIndex);
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
		}
		
		/** @param startIndexOfCloneInOriginSrc : 첫번째 호출할때는 -1, 두번째 호출할때는 해당 값
		 * @param listOfAllVarUses : 새로 복제된 listOfAllVarUses*/
		private void setChildVarUses(Compiler newCompiler, FindVarUseParams varUse, int startIndexOfLValueInOriginSrc, HighArray<FindVarUseParams> listOfAllVarUses, int startIndexOfCloneInOriginSrc, int startIndexOfmlistOfAllVarUses) {
			int offset;
			if (startIndexOfCloneInOriginSrc==-1) offset = 0;
			else offset = startIndexOfCloneInOriginSrc - startIndexOfLValueInOriginSrc;
			
			// 새로 복제된 listOfAllVarUses에서 인덱스가 일치하는 varUse로 바꿔준다.
			if (varUse.parent!=null) {
				FindVarUseParams parent = varUse.parent;
				int newIndex = parent.index()-startIndexOfLValueInOriginSrc+offset;
				int j;
				int len = listOfAllVarUses.getCount();
				for (j=startIndexOfmlistOfAllVarUses; j<len; j++) {
					FindVarUseParams v = (FindVarUseParams) listOfAllVarUses.getItem(j);
					if (v.index()==newIndex) {
						varUse.parent = v;
						break;
					}
				}
			}
			// 새로 복제된 mlistOfAllVarUses에서 인덱스가 일치하는 varUse로 바꿔준다.
			if (varUse.child!=null) {
				FindVarUseParams child = varUse.child;
				int newIndex = child.index()-startIndexOfLValueInOriginSrc+offset;
				int j;
				int len = listOfAllVarUses.getCount();
				for (j=startIndexOfmlistOfAllVarUses; j<len; j++) {
					FindVarUseParams v = (FindVarUseParams) listOfAllVarUses.getItem(j);
					if (v.index()==newIndex) {
						varUse.child = v;
						break;
					}
				}
			}
			if (varUse.index()==0) {
				int a;
				a=0;
				a++;
			}
			if (varUse.listOfArrayElementParams!=null) {
				int i;
				ArrayListIReset list = varUse.listOfArrayElementParams;
				list = list.clone();
				for (i=0; i<list.count; i++) {
					FindFuncCallParam func = (FindFuncCallParam) list.getItem(i);
					func = func.clone();
					func.startIndex = IndexForHighArray.indexRelative(func, compiler.mBuffer, func.startIndex() -startIndexOfLValueInOriginSrc+offset);
					func.endIndex = IndexForHighArray.indexRelative(func, compiler.mBuffer, func.endIndex() -startIndexOfLValueInOriginSrc+offset);
					func.compiler = newCompiler;
					if (func.expression!=null) {
						func.expression = func.expression.clone();
						func.expression.compiler = newCompiler;
					}
					
					if (func.expression!=null) {
						func.expression.startIndex = IndexForHighArray.indexRelative(func.expression, compiler.mBuffer, func.expression.startIndex()  -startIndexOfLValueInOriginSrc+offset);
						func.expression.endIndex = IndexForHighArray.indexRelative(func.expression, compiler.mBuffer, func.expression.endIndex()  -startIndexOfLValueInOriginSrc+offset);
						CodeStringEx[] postfix = new CodeStringEx[func.expression.postfix.length];
						
						for (int j=0; j<postfix.length; j++) {
							postfix[j] = func.expression.postfix[j].clone();
							CodeStringEx token = postfix[j];
							token.indicesInSrc = token.indicesInSrc.clone();
							token.listOfVarUses = token.listOfVarUses.clone();
							for (int k=0; k<token.indicesInSrc.count; k++) {								
								token.indicesInSrc.list[k] += -startIndexOfLValueInOriginSrc+offset;
								FindVarUseParams v = (FindVarUseParams) token.listOfVarUses.list[k];								
								if (v==null) continue;
								
								// 새로 복제된 mlistOfAllVarUses에서 인덱스가 일치하는 varUse로 바꿔준다.								 
								int newIndex = v.index()-startIndexOfLValueInOriginSrc+offset;
								int len = listOfAllVarUses.getCount();
								for (int m=startIndexOfmlistOfAllVarUses; m<len; m++) {
									FindVarUseParams v3 = (FindVarUseParams) listOfAllVarUses.getItem(m);
									if (v3.index()==newIndex) {
										token.listOfVarUses.list[k] = v3;
										break;
									}
								}
								
								// 중첩된 varUses들에 대해서 재귀적 호출
								/*FindVarUseParams newVarUse = (FindVarUseParams) token.listOfVarUses.list[k];
								this.setChildVarUses(newCompiler, newVarUse, startIndexOfLValueInOriginSrc, 
										mlistOfAllVarUses, startIndexOfCloneInOriginSrc, startIndexOfmlistOfAllVarUses);*/
							}//for (int k=0; k<token.indicesInSrc.count; k++) {
							
						}//for (int j=0; j<postfix.length; j++) {
						func.expression.postfix = postfix;
					}//if (func.expression!=null) {
					list.list[i] = func;
				}//for (i=0; i<list.count; i++) {
				varUse.listOfArrayElementParams = list;
			}//if (varUse.listOfArrayElementParams!=null) {
			
			if (varUse.index()==0) {
				int a;
				a=0;
				a++;
			}
		}
		
		FindAssignStatementParams toFindAssignStatementParams() {
			if (lValue.index()==394) {
				int a;
				a=0;
				a++;
			}
			HighArray_CodeString newSrc = new HighArray_CodeString(10);
			int startIndexOfLValue, endIndexOfLValue;
			int startIndexOfLValueInOriginSrc, endIndexOfLValueInOriginSrc;
			startIndexOfLValue = compiler.getFullNameIndex5(compiler.mBuffer, true, lValue.index());
			endIndexOfLValue = compiler.getFullNameIndex2(compiler.mBuffer, lValue.index());
			
			startIndexOfLValueInOriginSrc = startIndexOfLValue;
			endIndexOfLValueInOriginSrc = endIndexOfLValue;
			
			endIndexOfLValue -= startIndexOfLValue;
			startIndexOfLValue = 0;
			
			int i;
			for (i=startIndexOfLValueInOriginSrc; i<=endIndexOfLValueInOriginSrc; i++) {
				// 인용문, 주석, 상수와 같은 스트링 타입도 이전 소스와 같게 된다. 
				CodeString str = compiler.mBuffer.getItem(i);
				/*if (str.equals("+") || str.equals("-")) {
					int nextIndex = compiler.SkipBlank(compiler.mBuffer, false, i+1, endIndexOfLValueInOriginSrc);
					if (nextIndex>=endIndexOfLValueInOriginSrc) {
						return null;
					}
					if (compiler.mBuffer.getItem(nextIndex).equals(str)) {
						i = nextIndex;
						continue;
					}
				}*/
				newSrc.add(str);
			}
			
			int indexOfEqual = newSrc.count;
			
			newSrc.add(new CodeString("=", Compiler.textColor));
			
			int startIndexOfClone = indexOfEqual+1;
			int len = 0;
			for (i=0; i<indexOfEqual; i++) {
				// 인용문, 주석, 상수와 같은 스트링 타입도 이전 소스와 같게 된다.
				CodeString str = newSrc.getItem(i);
				newSrc.add(str);
				len++;
			}
			
			int indexOfPlus = startIndexOfClone+len;
			CodeString plus = new CodeString("+", Compiler.textColor);
			plus.setType(CodeStringType.Text);
			newSrc.add(plus);
			
			int indexOf1 = indexOfPlus+1;
			CodeString strOne = new CodeString("1", Compiler.textColor);
			strOne.setType(CodeStringType.Constant);
			newSrc.add(strOne);
			
			
			Compiler newCompiler = new Compiler();
			newCompiler.mBuffer = newSrc;
			newCompiler.setLanguage(Language.Java);
			if (newCompiler.mlistOfAllVarUsesHashed==null) {
				newCompiler.mlistOfAllVarUsesHashed = new Hashtable2(newSrc, 10, 2);
			}
			
			HighArray<FindVarUseParams> mlistOfAllVarUses = new HighArray<FindVarUseParams>(10);
			int startIndex = compiler.getIndexInmListOfAllVarUses(compiler.mBuffer, compiler.mlistOfAllVarUses, 0, startIndexOfLValueInOriginSrc, true);
			int endIndex =  compiler.getIndexInmListOfAllVarUses(compiler.mBuffer, compiler.mlistOfAllVarUses, startIndex, endIndexOfLValueInOriginSrc, false);
			
			
			int newStartIndex = 0;
			int newEndIndex = newSrc.count-1;
			FindAssignStatementParams r = new FindAssignStatementParams(newCompiler, newStartIndex, newEndIndex);
			
			// 2번 넣어준다.
			int indexVarUse;
			for (i=startIndex; i<=endIndex; i++) {
				FindVarUseParams varUse = (FindVarUseParams) compiler.mlistOfAllVarUses.getItem(i);
				FindVarUseParams varUse2 = varUse.clone();
				indexVarUse = varUse2.index();
				indexVarUse -= startIndexOfLValueInOriginSrc;
				indexVarUse = newCompiler.SkipBlank(newSrc, false, indexVarUse, newSrc.count-1);
				varUse2.index = IndexForHighArray.indexRelative(varUse2, newSrc, indexVarUse);
				mlistOfAllVarUses.add(varUse2);
			}//for (i=startIndex; i<=endIndex; i++) {
			
			if (lValue.index()==394) {
				int a;
				a=0;
				a++;
			}
			
			// 넣어진 varUse들의 parent와 child를 newSrc에서의 varUse로 바꿔준다.
			int len2 = mlistOfAllVarUses.getCount();
			for (i=0; i<len; i++) {
				FindVarUseParams varUse = (FindVarUseParams) mlistOfAllVarUses.getItem(i);
				this.setChildVarUses(newCompiler, varUse, startIndexOfLValueInOriginSrc, mlistOfAllVarUses, -1, 0);
			}
			
			int countOfVarUses = mlistOfAllVarUses.getCount();
			int k = startIndexOfClone;
			int startIndexOfmlistOfAllVarUses = mlistOfAllVarUses.getCount();
			int indexVarUse2;
			for (i=startIndex; i<=endIndex; i++) {
				FindVarUseParams varUse = (FindVarUseParams) compiler.mlistOfAllVarUses.getItem(i);
				FindVarUseParams varUse2 = varUse.clone();
				indexVarUse2 = varUse2.index();
				indexVarUse2 -= startIndexOfLValueInOriginSrc;
				indexVarUse2 += k;
				indexVarUse2 = newCompiler.SkipBlank(newSrc, false, indexVarUse2, newSrc.count-1);
				varUse2.index = IndexForHighArray.indexRelative(varUse2, newSrc, indexVarUse2);
				//k++;
				mlistOfAllVarUses.add(varUse2);			
				
			}
			
			if (lValue.index()==394) {
				int a;
				a=0;
				a++;
			}
			
			// 넣어진 varUse들의 parent와 child를 newSrc에서의 varUse로 바꿔준다.
			int len3 = mlistOfAllVarUses.getCount();
			for (i=startIndexOfmlistOfAllVarUses; i<len3; i++) {
				FindVarUseParams varUse = (FindVarUseParams) mlistOfAllVarUses.getItem(i);
				this.setChildVarUses(newCompiler, varUse, startIndexOfLValueInOriginSrc, mlistOfAllVarUses, 
						startIndexOfLValueInOriginSrc+startIndexOfClone, startIndexOfmlistOfAllVarUses);
			}
			
			int index = indexOf1-startIndexOfLValue;			 
			FindVarUseParams varUseOne = new FindVarUseParams(null);
			varUseOne.name = "1";
			varUseOne.originName = "1";
			index = newCompiler.SkipBlank(newSrc, false, index, newSrc.count-1);
			mlistOfAllVarUses.add(varUseOne);
			
			
			if (lValue.index()==394) {
				int a;
				a=0;
				a++;
			}
			
			
			
			newCompiler.mlistOfAllVarUses = mlistOfAllVarUses;
			
			int len4 = mlistOfAllVarUses.getCount();
			for (i=0; i<len4; i++) {
				try{
					if (i==2) {
						int a;
						a=0;
						a++;
					}
					FindVarUseParams varUse = (FindVarUseParams)mlistOfAllVarUses.getItem(i);
				newCompiler.mlistOfAllVarUsesHashed.input(varUse);
				}catch(Exception e) {
					e.printStackTrace();
					int a;
					a=0;
					a++;
				}
			}
			
			FindVarUseParams lValue = null;
			for (i=0; i<len4; i++) {
				FindVarUseParams varUse = (FindVarUseParams) mlistOfAllVarUses.getItem(i);
				int indexOfEqual2 = newCompiler.IsLValue(newSrc, varUse);
				if (indexOfEqual2>0) {
					lValue = varUse;
					break;
				}
			}
			
			
			
			
			r.compiler = newCompiler;
			r.indexOfEqual = IndexForHighArray.indexRelative(r, newSrc, indexOfEqual-startIndexOfLValue);
			r.lValue = lValue;			
			r.rValue = new FindFuncCallParam(newCompiler, r.indexOfEqual()+1, newEndIndex);
			r.lValue.rValue = r.rValue;
			
			try {
				r.rValue.typeFullName = newCompiler.getTypeOfExpression(newSrc, r.rValue);
			}catch(Exception e) {
				int a;
				a=0;
				a++;
				e.printStackTrace();
			}
			return r;
			
		}
	}
	
	
	/** 할당문, lValue(FindVarUseParams)로 시작하여 할당문을 찾는다.*/
	public static class FindAssignStatementParams extends FindStatementParams  implements IReset 
	{
		/*int startIndex;
		int endIndex;
		boolean found;*/	
		
		/** a = 1 + (a2 = 2);에서 a할당문은 true, a2는 false이다. f (a=3, b);에서 a는 false이다.*/
		boolean isIndependent = true;
		
		
		/** found가 true이고 lValue가 null이면 임시로컬변수에 rValue가 저장되는 것이고(a+1>b+2, 함수호출문에서처럼), 
		 * null이 아니면 lValue가 소스상에서 명시적으로 있는 것이다(a=b처럼).  
		 */
		FindVarUseParams lValue;
		
		//FindExpressionParams rValue;
		FindFuncCallParam rValue;
		
		/** 대입연산자를 포함한 수식을 말한다. 
		 * 예를들어 a+=b는 a b +=이 된다. 여기에서 funcCallParam은 a+=b이다.*/
		FindFuncCallParam funcCallParam;
		
		/** 함수호출 파라미터, 배열첨자, 타입캐스트의 수식 등의 
		 * getTypeOfExpression에서 처리된 수식의 fullname 타입을 갖는다.*/
		String typeFullNameOfRValue;

		/** =, +=, -=, *=, /=등에서 =의 인덱스*/
		public IndexForHighArray indexOfEqual;

		/** =, +=, -=, *=, /=등, CompilerHelper.IsAssignStatement()를 참조한다.*/
		public String operator;
		
		/**할당문의 변수사용 리스트(FindVarUseParams[]), 참고로 수식을 찾을때 활용한다.*/
		//ArrayListIReset listOfVarUses = new ArrayListIReset(1);
		
		/** 할당문이 배열초기화문이면 null이 아니다.*/
		FindArrayInitializerParams arrayInitializer;
		
		int indexOfEqual() {
			if (indexOfEqual==null) return -1;
			return indexOfEqual.index();
		}
		
		public String toString() {
			String r = "";
			if (lValue!=null && operator!=null && rValue!=null)
				r += lValue + operator + rValue + "\n";
			if (funcCallParam!=null) r += funcCallParam + "\n";
			//if (arrayInitializer!=null) r += arrayInitializer;
			return r;
		}
		
		
		FindAssignStatementParams(Compiler compiler, int startIndex, int endIndex) {
			//super(compiler, startIndex, endIndex);
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
		}



		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.arrayInitializer!=null) {
				this.arrayInitializer.destroy();
				this.arrayInitializer = null;
			}
			if (this.funcCallParam!=null) {
				this.funcCallParam.destroy();
				this.funcCallParam = null;
			}
			if (this.lValue!=null) {
				this.lValue.destroy();
				this.lValue = null;
			}
			if (this.rValue!=null) {
				this.rValue.destroy();
				this.rValue = null;
			}
			this.typeFullNameOfRValue = null;
		}
	}
	
	
	
	
	
	
	/** 독립적인 함수호출문, startIndex는 공백불포함, endIndex는 ;의 인덱스에서 -1*/
	public static class FindIndependentFuncCallParams extends FindStatementParams
	{
		/*int startIndex;
		int endIndex;
		boolean found;*/
		
		/**할당문의 변수사용 리스트(FindVarUseParams[]), 참고로 수식을 찾을때 활용한다.*/
		ArrayListIReset listOfVarUses = new ArrayListIReset(1);
		
		/** 독립적 함수호출문이 들어있는 해당 문서파일*/
		//Compiler compiler;
		
		public String toString() {
			int i;			
			HighArray_CodeString mBuffer = compiler.mBuffer;
			String r = "";
			int start = startIndex();
			int end = endIndex();
			for (i=start; i<=end; i++) {
				CodeString str = mBuffer.getItem(i);
				if (CompilerHelper.IsComment(str)) continue;
				else if (CompilerHelper.IsAnnotation(str)) continue;
				else r += str.str;
			}
			return r;
		}
		
		FindIndependentFuncCallParams(Compiler compiler, int startIndex, int endIndex) {
			//super(compiler, startIndex, endIndex);
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
		}
	}
	
	
	
	
	
	/** FindVarDeclarationAndVarUses에서 return, break, continue, throw 문등을 찾고 
	 *  mlistOfSpecialStatements에 넣는다.*/
	public static class FindSpecialStatementParams extends FindStatementParams
	{
		/*int startIndex;
		int endIndex;
		boolean found;*/
		/**return, break, continue, throw의 인덱스, 
		 * kewordIndex==null이면 synchronized안이나 메서드 안에 있는 가짜 try-catch블록에서
		 * catch안에 있는 가짜 throw문이다.
		 * 가짜 try-catch블록이란 try-catch가 없는 메서드내에서 예외를 호출함수로 던져주거나 
		 * try-catch가 없는 synchronized블록에서 예외가 발생할 경우 모니터를 해제하기 위한 것이다.
		 * 현재는 try-catch가 있더라도 가짜 try-catch블록을 만든다.
		 * (그래도 문제가 안되기 때문에)
		 * Compiler.putTryCatchShieldToSynchronized()를 참조한다.*/
		IndexForHighArray kewordIndex = null;
		
		/** 독립적 return, break 문 등이 들어있는 해당 문서파일*/
		//Compiler compiler;
		
		/** return expression; 와 같이 expression의 typeName을 말한다.*/
		FindFuncCallParam funcCall;
		
		/**return, break, continue, throw의 인덱스, 
		 * kewordIndex==null이면 synchronized안이나 메서드 안에 있는 가짜 try-catch블록에서
		 * catch안에 있는 가짜 throw문이다.
		 * 가짜 try-catch블록이란 try-catch가 없는 메서드내에서 예외를 호출함수로 던져주거나 
		 * try-catch가 없는 synchronized블록에서 예외가 발생할 경우 모니터를 해제하기 위한 것이다.
		 * 현재는 try-catch가 있더라도 가짜 try-catch블록을 만든다.
		 * (그래도 문제가 안되기 때문에)
		 * Compiler.putTryCatchShieldToSynchronized()를 참조한다.*/
		int kewordIndex() {
			if (kewordIndex==null) return -1;
			return kewordIndex.index();
		}
		
		public String toString() {
			int i;			
			HighArray_CodeString mBuffer = compiler.mBuffer;
			String r = "";
			int start = startIndex();
			int end = endIndex();
			for (i=start; i<=end; i++) {
				CodeString str = mBuffer.getItem(i);
				if (CompilerHelper.IsComment(str)) continue;
				else if (CompilerHelper.IsAnnotation(str)) continue;
				else r += str.str;
			}
			return r;
		}
		
		FindSpecialStatementParams(Compiler compiler, int startIndex, int endIndex, int kewordIndex) {
			//super(compiler, startIndex, endIndex);
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			this.kewordIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, kewordIndex);
		}
	}
	
	
	
	
	
	
	/** 수식, lValue(FindVarUseParams)로 시작하여 수식을 찾는다.
	 * CodeStringEx[] postfix(postfix로 변환된 수식) 멤버에서 postfix는 listOfVarUses을 가지므로
	 * 수식들이 중첩되더라도 수식의 계층구조를 표현할수있다. 
	 * 예를 들어 수식안에 함수호출이나 배열참조, 타입캐스트((타입)(수식)의 경우처럼) 등이 포함되어있는 경우
	 * 함수호출의 파라미터 리스트, 배열참조의 첨자리스트, 타입캐스트안에 있는 수식을 접근하기 위해 
	 * FindExpressionParams의 멤버인 postfix의 listOfVarUses(FindVarUseParams[])을 활용해서 
	 * 해당 varUse의 listOfArrayElementParams와 listOfFuncCallParams, typeCast에 접근한다.
	 * 또한 listOfArrayElementParams(배열참조)와 listOfFuncCallParams(함수호출)는 FindFuncCallParam[]이므로 
	 * FindFuncCallParam은 FindExpressionParams을 가지고 있고 typeCast 역시 FindExpressionParams을 멤버로
	 * 가지므로 수식의 계층구조에 접근할 수 있다.*/
	public static class FindExpressionParams implements IReset {
		/** 공백불포함*/
		IndexForHighArray startIndex;
		/** 세미콜론, 콜론 인덱스 - 1*/
		IndexForHighArray endIndex;
		boolean found;
		
		/**postfix로 변환된 수식*/
		CodeStringEx[] postfix;
		
		/**수식에 있는 변수참조 리스트(FindVarUseParams[])*/
		//ArrayListIReset listOfVarUses;
		
		/**수식에 있는 변수참조 리스트(FindVarUseParams[])*/
		//ArrayListIReset listOfVarUsesInPostFix;
		
		//CodeString strLValue;
		//CodeString strRValue;
		
		//CodeString strOperater;
		
		/** found가 true이고 lValue가 null이면 임시로컬변수에 rValue가 저장되는 것이고(a+1>b+2, 함수호출문에서처럼), 
		 * null이 아니면 lValue가 소스상에서 명시적으로 있는 것이다(a=b처럼).  
		 */
		//FindVarUseParams lValue;
		
		Compiler compiler;
		
		public FindExpressionParams clone() {
			FindExpressionParams r = new FindExpressionParams(compiler, -1, -1);
			r.compiler = this.compiler;
			r.endIndex = this.endIndex;
			r.found = this.found;
			r.postfix = this.postfix;
			r.startIndex = this.startIndex;
			return r;
		}
		
		FindExpressionParams(Compiler compiler, int startIndex, int endIndex) {
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
		}
		
		public int startIndex() {
			if (startIndex==null) return -1;
			return startIndex.index();
		}
		
		public int endIndex() {
			if (endIndex==null) return -1;
			return endIndex.index();
		}
		
		public String toString() {
			int i;
			String r = "";
			if (compiler==null) return r;
			HighArray_CodeString mBuffer = compiler.mBuffer;
			for (i=startIndex(); i<=endIndex(); i++) {
				CodeString str = mBuffer.getItem(i);
				if (CompilerHelper.IsComment(str)) continue;
				else if (CompilerHelper.IsAnnotation(str)) continue;
				else r += str.str;
			}
			return r;
		}

		public void destroy() {
			// TODO Auto-generated method stub
			if (this.postfix!=null) {
				int i;
				for (i=0; i<this.postfix.length; i++) {
					if (this.postfix[i]!=null) {
						this.postfix[i].reset();
						this.postfix[i] = null;
					}
				}
				this.postfix = null;
			}
		}
		
		
		
		
	}
	
	
	static class Package2 implements IReset {
		/** 디렉토리, Package2[]*/
		ArrayListIReset listOfChildPackages;
		/** .class, FindClassParams[] */
		ArrayListIReset listOfClasses;
		/** 풀경로가 아니라 자기 이름만*/
		String name;
		
		static int countLoaded = 0;
		
		/** recursive call*/
		Package2(Compiler compiler, String path) {
			try {
			File parent = new File(path);
			if (parent.exists()==false) return;
			
			name = FileHelper.getFilename(path);
			
			listOfChildPackages = new ArrayListIReset(10);
			listOfClasses = new ArrayListIReset(10);
			
			String[] list = parent.list();
			
			if (name.equals("javax")) {
				int a;
				a=0;
				a++;
			}
			
			int i;
			for (i=0; i<list.length; i++) {
				String childPath = path+File.separator+list[i];
				File child = new File(childPath);
				if (child.isDirectory()) {
					Package2 p = new Package2(compiler, childPath);
					listOfChildPackages.add(p);
				}
				else {
					String ext = FileHelper.getExt(childPath);
					if (ext.equals(".class") && childPath.contains("$")==false) {
						FindClassParams classParams = CompilerHelper.loadClass(compiler, childPath);
						if (classParams!=null && classParams.name!=null) {
							listOfClasses.add(classParams);
							countLoaded++;
							CompilerHelper.showMessage(true, classParams.name + " class loaded : " + 
									countLoaded + " completed.");
						}
					}
				}
			}
			}catch(Exception e) {
				e.printStackTrace();
				CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			}
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.listOfChildPackages!=null) {
				this.listOfChildPackages.reset();
				this.listOfChildPackages = null;
			}
			if (this.listOfClasses!=null) {
				this.listOfClasses.reset();
				this.listOfClasses = null;
			}
			this.name = null;
		}
	}
	
	/**문장을 만들때 필요, import XXX.XXX.XXX을 말한다.*/
	public static class ImportStatement extends FindStatementParams {
		/** import 문 등이 들어있는 해당 문서파일*/
		//Compiler compiler;
		
		public String toString() {
			int i;			
			HighArray_CodeString mBuffer = compiler.mBuffer;
			String r = "";
			int start = startIndex();
			int end = endIndex();
			for (i=start; i<=end; i++) {
				CodeString str = mBuffer.getItem(i);
				if (CompilerHelper.IsComment(str)) continue;
				else if (CompilerHelper.IsAnnotation(str)) continue;
				else r += str.str;
			}
			return r;
		}
		
		public ImportStatement(Compiler compiler, int startIndex, int endIndex) {
			//super(compiler, startIndex, endIndex);
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			this.compiler = compiler;
		}
	}
	
	/**문장을 만들때 필요, 파일의 첫번째 문장 package XXX.XXX.XXX을 말한다.*/
	public static class PackageStatement extends FindStatementParams {
		/** import 문 등이 들어있는 해당 문서파일*/
		//Compiler compiler;
		
		public String toString() {
			int i;			
			HighArray_CodeString mBuffer = compiler.mBuffer;
			String r = "";
			int start = startIndex();
			int end = endIndex();
			for (i=start; i<=end; i++) {
				CodeString str = mBuffer.getItem(i);
				if (CompilerHelper.IsComment(str)) continue;
				else if (CompilerHelper.IsAnnotation(str)) continue;
				else r += str.str;
			}
			return r;
		}
		public PackageStatement(Compiler compiler, int startIndex, int endIndex) {
			//super(compiler, startIndex, endIndex);
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
		}
	}
	
	/**문장을 만들때 필요, 주석, 다큐주석을 말한다.*/
	public static class Comment implements IReset {
		//public String str;
		HighArray_CodeString src;
		IndexForHighArray startIndex;
		IndexForHighArray endIndex;
		
		public Comment(HighArray_CodeString src, int startIndex, int endIndex) {
			this.src = src;
			this.startIndex = IndexForHighArray.indexRelative(this, src, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, src, endIndex);
			
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			src = null;
			if (startIndex!=null) {
				startIndex.destroy();
				startIndex = null;
			}
			if (endIndex!=null) {
				endIndex.destroy();
				endIndex = null;
			}
		}
		
	}
	
	/**문장을 만들때 필요, Annotation을 말한다.*/
	public static class Annotation extends FindStatementParams implements IReset {
		//public String str;
		public Annotation(Compiler compiler, int startIndex, int endIndex) {
			//super(compiler, startIndex, endIndex);
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			super.destroy();
		}
		
	}
	
	/**문장을 만들때 필요, package, import, 클래스들을 제외한 나머지 문장들을 말한다.*/
	public static class AndSoOnStatement extends FindStatementParams  implements IReset {
		//public String str;
		public AndSoOnStatement(Compiler compiler, int startIndex, int endIndex) {
			//super(compiler, startIndex, endIndex);
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			super.destroy();
		}
		
	}
	
	public static class DocuComment extends Comment implements IReset {
		//int startIndex;
		//int endIndex;
		String str;
		HighArray_CodeString src;
		
		DocuComment(HighArray_CodeString src, int startIndex, int endIndex) {
			super(src, startIndex, endIndex);
			
			ArrayListChar message = new ArrayListChar(100);
			int i;
			for (i=startIndex; i<=endIndex; i++) {
				CodeString str = src.getItem(i);
				message.add(str.toString());
			}
			this.str = new String(message.getItems());
		}
		
		public String toString() {
			return this.str;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			str = null;
			if (src!=null) {
				src.destroy();
				src = null;
			}
		}
	}
	
	
	
	
	public static class Error  implements IReset {
		IndexForHighArray startIndex;
		IndexForHighArray endIndex;
		public String msg;
		int errorNum;
		public Compiler compiler;
		
		static int Error_MiddlePair = 1;
		static int Error_SmallPair = 2;
		static int Error_LargePair = 3;
		
		Error(Compiler compiler, int startIndex, int endIndex, String msg) {
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			this.msg = msg;
			//this.errorNum = errorNum;
		}
		Error(Compiler compiler, int startIndex, int endIndex, String msg, int errorNum) {
			this.compiler = compiler;
			this.startIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, startIndex);
			this.endIndex = IndexForHighArray.indexRelative(this, compiler.mBuffer, endIndex);
			this.msg = msg;
			this.errorNum = errorNum;
		}
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			msg = null;
		}
		public int startIndex() {
			// TODO Auto-generated method stub
			if (startIndex==null) return -1;
			return startIndex.index();
		}
		public int endIndex() {
			// TODO Auto-generated method stub
			if (endIndex==null) return -1;
			return endIndex.index();
		} 
	}
	

	
	public static enum Language {
	    	Java,
	    	CSharp,
	    	C,
	    	Html, Class
	}
	
	/** 자주 사용하는 java 패키지의 클래스들을 모아 놓은 배열이다. 
	 * 여기에 있는 클래스들을 스레드를 활용하여 loadClass()로 읽어 캐시에
	 * 등록하여 성능을 높인다.
	 */
	public static String[] glistOfJavaClassesForThread = {
		// java.lang 패키지
		"java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Class", "java.lang.Double",
		"java.lang.Error", "java.lang.Exception", "java.lang.Float", "java.lang.Integer", "java.lang.Long",
		"java.lang.Math", "java.lang.Object", "java.lang.Runtime", "java.lang.Short", "java.lang.String",
		"java.lang.StringBuffer", "java.lang.StringBuilder", "java.lang.System", "java.lang.Thread",
		"java.lang.VirtualMachineError", 
		
		// java.io 패키지
		"java.io.BufferedInputStream", "java.io.BufferedOutputStream", "java.io.BufferedReader",
		"java.io.BufferedWriter", "java.io.File", "java.io.FileInputStream", "java.io.FileOutputStream",
		"java.io.FileNotFoundException", "java.io.InputStream", "java.io.InputStreamReader",
		"java.io.IOException", "java.io.Reader", "java.io.StringReader", "java.io.StringWriter", 
		"java.io.Writer"
		
	};
}