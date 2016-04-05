package com.example.peterbencestahorszki.viewpager_proba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

/**
 * Created by peterbencestahorszki on 2016. 03. 08..
 */
public class Downloaded extends Fragment {


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.browsedownloaded_fragment, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel3);

        return view;
    }



}
