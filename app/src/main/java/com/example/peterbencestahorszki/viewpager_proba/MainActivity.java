package com.example.peterbencestahorszki.viewpager_proba;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.ButterKnife.*;
import butterknife.OnClick;
import butterknife.Optional;


public class MainActivity extends AppCompatActivity {

    static String TAG = "xLyrics.MainActivity";

    public static Context context;

    public MusicPlaybackService myService;
    boolean serviceIsBound = false;
    private BroadcastReceiver broadcastReceiver = null;

    FragmentPagerAdapter adapterViewPager;

    static String tabBrowser;
    static String tabDownloaded;

    static SharedPreferences sp;
    static SharedPreferences.Editor editor;

    @BindView(R.id.docked_playButton)
    ImageButton docked_playButton;

    @BindView(R.id.docked_textview)
    TextView docked_textView;

    @BindView(R.id.vpPager)
    ViewPager vpPager;

    @OnClick(R.id.docked_textview)
    protected void onTextView(){

        if (!myService.isCurrentlyPlayingNull()) {

            Intent intent = new Intent(context, PlayMusic.class);
            intent.putExtra("SHOULD_I_START", false);
            startActivity(intent);
        }

    }

    @OnClick(R.id.docked_playButton)
    protected void onPlayButon(){

        if (!myService.isCurrentlyPlayingNull()) {
            if (myService.isMusicPlaying()) {

                try {
                    myService.playPauseMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                docked_playButton.setBackgroundResource(R.drawable.playbutton);

            } else {

                try {
                    myService.playPauseMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                docked_playButton.setBackgroundResource(R.drawable.pausebutton);

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = getApplicationContext();

        registerReciever();

        sp = getSharedPreferences(Constants.XLYRCS_SHARED_PREFS, MODE_PRIVATE);
        editor = sp.edit();

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), vpPager);
        vpPager.setAdapter(adapterViewPager);

        editor.putBoolean(Constants.IS_MUSIC_PLAYING, false);
        editor.putBoolean(Constants.HAS_PLAY_ACTIVITY_STARTED, false);
        editor.putBoolean(Constants.SHOULD_I_REFRESH_LYRICS, true);
        editor.putBoolean(Constants.SHOULD_BAKELIT_BE_FOREGROUND, true);
        editor.putBoolean(Constants.FIRST_RUN, false);
        editor.commit();

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

    private void registerReciever(){

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.MUSIC_STOPPED);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

               docked_playButton.setBackgroundResource(R.drawable.playbutton);

            }
        };
        getApplicationContext().registerReceiver(broadcastReceiver,filter);
    }

    public void setDockedView(){

        MusicFile dummy = myService.getCurrentlyPlaying();

        if(myService.getCurrentlyPlaying()!=null)

            docked_textView.setText(dummy.getTitle());

        if (myService.isMusicPlaying()) {

            docked_playButton.setBackgroundResource(R.drawable.pausebutton);

        } else {

            docked_playButton.setBackgroundResource(R.drawable.playbutton);

        }

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

                            LYRICS = getSongLyrics(
                                    sp.getString(Constants.PLAYING_SONG_ARTIST, null),
                                    sp.getString(Constants.PLAYING_SONG_TITLE, null)

                            );

                        }

                        editor.putString(Constants.PLAYING_SONG_LYRICS, LYRICS);
                        editor.commit();

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        } else {

        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Log.i(TAG, "onSaveInstance");

        savedInstanceState.putString(Constants.TITLE_BEFORE_BACK_BUTTON,
                sp.getString(Constants.PLAYING_SONG_TITLE, null));
        Log.i(TAG,"TITLE ON SAVING INSTANCE: "
                + sp.getString(Constants.PLAYING_SONG_TITLE, null));

        savedInstanceState.putString(Constants.ARTIST_BEFORE_BACK_BUTTON,
                sp.getString(Constants.PLAYING_SONG_ARTIST, null));
        Log.i(TAG,"ARTIST ON SAVING INSTANCE: "
                + sp.getString(Constants.PLAYING_SONG_ARTIST, null));


        savedInstanceState.putString(Constants.PATH_BEFORE_BACK_BUTTON,
                sp.getString(Constants.PLAYING_SONG_PATH, null));
        Log.i(TAG,"PATH ON SAVING INSTANCE: "
                + sp.getString(Constants.PLAYING_SONG_PATH, null));

        super.onSaveInstanceState(savedInstanceState);


    }

    @Override
    public void onStart(){

            super.onStart();

    }

    @Override
    public void onResume(){


        super.onResume();

        Log.i(TAG, "onResume playing song title: " + sp.getString(Constants.PLAYING_SONG_TITLE, "default"));
        Log.i(TAG, "onResume playing song artist: " + sp.getString(Constants.PLAYING_SONG_ARTIST, "default"));
        Log.i(TAG, "onResume playing song path: " + sp.getString(Constants.PLAYING_SONG_PATH, "default"));

        if(serviceIsBound) setDockedView();

            else {
            Intent intent = new Intent(getApplicationContext(), MusicPlaybackService.class);
            startService(intent);
            bindService(intent, mConnect, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public void onStop(){

        super.onStop();

    }

    @Override
    public void onDestroy(){

        super.onDestroy();

        unbindService(mConnect);

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

    private ServiceConnection mConnect = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlaybackService.LocalBinder binder = (MusicPlaybackService.LocalBinder) service;
            myService = binder.getService();
            serviceIsBound = true;

            setDockedView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceIsBound = false;
        }

    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
