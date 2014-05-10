package com.arretadogames.pilot.world;

import android.graphics.RectF;
import android.opengl.GLES11;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.CollisionFlag;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.GameWorldUI;
import com.arretadogames.pilot.ui.GameButtonListener;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class RaceStartManager implements Renderable, Steppable, GameButtonListener {

    private static final float COUNTDOWN_SIZE = 200;
    private static final float COUNTDOWN_START_TIME = 5.0f;
    
    private float halfScreenWidth;
    private float halfScreenHeight;
    
    private final RectF countdownRect;
    
    // Impulse it gives to the player that wins the drag
    private static final Vec2 START_RACE_IMPULSE = new Vec2(4, 0);
    // Force that pulls the players before the race begins
    private static final float FORCE = 40.0f;
    // The initial x offset
    private static final float INITIAL_POS_X_OFFSET = -42.0f;
    
 // This countdown is not the same as the images (3 here does not mean 3 in the image - check render)
    private float countDownTimer;
    private boolean raceStarted;
    private boolean arePlayersCollliding;
    
    private boolean activateInput;
    
    private Vec2 impulseP1;
    private Vec2 impulseBackP1;
    private Vec2 impulseP2;
    private Vec2 impulseBackP2;
    
    private Player p1;
    private Player p2;
    
    private GameButtonListener nextUIListener;
    
    private GameWorldUI ui;
    
    public RaceStartManager(GameWorld world, GameWorldUI ui) {
        this.p1 = world.getPlayers().get(PlayerNumber.ONE);
        this.p2 = world.getPlayers().get(PlayerNumber.TWO);
        this.ui = ui;
        
        ui.setAllButtonListeners(this);
        
        setNextUIListener(world);
        
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
        
        halfScreenWidth = MainActivity.getActivity().getResources().getDimension(R.dimen.screen_width) / 2;
        halfScreenHeight = MainActivity.getActivity().getResources().getDimension(R.dimen.screen_height) / 2;
        
        countdownRect = new RectF(
                halfScreenWidth - COUNTDOWN_SIZE / 2, halfScreenHeight - COUNTDOWN_SIZE / 2,
                halfScreenWidth + COUNTDOWN_SIZE / 2, halfScreenHeight + COUNTDOWN_SIZE / 2);
    }
    
    private void setNextUIListener(GameButtonListener listener) {
        nextUIListener = listener;
    }
    
    public void resetCountdown() {
        countDownTimer = COUNTDOWN_START_TIME;
        p1.setEnabled(false);
        p2.setEnabled(false);
        raceStarted = false;
        
        setPlayersCollision(false);
        
        Vec2 offset = new Vec2(INITIAL_POS_X_OFFSET, 0);
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
        ui.setAllButtonListeners(nextUIListener);
    }
    
    private float currentTimePercentage;

    @Override
    public void render(GLCanvas canvas, float timeElapsed) {
        
        // TODO: Remove these two GL-Specific methods, insert it inside Canvas to have same functionality
        GLES11.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        if (countDownTimer > 0) { // Draw numbers if we have a countdown
            if (countDownTimer > 4) {
                canvas.drawBitmap(R.drawable.countdown_3, countdownRect);
            } else if (countDownTimer > 3) {
                canvas.drawBitmap(R.drawable.countdown_2, countdownRect);
            } else if (countDownTimer > 2) {
                canvas.drawBitmap(R.drawable.countdown_1, countdownRect);
            } else if (countDownTimer > 1) {
                currentTimePercentage = 1 - interpolate(2, 1f, countDownTimer);
                canvas.saveState();
                canvas.scale(1+currentTimePercentage * 1.5f, 1+currentTimePercentage * 1.5f, halfScreenWidth, halfScreenHeight);
                canvas.drawBitmap(R.drawable.countdown_go, countdownRect, 255 * (1 - currentTimePercentage));
                canvas.restoreState();
            }
        }

        GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
    }

    @Override
    public void step(float timeElapsed) {
        
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
            // If the race has not started and it is 1.1s mark, then enable players anyway
            enablePlayers();
        }
        
        if (countDownTimer > 0) { // Decrements countdown only until it reaches a negative... (we dont need it anymore)
            countDownTimer -= timeElapsed;
        }
    }
    
    private float interpolate(float max, float min, float current) {
        return (current - min) / (max - min);
    }
    
    public boolean isActive() {
        return countDownTimer <= 0;
    }

    @Override
    public void onClick(int buttonId) {
        if (!activateInput) {
            return;
        }
        
        if (buttonId == GameWorldUI.BT_PLAYER_1_JUMP ||
            buttonId == GameWorldUI.BT_PLAYER_1_ACT) {
            
            impulsePlayer(PlayerNumber.ONE);
            activateInput = false;
                
        } else if (buttonId == GameWorldUI.BT_PLAYER_2_JUMP ||
                buttonId == GameWorldUI.BT_PLAYER_2_ACT) {
            
            impulsePlayer(PlayerNumber.TWO);
            activateInput = false;
            
        }
    }
}
