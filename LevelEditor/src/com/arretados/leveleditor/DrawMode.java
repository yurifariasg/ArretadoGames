/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor;

/**
 *
 * @author Bruno
 */

public enum DrawMode {
	
    BOX("box"), GROUND("ground"), PLAYER("player"), FIRE("fire"),
    PULLEY("pulley"), ONEWAY_WALL("oneway_wall"), FLAG("finalflag"),
    COIN("coin"), FLUID("water"), BREAKABLE("breakable"), LIANA("liana"),
    SPIKE("spike"), HOLE("hole"),
    TREE("tree"), SHRUB("shrub"), GRASS("grass"),
    BOX_ITEM("boxitem");

    private String name;

    DrawMode(String name) {
            this.name = name;
    }
    
    @Override
    public String toString(){
            return this.name;
    }
    
    public int toInt() {
        return ordinal();
    }
	
}