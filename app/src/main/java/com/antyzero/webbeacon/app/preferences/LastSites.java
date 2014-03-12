package com.antyzero.webbeacon.app.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tornax on 11.03.14.
 */
public class LastSites {

    private LastSites(){

    }

    public static void addSite(Context context, CharSequence site){

        SharedPreferences shared = context.getSharedPreferences( "someNam", Context.MODE_PRIVATE );


    }
}
