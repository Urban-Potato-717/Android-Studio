package com.example.finalproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.db.DBHelper;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etNickname = findViewById(R.id.etNickname);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        Button btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String nickname = etNickname.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirm = etPasswordConfirm.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(nickname)
                    || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm)) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = DBHelper.get(this).signup(username, password, nickname);
            if (id == -1) {
                Toast.makeText(this, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "회원가입 완료! 로그인해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
