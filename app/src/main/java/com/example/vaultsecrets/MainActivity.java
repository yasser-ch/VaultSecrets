package com.example.vaultsecrets;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vaultsecrets.database.AppDatabase;
import com.example.vaultsecrets.database.entity.Device;
import com.example.vaultsecrets.database.entity.Order;
import com.example.vaultsecrets.database.entity.User;
import com.example.vaultsecrets.security.KeyManager;
import com.example.vaultsecrets.security.SecretDetector;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "VaultSecrets";

    private TextView   tvResult;
    private AppDatabase db;
    private KeyManager  keyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult   = findViewById(R.id.tvResult);
        db         = AppDatabase.getInstance(this);
        keyManager = new KeyManager(this);

        findViewById(R.id.btnTestDb).setOnClickListener(v -> testDatabase());
        findViewById(R.id.btnTestSql).setOnClickListener(v -> testSqlProtection());
        findViewById(R.id.btnGetKey).setOnClickListener(v -> testKms());
        findViewById(R.id.btnVerifyHmac).setOnClickListener(v -> testHmac());
        findViewById(R.id.btnAuditSecrets).setOnClickListener(v -> auditSecrets());
        findViewById(R.id.btnClearCache).setOnClickListener(v -> clearCache());
    }

    // ── Room + Migrations ─────────────────────────────────────────────────

    private void testDatabase() {
        show("Test Room en cours…");
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // User
                User user = new User("Yasser", "yasser@vault.com");
                long uid  = db.userDao().insert(user);

                // Device
                Device device = new Device((int) uid, "Pixel 8 Pro");
                db.deviceDao().insert(device);

                // Orders
                db.orderDao().insert(new Order((int) uid, System.currentTimeMillis(), 120.0));
                db.orderDao().insert(new Order((int) uid, System.currentTimeMillis(), 340.0));
                db.orderDao().insert(new Order((int) uid, System.currentTimeMillis(), 80.0));

                List<Order> all      = db.orderDao().getByUser((int) uid);
                List<Order> above150 = db.orderDao().getAboveAmount(150.0);
                List<Device> devices = db.deviceDao().getByUserId((int) uid);

                String result =
                        "✅ User #" + uid + " — " + user.getName() + "\n" +
                                "📱 Appareils : " + devices.size() + "\n" +
                                "🧾 Commandes total : " + all.size() + "\n" +
                                "💰 Commandes > 150€ : " + above150.size() + "\n\n" +
                                "Schéma DB v4 :\n" +
                                "  users (id, name, email)\n" +
                                "  devices (id, userId↗, model)\n" +
                                "  orders (id, userId, date, amount)\n" +
                                "  → 3 migrations appliquées";

                runOnUiThread(() -> show(result));
            } catch (Exception e) {
                runOnUiThread(() -> show("❌ Erreur DB : " + e.getMessage()));
            }
        });
    }

    // ── Anti-injection SQL ────────────────────────────────────────────────

    private void testSqlProtection() {
        show(
                "━━ Protection Anti-Injection SQL ━━\n\n" +
                        "❌ VULNÉRABLE (ne jamais faire) :\n" +
                        "  rawQuery(\"SELECT * FROM users WHERE id = \" + userId)\n" +
                        "  → Payload : \"1 OR 1=1\" retourne TOUT\n\n" +
                        "✅ SÉCURISÉ (paramètres) :\n" +
                        "  rawQuery(\"SELECT * FROM users WHERE id = ?\", new String[]{userId})\n" +
                        "  → Les paramètres sont échappés\n\n" +
                        "✅✅ MEILLEURE PRATIQUE (Room DAO) :\n" +
                        "  @Query(\"SELECT * FROM users WHERE id = :id\")\n" +
                        "  User getById(int id)\n" +
                        "  → Room génère le code sécurisé automatiquement\n\n" +
                        "Les index accélèrent les requêtes :\n" +
                        "  orders → index (userId, date)\n" +
                        "  orders → index (amount)"
        );
    }

    // ── KMS ───────────────────────────────────────────────────────────────

    private void testKms() {
        show("Récupération clé KMS…");
        Executors.newSingleThreadExecutor().execute(() -> {
            byte[] key    = keyManager.getKey();
            boolean fresh = keyManager.isCacheExpired();
            String b64    = Base64.encodeToString(key, Base64.DEFAULT).substring(0, 16) + "…";

            runOnUiThread(() -> show(
                    "✅ Clé KMS obtenue\n\n" +
                            "Longueur : " + key.length + " octets (AES-256)\n" +
                            "Aperçu   : " + b64 + "\n" +
                            "Cache    : " + (fresh ? "nouveau" : "en cache") + "\n" +
                            "Stockage : EncryptedSharedPreferences\n" +
                            "           (AES256-SIV keys / AES256-GCM values)\n\n" +
                            "En production → remplacer par appel Retrofit\n" +
                            "vers KMS réel avec mTLS"
            ));
        });
    }

    // ── HMAC ──────────────────────────────────────────────────────────────

    private void testHmac() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                byte[] key  = keyManager.getKey();
                byte[] data = "données sensibles à signer".getBytes();
                byte[] sig  = keyManager.hmac(data, key);
                boolean ok  = keyManager.verifyHmac(data, sig, key);

                byte[] tampered = "données modifiées".getBytes();
                boolean fail    = keyManager.verifyHmac(tampered, sig, key);

                runOnUiThread(() -> show(
                        "━━ Vérification HMAC-SHA256 ━━\n\n" +
                                "Données originales → " + (ok   ? "✅ Signature valide" : "❌") + "\n" +
                                "Données modifiées  → " + (!fail ? "✅ Falsification détectée" : "❌ Non détectée") + "\n\n" +
                                "Signature (16 premiers octets) :\n" +
                                Base64.encodeToString(sig, Base64.DEFAULT).substring(0, 24) + "…\n\n" +
                                "Comparaison en temps constant\n" +
                                "→ protège contre les attaques temporelles"
                ));
            } catch (Exception e) {
                runOnUiThread(() -> show("❌ Erreur HMAC : " + e.getMessage()));
            }
        });
    }

    // ── Audit secrets ─────────────────────────────────────────────────────

    private void auditSecrets() {
        String[] samples = {
                "AKIAIOSFODNN7EXAMPLE",
                "api_key=abc123def456ghi789",
                "-----BEGIN PRIVATE KEY-----",
                "https://api.example.com/v1",
                "user@example.com",
                "password=mysecretpassword123"
        };

        StringBuilder sb = new StringBuilder("━━ Audit Secrets Hardcodés ━━\n\n");
        for (String s : samples) {
            sb.append(SecretDetector.audit(
                    s.length() > 30 ? s.substring(0, 30) + "…" : s,
                    s)).append("\n");
        }
        sb.append("\n💡 En CI/CD : utiliser GitLeaks\n")
                .append("pour scanner automatiquement\n")
                .append("chaque commit et PR");

        show(sb.toString());
    }

    // ── Cache ─────────────────────────────────────────────────────────────

    private void clearCache() {
        keyManager.clearCache();
        show("🗑️ Cache de clés vidé\n\nProchaine récupération → nouveau fetch KMS");
    }

    // ── Utilitaire ────────────────────────────────────────────────────────

    private void show(String text) {
        tvResult.setText(text);
    }
}