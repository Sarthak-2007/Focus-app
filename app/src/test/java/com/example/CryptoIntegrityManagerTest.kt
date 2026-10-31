package com.example

import android.util.Base64
import com.example.data.CryptoIntegrityManager
import com.example.data.FocusSessionEntity
import java.security.KeyStore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Audit R1 #36, finding 5 (Wave-3, issue-36 bundle): verify must be
 * constant-time and must never create a key. These tests pin the
 * cured behaviour of [CryptoIntegrityManager.verifySessionState]:
 * valid signature true, tampered / malformed / truncated signature
 * false, and a verify against a missing key returns false WITHOUT
 * minting the key it failed to find.
 *
 * Issue #36 build (the four #21 rulings, Oct 31 2026): the suite
 * now also pins the ruled state — blank FAILS CLOSED (ruling 1,
 * re-pinning the old parked-decision test below), no fallback key
 * exists so a keyless sign mints nothing (ruling 2), the v2
 * payload covers the four trusted time fields while pre-existing
 * v1 signatures keep verifying (ruling 3), and the hardware-backed
 * claim reads false wherever it cannot be verified (ruling 4).
 * The suite runs against the test-only AndroidKeyStore double
 * (FakeAndroidKeyStoreProvider, via TestMonolithApp): the
 * production keystore path is what executes here.
 *
 * PIN-SCOPE: SOURCE for the sign/verify contract tests in this
 * file (they pin the crypto source's own behaviour); the
 * environment tests at the bottom are PLUMBING.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CryptoIntegrityManagerTest {

    private fun sessionWith(signature: String, targetDurationSeconds: Int = 1800): FocusSessionEntity =
        FocusSessionEntity(
            intention = "Deep Work",
            profileName = "Study Loadout",
            targetDurationSeconds = targetDurationSeconds,
            elapsedSeconds = 10,
            startWallTimeMillis = 1_700_000_000_000L,
            startElapsedRealtimeMillis = 123_456L,
            bootCount = 3,
            hmacSignature = signature,
            isActive = true,
            completedSuccessfully = false
        )

    private fun signFor(targetDurationSeconds: Int = 1800): String =
        CryptoIntegrityManager.signSessionState(sessionWith("", targetDurationSeconds))

    private fun keyStoreHasHmacKey(): Boolean = try {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        ks.containsAlias("monolith_hmac_timer_guard_v1")
    } catch (_: Exception) {
        false
    }

    private fun deleteHmacKeyIfPresent() {
        try {
            val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            if (ks.containsAlias("monolith_hmac_timer_guard_v1")) {
                ks.deleteEntry("monolith_hmac_timer_guard_v1")
            }
        } catch (_: Exception) {
            // No working keystore in this environment: nothing to delete.
        }
    }

    @Test
    fun `valid signature verifies true`() {
        val session = sessionWith(signFor())
        assertTrue(
            "A signature produced by the sign path must verify",
            CryptoIntegrityManager.verifySessionState(session)
        )
    }

    @Test
    fun `tampered signed field fails verification`() {
        val session = sessionWith(signFor(), targetDurationSeconds = 60)
        assertFalse(
            "Changing a signed field without re-signing must fail verification",
            CryptoIntegrityManager.verifySessionState(session)
        )
    }

    @Test
    fun `malformed base64 signature fails verification`() {
        assertFalse(
            "A non-Base64 signature must fail, not throw",
            CryptoIntegrityManager.verifySessionState(sessionWith("!!!not-base64!!!"))
        )
        assertFalse(
            "Padding-only garbage must fail, not throw",
            CryptoIntegrityManager.verifySessionState(sessionWith("===="))
        )
    }

    @Test
    fun `well-formed but wrong signature fails verification`() {
        val zeros = Base64.encodeToString(ByteArray(32), Base64.NO_WRAP)
        assertFalse(
            "Well-formed Base64 of the wrong MAC bytes must fail",
            CryptoIntegrityManager.verifySessionState(sessionWith(zeros))
        )
    }

    @Test
    fun `truncated signature fails verification`() {
        // Issue #36: the stored form carries the "v2:" prefix —
        // the truncation cuts the MAC bytes beneath it, and the
        // truncated v2 signature is what must fail.
        val full = Base64.decode(signFor().removePrefix("v2:"), Base64.NO_WRAP)
        val truncated = "v2:" + Base64.encodeToString(full.copyOf(16), Base64.NO_WRAP)
        assertFalse(
            "A truncated MAC must fail the byte comparison",
            CryptoIntegrityManager.verifySessionState(sessionWith(truncated))
        )
    }

    @Test
    fun `verify with no existing key returns false and creates no key`() {
        deleteHmacKeyIfPresent()
        assertFalse("Precondition: no HMAC key present", keyStoreHasHmacKey())

        val forged = Base64.encodeToString(ByteArray(32) { (it + 1).toByte() }, Base64.NO_WRAP)
        assertFalse(
            "A non-blank signature cannot verify when no key exists",
            CryptoIntegrityManager.verifySessionState(sessionWith(forged))
        )
        assertFalse(
            "Verify must not mint the key it failed to find",
            keyStoreHasHmacKey()
        )
    }

    @Test
    fun `verify after key deletion returns false and does not resurrect the key`() {
        val signature = signFor()
        // Only meaningful where the sign path actually persisted a
        // keystore key. Under the test AndroidKeyStore double the
        // case is real (the sign path mints into the double), so
        // this test now RUNS in the suite instead of skipping; the
        // assumeTrue stays for any runtime where it cannot arise.
        assumeTrue("Sign path must have created a keystore key for this case", keyStoreHasHmacKey())
        assertTrue(
            "Precondition: the fresh signature verifies while its key exists",
            CryptoIntegrityManager.verifySessionState(sessionWith(signature))
        )

        deleteHmacKeyIfPresent()
        assertFalse(
            "Once the key is gone, its signatures must stop verifying",
            CryptoIntegrityManager.verifySessionState(sessionWith(signature))
        )
        assertFalse(
            "Verify must not resurrect a deleted key",
            keyStoreHasHmacKey()
        )
    }

    // ------------------------------------------------------------------
    // Audit R6 #340 F5/F48: the FINISH seal. A signature over the
    // finished receipt's own claims, chained over the previous
    // receipt's seal. Fail-closed: blank, malformed, edited-field
    // and broken-chain payloads never verify.
    // ------------------------------------------------------------------

    private fun signFinish(
        outcomeWord: String = "ENDED",
        sealedElapsedSeconds: Int = 1500,
        prevFinishSignature: String = ""
    ): String = CryptoIntegrityManager.signFinishState(
        sessionId = 7,
        receiptNumber = 48,
        outcomeWord = outcomeWord,
        armWallMs = 1_700_000_000_000L,
        endedAtWallMs = 1_700_001_500_000L,
        sealedElapsedSeconds = sealedElapsedSeconds,
        heldAppsAtSeal = 12,
        heldSitesAtSeal = 5,
        deflectionCount = 3,
        bankedBreakSeconds = 0,
        shieldGapSeen = false,
        breakTakenSeconds = 120,
        prevFinishSignature = prevFinishSignature
    )

    private fun verifyFinish(
        signature: String,
        outcomeWord: String = "ENDED",
        sealedElapsedSeconds: Int = 1500,
        prevFinishSignature: String = ""
    ): Boolean = CryptoIntegrityManager.verifyFinishState(
        sessionId = 7,
        receiptNumber = 48,
        outcomeWord = outcomeWord,
        armWallMs = 1_700_000_000_000L,
        endedAtWallMs = 1_700_001_500_000L,
        sealedElapsedSeconds = sealedElapsedSeconds,
        heldAppsAtSeal = 12,
        heldSitesAtSeal = 5,
        deflectionCount = 3,
        bankedBreakSeconds = 0,
        shieldGapSeen = false,
        breakTakenSeconds = 120,
        prevFinishSignature = prevFinishSignature,
        signature = signature
    )

    @Test
    fun `finish seal roundtrips`() {
        assertTrue(verifyFinish(signFinish()))
    }

    @Test
    fun `finish seal fails on an edited claim`() {
        val sig = signFinish()
        assertFalse(
            "An edited outcome word must break the finish seal",
            verifyFinish(sig, outcomeWord = "ATTENDED")
        )
        assertFalse(
            "An edited elapsed figure must break the finish seal",
            verifyFinish(sig, sealedElapsedSeconds = 1501)
        )
    }

    @Test
    fun `finish seal is chained - a swapped previous link breaks it`() {
        val first = signFinish(prevFinishSignature = "")
        val second = signFinish(prevFinishSignature = first)
        assertTrue(verifyFinish(second, prevFinishSignature = first))
        assertFalse(
            "Reordering the ledger (wrong previous link) must break the seal",
            verifyFinish(second, prevFinishSignature = "AAAA")
        )
    }

    @Test
    fun `finish seal fails closed on blank and sentinel signatures`() {
        assertFalse(verifyFinish(""))
        assertFalse(verifyFinish("UNSEALED"))
        assertFalse(verifyFinish("not-base64!!!"))
    }

    @Test
    fun `signature forged with the old shipped literal key fails verification`() {
        // Issue #317 F3 regression pin: the pre-fix fallback key was a
        // string literal in the APK ("Monolith_Hardware_HMAC_Fallback_32B!"),
        // so a signature made with it offline MUST NOT verify any more.
        val payload = "1700000000000|123456|3|1800|false"
        val mac = javax.crypto.Mac.getInstance("HmacSHA256")
        mac.init(
            javax.crypto.spec.SecretKeySpec(
                "Monolith_Hardware_HMAC_Fallback_32B!".toByteArray(Charsets.UTF_8),
                "HmacSHA256"
            )
        )
        val forged = Base64.encodeToString(mac.doFinal(payload.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
        assertFalse(
            "A signature forged with the formerly shipped literal key must fail",
            CryptoIntegrityManager.verifySessionState(sessionWith(forged))
        )
    }

    // PIN-SCOPE: SOURCE
    @Test
    fun `blank signature fails closed under ruling 1`() {
        // RULING-DRIVEN RE-PIN (issue #36, ruling 1 — MIGRATE, then
        // FAIL CLOSED): this test previously pinned blank = accepted
        // as the parked decision #21's standing semantics. The
        // ruling reverses it: unsigned rows are migrated by the
        // repository sweep, and a blank signature that reaches
        // verify is REJECTED. Blank never means valid again.
        assertFalse(
            "A blank signature must fail closed (ruling 1)",
            CryptoIntegrityManager.verifySessionState(sessionWith(""))
        )
    }

    // ------------------------------------------------------------------
    // Issue #36, rulings 2–4.
    // ------------------------------------------------------------------

    /** The keystore key of record, read from the (double) store —
     *  used to mint a genuine V1 signature the way pre-#36 code
     *  did, so the survival clause is exercised against a real
     *  legacy artefact, not a fabrication of the new code. */
    private fun keystoreKey(): javax.crypto.SecretKey {
        signFor() // the sign path mints the key if absent
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        return (ks.getEntry("monolith_hmac_timer_guard_v1", null)
            as KeyStore.SecretKeyEntry).secretKey
    }

    private fun macWithKeystoreKey(payload: String): String {
        val mac = javax.crypto.Mac.getInstance("HmacSHA256")
        mac.init(keystoreKey())
        return Base64.encodeToString(
            mac.doFinal(payload.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP
        )
    }

    // PIN-SCOPE: SOURCE
    @Test
    fun `new signatures carry the v2 version prefix`() {
        assertTrue(
            "Every newly minted session signature is a v2 signature (ruling 3)",
            signFor().startsWith("v2:")
        )
    }

    // PIN-SCOPE: SOURCE
    @Test
    fun `legacy v1 signature keeps verifying after the extension`() {
        // Ruling 3's survival clause: a signature minted under the
        // five-field v1 payload (the pre-#36 stored form, bare
        // Base64, no prefix) verifies against exactly what it
        // signed — records survive the versioned migration.
        val v1Payload = "1700000000000|123456|3|1800|false"
        val v1Signature = macWithKeystoreKey(v1Payload)
        assertTrue(
            "A genuine v1 signature must still verify (ruling 3 survival)",
            CryptoIntegrityManager.verifySessionState(sessionWith(v1Signature))
        )
        assertFalse(
            "A v1 signature still binds its five fields — an edited anchor must fail",
            CryptoIntegrityManager.verifySessionState(
                sessionWith(v1Signature, targetDurationSeconds = 60)
            )
        )
    }

    // PIN-SCOPE: SOURCE
    @Test
    fun `tampered time fields fail verification under v2`() {
        // Ruling 3's substance: each of the four trusted
        // elapsed-time fields is inside the v2 payload, so editing
        // any of them in the stored row breaks its signature.
        val signed = sessionWith(signFor())
        assertTrue(
            "Precondition: the untouched signed row verifies",
            CryptoIntegrityManager.verifySessionState(signed)
        )
        assertFalse(
            "elapsedSeconds is signed (ruling 3)",
            CryptoIntegrityManager.verifySessionState(signed.copy(elapsedSeconds = 11))
        )
        assertFalse(
            "accumulatedBeforeRebootSeconds is signed (ruling 3)",
            CryptoIntegrityManager.verifySessionState(
                signed.copy(accumulatedBeforeRebootSeconds = 5)
            )
        )
        assertFalse(
            "lastTickElapsedRealtimeMillis is signed (ruling 3)",
            CryptoIntegrityManager.verifySessionState(
                signed.copy(lastTickElapsedRealtimeMillis = 999L)
            )
        )
        assertFalse(
            "penaltySecondsAdded is signed (ruling 3)",
            CryptoIntegrityManager.verifySessionState(signed.copy(penaltySecondsAdded = 900))
        )
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun `with no keystore provider the sign path mints nothing and verify fails closed`() {
        // Ruling 2's fail-closed shape: there is NO fallback key,
        // so in a runtime with no AndroidKeyStore at all, signing
        // produces blank (nothing a substitute key sealed) and a
        // presented signature cannot verify.
        java.security.Security.removeProvider("AndroidKeyStore")
        try {
            assertTrue(
                "No keystore, no signature — sign mints nothing (ruling 2)",
                CryptoIntegrityManager.signSessionState(sessionWith("")).isBlank()
            )
            assertFalse(
                "No keystore, no verification (ruling 2)",
                CryptoIntegrityManager.verifySessionState(
                    sessionWith("v2:" + Base64.encodeToString(ByteArray(32), Base64.NO_WRAP))
                )
            )
        } finally {
            TestMonolithApp.installTestKeyStore()
        }
    }

    // PIN-SCOPE: PLUMBING
    @Test
    fun `hardware backing claim is dropped where it cannot be verified`() {
        // Ruling 4 (verify-or-drop): the test double deliberately
        // serves no KeyInfo, so the platform cannot verify secure
        // hardware custody here — the claim must read false, never
        // asserted on faith.
        signFor() // a key exists; custody is what cannot be proven
        assertFalse(
            "Where KeyInfo cannot verify TEE/StrongBox custody, the claim is dropped (ruling 4)",
            CryptoIntegrityManager.hardwareBackingVerified()
        )
    }

    // PIN-SCOPE: SOURCE
    @Test
    fun `finish seal fails once its key is gone`() {
        // The finish seal obeys ruling 2 like the arm signature:
        // keystore key or nothing. With the key deleted, a minted
        // seal stops verifying and verify does not resurrect it.
        val seal = signFinish()
        assertTrue("Precondition: the fresh seal verifies", verifyFinish(seal))
        deleteHmacKeyIfPresent()
        assertFalse(
            "A seal whose key is gone must not verify (ruling 2)",
            verifyFinish(seal)
        )
        assertFalse("Verify must not resurrect the key", keyStoreHasHmacKey())
    }
}
