package com.example.peterbencestahorszki.viewpager_proba;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SplashScreen extends AppCompatActivity {

    public MusicPlaybackService myService;
    boolean serviceIsBound = false;
    private BroadcastReceiver broadcastReceiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sp = getSharedPreferences(Constants.XLYRCS_SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(!sp.getBoolean(Constants.FIRST_RUN,true))
        {
            editor.putBoolean(Constants.FIRST_RUN, true);
            editor.commit();
        }


        final Intent intent = new Intent(this, MainActivity.class);
        ((ImageButton) findViewById(R.id.splash_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    public void onResume(){

        super.onResume();

        Intent intent = new Intent(getApplicationContext(), MusicPlaybackService.class);
        startService(intent);
        bindService(intent, mConnect, Context.BIND_AUTO_CREATE);

        SharedPreferences sp = getSharedPreferences(Constants.XLYRCS_SHARED_PREFS, Context.MODE_PRIVATE);

        boolean firstRun = sp.getBoolean(Constants.FIRST_RUN, false);




    }

    @Override
    public void onBackPressed(){



    }

    private ServiceConnection mConnect = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlaybackService.LocalBinder binder = (MusicPlaybackService.LocalBinder) service;
            myService = binder.getService();
            serviceIsBound = true;

            if(!myService.isCurrentlyPlayingNull()){

                Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent2);
                finish();

            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceIsBound = false;
        }

    };

}
