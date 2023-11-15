package com.example.solo2squad.ProfileSection;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.solo2squad.Authentication.Address;
import com.example.solo2squad.Authentication.GoogleSignInUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileUtil {

    public static void fetchDataFromFirebase(Activity activity, View textViewName, View textViewEmail, View textViewPhoneNumber, View textViewDOB, View textViewAddress, View imageViewProfile) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        GoogleSignInUsers userData = dataSnapshot.getValue(GoogleSignInUsers.class);

                        if (userData != null) {
                            ((TextView) textViewName).setText(userData.getName());
                            ((TextView) textViewEmail).setText(userData.getEmail());
                            ((TextView) textViewPhoneNumber).setText(userData.getPhoneNumber());
                            ((TextView) textViewDOB).setText(userData.getDob());

                            Address address = userData.getAddress();
                            if (address != null) {
                                String addressText = address.getStreet() + ", " +
                                        address.getCity() + ", " +
                                        address.getProvince() + " " +
                                        address.getPin();
                                ((TextView) textViewAddress).setText(addressText);
                            }

                            Glide.with(activity)
                                    .load(userData.getImage())
                                    .into((ImageView) imageViewProfile);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle the error if needed
                }
            });
        }
    }
}
