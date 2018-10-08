package com.gsoft.common.encoding.MS949;

import com.gsoft.common.Util.ArrayList;

/** 8822자 
 * [81-A0][41-5A, 61-7A, 81-FE]
 * [A1-C5][41-5A, 61-7A, 81-A0] -> 위에서와 같이 둘째바이트를 81-FE로 하면 KSX1001과 충돌이 나므로 A0까지이다.
 * C6[41-52] -> 0XC6A1-0XC8FE는 KSX1001과 충돌이 난다. 따라서 0XC652 까지이다.
 * 
 * KSX1001(KSC5601) : [A1-FE][A1-FE]
 * 
 * 8141-815A, 8161-817A, 8181-81FE, 8241-825A, .......
 * 
 * 
 * CP949 인코딩은 EUC-KR의 확장이며, 하위 호환성이 있다.

128보다 작은 바이트에 KS X 1003을 배당한다.
128보다 크거나 같은 두 바이트에 KS X 1001을 배당한다.
각 글자는 행과 열에 128을 더한 코드값을 사용하여 2바이트로 표현된다. 
행과 열 번호가 32부터 시작하기 때문에 실제로 이 문자 집합은 첫째/둘째 바이트가 161부터 254 범위에 있다.
나머지 공간에 KS X 1001에 없는 8822자의 현대 한글을 가나다 순서대로 배당한다. (모두 합하면 12160자)
이 경우 첫째 바이트가 129부터 197까지이며, 
둘째 바이트는 65부터 90까지(로마자 대문자), 97부터 122까지(로마자 소문자), 
129부터 254까지의 범위이다. 
단 첫째 바이트가 161 이상일 경우 KS X 1001과의 충돌을 막기 위해 
둘째 바이트는 161 이상이 될 수 없다.
한글 채움 문자의 부호값은 A4D4이다.
따라서 KS X 1001의 40-27에 배당된 "위"라는 글자는 CP949에서도 C0 A7라는 바이트 열로 표현된다. 
한편 KS X 1001에 없는 글자는 KS X 1001에 없는 현대 한글 중 10번째이고 
따라서 CP949에서 81 4A가 된다.

*참고 : 0x81(129), 0xA0(160), 0xA1(161), 0xC5(197), 0XFE(254)*/
public class MS949 {
	/**key는 ms949 코드를 기반으로 하는 해시테이블*/
	public static Hashtable tableOfMS949ToUnicode_korean;
	
	/**key는 unicode를 기반으로 하는 해시테이블*/
	public static Hashtable tableOfUnicodeToMS949_korean;
	
	private static Item[] mItems;
	
	public enum Language {
		ASCII,
		KOREAN,
		CHINESE,
		JAPANESE,
		LATIN
		
	}
	
	/** ms949와 unicode간의 매핑 클래스*/
	public static class Item {
		public char ms949Code;
		public char unicode;
		public Item(char ms949Code, char unicode) {
			this.ms949Code = ms949Code;
			this.unicode = unicode;
		}
	}
	
	public static class CodeMinMax {
		public char min;
		public char max;
		public CodeMinMax(char min, char max) {
			this.min = min;
			this.max = max;
		}
	}
	
	/** 인덱스가 하나인 해시테이블*/
	public static class Hashtable {
		ArrayList[] references;
		int initLenOfBucket;
		
		/** @param itemCount : 넣어질 item의 총갯수
		 * @param initLenOfBucket : 인덱스가 정해지면 아이템이 넣어지는 초기 버켓의 길이
		 */
		public Hashtable(int itemCount, int initLenOfBucket) {
			// 12160개의 itemCount는 인덱스의 길이는 1216이 된다.
			int lenOfIndices = itemCount / initLenOfBucket;
			references = new ArrayList[lenOfIndices];
			this.initLenOfBucket = initLenOfBucket;
			
		}
		
		public void input(int key, Item data) {
			int index = key % references.length;
			//int index = ch;
			if (references[index]==null) {
				references[index] = new ArrayList(initLenOfBucket);
			}
			references[index].add(data);
			
		}
		
		/** unicode(key)로 ms949를 얻거나 반대로 ms949(key)로 unicode를 얻는다.
		 * @param ms949OrUnicode : ms949를 얻으려면 true(key는 unicode가 된다.), 
		 * unicode를 얻으려면 false(key는 ms949가 된다.)*/
		public Object getData(int key, boolean ms949OrUnicode) {
			int index = key % references.length;
			ArrayList list = references[index];
			if (list==null) return null;
			int i;
			if (ms949OrUnicode) {//unicode로 ms949를 얻는다.
				for (i=0; i<list.count; i++) {
					Item item = (Item) list.getItem(i);
					if (item.unicode==key) {
						return item;
					}
				}
			}
			else {//ms949로 unicode를 얻는다.
				for (i=0; i<list.count; i++) {
					Item item = (Item) list.getItem(i);
					if (item.ms949Code==key) {
						return item;
					}
				}
			}
			return null;
			
		}
		
	}//public static class Hashtable_OneIndexing {
	
	
	/** 두개의 인덱스를 갖는 해시테이블*/
	public static class Hashtable_doubleIndexing {
		/** 첫번째 인덱스, 배열원소는 references2를 가리키게 된다.*/
		ArrayList references;
		ArrayList listOfCodeMinMax;
		/** 두번째 인덱스, 배열원소는 ArrayList를 가리키게 된다.*/
		ArrayList[] references2;
		/** 두번째 인덱스, references2가 가리키는 arrayList의 처음 길이*/
		int initLenOfBucket;
		int itemCount;
		int curItemCount;
		
		char lastItemCode;
		
		/**12160개이므로 references2의 길이는 122개이다.
		references2에는 대략 1220개(itemCountPerReferences2)의 아이템을 넣을 수 있다.*/
		int lenOfReferences2;
		/**12160개이므로 references2의 길이는 122개이다.
		references2에는 대략 1220개(itemCountPerReferences2)의 아이템을 넣을 수 있다.*/
		int itemCountPerReferences2;
		
		/** @param itemCount : 아이템의 총갯수
		 * @param initLenOfBucket : 인덱스가 정해지면 아이템이 넣어지는 초기 버켓의 길이
		 */
		public Hashtable_doubleIndexing(int itemCount, int initLenOfBucket) {
			this.itemCount = itemCount;			
			this.initLenOfBucket = initLenOfBucket;
			references = new ArrayList(10);
			listOfCodeMinMax = new ArrayList(references.count);
			
			lenOfReferences2 = itemCount/100+1;
			itemCountPerReferences2 = lenOfReferences2 * initLenOfBucket;
			
		}
		
		public void input(char key, Item data) {
			if (curItemCount==12159) {
				int a;
				a=0;
				a++;
			}
			
			int modular = curItemCount++ % itemCountPerReferences2;
			if (modular == 0) {
				//12160개이므로 references2의 길이는 122개이다.
				//references2에는 대략 1220개의 아이템을 넣을 수 있다.
				references2 = new ArrayList[lenOfReferences2];
				references.add(references2);
				
				if (listOfCodeMinMax.count>0) {
					CodeMinMax prev = (CodeMinMax) listOfCodeMinMax.list[listOfCodeMinMax.count-1];
					prev.max = lastItemCode;
				}
				
				CodeMinMax minMax = new CodeMinMax(key, (char)0xffff);
				listOfCodeMinMax.add(minMax);
			}
			lastItemCode = key;
			
			int index = key % references2.length;
			//int index = ch;
			if (references2[index]==null) {
				references2[index] = new ArrayList(initLenOfBucket);
			}
			references2[index].add(data);
			
		}
		
		/** unicode(key)로 ms949를 얻거나 반대로 ms949(key)로 unicode를 얻는다.
		 * @param ms949OrUnicode : ms949를 얻으려면 true(key는 unicode가 된다.), 
		 * unicode를 얻으려면 false(key는 ms949가 된다.)*/
		public Object getData(char key, boolean ms949OrUnicode) {
			int i;
			for (i=0; i<listOfCodeMinMax.count; i++) {
				CodeMinMax minMax = (CodeMinMax) listOfCodeMinMax.getItem(i);
				if (minMax.min<=key && key<=minMax.max) break;
			}
			references2 = (ArrayList[]) references.getItem(i);
			
			int index = key % references2.length;
			ArrayList list = references2[index];
			if (list==null) return null;
			
			if (ms949OrUnicode) {//unicode로 ms949를 얻는다.
				for (i=0; i<list.count; i++) {
					Item item = (Item) list.getItem(i);
					if (item.unicode==key) {
						return item;
					}
				}
			}
			else {//ms949로 unicode를 얻는다.
				for (i=0; i<list.count; i++) {
					Item item = (Item) list.getItem(i);
					if (item.ms949Code==key) {
						return item;
					}
				}
			}
			return null;
			
		}
		
	}//public static class Hashtable {
	
	/** 13000개 미만개의 아이템이므로 1300개의 인덱스길이와 버켓길이는 10인 해시테이블을 리턴한다.*/
	public static Hashtable makeHashtable_korean() {
		int itemCount = MS949_Code_Array_1_korean.code_MS949_1.length + 
                MS949_Code_Array_2_korean.code_MS949_2.length;
		Hashtable r = new Hashtable(itemCount, 10);
		return r;
	}
	
	public static Item[] makeAllItems_korean() {
		Item[] r = new Item[MS949_Code_Array_1_korean.code_MS949_1.length + 
		                    MS949_Code_Array_2_korean.code_MS949_2.length];
		char ms949Code;
		char unicode;
		int i;
		int k=0;
		for (i=0; i<MS949_Code_Array_1_korean.code_MS949_1.length; i++) {
			ms949Code = MS949_Code_Array_1_korean.code_MS949_1[i];
			unicode = Unicode_Code_Array_1_korean.code_Unicode_1[i];
			r[k++] = new Item(ms949Code, unicode);
		}
		
		for (i=0; i<MS949_Code_Array_2_korean.code_MS949_2.length; i++) {
			ms949Code = MS949_Code_Array_2_korean.code_MS949_2[i];
			unicode = Unicode_Code_Array_2_korean.code_Unicode_2[i];
			r[k++] = new Item(ms949Code, unicode);
		}
		
		return r;
	}
	
	/** unicode(key)로 해시테이블에 Item를 넣거나 반대로 ms949(key)로 해시테이블에 Item를 넣는다.
	 * @param ms949OrUnicode :  true이면 key는 unicode가 된다., 
	 * false이면 key는 ms949가 된다.*/
	public static void inputToHashtable(Hashtable table, Item[] items, boolean ms949OrUnicode) {
		int i;
		if (ms949OrUnicode==false) {//ms949(key)로 해시테이블에 Item를 넣는다.
			for (i=0; i<items.length; i++) {
				Item item = items[i];
				table.input(item.ms949Code, item);
			}
		}
		else {//unicode(key)로 해시테이블에 Item를 넣는다.
			for (i=0; i<items.length; i++) {
				Item item = items[i];
				table.input(item.unicode, item);
			}
		}
	}
	
	/** ms949코드를 읽기 위해 ms949를 유니코드로 변환하는 해시테이블을 생성한다.*/
	public static void loadForReading(Language lang) {
		char a = 0xa4a1;
		int debug=0;
		debug++;
		if (lang==Language.KOREAN) {
			if (tableOfMS949ToUnicode_korean==null) {
				if (mItems==null) {
					mItems = makeAllItems_korean();
				}
				tableOfMS949ToUnicode_korean = makeHashtable_korean(); 
				inputToHashtable(tableOfMS949ToUnicode_korean, mItems, false);
			}
		}
		else if (lang==Language.CHINESE) {
			
		}
	}
	
	/** ms949코드로 쓰기 위해 유니코드를 ms949로 변환하는 해시테이블을 생성한다.*/
	public static void loadForWriting(Language lang) {
		if (lang==Language.KOREAN) {
			if (tableOfUnicodeToMS949_korean==null) {
				if (mItems==null) {
					mItems = makeAllItems_korean();
				}
				tableOfUnicodeToMS949_korean = makeHashtable_korean(); 
				inputToHashtable(tableOfUnicodeToMS949_korean, mItems, true);
			}
		}
	}
}