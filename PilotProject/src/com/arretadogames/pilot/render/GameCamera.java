package com.arretadogames.pilot.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import android.graphics.Bitmap;

import com.arretadogames.pilot.Configuration;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.world.GameWorld;

/*
 * This class represents the viewport of our game, or in other words, 
 * the portion of the world that is currently being displayed.
 * 
 */

public class GameCamera {

	private GameWorld gameWorld = null;
	private GameCanvas gameCanvas = null;
	
	public GameCamera(GameWorld world){

		gameWorld = world;
	}

	//Determine viewport: portion of World that will be visible. Obviously, it is measured in meters.
	public void determineViewport(float timeElapsed){

		HashMap<PlayerNumber, Player> players = gameWorld.getPlayers();

		int numberOfPlayers = players.keySet().size();

		float maxDistance = 0;
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
				float currentDistance = Math.abs(x - x2);
				
				if ( maxDistance == -1 ){
					maxDistance = currentDistance;
				}
				else if ( maxDistance < currentDistance ){
					maxDistance = currentDistance;
				}
			}
		}
		
		center.mulLocal(1f / numberOfPlayers);
		
		float viewportWidth = maxDistance + 30;
		float physicsRatio = GameCanvas.SCREEN_WIDTH / viewportWidth;
		float viewportHeight = GameCanvas.SCREEN_HEIGHT / physicsRatio;
		
		Vec2 lowerBound = new Vec2(center.x - viewportWidth/2, center.y - viewportHeight/2);
		if ( Configuration.debugViewport ){
			lowerBound.addLocal(new Vec2(2, 2));
		}

		Vec2 upperBound = new Vec2(center.x + viewportWidth/2, center.y + viewportHeight/2);
		if ( Configuration.debugViewport ){
			upperBound.subLocal(new Vec2(2, 2));
		}
		
		Vec2 translator = new Vec2( -physicsRatio * (center.x - viewportWidth/2), physicsRatio * (center.y - viewportHeight/2) );
		
		gameCanvas.setPhysicsRatio(physicsRatio);

		gameCanvas.saveState();
		gameCanvas.translate(translator.x, translator.y);

		Collection<Entity> entities = getPhysicalEntitiesToBeDrawn(lowerBound, upperBound);
		
		for ( Entity entity : entities ){
			entity.render(gameCanvas, timeElapsed);
		}

		gameCanvas.restoreState();
	}

	private Collection<Entity> getPhysicalEntitiesToBeDrawn(Vec2 lowerBound, Vec2 upperBound) {

		if ( Configuration.debugViewport ){
			gameCanvas.drawCameraDebugRect(lowerBound.x, lowerBound.y, upperBound.x, upperBound.y);
		}

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
	
	public void render(final GameCanvas canvas, Bitmap background, final float timeElapsed) {

		if ( gameCanvas == null ){
			gameCanvas = canvas;
		}
		
		gameCanvas.drawBitmap(background, 0, 0);

		determineViewport(timeElapsed);
		
	}

}