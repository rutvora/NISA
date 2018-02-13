package com.nisaapp.HomeScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nisaapp.R;

/**
 * Created by charu on 21-06-2017.
 */

public class AroundMe extends android.app.Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.around_me, container, false);

        return rootView;
    }
}
