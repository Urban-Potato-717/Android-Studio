package com.example.finalproject.util;

import android.content.Context;
import android.widget.ImageView;

import com.example.finalproject.R;

public class Covers {

    /** cover 값이 drawable 이름이면 해당 리소스를, URL이면 비동기 로딩, 없으면 플레이스홀더 */
    public static void load(ImageView view, String cover) {
        Context ctx = view.getContext();
        if (cover != null && cover.startsWith("http")) {
            view.setImageResource(R.drawable.cover_placeholder);
            ImageLoader.load(view, cover);
            return;
        }
        int resId = 0;
        if (cover != null) {
            resId = ctx.getResources().getIdentifier(cover, "drawable", ctx.getPackageName());
        }
        view.setImageResource(resId != 0 ? resId : R.drawable.cover_placeholder);
    }
}
