package com.arretadogames.pilot.entities.effects;

import android.graphics.Color;

import com.arretadogames.pilot.entities.LayerEntity;
import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.util.Util;

public class Effect implements Renderable, LayerEntity {
    
    private final float REMAINING_SECONDS_TO_START_FADE = 0.5f;
    
    private AnimationSwitcher animation;
    private EffectDescriptor descriptor;
    private float totalTimeElapsed;
    private float alpha;
    
    protected Effect() {
    }
    
    public void setDescriptor(EffectDescriptor descriptor) {
        this.descriptor = descriptor;
        animation = AnimationManager.getInstance().getSprite(descriptor.type);
        totalTimeElapsed = 0;
        animation.setRepeatableForAnimations(descriptor.repeat);
        animation.setAnimationRateMultiplier(descriptor.animationVelocityMultiplier);
        alpha = 1;
    }

    public void setAnimation(AnimationSwitcher animation) {
        this.animation = animation;
    }
    
    public EffectDescriptor getDescriptor() {
        return descriptor;
    }
    
    @Override
    public int getLayerPosition() {
        return descriptor.layerPosition;
    }

    @Override
    public void render(GLCanvas canvas, float timeElapsed) {
        
        if (descriptor.duration > 0 && totalTimeElapsed < descriptor.duration ||
                descriptor.duration <= 0) {
        
            canvas.saveState();
            
            if (descriptor.duration - totalTimeElapsed <= REMAINING_SECONDS_TO_START_FADE) {
                alpha = ( descriptor.duration - totalTimeElapsed ) / REMAINING_SECONDS_TO_START_FADE;
            }
            
            if (descriptor.color != 0) {
                canvas.setColor(Util.adjustColorAlpha(descriptor.color, alpha));
            } else {
                canvas.setColor(Util.adjustColorAlpha(Color.WHITE, alpha));
            }
            
            if (descriptor.rect == null) {
                canvas.translatePhysics(descriptor.position.x + descriptor.xOffset, descriptor.position.y + descriptor.yOffset);
                animation.render(canvas, descriptor.pRect, timeElapsed);
            } else {
                canvas.translate(descriptor.position.x + descriptor.xOffset, descriptor.position.y + descriptor.yOffset);
                animation.render(canvas, descriptor.rect, timeElapsed);
            }
            
            if (descriptor.color != 0) {
                canvas.resetColor();
            }
            
            canvas.restoreState();
            
            totalTimeElapsed += timeElapsed;
        }
    }

    public boolean isFinished() {
        if (descriptor.duration > 0 && totalTimeElapsed >= descriptor.duration) {
            return true;
        }
        return false;
    }
    
}