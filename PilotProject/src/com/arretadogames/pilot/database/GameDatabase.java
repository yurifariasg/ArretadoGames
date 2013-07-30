package com.arretadogames.pilot.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.arretadogames.pilot.levels.LevelDescriptor;

public class GameDatabase {
	
    public static final String LEVEL_TABLE_NAME = "LEVELS";
    public static final String KEY_LEVEL_ID = "level_id"; // Primary Key
    public static final String BEST_LEVEL_COINS = "coins";
    public static final String BEST_LEVEL_TIME = "time";
    public static final String BEST_LEVEL_COINS_PLAYER = "coins_player";
    public static final String BEST_LEVEL_TIME_PLAYER = "time_player";
    public static final String LEVEL_ENABLED = "enabled";
   
    private static GameDatabase gameDatabase;
   
    private SQLiteDatabase db;
    private boolean started;
   
    private GameDatabase(Context c) {
        db = new GameDatabaseOpenHelper(c).getWritableDatabase();
        started = false;
    }
   
    public static void createDatabase(Context c) {
        gameDatabase = new GameDatabase(c);
    }
   
    public static GameDatabase getInstance() {
        return gameDatabase;
    }
   
    public void setStarted(boolean start){
        this.started = start;
    }
   
    public boolean getStarted(){
        return this.started;
    }
    
    public ArrayList<LevelDescriptor> getAllLevels(){
    	ArrayList<LevelDescriptor> allLevels = new ArrayList<LevelDescriptor>();
    	
    	Cursor c = db.query(LEVEL_TABLE_NAME, null, null, null, null, null, null);
    	c.moveToFirst();
    	
	    while(!c.isAfterLast()){
	    	LevelDescriptor curLevel = new LevelDescriptor(c.getInt(c.getColumnIndexOrThrow(KEY_LEVEL_ID))); 

	        curLevel.setBestCoins( c.getInt( c.getColumnIndexOrThrow(BEST_LEVEL_COINS))  );
	        curLevel.setBestTime( c.getInt(  c.getColumnIndexOrThrow(BEST_LEVEL_TIME))   );
	        curLevel.setEnabled (c.getInt(   c.getColumnIndexOrThrow(LEVEL_ENABLED)) ==1 );
	        allLevels.add(curLevel);
	        c.moveToNext();
	    }
	    c.close();
    	
    	return allLevels;
    }
    
    public LevelDescriptor getLevel(int levelID){
    	//TODO
    	LevelDescriptor ld = new LevelDescriptor(levelID);
    	return ld;
    }

	public void setBestCoins(int coins, int levelId, int playerId) {
		ContentValues values = new ContentValues();
		values.put(KEY_LEVEL_ID, levelId);
        values.put(BEST_LEVEL_COINS, coins);
        values.put(BEST_LEVEL_COINS_PLAYER, playerId );
        System.out.println("SETOU O BEST COINS? "+db.insertWithOnConflict(LEVEL_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE));
	}
	
	public void setBestTime(int time, int levelId, int playerId) {
		ContentValues values = new ContentValues();
		values.put(KEY_LEVEL_ID, levelId);
        values.put(BEST_LEVEL_TIME, time);
        values.put(BEST_LEVEL_TIME_PLAYER, playerId );
        db.insertWithOnConflict(LEVEL_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        System.out.println("SETOU O BEST TIME? "+db.insertWithOnConflict(LEVEL_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE));
	}

}
