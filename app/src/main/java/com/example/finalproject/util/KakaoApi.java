package com.example.finalproject.util;

import com.example.finalproject.BuildConfig;
import com.example.finalproject.model.Book;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class KakaoApi {

    private static final String ENDPOINT = "https://dapi.kakao.com/v3/search/book";

    public static boolean hasKey() {
        return BuildConfig.KAKAO_REST_KEY != null && !BuildConfig.KAKAO_REST_KEY.isEmpty();
    }

    /** 카카오 책 검색. 네트워크 스레드에서 호출할 것. 실패 시 null. */
    public static List<Book> search(String query) {
        HttpURLConnection conn = null;
        try {
            String url = ENDPOINT + "?size=20&query=" + URLEncoder.encode(query, "UTF-8");
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "KakaoAK " + BuildConfig.KAKAO_REST_KEY);
            conn.setConnectTimeout(7000);
            conn.setReadTimeout(7000);

            if (conn.getResponseCode() != 200) {
                return null;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            return parse(sb.toString());
        } catch (Exception e) {
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static List<Book> parse(String json) throws Exception {
        List<Book> list = new ArrayList<>();
        JSONObject root = new JSONObject(json);
        JSONArray docs = root.getJSONArray("documents");
        for (int i = 0; i < docs.length(); i++) {
            JSONObject d = docs.getJSONObject(i);
            Book b = new Book();
            b.title = d.optString("title");
            JSONArray authors = d.optJSONArray("authors");
            b.author = (authors != null && authors.length() > 0)
                    ? join(authors) : "저자 미상";
            b.publisher = d.optString("publisher");
            String datetime = d.optString("datetime");
            b.pubYear = datetime.length() >= 4 ? datetime.substring(0, 4) : "";
            b.genre = "도서";
            b.pageCount = 0;
            b.cover = d.optString("thumbnail");
            String contents = d.optString("contents");
            b.tagline = contents.length() > 60 ? contents.substring(0, 60) + "..." : contents;
            list.add(b);
        }
        return list;
    }

    private static String join(JSONArray arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(arr.optString(i));
        }
        return sb.toString();
    }
}
