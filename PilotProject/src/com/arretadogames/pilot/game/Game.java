package com.arretadogames.pilot.game;

import java.util.HashMap;

import android.graphics.Color;
import android.graphics.Rect;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;

import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.EndScreen;
import com.arretadogames.pilot.screens.GameScreen;
import com.arretadogames.pilot.screens.InputEventHandler;
import com.arretadogames.pilot.screens.LevelSelectionScreen;
import com.arretadogames.pilot.screens.MainMenuScreen;
import com.arretadogames.pilot.screens.CharacterSelectionScreen;
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
	
	private HashMap<GameState, GameScreen> gameScreens;
	
	private boolean transitionStateOn;
	private Rect transitionRect;
	
	private boolean resetWorld;
	
	private Game() {
		currentState = GameState.SPLASH;
		gameScreens = new HashMap<GameState, GameScreen>();
		gameScreens.put(GameState.RUNNING_GAME, new GameWorld());
		gameScreens.put(GameState.MAIN_MENU, new MainMenuScreen());
		gameScreens.put(GameState.SPLASH, new SplashScreen());
		gameScreens.put(GameState.GAME_OVER, new EndScreen());
		gameScreens.put(GameState.CHARACTER_SELECTION, new CharacterSelectionScreen());
		gameScreens.put(GameState.LEVEL_SELECTION, new LevelSelectionScreen());
		transitionStateOn = false;
		resetWorld = false;
	}
	
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
		
		getScreen(currentState).render(canvas, timeElapsed);
		
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
		if (resetWorld) {
			PhysicalWorld.restart();
			((GameWorld) gameScreens.get(GameState.RUNNING_GAME)).free();
			gameScreens.put(GameState.RUNNING_GAME, new GameWorld());
			resetWorld = false;
		}
		
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
	 * Switch the current game state
	 * 
	 * @param state
	 *            new game's current state
	 */
	public void goTo(GameState state) {
		startTransitionAnimation(state);
	}
	
	private void changeState(GameState state) {
		
//		if (state == GameState.RUNNING_GAME) // TODO: arrumar maneira melhor de fazer isso
//			((GameWorld)getScreen(GameState.RUNNING_GAME)).initialize();
		
		if (state != null)
			currentState = state;
	}

	public void onBackPressed() {
		gameScreens.get(currentState).onBackPressed();
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

	public void resetWorld() {
		resetWorld = true;
	}
}
