package com.thanhclub.dalochat.base;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.view.activity.BodyActivity;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    protected View view;
    protected final String URL_SERVER = "http://192.168.201.1:3000";
    protected Toolbar toolbar;
    private ActionBar actionBar;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initToolbar(true, "Dalo", true);
        initView();
        initAction();



        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract void initView();

    protected abstract void initAction();

    protected void replaceFragment(Fragment fragment) {
        Fragment f = getActivity().getSupportFragmentManager()
                .findFragmentByTag(fragment.getClass().getName());
        if (f != null && f == fragment) {
            if (fragment.isVisible()) {
                return;
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .show(fragment)
                    .commit();
            return;
        }

        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.anim_enter_right, R.anim.anim_exit_left)
                .replace(R.id.content, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    protected void replaceFragmentToBack(Fragment fragment) {
        Fragment f = getActivity().getSupportFragmentManager()
                .findFragmentByTag(fragment.getClass().getName());
        if (f != null && f == fragment) {
            if (fragment.isVisible()) {
                return;
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .show(fragment)
                    .commit();
            return;
        }

        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.anim_enter_left, R.anim.anim_exit_right)
                .replace(R.id.content, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    protected void replaceFragmentMain(Fragment fragment) {
        Fragment f = getActivity().getSupportFragmentManager()
                .findFragmentByTag(fragment.getClass().getName());
        if (f != null && f == fragment) {
            if (fragment.isVisible()) {
                return;
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .show(fragment)
                    .commit();
            return;
        }

        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.anim_enter_right, R.anim.anim_exit_left)
                .replace(R.id.container_layout, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    protected void replaceFragmentToBackMain(Fragment fragment) {
        Fragment f = getActivity().getSupportFragmentManager()
                .findFragmentByTag(fragment.getClass().getName());
        if (f != null && f == fragment) {
            if (fragment.isVisible()) {
                return;
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .show(fragment)
                    .commit();
            return;
        }

        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.anim_enter_left, R.anim.anim_exit_right)
                .replace(R.id.container_layout, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }



    protected void createDialog(String title, String content) {
        AlertDialog.Builder al = new AlertDialog.Builder(getActivity());
        al.setTitle(title);
        al.setMessage(content);
        al.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        al.create().show();
    }

    protected void createToast(String content) {
        Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }

    protected void initToolbar(boolean showToolbar, String title, boolean homeButton) {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setHomeButtonEnabled(homeButton);
        actionBar.setDisplayHomeAsUpEnabled(homeButton);
        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);
        if (showToolbar == false) {
            actionBar.hide();

        } else {
            actionBar.show();
        }

    }

    protected void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container_layout, fragment);
        ft.commit();
    }
}
