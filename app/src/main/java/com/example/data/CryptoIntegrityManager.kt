package com.example.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Provides:
 * AndroidKeyStore HmacSHA256 signing & verification of active focus timer
 * state to detect local DB/SharedPreferences tampering and trigger a
 * penalty lock. Keys are REQUESTED from the AndroidKeyStore; hardware/TEE
 * backing is not verified (no StrongBox request, no KeyInfo security-level
 * check), and when the KeyStore is unavailable signing falls back to a
 * built-in software key — so a valid signature proves consistency with
 * whatever key signed, not hardware custody of that key. (The AES-GCM
 * backup export that used to live here was killed Oct 29, 2026: its
 * payload had no egress and no restore - theatre, not a backup. Critics
 * Phase-3, caller-verified.)
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

    /**
     * SIGN path only: returns the AndroidKeyStore key, CREATING and
     * persisting it when absent. Verification must never call this —
     * a read-only check that mints a key silently masks key deletion
     * or loss (audit R1 #36, finding 5).
     */
    private fun getOrCreateHardwareHmacKeyOrNull(): SecretKey? {
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

    /**
     * Whether the HMAC key already exists in AndroidKeyStore —
     * WITHOUT generating it (issue #81 F1). AndroidKeyStore entries
     * are held by the system per app UID: `pm clear` / Clear-data
     * does NOT delete them (uninstall and factory reset do). A
     * pre-existing key under a virgin database is therefore the
     * one residue that proves the install ran before its data was
     * wiped. Returns false wherever the keystore is unreachable
     * (JVM tests, TEE failure) — absence of proof, never proof of
     * a wipe.
     */
    fun hmacKeyExists(): Boolean {
        return try {
            val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            ks.containsAlias(HMAC_KEY_ALIAS)
    /** VERIFY path: lookup ONLY — returns the existing key or null, never creates one. */
    private fun getExistingHardwareHmacKeyOrNull(): SecretKey? {
        return try {
            val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            (ks.getEntry(HMAC_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
        } catch (_: Exception) {
            null
        }
    }

    /**
     * True when this environment COULD hold a hardware key: the
     * AndroidKeyStore loads and an HMAC KeyGenerator exists for it.
     * Distinguishes "keystore present, key absent" (a fact verify
     * must honour with `false`) from "no keystore exists here at
     * all" (plain JVM / keystore-less runtime, where the sign path
     * itself can only ever have used the fallback key, so verify
     * mirrors it — there is no key storage here to mutate or mask).
     */
    private fun canHoldHardwareKey(): Boolean {
        return try {
            KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, ANDROID_KEYSTORE)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun hmacSha256OrNull(key: SecretKey, payloadBytes: ByteArray): ByteArray? {
        return try {
            val mac = Mac.getInstance(HMAC_ALGO)
            mac.init(key)
            mac.doFinal(payloadBytes)
        } catch (_: Exception) {
            null
        }
    }

    private fun payloadBytesFor(
        startWallTimeMillis: Long,
        startElapsedRealtimeMillis: Long,
        bootCount: Int,
        targetDurationSeconds: Int,
        isStopwatch: Boolean
    ): ByteArray {
        val payload = "$startWallTimeMillis|$startElapsedRealtimeMillis|$bootCount|$targetDurationSeconds|$isStopwatch"
        return payload.toByteArray(StandardCharsets.UTF_8)
    }

    fun signSessionState(
        startWallTimeMillis: Long,
        startElapsedRealtimeMillis: Long,
        bootCount: Int,
        targetDurationSeconds: Int,
        isStopwatch: Boolean
    ): String {
        val payloadBytes = payloadBytesFor(
            startWallTimeMillis, startElapsedRealtimeMillis, bootCount, targetDurationSeconds, isStopwatch
        )
        return try {
            val mac = Mac.getInstance(HMAC_ALGO)
            val hwKey = getOrCreateHardwareHmacKeyOrNull()
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
        // Blank-signature semantics UNCHANGED (audit R1 #36 finding 1;
        // the rule itself is parked decision #21, not this fix).
        if (session.hmacSignature.isBlank()) return true

        // Constant-time path: compare raw MAC bytes with
        // MessageDigest.isEqual, never String equality. A signature
        // that is not well-formed Base64 cannot verify.
        val storedBytes = try {
            Base64.decode(session.hmacSignature, Base64.NO_WRAP)
        } catch (_: Exception) {
            return false
        }
        if (storedBytes.isEmpty()) return false

        val payloadBytes = payloadBytesFor(
            startWallTimeMillis = session.startWallTimeMillis,
            startElapsedRealtimeMillis = session.startElapsedRealtimeMillis,
            bootCount = session.bootCount,
            targetDurationSeconds = session.targetDurationSeconds,
            isStopwatch = session.isStopwatchMode
        )

        // Verify NEVER creates a key (finding 5): resolve the key of
        // record lookup-only. Where a keystore exists but holds no
        // key, a non-blank signature cannot verify — key deletion or
        // loss surfaces as tamper instead of being silently healed
        // by minting a fresh key. Only where no keystore exists at
        // all does verify mirror the sign path's fallback, since no
        // key storage exists there to protect.
        val existingKey = getExistingHardwareHmacKeyOrNull()
        val expectedBytes = when {
            existingKey != null -> hmacSha256OrNull(existingKey, payloadBytes) ?: return false
            canHoldHardwareKey() -> return false
            else -> hmacSha256OrNull(fallbackHmacKey, payloadBytes) ?: return false
        }
        return MessageDigest.isEqual(expectedBytes, storedBytes)
    }
}
