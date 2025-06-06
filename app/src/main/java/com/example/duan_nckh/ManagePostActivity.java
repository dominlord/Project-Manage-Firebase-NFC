package com.example.duan_nckh;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ManagePostActivity extends AppCompatActivity {

    ListView listView;
    SearchView searchView;
    private ImageView troveBtn ;
    Spinner sortSpinner;
    ArrayAdapter<String> adapter;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<DocumentSnapshot> documents = new ArrayList<>();

    FirebaseFirestore db;

    String currentKeyword = "";
    Query.Direction currentDirection = Query.Direction.DESCENDING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_post);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        listView = findViewById(R.id.listViewPosts);
        searchView = findViewById(R.id.searchView);
        troveBtn = findViewById(R.id.backBtn);
        sortSpinner = findViewById(R.id.sortSpinner);

        db = FirebaseFirestore.getInstance();

        troveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        //change color for search view
        int hintTextid = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(hintTextid);
        textView.setHintTextColor(ContextCompat.getColor(this, R.color.white));
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(adapter);

        setupSearch();
        setupSort();

        loadPosts(Query.Direction.DESCENDING);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            DocumentSnapshot doc = documents.get(position);
            String title = doc.getString("title");
            String content = doc.getString("content");

            Intent intent = new Intent(this, ReadPostActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("postId", doc.getId());
            intent.putExtra("isFromManage", true);
            startActivityForResult(intent, 1);
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
                .addOnFailureListener(e -> Log.d("Search", "Lỗi khi tìm kiếm bài viết", e));
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
                .addOnFailureListener(e -> Log.d("LoadPosts", "Lỗi khi tải bài viết", e));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadPosts(currentDirection);
        }
    }
}
