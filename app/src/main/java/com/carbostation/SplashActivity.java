package com.carbostation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.carbostation.netatmo_sample.SampleHttpClient;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    private static OAuthActivity oauth;

    SampleHttpClient http_client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_splash_screen);

        http_client = SampleHttpClient.getInstance(this);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* OAuth flow first */
                Intent OAuthIntent = new Intent(SplashActivity.this, OAuthActivity.class);
                SplashActivity.this.startActivity(OAuthIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}