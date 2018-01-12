package com.thanhclub.dalochat.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.nkzawa.emitter.Emitter;
import com.thanhclub.dalochat.App;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.Untils.Key;
import com.thanhclub.dalochat.base.BaseFragment;
import com.thanhclub.dalochat.manager.DatabaseManager;
import com.thanhclub.dalochat.manager.SharedPrefs;
import com.thanhclub.dalochat.manager.SocketConnect;
import com.thanhclub.dalochat.view.activity.BodyActivity;

import org.java_websocket.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class InformationUserSearchedFragment extends BaseFragment {
    private TextView textView;
    private String json;
    private BodyActivity bodyActivity;

    private ImageView imgAvatar;
    private TextView txtUserName;
    private TextView txtRealName;
    private TextView txtSex;
    private TextView txtPhoneNumber;

    private Button btnAddFriend;
    private Button btnMoreInformation;

    private String userPerson;
    private String realName;
    private String phoneNumber;
    private int idUser;
    private String sex;

    private DatabaseManager databaseManager;
    private String actionButtonAddFriend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_infomation_user, container, false);
        return view;
    }

    @Override
    protected void initView() {
        databaseManager = new DatabaseManager(getActivity());

        actionButtonAddFriend = "ACCEPT";

        bodyActivity = (BodyActivity) getActivity();
        json = bodyActivity.getJson();

        imgAvatar = (ImageView) view.findViewById(R.id.img_avatar_info);
        txtUserName = (TextView) view.findViewById(R.id.txt_username_info);
        txtRealName = (TextView) view.findViewById(R.id.txt_real_name);
        txtPhoneNumber = (TextView) view.findViewById(R.id.txt_phone_number);
        txtSex = (TextView) view.findViewById(R.id.txt_sex);

        btnAddFriend = (Button) view.findViewById(R.id.btn_add_friend);
        btnMoreInformation = (Button) view.findViewById(R.id.btn_more_info);

        txtRealName.setVisibility(View.GONE);
        txtSex.setVisibility(View.GONE);
        txtPhoneNumber.setVisibility(View.GONE);

        try {
            JSONObject jsonObj = new JSONObject(json.toString());
            byte[] byteImg = Base64.decode(jsonObj.getString(Key.KEY_LOGO));
            Glide.with(getActivity()).load(byteImg).override(80,80).into(imgAvatar);
            userPerson = jsonObj.getString(Key.KEY_USERNAME);
            realName =  jsonObj.getString(Key.KEY_FIRSTNAME)+" " + jsonObj.getString(Key.KEY_LASTNAME);
            phoneNumber = jsonObj.getString(Key.KEY_PHONE_NUMBER);

            if(jsonObj.getInt(Key.KEY_SEX) == 0){
                sex = "Nữ";
            }else {
                sex = "Nam";
            }

            if (realName == null) {
                realName = "Chưa cập nhật";
            }
            if (phoneNumber.equals("")){
                phoneNumber = "Chưa cập nhật";
            }
            Log.e("INFO",phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        txtSex.setText("Giới tính: " + sex);
        txtPhoneNumber.setText("Số điện thoại: " + phoneNumber );
        txtUserName.setText(userPerson);
        txtRealName.setText("Họ tên: " + realName);
    }

    @Override
    protected void initAction() {
        btnMoreInformation.setOnClickListener(this);
        btnAddFriend.setOnClickListener(this);
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_RESULT_CHECK_EXIST_REQUEST_FRIEND, onResultCheckExistRequestFriend);
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_RESULT_DELETE_REQUEST_ADDFRIEND, onResultDelete);
    }

    private Emitter.Listener onResultCheckExistRequestFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String kq = data.getString("ketqua");
                        if (kq.equals("true")){
                            actionButtonAddFriend = "REQUESTED";
                            btnAddFriend.setText("Đã gửi yêu cầu");
                            btnAddFriend.setEnabled(false);
                            btnMoreInformation.setText("Hủy yêu cầu");
                        }

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_more_info:
                if (actionButtonAddFriend.equals("REQUESTED")){
                        SocketConnect.getInstance().sendData(Key.EVENT_CANCEL_REQUESTED_ADDFRIEND,
                                SharedPrefs.getInstance().get(Key.USER_NAME, String.class)+"_"+userPerson);
                }else if (actionButtonAddFriend.equals("ACCEPT")){
                    txtRealName.setVisibility(View.VISIBLE);
                    txtSex.setVisibility(View.VISIBLE);
                    txtPhoneNumber.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.btn_add_friend:
                if (actionButtonAddFriend.equals("ACCEPT")){
                    String userMyself = SharedPrefs.getInstance().get(Key.USER_NAME,String.class);
                    String idUserMyself = SharedPrefs.getInstance().get(Key.ID_USER, String.class);
                    SocketConnect.getInstance().sendData(Key.EVENT_SEND_REQUEST_ADD_FRIEND, idUserMyself+"_"+userMyself+"_"+userPerson );
                    btnAddFriend.setEnabled(false);
                    btnAddFriend.setText("Đã gửi yêu cầu");
                }else if (actionButtonAddFriend.equals("REQUESTED")){
                    createToast("đã yêu cầu");
                }




                break;

            default:
                break;
        }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                replaceFragmentToBackMain(new ContactFragment());
                //getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        SocketConnect.getInstance().sendData(Key.EVENT_CHECK_EXIST_REQUEST_FRIEND,
                SharedPrefs.getInstance().get(Key.ID_USER,String.class)+"_"+userPerson);

    }
}
