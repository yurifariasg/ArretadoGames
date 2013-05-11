package com.arretadogames.pilot.screens;

import android.view.MotionEvent;

import com.arretadogames.pilot.render.GameCanvas;

/**
 * GameScreen class represents a Screen in the Game<br>
 * It has all operations that support a screen
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
	public abstract void render(GameCanvas canvas, float timeElapsed);

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
	public abstract void input(InputEventHandler event);
	
	/**
	 * Handles the event for when the user press the physical back button
	 */
	public abstract void onBackPressed();
	
	/**
	 * Handles the pause event, when the user receives a call or locks the screen
	 */
	public abstract void onPause();

}
