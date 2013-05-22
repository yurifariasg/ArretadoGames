package com.arretadogames.pilot.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.opengl.GLUtils;
import android.util.SparseArray;

import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.loading.ImageLoader;

public class GLCanvas {
	
	private GL10 gl;
	private static SparseArray<SpriteData> textures = new SparseArray<SpriteData>();
	private static HashMap<Typeface, FontTexture> fontTextures = new HashMap<Typeface, FontTexture>();
	
	private Rect arbritaryRect = new Rect();
	public static float physicsRatio = 25;
	
	
	public GLCanvas(GL10 gl) {
		this.gl = gl;
	}

	
	public void setPhysicsRatio(float newRatio) {
		physicsRatio = newRatio;
	}

	
	public boolean initiate() {
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		gl.glLoadIdentity();
		// Rotate world by 180 around x axis so positive y is down (like canvas)
		gl.glRotatef(-180, 1, 0, 0);
		
		fillScreen(255, 0, 0, 0);
		
		/* OpenGL 2.0 */
		// Clears the screen and depth buffer.
//		GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
//		GLES20.gl
		// Rotate world by 180 around x axis so positive y is down (like canvas)
//		GLES20.glRotatef(-180, 1, 0, 0);
		return true;
	}

	
	public void flush() {
	}

	
	public void translate(float dx, float dy) {
		gl.glTranslatef(dx, dy, 0);
	}

	
	public void scale(float sx, float sy, float px, float py) {
		gl.glTranslatef(px, py, 0);
		gl.glScalef(sx, sy, 0);
		gl.glTranslatef(-px, -py, 0);
	}

	
	public void rotate(float degrees) {
		gl.glRotatef(degrees, 0, 0, 1);
	}

	
	public void rotatePhysics(float degrees) {
		rotate(degrees); // TODO: Method should be removed
	}

	
	public void drawDebugRect(int x, int y, int x2, int y2) {
		// TODO Auto-generated method stub
		arbritaryRect.left = x;
		arbritaryRect.top = y;
		arbritaryRect.right = x2;
		arbritaryRect.bottom = y2;
		drawRect(arbritaryRect, Color.RED);
	}

	
	public void drawCameraDebugRect(float x, float y, float x2, float y2) {
		// TODO Auto-generated method stub
	}

	
	public void drawPhysicsDebugRect(float centerX, float centerY,
			float sideLength) {
		drawPhysicsDebugRect(centerX, centerY, sideLength, Color.RED);
	}

	
	public void drawPhysicsDebugRect(float centerX, float centerY,
			float sideLength, int color) {
		arbritaryRect.left = (int) ((centerX - sideLength / 2) * physicsRatio);
		arbritaryRect.top = (int) (DisplaySettings.TARGET_HEIGHT - (centerY - sideLength / 2) * physicsRatio);
//		arbritaryRect.top = (int) ((centerY - sideLength / 2) * physicsRatio);
		arbritaryRect.right = (int) ((centerX + sideLength / 2) * physicsRatio);
		arbritaryRect.bottom = (int) (DisplaySettings.TARGET_HEIGHT - (centerY + sideLength / 2) * physicsRatio);
//		arbritaryRect.bottom = (int) ((centerY + sideLength / 2) * physicsRatio);
		drawRect(arbritaryRect, color);
	}
	
	private final float GROUND_BOTTOM = -10;

	
	public void drawPhysicsLines(Vec2[] lines) {
		
		Vec2 vertices[] = lines;
    	int cont = vertices.length + 3;
    	float[] squareCoords = new float[3*cont];

    	// Ultimo
    	squareCoords[0] = vertices[0].x * physicsRatio;
    	squareCoords[1] = DisplaySettings.TARGET_HEIGHT - GROUND_BOTTOM * physicsRatio;
    	squareCoords[2] = 0.0f;
    	
    	for( int i = 0; i < vertices.length; i++){
    		squareCoords[3 + 3*i] = vertices[i].x * physicsRatio;
    		squareCoords[3 + 3*i+1] = DisplaySettings.TARGET_HEIGHT - vertices[i].y * physicsRatio;
    		squareCoords[3 + 3*i+2] = 0.0f;
    	}
    	
    	// Penultimo
    	squareCoords[3 + 3 * vertices.length] = vertices[vertices.length - 1].x * physicsRatio;
    	squareCoords[3 + 3 * vertices.length + 1] = DisplaySettings.TARGET_HEIGHT - GROUND_BOTTOM * physicsRatio;
    	squareCoords[3 + 3 * vertices.length + 2] = 0.0f;

    	// Ultimo
    	squareCoords[3 + 3 * (vertices.length + 1)] = vertices[0].x * physicsRatio;
    	squareCoords[3 + 3 * (vertices.length + 1) + 1] = DisplaySettings.TARGET_HEIGHT - GROUND_BOTTOM * physicsRatio;
    	squareCoords[3 + 3 * (vertices.length + 1) + 2] = 0.0f;
    	
    	short[] drawOrder = new short[12]; // FIXME : fix this shit...
    	int i = 0;
    	drawOrder[i++] = 0;
    	drawOrder[i++] = 1;
    	drawOrder[i++] = 2;
    	
    	drawOrder[i++] = 0;
    	drawOrder[i++] = 2;
    	drawOrder[i++] = 3;
    	
    	drawOrder[i++] = 0;
    	drawOrder[i++] = 3;
    	drawOrder[i++] = 4;
    	
    	drawOrder[i++] = 0;
    	drawOrder[i++] = 4;
    	drawOrder[i++] = 5;
    	
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        
        gl.glColor4f(0.54f, 0.28f, 0.15f, 1f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, drawOrder.length, GL10.GL_UNSIGNED_SHORT, drawListBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColor4f(1,1,1,1);
		
		
	}

	
	public void drawPhysicsLine(float x1, float y1, float x2, float y2) {
		float[] vertices = {x1, y1, 0, x2, y2, 0};
		short[] indices = {0, 1, 0};
		
        ByteBuffer vbb  = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        ShortBuffer indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glLineWidth(2);
        gl.glColor4f(1, 0, 0, 1);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawElements(GL10.GL_LINES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        
	}

	
	public void saveState() {
		gl.glPushMatrix();
	}

	
	public void restoreState() {
		gl.glPopMatrix();
	}

	
	public void drawText(String text, float x, float y, Paint p, boolean centered) {
		if (fontTextures.get(p.getTypeface()) == null)
			createFont(p.getTypeface());
		
		fontTextures.get(p.getTypeface()).drawText(gl, text, (int) x, (int) y, p.getTextSize(), p.getColor(), centered);
	}

	private void createFont(Typeface typeface) {
		FontTexture fontTexture = new FontTexture(typeface);
		fontTextures.put(typeface, fontTexture);
	}

	
	public void fillScreen(float a, float r, float g, float b) {
		gl.glClearColor(r / 255f, g / 255f, b / 255f, a / 255f);
	}

	
	public void drawRect(Rect rect, int argb) {
		gl.glColor4f(Color.red(argb) / 255f, Color.green(argb) / 255f, Color.blue(argb) / 255f, Color.alpha(argb) / 255f);
		GLRect glRect = GLRect.create(rect);
		glRect.draw(gl);
	}

	
	public void drawBitmap(int imageId, float x, float y) {
		saveState();
		gl.glColor4f(1, 1, 1, 1);
		translate(x, y);
		drawBitmap(imageId);
		restoreState();
	}
	
	private void drawBitmap(int imageId) {
		SpriteData currentSpriteData = textures.get(imageId);
		if (currentSpriteData == null) {
			loadImage(imageId);
			currentSpriteData = textures.get(imageId);
		}
		
		// CONVERT INTO ARRAY
		float[] vertices = currentSpriteData.getVertices();
		short[] indices = currentSpriteData.getIndices();
		float[] textureCoords = currentSpriteData.getTextureCoords();
		
		
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

			// DRAW COMMAND
			
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			
			// Tell OpenGL where our texture is located.
			gl.glBindTexture(GL10.GL_TEXTURE_2D, currentSpriteData.getTextureID());
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

	
	public void drawBitmap(int imageId, float x, float y, Paint paint) {
		saveState();
		gl.glColor4f(255, 255, 255, paint.getAlpha() / 255f);
		translate(x, y);

		if (textures.get(imageId) == null)
			loadImage(imageId);
		SpriteData texture = textures.get(imageId);
		texture.clear();
		texture.addSprite(new Rect(0, 0, texture.getTextureWidth(), texture.getTextureHeight()));
		drawBitmap(imageId);
		restoreState();
	}

	
	public void drawBitmap(int imageId, Rect srcRect, RectF dstRect,
			boolean convertFromPhysics) {
		if (textures.get(imageId) == null)
			loadImage(imageId);
		SpriteData tex = textures.get(imageId);
		gl.glColor4f(1, 1, 1, 1);
		
		if (convertFromPhysics) {
			arbritaryRect.left = (int) (dstRect.left * physicsRatio);
			arbritaryRect.top = (int) (DisplaySettings.TARGET_HEIGHT - dstRect.top * physicsRatio);
			arbritaryRect.right = (int) (dstRect.right * physicsRatio);
			arbritaryRect.bottom = (int) (DisplaySettings.TARGET_HEIGHT - dstRect.bottom * physicsRatio);
		} else {
			arbritaryRect.left = (int) dstRect.left;
			arbritaryRect.top = (int) dstRect.top;
			arbritaryRect.right = (int) dstRect.right;
			arbritaryRect.bottom = (int) dstRect.bottom;
		}
		tex.clear();
		if (srcRect == null)
			tex.addSprite(arbritaryRect);
		else
			tex.addSprite(srcRect, arbritaryRect);
		
		textures.append(imageId, tex);
		
		drawBitmap(imageId);
	}

	
	public void drawBitmap(int imageId, RectF dstRect,
			boolean convertFromPhysics, Paint paint) {
		drawBitmap(imageId, null, dstRect, convertFromPhysics);
	}

	
	public void drawBitmap(int imageId, RectF dstRect,
			boolean convertFromPhysics) {
		drawBitmap(imageId, null, dstRect, convertFromPhysics);
	}

	
	public void loadImage(int imageId) {
		
		// Get bitmap
		Bitmap bitmap = ImageLoader.loadImage(imageId);
		loadImage(imageId, bitmap);
	}
	
	private void loadImage(int imageId, Bitmap bitmapToLoad) { /* We also load Bitmaps in FontTexture */
		IntBuffer t = IntBuffer.allocate(1);
		gl.glGenTextures(1, t);
		int texture_id = t.get(0);
		
		SpriteData texture = new SpriteData(Color.WHITE, texture_id);
		
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
		texture.setDimensions(bitmapToLoad.getWidth(), bitmapToLoad.getHeight());
		texture.addSprite(new Rect(0, 0, bitmapToLoad.getWidth(), bitmapToLoad.getHeight()));
		textures.append(imageId, texture);
	}

	
	public void recycleImage(int imageId) {
		// TODO Auto-generated method stub
		
	}

	
	public void translatePhysics(float posX, float posY) {
		gl.glTranslatef(posX * physicsRatio, DisplaySettings.TARGET_HEIGHT - posY * physicsRatio, 0);
	}

}
