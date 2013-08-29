package com.arretadogames.pilot.render;

import com.arretadogames.pilot.entities.Box;
import com.arretadogames.pilot.entities.Breakable;
import com.arretadogames.pilot.entities.Coin;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.Fruit;
import com.arretadogames.pilot.entities.Ground;
import com.arretadogames.pilot.entities.OneWayWall;
import com.arretadogames.pilot.entities.Player;

public class SpriteManager {
	
	public Sprite getSprite(Entity en){
		Sprite sprite = new Sprite();
		
		if (en.getType() == EntityType.PLAYER){
			Player player = (Player) en;
			setPlayerSprites(sprite, player);
		}else if(en.getType() == EntityType.BOX){
			Box box = (Box) en;
			setBoxSprites(sprite, box);
		}else if(en.getType() == EntityType.FRUIT){
			Fruit fruit = (Fruit) en;
			setFruitSprites(sprite, fruit);
		}else if(en.getType() == EntityType.GROUND){
			Ground ground = (Ground) en;
		}else if(en.getType() == EntityType.COIN){
			Coin coin = (Coin) en;
			setCoinSprites(sprite);
		}else if(en.getType() == EntityType.BREAKABLE){
			Breakable breakable = (Breakable) en;
			setBreakableSprites(sprite, breakable);
		}else if(en.getType() == EntityType.ONEWAY_WALL){
			OneWayWall oneWayWall = (OneWayWall) en;
			setOneWayWallSprite(sprite, oneWayWall);
		}
		return sprite;
	}
	
	private void setOneWayWallSprite(Sprite sprite, OneWayWall oneWayWall) {
		String name = "stopped";
		sprite.setAnimationState(name);
		sprite.addState(new SpriteState(name,
				oneWayWall.getStoppedFrames(),
				oneWayWall.getStoppedFramesDuration()));
		oneWayWall.setSprite(sprite);
	}

	private void setBreakableSprites(Sprite sprite, Breakable breakable) {
		String name = "stopped";
		sprite.setAnimationState(name);
		sprite.addState(new SpriteState(name,
				breakable.getStoppedFrames(),
				breakable.getStoppedFramesDuration()));
		breakable.setSprite(sprite);
	}

	private void setCoinSprites(Sprite sprite) {
		String name = "stopped";
		int[] frames = Coin.FRAMES;
		float[] framesDur = Coin.DURATION;
		sprite.setAnimationState(name);
		sprite.addState(new SpriteState(name, frames, framesDur));
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
		String name = "stopped";
		int[] frames = box.getStoppedFrames();
		float[] framesDur = box.getStoppedFramesDuration();
		sprite.setAnimationState(name);
		sprite.addState(new SpriteState(name, frames, framesDur));
	}
	
	
	private void setFruitSprites(Sprite sprite, Fruit fruit) {
		String name = "stopped";
		int[] frames = fruit.getStoppedFrames();
		float[] framesDur = fruit.getStoppedFramesDuration();
		sprite.setAnimationState(name);
		sprite.addState(new SpriteState(name, frames, framesDur));
	}
	
	private void setGroundImage(Sprite sprite) {
		String name = "stopped";
		int[] frames = new int[1];
		for (int i = 0; i < frames.length; i++) {
//			frames[i] = ImageLoader.loadImage(BOX_STOPPED[i]);			
		}
		float[] framesDur = {1f};
		sprite.addState(new SpriteState(name, frames, framesDur));
	}

	private void setFruitImage(Sprite sprite) {
	}
}
