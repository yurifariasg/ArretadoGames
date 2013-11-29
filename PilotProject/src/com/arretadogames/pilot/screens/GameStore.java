package com.arretadogames.pilot.screens;

import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.accounts.Account;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.Text;

public class GameStore extends GameScreen{
	
	private Text welcomeLabel;
	private Text nameLabel;
	private Text priceLabel;
	private RectF seedRenderingRect = new RectF(0, 0, 40, 40);

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.drawBitmap(R.drawable.store_background, 0, 0);
		seedRenderingRect.right = 550 + seedRenderingRect.width();
		seedRenderingRect.left = 550;
		seedRenderingRect.bottom = 170 + seedRenderingRect.height();
		seedRenderingRect.top = 170;
		canvas.drawBitmap(R.drawable.seed1, seedRenderingRect);
		
		if ( AccountManager.get().getAccount1() != null) { // SyncManager.get().isSignedIn() &&
			if (nameLabel == null || welcomeLabel == null ||
					AccountManager.get().getAccount1().getCoins() > 0) {
				
				createUserInfoLabels();
//				p1Coins = AccountManager.get().getAccount1().getCoins();
//				labelsAreRelatedToAccountProvider = true;
			}
			
			nameLabel.render(canvas, timeElapsed);
			welcomeLabel.render(canvas, timeElapsed);
			priceLabel.render(canvas, timeElapsed);
		}
	}
	
	private void createUserInfoLabels() {
		Account acc = AccountManager.get().getAccount1();
		welcomeLabel = new Text(400, 420, "Welcome, " + (acc.isAnnonymous() ? "" : acc.getName()),
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.5f, true);
		nameLabel = new Text(400, 445, "You have " + acc.getCoins() + " seeds",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 0.5f, true);
		priceLabel = new Text(640, 195, "50",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, true);
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
