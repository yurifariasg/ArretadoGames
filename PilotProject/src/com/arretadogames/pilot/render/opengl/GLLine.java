package com.arretadogames.pilot.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import android.graphics.Color;
import android.opengl.GLES11;

public class GLLine {
	
    protected static FloatBuffer vertexBuffer;
    protected static ShortBuffer indexBuffer;

	public static void draw(float x, float y, float x2, float y2, float width, int color) {
		drawLine(x, y, x2, y2, width, color);
	}
	
	private static void drawLine(float x, float y, float x2, float y2, float width, int color) {
		
//		Algorithm:
//		vec2  p1(x1, y1);
//		vec2  p2(x2, y2);
//		vec2  v = p2 - p1;
//		v /= v.length();  // make it a unit vector
//		vec2  vp(-v.y, v.x);  // compute the vector perpendicular to v
//		vec2  v[4];
//		v[0] = p1 + W/2 * vp;
//		v[1] = p1 - W/2 * vp;
//		v[2] = p2 + W/2 * vp;
//		v[3] = p2 - W/2 * vp;    
//		// Load the v[] array into a vertex-buffer object
//		Order: { 0, 1, 2, 3, 3, 4, 5, 6, 7 }
		
		Vec2 v = new Vec2(x, y);
		Vec2 v2 = new Vec2(x2, y2);
		Vec2 v3 = v2.add(v.negate());
		v3.normalize();
		
		Vec2 perpendicular = new Vec2(-v3.y, v3.x);
		perpendicular.mulLocal(width/2);
		
		Vec2[] quad = new Vec2[4];
		quad[0] = v.add(perpendicular);
		quad[1] = v.sub(perpendicular);
		quad[2] = v2.add(perpendicular);
		quad[3] = v2.sub(perpendicular);
		
		if (indexBuffer == null) {
			createIndicesBuffer(new short[] {1, 0, 2, 1, 2, 3});
		}
		indexBuffer.position(0);
		
	    float vertices[] = new float[12];
	    int verticesIndex = 0;
	    for (int i = 0 ; i < 4 ; i++) {
	    	vertices[verticesIndex++] = quad[i].x;
	    	vertices[verticesIndex++] = quad[i].y;
	    	vertices[verticesIndex++] = 0; // z
	    }
		
		if (vertexBuffer == null) {
	        ByteBuffer vbb  = ByteBuffer.allocateDirect(vertices.length * 4);
	        vbb.order(ByteOrder.nativeOrder());
	        vertexBuffer = vbb.asFloatBuffer();
		}
		
		vertexBuffer.clear();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        
		GLES11.glColor4f(Color.red(color) / 255f, Color.green(color) / 255f,
				Color.blue(color) / 255f, Color.alpha(color) / 255f);
        
        // Draw
        GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        GLES11.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer); // Sets Vertex Buffer
        GLES11.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, indexBuffer); // Sets Index Buffer
        GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        
        // Reset Color
        GLES11.glColor4f(1, 1, 1, 1);
		
	}
	
	protected static void createIndicesBuffer(short[] indices) {
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

}
