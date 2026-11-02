package com.example

import com.example.ui.strongroom.StrongroomCopy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Stage 9 (BUILD-SR-17) — the copy facade's shape: every deck key
 * exists exactly once, and the exit names stand in the deck's
 * ruled order (§1.9). The words themselves are pinned by the
 * resource dump diff (A1) filed with PR #859, not by this test.
 */
class StrongroomCopyTest {

    @Test
    fun facade_carries23DistinctKeys() {
        val keys = listOf(
            StrongroomCopy.DayShapeOrdinary, StrongroomCopy.DayShapeTest,
            StrongroomCopy.DayShapeSmall, StrongroomCopy.DayShapeRest,
            StrongroomCopy.Purpose, StrongroomCopy.CodesWaitTonight,
            StrongroomCopy.Clause, StrongroomCopy.Telling,
            StrongroomCopy.ExitCharger, StrongroomCopy.ExitPresetPin,
            StrongroomCopy.ExitTemporaryUnlock,
            StrongroomCopy.ExitDeactivationCooldown,
            StrongroomCopy.ExitTimeItself,
            StrongroomCopy.DayStandsComposed, StrongroomCopy.SealStands,
            StrongroomCopy.TakeIt, StrongroomCopy.LeaveIt,
            StrongroomCopy.OneMinute, StrongroomCopy.BracketWordsRecordOnly,
            StrongroomCopy.BreakStands, StrongroomCopy.Notification,
            StrongroomCopy.StandsFromNextDayStart,
            StrongroomCopy.EndDateIsTheExit
        )
        assertEquals(23, keys.size)
        assertEquals(23, keys.toSet().size)
    }

    @Test
    fun exitNames_standInTheRuledOrder() {
        assertEquals(
            listOf(
                StrongroomCopy.ExitCharger, StrongroomCopy.ExitPresetPin,
                StrongroomCopy.ExitTemporaryUnlock,
                StrongroomCopy.ExitDeactivationCooldown,
                StrongroomCopy.ExitTimeItself
            ),
            StrongroomCopy.ExitNamesInOrder
        )
        assertTrue(StrongroomCopy.ExitNamesInOrder.size == 5)
    }
}
