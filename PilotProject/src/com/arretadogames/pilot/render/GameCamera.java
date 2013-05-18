package com.arretadogames.pilot.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import android.graphics.Rect;
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
	private int backgroundId;
	
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
	
	private long time;

	public GameCamera(GameWorld world, int backgroundId){

		this(world, 250f);//Default is 1.5 seconds
		this.backgroundId = backgroundId;
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
		
		if (DisplaySettings.PROFILE_GAME_CAMERA)
			time = System.nanoTime() / 1000000;

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
//			System.out.println("TRANSITION IS STARTED");
		}
		currentNumberOfPlayers = numberOfPlayers;

		float viewportWidth, viewportHeight, physicsRatio;
		Vec2 lowerBound, upperBound, translator;

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

			viewportWidth = maxXDistance +10;//+ 15;//+ 30;
			physicsRatio = DisplaySettings.TARGET_WIDTH / viewportWidth;
			viewportHeight = DisplaySettings.TARGET_HEIGHT / physicsRatio;

			if ( !transitioning ){

				if ( !calculateWidthFirst ){
					transitioning = true;
					startTime = getCurrentTime();
//					System.out.println("TRANSITION IS STARTED");
				}
				calculateWidthFirst = true;
			}
		}
		else{

			viewportHeight = maxYDistance +6;//+ 9;//+ 18;
			physicsRatio = DisplaySettings.TARGET_HEIGHT / viewportHeight;
			viewportWidth = DisplaySettings.TARGET_WIDTH / physicsRatio;

			if ( !transitioning ){

				if ( calculateWidthFirst ){
					transitioning = true;
					startTime = getCurrentTime();
//					System.out.println("TRANSITION IS STARTED");
				}
				calculateWidthFirst = false;
			}
		}

		lowerBound = new Vec2(center.x - viewportWidth/2, center.y - viewportHeight/2);
		upperBound = new Vec2(center.x + viewportWidth/2, center.y + viewportHeight/2);
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

//				System.out.println("TRANSITION IS OVER");
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
		
		if (DisplaySettings.PROFILE_GAME_CAMERA) {
			System.out.println("Calculate Viewport: " + (System.nanoTime()/1000000 - time));
			time = System.nanoTime() / 1000000;
		}


		gameCanvas.setPhysicsRatio(physicsRatio);

		drawBackground(center);
		
		if (DisplaySettings.PROFILE_GAME_CAMERA) {
			System.out.println("Draw Background: " + (System.nanoTime()/1000000 - time));
			time = System.nanoTime() / 1000000;
		}

		gameCanvas.saveState();
		
		gameCanvas.translate(translator.x, translator.y);

		Collection<Entity> entities = getPhysicalEntitiesToBeDrawn(lowerBound, upperBound);

		for ( Entity entity : entities ){
			entity.render(gameCanvas, timeElapsed);
		}
		
		if (DisplaySettings.PROFILE_GAME_CAMERA) {
			System.out.println("Draw Entities: " + (System.nanoTime()/1000000 - time));
			time = System.nanoTime() / 1000000;
		}

		gameCanvas.restoreState();

	}

	private void drawBackground(Vec2 center) {

		float factor = (float) Math.ceil((DisplaySettings.TARGET_HEIGHT / 480)); // FIXME: background.getHeight()
		float backgroundWidth = 800 * factor; // FIXME: background.getWidth()
		float backgroundHeight = 480 * factor; // FIXME: background.getHeight()

		if ( backgroundWidth < DisplaySettings.TARGET_WIDTH ){
			factor = (float) Math.ceil(DisplaySettings.TARGET_WIDTH / backgroundWidth);
			backgroundWidth *= factor;
			backgroundHeight *= factor;
			System.out.println("yes");
		}
		
		RectF backgroundRect = new RectF(0f, 0f, backgroundWidth, backgroundHeight);

		float where_is = ( center.x / 199.74f );
		if ( where_is < 0 ){
			where_is = 0;
		}
		if ( where_is > 1 ){
			where_is = 1;
		}
		
		int translate_x = (int) (where_is * ( backgroundWidth - DisplaySettings.TARGET_WIDTH ));
		int translate_y = 0;
		
		if (DisplaySettings.PROFILE_GAME_CAMERA) {
			System.out.println("Calculate Background: " + (System.nanoTime()/1000000 - time));
			time = System.nanoTime() / 1000000;
		}
		
		gameCanvas.drawBitmap(backgroundId, new Rect(translate_x, translate_y, 
		translate_x + (int)backgroundWidth, translate_y + (int)backgroundHeight), 
		backgroundRect, false);
	}

	private Collection<Entity> getPhysicalEntitiesToBeDrawn(Vec2 lowerBound, Vec2 upperBound) {

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
		
		determineViewport(timeElapsed);
	}

	private long getCurrentTime() {

		return System.nanoTime()/1000000;
	}

}
