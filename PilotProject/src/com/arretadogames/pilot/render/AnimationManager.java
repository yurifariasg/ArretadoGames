package com.arretadogames.pilot.render;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;


public class AnimationManager {
    
    private static final String SPRITE_SHEET_TAG = "Sheet";
    private static final String ANIMATION_SWITCHER_TAG = "AnimationSwitcher";
    private static final String ANIMATION_TAG = "Animation";
    private static final String FRAME_TAG = "Frame";
    
    private static AnimationManager instance;
    
    private HashMap<String, AnimationSwitcher> spriteEntities;
    
    public static AnimationManager getInstance() {
        if (instance == null)
            instance = new AnimationManager();
        return instance;
    }
    
    private AnimationManager() {
    }
    
    public AnimationSwitcher getSprite(String name) {
        return spriteEntities.get(name);
    }
    
    public void loadXml() {
        
        if (spriteEntities != null)
            return;
        
        Log.i("SpriteManager", "Starting XML Loading...");
        spriteEntities = new HashMap<String, AnimationSwitcher>();
        
        Context context = MainActivity.getContext();
        XmlResourceParser parser = context.getResources().getXml(R.xml.spritesheets);
        try {
            
            ArrayList<Sprite> frames = new ArrayList<Sprite>();
            String currentAnimationName = null;
            AnimationSwitcher currentSpriteEntity = null;
            int currentSheetResId = 0;
            
            parser.next();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                
                if (eventType == XmlPullParser.START_TAG) {
                    
                    if (parser.getName().equalsIgnoreCase(SPRITE_SHEET_TAG)) {
                        
                        String resIdName = parser.getAttributeValue(null, "resId");
                        currentSheetResId = context.getResources().getIdentifier(resIdName, "drawable", context.getPackageName());
                        
                        if (currentSheetResId == 0) {
                            throw new Exception("No resource was found for " + resIdName);
                        }
                        
                    } else if (parser.getName().equalsIgnoreCase(ANIMATION_SWITCHER_TAG)) {
                        
                        String name = parser.getAttributeValue(null, "name");
                        currentSpriteEntity = new AnimationSwitcher(name);
                        
                    } else if (parser.getName().equalsIgnoreCase(ANIMATION_TAG)) {
                        
                        frames.clear();
                        currentAnimationName = parser.getAttributeValue(null, "name");
                        
                    } else if (parser.getName().equalsIgnoreCase(FRAME_TAG)) {
                        
                        int x, y, w, h;
                        float t;
                        x = parser.getAttributeIntValue(null, "x", 0);
                        y = parser.getAttributeIntValue(null, "y", 0);
                        w = parser.getAttributeIntValue(null, "w", 0);
                        h = parser.getAttributeIntValue(null, "h", 0);
                        t = parser.getAttributeFloatValue(null, "t", 0);
                        frames.add(new Sprite(currentSheetResId, x, y, w, h, t));
                        
                    }
                    
                    
                } else if (eventType == XmlPullParser.END_TAG) {
                    
                    if (parser.getName().equalsIgnoreCase(SPRITE_SHEET_TAG)) {
                        
                        currentSheetResId = 0;
                        
                    } else if (parser.getName().equalsIgnoreCase(ANIMATION_SWITCHER_TAG)) {
                        
                        spriteEntities.put(currentSpriteEntity.getName(), currentSpriteEntity);
                        currentSpriteEntity = null;
                        
                    } else if (parser.getName().equalsIgnoreCase(ANIMATION_TAG)) {
                        
                        Sprite[] fArray = new Sprite[0];
                        fArray = frames.toArray(fArray);
                        
                        currentSpriteEntity.addState(new Animation(currentAnimationName, fArray));
                        
                        currentAnimationName = null;
                        frames.clear();
                        
                    } else if (parser.getName().equalsIgnoreCase(FRAME_TAG)) {
                        
                    }
                }
                
                parser.next();
                eventType = parser.getEventType();
            }
        
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SpriteManager", "Malformed XML");
            throw new IllegalArgumentException("Malformed XML");
        }
        
        Log.i("SpriteManager", "Ending XML Loading...");
        
    }
    
    
    
}
