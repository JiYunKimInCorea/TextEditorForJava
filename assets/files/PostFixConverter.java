package com.gsoft.common;

import android.graphics.Color;

import com.gsoft.common.Compiler.CodeString;
import com.gsoft.common.Compiler.CompilerHelper;
import com.gsoft.common.Compiler.FindExpressionParams;
import com.gsoft.common.Compiler.FindVarUseParams;
import com.gsoft.common.Util.Stack;
import com.gsoft.common.Util.ArrayList;
import com.gsoft.common.Util.ArrayListCodeString;

public class PostFixConverter {
	
		
	//String[] input;
	CodeString[] mBuffer;
	CodeString[] output;
	
	ArrayListCodeString srcOld;
	FindExpressionParams expressionOld;
	ArrayList listOfVarUsesOld;
	
	ArrayListCodeString src;
	FindExpressionParams expression;
	ArrayList listOfVarUses;
	
	static final int EOB = -1; 
	
	ArrayList listOfBlocks = new ArrayList(0);
	ArrayList listOfSmallBlocks = new ArrayList(2);
	ArrayList listOfLargeBlocks = new ArrayList(0);
	private Compiler compiler;
	
	
	PostFixConverter(Compiler compiler, ArrayListCodeString src, FindExpressionParams expression)
	{
		this.compiler = compiler;
		//this.input = input;
		output = null;
		/*this.srcOld = src;
		this.expressionOld = expression;
		this.listOfVarUsesOld = expression.listOfVarUses;*/
		
		this.src = src;
		int i;
		this.src = new ArrayListCodeString(expression.endIndex-expression.startIndex+1);
		for (i=expression.startIndex; i<=expression.endIndex; i++) {
			this.src.add(new CodeString(src.getItem(i).str, Color.BLACK));
		}
		/*this.expression = new FindExpressionParams(expression);
		this.expression.startIndex = 0;
		this.expression.endIndex -= expression.startIndex; 
		
		listOfVarUses = this.expression.listOfVarUses;
		for (i=0; i<listOfVarUses.count; i++) {
			FindVarUseParams varUse = (FindVarUseParams)listOfVarUses.getItem(i);
			if (src.getItem(varUse.index).equals("bounds")) {
				int a;
				a=0;
				a++;
			}
			varUse.index -= expression.startIndex; 
		}*/
		this.expression = expression;
		this.listOfVarUses = expression.listOfVarUses;
		//mBuffer = input;
		
		
		//Compiler.CheckParenthesisAll(this.src, listOfBlocks, listOfSmallBlocks, listOfLargeBlocks);
	}
	
	static class IndexStartEnd {
		int startIndex;
		int endIndex;
		IndexStartEnd(int startIndex, int endIndex) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
	}
	
	
	/** 풀네임이 하나의 스트링으로 변환된 새로운 스트링 배열을 만든다.*/
	void ProcessFullnames2(ArrayListCodeString src, ArrayListCodeString result) {
		
		int i;
		
		// 원래 소스코드상의 Fullname의 시작과 끝 인덱스를 말한다.
		ArrayList indicesOfFullnames = new ArrayList(10);
		
		int startIndex=-1, endIndex=-1;
		// 변수사용이나 함수호출의 풀네임의 인덱스를 만든다.
		for (i=0; i<src.count; ) {
			CodeString str = src.getItem(i);
			if (str.equals("bounds")) {
				int a;
				a=0;
				a++;
			}
			if (compiler.IsIdentifier(str)) {
				startIndex = i;
				endIndex = compiler.getFullNameIndex2(src, false, i, this.listOfSmallBlocks);
				if (endIndex==src.count) {
					endIndex = src.count-1;
				}
				indicesOfFullnames.add(new IndexStartEnd(startIndex, endIndex));
				i = endIndex+1;
			}
			else {
				i++;
			}
			
		} // for i
		
		
		// 풀네임이 하나의 스트링으로 변환된 새로운 스트링 배열을 만든다.
		if (indicesOfFullnames.count==1) {
			IndexStartEnd index = (IndexStartEnd)indicesOfFullnames.getItem(0); 
			for (i=0; i<index.startIndex; i++) {
				result.add(src.getItem(i));
			} // for i
			
			CodeString fullname=new CodeString("", Color.BLACK);
			for (i=index.startIndex; i<=index.endIndex; i++) {
				fullname = fullname.concate(src.getItem(i));
			} // for i
			result.add(fullname);
			
			for (i=index.endIndex+1; i<=src.count-1; i++) {
				result.add(src.getItem(i));
			} // for i
		}
		else if (indicesOfFullnames.count>1) {
			int j;
			
			// 수식의 처음부터 첫번째 인덱스까지
			IndexStartEnd index = (IndexStartEnd)indicesOfFullnames.getItem(0); 
			for (i=0; i<index.startIndex; i++) {
				result.add(src.getItem(i));
			} // for i
			
			// 인덱스를 하나의 스트링으로 만들고 인덱스의 끝부터 다음 인덱스의 처음까지
			for (j=0; j<indicesOfFullnames.count; j++) {
				IndexStartEnd curIndex = (IndexStartEnd)indicesOfFullnames.getItem(j);
				
				CodeString fullname=new CodeString("", Color.BLACK);
				for (i=curIndex.startIndex; i<=curIndex.endIndex; i++) {
					fullname = fullname.concate(src.getItem(i));
				} // for i
				result.add(fullname);
				
				if (j<indicesOfFullnames.count-1) {
					IndexStartEnd next = (IndexStartEnd)indicesOfFullnames.getItem(j+1);
					for (i=curIndex.endIndex+1; i<next.startIndex; i++) {
						result.add(src.getItem(i));
					} // for i
				}				
			}// for j
			
			// 마지막 인덱스부터 수식의 끝까지
			IndexStartEnd last = (IndexStartEnd)indicesOfFullnames.getItem(indicesOfFullnames.count-1);
			for (i=last.endIndex+1; i<=src.count-1; i++) {
				result.add(src.getItem(i));
			} // for i
			
			
			
		} // else if (indicesOfFullnames.count>1) {
		else { //count==0
			for (i=0; i<=src.count-1; i++) {
				result.add(src.getItem(i));
			} // for i
			
		}
	}
	
	/** 풀네임이 하나의 스트링으로 변환된 새로운 스트링 배열을 만든다.*/
	void ProcessFullnames(ArrayListCodeString src, ArrayListCodeString result) {
		
		int i;
		
		// 원래 소스코드상의 Fullname의 시작과 끝 인덱스를 말한다.
		ArrayList indicesOfFullnames = new ArrayList(10);
		
		int startIndex=-1, endIndex=-1;
		// 변수사용이나 함수호출의 풀네임의 인덱스를 만든다.
		for (i=0; i<listOfVarUses.count; ) {
			FindVarUseParams varUse = (FindVarUseParams)listOfVarUses.getItem(i);
			// o.f0(a,b)에서 a, b를 건너뛰어야 한다.
			if (startIndex<=varUse.index && varUse.index<=endIndex) { 
				i++;
				continue;
			}
			if (src.getItem(varUse.index).equals("bounds")) {
				int a;
				a=0;
				a++;
			}
			startIndex = varUse.index;
			while (varUse.child!=null) {
				i++;
				varUse = varUse.child;
			}
			endIndex = varUse.index;
			int indexOfNext = compiler.SkipBlank(src, false, endIndex+1, src.count-1);
			try{
			if (indexOfNext!=src.count && src.getItem(indexOfNext).equals("(")) {
				int indexOfStartOfSmallBlock = indexOfNext;
				//int indexOfEndOfSmallBlock = Compiler.findEndIndexOfSmallBlock(this.listOfSmallBlocks, indexOfStartOfSmallBlock);
				int indexOfEndOfSmallBlock = compiler.CheckParenthesis(src, "(", ")", indexOfStartOfSmallBlock, src.count-1, false);
				if (indexOfEndOfSmallBlock!=-1) {
					endIndex = indexOfEndOfSmallBlock;
				}
			}
			}catch(Exception e) {
				int a;
				a=0;
				a++;
			}
			indicesOfFullnames.add(new IndexStartEnd(startIndex, endIndex));
			i++;
		} // for i
		
		
		// 풀네임이 하나의 스트링으로 변환된 새로운 스트링 배열을 만든다.
		if (indicesOfFullnames.count==1) {
			IndexStartEnd index = (IndexStartEnd)indicesOfFullnames.getItem(0); 
			for (i=expression.startIndex; i<index.startIndex; i++) {
				result.add(src.getItem(i));
			} // for i
			
			CodeString fullname=new CodeString("", Color.BLACK);
			for (i=index.startIndex; i<=index.endIndex; i++) {
				fullname = fullname.concate(src.getItem(i));
			} // for i
			result.add(fullname);
			
			for (i=index.endIndex+1; i<=expression.endIndex; i++) {
				result.add(src.getItem(i));
			} // for i
		}
		else if (indicesOfFullnames.count>1) {
			int j;
			
			// 수식의 처음부터 첫번째 인덱스까지
			IndexStartEnd index = (IndexStartEnd)indicesOfFullnames.getItem(0); 
			for (i=expression.startIndex; i<index.startIndex; i++) {
				result.add(src.getItem(i));
			} // for i
			
			// 인덱스를 하나의 스트링으로 만들고 인덱스의 끝부터 다음 인덱스의 처음까지
			for (j=0; j<indicesOfFullnames.count; j++) {
				IndexStartEnd curIndex = (IndexStartEnd)indicesOfFullnames.getItem(j);
				
				CodeString fullname=new CodeString("", Color.BLACK);
				for (i=curIndex.startIndex; i<=curIndex.endIndex; i++) {
					fullname = fullname.concate(src.getItem(i));
				} // for i
				result.add(fullname);
				
				if (j<indicesOfFullnames.count-1) {
					IndexStartEnd next = (IndexStartEnd)indicesOfFullnames.getItem(j+1);
					for (i=curIndex.endIndex+1; i<next.startIndex; i++) {
						result.add(src.getItem(i));
					} // for i
				}				
			}// for j
			
			// 마지막 인덱스부터 수식의 끝까지
			IndexStartEnd last = (IndexStartEnd)indicesOfFullnames.getItem(indicesOfFullnames.count-1);
			for (i=last.endIndex+1; i<=expression.endIndex; i++) {
				result.add(src.getItem(i));
			} // for i
			
			
			
		} // else if (indicesOfFullnames.count>1) {
		else { //count==0
			for (i=expression.startIndex; i<=expression.endIndex; i++) {
				result.add(src.getItem(i));
			} // for i
			
		}
	}
	
	/** (타입)id 와 같은 타입캐스팅이 하나의 스트링으로 변환된 새로운 스트링 배열을 만든다.*/
	void ProcessTypecastings(ArrayListCodeString src, ArrayListCodeString result) {
		int i;
		
		int s, e;
		int leftParent, rightParent;
		for (i=0; i<src.count; i++) {
			CodeString str = src.getItem(i);
			if (str.equals("(")) {
				leftParent = i;
				//s = Compiler.SkipBlank(src, false, 0, varUse.index-1);
				//rightParent = Compiler.findEndIndexOfSmallBlock(listOfSmallBlocks, leftParent);
				rightParent = compiler.CheckParenthesis(src, "(", ")", leftParent, src.count-1, false);
				if (rightParent!=-1) {
					
				}
			}
		}
	}
	
	/** (타입)id 와 같은 타입캐스팅이 하나의 스트링으로 변환된 새로운 스트링 배열을 만든다.*/
	void ProcessTypecastings2(ArrayListCodeString src, ArrayListCodeString result) {
		 
		
		int i;
		
		// 원래 소스코드상의 Typecasting의 시작과 끝 인덱스를 말한다.
		ArrayList indicesOfTypecastings = new ArrayList(10);
		
		int startIndex=-1, endIndex=-1;
		int s, e;
		int leftParent, rightParent;
		for (i=0; i<src.count; ) {
			CodeString str = src.getItem(i);
			if (str.equals("(")) {
				int a;
				a=0;
				a++;
			}
			if (str.equals("(")) {
				leftParent = i;
				
				//rightParent = Compiler.findEndIndexOfSmallBlock(this.listOfSmallBlocks, leftParent);
				rightParent = compiler.CheckParenthesis(src, "(", ")", leftParent, src.count-1, false);
				if (rightParent!=-1) {
					if (rightParent-leftParent==2) {
						//if ( Compiler.IsType(src, false, leftParent+1)!=-1 ) {
						//if ( Compiler.IsIdentifier( src.getItem(leftParent+1) ) ) {
						//	if ( Compiler.IsIdentifier( src.getItem(rightParent+1) ) ) {
								startIndex = leftParent;
								endIndex = rightParent+1;
								indicesOfTypecastings.add(new IndexStartEnd(startIndex, endIndex));
								i = endIndex+1;
								continue;
							/*}
							else {
								i++;
								continue;
							}
						}
						else {
							i++;
							continue;
						}		*/				
					} // if (rightParent-leftParent==2) {
					else {
						i++;
						continue;
					}
				}
				else {
					i++;
					continue;
				}
				
			}
			else {
				i++;
			}
			
		} // for i
		
		
		// 타입캐스팅이 하나의 스트링으로 변환된 새로운 스트링 배열을 만든다.
		if (indicesOfTypecastings.count==1) {
			IndexStartEnd index = (IndexStartEnd)indicesOfTypecastings.getItem(0); 
			for (i=0; i<index.startIndex; i++) {
				result.add(src.getItem(i));
			} // for i
			
			CodeString typecasting=new CodeString("", Color.BLACK);
			for (i=index.startIndex; i<=index.endIndex; i++) {
				typecasting = typecasting.concate(src.getItem(i));
			} // for i
			result.add(typecasting);
			
			for (i=index.endIndex+1; i<=src.count-1; i++) {
				result.add(src.getItem(i));
			} // for i
		}
		else if (indicesOfTypecastings.count>1) {
			int j;
			
			// 수식의 처음부터 첫번째 인덱스까지
			IndexStartEnd index = (IndexStartEnd)indicesOfTypecastings.getItem(0); 
			for (i=0; i<index.startIndex; i++) {
				result.add(src.getItem(i));
			} // for i
			
			// 인덱스를 하나의 스트링으로 만들고 인덱스의 끝부터 다음 인덱스의 처음까지
			for (j=0; j<indicesOfTypecastings.count; j++) {
				IndexStartEnd curIndex = (IndexStartEnd)indicesOfTypecastings.getItem(j);
				
				CodeString typecasting=new CodeString("", Color.BLACK);
				for (i=curIndex.startIndex; i<=curIndex.endIndex; i++) {
					typecasting = typecasting.concate(src.getItem(i));
				} // for i
				result.add(typecasting);
				
				if (j<indicesOfTypecastings.count-1) {
					IndexStartEnd next = (IndexStartEnd)indicesOfTypecastings.getItem(j+1);
					for (i=curIndex.endIndex+1; i<next.startIndex; i++) {
						result.add(src.getItem(i));
					} // for i
				}				
			}// for j
			
			// 마지막 인덱스부터 수식의 끝까지
			IndexStartEnd last = (IndexStartEnd)indicesOfTypecastings.getItem(indicesOfTypecastings.count-1);
			for (i=last.endIndex+1; i<=src.count-1; i++) {
				result.add(src.getItem(i));
			} // for i
			
			
			
		} // else if (indicesOfTypecastings.count>1) {
		else { //count==0
			for (i=0; i<=src.count-1; i++) {
				result.add(src.getItem(i));
			} // for i
			
		}
	}

	
	CodeString[] Convert()
	{
		/*RemoveBlank();

		CheckLetter();
		ConvertToStringArray();*/
		
		
		ArrayListCodeString result0 = new ArrayListCodeString(src.count);
		RemoveBlankAndComment(src, result0);
		
		compiler.CheckParenthesisAll(result0, listOfBlocks, listOfSmallBlocks, listOfLargeBlocks);
		
		ArrayListCodeString result1 = new ArrayListCodeString(result0.count);
		ProcessFullnames2(result0, result1);
		
		listOfBlocks.reset();
		listOfSmallBlocks.reset();
		listOfLargeBlocks.reset();
		compiler.CheckParenthesisAll(result1, listOfBlocks, listOfSmallBlocks, listOfLargeBlocks);
		
		ArrayListCodeString result2 = new ArrayListCodeString(result1.count);
		ProcessTypecastings2(result1, result2);
		
		this.mBuffer = result2.getItems();
		
		//this.mBuffer = result1.getItems();
		
		
		CheckOperatorRule();

		
		CodeString[] tempBuffer;
		tempBuffer = FindExpression(mBuffer);
		// 2 + 2 * 3	2 + (2 + 2 * 3) * 2			(2)+(2*3)			(2 + (2 + (2*3)) / 2)
		// (-1*2+(2*3)/2)-1
		tempBuffer = ProcessParenthesis(tempBuffer);
		if (tempBuffer==null) return null;
		// 2 + 2 * 3		2 + 2 2 3 *:T +:T @ * 2			2 + 2 3 *:T @		2 2 2 3 *:T +:T 2 /:T +:T @
		tempBuffer = ConvertInfixToPostfix(tempBuffer);
		// 2 2 3 *:T +:T @		2 2 2 3 *:T +:T 2 *:T +:T @			2 2 3 *:T +:T @
		tempBuffer = RemoveFlag(tempBuffer);	// ":T" 제거
		tempBuffer = RemoveSeparator(tempBuffer);	// "@" 제거
		return tempBuffer;
		
	}

	
	CodeString[] FindExpression(CodeString[] buffer) {
		CodeString[] tempBuffer;
		int index = Common.Find(buffer,"=",0);
		int oldIndex=0;
		while (index!=EOB) {
			oldIndex = index;
			index = Common.Find(buffer,"=",index+1);
		}
		if (Common.Find(buffer,"=",0)!=EOB) {
			tempBuffer = Common.Copy(buffer,oldIndex+3, buffer.length-oldIndex-3);
		}
		else {
			tempBuffer = Common.Copy(buffer,0, buffer.length);
		}
		
		int i;
		int pos;
		for (i=0; i<tempBuffer.length; i++) {
			pos = Common.Find(tempBuffer,";",0);
			if (pos!=EOB) {
				tempBuffer = Common.Remove(tempBuffer,pos,1);
				if (i>=pos)	i--;
			}
		}

		return tempBuffer;	
	}
	
	/*int[][] CheckParenthesis(ArrayList listOfSmallBlockParams) {
		int[][] r = new int[listOfSmallBlockParams.count][2];
		int i;
		FindSmallBlockParams smallBlock;
		for (i=0; i<listOfSmallBlockParams.count; i++) {
			smallBlock = (FindSmallBlockParams) listOfSmallBlockParams.getItem(i);
			r[i][0] = smallBlock.startIndex;
			r[i][1] = smallBlock.endIndex;
		}
		return r;
	}*/
	
	@SuppressWarnings("unchecked")
	int[][] CheckParenthesis(CodeString[] buffer) throws Exception
	{
		@SuppressWarnings("rawtypes")
		Stack stack = new Stack();
		int[][] tempTable = new int[100][2];
		int[][] table = new int[100][2];
		for (int i=0, k=0, j=0; i<buffer.length; i++) {
			if (buffer[i].equals("(")) {
				stack.Push(buffer[i]);
				tempTable[k][0] = i;
				tempTable[k][1] = -1;
				k++;
			}
			else if (buffer[i].equals(")")) {
				if (stack.IsEmpty()) {
					throw new Exception();
				}
				stack.Pop();
				k--;
				tempTable[k][1] = i;
				table[j][0] = tempTable[k][0];
				table[j][1] = tempTable[k][1];			
				j++;
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


	/** 2 + (2 + 2 * 3) * 2   ->   
	 * 2 + 2 2 3 *:T +:T @ * 2   ->   
	 * 2 2 2 3 *:T +:T 2 *:T +:T @
	 * @param buffer
	 * @return
	 */
	CodeString[] ProcessParenthesis(CodeString[] buffer) {
		int[][] parenthesisArray=null;
		try {
			parenthesisArray = CheckParenthesis(buffer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		CodeString[] tempBuffer;
		int srcIndex1, srcIndex2, len;
		for (int i=0; i<parenthesisArray.length; i++) {
			if (parenthesisArray[i][0]==0 && parenthesisArray[i][1]==0)
				break;
			// 괄호 제외 카피
			srcIndex1 = parenthesisArray[i][0] + 1;
			srcIndex2 = parenthesisArray[i][1] - 1;
			len = srcIndex2 - srcIndex1 + 1;
			tempBuffer = Common.Copy(buffer, srcIndex1, len);
			tempBuffer = ConvertInfixToPostfix(tempBuffer);
			// 괄호 포함 제거
			srcIndex1 = parenthesisArray[i][0];
			srcIndex2 = parenthesisArray[i][1];
			len = srcIndex2 - srcIndex1 + 1;
			buffer = Common.Remove(buffer, srcIndex1, len);
			// Postfix를 처음 괄호 위치에 복사
			buffer = Common.Insert(buffer, tempBuffer, srcIndex1);
			// 인덱스 보정
			for (int k=i+1; k<parenthesisArray.length; k++) {
				if (parenthesisArray[k][0]==0 && parenthesisArray[k][1]==0)
					break;
				if (parenthesisArray[k][0]>srcIndex1) {
					parenthesisArray[k][0] -= len-tempBuffer.length;
				}
				if (parenthesisArray[k][1]>srcIndex1) {
					parenthesisArray[k][1] -= len-tempBuffer.length;
				}
			}
		}
		return buffer;
	}

	

	/** 이 함수를 거치면 @가 항상 끝에 하나만 나온다
	 * 2 + (2 + 2 * 3) * 2   -->   
	 * 2 + 2 2 3 *:T +:T @ * 2   -->   
	 * 2 2 2 3 *:T +:T 2 *:T +:T @
	 * @param buffer
	 * @return
	 */
	CodeString[] ConvertInfixToPostfix(CodeString[] tempBuffer) {
		for (int i=0; i<tempBuffer.length && tempBuffer[i]!=null; i++) {			
			if (tempBuffer[i].equals("*") || tempBuffer[i].equals("/")) {
				// 연산자 앞에 괄호안 수식이 있을 때
				if (i-1>=0 && tempBuffer[i-1].equals("@")) {
					tempBuffer = Common.Remove(tempBuffer,i-1,1);
					i = i-1;
				}
				int pos;
				if (i+2<tempBuffer.length && tempBuffer[i+2]!=null) {
					if (CompilerHelper.IsOperator(tempBuffer[i+2]))
						pos = EOB;
						//pos = i+2;
					else {	// 연산자 뒤에 괄호안 수식이 있을 때
						pos = Common.Find(tempBuffer, "@", i+1);
					}
				}
				else {
					pos = EOB;
				}					
				
				tempBuffer[i] = tempBuffer[i].concate(new CodeString(":T", Color.BLACK));
				if (pos==EOB) {
					// 연산자를 세미콜론 앞에
					tempBuffer = Common.Insert(tempBuffer, tempBuffer[i], i+2);
					tempBuffer = Common.Insert(tempBuffer, new CodeString("@",Color.BLACK), i+3);
					tempBuffer = Common.Remove(tempBuffer, i, 1);
					i = i+2;
				}
				else {
					// 연산자를 세미콜론 앞에
					tempBuffer = Common.Insert(tempBuffer, tempBuffer[i], pos);
					tempBuffer = Common.Remove(tempBuffer, i, 1);
					i = pos;
				}
			}
		}
		for (int i=0; i<tempBuffer.length && tempBuffer[i]!=null; i++) {			
			if (tempBuffer[i].equals("+") || tempBuffer[i].equals("-")) {
				
				if (i-1>=0 && tempBuffer[i-1].equals("@")) {
					tempBuffer = Common.Remove(tempBuffer,i-1,1);
					i = i-1;
				}
				int pos;
				if (i+2<tempBuffer.length && tempBuffer[i+2]!=null) {
					if (CompilerHelper.IsOperator(tempBuffer[i+2]))
						pos = EOB;
						//pos = i+2;
					else
						pos = Common.Find(tempBuffer, "@", i+1);
				}
				else {
					pos = EOB;
				}		

				tempBuffer[i] = tempBuffer[i].concate(new CodeString(":T",Color.BLACK));
				if (pos==EOB) {
					tempBuffer = Common.Insert(tempBuffer, tempBuffer[i], i+2);
					tempBuffer = Common.Insert(tempBuffer, new CodeString("@",Color.BLACK), i+3);
					tempBuffer = Common.Remove(tempBuffer, i, 1);
					i = i+2;
				}
				else {
					tempBuffer = Common.Insert(tempBuffer, tempBuffer[i], pos);
					tempBuffer = Common.Remove(tempBuffer, i, 1);
					i = pos;
				}
			}
		}
		return tempBuffer;
			
	}

	CodeString[] RemoveSeparator(CodeString[] buffer) {
		for (int i=0; i<buffer.length && buffer[i]!=null; i++) {
			if (buffer[i].equals("@")) {
				buffer = Common.Remove(buffer, i, 1);
				i = i-1;
			}
		}
		return buffer;
	}

	CodeString[] RemoveFlag(CodeString[] buffer) {
		for (int i=0; i<buffer.length && buffer[i]!=null; i++) {
			if (buffer[i].equals("+:T") || buffer[i].equals("-:T") ||
				buffer[i].equals("*:T") || buffer[i].equals("/:T")) {
					buffer[i] = buffer[i].substring(0, 1);
			}
		}
		return buffer;
	}

			/*boolean IsSeparator(char c)
	        {
	            if (c == '[' || c==']' || c=='{' || c=='}' || c == '(' || c == ')' ||
	                c=='=' || c == ';' || c == ':' || c == ',' || c == '<' || c == '>')
	                return true;
	            return false;
	        }*/

			boolean IsSeparator(char c)
	        {
	            if (c == ';' || c == ':' || c == ',')
	                return true;
	            return false;
	        }

	        boolean IsBlank(char c)
	        {
	            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') return true;
	            return false;
	        }

	        boolean IsBlank(String c)
	        {
	            if (c.equals(" ") || c.equals("\t") || c.equals("\r") || c.equals("\n")) return true;
	            return false;
	        }

	        boolean IsStartParenthesis(char c)
	        {
	            if (c == '(' || c == '{' || c == '[') return true;
	            else return false;
	        }

	        boolean IsEndParenthesis(char c)
	        {
	            if (c == ')' || c == '}' || c == ']') return true;
	            else return false;
	        }

			boolean IsParenthesis(char c) {
				if (IsStartParenthesis(c)) return true;
				if (IsEndParenthesis(c)) return true;
				return false;
			}

			

			/*boolean IsLetterOrDigitOrDot(char c)
			{
				if (char.IsLetterOrDigit(c)) return true;
	            if (c == '.') return true;
	            return false;
			}*/

	/*void ConvertToStringArray() 
	{
				String word;
		        char[] word_wchar = new char[](WordLengthLimit);
		        mBuffer = new String[](StringArrayLimit);
		        int k=0;
		        int i;
	            int countOfmBuffer = 0;

		        for (i=0; i<input.length; i++) {
	                if (IsPlusOrMinus(i))
	                {   // 양이나 음의 부호
	                    word_wchar[k++] = input[i];
	                }
	                if (char.IsDigit(input[i]) || input[i] == '.')
	                {  // 숫자
	                    word_wchar[k++] = input[i];
	                }
	                if (char.IsLetter(input[i]))
	                {  // 변수
	                    word_wchar[k++] = input[i];
	                }

			        if ((input[i]=='+') || (input[i]=='-') || (input[i]=='*') || (input[i]=='/') 
						|| (input[i]=='=') )
					{  // 연산자
	                    if (IsPlusOrMinus(i))
	                    {
	                        // 이미 들어간 부호
	                    }
	                    else
	                    {
	                        if (i > 0 && IsLetterOrDigitOrDot(input[i - 1]))    // 숫자나 변수 넣기
	                        {
	                            word = (new String(word_wchar)).Substring(0, k);
	                            k = 0;
	                            word = word.Trim();
	                            mBuffer[countOfmBuffer++] = word;
	                            word_wchar = new char[](WordLengthLimit);
	                        }
	                        mBuffer[countOfmBuffer++] = "" + input[i];
	                    }
			        }                
			       
			        if (IsSeparator(input[i]) || IsParenthesis(input[i])) {   // Separator, Parenthesis
	                    if (i > 0 && IsLetterOrDigitOrDot(input[i - 1]))    // 숫자나 변수 넣기
	                    {
					        word = (new String(word_wchar)).Substring(0,k);				        
					        word = word.Trim();
					        mBuffer[countOfmBuffer++] = word;
	                        word_wchar = new char[](WordLengthLimit);
	                        k = 0;
				        }
	                    mBuffer[countOfmBuffer++] = "" + input[i];
			        }
			        if (i == input.Length-1) {
	                    if (i > 0 && IsLetterOrDigitOrDot(input[i]))   
	                        // 숫자나 변수로 끝나면 : 구분자가 이미 들어갈 수 있기 때문
	                    {
	                        word = (new String(word_wchar)).Substring(0, k);
	                        word = word.Trim();
	                        mBuffer[countOfmBuffer++] = word;
	                    }
			        }
		        }

				Array.Resize(mBuffer, countOfmBuffer);
	}*/


	void Copy(char[] src, int srcIndex, char[] dest, int destIndex, int len)
	{
		int i;
	    int j = destIndex;
	    for (i = srcIndex; i < srcIndex + len; i++)
	    {
			dest[j++] = src[i];
	    }
	}

	void Copy(String[] src, int srcIndex, String[] dest, int destIndex, int len)
	{
	    int i;
	    int j = destIndex;
	    for (i = srcIndex; i < srcIndex + len; i++)
	    {
	        dest[j++] = src[i];
	    }
	}


	/** 공백, '\n' 등과 주석 등을 제거한다.*/
	void RemoveBlankAndComment(ArrayListCodeString src, ArrayListCodeString result) {
		int i;		 
		for (i = 0; i < src.count; i++)
	    {
			CodeString str = src.getItem(i);
	        if (CompilerHelper.IsBlank(str)) continue;
	        else if (CompilerHelper.IsComment(str)) continue;
	        else {
	        	result.add(str);
	        }
	    }
	}
	
	
	
	/*void CheckLetter()
	{
		int i;
		int len = input.length();
		String letter;
		for (i=0; i<len; ) {
			letter = input[i].ToString();
			if (Common.IsLowAlphabet(input[i]) || Common.IsNumber(letter) || 
				Common.IsOperator(letter) || letter.Equals(this.Separator) || 
				letter.Equals(this.LParenthesis) || letter.Equals(this.RParenthesis) ||
				letter.Equals(".")) {
				i++;			
			}
			else {			
				throw new Exception();
			}
		}
	}*/

	

	void CheckOperatorRule()
	{
		/*try {
			if (mBuffer.length==0 || mBuffer.length==1)
				throw new Exception();
			for (int i=0; i<mBuffer.length && mBuffer[i]!=null; i++) {
				if (Compiler.IsOperator(mBuffer[i])) {
					if (mBuffer[i].equals("=")) {
						if (!Common.IsVarWithoutSign(mBuffer[i-1]))
							throw new Exception();
						if (!Common.IsNumber(mBuffer[i+1]))
							throw new Exception();
						if (!mBuffer[i+2].equals(";"))
							throw new Exception("Separator Not Exist");
					}
					else {
						if (mBuffer[i-1].equals(")") && mBuffer[i+1].equals("("))
							continue;
						if (Common.IsNumber(mBuffer[i-1]) && mBuffer[i+1].equals("("))
							continue;
						if (Common.IsVar(mBuffer[i-1]) && mBuffer[i+1].equals("("))
							continue;
						if (mBuffer[i-1].equals(")") && Common.IsNumber(mBuffer[i+1]))
							continue;
						if (mBuffer[i-1].equals(")") && Common.IsVar(mBuffer[i+1]))
							continue;
						if (!Common.IsNumber(mBuffer[i-1]) && !Common.IsVar(mBuffer[i-1]))
							throw new Exception();				
						if (!Common.IsNumber(mBuffer[i+1]) && !Common.IsVar(mBuffer[i+1]))
							throw new Exception();
					}			
				}
				else if (mBuffer[i].equals(";")) {
					if (!Common.IsVarWithoutSign(mBuffer[i-3]))
						throw new Exception();
					if (!mBuffer[i-2].equals("="))
						throw new Exception();
					if (!Common.IsNumber(mBuffer[i-1]))
						throw new Exception();
				}
			}
		}
		catch (Exception e) {
			throw e;
		}*/
	}
	
	
	static class Common {

		static CodeString[] Insert(CodeString[] input, CodeString c, int pos) {
			if (input==null || c==null)
				return null;
			CodeString[] result;
			if (pos>=input.length) {
				result = new CodeString[pos+1];
				CopyTo(input, result, 0);
				result[pos] = c;
			}
			else {
				result = new CodeString[input.length+1];
				for (int i=0, k=0; i<input.length; i++) {
					if (i==pos) {
						result[k] = c;
						k++;
						if (input[i]!=null) {
							result[k] = input[i];
							k++;
						}
					}
					else {
						if (input[i]!=null) {
							result[k] = input[i];
							k++;
						}
					}
				}
			}
			
			//if (input)
			//	delete input;
			return result;
		}
		
		static void CopyTo(CodeString[] srcArray, CodeString[] destArray, int pos) {
			int i, j;
			for (i=pos, j=0; i<pos+srcArray.length; i++) {
				destArray[i] = srcArray[j];
				j++;
			}
		}

		static CodeString[] Insert(CodeString[] destArray, CodeString[] srcArray, int pos) {
			if (destArray==null || srcArray==null || pos<0)
				return null;
			CodeString[] result;
			if (pos>=destArray.length) {
				result = new CodeString[pos+srcArray.length];
				CopyTo(destArray, result, 0);
				CopyTo(srcArray, result, pos);
			}
			else {
				result = new CodeString[destArray.length+srcArray.length];
				for (int i=0, k=0; i<destArray.length; i++) {
					if (i==pos) {
						CopyTo(srcArray, result, pos);
						k = k + srcArray.length;
						if (destArray[i]!=null) {
							result[k] = destArray[i];
							k++;
						}
					}
					else {
						if (destArray[i]!=null) {
							result[k] = destArray[i];
							k++;
						}
					}
				}
			}
			
			//if (destArray)
			//	delete destArray;
			return result;

		}


		static int Find(CodeString[] input, String c, int index) {
			if (input==null)
				return EOB;
			int i;
			for (i=index; i<input.length && input[i]!=null; i++) {
				if (input[i].equals(c))
					return i;
			}
			return EOB;
		}

		/*static int Find(CodeString[][] input, CodeString c, int colIndex) {
			if (input==null)
				return EOB;
			if (colIndex>1)
				return EOB;
			int i;
			String val = c.Trim();
			for (i=0; i<input.length && input[i][0]!=null; i++) {
				if (Equals(input[i][colIndex],val))
					return i;
			}
			return EOB;
		}*/

		static boolean Equals(String str1, String str2) {
			if (str1.length()!=str2.length()) {
				//System.Windows.Forms.MessageBox.Show("str1:"+str1.Length+"  "+"str2:"+str2.Length);
				return false;
			}
			int i;
			for (i=0; i<str1.length(); i++) {
				if (str1.charAt(i)!=str2.charAt(i)) 
					return false;
			}
			return true;
		}

		static CodeString[] Remove(CodeString[] input, int pos, int count) {
			if (input==null)
				return null;
			if (pos+count>input.length)
				return null;
			CodeString[] result = new CodeString[input.length-count];
			for (int i=0, k=0; i<input.length; i++) {
				if (pos<=i && i<=pos+count-1) continue;
				result[k] = input[i];
				k++;
			}
			return result;
		}

		static CodeString[] Copy(CodeString[] srcArray, int srcIndex, int len)
		{
			if (srcArray==null)
				return null;
			if (srcIndex+len>srcArray.length)
				return null;
			CodeString[] result = new CodeString[len];
			for (int i=srcIndex, k=0; i<srcIndex+len && srcArray[i]!=null; i++) {
				result[k] = srcArray[i];
				k++;
			}
			return result;
		}

		/*static boolean IsOperator(String str) {
			if (str==null)
				return false;
			if (str.equals("+") || str.equals("-") || str.equals("*") || 
				str.equals("/") || str.equals("="))
				return true;
			return false;
		}

		static boolean IsNumber(String str) {
			try {
				Single.Parse(str);
				return true;
			}
			catch(Exception) {
				return false;
			}
			
		}

		static boolean IsLowAlphabet(wchar_t ch) {
			if ('a'<=ch && ch<='z')
				return true;
			return false;
		}

		static boolean IsVar(String str) {
			if (str==null)
				return false;
			if (IsOperator(str))
				return false;
			if (str[0]=='\0')
				return false;
			int i;
			if (!IsLowAlphabet(str[0])) {
				if (str[0]=='+' || str[0]=='-') {
					if (!IsLowAlphabet(str[1])) 
						return false;
					for (i=2; i<str.length && str[i]!='\0'; i++) {
						if (char.IsDigit(str[i]) || (IsLowAlphabet(str[i])))
							continue;
						else 
							return false;
					}
					return true;
				}
				else {
					return false;
				}
			}
			else {
				for (i=1; i<str.length && str[i]!='\0'; i++) {
					if (char.IsDigit(str[i]) || (IsLowAlphabet(str[i])))
						continue;
					else 
						return false;
				}
				return true;
			}
		}

		static boolean IsVarWithoutSign(String str) {
			if (str==null)
				return false;
			if (IsOperator(str))
				return false;
			if (str[0]=='\0')
				return false;
			int i;
			if (!IsLowAlphabet(str[0])) {
				return false;
			}
			else {
				for (i=1; i<str.length && str[i]!='\0'; i++) {
					if (char.IsDigit(str[i]) || (IsLowAlphabet(str[i])))
						continue;
					else 
						return false;
				}
				return true;
			}
		}*/
		
	}


}
