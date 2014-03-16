
package com.arretadogames.pilot.render;

import android.graphics.Rect;

/**
 * Sprite Class<br>
 * A single frame inside a sprite sheet (this means a unique image) Sprite has
 * properties indicating where the sprite is located (x, y, width and height)
 * And also the time it should take inside a animation
 */
public class Sprite {

    private final int sourceSheet;
    private final Rect frameRect;
    private final float time;

    public Sprite(int sourceSheetRes, int x, int y, int width, int height, float time) {
        this.sourceSheet = sourceSheetRes;
        this.frameRect = new Rect(x, y, x + width, y + height);
        this.time = time;
    }

    public final Rect getFrameRect() {
        return frameRect;
    }

    public float getTime() {
        return time;
    }

    public int getSourceSheet() {
        return sourceSheet;
    }
}
