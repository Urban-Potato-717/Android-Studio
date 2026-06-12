package com.example.finalproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.db.DBHelper;
import com.example.finalproject.model.Book;
import com.example.finalproject.util.Covers;
import com.example.finalproject.util.Session;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WriteReviewActivity extends AppCompatActivity {

    // 어떤 책에 리뷰를 작성하는지 구분하기 위해 상세 화면에서 받은 book_id를 저장한다.
    private long bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // BookDetailActivity가 넘긴 book_id가 없으면 어떤 책의 리뷰인지 알 수 없으므로 화면을 종료한다.
        bookId = getIntent().getLongExtra("book_id", -1);
        if (bookId == -1) {
            finish();
            return;
        }

        ImageView ivCover = findViewById(R.id.ivCover);
        TextView tvBookTitle = findViewById(R.id.tvBookTitle);
        RatingBar rbInput = findViewById(R.id.rbInput);
        EditText etContent = findViewById(R.id.etContent);
        // "스포일러 포함" 체크박스. 여기서 켠 값이 리뷰와 함께 DB에 저장되어,
        // 나중에 목록에서 그 리뷰를 가릴지 말지 판단하는 기준이 된다.
        CheckBox cbSpoiler = findViewById(R.id.cbSpoiler);
        Button btnSave = findViewById(R.id.btnSave);

        // 작성 화면 상단에도 책 제목과 표지를 보여주기 위해 DB에서 책 정보를 조회한다.
        Book b = DBHelper.get(this).getBook(bookId);
        if (b != null) {
            Covers.load(ivCover, b.cover);
            tvBookTitle.setText(b.title);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            float rating = rbInput.getRating();
            String content = etContent.getText().toString().trim();

            // 별점과 한줄평은 리뷰 저장에 꼭 필요한 값이므로 저장 전에 검사한다.
            if (rating <= 0) {
                Toast.makeText(this, "별점을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "한줄평을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            String today = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date());
            // 입력한 별점/한줄평/스포일러 여부를 reviews 테이블에 한 줄 추가한다.
            // 로그인 사용자의 id·닉네임은 Session에서 가져온다. 이 한 줄이 추가되면
            // 상세 화면으로 돌아갈 때 집계 쿼리가 다시 돌아 평균 별점·리뷰 수가 곧바로 바뀐다.
            DBHelper.get(this).insertReview(
                    bookId,
                    Session.userId(this),
                    Session.nickname(this),
                    rating,
                    content,
                    cbSpoiler.isChecked(),
                    today);

            Toast.makeText(this, "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show();
            // 저장 후 작성 화면을 닫으면 BookDetailActivity의 onResume()이 실행되어 리뷰 목록이 갱신된다.
            finish();
        });
    }
}
