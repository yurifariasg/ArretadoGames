package com.arretadogames.pilot.database;

import com.arretadogames.pilot.levels.LevelTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabaseOpenHelper extends SQLiteOpenHelper  {
	
    private static final int DATABASE_VERSION = 42;
    private static final String DATABASE_NAME = "pilotproject_db";
   
    private static final String LEVEL_TABLE_CREATE = "CREATE TABLE " +
		    GameDatabase.LEVEL_TABLE_NAME + " (" +
			GameDatabase.KEY_LEVEL_ID + " INTEGER PRIMARY KEY, " +
			GameDatabase.BEST_LEVEL_COINS + " INTEGER, " +
			GameDatabase.BEST_LEVEL_COINS_PLAYER + " INTEGER, " +
			GameDatabase.LEVEL_ENABLED + " BOOLEAN); ";

    public GameDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LEVEL_TABLE_CREATE);
        
        ContentValues values = new ContentValues();
        
        for(int i = 0; i < LevelTable.LEVELS.length; i++){
	        values.put(GameDatabase.KEY_LEVEL_ID, i);
	        values.put(GameDatabase.BEST_LEVEL_COINS, -1);
	        values.put(GameDatabase.BEST_LEVEL_COINS_PLAYER, 1); //Bug esta aqui! qual player tem a melhor pontuacao?        
	        values.put(GameDatabase.LEVEL_ENABLED, true);
	        db.insert(GameDatabase.LEVEL_TABLE_NAME, null, values);
	        
	        values.clear();
	    }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	//TODO
    }
   
    @Override
    public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
    }
}
