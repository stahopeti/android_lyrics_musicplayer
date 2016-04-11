package com.example.peterbencestahorszki.viewpager_proba;

import android.annotation.TargetApi;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by peterbencestahorszki on 2016. 03. 08..
 */
public class Browser extends Fragment {

    private ListView list;
    private ArrayAdapter<String> adapter;
    private static ArrayList<String> musicTitles = new ArrayList<String>();
    private ArrayList<MusicFile> musicFilesOnStorage = new ArrayList<MusicFile>();
    private ContentResolver cR;
    private MusicFile lastClicked = new MusicFile();


    public static Browser newInstance(){

        Browser ff = new Browser();
        Bundle args = new Bundle();
        args.putStringArrayList("someList", musicTitles);
        ff.setArguments(args);

        return ff;

    }

    @Override
    public void onCreate(Bundle savedInstance){

        super.onCreate(savedInstance);
        System.out.println("BROWSER ONCREATE");
        musicTitles.clear();
        for (int i = 0; i<getArguments().getStringArrayList("someList").size(); i++) {

            musicTitles.add(getArguments().getStringArrayList("someList").get(i));

        }

    }

    // Inflate the view for the fragment based on layout XML
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.browsemusic_fragment, container, false);

        list = (ListView) view.findViewById(R.id.list);

        setList();

        adapter = new ArrayAdapter<String>(this.getContext(), R.layout.rowlayout_musiclist, musicTitles);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                lastClicked = musicFilesOnStorage.get(position);

                SharedPreferences sp = getActivity().getSharedPreferences(Constants.XLYRCS_SHARED_PREFS,
                        Context.MODE_APPEND);
                SharedPreferences.Editor editor = sp.edit();

                String sharedPrefPath = sp.getString(Constants.PLAYING_SONG_PATH, "default");

                System.out.println("Current sharedpref music path: " + sharedPrefPath);

                Intent intent = new Intent(getActivity(), PlayMusic.class);


                System.out.println("LAST CLICKED PATH: \n" +
                        lastClicked.getPath());
                editor.putString(Constants.PLAYING_SONG_PATH, lastClicked.getPath());

                if (lastClicked.getPath() == sharedPrefPath) {

                    intent.putExtra("SHOULD_I_START", false);


                } else {

                    editor.putString(Constants.PLAYING_SONG_ARTIST, lastClicked.getArtist());
                    editor.putString(Constants.PLAYING_SONG_TITLE, lastClicked.getTitle());
                    editor.putString(Constants.PLAYING_SONG_LYRICS, null);

                    MainActivity.setMusicParameters();

                    if (sharedPrefPath != null) MainActivity.stopMusic();
                    MainActivity.getMusicAndLyrics();
                    intent.putExtra("SHOULD_I_START", true);
                    editor.putBoolean(Constants.SHOULD_I_REFRESH_LYRICS, true);
                    editor.putBoolean(Constants.SHOULD_BAKELIT_BE_FOREGROUND, true);

                }


                editor.commit();

                startActivity(intent);

            }
        });

        return view;
    }

    private void setList(){

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

                    String temp = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)) + " \n " +
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));

                    System.out.println(temp);

                    int  i = (cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                    MusicFile dummy = new MusicFile(
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            cursor.getString(i),
                            null);

                    musicFilesOnStorage.add(dummy);
                    musicTitles.add(temp);
                }

            }

        }

    }

}
