package com.nisaapp.HomeScreen;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nisaapp.R;

/**
 * Created by charu on 21-06-2017.
 */

public class HomeScreen extends android.app.Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.home, container, false);
        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = rootView.findViewById(R.id.viewpager);

        CategoryAdapter adapter = new CategoryAdapter(getActivity(), getChildFragmentManager());

        TabLayout tabLayout = rootView.findViewById(R.id.tabs);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }
}
