package com.antyzero.webbeacon.app.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.antyzero.webbeacon.app.BuildConfig;
import com.antyzero.webbeacon.app.R;
import com.antyzero.webbeacon.app.network.SimpleWebChromeClient;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

public class WebViewActivity extends Activity implements BeaconManager.RangingListener, BeaconManager
        .ServiceReadyCallback {

    public static final String EXTRA_URL = "EXTRA_URL";

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region( "regionId", ESTIMOTE_PROXIMITY_UUID, null, null );

    private String url;

    private WebView webView;

    private BeaconManager beaconManager;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_web_view );

        if( !getIntent().hasExtra( EXTRA_URL ) ) {
            throw new IllegalStateException( "Missing url extra" );
        }

        url = String.valueOf( getIntent().getCharSequenceExtra( EXTRA_URL ) );

        webView = (WebView) findViewById( R.id.webView );

        //webView.setWebViewClient( new WebViewClient() );
        webView.setWebChromeClient( new SimpleWebChromeClient() );
        webView.getSettings().setJavaScriptEnabled( true );
        webView.loadUrl( url );

        beaconManager = new BeaconManager( this );
        beaconManager.setRangingListener( this );
    }

    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.connect( this );
    }

    @Override
    protected void onStop() {

        try {
            beaconManager.stopRanging( ALL_ESTIMOTE_BEACONS );
        } catch( RemoteException e ) {
            Log.e( "WebViewActivity", "Cannot stop but it does not matter now", e );
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        beaconManager.disconnect();
        super.onDestroy();
    }

    /**
     * Calls JavaScript
     *
     * @param beaconId
     * @param range
     */
    private void beacon( String beaconId, int range ) {
        webView.loadUrl( String.format( "javascript:%s(%s);", beaconId, range ) );
    }

    @Override
    public void onBeaconsDiscovered( Region region, List<Beacon> beacons ) {

        for( Beacon beacon : beacons ) {
            beacon(beacon.getProximityUUID(), beacon.getMeasuredPower());
        }
    }

    @Override
    public void onServiceReady() {
    }

    public static void start( Context context, CharSequence url ) {
        Intent intent = new Intent( context, WebViewActivity.class );
        intent.putExtra( EXTRA_URL, url );
        context.startActivity( intent );
    }
}
