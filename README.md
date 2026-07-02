# 🔑 VaultSecrets

Application Android de démonstration de la gestion sécurisée des secrets et de l'optimisation des bases de données Room — développée en Java avec Material Design 3.

---

## 📱 Aperçu

| Bouton | Description |
|---|---|
| 🗃️ Tester Room + Migrations | Insert User/Device/Orders, affiche les résultats des index |
| 🛡️ Demo Anti-Injection SQL | Compare requête vulnérable vs paramétrée vs Room DAO |
| 🔑 Récupérer clé KMS | Génère/met en cache une clé AES-256 dans EncryptedSharedPreferences |
| ✅ Vérifier HMAC | Signe des données et détecte la falsification |
| 🔍 Audit secrets hardcodés | Scanner des strings contre des patterns de secrets connus |
| 🗑️ Vider le cache | Efface la clé du cache chiffré |

---

## ✨ Fonctionnalités

- **Migrations Room** — évolution du schéma v1→v2→v3→v4 sans perte de données
- **Index DB** — `@Index` composite sur `(userId, date)` et `(amount)` pour accélérer les requêtes
- **DAO paramétrés** — requêtes Room avec `:paramètre` pour bloquer l'injection SQL
- **KMS simulé** — génération AES-256 + cache chiffré dans `EncryptedSharedPreferences`
- **HMAC-SHA256** — signature et vérification d'intégrité en temps constant (anti-timing attack)
- **SecretDetector** — regex patterns pour AWS keys, API keys, private keys, passwords
- **`allowBackup="false"`** — bloque l'extraction via `adb backup`

---

## 🗂️ Structure

```
app/src/main/java/com/example/vaultsecrets/
├── MainActivity.java
├── database/
│   ├── AppDatabase.java              # Room DB v4 + 3 migrations
│   ├── entity/
│   │   ├── User.java                 # Table users (id, name, email)
│   │   ├── Device.java               # Table devices (id, userId↗, model)
│   │   └── Order.java                # Table orders (id, userId, date, amount)
│   ├── dao/
│   │   ├── UserDao.java
│   │   ├── DeviceDao.java
│   │   └── OrderDao.java             # Requêtes paramétrées + index
│   └── migration/
│       └── Migrations.java           # MIGRATION_1_2, 2_3, 3_4
└── security/
    ├── SecureKeyStore.java           # EncryptedSharedPreferences AES256
    ├── KeyManager.java               # Cache KMS + HMAC-SHA256
    └── SecretDetector.java           # Audit regex secrets hardcodés
```

---

## 🛡️ Sécurité

| Risque | Contre-mesure | Implémentation |
|---|---|---|
| Secret hardcodé dans APK | Audit regex + config externe | `SecretDetector` + `assets/config.json` |
| Injection SQL | DAO paramétrés | `@Query("... WHERE id = :id")` |
| Clé en clair dans SharedPreferences | Chiffrement AES256-GCM | `EncryptedSharedPreferences` |
| Clé exposée en mémoire longtemps | Expiration 1h + cache vidable | `SecureKeyStore.isExpired()` |
| Falsification de données | HMAC-SHA256 | `KeyManager.verifyHmac()` |
| Timing attack sur HMAC | Comparaison temps constant | `diff |= a[i] ^ b[i]` |
| Extraction backup | `allowBackup="false"` | AndroidManifest.xml |

---

## 📊 Évolution du Schéma Room

```
v1 → users (id, name)
v2 → users (id, name, email)           ← ALTER TABLE ADD COLUMN
v3 → + devices (id, userId↗, model)    ← CREATE TABLE + INDEX
v4 → + orders (id, userId, date, amount) ← CREATE TABLE + 2 INDEX
```

---

## 🛠️ Stack

| Outil | Version |
|---|---|
| Java | 11 |
| Android SDK min | API 24 (Android 7.0) |
| `androidx.room` | 2.6.0 |
| `security-crypto` | 1.1.0-alpha06 |
| `retrofit2` | 2.9.0 |
| `okhttp3` | 4.11.0 |
| `material` | 1.10.0 |

---

## 🚀 Lancer le projet

```bash
git clone https://github.com/yasser-ch/VaultSecrets.git
```

Ouvrir dans Android Studio → **Run** sur émulateur ou appareil physique (API 24+).

---

## 📚 Contexte

TP réalisé dans le cadre du cursus **Génie Cyberdéfense & Télécommunications Embarquées** à l'ENSA Marrakech.  
Concepts abordés : migrations Room, index SQLite, injection SQL, KMS, EncryptedSharedPreferences, HMAC-SHA256, secrets hardcodés, GitLeaks.
