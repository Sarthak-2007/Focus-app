package com.example

import com.example.data.PreArmStore
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Issue #261: a fresh install's first start must commit to exactly
 * what the default preset's label promises. The birth dial/blocks
 * previously read 50 x 2 while the default preset was POMODORO_25_5
 * (25m Sprint) — 100 min of work under a 25m label. The defaults are
 * now derived from the preset itself; these tests pin that agreement
 * so the two can never drift apart again.
 */
class PreArmDefaultsIssue261Test {

    @Test
    fun dialDefaultComesFromTheDefaultPreset() {
        assertEquals(
            PreArmStore.DEFAULT_STUDY_PRESET.workMinutes,
            PreArmStore.DEFAULT_DIAL_MINUTES
        )
    }

    @Test
    fun blocksDefaultComesFromTheDefaultPreset() {
        assertEquals(
            PreArmStore.DEFAULT_STUDY_PRESET.defaultBlocks,
            PreArmStore.DEFAULT_STUDY_BLOCKS
        )
    }

    @Test
    fun firstStartSpanMatchesThePresetLabel() {
        // The span a first start commits to (work only; breaks are
        // priced separately) equals preset work x preset blocks.
        val committedWork = PreArmStore.DEFAULT_DIAL_MINUTES *
            PreArmStore.DEFAULT_STUDY_BLOCKS
        val promisedWork = PreArmStore.DEFAULT_STUDY_PRESET.workMinutes *
            PreArmStore.DEFAULT_STUDY_PRESET.defaultBlocks
        assertEquals(promisedWork, committedWork)
    }
}
