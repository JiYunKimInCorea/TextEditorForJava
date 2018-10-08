package com.gsoft.common;

public class Encoding {
	public static class EncodingFormatException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	}
	
	public static class KSC5601 {
		static class Mapping {
			char utf8Code;
			int kscCode;
			Mapping(char utf8Code, int kscCode) {
				this.utf8Code = utf8Code;
				this.kscCode = kscCode;
			}
		}
		
		
		/**0xB0A1(가)에서 B0는 PAGE를 말하고 A1은 OFFSET이므로 두 값을 더해서 매핑을 찾고,
		 * ms949 코드 '가'를 찾는다.*/
		static int findLSI(byte msb, byte lsb) {
			
			int msBits4_lsb, lsBits4_lsb;
			
			int iMsb = (int)(msb & 0xff);
			
			msBits4_lsb = (int)((lsb & 0xf0) >>> 4);
			lsBits4_lsb = (int)((lsb & 0x0f));
			
			int pageIndex = (iMsb - 0xb0) * 95;
			int offset = (msBits4_lsb-10) * 16 + lsBits4_lsb;
			return pageIndex + offset;
		}
		
		
		/** 0xA4A1 - 0xA4D3 사이 코드라는 가정하에서 호출한다. 매핑배열의 인덱스를 리턴*/
		static int findLSI_jamo(byte b) {
			int msBits4, lsBits4;
			msBits4 = (int)((b & 0xf0) >>> 4);
			lsBits4 = (int)((b & 0x0f));
			return (msBits4-10) * 16 + lsBits4;
		}
		
		/** 파일에 저장할 때 호출한다.  매핑배열의 인덱스를 리턴*/
		static int findKSC(char c) {
			int i;
			if ('ㄱ'<=c && c<='ㅎ') {
				for (i=1; i<31; i++) {
					if (c==ja_mo_code[i].utf8Code) return ja_mo_code[i].kscCode;
				}
			}
			else if ('ㅏ'<=c && c<='ㅣ') {
				for (i=31; i<ja_mo_code.length; i++) {
					if (c==ja_mo_code[i].utf8Code) return ja_mo_code[i].kscCode;
				}
			}
			else if ('가'<=c && c<='괆') {
				for (i=0; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('괌'<=c && c<='깸') {
				for (i=95; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('깹'<=c && c<='끙') {
				for (i=95*2; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('끝'<=c && c<='뇝') {
				for (i=95*3; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('뇟'<=c && c<='덥') {
				for (i=95*4; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('덧'<=c && c<='딸') {
				for (i=95*5; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('땀'<=c && c<='랗') {
				for (i=95*6; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('래'<=c && c<='륩') {
				for (i=95*7; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('륫'<=c && c<='뫼') {
				for (i=95*8; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('묀'<=c && c<='벗') {
				for (i=95*9; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('벙'<=c && c<='빤') {
				for (i=95*10; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('빨'<=c && c<='샤') {
				for (i=95*11; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('샥'<=c && c<='숭') {
				for (i=95*12; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('숯'<=c && c<='쐰') {
				for (i=95*13; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('쐴'<=c && c<='엎') {
				for (i=95*14; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('에'<=c && c<='웨') {
				for (i=95*15; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('웩'<=c && c<='젊') {
				for (i=95*16; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('점'<=c && c<='짓') {
				for (i=95*17; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('징'<=c && c<='찻') {
				for (i=95*18; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('찼'<=c && c<='층') {
				for (i=95*19; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('치'<=c && c<='퉜') {
				for (i=95*20; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('퉤'<=c && c<='퐁') {
				for (i=95*21; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('퐈'<=c && c<='혠') {
				for (i=95*22; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			else if ('혤'<=c && c<='힝') {
				for (i=95*23; i<wansung_code.length; i++) {
					if (c==wansung_code[i].utf8Code) return wansung_code[i].kscCode;
				}
			}
			
			return 0;
		}
		
		static Mapping[] ja_mo_code = 
		{	
			new Mapping('0', 0),
			new Mapping('ㄱ', 0xA4A1),    new Mapping('ㄲ', 0xA4A2),    new Mapping('ㄳ', 0xA4A3),    new Mapping('ㄴ', 0xA4A4),    new Mapping('ㄵ', 0xA4A5), 
			new Mapping('ㄶ', 0xA4A6),    new Mapping('ㄷ', 0xA4A7),    new Mapping('ㄸ', 0xA4A8),    new Mapping('ㄹ', 0xA4A9),    new Mapping('ㄺ', 0xA4AA), 
			new Mapping('ㄻ', 0xA4AB),    new Mapping('ㄼ', 0xA4AC),    new Mapping('ㄽ', 0xA4AD),    new Mapping('ㄾ', 0xA4AE),    new Mapping('ㄿ', 0xA4AF), 
			new Mapping('ㅀ', 0xA4B0),    new Mapping('ㅁ', 0xA4B1),    new Mapping('ㅂ', 0xA4B2),    new Mapping('ㅃ', 0xA4B3),    new Mapping('ㅄ', 0xA4B4), 
			new Mapping('ㅅ', 0xA4B5),    new Mapping('ㅆ', 0xA4B6),    new Mapping('ㅇ', 0xA4B7),   new Mapping('ㅈ', 0xA4B8 ),   new Mapping('ㅉ', 0xA4B9), 
			new Mapping('ㅊ', 0xA4BA),    new Mapping('ㅋ', 0xA4BB),    new Mapping('ㅌ', 0xA4BC),    new Mapping('ㅍ', 0xA4BD),    new Mapping('ㅎ', 0xA4BE),
			
			new Mapping('ㅏ', 0xA4BF), 	 new Mapping('ㅐ', 0xA4C0),    new Mapping('ㅑ', 0xA4C1),    new Mapping('ㅒ', 0xA4C2),    new Mapping('ㅓ', 0xA4C3), 
			new Mapping('ㅔ', 0xA4C4),    new Mapping('ㅕ', 0xA4C5),    new Mapping('ㅖ', 0xA4C6),    new Mapping('ㅗ', 0xA4C7),    new Mapping('ㅘ', 0xA4C8), 
			new Mapping('ㅙ', 0xA4C9),    new Mapping('ㅚ', 0xA4CA),    new Mapping('ㅛ', 0xA4CB),    new Mapping('ㅜ', 0xA4CC),    new Mapping('ㅝ', 0xA4CD), 
			new Mapping('ㅞ', 0xA4CE),    new Mapping('ㅟ', 0xA4CF),    new Mapping('ㅠ', 0xA4D0),    new Mapping('ㅡ', 0xA4D1),    new Mapping('ㅢ', 0xA4D2), 
			new Mapping('ㅣ', 0xA4D3)
		};	
		
		/** 한 페이지에 95개의 문자(맨처음코드를 빼면 94개)*/
		static Mapping[] wansung_code = {
			
				new Mapping((char)0, 0),
				new Mapping('가', 0xB0A1),    new Mapping('각', 0xB0A2),    new Mapping('간', 0xB0A3),    new Mapping('갇', 0xB0A4),    new Mapping('갈', 0xB0A5),     
				new Mapping('갉', 0xB0A6),    new Mapping('갊', 0xB0A7),    new Mapping('감', 0xB0A8),    new Mapping('갑', 0xB0A9),    new Mapping('값', 0xB0AA), 
				new Mapping('갓', 0xB0AB),    new Mapping('갔', 0xB0AC),    new Mapping('강', 0xB0AD),    new Mapping('갖', 0xB0AE),    new Mapping('갗', 0xB0AF), 
				new Mapping('같', 0xB0B0),    new Mapping('갚', 0xB0B1),    new Mapping('갛', 0xB0B2),    new Mapping('개', 0xB0B3),    new Mapping('객', 0xB0B4), 
				new Mapping('갠', 0xB0B5),    new Mapping('갤', 0xB0B6),    new Mapping('갬', 0xB0B7),    new Mapping('갭', 0xB0B8),    new Mapping('갯', 0xB0B9), 
				new Mapping('갰', 0xB0BA),    new Mapping('갱', 0xB0BB),    new Mapping('갸', 0xB0BC),    new Mapping('갹', 0xB0BD),    new Mapping('갼', 0xB0BE), 
				new Mapping('걀', 0xB0BF),    new Mapping('걋', 0xB0C0),    new Mapping('걍', 0xB0C1),    new Mapping('걔', 0xB0C2),    new Mapping('걘', 0xB0C3), 
				new Mapping('걜', 0xB0C4),    new Mapping('거', 0xB0C5),    new Mapping('걱', 0xB0C6),    new Mapping('건', 0xB0C7),    new Mapping('걷', 0xB0C8), 
				new Mapping('걸', 0xB0C9),    new Mapping('걺', 0xB0CA),    new Mapping('검', 0xB0CB),    new Mapping('겁', 0xB0CC),    new Mapping('것', 0xB0CD), 
				new Mapping('겄', 0xB0CE),    new Mapping('겅', 0xB0CF),    new Mapping('겆', 0xB0D0),    new Mapping('겉', 0xB0D1),    new Mapping('겊', 0xB0D2), 
				new Mapping('겋', 0xB0D3),    new Mapping('게', 0xB0D4),    new Mapping('겐', 0xB0D5),    new Mapping('겔', 0xB0D6),    new Mapping('겜', 0xB0D7), 
				new Mapping('겝', 0xB0D8),    new Mapping('겟', 0xB0D9),    new Mapping('겠', 0xB0DA),    new Mapping('겡', 0xB0DB),    new Mapping('겨', 0xB0DC), 
				new Mapping('격', 0xB0DD),    new Mapping('겪', 0xB0DE),    new Mapping('견', 0xB0DF),    new Mapping('겯', 0xB0E0),    new Mapping('결', 0xB0E1), 
				new Mapping('겸', 0xB0E2),    new Mapping('겹', 0xB0E3),    new Mapping('겻', 0xB0E4),    new Mapping('겼', 0xB0E5),    new Mapping('경', 0xB0E6), 
				new Mapping('곁', 0xB0E7),    new Mapping('계', 0xB0E8),    new Mapping('곈', 0xB0E9),    new Mapping('곌', 0xB0EA),    new Mapping('곕', 0xB0EB), 
				new Mapping('곗', 0xB0EC),    new Mapping('고', 0xB0ED),    new Mapping('곡', 0xB0EE),    new Mapping('곤', 0xB0EF),    new Mapping('곧', 0xB0F0), 
				new Mapping('골', 0xB0F1),    new Mapping('곪', 0xB0F2),    new Mapping('곬', 0xB0F3),    new Mapping('곯', 0xB0F4),    new Mapping('곰', 0xB0F5), 
				new Mapping('곱', 0xB0F6),    new Mapping('곳', 0xB0F7),    new Mapping('공', 0xB0F8),    new Mapping('곶', 0xB0F9),    new Mapping('과', 0xB0FA), 
				new Mapping('곽', 0xB0FB),    new Mapping('관', 0xB0FC),    new Mapping('괄', 0xB0FD),    new Mapping('괆', 0xB0FE),    
			//},
				
				
			//{	
				new Mapping((char)0, 0),
				new Mapping('괌', 0xB1A1), 
					new Mapping('괍', 0xB1A2),    new Mapping('괏', 0xB1A3),    new Mapping('광', 0xB1A4),    new Mapping('괘', 0xB1A5),    new Mapping('괜', 0xB1A6), 
					new Mapping('괠', 0xB1A7),    new Mapping('괩', 0xB1A8),    new Mapping('괬', 0xB1A9),    new Mapping('괭', 0xB1AA),    new Mapping('괴', 0xB1AB), 
					new Mapping('괵', 0xB1AC),    new Mapping('괸', 0xB1AD),    new Mapping('괼', 0xB1AE),    new Mapping('굄', 0xB1AF),    new Mapping('굅', 0xB1B0), 
					new Mapping('굇', 0xB1B1),    new Mapping('굉', 0xB1B2),    new Mapping('교', 0xB1B3),    new Mapping('굔', 0xB1B4),    new Mapping('굘', 0xB1B5), 
					new Mapping('굡', 0xB1B6),    new Mapping('굣', 0xB1B7),    new Mapping('구', 0xB1B8),    new Mapping('국', 0xB1B9),    new Mapping('군', 0xB1BA), 
					new Mapping('굳', 0xB1BB),    new Mapping('굴', 0xB1BC),    new Mapping('굵', 0xB1BD),    new Mapping('굶', 0xB1BE),    new Mapping('굻', 0xB1BF), 
					new Mapping('굼', 0xB1C0),    new Mapping('굽', 0xB1C1),    new Mapping('굿', 0xB1C2),    new Mapping('궁', 0xB1C3),    new Mapping('궂', 0xB1C4), 
					new Mapping('궈', 0xB1C5),    new Mapping('궉', 0xB1C6),    new Mapping('권', 0xB1C7),    new Mapping('궐', 0xB1C8),    new Mapping('궜', 0xB1C9), 
					new Mapping('궝', 0xB1CA),    new Mapping('궤', 0xB1CB),    new Mapping('궷', 0xB1CC),    new Mapping('귀', 0xB1CD),    new Mapping('귁', 0xB1CE), 
					new Mapping('귄', 0xB1CF),    new Mapping('귈', 0xB1D0),    new Mapping('귐', 0xB1D1),    new Mapping('귑', 0xB1D2),    new Mapping('귓', 0xB1D3), 
					new Mapping('규', 0xB1D4),    new Mapping('균', 0xB1D5),    new Mapping('귤', 0xB1D6),    new Mapping('그', 0xB1D7),    new Mapping('극', 0xB1D8), 
					new Mapping('근', 0xB1D9),    new Mapping('귿', 0xB1DA),    new Mapping('글', 0xB1DB),    new Mapping('긁', 0xB1DC),    new Mapping('금', 0xB1DD), 
					new Mapping('급', 0xB1DE),    new Mapping('긋', 0xB1DF),    new Mapping('긍', 0xB1E0),    new Mapping('긔', 0xB1E1),    new Mapping('기', 0xB1E2), 
					new Mapping('긱', 0xB1E3),    new Mapping('긴', 0xB1E4),    new Mapping('긷', 0xB1E5),    new Mapping('길', 0xB1E6),    new Mapping('긺', 0xB1E7), 
					new Mapping('김', 0xB1E8),    new Mapping('깁', 0xB1E9),    new Mapping('깃', 0xB1EA),    new Mapping('깅', 0xB1EB),    new Mapping('깆', 0xB1EC), 
					new Mapping('깊', 0xB1ED),    new Mapping('까', 0xB1EE),    new Mapping('깍', 0xB1EF),    new Mapping('깎', 0xB1F0),    new Mapping('깐', 0xB1F1), 
					new Mapping('깔', 0xB1F2),    new Mapping('깖', 0xB1F3),    new Mapping('깜', 0xB1F4),    new Mapping('깝', 0xB1F5),    new Mapping('깟', 0xB1F6), 
					new Mapping('깠', 0xB1F7),    new Mapping('깡', 0xB1F8),    new Mapping('깥', 0xB1F9),    new Mapping('깨', 0xB1FA),    new Mapping('깩', 0xB1FB), 
					new Mapping('깬', 0xB1FC),    new Mapping('깰', 0xB1FD),    new Mapping('깸', 0xB1FE),    
			//},

			//{	
					new Mapping((char)0, 0),
						new Mapping('깹', 0xB2A1),    new Mapping('깻', 0xB2A2), 
						new Mapping('깼', 0xB2A3),    new Mapping('깽', 0xB2A4),    new Mapping('꺄', 0xB2A5),    new Mapping('꺅', 0xB2A6),    new Mapping('꺌', 0xB2A7), 
						new Mapping('꺼', 0xB2A8),    new Mapping('꺽', 0xB2A9),    new Mapping('꺾', 0xB2AA),    new Mapping('껀', 0xB2AB),    new Mapping('껄', 0xB2AC), 
						new Mapping('껌', 0xB2AD),    new Mapping('껍', 0xB2AE),    new Mapping('껏', 0xB2AF),    new Mapping('껐', 0xB2B0),    new Mapping('껑', 0xB2B1), 
						new Mapping('께', 0xB2B2),    new Mapping('껙', 0xB2B3),    new Mapping('껜', 0xB2B4),    new Mapping('껨', 0xB2B5),    new Mapping('껫', 0xB2B6), 
						new Mapping('껭', 0xB2B7),    new Mapping('껴', 0xB2B8),    new Mapping('껸', 0xB2B9),    new Mapping('껼', 0xB2BA),    new Mapping('꼇', 0xB2BB), 
						new Mapping('꼈', 0xB2BC),    new Mapping('꼍', 0xB2BD),    new Mapping('꼐', 0xB2BE),    new Mapping('꼬', 0xB2BF),    new Mapping('꼭', 0xB2C0), 
						new Mapping('꼰', 0xB2C1),    new Mapping('꼲', 0xB2C2),    new Mapping('꼴', 0xB2C3),    new Mapping('꼼', 0xB2C4),    new Mapping('꼽', 0xB2C5), 
						new Mapping('꼿', 0xB2C6),    new Mapping('꽁', 0xB2C7),    new Mapping('꽂', 0xB2C8),    new Mapping('꽃', 0xB2C9),    new Mapping('꽈', 0xB2CA), 
						new Mapping('꽉', 0xB2CB),    new Mapping('꽐', 0xB2CC),    new Mapping('꽜', 0xB2CD),    new Mapping('꽝', 0xB2CE),    new Mapping('꽤', 0xB2CF), 
						new Mapping('꽥', 0xB2D0),    new Mapping('꽹', 0xB2D1),    new Mapping('꾀', 0xB2D2),    new Mapping('꾄', 0xB2D3),    new Mapping('꾈', 0xB2D4), 
						new Mapping('꾐', 0xB2D5),    new Mapping('꾑', 0xB2D6),    new Mapping('꾕', 0xB2D7),    new Mapping('꾜', 0xB2D8),    new Mapping('꾸', 0xB2D9), 
						new Mapping('꾹', 0xB2DA),    new Mapping('꾼', 0xB2DB),    new Mapping('꿀', 0xB2DC),    new Mapping('꿇', 0xB2DD),    new Mapping('꿈', 0xB2DE), 
						new Mapping('꿉', 0xB2DF),    new Mapping('꿋', 0xB2E0),    new Mapping('꿍', 0xB2E1),    new Mapping('꿎', 0xB2E2),    new Mapping('꿔', 0xB2E3), 
						new Mapping('꿜', 0xB2E4),    new Mapping('꿨', 0xB2E5),    new Mapping('꿩', 0xB2E6),    new Mapping('꿰', 0xB2E7),    new Mapping('꿱', 0xB2E8), 
						new Mapping('꿴', 0xB2E9),    new Mapping('꿸', 0xB2EA),    new Mapping('뀀', 0xB2EB),    new Mapping('뀁', 0xB2EC),    new Mapping('뀄', 0xB2ED), 
						new Mapping('뀌', 0xB2EE),    new Mapping('뀐', 0xB2EF),    new Mapping('뀔', 0xB2F0),    new Mapping('뀜', 0xB2F1),    new Mapping('뀝', 0xB2F2), 
						new Mapping('뀨', 0xB2F3),    new Mapping('끄', 0xB2F4),    new Mapping('끅', 0xB2F5),    new Mapping('끈', 0xB2F6),    new Mapping('끊', 0xB2F7), 
						new Mapping('끌', 0xB2F8),    new Mapping('끎', 0xB2F9),    new Mapping('끓', 0xB2FA),    new Mapping('끔', 0xB2FB),    new Mapping('끕', 0xB2FC), 
						new Mapping('끗', 0xB2FD),    new Mapping('끙', 0xB2FE),
			//},
			
			//{	
				new Mapping((char)0, 0),
				new Mapping('끝', 0xB3A1),    new Mapping('끼', 0xB3A2),    new Mapping('끽', 0xB3A3), 
				new Mapping('낀', 0xB3A4),    new Mapping('낄', 0xB3A5),    new Mapping('낌', 0xB3A6),    new Mapping('낍', 0xB3A7),    new Mapping('낏', 0xB3A8), 
				new Mapping('낑', 0xB3A9),    new Mapping('나', 0xB3AA),    new Mapping('낙', 0xB3AB),    new Mapping('낚', 0xB3AC),    new Mapping('난', 0xB3AD), 
				new Mapping('낟', 0xB3AE),    new Mapping('날', 0xB3AF),    new Mapping('낡', 0xB3B0),    new Mapping('낢', 0xB3B1),    new Mapping('남', 0xB3B2), 
				new Mapping('납', 0xB3B3),    new Mapping('낫', 0xB3B4),    new Mapping('났', 0xB3B5),    new Mapping('낭', 0xB3B6),    new Mapping('낮', 0xB3B7), 
				new Mapping('낯', 0xB3B8),    new Mapping('낱', 0xB3B9),    new Mapping('낳', 0xB3BA),    new Mapping('내', 0xB3BB),    new Mapping('낵', 0xB3BC), 
				new Mapping('낸', 0xB3BD),    new Mapping('낼', 0xB3BE),    new Mapping('냄', 0xB3BF),    new Mapping('냅', 0xB3C0),    new Mapping('냇', 0xB3C1), 
				new Mapping('냈', 0xB3C2),    new Mapping('냉', 0xB3C3),    new Mapping('냐', 0xB3C4),    new Mapping('냑', 0xB3C5),    new Mapping('냔', 0xB3C6), 
				new Mapping('냘', 0xB3C7),    new Mapping('냠', 0xB3C8),    new Mapping('냥', 0xB3C9),    new Mapping('너', 0xB3CA),    new Mapping('넉', 0xB3CB), 
				new Mapping('넋', 0xB3CC),    new Mapping('넌', 0xB3CD),    new Mapping('널', 0xB3CE),    new Mapping('넒', 0xB3CF),    new Mapping('넓', 0xB3D0), 
				new Mapping('넘', 0xB3D1),    new Mapping('넙', 0xB3D2),    new Mapping('넛', 0xB3D3),    new Mapping('넜', 0xB3D4),    new Mapping('넝', 0xB3D5), 
				new Mapping('넣', 0xB3D6),    new Mapping('네', 0xB3D7),    new Mapping('넥', 0xB3D8),    new Mapping('넨', 0xB3D9),    new Mapping('넬', 0xB3DA), 
				new Mapping('넴', 0xB3DB),    new Mapping('넵', 0xB3DC),    new Mapping('넷', 0xB3DD),    new Mapping('넸', 0xB3DE),    new Mapping('넹', 0xB3DF), 
				new Mapping('녀', 0xB3E0),    new Mapping('녁', 0xB3E1),    new Mapping('년', 0xB3E2),    new Mapping('녈', 0xB3E3),    new Mapping('념', 0xB3E4), 
				new Mapping('녑', 0xB3E5),    new Mapping('녔', 0xB3E6),    new Mapping('녕', 0xB3E7),    new Mapping('녘', 0xB3E8),    new Mapping('녜', 0xB3E9), 
				new Mapping('녠', 0xB3EA),    new Mapping('노', 0xB3EB),    new Mapping('녹', 0xB3EC),    new Mapping('논', 0xB3ED),    new Mapping('놀', 0xB3EE), 
				new Mapping('놂', 0xB3EF),    new Mapping('놈', 0xB3F0),    new Mapping('놉', 0xB3F1),    new Mapping('놋', 0xB3F2),    new Mapping('농', 0xB3F3), 
				new Mapping('높', 0xB3F4),    new Mapping('놓', 0xB3F5),    new Mapping('놔', 0xB3F6),    new Mapping('놘', 0xB3F7),    new Mapping('놜', 0xB3F8), 
				new Mapping('놨', 0xB3F9),    new Mapping('뇌', 0xB3FA),    new Mapping('뇐', 0xB3FB),    new Mapping('뇔', 0xB3FC),    new Mapping('뇜', 0xB3FD), 
				new Mapping('뇝', 0xB3FE),   
			//},
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('뇟', 0xB4A1),    new Mapping('뇨', 0xB4A2),    new Mapping('뇩', 0xB4A3),    new Mapping('뇬', 0xB4A4), 
				new Mapping('뇰', 0xB4A5),    new Mapping('뇹', 0xB4A6),    new Mapping('뇻', 0xB4A7),    new Mapping('뇽', 0xB4A8),    new Mapping('누', 0xB4A9), 
				new Mapping('눅', 0xB4AA),    new Mapping('눈', 0xB4AB),    new Mapping('눋', 0xB4AC),    new Mapping('눌', 0xB4AD),    new Mapping('눔', 0xB4AE), 
				new Mapping('눕', 0xB4AF),    new Mapping('눗', 0xB4B0),    new Mapping('눙', 0xB4B1),    new Mapping('눠', 0xB4B2),    new Mapping('눴', 0xB4B3), 
				new Mapping('눼', 0xB4B4),    new Mapping('뉘', 0xB4B5),    new Mapping('뉜', 0xB4B6),    new Mapping('뉠', 0xB4B7),    new Mapping('뉨', 0xB4B8), 
				new Mapping('뉩', 0xB4B9),    new Mapping('뉴', 0xB4BA),    new Mapping('뉵', 0xB4BB),    new Mapping('뉼', 0xB4BC),    new Mapping('늄', 0xB4BD), 
				new Mapping('늅', 0xB4BE),    new Mapping('늉', 0xB4BF),    new Mapping('느', 0xB4C0),    new Mapping('늑', 0xB4C1),    new Mapping('는', 0xB4C2), 
				new Mapping('늘', 0xB4C3),    new Mapping('늙', 0xB4C4),    new Mapping('늚', 0xB4C5),    new Mapping('늠', 0xB4C6),    new Mapping('늡', 0xB4C7), 
				new Mapping('늣', 0xB4C8),    new Mapping('능', 0xB4C9),    new Mapping('늦', 0xB4CA),    new Mapping('늪', 0xB4CB),    new Mapping('늬', 0xB4CC), 
				new Mapping('늰', 0xB4CD),    new Mapping('늴', 0xB4CE),    new Mapping('니', 0xB4CF),    new Mapping('닉', 0xB4D0),    new Mapping('닌', 0xB4D1), 
				new Mapping('닐', 0xB4D2),    new Mapping('닒', 0xB4D3),    new Mapping('님', 0xB4D4),    new Mapping('닙', 0xB4D5),    new Mapping('닛', 0xB4D6), 
				new Mapping('닝', 0xB4D7),    new Mapping('닢', 0xB4D8),    new Mapping('다', 0xB4D9),    new Mapping('닥', 0xB4DA),    new Mapping('닦', 0xB4DB), 
				new Mapping('단', 0xB4DC),    new Mapping('닫', 0xB4DD),    new Mapping('달', 0xB4DE),    new Mapping('닭', 0xB4DF),    new Mapping('닮', 0xB4E0), 
				new Mapping('닯', 0xB4E1),    new Mapping('닳', 0xB4E2),    new Mapping('담', 0xB4E3),    new Mapping('답', 0xB4E4),    new Mapping('닷', 0xB4E5), 
				new Mapping('닸', 0xB4E6),    new Mapping('당', 0xB4E7),    new Mapping('닺', 0xB4E8),    new Mapping('닻', 0xB4E9),    new Mapping('닿', 0xB4EA), 
				new Mapping('대', 0xB4EB),    new Mapping('댁', 0xB4EC),    new Mapping('댄', 0xB4ED),    new Mapping('댈', 0xB4EE),    new Mapping('댐', 0xB4EF), 
				new Mapping('댑', 0xB4F0),    new Mapping('댓', 0xB4F1),    new Mapping('댔', 0xB4F2),    new Mapping('댕', 0xB4F3),    new Mapping('댜', 0xB4F4), 
				new Mapping('더', 0xB4F5),    new Mapping('덕', 0xB4F6),    new Mapping('덖', 0xB4F7),    new Mapping('던', 0xB4F8),    new Mapping('덛', 0xB4F9), 
				new Mapping('덜', 0xB4FA),    new Mapping('덞', 0xB4FB),    new Mapping('덟', 0xB4FC),    new Mapping('덤', 0xB4FD),    new Mapping('덥', 0xB4FE),


			//},
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('덧', 0xB5A1),    new Mapping('덩', 0xB5A2),    new Mapping('덫', 0xB5A3),    new Mapping('덮', 0xB5A4),    new Mapping('데', 0xB5A5), 
				new Mapping('덱', 0xB5A6),    new Mapping('덴', 0xB5A7),    new Mapping('델', 0xB5A8),    new Mapping('뎀', 0xB5A9),    new Mapping('뎁', 0xB5AA), 
				new Mapping('뎃', 0xB5AB),    new Mapping('뎄', 0xB5AC),    new Mapping('뎅', 0xB5AD),    new Mapping('뎌', 0xB5AE),    new Mapping('뎐', 0xB5AF), 
				new Mapping('뎔', 0xB5B0),    new Mapping('뎠', 0xB5B1),    new Mapping('뎡', 0xB5B2),    new Mapping('뎨', 0xB5B3),    new Mapping('뎬', 0xB5B4), 
				new Mapping('도', 0xB5B5),    new Mapping('독', 0xB5B6),    new Mapping('돈', 0xB5B7),    new Mapping('돋', 0xB5B8),    new Mapping('돌', 0xB5B9), 
				new Mapping('돎', 0xB5BA),    new Mapping('돐', 0xB5BB),    new Mapping('돔', 0xB5BC),    new Mapping('돕', 0xB5BD),    new Mapping('돗', 0xB5BE), 
				new Mapping('동', 0xB5BF),    new Mapping('돛', 0xB5C0),    new Mapping('돝', 0xB5C1),    new Mapping('돠', 0xB5C2),    new Mapping('돤', 0xB5C3), 
				new Mapping('돨', 0xB5C4),    new Mapping('돼', 0xB5C5),    new Mapping('됐', 0xB5C6),    new Mapping('되', 0xB5C7),    new Mapping('된', 0xB5C8), 
				new Mapping('될', 0xB5C9),    new Mapping('됨', 0xB5CA),    new Mapping('됩', 0xB5CB),    new Mapping('됫', 0xB5CC),    new Mapping('됴', 0xB5CD), 
				new Mapping('두', 0xB5CE),    new Mapping('둑', 0xB5CF),    new Mapping('둔', 0xB5D0),    new Mapping('둘', 0xB5D1),    new Mapping('둠', 0xB5D2), 
				new Mapping('둡', 0xB5D3),    new Mapping('둣', 0xB5D4),    new Mapping('둥', 0xB5D5),    new Mapping('둬', 0xB5D6),    new Mapping('뒀', 0xB5D7), 
				new Mapping('뒈', 0xB5D8),    new Mapping('뒝', 0xB5D9),    new Mapping('뒤', 0xB5DA),    new Mapping('뒨', 0xB5DB),    new Mapping('뒬', 0xB5DC), 
				new Mapping('뒵', 0xB5DD),    new Mapping('뒷', 0xB5DE),    new Mapping('뒹', 0xB5DF),    new Mapping('듀', 0xB5E0),    new Mapping('듄', 0xB5E1), 
				new Mapping('듈', 0xB5E2),    new Mapping('듐', 0xB5E3),    new Mapping('듕', 0xB5E4),    new Mapping('드', 0xB5E5),    new Mapping('득', 0xB5E6), 
				new Mapping('든', 0xB5E7),    new Mapping('듣', 0xB5E8),    new Mapping('들', 0xB5E9),    new Mapping('듦', 0xB5EA),    new Mapping('듬', 0xB5EB), 
				new Mapping('듭', 0xB5EC),    new Mapping('듯', 0xB5ED),    new Mapping('등', 0xB5EE),    new Mapping('듸', 0xB5EF),    new Mapping('디', 0xB5F0), 
				new Mapping('딕', 0xB5F1),    new Mapping('딘', 0xB5F2),    new Mapping('딛', 0xB5F3),    new Mapping('딜', 0xB5F4),    new Mapping('딤', 0xB5F5), 
				new Mapping('딥', 0xB5F6),    new Mapping('딧', 0xB5F7),    new Mapping('딨', 0xB5F8),    new Mapping('딩', 0xB5F9),    new Mapping('딪', 0xB5FA), 
				new Mapping('따', 0xB5FB),    new Mapping('딱', 0xB5FC),    new Mapping('딴', 0xB5FD),    new Mapping('딸', 0xB5FE),    

			//},
			
			//{
				new Mapping((char)0, 0),
				new Mapping('땀', 0xB6A1), 
				new Mapping('땁', 0xB6A2),    new Mapping('땃', 0xB6A3),    new Mapping('땄', 0xB6A4),    new Mapping('땅', 0xB6A5),    new Mapping('땋', 0xB6A6), 
				new Mapping('때', 0xB6A7),    new Mapping('땍', 0xB6A8),    new Mapping('땐', 0xB6A9),    new Mapping('땔', 0xB6AA),    new Mapping('땜', 0xB6AB), 
				new Mapping('땝', 0xB6AC),    new Mapping('땟', 0xB6AD),    new Mapping('땠', 0xB6AE),    new Mapping('땡', 0xB6AF),    new Mapping('떠', 0xB6B0), 
				new Mapping('떡', 0xB6B1),    new Mapping('떤', 0xB6B2),    new Mapping('떨', 0xB6B3),    new Mapping('떪', 0xB6B4),    new Mapping('떫', 0xB6B5), 
				new Mapping('떰', 0xB6B6),    new Mapping('떱', 0xB6B7),    new Mapping('떳', 0xB6B8),    new Mapping('떴', 0xB6B9),    new Mapping('떵', 0xB6BA), 
				new Mapping('떻', 0xB6BB),    new Mapping('떼', 0xB6BC),    new Mapping('떽', 0xB6BD),    new Mapping('뗀', 0xB6BE),    new Mapping('뗄', 0xB6BF), 
				new Mapping('뗌', 0xB6C0),    new Mapping('뗍', 0xB6C1),    new Mapping('뗏', 0xB6C2),    new Mapping('뗐', 0xB6C3),    new Mapping('뗑', 0xB6C4), 
				new Mapping('뗘', 0xB6C5),    new Mapping('뗬', 0xB6C6),    new Mapping('또', 0xB6C7),    new Mapping('똑', 0xB6C8),    new Mapping('똔', 0xB6C9), 
				new Mapping('똘', 0xB6CA),    new Mapping('똥', 0xB6CB),    new Mapping('똬', 0xB6CC),    new Mapping('똴', 0xB6CD),    new Mapping('뙈', 0xB6CE), 
				new Mapping('뙤', 0xB6CF),    new Mapping('뙨', 0xB6D0),    new Mapping('뚜', 0xB6D1),    new Mapping('뚝', 0xB6D2),    new Mapping('뚠', 0xB6D3), 
				new Mapping('뚤', 0xB6D4),    new Mapping('뚫', 0xB6D5),    new Mapping('뚬', 0xB6D6),    new Mapping('뚱', 0xB6D7),    new Mapping('뛔', 0xB6D8), 
				new Mapping('뛰', 0xB6D9),    new Mapping('뛴', 0xB6DA),    new Mapping('뛸', 0xB6DB),    new Mapping('뜀', 0xB6DC),    new Mapping('뜁', 0xB6DD), 
				new Mapping('뜅', 0xB6DE),    new Mapping('뜨', 0xB6DF),    new Mapping('뜩', 0xB6E0),    new Mapping('뜬', 0xB6E1),    new Mapping('뜯', 0xB6E2), 
				new Mapping('뜰', 0xB6E3),    new Mapping('뜸', 0xB6E4),    new Mapping('뜹', 0xB6E5),    new Mapping('뜻', 0xB6E6),    new Mapping('띄', 0xB6E7), 
				new Mapping('띈', 0xB6E8),    new Mapping('띌', 0xB6E9),    new Mapping('띔', 0xB6EA),    new Mapping('띕', 0xB6EB),    new Mapping('띠', 0xB6EC), 
				new Mapping('띤', 0xB6ED),    new Mapping('띨', 0xB6EE),    new Mapping('띰', 0xB6EF),    new Mapping('띱', 0xB6F0),    new Mapping('띳', 0xB6F1), 
				new Mapping('띵', 0xB6F2),    new Mapping('라', 0xB6F3),    new Mapping('락', 0xB6F4),    new Mapping('란', 0xB6F5),    new Mapping('랄', 0xB6F6), 
				new Mapping('람', 0xB6F7),    new Mapping('랍', 0xB6F8),    new Mapping('랏', 0xB6F9),    new Mapping('랐', 0xB6FA),    new Mapping('랑', 0xB6FB), 
				new Mapping('랒', 0xB6FC),    new Mapping('랖', 0xB6FD),    new Mapping('랗', 0xB6FE),


			//},
			
			
			//{
				new Mapping((char)0, 0),
				new Mapping('래', 0xB7A1),    new Mapping('랙', 0xB7A2), 
				new Mapping('랜', 0xB7A3),    new Mapping('랠', 0xB7A4),    new Mapping('램', 0xB7A5),    new Mapping('랩', 0xB7A6),    new Mapping('랫', 0xB7A7), 
				new Mapping('랬', 0xB7A8),    new Mapping('랭', 0xB7A9),    new Mapping('랴', 0xB7AA),    new Mapping('략', 0xB7AB),    new Mapping('랸', 0xB7AC), 
				new Mapping('럇', 0xB7AD),    new Mapping('량', 0xB7AE),    new Mapping('러', 0xB7AF),    new Mapping('럭', 0xB7B0),    new Mapping('런', 0xB7B1), 
				new Mapping('럴', 0xB7B2),    new Mapping('럼', 0xB7B3),    new Mapping('럽', 0xB7B4),    new Mapping('럿', 0xB7B5),    new Mapping('렀', 0xB7B6), 
				new Mapping('렁', 0xB7B7),    new Mapping('렇', 0xB7B8),    new Mapping('레', 0xB7B9),    new Mapping('렉', 0xB7BA),    new Mapping('렌', 0xB7BB), 
				new Mapping('렐', 0xB7BC),    new Mapping('렘', 0xB7BD),    new Mapping('렙', 0xB7BE),    new Mapping('렛', 0xB7BF),    new Mapping('렝', 0xB7C0), 
				new Mapping('려', 0xB7C1),    new Mapping('력', 0xB7C2),    new Mapping('련', 0xB7C3),    new Mapping('렬', 0xB7C4),    new Mapping('렴', 0xB7C5), 
				new Mapping('렵', 0xB7C6),    new Mapping('렷', 0xB7C7),    new Mapping('렸', 0xB7C8),    new Mapping('령', 0xB7C9),    new Mapping('례', 0xB7CA), 
				new Mapping('롄', 0xB7CB),    new Mapping('롑', 0xB7CC),    new Mapping('롓', 0xB7CD),    new Mapping('로', 0xB7CE),    new Mapping('록', 0xB7CF), 
				new Mapping('론', 0xB7D0),    new Mapping('롤', 0xB7D1),    new Mapping('롬', 0xB7D2),    new Mapping('롭', 0xB7D3),    new Mapping('롯', 0xB7D4), 
				new Mapping('롱', 0xB7D5),    new Mapping('롸', 0xB7D6),    new Mapping('롼', 0xB7D7),    new Mapping('뢍', 0xB7D8),    new Mapping('뢨', 0xB7D9), 
				new Mapping('뢰', 0xB7DA),    new Mapping('뢴', 0xB7DB),    new Mapping('뢸', 0xB7DC),    new Mapping('룀', 0xB7DD),    new Mapping('룁', 0xB7DE), 
				new Mapping('룃', 0xB7DF),    new Mapping('룅', 0xB7E0),    new Mapping('료', 0xB7E1),    new Mapping('룐', 0xB7E2),    new Mapping('룔', 0xB7E3), 
				new Mapping('룝', 0xB7E4),    new Mapping('룟', 0xB7E5),    new Mapping('룡', 0xB7E6),    new Mapping('루', 0xB7E7),    new Mapping('룩', 0xB7E8), 
				new Mapping('룬', 0xB7E9),    new Mapping('룰', 0xB7EA),    new Mapping('룸', 0xB7EB),    new Mapping('룹', 0xB7EC),    new Mapping('룻', 0xB7ED), 
				new Mapping('룽', 0xB7EE),    new Mapping('뤄', 0xB7EF),    new Mapping('뤘', 0xB7F0),    new Mapping('뤠', 0xB7F1),    new Mapping('뤼', 0xB7F2), 
				new Mapping('뤽', 0xB7F3),    new Mapping('륀', 0xB7F4),    new Mapping('륄', 0xB7F5),    new Mapping('륌', 0xB7F6),    new Mapping('륏', 0xB7F7), 
				new Mapping('륑', 0xB7F8),    new Mapping('류', 0xB7F9),    new Mapping('륙', 0xB7FA),    new Mapping('륜', 0xB7FB),    new Mapping('률', 0xB7FC), 
				new Mapping('륨', 0xB7FD),    new Mapping('륩', 0xB7FE),


			//},
			
			//{
				new Mapping((char)0, 0),
				new Mapping('륫', 0xB8A1),    new Mapping('륭', 0xB8A2),    new Mapping('르', 0xB8A3), 
				new Mapping('륵', 0xB8A4),    new Mapping('른', 0xB8A5),    new Mapping('를', 0xB8A6),    new Mapping('름', 0xB8A7),    new Mapping('릅', 0xB8A8), 
				new Mapping('릇', 0xB8A9),    new Mapping('릉', 0xB8AA),    new Mapping('릊', 0xB8AB),    new Mapping('릍', 0xB8AC),    new Mapping('릎', 0xB8AD), 
				new Mapping('리', 0xB8AE),    new Mapping('릭', 0xB8AF),    new Mapping('린', 0xB8B0),    new Mapping('릴', 0xB8B1),    new Mapping('림', 0xB8B2), 
				new Mapping('립', 0xB8B3),    new Mapping('릿', 0xB8B4),    new Mapping('링', 0xB8B5),    new Mapping('마', 0xB8B6),    new Mapping('막', 0xB8B7), 
				new Mapping('만', 0xB8B8),    new Mapping('많', 0xB8B9),    new Mapping('맏', 0xB8BA),    new Mapping('말', 0xB8BB),    new Mapping('맑', 0xB8BC), 
				new Mapping('맒', 0xB8BD),    new Mapping('맘', 0xB8BE),    new Mapping('맙', 0xB8BF),    new Mapping('맛', 0xB8C0),    new Mapping('망', 0xB8C1), 
				new Mapping('맞', 0xB8C2),    new Mapping('맡', 0xB8C3),    new Mapping('맣', 0xB8C4),    new Mapping('매', 0xB8C5),    new Mapping('맥', 0xB8C6), 
				new Mapping('맨', 0xB8C7),    new Mapping('맬', 0xB8C8),    new Mapping('맴', 0xB8C9),    new Mapping('맵', 0xB8CA),    new Mapping('맷', 0xB8CB), 
				new Mapping('맸', 0xB8CC),    new Mapping('맹', 0xB8CD),    new Mapping('맺', 0xB8CE),    new Mapping('먀', 0xB8CF),    new Mapping('먁', 0xB8D0), 
				new Mapping('먈', 0xB8D1),    new Mapping('먕', 0xB8D2),    new Mapping('머', 0xB8D3),    new Mapping('먹', 0xB8D4),    new Mapping('먼', 0xB8D5), 
				new Mapping('멀', 0xB8D6),    new Mapping('멂', 0xB8D7),    new Mapping('멈', 0xB8D8),    new Mapping('멉', 0xB8D9),    new Mapping('멋', 0xB8DA), 
				new Mapping('멍', 0xB8DB),    new Mapping('멎', 0xB8DC),    new Mapping('멓', 0xB8DD),    new Mapping('메', 0xB8DE),    new Mapping('멕', 0xB8DF), 
				new Mapping('멘', 0xB8E0),    new Mapping('멜', 0xB8E1),    new Mapping('멤', 0xB8E2),    new Mapping('멥', 0xB8E3),    new Mapping('멧', 0xB8E4), 
				new Mapping('멨', 0xB8E5),    new Mapping('멩', 0xB8E6),    new Mapping('며', 0xB8E7),    new Mapping('멱', 0xB8E8),    new Mapping('면', 0xB8E9), 
				new Mapping('멸', 0xB8EA),    new Mapping('몃', 0xB8EB),    new Mapping('몄', 0xB8EC),    new Mapping('명', 0xB8ED),    new Mapping('몇', 0xB8EE), 
				new Mapping('몌', 0xB8EF),    new Mapping('모', 0xB8F0),    new Mapping('목', 0xB8F1),    new Mapping('몫', 0xB8F2),    new Mapping('몬', 0xB8F3), 
				new Mapping('몰', 0xB8F4),    new Mapping('몲', 0xB8F5),    new Mapping('몸', 0xB8F6),    new Mapping('몹', 0xB8F7),    new Mapping('못', 0xB8F8), 
				new Mapping('몽', 0xB8F9),    new Mapping('뫄', 0xB8FA),    new Mapping('뫈', 0xB8FB),    new Mapping('뫘', 0xB8FC),    new Mapping('뫙', 0xB8FD), 
				new Mapping('뫼', 0xB8FE), 

			//},
			
			//{
				new Mapping((char)0, 0),
				new Mapping('묀', 0xB9A1),    new Mapping('묄', 0xB9A2),    new Mapping('묍', 0xB9A3),    new Mapping('묏', 0xB9A4), 
				new Mapping('묑', 0xB9A5),    new Mapping('묘', 0xB9A6),    new Mapping('묜', 0xB9A7),    new Mapping('묠', 0xB9A8),    new Mapping('묩', 0xB9A9), 
				new Mapping('묫', 0xB9AA),    new Mapping('무', 0xB9AB),    new Mapping('묵', 0xB9AC),    new Mapping('묶', 0xB9AD),    new Mapping('문', 0xB9AE), 
				new Mapping('묻', 0xB9AF),    new Mapping('물', 0xB9B0),    new Mapping('묽', 0xB9B1),    new Mapping('묾', 0xB9B2),    new Mapping('뭄', 0xB9B3), 
				new Mapping('뭅', 0xB9B4),    new Mapping('뭇', 0xB9B5),    new Mapping('뭉', 0xB9B6),    new Mapping('뭍', 0xB9B7),    new Mapping('뭏', 0xB9B8), 
				new Mapping('뭐', 0xB9B9),    new Mapping('뭔', 0xB9BA),    new Mapping('뭘', 0xB9BB),    new Mapping('뭡', 0xB9BC),    new Mapping('뭣', 0xB9BD), 
				new Mapping('뭬', 0xB9BE),    new Mapping('뮈', 0xB9BF),    new Mapping('뮌', 0xB9C0),    new Mapping('뮐', 0xB9C1),    new Mapping('뮤', 0xB9C2), 
				new Mapping('뮨', 0xB9C3),    new Mapping('뮬', 0xB9C4),    new Mapping('뮴', 0xB9C5),    new Mapping('뮷', 0xB9C6),    new Mapping('므', 0xB9C7), 
				new Mapping('믄', 0xB9C8),    new Mapping('믈', 0xB9C9),    new Mapping('믐', 0xB9CA),    new Mapping('믓', 0xB9CB),    new Mapping('미', 0xB9CC), 
				new Mapping('믹', 0xB9CD),    new Mapping('민', 0xB9CE),    new Mapping('믿', 0xB9CF),    new Mapping('밀', 0xB9D0),    new Mapping('밂', 0xB9D1), 
				new Mapping('밈', 0xB9D2),    new Mapping('밉', 0xB9D3),    new Mapping('밋', 0xB9D4),    new Mapping('밌', 0xB9D5),    new Mapping('밍', 0xB9D6), 
				new Mapping('및', 0xB9D7),    new Mapping('밑', 0xB9D8),    new Mapping('바', 0xB9D9),    new Mapping('박', 0xB9DA),    new Mapping('밖', 0xB9DB), 
				new Mapping('밗', 0xB9DC),    new Mapping('반', 0xB9DD),    new Mapping('받', 0xB9DE),    new Mapping('발', 0xB9DF),    new Mapping('밝', 0xB9E0), 
				new Mapping('밞', 0xB9E1),    new Mapping('밟', 0xB9E2),    new Mapping('밤', 0xB9E3),    new Mapping('밥', 0xB9E4),    new Mapping('밧', 0xB9E5), 
				new Mapping('방', 0xB9E6),    new Mapping('밭', 0xB9E7),    new Mapping('배', 0xB9E8),    new Mapping('백', 0xB9E9),    new Mapping('밴', 0xB9EA), 
				new Mapping('밸', 0xB9EB),    new Mapping('뱀', 0xB9EC),    new Mapping('뱁', 0xB9ED),    new Mapping('뱃', 0xB9EE),    new Mapping('뱄', 0xB9EF), 
				new Mapping('뱅', 0xB9F0),    new Mapping('뱉', 0xB9F1),    new Mapping('뱌', 0xB9F2),    new Mapping('뱍', 0xB9F3),    new Mapping('뱐', 0xB9F4), 
				new Mapping('뱝', 0xB9F5),    new Mapping('버', 0xB9F6),    new Mapping('벅', 0xB9F7),    new Mapping('번', 0xB9F8),    new Mapping('벋', 0xB9F9), 
				new Mapping('벌', 0xB9FA),    new Mapping('벎', 0xB9FB),    new Mapping('범', 0xB9FC),    new Mapping('법', 0xB9FD),    new Mapping('벗', 0xB9FE),


			//},
			
			//{
				new Mapping((char)0, 0),
				new Mapping('벙', 0xBAA1),    new Mapping('벚', 0xBAA2),    new Mapping('베', 0xBAA3),    new Mapping('벡', 0xBAA4),    new Mapping('벤', 0xBAA5), 
				new Mapping('벧', 0xBAA6),    new Mapping('벨', 0xBAA7),    new Mapping('벰', 0xBAA8),    new Mapping('벱', 0xBAA9),    new Mapping('벳', 0xBAAA), 
				new Mapping('벴', 0xBAAB),    new Mapping('벵', 0xBAAC),    new Mapping('벼', 0xBAAD),    new Mapping('벽', 0xBAAE),    new Mapping('변', 0xBAAF), 
				new Mapping('별', 0xBAB0),    new Mapping('볍', 0xBAB1),    new Mapping('볏', 0xBAB2),    new Mapping('볐', 0xBAB3),    new Mapping('병', 0xBAB4), 
				new Mapping('볕', 0xBAB5),    new Mapping('볘', 0xBAB6),    new Mapping('볜', 0xBAB7),    new Mapping('보', 0xBAB8),    new Mapping('복', 0xBAB9), 
				new Mapping('볶', 0xBABA),    new Mapping('본', 0xBABB),    new Mapping('볼', 0xBABC),    new Mapping('봄', 0xBABD),    new Mapping('봅', 0xBABE), 
				new Mapping('봇', 0xBABF),    new Mapping('봉', 0xBAC0),    new Mapping('봐', 0xBAC1),    new Mapping('봔', 0xBAC2),    new Mapping('봤', 0xBAC3), 
				new Mapping('봬', 0xBAC4),    new Mapping('뵀', 0xBAC5),    new Mapping('뵈', 0xBAC6),    new Mapping('뵉', 0xBAC7),    new Mapping('뵌', 0xBAC8), 
				new Mapping('뵐', 0xBAC9),    new Mapping('뵘', 0xBACA),    new Mapping('뵙', 0xBACB),    new Mapping('뵤', 0xBACC),    new Mapping('뵨', 0xBACD), 
				new Mapping('부', 0xBACE),    new Mapping('북', 0xBACF),    new Mapping('분', 0xBAD0),    new Mapping('붇', 0xBAD1),    new Mapping('불', 0xBAD2), 
				new Mapping('붉', 0xBAD3),    new Mapping('붊', 0xBAD4),    new Mapping('붐', 0xBAD5),    new Mapping('붑', 0xBAD6),    new Mapping('붓', 0xBAD7), 
				new Mapping('붕', 0xBAD8),    new Mapping('붙', 0xBAD9),    new Mapping('붚', 0xBADA),    new Mapping('붜', 0xBADB),    new Mapping('붤', 0xBADC), 
				new Mapping('붰', 0xBADD),    new Mapping('붸', 0xBADE),    new Mapping('뷔', 0xBADF),    new Mapping('뷕', 0xBAE0),    new Mapping('뷘', 0xBAE1), 
				new Mapping('뷜', 0xBAE2),    new Mapping('뷩', 0xBAE3),    new Mapping('뷰', 0xBAE4),    new Mapping('뷴', 0xBAE5),    new Mapping('뷸', 0xBAE6), 
				new Mapping('븀', 0xBAE7),    new Mapping('븃', 0xBAE8),    new Mapping('븅', 0xBAE9),    new Mapping('브', 0xBAEA),    new Mapping('븍', 0xBAEB), 
				new Mapping('븐', 0xBAEC),    new Mapping('블', 0xBAED),    new Mapping('븜', 0xBAEE),    new Mapping('븝', 0xBAEF),    new Mapping('븟', 0xBAF0), 
				new Mapping('비', 0xBAF1),    new Mapping('빅', 0xBAF2),    new Mapping('빈', 0xBAF3),    new Mapping('빌', 0xBAF4),    new Mapping('빎', 0xBAF5), 
				new Mapping('빔', 0xBAF6),    new Mapping('빕', 0xBAF7),    new Mapping('빗', 0xBAF8),    new Mapping('빙', 0xBAF9),    new Mapping('빚', 0xBAFA), 
				new Mapping('빛', 0xBAFB),    new Mapping('빠', 0xBAFC),    new Mapping('빡', 0xBAFD),    new Mapping('빤', 0xBAFE),   



			//},
			
			
			//{
				new Mapping((char)0, 0),
				new Mapping('빨', 0xBBA1), 
				new Mapping('빪', 0xBBA2),    new Mapping('빰', 0xBBA3),    new Mapping('빱', 0xBBA4),    new Mapping('빳', 0xBBA5),    new Mapping('빴', 0xBBA6), 
				new Mapping('빵', 0xBBA7),    new Mapping('빻', 0xBBA8),    new Mapping('빼', 0xBBA9),    new Mapping('빽', 0xBBAA),    new Mapping('뺀', 0xBBAB), 
				new Mapping('뺄', 0xBBAC),    new Mapping('뺌', 0xBBAD),    new Mapping('뺍', 0xBBAE),    new Mapping('뺏', 0xBBAF),    new Mapping('뺐', 0xBBB0), 
				new Mapping('뺑', 0xBBB1),    new Mapping('뺘', 0xBBB2),    new Mapping('뺙', 0xBBB3),    new Mapping('뺨', 0xBBB4),    new Mapping('뻐', 0xBBB5), 
				new Mapping('뻑', 0xBBB6),    new Mapping('뻔', 0xBBB7),    new Mapping('뻗', 0xBBB8),    new Mapping('뻘', 0xBBB9),    new Mapping('뻠', 0xBBBA), 
				new Mapping('뻣', 0xBBBB),    new Mapping('뻤', 0xBBBC),    new Mapping('뻥', 0xBBBD),    new Mapping('뻬', 0xBBBE),    new Mapping('뼁', 0xBBBF), 
				new Mapping('뼈', 0xBBC0),    new Mapping('뼉', 0xBBC1),    new Mapping('뼘', 0xBBC2),    new Mapping('뼙', 0xBBC3),    new Mapping('뼛', 0xBBC4), 
				new Mapping('뼜', 0xBBC5),    new Mapping('뼝', 0xBBC6),    new Mapping('뽀', 0xBBC7),    new Mapping('뽁', 0xBBC8),    new Mapping('뽄', 0xBBC9), 
				new Mapping('뽈', 0xBBCA),    new Mapping('뽐', 0xBBCB),    new Mapping('뽑', 0xBBCC),    new Mapping('뽕', 0xBBCD),    new Mapping('뾔', 0xBBCE), 
				new Mapping('뾰', 0xBBCF),    new Mapping('뿅', 0xBBD0),    new Mapping('뿌', 0xBBD1),    new Mapping('뿍', 0xBBD2),    new Mapping('뿐', 0xBBD3), 
				new Mapping('뿔', 0xBBD4),    new Mapping('뿜', 0xBBD5),    new Mapping('뿟', 0xBBD6),    new Mapping('뿡', 0xBBD7),    new Mapping('쀼', 0xBBD8), 
				new Mapping('쁑', 0xBBD9),    new Mapping('쁘', 0xBBDA),    new Mapping('쁜', 0xBBDB),    new Mapping('쁠', 0xBBDC),    new Mapping('쁨', 0xBBDD), 
				new Mapping('쁩', 0xBBDE),    new Mapping('삐', 0xBBDF),    new Mapping('삑', 0xBBE0),    new Mapping('삔', 0xBBE1),    new Mapping('삘', 0xBBE2), 
				new Mapping('삠', 0xBBE3),    new Mapping('삡', 0xBBE4),    new Mapping('삣', 0xBBE5),    new Mapping('삥', 0xBBE6),    new Mapping('사', 0xBBE7), 
				new Mapping('삭', 0xBBE8),    new Mapping('삯', 0xBBE9),    new Mapping('산', 0xBBEA),    new Mapping('삳', 0xBBEB),    new Mapping('살', 0xBBEC), 
				new Mapping('삵', 0xBBED),    new Mapping('삶', 0xBBEE),    new Mapping('삼', 0xBBEF),    new Mapping('삽', 0xBBF0),    new Mapping('삿', 0xBBF1), 
				new Mapping('샀', 0xBBF2),    new Mapping('상', 0xBBF3),    new Mapping('샅', 0xBBF4),    new Mapping('새', 0xBBF5),    new Mapping('색', 0xBBF6), 
				new Mapping('샌', 0xBBF7),    new Mapping('샐', 0xBBF8),    new Mapping('샘', 0xBBF9),    new Mapping('샙', 0xBBFA),    new Mapping('샛', 0xBBFB), 
				new Mapping('샜', 0xBBFC),    new Mapping('생', 0xBBFD),    new Mapping('샤', 0xBBFE),   


			//},
			
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('샥', 0xBCA1),    new Mapping('샨', 0xBCA2), 
				new Mapping('샬', 0xBCA3),    new Mapping('샴', 0xBCA4),    new Mapping('샵', 0xBCA5),    new Mapping('샷', 0xBCA6),    new Mapping('샹', 0xBCA7), 
				new Mapping('섀', 0xBCA8),    new Mapping('섄', 0xBCA9),    new Mapping('섈', 0xBCAA),    new Mapping('섐', 0xBCAB),    new Mapping('섕', 0xBCAC), 
				new Mapping('서', 0xBCAD),    new Mapping('석', 0xBCAE),    new Mapping('섞', 0xBCAF),    new Mapping('섟', 0xBCB0),    new Mapping('선', 0xBCB1), 
				new Mapping('섣', 0xBCB2),    new Mapping('설', 0xBCB3),    new Mapping('섦', 0xBCB4),    new Mapping('섧', 0xBCB5),    new Mapping('섬', 0xBCB6), 
				new Mapping('섭', 0xBCB7),    new Mapping('섯', 0xBCB8),    new Mapping('섰', 0xBCB9),    new Mapping('성', 0xBCBA),    new Mapping('섶', 0xBCBB), 
				new Mapping('세', 0xBCBC),    new Mapping('섹', 0xBCBD),    new Mapping('센', 0xBCBE),    new Mapping('셀', 0xBCBF),    new Mapping('셈', 0xBCC0), 
				new Mapping('셉', 0xBCC1),    new Mapping('셋', 0xBCC2),    new Mapping('셌', 0xBCC3),    new Mapping('셍', 0xBCC4),    new Mapping('셔', 0xBCC5), 
				new Mapping('셕', 0xBCC6),    new Mapping('션', 0xBCC7),    new Mapping('셜', 0xBCC8),    new Mapping('셤', 0xBCC9),    new Mapping('셥', 0xBCCA), 
				new Mapping('셧', 0xBCCB),    new Mapping('셨', 0xBCCC),    new Mapping('셩', 0xBCCD),    new Mapping('셰', 0xBCCE),    new Mapping('셴', 0xBCCF), 
				new Mapping('셸', 0xBCD0),    new Mapping('솅', 0xBCD1),    new Mapping('소', 0xBCD2),    new Mapping('속', 0xBCD3),    new Mapping('솎', 0xBCD4), 
				new Mapping('손', 0xBCD5),    new Mapping('솔', 0xBCD6),    new Mapping('솖', 0xBCD7),    new Mapping('솜', 0xBCD8),    new Mapping('솝', 0xBCD9), 
				new Mapping('솟', 0xBCDA),    new Mapping('송', 0xBCDB),    new Mapping('솥', 0xBCDC),    new Mapping('솨', 0xBCDD),    new Mapping('솩', 0xBCDE), 
				new Mapping('솬', 0xBCDF),    new Mapping('솰', 0xBCE0),    new Mapping('솽', 0xBCE1),    new Mapping('쇄', 0xBCE2),    new Mapping('쇈', 0xBCE3), 
				new Mapping('쇌', 0xBCE4),    new Mapping('쇔', 0xBCE5),    new Mapping('쇗', 0xBCE6),    new Mapping('쇘', 0xBCE7),    new Mapping('쇠', 0xBCE8), 
				new Mapping('쇤', 0xBCE9),    new Mapping('쇨', 0xBCEA),    new Mapping('쇰', 0xBCEB),    new Mapping('쇱', 0xBCEC),    new Mapping('쇳', 0xBCED), 
				new Mapping('쇼', 0xBCEE),    new Mapping('쇽', 0xBCEF),    new Mapping('숀', 0xBCF0),    new Mapping('숄', 0xBCF1),    new Mapping('숌', 0xBCF2), 
				new Mapping('숍', 0xBCF3),    new Mapping('숏', 0xBCF4),    new Mapping('숑', 0xBCF5),    new Mapping('수', 0xBCF6),    new Mapping('숙', 0xBCF7), 
				new Mapping('순', 0xBCF8),    new Mapping('숟', 0xBCF9),    new Mapping('술', 0xBCFA),    new Mapping('숨', 0xBCFB),    new Mapping('숩', 0xBCFC), 
				new Mapping('숫', 0xBCFD),    new Mapping('숭', 0xBCFE),


			//},
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('숯', 0xBDA1),    new Mapping('숱', 0xBDA2),    new Mapping('숲', 0xBDA3), 
				new Mapping('숴', 0xBDA4),    new Mapping('쉈', 0xBDA5),    new Mapping('쉐', 0xBDA6),    new Mapping('쉑', 0xBDA7),    new Mapping('쉔', 0xBDA8), 
				new Mapping('쉘', 0xBDA9),    new Mapping('쉠', 0xBDAA),    new Mapping('쉥', 0xBDAB),    new Mapping('쉬', 0xBDAC),    new Mapping('쉭', 0xBDAD), 
				new Mapping('쉰', 0xBDAE),    new Mapping('쉴', 0xBDAF),    new Mapping('쉼', 0xBDB0),    new Mapping('쉽', 0xBDB1),    new Mapping('쉿', 0xBDB2), 
				new Mapping('슁', 0xBDB3),    new Mapping('슈', 0xBDB4),    new Mapping('슉', 0xBDB5),    new Mapping('슐', 0xBDB6),    new Mapping('슘', 0xBDB7), 
				new Mapping('슛', 0xBDB8),    new Mapping('슝', 0xBDB9),    new Mapping('스', 0xBDBA),    new Mapping('슥', 0xBDBB),    new Mapping('슨', 0xBDBC), 
				new Mapping('슬', 0xBDBD),    new Mapping('슭', 0xBDBE),    new Mapping('슴', 0xBDBF),    new Mapping('습', 0xBDC0),    new Mapping('슷', 0xBDC1), 
				new Mapping('승', 0xBDC2),    new Mapping('시', 0xBDC3),    new Mapping('식', 0xBDC4),    new Mapping('신', 0xBDC5),    new Mapping('싣', 0xBDC6), 
				new Mapping('실', 0xBDC7),    new Mapping('싫', 0xBDC8),    new Mapping('심', 0xBDC9),    new Mapping('십', 0xBDCA),    new Mapping('싯', 0xBDCB), 
				new Mapping('싱', 0xBDCC),    new Mapping('싶', 0xBDCD),    new Mapping('싸', 0xBDCE),    new Mapping('싹', 0xBDCF),    new Mapping('싻', 0xBDD0), 
				new Mapping('싼', 0xBDD1),    new Mapping('쌀', 0xBDD2),    new Mapping('쌈', 0xBDD3),    new Mapping('쌉', 0xBDD4),    new Mapping('쌌', 0xBDD5), 
				new Mapping('쌍', 0xBDD6),    new Mapping('쌓', 0xBDD7),    new Mapping('쌔', 0xBDD8),    new Mapping('쌕', 0xBDD9),    new Mapping('쌘', 0xBDDA), 
				new Mapping('쌜', 0xBDDB),    new Mapping('쌤', 0xBDDC),    new Mapping('쌥', 0xBDDD),    new Mapping('쌨', 0xBDDE),    new Mapping('쌩', 0xBDDF), 
				new Mapping('썅', 0xBDE0),    new Mapping('써', 0xBDE1),    new Mapping('썩', 0xBDE2),    new Mapping('썬', 0xBDE3),    new Mapping('썰', 0xBDE4), 
				new Mapping('썲', 0xBDE5),    new Mapping('썸', 0xBDE6),    new Mapping('썹', 0xBDE7),    new Mapping('썼', 0xBDE8),    new Mapping('썽', 0xBDE9), 
				new Mapping('쎄', 0xBDEA),    new Mapping('쎈', 0xBDEB),    new Mapping('쎌', 0xBDEC),    new Mapping('쏀', 0xBDED),    new Mapping('쏘', 0xBDEE), 
				new Mapping('쏙', 0xBDEF),    new Mapping('쏜', 0xBDF0),    new Mapping('쏟', 0xBDF1),    new Mapping('쏠', 0xBDF2),    new Mapping('쏢', 0xBDF3), 
				new Mapping('쏨', 0xBDF4),    new Mapping('쏩', 0xBDF5),    new Mapping('쏭', 0xBDF6),    new Mapping('쏴', 0xBDF7),    new Mapping('쏵', 0xBDF8), 
				new Mapping('쏸', 0xBDF9),    new Mapping('쐈', 0xBDFA),    new Mapping('쐐', 0xBDFB),    new Mapping('쐤', 0xBDFC),    new Mapping('쐬', 0xBDFD), 
				new Mapping('쐰', 0xBDFE),


			//},
			
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('쐴', 0xBEA1),    new Mapping('쐼', 0xBEA2),    new Mapping('쐽', 0xBEA3),    new Mapping('쑈', 0xBEA4), 
				new Mapping('쑤', 0xBEA5),    new Mapping('쑥', 0xBEA6),    new Mapping('쑨', 0xBEA7),    new Mapping('쑬', 0xBEA8),    new Mapping('쑴', 0xBEA9), 
				new Mapping('쑵', 0xBEAA),    new Mapping('쑹', 0xBEAB),    new Mapping('쒀', 0xBEAC),    new Mapping('쒔', 0xBEAD),    new Mapping('쒜', 0xBEAE), 
				new Mapping('쒸', 0xBEAF),    new Mapping('쒼', 0xBEB0),    new Mapping('쓩', 0xBEB1),    new Mapping('쓰', 0xBEB2),    new Mapping('쓱', 0xBEB3), 
				new Mapping('쓴', 0xBEB4),    new Mapping('쓸', 0xBEB5),    new Mapping('쓺', 0xBEB6),    new Mapping('쓿', 0xBEB7),    new Mapping('씀', 0xBEB8), 
				new Mapping('씁', 0xBEB9),    new Mapping('씌', 0xBEBA),    new Mapping('씐', 0xBEBB),    new Mapping('씔', 0xBEBC),    new Mapping('씜', 0xBEBD), 
				new Mapping('씨', 0xBEBE),    new Mapping('씩', 0xBEBF),    new Mapping('씬', 0xBEC0),    new Mapping('씰', 0xBEC1),    new Mapping('씸', 0xBEC2), 
				new Mapping('씹', 0xBEC3),    new Mapping('씻', 0xBEC4),    new Mapping('씽', 0xBEC5),    new Mapping('아', 0xBEC6),    new Mapping('악', 0xBEC7), 
				new Mapping('안', 0xBEC8),    new Mapping('앉', 0xBEC9),    new Mapping('않', 0xBECA),    new Mapping('알', 0xBECB),    new Mapping('앍', 0xBECC), 
				new Mapping('앎', 0xBECD),    new Mapping('앓', 0xBECE),    new Mapping('암', 0xBECF),    new Mapping('압', 0xBED0),    new Mapping('앗', 0xBED1), 
				new Mapping('았', 0xBED2),    new Mapping('앙', 0xBED3),    new Mapping('앝', 0xBED4),    new Mapping('앞', 0xBED5),    new Mapping('애', 0xBED6), 
				new Mapping('액', 0xBED7),    new Mapping('앤', 0xBED8),    new Mapping('앨', 0xBED9),    new Mapping('앰', 0xBEDA),    new Mapping('앱', 0xBEDB), 
				new Mapping('앳', 0xBEDC),    new Mapping('앴', 0xBEDD),    new Mapping('앵', 0xBEDE),    new Mapping('야', 0xBEDF),    new Mapping('약', 0xBEE0), 
				new Mapping('얀', 0xBEE1),    new Mapping('얄', 0xBEE2),    new Mapping('얇', 0xBEE3),    new Mapping('얌', 0xBEE4),    new Mapping('얍', 0xBEE5), 
				new Mapping('얏', 0xBEE6),    new Mapping('양', 0xBEE7),    new Mapping('얕', 0xBEE8),    new Mapping('얗', 0xBEE9),    new Mapping('얘', 0xBEEA), 
				new Mapping('얜', 0xBEEB),    new Mapping('얠', 0xBEEC),    new Mapping('얩', 0xBEED),    new Mapping('어', 0xBEEE),    new Mapping('억', 0xBEEF), 
				new Mapping('언', 0xBEF0),    new Mapping('얹', 0xBEF1),    new Mapping('얻', 0xBEF2),    new Mapping('얼', 0xBEF3),    new Mapping('얽', 0xBEF4), 
				new Mapping('얾', 0xBEF5),    new Mapping('엄', 0xBEF6),    new Mapping('업', 0xBEF7),    new Mapping('없', 0xBEF8),    new Mapping('엇', 0xBEF9), 
				new Mapping('었', 0xBEFA),    new Mapping('엉', 0xBEFB),    new Mapping('엊', 0xBEFC),    new Mapping('엌', 0xBEFD),    new Mapping('엎', 0xBEFE),


			//},
			
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('에', 0xBFA1),    new Mapping('엑', 0xBFA2),    new Mapping('엔', 0xBFA3),    new Mapping('엘', 0xBFA4),    new Mapping('엠', 0xBFA5), 
				new Mapping('엡', 0xBFA6),    new Mapping('엣', 0xBFA7),    new Mapping('엥', 0xBFA8),    new Mapping('여', 0xBFA9),    new Mapping('역', 0xBFAA), 
				new Mapping('엮', 0xBFAB),    new Mapping('연', 0xBFAC),    new Mapping('열', 0xBFAD),    new Mapping('엶', 0xBFAE),    new Mapping('엷', 0xBFAF), 
				new Mapping('염', 0xBFB0),    new Mapping('엽', 0xBFB1),    new Mapping('엾', 0xBFB2),    new Mapping('엿', 0xBFB3),    new Mapping('였', 0xBFB4), 
				new Mapping('영', 0xBFB5),    new Mapping('옅', 0xBFB6),    new Mapping('옆', 0xBFB7),    new Mapping('옇', 0xBFB8),    new Mapping('예', 0xBFB9), 
				new Mapping('옌', 0xBFBA),    new Mapping('옐', 0xBFBB),    new Mapping('옘', 0xBFBC),    new Mapping('옙', 0xBFBD),    new Mapping('옛', 0xBFBE), 
				new Mapping('옜', 0xBFBF),    new Mapping('오', 0xBFC0),    new Mapping('옥', 0xBFC1),    new Mapping('온', 0xBFC2),    new Mapping('올', 0xBFC3), 
				new Mapping('옭', 0xBFC4),    new Mapping('옮', 0xBFC5),    new Mapping('옰', 0xBFC6),    new Mapping('옳', 0xBFC7),    new Mapping('옴', 0xBFC8), 
				new Mapping('옵', 0xBFC9),    new Mapping('옷', 0xBFCA),    new Mapping('옹', 0xBFCB),    new Mapping('옻', 0xBFCC),    new Mapping('와', 0xBFCD), 
				new Mapping('왁', 0xBFCE),    new Mapping('완', 0xBFCF),    new Mapping('왈', 0xBFD0),    new Mapping('왐', 0xBFD1),    new Mapping('왑', 0xBFD2), 
				new Mapping('왓', 0xBFD3),    new Mapping('왔', 0xBFD4),    new Mapping('왕', 0xBFD5),    new Mapping('왜', 0xBFD6),    new Mapping('왝', 0xBFD7), 
				new Mapping('왠', 0xBFD8),    new Mapping('왬', 0xBFD9),    new Mapping('왯', 0xBFDA),    new Mapping('왱', 0xBFDB),    new Mapping('외', 0xBFDC), 
				new Mapping('왹', 0xBFDD),    new Mapping('왼', 0xBFDE),    new Mapping('욀', 0xBFDF),    new Mapping('욈', 0xBFE0),    new Mapping('욉', 0xBFE1), 
				new Mapping('욋', 0xBFE2),    new Mapping('욍', 0xBFE3),    new Mapping('요', 0xBFE4),    new Mapping('욕', 0xBFE5),    new Mapping('욘', 0xBFE6), 
				new Mapping('욜', 0xBFE7),    new Mapping('욤', 0xBFE8),    new Mapping('욥', 0xBFE9),    new Mapping('욧', 0xBFEA),    new Mapping('용', 0xBFEB), 
				new Mapping('우', 0xBFEC),    new Mapping('욱', 0xBFED),    new Mapping('운', 0xBFEE),    new Mapping('울', 0xBFEF),    new Mapping('욹', 0xBFF0), 
				new Mapping('욺', 0xBFF1),    new Mapping('움', 0xBFF2),    new Mapping('웁', 0xBFF3),    new Mapping('웃', 0xBFF4),    new Mapping('웅', 0xBFF5), 
				new Mapping('워', 0xBFF6),    new Mapping('웍', 0xBFF7),    new Mapping('원', 0xBFF8),    new Mapping('월', 0xBFF9),    new Mapping('웜', 0xBFFA), 
				new Mapping('웝', 0xBFFB),    new Mapping('웠', 0xBFFC),    new Mapping('웡', 0xBFFD),    new Mapping('웨', 0xBFFE),

				
			//},
			
			
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('웩', 0xC0A1), 
				new Mapping('웬', 0xC0A2),    new Mapping('웰', 0xC0A3),    new Mapping('웸', 0xC0A4),    new Mapping('웹', 0xC0A5),    new Mapping('웽', 0xC0A6), 
				new Mapping('위', 0xC0A7),    new Mapping('윅', 0xC0A8),    new Mapping('윈', 0xC0A9),    new Mapping('윌', 0xC0AA),    new Mapping('윔', 0xC0AB), 
				new Mapping('윕', 0xC0AC),    new Mapping('윗', 0xC0AD),    new Mapping('윙', 0xC0AE),    new Mapping('유', 0xC0AF),    new Mapping('육', 0xC0B0), 
				new Mapping('윤', 0xC0B1),    new Mapping('율', 0xC0B2),    new Mapping('윰', 0xC0B3),    new Mapping('윱', 0xC0B4),    new Mapping('윳', 0xC0B5), 
				new Mapping('융', 0xC0B6),    new Mapping('윷', 0xC0B7),    new Mapping('으', 0xC0B8),    new Mapping('윽', 0xC0B9),    new Mapping('은', 0xC0BA), 
				new Mapping('을', 0xC0BB),    new Mapping('읊', 0xC0BC),    new Mapping('음', 0xC0BD),    new Mapping('읍', 0xC0BE),    new Mapping('읏', 0xC0BF), 
				new Mapping('응', 0xC0C0),    new Mapping('읒', 0xC0C1),    new Mapping('읓', 0xC0C2),    new Mapping('읔', 0xC0C3),    new Mapping('읕', 0xC0C4), 
				new Mapping('읖', 0xC0C5),    new Mapping('읗', 0xC0C6),    new Mapping('의', 0xC0C7),    new Mapping('읜', 0xC0C8),    new Mapping('읠', 0xC0C9), 
				new Mapping('읨', 0xC0CA),    new Mapping('읫', 0xC0CB),    new Mapping('이', 0xC0CC),    new Mapping('익', 0xC0CD),    new Mapping('인', 0xC0CE), 
				new Mapping('일', 0xC0CF),    new Mapping('읽', 0xC0D0),    new Mapping('읾', 0xC0D1),    new Mapping('잃', 0xC0D2),    new Mapping('임', 0xC0D3), 
				new Mapping('입', 0xC0D4),    new Mapping('잇', 0xC0D5),    new Mapping('있', 0xC0D6),    new Mapping('잉', 0xC0D7),    new Mapping('잊', 0xC0D8), 
				new Mapping('잎', 0xC0D9),    new Mapping('자', 0xC0DA),    new Mapping('작', 0xC0DB),    new Mapping('잔', 0xC0DC),    new Mapping('잖', 0xC0DD), 
				new Mapping('잗', 0xC0DE),    new Mapping('잘', 0xC0DF),    new Mapping('잚', 0xC0E0),    new Mapping('잠', 0xC0E1),    new Mapping('잡', 0xC0E2), 
				new Mapping('잣', 0xC0E3),    new Mapping('잤', 0xC0E4),    new Mapping('장', 0xC0E5),    new Mapping('잦', 0xC0E6),    new Mapping('재', 0xC0E7), 
				new Mapping('잭', 0xC0E8),    new Mapping('잰', 0xC0E9),    new Mapping('잴', 0xC0EA),    new Mapping('잼', 0xC0EB),    new Mapping('잽', 0xC0EC), 
				new Mapping('잿', 0xC0ED),    new Mapping('쟀', 0xC0EE),    new Mapping('쟁', 0xC0EF),    new Mapping('쟈', 0xC0F0),    new Mapping('쟉', 0xC0F1), 
				new Mapping('쟌', 0xC0F2),    new Mapping('쟎', 0xC0F3),    new Mapping('쟐', 0xC0F4),    new Mapping('쟘', 0xC0F5),    new Mapping('쟝', 0xC0F6), 
				new Mapping('쟤', 0xC0F7),    new Mapping('쟨', 0xC0F8),    new Mapping('쟬', 0xC0F9),    new Mapping('저', 0xC0FA),    new Mapping('적', 0xC0FB), 
				new Mapping('전', 0xC0FC),    new Mapping('절', 0xC0FD),    new Mapping('젊', 0xC0FE),    

			//},
			
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('점', 0xC1A1),    new Mapping('접', 0xC1A2), 
				new Mapping('젓', 0xC1A3),    new Mapping('정', 0xC1A4),    new Mapping('젖', 0xC1A5),    new Mapping('제', 0xC1A6),    new Mapping('젝', 0xC1A7), 
				new Mapping('젠', 0xC1A8),    new Mapping('젤', 0xC1A9),    new Mapping('젬', 0xC1AA),    new Mapping('젭', 0xC1AB),    new Mapping('젯', 0xC1AC), 
				new Mapping('젱', 0xC1AD),    new Mapping('져', 0xC1AE),    new Mapping('젼', 0xC1AF),    new Mapping('졀', 0xC1B0),    new Mapping('졈', 0xC1B1), 
				new Mapping('졉', 0xC1B2),    new Mapping('졌', 0xC1B3),    new Mapping('졍', 0xC1B4),    new Mapping('졔', 0xC1B5),    new Mapping('조', 0xC1B6), 
				new Mapping('족', 0xC1B7),    new Mapping('존', 0xC1B8),    new Mapping('졸', 0xC1B9),    new Mapping('졺', 0xC1BA),    new Mapping('좀', 0xC1BB), 
				new Mapping('좁', 0xC1BC),    new Mapping('좃', 0xC1BD),    new Mapping('종', 0xC1BE),    new Mapping('좆', 0xC1BF),    new Mapping('좇', 0xC1C0), 
				new Mapping('좋', 0xC1C1),    new Mapping('좌', 0xC1C2),    new Mapping('좍', 0xC1C3),    new Mapping('좔', 0xC1C4),    new Mapping('좝', 0xC1C5), 
				new Mapping('좟', 0xC1C6),    new Mapping('좡', 0xC1C7),    new Mapping('좨', 0xC1C8),    new Mapping('좼', 0xC1C9),    new Mapping('좽', 0xC1CA), 
				new Mapping('죄', 0xC1CB),    new Mapping('죈', 0xC1CC),    new Mapping('죌', 0xC1CD),    new Mapping('죔', 0xC1CE),    new Mapping('죕', 0xC1CF), 
				new Mapping('죗', 0xC1D0),    new Mapping('죙', 0xC1D1),    new Mapping('죠', 0xC1D2),    new Mapping('죡', 0xC1D3),    new Mapping('죤', 0xC1D4), 
				new Mapping('죵', 0xC1D5),    new Mapping('주', 0xC1D6),    new Mapping('죽', 0xC1D7),    new Mapping('준', 0xC1D8),    new Mapping('줄', 0xC1D9), 
				new Mapping('줅', 0xC1DA),    new Mapping('줆', 0xC1DB),    new Mapping('줌', 0xC1DC),    new Mapping('줍', 0xC1DD),    new Mapping('줏', 0xC1DE), 
				new Mapping('중', 0xC1DF),    new Mapping('줘', 0xC1E0),    new Mapping('줬', 0xC1E1),    new Mapping('줴', 0xC1E2),    new Mapping('쥐', 0xC1E3), 
				new Mapping('쥑', 0xC1E4),    new Mapping('쥔', 0xC1E5),    new Mapping('쥘', 0xC1E6),    new Mapping('쥠', 0xC1E7),    new Mapping('쥡', 0xC1E8), 
				new Mapping('쥣', 0xC1E9),    new Mapping('쥬', 0xC1EA),    new Mapping('쥰', 0xC1EB),    new Mapping('쥴', 0xC1EC),    new Mapping('쥼', 0xC1ED), 
				new Mapping('즈', 0xC1EE),    new Mapping('즉', 0xC1EF),    new Mapping('즌', 0xC1F0),    new Mapping('즐', 0xC1F1),    new Mapping('즘', 0xC1F2), 
				new Mapping('즙', 0xC1F3),    new Mapping('즛', 0xC1F4),    new Mapping('증', 0xC1F5),    new Mapping('지', 0xC1F6),    new Mapping('직', 0xC1F7), 
				new Mapping('진', 0xC1F8),    new Mapping('짇', 0xC1F9),    new Mapping('질', 0xC1FA),    new Mapping('짊', 0xC1FB),    new Mapping('짐', 0xC1FC), 
				new Mapping('집', 0xC1FD),    new Mapping('짓', 0xC1FE),


			//},
			
			
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('징', 0xC2A1),    new Mapping('짖', 0xC2A2),    new Mapping('짙', 0xC2A3), 
				new Mapping('짚', 0xC2A4),    new Mapping('짜', 0xC2A5),    new Mapping('짝', 0xC2A6),    new Mapping('짠', 0xC2A7),    new Mapping('짢', 0xC2A8), 
				new Mapping('짤', 0xC2A9),    new Mapping('짧', 0xC2AA),    new Mapping('짬', 0xC2AB),    new Mapping('짭', 0xC2AC),    new Mapping('짯', 0xC2AD), 
				new Mapping('짰', 0xC2AE),    new Mapping('짱', 0xC2AF),    new Mapping('째', 0xC2B0),    new Mapping('짹', 0xC2B1),    new Mapping('짼', 0xC2B2), 
				new Mapping('쨀', 0xC2B3),    new Mapping('쨈', 0xC2B4),    new Mapping('쨉', 0xC2B5),    new Mapping('쨋', 0xC2B6),    new Mapping('쨌', 0xC2B7), 
				new Mapping('쨍', 0xC2B8),    new Mapping('쨔', 0xC2B9),    new Mapping('쨘', 0xC2BA),    new Mapping('쨩', 0xC2BB),    new Mapping('쩌', 0xC2BC), 
				new Mapping('쩍', 0xC2BD),    new Mapping('쩐', 0xC2BE),    new Mapping('쩔', 0xC2BF),    new Mapping('쩜', 0xC2C0),    new Mapping('쩝', 0xC2C1), 
				new Mapping('쩟', 0xC2C2),    new Mapping('쩠', 0xC2C3),    new Mapping('쩡', 0xC2C4),    new Mapping('쩨', 0xC2C5),    new Mapping('쩽', 0xC2C6), 
				new Mapping('쪄', 0xC2C7),    new Mapping('쪘', 0xC2C8),    new Mapping('쪼', 0xC2C9),    new Mapping('쪽', 0xC2CA),    new Mapping('쫀', 0xC2CB), 
				new Mapping('쫄', 0xC2CC),    new Mapping('쫌', 0xC2CD),    new Mapping('쫍', 0xC2CE),    new Mapping('쫏', 0xC2CF),    new Mapping('쫑', 0xC2D0), 
				new Mapping('쫓', 0xC2D1),    new Mapping('쫘', 0xC2D2),    new Mapping('쫙', 0xC2D3),    new Mapping('쫠', 0xC2D4),    new Mapping('쫬', 0xC2D5), 
				new Mapping('쫴', 0xC2D6),    new Mapping('쬈', 0xC2D7),    new Mapping('쬐', 0xC2D8),    new Mapping('쬔', 0xC2D9),    new Mapping('쬘', 0xC2DA), 
				new Mapping('쬠', 0xC2DB),    new Mapping('쬡', 0xC2DC),    new Mapping('쭁', 0xC2DD),    new Mapping('쭈', 0xC2DE),    new Mapping('쭉', 0xC2DF), 
				new Mapping('쭌', 0xC2E0),    new Mapping('쭐', 0xC2E1),    new Mapping('쭘', 0xC2E2),    new Mapping('쭙', 0xC2E3),    new Mapping('쭝', 0xC2E4), 
				new Mapping('쭤', 0xC2E5),    new Mapping('쭸', 0xC2E6),    new Mapping('쭹', 0xC2E7),    new Mapping('쮜', 0xC2E8),    new Mapping('쮸', 0xC2E9), 
				new Mapping('쯔', 0xC2EA),    new Mapping('쯤', 0xC2EB),    new Mapping('쯧', 0xC2EC),    new Mapping('쯩', 0xC2ED),    new Mapping('찌', 0xC2EE), 
				new Mapping('찍', 0xC2EF),    new Mapping('찐', 0xC2F0),    new Mapping('찔', 0xC2F1),    new Mapping('찜', 0xC2F2),    new Mapping('찝', 0xC2F3), 
				new Mapping('찡', 0xC2F4),    new Mapping('찢', 0xC2F5),    new Mapping('찧', 0xC2F6),    new Mapping('차', 0xC2F7),    new Mapping('착', 0xC2F8), 
				new Mapping('찬', 0xC2F9),    new Mapping('찮', 0xC2FA),    new Mapping('찰', 0xC2FB),    new Mapping('참', 0xC2FC),    new Mapping('찹', 0xC2FD), 
				new Mapping('찻', 0xC2FE),    

			//},
			
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('찼', 0xC3A1),    new Mapping('창', 0xC3A2),    new Mapping('찾', 0xC3A3),    new Mapping('채', 0xC3A4), 
				new Mapping('책', 0xC3A5),    new Mapping('챈', 0xC3A6),    new Mapping('챌', 0xC3A7),    new Mapping('챔', 0xC3A8),    new Mapping('챕', 0xC3A9), 
				new Mapping('챗', 0xC3AA),    new Mapping('챘', 0xC3AB),    new Mapping('챙', 0xC3AC),    new Mapping('챠', 0xC3AD),    new Mapping('챤', 0xC3AE), 
				new Mapping('챦', 0xC3AF),    new Mapping('챨', 0xC3B0),    new Mapping('챰', 0xC3B1),    new Mapping('챵', 0xC3B2),    new Mapping('처', 0xC3B3), 
				new Mapping('척', 0xC3B4),    new Mapping('천', 0xC3B5),    new Mapping('철', 0xC3B6),    new Mapping('첨', 0xC3B7),    new Mapping('첩', 0xC3B8), 
				new Mapping('첫', 0xC3B9),    new Mapping('첬', 0xC3BA),    new Mapping('청', 0xC3BB),    new Mapping('체', 0xC3BC),    new Mapping('첵', 0xC3BD), 
				new Mapping('첸', 0xC3BE),    new Mapping('첼', 0xC3BF),    new Mapping('쳄', 0xC3C0),    new Mapping('쳅', 0xC3C1),    new Mapping('쳇', 0xC3C2), 
				new Mapping('쳉', 0xC3C3),    new Mapping('쳐', 0xC3C4),    new Mapping('쳔', 0xC3C5),    new Mapping('쳤', 0xC3C6),    new Mapping('쳬', 0xC3C7), 
				new Mapping('쳰', 0xC3C8),    new Mapping('촁', 0xC3C9),    new Mapping('초', 0xC3CA),    new Mapping('촉', 0xC3CB),    new Mapping('촌', 0xC3CC), 
				new Mapping('촐', 0xC3CD),    new Mapping('촘', 0xC3CE),    new Mapping('촙', 0xC3CF),    new Mapping('촛', 0xC3D0),    new Mapping('총', 0xC3D1), 
				new Mapping('촤', 0xC3D2),    new Mapping('촨', 0xC3D3),    new Mapping('촬', 0xC3D4),    new Mapping('촹', 0xC3D5),    new Mapping('최', 0xC3D6), 
				new Mapping('쵠', 0xC3D7),    new Mapping('쵤', 0xC3D8),    new Mapping('쵬', 0xC3D9),    new Mapping('쵭', 0xC3DA),    new Mapping('쵯', 0xC3DB), 
				new Mapping('쵱', 0xC3DC),    new Mapping('쵸', 0xC3DD),    new Mapping('춈', 0xC3DE),    new Mapping('추', 0xC3DF),    new Mapping('축', 0xC3E0), 
				new Mapping('춘', 0xC3E1),    new Mapping('출', 0xC3E2),    new Mapping('춤', 0xC3E3),    new Mapping('춥', 0xC3E4),    new Mapping('춧', 0xC3E5), 
				new Mapping('충', 0xC3E6),    new Mapping('춰', 0xC3E7),    new Mapping('췄', 0xC3E8),    new Mapping('췌', 0xC3E9),    new Mapping('췐', 0xC3EA), 
				new Mapping('취', 0xC3EB),    new Mapping('췬', 0xC3EC),    new Mapping('췰', 0xC3ED),    new Mapping('췸', 0xC3EE),    new Mapping('췹', 0xC3EF), 
				new Mapping('췻', 0xC3F0),    new Mapping('췽', 0xC3F1),    new Mapping('츄', 0xC3F2),    new Mapping('츈', 0xC3F3),    new Mapping('츌', 0xC3F4), 
				new Mapping('츔', 0xC3F5),    new Mapping('츙', 0xC3F6),    new Mapping('츠', 0xC3F7),    new Mapping('측', 0xC3F8),    new Mapping('츤', 0xC3F9), 
				new Mapping('츨', 0xC3FA),    new Mapping('츰', 0xC3FB),    new Mapping('츱', 0xC3FC),    new Mapping('츳', 0xC3FD),    new Mapping('층', 0xC3FE), 

			//},
			
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('치', 0xC4A1),    new Mapping('칙', 0xC4A2),    new Mapping('친', 0xC4A3),    new Mapping('칟', 0xC4A4),    new Mapping('칠', 0xC4A5), 
				new Mapping('칡', 0xC4A6),    new Mapping('침', 0xC4A7),    new Mapping('칩', 0xC4A8),    new Mapping('칫', 0xC4A9),    new Mapping('칭', 0xC4AA), 
				new Mapping('카', 0xC4AB),    new Mapping('칵', 0xC4AC),    new Mapping('칸', 0xC4AD),    new Mapping('칼', 0xC4AE),    new Mapping('캄', 0xC4AF), 
				new Mapping('캅', 0xC4B0),    new Mapping('캇', 0xC4B1),    new Mapping('캉', 0xC4B2),    new Mapping('캐', 0xC4B3),    new Mapping('캑', 0xC4B4), 
				new Mapping('캔', 0xC4B5),    new Mapping('캘', 0xC4B6),    new Mapping('캠', 0xC4B7),    new Mapping('캡', 0xC4B8),    new Mapping('캣', 0xC4B9), 
				new Mapping('캤', 0xC4BA),    new Mapping('캥', 0xC4BB),    new Mapping('캬', 0xC4BC),    new Mapping('캭', 0xC4BD),    new Mapping('컁', 0xC4BE), 
				new Mapping('커', 0xC4BF),    new Mapping('컥', 0xC4C0),    new Mapping('컨', 0xC4C1),    new Mapping('컫', 0xC4C2),    new Mapping('컬', 0xC4C3), 
				new Mapping('컴', 0xC4C4),    new Mapping('컵', 0xC4C5),    new Mapping('컷', 0xC4C6),    new Mapping('컸', 0xC4C7),    new Mapping('컹', 0xC4C8), 
				new Mapping('케', 0xC4C9),    new Mapping('켁', 0xC4CA),    new Mapping('켄', 0xC4CB),    new Mapping('켈', 0xC4CC),    new Mapping('켐', 0xC4CD), 
				new Mapping('켑', 0xC4CE),    new Mapping('켓', 0xC4CF),    new Mapping('켕', 0xC4D0),    new Mapping('켜', 0xC4D1),    new Mapping('켠', 0xC4D2), 
				new Mapping('켤', 0xC4D3),    new Mapping('켬', 0xC4D4),    new Mapping('켭', 0xC4D5),    new Mapping('켯', 0xC4D6),    new Mapping('켰', 0xC4D7), 
				new Mapping('켱', 0xC4D8),    new Mapping('켸', 0xC4D9),    new Mapping('코', 0xC4DA),    new Mapping('콕', 0xC4DB),    new Mapping('콘', 0xC4DC), 
				new Mapping('콜', 0xC4DD),    new Mapping('콤', 0xC4DE),    new Mapping('콥', 0xC4DF),    new Mapping('콧', 0xC4E0),    new Mapping('콩', 0xC4E1), 
				new Mapping('콰', 0xC4E2),    new Mapping('콱', 0xC4E3),    new Mapping('콴', 0xC4E4),    new Mapping('콸', 0xC4E5),    new Mapping('쾀', 0xC4E6), 
				new Mapping('쾅', 0xC4E7),    new Mapping('쾌', 0xC4E8),    new Mapping('쾡', 0xC4E9),    new Mapping('쾨', 0xC4EA),    new Mapping('쾰', 0xC4EB), 
				new Mapping('쿄', 0xC4EC),    new Mapping('쿠', 0xC4ED),    new Mapping('쿡', 0xC4EE),    new Mapping('쿤', 0xC4EF),    new Mapping('쿨', 0xC4F0), 
				new Mapping('쿰', 0xC4F1),    new Mapping('쿱', 0xC4F2),    new Mapping('쿳', 0xC4F3),    new Mapping('쿵', 0xC4F4),    new Mapping('쿼', 0xC4F5), 
				new Mapping('퀀', 0xC4F6),    new Mapping('퀄', 0xC4F7),    new Mapping('퀑', 0xC4F8),    new Mapping('퀘', 0xC4F9),    new Mapping('퀭', 0xC4FA), 
				new Mapping('퀴', 0xC4FB),    new Mapping('퀵', 0xC4FC),    new Mapping('퀸', 0xC4FD),    new Mapping('퀼', 0xC4FE),   


			//},
			
			
			//{
				new Mapping((char)0, 0),
				new Mapping('큄', 0xC5A1), 
				new Mapping('큅', 0xC5A2),    new Mapping('큇', 0xC5A3),    new Mapping('큉', 0xC5A4),    new Mapping('큐', 0xC5A5),    new Mapping('큔', 0xC5A6), 
				new Mapping('큘', 0xC5A7),    new Mapping('큠', 0xC5A8),    new Mapping('크', 0xC5A9),    new Mapping('큭', 0xC5AA),    new Mapping('큰', 0xC5AB), 
				new Mapping('클', 0xC5AC),    new Mapping('큼', 0xC5AD),    new Mapping('큽', 0xC5AE),    new Mapping('킁', 0xC5AF),    new Mapping('키', 0xC5B0), 
				new Mapping('킥', 0xC5B1),    new Mapping('킨', 0xC5B2),    new Mapping('킬', 0xC5B3),    new Mapping('킴', 0xC5B4),    new Mapping('킵', 0xC5B5), 
				new Mapping('킷', 0xC5B6),    new Mapping('킹', 0xC5B7),    new Mapping('타', 0xC5B8),    new Mapping('탁', 0xC5B9),    new Mapping('탄', 0xC5BA), 
				new Mapping('탈', 0xC5BB),    new Mapping('탉', 0xC5BC),    new Mapping('탐', 0xC5BD),    new Mapping('탑', 0xC5BE),    new Mapping('탓', 0xC5BF), 
				new Mapping('탔', 0xC5C0),    new Mapping('탕', 0xC5C1),    new Mapping('태', 0xC5C2),    new Mapping('택', 0xC5C3),    new Mapping('탠', 0xC5C4), 
				new Mapping('탤', 0xC5C5),    new Mapping('탬', 0xC5C6),    new Mapping('탭', 0xC5C7),    new Mapping('탯', 0xC5C8),    new Mapping('탰', 0xC5C9), 
				new Mapping('탱', 0xC5CA),    new Mapping('탸', 0xC5CB),    new Mapping('턍', 0xC5CC),    new Mapping('터', 0xC5CD),    new Mapping('턱', 0xC5CE), 
				new Mapping('턴', 0xC5CF),    new Mapping('털', 0xC5D0),    new Mapping('턺', 0xC5D1),    new Mapping('텀', 0xC5D2),    new Mapping('텁', 0xC5D3), 
				new Mapping('텃', 0xC5D4),    new Mapping('텄', 0xC5D5),    new Mapping('텅', 0xC5D6),    new Mapping('테', 0xC5D7),    new Mapping('텍', 0xC5D8), 
				new Mapping('텐', 0xC5D9),    new Mapping('텔', 0xC5DA),    new Mapping('템', 0xC5DB),    new Mapping('텝', 0xC5DC),    new Mapping('텟', 0xC5DD), 
				new Mapping('텡', 0xC5DE),    new Mapping('텨', 0xC5DF),    new Mapping('텬', 0xC5E0),    new Mapping('텼', 0xC5E1),    new Mapping('톄', 0xC5E2), 
				new Mapping('톈', 0xC5E3),    new Mapping('토', 0xC5E4),    new Mapping('톡', 0xC5E5),    new Mapping('톤', 0xC5E6),    new Mapping('톨', 0xC5E7), 
				new Mapping('톰', 0xC5E8),    new Mapping('톱', 0xC5E9),    new Mapping('톳', 0xC5EA),    new Mapping('통', 0xC5EB),    new Mapping('톺', 0xC5EC), 
				new Mapping('톼', 0xC5ED),    new Mapping('퇀', 0xC5EE),    new Mapping('퇘', 0xC5EF),    new Mapping('퇴', 0xC5F0),    new Mapping('퇸', 0xC5F1), 
				new Mapping('툇', 0xC5F2),    new Mapping('툉', 0xC5F3),    new Mapping('툐', 0xC5F4),    new Mapping('투', 0xC5F5),    new Mapping('툭', 0xC5F6), 
				new Mapping('툰', 0xC5F7),    new Mapping('툴', 0xC5F8),    new Mapping('툼', 0xC5F9),    new Mapping('툽', 0xC5FA),    new Mapping('툿', 0xC5FB), 
				new Mapping('퉁', 0xC5FC),    new Mapping('퉈', 0xC5FD),    new Mapping('퉜', 0xC5FE),    

			//},
			
			
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('퉤', 0xC6A1),    new Mapping('튀', 0xC6A2), 
				new Mapping('튁', 0xC6A3),    new Mapping('튄', 0xC6A4),    new Mapping('튈', 0xC6A5),    new Mapping('튐', 0xC6A6),    new Mapping('튑', 0xC6A7), 
				new Mapping('튕', 0xC6A8),    new Mapping('튜', 0xC6A9),    new Mapping('튠', 0xC6AA),    new Mapping('튤', 0xC6AB),    new Mapping('튬', 0xC6AC), 
				new Mapping('튱', 0xC6AD),    new Mapping('트', 0xC6AE),    new Mapping('특', 0xC6AF),    new Mapping('튼', 0xC6B0),    new Mapping('튿', 0xC6B1), 
				new Mapping('틀', 0xC6B2),    new Mapping('틂', 0xC6B3),    new Mapping('틈', 0xC6B4),    new Mapping('틉', 0xC6B5),    new Mapping('틋', 0xC6B6), 
				new Mapping('틔', 0xC6B7),    new Mapping('틘', 0xC6B8),    new Mapping('틜', 0xC6B9),    new Mapping('틤', 0xC6BA),    new Mapping('틥', 0xC6BB), 
				new Mapping('티', 0xC6BC),    new Mapping('틱', 0xC6BD),    new Mapping('틴', 0xC6BE),    new Mapping('틸', 0xC6BF),    new Mapping('팀', 0xC6C0), 
				new Mapping('팁', 0xC6C1),    new Mapping('팃', 0xC6C2),    new Mapping('팅', 0xC6C3),    new Mapping('파', 0xC6C4),    new Mapping('팍', 0xC6C5), 
				new Mapping('팎', 0xC6C6),    new Mapping('판', 0xC6C7),    new Mapping('팔', 0xC6C8),    new Mapping('팖', 0xC6C9),    new Mapping('팜', 0xC6CA), 
				new Mapping('팝', 0xC6CB),    new Mapping('팟', 0xC6CC),    new Mapping('팠', 0xC6CD),    new Mapping('팡', 0xC6CE),    new Mapping('팥', 0xC6CF), 
				new Mapping('패', 0xC6D0),    new Mapping('팩', 0xC6D1),    new Mapping('팬', 0xC6D2),    new Mapping('팰', 0xC6D3),    new Mapping('팸', 0xC6D4), 
				new Mapping('팹', 0xC6D5),    new Mapping('팻', 0xC6D6),    new Mapping('팼', 0xC6D7),    new Mapping('팽', 0xC6D8),    new Mapping('퍄', 0xC6D9), 
				new Mapping('퍅', 0xC6DA),    new Mapping('퍼', 0xC6DB),    new Mapping('퍽', 0xC6DC),    new Mapping('펀', 0xC6DD),    new Mapping('펄', 0xC6DE), 
				new Mapping('펌', 0xC6DF),    new Mapping('펍', 0xC6E0),    new Mapping('펏', 0xC6E1),    new Mapping('펐', 0xC6E2),    new Mapping('펑', 0xC6E3), 
				new Mapping('페', 0xC6E4),    new Mapping('펙', 0xC6E5),    new Mapping('펜', 0xC6E6),    new Mapping('펠', 0xC6E7),    new Mapping('펨', 0xC6E8), 
				new Mapping('펩', 0xC6E9),    new Mapping('펫', 0xC6EA),    new Mapping('펭', 0xC6EB),    new Mapping('펴', 0xC6EC),    new Mapping('편', 0xC6ED), 
				new Mapping('펼', 0xC6EE),    new Mapping('폄', 0xC6EF),    new Mapping('폅', 0xC6F0),    new Mapping('폈', 0xC6F1),    new Mapping('평', 0xC6F2), 
				new Mapping('폐', 0xC6F3),    new Mapping('폘', 0xC6F4),    new Mapping('폡', 0xC6F5),    new Mapping('폣', 0xC6F6),    new Mapping('포', 0xC6F7), 
				new Mapping('폭', 0xC6F8),    new Mapping('폰', 0xC6F9),    new Mapping('폴', 0xC6FA),    new Mapping('폼', 0xC6FB),    new Mapping('폽', 0xC6FC), 
				new Mapping('폿', 0xC6FD),    new Mapping('퐁', 0xC6FE),


			//},
			
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('퐈', 0xC7A1),    new Mapping('퐝', 0xC7A2),    new Mapping('푀', 0xC7A3), 
				new Mapping('푄', 0xC7A4),    new Mapping('표', 0xC7A5),    new Mapping('푠', 0xC7A6),    new Mapping('푤', 0xC7A7),    new Mapping('푭', 0xC7A8), 
				new Mapping('푯', 0xC7A9),    new Mapping('푸', 0xC7AA),    new Mapping('푹', 0xC7AB),    new Mapping('푼', 0xC7AC),    new Mapping('푿', 0xC7AD), 
				new Mapping('풀', 0xC7AE),    new Mapping('풂', 0xC7AF),    new Mapping('품', 0xC7B0),    new Mapping('풉', 0xC7B1),    new Mapping('풋', 0xC7B2), 
				new Mapping('풍', 0xC7B3),    new Mapping('풔', 0xC7B4),    new Mapping('풩', 0xC7B5),    new Mapping('퓌', 0xC7B6),    new Mapping('퓐', 0xC7B7), 
				new Mapping('퓔', 0xC7B8),    new Mapping('퓜', 0xC7B9),    new Mapping('퓟', 0xC7BA),    new Mapping('퓨', 0xC7BB),    new Mapping('퓬', 0xC7BC), 
				new Mapping('퓰', 0xC7BD),    new Mapping('퓸', 0xC7BE),    new Mapping('퓻', 0xC7BF),    new Mapping('퓽', 0xC7C0),    new Mapping('프', 0xC7C1), 
				new Mapping('픈', 0xC7C2),    new Mapping('플', 0xC7C3),    new Mapping('픔', 0xC7C4),    new Mapping('픕', 0xC7C5),    new Mapping('픗', 0xC7C6), 
				new Mapping('피', 0xC7C7),    new Mapping('픽', 0xC7C8),    new Mapping('핀', 0xC7C9),    new Mapping('필', 0xC7CA),    new Mapping('핌', 0xC7CB), 
				new Mapping('핍', 0xC7CC),    new Mapping('핏', 0xC7CD),    new Mapping('핑', 0xC7CE),    new Mapping('하', 0xC7CF),    new Mapping('학', 0xC7D0), 
				new Mapping('한', 0xC7D1),    new Mapping('할', 0xC7D2),    new Mapping('핥', 0xC7D3),    new Mapping('함', 0xC7D4),    new Mapping('합', 0xC7D5), 
				new Mapping('핫', 0xC7D6),    new Mapping('항', 0xC7D7),    new Mapping('해', 0xC7D8),    new Mapping('핵', 0xC7D9),    new Mapping('핸', 0xC7DA), 
				new Mapping('핼', 0xC7DB),    new Mapping('햄', 0xC7DC),    new Mapping('햅', 0xC7DD),    new Mapping('햇', 0xC7DE),    new Mapping('했', 0xC7DF), 
				new Mapping('행', 0xC7E0),    new Mapping('햐', 0xC7E1),    new Mapping('향', 0xC7E2),    new Mapping('허', 0xC7E3),    new Mapping('헉', 0xC7E4), 
				new Mapping('헌', 0xC7E5),    new Mapping('헐', 0xC7E6),    new Mapping('헒', 0xC7E7),    new Mapping('험', 0xC7E8),    new Mapping('헙', 0xC7E9), 
				new Mapping('헛', 0xC7EA),    new Mapping('헝', 0xC7EB),    new Mapping('헤', 0xC7EC),    new Mapping('헥', 0xC7ED),    new Mapping('헨', 0xC7EE), 
				new Mapping('헬', 0xC7EF),    new Mapping('헴', 0xC7F0),    new Mapping('헵', 0xC7F1),    new Mapping('헷', 0xC7F2),    new Mapping('헹', 0xC7F3), 
				new Mapping('혀', 0xC7F4),    new Mapping('혁', 0xC7F5),    new Mapping('현', 0xC7F6),    new Mapping('혈', 0xC7F7),    new Mapping('혐', 0xC7F8), 
				new Mapping('협', 0xC7F9),    new Mapping('혓', 0xC7FA),    new Mapping('혔', 0xC7FB),    new Mapping('형', 0xC7FC),    new Mapping('혜', 0xC7FD), 
				new Mapping('혠', 0xC7FE),   

			//},
			
			
			
			//{	
					new Mapping((char)0, 0),
				new Mapping('혤', 0xC8A1),    new Mapping('혭', 0xC8A2),    new Mapping('호', 0xC8A3),    new Mapping('혹', 0xC8A4), 
				new Mapping('혼', 0xC8A5),    new Mapping('홀', 0xC8A6),    new Mapping('홅', 0xC8A7),    new Mapping('홈', 0xC8A8),    new Mapping('홉', 0xC8A9), 
				new Mapping('홋', 0xC8AA),    new Mapping('홍', 0xC8AB),    new Mapping('홑', 0xC8AC),    new Mapping('화', 0xC8AD),    new Mapping('확', 0xC8AE), 
				new Mapping('환', 0xC8AF),    new Mapping('활', 0xC8B0),    new Mapping('홧', 0xC8B1),    new Mapping('황', 0xC8B2),    new Mapping('홰', 0xC8B3), 
				new Mapping('홱', 0xC8B4),    new Mapping('홴', 0xC8B5),    new Mapping('횃', 0xC8B6),    new Mapping('횅', 0xC8B7),    new Mapping('회', 0xC8B8), 
				new Mapping('획', 0xC8B9),    new Mapping('횐', 0xC8BA),    new Mapping('횔', 0xC8BB),    new Mapping('횝', 0xC8BC),    new Mapping('횟', 0xC8BD), 
				new Mapping('횡', 0xC8BE),    new Mapping('효', 0xC8BF),    new Mapping('횬', 0xC8C0),    new Mapping('횰', 0xC8C1),    new Mapping('횹', 0xC8C2), 
				new Mapping('횻', 0xC8C3),    new Mapping('후', 0xC8C4),    new Mapping('훅', 0xC8C5),    new Mapping('훈', 0xC8C6),    new Mapping('훌', 0xC8C7), 
				new Mapping('훑', 0xC8C8),    new Mapping('훔', 0xC8C9),    new Mapping('훗', 0xC8CA),    new Mapping('훙', 0xC8CB),    new Mapping('훠', 0xC8CC), 
				new Mapping('훤', 0xC8CD),    new Mapping('훨', 0xC8CE),    new Mapping('훰', 0xC8CF),    new Mapping('훵', 0xC8D0),    new Mapping('훼', 0xC8D1), 
				new Mapping('훽', 0xC8D2),    new Mapping('휀', 0xC8D3),    new Mapping('휄', 0xC8D4),    new Mapping('휑', 0xC8D5),    new Mapping('휘', 0xC8D6), 
				new Mapping('휙', 0xC8D7),    new Mapping('휜', 0xC8D8),    new Mapping('휠', 0xC8D9),    new Mapping('휨', 0xC8DA),    new Mapping('휩', 0xC8DB), 
				new Mapping('휫', 0xC8DC),    new Mapping('휭', 0xC8DD),    new Mapping('휴', 0xC8DE),    new Mapping('휵', 0xC8DF),    new Mapping('휸', 0xC8E0), 
				new Mapping('휼', 0xC8E1),    new Mapping('흄', 0xC8E2),    new Mapping('흇', 0xC8E3),    new Mapping('흉', 0xC8E4),    new Mapping('흐', 0xC8E5), 
				new Mapping('흑', 0xC8E6),    new Mapping('흔', 0xC8E7),    new Mapping('흖', 0xC8E8),    new Mapping('흗', 0xC8E9),    new Mapping('흘', 0xC8EA), 
				new Mapping('흙', 0xC8EB),    new Mapping('흠', 0xC8EC),    new Mapping('흡', 0xC8ED),    new Mapping('흣', 0xC8EE),    new Mapping('흥', 0xC8EF), 
				new Mapping('흩', 0xC8F0),    new Mapping('희', 0xC8F1),    new Mapping('흰', 0xC8F2),    new Mapping('흴', 0xC8F3),    new Mapping('흼', 0xC8F4), 
				new Mapping('흽', 0xC8F5),    new Mapping('힁', 0xC8F6),    new Mapping('히', 0xC8F7),    new Mapping('힉', 0xC8F8),    new Mapping('힌', 0xC8F9), 
				new Mapping('힐', 0xC8FA),    new Mapping('힘', 0xC8FB),    new Mapping('힙', 0xC8FC),    new Mapping('힛', 0xC8FD),    new Mapping('힝', 0xC8FE) 
			//}
			 
			 
		};
	}//ksc5601
	
	
}