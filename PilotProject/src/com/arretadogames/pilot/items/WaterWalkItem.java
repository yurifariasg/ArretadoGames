package com.arretadogames.pilot.items;

import android.graphics.Color;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.world.GameWorld;

public class WaterWalkItem implements Item {
    
    private static final float WATER_WALK_DURATION = 15; // in seconds
    
    private ItemType type = ItemType.WaterWalk;
    
    private boolean isActive;
    private Player owner;
    
    private float remainingTime;
    
    public WaterWalkItem() {
        this.isActive = false;
    }
    
    @Override
    public int getImageDrawable() {
        return R.drawable.waterwalk_item;
    }

    @Override
    public String getName() {
        return type.getName();
    }

    @Override
    public void activate(Player owner, GameWorld world) {
        if (!isActive() && !owner.isOnWater()) {
            this.remainingTime = WATER_WALK_DURATION;
            this.isActive = true;
            this.owner = owner;
        }
    }
    
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public void step(float timeElapsed) {
        if (isActive()) {
            if (remainingTime <= 0) {
                this.isActive = false; // It is over!
                this.owner.setItem(null);
            } else {
                remainingTime -= timeElapsed;
            }
        }
    }

    @Override
    public ItemType getType() {
        return type;
    }
    
    @Override
    public int getColor() {
        return Color.BLUE;
    }

}
