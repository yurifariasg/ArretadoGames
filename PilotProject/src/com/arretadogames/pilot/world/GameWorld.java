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
import com.arretadogames.pilot.entities.AraraAzul;
import com.arretadogames.pilot.entities.Box;
import com.arretadogames.pilot.entities.Breakable;
import com.arretadogames.pilot.entities.Coin;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.FinalFlag;
import com.arretadogames.pilot.entities.Fire;
import com.arretadogames.pilot.entities.Fruit;
import com.arretadogames.pilot.entities.Ground;
import com.arretadogames.pilot.entities.Liana;
import com.arretadogames.pilot.entities.LoboGuara;
import com.arretadogames.pilot.entities.OneWayWall;
import com.arretadogames.pilot.entities.PlayableCharacter;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.levels.EntityDescriptor;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.levels.LevelManager;
import com.arretadogames.pilot.levels.PlayerDescriptor;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.GameCamera;
import com.arretadogames.pilot.render.SpriteManager;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.EndScreen;
import com.arretadogames.pilot.screens.GameScreen;
import com.arretadogames.pilot.screens.GameWorldUI;
import com.arretadogames.pilot.screens.InputEventHandler;
import com.arretadogames.pilot.screens.PauseScreen;
//import com.arretadogames.pilot.entities.Liana;

/**
 * GameWorld class represents the World in our Game
 */
public class GameWorld extends GameScreen {
	
	private static final int LEVEL_INDEX = 0; /* Level to load */
	
	private int backgroundId;
	
	private GameWorldUI ui;
	private PhysicalWorld pWorld;
	private Collection<Entity> worldEntities;
	private Collection<Steppable> steppables;
	private HashMap<PlayerNumber, Player> players;
	private HashMap<PlayerNumber, PlayableCharacter> selectedCharacters;
	private GameCamera gameCamera;
	private PauseScreen pauseScreen;
	
	private SpriteManager sm;
	private float totalElapsedSeconds;
	
	private long time;
	
	private boolean isInitialized;
	
	public GameWorld() {
		backgroundId = R.drawable.stage_background;
		pWorld = PhysicalWorld.getInstance();
		ui = new GameWorldUI(this);
		gameCamera = new GameCamera(this, backgroundId);
		pauseScreen = new PauseScreen();
		sm = new SpriteManager();
		totalElapsedSeconds = 0;
		isInitialized = false;
	}
	
	public void initialize() {
		if (isInitialized)
			return;
		
		try {
			load(LevelManager.loadLevel(0)); // 0: Default Level
		} catch (IOException e) {
			e.printStackTrace();
		}
		isInitialized = true;
	}
	
	public void load(LevelDescriptor ld) {
		createEntities(ld);
	}
	
	public void setSelectedCharacters(HashMap<PlayerNumber, PlayableCharacter> selectedCharacters) {
		this.selectedCharacters = selectedCharacters;
	}
	
	private void createEntities(LevelDescriptor ld) {
		players = new HashMap<PlayerNumber, Player>();
		worldEntities = new ArrayList<Entity>();
		steppables = new ArrayList<Steppable>();
		
		//TODO fzr direito
		worldEntities.add(new Fire(0,0));
//		worldEntities.add(new Liana(25,9,23,7));
//		worldEntities.add(new OneWayWall(30,6.5f));
		worldEntities.add(new Breakable(30,6.5f,0.2f,2f,0,false));
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
				entity = createPlayerCharacter(entityDescriptor.getX(), entityDescriptor.getY(),
						((PlayerDescriptor)entityDescriptor).getPlayerNumber());
				break;
			case FINALFLAG:
				entity = new FinalFlag(entityDescriptor.getX(), entityDescriptor.getY());
				break;
			case COIN:
				entity = new Coin(entityDescriptor.getX(), entityDescriptor.getY(), 10);
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
		
		for(Entity e : worldEntities){
			if(e instanceof Steppable){
				steppables.add((Steppable) e);
			}
		}
	}
	
	private Entity createPlayerCharacter(float x, float y,
			PlayerNumber playerNumber) {
		
		PlayableCharacter chosenCharacter = selectedCharacters.get(playerNumber);
		switch (chosenCharacter) {
		
		case LOBO_GUARA:
			return new LoboGuara(x, y, playerNumber);
		case ARARA_AZUL:
			return new AraraAzul(x, y, playerNumber);
		default:
			break;
		}
		
		return null;
	}

	public void free() {
		
	}
	
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
	
	public int getTotalElapsedTime() {
		return (int) (totalElapsedSeconds);
	}
	
	@Override
	public void step(float timeElapsed) {
		if (!isInitialized)
			initialize();
		
		totalElapsedSeconds += timeElapsed;
		
		// TODO: Perform a World Step
		pauseScreen.step(timeElapsed);
		if (pauseScreen.isHidden()) {
			for (Steppable p : steppables)
				p.step(timeElapsed);
			ui.step(timeElapsed);
			pWorld.step(timeElapsed);
		}
		removeDeadEntities();
		
		boolean hasFinished = true;
		for (Player p : players.values()) {
			hasFinished &= (p.hasFinished() || !p.isAlive()); // if all have finished or dead
		}
		
		if (hasFinished)
			onLevelFinished();
	}
	
	private void removeDeadEntities(){
		Iterator<Entity> it = pWorld.getDeadEntities();
		while(it.hasNext()){
			Entity e = it.next();
			pWorld.destroyEntity(e);
			worldEntities.remove(e);
			if(e instanceof Steppable) steppables.remove((Steppable)e);
			for ( PlayerNumber p : players.keySet() ){
				if ( players.get(p).equals(e) ){
					players.get(p).setDead(true);
					//players.remove(p); @yuri: NEVER remove a player! Just check if he's alive
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
	
	public HashMap<PlayerNumber, Player> getPlayers(){
		return players;
	}

	public Collection<Entity> getEntities(){
		return worldEntities;
	}
	
	private boolean finishWorld = false;
	
	public void onLevelFinished() {
		if (finishWorld)
			return;
		finishWorld = true;
		
		((EndScreen) Game.getInstance().getScreen(GameState.GAME_OVER)).initialize(players);
		Game.getInstance().goTo(GameState.GAME_OVER);
	}
}
