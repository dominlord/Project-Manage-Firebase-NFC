package com.example.duan_nckh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<DocumentSnapshot> documents = new ArrayList<>();
    FirebaseFirestore db;
    SearchView searchView;
    Spinner sortSpinner;

    String currentKeyword = "";
    Query.Direction currentDirection = Query.Direction.DESCENDING;

    private ViewFlipper viewFlipper;
    private int[] imageIds = {
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3
    };

    private String[] links = {
            "https://www.facebook.com/DoanTN.HoiSV.Uneti/posts/pfbid025wMjQAPgF5eTvsUNzMiGb8JnbZ1Z66oJJ4sXqgwKc6m7nqoNbTqztAZn4ymBQsZol?rdid=FTzw8ov98bngnvSd#",
            "https://www.facebook.com/DoanTN.HoiSV.Uneti/posts/pfbid0pTm5Zfs7p9K3UtBttnoQMuG4iFSX6RUqPGr4GWfXq9y279o6L1GrEauu7qLvt7wSl?rdid=o2PYrGdIpX9GV2wo#",
            "https://uneti.edu.vn/thong-tin-tuyen-sinh-nam-2025/"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        searchView = findViewById(R.id.searchView);
        sortSpinner = findViewById(R.id.sortSpinner);
        listView = findViewById(R.id.listView);
        db = FirebaseFirestore.getInstance();
        viewFlipper = findViewById(R.id.viewFlipper);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(adapter);

        loadPosts(Query.Direction.DESCENDING);
        setupSearch();
        setupSort();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            DocumentSnapshot doc = documents.get(position);
            String title = doc.getString("title");
            String content = doc.getString("content");

            Intent intent = new Intent(this, ReadPostActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            startActivity(intent);
        });


        for (int i = 0; i < imageIds.length; i++) {
            final int index = i;
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageIds[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);


            // Gắn sự kiện click
            imageView.setOnClickListener(v -> {
                String link = links[index];
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            });

            viewFlipper.addView(imageView);
        }

        // Tự động chuyển ảnh mỗi 3 giây
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping();














        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_news);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_news) {
                return true;
            } else if (itemId == R.id.bottom_supports) {
                startActivity(new Intent(getApplicationContext(), SupportActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchPosts(query.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchPosts(newText.toLowerCase());
                return true;
            }
        });
    }

    private void setupSort() {
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Mới nhất", "Cũ nhất"});
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDirection = (position == 0) ? Query.Direction.DESCENDING : Query.Direction.ASCENDING;
                loadPosts(currentDirection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void searchPosts(String keyword) {
        currentKeyword = keyword;

        Log.d("DebugKeyword", "currentKeyword: " + currentKeyword);

        db.collection("posts")
                .orderBy("title")
                .startAt(keyword)
                .endAt(keyword + "\uf8ff")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    documents.clear();
                    titles.clear();

                    for (DocumentSnapshot doc : querySnapshot) {
                        documents.add(doc);
                        titles.add(doc.getString("title"));
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Search", "Lỗi khi tìm kiếm bài viết", e));
    }

    private void loadPosts(Query.Direction direction) {
        currentDirection = direction;

        Query query = db.collection("posts")
                .orderBy("createdAt", direction);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    documents.clear();
                    titles.clear();

                    for (DocumentSnapshot doc : querySnapshot) {
                        String title = doc.getString("title");
                        documents.add(doc);
                        titles.add(title);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("LoadPosts", "Lỗi khi tải bài viết", e));
    }


}