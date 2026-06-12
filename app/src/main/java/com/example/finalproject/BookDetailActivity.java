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

    // 홈/검색/내 리뷰 화면에서 전달받은 책의 기본키이다.
    private long bookId;
    // false면 최신순, true면 별점순으로 리뷰를 조회한다.
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

        // 상세 화면은 Intent extra의 book_id를 기준으로 책과 리뷰를 조회한다.
        bookId = getIntent().getLongExtra("book_id", -1);
        if (bookId == -1) {
            finish();
            return;
        }

        ListView listReviews = findViewById(R.id.listReviews);
        // ListView 상단에 책 정보 영역(detail_header.xml)을 헤더로 붙이고,
        // 그 아래에 ReviewAdapter가 리뷰 목록을 이어서 표시한다.
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
                // 정렬 글자를 누르면 AlertDialog의 단일 선택 목록으로 최신순/별점순을 고른다.
                .setSingleChoiceItems(sortOptions, sortByRating ? 1 : 0, (dialog, which) -> {
                    sortByRating = (which == 1);
                    tvSort.setText(sortOptions[which]);
                    // 선택이 바뀌면 sortByRating 값만 바꾸고 리뷰를 다시 조회한다(ORDER BY가 바뀜).
                    loadReviews();
                    dialog.dismiss();
                })
                .show());

        //ReviewAdapter로 리뷰 목록을 표시
        reviewAdapter = new ReviewAdapter(this, new java.util.ArrayList<>());
        listReviews.setAdapter(reviewAdapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.fabWrite).setOnClickListener(v -> {
            // 리뷰 작성 화면도 같은 책에 리뷰를 저장해야 하므로 book_id를 넘긴다.
            Intent intent = new Intent(this, WriteReviewActivity.class);
            intent.putExtra("book_id", bookId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 리뷰 작성 화면에서 돌아오면 onResume이 실행된다. 평균 별점·리뷰 수·리뷰 목록을
        // 캐시하지 않고 매번 DB에서 다시 계산해 가져오므로, 방금 쓴 리뷰가 즉시 반영된다.
        bindBook();
        loadReviews();
    }

    private void bindBook() {
        // 책 정보와 평균 별점은 DBHelper.getBook()의 집계 쿼리에서 계산된 Book 객체로 받는다.
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
        // sortByRating 값에 따라 최신순 또는 별점순 ORDER BY로 리뷰를 다시 가져온다.
        reviewAdapter.setReviews(DBHelper.get(this).getReviewsForBook(bookId, sortByRating));
    }
}
