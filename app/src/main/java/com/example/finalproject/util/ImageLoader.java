package com.example.finalproject.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** ListView 재활용을 고려한 아주 단순한 URL 이미지 로더 (외부 라이브러리 없이). */
public class ImageLoader {

    private static final ExecutorService POOL = Executors.newFixedThreadPool(3);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    // Glide·Picasso 없이 표지 URL을 직접 다운로드한다. ExecutorService로 받아 Handler로 화면에 적용한다.
    public static void load(ImageView view, String url) {
        // ListView는 화면 밖으로 나간 ImageView를 재사용한다. 그래서 요청 시점의 URL을 tag에 저장해 둔다.
        view.setTag(url);
        POOL.execute(() -> {
            Bitmap bmp = download(url);
            if (bmp == null) return;
            MAIN.post(() -> {
                // 다운로드가 끝났을 때 tag가 바뀌었으면(다른 책으로 재활용됨) 적용하지 않는다.
                // 스크롤 중 엉뚱한 책 표지가 잠깐 뜨는 문제를 막는 장치다.
                if (url.equals(view.getTag())) {
                    view.setImageBitmap(bmp);
                }
            });
        });
    }

    private static Bitmap download(String urlStr) {
        HttpURLConnection conn = null;
        try {
            // URL 이미지를 HTTP로 받아 Bitmap으로 변환한다.
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            InputStream is = conn.getInputStream();
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            return bmp;
        } catch (Exception e) {
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}
