package com.arretadogames.pilot.accounts;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.arretadogames.pilot.database.GameDatabase;
import com.arretadogames.pilot.googlesync.SyncManager;
import com.arretadogames.pilot.items.ItemType;
import com.arretadogames.pilot.util.Logger;
import com.google.android.gms.appstate.OnStateLoadedListener;


public class Account implements OnStateLoadedListener {

	public static String PLAYER_1_DEFAULT_ACCOUNT_NAME = "Annonymous1"; // for saving on db
	public static String PLAYER_2_DEFAULT_ACCOUNT_NAME = "Annonymous2"; // for saving on db

	private String accountId;
	private String accountName;
	private boolean isAnnonymous;
	private String name;
	
	private long coins;
	private HashMap<String, Integer> unlockedMaps;
	// add achievements later on..
	

	/**
	 * Default constructor for Annonymous accounts
	 */
	public Account() {
		isAnnonymous = true;
		initMaps();
	}

	/**
	 * Constructor for accounts with an accountId and accountName
	 * 
	 * @param accountId
	 *            Account ID to be used for this account. Must be unique between
	 *            all accounts
	 * @param accountName
	 *            The name of this account
	 */
	public Account(String accountId, String accountName) {
		this.accountId = accountId;
		this.accountName = accountName;
		this.isAnnonymous = false;
		initMaps();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private void initMaps() {
		unlockedMaps = new HashMap<String, Integer>();
		unlockedMaps.put("default", 0); // We should have all level categories here...
	}

	public String getAccountName() {
		return accountName;
	}
	
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	public boolean isAnnonymous() {
		return isAnnonymous;
	}
	
	public void setCoins(long coins) {
		this.coins = coins;
	}
	
	public long getCoins() {
		return coins;
	}
	
	public HashMap<String, Integer> getUnlockedMaps() {
		return unlockedMaps;
	}
	
	public HashMap<ItemType, Integer> getAccountItems() {
		return new HashMap<ItemType, Integer>();
	}
	
	public boolean buyItem(ItemType it){
		return GameDatabase.getInstance().buyItem(it);
	}
	
	private String toJSON() {
		try {
			JSONObject json = new JSONObject();
			json.put("coins", getCoins());
			
			JSONObject levels = new JSONObject();
			for (String key : unlockedMaps.keySet()) {
				levels.put(key, unlockedMaps.get(key));
			}
			json.put("levels", levels);
			System.out.println("toJSON: " + json.toString());
			return json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	private void fromJSON(JSONObject jsonObject) throws JSONException {
		int coins = jsonObject.getInt("coins");
		HashMap<String, Integer> unlockedMaps = new HashMap<String, Integer>();
		JSONObject maps = jsonObject.getJSONObject("levels");
		
		Iterator it = maps.keys();
		while (it.hasNext()) {
			String levelCategory = (String) it.next();
			unlockedMaps.put(levelCategory, maps.getInt(levelCategory));
		}
		
		this.unlockedMaps = unlockedMaps;
		setCoins(coins);
	}
	
	public void saveDatabase() {
		// Perform Savings on the database
	}
	
	public void saveState() {
		if (!SyncManager.get().getAppStateClient().isConnected()) {
			throw new IllegalStateException("App State Client not connected");
		}
		
		String json = toJSON();
		if (json == null) {
			Log.e("Account", "JSON failed to encode.. couldn't save");
		}
		
		byte[] bytes = json.getBytes();
		lastSentBytes = bytes;
		
		SyncManager.get().getAppStateClient().updateStateImmediate(this, 0, bytes);
	}

	public void updateFrom(byte[] bytes) {
		String jsonString = new String(bytes);
		System.out.println("ByteString: " + jsonString);
		
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			fromJSON(jsonObject);
			Logger.v("Loaded Sucessfully");
		} catch (JSONException e) {
			Logger.e("Failed Decoding JSON String when updating account");
			e.printStackTrace();
		}
		
	}
	
	private byte[] lastSentBytes;

	@Override
	public void onStateConflict(int stateKey, String resolvedVersion, byte[] localData, byte[] serverData) {
		System.out.println("State Conflicted");
		
	}

	@Override
	public void onStateLoaded(int statusCode, int stateKey, byte[] localData) {
		System.out.println("State Loaded");
		if (lastSentBytes.equals(localData))
			System.out.println("Bytes are the same");
		else
			System.out.println("Bytes are NOT the same");
		
		System.out.println("State Key: " + stateKey + " (The Same? " + (stateKey == 0) + " )");
	}

	public static Account mergeAccounts(Account acc, Account acc2) {
		
		Account mergedAcc = new Account();
		mergedAcc.setCoins(Math.max(acc.getCoins(), acc2.getCoins()));
		HashMap<String, Integer> levels = acc.getUnlockedMaps();
		
		// Merge Maps..
		for (String key : acc2.getUnlockedMaps().keySet()) {
			if (levels.containsKey(key)) {
				
				// Add the max level reached..
				int levelAcc1 = levels.get(key);
				int levelAcc2 = acc2.getUnlockedMaps().get(key);
				levels.put(key, Math.max(levelAcc1, levelAcc2));
				
			} else { // Just add the key and the value from acc2
				levels.put(key, acc2.getUnlockedMaps().get(key));
			}
			
		}
		
		mergedAcc.unlockedMaps = levels;
		return mergedAcc;
	}
}
