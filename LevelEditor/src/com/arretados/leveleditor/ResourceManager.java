package com.arretados.leveleditor;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class ResourceManager {
    
    public enum Resource {
        COIN, ONEWAY_WALL, BOX, BREAKABLE, P1, P2, TREE1,
        SHRUB, GRASS, SPIKE, FLAG, HOLE
    }
    
    
    private static HashMap<Resource, Image> images = null;
    
    public static Image getImageFor(Resource entity) {
        validateImageMap();
        return images.get(entity);
    }
    
    private static void validateImageMap() {
        if (images == null)
            createImages();
    }

    private static void createImages() {
        images = new HashMap<Resource, Image>();
        try {
            images.put(Resource.COIN, ImageIO.read(new File("imgs/coin.png")));
            images.put(Resource.ONEWAY_WALL, ImageIO.read(new File("imgs/forest_platform.png")));
            images.put(Resource.BOX, ImageIO.read(new File("imgs/box.png")));
            images.put(Resource.BREAKABLE, ImageIO.read(new File("imgs/breakable.png")));
            images.put(Resource.P1, ImageIO.read(new File("imgs/p1.png")));
            images.put(Resource.P2, ImageIO.read(new File("imgs/p2.png")));
            images.put(Resource.TREE1, ImageIO.read(new File("imgs/tree1.png")));
            images.put(Resource.SHRUB, ImageIO.read(new File("imgs/shrub.png")));
            images.put(Resource.GRASS, ImageIO.read(new File("imgs/grass.png")));
            images.put(Resource.SPIKE, ImageIO.read(new File("imgs/spike.png")));
            images.put(Resource.FLAG, ImageIO.read(new File("imgs/flag.png")));
            images.put(Resource.HOLE, ImageIO.read(new File("imgs/hole.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
    
}
