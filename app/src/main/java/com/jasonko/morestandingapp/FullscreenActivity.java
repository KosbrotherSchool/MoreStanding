package com.jasonko.morestandingapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class FullscreenActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        LocationListener, OnConnectionFailedListener, MagicLenGCM.MagicLenGCMListener {

    private WebView mWebView;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 1000; // 10 sec
    private static int FATEST_INTERVAL = 500; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    String str_account="";
    String str_password="";

    private MagicLenGCM mGCMManager;

    String myRegID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        Bundle bundle = getIntent().getExtras();
        str_account = bundle.getString("account");
        str_password = bundle.getString("password");

        mGCMManager = new MagicLenGCM(this, this);
        mGCMManager.startGCM();

        SharedPreferences myShared = getSharedPreferences(null, 0);
        myRegID = myShared.getString("regID", "");
        if(!myRegID.equals("")){
            new PostRegIDAsyncTask().execute();
        }

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl("http://114.35.74.182:8086/app/web/login/"+str_account+"/"+str_password);

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if(mWebView.canGoBack()){
                        mWebView.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mLocationRequest != null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (mLocationRequest != null && mLocationRequest != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        ToastLocation();
        startLocationUpdates();
    }

    private void ToastLocation() {
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (mLastLocation!=null) {
//            Toast.makeText(this, mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            new PostLocationTask().execute();
        }else {
            mGoogleApiClient.connect();
//            Toast.makeText(this, "Location is null!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
//        Toast.makeText(this, "Suspended !", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        ToastLocation();
    }

    @Override
    public void gcmRegistered(boolean successfull, String regID) {
//        if (successfull) {
//            Toast.makeText(this, "register gcm success and regID = " + regID, Toast.LENGTH_LONG).show();
//        }else{
//            Toast.makeText(this, "fail to register", Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public boolean gcmSendRegistrationIdToAppServer(String regID) {
        myRegID = regID;
        new PostRegIDAsyncTask().execute();
        return true;
    }

    private class PostRegIDAsyncTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            API.postRegID(str_account, myRegID);
            saveRegIdToSharedPre(mGCMManager.getRegistrationId());
            return null;
        }
    }

    private void saveRegIdToSharedPre(String registrationId) {
        SharedPreferences myPre = getSharedPreferences(null, 0);
        myPre.edit().putString("regID", registrationId).commit();
    }

    private class PushGCMAsyncTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            API.pushGCM(mGCMManager.getRegistrationId(), "test Message", "Test Title", "Test Content");
            return null;
        }
    }


    private class PostLocationTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            API.httpUpdateLocation(str_account, str_password, Double.toString(mLastLocation.getLatitude()), Double.toString(mLastLocation.getLongitude()));
            return null;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "can't get location", Toast.LENGTH_SHORT).show();
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onStop() {
        super.onStop();
            finish();

    }
}
