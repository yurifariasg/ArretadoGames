package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

public class Coconut extends Entity {
    
    private static final float COCONUT_SIZE = 0.1f;
    private static final float COCONUT_IMAGE_SIZE = 0.3f;
    private static final float STUN_DURATION = 3;

    public Coconut(float x, float y) {
        super(x, y);
        
        CircleShape shape = new CircleShape();
        shape.setRadius(COCONUT_SIZE);
        Fixture bodyFixture = body.createFixture(shape,  50f);
        bodyFixture.setFriction(0.8f);
        body.setType(BodyType.DYNAMIC);
        body.setFixedRotation(false);
        
        physRect = new PhysicsRect(COCONUT_IMAGE_SIZE, COCONUT_IMAGE_SIZE);
    }

    @Override
    public void render(GLCanvas canvas, float timeElapsed) {
        canvas.saveState();
        canvas.translatePhysics(getPosX(), getPosY());
        canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
        canvas.drawBitmap(R.drawable.coconut, physRect);
        canvas.restoreState();
    }

    @Override
    public EntityType getType() {
        return EntityType.COCONUT;
    }

    @Override
    public void setSprite(AnimationSwitcher sprite) {
    }
    
    @Override
    public void beginContact(Entity e, Contact contact) {
        if (body.getLinearVelocity().length() > 2 && e.getType() == EntityType.PLAYER) {
            Player p = (Player) e;
            p.stun(STUN_DURATION);
        }
    }

}
