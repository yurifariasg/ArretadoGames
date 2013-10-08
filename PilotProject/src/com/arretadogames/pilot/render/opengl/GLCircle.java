package com.arretadogames.pilot.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.opengl.GLES11;

public class GLCircle {
	
	private static final int DEFAULT_NUM_SEGMENTS = 100; // Minimum: 3 (Triangle)
	
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private int numSegments;
    
    public GLCircle(float radius) {
    	this(radius, DEFAULT_NUM_SEGMENTS);
    }
    
    public GLCircle() {
    	this(1, DEFAULT_NUM_SEGMENTS);
    }
    
    public GLCircle(float radius, int segments) {
    	numSegments = segments;
		
		// Creating Vertices
		short indices[] = new short[(numSegments * 3)];
		int indicesIndex = 0;
		float vertices[] = new float[(numSegments + 1) * 3];
	    int verticesIndex = 0;
	    
    	// Center of the Circle
    	vertices[verticesIndex++] = 0; // x
    	vertices[verticesIndex++] = 0; // y
    	vertices[verticesIndex++] = 0; // z
	    
	    for (int i = 0 ; i < numSegments ; i++) {
			double theta = 2 * Math.PI * i / numSegments;
	    	vertices[verticesIndex++] = (float) (radius * Math.cos(theta));
	    	vertices[verticesIndex++] = (float) (radius * Math.sin(theta));
	    	vertices[verticesIndex++] = 0; // z
	    }
	    
	    // Triangulating
	    for (int i = 2 ; i < segments + 1 ; i++) { // Adding a +1 because 0 vertex is the center
	    	indices[indicesIndex++] = 0; // Center
	    	indices[indicesIndex++] = (short) i; // Current Segment Vertex
	    	indices[indicesIndex++] = (short) (i - 1); // Previous Segment Vertex
	    }
	    
	    // Last Triangle
	    indices[indicesIndex++] = 0; // Center
	    indices[indicesIndex++] = 1; // First
	    indices[indicesIndex++] = (short) (Math.floor(verticesIndex / 3.0) - 1); // Last Circle Segment
		
	    // Creating Buffers
        ByteBuffer vbb  = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
		createIndicesBuffer(indices);
		
		indexBuffer.position(0);
        vertexBuffer.position(0);
    }
    
    // default draw
	public void drawCircle(GLCanvas gl, float x, float y, int color, boolean filled) {
		drawCircle(gl, x, y, color, filled, 0, numSegments);
	}
    
	// percent: 0 to 100
    public void drawCircle(GLCanvas gl, float x, float y, int color, boolean filled, int percent) {
    	drawCircle(gl, x, y, color, filled, 0, percent * numSegments / 100);
    }
    
    // fromsegment - starting segment to draw
    // tosegment - last segment to draw
    public void drawCircle(GLCanvas gl, float x, float y, int color, boolean filled, int fromSegment, int toSegment) {
    	if (fromSegment > toSegment || fromSegment < 0 || toSegment > numSegments)
    		throw new IllegalArgumentException("Error in segment parameter while drawing circle");
    	
    	indexBuffer.position(fromSegment * 3);
        vertexBuffer.position(0);
        
        gl.saveState();
        
        gl.translate(x, y);
        
        gl.rotate(-90); // Hack to start from top
		
		GLES11.glColor4f(Color.red(color) / 255f, Color.green(color) / 255f,
				Color.blue(color) / 255f, Color.alpha(color) / 255f);
        
        // Draw
        GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        GLES11.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer); // Sets Vertex Buffer
        GLES11.glDrawElements(GL10.GL_TRIANGLES, (toSegment - fromSegment) * 3, GL10.GL_UNSIGNED_SHORT, indexBuffer); // Sets Index Buffer
        GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        
        // Reset Color
        GLES11.glColor4f(1, 1, 1, 1);
        
        gl.restoreState();
    	
    }
	
	private void createIndicesBuffer(short[] indices) {
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

}
