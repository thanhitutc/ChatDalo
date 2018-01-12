package com.thanhclub.dalochat.manager;

import android.content.Context;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import com.github.nkzawa.socketio.client.Socket;
import com.thanhclub.dalochat.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class SocketConnect {
    private final String URL_SERVER = "http://192.168.201.1:3000";
    private static SocketConnect instance;

    public static SocketConnect getInstance() {
        if (instance == null) {
            instance = new SocketConnect();
        }
        return instance;
    }

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(URL_SERVER);
        } catch (URISyntaxException e) {
        }
    }

    private SocketConnect() {
        mSocket.connect();
    }

    public void connection(){
        mSocket.connect();
    }

    public void sendData(String event, String data) {
        mSocket.emit(event, data);
    }


    public Socket getmSocket() {
        return mSocket;
    }
}
