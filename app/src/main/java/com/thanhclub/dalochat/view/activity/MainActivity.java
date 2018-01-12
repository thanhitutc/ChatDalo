package com.thanhclub.dalochat.view.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.Untils.ActionManager;
import com.thanhclub.dalochat.Untils.Key;
import com.thanhclub.dalochat.base.BaseActivity;
import com.thanhclub.dalochat.interfaces.OnSendDataFragment;
import com.thanhclub.dalochat.manager.SharedPrefs;
import com.thanhclub.dalochat.manager.SocketConnect;
import com.thanhclub.dalochat.service.MyService;
import com.thanhclub.dalochat.view.dialog.CustomBottomSheet;
import com.thanhclub.dalochat.view.fragment.MainFragment;
import com.thanhclub.dalochat.view.fragment.UpdateUserFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements OnSendDataFragment {
    private MainFragment mainFragment;
    private String username;
    private CustomBottomSheet customBottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void initView() {
        initToolbar();
        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }
        String user = SharedPrefs.getInstance().get(Key.USER_NAME, String.class);
        String pass = SharedPrefs.getInstance().get(Key.PASSWORD, String.class);
        if (user == null || user.equals("")) {
            initFragment(mainFragment);
        } else {
            SocketConnect.getInstance().getmSocket().on(Key.EVENT_RECEIPT_RESULT_LOGIN, onReceiptResultLogin);
            SocketConnect.getInstance().sendData(Key.EVENT_SEND_USERNAME_LOGIN, user + "_" + pass);
        }
        customBottomSheet = new CustomBottomSheet();

    }

    private Emitter.Listener onReceiptResultLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String js = data.getString("ketqua");
                        String[] result = js.split("_");
                        //SharedPrefs.getInstance().put(Key.ID_USER,result[1]+"");

                        if (result[0].equals("true")) {
                            Toast.makeText(getBaseContext(), "Đăng nhập thành công!",Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(getBaseContext(), BodyActivity.class);
                            finish();
                            startActivity(in);
                            SocketConnect.getInstance().sendData(Key.EVENT_INIT_LIST_FRIEND,
                                    SharedPrefs.getInstance().get(Key.ID_USER,String.class));

                            if (ActionManager.isMyServiceRunning(MyService.class)==false){
                                Intent i = new Intent();
                                i.setClass(getBaseContext(),MyService.class);
                                getBaseContext().startService(i);
                            }

                        } else {
                            Toast.makeText(getBaseContext(),"Lỗi đăng nhập", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };


    protected void initFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }


    @Override
    public void onSendData(String user) {
        username = user;
//        Bundle bundle = new Bundle();
//        bundle.putString(Key.ACTION_SEND_DATA_FRAGMENT, user);
//        UpdateUserFragment updateUserFragment = new UpdateUserFragment();
//        updateUserFragment.setArguments(bundle);
    }

    @Override
    public void onSendFromFind(String json) {

    }

    @Override
    public void onSendFromContact(String username) {

    }

    public String getUsername() {
        return username;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.customBottomSheet.onActivityResult(requestCode, resultCode, data);
    }
}
