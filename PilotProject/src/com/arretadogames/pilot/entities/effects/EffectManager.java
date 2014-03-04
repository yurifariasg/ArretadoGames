package com.arretadogames.pilot.entities.effects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jbox2d.common.Vec2;

import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;

/**
 * EffectManager class is a manager to Effects inside the GameWorld
 */
public class EffectManager {
	
	private static EffectManager instance;
	
	private List<Effect> activeEffects;
	private Queue<Effect> inactiveEffects;
	
	private EffectManager() {
		activeEffects = new ArrayList<Effect>();
		inactiveEffects = new LinkedList<Effect>();
	}
	
	public void removeInactiveEffects() {
		Iterator<Effect> it = activeEffects.iterator();
		while (it.hasNext()) {
			Effect e = it.next();
			if (e.isFinished()) {
				it.remove();
				inactiveEffects.add(e);
			}
		}
	}
	
	public List<Effect> getEffects() {
		return this.activeEffects;
	}
	
	public static EffectManager getInstance() {
		if (instance == null)
			instance = new EffectManager();
		return instance;
	}
	
	public void addEffect(String type, Vec2 position, PhysicsRect phRect, float xOffset, float yOffset) {
		
		Effect effect;
		if (!inactiveEffects.isEmpty()) {
			// Recycle
			effect = inactiveEffects.poll();
		} else {
			// Adds a new one
			effect = new Effect();
		}

		effect.setPhysicsRect(phRect);
		effect.setPosition(position);
		effect.setOffsets(xOffset, yOffset);
		effect.setAnimation(getAnimationForType(type));
		
		activeEffects.add(effect);
	}
	
	private AnimationSwitcher getAnimationForType(String type) {
		return AnimationManager.getInstance().getSprite(type);
	}

}
