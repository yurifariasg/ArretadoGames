package com.arretadogames.pilot.screens;

import java.util.List;

import android.graphics.Paint;
import android.graphics.RectF;
import aurelienribon.tweenengine.TweenAccessor;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.levels.LevelManager;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.world.GameWorld;

public class LevelSelectionScreen extends GameScreen implements GameButtonListener {
	
	private static final int NEXT_BUTTON = 0;
	private static final int PREVIOUS_BUTTON = 1;
	private static final int MAX_SPOTS = 4;
	
	private List<LevelDescriptor> levels;
	private int currentIndex; // Current Index will always point to the Level at the Center
	
	// Rendering attributes
	private LevelSpot[] spots = new LevelSpot[MAX_SPOTS];
	
	private ImageButton bt_next;
	private ImageButton bt_prev;
	
	private void setSpotLocation(int index) {
		if (spots[index] == null)
			return ;
		
		final float Y_ALIGNMENT = 240f;
		final float X_ALINGMENT_LEFT_SPOT = 100f;
		final float X_ALINGMENT_MIDDLE_SPOT = 400f;
		final float X_ALINGMENT_RIGHT_SPOT = 700f;
		
		if (index == 0) {
			// Left
			spots[index].setCenter(X_ALINGMENT_LEFT_SPOT, Y_ALIGNMENT);
		} else if (index == 1) {
			// Middle
			spots[index].setCenter(X_ALINGMENT_MIDDLE_SPOT, Y_ALIGNMENT);
		} else if (index == 2) {
			// Right
			spots[index].setCenter(X_ALINGMENT_RIGHT_SPOT, Y_ALIGNMENT);
		} else if (index == 3) {
			// Outside Right
			spots[index].setCenter(X_ALINGMENT_RIGHT_SPOT + 200, Y_ALIGNMENT); // Experimental			
		}
	}
	
	
	public LevelSelectionScreen() {
		levels = LevelManager.getLevels();
		currentIndex = 0;
		
		int maxSpots = levels.size() < MAX_SPOTS ? levels.size() : MAX_SPOTS;
		for (int i = 1 ; i < maxSpots ; i++) {
			LevelSpot spot = new LevelSpot();
			spot.index = i - 1;
			spots[i] = spot;
			setSpotLocation(i);
		}
		
		initializeButtons();
	}
	
	private void initializeButtons() {
		bt_prev = new ImageButton(PREVIOUS_BUTTON,
				110f, 380f,
				this,
				R.drawable.bt_settings_selected,
				R.drawable.bt_settings_unselected);
		
		bt_next = new ImageButton(NEXT_BUTTON,
				610, 380f,
				this,
				R.drawable.bt_settings_selected,
				R.drawable.bt_settings_unselected);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		for (int i = 0 ; i < spots.length ; i++)
			if (spots[i] != null)
				spots[i].render(canvas, timeElapsed);
		
		bt_next.render(canvas, timeElapsed);
		bt_prev.render(canvas, timeElapsed);
	}

	@Override
	public void step(float timeElapsed) {
	}

	@Override
	public void input(InputEventHandler event) {
		bt_next.input(event);
		bt_prev.input(event);
		
		if (spots[1] != null && spots[1].contains(event.getX(), event.getY()))
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
		((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).setLevel(levels.get(currentIndex));
		// Start Loading ?
		Game.getInstance().goTo(GameState.CHARACTER_SELECTION);
	}
	

	private class LevelSpot implements Renderable, Steppable, TweenAccessor<LevelSpot> {
		
		protected int index;
		protected RectF drawRect;
		
		public LevelSpot() {
			drawRect = new RectF(0, 0, 100, 100);
			textPaint = new Paint();
			textPaint.setTextSize(1f);
		}
		
		public boolean contains(float x, float y) {
			return drawRect.contains(x, y);
		}
		
		@Override
		public int getValues(LevelSpot arg0, int arg1, float[] arg2) {
			return 0;
		}

		public void setCenter(float x, float y) {
			drawRect.set(
					x - drawRect.width() / 2,
					y - drawRect.height() / 2,
					x + drawRect.width() / 2,
					y + drawRect.height() / 2);
		}

		@Override
		public void setValues(LevelSpot arg0, int arg1, float[] arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void step(float timeElapsed) {
			// TODO Auto-generated method stub
			
		}

		Paint textPaint;

		@Override
		public void render(GLCanvas canvas, float timeElapsed) {
			canvas.drawBitmap(R.drawable.no_character, drawRect, false);
			canvas.drawText(String.valueOf(index), drawRect.centerX(),
					drawRect.centerY(), textPaint, true);
		}
	}
	
	private void next() {
		if (currentIndex + 1 < levels.size()) {
			currentIndex++;
			
			spots[0] = spots[1];
			setSpotLocation(0);
			spots[1] = spots[2];
			setSpotLocation(1);
			spots[2] = spots[3];
			setSpotLocation(2);
			
			if (currentIndex + 2 < levels.size()) {
				spots[3] = new LevelSpot();
				spots[3].index = currentIndex + 2;
				setSpotLocation(3);
			} else
				spots[3] = null;
		}
	}
	
	private void previous() {
		if (currentIndex - 1 >= 0) {
			currentIndex--;
			
			spots[3] = spots[2];
			setSpotLocation(3);
			spots[2] = spots[1];
			setSpotLocation(2);
			spots[1] = spots[0];
			setSpotLocation(1);
			
			if (currentIndex - 1 >= 0) {
				spots[0] = new LevelSpot();
				spots[0].index = currentIndex - 1;
				setSpotLocation(0);
			} else
				spots[0] = null;
		}
	}


	@Override
	public void onClick(int buttonId) {
		switch (buttonId) {
		case NEXT_BUTTON:
			next();
			break;
		case PREVIOUS_BUTTON:
			previous();
			break;
		default:
			break;
		}
	}
}

