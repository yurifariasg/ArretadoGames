package com.arretadogames.pilot.render;

import com.arretadogames.pilot.entities.Box;
import com.arretadogames.pilot.entities.Breakable;
import com.arretadogames.pilot.entities.Coin;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.OneWayWall;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.Spike;

public class SpriteManager {
	final String STOPPED_NAME = "stopped";
	
	public Sprite getSprite(Entity en){
		Sprite sprite = new Sprite();
		
		if (en.getType() == EntityType.PLAYER){
			Player player = (Player) en;
			setPlayerSprites(sprite, player);
		}else if(en.getType() == EntityType.BOX){
			Box box = (Box) en;
			setBoxSprites(sprite, box);
		}else if(en.getType() == EntityType.COIN){
//			Coin coin = (Coin) en;
			setCoinSprites(sprite);
		}else if(en.getType() == EntityType.BREAKABLE){
			Breakable breakable = (Breakable) en;
			setBreakableSprites(sprite, breakable);
		}else if(en.getType() == EntityType.ONEWAY_WALL){
			OneWayWall oneWayWall = (OneWayWall) en;
			setOneWayWallSprite(sprite, oneWayWall);
		}else if(en.getType() == EntityType.SPIKE){
			Spike spike = (Spike) en;
			setSpikeSprite(sprite, spike);
		}
		return sprite;
	}
		
	private void setSpikeSprite(Sprite sprite, Spike spike){
		sprite.setAnimationState(STOPPED_NAME);
		sprite.addState(new SpriteState(STOPPED_NAME,
				spike.getStoppedFrames(),
				spike.getStoppedFramesDuration()));
		spike.setSprite(sprite);
	}
	
	private void setOneWayWallSprite(Sprite sprite, OneWayWall oneWayWall) {
		sprite.setAnimationState(STOPPED_NAME);
		sprite.addState(new SpriteState(STOPPED_NAME,
				oneWayWall.getStoppedFrames(),
				oneWayWall.getStoppedFramesDuration()));
		oneWayWall.setSprite(sprite);
	}

	private void setBreakableSprites(Sprite sprite, Breakable breakable) {
		sprite.setAnimationState(STOPPED_NAME);
		sprite.addState(new SpriteState(STOPPED_NAME,
				breakable.getStoppedFrames(),
				breakable.getStoppedFramesDuration()));
		breakable.setSprite(sprite);
	}

	private void setCoinSprites(Sprite sprite) {
		int[] frames = Coin.FRAMES;
		float[] framesDur = Coin.DURATION;
		sprite.setAnimationState(STOPPED_NAME);
		sprite.addState(new SpriteState(STOPPED_NAME, frames, framesDur));
	}

	private void setPlayerSprites(Sprite sprite, Player player){

		String name = "walking";
		int[] frames = player.getWalkFrames();
		float[] framesDur = player.getWalkFramesDuration();
		sprite.setAnimationState(name);
		sprite.addState(new SpriteState(name, frames, framesDur));
		
		name = "jump";
		frames = player.getJumpFrames();
		framesDur = player.getJumpFramesDuration();
		sprite.addState(new SpriteState(name, frames, framesDur));
		
		name = "act";
		frames = player.getActFrames();
		framesDur = player.getActFramesDuration();
		sprite.addState(new SpriteState(name, frames, framesDur));
		
	}
	
	private void setBoxSprites(Sprite sprite, Box box) {
		int[] frames = box.getStoppedFrames();
		float[] framesDur = box.getStoppedFramesDuration();
		sprite.setAnimationState(STOPPED_NAME);
		sprite.addState(new SpriteState(STOPPED_NAME, frames, framesDur));
	}
	
}
