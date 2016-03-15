package com.example.peterbencestahorszki.viewpager_proba;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by peterbencestahorszki on 2016. 03. 08..
 */
public class Player extends Fragment {

    private String title;
    private int page;
    private boolean isMusicPlaying = false;
    MediaPlayer music;
    int musicPosition = 0;
    private String artist;
    private String song;
    private String LYRICS;

    public static Player newInstance(int page, String title){

        Player sf = new Player();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someString", title);
        sf.setArguments(args);
        return sf;

    }

    @Override
    public void onCreate(Bundle savedInstance){

        super.onCreate(savedInstance);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someString");

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.musicplay_fragment, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.artist_and_title);
        tvLabel.setText("Zene");

        view.findViewById(R.id.play_button).setOnClickListener(playStopMusic);
        view.findViewById(R.id.refresh_button).setOnClickListener(refreshButtonListener);
        view.findViewById(R.id.seekBackward).setOnClickListener(seekBackwards);
        view.findViewById(R.id.seekForward).setOnClickListener(seekForward);

        return view;
    }

    private boolean isInternetUp(){

        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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

            getMusicAndLyrics();
            playMusic();

        }



    };

    private void getMusicAndLyrics(){



        Resources res = getResources();
        AssetFileDescriptor afd = res.openRawResourceFd(R.raw.aceofspades);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        retriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

        artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        song = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        if(isInternetUp()){

            Thread thread = new  Thread(new Runnable(){


                @Override
                public void run() {
                    try{

                        LYRICS = null;
                        while(LYRICS==null){
                            System.out.println("VAN INTERNET");
                            LYRICS = Player.getSongLyrics(
                                    artist,//((TextView) findViewById(R.id.artist)).getText().toString(),
                                    song//((TextView) findViewById(R.id.song)).getText().toString()

                            );
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ((TextView) getView().findViewById(R.id.lyrics_textview)).setText(LYRICS);
                                ((TextView) getView().findViewById(R.id.lyrics_textview)).invalidate();
                                ((TextView) getView().findViewById(R.id.artist_and_title)).setText(artist + "\n" + song);
                                ((TextView) getView().findViewById(R.id.artist_and_title)).invalidate();


                            }
                        });

                    }catch(Exception e){
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

                music = MediaPlayer.create(getContext(), R.raw.aceofspades);

                music.seekTo(musicPosition);
                music.start();
                getView().findViewById(R.id.play_button).setBackgroundResource(R.drawable.pausebutton);
                isMusicPlaying=true;

            } else {

                musicPosition = music.getCurrentPosition();
                music.stop();
                getView().findViewById(R.id.play_button).setBackgroundResource(R.drawable.playbutton);
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

}
