package com.arretadogames.pilot.entities;

public enum PlayableItem {
	SUPER_JUMP(0), SUPER_STRENGTH(1), SUPER_SPEED(2), DOUBLE_JUMP(3); 

    public final int pId;

    private PlayableItem(int id) {
        this.pId = id;
    }
    
    public static PlayableItem forInt(int id) {
        for (PlayableItem item : values()) {
            if (item.pId == id) {
                return item;
            }
        }
        throw new IllegalArgumentException("Invalid Day id: " + id);
    }
}