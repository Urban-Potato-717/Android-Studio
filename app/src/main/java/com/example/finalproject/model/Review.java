package com.example.finalproject.model;

public class Review {
    public long id;
    public long bookId;
    public long userId;        // 0 = 시드(커뮤니티) 리뷰
    public String nickname;
    public float rating;
    public String content;
    public boolean isSpoiler;
    public int helpfulCount;
    public String createdAt;   // "2026.05.12"

    // 내 리뷰 목록에서 책 정보 함께 표시용
    public String bookTitle;
    public String bookCover;

    // 스포일러 펼침 상태 (런타임 전용)
    public boolean revealed;

    public Review() {}
}
