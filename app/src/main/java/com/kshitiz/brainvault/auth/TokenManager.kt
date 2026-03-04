package com.kshitiz.brainvault.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {

    companion object {
        private const val KEY_TOKEN      = "jwt_token"
        private const val KEY_TOKEN_TIME = "jwt_saved_at"
        private const val TOKEN_EXPIRY_MS = 30 * 60 * 1000L  // 30 minutes in ms
        private const val PREFS_FILE      = "brainvault_secure_prefs"
    }

    // 👇 Encrypted prefs — replaces regular SharedPreferences
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)  // industry standard encryption
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,   // encrypts keys
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM  // encrypts values
        )
    }

    // Save token + record the exact time it was saved
    fun saveToken(token: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_TOKEN_TIME, System.currentTimeMillis())  // 👈 timestamp for expiry check
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun clearToken() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_TOKEN_TIME)
            .apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null && !isTokenExpired()

    // 👇 Check if 30 minutes have passed since token was saved
    fun isTokenExpired(): Boolean {
        val savedAt = prefs.getLong(KEY_TOKEN_TIME, 0L)
        if (savedAt == 0L) return true
        return (System.currentTimeMillis() - savedAt) >= TOKEN_EXPIRY_MS
    }
}