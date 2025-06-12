package com.example.news_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DeveloperInfoActivity extends AppCompatActivity {

    Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_info);

        exitButton = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(DeveloperInfoActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // optional: prevent backstack
        });
    }
}
