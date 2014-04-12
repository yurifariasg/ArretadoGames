package com.arretadogames.pilot.database;

import com.arretadogames.pilot.items.ItemType;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class StoreInitializeHelper {
	
	public static void initializeStore(SQLiteDatabase db) {
		
		ContentValues values = new ContentValues();
		
		values.put(GameDatabase.R_ITEM_NAME, "Small Seed Pack");
		values.put(GameDatabase.R_ITEM_DESCRIPTION, "A little pack of seeds");
		values.put(GameDatabase.R_ITEM_PRICE, 1.0);
		values.put(GameDatabase.R_ITEM_RES_NAME, "small_bag");
		values.put(GameDatabase.R_ITEM_SKU_CODE, "small_seed_pack");
        db.insert(GameDatabase.TABLE_REAL_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, "Medium Seed Pack");
		values.put(GameDatabase.R_ITEM_DESCRIPTION, "A pack with some seeds");
		values.put(GameDatabase.R_ITEM_PRICE, 5.0);
		values.put(GameDatabase.R_ITEM_RES_NAME, "medium_bag");
		values.put(GameDatabase.R_ITEM_SKU_CODE, "medium_seed_pack");
        db.insert(GameDatabase.TABLE_REAL_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, "Big Seed Pack");
		values.put(GameDatabase.R_ITEM_DESCRIPTION, "A pack with A LOT of seeds");
		values.put(GameDatabase.R_ITEM_PRICE, 10.0);
		values.put(GameDatabase.R_ITEM_RES_NAME, "big_bag");
		values.put(GameDatabase.R_ITEM_SKU_CODE, "big_seed_pack");
        db.insert(GameDatabase.TABLE_REAL_ITEMS, null, values);
        values.clear();

        // Uncomment when the item is added to the game
//        values.put(GameDatabase.R_ITEM_NAME, ItemType.Coconut.getName());
//		values.put(GameDatabase.R_ITEM_DESCRIPTION, "A coconut to throw at your enemies! HAAAA");
//		values.put(GameDatabase.R_ITEM_PRICE, 10);
//		values.put(GameDatabase.R_ITEM_RES_NAME, "it_superjump");
//        db.insert(GameDatabase.TABLE_DIGITAL_ITEMS, null, values);
//        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, ItemType.WaterWalk.getName());
		values.put(GameDatabase.R_ITEM_DESCRIPTION, "Be just like Jesus!");
		values.put(GameDatabase.R_ITEM_PRICE, 10);
		values.put(GameDatabase.R_ITEM_RES_NAME, "waterwalk_item");
        db.insert(GameDatabase.TABLE_DIGITAL_ITEMS, null, values);
        values.clear();
        
        // Uncomment when the item is added to the game
//        values.put(GameDatabase.R_ITEM_NAME, ItemType.Mine.getName());
//		values.put(GameDatabase.R_ITEM_DESCRIPTION, "I would not step on that...");
//		values.put(GameDatabase.R_ITEM_PRICE, 10);
//		values.put(GameDatabase.R_ITEM_RES_NAME, "it_speed");
//        db.insert(GameDatabase.TABLE_DIGITAL_ITEMS, null, values);
//        values.clear();
        
        // These are the INITIAL Box Items
        values.put(GameDatabase.R_ITEM_NAME, ItemType.Mine.getName());
        db.insert(GameDatabase.TABLE_PLAYER_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, ItemType.Coconut.getName());
        db.insert(GameDatabase.TABLE_PLAYER_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, ItemType.WaterWalk.getName());
        db.insert(GameDatabase.TABLE_PLAYER_ITEMS, null, values);
        values.clear();
	}

}
