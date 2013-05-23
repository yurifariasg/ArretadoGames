package com.arretadogames.pilot.world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jbox2d.common.Vec2;

import android.util.Log;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.entities.Box;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.Fire;
import com.arretadogames.pilot.entities.Fruit;
import com.arretadogames.pilot.entities.Ground;
import com.arretadogames.pilot.entities.LoboGuara;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.levels.EntityDescriptor;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.levels.LevelManager;
import com.arretadogames.pilot.levels.PlayerDescriptor;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.GameCamera;
import com.arretadogames.pilot.render.SpriteManager;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.GameScreen;
import com.arretadogames.pilot.screens.GameWorldUI;
import com.arretadogames.pilot.screens.InputEventHandler;
import com.arretadogames.pilot.screens.PauseScreen;

/**
 * GameWorld class represents the World in our Game
 */
public class GameWorld extends GameScreen {
	
	private int backgroundId;
	
	private GameWorldUI ui;
	private PhysicalWorld pWorld;
	private Collection<Entity> worldEntities;
	private HashMap<PlayerNumber, Player> players;
	private GameCamera gameCamera;
	private PauseScreen pauseScreen;
	
	private SpriteManager sm;
	
	public GameWorld() {
		backgroundId = R.drawable.stage_background;
		pWorld = PhysicalWorld.getInstance();
		ui = new GameWorldUI(this);
		gameCamera = new GameCamera(this, backgroundId);
		pauseScreen = new PauseScreen();
		sm = new SpriteManager();
		
		try {
			load(LevelManager.loadLevel(0)); // 0: Default Level
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load(LevelDescriptor ld) {
		createEntities(ld);
	}
	
	private void createEntities(LevelDescriptor ld) {
		players = new HashMap<PlayerNumber, Player>();
		worldEntities = new ArrayList<Entity>();
		
		//TODO fzr direito
		worldEntities.add(new Fire(0,0));
		
		List<EntityDescriptor> entities = ld.getEntities();
		for (EntityDescriptor entityDescriptor : entities) {
			Entity entity = null;
			switch (entityDescriptor.getType()) {
			case BOX:
				entity = new Box(entityDescriptor.getX(), entityDescriptor.getY(),
						entityDescriptor.getSize());
				break;
			case FRUIT:
				entity = new Fruit(entityDescriptor.getX(), entityDescriptor.getY(),
						entityDescriptor.getSize());
				break;
			case PLAYER:
				entity = new LoboGuara(entityDescriptor.getX(), entityDescriptor.getY(),
						((PlayerDescriptor)entityDescriptor).getPlayerNumber());
				break;
			default:
				break;
			}
			
			if (entity != null) {
				entity.setSprite(sm.getSprite(entity));
				worldEntities.add(entity);
				if (entity.getType() == EntityType.PLAYER)
					players.put(((Player)entity).getNumber(), (Player) entity);
			}
		}
		
		// Add Ground
		Vec2[] groundPoints = new Vec2[ld.getGroundDescriptor().getPoints().size()];
		ld.getGroundDescriptor().getPoints().toArray(groundPoints);
		int amountOfPoints = groundPoints.length;
		
		Vec2[] vecs = new Vec2[amountOfPoints > DisplaySettings.GROUND_ENTITY_THRESHOLD ? DisplaySettings.GROUND_ENTITY_THRESHOLD : amountOfPoints];
		int internalPointer = 0;
		for (int i = 0 ; i < amountOfPoints ; i++) {
			
			vecs[internalPointer] = groundPoints[i];
			
			if (internalPointer == DisplaySettings.GROUND_ENTITY_THRESHOLD - 1) {
				worldEntities.add(new Ground(vecs, vecs.length));
				vecs = new Vec2[DisplaySettings.GROUND_ENTITY_THRESHOLD];
				vecs[0] = groundPoints[i];
				internalPointer = 1;
			} else {
				internalPointer %= DisplaySettings.GROUND_ENTITY_THRESHOLD;
				internalPointer++;
			}
			
		}
		
		if (internalPointer != 0) {
			Vec2[] lastVec = new Vec2[internalPointer];
			for (int i = 0 ; i < internalPointer ; i++)
				lastVec[i] = vecs[i];
			worldEntities.add(new Ground(lastVec, internalPointer));
		}
	}
	
	public void free() {
		
	}
	
	private long time;
	
	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		// Render the World
		
		if (DisplaySettings.PROFILE_RENDER_SPEED)
			time = System.nanoTime()/1000000;
		if (!pauseScreen.isHidden()) {
			gameCamera.render(canvas, 0); // Draw a fixed frame
		} else {
			gameCamera.render(canvas, timeElapsed);
		}
		if (DisplaySettings.PROFILE_RENDER_SPEED) {
			Log.d("Profile", "Camera Render Time: " + (System.nanoTime()/1000000 - time));
			time = System.nanoTime()/1000000;
		}
		
		ui.render(canvas, timeElapsed);
		
		if (DisplaySettings.PROFILE_RENDER_SPEED) {
			Log.d("Profile", "UI Render Time: " + (System.nanoTime()/1000000 - time));
			time = System.nanoTime()/1000000;
		}
		
		pauseScreen.render(canvas, timeElapsed);
		
		if (DisplaySettings.PROFILE_RENDER_SPEED) {
			Log.d("Profile", "Pause Screen Render Time: " + (System.nanoTime()/1000000 - time));
		}
	}
	
	@Override
	public void step(float timeElapsed) {
		// TODO: Perform a World Step
		pauseScreen.step(timeElapsed);
		if (pauseScreen.isHidden()) {
			for (Entity p : worldEntities)
				p.step(timeElapsed);
			ui.step(timeElapsed);
			pWorld.step(timeElapsed);
		}
		removeDeadEntities();
	}
	
	private void removeDeadEntities(){
		Iterator<Entity> it = pWorld.getDeadEntities();
		while(it.hasNext()){
			Entity e = it.next();
			pWorld.destroyEntity(e);
			worldEntities.remove(e);
			for ( PlayerNumber p : players.keySet() ){
				if ( players.get(p).equals(e) ){
					players.remove(p);
					break;
				}
			}
		}
		pWorld.clearDeadEntities();
	}

	@Override
	public void input(InputEventHandler event) {
		// TODO Handle Inputs
		pauseScreen.input(event);
		if (pauseScreen.isHidden())
			ui.input(event);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (pauseScreen.isHidden())
			ui.onBackPressed();
		else
			pauseScreen.onBackPressed();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
	}
	
	public void jumpPlayer(PlayerNumber number) {
		Player p = players.get(number);
		if (p != null)
			p.jump();
		
	}
	
	public void actPlayer(PlayerNumber number) {
		Player p = players.get(number);
		if (p != null)
			p.act();
	}
	
	public HashMap<PlayerNumber, Player> getPlayers(){
		return players;
	}

	public Collection<Entity> getEntities(){
		return worldEntities;
	}
	
}
