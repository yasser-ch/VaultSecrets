package com.example.vaultsecrets.security;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecureKeyStore {

    private static final String TAG        = "SecureKeyStore";
    private static final String PREFS_NAME = "vault_key_store";
    private static final String KEY_DATA   = "kms_key";
    private static final String KEY_EXPIRY = "kms_key_expiry";

    private EncryptedSharedPreferences prefs;

    public SecureKeyStore(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            prefs = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    context, PREFS_NAME, masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            Log.d(TAG, "SecureKeyStore initialisé");
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Erreur init SecureKeyStore", e);
            throw new RuntimeException("Impossible d'initialiser le stockage sécurisé", e);
        }
    }

    public void storeKey(byte[] keyBytes, long expiryTimestamp) {
        String b64 = Base64.encodeToString(keyBytes, Base64.DEFAULT);
        prefs.edit()
                .putString(KEY_DATA, b64)
                .putLong(KEY_EXPIRY, expiryTimestamp)
                .apply();
        Log.d(TAG, "Clé stockée, expiry=" + expiryTimestamp);
    }

    public byte[] getKey() {
        String b64 = prefs.getString(KEY_DATA, null);
        if (b64 == null) return null;
        return Base64.decode(b64, Base64.DEFAULT);
    }

    public boolean isExpired() {
        if (!prefs.contains(KEY_DATA)) return true;
        long expiry = prefs.getLong(KEY_EXPIRY, 0);
        return System.currentTimeMillis() >= expiry;
    }

    public void clearKey() {
        prefs.edit().remove(KEY_DATA).remove(KEY_EXPIRY).apply();
        Log.d(TAG, "Clé supprimée");
    }
}