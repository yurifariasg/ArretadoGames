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
    
    BOX, COIN, FRUIT, PLAYER, ONEWAY_WALL, PULLEY, FLUID, BREAKABLE, LIANA, FLAG, LINE,
    P1, P2;
    
    public int toInt() {
        return ordinal();
    }
    
}
