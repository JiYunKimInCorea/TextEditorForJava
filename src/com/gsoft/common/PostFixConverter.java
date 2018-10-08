package com.gsoft.common;

import android.graphics.Color;
import com.gsoft.common.CompilerHelper;
import com.gsoft.common.Code.CodeChar;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Compiler_types.*;
import com.gsoft.common.Compiler.GetVarUseWithIndexReturnType;
import com.gsoft.common.Compiler_types.TypeCast;
import com.gsoft.common.Util.ArrayListInt;
import com.gsoft.common.Compiler_types.HighArray_CodeString;
import com.gsoft.common.Util.Stack;
import com.gsoft.common.Util.ArrayListIReset;
import com.gsoft.common.Util.ArrayListCodeString;

public class PostFixConverter {
	
		
	//String[] input;
	CodeStringEx[] mBuffer;
	CodeStringEx[] output;
	
	HighArray_CodeString srcOld;
	FindExpressionParams expressionOld;
	ArrayListIReset listOfVarUsesOld;
	
	/** 실제 원소 타입은 CodeStringEx이다. constructor참조*/
	HighArray_CodeString src;
	FindExpressionParams expression;
	ArrayListIReset listOfVarUses;
	
	static final int EOB = -1;
	
	/** "!", "~", "+", "-"*/
	String[] operatorsForOne = {"!", "~", "+", "-"};
	
	/** "^"*/
	//String[] operatorsPower = {"^"};
	
	/** "*", "/", "%"*/
	String[] operatorsMultipliers = {"*", "/", "%"};
	
	/** "+", "-"*/
	String[] operatorsAdders = {"+", "-"};
	
	/** "<<", ">>", "<<<", ">>>"*/
	String[] operatorsShifters = {"<<", ">>", "<<<", ">>>"};
	
	/** "<", ">", "<=", ">="*/
	String[] operatorsRelations = {"<", ">", "<=", ">="};
	
	/** "==", "!="*/
	String[] operatorsEquals = {"==", "!="};
	
	/** instanceof */
	String[] operatorsInstanceof = {"instanceof"};
	
	/** "&", "|", "~"*/
	String[] operatorsBitLogical = {"&", "|", "~", "^"};
	
	/** "&&", "!"*/
	String[] operatorsLogical1 = {"&&", "!"};
	/** "||"*/
	String[] operatorsLogical2 = {"||"};
	
	/** ":", 삼항연산자 ? : 의 ":"을 말한다. */
	String[] operatorsChoose = {":"};
	
	/** "=", "+=", "-=", "*=", "/=", "%=", "|=", "&=" */
	String[] operatorsAllocater = {"=", "+=", "-=", "*=", "/=", "%=", "~=", "|=", "&=", "^="};
	
	
	ArrayListIReset listOfBlocks = new ArrayListIReset(0);
	ArrayListIReset listOfSmallBlocks = new ArrayListIReset(2);
	ArrayListIReset listOfLargeBlocks = new ArrayListIReset(0);
	private Compiler compiler;
	
	/** 주석, 공백 등이 제거된다.*/
	PostFixConverter(Compiler compiler, HighArray_CodeString src, FindExpressionParams expression)
	{
		this.compiler = compiler;
		//this.input = input;
		output = null;
		/*this.srcOld = src;
		this.expressionOld = expression;
		this.listOfVarUsesOld = expression.listOfVarUses;*/
		
		this.src = src;
		int i;
		this.src = new HighArray_CodeString(expression.endIndex()-expression.startIndex()+1);
		int startIndex = 0;
		ArrayListIReset listOfVarUses = null;
		if (562<=expression.startIndex() && expression.startIndex()<=563) {
			int a;
			a=0;
			a++;
		}
		for (i=expression.startIndex(); i<=expression.endIndex(); i++) {
			CodeString strSrc = src.getItem(i);
			if (strSrc.equals("new")) continue;
			
			if (CompilerHelper.IsBlank(strSrc) || CompilerHelper.IsComment(strSrc)) continue;
			
			ArrayListInt indicesInSrc = new ArrayListInt(1);
			indicesInSrc.add(i);
			
			listOfVarUses = new ArrayListIReset(1);
			GetVarUseWithIndexReturnType r = 
				compiler.getVarUseWithIndex(compiler.mlistOfAllVarUses, startIndex, i);			
			if (r!=null) {
				listOfVarUses.add(r.r);
				startIndex = r.indexInlistOfAllVarUses+1;
			}
			else listOfVarUses.add(null);
			CodeStringEx str = new CodeStringEx(strSrc.str, Color.BLACK, indicesInSrc, listOfVarUses);
			str.setType(strSrc.charAt(0).type);
			this.src.add(str);
		}
		
		this.expression = expression;
		this.listOfVarUses = listOfVarUses;
		//mBuffer = input;
		
		
		//Compiler.CheckParenthesisAll(this.src, listOfBlocks, listOfSmallBlocks, listOfLargeBlocks);
	}
	
	static class IndexStartEnd implements IReset {
		int startIndex;
		int endIndex;
		IndexStartEnd(int startIndex, int endIndex) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
		}
	}
	
	/** startIndex, endIndex는 fullname 인덱스의 시작과 끝 인덱스이고 이 영역밖으로 가짜 괄호들의 인덱스들을 리턴한다.*/
	ArrayListIReset getFakeParenthesis(HighArray_CodeString src, int startIndex, int endIndex) {
		int i;
		ArrayListIReset r = new ArrayListIReset(5);
		for (i=startIndex-1; i>=0; i--) {
			CodeStringEx str = (CodeStringEx) src.getItem(i);
			int inc = startIndex - i;
			if (str.equals("(")) {
				CodeStringEx rightPair = (CodeStringEx) src.getItem(endIndex+inc);
				try {
				if (rightPair.equals(")")) { // fake Parenthesis
					r.add(new IndexStartEnd(i,endIndex+inc));
				}
				}catch(Exception e) {
					int a;
					a=0;
					a++;
				}
			}
			else {
				break;
			}
		}
		return r;
	}
	
	boolean isTypeCast(CodeStringEx str) {
		ArrayListIReset listOfTypeCasts = compiler.mlistOfAllTypeCasts;
		int i;
		int indexOfOriginalSrc = str.indicesInSrc.getItem(0);
		if (indexOfOriginalSrc==2234) {
			int a;
			a=0;
			a++;
		}
		for (i=0; i<listOfTypeCasts.count; i++) {
			TypeCast typeCast = (TypeCast) listOfTypeCasts.getItem(i);
			if (typeCast.startIndex()<=indexOfOriginalSrc && indexOfOriginalSrc<=typeCast.endIndex())
				return true;
		}
		return false;
		
	}
	
	/** 함수호출, 네임스페이스(멤버연산자를 이용한 멤버접근), 변수, 상수 등 토큰 하나에 붙어있는 괄호를 제거한다.
	 * 예를들어 (a.b.c())+3, (a)+2, (a)[0]+2, (a[0])+2 등에 있는 괄호를 제거한다.*/
	void removeFakeParenthesis(HighArray_CodeString src, HighArray_CodeString result) {		
		int i;
		
		// 원래 소스코드상의 Fullname의 시작과 끝 인덱스를 말한다.
		ArrayListIReset indicesOfParenthesis = new ArrayListIReset(10);
				
		for (i=0; i<src.count; i++) {
			CodeStringEx str = (CodeStringEx) src.getItem(i);
			if (str.equals("bounds")) {
				int a;
				a=0;
				a++;
			}
			
			if (compiler.IsIdentifier(str) || CompilerHelper.IsNumber2(str)!=0) {
				if (isTypeCast(str)) continue;
				
				int indexTemp = compiler.SkipBlank(src, false, i, src.count-1);
				int fullNameIndex = compiler.getFullNameIndex2(src, indexTemp);
				if (fullNameIndex==src.count) fullNameIndex = src.count-1;
				fullNameIndex = compiler.SkipBlank(src, true, 0, fullNameIndex);
				ArrayListIReset r = getFakeParenthesis(src, i, fullNameIndex);
				for (int j=0; j<r.count; j++) {
					indicesOfParenthesis.add(r.getItem(j));
				}
				if (r.count>0) {
					IndexStartEnd last = (IndexStartEnd) r.getItem(r.count-1);
					i = last.endIndex; // 마지막 가짜 괄호의 인덱스
				}
				else {
					i = fullNameIndex;
				}
			}
		}
		
		for (i=0; i<src.count; i++) {
			CodeStringEx str = (CodeStringEx) src.getItem(i);
			if (str.equals("(")) {
				boolean b = exists(indicesOfParenthesis, i, true);
				if (b==false) {
					result.add(str);
				}
			}
			else if (str.equals(")")) {
				boolean b = exists(indicesOfParenthesis, i, false);
				if (b==false) {
					result.add(str);
				}
			}
			else {
				result.add(str);
			}
		}
	}
	
	boolean exists(ArrayListIReset indices, int targetIndex, boolean leftOrRight) {
		if (leftOrRight) {
			int i;
			for (i=0; i<indices.count; i++) {
				IndexStartEnd index = (IndexStartEnd) indices.getItem(i);
				if (index.startIndex==targetIndex) return true;
			}
		}
		else {
			int i;
			for (i=0; i<indices.count; i++) {
				IndexStartEnd index = (IndexStartEnd) indices.getItem(i);
				if (index.endIndex==targetIndex) return true;
			}	
		}
		return false;
	}
	
		
	/** 풀네임이 하나의 스트링으로 변환된 새로운 스트링 배열을 만든다. 
	 * i++, i--와 같은 경우와 복합연산자(<=, >=, ==, !=)들도 여기서 하나의 스트링으로 합쳐진다.*/
	void ProcessFullnames2(HighArray_CodeString src, HighArray_CodeString result) {
		int i;
		
		// 원래 소스코드상의 Fullname의 시작과 끝 인덱스를 말한다.
		ArrayListIReset indicesOfFullnames = new ArrayListIReset(10);
		
		int startIndex=-1, endIndex=-1;
		boolean isTemplateLeftPairOrOperator = true;
		// 변수사용이나 함수호출의 풀네임의 인덱스를 만든다.
		for (i=0; i<src.count; i++) {
			CodeStringEx str = (CodeStringEx) src.getItem(i);
			if (str.equals("a")) {
				int a;
				a=0;
				a++;
			}
			if (i==0) {
				startIndex = i;
				//i++;
			}
			
			
			if ( (str.equals("+") && (i+1<src.count && ((CodeStringEx)src.getItem(i+1)).equals("+"))) || 
					(str.equals("-") && (i+1<src.count && ((CodeStringEx)src.getItem(i+1)).equals("-"))) ) {
				int indexId = compiler.SkipBlank(src, false, i+2, src.count-1);
				
				if (indexId<=src.count-1 && compiler.IsIdentifier(src.getItem(indexId))) { // ++i, --i
					if (indexId+1>=src.count ||
							(indexId+1<src.count && (src.getItem(indexId+1).equals("[")==false && src.getItem(indexId+1).equals("(")==false))) {
						// endIndex는 id의 인덱스
						endIndex = indexId;
						if (startIndex<=endIndex) {
							indicesOfFullnames.add(new IndexStartEnd(startIndex, endIndex));
						}						
						// startIndex는 id의 인덱스+1
						startIndex = endIndex+1;
						// i는 id의 인덱스
						i = startIndex-1;				
						//continue;
					}
					else {
						if (indexId+1<src.count && src.getItem(indexId+1).equals("[")) {
							int rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", indexId+1, src.count-1, false);
							if (rightPair!=-1) {					
								i = rightPair;
							}
							// a = ++a[b[++k]]; 에서 id가 배열참조 a일 경우
							endIndex = rightPair;
							if (startIndex<=endIndex) {
								indicesOfFullnames.add(new IndexStartEnd(startIndex, endIndex));
							}
							startIndex = endIndex+1;
							i = startIndex-1;
						}
					}
				}//if (indexId<=src.count-1 && compiler.IsIdentifier(src.getItem(indexId))) { // ++i, --i
				else {
					// i는 첫번째 +의 인덱스
					indexId = compiler.SkipBlank(src, true, 0, i-1);
					if (indexId==-1) {
						return;
					}
					if (compiler.IsIdentifier(src.getItem(indexId))) { // i++, i--
						// endIndex는 i++의 두번째 +의 인덱스
						endIndex = i+1;
						if (startIndex<=endIndex)
							indicesOfFullnames.add(new IndexStartEnd(startIndex, endIndex));
						
						// startIndex는 i++의 다음 인덱스
						startIndex = endIndex+1;
						// i는 i++의 두번째 +의 인덱스
						i = startIndex-1;
						
					}
					else if (src.getItem(indexId).equals("]")) {// a[k++]++
						int leftPair = CompilerHelper.CheckParenthesis(src, "[", "]", 0, indexId, true);
						if (leftPair==-1) {
							return;
						}
						int idIndex = compiler.SkipBlank(src, true, 0, leftPair-1);
						if (idIndex==-1) {
							return;
						}
						if (compiler.IsIdentifier(src.getItem(idIndex))) {
							// i는 첫번째 +의 인덱스
							endIndex = i+1;
							if (startIndex<=endIndex)
								indicesOfFullnames.add(new IndexStartEnd(startIndex, endIndex));
							
							// startIndex는 a[k++]++의 다음 인덱스
							startIndex = endIndex+1;
							// i는 a[k++]++의 두번째 +의 인덱스
							i = startIndex-1;
						}
						
					}//else if (src.getItem(indexId).equals("]")) {// a[k++]++
					else { // -1.0f + +(2.0f) 에서 첫번째 +인 경우
						indicesOfFullnames.add(new IndexStartEnd(i, i)); // 연산자 넣기				
						startIndex = i+1;
					}
				}
			}
			
			else if (str.equals("<") && isTemplateLeftPairOrOperator==true) {
				Template t = null;
				int rightPairIndex = CompilerHelper.CheckParenthesis(src, "<", ">", i, src.count-1, false);
				if (rightPairIndex!=-1) {
					t = compiler.isTemplate(src, rightPairIndex);
					if (t!=null) {
						i = rightPairIndex;						
						continue;
					}
				}
				if (t==null) {
					isTemplateLeftPairOrOperator = false;
					i--;
					continue;
					
				}
				
			}
			else if (str.equals("(")) { // 건너뛰기, ((new RectForPage(owner, bounds, backColor, isUpOrDown))).a
				boolean isFuncCall = false;
				int leftPair = i;
				int indexID = compiler.SkipBlank(src, true, 0, leftPair-1);
				// 템플릿인지를 확인해야 한다. stack = new Stack<Block>();
				if (indexID!=-1 && src.getItem(indexID).equals(">")) {
					Template t = compiler.isTemplate(src, indexID);
					int leftPairIndexTemplate = t.indexLeftPair();
					if (t!=null) {
						leftPair = leftPairIndexTemplate;
					}
				}
				
				// 템플릿 함수이든 일반함수이든
				indexID = compiler.SkipBlank(src, true, 0, leftPair-1);
				if (indexID!=-1 && compiler.IsIdentifier(src.getItem(indexID))) { 
					// 함수호출, f(a, b).c + 2
					isFuncCall = true;
				}
				
				// ((new RectForPage(owner, bounds, backColor, isUpOrDown))).a
				int rightPair = CompilerHelper.CheckParenthesis(src, "(", ")", leftPair, src.count-1, false);
				if (rightPair==-1) i = src.count; // error, ) not exist
				else {
					if (isFuncCall) {
						i = rightPair;
						//continue;
					}
					else {
						boolean mustMerge = false;
						int dotIndex = compiler.SkipBlank(src, false, rightPair+1, src.count-1);
						if (dotIndex!=src.count && src.getItem(dotIndex).equals(".")) { // 건너뛰기
							// ((new RectForPage(owner, bounds, backColor, isUpOrDown))).a
							mustMerge = true;
							i = rightPair;
							continue;
						}
						
						// 타입캐스트문이나 일반수식의 괄호, 단 fake 괄호는 제거된 상태이다.
						if (mustMerge==false) { // ( 만 독립적으로 넣는다. 건너뛰지 않는다.
							// 기존 토큰을 넣는다.
							endIndex = i-1;
							if (endIndex>=0 && startIndex<=endIndex)
								indicesOfFullnames.add(new IndexStartEnd(startIndex, endIndex));
							// (을 넣는다.
							startIndex = i;
							endIndex = i;
							if (startIndex<=endIndex)
								indicesOfFullnames.add(new IndexStartEnd(startIndex, endIndex));
							
							startIndex = endIndex+1;
							i = startIndex-1;
							//continue;
						}
						
						// 타입캐스트문이나 일반수식의 괄호, fake 괄호는 제거된 상태이다.
					
						
					}//else if (isFuncCall) {
				}
			}//else if (str.equals("(")) {
			else if (str.equals("[")) {
				int rightPair = CompilerHelper.CheckParenthesis(src, "[", "]", i, src.count-1, false);
				if (rightPair!=-1) {					
					i = rightPair;
				}
				else { // error, ] not exist
					break;
				}
			}
			else if (str.equals(")") || str.equals("]")) { 
				// ((new RectForPage(owner, bounds, backColor, isUpOrDown))).a+(2+1+3)*(1+0)
				endIndex = i-1;
				if (startIndex<=endIndex)
					indicesOfFullnames.add(new IndexStartEnd(startIndex, endIndex));  // 예제에서 3넣기
				
				indicesOfFullnames.add(new IndexStartEnd(i, i)); // 괄호 넣기
				
				startIndex = i+1;
				//i++;
			}
			
			else if (CompilerHelper.IsOperator(str)) // 이항연산자
			{  // a + b 에서 a 까지, ((new RectForPage(owner, bounds, backColor, isUpOrDown))).a+(2+1+3)*(1+0)
				if (str.equals("<")) {
					isTemplateLeftPairOrOperator = true; // 다시 초기화
				}
				// 기존 토큰을 넣는다.
				endIndex = i-1;
				if (endIndex>=0 && startIndex<=endIndex)
					indicesOfFullnames.add(new IndexStartEnd(startIndex, endIndex));
				
				CodeStringEx next = null;
				if (i+1<src.count) next = (CodeStringEx) src.getItem(i+1);
				if (next!=null && CompilerHelper.IsOperator(next)) { // 복합연산자
					CodeStringEx nextOfNext = null;
					if (i+2<src.count) nextOfNext = (CodeStringEx) src.getItem(i+2);
					
					String operator3 = str.concate(next).concate(nextOfNext).str;
					//if (nextOfNext!=null && CompilerHelper.IsOperator(nextOfNext)) { // <<<, >>> 등 연산자가 3개인 경우
					if (operator3.equals("<<<") || operator3.equals(">>>")) {
						// <<<, >>> 인 경우
						if (CompilerHelper.IsCompositiveOperator(str, next, nextOfNext)) {
							indicesOfFullnames.add(new IndexStartEnd(i, i+2)); // 연산자 넣기					
							startIndex = i+3;
							i += 2;
						}
						else {
							// if (true && !false)에서 nextOfNext가 !인 경우
							if (CompilerHelper.IsCompositiveOperator(str, next)) {
								indicesOfFullnames.add(new IndexStartEnd(i, i+1)); // 연산자 넣기					
								startIndex = i+2;
								i++;
							}
							// 연산자가 세개가 연속으로 나오면서 그밖의 경우
							else {
								indicesOfFullnames.add(new IndexStartEnd(i, i)); // 연산자 넣기				
								startIndex = i+1;
							}
						}						
					}//연산자가 3개인 경우
					else { 
						// <=, >=, >>, <<등 연산자가 2개인 경우
						if (CompilerHelper.IsCompositiveOperator(str, next)) {
							indicesOfFullnames.add(new IndexStartEnd(i, i+1)); // 연산자 넣기					
							startIndex = i+2;
							i++;
						}
						//-1.0f + -(2.0f), -1.0f - -(2.0f) 이와 같은경우
						// if (1 & ~2)에서 next 가 ~인 경우
						else {
							indicesOfFullnames.add(new IndexStartEnd(i, i)); // 연산자 넣기				
							startIndex = i+1;
						}
					}
				}//if (next!=null && CompilerHelper.IsOperator(next)) {
				else {		// 연산자가 1개인 경우		
					indicesOfFullnames.add(new IndexStartEnd(i, i)); // 연산자 넣기				
					startIndex = i+1;
				}
				//i++;
			}//else if (CompilerHelper.IsOperator(str))
			else { // '.' 포함 기타 토큰
				//i++;
			}
			
			if (i==src.count-1) {
				endIndex = i;
				if (startIndex<=endIndex)
					indicesOfFullnames.add(new IndexStartEnd(startIndex, endIndex));
				//i++;
			}
			
			
		} // for i
		
		if (src.count>0 && src.getItem(0).equals("\"abc\"")) {
			int a;
			a=0;
			a++;
		}
		
		
		// 풀네임이 하나의 스트링으로 변환된 새로운 스트링 배열을 만든다.
		if (indicesOfFullnames.count==1) {
			IndexStartEnd index = (IndexStartEnd)indicesOfFullnames.getItem(0); 
			for (i=0; i<index.startIndex; i++) {
				result.add(src.getItem(i));
			} // for i
			
			ArrayListInt indicesInSrc = ((CodeStringEx)src.getItem(index.startIndex)).indicesInSrc;
			ArrayListIReset listOfVarUses = ((CodeStringEx)src.getItem(index.startIndex)).listOfVarUses;
			CodeStringEx fullname = new CodeStringEx(src.getItem(index.startIndex).str, Color.BLACK, indicesInSrc, listOfVarUses);
			fullname.setType(src.getItem(index.startIndex).charAt(0).type);
			
			for (i=index.startIndex+1; i<=index.endIndex; i++) {
				// str과 인덱스는 일대일 대응한다.
				CodeStringEx str = (CodeStringEx) src.getItem(i);
				fullname = (CodeStringEx) fullname.concate(str, str.indicesInSrc.getItem(0), 
						(FindVarUseParams)str.listOfVarUses.getItem(0));
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
				
				ArrayListInt indicesInSrc = ((CodeStringEx)src.getItem(curIndex.startIndex)).indicesInSrc;
				ArrayListIReset listOfVarUses = ((CodeStringEx)src.getItem(curIndex.startIndex)).listOfVarUses;
				CodeStringEx fullname = new CodeStringEx(src.getItem(curIndex.startIndex).str, Color.BLACK, indicesInSrc, listOfVarUses);
				fullname.setType(src.getItem(curIndex.startIndex).charAt(0).type);
				
				for (i=curIndex.startIndex+1; i<=curIndex.endIndex; i++) {
					CodeStringEx str = (CodeStringEx) src.getItem(i);
					fullname = (CodeStringEx) fullname.concate(str, str.indicesInSrc.getItem(0), 
							(FindVarUseParams)str.listOfVarUses.getItem(0));
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
	
	
	
	/** @param startIndex : (의 인덱스
	 *  @param endIndex : )의 인덱스
	 * @return 타입의 끝 인덱스를 리턴한다. 타입이 아닌것(상수, 구분자 등)을 만나면 그 인덱스-1을 리턴한다. 
	 */
	int IsType(Compiler compiler, HighArray_CodeString src, int startIndex, int endIndex) {
		int i;
		for (i=startIndex+1; i<endIndex; i++) {
			CodeString str = src.getItem(i);
			if (str.equals("bounds.x")) {
				int a;
				a=0;
				a++;
			}
			if (compiler.IsDefaultType(str) || compiler.IsIdentifier(str)) {
				// charATextLine = (new TextLine(charA, fontSize)); 에서 startIndex가 첫번째 (을 가리키는 경우
				// src는 '(', 'TextLine(charA, fontSize)', ')'이다.
				if (str.str.contains("(")) return i-1;
				else {
					if (str.str.contains(".")) {
						// clipRect.left = (int) (bounds.x); 에서 bounds.x를 가리키고 이것은 스트링 하나이다.
						HighArray_CodeString backupMBuffer = compiler.mBuffer;
						compiler.mBuffer = null;
						HighArray_CodeString result = new StringTokenizer().ConvertToStringArray2(str, 20, Language.Java);
						//String typeName = compiler.getFullNameType(compiler, 0, result.count-1);
						String typeName = compiler.getFullName(result, 0, result.count-1).str;
						compiler.mBuffer = backupMBuffer;
						if (typeName==null) return i-1;
					}
				}
				// 배열과 템플릿인 경우는 다음에 생각해본다.
				return i;
			}
			else if (CompilerHelper.IsConstant(str)) return i-1;
			else if (CompilerHelper.IsSeparator(str)) return i-1;
			else if (CompilerHelper.IsBlank(str) || CompilerHelper.IsAnnotation(str)) continue;
			
		}
		
		
		return -1;
		
	}
	
	/** (타입)id, (타입)(수식) 와 같은 타입캐스팅이 하나의 스트링으로 변환된 새로운 스트링 배열을 만든다.*/
	void ProcessTypecastings2(HighArray_CodeString src, ArrayListCodeString result) {
		
		int i;
		
		// 원래 소스코드상의 Typecasting의 시작과 끝 인덱스를 말한다.
		ArrayListIReset indicesOfTypecastings = new ArrayListIReset(10);
		
		int startIndex=-1, endIndex=-1;
		int s, e;
		int leftParent, rightParent;
		for (i=0; i<src.count; ) {
			CodeStringEx str = (CodeStringEx) src.getItem(i);
			if (str.equals("(")) {
				int a;
				a=0;
				a++;
			}
			if (str.equals("(")) {
				leftParent = i;
				
				rightParent = CompilerHelper.CheckParenthesis(src, "(", ")", leftParent, src.count-1, false);
				if (rightParent!=-1) {
					//int typeIndex = compiler.IsType(src, false, leftParent+1, null);
					int typeIndex = IsType(compiler, src, leftParent, rightParent);
					if (typeIndex!=-1) {
						int tempRightPair = compiler.SkipBlank(src, false, typeIndex+1, src.count-1);
						if (tempRightPair==rightParent) { // 괄호안은 타입캐스트문
							// RectForPage.test((rectForPageLeft), 3);에서 (rectForPageLeft)을 말한다.
							if (src.count<=rightParent+1) {
								i++;
								continue;
							}
							CodeString id = src.getItem(rightParent+1);
							try {
							if ( compiler.IsIdentifier( id ) || CompilerHelper.IsConstant(id)) { // (타입)id
								startIndex = leftParent;
								endIndex = rightParent+1;
								indicesOfTypecastings.add(new IndexStartEnd(startIndex, endIndex));
								i = endIndex+1;
								continue;
							}
							else if (id.equals("(")) { // (타입)(수식)
								int rightParent2 = CompilerHelper.CheckParenthesis(src, "(", ")", rightParent+1, src.count-1, false);
								startIndex = leftParent;
								endIndex = rightParent2;
								indicesOfTypecastings.add(new IndexStartEnd(startIndex, endIndex));
								i = endIndex+1;
								continue;
							}
							else {
								i++;
								continue;
							}
							}catch(Exception e1) {
								int a;
								a=0;
								a++;
							}
						}//if (tempRightPair==rightParent) { // 괄호안은 타입캐스트문
						else {
							i++;
							continue;
						}
					}//if (typeIndex!=-1) {
					else {
						i++;
						continue;
					}
				} // if (rightParent!=-1) {
				else {
					i++;
					continue;
				}
				
			} // if (str.equals("(")) {
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
			
			/*ArrayListInt indicesInSrc = ((CodeStringEx)src.getItem(index.startIndex)).indicesInSrc;
			ArrayListIReset listOfVarUses = ((CodeStringEx)src.getItem(index.startIndex)).listOfVarUses;
			CodeStringEx typecasting = new CodeStringEx(src.getItem(index.startIndex).str, Color.BLACK, indicesInSrc, listOfVarUses);
			for (i=index.startIndex+1; i<=index.endIndex; i++) {
				CodeStringEx str = (CodeStringEx) src.getItem(i);
				//typecasting = (CodeStringEx) typecasting.concate(str, str.indicesInSrc.getItem(0), 
				//		(FindVarUseParams)str.listOfVarUses.getItem(0));
				typecasting = typecasting.concate(str);
			} // for i
			result.add(typecasting);*/
			
			
			CodeStringEx typecasting = (CodeStringEx) src.getItem(index.startIndex);
			for (i=index.startIndex+1; i<=index.endIndex; i++) {
				CodeStringEx str = (CodeStringEx) src.getItem(i);
				typecasting = typecasting.concate(str);
			}
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
				/*IndexStartEnd curIndex = (IndexStartEnd)indicesOfTypecastings.getItem(j);
				
				ArrayListInt indicesInSrc = ((CodeStringEx)src.getItem(index.startIndex)).indicesInSrc;
				ArrayListIReset listOfVarUses = ((CodeStringEx)src.getItem(index.startIndex)).listOfVarUses;
				CodeStringEx typecasting = new CodeStringEx(src.getItem(index.startIndex).str, Color.BLACK, indicesInSrc, listOfVarUses);
				for (i=curIndex.startIndex+1; i<=curIndex.endIndex; i++) {
					CodeStringEx str = (CodeStringEx) src.getItem(i);
					//typecasting = (CodeStringEx) typecasting.concate(str, str.indicesInSrc.getItem(0), 
					//		(FindVarUseParams)str.listOfVarUses.getItem(0));
					typecasting = typecasting.concate(str);
				} // for i
				result.add(typecasting);*/
				
				IndexStartEnd curIndex = (IndexStartEnd)indicesOfTypecastings.getItem(j);
				CodeStringEx typecasting = (CodeStringEx) src.getItem(curIndex.startIndex);
				for (i=curIndex.startIndex+1; i<=curIndex.endIndex; i++) {
					CodeStringEx str = (CodeStringEx) src.getItem(i);
					typecasting = typecasting.concate(str);
				}
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
	
	/** ArrayListIReset listOfVarUses(구분자이면 null, 변수나 함수호출이면 해당 varUse를 참조한다) 멤버를 갖는다. */
	static class CodeStringEx extends CodeString implements IReset {
		/** 소스상에서의 인덱스*/
		ArrayListInt indicesInSrc;
		/** 구분자이면 null, 변수나 함수호출이면 해당 varUse를 참조한다.*/
		ArrayListIReset listOfVarUses;
		/** 토큰의 fullname 타입, getTypeOfVarUseOrFuncCallOfFullName에서 정해진다.*/
		String typeFullName;
		
		/** +, -가 일항연산자인 경우 이항연산자와 구분하기 위해 사용된다. 
		 * 일항연산자이면 true, 아니면 false 이다. processOperatorForOne()에서 정해진다.
		 */
		boolean isPlusOrMinusForOne;
		
		/** 값을 말한다. byte b = 1+127; 에서 1, 127은 value를 갖게 되며, 
		 * CompilerHelper.getTypeOfOperator()의 allowsOperator()에서 연산결과의 타입을 갖게된다.*/
		String value;
		
		/** this.backColor의 경우 'this', '.', 'backColor'가 된다.*/
		HighArray_CodeString arrayListCodeStringForToken;
		
		/** 묵시적 타입캐스팅, CompilerHelper.getTypeOfOperator()에서 정해진다.
		 * 예를들어 f = i + f;에서 i는 float로 타입캐스트된다.
		 * 여기에서 i의 typeFullName은 int가 되고 typeFullNameAfterOperation은 float가 된다.
		 * int i = i1 + i2 + f; 에서 i1과 i2의 덧셈결과는 int가 되고 f를 더할 때 i1과 i2의 덧셈결과는
			float로 타입캐스트되어야 한다. 이때는 첫번째 '+"의 typeFullNameAfterOperation이 float가 된다.
			그러기 위해서 operand나 operator에 있는 typeFullNameAfterOperation에 연산시 바뀌는
			타입을 저장한다.*/
		String typeFullNameAfterOperation;
		
		/** 묵시적 타입 캐스트를 위해 필요하다. i2f, i2d, f2i등
		 * Compiler.getTypeOfExpression(), CompilerHelper.getTypeOfOperator()에서 사용한다.
		 * int i = i1 + i2 + f; 에서 i1과 i2의 덧셈결과는 int가 되고 f를 더할 때 i1과 i2의 덧셈결과는
			float로 타입캐스트되어야 한다. 이때는 첫번째 '+"의 typeFullNameAfterOperation이 float가 된다.
			그러기 위해서 operand나 operator에 있는 typeFullNameAfterOperation에 연산시 바뀌는
			타입을 저장한다. 수식스택에 타입을 넣을때 원래의 operand나 operator를 갖게된다.*/
		CodeStringEx operandOrOperator;
		
		/** i = i1+2; 에서 i1의 affectedBy_left는 '+'이다.
		 * if (i1+2>3 && i2<2) 에서 '+'의 affectedBy_left는 '>'이다. 
		 * '>'의 affectedBy_left는 '&&'이다.*/
		CodeStringEx affectedBy_left;
		
		/** i = i1+2; 에서 2의 affectedBy_right는 '+'이다.
		 * if (i1+2>3 && i2<2) 에서 3의 affectedBy_right는 '>'이다. 
		 * '<'의 affectedBy_right는 '&&'이다.*/
		CodeStringEx affectedBy_right;
		
		/** left, right 이든 상관없이 영향받을 경우*/
		CodeStringEx affectedBy;
		
		/**왼쪽 오퍼랜드*/
		CodeStringEx affectsLeft;
		/**오른쪽 오퍼랜드*/
		CodeStringEx affectsRight;
		
		/** 바이트코드출력시에 조건문에서 어떤 조건에서 OR위치로 점프해야할때 해당 OR위치에 
		 *  OR주석을 출력한다. 
		 */
		boolean printsORComment;
		
		/** 바이트코드출력시에 조건문에서 어떤 조건에서 AND위치로 점프해야할때 해당 AND위치에 
		 *  AND주석을 출력한다. 
		 */
		boolean printsANDComment;
		
		/** 바이트코드출력시에 조건문에서 어떤 조건에서 NOT위치로 점프해야할때 해당 NOT위치에 
		 *  NOT주석을 출력한다. 
		 */
		boolean printsNOTComment;
		
		/** 바이트코드 생성시에 if**에서 브랜치할 OR 이후의 토큰*/
		CodeStringEx tokenAfterOR;
		/** 바이트코드 생성시에 if**에서 브랜치할 AND 이후의 토큰*/
		CodeStringEx tokenAfterAND;
		/** 바이트코드 생성시에 if**에서 브랜치할 NOT 이후의 토큰*/
		CodeStringEx tokenAfterNOT;
		
		/** 바이트코드 생성시에 if**에서 브랜치한 후 스택에 넣을 값이 iconst 1이면 true, 
		 * 그렇지 않으면 false*/
		boolean trueOrFalse;
		
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
		
		public CodeStringEx(String str) {
			// TODO Auto-generated constructor stub
			super(str, Compiler.textColor);
		}
		
		public CodeStringEx clone() {
			CodeStringEx r = new CodeStringEx(str);
			r.affectedBy = this.affectedBy;
			r.affectedBy_left = this.affectedBy_left;
			r.affectedBy_right = this.affectedBy_right;
			r.affectsLeft = this.affectsLeft;
			r.affectsRight = this.affectsRight;
			r.arrayListCodeStringForToken = this.arrayListCodeStringForToken;
			r.count = this.count;
			r.indicesInSrc = this.indicesInSrc;
			r.isPlusOrMinusForOne = this.isPlusOrMinusForOne;
			r.listCodeChar = this.listCodeChar;
			r.listOfVarUses = this.listOfVarUses;
			r.operandOrOperator = this.operandOrOperator;
			r.printsANDComment = this.printsANDComment;
			r.printsNOTComment = this.printsNOTComment;
			r.printsORComment = this.printsORComment;
			r.str = this.str;
			r.tokenAfterAND = this.tokenAfterAND;
			r.tokenAfterNOT = this.tokenAfterNOT;
			r.tokenAfterOR = this.tokenAfterOR;
			r.trueOrFalse = this.trueOrFalse;
			r.typeFullName = this.typeFullName;
			r.typeFullNameAfterOperation = this.typeFullNameAfterOperation;
			r.value = this.value;
			return r;
					
		}
		
		public void setStrAndTypeFullName(String str, String typeFullName) {
			this.str = str;
			this.typeFullName = typeFullName;
		}
		
		public String toString() {
			/*String r = this.str;
			if (this.indicesInSrc!=null && this.indicesInSrc.count>0) {
				String index = "("+this.indicesInSrc.list[0]+")";
				r += index;
			}
			return r;*/
			return this.str;
		}
		
		/** indexInSrc는 a = a.concate(b)에서 a 의 indexInSrc에 b 의 indexInSrc(-1이 아닌경우에만)이 추가된다.*/
		public CodeStringEx concate(CodeStringEx str) {
			CodeString temp;
			temp = super.concate(str);
			CodeStringEx r;
			ArrayListInt p = this.indicesInSrc;
			ArrayListIReset v = this.listOfVarUses;
			
			int i;
			ArrayListInt p2 = str.indicesInSrc;
			ArrayListIReset v2 = str.listOfVarUses;
			for (i=0; i<p2.count; i++) {
				p.add(p2.getItem(i));
			}
			for (i=0; i<v2.count; i++) {
				v.add(v2.getItem(i));
			}
			r = new CodeStringEx(temp.listCodeChar, temp.count, p, v);
			if (this.count>0) {
				r.setType(this.listCodeChar[0].type);
			}
			if (this.isPlusOrMinusForOne) {
				r.isPlusOrMinusForOne = true;
			}
			return r;
		}

		/** indexInSrc는 a = a.concate(b)에서 a 의 indexInSrc에 b 의 indexInSrc(-1이 아닌경우에만)이 추가된다.*/
		public CodeString concate(CodeString str, int indexInSrc, FindVarUseParams varUse) {
			CodeString temp;
			temp = super.concate(str);
			CodeStringEx r;
			ArrayListInt p = this.indicesInSrc;
			ArrayListIReset v = this.listOfVarUses;
			if (indexInSrc!=-1) {				
				p.add(indexInSrc);
				v.add(varUse);
			}
			r = new CodeStringEx(temp.listCodeChar, temp.count, p, v);
			if (this.isPlusOrMinusForOne) {
				r.isPlusOrMinusForOne = true;
			}
			if (this.count>0) {
				r.setType(this.listCodeChar[0].type);
			}
			return r;
		}
		
		/** indexInSrc는 a = a.substring(s, e)에서 a의 indexInSrc이 된다.*/
		public CodeString substring(int start, int end) {
			CodeString temp;
			temp = super.substring(start, end);
			CodeStringEx r;
			r = new CodeStringEx(temp.listCodeChar, temp.count, this.indicesInSrc, this.listOfVarUses);
			if (this.isPlusOrMinusForOne) {
				r.isPlusOrMinusForOne = true;
			}
			if (this.count>0) {
				r.setType(this.listCodeChar[0].type);
			}
			return r;
		}

		public void reset() {
			// TODO Auto-generated method stub
			if (this.indicesInSrc!=null) {
				this.indicesInSrc.reset2();
				this.indicesInSrc = null;
			}
			if (this.arrayListCodeStringForToken!=null) {
				this.arrayListCodeStringForToken.destroy();
				this.arrayListCodeStringForToken = null;
			}
			if (this.listCodeChar!=null) {
				int i;
				for (i=0; i<this.listCodeChar.length; i++) {
					this.listCodeChar[i] = null;
				}
				this.listCodeChar = null;
			}
			if (this.listOfVarUses!=null) {
				this.listOfVarUses.reset();
				this.listOfVarUses = null;
			}
			this.str = null;
			this.typeFullName = null;
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
		/*RemoveBlank();

		CheckLetter();
		ConvertToStringArray();*/
		
		if (1911<=this.expression.endIndex() && this.expression.endIndex()<=1913) {
			int a;
			a=0;
			a++;
		}
		else if (this.expression.startIndex()==3116) {
			int a;
			a=0;
			a++;
		}
		if (src.count==1) {
			this.mBuffer = toConvertExArr(src.getItems());
			return this.mBuffer;
		}
		
	
		
		HighArray_CodeString result0 = src;
		
		compiler.CheckParenthesisAll(result0, listOfBlocks, listOfSmallBlocks, listOfLargeBlocks);
		
		HighArray_CodeString result = new HighArray_CodeString(result0.count);
		removeFakeParenthesis(result0, result);
		
		HighArray_CodeString result1 = new HighArray_CodeString(result0.count);
		ProcessFullnames2(result, result1);
		
		listOfBlocks.reset();
		listOfSmallBlocks.reset();
		listOfLargeBlocks.reset();
		
		ArrayListCodeString result2 = new ArrayListCodeString(result1.count);
		ProcessTypecastings2(result1, result2);
		
		
		this.mBuffer = toConvertExArr(result2.getItems()); 
		
		//this.mBuffer = result1.getItems();
		
		
		CheckOperatorRule();

		
		CodeStringEx[] tempBuffer = mBuffer;
		//tempBuffer = FindExpression(mBuffer);
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

	/** (str.toCharArray())[0]; 에서 토큰은 "str.toCharArray()"와 "[0]"이 되므로 이것들을 합쳐야 한다.*/
	CodeStringEx[] removeInvalidArrayElements(CodeStringEx[] tempBuffer) {
		boolean invalidArrayElement = false;
		int i;		
		ArrayListInt arrayElements = new ArrayListInt(5);
		for (i=0; i<tempBuffer.length; i++) {
			if (tempBuffer[i].count>0 && tempBuffer[i].charAt(0).c=='[') {
				arrayElements.add(i);
				invalidArrayElement = true;
			}
		}
		if (invalidArrayElement) {
			for (i=0; i<arrayElements.count; i++) {
				int index = arrayElements.getItem(i);
				tempBuffer[index-1] = tempBuffer[index-1].concate(tempBuffer[index]);
				tempBuffer[index] = null;
			}
			
			CodeStringEx[] buf = new CodeStringEx[tempBuffer.length-arrayElements.count];
			int j=0;
			for (i=0; i<tempBuffer.length; i++) {
				if (tempBuffer[i]!=null) {
					buf[j] = tempBuffer[i];
					j++;
				}
			}
			return buf;
		}
		return tempBuffer;
	}
	
	CodeStringEx[] FindExpression(CodeStringEx[] buffer) {
		CodeStringEx[] tempBuffer;
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
	
	/*int[][] CheckParenthesis(ArrayListIReset listOfSmallBlockParams) {
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
	CodeStringEx[] ProcessParenthesis(CodeStringEx[] buffer) {
		int[][] parenthesisArray=null;
		try {
			parenthesisArray = CheckParenthesis(buffer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CompilerHelper.printStackTrace(CommonGUI.textViewLogBird, e);
			return null;
		}
		CodeStringEx[] tempBuffer;
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
	
	boolean contains(String[] operators, CodeStringEx operator) {
		int i;
		for (i=0; i<operators.length; i++) {
			if (operator.equals(operators[i])) return true;
		}
		return false;
		
	}
	
	/** !, ~, +, -와 같은 일항연산자를 포스트픽스로 바꾼다.*/
	CodeStringEx[] processOperatorForOne(CodeStringEx[] tempBuffer, String[] operators) {
		for (int i=0; i<tempBuffer.length && tempBuffer[i]!=null; i++) {			
			if (contains(operators, tempBuffer[i])) {
				boolean isOperatorForOne = false;
				if (tempBuffer[i].equals("+") || tempBuffer[i].equals("-")) {
					// -(2.0f) 이와 같은 경우는 tempBuffer가 -, 2.0f 이 된다.
					if (i-1<0) {
						tempBuffer[i].isPlusOrMinusForOne = true;
						isOperatorForOne = true;
					}
					// -1.0f + -(2.0f)이와 같은 경우는 tempBuffer가 -1.0f, +, -, 2.0f 이 된다.
					else if (i-1>=0 && CompilerHelper.IsOperator(tempBuffer[i-1])) {
						tempBuffer[i].isPlusOrMinusForOne = true;
						isOperatorForOne = true;
					}
					
					if (isOperatorForOne==false) continue;
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
				
				ArrayListInt indices = new ArrayListInt(1);
				indices.add(-1);
				ArrayListIReset varUses = new ArrayListIReset(1);
				varUses.add(null);
				// 연산자에 :T연결
				CodeStringEx strT = new CodeStringEx(":T", Color.BLACK, indices, varUses);
				tempBuffer[i] = (CodeStringEx) tempBuffer[i].concate(strT, -1, null);
				if (pos==EOB) {
					tempBuffer = Common.Insert(tempBuffer, tempBuffer[i], i+2); // 연산자를 오퍼랜드뒤에 복사
					tempBuffer = Common.Insert(tempBuffer, new CodeStringEx("@",Color.BLACK, indices, varUses), i+3);
					// @를 연산자뒤에 복사
					tempBuffer = Common.Remove(tempBuffer, i, 1); // 연산자가 이동되었으므로 원래 연사자는 제거
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
	
	/** +, -, *, /와 같은 이항연산자를 포스트픽스로 바꾼다.*/
	CodeStringEx[] processOperator(CodeStringEx[] tempBuffer, String[] operators) {
		for (int i=0; i<tempBuffer.length && tempBuffer[i]!=null; i++) {			
			if (contains(operators, tempBuffer[i])) {
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
				
				ArrayListInt indices = new ArrayListInt(1);
				indices.add(-1);
				ArrayListIReset varUses = new ArrayListIReset(1);
				varUses.add(null);
				// :T연결
				CodeStringEx strT = new CodeStringEx(":T", Color.BLACK, indices, varUses);
				tempBuffer[i] = (CodeStringEx) tempBuffer[i].concate(strT, -1, null); 
				// 연산자가 +일경우 +:T가 된다.
				if (pos==EOB) {
					tempBuffer = Common.Insert(tempBuffer, tempBuffer[i], i+2); 
					// 2.5f % 2일 경우 2.5f  %:T  2  %:T가 된다.
					tempBuffer = Common.Insert(tempBuffer, new CodeStringEx("@",Color.BLACK, indices, varUses), i+3); 
					//2.5f  %:T  2  %:T  @
					tempBuffer = Common.Remove(tempBuffer, i, 1); 
					//2.5f  2  %:T  @
					i = i+2; 
					// i=1이었으므로  i=3이 된다.
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

	

	/** 이 함수를 거치면 @가 항상 끝에 하나만 나온다
	 * 2 + (2 + 2 * 3) * 2   -->   
	 * 2 + 2 2 3 *:T +:T @ * 2   -->   
	 * 2 2 2 3 *:T +:T 2 *:T +:T @
	 * @param buffer
	 * @return
	 */
	CodeStringEx[] ConvertInfixToPostfix(CodeStringEx[] tempBuffer) {
		tempBuffer = processOperatorForOne(tempBuffer, operatorsForOne);
		
		//String[] operatorsPower = {"^"};
		//tempBuffer = processOperator(tempBuffer, operatorsPower);
		
		//String[] operatorsMultipliers = {"*", "/", "%"};
		tempBuffer = processOperator(tempBuffer, operatorsMultipliers);
		
		//String[] operatorsShifters = {"<<", ">>", ">>>"};
		tempBuffer = processOperator(tempBuffer, operatorsShifters);
		
		//String[] operatorsBitLogical = {"&", "|", "~", "^"};
		tempBuffer = processOperator(tempBuffer, operatorsBitLogical);
		
		//String[] operatorsAdders = {"+", "-"};
		tempBuffer = processOperator(tempBuffer, operatorsAdders);
		
		
		
		//String[] operatorsRelations = {"<", ">", "<=", ">="};
		tempBuffer = processOperator(tempBuffer, operatorsRelations);
		
		//String[] operatorsInstanceof = {"instanceof"};
		tempBuffer = processOperator(tempBuffer, operatorsInstanceof);
				
		//String[] operatorsEquals = {"==", "!="};
		tempBuffer = processOperator(tempBuffer, operatorsEquals);
		
		
		
		
		
		//String[] operatorsLogical1 = {"&&", "!"};
		tempBuffer = processOperator(tempBuffer, operatorsLogical1);
		
		//String[] operatorsLogical2 = {"||"};
		tempBuffer = processOperator(tempBuffer, operatorsLogical2);
		
		//String[] operatorsChoose = {":"};
		tempBuffer = processOperator(tempBuffer, operatorsChoose);
		
		//String[] operatorsAllocater = {"=", "+=", "-=", "*=", "/=", "%=", "|=", "&="};
		tempBuffer = processOperator(tempBuffer, operatorsAllocater);
		
		return tempBuffer;
			
	}

	CodeStringEx[] RemoveSeparator(CodeStringEx[] buffer) {
		for (int i=0; i<buffer.length && buffer[i]!=null; i++) {
			if (buffer[i].equals("@")) {
				buffer = Common.Remove(buffer, i, 1);
				i = i-1;
			}
		}
		return buffer;
	}

	CodeStringEx[] RemoveFlag(CodeStringEx[] buffer) {
		for (int i=0; i<buffer.length && buffer[i]!=null; i++) {
			int indexT = buffer[i].indexOf(":T");
			if (indexT!=-1) {
					buffer[i] = (CodeStringEx) buffer[i].substring(0, indexT);
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
	void RemoveBlankAndComment(HighArray_CodeString src, ArrayListCodeString result) {
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

		static CodeStringEx[] Insert(CodeStringEx[] input, CodeStringEx c, int pos) {
			if (input==null || c==null)
				return null;
			CodeStringEx[] result;
			if (pos>=input.length) {
				result = new CodeStringEx[pos+1];
				CopyTo(input, result, 0);
				result[pos] = c;
			}
			else {
				result = new CodeStringEx[input.length+1];
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
		
		static void CopyTo(CodeStringEx[] srcArray, CodeStringEx[] destArray, int pos) {
			int i, j;
			for (i=pos, j=0; i<pos+srcArray.length; i++) {
				destArray[i] = srcArray[j];
				j++;
			}
		}

		static CodeStringEx[] Insert(CodeStringEx[] destArray, CodeStringEx[] srcArray, int pos) {
			if (destArray==null || srcArray==null || pos<0)
				return null;
			CodeStringEx[] result;
			if (pos>=destArray.length) {
				result = new CodeStringEx[pos+srcArray.length];
				CopyTo(destArray, result, 0);
				CopyTo(srcArray, result, pos);
			}
			else {
				result = new CodeStringEx[destArray.length+srcArray.length];
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

		/*static int Find(CodeStringEx[][] input, CodeStringEx c, int colIndex) {
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

		static CodeStringEx[] Remove(CodeStringEx[] input, int pos, int count) {
			if (input==null)
				return null;
			if (pos+count>input.length)
				return null;
			CodeStringEx[] result = new CodeStringEx[input.length-count];
			for (int i=0, k=0; i<input.length; i++) {
				if (pos<=i && i<=pos+count-1) continue;
				result[k] = input[i];
				k++;
			}
			return result;
		}

		static CodeStringEx[] Copy(CodeStringEx[] srcArray, int srcIndex, int len)
		{
			if (srcArray==null)
				return null;
			if (srcIndex+len>srcArray.length)
				return null;
			if (len<0) {
				int a;
				a=0;
				a++;
			}
			CodeStringEx[] result = new CodeStringEx[len];
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