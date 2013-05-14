package com.arretadogames.pilot.render.canvas;

import org.jbox2d.common.Vec2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;

import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.render.GameCanvas;

/**
 * GameCanvas is the Canvas class which drawing operations on the Game will be
 * done
 */
public class RenderingCanvas implements GameCanvas {

	private SurfaceHolder surfaceHolder;
	private Canvas canvas;

	private Paint defaultPaint;
	private Paint debugPaint;
	private float physicsRatio;

	public RenderingCanvas(SurfaceHolder surfaceHolder) {
		this.surfaceHolder = surfaceHolder;

		defaultPaint = new Paint();
		defaultPaint.setAntiAlias(true);
		physicsRatio = 25f;

		debugPaint = new Paint();
		debugPaint.setColor(Color.RED);
	}

	/**
	 * Sets the new ratio used by Physics methods - this only works if the new
	 * ratio is higher than 0. Higher Ratio means more zoom
	 * 
	 * @param newRatio
	 *            New Ratio to be used - Must be higher than 0
	 */
	public void setPhysicsRatio(float newRatio) {
		if (newRatio > 0)
			this.physicsRatio = newRatio;
	}

	/**
	 * Initiates the GameCanvas
	 * 
	 * @return True - Initiate was successful<br>
	 *         False - Initiate failed
	 */
	public boolean initiate() {
		canvas = surfaceHolder.lockCanvas();
		if (canvas != null) {
			DisplaySettings.DISPLAY_WIDTH = canvas.getWidth(); // FIXME: Find a way of doing it only once
			DisplaySettings.DISPLAY_HEIGHT = canvas.getHeight();
			DisplaySettings.WIDTH_RATIO = DisplaySettings.DISPLAY_WIDTH / DisplaySettings.TARGET_WIDTH;
			DisplaySettings.HEIGHT_RATIO = DisplaySettings.DISPLAY_HEIGHT / DisplaySettings.TARGET_HEIGHT;
			
			canvas.scale(DisplaySettings.WIDTH_RATIO, DisplaySettings.HEIGHT_RATIO);
		}
		return canvas != null;
	}

	/**
	 * Flushes draws to screen
	 */
	public void flush() {
		surfaceHolder.unlockCanvasAndPost(canvas);
		canvas = null;
	}

	/**
	 * Translates the canvas dx x-coordinates and dy y-coordinates
	 * 
	 * @param dx
	 *            X Coordinates to Translate
	 * @param dy
	 *            Y Coordinates to Translate
	 */
	public void translate(float dx, float dy) {
		canvas.translate(dx, dy);
	}
	
	public void scale(float sx, float sy, float px, float py) {
		canvas.scale(sx, sy, px, py);
	}

	/**
	 * Rotates the canvas on the given point the amount of given degrees
	 * 
	 * @param degrees
	 *            Degrees to rotate
	 * @param x
	 *            X Coordinate of the point
	 * @param y
	 *            Y Coordinate of the point
	 */
	public void rotate(float degrees, float x, float y) {
		canvas.rotate(degrees, x, y);
	}

	/**
	 * Rotates the canvas on the given point the amount of given degrees. All X
	 * and Y coords are units given in meters
	 * 
	 * @param degrees
	 *            Degrees to rotate
	 * @param x
	 *            X Coordinate of the point
	 * @param y
	 *            Y Coordinate of the point
	 */
	public void rotatePhysics(float degrees, float x, float y) {
		canvas.rotate(degrees, x * physicsRatio, DisplaySettings.TARGET_HEIGHT - y * physicsRatio);
	}

	/**
	 * Draws a debugging rect at the given location
	 * 
	 * @param x
	 *            Top Left X Position
	 * @param y
	 *            Top Left Y Position
	 * @param x2
	 *            Bottom Right X Position
	 * @param y2
	 *            Bottom Right Y Position
	 */
	public void drawDebugRect(int x, int y, int x2, int y2) {
		canvas.drawRect(new Rect(x, y, x2, y2), debugPaint);
	}
	
	public void drawCameraDebugRect(float x, float y, float x2, float y2) {
		
		debugPaint.setColor(Color.GRAY);
		canvas.drawRect(new RectF(x*physicsRatio, DisplaySettings.DISPLAY_HEIGHT - y*physicsRatio,
				x2*physicsRatio, DisplaySettings.TARGET_HEIGHT - y2*physicsRatio), debugPaint);
	}

	public void drawPhysicsDebugRect(float centerX, float centerY,
			float sideLength) {
		drawPhysicsDebugRect(centerX, centerY, sideLength, Color.RED);
	}

	public void drawPhysicsDebugRect(float centerX, float centerY,
			float sideLength, int color) {
		sideLength *= physicsRatio;
		sideLength /= 2;
		debugPaint.setColor(color);
		canvas.drawRect(new Rect((int) ((centerX * physicsRatio - sideLength)),
				(int) (DisplaySettings.TARGET_HEIGHT - (centerY * physicsRatio + sideLength)),
				(int) ((centerX * physicsRatio + sideLength)),
				(int) (DisplaySettings.TARGET_HEIGHT - (centerY * physicsRatio - sideLength))),
				debugPaint);
	}
	
	private final static int BOTTOM_MAP = -100;
	
	public void drawPhysicsLines(Vec2[] lines) {
		
		// FIXME Can be optimized
		Path path = new Path();
		path.moveTo(lines[0].x * physicsRatio, DisplaySettings.TARGET_HEIGHT - lines[1].y * physicsRatio);
		for (int i = 1 ; i < lines.length ; i++) {
			path.lineTo(lines[i].x * physicsRatio, DisplaySettings.TARGET_HEIGHT - lines[i].y * physicsRatio);
		}
		
		path.lineTo(lines[lines.length - 1].x * physicsRatio, DisplaySettings.TARGET_HEIGHT - BOTTOM_MAP * physicsRatio);
		path.lineTo(lines[0].x * physicsRatio, DisplaySettings.TARGET_HEIGHT -  BOTTOM_MAP * physicsRatio);
		path.lineTo(lines[0].x * physicsRatio, DisplaySettings.TARGET_HEIGHT - lines[1].y * physicsRatio);
		

		debugPaint.setStyle(Style.FILL);
		int oldColor = debugPaint.getColor();
		debugPaint.setARGB(255, 124, 60, 3);
		canvas.drawPath(path, debugPaint);
		debugPaint.setColor(oldColor);
	}

	public void drawPhysicsLine(float x1, float y1, float x2, float y2) {
		debugPaint.setColor(Color.RED);
		canvas.drawLine(
				(int) (x1 * physicsRatio),
				(int) (DisplaySettings.TARGET_HEIGHT - y1
				* physicsRatio), (int) (x2 * physicsRatio),
				(int) (DisplaySettings.TARGET_HEIGHT - y2 * physicsRatio),
				debugPaint);
	}

	/**
	 * Saves the current state of the GameCanvas<br>
	 * This should be done before rotating or translating operations
	 */
	public void saveState() {
		canvas.save();
	}

	/**
	 * Restores the canvas to the last saved state<br>
	 * This should be done after performing rotating, translating and drawing
	 * operations
	 */
	public void restoreState() {
		canvas.restore();
	}

	/**
	 * Rotates the canvas on the given point by the given angle
	 * 
	 * @param angle
	 *            Rotate the amount of degrees
	 * @param point
	 *            Rotates on this given point
	 */
	public void rotate(float angle, Point point) {
		canvas.rotate(angle, point.x, point.y);
	}

	/**
	 * Draws the given bitmap on the given coordinates
	 * 
	 * @param bitmap
	 *            Bitmap to be drawn
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public void drawBitmap(Bitmap bitmap, float x, float y) {
		drawBitmap(bitmap, x, y, defaultPaint);
	}

	/**
	 * Draws the given bitmap on the given coordinates
	 * 
	 * @param bitmap
	 *            bitmap to be drawn
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param paint
	 *            paint to be used when drawing
	 * 
	 */
	public void drawBitmap(Bitmap bitmap, float x, float y, Paint paint) {
//		Rect rs = new Rect();
//		RectF rd = new RectF();
//		rs.left = rs.top = 0;
//		rs.right = bitmap.getWidth();
//		rs.bottom = bitmap.getHeight();
//		rd.left = x;
//		rd.top = y;
//		rd.right = x + bitmap.getWidth();
//		rd.bottom = y + bitmap.getHeight();
//		
//		saveState();
//		canvas.scale(originalCanvas.getWidth() / 800f, originalCanvas.getHeight() / 480f);
//		canvas.translate(x, y);
//		
//		canvas.drawBitmap(bitmap, 0, 0, paint);
//		
//		restoreState();
//		canvas.drawBitmap(bitmap, rs, rd, paint);
//		bitmap.setDensity(Bitmap.DENSITY_NONE);
		canvas.drawBitmap(bitmap, x, y, paint);
//		canvas.drawBitmap(bitmap, null, rd, paint);
	}
	
	
	public void drawText(String text, float x, float y, Paint p) {
		canvas.drawText(text, x, y, p);
	}
	
	public void fillScreen(float a, float r, float g, float b) {
		canvas.drawARGB((int) a, (int) r, (int) g, (int) b);
	}

	public void drawRect(Rect rect, int argb) {
		Paint p = new Paint();
		p.setColor(argb);
		canvas.drawRect(rect, p);
	}
}
