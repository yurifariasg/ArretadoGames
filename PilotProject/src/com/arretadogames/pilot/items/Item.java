package com.arretadogames.pilot.items;

import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.world.GameWorld;

public interface Item extends Steppable {
    
    public int getImageDrawable();
    public String getName();
    
    public void activate(Player owner, GameWorld world); // Maybe GameWorld is too much ?
    public ItemType getType();
    public boolean isActive();
    public int getColor();
    
}
