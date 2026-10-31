package com.example

import android.content.Context
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import com.example.data.ShieldRaiseStore
import com.example.data.unshieldedRebootMillis
import com.example.data.witnessedPostRebootMillis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * PIN scope: SOURCE — pins the shield-raise stamp's law and
 * the witnessed/unshielded split arithmetic the reboot
 * derivation prices from, at the store's own seam.
 *
 * Issue #525: the shield-raise stamp is the honest witness
 * for when a boot's shield first stood. Its law: the FIRST
 * flip of a boot wins (later demote/re-flip cycles never move
 * it), a stamp testifies only for its own boot, and a missing
 * stamp reads as no witness at all — never as a guess.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ShieldRaiseStoreTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        ShieldRaiseStore.clear(context)
    }

    private fun setMachineBootCount(count: Int) {
        Settings.Global.putInt(context.contentResolver, Settings.Global.BOOT_COUNT, count)
        assertEquals(
            "the test must control the machine boot count",
            count,
            Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
        )
    }

    /** The stamp's law: the FIRST flip of a boot wins, a stamp
     *  testifies only for its own boot, and the next boot's
     *  first flip starts a fresh stamp. */
    @Test
    fun shieldRaiseStampKeepsTheFirstFlipOfTheBoot() {
        setMachineBootCount(10)
        ShieldRaiseStore.stampFirstRaise(context, 111_000L)
        ShieldRaiseStore.stampFirstRaise(context, 222_000L)
        assertEquals(111_000L, ShieldRaiseStore.raiseMonoForBoot(context, 10))
        assertNull(ShieldRaiseStore.raiseMonoForBoot(context, 11))

        setMachineBootCount(11)
        ShieldRaiseStore.stampFirstRaise(context, 5_000L)
        assertEquals(5_000L, ShieldRaiseStore.raiseMonoForBoot(context, 11))
        assertNull(ShieldRaiseStore.raiseMonoForBoot(context, 10))
    }

    /** An unreadable boot count stamps nothing: unknown stays
     *  unknown, and no witness is ever fabricated. */
    @Test
    fun shieldRaiseStampWithNoBootCountStandsForNothing() {
        // Robolectric's default: BOOT_COUNT was never written.
        ShieldRaiseStore.stampFirstRaise(context, 111_000L)
        assertNull(ShieldRaiseStore.raiseMonoForBoot(context, 0))
        assertNull(ShieldRaiseStore.raiseMonoForBoot(context, 1))
    }

    /** The pure seam's arithmetic: witnessed + unshielded is
     *  exactly the uptime the old derivation gifted whole — the
     *  cure splits it, it does not invent or lose a second. */
    @Test
    fun witnessedSpanMathSplitsTheOldGiftedFigureExactly() {
        // No stamp: nothing witnessed, everything unshielded.
        assertEquals(0L, witnessedPostRebootMillis(7_200_000L, null))
        assertEquals(7_200_000L, unshieldedRebootMillis(7_200_000L, null))
        // Raise 8 s into the boot, tick at 20 s: 12 s witnessed,
        // 8 s unshielded — and the two sum to the old figure.
        assertEquals(12_000L, witnessedPostRebootMillis(20_000L, 8_000L))
        assertEquals(8_000L, unshieldedRebootMillis(20_000L, 8_000L))
        assertEquals(
            20_000L,
            witnessedPostRebootMillis(20_000L, 8_000L) +
                unshieldedRebootMillis(20_000L, 8_000L)
        )
        // A stamp ahead of the tick witnesses nothing.
        assertEquals(0L, witnessedPostRebootMillis(5_000L, 9_000L))
        assertEquals(5_000L, unshieldedRebootMillis(5_000L, 9_000L))
    }
}
