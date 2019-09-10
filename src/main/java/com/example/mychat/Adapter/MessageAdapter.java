package com.example.mychat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mychat.MessageActivity;
import com.example.mychat.Model.Chat;
import com.example.mychat.Model.User;
import com.example.mychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
private FirebaseUser fuser;

	public static final int MSG_TYPE_LEFT = 0;
	public static final int MSG_TYPE_RIGHT = 1;

private Context mContext;
private List<Chat> mChat;

public MessageAdapter(Context mContext, List<Chat> mChat){
		this.mChat= mChat;
		this.mContext = mContext;
		}

@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
	if (viewType == MSG_TYPE_RIGHT) {


		View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);

		return new MessageAdapter.ViewHolder(view);
	}else{
		View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
		return new MessageAdapter.ViewHolder(view);
	}
}
@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

Chat chat = mChat.get(position);
holder.show_message.setText(chat.getMessage());

if(position==mChat.size()-1){
	if(chat.isIsseen()){
		holder.txt_seen.setText("Seen");

	}else{
		holder.txt_seen.setText("Delivered");
	}
}else{
	holder.txt_seen.setVisibility(View.GONE);
}
		}

@Override
public int getItemCount() {
		return mChat.size();
		}

public class ViewHolder extends RecyclerView.ViewHolder{
	public TextView show_message;

	public TextView txt_seen;


	public ViewHolder(@NonNull View itemView) {
		super(itemView);
		show_message = itemView.findViewById(R.id.show_message);

		txt_seen = itemView.findViewById(R.id.txt_seen);

	}
}
@Override
	public int getItemViewType(int position){
	fuser = FirebaseAuth.getInstance().getCurrentUser();
	if(mChat.get(position).getSender().equals(fuser.getUid())){
		return MSG_TYPE_RIGHT;
	}else{
		return MSG_TYPE_LEFT;
	}
}
}
