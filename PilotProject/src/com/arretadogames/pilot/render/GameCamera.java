package com.arretadogames.pilot.render;

import java.util.HashMap;

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
	private float width;
	private float height;
	private Vec2 origin;
	private Vec2 otherEnd;
	
	public GameCamera(GameWorld world){

		this(world, 0, 0, 800, 380);
	}
	
	public GameCamera(GameWorld world, int x, int y, int newWidth, int newHeight){
		
		gameWorld = world;
		width = newWidth;
		height = newHeight;

		origin = new Vec2(x, y);
		otherEnd = new Vec2(origin);
		otherEnd = otherEnd.add(new Vec2(width, height));
		System.out.println("Origin at :"+origin.x+", "+origin.y+". End at :"+otherEnd.x+", "+otherEnd.y+".");
	}

	public void setPosition(int x, int y){

		setPosition(new Vec2(x, y));
	}
	
	public void setPosition(Vec2 vec){
		
		origin.set(vec);
		otherEnd.set(origin);
		otherEnd = otherEnd.add(new Vec2(width, height));
	}
	
	public void setDimentions(float newWidth, float newHeight){

		this.width = newWidth;
		this.height = newHeight;
		
		otherEnd.set(origin);
		otherEnd = otherEnd.add(new Vec2(width, height));
	}

	public void update(){
		
		HashMap<PlayerNumber, Player> players = gameWorld.getPlayers();
		
		Vec2 p1Pos = new Vec2(players.get(PlayerNumber.ONE).getPosX(), players.get(PlayerNumber.ONE).getPosY());
		Vec2 p2Pos = new Vec2(players.get(PlayerNumber.TWO).getPosX(), players.get(PlayerNumber.TWO).getPosY());

		float distance = Math.abs(p1Pos.sub(p2Pos).length());

		float max = Math.max(width, height);
		if ( distance > max ){
			//Both players arent close enough
			float zoom = distance / max;
			
			setDimentions(width*zoom, height*zoom);
		}
		
		Vec2 center = new Vec2(p1Pos);
		center.add(p2Pos);
		center.mul(.5f);

		setPosition(center.sub(new Vec2(width/2, height/2)));
		setDimentions(width, height);
	}
	
	public void render(final GameCanvas canvas, Bitmap background, final float timeElapsed) {

		canvas.drawBitmap(background, 0, 0);
	
		PhysicalWorld.getInstance().getWorld().queryAABB(new QueryCallback() {
			
			@Override
			public boolean reportFixture(Fixture fixture) {

				Object e = fixture.getBody().getUserData();
				if ( e != null ){
					Entity entity = (Entity) e;
					entity.render(canvas, timeElapsed);
//					System.out.println("drawing entity: "+entity.getClass());
				}
				else{
				}
				return true;
			}
		}, new AABB(origin, otherEnd));
		
		canvas.drawCameraDebugRect(origin.x, origin.y, otherEnd.x, otherEnd.y);

	}

}