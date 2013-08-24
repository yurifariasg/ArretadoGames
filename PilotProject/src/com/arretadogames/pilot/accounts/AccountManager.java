package com.arretadogames.pilot.accounts;

import java.util.Iterator;

import android.util.Log;

import com.arretadogames.pilot.googlesync.SyncManager;
import com.arretadogames.pilot.util.Logger;
import com.google.android.gms.appstate.AppState;
import com.google.android.gms.appstate.AppStateBuffer;
import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.appstate.OnStateListLoadedListener;

public class AccountManager implements OnStateListLoadedListener {
	
	private SyncManager syncManager;
	private Account account1;
	private Account account2;
	
	private AccountManager() {
		syncManager = SyncManager.get();
		account1 = new Account();
		account2 = new Account();
	}
	
	public Account getAccount1() {
		return account1;
	}
	
	public Account getAccount2() {
		return account2;
	}
	
	public void refreshAccounts() {
		if (syncManager.getAppStateClient().isConnected()) {
			// App State Client supports only one player, so we suppose it is player one
			syncManager.getAppStateClient().listStates(this);
			
		}
	}
	
	public void saveDatabase() {
		if (account1 != null) {
			account1.saveDatabase();
		}
		
		if (account2 != null) {
			account2.saveDatabase();
		}
	}
	
	public void saveState() {
		if (account1 != null) {
			account1.saveState();
		}
		
		if (account2 != null) {
//			account2.saveState(); Currently it is only supported one account
		}
	}

	@Override
	public void onStateListLoaded(int statusCode, AppStateBuffer buffer) {
		if (statusCode == AppStateClient.STATUS_OK) {
			
			Iterator<AppState> it = buffer.iterator();
			while (it.hasNext()) {
				AppState state = it.next();
				
				if (state.getKey() == 0 && state.getLocalData() != null) {
					Account loadedAccount;
					if (state.hasConflict()) {
						// Load both accounts
						byte[] bytes = state.getLocalData();
						Account acc = new Account(
								syncManager.getPlusClient().getAccountName(),
								syncManager.getPlusClient().getAccountName());
						acc.updateFrom(bytes);
						
						byte[] bytesConflicted = state.getConflictData();
						Account accConflicted = new Account(
								syncManager.getPlusClient().getAccountName(),
								syncManager.getPlusClient().getAccountName());
						accConflicted.updateFrom(bytesConflicted);
						
						// Now, merge them
						loadedAccount = Account.mergeAccounts(acc, accConflicted);
						
					} else {
						// Data is fine! Just load...
						Logger.v("Trying to load version: " + state.getLocalVersion());
						byte[] bytes = state.getLocalData();
						loadedAccount = new Account(
								syncManager.getPlusClient().getAccountName(),
								syncManager.getPlusClient().getAccountName());
						loadedAccount.updateFrom(bytes);
					}
					
					account1 = loadedAccount;
					
				} else if (state.getKey() == 0) {
					// First Time Loading..
					Logger.v("First Time Loading! Welcome!");
				} else {
					Logger.v("Another Key came up: " + state.getKey());
					Logger.v("with version: " + state.getLocalVersion());
				}
			}
			
		} else {
			// Something wrong..
			Log.e("AccountManager", "State List Loaded failed with error code: " + statusCode);
		}
	}
	

	private static AccountManager accountManager = null;
	public static AccountManager get() {
		if (accountManager == null)
			accountManager = new AccountManager();
		return accountManager;
	}
}
