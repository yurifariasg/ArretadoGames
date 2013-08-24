package com.arretadogames.pilot.loading;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arretadogames.pilot.MainActivity;

public class ImageLoader {
	
	public static Bitmap loadImage(int resourceId) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		return BitmapFactory.decodeResource(MainActivity.getContext().getResources(), resourceId, options);
	}
	
	public static int[] checkBitmapSize(int resourceId) {
		int[] size = new int[2];
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(MainActivity.getContext().getResources(), resourceId, options);
		size[0] = options.outWidth;
		size[1] = options.outHeight;
		return size;
	}

}
