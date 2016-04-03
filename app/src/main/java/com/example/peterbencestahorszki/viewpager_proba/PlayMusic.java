package com.example.peterbencestahorszki.viewpager_proba;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PlayMusic extends AppCompatActivity {

    String musicPath = null;

    int musicPosition = 0;
    private String artist;
    private String song;
    private String LYRICS = null;
    private boolean shouldIStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        artist = MainActivity.getArtist();
        song = MainActivity.getSongTitle();

        Bundle bundle = getIntent().getExtras();

        LYRICS = MainActivity.getLYRICS();

        setContentView(R.layout.activity_play_music);

        findViewById(R.id.play_button).setOnClickListener(playStopMusic);
        findViewById(R.id.refresh_button).setOnClickListener(refreshButtonListener);
        findViewById(R.id.seekBackward).setOnClickListener(seekBackwards);
        findViewById(R.id.seekForward).setOnClickListener(seekForward);

        ((TextView) findViewById(R.id.artist_and_title)).setText(artist + "\n" + song);

        (findViewById(R.id.artist_and_title)).invalidate();

        if(bundle.getBoolean("SHOULD_I_START")) playMusic();

        if(!MainActivity.isMusicPlaying()) findViewById(R.id.play_button).setBackgroundResource(R.drawable.playbutton);
        if(MainActivity.isMusicPlaying()) findViewById(R.id.play_button).setBackgroundResource(R.drawable.pausebutton);

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

            musicPosition = MainActivity.music.getCurrentPosition();
            MainActivity.music.seekTo(musicPosition + 500);

        }



    };

    View.OnClickListener seekBackwards = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

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

    private void refreshLyrics(){

        MainActivity.getMusicAndLyrics();
        LYRICS = MainActivity.getLYRICS();
        ((TextView) findViewById(R.id.lyrics_textview)).setText(LYRICS);

    }

    private void playMusic(){

        MainActivity.playMusic();
        if(!MainActivity.isMusicPlaying()){
            (findViewById(R.id.play_button)).setBackgroundResource(R.drawable.playbutton);}
        if(MainActivity.isMusicPlaying()){
            (findViewById(R.id.play_button)).setBackgroundResource(R.drawable.pausebutton);}

        refreshLyrics();

    }


}
