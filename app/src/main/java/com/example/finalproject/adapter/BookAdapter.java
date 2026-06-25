package com.example.finalproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.R;
import com.example.finalproject.model.Book;
import com.example.finalproject.util.Covers;

import java.util.List;
import java.util.Locale;

public class BookAdapter extends BaseAdapter {

    private final Context context;
    private List<Book> books;

    public BookAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        // HomeActivity/SearchActivity에서 새 책 목록을 넘기면 ListView를 다시 그린다.
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return books == null ? 0 : books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return books.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // list_item_book.xml 하나가 책 카드 한 개가 된다.
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_book, parent, false);
        }
        Book b = books.get(position);

        ImageView ivCover = convertView.findViewById(R.id.ivCover);
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvAuthor = convertView.findViewById(R.id.tvAuthor);
        TextView tvPublisher = convertView.findViewById(R.id.tvPublisher);
        TextView tvRating = convertView.findViewById(R.id.tvRating);
        TextView tvReviewCount = convertView.findViewById(R.id.tvReviewCount);
        TextView tvQuote = convertView.findViewById(R.id.tvQuote);

        // cover 값이 drawable 이름이든 URL이든 Covers.load()가 구분해서 ImageView에 넣는다.
        Covers.load(ivCover, b.cover);
        tvTitle.setText(b.title);
        tvAuthor.setText(b.author);
        tvPublisher.setText(b.publisher);
        tvRating.setText(String.format(Locale.KOREA, "%.1f", b.avgRating));
        tvReviewCount.setText(context.getString(R.string.review_count_format, b.reviewCount));
        if (b.tagline != null && !b.tagline.isEmpty()) {
            tvQuote.setVisibility(View.VISIBLE);
            tvQuote.setText("\"" + b.tagline + "\"");
        } else {
            tvQuote.setVisibility(View.GONE);
        }

        return convertView;
    }
}
