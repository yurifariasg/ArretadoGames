package com.arretadogames.pilot.entities.effects;

import android.graphics.RectF;

import com.arretadogames.pilot.render.PhysicsRect;

import org.jbox2d.common.Vec2;

public class EffectDescriptor {

    public Vec2 position;
    public int color = 0;
    public String type;
    public RectF rect;
    public PhysicsRect pRect;
    public int layerPosition = 0;
    public float xOffset, yOffset;
    public float duration;
    public float animationVelocityMultiplier = 1;
    public float alpha = 0.3f; /* 0-1 */
    public boolean repeat = true;
    public PostEffectCallback callback;
    public int angle;
    
    @Override
    public EffectDescriptor clone() {
        EffectDescriptor descriptor = new EffectDescriptor();
        descriptor.position = position;
        descriptor.color = color;
        descriptor.type = type;
        descriptor.rect = rect;
        descriptor.pRect = pRect;
        descriptor.layerPosition = layerPosition;
        descriptor.xOffset = xOffset;
        descriptor.yOffset = yOffset;
        descriptor.duration = duration;
        descriptor.animationVelocityMultiplier = animationVelocityMultiplier;
        descriptor.repeat = repeat;
        descriptor.callback = callback;
        descriptor.alpha = alpha;
        descriptor.angle = angle;
        return descriptor;
    }
    
}
