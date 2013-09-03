package com.arretadogames.pilot.render;

public class TransitionManager {

	private static boolean transitioning;
	private static float transitionDuration; //Measured in milliseconds.
	private static long startTime;
	private static float lastReachedPercentage = -1;
	
	public static void startTransition(){
		transitioning = true;
		transitionDuration = 250f;
		startTime = getCurrentTime();
	}
	
	public static boolean transitionWasHappening(){
		return transitioning == true;
	}
	
	public static boolean transitionWasNotHappening(){
		return transitioning == false;
	}

	public static boolean transitionIsOver(){
		float elapsedTime = getCurrentTime() - startTime;
		float reachedPercentage = elapsedTime / transitionDuration;
		
		if ( reachedPercentage >= 1 ){
			transitioning = false;
			startTime = 0;
			lastReachedPercentage = reachedPercentage;
			return true;
		}
		else{
			lastReachedPercentage = reachedPercentage;
			return false;
		}
	}
	
	public static float getTransitionPercentage(){
		if ( lastReachedPercentage == -1 ){
			transitionIsOver();
		}
		float percentage = lastReachedPercentage;
		lastReachedPercentage = -1;
		return percentage;
	}
	
	private static long getCurrentTime() {

		return System.nanoTime()/1000000;
	}
}
