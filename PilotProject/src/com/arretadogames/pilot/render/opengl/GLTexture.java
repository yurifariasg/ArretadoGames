package com.arretadogames.pilot.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Rect;

public class GLTexture extends GLRect {
	
    private static FloatBuffer textureBuffer;
    
    private static float[] textureCoords = new float[8];

	private static GLImage image;
    
    private static void draw(GL10 gl){
    	
    	// DRAW COMMAND
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		// Tell OpenGL where our texture is located.
		gl.glBindTexture(GL10.GL_TEXTURE_2D, image.getTextureID());
		// Telling OpenGL where our textureCoords are.
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		// Specifies the location and data format of the array of
		// vertex
		// coordinates to use when rendering.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		// Draw elements command using indices so it knows which
		// vertices go together to form each element
		gl.glDrawElements(GL10.GL_TRIANGLES, INDICES.length,
				GL10.GL_UNSIGNED_SHORT, indexBuffer);
	
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
    	
    }
    
    private static void createTextureCoord(Rect src) {
    	
    	if (src == null) {
    		
    		textureCoords[0] = 0;
    		textureCoords[1] = 0;
    		textureCoords[2] = 0;
    		textureCoords[3] = 1;
    		textureCoords[4] = 1;
    		textureCoords[5] = 1;
    		textureCoords[6] = 1;
    		textureCoords[7] = 0;

    	} else {

    		textureCoords[0] = (src.left + 0.5f) / image.getTextureWidth();
    		textureCoords[1] = (src.top + 0.5f) / image.getTextureHeight();
    		textureCoords[2] = (src.left + 0.5f) / image.getTextureWidth();
    		textureCoords[3] = (src.bottom - 0.5f) / image.getTextureHeight();
    		textureCoords[4] = (src.right - 0.5f) / image.getTextureWidth();
    		textureCoords[5] = (src.bottom - 0.5f) / image.getTextureHeight();
    		textureCoords[6] = (src.right - 0.5f) / image.getTextureWidth();
    		textureCoords[7] = (src.top + 0.5f) / image.getTextureHeight();
    		
    	}
	}
	
	
	public static void draw(GL10 gl, Rect src, Rect dst, GLImage image) {
		if (indexBuffer == null)
			createIndicesBuffer();
		
		GLTexture.image = image;
		fillVertices(dst.left, dst.top, dst.right, dst.bottom);
		
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
		
        createTextureCoord(src);
		
		textureBuffer.clear();
		textureBuffer.put(textureCoords);
		textureBuffer.position(0);
		
		vertexBuffer.clear();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        draw(gl);
	}

	public static void draw(GL10 gl, int x, int y, int width, int height, int color, GLImage image) {
		if (indexBuffer == null)
			createIndicesBuffer();

		GLTexture.image = image;
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
	        createTextureCoord(null);
		}
        createTextureCoord(null);
		
		textureBuffer.clear();
		textureBuffer.put(textureCoords);
		textureBuffer.position(0);
		
		vertexBuffer.clear();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        
        
//        gl.glColor4f(Color.red(color) / 255f, Color.green(color) / 255f,
//				Color.blue(color) / 255f, Color.alpha(color) / 255f);
        draw(gl);
        gl.glColor4f(1, 1, 1, 1);
	}
}
