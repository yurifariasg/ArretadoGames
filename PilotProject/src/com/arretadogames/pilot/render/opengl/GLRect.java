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
    protected static float colors[] = new float[16];

    protected static FloatBuffer vertexBuffer;
    protected static ShortBuffer indexBuffer;
    protected static FloatBuffer colorBuffer;
    
    protected static void createColorBuffer() {
    	colorBuffer = ByteBuffer.allocateDirect(colors.length * 4)
    			.order(ByteOrder.nativeOrder()).asFloatBuffer();
    }
    
    protected static void createIndicesBuffer() {
        ByteBuffer ibb = ByteBuffer.allocateDirect(INDICES.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(INDICES);
        indexBuffer.position(0);
    }

    private static void draw(GL10 gl){
        GLES11.glEnableClientState(GLES11.GL_COLOR_ARRAY);
        GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        GLES11.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
        GLES11.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer); // Sets Vertex Buffer
        GLES11.glDrawElements(GL10.GL_TRIANGLES, INDICES.length, GL10.GL_UNSIGNED_SHORT, indexBuffer); // Sets Index Buffer
        GLES11.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        GLES11.glDisableClientState(GLES11.GL_COLOR_ARRAY);
    }
    public static void draw (GL10 gl,
    		float x, float y,
    		float x2, float y2,
    		float x3, float y3,
    		float x4, float y4,
    		int colorV1, int colorV2,
    		int colorV3, int colorV4) {
    	
    	if (colorBuffer == null)
    		createColorBuffer();
    	
    	if (indexBuffer == null)
			createIndicesBuffer();
    	
    	fillColor(colorV1, colorV2, colorV3, colorV4);
		
    	fillVertices(x, y, x2, y2, x3, y3, x4, y4);
		
		if (vertexBuffer == null) {
	        ByteBuffer vbb  = ByteBuffer.allocateDirect(vertices.length * 4);
	        vbb.order(ByteOrder.nativeOrder());
	        vertexBuffer = vbb.asFloatBuffer();
		}
		
		colorBuffer.clear();
		colorBuffer.put(colors);
		colorBuffer.position(0);
		
		vertexBuffer.clear();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

//		GLES11.glColor4f(Color.red(Color.WHITE) / 255f, Color.green(Color.WHITE) / 255f,
//				Color.blue(Color.WHITE) / 255f, Color.alpha(Color.WHITE) / 255f);
        
        draw(gl);
        GLES11.glColor4f(1, 1, 1, 1);
    	
    }
    
    protected static void fillColor(int colorV1, int colorV2, int colorV3,
			int colorV4) {
    	
    	//V1
    	colors[0] = Color.red(colorV1) / 255f;
    	colors[1] = Color.green(colorV1) / 255f;
    	colors[2] = Color.blue(colorV1) / 255f;
    	colors[3] = Color.alpha(colorV1) / 255f;
    	//V2
    	colors[4] = Color.red(colorV2) / 255f;
    	colors[5] = Color.green(colorV2) / 255f;
    	colors[6] = Color.blue(colorV2) / 255f;
    	colors[7] = Color.alpha(colorV2) / 255f;
    	//V3
    	colors[8] = Color.red(colorV3) / 255f;
    	colors[9] = Color.green(colorV3) / 255f;
    	colors[10] = Color.blue(colorV3) / 255f;
    	colors[11] = Color.alpha(colorV3) / 255f;
    	//V4
    	colors[12] = Color.red(colorV4) / 255f;
    	colors[13] = Color.green(colorV4) / 255f;
    	colors[14] = Color.blue(colorV4) / 255f;
    	colors[15] = Color.alpha(colorV4) / 255f;
		
	}

	public static void draw (GL10 gl, float x, float y, float x2, float y2, float x3, float y3, float x4, float y4, int color) {
    	draw(gl, x, y, x2, y2, x3, y3, x4, y4, color, color, color, color);
    	
    	if (indexBuffer == null)
			createIndicesBuffer();
		
    	fillVertices(x, y, x2, y2, x3, y3, x4, y4);
		
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
	
	public static void draw(GL10 gl, float left, float top, float right, float bottom, int color) {
		draw(gl, left, top, left, bottom, right, bottom, right, top, color);
	}
	
	/*
	 * Fill array for points in the following order:
	 * TopLeft, BottomLeft, BottomRight, TopRight
	 */
	
	protected static void fillVertices(
			float x, float y,
			float x2, float y2,
			float x3, float y3,
			float x4, float y4) {
		vertices[0] = x;
		vertices[1] = y;
		vertices[2] = 0.0001f;
		vertices[3] = x2;
		vertices[4] = y2;
		vertices[5] = 0.0001f;
		vertices[6] = x3;
		vertices[7] = y3;
		vertices[8] = 0.0001f;
		vertices[9] = x4;
		vertices[10] = y4;
		vertices[11] = 0.0001f;
		
	}
	


	protected static void fillVertices(float left, float top, float right, float bottom) {
		fillVertices(left, top, left, bottom, right, bottom, right, top);
	}
}
