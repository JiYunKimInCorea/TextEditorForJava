package com.gsoft.common;

public class Charset {
	
	public enum Codeset {
		UTF_8,
		UTF_16,
		MS_949
	}
	
	/**UTF-8 형식은 다음과 같다.<p>
	 * -1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	-2바이트 이상으로 표시된 문자의 경우, 
	첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 
	예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	-첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.
	예를 들어, 
	가(44032, AC00) =-22, -80, -128 =
		첫바이트 : 	  1110(14)   1010(A)   
		두번째바이트 : 10(나머지바이트상위2비트) 11   00(C) 00    
		세번째바이트 : 10(나머지바이트상위2비트) 00(0)   0000(0)
		
	-22 : 22의 2의보수 11 0, 5 10, 2 110, 00010110  
		 따라서 1110(14) 1010(A)
	-80 : 80의 2의보수 40 0, 20 00, 10 000, 5 0000, 2 10000, 01010000, 
		 따라서 10 1100(C) 00
	-128 : 128의 2의보수 64 0, 32 00, 16 000, 8 0000, 4 00000, 2 000000 10000000 
		 따라서 10 00(0) 0000(0)
		 
	가 : char c = 0xac00;*/
	public static char decode(Codeset codeName, byte[] buf) {
		//byte test = buf[0] & 0B00;
		// 키 d0a4
		if (codeName==Codeset.UTF_8) {
			// 한글, 한자, 일본어, LatinExtendC[-30, -79, -96][-30, -79, -95]
			if (buf.length==3) {
				byte a = (byte) (buf[0] & 0x0f);
				byte c = (byte) ((buf[1] >> 2) & 0x0f);
				// 0b00000011은 3이다.
				// 0b00110000은 48이다.
				byte v0_0 = (byte) (((buf[1] & /*0b00000011*/(byte)3) << 2) | ((buf[2] & /*0b00110000*/(byte)48) >> 4));
				byte v0_1 = (byte) (buf[2] & 0x0f);
				
				char r = (char) (((a & 0x0f) << 12) | ((c & 0x0f) << 8) | ((v0_0 & 0x0f) << 4) | (v0_1 & 0x0f)); 
				return r;
			}
			else if (buf.length==2) {
				// 첫번째 바이트의 마지막 4비트로 구분한다.
				byte type = (byte) (buf[0] & 0x0f);
				
				// LatinSupplement[-61, -87, 0] 
				
				// [-61, -87]
				// 61은 30 1, 15 01, 7 101, 3 1101, 00111101 따라서 -61은 11000011 (c3)
				// 87은 43 1, 21 11, 10 111, 5 0111, 2 10111, 01010111 따라서 -87은 10101001 (a9) 
				// 따라서 11101001(e9)
				
				// [-61, -88]
				// 88은 01011000 따라서 10101000 
				// 따라서 e8
				
				// [-61, -79]
				// 61은 30 1, 15 01, 7 101, 3 1101, 00111101 따라서 -61은 11000011 (c3)
				// 79는 39 1, 19 11, 9 111, 4 1111, 01001111 따라서 -79는 10110001    
				// 따라서 f1
				if (type==3) { // 0b0011  Latin Supplement 
					byte a = (byte) (buf[0] & 0x03); // 마지막 2비트
					byte b = (byte) ((buf[1] & 0x30) >> 4); // 5, 6 비트
					a = (byte) ((a << 2) | b);
					b = (byte) (buf[1] & 0x0f); // 마지막 4비트
					
					char r = (char) (((a & 0x0f) << 4) | (b & 0x0f));
					return r;
				}
				// LatinExtendA 
				// [-17, -69, -65] 이후에 LatinExtendA 두 바이트 문자를 넣는다. 
				
				// 0x0100[-60, -128] 
				// 60은 00111100 따라서 -60은 1100(12) 0100
				// 128은 10000000 따라서 -128은 10000000
				
				// 0x0101[-60, -127]
				// 127은 01111111 따라서 -127은 10000001
				
				// 0x0202[-60, -126]
				
				// 0x017E [-59, -66]
				// 59는 00111011 따라서 -59는 1100(12) 0101
				// 66은 33 0, 16 10, 8 010, 4 0010, 2 00010, 01000010 따라서 -66은 10 111110
				// 첫바이트의 마지막 2비트 01과 두번째 바이트의 마지막 6비트 111110을 합치면 126이 되고,
				// 첫바이트의 마지막 3번째 비트 0x0100 + 126을 더하면 0x017E이 된다.
				else if (type==4 || type==5) { // 0b0100  Latin Extended A 
					char base = 0x0100;
					byte a = (byte) ((buf[0] & 0x03) << 6); // 마지막 2비트
					byte b = (byte) (buf[1] & 0x3f); // 마지막 6비트
					byte inc = (byte) (a | b);
					
					char r = (char) (base + inc);
					return r;
					
				}
				// Latin Extend B
				// 0x0180[-58, -128]
				// 58은 00111010 따라서 -58은 1100(12) 0110
				// 128은 10000000 따라서 -128은 10000000
				
				// 0x0200[-56, -128]
				// 56은 00111000 따라서 -56은 1100(12) 1000
				
				// 0x024E[-55, -114]
				// 55는 27 1, 13 11, 6 111, 3 0111, 00110111 따라서 -55는 1100(12) 1001
				// 114는 57 0, 28 10, 14 010, 7 0010, 3 10010, 01110010 따라서 -114는 10 001110
				else if (type==6) { // 0b0110  Latin Extended B 
					char base = 0x0180;
					byte a = (byte) ((buf[0] & 0x01) << 6); // 마지막 1비트
					byte b = (byte) (buf[1] & 0x3f); // 마지막 6비트
					byte inc = (byte) (a | b);
					
					char r = (char) (base + inc);
					return r;
				}
				else if (type==8 || type==9) { // 0b0110  Latin Extended B 
					char base = 0x0200;
					byte a = (byte) ((buf[0] & 0x01) << 6); // 마지막 1비트
					byte b = (byte) (buf[1] & 0x3f); // 마지막 6비트
					byte inc = (byte) (a | b);
					
					char r = (char) (base + inc);
					return r;
				}
				/*else if (type==9) { // 0b1001  Latin Extended B 
					// 13(가로) * 16(세로) = 160 + 48 = 208개이다.
					// 208 - 127 = 81이다.
					char base = 0x0180; 
					short a = (short) ((buf[0] & 0x01) << 6); // 마지막 1비트
					short b = (short) (buf[1] & 0x0f); // 마지막 4비트
					short inc45 = 0x30; // 0b00110000;
					short inc = (byte) (a | inc45 | b);
					inc += 80;
					
					char r = (char) (base + inc);
					return r;
				
				}*/
			}
			else if (buf.length==1) {
				char r = (char) buf[0];
				return r;
			}
		}//if (codeName==Codeset.UTF_8) {
		return '0';
		
	}
	
	/**-1바이트로 표시된 문자의 최상위 비트는 항상 0이다.
	-2바이트 이상으로 표시된 문자의 경우, 
	첫 바이트의 상위 비트들이 그 문자를 표시하는 데 필요한 바이트 수를 결정한다. 
	예를 들어서 2바이트는 110으로 시작하고, 3바이트는 1110으로 시작한다.
	-첫 바이트가 아닌 나머지 바이트들은 상위 2비트가 항상 10이다.
	예를 들어, 
	가(44032, AC00) =-22, -80, -128 =
		첫바이트 : 	  1110(14)   1010(A)   
		두번째바이트 : 10(나머지바이트상위2비트) 11   00(C) 00    
		세번째바이트 : 10(나머지바이트상위2비트) 00(0)   0000(0)
		
	-22 : 22의 2의보수 11 0, 5 10, 2 110, 00010110  
		 따라서 1110(14) 1010(A)
	-80 : 80의 2의보수 40 0, 20 00, 10 000, 5 0000, 2 10000, 01010000, 
		 따라서 10 1100(C) 00
	-128 : 128의 2의보수 64 0, 32 00, 16 000, 8 0000, 4 00000, 2 000000 10000000 
		 따라서 10 00(0) 0000(0)
		 
	가 : char c = 0xac00;*/
	public static byte[] endcode(Codeset codeName, char ch) {
		if (codeName==Codeset.UTF_8) {
			if (0<=ch && ch<=127) {
				byte[] r = new byte[1];
				r[0] = (byte) ch;
				return r;
			}
			else {
				if (0x0080<=ch && ch<=0x00FF) { // Latin Supplement
					// LatinSupplement[-61, -87, 0] 
					
					// [-61, -87]
					// 61은 30 1, 15 01, 7 101, 3 1101, 00111101 따라서 -61은 11000011 (c3)
					// 87은 43 1, 21 11, 10 111, 5 0111, 2 10111, 01010111 따라서 -87은 10101001 (a9) 
					// 따라서 11101001(e9)
					
					// [-61, -88]
					// 88은 01011000 따라서 10101000 
					// 따라서 e8
					
					// [-61, -79]
					// 61은 30 1, 15 01, 7 101, 3 1101, 00111101 따라서 -61은 11000011 (c3)
					// 79는 39 1, 19 11, 9 111, 4 1111, 01001111 따라서 -79는 10110001    
					// 따라서 f1
					
					byte[] r = new byte[2];
					
					byte a = (byte) ((ch & 0x00f0) >> 4); // 1110
					byte b = (byte) (ch & 0x000f);	// 1001
					
					byte a0 = (byte) (a & 0x03); // a에서 마지막 2비트(즉 0, 1비트), 10
					byte a1 = (byte) ((a & 0x0C) >> 2); // a에서 2, 3 비트, 11
					
					r[0] = (byte) (0xc0 | a1);
					r[1] = (byte) (0x80 | (a0 << 4) | b);
					
					return r;
				}
				// LatinExtendA 
				// [-17, -69, -65] 이후에 LatinExtendA 두 바이트 문자를 넣는다. 
				
				// 0x0100[-60, -128] 
				// 60은 00111100 따라서 -60은 1100(12) 0100
				// 128은 10000000 따라서 -128은 10000000
				
				// 0x0101[-60, -127]
				// 127은 01111111 따라서 -127은 10000001
				
				// 0x0202[-60, -126]
				
				// 0x017E [-59, -66]
				// 59는 00111011 따라서 -59는 1100(12) 0101
				// 66은 33 0, 16 10, 8 010, 4 0010, 2 00010, 01000010 따라서 -66은 10 111110
				// 첫바이트의 마지막 2비트 01과 두번째 바이트의 마지막 6비트 111110을 합치면 126이 되고,
				// 첫바이트의 마지막 3번째 비트 0x0100 + 126을 더하면 0x017E이 된다.
				else if (0x0100<=ch && ch<=0x017F) { // Latin Extended A
					byte[] r = new byte[2];
					
					char base = 0x0100;
					byte inc = (byte) (ch - base); // 0
					
					byte a = (byte) ((inc & 0xC0) >> 6); // 최상위 2비트, 00
					byte b = (byte) (inc & 0x3f); // 마지막 6비트, 000000
					
					r[0] = (byte) (0xC4 | a); // 1100(12) 0100
					r[1] = (byte) (0x80 | b); // 10000000
					
					return r;
				
				}
				else if (0x0180<=ch && ch<=0x024F) { // Latin Extended B
					// Latin Extend B
					// 0x0180[-58, -128]
					// 58은 00111010 따라서 -58은 1100(12) 0110
					// 128은 10000000 따라서 -128은 10000000
					
					// 0x0200[-56, -128]
					// 56은 00111000 따라서 -56은 1100(12) 1000
					
					// 024E[-55, -114]
					// 55는 27 1, 13 11, 6 111, 3 0111, 00110111 따라서 -55는 1100(12) 1001
					// 114는 57 0, 28 10, 14 010, 7 0010, 3 10010, 01110010 따라서 -114는 10 001110
					if (0x0180<=ch && ch<=0x01FF) {
						byte[] r = new byte[2];
						
						char base = 0x0180;
						byte inc = (byte) (ch - base); // 0
						
						byte a = (byte) ((inc & 0xC0) >> 6); // 최상위 2비트, 00
						byte b = (byte) (inc & 0x3f); // 마지막 6비트, 000000
						
						r[0] = (byte) (0xC6 | a); // 1100(12) 0110
						r[1] = (byte) (0x80 | b); // 10000000
						
						return r;
					}
					// 13(가로) * 16(세로) = 160 + 48 = 208개이다.
					// 208 - 127 = 81이다.
					else {
						byte[] r = new byte[2];
						
						char base = 0x0200;
						byte inc = (byte) (ch - base); // 0
						
						byte a = (byte) ((inc & 0xC0) >> 6); // 최상위 2비트, 00
						byte b = (byte) (inc & 0x3f); // 마지막 6비트, 000000
						
						r[0] = (byte) (0xC8 | a); // 1100(12) 1000
						r[1] = (byte) (0x80 | b); // 10000000
						
						return r;
					}
				}
				else  { //if (0x2C60<=ch && ch<=0x2C7F)
					// 한글, 한자, 일본어, LatinExtendC[-30, -79, -96][-30, -79, -95]
					byte a = (byte) ((ch & 0xf000) >> 12);
					byte c = (byte) ((ch & 0x0f00) >> 8);
					byte v0_0 = (byte) ((ch & 0x00f0) >> 4);
					byte v0_1 = (byte) ((ch & 0x000f));
					
					byte[] r = new byte[3];
					// 0b11100000을 2의 보수로 고치면 00100000(32)이므로 -32가 된다.				
					r[0] = (byte) (/*0b11100000*/(byte)-32 | a);
					// 0b10000000을 2의 보수로 고치면 0b10000000(128)이므로 -128이 된다. 
					// 0b00001100을 12이다.
					r[1] = (byte) (/*0b10000000*/(byte)-128 | (byte)(c << 2) | (byte)((v0_0 & /*0b00001100*/(byte)12) >> 2));
					// 0b10000000은 -128이다.
					// 0b00000011은 3이다.
					r[2] = (byte) (/*0b10000000*/(byte)-128 | (byte)((v0_0 & /*0b00000011*/(byte)3) << 4) | (byte)((v0_1 & 0x0f)));
					return r;
				}
			}
		}
		return null;
	}
	
	
	
}