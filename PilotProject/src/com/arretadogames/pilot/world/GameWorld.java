package com.arretadogames.pilot.world;

import android.graphics.Color;
import android.util.SparseArray;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityBuilder;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.PlayableCharacter;
import com.arretadogames.pilot.entities.PlayableItem;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.entities.effects.EffectDescriptor;
import com.arretadogames.pilot.entities.effects.EffectManager;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.GameCamera;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.EndScreen;
import com.arretadogames.pilot.screens.GameScreen;
import com.arretadogames.pilot.screens.GameWorldUI;
import com.arretadogames.pilot.screens.InputEventHandler;
import com.arretadogames.pilot.screens.PauseScreen;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.util.Profiler;
import com.arretadogames.pilot.util.Profiler.ProfileType;
import com.arretadogames.pilot.weathers.Storm;
import com.arretadogames.pilot.weathers.Weather;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * GameWorld class represents the World in our Game
 */
public class GameWorld extends GameScreen implements GameButtonListener {
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

	private Weather weather;
	private RaceStartManager raceStartManager;
	
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
        raceStartManager = new RaceStartManager(this, ui);
	}
	
	private void load(LevelDescriptor ld) {
		if (!ld.isLoaded())
			ld.load();
		createEntities(ld);
		PhysicalWorld.getInstance().sleepAllEntities();
	}
	
	private void createEntities(LevelDescriptor ld) {
		EntityBuilder builder = new EntityBuilder(ld);
		builder.buildEntities();
		
		builder.addPlayer(PlayerNumber.ONE, selectedCharacters.get(PlayerNumber.ONE));
		builder.addPlayer(PlayerNumber.TWO, selectedCharacters.get(PlayerNumber.TWO));
		
		worldEntities = builder.getAllEntities();
		steppables = new ArrayList<Steppable>();
		players = new HashMap<PlayerNumber, Player>();
		
		for (Entity entity : worldEntities) {
			if (entity instanceof Steppable) {
				steppables.add((Steppable)entity);
			}
			
			if (entity.getType().equals(EntityType.PLAYER)) {
				// If is a player
				
				Player p = (Player) entity;
				players.put(p.getNumber(), p);
				
			} else if (entity.getType().equals(EntityType.FINALFLAG)) {
				flagPos = entity.getPosX();
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
		raceStartManager.render(canvas, timeElapsed);

		Profiler.profileFromLastTick(ProfileType.RENDER, "UI Render Time");
		Profiler.initTick(ProfileType.RENDER);
		
		pauseScreen.render(canvas, timeElapsed);

		Profiler.profileFromLastTick(ProfileType.RENDER, "Pause Screen Render Time");
	}
	
	public float getTotalElapsedTime() {
		return totalElapsedSeconds;
	}
	
	@Override
	public void step(float timeElapsed) {
		totalElapsedSeconds += timeElapsed;
		
		pauseScreen.step(timeElapsed);
		if (pauseScreen.isHidden()) {
			for (Steppable p : steppables)
				p.step(timeElapsed);
			gameCamera.step(timeElapsed);
			ui.step(timeElapsed);
			raceStartManager.step(timeElapsed);
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

	public float getFlagPos(){
		return flagPos;
	}

    @Override
    public void onClick(int buttonId) {
        Player p;
        if (buttonId == GameWorldUI.BT_PLAYER_1_ACT) {
            p = getPlayers().get(PlayerNumber.ONE);
            if (p != null) {
                p.setAct(true);
            }
        } else if (buttonId == GameWorldUI.BT_PLAYER_1_JUMP) {
            p = getPlayers().get(PlayerNumber.ONE);
            if (p != null) {
                p.setJumping(true);
            }
        } else if (buttonId == GameWorldUI.BT_PLAYER_1_ITEM) {
            p = getPlayers().get(PlayerNumber.ONE);
            if (p != null && p.getItem() != null) {
                p.getItem().activate(p, this);
            }
        } else if (buttonId == GameWorldUI.BT_PLAYER_2_ACT) {
            p = getPlayers().get(PlayerNumber.TWO);
            if (p != null) {
                p.setAct(true);
            }
        } else if (buttonId == GameWorldUI.BT_PLAYER_2_JUMP) {
            p = getPlayers().get(PlayerNumber.TWO);
            if (p != null) {
                p.setJumping(true);
            }
        } else if (buttonId == GameWorldUI.BT_PLAYER_2_ITEM) {
            p = getPlayers().get(PlayerNumber.TWO);
            if (p != null && p.getItem() != null) {
                p.getItem().activate(p, this);
            }
        }
    }
}
