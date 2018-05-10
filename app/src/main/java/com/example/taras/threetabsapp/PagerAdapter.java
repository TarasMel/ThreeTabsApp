package com.example.taras.threetabsapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;



public class PagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int  NumberOfTabs){
        super(fm);
        this.mNumOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                SQLTabList geograficTab  = new SQLTabList();
                return geograficTab;
            case 1:
                ImageTab imageTab = new ImageTab();
                return imageTab;
            case 2:
                ArchiveTab archiveTab = new ArchiveTab();
                return archiveTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
