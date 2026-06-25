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
        // local.properties에서 build.gradle을 통해 BuildConfig.KAKAO_REST_KEY로 들어온 값을 확인한다.
        return BuildConfig.KAKAO_REST_KEY != null && !BuildConfig.KAKAO_REST_KEY.isEmpty();
    }

    // Retrofit 같은 라이브러리 없이 HttpURLConnection으로 카카오 REST API를 직접 호출한다.
    // API 키는 Authorization 헤더에 "KakaoAK ..." 형식으로 넣고, 받은 JSON을 org.json으로 파싱한다.
    /** 카카오 책 검색. 네트워크 스레드에서 호출할 것. 실패 시 null. */
    public static List<Book> search(String query) {
        HttpURLConnection conn = null;
        try {
            // 검색어를 URL 인코딩해서 카카오 도서 검색 API 주소를 만든다.
            String url = ENDPOINT + "?size=20&query=" + URLEncoder.encode(query, "UTF-8");
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            // 카카오 REST API 키는 Authorization 헤더에 KakaoAK 형식으로 넣는다.
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

            // JSON 문자열을 앱에서 쓰는 Book 객체 리스트로 변환한다.
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
            // 카카오 API의 documents 배열 한 칸을 Book 객체 한 개로 옮긴다.
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
