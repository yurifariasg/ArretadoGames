package com.arretadogames.pilot.entities;


import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.Color;
import android.graphics.RectF;

import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class LianaNode extends Entity implements Steppable{

	private float height;
	private float width;
	private Liana liana;
	
	public LianaNode(float x, float y, float width , float height, Liana liana) {
		super(x, y);
		this.height = height;
		this.width = width;
		this.liana = liana;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2, height/2);

		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 3.0f;
		fd.friction = 0.2f;

		body.setType(BodyType.DYNAMIC);
		body.createFixture(fd).setSensor(true);

	}

	@Override
	public void step(float timeElapsed) {
		body.applyLinearImpulse((new Vec2(-0.1f,0)).mul(body.getMass()), new Vec2(body.getWorldCenter().x,body.getWorldCenter().y));
	}
	
	@Override
	public void beginContact(Entity e, Contact contact) {
		super.beginContact(e, contact);
		if(e.getType() == EntityType.PLAYER && e instanceof MacacoPrego && ((Player)e).actActive){
			liana.playerContact(e,this);
		}
		
	}

	@Override
	public EntityType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSprite(Sprite sprite) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(-width/2 * GLCanvas.physicsRatio), // Top Left
				(-height/2 * GLCanvas.physicsRatio), // Top Left
				(width/2 * GLCanvas.physicsRatio), // Bottom Right
				(height/2 * GLCanvas.physicsRatio)); // Bottom Right
		
		
		canvas.drawRect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom, Color.GRAY);
		canvas.restoreState();
		
	}
}
