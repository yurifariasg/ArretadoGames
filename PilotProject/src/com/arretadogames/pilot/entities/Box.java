package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.canvas.RenderingCanvas;
import com.arretadogames.pilot.render.opengl.OpenGLCanvas;

public class Box extends Entity {
	
	private static final int[] STOPPED = {R.drawable.box_stopped};
	private Sprite sprite;
	private float size;
	
	public Box(float x, float y, float size) {
		super(x, y);
		this.size = size;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size/2, size/2);
		body.createFixture(shape,  7f).setFriction(0.8f);
		body.setType(BodyType.DYNAMIC);
		body.setFixedRotation(false);
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- 0.5f * OpenGLCanvas.physicsRatio), // Top Left
				(- 0.5f * OpenGLCanvas.physicsRatio), // Top Left
				(0.5f * OpenGLCanvas.physicsRatio), // Bottom Right
				(0.5f * OpenGLCanvas.physicsRatio)); // Bottom Right
		
//		canvas.drawRect(new Rect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom), Color.RED);
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), rect, false);
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
