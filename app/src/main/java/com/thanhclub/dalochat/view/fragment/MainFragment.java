package com.thanhclub.dalochat.view.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.base.BaseFragment;


public class MainFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private Button btnLogin;
    private TextView txtRegister;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    protected void initView() {
        btnLogin = (Button) view.findViewById(R.id.btn_login);
        txtRegister = (TextView) view.findViewById(R.id.txt_register);
        txtRegister.setPaintFlags(txtRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }
        if (registerFragment == null){
            registerFragment = new RegisterFragment();
        }
       //initAction();
    }

//    private void initToolbar() {
//        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        actionBar.hide();
//    }


    @Override
    protected void initToolbar(boolean showToolbar, String title, boolean homeButton) {
        super.initToolbar(false, title, homeButton);
    }

    @Override
    protected void initAction() {
        btnLogin.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                replaceFragment(loginFragment);
                break;

            case R.id.txt_register:
                replaceFragment(registerFragment);
                break;

            default:
                break;
        }
    }

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
                .setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left)
                .replace(R.id.content, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }


    @Override
    public void onResume() {
        super.onResume();
        //actionBar.hide();
    }
}
