package com.arretadogames.pilot.ui;

import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.accounts.Account;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class ItemWidget implements Renderable{
	
	private float x, y;
	private int id;
	private RectF itemRenderingRect;
	private RectF seedRenderingRect = new RectF(0, 0, 22, 22);
	private Text titleLabel;
	private Text descriptionLabel;
	private Text descriptionLabel2;
	private Text priceLabel;
	private Text buttonBuyText;
	
// TODO
//	private ImageButton buttonBuy;
//	private ImageButton buttonBack;
	
	public ItemWidget(int id, float x, float y, float width, float height){
		this.id = id;
		this.x = x;
		this.y = y;
		this.itemRenderingRect = new RectF(x, y, x + width, y + height);
		
		/*	
		TODO
		ImageButton buttonBuy = new ImageButton(STORE_BUTTON,
		700, 220, this,
		R.drawable.bt_store_selected,
		R.drawable.bt_store_unselected);
		
		ImageButton buttonBack = new ImageButton(STORE_BUTTON,
		700, 220, this,
		R.drawable.bt_store_selected,
		R.drawable.bt_store_unselected);*/
	}
	
	public int getId(){
		return this.id;
	}
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}	

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		canvas.drawBitmap(R.drawable.item_widget, itemRenderingRect);
		
		if (titleLabel == null || descriptionLabel == null || priceLabel == null){
			createItemInfoLabels();
		}
		
		seedRenderingRect.right = x + 521 + seedRenderingRect.width();
		seedRenderingRect.left = x + 521;
		seedRenderingRect.bottom = y + 104 + seedRenderingRect.height();
		seedRenderingRect.top = y + 104;
		
		titleLabel.render(canvas, timeElapsed);
		descriptionLabel.render(canvas, timeElapsed);
		descriptionLabel2.render(canvas, timeElapsed);
		
		canvas.drawBitmap(R.drawable.item_bg, x + 25, y + 22);
		canvas.drawBitmap(R.drawable.elephants_power, x + 38, y + 35); // THE ITEM ICON
		
		canvas.drawBitmap(R.drawable.seed1, seedRenderingRect);
		priceLabel.render(canvas, timeElapsed);
		canvas.drawBitmap(R.drawable.buy_bg, x + 447, y + 31);
		buttonBuyText.render(canvas, timeElapsed);
	}
	
	private void createItemInfoLabels() {
		Account acc = AccountManager.get().getAccount1();
		titleLabel = new Text(x + 143, y + 45, "Elephant's Power",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.75f, false);
		descriptionLabel = new Text(x + 141, y + 96, "Increases your ability to break",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.424f, false);
		descriptionLabel2 = new Text(x + 141, y + 110, "all obstacles in your way",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.424f, false);		 
		priceLabel = new Text(x + 489, y + 115, "100",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.5f, true);
		buttonBuyText = new Text(x + 498, y + 52, "BUY",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.5f, true);
		
	}

}
