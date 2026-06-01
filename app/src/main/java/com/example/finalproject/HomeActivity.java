package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

public class HomeActivity extends AppCompatActivity {

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
