package com.arretadogames.pilot.loading;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arretadogames.pilot.GameActivity;

public class ImageLoader {
	
	public static Bitmap loadImage(int resourceId) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
//		options.inDensity = Bitp
		return BitmapFactory.decodeResource(GameActivity.getContext().getResources(), resourceId, options);
	}
	
	public static int[] checkBitmapSize(int resourceId) {
		int[] size = new int[2];
		Bitmap b = BitmapFactory.decodeResource(GameActivity.getContext().getResources(), resourceId, null);
		size[0] = b.getWidth();
		size[1] = b.getHeight();
		b.recycle();
		return size;
	}

}
