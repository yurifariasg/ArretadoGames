package com.arretadogames.pilot.store;

import android.widget.Toast;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.util.billing.IabHelper;
import com.arretadogames.pilot.util.billing.IabResult;
import com.arretadogames.pilot.util.billing.Purchase;

public class StorePurchaseHandler implements
		IabHelper.OnIabPurchaseFinishedListener,
		IabHelper.OnConsumeFinishedListener {

	private static StorePurchaseHandler handler;

	// Singleton
	private StorePurchaseHandler() {
	};

	public static StorePurchaseHandler get() {
		if (handler == null)
			handler = new StorePurchaseHandler();
		return handler;
	}

	@Override
	public void onConsumeFinished(Purchase purchase, IabResult result) {
		if (purchase.getSku().equals("small_seed_pack")) {
			AccountManager.get().getAccount1()
			.setCoins(AccountManager.get().getAccount1().getCoins() + 100);
		} else if (purchase.getSku().equals("medium_seed_pack")) {
			AccountManager.get().getAccount1()
					.setCoins(AccountManager.get().getAccount1().getCoins() + 500);
		} else if (purchase.getSku().equals("big_seed_pack")) {
			AccountManager.get().getAccount1()
					.setCoins(AccountManager.get().getAccount1().getCoins() + 1000);
		}
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info, IabHelper iabHelper) {
		if (result.isSuccess()) {
			Toast.makeText(MainActivity.getContext(), "Yea! Purchase complete!",
					Toast.LENGTH_SHORT).show();
			iabHelper.consumeAsync(info, this);
		} else {
			// Do nothing..
		}
	}

}
