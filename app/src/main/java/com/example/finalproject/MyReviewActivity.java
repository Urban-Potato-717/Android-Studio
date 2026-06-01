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
        listMyReviews.setEmptyView(findViewById(R.id.tvEmpty));

        adapter = new MyReviewAdapter(this, new java.util.ArrayList<>());
        listMyReviews.setAdapter(adapter);

        listMyReviews.setOnItemClickListener((parent, view, position, id) -> {
            Review r = (Review) adapter.getItem(position);
            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("book_id", r.bookId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        long uid = Session.userId(this);
        List<Review> myReviews = DBHelper.get(this).getMyReviews(uid);
        adapter.setReviews(myReviews);
        tvCount.setText(getString(R.string.my_review_count_format, myReviews.size()));
    }
}
