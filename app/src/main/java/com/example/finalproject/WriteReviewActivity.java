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

    private long bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        bookId = getIntent().getLongExtra("book_id", -1);
        if (bookId == -1) {
            finish();
            return;
        }

        ImageView ivCover = findViewById(R.id.ivCover);
        TextView tvBookTitle = findViewById(R.id.tvBookTitle);
        RatingBar rbInput = findViewById(R.id.rbInput);
        EditText etContent = findViewById(R.id.etContent);
        CheckBox cbSpoiler = findViewById(R.id.cbSpoiler);
        Button btnSave = findViewById(R.id.btnSave);

        Book b = DBHelper.get(this).getBook(bookId);
        if (b != null) {
            Covers.load(ivCover, b.cover);
            tvBookTitle.setText(b.title);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            float rating = rbInput.getRating();
            String content = etContent.getText().toString().trim();

            if (rating <= 0) {
                Toast.makeText(this, "별점을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "한줄평을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            String today = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date());
            DBHelper.get(this).insertReview(
                    bookId,
                    Session.userId(this),
                    Session.nickname(this),
                    rating,
                    content,
                    cbSpoiler.isChecked(),
                    today);

            Toast.makeText(this, "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
