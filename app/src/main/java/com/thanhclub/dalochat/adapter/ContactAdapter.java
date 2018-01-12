package com.thanhclub.dalochat.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thanhclub.dalochat.App;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.model.Friend;

import org.java_websocket.util.Base64;

import java.io.IOException;
import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContatcHolder>{
    private LayoutInflater layoutInflater;
    private ArrayList<Friend> friends;

    public ContactAdapter(ArrayList<Friend> f){
        friends = f;
        layoutInflater = LayoutInflater.from(App.getContext());
    }

    @Override
    public ContatcHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_contact, parent, false);
        return new ContatcHolder(view);
    }

    @Override
    public void onBindViewHolder(ContatcHolder holder, int position) {
        try {
            byte[] byteImg = Base64.decode(friends.get(position).getLogo());
            Glide.with(App.getContext()).load(byteImg).override(96,96).into(holder.imgAvatar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.txtUsername.setText(friends.get(position).getUserName());
        holder.txtRealname.setText(friends.get(position).getFirstname() +" "+ friends.get(position).getLastname());
    }

    @Override
    public int getItemCount() {
        if (friends != null){
            return friends.size();
        }
        return 0;
    }


    public class ContatcHolder extends RecyclerView.ViewHolder{
        private ImageView imgAvatar;
        private TextView txtUsername;
        private TextView txtRealname;
        public ContatcHolder(View itemView) {
            super(itemView);
            imgAvatar = (ImageView) itemView.findViewById(R.id.img_avatar_contact);
            txtUsername = (TextView) itemView.findViewById(R.id.txt_username);
            txtRealname = (TextView) itemView.findViewById(R.id.txt_real_name);
        }
    }
}
