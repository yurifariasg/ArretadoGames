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
	private static final int UP_BT = 3;
	private static final int DOWN_BT = 4;
	
	private Text storeLabel;
	private Text moneyLabel;
	private RectF seedRenderingMoneyRect = new RectF(0, 0, 45, 45);

	private ArrayList<ItemWidget> storeItems = new ArrayList<ItemWidget>();
	private ImageButton buttonBack;
	private ImageButton buttonUp;
	private ImageButton buttonDown;
	
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
		
		buttonUp = new ImageButton(UP_BT,
				12, 288, this,
				R.drawable.arrow_up_selected,
				R.drawable.arrow_up);
		
		buttonDown = new ImageButton(DOWN_BT,
				12, 388, this,
				R.drawable.arrow_down_selected,
				R.drawable.arrow_down);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		canvas.drawBitmap(R.drawable.store_background, 0, 0);
		
		renderWidgets(canvas, timeElapsed);
		
		canvas.drawBitmap(R.drawable.store_top, 100, 68);
		
		if ( AccountManager.get().getAccount1() != null) { // SyncManager.get().isSignedIn() &&
			if (storeLabel == null || moneyLabel == null ||
					AccountManager.get().getAccount1().getCoins() > 0) {

				
				createUserInfoLabels();
//				p1Coins = AccountManager.get().getAccount1().getCoins();
//				labelsAreRelatedToAccountProvider = true;
			}
			
			seedRenderingMoneyRect.right = 10 + seedRenderingMoneyRect.width();
			seedRenderingMoneyRect.left = 10;
			seedRenderingMoneyRect.bottom = 13 + seedRenderingMoneyRect.height();
			seedRenderingMoneyRect.top = 13;
			canvas.drawBitmap(R.drawable.seed1, seedRenderingMoneyRect);
			
			storeLabel.render(canvas, timeElapsed);
			moneyLabel.render(canvas, timeElapsed);
		}
		
		buttonBack.render(canvas, timeElapsed);
		buttonUp.render(canvas, timeElapsed);
		buttonDown.render(canvas, timeElapsed);
	}

	private void createUserInfoLabels() {
		Account acc = AccountManager.get().getAccount1();
		storeLabel = new Text(333, 100, "Store",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STORE), 1.3f, false);
		moneyLabel = new Text(70, 34, String.valueOf(acc.getCoins()),
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STORE), 0.9f, false);
	}

	@Override
	public void step(float timeElapsed) {

	}

	@Override
	public void input(InputEventHandler event) {
//		for (ItemWidget widget : storeItems) {
//			widget.input(event);
//		}
		for (int i = (currentPage - 1) * PAGE_SIZE ; i < currentPage * PAGE_SIZE && i < storeItems.size() ; i++) {
			storeItems.get(i).input(event);
//			storeItems.get(i).render(canvas, timeElapsed);
//			currentY += 150;
		}
		buttonBack.input(event);
		buttonUp.input(event);
		buttonDown.input(event);
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
		case UP_BT:
			scrollItems(true);
			break;
		case DOWN_BT:
			scrollItems(false);
			break;
		}
	}
	
	private static final int PAGE_SIZE = 2;
	private int currentPage = 1;
	
	private void scrollItems(boolean isScrollUp) {
		
		if (isScrollUp && currentPage > 1)  {
			currentPage--;
		} else if (!isScrollUp && currentPage <= Math.ceil(storeItems.size() / PAGE_SIZE)) {
			currentPage++;
		}
		
	}
	
	private void renderWidgets(GLCanvas canvas, float timeElapsed) {
		
		int currentY = 135;
		for (int i = (currentPage - 1) * PAGE_SIZE ; i < currentPage * PAGE_SIZE && i < storeItems.size() ; i++) {
			storeItems.get(i).setY(currentY);
			storeItems.get(i).render(canvas, timeElapsed);
			currentY += 150;
		}
		
	}

	@Override
	public void onBackPressed() {
		Game.getInstance().goTo(GameState.MAIN_MENU);
	}
}
