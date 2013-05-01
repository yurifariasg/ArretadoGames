package com.arretadogames.pilot.world;

import android.view.MotionEvent;

import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.screens.GameScreen;

/**
 * GameWorld class represents the World in our Game
 */
public class GameWorld extends GameScreen {
	
	private PhysicalWorld pWorld;
	
	public GameWorld() {
		pWorld = PhysicalWorld.getInstance();
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
		// TODO: Danilo IMPLEMENTA SA POHA
		canvas.drawRect(100, 10, 160, 60);
	}

}
