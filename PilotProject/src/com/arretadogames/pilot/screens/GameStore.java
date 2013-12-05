package com.arretadogames.pilot.screens;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.accounts.Account;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.database.GameDatabase;
import com.arretadogames.pilot.database.descriptors.StoreItemDescriptor;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.ui.ItemWidget;
import com.arretadogames.pilot.ui.Text;

public class GameStore extends GameScreen implements GameButtonListener {
	
	private static final int BACK_BT = 2;
	
	private Text storeLabel;
	private Text moneyLabel;
	private RectF seedRenderingMoneyRect = new RectF(0, 0, 45, 45);

	private ArrayList<ItemWidget> storeItems = new ArrayList<ItemWidget>();
	private ImageButton buttonBack;
	
	public GameStore() {
		
		List<StoreItemDescriptor> items = GameDatabase.getInstance().getStoreItems();
		
		int y = 135;
		
		for (StoreItemDescriptor item : items) {
			ItemWidget widget = new ItemWidget(0, 110, y, 580, 150, item);
			storeItems.add(widget);
			y += 150;
		}
		
		buttonBack = new ImageButton(BACK_BT,
				702, 388, this,
				R.drawable.bt_back_selected,
				R.drawable.bt_back_unselected);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		canvas.drawBitmap(R.drawable.store_background, 0, 0);
		
		for (ItemWidget widget : storeItems){
			widget.render(canvas, timeElapsed);
		}
		
		canvas.drawBitmap(R.drawable.store_top, 100, 68);
		
		if ( AccountManager.get().getAccount1() != null) { // SyncManager.get().isSignedIn() &&
			if (storeLabel == null || moneyLabel == null ||
					AccountManager.get().getAccount1().getCoins() > 0) {

				
				createUserInfoLabels();
//				p1Coins = AccountManager.get().getAccount1().getCoins();
//				labelsAreRelatedToAccountProvider = true;
			}
			
			seedRenderingMoneyRect.right = 220 + seedRenderingMoneyRect.width();
			seedRenderingMoneyRect.left = 220;
			seedRenderingMoneyRect.bottom = 13 + seedRenderingMoneyRect.height();
			seedRenderingMoneyRect.top = 13;
			canvas.drawBitmap(R.drawable.seed1, seedRenderingMoneyRect);
			
			storeLabel.render(canvas, timeElapsed);
			moneyLabel.render(canvas, timeElapsed);
		}
		
		canvas.drawBitmap(R.drawable.bt_back_unselected, 702, 388);
	}
	
	private void createUserInfoLabels() {
		Account acc = AccountManager.get().getAccount1();
		storeLabel = new Text(333, 100, "Store",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1.3f, false);
		moneyLabel = new Text(98, 34, String.valueOf(acc.getCoins()),
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.9f, false);
	}

	@Override
	public void step(float timeElapsed) {

	}

	@Override
	public void input(InputEventHandler event) {
		for (ItemWidget widget : storeItems) {
			widget.input(event);
		}
		buttonBack.input(event);
	}

	@Override
	public void onPause() {
		
	}

	@Override
	public void onClick(int buttonId) {
		switch (buttonId) {
		case BACK_BT:
			Game.getInstance().goTo(GameState.MAIN_MENU);
			break;
		}
	}
}
