package com.arretadogames.pilot.entities.scenario;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Tree extends ScenarioEntity {
	
	private static int[] TREE_RESOURCES = {
		R.drawable.tree1 // We may change where this is currently located when we refactor sprites 
	};
	
	// Tree Sizes
	private static float[][] TREE_SIZES = { // {width, height}
		{3, 4}, // Tree Type 0
		{1, 4} // Tree Type 1
	};
	
	// Object Properties
	private int treeType;
	
	public Tree(float x, float y) {
		this(x, y, 0);
	}
	
	public Tree(float x, float y, int type) {
		super(x, y, TREE_SIZES[type][0], TREE_SIZES[type][1]);
		this.treeType = type;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		drawBasic(canvas, TREE_RESOURCES[treeType]);
	}

	@Override
	public EntityType getType() {
		return EntityType.TREE;
	}

	@Override
	public void setSprite(AnimationSwitcher sprite) {
		// TODO Remove this when sprites are refactored
	}
	
	@Override
	public int getLayerPosition() {
		return 10;
	}
}
