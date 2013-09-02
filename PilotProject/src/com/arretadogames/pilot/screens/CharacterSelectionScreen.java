package com.arretadogames.pilot.screens;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.RectF;
import android.widget.Toast;
import aurelienribon.tweenengine.TweenAccessor;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.entities.PlayableCharacter;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.util.Util;
import com.arretadogames.pilot.world.GameWorld;

public class CharacterSelectionScreen extends GameScreen implements GameButtonListener {
	
	private final RectF BASE_RECT = new RectF(0, 0, 220, 220);
	private PlayerSelector[] selectors;
	private CharacterSpot[] spots;
	private ImageButton selectButton1;
	private ImageButton selectButton2;
	private boolean isPlayerOne;

	public CharacterSelectionScreen() {
		isPlayerOne = true;
		initializeSelectors();
		initializeSpots();
		
		// Initialize Button1
		selectButton1 = new ImageButton(R.drawable.player1,
			DisplaySettings.TARGET_WIDTH / 2, DisplaySettings.TARGET_HEIGHT / 2,
			this, R.drawable.player1, R.drawable.player1);
		selectButton1.setX(selectButton1.getX() - selectButton1.getWidth() / 2);
		selectButton1.setY(selectButton1.getY() - selectButton1.getHeight() / 2);

		// Initialize Button2
		selectButton2 = new ImageButton(R.drawable.player2,
			DisplaySettings.TARGET_WIDTH / 2, DisplaySettings.TARGET_HEIGHT / 2,
			this, R.drawable.player2, R.drawable.player2);
		selectButton2.setX(selectButton2.getX() - selectButton2.getWidth() / 2);
		selectButton2.setY(selectButton2.getY() - selectButton2.getHeight() / 2);
	}
	
	private void initializeSelectors() {
		selectors = new PlayerSelector[2];
		
		selectors[0] = new PlayerSelector();
		selectors[0].player = PlayerNumber.ONE;

		selectors[1] = new PlayerSelector();
		selectors[1].player = PlayerNumber.TWO;
	}
	
	@SuppressLint("NewApi")
	private void initializeSpots() {
		
		final float CENTER_X = DisplaySettings.TARGET_WIDTH / 2;
		final float CENTER_Y = DisplaySettings.TARGET_HEIGHT / 2;
		
		spots = new CharacterSpot[4];
		
		spots[0] = new CharacterSpot();
		spots[0].character = PlayableCharacter.LOBO_GUARA;
		spots[0].rect = new RectF(BASE_RECT);
		spots[0].rect.left = CENTER_X - BASE_RECT.width() - 20;
		spots[0].rect.right = spots[0].rect.left + BASE_RECT.width();
		spots[0].rect.top = CENTER_Y - BASE_RECT.height() - 20;
		spots[0].rect.bottom = spots[0].rect.top + BASE_RECT.height();
		spots[0].selector = selectors[0];
		selectors[0].spot = spots[0];
		selectors[0].selectorRect = new RectF(spots[0].rect);

		spots[1] = new CharacterSpot();
		spots[1].character = PlayableCharacter.ARARA_AZUL;
		spots[1].rect = new RectF(BASE_RECT);
		spots[1].rect.left = CENTER_X + 20;
		spots[1].rect.right = spots[1].rect.left + BASE_RECT.width();
		spots[1].rect.top = CENTER_Y - BASE_RECT.height() - 20;
		spots[1].rect.bottom = spots[1].rect.top + BASE_RECT.height();
		
		spots[2] = new CharacterSpot();
		spots[2].character = PlayableCharacter.TATU_BOLA;
		spots[2].rect = new RectF(BASE_RECT);
		spots[2].rect.left = CENTER_X - BASE_RECT.width() - 20;
		spots[2].rect.right = spots[2].rect.left + BASE_RECT.width();
		spots[2].rect.top = CENTER_Y + 20;
		spots[2].rect.bottom = spots[2].rect.top + BASE_RECT.height();
		
		spots[3] = new CharacterSpot();
		spots[3].character = PlayableCharacter.MACACO_PREGO;
		spots[3].rect = new RectF(BASE_RECT);
		spots[3].rect.left = CENTER_X + 20;
		spots[3].rect.right = spots[3].rect.left + BASE_RECT.width();
		spots[3].rect.top = CENTER_Y + 20;
		spots[3].rect.bottom = spots[3].rect.top + BASE_RECT.height();
	}
	
	private void initSecondPlayer(){
		if (selectors[0].spot == spots[0]){
			selectors[1].spot = spots[1];
			selectors[1].selectorRect = new RectF(spots[1].rect);
		}else{
			selectors[1].spot = spots[0];
			selectors[1].selectorRect = new RectF(spots[0].rect);
		}
	}
	
	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		for (int i = 0 ; i < spots.length ; i++)
			spots[i].render(canvas, timeElapsed);
		
		if (isPlayerOne){
			selectors[0].render(canvas, timeElapsed);
			selectButton1.render(canvas, timeElapsed);
		}else{
			selectors[0].render(canvas, timeElapsed);
			selectors[1].render(canvas, timeElapsed);
			selectButton2.render(canvas, timeElapsed);
		}
	}

	@Override
	public void step(float timeElapsed) {
	}

	@Override
	public void input(InputEventHandler event) {
		if (isPlayerOne){
			if (!selectButton1.input(event)){
				selectors[0].touch(event.getX(), event.getY());
			}
			
		}else{
			if (!selectButton2.input(event)){
				selectors[1].touch(event.getX(), event.getY());
			}
		}
	}

	@Override
	public void onPause() {
	}	
	
	private class PlayerSelector implements Renderable, TweenAccessor<PlayerSelector> {
		
		PlayerNumber player;
		CharacterSpot spot;
		RectF selectorRect;

		@Override
		public void render(GLCanvas canvas, float timeElapsed) {
			int imageId = -1;
			switch (player) {
			case ONE:
					imageId = R.drawable.blue_selector;
				break;
			case TWO:
					imageId = R.drawable.red_selector;
				break;
			}

			canvas.saveState();
			if (selectorRect != null)
				canvas.drawBitmap(imageId, selectorRect, false);
			canvas.restoreState();
		}
		
		public void touch(float x, float y) {
			CharacterSpot newSpot = findClosestSpot(x, y);
			selectorRect.set(newSpot.rect);
			spot.selector = null;
			spot = newSpot;
			spot.selector = this;
		}

		private CharacterSpot findClosestSpot(float x, float y) {

			CharacterSpot closestSpot = spot;
			float closestDistance = Util.distance(x, y, closestSpot.rect.centerX(), closestSpot.rect.centerY());
			for (int i = 0 ; i < spots.length ; i++) {
				if (closestDistance > Util.distance(x, y, spots[i].rect.centerX(), spots[i].rect.centerY()) &&
				spots[i].isAvailable()) {
					closestSpot = spots[i];
					closestDistance = Util.distance(x, y, spots[i].rect.centerX(), spots[i].rect.centerY());
				}
			}
			return closestSpot;
		}

		@Override
		public int getValues(PlayerSelector pSelector, int arg1, float[] returnValues) {
			returnValues[0] = pSelector.selectorRect.centerX();
			returnValues[1] = pSelector.selectorRect.centerY();
			return 0;
		}

		@Override
		public void setValues(PlayerSelector pSelector, int arg1, float[] newValues) {
			RectF oldR = new RectF(pSelector.selectorRect);
			pSelector.selectorRect.set(
			newValues[0] - oldR.width() / 2,
			newValues[1] - oldR.height() / 2,
			newValues[0] + oldR.width() / 2,
			newValues[1] + oldR.height() / 2);
		}
	}
	
	private class CharacterSpot implements Renderable {
		
		RectF rect;
		PlayerSelector selector = null;
		PlayableCharacter character;
		
		public boolean isAvailable() {
			return (selector == null);
		}
		
		@Override
		public void render(GLCanvas canvas, float timeElapsed) {
			int imageId = -1;
			switch (character) {
			case LOBO_GUARA:
				imageId = R.drawable.selection_lobo_guara;
				break;
			case ARARA_AZUL:
				imageId = R.drawable.selection_arara_azul;
				break;
			case TATU_BOLA:
				imageId = R.drawable.tatuzinlindinmininin;
				break;
			case MACACO_PREGO:
				imageId = R.drawable.macacopregotxutxu;
				break;
			default:
				System.out.println("No ImageId.. ERROR!");
			}
			
			canvas.drawBitmap(imageId, rect, false);
		}
	}
	
	public void resetSelections(){
		isPlayerOne = true;
		initializeSelectors();
		initializeSpots();
	}
	
	@Override
	public void onClick(int buttonId) {
		
		if (isPlayerOne){
			initSecondPlayer();
			isPlayerOne = false;
		}else{
			HashMap<PlayerNumber, PlayableCharacter> selectedCharacters = new HashMap<PlayerNumber, PlayableCharacter>();
			
			for (PlayerSelector selector : selectors) {
				selectedCharacters.put(selector.player, selector.spot.character);
			}
			
			((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).setSelectedCharacters(selectedCharacters);
			((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).initialize();
			Game.getInstance().goTo(GameState.RUNNING_GAME);
		}		
		
	}

}
