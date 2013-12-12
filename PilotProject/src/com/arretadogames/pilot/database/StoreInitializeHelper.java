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
        
        values.put(GameDatabase.R_ITEM_NAME, ItemType.SUPER_JUMP.getValue());
		values.put(GameDatabase.R_ITEM_DESCRIPTION, "Jump twice higher than before");
		values.put(GameDatabase.R_ITEM_PRICE, 10);
		values.put(GameDatabase.R_ITEM_RES_NAME, "it_superjump");
        db.insert(GameDatabase.TABLE_DIGITAL_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, ItemType.SUPER_JUMP.getValue());
		values.put(GameDatabase.R_QUANT_ITEMS, 11);
        db.insert(GameDatabase.TABLE_PLAYER_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, ItemType.DOUBLE_JUMP.getValue());
		values.put(GameDatabase.R_ITEM_DESCRIPTION, "Extra jump in the air");
		values.put(GameDatabase.R_ITEM_PRICE, 10);
		values.put(GameDatabase.R_ITEM_RES_NAME, "it_double_jump");
        db.insert(GameDatabase.TABLE_DIGITAL_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, ItemType.DOUBLE_JUMP.getValue());
		values.put(GameDatabase.R_QUANT_ITEMS, 0);
        db.insert(GameDatabase.TABLE_PLAYER_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, ItemType.SUPER_VELOCITY.getValue());
		values.put(GameDatabase.R_ITEM_DESCRIPTION, "More velocity");
		values.put(GameDatabase.R_ITEM_PRICE, 10);
		values.put(GameDatabase.R_ITEM_RES_NAME, "it_speed");
        db.insert(GameDatabase.TABLE_DIGITAL_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, ItemType.SUPER_VELOCITY.getValue());
		values.put(GameDatabase.R_QUANT_ITEMS, 0);
        db.insert(GameDatabase.TABLE_PLAYER_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, ItemType.SUPER_STRENGHT.getValue());
		values.put(GameDatabase.R_ITEM_DESCRIPTION, "More strength");
		values.put(GameDatabase.R_ITEM_PRICE, 10);
		values.put(GameDatabase.R_ITEM_RES_NAME, "it_strength");
        db.insert(GameDatabase.TABLE_DIGITAL_ITEMS, null, values);
        values.clear();
        
        values.put(GameDatabase.R_ITEM_NAME, ItemType.SUPER_STRENGHT.getValue());
		values.put(GameDatabase.R_QUANT_ITEMS, 0);
        db.insert(GameDatabase.TABLE_PLAYER_ITEMS, null, values);
        values.clear();
	}

}
