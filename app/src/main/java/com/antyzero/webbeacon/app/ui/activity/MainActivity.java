package com.antyzero.webbeacon.app.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;

import com.antyzero.webbeacon.app.BuildConfig;
import com.antyzero.webbeacon.app.R;


public class MainActivity extends Activity implements TextWatcher, View.OnClickListener {

    private Button button;
    private EditText editTextUrl;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        button = (Button) findViewById( R.id.button );
        editTextUrl = (EditText) findViewById( R.id.editTextUrl );

        button.setEnabled( BuildConfig.DEBUG );
        button.setOnClickListener( this );

        editTextUrl.setText( "http://" );
        editTextUrl.addTextChangedListener( this );

        if(BuildConfig.DEBUG){
            editTextUrl.setText( "http://192.168.1.84/Workspace/test.html" );
        }
    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {

    }

    @Override
    public void afterTextChanged( Editable s ) {

        if( BuildConfig.DEBUG ) {
            button.setEnabled( URLUtil.isValidUrl( String.valueOf( s ) ) );
        }
    }

    @Override
    public void onClick( View v ) {
        Editable url = editTextUrl.getText();
        WebViewActivity.start( this, url );
    }
}
