package com.arretadogames.pilot.screens;

import java.util.ArrayList;

import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.accounts.Account;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.ItemWidget;
import com.arretadogames.pilot.ui.Text;

public class GameStore extends GameScreen{
	
	private Text storeLabel;
	private Text moneyLabel;
	private RectF seedRenderingMoneyRect = new RectF(0, 0, 45, 45);

	private ArrayList<ItemWidget> storeItems = new ArrayList<ItemWidget>();
	private ItemWidget item1 = new ItemWidget(0, 110, 135, 580, 150);
	private ItemWidget item2 = new ItemWidget(0, 110, 285, 580, 150);
	private ItemWidget item3 = new ItemWidget(0, 110, 435, 580, 150);

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		canvas.drawBitmap(R.drawable.store_background, 0, 0);
		
//		for (int i = 0; i < storeItens; i++){
		item1.render(canvas, timeElapsed);
		item2.render(canvas, timeElapsed);
		item3.render(canvas, timeElapsed);
		
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
		moneyLabel = new Text(98, 34, "12530",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.9f, false);
	}

	@Override
	public void step(float timeElapsed) {
//		Game.getInstance().goTo(GameState.MAIN_MENU);
	}

	@Override
	public void input(InputEventHandler event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

}
