package com.arretadogames.pilot.screens;

import java.util.List;

import android.graphics.RectF;
import aurelienribon.tweenengine.TweenAccessor;

import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.levels.LevelManager;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.world.GameWorld;

public class LevelSelectionScreen extends GameScreen {
	
	private List<LevelDescriptor> levels;
	private int currentIndex;
	
	// Rendering attributes
	
	
	public LevelSelectionScreen() {
		levels = LevelManager.getLevels();
		currentIndex = 0;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub
	}

	@Override
	public void input(InputEventHandler event) {
		start();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
	}
	
	private void start() {
		((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).setLevel(levels.get(0));
		// Start Loading ?
		Game.getInstance().goTo(GameState.CHARACTER_SELECTION);
	}
	

	private class LevelSpot implements Renderable, Steppable, TweenAccessor<LevelSpot> {
		
		protected int index;
		protected RectF drawRect;
		
		@Override
		public int getValues(LevelSpot arg0, int arg1, float[] arg2) {
			return 0;
		}

		@Override
		public void setValues(LevelSpot arg0, int arg1, float[] arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void step(float timeElapsed) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void render(GLCanvas canvas, float timeElapsed) {
			// TODO Auto-generated method stub
			
		}
		
		
	}

}

