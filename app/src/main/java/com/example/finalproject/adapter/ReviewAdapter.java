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
