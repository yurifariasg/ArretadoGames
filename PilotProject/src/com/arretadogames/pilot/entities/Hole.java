package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;


public class Hole extends Entity implements Steppable{

	public static final int HOLE_LAYER_POSITION = Ground.GROUND_LAYER_POSITION - 1;
	
	private Fixture fixture;
	private Body entrance;
	private Fixture fentrance;
	private Body exit;
	private Fixture fexit;
	private TatuBola tatu;
	private boolean seta;
	private boolean sai;
	private boolean contatoTatu;
	private boolean entrou;
	
	private final float ENTRANCE_IMAGE_X_OFFSET = 0.3f;
	private final float HOLE_WIDTH = 0.5f;
	private final float HOLE_HEIGHT = 1f;
	private final float ENTRANCE_SENSOR_WIDTH = 0.2f;
	private final float ENTRANCE_SENSOR_HEIGHT = 0.2f;
	
	/**
	 * deve obedecer a x2 > x1 + 1;
	 * @param x1 o inicio do primeiro buraco
	 * @param x2 fim do buraco de saida
	 */
	private float x1;
	private float x2;
	public Hole(float x1, float x2) {
		super(0,0);
		this.x1 = x1;
		this.x2 = x2;
		physRect =  new PhysicsRect(2*HOLE_WIDTH,0.5f);
		Vec2 vec[] = new Vec2[4];
		vec[0] = new Vec2(x1,0);
		vec[1] = new Vec2(x1+2*HOLE_WIDTH, -HOLE_HEIGHT);
		vec[2] = new Vec2(x2-2*HOLE_WIDTH, -HOLE_HEIGHT);
		vec[3] = new Vec2(x2,0);
		ChainShape shape = new ChainShape();
		shape.createChain(vec, 4);
		fixture = body.createFixture(shape, 0.5f);
		Filter filter = new Filter();
		filter.categoryBits = CollisionFlag.GROUP_TATU_HOLE.getValue();
		filter.maskBits = CollisionFlag.GROUP_TATU_HOLE.getValue();
		fixture.setFilterData(filter);
		body.setType(BodyType.STATIC);
		
		PolygonShape pshape = new PolygonShape();
		pshape.setAsBox(ENTRANCE_SENSOR_WIDTH, ENTRANCE_SENSOR_HEIGHT);
		BodyDef bd = new BodyDef();
		bd.position = new Vec2(x1 + 0.3f,ENTRANCE_SENSOR_HEIGHT);
		entrance = world.createBody(bd);
		fentrance = entrance.createFixture(pshape, 0);
		fentrance.setFilterData(filter);
		fentrance.setSensor(true);
		entrance.setUserData(this);
		
		PolygonShape pshape2 = new PolygonShape();
		pshape2.setAsBox(HOLE_WIDTH, 0.1f);
		BodyDef bd2 = new BodyDef();
		bd2.position = new Vec2(x2-HOLE_WIDTH,-0.2f);
		exit = world.createBody(bd2);
		fexit = exit.createFixture(pshape2, 0);
		fexit.setSensor(true);
		fexit.setFilterData(filter);
		exit.setUserData(this);
		
		tatu = null;
		seta = false;
		sai = false;
		contatoTatu = false;
		entrou = false;
	}

	@Override
	public void beginContact(Entity e, Contact contact) {
		super.beginContact(e, contact);
		if(e instanceof TatuBola && (contact.m_fixtureA == fentrance || contact.m_fixtureB == fentrance) && ((TatuBola)e).actActive ){
			tatu = (TatuBola)e;
			seta = true;
			((TatuBola)e).actActive = false;
		}
		if(e instanceof TatuBola && (contact.m_fixtureA == fentrance || contact.m_fixtureB == fentrance)){
			tatu = (TatuBola)e;
			contatoTatu = true;
		}
	}
	
	@Override
	public void endContact(Entity e, Contact contact) {
		super.endContact(e, contact);
		if(e instanceof TatuBola && (contact.m_fixtureA == fexit || contact.m_fixtureB == fexit) && tatu != null){
			sai = true;
		}
		if(e instanceof TatuBola && (contact.m_fixtureA == fentrance || contact.m_fixtureB == fentrance)){
			contatoTatu = false;
		}
	}
	
	@Override
	public void render(GLCanvas canvas, float timeElapsed) {

		canvas.saveState();
		canvas.translatePhysics(x1 + ENTRANCE_IMAGE_X_OFFSET, 0f);
		canvas.drawBitmap(R.drawable.hole, physRect);
		
		canvas.restoreState();
		canvas.saveState();
		
		canvas.translatePhysics(x2, 0f);
		canvas.drawBitmap(R.drawable.hole, physRect);
		canvas.restoreState();
	}

	@Override
	public EntityType getType() {
		return EntityType.HOLE;
	}

	@Override
	public void setSprite(AnimationSwitcher sprite) {
	}

	@Override
	public void step(float timeElapsed) {
		if(contatoTatu && tatu!=null && tatu.actActive && ! seta && !entrou) seta = true;
		if(seta){
			entrou = true;
			seta = false;			
			
            Filter filter = new Filter();
            filter.categoryBits = CollisionFlag.GROUP_TATU_HOLE.getValue();
            filter.maskBits = CollisionFlag.GROUP_TATU_HOLE.getValue();

            tatu.bodyFixture.setFilterData(filter);
            tatu.footFixture.setFilterData(filter);
            tatu.body.setLinearVelocity(new Vec2(0,0));
			
			Vec2 vec[] = new Vec2[2];
			vec[0] = new Vec2(x1+HOLE_WIDTH,HOLE_HEIGHT);
			vec[1] = new Vec2(x1+2*HOLE_WIDTH, 0);
			ChainShape shape = new ChainShape();
			shape.createChain(vec, 2);
			body.createFixture(shape, 0.5f).setFilterData(filter);
			tatu.setOnHole(true);
		}
		if (sai){
            tatu.setMaskAndCategoryBits(); // Set Default Back
			tatu.setOnHole(false);
            sai = false;
			tatu = null;
		}
	}
	
	@Override
	public int getLayerPosition() {
		return HOLE_LAYER_POSITION;
	}

}
