package com.example.solo2squad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_main);

        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);

        // Set click listener for the ImageView
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click, for example, start another activity
                Intent intent = new Intent(DashboardMain.this, Activity_profile_login.class);
                startActivity(intent);
            }
        });

    }

}
