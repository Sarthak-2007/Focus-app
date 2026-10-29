package com.example.data

/**
 * THE SEAM for the editable durations (PILL-AND-TIMES §3).
 *
 * One small provider is the only read-point for the peek's length
 * and the break's length, so Wagen's Truth Car 3 columns drop in
 * at merge without any consumer rewriting:
 *
 * - Truth Car 3 adds `peekMinutes` / `breakMinutes` to
 *   GuardSettingsEntity (migration 11→12) and stamps
 *   `breakSeconds` onto the session row at creation.
 * - When those columns land, THIS class's two functions read them
 *   (peekMinutes × 60_000 / breakMinutes × 60 off the settings
 *   row, clamped to §3.5's bounds: peek 0–30, break 0–60) and the
 *   constants below retire to being the migration defaults'
 *   ancestors (§3.6: defaults 2 and 10 — today, bit-identical).
 *
 * Until then the shipped constants are the source, which is the
 * truth of the tree as it stands: no surface may hardcode the
 * peek's 2 or the break's 10 anywhere else (§4.3's law) — they
 * read through here.
 *
 * The mid-flight law (§3.4) is structural: this provider is read
 * at grant time and at session-creation time ONLY. No enforcer
 * and no derivation reads it — a live lease holds an absolute
 * end already written, a running break holds its row's stamp,
 * so an edit prices the future and can never re-price the
 * present.
 */
class EditableTimesProvider(
    @Suppress("unused") private val dao: MonolithDao
) {
    /** The peek lease's length at grant, in milliseconds. Today:
     *  the shipped 120 s constant. At the Car 3 merge: the
     *  settings row's peekMinutes, clamped 0–30 (§3.5); 0 means
     *  no lease is granted at all. */
    suspend fun peekDurationMillis(): Long = MonolithRepository.PEEK_DURATION_MILLIS

    /** The safe break's length at session creation, in seconds
     *  (the future stamp's source). Today: the shipped 600 s
     *  constant. At the Car 3 merge: the settings row's
     *  breakMinutes × 60, clamped 0–60 (§3.5). */
    suspend fun breakDurationSeconds(): Int = MonolithRepository.SAFE_BREAK_DURATION_SECONDS
}
