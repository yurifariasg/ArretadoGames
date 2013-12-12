package com.arretadogames.pilot.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.RectF;
import android.view.MotionEvent;
import aurelienribon.tweenengine.TweenAccessor;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.database.GameDatabase;
import com.arretadogames.pilot.entities.PlayableCharacter;
import com.arretadogames.pilot.entities.PlayableItem;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.items.ItemType;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ToggleButton;
import com.arretadogames.pilot.world.GameWorld;

public class CharacterSelectionScreen extends GameScreen implements GameButtonListener {
	
	private final RectF BASE_RECT = new RectF(0, 0, 170, 170);
//	private final RectF BASE_ITEM_RECT = new RectF(0, 0, 80, 80);
	
	private PlayerSelector[] selectors;
	private CharacterSpot[] spots;
	
	private ToggleButton[] itemsButtons;
	
	private boolean isPlayerOne;
	private final int playerImgSize[] = ImageLoader.checkBitmapSize(R.drawable.player1);
	
	private float imgPlayerWidth = GameSettings.TARGET_WIDTH / 2 - (playerImgSize[0]/2);
	private float imgPlayerHeight = GameSettings.TARGET_HEIGHT / 2 - (playerImgSize[1]/2);
	private boolean[] possibleItems;;

	public CharacterSelectionScreen() {
		isPlayerOne = true;
		
		initializeSelectors();
		initializeSpots();
		initializeItems();
	}
	
	@Override
		public void onUnloading() {
			resetSelections();
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
		
		final float CENTER_X = GameSettings.TARGET_WIDTH / 2;
		final float CENTER_Y = GameSettings.TARGET_HEIGHT / 2;
		
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
	
	private void initializeItems(){
		
		
		itemsButtons = new ToggleButton[8];
		
		itemsButtons[0] = new ToggleButton(0, 8, 58, this, R.drawable.it_bg_selected, R.drawable.it_bg_unselected);
		itemsButtons[1] = new ToggleButton(1, 8, 148, this, R.drawable.it_bg_selected, R.drawable.it_bg_unselected);
		itemsButtons[2] = new ToggleButton(2, 8, 238, this, R.drawable.it_bg_selected, R.drawable.it_bg_unselected);
		itemsButtons[3] = new ToggleButton(3, 8, 328, this, R.drawable.it_bg_selected, R.drawable.it_bg_unselected);
		itemsButtons[4] = new ToggleButton(4, 712, 58, this, R.drawable.it_bg_selected, R.drawable.it_bg_unselected);
		itemsButtons[5] = new ToggleButton(5, 712, 148, this, R.drawable.it_bg_selected, R.drawable.it_bg_unselected);
		itemsButtons[6] = new ToggleButton(6, 712, 238, this, R.drawable.it_bg_selected, R.drawable.it_bg_unselected);
		itemsButtons[7] = new ToggleButton(7, 712, 328, this, R.drawable.it_bg_selected, R.drawable.it_bg_unselected);
	}
	
	private int getItemIconById(int id){
		
		if (id == 0 || id == 4){
			return R.drawable.it_superjump;
		}else if (id == 1 || id == 5){
			return R.drawable.it_strength;
		}else if (id == 2 || id == 6){
			return R.drawable.it_speed;
		}else if (id == 3 || id == 7){
			return R.drawable.it_double_jump;
		}
		
		return -1;
	}
	
	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		possibleItems = new boolean[8];
		possibleItems[0] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_JUMP) > 0; 
		possibleItems[1] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_STRENGHT) > 0;
		possibleItems[2] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_VELOCITY) > 0;
		possibleItems[3] = GameDatabase.getInstance().getQuantItems(ItemType.DOUBLE_JUMP) > 0;
		possibleItems[4] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_JUMP) > 0; 
		possibleItems[5] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_STRENGHT) > 0;
		possibleItems[6] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_VELOCITY) > 0;
		possibleItems[7] = GameDatabase.getInstance().getQuantItems(ItemType.DOUBLE_JUMP) > 0;
		
		canvas.drawBitmap(R.drawable.bg_select_chars, 0, 0);
		canvas.drawBitmap(R.drawable.bg_platforms_chars, 0, 0);
		
		for (int i = 0 ; i < spots.length ; i++)
			spots[i].render(canvas, timeElapsed);
		
		for (int j = 0; j < itemsButtons.length; j++){
			if( possibleItems[j] ) itemsButtons[j].render(canvas, timeElapsed);
			
			if( possibleItems[j] ) canvas.drawBitmap(getItemIconById(j), itemsButtons[j].getX(), itemsButtons[j].getY());
		}
		
		
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
		
		for (int i = 0; i < itemsButtons.length; i++) {
			itemsButtons[i].input(event);
		}
		
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
				canvas.drawBitmap(imageId, selectorRect);
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
				imageId = R.drawable.selection_macaco_prego;
				break;
			default:
				System.out.println("No ImageId ERROR!");
			}
			
			canvas.drawBitmap(imageId, rect);
		}
	}
	
	public void resetSelections(){
		isPlayerOne = true;
		initializeSelectors();
		initializeSpots();
	}
	
	private void initGame(){
		HashMap<PlayerNumber, PlayableCharacter> selectedCharacters = new HashMap<PlayerNumber, PlayableCharacter>();
		HashMap<PlayerNumber, List<PlayableItem>> selectedItems = new HashMap<PlayerNumber, List<PlayableItem>>();
		
		for (PlayerSelector selector : selectors) {
			selectedCharacters.put(selector.player, selector.spot.character);
		}
		
		List<PlayableItem> itemListp1 = new ArrayList<PlayableItem>();
		List<PlayableItem> itemListp2 = new ArrayList<PlayableItem>();
		
		for (int i = 0; i < itemsButtons.length; i++) {
			if (i < 4){
				if (itemsButtons[i].isToggled()){
					itemListp1.add(PlayableItem.forInt(i));
					selectedItems.put(PlayerNumber.ONE, itemListp1);
				}
			}else{
				if (itemsButtons[i].isToggled())
					itemListp2.add(PlayableItem.forInt(i-4));
					selectedItems.put(PlayerNumber.TWO, itemListp2);
			}				
		}		
		
		((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).setSelectedCharacters(selectedCharacters);
		((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).setSelectedItems(selectedItems);
		((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).initialize();
		Game.getInstance().goTo(GameState.RUNNING_GAME);
	}
	
	@Override
	public void onBackPressed() {
		Game.getInstance().goTo(GameState.LEVEL_SELECTION);
	}

	@Override
	public void onClick(int buttonId) {
		possibleItems = new boolean[8];
		possibleItems[0] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_JUMP) > 0; 
		possibleItems[1] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_STRENGHT) > 0;
		possibleItems[2] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_VELOCITY) > 0;
		possibleItems[3] = GameDatabase.getInstance().getQuantItems(ItemType.DOUBLE_JUMP) > 0;
		possibleItems[4] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_JUMP) > 0; 
		possibleItems[5] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_STRENGHT) > 0;
		possibleItems[6] = GameDatabase.getInstance().getQuantItems(ItemType.SUPER_VELOCITY) > 0;
		possibleItems[7] = GameDatabase.getInstance().getQuantItems(ItemType.DOUBLE_JUMP) > 0;
		if (buttonId < 4){
			for(int i = 0; i < 4; i++){
				if (i != buttonId)
					itemsButtons[i].setToggled(false);
			}
		}else {
			for(int i = 4; i < 8; i++){
				if (i != buttonId)
					itemsButtons[i].setToggled(false);
			}
		}
	}

}
