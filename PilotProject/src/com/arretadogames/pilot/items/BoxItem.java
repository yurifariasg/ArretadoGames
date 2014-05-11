 package com.arretadogames.pilot.items;

import android.graphics.Color;
import android.opengl.GLES11;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.GameCamera;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.GameWorldUI;
import com.arretadogames.pilot.util.Assets;
import com.arretadogames.pilot.util.Util;
import com.arretadogames.pilot.world.GameWorld;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class BoxItem extends Entity implements Steppable {
    
    public static final PhysicsRect BOX_SIZE = new PhysicsRect(0.8f, 0.8f);
    public static final PhysicsRect ITEM_IMAGE_SIZE = new PhysicsRect(0.6f, 0.6f);
    private static final int IMAGE = R.drawable.box_stopped;
    
    private Item item;
    private boolean isRandom;

    public BoxItem(float x, float y) {
        super(x, y);
        this.item = randomizeItem();
        this.isRandom = new Random().nextBoolean();
        
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
        canvas.rotate((float) (-body.getAngle() * 180 / Math.PI));
        canvas.drawBitmap(IMAGE, BOX_SIZE);
        
        if (isRandom) {
            canvas.drawBitmap(item.getImageDrawable(), ITEM_IMAGE_SIZE);
        } else {
            canvas.drawBitmap(R.drawable.question_item, ITEM_IMAGE_SIZE);
        }
        
        canvas.restoreState();
    }
    
    
    @Override
    public void preSolve(Entity e, Contact contact, Manifold oldManifold) {

    	if (e.getType() == EntityType.PLAYER && isAlive()) {
            Player p = (Player) e;
            if (p.getItem() == null) {
                
                p.setItem(item);
                Assets.playSound(Assets.pickupSound, 0.05f);
                
                state = State.DYING;
                
                // Add Item to UI
                GameWorld gameWorld = ((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME));
                GameWorldUI ui = gameWorld.getUI();
                GameCamera camera = gameWorld.getCamera();
                
                Vec2 result = camera.convertWorldToPixel(p.getPosX(), p.getPosY());
                
                ui.addItemAnimation(p.getNumber(), result.x, result.y, item);
            }
        }
    	
    	if (isDead() || state == State.DYING) {
    		contact.setEnabled(false);
    	}
    	
    }
    
    @Override
    public void beginContact(Entity e, Contact contact) {
        super.beginContact(e, contact);
    }

    @Override
    public EntityType getType() {
        return EntityType.BOX_ITEM;
    }

    @Override
    public void setSprite(AnimationSwitcher sprite) {
    }
    
    private static class BoxPiece extends Entity {
        
        private static final String[] PIECE_TYPES = {"box_piece1", "box_piece2", "box_piece3"};
        private static final PhysicsRect PIECE_SIZE = new PhysicsRect(0.1f, 0.2f);
        private static final float DURATION = 4;
        
        private AnimationSwitcher anim;
        private float timeToLive;

        public BoxPiece(float x, float y) {
            super(x, y);
            int imageIndex = Util.random(0, 2);
            anim = com.arretadogames.pilot.render.AnimationManager.getInstance().getSprite(PIECE_TYPES[imageIndex]);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(PIECE_SIZE.width()/2, PIECE_SIZE.height()/2);
            body.createFixture(shape,  0.5f).setFriction(0.8f);
            body.setType(BodyType.DYNAMIC);
            body.setFixedRotation(false);
            physRect = Util.convertToSquare(PIECE_SIZE);
            physRect.inset(-0.1f, -0.1f);
            timeToLive = DURATION;
            
            Vec2 directionVec = new Vec2(Util.random(0.0f, 2.0f) - 1f, Util.random(0.0f, 2.0f) - 1f);
            directionVec.mulLocal(Util.random(0f, 0.1f));
            body.applyLinearImpulse(directionVec, body.getPosition(), true);
        }

        @Override
        public void render(GLCanvas canvas, float timeElapsed) {
            canvas.saveState();
            canvas.translatePhysics(body.getPosition().x, body.getPosition().y);
            canvas.rotate((float) (-body.getAngle() * 180 / Math.PI));
            

            GLES11.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            
            canvas.setColor(Util.adjustColorAlpha(Color.WHITE, (timeToLive / DURATION)));
            anim.render(canvas, physRect, timeElapsed);
            canvas.setColor(Color.WHITE);
            canvas.restoreState();

            GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
            
            timeToLive -= timeElapsed;
            
            if (timeToLive <= 0) {
                kill();
                PhysicalWorld.getInstance().addDeadEntity(this);
            }
        }

        @Override
        public EntityType getType() {
            return EntityType.BOX_PIECE;
        }

        @Override
        public void setSprite(AnimationSwitcher sprite) {
        }
        
    }

    @Override
    public void step(float timeElapsed) {
        
        if (state == State.DYING) {
            int pieces = isOnWater() ? 3 : 8;
            for (int i = 0 ; i < pieces ; i++) {
                new BoxPiece(getPosX(), getPosY());
            }
            
            kill();
            PhysicalWorld.getInstance().addDeadEntity(this);
        }
    }
}
