package com.arretadogames.pilot.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.LayerEntity.Layer;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.world.GameWorld;

public class GameCamera {

	private static GameWorld gameWorld = null;
	private int backgroundId;

	private boolean calculateWidthFirst;
	private int currentNumberOfPlayers;

	private Vec2 currentLowerBound;
	private Vec2 currentUpperBound;
	private Vec2 currentTranslator;
	private float currentPhysicsRatio;

	private boolean transitioning;
	private float transitionDuration; // Measured in milliseconds.
	private long startTime;

	private Vec2 targetLowerBound;
	private Vec2 targetUpperBound;
	private Vec2 targetTranslator;
	private float targetPhysicsRatio;

	private long time;

	public GameCamera(GameWorld world, int backgroundId) {

		this(world, 1000f);// Default is 1000 milliseconds
		this.backgroundId = backgroundId;
	}

	public GameCamera(GameWorld world, float setTransitionDuration) {

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

	private int getNumberOfAlivePlayers(Collection<Player> players) {
		int alive = 0;
		for (Player p : players)
			if (p.isAlive())
				alive++;
		return alive;
	}

	// Determine viewport: portion of World that will be visible. Obviously, it
	// is measured in meters.
	private void determineViewport(GLCanvas gameCanvas, float timeElapsed) {

		if (GameSettings.PROFILE_GAME_CAMERA)
			time = System.nanoTime() / 1000000;

		HashMap<PlayerNumber, Player> players = gameWorld.getPlayers();

		int numberOfPlayers = getNumberOfAlivePlayers(players.values());
		if (currentNumberOfPlayers == -1) {
			currentNumberOfPlayers = numberOfPlayers;
		}
		if (numberOfPlayers != currentNumberOfPlayers) {
			transitioning = true;
			startTime = getCurrentTime();
			targetLowerBound = null;
			targetUpperBound = null;
			targetTranslator = null;
			// System.out.println("TRANSITION IS STARTED");
		}
		currentNumberOfPlayers = numberOfPlayers;

		float viewportWidth, viewportHeight, physicsRatio;
		Vec2 lowerBound, upperBound, translator;

		float maxXDistance = 0;
		float maxYDistance = 0;
		Vec2 center = new Vec2();

		Iterator<PlayerNumber> iiterator = players.keySet().iterator();
		while (iiterator.hasNext()) {

			PlayerNumber i = iiterator.next();
			if (!players.get(i).isAlive())
				continue;

			float x = players.get(i).getPosX();
			float y = players.get(i).getPosY();

			center.addLocal(x, y);

			Iterator<PlayerNumber> jiterator = players.keySet().iterator();
			while (jiterator.hasNext()) {

				PlayerNumber j = jiterator.next();

				if (i.equals(j) || !players.get(j).isAlive()) {
					continue;
				}

				float x2 = players.get(j).getPosX();
				float y2 = players.get(j).getPosY();

				float currentXDistance = Math.abs(x - x2);
				float currentYDistance = Math.abs(y - y2);

				if (maxXDistance == 0) {
					maxXDistance = currentXDistance;
				} else if (maxXDistance < currentXDistance) {
					maxXDistance = currentXDistance;
				}
				if (maxYDistance == 0) {
					maxYDistance = currentYDistance;
				} else if (maxYDistance < currentYDistance) {
					maxYDistance = currentYDistance;
				}
			}
		}

		center.mulLocal(1f / numberOfPlayers);

		float floorX = center.x + (maxXDistance / 2);
		
		iiterator = players.keySet().iterator();
		while (iiterator.hasNext()) {

			PlayerNumber i = iiterator.next();
			if (!players.get(i).isAlive())
				continue;

			float x = players.get(i).getPosX();
			float y = players.get(i).getPosY();
			
			float x2 = floorX;
			float y2 = 0;

			float currentXDistance = Math.abs(x - x2);
			float currentYDistance = Math.abs(y - y2);

			if (maxXDistance == 0) {
				maxXDistance = currentXDistance;
			} else if (maxXDistance < currentXDistance) {
				maxXDistance = currentXDistance;
			}
			if (maxYDistance == 0) {
				maxYDistance = currentYDistance;
			} else if (maxYDistance < currentYDistance) {
				maxYDistance = currentYDistance;
			}
			
		}
		
		center.addLocal(new Vec2(2, 0));
		
		if (maxYDistance <= maxXDistance * 0.5f) { // Threshold indicating when
													// it is good to start
													// calculating height first.
													// Measured in meters.

			viewportWidth = maxXDistance + 10;// + 15;//+ 30;
			physicsRatio = GameSettings.TARGET_WIDTH / viewportWidth;
			viewportHeight = GameSettings.TARGET_HEIGHT / physicsRatio;

			if (!transitioning) {

				if (!calculateWidthFirst) {
					transitioning = true;
					startTime = getCurrentTime();
					// System.out.println("TRANSITION IS STARTED");
				}
				calculateWidthFirst = true;
			}
		} else {

			viewportHeight = maxYDistance + 6;// + 9;//+ 18;
			physicsRatio = GameSettings.TARGET_HEIGHT / viewportHeight;
			viewportWidth = GameSettings.TARGET_WIDTH / physicsRatio;

			if (!transitioning) {

				if (calculateWidthFirst) {
					transitioning = true;
					startTime = getCurrentTime();
					// System.out.println("TRANSITION IS STARTED");
				}
				calculateWidthFirst = false;
			}
		}

		lowerBound = new Vec2(center.x - viewportWidth / 2, center.y - viewportHeight / 2);
		upperBound = new Vec2(center.x + viewportWidth / 2, center.y + viewportHeight / 2);
		translator = new Vec2(-physicsRatio * (center.x - viewportWidth / 2),
		physicsRatio * (center.y - viewportHeight / 2));

		if (!transitioning) {
			currentLowerBound = lowerBound;
			currentUpperBound = upperBound;
			currentTranslator = translator;
			currentPhysicsRatio = physicsRatio;
		} else {
			targetLowerBound = lowerBound;
			targetUpperBound = upperBound;
			targetTranslator = translator;
			targetPhysicsRatio = physicsRatio;
		}

		if (currentLowerBound == null) {
			currentLowerBound = targetLowerBound;
			currentUpperBound = targetUpperBound;
			currentTranslator = targetTranslator;
			currentPhysicsRatio = targetPhysicsRatio;
		} else if (targetLowerBound == null) {
			targetLowerBound = currentLowerBound;
			targetUpperBound = currentUpperBound;
			targetTranslator = currentTranslator;
			targetPhysicsRatio = currentPhysicsRatio;
		}

		if (transitioning) {

			float currentTime = getCurrentTime();
			float elapsedTime = currentTime - startTime;
			float reachedPercentage = elapsedTime / transitionDuration;

			if (reachedPercentage >= 1) {

				// System.out.println("TRANSITION IS OVER");
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
			} else {

				lowerBound = new Vec2(currentLowerBound);
				lowerBound.addLocal(targetLowerBound.sub

				(currentLowerBound).mul(reachedPercentage));

				upperBound = new Vec2(currentUpperBound);
				upperBound.addLocal(targetUpperBound.sub

				(currentUpperBound).mul(reachedPercentage));

				translator = new Vec2(currentTranslator);
				translator.addLocal(targetTranslator.sub

				(currentTranslator).mul(reachedPercentage));

				physicsRatio = currentPhysicsRatio;
				physicsRatio += (targetPhysicsRatio - currentPhysicsRatio)

				* reachedPercentage;
			}
		}

		if (GameSettings.PROFILE_GAME_CAMERA) {
			Log.d("Profling", "Calculate Viewport: "
					+ (System.nanoTime() / 1000000

					- time));
			time = System.nanoTime() / 1000000;
		}

		gameCanvas.setPhysicsRatio(physicsRatio);

		drawBackground(gameCanvas, center);

		if (GameSettings.PROFILE_GAME_CAMERA) {
			Log.d("Profling", "Draw Background: "
					+ (System.nanoTime() / 1000000 -

					time));
			time = System.nanoTime() / 1000000;
		}

		gameCanvas.saveState();

		gameCanvas.translate(translator.x, translator.y);

		List<Entity> entities = getPhysicalEntitiesToBeDrawn(lowerBound, upperBound);

		// Sort based on layer
		Collections.sort(entities, Layer.getComparator());
		
		for (Entity entity : entities) {
			entity.render(gameCanvas, timeElapsed);
		}

		if (GameSettings.PROFILE_GAME_CAMERA) {
			Log.d("Profling", "Draw Entities: " + (System.nanoTime() / 1000000 -

			time));
			time = System.nanoTime() / 1000000;
		}

		gameCanvas.restoreState();

	}

	private void drawBackground(GLCanvas gameCanvas, Vec2 center) {

		backgroundId = R.drawable.repeatable_background;

		int backgroundImageWidth = ImageLoader.checkBitmapSize(backgroundId)[0];
		int backgroundImageHeight = ImageLoader.checkBitmapSize(backgroundId)[1];

		float pos_rel_to_map = (center.x / 199.74f);
		if (pos_rel_to_map < 0) {
			pos_rel_to_map = 0;
		}
		if (pos_rel_to_map > 1) {
			pos_rel_to_map = 1;
		}

		if (backgroundImageWidth > GameSettings.TARGET_WIDTH &&

		backgroundImageHeight > GameSettings.TARGET_HEIGHT) {

			float factor = (float) Math.ceil((GameSettings.TARGET_HEIGHT /

			backgroundImageHeight));
			float backgroundWidth = backgroundImageWidth * factor;
			float backgroundHeight = backgroundImageHeight * factor;

			if (backgroundWidth < GameSettings.TARGET_WIDTH) {
				factor = (float) Math.ceil(GameSettings.TARGET_WIDTH /

				backgroundWidth);
				backgroundWidth *= factor;
				backgroundHeight *= factor;
			}

			RectF displayRect = new RectF(0f, 0f, backgroundWidth,

			backgroundHeight);

			int translate_x = (int) (pos_rel_to_map * (backgroundWidth -

			GameSettings.TARGET_WIDTH));
			int translate_y = 0;

			Rect showRect = new Rect(translate_x, translate_y, translate_x
					+ (int) backgroundWidth, translate_y +

			(int) backgroundHeight);

			if (GameSettings.PROFILE_GAME_CAMERA) {
				Log.d("Profiling", "Calculate Background: " +

				(System.nanoTime() / 1000000 - time));
				time = System.nanoTime() / 1000000;
			}

			gameCanvas.fillScreen(255, 255, 255, 255);
			gameCanvas.drawBitmap(backgroundId, showRect, displayRect, false);

		} else {

			RectF displayRect = new RectF(0f, 0f, GameSettings.TARGET_WIDTH,

			GameSettings.TARGET_HEIGHT);

			int backgroundHeight = backgroundImageHeight;
			int backgroundWidth = backgroundHeight * (int)

			(GameSettings.TARGET_WIDTH / GameSettings.TARGET_HEIGHT);

			int translate_x = (int) (pos_rel_to_map * (backgroundImageWidth -

			backgroundWidth));
			int translate_y = 0;

			Rect showRect = new Rect(translate_x, translate_y, translate_x +

			backgroundWidth, backgroundHeight + translate_y);

			if (GameSettings.PROFILE_GAME_CAMERA) {
				Log.d("Profiling", "Calculate Background: " +

				(System.nanoTime() / 1000000 - time));
				time = System.nanoTime() / 1000000;
			}

			gameCanvas.fillScreen(255, 255, 255, 255);
			gameCanvas.drawBitmap(backgroundId, showRect, displayRect, false);

		}
	}

	private List<Entity> getPhysicalEntitiesToBeDrawn(Vec2 lowerBound, Vec2 
			upperBound) {

		final List<Entity> entities = new ArrayList<Entity>();

		PhysicalWorld
				.getInstance()
				.getWorld()
				.queryAABB(
						new QueryCallback() {

							@Override
							public boolean reportFixture(Fixture fixture) {

								Object e = fixture.getBody().getUserData();
								if (e != null) {

									Entity entity = (Entity) e;
									entities.add(entity);
								}
								return true;
							}
						},
						new AABB(lowerBound.addLocal(-10, -10), upperBound
								.addLocal(10, 10))); // TODO: Check this..

		Body b = PhysicalWorld.getInstance().getWorld().getBodyList();
		while (b != null) {
			Object uData = b.getUserData();
			if (uData != null && ((Entity) uData).getType() != null
					&& ((Entity) uData).getType().equals(EntityType.PULLEY)) {
				entities.add((Entity) uData);
			}
			b = b.getNext();
		}

		return entities;
	}

	public void render(final GLCanvas canvas, final float timeElapsed) {
		determineViewport(canvas, timeElapsed);
	}

	private long getCurrentTime() {

		return System.nanoTime() / 1000000;
	}

	public void setEntitiesToWatch(SparseArray<Watchable> toWatch) {
	}

}
