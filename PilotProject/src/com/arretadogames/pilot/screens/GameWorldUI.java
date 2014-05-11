package com.arretadogames.pilot.screens;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES11;
import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.GameHUDButton;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.world.GameWorld;

import javax.microedition.khronos.opengles.GL10;

public class GameWorldUI extends GameScreen implements GameButtonListener {

	public static final int BT_PLAYER_1_JUMP = 1;
	public static final int BT_PLAYER_1_ACT = 2;
	public static final int BT_PLAYER_1_ITEM = 4;
	public static final int BT_PLAYER_2_JUMP = 5;
	public static final int BT_PLAYER_2_ACT = 6;
	public static final int BT_PLAYER_2_ITEM = 8;

	private static final int PAUSE_BT = 9;

	private final float COOLDOWN_IMAGE_SIZE = getDimension(R.dimen.cooldown_image_size);
	private final int INIT_OF_STATUS_INTERVAL = 270;
	private final int END_OF_STATUS_INTERVAL = 487 - INIT_OF_STATUS_INTERVAL;

	// These two first rects will determine the position of the others
	private final RectF PLAYER_1_ITEM_FRAME_SIZE = new RectF(
			40 - COOLDOWN_IMAGE_SIZE / 2, 40 - COOLDOWN_IMAGE_SIZE / 2,
			40 + COOLDOWN_IMAGE_SIZE / 2, 40 + COOLDOWN_IMAGE_SIZE / 2);

	private final RectF PLAYER_2_ITEM_FRAME_SIZE = new RectF(
			590 - COOLDOWN_IMAGE_SIZE / 2, 40 - COOLDOWN_IMAGE_SIZE / 2,
			590 + COOLDOWN_IMAGE_SIZE / 2, 40 + COOLDOWN_IMAGE_SIZE / 2);

	private final RectF PLAYER_1_ITEM_SIZE = new RectF(PLAYER_1_ITEM_FRAME_SIZE);
	{
		PLAYER_1_ITEM_SIZE.inset(5, 5); // Move 10 px inwards
	}

	private final RectF PLAYER_2_ITEM_SIZE = new RectF(PLAYER_2_ITEM_FRAME_SIZE);
	{
		PLAYER_2_ITEM_SIZE.inset(5, 5); // Move 10 px inwards
	}

	private final RectF PLAYER_1_EFFECT_ACTIVE_SIZE = new RectF(
			PLAYER_1_ITEM_FRAME_SIZE);
	{
		PLAYER_1_EFFECT_ACTIVE_SIZE.inset(-30, -30); // Move 50 px outwards
	}

	private final RectF PLAYER_2_EFFECT_ACTIVE_SIZE = new RectF(
			PLAYER_2_ITEM_FRAME_SIZE);
	{
		PLAYER_2_EFFECT_ACTIVE_SIZE.inset(-30, -30); // Move 50 px outwards
	}

	private Player p1;
	private Player p2;
	private GameWorld gWorld;

	private AnimationSwitcher itemActiveAnim;
	private AnimationSwitcher itemActiveAnim2;

	private GameHUDButton p1JumpListener;
	private GameHUDButton p1ActListener;
	private GameHUDButton p1ItemListener;
	private GameHUDButton p2JumpListener;
	private GameHUDButton p2ActListener;
	private GameHUDButton p2ItemListener;

	private ImageButton pauseBt;
	private PauseScreen pauseScreen;
	private float totalDistance = Float.MIN_VALUE;

	public GameWorldUI(GameWorld gameWorld) {
		this.gWorld = gameWorld;
		
		p1 = gWorld.getPlayers().get(PlayerNumber.ONE);
		p2 = gWorld.getPlayers().get(PlayerNumber.TWO);

		itemActiveAnim = AnimationManager.getInstance().getSprite("Spinner");
		itemActiveAnim2 = AnimationManager.getInstance().getSprite("Spinner");
		
		pauseScreen = new PauseScreen();
		pauseBt = new ImageButton(PAUSE_BT, 375, 10, 
				getDimension(R.dimen.main_menu_button_size) - 30, 
				getDimension(R.dimen.main_menu_button_size) - 30,
				this, 
				R.drawable.pause_selected, 
				R.drawable.pause_unselected);
		
	}
	public void setAllButtonListeners(GameHUDButton listener) {
		setP1ButtonListeners(listener);
		setP2ButtonListeners(listener);
	}

	// We may need to split this for each listener... For now, we don't need
	// this
	public void setP1ButtonListeners(GameHUDButton listener) {
		p1JumpListener = listener;
		p1ActListener = listener;
		p1ItemListener = listener;
	}

	public void setP2ButtonListeners(GameHUDButton listener) {
		p2JumpListener = listener;
		p2ActListener = listener;
		p2ItemListener = listener;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.drawBitmap(R.drawable.ui_buttons, 0,
				getDimension(R.dimen.screen_height)
						- getDimension(R.dimen.ui_buttons_height),
				getDimension(R.dimen.screen_width),
				getDimension(R.dimen.ui_buttons_height));
		
		pauseBt.render(canvas, timeElapsed);
		pauseScreen.render(canvas, timeElapsed);
		if (!p1.isDead()) {
			canvas.drawBitmap(p1.getStatusImg(), INIT_OF_STATUS_INTERVAL
					+ calculateMapCompletion(p1.body.getPosition().x), 390,
					getDimension(R.dimen.progression_character_image_size),
					getDimension(R.dimen.progression_character_image_size));

			if (p1.getItem() != null) {
				canvas.drawRect(PLAYER_1_ITEM_SIZE, p1.getItem().getColor());
				canvas.drawBitmap(p1.getItem().getImageDrawable(),
						PLAYER_1_ITEM_SIZE);

				if (p1.getItem().isActive()) {

					GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE); // Additive
																			// Blending
																			// Effect

					itemActiveAnim.render(canvas, PLAYER_1_EFFECT_ACTIVE_SIZE,
							timeElapsed);

					GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
				}
			}
		}

		canvas.drawBitmap(R.drawable.item_frame, PLAYER_1_ITEM_FRAME_SIZE);

		if (!p2.isDead()) {
			canvas.drawBitmap(p2.getStatusImg(), INIT_OF_STATUS_INTERVAL
					+ calculateMapCompletion(p2.body.getPosition().x), 440,
					getDimension(R.dimen.progression_character_image_size),
					getDimension(R.dimen.progression_character_image_size));
			if (p2.getItem() != null) {
				canvas.drawRect(PLAYER_2_ITEM_SIZE, p2.getItem().getColor());
				canvas.drawBitmap(p2.getItem().getImageDrawable(),
						PLAYER_2_ITEM_SIZE);

				if (p2.getItem().isActive()) {

					GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE); // Additive
																			// Blending
																			// Effect

					itemActiveAnim2.render(canvas, PLAYER_2_EFFECT_ACTIVE_SIZE,
							timeElapsed);

					GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
				}
			}
		}

		canvas.drawBitmap(R.drawable.item_frame, PLAYER_2_ITEM_FRAME_SIZE);
	}

	@Override
	public void step(float timeElapsed) {
	}

	private int calculateMapCompletion(float pos) {

		float flagXPosition = gWorld.getFlagPos();

		if (totalDistance == Float.MIN_VALUE) {
			totalDistance = flagXPosition - pos;
		}

		int totalCompletion = END_OF_STATUS_INTERVAL
				- (int) (END_OF_STATUS_INTERVAL * (flagXPosition - pos) / totalDistance);

		if (totalCompletion < 0)
			totalCompletion = 0;
		if (totalCompletion > END_OF_STATUS_INTERVAL)
			totalCompletion = END_OF_STATUS_INTERVAL;

		return totalCompletion;
	}

	
	//pauseScreen.input(event);
//	if (pauseScreen.isHidden())
//		ui.input(event);
	
	@Override
	public void input(InputEventHandler event) {
		pauseBt.input(event);
		pauseScreen.input(event);
		if(pauseScreen.isHidden()) {
			int action = event.getAction() & MotionEvent.ACTION_MASK;
			if (action == MotionEvent.ACTION_DOWN
					|| action == MotionEvent.ACTION_POINTER_DOWN) {
				pressButtons(event.getX(), event.getY(), true);
			} else if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_POINTER_UP) {
				pressButtons(event.getX(), event.getY(), false);
			}
		}
		
		
		
	}

	private void pressButtons(float x, float y, boolean pressed) {
		if (y > 340) {
			if (x < 230) {
				if (x < 105) {
					// Jump 1
					if (p1JumpListener != null) {
						p1JumpListener.onClick(BT_PLAYER_1_JUMP, pressed);
					}
				} else {
					// Act 1
					if (p1ActListener != null) {
						p1ActListener.onClick(BT_PLAYER_1_ACT, pressed);
					}
				}
			}

			if (x > 600) {
				if (x < 710) {
					// Jump 2
					if (p2JumpListener != null) {
						p2JumpListener.onClick(BT_PLAYER_2_JUMP, pressed);
					}
				} else {
					// Act 2
					if (p2ActListener != null) {
						p2ActListener.onClick(BT_PLAYER_2_ACT, pressed);
					}
				}
			}
		}

		if (PLAYER_1_ITEM_FRAME_SIZE.contains(x, y)) {
			if (p1ItemListener != null) {
				p1ItemListener.onClick(BT_PLAYER_1_ITEM, pressed);
			}
		} else if (PLAYER_2_ITEM_FRAME_SIZE.contains(x, y)) {
			if (p2ItemListener != null) {
				p2ItemListener.onClick(BT_PLAYER_2_ITEM, pressed);
			}
		}
	}
	
	public boolean isPauseHidden() {
		return pauseScreen.isHidden();
	}
	

	@Override
	public void onPause() {
		if (pauseScreen.isHidden())
			pauseScreen.show();
	}
	
	@Override
	public void onClick(int buttonId) {
		switch (buttonId) {
		case PAUSE_BT:
			pauseScreen.show();
			System.out.println("APERTOU O BOTAO DE PAUSE ***********");
			break;
		}
		
	}

}
