package com.example.news_app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    TextView welcomeText;
    EditText searchBar;
    ImageView searchIcon;
    RecyclerView recyclerView;
    BottomNavigationView bottomNavigationView;
    CardAdapter adapter;
    List<CardItem> itemList;
    List<CardItem> allItems;

    String selectedCategory = "Sports"; // Default to Sports

    DatabaseReference newsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeText = findViewById(R.id.welcomeText);
        searchBar = findViewById(R.id.searchBar);
        searchIcon = findViewById(R.id.searchIcon);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottomNav);

        String username = getIntent().getStringExtra("username");
        welcomeText.setText(username != null && !username.isEmpty() ? "Hi, " + username : "Hi, User");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        allItems = new ArrayList<>();
        adapter = new CardAdapter(itemList, this);
        recyclerView.setAdapter(adapter);

        newsRef = FirebaseDatabase.getInstance().getReference("news");
        fetchNewsFromFirebase(); // 🔄 This will show only "Sports" initially

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNews(s.toString());
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_sports) {
                selectedCategory = "Sports";
            } else if (id == R.id.nav_academic) {
                selectedCategory = "Academic";
            } else if (id == R.id.nav_events) {
                selectedCategory = "Events";
            }
            filterNews(searchBar.getText().toString());
            return true;
        });

        // Programmatically select Sports tab on load
        bottomNavigationView.setSelectedItemId(R.id.nav_sports);
    }

    private void fetchNewsFromFirebase() {
        newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allItems.clear();
                for (DataSnapshot newsSnapshot : snapshot.getChildren()) {
                    String title = newsSnapshot.child("headline").getValue(String.class);
                    String base64Image = newsSnapshot.child("image").getValue(String.class);
                    String category = newsSnapshot.child("category").getValue(String.class);

                    if (title != null && base64Image != null && category != null) {
                        CardItem item = new CardItem();
                        item.setTitle(title);
                        item.setBase64Image(base64Image);
                        item.setCategory(category);
                        allItems.add(item);
                    }
                }
                filterNews(searchBar.getText().toString()); // 🔍 Filter after fetching
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Failed to load news: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterNews(String query) {
        query = query.toLowerCase().trim();
        itemList.clear();

        for (CardItem item : allItems) {
            boolean matchesCategory = selectedCategory.equals("All") || item.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesQuery = query.isEmpty() || (item.getTitle() != null && item.getTitle().toLowerCase().contains(query));

            if (matchesCategory && matchesQuery) {
                itemList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
