package com.example.finalproject.util;

import android.content.Context;
import android.widget.ImageView;

import com.example.finalproject.R;

public class Covers {

    /** cover 값이 drawable 이름이면 해당 리소스를, URL이면 비동기 로딩, 없으면 플레이스홀더 */
    public static void load(ImageView view, String cover) {
        Context ctx = view.getContext();
        if (cover != null && cover.startsWith("http")) {
            // 카카오 API에서 받은 표지는 URL이므로 일단 기본 이미지를 보여주고 백그라운드에서 다운로드한다.
            view.setImageResource(R.drawable.cover_placeholder);
            ImageLoader.load(view, cover);
            return;
        }
        int resId = 0;
        if (cover != null) {
            // 시드 데이터의 cover는 drawable 리소스 이름이므로 실제 리소스 id로 변환한다.
            resId = ctx.getResources().getIdentifier(cover, "drawable", ctx.getPackageName());
        }
        // 리소스를 찾지 못하면 깨진 이미지 대신 기본 플레이스홀더를 보여준다.
        view.setImageResource(resId != 0 ? resId : R.drawable.cover_placeholder);
    }
}
