package com.arretadogames.pilot.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Rect;
import android.opengl.GLES11;

public class GLTexturedRect extends GLRect {
	
    private static FloatBuffer textureBuffer;
    
    private static float[] textureCoords = new float[8];

	private static GLTexture image;
    
    private static void draw(GL10 gl){
    	
    	// DRAW COMMAND
		GLES11.glEnable(GL10.GL_TEXTURE_2D);
		GLES11.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		// Tell OpenGL where our texture is located.
		GLES11.glBindTexture(GL10.GL_TEXTURE_2D, image.getTextureID());
		// Telling OpenGL where our textureCoords are.
		GLES11.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		// Specifies the location and data format of the array of
		// vertex
		// coordinates to use when rendering.
		GLES11.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		// Draw elements command using indices so it knows which
		// vertices go together to form each element
		GLES11.glDrawElements(GL10.GL_TRIANGLES, INDICES.length,
				GL10.GL_UNSIGNED_SHORT, indexBuffer);
		
		// 0,0 ---
		/*
		 * vertices[0] = left;
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
		 */

		GLES11.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		GLES11.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDisable(GL10.GL_TEXTURE_2D);
    	
    }
    
    private static void fillTextureCoordArray() {
    	textureCoords[0] = 0;
		textureCoords[1] = 0;
		textureCoords[2] = 0;
		textureCoords[3] = 1;
		textureCoords[4] = 1;
		textureCoords[5] = 1;
		textureCoords[6] = 1;
		textureCoords[7] = 0;
    }
    
    private static void fillTextureCoordArray(float left, float top, float right, float bottom) {
    	textureCoords[0] = (left) / image.getTextureWidth();
		textureCoords[1] = (top) / image.getTextureHeight();
		
		textureCoords[2] = (left) / image.getTextureWidth();
		textureCoords[3] = (bottom) / image.getTextureHeight();
		
		textureCoords[4] = (right) / image.getTextureWidth();
		textureCoords[5] = (bottom) / image.getTextureHeight();
		
		textureCoords[6] = (right) / image.getTextureWidth();
		textureCoords[7] = (top) / image.getTextureHeight();
	}
    
    public static void draw(GL10 gl,
    		float dstLeft, float dstTop, float dstRight, float dstBottom,
    		GLTexture image) {
    	draw(gl,
			0, 0, image.getTextureWidth(), image.getTextureHeight(),
			dstLeft, dstTop, dstRight, dstBottom,
			image);
    }
    
    
    public static void draw(GL10 gl,
    		float srcLeft, float srcTop, float srcRight, float srcBottom,
    		float dstLeft, float dstTop, float dstRight, float dstBottom,
    		GLTexture image) {
    	
    	if (indexBuffer == null)
			createIndicesBuffer();
		
		GLTexturedRect.image = image;
		fillVertices(dstLeft, dstTop, dstRight, dstBottom);
		
		if (vertexBuffer == null) {
	        ByteBuffer vbb  = ByteBuffer.allocateDirect(vertices.length * 4);
	        vbb.order(ByteOrder.nativeOrder());
	        vertexBuffer = vbb.asFloatBuffer();
		}
		
		if (textureBuffer == null) {
			ByteBuffer vbb  = ByteBuffer.allocateDirect(textureCoords.length * 4);
	        vbb.order(ByteOrder.nativeOrder());
	        textureBuffer = vbb.asFloatBuffer();
		}
		
		fillTextureCoordArray(srcLeft, srcTop, srcRight, srcBottom);
		
		textureBuffer.clear();
		textureBuffer.put(textureCoords);
		textureBuffer.position(0);
		
		vertexBuffer.clear();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        draw(gl);
    }
    
	public static void draw(GL10 gl, Rect src, Rect dst, GLTexture image) {
		draw(gl, src.left, src.top, src.right, src.bottom, dst.left, dst.top, dst.right, dst.bottom, image);
	}

	public static void draw(GL10 gl, int x, int y, int width, int height, GLTexture image) {
		if (indexBuffer == null)
			createIndicesBuffer();

		GLTexturedRect.image = image;
		fillVertices(x, y, x + width, y + height);
		
		if (vertexBuffer == null) {
	        ByteBuffer vbb  = ByteBuffer.allocateDirect(vertices.length * 4);
	        vbb.order(ByteOrder.nativeOrder());
	        vertexBuffer = vbb.asFloatBuffer();
		}
		
		if (textureBuffer == null) {
			ByteBuffer vbb  = ByteBuffer.allocateDirect(textureCoords.length * 4);
	        vbb.order(ByteOrder.nativeOrder());
	        textureBuffer = vbb.asFloatBuffer();
	        fillTextureCoordArray();
		}
        fillTextureCoordArray();
        
		textureBuffer.clear();
		textureBuffer.put(textureCoords);
		textureBuffer.position(0);
		
		vertexBuffer.clear();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        
        draw(gl);
	}
}
