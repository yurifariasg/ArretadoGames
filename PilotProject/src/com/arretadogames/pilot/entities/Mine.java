package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.entities.effects.EffectDescriptor;
import com.arretadogames.pilot.entities.effects.EffectManager;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.HashSet;
import java.util.Set;

public class Mine extends Entity {
    
    private static final float MINE_SIZE = 0.1f;
    private static final PhysicsRect MINE_IMAGE_SIZE = new PhysicsRect(0.6f, 0.6f);
    private static final PhysicsRect EXPLOSION_SIZE = new PhysicsRect(5, 5);
    private static final float PUSH_FORCE = 10;
    private static final float PARALYSIS_DURATION = 5;
    
    private AnimationSwitcher sprite;

    public Mine(float x, float y) {
        super(x, y);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(MINE_SIZE / 2, MINE_SIZE / 2);
        Fixture bodyFixture = body.createFixture(shape,  50f);
        bodyFixture.setFriction(0.8f);
        body.setType(BodyType.DYNAMIC);
        body.setFixedRotation(true);
        
        physRect = MINE_IMAGE_SIZE;
        
        sprite = AnimationManager.getInstance().getSprite("mine");
    }

    @Override
    public void render(GLCanvas canvas, float timeElapsed) {
        canvas.saveState();
        canvas.translatePhysics(getPosX(), getPosY() + MINE_IMAGE_SIZE.height() / 2.3f); // Mine is on the bottom of the image
        canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
        sprite.render(canvas, physRect, timeElapsed);
        canvas.restoreState();
    }

    @Override
    public EntityType getType() {
        return EntityType.MINE;
    }

    @Override
    public void setSprite(AnimationSwitcher sprite) {
    }
    
    @Override
    public void beginContact(Entity e, Contact contact) {
        if (e.getType() == EntityType.PLAYER && isAlive()) {
            Player p = (Player) e;
            p.paralyze(PARALYSIS_DURATION);
            
            EffectDescriptor effect = new EffectDescriptor();
            effect.type = "explosion";
            effect.pRect = EXPLOSION_SIZE;
            effect.position = body.getPosition().clone();
            effect.layerPosition = Ground.GROUND_LAYER_POSITION - 1;
            
            EffectManager.getInstance().addEffect(effect);
            
            pushEntities();
            
            kill();
            PhysicalWorld.getInstance().addDeadEntity(this);
        }
    }

    private void pushEntities() {
        
        World world = body.getWorld();
        
        Vec2 upperVertex = new Vec2(body.getPosition().x + EXPLOSION_SIZE.width() / 2,
                body.getPosition().y + EXPLOSION_SIZE.height() / 2);
        Vec2 lowerVertex = new Vec2(body.getPosition().x - EXPLOSION_SIZE.width() / 2,
                body.getPosition().y - EXPLOSION_SIZE.height() / 2);
        
        
        AABB aabb = new AABB(lowerVertex, upperVertex);
        
        final Set<Entity> affectedEntities = new HashSet<Entity>();
        
        world.queryAABB(new QueryCallback() {
            
            @Override
            public boolean reportFixture(Fixture fixture) {
                Entity entity = (Entity) fixture.getBody().getUserData();
                if (entity != null) {
                    affectedEntities.add(entity);
                }
                return true;
            }
            
        }, aabb);
        
        Vec2 minePosition = body.getPosition();
        
        for (Entity entity : affectedEntities) {
            
            Body b = entity.body;
            Vec2 pos = b.getPosition();
            
            Vec2 pushDirection = pos.sub(minePosition);
            float distance = pushDirection.normalize(); // Normalizes the vector
            float halfExplosionSize = EXPLOSION_SIZE.width() / 2;
            
            float pushForce = (halfExplosionSize - distance) * PUSH_FORCE;
            
            pushDirection.mulLocal(pushForce);
            
            entity.body.applyLinearImpulse(pushDirection, entity.body.getWorldCenter());
            
        }
        
        
    }

}
