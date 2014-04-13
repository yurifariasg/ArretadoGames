package com.arretadogames.pilot.screens;

import android.graphics.RectF;
import android.opengl.GLES11;
import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.Fire;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.world.GameWorld;

import javax.microedition.khronos.opengles.GL10;

public class GameWorldUI extends GameScreen {

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
	
    private final RectF PLAYER_1_EFFECT_ACTIVE_SIZE = new RectF(PLAYER_1_ITEM_FRAME_SIZE);
    {
        PLAYER_1_EFFECT_ACTIVE_SIZE.inset(-30, -30); // Move 50 px outwards
    }
    
    private final RectF PLAYER_2_EFFECT_ACTIVE_SIZE = new RectF(PLAYER_2_ITEM_FRAME_SIZE);
    {
        PLAYER_2_EFFECT_ACTIVE_SIZE.inset(-30, -30); // Move 50 px outwards
    }

	private Player p1;
	private Player p2;
	private Fire fire;
	private GameWorld gWorld;
	
	private AnimationSwitcher itemActiveAnim;
	private AnimationSwitcher itemActiveAnim2;

	float totalDistance = Float.MIN_VALUE;

	public GameWorldUI(GameWorld gameWorld) {
		this.gWorld = gameWorld;

		p1 = gWorld.getPlayers().get(PlayerNumber.ONE);
		p2 = gWorld.getPlayers().get(PlayerNumber.TWO);

		for (Entity e : gWorld.getEntities()){
			if (e.getType() == EntityType.FIRE)
				fire = (Fire) e;
		}

		itemActiveAnim = AnimationManager.getInstance().getSprite("Spinner");
		itemActiveAnim2 = AnimationManager.getInstance().getSprite("Spinner");
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {

		canvas.drawBitmap(R.drawable.ui_buttons, 0, 
		        getDimension(R.dimen.screen_height) - getDimension(R.dimen.ui_buttons_height),
		        getDimension(R.dimen.screen_width), getDimension(R.dimen.ui_buttons_height));
		
		if (!p1.isDead()){
			canvas.drawBitmap(p1.getStatusImg(), INIT_OF_STATUS_INTERVAL + calculateMapCompletion(p1.body.getPosition().x), 390,
			        getDimension(R.dimen.progression_character_image_size), getDimension(R.dimen.progression_character_image_size));
			
			if (p1.getItem() != null) {
    			canvas.drawRect(PLAYER_1_ITEM_SIZE, p1.getItem().getColor());
    	        canvas.drawBitmap(p1.getItem().getImageDrawable(), PLAYER_1_ITEM_SIZE);
    	        
    	        if (p1.getItem().isActive()) {
    	        	
    	        	GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE); // Additive Blending Effect
    	        	
    	        	itemActiveAnim.render(canvas, PLAYER_1_EFFECT_ACTIVE_SIZE, timeElapsed);
    
    	            GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
    	        }
			}
		}
		
        canvas.drawBitmap(R.drawable.item_frame, PLAYER_1_ITEM_FRAME_SIZE);

		if (!p2.isDead()){
			canvas.drawBitmap(p2.getStatusImg(), INIT_OF_STATUS_INTERVAL + calculateMapCompletion(p2.body.getPosition().x), 440,
                    getDimension(R.dimen.progression_character_image_size), getDimension(R.dimen.progression_character_image_size));
			
			if (p2.getItem() != null) {
                canvas.drawRect(PLAYER_2_ITEM_SIZE, p2.getItem().getColor());
    	        canvas.drawBitmap(p2.getItem().getImageDrawable(), PLAYER_2_ITEM_SIZE);
    	        
    	        if (p2.getItem().isActive()) {
                    
                    GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE); // Additive Blending Effect
                    
    	        	itemActiveAnim2.render(canvas, PLAYER_2_EFFECT_ACTIVE_SIZE, timeElapsed);
    
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
		
		if (PLAYER_1_ITEM_FRAME_SIZE.contains(x, y)) {
		    if (p1.getItem() != null) {
		        p1.getItem().activate(p1, gWorld);
		    }
		} else if (PLAYER_2_ITEM_FRAME_SIZE.contains(x, y)) {
		    if (p2.getItem() != null) {
		        p2.getItem().activate(p2, gWorld);
		    }
		}
	}
	
	@Override
	public void onPause() {
	}

}
