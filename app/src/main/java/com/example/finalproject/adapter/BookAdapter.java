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
