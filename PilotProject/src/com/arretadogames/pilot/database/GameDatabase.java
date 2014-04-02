package com.arretadogames.pilot.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.arretadogames.pilot.accounts.Account;
import com.arretadogames.pilot.database.descriptors.DigitalStoreItemDescriptor;
import com.arretadogames.pilot.database.descriptors.RealStoreItemDescriptor;
import com.arretadogames.pilot.database.descriptors.StoreItemDescriptor;
import com.arretadogames.pilot.items.ItemType;
import com.arretadogames.pilot.levels.LevelDescriptor;

public class GameDatabase {
	
	public static final String TABLE_ACCOUNT = "ACCOUNT";
	public static final String ACCOUNT_ID = "acount_id";
	public static final String ACC_COINS = "account_coins";
	public static final String USER_NAME = "user_name";
	public static final String IMAGE = "user_image";
	public static final String ACC_PROVIDER = "acc_provided_id";
	public static final String PROVIDER_ACC_ID = "provider_acc_id";
	
    public static final String TABLE_LEVEL = "LEVEL";
    public static final String LEVEL_ID = "level_id";
    public static final String LEVEL_ENABLED = "enabled";
    public static final String ACC_ID_RECORD_FIRST = "acc_id_first";
    public static final String RECORD_VALUE_FIRST = "record_value_first";
    public static final String ACC_ID_RECORD_SECOND = "acc_id_second";
    public static final String RECORD_VALUE_SECOND = "record_value_second";
    public static final String ACC_ID_RECORD_THIRD = "acc_id_third";
    public static final String RECORD_VALUE_THIRD = "record_value_third";
    
    public static final String TABLE_REAL_ITEMS = "REAL_ITEMS";
    public static final String R_ITEM_ID = "item_id";
    public static final String R_ITEM_NAME = "item_name";
    public static final String R_ITEM_DESCRIPTION = "item_desc";
    public static final String R_ITEM_PRICE = "item_price";
    public static final String R_ITEM_SKU_CODE = "item_sku_code";
	public static final String R_ITEM_RES_NAME = "item_res_id";
	
	public static final String TABLE_DIGITAL_ITEMS = "DIGITAL_ITEMS";
	
	public static final String TABLE_PLAYER_ITEMS = "PLAYER_ITEMS";
	public static final String R_QUANT_ITEMS = "item_quant";
    
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
    
    public Account getDefaultUser() {
    	Cursor c = db.query(TABLE_ACCOUNT, null, ACC_PROVIDER + " = 'self'", null, null, null, null);
    	c.moveToFirst();
    	
    	Account a = new Account();
    	
    	a.setName(c.getString(c.getColumnIndex(GameDatabase.USER_NAME)));
    	a.setCoins(c.getInt(c.getColumnIndex(GameDatabase.ACC_COINS)));
    	a.setAccountName(c.getString(c.getColumnIndex(GameDatabase.PROVIDER_ACC_ID)));
    	a.setAccountId(String.valueOf(c.getInt(c.getColumnIndex(GameDatabase.ACCOUNT_ID))));
    	
    	c.close();
    	
    	return a;
	}
    
    public ArrayList<LevelDescriptor> getAllLevels(){
    	ArrayList<LevelDescriptor> allLevels = new ArrayList<LevelDescriptor>();
    	
    	Cursor c = db.query(TABLE_LEVEL, null, null, null, null, null, null);
    	c.moveToFirst();
    	
	    while (!c.isAfterLast()) {
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
	
	public List<StoreItemDescriptor> getStoreItems() {
		ArrayList<StoreItemDescriptor> items = new ArrayList<StoreItemDescriptor>();
		for(StoreItemDescriptor item : getAllDigitalStoreItems()){
			items.add(item);
		}
		for(StoreItemDescriptor item : getAllRealStoreItems()){
			items.add(item);
		}
		return items;
	}

	private List<DigitalStoreItemDescriptor> getAllDigitalStoreItems() {
		ArrayList<DigitalStoreItemDescriptor> items = new ArrayList<DigitalStoreItemDescriptor>();
		
		Cursor c = db.query(TABLE_DIGITAL_ITEMS, null, null, null, null, null, null);
    	c.moveToFirst();
    	
    	int nameIndex = c.getColumnIndexOrThrow(R_ITEM_NAME);
    	int descIndex = c.getColumnIndexOrThrow(R_ITEM_DESCRIPTION);
    	int priceIndex = c.getColumnIndexOrThrow(R_ITEM_PRICE);
    	int resIdIndex = c.getColumnIndexOrThrow(R_ITEM_RES_NAME);
    	
	    while(!c.isAfterLast()){
	    	DigitalStoreItemDescriptor item;
	    	item = new DigitalStoreItemDescriptor(
	    			c.getString(nameIndex), c.getString(descIndex),
	    			c.getString(resIdIndex),
	    			c.getInt(priceIndex));
	    	items.add(item);
	        c.moveToNext();
	    }
	    c.close();
		
		
		
		return items;
	}
	
	public int getQuantItems(ItemType itype) {
		Cursor c = db.query(TABLE_PLAYER_ITEMS, null, null, null, null, null, null);
    	c.moveToFirst();
    	
    	int nameIndex = c.getColumnIndexOrThrow(R_ITEM_NAME);
    	int priceIndex = c.getColumnIndexOrThrow(R_QUANT_ITEMS);
	    while(!c.isAfterLast()){
	    			if(c.getString(nameIndex).equals(itype.getValue())){
	    				return c.getInt(priceIndex);
	    			}
	        c.moveToNext();
	    }
	    c.close();
	    
		return 0;
	}
	
	
	
	private List<RealStoreItemDescriptor> getAllRealStoreItems() {
		ArrayList<RealStoreItemDescriptor> items = new ArrayList<RealStoreItemDescriptor>();
		
		Cursor c = db.query(TABLE_REAL_ITEMS, null, null, null, null, null, null);
    	c.moveToFirst();
    	
    	int nameIndex = c.getColumnIndexOrThrow(R_ITEM_NAME);
    	int descIndex = c.getColumnIndexOrThrow(R_ITEM_DESCRIPTION);
    	int priceIndex = c.getColumnIndexOrThrow(R_ITEM_PRICE);
    	int skuIndex = c.getColumnIndexOrThrow(R_ITEM_SKU_CODE);
    	int resIdIndex = c.getColumnIndexOrThrow(R_ITEM_RES_NAME);
    	
	    while(!c.isAfterLast()){
	    	RealStoreItemDescriptor item;
	    	item = new RealStoreItemDescriptor(
	    			c.getString(nameIndex), c.getString(descIndex),
	    			c.getString(resIdIndex), c.getString(skuIndex),
	    			c.getFloat(priceIndex));
	    	items.add(item);
	        c.moveToNext();
	    }
	    c.close();
		
		
		
		return items;
	}

	public boolean useItem(ItemType it) {
		if(getQuantItems(it) <= 0 ) return false;
		ContentValues cv = new ContentValues();
		cv.put(R_QUANT_ITEMS,getQuantItems(it)-1);
		return db.update(TABLE_PLAYER_ITEMS, cv, R_ITEM_NAME + " = " + "\"" + it.getValue()+ "\"" , null) > 0;
	}
	
	public boolean buyItem(ItemType it) {
		ContentValues cv = new ContentValues();
		cv.put(R_QUANT_ITEMS,getQuantItems(it)+1);
		return db.update(TABLE_PLAYER_ITEMS, cv, R_ITEM_NAME + " = " + "\"" + it.getValue()+ "\"" , null) > 0;
	}
	
}
