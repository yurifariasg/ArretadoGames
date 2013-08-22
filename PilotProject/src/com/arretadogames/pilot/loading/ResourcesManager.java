package com.arretadogames.pilot.loading;

import java.util.ArrayList;
import java.util.List;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.loading.FontLoader.Fonts;

public class ResourcesManager {
	
	public static Fonts[] getFonts(GameState state) {
		Fonts[] resources = null;
		
		if (state.equals(GameState.SPLASH)) {
			resources = new Fonts[] {
				Fonts.TRANSMETALS
			};
		} else if (state.equals(GameState.MAIN_MENU)) {
			resources = new Fonts[] {
					Fonts.TRANSMETALS
				};
		} else if (state.equals(GameState.CHARACTER_SELECTION)) {
			resources = new Fonts[] {
					Fonts.TRANSMETALS
				};
		} else if (state.equals(GameState.GAME_OVER)) {
			resources = new Fonts[] {
					Fonts.TRANSMETALS
				};
		} else if (state.equals(GameState.RUNNING_GAME)) {
			resources = new Fonts[] {
					Fonts.TRANSMETALS
				};
		} else if (state.equals(GameState.LEVEL_SELECTION)) {
			resources = new Fonts[] {
					Fonts.TRANSMETALS
				};
		}
		
		return resources;
	}
	
	public static int[] getDrawables(GameState state) {
		int[] resources = null;
		
		if (state.equals(GameState.SPLASH)) {
			resources = new int[] {
				R.drawable.logo
			};
		} else if (state.equals(GameState.MAIN_MENU)) {
			resources = new int[] {
					R.drawable.bt_play_selected,
					R.drawable.bt_play_unselected,
					R.drawable.bt_settings_selected,
					R.drawable.bt_settings_unselected,
					R.drawable.menu_background,
					R.drawable.bt_back_selected,
					R.drawable.bt_back_unselected,
					R.drawable.checked_box,
					R.drawable.unchecked_box,
					R.drawable.bt_gplus_unselected,
					R.drawable.bt_gplus_selected
			};
		} else if (state.equals(GameState.CHARACTER_SELECTION)) {
			resources = new int[] {
					R.drawable.bt_play_selected,
					R.drawable.bt_play_unselected,
					R.drawable.blue_selector,
					R.drawable.red_selector,
					R.drawable.selection_lobo_guara,
					R.drawable.selection_arara_azul,
					R.drawable.selection_anonymous
			};
			
		} else if (state.equals(GameState.GAME_OVER)) {
			resources = new int[] {
					R.drawable.victory_bg,
					R.drawable.defeat_bg
			};
		} else if (state.equals(GameState.RUNNING_GAME)) {
			resources = new int[] {
					R.drawable.ui_buttons,
					R.drawable.coin_1_1,
					R.drawable.pause_menu_bg,
					R.drawable.bt_pause_selected,
					R.drawable.stage_background,
					R.drawable.dark
			};
		} else if (state.equals(GameState.LEVEL_SELECTION)) {
			resources = new int[] {
					R.drawable.bt_prev_selected,
					R.drawable.bt_prev_unselected,
					R.drawable.bt_next_selected,
					R.drawable.bt_next_unselected,
					R.drawable.menu_background,
					R.drawable.bt_level_selector,
			};
		}
		
		return resources;
	}
	
//	public static 
	
	private static List<LoadableGLObject> convertDrawablesToLoadableObjects(int[] drawableResources) {
		List<LoadableGLObject> objects = new ArrayList<LoadableGLObject>();
		
		for (int i = 0 ; i < drawableResources.length ; i++) {
			LoadableGLObject object = new LoadableGLObject(drawableResources[i], LoadableType.TEXTURE);
			objects.add(object);
		}
		
		return objects;
	}
	
	private static List<LoadableGLObject> convertFontsToLoadableObjects(Fonts[] fontsResources) {
		List<LoadableGLObject> objects = new ArrayList<LoadableGLObject>();
		
		for (int i = 0 ; i < fontsResources.length ; i++) {
			LoadableGLObject object = new LoadableGLObject(fontsResources[i].ordinal(), LoadableType.FONT);
			objects.add(object);
		}
		
		return objects;
	}
	
	public static List<LoadableGLObject> getResourcesFrom(GameState state) {
		List<LoadableGLObject> objects = new ArrayList<LoadableGLObject>();
		objects.addAll(convertDrawablesToLoadableObjects(getDrawables(state)));
		objects.addAll(convertFontsToLoadableObjects(getFonts(state)));
		return objects;
	}

}
