package com.arretadogames.pilot.weathers;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Storm extends Weather{
	
	private Rect weatherSrcRect;
	private RectF weatherDstRect;
	private int originalHeight;
	private int originalWidth;
	private float aspectRatio = GameSettings.TARGET_WIDTH/GameSettings.TARGET_HEIGHT;
	private float offsetX = 0;
	
	private final float rectWidth;
	private static final float FOG_VELOCITY = 25f;
	
	public Storm(){

		this.originalWidth = ImageLoader.checkBitmapSize(R.drawable.w_fog)[0];
		this.originalHeight = ImageLoader.checkBitmapSize(R.drawable.w_fog)[1];
		weatherSrcRect = new Rect(0, 0, (int) (aspectRatio * originalHeight), originalHeight); 
		weatherDstRect = new RectF(0, 0, GameSettings.TARGET_WIDTH, GameSettings.TARGET_HEIGHT);
		this.rectWidth = weatherSrcRect.width();
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {

		setSrcRect(timeElapsed);
		canvas.drawBitmap(R.drawable.w_fog, weatherSrcRect, weatherDstRect);
	}

	@Override
	public void step(float timeElapsed) {
		
	}
		
	private void setSrcRect(float timeElapsed) {
		offsetX += (timeElapsed * FOG_VELOCITY);
		
		if (weatherSrcRect.left > this.originalWidth) {
			offsetX %= originalWidth;
		}
		
		weatherSrcRect.left = (int) offsetX;
		weatherSrcRect.right = (int) (rectWidth + weatherSrcRect.left);
	}

	@Override
	public void drawBackground(GLCanvas gameCanvas) {
		// Draw Sky
		int topSky = Color.rgb(0, 134, 168);
		int bottomSky = Color.rgb(277, 251, 145);

		gameCanvas.drawRect(0, 0, 0, GameSettings.TARGET_HEIGHT,
				GameSettings.TARGET_WIDTH, GameSettings.TARGET_HEIGHT,
				GameSettings.TARGET_WIDTH, 0, topSky, bottomSky, bottomSky,
				topSky);
	}

}
