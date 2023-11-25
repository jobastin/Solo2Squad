package com.example.solo2squad.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.solo2squad.DashboardActivity;
import com.example.solo2squad.DashboardMain;
import com.example.solo2squad.ProfileSection.ProfileSection1Activity;
import com.example.solo2squad.databinding.ActivityIntroBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    ActivityIntroBinding introBinding;
    private FirebaseAuth mAuth;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        introBinding=ActivityIntroBinding.inflate(getLayoutInflater());
        View view = introBinding.getRoot();
        setContentView(view);
        FirebaseAuth.getInstance().signOut();

        mAuth = FirebaseAuth.getInstance();
        introBinding.getStartedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkIfUserLoggedIn();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
     protected void onResume(){
        super.onResume();
        FirebaseAuth.getInstance().signOut();
        checkIfUserLoggedIn();
     }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Add your code to perform actions when the activity is restarting

        // For example, sign out the user if you are using Firebase Authentication
        signOut();
    }

//    private void checkIfUserLoggedIn() {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            // User is already logged in, go to SecondActivity
//            Log.e("SIGNUP",String.valueOf(mAuth.getCurrentUser()));
//            Intent intent = new Intent(MainActivity.this, DashboardMain.class);
//            startActivity(intent);
//            finish(); // Optional: Finish this activity to prevent going back
//        } else {
//            // User is not logged in, redirect to LoginActivity
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
//    }

    private void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                //startActivity(new Intent(SecondActivity.this, MainActivity.class));
            }
        });
    }
    private void checkIfUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int profileSection = dataSnapshot.child("profileSection").getValue(Integer.class);
                        Log.e("Login Google", String.valueOf(profileSection));
                        if (profileSection == 0) {
                            // Redirect to ProfileSection1Activity
                            startActivity(new Intent(MainActivity.this, ProfileSection1Activity.class));
                        } else if (profileSection == 1) {
                            // Redirect to DashboardActivityß
                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                        } else {
                            Log.e("Login button google Last else", String.valueOf(profileSection));
                        }
                        finish();
                    } else {
                        // Handle the case where user data is not available
                        Log.e("Login button google", "User data not available");
                        // Redirect to LoginActivity or handle accordingly
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors if any
                    Log.e("Login button google", "Error reading user data: " + error.getMessage());
                }
            });
        } else {
            // User is not logged in, redirect to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

}
