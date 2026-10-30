package com.example.data

import android.content.Context

/**
 * The pre-arm configuration's memory (Issue #87, process-death F3).
 *
 * The five values a user sets before arming — dial minutes, study
 * blocks, preset, intention, destination — lived only in ViewModel
 * flows with hardcoded birth values. A process death in the
 * background (Android kills background processes routinely) silently
 * reset all five, and the user could seal a session with a duration
 * and an intention they never chose.
 *
 * They now rest on a stored fact, on the LoadoutStore pattern: a
 * small SharedPreferences store OUTSIDE Room — five scalars with no
 * schema business (a Room column/migration for them would be a
 * schema change this fact does not need). The ViewModel seeds its
 * flows from here at birth and its five setters write through.
 * Writes COMMIT (the G-02 standard LoadoutStore set): a deferred
 * write must not lose a value the setup screen has already shown
 * as resting.
 */
class PreArmStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun dialMinutes(): Int = prefs.getInt(KEY_DIAL_MINUTES, DEFAULT_DIAL_MINUTES)

    fun putDialMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_DIAL_MINUTES, minutes).commit()
    }

    fun studyBlocksCount(): Int = prefs.getInt(KEY_STUDY_BLOCKS, DEFAULT_STUDY_BLOCKS)

    fun putStudyBlocksCount(blocks: Int) {
        prefs.edit().putInt(KEY_STUDY_BLOCKS, blocks).commit()
    }

    /** The last hand-picked preset, or the birth default if none. */
    fun studyPreset(): StudyModePreset =
        prefs.getString(KEY_STUDY_PRESET, null)?.let { name ->
            runCatching { StudyModePreset.valueOf(name) }.getOrNull()
        } ?: DEFAULT_STUDY_PRESET

    fun putStudyPreset(preset: StudyModePreset) {
        prefs.edit().putString(KEY_STUDY_PRESET, preset.name).commit()
    }

    fun intentionText(): String =
        prefs.getString(KEY_INTENTION_TEXT, null) ?: DEFAULT_INTENTION_TEXT

    fun putIntentionText(text: String) {
        prefs.edit().putString(KEY_INTENTION_TEXT, text).commit()
    }

    /** The last destination's enum NAME (the enum itself lives in
     *  the UI layer; this store keeps the name and the ViewModel
     *  maps it back, so data never depends on ui). */
    fun destinationName(): String =
        prefs.getString(KEY_DESTINATION, null) ?: DEFAULT_DESTINATION_NAME

    fun putDestinationName(name: String) {
        prefs.edit().putString(KEY_DESTINATION, name).commit()
    }

    companion object {
        private const val PREFS_NAME = "monolith_prearm"
        private const val KEY_DIAL_MINUTES = "dial_minutes"
        private const val KEY_STUDY_BLOCKS = "study_blocks_count"
        private const val KEY_STUDY_PRESET = "study_preset"
        private const val KEY_INTENTION_TEXT = "intention_text"
        private const val KEY_DESTINATION = "destination"

        // Issue #261 (W3-B1 FORGE re-port of the DURCH-E verdict):
        // the birth defaults MUST agree with the default preset.
        // They previously read 50 x 2 while DEFAULT_STUDY_PRESET said
        // POMODORO_25_5 (25m Sprint), so a first start silently
        // committed to 100 min of work (110 min span) under a 25m
        // label. Single source of truth: the preset itself, exactly
        // as selectStudyPreset applies it. (Not const: an enum's
        // constructor values are not compile-time constants.)
        val DEFAULT_STUDY_PRESET: StudyModePreset = StudyModePreset.POMODORO_25_5
        val DEFAULT_DIAL_MINUTES: Int = DEFAULT_STUDY_PRESET.workMinutes
        val DEFAULT_STUDY_BLOCKS: Int = DEFAULT_STUDY_PRESET.defaultBlocks
        // Issue #124 overrode the old factory phrase ("Finish
        // Chapter 4 Problem Set"): the intention is the user's own
        // words or blank — so the store's default is blank, and a
        // fresh user is born with no name, exactly as #124 ruled.
        const val DEFAULT_INTENTION_TEXT = ""
        const val DEFAULT_DESTINATION_NAME = "DECK"
    }
}
