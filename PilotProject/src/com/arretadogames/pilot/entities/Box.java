package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;

import android.graphics.Bitmap;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.render.Sprite;

public class Box extends Entity {
	
	private static final int[] STOPPED = {R.drawable.ic_launcher};
	private Sprite sprite;
	private float size;
	public Box(float x, float y, float size) {
		super(x, y);
		this.size = size;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size/2, size/2);
		body.createFixture(shape,  5f);
		body.setType(BodyType.DYNAMIC);
		body.setFixedRotation(false);
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
//		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), getPosX(), getPosY());
		
		canvas.saveState();
		canvas.rotatePhysics((float) (180 * - body.getAngle() / Math.PI), getPosX(), getPosY());
		canvas.drawPhysicsDebugRect(getPosX(), getPosY(), size );
		canvas.restoreState();
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Box Logic
	}

	@Override
	public EntityType getType() {
		return EntityType.BOX;
	}
	
	public Bitmap[] getStoppedFrames() {
		Bitmap[] frames = new Bitmap[STOPPED.length];
		for (int i = 0; i < STOPPED.length; i++) {
			frames[i] = ImageLoader.loadImage(STOPPED[i]);
		}
		return frames;
	}
	
	public float[] getStoppedFramesDuration(){
		return new float[] {0.3f};
	}

	public void setSprite(Sprite sprite){
		this.sprite = sprite;
	}
}
