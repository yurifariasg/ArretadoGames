package com.arretadogames.pilot.entities;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.PulleyJoint;
import org.jbox2d.dynamics.joints.PulleyJointDef;

import android.graphics.Color;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Pulley extends Entity {

	private PulleyJoint joint;
	private Entity a;
	private Vec2 anchorA;
	private float ratio;
	private Vec2 anchorB;
	private Entity b;
	private Vec2 groundAnchorA;
	private Vec2 groundAnchorB;

	public Pulley(Entity a, Vec2 anchorA,Entity b, Vec2 anchorB, Vec2 groundAnchorA, Vec2 groundAnchorB, float ratio) {
		super((anchorA.x + anchorB.x) / 2, (anchorA.y + anchorB.y) / 2);
		this.a = a;
		this.anchorA = anchorA;
		this.b = b;
		this.anchorB = anchorB;
		this.groundAnchorA = groundAnchorA;
		this.groundAnchorB = groundAnchorB;
		this.ratio = ratio;
		PulleyJointDef pd = new PulleyJointDef();
		pd.initialize(a.body, b.body, groundAnchorA, groundAnchorB, anchorA, anchorB, ratio);
		joint = (PulleyJoint) world.createJoint(pd);
		
		physRect = new PhysicsRect(0.4f, 0.4f);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		Vec2 lines[] = new Vec2[4];
		lines[0] = anchorA;
		lines[1] = groundAnchorA;
		lines[3] = anchorB;
		lines[2] = groundAnchorB;
		int color = Color.rgb(77, 34, 0);;
		float width = 0.05f * GLCanvas.physicsRatio;
		canvas.drawLine(a.getPosX() * GLCanvas.physicsRatio, GameSettings.TARGET_HEIGHT - a.getPosY() * GLCanvas.physicsRatio, anchorA.x * GLCanvas.physicsRatio, GameSettings.TARGET_HEIGHT - anchorA.y * GLCanvas.physicsRatio, width, color);
		canvas.drawLine(anchorA.x * GLCanvas.physicsRatio, GameSettings.TARGET_HEIGHT - anchorA.y * GLCanvas.physicsRatio, anchorB.x * GLCanvas.physicsRatio, GameSettings.TARGET_HEIGHT - anchorB.y * GLCanvas.physicsRatio, width, color);
		canvas.drawLine(anchorB.x * GLCanvas.physicsRatio, GameSettings.TARGET_HEIGHT - anchorB.y * GLCanvas.physicsRatio, b.getPosX() * GLCanvas.physicsRatio, GameSettings.TARGET_HEIGHT - b.getPosY() * GLCanvas.physicsRatio, width, color);
		
		// Rendering Pulley Entities
		a.render(canvas, timeElapsed);
		b.render(canvas, timeElapsed);
		
		// Draw First Sheave
		canvas.saveState();
		canvas.translatePhysics(anchorA.x, anchorA.y);
//		RectF rect = new RectF(
//				(- 0.2f * GLCanvas.physicsRatio), // Top Left
//				(- 0.2f * GLCanvas.physicsRatio), // Top Left
//				(0.2f * GLCanvas.physicsRatio), // Bottom Right
//				(0.2f * GLCanvas.physicsRatio)); // Bottom Right
		
//		canvas.drawBitmap(R.drawable.sheave, rect, false);
		canvas.drawBitmap(R.drawable.sheave, physRect);
		canvas.restoreState();
		
		canvas.saveState();
		canvas.translatePhysics(anchorB.x, anchorB.y);
//		rect = new RectF(
//				(- 0.2f * GLCanvas.physicsRatio), // Top Left
//				(- 0.2f * GLCanvas.physicsRatio), // Top Left
//				(0.2f * GLCanvas.physicsRatio), // Bottom Right
//				(0.2f * GLCanvas.physicsRatio)); // Bottom Right
//		
//		canvas.drawBitmap(R.drawable.sheave, rect, false);
		canvas.drawBitmap(R.drawable.sheave, physRect);
		canvas.restoreState();
		
		canvas.restoreState();
	}

	@Override
	public EntityType getType() {
		// TODO Auto-generated method stub
		return EntityType.PULLEY;
	}

	@Override
	public void setSprite(Sprite sprite) {
		// TODO Auto-generated method stub

	}

}
