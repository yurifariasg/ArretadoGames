package com.arretadogames.pilot.render;

import java.util.HashMap;

import android.graphics.RectF;

import com.arretadogames.pilot.render.opengl.GLCanvas;

public class AnimationSwitcher implements Cloneable {
	
    private String name;
	private HashMap<String, Animation> spriteStates;
	private String currentState;
	
	public AnimationSwitcher(String name) {
	    this.name = name;
		spriteStates = new HashMap<String, Animation>();
		currentState = "default";
	}
	
	public String getName() {
        return name;
    }

	public void addState(Animation spriteAnimation) {
		spriteStates.put(spriteAnimation.getName(), spriteAnimation);
	}
	
	public void render(GLCanvas canvas, PhysicsRect phyRect, float timeElapsed) {
        if (!spriteStates.containsKey(currentState)) {
            throw new IllegalArgumentException("Animation with name '" + currentState + "' not found");
        }
	    spriteStates.get(currentState).render(canvas, phyRect, timeElapsed);
	}
	
	public void render(GLCanvas canvas, RectF rect, float timeElapsed) {
        if (!spriteStates.containsKey(currentState)) {
            throw new IllegalArgumentException("Animation with name '" + currentState + "' not found");
        }
	    spriteStates.get(currentState).render(canvas, rect, timeElapsed);
	}
	
	public void setAnimationState(String state) {
		this.currentState = state;
		if (spriteStates.get(currentState) != null) {
			spriteStates.get(currentState).resetIfInfinite();
		}
	}
	
	public void setRepeatableForAnimations(boolean repeat) {
	    for (Animation a : spriteStates.values()) {
	        a.setRepeat(repeat);
	    }
    }
	
	public void setAnimationRateMultiplier(float multiplier) {
	    for (Animation a : spriteStates.values()) {
            a.setAnimationRate(multiplier);
        }
	}
	
	@Override
	public AnimationSwitcher clone() {
	    AnimationSwitcher as = new AnimationSwitcher(name);
	    for (String state : spriteStates.keySet()) {
	        as.addState(spriteStates.get(state).clone());
	    }
	    return as;
	}

    /**
     * Gets the duration of the current animation
     * @return
     */
    public float getDuration() {
        return spriteStates.get(currentState).getTotalDuration();
    }
	
//	@Override
//	protected AnimationSwitcher clone()  {
//	    return null;
//	}

//	public List<LoadableGLObject> getAllFrames() {
//		List<LoadableGLObject> loadableObject = new ArrayList<LoadableGLObject>();
//		for (Animation state : spriteStates.values()) {
//			if (state != null) {
//				int[] frameIds = state.getFrames();
//				if (frameIds != null) {
//					for (int frameId : state.getFrames()) {
//						loadableObject.add(new LoadableGLObject(frameId, LoadableType.TEXTURE));
//					}
//				}
//			}
//		}
//		return loadableObject;
//	}

}
