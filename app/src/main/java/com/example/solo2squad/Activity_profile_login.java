package com.example.solo2squad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.solo2squad.Authentication.Address;
import com.example.solo2squad.Authentication.GoogleSignInUsers;
import com.example.solo2squad.Authentication.LoginActivity;
import com.example.solo2squad.Authentication.MainActivity;
import com.example.solo2squad.Authentication.SecondActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//public class Activity_profile_login extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile_login);
//
//
//    }
//}



public class Activity_profile_login extends AppCompatActivity {

    private TextView textViewName, textViewEmail, textViewPhoneNumber, textViewDOB, textViewAddress;
    private ImageView imageViewProfile;
    private Button buttonEdit, buttonUpdate;

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private String userId;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_login);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            textViewName = findViewById(R.id.textView4);
            textViewEmail = findViewById(R.id.textView5);
            textViewPhoneNumber = findViewById(R.id.textViewPhone);
            textViewDOB = findViewById(R.id.textViewDOB);
            textViewAddress = findViewById(R.id.textViewAddress);
            imageViewProfile = findViewById(R.id.imageView4);
//            buttonEdit = findViewById(R.id.buttonEdit);
//            buttonUpdate = findViewById(R.id.buttonUpdate);
            ConstraintLayout logoutLayout = findViewById(R.id.LogoutLayout);


            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gsc = GoogleSignIn.getClient(this,gso);
            fetchDataFromFirebase();



//            buttonEdit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Enable editing of TextViews
//                    enableEditing(true);
//                }
//            });

            logoutLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }

                private void signOut() {
                    gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            finish();
                            startActivity(new Intent(Activity_profile_login.this, LoginActivity.class));
                        }
                    });
                }
            });

//            buttonUpdate.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Update data in Firebase
//                    updateDataInFirebase();
//                    // Disable editing after update
//                    enableEditing(false);
//                }
//            });
        }
    }

    private void fetchDataFromFirebase() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user data from dataSnapshot and populate views
                    // You'll need to replace "YourDataModel" with the actual model class for your data
                    GoogleSignInUsers userData = dataSnapshot.getValue(GoogleSignInUsers.class);

                    if (userData != null) {
                        textViewName.setText(userData.getName());
                        textViewEmail.setText(userData.getEmail());
                        textViewPhoneNumber.setText(userData.getPhoneNumber());
                        textViewDOB.setText(userData.getDob());
                        // Populate address
                        Address address = userData.getAddress();
                        if (address != null) {
                            String addressText = address.getStreet() + ", " +
                                    address.getCity() + ", " +
                                    address.getProvince() + " " +
                                    address.getPin();
                            textViewAddress.setText(addressText);
                        }

//                        // Load profile image using Glide or any other image loading library
                        Glide.with(Activity_profile_login.this)
                                .load(userData.getImage())
                                .into(imageViewProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateDataInFirebase() {
        // Update data in Firebase using userRef
        // You need to get the updated data from your EditTexts or other input fields
        // and update the corresponding fields in the database
        // Example:
        // String updatedName = editTextName.getText().toString();
        // userRef.child("name").setValue(updatedName);
        // Repeat this for other fields that need to be updated
    }

    private void enableEditing(boolean enable) {
        // Enable or disable editing of TextViews based on the 'enable' parameter
        // Example:
        // editTextName.setEnabled(enable);
        // Repeat this for other TextViews that need to be editable
    }
}
