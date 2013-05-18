package com.arretadogames.pilot.render.canvas;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.arretadogames.pilot.GameActivity;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.loop.GameThread;

public class RenderingSurface extends SurfaceView implements SurfaceHolder.Callback {
	
	private GameThread gameThread;
	
	/**
	* Default SurfaceView Creator
	* 
	* @param context
	*            Context of the SurfaceView
	* @param attrSet
	*            AttributeSet of the SurfaceView
	*/
	public RenderingSurface(GameActivity activity) {
		super(activity.getApplicationContext());
		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		setOnTouchListener(activity);
	}
	
//	public void setGameThread(GameThread gameThread) {
//		if (gameThread == null) {
//			this.gameThread = gameThread;
//			if (surfaceIsCreated) {
//				startGameThread();
//			}
//		} else
//			Log.e("RenderingSurface.setGameThread()", "Tried to set GameThread while other is still active");
//	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
		int height) {
		Log.e("SurfaceChanged", "Surface Changed!");
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (gameThread == null) {
			startGameThread();
		}
	}
	
	private void startGameThread() {
		gameThread = new GameThread();
		gameThread.setGame(Game.getInstance());
		gameThread.setSurfaceHolder(getHolder());
		gameThread.setRunning(true);
		gameThread.start();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (gameThread != null) {
			gameThread.setRunning(false);
			gameThread = null;
		}
	}
}