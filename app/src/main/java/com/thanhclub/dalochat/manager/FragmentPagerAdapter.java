package com.thanhclub.dalochat.manager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.thanhclub.dalochat.view.fragment.CommunityFragment;
import com.thanhclub.dalochat.view.fragment.ContactFragment;
import com.thanhclub.dalochat.view.fragment.MessageFragment;
import com.thanhclub.dalochat.view.fragment.MoreFragment;


public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    public FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MessageFragment();

            case 1:
                return new ContactFragment();

            case 2:
                return new CommunityFragment();

            case 3:
                return new MoreFragment();
        }

        return null;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getCount() {
        return 4;
    }
}
