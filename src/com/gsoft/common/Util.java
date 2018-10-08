package com.gsoft.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;

import com.gsoft.common.Code.CodeChar;
import com.gsoft.common.Code.CodeString;
import com.gsoft.common.Code.CodeStringType;
import com.gsoft.common.Compiler_types.FindClassParams;
import com.gsoft.common.Compiler_types.FindVarUseParams;
import com.gsoft.common.Compiler_types.HighArray_CodeString;
import com.gsoft.common.Compiler_types.IReset;
import com.gsoft.common.Compiler_types.IndexForHighArray;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.IO.FileHelper;
import com.gsoft.common.Sizing.Rectangle;
import com.gsoft.common.Sizing.Size;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.Control;
import com.gsoft.common.gui.Dialog;
import com.gsoft.common.gui.EditRichText;
import com.gsoft.common.gui.EditText;
import com.gsoft.common.gui.Menu;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.EditRichText.Character;
import com.gsoft.common.gui.EditRichText.TextLine;
import com.gsoft.common.gui.FileDialog.SortByTime;
import com.gsoft.common.gui.MenuWithScrollBar.ButtonLine;
import com.gsoft.common.gui.MenuWithScrollBar_EditText.EditTextLine;

public class Util {
	/**오프셋 i 를 줄마다 칸수가 같은 스트링으로 변환한다.*/
	public static String getLineOffset(int i) {
		if (0<=i && i<=9) return new String(i+"         ");
		else if (10<=i && i<=99) return new String(i+"        ");
		else if (100<=i && i<=999) return new String(i+"       ");
		else if (1000<=i && i<=9999) return new String(i+"      ");
		else if (10000<=i && i<=99999) return new String(i+"     ");
		else if (100000<=i && i<=999999) return new String(i+"    ");
		else if (1000000<=i && i<=9999999) return new String(i+"   ");
		else if (10000000<=i && i<=99999999) return new String(i+"  ");
		else if (100000000<=i && i<=999999999) return new String(i+" ");
		else return String.valueOf(i);
	}
	
	public static void copy(byte[] src, int srcIndex, byte[] dest, int destIndex, int len) {
		int i, k;
		for (i=srcIndex, k=destIndex; i<srcIndex+len; i++, k++) {
			dest[k] = src[i];
		}
	}
	
	public static class Math {
		public static int min(int n1, int n2) {
			if (n1>=n2) return n2;
			return n1;
		}
		public static int max(int n1, int n2) {
			if (n1>=n2) return n1;
			return n2;
		}
	}
	
	/** 아주 큰 배열을 고정크기(arrayLimit)의 작은 배열 여러개로 나눈 array이다.*/
	public static class HighArrayForReading {
		int arrayLimit;
		
		ArrayList data;
		
		/** @param arrayLimit : 작은 배열의 limit*/
		public HighArrayForReading(int arrayLimit) {
			this.arrayLimit = arrayLimit;
			data = new ArrayList(10);
		}
		
		public int getCount() {
			// 마지막 item 을 얻는다.
			ArrayList arrItem = (ArrayList) data.getItem(data.count-1);
			int len = arrayLimit * (data.count-1) + arrItem.count;
			return len;
		}
		
		/** 모든 자원을 해제한다.*/
		public void reset() {
			int j;
			for (j=0; j<data.list.length; j++) {
				ArrayListChar list = (ArrayListChar) data.getItem(j);
				if (list!=null) list.reset();
				list = null;
			}
			data.reset();
		}
		
				
		public Object get(int index) {
			int indexOfData = index / arrayLimit;
			int indexInArray = index % arrayLimit;
			
			ArrayList arrItem = (ArrayList) data.getItem(indexOfData);
			return arrItem.getItem(indexInArray);
		}
		
		public void add(Object o) {
			if (data.count==0) {
				ArrayList newItem = new ArrayList(arrayLimit);
				data.add(newItem);
			}
			
			ArrayList item;
			
			// 마지막 item을 얻는다.
			ArrayList arrItem = (ArrayList) data.getItem(data.count-1);
			if (arrItem.count>=arrayLimit) { // 새로운 item을 생성해서 넣는다.
				ArrayList newItem = new ArrayList(arrayLimit);
				data.add(newItem);
				item = newItem;
			}
			else {
				item = arrItem;
			}
			
			item.add(o);
		}
		
		public Object[] toArray() {
			if (data.count<=0) {
				return new Object[0];
			}
			
			int i, j;
			// 마지막 item을 얻는다.
			ArrayList arrItem = (ArrayList) data.getItem(data.count-1);
			int len = arrayLimit * (data.count-1) + arrItem.count;
			
			Object[] r = new Object[len];
			int k=0;
			
			for (i=0; i<data.count; i++) {
				ArrayList item = (ArrayList) data.getItem(i);
				for (j=0; j<item.count; j++) {
					r[k] =  item.getItem(j);
					k++;
				}
			}
			
			return r;
		}
		
	}//public static class HighArrayForReading {
	
	

	
	
	/** 아주 큰 배열을 고정크기(arrayLimit)의 작은 배열 여러개로 나눈 array이다.*/
	public static class HighArrayForReading_char implements IReset {
		int arrayLimit;
		
		ArrayList data;
		
		/** @param arrayLimit : 작은 배열의 limit*/
		public HighArrayForReading_char(int arrayLimit) {
			this.arrayLimit = arrayLimit;
			data = new ArrayList(10);
		}
		
		public int getCount() {
			// 마지막 item을 얻는다.
			ArrayListChar arrItem = (ArrayListChar) data.getItem(data.count-1);
			int len = arrayLimit * (data.count-1) + arrItem.count;
			return len;
		}
		
		/** 모든 자원을 해제한다.*/
		public void destroy() {
			int j;
			for (j=0; j<data.list.length; j++) {
				ArrayListChar list = (ArrayListChar) data.getItem(j);
				if (list!=null) list.reset();
				list = null;
			}
			data.reset();
		}
		
		
				
		public char get(int index) {
			int indexOfData = index / arrayLimit;
			int indexInArray = index % arrayLimit;
			
			ArrayListChar arrItem = (ArrayListChar) data.getItem(indexOfData);
			return arrItem.getItem(indexInArray);
		}
		
		public void add(char c) {
			if (data.count==0) {
				ArrayListChar newItem = new ArrayListChar(arrayLimit);
				data.add(newItem);
			}
			
			ArrayListChar item;
			
			// 마지막 item을 얻는다.
			ArrayListChar arrItem = (ArrayListChar) data.getItem(data.count-1);
			if (arrItem.count>=arrayLimit) { // 새로운 item을 생성해서 넣는다.
				ArrayListChar newItem = new ArrayListChar(arrayLimit);
				data.add(newItem);
				item = newItem;
			}
			else {
				item = arrItem;
			}			
			item.add(c);
		}
		
		public void add(String str) {
			int i;
			for (i=0; i<str.length(); i++) {
				this.add(str.charAt(i));
			}
		}
		
		public String toString() {
			int i;
			int len = this.getCount();
			char[] r = new char[len]; 
			for (i=0; i<len ;i++) {
				char c = this.get(i);
				r[i] = c;
			}
			return new String(r);
		}
		
		public char[] toArray() {
			if (data.count<=0) {
				return new char[0];
			}
			
			int i, j;
			// 마지막 item을 얻는다.
			ArrayListChar arrItem = (ArrayListChar) data.getItem(data.count-1);
			int len = arrayLimit * (data.count-1) + arrItem.count;
			
			char[] r = new char[len];
			int k=0;
			
			for (i=0; i<data.count; i++) {
				ArrayListChar item = (ArrayListChar) data.getItem(i);
				for (j=0; j<item.count; j++) {
					r[k] =  item.getItem(j);
					k++;
				}
			}
			
			return r;
		}
		
	}//public static class HighArrayForReading {
	
	
	/** 아주 큰 배열을 고정크기(arrayLimit)의 작은 배열 여러개로 나눈 array이다.*/
	public static class HighArrayForReading_CodeChar implements IReset {
		int arrayLimit;
		
		ArrayList data;
		
		/** @param arrayLimit : 작은 배열의 limit*/
		public HighArrayForReading_CodeChar (int arrayLimit) {
			this.arrayLimit = arrayLimit;
			data = new ArrayList(10);
		}
		
		/** 모든 자원을 해제한다.*/
		public void destroy() {
			int j;
			for (j=0; j<data.list.length; j++) {
				ArrayListCodeChar list = (ArrayListCodeChar) data.getItem(j);
				if (list!=null) list.destroy();
				list = null;
			}
			data.reset();
		}
		
		public int getCount() {
			// 마지막 item을 얻는다.
			ArrayListCodeChar arrItem = (ArrayListCodeChar) data.getItem(data.count-1);
			int len = arrayLimit * (data.count-1) + arrItem.count;
			return len;
		}
		
		public String toString() {
			int i;
			int len = this.getCount();
			char[] r = new char[len]; 
			for (i=0; i<len ;i++) {
				CodeChar c = this.get(i);
				r[i] = c.c;
			}
			return new String(r);
		}
		
		
				
		public CodeChar get(int index) {
			int indexOfData = index / arrayLimit;
			int indexInArray = index % arrayLimit;
			
			ArrayListCodeChar arrItem = (ArrayListCodeChar) data.getItem(indexOfData);
			return arrItem.getItem(indexInArray);
		}
		
		public void add(CodeChar c) {
			if (data.count==0) {
				ArrayListCodeChar newItem = new ArrayListCodeChar(arrayLimit);
				data.add(newItem);
			}
			
			ArrayListCodeChar item;
			
			// 마지막 item을 얻는다.
			ArrayListCodeChar arrItem = (ArrayListCodeChar) data.getItem(data.count-1);
			if (arrItem.count>=arrayLimit) { // 새로운 item을 생성해서 넣는다.
				ArrayListCodeChar newItem = new ArrayListCodeChar(arrayLimit);
				data.add(newItem);
				item = newItem;
			}
			else {
				item = arrItem;
			}
			
			
			item.add(c);
			
		}
		
		public void concate(CodeString str) {
			int i;
			for (i=0; i<str.count; i++) {
				add(str.charAt(i));
			}
		}
		
		public CodeChar[] toArray() {
			if (data.count<=0) {
				return new CodeChar[0];
			}
			
			int i, j;
			// 마지막 item을 얻는다.
			ArrayListCodeChar arrItem = (ArrayListCodeChar) data.getItem(data.count-1);
			int len = arrayLimit * (data.count-1) + arrItem.count;
			
			CodeChar[] r = new CodeChar[len];
			int k=0;
			
			for (i=0; i<data.count; i++) {
				ArrayListCodeChar item = (ArrayListCodeChar) data.getItem(i);
				for (j=0; j<item.count; j++) {
					r[k] =  item.getItem(j);
					k++;
				}
			}
			
			return r;
		}
		
	}//public static class HighArrayForReading_CodeChar {
	
	public static class IndexOfHighArray {
		/** HighArray 내에 있는 array의 번호, 이것은 data의 인덱스이다.*/
		int arrayNumber;
		/** HighArray 내에 있는 array의 스트링을 가리키는 인덱스*/
		int offset;
		
		IndexOfHighArray(int arrayNumber, int offset) {
			this.arrayNumber = arrayNumber;
			this.offset = offset;
		}
	}
	
	
	/*public static class HighArray {
		int arrayLimit;
		
		ArrayList data;
		
		public HighArray(int arrayLimit) {
			this.arrayLimit = arrayLimit;
			data = new ArrayList(10);
		}
		
		
		public void destroy() {
			int j;
			for (j=0; j<data.list.length; j++) {
				ArrayList list = (ArrayList) data.getItem(j);
				if (list!=null) list.reset();
				list = null;
			}
			data.reset();
		}
		
		
		
		public int index(int arrayNumber, int offset) {
			int i;
			int index = 0;
			// 이전 array까지의 개수들의 합
			for (i=0; i<arrayNumber; i++) {
				ArrayList arr = (ArrayList) data.getItem(i);
				index += arr.count;
			}
			index += offset;
			return index;
		}
		
		
		public IndexOfHighArray indexRelative(int index) {
			int indexOfData = -1;
			int i;
			int len = 0;   // 현재 array까지의 개수들의 합
			int oldLen = 0;// 이전 array까지의 개수들의 합
			for (i=0; i<data.count; i++) {
				ArrayList arr = (ArrayList) data.getItem(i);
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
			return new IndexOfHighArray(indexOfData, indexInArray);
		}
		
		
		
		public void indexRelative(Object owner, int index, IndexOfHighArray result) {
			int indexOfData = -1;
			int i;
			int len = 0;    // 현재 array까지의 개수들의 합
			int oldLen = 0; // 이전 array까지의 개수들의 합
			for (i=0; i<data.count; i++) {
				ArrayList arr = (ArrayList) data.getItem(i);
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
		}
		
		
		public int getCount() {
			// 마지막 item을 얻는다.
			ArrayList arrItem = (ArrayList) data.getItem(data.count-1);
			//int len = arrayLimit * (data.count-1) + arrItem.count;
			int i;
			int len = 0;
			for (i=0; i<data.count-1; i++) {
				ArrayList arr = (ArrayList) data.getItem(i);
				len += arr.count;
			}
			len += arrItem.count;
			return len;
		}
				
		public Object getItem(int index) {
			//int indexOfData = index / arrayLimit;
			//int indexInArray = index % arrayLimit;
			
			IndexOfHighArray indexOfHigh = this.indexRelative(index);
			
			ArrayList arrItem = (ArrayList) data.getItem(indexOfHigh.arrayNumber);
			return arrItem.getItem(indexOfHigh.offset);
		}
		
		
		
		public void add(Object o) {
			if (data.count==0) {
				ArrayList newItem = new ArrayList(arrayLimit);
				data.add(newItem);
			}
			
			ArrayList item;
			
			// 마지막 item을 얻는다.
			ArrayList arrItem = (ArrayList) data.getItem(data.count-1);
			if (arrItem.count>=arrayLimit) { // 새로운 item을 생성해서 넣는다.
				ArrayList newItem = new ArrayList(arrayLimit);
				data.add(newItem);
				item = newItem;
			}
			else {
				item = arrItem;
			}
			
			item.add(o);
		}
		
		public void add(Object[] o) {
			int i;
			for (i=0; i<o.length; i++) {
				this.add(o[i]);
			}
		}
		
	}*/
	
	
	/** 아주 큰 배열을 가변크기(처음 로딩시에는 고정크기 arrayLimit를 갖지만 
	 * 수정을 하면 가변크기가 된다.)의 작은 배열 여러개로 나눈 array이다.*/
	public static class HighArray<T> implements IReset {
		int arrayLimit;
		
		/**ArrayList[]*/
		ArrayList data;
		
		
		//public int count;

		public String name;

		public int resizeInc = 100;

		public void list(int index) {
			
		}
		
		/** @param arrayLimit : 작은 배열의 limit*/
		public HighArray (int arrayLimit) {
			this.arrayLimit = arrayLimit;
			data = new ArrayList(10);
		}
		
		/** 모든 자원을 해제한다.*/
		public void destroy() {
			int j;
			for (j=0; j<data.list.length; j++) {
				ArrayList list = (ArrayList) data.getItem(j);
				if (list!=null) list.reset();
				list = null;
			}
			data.reset();
		}
		
		public int getCount() {
			// 마지막 item을 얻는다.
			ArrayList arrItem = null;
			if (data.count>0) {
				arrItem = (ArrayList) data.getItem(data.count-1);
			
				int i;
				int len = 0;
				for (i=0; i<data.count-1; i++) {
					ArrayList arr = (ArrayList) data.getItem(i);
					len += arr.count;
				}
				len += arrItem.count;
				return len;
			}
			else {
				return 0;
			}
		}
				
		@SuppressWarnings("unchecked")
		public T getItem(int index) {
			//int indexOfData = index / arrayLimit;
			//int indexInArray = index % arrayLimit;
			
			int indexOfData = -1;
			int i;
			int len = 0;
			int oldLen = 0;
			for (i=0; i<data.count; i++) {
				ArrayList arr = (ArrayList) data.getItem(i);
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
			
			int indexInArray = index - oldLen;
			
			ArrayList arrItem = (ArrayList) data.getItem(indexOfData);
			return (T)arrItem.getItem(indexInArray);
		}
		
		public void add(T c) {
			if (data.count==0) {
				ArrayList newItem = new ArrayList(arrayLimit);
				data.add(newItem);
				newItem.resizeInc = this.resizeInc;
			}
			
			ArrayList item;
			
			// 마지막 item을 얻는다.
			ArrayList arrItem = (ArrayList) data.getItem(data.count-1);
			if (arrItem.count>=arrayLimit) { // 새로운 item을 생성해서 넣는다.
				ArrayList newItem = new ArrayList(arrayLimit);
				newItem.resizeInc = this.resizeInc;
				data.add(newItem);
				item = newItem;
			}
			else {
				item = arrItem;
			}
			
			item.add(c);
			//count++;
		}
		
		public void add(T[] o) {
			int i;
			for (i=0; i<o.length; i++) {
				this.add(o[i]);
			}
		}
		
		public void insert(int index, T c) {
			int indexOfData = -1;
			int i;
			int len = 0;
			int oldLen = 0;
			for (i=0; i<data.count; i++) {
				ArrayList arr = (ArrayList) data.getItem(i);
				oldLen = len;
				len += arr.count;
				if (index<len) {
					indexOfData = i;
					break;
				}
			}
			/*if (index>=len) {
				return; // ArrayIndexOUtOfBoundsException
			}*/
			
			int indexInArray = index - oldLen;
			
			ArrayList arrItem = (ArrayList) data.getItem(indexOfData);
			//T oldItem = (T)arrItem.getItem(indexInArray);
			
			arrItem.insert(indexInArray, c);
		}
		
		public String toString() {
			int i;
			int len = this.getCount();
			HighArray_char r = new HighArray_char(500); 
			for (i=0; i<len; i++) {
				T item = this.getItem(i);
				r.add(i+":"+item.toString()+"   ");
			}
			return r.getItems();
		}
		
		
		/*public T[] toArray() {
			if (data.count<=0) {
				return new T[0];
			}
			
			int i, j;
			// 마지막 item을 얻는다.
			//ArrayListT arrItem = (ArrayListT) data.getItem(data.count-1);
			//int len = arrayLimit * (data.count-1) + arrItem.count;
			int len = this.getCount();
			
			T[] r = new T[len];
			int k=0;
			
			for (i=0; i<data.count; i++) {
				ArrayList item = (ArrayList) data.getItem(i);
				for (j=0; j<item.count; j++) {
					r[k] =  (T)item.getItem(j);
					k++;
				}
			}			
			return r;
		}

		public T[] getItems() {
			// TODO Auto-generated method stub
			return this.toArray();
		}*/

		
	}//public static class HighArray<T> {
	
	
	
	public static class HighArray_char {
		int arrayLimit;
		
		ArrayList data;
		
		public HighArray_char(int arrayLimit) {
			this.arrayLimit = arrayLimit;
			data = new ArrayList(10);
		}
		
		/** 모든 자원을 해제한다.*/
		public void destroy() {
			int j;
			for (j=0; j<data.list.length; j++) {
				ArrayListChar list = (ArrayListChar) data.getItem(j);
				if (list!=null) list.reset();
				list = null;
			}
			data.reset();
		}
		
		
		/** 상대인덱스(arrayNumber,offset)를 절대인덱스로 바꾼다.*/
		public int index(int arrayNumber, int offset) {
			int i;
			int index = 0;
			// 이전 array까지의 개수들의 합
			for (i=0; i<arrayNumber; i++) {
				ArrayListChar arr = (ArrayListChar) data.getItem(i);
				index += arr.count;
			}
			index += offset;
			return index;
		}
		
		/** 절대인덱스를 상대인덱스(arrayNumber,offset)로 바꾼다.*/
		public IndexOfHighArray indexRelative(int index) {
			int indexOfData = -1;
			int i;
			int len = 0;   // 현재 array까지의 개수들의 합
			int oldLen = 0;// 이전 array까지의 개수들의 합
			for (i=0; i<data.count; i++) {
				ArrayListChar arr = (ArrayListChar) data.getItem(i);
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
			return new IndexOfHighArray(indexOfData, indexInArray);
		}
		
		
		/** 절대인덱스를 상대인덱스(arrayNumber,offset)로 바꾼다.*/
		public void indexRelative(Object owner, int index, IndexOfHighArray result) {
			int indexOfData = -1;
			int i;
			int len = 0;    // 현재 array까지의 개수들의 합
			int oldLen = 0; // 이전 array까지의 개수들의 합
			for (i=0; i<data.count; i++) {
				ArrayListChar arr = (ArrayListChar) data.getItem(i);
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
		}
		
		
		public int getCount() {
			// 마지막 item을 얻는다.
			ArrayListChar arrItem = (ArrayListChar) data.getItem(data.count-1);
			//int len = arrayLimit * (data.count-1) + arrItem.count;
			int i;
			int len = 0;
			for (i=0; i<data.count-1; i++) {
				ArrayListChar arr = (ArrayListChar) data.getItem(i);
				len += arr.count;
			}
			len += arrItem.count;
			return len;
		}
		
		public String getItems() {
			int i;
			char[] r = new char[this.getCount()];
			for (i=0; i<r.length; i++) {
				r[i] = this.getItem(i);
			}
			return new String(r);
		}
				
		public char getItem(int index) {
			//int indexOfData = index / arrayLimit;
			//int indexInArray = index % arrayLimit;
			
			IndexOfHighArray indexOfHigh = this.indexRelative(index);
			
			ArrayListChar arrItem = (ArrayListChar) data.getItem(indexOfHigh.arrayNumber);
			return arrItem.getItem(indexOfHigh.offset);
		}
		
		
		public void add(String str) {
			int i;
			int len = str.length();
			for (i=0; i<len; i++) {
				this.add(str.charAt(i));
			}
		}
		
		
		
		public void add(char c) {
			if (data.count==0) {
				ArrayListChar newItem = new ArrayListChar(arrayLimit);
				data.add(newItem);
			}
			
			ArrayListChar item;
			
			// 마지막 item을 얻는다.
			ArrayListChar arrItem = (ArrayListChar) data.getItem(data.count-1);
			if (arrItem.count>=arrayLimit) { // 새로운 item을 생성해서 넣는다.
				ArrayListChar newItem = new ArrayListChar(arrayLimit);
				data.add(newItem);
				item = newItem;
			}
			else {
				item = arrItem;
			}
			
			item.add(c);
		}
		
	}
	
	
	
	public static class HighArray_byte {
		int arrayLimit;
		
		ArrayList data;
		
		public HighArray_byte(int arrayLimit) {
			this.arrayLimit = arrayLimit;
			data = new ArrayList(10);
		}
		
		/** 모든 자원을 해제한다.*/
		public void destroy() {
			int j;
			for (j=0; j<data.list.length; j++) {
				ArrayListByte list = (ArrayListByte) data.getItem(j);
				if (list!=null) list.reset();
				list = null;
			}
			data.reset();
		}
		
		
		/** 상대인덱스(arrayNumber,offset)를 절대인덱스로 바꾼다.*/
		public int index(int arrayNumber, int offset) {
			int i;
			int index = 0;
			// 이전 array까지의 개수들의 합
			for (i=0; i<arrayNumber; i++) {
				ArrayListByte arr = (ArrayListByte) data.getItem(i);
				index += arr.count;
			}
			index += offset;
			return index;
		}
		
		/** 절대인덱스를 상대인덱스(arrayNumber,offset)로 바꾼다.*/
		public IndexOfHighArray indexRelative(int index) {
			int indexOfData = -1;
			int i;
			int len = 0;   // 현재 array까지의 개수들의 합
			int oldLen = 0;// 이전 array까지의 개수들의 합
			for (i=0; i<data.count; i++) {
				ArrayListByte arr = (ArrayListByte) data.getItem(i);
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
			return new IndexOfHighArray(indexOfData, indexInArray);
		}
		
		
		/** 절대인덱스를 상대인덱스(arrayNumber,offset)로 바꾼다.*/
		public void indexRelative(Object owner, int index, IndexOfHighArray result) {
			int indexOfData = -1;
			int i;
			int len = 0;    // 현재 array까지의 개수들의 합
			int oldLen = 0; // 이전 array까지의 개수들의 합
			for (i=0; i<data.count; i++) {
				ArrayListByte arr = (ArrayListByte) data.getItem(i);
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
		}
		
		
		public int getCount() {
			// 마지막 item을 얻는다.
			ArrayListByte arrItem = (ArrayListByte) data.getItem(data.count-1);
			//int len = arrayLimit * (data.count-1) + arrItem.count;
			int i;
			int len = 0;
			for (i=0; i<data.count-1; i++) {
				ArrayListByte arr = (ArrayListByte) data.getItem(i);
				len += arr.count;
			}
			len += arrItem.count;
			return len;
		}
		
		
				
		public byte getItem(int index) {
			//int indexOfData = index / arrayLimit;
			//int indexInArray = index % arrayLimit;
			
			IndexOfHighArray indexOfHigh = this.indexRelative(index);
			
			ArrayListByte arrItem = (ArrayListByte) data.getItem(indexOfHigh.arrayNumber);
			return (byte) arrItem.getItem(indexOfHigh.offset);
		}
		
		
		public void add(byte[] arr) {
			int i;
			int len = arr.length;
			for (i=0; i<len; i++) {
				this.add(arr[i]);
			}
		}
		
		
		
		public void add(byte c) {
			if (data.count==0) {
				ArrayListByte newItem = new ArrayListByte(arrayLimit);
				data.add(newItem);
			}
			
			ArrayListByte item;
			
			// 마지막 item을 얻는다.
			ArrayListByte arrItem = (ArrayListByte) data.getItem(data.count-1);
			if (arrItem.count>=arrayLimit) { // 새로운 item을 생성해서 넣는다.
				ArrayListByte newItem = new ArrayListByte(arrayLimit);
				data.add(newItem);
				item = newItem;
			}
			else {
				item = arrItem;
			}
			
			item.add(c);
		}
		
	}
	
	
	
	public static class JarFile {
		/** @param destFilename : '/'으로 시작하는 완벽한 path, 확장자는 .jar 파일
		 * @param srcFilename : '/'으로 시작하는 완벽한 path, 압축을 풀 디렉토리*/
		public static boolean compress(String srcFilename, String destFilename) throws IOException {
			
			return false;
		}
		
		/** @param srcFilename : '/'으로 시작하는 완벽한 path, 확장자는 .jar파일
		 * @param destFilename : '/'으로 시작하는 완벽한 path, 압축을 풀 디렉토리*/
		public static boolean decompress(String srcFilename, String destFilename) throws IOException {
			File file = new File(srcFilename);
			try {
				File destFile = new File(destFilename);
				destFile.mkdir();
				
				java.util.jar.JarFile jarFile = new java.util.jar.JarFile(file,false);
				Enumeration<JarEntry> entries = jarFile.entries();
				String directoryName;
				File writeFile;
				
				byte[] buf = new byte[10000];
				
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					CompilerHelper.showMessage(true, entry.getName());
					if (entry.isDirectory()) {
						directoryName = entry.getName();
						String name = destFilename + File.separator + directoryName;
						writeFile = new File(name);
						writeFile.mkdirs();
					}
					else {
						String filename = entry.getName();
						writeFile = new File(destFilename + File.separator + filename);
						directoryName = FileHelper.getDirectory(writeFile.getAbsolutePath());
						if (directoryName!=null && directoryName.equals("")==false) {
							File d = new File(directoryName);
							d.mkdirs();
						}
						
						long size = entry.getSize();
						InputStream is = jarFile.getInputStream(entry);
						FileOutputStream os = new FileOutputStream(writeFile);
						
						
						while(true) {
							int count = is.read(buf, 0, buf.length);
							if (count<0) {
								break;
							}
							os.write(buf, 0, count);
						}
						os.close();
					}
				}
				jarFile.close();
				return true;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				return false;
			}
		}
	}
	
	/*public static class Hashtable  {
		int[] indices;
		ArrayList[] references;
		int initLenOfBucket;
		
		public Hashtable(int lenOfIndices, int initLenOfBucket) {
			indices = new int[lenOfIndices];
			references = new ArrayList[lenOfIndices];
			this.initLenOfBucket = initLenOfBucket;
			
		}
		
		public void input(FindVarUseParams data) {
			int n = data.number;
			int index = n % indices.length;
			if (references[index]==null) {
				references[index] = new ArrayList(initLenOfBucket);
			}
			references[index].add(data);
			
		}
		
		public FindVarUseParams getData(FindVarUseParams data) {
			int n = data.number;
			int index = n % indices.length;
			ArrayList list = references[index];
			int i;
			for (i=0; i<list.count; i++) {
				FindVarUseParams varUse = (FindVarUseParams) list.getItem(i);
				if (varUse.number==n) return varUse;
			}
			return null;
			
		}
	}*/
	
	public static class Hashtable2 {
		//int[] indices;
		ArrayList[] references;
		int initLenOfBucket;
		HighArray_CodeString src;
		
		public Hashtable2(HighArray_CodeString src, int lenOfIndices, int initLenOfBucket) {
			this.src = src;
			//indices = new int[lenOfIndices];
			references = new ArrayList[lenOfIndices];
			this.initLenOfBucket = initLenOfBucket;
			
		}
		
		public void reset() {
			int i;
			for (i=0; i<references.length; i++) {
				if (references[i] != null)	{
					references[i].reset();
					references[i] = null;
				}
			}
		}
		
		/** varUse의 originName을 key로 하여 해시에 넣는다.*/
		public void input(FindVarUseParams data) {
			String name = src.getItem(data.index()).str;
			char ch = name.charAt(0);
			int index = ch % references.length;
			//int index = ch;
			if (references[index]==null) {
				references[index] = new ArrayList(initLenOfBucket);
			}
			references[index].add(data);
			
		}
		
		public ArrayList getData(String dataName) {
			char ch = dataName.charAt(0);
			int index = ch % references.length;
			//int index = ch;
			ArrayList list = references[index];
			if (list==null) return null;
			int i;
			ArrayList result = new ArrayList(10);
			for (i=0; i<list.count; i++) {
				FindVarUseParams varUse = (FindVarUseParams) list.getItem(i);
				String name = src.getItem(varUse.index()).str;
				if (name.equals(dataName)) {
					result.add(varUse);
				}
			}
			return result;
			
		}
		
		public FindVarUseParams getData(String dataName, int indexOfVarUse) {
			char ch = dataName.charAt(0);
			int index = ch % references.length;
			//int index = ch;
			ArrayList list = references[index];
			if (list==null) return null;
			int i;
			for (i=0; i<list.count; i++) {
				FindVarUseParams varUse = (FindVarUseParams) list.getItem(i);
				if (varUse.index()==indexOfVarUse) {
					return varUse;
				}
			}
			return null;
			
		}
	}
	
	/** String을 key로 하는 해시테이블*/
	public static class Hashtable2_String {
		public static class HashItem {
			String key;
			Object data;
			HashItem(String key, Object data) {
				this.key = key;
				this.data = data;
			}
		}
		
		ArrayList[] references;
		int initLenOfBucket;
		
		public Hashtable2_String(int lenOfIndices, int initLenOfBucket) {
			references = new ArrayList[lenOfIndices];
			this.initLenOfBucket = initLenOfBucket;
		}
		
		public void reset() {
			int i;
			for (i=0; i<references.length; i++) {
				if (references[i] != null)	{
					references[i].reset();
					references[i] = null;
				}
			}
		}
		
		public void input(String key, Object data) {
			char ch = key.charAt(0);
			int index = ch % references.length;
			//int index = ch;
			if (references[index]==null) {
				references[index] = new ArrayList(initLenOfBucket);
			}
			references[index].add(new HashItem(key, data));
			
		}
		
		public Object getData(String key) {
			char ch = key.charAt(0);
			int index = ch % references.length;
			//int index = ch;
			ArrayList list = references[index];
			if (list==null) return null;
			int i;
			for (i=0; i<list.count; i++) {
				HashItem item = (HashItem) list.getItem(i);
				if (item.key.equals(key)) {
					return item.data;
				}
			}
			return null;			
		} //getData()		
	}
	
	
	/** key 가 integer 인 좀더 일반적인 해시테이블이다.*/
	public static class Hashtable2_Object {
		
		public static class HashItem {
			int key;
			Object item;
			HashItem(int key, Object item) {
				this.key = key;
				this.item = item;
			}
		}		
		
		ArrayList[] references;
		int initLenOfBucket;
		
		public Hashtable2_Object(int lenOfIndices, int initLenOfBucket) {
			//indices = new int[lenOfIndices];
			references = new ArrayList[lenOfIndices];
			this.initLenOfBucket = initLenOfBucket;
			
		}
		
		public void reset() {
			int i;
			for (i=0; i<references.length; i++) {
				if (references[i] != null)	{
					references[i].reset();
					references[i] = null;
				}
			}
		}
		
		/** 호출시 넣는 정수인 key 와 data 로 HashItem을 만들어서 해시테이블에 넣는다.*/
		public void input(int key, Object data) {
			int index = key % references.length;
			//int index = ch;
			if (references[index]==null) {
				references[index] = new ArrayList(initLenOfBucket);
			}
			references[index].add(new HashItem(key,data));
			
		}
		
		/** 호출시 넣는 정수인 key 로 해시테이블의 HashItem들을 검색해서 
		 * input()시 넣어진 아이템을 찾는다.*/		
		public Object getData(int key) {
			int index = key % references.length;
			//int index = ch;
			ArrayList list = references[index];
			if (list==null) return null;
			int i;
			for (i=0; i<list.count; i++) {
				HashItem hashItem = (HashItem) list.getItem(i);
				if (hashItem.key==key) {
					return hashItem.item;
				}
			}
			return null;
			
		}
		
		
	}
	
	public static class Hashtable_FullClassName {
		//int[] indices;
		ArrayList[] references;
		int initLenOfBucket;
		
		public Hashtable_FullClassName(int lenOfIndices, int initLenOfBucket) {
			//indices = new int[lenOfIndices];
			references = new ArrayList[lenOfIndices];
			this.initLenOfBucket = initLenOfBucket;
			
		}
		
		/** 배열원소의 reset()을 호출하여 모든 메모리자원들을 해제한다.*/
		public void reset() {
			int i;
			for (i=0; i<references.length; i++) {
				if (references[i] != null)	{
					references[i].reset();
					references[i] = null;
				}
			}
		}
		
		/** add와 같음*/
		public synchronized void input(FindClassParams classParams) {
			String name = classParams.name;
			char ch = name.charAt(0);
			int index = ch % references.length;
			//int index = ch;
			
			if (references[index]==null) {
				references[index] = new ArrayList(initLenOfBucket);
			}
			synchronized(references[index]) {
				references[index].add(classParams);
			}
		}
		
		/** 기존 item을 대체한다.*/
		public synchronized void replace(FindClassParams classParams) {
			char ch = classParams.name.charAt(0);
			int index = ch % references.length;
			ArrayList list = references[index];
			int i;
			synchronized(references[index]) {
				for (i=0; i<list.count; i++) {
					FindClassParams r = (FindClassParams) list.getItem(i);
					String name = r.name;
					if (name.equals(classParams.name)) {
						list.list[i] = classParams;
					}
				}
			}
		}
		
		/** fullClassName으로 item을 검색한다.*/
		public synchronized FindClassParams getData(String fullClassName) {
			char ch = fullClassName.charAt(0);
			int index = ch % references.length;
			ArrayList list = references[index];
			if (list==null) return null;
			int i;
			synchronized(references[index]) {
				for (i=0; i<list.count; i++) {
					FindClassParams r = (FindClassParams) list.getItem(i);
					String name = r.name;
					if (name.equals(fullClassName)) {
						return r;
					}
				}
				return null;
			}
			
		}
		
	}
	
	/** generic, template stack*/
	public static class Stack <T>
    {
        class Node
        {
            public Node NodePtr;
            public T Data;
            public Node()
            {
                //Data = null;
                NodePtr = null;
            }
        }

        Node head;
        Node top;
        int len;
        public Stack()
        {
            Node node = new Node();
            head = node;
            top = node;
            len = 0;
        }

        public boolean IsEmpty()
        {
            if (head == top)
                return true;
            return false;
        }

        public void Push(T data)
        {
            Node node = new Node();
            node.Data = data;
            node.NodePtr = top;
            top = node;
            len++;
        }

        public T Get()
        {
        	if (top!=null) {
        		return top.Data;
        	}
        	else return null;
        }

        public T Pop() /*throws Exception*/
        {
            //if (IsEmpty()) throw new Exception("스택이 비어 있습니다.");
            T data = top.Data;
            top = top.NodePtr;
            len--;
            return data;
        }

    }
	
	public static class StringDel {
		public static java.lang.String clone(java.lang.String str) {
			char[] buf = new char[str.length()];
			str.getChars(0, buf.length, buf, 0);
			return new java.lang.String(str);
		}
	}
		
	public static class Date {
		static int country;
		static long countryMillis;
		static long userTimeMillis;
		static int mYoil;
		
		int year;
		int month;
		int date;
		boolean isAM;
		int hour;
		int min;
		int sec;
		
		/** 요일*/
		int day;
		
		//					   0, 1,  2,  3,  4,  5,  6,  7,  8,  9   10, 11, 12
		int[] dayOfMonth = 	  {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		int[] dayOfMonthSum = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365};
		
		/*2월29일이 있는 해는 4년마다 반복됩니다. 몇년인지 궁금하시다면, 올림픽이 있는 해를 알아보시면 됩니다.ㅋ

		(2012, 2016, 2020,...)

		하지만, 2100년과 같은 100의 배수인 연도에는 윤달이 없고, 
		2000년과 같은 100의배수이지만 400의 배수인 연도에는 윤달이 존재합니다*/
		
		// 72, 76, 80, 84, 88, 92, 96, 2000, 2004, 08, 12............16
		int sumOfYearThat2Has29Since1970 = 11; // 1년이 366인 해
		
		int getCountOf0229(int year) {
			int count = year / 4;
			count -= year / 100;
			count += year / 400;
			return count;
		}

		// 윤달(2월이 29일) = 
		public Date(long millisec) {
			long argMillisec = millisec + countryMillis + userTimeMillis;
			
			final int millisPerSec = 1000;
			//int millisPerMin = 1000 * 60;
			//int millisPerHour = 1000 * 60 * ;
			final int millisPerMin = millisPerSec * 60;
			final long millisPerHour = millisPerMin * 60;
			final long millisPerDay = millisPerHour * 24;
			final long millisPerYear = millisPerDay * 365;
			
			
			int yearSince1970 = (int) (argMillisec / millisPerYear);
			//if (yearSince1970<1) {
				year = yearSince1970 + 1970;
			/*}
			else {
				year = yearSince1970 + 1969;
			}*/
			
			// year와 1970사이의 2월29일을 갖는 해의 개수를 구한다.
			sumOfYearThat2Has29Since1970 = getCountOf0229(year) - getCountOf0229(1970);
			long millisec0229 = argMillisec - millisPerDay * sumOfYearThat2Has29Since1970;
			
			long millisRemainder = millisec0229 % millisPerYear;
			
			
			
			// 1년 아래의 날짜수, 즉 364일 이하
			int dateInRemainder = (int) (millisRemainder / millisPerDay);
			
			// millisPerDay아래의 millis
			int millisInDateRemainder = (int)(millisRemainder % millisPerDay);	
			
			
			
			hour = (int)(millisInDateRemainder / millisPerHour);
			
			// millisPerHour아래의 millis
			int millisInHourRemainder = (int)(millisInDateRemainder % millisPerHour);
			
			
			
			min = millisInHourRemainder / millisPerMin;
			
			int millisInMinRemainder = millisInHourRemainder % millisPerMin;
			
			
			
			sec = millisInMinRemainder / millisPerSec;
			
			
			
			int i;
			for (i=dayOfMonthSum.length-1; i>=0; i--) {
				if (dateInRemainder>dayOfMonthSum[i]) {
					break;
				}
			}
			if (i<0) {
				i=0;
				month = 1;
				date = dateInRemainder - dayOfMonthSum[i] + 1;
			}
			else {			
				month = i+1;
				date = dateInRemainder - dayOfMonthSum[i];
			}
			
			
			final long millisPer7Day = millisPerDay * 7;
			
			long millisIn7DayRemainder = millisec0229 % millisPer7Day;
			
			day = (int)((millisIn7DayRemainder+mYoil*millisPerDay) / millisPerDay);
			
			day = day % 7;
		}
		
		/** 요일*/
		public int getDay() {
			return day;
		}
		
		public int getDate() {
			return date;
		}
		
		public int getMonth() {
			return month;
		}
		
		public int getYear() {
			return year;
		}
		
		public int getHour(boolean isHalfDate) {
			if (isHalfDate) {
				if (hour>12) {
					isAM = false;
					return hour-12;
				}
				else {
					isAM = true;
					return hour;
				}
			}
			return hour;
		}
		
		public boolean getIsAM() {
			return isAM;
		}
		
		public int getMin() {
			return min;
		}
		
		public int getSec() {
			return sec;
		}
		
		/*public static void setCountry(int c) {
			country = c;
			final int millisPerSec = 1000;
			final int millisPerMin = millisPerSec * 60;
			final long millisPerHour = millisPerMin * 60;
			if (country==1) {
				//countryMillis = millisPerDay + 9*millisPerHour + 5*millisPerMin;
				countryMillis = 9*millisPerHour;
				
			}
		}*/
		
		public static void addUserTime(int day, int hour, int min, int yoil) {
			final int millisPerSec = 1000;
			final int millisPerMin = millisPerSec * 60;
			final long millisPerHour = millisPerMin * 60;
			final long millisPerDay = millisPerHour * 24;
			
			userTimeMillis = day * millisPerDay + hour *millisPerHour + min*millisPerMin;
			mYoil = yoil;
		}
		
		public static String getCurDateTime(boolean isHalfDate) {
			//Date.setCountry(1);
			Date.addUserTime(1,9,0,1);
			Date date = new Date(System.currentTimeMillis());
				
			String curTime = " " + String.valueOf(date.getHour(isHalfDate)) + ":" + 
					String.valueOf(date.getMin());
			if (date.getIsAM()) curTime += " AM";
			else curTime += " PM";
			
			switch(date.getDay()) {
			case 0: curTime += " Sun"; break;
			case 1: curTime += " Mon"; break;
			case 2: curTime += " Tue"; break;
			case 3: curTime += " Wed"; break;
			case 4: curTime += " Thu"; break;
			case 5: curTime += " Fri"; break;
			case 6: curTime += " Sat"; break;
			}
				
			String curDate = " " + String.valueOf(date.getMonth()) + "-" + 
					String.valueOf(date.getDate()) + "-" + String.valueOf(date.getYear());
					
			return curTime + "\n" + curDate;
		}
	}
	
	public static class Sort {
		public static void merge_sort(int[] num, int start, int end, boolean isAcending){ // Array를 두개의 덩어리로 나눔    
			int median = (start + end)/2;     
			if (start < end){         
				merge_sort(num, start, median, isAcending);         
				merge_sort(num, median+1, end, isAcending);          
				merge(num, start, median, end, isAcending);     
			} 
		}  
		private static void merge(int[] num, int start, int median, int end, boolean isAcending){     
			int i,j,k,m,n;     
			int[] tempArr = new int[num.length]; // 임시로 데이터를 저장할 배열    
			i = start;     
			j = median+1;     
			k = start;      
			while (i <= median && j <= end){
				if (isAcending) {
					tempArr[k++] = (num[i] > num [j]) ? num [j++] : num [i++]; 
				}
				else {
					tempArr[k++] = (num[i] < num [j]) ? num [j++] : num [i++];
				}
			}           // 아직 배열에 속하지 못한 부분들을 넣기 위한 부분    
			m = (i > median) ? j : i; // 아직 원소가 남아있는 덩어리가 어디인지 파악    
			n = (i > median) ? end : median; // 마찬가지로, for문의 끝 Index를 정하기 위함임     
			for (; m<=n; m++){ // 앞에서 구한 m, n으로 배열에 속하지 못한 원소들을 채워넣음       
				tempArr[k++] = num[m];     
			}      
			for (m=start; m<=end; m++){         
				num[m] = tempArr[m]; // 임시 배열에서 원래 배열로 데이터 옮기기    
			} 
		} 
		
		public static void merge_sort(String[] strs, int start, int end, boolean isAcending){ // Array를 두개의 덩어리로 나눔    
			int median = (start + end)/2;     
			if (start < end){         
				merge_sort(strs, start, median, isAcending);         
				merge_sort(strs, median+1, end, isAcending);          
				merge(strs, start, median, end, isAcending);     
			} 
		}  
		private static void merge(String[] strs, int start, int median, int end, boolean isAcending){     
			int i,j,k,m,n;     
			String[] tempArr = new String[strs.length]; // 임시로 데이터를 저장할 배열    
			i = start;     
			j = median+1;     
			k = start;      
			while (i <= median && j <= end){
				if (isAcending) {
					tempArr[k++] = (strs[i].compareTo(strs [j]) > 0) ? strs [j++] : strs [i++]; 
				}
				else {
					tempArr[k++] = (strs[i].compareTo(strs [j]) < 0) ? strs [j++] : strs [i++];
				}
			}           // 아직 배열에 속하지 못한 부분들을 넣기 위한 부분    
			m = (i > median) ? j : i; // 아직 원소가 남아있는 덩어리가 어디인지 파악    
			n = (i > median) ? end : median; // 마찬가지로, for문의 끝 Index를 정하기 위함임     
			for (; m<=n; m++){ // 앞에서 구한 m, n으로 배열에 속하지 못한 원소들을 채워넣음       
				tempArr[k++] = strs[m];     
			}      
			for (m=start; m<=end; m++){         
				strs[m] = tempArr[m]; // 임시 배열에서 원래 배열로 데이터 옮기기    
			} 
		}
		
		public static void merge_sort(SortByTime[] list, int start, int end, boolean isAcending){ // Array를 두개의 덩어리로 나눔    
			int median = (start + end)/2;     
			if (start < end){         
				merge_sort(list, start, median, isAcending);         
				merge_sort(list, median+1, end, isAcending);          
				merge(list, start, median, end, isAcending);     
			} 
		}  
		private static void merge(SortByTime[] list, int start, int median, int end, boolean isAcending){     
			int i,j,k,m,n;     
			SortByTime[] tempArr = new SortByTime[list.length]; // 임시로 데이터를 저장할 배열    
			i = start;     
			j = median+1;     
			k = start;      
			while (i <= median && j <= end){
				if (isAcending) {
					tempArr[k++] = (list[i].modifiedTime > list [j].modifiedTime) ? list [j++] : list [i++]; 
				}
				else {
					tempArr[k++] = (list[i].modifiedTime < list [j].modifiedTime) ? list [j++] : list [i++];
				}
			}           // 아직 배열에 속하지 못한 부분들을 넣기 위한 부분    
			m = (i > median) ? j : i; // 아직 원소가 남아있는 덩어리가 어디인지 파악    
			n = (i > median) ? end : median; // 마찬가지로, for문의 끝 Index를 정하기 위함임     
			for (; m<=n; m++){ // 앞에서 구한 m, n으로 배열에 속하지 못한 원소들을 채워넣음       
				tempArr[k++] = list[m];     
			}      
			for (m=start; m<=end; m++){         
				list[m] = tempArr[m]; // 임시 배열에서 원래 배열로 데이터 옮기기    
			} 
		}
		
		public static void merge_sort(File[] list, int start, int end, boolean isAcending){ // Array를 두개의 덩어리로 나눔    
			int median = (start + end)/2;     
			if (start < end){         
				merge_sort(list, start, median, isAcending);         
				merge_sort(list, median+1, end, isAcending);          
				merge(list, start, median, end, isAcending);     
			} 
		}  
		private static void merge(File[] list, int start, int median, int end, boolean isAcending){     
			int i,j,k,m,n;     
			File[] tempArr = new File[list.length]; // 임시로 데이터를 저장할 배열    
			i = start;     
			j = median+1;     
			k = start;      
			while (i <= median && j <= end){
				File file1 = list[i];
				File file2 = list[j];
				if (isAcending) {
					tempArr[k++] = (file1.getName().compareTo(file2.getName()) > 0) ? list [j++] : list [i++]; 
				}
				else {
					tempArr[k++] = (file1.getName().compareTo(file2.getName()) < 0) ? list [j++] : list [i++];
				}
			}           // 아직 배열에 속하지 못한 부분들을 넣기 위한 부분    
			m = (i > median) ? j : i; // 아직 원소가 남아있는 덩어리가 어디인지 파악    
			n = (i > median) ? end : median; // 마찬가지로, for문의 끝 Index를 정하기 위함임     
			for (; m<=n; m++){ // 앞에서 구한 m, n으로 배열에 속하지 못한 원소들을 채워넣음       
				tempArr[k++] = list[m];     
			}      
			for (m=start; m<=end; m++){         
				list[m] = tempArr[m]; // 임시 배열에서 원래 배열로 데이터 옮기기    
			} 
		}
	}
	
	public static class BufferByte {
		public byte[] buffer;
		public int offset;
		public int len;
		
		public BufferByte() {
			
		}
		
		public BufferByte(byte[] buffer) {
			this.buffer = buffer;
			offset = 0;
			len = buffer.length;
		}
	}
	public static class PoolOfEditText {
		public ArrayList list;
		public PoolOfEditText(int initMaxLength) {
			list = new ArrayList(initMaxLength);
			setCapacity(initMaxLength);
		}
		public void reset() {
			list.reset();
		}
		/**저장공간을 늘리거나 줄인다. 또한 Array.Resize는 배열공간만 늘리고 아이템은 null이므로 아이템
		 * 까지 만들어서 넣어준다
		 * @param c
		 */
		public void setCapacity(int c) {
			list.setCapacity(c);
			int i;
			for (i=list.count; i<list.capacity; i++) {
				Rectangle boundsOfEditText = new Rectangle(0,0,100,50); 
				EditText editText = new EditText(true, false, this, "EditText", boundsOfEditText, 
						30, false, new CodeString("", Color.BLACK), 
						EditText.ScrollMode.Both, Color.WHITE);
				list.add(editText);
			}
		}
		public void add(Object e) {
			list.add(e);
		}
		
		public Object[] getItems() {
			return list.getItems();
		}
		
		public Object getItem(int index) {
			return list.getItem(index);			
		}
	}
	public static class PoolOfButton {
		public ArrayList list;
		public PoolOfButton(int initMaxLength, Size buttonSize) {
			list = new ArrayList(initMaxLength);
			setCapacity(initMaxLength, buttonSize);
		}
		public void reset() {
			list.reset();
		}
		/**저장공간을 늘리거나 줄인다. 또한 Array.Resize는 배열공간만 늘리고 아이템은 null이므로 아이템
		 * 까지 만들어서 넣어준다
		 * @param c
		 */
		public void setCapacity(int c, Size buttonSize) {
			list.setCapacity(c);
			int i;
			for (i=list.count; i<list.capacity; i++) {
				Button button  = new Button(null, "", "", Color.BLUE, 
						new Rectangle(0,0,buttonSize.width,buttonSize.height), 
						false, 255, true, 0, null, Color.CYAN);
				list.add(button);
			}
		}
		public void add(Object e) {
			list.add(e);
		}
		
		public Object[] getItems() {
			return list.getItems();
		}
		
		public Object getItem(int index) {
			return list.getItem(index);			
		}
	}
	
	public static class ObjectPool  {
		public HighArray list;
		public int countOfUsedItems;
		
		public ObjectPool(int initMaxLength) {
			list = new HighArray(initMaxLength);
		}
		public void reset() {
			list.destroy();
		}
		
		/** 풀에 재사용할 아이템을 넣는다.*/
		public void add(Object e) {
			list.add(e);
		}
		
		/** 풀에서 count만큼의 아이템들을 가져온다. 
		 * 만약에 풀에 count개의 아이템이 없으면 null을 리턴한다.*/
		public Object[] getItems(int count) {
			if (list.getCount()-countOfUsedItems<count) return null;
			Object[] r = new Object[count];
			int i;
			int len = countOfUsedItems+count;
			for (i=countOfUsedItems; i<len; i++) {
				r[i] = list.getItem(i);
			}
			countOfUsedItems += count;
			return r;
		}
		
	}
	
	/** View의 OnTouchListener와 onDraw에서 ControlStack을 잠그기(synchronized) 때문에 
	 * 여기서는 잠그지 않는다. 
	 * @author kim ji yun
	 *
	 */
	public static class ControlStack {
		private Control[] list;		
		public int count=0;
		public int resizeInc=20;
		public int capacity=0;
		
		public ControlStack(int initMaxLength) {
			capacity = initMaxLength;
			list = new Control[initMaxLength];
		}
		synchronized public void reset2() {
			count=0;
		}
		synchronized public void setCapacity(int c) {
			capacity = c; 
			list = Array.Resize(list, capacity);
			
		}
		/** 기존 컨트롤 스텍에 있던 컨트롤의 레퍼런스를 모두 삭제하고 새로이 컨트롤을 추가한다.*/
		synchronized public void add(Control e) {
			deleteItem(e.iName);
			
			if (count>=list.length) {
				capacity = list.length+resizeInc;
				list = Array.Resize(list, capacity);
			}			
			list[count] = e;
			count++;
		}
		
		synchronized public Control[] getItems() {
			//if (list.length==count) return list;
			list = Array.Resize(list,count);
			return list;
		}
		
		synchronized public Control getItem(int index) {
			return list[index];
		}
		
		synchronized public Control findItem(int iName) {
			int i;
			for (i=0; i<count; i++) {
				if (list[i].iName==iName) return list[i];
			}
			return null;
		}
		
		synchronized public void deleteLastItem() {
			if (count>0) {
				list[count-1] = null;
				count--;
			}
		}
		
		/** iName을 갖는 컨트롤의 레퍼런스들을 컨트롤 스텍에서 모두 삭제한다.*/
		synchronized public void deleteItem(int iName) {
			int i;
			for (i=count-1; i>=0; i--) {
				if (list[i].iName==iName) {
					try {
						list = Array.Delete(list, i, 1);
						count--;
						if (count<=0) break;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}						
					//break;
				}
			}
		}
		
	}
	
	public static class LinkedList {
		public static class Node {
			public Object data;
			public Node next;
			public Node (Object data, Node next) {
				this.data = data;
				this.next = next;
			}
		}
		
		/** 처음 노드를 가리킨다. 빈리스트이면 null*/
		Node listLinked;
		/** 빈노드*/
		Node head;
		/** 처음에는 빈노드, 리스트의 마지막 노드를 가리킨다.*/
		Node tail;
		int count;
		
		/** 검색 또는 insert 작업을 빠르게하기 위함이다.*/
		//Node cur;
		//int indexOfStartNode;
		
	
		public LinkedList(int initMaxLength) {
			head = new Node(null, null);
			tail = head;
			listLinked = head.next;
		}
		
		public void makeLinkedListFromArray(ArrayList list) {
			int i;
			tail = head;
			if (list.count>0) {
				Node node = new Node(list.getItem(0), null);
				tail.next = node;
				tail = node;
				head.next = node;
			}
			listLinked = head.next;
			
			for (i=1; i<list.count; i++) {
				Node node = new Node(list.getItem(i), null);
				tail.next = node;
				tail = node;
			}
			count = list.count;
		}
		
		public ArrayList makeArrayList() {
			ArrayList r = new ArrayList(count);
			Node n;
			for (n=listLinked; n!=null; n=n.next) {
				r.add(n.data);
			}
			return r;
		}
		
		public Node addToLinkdedList(Object o) {			
			Node newNode = new Node(o, null); 
			tail.next = newNode;
			tail = newNode;
			if (count==0) {
				listLinked = newNode;
				head.next = listLinked; 
			}
			count++;
			return newNode;
		}
		
		public Node insertToLinkdedList(int index, Object o) throws Exception {
			Node newNode = new Node(o, null);
			
			int i;
			//Node nodeCur = listLinked;
			Node nodeCur = head;
			if (listLinked!=null) {  // count>0
				if (index<count) { // 리스트 중간에 넣기
					for (i=0; i<index; i++) {
						nodeCur = nodeCur.next;
					}
					Node nextNode = nodeCur.next;
					nodeCur.next = newNode;
					newNode.next = nextNode;
					if (index==0) {
						listLinked = newNode;
						head.next = newNode;
					}
				}
				else if (index==count) {	// 마지막에 넣기
					tail.next = newNode;
					tail = newNode;
				}
				else {
					throw new Exception("invalid index");
				}
			}
			else {
				if (index==0) {
					head.next = newNode;
					listLinked = newNode;
					tail = newNode;
				}
				else {
					throw new Exception("invalid index");
				}
			}
			
			count++;
			return newNode;
			
		}
		
		/**listLinked를 0의 인덱스로 하여 가장 마지막 노드는 count-1의 인덱스를 갖는다.
		 * base+offset은 인덱스이다. insert되는 노드는 base+offset의 인덱스를 갖는다.
		 * @param startNode : 검색 시작 노드 
		 * @param base : startNode의 인덱스
		 * @param offset : base에서 떨어진 거리
		 * @return : 해당 아이템
		 * @throws Exception
		 */
		public Node insertToLinkdedList(Node startNode, int base, int offset, Object o) throws Exception 
		{
			Node newNode = new Node(o, null);
			
			int i;
			//Node nodeCur = listLinked;
			int index = base+offset;
			Node nodeCur = head;
			if (listLinked!=null) {  // count>0
				if (index<count) { // 리스트 중간에 넣기
					for (i=base; i<index; i++) {
						nodeCur = nodeCur.next;
					}
					Node nextNode = nodeCur.next;
					nodeCur.next = newNode;
					newNode.next = nextNode;
					if (index==0) {
						listLinked = newNode;
						head.next = newNode;
					}
				}
				else if (index==count) {	// 마지막에 넣기
					tail.next = newNode;
					tail = newNode;
				}
				else {
					throw new Exception("invalid index");
				}
			}
			else {
				if (index==0) {
					head.next = newNode;
					listLinked = newNode;
					tail = newNode;
				}
				else {
					throw new Exception("invalid index");
				}
			}
			
			count++;
			return newNode;
			
		}
		
		synchronized public void reset2() {
			head.next = null;
			listLinked = head.next;
			tail = head;
			count=0;
		}

		public Node getItem(int index) throws Exception {
			Node r=null;			
			if (index<count) {
				int i;
				Node nodeCur = listLinked;
				for (i=0; i<=index; i++) {
					r = nodeCur;
					nodeCur=nodeCur.next;
				}
				
				return r;
			}
			else {
				throw new Exception("invalid index");
			}
		}
		
		/** listLinked를 0의 인덱스로 하여 가장 마지막 노드는 count-1의 인덱스를 갖는다.
		 * base+offset은 인덱스이다. 
		 * @param startNode : 검색 시작 노드 
		 * @param base : startNode의 인덱스
		 * @param offset : base에서 떨어진 거리
		 * @return : 해당 아이템
		 * @throws Exception
		 */
		public Node getItem(Node startNode, int base, int offset) throws Exception {
			Node r=null;
			int index = base+offset;
			//this.indexOfStartNode = base; 
			if (index<count) {
				int i;
				Node nodeCur = startNode;
				for (i=base; i<=index; i++) {
					r = nodeCur;
					nodeCur=nodeCur.next;
				}
				return r;
			}
			else {
				throw new Exception("invalid index");
			}
		}
		
	}
	
	public static class ArrayList_FindClassParams {
		public FindClassParams[] list;
		public int count=0;
		public int capacity=0;
		public int resizeInc=100;
		public ArrayList_FindClassParams(int initMaxLength) {
			capacity = initMaxLength;
			list = new FindClassParams[initMaxLength];			
		}
		/** 배열원소의 reset()을 호출하여 모든 메모리자원들을 해제한다.*/
		synchronized public void reset() {			
			int i;
			for (i=0; i<count; i++) {
				if (list[i]!=null) {
					list[i].destroy();
					list[i] = null;
				}
			}
			count=0;
		}
		/** 배열원소는 그대로 놔두고 count만 0으로 만든다.*/
		public void reset2() {
			count=0;
		}
		synchronized public void setCapacity(int c) {
			capacity = c; 
			list = Array.Resize(list, capacity);
			
		}
		synchronized public void add(FindClassParams e) {
			if (count>=list.length) {
				capacity = list.length+resizeInc;
				list = Array.Resize(list, capacity);
			}
			list[count] = e;
			count++;
		}
		
		synchronized public void insert(int index, FindClassParams e) {
			try {
				FindClassParams[] src = {e};
				this.list = Array.InsertNoSpaceError(src, 0, list, index, 1);
				count++;
			}catch(Exception ex) {
			}
		}
		
		synchronized public FindClassParams[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		synchronized public FindClassParams getItem(int index) {
			return list[index];
		}
		
		/** item의 내용이 아니라 참조 주소가 같으면 가진 것으로 한다.*/
		public boolean hasItem(FindClassParams item) {
			int i;
			for (i=0; i<count; i++)  {
				if (list[i]==item) return true;
			}
			return false;
		}
	}
	
	public static class ArrayList_FindVarUseParams {
		public FindVarUseParams[] list;
		public int count=0;
		public int capacity=0;
		public int resizeInc=100;
		public ArrayList_FindVarUseParams(int initMaxLength) {
			capacity = initMaxLength;
			list = new FindVarUseParams[initMaxLength];			
		}
		/** 배열원소의 reset()을 호출하여 모든 메모리자원들을 해제한다.*/
		synchronized public void reset() {			
			int i;
			for (i=0; i<count; i++) {
				if (list[i]!=null) {
					list[i].destroy();
					list[i] = null;
				}
			}
			count=0;
		}
		/** 배열원소는 그대로 놔두고 count만 0으로 만든다.*/
		public void reset2() {
			count=0;
		}
		synchronized public void setCapacity(int c) {
			capacity = c; 
			list = Array.Resize(list, capacity);
			
		}
		synchronized public void add(FindVarUseParams e) {
			if (count>=list.length) {
				capacity = list.length+resizeInc;
				list = Array.Resize(list, capacity);
			}
			list[count] = e;
			count++;
		}
		
		synchronized public void insert(int index, FindVarUseParams e) {
			try {
				FindVarUseParams[] src = {e};
				this.list = Array.InsertNoSpaceError(src, 0, list, index, 1);
				count++;
			}catch(Exception ex) {
			}
		}
		
		synchronized public FindVarUseParams[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		synchronized public FindVarUseParams getItem(int index) {
			return list[index];
		}
		
		/** item의 내용이 아니라 참조 주소가 같으면 가진 것으로 한다.*/
		public boolean hasItem(FindVarUseParams item) {
			int i;
			for (i=0; i<count; i++)  {
				if (list[i]==item) return true;
			}
			return false;
		}
	}
	
	public static class ArrayListIReset {
		public IReset[] list;
		public int count=0;
		public int capacity=0;
		public int resizeInc=100;
		public ArrayListIReset(int initMaxLength) {
			capacity = initMaxLength;
			list = new IReset[initMaxLength];			
		}
		
		public ArrayListIReset clone() {
			ArrayListIReset r = new ArrayListIReset(count);
			int i;
			for (i=0; i<count; i++) {
				r.list[i] = this.list[i];
			}
			r.count = this.count;
			return r;
		}
		
		/** 배열원소의 reset()을 호출하여 모든 메모리자원들을 해제한다.*/
		synchronized public void reset() {				
			int i;
			for (i=0; i<count; i++) {				
				if (list[i]!=null) {
					if (list[i] instanceof com.gsoft.common.Compiler_types.FindFunctionParams) {
						int a;
						a=0;
						a++;
					}
					list[i].destroy();
					list[i] = null;
				}
			}
			count=0;
		}
		/** 배열원소는 그대로 놔두고 count만 0으로 만든다.*/
		public void reset2() {
			count=0;
		}
		synchronized public void setCapacity(int c) {
			capacity = c; 
			list = Array.Resize(list, capacity);
			
		}
		synchronized public void add(IReset e) {
			if (count>=list.length) {
				capacity = list.length+resizeInc;
				list = Array.Resize(list, capacity);
			}
			list[count] = e;
			count++;
		}
		
		synchronized public void insert(int index, IReset e) {
			try {
				IReset[] src = {e};
				this.list = Array.InsertNoSpaceError(src, 0, list, index, 1);
				count++;
			}catch(Exception ex) {
			}
		}
		
		synchronized public IReset[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		synchronized public IReset getItem(int index) {
			return list[index];
		}
		
		/** item의 내용이 아니라 참조 주소가 같으면 가진 것으로 한다.*/
		public boolean hasItem(IReset item) {
			int i;
			for (i=0; i<count; i++)  {
				if (list[i]==item) return true;
			}
			return false;
		}
		
	}
	
	public static class ArrayListByte {
		public byte[] list;
		public int count=0;
		public int capacity=0;
		public int resizeInc=100;
		public ArrayListByte(int initMaxLength) {
			capacity = initMaxLength;
			list = new byte[initMaxLength];			
		}
		public byte getItem(int offset) {
			// TODO Auto-generated method stub
			return list[offset];
		}
		synchronized public void reset() {
			count=0;
		}
		synchronized public void setCapacity(int c) {
			capacity = c; 
			list = Array.Resize(list, capacity);
			
		}
		synchronized public void add(byte e) {
			if (count>=list.length) {
				capacity = list.length+resizeInc;
				list = Array.Resize(list, capacity);
			}
			list[count] = e;
			count++;
		}
	}
	
	public static class ArrayList {
		public Object[] list;
		public int count=0;
		public int capacity=0;
		public int resizeInc=100;
		public ArrayList(int initMaxLength) {
			capacity = initMaxLength;
			list = new Object[initMaxLength];			
		}
		synchronized public void reset() {			
			int i;
			for (i=0; i<count; i++) {
				list[i] = null;
			}
			count=0;
		}
		synchronized public void setCapacity(int c) {
			capacity = c; 
			list = Array.Resize(list, capacity);
			
		}
		synchronized public void add(Object e) {
			if (count>=list.length) {
				capacity = list.length+resizeInc;
				list = Array.Resize(list, capacity);
			}
			list[count] = e;
			count++;
		}
		
		synchronized public void insert(int index, Object e) {
			try {
				Object[] src = {e};
				this.list = Array.InsertNoSpaceError(src, 0, list, index, 1);
				count++;
			}catch(Exception ex) {
			}
		}
		
		synchronized public Object[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		synchronized public Object getItem(int index) {
			return list[index];
		}
		
		/** item의 내용이 아니라 참조 주소가 같으면 가진 것으로 한다.*/
		public boolean hasItem(Object item) {
			int i;
			for (i=0; i<count; i++)  {
				if (list[i]==item) return true;
			}
			return false;
		}
		
		synchronized public void delete(int startIndex, int len) {
			list = Array.Delete(list, startIndex, len);
			count -= len;
		}
		
	}
	
	public static class StackTracer {
		CodeString message=null;
		
		public StackTracer(Exception e) {
			StackTraceElement[] arrSTE = e.getStackTrace();
			int i;
			message = new CodeString(e.toString() + "\n",Color.BLACK);
			for (i=0; i<arrSTE.length; i++) {
				CodeString str = 
						(new CodeString(arrSTE[i].getClassName() + "." + arrSTE[i].getMethodName() + "()" + "<",Color.BLACK))
						.concate(new CodeString(""+arrSTE[i].getLineNumber(),Color.RED))
						.concate(new CodeString(">\n",Color.BLACK));
				message = message.concate(str);
			}
			
		}
		
		public CodeString getMessage() {
			return message;
		}
	}
	
	public static class ArrayListInt {
		public int[] list;
		public int count=0;
		public int capacity=0;
		public int resizeInc=20;
		public ArrayListInt(int initMaxLength) {
			capacity = initMaxLength;
			list = new int[initMaxLength];			
		}
		
		public ArrayListInt clone() {
			ArrayListInt r = new ArrayListInt(count);
			int i;
			for (i=0; i<count; i++) {
				r.list[i] = this.list[i];
			}
			r.count = this.count;
			return r;
		}
		/** 배열원소는 그대로 놔두고 count만 0으로 만든다.*/
		synchronized public void reset2() {
			count=0;
		}
		synchronized public void setCapacity(int c) {
			capacity = c; 
			list = Array.Resize(list, capacity);
			
		}
		synchronized public void add(int e) {
			if (count>=list.length) {
				capacity = list.length+resizeInc;
				list = Array.Resize(list, capacity);
			}
			list[count] = e;
			count++;
		}
		
		synchronized public int[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		synchronized public int getItem(int index) {
			return list[index];
		}
		
	}
	
	/*public static class ArrayListCharacter {
		public Character[] list;
		public int count=0;
		public int capacity=0;
		public ArrayListChar(int initMaxLength) {
			capacity = initMaxLength;
			list = new char[initMaxLength];			
		}
		synchronized public void reset() {
			count=0;
		}
		synchronized public void setCharacters(Character[] chars) {
			if (count<str.length()) {
				list = Array.Resize(list, chars.length);
			}
			//char[] arr = new char[str.length()];
			str.getChars(0, str.length(), list, 0);
			count = chars.length();
			//list = arr;
		}
		synchronized public void insert(int index, String str) {			
			char[] buf = new char[str.length()];
			str.getChars(0, str.length(), buf, 0);
			if (list.length < count+buf.length) {
				list = Array.Resize(list, count+buf.length);
			}
			try {
				Array.Insert(buf, 0, list, index, buf.length);
				count += buf.length;
			}catch(Exception e) {
			}
		}
		synchronized public void delete(int startIndex, int len) {
			list = Array.Delete(list, startIndex, len);
			count -= len;
		}
		synchronized public void setCapacity(int c) {
			capacity = c; 
			list = Array.Resize(list, capacity);
			
		}
		synchronized public void add(char e) {
			if (count>=list.length) {
				capacity = list.length+resizeInc;
				list = Array.Resize(list, capacity);
			}
			list[count] = e;
			count++;
		}
		
		synchronized public char[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		synchronized public char getItem(int index) {
			return list[index];
		}
		
	}*/
	
	public static class ArrayListTextLine {
		private TextLine[] list;
		public int count=0;
		public int resizeInc=100;
		public ArrayListTextLine(int initMaxLength) {
			list = new TextLine[initMaxLength];			
		}
		public ArrayListTextLine(TextLine[] strs) {
			list = new TextLine[strs.length];
			int i;
			for (i=0; i<list.length; i++) {
				list[i] = strs[i];
			}
			count = list.length;
		}
		public void reset2() {
			count=0;
		}
		public void add(TextLine e) {
			if (count>=list.length) list = Array.Resize(list, list.length+resizeInc);
			list[count] = e;
			count++;
		}
		
		public TextLine[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		public TextLine getItem(int index) {
			return list[index];
		}
		
		/*public ArrayListTextLine clone() {
			int i;
			ArrayListTextLine r = new ArrayListTextLine(count);
			
			for (i=0; i<count; i++) {
				char[] buf = new char[list[i].length()];
				list[i].getChars(0, buf.length, buf, 0);
				r.add(new String(buf));
			}
			return r;
		}*/
		
		
		synchronized public void delete(int startIndex, int len) throws Exception {
			list = Array.Delete(list, startIndex, len);
			count -= len;
		}
		
	}
	
	
	public static class ArrayListChar {
		public char[] list;
		public int count=0;
		public int capacity=0;
		public int resizeInc=100;
		public ArrayListChar(int initMaxLength) {
			capacity = initMaxLength;
			list = new char[initMaxLength];			
		}
		/** 모든 자원을 해제한다.*/
		void reset() {
			list = null;
			count = 0;
		}
		synchronized public void reset2() {
			count=0;
		}
		synchronized public void setText(String str) {
			if (count<str.length()) {
				list = Array.Resize(list, str.length());
			}
			//char[] arr = new char[str.length()];
			str.getChars(0, str.length(), list, 0);
			count = str.length();
			//list = arr;
		}
		synchronized public void insert(int index, String str) {			
			char[] buf = new char[str.length()];
			str.getChars(0, str.length(), buf, 0);
			if (list.length < count+buf.length) {
				list = Array.Resize(list, count+resizeInc);
			}
			try {
				Array.Insert(buf, 0, list, index, buf.length);
				count += buf.length;
			}catch(Exception e) {
			}
		}
		synchronized public void delete(int startIndex, int len) {
			list = Array.Delete(list, startIndex, len);
			count -= len;
		}
		synchronized public void setCapacity(int c) {
			capacity = c; 
			list = Array.Resize(list, capacity);
			
		}
		synchronized public void add(char e) {
			if (count>=list.length) {
				capacity = list.length+resizeInc;
				list = Array.Resize(list, capacity);
			}
			list[count] = e;
			count++;
		}
		
		synchronized public void add(String str) {
			if (count+str.length()>=list.length) {
				capacity = list.length+str.length()+resizeInc;
				list = Array.Resize(list, capacity);
			}
			str.getChars(0, str.length(), list, count);
			count += str.length();
		}
		
		synchronized public char[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		synchronized public char getItem(int index) {
			return list[index];
		}
		
	}
	
	
	public static class ArrayListCodeChar implements IReset {
		public CodeChar[] list;
		public int count=0;
		public int capacity=0;
		public int resizeInc=100;
		public ArrayListCodeChar(int initMaxLength) {
			capacity = initMaxLength;
			list = new CodeChar[initMaxLength];			
		}
		public void destroy() {
			// TODO Auto-generated method stub
			int i;
			for (i=0; i<list.length; i++) {
				if (list[i]!=null) {
					list[i].destroy();
					list[i] = null;
				}
			}
			list = null;
			count = 0;
			
		}
		/** 배열원소는 그대로 놔두고 count만 0으로 만든다.*/
		synchronized public void reset2() {
			count=0;
		}
		synchronized public void setText(CodeString str) {
			if (count<str.length()) {
				list = Array.Resize(list, str.length());
			}
			int i;
			for (i=0; i<str.length(); i++) {
				list[i] = str.charAt(i);
			}
			count = str.length();
			//list = arr;
		}
		synchronized public void concate(CodeString str) {
			if (list.length<count+str.count) {
				capacity = count+str.count*5;
				list = Array.Resize(list, capacity);
			}
			Array.Copy(str.listCodeChar, 0, list, count, str.length());
			count += str.length();
		}
		synchronized public void insert(int index, CodeString str) {
			try {
				this.list = Array.InsertNoSpaceError(str.listCodeChar, 0, list, index, str.length());
				count += str.length();
			}catch(Exception e) {
			}
		}
		synchronized public void delete(int startIndex, int len) {
			list = Array.Delete(list, startIndex, len);
			count -= len;
		}
		synchronized public void setCapacity(int c) {
			capacity = c; 
			list = Array.Resize(list, capacity);
			
		}
		synchronized public void add(CodeChar e) {
			if (count>=list.length) {
				capacity = list.length+resizeInc;
				list = Array.Resize(list, list.length+resizeInc);
			}
			/*if (list[count]==null) {
				list[count] = e;
			}
			else {
				list[count].copy(e);
			}*/
			list[count] = e;
			count++;
		}
		
		synchronized public CodeChar[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		synchronized public CodeChar getItem(int index) {
			return list[index];
		}
		
	}
	
	public static class ArrayListString  implements IReset {
		public String[] list;
		public int count=0;
		public int resizeInc=100;
		public ArrayListString(int initMaxLength) {
			list = new String[initMaxLength];			
		}
		public ArrayListString(String[] strs) {
			list = new String[strs.length];
			int i;
			for (i=0; i<list.length; i++) {
				list[i] = strs[i];
			}
			count = list.length;
		}
		public void destroy() {
			int i;
			for (i=0; i<list.length; i++) {
				list[i] = null;
			}
			count=0;
		}
		/** 배열원소는 그대로 놔두고 count만 0으로 만든다.*/
		public void reset2() {
			count=0;
		}
		public void add(String e) {
			if (count>=list.length) list = Array.Resize(list, list.length+resizeInc);
			list[count] = e;
			count++;
		}
		
		public String[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		public String getItem(int index) {
			return list[index];
		}
		
		public ArrayListString clone() {
			int i;
			ArrayListString r = new ArrayListString(count);
			
			for (i=0; i<count; i++) {
				char[] buf = new char[list[i].length()];
				list[i].getChars(0, buf.length, buf, 0);
				r.add(new String(buf));
			}
			return r;
		}
		
		
		synchronized public void delete(int startIndex, int len) throws Exception {
			list = Array.Delete(list, startIndex, len);
			count -= len;
		}
		
	}
	
	public static class ArrayListCodeString {
		private CodeString[] list;
		public int count=0;
		public int resizeInc=100;
		public String name;
		
		public ArrayListCodeString(int initMaxLength) {
			list = new CodeString[initMaxLength];			
		}
		public ArrayListCodeString(CodeString[] strs) {
			list = new CodeString[strs.length];
			int i;
			for (i=0; i<list.length; i++) {
				list[i] = strs[i];
			}
			count = list.length;
		}
		public void reset() {
			int i;
			for (i=0; i<list.length; i++) {
				list[i] = null;
			}
			count=0;
		}
		/** count만 0으로 만들고 배열원소는 그대로 놔둔다.*/
		public void reset2() {
			count=0;
		}
		public void add(CodeString e) {
			if (count>=list.length) list = Array.Resize(list, list.length+resizeInc);
			list[count] = e;
			count++;
		}
		
		public void setCodeString(int index, CodeString str) {
			list[index] = str;
		}
		
		public CodeString[] substring(int start, int len) {
			ArrayListCodeString r = new ArrayListCodeString(len);
			int i;
			for (i=start; i<start+len; i++) {
				r.add(this.getItem(i));
			}
			return r.getItems();
		}
		
		public CodeString[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		public CodeString getItem(int index) {
			return list[index];
		}
		
		/*public ArrayListCodeString clone() {
			int i;
			ArrayListString r = new ArrayListString(count);
			
			for (i=0; i<count; i++) {
				char[] buf = new char[list[i].length()];
				list[i].getChars(0, buf.length, buf, 0);
				r.add(new String(buf));
			}
			return r;
		}*/
		
		
		synchronized public void delete(int startIndex, int len) throws Exception {
			list = Array.Delete(list, startIndex, len);
			count -= len;
		}
		
	}
	
	public static class ArrayListControl {
		private Control[] list;
		public int count=0;
		public int resizeInc=20;
		public ArrayListControl(int initMaxLength) {
			list = new Control[initMaxLength];			
		}
		synchronized public void reset2() {
			count=0;
		}
		synchronized public void add(Control e) {
			if (count>=list.length) list = Array.Resize(list, list.length+resizeInc);
			list[count] = e;
			count++;
		}
		
		synchronized public Control[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		synchronized public Control getItem(int index) {
			return list[index];
		}
		
	}
	
	public static class ArrayListMenu {
		private Menu[] list;
		public int count=0;
		public int resizeInc=20;
		public ArrayListMenu(int initMaxLength) {
			list = new Menu[initMaxLength];			
		}
		public void reset2() {
			count=0;
		}
		public void add(Menu e) {
			if (count>=list.length) list = Array.Resize(list, list.length+resizeInc);
			list[count] = e;
			count++;
		}
		
		public Menu[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		public Menu getItem(int index) {
			return list[index];
		}
		
		public void draw(Canvas canvas) {
			int i;
			for (i=0; i<count; i++) {
				if (list[i].getIsOpen()) {
					list[i].draw(canvas);
				}
			}			
		}
		
		public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
			int i;
			boolean r=false;
			for (i=0; i<count; i++) {
				if (list[i].getIsOpen()) {
					r = list[i].onTouch(event, scaleFactor);
					if (r) return r;
				}
			}
			return r;
		}
	}
	
	public static class ArrayListDialog {
		private Dialog[] list;
		public int count=0;
		public int resizeInc=20;
		public ArrayListDialog(int initMaxLength) {
			list = new Dialog[initMaxLength];			
		}
		public void reset2() {
			count=0;
		}
		public void add(Dialog e) {
			if (count>=list.length) list = Array.Resize(list, list.length+resizeInc);
			list[count] = e;
			count++;
		}
		
		public Dialog[] getItems() {
			if (list.length==count) return list;
			return Array.Resize(list,count);
		}
		
		public Dialog getItem(int index) {
			return list[index];
		}
		
		public void draw(Canvas canvas) {
			int i;
			for (i=0; i<count; i++) {
				if (list[i].getIsOpen()) {
					list[i].draw(canvas);
				}
			}			
		}
		
		public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
			int i;
			boolean r=false;
			for (i=0; i<count; i++) {
				if (list[i].getIsOpen()) {
					r = list[i].onTouch(event, scaleFactor);
					if (r) return r;
				}
			}
			return r;
		}
	}
	
	public static class Array {
		public static void Copy(Object[] src, int srcIndex, Object[] dest, int destIndex, int len) {
			int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}		
		}
		
		public static void Copy(int[] src, int srcIndex, int[] dest, int destIndex, int len) {
			int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}		
		}
		
		public static void Copy(boolean[] src, int srcIndex, boolean[] dest, int destIndex, int len) {
			int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}		
		}
		
		public static void Copy(byte[] src, int srcIndex, byte[] dest, int destIndex, int len) {
			int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}		
		}
		
		public static void Copy(CodeChar[] src, int srcIndex, CodeChar[] dest, int destIndex, int len) {
			int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}		
		}
		
		public static void Copy(EditRichText.Character[] src, int srcIndex, EditRichText.Character[] dest, int destIndex, int len) {
			int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}		
		}
		
		// 늘릴수도 줄일수도 있다.
		public static EditTextLine[] Resize(EditTextLine[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			EditTextLine[] r = new EditTextLine[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		// 늘릴수도 줄일수도 있다.
		public static FindClassParams[] Resize(FindClassParams[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			FindClassParams[] r = new FindClassParams[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		// 늘릴수도 줄일수도 있다.
		public static FindVarUseParams[] Resize(FindVarUseParams[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			FindVarUseParams[] r = new FindVarUseParams[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		// 늘릴수도 줄일수도 있다.
		public static IReset[] Resize(IReset[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			IReset[] r = new IReset[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		// 늘릴수도 줄일수도 있다.
		public static Object[] Resize(Object[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			Object[] r = new Object[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		public static CodeStringType[] Resize(CodeStringType[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			CodeStringType[] r = new CodeStringType[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		// 늘릴수도 줄일수도 있다.
		public static int[] Resize(int[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			int[] r = new int[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		// 늘릴수도 줄일수도 있다.
		public static Control[] Resize(Control[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			Control[] r = new Control[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		// 늘릴수도 줄일수도 있다.
		public static boolean[] Resize(boolean[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			boolean[] r = new boolean[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		// 늘릴수도 줄일수도 있다.
		public static Menu[] Resize(Menu[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			Menu[] r = new Menu[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		// 늘릴수도 줄일수도 있다.
		public static Dialog[] Resize(Dialog[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			Dialog[] r = new Dialog[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		public static TextLine[] Resize(TextLine[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			TextLine[] r = new TextLine[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		public static Character[] Resize(Character[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			Character[] r = new Character[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		public static char[] Resize(char[] src, int count) {
			int len;
			if (src.length > count) len = count;	// 줄이기
			else len = src.length;		// 늘리기
			char[] r = new char[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		public static CodeChar[] Resize(CodeChar[] src, int count) {
			int len;
			if (src.length > count) len = count;	// 줄이기
			else len = src.length;		// 늘리기
			CodeChar[] r = new CodeChar[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}

		public static byte[] Resize(byte[] src, int count) {
			int len;
			if (src.length > count) len = count;	// 줄이기
			else len = src.length;		// 늘리기
			byte[] r = new byte[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			src = null;
			return r;
		}
		
		public static void Copy(char[] src, int srcIndex, char[] dest, int destIndex, int len) {
			int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}		
		}
			
		
		public static char[] SubArray(char[] src, int srcIndex, int len) {
			char[] r = new char[len];
			int i;
			int count = 0;
			for (i=srcIndex; i<srcIndex+len; i++) {
				r[count++] = src[i];			
			}
			return r;
		}
		
		public static Character[] SubArray(Character[] src, int srcIndex, int len) {
			Character[] r = new Character[len];
			int i;
			int count = 0;
			for (i=srcIndex; i<srcIndex+len; i++) {
				r[count++] = src[i];			
			}
			return r;
		}
		
		public static int Find(char[] src, int srcIndex, int c) {
			int i;
			for (i=srcIndex; i<src.length; i++) {
				if (src[i]==c) return i;
			}
			return -1;
		}
		
		
		/** 배열공간크기에 영향을 받는다.*/ 
		public static void Insert(char[] src, int srcIndex, char[] dest, int destIndex, int len) throws Exception {
			int i;
			int nullPos = Array.Find(dest, destIndex, 0);
			if (nullPos<0) throw new Exception("Insert 공간부족");
			if (nullPos-destIndex<0)  throw new Exception("Insert-NegativeArraySize");
			char[] remainder = Array.SubArray(dest, destIndex, nullPos-destIndex);
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}
			Array.Copy(remainder, 0, dest, destIndex, remainder.length);
		}
		
		public static Object[] Delete(Object[] src, int srcIndex, int len) {
			Object[] result = new Object[src.length-len];
			Array.Copy(src, 0, result, 0, srcIndex);
			Array.Copy(src, srcIndex+len, result, srcIndex, src.length-(srcIndex+len));
			return result;
		}
		
		public static CodeChar[] Delete(CodeChar[] src, int srcIndex, int len) {
			CodeChar[] result = new CodeChar[src.length-len];
			Array.Copy(src, 0, result, 0, srcIndex);
			Array.Copy(src, srcIndex+len, result, srcIndex, src.length-(srcIndex+len));
			return result;
		}
		
		public static char[] Delete(char[] src, int srcIndex, int len) {
			char[] result = new char[src.length-len];
			Array.Copy(src, 0, result, 0, srcIndex);
			Array.Copy(src, srcIndex+len, result, srcIndex, src.length-(srcIndex+len));
			return result;
		}
		
		public static EditRichText.Character[] Delete(EditRichText.Character[] src, int srcIndex, int len) {
			EditRichText.Character[] result = new EditRichText.Character[src.length-len];
			Array.Copy(src, 0, result, 0, srcIndex);
			Array.Copy(src, srcIndex+len, result, srcIndex, src.length-(srcIndex+len));
			return result;
		}

		public static int Find(String[] src, int srcIndex, String c) {
			int i;
			for (i=srcIndex; i<src.length; i++) {
				if (src[i].equals(c)) return i;
			}
			return -1;
		}
		
		public static int Find(TextLine[] src, int srcIndex, TextLine c) {
			int i;
			for (i=srcIndex; i<src.length; i++) {
				if (src[i].equals(c)) return i;
			}
			return -1;
		}
		
		public static int Find(EditRichText.Character[] src, int srcIndex, EditRichText.Character c) {
			int i;
			if (c!=null) {
				for (i=srcIndex; i<src.length; i++) {
					if (src[i].equals(c)) return i;
				}
			}
			else {
				for (i=srcIndex; i<src.length; i++) {
					if (src[i]==null) return i;
				}
			}
			return -1;
		}
		
		public static void Copy(Control[] src, int srcIndex, Control[] dest, int destIndex, int len) {
			int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}		
		}
		
		public static void Copy(String[] src, int srcIndex, String[] dest, int destIndex, int len) {
			int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}		
		}
		
		public static void Copy(CodeString[] src, int srcIndex, CodeString[] dest, int destIndex, int len) {
			int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}		
		}
		
		public static void Copy(TextLine[] src, int srcIndex, TextLine[] dest, int destIndex, int len) {
			int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}		
		}
		
		public static String[] SubArray(String[] src, int srcIndex, int len) {
			String[] r = new String[len];
			int i;
			int count = 0;
			for (i=srcIndex; i<srcIndex+len; i++) {
				r[count++] = src[i];			
			}
			return r;
		}
		
		public static TextLine[] SubArray(TextLine[] src, int srcIndex, int len) {
			TextLine[] r = new TextLine[len];
			int i;
			int count = 0;
			for (i=srcIndex; i<srcIndex+len; i++) {
				r[count++] = src[i];			
			}
			return r;
		}

		public static void Insert(String[] src, int srcIndex, String[] dest, int destIndex, int len) 
				throws Exception {
			int i;
			int nullPos = Array.Find(dest, destIndex, "");
			if (nullPos<0) throw new Exception("Insert 공간부족");
			if (nullPos-destIndex<0)  throw new Exception("Insert-NegativeArraySize");
			String[] remainder = Array.SubArray(dest, destIndex, nullPos-destIndex);
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}
			Array.Copy(remainder, 0, dest, destIndex, remainder.length);		
		}
		
		public static EditRichText.Character[] Insert(EditRichText.Character[] src, int srcIndex, EditRichText.Character[] dest, int destIndex, int len) 
				throws Exception {
			/*int i;
			int nullPos = Array.Find(dest, destIndex, null);
			if (nullPos<0) throw new Exception("Insert 공간부족");
			if (nullPos-destIndex<0)  throw new Exception("Insert-NegativeArraySize");
			EditRichText.Character[] remainder = Array.SubArray(dest, destIndex, nullPos-destIndex);
			for (i=srcIndex; i<srcIndex+len; i++) {
				dest[destIndex++] = src[i];
			}
			Array.Copy(remainder, 0, dest, destIndex, remainder.length);*/
			
			if (len<=0) return dest;
			EditRichText.Character[] r = new EditRichText.Character[dest.length+len];
			if (destIndex>0) {
				Array.Copy(dest, 0, r, 0, destIndex);
			}
			Array.Copy(src, srcIndex, r, destIndex, len);
			if (dest.length>destIndex) {
				Array.Copy(dest, destIndex, r, destIndex+len, dest.length-destIndex);
			}
			return r;
		}
		
		public static FindClassParams[] InsertNoSpaceError(FindClassParams[] src, int srcIndex, 
				FindClassParams[] dest, int destIndex, int len) {
			if (len<=0) return dest;
			FindClassParams[] r = new FindClassParams[dest.length+len];
			if (destIndex>0) {
				Array.Copy(dest, 0, r, 0, destIndex);
			}
			Array.Copy(src, srcIndex, r, destIndex, len);
			if (dest.length>destIndex) {
				Array.Copy(dest, destIndex, r, destIndex+len, dest.length-destIndex);
			}
			return r;
		}
		
		public static FindVarUseParams[] InsertNoSpaceError(FindVarUseParams[] src, int srcIndex, 
				FindVarUseParams[] dest, int destIndex, int len) {
			if (len<=0) return dest;
			FindVarUseParams[] r = new FindVarUseParams[dest.length+len];
			if (destIndex>0) {
				Array.Copy(dest, 0, r, 0, destIndex);
			}
			Array.Copy(src, srcIndex, r, destIndex, len);
			if (dest.length>destIndex) {
				Array.Copy(dest, destIndex, r, destIndex+len, dest.length-destIndex);
			}
			return r;
		}
		
		public static IReset[] InsertNoSpaceError(IReset[] src, int srcIndex, 
				IReset[] dest, int destIndex, int len) {
			if (len<=0) return dest;
			IReset[] r = new IReset[dest.length+len];
			if (destIndex>0) {
				Array.Copy(dest, 0, r, 0, destIndex);
			}
			Array.Copy(src, srcIndex, r, destIndex, len);
			if (dest.length>destIndex) {
				Array.Copy(dest, destIndex, r, destIndex+len, dest.length-destIndex);
			}
			return r;
		}
		
		public static Object[] InsertNoSpaceError(Object[] src, int srcIndex, 
				Object[] dest, int destIndex, int len) {
			if (len<=0) return dest;
			Object[] r = new Object[dest.length+len];
			if (destIndex>0) {
				Array.Copy(dest, 0, r, 0, destIndex);
			}
			Array.Copy(src, srcIndex, r, destIndex, len);
			if (dest.length>destIndex) {
				Array.Copy(dest, destIndex, r, destIndex+len, dest.length-destIndex);
			}
			return r;
		}
		
		public static CodeChar[] InsertNoSpaceError(CodeChar[] src, int srcIndex, 
				CodeChar[] dest, int destIndex, int len) {
			if (len<=0) return dest;
			CodeChar[] r = new CodeChar[dest.length+len];
			if (destIndex>0) {
				Array.Copy(dest, 0, r, 0, destIndex);
			}
			Array.Copy(src, srcIndex, r, destIndex, len);
			if (dest.length>destIndex) {
				Array.Copy(dest, destIndex, r, destIndex+len, dest.length-destIndex);
			}
			return r;
		}
		
		public static Character[] InsertNoSpaceError(Character[] src, int srcIndex, 
				Character[] dest, int destIndex, int len) {
			if (len<=0) return dest;
			Character[] r = new Character[dest.length+len];
			if (destIndex>0) {
				Array.Copy(dest, 0, r, 0, destIndex);
			}
			Array.Copy(src, srcIndex, r, destIndex, len);
			if (dest.length>destIndex) {
				Array.Copy(dest, destIndex, r, destIndex+len, dest.length-destIndex);
			}
			return r;
		}
		
		public static String[] InsertNoSpaceError(String[] src, int srcIndex, 
				String[] dest, int destIndex, int len) {
			if (len<=0) return dest;
			String[] r = new String[dest.length+len];
			if (destIndex>0) {
				Array.Copy(dest, 0, r, 0, destIndex);
			}
			Array.Copy(src, srcIndex, r, destIndex, len);
			if (dest.length>destIndex) {
				Array.Copy(dest, destIndex, r, destIndex+len, dest.length-destIndex);
			}
			return r;
		}
		
		public static CodeString[] InsertNoSpaceError(CodeString[] src, int srcIndex, 
				CodeString[] dest, int destIndex, int len) {
			if (len<=0) return dest;
			CodeString[] r = new CodeString[dest.length+len];
			if (destIndex>0) {
				Array.Copy(dest, 0, r, 0, destIndex);
			}
			Array.Copy(src, srcIndex, r, destIndex, len);
			if (dest.length>destIndex) {
				Array.Copy(dest, destIndex, r, destIndex+len, dest.length-destIndex);
			}
			return r;
		}
		
		public static TextLine[] InsertNoSpaceError(TextLine[] src, int srcIndex, 
				TextLine[] dest, int destIndex, int len) {
			if (len<=0) return dest;
			TextLine[] r = new TextLine[dest.length+len];
			if (destIndex>0) {
				Array.Copy(dest, 0, r, 0, destIndex);
			}
			Array.Copy(src, srcIndex, r, destIndex, len);
			if (dest.length>destIndex) {
				Array.Copy(dest, destIndex, r, destIndex+len, dest.length-destIndex);
			}
			return r;
			
			/*int i;
			boolean spaceError = false;
			int extra=0;
			int nullPos = Array.Find(dest, destIndex, new TextLine(0,0));
			if (nullPos<0) spaceError = true;
			else {
				extra = dest.length - nullPos;
				if (extra<len) spaceError = true;
			}
			if (spaceError==false) {
				try {
					TextLine[] remainder = Array.SubArray(dest, destIndex, nullPos-destIndex);
				for (i=srcIndex; i<srcIndex+len; i++) {
					dest[destIndex++] = src[i];
				}
				Array.Copy(remainder, 0, dest, destIndex, remainder.length);
				}catch(Exception e) {
					Log.e("InsertNoSpaceError-spaceError==false", e.toString());
				}
				return dest;
			}
			else {
				try {			
				do {
					extra = 0;
					TextLine[] r = Resize(dest, dest.length+10*len);
					for (i=0; i<r.length; i++) {
						r[i] = new TextLine(0,0);
					}
					Array.Copy(dest, 0, r, 0, dest.length);
					nullPos = Array.Find(r, destIndex, new TextLine(0,0));				
					if (nullPos<0) spaceError = true;
					else {
						extra = r.length - nullPos;
						if (extra<len) spaceError = true;
						else spaceError = false;
					}
					if (!spaceError) {
						TextLine[] remainder = Array.SubArray(r, destIndex, nullPos-destIndex);
						for (i=srcIndex; i<srcIndex+len; i++) {
							r[destIndex++] = src[i];
						}
						Array.Copy(remainder, 0, r, destIndex, remainder.length);
						return r;
					}
				}while(spaceError);
				}catch(Exception e) {
					Log.e("InsertNoSpaceError-spaceError==true", e.toString());
				}
				return null;			
			}*/
		}
		
		public static CodeString[] Resize(CodeString[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			CodeString[] r = new CodeString[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			return r;
			
		}
		
		public static String[] Resize(String[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			String[] r = new String[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			for (i=len; i<count; i++) {
				r[i] = "";
			}
			return r;
			
		}
		
		public static ArrayListCodeChar[] Resize(ArrayListCodeChar[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			ArrayListCodeChar[] r = new ArrayListCodeChar[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			return r;
			
		}
		
		public static ArrayListChar[] Resize(ArrayListChar[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			ArrayListChar[] r = new ArrayListChar[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			for (i=len; i<count; i++) {
				r[i] = new ArrayListChar(0);
			}
			return r;
			
		}
		
		public static ButtonLine[] Resize(ButtonLine[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			ButtonLine[] r = new ButtonLine[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			return r;
		}
		
		public static Point[] Resize(Point[] src, int count) {
			int len;
			if (src.length < count) len = src.length;	// 늘리기
			else len = count;		// 줄이기
			Point[] r = new Point[count];
			int i;
			for (i=0; i<len; i++) {
				r[i] = src[i];
			}
			return r;
		}
		
		public static Control[] Delete(Control[] src, int srcIndex, int len) throws Exception {
			if (src.length-len<0) throw new Exception("Delete-src.length-len<0");
			Control[] result = new Control[src.length-len];
			if (srcIndex<0) throw new Exception("Delete-srcIndex<0");
			Array.Copy(src, 0, result, 0, srcIndex);
			if (src.length-(srcIndex+len)<0) throw new Exception("Delete-src.length-(srcIndex+len)<0");
			Array.Copy(src, srcIndex+len, result, srcIndex, src.length-(srcIndex+len));
			/*int i;
			for (i=srcIndex; i<srcIndex+len; i++) {
				src[i] = null;
			}*/
			return result;
		}
		
		public static boolean[] Delete(boolean[] src, int srcIndex, int len) throws Exception {
			if (src.length-len<0) throw new Exception("Delete-src.length-len<0");
			boolean[] result = new boolean[src.length-len];
			if (srcIndex<0) throw new Exception("Delete-srcIndex<0");
			Array.Copy(src, 0, result, 0, srcIndex);
			if (src.length-(srcIndex+len)<0) throw new Exception("Delete-src.length-(srcIndex+len)<0");
			Array.Copy(src, srcIndex+len, result, srcIndex, src.length-(srcIndex+len));
			return result;
		}
		
		public static String[] Delete(String[] src, int srcIndex, int len) throws Exception {
			if (src.length-len<0) throw new Exception("Delete-src.length-len<0");
			String[] result = new String[src.length-len];
			if (srcIndex<0) throw new Exception("Delete-srcIndex<0");
			Array.Copy(src, 0, result, 0, srcIndex);
			if (src.length-(srcIndex+len)<0) throw new Exception("Delete-src.length-(srcIndex+len)<0");
			Array.Copy(src, srcIndex+len, result, srcIndex, src.length-(srcIndex+len));
			return result;
		}
		
		public static CodeString[] Delete(CodeString[] src, int srcIndex, int len) throws Exception {
			if (src.length-len<0) throw new Exception("Delete-src.length-len<0");
			CodeString[] result = new CodeString[src.length-len];
			if (srcIndex<0) throw new Exception("Delete-srcIndex<0");
			Array.Copy(src, 0, result, 0, srcIndex);
			if (src.length-(srcIndex+len)<0) throw new Exception("Delete-src.length-(srcIndex+len)<0");
			Array.Copy(src, srcIndex+len, result, srcIndex, src.length-(srcIndex+len));
			return result;
		}
		
		public static TextLine[] Delete(TextLine[] src, int srcIndex, int len) throws Exception {
			if (src.length-len<0) throw new Exception("Delete-src.length-len<0");
			TextLine[] result = new TextLine[src.length-len];
			if (srcIndex<0) throw new Exception("Delete-srcIndex<0");
			Array.Copy(src, 0, result, 0, srcIndex);
			if (src.length-(srcIndex+len)<0) throw new Exception("Delete-src.length-(srcIndex+len)<0");
			Array.Copy(src, srcIndex+len, result, srcIndex, src.length-(srcIndex+len));
			return result;
		}

		
	}

}