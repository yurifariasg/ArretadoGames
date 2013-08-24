package com.arretadogames.pilot.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Paint;
import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.accounts.Account;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.database.GameDatabase;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.levels.LevelDescriptor;
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
	private LevelDescriptor ld;
	
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
	
	public void initialize(HashMap<PlayerNumber, Player> players, LevelDescriptor ld) {
		reset();
		
		this.ld = ld;
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
		Player p1 = players.get(PlayerNumber.ONE);
		Player p2 = players.get(PlayerNumber.TWO);
		
		//Setando melhor quantia de moedas
		if (p2.getCoins() < p1.getCoins()){
			if (ld.getBestCoins() < p1.getCoins()){
				ld.setBestCoins(p1.getCoins());
				GameDatabase.getInstance().setBestCoins(p1.getCoins(), ld.getId(), 1/*p1.getId()*/);
			}			
			
		}else{
			if (ld.getBestCoins() < p2.getCoins()){
				ld.setBestCoins(p2.getCoins());
				GameDatabase.getInstance().setBestCoins(p2.getCoins(), ld.getId(), 2/*p2.getId()*/);
			}
		}
		
		System.out.println("P1 Coins: " + p1.getCoins());
		Account acc1 = AccountManager.get().getAccount1();
		acc1.setCoins(acc1.getCoins() + p1.getCoins());
		Account acc2 = AccountManager.get().getAccount2();
		acc2.setCoins(acc2.getCoins() + p2.getCoins());
		AccountManager.get().saveState();
		
		initializePlayerInfo(130, p1);
		initializePlayerInfo(680, p2);
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
	public void onPause() {
	}

}
