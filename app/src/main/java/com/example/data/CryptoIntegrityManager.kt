package com.example.data

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey

/**
 * AndroidKeyStore HmacSHA256 signing & verification of focus timer
 * state, so local DB tampering with a session's trusted clock reads
 * as tamper and draws the penalty lock.
 *
 * The four #36 rulings (Sarthak, via #21, Oct 31 2026 — filed on #36
 * as 6544471900) are the law of this file:
 *
 *  1. FAIL CLOSED ON BLANK. A blank session signature NEVER
 *     verifies. Rows born unsigned under the old semantics are
 *     migrated by MonolithRepository's one-time sweep (they are
 *     signed over their standing fields at the first seed-sync /
 *     tick after this lands); from the migration on, blank is
 *     rejection, not acceptance.
 *  2. NO FALLBACK KEY EXISTS. There is no second key of any kind —
 *     no shipped literal (killed by #317/PR #426), no per-process
 *     random key (killed here). The only key is the AndroidKeyStore
 *     key. Where it cannot be obtained, signing mints NOTHING
 *     (returns blank) and verification returns false: fail closed,
 *     never silently healed by a substitute key.
 *  3. THE SIGNATURE COVERS THE TIME FIELDS. The v2 payload signs
 *     nine fields: the five v1 anchors plus the four trusted
 *     elapsed-time fields the tamper derivation reads
 *     (elapsedSeconds, accumulatedBeforeRebootSeconds,
 *     lastTickElapsedRealtimeMillis, penaltySecondsAdded). Stored
 *     v2 signatures carry a "v2:" prefix; pre-existing v1
 *     signatures (bare Base64) keep verifying against the v1
 *     payload, so records signed before this change survive
 *     exactly — and every re-write of a row upgrades it to v2,
 *     because every writer of a covered field re-signs the row it
 *     persists.
 *  4. HARDWARE-BACKED IS A VERIFIED CLAIM OR NO CLAIM. Minting
 *     asks for StrongBox first where the silicon offers it;
 *     [hardwareBackingVerified] reads the key's actual security
 *     level (KeyInfo) and returns true only where the platform
 *     reports TEE or StrongBox custody. Where that read is
 *     impossible, the claim is dropped (false), never asserted.
 *
 * Verification NEVER creates a key (audit R1 #36 finding 5): a
 * read-only check that mints a key silently masks key deletion.
 */
object CryptoIntegrityManager {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val HMAC_KEY_ALIAS = "monolith_hmac_timer_guard_v1"
    private const val HMAC_ALGO = "HmacSHA256"

    /** Stored-form prefix marking a v2 (nine-field) session
     *  signature. Base64 never contains ':', so the prefix is
     *  unambiguous against every legacy bare-Base64 signature. */
    private const val V2_PREFIX = "v2:"

    // Audit R6 #341 F53: the key is cached after first load, so a
    // progress tick does not re-run the full KeyStore load per
    // second. Issue #36 keeps the saving and closes its hole: the
    // verify path probes the store for the alias's existence
    // before trusting the cache, so a key deleted behind our back
    // surfaces as tamper at once (the finding-5 law) instead of
    // verifying forever against a ghost — one existence probe
    // replaces F53's full getEntry round-trip. The cache is
    // consulted on BOTH paths; the sign path checks it before
    // touching the store (the pre-#36 sign path reloaded the
    // store on every call).
    @Volatile
    private var cachedHardwareKey: SecretKey? = null

    /**
     * SIGN path only: returns the AndroidKeyStore key, CREATING and
     * persisting it when absent. Verification must never call this.
     *
     * Ruling 4: the mint asks for StrongBox custody first; where
     * the device refuses (no StrongBox, or StrongBox cannot hold
     * this key), it retries as a plain keystore mint. Either way
     * the key lives in the AndroidKeyStore — the backing level is
     * a claim [hardwareBackingVerified] reads back, never a
     * substitute key.
     *
     * Returns null wherever no keystore key can be obtained at
     * all; the caller fails closed (ruling 2 — no fallback key).
     */
    /** The cached key, but only while the store still holds the
     *  alias — a deleted key is a ghost, and neither path may sign
     *  or verify against a ghost (issue #36, finding 5). */
    private fun cachedKeyIfStillHeld(): SecretKey? {
        val cached = cachedHardwareKey ?: return null
        val stillHeld = try {
            val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            ks.containsAlias(HMAC_KEY_ALIAS)
        } catch (_: Exception) {
            false
        }
        if (stillHeld) return cached
        cachedHardwareKey = null
        return null
    }

    private fun getOrCreateHardwareHmacKeyOrNull(): SecretKey? {
        cachedKeyIfStillHeld()?.let { return it }
        return try {
            val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            val existing = ks.getEntry(HMAC_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            if (existing != null) {
                cachedHardwareKey = existing.secretKey
                existing.secretKey
            } else {
                val minted = mintHmacKey(strongBox = true) ?: mintHmacKey(strongBox = false)
                if (minted != null) cachedHardwareKey = minted
                minted
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun mintHmacKey(strongBox: Boolean): SecretKey? {
        return try {
            val keyGen = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_HMAC_SHA256, ANDROID_KEYSTORE
            )
            val spec = KeyGenParameterSpec.Builder(
                HMAC_KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
            // StrongBox exists only from API 28 (Build.VERSION_CODES.P); below it there is
            // no StrongBox silicon to ask for, so the request is skipped and the key mints
            // with TEE custody — the design's "StrongBox first where the silicon offers it".
            if (strongBox && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                spec.setIsStrongBoxBacked(true)
            }
            keyGen.init(spec.build())
            keyGen.generateKey()
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
     * wiped. Returns false wherever the keystore is unreachable —
     * absence of proof, never proof of a wipe.
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
        cachedKeyIfStillHeld()?.let { return it }
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
     * Ruling 4 (verify-or-drop): true ONLY where the platform
     * itself reports the signing key held in secure hardware —
     * TEE or StrongBox, read from the key's own KeyInfo. Lookup
     * only (never mints). Any failure to read — no key, no
     * KeyFactory, an opaque key — returns false: the
     * hardware-backed claim is dropped wherever it cannot be
     * verified, never asserted on faith.
     */
    fun hardwareBackingVerified(): Boolean {
        val key = getExistingHardwareHmacKeyOrNull() ?: return false
        return try {
            val factory = KeyFactory.getInstance(key.algorithm, ANDROID_KEYSTORE)
            val info = factory.getKeySpec(key, KeyInfo::class.java) ?: return false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                info.securityLevel == KeyProperties.SECURITY_LEVEL_TRUSTED_ENVIRONMENT ||
                    info.securityLevel == KeyProperties.SECURITY_LEVEL_STRONGBOX
            } else {
                @Suppress("DEPRECATION")
                info.isInsideSecureHardware
            }
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

    /** The v1 payload (five anchor fields) — VERIFY-ONLY since
     *  ruling 3: it exists so signatures minted before the
     *  extension keep verifying against exactly what they signed.
     *  Nothing new is ever signed under it. */
    private fun v1PayloadBytesFor(session: FocusSessionEntity): ByteArray {
        val payload = "${session.startWallTimeMillis}|${session.startElapsedRealtimeMillis}|" +
            "${session.bootCount}|${session.targetDurationSeconds}|${session.isStopwatchMode}"
        return payload.toByteArray(StandardCharsets.UTF_8)
    }

    /** The v2 payload: the v1 anchors PLUS the four trusted
     *  elapsed-time fields (ruling 3). The "S2" tag is part of the
     *  signed bytes, so a v1 signature can never be replayed as a
     *  v2 one even before the prefix is read. Field order is law —
     *  changing it invalidates every v2 signature already minted,
     *  exactly as a format change should. */
    private fun v2PayloadBytesFor(session: FocusSessionEntity): ByteArray {
        val payload = "S2|${session.startWallTimeMillis}|${session.startElapsedRealtimeMillis}|" +
            "${session.bootCount}|${session.targetDurationSeconds}|${session.isStopwatchMode}|" +
            "${session.elapsedSeconds}|${session.accumulatedBeforeRebootSeconds}|" +
            "${session.lastTickElapsedRealtimeMillis}|${session.penaltySecondsAdded}"
        return payload.toByteArray(StandardCharsets.UTF_8)
    }

    /**
     * Signs the session row's covered fields under the v2 payload.
     * The signature is computed over the row AS PASSED — every
     * writer builds its final row first and signs exactly what it
     * persists, so the stored signature is always the signature OF
     * the stored row. Returns blank where no keystore key can be
     * obtained (ruling 2: nothing is minted as a substitute, and a
     * blank signature fails verification closed until a working
     * keystore signs the row).
     */
    fun signSessionState(session: FocusSessionEntity): String {
        val key = getOrCreateHardwareHmacKeyOrNull() ?: return ""
        val sig = hmacSha256OrNull(key, v2PayloadBytesFor(session)) ?: return ""
        return V2_PREFIX + Base64.encodeToString(sig, Base64.NO_WRAP)
    }

    fun verifySessionState(session: FocusSessionEntity): Boolean {
        // Ruling 1: blank NEVER verifies. Unsigned legacy rows are
        // migrated by the repository sweep; an unsigned row that
        // reaches this function unsigned is rejected, full stop.
        if (session.hmacSignature.isBlank()) return false

        val isV2 = session.hmacSignature.startsWith(V2_PREFIX)
        val encoded = if (isV2) session.hmacSignature.substring(V2_PREFIX.length)
        else session.hmacSignature

        // Constant-time path: compare raw MAC bytes with
        // MessageDigest.isEqual, never String equality. A signature
        // that is not well-formed Base64 cannot verify.
        val storedBytes = try {
            Base64.decode(encoded, Base64.NO_WRAP)
        } catch (_: Exception) {
            return false
        }
        if (storedBytes.isEmpty()) return false

        val payloadBytes = if (isV2) v2PayloadBytesFor(session) else v1PayloadBytesFor(session)

        // Verify NEVER creates a key (finding 5) and there is no
        // fallback key to mirror (ruling 2): the keystore key of
        // record, lookup-only, or nothing verifies.
        val existingKey = getExistingHardwareHmacKeyOrNull() ?: return false
        val expectedBytes = hmacSha256OrNull(existingKey, payloadBytes) ?: return false
        return MessageDigest.isEqual(expectedBytes, storedBytes)
    }

    // ------------------------------------------------------------------
    // THE FINISH SEAL (Audit R6 #340 F5 + F48)
    //
    // The arm signature above covers the session's start anchors
    // and (v2) its trusted time fields; the finish seal is the
    // second signature the audit demanded: minted at publish time
    // over the finished slip's own claims, and CHAINED — the
    // payload embeds the previous receipt's finish seal, so the
    // ledger is one tamper-evident body: an edit, reorder or
    // deletion anywhere breaks every later verification loudly.
    //
    // Fail-closed law (the anti-Blockit rule): a finish seal that
    // does not verify marks the receipt FAULTED at read time. It
    // is never silently re-minted over edited fields. The seal
    // obeys ruling 2 like the arm signature: the keystore key or
    // nothing — where no key can be obtained, no seal is minted
    // (blank) and no seal verifies.
    // ------------------------------------------------------------------

    /** The canonical finish payload (v1). Field order is law —
     *  changing it invalidates every seal already minted, exactly
     *  as a format change should. */
    fun finishPayload(
        sessionId: Int,
        receiptNumber: Int,
        outcomeWord: String,
        armWallMs: Long,
        endedAtWallMs: Long,
        sealedElapsedSeconds: Int,
        heldAppsAtSeal: Int,
        heldSitesAtSeal: Int,
        deflectionCount: Int,
        bankedBreakSeconds: Int,
        shieldGapSeen: Boolean,
        breakTakenSeconds: Int,
        prevFinishSignature: String
    ): String = listOf(
        "F1",
        sessionId.toString(),
        receiptNumber.toString(),
        outcomeWord,
        armWallMs.toString(),
        endedAtWallMs.toString(),
        sealedElapsedSeconds.toString(),
        heldAppsAtSeal.toString(),
        heldSitesAtSeal.toString(),
        deflectionCount.toString(),
        bankedBreakSeconds.toString(),
        shieldGapSeen.toString(),
        breakTakenSeconds.toString(),
        prevFinishSignature
    ).joinToString("|")

    fun signFinishState(
        sessionId: Int,
        receiptNumber: Int,
        outcomeWord: String,
        armWallMs: Long,
        endedAtWallMs: Long,
        sealedElapsedSeconds: Int,
        heldAppsAtSeal: Int,
        heldSitesAtSeal: Int,
        deflectionCount: Int,
        bankedBreakSeconds: Int,
        shieldGapSeen: Boolean,
        breakTakenSeconds: Int,
        prevFinishSignature: String
    ): String {
        // Ruling 2: no keystore key, no seal — blank, never a
        // substitute key's seal.
        val key = getOrCreateHardwareHmacKeyOrNull() ?: return ""
        val payloadBytes = finishPayload(
            sessionId, receiptNumber, outcomeWord, armWallMs, endedAtWallMs,
            sealedElapsedSeconds, heldAppsAtSeal, heldSitesAtSeal,
            deflectionCount, bankedBreakSeconds, shieldGapSeen,
            breakTakenSeconds, prevFinishSignature
        ).toByteArray(StandardCharsets.UTF_8)
        val sig = hmacSha256OrNull(key, payloadBytes) ?: return ""
        return Base64.encodeToString(sig, Base64.NO_WRAP)
    }

    /** Strict: a blank or malformed finish signature NEVER
     *  verifies. Callers exempt pre-v13 legacy rows by checking
     *  blankness themselves before calling this. */
    fun verifyFinishState(
        sessionId: Int,
        receiptNumber: Int,
        outcomeWord: String,
        armWallMs: Long,
        endedAtWallMs: Long,
        sealedElapsedSeconds: Int,
        heldAppsAtSeal: Int,
        heldSitesAtSeal: Int,
        deflectionCount: Int,
        bankedBreakSeconds: Int,
        shieldGapSeen: Boolean,
        breakTakenSeconds: Int,
        prevFinishSignature: String,
        signature: String
    ): Boolean {
        if (signature.isBlank()) return false
        val storedBytes = try {
            Base64.decode(signature, Base64.NO_WRAP)
        } catch (_: Exception) {
            return false
        }
        if (storedBytes.isEmpty()) return false
        val payloadBytes = finishPayload(
            sessionId, receiptNumber, outcomeWord, armWallMs, endedAtWallMs,
            sealedElapsedSeconds, heldAppsAtSeal, heldSitesAtSeal,
            deflectionCount, bankedBreakSeconds, shieldGapSeen,
            breakTakenSeconds, prevFinishSignature
        ).toByteArray(StandardCharsets.UTF_8)
        // Lookup-only, no fallback (rulings 2 + finding 5).
        val existingKey = getExistingHardwareHmacKeyOrNull() ?: return false
        val expectedBytes = hmacSha256OrNull(existingKey, payloadBytes) ?: return false
        return MessageDigest.isEqual(expectedBytes, storedBytes)
    }
}
