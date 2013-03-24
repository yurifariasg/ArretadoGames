package com.arretadogames.pilot.util;

/**
 * Helper class to perform usual operations
 */
public class Util {

	/**
	 * Converts a Nano Time to Seconds
	 * 
	 * @param nanoTime
	 *            Nano Time
	 * @return float - nano time in seconds
	 */
	public static float convertToSeconds(float nanoTime) {
		return nanoTime / 1000000f;
	}

}
