package com.arretadogames.pilot;

import com.arretadogames.pilot.database.GameDatabase;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.render.opengl.GameGLSurfaceView;
import com.arretadogames.pilot.screens.InputEventHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class LoginActivity extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener, OnClickListener {

    private static final String TAG = "ExampleActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);
        mPlusClient = new PlusClient.Builder(this, this, this)
                .setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
                .build();
        // Progress bar to be displayed if the connection failure is not resolved.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.revoke_access_button).setOnClickListener(this);
        findViewById(R.id.login_play_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
      if (mConnectionProgressDialog.isShowing()) {
        // The user clicked the sign-in button already. Start to resolve
        // connection errors. Wait until onConnected() to dismiss the
        // connection dialog.
        if (result.hasResolution()) {
          try {
                   result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
           } catch (SendIntentException e) {
                   mPlusClient.connect();
           }
        }
      }
      // Save the result and resolve the connection failure upon a user click.
      mConnectionResult = result;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    	mConnectionProgressDialog.dismiss();
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
	        if (mConnectionResult == null) {
	            mConnectionProgressDialog.show();
	        } else {
	            try {
	                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
	            } catch (SendIntentException e) {
	                // Try connecting again.
	                mConnectionResult = null;
	                mPlusClient.disconnect();
	                mPlusClient.connect();
	                
	            }
	        }
	    }
		if(view.getId() == R.id.login_play_button){
			Intent a = new Intent(this,GameActivity.class);
			startActivity(a);
		}
		if (view.getId() == R.id.sign_out_button) {
            if (mPlusClient.isConnected()) {
                mPlusClient.clearDefaultAccount();
                mPlusClient.disconnect();
                mPlusClient.connect();
            }
        }
		if( view.getId() == R.id.revoke_access_button && mPlusClient.isConnected()){
			 // Prior to disconnecting, run clearDefaultAccount().
		    mPlusClient.clearDefaultAccount();

		    
		    mPlusClient.revokeAccessAndDisconnect(new PlusClient.OnAccessRevokedListener() {
		       @Override
		       public void onAccessRevoked(ConnectionResult status) {
		           // mPlusClient is now disconnected and access has been revoked.
		           // Trigger app logic to comply with the developer policies
		    	   mPlusClient.disconnect();
	               mPlusClient.connect();
		       }
		    });
		}
		
	}
}
