package com.gsoft.common.java.lang;

public abstract class Number {
	/**Returns the value of the specified number as a byte, which may involve rounding or truncation.*/
	byte	byteValue() {
		return 0;
		
	}
	/**Returns the value of the specified number as a double, which may involve rounding.*/
	abstract double	doubleValue();
	/**Returns the value of the specified number as a float, which may involve rounding.*/
	abstract float	floatValue();
	/**Returns the value of the specified number as an int, which may involve rounding or truncation.*/
	abstract int	intValue();
	/**Returns the value of the specified number as a long, which may involve rounding or truncation.*/
	abstract long	longValue();
	/**Returns the value of the specified number as a short, which may involve rounding or truncation.*/
	short	shortValue() {
		return 0;
		
	}
	
}