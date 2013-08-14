package com.arretadogames.pilot.loading;

import java.util.ArrayList;
import java.util.List;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.screens.CharacterSelectionScreen;
import com.arretadogames.pilot.screens.EndScreen;
import com.arretadogames.pilot.screens.GameScreen;
import com.arretadogames.pilot.screens.GameWorldUI;
import com.arretadogames.pilot.screens.LevelSelectionScreen;
import com.arretadogames.pilot.screens.MainMenuScreen;
import com.arretadogames.pilot.screens.PauseScreen;
import com.arretadogames.pilot.screens.SettingsScreen;
import com.arretadogames.pilot.screens.SplashScreen;

public class LoadingResources {
	
	public static int[] getRawResources(GameScreen screen) {
		
		int[] resources = null;
		
		if (screen instanceof SplashScreen) {
			resources = new int[] {
				R.drawable.logo
			};
		} else if (screen instanceof MainMenuScreen) {
			resources = new int[] {
					R.drawable.bt_play_selected,
					R.drawable.bt_play_unselected,
					R.drawable.bt_settings_selected,
					R.drawable.bt_settings_unselected,
					R.drawable.menu_background
			};
		} else if (screen instanceof CharacterSelectionScreen) {
			resources = new int[] {
					R.drawable.bt_play_selected,
					R.drawable.bt_play_unselected,
					R.drawable.blue_selector,
					R.drawable.red_selector,
					R.drawable.selection_lobo_guara,
					R.drawable.selection_arara_azul,
					R.drawable.selection_anonymous
			};
			
		} else if (screen instanceof EndScreen) {
			resources = new int[] {
					R.drawable.victory_bg,
					R.drawable.defeat_bg
			};
		} else if (screen instanceof GameWorldUI) {
			resources = new int[] {
					R.drawable.ui_buttons,
					R.drawable.coin_1_1
			};
		} else if (screen instanceof LevelSelectionScreen) {
			resources = new int[] {
					R.drawable.bt_prev_selected,
					R.drawable.bt_prev_unselected,
					R.drawable.bt_next_selected,
					R.drawable.bt_next_unselected,
					R.drawable.menu_background,
					R.drawable.bt_level_selector,
			};
		} else if (screen instanceof PauseScreen) {
			resources = new int[] {
					R.drawable.pause_menu_bg,
					R.drawable.bt_pause_selected,
			};
		} else if (screen instanceof SettingsScreen) {
			resources = new int[] {
					R.drawable.bt_back_selected,
					R.drawable.bt_back_unselected,
					R.drawable.checked_box,
					R.drawable.unchecked_box
			};
		}
		
		return resources;
	}
	
	private static List<LoadableGLObject> convertToLoadableObjects(int[] drawableResources) {
		List<LoadableGLObject> objects = new ArrayList<LoadableGLObject>();
		
		for (int i = 0 ; i < drawableResources.length ; i++) {
			LoadableGLObject object = new LoadableGLObject(drawableResources[i], LoadableType.TEXTURE);
			objects.add(object);
		}
		
		return objects;
	}
	
	
	public static List<LoadableGLObject> getResourcesFrom(GameScreen screen) {
		return convertToLoadableObjects(getRawResources(screen));
	}

}
