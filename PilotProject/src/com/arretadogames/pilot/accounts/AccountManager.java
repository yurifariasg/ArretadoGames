package com.arretadogames.pilot.accounts;

import com.arretadogames.pilot.googlesync.SyncManager;

public class AccountManager {
	
	private SyncManager syncManager;
	
	private void AccountManager() {
		syncManager = SyncManager.get();
	};
	
	
	
	
	private static AccountManager accountManager = null;
	public static AccountManager get() {
		if (accountManager == null)
			accountManager = new AccountManager();
		return accountManager;
	}
}
