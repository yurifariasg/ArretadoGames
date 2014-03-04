package com.arretadogames.pilot.entities;

import java.util.Comparator;

public interface LayerEntity {
	
	public int getLayerPosition();
	
	public class Layer {
		
		private static Comparator<LayerEntity> comparator;
		
		public static Comparator<LayerEntity> getComparator() {
			if (comparator == null)
				comparator = createComparator();
			return comparator;
		}

		private static Comparator<LayerEntity> createComparator() {
			Comparator<LayerEntity> comparator = new Comparator<LayerEntity>() {
			    public int compare(LayerEntity c1, LayerEntity c2) {
			        return c2.getLayerPosition() - c1.getLayerPosition();
			    }
			};
			return comparator;
		}
	}

}
