package com.example.duan_nckh;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {
    EditText edtTitle, edtContent;
    RelativeLayout btnSubmit ;
    FirebaseFirestore db;
    private ImageView troveBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        troveBtn = findViewById(R.id.backBtn);
        btnSubmit = findViewById(R.id.btnSubmit);
        db = FirebaseFirestore.getInstance();

        troveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnSubmit.setOnClickListener(v -> {
            String title = edtTitle.getText().toString();
            String content = edtContent.getText().toString();

            Map<String, Object> post = new HashMap<>();
            post.put("title", title);
            post.put("content", content);
            post.put("createdAt", FieldValue.serverTimestamp());

            db.collection("posts")
                    .add(post)
                    .addOnSuccessListener(doc -> {
                        Log.d("FIRESTORE", "Post added: " + doc.getId());
                        finish(); // quay lại
                    })
                    .addOnFailureListener(e -> Log.e("FIRESTORE", "Error adding post", e));
        });
    }
}
