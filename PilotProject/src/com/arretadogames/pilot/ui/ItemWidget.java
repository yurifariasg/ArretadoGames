package com.arretadogames.pilot.ui;

import android.graphics.RectF;
import android.widget.Toast;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.R;
import com.arretadogames.pilot.accounts.Account;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.database.GameDatabase;
import com.arretadogames.pilot.database.descriptors.DigitalStoreItemDescriptor;
import com.arretadogames.pilot.database.descriptors.RealStoreItemDescriptor;
import com.arretadogames.pilot.database.descriptors.StoreItemDescriptor;
import com.arretadogames.pilot.database.descriptors.StoreItemType;
import com.arretadogames.pilot.items.ItemType;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.InputEventHandler;

import java.text.NumberFormat;

public class ItemWidget implements Renderable, GameButtonListener {

	private static final int BUY_BT = 1;

	private float x, y;
	private int id;
	private RectF itemRenderingRect;
	private RectF seedRenderingRect = new RectF(0, 0, 22, 22);
	private Text titleLabel;
	private Text descriptionLabel;
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
				700, 220,
				MainActivity.getContext().getResources().getDimension(R.dimen.button_buy_width),
                MainActivity.getContext().getResources().getDimension(R.dimen.button_buy_height),
				this,
				R.drawable.buy_bg,
				R.drawable.buy_bg,
				"BUY",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STORE), 0.5f);
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
		if (this.x != x) {
			itemRenderingRect = new RectF(x, y, x + itemRenderingRect.width(), y + itemRenderingRect.height());
			this.x = x;
			createItemInfoLabels();
		} else {
			this.x = x;
		}
	}

	public void setY(float y){
		if (this.y != y) {
			itemRenderingRect = new RectF(x, y, x + itemRenderingRect.width(), y + itemRenderingRect.height());
			this.y = y;
			createItemInfoLabels();
		} else {
			this.y = y;
		}
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {

		canvas.drawBitmap(R.drawable.item_widget,
		        itemRenderingRect.left, itemRenderingRect.top,
		        itemRenderingRect.width(), itemRenderingRect.height(),
		        0, MainActivity.getContext().getResources().getDimension(R.dimen.store_widget_extra_height));
		        //itemRenderingRect);

		if (titleLabel == null || descriptionLabel == null || priceLabel == null){
			createItemInfoLabels();
		}

		// TODO @yuri: avoid this calc every frame
		seedRenderingRect.right = x + 455 + seedRenderingRect.width();
		seedRenderingRect.left = x + 455;
		seedRenderingRect.bottom = y + 88 + seedRenderingRect.height();
		seedRenderingRect.top = y + 88;

		titleLabel.render(canvas, timeElapsed);
		descriptionLabel.render(canvas, timeElapsed);
//		descriptionLabel2.render(canvas, timeElapsed);

		// TODO @yuri: avoid this calc every frame
		canvas.drawBitmap(R.drawable.item_bg, x + 25, y + 22,
                MainActivity.getContext().getResources().getDimension(R.dimen.item_icon_bg_size),
                MainActivity.getContext().getResources().getDimension(R.dimen.item_icon_bg_size));
		canvas.drawBitmap(itemDescriptor.getIconId(), x + 38, y + 35,
                MainActivity.getContext().getResources().getDimension(R.dimen.item_icon_size),
                MainActivity.getContext().getResources().getDimension(R.dimen.item_icon_size)); // THE ITEM ICON

		if (itemDescriptor.getType() != StoreItemType.REAL)
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
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STORE), 0.75f, false);
		descriptionLabel = new Text(x + 141, y + 96, itemDescriptor.getDescription(),
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STORE), 0.524f, false);
//		descriptionLabel2 = new Text(x + 141, y + 110, "all obstacles in your way",
//				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.424f, false);

		if (itemDescriptor.getType() == StoreItemType.REAL) {
			priceLabel = new Text(x + 500, y + 100, getPrice(((RealStoreItemDescriptor)itemDescriptor).getPrice()),
					FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STORE), 0.65f, true);
		} else {
			priceLabel = new Text(x + 485, y + 100, ""+((DigitalStoreItemDescriptor)itemDescriptor).getValue(),
					FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STORE), 0.65f, false);
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
			} else {
				DigitalStoreItemDescriptor it = (DigitalStoreItemDescriptor) itemDescriptor;
				int valor = it.getValue();
				if(AccountManager.get().getAccount1().getCoins() >= valor ){
					AccountManager.get().getAccount1().setCoins(AccountManager.get().getAccount1().getCoins()-valor);
					ItemType type = ItemType.SUPER_STRENGHT;
					if(it.getName().equals("Double Jump")) type = ItemType.DOUBLE_JUMP;
					else if(it.getName().equals("Super Velocity")) type = ItemType.SUPER_VELOCITY;
					else if(it.getName().equals("Super Jump")) type = ItemType.SUPER_JUMP;
					AccountManager.get().getAccount1().buyItem(type);
					Toast.makeText(MainActivity.getContext(),"Item comprado! Agora voce tem " + GameDatabase.getInstance().getQuantItems(type), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.getContext(),"Sem sementes suficientes!", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}

	}

	public void input(InputEventHandler event) {
		buttonBuy.input(event);
	}

}
