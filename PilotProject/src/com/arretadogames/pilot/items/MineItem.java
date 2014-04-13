package com.arretadogames.pilot.items;

import android.graphics.Color;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Mine;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.world.GameWorld;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

public class MineItem implements Item {
    
    // Add an small offset to avoid falling below the ground and hitting the player
    private static final Vec2 MINE_POSITION_OFFSET = new Vec2(-0.1f, 0.1f);
    
    private ItemType type = ItemType.Mine;
    private Player owner;
    
    @Override
    public int getImageDrawable() {
        return R.drawable.mine_item;
    }

    @Override
    public String getName() {
        return type.getName();
    }

    @Override
    public void activate(Player owner, GameWorld world) {
        if (!isActive() && !owner.isOnWater()) {
            this.owner = owner;
            this.owner.setItem(null);
            
            Fixture f = owner.body.getFixtureList();
            
            AABB aabb = null;
            
            while (f != null) {
                if (aabb == null) {
                    aabb = f.getAABB(0);
                } else {
                    aabb.combine(f.getAABB(0));
                }
            	f = f.getNext();
            }
            
            Mine mine = new Mine(aabb.lowerBound.x + MINE_POSITION_OFFSET.x,
                    aabb.lowerBound.y  + MINE_POSITION_OFFSET.y);
            world.getEntities().add(mine);
        }
    }
    
    @Override
    public void step(float timeElapsed) {
        if (isActive()) {
            
        }
    }

    @Override
    public ItemType getType() {
        return type;
    }
    
    @Override
    public int getColor() {
        return Color.GREEN;
    }

    @Override
    public boolean isActive() {
        return false;
    }

}
