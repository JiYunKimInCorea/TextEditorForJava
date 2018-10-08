package com.gsoft.common.gui;

import com.gsoft.common.ColorEx;
import com.gsoft.common.Events.MotionEvent;
import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.Sizing.SizeF;
import com.gsoft.common.gui.Buttons.Button;
import com.gsoft.common.gui.Buttons.ButtonGroup;
import com.gsoft.common.interfaces.OnTouchListener;
import com.gsoft.common.R;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;

public class IntegrationKeyboard extends Control implements OnTouchListener {
	static final String 자판 = Control.res.getString(R.string.keys_keys);
	static final String Lang = Control.res.getString(R.string.keys_lang);
	static final String Exc = Control.res.getString(R.string.keys_exc);
	static final String Shift = "Shift";
	static final String BackSpace = "BkSp";
	static final String Space = "Space";
	public static final String Enter = "Enter";
	static final String Delete = "Delete";
	
	static int ShiftIndex = 2;
	
	public static String[] SpecialKeys = {
		"Left", "Right", "Up", "Down", "Home", "End", "PgUp", "PgDn"
	};
	
	public static class Hangul {
						
				
		static char[] chars_shiftNotPressed = {
			'ㅂ', 'ㅈ', 'ㄷ', 'ㄱ', 'ㅅ',   'ㅠ', 'ㅐ', 'ㅔ', 'ㅚ' , 'ㅢ', 
			'ㅝ', '\'', '.'
		};
		static char[] chars_shiftPressed = {
			'ㅃ', 'ㅉ', 'ㄸ', 'ㄲ', 'ㅆ',   'ㅛ', 'ㅒ', 'ㅖ', 'ㅙ',  'ㅟ', 
			'ㅘ', '\"', ','
		};

		
		static char[] 통합자음 = {	// 30개
			'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄸ',     // 8
            'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ',      // 8
            'ㅁ', 'ㅂ', 'ㅃ', 'ㅄ', 'ㅅ', 'ㅆ',       // 6
            'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'  // 8
		};
		
		static char[] 종성자음 = {	// 27개	통합자음에서 ㄸ, ㅃ, ㅉ을 제외
			'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ',      // 7
            'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ',      // 8
            'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ',       // 5
            'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'  // 7
		};
		
		static char[] 자음 = {	// 19개
			'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ',     // 5
            'ㄹ',       // 1
            'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ',       // 5
            'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'  // 8
		};
		
	/*  [0]	12593	ㄱ       12594	ㄲ       12595 ㄳ
        [1]	12596	ㄴ       12597  ㄵ        12598 ㄶ
        [2]	12599	ㄷ       12600	ㄸ
        [3]	12601	ㄹ       12602  ㄺ     12603 ㄻ    12604 ㄼ     12605 ㄽ     12606 ㄾ     12607 ㄿ     12608 ㅀ 
        [4]	12609	ㅁ       
        [5]	12610	ㅂ       12611	ㅃ       12612 ㅄ
        [6]	12613	ㅅ       12614	ㅆ
        [7]	12615	ㅇ
        [8]	12616	ㅈ       12617	ㅉ
        [9]	12618	ㅊ
        [10]12619	ㅋ
        [11]12620	ㅌ
        [12]12621	ㅍ
        [13]12622	ㅎ  */


		static char[] 겹받침 = {		// 11개
		  'ㄳ','ㄵ','ㄶ',
          'ㄺ', 'ㄻ', 'ㄼ','ㄽ','ㄾ','ㄿ', 'ㅀ',
          'ㅄ'
		};
		
		static char[] 모음 = {        // 21
			'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', // 4
            'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', // 4
            'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ',    // 5
            'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ',    // 5
            'ㅡ', 'ㅢ',   // 2
            'ㅣ'     // 1
		};
		
		static int countOf모음 = 모음.length;
		
		static char[] 모음뒤에나올수있는모음 = {
			'ㅣ', 'ㅐ', 'ㅏ', 'ㅓ'
		};
		
		static char[] ㅣ앞에오는모음 = {
			'ㅗ', 'ㅜ', 'ㅡ'
		};
		
		static char[] ㅐ앞에오는모음 = {
			'ㅗ'
		};
		
		static char[] ㅏ앞에오는모음 = {
			'ㅗ'
		};
		
		static char[] ㅓ앞에오는모음 = {
			'ㅜ'
		};

/*      [0]	12623	ㅏ   12624	ㅐ   12625	ㅑ   12626	ㅒ
        [1]	12627	ㅓ   12628	ㅔ   12629	ㅕ   12630	ㅖ
        [2]	12631	ㅗ   12632	ㅘ   12633	ㅙ   12634	ㅚ   12635	ㅛ
        [3]	12636	ㅜ   12637	ㅝ   12638	ㅞ   12639	ㅟ   12640	ㅠ
        [4]	12641	ㅡ   12642	ㅢ
        [5]	12643	ㅣ	 */
		
		static char[] 자모 = {
			'가', '까', '나', '다', '따', 
            '라', '마', '바', '빠', '사', 
            '싸', '아', '자', '짜', '차', 
            '카', '타', '파', '하'
        };
		
		static int baseOf완성글자 = 44032;	// 44032(가) - 55203(힣)
		static int diffOf초성 = 588;
		static int diffOf중성 = 28;

		// 588의 차이
/* 		[0]	44032	가
		[1]	44620	까
		[2]	45208	나
		[3]	45796	다
		[4]	46384	따
		[5]	46972	라
		[6]	47560	마
		[7]	48148	바
		[8]	48736	빠
		[9]	49324	사
		[10]	49912	싸
		[11]	50500	아
		[12]	51088	자
		[13]	51676	짜
		[14]	52264	차
		[15]	52852	카
		[16]	53440	타
		[17]	54028	파
		[18]	54616	하  */
		

        // 44031 공백
		// 28의 차이 (종성자음이 27개이므로) 
        /* 44032	가       44033	각      44034 갂     44035 갃	 44059 갛
         * 44060	개	// 가와 비교 28의 차이
         * 44088	갸	// 개와 비교 28의 차이
         * 44116	걔
         * 44144	거       44145	걱
         * 
         * 44172	게
         * 44200	겨
         * 44228	계
         * 44256	고       44257	곡
         * 44284	과
         * 
         * 44312	괘
         * 44340	괴
         * 44368	교       44369	굑
         * 44396	구       44397	국
         * 44424	궈
         * 
         * 44452 	궤 
         * 44480	귀
         * 44508	규
         * 44536	그       44537	극
         * 44564	긔       44565	긕
         * 
         * 44592	기       44593	긱
         * 
         * 		c		char

          
           44619	깋
           44620	까	-> 가와 비교 588의 차이
           
           45207	낗
         * 45208	나    -> 까와 비교 588의 차이 
         * 
         * */
		
		/* 44032	가       44033	각      44034 갂     44035 갃	 44059 갛
		 * 
		 */
		
		
		
		
		static boolean is자음(char c) {
			for (int i=0; i<자음.length; i++) {
				if (자음[i]==c) return true;
			}
			return false;
		}
		static boolean is모음(char c) {
			for (int i=0; i<모음.length; i++) {
				if (모음[i]==c) return true;
			}
			return false;
		}
		
		enum FindMode {
			자음, 통합자음, 종성자음, 겹받침,
			모음, 
			모음뒤에나올수있는모음, 
			ㅣ앞에오는모음, ㅐ앞에오는모음, ㅏ앞에오는모음, ㅓ앞에오는모음			
		}
		
		static int findIndex(FindMode findMode, char c) {
			if (findMode==FindMode.자음) {
				for (int i=0; i<자음.length; i++) {
					if (자음[i]==c) return i;
				}
			}
			else if (findMode==FindMode.모음) {
				for (int i=0; i<모음.length; i++) {
					if (모음[i]==c) return i;
				}
			}
			else if (findMode==FindMode.모음뒤에나올수있는모음) {
				for (int i=0; i<모음뒤에나올수있는모음.length; i++) {
					if (모음뒤에나올수있는모음[i]==c) return i;
				}
			}
			else if (findMode==FindMode.ㅣ앞에오는모음) {
				for (int i=0; i<ㅣ앞에오는모음.length; i++) {
					if (ㅣ앞에오는모음[i]==c) return i;
				}
			}
			else if (findMode==FindMode.ㅐ앞에오는모음) {
				for (int i=0; i<ㅐ앞에오는모음.length; i++) {
					if (ㅐ앞에오는모음[i]==c) return i;
				}
			}
			else if (findMode==FindMode.ㅏ앞에오는모음) {
				for (int i=0; i<ㅏ앞에오는모음.length; i++) {
					if (ㅏ앞에오는모음[i]==c) return i;
				}
			}
			else if (findMode==FindMode.ㅓ앞에오는모음) {
				for (int i=0; i<ㅓ앞에오는모음.length; i++) {
					if (ㅓ앞에오는모음[i]==c) return i;
				}
			}
			else if (findMode==FindMode.종성자음) {
				for (int i=0; i<종성자음.length; i++) {
					if (종성자음[i]==c) return i;
				}
			}
			else if (findMode==FindMode.겹받침) {
				for (int i=0; i<겹받침.length; i++) {
					if (겹받침[i]==c) return i;
				}
			}
			return -1;
		}
		
		/*static Timer timer;
		static TimerListener timerListener;
		static void createTimer(TimerListener listener) {
			timerListener = listener;
		}
		static void startTimer() {
			timer = new Timer(timerListener, 1000);
		}*/
		static String[] buffer = new String[6];
		static int lenOfBuffer = 0;
		
		static void resetBuffer() {
			lenOfBuffer = 0;
		}
		
		public enum Mode {	// 한글 조합이 이루어질 때의 모드
			None,
			//BackSpace,	// 한글 조합이 만들어지는 도중 BkSp가 눌릴 때 모드
			초성,			 
			초중성, 초중성_중성, 초중성_초성,
			초중종성, 초중종종성, 초중종성_종성, 초중종성_초성, 초중성_종중성,
			초중종종성_초성, 초중종성_초중성, 
			중성, 중중성, 중성_중성, 중성_초성,
			겹받침
		};
		public static Mode mode = Mode.None;
		
		// 'ㄳ','ㄵ','ㄶ',
        // 'ㄺ', 'ㄻ', 'ㄼ','ㄽ','ㄾ','ㄿ', 'ㅀ',
        // 'ㅄ'
		static int merge자음(char 앞자음, char 뒷자음) {
			int indexResult = -1;
			switch(앞자음) {
			case 'ㄱ': 
				if (뒷자음=='ㅅ') indexResult = findIndex(FindMode.종성자음,'ㄳ');
				break;
			case 'ㄴ': 
				if (뒷자음=='ㅈ') indexResult = findIndex(FindMode.종성자음,'ㄵ');
				else if (뒷자음=='ㅎ') indexResult = findIndex(FindMode.종성자음,'ㄶ');
				break;
			case 'ㄹ': 
				if (뒷자음=='ㄱ') indexResult = findIndex(FindMode.종성자음,'ㄺ');
				else if (뒷자음=='ㅁ') indexResult = findIndex(FindMode.종성자음,'ㄻ');
				else if (뒷자음=='ㅂ') indexResult = findIndex(FindMode.종성자음,'ㄼ');
				else if (뒷자음=='ㅅ') indexResult = findIndex(FindMode.종성자음,'ㄽ');
				else if (뒷자음=='ㅌ') indexResult = findIndex(FindMode.종성자음,'ㄾ');
				else if (뒷자음=='ㅍ') indexResult = findIndex(FindMode.종성자음,'ㄿ');
				else if (뒷자음=='ㅎ') indexResult = findIndex(FindMode.종성자음,'ㅀ');
				break;
			case 'ㅂ':
				if (뒷자음=='ㅅ') indexResult = findIndex(FindMode.종성자음,'ㅄ');
				break;
			}
			return indexResult;
		}
		
		static int merge모음(/*int len,*/ char 앞모음, int index모음뒤에나올수있는모음) {
			int index합쳐진모음 = -1;
			int index앞에오는모음 = -1;
			switch (index모음뒤에나올수있는모음) {
			case 0: 
				index앞에오는모음 = findIndex(FindMode.ㅣ앞에오는모음, 앞모음);								
				if (index앞에오는모음!=-1) { // 'ㅣ'
					switch (index앞에오는모음) {	//'ㅗ', 'ㅜ', 'ㅡ'
					case 0: index합쳐진모음 = findIndex(FindMode.모음, 'ㅚ'); break;
					case 1: index합쳐진모음 = findIndex(FindMode.모음, 'ㅟ');break;
					case 2: index합쳐진모음 = findIndex(FindMode.모음, 'ㅢ');break;
					}
				}
				break;
			case 1: // 'ㅐ'
				index앞에오는모음 = findIndex(FindMode.ㅐ앞에오는모음, 앞모음);								
				if (index앞에오는모음!=-1) { // 'ㅐ'
					switch (index앞에오는모음) {	//'ㅗ'
					case 0: index합쳐진모음 = findIndex(FindMode.모음, 'ㅙ');break;
					}
				}
				break;	
			case 2: // 'ㅏ'
				index앞에오는모음 = findIndex(FindMode.ㅏ앞에오는모음, 앞모음);								
				if (index앞에오는모음!=-1) { // 'ㅏ'
					switch (index앞에오는모음) {	//'ㅗ'
					case 0: index합쳐진모음 = findIndex(FindMode.모음, 'ㅘ');break;
					}
				}
				break;	
			case 3: // 'ㅓ'
				index앞에오는모음 = findIndex(FindMode.ㅓ앞에오는모음, 앞모음);								
				if (index앞에오는모음!=-1) { // 'ㅓ'
					switch (index앞에오는모음) {	//'ㅜ'
					case 0: index합쳐진모음 = findIndex(FindMode.모음, 'ㅝ');break;
					}
				}
				break;	
			}
			return index합쳐진모음;
			
		}
		
		static boolean isNextToCursor;
		
		static String[] bufferFor겹받침 = new String[2];
		
		static boolean isBkSpPressed = false;
		
		static String convert(String str) throws Exception {
			isNextToCursor = false;
			isBkSpPressed = false;
			if (str.equals(Space)|| str.equals(Delete) || str.equals(Enter)) {
				if (mode!=Mode.None) {
					isNextToCursor = true;
					mode = Mode.None;
					lenOfBuffer = 0;							
					return str;
				}
				else {
					mode = Mode.None;
					lenOfBuffer = 0;							
					return str;					
				}				
			}
			else if (str.equals(BackSpace)) {
				if (lenOfBuffer>0 && mode!=Mode.None) {
					lenOfBuffer--;
					//mode = Mode.BackSpace;
					isBkSpPressed = true;
					if (lenOfBuffer==0) {
						mode = Mode.None;
						return "";
					}
					else {	// 이후에서 처리
						str = buffer[lenOfBuffer-1];
					}
					
				}
				else {
					mode = Mode.None;
					lenOfBuffer = 0;							
					return str;					
				}
			}
			else {
				char c = str.toCharArray()[0];			
				if (is자음(c)==false && is모음(c)==false) {
					if (mode!=Mode.None) {
						isNextToCursor = true;
						mode = Mode.None;
						lenOfBuffer = 0;							
						return str;
					}
					else {
						mode = Mode.None;
						lenOfBuffer = 0;							
						return str;					
					}
				}
				buffer[lenOfBuffer++] = str;
			}			
			
			int len = lenOfBuffer;
			if (len==1) {
				String first = buffer[0];
				char fc = first.toCharArray()[0];
				int indexOfFirst = findIndex(FindMode.자음, fc);
				if (indexOfFirst!=-1) {
					mode = Mode.초성;
					return str;
				}
				else {
					indexOfFirst = findIndex(FindMode.모음, fc);
					if (indexOfFirst!=-1) {
						mode = Mode.중성;
						return str;
					}
				}
				isNextToCursor = true;
				mode = Mode.None;
				lenOfBuffer = 0;
				return str;
			}
			else if (len==2) {
				String first = buffer[0];
				String second = buffer[1];
				char fc = first.toCharArray()[0];
				char sc = second.toCharArray()[0];
				int indexOfFirst = findIndex(FindMode.자음, fc);
				int indexOfSecond = findIndex(FindMode.모음, sc);
				int indexResult=-1;
				if (indexOfFirst!=-1) {
					if (indexOfSecond!=-1) {	// 자모						
						indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
								+ indexOfSecond * Hangul.diffOf중성;
						// 개=44032+0*588+1*28=44060
						char r = (char)indexResult;
						char[] arg = {r};
						mode = Mode.초중성;
						return new String(arg);					
					}
					else {	// 자자
						indexOfSecond = findIndex(FindMode.자음, sc);
						if (indexOfSecond!=-1) {
							int index겹받침 = merge자음(fc, sc);
							if (index겹받침!=-1) { // 자모겹받침								
								char r = 종성자음[index겹받침];
								char[] arg = {r};
								mode = Mode.겹받침;
								bufferFor겹받침[0] = buffer[0];
								bufferFor겹받침[1] = buffer[1];
								String rStr = new String(arg); 
								buffer[0] = rStr;
								lenOfBuffer = 1; 
								return rStr;		
							}
							else {
								mode = Mode.초성;
								isNextToCursor = true;
								//String r = buffer[0] + buffer[1];
								buffer[0] = buffer[1];
								lenOfBuffer = 1;
								return str;
							}
							
						}
						
						
					}
				}
				else {	// if (indexOfFirst==-1)
					indexOfFirst = findIndex(FindMode.모음, fc);
					if (indexOfFirst!=-1) {	// 모음
						if (indexOfSecond!=-1) {	// 모모
							int index모음뒤에나올수있는모음 = 
									findIndex(FindMode.모음뒤에나올수있는모음, sc);
							if (index모음뒤에나올수있는모음!=-1) {
								int index합쳐진모음 = merge모음(fc, index모음뒤에나올수있는모음);
								
								if (index합쳐진모음!=-1) {	// 모
									indexResult = index합쳐진모음;
									char[] arg = {모음[indexResult]};
									mode = Mode.중중성;
									buffer[0] = new String(arg);
									lenOfBuffer = 1;
									return new String(arg);
								}
								else {	// 모모
									mode = Mode.중성_중성;
									String str1 = buffer[0];
									buffer[0] = buffer[1];
									lenOfBuffer = 1;
									return str1 + buffer[1];
								}
							}
							else {	// 모모
								mode = Mode.중성_중성;
								String str1 = buffer[0];
								buffer[0] = buffer[1];
								lenOfBuffer = 1;
								return str1 + buffer[1];
							}
							
						}
						else {
							indexOfSecond = findIndex(FindMode.자음, sc);
							if (indexOfSecond!=-1) {	// 모자
								mode = Mode.중성_초성;
								String str1 = buffer[0];
								buffer[0] = buffer[1];
								lenOfBuffer = 1;
								return str1 + buffer[1];
								
							}
						}
					} // 모모
					else {	// first=겹받침
						indexOfFirst = findIndex(FindMode.종성자음, fc);
						if (mode==Mode.겹받침) {		
							indexOfSecond = findIndex(FindMode.모음, sc);
							if (indexOfSecond!=-1) {	// 겹받침모음
								int index겹받침1 = findIndex(FindMode.자음, bufferFor겹받침[0].toCharArray()[0]);
								char[] arg = {자음[index겹받침1]};
								String str겹받침1 = new String(arg);
								
								int index겹받침2 = findIndex(FindMode.자음, bufferFor겹받침[1].toCharArray()[0]);
								indexResult = Hangul.baseOf완성글자 + index겹받침2 * Hangul.diffOf초성
										+ indexOfSecond * Hangul.diffOf중성;
								char r = (char)indexResult;
								char[] arg1 = {r};
								buffer[0] = bufferFor겹받침[1];
								lenOfBuffer = 2;
								mode = Mode.초중성;
								return str겹받침1 + new String(arg1);
							}
							else {	// 겹받침자음
								buffer[0] = buffer[1];
								lenOfBuffer = 1;
								mode = Mode.초성;
								isNextToCursor = true;
								return str;
								
							}
						}
						
					}
				}
				isNextToCursor = true;
				mode = Mode.None;
				lenOfBuffer = 0;
				return str;
			}
			else if (len==3) {
				String first = buffer[0];
				String second = buffer[1];
				String third = buffer[2];
				char fc = first.toCharArray()[0];
				char sc = second.toCharArray()[0];
				char tc = third.toCharArray()[0];
				int indexOfFirst = findIndex(FindMode.자음, fc);	// 자음
				int indexOfSecond = findIndex(FindMode.모음, sc);	// 모음
				int indexOfThird = findIndex(FindMode.자음, tc);
				int indexResult=-1;
				if (indexOfFirst!=-1 && indexOfSecond!=-1) {
					if (indexOfThird==-1) {	// 자모모
						int index모음뒤에나올수있는모음 = 
								findIndex(FindMode.모음뒤에나올수있는모음, tc);
						if (index모음뒤에나올수있는모음!=-1) {
							int index합쳐진모음 = merge모음( sc, index모음뒤에나올수있는모음);
							
							if (index합쳐진모음!=-1) {	// 자모
								indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
										+ index합쳐진모음 * Hangul.diffOf중성;
								char r = (char)indexResult;
								char[] arg = {r};
								mode = Mode.초중성;
								//char[] a = {모음[index합쳐진모음]};
								//buffer[1] = new String(a);
								//lenOfBuffer = 2;
								return new String(arg);
							}
							else {	// 자모모
								indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
										+ indexOfSecond * Hangul.diffOf중성;
								char r = (char)indexResult;
								char[] arg = {r};
								mode = Mode.초중성_중성;								
								buffer[0] = buffer[2];
								lenOfBuffer = 1;
								return (new String(arg))+third;
							}
						} //if (index모음뒤에나올수있는모음==-1)
						else {	// 자모모
							//indexResult = indexOfFirst * countOf모음 + indexOfSecond;
							indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
									+ indexOfSecond * Hangul.diffOf중성;
							char r = (char)indexResult;
							char[] arg = {r};
							mode = Mode.초중성_중성;
							buffer[0] = buffer[2];
							lenOfBuffer = 1;
							return (new String(arg))+third;
						}
					}
					else {	// 자모자		// if (indexOfThird!=-1)
						int index종성자음 = findIndex(FindMode.종성자음, tc);
						//indexResult = indexOfThird * countOf자모 + indexOfFirst * countOf모음 + indexOfSecond;
						if (index종성자음!=-1) {
							indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
									+ indexOfSecond * Hangul.diffOf중성 + (index종성자음+1);
							// 낵=44032+2*588+1*28+1=45237
							char r = (char)indexResult;
							char[] arg = {r};
							mode = Mode.초중종성;
							return new String(arg);
						}
						else {	// 자음인데 종성자음으로 들어갈 수 없으면, 즉 ㄸ, ㅃ, ㅉ
							// 자모_자
							indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
									+ indexOfSecond * Hangul.diffOf중성;
							char r = (char)indexResult;
							char[] arg = {r};
							mode = Mode.초중성_초성;
							buffer[0] = buffer[2];
							lenOfBuffer = 1;
							return (new String(arg))+third;
						}
						
					}
				}
				isNextToCursor = true;
				mode = Mode.None;
				lenOfBuffer = 0;
				return str;
				
			}
			else if (len==4) {	// 종성이 쌍자음, 자모, 자모 두 글자
				String first = buffer[0];
				String second = buffer[1];
				String third = buffer[2];
				String fourth = buffer[3];
				char fc = first.toCharArray()[0];
				char sc = second.toCharArray()[0];
				char tc = third.toCharArray()[0];
				char fourc = fourth.toCharArray()[0];
				int indexOfFirst = findIndex(FindMode.자음, fc);
				int indexOfSecond = findIndex(FindMode.모음, sc);
				int indexOfThird = findIndex(FindMode.자음, tc);
				int indexOfFourth = findIndex(FindMode.자음, fourc);
				
				
				int indexResult=-1;
				
				if (indexOfThird==-1) {	// 세번째가 자음이 아니면
					int index모음뒤에나올수있는모음 = 
							findIndex(FindMode.모음뒤에나올수있는모음, tc);
					
					// 합쳐지는 모음이 아니면 len=3에서 처리
					if (index모음뒤에나올수있는모음!=-1) {
						int index합쳐진모음 = merge모음( sc, index모음뒤에나올수있는모음);
						
						if (index합쳐진모음!=-1) {	// 자모모자
							int index종성자음 = findIndex(FindMode.종성자음, fourc);
							if (index종성자음!=-1) {
								indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
										+ index합쳐진모음 * Hangul.diffOf중성 + (index종성자음+1);
								char r = (char)indexResult;
								char[] arg = {r};
								mode = Mode.초중종성;
								//char[] a = {모음[index합쳐진모음]};
								//buffer[1] = new String(a);
								//lenOfBuffer = 2;
								return new String(arg);
							}
						}
					}
				}
								
				if (indexOfFirst!=-1 && indexOfSecond!=-1 && indexOfThird!=-1) {
					// 세번째 자음은 종성자음이다. 왜냐하면 len=3에서 처리되므로					
					if (indexOfFourth!=-1) {	// 4번째가 자음, 자모자자
						int index종성자음 = findIndex(FindMode.종성자음, fourc);
						if (index종성자음!=-1) {	// 자모자자
							int index겹받침 = merge자음(tc, fourc);
							if (index겹받침!=-1) { // 자모겹받침
								indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
										+ indexOfSecond * Hangul.diffOf중성 + (index겹받침+1);
								char r = (char)indexResult;
								char[] arg = {r};
								mode = Mode.초중종종성;
								//lenOfBuffer = 0; 
								return new String(arg);		
							}
							else {	// 자모자_자
								String r1;
								int index종성자음Third = findIndex(FindMode.종성자음, tc);
								indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
										+ indexOfSecond * Hangul.diffOf중성 + (index종성자음Third+1);
								char r = (char)indexResult;
								char[] arg = {r};
								r1 = new String(arg);
								mode = Mode.초중종성_초성;
								buffer[0] = buffer[3];
								lenOfBuffer = 1;
								return (r1+fourth);
								
							}
							
						}
					}
					else {	// 4번째가 모음, 자모자모
						indexOfFourth = findIndex(FindMode.모음, fourc);
						String r1, r2;
						//indexResult = indexOfFirst * countOf모음 + indexOfSecond;
						indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
								+ indexOfSecond * Hangul.diffOf중성;
						char r = (char)indexResult;
						char[] arg = {r};
						r1 = new String(arg);
						//indexResult = indexOfThird * countOf모음 + indexOfFourth;
						indexResult = Hangul.baseOf완성글자 + indexOfThird * Hangul.diffOf초성
								+ indexOfFourth * Hangul.diffOf중성;
						r = (char)indexResult;
						char[] arg1 = {r};
						r2 = new String(arg1);
						mode = Mode.초중성_종중성;
						buffer[0] = buffer[2];
						buffer[1] = buffer[3];
						lenOfBuffer = 2;
						return (r1+r2);						
						
					}
				}
				isNextToCursor = true;
				mode = Mode.None;
				lenOfBuffer = 0;
				return str;
			}
			
			else if (len==5){	//
				
				String first = buffer[0];
				String second = buffer[1];
				String third = buffer[2];
				String fourth = buffer[3];
				String fifth = buffer[4];
				char fc = first.toCharArray()[0];
				char sc = second.toCharArray()[0];
				char tc = third.toCharArray()[0];
				char fourc = fourth.toCharArray()[0];
				char fivec = fifth.toCharArray()[0];
				int indexOfFirst = findIndex(FindMode.자음, fc);
				int indexOfSecond = findIndex(FindMode.모음, sc);
				int indexOfThird = findIndex(FindMode.자음, tc);
				int indexOfFourth = findIndex(FindMode.자음, fourc);
				int indexOfFifth = findIndex(FindMode.자음, fivec);
				int indexResult;
				
				if (indexOfThird==-1) {	// 세번째가 자음이 아니면
					int index모음뒤에나올수있는모음 = 
							findIndex(FindMode.모음뒤에나올수있는모음, tc);
					
					// 합쳐지는 모음이 아니면 len=3에서 처리
					if (index모음뒤에나올수있는모음!=-1) {
						int index합쳐진모음 = merge모음( sc, index모음뒤에나올수있는모음);
						
						if (index합쳐진모음!=-1) {
							if (indexOfFifth!=-1) {	// 다섯번째가 자음이면, 자모모자자
								int index종성자음 = findIndex(FindMode.종성자음, fourc);
								if (index종성자음!=-1) {
									int index겹받침 = merge자음(fourc, fivec);
									if (index겹받침!=-1) { // 자모모겹받침
										indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
												+ index합쳐진모음 * Hangul.diffOf중성 + (index겹받침+1);
										char r = (char)indexResult;
										char[] arg = {r};
										mode = Mode.초중종종성;
										//char[] a = {모음[index합쳐진모음]};
										//buffer[1] = new String(a);
										//lenOfBuffer = 2;
										return new String(arg);
									}
									else {	// 자모모자_자, 겹받침이 안되면
										String r1;
										indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
												+ index합쳐진모음 * Hangul.diffOf중성 + (index종성자음+1);
										char r = (char)indexResult;
										char[] arg = {r};
										r1 = new String(arg);
										mode = Mode.초중종성_초성;
										buffer[0] = buffer[4];
										lenOfBuffer = 1;
										return (r1+fifth);
										
									}
								}
							}
							else {	// 다섯번째가 모음이면, 자모모_자모
								indexOfFifth = findIndex(FindMode.모음, fivec);
								if (indexOfFifth!=-1) {
									//indexOfFourth = findIndex(FindMode.모음, fourc);
									String r1, r2;									
									indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
											+ index합쳐진모음 * Hangul.diffOf중성;
									char r = (char)indexResult;
									char[] arg = {r};
									r1 = new String(arg);
									//indexResult = indexOfThird * countOf모음 + indexOfFourth;
									indexResult = Hangul.baseOf완성글자 + indexOfFourth * Hangul.diffOf초성
											+ indexOfFifth * Hangul.diffOf중성;
									r = (char)indexResult;
									char[] arg1 = {r};
									r2 = new String(arg1);
									mode = Mode.초중성_종중성;
									buffer[0] = buffer[3];
									buffer[1] = buffer[4];
									lenOfBuffer = 2;
									return (r1+r2);									
								}								
							}
						}
					}
				}
				
				if (mode != Mode.초중종종성) throw new Exception("잘못된 한글모드 len=5");
				
				if (indexOfFifth!=-1) {	// 자모자자_자
					int index겹받침 = merge자음(tc, fourc);
					if (index겹받침!=-1) { // 자모겹받침
						indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
								+ indexOfSecond * Hangul.diffOf중성 + (index겹받침+1);
						char r = (char)indexResult;
						char[] arg = {r};
						mode = Mode.초중종종성_초성;
						buffer[0] = buffer[4];
						lenOfBuffer = 1; 
						return (new String(arg))+buffer[4];		
					}
					
				}
				else { // 자모자_자모
					indexOfFifth = findIndex(FindMode.모음, fivec);
					if (indexOfFifth!=-1) {
						indexOfThird = findIndex(FindMode.종성자음, tc);
						indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
								+ indexOfSecond * Hangul.diffOf중성 + (indexOfThird+1);
						char r = (char)indexResult;
						char[] arg = {r};
						String r1, r2;
						r1 = new String(arg);
						indexResult = Hangul.baseOf완성글자 + indexOfFourth * Hangul.diffOf초성
								+ indexOfFifth * Hangul.diffOf중성;
						r = (char)indexResult;
						char[] arg1 = {r};
						r2 = new String(arg1);
						mode = Mode.초중종성_초중성;
						buffer[0] = buffer[3];
						buffer[1] = buffer[4];
						lenOfBuffer = 2; 
						return r1+r2;
						
					}
				}				
				
				isNextToCursor = true;
				mode = Mode.None;
				lenOfBuffer = 0;
				return str;				
			}
			else if (len==6) {
				String first = buffer[0];
				String second = buffer[1];
				String third = buffer[2];
				String fourth = buffer[3];
				String fifth = buffer[4];
				String sixth = buffer[5];
				char fc = first.toCharArray()[0];
				char sc = second.toCharArray()[0];
				char tc = third.toCharArray()[0];
				char fourc = fourth.toCharArray()[0];
				char fivec = fifth.toCharArray()[0];
				char sixc = sixth.toCharArray()[0];
				int indexOfFirst = findIndex(FindMode.자음, fc);
				int indexOfThird = findIndex(FindMode.모음, tc);
				int indexOfFourth = findIndex(FindMode.자음, fourc);
				int indexOfFifth = findIndex(FindMode.자음, fivec);
				int indexOfSixth = findIndex(FindMode.자음, sixc);
				int indexResult;
				
				if (mode != Mode.초중종종성) throw new Exception("잘못된 한글모드 len=5");
				
				if (indexOfSixth!=-1) {	// 자모모자자_자, 6번째가 자음이면
					if (indexOfThird!=-1) {	// 세번째가 모음이면
						int index모음뒤에나올수있는모음 = 
								findIndex(FindMode.모음뒤에나올수있는모음, tc);
						
						// 합쳐지는 모음이 아니면 len=3에서 처리
						if (index모음뒤에나올수있는모음!=-1) {
							int index합쳐진모음 = merge모음( sc, index모음뒤에나올수있는모음);
							
							if (index합쳐진모음!=-1) {
								int index겹받침 = merge자음(fourc, fivec);
								if (index겹받침!=-1) { // 자모겹받침
									indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
											+ index합쳐진모음 * Hangul.diffOf중성 + (index겹받침+1);
									char r = (char)indexResult;
									char[] arg = {r};
									mode = Mode.초중종종성_초성;
									buffer[0] = buffer[5];
									lenOfBuffer = 1; 
									return (new String(arg))+buffer[5];		
								}
							}
						}
					}
					
				}
				else { // 자모모자_자모, 6번째가 모음이면
					indexOfSixth = findIndex(FindMode.모음, sixc);
					if (indexOfSixth!=-1) {
						if (indexOfThird!=-1) {	// 세번째가 모음이면
							int index모음뒤에나올수있는모음 = 
									findIndex(FindMode.모음뒤에나올수있는모음, tc);
							
							// 합쳐지는 모음이 아니면 len=3에서 처리
							if (index모음뒤에나올수있는모음!=-1) {
								int index합쳐진모음 = merge모음( sc, index모음뒤에나올수있는모음);
								
								if (index합쳐진모음!=-1) {
									indexOfFourth = findIndex(FindMode.종성자음, fourc);
									indexResult = Hangul.baseOf완성글자 + indexOfFirst * Hangul.diffOf초성
											+ index합쳐진모음 * Hangul.diffOf중성 + (indexOfFourth+1);
									char r = (char)indexResult;
									char[] arg = {r};
									String r1, r2;
									r1 = new String(arg);
									indexResult = Hangul.baseOf완성글자 + indexOfFifth * Hangul.diffOf초성
											+ indexOfSixth * Hangul.diffOf중성;
									r = (char)indexResult;
									char[] arg1 = {r};
									r2 = new String(arg1);
									mode = Mode.초중종성_초중성;
									buffer[0] = buffer[4];
									buffer[1] = buffer[5];
									lenOfBuffer = 2; 
									return r1+r2;
								}
							}
						}
						
					}
				}
				isNextToCursor = true;
				mode = Mode.None;
				lenOfBuffer = 0;
				return str;
				
			}
			else {
				isNextToCursor = true;
				mode = Mode.None;
				lenOfBuffer = 0;
				return str;
				
			}		
		}
		
		// shift키가 눌렸는지 아닌지에 따라 문자를 변환한다.
		static char convert(boolean isShiftPressed, char charA) {
			int i;
			for (i=0; i<chars_shiftPressed.length; i++) {
				if (charA==chars_shiftPressed[i]) {
					if (isShiftPressed) {
						return charA;
					}
					else {
						return chars_shiftNotPressed[i];
					}
				}
			}
			
			return charA;
		}
		
	}
	
	public static class English {
		// shift키가 눌렸는지 아닌지에 따라 문자를 변환한다.
		static char convert(boolean isShiftPressed, char charA) {
			if (isShiftPressed) {
				// 	'a';	// 97
				//	'z';	// 122
				//	'A';	// 65
				//	'Z';	// 90
				// 	len = ('z'-'a' + 1);	// 26
				int r=0;
				
				if ('a'<=charA && charA<='z') {
					r = (charA - 32);
					return (char)r;
				}
			}
			else {
				return charA;
			}
			return 0;
		}
		
	}
	
	public static int startHiragana = 12353;
	public static int endHiragana = 12447;
	public static int startKatakana = 12448;
	public static int endKatakana = 12543;
	public static int startLatin2 = 0x00a1;
	public static int endLatin2 = 0x00ff;
	
	public LangDialog langDialog;
	
	public static char[] hiragana;/* = {
		12353, 12354, 12355, 12356, 12357, 12358, 12359, 12360, 12361, 12362, 12363, 12364, 12365, 12366, 12367, 12368, 
		12369, 12370, 12371, 12372, 12373, 12374, 12375, 12376, 12377, 12378, 12379, 12380, 12381, 12382, 12383, 12384, 
		12385, 12386, 12387, 12388, 12389, 12390, 12391, 12392, 12393, 12394, 12395, 12396, 12397, 12398, 12399, 12400, 
		12401, 12402, 12403, 12404, 12405, 12406, 12407, 12408, 12409, 12410, 12411, 12412, 12413, 12414, 12415, 12416, 
		12417, 12418, 12419, 12420, 12421, 12422, 12423, 12424, 12425, 12426, 12427, 12428, 12429, 12430, 12431, 12432, 
		12433, 12434, 12435, 12436, 12437, 12438, 12439, 12440, 12441, 12442, 12443, 12444, 12445, 12446, 12447};*/
	
	public static char[] katakana;/* = {
		12448, 12449, 12450, 12451, 12452, 12453, 12454, 12455, 12456, 12457, 12458, 12459, 12460, 12461, 12462, 12463, 
		12464, 12465, 12466, 12467, 12468, 12469, 12470, 12471, 12472, 12473, 12474, 12475, 12476, 12477, 12478, 12479, 
		12480, 12481, 12482, 12483, 12484, 12485, 12486, 12487, 12488, 12489, 12490, 12491, 12492, 12493, 12494, 12495, 
		12496, 12497, 12498, 12499, 12500, 12501, 12502, 12503, 12504, 12505, 12506, 12507, 12508, 12509, 12510, 12511, 
		12512, 12513, 12514, 12515, 12516, 12517, 12518, 12519, 12520, 12521, 12522, 12523, 12524, 12525, 12526, 12527, 
		12528, 12529, 12530, 12531, 12532, 12533, 12534, 12535, 12536, 12537, 12538, 12539, 12540, 12541, 12542, 12543
		};*/
	
	public static char[] 부수 = {																								//팔팔											
		0x4e00, 0x4e28, 0x4e36, 0x4e3f, 0x4e59, 0x4e85, 0x4e8c, 0x4ea0, 0x4eba, 0x513f, 0x5165, 0x516b, 0x5182, 0x5196, 0x51ab, 0x51e0, '\n',
																												//입구			//흙토						
		0x51f5, 0x5200, 0x529b, 0x52f9, 0x5315, 0x531a, 0x5338,	0x5341, 0x535c,	0x5369,	0x5382,	0x53b6, 0x53c8, 0x53e3, 0x56d7, 0x571f, '\n',
																								 //호			//뫼산																							
		0x58eb, 0x5902, 0x590a, 0x5915, 0x5927, 0x5973, 0x5b50, 0x5b80, 0x5bf8, 0x5c0f, 0x5c22, 0x5c38, 0x5c6e, 0x5c71, 0x5ddb, 0x5de5, '\n',
		0x5df1, 0x5dfe, 0x5e72, 0x5e7a, 0x5e7f, 0x5ef4, 0x5efe, 0x5f0b, 0x5f13, 0x5f50, 0x5f61, 0x5f73, 0x5fc3, 0x6208, 0x6236, 0x624b, '\n',
		0x652f, 0x6534, 0x6587, 0x6597, 0x65a4, 0x65b9, 0x65e0, 0x65e5, 0x66f0, 0x6708, 0x6728, 0x6b20, 0x6b62, 0x6b79, 0x6bb3, 0x6bcb, '\n',
		0x6bd4, 0x6bdb, 0x6c0f, 0x6c14, 0x6c34, 0x706b, 0x722a, 0x7236, 0x723b, 0x723f, 0x7247, 0x7259, 0x725b, 0x72ac, 0x7384, 0x7389, '\n',
		0x74dc, 0x74e6, 0x7518, 0x751f, 0x7528, 0x7530, 0x758b, 0x7592, 0x7676, 0x767d, 0x76ae, 0x76bf, 0x76ee, 0x77db, 0x77e2, 0x77f3, '\n',
																								//양양    //깃우    //늙을로    //이    
		0x793a, 0x79b8, 0x79be, 0x7a74, 0x7acb, 0x7af9, 0x7c73, 0x7cf8, 0x7f36, 0x7f51, 0x7f8a, 0x7fbd, 0x8001, 0x800c, 0x8012, 0x8033, '\n',
		0x807f, 0x8089, 0x81e3, 0x81ea, 0x81f3, 0x81fc, 0x820c, 0x821b, 0x821f, 0x826e, 0x8272, 0x8278, 0x864d, 0x866b, 0x8840, 0x884c, '\n',
										//말씀언					//좇을축					//붉을적			//발족	//몸신
		0x8863, 0x897e, 0x898b, 0x89d2, 0x8a00, 0x8c37, 0x8c46, 0x8c55, 0x8c78, 0x8c9d, 0x8d64, 0x8d70, 0x8db3, 0x8eab,	0x8eca, 0x8f9b, '\n',
		//별진			//고을읍	//술주			//마을리	//쇠금	//길장	//문문							//비우			//아닐비																
		0x8fb0, 0x8fb6, 0x9091, 0x9149, 0x91c6, 0x91cc, 0x91d1, 0x9577, 0x9580, 0x961c, 0x96b6, 0x96b9, 0x96e8, 0x9751, 0x975e, 0x9762, '\n',
		0x9769, 0x97cb, 0x97ed, 0x97f3, 0x9801, 0x98a8, 0x98db, 0x98df, 0x9996, 0x9999, 0x99ac, 0x9aa8, 0x9ad8, 0x9adf, 0x9b25, 0x9b2f, '\n',
		0x9b32, 0x9b3c, 0x9b5a, 0x9ce5, 0x9e75, 0x9e7f, 0x9ea5, 0x9ebb, 0x9ec3, 0x9ecd, 0x9ed1, 0x9ef9, 0x9efd, 0x9f0e, 0x9f13, 0x9f20, '\n',
		0x9f3b, 0x9f4a, 0x9f52, 0x9f8d, 0x9f9c, 0x9fa0
			
	};
	
	public static int endOf부수 = 0x9fa5;
	
	//public View owner;
	
	public Button[] buttons;
	public Button[] mathButtons;
	
	public String key;
	
	boolean isNextToCursorMember;

	public enum Mode {
		Math,
		Hangul,
		Eng,
		Remainder,
		ChineseAndJapanese
	};
	
	//public RectangleF bounds;
	
	public String[] buttonTextArray;
	public int buttonCountHorz;
	public int buttonCountVert;
	
	// 윗 한줄
	public int ConstantButtonsCountHorz = 7;
	public int ConstantButtonsCountVert = 1;
	
	// 통합 버튼들
	public int ButtonsCountHorz = 10;
	public int ButtonsCountVert = 4;
	
	// 수학 버튼들
	public int MathButtonsCountHorz = 7;
	public int MathButtonsCountVert = 5;
	
	public Mode mode;
	public boolean shiftPressed;
	
	boolean is자판Selected;
	
	float widthOfGap;
	float heightOfGap;
	
	Paint paint = new Paint();
	
	//public OnTouchListener listener;
	
	// 모든키
	public Byte[] indicesOfButtonsInMathGroup = {
		0,  1,  2,  3,  4,  5,  6,  
		7,  8,  9,  10, 11, 12, 13, 
		14, 15, 16, 17, 18, 19,	20, 
		21, 22, 23, 24, 25, 26,	27, 
		28, 29, 30, 31, 32, 33, 34
	};
	
	// shift를 제외한 모든키
	public Byte[] indicesOfButtonsInIntegrationGroup = {
			0,  1,  3,  4,  5,  6, 
			7,  8,  9,  10, 11, 12, 13, 14, 15, 16, 
			17, 18, 19, 20, 21, 22, 23, 24, 25,	26, 
			27, 28, 29, 30, 31, 32, 33, 34, 35, 36
		};
	
	ButtonGroup mathGroup;
	ButtonGroup integrationGroup;
			
	public String[] MathButtonTextArray = {
			Control.res.getString(R.string.keys_keys), Control.res.getString(R.string.keys_lang), Control.res.getString(R.string.keys_exc), "Space", "BkSp", "Delete", "Enter",
			"1", "2", "3", "S", "R", "Sin", "2'c",
			"4", "5", "6", "(", ")", "Cos", "1'c",
			"7", "8", "9", "*", "+", "Tan", "",
			".", "0", "=", "/", "-", "Log", ""
	};
	
	public String[] ButtonTextArray_SmallEng = {
			Control.res.getString(R.string.keys_keys), Control.res.getString(R.string.keys_lang), "Shift", "Space", "BkSp", "Delete", "Enter",
			"q", "w", "e", "r", "t", 	"y", "u", "i", "o", "p",
			"a", "s", "d", "f", "g", 	"h", "j", "k", "l", "\"",
			"z", "x", "c", "v", "b", 	"n", "m", ",", ".", "'",
	};
	
	public String[] ButtonTextArray_Hangul = {
			Control.res.getString(R.string.keys_keys), Control.res.getString(R.string.keys_lang), "Shift", "Space", "BkSp", "Delete", "Enter",
			"ㅃ\n/\nㅂ", "ㅉ\n/\nㅈ", "ㄸ\n/\nㄷ", "ㄲ\n/\nㄱ", "ㅆ\n/\nㅅ",     "ㅕ", "ㅑ", "ㅛ\n/\nㅠ", "ㅒ\n/\nㅐ", "ㅖ\n/\nㅔ",
			"ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ",   				"ㅗ", "ㅓ", "ㅏ", "ㅣ", "\"\n/\n'",
			"ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅜ", 				"ㅡ", "ㅙ\n/\nㅚ", "ㅟ\n/\nㅢ", "ㅘ\n/\nㅝ", ",\n/\n."
	};
	
	public String[] ButtonTextArray_Remainder = {
			Control.res.getString(R.string.keys_keys), Control.res.getString(R.string.keys_lang), "Shift", "Space", "BkSp", "Delete", "Enter",			
			"~", "!", "@", "#", "$", 	"%", "^", "Home", "End", "PgUp",
			"?", "-", "(", ")", "{", 	"}", "[", "]", "Up", "PgDn", 
			"&", "'", ";", ":", "`", 	"<", ">", "Left", "Down", "Right",
	};
	
	
	public void setBackColor(int color) {
		backColor = color;
		textColor = ColorEx.reverseColor(backColor);
		int i;
		for (i=0; i<mathButtons.length; i++) {
			mathButtons[i].setBackColor(backColor);
		}
		for (i=0; i<buttons.length; i++) {
			buttons[i].setBackColor(backColor);
		}
	}
	 
	public IntegrationKeyboard(View owner, RectangleF bounds, Mode mode, 
			float widthOfGap, float heightOfGap, 
			int colorOfButton, int alpha) {
		super();
		this.bounds = bounds;
		this.mode = mode;
		this.owner = owner;
		this.name = "IntegrationKeyboard";
		this.widthOfGap = widthOfGap;
		this.heightOfGap = heightOfGap;
		
		changeMode(Mode.Math);
		createKeyboard(Mode.Math, widthOfGap, heightOfGap, colorOfButton, alpha, listener);
		changeMode(Mode.Eng);
		createKeyboard(Mode.Eng, widthOfGap, heightOfGap, colorOfButton, alpha, listener);
		
		Control.keyboard = this;
		
		changeModeAndButtonText(mode);
		
		langDialog = new LangDialog(owner, bounds);
		
		paint.setStyle(Style.FILL);
		backColor = Color.WHITE;
		paint.setColor(backColor);
	}
	
	public void changeBounds(RectangleF bounds) {
		
		this.bounds = bounds;
		int i, j, k;
		float x, y, w, h;
		int count = 0;
		RectangleF boundsOfButton=null;
		Button[] buttons=null;
		
		for (k=0; k<2; k++) {
			if (k==0) {
				changeMode(Mode.Math);
				buttons = this.mathButtons;
			}
			else {
				changeMode(Mode.Eng);
				buttons = this.buttons;
			}
			count = 0;
			
			// 자판, lang, shift, space, BkSp, delete, enter 키
			x = widthOfGap;
			y = bounds.y + heightOfGap;
			w = (bounds.width - (ConstantButtonsCountHorz+1) * widthOfGap) / ConstantButtonsCountHorz; 
			h = (bounds.height - (buttonCountVert+1) * heightOfGap) / buttonCountVert;
			for (i=0; i<ConstantButtonsCountHorz; i++) {				
				boundsOfButton = new RectangleF(x,y,w,h);
				buttons[count].changeBounds(boundsOfButton);				
				count++;				
				x += w + widthOfGap;
			}			
			y += h;
			
			// 위 7키를 제외한 통합버튼들
			x = widthOfGap;
			y = y + heightOfGap;
			w = (bounds.width - (buttonCountHorz+1) * widthOfGap) / buttonCountHorz; 
			h = (bounds.height - (buttonCountVert+1) * heightOfGap) / buttonCountVert;
					
			for (j=1; j<buttonCountVert; j++) {			
				for (i=0; i<buttonCountHorz; i++) {				
					boundsOfButton = new RectangleF(x,y,w,h);
					buttons[count].changeBounds(boundsOfButton);
					count++;					
					x += w + widthOfGap;
				}
				x = widthOfGap;
				y += h + heightOfGap;
			}			
		} // for k
		changeModeAndButtonText(mode);
		
		langDialog.changeBounds(bounds);
	}
	
	void changeMode(Mode mode) {
		if (mode==Mode.Math) {
			buttonTextArray = this.MathButtonTextArray;
			buttonCountHorz = this.MathButtonsCountHorz;
			buttonCountVert = this.MathButtonsCountVert;
		}
		else {
			if (mode==Mode.Eng) buttonTextArray = this.ButtonTextArray_SmallEng;
			//else if (mode==Mode.SmallEng) buttonTextArray = this.ButtonTextArray_SmallEng;
			else if (mode==Mode.Hangul) buttonTextArray = this.ButtonTextArray_Hangul;
			else buttonTextArray = this.ButtonTextArray_Remainder;
			buttonCountHorz = this.ButtonsCountHorz;
			buttonCountVert = this.ButtonsCountVert;
			
		}
		
	}
	
	void changeModeAndButtonText(Mode mode) {
		changeMode(mode);
		int i;
		if (mode!=Mode.Math) {
			for (i=0; i<buttons.length; i++) {
				buttons[i].setText(buttonTextArray[i]);
			}
		}
	}
	
	void createKeyboard(Mode mode, float widthOfGap, float heightOfGap, 
			int colorOfButton, int alpha, OnTouchListener listener) {
		
		int i, j;
		float x, y, w, h;
		int count = 0;
		RectangleF boundsOfButton=null;
				
		// 자판, shift, space, BkSp, delete, enter 키
		buttons = new Button[ConstantButtonsCountHorz + (buttonCountVert-1)*buttonCountHorz];
		x = widthOfGap;
		y = bounds.y + heightOfGap;
		w = (bounds.width - (ConstantButtonsCountHorz+1) * widthOfGap) / ConstantButtonsCountHorz; 
		h = (bounds.height - (buttonCountVert+1) * heightOfGap) / buttonCountVert;
		for (i=0; i<ConstantButtonsCountHorz; i++) {				
			boundsOfButton = new RectangleF(x,y,w,h);
			buttons[count] = new Button(this, buttonTextArray[count], buttonTextArray[count], 
					colorOfButton, boundsOfButton, false, alpha, true, 0);
			// 이벤트를 이 클래스에서 직접 처리
			buttons[count].setOnTouchListener(this);
			count++;
			
			x += w + widthOfGap;
		}			
		y += h;
		
		// 수학버튼들과 위 5키를 제외한 통합버튼들
		x = widthOfGap;
		y = y + heightOfGap;
		w = (bounds.width - (buttonCountHorz+1) * widthOfGap) / buttonCountHorz; 
		h = (bounds.height - (buttonCountVert+1) * heightOfGap) / buttonCountVert;
			
				
		for (j=1; j<buttonCountVert; j++) {			
			for (i=0; i<buttonCountHorz; i++) {				
				boundsOfButton = new RectangleF(x,y,w,h);
				buttons[count] = new Button(this, buttonTextArray[count], buttonTextArray[count], 
						colorOfButton, boundsOfButton, false, alpha, true, 0);
				// 이벤트를 이 클래스에서 직접 처리
				buttons[count].setOnTouchListener(this);
				count++;
				
				x += w + widthOfGap;
			}
			x = widthOfGap;
			y += h + heightOfGap;
		}
		
		if (mode==Mode.Math) {
			mathButtons = buttons;			
		}
		
		// 버튼 그룹을 설정한다.
		
		if (mode==Mode.Math) {
			mathGroup = new ButtonGroup(indicesOfButtonsInMathGroup, mathButtons);
		}
		else {
			integrationGroup = new ButtonGroup(indicesOfButtonsInIntegrationGroup, buttons);
		}
		
		if (mode==Mode.Math) {
			
			for (i=0; i<buttons.length; i++) {
				//buttons[i].indicesOfButtonsInGroup = indicesOfButtonsInMathGroup;
				//buttons[i].buttons = mathButtons;
				buttons[i].setGroup(mathGroup, i);
				buttons[i].selectable = true;
			}
		}
		else {
			for (i=0; i<buttons.length; i++) {
				//buttons[i].indicesOfButtonsInGroup = indicesOfButtonsInIntegrationGroup;
				//buttons[i].buttons = buttons;
				buttons[i].setGroup(integrationGroup, i);
				buttons[i].selectable = true;
			}
			
			buttons[ShiftIndex].selectable = true;	// shift키는 토글로 동작한다.
			buttons[ShiftIndex].toggleable = true;
			buttons[ShiftIndex].ColorSelected = Color.YELLOW;			
		}
	}
	
	public synchronized void setHides(boolean hides) {
		super.setHides(hides);
		if (hides) {
			OnTouchListener listener = this.listener;
			//if (Control.isMaximized==true) return;
			if (listener instanceof EditRichText) {
				EditRichText editRichText = (EditRichText)listener;
				if (editRichText.getHides()==false) {
					/*float x = editRichText.bounds.x;
					float y = editRichText.bounds.y;
					float w = editRichText.bounds.width;
					float h = Control.view.getHeight() - y;
					RectangleF bounds = new RectangleF(x,y,w,h);								
					editRichText.changeBounds(bounds);
					Control.isMaximized = true;*/
					if (editRichText.isMaximized()==false)
						editRichText.setMaximized(true);
				}
			}
			else if (listener instanceof EditText) {
				EditText editText = (EditText)listener;
				if (editText.owner instanceof FileDialog) {
					fileDialog.isFullScreen = true;
					//fileDialog.canSelectFileType = true;
					fileDialog.isForViewing = true;
					fileDialog.setScaleValues();
					fileDialog.changeBounds(new RectangleF(0,0,view.getWidth(),view.getHeight()));
					//fileDialog.createAndSetFileListButtons(fileDialog.curDir, FileDialog.Category.All);
					//integrationKeyboard.setHides(true);
					fileDialog.open(this, "FileExplorer");
				}
				else {
					if (editText.getHides()==false) {
						if (editText.isSingleLine==false) {
							/*float x = editText.bounds.x;
							float y = editText.bounds.y;
							float w = editText.bounds.width;
							float h = Control.view.getHeight() - y;
							RectangleF bounds = new RectangleF(x,y,w,h);								
							editText.changeBounds(bounds);
							Control.isMaximized = true;*/
							if (editText.isMaximized()==false)
								editText.setMaximized(true);
						}
					}
				}
			}
		}
	}
	
	public boolean onTouch(MotionEvent event, SizeF scaleFactor) {
		if (hides==true) return false;
		if (langDialog.getIsOpen()) return langDialog.onTouch(event, scaleFactor);
		
    	if (super.onTouch(event, scaleFactor)==false) return false;
    	
    	if (event.actionCode==MotionEvent.ActionDown) {
    		int i;
    		boolean r=false;
    		if (mode==Mode.Math) {
	    		for (i=0; i<mathButtons.length; i++) {
					r = mathButtons[i].onTouch(event, null);
					if (r) return true;
				}
    		}
    		else {
	    		for (i=0; i<buttons.length; i++) {
					r = buttons[i].onTouch(event, null);
					if (r) return true;
				}
    		}
    	}
    	
    	return false;
    }
	
	public void draw(Canvas canvas) {
		synchronized(this) {
		try{
		if (hides) return;
		
		
		canvas.drawRect(bounds.toRectF(), paint);
		if (langDialog.getIsOpen()) {
			langDialog.draw(canvas);
			return;
		}		
		
		if (mode==Mode.Math) {
			int i;
			for (i=0; i<mathButtons.length; i++) {
				mathButtons[i].draw(canvas);
			}		
		}
		else {
			int i;
			for (i=0; i<buttons.length; i++) {
				buttons[i].draw(canvas);
			}			
		}
		}catch(Exception e) {
    		
    	}
		}
	}
	
	/**mode를 바꿀 때는 바꿀 mode의 한 칸 전 Mode로 파라미터를 설정한다.*/
	public void process자판(Mode paramMode) {
		// 자판이 바뀌기 전의 선택상태를 물려받는다.
		Mode modeLocal = paramMode;
		if (modeLocal==Mode.Math) {
			mode = Mode.Hangul;
			is자판Selected = mathButtons[0].isSelected;					
		}
		else if (modeLocal==Mode.Hangul) {
			mode = Mode.Eng;
			is자판Selected = buttons[0].isSelected;
		}
		else if (modeLocal==Mode.Eng) {
			mode = Mode.Remainder;
			is자판Selected = buttons[0].isSelected;
		}
		else {
			mode = Mode.Math;
			is자판Selected = buttons[0].isSelected;
		}
		
		changeModeAndButtonText(mode);
		
		if (mode==Mode.Math) {
			mathButtons[0].isSelected = is자판Selected;
		}
		else if (mode==Mode.Hangul) {
			buttons[0].isSelected = is자판Selected;
		}
		else if (mode==Mode.Eng) {
			buttons[0].isSelected = is자판Selected;
		}
		else {
			buttons[0].isSelected = is자판Selected;
		}
	}
	
	void processLang(Button button) {		
		langDialog.open();
		this.hides = false;
	}
	
	void forwardCursorOfEditText() {
		if (mode==Mode.Hangul && Hangul.mode!=Hangul.Mode.None){
			String className = (listener.getClass()).getName();
			String packageName = listener.getClass().getPackage().getName();
			if (className.equals(packageName + ".EditText")) {
				((EditText)listener).forwardCursorX();
			}
			else if (className.equals(packageName + ".EditRichText")) {
				((EditRichText)listener).forwardCursorX();
			} 
		}
	}
	
	void initHangul() {
		Hangul.resetBuffer();
		Hangul.isNextToCursor = false;
		Hangul.mode = Hangul.Mode.None;
	}
	
	public static class HardwareKeyboard {
		int keyCode;
		KeyEvent event;
		static boolean isEnglishKeyboard = true;
		
		static KeyCharacterMap keyCharacterMap;
		String key;
		
		static char[] buf = new char[2];
		static int count = 0;
		
		public HardwareKeyboard(int keyCode, KeyEvent event) {
			this.keyCode = keyCode;
			this.event = event;
			
			if (Control.keyboard.hides==false) {
				Control.keyboard.setHides(true);
			}
			
			int deviceId = event.getDeviceId();
			if (keyCharacterMap==null) {
				keyCharacterMap = KeyCharacterMap.load(deviceId);
			}
			
			
			
			int metaState = event.getMetaState();
			//char c = (char) keyCharacterMap.get(keyCode, metaState);
			int unicode = event.getUnicodeChar(metaState); 
			char c = (char)unicode;
			boolean isAltPressed = event.isAltPressed();
			boolean isShiftPressed = event.isShiftPressed();
			// 0(keyCode==59, 65)
			if (keyCode==67) {
				key = "BkSp";
				count = 0;
			}
			else if (isShiftPressed && keyCode==KeyEvent.KEYCODE_SHIFT_LEFT || keyCode==KeyEvent.KEYCODE_SHIFT_RIGHT) {
				key = "Shift";
				count = 0;
			}
			else if (c=='%') {
				buf[count++] = c;
				char[] arg = {c};
				key = new String(arg);
			}
			else if (c=='h') {				
				if (isEnglishKeyboard) {
					char[] arg = {c};
					key = new String(arg);
				}
				else {
					key = convertToHangul(c);
				}
				if (count>0 && buf[count-1]=='%') {
					isEnglishKeyboard = !isEnglishKeyboard;
					if (isEnglishKeyboard) {
						Control.keyboard.mode = Mode.Eng;
					}
					else {
						Control.keyboard.mode = Mode.Hangul;
					}
					//process자판(Control.keyboard.mode);
				}
				count = 0;
			}
			else {
				count = 0;
				if (isEnglishKeyboard) {
					char[] arg = {c};
					key = new String(arg);
				}
				else {
					key = convertToHangul(c);
				}
			}
		}
		
		String convertToHangul(char c) {
			switch (c) {
			case 'a': return "ㅁ";
			case 'b': return "ㅠ";
			case 'c': return "ㅊ";
			case 'd': return "ㅇ";
			case 'e': return "ㄷ";
			case 'f': return "ㄹ";
			case 'g': return "ㅎ";
			case 'h': return "ㅗ";
			case 'i': return "ㅑ";
			case 'j': return "ㅓ";
			case 'k': return "ㅏ";
			case 'l': return "ㅣ";
			case 'm': return "ㅡ";
			case 'n': return "ㅜ";
			case 'o': return "ㅐ";
			case 'p': return "ㅔ";
			case 'q': return "ㅂ";
			case 'r': return "ㄱ";
			case 's': return "ㄴ";
			case 't': return "ㅅ";
			case 'u': return "ㅕ";
			case 'v': return "ㅍ";
			case 'w': return "ㅈ";
			case 'x': return "ㅌ";
			case 'y': return "ㅛ";
			case 'z': return "ㅋ";
			
			case 'A': return "ㅁ";
			case 'B': return "ㅠ";
			case 'C': return "ㅁ";
			case 'D': return "ㅇ";
			case 'E': return "ㄸ";
			case 'F': return "ㄹ";
			case 'G': return "ㅎ";
			case 'H': return "ㅗ";
			case 'I': return "ㅑ";
			case 'J': return "ㅓ";
			case 'K': return "ㅏ";
			case 'L': return "ㅣ";
			case 'M': return "ㅡ";
			case 'N': return "ㅜ";
			case 'O': return "ㅒ";
			case 'P': return "ㅖ";
			case 'Q': return "ㅃ";
			case 'R': return "ㄲ";
			case 'S': return "ㄴ";
			case 'T': return "ㅆ";
			case 'U': return "ㅕ";
			case 'V': return "ㅍ";
			case 'W': return "ㅉ";
			case 'X': return "ㅌ";
			case 'Y': return "ㅛ";
			case 'Z': return "ㅋ";			
			}
			char[] r = {c};
			return new String(r);
		}
		
	}

	@Override
	public void onTouchEvent(Object sender, MotionEvent e) {
		// TODO Auto-generated method stub
		try{
		Mode oldMode=null;
		
		if (sender instanceof HardwareKeyboard) {
			HardwareKeyboard hardwareKeyboard = (HardwareKeyboard)sender;
			
			String k = hardwareKeyboard.key;
			if (k.equals("Shift")) return;
			
			/*if (0<=k.compareTo("0") && k.compareTo("~")<=0) {
				key = hardwareKeyboard.key;			
			}
			else if (0<=k.compareTo("!") && k.compareTo("/")<=0) {
				key = hardwareKeyboard.key;			
			}
			else if (0<=k.compareTo("ㄱ") && k.compareTo("ㅎ")<=0) {
				key = hardwareKeyboard.key;
				try {
					key = Hangul.convert(key);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else {
				key = hardwareKeyboard.key;
			}*/
			if (HardwareKeyboard.isEnglishKeyboard==false) {
				key = hardwareKeyboard.key;
				try {
					key = Hangul.convert(key);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else {
				key = hardwareKeyboard.key;
			}
			listener.onTouchEvent(this, e);
		}
		else if (sender instanceof Button) {
			Button button = (Button)sender;
			
			if (button.name.equals(자판)) {
				forwardCursorOfEditText();
				process자판(this.mode);
				//if (mode!=Mode.Hangul) {
					initHangul();
				//}
				return;
			}
			if (button.name.equals(Lang)) {
				forwardCursorOfEditText();
				processLang(button);
				//if (mode!=Mode.Hangul) {
					initHangul();
				//}
				return;
			}
			
			/*// 한글이 완성중일 때는 isNextToCursor를 true로 설정해 다른 문자를 입력시
			// 커서 다음 칸에 입력되도록 한다.			
			Hangul.isNextToCursor = isNextToCursorMember;*/
			
			
			if (mode==Mode.Math) {
				key = button.text;
				if (key.equals(Shift)) return;
				listener.onTouchEvent(this, e);
			}			
			else if (mode==Mode.Hangul) {
				try {
					key = button.text;
					if (key.equals(Shift)) return;
					if (key.equals(Space)|| key.equals(Delete) || key.equals(Enter)
							 || key.equals(BackSpace)) {
					}
					else {
						char c = key.toCharArray()[0];	// ㅃ/ㅂ의 첫 번째문자:ㅃ
						char[] arg = new char[1];
						arg[0] = Hangul.convert(buttons[ShiftIndex].isSelected, c);
						key = new String(arg);
					}
					key = Hangul.convert(key);
					listener.onTouchEvent(this, e);
				}catch(Exception ex) {
					Log.e("Keyboard-Hangul", ex.toString());
					Hangul.resetBuffer();
				}
			}
			else if (mode==Mode.Eng) {
				key = button.text;
				if (key.equals(Shift)) return;
				if (0<=button.text.compareTo("a") && button.text.compareTo("z")<=0) {
					char c = button.text.toCharArray()[0];
					char[] arg = new char[1];
					arg[0] = English.convert(buttons[ShiftIndex].isSelected, c);
					key = new String(arg);					
				}
				listener.onTouchEvent(this, e);
			}
			else {
				key = button.text;
				if (key.equals(Shift)) return;
				listener.onTouchEvent(this, e);
			}			
		
		} // if (className.equals(packageName + ".Button"))
		else if (sender instanceof LangDialog) {
			// LangDialog에서 문자를 입력시
			oldMode = mode; 
			mode = Mode.ChineseAndJapanese;
			
			String curChar = langDialog.curChar;
			key = curChar;					
			listener.onTouchEvent(this, e);
			
			mode = oldMode;
		}
		}catch(Exception e3) {
			e3.printStackTrace();
		}
		
	}

}
