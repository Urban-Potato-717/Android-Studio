package com.example.finalproject.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.finalproject.model.User;

public class Session {
    private static final String PREF = "booklog_session";
    private static final String K_UID = "user_id";
    private static final String K_NAME = "username";
    private static final String K_NICK = "nickname";

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

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

    public static boolean isLoggedIn(Context c) {
        return prefs(c).getLong(K_UID, -1) != -1;
    }

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
