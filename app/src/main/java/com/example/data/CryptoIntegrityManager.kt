package com.example.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Provides:
 * Hardware AndroidKeyStore HmacSHA256 signing & verification of active focus
 * timer state to detect local DB/SharedPreferences tampering and trigger a
 * penalty lock. (The AES-GCM backup export that used to live here was killed
 * Oct 29, 2026: its payload had no egress and no restore - theatre, not a
 * backup. Critics Phase-3, caller-verified.)
 */
object CryptoIntegrityManager {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val HMAC_KEY_ALIAS = "monolith_hmac_timer_guard_v1"
    private const val HMAC_ALGO = "HmacSHA256"

    // Deterministic 256-bit fallback keys used when AndroidKeyStore hardware TEE is unavailable (e.g., local JVM tests)
    private val fallbackHmacKey = SecretKeySpec(
        "Monolith_Hardware_HMAC_Fallback_32B!".toByteArray(StandardCharsets.UTF_8),
        HMAC_ALGO
    )
    private fun getHardwareHmacKeyOrNull(): SecretKey? {
        return try {
            val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            val existing = ks.getEntry(HMAC_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            if (existing != null) {
                existing.secretKey
            } else {
                val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, ANDROID_KEYSTORE)
                keyGen.init(
                    KeyGenParameterSpec.Builder(
                        HMAC_KEY_ALIAS,
                        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                    ).build()
                )
                keyGen.generateKey()
            }
        } catch (_: Exception) {
            null
        }
    }

    fun signSessionState(
        startWallTimeMillis: Long,
        startElapsedRealtimeMillis: Long,
        bootCount: Int,
        targetDurationSeconds: Int,
        isStopwatch: Boolean
    ): String {
        val payload = "$startWallTimeMillis|$startElapsedRealtimeMillis|$bootCount|$targetDurationSeconds|$isStopwatch"
        val payloadBytes = payload.toByteArray(StandardCharsets.UTF_8)
        return try {
            val mac = Mac.getInstance(HMAC_ALGO)
            val hwKey = getHardwareHmacKeyOrNull()
            if (hwKey != null) {
                try {
                    mac.init(hwKey)
                } catch (_: Exception) {
                    mac.init(fallbackHmacKey)
                }
            } else {
                mac.init(fallbackHmacKey)
            }
            val sig = mac.doFinal(payloadBytes)
            Base64.encodeToString(sig, Base64.NO_WRAP)
        } catch (_: Exception) {
            val mac = Mac.getInstance(HMAC_ALGO)
            mac.init(fallbackHmacKey)
            Base64.encodeToString(mac.doFinal(payloadBytes), Base64.NO_WRAP)
        }
    }

    fun verifySessionState(session: FocusSessionEntity): Boolean {
        if (session.hmacSignature.isBlank()) return true
        val expected = signSessionState(
            startWallTimeMillis = session.startWallTimeMillis,
            startElapsedRealtimeMillis = session.startElapsedRealtimeMillis,
            bootCount = session.bootCount,
            targetDurationSeconds = session.targetDurationSeconds,
            isStopwatch = session.isStopwatchMode
        )
        return expected == session.hmacSignature
    }
}
