package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.db.DBHelper;
import com.example.finalproject.util.BottomNav;
import com.example.finalproject.util.Session;

public class MyPageActivity extends AppCompatActivity {

    private TextView tvReviewCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        BottomNav.setup(this, BottomNav.MYPAGE);

        TextView tvNickname = findViewById(R.id.tvNickname);
        TextView tvUsername = findViewById(R.id.tvUsername);
        tvReviewCount = findViewById(R.id.tvReviewCount);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvNickname.setText(Session.nickname(this));
        tvUsername.setText("@" + Session.username(this));

        btnLogout.setOnClickListener(v -> {
            Session.logout(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int count = DBHelper.get(this).getMyReviewCount(Session.userId(this));
        tvReviewCount.setText(count + "개");
    }
}
