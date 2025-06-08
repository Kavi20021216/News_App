package com.example.news_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends Activity {

    EditText editEmail, editPassword;
    Button btnSignIn;
    TextView descText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        descText = findViewById(R.id.descText);

        btnSignIn.setOnClickListener(view -> {
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignInActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignInActivity.this, "Login clicked", Toast.LENGTH_SHORT).show();
                // Implement login logic here
            }
        });

        descText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });
    }
}