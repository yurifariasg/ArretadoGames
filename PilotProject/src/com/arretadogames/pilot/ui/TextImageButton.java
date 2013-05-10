package com.arretadogames.pilot.ui;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.Fonts;
import com.arretadogames.pilot.render.GameCanvas;

/**
 *	TextImageButton class implements a ImageButton with a styled text centered on it<br>
 *	This class (for now) has a fixed implementation of the Style for the Text (color, font, etc)
 */
public class TextImageButton extends ImageButton {

	// why do these variables were static?
	private Paint textPaint;
	private Paint strokePaint;
	private String text = "";
	
	private Point textPos = null;

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
	public TextImageButton(int id, float x, float y, GameButtonListener listener,
			Bitmap selectedImage, Bitmap unselectedImage, String text) {
		super(id, x, y, listener, selectedImage, unselectedImage);
		this.text = text;
		createPaints();
	}

	public TextImageButton(int id, float x, float y, GameButtonListener listener,
			Bitmap selectedImage, Bitmap unselectedImage, String text, Paint textPaint, 
						Paint strokePaint) {
		super(id, x, y, listener, selectedImage, unselectedImage);
		this.text = text;
		this.textPaint = textPaint;
		this.strokePaint = strokePaint;
	}
	
	private void createPaints() {
		int textSize = FontLoader.getInstance().getFontSize();

		textPaint = new Paint();
//		textPaint.setARGB(255, 255, 98, 61);
		textPaint.setARGB(255, 255, 255, 255);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);
		textPaint.setTypeface(FontLoader.getInstance().getFont(Fonts.TRANSMETALS));

//		strokePaint = new Paint();
//		strokePaint.setARGB(255, 172, 51, 22);
//		strokePaint.setAntiAlias(true);
//		strokePaint.setStrokeWidth(3);
//		strokePaint.setTextSize(textSize);
//		strokePaint.setStyle(Style.STROKE);
//		strokePaint.setTypeface(FontLoader.getInstance().getFont(Fonts.TRANSMETALS));
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		super.render(canvas, timeElapsed);
		
		textPos = centerTextOnCanvas(textPaint, x, y, width, height, text);
		canvas.drawText(text, textPos.x, textPos.y, textPaint);
	}
	
	public static Point centerTextOnCanvas(Paint paint, float x, float y, float width, float height, String text) {
	    Rect bounds = new Rect();
	    paint.getTextBounds(text, 0, text.length(), bounds);

	    return new Point(
	    		(int) (x + width / 2 - paint.measureText(text) / 2),
	    		(int) (y + height / 2 + (bounds.bottom-bounds.top) / 2));
	}

}