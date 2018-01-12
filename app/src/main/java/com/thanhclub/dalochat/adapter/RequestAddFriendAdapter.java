package com.thanhclub.dalochat.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thanhclub.dalochat.App;
import com.thanhclub.dalochat.R;
import com.thanhclub.dalochat.interfaces.OnClickItem;
import com.thanhclub.dalochat.model.User;
import com.thanhclub.dalochat.model.UserAddFriend;
import com.thanhclub.dalochat.view.fragment.ContactFragment;

import java.util.ArrayList;

public class RequestAddFriendAdapter extends RecyclerView.Adapter<RequestAddFriendAdapter.RecyclerResquestAddFriendHoder>{
    private LayoutInflater layoutInflater;
    private ArrayList<UserAddFriend> users;
    private OnClickItem onClickItem;

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    public RequestAddFriendAdapter(ArrayList<UserAddFriend> users){
        this.users = users;
        layoutInflater = LayoutInflater.from(App.getContext());
    }


    @Override
    public RecyclerResquestAddFriendHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_request_add_friend,parent, false);
        return new RecyclerResquestAddFriendHoder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerResquestAddFriendHoder holder, final int position) {
            holder.img.setImageResource(R.drawable.chat);
            holder.txtContent.setText(users.get(position).getBody());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem.onClickItemRequest(users.get(position).getUsername());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (users != null){
            return users.size();
        }
        return 0;
    }

    public void addRequest(String avatar, String user, String body, String lastname, String firstname, String phone, int sex){
        users.add(new UserAddFriend(avatar,user,body,firstname,lastname,phone,sex));
    }

    public void removeRequest(int position){
        users.remove(position);
        notifyDataSetChanged();
    }

    public UserAddFriend getItem(int position){
        return users.get(position);
    }

    public class RecyclerResquestAddFriendHoder extends RecyclerView.ViewHolder  {
        private ImageView img;
        private TextView txtContent;

        public RecyclerResquestAddFriendHoder(final View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.item_avatar_request);
            txtContent = (TextView) itemView.findViewById(R.id.txt_content_request);


        }


    }
}
