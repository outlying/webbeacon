package com.antyzero.webbeacon.app.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.webkit.WebView;
import com.antyzero.webbeacon.app.BuildConfig;
import com.antyzero.webbeacon.app.R;
import com.antyzero.webbeacon.app.network.SimpleWebChromeClient;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

public class WebViewActivity extends Activity implements BeaconManager.RangingListener, BeaconManager
        .ServiceReadyCallback, BeaconManager.MonitoringListener {

    public static final String EXTRA_URL = "EXTRA_URL";

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final String ESTIMOTE_VIRTUAL_UUID   = "8492E75F-4FD6-469D-B132-043FE94921D8";
    private static final Region ALL_BEACONS = new Region( "regionId", null, null, null );

    private final int AREA_CLOSE = -60;
    private final int AREA_MID = -90;

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

        if( BuildConfig.DEBUG ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        //webView.setWebViewClient( new WebViewClient() );
        webView.setWebChromeClient( new SimpleWebChromeClient() );
        webView.getSettings().setJavaScriptEnabled( true );
        webView.loadUrl( url );

        beaconManager = new BeaconManager( this );
        beaconManager.setRangingListener(this);
        beaconManager.setMonitoringListener( this );
    }

    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.connect( this );
    }

    @Override
    protected void onStop() {

        try {
            beaconManager.stopRanging(ALL_BEACONS);
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
     * @param beacon
     */
    private void beacon( Beacon beacon ) {

        String beaconId = getBeaconId(beacon);
        int range = beacon.getMeasuredPower();

        if( range > AREA_CLOSE ){
          range = 1;
        } else if ( range > AREA_MID && range <= AREA_CLOSE ){
          range = 2;
        } else {
          range = 3;
        }

        String js = String.format("javascript:f%s(%s);", beaconId, range);

        webView.loadUrl(js);
    }

    private String getBeaconId(Beacon beacon){
       return beacon.getProximityUUID().replace("-", "") + beacon.getMajor() + beacon.getMinor();
    }

    @Override
    public void onBeaconsDiscovered( Region region, List<Beacon> beacons ) {

        for( Beacon beacon : beacons ) {
            beacon(beacon);
        }
    }

    @Override
    public void onEnteredRegion(Region region) {
        String.valueOf(region);
    }

    @Override
    public void onExitedRegion(Region region) {
        String.valueOf(region);
    }

    @Override
    public void onServiceReady() {
        try {
            beaconManager.startRanging(ALL_BEACONS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void start( Context context, CharSequence url ) {
        Intent intent = new Intent( context, WebViewActivity.class );
        intent.putExtra( EXTRA_URL, url );
        context.startActivity( intent );
    }
}
