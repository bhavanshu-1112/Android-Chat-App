package com.example.mychat.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mychat.Adapter.UserAdapter;
import com.example.mychat.Model.Chat;
import com.example.mychat.Model.ChatList;
import com.example.mychat.Model.User;
import com.example.mychat.Notifications.Token;
import com.example.mychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragments extends Fragment {

	private RecyclerView recyclerView;
	private UserAdapter userAdapter;
	private List<User> mUsers;

	FirebaseUser fuser;
	DatabaseReference reference;

	private List<ChatList> usersList;
	@Override

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState){
            final View view = inflater.inflate(R.layout.fragments_chats,container,false);

            recyclerView= view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            fuser = FirebaseAuth.getInstance().getCurrentUser();
            usersList = new ArrayList<>();

            reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
         reference.addValueEventListener(new ValueEventListener() {
         	@Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         		usersList.clear();
         		String chatList;
         		for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
         			chatList = (String) snapshot.getKey(ChatList.class);usersList.add(chatList);
         		}
         		chatList();
         	}

         	@Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

         	}

         	@Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

         	}
         });
            updateToken(FirebaseInstanceId.getInstance().getToken());
	           return view;


	}

private void updateToken(String token){
			DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
			Token token1 = new Token(token);
			reference.child(fuser.getUid()).setValue(token1);

}

	private void chatList() {

			mUsers = new ArrayList<>();
			reference = FirebaseDatabase.getInstance().getReference("Users");
			reference.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					mUsers.clear();
					for(DataSnapshot snapshot: dataSnapshot.getChildren()){
						User user = dataSnapshot.getValue(User.class);
						for(ChatList chatList: usersList){
							if(user.getId().equals(chatList.getId())){
								mUsers.add(user);
							}
						}
					}
					userAdapter = new UserAdapter(getContext(),mUsers,true);
					recyclerView.setAdapter(userAdapter);
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {

				}
			});
		}
	}

