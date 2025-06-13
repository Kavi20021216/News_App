package com.example.news_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfileActivity extends AppCompatActivity {

    ImageView backButton, profileImage;
    TextView usernameLabel, emailLabel;
    Button editProfileButton, signOutButton;

    FirebaseUser currentUser;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Bind views
        backButton = findViewById(R.id.backButton);
        profileImage = findViewById(R.id.profileImage);
        usernameLabel = findViewById(R.id.usernameLabel);
        emailLabel = findViewById(R.id.emailLabel);
        editProfileButton = findViewById(R.id.editProfileButton);
        signOutButton = findViewById(R.id.signOutButton);

        // Firebase references
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            usernameLabel.setText("User Name : " + (displayName != null ? displayName : "Not set"));
            emailLabel.setText("Email : " + (email != null ? email : "Not available"));
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user is logged in
        }

        // Back button
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(UserProfileActivity.this, HomeActivity.class));
            finish();
        });

        // Sign out
        signOutButton.setOnClickListener(v -> showSignOutDialog());

        // Edit profile
        editProfileButton.setOnClickListener(v -> showEditProfileDialog());
    }

    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_signout, null);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnYes = view.findViewById(R.id.btnYes);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnYes.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(UserProfileActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText editUsername = view.findViewById(R.id.editUsername);
        EditText editEmail = view.findViewById(R.id.editEmail);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        // Pre-fill with current info
        editUsername.setText(usernameLabel.getText().toString().replace("User Name : ", ""));
        editEmail.setText(emailLabel.getText().toString().replace("Email : ", ""));

        btnUpdate.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update Realtime Database
            usersRef.child("username").setValue(newUsername);
            usersRef.child("email").setValue(newEmail);

            // Reflect changes in UI
            usernameLabel.setText("User Name : " + newUsername);
            emailLabel.setText("Email : " + newEmail);

            dialog.dismiss();
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }
}
