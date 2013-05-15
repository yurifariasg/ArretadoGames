package com.arretadogames.pilot.render;

import android.graphics.Bitmap;

import com.arretadogames.pilot.entities.Box;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.Fruit;
import com.arretadogames.pilot.entities.Ground;
import com.arretadogames.pilot.entities.Player;

public class SpriteManager {
	
/*	private final int[] WALKING = {R.drawable.lobo_guara1,
							   		     R.drawable.lobo_guara2,
							   		     R.drawable.lobo_guara3,
							   		     R.drawable.lobo_guara4,
							   		     R.drawable.lobo_guara5,
							   		     R.drawable.lobo_guara6};
	
	private final int[] JUMP = {R.drawable.lobo_guara_jump1,
							   		  R.drawable.lobo_guara_jump2};
	
	private final int[] ACT = {R.drawable.lobo_guara_act1,
	   		  						 R.drawable.lobo_guara_act2,
	   		  						 R.drawable.lobo_guara_act3,
	   		  						 };
*/	
	public SpriteManager() {
	}
	
	public Sprite getSprite(Entity en){
		Sprite sprite = new Sprite();
		
		if (en.getType() == EntityType.PLAYER){
			Player player = (Player) en;
			setPlayerSprites(sprite, player);
		}else if(en.getType() == EntityType.BOX){
			Box player = (Box) en;			
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
		float[] framesDur = {0.15f, 0.15f, 0.15f, 0.15f, 0.15f ,0.15f};
		sprite.setAnimationState(name);
		sprite.addState(new SpriteState(name, frames, framesDur));
		
		name = "jump";
		frames = player.getJumpFrames();
		framesDur = new float[]{0.4f, 0.3f};
		sprite.addState(new SpriteState(name, frames, framesDur));
		
		name = "act";
		frames = player.getActFrames();
		framesDur = new float[]{0.4f, 0.3f};
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
