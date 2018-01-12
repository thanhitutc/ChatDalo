package com.thanhclub.dalochat.view.fragment;

import android.content.Context;
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
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.Untils.Key;
import com.thanhclub.dalochat.base.BaseFragment;
import com.thanhclub.dalochat.interfaces.OnSendDataFragment;
import com.thanhclub.dalochat.manager.DatabaseManager;
import com.thanhclub.dalochat.manager.SharedPrefs;
import com.thanhclub.dalochat.manager.SocketConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FindFriendFragment extends BaseFragment {
    private EditText edtInputInfo;
    private Button btnSearchUser;
    private DatabaseManager databaseManager;
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
        view = inflater.inflate(R.layout.fragment_search_friends, container, false);
        return view;
    }

    @Override
    protected void initToolbar(boolean showToolbar, String title, boolean homeButton) {
        super.initToolbar(true, "Tìm bạn", true);
    }

    @Override
    protected void initView() {
        edtInputInfo = (EditText) view.findViewById(R.id.edt_input_search_info);
        btnSearchUser = (Button) view.findViewById(R.id.btn_search_user);

    }

    @Override
    protected void initAction() {
        btnSearchUser.setOnClickListener(this);
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_RECEIPT_RESULT_SEARCH_USER, onReceiptResultSearchUser);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_search_user:
                String input = edtInputInfo.getText().toString().trim();
//                databaseManager = new DatabaseManager(getActivity());
//                if (databaseManager.getListUserAddFriends().size()!=0){
//                    for (int i = 0; i < databaseManager.getListUserAddFriends().size(); i++) {
//                        if (databaseManager.getListUserAddFriends().get(i).getUsername().equals(input)){
//                            createToast("Tài khoản đã gửi yêu cầu cho bạn!");
//                            return;
//                        }
//                    }
//                }

                SocketConnect.getInstance().sendData(Key.EVENT_SEND_SEARCH_USER, input);
                break;
        }
    }

    private Emitter.Listener onReceiptResultSearchUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    if (data.isNull(0)) {
                        createToast("Không tìm thấy người dùng!");
                        return;
                    }
                    try {
                        JSONObject result = data.getJSONObject(0);
                        replaceFragmentMain(new InformationUserSearchedFragment());
                        listener.onSendFromFind(result.toString());
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
}
