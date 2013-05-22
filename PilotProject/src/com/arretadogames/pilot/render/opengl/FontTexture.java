package com.arretadogames.pilot.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.opengl.GLUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;

public class FontTexture {
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

	private SpriteData spriteData;

	public FontTexture(Typeface tf) {
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
	
	public void drawText(GL10 gl, String text, int x, int y, float scale, int argb, boolean centered) {
		
		if (spriteData == null)  {// Not created
			createTexture(gl);
		}
		
		// Draw text centred at x,y
		// Get width of text
		int textWidth = 0;
		int charWidth;
		for (int i = 0; i < text.length(); i++) {
			charWidth = charWidths.get((int) text.charAt(i));
			textWidth += charWidth * scale;
		}
		// Adjust to centre text about x,y
		x -= textWidth / 2;
		y -= fontHeight / 2;

		// No cycle through and draw text
		int scaledCellWidth = (int) (scale * cellWidth);
		int scaledCellHeight = (int) (scale * cellHeight);
		Rect src;
		Rect dst = new Rect();
		int charNumber;
		gl.glColor4f(Color.red(argb) / 255f, Color.green(argb) / 255f, Color.blue(argb) / 255f, Color.alpha(argb) / 255f);
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

			// Add sprite
			spriteData.clear();
			spriteData.addSprite(src,dst);
			// Move forward CHAR WIDTH (not cell width)
			x += charWidth * scale;
			draw(gl);
		}
		gl.glColor4f(1,1,1,1);
	}
	
	
	private void createTexture(GL10 gl) {
		loadBitmap(gl, getBitmap());
	}
	
	int textureSize;
	
	public Bitmap getBitmap() {
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
			characterRects.put((int) c, new Rect(x, (int) (y - cellHeight * 0.7), x
					+ cellWidth, (int) (y + cellHeight * 0.3)));
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
		gl.glGenTextures(1, t);
		int texture_id = t.get(0);
		
		spriteData = new SpriteData(Color.WHITE, texture_id);
		
//		gl.glEnable(GL10.GL_TEXTURE_2D);

		// Working with textureId
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_id);

		// SETTINGS
		// Scale up if the texture is smaller.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		// Scale down if the mesh is smaller.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		// Clamp to edge behaviour at edge of texture (repeats last pixel)
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		// Attach bitmap to current texture
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapToLoad, 0);

		// Add dimensional info to spritedata
		spriteData.setDimensions(textureSize, textureSize);
//		texture.addSprite(new Rect(0, 0, bitmapToLoad.getWidth(), bitmapToLoad.getHeight()));
//		textures.append(imageId, texture);
		
//		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	private void draw(GL10 gl) {
		// CONVERT INTO ARRAY
		float[] vertices = spriteData.getVertices();
		short[] indices = spriteData.getIndices();
		float[] textureCoords = spriteData.getTextureCoords();
		
		
		// ONLY DRAW IF ALL NOT NULL
		if (vertices != null && indices != null
				&& textureCoords != null) {
			// CREATE BUFFERS - these are just containers for sending
			// the
			// draw information we have already collected to OpenGL

			// Vertex buffer (position information of every draw
			// command)
			ByteBuffer vbb = ByteBuffer
					.allocateDirect(vertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			FloatBuffer vertexBuffer = vbb.asFloatBuffer();
			vertexBuffer.put(vertices);
			vertexBuffer.position(0);

			// Index buffer (which vertices go together to make the
			// elements)
			ByteBuffer ibb = ByteBuffer
					.allocateDirect(indices.length * 2);
			ibb.order(ByteOrder.nativeOrder());
			ShortBuffer indexBuffer = ibb.asShortBuffer();
			indexBuffer.put(indices);
			indexBuffer.position(0);

			// How to paste the texture over each element so that the
			// right
			// image is shown
			ByteBuffer tbb = ByteBuffer
					.allocateDirect(textureCoords.length * 4);
			tbb.order(ByteOrder.nativeOrder());
			FloatBuffer textureBuffer = tbb.asFloatBuffer();
			textureBuffer.put(textureCoords);
			textureBuffer.position(0);

			// CONVERT RGBA TO SEPERATE VALUES
//					int color = currentSpriteData.getARGB();
//					float r = Color.red(color) / 255f;
//					float g = Color.green(color) / 255f;
//					float b = Color.blue(color) / 255f;
//					float a = Color.alpha(color) / 255f;

			// DRAW COMMAND
			
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//					gl.glEnableClientState(GL10.GL_CO);
			
//					gl.glColor4f(r, g, b, a);
			// Tell OpenGL where our texture is located.
			gl.glBindTexture(GL10.GL_TEXTURE_2D, spriteData.getTextureID());
			// Telling OpenGL where our textureCoords are.
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
			// Specifies the location and data format of the array of
			// vertex
			// coordinates to use when rendering.
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
			// Draw elements command using indices so it knows which
			// vertices go together to form each element
			gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
					GL10.GL_UNSIGNED_SHORT, indexBuffer);

			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL10.GL_TEXTURE_2D);
			
		}
	}
}
