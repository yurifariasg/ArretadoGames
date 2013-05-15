package com.arretadogames.pilot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.render.canvas.RenderingSurface;
import com.arretadogames.pilot.render.opengl.GameGLSurfaceView;
import com.arretadogames.pilot.screens.InputEventHandler;

/**
 * GameActivity represents the MainActivity of our game,
 * this activity connects the game and the GLSurfaceView
 * that it should be draw into
 */
public class GameActivity extends Activity implements OnTouchListener {
	
	private static Context context;
	
	private SurfaceView renderingSurface;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext(); // Sets the Context for external use
		FontLoader.create(context); // Create the FontLoader
		Game.getInstance(); // Create Game
		
		if (DisplaySettings.USE_OPENGL) {
			renderingSurface = new GameGLSurfaceView(this);
		} else {
			renderingSurface = new RenderingSurface(this);
		}
		setContentView(renderingSurface);
	}
	
	@Override
	protected void onPause() {
		// TODO Handles the Paused Operation into Activity
		// http://developer.android.com/reference/android/app/Activity.html
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Handles the Resume Operation into Activity
		// http://developer.android.com/reference/android/app/Activity.html
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Handles the Destroyed Operation into Activity
		// http://developer.android.com/reference/android/app/Activity.html
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Handles the Back Button input from a Physical Button
		Game.getInstance().onBackPressed();
	}
	
	public static Context getContext() {
		return context;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		Game.getInstance().input(new InputEventHandler(event));
		return true;
	}

}
