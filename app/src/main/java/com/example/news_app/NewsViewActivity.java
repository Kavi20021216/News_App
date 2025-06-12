package com.example.news_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewsViewActivity extends AppCompatActivity {

    ImageView backButton, bannerImage, userIcon;
    TextView categoryText, dateText, headlineText, descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        backButton = findViewById(R.id.backButton);
        bannerImage = findViewById(R.id.bannerImage);
        userIcon = findViewById(R.id.userIcon);
        categoryText = findViewById(R.id.categoryText);
        dateText = findViewById(R.id.dateText);
        headlineText = findViewById(R.id.headlineText);
        descriptionText = findViewById(R.id.descriptionText);

        // Back button click
        backButton.setOnClickListener(v -> finish());

        // User profile icon click
        userIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NewsViewActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Get data from intent
        String base64Image = getIntent().getStringExtra("image");
        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");
        String description = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");

        // Set data to views
        headlineText.setText(title);
        dateText.setText(date);
        descriptionText.setText(description);

        // Capitalize category
        if (category != null && category.length() > 0) {
            String formattedCategory = category.substring(0, 1).toUpperCase() + category.substring(1).toLowerCase();
            categoryText.setText(formattedCategory);
        } else {
            categoryText.setText("");
        }

        // Decode image
        if (base64Image != null && base64Image.contains(",")) {
            base64Image = base64Image.split(",")[1];
        }
        try {
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            bannerImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            bannerImage.setImageResource(R.drawable.news_image1); // fallback
        }
    }
}
