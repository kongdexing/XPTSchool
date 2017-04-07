package com.xptschool.parent.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xptschool.parent.R;

/**
 * Created by dexing on 2016/12/13.
 * No1
 */

public class SplashAdapter extends FragmentPagerAdapter {

    private int[] resIds = new int[]{R.drawable.splash_1, R
            .drawable.splash_2, R.drawable.splash_3};

    public SplashAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("SplashFragment", "getItem: " + position);
        return SplashFragment.newInstance(resIds[position]);
    }

    @Override
    public int getCount() {
        return resIds.length;
    }

    public static class SplashFragment extends Fragment {

        private ImageView imgSplash;
        private int imgResId;

        public static SplashFragment newInstance(int imgId) {
            SplashFragment fragment = new SplashFragment();
            fragment.imgResId = imgId;
            return fragment;
        }

        public SplashFragment() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i("SplashFragment", "onCreate: ");
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.i("SplashFragment", "onCreateView: " + imgResId);
            View view = inflater.inflate(R.layout.fragment_splash, container, false);
            imgSplash = (ImageView) view.findViewById(R.id.imgSplash);
            imgSplash.setBackgroundResource(imgResId);
            return view;
        }

    }

}
