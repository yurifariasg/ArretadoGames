package com.arretadogames.pilot.loading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.arretadogames.pilot.render.opengl.GLCanvas;

public class LoadManager {
	
	private HashMap<LoadableType, Set<LoadableGLObject>> loadedEntities;
	private HashMap<LoadableType, Set<LoadableGLObject>> loadingEntities;
	
	// Hide Constructor (Singleton)
	private LoadManager() {
		loadedEntities = new HashMap<LoadableType , Set<LoadableGLObject>>();
		loadingEntities = new HashMap<LoadableType, Set<LoadableGLObject>>();
		
		loadedEntities.put(LoadableType.TEXTURE, new HashSet<LoadableGLObject>());
		loadedEntities.put(LoadableType.FONT, new HashSet<LoadableGLObject>());
		loadingEntities.put(LoadableType.TEXTURE, new HashSet<LoadableGLObject>());
		loadingEntities.put(LoadableType.FONT, new HashSet<LoadableGLObject>());
	}
	
	public boolean addLoadableObject(LoadableGLObject object) {
		return loadingEntities.get(object.getType()).add(object);
	}
	
	private List<LoadableGLObject> getObjectsToRemove() {
		List<LoadableGLObject> objectsToRemove = new ArrayList<LoadableGLObject>();
		
		for (LoadableType type : loadingEntities.keySet()) {
			for (LoadableGLObject object : loadedEntities.get(type))
				if (!(loadingEntities.get(type).contains(object)))
					objectsToRemove.add(object);
		}
		
		return objectsToRemove;
	}
	
	private List<LoadableGLObject> getObjectsToAdd() {
		List<LoadableGLObject> objectsToRemove = new ArrayList<LoadableGLObject>();
		
		for (LoadableType type : loadingEntities.keySet()) {
			for (LoadableGLObject object : loadingEntities.get(type))
				if (!(loadedEntities.get(type).contains(object)))
					objectsToRemove.add(object);
		}
		
		return objectsToRemove;
	}
	
	public void swapTextures(final LoadFinisherCallBack callback, GLCanvas glCanvas) {
		List<LoadableGLObject> objectsToRemove = getObjectsToRemove();
		List<LoadableGLObject> objectsToAdd = getObjectsToAdd();
		
		int[] toRemove = new int[objectsToRemove.size()];
		for (int i = 0 ; i < toRemove.length ; i++)
			toRemove[i] = objectsToRemove.get(i).getGlId();
		
		glCanvas.removeTextures(toRemove);
		
		for (LoadableGLObject object : objectsToAdd)
			glCanvas.loadObject(object);
		
		callback.onLoadFinished(true);
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
