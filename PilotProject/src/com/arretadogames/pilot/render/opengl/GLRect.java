package com.arretadogames.pilot.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Rect;
import android.graphics.RectF;

public class GLRect {

    private short[] indices = {0,1,2,0,2,3};
    
	private float vertices[]={
	        -1.0f, 1.0f, 0.0f,
	        -1.0f,-1.0f,0.0f,
	        1.0f,-1.0f,0.0f,
	        1.0f,1.0f,0.0f
	    };

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;

    private GLRect(float left, float top, float right, float bottom){
    	
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
		
        ByteBuffer vbb  = ByteBuffer.allocateDirect(this.vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

    public void draw(GL10 gl){
//        gl.glFrontFace(GL10.GL_CCW);
//        gl.glEnable(GL10.GL_CULL_FACE);
//        gl.glCullFace(GL10.GL_BACK);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//        gl.glDisable(GL10.GL_CULL_FACE);
    }
	
	
	public static GLRect create(Rect rect) {
		return create(rect.left, rect.top, rect.right, rect.bottom);
	}
	
	public static GLRect create(RectF rect) {
		return create(rect.left, rect.top, rect.right, rect.bottom);
	}
	
	private static GLRect create(float left, float top, float right, float bottom) {
		return new GLRect(left, top, right, bottom);
	}

}
