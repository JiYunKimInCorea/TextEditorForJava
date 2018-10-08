package com.gsoft.common;

import com.gsoft.common.Compiler_types.IReset;

import android.graphics.Color;

public class Code {
	public static class CodeChar implements IReset {
		public char c;
		public int color = Color.BLACK;
		public byte type;
		public CodeChar(char c, int color) {
			this.c = c;
			this.color = color;
		}
		public CodeChar(char c, int color, byte type) {
			this.c = c;
			this.color = color;
			this.type = type;
		}
		public String toString() {
			char[] arr = {c};
			return new String(arr);
		}
		
		/** = 와 같다, new로 새로 할당하는 것이 아니라 기존 메모리를 재사용한다.*/
		public void copy(CodeChar c) {
			this.c = c.c;
			this.color = c.color;
			this.type = c.type;
		}
		public void destroy() {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static class CodeStringType {
		public static byte Text = 1;
		public static byte Keyword = 2;
		public static byte FuncUse = 3;
		public static byte MemberVarDecl = 4;
		public static byte MemberVarUse = 5;
		public static byte Constant = 6;
		public static byte Parenthesis = 7;
		public static byte Comment = 8;
		public static byte DocuComment = 9;
		public static byte Annotation = 10;
	}
	
	public static class CodeString implements IReset {
		//boolean isStruct;
		
		//char[] listChar;
		//int[] listColor;
		
		public CodeChar[] listCodeChar;
		public int count;
		
		/** cache이다. toString()에서 CodeString을 String으로 변환하는 비용을 제거한다.*/
		public String str;
		
		/** types이 없으므로 CodeChar의 type은 null이다.*/
		public CodeString(char[] text, int[] colors) {
			// TODO Auto-generated constructor stub
			int i;
			listCodeChar = new CodeChar[text.length];
			count = text.length;
			for (i=0; i<text.length; i++) {
				listCodeChar[i] = new CodeChar(text[i], colors[i]);
			}
			this.str = new String(text);
		}
		
		/** CodeString에 CodeStringType이 있는게 아니라 CodeChar에 있어야 한다. 
		 * 왜냐하면 editText의 setText가 ArrayListCodeChar혹은 CodeString이기 때문이다.
		 * CodeString에 type이 있다면 setText호출시 CodeString으로 변환할때 type이 소실된다. 
		 * @param text
		 * @param colors
		 * @param types
		 */
		public CodeString(char[] text, int[] colors, byte[] types) {
			// TODO Auto-generated constructor stub
			int i;
			listCodeChar = new CodeChar[text.length];
			count = text.length;
			for (i=0; i<text.length; i++) {
				listCodeChar[i] = new CodeChar(text[i], colors[i], types[i]);
			}
			this.str = new String(text);
		}
		
		public CodeString(String text, int textColor) {
			int i;
			if (text==null) text="";
			listCodeChar = new CodeChar[text.length()];
			count = text.length();
			for (i=0; i<text.length(); i++) {
				listCodeChar[i] = new CodeChar(text.charAt(i), textColor);
			}
			this.str = text;
		}
		
		
		/** len : text의 실제길이(text.length가 아니다)*/
		public CodeString(CodeChar[] text, int len) {
			try{
			this.listCodeChar = text;
			count = len;
			int i;
			char[] buf = new char[len];
			for (i=0; i<buf.length; i++) {
				buf[i] = text[i].c;
			}
			this.str = new String(buf);
			}catch(Exception e) {
			}
		}
		
		
		public boolean equals(String str) {
			try {
			if (this.toString().equals(str)) return true;
			}catch(Exception e) {
				int a;
				a=0;
				a++;
				e.printStackTrace();
			}
			return false;
		}
		
		public boolean equals(CodeString str) {
			if (this.toString().equals(str.toString())) return true;
			return false;
		}
		
		public int length() {
			// TODO Auto-generated method stub
			return count;
		}
		public CodeChar charAt(int i) {
			// TODO Auto-generated method stub
			return listCodeChar[i];
		}
		/** start포함, end미포함, 현재 스트링이 빈스트링일 경우 빈스트링 리턴*/
		public CodeString substring(int start, int end) {
			// TODO Auto-generated method stub
			if (end-start<0) return null;
			if (this.count<=0) return new CodeString("",Compiler.textColor);
			
			if (end>listCodeChar.length) return new CodeString("",Compiler.textColor);
			int i;
			CodeChar[] r = new CodeChar[end-start];
			int count = 0;
			for (i=start; i<end; i++) {
				r[count++] = listCodeChar[i];
			}
			return new CodeString(r, r.length);
		}
		public CodeString substring(int i) {
			// TODO Auto-generated method stub
			return substring(i, this.length());
		}
		
		public String toString() {
			return str;
		}

		public int indexOf(String string) {
			// TODO Auto-generated method stub
			return toString().indexOf(string);
		}
		
		public CodeString concate(CodeString str) {
			if (str==null) return this;
			listCodeChar = com.gsoft.common.Util.Array.InsertNoSpaceError(str.listCodeChar, 0, 
					listCodeChar, length(), str.length());
			count = listCodeChar.length;
			return new CodeString(listCodeChar, listCodeChar.length);
		}
		
		public void setColor(int color) {
			int i;
			for (i=0; i<count; i++) {
				listCodeChar[i].color = color;
			}
		}
		
		public void setType(byte type) {
			int i;
			for (i=0; i<count; i++) {
				listCodeChar[i].type = type;
			}
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			if (this.listCodeChar!=null) {
				int i;
				for (i=0; i<this.listCodeChar.length; i++) {
					listCodeChar[i] = null;
				}
			}
			this.str = null;
		}

		
	}

}