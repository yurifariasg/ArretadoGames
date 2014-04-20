package com.arretadogames.pilot.screens;

import android.graphics.RectF;
import android.opengl.GLES11;
import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.CollisionFlag;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.world.GameWorld;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import java.util.Random;

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
    
    private final float HALF_SCREEN_WIDTH = getDimension(R.dimen.screen_width) / 2;
    private final float HALF_SCREEN_HEIGHT = getDimension(R.dimen.screen_height) / 2;

    private static final float countdownSize = 200;
    
    private final RectF COUNTDOWN_RECT = new RectF(
            HALF_SCREEN_WIDTH - countdownSize / 2, HALF_SCREEN_HEIGHT - countdownSize / 2,
            HALF_SCREEN_WIDTH + countdownSize / 2, HALF_SCREEN_HEIGHT + countdownSize / 2);
    
    // Impulse it gives to the player that wins the drag
    private static final Vec2 START_RACE_IMPULSE = new Vec2(4, 0);
    // Force that pulls the players before the race begins
    private static final float FORCE = 40.0f;
    // The initial x offset
    private static final float INITIAL_POS_X_OFFSET = -42.0f;

	private Player p1;
	private Player p2;
	private GameWorld gWorld;
	
	private AnimationSwitcher itemActiveAnim;
	private AnimationSwitcher itemActiveAnim2;
	
	private boolean activateItemP1;
	private boolean activateItemP2;

	// This countdown is not the same as the images (3 here does not mean 3 in the image - check render)
	private float countDownTimer;
	private boolean raceStarted;
	private boolean arePlayersCollliding;
	
	private boolean activateInput;
    
    private Vec2 impulseP1;
    private Vec2 impulseBackP1;
    private Vec2 impulseP2;
    private Vec2 impulseBackP2;
    
	private float totalDistance = Float.MIN_VALUE;

	public GameWorldUI(GameWorld gameWorld) {
		this.gWorld = gameWorld;

		p1 = gWorld.getPlayers().get(PlayerNumber.ONE);
		p2 = gWorld.getPlayers().get(PlayerNumber.TWO);

		itemActiveAnim = AnimationManager.getInstance().getSprite("Spinner");
		itemActiveAnim2 = AnimationManager.getInstance().getSprite("Spinner");
		
		activateItemP1 = false;
		activateItemP2 = false;
		
		resetCountdown();
		
		impulseP1 = new Vec2(1,0);
		impulseP1.normalize();
		impulseP1.mul(FORCE * p1.body.getMass());

        impulseP2 = new Vec2(1,0);
        impulseP2.normalize();
        impulseP2.mul(FORCE * p2.body.getMass());
        
        impulseBackP1 = new Vec2(-1,0);
        impulseBackP1.normalize();
        impulseBackP1.mul(p1.body.getMass());

        impulseBackP2 = new Vec2(-1,0);
        impulseBackP2.normalize();
        impulseBackP2.mul(p2.body.getMass());
	}
	
	public void resetCountdown() {
	    countDownTimer = 5.5f;
	    p1.setEnabled(false);
	    p2.setEnabled(false);
	    raceStarted = false;
	    
	    setPlayersCollision(false);
	    
        Vec2 offset = new Vec2(-INITIAL_POS_X_OFFSET, 0);
        p1.body.setTransform(p1.body.getPosition().add(offset), 0);
        p2.body.setTransform(p1.body.getPosition(), 0);
        
        activateInput = false;
        
        // Give P1 an bigger impulse
        p1.body.applyLinearImpulse(new Vec2(5.0f, 0).mul(p1.body.getMass()), p1.body.getPosition(), true);
        p2.body.applyLinearImpulse(new Vec2(4.5f, 0).mul(p2.body.getMass()), p2.body.getPosition(), true);
        
	}
	
	private void setPlayersCollision(boolean shouldCollide) {
	    if (shouldCollide) {
	        Fixture f = p1.body.getFixtureList();
	        while (f != null) {
	            f.getFilterData().maskBits = f.getFilterData().maskBits | CollisionFlag.GROUP_PLAYERS.getValue();
	            
	            f = f.getNext();
	        }
	        
	        f = p2.body.getFixtureList();
	        
	        while (f != null) {
	            f.getFilterData().maskBits = f.getFilterData().maskBits | CollisionFlag.GROUP_PLAYERS.getValue();
	            
	            f = f.getNext();
	        }
	    } else {
	        Fixture f = p1.body.getFixtureList();
	        while (f != null) {
	            f.getFilterData().maskBits = f.getFilterData().maskBits & ~CollisionFlag.GROUP_PLAYERS.getValue();
	            
	            f = f.getNext();
	        }
	        
	        f = p2.body.getFixtureList();
	        
	        while (f != null) {
	            f.getFilterData().maskBits = f.getFilterData().maskBits & ~CollisionFlag.GROUP_PLAYERS.getValue();
	            
	            f = f.getNext();
	        }
	    }
	    
	    arePlayersCollliding = shouldCollide;
	}
	
	private void impulsePlayer(PlayerNumber impulseWinPlayer) {
	    raceStarted = true;
	    
	    if (impulseWinPlayer == null) {
	        // Random..
	        int randomizedNumber = new Random().nextInt(10);
	        if (randomizedNumber % 2 == 0) {
	            impulseWinPlayer = PlayerNumber.ONE;
	        } else {
	            impulseWinPlayer = PlayerNumber.TWO;
	        }
	    }
	    
	    if (impulseWinPlayer.equals(PlayerNumber.ONE)) {
	        p1.body.applyLinearImpulse(START_RACE_IMPULSE.mul(p1.body.getMass()), p1.body.getPosition(), true);
	    } else if (impulseWinPlayer.equals(PlayerNumber.TWO)) {
	        p2.body.applyLinearImpulse(START_RACE_IMPULSE.mul(p2.body.getMass()), p2.body.getPosition(), true);
	    }
	}
	
	private void enablePlayers() {
	    p1.setEnabled(true);
        p2.setEnabled(true);
        setPlayersCollision(true);
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
        
        
        
        if (countDownTimer > 0) { // Draw numbers if we have a countdown
            if (countDownTimer > 4) {
                canvas.drawBitmap(R.drawable.countdown_3, COUNTDOWN_RECT);
            } else if (countDownTimer > 3) {
                canvas.drawBitmap(R.drawable.countdown_2, COUNTDOWN_RECT);
            } else if (countDownTimer > 2) {
                canvas.drawBitmap(R.drawable.countdown_1, COUNTDOWN_RECT);
            } else if (countDownTimer > 1) {
                canvas.drawBitmap(R.drawable.countdown_go, COUNTDOWN_RECT);
            }
        }
		
	}

	@Override
	public void step(float timeElapsed) {
	    
        if (activateItemP1 && p1.getItem() != null) {
            p1.getItem().activate(p1, gWorld);
        }
        if (activateItemP2 && p2.getItem() != null) {
            p2.getItem().activate(p2, gWorld);
        }
        
        activateItemP1 = activateItemP2 = false;
        
        
        if (countDownTimer >= 2) {
            
            if (p1.getPosX() - 0.1f > p2.getPosX()) {
                p2.body.applyForceToCenter(impulseP2);
                p1.body.applyForceToCenter(impulseBackP1);
            } else if (p2.getPosX() - 0.1f > p1.getPosX()) {
                p1.body.applyForceToCenter(impulseP1);
                p2.body.applyForceToCenter(impulseBackP2);
            }
            
        } else if (countDownTimer < 2 && !raceStarted && !activateInput) { // At 2s mark, activate input
            activateInput = true;
        } else if (countDownTimer < 1.5 && !raceStarted) { // If players havent pressed, impulse someone...
            impulsePlayer(null);
        } else if (countDownTimer < 1.1 && !arePlayersCollliding) {
            // If the race hasnt started and it is 1.1 mark, then enable players anyway
            enablePlayers();
        }
        
        if (countDownTimer > 0) { // Decrements countdown only until it reaches a negative... (we dont need it anymore)
            countDownTimer -= timeElapsed;
        }
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
	    if (!activateInput) { // Only if input is active
	        return;
	    }
	    
		if (y > 340) {
			if (x < 230) {
			    if (!raceStarted) { // If race has not started, then try to get impulse!
			        impulsePlayer(PlayerNumber.ONE);
			    } else if (x < 105) {
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
			    if (!raceStarted) { // If race has not started, then try to get impulse!
                    impulsePlayer(PlayerNumber.TWO);
                } else if (x < 710) {
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
		        activateItemP1 = true;
		    }
		} else if (PLAYER_2_ITEM_FRAME_SIZE.contains(x, y)) {
		    if (p2.getItem() != null) {
		        activateItemP2 = true;
		    }
		}
	}
	
	@Override
	public void onPause() {
	}

}
