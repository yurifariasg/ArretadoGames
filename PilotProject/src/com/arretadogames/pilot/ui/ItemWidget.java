package com.arretadogames.pilot.ui;

import java.text.NumberFormat;

import android.graphics.RectF;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.R;
import com.arretadogames.pilot.accounts.Account;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.database.descriptors.RealStoreItemDescriptor;
import com.arretadogames.pilot.database.descriptors.StoreItemDescriptor;
import com.arretadogames.pilot.database.descriptors.StoreItemType;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.InputEventHandler;

public class ItemWidget implements Renderable, GameButtonListener {
	
	private static final int BUY_BT = 1;
	
	private float x, y;
	private int id;
	private RectF itemRenderingRect;
	private RectF seedRenderingRect = new RectF(0, 0, 22, 22);
	private Text titleLabel;
	private Text descriptionLabel;
	private Text descriptionLabel2;
	private Text priceLabel;
	
	private TextImageButton buttonBuy;
	
	private StoreItemDescriptor itemDescriptor;
	
	public ItemWidget(int id, float x, float y, float width, float height, StoreItemDescriptor item){
		this.id = id;
		this.x = x;
		this.y = y;
		this.itemRenderingRect = new RectF(x, y, x + width, y + height);
		this.itemDescriptor = item;
		
		buttonBuy = new TextImageButton(BUY_BT,
				700, 220, this,
				R.drawable.buy_bg,
				R.drawable.buy_bg,
				"BUY",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.5f);
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
		
		// TODO @yuri: avoid this calc every frame
		seedRenderingRect.right = x + 521 + seedRenderingRect.width();
		seedRenderingRect.left = x + 521;
		seedRenderingRect.bottom = y + 104 + seedRenderingRect.height();
		seedRenderingRect.top = y + 104;
		
		titleLabel.render(canvas, timeElapsed);
		descriptionLabel.render(canvas, timeElapsed);
//		descriptionLabel2.render(canvas, timeElapsed);
		
		// TODO @yuri: avoid this calc every frame
		canvas.drawBitmap(R.drawable.item_bg, x + 25, y + 22);
		canvas.drawBitmap(itemDescriptor.getIconId(), x + 38, y + 35); // THE ITEM ICON
		
		canvas.drawBitmap(R.drawable.seed1, seedRenderingRect);
		priceLabel.render(canvas, timeElapsed);
		
		// TODO @yuri: avoid this calc every frame
		buttonBuy.setX(x + 447);
		buttonBuy.setY(y + 31);
		buttonBuy.render(canvas, timeElapsed);
//		canvas.drawBitmap(R.drawable.buy_bg, x + 447, y + 31);
//		buttonBuyText.render(canvas, timeElapsed);
	}
	
	private void createItemInfoLabels() {
		Account acc = AccountManager.get().getAccount1();
		titleLabel = new Text(x + 143, y + 45, itemDescriptor.getName(),
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.75f, false);
		descriptionLabel = new Text(x + 141, y + 96, itemDescriptor.getDescription(),
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.424f, false);
//		descriptionLabel2 = new Text(x + 141, y + 110, "all obstacles in your way",
//				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.424f, false);	
		
		if (itemDescriptor.getType() == StoreItemType.REAL) {

			priceLabel = new Text(x + 489, y + 115, getPrice(((RealStoreItemDescriptor)itemDescriptor).getPrice()),
					FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.5f, true);
			
			
		}
	}
	
	public String getPrice(float price) {
		NumberFormat.getCurrencyInstance().format(price);
		return NumberFormat.getCurrencyInstance().format(price);
	}

	@Override
	public void onClick(int buttonId) {
		
		switch (buttonId) {
		case BUY_BT:
			if (itemDescriptor.getType() == StoreItemType.REAL) {
				MainActivity.getActivity().purchase(((RealStoreItemDescriptor) itemDescriptor).getSkuCode());
			}
			
			break;
		}
		
	}

	public void input(InputEventHandler event) {
		buttonBuy.input(event);
	}

}
