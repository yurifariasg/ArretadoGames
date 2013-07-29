package com.arretadogames.pilot.screens;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.Toast;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;

import com.arretadogames.pilot.GameActivity;
import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.entities.PlayableCharacter;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.AnimationManager;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.util.Util;
import com.arretadogames.pilot.world.GameWorld;

public class CharacterSelectionScreen extends GameScreen implements GameButtonListener {
	
	private final RectF BASE_RECT = new RectF(0, 0, 220, 220);
	
	private PlayerSelector[] selectors;
	private CharacterSpot[] spots;
	
	private ImageButton startButton;
	
	public CharacterSelectionScreen() {
		initializeSelectors();
		initializeSpots();
		
		// Initialize Button
		startButton = new ImageButton(R.drawable.bt_play_unselected,
				DisplaySettings.TARGET_WIDTH / 2, DisplaySettings.TARGET_HEIGHT / 2,
				this, R.drawable.bt_play_selected, R.drawable.bt_play_unselected);
		startButton.setX(startButton.getX() - startButton.getWidth() / 2);
		startButton.setY(startButton.getY() - startButton.getHeight() / 2);
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
		spots[1].selector = selectors[1];
		selectors[1].spot = spots[1];
		selectors[1].selectorRect = new RectF(spots[1].rect);
		
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
		
		for (int i = 0 ; i < selectors.length ; i++)
			selectors[i].render(canvas, timeElapsed);
		
		startButton.render(canvas, timeElapsed);
	}

	@Override
	public void step(float timeElapsed) {
	}

	// TODO: Enable Multi touch
	@Override
	public void input(InputEventHandler event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN ||
				action == MotionEvent.ACTION_MOVE) {
			pressButtons(event.getX(), event.getY(), true, event.getEvent().getPointerId(0));
		} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
			
			final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) 
	                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	        final int pointerId = event.getEvent().getPointerId(pointerIndex);
			pressButtons(event.getX(), event.getY(), false, pointerId);
		}
		
		startButton.input(event);
	}

	private void pressButtons(float x, float y, boolean pressed, int pointerId) {
		
		for (int i = 0 ; i < selectors.length ; i++) {
			if (selectors[i].collides(x, y) && selectors[i].hasCapturedPointer(pointerId)) {
				selectors[i].touch(x, y, !pressed, pointerId);
				return;
			}
		}
		
		for (int i = 0 ; i < selectors.length ; i++) {
			if (selectors[i].collides(x, y)) {
				selectors[i].touch(x, y, !pressed, pointerId);
				return;
			}
		}
	}


	@Override
	public void onBackPressed() {
	}

	@Override
	public void onPause() {
	}
	
	
	private class PlayerSelector implements Renderable, TweenAccessor<PlayerSelector> {
		
		private static final int ZOOM = 0;
		private static final int ROTATION = 1;
		private static final int POSITION = 2;
		
		PlayerNumber player;
		CharacterSpot spot;
		RectF selectorRect;
		int pointerId;
		
		// Animation Attr (current zoom and rotation)
		private float cZoom;
		private float cRotation;
		private Tween rotationTween;
		
		
		boolean isPlaced;
		
		public PlayerSelector() {
			isPlaced = true;
			cZoom = 1;
			cRotation = 0;
			
			final PlayerSelector itself = this;
			
			rotationTween = Tween.call(new TweenCallback() {
				
				@Override
				public void onEvent(int arg0, BaseTween<?> arg1) {
					Tween.to(itself, PlayerSelector.ROTATION, 0.1f).targetRelative(10f).start(AnimationManager.getInstance());
				}
			}).repeat(Tween.INFINITY, 0.1f);
		}
		
		public boolean collides(float x, float y) {
			return selectorRect.contains(x, y);
		}

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
			
//			if (isPlaced) {
//				canvas.drawBitmap(imageId, selectorRect, false);
//			} else {
				canvas.saveState();
				canvas.translate(selectorRect.centerX(), selectorRect.centerY());
				canvas.rotate(cRotation % 360);
				canvas.translate(-selectorRect.centerX(), -selectorRect.centerY());
				canvas.scale(cZoom, cZoom, selectorRect.centerX(), selectorRect.centerY());
				canvas.drawBitmap(imageId, selectorRect, false);
				
				canvas.restoreState();
//			}
		}
		
		public boolean hasCapturedPointer(int pointerId) {
			return this.pointerId == pointerId;
		}
		
		public void touch(float x, float y, boolean toRelease, int pointerId) {
			
			if (this.pointerId == -1)
				this.pointerId = pointerId;
			if (this.pointerId != pointerId)
				return;
			
			// Update Position
			selectorRect.set(x - selectorRect.width() / 2, y - selectorRect.height() / 2,
					x + selectorRect.width() / 2, y + selectorRect.height() / 2);
			
			if (!isPlaced && toRelease)
				startReleaseAnimation();
			if (isPlaced && !toRelease)
				startPressAnimation();
			
			isPlaced = toRelease;
			updateSpots();
			
			if (isPlaced) {
				// Find Closest Spot
				CharacterSpot newSpot = findClosestSpot(x, y);
				spot.selector = null;
				spot = newSpot;
				spot.selector = this;
				this.pointerId = -1;
				
				Tween.to(this, PlayerSelector.POSITION, 0.2f).target(spot.rect.centerX(), spot.rect.centerY()).start(AnimationManager.getInstance());
//				selectorRect.set(spot.rect);
				
			}
		}

		public void startPressAnimation() {
			Tween.to(this, PlayerSelector.ZOOM, 0.25f).target(0.3f).start(AnimationManager.getInstance());
			if (rotationTween.isStarted()) {
				rotationTween.resume();
			} else {
				rotationTween.start(AnimationManager.getInstance());
			}
		}

		public void startReleaseAnimation() {
			Tween.to(this, PlayerSelector.ZOOM, 0.25f).target(1f).start(AnimationManager.getInstance());
			rotationTween.pause();
			Tween.to(this, PlayerSelector.ROTATION, 0.1f).target(0f).start(AnimationManager.getInstance());
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

		private void updateSpots() {
			
		}

		@Override
		public int getValues(PlayerSelector pSelector, int type, float[] returnValues) {
			
			switch (type) {
			case PlayerSelector.ZOOM:
				returnValues[0] = pSelector.cZoom;
				return 1;
			case PlayerSelector.ROTATION:
				returnValues[0] = pSelector.cRotation;
				return 1;
			case PlayerSelector.POSITION:
				returnValues[0] = pSelector.selectorRect.centerX();
				returnValues[1] = pSelector.selectorRect.centerY();
				return 2;
			}
			return 0;
		}

		@Override
		public void setValues(PlayerSelector pSelector, int type, float[] newValues) {
			switch (type) {
			case PlayerSelector.ZOOM:
				pSelector.cZoom = newValues[0];
				break;
			case PlayerSelector.ROTATION:
				pSelector.cRotation = newValues[0];
				break;
			case PlayerSelector.POSITION:
				RectF oldR = new RectF(pSelector.selectorRect);
				pSelector.selectorRect.set(
						newValues[0] - oldR.width() / 2,
						newValues[1] - oldR.height() / 2,
						newValues[0] + oldR.width() / 2,
						newValues[1] + oldR.height() / 2);
				break;
			}
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
				imageId = R.drawable.selection_anonymous;
				break;
			case MACACO_PREGO:
				imageId = R.drawable.selection_anonymous;
				break;
			default:
				System.out.println("No ImageId.. ERROR!");
			}
			
			canvas.drawBitmap(imageId, rect, false);
		}
	}

	@Override
	public void onClick(int buttonId) {
		// No need to check buttonId, we only have startButton
		
		
		HashMap<PlayerNumber, PlayableCharacter> selectedCharacters = new HashMap<PlayerNumber, PlayableCharacter>();
		for (PlayerSelector selector : selectors) {
			if (selector.spot.character == PlayableCharacter.TATU_BOLA ||
					selector.spot.character == PlayableCharacter.MACACO_PREGO) {
				showInvalidCharacterMessage();
				return;
			}
			selectedCharacters.put(selector.player, selector.spot.character);
		}
		
		((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).setSelectedCharacters(selectedCharacters);
		((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).initialize();
		Game.getInstance().goTo(GameState.RUNNING_GAME);
		
		
		
	}

	private void showInvalidCharacterMessage() {
		Toast.makeText(GameActivity.getContext(), "Selected character not available", Toast.LENGTH_SHORT).show();
	}

}
