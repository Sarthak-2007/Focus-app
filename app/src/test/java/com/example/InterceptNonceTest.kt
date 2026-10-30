package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.service.InterceptNonce
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Issue #68 F5, Tier 2 — the intercept provenance nonce:
 * minted inside the app, single-use, short-lived, and a wrong
 * guess must not burn the real token.
 */
@RunWith(RobolectricTestRunner::class)
class InterceptNonceTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun mintedNonceConsumesExactlyOnce() {
        val token = InterceptNonce.mint(context)
        assertTrue(InterceptNonce.consume(context, token))
        // Replay of the same captured token is refused.
        assertFalse(InterceptNonce.consume(context, token))
    }

    @Test
    fun wrongTokenIsRefusedAndDoesNotBurnTheRealOne() {
        val token = InterceptNonce.mint(context)
        assertFalse(InterceptNonce.consume(context, "forged-token"))
        assertFalse(InterceptNonce.consume(context, null))
        assertFalse(InterceptNonce.consume(context, ""))
        // The lawful intercept in flight still lands.
        assertTrue(InterceptNonce.consume(context, token))
    }

    @Test
    fun staleNonceIsRefusedAndBurned() {
        val token = InterceptNonce.mint(context)
        val later = System.currentTimeMillis() + InterceptNonce.MAX_AGE_MILLIS + 1_000L
        assertFalse(InterceptNonce.consume(context, token, later))
        // Burned by the match: even a fresh-clock replay fails.
        assertFalse(InterceptNonce.consume(context, token))
    }

    @Test
    fun noMintedNonceMeansNothingConsumes() {
        // Fresh store (previous test state is per-test sandboxed):
        // a token that was never minted is refused.
        assertFalse(InterceptNonce.consume(context, "never-minted"))
    }
}
