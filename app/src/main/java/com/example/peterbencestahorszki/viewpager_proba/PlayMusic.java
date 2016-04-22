package com.example.peterbencestahorszki.viewpager_proba;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PlayMusic extends AppCompatActivity {

    private String LYRICS = null;
    private SharedPreferences sp = MainActivity.context.getSharedPreferences(Constants.XLYRCS_SHARED_PREFS, MODE_APPEND);
    private SharedPreferences.Editor editor;
    MusicPlaybackService playbackService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        editor = sp.edit();
        editor.putBoolean(Constants.HAS_PLAY_ACTIVITY_STARTED, true);
        editor.putBoolean(Constants.IS_BAKELIT_FOREGROUND, true);
        Bundle bundle = getIntent().getExtras();

        LYRICS = sp.getString(Constants.PLAYING_SONG_LYRICS, null);

        setContentView(R.layout.activity_play_music);

        findViewById(R.id.play_button).setOnClickListener(playStopMusic);
        findViewById(R.id.seekBackward).setOnClickListener(seekBackwards);
        findViewById(R.id.seekForward).setOnClickListener(seekForward);

        ((TextView) findViewById(R.id.artist_and_title)).setText(
                sp.getString(Constants.PLAYING_SONG_ARTIST, null) +
                "\n" +
                sp.getString(Constants.PLAYING_SONG_TITLE, null));

        if(LYRICS != null){

            ((TextView) findViewById(R.id.lyrics_textview)).setText(LYRICS);

        }

        (findViewById(R.id.artist_and_title)).invalidate();

        if(bundle.getBoolean("SHOULD_I_START")) playMusic();

        Boolean isMusicPlaying = sp.getBoolean(Constants.IS_MUSIC_PLAYING, false);

        if(!isMusicPlaying) findViewById(R.id.play_button).setBackgroundResource(R.drawable.playbutton);
        if(isMusicPlaying) findViewById(R.id.play_button).setBackgroundResource(R.drawable.pausebutton);

        TextView lpRecord = (TextView) findViewById(R.id.lp_record_textview);

        RotateAnimation ra = new RotateAnimation(0.0f, 1080.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setInterpolator(new LinearInterpolator());
        ra.setDuration(5000);
        ra.setRepeatCount(-1);
        lpRecord.startAnimation(ra);

        RelativeLayout myLL = (RelativeLayout) findViewById(R.id.myLayout);


        ImageButton lyricsButton = (ImageButton) findViewById(R.id.lyrics_button);

        if(sp.getBoolean(Constants.SHOULD_BAKELIT_BE_FOREGROUND, false)){

            setBakelitForeground();

            }else{

            setBakelitBackground();

        }

        lyricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("BAKELIT VIEW PRESSED");

                if (sp.getBoolean(Constants.IS_BAKELIT_FOREGROUND, false)) {

                    setBakelitBackground();

                } else {

                    setBakelitForeground();

                }

            }
        });

        editor.commit();
        refreshLyrics();

    }


    View.OnClickListener refreshButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

        refreshLyrics();

        }
    };

    View.OnClickListener seekForward = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int musicPosition;

            musicPosition = MainActivity.music.getCurrentPosition();
            MainActivity.music.seekTo(musicPosition + 500);

        }



    };

    View.OnClickListener seekBackwards = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int musicPosition;

            musicPosition = MainActivity.music.getCurrentPosition();
            MainActivity.music.seekTo(musicPosition - 500);

        }



    };

    View.OnClickListener playStopMusic = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            playMusic();

        }

    };

    private void setBakelitBackground(){

        (findViewById(R.id.scroll_lyrics)).setAlpha(1);
        (findViewById(R.id.lyrics_textview)).setAlpha(1);
        (findViewById(R.id.lp_record_textview)).setAlpha(0);
        editor.putBoolean(Constants.IS_BAKELIT_FOREGROUND, false);
        editor.putBoolean(Constants.SHOULD_BAKELIT_BE_FOREGROUND, false);
        editor.commit();
        refreshLyrics();

    }

    private void setBakelitForeground(){

        (findViewById(R.id.scroll_lyrics)).setAlpha(0);
        (findViewById(R.id.lyrics_textview)).setAlpha(0);
        (findViewById(R.id.lp_record_textview)).setAlpha(1);
        editor.putBoolean(Constants.IS_BAKELIT_FOREGROUND, true);
        editor.putBoolean(Constants.SHOULD_BAKELIT_BE_FOREGROUND, true);
        editor.commit();

    }

    private void refreshLyrics(){

        if(sp.getBoolean(Constants.SHOULD_I_REFRESH_LYRICS, true)) {
            MainActivity.getMusicAndLyrics();
            LYRICS = sp.getString(Constants.PLAYING_SONG_LYRICS, null);
            if (LYRICS != null){
                editor.putBoolean(Constants.SHOULD_I_REFRESH_LYRICS,false);
                ((TextView) findViewById(R.id.lyrics_textview)).setText(LYRICS);

                MusicFile asd = new MusicFile(
                        sp.getString(Constants.PLAYING_SONG_ARTIST, null),
                        sp.getString(Constants.PLAYING_SONG_TITLE, null),
                        sp.getString(Constants.PLAYING_SONG_PATH, null),
                        sp.getString(Constants.PLAYING_SONG_LYRICS, null));

                System.out.println("EZEKET KELLENE SERIALIZALNI: " +
                        sp.getString(Constants.PLAYING_SONG_PATH, null));

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

        System.out.println("Serializalando: " + dummy.toString());

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

            System.out.println("FÁJL NEM LÉTEZIK");

            try {
                FileOutputStream out = new FileOutputStream(serializedFile);

                readFromSerialized.add(dummy);
                System.out.println("SERIALIZALT FAJLOK SZAMA: " + readFromSerialized.size());

                ObjectOutputStream objectOut = new ObjectOutputStream(out);
                objectOut.writeObject(readFromSerialized);

                objectOut.close();
                out.close();

                System.out.println("!! SERIALIZALAS KESZ !!");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void playMusic(){

        Boolean isMusicPlaying
                = sp.getBoolean(Constants.IS_MUSIC_PLAYING, false);



        if(isMusicPlaying){

            (findViewById(R.id.play_button)).setBackgroundResource(R.drawable.playbutton);

        }
        if(!isMusicPlaying){

            (findViewById(R.id.play_button)).setBackgroundResource(R.drawable.pausebutton);

        }
        MainActivity.playMusic();
    }

    @Override
    public void onDestroy(){

        super.onDestroy();
        editor.putBoolean(Constants.SHOULD_I_REFRESH_LYRICS, true);
        editor.commit();

    }

}
