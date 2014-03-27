package com.arretadogames.pilot.levels;

import java.util.HashMap;
import java.util.Map;

import com.arretadogames.pilot.R;

public class LevelTable {
	
	/* This table holds reference to all our levels */
	public static final int[] LEVELS = {
            R.raw.map, R.raw.map2, R.raw.third_stage,
            R.raw.second_stage, R.raw.fourth_stage,
            R.raw.fifth_stage, R.raw.sixth_stage
	};
	
	public static final Map<TournamentType, Integer> TOURNAMENT_BACKGROUNDS;
    static
    {
    	TOURNAMENT_BACKGROUNDS = new HashMap<TournamentType, Integer>();
    	TOURNAMENT_BACKGROUNDS.put(TournamentType.JUNGLE, R.drawable.jungle_tournament_bg);
    	TOURNAMENT_BACKGROUNDS.put(TournamentType.DESERT, R.drawable.desert_tournament_bg);
    	TOURNAMENT_BACKGROUNDS.put(TournamentType.SWAMP, R.drawable.swamp_tournament_bg);
    }

}
