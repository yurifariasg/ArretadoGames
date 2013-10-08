package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.Color;
import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Fire extends Entity implements Steppable{

	private float height;
	private float width;
	private float velocity = 3.2f;
	
	public Fire(float x, float y) {
		super(x, y);
		PolygonShape shape = new PolygonShape();
		width = 1;
		height = 50;
		shape.setAsBox(width/2, height/2);
		Fixture f = body.createFixture(shape,  1f);
		f.setSensor(true);
		body.setType(BodyType.KINEMATIC);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		//canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- width/2 * GLCanvas.physicsRatio), // Top Left
				(- height/2 * GLCanvas.physicsRatio), // Top Left
				(width/2 * GLCanvas.physicsRatio), // Bottom Right
				(height/2 * GLCanvas.physicsRatio)); // Bottom Right
		
		canvas.drawRect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom, Color.YELLOW);
		canvas.restoreState();
		
	}
	
	@Override
	public void beginContact(Entity e, Contact contact) {
		super.endContact(e, contact);
		PhysicalWorld.getInstance().addDeadEntity(e);
	}

	@Override
	public void step(float timeElapsed) {
		body.setLinearVelocity(new Vec2(velocity ,0));
	}

	@Override
	public EntityType getType() {
		return EntityType.FIRE;
	}

	@Override
	public void setSprite(Sprite sprite) {
		// TODO Auto-generated method stub
		
	}
	
	public int getStatusImg(){
		return R.drawable.fire_status;
	}

}
