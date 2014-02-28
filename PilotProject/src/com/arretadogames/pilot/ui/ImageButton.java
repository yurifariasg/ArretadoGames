package com.arretadogames.pilot.ui;

import com.arretadogames.pilot.render.opengl.GLCanvas;

public class ImageButton extends Button {

	protected int selectedImageId;
	protected int unselectedImageId;

	/**
	 * Creates a ImageButton based on the given position and Images<br>
	 * This constructor automatically converts to screen resolution based on DisplayConverter
	 *
	 * @param x
	 *            X - Position
	 * @param y
	 *            Y - Position
	 * @param listener
	 *            ButtonListener to be called when the button is clicked
	 * @param selectedImage
	 *            Selected Image for the Button
	 * @param unselectedImage
	 *            Unselected Image for the Button
	 */
	public ImageButton(int id, float x, float y, float width, float height,
	        GameButtonListener listener,
			int selectedImageId, int unselectedImageId) {
		super(id, x, y, width, height, listener);

		this.selectedImageId = selectedImageId;
		this.unselectedImageId = unselectedImageId;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		if (isSelected && selectedImageId != 0) {
			canvas.drawBitmap(selectedImageId, x, y, width, height);
		} else if (unselectedImageId != 0) {
			canvas.drawBitmap(unselectedImageId, x, y, width, height);
		}
	}

	public void setSelectedImage(int selectedImageId) {
		this.selectedImageId = selectedImageId;
	}

	public void setUnselectedImage(int unselectedImageId) {
		this.unselectedImageId = unselectedImageId;
	}

}