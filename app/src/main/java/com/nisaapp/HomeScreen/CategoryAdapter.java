/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nisaapp.HomeScreen;

import android.content.Context;

import com.nisaapp.R;


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
