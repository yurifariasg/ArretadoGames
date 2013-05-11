package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.Color;

import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.SpriteManager;

public class LoboGuara extends Player {
	
	private Sprite sprite;
	private int contJump;
	private int contacts;
	private Fixture footFixture;
	
	public LoboGuara(float x, float y, PlayerNumber number, Sprite sprite) {
		super(1f, 10f, number);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.5f); // FIXME Check this size
		body.createFixture(shape,  0.5f);
		body.setType(BodyType.DYNAMIC);
		contJump = 0;
		contacts = 0;
		body.setFixedRotation(false);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(0.4f, 0.1f, new Vec2(0f,-0.5f), 0f);
		this.sprite = sprite;
		
		//footFixture = body.createFixture(footShape, 0f);
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		// TODO Auto-generated method stub
//		canvas.drawDebugRect((int)getPosX(), (int)getPosY(),
//				(int)(getPosX() ), (int)(getPosY()+size));
		
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), 10, 280);

		canvas.saveState();
		canvas.rotatePhysics((float) (180 * - body.getAngle() / Math.PI), getPosX(), getPosY());
		canvas.drawPhysicsDebugRect(getPosX(), getPosY(), 1f, Color.BLUE);
		canvas.restoreState();
		
		
	}

	@Override
	public void jump() {
		sprite.setAnimationState("jump");
//		System.out.println("jump " + contJump);
		if( contJump > 0 || contacts <= 0) return;	
		float impulseX = (8) * body.getMass();
		Vec2 direction = new Vec2(0f,1f);
		direction.normalize();
		direction.mulLocal(impulseX);
		body.applyLinearImpulse(direction, body.getWorldCenter());
		contJump = 10;
//		System.out.println("Jump Player 1");
	}
	
	public void run(){
		body.applyLinearImpulse(new Vec2((10 - body.m_linearVelocity.x) * body.getMass(), 0.0f), body.getPosition());
	}

	@Override
	public void act() {
		// TODO stop moving for awhile or do something else...
		System.out.println("Act Player 1");
	}

	@Override
	public void step() {
		if(contJump >= 0) contJump--;
		run();
	}
	
	public void beginContact(Entity e, Contact contact) {
		//if(contact.m_fixtureA.equals(footFixture) || contact.m_fixtureB.equals(footFixture)){
			System.out.println(" Contacts " + contacts++);
			System.out.println("encostou " + contacts);
		//}
	}

	public void endContact(Entity e , Contact contact) {
		//if(contact.m_fixtureA.equals(footFixture) || contact.m_fixtureB.equals(footFixture)){
			System.out.println(" Contacts " + contacts--);
			System.out.println("DESencostou " + contacts);
		//}
	}

}
