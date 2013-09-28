package com.arretadogames.pilot.loading;

import android.graphics.Paint;

public class FontSpecification {
	
	private Paint fontPaint;
	private Paint strokePaint;
	
	public FontSpecification(Paint fontPaint, Paint strokePaint) {
		if (fontPaint == null && strokePaint == null)
			throw new IllegalArgumentException("Both font specification paints are null");
		
		this.fontPaint = fontPaint;
		this.strokePaint = strokePaint;
	}
	
	public Paint getFontPaint() {
		return fontPaint;
	}
	
	public Paint getStrokePaint() {
		return strokePaint;
	}

}
