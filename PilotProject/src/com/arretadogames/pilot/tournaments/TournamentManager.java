package com.arretadogames.pilot.tournaments;

import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.levels.LevelManager;
import com.arretadogames.pilot.screens.PremiationScreen;
import com.arretadogames.pilot.world.GameWorld;

public class TournamentManager {

	private static TournamentManager tManager;

	private int winsNumberP1;
	private int winsNumberP2;
	private int p1TotalScore;
	private int p2TotalScore;
	private int currentLevel;

	private TournamentManager() {
		currentLevel = 0;
		winsNumberP1 = 0;
		winsNumberP2 = 0;
		p1TotalScore = 0;
		p2TotalScore = 0;
	}

	/**
	 * Gets the only and single instance of Tournament
	 * 
	 * @return TournamentManager
	 */
	public static TournamentManager getInstance() {
		if (tManager == null)
			tManager = new TournamentManager();
		return tManager;
	}
	
	public void resetTournamentData(){
		winsNumberP1 = 0;
		winsNumberP2 = 0;
		p1TotalScore = 0;
		p2TotalScore = 0;
		currentLevel = 0;
	}
	
	public int getWins(PlayerNumber pNumber){
		switch (pNumber) {
		case ONE:
			return winsNumberP1; 
		case TWO:
			return winsNumberP2;
		default:
			return 0;
		}
	}
	
	public void countWins(PlayerNumber pNumber) {
		switch (pNumber) {
		case ONE:
			winsNumberP1++;
			break;

		case TWO:
			winsNumberP2++;
			break;
		default:
			break;
		}
	}
	
	public void countScore(PlayerNumber pNumber, int score) {
		switch (pNumber) {
		case ONE:
			p1TotalScore += score;
			break;

		case TWO:
			p2TotalScore += score;
			break;
		default:
			break;
		}
	}
	
	public String getWinner(){
		if (winsNumberP1 > winsNumberP2) {
			return "P1 wins!";
		} else if (winsNumberP1 < winsNumberP2){
			return "P2 wins!";
		} else {
			return "P1 & P2 wins!";
		}
	}

	/**
	 * Change to the next Level of the tournament selected
	 * 
	 * @return Returns true if has a next level
	 */
	public boolean nextLevel() {
		
		if (winsNumberP1 == 4 || winsNumberP2 == 4) {
			((PremiationScreen) Game.getInstance().getScreen(GameState.PREMIATION))
			.updateWinner();
			Game.getInstance().goTo(GameState.PREMIATION);
			currentLevel = 0;
			return false;
		}
		
		if (currentLevel < 5 ) {
			currentLevel++;
			
			((GameWorld) Game.getInstance().getScreen(GameState.RUNNING_GAME))
			.setLevel(LevelManager.getLevels().get(currentLevel));
			return true;
		} else {
			((PremiationScreen) Game.getInstance().getScreen(GameState.PREMIATION))
			.updateWinner();
			Game.getInstance().goTo(GameState.PREMIATION);
			currentLevel = 0;
		}
		return false;	
	}
}
