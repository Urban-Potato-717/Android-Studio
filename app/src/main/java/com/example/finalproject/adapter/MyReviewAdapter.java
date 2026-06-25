package com.example.finalproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.finalproject.R;
import com.example.finalproject.model.Review;
import com.example.finalproject.util.Covers;

import java.util.List;

public class MyReviewAdapter extends BaseAdapter {

    private final Context context;
    private List<Review> reviews;

    public MyReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        // 내 리뷰 목록을 다시 조회한 뒤 화면에 반영한다.
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return reviews == null ? 0 : reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return reviews.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // list_item_myreview.xml 하나가 내가 쓴 리뷰 카드 한 개가 된다.
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_myreview, parent, false);
        }
        Review r = reviews.get(position);

        ImageView ivCover = convertView.findViewById(R.id.ivCover);
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        RatingBar rbRating = convertView.findViewById(R.id.rbRating);
        TextView tvContent = convertView.findViewById(R.id.tvContent);
        TextView tvDate = convertView.findViewById(R.id.tvDate);

        // DBHelper.getMyReviews()에서 JOIN으로 가져온 책 제목/표지와 리뷰 내용을 함께 표시한다.
        Covers.load(ivCover, r.bookCover);
        tvTitle.setText(r.bookTitle);
        rbRating.setRating(r.rating);
        tvContent.setText(r.content);
        tvDate.setText(r.createdAt);

        return convertView;
    }
}
