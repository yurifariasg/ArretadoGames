
package com.arretadogames.pilot.render;

import android.graphics.RectF;

import com.arretadogames.pilot.render.opengl.GLCanvas;

/**
 * Animation class<br>
 * A container to hold several sprites that together makes an Animation, all
 * sprites have a time that the frame lasts. This class is also a manager to
 * draw frames based on time.
 */
public class Animation {

    private static RectF auxRect = new RectF();

    private String name;
    private Sprite[] sprites;
    private boolean repeat;
    
    private float animationRate = 1;
    private int currentKeyFrameIndex = -1;
    private float currentFrameTimeLeft = 0;

    public Animation(String name, Sprite[] sprites) {
        this.name = name;
        this.currentKeyFrameIndex = 0;
        this.sprites = sprites;
        this.currentFrameTimeLeft = sprites[currentKeyFrameIndex].getTime();
        this.repeat = true; // Default
    }
    
    public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
    
    public boolean isRepeatable() {
    	return this.repeat;
    }
    
    public String getName() {
        return this.name;
    }

    public void render(GLCanvas canvas, PhysicsRect phyRect, float timeElapsed) {
        // Prepare rect to draw
        auxRect.left = phyRect.left * GLCanvas.physicsRatio;
        auxRect.top = phyRect.top * GLCanvas.physicsRatio;
        auxRect.right = phyRect.right * GLCanvas.physicsRatio;
        auxRect.bottom = phyRect.bottom * GLCanvas.physicsRatio;
        
        render(canvas, auxRect, timeElapsed);
    }
    
    public void render(GLCanvas canvas, RectF rect, float timeElapsed) {
        // Figure out which frame is currently selected
        if (sprites[currentKeyFrameIndex].getTime() > 0) {
            float currentTimeElapsed = timeElapsed * animationRate;
            while (currentTimeElapsed > 0) {
                float timeElapsedBefore = currentTimeElapsed;
                currentTimeElapsed -= currentFrameTimeLeft;
                currentFrameTimeLeft -= timeElapsedBefore;
                if (currentFrameTimeLeft < 0) { // Frame Expired
                    nextFrame();
                    if (currentFrameTimeLeft == 0)
                        break; // if frame is suppose to be infinite
                }
            }
        }
        
        // Draw
        canvas.drawBitmap(sprites[currentKeyFrameIndex].getSourceSheet(),
                sprites[currentKeyFrameIndex].getFrameRect(),
                rect);
    }

    public void resetIfInfinite() {
        if (currentFrameTimeLeft == 0) {
            this.currentKeyFrameIndex = 0;
            this.currentFrameTimeLeft = sprites[currentKeyFrameIndex].getTime();
        }
    }

    private void nextFrame() {
        currentKeyFrameIndex++;
        if (currentKeyFrameIndex >= sprites.length) {
        	if (repeat) {
                currentKeyFrameIndex = 0;
                currentFrameTimeLeft = sprites[currentKeyFrameIndex].getTime();
        	} else {
        		currentFrameTimeLeft = 0; // Keep infinite time
        		currentKeyFrameIndex = sprites.length - 1; // The last frame
        	}
        } else {
            currentFrameTimeLeft = sprites[currentKeyFrameIndex].getTime();
        }
    }

    public void setAnimationRate(float multiplier) {
        animationRate = multiplier;
    }

}
