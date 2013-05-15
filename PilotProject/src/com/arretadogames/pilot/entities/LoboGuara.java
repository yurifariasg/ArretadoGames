package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.GameCamera;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.render.Sprite;

public class LoboGuara extends Player {
	
	private Sprite sprite;
	private int contJump;
	private int contacts;
	private Fixture footFixture;
	
<<<<<<< HEAD
	public LoboGuara(float x, float y, PlayerNumber number, Sprite sprite) {
		super(x, y, number);
=======
	private static final int[] WALKING = {R.drawable.lobo_guara1,
								  		     R.drawable.lobo_guara2,
								  		     R.drawable.lobo_guara3,
								  		     R.drawable.lobo_guara4,
								  		     R.drawable.lobo_guara5,
								  		     R.drawable.lobo_guara6};

	private static final int[] JUMP = {R.drawable.lobo_guara_jump1,
  		  						  		R.drawable.lobo_guara_jump2};
	
	/*private static final int[] ACT = {R.drawable.lobo_guara_act1,
				 R.drawable.lobo_guara_act2,
				 R.drawable.lobo_guara_act3,
				 };*/
	
	public LoboGuara(float x, float y, PlayerNumber number) {
		super(1f, 10f, number);
>>>>>>> branch 'master' of https://github.com/yurifariasg/ArretadoGames.git
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.5f); // FIXME Check this size
		body.createFixture(shape,  0f);
		body.setType(BodyType.DYNAMIC);
		contJump = 0;
		contacts = 0;
		body.setFixedRotation(false);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(0.4f, 0.1f, new Vec2(0f,-0.5f), 0f);
		footFixture = body.createFixture(footShape, 10f);

		//footFixture = body.createFixture(footShape, 0f);
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		// TODO Auto-generated method stub
//		canvas.drawDebugRect((int)getPosX(), (int)getPosY(),
//				(int)(getPosX() ), (int)(getPosY()+size));
		
<<<<<<< HEAD
//		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), 10, 280);

=======
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), 10, 280);
>>>>>>> branch 'master' of https://github.com/yurifariasg/ArretadoGames.git
		
		canvas.saveState();
		canvas.rotatePhysics((float) (180 * - body.getAngle() / Math.PI), getPosX(), getPosY());
		canvas.drawPhysicsDebugRect(getPosX(), getPosY(), 1f, Color.BLUE);
		canvas.restoreState();
		
	}

	@Override
	public void jump() {
//		sprite.setAnimationState("jump");
		if( contJump > 0 || contacts <= 0) return;	
		float impulseX = (8) * body.getMass();
		Vec2 direction = new Vec2(0f,1f);
		direction.normalize();
		direction.mulLocal(impulseX);
		body.applyLinearImpulse(direction, body.getWorldCenter());
		contJump = 5;
//		System.out.println("Jump Player 1");
	}
	
	public void run(){
		if(contacts > 0 && body.getLinearVelocity().x < 20f){
			body.applyForceToCenter(new Vec2(5 * body.getMass(), 0.0f));
		}
		//body.setLinearVelocity(new Vec2(10, 20));
		
		
	}

	@Override
	public void act() {
		// TODO stop moving for awhile or do something else...
		System.out.println("Act Player 1");
		//body.applyLinearImpulse((new Vec2(20 * body.getMass(), 0.0f)),body.getWorldCenter());
		body.applyAngularImpulse(-2f);
	}

	@Override
	public void step(float timeElapsed) {
		if(contJump >= 0) contJump--;
		run();
	}
	
	public void beginContact(Entity e, Contact contact) {
		if(contact.m_fixtureA.equals(footFixture) || contact.m_fixtureB.equals(footFixture)){
		contacts++;
		}
	}

	public void endContact(Entity e , Contact contact) {
		if(contact.m_fixtureA.equals(footFixture) || contact.m_fixtureB.equals(footFixture)){
		contacts--;
		}
	}

	@Override
	public Bitmap[] getWalkFrames() {
		Bitmap[] frames = new Bitmap[WALKING.length];
		for (int i = 0; i < WALKING.length; i++) {
			frames[i] = ImageLoader.loadImage(WALKING[i]);
		}
		return frames;
	}
	
	public float[] getWalkFramesDuration(){
		return new float[] {0.15f, 0.15f, 0.15f, 0.15f, 0.15f ,0.15f};
	}

	@Override
	public Bitmap[] getJumpFrames() {
		Bitmap[] frames = new Bitmap[JUMP.length];
		for (int i = 0; i < JUMP.length; i++) {
			frames[i] = ImageLoader.loadImage(JUMP[i]);
		}
		return frames;
	}
	
	public float[] getJumpFramesDuration(){
		return new float[] {0.3f, 0.4f};
	}

	@Override
	public Bitmap[] getActFrames() {
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
