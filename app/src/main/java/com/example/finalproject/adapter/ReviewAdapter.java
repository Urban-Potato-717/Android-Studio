package com.example.finalproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.finalproject.R;
import com.example.finalproject.model.Review;

import java.util.List;

public class ReviewAdapter extends BaseAdapter {

    private final Context context;
    private List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        // DB에서 새로 가져온 리뷰 목록을 화면에 다시 그리라고 ListView에 알린다.
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
            // list_item_review.xml 하나를 리뷰 한 개의 화면으로 만들어 재사용한다.
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
        }
        Review r = reviews.get(position);

        TextView tvNickname = convertView.findViewById(R.id.tvNickname);
        RatingBar rbRating = convertView.findViewById(R.id.rbRating);
        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView tvContent = convertView.findViewById(R.id.tvContent);
        TextView tvHelpful = convertView.findViewById(R.id.tvHelpful);
        View groupContent = convertView.findViewById(R.id.groupContent);
        View groupSpoiler = convertView.findViewById(R.id.groupSpoiler);

        tvNickname.setText(r.nickname);
        rbRating.setRating(r.rating);
        tvDate.setText(r.createdAt);
        tvContent.setText(r.content);
        tvHelpful.setText(context.getString(R.string.helpful_format, r.helpfulCount));

        // isSpoiler는 DB에 저장된 값이고, revealed는 현재 화면에서만 쓰는 임시 공개 상태이다.
        // 스포일러인데 아직 공개하지 않았다면 실제 내용 영역을 숨기고 안내 영역을 보여준다.
        boolean blinded = r.isSpoiler && !r.revealed;
        if (blinded) {
            groupContent.setVisibility(View.GONE);
            groupSpoiler.setVisibility(View.VISIBLE);
            convertView.setOnClickListener(v -> showSpoilerDialog(r));
        } else {
            groupContent.setVisibility(View.VISIBLE);
            groupSpoiler.setVisibility(View.GONE);
            convertView.setOnClickListener(null);
            convertView.setClickable(false);
        }

        return convertView;
    }

    private void showSpoilerDialog(Review r) {
        // 사용자가 확인을 누른 경우에만 revealed를 true로 바꾸고 리스트를 다시 그린다.
        // revealed는 DB에 저장하지 않으므로 화면을 새로 열면 다시 가려진 상태가 된다.
        new AlertDialog.Builder(context)
                .setTitle(R.string.spoiler_dialog_title)
                .setMessage(R.string.spoiler_dialog_msg)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    r.revealed = true;
                    notifyDataSetChanged();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
