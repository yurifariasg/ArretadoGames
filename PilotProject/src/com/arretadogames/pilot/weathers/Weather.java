package com.arretadogames.pilot.weathers;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public abstract class Weather implements Renderable, Steppable{
	
	private WeatherKind weatherKind;
	
	public void setWeatherKind(WeatherKind wk){
		this.weatherKind = wk;
	}
	
	public WeatherKind getWeatherKind(){
		return this.weatherKind;
	}
	
    /**
     * Gets a dimension from the resources
     *
     * @param resId The resource id
     * @return Dimension
     */
    public float getDimension(int resId) {
        return MainActivity.getContext().getResources().getDimension(resId);
    }

	@Override
	public abstract void render(GLCanvas canvas, float timeElapsed);

	@Override
	public abstract void step(float timeElapsed);
	
	/**
	 * All kind of weather specifies your own background image.
	 * @param gameCanvas
	 */
	public abstract void drawBackground(GLCanvas gameCanvas);

}
