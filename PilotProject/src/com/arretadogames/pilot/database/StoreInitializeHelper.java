package com.arretadogames.pilot.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class StoreInitializeHelper {
	
	public static void initializeStore(SQLiteDatabase db) {
		
		ContentValues values = new ContentValues();
		
		values.put(GameDatabase.R_ITEM_NAME, "Small Seed Pack");
		values.put(GameDatabase.R_ITEM_DESCRIPTION, "A little pack of seeds");
		values.put(GameDatabase.R_ITEM_PRICE, 1.0);
		values.put(GameDatabase.R_ITEM_RES_NAME, "elephants_power");
		values.put(GameDatabase.R_ITEM_SKU_CODE, "small_seed_pack");
        db.insert(GameDatabase.TABLE_REAL_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, "Medium Seed Pack");
		values.put(GameDatabase.R_ITEM_DESCRIPTION, "A pack with some seeds");
		values.put(GameDatabase.R_ITEM_PRICE, 5.0);
		values.put(GameDatabase.R_ITEM_RES_NAME, "elephants_power");
		values.put(GameDatabase.R_ITEM_SKU_CODE, "medium_seed_pack");
        db.insert(GameDatabase.TABLE_REAL_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, "Big Seed Pack");
		values.put(GameDatabase.R_ITEM_DESCRIPTION, "A pack with A LOT of seeds");
		values.put(GameDatabase.R_ITEM_PRICE, 10.0);
		values.put(GameDatabase.R_ITEM_RES_NAME, "elephants_power");
		values.put(GameDatabase.R_ITEM_SKU_CODE, "big_seed_pack");
        db.insert(GameDatabase.TABLE_REAL_ITEMS, null, values);
        values.clear();
	}

}
