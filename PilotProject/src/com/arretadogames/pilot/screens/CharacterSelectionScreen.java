package com.arretadogames.pilot.screens;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.RectF;
import android.view.MotionEvent;
import aurelienribon.tweenengine.TweenAccessor;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.entities.PlayableCharacter;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.world.GameWorld;

public class CharacterSelectionScreen extends GameScreen{// implements GameButtonListener {
	
	private final RectF BASE_RECT = new RectF(0, 0, 220, 220);
	private PlayerSelector[] selectors;
	private CharacterSpot[] spots;
	private boolean isPlayerOne;
	private final int playerImgSize[] = ImageLoader.checkBitmapSize(R.drawable.player1);
	
	private float imgPlayerWidth = DisplaySettings.TARGET_WIDTH / 2 - (playerImgSize[0]/2);
	private float imgPlayerHeight = DisplaySettings.TARGET_HEIGHT / 2 - (playerImgSize[1]/2);;

	public CharacterSelectionScreen() {
		isPlayerOne = true;
		initializeSelectors();
		initializeSpots();
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
		selectors[0].selectorRect = new RectF();
		
		spots[1] = new CharacterSpot();
		spots[1].character = PlayableCharacter.ARARA_AZUL;
		spots[1].rect = new RectF(BASE_RECT);
		spots[1].rect.left = CENTER_X + 20;
		spots[1].rect.right = spots[1].rect.left + BASE_RECT.width();
		spots[1].rect.top = CENTER_Y - BASE_RECT.height() - 20;
		spots[1].rect.bottom = spots[1].rect.top + BASE_RECT.height();
		selectors[1].selectorRect = new RectF();
		
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
	
	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		for (int i = 0 ; i < spots.length ; i++)
			spots[i].render(canvas, timeElapsed);
		
		if (isPlayerOne){
			selectors[0].render(canvas, timeElapsed);
			canvas.drawBitmap(R.drawable.player1, imgPlayerWidth, imgPlayerHeight);
		}else{
			selectors[0].render(canvas, timeElapsed);
			selectors[1].render(canvas, timeElapsed);
			canvas.drawBitmap(R.drawable.player2, imgPlayerWidth, imgPlayerHeight);
		}
	}

	@Override
	public void step(float timeElapsed) {
	}

	@Override
	public void input(InputEventHandler event) {
		if (isPlayerOne){
			if (event.getAction()== MotionEvent.ACTION_UP){
				if (selectors[0].touch(event.getX(), event.getY()))
					isPlayerOne = false;
			}
			
		}else{
			if (event.getAction()== MotionEvent.ACTION_UP){
				if (selectors[1].touch(event.getX(), event.getY()))
					initGame();
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
		
		public boolean touch(float x, float y) {
			CharacterSpot newSpot = getSpotAt(x, y);
			if (newSpot != null){
				selectorRect.set(newSpot.rect);
				spot = newSpot;
				spot.selector = this;
				return true;
			}
			return false;
		}

		private CharacterSpot getSpotAt(float x, float y){
			for (int i = 0; i < spots.length ; i++) {
				if (spots[i].rect.contains(x, y) && spots[i].isAvailable()){
					return spots[i];
				}
			}
			return null;
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
				imageId = R.drawable.selection_tatu_bola;
				break;
			case MACACO_PREGO:
				imageId = R.drawable.macacopregotxutxu;
				break;
			default:
				System.out.println("No ImageId ERROR!");
			}
			
			canvas.drawBitmap(imageId, rect, false);
		}
	}
	
	public void resetSelections(){
		isPlayerOne = true;
		initializeSelectors();
		initializeSpots();
	}
	
	
	private void initGame(){
		HashMap<PlayerNumber, PlayableCharacter> selectedCharacters = new HashMap<PlayerNumber, PlayableCharacter>();
		
		for (PlayerSelector selector : selectors) {
			selectedCharacters.put(selector.player, selector.spot.character);
		}
		
		((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).setSelectedCharacters(selectedCharacters);
		((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).initialize();
		Game.getInstance().goTo(GameState.RUNNING_GAME);
	}

}
