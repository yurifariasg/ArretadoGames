package com.arretadogames.pilot.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import com.arretadogames.pilot.config.GameSettings;

import android.graphics.Color;
import android.opengl.GLES11;

public class GLLine {
	
    protected static FloatBuffer vertexBuffer;
    protected static ShortBuffer indexBuffer;
    
    public static void drawLineStrip(Vec2[] vecs, int count, float width, int color, boolean connectEndAndStart, float multiplier) {
    	drawLineStrip(vecs, count, width, color, connectEndAndStart, multiplier, false);
    }

	public static void drawLineStrip(Vec2[] vecs, int count, float width, int color, boolean connectEndAndStart, float multiplier, boolean invertY) {
		if (indexBuffer == null || indexBuffer.capacity() < count) {
			createIndicesBuffer(count);
		}
		indexBuffer.position(0);
		if (vertexBuffer == null || vertexBuffer.capacity() < count * 3) {
			createVertexBuffer(count * 3);
		}
		
		// Fill VertexBuffer
		vertexBuffer.position(0);
		for (int i = 0 ; i < count ; i++) {
			vertexBuffer.put(vecs[i].x * multiplier);
			
			if (invertY) // TODO: This hack should be removed when we invert OpenGL coordinates
				vertexBuffer.put(GameSettings.TARGET_HEIGHT - vecs[i].y * multiplier);
			else
				vertexBuffer.put(vecs[i].y * multiplier);
			
			vertexBuffer.put(0);
			
		}
		
		// Resets position
		vertexBuffer.position(0);
		
		GLES11.glColor4f(Color.red(color) / 255f, Color.green(color) / 255f,
				Color.blue(color) / 255f, Color.alpha(color) / 255f);
        
		GLES11.glLineWidth(width);
		
        // Draw
        GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        GLES11.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer); // Sets Vertex Buffer
        GLES11.glDrawElements(connectEndAndStart ? GL10.GL_LINE_LOOP : GL10.GL_LINE_STRIP,
        		count, GL10.GL_UNSIGNED_SHORT, indexBuffer); // Sets Index Buffer
        GLES11.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        
        // Reset Color
        GLES11.glColor4f(1, 1, 1, 1);
	}

	private static void createVertexBuffer(int length) {
		ByteBuffer vbb  = ByteBuffer.allocateDirect(length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
	}
	
	protected static void createIndicesBuffer(int length) {
        ByteBuffer ibb = ByteBuffer.allocateDirect(length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        for (short i = 0 ; i < length ; i++)
        	indexBuffer.put(i);
        indexBuffer.position(0);
    }

	public static void drawLineStrip(FloatBuffer vBuffer, int count, int lineWidth, int color, boolean loop) {
		if (indexBuffer == null || indexBuffer.capacity() < count) {
			createIndicesBuffer(count);
		}
		indexBuffer.position(0);
		
		GLES11.glColor4f(Color.red(color) / 255f, Color.green(color) / 255f,
				Color.blue(color) / 255f, Color.alpha(color) / 255f);
        
		GLES11.glLineWidth(lineWidth);
		
        // Draw
        GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        GLES11.glEnable(GL10.GL_LINE_SMOOTH);
        GLES11.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
        GLES11.glVertexPointer(3, GL10.GL_FLOAT, 0, vBuffer); // Sets Vertex Buffer
        GLES11.glDrawElements(loop ? GL10.GL_LINE_LOOP : GL10.GL_LINE_STRIP,
        		count + (loop ? 0 : 1), // plus one to get the next vertex for strip
        		GL10.GL_UNSIGNED_SHORT, indexBuffer); // Sets Index Buffer
        GLES11.glDisable(GL10.GL_LINE_SMOOTH);
        GLES11.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        
        // Reset Color
        GLES11.glColor4f(1, 1, 1, 1);
	}

}
