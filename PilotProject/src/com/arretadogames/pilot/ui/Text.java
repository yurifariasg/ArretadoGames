package com.arretadogames.pilot.ui;

import android.graphics.Paint;

import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.Fonts;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Text implements Renderable {

	private Paint textPaint;
	private String text;
	private float size;
	private float x, y;
	
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
	public Text(float x, float y, String text, float size) {
		this.text = text;
		this.size = size;
		this.x = x;
		this.y = y;
		createPaints();
	}

	private void createPaints() {
		float textSize = FontLoader.getInstance().getFontSize();

		textPaint = new Paint();
		textPaint.setARGB(255, 255, 255, 255);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize * size);
		textPaint.setTypeface(FontLoader.getInstance().getFont(Fonts.TRANSMETALS));
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.drawText(text, x, y, textPaint, false);
	}
	/*
	public static Point centerTextOnCanvas(Paint paint, float x, float y, float width, float height, String text) {
	    Rect bounds = new Rect();
	    paint.getTextBounds(text, 0, text.length(), bounds);

	    return new Point(
	    		(int) (x + width / 2 - paint.measureText(text) / 2),
	    		(int) (y + height / 2 + (bounds.bottom-bounds.top) / 2));
	}*/

}