package com.arretadogames.pilot.weathers;


public class WeatherManager {
    
    private static WeatherManager instance;
    
    private Weather mWeather;
    
    public static WeatherManager getInstance() {
        if (instance == null)
            instance = new WeatherManager();
        return instance;
    }
    
    private WeatherManager() {
    }
    
    @SuppressWarnings("unused")
	private Weather getWeather(WeatherKind weather){
    	if (mWeather == null){
	    	switch(weather){
	    	case STORM:
	    		mWeather = new Storm();
	    		break;
	    	case FOG:
	    		mWeather = new Fog();
	    		break;
	    	case DARK:
	    		mWeather = new Dark();
	    		break;
	    	case SUNNY:
	    		mWeather = new Sunny();
	    		break;
	    	case WINDY:
	    		mWeather = new Windy();
	    		break;
	    	}
    	}else{
    		mWeather.setWeatherKind(weather);
	    }
    	return mWeather;
    }
    
}