package com.arretadogames.pilot.render.opengl;

import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES11;
import android.opengl.GLUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.arretadogames.pilot.loading.FontSpecification;

public class GLTexturedFont {
	// Font texture for drawing text

	// General
	private FontSpecification fontSpec;

	// Render parameters
	private int fontHeight;
	private SparseIntArray charWidths;
	private SparseArray<Rect> characterRects;
	private int cellWidth;
	private int cellHeight;

	// Font settings (defaults)
	private int charStart = 32;
	private int charEnd = 126;
	private int charUnknown = 32; // Must be between start and end
	private int padX = 4; // X Padding 4
	private int padY = 2; // Y Padding 4

	private GLTexture spriteData;

	public GLTexturedFont(FontSpecification fontSpecification, GL10 gl) {//Typeface tf, GL10 gl) {
		this.fontSpec = fontSpecification;
		createTexture(gl);
	}
	
	public void drawText(GL10 gl, String text, int x, int y, float scale, boolean centered) {
		scale /= 2;
		// Get width of text
		int textWidth = 0;
		int charWidth;
		for (int i = 0; i < text.length(); i++) {
			charWidth = charWidths.get((int) text.charAt(i));
			textWidth += charWidth * scale;
		}
		if (centered) {
			// Adjust to centre text about x,y
			x -= textWidth / 2;
		}
		
//		Uncomment the following line to draw the baseline
//		GLLine.draw(x, y, x + textWidth, y, 2, Color.YELLOW);
		
		y -= scale * fontHeight / 2;

		// No cycle through and draw text
		int scaledCellWidth = (int) (scale * cellWidth);
		int scaledCellHeight = (int) (scale * cellHeight);
		Rect src;
		Rect dst = new Rect();
		int charNumber;
//		GLES11.glColor4f(Color.red(argb) / 255f, Color.green(argb) / 255f, Color.blue(argb) / 255f, Color.alpha(argb) / 255f);
		gl.glColor4f(1, 1, 1, 1);
		gl.glPushMatrix();
		for (int i = 0; i < text.length() ; i++) {
			// Source rect
			charNumber = (int) text.charAt(i);
			src = characterRects.get(charNumber);
			charWidth = charWidths.get(charNumber);
			if (src == null) {
				// Defaults to unknown char
				src = characterRects.get(text.charAt(charUnknown));
				charWidth = charWidths.get(text.charAt(charUnknown));
			}
			src = new Rect(src);
			src.right -= 1; // Adjust

			// Destination rect
			dst.set(x, y, x + scaledCellWidth, y + scaledCellHeight);

			// Draw
			GLTexturedRect.draw(gl, src, dst, spriteData);
			// Move forward CHAR WIDTH (not cell width)
			x += charWidth * scale;
		}
		gl.glPopMatrix();
	}
	
	
	private void createTexture(GL10 gl) {
		loadBitmap(gl, createBitmap());
	}
	
	int textureSize;
	
	public Bitmap createBitmap() {
		// We use the font to create a sprite atlas containing every letter,
		// then we return the sprite atlas bitmap to be used as the texture.
		Paint paint = fontSpec.getFontPaint();
		Paint mStrokePaint = fontSpec.getStrokePaint();
		boolean hasStroke = mStrokePaint != null;

		// get font metrics
		Paint.FontMetrics fm = paint.getFontMetrics();
		fontHeight = (int) Math.ceil(Math.abs(fm.bottom) + Math.abs(fm.top));
		if (hasStroke)
			fontHeight += (int)Math.floor(mStrokePaint.getStrokeWidth()) * 2;

		// Store for char widths
		charWidths = new SparseIntArray();

		// Cycle through chars and store width of each character
		char[] s = new char[1];
		float[] w = new float[1];
		int charWidthMax = 0;
		int charWidth;
		for (char c = (char) charStart; c <= (char) charEnd; c++) {
			s[0] = c;
			paint.getTextWidths(s, 0, 1, w);
			charWidth = (int) Math.ceil(w[0]);
			if (hasStroke)
				charWidth += (int)Math.floor(mStrokePaint.getStrokeWidth()) * 2;
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
		// Here is how calculation goes:
		// Number of chars = 95
		// Square root (round up) = 10 x 10 grid
		// Max size = 256 / 10 = 25.6 (24?)
	    textureSize = 0;
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
		Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ARGB_4444);
		// Create Canvas for rendering to Bitmap
		Canvas canvas = new Canvas(bitmap);
		bitmap.eraseColor(0x00000000); // Set Transparent Background (ARGB)

		// Render each of the characters to the canvas (i.e. build the font map)
		// Also store a source rectangle for each char
		characterRects = new SparseArray<Rect>();
		int x = 0;
		int y = cellHeight;// (cellHeight - 1) - fontDescent - padY;
		for (char c = (char) charStart; c <= (char) charEnd; c++) {
			// Draw char
			s[0] = c;
			// Store source rectangle
			if (hasStroke)
				characterRects.put((int) c,
					new Rect(
							x - (int)(Math.floor(mStrokePaint.getStrokeWidth())),
							(int) (y - cellHeight * 0.7),
							x + cellWidth - padX * 4,
							(int) (y + cellHeight * 0.3 - padY)));
			else
				characterRects.put((int) c,
						new Rect(x, (int) (y - cellHeight * 0.7), x
						+ cellWidth, (int) (y + cellHeight * 0.3)));
			

			canvas.drawText(s, 0, 1, x, y, paint);
			if (hasStroke)
				canvas.drawText(s, 0, 1, x, y, mStrokePaint);
			
			// Increment and wrap at end of line
			x += cellWidth;
			if ((x + cellWidth) > textureSize) {
				x = 0;
				y += cellHeight;
			}
		}

		return bitmap;
	}
	
	// Load Bitmap
	private void loadBitmap(GL10 gl, Bitmap bitmapToLoad) {
		IntBuffer t = IntBuffer.allocate(1);
		GLES11.glGenTextures(1, t);
		int texture_id = t.get(0);
		
		spriteData = new GLTexture(texture_id);
		
		// Working with textureId
		GLES11.glBindTexture(GL10.GL_TEXTURE_2D, texture_id);

		// SETTINGS
		// Scale up if the texture is smaller.
		GLES11.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		// Scale down if the mesh is smaller.
		GLES11.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		// Clamp to edge behaviour at edge of texture (repeats last pixel)
		GLES11.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		GLES11.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		// Attach bitmap to current texture
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapToLoad, 0);

		// Add dimensional info to spritedata
		spriteData.setDimensions(textureSize, textureSize);
	}

	public int getGLId() {
		return spriteData.getTextureID();
	}
	
}
