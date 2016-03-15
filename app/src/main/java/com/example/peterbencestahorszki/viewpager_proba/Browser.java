package com.example.peterbencestahorszki.viewpager_proba;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peterbencestahorszki on 2016. 03. 08..
 */
public class Browser extends Fragment {

    private ListView list;
    public ArrayAdapter<String> adapter;
    private String title;
    private int page;
    private static ArrayList<String> musicListToBrowse = new ArrayList<String>();
    private ArrayList<MusicFile> musicData = new ArrayList<MusicFile>();
    ContentResolver cR;
    private MusicFile lastClicked = new MusicFile(null,null,null);

    static ViewPager vpagerBrowser;

    public static Browser newInstance(int page, String title, ViewPager vpagerParam){

        Browser ff = new Browser();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someString", title);
        args.putStringArrayList("someList", musicListToBrowse);
        ff.setArguments(args);
        vpagerBrowser = vpagerParam;

        return ff;

    }

    @Override
    public void onCreate(Bundle savedInstance){

        super.onCreate(savedInstance);
        System.out.println("BROWSER ONCREATE");

        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someString");
        musicListToBrowse.clear();
        for (int i = 0; i<getArguments().getStringArrayList("someList").size(); i++) {

            musicListToBrowse.add(getArguments().getStringArrayList("someList").get(i));

        }

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("musicPlayerPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("Artist", "Válassz");
        editor.putString("Title", "zenét");
        editor.putString("Lyrics", "Válassz");

    }

    // Inflate the view for the fragment based on layout XML
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.browsemusic_fragment, container, false);

        list = (ListView) view.findViewById(R.id.list);

        cR  = getActivity().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String order = MediaStore.Audio.Media.ALBUM + " ASC";
        Cursor cursor = cR.query(uri,
                                 null,
                                 selection,
                                 null,
                                 order);
        int count = 0;

        if(cursor != null){

            count = cursor.getCount();

            if(count>0){

                while (cursor.moveToNext()){

                    String asd = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)) + " - " +
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));

                    System.out.println(asd);

                    int  i = (cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                    //System.out.println(cursor.getString(i));

                    MusicFile dummy = new MusicFile(
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            cursor.getString(i));

                    musicData.add(dummy);
                    musicListToBrowse.add(asd);
                }

            }

        }

        adapter = new ArrayAdapter<String>(this.getContext(), R.layout.rowlayout_musiclist, musicListToBrowse);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), PlayMusic.class);
                Bundle bundle = new Bundle();


                bundle.putString("pathForMusic", musicData.get(position).getPath());

                lastClicked = musicData.get(position);

                if (bundle != null) {
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });

        Button toMusic = (Button) view.findViewById(R.id.zene);

        toMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        System.out.println("BROWSER ONRESUME");

    }

    @Override
    public void onStop(){
        super.onStop();
        System.out.println("BROWSER ONSTOP");

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        System.out.println("BROWSER ONDESTROY");

    }

}
