package com.arretadogames.pilot.screens;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.AraraAzul;
import com.arretadogames.pilot.entities.LoboGuara;
import com.arretadogames.pilot.entities.MacacoPrego;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.entities.TatuBola;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameMode;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.loading.FontSpecification;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.tournaments.TournamentManager;
import com.arretadogames.pilot.ui.GameButtonListener;

public class FinishRaceScreen extends GameScreen implements GameButtonListener {

	final FontSpecification titleFont = FontLoader.getInstance().getFont(FontTypeFace.ARIAN);
	private final float GAME_WIDTH = getDimension(R.dimen.screen_width);
	private final float GAME_HEIGHT = getDimension(R.dimen.screen_height);
	
	private final float STAR_OFFSET = 72;
	private final float STAR_HEIGHT = 334;
	private final float STAR_WIDTH_POS_P1 = 116;
	private final float STAR_WIDTH_POS_P2 = 407;
	
	private int starId;
	private int winnerProfile;	
	private int backgroundId;
	private int starShadowId;
	
	private int p1Score = 500;
	private int p2Score = 750;
	
	private int p1Victories = 0;
	private int p2Victories = 0;

	public FinishRaceScreen() {
		backgroundId = R.drawable.finishracebg;
		starId = R.drawable.star_plain;
		starShadowId = R.drawable.star_shadow;
	}
	
	public void setRaceWinner(PlayerNumber winner, Player player) {
		if (player instanceof MacacoPrego) {
			winnerProfile = R.drawable.macaco_profile;
		} else if (player instanceof TatuBola) {
			winnerProfile = R.drawable.tatu_profile;
		} else if (player instanceof LoboGuara) {
			winnerProfile = R.drawable.lobo_profile;
		} else if (player instanceof AraraAzul) {
			winnerProfile = R.drawable.arara_profile;
		}
		
		if (winner == PlayerNumber.ONE){
			TournamentManager.getInstance().countWins(PlayerNumber.ONE);
		} else {
			TournamentManager.getInstance().countWins(PlayerNumber.TWO);
		}
		
		if (Game.getInstance().getGameMode() == GameMode.TOURNAMENT) {
			p1Victories = TournamentManager.getInstance().getWins(PlayerNumber.ONE);
			p2Victories = TournamentManager.getInstance().getWins(PlayerNumber.TWO);
		}
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.drawBitmap(backgroundId, 0, 0, GAME_WIDTH, GAME_HEIGHT, 0,
				getDimension(R.dimen.main_menu_bg_extra_height));
		
		canvas.drawBitmap(winnerProfile, 302, 132, 200, 200);
		
		if (Game.getInstance().getGameMode() == GameMode.TOURNAMENT) {
			drawPlayerStars(p1Victories, STAR_WIDTH_POS_P1, canvas);
			drawPlayerStars(p2Victories, STAR_WIDTH_POS_P2, canvas);
		} 
		drawScore(canvas);
	}
	
	private void drawScore(GLCanvas canvas) {
		canvas.drawText("" + p1Score, 220f, 240f, titleFont, 1.2f, true, 0f);
		canvas.drawText("" + p2Score, 570f, 240f, titleFont, 1.2f, true, 0f);
	}

	private void drawPlayerStars(int numVictories, float starWidthPos, GLCanvas canvas) {
		int offset = 0;
		float startPos = starWidthPos;
		for (int i=0; i<4; i++) {
			if (i < numVictories) {
				canvas.drawBitmap(starId, startPos + offset, STAR_HEIGHT, 64, 64);
				offset += STAR_OFFSET;
			} else {
				canvas.drawBitmap(starShadowId, startPos + offset, STAR_HEIGHT, 64, 64);
				offset += STAR_OFFSET;
			}
		}
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub
	}

	@Override
	public void input(InputEventHandler event) {
		
		if (Game.getInstance().getGameMode() == GameMode.TOURNAMENT) {
			if (TournamentManager.getInstance().nextLevel())
				Game.getInstance().goTo(GameState.LEVEL_RESTART);

		} else {
			TournamentManager.getInstance().resetTournamentData();
			Game.getInstance().goTo(GameState.MAIN_MENU);
		}
	}

	@Override
	public void onPause() {
	}

	@Override
	public void onClick(int buttonId) {
// All functions here are ASYNCHRONOUS
//		switch (buttonId) {
//		case CONTINUE_BT:
//			hide();
//			break;
//		case RESTART_BT:
//			Game.getInstance().goTo(GameState.LEVEL_RESTART);
//			break;
//		case QUIT_BT:
//			if (Game.getInstance().getGameMode() == GameMode.TOURNAMENT)
//				TournamentManager.getInstance().resetTournamentData();
//			
//			Game.getInstance().goTo(GameState.MAIN_MENU);
//			break;
//		default:
//			break;
//		}
	}
}
