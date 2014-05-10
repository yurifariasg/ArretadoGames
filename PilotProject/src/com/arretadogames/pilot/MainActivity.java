package com.arretadogames.pilot;

//import com.crashlytics.android.Crashlytics;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.arretadogames.pilot.android.KeyboardManager;
import com.arretadogames.pilot.audio.AndroidAudio;
import com.arretadogames.pilot.audio.AudioI;
import com.arretadogames.pilot.database.GameDatabase;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.render.opengl.GameGLSurfaceView;
import com.arretadogames.pilot.screens.InputEventHandler;
import com.arretadogames.pilot.util.Assets;

/**
 * GameActivity represents the MainActivity of our game,
 * this activity connects the game and the GLSurfaceView
 * that it should be draw into
 */
public class MainActivity extends BaseGameActivity implements OnTouchListener {

	private static Context context;
	private static MainActivity mainActivity;
	private GameGLSurfaceView renderingSurface;
	AudioI audio;
	
	public MainActivity() {
		super();
		MainActivity.mainActivity = this;
	}

	public static MainActivity getActivity() {
		return mainActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (GameSettings.USE_CRASHLYTICS)
//			Crashlytics.start(this);
		audio = new AndroidAudio(this);
		context = getApplicationContext(); // Sets the Context for external use
		FontLoader.create(context); // Create the FontLoader
		GameDatabase.createDatabase(getApplicationContext());
		Game.getInstance(); // Create Game
		KeyboardManager.setup(this);
		
	}

//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		return KeyboardManager.dispatchKeyEvent(event);
//	}

	@Override
	protected void onPause() {
		// TODO Handles the Paused Operation into Activity
		// http://developer.android.com/reference/android/app/Activity.html
		super.onPause();
		((GLSurfaceView) renderingSurface).onPause();
		Game.getInstance().onPause();
	}

	@Override
	protected void onResume() {
		// TODO Handles the Resume Operation into Activity
		// http://developer.android.com/reference/android/app/Activity.html
		super.onResume();

		// Creates a new
		renderingSurface = new GameGLSurfaceView(this);
		renderingSurface.init();
		Game.getInstance().onResume();
		setContentView(renderingSurface);
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

	/**
	 * Gets the context associated with this activity
	 * @return Context
	 */
	public static Context getContext() {
		return context;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		Game.getInstance().input(new InputEventHandler(event));
		return true;
	}

	public void showExitDialog() {
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Quit")
        .setMessage("Are you really leaving?")
        .setPositiveButton("Yea..", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Stop the activity
            	System.exit(0);
            }

        })
        .setNegativeButton("Play More!", null)
        .show();
	}
	
    public AudioI getAudio() {
        return audio;
    }

}
