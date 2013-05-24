package com.arretadogames.pilot.ui;

import javax.microedition.khronos.opengles.GL10;

import android.view.MotionEvent;

/**
 * GameScreen class represents a Screen in the Game<br>
 * It has all operations that support a screen
 * 
 */
public abstract class GameScreen {
	// For now this class is abstract because we are going to add some
	// Common content for all of them in the future...

	/**
	 * Renders the Screen
	 * 
	 * @param gl
	 *            GL Interface to Draw
	 * @param timeElapsed
	 *            Time Elapsed from last frame
	 */
	public abstract void render(GL10 gl, float timeElapsed);

	/**
	 * Performs a Step in the Screen's logic
	 * 
	 * @param timeElapsed
	 *            Time Elapsed from last frame
	 */
	public abstract void step(float timeElapsed);

	/**
	 * Handles the input into the Screen
	 * 
	 * @param event
	 *            MotionEvent / Input Event to be handled
	 */
	public abstract void input(MotionEvent event);

}
