package com.arretadogames.pilot.entities;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;

import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.items.Item;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.world.GameWorld;

public abstract class Player extends Entity implements Steppable{
	
	private static float TIME_BEFORE_DIE = 3f; // This will actually depend on the animation
	private float timeToDie;

	private float maxJumpVelocity = 5;
	private float maxRunVelocity = 3;
	private int maxDoubleJumps = 0;
	private float jumpAceleration = 3;
	private float runAceleration = 5;
	private float timeWaitingForAct = 6;
	
	private PlayerNumber playerNumber;
	private boolean hasFinished; /* Player has finished level */
	
	protected boolean jumpActive;
	protected boolean actActive;
	
	private int acquiredCoins;
	private int timeFinished;

	private List<Item> items;
	
	public Player(float x, float y, PlayerNumber playerNumber) {
		super(x, y);
		this.playerNumber = playerNumber;
		hasFinished = false;
		jumpActive = false;
		actActive = false;
		items = new ArrayList<Item>();
		timeToDie = 0;
	}
	
	public boolean addItem(Item i){
		return items.add(i);
	}
	
	public boolean remove(Item i){
		return items.remove(i);
	}
	
	public List<Item> getItems() {
		return items;
	}
	
	@Override
	public void step(float timeElapsed){
		if (state == State.DYING) {
			System.out.println( "Time to die: " + timeToDie + " - TimeElapsed: " + timeElapsed);
			timeToDie -= timeElapsed;
			if (timeToDie <= 0) {
				state = State.DEAD;
				System.out.println("DEAD");
				PhysicalWorld.getInstance().addDeadEntity(this);
			}
		}
		
		for(Item i : items){
			i.applyEffect(this);
		}
		
	}
	
	public PlayerNumber getNumber() {
		return playerNumber;
	}
	
	public void setFinished(boolean hasFinished) {
		if (!hasFinished() && hasFinished) // Was not finished before, now it is
			timeFinished = ((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).getTotalElapsedTime();
		this.hasFinished = hasFinished;
	}
	
	public boolean hasFinished() {
		return hasFinished;
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}
	
//	public abstract void jump();
	public void setJumping(boolean isJumping) {
		this.jumpActive = isJumping;
	}
	
//	public abstract void act();
	public void setAct(boolean isAct) {
		this.actActive = isAct;
	}
	
	@Override
	public void kill() {
		if (isAlive()) {
			state = State.DYING;
			timeToDie = TIME_BEFORE_DIE;
		}
	}
	
	public int getTimeFinished() {
		if (hasFinished())
			return timeFinished;
		return 0;
	}
	
	public void addCoins(int amount) {
		acquiredCoins += amount;
	}
	
	public void resetCoins() {
		acquiredCoins = 0;
	}
	
	public int getCoins() {
		return acquiredCoins;
	}
	
	private Vec2 stopImpulse = new Vec2(-1f, 0);
	
	protected void stopAction() {
		if (body.getLinearVelocity().x != 0) {
			if (body.getLinearVelocity().x > 0) {
				body.applyLinearImpulse(stopImpulse.mul(body.getMass()/ 10f), body.getPosition());
			} else {
				body.setLinearVelocity(new Vec2(0, 0)); // Just done once
				
			}
		}
	}
	
	public float getMaxJumpVelocity() {
		return maxJumpVelocity;
	}

	public void setMaxJumpVelocity(float maxJumpVelocity) {
		this.maxJumpVelocity = maxJumpVelocity;
	}

	public float getMaxRunVelocity() {
		return maxRunVelocity;
	}

	public void setMaxRunVelocity(float maxRunVelocity) {
		this.maxRunVelocity = maxRunVelocity;
	}

	public float getJumpAceleration() {
		return jumpAceleration;
	}

	public void setJumpAceleration(float jumpAceleration) {
		this.jumpAceleration = jumpAceleration;
	}

	public float getRunAceleration() {
		return runAceleration;
	}

	public void setRunAceleration(float runAceleration) {
		this.runAceleration = runAceleration;
	}

	public float getTimeWaitingForAct() {
		return timeWaitingForAct;
	}

	public void setTimeWaitingForAct(float timeWaitingForAct) {
		this.timeWaitingForAct = timeWaitingForAct;
	}
	
	public int getPercentageLeftToNextAct(){
		return 100;
	}
	public abstract int getStatusImg();
	
//	public abstract int[] getWalkFrames();
//	
//	public abstract int[] getJumpFrames();
//	
//	public abstract int[] getActFrames();
//
//	public abstract float[] getWalkFramesDuration();
//	
//	public abstract float[] getJumpFramesDuration();
//	
//	public abstract float[] getActFramesDuration();

	public int getMaxDoubleJumps() {
		return maxDoubleJumps;
	}

	public void setMaxDoubleJumps(int maxDoubleJumps) {
		this.maxDoubleJumps = maxDoubleJumps;
	}
	
}
