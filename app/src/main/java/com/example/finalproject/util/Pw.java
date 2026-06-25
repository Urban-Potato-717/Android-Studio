package com.example.finalproject.util;

import java.security.MessageDigest;

public class Pw {
    // 비밀번호를 평문 그대로 저장하지 않고 SHA-256 해시로 바꿔 저장한다.
    // 같은 비밀번호는 항상 같은 해시가 나오므로, 로그인 때도 해시끼리 비교해 일치 여부를 확인한다.
    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plain.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
