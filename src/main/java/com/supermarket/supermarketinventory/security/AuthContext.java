package com.supermarket.supermarketinventory.security;

public final class AuthContext {

    private static final ThreadLocal<JwtUserInfo> CURRENT_USER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void setCurrentUser(JwtUserInfo userInfo) {
        CURRENT_USER.set(userInfo);
    }

    public static JwtUserInfo getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
