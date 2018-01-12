package com.thanhclub.dalochat.service;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.Untils.Key;
import com.thanhclub.dalochat.manager.DatabaseManager;
import com.thanhclub.dalochat.manager.SharedPrefs;
import com.thanhclub.dalochat.manager.SocketConnect;
import com.thanhclub.dalochat.model.Friend;
import com.thanhclub.dalochat.model.UserAddFriend;
import com.thanhclub.dalochat.view.activity.BodyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyService extends Service {
    private Context context;
    private String js;
    private NotificationManagerCompat notificationManager;
    private Notification.Builder notificationBuilder;
    private Notification mNotification;
    private int countReq = 0;
    private DatabaseManager databaseManager;
    private Boolean actionDeleteRequest;
    private Boolean actionAddRequest;
    private Boolean actionAcceptAddFriend;
    private String userDelete;

    private ArrayList<UserAddFriend> userAddFriends;

    private boolean completeAddDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseManager = new DatabaseManager(getBaseContext());
        actionDeleteRequest = false;
        actionAddRequest = false;
        actionAcceptAddFriend = false;
        completeAddDatabase = false;
        userAddFriends = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SocketConnect.getInstance().sendData(Key.EVENT_JOIN_ROOM_MYSELF, SharedPrefs.getInstance().get(Key.USER_NAME, String.class));
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_LISTEN_REQUEST_ADD_FRIEND, onListenRequestAddFriend);
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_LISTEN_SERVER_DELETE_REQUEST_ADDFRIEND, onListenDeleteRequest);
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_LISTEN_ACCEPT_ADD_FRIEND, onListenAcceptAddFriend);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinderRequestAddFriend();
    }

    public class MyBinderRequestAddFriend extends Binder {
        public MyService getResquestAddFriendService() {
            return MyService.this;
        }
    }

    private Emitter.Listener onListenRequestAddFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    try {
                        countReq++;
                        actionAddRequest = true;
                        Log.e("EVENT","AADD FRIEND");
                        JSONObject jsonObj = data.getJSONObject(0);
                        //Toast.makeText(App.getContext(), js.toString(),Toast.LENGTH_LONG).show();
                        //createNotification(js.toString());
                        if (databaseManager.insertListRequest(jsonObj.getString("u_logo"),
                                jsonObj.getString("u_name"),
                                jsonObj.getString("u_name") + " đã gửi lời mời kết bạn",
                                jsonObj.getString("u_first_name"),
                                jsonObj.getString("u_last_name"),
                                jsonObj.getString("u_phone_number"),
                                jsonObj.getInt("u_sex"))){
                            completeAddDatabase = true;
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }

    };

    private Emitter.Listener onListenDeleteRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Log.e("SERVICE", "da xoa");
                        userDelete = data.getString("userRequest");

                        databaseManager.deleteRequestAddfriend(
                                DatabaseManager.TABLE_LISTREQUEST,
                                DatabaseManager.USER_NAME + "=?",
                                new String[]{userDelete});
                        actionDeleteRequest = true;

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }

    };

    private Emitter.Listener onListenAcceptAddFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    JSONObject jsonObj = (JSONObject) args[0];
                    try {
                        actionAcceptAddFriend = true;
                        countReq++;
                        userAddFriends.add(new UserAddFriend(
                                jsonObj.getString(Key.KEY_LOGO),
                                jsonObj.getString(Key.KEY_USERNAME),
                                "",
                                jsonObj.getString(Key.KEY_FIRSTNAME),
                                jsonObj.getString(Key.KEY_LASTNAME),
                                jsonObj.getString(Key.KEY_PHONE_NUMBER),
                                jsonObj.getInt(Key.KEY_SEX)
                              ));
                        Log.e("SERVICE",  jsonObj.getString(Key.KEY_USERNAME)+"");

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }

    };


    private void createNotification(String content) {

        notificationManager = NotificationManagerCompat.from(this);

        Intent intent = new Intent(this, BodyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        notificationBuilder = new Notification.Builder(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotification = notificationBuilder
                    .setSmallIcon(R.drawable.l)
                    .setContentIntent(pendingIntent)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setDefaults(Notification.FLAG_AUTO_CANCEL)
                    .build();
        }
        startForeground(111, mNotification);
    }

    public String getUserSendResquestAddFriend() {
        return js.split(" ")[0];
    }

    public int getCountReq() {
        return countReq;
    }

    public void setCountReq(int countReq) {
        this.countReq = countReq;
    }

    public Boolean getActionDeleteRequest() {
        return actionDeleteRequest;
    }

    public Boolean getActionAddRequest() {
        return actionAddRequest;
    }

    public void setActionAddRequest(Boolean actionAddRequest) {
        this.actionAddRequest = actionAddRequest;
    }

    public void setActionDeleteRequest(Boolean actionDeleteRequest) {
        this.actionDeleteRequest = actionDeleteRequest;
    }

    public String getUserDelete() {
        return userDelete;
    }


    public boolean isCompleteAddDatabase() {
        return completeAddDatabase;
    }

    public void setCompleteAddDatabase(boolean completeAddDatabase) {
        this.completeAddDatabase = completeAddDatabase;
    }

    public void setActionAcceptAddFriend(Boolean actionAcceptAddFriend) {
        this.actionAcceptAddFriend = actionAcceptAddFriend;
    }

    public Boolean getActionAcceptAddFriend() {
        return actionAcceptAddFriend;
    }

    public ArrayList<UserAddFriend> getUserAccpet(){
        return userAddFriends;
    }

    public void removeUserAccept(int position){
        userAddFriends.remove(position);
    }
}
