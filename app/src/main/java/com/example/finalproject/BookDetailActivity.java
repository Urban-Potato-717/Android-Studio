package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.adapter.ReviewAdapter;
import com.example.finalproject.db.DBHelper;
import com.example.finalproject.model.Book;
import com.example.finalproject.util.BottomNav;
import com.example.finalproject.util.Covers;

import java.util.Locale;

public class BookDetailActivity extends AppCompatActivity {

    private long bookId;
    private boolean sortByRating = false;
    private ReviewAdapter reviewAdapter;

    private ImageView ivCover;
    private TextView tvTitle, tvAuthor, tvPublisher, tvGenre, tvPages, tvAvg, tvReviewCount;
    private RatingBar rbAvg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        BottomNav.setup(this, -1);

        bookId = getIntent().getLongExtra("book_id", -1);
        if (bookId == -1) {
            finish();
            return;
        }

        ListView listReviews = findViewById(R.id.listReviews);
        View header = LayoutInflater.from(this).inflate(R.layout.detail_header, listReviews, false);
        listReviews.addHeaderView(header, null, false);

        ivCover = header.findViewById(R.id.ivCover);
        tvTitle = header.findViewById(R.id.tvTitle);
        tvAuthor = header.findViewById(R.id.tvAuthor);
        tvPublisher = header.findViewById(R.id.tvPublisher);
        tvGenre = header.findViewById(R.id.tvGenre);
        tvPages = header.findViewById(R.id.tvPages);
        tvAvg = header.findViewById(R.id.tvAvg);
        tvReviewCount = header.findViewById(R.id.tvReviewCount);
        rbAvg = header.findViewById(R.id.rbAvg);

        TextView tvSort = header.findViewById(R.id.tvSort);
        final CharSequence[] sortOptions = getResources().getTextArray(R.array.sort_options);
        tvSort.setText(sortOptions[sortByRating ? 1 : 0]);
        tvSort.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.sort_title)
                .setSingleChoiceItems(sortOptions, sortByRating ? 1 : 0, (dialog, which) -> {
                    sortByRating = (which == 1);
                    tvSort.setText(sortOptions[which]);
                    loadReviews();
                    dialog.dismiss();
                })
                .show());

        reviewAdapter = new ReviewAdapter(this, new java.util.ArrayList<>());
        listReviews.setAdapter(reviewAdapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.fabWrite).setOnClickListener(v -> {
            Intent intent = new Intent(this, WriteReviewActivity.class);
            intent.putExtra("book_id", bookId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindBook();
        loadReviews();
    }

    private void bindBook() {
        Book b = DBHelper.get(this).getBook(bookId);
        if (b == null) {
            Toast.makeText(this, "책 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Covers.load(ivCover, b.cover);
        tvTitle.setText(b.title);
        tvAuthor.setText(b.author);
        tvPublisher.setText(b.publisher + " · " + b.pubYear);
        tvGenre.setText(b.genre);
        tvPages.setText(b.pageCount + "쪽");
        tvAvg.setText(String.format(Locale.KOREA, "%.1f", b.avgRating));
        tvReviewCount.setText(b.reviewCount + "개의 리뷰");
        rbAvg.setRating((float) b.avgRating);
    }

    private void loadReviews() {
        reviewAdapter.setReviews(DBHelper.get(this).getReviewsForBook(bookId, sortByRating));
    }
}
