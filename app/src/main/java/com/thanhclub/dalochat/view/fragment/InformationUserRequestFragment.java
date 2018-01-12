package com.thanhclub.dalochat.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.nkzawa.emitter.Emitter;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.Untils.Key;
import com.thanhclub.dalochat.base.BaseFragment;
import com.thanhclub.dalochat.manager.DatabaseManager;
import com.thanhclub.dalochat.manager.SharedPrefs;
import com.thanhclub.dalochat.manager.SocketConnect;
import com.thanhclub.dalochat.model.UserAddFriend;
import com.thanhclub.dalochat.view.activity.BodyActivity;

import org.java_websocket.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class InformationUserRequestFragment extends BaseFragment {
    private View view;
    private DatabaseManager databaseManager;
    private ArrayList<UserAddFriend> userAddFriends;
    private String userRequest;
    private BodyActivity bodyActivity;
    private String actionButtonAddFriend;

    private ImageView imgAvatar;
    private TextView txtUserName;
    private TextView txtRealName;
    private TextView txtSex;
    private TextView txtPhoneNumber;

    private Button btnAddFriend;
    private Button btnCancel;

    private String actionButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_information_user_request, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        userRequest =  bodyActivity.getUsernameRequestAddFriend();
        for (int i = 0; i < userAddFriends.size(); i++) {
            if (userAddFriends.get(i).getUsername().equals(userRequest)){
                txtUserName.setText(userRequest);
                try {
                    byte[] byteImg = Base64.decode(userAddFriends.get(i).getAvatar());
                    Glide.with(getActivity()).load(byteImg).override(80,80).into(imgAvatar);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                txtRealName.setText("Họ tên: " + userAddFriends.get(i).getFisrtname() +" " + userAddFriends.get(i).getLastname());
                txtPhoneNumber.setText("Số điện thoại: " + userAddFriends.get(i).getPhone());
                if (userAddFriends.get(i).getSex() == 0){
                    txtSex.setText("Giới tính: Nữ");
                }else {
                    txtSex.setText("Giới tính: Nam");
                }
            }
        }

    }

    @Override
    protected void initView() {
        bodyActivity = (BodyActivity) getActivity();
        databaseManager = new DatabaseManager(getActivity());
        userAddFriends = databaseManager.getListUserAddFriends();

        imgAvatar = (ImageView) view.findViewById(R.id.img_avatar_info);
        txtUserName = (TextView) view.findViewById(R.id.txt_username_info);
        txtRealName = (TextView) view.findViewById(R.id.txt_real_name);
        txtPhoneNumber = (TextView) view.findViewById(R.id.txt_phone_number);
        txtSex = (TextView) view.findViewById(R.id.txt_sex);

        btnAddFriend = (Button) view.findViewById(R.id.btn_add_friend);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel_request);
    }

    @Override
    protected void initAction() {
        btnAddFriend.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_RESULT_DELETE_REQUEST_ADDFRIEND, onResultDelete);
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_RESULT_ACCEPT_ADD_FRIEND, onResultAccept);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_cancel_request:
                actionButton = "CANCEL";
                break;
            case R.id.btn_add_friend:
                actionButton = "ACCEPT";
                break;
        }
        SocketConnect.getInstance().sendData(Key.EVENT_SEND_RESULT_REQUEST_ADD_FRIEND,
                actionButton+"_"+ SharedPrefs.getInstance().get(Key.USER_NAME,String.class)+"_"+userRequest);
    }

    private Emitter.Listener onResultDelete = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        if (data.getString("ketqua").equals("true")){
                            replaceFragmentMain(new ContactFragment());
                        }

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onResultAccept = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        if (data.getString("ketqua").equals("true")){
                            //replaceFragmentMain(new ContactFragment());
                            getActivity().onBackPressed();
                        }

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
}
