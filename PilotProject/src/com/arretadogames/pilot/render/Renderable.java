package com.arretadogames.pilot.render;

import com.arretadogames.pilot.render.opengl.GLCanvas;

public interface Renderable {

	public void render(GLCanvas canvas, float timeElapsed);

}
