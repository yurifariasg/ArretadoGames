package com.arretadogames.pilot.accounts;

import java.util.Iterator;

import android.util.Log;

import com.arretadogames.pilot.database.GameDatabase;
import com.arretadogames.pilot.googlesync.SyncManager;
import com.arretadogames.pilot.util.Logger;
import com.google.android.gms.appstate.AppState;
import com.google.android.gms.appstate.AppStateBuffer;
import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.appstate.OnStateListLoadedListener;

public class AccountManager implements OnStateListLoadedListener {
	
	private SyncManager syncManager;
	
	private static Account defaultUser;
	
	private Account account1;
	private Account account2;
	
	private AccountManager() {
		syncManager = SyncManager.get();
		defaultUser = GameDatabase.getInstance().getDefaultUser();
		account1 = defaultUser;
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
			System.out.println("Requesting List States");
			// App State Client supports only one player, so we suppose it is player one
//			account1 = null; // Avoid wrong info
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
		if (SyncManager.get().isSignedIn()) {
			if (account1 != null) {
				account1.saveState();
			}
			
			if (account2 != null) {
	//			account2.saveState(); Currently it is only supported one account
			}
		}
	}

	@Override
	public void onStateListLoaded(int statusCode, AppStateBuffer buffer) {
		if (statusCode == AppStateClient.STATUS_OK) {
			System.out.println("List States OK");
			
			Iterator<AppState> it = buffer.iterator();
			if (!it.hasNext()) {
				Logger.v("User has no previous state... creating default account");
				String accName = SyncManager.get().getPlusClient().getAccountName();
				account1 = new Account(accName, accName);
				account1.setName(SyncManager.get().getPlusClient().getCurrentPerson().getName().getGivenName());
				System.out.println("Acc Name: " + accName);
				System.out.println("Name: " + account1.getName());
				return;
			}
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
						loadedAccount.setName(SyncManager.get().getPlusClient().getCurrentPerson().getName().getGivenName());
						System.out.println("Loaded");
						
					} else {
						// Data is fine! Just load...
						Logger.v("Trying to load version: " + state.getLocalVersion());
						byte[] bytes = state.getLocalData();
						loadedAccount = new Account(
								syncManager.getPlusClient().getAccountName(),
								syncManager.getPlusClient().getAccountName());
						loadedAccount.setName(SyncManager.get().getPlusClient().getCurrentPerson().getName().getGivenName());
						loadedAccount.updateFrom(bytes);
					}
					
					account1 = loadedAccount;
					
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

	public void clearArrount1() {
		account1 = defaultUser;
//		account1 = new Account();
//		account1.setAccountId(Account.PLAYER_1_DEFAULT_ACCOUNT_NAME);
//		account1.setAccountName(Account.PLAYER_1_DEFAULT_ACCOUNT_NAME);
	}
}
