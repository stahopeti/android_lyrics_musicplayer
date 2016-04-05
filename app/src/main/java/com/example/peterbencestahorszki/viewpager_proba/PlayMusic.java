package com.example.peterbencestahorszki.viewpager_proba;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PlayMusic extends AppCompatActivity {

    String musicPath = null;

    int musicPosition = 0;
    private String artist;
    private String song;
    private String LYRICS = null;
    private boolean shouldIStart = true;
    private boolean isBakelitForeground = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.playActivityHasStarted = true;
        artist = MainActivity.getArtist();
        song = MainActivity.getSongTitle();

        Bundle bundle = getIntent().getExtras();

        LYRICS = MainActivity.getLYRICS();

        setContentView(R.layout.activity_play_music);

        findViewById(R.id.play_button).setOnClickListener(playStopMusic);
        //findViewById(R.id.refresh_button).setOnClickListener(refreshButtonListener);
        findViewById(R.id.seekBackward).setOnClickListener(seekBackwards);
        findViewById(R.id.seekForward).setOnClickListener(seekForward);

        ((TextView) findViewById(R.id.artist_and_title)).setText(artist + "\n" + song);

        (findViewById(R.id.artist_and_title)).invalidate();

        if(bundle.getBoolean("SHOULD_I_START")) playMusic();

        if(!MainActivity.isMusicPlaying()) findViewById(R.id.play_button).setBackgroundResource(R.drawable.playbutton);
        if(MainActivity.isMusicPlaying()) findViewById(R.id.play_button).setBackgroundResource(R.drawable.pausebutton);

        TextView bkelit = (TextView) findViewById(R.id.bakelit_textview);
        //TextView lyricsTv = (TextView) findViewById(R.id.lyrics_textview);

        RotateAnimation ra = new RotateAnimation(0.0f, 1080.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setInterpolator(new LinearInterpolator());
        ra.setDuration(5000);
        ra.setRepeatCount(-1);
        bkelit.startAnimation(ra);

        RelativeLayout myLL = (RelativeLayout) findViewById(R.id.myLayout);


        myLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("BAKELIT VIEW PRESSED");

                if (isBakelitForeground) {

                    ((ScrollView) findViewById(R.id.scroll_lyrics)).setAlpha(1);
                    ((TextView) findViewById(R.id.lyrics_textview)).setAlpha(1);
                    ((TextView) findViewById(R.id.bakelit_textview)).setAlpha(0);
                    isBakelitForeground = false;
                    refreshLyrics();

                } else {

                    ((ScrollView) findViewById(R.id.scroll_lyrics)).setAlpha(0);
                    ((TextView) findViewById(R.id.lyrics_textview)).setAlpha(0);
                    ((TextView) findViewById(R.id.bakelit_textview)).setAlpha(1);
                    isBakelitForeground = true;

                }

            }
        });

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

        if(MainActivity.shouldIRefreshLyrics) {
            MainActivity.getMusicAndLyrics();
            LYRICS = MainActivity.getLYRICS();
            ((TextView) findViewById(R.id.lyrics_textview)).setText(LYRICS);
            if (LYRICS != null)
                MainActivity.shouldIRefreshLyrics = false;
        }
    }

    private void playMusic(){

        MainActivity.playMusic();
        if(!MainActivity.isMusicPlaying()){

            (findViewById(R.id.play_button)).setBackgroundResource(R.drawable.playbutton);

        }
        if(MainActivity.isMusicPlaying()){

            (findViewById(R.id.play_button)).setBackgroundResource(R.drawable.pausebutton);

        }

    }

    @Override
    public void onDestroy(){

        super.onDestroy();
        MainActivity.shouldIRefreshLyrics = true;

    }

}
