package com.example.finalproject.model;

public class Book {
    public long id;
    public String title;
    public String author;
    public String publisher;
    public String pubYear;
    public String genre;
    public int pageCount;
    public String cover;       // drawable 리소스 이름 또는 이미지 URL
    public String tagline;     // 홈 대표 한줄평

    // 집계 값 (쿼리로 계산)
    public double avgRating;
    public int reviewCount;

    public Book() {}

    public boolean coverIsUrl() {
        return cover != null && cover.startsWith("http");
    }
}
