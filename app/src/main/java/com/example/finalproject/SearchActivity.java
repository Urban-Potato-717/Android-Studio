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

    // 네트워크 요청은 UI 스레드에서 실행할 수 없으므로 별도 스레드에서 카카오 API를 호출한다.
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // API 결과를 받은 뒤 ListView를 갱신하기 위해 메인(UI) 스레드로 돌아올 때 사용한다.
    private final Handler main = new Handler(Looper.getMainLooper());

    private BookAdapter adapter;
    private TextView tvStatus;
    // 카카오 검색 결과를 담는 리스트. BookAdapter가 이 리스트를 ListView에 표시한다.
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
            // 카카오 결과는 아직 우리 DB의 책이 아닐 수 있다.
            // insertOrGetBook()은 같은 제목+저자가 있으면 기존 book_id를 쓰고, 없으면 새로 저장한다.
            long bookId = DBHelper.get(this).insertOrGetBook(
                    b.title, b.author, b.publisher, b.pubYear, b.genre,
                    b.pageCount, b.cover, b.tagline);
            Intent intent = new Intent(this, BookDetailActivity.class);
            // 상세 화면은 book_id만 받으면 책 정보와 리뷰를 DB에서 다시 조회할 수 있다.
            intent.putExtra("book_id", bookId);
            startActivity(intent);
        });

        // 뒤로가기 버튼은 SearchActivity만 종료해서 이전 화면으로 돌아간다.
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 검색 화면 안에서도 키보드 검색 버튼을 누르면 새 검색어로 카카오 API를 다시 호출한다.
                doSearch(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });

        // HomeActivity에서 넘어온 검색어가 있으면 입력칸에 채우고 즉시 카카오 검색을 실행한다.
        String query = getIntent().getStringExtra("query");
        if (!TextUtils.isEmpty(query)) {
            etSearch.setText(query);
            etSearch.setSelection(query.length());
            doSearch(query);
        }
    }

    private void doSearch(String query) {
        if (TextUtils.isEmpty(query)) return;

        // local.properties에 API 키가 없으면 네트워크 검색을 실행하지 않고 안내 문구만 보여준다.
        if (!KakaoApi.hasKey()) {
            showStatus("카카오 API 키가 설정되지 않았습니다.\nlocal.properties에 KAKAO_REST_KEY를 입력하세요.");
            return;
        }

        showStatus("검색 중...");
        // 네트워크 요청은 UI 스레드에서 실행할 수 없다. ExecutorService로 백그라운드에서
        // 검색하고, 결과가 오면 Handler(main.post)로 UI 스레드에 돌아와 목록을 갱신한다.
        executor.execute(() -> {
            // 실제 HTTP 요청과 JSON 파싱은 KakaoApi.search()에서 처리한다.
            List<Book> list = KakaoApi.search(query);
            main.post(() -> {
                if (list == null) {
                    showStatus("검색에 실패했습니다. 네트워크나 API 키를 확인하세요.");
                } else if (list.isEmpty()) {
                    showStatus("검색 결과가 없습니다.");
                } else {
                    // 결과가 있으면 안내 문구를 숨기고 BookAdapter에 새 목록을 넘겨 화면을 갱신한다.
                    tvStatus.setVisibility(View.GONE);
                    adapter.setBooks(list);
                }
            });
        });
    }

    private void showStatus(String msg) {
        // 로딩, 오류, 결과 없음 상태에서는 기존 목록을 비우고 상태 문구만 보여준다.
        adapter.setBooks(new ArrayList<>());
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(msg);
    }
}
