package com.arretadogames.pilot.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.LayerEntity.Layer;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.util.Profiler;
import com.arretadogames.pilot.util.Profiler.ProfileType;
import com.arretadogames.pilot.world.GameWorld;

public class GameCamera {

	private static GameWorld gameWorld = null;
	private int repeatableBackgroundId;
	private int finalSliceBackgroundId;

	private boolean calculateWidthFirst;
	private int currentNumberOfPlayers;

	private Vec2 currentLowerBound;
	private Vec2 currentUpperBound;
	private Vec2 currentTranslator;
	private float currentPhysicsRatio;

	private boolean transitioning;
	private float transitionDuration; // Measured in milliseconds.
	private long startTime;

	private MovingBackground movingBackground;
	private List<Entity> entitiesToDraw;

	private enum TransitionTrigger {
		NONE, PLAYER_NUM_CHANGED, VIEWPORT_SIDE_PRIORITY_CHANGED;
	}

	private TransitionTrigger transitionTrigger = TransitionTrigger.NONE;

	private Vec2 targetLowerBound;
	private Vec2 targetUpperBound;
	private Vec2 targetTranslator;
	private float targetPhysicsRatio;

	// FOR NOW THESE ARE CONSTANTS
	private static final float NUMBER_OF_REPETITIONS = 2;
	private static final int END_POSITION = 1600;

	private float initialX = -1; // Initial X position from players
	private float flagX = -1;

	public GameCamera(GameWorld world, int backgroundId) {
		this(world, 1000f);// Default is 1000 milliseconds
		this.repeatableBackgroundId = backgroundId;

		movingBackground = new MovingBackground(R.drawable.mountains_repeatable);
		entitiesToDraw = new ArrayList<Entity>();
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

		Profiler.initTick(ProfileType.RENDER);

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
			transitionTrigger = TransitionTrigger.PLAYER_NUM_CHANGED;
			// System.out.println("TRANSITION IS STARTED");
		}
		currentNumberOfPlayers = numberOfPlayers;

		float viewportWidth, viewportHeight, physicsRatio;
		Vec2 lowerBound, upperBound, translator;

		float maxXDistance = 0;
		float maxYDistance = 0;
		float minX = Float.MAX_VALUE;
		Vec2 center = new Vec2();

		Iterator<PlayerNumber> iiterator = players.keySet().iterator();
		while (iiterator.hasNext()) {

			PlayerNumber i = iiterator.next();
			if (!players.get(i).isAlive())
				continue;

			float x = players.get(i).getPosX();
			float y = players.get(i).getPosY();
			
			if (minX > x)
				minX = x;

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
		
		float fireDistance = minX - gameWorld.getFire().getPosX();

		// This function says:
		// If the distance is negative (fire is in front of players), the firePositionWeight is 1 (maximum)
		// If the distance is higher than 5, the firePositionWeight is 0 (insignificant)
		// If it is between 0 and 5, it gets between 0 and 1, higher if it gets closer to 0
//		float k;
//		if (fireDistance < 0)
//			k = 1;
//		else if (fireDistance > 5)
//			k = 0;
//		else
//			k = 1 - (fireDistance - 1f) / 4f;
			
		// Add fire
		center.mulLocal(1f / numberOfPlayers);
		
//		center.addLocal(gameWorld.getFire().getPosX() / (2 + fireDistance), 0);
//		center.addLocal(gameWorld.getFire().getPosX() * k, 0);
//		if (k > 0) {
//			k = 2f * k;
//			if (k <= 1)
//				k = 1;
//			center.mulLocal(1f / (2f *k));
//		}
		
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
					transitionTrigger = TransitionTrigger.VIEWPORT_SIDE_PRIORITY_CHANGED;
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
					transitionTrigger = TransitionTrigger.VIEWPORT_SIDE_PRIORITY_CHANGED;
					// System.out.println("TRANSITION IS STARTED");
				}
				calculateWidthFirst = false;
			}
		}

		lowerBound = new Vec2(center.x - viewportWidth / 2, center.y
				- viewportHeight / 2);
		upperBound = new Vec2(center.x + viewportWidth / 2, center.y
				+ viewportHeight / 2);
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

				transitionTrigger = TransitionTrigger.NONE;
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
				lowerBound.addLocal(targetLowerBound.sub(currentLowerBound)
						.mul(reachedPercentage));

				upperBound = new Vec2(currentUpperBound);
				upperBound.addLocal(targetUpperBound.sub(currentUpperBound)
						.mul(reachedPercentage));

				translator = new Vec2(currentTranslator);
				translator.addLocal(targetTranslator.sub(currentTranslator)
						.mul(reachedPercentage));

				physicsRatio = currentPhysicsRatio;
				physicsRatio += (targetPhysicsRatio - currentPhysicsRatio)
						* reachedPercentage;
			}
		}

		Profiler.profileFromLastTick(ProfileType.RENDER, "Calculate Viewport");
		Profiler.initTick(ProfileType.RENDER);

		gameCanvas.setPhysicsRatio(physicsRatio);

		if (GameSettings.ACTIVATE_FIRE) {
			float cameraWidth = upperBound.x - lowerBound.x;
			float velocityIncrease = 0;
			if (cameraWidth >= 11) {
				velocityIncrease = (cameraWidth - 11) / 20.0f;
			}
			float newVelocity = gameWorld.getFire().getBaseVelocity()
					* (1 + velocityIncrease);
			gameWorld.getFire().setCurrentVelocity(newVelocity);
		}

		// Draw Background
		if (GameSettings.USE_OLD_BACKGROUND) {
			gameCanvas.fillScreen(255, 255, 255, 255);
			if (transitionTrigger == TransitionTrigger.PLAYER_NUM_CHANGED
					|| transitionTrigger == TransitionTrigger.NONE) {
				float pos = upperBound.x;
				// float pos = lowerBound.x;// + (upperBound.x * (0.5f));
				drawBackground(gameCanvas, pos);
			} else {
				float pos = targetUpperBound.x;
				// float pos = targetLowerBound.x;// + (targetUpperBound.x *
				// (0.5f));
				drawBackground(gameCanvas, pos);
			}
		} else {
			// Draw Sky
			int topSky = Color.rgb(0, 134, 168);
			int bottomSky = Color.rgb(277, 251, 145);
	
			gameCanvas.drawRect(0, 0, 0, GameSettings.TARGET_HEIGHT,
					GameSettings.TARGET_WIDTH, GameSettings.TARGET_HEIGHT,
					GameSettings.TARGET_WIDTH, 0, topSky, bottomSky, bottomSky,
					topSky);
	
			movingBackground.render(gameCanvas, 0, GLCanvas.physicsRatio, center.x,
					center.y, initialX, flagX, translator);
		}

		Profiler.profileFromLastTick(ProfileType.RENDER, "Draw background");
		Profiler.initTick(ProfileType.RENDER);

		gameCanvas.saveState();

		gameCanvas.translate(translator.x, translator.y);

		Collection<Entity> entities = getPhysicalEntitiesToBeDrawn(lowerBound,
				upperBound);
		entitiesToDraw.addAll(entities);

		// Sort based on layer
		Collections.sort(entitiesToDraw, Layer.getComparator());

		for (Entity entity : entitiesToDraw) {
			entity.render(gameCanvas, timeElapsed);
		}

		entitiesToDraw.clear();

		Profiler.profileFromLastTick(ProfileType.RENDER, "Draw entities");
		Profiler.initTick(ProfileType.RENDER);

		if (GameSettings.DRAW_PHYSICS)
			PhysicalWorld.getInstance().render(gameCanvas, timeElapsed);

		gameCanvas.restoreState();
	}

	private void drawBackground(GLCanvas gameCanvas, float pos) {

		repeatableBackgroundId = R.drawable.editing_background;
		finalSliceBackgroundId = R.drawable.final_slice_background;

		int backgroundImageWidth = ImageLoader
				.checkBitmapSize(repeatableBackgroundId)[0];
		int backgroundImageHeight = ImageLoader
				.checkBitmapSize(repeatableBackgroundId)[1];

		float reached = (pos / gameWorld.getFlagPos());
		if (reached < 0) {
			reached = 0;
		} else if (reached > 1) {
			reached = 1;
		}

		float factor = (float) Math
				.ceil((GameSettings.TARGET_HEIGHT / backgroundImageHeight));
		float backgroundWidth = backgroundImageWidth * factor;
		float backgroundHeight = backgroundImageHeight * factor; // @yuri: This
																	// wil
																	// always be
																	// equals to
																	// TARGET_HEIGHT,
																	// isnt it ?

		// if (backgroundWidth < GameSettings.TARGET_WIDTH) {
		// factor = (float) Math.ceil(GameSettings.TARGET_WIDTH /
		// backgroundWidth);
		// backgroundWidth *= factor;
		// backgroundHeight *= factor;
		// }

		float actualEndPos = (backgroundWidth * (NUMBER_OF_REPETITIONS - 1))
				+ END_POSITION;

		int translate_x = (int) (reached * ((backgroundWidth * NUMBER_OF_REPETITIONS) - GameSettings.TARGET_WIDTH));
		int translate_y = 0;

		float endPosRelToScreen = 0;

		if ((translate_x + (int) GameSettings.TARGET_WIDTH) < actualEndPos) {
			endPosRelToScreen = 1;
		} else if (translate_x > actualEndPos) {
			endPosRelToScreen = 0;
		} else {
			endPosRelToScreen = (actualEndPos - translate_x)
					/ GameSettings.TARGET_WIDTH;
		}

		RectF displayRectRepeatablePart = new RectF(0f, 0f,
				GameSettings.TARGET_WIDTH * endPosRelToScreen, backgroundHeight);

		RectF displayRectFinalPart = new RectF(
				(GameSettings.TARGET_WIDTH * endPosRelToScreen), 0f,
				GameSettings.TARGET_WIDTH, backgroundHeight);

		Rect showRectRepeatablePart = new Rect(
				translate_x,
				translate_y,
				(translate_x + (int) (GameSettings.TARGET_WIDTH * endPosRelToScreen)),
				(translate_y + (int) backgroundHeight));

		Rect showRectFinalPart = new Rect(
				((translate_x - (int) actualEndPos) + (int) (GameSettings.TARGET_WIDTH * endPosRelToScreen)),
				translate_y, (translate_x - (int) actualEndPos)
						+ (int) (GameSettings.TARGET_WIDTH), translate_y
						+ (int) backgroundHeight);

		Profiler.profileFromLastTick(ProfileType.RENDER, "Calculate background");
		Profiler.initTick(ProfileType.RENDER);

		gameCanvas.fillScreen(255, 255, 255, 255);

		gameCanvas.drawBitmap(repeatableBackgroundId, showRectRepeatablePart,
				displayRectRepeatablePart);

		gameCanvas.drawBitmap(finalSliceBackgroundId, showRectFinalPart,
				displayRectFinalPart);
	}

	private Collection<Entity> getPhysicalEntitiesToBeDrawn(Vec2 lowerBound,
			Vec2 upperBound) {

		final Set<Entity> entities = new HashSet<Entity>();

		PhysicalWorld.getInstance().getWorld().queryAABB(new QueryCallback() { // TODO:
																				// create
																				// QueryCallback
																				// just
																				// once

					@Override
					public boolean reportFixture(Fixture fixture) {

						Object e = fixture.getBody().getUserData();
						if (e != null) {

							Entity entity = (Entity) e;
							entities.add(entity);
						}
						return true;
					}
				}, new AABB(lowerBound, upperBound)); // TODO: create AABB just
														// once

		return entities;
	}

	public void render(final GLCanvas canvas, final float timeElapsed) {
		if (flagX == -1 || initialX == -1) {
			flagX = gameWorld.getFlagPos();
			initialX = gameWorld.getPlayers().get(PlayerNumber.ONE).getPosX();
		}

		determineViewport(canvas, timeElapsed);
	}

	private long getCurrentTime() {
		return System.nanoTime() / 1000000;
	}

}
