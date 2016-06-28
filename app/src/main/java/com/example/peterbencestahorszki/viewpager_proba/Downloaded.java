package com.example.peterbencestahorszki.viewpager_proba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

/**
 * Created by peterbencestahorszki on 2016. 03. 08..
 */
public class Downloaded extends Fragment {

    private ListView list;
    private ArrayList<MusicFile> theseHaveLyrics;
    private ArrayList<String> stringsForListing;
    private ArrayAdapter<String> adapter;
    private MusicFile lastClicked = new MusicFile();

    public static Downloaded newInstance(){

        Downloaded tf = new Downloaded();
        Bundle args = new Bundle();
        tf.setArguments(args);
        return tf;

    }

    @Override
    public void onCreate(Bundle savedInstance){

        super.onCreate(savedInstance);

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.browsedownloaded_fragment, container, false);
        list = (ListView) view.findViewById(R.id.downloaded_list);

        theseHaveLyrics = new ArrayList<>();


        if(deserialize() && (theseHaveLyrics != null)){

        } else {

        }


        setList();


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                lastClicked = theseHaveLyrics.get(position);

                SharedPreferences sp = getActivity().getSharedPreferences(Constants.XLYRCS_SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                String sharedPrefPath = sp.getString(Constants.PLAYING_SONG_PATH, "default");


                Intent intent = new Intent(getActivity(), PlayMusic.class);

                editor.putString(Constants.PLAYING_SONG_PATH, lastClicked.getPath());

                if (lastClicked.getPath().equals(sharedPrefPath)) {

                    intent.putExtra("SHOULD_I_START", false);


                } else {

                    editor.putString(Constants.PLAYING_SONG_ARTIST, lastClicked.getArtist());
                    editor.putString(Constants.PLAYING_SONG_TITLE, lastClicked.getTitle());
                    editor.putString(Constants.PLAYING_SONG_LYRICS, lastClicked.getLYRICS());


                    MainActivity.getMusicAndLyrics();
                    intent.putExtra("SHOULD_I_START", true);
                    editor.putBoolean(Constants.SHOULD_I_REFRESH_LYRICS, false);
                    editor.putBoolean(Constants.SHOULD_BAKELIT_BE_FOREGROUND, false);

                }

                editor.commit();

                startActivity(intent);

            }
        });

        return view;
    }

    public boolean deserialize() {

        String fileName = this.getContext().getFilesDir().getPath().toString() + "serialized.dat";

        File f = new File(fileName);

        if(f.exists()){

            try {

                FileInputStream in = new FileInputStream(f);

                ObjectInputStream objectInputStream = new ObjectInputStream(in);

                theseHaveLyrics = (ArrayList) objectInputStream.readObject();

                objectInputStream.close();
                in.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OptionalDataException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return true;

        }

        return false;

    }

    public void setList(){

        stringsForListing = new ArrayList<>();

        for (int i = 0; i<theseHaveLyrics.size(); i++){

            stringsForListing.add(theseHaveLyrics.get(i).getArtist() +
                                    "\n " + theseHaveLyrics.get(i).getTitle());

        }

        adapter = new ArrayAdapter<String>(this.getContext(), R.layout.rowlayout_musiclist, stringsForListing);
        list.setAdapter(adapter);

    }

    @Override
    public void onResume(){

        super.onResume();
        deserialize();
        setList();

    }

    @Override
    public void onPause(){

        super.onPause();

    }

}
