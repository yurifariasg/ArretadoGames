package com.arretadogames.pilot.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class GameCanvas {
	
	private SurfaceHolder surfaceHolder;
	
	private Canvas canvas;
	
	public GameCanvas(SurfaceHolder surfaceHolder) {
		this.surfaceHolder = surfaceHolder;
	}
	
	public boolean initiate() {
		canvas = surfaceHolder.lockCanvas();
		return canvas == null;
	}
	
	public void flush() {
		surfaceHolder.unlockCanvasAndPost(canvas);
		canvas = null;
	}
	
	public void drawRect(int x, int y, int width, int height) {
		Paint p = new Paint();
		p.setColor(Color.RED);
		canvas.drawRect(new Rect(500, 10, 600, 110), p);
	}
	
}
