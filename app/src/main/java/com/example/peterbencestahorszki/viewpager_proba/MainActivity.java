package com.example.peterbencestahorszki.viewpager_proba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;
    ViewPager vpPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), vpPager);
        vpPager.setAdapter(adapterViewPager);


    }

    // ...

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

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
                    return Browser.newInstance(0, "Page # 1",vpagerAdapter);
                case 1: // Fragment # 0 - This will show Browser different title
                    return Player.newInstance(1, "Page # 2");
                case 2: // Fragment # 1 - This will show Player
                    return Downloaded.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {

            if(position == 0)return "Böngésző";
            if(position == 1)return "Zenelejátszó";
            if(position == 2)return "Letöltött";

            return "default";
        }

    }
}
