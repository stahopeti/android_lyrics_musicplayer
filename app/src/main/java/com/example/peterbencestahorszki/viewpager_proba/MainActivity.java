package com.example.peterbencestahorszki.viewpager_proba;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static Context context;

    FragmentPagerAdapter adapterViewPager;
    ViewPager vpPager = null;
    public static int number = 0;
    private static String artist;
    private static String musicPath;
    private static String title;
    private static String LYRICS;
    private static boolean isMusicPlaying = false;
    public static MediaPlayer music;
    private static int musicPosition;
    public static String tabBrowser;
    public static String tabDownloaded;


    public static String getArtist() {
        return artist;
    }

    public static String getMusicPath() {
        return musicPath;
    }

    public static String getSongTitle() {
        return title;
    }

    public static String getLYRICS() {
        return LYRICS;
    }

    public static boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    public static int getMusicPosition() {
        return musicPosition;
    }

    public static void setTabName(String tB, String tD){

        tabBrowser = tB;
        tabDownloaded = tD;

    }

    /* beállítja 0-ra a pozíciót, és leállítja a lejátszást*/
    public static void stopMusic(){

        if(isMusicPlaying) {

            music.seekTo(0);
            music.stop();
            isMusicPlaying = false;

        }
    }

    /* beállítja 0-ra a pozíciót, és elindítja a lejátszást*/
    public static void startMusic(){

        if(!isMusicPlaying){

            music.seekTo(0);
            music.start();
            isMusicPlaying = true;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), vpPager);
        vpPager.setAdapter(adapterViewPager);
        context = getApplicationContext();

        setTabName(getString(R.string.tab_name_browser),getString(R.string.tab_name_downloaded));

    }

    public static void setMusicParameters(String pathParam, String artistParam, String titleParam){

        musicPath = pathParam;
        artist = artistParam;
        title = titleParam;
        musicPosition = 0;
        if(isMusicPlaying) music.stop();

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

                        LYRICS = null;
                        while(LYRICS==null){
                            System.out.println("VAN INTERNET");
                            LYRICS = getSongLyrics(
                                    artist,//((TextView) findViewById(R.id.artist)).getText().toString(),
                                    title//((TextView) findViewById(R.id.title)).getText().toString()

                            );
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        } else {


        }


    }

    public static void playMusic(){

        if(!isMusicPlaying) {

            Uri musicUri = Uri.parse(musicPath);

            music = MediaPlayer.create(context, musicUri);

            music.seekTo(musicPosition);
            music.start();
            isMusicPlaying=true;

        } else {

            musicPosition = music.getCurrentPosition();
            music.stop();
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

    public static class MyPagerAdapter extends FragmentPagerAdapter {

        private static int NUM_ITEMS = 2;

        ViewPager vpagerAdapter;

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
                    return Browser.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show Browser different title
                    return Downloaded.newInstance(1, "Page # 2");
            //    case 2: // Fragment # 1 - This will show Player
            //       return Downloaded.newInstance(2, "Page # 3");
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


}
