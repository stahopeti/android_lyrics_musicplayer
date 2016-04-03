
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
 import android.support.v4.view.ViewPager;
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

     int musicPosition = 0;

    public static Player newInstance(int page, String title){

        Player ff = new Player();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someString", title);
        ff.setArguments(args);

        return ff;

    }


     @Override
     public void onCreate(Bundle savedInstance){
 
         super.onCreate(savedInstance);
 
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.musicplay_fragment, container, false);
         TextView tvLabel = (TextView) view.findViewById(R.id.artist_and_title);
//         tvLabel.setText("Zene");
 
         view.findViewById(R.id.play_button).setOnClickListener(playStopMusic);
         view.findViewById(R.id.refresh_button).setOnClickListener(refreshButtonListener);
         view.findViewById(R.id.seekBackward).setOnClickListener(seekBackwards);
         view.findViewById(R.id.seekForward).setOnClickListener(seekForward);
 
         return view;
     }
 

     View.OnClickListener refreshButtonListener = new View.OnClickListener(){
         @Override
         public void onClick(View v) {

 
         }
     };
 
     View.OnClickListener seekForward = new View.OnClickListener() {
         @Override
         public void onClick(View v) {
 
          //   musicPosition = music.getCurrentPosition();
          //  music.seekTo(musicPosition + 500);
 
         }
 
 
 
     };
 
     View.OnClickListener seekBackwards = new View.OnClickListener() {
         @Override
         public void onClick(View v) {
 
             //musicPosition = music.getCurrentPosition();
//             music.seekTo(musicPosition - 500);
 
         }
 
 
 
     };
 
     View.OnClickListener playStopMusic = new View.OnClickListener() {
         @Override
         public void onClick(View v) {

 
         }
 
 
 
     };
 }

