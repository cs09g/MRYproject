package project.mobilecloud.mry;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.IntentSender.SendIntentException;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mSignInClicked;
    private boolean mIntentInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope("profile"))
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result){
        if(!mIntentInProgress){
            if(mSignInClicked && result.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    result.startResolutionForResult(this, RC_SIGN_IN);

                } catch (SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()){
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent){
        if(requestCode == RC_SIGN_IN){
            if(responseCode != RESULT_OK){
                mSignInClicked = false;
            }
        }

        mIntentInProgress = false;

        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.reconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint){
        mSignInClicked = false;
        Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show();

        // moving to video page after toast
        Intent searchActive = new Intent(this, SearchActivity.class);
        startActivity(searchActive);
    }

    @Override
    public void onConnectionSuspended(int cause){
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
