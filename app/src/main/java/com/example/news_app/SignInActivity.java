package com.example.news_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends Activity {

    EditText editEmail, editPassword;
    Button btnSignIn;
    TextView descText;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        descText = findViewById(R.id.descText);

        // Firebase reference to users node
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        btnSignIn.setOnClickListener(view -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignInActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                validateUser(email, password);
            }
        });

        // Navigate to signup screen
        descText.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });
    }

    private void validateUser(String email, String password) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean found = false;

                for (DataSnapshot user : snapshot.getChildren()) {
                    String dbEmail = user.child("email").getValue(String.class);
                    String dbPassword = user.child("password").getValue(String.class);

                    if (email.equals(dbEmail) && password.equals(dbPassword)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    // Navigate to home or dashboard if needed
                } else {
                    Toast.makeText(SignInActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SignInActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
