package com.arretadogames.pilot.entities;

import android.graphics.Color;

import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.util.Util;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;

public class Ground extends Entity {
	
	public static final int GROUND_LAYER_POSITION = -50;
	private int GROUND_SURFACE_COLOR = Color.argb(255, 137, 71, 38);
	private int GROUND_BOTTOM_COLOR = Color.argb(255, 0, 0, 0);
	
	private Vec2[] vec;
	boolean chain = true;
	
	public Ground(Vec2[] vec, int count) {
		super(0, 0);
		chain = true;
		this.vec = vec;
		ChainShape shape = new ChainShape();
		shape.createChain(vec, count);
		Fixture bodyFixture = body.createFixture(shape, 0.5f);
		bodyFixture.setFriction(0.5f);
		bodyFixture.setRestitution(0.1f);
		body.setType(BodyType.STATIC);
		
        Filter filter = new Filter();
        filter.categoryBits = CollisionFlag.GROUP_GROUND.getValue();
        filter.maskBits = CollisionFlag.GROUP_COMMON_ENTITIES.getValue() | CollisionFlag.GROUP_PLAYERS.getValue();
        bodyFixture.setFilterData(filter);
		
	}

	public Ground() {
		super(0,0);
		chain = false;
		vec = new Vec2[2];
		vec[0] = new Vec2(-10000,0);
		vec[1] = new Vec2(10000,0);
		ChainShape shape = new ChainShape();
		shape.createChain(vec, 2);
		Fixture bodyFixture = body.createFixture(shape, 0.5f);
		body.setType(BodyType.STATIC);
		

        Filter filter = new Filter();
        filter.categoryBits = CollisionFlag.GROUP_GROUND.getValue();
        filter.maskBits = CollisionFlag.GROUP_GROUND.getValue();
        bodyFixture.setFilterData(filter);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		if(chain){
			drawGround(canvas, vec);
		
        // Draw Darker Lines
        int width = 2;
        int color = Color.rgb(77, 34, 0);
        canvas.saveState();
        
//        canvas.translatePhysics(0, 0);
        
        canvas.drawGroundLines(vec, vec.length, width, color);
        canvas.restoreState();
		}else{
			drawGround(canvas, vec);
			int width = 2;
	        int color = Color.rgb(77, 34, 0);
	        canvas.drawGroundLines(vec, vec.length, width, color);
		}
	}
	
	private void drawGround(GLCanvas canvas, Vec2[] lines) {
			
		for (int i = 1 ; i < lines.length ; i++) {
			if (lines[i - 1].x == lines[i].x)
				continue;
			
			canvas.drawRectFromPhysics(
					lines[i - 1].x, lines[i - 1].y,
					lines[i - 1].x, GameSettings.GROUND_BOTTOM,
					lines[i].x, GameSettings.GROUND_BOTTOM,
					lines[i].x, lines[i].y,
					Util.interpolateColor(GROUND_SURFACE_COLOR, GROUND_BOTTOM_COLOR, lines[i - 1].y / GameSettings.GROUND_BOTTOM),
					GROUND_BOTTOM_COLOR,
					GROUND_BOTTOM_COLOR,
					Util.interpolateColor(GROUND_SURFACE_COLOR, GROUND_BOTTOM_COLOR, lines[i].y / GameSettings.GROUND_BOTTOM));
		}
	}

	@Override
	public int getLayerPosition() {
		return GROUND_LAYER_POSITION;
	}

	@Override
	public EntityType getType() {
		return EntityType.GROUND;
	}

	@Override
	public void setSprite(AnimationSwitcher sprite) {
		// Doesnt use one
	}

}
