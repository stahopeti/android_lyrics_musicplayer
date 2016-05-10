package com.example.peterbencestahorszki.viewpager_proba;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by peterbencestahorszki on 2016. 04. 10..
 */
public class MusicPlaybackService extends Service{

    private final IBinder binder = new LocalBinder();
    boolean isMusicPlaying = false;
    boolean musicPaused = false;

    String TAG = "MusicPlayback";

    private MediaPlayer music = null;
    private MusicFile currentlyPlaying = null;
    private ArrayList<MusicFile> songs;
    private int position;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.i(TAG, "service onBind");

        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Service OnBind",Toast.LENGTH_SHORT).show();
            }
        });

        sp = getApplicationContext().getSharedPreferences(Constants.XLYRCS_SHARED_PREFS, MODE_PRIVATE);

/*        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Intent intent = new Intent(Constants.MUSIC_STOPPED);
                sendBroadcast(intent);
                try {
                    setNewMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
*/
        return binder;

    }

    public boolean isMusicPaused() {
        return musicPaused;
    }

    public boolean isCurrentlyPlayingNull(){

        if(currentlyPlaying == null) return true;

        return false;

    }

    public MusicFile getCurrentlyPlaying(){

        return currentlyPlaying;

    }

    public void setNewMusic() throws IOException {

        Log.i(TAG, "service setNewMusic: " + sp.getString(Constants.PLAYING_SONG_TITLE, null));

         String s = (sp.getString(Constants.PLAYING_SONG_ARTIST, null) + "\n" +
                sp.getString(Constants.PLAYING_SONG_TITLE, null) + "\n" +
                sp.getString(Constants.PLAYING_SONG_PATH, null) + "\n" +
                sp.getString(Constants.PLAYING_SONG_LYRICS, null) + "\n");



        currentlyPlaying = new MusicFile(
                sp.getString(Constants.PLAYING_SONG_ARTIST, null),
                sp.getString(Constants.PLAYING_SONG_TITLE, null),
                sp.getString(Constants.PLAYING_SONG_PATH, null),
                sp.getString(Constants.PLAYING_SONG_LYRICS, null)
        );

        Uri musicUri = Uri.parse(currentlyPlaying.getPath());

        if(music == null) music = MediaPlayer.create(getApplicationContext(), musicUri);
            else{

            music.release();
            music = MediaPlayer.create(getApplicationContext(), musicUri);

//            music.setDataSource(getApplicationContext(), musicUri);

        }

        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Intent intent = new Intent(Constants.MUSIC_STOPPED);
                sendBroadcast(intent);

                Log.i(TAG, "PLAYBACK COMPLETED");

                currentlyPlaying = null;
                isMusicPlaying = false;
            }
        });

        music.seekTo(0);

    }

    public void playPauseMusic() throws IOException {

        Log.i(TAG, "service playPause");

        if(currentlyPlaying == null){

            Intent intent = new Intent(Constants.START_LP);
            sendBroadcast(intent);

            setNewMusic();
            music.start();
            musicPaused = false;
            isMusicPlaying = true;

        } else {
            if(!currentlyPlaying.getPath().equals(sp.getString(Constants.PLAYING_SONG_PATH, null))){

                setNewMusic();
                music.start();
                musicPaused = false;
                isMusicPlaying = true;

            } else {

                if(isMusicPlaying){

                    position = music.getCurrentPosition();
                    music.pause();
                    musicPaused = true;
                    isMusicPlaying = false;

                } else {

                    music.seekTo(position);
                    music.start();
                    musicPaused = false;
                    isMusicPlaying = true;

                }

            }

        }

    }

    public void seekForward(){

        music.seekTo(music.getCurrentPosition() + 10000);

    }

    public void seekBackward(){

        music.seekTo(music.getCurrentPosition() - 1000);

    }

    public boolean isMusicPlaying(){

        Log.i(TAG, "service isMusicPlaying");

        return isMusicPlaying;

    }

    private void startForeground(){

//        Notification notification = new Notification(R.drawable.green_lp_sized, "Zenelejatszo", System.currentTimeMillis());
//        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

    }

    @Override
    public void onDestroy(){

        Log.i(TAG, "SERVICE ONDESTROY");
        super.onDestroy();
        music.release();

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
