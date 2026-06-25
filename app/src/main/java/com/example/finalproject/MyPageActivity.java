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

        // DB를 다시 조회하지 않고, 로그인할 때 Session에 저장해 둔 닉네임/아이디를 그대로 보여준다.
        tvNickname.setText(Session.nickname(this));
        tvUsername.setText("@" + Session.username(this));

        btnLogout.setOnClickListener(v -> {
            // 로그아웃 = SharedPreferences의 세션 값을 비우는 것. 이후 스택을 모두 지우고
            // 로그인 화면으로 보내므로, 뒤로가기로 홈에 다시 들어갈 수 없다.
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
        // 화면이 다시 보일 때마다 "작성한 리뷰 수"를 다시 센다.
        // 리뷰를 쓰고 돌아오면 이 숫자가 바로 갱신된다.
        int count = DBHelper.get(this).getMyReviewCount(Session.userId(this));
        tvReviewCount.setText(count + "개");
    }
}
