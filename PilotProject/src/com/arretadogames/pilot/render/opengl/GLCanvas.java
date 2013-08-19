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
import android.opengl.GLES11;
import android.opengl.GLUtils;
import android.util.Log;
import android.util.SparseArray;

import com.arretadogames.pilot.GameActivity;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.Fonts;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.loading.LoadableGLObject;
import com.arretadogames.pilot.loading.LoadableType;

public class GLCanvas {
	
	// OpenGLES1.0 Interface
	private GL10 gl;
	
	// SparseArray = HashMap<int, GLImage> - DrawableID -> GLImage
	private SparseArray<GLImage> textures = new SparseArray<GLImage>();
	
	// TypeFace = Font Properties - TypeFace -> FontTexture
	private HashMap<Typeface, FontTexture> fontTextures = new HashMap<Typeface, FontTexture>();
	
	// Rect to be used to store drawing calculations
	private Rect auxiliaryRect = new Rect();
	
	// Pixel/Meters Ratio
	public static float physicsRatio = 25;
	
	// Bottom-most Y coordinate position that the ground will have, in meters
	private final float GROUND_BOTTOM = -10;
	
	public void setGLInterface(GL10 gl) {
		this.gl = gl;
	}
	
	public void setPhysicsRatio(float newRatio) {
		physicsRatio = newRatio;
	}
	
	public boolean initiate() {
		
		GLES11.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		GLES11.glLoadIdentity();
		// Rotate world by 180 around x axis so positive y is down (like canvas)
		GLES11.glRotatef(-180, 1, 0, 0);
		
		// Fills the screen with black
		fillScreen(255, 0, 0, 0);

		return true;
	}

	
	public void translate(float dx, float dy) {
		GLES11.glTranslatef(dx, dy, 0);
	}

	
	public void scale(float sx, float sy, float px, float py) {
		GLES11.glTranslatef(px, py, 0);
		GLES11.glScalef(sx, sy, 0);
		GLES11.glTranslatef(-px, -py, 0);
	}

	
	public void rotate(float degrees) {
		GLES11.glRotatef(degrees, 0, 0, 1);
	}

	public void drawDebugRect(int x, int y, int x2, int y2) {
		auxiliaryRect.left = x;
		auxiliaryRect.top = y;
		auxiliaryRect.right = x2;
		auxiliaryRect.bottom = y2;
		
		GLRect.draw(gl, x, y, x2, y2, Color.RED);
	}

	
	public void drawPhysicsDebugRect(float centerX, float centerY,
			float sideLength) {
		drawPhysicsDebugRect(centerX, centerY, sideLength, Color.RED);
	}
	
	public void drawPhysicsDebugRect(float centerX, float centerY,
			float sideLength, int color) {
		auxiliaryRect.left = (int) ((centerX - sideLength / 2) * physicsRatio);
		auxiliaryRect.top = (int) (DisplaySettings.TARGET_HEIGHT - (centerY - sideLength / 2) * physicsRatio);
		auxiliaryRect.right = (int) ((centerX + sideLength / 2) * physicsRatio);
		auxiliaryRect.bottom = (int) (DisplaySettings.TARGET_HEIGHT - (centerY + sideLength / 2) * physicsRatio);
		
		GLRect.draw(gl, auxiliaryRect.left, auxiliaryRect.top, auxiliaryRect.right,
				auxiliaryRect.bottom, color);
	}
	
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
    	
    	short[] drawOrder = new short[12]; // FIXME : refactor this method
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
        
        GLES11.glColor4f(0.54f, 0.28f, 0.15f, 1f); // Brown
        GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        GLES11.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        GLES11.glDrawElements(GL10.GL_TRIANGLE_STRIP, drawOrder.length, GL10.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES11.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        GLES11.glColor4f(1,1,1,1);
		
	}

	public void saveState() {
		GLES11.glPushMatrix();
	}
	
	public void restoreState() {
		GLES11.glPopMatrix();
	}

	public void drawText(String text, float x, float y, Paint p, boolean centered) {
		if (fontTextures.get(p.getTypeface()) == null) {
			Log.e("GLCanvas", "Font not loaded when drawing (\"" + text + "\")");
			loadFont(p.getTypeface());
		}
		
		fontTextures.get(p.getTypeface()).drawText(gl, text, (int) x, (int) y, p.getTextSize(), p.getColor(), centered);
	}

	private int loadFont(Typeface typeface) {
		FontTexture fontTexture = new FontTexture(typeface, gl);
		fontTextures.put(typeface, fontTexture);
		return fontTexture.getGLId();
	}

	public void fillScreen(float a, float r, float g, float b) {
		GLES11.glClearColor(r / 255f, g / 255f, b / 255f, a / 255f);
	}

	public void drawRect(float left, float top, float right, float bottom, int color) {
		GLRect.draw(gl, left, top, right, bottom, color);
	}

	public void drawBitmap(int imageId, float x, float y) {
		drawBitmap(imageId, x, y, null);
	}
	
	public void drawBitmap(int imageId, float x, float y, Paint paint) {
		if (textures.get(imageId) == null) {
			Log.e("GLCanvas", "Texture not loaded");
			loadImage(imageId);
		}
		saveState();
			if (paint != null)
				GLES11.glColor4f(255, 255, 255, paint.getAlpha() / 255f);
			translate(x, y);
	
			GLImage texture = textures.get(imageId);
			GLTexture.draw(gl, 0, 0, texture.getTextureWidth(), texture.getTextureHeight(), Color.WHITE, texture);
		restoreState();
	}
	
	public void drawBitmap(int imageId, Rect srcRect, RectF dstRect,
			boolean convertFromPhysics) {
		
		if (textures.get(imageId) == null) {
			Log.e("GLCanvas", "Texture not loaded " +
					GameActivity.getContext().getResources().getResourceEntryName(imageId));
			loadImage(imageId);
		}
		
		GLImage tex = textures.get(imageId);
		GLES11.glColor4f(1, 1, 1, 1);
		
		if (convertFromPhysics) {
			auxiliaryRect.left = (int) (dstRect.left * physicsRatio);
			auxiliaryRect.top = (int) (DisplaySettings.TARGET_HEIGHT - dstRect.top * physicsRatio);
			auxiliaryRect.right = (int) (dstRect.right * physicsRatio);
			auxiliaryRect.bottom = (int) (DisplaySettings.TARGET_HEIGHT - dstRect.bottom * physicsRatio);
		} else {
			auxiliaryRect.left = (int) dstRect.left;
			auxiliaryRect.top = (int) dstRect.top;
			auxiliaryRect.right = (int) dstRect.right;
			auxiliaryRect.bottom = (int) dstRect.bottom;
		}
		
		GLTexture.draw(gl, srcRect, auxiliaryRect, tex);
	}

	
	public void drawBitmap(int imageId, RectF dstRect,
			boolean convertFromPhysics, Paint paint) {
		drawBitmap(imageId, null, dstRect, convertFromPhysics);
	}

	
	public void drawBitmap(int imageId, RectF dstRect,
			boolean convertFromPhysics) {
		drawBitmap(imageId, null, dstRect, convertFromPhysics);
	}

	
	public int loadImage(int imageId) {
		// Get bitmap
		Bitmap bitmap = ImageLoader.loadImage(imageId);
		return loadImage(imageId, bitmap);
	}
	
	private int loadImage(int imageId, Bitmap bitmapToLoad) { /* We also load Bitmaps in FontTexture */
		IntBuffer t = IntBuffer.allocate(1);
		GLES11.glGenTextures(1, t);
		int texture_id = t.get(0);
		
		GLImage texture = new GLImage(texture_id);
		
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

		// Attach bitmap to current texture
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapToLoad, 0);

		// Add dimensional info to spritedata
		texture.setDimensions(bitmapToLoad.getWidth(), bitmapToLoad.getHeight());
		textures.append(imageId, texture);
		return texture_id;
	}
	
	public void recycleImage(int imageId) {
		// TODO Auto-generated method stub
	}
	
	public void translatePhysics(float posX, float posY) {
		GLES11.glTranslatef(posX * physicsRatio, DisplaySettings.TARGET_HEIGHT - posY * physicsRatio, 0);
	}

	public void removeTextures(LoadableGLObject[] objects) {
		
		int[] glIds = new int[objects.length];
		for (int i = 0 ; i < objects.length ; i++) {
			
			if (objects[i].getType().equals(LoadableType.TEXTURE)){
				glIds[i] = textures.get(objects[i].getId()).getTextureID();
				textures.remove(objects[i].getId());
			} else if (objects[i].getType().equals(LoadableType.FONT)) {
//				fontTextures.get(key)
			}
			
		}
		
		GLES11.glDeleteTextures(glIds.length, glIds, 0);
	}
	
	public void loadObject(LoadableGLObject object) {
		
		if (object.getType().equals(LoadableType.TEXTURE)) {
			object.setGLId(loadImage(object.getId()));
		} else if (object.getType().equals(LoadableType.FONT)) {
			object.setGLId(loadFont(FontLoader.getInstance().getFont(Fonts.values()[object.getId()])));
		}
		
	}
}
