package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;

public class Ground extends Entity {
	
	public static final int GROUND_LAYER_POSITION = -50;
	
	private static final PhysicsRect GROUND_SURFACE_SIZE = new PhysicsRect(8, 1); // Image Ratio 8:1
    private static final PhysicsRect GROUND_SIZE = new PhysicsRect(2, 2); // Image Ratio 1:1
	
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
	
	private PhysicsRect aux = new PhysicsRect(0, GROUND_SURFACE_SIZE.height());

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
        // We are considering that we have just perpendicular edges
        // and vertices go from left to right
        for (int i = 1 ; i < vec.length ; i++) {
            Vec2 prev = vec[i-1];
            Vec2 current = vec[i];
            
            if (prev.x != current.x) {
                // We have a perpendicular edge
                aux.setWidth(current.x - prev.x);
                aux.setHeight( - GameSettings.GROUND_BOTTOM);
                canvas.saveState();
                canvas.translatePhysics(prev.x + (aux.width() / 2), - aux.height() / 2 + current.y);
                canvas.drawBitmap(R.drawable.inner_ground, aux,
                        aux.width() / GROUND_SIZE.width(),
                        aux.height() / GROUND_SIZE.height());
                
                canvas.restoreState();
                
                if (prev.y == 0 && current.y == 0) {
                    
                    // We have a surface edge
                    aux.setHeight(GROUND_SURFACE_SIZE.height());
                    canvas.saveState();
                    canvas.translatePhysics(prev.x + (aux.width() / 2), -0.2f);
                    canvas.drawBitmap(R.drawable.ground, aux, aux.width() / GROUND_SURFACE_SIZE.width(), 1);
                    
                    canvas.restoreState();
                }
            }
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
