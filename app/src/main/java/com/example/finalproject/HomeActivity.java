package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.adapter.BookAdapter;
import com.example.finalproject.db.DBHelper;
import com.example.finalproject.model.Book;
import com.example.finalproject.util.BottomNav;
import com.example.finalproject.util.KakaoApi;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    private BookAdapter adapter;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNav.setup(this, BottomNav.HOME);

        etSearch = findViewById(R.id.etSearch);
        ListView listBooks = findViewById(R.id.listBooks);

        adapter = new BookAdapter(this, DBHelper.get(this).getPopularBooks());
        listBooks.setAdapter(adapter);

        listBooks.setOnItemClickListener((parent, view, position, id) -> {
            Book b = (Book) adapter.getItem(position);
            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("book_id", b.id);
            startActivity(intent);
        });

        // 입력 시 로컬 도서 실시간 필터
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                refreshList(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 검색 실행(IME) 시 카카오 도서 검색 화면으로
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String q = etSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(q)) {
                    Intent intent = new Intent(this, SearchActivity.class);
                    intent.putExtra("query", q);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        });

        enrichCoversFromKakao();
    }

    /** 시드 책들의 표지/메타데이터를 카카오에서 받아 채운다 (최초 1회, 백그라운드). */
    private void enrichCoversFromKakao() {
        if (!KakaoApi.hasKey()) return;
        DBHelper db = DBHelper.get(this);
        List<Book> need = db.getBooksWithoutRealCover();
        if (need.isEmpty()) return;

        io.execute(() -> {
            boolean updated = false;
            for (Book b : need) {
                List<Book> results = KakaoApi.search(b.title);
                if (results == null || results.isEmpty()) continue;
                Book match = bestMatch(results, b.author);
                db.updateBookMeta(b.id, match.publisher, match.pubYear, match.cover);
                updated = true;
            }
            if (updated) {
                main.post(() -> refreshList(etSearch.getText().toString().trim()));
            }
        });
    }

    /** 저자명이 겹치는 결과를 우선 선택, 없으면 첫 번째 결과. */
    private Book bestMatch(List<Book> results, String seedAuthor) {
        if (seedAuthor != null && !seedAuthor.isEmpty()) {
            String key = seedAuthor.split("[ ,]")[0];
            for (Book r : results) {
                if (r.author != null && r.author.contains(key)) return r;
            }
        }
        return results.get(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList(etSearch.getText().toString().trim());
    }

    private void refreshList(String query) {
        List<Book> books = TextUtils.isEmpty(query)
                ? DBHelper.get(this).getPopularBooks()
                : DBHelper.get(this).searchBooks(query);
        adapter.setBooks(books);
    }
}
