package com.example.news_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
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

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private List<CardItem> allItems = new ArrayList<>();
    private List<CardItem> filteredItems = new ArrayList<>();
    private EditText searchBar;
    private String currentCategory = "Sports";

    private ImageView developerIcon, userIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.searchBar);
        developerIcon = findViewById(R.id.developerIcon);
        userIcon = findViewById(R.id.userIcon);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardAdapter = new CardAdapter(filteredItems, this);
        recyclerView.setAdapter(cardAdapter);

        fetchNewsFromFirebase();

        bottomNav.setSelectedItemId(R.id.nav_sports);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_sports) {
                    setCategory("Sports");
                    return true;
                } else if (id == R.id.nav_academic) {
                    setCategory("Academic");
                    return true;
                } else if (id == R.id.nav_events) {
                    setCategory("Events");
                    return true;
                }
                return false;
            }
        });


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNews(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        developerIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DeveloperInfoActivity.class);
            startActivity(intent);
        });

        userIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setCategory(String category) {
        currentCategory = category;
        filterNews(searchBar.getText().toString());
    }

    private void fetchNewsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("news");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allItems.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    CardItem item = data.getValue(CardItem.class);
                    if (item != null) {
                        allItems.add(item);
                    }
                }
                filterNews(searchBar.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Failed to load news.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterNews(String query) {
        filteredItems.clear();
        for (CardItem item : allItems) {
            if (item == null || item.getCategory() == null || item.getHeadline() == null) {
                continue;
            }
            boolean matchesCategory = item.getCategory().equalsIgnoreCase(currentCategory);
            boolean matchesQuery = item.getHeadline().toLowerCase().contains(query.toLowerCase());

            if (matchesCategory && matchesQuery) {
                filteredItems.add(item);
            }
        }
        cardAdapter.notifyDataSetChanged();
    }
}
