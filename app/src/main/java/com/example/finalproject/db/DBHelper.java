package com.example.finalproject.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.finalproject.model.Book;
import com.example.finalproject.model.Review;
import com.example.finalproject.model.User;
import com.example.finalproject.util.Pw;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "booklog.db";
    private static final int DB_VERSION = 1;

    private static DBHelper instance;

    public static synchronized DBHelper get(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "pw_hash TEXT NOT NULL," +
                "nickname TEXT NOT NULL)");

        db.execSQL("CREATE TABLE books(" +
                "book_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "author TEXT," +
                "publisher TEXT," +
                "pub_year TEXT," +
                "genre TEXT," +
                "page_count INTEGER," +
                "cover TEXT," +
                "tagline TEXT," +
                "base_count INTEGER DEFAULT 0," +
                "base_sum REAL DEFAULT 0)");

        db.execSQL("CREATE TABLE reviews(" +
                "review_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "book_id INTEGER NOT NULL," +
                "user_id INTEGER DEFAULT 0," +
                "nickname TEXT," +
                "rating REAL," +
                "content TEXT," +
                "is_spoiler INTEGER DEFAULT 0," +
                "helpful_count INTEGER DEFAULT 0," +
                "created_at TEXT)");

        seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reviews");
        db.execSQL("DROP TABLE IF EXISTS books");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // ---------------- 시드 데이터 ----------------

    private void seed(SQLiteDatabase db) {
        // 데모 계정 (아이디: demo / 비번: demo)
        long demoId = insertUser(db, "demo", Pw.hash("demo"), "김준영");

        long bTrend = insertBook(db, "트렌드 코리아 2026", "김난도 외", "미래의창",
                "2025", "경제경영", 384, "cover_placeholder",
                "2026 흐름을 빠르게 파악하기 좋은 책.", 210, 4.3 * 210);
        long bDark = insertBook(db, "다크 심리학", "다크 사이드 프로젝트", "위즈덤하우스",
                "2024", "심리", 320, "cover_placeholder",
                "사람의 심리를 실전적으로 이해하기 좋다.", 76, 4.4 * 76);
        long bHonmono = insertBook(db, "혼모노", "성해나", "창비",
                "2025", "소설", 280, "cover_placeholder",
                "인물들의 감정선이 강렬하고 현실적이다.", 94, 4.5 * 94);
        long bGeupryu = insertBook(db, "급류", "정대건", "민음사",
                "2022", "소설", 240, "cover_placeholder",
                "잔잔한 문장 속 감정의 파도가 오래 남는다.", 128, 4.6 * 128);
        long bSonyeon = insertBook(db, "소년이 온다", "한강", "창비",
                "2014", "소설", 216, "cover_placeholder",
                "읽고 나서 쉽게 잊히지 않는 책이다.", 1240, 4.9 * 1240);

        // 소년이 온다 — 커뮤니티 리뷰
        insertReview(db, bSonyeon, 0, "책읽는사람", 5, "읽고 나서 쉽게 잊히지 않는 책이다.", 0, 42, "2026.05.12");
        insertReview(db, bSonyeon, 0, "문학소녀", 5, "역사를 감정으로 마주하게 만드는 강한 작품.", 0, 37, "2026.05.08");
        insertReview(db, bSonyeon, 0, "독서광", 4, "문장은 담담하지만 남는 울림은 크다.", 0, 29, "2026.05.03");
        insertReview(db, bSonyeon, 0, "끝까지읽음", 5, "마지막에 동호의 운명이 드러나는 장면에서 무너졌다.", 1, 15, "2026.05.01");

        // 혼모노 — 커뮤니티 리뷰
        insertReview(db, bHonmono, 0, "감상러", 5, "단편인데 여운이 길게 남는다.", 0, 21, "2026.05.09");
        insertReview(db, bHonmono, 0, "책벌레", 4, "현실적인 인물 묘사가 인상적.", 0, 12, "2026.05.04");

        // 트렌드 코리아 — 커뮤니티 리뷰
        insertReview(db, bTrend, 0, "직장인A", 4, "매년 챙겨보게 되는 책.", 0, 18, "2026.05.11");

        // 데모 사용자의 내 리뷰 (Figma '내 리뷰' 화면 재현)
        insertReview(db, bSonyeon, demoId, "김준영", 5, "읽는 내내 마음이 무거웠지만 꼭 읽어야 할 책.", 0, 0, "2026.05.18");
        insertReview(db, bHonmono, demoId, "김준영", 4, "인물들의 감정선이 강렬하고 현실적이다.", 0, 0, "2026.05.10");
    }

    private long insertUser(SQLiteDatabase db, String username, String pwHash, String nickname) {
        ContentValues v = new ContentValues();
        v.put("username", username);
        v.put("pw_hash", pwHash);
        v.put("nickname", nickname);
        return db.insert("users", null, v);
    }

    private long insertBook(SQLiteDatabase db, String title, String author, String publisher,
                            String year, String genre, int pages, String cover, String tagline,
                            int baseCount, double baseSum) {
        ContentValues v = new ContentValues();
        v.put("title", title);
        v.put("author", author);
        v.put("publisher", publisher);
        v.put("pub_year", year);
        v.put("genre", genre);
        v.put("page_count", pages);
        v.put("cover", cover);
        v.put("tagline", tagline);
        v.put("base_count", baseCount);
        v.put("base_sum", baseSum);
        return db.insert("books", null, v);
    }

    private void insertReview(SQLiteDatabase db, long bookId, long userId, String nick,
                              float rating, String content, int spoiler, int helpful, String date) {
        ContentValues v = new ContentValues();
        v.put("book_id", bookId);
        v.put("user_id", userId);
        v.put("nickname", nick);
        v.put("rating", rating);
        v.put("content", content);
        v.put("is_spoiler", spoiler);
        v.put("helpful_count", helpful);
        v.put("created_at", date);
        db.insert("reviews", null, v);
    }

    // ---------------- 사용자 ----------------

    public boolean usernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM users WHERE username=?", new String[]{username});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    /** @return 새 user_id, 이미 존재하면 -1 */
    public long signup(String username, String password, String nickname) {
        if (usernameExists(username)) return -1;
        return insertUser(getWritableDatabase(), username, Pw.hash(password), nickname);
    }

    /** 로그인 성공 시 User, 실패 시 null */
    public User login(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT user_id, username, nickname FROM users WHERE username=? AND pw_hash=?",
                new String[]{username, Pw.hash(password)});
        User u = null;
        if (c.moveToFirst()) {
            u = new User(c.getLong(0), c.getString(1), c.getString(2));
        }
        c.close();
        return u;
    }

    // ---------------- 책 ----------------

    private static final String BOOK_AGG_SELECT =
            "SELECT b.book_id, b.title, b.author, b.publisher, b.pub_year, b.genre, " +
            "b.page_count, b.cover, b.tagline, " +
            "(b.base_count + COUNT(r.review_id)) AS total_count, " +
            "(b.base_sum + COALESCE(SUM(r.rating),0)) AS total_sum " +
            "FROM books b LEFT JOIN reviews r ON r.book_id = b.book_id ";

    private Book mapBook(Cursor c) {
        Book b = new Book();
        b.id = c.getLong(0);
        b.title = c.getString(1);
        b.author = c.getString(2);
        b.publisher = c.getString(3);
        b.pubYear = c.getString(4);
        b.genre = c.getString(5);
        b.pageCount = c.getInt(6);
        b.cover = c.getString(7);
        b.tagline = c.getString(8);
        b.reviewCount = c.getInt(9);
        double sum = c.getDouble(10);
        b.avgRating = b.reviewCount > 0 ? sum / b.reviewCount : 0;
        return b;
    }

    public List<Book> getPopularBooks() {
        List<Book> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                BOOK_AGG_SELECT + "GROUP BY b.book_id ORDER BY total_count DESC", null);
        while (c.moveToNext()) list.add(mapBook(c));
        c.close();
        return list;
    }

    public List<Book> searchBooks(String query) {
        List<Book> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                BOOK_AGG_SELECT + "WHERE b.title LIKE ? GROUP BY b.book_id ORDER BY total_count DESC",
                new String[]{"%" + query + "%"});
        while (c.moveToNext()) list.add(mapBook(c));
        c.close();
        return list;
    }

    public Book getBook(long bookId) {
        Cursor c = getReadableDatabase().rawQuery(
                BOOK_AGG_SELECT + "WHERE b.book_id=? GROUP BY b.book_id",
                new String[]{String.valueOf(bookId)});
        Book b = c.moveToFirst() ? mapBook(c) : null;
        c.close();
        return b;
    }

    /** 카카오 검색 결과 등 외부 책을 저장하고 book_id 반환 (제목+저자 기준 중복 방지) */
    public long insertOrGetBook(String title, String author, String publisher,
                                String year, String genre, int pages, String cover, String tagline) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT book_id FROM books WHERE title=? AND author=?",
                new String[]{title, author});
        if (c.moveToFirst()) {
            long id = c.getLong(0);
            c.close();
            return id;
        }
        c.close();
        return insertBook(db, title, author, publisher, year, genre, pages, cover, tagline, 0, 0);
    }

    /** 아직 실제 표지(http)를 받지 못한 시드 책들 (id+title만 채움) */
    public List<Book> getBooksWithoutRealCover() {
        List<Book> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT book_id, title, author FROM books " +
                "WHERE cover IS NULL OR cover NOT LIKE 'http%'", null);
        while (c.moveToNext()) {
            Book b = new Book();
            b.id = c.getLong(0);
            b.title = c.getString(1);
            b.author = c.getString(2);
            list.add(b);
        }
        c.close();
        return list;
    }

    /** 카카오에서 받은 메타데이터로 책을 갱신 (빈 값은 기존 값 유지) */
    public void updateBookMeta(long bookId, String publisher, String year, String cover) {
        ContentValues v = new ContentValues();
        if (publisher != null && !publisher.isEmpty()) v.put("publisher", publisher);
        if (year != null && !year.isEmpty()) v.put("pub_year", year);
        if (cover != null && !cover.isEmpty()) v.put("cover", cover);
        if (v.size() == 0) return;
        getWritableDatabase().update("books", v, "book_id=?",
                new String[]{String.valueOf(bookId)});
    }

    // ---------------- 리뷰 ----------------

    public List<Review> getReviewsForBook(long bookId, boolean sortByRating) {
        String order = sortByRating ? "rating DESC, created_at DESC" : "created_at DESC";
        List<Review> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT review_id, book_id, user_id, nickname, rating, content, " +
                "is_spoiler, helpful_count, created_at FROM reviews WHERE book_id=? ORDER BY " + order,
                new String[]{String.valueOf(bookId)});
        while (c.moveToNext()) list.add(mapReview(c));
        c.close();
        return list;
    }

    public List<Review> getMyReviews(long userId) {
        List<Review> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT r.review_id, r.book_id, r.user_id, r.nickname, r.rating, r.content, " +
                "r.is_spoiler, r.helpful_count, r.created_at, b.title, b.cover " +
                "FROM reviews r JOIN books b ON b.book_id = r.book_id " +
                "WHERE r.user_id=? ORDER BY r.created_at DESC",
                new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            Review r = mapReview(c);
            r.bookTitle = c.getString(9);
            r.bookCover = c.getString(10);
            list.add(r);
        }
        c.close();
        return list;
    }

    public int getMyReviewCount(long userId) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM reviews WHERE user_id=?",
                new String[]{String.valueOf(userId)});
        int n = c.moveToFirst() ? c.getInt(0) : 0;
        c.close();
        return n;
    }

    public long insertReview(long bookId, long userId, String nickname, float rating,
                             String content, boolean spoiler, String date) {
        ContentValues v = new ContentValues();
        v.put("book_id", bookId);
        v.put("user_id", userId);
        v.put("nickname", nickname);
        v.put("rating", rating);
        v.put("content", content);
        v.put("is_spoiler", spoiler ? 1 : 0);
        v.put("helpful_count", 0);
        v.put("created_at", date);
        return getWritableDatabase().insert("reviews", null, v);
    }

    private Review mapReview(Cursor c) {
        Review r = new Review();
        r.id = c.getLong(0);
        r.bookId = c.getLong(1);
        r.userId = c.getLong(2);
        r.nickname = c.getString(3);
        r.rating = c.getFloat(4);
        r.content = c.getString(5);
        r.isSpoiler = c.getInt(6) == 1;
        r.helpfulCount = c.getInt(7);
        r.createdAt = c.getString(8);
        return r;
    }
}
