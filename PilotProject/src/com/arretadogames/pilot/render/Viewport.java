package com.arretadogames.pilot.render;

import org.jbox2d.common.Vec2;

import com.arretadogames.pilot.render.GameCamera.EarlyConstraints;
import com.arretadogames.pilot.render.GameCamera.LateConstraints;

public class Viewport{
	
	public Vec2 lowerBound, upperBound, translator;
	public float physicsRatio;

	private static Viewport currentViewport = new Viewport();
	private static Viewport targetViewport = new Viewport();
	
	private Viewport(){

		lowerBound = null;
		upperBound = null;
		translator = null;
		physicsRatio = 0;
	}
	
	private Viewport(Vec2 lowerBound, Vec2 upperBound, Vec2 translator, float physicsRatio){

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.translator = translator;
		this.physicsRatio = physicsRatio;
	}
	
	public Viewport(Viewport viewport){
		
		lowerBound = viewport.lowerBound;
		upperBound = viewport.upperBound;
		translator = viewport.translator;
		physicsRatio = viewport.physicsRatio;
	}
	
	public Viewport(EarlyConstraints earlyConstraints, LateConstraints lateConstraints){

		lowerBound = new Vec2(earlyConstraints.center.x - lateConstraints.viewportWidth/2,
				earlyConstraints.center.y - lateConstraints.viewportHeight/2);
		
		upperBound = new Vec2(earlyConstraints.center.x + lateConstraints.viewportWidth/2,
				earlyConstraints.center.y + lateConstraints.viewportHeight/2);

		translator = new Vec2( -lateConstraints.physicsRatio * (earlyConstraints.center.x - lateConstraints.viewportWidth/2),
				lateConstraints.physicsRatio * (earlyConstraints.center.y - lateConstraints.viewportHeight/2) );

		physicsRatio = lateConstraints.physicsRatio;

		if ( TransitionManager.transitionWasNotHappening() ){
			currentViewport = new Viewport(lowerBound, upperBound, translator, physicsRatio);
		}
		else{
			targetViewport = new Viewport(lowerBound, upperBound, translator, physicsRatio);
		}

		if ( currentViewport.isNull() ){
			currentViewport = new Viewport(targetViewport);
		}
		else if ( targetViewport.isNull() ){
			targetViewport = new Viewport(currentViewport);
		}
		
		if ( TransitionManager.transitionWasHappening() ){
			
			if ( TransitionManager.transitionIsOver() ){

				currentViewport = new Viewport(targetViewport);

				lowerBound = new Vec2(currentViewport.lowerBound);
				upperBound = new Vec2(currentViewport.upperBound);
				translator = new Vec2(currentViewport.translator);
				physicsRatio = currentViewport.physicsRatio;

				targetViewport = new Viewport();

			}
			else{

				float reachedPercentage = TransitionManager.getTransitionPercentage();
				
				lowerBound = new Vec2(currentViewport.lowerBound);
				lowerBound.addLocal(targetViewport.lowerBound.sub(currentViewport.lowerBound).mul(reachedPercentage));

				upperBound = new Vec2(currentViewport.upperBound);
				upperBound.addLocal(targetViewport.upperBound.sub(currentViewport.upperBound).mul(reachedPercentage));

				translator = new Vec2(currentViewport.translator);
				translator.addLocal(targetViewport.translator.sub(currentViewport.translator).mul(reachedPercentage));

				physicsRatio = currentViewport.physicsRatio;
				physicsRatio += (targetViewport.physicsRatio - currentViewport.physicsRatio)*reachedPercentage;
			}
		}
	}

	public boolean isNull(){
		return lowerBound == null || upperBound == null || translator == null;
	}

	public static void resetTargetViewport() {
		targetViewport = new Viewport();
	}
	
}
