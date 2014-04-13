package com.arretadogames.pilot.tournaments;

import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.levels.LevelManager;
import com.arretadogames.pilot.world.GameWorld;

public class TournamentManager {

	private static TournamentManager tManager;

//	private Tournament mTournament;
	private int winsNumberP1;
	private int winsNumberP2;
	private int currentLevel;

	private TournamentManager() {

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
		currentLevel = 0;
	}
	
	public void countWins(PlayerNumber pNumber){
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
	
	public String getWinner(){
		if (winsNumberP1 > winsNumberP2) {
			return "P1 wins";
		} else if (winsNumberP1 < winsNumberP2){
			return "P2 wins";
		} else {
			return "P1 and P2 wins!!!";
		}
	}

	/**
	 * Change to the next Level of the tournament selected
	 * 
	 * @return Returns true if has a next level
	 */
	public boolean nextLevel() {
		if (currentLevel < 6) {
			((GameWorld) Game.getInstance().getScreen(GameState.RUNNING_GAME))
			.setLevel(LevelManager.getLevels().get(currentLevel));
			
			currentLevel++;
			return true;
		} else {
			
			currentLevel = 0;
			System.out.println("Who wins the tournament!?!");
			//TODO goto the premiation screen with the winner and that kind of stuff
		}
		return false;	
	}
}
