package com.arretadogames.pilot.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.opengl.GLES11;

public class GLRect {

    protected static final short[] INDICES = {0,1,2,0,2,3};
    
    protected static float vertices[] = new float[12];

    protected static FloatBuffer vertexBuffer;
    protected static ShortBuffer indexBuffer;
    
    protected static void createIndicesBuffer() {
        ByteBuffer ibb = ByteBuffer.allocateDirect(INDICES.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(INDICES);
        indexBuffer.position(0);
    }

    private static void draw(GL10 gl){
        GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        GLES11.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer); // Sets Vertex Buffer
        GLES11.glDrawElements(GL10.GL_TRIANGLES, INDICES.length, GL10.GL_UNSIGNED_SHORT, indexBuffer); // Sets Index Buffer
        GLES11.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
	
	public static void draw(GL10 gl, float left, float top, float right, float bottom, int color) {
		if (indexBuffer == null)
			createIndicesBuffer();
		
		fillVertices(left, top, right, bottom);
		
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
        draw(gl);
        GLES11.glColor4f(1, 1, 1, 1);
	}

	protected static void fillVertices(float left, float top, float right, float bottom) {
		vertices[0] = left;
		vertices[1] = top;
		vertices[2] = 0f;
		vertices[3] = left;
		vertices[4] = bottom;
		vertices[5] = 0f;
		vertices[6] = right;
		vertices[7] = bottom;
		vertices[8] = 0f;
		vertices[9] = right;
		vertices[10] = top;
		vertices[11] = 0f;
	}
}
