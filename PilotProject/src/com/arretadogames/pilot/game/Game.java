package com.arretadogames.pilot.game;

import javax.microedition.khronos.opengles.GL10;

import android.view.MotionEvent;

/**
 * Game class represents our Game
 */
public class Game {
	// TODO: remove the message below
	// This class isn't inherit from GameScreen
	// becucase it will not have some common functionalities
	// as the other screens
	
	private GameState currentState;
	
	public Game() {
		currentState = GameState.RUNNING_GAME;
	}

	/**
	 * Renders the current state of the game
	 * 
	 * @param gl
	 *            GL Interface to draw
	 * @param timeElapsed
	 *            Time Elapsed since last frame
	 */
	public void render(GL10 gl, float timeElapsed) {

	}

	/**
	 * Performs a step in the current game state's logic
	 * 
	 * @param timeElapsed
	 *            Time Elapsed since last frame
	 */
	public void step(float timeElapsed) {

	}

	/**
	 * Handles the input in the current game state
	 * 
	 * @param event
	 *            Input Event to be handled
	 */
	public void input(MotionEvent event) {

	}

}
