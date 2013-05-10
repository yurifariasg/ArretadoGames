package com.arretadogames.pilot.render;

import android.graphics.Bitmap;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.loading.ImageLoader;

public class SpriteManager {
	
	private final int[] GUARA_WALKING = {R.drawable.lobo_guara1,
							   		   R.drawable.lobo_guara2,
							   		   R.drawable.lobo_guara3,
							   		   R.drawable.lobo_guara4,
							   		   R.drawable.lobo_guara5,
							   		   R.drawable.lobo_guara6};
	
	private final int[] GUARA_JUMP = {R.drawable.lobo_guara_jump1,
							   		   R.drawable.lobo_guara_jump2};
	
	public SpriteManager() {
	}
	
	public Sprite getSprite(EntityType type){
		
		Sprite sprite = new Sprite();
		
		if (type == EntityType.PLAYER){
			setWalk(sprite);
			setJump(sprite);
			setAct(sprite);
		}
		
		return sprite;
	}
	
	private void setWalk(Sprite sprite){
		String name = "walking";
		Bitmap[] frames = new Bitmap[6];
		for (int i = 0; i < 6; i++) {
			frames[i] = ImageLoader.loadImage(GUARA_WALKING[i]);
		}
		float[] framesDur = {0.15f, 0.15f, 0.15f, 0.15f, 0.15f ,0.15f};
		sprite.setAnimationState(name);
		sprite.addState(new SpriteState(name, frames, framesDur));
	}
	
	private void setJump(Sprite sprite){
		String name = "jump";
		Bitmap[] frames = new Bitmap[2];
		for (int i = 0; i < 2; i++) {
			frames[i] = ImageLoader.loadImage(GUARA_JUMP[i]);
		}
		float[] framesDur = {0.4f, 0.3f};
		sprite.addState(new SpriteState(name, frames, framesDur));
		
	}
	
	private void setAct(Sprite sprite){
		
	}
}
