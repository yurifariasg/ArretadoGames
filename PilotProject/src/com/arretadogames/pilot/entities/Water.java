package com.arretadogames.pilot.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.Color;
import android.graphics.RectF;

import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Water extends Entity implements Steppable{

	private PolygonShape shapeA;
	private Sprite sprite;
	Collection<Entity> entitiesContact;
	private float height;
	private float width;
	private float density;
	
	public Water(float x, float y, float w, float h, float d) {
		super(x, y);
		width = w;
		height = h;
		shapeA = new PolygonShape();
		shapeA.setAsBox(width/2,height/2);
		body.createFixture(shapeA,0.0f).setSensor(true);
		body.setType(BodyType.STATIC);
		density = d;
		entitiesContact = new ArrayList<Entity>();
	}
	
	@Override
	public int getLayerPosition() {
		return 1;
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
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		RectF rect = new RectF(
				(- width/2 * GLCanvas.physicsRatio), // Top Left
				(- height/2 * GLCanvas.physicsRatio), // Top Left
				(width/2 * GLCanvas.physicsRatio), // Bottom Right
				(height/2 * GLCanvas.physicsRatio)); // Bottom Right
		
		//canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), rect, false);
		canvas.drawRect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom, Color.BLUE);
		canvas.restoreState();
	}

	@Override
	public void step(float timeElapsed) {
		for( Entity e : entitiesContact){
			applyBuoyancy(e);
		}
	}

	private void applyBuoyancy(Entity caixa) {
		
		Vec2 v[] = caixa.getWaterContactShape().getVertices();
		int cont = caixa.getWaterContactShape().getVertexCount();
		
		Vec2 v2[] = shapeA.getVertices();
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
		System.out.println("area " + area);
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
		      float edgeLength = edge.normalize();
		      Vec2 normal = Vec2.cross(-1,edge); //gets perpendicular vector
		      
		      float dragDot = Vec2.dot(normal, velDir);
		      if ( dragDot < 0 )
		          continue; //normal points backwards - this is not a leading edge
		  
		      float dragMag = (float) (dragDot * edgeLength * density * vel * vel);
		      Vec2 dragForce = velDir.mul(- dragMag);
		      caixa.body.applyForce( dragForce, midPoint );
		  }
	}

	@Override
	public EntityType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSprite(Sprite sprite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginContact(Entity e, Contact contact) {
		super.beginContact(e, contact);
		entitiesContact.add(e);
	}
	
	@Override
	public void endContact(Entity e, Contact contact) {
		super.endContact(e, contact);
		entitiesContact.remove(e);
	}
}
