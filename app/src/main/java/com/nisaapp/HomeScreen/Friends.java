package com.nisaapp.HomeScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nisaapp.R;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class Friends extends android.app.Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.friends, container, false);

        return rootView;
    }
}
