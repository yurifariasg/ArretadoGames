package com.arretadogames.pilot.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.arretadogames.pilot.levels.LevelDescriptor;

public class GameDatabase {
	
	public static final String TABLE_ACCOUNT = "ACCOUNT";
	public static final String ACCOUNT_ID = "acount_id";
	public static final String ACC_NAME = "acount_name";
	public static final String ACC_COINS = "account_coins";
	public static final String ACC_ID = "acc_id";
	public static final String NAME = "user_name";
	public static final String IMAGE = "user_image";
	public static final String ACC_PROVIDER = "acc_provided_id";
	
    public static final String TABLE_LEVEL = "LEVEL";
    public static final String LEVEL_ID = "level_id";
    public static final String LEVEL_ENABLED = "enabled";
    public static final String ACC_ID_RECORD_FIRST = "acc_id_first";
    public static final String RECORD_VALUE_FIRST = "record_value_first";
    public static final String ACC_ID_RECORD_SECOND = "acc_id_second";
    public static final String RECORD_VALUE_SECOND = "record_value_second";
    public static final String ACC_ID_RECORD_THIRD = "acc_id_third";
    public static final String RECORD_VALUE_THIRD = "record_value_third";
    
    public static final String TABLE_NEXT_LEVEL = "NEXT_LEVEL";
//    public static final String LEVEL_ID = "level_id";
    public static final String ID_NEXT = "next_level";
    
   
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
    	
    	Cursor c = db.query(TABLE_LEVEL, null, null, null, null, null, null);
    	c.moveToFirst();
    	
	    while(!c.isAfterLast()){
	    	LevelDescriptor curLevel = new LevelDescriptor(c.getInt(c.getColumnIndexOrThrow(LEVEL_ID))); 

	        curLevel.setRecords( new int[] {c.getInt( c.getColumnIndexOrThrow(RECORD_VALUE_FIRST)),
	        								c.getInt( c.getColumnIndexOrThrow(RECORD_VALUE_SECOND)),
	        								c.getInt( c.getColumnIndexOrThrow(RECORD_VALUE_THIRD))}  );
	        curLevel.setEnabled (c.getInt( c.getColumnIndexOrThrow(LEVEL_ENABLED))==1 );
	        allLevels.add(curLevel);
	        c.moveToNext();
	    }
	    c.close();
    	
    	return allLevels;
    }
    
    public LevelDescriptor getLevel(int levelID){
    	LevelDescriptor ld = new LevelDescriptor(levelID);
    	return ld;
    }    
    
	public void setNewRecord(int levelId, String accId, int firRec, int secRec, int thiRec ) {
		ContentValues values = new ContentValues();
		values.put(LEVEL_ID, levelId);
        values.put(ACC_ID_RECORD_FIRST, accId);
       	values.put(RECORD_VALUE_FIRST, firRec );
       	values.put(RECORD_VALUE_SECOND, secRec );
       	values.put(RECORD_VALUE_THIRD, thiRec );
        
        db.insertWithOnConflict(TABLE_LEVEL, null, values, SQLiteDatabase.CONFLICT_REPLACE);
	}
	
}
