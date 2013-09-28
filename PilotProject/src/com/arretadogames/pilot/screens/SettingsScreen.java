package com.arretadogames.pilot.screens;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.MainMenuScreen.State;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.ui.Text;
import com.arretadogames.pilot.ui.ToggleButton;

public class SettingsScreen extends GameScreen implements GameButtonListener {

	private static final int BACK_BUTTON = 0;
	private static final int MUTE_BUTTON = 1;
	
	// Buttons
	private ToggleButton muteToggle;
	private Text muteText;
	
	private ImageButton backButton;
	
	// MainMenu
	private MainMenuScreen mainMenu;
	
	public SettingsScreen(MainMenuScreen mainMenu) {
		this.mainMenu = mainMenu;
		
		muteToggle = new ToggleButton(
				MUTE_BUTTON,
				50, 200,
				R.drawable.checked_box, R.drawable.unchecked_box);
		
		muteText = new Text(240, 235, "Mute Game",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1.3f,
				true);
		
		backButton = new ImageButton(
				BACK_BUTTON,
				700, 390, this,
				R.drawable.bt_back_selected, R.drawable.bt_back_unselected);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		muteToggle.render(canvas, timeElapsed);
		muteText.render(canvas, timeElapsed);
		backButton.render(canvas, timeElapsed);
	}

	@Override
	public void step(float timeElapsed) {
	}

	@Override
	public void input(InputEventHandler event) {
		muteToggle.input(event);
		backButton.input(event);
	}

	@Override
	public void onPause() {
	}

	@Override
	public void onClick(int buttonId) {
		if (buttonId == BACK_BUTTON)
			mainMenu.setState(State.MAIN);
	}
}
