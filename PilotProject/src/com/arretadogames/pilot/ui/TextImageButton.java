package com.arretadogames.pilot.ui;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.arretadogames.pilot.loading.FontSpecification;
import com.arretadogames.pilot.render.opengl.GLCanvas;

/**
 *	TextImageButton class implements a ImageButton with a styled text centered on it<br>
 *	This class (for now) has a fixed implementation of the Style for the Text (color, font, etc)
 */
public class TextImageButton extends ImageButton {

	private String text = "";
	private FontSpecification fontSpecification;
	private float textSize;

	/**
	 * Creates a TextImageButton based on the given position and Images
	 *
	 * @param id
	 * Button Id
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
	 * @param text
	 *            Text to be rendered
	 */
	public TextImageButton(int id, float x, float y, float width, float height, GameButtonListener listener,
			int selectedImageId, int unselectedImageId, String text, FontSpecification fs, float textSize) {
		super(id, x, y, width, height, listener, selectedImageId, unselectedImageId);
		this.text = text;
		this.textSize = textSize;
		this.fontSpecification = fs;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		super.render(canvas, timeElapsed);
		canvas.drawText(text, x + width / 2, y + height / 2f, fontSpecification, textSize, true);
	}

	public static Point centerTextOnCanvas(Paint paint, float x, float y, float width, float height, String text) {
	    Rect bounds = new Rect();
	    paint.getTextBounds(text, 0, text.length(), bounds);

	    return new Point(
	    		(int) (x + width / 2 - paint.measureText(text) / 2),
	    		(int) (y + height / 2 + (bounds.bottom-bounds.top) / 2));
	}

}