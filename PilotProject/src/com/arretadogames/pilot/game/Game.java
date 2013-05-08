package com.arretadogames.pilot.game;

import android.view.MotionEvent;

import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.screens.MainMenuScreen;
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
	
	private MainMenuScreen mainMenu;
	
	public Game() {
		currentState = GameState.MAIN_MENU;
		gameWorld = new GameWorld();
		mainMenu = new MainMenuScreen();
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
		switch (currentState) {
		case RUNNING_GAME:
			gameWorld.render(canvas, timeElapsed);
			break;
		case MAIN_MENU:
			mainMenu.render(canvas, timeElapsed);
			break;
		default:
			break;
		}

	}

	/**
	 * Performs a step in the current game state's logic
	 * 
	 * @param timeElapsed
	 *            Time Elapsed since last frame
	 */
	public void step(float timeElapsed) {
		switch (currentState) {
		case RUNNING_GAME:
			gameWorld.step(timeElapsed);
			break;
		case MAIN_MENU:
			mainMenu.step(timeElapsed);
			break;
		default:
			break;
		}

	}

	/**
	 * Handles the input in the current game state
	 * 
	 * @param event
	 *            Input Event to be handled
	 */
	public void input(MotionEvent event) {
		
		switch (currentState) {
		case RUNNING_GAME:
			gameWorld.input(event);
			break;
		case MAIN_MENU:
			mainMenu.input(event);
			break;
		default:
			break;
		}

	}

}
