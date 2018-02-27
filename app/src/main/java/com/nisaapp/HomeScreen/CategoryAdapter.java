package com.nisaapp.HomeScreen;

import android.content.Context;

import com.nisaapp.R;

/**
 * Created by rutvora (www.github.com/rutvora)
 */
class CategoryAdapter extends android.support.v13.app.FragmentPagerAdapter {

    private Context mContext;

    CategoryAdapter(Context context, android.app.FragmentManager fm) {
        super(fm);
        mContext = context;
    }


    @Override
    public android.app.Fragment getItem(int position) {
        if (position == 0) {
            return new AroundMe();
        } else if (position == 1) {
            return new Friends();
        } else {
            return new Strangers();
        }

    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.aroundMe);
        } else if (position == 1) {
            return mContext.getString(R.string.friends);
        } else {
            return mContext.getString(R.string.strangers);
        }
    }
}
