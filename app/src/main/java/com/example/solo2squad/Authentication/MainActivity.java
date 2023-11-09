package com.example.solo2squad.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.solo2squad.databinding.ActivityIntroBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    ActivityIntroBinding introBinding;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        introBinding=ActivityIntroBinding.inflate(getLayoutInflater());
        View view = introBinding.getRoot();
        setContentView(view);

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

    private void checkIfUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, go to SecondActivity
            Log.e("SIGNUP",String.valueOf(mAuth.getCurrentUser()));
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
            finish(); // Optional: Finish this activity to prevent going back
        } else {
            // User is not logged in, redirect to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
