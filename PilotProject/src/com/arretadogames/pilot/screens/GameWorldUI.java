package com.arretadogames.pilot.screens;

import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.Text;
import com.arretadogames.pilot.world.GameWorld;

public class GameWorldUI extends GameScreen {
	
	private GameWorld gWorld;
	private Text completionText;
	private Text coin1Text;
	private Text coin2Text;
	
	float totalDistance = Float.MIN_VALUE;
	
	public GameWorldUI(GameWorld gameWorld) {
		this.gWorld = gameWorld;
		completionText = new Text(400, 430, "0% completed", 1);
		
		coin1Text = new Text(85, 40, "0", 1);
		coin2Text = new Text(710, 40, "0", 1);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.drawBitmap(R.drawable.ui_buttons, 0, 340);
		completionText.render(canvas, timeElapsed);
		
		canvas.drawBitmap(R.drawable.coin_1_1, 20, 35);
		coin1Text.render(canvas, timeElapsed);
		
		canvas.drawBitmap(R.drawable.coin_1_1, 645, 35);
		coin2Text.render(canvas, timeElapsed);
		
	}

	@Override
	public void step(float timeElapsed) {
		
		int coins = gWorld.getPlayers().get(PlayerNumber.ONE).getCoins();
		coin1Text.setText(String.valueOf(coins));
		coins = gWorld.getPlayers().get(PlayerNumber.TWO).getCoins();
		coin2Text.setText(String.valueOf(coins));
		
		int percentageCompleted = calculateMapCompletion();
		completionText.setText(String.valueOf(percentageCompleted) + "% completed");
		
		
	}

	private int calculateMapCompletion() {
		
		float playerFurthestDistance = 0;
		for (Player p : gWorld.getPlayers().values()) {
			if (p.body.getPosition().x > playerFurthestDistance)
				playerFurthestDistance = p.body.getPosition().x;
		}
		
		float flagXPosition = 0;
		for (Entity entity : gWorld.getEntities()) {
			if (entity.getType() == EntityType.FINALFLAG) {
				flagXPosition = entity.body.getPosition().x;
				break;
			}
		}
		
		if (totalDistance == Float.MIN_VALUE) {
			totalDistance = flagXPosition -
					gWorld.getPlayers().get(PlayerNumber.ONE).body.getPosition().x;
		}
		
		return 100 - (int) (100 * (flagXPosition - playerFurthestDistance) / totalDistance);
	}

	@Override
	public void input(InputEventHandler event) { // FIX : Detect several inputs
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
	}

}
