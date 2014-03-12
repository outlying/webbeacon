package com.antyzero.webbeacon.app.network;

import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by tornax on 11.03.14.
 */
public class SimpleWebChromeClient extends WebChromeClient {

    @Override
    public boolean onJsAlert( WebView view, String url, String message, JsResult result ) {
        return super.onJsAlert( view, url, message, result );
    }
}
