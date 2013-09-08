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
		    GameDatabase.TABLE_LEVEL + " (" +
			GameDatabase.LEVEL_ID + " INTEGER PRIMARY KEY, " +
			GameDatabase.ACC_ID_RECORD_FIRST + " INTEGER, " +
			GameDatabase.RECORD_VALUE_FIRST + " INTEGER, " +
			GameDatabase.ACC_ID_RECORD_SECOND + " INTEGER, " +
			GameDatabase.RECORD_VALUE_SECOND + " INTEGER, " +
			GameDatabase.ACC_ID_RECORD_THIRD + " INTEGER, " +
			GameDatabase.RECORD_VALUE_THIRD + " INTEGER, " +
			GameDatabase.LEVEL_ENABLED + " BOOLEAN); ";

    public GameDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LEVEL_TABLE_CREATE);
        
        ContentValues values = new ContentValues();
        
        for(int i = 0; i < LevelTable.LEVELS.length; i++){
	        values.put(GameDatabase.LEVEL_ID, i);
	        values.put(GameDatabase.LEVEL_ENABLED, true);
	        values.put(GameDatabase.RECORD_VALUE_FIRST, -1);
	        values.put(GameDatabase.RECORD_VALUE_SECOND, -1);
	        values.put(GameDatabase.RECORD_VALUE_THIRD, -1); 
	        db.insert(GameDatabase.TABLE_LEVEL, null, values);
	        
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
