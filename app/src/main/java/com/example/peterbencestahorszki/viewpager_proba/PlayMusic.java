package com.example.peterbencestahorszki.viewpager_proba;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayMusic extends AppCompatActivity {

    String musicPath = null;

    private String title;
    private int page;
    private boolean isMusicPlaying = false;
    MediaPlayer music;
    int musicPosition = 0;
    private String artist;
    private String song;
    private String LYRICS = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();


        if(bundle != null) musicPath = bundle.getString("pathForMusic");
        setContentView(R.layout.activity_play_music);

        findViewById(R.id.play_button).setOnClickListener(playStopMusic);
        findViewById(R.id.refresh_button).setOnClickListener(refreshButtonListener);
        findViewById(R.id.seekBackward).setOnClickListener(seekBackwards);
        findViewById(R.id.seekForward).setOnClickListener(seekForward);

        getArtistAndTitle();

        ((TextView) findViewById(R.id.artist_and_title)).setText(artist + "\n" + song);
        ((TextView) findViewById(R.id.artist_and_title)).invalidate();

        getMusicAndLyrics();

    }

    private boolean isInternetUp(){

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;

    }

    View.OnClickListener refreshButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            getMusicAndLyrics();

        }
    };

    View.OnClickListener seekForward = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            musicPosition = music.getCurrentPosition();
            music.seekTo(musicPosition + 500);

        }



    };

    View.OnClickListener seekBackwards = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            musicPosition = music.getCurrentPosition();
            music.seekTo(musicPosition - 500);

        }



    };

    View.OnClickListener playStopMusic = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            playMusic();

        }



    };

    private void getArtistAndTitle(){

        if(musicPath!= null) {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(musicPath);

            artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            song = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }
    }

    private void getMusicAndLyrics(){
        if(isInternetUp()){

            Thread thread = new  Thread(new Runnable(){


                @Override
                public void run() {
                    try{

                        LYRICS = null;
                        while(LYRICS==null){
                            System.out.println("VAN INTERNET");
                            LYRICS = getSongLyrics(
                                    artist,//((TextView) findViewById(R.id.artist)).getText().toString(),
                                    song//((TextView) findViewById(R.id.song)).getText().toString()

                            );
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ((TextView) findViewById(R.id.lyrics_textview)).setText(LYRICS);
                                ((TextView) findViewById(R.id.lyrics_textview)).invalidate();
                            }
                        });

                    }catch(Exception e){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(LYRICS==null) {
                                    LYRICS = "NOLYRICS";
                                    ((TextView) findViewById(R.id.lyrics_textview)).setText(LYRICS);
                                    ((TextView) findViewById(R.id.lyrics_textview)).invalidate();
                                }
                            }
                        });
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        } else {

            LYRICS = "INTERNET\n IST \n AUSGEHEN";

        }


    }

    private void playMusic(){

        if(!isMusicPlaying) {

            Uri musicUri = Uri.parse(musicPath);

            music = MediaPlayer.create(this, musicUri);

            music.seekTo(musicPosition);
            music.start();
            findViewById(R.id.play_button).setBackgroundResource(R.drawable.pausebutton);
            isMusicPlaying=true;

        } else {

            musicPosition = music.getCurrentPosition();
            music.stop();
            findViewById(R.id.play_button).setBackgroundResource(R.drawable.playbutton);
            isMusicPlaying=false;
        }


    }

    public static String getSongLyrics( String band, String songTitle) throws IOException {
        List<String> lyrics= new ArrayList<String>();

        String songLyricsURL = "http://www.songlyrics.com";
        Document doc = Jsoup.connect(songLyricsURL + "/" + band.replace(" ", "-").toLowerCase() + "/" + songTitle.replace(" ", "-").toLowerCase() + "-lyrics/").get();
        String title = doc.title();
        System.out.println(title);
        Element p = doc.select("p.songLyricsV14").get(0);
        for (Node e: p.childNodes()) {
            if (e instanceof TextNode) {
                lyrics.add(((TextNode)e).getWholeText());
            }
        }

        String lyricsString = "";

        for(String s : lyrics){

            lyricsString += s;

        }

        return lyricsString;
    }

    @Override
    public void onStart(){

        super.onStart();
    }


    @Override
    public void onStop(){

        super.onStop();
        System.out.println("PLAY MUSIC ONSTOP");

    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }


}
