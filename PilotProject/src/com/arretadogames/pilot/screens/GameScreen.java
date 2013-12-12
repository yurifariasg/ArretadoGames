package com.arretadogames.pilot.screens;

import com.arretadogames.pilot.render.opengl.GLCanvas;

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
	public abstract void render(GLCanvas canvas, float timeElapsed);

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
	 * Handles the pause event, when the user receives a call or locks the screen
	 */
	public abstract void onPause();
	
	// Asynchronous method. Called when the screen should be unloaded
	public void onLoading() {
		// TODO Auto-generated method stub
	}
	
	
	// Asynchronous method. Called when the screen should unloaded
	public void onUnloading() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Handles the physical back button
	 */
	public void onBackPressed() { }

}
