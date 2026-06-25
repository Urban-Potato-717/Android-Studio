package com.example.finalproject.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.finalproject.model.User;

//세션 클래스 - 로그인한 사용자 정보 저장 및 꺼내기
public class Session {
    private static final String PREF = "booklog_session";
    private static final String K_UID = "user_id";
    private static final String K_NAME = "username";
    private static final String K_NICK = "nickname";

    //로그인 한 정보를 SharedPreferences에 저장
    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    // 로그인 상태 유지: 로그인에 성공하면 사용자 정보를 SharedPreferences에 저장한다.
    // 그래서 앱을 껐다 켜도 로그인이 유지되고, isLoggedIn()으로 자동 로그인을 판단한다.
    public static void login(Context c, User u) {
        prefs(c).edit()
                .putLong(K_UID, u.id)
                .putString(K_NAME, u.username)
                .putString(K_NICK, u.nickname)
                .apply();
    }

    public static void logout(Context c) {
        prefs(c).edit().clear().apply();
    }

    //로그인은 boolean 값으로 사용 - 값이 -1이 아니면 로그인 된 상태 판단
    public static boolean isLoggedIn(Context c) {
        return prefs(c).getLong(K_UID, -1) != -1;
    }

    // 지금 로그인 한 사람 유지시키기 (ID 저장)
    public static long userId(Context c) {
        return prefs(c).getLong(K_UID, -1);
    }

    public static String nickname(Context c) {
        return prefs(c).getString(K_NICK, "");
    }

    public static String username(Context c) {
        return prefs(c).getString(K_NAME, "");
    }
}
