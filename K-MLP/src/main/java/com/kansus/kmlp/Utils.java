package com.kansus.kmlp;

import java.nio.ByteBuffer;

public class Utils {

	public static byte[] toByteArray(double[] doubleArray) {
		int times = Double.SIZE / Byte.SIZE;
		byte[] bytes = new byte[doubleArray.length * times];
		
		for (int i = 0; i < doubleArray.length; i++) {
			ByteBuffer.wrap(bytes, i * times, times).putDouble(doubleArray[i]);
		}
		
		return bytes;
	}

	public static double[] toDoubleArray(byte[] byteArray) {
		int times = Double.SIZE / Byte.SIZE;
		double[] doubles = new double[byteArray.length / times];
		
		for (int i = 0; i < doubles.length; i++) {
			doubles[i] = ByteBuffer.wrap(byteArray, i * times, times).getDouble();
		}
		
		return doubles;
	}
	
	public static byte[] intToByteArray(int integer) {
		ByteBuffer newBuf = ByteBuffer.allocate(4);
		newBuf.putInt(integer);
		return newBuf.array();
	}
	
	public static int byteArrayToInt(byte[] array) {
		ByteBuffer bf = ByteBuffer.wrap(array, 1, 4);
		return bf.getInt(); 
	}
}
