package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.canvas.RenderingCanvas;

public class LoboGuara extends Player {
	
	private Sprite sprite;
	private int contJump;
	private int contAct;
	private int contacts;
	private Fixture footFixture;
	private Fixture headFixture;
	private int contactsHead;
	
	private static final int[] WALKING = {R.drawable.lobo_g_walking1,
								  		     R.drawable.lobo_g_walking2,
								  		     R.drawable.lobo_g_walking3,
								  		     R.drawable.lobo_g_walking4,
								  		     R.drawable.lobo_g_walking5,
								  		     R.drawable.lobo_g_walking6};

	private static final int[] JUMP = {R.drawable.lobo_g_jump1,
  		  						  		R.drawable.lobo_g_jump2,
  		  						  		R.drawable.lobo_g_jump4};
	
	/*private static final int[] ACT = {R.drawable.lobo_guara_act1,
				 R.drawable.lobo_guara_act2,
				 R.drawable.lobo_guara_act3,
				 };*/
	
	public LoboGuara(float x, float y, PlayerNumber number) {
		super(x, y, number);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.5f); // FIXME Check this size
		footFixture = body.createFixture(shape,  7f);
		body.setType(BodyType.DYNAMIC);
		contJump = 0;
		contacts = 0;
		contactsHead = 0;
		body.setFixedRotation(false);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(0.7f, 0.1f, new Vec2(0f,-0.5f), 0f);
		//footFixture = body.createFixture(footShape, 50f);
		
		PolygonShape headShape = new PolygonShape();
		headShape.setAsBox(0.6f, 0.1f, new Vec2(0f,0.5f), 0f);
		//headFixture = body.createFixture(headShape, 0f);
	}

	double getAngle(){
		double angle = 0;
		if(body.getLinearVelocity().length() > 0.001){
			double cos = Vec2.dot(body.getLinearVelocity(), new Vec2(1,0)) / (body.getLinearVelocity().length());
			cos = Math.abs(cos);
			angle = Math.acos(cos);
			//System.out.println(cos + " - " + angle);
			if( body.getLinearVelocity().y < 0 ) angle = angle * -1;
			angle = Math.min(Math.PI/6,angle);
			angle = Math.max(-Math.PI/6,angle);
		}
		return angle;
	}
	
	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - getAngle() / Math.PI)); // getAngle() ou body.getAngle() ?
		RectF rect = new RectF(
				(- 0.7f* RenderingCanvas.physicsRatio), // Top Left
				(- 1f * RenderingCanvas.physicsRatio), // Top Top Left
				(0.71f * RenderingCanvas.physicsRatio), // Bottom Right
				(0.55f * RenderingCanvas.physicsRatio)); // Bottom Right
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), rect, false);
		canvas.restoreState();
		
	}

	@Override
	public void jump() {
		sprite.setAnimationState("jump");
		if( contJump > 0 || contacts <= 0) return;	
		float impulseX = (8) * body.getMass();
		Vec2 direction = new Vec2(1,6);
		direction.normalize();
		direction.mulLocal(impulseX);
		body.applyLinearImpulse(direction, body.getWorldCenter());
		contJump = 5;
//		System.out.println("Jump Player 1");
	}
	
	public void run(){
		if(body.getLinearVelocity().x < 2){ 
			body.applyLinearImpulse(new Vec2(1 * body.getMass(),0f), body.getWorldCenter());
		}
		if(contacts > 0 && body.getLinearVelocity().x < 7f){
			float force = (11) * body.getMass();
			Vec2 direction = new Vec2((float)Math.cos(body.getAngle() ),(float)Math.sin(body.getAngle()));
			direction.normalize();
			direction.mulLocal(force);
			body.applyForceToCenter(direction);
		}
//		body.setLinearVelocity(new Vec2(5, body.getLinearVelocity().y));
	}

	@Override
	public void act() {
		if( contactsHead > 0 && contAct == 0){
			body.applyLinearImpulse(new Vec2(0f,2 * body.getMass()), body.getWorldCenter());
			body.applyAngularImpulse(-0.5f);
		} else if( contacts > 0 && contAct == 0){
			float impulse = (5) * body.getMass();
			Vec2 direction = new Vec2((float)Math.cos(body.getAngle() ),(float)Math.sin(body.getAngle()));
			direction.normalize();
			direction.mulLocal(impulse);
			body.applyLinearImpulse(direction, body.getWorldCenter());
			contAct = 50;
		}
		
	}

	@Override
	public void step(float timeElapsed) {
		if(contJump > 0) contJump--;
		if(contAct > 0 ) contAct--;
		run();
	}
	
	public void beginContact(Entity e, Contact contact) {
		sprite.setAnimationState("walking");
		if(contact.m_fixtureA.equals(footFixture) || contact.m_fixtureB.equals(footFixture)){
			sprite.setAnimationState("walking");
			contacts++;
		} else if(contact.m_fixtureA.equals(headFixture) || contact.m_fixtureB.equals(headFixture)){
			contactsHead++;
		}
	}

	public void endContact(Entity e , Contact contact) {
		if(contact.m_fixtureA.equals(footFixture) || contact.m_fixtureB.equals(footFixture)){
			contacts--;
		} else if (contact.m_fixtureA.equals(headFixture) || contact.m_fixtureB.equals(headFixture)){
			contactsHead--;
		}
	}

	@Override
	public int[] getWalkFrames() {
		return WALKING;
	}
	
	public float[] getWalkFramesDuration(){
		return new float[] {0.15f, 0.15f, 0.15f, 0.15f, 0.15f ,0.15f};
	}

	@Override
	public int[] getJumpFrames() {
		return JUMP;
	}
	
	public float[] getJumpFramesDuration(){
		return new float[] {0.3f, 0.3f, 0f};
	}

	@Override
	public int[] getActFrames() {
		return null;
		//TODO
/*		Bitmap[] frames = new Bitmap[ACT.length];
		for (int i = 0; i < ACT.length; i++) {
			frames[i] = ImageLoader.loadImage(ACT[i]);
		}
		return frames;*/
	}
	
	public float[] getActFramesDuration(){
		return new float[] {0.15f, 0.15f};
	}
	
	public void setSprite(Sprite sprite){
		this.sprite = sprite;
	}
}
