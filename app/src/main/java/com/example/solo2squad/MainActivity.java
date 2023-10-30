package com.example.solo2squad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.solo2squad.databinding.ActivityIntroBinding;


public class MainActivity extends AppCompatActivity {

    ActivityIntroBinding introBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        introBinding=ActivityIntroBinding.inflate(getLayoutInflater());
        View view = introBinding.getRoot();
        setContentView(view);
        introBinding.getStartedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}