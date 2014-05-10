package com.arretadogames.pilot.screens;

import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.tournaments.TournamentManager;
import com.arretadogames.pilot.util.Assets;

public class PremiationScreen extends GameScreen {
	
	private String winner = ""; 
	
	public PremiationScreen() {
	}
	
	public void updateWinner() {
		this.winner = TournamentManager.getInstance().getWinner();
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.drawText(this.winner,
				getDimension(R.dimen.screen_width)/2,
				getDimension(R.dimen.screen_height)/2,
				FontLoader.getInstance().getFont(FontTypeFace.ARIAN),
				2, true);
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub
	}

	@Override
	public void input(InputEventHandler event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Assets.mainMenuMusic.play();
			Game.getInstance().goTo(GameState.MAIN_MENU);
			TournamentManager.getInstance().resetTournamentData();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
	}

}
