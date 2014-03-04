package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;

public class Box extends Entity {
	
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
//        sprite.render(canvas, physRect, timeElapsed);
		canvas.drawBitmap(R.drawable.box_stopped, physRect);
		canvas.restoreState();
	}

	@Override
	public EntityType getType() {
		return EntityType.BOX;
	}

    @Override
    public void setSprite(AnimationSwitcher sprite) {
    }
	
}
