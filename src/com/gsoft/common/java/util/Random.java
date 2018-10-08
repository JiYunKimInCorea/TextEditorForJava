package com.gsoft.common.java.util;

public class Random {
	/**Creates a new random number generator.*/
	Random() {
		
	}
	/**Creates a new random number generator using a single long seed.*/
	Random(long seed) {
		
	}
	/**Returns an effectively unlimited stream of pseudorandom double values, each between zero (inclusive) and one (exclusive).*/
	/*DoubleStream	doubles() {
		
	}*/
	/**Returns an effectively unlimited stream of pseudorandom double values, each conforming to the given origin (inclusive) and bound (exclusive).*/
	/*DoubleStream	doubles(double randomNumberOrigin, double randomNumberBound) {
		
	}*/
	/**Returns a stream producing the given streamSize number of pseudorandom double values, each between zero (inclusive) and one (exclusive).*/
	/*DoubleStream	doubles(long streamSize) {
		
	}*/
	/**Returns a stream producing the given streamSize number of pseudorandom double values, each conforming to the given origin (inclusive) and bound (exclusive).*/
	/*DoubleStream	doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) {
		
	}*/
	/**Returns an effectively unlimited stream of pseudorandom int values.*/
	/*IntStream	ints() {
		
	}*/
	/**Returns an effectively unlimited stream of pseudorandom int values, each conforming to the given origin (inclusive) and bound (exclusive).*/
	/*IntStream	ints(int randomNumberOrigin, int randomNumberBound) {
		
	}*/
	/**Returns a stream producing the given streamSize number of pseudorandom int values.*/
	/*IntStream	ints(long streamSize) {
		
	}*/
	/**Returns a stream producing the given streamSize number of pseudorandom int values, each conforming to the given origin (inclusive) and bound (exclusive).*/
	/*IntStream	ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
		
	}*/
	/**Returns an effectively unlimited stream of pseudorandom long values.*/
	/*LongStream	longs() {
		
	}*/
	/**Returns a stream producing the given streamSize number of pseudorandom long values.*/
	/*LongStream	longs(long streamSize) {
		
	}*/
	/**Returns an effectively unlimited stream of pseudorandom long values, each conforming to the given origin (inclusive) and bound (exclusive).*/
	/*LongStream	longs(long randomNumberOrigin, long randomNumberBound) {
		
	}*/
	/**Returns a stream producing the given streamSize number of pseudorandom long, each conforming to the given origin (inclusive) and bound (exclusive).*/
	/*LongStream	longs(long streamSize, long randomNumberOrigin, long randomNumberBound) {
		
	}*/
	/**Generates the next pseudorandom number.*/
	int	next(int bits) {
		return bits;
		
	}
	/**Returns the next pseudorandom, uniformly distributed boolean value from this random number generator's sequence.*/
	boolean	nextBoolean() {
		return false;
		
	}
	/**Generates random bytes and places them into a user-supplied byte array.*/
	void	nextBytes(byte[] bytes) {
		
	}
	/**Returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0 from this random number generator's sequence.*/
	double	nextDouble() {
		return 0;
		
	}
	/**Returns the next pseudorandom, uniformly distributed float value between 0.0 and 1.0 from this random number generator's sequence.*/
	float	nextFloat() {
		return 0;
		
	}
	/**Returns the next pseudorandom, Gaussian ("normally") distributed double value with mean 0.0 and standard deviation 1.0 from this random number generator's sequence.*/
	double	nextGaussian() {
		return 0;
		
	}
	/**Returns the next pseudorandom, uniformly distributed int value from this random number generator's sequence.*/
	int	nextInt() {
		return 0;
		
	}
	/**Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value (exclusive), drawn from this random number generator's sequence.*/
	int	nextInt(int bound) {
		return bound;
		
	}
	/**Returns the next pseudorandom, uniformly distributed long value from this random number generator's sequence.*/
	long	nextLong() {
		return 0;
		
	}
	/**Sets the seed of this random number generator using a single long seed.*/
	void	setSeed(long seed) {
		
	}
	
	
}