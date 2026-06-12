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

        // 로그인할 때 Session에 저장해 둔 닉네임과 아이디를 마이페이지에 표시한다.
        tvNickname.setText(Session.nickname(this));
        tvUsername.setText("@" + Session.username(this));

        btnLogout.setOnClickListener(v -> {
            // 로그아웃은 SharedPreferences에 저장된 세션 값을 지우는 방식이다.
            Session.logout(this);
            Intent intent = new Intent(this, LoginActivity.class);
            // 이전 화면 스택을 지워서 뒤로가기로 홈 화면에 다시 들어가지 못하게 한다.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 리뷰 작성/삭제 같은 변화가 있었을 수 있으므로 화면이 다시 보일 때 리뷰 수를 다시 계산한다.
        int count = DBHelper.get(this).getMyReviewCount(Session.userId(this));
        tvReviewCount.setText(count + "개");
    }
}
