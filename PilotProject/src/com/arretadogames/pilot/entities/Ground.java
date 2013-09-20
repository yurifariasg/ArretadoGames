package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import android.graphics.Color;

import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Ground extends Entity {
	
	private Vec2[] vec;

	public Ground(Vec2[] vec, int count) {
		super(0, 0);
		this.vec = vec;
		ChainShape shape = new ChainShape();
		shape.createChain(vec, count);
		body.createFixture(shape, 0.5f);
		body.setType(BodyType.STATIC);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.drawPhysicsLines(vec);
		
        // Draw Darker Lines
        int width = 2;
        int color = Color.rgb(77, 34, 0);
        for (int lineIndex = 1 ; lineIndex < vec.length ; lineIndex++) {
        	canvas.drawLine(
        			vec[lineIndex - 1].x * GLCanvas.physicsRatio, DisplaySettings.TARGET_HEIGHT - vec[lineIndex - 1].y * GLCanvas.physicsRatio,
        			vec[lineIndex].x * GLCanvas.physicsRatio, DisplaySettings.TARGET_HEIGHT - vec[lineIndex].y * GLCanvas.physicsRatio,
        			width, color);
        	
        }
	}

	@Override
	public EntityType getType() {
		return EntityType.GROUND;
	}

	@Override
	public void setSprite(Sprite sprite) {
		// Doesnt use one
	}

}
