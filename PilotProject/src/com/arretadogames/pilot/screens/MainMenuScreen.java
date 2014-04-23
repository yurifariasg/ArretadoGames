package com.arretadogames.pilot.screens;

import aurelienribon.tweenengine.TweenAccessor;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.R;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.android.KeyboardManager;
import com.arretadogames.pilot.audio.MusicI;
import com.arretadogames.pilot.audio.SoundI;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameMode;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.googlesync.SyncManager;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.ui.Text;
import com.arretadogames.pilot.ui.ZoomImageButton;
import com.arretadogames.pilot.util.Settings;

public class MainMenuScreen extends GameScreen implements GameButtonListener, TweenAccessor<MainMenuScreen> {

	private final static int ZOOM_PROPERTY = 1;
	private final static int BLACK_ALPHA_PROPERTY = 2;

	private static final int PLAY_BUTTON = 1;
	private static final int SETTINGS_BUTTON = 2;
	private static final int G_SIGN_IN_BUTTON = 3;
	private static final int STORE_BUTTON = 4;
	private static final int TOURNAMENT_BUTTON = 5;

	private ImageButton playBt;
	private ImageButton settingsBt;
	private ImageButton gPlusBt;
	private ImageButton tournamentBt;
	private ImageButton storeBt;
	private Text inputLabel;

	// Main Menu Screens
	private SettingsScreen settingsScreen;

	private float currentBlackAlpha;
	private float currentZoom;
	private State currentState;
	
	// Audio
	public static MusicI music;
	public static SoundI clickSound;

	public MainMenuScreen() {
		playBt = new ZoomImageButton(PLAY_BUTTON, 340, 240,
                getDimension(R.dimen.main_menu_play_button_size)+30,
                getDimension(R.dimen.main_menu_play_button_size)+30,
                this,
				R.drawable.quickrace_button_selected,
				R.drawable.quickrace_button_unselected);

		settingsBt = new ZoomImageButton(SETTINGS_BUTTON, 20, 20,
				getDimension(R.dimen.main_menu_button_size),
                getDimension(R.dimen.main_menu_button_size),
				this,
				R.drawable.bt_settings_selected,
				R.drawable.bt_settings_unselected);

		gPlusBt = new ImageButton(G_SIGN_IN_BUTTON,
				700, 20,
                getDimension(R.dimen.main_menu_button_size),
                getDimension(R.dimen.main_menu_button_size),
                this,
				R.drawable.bt_gplus_selected,
				R.drawable.bt_gplus_unselected);
		
		tournamentBt = new ImageButton(TOURNAMENT_BUTTON, 40, 210,
                getDimension(R.dimen.main_menu_play_button_size),
                getDimension(R.dimen.main_menu_play_button_size),
                this,
				R.drawable.bt_tournament_selected,
				R.drawable.bt_tournament_unselected);

		storeBt = new ZoomImageButton(STORE_BUTTON,
				550, 240,
                getDimension(R.dimen.main_menu_play_button_size)+30,
                getDimension(R.dimen.main_menu_play_button_size)+30,
                this,
				R.drawable.store_button_selected,
				R.drawable.store_button_unselected);
		
		tournamentBt = new ZoomImageButton(TOURNAMENT_BUTTON,
				130, 240,
                getDimension(R.dimen.main_menu_play_button_size)+30,
                getDimension(R.dimen.main_menu_play_button_size)+30,
                this,
				R.drawable.tournament_button_selected,
				R.drawable.tournament_button_unselected);

		inputLabel = new Text(400, 50, "",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1, true);

		currentBlackAlpha = 0;
		currentZoom = 1f;

		currentState = State.MAIN;
		settingsScreen = new SettingsScreen(this);
		
		System.out.println("------b4------");
		System.out.println("getAudio : " + MainActivity.getActivity().getAudio());
		music = MainActivity.getActivity().getAudio().newMusic("main_menu.mp3");
		System.out.println("------end------");
		music.setLooping(true);
		music.setVolume(0.5f);
		if (Settings.soundEnabled)
			music.play();
		
		
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();

		canvas.setClearColor(255, 0, 0, 0);

//		canvas.scale(currentZoom, currentZoom, GameSettings.TARGET_WIDTH / 2, GameSettings.TARGET_HEIGHT / 2);

		canvas.drawBitmap(R.drawable.menu_background, 0, 0,
		        getDimension(R.dimen.screen_width), getDimension(R.dimen.screen_height),
		        0, getDimension(R.dimen.main_menu_bg_extra_height));

		if (currentState == State.MAIN) {
			settingsBt.render(canvas, timeElapsed);
			playBt.render(canvas, timeElapsed);
			gPlusBt.render(canvas, timeElapsed);
			storeBt.render(canvas, timeElapsed);
			tournamentBt.render(canvas, timeElapsed);

			if (KeyboardManager.isShowing()) {
				inputLabel.render(canvas, timeElapsed);
			}

		} else if (currentState == State.SETTINGS) {
			settingsScreen.render(canvas, timeElapsed);
		}

		canvas.setClearColor(currentBlackAlpha, 0, 0, 0);
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
			settingsBt.input(event);
			gPlusBt.input(event);
			storeBt.input(event);
			tournamentBt.input(event);
		} else if (currentState == State.SETTINGS) {
			settingsScreen.input(event);
		}
	}

	@Override
	public void onPause() {
//
	}

	@Override
	public void onClick(int buttonId) {
		switch (buttonId) {
		case PLAY_BUTTON:
			Game.getInstance().setGameMode(GameMode.QUICKRACE);
			startGame();
			music.stop();
			break;
		case SETTINGS_BUTTON:
			//currentState = State.SETTINGS;
			break;
		case G_SIGN_IN_BUTTON:
			if (SyncManager.get().isSignedIn()) {
				SyncManager.get().revokeAccess();
				AccountManager.get().clearArrount1();
			} else {
				SyncManager.get().userClickedSignIn();
			}
			music.stop();
			break;
		case STORE_BUTTON:
			startStore();
			music.stop();
			break;
		case TOURNAMENT_BUTTON:
			startTournamentSelection();
			Game.getInstance().setGameMode(GameMode.TOURNAMENT);
			music.stop();
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
	
	private void startTournamentSelection() {
		Game.getInstance().goTo(GameState.TOURNAMENT_SELECTION);
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

	@Override
	public void onBackPressed() {
		MainActivity.getActivity().showExitDialog();
	}
}
