package com.arretadogames.pilot.render.canvas;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import com.arretadogames.pilot.loop.GameThread;

public class RenderingSurface extends SurfaceView implements
	SurfaceHolder.Callback, OnTouchListener {
	
	private static final boolean SHOW_FPS = false;
	private static final boolean PROFILE_SPEED = false;
	private static final float TARGET_FPS = 100.0f;
	
	private GameThread gameThread;
//	private Game game;
	private Paint fpsPaint;
	
	/**
	* Default SurfaceView Creator
	* 
	* @param context
	*            Context of the SurfaceView
	* @param attrSet
	*            AttributeSet of the SurfaceView
	*/
	public RenderingSurface(Context context) {
	super(context);
	
	SurfaceHolder holder = getHolder();
	holder.addCallback(this);
	setOnTouchListener(this);
	
	// Debug Purposes
	fpsPaint = new Paint();
	fpsPaint.setColor(Color.RED);
	fpsPaint.setAntiAlias(true);
	fpsPaint.setTextSize(15);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
		int height) {
		Log.e("SurfaceChanged", "Surface Changed!");
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		gameThread = new GameThread();
		gameThread.setSurfaceHolder(holder);
		gameThread.setRunning(true);
		gameThread.start();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
//		gameThread.setRunning(false);
//		boolean retry = true;
//		
//		while (retry) {
//			try {
//		
//				gameThread.join();
//				retry = false;
//		
//			} catch (Exception e) {
//				Log.v("Exception Occured", e.getMessage());
//			}
//		}
	}
	
	/**
	* Sets the Game to be used on this surface
	* 
	* @param g
	*            Game to be used
	*/
//	public void setGame(Game g) {
//		this.game = g;
//	}
	
	/**
	* Perform the Drawing on this Surface
	* 
	* @param mcanvas
	*            Canvas to be drawn
	* @param timePassed
	*            Elapsed Time in Seconds
	*/
//	@SuppressLint("NewApi")
//	public void doDraw(GameCanvas mcanvas, float timePassed) {
//		if (game != null) {
//			long before = System.currentTimeMillis();
//			game.step(timePassed);
//			if (PROFILE_SPEED)
//				Log.i("Profiling", "Step Speed: " + (System.currentTimeMillis() - before));
//			long beforeRender = System.currentTimeMillis();
//			game.render(mcanvas, timePassed);
//			if (PROFILE_SPEED)
//				Log.i("Profiling", "Render Speed: " + (System.currentTimeMillis() - beforeRender));
//			
//			long millisToWait = getTargetMilli(before)
//					- System.currentTimeMillis();
//		
//			if (millisToWait > 0) { // Wait
//				try {
//					synchronized (this) {
//						wait(millisToWait);
//					}
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		
//			if (SHOW_FPS) {
//				float fps = (1000.0f / ((System.currentTimeMillis() - before)));
//				mcanvas.drawText("FPS: " + String.format("%.3f", fps), 5, 20,
//						fpsPaint);
//			}
//		}
//	}
	
	private long getTargetMilli(long timeBefore) {
		return (long) (1000.0 / TARGET_FPS) + timeBefore;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
//		if (v == this) {
//			game.input(event);
//		}
//		return true;
		return true;
	}
}