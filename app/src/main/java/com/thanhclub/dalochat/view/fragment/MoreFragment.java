package com.thanhclub.dalochat.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.base.BaseFragment;
import com.thanhclub.dalochat.view.activity.BodyActivity;

public class MoreFragment extends BaseFragment{
    private BodyActivity bodyActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_more, container, false);
        return view;
    }

    @Override
    protected void initToolbar(boolean showToolbar, String title, boolean homeButton) {
        super.initToolbar(true, title, false);
    }

    @Override
    protected void initView() {
        bodyActivity = (BodyActivity) getActivity();
    }

    @Override
    protected void initAction() {

    }

    @Override
    public void onResume() {
        super.onResume();
        bodyActivity.getNavigation().setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        bodyActivity.getNavigation().setVisibility(View.GONE);
    }
}
