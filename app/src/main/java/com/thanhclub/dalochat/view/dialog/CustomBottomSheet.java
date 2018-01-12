package com.thanhclub.dalochat.view.dialog;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.Untils.Key;
import com.thanhclub.dalochat.manager.SharedPrefs;

import org.java_websocket.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class CustomBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    private View view;
    private TextView txtTakePhoto;
    private TextView txtChoosePhoto;
    private TextView txtCancel;
    private int REQUEST_TAKE_PHOTO = 609;
    private int REQUEST_CHOOSE_PHOTO = 906;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_bottom_sheet, container, false);
        initView();
        initAction();
        return view;
    }

    private void initView() {
        txtTakePhoto = (TextView) view.findViewById(R.id.txt_take_photo);
        txtChoosePhoto = (TextView) view.findViewById(R.id.txt_choose_photo);
        txtCancel = (TextView) view.findViewById(R.id.txt_cancel);
    }

    private void initAction(){
        txtChoosePhoto.setOnClickListener(this);
        txtTakePhoto.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_cancel:
                dismiss();
                break;

            case R.id.txt_take_photo:
                takePhoto();
                break;

            case R.id.txt_choose_photo:
                choosePhoto();
                break;
        }
    }

    private void takePhoto(){
        checkPermistionCamera();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    private void choosePhoto(){
        checkPermistionReadStorate();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_CHOOSE_PHOTO){
                Uri imageUri = data.getData();
                //byte[] bytesImg = convertFileToByte(imageUri.getPath());
                try {
                    InputStream is = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap bm = BitmapFactory.decodeStream(is);
                    byte[] bytesImg = convertBitmapToByte(bm);
                    //String avatar = Arrays.toString(bytesImg);
                    String avatar = Base64.encodeBytes(bytesImg);
                    SharedPrefs.getInstance().put(Key.AVATAR, avatar );
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                dismiss();
            }else if (requestCode == REQUEST_TAKE_PHOTO){
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                byte[] bImg = convertBitmapToByte(bm);
               // String avatar = Arrays.toString(bImg);
                String avatar = Base64.encodeBytes(bImg);
                SharedPrefs.getInstance().put(Key.AVATAR, avatar );
                dismiss();
            }
        }

    }

    public byte[] convertFileToByte(String path){
        File file = new File(path);
        int size = (int) file.length();

        FileInputStream fis = null;
        byte[] bytes = new byte[size];
        //byte[] bFile = new byte[(int) file.length()];
        try {
            // convert file into array of bytes
            fis = new FileInputStream(file);
            fis.read(bytes);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public byte[] convertBitmapToByte(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public void checkPermistionReadStorate() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }
    }

    public void checkPermistionCamera() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                    getContext(), Manifest.permission.CAMERA)) {


            } else {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.CAMERA},
                        200);
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
