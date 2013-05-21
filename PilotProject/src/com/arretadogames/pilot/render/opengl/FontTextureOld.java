package com.arretadogames.pilot.render.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.util.SparseIntArray;

public class FontTextureOld extends Texture {
	// Font texture for drawing text

	// General
	private Typeface tf;

	// Render parameters
	private int fontHeight;
	// TODO: support multi-line: add max chars in row and wrap
	private int fontAscent;
	private int fontDescent;
	private SparseIntArray charWidths;
	private SparseArray<Rect> characterRects;
	private int cellWidth;
	private int cellHeight;

	// Font settings (defaults)
	// TODO: Add way to change these:
	private int size = 24;
	private int colour = 0xffffffff;
	private int charStart = 32;
	private int charEnd = 126;
	private int charUnknown = 32; // Must be between start and end
	private int padX = 2;
	private int padY = 2;

	public FontTextureOld(Typeface tf) {
		this.tf = tf;
	}

	protected void setParams(FontParams params) {
		this.size = params.getSize();
		this.colour = params.getArgb();
		this.charStart = params.getcharStart();
		this.charEnd = params.getCharEnd();
		this.charUnknown = params.getCharUnknown();
		this.padX = params.getPadX();
		this.padY = params.getPadY();
	}

	public void drawText(GL10 gl, String text, int x, int y, float scale,
			int argb) {
		// Draw text centred at x,y
		// Get width of text
		int textWidth = 0;
		int charWidth;
		for (int i = 0; i < text.length(); i++) {
			charWidth = charWidths.get((int) text.charAt(i));
			textWidth += charWidth;
		}
		// Adjust to centre text about x,y
		x -= textWidth / 2;
		y -= fontHeight;

		// No cycle through and draw text
		int scaledCellWidth = (int) (scale * cellWidth);
		int scaledCellHeight = (int) (scale * cellHeight);
		Rect src;
		Rect dst = new Rect();
		int charNumber;
		for (int i = 0; i < text.length(); i++) {
			// Source rect
			charNumber = (int) text.charAt(i);
			src = characterRects.get(charNumber);
			charWidth = charWidths.get(charNumber);
			if (src == null) {
				// Defaults to unknown char
				src = characterRects.get(text.charAt(charUnknown));
				charWidth = charWidths.get(text.charAt(charUnknown));
			}

			// Destination rect
			dst.set(x, y, x + scaledCellWidth, y + scaledCellHeight);

			// Add sprite
			getARGBSpriteData(argb).addSprite(src, dst);

			// Move forward CHAR WIDTH (not cell width)
			x += charWidth;
		}
	}

	@Override
	public Bitmap getBitmap(Resources resources) {
		// We use the font to create a sprite atlas containing every letter,
		// then we return the sprite atlas bitmap to be used as the texture.

		// Set-up Paint object for drawing letters to bitmap
		Paint paint = new Paint(); // Create Android Paint Instance
		paint.setAntiAlias(true); // Enable Anti Alias
		paint.setTypeface(tf); // Set Typeface
		paint.setTextSize(size); // Set Text Size
		paint.setColor(colour); // Set ARGB (White, Opaque)
		paint.setTextAlign(Paint.Align.LEFT);

		// get font metrics
		Paint.FontMetrics fm = paint.getFontMetrics();
		fontHeight = (int) Math.ceil(Math.abs(fm.bottom) + Math.abs(fm.top));
		fontAscent = (int) Math.ceil(Math.abs(fm.ascent));
		fontDescent = (int) Math.ceil(Math.abs(fm.descent));

		// Store for char widths
		charWidths = new SparseIntArray();

		// Cycle through chars and store width of each character
		char[] s = new char[2];
		float[] w = new float[2];
		int charWidthMax = 0;
		int charWidth;
		for (char c = (char) charStart; c <= (char) charEnd; c++) {
			s[0] = c;
			paint.getTextWidths(s, 0, 1, w);
			charWidth = (int) Math.ceil(w[0]);
			// Store it
			charWidths.put(c, charWidth);
			if (charWidth > charWidthMax) {
				// Store max width
				charWidthMax = charWidth;
			}
		}

		// set character height to font height
		int charHeight = fontHeight;

		// Find the maximum size, validate, and setup cell sizes
		cellWidth = (int) charWidthMax + (2 * padX);
		cellHeight = (int) charHeight + (2 * padY);
		// Save whichever is bigger
		int maxSize = cellWidth > cellHeight ? cellWidth : cellHeight;

		// Ensure power-of-2 texture sizes. Base it on maxSize
		// TODO: Make automatic? Are we OK beyond 1024?
		// Here is how calculation goes:
		// Number of chars = 95
		// Square root (round up) = 10 x 10 grid
		// Max size = 256 / 10 = 25.6 (24?)
		int textureSize = 0;
		if (maxSize <= 24) // IF Max Size is 24 or Less
			textureSize = 256;
		else if (maxSize <= 40) // ELSE IF Max Size is 40 or Less
			textureSize = 512;
		else if (maxSize <= 80) // ELSE IF Max Size is 80 or Less
			textureSize = 1024;
		else if (maxSize <= 160) // ELSE IF Max Size is 80 or Less
			textureSize = 2048;
		else
			// ELSE IF Max Size is Larger Than 80 (and Less than FONT_SIZE_MAX)
			textureSize = 4096;

		// Create an empty bitmap (alpha only)
		Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize,
				Bitmap.Config.ALPHA_8);
		// Create Canvas for rendering to Bitmap
		Canvas canvas = new Canvas(bitmap);
		bitmap.eraseColor(0x00000000); // Set Transparent Background (ARGB)

		// Calculate number of columns
		// int colCnt = textureSize / cellWidth;

		// Render each of the characters to the canvas (i.e. build the font map)
		// Also store a source rectangle for each char
		characterRects = new SparseArray<Rect>();
		int x = 0;
		int y = cellHeight;// (cellHeight - 1) - fontDescent - padY;
		for (char c = (char) charStart; c <= (char) charEnd; c++) {
			// Draw char
			s[0] = c;
			canvas.drawText(s, 0, 1, x, y, paint);
			// Store source rectangle
			characterRects.put((int) c, new Rect(x, y - cellHeight, x
					+ cellWidth, y));
			// Increment and wrap at end of line
			x += cellWidth;
			if ((x + cellWidth) > textureSize) {
				x = 0;
				y += cellHeight;
			}
		}

		return bitmap;
	}
}