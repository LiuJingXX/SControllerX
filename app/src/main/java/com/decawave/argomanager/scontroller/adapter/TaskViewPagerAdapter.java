package com.decawave.argomanager.scontroller.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Hu_codeman on 2019/5/31.
 */

public class TaskViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<String> mDatas;
    private List<Fragment> mFragments;

    public TaskViewPagerAdapter(FragmentManager fm, List<String> mDatas, List<Fragment> fragments) {
        super(fm);
        this.mDatas = mDatas;
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mDatas.size()==mFragments.size()?mFragments.size():0;
    }

    /**
     * 重写此方法，返回TabLayout的内容
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mDatas.get(position);
    }
}
