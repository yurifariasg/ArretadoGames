package com.arretadogames.pilot.loading;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.util.Log;

import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class LoadManager {
	
	private Set<LoadableGLObject> loadedResources;
	private LoadableGLObject[] objectsToLoad;
	private LoadableGLObject[] objectsToRemove;
	
	
	private ArrayList<LoadableGLObject> extraResources;
	
	// Hide Constructor (Singleton)
	private LoadManager() {
		loadedResources = new HashSet<LoadableGLObject>();
		extraResources = new ArrayList<LoadableGLObject>();
	}
	
	public void addExtraObject(LoadableGLObject object) {
		if (object != null)
			extraResources.add(object);
	}
	
	public void addExtraObjects(List<LoadableGLObject> objects) {
		if (objects != null)
			for (LoadableGLObject object : objects)
				addExtraObject(object);
	}
	
	public void prepareLoad(GameState[] states) {
		
		// Add all resources to be needed
		HashSet<LoadableGLObject> neededResources = new HashSet<LoadableGLObject>();
		for (int i = 0 ; i < states.length ; i++)
			neededResources.addAll(ResourcesManager.getResourcesFrom(states[i]));
		
		neededResources.addAll(extraResources);
		extraResources.clear();
		
		// get what is not in loaded and is in needed - those should be loaded
		List<LoadableGLObject> resourcesToLoad = new ArrayList<LoadableGLObject>();
		for (LoadableGLObject resource : neededResources)
			if (!loadedResources.contains(resource))
				resourcesToLoad.add(resource);

		// get what is in loaded and is not in needed - those should be removed
		List<LoadableGLObject> resourcesToRemove = new ArrayList<LoadableGLObject>();
		for (LoadableGLObject resource : loadedResources)
			if (!neededResources.contains(resource))
				resourcesToRemove.add(resource);
		
		// Set Variables
		this.objectsToLoad = new LoadableGLObject[resourcesToLoad.size()];
		this.objectsToRemove = new LoadableGLObject[resourcesToRemove.size()];
		
		resourcesToLoad.toArray(objectsToLoad);
		resourcesToRemove.toArray(objectsToRemove);
	}
	
	public void swapTextures(final LoadFinisherCallBack callback, GLCanvas glCanvas) {
		if (objectsToLoad == null || objectsToRemove == null) {
			Log.e("LoadManager.SwapTextures", "Tried to swap textures without calling prepareLoad() or game failed to load");
			return;
		}
		
		for (int i = 0 ; i < objectsToRemove.length ; i++) {
			loadedResources.remove(objectsToRemove[i]);
		}
		
		glCanvas.removeTextures(objectsToRemove);
		
		for (int i = 0 ; i < objectsToLoad.length ; i++) {
			glCanvas.loadObject(objectsToLoad[i]);
			loadedResources.add(objectsToLoad[i]);
		}
		
		callback.onLoadFinished(true);
		
		objectsToRemove = null;
		objectsToLoad = null;
	}
	
	/**
	 * Callback to be called when the LoadManager finishes loading
	 */
	public interface LoadFinisherCallBack {
		public void onLoadFinished(boolean error);
	}
	
	
	// Singleton Properties
	private static LoadManager loadManager;
	public static LoadManager getInstance() {
		if (loadManager == null)
			loadManager = new LoadManager();
		return loadManager;
	}
}
