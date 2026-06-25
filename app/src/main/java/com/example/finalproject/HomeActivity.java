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

    // 카카오 API처럼 시간이 걸리는 작업은 백그라운드 스레드에서 실행한다.
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    // 백그라운드 작업이 끝난 뒤 화면 갱신은 메인(UI) 스레드에서 해야 한다.
    private final Handler main = new Handler(Looper.getMainLooper());

    private BookAdapter adapter;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //HomeActivity - layout 화면 연결
        setContentView(R.layout.activity_home);
        BottomNav.setup(this, BottomNav.HOME);

        etSearch = findViewById(R.id.etSearch);
        ListView listBooks = findViewById(R.id.listBooks);

        // DBHelper.getPopularBooks()로 DB에서 인기 도서 목록을 가져오고
        adapter = new BookAdapter(this, DBHelper.get(this).getPopularBooks());
        // BookAdapter가 그 목록을 ListView에 표시함
        listBooks.setAdapter(adapter);

        listBooks.setOnItemClickListener((parent, view, position, id) -> {
            Book b = (Book) adapter.getItem(position);
            // 홈 목록의 책을 누르면 해당 책의 book_id만 상세 화면으로 전달한다.
            // 상세 화면은 이 id로 DB에서 책 정보와 리뷰를 다시 조회한다.
            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("book_id", b.id);
            startActivity(intent);
        });

        // 검색창에 글자를 입력하는 동안에는 카카오 API가 아니라 로컬 DB만 필터링한다.
        // TextWatcher는 글자가 바뀔 때마다 refreshList()를 호출한다.
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                refreshList(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 키보드의 검색 버튼(IME_ACTION_SEARCH)을 누르면 외부 검색 화면으로 이동한다.
        // 이때 검색어를 Intent extra로 넘겨 SearchActivity가 바로 카카오 검색을 실행하게 한다.
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
        // API 키가 없으면 외부 표지 보강은 건너뛴다. 로컬 DB 기능은 계속 동작한다.
        if (!KakaoApi.hasKey()) return;
        DBHelper db = DBHelper.get(this);
        // cover가 URL이 아닌 시드 책만 골라서 카카오에서 실제 표지를 찾아본다.
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
                // DB에 표지 정보가 갱신되었으므로 현재 검색어 기준으로 목록을 다시 그린다.
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
        // 다른 화면에서 돌아왔을 때 리뷰 수나 표지 정보가 바뀌었을 수 있으므로 다시 조회한다.
        refreshList(etSearch.getText().toString().trim());
    }

    private void refreshList(String query) {
        // 검색어가 비어 있으면 인기 도서, 검색어가 있으면 로컬 DB 제목 LIKE 검색 결과를 보여준다.
        List<Book> books = TextUtils.isEmpty(query)
                ? DBHelper.get(this).getPopularBooks()
                : DBHelper.get(this).searchBooks(query);
        adapter.setBooks(books);
    }
}
