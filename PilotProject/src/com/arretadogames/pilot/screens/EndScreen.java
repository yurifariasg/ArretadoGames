package com.arretadogames.pilot.screens;

import android.graphics.Paint;
import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameMode;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.tournaments.TournamentManager;
import com.arretadogames.pilot.ui.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EndScreen extends GameScreen {

	private static final int PLAYER_INFO_Y = 130;
	private static final int PLAYER_INFO_Y_SPACING = 50;
	private static final int PLAYER_INFO_X_OFFSET = 0;
	private static final int SECONDS_TO_WAIT = 8;

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

	public void initialize(HashMap<PlayerNumber, Player> players, LevelDescriptor ld) {
		reset();

		for (Player p : players.values())
			hasWon |= p.isAlive(); // At least one alive

		this.players = players;
		backgroundId = hasWon ? R.drawable.victory_bg : R.drawable.defeat_bg;
		initializeInfo();

		playerInformation.add(new Text(GameSettings.TARGET_WIDTH / 2,
				GameSettings.TARGET_HEIGHT - 50,
				"PRESS TO CONTINUE",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, true));
	}

	private void initializeInfo() {
		playerInformation = new ArrayList<Text>();
		Player p1 = players.get(PlayerNumber.ONE);
		Player p2 = players.get(PlayerNumber.TWO);

		AccountManager.get().saveState();

		initializePlayerInfo(140, p1);
		initializePlayerInfo(660, p2);
		
		if (p1.getTimeFinished() < p2.getTimeFinished()){
			TournamentManager.getInstance().countWins(PlayerNumber.ONE);
		} else {
			TournamentManager.getInstance().countWins(PlayerNumber.TWO);
		}
	}

	private void initializePlayerInfo(int x, Player player) {

		int currentY = PLAYER_INFO_Y;

		playerInformation.add(new Text(x, currentY, "Player " + player.getNumber().toString(),
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1f, true));
		currentY += PLAYER_INFO_Y_SPACING;

		playerInformation.add(new Text(x + PLAYER_INFO_X_OFFSET,
				currentY, "Time: " + player.getTimeFinished() + "s",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, true));
		currentY += PLAYER_INFO_Y_SPACING;

	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		// Background
		canvas.drawBitmap(backgroundId, 0, 0, getDimension(R.dimen.screen_width), getDimension(R.dimen.screen_height),
		        0, getDimension(R.dimen.main_menu_bg_extra_height));

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

			if (Game.getInstance().getGameMode() == GameMode.QUICKRACE)
				Game.getInstance().goTo(GameState.MAIN_MENU);// Go To Main Menu
			
			else {
				
				if (TournamentManager.getInstance().nextLevel()){
					Game.getInstance().goTo(GameState.RUNNING_GAME);
				} else {
					//Premiation
					Game.getInstance().goTo(GameState.PREMIATION);// Go To Main Menu
				}

			}
		}
	}

	@Override
	public void input(InputEventHandler event) {
		if (event.getAction() == MotionEvent.ACTION_UP)
			callNextScreen();
	}

	@Override
	public void onPause() {
	}

}
