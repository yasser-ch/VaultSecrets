package com.example.vaultsecrets.security;

import android.content.Context;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyManager {

    private static final String TAG            = "KeyManager";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final SecureKeyStore keyStore;

    public KeyManager(Context context) {
        this.keyStore = new SecureKeyStore(context);
    }

    /**
     * Retourne la clé depuis le cache local.
     * Si expirée ou absente, génère une nouvelle clé simulée.
     */
    public byte[] getKey() {
        if (!keyStore.isExpired()) {
            byte[] cached = keyStore.getKey();
            if (cached != null) {
                Log.d(TAG, "Clé récupérée depuis le cache");
                return cached;
            }
        }
        return fetchAndCacheKey();
    }

    /**
     * Simule la récupération d'une clé depuis un KMS distant
     * et la met en cache chiffré localement.
     */
    private byte[] fetchAndCacheKey() {
        Log.d(TAG, "Génération d'une nouvelle clé (simulation KMS)");

        // Simulation : clé AES-256 aléatoire
        byte[] newKey = new byte[32];
        new SecureRandom().nextBytes(newKey);

        // Expiration dans 1 heure
        long expiry = System.currentTimeMillis() + 3_600_000L;
        keyStore.storeKey(newKey, expiry);

        return newKey;
    }

    public void clearCache() {
        keyStore.clearKey();
    }

    public boolean isCacheExpired() {
        return keyStore.isExpired();
    }



    public byte[] hmac(byte[] data, byte[] key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
        return mac.doFinal(data);
    }

    public boolean verifyHmac(byte[] data, byte[] expected, byte[] key) {
        try {
            byte[] computed = hmac(data, key);
            if (computed.length != expected.length) return false;
            int diff = 0;
            for (int i = 0; i < computed.length; i++)
                diff |= computed[i] ^ expected[i];
            return diff == 0;
        } catch (Exception e) {
            Log.e(TAG, "Erreur vérification HMAC", e);
            return false;
        }
    }
}