package com.arretadogames.pilot.render;

import android.graphics.Rect;
import android.graphics.RectF;

import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.entities.FinalFlag;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class MovingBackground {
	
	private static final float IMAGE_TOP = -40; // Baseline
	
	private int imageId;
	private float width;
	private float height;
	private float ratio;
	private float imageMeterRatio = -1;
	private Rect srcRectF;
	private RectF dstRectF;
	
	public MovingBackground(int imageId) {
		this.imageId = imageId;
		int[] imageSize = ImageLoader.checkBitmapSize(imageId);
		this.width = imageSize[0];// * imageWidthMultiplier;
		this.height = imageSize[1];
		this.ratio = imageSize[0] / imageSize[1];
		this.srcRectF = new Rect();
		this.dstRectF = new RectF(0, 0, GameSettings.TARGET_WIDTH, GameSettings.TARGET_HEIGHT);
	}
	
	private void setSrcRect(float centerX, float centerY, float width, float height) {
		srcRectF.left = (int) (centerX - width / 2);
		srcRectF.top = (int) (centerY - height / 2);
		srcRectF.right = (int) (centerX + width / 2);
		srcRectF.bottom = (int) (centerY + height / 2);
	}
	
	private void setDstRect(float centerX, float centerY, float width, float height) {
		dstRectF.left = (centerX - width / 2);
		dstRectF.top = (centerY - height / 2);
		dstRectF.right = (centerX + width / 2);
		dstRectF.bottom = (centerY + height / 2);
	}

	public void render(GLCanvas canvas, float timeElapsed, float zoomRatio, float currentX, float currentY, float initialX, float finalX) {
		
		if (imageMeterRatio == -1) {
			initialX -= 30;
			finalX += 30;
			this.width *= (finalX - initialX) / 100f;
			imageMeterRatio = this.width / (finalX - initialX);
		}
		
		setSrcRect((currentX + 30) * imageMeterRatio ,
                GameSettings.TARGET_HEIGHT / 2,
                this.width,
                GameSettings.TARGET_HEIGHT);

		dstRectF.top = ( -(currentY / 50f)) * imageMeterRatio;
		dstRectF.bottom = height;
		
		float bgMultiplier = 1 + (zoomRatio - 40) / 50f;
		currentY -= (zoomRatio / 8); // 8
		
		setDstRect(GameSettings.TARGET_WIDTH / 2,
		                GameSettings.TARGET_HEIGHT / 2 - IMAGE_TOP + currentY * 20,
		                this.width * bgMultiplier,
		                dstRectF.height() * bgMultiplier);
		
		canvas.drawBitmap(imageId, srcRectF, dstRectF);
	}

}
