package com.example.mychat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychat.MainActivity;
import com.example.mychat.MessageActivity;
import com.example.mychat.Model.Chat;
import com.example.mychat.Model.User;
import com.example.mychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
  private Context mContext;
  private List<User> mUsers;
  private boolean isChat;
String theLastMessage;


  public UserAdapter(Context mContext, List<User> mUser,boolean isChat){
   this.mUsers = mUsers;
   this.mContext = mContext;
   this.isChat = isChat;


  }



	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
		return new UserAdapter.ViewHolder(view);
	}

	@Override


	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
 final User user = mUsers.get(position);
 holder.username.setText(user.getUsername());

 if(isChat){
 	lastMessage(user.getId(),holder.last_msg);
 }else{
 	holder.last_msg.setVisibility(View.GONE);

 }

 if(isChat) {
	 if (user.getStatus().equals("online")) {
		 Toast.makeText(UserAdapter.this, "Online", Toast.LENGTH_SHORT).show();
	 } else if (user.getStatus().equals("offline"))
		 Toast.makeText(UserAdapter.this, "Offline", Toast.LENGTH_SHORT).show();

 }else {
	 Toast.makeText().show();
 }
holder.itemView.setOnClickListener(new View.OnClickListener(){

	@Override
	public void onClick(View view) {
		Intent intent = new Intent(mContext, MessageActivity.class);
		intent.putExtra("user",user.getId());
		mContext.startActivity(intent);
	}
});

	}

	@Override
	public int getItemCount() {
		return mUsers.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder{
  	public TextView username;
  	private TextView last_msg;


	  public ViewHolder(@NonNull View itemView) {
		  super(itemView);
		  username = itemView.findViewById(R.id.username);
          last_msg = itemView.findViewById(R.id.last_msg);
	  }
  }
  private void lastMessage(final String userid, final TextView last_msg){
      theLastMessage = "default";
	  final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
	  DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

	  reference.addValueEventListener(new ValueEventListener() {
		  @Override
		  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
			  for(DataSnapshot snapshot: dataSnapshot.getChildren()){
			  	Chat chat = snapshot.getValue(Chat.class);
				  				  if(chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid) ||
							      chat.getSender().equals(firebaseUser.getUid())&& chat.getReceiver().equals(userid)){
                                     theLastMessage = chat.getMessage();

							      }
			  }
			  switch (theLastMessage){
				  case "default":
				  	last_msg.setText("No Message");
				  	break;
				  	default:
				  		last_msg.setText(theLastMessage);
				  		break;

			  }
			  theLastMessage = "default";

		  }

		  @Override
		  public void onCancelled(@NonNull DatabaseError databaseError) {

		  }
	  })
  }
}