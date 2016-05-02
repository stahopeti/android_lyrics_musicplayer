package com.example.peterbencestahorszki.viewpager_proba;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static Context context;
    public MusicPlaybackService myService;
    boolean isBound;

    FragmentPagerAdapter adapterViewPager;
    ViewPager vpPager = null;

    static MediaPlayer music;
    static String tabBrowser;
    static String tabDownloaded;

    static SharedPreferences sp;
    static SharedPreferences.Editor editor;

    public static void setTabName(String tB, String tD){

        tabBrowser = tB;
        tabDownloaded = tD;

    }

    /* beállítja 0-ra a pozíciót, és leállítja a lejátszást*/
    public static void stopMusic(){

        Boolean isMusicPlaying = sp.getBoolean(Constants.IS_MUSIC_PLAYING, false);
        if(isMusicPlaying) {

            music.seekTo(0);
            music.stop();
            editor.putBoolean(Constants.IS_MUSIC_PLAYING, false);
            editor.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
/*
        Intent intent = new Intent(getApplicationContext(), MusicPlaybackService.class);
        bindService(intent,mConnect,Context.BIND_AUTO_CREATE);
*/
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {

            editor.putBoolean(Constants.IS_MUSIC_PLAYING,
                    savedInstanceState.getBoolean(Constants.WAS_MUSIC_PLAYING_BEFORE_BACK_BUTTON));
            editor.putString(Constants.PLAYING_SONG_TITLE,
                    savedInstanceState.getString(Constants.TITLE_BEFORE_BACK_BUTTON));

            editor.commit();

      }

        vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), vpPager);
        vpPager.setAdapter(adapterViewPager);
        context = getApplicationContext();
        final ImageButton docked_button = (ImageButton) findViewById(R.id.docked_playButton);

        TextView playing = (TextView) findViewById(R.id.docked_textview);

        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isMusicPlaying = sp.getBoolean(Constants.IS_MUSIC_PLAYING, false);

                if (isMusicPlaying) {

                    Intent intent = new Intent(context, PlayMusic.class);
                    intent.putExtra("SHOULD_I_START", false);
                    startActivity(intent);
                }

            }
        });

        docked_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean isMusicPlaying = sp.getBoolean(Constants.IS_MUSIC_PLAYING, false);
                Boolean hasPlayActivityStarted = sp.getBoolean(Constants.HAS_PLAY_ACTIVITY_STARTED, false);

                if (!isMusicPlaying) {

                    if (hasPlayActivityStarted) {

                        docked_button.setBackgroundResource(R.drawable.pausebutton);

                    }

                } else {

                    docked_button.setBackgroundResource(R.drawable.playbutton);

                }
            }

        });

        sp = getSharedPreferences(Constants.XLYRCS_SHARED_PREFS, MODE_APPEND);
        editor = sp.edit();
        editor.putString(Constants.PLAYING_SONG_PATH, null);
        editor.putBoolean(Constants.IS_MUSIC_PLAYING, false);
        editor.putBoolean(Constants.HAS_PLAY_ACTIVITY_STARTED, false);
        editor.putBoolean(Constants.SHOULD_I_REFRESH_LYRICS, true);
        editor.putString(Constants.PLAYING_SONG_TITLE, null);
        editor.putString(Constants.PLAYING_SONG_LYRICS, null);
        editor.putBoolean(Constants.SHOULD_BAKELIT_BE_FOREGROUND, true);
        editor.commit();

    }

    public static void setMusicParameters(){

        editor.putInt(Constants.PLAYING_MUSIC_POSITION, 0);
        if(sp.getBoolean(Constants.IS_MUSIC_PLAYING, false)) music.stop();
        editor.commit();

    }

    private static boolean isInternetUp(){

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;

    }

    public static void getMusicAndLyrics(){

        if(isInternetUp()){

            Thread thread = new  Thread(new Runnable(){


                @Override
                public void run() {
                    try{

                        String LYRICS = null;
                        while(LYRICS==null){
                            System.out.println("VAN INTERNET");
                            LYRICS = getSongLyrics(
                                    sp.getString(Constants.PLAYING_SONG_ARTIST, null),
                                    sp.getString(Constants.PLAYING_SONG_TITLE, null)

                            );
                        }
                        System.out.println("PUTTING STRING IN PREFERENCES");
                        editor.putString(Constants.PLAYING_SONG_LYRICS, LYRICS);
                        editor.commit();
                        System.out.println("COMMITED EDITOR !!! COMMITED EDITOR !!!");

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        } else {

        }

    }

    //Inserted code, from api README
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

    public static class MyPagerAdapter extends FragmentPagerAdapter {

        private static int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager, ViewPager vpager) {
            super(fragmentManager);
            vpager = vpager;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show Browser
                    return Browser.newInstance();
                case 1: // Fragment # 0 - This will show Downloaded
                    return Downloaded.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            if(position == 0)return tabBrowser;
            if(position == 1)return tabDownloaded;

            return "default";
        }

    }

    @Override
    public void onStop(){

        super.onStop();
        System.out.println("Main Activity ONSTOP");

    }

    @Override
    public void onDestroy(){

        super.onDestroy();

        Bundle savedInstance = new Bundle();


        System.out.println("Main Activity ONDESTROY");

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        System.out.println("ON SAVE INSTANCE");

        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(Constants.WAS_MUSIC_PLAYING_BEFORE_BACK_BUTTON,
                sp.getBoolean(Constants.IS_MUSIC_PLAYING, false));
        System.out.println("IS MUSIC PLAYING ON SAVING INSTANCE: "
                + sp.getBoolean(Constants.IS_MUSIC_PLAYING, false));

        savedInstanceState.putString(Constants.TITLE_BEFORE_BACK_BUTTON,
                sp.getString(Constants.PLAYING_SONG_TITLE, null));
        System.out.println("TITLE ON SAVING INSTANCE: "
                + sp.getString(Constants.PLAYING_SONG_TITLE, null));

        savedInstanceState.putString(Constants.ARTIST_BEFORE_BACK_BUTTON,
                sp.getString(Constants.PLAYING_SONG_ARTIST, null));
        System.out.println("ARTIST ON SAVING INSTANCE: "
                + sp.getString(Constants.PLAYING_SONG_ARTIST, null));

        savedInstanceState.putString(Constants.LYRICS_BEFORE_BACK_BUTTON,
                sp.getString(Constants.PLAYING_SONG_LYRICS, null));
        System.out.println("LYRICS ON SAVING INSTANCE: "
                + "asdLYRICS");
    }

    @Override
    public void onResume(){

        super.onResume();



        System.out.println("MainActivity ONRESUME");

        Boolean isMusicPlaying = sp.getBoolean(Constants.IS_MUSIC_PLAYING, false);

        if(!isMusicPlaying){

            (findViewById(R.id.docked_playButton)).setBackgroundResource(R.drawable.playbutton);

        }
        if(isMusicPlaying){

            (findViewById(R.id.docked_playButton)).setBackgroundResource(R.drawable.pausebutton);

        }

        String title = sp.getString(Constants.PLAYING_SONG_TITLE, null);

        if(title != null) {
            ((TextView) findViewById(R.id.docked_textview)).setText(title);
        } else ((TextView) findViewById(R.id.docked_textview)).setText(getString(R.string.docked_nothing_playing));

    }

    private ServiceConnection mConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlaybackService.LocalBinder binder = (MusicPlaybackService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

/*
    public static void playMusic(){

        String path = sp.getString(Constants.PLAYING_SONG_PATH, null);
        Boolean isMusicPlaying = sp.getBoolean(Constants.IS_MUSIC_PLAYING, false);
        int musicPosition = sp.getInt(Constants.PLAYING_MUSIC_POSITION, 0);

        if(!isMusicPlaying) {

            Uri musicUri = Uri.parse(path);

            music = MediaPlayer.create(context, musicUri);

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
*/
}
