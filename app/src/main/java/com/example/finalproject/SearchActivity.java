package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.adapter.BookAdapter;
import com.example.finalproject.db.DBHelper;
import com.example.finalproject.model.Book;
import com.example.finalproject.util.KakaoApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    private BookAdapter adapter;
    private TextView tvStatus;
    private List<Book> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EditText etSearch = findViewById(R.id.etSearch);
        ListView listResults = findViewById(R.id.listResults);
        tvStatus = findViewById(R.id.tvStatus);

        adapter = new BookAdapter(this, results);
        listResults.setAdapter(adapter);

        listResults.setOnItemClickListener((parent, view, position, id) -> {
            Book b = (Book) adapter.getItem(position);
            long bookId = DBHelper.get(this).insertOrGetBook(
                    b.title, b.author, b.publisher, b.pubYear, b.genre,
                    b.pageCount, b.cover, b.tagline);
            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("book_id", bookId);
            startActivity(intent);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });

        String query = getIntent().getStringExtra("query");
        if (!TextUtils.isEmpty(query)) {
            etSearch.setText(query);
            etSearch.setSelection(query.length());
            doSearch(query);
        }
    }

    private void doSearch(String query) {
        if (TextUtils.isEmpty(query)) return;

        if (!KakaoApi.hasKey()) {
            showStatus("카카오 API 키가 설정되지 않았습니다.\nlocal.properties에 KAKAO_REST_KEY를 입력하세요.");
            return;
        }

        showStatus("검색 중...");
        executor.execute(() -> {
            List<Book> list = KakaoApi.search(query);
            main.post(() -> {
                if (list == null) {
                    showStatus("검색에 실패했습니다. 네트워크나 API 키를 확인하세요.");
                } else if (list.isEmpty()) {
                    showStatus("검색 결과가 없습니다.");
                } else {
                    tvStatus.setVisibility(View.GONE);
                    adapter.setBooks(list);
                }
            });
        });
    }

    private void showStatus(String msg) {
        adapter.setBooks(new ArrayList<>());
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(msg);
    }
}
