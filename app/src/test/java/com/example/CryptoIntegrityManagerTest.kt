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
 * The blank-signature test at the bottom documents CURRENT semantics
 * only — whether an unsigned row is valid is parked decision #21
 * (finding 1), not settled by this suite.
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
        CryptoIntegrityManager.signSessionState(
            startWallTimeMillis = 1_700_000_000_000L,
            startElapsedRealtimeMillis = 123_456L,
            bootCount = 3,
            targetDurationSeconds = targetDurationSeconds,
            isStopwatch = false
        )

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
        val full = Base64.decode(signFor(), Base64.NO_WRAP)
        val truncated = Base64.encodeToString(full.copyOf(16), Base64.NO_WRAP)
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
        // keystore key; in a keystore-less runtime there is no key
        // whose deletion could be masked, so the case cannot arise.
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
    fun `blank signature keeps current accepted semantics pending decision 21`() {
        // Documents finding-1 behaviour as it stands today (blank =
        // accepted). The rule is Sarthak's parked decision #21; this
        // test pins that the finding-5 fix did not silently change it.
        assertTrue(
            "Blank signature follows the existing line-84 semantics",
            CryptoIntegrityManager.verifySessionState(sessionWith(""))
        )
    }
}
