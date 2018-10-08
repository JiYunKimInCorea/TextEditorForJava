package com.gsoft.common.gui;

public class Test {
	void a() {
		float f;
		// ^, <<, >>, |, &, ~(정수만, 단항) 는 정수-정수만 가능하다.
		//int i = 1^2.5f + 2; //지수가 float이므로 에러
		//int i = 2.5f^1 + 2; //밑수가 float이므로 에러
		int i = 2^1 + 2;
		int k = i<<2;
		//int k2 = i<<2.5f; // float이므로 에러
		//int k3 = 2.5f<<2; // float이므로 에러
		int k1= i<<(char)1; // int-char 가능
		int k2= i<<(long)1; // int-long 가능
		int k3 = (char)k2 << 1; // char-int가능
		
		
		// +, -, *, /, %, 는 float-float가 가능하다.boolean-boolean불가능
		int m = (int) (2.5f%2);
		int m1 = (int) (2.56f%1.3f); // 사칙연산과 동일
		String m2 = true + "a"; // +일 경우 boolean-String가능
		
		// >, <, >=, <= 는 float-float가 가능하다.boolean-boolean불가능
		boolean j = (2.5f>2);
		boolean j1 = (2.56f>=1.3f); // 사칙연산과 동일
		//boolean j2 = "a">"b"; // String-String불가능
		//boolean j3 = true>false; //boolean-boolean불가능
		
		int n = 1 | 0;
		//int n1 = 1.5f | 1.2f; // float 에러
		
		int o = ~1;
		
		// ==, !=는 float-float가 가능하다.boolean-boolean도 가능하다.
		boolean p = 1.f>=1 && 0.f<=1 || -1.d<0;
		boolean p1 = 1.f==1;
		//boolean p2 = true>=false; // boolean-boolean불가능
		boolean p3 = 1.f==1;
		boolean p4 = true==false;
		
		// &&, ||, !(단항) float-float가 불가능하다.boolean-boolean만 가능하다.
		//boolean q = 1.5f && 1.2f; // float 에러
		//boolean q1 = true && 1; // boolean-int 에러 
		
		f = i; // 초기화안하면 에러
		//i = f; // 실수를 정수에 넣을때 타입캐스트 필요
		i += (char) f + 1.0f; // 실수를 정수에 넣을때 타입캐스트 필요
		i -= (char) f + 1;
				
		i += i*4%2 & i; 
		boolean bool = i>f==true;
		boolean bool2 = false==true!=i<=f;
		boolean bool3 = (false==true)!=(i<=f);
		boolean bool4 = (false==true)!=(i<<2<=f);
		boolean bool5 = (false==true)!=(i<<2<=f) && i<f;
		i = i<f ? 1 : 0;
		
		char ch = 0;
		i = ch; // 초기화안하면 에러
		i = ch + 1;
		ch %= (char) (ch + 1); // char로 캐스팅 필요
		f = ch;
		ch = (char) i; // char 타입캐스트해야함
		long l = 0;
		f = l;
		
		
		
		
		//////////////////   단항연산자   /////////////////////////////
		boolean b = !true;
		boolean b1 = !true==true;
		boolean b2 = !(true==false);
		boolean b3 = !(true==false) && (false!=true);
		
		//boolean b4 = ~(true==false) && (false!=true); // ~은 boolean에 불가능
		int i0 = ~1;
		boolean i1 = ~1==0;
		//int i2 = ~(1==0); // ~은 boolean에 불가능
		int i2 = ~(-1+2*2);
		int i3 = ~(-1+2) + (~1+(~1));
	}
}
