package com.arretadogames.pilot.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLUtils;
import android.util.SparseArray;

import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.GameCanvas;

public class OpenGLCanvas implements GameCanvas {
	
	private GL10 gl;
	private static SparseArray<SpriteData> textures = new SparseArray<SpriteData>();
	
	
	public OpenGLCanvas(GL10 gl) {
		this.gl = gl;
	}

	@Override
	public void setPhysicsRatio(float newRatio) {
	}

	@Override
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

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void translate(float dx, float dy) {
		gl.glTranslatef(dx, dy, 0);
	}

	@Override
	public void scale(float sx, float sy, float px, float py) {
		gl.glTranslatef(px, py, 0);
		gl.glScalef(sx, sy, 0);
		gl.glTranslatef(-px, -py, 0);
	}

	@Override
	public void rotate(float degrees) {
		gl.glRotatef(degrees, 0, 0, 1);
	}

	@Override
	public void rotatePhysics(float degrees) {
		// TODO Check if this really works
		rotate(degrees);
	}

	@Override
	public void drawDebugRect(int x, int y, int x2, int y2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawCameraDebugRect(float x, float y, float x2, float y2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawPhysicsDebugRect(float centerX, float centerY,
			float sideLength) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawPhysicsDebugRect(float centerX, float centerY,
			float sideLength, int color) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawPhysicsLines(Vec2[] lines) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawPhysicsLine(float x1, float y1, float x2, float y2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void saveState() {
		gl.glPushMatrix();
	}

	@Override
	public void restoreState() {
		gl.glPopMatrix();
	}

	@Override
	public void drawText(String text, float x, float y, Paint p) {
		// TODO Auto-generated method stub
	}

	@Override
	public void fillScreen(float a, float r, float g, float b) {
		gl.glClearColor(r / 255f, g / 255f, b / 255f, a / 255f);
	}

	@Override
	public void drawRect(Rect rect, int argb) {
		gl.glColor4f(Color.red(argb) / 255f, Color.green(argb) / 255f, Color.blue(argb) / 255f, Color.alpha(argb) / 255f);
		GLRect glRect = GLRect.create(rect);
		glRect.draw(gl);
	}

	@Override
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

			// CONVERT RGBA TO SEPERATE VALUES
//			int color = currentSpriteData.getARGB();
//			float r = Color.red(color) / 255f;
//			float g = Color.green(color) / 255f;
//			float b = Color.blue(color) / 255f;
//			float a = Color.alpha(color) / 255f;

			// DRAW COMMAND
			
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//			gl.glEnableClientState(GL10.GL_CO);
			
//			gl.glColor4f(r, g, b, a);
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
			
			// Clear spriteData
//			currentSpriteData.clear();
		}
	}

	@Override
	public void drawBitmap(int imageId, float x, float y, Paint paint) {
		saveState();
		gl.glColor4f(255, 255, 255, paint.getAlpha() / 255f);
		translate(x, y);
		drawBitmap(imageId);
		restoreState();
	}

	@Override
	public void drawBitmap(int imageId, Rect srcRect, RectF dstRect,
			boolean convertFromPhysics) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBitmap(int imageId, RectF dstRect,
			boolean convertFromPhysics, Paint paint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBitmap(int imageId, RectF dstRect,
			boolean convertFromPhysics) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadImage(int imageId) {
		
		// Get bitmap
		Bitmap bitmap = ImageLoader.loadImage(imageId);
		
		
		IntBuffer t = IntBuffer.allocate(1);
		gl.glGenTextures(1, t);
		int texture_id = t.get(0);
		
		SpriteData texture = new SpriteData(Color.WHITE, texture_id);
		
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
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// Add dimensional info to spritedata
		texture.setDimensions(bitmap.getWidth(), bitmap.getHeight());
		texture.addSprite(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
		textures.append(imageId, texture);
//		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

	@Override
	public void recycleImage(int imageId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void translatePhysics(float posX, float posY) {
		// TODO Auto-generated method stub
		
	}

}
