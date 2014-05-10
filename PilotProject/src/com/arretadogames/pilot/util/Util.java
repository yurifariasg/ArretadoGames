package com.arretadogames.pilot.util;

import android.graphics.Color;

import com.arretadogames.pilot.render.PhysicsRect;


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

	public static float distance(float x, float y, float x2, float y2) {
		return (float) Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
	}

	/** Returns an interpoloated color, between <code>a</code> and <code>b</code> */
	public static int interpolateColor(int a, int b, float proportion) {
		if (proportion < 0)
			proportion = 0;
		if (proportion > 1)
			proportion = 1;
		return (Integer) evaluate(proportion, a, b);
	}
	
	/**
	 * Adjusts the alpha of a Java color
	 */
	public static int adjustColorAlpha(int color, float factor) {
	    int alpha = Math.round(Color.alpha(color) * factor);
	    int red = Color.red(color);
	    int green = Color.green(color);
	    int blue = Color.blue(color);
	    return Color.argb(alpha, red, green, blue);
	}
	
	public static PhysicsRect convertToSquare(PhysicsRect pRect) {
	    return new PhysicsRect(Math.max(pRect.width(), pRect.height()), Math.max(pRect.width(), pRect.height()));
	}
	
//	public RectF convertToSquare(RectF rect) {
//        return new RectF(Math.max(rect.width(), rect.height()), Math.max(rect.width(), rect.height()));
//    }
	
	/**
	 * @credits:
	 * https://github.com/android/platform_frameworks_base/blob/master/core/java/android/animation/ArgbEvaluator.java
	 * 
     * This function returns the calculated in-between value for a color
     * given integers that represent the start and end values in the four
     * bytes of the 32-bit int. Each channel is separately linearly interpolated
     * and the resulting calculated values are recombined into the return value.
     *
     * @param fraction The fraction from the starting to the ending values
     * @param startValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @param endValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @return A value that is calculated to be the linearly interpolated
     * result, derived by separating the start and end values into separate
     * color channels and interpolating each one separately, recombining the
     * resulting values in the same way.
     */
	public static Object evaluate(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }
}
