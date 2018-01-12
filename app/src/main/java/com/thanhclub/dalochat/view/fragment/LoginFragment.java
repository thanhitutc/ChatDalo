package com.thanhclub.dalochat.view.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.Untils.ActionManager;
import com.thanhclub.dalochat.Untils.Key;
import com.thanhclub.dalochat.base.BaseFragment;
import com.thanhclub.dalochat.manager.DatabaseManager;
import com.thanhclub.dalochat.manager.SharedPrefs;
import com.thanhclub.dalochat.manager.SocketConnect;
import com.thanhclub.dalochat.service.MyService;
import com.thanhclub.dalochat.view.activity.BodyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView txtRecoverPassword;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private MainFragment mainFragment;
    private DatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    @Override
    protected void initView() {
        // initToolbar();
        edtUsername = (EditText) view.findViewById(R.id.edt_username);
        edtPassword = (EditText) view.findViewById(R.id.edt_password);
        btnLogin = (Button) view.findViewById(R.id.btn_login);
        txtRecoverPassword = (TextView) view.findViewById(R.id.txt_recover_password);
        txtRecoverPassword.setPaintFlags(txtRecoverPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mainFragment = new MainFragment();

        databaseManager = new DatabaseManager(getActivity());

    }


    @Override
    protected void initToolbar(boolean showToolbar, String title, boolean homeButton) {
        super.initToolbar(true, "Đăng nhập", true);
    }

    @Override
    protected void initAction() {
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_RECEIPT_RESULT_LOGIN, onReceiptResultLogin);
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_RECEIPT_LIST_REQUEST_ADD_FRIEND, onReceiptListRequestAddFriend);
        btnLogin.setOnClickListener(this);
        txtRecoverPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                SocketConnect.getInstance().sendData(Key.EVENT_SEND_USERNAME_LOGIN,
                        edtUsername.getText().toString().toLowerCase().trim() + "_" + edtPassword.getText().toString().toLowerCase().trim());
                break;

            case R.id.txt_recover_password:
                break;

            default:
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
                replaceFragmentToBack(new MainFragment());
                //getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Emitter.Listener onReceiptResultLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String js = data.getString("ketqua");
                        String[] result = js.split("_");

                        if (result[0].equals("true")) {
                            createToast("Đăng nhập thành công!");
                            SharedPrefs.getInstance().put(Key.ID_USER,result[1]+"");
                            SharedPrefs.getInstance().put(Key.USER_NAME, edtUsername.getText().toString().trim());
                            SharedPrefs.getInstance().put(Key.PASSWORD, edtPassword.getText().toString().trim());
                            Intent in = new Intent(getActivity(), BodyActivity.class);
                            getActivity().finish();
                            getActivity().startActivity(in);

                            SocketConnect.getInstance().sendData(Key.EVENT_INIT_LIST_FRIEND,
                                    SharedPrefs.getInstance().get(Key.ID_USER,String.class));

                            SocketConnect.getInstance().sendData(Key.EVENT_GET_LIST_REQUEST_ADD_FRIEND,
                                    edtUsername.getText().toString().trim());

                            if (ActionManager.isMyServiceRunning(MyService.class)==false){
                                Intent i = new Intent();
                                i.setClass(getActivity(),MyService.class);
                                getActivity().startService(i);
                            }
                        } else {
                            createToast("Sai thông tin tài khoản!");
                            edtUsername.requestFocus();
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onReceiptListRequestAddFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    try{
                       // if (data.length()==0 || data == null) return;
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObj = data.getJSONObject(i);
                            String logo = jsonObj.getString("u_logo");
                            String username = jsonObj.getString("u_name");
                            String body = jsonObj.getString("m_body");
                            String first_name = jsonObj.getString("u_first_name");
                            String last_name = jsonObj.getString("u_last_name");
                            String phone = jsonObj.getString("u_phone_number");
                            int sex = jsonObj.getInt("u_sex");
                            databaseManager.insertListRequest(logo, username, body,first_name,last_name,phone,sex);
                        }
                    }catch (JSONException e){
                        return;
                    }
                }
            });
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }
    }




