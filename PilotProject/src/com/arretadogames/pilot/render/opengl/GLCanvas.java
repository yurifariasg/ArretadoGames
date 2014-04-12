package com.arretadogames.pilot.render.opengl;

import java.nio.IntBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES11;
import android.opengl.GLUtils;
import android.util.Log;
import android.util.SparseArray;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.loading.FontSpecification;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.loading.LoadableGLObject;
import com.arretadogames.pilot.loading.LoadableType;
import com.arretadogames.pilot.render.PhysicsRect;

public class GLCanvas {

	// OpenGLES1.0 Interface
	private GL10 gl;

	// SparseArray = HashMap<int, GLImage> - DrawableID -> GLImage
	private SparseArray<GLTexture> textures = new SparseArray<GLTexture>();

	// FontSpecification = Font Properties
	private HashMap<FontSpecification, GLTexturedFont> fontTextures = new HashMap<FontSpecification, GLTexturedFont>();

	// Rect to be used to store drawing calculations
	private Rect auxiliaryRect = new Rect();

	// Pixel/Meters Ratio
	public static float physicsRatio = 25;

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
		setClearColor(255, 0, 0, 0);

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

	public void saveState() {
		GLES11.glPushMatrix();
	}

	public void restoreState() {
		GLES11.glPopMatrix();
	}

	public void drawText(String text, float x, float y, FontSpecification fs, float size, boolean centered) {
		if (fontTextures.get(fs) == null) {
			Log.w("GLCanvas", "Font not loaded when drawing (\"" + text + "\")");
			if (GameSettings.LAZY_LOAD_ENABLED)
				loadFont(fs);
		}

		fontTextures.get(fs).drawText(gl, text, (int) x, (int) y, size, centered);
	}
	
	public void drawText(String text, float x, float y, FontSpecification fs, float size,
			boolean centered, float alpha) {
		
		if (alpha >= 0 && alpha <= 255) {
            GLES11.glColor4f(1f, 1f, 1f, alpha / 255f);
        }
		
		drawText(text, x, y, fs, size, centered);
		
		if (alpha >= 0 && alpha <= 255) {
            GLES11.glColor4f(1f, 1f, 1f, 1f);
        }
		
	}

	private int loadFont(FontSpecification fs) {
		GLTexturedFont fontTexture = new GLTexturedFont(fs, gl);
		fontTextures.put(fs, fontTexture);
		return fontTexture.getGLId();
	}

	public void setClearColor(float a, float r, float g, float b) {
		GLES11.glClearColor(r / 255f, g / 255f, b / 255f, a / 255f);
	}
	
	public void drawRect(final RectF dst, int color) {
        GLRect.draw(gl, dst.left, dst.top, dst.right, dst.bottom, color);
    }

	public void drawRect(float left, float top, float right, float bottom, int color) {
		GLRect.draw(gl, left, top, right, bottom, color);
	}

	public void drawLines(Vec2[] vecs, float width, int color, boolean connectStartAndEnd) {
		GLLine.drawLineStrip(vecs, vecs.length, width, color, connectStartAndEnd, 1);
	}

	public void drawPhysicsLines(Vec2[] vecs, int count, float width, int color, boolean connectStartAndEnd) {
		GLLine.drawLineStrip(vecs, count, width, color, connectStartAndEnd, GLCanvas.physicsRatio);
	}

	public void drawGroundLines(Vec2[] vecs, int count, float width, int color) {
		GLLine.drawLineStrip(vecs, count, width, color, false, GLCanvas.physicsRatio, true);
	}

	public void drawBitmap(int imageId, float x, float y, float width, float height) {
		drawBitmap(imageId, x, y, width, height, 0, 0, -1);
	}

    public void drawBitmap(int imageId, float x, float y, float width, float height, float alpha) {
        drawBitmap(imageId, x, y, width, height, 0, 0, alpha);
    }

	public void drawBitmap(int imageId, float x, float y, float width, float height, float extraWidth, float extraHeight) {
        drawBitmap(imageId, x, y, width, height, extraWidth, extraHeight, -1);
    }

	public void drawBitmap(int imageId, float x, float y, float width, float height,
	        float extraWidth, float extraHeight, float alpha) {
		if (textures.get(imageId) == null) {
			Log.w("GLCanvas", "Texture not loaded: " +
					MainActivity.getContext().getResources().getResourceEntryName(imageId));
			if (GameSettings.LAZY_LOAD_ENABLED)
				loadImage(imageId);
		}
		saveState();
    		if (alpha >= 0 && alpha <= 255) {
                GLES11.glColor4f(1f, 1f, 1f, alpha / 255f);
            }
			translate(x, y);

			GLTexture texture = textures.get(imageId);
			GLTexturedRect.draw(gl,
			        0, 0, texture.getTextureWidth() - extraWidth, texture.getTextureHeight() - extraHeight,
			        0, 0, width, height, texture);
			
    		if (alpha >= 0 && alpha <= 255) {
    			GLES11.glColor4f(1f, 1f, 1f, 1f);
    		}
		restoreState();
	}

	public void drawBitmap(int imageId, Rect srcRect, RectF dstRect) {
		if (textures.get(imageId) == null) {
			Log.w("GLCanvas", "Texture not loaded " +
					MainActivity.getContext().getResources().getResourceEntryName(imageId));
			if (GameSettings.LAZY_LOAD_ENABLED)
				loadImage(imageId);
		}

		GLTexture tex = textures.get(imageId);
//		GLES11.glColor4f(1, 1, 1, 1);

		auxiliaryRect.left = (int) dstRect.left;
		auxiliaryRect.top = (int) dstRect.top;
		auxiliaryRect.right = (int) dstRect.right;
		auxiliaryRect.bottom = (int) dstRect.bottom;

		GLTexturedRect.draw(gl, srcRect, auxiliaryRect, tex);
	}

	public void drawBitmap(int imageId, RectF dstRect) {
		if (textures.get(imageId) == null) {
			Log.w("GLCanvas", "Texture not loaded " +
					MainActivity.getContext().getResources().getResourceEntryName(imageId));
			if (GameSettings.LAZY_LOAD_ENABLED)
				loadImage(imageId);
		}
		GLTexture img = textures.get(imageId);
		GLTexturedRect.draw(gl,
		        0, 0, img.getTextureWidth(), img.getTextureHeight(),
				dstRect.left, dstRect.top,
				dstRect.right, dstRect.bottom,
				textures.get(imageId));
	}

	public void drawColorRect(int color, PhysicsRect physicsRect) {
		GLRect.draw(gl,
				physicsRect.left * physicsRatio,
				physicsRect.top * physicsRatio,
				physicsRect.right * physicsRatio,
				physicsRect.bottom * physicsRatio,
				color);
	}

	public void drawBitmap(int imageId, PhysicsRect physicsRect) {
		if (textures.get(imageId) == null) {
			Log.e("GLCanvas", "Texture not loaded " +
					MainActivity.getContext().getResources().getResourceEntryName(imageId));
			if (GameSettings.LAZY_LOAD_ENABLED)
				loadImage(imageId);
		}

		GLTexture tex = textures.get(imageId);
//		GLES11.glColor4f(1, 1, 1, 1);

		GLTexturedRect.draw(gl,
		        0, 0, tex.getTextureWidth(), tex.getTextureHeight(),
				physicsRect.left * physicsRatio,
				physicsRect.top * physicsRatio,
				physicsRect.right * physicsRatio,
				physicsRect.bottom * physicsRatio,
				tex);
	}

	public int loadImage(int imageId) {
		// Get bitmap
		Bitmap bitmap = ImageLoader.loadImage(imageId);
		return loadImage(imageId, bitmap);
	}

	public void drawRect(
    		float x, float y,
    		float x2, float y2,
    		float x3, float y3,
    		float x4, float y4,
    		int colorV1, int colorV2,
    		int colorV3, int colorV4) {
		GLRect.draw(gl,
				(x), (y),
				(x2),(y2),
				(x3), (y3),
				(x4), (y4),
				colorV1, colorV2, colorV3, colorV4);
	}
	
	public void setColor(int color) {
        GLES11.glColor4f(Color.red(color) / 255f, Color.green(color) / 255f,
                Color.blue(color) / 255f, Color.alpha(color) / 255f);
	}
	
	public void resetColor() {
	    setColor(Color.WHITE);
	}

	public void drawRectFromPhysics(
    		float x, float y,
    		float x2, float y2,
    		float x3, float y3,
    		float x4, float y4,
    		int colorV1, int colorV2,
    		int colorV3, int colorV4) {
		GLRect.draw(gl,
				(x*physicsRatio), GameSettings.TARGET_HEIGHT - (y*physicsRatio),
				(x2*physicsRatio), GameSettings.TARGET_HEIGHT - (y2*physicsRatio),
				(x3*physicsRatio), GameSettings.TARGET_HEIGHT - (y3*physicsRatio),
				(x4*physicsRatio), GameSettings.TARGET_HEIGHT - (y4*physicsRatio),
				colorV1, colorV2, colorV3, colorV4);
	}

	private int loadImage(int imageId, Bitmap bitmapToLoad) { /* We also load Bitmaps in FontTexture */
		IntBuffer t = IntBuffer.allocate(1);
		GLES11.glGenTextures(1, t);
		int texture_id = t.get(0);

		GLTexture texture = new GLTexture(texture_id);

		// Working with textureId
		GLES11.glBindTexture(GL10.GL_TEXTURE_2D, texture_id);

		// SETTINGS
		// Scale up if the texture is smaller.
		GLES11.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		// Scale down if the mesh is smaller.
		GLES11.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		// Clamp to edge behaviour at edge of texture (repeats last pixel)
		GLES11.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_REPEAT);

		GLES11.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);

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
		GLES11.glTranslatef(posX * physicsRatio, GameSettings.TARGET_HEIGHT - posY * physicsRatio, 0);
	}

	public void removeTextures(LoadableGLObject[] objects) {

		int[] glIds = new int[objects.length];
		for (int i = 0 ; i < objects.length ; i++) {

			if (objects[i].getType().equals(LoadableType.TEXTURE)){
				if (textures.get(objects[i].getId()) == null)
					continue;
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
			object.setGLId(loadFont(FontLoader.getInstance().getFont(FontTypeFace.values()[object.getId()])));
		}
	}

	private Vec2[] auxVec = new Vec2[] { new Vec2(), new Vec2() };
	public void drawLine(float f, float g, float h, float i, float width, int color) {
		auxVec[0].x = f;
		auxVec[0].y = g;
		auxVec[1].x = h;
		auxVec[1].y = i;
		drawLines(auxVec, width, color, false);
	}
}
