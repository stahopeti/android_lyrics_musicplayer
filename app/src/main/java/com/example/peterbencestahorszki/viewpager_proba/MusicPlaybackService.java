package com.example.peterbencestahorszki.viewpager_proba;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by peterbencestahorszki on 2016. 04. 10..
 */
public class MusicPlaybackService extends Service implements MediaPlayer.OnPreparedListener{

    private MediaPlayer player;
    private ArrayList<MusicFile> songs;
    private int position;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){

        super.onCreate();
        position = 0;
        player = new MediaPlayer();
        initialize();

    }

    public void initialize(){

        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }

    public void setList(ArrayList<MusicFile> param){

        songs = param;

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
    }

    public class MusicBinder extends Binder {
        MusicPlaybackService getService() {
            return MusicPlaybackService.this;
        }
    }

}
