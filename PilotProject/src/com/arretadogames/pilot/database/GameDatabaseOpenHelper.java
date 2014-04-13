package com.arretadogames.pilot.database;

import com.arretadogames.pilot.levels.LevelTable;
import com.arretadogames.pilot.tournaments.TournamentType;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabaseOpenHelper extends SQLiteOpenHelper  {

    private static final int DATABASE_VERSION = 60;

    private static final String DATABASE_NAME = "pilotproject_db";

    private static final String USER_TABLE_CREATE = "CREATE TABLE " +
		    GameDatabase.TABLE_ACCOUNT + " (" +
			GameDatabase.ACCOUNT_ID + " INTEGER PRIMARY KEY, " +
			GameDatabase.ACC_COINS + " INTEGER, " +
			GameDatabase.USER_NAME + " TEXT, " +
			GameDatabase.IMAGE + " NONE, " +
			GameDatabase.PROVIDER_ACC_ID + " TEXT, " +
			GameDatabase.ACC_PROVIDER + " TEXT); ";

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

    private static final String REAL_ITEMS_TABLE_CREATE = "CREATE TABLE " +
		    GameDatabase.TABLE_REAL_ITEMS + " (" +
			GameDatabase.R_ITEM_ID + " INTEGER PRIMARY KEY, " +
			GameDatabase.R_ITEM_NAME + " TEXT, " +
			GameDatabase.R_ITEM_DESCRIPTION + " TEXT, " +
			GameDatabase.R_ITEM_RES_NAME + " TEXT, " +
			GameDatabase.R_ITEM_PRICE + " REAL, " +
			GameDatabase.R_ITEM_SKU_CODE + " TEXT); ";

    private static final String DIGITAL_ITEMS_TABLE_CREATE = "CREATE TABLE " +
		    GameDatabase.TABLE_DIGITAL_ITEMS + " (" +
			GameDatabase.R_ITEM_ID + " INTEGER PRIMARY KEY, " +
			GameDatabase.R_ITEM_NAME + " TEXT, " +
			GameDatabase.R_ITEM_DESCRIPTION + " TEXT, " +
			GameDatabase.R_ITEM_RES_NAME + " TEXT, " +
			GameDatabase.R_ITEM_PRICE + " INTEGER); ";

    private static final String PLAYER_ITEMS_TABLE_CREATE = "CREATE TABLE " +
		    GameDatabase.TABLE_PLAYER_ITEMS + " (" +
			GameDatabase.R_ITEM_ID + " INTEGER PRIMARY KEY, " +
			GameDatabase.R_ITEM_NAME + " TEXT); ";

    private static final String PLAYER_TOURNAMENTS_TABLE_CREATE = "CREATE TABLE " +
    		GameDatabase.TABLE_PLAYER_TOURNAMENTS + " (" +
    		GameDatabase.TOURNAMENT_ID + " INTEGER PRIMARY KEY, " +
    		GameDatabase.T_ENABLED + " BOOLEAN, " +
    		GameDatabase.T_TYPE_NAME + " TEXT); ";

    private static final String PLAYER_TOURNAMENT_LEVELS_CREATE = "CREATE TABLE " +
    		GameDatabase.TABLE_TOURNAMENT_LEVELS + " (" +
    		GameDatabase.TOURNAMENT_ID + " INTEGER , " +
    		GameDatabase.LEVEL_ID + " INTEGER ," +
    		" FOREIGN KEY (" + GameDatabase.TOURNAMENT_ID + ") REFERENCES " + GameDatabase.TABLE_PLAYER_TOURNAMENTS + " ("+GameDatabase.TOURNAMENT_ID+")," +
    		" FOREIGN KEY (" + GameDatabase.LEVEL_ID + ") REFERENCES " + GameDatabase.TABLE_LEVEL + " ("+GameDatabase.LEVEL_ID+"));";

    public GameDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	initializeDB(db);
    }

    private void initializeDB(SQLiteDatabase db) {
        db.execSQL(LEVEL_TABLE_CREATE);
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(REAL_ITEMS_TABLE_CREATE);
        db.execSQL(DIGITAL_ITEMS_TABLE_CREATE);
        db.execSQL(PLAYER_ITEMS_TABLE_CREATE);
        db.execSQL(PLAYER_TOURNAMENTS_TABLE_CREATE);
        db.execSQL(PLAYER_TOURNAMENT_LEVELS_CREATE);


        ContentValues values = new ContentValues();
        // Add Levels
        for(int i = 0; i < LevelTable.LEVELS.length; i++){
	        values.put(GameDatabase.LEVEL_ID, i);
	        values.put(GameDatabase.LEVEL_ENABLED, true);
	        values.put(GameDatabase.RECORD_VALUE_FIRST, -1);
	        values.put(GameDatabase.RECORD_VALUE_SECOND, -1);
	        values.put(GameDatabase.RECORD_VALUE_THIRD, -1);
	        db.insert(GameDatabase.TABLE_LEVEL, null, values);

	        values.clear();
	    }

        // Add Anonymous Account
        values.put(GameDatabase.ACCOUNT_ID, 0);
        values.put(GameDatabase.USER_NAME, "Anonymous");
        values.put(GameDatabase.ACC_COINS, 0);
        values.put(GameDatabase.PROVIDER_ACC_ID, "self");
        values.putNull(GameDatabase.IMAGE);
        values.put(GameDatabase.ACC_PROVIDER, "self");
        db.insert(GameDatabase.TABLE_ACCOUNT, null, values);

        values.clear();

        // Add Tournaments
        for(int i = 0; i < 3; i++){
            System.out.println("Criando Tournament de id : " + i);
        	values.put(GameDatabase.TOURNAMENT_ID, i);
        	if (i == 0){
        		values.put(GameDatabase.T_ENABLED, true);
        	}else{
        		values.put(GameDatabase.T_ENABLED, false);
        	}
        	switch (i) {
			case 0:
				values.put(GameDatabase.T_TYPE_NAME, TournamentType.JUNGLE.toString());
				break;
			case 1:
				values.put(GameDatabase.T_TYPE_NAME, TournamentType.DESERT.toString());
				break;
			case 2:
				values.put(GameDatabase.T_TYPE_NAME, TournamentType.SWAMP.toString());
				break;
			default:
				break;
			}
        	db.insert(GameDatabase.TABLE_PLAYER_TOURNAMENTS, null, values);
        	values.clear();
        }

        // Adding Levels to Tournaments
        for (int i = 0; i < 3; i++) {
	        values.put(GameDatabase.TOURNAMENT_ID, 0);
	        values.put(GameDatabase.LEVEL_ID, i);
	        db.insert(GameDatabase.TABLE_TOURNAMENT_LEVELS, null, values);
	        values.clear();
        }

        StoreInitializeHelper.initializeStore(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    	if (oldVersion < newVersion) {

    		db.execSQL("DROP TABLE IF EXISTS " + GameDatabase.TABLE_LEVEL);
    		db.execSQL("DROP TABLE IF EXISTS " + GameDatabase.TABLE_NEXT_LEVEL);
    		db.execSQL("DROP TABLE IF EXISTS " + GameDatabase.TABLE_ACCOUNT);
    		db.execSQL("DROP TABLE IF EXISTS " + GameDatabase.TABLE_REAL_ITEMS);
    		db.execSQL("DROP TABLE IF EXISTS " + GameDatabase.TABLE_DIGITAL_ITEMS);
    		db.execSQL("DROP TABLE IF EXISTS " + GameDatabase.TABLE_PLAYER_ITEMS);
    		db.execSQL("DROP TABLE IF EXISTS " + GameDatabase.TABLE_PLAYER_TOURNAMENTS);
    		db.execSQL("DROP TABLE IF EXISTS " + GameDatabase.TABLE_TOURNAMENT_LEVELS);

    		initializeDB(db);
    	}
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
    }
}
