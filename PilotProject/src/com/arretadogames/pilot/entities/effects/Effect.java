package com.arretadogames.pilot.entities.effects;

import android.graphics.Color;
import android.graphics.RectF;

import com.arretadogames.pilot.entities.LayerEntity;
import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.util.Util;

import org.jbox2d.common.Vec2;

public class Effect implements Renderable, LayerEntity {
    
    private final float REMAINING_SECONDS_TO_START_FADE = 0.5f;
    
    private AnimationSwitcher animation;
    private float totalTimeElapsed;
    private float alpha;
    public Vec2 position;
    public int color = 0;
    public String type;
    public RectF rect;
    public PhysicsRect pRect;
    public int layerPosition = 0;
    public float xOffset, yOffset;
    public float duration;
    public float animationVelocityMultiplier = 1;
    public boolean repeat = true;
    
    protected Effect() {
    }
    
    public void setDescriptor(EffectDescriptor descriptor) {
        repeat = descriptor.repeat;
        animationVelocityMultiplier = descriptor.animationVelocityMultiplier;
        duration = descriptor.duration;
        xOffset = descriptor.xOffset;
        yOffset = descriptor.yOffset;
        layerPosition = descriptor.layerPosition;
        pRect = descriptor.pRect;
        rect = descriptor.rect;
        color = descriptor.color;
        position = descriptor.position;
        
        animation = AnimationManager.getInstance().getSprite(descriptor.type);
        totalTimeElapsed = 0;
        animation.setRepeatableForAnimations(repeat);
        animation.setAnimationRateMultiplier(animationVelocityMultiplier);
        alpha = 1;
        
        if (duration <= 0) {
            duration = animation.getDuration();
        }
    }

    public void setAnimation(AnimationSwitcher animation) {
        this.animation = animation;
    }
    
    @Override
    public int getLayerPosition() {
        return layerPosition;
    }

    @Override
    public void render(GLCanvas canvas, float timeElapsed) {
        
        if (duration > 0 && totalTimeElapsed < duration ||
                duration <= 0) {
        
            canvas.saveState();
            
            if (duration - totalTimeElapsed <= REMAINING_SECONDS_TO_START_FADE) {
                alpha = ( duration - totalTimeElapsed ) / REMAINING_SECONDS_TO_START_FADE;
            }
            
            if (color != 0) {
                canvas.setColor(Util.adjustColorAlpha(color, alpha));
            } else {
                canvas.setColor(Util.adjustColorAlpha(Color.WHITE, alpha));
            }
            
            if (rect == null) {
                canvas.translatePhysics(position.x + xOffset, position.y + yOffset);
                animation.render(canvas, pRect, timeElapsed);
            } else {
                canvas.translate(position.x + xOffset, position.y + yOffset);
                animation.render(canvas, rect, timeElapsed);
            }
            
            if (color != 0) {
                canvas.resetColor();
            }
            
            canvas.restoreState();
            
            totalTimeElapsed += timeElapsed;
        }
    }

    public boolean isFinished() {
        if (duration > 0 && totalTimeElapsed >= duration) {
            return true;
        }
        return false;
    }
    
}