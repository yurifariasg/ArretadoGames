package com.arretadogames.pilot.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Paint;
import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.Text;

public class EndScreen extends GameScreen {
	
	private static final int PLAYER_INFO_Y = 130;
	private static final int PLAYER_INFO_Y_SPACING = 50;
	private static final int PLAYER_INFO_X_OFFSET = 0;
	private static final int SECONDS_TO_WAIT = 5;
	
	private float totalTimeElapsed;
	private boolean hasWon;
	private HashMap<PlayerNumber, Player> players;
	private Paint textPaint;
	private int backgroundId;
	
	private List<Text> playerInformation;
	
	public EndScreen() {
		textPaint = new Paint();
		textPaint.setTextSize(1f);
	}
	
	private void reset() {
		if (playerInformation != null) {
			playerInformation.clear();
			playerInformation = null;
		}
		totalTimeElapsed = 0;
		nextScreenCalled = false;
		players = null;
		backgroundId = -1;
		hasWon = false;
	}
	
	public void initialize(HashMap<PlayerNumber, Player> players) {
		reset();
		
		for (Player p : players.values())
			hasWon |= p.isAlive(); // At least one alive
		
		this.players = players;
		backgroundId = hasWon ? R.drawable.victory_bg : R.drawable.defeat_bg;
		initializeInfo();
		
		playerInformation.add(new Text(DisplaySettings.DISPLAY_WIDTH / 2,
				DisplaySettings.DISPLAY_HEIGHT - 50,
				"PRESS TO CONTINUE", 1));
	}

	private void initializeInfo() {
		playerInformation = new ArrayList<Text>();
		initializePlayerInfo(130, players.get(PlayerNumber.ONE));
		initializePlayerInfo(680, players.get(PlayerNumber.TWO));
		
	}

	private void initializePlayerInfo(int x, Player player) {
		
		int currentY = PLAYER_INFO_Y;
		
		playerInformation.add(new Text(x, currentY, "Player " + player.getNumber().toString(), 1.35f));
		currentY += PLAYER_INFO_Y_SPACING;

		playerInformation.add(new Text(x + PLAYER_INFO_X_OFFSET,
				currentY, "Coins: " + player.getCoins(), 1f));
		currentY += PLAYER_INFO_Y_SPACING;
		
		playerInformation.add(new Text(x + PLAYER_INFO_X_OFFSET,
				currentY, "Time: " + player.getTimeFinished() + "s", 1f));
		currentY += PLAYER_INFO_Y_SPACING;
		
		playerInformation.add(new Text(x + PLAYER_INFO_X_OFFSET,
				currentY, "Deaths: " + player.getDeathCount(), 1f));
		
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		// Background
		canvas.drawBitmap(backgroundId, 0, 0);
		
		// Draw Information
		for (int i = 0 ; i < playerInformation.size() ; i++)
			playerInformation.get(i).render(canvas, timeElapsed);
	}

	@Override
	public void step(float timeElapsed) {
		totalTimeElapsed += timeElapsed;
		if (totalTimeElapsed >= SECONDS_TO_WAIT)
			callNextScreen();
	}
	
	private boolean nextScreenCalled;

	private void callNextScreen() {
		if (!nextScreenCalled) {
			nextScreenCalled = true;
			
			// Reset GameWorld
			Game.getInstance().resetWorld();
			
			// Go To Main Menu
			Game.getInstance().goTo(GameState.MAIN_MENU);
			
		}
	}

	@Override
	public void input(InputEventHandler event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
			callNextScreen();
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void onPause() {
	}

}
