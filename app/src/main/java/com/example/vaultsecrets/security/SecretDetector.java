package com.example.vaultsecrets.security;

import android.util.Log;

import java.util.regex.Pattern;

public class SecretDetector {

    private static final String TAG = "SecretDetector";

    private static final Pattern[] PATTERNS = {
            Pattern.compile("AKIA[0-9A-Z]{16}"),
            Pattern.compile("(?i)(api[_-]?key)[ :='\"]([a-zA-Z0-9]{16,})"),
            Pattern.compile("-----BEGIN PRIVATE KEY-----"),
            Pattern.compile("(?i)password[ :='\"]([^\\s'\"]{8,})")
    };

    public static boolean containsSecret(String input) {
        if (input == null || input.isEmpty()) return false;
        for (Pattern p : PATTERNS) {
            if (p.matcher(input).find()) {
                Log.w(TAG, "Secret potentiel détecté — pattern : " + p.pattern());
                return true;
            }
        }
        return false;
    }

    public static String audit(String label, String value) {
        boolean found = containsSecret(value);
        return (found ? "🔴 SECRET DÉTECTÉ" : "🟢 OK") + " — " + label;
    }
}