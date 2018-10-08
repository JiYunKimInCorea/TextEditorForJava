package com.gsoft.common;

import com.gsoft.common.Code.CodeChar;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Code.CodeStringType;
import com.gsoft.common.Compiler_types.Comment;
import com.gsoft.common.Compiler_types.Error;
import com.gsoft.common.Compiler_types.HighArray_CodeString;
import com.gsoft.common.Compiler_types.IReset;
import com.gsoft.common.Compiler_types.Language;
import com.gsoft.common.Util.ArrayListCodeChar;
import com.gsoft.common.Util.ArrayListIReset;

public class StringTokenizer implements IReset {
	
	static int WordLengthLimit = 70;
	
	public HighArray_CodeString mBuffer;
	
	/** 파일상의 주석, 다큐주석들의 리스트, 문장의 리스트를 만들때 사용, Comment[], 
	 * mlistOfFindStatementParams을 참조*/
	ArrayListIReset mlistOfComments = new ArrayListIReset(100);
	
	StringTokenizer() {

	}
	
	/** 거의 모든 언어에 적용 가능, 
     * 정수(음수, 양수), 실수, 변수, 문자상수, 구분자(공백, 연산자, 괄호 등)를 구별한다.
     * 공백은 구분자이다. 따라서 id나 상수에 공백이 있어서는 안된다. 
     * start2()와 start_onlyInterface()에서 호출된다.
	 * @param compiler : CompilerHelper의 loadClassFromSrc_onlyInterface()에서 
	 * compiler.start_onlyInterface(...) 이렇게 호출이 되고  start_onlyInterface()에서
	 * RegisterPackageName(...)호출을 하면
	 * RegisterPackageName()내부의 this나 this가 없는 
	 * Compiler클래스의 멤버 사용은 compiler의 멤버사용이므로 없어도 되는 것이다.
	 * start2()에서 호출이 되어도 마찬가지이다.
	 * @param src : compiler.mBuffer와 같다.*/
    public HighArray_CodeString ConvertToStringArray2(CodeString input, int initMaxLengthOfArray, Language language)
    {
        CodeString word = null;
        ArrayListCodeChar word_wchar = new ArrayListCodeChar(WordLengthLimit);

        CodeString str;     // 인용부호안의 문자열
        ArrayListCodeChar str_wchar = new ArrayListCodeChar(WordLengthLimit);
        
        boolean isComment = false;
        ArrayListCodeChar str_Comment = new ArrayListCodeChar(WordLengthLimit);

        boolean strPutted = false;
        boolean isStrOrChar = true;
        
        
        if (mBuffer==null) {
        	mBuffer = new HighArray_CodeString(initMaxLengthOfArray/10);
        	//mBuffer = new ArrayListCodeString(initMaxLengthOfArray);
        	//mBuffer.resizeInc = initMaxLengthOfArray;
        }
        else {
        	//mBuffer.reset2();
        }
    
        int i, j;
        boolean isSignalOfPosOrNeg = false;
        CodeChar c;
        int startIndexInmBuffer = -1;
    	int endIndexInmBuffer = -1;
    	int len = input.length();

        for (i = 0; i < len; i++)
        {
        	if (i==11215) {
        		int a;
        		a=0;
        		a++;
        	}
        	try{
        	c = input.charAt(i);
        	
        	if (c.c=='\\') {
        		int a;
        		a=0;
        		a++;
        	}
        	
        	// 주석 "/*" 처리
            if (input.charAt(i).c == '/' && 
            		(i + 1 < input.length() && input.charAt(i + 1).c == '*'))
            {
            	
            	if (!strPutted)
                {
                    //이전 토큰
                	if (word==null && word_wchar.count>0)
                    {
                    	word = new CodeString(word_wchar.getItems(), word_wchar.count);
                        PutTomBuffer(mBuffer, word);
                        word_wchar.reset2();
                        word = null;
                    }
                }
                else//스트링이면
                {
                	str_wchar.add(input.charAt(i));
                	continue;
                }
            	
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
                	// "/*" 넣기
                	CodeChar[] cs0 = {input.charAt(i)};	// Separator넣기
                	cs0[0].type = CodeStringType.Comment;
                	cs0[0].color = Compiler.commentColor;
                	startIndexInmBuffer = PutTomBuffer(mBuffer, new CodeString(cs0, cs0.length));
                    
                	CodeChar[] cs1 = {input.charAt(i+1)};	// Separator넣기
                	cs1[0].type = CodeStringType.Comment;
                	cs1[0].color = Compiler.commentColor;
                    PutTomBuffer(mBuffer, new CodeString(cs1, cs1.length));
                    
                	int startIndex = startOfComment+2;
                	int endIndex = endOfComment-2;
                	str_Comment.reset2();
                    for (k=startIndex; k<=endIndex; k++) {                    	
                    	CodeChar ch = input.charAt(k);
                    	if (ch.c=='\n' || ch.c=='\r') {
                    		if (str_Comment.count>0) {
	                    		 CodeString comment = new CodeString(str_Comment.getItems(), str_Comment.count);
	                             comment.setType(CodeStringType.Comment);
	                             comment.setColor(Compiler.commentColor);
	                             PutTomBuffer(mBuffer, comment);
	                             str_Comment.reset2();
                    		}
                             
                             CodeChar[] special = {ch};// '\n', '\r'
                             CodeString commentSpecial = new CodeString(special, special.length);
                             commentSpecial.setType(CodeStringType.Comment);
                             commentSpecial.setColor(Compiler.commentColor);
                             PutTomBuffer(mBuffer, commentSpecial);
                    	}
                    	else {
                    		str_Comment.add(ch);
                    	}
                    }
                    // 주석의 마지막 부분
                    if (str_Comment.count>0) {
	                    CodeString comment = new CodeString(str_Comment.getItems(), str_Comment.count);
	                    comment.setType(CodeStringType.Comment);
	                    comment.setColor(Compiler.commentColor);
	                    PutTomBuffer(mBuffer, comment);
                    }
                    
                 // "*/" 넣기
                	CodeChar[] cs3 = {input.charAt(endOfComment-1)};	// Separator넣기
                	cs3[0].type = CodeStringType.Comment;
                	cs3[0].color = Compiler.commentColor;
                    PutTomBuffer(mBuffer, new CodeString(cs3, cs3.length));
                	CodeChar[] cs4 = {input.charAt(endOfComment)};	// Separator넣기
                	cs4[0].type = CodeStringType.Comment;
                	cs4[0].color = Compiler.commentColor;
                	endIndexInmBuffer = PutTomBuffer(mBuffer, new CodeString(cs4, cs4.length));
                }//if (isDocuComment==false) {
                else {
                	// "/**" 넣기
                	CodeChar[] cs0 = {input.charAt(i)};	// Separator넣기
                	cs0[0].type = CodeStringType.DocuComment;
                	cs0[0].color = Compiler.docuCommentColor;
                	startIndexInmBuffer = PutTomBuffer(mBuffer, new CodeString(cs0, cs0.length));
                	
                	CodeChar[] cs1 = {input.charAt(i+1)};	// Separator넣기
                	cs1[0].type = CodeStringType.DocuComment;
                	cs1[0].color = Compiler.docuCommentColor;
                    PutTomBuffer(mBuffer, new CodeString(cs1, cs1.length));
                    CodeChar[] cs2 = {input.charAt(i+2)};	// Separator넣기
                    cs2[0].type = CodeStringType.DocuComment;
                	cs2[0].color = Compiler.docuCommentColor;
                    PutTomBuffer(mBuffer, new CodeString(cs2, cs2.length));
                    
                	int startIndex = startOfComment+3;
                	int endIndex = endOfComment-2;
                	str_Comment.reset2();
                	for (k=startIndex; k<=endIndex; k++) {                		
                    	CodeChar ch = input.charAt(k);
                    	if (ch.c=='\n' || ch.c=='\r') {
                    		if (str_Comment.count>0) {
	                   		 	CodeString comment = new CodeString(str_Comment.getItems(), str_Comment.count);
	                            comment.setType(CodeStringType.DocuComment);
	                            comment.setColor(Compiler.docuCommentColor);
	                            PutTomBuffer(mBuffer, comment);
	                            str_Comment.reset2();
                    		}
                            
                            CodeChar[] special = {ch}; // '\n', '\r'
                            CodeString commentSpecial = new CodeString(special, special.length);
                            commentSpecial.setType(CodeStringType.DocuComment);
                            commentSpecial.setColor(Compiler.docuCommentColor);
                            PutTomBuffer(mBuffer, commentSpecial);
	                   	}
	                   	else {
	                   		str_Comment.add(ch);
	                   	}
                    }
                	// 주석의 마지막 부분
                	if (str_Comment.count>0) {
	                	CodeString comment = new CodeString(str_Comment.getItems(), str_Comment.count);
	                    comment.setType(CodeStringType.DocuComment);
	                    comment.setColor(Compiler.docuCommentColor);
	                    PutTomBuffer(mBuffer, comment);
                	}
                    
                 // "*/" 넣기
                	CodeChar[] cs3 = {input.charAt(endOfComment-1)};	// Separator넣기
                	cs3[0].type = CodeStringType.DocuComment;
                	cs3[0].color = Compiler.docuCommentColor;
                    PutTomBuffer(mBuffer, new CodeString(cs3, cs3.length));
                	CodeChar[] cs4 = {input.charAt(endOfComment)};	// Separator넣기
                	cs4[0].type = CodeStringType.DocuComment;
                	cs4[0].color = Compiler.docuCommentColor;
                	endIndexInmBuffer = PutTomBuffer(mBuffer, new CodeString(cs4, cs4.length));
                }//if (isDocuComment==false) {
                
                mlistOfComments.add(new Comment(mBuffer, startIndexInmBuffer, endIndexInmBuffer));
                
                i = endOfComment;
                continue;
                
               
            }//if (input.charAt(i).c == '/' && 
    		// (i + 1 < input.length() && input.charAt(i + 1).c == '*'))
        	
            // 주석 "//" 처리
            else if (input.charAt(i).c == '/' && 
            		(i + 1 < input.length() && input.charAt(i + 1).c == '/'))
            {
            	if (!strPutted)
                {
                    //if (i > 0 && Character.isLetterOrDigit(input.charAt(i - 1).c))    // 숫자나 변수 넣기
                	if (word==null && word_wchar.count>0)
                    {
                    	word = new CodeString(word_wchar.getItems(), word_wchar.count);
                        PutTomBuffer(mBuffer, word);
                        word_wchar.reset2();
                        word = null;
                    }
                	// "//" 넣기
                    CodeChar[] cs = {input.charAt(i)};	// Separator넣기
                    cs[0].type = CodeStringType.Comment;
                	cs[0].color = Compiler.commentColor;
                	startIndexInmBuffer = PutTomBuffer(mBuffer, new CodeString(cs, cs.length));
                	
                    CodeChar[] cs2 = {input.charAt(i+1)};	// Separator넣기
                    cs2[0].type = CodeStringType.Comment;
                	cs2[0].color = Compiler.commentColor;
                    PutTomBuffer(mBuffer, new CodeString(cs2, cs2.length));
                }
                else // strPutted==true
                {
                	str_wchar.add(input.charAt(i));
                	continue;
                }
            	
            	int startOfComment = i;
            	int endOfComment = input.length()-1;     	
            	                	
                for (j = i+2; j < input.length(); j++)
                {
                	if (input.charAt(j).c == '\r' && 
                    		(j + 1 < input.length() && input.charAt(j + 1).c == '\n'))
                    {
                		endOfComment = j + 1;
                		break;
                    }
                	else if (input.charAt(j).c == '\n') {
                		endOfComment = j;
                		break;
                	}
                }
                
                
                
                
                int startIndex = startOfComment+2;
            	int endIndex = endOfComment;
            	
            	int k;
            	str_Comment.reset2();
                for (k=startIndex; k<=endIndex; k++) {                	
                	CodeChar ch = input.charAt(k);
                	if (ch.c=='\n' || ch.c=='\r') {
                		if (str_Comment.count>0) {
	               		 	CodeString comment = new CodeString(str_Comment.getItems(), str_Comment.count);
	                        comment.setType(CodeStringType.Comment);
	                        comment.setColor(Compiler.commentColor);
	                        PutTomBuffer(mBuffer, comment);
	                        str_Comment.reset2();
                		}
                        
                        CodeChar[] special = {ch};// '\n', '\r'
                        CodeString commentSpecial = new CodeString(special, special.length);
                        commentSpecial.setType(CodeStringType.Comment);
                        commentSpecial.setColor(Compiler.commentColor);
                        endIndexInmBuffer = PutTomBuffer(mBuffer, commentSpecial);
	               	}
	               	else {
	               		str_Comment.add(ch);
	               	}
                }
                // 주석의 마지막 부분
                if (str_Comment.count>0) {
	                CodeString comment = new CodeString(str_Comment.getItems(), str_Comment.count);
	                comment.setType(CodeStringType.Comment);
	                comment.setColor(Compiler.commentColor);
	                PutTomBuffer(mBuffer, comment);
                }
                
                mlistOfComments.add(new Comment(mBuffer, startIndexInmBuffer, endIndexInmBuffer));
               
                i = endOfComment;
                continue;
            }// 주석
        	
			if (c.c=='_') {
        		if (strPutted==false) {
        			word_wchar.add(c);
        		}
        		else {
        			str_wchar.add(c);
        		}
        	}
        	// 음수나 양의 부호 혹은 연산자인지 확인한다.
			// 연산자일 경우 마지막 else 처럼 앞의 토큰과 연산자를 넣는다.
        	else if ((c.c=='-' || c.c=='+')) {
        		isSignalOfPosOrNeg = false;
        		char prev=0, next=0;
        		int prevIndex = CompilerHelper.SkipBlank(input, true, 0, i-1);
        		if (prevIndex!=-1) prev = input.charAt(prevIndex).c;
        		int nextIndex = CompilerHelper.SkipBlank(input, false, i+1, input.count-1);
        		if (nextIndex!=input.count) next = input.charAt(nextIndex).c;
        		/*if ( (prev=='=' || prev=='(' || prev=='[' || prev=='{' || prev==',' || prev==')' || CompilerHelper.IsOperator(prev)) &&        	
        				java.lang.Character.isDigit(next)) {
        			// 2 + -3 에서 c는 '-'이다.
        			
        			if (prev==')') {
        				// (byte)-32 에서 c.c는 -이다.
        				int rightPairIndex = mBuffer.count-1;
        				rightPairIndex = SkipBlank(mBuffer, true, 0, rightPairIndex-1);
        				if (rightPairIndex==21885 || rightPairIndex==21886) {
        					int a;
        					a=0;
        					a++;
        				}
        				int leftPairIndex = CompilerHelper.CheckParenthesis(mBuffer, "(", ")", 0, rightPairIndex, true);
        				if (leftPairIndex==-1) {
        					// 부호가 아니라 연산자로 처리하기 위해 아무 일도 않한다.
        				}
        				else {
	        				int indexRightPairOfTemplate = SkipBlank(mBuffer, true, 0, rightPairIndex-1);
	        				if (indexRightPairOfTemplate==-1) {
	        					// 부호가 아니라 연산자로 처리하기 위해 아무 일도 않한다.
	        				}
	        				else {
		        				int prevTypeIndex;
		        				int typeIndex;
		        				if (mBuffer.getItem(indexRightPairOfTemplate).equals(">")) {
		        					Template template = new Template();
		        					typeIndex = IsType(mBuffer, true, indexRightPairOfTemplate, template);
		        				}
		        				else {
		        					typeIndex = IsType(mBuffer, true, indexRightPairOfTemplate, null);
		        				}
		        				if (typeIndex==-1) {
		        					// 부호가 아니라 연산자로 처리하기 위해 아무 일도 않한다.
		        				}
		        				else {
			        				prevTypeIndex = SkipBlank(mBuffer, true, 0, typeIndex-1);
			        				if (prevTypeIndex==leftPairIndex) {
			        					int idIndex = SkipBlank(mBuffer, true, 0, leftPairIndex-1);
			        					if (this.IsIdentifier(mBuffer.getItem(idIndex))) {
			        						// 타입캐스트가 아니라 함수호출이다. func(a)+1에서 c.c는 +이다.
			        						// 부호가 아니라 연산자로 처리하기 위해 아무 일도 않한다.
			        					}
			        					else { // 타입캐스트이므로 부호이다.
			        						// (byte)-32 에서 c.c는 -이다.
				        					if (strPutted==false) { // 음수로 가정한다.
				    	        				isSignalOfPosOrNeg = true;
				    	            			word_wchar.add(c);
				    	            		}
				    	            		else {
				    	            			str_wchar.add(c);
				    	            		}
			        					}
			        				} 
			        				else { // 부호가 아니라 연산자로 처리하기 위해 아무 일도 않한다.
			        					
			        				}
		        				}//typeIndex는 -1이 아니다.
	        				}//indexRightPairOfTemplate는 -1이 아니다.
        				}//leftPairIndex는 -1이 아니다.
        				
        			}//if (prev==')') {
        			else {    // 일반적인 경우는 부호로 처리한다.			
	        			if (strPutted==false) { // 음수로 가정한다.
	        				isSignalOfPosOrNeg = true;
	            			word_wchar.add(c);
	            		}
	            		else {
	            			str_wchar.add(c);
	            		}
        			}
        		}//if ( (prev=='=' || prev=='(' || prev=='[' || prev=='{' || prev==',' || prev==')' || CompilerHelper.IsOperator(prev)) &&
        		*/
        		// -, +가 연산자인 경우
        		if (!isSignalOfPosOrNeg) {
        			if (!strPutted)
                    {
                        //if (i > 0 && Character.isLetterOrDigit(input.charAt(i - 1).c))    // 숫자나 변수 넣기
                    	if (word==null && word_wchar.count>0)
                        {
                        	word = new CodeString(word_wchar.getItems(), word_wchar.count);
                            PutTomBuffer(mBuffer, word);
                            word_wchar.reset2();
                            word = null;
                        }
                        CodeChar[] cs = {input.charAt(i)};	// Separator넣기
                        PutTomBuffer(mBuffer, new CodeString(cs, cs.length));
                    }
                    else
                    {
                    	str_wchar.add(input.charAt(i));
                    }
        		}
        		
        	}
			// 숫자, 변수인경우
        	else if (c.c!='"' && c.c!='\'' && CompilerHelper.IsSeparator(c.c)==false/* && IsBlank(c.c)==false*/) {
        		if (strPutted==false) {
        			word_wchar.add(c);
        		}
        		else {
        			str_wchar.add(c);
        		}
        	}
			
        	// 실수상수일 경우, 소수점앞뒤에 공백이 있어서는 안된다. 1.f 가능
            else if (c.c == '.' && 
            		(i > 0 && java.lang.Character.isDigit(input.charAt(i - 1).c)) && 
            		(i < input.length() - 1 && java.lang.Character.isDigit(input.charAt(i + 1).c) 
            				/*|| input.charAt(i + 1).c=='f' || input.charAt(i + 1).c=='d'*/))
            {
                if (!strPutted)
                {
                	word_wchar.add(input.charAt(i));
                }
                else
                {
                	str_wchar.add(input.charAt(i));
                }
            }
        	
           
            // 인용부호안의 문자열, 주석안의 문자열은 제외한다.
            else if (c.c == '"' && CompilerHelper.IsComment(c)==false)
            {
                if (!strPutted)
                {
                    //if (i > 0 && Character.isLetterOrDigit(input.charAt(i - 1).c) )  // 숫자나 변수 넣기
                	if (word==null && word_wchar.count>0)
                    {
                        word = new CodeString(word_wchar.getItems(), word_wchar.count);
                        PutTomBuffer(mBuffer, word);
                        word_wchar.reset2();
                        word = null;
                        
                    }
                    str_wchar.add(input.charAt(i));
                    strPutted = true;
                    isStrOrChar = true;
                }
                else //if (isStrOrChar)
                {
                	if (c.c=='c') {
                		int a;
                		a=0;
                		a++;
                	}
                	
                	// 인용문자열로 들어가는 ", 즉 "\", '\"' 을 말한다.
                	// String a = "\"; // 스트링 에러
                	// char ch = '\'; // char 에러
                	// String test1 = "c:\"; // 스트링 에러
                	// String test2 = "c:\\";  // 가능
                	if ((str_wchar.count-1>=0 && str_wchar.getItem(str_wchar.count-1).c=='\\') && 
                			(str_wchar.count-2>=0 && str_wchar.getItem(str_wchar.count-2).c!='\\')) {
                		str_wchar.add(input.charAt(i));
                	}
                	else if (isStrOrChar==false) { // '"'
                		str_wchar.add(input.charAt(i));
                	}
                	// "\\" 등, 인용부호의 끝
                	else {
                		str_wchar.add(input.charAt(i));                    	
                        str = new CodeString(str_wchar.getItems(), str_wchar.count);
                        str.setType(CodeStringType.Constant);
                        str.setColor(Compiler.keywordColor);
                        
                        PutTomBuffer(mBuffer, str);
                        str_wchar.reset2();
                        strPutted = false;
                	}
                }
            }
            
            else if (c.c == '\''  && CompilerHelper.IsComment(c)==false)
            {
                if (!strPutted && language!=Language.Html)
                {
                    //if (i > 0 && Character.isLetterOrDigit(input.charAt(i - 1).c) )  // 숫자나 변수 넣기
                	if (word==null && word_wchar.count>0)
                    {
                        word = new CodeString(word_wchar.getItems(), word_wchar.count);
                        PutTomBuffer(mBuffer, word);
                        word_wchar.reset2();
                        word = null;
                        
                    }
                    str_wchar.add(input.charAt(i));
                    strPutted = true;
                    isStrOrChar = false;
                }
                else //if (!strPutted && language!=Language.Html)
                {
                	// 프랑스어 등에서 인용문이 아닌데도 '가 쓰이는 경우가 있다.
                	if (language==Language.Html) {
                		if (!strPutted) {
                			if (word==null && word_wchar.count>0)
                            {
                                word = new CodeString(word_wchar.getItems(), word_wchar.count);
                                PutTomBuffer(mBuffer, word);
                                word_wchar.reset2();
                                word = null;
                                
                            }
                			CodeChar[] cs = {input.charAt(i)};	// ' 넣기
                            PutTomBuffer(mBuffer, new CodeString(cs, cs.length));
                            strPutted = false;
                		}
                	}
                	else { // strPutted==true
	                	// 인용문자열로 들어가는 ', 즉 '\''을 말한다.
	                	if ((str_wchar.count-1>=0 && str_wchar.getItem(str_wchar.count-1).c=='\\') && 
	                			(str_wchar.count-2>=0 && str_wchar.getItem(str_wchar.count-2).c!='\\')) {
	                		str_wchar.add(input.charAt(i));
	                	}
	                	else if (isStrOrChar) { // "'"
	                		str_wchar.add(input.charAt(i));
	                	}
	                	// '\\' 등, 인용부호의 끝
	                	else {
	                		str_wchar.add(input.charAt(i));                    	
	                        str = new CodeString(str_wchar.getItems(), str_wchar.count);
	                        str.setType(CodeStringType.Constant);
	                        str.setColor(Compiler.keywordColor);
	                        PutTomBuffer(mBuffer, str);
	                        str_wchar.reset2();
	                        strPutted = false;
	                	}
                	}
                }//else //if (isStrOrChar==false)
            }// else if (c.c == '\''  && CompilerHelper.IsComment(c)==false)
			
            else if (input.charAt(i).c=='\\' && 
            		(i+1<input.length() && input.charAt(i+1).c=='\\') && 
            		CompilerHelper.IsComment(c)==false)   { 
            	
            	
            	if (!strPutted)
                { // 디렉토리 경로 문자
                    // 숫자나 변수 넣기
                	if (word==null && word_wchar.count>0)
                    {
                    	word = new CodeString(word_wchar.getItems(), word_wchar.count);
                        PutTomBuffer(mBuffer, word);
                        word_wchar.reset2();
                        word = null;
                    }
                    CodeChar[] cs = {input.charAt(i)};	// Separator넣기
                    PutTomBuffer(mBuffer, new CodeString(cs, cs.length));
                    
                    i++; // 뒤에 나오는 '\' 은 건너뛴다.
                }
                else
                {
                	str_wchar.add(input.charAt(i));
                }
            	
            	
            	
            }
			

            // Separator : 실수상수일 경우 소수점, 음수나 양의 부호를 제외한 
        	// 연산자, 공백, 세미콜론, 콜론 기타 등등 
            else /*if (IsSeparator(input.charAt(i)))*/
            {
            	//CodeChar c = input.charAt(i);
            	if (c.c=='\\') {
            		int a;
            		a=0;
            		a++;
            	}
            	if (c.c=='@') {
            		int a;
            		a=0;
            		a++;
            	}
                if (!strPutted)
                {
                    //if (i > 0 && Character.isLetterOrDigit(input.charAt(i - 1).c))    // 숫자나 변수 넣기
                	if (word==null && word_wchar.count>0)
                    {
                    	word = new CodeString(word_wchar.getItems(), word_wchar.count);
                        PutTomBuffer(mBuffer, word);
                        word_wchar.reset2();
                        word = null;
                    }
                    CodeChar[] cs = {input.charAt(i)};	// Separator넣기
                    PutTomBuffer(mBuffer, new CodeString(cs, cs.length));
                }
                else
                {
                	str_wchar.add(input.charAt(i));
                }
            }//else /*if (IsSeparator(input.charAt(i)))*/

        	}catch(Exception e) {
        		int debug;
        		debug=0;
        		debug++;
        	}
        } //for (i = 0; i < input.length(); i++)

        if (i == input.length())
        {
            //if (i > 0 && Character.isLetterOrDigit(input.charAt(i - 1).c))
        	if (word==null && word_wchar.count>0)
            // 숫자나 변수로 끝나면 : 구분자가 이미 들어갈 수 있기 때문
            {
            	word = new CodeString(word_wchar.getItems(), word_wchar.count);
                PutTomBuffer(mBuffer, word);
                word = null;
            }
            
            if (strPutted) {
            	//Compiler.errors.add(new Error(mBuffer, mBuffer.count-1, mBuffer.count-1, "invalid leftParenthesis.. RightParenthesis not exist.."));
            }
        }
        
        return mBuffer;

    }
    
    static int PutTomBuffer(HighArray_CodeString mBuffer, CodeString str)
    {
    	int r = -1;
    	try {
    		r = mBuffer.count;
    		if (r>=11735) {
        		int a;
        		a=0;
        		a++;
        	}
    		if (str.str.equals("buttonLatin2.draw")) {
    			int a;
    			a=0;
    			a++;
    		}
    		mBuffer.add(str);
    		return r;
    	}catch(OutOfMemoryError e) {
    		CompilerHelper.showMessage(true, "buffer count:"+mBuffer.count+ " " + e.toString());
    	}
        return r;
    }

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		if (this.mlistOfComments!=null) {
			this.mlistOfComments.reset();
			this.mlistOfComments = null;
		}
	}
}