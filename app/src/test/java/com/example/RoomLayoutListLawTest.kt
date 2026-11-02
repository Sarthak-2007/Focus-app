package com.example

import com.example.ui.strongroom.layout.AntechamberSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * B-9 — THE ANTECHAMBER'S LIST LAW, exhausted at the slot seam.
 * Landing: layout tree, with Slice 2 (ui/strongroom/layout/
 * RoomLayout.kt — AntechamberSlot), beside BUILD-SR-7's
 * RoomLayoutLawTest.
 *
 * BUILD-SR-7's law test proves the ruled order by its filed
 * examples. This test closes the order's whole space:
 *
 *  1. EVERY PERMUTATION — of the three slots, exactly one
 *     arrangement is lawful: calling out first, the allowed
 *     things as signed second, the held pile behind its second
 *     step third. Any reordering a later slice attempts fails
 *     here by name, not by review.
 *  2. EVERY SUBSET — a shorter room is lawful exactly when it
 *     is a subsequence of the ruled order (an absent element is
 *     a room not yet furnished; a present one out of order is
 *     a broken law). Duplicates are never lawful: a slot that
 *     stands twice is an insertion.
 *  3. NO FOURTH SLOT — the census is three. B-9 limb 5 (if it
 *     is not on that list, it is not behind the door) and limb
 *     4 (spent things have no slot at all) are one fact: there
 *     is no entry a spent thing, a drum row, a crank, a break
 *     control, or a settings gear could ever occupy.
 *
 * The list's STATE law — the allowed words exact as signed,
 * spent things GONE as rendered state — has no model to assert
 * against until P5 lands (GAP-T1 on TEST-SR-2's map); those
 * tests stand written in tests-spec/S1. This file proves the
 * spatial half P5 must compose inside.
 */
class RoomLayoutListLawTest {

    private val ruled = AntechamberSlot.ruledOrder

    private fun permutations(items: List<AntechamberSlot>): List<List<AntechamberSlot>> =
        if (items.size <= 1) listOf(items)
        else items.flatMap { head ->
            permutations(items - head).map { tail -> listOf(head) + tail }
        }

    @Test
    fun everyPermutation_onlyTheRuledOrderIsLawful() {
        val all = permutations(AntechamberSlot.entries)
        assertEquals(6, all.size)
        all.forEach { arrangement ->
            assertEquals(
                "lawful only as ruled: $arrangement",
                arrangement == ruled,
                AntechamberSlot.isLawfulOrder(arrangement)
            )
        }
    }

    @Test
    fun everySubset_lawfulExactlyWhenASubsequenceOfTheRuledOrder() {
        // All 8 subsets, standing in ruled order: lawful.
        val slots = AntechamberSlot.entries
        for (mask in 0 until (1 shl slots.size)) {
            val subset = slots.filterIndexed { i, _ -> mask and (1 shl i) != 0 }
            assertTrue(
                "a subsequence of the ruled order is lawful: $subset",
                AntechamberSlot.isLawfulOrder(subset)
            )
            // The same subset reversed (when it has 2+ members)
            // is never lawful.
            if (subset.size >= 2) {
                assertFalse(
                    "a reversed subset is an insertion of disorder: $subset",
                    AntechamberSlot.isLawfulOrder(subset.reversed())
                )
            }
        }
        // Duplicates are insertions, at any position.
        assertFalse(AntechamberSlot.isLawfulOrder(listOf(slots[0], slots[0])))
        assertFalse(AntechamberSlot.isLawfulOrder(ruled + slots[0]))
        assertFalse(AntechamberSlot.isLawfulOrder(listOf(slots[0]) + ruled))
    }

    @Test
    fun census_isThreeSlots_andNoSpentSlotCanExist() {
        assertEquals(
            listOf("ALLOWED_AS_SIGNED", "CALLING_OUT", "HELD_PILE_SECOND_STEP"),
            AntechamberSlot.entries.map { it.name }.sorted()
        )
        assertEquals(listOf(1, 2, 3), ruled.map { it.rank })
        // Limb 4 + limb 5 as one census: nothing named for the
        // spent, the drum, the crank, the break, or a loosening
        // stands among the slots — there is nowhere to put one.
        val forbidden = listOf(
            "SPENT", "DRUM", "CRANK", "BREAK", "SETTING", "DIMMED", "GONE"
        )
        AntechamberSlot.entries.forEach { slot ->
            forbidden.forEach { token ->
                assertFalse(
                    "slot ${slot.name} must never be a '$token' slot (B-9)",
                    slot.name.contains(token)
                )
            }
        }
    }
}
