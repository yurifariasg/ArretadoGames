package com.arretadogames.pilot.loading;

import java.util.HashMap;

import com.arretadogames.pilot.config.GameSettings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;

public class FontLoader {
	
	// Class Variables
	private static FontLoader fontLoader;
	
	// Object Variables
	private HashMap<FontTypeFace, FontSpecification> fonts;
	private Context context;
	
	private FontLoader(Context context) {
		this.context = context;
		fonts = new HashMap<FontTypeFace, FontSpecification>();
	}
	
	public FontSpecification getFont(FontTypeFace font) {
		if (fonts.get(font) == null)
			fonts.put(font, createSpecificationFor(font));
		return fonts.get(font);
	}
	
	public static FontLoader getInstance() {
		return fontLoader;
	}
	
	public static void create(Context c) {
		if (fontLoader == null)
			fontLoader = new FontLoader(c);
	}
	
	/*
	 * If you want a need font type added (different font, color, or stroke, just add them below
	 */
	
	private FontSpecification createSpecificationFor(FontTypeFace fontTypeFace) {
		
		switch (fontTypeFace) {
		
		case TRANSMETALS_STROKED:
			Typeface tf = Typeface.create(Typeface.createFromAsset(
					context.getAssets(), "Transmetals.ttf"),
					Typeface.NORMAL);

			// Set-up Paint object for drawing letters to bitmap
			Paint paint = new Paint(); // Create Android Paint Instance
			paint.setAntiAlias(true); // Enable Anti Alias
			paint.setTypeface(tf); // Set Typeface
			paint.setTextSize(GameSettings.DEFAULT_FONT_SIZE * 1.5f); // Set Text Size
			paint.setColor(Color.WHITE); // Set ARGB (White, Opaque)
			paint.setTextAlign(Paint.Align.LEFT);
			
			Paint mStrokePaint = new Paint();
			mStrokePaint.setTypeface(tf);
			mStrokePaint.setStyle(Style.STROKE);
			mStrokePaint.setStrokeWidth(5);
			mStrokePaint.setColor(Color.rgb(89, 103, 213));
			mStrokePaint.setTextSize(GameSettings.DEFAULT_FONT_SIZE * 1.5f);
			mStrokePaint.setAntiAlias(true);
			mStrokePaint.setTextAlign(Paint.Align.LEFT);
			
			return new FontSpecification(paint, mStrokePaint);
			
		case TRANSMETALS:
			Typeface transmetals = Typeface.create(Typeface.createFromAsset(
					context.getAssets(), "Transmetals.ttf"),
					Typeface.NORMAL);

			// Set-up Paint object for drawing letters to bitmap
			Paint transmetalsPaint = new Paint(); // Create Android Paint Instance
			transmetalsPaint.setAntiAlias(true); // Enable Anti Alias
			transmetalsPaint.setTypeface(transmetals); // Set Typeface
			transmetalsPaint.setTextSize(GameSettings.DEFAULT_FONT_SIZE); // Set Text Size
			transmetalsPaint.setColor(Color.BLACK); // Set ARGB (White, Opaque)
			transmetalsPaint.setTextAlign(Paint.Align.LEFT);
			
			return new FontSpecification(transmetalsPaint, null);
		case TRANSMETALS_STORE:
			Typeface tfStore = Typeface.create(Typeface.createFromAsset(
					context.getAssets(), "Transmetals.ttf"),
					Typeface.NORMAL);

			// Set-up Paint object for drawing letters to bitmap
			Paint transmetalStorePaint = new Paint(); // Create Android Paint Instance
			transmetalStorePaint.setAntiAlias(true); // Enable Anti Alias
			transmetalStorePaint.setTypeface(tfStore); // Set Typeface
			transmetalStorePaint.setTextSize(GameSettings.DEFAULT_FONT_SIZE * 1.5f); // Set Text Size
			transmetalStorePaint.setColor(Color.WHITE); // Set ARGB (White, Opaque)
			transmetalStorePaint.setTextAlign(Paint.Align.LEFT);
			
			Paint mStoreStrokePaint = new Paint();
			mStoreStrokePaint.setTypeface(tfStore);
			mStoreStrokePaint.setStyle(Style.STROKE);
			mStoreStrokePaint.setStrokeWidth(5);
			mStoreStrokePaint.setColor(Color.rgb(62, 35, 0));
			mStoreStrokePaint.setTextSize(GameSettings.DEFAULT_FONT_SIZE * 1.5f);
			mStoreStrokePaint.setAntiAlias(true);
			mStoreStrokePaint.setTextAlign(Paint.Align.LEFT);
			
			return new FontSpecification(transmetalStorePaint, mStoreStrokePaint);
		case ARIAN:
			Typeface tfArianExtended = Typeface.create(Typeface.createFromAsset(
					context.getAssets(), "SFArianExtended.ttf"),
					Typeface.NORMAL);

			// Set-up Paint object for drawing letters to bitmap
			Paint arianExtendedPaint = new Paint(); // Create Android Paint Instance
			arianExtendedPaint.setAntiAlias(true); // Enable Anti Alias
			arianExtendedPaint.setTypeface(tfArianExtended); // Set Typeface
			arianExtendedPaint.setTextSize(GameSettings.DEFAULT_FONT_SIZE * 1.5f); // Set Text Size
			arianExtendedPaint.setColor(Color.WHITE); // Set ARGB (White, Opaque)
			arianExtendedPaint.setTextAlign(Paint.Align.LEFT);
			
			Paint arianExtendedPaintStroke = new Paint();
			arianExtendedPaintStroke.setTypeface(tfArianExtended);
			arianExtendedPaintStroke.setStyle(Style.STROKE);
			arianExtendedPaintStroke.setStrokeWidth(5);
			arianExtendedPaintStroke.setColor(Color.rgb(62, 35, 0));
			arianExtendedPaintStroke.setTextSize(GameSettings.DEFAULT_FONT_SIZE * 1.5f);
			arianExtendedPaintStroke.setAntiAlias(true);
			arianExtendedPaintStroke.setTextAlign(Paint.Align.LEFT);
			
			return new FontSpecification(arianExtendedPaint, arianExtendedPaintStroke);
			
		case ARIAN_BLACK:
			Typeface tfArianBlackExtended = Typeface.create(Typeface.createFromAsset(
					context.getAssets(), "SFArianExtended.ttf"),
					Typeface.NORMAL);

			// Set-up Paint object for drawing letters to bitmap
			Paint arianBlackPaint = new Paint(); // Create Android Paint Instance
			arianBlackPaint.setAntiAlias(true); // Enable Anti Alias
			arianBlackPaint.setTypeface(tfArianBlackExtended); // Set Typeface
			arianBlackPaint.setTextSize(GameSettings.DEFAULT_FONT_SIZE * 1.5f); // Set Text Size
			arianBlackPaint.setColor(Color.BLACK); // Set ARGB (White, Opaque)
			arianBlackPaint.setTextAlign(Paint.Align.LEFT);
			
			return new FontSpecification(arianBlackPaint, null);
		}
		
		return null;
	}
	
	public enum FontTypeFace {
		TRANSMETALS_STROKED, TRANSMETALS, TRANSMETALS_STORE, ARIAN, ARIAN_BLACK;
	}
}
