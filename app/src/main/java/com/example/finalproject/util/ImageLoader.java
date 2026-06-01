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

    public static void load(ImageView view, String url) {
        view.setTag(url);
        POOL.execute(() -> {
            Bitmap bmp = download(url);
            if (bmp == null) return;
            MAIN.post(() -> {
                // 재활용으로 다른 항목이 되었으면 적용하지 않음
                if (url.equals(view.getTag())) {
                    view.setImageBitmap(bmp);
                }
            });
        });
    }

    private static Bitmap download(String urlStr) {
        HttpURLConnection conn = null;
        try {
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
