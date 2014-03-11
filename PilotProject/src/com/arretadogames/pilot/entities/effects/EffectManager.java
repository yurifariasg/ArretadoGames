package com.arretadogames.pilot.entities.effects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
	
	public void addEffect(final EffectDescriptor effectDescriptor) {
		
		Effect effect;
		if (!inactiveEffects.isEmpty()) {
			// Recycle
			effect = inactiveEffects.poll();
		} else {
			// Adds a new one
			effect = new Effect();
		}
		
		effect.setDescriptor(effectDescriptor);
		activeEffects.add(effect);
	}
}
