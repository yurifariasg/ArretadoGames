package com.arretadogames.pilot.screens;

import java.util.List;

import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.levels.LevelManager;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.loading.FontSpecification;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.AnimationManager;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.world.GameWorld;

public class LevelSelectionScreen extends GameScreen implements GameButtonListener, OnGestureListener {
	
	private static final int NEXT_BUTTON = 0;
	private static final int PREVIOUS_BUTTON = 1;
	private static final int MAX_SPOTS = 5;
	
	private List<LevelDescriptor> levels;
	private int currentIndex; // Current Index will always point to the Level at the Center
	
	// Rendering attributes
	private LevelSpot[] spots = new LevelSpot[MAX_SPOTS];
	
	private GestureDetectorCompat mDetector; 	
	
	private void setSpotLocation(int index, boolean animate) {
		if (spots[index] == null)
			return ;
		
		final float Y_ALIGNMENT = 240f;
		final float X_ALINGMENT_LEFT_SPOT = 100f;
		final float X_ALINGMENT_MIDDLE_SPOT = 400f;
		final float X_ALINGMENT_RIGHT_SPOT = 700f;
		
		float x = 0, y = Y_ALIGNMENT;
		boolean doZooming = false;
		
		if (index == 0) {
			// Outside Left
			x = X_ALINGMENT_LEFT_SPOT - 200;
		} else if (index == 1) {
			// Left
			x = X_ALINGMENT_LEFT_SPOT;
		} else if (index == 2) {
			// Middle
			x = X_ALINGMENT_MIDDLE_SPOT;
			doZooming = true;
		} else if (index == 3) {
			// Right
			x = X_ALINGMENT_RIGHT_SPOT;
		} else if (index == 4) {
			// Outside Right
			x = X_ALINGMENT_RIGHT_SPOT + 200;	
		}
		
		if (animate) {
			spots[index].startAnimationTo(x, y, doZooming);
		} else {
			spots[index].setCenter(x, y);
		}
		
	}
	
	
	public LevelSelectionScreen() {
		levels = LevelManager.getLevels();
		currentIndex = 0;

		final int INITIAL = 2;
		int maxSpots = levels.size() + INITIAL < MAX_SPOTS ? levels.size() + INITIAL : MAX_SPOTS;
		for (int i = INITIAL ; i < maxSpots ; i++) {
			LevelSpot spot = new LevelSpot();
			spot.index = i - INITIAL;
			spots[i] = spot;
			setSpotLocation(i, false);
		}
		setSpotLocation(2, true);
		
		mDetector = new GestureDetectorCompat(MainActivity.getContext(), this);
	}
	

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.drawBitmap(R.drawable.menu_background, 0, 0);
		
		for (int i = 0 ; i < spots.length ; i++)
			if (spots[i] != null)
				spots[i].render(canvas, timeElapsed);
	}

	@Override
	public void step(float timeElapsed) {
	}

	@Override
	public void input(InputEventHandler event) {
		mDetector.onTouchEvent(event.getEvent());
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
		private float zoom;
		private FontSpecification textSpecification;
		
		public LevelSpot() {
			drawRect = new RectF(0, 0, 100, 100);
			textSpecification = FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS);
			zoom = 1;
		}
		
		public boolean contains(float x, float y) {
			return drawRect.contains(x, y);
		}

		public void startAnimationTo(float x, float y, boolean zoomed) {
			Tween.to(this, 0, 0.5f).target(x, y, zoomed ? 2 : 1f).start(AnimationManager.getInstance());
		}
		
		private void setCenter(float x, float y) {
			drawRect.set(
					x - drawRect.width() / 2,
					y - drawRect.height() / 2,
					x + drawRect.width() / 2,
					y + drawRect.height() / 2);
		}
		
		@Override
		public int getValues(LevelSpot spot, int type, float[] returnValues) {
			
			returnValues[0] = spot.drawRect.centerX();
			returnValues[1] = spot.drawRect.centerY();
			returnValues[2] = spot.zoom;
			
			return 3;
		}

		@Override
		public void setValues(LevelSpot spot, int type, float[] newValues) {
			spot.setCenter(newValues[0], newValues[1]);
			spot.zoom = newValues[2];
		}

		@Override
		public void step(float timeElapsed) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void render(GLCanvas canvas, float timeElapsed) {
			canvas.saveState();
			canvas.scale(zoom, zoom, drawRect.centerX(), drawRect.centerY());
			canvas.drawBitmap(R.drawable.bt_level_selector, drawRect);
			canvas.drawText(String.valueOf(index+1), drawRect.centerX(),
					drawRect.centerY(), textSpecification, 1, true);
			canvas.restoreState();
			
			if (zoom == 2) {
				int stRecord = levels.get(currentIndex).getRecords()[0];
				canvas.drawText(
						"1st Seeds: " + (stRecord == -1 ? "?" : String.valueOf(stRecord)),
						drawRect.centerX(),	drawRect.centerY() + 130, textSpecification, 1, true);
				
				int ndRecord = levels.get(currentIndex).getRecords()[1];
				canvas.drawText(
						"2nd Seeds: " + (ndRecord == -1 ? "?" : String.valueOf(ndRecord)),
						drawRect.centerX(),	drawRect.centerY() + 160, textSpecification, 1, true);
				
				int rdRecord = levels.get(currentIndex).getRecords()[2];
				canvas.drawText(
						"3rd Seeds: " + (rdRecord == -1 ? "?" : String.valueOf(rdRecord)),
						drawRect.centerX(),	drawRect.centerY() + 190, textSpecification, 1, true);

				
			}
		}
	}
	
	private void next() {
		if (currentIndex + 1 < levels.size()) {
			currentIndex++;
			
			// Move Spots
			spots[0] = spots[1];
			setSpotLocation(0, true);
			spots[1] = spots[2];
			setSpotLocation(1, true);
			spots[2] = spots[3];
			setSpotLocation(2, true);
			spots[3] = spots[4];
			setSpotLocation(3, true);
			
			// Create new one, if needed
			if (currentIndex + 2 < levels.size()) {
				spots[4] = new LevelSpot();
				spots[4].index = currentIndex + 2;
				setSpotLocation(4, false);
			} else
				spots[4] = null;
		}
	}
	
	private void previous() {
		if (currentIndex - 1 >= 0) {
			currentIndex--;
			
			// Move Spots
			spots[4] = spots[3];
			setSpotLocation(4, true);
			spots[3] = spots[2];
			setSpotLocation(3, true);
			spots[2] = spots[1];
			setSpotLocation(2, true);
			spots[1] = spots[0];
			setSpotLocation(1, true);
			
			// Create new one if needed
			if (currentIndex - 2 >= 0) {
				spots[0] = new LevelSpot();
				spots[0].index = currentIndex - 2;
				setSpotLocation(0, false);
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


	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if( velocityX < -1) {
			onClick(NEXT_BUTTON);
			return true;
		}
		if( velocityX > 1) {
			onClick(PREVIOUS_BUTTON);
			return true;
			
		}
		return false;
	}


	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		
		return false;
	}


	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	int cont = 0;
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		InputEventHandler event = new InputEventHandler(e);
		if (spots[2] != null && spots[2].contains(event.getX(), event.getY()))
			start();
		
		// Click on right spot
		if (spots[3] != null && spots[3].contains(event.getX(), event.getY()))
			next();
		
		// Click on left spot
		if (spots[1] != null && spots[1].contains(event.getX(), event.getY()))
			previous();
		return false;
	}
	
	@Override
	public void onBackPressed() {
		System.out.println("Hello World");
		Game.getInstance().goTo(GameState.MAIN_MENU);
	}
}

