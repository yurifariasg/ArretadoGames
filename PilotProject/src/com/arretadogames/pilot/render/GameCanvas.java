package com.arretadogames.pilot.render;

import org.jbox2d.common.Vec2;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * GameCanvas is the Canvas class which drawing operations on the Game will be
 * done
 */
public interface GameCanvas {

	/**
	 * Sets the new ratio used by Physics methods - this only works if the new
	 * ratio is higher than 0. Higher Ratio means more zoom
	 * 
	 * @param newRatio
	 *            New Ratio to be used - Must be higher than 0
	 */
	public void setPhysicsRatio(float newRatio);

	/**
	 * Initiates the GameCanvas
	 * 
	 * @return True - Initiate was successful<br>
	 *         False - Initiate failed
	 */
	public boolean initiate();

	/**
	 * Flushes draws to screen
	 */
	public void flush();

	/**
	 * Translates the canvas dx x-coordinates and dy y-coordinates
	 * 
	 * @param dx
	 *            X Coordinates to Translate
	 * @param dy
	 *            Y Coordinates to Translate
	 */
	public void translate(float dx, float dy);
	
	public void scale(float sx, float sy, float px, float py);

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
	public void rotate(float degrees, float x, float y);

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
	public void rotatePhysics(float degrees, float x, float y);

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
	public void drawDebugRect(int x, int y, int x2, int y2);
	
	public void drawCameraDebugRect(float x, float y, float x2, float y2);

	public void drawPhysicsDebugRect(float centerX, float centerY,
			float sideLength);

	public void drawPhysicsDebugRect(float centerX, float centerY,
			float sideLength, int color);
	
	public void drawPhysicsLines(Vec2[] lines);

	public void drawPhysicsLine(float x1, float y1, float x2, float y2);

	/**
	 * Saves the current state of the GameCanvas<br>
	 * This should be done before rotating or translating operations
	 */
	public void saveState();

	/**
	 * Restores the canvas to the last saved state<br>
	 * This should be done after performing rotating, translating and drawing
	 * operations
	 */
	public void restoreState();

	/**
	 * Rotates the canvas on the given point by the given angle
	 * 
	 * @param angle
	 *            Rotate the amount of degrees
	 * @param point
	 *            Rotates on this given point
	 */
	public void rotate(float angle, Point point);

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
	public void drawBitmap(Bitmap bitmap, float x, float y);

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
	public void drawBitmap(Bitmap bitmap, float x, float y, Paint paint) ;
	
	public void drawBitmap(Bitmap bitmap, Rect srcRect, RectF dstRect, boolean convertFromPhysics) ;

	public void drawBitmap(Bitmap bitmap, RectF dstRect, boolean convertFromPhysics, Paint paint) ;
	
	public void drawBitmap(Bitmap bitmap, RectF dstRect, boolean convertFromPhysics);
	
	public void drawText(String text, float x, float y, Paint p);
	
	public void fillScreen(float a, float r, float g, float b);

	public void drawRect(Rect rect, int argb);
}
