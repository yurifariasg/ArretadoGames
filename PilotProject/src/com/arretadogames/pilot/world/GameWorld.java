package com.arretadogames.pilot.world;

import android.graphics.Color;
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
import com.arretadogames.pilot.entities.Ground;
import com.arretadogames.pilot.entities.Hole;
import com.arretadogames.pilot.entities.Liana;
import com.arretadogames.pilot.entities.LoboGuara;
import com.arretadogames.pilot.entities.MacacoPrego;
import com.arretadogames.pilot.entities.OneWayWall;
import com.arretadogames.pilot.entities.PlayableCharacter;
import com.arretadogames.pilot.entities.PlayableItem;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.entities.Spike;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.entities.TatuBola;
import com.arretadogames.pilot.entities.Water;
import com.arretadogames.pilot.entities.effects.EffectDescriptor;
import com.arretadogames.pilot.entities.effects.EffectManager;
import com.arretadogames.pilot.entities.scenario.Grass;
import com.arretadogames.pilot.entities.scenario.Shrub;
import com.arretadogames.pilot.entities.scenario.Tree;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.items.DoubleJump;
import com.arretadogames.pilot.items.SuperJump;
import com.arretadogames.pilot.items.SuperStrength;
import com.arretadogames.pilot.items.Velocity;
import com.arretadogames.pilot.levels.EntityDescriptor;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.levels.LianaDescriptor;
import com.arretadogames.pilot.levels.PlayerDescriptor;
import com.arretadogames.pilot.levels.WaterDescriptor;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.GameCamera;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.EndScreen;
import com.arretadogames.pilot.screens.GameScreen;
import com.arretadogames.pilot.screens.GameWorldUI;
import com.arretadogames.pilot.screens.InputEventHandler;
import com.arretadogames.pilot.screens.PauseScreen;
import com.arretadogames.pilot.util.Profiler;
import com.arretadogames.pilot.util.Profiler.ProfileType;
import com.arretadogames.pilot.weathers.Storm;
import com.arretadogames.pilot.weathers.Weather;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
	private HashMap<PlayerNumber, List<PlayableItem>> selectedItems;
	private GameCamera gameCamera;
	private PauseScreen pauseScreen;
	private float flagPos;
	private float totalElapsedSeconds;
	
	private boolean isInitialized;
	private LevelDescriptor level;
	
	private Fire fire;

	private Weather weather;
	
	public GameWorld() {
		backgroundId = R.drawable.mountains_repeatable;
		pWorld = PhysicalWorld.getInstance();
		totalElapsedSeconds = 0;
		weather = new Storm();
		isInitialized = false;
	}
	
	@Override
	public void onLoading() {
		gameCamera = new GameCamera(this, backgroundId);
		pauseScreen = new PauseScreen();
		initialize();
	}
	
	@Override
	public void onUnloading() {
		totalElapsedSeconds = 0;
		isInitialized = false;
		pWorld.removeAll();
		fire = null;
		flagPos = 0;
		players.clear();
		steppables.clear();
		worldEntities.clear();
		gameCamera = null;
		pauseScreen = null;
		finishWorld = false;
		System.gc();
	}
	
	public void initialize() {
		if (isInitialized || selectedCharacters == null || level == null || selectedItems == null)
			return;
		
		AnimationManager.getInstance().loadXml();
		load(level); 
		isInitialized = true;
		setPlayersAsCurrentEntitiesToWatch();
		ui = new GameWorldUI(this);
	}
	
	private void load(LevelDescriptor ld) {
		if (!ld.isLoaded())
			ld.load();
		createEntities(ld);
		PhysicalWorld.getInstance().sleepAllEntities();
	}
	
	private void createEntities(LevelDescriptor ld) {
		players = new HashMap<PlayerNumber, Player>();
		worldEntities = new ArrayList<Entity>();
		steppables = new ArrayList<Steppable>();
		
		if (GameSettings.ACTIVATE_FIRE){
			fire = new Fire(-5,0);
			worldEntities.add(fire);
		}
		
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
				case SEED:
				    System.out.println(" Creating Seed ");
					entity = new Coin(entityDescriptor.getX(), entityDescriptor.getY(), 10);
					break;
				case HOLE:
					entity = new Hole(entityDescriptor.getX(), entityDescriptor.getY());
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
//				case PULLEY:
//					break;
				case BREAKABLE:
					entity = new Breakable(entityDescriptor.getX(),entityDescriptor.getY(),0.2f,1.5f,0,false);
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
				case TREE:
					entity = new Tree(entityDescriptor.getX(), entityDescriptor.getY());
					break;
				case SHRUB:
					entity = new Shrub(entityDescriptor.getX(), entityDescriptor.getY());
					break;
				case GRASS:
					entity = new Grass(entityDescriptor.getX(), entityDescriptor.getY());
					break;
				default:
					break;
				}

				if (entity != null) {
				    AnimationSwitcher sprite = null;
				    if (entityDescriptor.getType().equals(EntityType.PLAYER)) {
				        if (entity instanceof LoboGuara) {
				            sprite = AnimationManager.getInstance().getSprite("LoboGuara");
				        } else if (entity instanceof AraraAzul) {
				            sprite = AnimationManager.getInstance().getSprite("AraraAzul");
				        } else if (entity instanceof TatuBola) {
				            sprite = AnimationManager.getInstance().getSprite("TatuBola");
				        } else if (entity instanceof MacacoPrego) {
				            sprite = AnimationManager.getInstance().getSprite("MacacoPrego");
				        }
				        
				    } else {
				        sprite = AnimationManager.getInstance().getSprite(entityDescriptor.getType().toString());
				    }
				    
					entity.setSprite(sprite);
//					LoadManager.getInstance().addExtraObjects(sprite.getAllFrames());
					worldEntities.add(entity);
					if (entity.getType() == EntityType.PLAYER) {
						players.put(((Player)entity).getNumber(), (Player) entity);
					}
				}
			}
		}

		// Add Ground
		Vec2[] groundPoints = PhysicalWorld.getInstance().createGroundLines(waterEntities, flagPos);
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
		
		// Add Arrows
		PhysicsRect phrect = new PhysicsRect(0.4f, 0.8f);
		Player p1 = players.get(PlayerNumber.ONE);
        Player p2 = players.get(PlayerNumber.TWO);
        EffectDescriptor descriptor = new EffectDescriptor();
        descriptor.type = "Arrow";
        descriptor.position = p1.body.getPosition();
        descriptor.pRect = phrect;
        descriptor.yOffset = 1;
        descriptor.duration = 5;
        descriptor.color = Color.BLUE;
        
        EffectManager.getInstance().addEffect(descriptor);
        
        descriptor = descriptor.clone();
        descriptor.position = p2.body.getPosition();
        descriptor.color = Color.RED;
        
        EffectManager.getInstance().addEffect(descriptor);
	}
	
	private void setItemToPlayer(Player p, List<PlayableItem> list){
		if (list != null && list.size() > 0) {
			switch (list.get(0)) {
			case SUPER_JUMP:
				p.addItem(new SuperJump(5));
				break;
				
			case SUPER_STRENGTH:
				p.addItem(new SuperStrength(5));
				break;
				
			case SUPER_SPEED:
				p.addItem(new Velocity(5));
				break;
				
			case DOUBLE_JUMP:
				p.addItem(new DoubleJump());
				break;
	
			default:
				break;
			}
		}
	}
	
	private Entity createPlayerCharacter(float x, float y,
			PlayerNumber playerNumber) {
		
		PlayableCharacter chosenCharacter = selectedCharacters.get(playerNumber);
		Player p;
		switch (chosenCharacter) {
		case LOBO_GUARA:
			p = new LoboGuara(x, y, playerNumber);
			setItemToPlayer(p, selectedItems.get(playerNumber));
			return p;
		case ARARA_AZUL:
			p = new AraraAzul(x, y, playerNumber);
			setItemToPlayer(p, selectedItems.get(playerNumber));
			return p;
		case MACACO_PREGO:
			p = new MacacoPrego(x, y, playerNumber);
			setItemToPlayer(p, selectedItems.get(playerNumber));
			return p;
		case TATU_BOLA:
			p = new TatuBola(x, y, playerNumber);
			setItemToPlayer(p, selectedItems.get(playerNumber));
			return p;
		default:
			break;
		}
		
		return null;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		Profiler.initTick(ProfileType.RENDER);

		weather.drawBackground(canvas);
		if (!pauseScreen.isHidden()) {
			gameCamera.render(canvas, 0); // Draw a fixed frame - Dont move anything
		} else {
			gameCamera.render(canvas, timeElapsed);
		}
		
//		weather.render(canvas, timeElapsed);
		
		Profiler.profileFromLastTick(ProfileType.RENDER, "Camera Render Time");
		Profiler.initTick(ProfileType.RENDER);
		
		ui.render(canvas, timeElapsed);
		

		Profiler.profileFromLastTick(ProfileType.RENDER, "UI Render Time");
		Profiler.initTick(ProfileType.RENDER);
		
		pauseScreen.render(canvas, timeElapsed);

		Profiler.profileFromLastTick(ProfileType.RENDER, "Pause Screen Render Time");
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
			hasFinished &= (p.hasFinished() || p.isDead()); // if all have finished or dead
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
					players.get(p).kill();
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
		SparseArray<Player> toWatch = new SparseArray<Player>();
		
		for (PlayerNumber n : players.keySet() ){
			toWatch.put(n.getValue(), players.get(n));
		}
//		gameCamera.setEntitiesToWatch(toWatch);
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
	
	public void setSelectedItems(HashMap<PlayerNumber, List<PlayableItem>> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public void destroyResources() {
		// TODO Auto-generated method stub
	}
	
	public float getFlagPos(){
		return flagPos;
	}
	
	public Fire getFire(){
		return fire;
	}
}
