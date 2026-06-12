package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.adapter.MyReviewAdapter;
import com.example.finalproject.db.DBHelper;
import com.example.finalproject.model.Review;
import com.example.finalproject.util.BottomNav;
import com.example.finalproject.util.Session;

import java.util.List;

public class MyReviewActivity extends AppCompatActivity {

    private MyReviewAdapter adapter;
    private TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);
        BottomNav.setup(this, BottomNav.REVIEW);

        tvCount = findViewById(R.id.tvCount);
        ListView listMyReviews = findViewById(R.id.listMyReviews);
        // 내가 쓴 리뷰가 하나도 없을 때 ListView 대신 tvEmpty가 보이도록 연결한다.
        listMyReviews.setEmptyView(findViewById(R.id.tvEmpty));

        adapter = new MyReviewAdapter(this, new java.util.ArrayList<>());
        listMyReviews.setAdapter(adapter);

        listMyReviews.setOnItemClickListener((parent, view, position, id) -> {
            Review r = (Review) adapter.getItem(position);
            // 내 리뷰 항목을 누르면 그 리뷰가 달린 책의 상세 화면으로 이동한다.
            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("book_id", r.bookId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 보일 때마다 현재 로그인 사용자 id로 "내가 쓴 리뷰"만 모아서 보여준다.
        // getMyReviews는 책 제목/표지까지 JOIN해서 가져오므로 한 페이지에서 과거 리뷰를 회상할 수 있다.
        long uid = Session.userId(this);
        List<Review> myReviews = DBHelper.get(this).getMyReviews(uid);
        adapter.setReviews(myReviews);
        tvCount.setText(getString(R.string.my_review_count_format, myReviews.size()));
    }
}
