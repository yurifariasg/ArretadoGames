package com.arretadogames.pilot.render;

import org.jbox2d.common.Vec2;

import android.graphics.Rect;
import android.graphics.RectF;

import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class MovingBackground {
	
	private int imageId;
	private float width;
	private float height;
	private float imageMeterRatio = -1;
	private Rect srcRectF;
	private RectF dstRectF;
	
	public MovingBackground(int imageId) {
		this.imageId = imageId;
		int[] imageSize = ImageLoader.checkBitmapSize(imageId);
		this.width = imageSize[0];
		this.height = imageSize[1];
		this.srcRectF = new Rect();
		this.dstRectF = new RectF(0, 0, GameSettings.TARGET_WIDTH, GameSettings.TARGET_HEIGHT);
	}
	
	private void setSrcRect(float centerX, float centerY, float width, float height) {
		srcRectF.left = (int) (centerX - width / 2);
		srcRectF.top = (int) (centerY - height / 2);
		srcRectF.right = (int) (centerX + width / 2);
		srcRectF.bottom = (int) (centerY + height / 2);
	}

	public void render(GLCanvas canvas, float timeElapsed, float zoomRatio, float currentX, float currentY, float initialX, float finalX, Vec2 translator) {
		
		if (imageMeterRatio == -1) {
			initialX -= 30; // GAP
			finalX += 30; // GAP
			this.width *= (finalX - initialX) / 100f; // Adjust this.. will affect speed
			imageMeterRatio = this.width / (finalX - initialX);
		}
		
		dstRectF.bottom = GameSettings.TARGET_HEIGHT + translator.y;
		// ADAPT THIS DEPENDING ON THE IMAGE (just to make sure it is where it is supposed to be)
		// Higher Values means..
		dstRectF.bottom +=  80f // 110
				*  Math.abs(zoomRatio) / 70f; // MaximumZoom = 70
		dstRectF.top = dstRectF.bottom - height * Math.abs(zoomRatio) / 70f; // MaximumZoom = 70
		
		dstRectF.left = 0;
		dstRectF.right = GameSettings.TARGET_WIDTH;
		
		// Source Rect
		setSrcRect((currentX + 30) * imageMeterRatio ,
                height / 2,
                dstRectF.width() * height / dstRectF.height(),
                height);
		
		canvas.drawBitmap(imageId, srcRectF, dstRectF);
	}

}
