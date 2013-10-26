package com.arretadogames.pilot.game;

import java.util.HashMap;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;

import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.loading.LoadManager;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.CharacterSelectionScreen;
import com.arretadogames.pilot.screens.EndScreen;
import com.arretadogames.pilot.screens.GameScreen;
import com.arretadogames.pilot.screens.InputEventHandler;
import com.arretadogames.pilot.screens.LevelSelectionScreen;
import com.arretadogames.pilot.screens.MainMenuScreen;
import com.arretadogames.pilot.screens.SplashScreen;
import com.arretadogames.pilot.ui.AnimationManager;
import com.arretadogames.pilot.world.GameWorld;

/**
 * Game class represents our Game.
 * This entity should hold all information related to <b>Game Logic</b>
 */
public class Game implements TweenAccessor<Game>, LoadManager.LoadFinisherCallBack {
	
	private static final int LEFT = 1;
	private static final int RIGHT = 2;
	
	private static Game game;

	private GameState currentState;
	
	private HashMap<GameState, GameScreen> gameScreens;
	
	// Transition
	private boolean transitionStateOn;
	private Rect transitionRect;
	private LoadManager loadManager;
	private GameState nextState;
	
	/**
	 * Creates a Game
	 */
	private Game() {
		nextState = GameState.SPLASH;
		loadManager = LoadManager.getInstance();
		gameScreens = new HashMap<GameState, GameScreen>();
		gameScreens.put(GameState.RUNNING_GAME, new GameWorld());
		gameScreens.put(GameState.MAIN_MENU, new MainMenuScreen());
		gameScreens.put(GameState.SPLASH, new SplashScreen());
		gameScreens.put(GameState.GAME_OVER, new EndScreen());
		gameScreens.put(GameState.CHARACTER_SELECTION, new CharacterSelectionScreen());
		gameScreens.put(GameState.LEVEL_SELECTION, new LevelSelectionScreen());
		transitionStateOn = false;
		loadManager.prepareLoad(new GameState[] { nextState });
		currentState = GameState.LOADING;
	}
	
	/**
	 * Gets the GameScreen related to the given GameState
	 * @param state
	 * GameState
	 * @return GameScreen, or null if there's not screen related to this GameState
	 */
	public GameScreen getScreen(GameState state) {
		return gameScreens.get(state);
	}

	/**
	 * Renders the current state of the game
	 * 
	 * @param canvas
	 *            GameCanvas to draw
	 * @param timeElapsed
	 *            Time Elapsed since last frame
	 */
	public void render(GLCanvas canvas, float timeElapsed) {
		
		AnimationManager.getInstance().update(timeElapsed);
		
		if (currentState.equals(GameState.LOADING)) {
			loadManager.swapTextures(game, canvas);
			return;
		} else {
			if (gameScreens.containsKey(currentState))
				getScreen(currentState).render(canvas, timeElapsed);
		}
		
		if (transitionStateOn) {
			canvas.drawRect(transitionRect.left, transitionRect.top,
					transitionRect.right, transitionRect.bottom,
					Color.rgb(0, 0, 0));
		}
	}
	
	/**
	 * Performs a step in the current game state's logic
	 * 
	 * @param timeElapsed
	 *            Time Elapsed since last frame
	 */
	public void step(float timeElapsed) {
		if (currentState.equals(GameState.LEVEL_RESTART)) {
			goTo(GameState.RUNNING_GAME);
			return;
		}
		
		if (gameScreens.containsKey(currentState))
			gameScreens.get(currentState).step(timeElapsed);
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

		gameScreens.get(currentState).input(event);
	}

	/**
	 * (Asynchronous method) Switch the current game state
	 * 
	 * @param state
	 *            new game's current state
	 */
	public void goTo(GameState state) {
		if (transitionStateOn) {
			Log.e("Game", "Tried to call goTo while on transition state");
			return;
		}
		nextState = state;
		loadManager.prepareLoad(new GameState[] { state });
		startTransitionAnimation();
	}
	
	
	/*
	 * (Asynchronous method) Changes the current state of the Game
	 */
	private void changeState(GameState state) {
		if (state != null) {

			if (getScreen(currentState) != null) // Unloads if there is a screen
				getScreen(currentState).onUnloading();

			currentState = state;

			if (getScreen(currentState) != null) // Loads if there is a screen
				getScreen(currentState).onLoading();
			
		}
	}
	
	/**
	 * Gets the only and single instance of Game
	 * @return Game
	 */
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
	
	/*
	 * (Synchronous method) Starts the transition to the next state
	 */
	private void startTransitionAnimation() {
		transitionStateOn = true;
		
		// Stop All tweens associated with game
		AnimationManager.getInstance().killTarget(this);
		
		transitionRect = new Rect(
				(int) GameSettings.TARGET_WIDTH, 0,
				(int) GameSettings.TARGET_WIDTH, (int) GameSettings.TARGET_HEIGHT);
		
		Timeline.createSequence()
				.push(Tween.to(this, LEFT, 0.4f).target(0f))
				.push(Tween.call(new TweenCallback() {
					
					@Override
					public void onEvent(int arg0, BaseTween<?> arg1) {
						changeState(GameState.LOADING);
					}
				}))
				.start(AnimationManager.getInstance());
	}

	/**
	 * (Asynchronous method) Causes the Game to pause
	 */
	public void onPause() {
		if (currentState == GameState.RUNNING_GAME) {
			((GameWorld)getScreen(GameState.RUNNING_GAME)).onPause();
		}
	}
	
	/**
	 * (Asynchronous method) Causes the Game to resume
	 */
	public void onResume() {
	}

	@Override
	public void onLoadFinished(boolean error) { // TODO @yuri: Handle error
		
		if (nextState.equals(GameState.SPLASH)) {
			//Don't perform animation on splash screen
			changeState(nextState);
			nextState = null;
			transitionStateOn = false;
			return;
		} 
		
		Timeline.createSequence()
			.push(Tween.call(new TweenCallback() {
			
			@Override
			public void onEvent(int arg0, BaseTween<?> arg1) {
				changeState(nextState);
				nextState = null;
				
				transitionRect.left = 0;
			}
		}))
		.push(Tween.to(game, RIGHT, 0.5f).target(0f))
		.push(Tween.call(new TweenCallback() {
			
			@Override
			public void onEvent(int arg0, BaseTween<?> arg1) {
				transitionStateOn = false;
			}
		}))
		.start(AnimationManager.getInstance());
	}
}
