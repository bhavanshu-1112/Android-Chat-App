package com.example.mychat.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mychat.Model.User;
import com.example.mychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

public class ProfileFragment extends Fragment {

TextView username;
DatabaseReference reference;
FirebaseUser fuser;





	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile,container,false);

		username = view.findViewById(R.id.username);

		fuser = FirebaseAuth.getInstance().getCurrentUser();
		reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()) ;

		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				User user = dataSnapshot.getValue(User.class);
				username.setText(user.getUsername());

			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});

		return view;
	}
}

