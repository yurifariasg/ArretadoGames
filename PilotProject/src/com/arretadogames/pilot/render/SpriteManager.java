package com.arretadogames.pilot.render;

import android.graphics.Bitmap;

import com.arretadogames.pilot.entities.Box;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.Fruit;
import com.arretadogames.pilot.entities.Ground;
import com.arretadogames.pilot.entities.Player;

public class SpriteManager {
	
	public SpriteManager() {
	}
	
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
		}else if(en.getType() == EntityType.GROUND){
			Ground ground = (Ground) en;
		}
		return sprite;
	}
	
	private void setPlayerSprites(Sprite sprite, Player player){

		String name = "walking";
		Bitmap[] frames = player.getWalkFrames();
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
		Bitmap[] frames = box.getStoppedFrames();
		float[] framesDur = box.getStoppedFramesDuration();
		sprite.setAnimationState(name);
		sprite.addState(new SpriteState(name, frames, framesDur));
	}
	
	
	private void setBoxImage(Sprite sprite) {
		String name = "stopped";
		Bitmap[] frames = new Bitmap[1];
		for (int i = 0; i < frames.length; i++) {
//			frames[i] = ImageLoader.loadImage(BOX_STOPPED[i]);			
		}
		float[] framesDur = {1f};
		sprite.addState(new SpriteState(name, frames, framesDur));
	}
	
	private void setGroundImage(Sprite sprite) {
		String name = "stopped";
		Bitmap[] frames = new Bitmap[1];
		for (int i = 0; i < frames.length; i++) {
//			frames[i] = ImageLoader.loadImage(BOX_STOPPED[i]);			
		}
		float[] framesDur = {1f};
		sprite.addState(new SpriteState(name, frames, framesDur));
	}

	private void setFruitImage(Sprite sprite) {
	}
}
