package com.ngynstvn.android.blocparty.ui.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ngynstvn.android.blocparty.ui.fragment.FacebookUserFragment;
import com.ngynstvn.android.blocparty.ui.fragment.InstagramUserFragment;
import com.ngynstvn.android.blocparty.ui.fragment.TwitterUserFragment;

/**
 * Created by Ngynstvn on 11/2/15.
 */
public class AddUserPagerAdapter extends FragmentStatePagerAdapter {

    // Must import v13 library to use v13 version. Default: v4

    public AddUserPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        FacebookUserFragment facebookUserFragment = FacebookUserFragment.newInstance(position);
        TwitterUserFragment twitterUserFragment = TwitterUserFragment.newInstance(position);
        InstagramUserFragment instagramUserFragment = InstagramUserFragment.newInstance(position);

        switch(position) {
            case 0:
                return facebookUserFragment;
            case 1:
                return twitterUserFragment;
            case 2:
                return instagramUserFragment;
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch(position) {
            case 0:
                return "Facebook";
            case 1:
                return "Twitter";
            case 2:
                return "Instagram";
        }

        return "";
    }

    @Override
    public int getCount() {
        return 3;
    }
}
