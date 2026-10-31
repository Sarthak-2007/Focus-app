package com.example

import com.example.data.updateAbsorbBankSeconds
import com.example.data.updateLandedDuringSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Issue #524 (day-walk Step 9): the update absorb's pure law.
 * An app update mid-session kills the process but not the boot,
 * so monotonic elapsed runs through the install and the dead
 * interval booked as held time. The absorb — banked like a
 * reboot on the first tick that sees the landing — stands on
 * two pure decisions pinned here: WHEN it fires
 * ([updateLandedDuringSession]) and WHAT it banks
 * ([updateAbsorbBankSeconds]: the last proven elapsed, never
 * the through-gap figure).
 */
class UpdateAbsorbLawTest {

    private val startWall = 1_700_000_000_000L
    private val startMono = 400_000_000L

    @Test
    fun landingAfterSessionStart_fires() {
        assertTrue(
            updateLandedDuringSession(
                appLastUpdateMillis = startWall + 600_000L,
                startWallTimeMillis = startWall,
                startElapsedRealtimeMillis = startMono
            )
        )
    }

    @Test
    fun landingBeforeSessionStart_neverFires() {
        // The session started under the running build: its start
        // wall sits after the landing instant.
        assertFalse(
            updateLandedDuringSession(
                appLastUpdateMillis = startWall - 1L,
                startWallTimeMillis = startWall,
                startElapsedRealtimeMillis = startMono
            )
        )
        // Landing exactly AT the start wall is not after it.
        assertFalse(
            updateLandedDuringSession(
                appLastUpdateMillis = startWall,
                startWallTimeMillis = startWall,
                startElapsedRealtimeMillis = startMono
            )
        )
    }

    @Test
    fun unreadableLanding_neverFires() {
        // appLastUpdateMillis() reads 0 when the package info is
        // unreadable — an unknown landing proves nothing.
        assertFalse(
            updateLandedDuringSession(
                appLastUpdateMillis = 0L,
                startWallTimeMillis = startWall,
                startElapsedRealtimeMillis = startMono
            )
        )
    }

    @Test
    fun deadAnchors_neverFire() {
        // No live monotonic anchor (start mono 0): the row has
        // no witnessed span to protect.
        assertFalse(
            updateLandedDuringSession(
                appLastUpdateMillis = startWall + 600_000L,
                startWallTimeMillis = startWall,
                startElapsedRealtimeMillis = 0L
            )
        )
    }

    @Test
    fun absorbSelfExtinguishes_secondUpdateFiresAgain() {
        val landing = startWall + 600_000L
        // The absorb moves the start wall to its own tick, which
        // sits at/after the landing instant: the same update can
        // never fire the absorb twice.
        assertFalse(
            updateLandedDuringSession(
                appLastUpdateMillis = landing,
                startWallTimeMillis = landing + 5_000L,
                startElapsedRealtimeMillis = startMono + 605_000L
            )
        )
        // A SECOND update lands after the moved start wall: the
        // absorb fires for it, fresh.
        assertTrue(
            updateLandedDuringSession(
                appLastUpdateMillis = landing + 3_600_000L,
                startWallTimeMillis = landing + 5_000L,
                startElapsedRealtimeMillis = startMono + 605_000L
            )
        )
    }

    @Test
    fun bankIsTheLastProvenElapsed_neverTheThroughGapFigure() {
        // The reboot law's bank shape: accumulated or the last
        // persisted elapsed, whichever stands higher. The
        // through-gap monotonic figure is not an input — the
        // install interval between the last persisted tick and
        // the restart was witnessed by nothing and earns zero.
        assertEquals(900, updateAbsorbBankSeconds(900, 850))
        assertEquals(900, updateAbsorbBankSeconds(850, 900))
        assertEquals(0, updateAbsorbBankSeconds(0, 0))
    }
}
