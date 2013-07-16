package com.arretadogames.pilot.entities;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.PulleyJoint;
import org.jbox2d.dynamics.joints.PulleyJointDef;

import android.graphics.RectF;

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
		super(10000,0);
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
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		Vec2 lines[] = new Vec2[4];
		lines[0] = anchorA;
		lines[1] = groundAnchorA;
		lines[3] = anchorB;
		lines[2] = groundAnchorB;
//		canvas.drawRect(new Rect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom), Color.RED);
		canvas.drawPhysicsLines(lines);
		canvas.restoreState();
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

}
