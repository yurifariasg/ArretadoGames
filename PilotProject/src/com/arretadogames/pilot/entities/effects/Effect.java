package com.arretadogames.pilot.entities.effects;

import org.jbox2d.common.Vec2;

import android.graphics.RectF;

import com.arretadogames.pilot.entities.LayerEntity;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Effect implements Renderable, LayerEntity {
    
    private AnimationSwitcher animation;
    private RectF rect;
    private PhysicsRect pRect;
    private Vec2 position;
    private int layerPosition = 0;
    private float xOffset, yOffset;

    public void setAnimation(AnimationSwitcher animation) {
		this.animation = animation;
	}
    
    public void setLayerPosition(int layerPosition) {
		this.layerPosition = layerPosition;
	}
    
    public void setPosition(Vec2 position) {
		this.position = position;
	}
    
    public Vec2 getPosition() {
		return position;
	}
    
    public void setPhysicsRect(PhysicsRect rect) {
    	this.pRect = rect;
	}
    
    public void setRectF(RectF rect) {
    	this.rect = rect;
    }
    
    public void setOffsets(float xOffset, float yOffset) {
    	this.xOffset = xOffset;
    	this.yOffset = yOffset;
	}

    @Override
    public int getLayerPosition() {
        return layerPosition;
    }

    @Override
    public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		
    	if (rect == null) {
    		canvas.translatePhysics(position.x + xOffset, position.y + yOffset);
            animation.render(canvas, pRect, timeElapsed);
    	} else {
    		canvas.translate(position.x + xOffset, position.y + yOffset);
    		animation.render(canvas, rect, timeElapsed);
    	}
    	
        canvas.restoreState();
    }

	public boolean isFinished() {
		return false;
	}
	
}
