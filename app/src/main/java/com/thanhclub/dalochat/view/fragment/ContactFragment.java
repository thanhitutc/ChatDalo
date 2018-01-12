package com.thanhclub.dalochat.view.fragment;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.github.nkzawa.emitter.Emitter;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.Untils.Key;
import com.thanhclub.dalochat.adapter.AcceptAddFriendAdapter;
import com.thanhclub.dalochat.adapter.ContactAdapter;
import com.thanhclub.dalochat.adapter.RequestAddFriendAdapter;
import com.thanhclub.dalochat.base.BaseFragment;
import com.thanhclub.dalochat.interfaces.OnClickItem;
import com.thanhclub.dalochat.interfaces.OnSendDataFragment;
import com.thanhclub.dalochat.manager.DatabaseManager;
import com.thanhclub.dalochat.manager.SharedPrefs;
import com.thanhclub.dalochat.manager.SocketConnect;
import com.thanhclub.dalochat.model.Friend;
import com.thanhclub.dalochat.model.UserAddFriend;
import com.thanhclub.dalochat.service.MyService;
import com.thanhclub.dalochat.view.activity.BodyActivity;
import com.thanhclub.dalochat.view.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ContactFragment extends BaseFragment implements OnClickItem {
    private FloatingActionButton fab;
    private FloatingActionButton fabFindPeople;
    private FloatingActionButton fabCreateGroup;
    private FloatingActionButton fabMaybeKnow;
    private boolean visible;
    private BodyActivity bodyActivity;
    private Intent intentS;
    private Handler handlerUpdate;
    private MyService myService;
    private int countRe;

    private RecyclerView rcRequestAddFriend;
    private RecyclerView rcContact;

    private RecyclerView.LayoutManager layoutManager;
    private RequestAddFriendAdapter addFriendAdapter;
    private ContactAdapter contactAdapter;

    private DatabaseManager databaseManager;

    private ArrayList<UserAddFriend> userAddFriends;
    private ArrayList<Friend> friends;
    private OnSendDataFragment listener;

    private ArrayList<UserAddFriend> userAccept;
    private RecyclerView rcAcceptFriend;
    private AcceptAddFriendAdapter acceptAddFriendAdapter;


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
        view = inflater.inflate(R.layout.fragment_contact, container, false);
        bodyActivity = (BodyActivity) getActivity();


        return view;

    }

    @Override
    protected void initToolbar(boolean showToolbar, String title, boolean homeButton) {
        super.initToolbar(true, title, false);
    }


    @Override
    protected void initView() {
        initFloatingCircleMenu();

        handlerUpdate = new Handler();
        databaseManager = new DatabaseManager(getActivity());

        initRecylerRequest();
        intentS = new Intent(getActivity(), MyService.class);
        getActivity().bindService(intentS, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private void initRecylerRequest() {

        userAddFriends = databaseManager.getListUserAddFriends();
        addFriendAdapter = new RequestAddFriendAdapter(userAddFriends);
        rcRequestAddFriend = (RecyclerView) view.findViewById(R.id.rc_request_add_friends);

        layoutManager = new LinearLayoutManager(getActivity());
        rcRequestAddFriend.setLayoutManager(layoutManager);
        rcRequestAddFriend.setAdapter(addFriendAdapter);

        rcContact = (RecyclerView) view.findViewById(R.id.rc_contact);
        RecyclerView.LayoutManager m = new LinearLayoutManager(getActivity());
        rcContact.setLayoutManager(m);

        friends = new ArrayList<>();
        contactAdapter = new ContactAdapter(friends);
        rcContact.setAdapter(contactAdapter);

        userAccept = new ArrayList<>();
        rcAcceptFriend = (RecyclerView) view.findViewById(R.id.rc_accept_add_friend);
        acceptAddFriendAdapter = new AcceptAddFriendAdapter(userAccept);
        RecyclerView.LayoutManager layManager = new LinearLayoutManager(getActivity());
        rcAcceptFriend.setLayoutManager(layManager);
        rcAcceptFriend.setAdapter(acceptAddFriendAdapter);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinderRequestAddFriend mybin =
                    (MyService.MyBinderRequestAddFriend) service;
            myService = mybin.getResquestAddFriendService();
            update();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void update() {
        handlerUpdate.postDelayed(onUpdate, 1000);
    }

    private Runnable onUpdate = new Runnable() {
        @Override
        public void run() {
            if (myService.getActionAddRequest() == true && myService.isCompleteAddDatabase()) {
                if (userAddFriends != null) {
                    userAddFriends = null;
                }
                userAddFriends = databaseManager.getListUserAddFriends();
                addFriendAdapter = new RequestAddFriendAdapter(userAddFriends);
                rcRequestAddFriend.setAdapter(addFriendAdapter);
                //addFriendAdapter.notifyDataSetChanged();

                myService.setActionAddRequest(false);
                myService.setCompleteAddDatabase(false);
                addFriendAdapter.setOnClickItem(new OnClickItem() {
                    @Override
                    public void onClickItemRequest(String username) {
                        replaceFragmentMain(new InformationUserRequestFragment());
                        listener.onSendFromContact(username);
                    }
                });
            }
            else if (myService.getActionAcceptAddFriend()){
                Log.e("CONTACTFRAG","co su kien dong y ket ban");
                userAccept = myService.getUserAccpet();
                acceptAddFriendAdapter.notifyDataSetChanged();
                //Log.e("TAG", userAccept.get(0).getUsername());
                SocketConnect.getInstance().sendData(Key.EVENT_GET_LIST_FRIEND,
                        SharedPrefs.getInstance().get(Key.ID_USER, String.class));

                myService.setActionAcceptAddFriend(false);
            }else if(myService.getActionDeleteRequest()){
                if (userAddFriends != null) {
                    userAddFriends = null;
                }
                userAddFriends = databaseManager.getListUserAddFriends();
                addFriendAdapter = new RequestAddFriendAdapter(userAddFriends);
                rcRequestAddFriend.setAdapter(addFriendAdapter);
                addFriendAdapter.notifyDataSetChanged();
                myService.setActionDeleteRequest(false);
            }
            update();
        }
    };

    private void initFloatingCircleMenu() {
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fabFindPeople = (FloatingActionButton) view.findViewById(R.id.fab_find);
        fabCreateGroup = (FloatingActionButton) view.findViewById(R.id.fab_create_group);
        fabMaybeKnow = (FloatingActionButton) view.findViewById(R.id.fab_may_know);
        visible = false;

        fab.setImageResource(R.drawable.ic_add);

    }

    @Override
    protected void initAction() {
        fab.setOnClickListener(this);
        fabFindPeople.setOnClickListener(this);
        fabCreateGroup.setOnClickListener(this);
        fabMaybeKnow.setOnClickListener(this);
        addFriendAdapter.setOnClickItem(this);
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_RESULT_GET_LIST_FRIEND, onResultGetListFriend);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.fab:
                if (visible == false) {
                    showFloatMenu();
                    visible = true;
                } else if (visible == true) {
                    hideFloatMenu();
                    visible = false;
                }

                break;

            case R.id.fab_find:
                replaceFragmentMain(new FindFriendFragment());
                break;

            case R.id.fab_create_group:
//                SocketConnect.getInstance().sendData(Key.EVENT_GET_LIST_FRIEND,
//                        SharedPrefs.getInstance().get(Key.ID_USER, String.class));
                //replaceFragmentMain(new UpdateUserFragment());
                break;

            case R.id.fab_may_know:
                SharedPrefs.getInstance().clear();
                Intent i = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(i);
                myService.stopSelf();
                break;

            default:
                break;
        }
    }



    public byte[] convertImageView_To_Byte(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void hideFloatMenu() {
        fabFindPeople.hide();
        fabCreateGroup.hide();
        fabMaybeKnow.hide();
    }

    private void showFloatMenu() {
        fabFindPeople.show();
        fabCreateGroup.show();
        fabMaybeKnow.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("ON","RESUME CONTACT FRAG");
        bodyActivity.getNavigation().setVisibility(View.VISIBLE);
        SocketConnect.getInstance().sendData(Key.EVENT_GET_LIST_FRIEND,
                SharedPrefs.getInstance().get(Key.ID_USER, String.class));
    }


    @Override
    public void onPause() {
        super.onPause();
        bodyActivity.getNavigation().setVisibility(View.GONE);
    }


    @Override
    public void onClickItemRequest(String username) {
        replaceFragmentMain(new InformationUserRequestFragment());
        listener.onSendFromContact(username);
    }

    private Emitter.Listener onResultGetListFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    friends.clear();
                    try {
                        if (data == null || data.length() == 0) return;
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObj = data.getJSONObject(i);
                            friends.add(new Friend(
                                    jsonObj.getString(Key.KEY_ID_USER),
                                    jsonObj.getString(Key.KEY_USERNAME),
                                    jsonObj.getString(Key.KEY_FIRSTNAME),
                                    jsonObj.getString(Key.KEY_LASTNAME),
                                    jsonObj.getString(Key.KEY_LOGO),
                                    jsonObj.getString(Key.KEY_BIRTHDAY),
                                    jsonObj.getString(Key.KEY_SEX),
                                    jsonObj.getString(Key.KEY_DESCRIPTION),
                                    jsonObj.getString(Key.KEY_STATUS),
                                    jsonObj.getString(Key.KEY_TIMEOFF),
                                    jsonObj.getString(Key.KEY_C_ID),
                                    jsonObj.getString(Key.KEY_PHONE_NUMBER)));
                            contactAdapter.notifyDataSetChanged();
                        }


                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };


}
