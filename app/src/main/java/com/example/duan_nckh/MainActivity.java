package com.example.duan_nckh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    TextInputEditText taikhoanemail, mk ;
    RelativeLayout dangnhap , dangnhapAdmin;
    TextView dangki, quenMK;
    CheckBox saveLogin;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.new_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        taikhoanemail = findViewById(R.id.email);
        mk = findViewById(R.id.mk);
        dangki = findViewById(R.id.dangki);
        saveLogin = findViewById(R.id.Savelogin);
        dangnhapAdmin = findViewById(R.id.loginAdmin);
        dangnhap = findViewById(R.id.login);
        quenMK = findViewById(R.id.forgetPass);

        dangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DangKiActivity.class);
                startActivity(intent);
                finish();
            }
        });

        quenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, QuenMKActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        //save password
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isChecked = sharedPreferences.getBoolean("saveLogin", false);

        if (isChecked) {
            taikhoanemail.setText(sharedPreferences.getString("email", ""));
            mk.setText(sharedPreferences.getString("password", ""));
            saveLogin.setChecked(true);
        }

        dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = String.valueOf(taikhoanemail.getText());
                password = String.valueOf(mk.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Nhập Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Nhập Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!email.equals("quantrivien@gmail.com")) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Kiểm tra xác nhận email trước khi đăng nhập
                                        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
                                            Toast.makeText(MainActivity.this, "Chào mừng đến với trang chủ!", Toast.LENGTH_SHORT).show();

                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            if (saveLogin.isChecked()) {
                                                editor.putString("email", email);
                                                editor.putString("password", password);
                                                editor.putBoolean("saveLogin", true);
                                            } else {
                                                editor.clear();
                                            }
                                            editor.apply();

                                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(MainActivity.this,
                                                    "Vui lòng xác nhận email trước khi đăng nhập!",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "Đăng nhập thất bại, hãy thử lại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng sử dụng phần đăng nhập Admin để truy cập tài khoản này", Toast.LENGTH_SHORT).show();
                }
            }
        });



        dangnhapAdmin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email, password ;
                        email = String.valueOf(taikhoanemail.getText());
                        password = String.valueOf(mk.getText());

                        if(TextUtils.isEmpty(email))
                        {
                            Toast.makeText(MainActivity.this, "Nhập Email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(TextUtils.isEmpty(password))
                        {
                            Toast.makeText(MainActivity.this, "Nhập Password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (email.equals("quantrivien@gmail.com") && password.equals("qtv12345")) {
                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this, "Chào mừng quay trở lại!", Toast.LENGTH_SHORT).show();

                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                if (saveLogin.isChecked()) {
                                                    editor.putString("email", email);
                                                    editor.putString("password", password);
                                                    editor.putBoolean("saveLogin", true);
                                                } else {
                                                    editor.clear();
                                                }
                                                editor.apply();

                                                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Đăng nhập thất bại, hãy thử lại", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Email hoặc mật khẩu không hợp lệ cho Admin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}