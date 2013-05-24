package com.arretadogames.pilot.screens;

import java.util.HashMap;

import android.graphics.Paint;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.AnimationManager;

public class GameOverScreen extends GameScreen {
	
	private boolean hasWon;
	private HashMap<PlayerNumber, Player> players;
	private Paint textPaint;
	
	public GameOverScreen() {
		textPaint = new Paint();
		textPaint.setTextSize(1f);
	}
	
	public void initialize(boolean hasWon, HashMap<PlayerNumber, Player> players) {
		this.hasWon = hasWon;
		this.players = players;
		System.out.println("Call");
		Timeline.createSequence()
			.pushPause(1f)
			.push(Tween.call(new TweenCallback() {
				
				@Override
				public void onEvent(int arg0, BaseTween<?> arg1) {
					Game.getInstance().resetWorld(); // Reset World
				}
			}))
			.pushPause(1f)
			.push(Tween.call(new TweenCallback() {
			
			@Override
			public void onEvent(int arg0, BaseTween<?> arg1) {
				Game.getInstance().goTo(GameState.MAIN_MENU);
			}
		})).start(AnimationManager.getInstance());
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		if (hasWon)
			canvas.fillScreen(255, 255,255, 255);
		else
			canvas.fillScreen(200, 200, 150, 255);
		canvas.drawText(hasWon ? "Congrats, bitch" : "bad.",
				DisplaySettings.TARGET_WIDTH / 2,  DisplaySettings.TARGET_HEIGHT / 2,
				textPaint, true);
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub
	}

	@Override
	public void input(InputEventHandler event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
	}

}
