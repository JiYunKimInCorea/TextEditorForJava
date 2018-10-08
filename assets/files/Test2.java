package com.gsoft.common.gui;

public class Test2 {
	void a() {
		float f3 = -1.0f + -(2.0f) * 2345 - 1234;
		float f4 = -1.0f + +(2.0f);
		float f5 = -1.0f * +(2.0f);
		
		boolean p = 1.f>=-1 && -0.f<=1 || -1.d<0;
		boolean p1 = 1.f>=-1 && -0.f<=1 || -1l<0;
		boolean p2 = 1.1f>=-1.223d && -0.f<=1 || -1000l<0;
		
		float f1 = -(-1.f+2.f)+2;
		float f2 = (float) (-(-1.f+2.f*2+(char)3)*2.2f + -(-3.0d*2.0f)+3.0f);
		
		//int i2 = ~(-1c+2s*2f);
		int i3 = ~(-1+2) + (~1+(~1));
		int i4 = -((-1)+2) + (~(-1)+(~1));
		int i5 = +((+1)+2) + (~(+1)+(~1));
	}
}
