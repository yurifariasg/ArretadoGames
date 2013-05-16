package com.arretadogames.pilot.render.opengl;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;

import com.arretadogames.pilot.render.GameCanvas;

public class OpenGLCanvas implements GameCanvas {

	@Override
	public void setPhysicsRatio(float newRatio) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean initiate() {
		// Clears the screen and depth buffer.
		GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scale(float sx, float sy, float px, float py) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotate(float degrees, float x, float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotatePhysics(float degrees, float x, float y) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restoreState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotate(float angle, Point point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBitmap(Bitmap bitmap, float x, float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBitmap(Bitmap bitmap, float x, float y, Paint paint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawText(String text, float x, float y, Paint p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fillScreen(float a, float r, float g, float b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawRect(Rect rect, int argb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBitmap(Bitmap bitmap, RectF dstRect,
			boolean convertFromPhysics, Paint paint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBitmap(Bitmap bitmap, RectF dstRect,
			boolean convertFromPhysics) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBitmap(Bitmap bitmap, Rect srcRect, RectF dstRect,
			boolean convertFromPhysics) {
		// TODO Auto-generated method stub
		
	}

}
