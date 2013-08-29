package com.arretados.leveleditor;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class ResourceManager {
    
    private static HashMap<DrawMode, Image> images = null;
    
    public static Image getImageFor(DrawMode entity) {
        validateImageMap();
        return images.get(entity);
    }
    
    private static void validateImageMap() {
        if (images == null)
            createImages();
    }

    private static void createImages() {
        images = new HashMap<DrawMode, Image>();
        try {
            images.put(DrawMode.COIN, ImageIO.read(new File("imgs/coin.png")));
            images.put(DrawMode.ONEWAY_WALL, ImageIO.read(new File("imgs/forest_platform.png")));
            images.put(DrawMode.BOX, ImageIO.read(new File("imgs/box.png")));
            images.put(DrawMode.BREAKABLE, ImageIO.read(new File("imgs/breakable.png")));
            images.put(DrawMode.P1, ImageIO.read(new File("imgs/p1.png")));
            images.put(DrawMode.P2, ImageIO.read(new File("imgs/p2.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
    
}
