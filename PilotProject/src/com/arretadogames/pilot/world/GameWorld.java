package com.arretadogames.pilot.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jbox2d.common.Vec2;

import android.util.Log;
import android.util.SparseArray;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
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
import com.arretadogames.pilot.entities.MacacoPrego;
import com.arretadogames.pilot.entities.OneWayWall;
import com.arretadogames.pilot.entities.PlayableCharacter;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.entities.Pulley;
import com.arretadogames.pilot.entities.Spike;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.entities.TatuBola;
import com.arretadogames.pilot.entities.Water;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.levels.EntityDescriptor;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.levels.LianaDescriptor;
import com.arretadogames.pilot.levels.PlayerDescriptor;
import com.arretadogames.pilot.levels.WaterDescriptor;
import com.arretadogames.pilot.loading.LoadManager;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.GameCamera;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.SpriteManager;
import com.arretadogames.pilot.render.Watchable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.EndScreen;
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
	private Collection<Steppable> steppables;
	private HashMap<PlayerNumber, Player> players;
	private HashMap<PlayerNumber, PlayableCharacter> selectedCharacters;
	private GameCamera gameCamera;
	private PauseScreen pauseScreen;
	private float flagPos;
	private float firePos;
	private SpriteManager sm;
	private float totalElapsedSeconds;
	
	private long time;
	
	private boolean isInitialized;
	private LevelDescriptor level;
	
	public GameWorld() {
		backgroundId = R.drawable.repeatable_background;
		pWorld = PhysicalWorld.getInstance();
		gameCamera = new GameCamera(this, backgroundId);
		pauseScreen = new PauseScreen();
		sm = new SpriteManager();
		totalElapsedSeconds = 0;
		isInitialized = false;
	}
	
	public void initialize() {
		if (isInitialized || selectedCharacters == null || level == null)
			return;
		
		load(level); 
		isInitialized = true;
		setPlayersAsCurrentEntitiesToWatch();
		ui = new GameWorldUI(this);
	}
	
	private void load(LevelDescriptor ld) {
		if (!ld.isLoaded())
			ld.load();
		createEntities(ld);
	}
	
	private void createEntities(LevelDescriptor ld) {
		players = new HashMap<PlayerNumber, Player>();
		worldEntities = new ArrayList<Entity>();
		steppables = new ArrayList<Steppable>();
		
		if (GameSettings.ACTIVATE_FIRE)
			worldEntities.add(new Fire(-5,0));
		
		worldEntities.add(new Liana(25f,9f,23f,7f));
		List<EntityDescriptor> entities = ld.getEntities();
		List<Water> waterEntities = new ArrayList<Water>();
		
		if (entities != null){
			
			for (EntityDescriptor entityDescriptor : entities) {
				Entity entity = null;
				switch (entityDescriptor.getType()) {
				case BOX:
					entity = new Box(entityDescriptor.getX(), entityDescriptor.getY(),
							entityDescriptor.getSize());
					break;
				case COIN:
					entity = new Coin(entityDescriptor.getX(), entityDescriptor.getY(), 10);
					break;
				case FRUIT:
					entity = new Fruit(entityDescriptor.getX(), entityDescriptor.getY(),
							entityDescriptor.getSize());
					break;
				case PLAYER:
					entity = createPlayerCharacter(entityDescriptor.getX(), entityDescriptor.getY(),
							((PlayerDescriptor)entityDescriptor).getPlayerNumber());
					break;
				case ONEWAY_WALL:
					entity = new OneWayWall(entityDescriptor.getX(), entityDescriptor.getY());
					break;
					
				case SPIKE:
					entity = new Spike(entityDescriptor.getX(), entityDescriptor.getY());
					break;
				case PULLEY:
					Entity a = new Box(entityDescriptor.getX()-1,entityDescriptor.getY()-0.7f, 0.7f);
					a.setSprite(sm.getSprite(a));
					Entity b = new Box(entityDescriptor.getX()+1,entityDescriptor.getY(), 1.5f);
					b.setSprite(sm.getSprite(b));
					worldEntities.add(a);
					worldEntities.add(b);
					
					entity = new Pulley(
							a, new Vec2(entityDescriptor.getX()-1, entityDescriptor.getY() + 2),
							b, new Vec2(entityDescriptor.getX()+1, entityDescriptor.getY() + 2),
							new Vec2(entityDescriptor.getX()-0.4f, 2),
							new Vec2(entityDescriptor.getX()+1, 2.5f), 5);
					break;
				case BREAKABLE:
					entity = new Breakable(entityDescriptor.getX(),entityDescriptor.getY(),0.2f,2f,0,false);
					break;
				
				case LIANA:
					LianaDescriptor entityLiana = (LianaDescriptor) entityDescriptor; 
					entity = new Liana(entityLiana.getX(), entityLiana.getY(), entityLiana.getX1(), entityLiana.getY1());
					break;
					
				case FINALFLAG:
					entity = new FinalFlag(entityDescriptor.getX(), entityDescriptor.getY());
					flagPos = entityDescriptor.getX();
					break;
				case WATER:
					entity = new Water(entityDescriptor.getX(), entityDescriptor.getY(),((WaterDescriptor)entityDescriptor).getWidth(),((WaterDescriptor)entityDescriptor).getHeight(),((WaterDescriptor)entityDescriptor).getDensity());
					waterEntities.add((Water)entity);
					break;
				default:
					break;
				}
				
				if (entity != null) {
					Sprite sprite = sm.getSprite(entity);
					entity.setSprite(sprite);
					LoadManager.getInstance().addExtraObjects(sprite.getAllFrames());
					worldEntities.add(entity);
					if (entity.getType() == EntityType.PLAYER)
						players.put(((Player)entity).getNumber(), (Player) entity);
				}
			}
		}
		
		// Add Ground
		System.out.println("WATER SPOTS: " + waterEntities.size());
		Vec2[] groundPoints = PhysicalWorld.getInstance().createGroundLines(waterEntities);//new Vec2[ld.getGroundDescriptor().getPoints().size()];
		int amountOfPoints = groundPoints.length;
		
		Vec2[] vecs = new Vec2[amountOfPoints > GameSettings.GROUND_ENTITY_THRESHOLD ? GameSettings.GROUND_ENTITY_THRESHOLD : amountOfPoints];
		int internalPointer = 0;
		for (int i = 0 ; i < amountOfPoints ; i++) {
			
			vecs[internalPointer] = groundPoints[i];
			
			if (internalPointer == GameSettings.GROUND_ENTITY_THRESHOLD - 1) {
				worldEntities.add(new Ground(vecs, vecs.length));
				vecs = new Vec2[GameSettings.GROUND_ENTITY_THRESHOLD];
				vecs[0] = groundPoints[i];
				internalPointer = 1;
			} else {
				internalPointer %= GameSettings.GROUND_ENTITY_THRESHOLD;
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
		case MACACO_PREGO:
			return new MacacoPrego(x, y, playerNumber);
		case TATU_BOLA:
			return new TatuBola(x, y, playerNumber);
		default:
			break;
		}
		
		return null;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		if (GameSettings.PROFILE_RENDER_SPEED)
			time = System.nanoTime()/1000000;
		if (!pauseScreen.isHidden()) {
			gameCamera.render(canvas, 0); // Draw a fixed frame - Dont move anything
		} else {
			gameCamera.render(canvas, timeElapsed);
		}
		if (GameSettings.PROFILE_RENDER_SPEED) {
			Log.d("Profile", "Camera Render Time: " + (System.nanoTime()/1000000 - time));
			time = System.nanoTime()/1000000;
		}
		
		ui.render(canvas, timeElapsed);
		
		if (GameSettings.PROFILE_RENDER_SPEED) {
			Log.d("Profile", "UI Render Time: " + (System.nanoTime()/1000000 - time));
			time = System.nanoTime()/1000000;
		}
		
		pauseScreen.render(canvas, timeElapsed);
		
		if (GameSettings.PROFILE_RENDER_SPEED) {
			Log.d("Profile", "Pause Screen Render Time: " + (System.nanoTime()/1000000 - time));
		}
	}
	
	public int getTotalElapsedTime() {
		return (int) (totalElapsedSeconds);
	}
	
	@Override
	public void step(float timeElapsed) {
		totalElapsedSeconds += timeElapsed;
		
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
		pauseScreen.input(event);
		if (pauseScreen.isHidden())
			ui.input(event);
	}

	@Override
	public void onPause() {
		if (pauseScreen.isHidden())
			pauseScreen.show();
	}
	
	public HashMap<PlayerNumber, Player> getPlayers(){
		return players;
	}
	
	public void setPlayersAsCurrentEntitiesToWatch(){
		
		HashMap<PlayerNumber, Player> players = getPlayers();
		SparseArray<Watchable> toWatch = new SparseArray<Watchable>();
		
		for (PlayerNumber n : players.keySet() ){
			toWatch.put(n.getValue(), players.get(n));
		}
		gameCamera.setEntitiesToWatch(toWatch);
	}

	public Collection<Entity> getEntities(){
		return worldEntities;
	}
	
	private boolean finishWorld = false;
	
	public void onLevelFinished() {
		if (finishWorld)
			return;
		finishWorld = true;
		
		((EndScreen) Game.getInstance().getScreen(GameState.GAME_OVER)).initialize(players, level);
		Game.getInstance().goTo(GameState.GAME_OVER);
	}

	public void setLevel(LevelDescriptor level) {
		this.level = level;
	}
	
	public void setSelectedCharacters(HashMap<PlayerNumber, PlayableCharacter> selectedCharacters) {
		this.selectedCharacters = selectedCharacters;
	}

	public void destroyResources() {
		// TODO Auto-generated method stub
	}
	
	public float getFlagPos(){
		return flagPos;
	}
}
