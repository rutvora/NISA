package com.nisaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class SplashScreen extends android.app.Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.splash_screen, container, false);

        return rootView;
    }
}
