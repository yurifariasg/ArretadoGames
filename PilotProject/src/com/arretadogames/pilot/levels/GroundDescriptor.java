package com.arretadogames.pilot.levels;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;

public class GroundDescriptor {
	
	private List<Vec2> points;
	
	public GroundDescriptor() {
		points = new ArrayList<Vec2>();
	}
	
	public void addPoint(Vec2 point) {
		points.add(point);
	}
	
	public List<Vec2> getPoints() {
		return points;
	}
	
}
