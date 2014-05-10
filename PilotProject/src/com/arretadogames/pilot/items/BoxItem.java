package com.arretadogames.pilot.items;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.util.Assets;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.Random;

public class BoxItem extends Entity {
    
    private static final PhysicsRect BOX_SIZE = new PhysicsRect(0.8f, 0.8f);
    private static final PhysicsRect ITEM_IMAGE_SIZE = new PhysicsRect(0.6f, 0.6f);
    private static final int IMAGE = R.drawable.box_stopped;
    
    private Item item;
    private boolean isVisible;

    public BoxItem(float x, float y) {
        super(x, y);
        this.item = randomizeItem();
        this.isVisible = new Random().nextBoolean();
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(BOX_SIZE.width()/2, BOX_SIZE.height()/2);
        body.createFixture(shape,  0.5f).setFriction(0.8f);
        body.setType(BodyType.DYNAMIC);
        body.setFixedRotation(false);
        physRect = BOX_SIZE.clone();
    }
    
    public static Item randomizeItem() {
        ItemType[] items = ItemType.values();
        int randomIndex = new Random().nextInt(items.length);
        
        switch (items[randomIndex]) {
            case Coconut:
                return new CoconutItem();
            case Mine:
                return new MineItem();
            case WaterWalk:
                return new WaterWalkItem();
        }
        return null;
    }

    @Override
    public void render(GLCanvas canvas, float timeElapsed) {
        canvas.saveState();
        
        canvas.translatePhysics(body.getPosition().x, body.getPosition().y);
        canvas.drawBitmap(IMAGE, BOX_SIZE);
        
        if (isVisible) {
            canvas.drawBitmap(item.getImageDrawable(), ITEM_IMAGE_SIZE);
        } else {
            canvas.drawBitmap(R.drawable.question_item, ITEM_IMAGE_SIZE);
        }
        
        canvas.restoreState();
    }
    
    @Override
    public void beginContact(Entity e, Contact contact) {
        
        if (e.getType() == EntityType.PLAYER && isAlive()) {
            Player p = (Player) e;
            if (p.getItem() == null) {
                p.setItem(item);
                Assets.playSound(Assets.pickupSound, 0.05f);
                kill();
                PhysicalWorld.getInstance().addDeadEntity(this);
            }
        }
        
        super.beginContact(e, contact);
    }

    @Override
    public EntityType getType() {
        return EntityType.BOX_ITEM;
    }

    @Override
    public void setSprite(AnimationSwitcher sprite) {
    }
	
}
