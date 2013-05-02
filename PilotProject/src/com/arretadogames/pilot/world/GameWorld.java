package com.arretadogames.pilot.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.view.MotionEvent;

import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.loading.Loader;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.screens.GameScreen;

/**
 * GameWorld class represents the World in our Game
 */
public class GameWorld extends GameScreen {
	
	private PhysicalWorld pWorld;
	private Collection<Entity> worldEntities;;
	
	public GameWorld() {
		pWorld = PhysicalWorld.getInstance();
		Loader loader = new Loader(Loader.jsonExample);
		this.worldEntities = loader.getEntities();
	}
	
	@Override
	public void step(float timeElapsed) {
		// TODO: Perform a World Step
	}

	@Override
	public void input(MotionEvent event) {
		// TODO Handle Inputs
	}
	
	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		// Render the World
		
		for (Entity entity : worldEntities)
			entity.render(canvas, timeElapsed);
	}

}
