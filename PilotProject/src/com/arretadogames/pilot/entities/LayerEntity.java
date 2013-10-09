package com.arretadogames.pilot.entities;

import java.util.Comparator;

public interface LayerEntity {
	
	public int getLayerPosition();
	
	public class Layer {
		
		private static Comparator<Entity> comparator;
		
		public static Comparator<Entity> getComparator() {
			if (comparator == null)
				comparator = createComparator();
			return comparator;
		}

		private static Comparator<Entity> createComparator() {
			Comparator<Entity> comparator = new Comparator<Entity>() {
			    public int compare(Entity c1, Entity c2) {
			        return c2.getLayerPosition() - c1.getLayerPosition();
			    }
			};
			return comparator;
		}
	}

}
