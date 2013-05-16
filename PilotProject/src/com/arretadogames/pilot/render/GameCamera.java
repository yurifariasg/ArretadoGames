package com.arretadogames.pilot.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.world.GameWorld;
public class GameCamera {

	private static GameWorld gameWorld = null;
	private GameCanvas gameCanvas = null;
	private Bitmap background = null;
	
	private boolean calculateWidthFirst;
	private int currentNumberOfPlayers;

	private Vec2 currentLowerBound;
	private Vec2 currentUpperBound;
	private Vec2 currentTranslator;
	private float currentPhysicsRatio;

	private boolean transitioning;
	private float transitionDuration; //Measured in milliseconds.
	private long startTime;

	private Vec2 targetLowerBound;
	private Vec2 targetUpperBound;
	private Vec2 targetTranslator;
	private float targetPhysicsRatio;

	public GameCamera(GameWorld world, Bitmap newBackground){

		this(world, 250);//Default is 1.5 seconds
		background = newBackground;
	}

	public GameCamera(GameWorld world, float setTransitionDuration){

		gameWorld = world;

		calculateWidthFirst = true;
		currentNumberOfPlayers = -1;
		transitioning = false;
		transitionDuration = setTransitionDuration;
		currentLowerBound = null;
		currentUpperBound = null;
		currentTranslator = null;
		currentPhysicsRatio = 0;
		targetLowerBound = null;
		targetUpperBound = null;
		targetTranslator = null;
		targetPhysicsRatio = 0;
		startTime = 0;
	}

	//Determine viewport: portion of World that will be visible. Obviously, it is measured in meters.
	private void determineViewport(float timeElapsed){

		HashMap<PlayerNumber, Player> players = gameWorld.getPlayers();

		int numberOfPlayers = players.keySet().size();
		if ( currentNumberOfPlayers == -1 ){
			currentNumberOfPlayers = numberOfPlayers;
		}
		if ( numberOfPlayers != currentNumberOfPlayers ){
			transitioning = true;
			startTime = getCurrentTime();
			targetLowerBound = null;
			targetUpperBound = null;
			targetTranslator = null;
			System.out.println("TRANSITION IS STARTED");
		}
		currentNumberOfPlayers = numberOfPlayers;

		float viewportWidth, viewportHeight, physicsRatio;
		Vec2 lowerBound, upperBound, translator;

//		if ( transitioning == false || (targetLowerBound == null && targetUpperBound == null && targetTranslator == null) ){

		float maxXDistance = 0;
		float maxYDistance = 0;
		Vec2 center = new Vec2();

		for ( int i=0; i<numberOfPlayers; i++ ){

			float x = players.get(PlayerNumber.values()[i]).getPosX();
			float y = players.get(PlayerNumber.values()[i]).getPosY();

			center.addLocal(x, y);

			for ( int j=0; j<numberOfPlayers; j++ ){

				if ( i == j ){
					continue;
				}

				float x2 = players.get(PlayerNumber.values()[j]).getPosX();
				float y2 = players.get(PlayerNumber.values()[j]).getPosY();

				float currentXDistance = Math.abs(x - x2);
				float currentYDistance = Math.abs(y - y2);

				if ( maxXDistance == 0 ){
					maxXDistance = currentXDistance;
				}
				else if ( maxXDistance < currentXDistance ){
					maxXDistance = currentXDistance;
				}
				if ( maxYDistance == 0 ){
					maxYDistance = currentYDistance;
				}
				else if ( maxYDistance < currentYDistance ){
					maxYDistance = currentYDistance;
				}
			}
		}

		center.mulLocal(1f / numberOfPlayers);

		if ( maxYDistance <= maxXDistance * 0.5f ){ //Threshold indicating when it is good to start calculating height first. Measured in meters.

			viewportWidth = maxXDistance + 30;
			physicsRatio = DisplaySettings.TARGET_WIDTH / viewportWidth;
			viewportHeight = DisplaySettings.TARGET_HEIGHT / physicsRatio;

			if ( !transitioning ){

				if ( !calculateWidthFirst ){
					transitioning = true;
					startTime = getCurrentTime();
					System.out.println("TRANSITION IS STARTED");
				}
				calculateWidthFirst = true;
			}
		}
		else{

			viewportHeight = maxYDistance + 18;
			physicsRatio = DisplaySettings.TARGET_HEIGHT / viewportHeight;
			viewportWidth = DisplaySettings.TARGET_WIDTH / physicsRatio;

			if ( !transitioning ){

				if ( calculateWidthFirst ){
					transitioning = true;
					startTime = getCurrentTime();
					System.out.println("TRANSITION IS STARTED");
				}
				calculateWidthFirst = false;
			}
		}

		lowerBound = new Vec2(center.x - viewportWidth/2, center.y - viewportHeight/2);
		if ( DisplaySettings.debugViewport ){
//			lowerBound.addLocal(new Vec2(3f, 3f));
		}

		upperBound = new Vec2(center.x + viewportWidth/2, center.y + viewportHeight/2);
		if ( DisplaySettings.debugViewport ){
//			upperBound.subLocal(new Vec2(3f, 3f));
		}

		translator = new Vec2( -physicsRatio * (center.x - viewportWidth/2), physicsRatio * (center.y - viewportHeight/2) );

		if ( !transitioning ){
			currentLowerBound = lowerBound;
			currentUpperBound = upperBound;
			currentTranslator = translator;
			currentPhysicsRatio = physicsRatio;
		}
		else{
			targetLowerBound = lowerBound;
			targetUpperBound = upperBound;
			targetTranslator = translator;
			targetPhysicsRatio = physicsRatio;
		}

		if ( currentLowerBound == null ){
			currentLowerBound = targetLowerBound;
			currentUpperBound = targetUpperBound;
			currentTranslator = targetTranslator;
			currentPhysicsRatio = targetPhysicsRatio;
		}
		else if ( targetLowerBound == null ){
			targetLowerBound = currentLowerBound;
			targetUpperBound = currentUpperBound;
			targetTranslator = currentTranslator;
			targetPhysicsRatio = currentPhysicsRatio;
		}
		
		if ( transitioning ){

			float currentTime = getCurrentTime();
			float elapsedTime = currentTime - startTime;
			float reachedPercentage = elapsedTime / transitionDuration;

			if ( reachedPercentage >= 1 ){

				System.out.println("TRANSITION IS OVER");
				transitioning = false;
				
				currentLowerBound = new Vec2(targetLowerBound);
				lowerBound = currentLowerBound;

				currentUpperBound = new Vec2(targetUpperBound);
				upperBound = currentUpperBound;

				currentTranslator = new Vec2(targetTranslator);
				translator = currentTranslator;

				currentPhysicsRatio = targetPhysicsRatio;
				physicsRatio = currentPhysicsRatio;

				targetLowerBound = null;
				targetUpperBound = null;
				targetTranslator = null;
				targetPhysicsRatio = 0;
			}
			else{

				lowerBound = new Vec2(currentLowerBound);
				lowerBound.addLocal(targetLowerBound.sub(currentLowerBound).mul(reachedPercentage));

				upperBound = new Vec2(currentUpperBound);
				upperBound.addLocal(targetUpperBound.sub(currentUpperBound).mul(reachedPercentage));

				translator = new Vec2(currentTranslator);
				translator.addLocal(targetTranslator.sub(currentTranslator).mul(reachedPercentage));

				physicsRatio = currentPhysicsRatio;
				physicsRatio += (targetPhysicsRatio - currentPhysicsRatio)*reachedPercentage;
			}
		}


		gameCanvas.setPhysicsRatio(physicsRatio);

		gameCanvas.saveState();

		drawBackground(center);
		
		gameCanvas.translate(translator.x, translator.y);

		Collection<Entity> entities = getPhysicalEntitiesToBeDrawn(lowerBound, upperBound);

		for ( Entity entity : entities ){
			entity.render(gameCanvas, timeElapsed);
		}

		gameCanvas.restoreState();

	}

	private void drawBackground(Vec2 center) {

		float factor = (float) Math.ceil((DisplaySettings.DISPLAY_HEIGHT / background.getHeight()));
		float backgroundWidth = background.getWidth() * factor;
		float backgroundHeight = background.getHeight() * factor;

		RectF backgroundRect = new RectF(0f, 0f, backgroundWidth, backgroundHeight);

		System.out.println("background image: "+background.getWidth()+" "+background.getHeight());
		System.out.println("background rect: "+backgroundWidth+" "+backgroundHeight);
		
		System.out.println("center x: "+center.x);
		System.out.println("back width: "+backgroundWidth);
		System.out.println("back height: "+backgroundHeight);
		System.out.println("display width: "+DisplaySettings.DISPLAY_WIDTH);
		System.out.println("display height: "+DisplaySettings.DISPLAY_HEIGHT);
//		float translate_x = ( center.x / 199.74f ) *
//				( backgroundWidth - ( DisplaySettings.DISPLAY_WIDTH * ( backgroundHeight / DisplaySettings.DISPLAY_HEIGHT ) ) );
		float translate_x = ( center.x / 199.74f ) * ( backgroundWidth - DisplaySettings.DISPLAY_WIDTH );
		
		System.out.println("translate x: "+translate_x);
		
		float translate_y = 0;
		
		Vec2 translateBackground = new Vec2(translate_x, translate_y);
		gameCanvas.drawBitmap(background, backgroundRect, true);
		gameCanvas.drawCameraDebugRect(0, 0, backgroundRect.right, backgroundRect.bottom);
//		gameCanvas.translate(translate_x, translate_y);
	}

	private Collection<Entity> getPhysicalEntitiesToBeDrawn(Vec2 lowerBound, Vec2 upperBound) {

//		if ( DisplaySettings.debugViewport ){
//			gameCanvas.drawCameraDebugRect(lowerBound.x, lowerBound.y, upperBound.x, upperBound.y);
//		}

		final Collection<Entity> entities = new ArrayList<Entity>();

		PhysicalWorld.getInstance().getWorld().queryAABB(new QueryCallback() {

			@Override
			public boolean reportFixture(Fixture fixture) {

				Object e = fixture.getBody().getUserData();
				if ( e != null ){

					Entity entity = (Entity) e;
					entities.add(entity);
				}
				return true;
			}
		}, new AABB(lowerBound, upperBound));

		return entities;
	}

	public void render(final GameCanvas canvas, final float timeElapsed) {

		if ( gameCanvas == null ){
			gameCanvas = canvas;
		}

		if ( DisplaySettings.mockDanilo ){
			System.out.println("danilo da o cu amuado, e se nao da eu cegue");
		}


		determineViewport(timeElapsed);

	}

	private long getCurrentTime() {
		return System.nanoTime()/1000000;
	}

}