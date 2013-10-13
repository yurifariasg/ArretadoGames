package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Box extends Entity {
	
	private static final int[] STOPPED = {R.drawable.box_stopped};
	private Sprite sprite;
	
	public Box(float x, float y, float size) {
		super(x, y);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size/2, size/2);
		body.createFixture(shape,  0.5f).setFriction(0.8f);
		body.setType(BodyType.DYNAMIC);
		body.setFixedRotation(false);
		
		physRect = new PhysicsRect(size, size);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), physRect);
		canvas.restoreState();
	}


	@Override
	public EntityType getType() {
		return EntityType.BOX;
	}
	
	public int[] getStoppedFrames() {
		return STOPPED;
	}
	
	public float[] getStoppedFramesDuration(){
		return new float[] {0.3f};
	}

	public void setSprite(Sprite sprite){
		this.sprite = sprite;
	}
}
