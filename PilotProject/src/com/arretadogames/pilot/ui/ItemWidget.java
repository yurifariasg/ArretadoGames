package com.arretadogames.pilot.ui;

import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class ItemWidget implements Renderable{
	
//	private float x, y;
	private int id;
	private RectF itemRenderingRect;
	
	public ItemWidget(int id, float x, float y, float width, float height){
		this.id = id;
//		this.x = x;
//		this.y = y;
//		this.itemRenderingRect = new RectF(0, 0, 40, 40);
		this.itemRenderingRect = new RectF(0, 0, 30, 30);
//		this.itemRenderingRect = new RectF(x, y, x + width, y + height);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
/*		itemRenderingRect.right = 550 + itemRenderingRect.width();
		itemRenderingRect.left = 550;
		itemRenderingRect.bottom = 170 + itemRenderingRect.height();
		itemRenderingRect.top = 170;
		canvas.drawBitmap(R.drawable.seed1, itemRenderingRect);*/
		
		canvas.drawBitmap(R.drawable.item_widget, itemRenderingRect);
	}

}
