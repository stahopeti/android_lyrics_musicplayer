package com.example.peterbencestahorszki.viewpager_proba;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayMusic extends AppCompatActivity {


    public MusicPlaybackService myService;
    boolean isBound;
    private BroadcastReceiver broadcastReceiver = null;

    boolean shouldIStart;

    private String LYRICS = null;
    private SharedPreferences sp = MainActivity.context.getSharedPreferences(Constants.XLYRCS_SHARED_PREFS, MODE_PRIVATE);
    private SharedPreferences.Editor editor;

    @BindView(R.id.play_button)
    ImageButton playButton;
    @BindView(R.id.seekBackward)
    ImageButton seekBackwardButton;
    @BindView(R.id.seekForward)
    ImageButton seekForwardButton;
    @BindView(R.id.artist_and_title)
    TextView artistAndTitle;
    @BindView(R.id.lyrics_textview)
    TextView lyricsTextView;
    @BindView(R.id.lyrics_button)
    ImageButton lyricsButton;
    @BindView(R.id.lp_record_textview)
    TextView lpRecordTextView;

    @OnClick(R.id.play_button)
    protected void playStopMusic(){

        try {
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.seekBackward)
    protected void seekBackward(){

        myService.seekBackward();

    }

    @OnClick(R.id.seekForward)
    protected void seekForward(){

        myService.seekForward();

    }

    @OnClick(R.id.lyrics_button)
    protected void onLyricsButton(){

        if (sp.getBoolean(Constants.IS_BAKELIT_FOREGROUND, false)) {

            setBakelitBackground();

        } else {

            setBakelitForeground();

        }

    }

    private void windUpAnimation(){



        RotateAnimation ra = new RotateAnimation(0.0f, 1080.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);


        ra.setInterpolator(new AccelerateInterpolator(1));
        ra.setDuration(7000);
        lpRecordTextView.startAnimation(ra);

        ra.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rotateAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void windDownAnimation(){

        RotateAnimation ra = new RotateAnimation(0.0f, 1080.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);


        ra.setInterpolator(new DecelerateInterpolator(1));
        ra.setDuration(7000);
        lpRecordTextView.startAnimation(ra);

    }

    private void rotateAnimation(){

        RotateAnimation ra = new RotateAnimation(0.0f, 1080.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setInterpolator(new LinearInterpolator());
        ra.setDuration(4500);
        ra.setRepeatCount(-1);
        lpRecordTextView.startAnimation(ra);

    }

    private void registerReciever(){

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.MUSIC_STOPPED);
        filter.addAction(Constants.START_LP);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals(Constants.MUSIC_STOPPED)) {

                    playButton.setBackgroundResource(R.drawable.playbutton);
                    windDownAnimation();
                }
                if(intent.getAction().equals(Constants.START_LP)) {

                    windUpAnimation();

                }

            }
        };
        getApplicationContext().registerReceiver(broadcastReceiver,filter);
    }

    private void setBakelitBackground(){
<<<<<<< HEAD

        (findViewById(R.id.scroll_lyrics)).setAlpha(1);

        lyricsTextView.setAlpha(1);
        lpRecordTextView.setAlpha(0);

=======

        (findViewById(R.id.scroll_lyrics)).setAlpha(1);

        lyricsTextView.setAlpha(1);
        lpRecordTextView.setAlpha(0);

>>>>>>> master
        editor.putBoolean(Constants.IS_BAKELIT_FOREGROUND, false);
        editor.putBoolean(Constants.SHOULD_BAKELIT_BE_FOREGROUND, false);
        editor.commit();
        refreshLyrics();

    }

    private void setBakelitForeground(){

        (findViewById(R.id.scroll_lyrics)).setAlpha(0);

        lyricsTextView.setAlpha(0);
        lpRecordTextView.setAlpha(1);

        editor.putBoolean(Constants.IS_BAKELIT_FOREGROUND, true);
        editor.putBoolean(Constants.SHOULD_BAKELIT_BE_FOREGROUND, true);
        editor.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReciever();
        setContentView(R.layout.activity_play_music);

        ButterKnife.bind(this);

        editor = sp.edit();
        editor.putBoolean(Constants.HAS_PLAY_ACTIVITY_STARTED, true);
        editor.putBoolean(Constants.IS_BAKELIT_FOREGROUND, true);
        Bundle bundle = getIntent().getExtras();

        shouldIStart = bundle.getBoolean("SHOULD_I_START");

        LYRICS = sp.getString(Constants.PLAYING_SONG_LYRICS, null);

        artistAndTitle.setText(sp.getString(Constants.PLAYING_SONG_ARTIST, null) + "\n"
                + sp.getString(Constants.PLAYING_SONG_TITLE, null));

        if(LYRICS != null){

            lyricsTextView.setText(LYRICS);

        }

        artistAndTitle.invalidate();


        if(sp.getBoolean(Constants.SHOULD_BAKELIT_BE_FOREGROUND, false)){

            setBakelitForeground();

        }else{

            setBakelitBackground();

        }

        editor.commit();

        refreshLyrics();

    }

    private void refreshLyrics(){

        if(sp.getBoolean(Constants.SHOULD_I_REFRESH_LYRICS, true)) {
            MainActivity.getMusicAndLyrics();
            LYRICS = sp.getString(Constants.PLAYING_SONG_LYRICS, null);
            if (LYRICS != null){
                editor.putBoolean(Constants.SHOULD_I_REFRESH_LYRICS,false);
                lyricsTextView.setText(LYRICS);

                MusicFile asd = new MusicFile(
                        sp.getString(Constants.PLAYING_SONG_ARTIST, null),
                        sp.getString(Constants.PLAYING_SONG_TITLE, null),
                        sp.getString(Constants.PLAYING_SONG_PATH, null),
                        sp.getString(Constants.PLAYING_SONG_LYRICS, null));

                serialize(
                        sp.getString(Constants.PLAYING_SONG_ARTIST, null),
                        sp.getString(Constants.PLAYING_SONG_TITLE, null),
                        sp.getString(Constants.PLAYING_SONG_PATH, null),
                        sp.getString(Constants.PLAYING_SONG_LYRICS, null)
                );
            }
        }


        editor.commit();
    }

    public void serialize(String artist, String title, String path, String LYRICS){

        MusicFile dummy = new MusicFile(artist,title,path,LYRICS);

        String fileName = getApplicationContext().getFilesDir().getPath().toString() + "serialized.dat";

        File serializedFile = new File(fileName);

        ArrayList<MusicFile> readFromSerialized = new ArrayList<>();
        boolean foundSame = false;

        if (serializedFile.exists()){

            try {
                InputStream in = new FileInputStream(serializedFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(in);
                readFromSerialized = (ArrayList) objectInputStream.readObject();


                for(MusicFile mF : readFromSerialized){

                    if(mF.getPath().equals(dummy.getPath())) foundSame = true;

                }


                objectInputStream.close();
                in.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        if (!serializedFile.exists() || !foundSame){

            try {
                FileOutputStream out = new FileOutputStream(serializedFile);

                readFromSerialized.add(dummy);

                ObjectOutputStream objectOut = new ObjectOutputStream(out);
                objectOut.writeObject(readFromSerialized);

                objectOut.close();
                out.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void playMusic() throws IOException {

        if(myService.isMusicPlaying()){

            playButton.setBackgroundResource(R.drawable.playbutton);

        }
        if(!myService.isMusicPlaying()){

            playButton.setBackgroundResource(R.drawable.pausebutton);

        }
        myService.playPauseMusic();
    }

    @Override
    public void onResume(){

        super.onResume();

        Intent intent = new Intent(getApplicationContext(), MusicPlaybackService.class);
        bindService(intent,mConnect,Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy(){

        super.onDestroy();
        editor.putBoolean(Constants.SHOULD_I_REFRESH_LYRICS, true);
        editor.commit();

        unbindService(mConnect);

    }

    private ServiceConnection mConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlaybackService.LocalBinder binder = (MusicPlaybackService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;

            //animation function
            if(myService.isMusicPlaying() || myService.isMusicPaused()) rotateAnimation();

            try {
                if(shouldIStart) playMusic();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(!myService.isMusicPlaying()) playButton.setBackgroundResource(R.drawable.playbutton);
            if(myService.isMusicPlaying()) playButton.setBackgroundResource(R.drawable.pausebutton);


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

}
