package com.arretadogames.pilot.screens;

import aurelienribon.tweenengine.TweenAccessor;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.accounts.Account;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.android.KeyboardManager;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.googlesync.SyncManager;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.ui.Text;
import com.arretadogames.pilot.ui.ZoomImageButton;

public class MainMenuScreen extends GameScreen implements GameButtonListener, TweenAccessor<MainMenuScreen> {
	
	private final static int ZOOM_PROPERTY = 1;
	private final static int BLACK_ALPHA_PROPERTY = 2;
	
	private static final int PLAY_BUTTON = 1;
	private static final int SETTINGS_BUTTON = 2;
	private static final int G_SIGN_IN_BUTTON = 3;
	private static final int STORE_BUTTON = 4; // NOWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
	
	private ImageButton playBt;
//	private ImageButton settingsBt;
	private ImageButton gPlusBt;
	private ImageButton storeBt; // NOWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW
	private Text welcomeLabel;
	private Text nameLabel;
	private Text inputLabel;
	private boolean labelsAreRelatedToAccountProvider;
	private long p1Coins; // Variable to detect if the account coins have changed since last time
	
	// Main Menu Screens
	private SettingsScreen settingsScreen;
	
	private float currentBlackAlpha;
	private float currentZoom;
	private State currentState;
	
	public MainMenuScreen() {
		playBt = new ZoomImageButton(PLAY_BUTTON, 340, 210, this,
				R.drawable.bt_play_selected,
				R.drawable.bt_play_unselected);
		
//		settingsBt = new ImageButton(SETTINGS_BUTTON,
//				700, 390, this,
//				R.drawable.bt_settings_selected,
//				R.drawable.bt_settings_unselected);
		
		gPlusBt = new ImageButton(G_SIGN_IN_BUTTON,
				700, 20, this,
				R.drawable.bt_gplus_selected,
				R.drawable.bt_gplus_unselected);
		
		storeBt = new ImageButton(STORE_BUTTON,
				700, 220, this,
				R.drawable.bt_store_selected,
				R.drawable.bt_store_unselected);
		
		inputLabel = new Text(400, 50, "",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, true);
		
		currentBlackAlpha = 0;
		currentZoom = 1f;
		
		currentState = State.MAIN;
		settingsScreen = new SettingsScreen(this);
	}
	
	private void createUserInfoLabels() {
		Account acc = AccountManager.get().getAccount1();
		welcomeLabel = new Text(400, 395, "Welcome, " + (acc.isAnnonymous() ? "" : acc.getName()),
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, true);
		nameLabel = new Text(400, 445, "You have " + acc.getCoins() + " seeds",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, true);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		
		canvas.fillScreen(255, 0, 0, 0);
		
		canvas.scale(currentZoom, currentZoom, GameSettings.TARGET_WIDTH / 2, GameSettings.TARGET_HEIGHT / 2);
		
		canvas.drawBitmap(R.drawable.menu_background, 0, 0);
		
		if (currentState == State.MAIN) {
//			settingsBt.render(canvas, timeElapsed);
			playBt.render(canvas, timeElapsed);
			gPlusBt.render(canvas, timeElapsed);
			storeBt.render(canvas, timeElapsed);
			
			if ( AccountManager.get().getAccount1() != null) { // SyncManager.get().isSignedIn() &&
				if (nameLabel == null || welcomeLabel == null ||
						AccountManager.get().getAccount1().getCoins() != p1Coins) {
					
					createUserInfoLabels();
					p1Coins = AccountManager.get().getAccount1().getCoins();
					labelsAreRelatedToAccountProvider = true;
				}
				
				nameLabel.render(canvas, timeElapsed);
				welcomeLabel.render(canvas, timeElapsed);
			}
			
			if (KeyboardManager.isShowing()) {
				inputLabel.render(canvas, timeElapsed);
			}
			
		} else if (currentState == State.SETTINGS) {
			settingsScreen.render(canvas, timeElapsed);
		}
		
		canvas.fillScreen(currentBlackAlpha, 0, 0, 0);
		canvas.restoreState();
	}

	@Override
	public void step(float timeElapsed) {
		
		if (KeyboardManager.isShowing()) {
			inputLabel.setText(KeyboardManager.getText());
		}
		
	}

	@Override
	public void input(InputEventHandler event) {
		if (currentState == State.MAIN) {
			playBt.input(event);
//			settingsBt.input(event);
			gPlusBt.input(event);
			storeBt.input(event);
		} else if (currentState == State.SETTINGS) {
			settingsScreen.input(event);
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(int buttonId) {
		switch (buttonId) {
		case PLAY_BUTTON:
			startGame();
			break;
		case SETTINGS_BUTTON:
			currentState = State.SETTINGS;
			break;
		case G_SIGN_IN_BUTTON:
			if (SyncManager.get().isSignedIn()) {
				SyncManager.get().revokeAccess();
				AccountManager.get().clearArrount1();
			} else {
				SyncManager.get().userClickedSignIn();
			}
			break;
		case STORE_BUTTON:
			startStore();
			break;
		}
			
	}
	
	private void startGame() {
		Game.getInstance().goTo(GameState.LEVEL_SELECTION);
		currentBlackAlpha = 0;
		currentZoom = 1;
	}
	
	private void startStore() {
		Game.getInstance().goTo(GameState.GAME_STORE);
	}
	

	@Override
	public int getValues(MainMenuScreen target, int tweenType, float[] returnValues) {
		if (tweenType == ZOOM_PROPERTY) {
			returnValues[0] = target.currentZoom;
		} else if (tweenType == BLACK_ALPHA_PROPERTY) {
			returnValues[0] = target.currentBlackAlpha;
		}
		return 1;
	}

	@Override
	public void setValues(MainMenuScreen target, int tweenType, float[] newValues) {
		if (tweenType == ZOOM_PROPERTY) {
			target.currentZoom = newValues[0];
		} else if (tweenType == BLACK_ALPHA_PROPERTY) {
			target.currentBlackAlpha = newValues[0];
		}
	}
	
	public void setState(State newState) {
		currentState = newState;
	}
	
	public enum State {
		MAIN, SETTINGS;
	}
}
