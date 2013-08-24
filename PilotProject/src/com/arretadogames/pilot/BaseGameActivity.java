/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arretadogames.pilot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.googlesync.SyncManager;
import com.google.android.gms.appstate.AppStateClient;


public class BaseGameActivity extends FragmentActivity implements
        SyncManager.GameHelperListener {

    /** Constructs a BaseGameActivity with default client (GamesClient). */
    protected BaseGameActivity() {
        super();
    }

    /**
     * Constructs a BaseGameActivity with the requested clients.
     * @param requestedClients The requested clients (a combination of CLIENT_GAMES,
     *         CLIENT_PLUS and CLIENT_APPSTATE).
     */
    protected BaseGameActivity(int requestedClients) {
        super();
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        SyncManager.create(this);
        SyncManager.get().setup(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SyncManager.get().onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SyncManager.get().onStop();
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        SyncManager.get().onActivityResult(request, response, data);
    }

    protected AppStateClient getAppStateClient() {
        return SyncManager.get().getAppStateClient();
    }

    protected boolean isSignedIn() {
        return SyncManager.get().isSignedIn();
    }

    protected void beginUserInitiatedSignIn() {
    	SyncManager.get().beginUserInitiatedSignIn();
    }

    protected void signOut() {
    	SyncManager.get().signOut();
    }

    protected void showAlert(String title, String message) {
    	SyncManager.get().showAlert(title, message);
    }

    protected void showAlert(String message) {
    	SyncManager.get().showAlert(message);
    }

    protected void reconnectClients() {
    	SyncManager.get().reconnectClients();
    }

    protected String getScopes() {
        return SyncManager.get().getScopes();
    }

    protected String[] getScopesArray() {
        return SyncManager.get().getScopesArray();
    }

    protected boolean hasSignInError() {
        return SyncManager.get().hasSignInError();
    }

    protected SyncManager.SignInFailureReason getSignInError() {
        return SyncManager.get().getSignInError();
    }

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSignInSucceeded() {
		AccountManager.get().refreshAccounts();
	}
}