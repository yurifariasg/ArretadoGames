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
	
	public void drawRect(int x, int y, int x2, int y2) {
		Paint p = new Paint();
		p.setColor(Color.RED);
		canvas.drawRect(new Rect(x, y, x2, y2), p);
	}
	
}
