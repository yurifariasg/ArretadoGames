package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Coin extends Entity {
	
	public final static int[] FRAMES = {
		R.drawable.coin_1_1,R.drawable.coin_1_2,R.drawable.coin_1_3,R.drawable.coin_1_4,
		R.drawable.coin_2_1,R.drawable.coin_2_2,R.drawable.coin_2_3,R.drawable.coin_2_4,
		R.drawable.coin_3_1,R.drawable.coin_3_2,R.drawable.coin_3_3,R.drawable.coin_3_4,
		R.drawable.coin_4_1,R.drawable.coin_4_2,R.drawable.coin_4_3,R.drawable.coin_4_4};
	
	public final static float[] DURATION = {
		0.08f, 0.08f, 0.08f, 0.08f,
		0.08f, 0.08f, 0.08f, 0.08f,
		0.08f, 0.08f, 0.08f, 0.08f,
		0.08f, 0.08f, 0.08f, 0.08f
	};
	
	private Sprite sprite;
	private int value;

	public Coin(float x, float y, int value) {
		super(x, y);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(0.5f);
		body.createFixture(shape, 0f).setSensor(true);
		body.setType(BodyType.KINEMATIC);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- 0.2f * GLCanvas.physicsRatio), // Top Left
				(- 0.2f * GLCanvas.physicsRatio), // Top Left
				(0.2f * GLCanvas.physicsRatio), // Bottom Right
				(0.2f * GLCanvas.physicsRatio)); // Bottom Right
		
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), rect, false);
		canvas.restoreState();
		
	}

	@Override
	public EntityType getType() {
		return EntityType.COIN;
	}

	@Override
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	@Override
	public void beginContact(Entity e, Contact contact) {
		super.beginContact(e, contact);
		System.out.println("Colidiu");
		if (e.getType() == EntityType.PLAYER && isAlive()) {
			System.out.println("com player");
			Player p = (Player) e;
			p.addCoins(value);
			setDead(true);
			PhysicalWorld.getInstance().addDeadEntity(this);
		}
	}

}
