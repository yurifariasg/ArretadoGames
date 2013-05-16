package com.arretadogames.pilot.loading;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;


public class FontLoader {
	
	// Class Variables
	private static FontLoader fontLoader;
	
	// Object Variables
	private HashMap<Fonts, Typeface> fonts;
	private int fontSize;
	
	private FontLoader(Context c) {
		
		fontSize = 45; //c.getResources().getDimensionPixelSize(45); // FIXME add to r.dimens

		fonts = new HashMap<Fonts, Typeface>();
		fonts.put(Fonts.TRANSMETALS,
				Typeface.create(Typeface.createFromAsset(
				c.getAssets(), "Transmetals.ttf"),
				Typeface.NORMAL));
		
	}
	
	public int getFontSize() {
		return fontSize;
	}
	
	public Typeface getFont(Fonts font) {
		return fonts.get(font);
	}
	
	public static FontLoader getInstance() {
		return fontLoader;
	}
	
	public static void create(Context c) {
		if (fontLoader == null)
			fontLoader = new FontLoader(c);
	}
	
	public enum Fonts {
		TRANSMETALS
	}
}
