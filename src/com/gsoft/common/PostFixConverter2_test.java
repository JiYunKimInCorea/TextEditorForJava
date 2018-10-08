package com.gsoft.common;

import android.graphics.Color;

import com.gsoft.common.CompilerHelper;
import com.gsoft.common.Code.CodeChar;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Compiler_types.FindExpressionParams;
import com.gsoft.common.Util.ArrayListInt;
import com.gsoft.common.Compiler_types.HighArray_CodeString;
import com.gsoft.common.Util.Stack;
import com.gsoft.common.Util.ArrayListIReset;
import com.gsoft.common.Util.ArrayListCodeString;

public class PostFixConverter2_test {
	
		
	//String[] input;
	CodeStringEx[] mBuffer;
	CodeStringEx[] output;
	

	/** 실제 원소 타입은 CodeStringEx이다. constructor참조*/
	HighArray_CodeString src;
	FindExpressionParams expression;
	ArrayListIReset listOfVarUses;
	
	static final int EOB = -1; 
	
	ArrayListIReset listOfBlocks = new ArrayListIReset(0);
	private Compiler compiler;
	
	
	
	
	static class IndexStartEnd {
		int startIndex;
		int endIndex;
		IndexStartEnd(int startIndex, int endIndex) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
	}
		
	
	
	/** ArrayListIReset listOfVarUses(구분자이면 null, 변수나 함수호출이면 해당 varUse를 참조한다) 멤버를 갖는다. */
	static class CodeStringEx extends CodeString {
		/** 소스상에서의 인덱스*/
		ArrayListInt indicesInSrc;
		/** 구분자이면 null, 변수나 함수호출이면 해당 varUse를 참조한다.*/
		ArrayListIReset listOfVarUses;
		
		public CodeStringEx(CodeChar[] text, int len, ArrayListInt indicesInSrc, ArrayListIReset listOfVarUses) {
			super(text, len);
			// TODO Auto-generated constructor stub
			this.indicesInSrc = indicesInSrc;
			this.listOfVarUses = listOfVarUses;
		}
		
		public CodeStringEx(String str, int textColor, ArrayListInt indicesInSrc, ArrayListIReset listOfVarUses) {
			super(str, textColor);
			this.indicesInSrc = indicesInSrc;
			this.listOfVarUses = listOfVarUses;
		}
		
		
	}
	
	/** arr의 원소의 실제 타입은 CodeStringEx이다.*/
	CodeStringEx[] toConvertExArr(CodeString[] arr) {
		CodeStringEx[] r = new CodeStringEx[arr.length];
		int i;
		for (i=0; i<arr.length; i++) {
			r[i] = (CodeStringEx) arr[i];
		}
		return r;
	}

	
	CodeStringEx[] Convert()
	{
		
		HighArray_CodeString result0 = new HighArray_CodeString(src.count);
		RemoveBlankAndComment(src, result0);
		
		compiler.CheckParenthesisAll(result0, listOfBlocks, null, null);
		
	
		HighArray_CodeString result2 = new HighArray_CodeString(10);
		
		this.mBuffer = toConvertExArr(result2.getItems()); 
		
			
		CodeStringEx[] tempBuffer;
		tempBuffer = FindExpression(mBuffer);
		// 2 + 2 * 3		2 + 2 2 3 *:T +:T @ * 2			2 + 2 3 *:T @		2 2 2 3 *:T +:T 2 /:T +:T @
		tempBuffer = ConvertInfixToPostfix(tempBuffer);
		// 2 2 3 *:T +:T @		2 2 2 3 *:T +:T 2 *:T +:T @			2 2 3 *:T +:T @
		return tempBuffer;
		
	}

	
	CodeStringEx[] FindExpression(CodeStringEx[] buffer) {
		CodeStringEx[] tempBuffer = null;
		int index = Common.Find(buffer,"=",0);
		
		return tempBuffer;	
	}
		
	@SuppressWarnings("unchecked")
	int[][] CheckParenthesis(CodeStringEx[] buffer) throws Exception
	{
		@SuppressWarnings("rawtypes")
		Stack stack = new Stack();
		int[][] tempTable = new int[100][2];
		int[][] table = new int[100][2];
		for (int i=0, k=0, j=0; i<buffer.length; i++) {
			if (buffer[i].equals("(")) {
				stack.Push(buffer[i]);
				tempTable[k][0] = i;
				k++;
			}
			else {
				continue;
			}
		}
		if (!stack.IsEmpty()) {
			throw new Exception();
		}
		else
			return table;
	}


	

	/** 이 함수를 거치면 @가 항상 끝에 하나만 나온다
	 * 2 + (2 + 2 * 3) * 2   -->   
	 * 2 + 2 2 3 *:T +:T @ * 2   -->   
	 * 2 2 2 3 *:T +:T 2 *:T +:T @
	 * @param buffer
	 * @return
	 */
	CodeStringEx[] ConvertInfixToPostfix(CodeStringEx[] tempBuffer) {
		for (int i=0; i<tempBuffer.length && tempBuffer[i]!=null; i++) {			
			if (tempBuffer[i].equals("*") || tempBuffer[i].equals("/")) {
				int pos;
				if (i+2<tempBuffer.length && tempBuffer[i+2]!=null) {
					if (CompilerHelper.IsOperator(tempBuffer[i+2]))
						pos = EOB;
						//pos = i+2;
					else {	// 연산자 뒤에 괄호안 수식이 있을 때
						pos = Common.Find(tempBuffer, "@", i+1);
					}
				}				
				
				ArrayListInt indices = new ArrayListInt(1);
				indices.add(-1);
				ArrayListIReset varUses = new ArrayListIReset(1);
				varUses.add(null);
				// :T연결
				CodeStringEx strT = new CodeStringEx(":T", Color.BLACK, indices, varUses);
				tempBuffer = Common.Insert(tempBuffer, new CodeStringEx("@",Color.BLACK, indices, varUses), i+3);
			}
		}
		return tempBuffer;
			
	}

	void Copy(char[] src, int srcIndex, char[] dest, int destIndex, int len)
	{
		int i;
	    int j = destIndex;
	    for (i = srcIndex; i < srcIndex + len; i++)
	    {
			dest[j++] = src[i];
	    }
	}

	/** 공백, '\n' 등과 주석 등을 제거한다.*/
	void RemoveBlankAndComment(HighArray_CodeString src, HighArray_CodeString result) {
		int i;		 
		for (i = 0; i < src.count; i++)
	    {
			CodeStringEx str = (CodeStringEx) src.getItem(i);
	        if (CompilerHelper.IsBlank(str)) continue;
	        else if (CompilerHelper.IsComment(str)) continue;
	        else {
	        	result.add(str);
	        }
	    }
	}	
	
	static class Common {

		static CodeStringEx[] Insert(CodeStringEx[] input, CodeStringEx c, int pos) {
			if (input==null || c==null)
				return null;
			CodeStringEx[] result = null;
			if (pos>=input.length) {
				result = new CodeStringEx[pos+1];
				CopyTo(input, result, 0);
				result[pos] = c;
			}
			return result;
		}
		
		static void CopyTo(CodeStringEx[] srcArray, CodeStringEx[] destArray, int pos) {
			int i, j;
			for (i=pos, j=0; i<pos+srcArray.length; i++) {
				destArray[i] = srcArray[j];
				j++;
			}
		}

		
		static int Find(CodeStringEx[] input, String c, int index) {
			if (input==null)
				return EOB;
			int i;
			for (i=index; i<input.length && input[i]!=null; i++) {
				if (input[i].equals(c))
					return i;
			}
			return EOB;
		}
	}


}