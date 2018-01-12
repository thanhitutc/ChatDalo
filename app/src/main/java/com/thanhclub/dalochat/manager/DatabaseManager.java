package com.thanhclub.dalochat.manager;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.thanhclub.dalochat.App;
import com.thanhclub.dalochat.model.UserAddFriend;

import java.util.ArrayList;

public class DatabaseManager {
    private String DATABASE_NAME = "Contact.sqlite";
    private String DATABASE_PATH = Environment.getDataDirectory().getAbsolutePath() + "/data/com.thanhclub.dalochat/";
    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    public static final String TABLE_LISTREQUEST = "ListRequest";
    public static final String USER_NAME = "u_name";
    public static final String FIRST_NAME = "u_first_name";
    public static final String LAST_NAME = "u_last_name";
    public static final String LOGO = "u_logo";
    public static final String BIRTH_DAY = "u_birth_day";
    public static final String SEX = "u_sex";
    public static final String PHONE_NUMBER = "u_phone_number";
    public static final String BODY = "u_body";


    public DatabaseManager(Context context) {
        this.context = context;
        createDatabase();
    }

    private void createDatabase() {

        if (sqLiteDatabase == null) {
            sqLiteDatabase = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            String sql = "create table if not exists " + TABLE_LISTREQUEST + "(";
            sql += USER_NAME + " TEXT primary key,";
            sql += LOGO + " TEXT,";
            sql += FIRST_NAME + " TEXT,";
            sql += LAST_NAME + " TEXT,";
            sql += PHONE_NUMBER + " TEXT,";
            sql += SEX + " int,";
            sql += BODY + " TEXT)";
            sqLiteDatabase.execSQL(sql);

        }
    }


    private void openDatabase() {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            sqLiteDatabase = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        }


    }

    private void closeDatabase() {
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }
    }

    public boolean insertListRequest(String logo, String username, String body, String firstname, String lastname, String phone, int sex) {
        openDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, username);
        values.put(LOGO, logo);
        values.put(BODY, body);
        values.put(FIRST_NAME, firstname);
        values.put(LAST_NAME, lastname);
        values.put(PHONE_NUMBER, phone);
        values.put(SEX, sex);

        if (sqLiteDatabase.insert(TABLE_LISTREQUEST, null, values) == -1) {
            return false;
        } else {
            return true;
        }

    }

    public ArrayList<UserAddFriend> getListUserAddFriends() {
        openDatabase();
        ArrayList<UserAddFriend> userAddFriends = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from " + TABLE_LISTREQUEST, null);
        if (cursor.getCount() == 0 || cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String logo = cursor.getString(cursor.getColumnIndex(LOGO));
            String u_name = cursor.getString(cursor.getColumnIndex(USER_NAME));
            String body = cursor.getString(cursor.getColumnIndex(BODY));
            String first_name = cursor.getString(cursor.getColumnIndex(FIRST_NAME));
            String last_name = cursor.getString(cursor.getColumnIndex(LAST_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(PHONE_NUMBER));
            int sex = cursor.getInt(cursor.getColumnIndex(SEX));
            userAddFriends.add(new UserAddFriend(logo, u_name, body,first_name, last_name,phone, sex));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return userAddFriends;

    }

    public boolean deleteRequestAddfriend(String tableName, String whereClause, String[] whereArgs) {
        openDatabase();
        if (sqLiteDatabase.delete(tableName, whereClause, whereArgs) == 1) {
            return true;
        }
        closeDatabase();
        return false;
    }
}