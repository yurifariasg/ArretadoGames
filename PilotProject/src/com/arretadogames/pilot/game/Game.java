package com.arretadogames.pilot.game;

import android.view.MotionEvent;

import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.world.GameWorld;

/**
 * Game class represents our Game
 */
public class Game {
	// TODO: remove the message below
	// This class isn't inherit from GameScreen
	// becucase it will not have some common functionalities
	// as the other screens
	
	private GameState currentState;
	private GameWorld gameWorld;
	
	public Game() {
		currentState = GameState.RUNNING_GAME;
		gameWorld = new GameWorld();
	}

	/**
	 * Renders the current state of the game
	 * 
	 * @param canvas
	 *            GameCanvas to draw
	 * @param timeElapsed
	 *            Time Elapsed since last frame
	 */
	public void render(GameCanvas canvas, float timeElapsed) {
		
		gameWorld.render(canvas, timeElapsed);

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
