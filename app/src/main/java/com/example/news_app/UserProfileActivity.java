package com.example.news_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity {

    ImageView backButton, profileImage;
    TextView usernameLabel, emailLabel;
    Button editProfileButton, signOutButton;

    DatabaseReference usersRef;
    String loggedInUsername;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        backButton = findViewById(R.id.backButton);
        profileImage = findViewById(R.id.profileImage);
        usernameLabel = findViewById(R.id.usernameLabel);
        emailLabel = findViewById(R.id.emailLabel);
        editProfileButton = findViewById(R.id.editProfileButton);
        signOutButton = findViewById(R.id.signOutButton);

        loggedInUsername = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("loggedInUsername", null);

        if (loggedInUsername != null) {
            usersRef = FirebaseDatabase.getInstance().getReference("users").child(loggedInUsername);

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String dbUsername = snapshot.child("username").getValue(String.class);
                        String dbEmail = snapshot.child("email").getValue(String.class);

                        usernameLabel.setText("User Name : " + dbUsername);
                        emailLabel.setText("Email : " + dbEmail);
                    } else {
                        Toast.makeText(UserProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(UserProfileActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(UserProfileActivity.this, HomeActivity.class));
            finish();
        });

        profileImage.setOnClickListener(v -> chooseImage());

        signOutButton.setOnClickListener(v -> showSignOutDialog());

        editProfileButton.setOnClickListener(v -> showEditProfileDialog());
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .edit()
                    .remove("loggedInUsername")
                    .apply();

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

        editUsername.setText(usernameLabel.getText().toString().replace("User Name : ", ""));
        editEmail.setText(emailLabel.getText().toString().replace("Email : ", ""));

        btnUpdate.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newUsername.equals(loggedInUsername)) {
                usersRef.child("email").setValue(newEmail);
                emailLabel.setText("Email : " + newEmail);
                dialog.dismiss();
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("users");

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        rootRef.child(newUsername).setValue(snapshot.getValue())
                                .addOnSuccessListener(unused -> {
                                    rootRef.child(newUsername).child("username").setValue(newUsername);
                                    rootRef.child(newUsername).child("email").setValue(newEmail);
                                    usersRef.removeValue();

                                    getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                            .edit()
                                            .putString("loggedInUsername", newUsername)
                                            .apply();

                                    loggedInUsername = newUsername;
                                    usersRef = rootRef.child(newUsername);

                                    usernameLabel.setText("User Name : " + newUsername);
                                    emailLabel.setText("Email : " + newEmail);

                                    dialog.dismiss();
                                    Toast.makeText(UserProfileActivity.this, "Username & Email Updated", Toast.LENGTH_SHORT).show();
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(UserProfileActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }
}
