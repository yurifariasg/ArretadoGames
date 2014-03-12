package com.arretadogames.pilot.screens;

import android.graphics.Color;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.Fire;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.render.opengl.GLCircle;
import com.arretadogames.pilot.ui.Text;
import com.arretadogames.pilot.world.GameWorld;

public class GameWorldUI extends GameScreen {

	private final int INIT_OF_STATUS_INTERVAL = 270;
	private final int END_OF_STATUS_INTERVAL = 487 - INIT_OF_STATUS_INTERVAL;
	private final int COOLDOWN_RADIUS = 30;
	private final int COOLDOWN1_X = 40;
	private final int COOLDOWN2_X = 590;
	private final int COOLDOWN_Y = 40;

	private final int COIN1_X = 140;
	private final int COIN2_X = 690;
	private final int COINS_Y = 40;

	private Player p1;
	private Player p2;
	private Fire fire;
	private GameWorld gWorld;
//	private Text completionText;
	private Text coin1Text;
	private Text coin2Text;
	private GLCircle coolDown1;
	private GLCircle coolDown2;

	float totalDistance = Float.MIN_VALUE;
	private RectF seedRenderingRect = new RectF(0, 0, 40, 40);

	public GameWorldUI(GameWorld gameWorld) {
		this.gWorld = gameWorld;

		p1 = gWorld.getPlayers().get(PlayerNumber.ONE);
		p2 = gWorld.getPlayers().get(PlayerNumber.TWO);

		for (Entity e : gWorld.getEntities()){
			if (e.getType() == EntityType.FIRE)
				fire = (Fire) e;
		}

		coin1Text = new Text(COIN1_X, COINS_Y, "0",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, false);
		coin2Text = new Text(COIN2_X, COINS_Y, "0",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, false);

		coolDown1 = new GLCircle(COOLDOWN_RADIUS);
		coolDown2 = new GLCircle(COOLDOWN_RADIUS);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {

		canvas.drawBitmap(R.drawable.ui_buttons, 0, 
		        getDimension(R.dimen.screen_height) - getDimension(R.dimen.ui_buttons_height),
		        getDimension(R.dimen.screen_width), getDimension(R.dimen.ui_buttons_height));

		seedRenderingRect.right = 90 + seedRenderingRect.width();
		seedRenderingRect.left = 90;
		if (!p1.getItems().isEmpty()) {
			seedRenderingRect.right += 80;
			seedRenderingRect.left += 80;
			coin1Text.setX(COIN1_X + 80);
		}

		seedRenderingRect.bottom = 20 + seedRenderingRect.height();
		seedRenderingRect.top = 20;
		canvas.drawBitmap(R.drawable.seed1, seedRenderingRect);
		coin1Text.render(canvas, timeElapsed);

		seedRenderingRect.right = 640 + seedRenderingRect.width();
		seedRenderingRect.left = 640;
		seedRenderingRect.bottom = 20 + seedRenderingRect.height();
		seedRenderingRect.top = 20;
		canvas.drawBitmap(R.drawable.seed1, seedRenderingRect);
		coin2Text.render(canvas, timeElapsed);

		// Draw Items
		if (p1.getItems().size() > 0) {
			// For now, just show 1 Item
			canvas.drawBitmap(p1.getItems().get(0).getImage(), COOLDOWN1_X + 40, COOLDOWN_Y - 40,
			        getDimension(R.dimen.cooldown_image_size), getDimension(R.dimen.cooldown_image_size));
		}

		if (p2.getItems().size() > 0) {
			// For now, just show 1 Item
			canvas.drawBitmap(p2.getItems().get(0).getImage(), COOLDOWN2_X - 40, COOLDOWN_Y - 40,
			        getDimension(R.dimen.cooldown_image_size), getDimension(R.dimen.cooldown_image_size));
		}

		if (!p1.isDead()){
			canvas.drawBitmap(p1.getStatusImg(), INIT_OF_STATUS_INTERVAL + calculateMapCompletion(p1.body.getPosition().x), 390,
			        getDimension(R.dimen.progression_character_image_size), getDimension(R.dimen.progression_character_image_size));
			coolDown1.drawCircle(canvas, COOLDOWN1_X, COOLDOWN_Y, Color.BLUE, true, 5, p1.getPercentageLeftToNextAct());
			coolDown1.drawCircle(canvas, COOLDOWN1_X, COOLDOWN_Y, Color.BLACK, false, 5, p1.getPercentageLeftToNextAct());

			canvas.drawBitmap(R.drawable.power, centerImage(R.drawable.power, 0, COOLDOWN1_X),
												centerImage(R.drawable.power, 2, COOLDOWN_Y),
							                    getDimension(R.dimen.cooldown_image_size), getDimension(R.dimen.cooldown_image_size));
		} else {
			coolDown1.drawCircle(canvas, COOLDOWN1_X, COOLDOWN_Y, Color.GRAY, true, 5, 100);
			coolDown1.drawCircle(canvas, COOLDOWN1_X, COOLDOWN_Y, Color.BLACK, false, 5, 100);
			canvas.drawBitmap(R.drawable.power, centerImage(R.drawable.power, 0, COOLDOWN1_X),
					centerImage(R.drawable.power, 2, COOLDOWN_Y),
                    getDimension(R.dimen.cooldown_image_size), getDimension(R.dimen.cooldown_image_size));
		}

		if (!p2.isDead()){
			int cooldownX = COOLDOWN2_X;
			if (!p2.getItems().isEmpty())
				cooldownX -= 80;

			canvas.drawBitmap(p2.getStatusImg(), INIT_OF_STATUS_INTERVAL + calculateMapCompletion(p2.body.getPosition().x), 440,
                    getDimension(R.dimen.progression_character_image_size), getDimension(R.dimen.progression_character_image_size));
			coolDown2.drawCircle(canvas, cooldownX, COOLDOWN_Y, Color.RED, true, 5, p2.getPercentageLeftToNextAct());
			coolDown2.drawCircle(canvas, cooldownX, COOLDOWN_Y, Color.BLACK, false, 5, p2.getPercentageLeftToNextAct());
			canvas.drawBitmap(R.drawable.power, centerImage(R.drawable.power, 1, cooldownX),
												centerImage(R.drawable.power, 2, COOLDOWN_Y),
							                    getDimension(R.dimen.cooldown_image_size), getDimension(R.dimen.cooldown_image_size));
		} else {
			int cooldownX = COOLDOWN2_X;
			if (!p2.getItems().isEmpty())
				cooldownX -= 80;
			coolDown1.drawCircle(canvas, cooldownX, COOLDOWN_Y, Color.GRAY, true, 5, 100);
			coolDown1.drawCircle(canvas, cooldownX, COOLDOWN_Y, Color.BLACK, false, 5, 100);
			canvas.drawBitmap(R.drawable.power, centerImage(R.drawable.power, 1, cooldownX),
					centerImage(R.drawable.power, 2, COOLDOWN_Y),
                    getDimension(R.dimen.cooldown_image_size), getDimension(R.dimen.cooldown_image_size));
		}

		if (GameSettings.ACTIVATE_FIRE)
			canvas.drawBitmap(fire.getStatusImg(), INIT_OF_STATUS_INTERVAL + calculateMapCompletion(fire.getPosX()), 415,
                    getDimension(R.dimen.progression_character_image_size), getDimension(R.dimen.progression_character_image_size));

//		System.out.println("Position Player2: "+p2.body.getPosition().x);
	}

	private float centerImage(int imgId, int cooldown, float cooldownValue){
		int[] size = ImageLoader.checkBitmapSize(imgId);
		int width = size[0];
		int height = size[1];
		float x = 0.0f;

		switch(cooldown){
		case 0://First Player - Horizontal
			x = cooldownValue - (width/2);
			break;
		case 1://Second Player - Horizontal
			x = cooldownValue - (width/2);
			break;
		case 2://Players - Vertical
			x = cooldownValue - (height/2);
			break;
		default:
			break;
		}

		return x;
	}

	@Override
	public void step(float timeElapsed) {

		int coins = gWorld.getPlayers().get(PlayerNumber.ONE).getCoins();
		coin1Text.setText(String.valueOf(coins));
		coins = gWorld.getPlayers().get(PlayerNumber.TWO).getCoins();
		coin2Text.setText(String.valueOf(coins));

	}

	private int calculateMapCompletion(float pos) {

		float flagXPosition = gWorld.getFlagPos();

		if (totalDistance == Float.MIN_VALUE) {
			totalDistance = flagXPosition -	pos;
		}

		int totalCompletion = END_OF_STATUS_INTERVAL - (int) (END_OF_STATUS_INTERVAL * (flagXPosition - pos) / totalDistance);

		if (totalCompletion < 0)
			totalCompletion = 0;
		if (totalCompletion > END_OF_STATUS_INTERVAL)
			totalCompletion = END_OF_STATUS_INTERVAL;

		return totalCompletion;
	}

	@Override
	public void input(InputEventHandler event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
			pressButtons(event.getX(), event.getY(), true);
		} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
			pressButtons(event.getX(), event.getY(), false);
		}
	}

	private void pressButtons(float x, float y, boolean pressed) {
		if (y > 340) {
			if (x < 230) {
				if (x < 105) {
					// Jump 1
					Player p = gWorld.getPlayers().get(PlayerNumber.ONE);
					if(p!=null) p.setJumping(pressed);
				} else {
					// Act 1
					Player p = gWorld.getPlayers().get(PlayerNumber.ONE);
					if(p!=null) p.setAct(pressed);
				}
			}

			if (x > 600) {
				if (x < 710) {
					// Jump 2
					Player p = gWorld.getPlayers().get(PlayerNumber.TWO);
					if(p!=null) p.setJumping(pressed);
				} else {
					// Act 2
					Player p = gWorld.getPlayers().get(PlayerNumber.TWO);
					if(p!=null) p.setAct(pressed);
				}
			}
		}
	}

	@Override
	public void onPause() {
	}

}
