package com.example.finalproject.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.finalproject.HomeActivity;
import com.example.finalproject.MyPageActivity;
import com.example.finalproject.MyReviewActivity;
import com.example.finalproject.R;

public class BottomNav {

    public static final int HOME = 0;
    public static final int REVIEW = 1;
    public static final int MYPAGE = 2;

    public static void setup(Activity activity, int current) {
        // 각 화면에서 같은 include_bottom_nav.xml을 쓰고, 여기서 현재 탭 색상과 클릭 이동을 공통 처리한다.
        wire(activity, R.id.tabHome, R.id.iconHome, R.id.labelHome, current == HOME,
                current == HOME ? null : HomeActivity.class);
        wire(activity, R.id.tabReview, R.id.iconReview, R.id.labelReview, current == REVIEW,
                current == REVIEW ? null : MyReviewActivity.class);
        wire(activity, R.id.tabMypage, R.id.iconMypage, R.id.labelMypage, current == MYPAGE,
                current == MYPAGE ? null : MyPageActivity.class);
    }

    private static void wire(Activity a, int tabId, int iconId, int labelId,
                             boolean active, Class<?> target) {
        LinearLayout tab = a.findViewById(tabId);
        ImageView icon = a.findViewById(iconId);
        TextView label = a.findViewById(labelId);

        // 현재 화면에 해당하는 탭은 강조색, 나머지 탭은 보조색으로 표시한다.
        int color = ContextCompat.getColor(a, active ? R.color.brand_brown : R.color.text_sub);
        icon.setColorFilter(color);
        label.setTextColor(color);

        if (target != null) {
            tab.setOnClickListener(v -> {
                // 다른 탭을 누르면 해당 Activity로 이동하고 현재 Activity는 종료해서 탭 화면을 교체한다.
                Intent intent = new Intent(a, target);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                a.startActivity(intent);
                a.overridePendingTransition(0, 0);
                a.finish();
            });
        } else {
            tab.setOnClickListener(null);
        }
    }
}
