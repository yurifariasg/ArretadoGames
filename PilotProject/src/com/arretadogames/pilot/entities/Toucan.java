
package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Toucan implementation
 */
public class Toucan implements Renderable, Steppable {

    private enum ToucanState {
        INITIAL_FLIGHT, GRAB, PULL_FLIGHT, DROP, OUT_FLIGHT;
    }

    private static final PhysicsRect TOUCAN_SIZE = new PhysicsRect(2, 2);
    private static final Vec2 GRAB_OFFSET = new Vec2(0, 0.6f);
    private static final float TARGET_PLAYER_X_OFFSET = -5f;
    private static final float TARGET_GROUND_Y_OFFSET = 2f;
    private static final Vec2 TARGET_FLAG_OFFSET = new Vec2(-10, 2);
    private static final Vec2 FLIGHT_OUT_POSITION_RELATIVE_TO_TARGET = new Vec2(4, 10);

    /* All duration are in seconds */
    private static final float GRAB_FLIGHT_DURATION = 1;
    private static final float GRAB_DURATION = 0.0001f;
    private static final float PULL_FLIGHT_DURATION = 1f;
    private static final float DROP_DURATION = 0.0001f;
    private static final float OUT_FLIGHT_DURATION = 3;

    private Vec2 flightOutTargetPosition = new Vec2();
    private Vec2 oldPos = new Vec2();
    private Vec2 center = new Vec2();
    private Vec2 grabbedPlayerPos = new Vec2();
    private Player playerToGrab;
    private Player target;
    private AnimationSwitcher sprite;
    private float flagX;

    private float remainingTime;
    private ToucanState state;

    /**
     * Toucan receives the camera to updates its starting position
     * 
     * @param camera
     */
    public Toucan() {
        this.sprite = AnimationManager.getInstance().getSprite("AraraAzul");
    }

    /**
     * If the Toucan is currently active
     * 
     * @return True - If the toucan if active<br>
     *         False - Otherwise
     */
    public boolean isActive() {
        return playerToGrab != null;
    }

    /**
     * Activates the Toucan using the given parameters
     * 
     * @param xPos The initial X position
     * @param yPos The initial Y position
     * @param playerToGrab The player to be grabbed
     * @param targetPlayer The player that the toucan will be grabbed to
     */
    public void activate(float xPos, float yPos, Player playerToGrab, Player targetPlayer, float flagX) {
        if (!isActive() && playerToGrab != null && targetPlayer != null) {
            this.center.set(xPos, yPos);
            this.oldPos.set(xPos, yPos);
            this.target = targetPlayer;
            this.playerToGrab = playerToGrab;
            this.remainingTime = GRAB_FLIGHT_DURATION;
            this.state = ToucanState.INITIAL_FLIGHT;
            this.playerToGrab.setForceStop(true);
            this.flagX = flagX;
        }
    }

    /*
     * Performs a cleanup operation after a grab.
     */
    private void cleanUp() {
        this.target = null;
        this.playerToGrab = null;
        this.remainingTime = 0;
        this.state = null;
        this.flagX = Float.MAX_VALUE;
    }

    @Override
    public void render(GLCanvas canvas, float timeElapsed) {
        if (!isActive())
            return;

        canvas.saveState();

        canvas.translatePhysics(center.x, center.y);

        sprite.render(canvas, TOUCAN_SIZE, timeElapsed);

        canvas.restoreState();

    }

    @Override
    public void step(float timeElapsed) {
        if (!isActive())
            return;

        remainingTime -= timeElapsed;

        switch (state) {
            case INITIAL_FLIGHT:

                if (remainingTime <= 0) {
                    state = ToucanState.GRAB;
                    remainingTime = GRAB_DURATION;
                    setOldPosition();
                } else {
                    moveTowards(playerToGrab.body.getPosition().x + GRAB_OFFSET.x,
                            playerToGrab.body.getPosition().y + GRAB_OFFSET.y,
                            remainingTime, GRAB_FLIGHT_DURATION);
                }

                break;
            case GRAB:

                if (remainingTime <= 0) {
                    state = ToucanState.PULL_FLIGHT;
                    remainingTime = PULL_FLIGHT_DURATION;
                    playerToGrab.setGhostMode(true);
                }

                break;
            case PULL_FLIGHT:

                if (remainingTime <= 0) {
                    playerToGrab.setGhostMode(false);
                    playerToGrab.setForceStop(false);
                    state = ToucanState.DROP;
                    remainingTime = DROP_DURATION;
                    setOldPosition();
                } else {
                    
                    Body b = target.body;
                    
                    if (b.getPosition().x < flagX) {
                        moveTowards(b.getPosition().x + TARGET_PLAYER_X_OFFSET,
                                TARGET_GROUND_Y_OFFSET,
                                remainingTime, PULL_FLIGHT_DURATION);
                    } else {
                        moveTowards(flagX + TARGET_FLAG_OFFSET.x, TARGET_FLAG_OFFSET.y,
                                remainingTime, PULL_FLIGHT_DURATION);
                    }
                    
                    grabbedPlayerPos.set(center).addLocal(GRAB_OFFSET.x, -GRAB_OFFSET.y);
                    playerToGrab.body.setTransform(grabbedPlayerPos, 0);
                }

                break;
            case DROP:

                if (remainingTime <= 0) {
                    state = ToucanState.OUT_FLIGHT;
                    remainingTime = OUT_FLIGHT_DURATION;
                    flightOutTargetPosition.set(target.body.getPosition())
                            .addLocal(FLIGHT_OUT_POSITION_RELATIVE_TO_TARGET.x,
                                    FLIGHT_OUT_POSITION_RELATIVE_TO_TARGET.y);
                    setOldPosition();
                }

                break;
            case OUT_FLIGHT:

                if (remainingTime <= 0) {
                    cleanUp();
                } else {
                    moveTowards(flightOutTargetPosition.x, flightOutTargetPosition.y,
                            remainingTime, OUT_FLIGHT_DURATION);
                }

                break;
            default:
                break;
        }
    }

    /*
     * Sets the variable oldPos to the current center.
     */
    private void setOldPosition() {
        oldPos.set(center);
    }

    /*
     * Move the toucan towards the given position and in the given time
     * parameters.<br> The velocity will be based on the total time he had and
     * the remaining time he now has.
     */
    private void moveTowards(float targetX, float targetY, float time, float totalTime) {
        center.x = oldPos.x + (targetX - oldPos.x) * (1 - (time / totalTime));
        center.y = oldPos.y + (targetY - oldPos.y) * (1 - (time / totalTime));
    }

}
