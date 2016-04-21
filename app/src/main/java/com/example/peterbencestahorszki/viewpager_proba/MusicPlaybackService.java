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

import java.util.ArrayList;

/**
 * Created by peterbencestahorszki on 2016. 04. 10..
 */
public class MusicPlaybackService extends Service{

    private MediaPlayer music;
    private ArrayList<MusicFile> songs;
    private int position;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        //if intent wants another music
        if(!intent.getStringExtra(Constants.INTENT_PATH).equals(sp.getString(Constants.PLAYING_SONG_PATH,null))){

            Uri musicUri = Uri.parse(intent.getStringExtra(Constants.INTENT_PATH));

            music = MediaPlayer.create(getApplicationContext(), musicUri);

            music.seekTo(position);
            music.start();
            editor.putBoolean(Constants.IS_MUSIC_PLAYING, true);

        } else {



        }

        return null;
    }



    public void playMusic(){

        String path = sp.getString(Constants.PLAYING_SONG_PATH, null);
        Boolean isMusicPlaying = sp.getBoolean(Constants.IS_MUSIC_PLAYING, false);
        int musicPosition = sp.getInt(Constants.PLAYING_MUSIC_POSITION, 0);

        if(!isMusicPlaying) {

            Uri musicUri = Uri.parse(path);

            music = MediaPlayer.create(getApplicationContext(), musicUri);

            music.seekTo(musicPosition);
            music.start();
            editor.putBoolean(Constants.IS_MUSIC_PLAYING, true);

        } else {

            editor.putInt(Constants.PLAYING_MUSIC_POSITION, music.getCurrentPosition());
            music.stop();
            editor.putBoolean(Constants.IS_MUSIC_PLAYING, false);

        }

        editor.commit();
    }

    @Override
    public void onDestroy(){

    }
}
