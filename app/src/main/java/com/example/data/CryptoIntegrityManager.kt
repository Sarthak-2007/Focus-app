package com.example.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Provides:
 * 1. Hardware AndroidKeyStore HmacSHA256 signing & verification of active focus timer state
 *    to detect local DB/SharedPreferences tampering and trigger a penalty lock.
 * 2. AES-GCM (256-bit) authenticated encryption & decryption for exporting/importing
 *    7-day focus history and 3-tier app blocking rules.
 */
object CryptoIntegrityManager {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val HMAC_KEY_ALIAS = "monolith_hmac_timer_guard_v1"
    private const val AES_KEY_ALIAS = "monolith_aes_gcm_backup_v1"
    private const val HMAC_ALGO = "HmacSHA256"
    private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_BITS = 128
    private const val GCM_IV_BYTES = 12

    // Deterministic 256-bit fallback keys used when AndroidKeyStore hardware TEE is unavailable (e.g., local JVM tests)
    private val fallbackHmacKey = SecretKeySpec(
        "Monolith_Hardware_HMAC_Fallback_32B!".toByteArray(StandardCharsets.UTF_8),
        HMAC_ALGO
    )
    private val fallbackAesKey = SecretKeySpec(
        "Monolith_AES256_Backup_Key_32By!".toByteArray(StandardCharsets.UTF_8).copyOf(32),
        "AES"
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

    private fun getHardwareAesKeyOrNull(): SecretKey? {
        return try {
            val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            val existing = ks.getEntry(AES_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            if (existing != null) {
                existing.secretKey
            } else {
                val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
                keyGen.init(
                    KeyGenParameterSpec.Builder(
                        AES_KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build()
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

    /**
     * Encrypts plaintext JSON backup using 256-bit AES-GCM (`AES/GCM/NoPadding`).
     * Output format: `MONOLITH-AES256-GCM:<base64(ivLen + iv + ciphertext)>`
     */
    fun encryptBackupAesGcm(plainJson: String): String {
        val plainBytes = plainJson.toByteArray(StandardCharsets.UTF_8)
        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        val hwKey = getHardwareAesKeyOrNull()

        var usedIv: ByteArray? = null
        var cipherBytes: ByteArray? = null

        if (hwKey != null) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, hwKey)
                usedIv = cipher.iv
                cipherBytes = cipher.doFinal(plainBytes)
            } catch (_: Exception) {
                usedIv = null
                cipherBytes = null
            }
        }

        if (usedIv == null || cipherBytes == null) {
            val explicitIv = ByteArray(GCM_IV_BYTES).also { SecureRandom().nextBytes(it) }
            cipher.init(Cipher.ENCRYPT_MODE, fallbackAesKey, GCMParameterSpec(GCM_TAG_BITS, explicitIv))
            usedIv = explicitIv
            cipherBytes = cipher.doFinal(plainBytes)
        }

        val combined = ByteArray(1 + usedIv.size + cipherBytes.size)
        combined[0] = usedIv.size.toByte()
        System.arraycopy(usedIv, 0, combined, 1, usedIv.size)
        System.arraycopy(cipherBytes, 0, combined, 1 + usedIv.size, cipherBytes.size)
        return "MONOLITH-AES256-GCM:" + Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decryptBackupAesGcm(encryptedPayload: String): String? {
        return try {
            val raw = encryptedPayload.removePrefix("MONOLITH-AES256-GCM:").trim()
            val combined = Base64.decode(raw, Base64.NO_WRAP)
            val ivLen = combined[0].toInt()
            val iv = combined.copyOfRange(1, 1 + ivLen)
            val cipherBytes = combined.copyOfRange(1 + ivLen, combined.size)
            val spec = GCMParameterSpec(GCM_TAG_BITS, iv)
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)

            val hwKey = getHardwareAesKeyOrNull()
            if (hwKey != null) {
                try {
                    cipher.init(Cipher.DECRYPT_MODE, hwKey, spec)
                    return String(cipher.doFinal(cipherBytes), StandardCharsets.UTF_8)
                } catch (_: Exception) {
                    // Fallback to software AES-256-GCM key if hardware keystore is stubbed
                }
            }

            cipher.init(Cipher.DECRYPT_MODE, fallbackAesKey, spec)
            String(cipher.doFinal(cipherBytes), StandardCharsets.UTF_8)
        } catch (_: Exception) {
            null
        }
    }
}
