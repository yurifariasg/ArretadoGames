package com.arretadogames.pilot.world;

import java.util.Collection;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;

import com.arretadogames.pilot.GameActivity;
import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.LoboGuara;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.loading.Loader;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.screens.GameScreen;
import com.arretadogames.pilot.screens.GameWorldUI;

/**
 * GameWorld class represents the World in our Game
 */
public class GameWorld extends GameScreen {
	
	private Bitmap background;
	
	private GameWorldUI ui;
	private PhysicalWorld pWorld;
	private Collection<Entity> worldEntities;
	private HashMap<PlayerNumber, Player> players;
	
	
	public GameWorld() {
		background = BitmapFactory.decodeResource(GameActivity.getContext().getResources(),
				R.drawable.stage_background);
		pWorld = PhysicalWorld.getInstance();
		Loader loader = new Loader(Loader.jsonExample2);
		ui = new GameWorldUI(this);
		worldEntities = loader.getEntities();
		players = new HashMap<PlayerNumber, Player>();
		
		LoboGuara loboGuara = new LoboGuara(0f, 0f, PlayerNumber.ONE);
		
		players.put(loboGuara.getNumber(), loboGuara);
		worldEntities.add(loboGuara);
	}
	
	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		// Render the World
		canvas.drawBitmap(background, 0, 0);
		
		for (Entity entity : worldEntities)
			entity.render(canvas, timeElapsed);
		
		ui.render(canvas, timeElapsed);
	}
	
	@Override
	public void step(float timeElapsed) {
		// TODO: Perform a World Step
		ui.step(timeElapsed);
		pWorld.step(timeElapsed);
	}

	@Override
	public void input(MotionEvent event) {
		// TODO Handle Inputs
		ui.input(event);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		ui.onBackPressed();
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

}
