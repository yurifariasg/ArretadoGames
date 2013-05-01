package com.arretadogames.pilot;

import android.app.Activity;
import android.os.Bundle;

import com.arretadogames.pilot.render.canvas.RenderingSurface;

/**
 * GameActivity represents the MainActivity of our game,
 * this activity connects the game and the GLSurfaceView
 * that it should be draw into
 */
public class GameActivity extends Activity {
	
	private RenderingSurface renderingSurface;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		renderingSurface = new RenderingSurface(this);
		setContentView(renderingSurface);
		
//		GameThread gameThread = new GameThread();
//		gameThread.setSurfaceHolder(renderingSurface.getHolder());
//		gameThread.start();
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
		super.onBackPressed();
	}

}
