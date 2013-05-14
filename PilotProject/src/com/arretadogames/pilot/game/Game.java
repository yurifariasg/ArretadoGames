package com.arretadogames.pilot.game;

import android.graphics.Color;
import android.graphics.Rect;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;

import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.screens.InputEventHandler;
import com.arretadogames.pilot.screens.MainMenuScreen;
import com.arretadogames.pilot.screens.SplashScreen;
import com.arretadogames.pilot.ui.AnimationManager;
import com.arretadogames.pilot.world.GameWorld;

/**
 * Game class represents our Game
 */
public class Game implements TweenAccessor<Game> {
	
	private static final int LEFT = 1;
	private static final int RIGHT = 2;
	
	private static Game game;

	private GameState currentState;
	private GameWorld gameWorld;

	private MainMenuScreen mainMenu;
	private SplashScreen splashScreen;
	
	private boolean transitionStateOn;
	private Rect transitionRect;
	
	private Game() {
		currentState = GameState.SPLASH;
		gameWorld = new GameWorld();
		mainMenu = new MainMenuScreen(this);
		splashScreen = new SplashScreen(this);
		transitionStateOn = false;
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
		
		AnimationManager.getInstance().update(timeElapsed);
		
		switch (currentState) {
		case RUNNING_GAME:
			gameWorld.render(canvas, timeElapsed);
			break;
		case MAIN_MENU:
			mainMenu.render(canvas, timeElapsed);
			break;
		case SPLASH:
			splashScreen.render(canvas, timeElapsed);
			break;
		default:
			break;
		}
		
		if (transitionStateOn) {
			canvas.drawRect(transitionRect, Color.rgb(0, 0, 0));
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
		case SPLASH:
			splashScreen.step(timeElapsed);
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
	public void input(InputEventHandler event) {
		if (transitionStateOn)
			return; // Input Disabled when Transition

		switch (currentState) {
		case RUNNING_GAME:
			gameWorld.input(event);
			break;
		case MAIN_MENU:
			mainMenu.input(event);
			break;
		case SPLASH:
			splashScreen.input(event);
			break;
		default:
			break;
		}

	}

	/**
	 * Switch the current game state
	 * 
	 * @param state
	 *            new game's current state
	 */
	public void goTo(GameState state) {
		startTransitionAnimation(state);
	}
	
	private void changeState(GameState state) {
		if (state != null)
			currentState = state;
	}

	public void onBackPressed() {
		switch (currentState) {
		case RUNNING_GAME:
			gameWorld.onBackPressed();
			break;
		case MAIN_MENU:
			mainMenu.onBackPressed();
			break;
		default:
			break;
		}
	}
	
	public static Game getInstance() {
		if (game == null)
			game = new Game();
		return game;
	}
	
	@Override
	public int getValues(Game game, int type, float[] returnValues) {
		switch (type) {
		case LEFT:
			returnValues[0] = transitionRect.left;
			break;
		case RIGHT:
			returnValues[0] = transitionRect.right;
			break;
		}
		return 1;
	}

	@Override
	public void setValues(Game game, int type, float[] setValues) {
		switch (type) {
		case LEFT:
			transitionRect.left = (int) setValues[0];
			break;
		case RIGHT:
			transitionRect.right = (int) setValues[0];
			break;
		}
	}
	
	private void startTransitionAnimation(final GameState state) {
		transitionStateOn = true;
		
		transitionRect = new Rect(
				(int) DisplaySettings.TARGET_WIDTH, 0,
				(int) DisplaySettings.TARGET_WIDTH, (int) DisplaySettings.TARGET_HEIGHT);
		
		Timeline.createSequence()
				.push(Tween.to(this, LEFT, 0.4f).target(0f))
				.push(Tween.call(new TweenCallback() {
					
					@Override
					public void onEvent(int arg0, BaseTween<?> arg1) {
						changeState(state);
					}
				}))
				.push(Tween.to(this, RIGHT, 0.4f).target(0f))
				.push(Tween.call(new TweenCallback() {
					
					@Override
					public void onEvent(int arg0, BaseTween<?> arg1) {
						transitionStateOn = false;
					}
				}))
				.start(AnimationManager.getInstance());
	}

}
