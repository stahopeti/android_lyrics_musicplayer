package com.example.peterbencestahorszki.viewpager_proba;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by peterbencestahorszki on 2016. 04. 10..
 */
public class MusicPlaybackService extends Service{

    private final IBinder binder = new LocalBinder();
    boolean isMusicPlaying = false;

    String TAG = "MusicPlayback";

    private MediaPlayer music;
    private MusicFile currentlyPlaying = null;
    private ArrayList<MusicFile> songs;
    private int position;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.i(TAG, "service onBind");
        sp = getApplicationContext().getSharedPreferences(Constants.XLYRCS_SHARED_PREFS, MODE_PRIVATE);
        return binder;

    }

    public void setNewMusic(){

        Log.i(TAG, "service setNewMusic: " + sp.getString(Constants.PLAYING_SONG_TITLE, null));

        currentlyPlaying = new MusicFile(
                sp.getString(Constants.PLAYING_SONG_ARTIST, null),
                sp.getString(Constants.PLAYING_SONG_TITLE, null),
                sp.getString(Constants.PLAYING_SONG_PATH, null),
                sp.getString(Constants.PLAYING_SONG_LYRICS, null)
        );

        Uri musicUri = Uri.parse(currentlyPlaying.getPath());

        music = MediaPlayer.create(getApplicationContext(), musicUri);

        music.seekTo(0);

    }

    public void playPauseMusic(){

        Log.i(TAG, "service playPause");

        if(currentlyPlaying == null){

            setNewMusic();
            music.start();

        } else {

            if(!currentlyPlaying.getPath().equals(sp.getString(Constants.PLAYING_SONG_PATH, null))){

                setNewMusic();
                music.start();

            } else {

                if(isMusicPlaying){

                    position = music.getCurrentPosition();
                    music.pause();
                    isMusicPlaying = false;

                } else {

                    music.seekTo(position);
                    music.start();
                    isMusicPlaying = true;

                }

            }

        }

    }

    public boolean isMusicPlaying(){

        Log.i(TAG, "service isMusicPlaying");

        return isMusicPlaying;

    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public MusicPlaybackService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicPlaybackService.this;
        }
    }

}
