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
 * check). Issue #317 F3: there is NO built-in key — the old fallback was a
 * string literal shipped in the APK, so anyone could forge a "valid"
 * signature offline. Where no KeyStore exists at all, signing falls back
 * to a random per-process software key (generated at first use, never
 * shipped, never persisted): a valid signature then proves consistency
 * only within the signing process, and signatures made under it fail
 * closed after process death instead of verifying forever under a
 * public key. (The AES-GCM backup export that used to live here was
 * killed Oct 29, 2026: its payload had no egress and no restore -
 * theatre, not a backup. Critics Phase-3, caller-verified.)
 */
object CryptoIntegrityManager {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val HMAC_KEY_ALIAS = "monolith_hmac_timer_guard_v1"
    private const val HMAC_ALGO = "HmacSHA256"

    // Issue #317 F3: the fallback key is RANDOM PER PROCESS — generated
    // at first use, held only in memory, never shipped in the APK and
    // never persisted. The previous literal string key let anyone with
    // the APK forge signatures; this one is unknowable outside the
    // process, and its signatures die with the process (fail-closed).
    private val processFallbackKey: SecretKey by lazy {
        val bytes = ByteArray(32)
        java.security.SecureRandom().nextBytes(bytes)
        SecretKeySpec(bytes, HMAC_ALGO)
    }

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
                cachedHardwareKey = existing.secretKey
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

    // Audit R6 #341 F53: the key is cached after first load. Before
    // this, every progress tick (1s ViewModel + 2s shield) re-ran
    // KeyStore.getInstance + load(null) + getEntry — ~5,400 store
    // round-trips per session-hour to re-prove a signature over
    // four values that cannot change mid-session. A key deleted
    // behind our back now fails verification conservatively (the
    // cached key no longer matches a re-signed state) instead of
    // being silently re-minted — the R1 #36 law still holds.
    @Volatile
    private var cachedHardwareKey: SecretKey? = null

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
        } catch (_: Exception) {
            false
        }
    }

    /** VERIFY path: lookup ONLY — returns the existing key or null, never creates one. */
    private fun getExistingHardwareHmacKeyOrNull(): SecretKey? {
        cachedHardwareKey?.let { return it }
        return try {
            val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            val key = (ks.getEntry(HMAC_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
            if (key != null) cachedHardwareKey = key
            key
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
                    mac.init(processFallbackKey)
                }
            } else {
                mac.init(processFallbackKey)
            }
            val sig = mac.doFinal(payloadBytes)
            Base64.encodeToString(sig, Base64.NO_WRAP)
        } catch (_: Exception) {
            val mac = Mac.getInstance(HMAC_ALGO)
            mac.init(processFallbackKey)
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
            else -> hmacSha256OrNull(processFallbackKey, payloadBytes) ?: return false
        }
        return MessageDigest.isEqual(expectedBytes, storedBytes)
    }
}
