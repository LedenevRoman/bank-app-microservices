package com.training.rledenev.client;

public class FeignClientJwtTokenHolder {

    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();

    public static void setToken(String token) {
        tokenHolder.set(token);
    }

    public static String getToken() {
        return tokenHolder.get();
    }

    public static void clear() {
        tokenHolder.remove();
    }

    private FeignClientJwtTokenHolder() {
    }
}
