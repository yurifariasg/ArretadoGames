package com.arretadogames.pilot.render;

import java.util.ArrayList;
import java.util.Collection;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class GameCamera {

	private int backgroundId;
	
	private boolean calculateWidthFirst;
	
	private int currentNumEnabledEntitiesToWatch;
	private SparseArray<Watchable> entitiesToWatch;

	private long time;
	
	public GameCamera(int backgroundId){
		
		this(null, backgroundId);
	}
	
	public GameCamera(SparseArray<Watchable> toWatch, int backgroundId){

		calculateWidthFirst = true;
		entitiesToWatch = toWatch;
		currentNumEnabledEntitiesToWatch = getNumberOfEnabledEntitiesToWatch();
		this.backgroundId = backgroundId;
	}

	public void setEntitiesToWatch(SparseArray<Watchable> toWatch){
		
		entitiesToWatch = toWatch;
		currentNumEnabledEntitiesToWatch = getNumberOfEnabledEntitiesToWatch();
	}
	
	private int getNumberOfEnabledEntitiesToWatch() {
		
		if ( entitiesToWatch == null ){
			return -1;
		}
		
		int alive = 0;
		
		for ( int i=0; i<entitiesToWatch.size(); i++ ){
			if ( entitiesToWatch.get(entitiesToWatch.keyAt(i)).isEnabled() ){
				alive++;
			}
		}
		return alive;
	}

	private void handleCurrentNumberOfEntitiesToWatch() {

		int numEnabledEntitiesToWatch = getNumberOfEnabledEntitiesToWatch();
		
		if ( numberOfEntitiesToWatchHasChanged(numEnabledEntitiesToWatch) ){
			TransitionManager.startTransition();
			Viewport.resetTargetViewport();
		}
		currentNumEnabledEntitiesToWatch = numEnabledEntitiesToWatch;
	}

	private boolean numberOfEntitiesToWatchHasChanged(int numEnabledEntitiesToWatch){

		return numEnabledEntitiesToWatch != currentNumEnabledEntitiesToWatch;
	}
	
	public class EarlyConstraints{
		
		public float maxXDistance, maxYDistance;
		public Vec2 center;
		
		public EarlyConstraints(){
			maxXDistance = 0;
			maxYDistance = 0;
			center = new Vec2();
		}
	}
	
	private EarlyConstraints determineEarlyConstraints(){

		EarlyConstraints constraints = new EarlyConstraints();
		
		for ( int i=0; i<entitiesToWatch.size(); i++ ){
			
			int key = entitiesToWatch.keyAt(i);
			Watchable entity = entitiesToWatch.get(key);
			if ( entity.isDisabled() ){
				continue;
			}
			
			float x = entity.getPosX();
			float y = entity.getPosY();

			constraints.center.addLocal(x, y);

			for ( int j=0; j<entitiesToWatch.size(); j++ ){
			
				int key2 = entitiesToWatch.keyAt(j);
				Watchable otherEntity = entitiesToWatch.get(key2);
				
				if ( key == key2 || otherEntity.isDisabled() ){
					continue;
				}

				float x2 = otherEntity.getPosX();
				float y2 = otherEntity.getPosY();

				float currentXDistance = Math.abs(x - x2);
				float currentYDistance = Math.abs(y - y2);

				if ( constraints.maxXDistance == 0 ){
					constraints.maxXDistance = currentXDistance;
				}
				else if ( constraints.maxXDistance < currentXDistance ){
					constraints.maxXDistance = currentXDistance;
				}
				if ( constraints.maxYDistance == 0 ){
					constraints.maxYDistance = currentYDistance;
				}
				else if ( constraints.maxYDistance < currentYDistance ){
					constraints.maxYDistance = currentYDistance;
				}
			}
		}

		constraints.center.mulLocal(1f / currentNumEnabledEntitiesToWatch);
		return constraints;
	}

	public class LateConstraints{
		
		public float viewportWidth, viewportHeight, physicsRatio;
		
		public LateConstraints(){
			viewportWidth = 0;
			viewportHeight = 0;
			physicsRatio = 0;
		}
	}
	
	private LateConstraints determineLateConstraints(EarlyConstraints earlyConstraints) {

		LateConstraints constraints = new LateConstraints();
		
		if ( earlyConstraints.maxYDistance <= earlyConstraints.maxXDistance * 0.5f ){
			//Threshold indicating when it is good to start calculating height first. Measured in meters.

			constraints.viewportWidth = earlyConstraints.maxXDistance +10;//+ 15;//+ 30;
			constraints.physicsRatio = DisplaySettings.TARGET_WIDTH / constraints.viewportWidth;
			constraints.viewportHeight = DisplaySettings.TARGET_HEIGHT / constraints.physicsRatio;

			if ( TransitionManager.transitionWasNotHappening() ){

				if ( !calculateWidthFirst ){
					TransitionManager.startTransition();
				}
				calculateWidthFirst = true;
			}
		}
		else{

			constraints.viewportHeight = earlyConstraints.maxYDistance +6;//+ 9;//+ 18;
			constraints.physicsRatio = DisplaySettings.TARGET_HEIGHT / constraints.viewportHeight;
			constraints.viewportWidth = DisplaySettings.TARGET_WIDTH / constraints.physicsRatio;

			if ( TransitionManager.transitionWasNotHappening() ){

				if ( calculateWidthFirst ){
					TransitionManager.startTransition();
				}
				calculateWidthFirst = false;
			}
		}
		return constraints;
	}

	private Collection<Entity> getPhysicalEntitiesToBeDrawn(Vec2 lowerBound, Vec2 upperBound) {

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
	
	private void drawBackground(GLCanvas gameCanvas, Vec2 center) {
		
		// @yuri: these calculations shouldn't be done every frame..
		int backgroundImageWidth = ImageLoader.checkBitmapSize(backgroundId)[0];
		int backgroundImageHeight = ImageLoader.checkBitmapSize(backgroundId)[1];
		
		float pos_rel_to_map = ( center.x / 199.74f );
		if ( pos_rel_to_map < 0 ){
			pos_rel_to_map = 0;
		}
		if ( pos_rel_to_map > 1 ){
			pos_rel_to_map = 1;
		}
		
		if ( backgroundImageWidth > DisplaySettings.TARGET_WIDTH && backgroundImageHeight > DisplaySettings.TARGET_HEIGHT ){

			float factor = (float) Math.ceil((DisplaySettings.TARGET_HEIGHT / backgroundImageHeight));
			float backgroundWidth = backgroundImageWidth * factor;
			float backgroundHeight = backgroundImageHeight * factor;

			if ( backgroundWidth < DisplaySettings.TARGET_WIDTH ){
				factor = (float) Math.ceil(DisplaySettings.TARGET_WIDTH / backgroundWidth);
				backgroundWidth *= factor;
				backgroundHeight *= factor;
			}

			RectF displayRect = new RectF(0f, 0f, backgroundWidth, backgroundHeight);

			int translate_x = (int) (pos_rel_to_map * ( backgroundWidth - DisplaySettings.TARGET_WIDTH ));
			int translate_y = 0;

			Rect showRect = new Rect(translate_x, translate_y, 
					translate_x + (int)backgroundWidth, translate_y + (int)backgroundHeight);
			
			if (DisplaySettings.PROFILE_GAME_CAMERA) {
				Log.d("Profiling", "Calculate Background: " + (System.nanoTime()/1000000 - time));
				time = System.nanoTime() / 1000000;
			}
			
			gameCanvas.fillScreen(255, 255, 255, 255);
			gameCanvas.drawBitmap(backgroundId, showRect,
			displayRect, false);
			
		}
		else{
			
			RectF displayRect = new RectF(0f, 0f, DisplaySettings.TARGET_WIDTH, DisplaySettings.TARGET_HEIGHT);
			
			int backgroundHeight = backgroundImageHeight;
			int backgroundWidth = backgroundHeight * (int)(DisplaySettings.TARGET_WIDTH / DisplaySettings.TARGET_HEIGHT);
			
			int translate_x = (int) (pos_rel_to_map * ( backgroundImageWidth - backgroundWidth));
			int translate_y = 0;

			Rect showRect = new Rect(translate_x, translate_y, translate_x + backgroundWidth, backgroundHeight + translate_y);

			if (DisplaySettings.PROFILE_GAME_CAMERA) {
				Log.d("Profiling", "Calculate Background: " + (System.nanoTime()/1000000 - time));
				time = System.nanoTime() / 1000000;
			}
			
			gameCanvas.fillScreen(255, 255, 255, 255);
			gameCanvas.drawBitmap(backgroundId, showRect, displayRect, false);

		}
	}

	private void drawViewportEntities(GLCanvas gameCanvas, float timeElapsed, Viewport viewport) {

		gameCanvas.saveState();
		
		gameCanvas.translate(viewport.translator.x, viewport.translator.y);

		Collection<Entity> entities = getPhysicalEntitiesToBeDrawn(viewport.lowerBound, viewport.upperBound);

		for ( Entity entity : entities ){
			entity.render(gameCanvas, timeElapsed);
		}
		
		gameCanvas.restoreState();
	}
	
	private void drawViewportAndBackground(GLCanvas gameCanvas, float timeElapsed){
		
		if (DisplaySettings.PROFILE_GAME_CAMERA){
			time = System.nanoTime() / 1000000;
		}

		if ( entitiesToWatch == null ){
			return;
		}
		
		handleCurrentNumberOfEntitiesToWatch();

		EarlyConstraints earlyConstraints = determineEarlyConstraints();

		LateConstraints lateConstraints = determineLateConstraints(earlyConstraints);
		
		Viewport viewport = new Viewport(earlyConstraints, lateConstraints);
		
		if (DisplaySettings.PROFILE_GAME_CAMERA) {
			Log.d("Profling", "Calculate Viewport: " + (System.nanoTime()/1000000 - time));
			time = System.nanoTime() / 1000000;
		}

		gameCanvas.setPhysicsRatio(lateConstraints.physicsRatio);

		drawBackground(gameCanvas, earlyConstraints.center);
		
		if (DisplaySettings.PROFILE_GAME_CAMERA) {
			Log.d("Profling", "Draw Background: " + (System.nanoTime()/1000000 - time));
			time = System.nanoTime() / 1000000;
		}

		drawViewportEntities(gameCanvas, timeElapsed, viewport);

		if (DisplaySettings.PROFILE_GAME_CAMERA) {
			Log.d("Profling", "Draw Viewport Entities: " + (System.nanoTime()/1000000 - time));
			time = System.nanoTime() / 1000000;
		}

	}
	
	public void render(final GLCanvas canvas, final float timeElapsed) {
		drawViewportAndBackground(canvas, timeElapsed);
	}

}
