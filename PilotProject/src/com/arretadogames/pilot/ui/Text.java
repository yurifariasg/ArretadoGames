package com.arretadogames.pilot.ui;

import com.arretadogames.pilot.loading.FontSpecification;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Text implements Renderable {

	private String text;
	private float size;
	private float x, y;
	private boolean centered;
	private FontSpecification fontSpecification;
	
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
	public Text(float x, float y, String text, FontSpecification fs, float size, boolean centered) {
		this.text = text;
		this.size = size;
		this.x = x;
		this.y = y;
		this.centered = centered;
		this.fontSpecification = fs;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.drawText(text, x, y, fontSpecification, size, centered);
	}

	public void setText(String text) {
		this.text = text;
	}

}