package com.arretadogames.pilot.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import android.graphics.Bitmap;

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
	private float range = 5; //range of sight of a player in meters

	public GameCamera(GameWorld world){

		gameWorld = world;
	}

	//Determine viewport: portion of World that will be visible. Obviously, it is measured in meters.
	public void determineViewport(float timeElapsed){

		HashMap<PlayerNumber, Player> players = gameWorld.getPlayers();
		Vec2 p1 = null, p2 = null;

		if ( players.containsKey(PlayerNumber.ONE) ){
			p1 = new Vec2(players.get(PlayerNumber.ONE).getPosX(), players.get(PlayerNumber.ONE).getPosY());
		}
		if ( players.containsKey(PlayerNumber.TWO) ){
			p2 = new Vec2(players.get(PlayerNumber.TWO).getPosX(), players.get(PlayerNumber.TWO).getPosY());
		}

		List<Vec2> boundaries = null;
		
		if ( p1 == null || p2 == null ){

			if ( p1 == null ){
				
				boundaries = singlePlayerViewport(p1);
			}
			else{
				
				boundaries = singlePlayerViewport(p2);
			}
		}
		else{

			float distance = Math.abs(p1.sub(p2).length());

			if ( distance < range ){

				boundaries = twoPlayerFarViewport(p1, p2);
			}
			else{

				boundaries = twoPlayerFarViewport(p1, p2);
			}
			
		}

		Vec2 lowerBound = boundaries.get(0);
		Vec2 upperBound = boundaries.get(1);
		
		Collection<Entity> entities = getPhysicalEntitiesToBeDrawn(lowerBound, upperBound);

//		Collection<Drawing> drawings = new ArrayList<Drawing>();
		
		for ( Entity entity : entities ){
			entity.render(gameCanvas, timeElapsed);
		}
	}

	private List<Vec2> singlePlayerViewport(Vec2 player) {

		List<Vec2> boundaries = new ArrayList<Vec2>();
		Vec2 lowerBound = new Vec2(player);
		Vec2 upperBound = new Vec2(player);

		lowerBound = lowerBound.sub(new Vec2(range*1.5f, range*1.5f));
		upperBound = upperBound.add(new Vec2(range*1.5f, range*1.5f));

		boundaries.add(lowerBound);
		boundaries.add(upperBound);
		return boundaries;
	}

	private List<Vec2> twoPlayerCloseViewport(Vec2 p1, Vec2 p2) {

		List<Vec2> boundaries = new ArrayList<Vec2>();
		Vec2 lowerBound = null;
		Vec2 upperBound = null;

		if ( p1.x < p2.x ){
			lowerBound = new Vec2(p1);
			upperBound = new Vec2(p2);
		}
		else{
			lowerBound = new Vec2(p2);
			upperBound = new Vec2(p1);			
		}
		
		lowerBound = lowerBound.sub(new Vec2(range*1.5f, range*1.5f));
		upperBound = upperBound.add(new Vec2(range*1.5f, range*1.5f));

		boundaries.add(lowerBound);
		boundaries.add(upperBound);
		return boundaries;
	}

	private List<Vec2> twoPlayerFarViewport(Vec2 p1, Vec2 p2) {

		List<Vec2> boundaries = new ArrayList<Vec2>();
		Vec2 lowerBound = null;
		Vec2 upperBound = null;
		
		if ( p1.x < p2.x ){
			if ( p1.y < p2.y ){
				lowerBound = new Vec2(p1);
				upperBound = new Vec2(p2);
			}
			else{
				lowerBound = new Vec2(p1.x, p2.y);
				upperBound = new Vec2(p2.x, p1.y);
			}
		}
		else{
			if ( p1.y < p2.y ){
				lowerBound = new Vec2(p2.x, p1.y);
				upperBound = new Vec2(p1.x, p2.y);
			}
			else{
				lowerBound = new Vec2(p2);
				upperBound = new Vec2(p1);
			}
		}

		lowerBound = lowerBound.sub(new Vec2(range, range));
		upperBound = upperBound.add(new Vec2(range, range));
		
		boundaries.add(lowerBound);
		boundaries.add(upperBound);
		return boundaries;
	}

	private Collection<Entity> getPhysicalEntitiesToBeDrawn(Vec2 lowerBound, Vec2 upperBound) {

		gameCanvas.drawCameraDebugRect(lowerBound.x, lowerBound.y, upperBound.x, upperBound.y);
		
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