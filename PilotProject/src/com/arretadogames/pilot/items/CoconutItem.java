package com.arretadogames.pilot.items;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import android.graphics.Color;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Coconut;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.world.GameWorld;

public class CoconutItem implements Item {
    
    private ItemType type = ItemType.Coconut;
    private Player owner;
    
    @Override
    public int getImageDrawable() {
        return R.drawable.coconut;
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
            
            AABB aabb = new AABB();
            
            while (f != null) {
            	aabb.combine(f.getAABB(0));
            	f = f.getNext();
            }
            
            Coconut coconut = new Coconut(aabb.upperBound.x, aabb.upperBound.y);
            world.getEntities().add(coconut);
            
            Vec2 impulse = new Vec2(0.9f, 0.4f);
            
            impulse.mulLocal(14);
            
            coconut.body.applyLinearImpulse(impulse, coconut.body.getWorldCenter());
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
        return Color.WHITE;
    }

    @Override
    public boolean isActive() {
        return false;
    }

}
