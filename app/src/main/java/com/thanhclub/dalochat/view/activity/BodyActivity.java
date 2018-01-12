package com.thanhclub.dalochat.view.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.Untils.ActionManager;
import com.thanhclub.dalochat.Untils.Key;
import com.thanhclub.dalochat.base.BaseActivity;
import com.thanhclub.dalochat.interfaces.OnSendDataFragment;
import com.thanhclub.dalochat.service.MyService;
import com.thanhclub.dalochat.view.fragment.CommunityFragment;
import com.thanhclub.dalochat.view.fragment.ContactFragment;
import com.thanhclub.dalochat.view.fragment.InformationUserSearchedFragment;
import com.thanhclub.dalochat.view.fragment.MessageFragment;
import com.thanhclub.dalochat.view.fragment.MoreFragment;

public class BodyActivity extends BaseActivity implements OnSendDataFragment {
    private BottomNavigationView navigation;
    private String json;
    private Intent intentS;
    private MyService myService;
    private Handler handlerUpdate;
    private int countRe;
    private AHBottomNavigation bottomNavigation;
    private String usernameRequestAddFriend;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);
        initView();
    }

    protected void initView() {
//        navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        initBottomNavigation();
        initFragment(new MessageFragment());

        if (ActionManager.isMyServiceRunning(MyService.class) == false) {
            Intent i = new Intent();
            i.setClass(getBaseContext(), MyService.class);
            startService(i);
        }
        intentS = new Intent();
        intentS.setClass(this, MyService.class);
        bindService(intentS, serviceConnection, BIND_AUTO_CREATE);
        handlerUpdate = new Handler();
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
        handlerUpdate.postDelayed(onUpdate, 2000);
    }

    private Runnable onUpdate = new Runnable() {
        @Override
        public void run() {
            if (countRe != myService.getCountReq()) {
                countRe = myService.getCountReq();
                if (countRe != 0 && bottomNavigation.getCurrentItem()!=1){
                    createNotificationBottomNavigation(countRe+"",1);
                }
            }
            update();
        }
    };


    @Override
    protected void initFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container_layout, fragment);
        ft.commit();
    }

    private void initBottomNavigation() {
        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem itemMessage = new AHBottomNavigationItem("Message", R.drawable.ic_message);
        AHBottomNavigationItem itemContact = new AHBottomNavigationItem("Contact", R.drawable.ic_contact);
        AHBottomNavigationItem itemCommunity = new AHBottomNavigationItem("Community", R.drawable.ic_community);
        AHBottomNavigationItem itemMore = new AHBottomNavigationItem("More", R.drawable.ic_more);


        bottomNavigation.addItem(itemMessage);
        bottomNavigation.addItem(itemContact);
        bottomNavigation.addItem(itemCommunity);
        bottomNavigation.addItem(itemMore);

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#f1f1f1"));

        bottomNavigation.setAccentColor(Color.parseColor("#3b5998"));

        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        changeFragment(new MessageFragment());
                        return true;
                    case 1:
                        changeFragment(new ContactFragment());
                        removeNotificationVBottomNavigation(1);
                        myService.setCountReq(0);
                        return true;
                    case 2:
                        changeFragment(new CommunityFragment());
                        return true;
                    case 3:
                        changeFragment(new MoreFragment());
                        return true;
                }
                return false;
            }
        });
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override
            public void onPositionChange(int y) {
            }
        });

    }




    public AHBottomNavigation getNavigation() {
        return bottomNavigation;
    }

    @Override
    public void onSendData(String user) {

    }

    @Override
    public void onSendFromFind(String json) {
        Bundle bundle = new Bundle();
        bundle.putString(Key.ACTION_SEND_DATA_FROM_FINDFRAGMENT, json);
        InformationUserSearchedFragment infoFragment = new InformationUserSearchedFragment();
        infoFragment.setArguments(bundle);

        this.json = json;
    }

    @Override
    public void onSendFromContact(String username) {
        usernameRequestAddFriend = username;
    }

    public String getUsernameRequestAddFriend() {
        return usernameRequestAddFriend;
    }

    public String getJson() {
        return json;
    }


    private void createNotificationBottomNavigation(String content, int position){
        AHNotification notification = new AHNotification.Builder()
                .setText(content)
                .setBackgroundColor(Color.RED)
                .setTextColor(Color.BLACK)
                .build();
        bottomNavigation.setNotification(notification, position);
    }

    private void removeNotificationVBottomNavigation(int position){
        bottomNavigation.setNotification("",position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
