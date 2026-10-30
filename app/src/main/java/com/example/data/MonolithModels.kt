package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 3-Tier Per-App Protection Rules:
 * Every installed app can be set with a single tap between:
 * - LOCKED (Blocked during focus & strict schedules)
 * - PEEK_2M (2m Peek with mandatory 20-minute cooldown)
 * - OPEN (Always Allowed)
 */
enum class AppBlockTier(
    val label: String,
    val badge: String,
    val description: String,
    val displayLabel: String = label
) {
    LOCKED(
        label = "Shut",
        badge = "Blocked",
        description = "Blocked while a focus session or protection schedule is active",
        displayLabel = "Blocked"
    ),
    PEEK_2M(
        label = "2m Knock",
        badge = "2m Peek",
        description = "Allows a mindful 2-minute check-in for urgent messages + 20m cooldown",
        displayLabel = "2m Peek"
    ),
    OPEN(
        label = "Open",
        badge = "Open",
        description = "Always accessible for study tools, calls, and reference",
        displayLabel = "Open"
    )
}

enum class SmartLoadout(
    val title: String,
    val subtitle: String,
    val badge: String
) {
    STUDY(
        title = "Deep Study",
        subtitle = "Blocks Social & Games, sets YouTube & Messaging to 2m Peek, enables Shorts & Web shields.",
        badge = "Recommended"
    ),
    WORK_SPRINT(
        title = "Work Sprint",
        subtitle = "Blocks Short Video, Social Feeds, and Games while keeping Messaging and Docs accessible.",
        badge = " Balanced"
    ),
    SLEEP(
        title = "Full Lockdown",
        subtitle = "Blocks every distracting app except Phone, Clock, and Maps for total focus or wind-down.",
        badge = "Strict Mode"
    )
}

enum class StudyModePreset(
    val label: String,
    val workMinutes: Int,
    val breakMinutes: Int,
    val defaultBlocks: Int,
    val badge: String
) {
    STOPWATCH_FLOW("Open Flow", 5, 0, 1, "20m Min Floor"),
    POMODORO_25_5("25m Sprint", 25, 5, 2, "5m Break"),
    DEEP_50_10("50m Deep", 50, 10, 3, "10m Break"),
    EXAM_90("90m Exam", 90, 15, 4, "Full Session")
}

data class StudyChecklistItem(
    val id: Int,
    val title: String,
    val isDone: Boolean
)

data class TaskSubStep(
    val index: Int,
    val title: String,
    val isDone: Boolean
)

enum class TaskQueueStatus(val label: String) {
    ACTIVE("Active Now"),
    QUEUED("Up Next"),
    DEFERRED("Saved for Later"),
    COMPLETED("Completed")
}

/**
 * One month that holds at least one stored (COMPLETED) task — a
 * Drawer address for finding things, never a measurement scope.
 * Projected by the DAO months index on the 03:30 filing day.
 */
data class StoredMonth(val year: Int, val month: Int)

@Entity(tableName = "focus_tasks")
data class FocusTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val notesOrSubsteps: String = "",
    // Truth Car 2 (MINUTES DIE — Sarthak's order): a task is a
    // name, torn off when done. New tasks are stored with 0 = no
    // estimate was ever given; session length lives on the wheel,
    // never on a task. Rows written before the order keep their
    // stored estimate as history, and surfaces print an estimate
    // only where one was actually given (estimatedMinutes > 0).
    val estimatedMinutes: Int = 25,
    val actualSecondsSpent: Int = 0,
    val categoryTag: String = "Study",
    val queueOrder: Int = 0,
    val status: String = TaskQueueStatus.QUEUED.name,
    val completedAtMillis: Long = 0L,
    val completedInSessionId: Int = 0,
    // Routines rebuild (8→9, Nadia): session-elapsed offset at the
    // instant this task was promoted to ACTIVE. Nullable, no backfill:
    // NULL = no offset was ever recorded (the only truthful value for
    // pre-migration rows — 0 is a real offset). Writer is Nadia's
    // promotion funnel; this build lands the column only.
    val startedAtSessionElapsedSec: Int? = null
) {
    val resolvedStatus: TaskQueueStatus
        get() = runCatching { TaskQueueStatus.valueOf(status) }.getOrDefault(TaskQueueStatus.QUEUED)

    val subSteps: List<TaskSubStep>
        get() = parseSubSteps(notesOrSubsteps)

    val completedSubStepsCount: Int
        get() = subSteps.count { it.isDone }

    val subStepProgressFraction: Float
        get() {
            val steps = subSteps
            if (steps.isEmpty()) return if (resolvedStatus == TaskQueueStatus.COMPLETED) 1f else 0f
            return (steps.count { it.isDone }.toFloat() / steps.size.toFloat()).coerceIn(0f, 1f)
        }

    companion object {
        fun parseSubSteps(raw: String): List<TaskSubStep> {
            if (raw.isBlank()) return emptyList()
            val lines = raw.lines()
                .flatMap { it.split("||") }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            return lines.mapIndexed { idx, line ->
                when {
                    line.startsWith("[x]", ignoreCase = true) ->
                        TaskSubStep(idx, line.removePrefix("[x]").removePrefix("[X]").trim(), true)
                    line.startsWith("[ ]") ->
                        TaskSubStep(idx, line.removePrefix("[ ]").trim(), false)
                    line.startsWith("✓") ->
                        TaskSubStep(idx, line.removePrefix("✓").trim(), true)
                    else ->
                        TaskSubStep(idx, line, false)
                }
            }.filter { it.title.isNotEmpty() }
        }

        fun serializeSubSteps(steps: List<TaskSubStep>): String {
            return steps.joinToString("\n") { step ->
                if (step.isDone) "[x] ${step.title.trim()}" else "[ ] ${step.title.trim()}"
            }
        }

        fun defaultSubStepsForCategory(title: String, category: String): List<TaskSubStep> {
            return when (category.lowercase()) {
                "code" -> listOf(
                    TaskSubStep(0, "Trace base cases & data invariants", false),
                    TaskSubStep(1, "Implement core algorithm logic", false),
                    TaskSubStep(2, "Verify edge cases & complexity", false)
                )
                "writing" -> listOf(
                    TaskSubStep(0, "Draft 3-bullet structural outline", false),
                    TaskSubStep(1, "Write core synthesis section", false),
                    TaskSubStep(2, "Tighten key takeaways & citations", false)
                )
                "exam" -> listOf(
                    TaskSubStep(0, "First pass without reference notes", false),
                    TaskSubStep(1, "Flag & drill missed concepts", false),
                    TaskSubStep(2, "Final rapid recall check", false)
                )
                else -> listOf(
                    TaskSubStep(0, "Sketch diagram & list known variables", false),
                    TaskSubStep(1, "Solve core problem set step-by-step", false),
                    TaskSubStep(2, "Check units & boundary conditions", false)
                )
            }
        }
    }
}

enum class DistractionTriggerReason(
    val shortTag: String,
    val label: String,
    val coachingTip: String
) {
    MUSCLE_MEMORY(
        shortTag = "Habit",
        label = "Opened by Habit",
        coachingTip = "Your thumb tapped this on autopilot. Take one slow breath and return to your next micro-step."
    ),
    STUCK_ON_PROBLEM(
        shortTag = "Stuck",
        label = "Stuck on a Hard Step",
        coachingTip = "When a task feels heavy, your brain seeks quick novelty. Break the current task into a 10-minute micro-step."
    ),
    BORED_OR_TIRED(
        shortTag = "Tired",
        label = "Low Energy / Brain Fog",
        coachingTip = "Doomscrolling drains working memory faster than resting. Stand up, drink water, or finish this short block."
    ),
    NEED_INFO(
        shortTag = "Message",
        label = "Checking Urgent Message / Code",
        coachingTip = "If this app allows a 2m Peek, start the 2-minute pass below—the floating timer will keep you anchored."
    )
}

enum class AppCategory(val label: String, val code: String) {
    SHORT_VIDEO("Short Video", "Video"),
    SOCIAL("Social", "Social"),
    GAMES("Games", "Games"),
    MESSAGING("Messaging", "Chat"),
    BROWSERS("Web & News", "Web"),
    SHOPPING("Other Apps", "Tools")
}

@Entity(tableName = "blocked_apps")
data class BlockedAppEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val category: String,
    val isBlocked: Boolean,
    val blockTier: String = if (isBlocked) AppBlockTier.LOCKED.name else AppBlockTier.OPEN.name,
    val dailyLimitMinutes: Int = 30,
    val todayUsageMinutes: Int = 0,
    val interceptCount: Int = 0,
    val activePeekUntilElapsedRealtime: Long = 0L,
    val lastPeekEndElapsedRealtime: Long = 0L,
    val isSystemDetected: Boolean = false
) {
    val resolvedTier: AppBlockTier
        get() = runCatching { AppBlockTier.valueOf(blockTier) }.getOrElse {
            if (isBlocked) AppBlockTier.LOCKED else AppBlockTier.OPEN
        }
}

@Entity(tableName = "focus_profiles")
data class FocusProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val codeName: String,
    val description: String,
    val defaultMinutes: Int,
    val blockedCategoriesCsv: String,
    val scheduleLabel: String,
    val isScheduleEnabled: Boolean,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val intention: String,
    val profileName: String,
    val targetDurationSeconds: Int,
    val elapsedSeconds: Int,
    val startWallTimeMillis: Long,
    val startElapsedRealtimeMillis: Long,
    val bootCount: Int = 0,
    val accumulatedBeforeRebootSeconds: Int = 0,
    val lastTickElapsedRealtimeMillis: Long = 0L,
    val hmacSignature: String = "",
    val isStopwatchMode: Boolean = false,
    val totalStudyBlocks: Int = 1,
    val currentBlockIndex: Int = 1,
    val isInSafeBreak: Boolean = false,
    val safeBreakSecondsRemaining: Int = 0,
    // Truth Car 3 (ST-02, Tamm): the accumulated seconds of Safe
    // Break ACTUALLY TAKEN across this session's completed breaks,
    // written at every break exit — the expiry flip books the full
    // length in force, an early end (endSafeBreakEarly) books the
    // wall-derived part. spanElapsedSeconds reads this instead of
    // pricing every completed break at the full length: before
    // this column, a break that ended early would still have been
    // counted whole by the span — break time that never ran,
    // claimed on the desk clock and the receipt alike.
    val breakTakenSeconds: Int = 0,
    val penaltySecondsAdded: Int = 0,
    val isActive: Boolean,
    val completedSuccessfully: Boolean,
    val phantomReachesBlocked: Int = 0,
    val tamperDetected: Boolean = false,
    val forgedArtifactId: String? = null,
    val completedTasksSummary: String = "",
    val tasksCompletedCount: Int = 0,
    // Routines rebuild (8→9): the session row becomes the attendance
    // row. routineId NULL = manual session (true for every
    // pre-migration row, never backfilled). windowStart/windowEnd are
    // the FROZEN schedule snapshot written at materialisation (R5):
    // stand-down and judgement read these, never the live routine
    // row, never a name string — Lie #13's name-matched END dies here.
    val routineId: Int? = null,
    val windowStartMillis: Long = 0L,
    val windowEndMillis: Long = 0L,
    // NULL until finalised by the one booking funnel, then
    // ATTENDED / PARTIAL / MISSED / LATE_FIRE. Stamps are immutable.
    val routineOutcome: String? = null,
    // Plan §5.3 evidence kinds E1–E4 as bits 0–3, written only via
    // MonolithRepository.recordAttendanceEvidence (+ finalise's E3).
    val evidenceMask: Int = 0,
    val firstEvidenceMillis: Long = 0L,
    val lastEvidenceMillis: Long = 0L,
    // Routines rebuild (8→9, Rurik): the Arm Ticket's task bond.
    // Nullable, no backfill: NULL = no bond was recorded. Writer is
    // the Arm Ticket path; this build lands the column only.
    val taskId: Int? = null,
    // Slice E (9→10, Saum's unified manifest): the seal's permanent
    // record. heldAppsAtSeal / heldSitesAtSeal are written exactly
    // once, inside startSession's transaction, from the same
    // snapshot that deactivates the predecessor (Fader's writer
    // law: one writer, no DAO update for these columns will ever
    // exist). -1 = uncaptured (a pre-9→10 row, or a birth whose
    // snapshot could not be taken); 0 is a real count. Readers
    // OMIT a -1 figure, never print it (Zettel Z2).
    val heldAppsAtSeal: Int = -1,
    val heldSitesAtSeal: Int = -1,
    // Draht's release-fault record (9→10): the shield that would
    // not stop at a finish. releaseFaultShield names the faulted
    // link; releaseRetriesDone counts the retry cadence's spent
    // attempts; releaseFaultedAtWallMillis is the fault's wall
    // instant. Written only by the finish machine's fault path.
    val releaseFaultShield: Boolean = false,
    val releaseRetriesDone: Int = 0,
    val releaseFaultedAtWallMillis: Long = 0L,
    // Nocke's discriminator (9→10): the session's ending, classified
    // once at the ending write and never revised. '' = not ended
    // (or a routine row, whose judgement lives in routineOutcome).
    // Vocabulary: SessionOutcome in ReceiptState.kt.
    val outcome: String = "",
    // The shield's persisted beat (9→10): the last wall instant the
    // shield's monitor loop ticked this row. The finish's gap
    // classification reads it against the 10s law (5 missed 2s
    // beats). Written only via updateSessionProgress's beat stamp.
    val shieldLastBeatWallMillis: Long = 0L,
    // The gap latch (9→10): once any observer has seen the beat
    // stale past the grace, the latch is set forever — a shield
    // that comes back late does not un-break the session's books.
    val shieldGapSeen: Boolean = false,
    // Truth Car 2 (10→11, RC-01): the session's true end, wall
    // clock, stamped ONCE at the ending write (the elapsed path
    // in updateSessionProgress, the disarm doors, the routine
    // finalise funnel) in the same write that classifies the
    // outcome. 0 = never stamped (every pre-v11 row). The receipt
    // reads this stamp on every path — witnessed, discovered,
    // rehydrated — so ENDED prints a witnessed instant, never a
    // discovery time or a derivation wearing the same typeface.
    val endWallMillis: Long = 0L,
    // Truth Car 2 (10→11, RC-04): the receipt № this session
    // claimed, stamped on the row INSIDE the claim transaction.
    // 0 = no № claimed. The claim latches on this column — a
    // session can never mint a second №, whatever the settings
    // slot holds by then — replacing the single-slot arithmetic
    // coincidence the guarantee used to rest on.
    val receiptNumber: Int = 0
) {
    // ONE derivation of session time (the Now Frame fix, Nadia x Freya):
    // every surface - Deck, desk clock, airlock, the enforcing service -
    // reads these and only these, so the app can only ever tell one time.
    // The old remainingSeconds ignored penaltySecondsAdded while the
    // Shield service added it: two screens could disagree with the
    // service doing the actual blocking. That lie dies here.
    val effectiveTargetSeconds: Int
        get() = targetDurationSeconds + penaltySecondsAdded

    val remainingSeconds: Int
        get() = (effectiveTargetSeconds - elapsedSeconds).coerceAtLeast(0)

    // The whole-span derivations (the desk clock's truth, charged by
    // the Critics' plan audit and verified in this tree): a session
    // of n blocks spans n block targets + (n-1) Safe Breaks (Slice
    // A's W3 law). Per-block remainingSeconds reads 0 for the whole
    // of a break - that block HAS finished - so a surface with no
    // block context (the desk clock) must read THESE, or it shows
    // 00:00 for ten minutes: a break displayed as FINISH. During a
    // break the span simply keeps counting - the break is not
    // rendered AS a break (Nacht's law holds), session time is.
    // The penalty form mirrors the enforcement exactly (Register 3
    // M-1/M-2): penaltySecondsAdded is never cleared at a block
    // boundary, so EVERY block's enforced target includes it - the
    // span therefore prices every block at effectiveTargetSeconds,
    // no more and no less.
    val spanTargetSeconds: Int
        get() = totalStudyBlocks * effectiveTargetSeconds +
            (totalStudyBlocks - 1).coerceAtLeast(0) * 600

    // Truth Car 3 (ST-02, Tamm): completed breaks contribute the
    // seconds ACTUALLY taken (breakTakenSeconds, booked at every
    // break exit), never the assumed full length — the old cycle
    // arithmetic priced every finished break at the whole 600, so
    // an early-ended break would have jumped the span forward by
    // its untaken remainder at the flip. With every break taken in
    // full the two formulas agree to the second. The in-break term
    // reads the length in force through the repository's constant
    // today; when the car's breakSeconds stamp lands (Rurik,
    // PILL-AND-TIMES §3.2) that term re-points at the row's stamp —
    // the seam is declared in mail/rurik/2250.
    val spanElapsedSeconds: Int
        get() {
            return if (isInSafeBreak) {
                currentBlockIndex * effectiveTargetSeconds +
                    breakTakenSeconds +
                    (MonolithRepository.SAFE_BREAK_DURATION_SECONDS - safeBreakSecondsRemaining)
                        .coerceAtLeast(0)
            } else {
                (currentBlockIndex - 1).coerceAtLeast(0) * effectiveTargetSeconds +
                    breakTakenSeconds +
                    elapsedSeconds
            }
        }

    val spanRemainingSeconds: Int
        get() = (spanTargetSeconds - spanElapsedSeconds).coerceAtLeast(0)
}

/**
 * One spent weekly emergency break (TOKEN-SPEC §2). Append-only: no
 * update, no delete path exists anywhere in the app. The token is not
 * a counter — it is derived from these rows: remaining = 1 − spends
 * booked against the current week anchor. [weekAnchorMillis] is the
 * Monday-00:00 anchor of the week this spend counts against, STORED at
 * spend time and never recomputed (a later timezone change must not
 * re-book history). [heldSeconds] is what was paid, in the valve's own
 * terms — always 60 today.
 */
@Entity(tableName = "emergency_break_spends")
data class EmergencyBreakSpendEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val spentAtWallMillis: Long,
    val weekAnchorMillis: Long,
    val heldSeconds: Int
)

@Entity(tableName = "intercept_logs")
data class InterceptLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestampMillis: Long = System.currentTimeMillis(),
    val packageName: String,
    val appName: String,
    val sessionIntention: String,
    val outcome: String,
    val triggerReason: String = "Habit"
)

@Entity(tableName = "captured_notifications")
data class CapturedNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestampMillis: Long = System.currentTimeMillis(),
    val packageName: String,
    val appName: String,
    val senderTitle: String,
    val messageBody: String,
    val isRead: Boolean = false
)

data class DailyFocusStat(
    val dayLabel: String,
    val dateIso: String,
    val totalMinutes: Int,
    val blocks25mCount: Int,
    val interceptionsCount: Int
)

/**
 * One cabinet artifact: the catalog entry and nothing else. Truth
 * Car 2 (DRW-01/DRW-02) cut every field that described machinery
 * the app does not have: the focus/interception "requirements" no
 * code ever read (forging is one artifact chosen by lot per
 * completed session — MonolithRepository's ending write), and the
 * toy-physics parameters (freqA / freqB / phase) of kinetic views
 * that were never built. What remains is what the cabinet can
 * truthfully print: a serial, a name, a form, a line of lore that
 * describes the object — never an interaction, never an unlock.
 */
data class KineticArtifact(
    val id: String,
    val serialCode: String,
    val name: String,
    val subtitle: String,
    val lore: String
)

/**
 * One artifact's forge record, derived from real session rows
 * (Issue #110, finding 5): how many sessions forged it, and the
 * wall instant of the most recent forge. [lastForgedAtMillis] is
 * the forging session's end stamp where one exists (endWallMillis,
 * v11+), else its start — never an invented instant.
 */
data class ForgeStat(
    val count: Int,
    val lastForgedAtMillis: Long
)

/** The app's two-digit counter voice (Truth Car 2, DRW-08): 01…09,
 *  then 10, 11 — never "010". One formatter for every surface that
 *  numbers a sub-step (Tasks leaf, Deck task line, Drawer detail);
 *  the old string-prefix idiom ("0" + n) broke at exactly the step
 *  where the count started carrying information. */
fun twoDigitCount(value: Int): String = value.toString().padStart(2, '0')

object BlockedBrowserDomains {
    val defaultDomains = listOf(
        "instagram.com",
        "tiktok.com",
        "reddit.com",
        "x.com",
        "twitter.com",
        "facebook.com",
        "snapchat.com",
        "youtube.com/shorts"
    )
    val allowedStudyExamples = listOf(
        "scholar.google.com",
        "arxiv.org",
        "wikipedia.org",
        "canvas.instructure.com",
        "leetcode.com"
    )

    /**
     * Normalises free-typed input ("https://www.YouTube.com/shorts?x=1")
     * into a stored domain ("youtube.com/shorts"). Null = not a usable site.
     */
    fun normalizeDomain(raw: String): String? {
        var d = raw.trim().lowercase()
        if (d.isEmpty()) return null
        d = d.removePrefix("https://").removePrefix("http://")
        d = d.substringBefore('?').substringBefore('#')
        d = d.removePrefix("www.")
        d = d.trim().trimEnd('/')
        if (d.isEmpty() || !d.contains('.') || d.contains(' ')) return null
        return d
    }
}

/** One user-editable blocked website — the list the browser blocker enforces (Ren). */
@Entity(tableName = "blocked_sites")
data class BlockedSiteEntity(
    @PrimaryKey val domain: String
)

/** A real, persisted routine: a daily window on chosen days that arms a loadout session by itself (Ren). */
@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val startMinutes: Int,
    val endMinutes: Int,
    val daysMask: Int,
    val loadoutName: String,
    val enabled: Boolean = true,
    // Routines rebuild (8→9): pause-until (plan §3.5). 0 = not paused.
    // An input to the one derivation; a window whose start precedes
    // this instant is paused while the pause is in force.
    val pausedUntilMillis: Long = 0L,
    // Eligibility horizon (Inv. §3.5): a no-session window is judged
    // only if it starts after the last edit — before that horizon the
    // current row cannot prove the window was ever scheduled, so the
    // judgement is UNKNOWN, never an invented MISSED. Stamped by the
    // single persistRoutine funnel on every write. 0 = no edit
    // recorded under the new engine.
    val scheduleEditedAtMillis: Long = 0L
)

/** Pure window math for routines. daysMask: bit 0 = Monday … bit 6 = Sunday. */
object RoutineSchedule {
    const val DAY_MINUTES = 1440
    const val ALL_DAYS = 127
    const val MON_FRI = 31

    fun durationMinutes(routine: RoutineEntity): Int {
        var d = routine.endMinutes - routine.startMinutes
        if (d <= 0) d += DAY_MINUTES
        // Routines rebuild: the clamp unifies at 30..1080 with
        // adjustRoutineTime's editor clamp (Sten §1.9) — printed
        // duration ≡ raw span for every storable routine, and
        // DAY BLOCKS' 630 minutes sits mid-range. Was 15..720.
        return d.coerceIn(30, 1080)
    }

    private fun bitFor(calendar: java.util.Calendar): Int =
        (calendar.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7

    private fun atMinutes(day: java.util.Calendar, minutes: Int): Long {
        val c = day.clone() as java.util.Calendar
        c.set(java.util.Calendar.HOUR_OF_DAY, minutes / 60)
        c.set(java.util.Calendar.MINUTE, minutes % 60)
        c.set(java.util.Calendar.SECOND, 0)
        c.set(java.util.Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    /** Next window start strictly after [nowMillis]; null if no days selected. */
    fun nextStartMillis(routine: RoutineEntity, nowMillis: Long): Long? {
        if (routine.daysMask == 0) return null
        val day = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        repeat(8) {
            if (routine.daysMask and (1 shl bitFor(day)) != 0) {
                val start = atMinutes(day, routine.startMinutes)
                if (start > nowMillis) return start
            }
            day.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        return null
    }

    /** End of the window containing [nowMillis], or of the next window if none contains it. */
    fun nextEndMillis(routine: RoutineEntity, nowMillis: Long): Long? {
        if (routine.daysMask == 0) return null
        val durMs = durationMinutes(routine) * 60_000L
        val day = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        // A window that started yesterday may still be running (overnight).
        day.add(java.util.Calendar.DAY_OF_YEAR, -1)
        repeat(9) {
            if (routine.daysMask and (1 shl bitFor(day)) != 0) {
                val start = atMinutes(day, routine.startMinutes)
                val end = start + durMs
                if (nowMillis < end && end > nowMillis && start <= nowMillis) return end
                if (start > nowMillis) return end
            }
            day.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        return null
    }

    /**
     * The window containing [nowMillis], or null. Folds in the
     * overnight logic that used to live in the caller-facing
     * [nextEndMillis]: a window that started yesterday may still be
     * running. No caller outside the schedule layer may ask for a
     * window end any other way (Sten §1.9).
     */
    fun windowContaining(routine: RoutineEntity, nowMillis: Long): WindowInstance? {
        if (routine.daysMask == 0) return null
        val durMs = durationMinutes(routine) * 60_000L
        val day = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        day.add(java.util.Calendar.DAY_OF_YEAR, -1)
        repeat(2) {
            if (routine.daysMask and (1 shl bitFor(day)) != 0) {
                val start = atMinutes(day, routine.startMinutes)
                val end = start + durMs
                if (nowMillis in start until end) {
                    return WindowInstance(
                        startMillis = start,
                        endMillis = end,
                        remainderMinutes = ((end - nowMillis) / 60_000L).toInt(),
                        driftMinutes = ((nowMillis - start) / 60_000L).toInt().coerceAtLeast(0)
                    )
                }
            }
            day.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        return null
    }

    /** The next window to start strictly after [nowMillis], as an instance. */
    fun nextWindow(routine: RoutineEntity, nowMillis: Long): WindowInstance? {
        val start = nextStartMillis(routine, nowMillis) ?: return null
        val end = start + durationMinutes(routine) * 60_000L
        return WindowInstance(
            startMillis = start,
            endMillis = end,
            remainderMinutes = ((end - nowMillis) / 60_000L).toInt(),
            driftMinutes = 0
        )
    }

    /**
     * The most recent window whose end is at or before [nowMillis],
     * searching back up to 8 start-days. One routine's windows never
     * overlap (starts are 24h apart, spans ≤ 18h), so the first hit
     * scanning backwards is the latest closed window.
     */
    fun latestClosedWindow(routine: RoutineEntity, nowMillis: Long): WindowInstance? {
        if (routine.daysMask == 0) return null
        val durMs = durationMinutes(routine) * 60_000L
        val day = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        repeat(9) {
            if (routine.daysMask and (1 shl bitFor(day)) != 0) {
                val start = atMinutes(day, routine.startMinutes)
                val end = start + durMs
                if (end <= nowMillis) {
                    return WindowInstance(
                        startMillis = start,
                        endMillis = end,
                        remainderMinutes = 0,
                        driftMinutes = ((nowMillis - start) / 60_000L).toInt().coerceAtLeast(0)
                    )
                }
            }
            day.add(java.util.Calendar.DAY_OF_YEAR, -1)
        }
        return null
    }

    /**
     * Pause-aware next start (Sten §1.9): the scheduler and the
     * ribbon read this one helper so they can never disagree. The
     * origin is `max(now, pausedUntilMillis)` — a paused routine's
     * next start is computed from the pause's end.
     */
    fun effectiveNextStartMillis(routine: RoutineEntity, nowMillis: Long): Long? =
        nextStartMillis(routine, maxOf(nowMillis, routine.pausedUntilMillis))

    /**
     * LIVE (UNA-09, Truth Car 3): a routine the scheduler will ever
     * fire — switched on AND carrying at least one day. This is the
     * ONE definition every reader shares: the chain-alarm derivation
     * (RoutineDerive.nextBoundary skips non-live routines) and
     * Shield's "n ACTIVE" header both read it, so the printed count
     * can never again include a daysMask == 0 routine the scheduler
     * silently skips.
     */
    fun isLive(routine: RoutineEntity): Boolean =
        routine.enabled && routine.daysMask != 0

    /** How many of [routines] are LIVE — Shield's "n ACTIVE" number. */
    fun liveCount(routines: List<RoutineEntity>): Int =
        routines.count { isLive(it) }

    fun isActiveNow(routine: RoutineEntity, nowMillis: Long): Boolean {
        if (!isLive(routine)) return false
        val durMs = durationMinutes(routine) * 60_000L
        val day = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        day.add(java.util.Calendar.DAY_OF_YEAR, -1)
        repeat(2) {
            if (routine.daysMask and (1 shl bitFor(day)) != 0) {
                val start = atMinutes(day, routine.startMinutes)
                if (nowMillis in start until start + durMs) return true
            }
            day.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        return false
    }
}

/** Single-row guard settings the accessibility service must honour (Ren). */
@Entity(tableName = "guard_settings")
data class GuardSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val browserBlockerEnabled: Boolean = true,
    // Routines rebuild (8→9): the reseed's memory (the transform
    // itself is NOT a migration — it runs in seed-sync under this
    // flag, version-blind) and its one-time note's dismissal.
    val routineReseedV1Done: Boolean = false,
    val routineReseedNoteDismissed: Boolean = false,
    // Boot witness (Inv. §3 Q3): a closed window containing a boot,
    // with no session, is the only provable NOT_DELIVERED.
    val lastBootAtMillis: Long = 0L,
    // Otis §3.2/§6: when the heartbeat last completed a tick. Rides
    // 8→9 because routines must not ship a build with no
    // heartbeat-absence detection; the 9→10 readiness log later
    // becomes this scalar's backing store.
    val lastRoutineTickMillis: Long = 0L,
    // Slice E (9→10): the receipt's home (Zettel Z1 — the store is
    // this singleton row, no receipt_store table). receiptSequence
    // is the № counter, minted ONLY inside the claim transaction;
    // lastReceiptSessionId is the slot pointer (the session whose
    // receipt stands); lastReceiptOutcome its outcome word;
    // lastCompositeSessionId is Nocke's finish latch — the session
    // whose finish composite has fired, once, ever. -1/0 = none.
    val lastCompositeSessionId: Int = -1,
    val lastReceiptOutcome: String = "",
    val receiptSequence: Int = 0,
    val lastReceiptSessionId: Int = 0,
    // Truth Car 3 (11→12, UNA-01): the Shorts + Reels sniper's
    // persisted switch. Until this column the toggle lived only
    // in the ViewModel's memory — PARK printed a control that did
    // not exist and the sniper fired (or not) on a value no store
    // held. Default true IS the truthful pre-v12 value: the sniper
    // fired unconditionally whenever a session was active.
    val shortsReelsSniperEnabled: Boolean = true
)

/**
 * A declared day word (F-SCH-1 — the Drawer's `day_notes` table,
 * riding Migration 8→9). [dateIso] is the filing date
 * (yyyy-MM-dd); [word] is his authored declaration for that day,
 * one of the closed list REST / ILL / FAMILY, printed beside the
 * date's heading in the Drawer. It is a word, never a number,
 * and it is never folded into any total.
 *
 * Never TEST: this app does not know test days (Ren's test-date
 * kill). The prefs drain that populates this table refuses any
 * word outside the closed list, so no TEST row can ever exist.
 */
@Entity(tableName = "day_notes")
data class DayNoteEntity(
    @PrimaryKey val dateIso: String,
    val word: String
)

/**
 * The Partial Stamp's honest identity (Issue #110, finding 5).
 * An early-ended session that banked at least a minute stamps
 * [PARTIAL_STAMP_ID] as its forgedArtifactId (the disarm doors in
 * MonolithRepository). Before this entry existed, that id matched
 * NO catalog row: the cabinet's DISTINCT read swallowed it, and a
 * real record of real minutes stood in no cabinet and answered to
 * no name. The stamp is not a forged artifact and never prints as
 * one — it is the mark of a span that ended early, and its lore
 * says exactly that.
 */
const val PARTIAL_STAMP_ID = "partial_stamp"

val PARTIAL_STAMP_ARTIFACT = KineticArtifact(
    id = PARTIAL_STAMP_ID,
    serialCode = "Stamp 07",
    name = "Partial Stamp",
    subtitle = "A Span Ended Early",
    lore = "Not a forged curio. When a sealed span ends early with at least a minute banked, the session is stamped partial — the minutes were real, the finish was not. It stands here so the record has a name, never as a prize."
)

object CurioCatalog {
    // Truth Car 2 (DRW-01/DRW-02): serials carry no minutes (the
    // "• 300M" suffix printed a price no code enforced), subtitles
    // name the object's form only, and the lore describes the
    // object as an object. Nothing here promises an interaction,
    // an unlock, or a requirement — the cabinet's one true rule
    // (a completed session forges one, chosen by lot) is printed
    // by the cabinet itself, from the forge's actual code path.
    val artifacts = listOf(
        KineticArtifact(
            id = "artifact_lissajous",
            serialCode = "Toy 01",
            name = "Harmonic Loop",
            subtitle = "A Wave Bent Against Itself",
            lore = "Two waves of different pitch, drawn against each other until the line closes into a knot of light."
        ),
        KineticArtifact(
            id = "artifact_gyro",
            serialCode = "Toy 02",
            name = "Brass Gyroscope",
            subtitle = "Four Concentric Rings",
            lore = "Four brass rings nested one inside the next, each free on its own axis, the whole thing no wider than a palm."
        ),
        KineticArtifact(
            id = "artifact_loom",
            serialCode = "Toy 03",
            name = "String Prism",
            subtitle = "Straight Lines, A Curved Form",
            lore = "Nothing in it is curved. Straight threads under tension borrow the shape of a prism from the air between them."
        ),
        KineticArtifact(
            id = "artifact_kepler",
            serialCode = "Toy 04",
            name = "Pocket Planetarium",
            subtitle = "Two Orbits, One Flower",
            lore = "Two small planets on fixed orbits; the line joining them, traced patiently, blooms into a geometric flower."
        ),
        KineticArtifact(
            id = "artifact_pendulum",
            serialCode = "Toy 05",
            name = "Sand Pendulum",
            subtitle = "A Spiral Drawn In Sand",
            lore = "A heavy brass pendulum over a bed of sand, its swing decaying into a spiral that narrows toward rest."
        ),
        KineticArtifact(
            id = "artifact_tesseract",
            serialCode = "Toy 06",
            name = "The Monolith Cube",
            subtitle = "A Cube Within A Cube",
            lore = "Sixteen vertices, two cubes, one held inside the other by lines that should not reach — and do."
        )
    )
}

