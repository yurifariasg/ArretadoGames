package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.items.Item;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.world.GameWorld;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.Collection;
import java.util.HashSet;

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

	protected AnimationSwitcher sprite;
	protected int contJump;
	protected int contAct;
    protected Fixture bodyFixture;
	protected Fixture footFixture;
	protected Collection<Body> bodiesContact;
	
	private boolean ghostModeActive;
	private boolean toucanTarget;
    
    private Vec2 stopImpulse = new Vec2(-1f, 0);
    private Item item;
	
	public Player(float x, float y, PlayerNumber playerNumber) {
		super(x, y);
		this.playerNumber = playerNumber;
		hasFinished = false;
		jumpActive = false;
		actActive = false;
		timeToDie = 0;
        bodiesContact = new HashSet<Body>();
        ghostModeActive = false;
        toucanTarget = false;
	}
	
	public void setGhostMode(boolean ghostModeActive) {
	    
	    if (this.ghostModeActive != ghostModeActive) {

            Filter filter = new Filter();
	        if (ghostModeActive) {
	            filter.categoryBits = CollisionFlag.GROUP_3.getValue() ;
	            filter.maskBits = CollisionFlag.GROUP_3.getValue() ;
	            body.setGravityScale(0);
	        } else {
                filter.categoryBits = CollisionFlag.GROUP_1.getValue() ;
                filter.maskBits = CollisionFlag.GROUP_1.getValue() ;
                body.setGravityScale(1);
	        }
	        
            bodyFixture.setFilterData(filter);
	        
	        this.ghostModeActive = ghostModeActive;
	    }
    }
	
	public boolean shouldStop() {
	    return isToucanTarget() || isDead() || hasFinished();
	}
	
	public void setToucanTarget(boolean isToucanTarget) {
	    this.toucanTarget = isToucanTarget;
	}
	
	@Override
	public void step(float timeElapsed){
		if (state == State.DYING) {
			timeToDie -= timeElapsed;
			if (timeToDie <= 0) {
				state = State.DEAD;
				PhysicalWorld.getInstance().addDeadEntity(this);
			}
		}
		
		if (getItem() != null) {
		    getItem().step(timeElapsed);
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
	
	public boolean isGhostMode() {
        return ghostModeActive;
    }
	
	public boolean isToucanTarget() {
	    return toucanTarget;
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

	public int getMaxDoubleJumps() {
		return maxDoubleJumps;
	}

	public void setMaxDoubleJumps(int maxDoubleJumps) {
		this.maxDoubleJumps = maxDoubleJumps;
	}
	
    public void beginContact(Entity e, Contact contact) {
        if( (contact.m_fixtureA.equals(footFixture)
                && (!contact.m_fixtureB.isSensor() || e.getType() == EntityType.FLUID)) ||
                        (contact.m_fixtureB.equals(footFixture)
                                &&(!contact.m_fixtureA.isSensor() || e.getType() == EntityType.FLUID)) ){
            bodiesContact.add(e.body);
            sprite.setAnimationState("default");
        }
    }

    public void endContact(Entity e , Contact contact) {
        if(contact.m_fixtureA.equals(footFixture) || contact.m_fixtureB.equals(footFixture)){
            if(bodiesContact.contains(e.body)){
                bodiesContact.remove(e.body);
                
                if(bodiesContact.size()==0){
                    sprite.setAnimationState("jump");
                }
            }
            
        }
    }
    
    public void setSprite(AnimationSwitcher sprite){
        this.sprite = sprite;
    }
    
    protected double getAngle(){
        //return body.getAngle();
        double angle = 0;
        if(body.getLinearVelocity().length() > 1){
            double cos = Vec2.dot(body.getLinearVelocity(), new Vec2(1,0)) / (body.getLinearVelocity().length());
            cos = Math.abs(cos);
            angle = Math.acos(cos);
            if( body.getLinearVelocity().y < 0 ) angle = angle * -1;
            angle = Math.min(Math.PI/6,angle);
            angle = Math.max(-Math.PI/6,angle);
        }
        return angle;
    }
    
    protected void applyReturn(Vec2 impulse){
        int quant = bodiesContact.size() * 3;
        for(Body b : bodiesContact){
            b.applyLinearImpulse(impulse.mul(-1/quant), b.getWorldCenter());
            b.applyLinearImpulse(impulse.mul(-1/quant), body.getWorldPoint(new Vec2(-0.5f,-0.6f)));
            b.applyLinearImpulse(impulse.mul(-1/quant), body.getWorldPoint(new Vec2(0.5f,-0.6f)));
        }
    }

    public void setItem(Item item) {
        if (item != null && this.item != null) {
            return;
        }
        this.item = item;
    }
    
    public Item getItem() {
        return this.item;
    }
	
}
