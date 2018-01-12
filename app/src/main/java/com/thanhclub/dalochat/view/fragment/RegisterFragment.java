package com.thanhclub.dalochat.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.Untils.Key;
import com.thanhclub.dalochat.base.BaseFragment;
import com.thanhclub.dalochat.interfaces.OnSendDataFragment;
import com.thanhclub.dalochat.manager.SocketConnect;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterFragment extends BaseFragment {
    private View view;
    private Button btnRegister;
    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtPasswordAgain;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private MainFragment mainFragment;
    private UpdateUserFragment updateUserFragment;
    private OnSendDataFragment listener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSendDataFragment) {
            listener = (OnSendDataFragment) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    @Override
    protected void initView() {
        //initToolbar();
        edtUsername = (EditText) view.findViewById(R.id.edt_username);
        edtPassword = (EditText) view.findViewById(R.id.edt_password);
        edtPasswordAgain = (EditText) view.findViewById(R.id.edt_password_again);
        btnRegister = (Button) view.findViewById(R.id.btn_register);

        //mainFragment = new MainFragment();
        updateUserFragment = new UpdateUserFragment();
    }


    @Override
    protected void initToolbar(boolean showToolbar, String title, boolean homeButton) {
        super.initToolbar(true, "Đăng ký", true);
    }

    @Override
    protected void initAction() {
        btnRegister.setOnClickListener(this);
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_RECEIPT_RESULT_REGISTER, onReceiptResultRegist);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_register:
                if (edtUsername.getText().toString().equals("") || edtPassword.getText().toString().equals("")){
                    createDialog("Lỗi", "Nhập đầy đủ thông tin");
                }else {
                    if (edtPassword.getText().toString().equals(edtPasswordAgain.getText().toString())) {
                        String user = edtUsername.getText().toString().toLowerCase().trim();
                        String password = edtPassword.getText().toString().toLowerCase().trim();
                        SocketConnect.getInstance().sendData(Key.EVENT_SEND_USERNAME_REGISTER, user + "_" + password);

                    } else {
                        createDialog("Lỗi", "Nhập lại mật khẩu không khớp");
                        edtPassword.setText("");
                        edtPasswordAgain.setText("");
                        edtPassword.requestFocus();
                    }
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                //replaceFragmentToBack(new MainFragment());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Emitter.Listener onReceiptResultRegist = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String result = data.getString("ketqua");
                        if (result.equals("true")) {
                            createToast("Đăng ký tài khoản thành công!");
                            replaceFragment(updateUserFragment);
                            listener.onSendData(edtUsername.getText().toString().trim());
                        } else {
                            createToast("Tài khoản đã tồn tại!");
                            edtPassword.setText("");
                            edtPasswordAgain.setText("");
                            edtUsername.setText("");
                            edtUsername.requestFocus();
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };


}
