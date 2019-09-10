package com.example.mychat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.mychat.Adapter.MessageAdapter;
import com.example.mychat.Fragments.APIService;
import com.example.mychat.Model.Chat;
import com.example.mychat.Model.User;
import com.example.mychat.Notifications.Client;
import com.example.mychat.Notifications.Data;
import com.example.mychat.Notifications.MyResponse;
import com.example.mychat.Notifications.Sender;
import com.example.mychat.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.lang.String;

public class MessageActivity extends AppCompatActivity {

	TextView username;
	FirebaseUser fuser;
	DatabaseReference reference;
	Intent intent;
	ImageButton btn_send;
	EditText text_send;

	ValueEventListener seenListener;

	MessageAdapter messageAdapter;
	List<Chat> mchat;

	RecyclerView recyclerView;

	APIService apiService;

	boolean notify = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);



		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("");
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	    toolbar.setNavigationOnClickListener(new View.OnClickListener(){
	    	@Override
		    public void onClick(View view) {
			    startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		    }
	    });

		    recyclerView = findViewById(R.id.recycler_view);
		    recyclerView.setHasFixedSize(true);
		    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
		    linearLayoutManager.setStackFromEnd(true);
		    recyclerView.setLayoutManager(linearLayoutManager)

		    username = (TextView)findViewById(R.id.username);
		    btn_send = (ImageButton)findViewById(R.id.btn_send);
		    text_send = (EditText)findViewById(R.id.text_send);

		    intent = getIntent();
           final String userid = intent.getStringExtra("userid");
		    fuser = FirebaseAuth.getInstance().getCurrentUser();

            btn_send.setOnClickListener(new View.OnClickListener(){
	            @Override
	            public void onClick(View v) {
	            	notify = true;
		            String msg = text_send.getText().toString();
		            if(!msg.equals("")){
			            sendMessage(fuser.getUid(),userid,msg);
		            }else{
			            Toast.makeText(MessageActivity.this,"You can't send an empty message",Toast.LENGTH_SHORT).show();
		            }text_send.setText("");
	            }



		    });
    apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

            reference.addValueEventListener(new ValueEventListener(){
            	@Override
			            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
            		User user = dataSnapshot.getValue(User.class);
            		username.setText(user.getUsername());
                 readMessages(fuser.getUid(),userid);
			    }

	            @Override
	            public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {

	            }
            });seenMessage(userid);
	    }
	    private void seenMessage(final String userid){
		reference = FirebaseDatabase.getInstance().getReference("Chats");
		seenListener = reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
				for(DataSnapshot snapshot:dataSnapshot.getChildren()){
					Chat chat = snapshot.getValue(Chat.class);


					if(chat.getReceiver().equals(fuser.getUid())&&chat.getSender().equals(userid)){
						HashMap<String,Object> hashMap = new HashMap<>();
						hashMap.put("isseen",true);
						snapshot.getRef().updateChildren(hashMap);

					}
				}
			}

			@Override
			public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {

			}
		});


	    }
private void currentUser(String userid){
	SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
	editor.putString("currentuser",userid);
	editor.apply();

}

	private void sendMessage(String sender, final String receiver, String message){
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
		HashMap<String,Object> hashMap = new HashMap<>();
		hashMap.put("sender",sender);
		hashMap.put("receiver",receiver);
		hashMap.put("message",message);
		hashMap.put("isseen",false);


		reference.child("Chats").push().setValue(hashMap);


		final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
		                             .child(fuser.getUid())
				                     .child(userid);

		chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
				if(!dataSnapshot.exists()){

					chatRef.child("id").setValue(userid);

				}
			}

			@Override
			public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {

			}
		});
		final String msg = message;
		reference= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
				User user = dataSnapshot.getValue(User.class);
				if(notify) {
					sendNotification(receiver, user.getUsername(), msg);
				} notify = false;

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
		private void sendNotification(String receiver,String username,String message){
			final DatabaseReference tokens =  FirebaseDatabase.getInstance().getReference("Tokens");
			Query query = tokens.orderByKey().equalTo(receiver);
			query.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					for(DataSnapshot snapshot:dataSnapshot.getChildren()){
						Token token = snapshot.getValue(Token.class);

						Sender sender = new Sender(data,token.getToken());

						apiService.sendNotification(sender)
								.enqueue(new Callback<MyResponse>() {
									@Override
									public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
										if(response.body()==200){
											if(response.body().success != 1){
												Toast.makeText(MessageActivity.this,"Failed!",Toast.LENGTH_SHORT).show();
											}
										}
									}

									@Override
									public void onFailure(Call<MyResponse> call, Throwable t) {

									}
								});

						Data data = new Data(fuser.getUid(),R.mipmap.ic_launcher,username+": "+ message , "New Message",userid);

					}
				}

				@Override
				public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {

				}
			});
		}
	}
	private void readMessages(final String myid, final String userid){
		mchat = new ArrayList<>();
		reference = FirebaseDatabase.getInstance().getReference("Chats");
		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             mchat.clear();
             for(DataSnapshot snapshot: dataSnapshot.getChildren()){
             	Chat chat = snapshot.getValue(Chat.class);
             	if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                chat.getSender().equals(myid) && chat.getReceiver().equals(userid)){
                  mchat.add(chat);

                }
                messageAdapter = new MessageAdapter(MessageActivity.this,mchat);
             	recyclerView.setAdapter(messageAdapter);
             }
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}
	private void status(String status){
		reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

		HashMap<String , Object> hashMap = new HashMap<>();
		hashMap.put("status",status);
		reference.updateChildren(hashMap);

	}

	@Override
	protected void onResume() {
		super.onResume();
		status("online");
		String userid;
		currentUser(userid);
	}

	@Override
	protected void onPause() {
		super.onPause();
		reference.removeEventListener(seenListener);
		status("offline");
		currentUser("none");


	}
}
