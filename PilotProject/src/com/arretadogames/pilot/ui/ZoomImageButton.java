package com.arretadogames.pilot.ui;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;

import com.arretadogames.pilot.render.opengl.GLCanvas;

public class ZoomImageButton extends ImageButton implements TweenAccessor<ZoomImageButton> {

	private static final float MAX_ZOOM = 1.1f;

	private TweenManager tweenManager;
	private float currentZoom;
	private boolean isFirstSelected;

	public ZoomImageButton(int id, float x, float y, float width, float height,
			GameButtonListener listener, int selectedImageId,
			int unselectedImageId) {
		super(id, x, y, width, height, listener, selectedImageId, unselectedImageId);
		tweenManager = new TweenManager();
		currentZoom = 1f;
		isFirstSelected = false;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		tweenManager.update(timeElapsed);

		if (isSelected && !isFirstSelected) {
			Tween.to(this, 0, 0.2f).target(MAX_ZOOM).start(tweenManager);
			isFirstSelected = true;
		} else if (!isSelected && isFirstSelected) {
			Tween.to(this, 0, 0.2f).target(1f).start(tweenManager);
			isFirstSelected = false;
		}

		canvas.saveState();

		canvas.scale(currentZoom, currentZoom, x + width / 2, y + height / 2);

		super.render(canvas, timeElapsed);

		canvas.restoreState();

	}

	@Override
	public int getValues(ZoomImageButton target, int tweenType, float[] returnValues) {
		returnValues[0] = target.currentZoom;
		return 1;
	}

	@Override
	public void setValues(ZoomImageButton target, int tweenType, float[] newValues) {
		target.currentZoom = newValues[0];
	}

}
