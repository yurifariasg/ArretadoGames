package com.arretadogames.pilot.screens;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.ViewDebug.FlagToString;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
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
	
	private Player p1;
	private Player p2;
	private GameWorld gWorld;
	private Text completionText;
	private Text coin1Text;
	private Text coin2Text;
	private GLCircle coolDown1;
	private GLCircle coolDown2;
	
	float totalDistance = Float.MIN_VALUE;
	public GameWorldUI(GameWorld gameWorld) {
		this.gWorld = gameWorld;
		
		p1 = gWorld.getPlayers().get(PlayerNumber.ONE);
		p2 = gWorld.getPlayers().get(PlayerNumber.TWO);
		
		completionText = new Text(400, 430, "0% completed",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, true);
		
		coin1Text = new Text(140, 40, "0",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, false);
		coin2Text = new Text(690, 40, "0",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, false);
		
		coolDown1 = new GLCircle(COOLDOWN_RADIUS);
		coolDown2 = new GLCircle(COOLDOWN_RADIUS);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		canvas.drawBitmap(R.drawable.ui_buttons, 0, 340);
//		completionText.render(canvas, timeElapsed);
		
		canvas.drawBitmap(R.drawable.coin_1_1, 90, 25);
		coin1Text.render(canvas, timeElapsed);
		
		canvas.drawBitmap(R.drawable.coin_1_1, 640, 25);
		coin2Text.render(canvas, timeElapsed);
		
		coolDown1.drawCircle(canvas, COOLDOWN1_X, COOLDOWN_Y, Color.BLUE, true, p1.getPercentageLeftToNextAct());
		canvas.drawBitmap(R.drawable.power, centerImage(R.drawable.power, 0),
											centerImage(R.drawable.power, 2));
		
		coolDown2.drawCircle(canvas, COOLDOWN2_X, COOLDOWN_Y, Color.RED, true, p2.getPercentageLeftToNextAct());
		canvas.drawBitmap(R.drawable.power, centerImage(R.drawable.power, 1),
											centerImage(R.drawable.power, 2));
		
		
		canvas.drawBitmap(p1.getStatusImg(), INIT_OF_STATUS_INTERVAL + calculateMapCompletion(p1), 390);
		canvas.drawBitmap(p2.getStatusImg(), INIT_OF_STATUS_INTERVAL + calculateMapCompletion(p2), 440);
		
//		System.out.println("Position Player2: "+p2.body.getPosition().x);
	}
		
	private float centerImage(int imgId, int cooldown){
		int[] size = ImageLoader.checkBitmapSize(R.drawable.power); 
		int width = size[0];
		int height = size[1];
		float x = 0.0f;
		
		switch(cooldown){
		case 0://First Player - Horizontal
			//int[] size = ImageLoader.checkBitmapSize(R.drawable.power);//take from the player your image
			//width = size[0];
			x = COOLDOWN1_X - (width/2);
			break;
		case 1://Second Player - Horizontal
			//int[] size = ImageLoader.checkBitmapSize(R.drawable.power);//take from the player your image
			//width = size[0];		
			x = COOLDOWN2_X - (width/2);
			break;
		case 2://Players - Vertical
			//int[] size = ImageLoader.checkBitmapSize(R.drawable.power);//take from the player your image
			//height = size[1];
			x = COOLDOWN_Y - (height/2);
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
		
//		int percentageCompleted = calculateMapCompletion();
//		completionText.setText(String.valueOf(percentageCompleted) + "% completed");
		
	}

	private int calculateMapCompletion(Player p) {
		
		float flagXPosition = gWorld.getFlagPos();
		
		if (totalDistance == Float.MIN_VALUE) {
			totalDistance = flagXPosition -
					gWorld.getPlayers().get(PlayerNumber.ONE).body.getPosition().x;
		}
		
		int totalCompletion = END_OF_STATUS_INTERVAL - (int) (END_OF_STATUS_INTERVAL * (flagXPosition - p.body.getPosition().x) / totalDistance);
		
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
