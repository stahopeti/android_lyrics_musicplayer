package com.example.peterbencestahorszki.viewpager_proba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by peterbencestahorszki on 2016. 03. 08..
 */
public class Downloaded extends Fragment {

    private String title;
    private int page;

    public static Downloaded newInstance(int page, String title){

        Downloaded tf = new Downloaded();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someString", title);
        tf.setArguments(args);
        return tf;

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
        View view = inflater.inflate(R.layout.browsedownloaded_fragment, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel3);
        tvLabel.setText("Letöltött");
        return view;
    }



}
