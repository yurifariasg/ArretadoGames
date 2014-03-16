package com.arretadogames.pilot.entities;

import android.graphics.Color;

import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.entities.effects.EffectDescriptor;
import com.arretadogames.pilot.entities.effects.EffectManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Water extends Entity implements Steppable {
    
    private static final int WATER_LAYER_POS = -1;
    private static EffectDescriptor splashEffect;
    
    public static EffectDescriptor getSplashEffect() {
        if (splashEffect == null) {
            splashEffect = new EffectDescriptor();
            splashEffect.type = "Splash";
            splashEffect.repeat = false;
            splashEffect.layerPosition = WATER_LAYER_POS + 1;
        }
        
        return splashEffect;
    }
	
	private class Spring {
		public float springHeight;
		public float springRelativeX;
		public float speed;
		private float deltaFromNaturalheight;
		
		public void Update() {
			deltaFromNaturalheight = springHeight - waterHeight;
		    speed += -SPRING_SWIFTNESS * deltaFromNaturalheight - SPRING_DAMPENING * speed;
		    
		    if (Math.abs(speed) > SPRING_MAXIMUM_SPEED)
		    	speed = SPRING_MAXIMUM_SPEED * (speed / Math.abs(speed));
		    
		    springHeight += speed;
		}
	}
	
	// Rendering Properties
	
	// This is the factor that makes the collided wave goes further down or not
	private static final float ENTITY_VELOCITY_REDUCTION_FACTOR = 30;
	// This makes the springs more dense or not
	private static final float SPRING_SWIFTNESS = 0.01f;
	// This makes the waves spread more or less..
	private static final float WAVES_SPREADNESS = 0.006f;
	// Maximum distance between springs, smaller value causes more springs to be created
	private static final float WATER_SPRINGS_MAX_DISTANCE = 0.6f; // Meters
	// Factor which makes the springs stop (higher value makes them stop faster)
	private static final float SPRING_DAMPENING = 0.0005f; //0.025f;
	// Maximum speed that a spring can go.. This avoids then to go a lot higher or lower
	private static final float SPRING_MAXIMUM_SPEED = 0.035f; // 0.05 - 0.04
	// Padding top for the water drawing
	private static final float WATER_TOP_PADDING = 0.25f; // Meters
	
	// Colors
	private static final int COLOR_ALPHA = 200;
	private static final int WATER_SURFACE_COLOR = Color.argb(COLOR_ALPHA, 92, 133, 255);
	private static final int WATER_BOTTOM_COLOR = Color.argb(COLOR_ALPHA, 0, 56, 224);
	private static final float WATER_SURFACE_LINE_WIDTH = 3;
	
	private Spring[] springs;
	float[] leftDeltas;
	float[] rightDeltas;
	
	// Physics Properties
	private PolygonShape shapeA;
	Collection<Entity> entitiesContact;
	private float waterHeight;
	private float waterWidth;
	private float density;
	private Fixture fixture;
	
	public Water(float x, float y, float width, float height, float density) {
		super(x, y);
		this.waterWidth = width;
		this.waterHeight = height;
		this.density = density;
		shapeA = new PolygonShape();
		shapeA.setAsBox(width/2,height/2);
		fixture = body.createFixture(shapeA,0.0f);
		body.setType(BodyType.STATIC);
		entitiesContact = new ArrayList<Entity>();
		
		fixture.setSensor(true);
		
		initializeWaterSprings();
	}
	
	private void initializeWaterSprings() {
		// Check this stuff
		int waterDivisions = (int) Math.floor(waterWidth / WATER_SPRINGS_MAX_DISTANCE);
		float divisionlength = waterWidth / waterDivisions;
		
		springs = new Spring[waterDivisions + 1];
		float currentX = 0;
		
		for (int i = 0 ; i < waterDivisions + 1 ; i++) {
			springs[i] = new Spring();
			
			springs[i].springRelativeX = currentX;
			currentX += divisionlength;
			springs[i].springHeight = this.waterHeight;
		}
		
		leftDeltas = new float[springs.length];
		rightDeltas = new float[springs.length];
	}
	
	/**
	 * Causes the water to create waves and a splash effect in the given x position
	 * @param e
	 */
	public void splash(Entity e, Contact contact) {
	    
	    float x = e.getPosX();
	    float yVel = e.body.m_linearVelocity.y / ENTITY_VELOCITY_REDUCTION_FACTOR;
	    
		if (x <= getPosX() - waterWidth / 2 || x >= getPosX() + waterWidth / 2) {
			return; // Out of range
		}
		
		if (yVel < 0) { // Splash Threshold (usually -0.1 or less, closer to 0 )
		    
		    EffectDescriptor splashEffect = getSplashEffect();
            splashEffect.pRect = e.physRect;
		    splashEffect.position = new Vec2(e.getPosX() + 0.2f, // Splash a little in front of the player
		            getPosY() + waterHeight / 2
		            + e.physRect.height() * 0.5f); // There is a little offset up based on the image (so the base of the water fits the top of the water)
		    EffectManager.getInstance().addEffect(splashEffect);
		}
		
		float waterRelativePosition = x - (getPosX() - waterWidth / 2);
		int selectedDivision = (int) Math.floor(waterRelativePosition / WATER_SPRINGS_MAX_DISTANCE);
		
		if (selectedDivision >= springs.length)
			selectedDivision = springs.length - 1;
		
		springs[selectedDivision].speed = yVel; // speed it up!
	}

	@Override
	public int getLayerPosition() {
		return WATER_LAYER_POS;
	}
	
	public static List<Vec2> transformToVec2(List<List<Float>> l){
		List<Vec2> result = new ArrayList<Vec2>();
		for(List<Float> pair : l){
			result.add(new Vec2(pair.get(0), pair.get(1)));
		}
		return result;
	}
	
	public static Vec2 getCentroid(List<List<Float>> l){
		Vec2 centroid = new Vec2(0,0);
		PolygonShape a = new PolygonShape();
		List<Vec2> ele = transformToVec2(l);
		Vec2 arr[] = new Vec2[ele.size()];
		for(int i = 0 ; i < ele.size(); i++){
			arr[i] = ele.get(i);
		}
		a.computeCentroidToOut(arr, ele.size(), centroid);
		return centroid;
	}
	
	public static float calcArea(List<List<Float>> l){
		if(l.size() < 3) return 0f;
		float area = 0f;
		List<Vec2> ele = transformToVec2(l);
		for(int i = 2; i < ele.size(); i++){
			area += Vec2.cross(ele.get(i).sub(ele.get(0)), ele.get(i-1).sub(ele.get(0)));
		}
		area = area/2;
		return Math.abs(area);
	}
	
	public static List<List<Float>> findIntersectionOfPolygon(List<List<Float>> subjectPolygon, List<List<Float>> clipPolygon){
		if(subjectPolygon.size() < 3 || clipPolygon.size() < 3) return new ArrayList<List<Float>>();
		List<List<Float>> output = new ArrayList<List<Float>>();
		for(List<Float> a : subjectPolygon){
			output.add(a);
		}
		List<Float > cp1 = clipPolygon.get(clipPolygon.size() - 1);
		
		for(List<Float> clipVertex : clipPolygon){
			List<Float> cp2 = clipVertex;
			List<List<Float>> inputList = new ArrayList<List<Float>>();
			for(List<Float> a : output){
				inputList.add(a);
			}
			output.clear();
			List<Float> s;
			if( inputList.size() > 0 ){ s = inputList.get(inputList.size() - 1);
			for(List<Float> subjectVertex : inputList){
				List<Float> e = subjectVertex;
				if(inside(cp1,cp2,e)){
					if(! inside(cp1,cp2,s)){
						output.add(computeIntersection(cp1,cp2,e,s));
					}
					output.add(e);
				} else if (inside(cp1,cp2,s)){
					output.add(computeIntersection(cp1,cp2,e,s));
				}
				s = e;
			}
			}
			cp1 = cp2;
		}
		return output;
	}
	
	private static List<Float> computeIntersection(List<Float> cp1, List<Float> cp2, List<Float> e, List<Float> s) {
		List<Float> dc = new ArrayList<Float>();
		dc.add(cp1.get(0) - cp2.get(0));
		dc.add(cp1.get(1) - cp2.get(1));
		List<Float> dp = new ArrayList<Float>();
		dp.add(s.get(0) - e.get(0));
		dp.add(s.get(1) - e.get(1));
		Float n1 = Float.valueOf(cp1.get(0) * cp2.get(1) - cp1.get(1) * cp2.get(0));
		Float n2 = Float.valueOf(s.get(0) * e.get(1) - s.get(1) * e.get(0)); 
		Float n3 = Float.valueOf(1.0f / (dc.get(0) * dp.get(1) - dc.get(1) * dp.get(0)));
		List<Float> result = new ArrayList<Float>();
		result.add((n1*dp.get(0) - n2*dc.get(0)) * n3);
		result.add((n1*dp.get(1) - n2*dc.get(1)) * n3);
		return result;
	}

	private static boolean inside(List<Float> cp1, List<Float>cp2, List<Float> p) {
		return(cp2.get(0)-cp1.get(0))*(p.get(1)-cp1.get(1)) > (cp2.get(1)-cp1.get(1))*(p.get(0)-cp1.get(0));
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		float bottomY = getPosY() - waterHeight / 2;
		float initialX = getPosX() - waterWidth / 2;
		
		for (int i = 1 ; i < springs.length ; i++) {
			
			canvas.drawRectFromPhysics(
					initialX + springs[i-1].springRelativeX, bottomY + springs[i-1].springHeight + WATER_TOP_PADDING,
					initialX + springs[i-1].springRelativeX, bottomY,
					initialX + springs[i].springRelativeX, bottomY,
					initialX + springs[i].springRelativeX, bottomY + springs[i].springHeight + WATER_TOP_PADDING,
					WATER_SURFACE_COLOR, WATER_BOTTOM_COLOR,
					WATER_BOTTOM_COLOR, WATER_SURFACE_COLOR);
		}
		
		// Draw Surface Lines
		for (int i = 1 ; i < springs.length ; i++) {
			canvas.drawLine(
					GLCanvas.physicsRatio * (initialX + springs[i-1].springRelativeX), GameSettings.TARGET_HEIGHT - GLCanvas.physicsRatio * (bottomY + springs[i-1].springHeight + WATER_TOP_PADDING), 
					GLCanvas.physicsRatio * (initialX + springs[i].springRelativeX), GameSettings.TARGET_HEIGHT - GLCanvas.physicsRatio * (bottomY + springs[i].springHeight + WATER_TOP_PADDING),
					WATER_SURFACE_LINE_WIDTH, WATER_BOTTOM_COLOR);
		}
		
	}

	@Override
	public void step(float timeElapsed) {
		for( Entity e : entitiesContact){
			applyBuoyancy(e);
		}
		
		// Update WaterSprings
		updateWaterSprings();
	}
	
	private void updateWaterSprings() {
		for (Spring s : springs)
			s.Update();
		             
		// do some passes where springs pull on their neighbours
		for (int j = 0; j < 8; j++) {
		    for (int i = 0; i < springs.length; i++) {
		        if (i > 0)
		        {
		            leftDeltas[i] = WAVES_SPREADNESS * (springs[i].springHeight - springs [i - 1].springHeight);
		            springs[i - 1].speed += leftDeltas[i];
		        }
		        if (i < springs.length - 1)
		        {
		            rightDeltas[i] = WAVES_SPREADNESS * (springs[i].springHeight - springs [i + 1].springHeight);
		            springs[i + 1].speed += rightDeltas[i];
		        }
		    }
		 
		    for (int i = 0; i < springs.length; i++)
		    {
		        if (i > 0)
		            springs[i - 1].springHeight += leftDeltas[i];
		        if (i < springs.length - 1)
		            springs[i + 1].springHeight += rightDeltas[i];
		    }
		}
	}

	private void applyBuoyancy(Entity caixa) {
		
		final Vec2 v[] = caixa.getWaterContactShape().getVertices();
		int cont = caixa.getWaterContactShape().getVertexCount();
		
		final Vec2 v2[] = shapeA.getVertices();
		int cont2 = shapeA.getVertexCount();
		
		List<List<Float>> a = new ArrayList<List<Float>>();
		List<List<Float>> b = new ArrayList<List<Float>>();
		for( int i = 0; i < cont; i++){
			Vec2 p = caixa.body.getWorldPoint(v[i]);
			List<Float> ui = new ArrayList<Float>();
			ui.add(p.x);
			ui.add(p.y);
			
			a.add(ui);
		}
		
		for( int i = 0; i < cont2; i++){
			Vec2 p = body.getWorldPoint(v2[i]);
			List<Float> ui = new ArrayList<Float>();
			ui.add(p.x);
			ui.add(p.y);
			
			b.add(ui);
		}
		List<List<Float>> in = findIntersectionOfPolygon(a, b);
		if( in.size() > 2){
		float area = calcArea(in);
		Vec2 centroid = getCentroid(in);
		float displacedMass = density * area;
		Vec2 force =  world.getGravity().mul(-displacedMass);
		caixa.body.applyForce(force, centroid);
		}
		
		List<Vec2> intersectionPoints = transformToVec2(in);
		for (int i = 0; i < intersectionPoints.size(); i++) {
		      //the end points and mid-point of this edge 
		      Vec2 v0 = intersectionPoints.get(i);
		      Vec2 v1 = i+1 < intersectionPoints.size() ? intersectionPoints.get(i+1) : intersectionPoints.get(0);
		      Vec2 midPoint = (v0.add(v1)).mul(0.5f);
		      
		      //find relative velocity between object and fluid at edge midpoint
		      Vec2 velDir = caixa.body.getLinearVelocityFromWorldPoint( midPoint ).sub(
		                      body.getLinearVelocityFromWorldPoint( midPoint ));
		      float vel = velDir.normalize();
		  
		      Vec2 edge = v1.sub(v0);
		      float edgelength = edge.normalize();
		      Vec2 normal = Vec2.cross(-1,edge); //gets perpendicular vector
		      
		      float dragDot = Vec2.dot(normal, velDir);
		      if ( dragDot < 0 )
		          continue; //normal points backwards - this is not a leading edge
		  
		      float dragMag = (float) (dragDot * edgelength * density * vel * vel);
		      Vec2 dragForce = velDir.mul(- dragMag);
		      caixa.body.applyForce( dragForce, midPoint );
		  }
	}

	@Override
	public EntityType getType() {
		return EntityType.FLUID;
	}

	@Override
	public void setSprite(AnimationSwitcher sprite) {
		// No Sprite
	}

	@Override
	public void beginContact(Entity e, Contact contact) {
		super.beginContact(e, contact);
		entitiesContact.add(e);
		splash(e, contact);
	}
	
	@Override
	public void endContact(Entity e, Contact contact) {
		super.endContact(e, contact);
		entitiesContact.remove(e);
	}

	public float getWidth() {
		return waterWidth;
	}
	
	public float getHeight() {
		return waterHeight;
	}

}
