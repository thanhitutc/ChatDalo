package com.thanhclub.dalochat.view.fragment;


import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.nkzawa.emitter.Emitter;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.Untils.Key;
import com.thanhclub.dalochat.base.BaseFragment;
import com.thanhclub.dalochat.manager.SharedPrefs;
import com.thanhclub.dalochat.manager.SocketConnect;
import com.thanhclub.dalochat.view.activity.MainActivity;
import com.thanhclub.dalochat.view.dialog.CustomBottomSheet;

import org.java_websocket.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateUserFragment extends BaseFragment {
    private View view;
    private ImageView imgAvatar;
    private EditText edtFirstName;
    private EditText edtLastName;
    private RadioGroup radioGroupSex;
    private RadioButton rdMale;
    private RadioButton rdFemale;
    private EditText edtPhoneNumber;
    private TextView txtBirthday;
    private EditText edtDescription;
    private Button btnOK;
    private Button btnCancel;
    private Calendar cal;
    private Date dateFinish;
    private int sex;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private String username = "";
    private String avatar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_user, container, false);
//        if (getArguments() != null){
//            username = getArguments().getString(Key.ACTION_SEND_DATA_FRAGMENT);
//            Log.e("FRAG",username);
//        }

        MainActivity activity = (MainActivity) getActivity();
        username = activity.getUsername();
        avatar = "";
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        avatar = SharedPrefs.getInstance().get(Key.AVATAR, String.class);
        if (!avatar.equals("")) {
             Log.e("ava", avatar +"");
            try {
                byte[] byteImg = Base64.decode(avatar);
                Glide.with(getActivity()).load(byteImg).override(96,96).into(imgAvatar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Glide.with(getActivity()).load(R.drawable.user_default).override(96,96).into(imgAvatar);
        }
    }

    @Override
    protected void initView() {

        imgAvatar = (ImageView) view.findViewById(R.id.img_avatar);
        edtFirstName = (EditText) view.findViewById(R.id.edt_firstname);
        edtLastName = (EditText) view.findViewById(R.id.edt_lastname);
        radioGroupSex = (RadioGroup) view.findViewById(R.id.rd_group_sex);
        rdMale = (RadioButton) view.findViewById(R.id.rd_male);
        rdFemale = (RadioButton) view.findViewById(R.id.rd_female);
        edtPhoneNumber = (EditText) view.findViewById(R.id.edt_phonenumber);
        txtBirthday = (TextView) view.findViewById(R.id.txt_birthday);
        edtDescription = (EditText) view.findViewById(R.id.edt_description);
        btnOK = (Button) view.findViewById(R.id.btn_update);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        getDefaultInfor();
        cal = Calendar.getInstance();
        dateFinish = cal.getTime();
    }

    @Override
    protected void initToolbar(boolean showToolbar, String title, boolean homeButton) {
        super.initToolbar(true, "Cập nhật tài khoản", true);
    }

    @Override
    protected void initAction() {
        imgAvatar.setOnClickListener(this);
        txtBirthday.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        SocketConnect.getInstance().getmSocket().on(Key.EVENT_RECEIPT_RESULT_UPDATEUSER, onReceiptResultUpdateUser);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.txt_birthday: {
                DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear,
                                          int dayOfMonth) {
                        //Mỗi lần thay đổi ngày tháng năm thì cập nhật lại TextView Date
                        txtBirthday.setText(
                                year + "/" + (monthOfYear + 1) + "/" + (dayOfMonth));
                        cal.set(year, monthOfYear, dayOfMonth);
                        dateFinish = cal.getTime();
                    }
                };

                String s = txtBirthday.getText().toString();
                String strArrtmp[] = s.split("/");
                int ngay = Integer.parseInt(strArrtmp[0]);
                int thang = Integer.parseInt(strArrtmp[1]) - 1;
                int nam = Integer.parseInt(strArrtmp[2]);
                DatePickerDialog pic = new DatePickerDialog(
                        getActivity(),
                        callback, nam, thang, ngay);
                pic.setTitle("Chọn ngày sinh của bạn");
                pic.show();
            }

            break;

            case R.id.btn_update:
                if (rdMale.isChecked()) {
                    sex = 1;
                } else if (rdFemale.isChecked()) {
                    sex = 0;
                }
                String fisrname = edtFirstName.getText().toString().trim();
                String lastname = edtLastName.getText().toString().trim();
                String logo = avatar;
                String birthday = txtBirthday.getText().toString().trim();
                String phoneNumber = edtPhoneNumber.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();
                if (fisrname.equals("") || lastname.equals("") || phoneNumber.equals("")) {
                    createDialog("Thông báo", "Bạn cần nhập đầy đủ thông tin!");
                } else {
                    Log.e("avat", logo);
                    SocketConnect.getInstance().sendData(Key.EVENT_SEND_REQUEST_UPDATEUSER,
                            username + "_" +
                                    fisrname + "_" +
                                    lastname + "_" +
                                    logo + "_" +
                                    birthday + "_" +
                                    String.valueOf(sex) + "_" +
                                    phoneNumber + "_" +
                                    description);
                }
                break;

            case R.id.btn_cancel:
                getActivity().onBackPressed();
                break;

            case R.id.img_avatar:
                BottomSheetDialogFragment bottomSheet = new CustomBottomSheet();
                bottomSheet.show(getActivity().getSupportFragmentManager(), bottomSheet.getTag());
                break;
        }
    }

    public void getDefaultInfor() {
        //lấy ngày hiện tại của hệ thống
        cal = Calendar.getInstance();
        SimpleDateFormat dft = null;
        //Định dạng ngày / tháng /năm
        dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String strDate = dft.format(cal.getTime());
        //hiển thị lên giao diện
        txtBirthday.setText(strDate);

        //gán cal.getTime() cho ngày hoàn thành và giờ hoàn thành
        dateFinish = cal.getTime();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // replaceFragmentToBack(getParentFragment());
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Emitter.Listener onReceiptResultUpdateUser = new Emitter.Listener() {
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
                            createToast("Cập nhật thông tin tài khoản thành công!");
                            getActivity().onBackPressed();
                        } else {
                            createToast("Lỗi cập nhật");
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
}
