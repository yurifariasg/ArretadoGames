package com.arretadogames.pilot.ui;

import android.graphics.Bitmap;

import com.arretadogames.pilot.render.GameCanvas;

public class ImageButton extends Button {
	
	private Bitmap selectedImage;
	private Bitmap unselectedImage;

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
	public ImageButton(int id, float x, float y, GameButtonListener listener,
			Bitmap selectedImage, Bitmap unselectedImage) {
		super(id, x, y, selectedImage.getWidth(),
			  selectedImage.getHeight(), listener);
		this.selectedImage = selectedImage;
		this.unselectedImage = unselectedImage;
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		if (isSelected && selectedImage != null) {
			canvas.drawBitmap(selectedImage, x, y);
		} else if (unselectedImage != null) {
			canvas.drawBitmap(unselectedImage, x, y);
		}
	}
	
	public void setSelectedImage(Bitmap selectedImage) {
		this.selectedImage = selectedImage;
	}
	
	public void setUnselectedImage(Bitmap unselectedImage) {
		this.unselectedImage = unselectedImage;
	}
	
}