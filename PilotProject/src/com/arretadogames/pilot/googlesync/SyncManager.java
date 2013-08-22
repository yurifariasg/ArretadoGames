package com.arretadogames.pilot.googlesync;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.arretadogames.pilot.R;
import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.plus.PlusClient;

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

public class SyncManager implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	/** Listener for sign-in success or failure events. */
	public interface GameHelperListener {
		/**
		 * Called when sign-in fails. As a result, a "Sign-In" button can be
		 * shown to the user; when that button is clicked, call
		 * 
		 * @link{GamesHelper#beginUserInitiatedSignIn . Note that not all calls
		 *                                            to this method mean an
		 *                                            error; it may be a result
		 *                                            of the fact that automatic
		 *                                            sign-in could not proceed
		 *                                            because user interaction
		 *                                            was required (consent
		 *                                            dialogs). So
		 *                                            implementations of this
		 *                                            method should NOT display
		 *                                            an error message unless a
		 *                                            call to @link{GamesHelper#
		 *                                            hasSignInError} indicates
		 *                                            that an error indeed
		 *                                            occurred.
		 */
		void onSignInFailed();

		/** Called when sign-in succeeds. */
		void onSignInSucceeded();
	}
	
	private static SyncManager syncManager;
	
	public static void create(Activity activity) {
		if (syncManager == null)
			syncManager = new SyncManager(activity);
	}
	
	public static SyncManager get() {
		return syncManager;
	}
	
	// States we can be in
	public static final int STATE_UNCONFIGURED = 0;
	public static final int STATE_DISCONNECTED = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;

	// State names (for debug logging, etc)
	public static final String[] STATE_NAMES = { "UNCONFIGURED",
			"DISCONNECTED", "CONNECTING", "CONNECTED" };

	// State we are in right now
	int mState = STATE_UNCONFIGURED;

	// Are we expecting the result of a resolution flow?
	boolean mExpectingResolution = false;

	/**
	 * The Activity we are bound to. We need to keep a reference to the Activity
	 * because some games methods require an Activity (a Context won't do). We
	 * are careful not to leak these references: we release them on onStop().
	 */
	Activity mActivity = null;

	// OAuth scopes required for the clients. Initialized in setup().
	String mScopes[];

	// Request code we use when invoking other Activities to complete the
	// sign-in flow.
	final static int RC_RESOLVE = 9001;

	// Request code when invoking Activities whose result we don't care about.
	final static int RC_UNUSED = 9002;

	AppStateClient mAppStateClient = null;
	PlusClient mPlusClient = null;

	// Whether to automatically try to sign in on onStart().
	boolean mAutoSignIn = true;

	/*
	 * Whether user has specifically requested that the sign-in process begin.
	 * If mUserInitiatedSignIn is false, we're in the automatic sign-in attempt
	 * that we try once the Activity is started -- if true, then the user has
	 * already clicked a "Sign-In" button or something similar
	 */
	boolean mUserInitiatedSignIn = false;

	// The connection result we got from our last attempt to sign-in.
	ConnectionResult mConnectionResult = null;

	// The error that happened during sign-in.
	SignInFailureReason mSignInFailureReason = null;

	// Print debug logs?
	boolean mDebugLog = false;
	String mDebugTag = "SyncManager";

	// Listener
	GameHelperListener mListener = null;

	/**
	 * Construct a GameHelper object, initially tied to the given Activity.
	 * After constructing this object, call @link{setup} from the onCreate()
	 * method of your Activity.
	 */
	private SyncManager(Activity activity) {
		mActivity = activity;
	}

	static private final int TYPE_DEVELOPER_ERROR = 1001;
	static private final int TYPE_GAMEHELPER_BUG = 1002;

	boolean checkState(int type, String operation, String warning,
			int... expectedStates) {
		for (int expectedState : expectedStates) {
			if (mState == expectedState) {
				return true;
			}
		}
		StringBuilder sb = new StringBuilder();
		if (type == TYPE_DEVELOPER_ERROR) {
			sb.append("GameHelper: you attempted an operation at an invalid. ");
		} else {
			sb.append("GameHelper: bug detected. Please report it at our bug tracker ");
			sb.append("https://github.com/playgameservices/android-samples/issues. ");
			sb.append("Please include the last couple hundred lines of logcat output ");
			sb.append("and describe the operation that caused this. ");
		}
		sb.append("Explanation: ").append(warning);
		sb.append("Operation: ").append(operation).append(". ");
		sb.append("State: ").append(STATE_NAMES[mState]).append(". ");
		if (expectedStates.length == 1) {
			sb.append("Expected state: ")
					.append(STATE_NAMES[expectedStates[0]]).append(".");
		} else {
			sb.append("Expected states:");
			for (int expectedState : expectedStates) {
				sb.append(" ").append(STATE_NAMES[expectedState]);
			}
			sb.append(".");
		}

		logWarn(sb.toString());
		return false;
	}

	void assertConfigured(String operation) {
		if (mState == STATE_UNCONFIGURED) {
			String error = "GameHelper error: Operation attempted without setup: "
					+ operation
					+ ". The setup() method must be called before attempting any other operation.";
			logError(error);
			throw new IllegalStateException(error);
		}
	}

	/**
	 * Performs setup on this GameHelper object. Call this from the onCreate()
	 * method of your Activity. This will create the clients and do a few other
	 * initialization tasks. Next, call @link{#onStart} from the onStart()
	 * method of your Activity.
	 * 
	 * @param listener
	 *            The listener to be notified of sign-in events.
	 * @param clientsToUse
	 *            The clients to use. Use a combination of CLIENT_GAMES,
	 *            CLIENT_PLUS and CLIENT_APPSTATE, or CLIENT_ALL to request all
	 *            clients.
	 * @param additionalScopes
	 *            Any scopes to be used that are outside of the ones defined in
	 *            the Scopes class. I.E. for YouTube uploads one would add
	 *            "https://www.googleapis.com/auth/youtube.upload"
	 */
	public void setup(GameHelperListener listener, String... additionalScopes) {
		if (mState != STATE_UNCONFIGURED) {
			String error = "GameHelper: you called GameHelper.setup() twice. You can only call "
					+ "it once.";
			logError(error);
			throw new IllegalStateException(error);
		}
		
		mListener = listener;
		mAppStateClient = new AppStateClient.Builder(getContext(), this, this).create();
		mPlusClient = new PlusClient.Builder(getContext(), this, this).build();
		System.out.println("Built");
		setState(STATE_DISCONNECTED);
	}

	void setState(int newState) {
		String oldStateName = STATE_NAMES[mState];
		String newStateName = STATE_NAMES[newState];
		mState = newState;
		debugLog("State change " + oldStateName + " -> " + newStateName);
	}

	/**
	 * Returns the AppStateClient object. In order to call this method, you must
	 * have called @link{#setup} with a set of clients that includes
	 * CLIENT_APPSTATE.
	 */
	public AppStateClient getAppStateClient() {
		if (mAppStateClient == null) {
			throw new IllegalStateException(
					"No AppStateClient. Did you request it at setup?");
		}
		return mAppStateClient;
	}

	/** Returns whether or not the user is signed in. */
	public boolean isSignedIn() {
		return mState == STATE_CONNECTED;
	}

	/**
	 * Returns whether or not there was a (non-recoverable) error during the
	 * sign-in process.
	 */
	public boolean hasSignInError() {
		return mSignInFailureReason != null;
	}

	/**
	 * Returns the error that happened during the sign-in process, null if no
	 * error occurred.
	 */
	public SignInFailureReason getSignInError() {
		return mSignInFailureReason;
	}

	/** Call this method from your Activity's onStart(). */
	public void onStart(Activity act) {
		mActivity = act;

		debugLog("onStart, state = " + STATE_NAMES[mState]);
		assertConfigured("onStart");

		switch (mState) {
		case STATE_DISCONNECTED:
			// we are not connected, so attempt to connect
			if (mAutoSignIn) {
				debugLog("onStart: Now connecting clients.");
				startConnections();
			} else {
				debugLog("onStart: Not connecting (user specifically signed out).");
			}
			break;
		case STATE_CONNECTING:
			// connection process is in progress; no action required
			debugLog("onStart: connection process in progress, no action taken.");
			break;
		case STATE_CONNECTED:
			// already connected (for some strange reason). No complaints :-)
			debugLog("onStart: already connected (unusual, but ok).");
			break;
		default:
			String msg = "onStart: BUG: unexpected state "
					+ STATE_NAMES[mState];
			logError(msg);
			throw new IllegalStateException(msg);
		}
	}

	/** Call this method from your Activity's onStop(). */
	public void onStop() {
		debugLog("onStop, state = " + STATE_NAMES[mState]);
		assertConfigured("onStop");
		switch (mState) {
		case STATE_CONNECTED:
		case STATE_CONNECTING:
			// kill connections
			debugLog("onStop: Killing connections");
			killConnections();
			break;
		case STATE_DISCONNECTED:
			debugLog("onStop: not connected, so no action taken.");
			break;
		default:
			String msg = "onStop: BUG: unexpected state " + STATE_NAMES[mState];
			logError(msg);
			throw new IllegalStateException(msg);
		}

		// let go of the Activity reference
		mActivity = null;
	}

	/** Convenience method to show an alert dialog. */
	public void showAlert(String title, String message) {
		(new AlertDialog.Builder(getContext())).setTitle(title)
				.setMessage(message)
				.setNeutralButton(android.R.string.ok, null).create().show();
	}

	/** Convenience method to show an alert dialog. */
	public void showAlert(String message) {
		(new AlertDialog.Builder(getContext())).setMessage(message)
				.setNeutralButton(android.R.string.ok, null).create().show();
	}

	/** Enables debug logging */
	public void enableDebugLog(boolean enabled, String tag) {
		mDebugLog = enabled;
		mDebugTag = tag;
		if (enabled) {
			debugLog("Debug log enabled, tag: " + tag);
		}
	}

	/**
	 * Returns the current requested scopes. This is not valid until setup() has
	 * been called.
	 * 
	 * @return the requested scopes, including the oauth2: prefix
	 */
	public String getScopes() {
		StringBuilder scopeStringBuilder = new StringBuilder();
		if (null != mScopes) {
			for (String scope : mScopes) {
				addToScope(scopeStringBuilder, scope);
			}
		}
		return scopeStringBuilder.toString();
	}

	/**
	 * Returns an array of the current requested scopes. This is not valid until
	 * setup() has been called
	 * 
	 * @return the requested scopes, including the oauth2: prefix
	 */
	public String[] getScopesArray() {
		return mScopes;
	}

	/** Sign out and disconnect from the APIs. */
	public void signOut() {
		if (mState == STATE_DISCONNECTED) {
			// nothing to do
			debugLog("signOut: state was already DISCONNECTED, ignoring.");
			return;
		}

		// Ready to disconnect
		debugLog("Proceeding with disconnection.");
		killConnections();
	}

	void killConnections() {
		if (!checkState(TYPE_GAMEHELPER_BUG, "killConnections",
				"killConnections() should only "
						+ "get called while connected or connecting.",
				STATE_CONNECTED, STATE_CONNECTING)) {
			return;
		}
		debugLog("killConnections: killing connections.");

		mConnectionResult = null;
		mSignInFailureReason = null;

		if (mAppStateClient != null && mAppStateClient.isConnected()) {
			debugLog("Disconnecting AppStateClient.");
			mAppStateClient.disconnect();
		}
		
		if (mPlusClient != null && mPlusClient.isConnected()) {
			debugLog("Disconnecting mPlusClient.");
			mPlusClient.disconnect();
		}
		
		debugLog("killConnections: all clients disconnected.");
		setState(STATE_DISCONNECTED);
	}

	static String activityResponseCodeToString(int respCode) {
		switch (respCode) {
		case Activity.RESULT_OK:
			return "RESULT_OK";
		case Activity.RESULT_CANCELED:
			return "RESULT_CANCELED";
		case GamesActivityResultCodes.RESULT_APP_MISCONFIGURED:
			return "RESULT_APP_MISCONFIGURED";
		case GamesActivityResultCodes.RESULT_LEFT_ROOM:
			return "RESULT_LEFT_ROOM";
		case GamesActivityResultCodes.RESULT_LICENSE_FAILED:
			return "RESULT_LICENSE_FAILED";
		case GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED:
			return "RESULT_RECONNECT_REQUIRED";
		case GamesActivityResultCodes.RESULT_SIGN_IN_FAILED:
			return "SIGN_IN_FAILED";
		default:
			return String.valueOf(respCode);
		}
	}

	/**
	 * Handle activity result. Call this method from your Activity's
	 * onActivityResult callback. If the activity result pertains to the sign-in
	 * process, processes it appropriately.
	 */
	public void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		debugLog("onActivityResult: req="
				+ (requestCode == RC_RESOLVE ? "RC_RESOLVE" : String
						.valueOf(requestCode)) + ", resp="
				+ activityResponseCodeToString(responseCode));
		if (requestCode != RC_RESOLVE) {
			debugLog("onActivityResult: request code not meant for us. Ignoring.");
			return;
		}

		// no longer expecting a resolution
		mExpectingResolution = false;

		if (mState != STATE_CONNECTING) {
			debugLog("onActivityResult: ignoring because state isn't STATE_CONNECTING ("
					+ "it's " + STATE_NAMES[mState] + ")");
			return;
		}

		// We're coming back from an activity that was launched to resolve a
		// connection problem. For example, the sign-in UI.
		if (responseCode == Activity.RESULT_OK) {
			// Ready to try to connect again.
			debugLog("onAR: Resolution was RESULT_OK, so connecting current client again.");
			connectCurrentClient();
		} else if (responseCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
			debugLog("onAR: Resolution was RECONNECT_REQUIRED, so reconnecting.");
			connectCurrentClient();
		} else if (responseCode == Activity.RESULT_CANCELED) {
			// User cancelled.
			debugLog("onAR: Got a cancellation result, so disconnecting.");
			mAutoSignIn = false;
			mUserInitiatedSignIn = false;
			mSignInFailureReason = null; // cancelling is not a failure!
			killConnections();
			notifyListener(false);
		} else {
			// Whatever the problem we were trying to solve, it was not
			// solved. So give up and show an error message.
			debugLog("onAR: responseCode="
					+ activityResponseCodeToString(responseCode)
					+ ", so giving up.");
			giveUp(new SignInFailureReason(mConnectionResult.getErrorCode(),
					responseCode));
		}
	}

	void notifyListener(boolean success) {
		debugLog("Notifying LISTENER of sign-in "
				+ (success ? "SUCCESS"
						: mSignInFailureReason != null ? "FAILURE (error)"
								: "FAILURE (no error)"));
		if (mListener != null) {
			if (success) {
				mListener.onSignInSucceeded();
			} else {
				mListener.onSignInFailed();
			}
		}
	}

	/**
	 * Starts a user-initiated sign-in flow. This should be called when the user
	 * clicks on a "Sign In" button. As a result, authentication/consent dialogs
	 * may show up. At the end of the process, the GameHelperListener's
	 * onSignInSucceeded() or onSignInFailed() methods will be called.
	 */
	public void beginUserInitiatedSignIn() {
		if (mState == STATE_CONNECTED) {
			// nothing to do
			logWarn("beginUserInitiatedSignIn() called when already connected. "
					+ "Calling listener directly to notify of success.");
			notifyListener(true);
			return;
		} else if (mState == STATE_CONNECTING) {
			logWarn("beginUserInitiatedSignIn() called when already connecting. "
					+ "Be patient! You can only call this method after you get an "
					+ "onSignInSucceeded() or onSignInFailed() callback. Suggestion: disable "
					+ "the sign-in button on startup and also when it's clicked, and re-enable "
					+ "when you get the callback.");
			// ignore call (listener will get a callback when the connection
			// process finishes)
			return;
		}

		debugLog("Starting USER-INITIATED sign-in flow.");

		// sign in automatically on onStart()
		mAutoSignIn = true;

		// Is Google Play services available?
		int result = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getContext());
		debugLog("isGooglePlayServicesAvailable returned " + result);
		if (result != ConnectionResult.SUCCESS) {
			// Google Play services is not available.
			debugLog("Google Play services not available. Show error dialog.");
			mSignInFailureReason = new SignInFailureReason(result, 0);
			showFailureDialog();
			notifyListener(false);
			return;
		}

		// indicate that user is actively trying to sign in (so we know to
		// resolve
		// connection problems by showing dialogs)
		mUserInitiatedSignIn = true;

		if (mConnectionResult != null) {
			// We have a pending connection result from a previous failure, so
			// start with that.
			debugLog("beginUserInitiatedSignIn: continuing pending sign-in flow.");
			setState(STATE_CONNECTING);
			resolveConnectionResult();
		} else {
			// We don't have a pending connection result, so start anew.
			debugLog("beginUserInitiatedSignIn: starting new sign-in flow.");
			startConnections();
		}
	}

	Context getContext() {
		return mActivity;
	}

	void addToScope(StringBuilder scopeStringBuilder, String scope) {
		if (scopeStringBuilder.length() == 0) {
			scopeStringBuilder.append("oauth2:");
		} else {
			scopeStringBuilder.append(" ");
		}
		scopeStringBuilder.append(scope);
	}

	void startConnections() {
		if (!checkState(TYPE_GAMEHELPER_BUG, "startConnections",
				"startConnections should "
						+ "only get called when disconnected.",
				STATE_DISCONNECTED)) {
			return;
		}
		debugLog("Starting connections.");
		setState(STATE_CONNECTING);
		
		connectNextClient();
	}
	
	void connectNextClient() {
		// do we already have all the clients we need?
		if (mAppStateClient != null && mPlusClient != null && 
				mAppStateClient.isConnected() &&
				mPlusClient.isConnected()) {
			logWarn("All states connected");
			succeedSignIn();
			return;
		}

		// which client should be the next one to connect?
		connectCurrentClient();
	}

	void connectCurrentClient() {
		if (mState == STATE_DISCONNECTED) {
			// we got disconnected during the connection process, so abort
			logWarn("GameHelper got disconnected during connection process. Aborting.");
			return;
		}
		if (!checkState(TYPE_GAMEHELPER_BUG, "connectCurrentClient",
				"connectCurrentClient "
						+ "should only get called when connecting.",
				STATE_CONNECTING)) {
			return;
		}
		
		if (!mPlusClient.isConnected())
			mPlusClient.connect();
		else if (!mAppStateClient.isConnected())
			mAppStateClient.connect();
	}

	/**
	 * Disconnects the indicated clients, then connects them again.
	 * 
	 * @param whatClients
	 *            Indicates which clients to reconnect.
	 */
	public void reconnectClients() {
		checkState(TYPE_DEVELOPER_ERROR, "reconnectClients",
				"reconnectClients should "
						+ "only be called when connected. Proceeding anyway.",
				STATE_CONNECTED);
		boolean actuallyReconnecting = false;

		if (mAppStateClient != null && mAppStateClient.isConnected()) {
			debugLog("Reconnecting AppStateClient.");
			actuallyReconnecting = true;
			mAppStateClient.reconnect();
		}

		if (actuallyReconnecting) {
			setState(STATE_CONNECTING);
		} else {
			// No reconnections are to take place, so for consistency we call
			// the listener
			// as if sign in had just succeeded.
			debugLog("No reconnections needed, so behaving as if sign in just succeeded");
			notifyListener(true);
		}
	}

	/** Called when we successfully obtain a connection to a client. */
	@Override
	public void onConnected(Bundle connectionHint) {
		debugLog("onConnected: connected!");

		// connect the next client in line, if any.
		connectNextClient();
	}

	void succeedSignIn() {
		checkState(
				TYPE_GAMEHELPER_BUG,
				"succeedSignIn",
				"succeedSignIn should only "
						+ "get called in the connecting or connected state. Proceeding anyway.",
				STATE_CONNECTING, STATE_CONNECTED);
		debugLog("All requested clients connected. Sign-in succeeded!");
		setState(STATE_CONNECTED);
		mSignInFailureReason = null;
		mAutoSignIn = true;
		mUserInitiatedSignIn = false;
		notifyListener(true);
	}

	/** Handles a connection failure reported by a client. */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// save connection result for later reference
		debugLog("onConnectionFailed");

		mConnectionResult = result;
		debugLog("Connection failure:");
		debugLog("   - code: "
				+ errorCodeToString(mConnectionResult.getErrorCode()));
		debugLog("   - resolvable: " + mConnectionResult.hasResolution());
		debugLog("   - details: " + mConnectionResult.toString());

		if (!mUserInitiatedSignIn) {
			// If the user didn't initiate the sign-in, we don't try to resolve
			// the connection problem automatically -- instead, we fail and wait
			// for the user to want to sign in. That way, they won't get an
			// authentication (or other) popup unless they are actively trying
			// to
			// sign in.
			debugLog("onConnectionFailed: since user didn't initiate sign-in, failing now.");
			mConnectionResult = result;
			setState(STATE_DISCONNECTED);
			notifyListener(false);
			return;
		}

		debugLog("onConnectionFailed: since user initiated sign-in, resolving problem.");

		// Resolve the connection result. This usually means showing a dialog or
		// starting an Activity that will allow the user to give the appropriate
		// consents so that sign-in can be successful.
		resolveConnectionResult();
	}

	/**
	 * Attempts to resolve a connection failure. This will usually involve
	 * starting a UI flow that lets the user give the appropriate consents
	 * necessary for sign-in to work.
	 */
	void resolveConnectionResult() {
		// Try to resolve the problem
		checkState(
				TYPE_GAMEHELPER_BUG,
				"resolveConnectionResult",
				"resolveConnectionResult should only be called when connecting. Proceeding anyway.",
				STATE_CONNECTING);

		if (mExpectingResolution) {
			debugLog("We're already expecting the result of a previous resolution.");
			return;
		}

		debugLog("resolveConnectionResult: trying to resolve result: "
				+ mConnectionResult);
		if (mConnectionResult.hasResolution()) {
			// This problem can be fixed. So let's try to fix it.
			debugLog("Result has resolution. Starting it.");
			try {
				// launch appropriate UI flow (which might, for example, be the
				// sign-in flow)
				mExpectingResolution = true;
				mConnectionResult.startResolutionForResult(mActivity,
						RC_RESOLVE);
			} catch (SendIntentException e) {
				// Try connecting again
				debugLog("SendIntentException, so connecting again.");
				connectCurrentClient();
			}
		} else {
			// It's not a problem what we can solve, so give up and show an
			// error.
			debugLog("resolveConnectionResult: result has no resolution. Giving up.");
			giveUp(new SignInFailureReason(mConnectionResult.getErrorCode()));
		}
	}

	/**
	 * Give up on signing in due to an error. Shows the appropriate error
	 * message to the user, using a standard error dialog as appropriate to the
	 * cause of the error. That dialog will indicate to the user how the problem
	 * can be solved (for example, re-enable Google Play Services, upgrade to a
	 * new version, etc).
	 */
	void giveUp(SignInFailureReason reason) {
		checkState(TYPE_GAMEHELPER_BUG, "giveUp",
				"giveUp should only be called when "
						+ "connecting. Proceeding anyway.", STATE_CONNECTING);
		mAutoSignIn = false;
		killConnections();
		mSignInFailureReason = reason;
		showFailureDialog();
		notifyListener(false);
	}

	/** Called when we are disconnected from a client. */
	@Override
	public void onDisconnected() {
		debugLog("onDisconnected.");
		if (mState == STATE_DISCONNECTED) {
			// This is expected.
			debugLog("onDisconnected is expected, so no action taken.");
			return;
		}

		// Unexpected disconnect (rare!)
		logWarn("Unexpectedly disconnected. Severing remaining connections.");

		// kill the other connections too, and revert to DISCONNECTED state.
		killConnections();
		mSignInFailureReason = null;

		// call the sign in failure callback
		debugLog("Making extraordinary call to onSignInFailed callback");
		notifyListener(false);
	}

	/** Shows an error dialog that's appropriate for the failure reason. */
	void showFailureDialog() {
		Context ctx = getContext();
		if (ctx == null) {
			debugLog("*** No context. Can't show failure dialog.");
			return;
		}
		debugLog("Making error dialog for failure: " + mSignInFailureReason);
		Dialog errorDialog = null;
		int errorCode = mSignInFailureReason.getServiceErrorCode();
		int actResp = mSignInFailureReason.getActivityResultCode();

		switch (actResp) {
		case GamesActivityResultCodes.RESULT_APP_MISCONFIGURED:
			errorDialog = makeSimpleDialog(ctx
					.getString(R.string.gamehelper_app_misconfigured));
			System.out.println("Misconfigured App");
			break;
		case GamesActivityResultCodes.RESULT_SIGN_IN_FAILED:
			errorDialog = makeSimpleDialog(ctx
					.getString(R.string.gamehelper_sign_in_failed));
			break;
		case GamesActivityResultCodes.RESULT_LICENSE_FAILED:
			errorDialog = makeSimpleDialog(ctx
					.getString(R.string.gamehelper_license_failed));
			break;
		default:
			// No meaningful Activity response code, so generate default Google
			// Play services dialog
			errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
					mActivity, RC_UNUSED, null);
			if (errorDialog == null) {
				// get fallback dialog
				debugLog("No standard error dialog available. Making fallback dialog.");
				errorDialog = makeSimpleDialog(ctx
						.getString(R.string.gamehelper_unknown_error)
						+ " "
						+ errorCodeToString(errorCode));
			}
		}

		debugLog("Showing error dialog.");
		errorDialog.show();
	}

	Dialog makeSimpleDialog(String text) {
		return (new AlertDialog.Builder(getContext())).setMessage(text)
				.setNeutralButton(android.R.string.ok, null).create();
	}

	void debugLog(String message) {
		Log.d(mDebugTag, "GameHelper: " + message);
	}

	void logWarn(String message) {
		Log.w(mDebugTag, "!!! GameHelper WARNING: " + message);
	}

	void logError(String message) {
		Log.e(mDebugTag, "*** GameHelper ERROR: " + message);
	}

	static String errorCodeToString(int errorCode) {
		switch (errorCode) {
		case ConnectionResult.DEVELOPER_ERROR:
			return "DEVELOPER_ERROR(" + errorCode + ")";
		case ConnectionResult.INTERNAL_ERROR:
			return "INTERNAL_ERROR(" + errorCode + ")";
		case ConnectionResult.INVALID_ACCOUNT:
			return "INVALID_ACCOUNT(" + errorCode + ")";
		case ConnectionResult.LICENSE_CHECK_FAILED:
			return "LICENSE_CHECK_FAILED(" + errorCode + ")";
		case ConnectionResult.NETWORK_ERROR:
			return "NETWORK_ERROR(" + errorCode + ")";
		case ConnectionResult.RESOLUTION_REQUIRED:
			return "RESOLUTION_REQUIRED(" + errorCode + ")";
		case ConnectionResult.SERVICE_DISABLED:
			return "SERVICE_DISABLED(" + errorCode + ")";
		case ConnectionResult.SERVICE_INVALID:
			return "SERVICE_INVALID(" + errorCode + ")";
		case ConnectionResult.SERVICE_MISSING:
			return "SERVICE_MISSING(" + errorCode + ")";
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
			return "SERVICE_VERSION_UPDATE_REQUIRED(" + errorCode + ")";
		case ConnectionResult.SIGN_IN_REQUIRED:
			return "SIGN_IN_REQUIRED(" + errorCode + ")";
		case ConnectionResult.SUCCESS:
			return "SUCCESS(" + errorCode + ")";
		default:
			return "Unknown error code " + errorCode;
		}
	}

	// Represents the reason for a sign-in failure
	public static class SignInFailureReason {
		public static final int NO_ACTIVITY_RESULT_CODE = -100;
		int mServiceErrorCode = 0;
		int mActivityResultCode = NO_ACTIVITY_RESULT_CODE;

		public int getServiceErrorCode() {
			return mServiceErrorCode;
		}

		public int getActivityResultCode() {
			return mActivityResultCode;
		}

		public SignInFailureReason(int serviceErrorCode, int activityResultCode) {
			mServiceErrorCode = serviceErrorCode;
			mActivityResultCode = activityResultCode;
		}

		public SignInFailureReason(int serviceErrorCode) {
			this(serviceErrorCode, NO_ACTIVITY_RESULT_CODE);
		}

		@Override
		public String toString() {
			return "SignInFailureReason(serviceErrorCode:"
					+ errorCodeToString(mServiceErrorCode)
					+ ((mActivityResultCode == NO_ACTIVITY_RESULT_CODE) ? ")"
							: (",activityResultCode:"
									+ activityResponseCodeToString(mActivityResultCode) + ")"));
		}
	}

	public PlusClient getPlusClient() {
		return mPlusClient;
	}

}