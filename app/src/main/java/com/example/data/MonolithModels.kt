package com.example.data

import androidx.room.Entity
import androidx.room.Index
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
        badge = "Shut",
        description = "Blocked while a focus session or protection schedule is active",
        displayLabel = "Shut"
    ),
    PEEK_2M(
        label = "2m Peek",
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
        subtitle = "Blocks Social & Games, keeps YouTube, browsers & study apps open for lectures, sets Messaging to 2m Peek, enables Shorts & Web shields.",
        badge = "Recommended"
    ),
    WORK_SPRINT(
        title = "Work Sprint",
        subtitle = "Blocks Short Video, Social Feeds, and Games while keeping Messaging and Docs accessible.",
        badge = " Balanced"
    ),
    SLEEP(
        title = "Full Lockdown",
        subtitle = "Blocks every distracting app except Phone, Clock, Maps, Camera, and navigation for total focus or wind-down.",
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

@Entity(
    tableName = "focus_tasks",
    // #30 F1 (train inheritance): the Drawer's hot sort — tasks
    // by completion instant — stops being a full table scan.
    indices = [Index(value = ["completedAtMillis"])]
)
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
    // Issue #32 F1: the default IS the invariant — 0 = no estimate
    // was ever given. The old default of 25 fabricated an estimate
    // on every direct construction / copy() / Room default, which
    // the >0 surfaces then printed as if the user had given it.
    val estimatedMinutes: Int = 0,
    val actualSecondsSpent: Int = 0,
    // Issue #270: the default tag is "" — no category was given.
    // (Was "Study": an app-assigned bucket on every construction,
    // against Sarthak's 08:55 own-words-or-nameless ruling.)
    val categoryTag: String = "",
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
    /** Issue #32 F3: true when the stored status string is not a
     *  real [TaskQueueStatus] — a corrupt row, marked, never
     *  laundered into a plausible-looking state. */
    val statusIsCorrupt: Boolean
        get() = runCatching { TaskQueueStatus.valueOf(status) }.isFailure

    // Issue #32 F3 (as landed): a corrupt status is MARKED via
    // [statusIsCorrupt] above, and the fallback stays QUEUED —
    // exactly the pre-audit behaviour. The stronger cure (corrupt
    // → DEFERRED, inert + visible instead of promotable) changes
    // what the app DOES with a corrupt row: a garbled task would
    // stop auto-promoting into the live queue. Per Sarthak's
    // no-downgrade rule (Oct 30, 04:58 IST) that behaviour change
    // is NOT landed unilaterally — it is DECISION-NEEDED, options
    // spelled out in studio/plan/ISSUE-32-WAVE3-REPORT.md.
    val resolvedStatus: TaskQueueStatus
        get() = runCatching { TaskQueueStatus.valueOf(status) }.getOrDefault(TaskQueueStatus.QUEUED)

    /** Issue #32 F2 (write-boundary cure): a copy of this row with
     *  every numeric field inside its lawful range. Applied at
     *  construction/write funnels ONLY — Room reads legacy rows
     *  through the plain constructor, so nothing here can throw on
     *  or rewrite stored data by itself. */
    fun sanitized(): FocusTaskEntity = copy(
        estimatedMinutes = if (estimatedMinutes < 0) 0 else estimatedMinutes,
        actualSecondsSpent = actualSecondsSpent.coerceAtLeast(0),
        queueOrder = queueOrder.coerceAtLeast(0),
        completedAtMillis = completedAtMillis.coerceAtLeast(0L)
    )

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

        /**
         * SPLIT's steps (issue #106, finding 5). The old generator
         * never read [title] at all: it switched on category alone,
         * and every task the composer creates is category "Study",
         * so every task — "Revise History dates", "Learn French
         * verbs" — was stamped with the same three problem-set steps
         * ("Sketch diagram & list known variables" …) and presented
         * as that task's own breakdown. Now the task's OWN words
         * decide, in this order:
         *
         * 1. A title that already lists its parts ("Read Ch.4; make
         *    notes; self-test") is split on its own separators —
         *    those parts ARE the steps, verbatim.
         * 2. A title/category that names a kind of work (code,
         *    writing, exam, a problem set, language drill, reading /
         *    revision) gets the method steps for THAT kind — the
         *    problem-set triple survives only where the task is
         *    actually problem work, which is the one place it was
         *    ever a deliberate template.
         * 3. Anything else gets three phases of the task itself,
         *    named with its own title — begin it, finish it, check
         *    it — never a borrowed subject's steps.
         */
        fun defaultSubStepsForCategory(title: String, category: String): List<TaskSubStep> {
            val cleanTitle = title.trim()
            val listedParts = cleanTitle
                .split(';', '•', '|')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            if (listedParts.size >= 2) {
                return listedParts.take(5).mapIndexed { idx, part ->
                    TaskSubStep(idx, part, false)
                }
            }
            val words = cleanTitle.lowercase(java.util.Locale.ROOT)
            fun mentions(vararg needles: String) = needles.any { it in words }
            return when {
                category.lowercase(java.util.Locale.ROOT) == "code" ||
                    mentions("code", "coding", "debug", "program", "algorithm") -> listOf(
                    TaskSubStep(0, "Trace base cases & data invariants", false),
                    TaskSubStep(1, "Implement core algorithm logic", false),
                    TaskSubStep(2, "Verify edge cases & complexity", false)
                )
                category.lowercase(java.util.Locale.ROOT) == "writing" ||
                    mentions("write", "essay", "draft", "report") -> listOf(
                    TaskSubStep(0, "Draft 3-bullet structural outline", false),
                    TaskSubStep(1, "Write core synthesis section", false),
                    TaskSubStep(2, "Tighten key takeaways & citations", false)
                )
                category.lowercase(java.util.Locale.ROOT) == "exam" ||
                    mentions("exam", "mock test", "quiz") -> listOf(
                    TaskSubStep(0, "First pass without reference notes", false),
                    TaskSubStep(1, "Flag & drill missed concepts", false),
                    TaskSubStep(2, "Final rapid recall check", false)
                )
                mentions("problem", "equation", "numerical", "diagram", "units") -> listOf(
                    TaskSubStep(0, "Sketch diagram & list known variables", false),
                    TaskSubStep(1, "Solve core problem set step-by-step", false),
                    TaskSubStep(2, "Check units & boundary conditions", false)
                )
                mentions("verb", "vocab", "grammar", "spelling", "french", "spanish", "german", "language") -> listOf(
                    TaskSubStep(0, "Go through the list once, saying each item aloud", false),
                    TaskSubStep(1, "Drill only the items you missed", false),
                    TaskSubStep(2, "Final recall pass from memory, no notes", false)
                )
                mentions("revise", "revision", "read", "learn", "memori", "chapter", "notes", "history", "dates", "theory", "recall") -> listOf(
                    TaskSubStep(0, "First pass through the material, marking what is unclear", false),
                    TaskSubStep(1, "Second pass on the marked parts only", false),
                    TaskSubStep(2, "Close the material and recall the key points from memory", false)
                )
                else -> listOf(
                    TaskSubStep(0, "Begin: $cleanTitle", false),
                    TaskSubStep(1, "Continue and complete: $cleanTitle", false),
                    TaskSubStep(2, "Review what you did for: $cleanTitle", false)
                )
            }
        }
    }
}


enum class AppCategory(val label: String, val code: String) {
    SHORT_VIDEO("Short Video", "Video"),
    SOCIAL("Social", "Social"),
    GAMES("Games", "Games"),
    MESSAGING("Messaging", "Chat"),
    BROWSERS("Web & News", "Web"),
    // Issue #132 (music-during-focus): media players get a real
    // home in the EXISTING category system — before this, a player
    // was filed by substring luck (YouTube Music as SHORT_VIDEO,
    // "…Player" labels as GAMES, Spotify as SHOPPING by accident).
    MUSIC("Music & Audio", "Music"),
    // EDUCATION (issue #133, study-on-phone-itself): the
    // deterministic home for study apps — filed by AppCategorizer
    // (deterministic package homes + study signals), correctable
    // by the user (setAppCategory, issue #107). Before this value
    // a study app had no home at all and fell to SHOPPING or a
    // name-substring trap.
    EDUCATION("Education", "Study"),
    // Issue #120 (otp-banking-mid-session): finance gets a real
    // home. Banks, UPI apps and authenticators are time-critical
    // infrastructure — an OTP or a UPI collect request dies in
    // minutes — not shopping. Before this value existed they fell
    // through to SHOPPING, and the Sleep profile bulk-locked every
    // one of them without the user ever reviewing a single bank.
    FINANCE("Finance & Payments", "Money"),
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
    /** Issue #32 F3/F4: true when the two stored tier facts
     *  disagree or the tier string is garbage — corruption is
     *  marked here, never silently laundered. */
    val tierIsCorrupt: Boolean
        get() {
            val parsed = runCatching { AppBlockTier.valueOf(blockTier) }.getOrNull()
                ?: return true
            return isBlocked != (parsed != AppBlockTier.OPEN)
        }

    // Issue #32 F3/F4 — THE single derivation point for this row's
    // tier. blockTier is the truth; isBlocked is its derived cache
    // (isBlocked == tier != OPEN). Resolution order:
    //  1. a valid blockTier wins, exactly as stored;
    //  2. a GARBAGE blockTier fails CLOSED to LOCKED when the row
    //     claims any block (isBlocked) — a corrupt tier must never
    //     silently become OPEN and defeat the block;
    //  3. garbage tier + isBlocked=false resolves OPEN (the row
    //     claims no block; inventing one would lock a user's app
    //     on a typo) — and [tierIsCorrupt] marks the row.
    val resolvedTier: AppBlockTier
        get() = runCatching { AppBlockTier.valueOf(blockTier) }.getOrElse {
            if (isBlocked) AppBlockTier.LOCKED else AppBlockTier.OPEN
        }

    /** Issue #32 F4: the normalized form of this row — both stored
     *  facts rewritten from the ONE derivation ([resolvedTier]),
     *  counts floored at 0. Every write funnel that sets a tier
     *  routes through here (or writes both fields from one tier
     *  value), so isBlocked and blockTier cannot diverge after a
     *  copy() the way they could when each was set independently. */
    fun normalized(): BlockedAppEntity {
        val tier = resolvedTier
        return copy(
            isBlocked = tier != AppBlockTier.OPEN,
            blockTier = tier.name,
            dailyLimitMinutes = dailyLimitMinutes.coerceAtLeast(0),
            todayUsageMinutes = todayUsageMinutes.coerceAtLeast(0),
            interceptCount = interceptCount.coerceAtLeast(0)
        )
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
    val endMinute: Int,
    // Issue #253 lines 8 + 9: the full-context bundle (DB 20→21).
    // A profile is a SNAPSHOT, not just a category stencil:
    // appTiersCsv carries per-app tier rulings (an OPEN entry is
    // the per-app exception), appLimitsCsv the per-app daily
    // limits, sitesCsv the blocked-site set — all encoded by
    // ProfileBundle. The three strictness/sound facts are
    // nullable: null means this profile does not rule that
    // surface (every pre-bundle row), a value means applying
    // the profile writes that fact. Notifications are NOT in
    // the bundle on purpose: the app's only notification facts
    // are the seal's automatic DND engagement (session-scoped,
    // not a preference) and a privacy consent a profile must
    // never flip — there is no lawful stored carrier today.
    val appTiersCsv: String = "",
    val appLimitsCsv: String = "",
    val sitesCsv: String = "",
    val soundEnabled: Boolean? = null,
    val sniperEnabled: Boolean? = null,
    val browserBlockEnabled: Boolean? = null
) {
    /** Issue #32 F2 (write-boundary cure): hours/minutes inside
     *  their lawful ranges. endHour may be 24 — the existing
     *  updateProfileTimeWindow writer uses 24 as its midnight-end
     *  sentinel (coerceIn(1, 24)), so the range is 0..24, not
     *  0..23. Applied at write funnels only; Room reads legacy
     *  rows through the plain constructor, so stored rows can
     *  never crash or be rewritten by this. */
    fun sanitized(): FocusProfileEntity = copy(
        startHour = startHour.coerceIn(0, 23),
        startMinute = startMinute.coerceIn(0, 59),
        endHour = endHour.coerceIn(0, 24),
        endMinute = endMinute.coerceIn(0, 59),
        defaultMinutes = defaultMinutes.coerceIn(1, 1440)
    )
}

@Entity(
    tableName = "focus_sessions",
    // #30 F1 (train inheritance): sessions are read by routine
    // and by window end on the hot paths — both stop scanning.
    indices = [Index(value = ["routineId"]), Index(value = ["windowEndMillis"])]
)
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
    // W3 #134 (phone-call-mid-session, DB 14→15): the seconds of THIS
    // block the call bypass was active — AudioManager in a call
    // mode (ringing / in-call / VoIP) while the progress machine
    // ticked. Those seconds are excluded from the block's booked
    // elapsed at every tick: call time is not focus time, and a
    // session may not finish, flip into its Safe Break, or stamp
    // ELAPSED_CLEAN on the strength of minutes spent talking.
    // Booked per block: the break-exit write (advancePastBreak)
    // resets it to 0 alongside the anchors it re-bases, so one
    // block's call never taxes the next block's clock.
    val callExcludedSeconds: Int = 0,
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
    // W-41 (13→14): the HELD total split by tier — heldShutAtSeal
    // counts LOCKED ("Shut") apps, heldPeekAtSeal counts PEEK_2M
    // ("2m Peek") apps. Written by the same single writer, inside
    // the same birth transaction, from the same snapshot as
    // heldAppsAtSeal (their sum IS the total, by construction).
    // The receipt prints the split; it never flattens the two
    // tiers into one number. -1 = uncaptured (a pre-13→14 row):
    // readers fall back to the stored total, never invent a split
    // that was never recorded.
    val heldShutAtSeal: Int = -1,
    val heldPeekAtSeal: Int = -1,
    // Draht's release-fault record (9→10): the shield that would
    // not stop at a finish. releaseFaultShield names the faulted
    // link; releaseRetriesDone counts the retry cadence's spent
    // attempts; releaseFaultedAtWallMillis is the fault's wall
    // instant. Set only by the finish machine's fault path; the
    // flag (never the stamps) is cleared by the retry scheduler
    // when the fault heals or is superseded (issue #47 F1).
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
    val receiptNumber: Int = 0,
    // Issue #287 (12→13, Forest head-to-head F5/F8): the session's
    // own failure record and the user's own words about it.
    // failureCausePackage names the blocked app whose reach the
    // record blames for the break ('' = none recorded — a clean
    // finish, a grace cancel, or a break with no deflection on
    // record); failureCauseKind is the trigger vocabulary:
    // EMERGENCY_VALVE / MANUAL_DISARM. Both are stamped once, at
    // the ending write, by the disarm doors — never revised.
    // reflectionNote is the one user-authored line stored against
    // the finished session ('' = he said nothing); written only
    // through MonolithRepository.setSessionReflection.
    val failureCausePackage: String = "",
    val failureCauseKind: String = "",
    val reflectionNote: String = "",
    // Audit R6 #340 F5/F48 (16→17): the FINISH seal. The arm-time
    // hmacSignature covers only the session's first five fields;
    // this second signature is minted at publish time over the
    // finished receipt's own claims (№, outcome word, arm/end
    // walls, sealed elapsed, held counts, deflection count,
    // banked break seconds, gap + break facts) AND over the
    // previous receipt's finish seal — so the receipts form a
    // hash chain: editing, reordering or dropping one slip breaks
    // every later verification loudly. '' = never sealed (every
    // pre-v17 row, and any row whose publish never ran).
    val finishSignature: String = "",
    // The previous receipt's finish seal, embedded in this row's
    // finish payload at sign time (the chain link itself, kept
    // on the row so verification needs no ordering assumption).
    val prevFinishSignature: String = "",
    // Issue #261 (15→16, the mid-block Safe Break door — the
    // transferred W9 DAYLIGHT DS3 finding): a Safe Break entered
    // MID-BLOCK, at the man's tap, instead of only at a block
    // flip. midBlockSafeBreak marks the CURRENT break's origin
    // while it runs (false = the break in force, if any, began
    // at a block flip; the flip's own rows never set it) — the
    // break exits read it to choose resume-same-block over
    // advance-to-next-block, a fact recorded on the row, never
    // inferred from elapsed arithmetic. Cleared at every break
    // exit. extraSafeBreakSeconds is the PLANNED extra break
    // time this session has booked by entering mid-block breaks
    // (the break length in force, stamped once per entry, never
    // revised): spanTargetSeconds prices it, because a mid-block
    // break is a break slot the (totalStudyBlocks − 1) scheduled
    // slots do not contain. 0 / false ARE the truthful pre-v16
    // values — before this door no break could start mid-block.
    val midBlockSafeBreak: Boolean = false,
    val extraSafeBreakSeconds: Int = 0,
    // Batch 10 (12→13): appended after receiptNumber, in exactly
    // MIGRATION_12_13's ADD COLUMN order (entity order = column
    // order, per table — the composed-order law).
    // Frist: the break length stamped on the row at seal, so the
    // span prices the break that was SET, not a constant.
    // Issue #264 B-264-18: the startSession funnel stamps the
    // arming preset's breakMinutes at birth (0 = unstamped: arms
    // that carry no break choice); effectiveBreakSeconds resolves
    // the stamp against the repository's named constant, and the
    // 600 default below keeps every row born outside the funnel
    // (and every migrated row) at the pre-stamp truth.
    val breakSeconds: Int = 600,
    // Frist: ADD TIME's accumulated grant on this row; the span
    // getters price it via effectiveTargetSeconds' extension term.
    val extensionSecondsAdded: Int = 0,
    // Frist: the per-extension event log ("+10@HH:MM" entries),
    // list-never-sum on the slip (W-44).
    val extensionLog: String = "",
    // Voss (routine-edit, RC-03): the routine's WITHIN label,
    // frozen at seal; null = no label. Nullable, NO default in
    // the migration — the only nullable column of the eight.
    val withinLabel: String? = null
) {
    // ONE derivation of session time (the Now Frame fix, Nadia x Freya):
    // every surface - Deck, desk clock, airlock, the enforcing service -
    // reads these and only these, so the app can only ever tell one time.
    // The old remainingSeconds ignored penaltySecondsAdded while the
    // Shield service added it: two screens could disagree with the
    // service doing the actual blocking. That lie dies here.
    val effectiveTargetSeconds: Int
        get() = (targetDurationSeconds.coerceAtLeast(0) + penaltySecondsAdded.coerceAtLeast(0))

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
    // Issue #32 F2: block counts read through these clamps at
    // every derivation point, so a legacy row with totalStudyBlocks
    // = 0 or currentBlockIndex = 999 prices a sane span instead of
    // a negative/absurd one — coercion at READ, because Room loads
    // stored rows through the plain constructor and an init{}
    // require() would crash on exactly those rows.
    val safeTotalBlocks: Int
        get() = totalStudyBlocks.coerceIn(1, 64)

    val safeCurrentBlockIndex: Int
        get() = currentBlockIndex.coerceIn(1, safeTotalBlocks)

    /** Issue #32 F2 (write-boundary cure): durations/counts floored
     *  and block counts bounded. Sentinels are untouched on
     *  purpose: heldAppsAtSeal/heldSitesAtSeal keep -1 =
     *  uncaptured, evidenceMask keeps its bits. Applied at the
     *  startSession write funnel only. */
    fun sanitized(): FocusSessionEntity = copy(
        targetDurationSeconds = targetDurationSeconds.coerceAtLeast(0),
        elapsedSeconds = elapsedSeconds.coerceAtLeast(0),
        accumulatedBeforeRebootSeconds = accumulatedBeforeRebootSeconds.coerceAtLeast(0),
        totalStudyBlocks = totalStudyBlocks.coerceIn(1, 64),
        currentBlockIndex = currentBlockIndex.coerceIn(1, totalStudyBlocks.coerceIn(1, 64)),
        safeBreakSecondsRemaining = safeBreakSecondsRemaining.coerceAtLeast(0),
        breakSeconds = breakSeconds.coerceIn(0, MonolithRepository.MAX_BREAK_SECONDS),
        breakTakenSeconds = breakTakenSeconds.coerceAtLeast(0),
        extraSafeBreakSeconds = extraSafeBreakSeconds.coerceAtLeast(0),
        penaltySecondsAdded = penaltySecondsAdded.coerceAtLeast(0),
        tasksCompletedCount = tasksCompletedCount.coerceAtLeast(0),
        receiptNumber = receiptNumber.coerceAtLeast(0)
    )

    /** Issue #264 B-264-18: the break length in force for THIS
     *  row — its own birth stamp when one was written, the
     *  repository's named constant for every unstamped row. The
     *  one reader every break-length consumer shares (span
     *  pricing here, the progress machine and break verbs via
     *  MonolithRepository.breakLengthSeconds, SessionWallClock). */
    val effectiveBreakSeconds: Int
        get() = if (breakSeconds > 0) breakSeconds
            else MonolithRepository.SAFE_BREAK_DURATION_SECONDS

    // Issue #32 F5: the break length in the span TARGET is the
    // length in force for the row — the same one spanElapsedSeconds
    // reads — never the magic 600 that used to sit here. Change
    // the row's effectiveBreakSeconds and target and elapsed move
    // together. (Issue #264 B-264-18: the span now prices the
    // session's own stamped break length — the arming preset's
    // breakMinutes 5/10/15 — instead of the fixed 10 minutes the
    // mechanism used to impose on every session.)
    // Issue #261: the planned span also prices every mid-block
    // Safe Break entered (extraSafeBreakSeconds, stamped at each
    // entry) — a mid-block break is real span time the scheduled
    // (blocks − 1) break slots do not contain, and a target that
    // ignored it would let the desk clock and the planned-end
    // derivation call the session finished while its resumed
    // block still had the break's length left to run.
    val spanTargetSeconds: Int
        get() = safeTotalBlocks * effectiveTargetSeconds +
            (safeTotalBlocks - 1).coerceAtLeast(0) * effectiveBreakSeconds +
            extraSafeBreakSeconds.coerceAtLeast(0)

    // Truth Car 3 (ST-02, Tamm): completed breaks contribute the
    // seconds ACTUALLY taken (breakTakenSeconds, booked at every
    // break exit), never the assumed full length — the old cycle
    // arithmetic priced every finished break at the whole 600, so
    // an early-ended break would have jumped the span forward by
    // its untaken remainder at the flip. With every break taken in
    // full the two formulas agree to the second. The in-break term
    // reads the length in force through the row's own stamp
    // (effectiveBreakSeconds — the Truth Car seam, landed by
    // issue #264 B-264-18).
    val spanElapsedSeconds: Int
        get() {
            return if (isInSafeBreak && midBlockSafeBreak) {
                // Issue #261: inside a MID-BLOCK break the current
                // block has NOT finished — the span is the finished
                // blocks, this block's frozen elapsed, the breaks
                // already booked, and the running part of this one.
                // (The flip-born branch below prices the current
                // block whole, because at a flip it HAS finished.)
                (safeCurrentBlockIndex - 1).coerceAtLeast(0) * effectiveTargetSeconds +
                    elapsedSeconds.coerceAtLeast(0) +
                    breakTakenSeconds.coerceAtLeast(0) +
                    (effectiveBreakSeconds - safeBreakSecondsRemaining)
                        .coerceAtLeast(0)
            } else if (isInSafeBreak) {
                safeCurrentBlockIndex * effectiveTargetSeconds +
                    breakTakenSeconds.coerceAtLeast(0) +
                    (effectiveBreakSeconds - safeBreakSecondsRemaining)
                        .coerceAtLeast(0)
            } else {
                (safeCurrentBlockIndex - 1).coerceAtLeast(0) * effectiveTargetSeconds +
                    breakTakenSeconds.coerceAtLeast(0) +
                    elapsedSeconds.coerceAtLeast(0)
            }
        }

    val spanRemainingSeconds: Int
        get() = (spanTargetSeconds - spanElapsedSeconds).coerceAtLeast(0)

    // #127 (glance-time-remaining, finding 1): the labelled scope of
    // every number a glance can meet. Per-block remaining and whole-
    // session (span) remaining are BOTH true — the lie was printing
    // either one unlabelled, so two surfaces could answer the same
    // question with different numbers. Every running surface that
    // prints a time now also prints this caption's scope words, or
    // the scope word for its own number, so a two-second glance
    // always knows WHICH truth it is reading.
    val glanceCaption: String
        get() = when {
            isStopwatchMode -> "ELAPSED · NO SET END"
            isInSafeBreak ->
                "BREAK ${formatSessionDuration(safeBreakSecondsRemaining)} LEFT" +
                    " · SESSION ${formatSessionDuration(spanRemainingSeconds)} LEFT"
            totalStudyBlocks > 1 ->
                "BLOCK $currentBlockIndex/$totalStudyBlocks" +
                    " · ${formatSessionDuration(remainingSeconds)} LEFT IN BLOCK" +
                    " · SESSION ${formatSessionDuration(spanRemainingSeconds)} LEFT"
            else -> "SESSION ${formatSessionDuration(spanRemainingSeconds)} LEFT"
        }
}

/**
 * #127: one duration print for glance surfaces. Under an hour it is
 * MM:SS; at an hour or more it is H:MM:SS — never "170:00", which a
 * glance reads as a broken clock, not as 2 hours 50 minutes.
 */
fun formatSessionDuration(totalSeconds: Int): String {
    val s = totalSeconds.coerceAtLeast(0)
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return if (h > 0) String.format(java.util.Locale.US, "%d:%02d:%02d", h, m, sec)
    else String.format(java.util.Locale.US, "%02d:%02d", m, sec)
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

/**
 * #110 (REISE's priced cure, folded into MIGRATION_12_13 by the
 * batch-10 train): one accrual segment of a task — [seconds] of
 * wall time credited to [taskId] during [sessionId], filed under
 * [dateIso], the Drawer's filing day (DrawerDay) on which the
 * credit was observed. Segments are append-only sums: repeated
 * credits for the same (task, session, day) accumulate into one
 * row (the DAO's upsert). The credit a segment records is the
 * SAME observed credit that lands in FocusTaskEntity's lifetime
 * scalar, written in the same branch of accrueActiveTaskSeconds —
 * so for accrual after v13 the segments sum to the scalar by
 * construction. Accrual from before v13 has NO segments and can
 * never get any: it stays lifetime-only (printed TOTAL), never
 * retro-split. A credit whose span straddles midnight files
 * whole under its observation day.
 */

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

@Entity(
    tableName = "task_accrual_segments",
    indices = [Index(value = ["taskId", "sessionId", "dateIso"], unique = true)]
)
data class TaskAccrualSegmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int,
    val sessionId: Int,
    val dateIso: String,
    val seconds: Int
)

@Entity(
    tableName = "intercept_logs",
    // #30 F1 (train inheritance): intercept history reads are
    // time-ordered — the timestamp stops being a full scan.
    indices = [Index(value = ["timestampMillis"])]
)
data class InterceptLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestampMillis: Long = System.currentTimeMillis(),
    val packageName: String,
    val appName: String,
    val sessionIntention: String,
    val outcome: String,
    // FIX-3a: the "Habit" default dies — a log row nobody
    // explained books UNSTATED, never a fabricated reason.
    val triggerReason: String = "UNSTATED",
    // Issue #324 finding 3 (build half): the TYPED event, stored
    // as enum NAMEs beside the legacy words (migration 18→19).
    // Writers compute them at the write via InterceptOutcomeKind
    // .fromStoredWord / InterceptTriggerKind.derive; rows that
    // predate the typing carry UNKNOWN and derive on read below.
    val outcomeKind: String = InterceptOutcomeKind.UNKNOWN.name,
    val triggerKind: String = InterceptTriggerKind.UNKNOWN.name
) {
    /** This row's typed outcome: the persisted kind, falling
     *  back to derivation from the legacy word for rows written
     *  before the typing landed (their column reads UNKNOWN). */
    val outcomeKindTyped: InterceptOutcomeKind
        get() = InterceptOutcomeKind.fromKindName(outcomeKind)
            .takeIf { it != InterceptOutcomeKind.UNKNOWN }
            ?: InterceptOutcomeKind.fromStoredWord(outcome)

    /** This row's typed trigger, same fallback as above. */
    val triggerKindTyped: InterceptTriggerKind
        get() = InterceptTriggerKind.fromKindName(triggerKind)
            .takeIf { it != InterceptTriggerKind.UNKNOWN }
            ?: InterceptTriggerKind.derive(triggerReason, packageName)
}

@Entity(tableName = "captured_notifications")
data class CapturedNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestampMillis: Long = System.currentTimeMillis(),
    val packageName: String,
    val appName: String,
    // Issue #138 CUT (Sarthak's ruling, Oct 30): notification-CONTENT
    // storage is removed, not repaired — no senderTitle, no
    // messageBody is stored anywhere. A held row is metadata only:
    // which app, and when it was held. The digest prints exactly
    // that; the message itself is never transcribed to disk.
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
        "youtube.com/shorts",
        // W3 #130 (browser-as-loophole): the whole YouTube host, not
        // only its /shorts path — the home/watch feed was the biggest
        // unlisted feed. Enforced by the same site blocker; a needed
        // video is reachable through the domain-scoped site peek.
        "youtube.com"
    )
    // Issue #133 (finding 4): an "allowed study sites" example
    // list used to live here as dead decoration — no study-site
    // allowlist exists anywhere in enforcement (the browser
    // blocker is blocklist-only and reads the user's own blocked
    // list), and nothing in production consumed the examples.
    // Removed rather than left lying on the surface; a real
    // study-site allowlist is a feature decision, not a constant.

    /**
     * Normalises free-typed input ("https://www.YouTube.com/shorts?x=1")
     * into a stored domain ("youtube.com/shorts"). Null = not a usable site.
     *
     * Issue #79 (Audit R2, url-blocker-bypasses #4): the authority is
     * parsed, not just prefix-stripped — userinfo ("user:pass@"),
     * ports (":8443") and trailing dots ("instagram.com.") are
     * stripped, so an accepted entry can always match a real visit.
     * Before this, all three were stored verbatim and could never be
     * a substring of the real URL-bar text: a rule the user believed
     * blocked a site that was wide open. The path is kept (the
     * "youtube.com/shorts" form above is a real, shipped rule), with
     * the same authority canonicalisation applied in front of it.
     */
    fun normalizeDomain(raw: String): String? {
        var d = raw.trim().lowercase(java.util.Locale.ROOT)
        if (d.isEmpty() || d.contains(' ')) return null
        d = d.removePrefix("https://").removePrefix("http://").removePrefix("//")
        d = d.substringBefore('?').substringBefore('#')
        if (d.isEmpty()) return null
        val slash = d.indexOf('/')
        val authority = if (slash >= 0) d.substring(0, slash) else d
        val path = if (slash >= 0) d.substring(slash + 1).trim().trimEnd('/') else ""
        // Authority canonicalisation: drop userinfo and port, then a
        // trailing dot (FQDN root label), then one leading "www.".
        var host = authority.substringAfterLast('@')
        host = host.substringBefore(':')
        host = host.trim().trimEnd('.').removePrefix("www.").trimEnd('.')
        if (host.isEmpty() || !host.contains('.')) return null
        if (host.split('.').any { it.isEmpty() }) return null
        return if (path.isEmpty()) host else "$host/$path"
    }
}

/** One user-editable blocked website — the list the browser blocker enforces (Ren). */
@Entity(tableName = "blocked_sites")
data class BlockedSiteEntity(
    @PrimaryKey val domain: String,
    // Audit R6 #340 F10 (12→13): where a blocked visit is SENT
    // instead of dead-ending at BACK + the Airlock wall — a
    // per-site redirect destination (a full URL the user chose).
    // '' = no destination: the classic BACK + Airlock stands.
    val redirectTarget: String = ""
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
) {
    /** Issue #32 F2 (write-boundary + derive-point cure): minutes
     *  wrapped into 0..1439 and the days mask masked to its 7
     *  lawful bits (bit 0 = Mon … bit 6 = Sun). A stored daysMask
     *  of -1 used to mean "every day, plus garbage bits"; masked,
     *  it means exactly every day. Applied at the updateRoutine
     *  write funnel and inside RoutineSchedule's derivations —
     *  never in init{}, so Room can always load a legacy row. */
    fun sanitized(): RoutineEntity = copy(
        startMinutes = ((startMinutes % 1440) + 1440) % 1440,
        endMinutes = ((endMinutes % 1440) + 1440) % 1440,
        daysMask = daysMask and RoutineSchedule.ALL_DAYS
    )
}

/** Pure window math for routines. daysMask: bit 0 = Monday … bit 6 = Sunday. */
object RoutineSchedule {
    const val DAY_MINUTES = 1440
    const val ALL_DAYS = 127
    const val MON_FRI = 31

    fun durationMinutes(routine: RoutineEntity): Int {
        // Issue #32 F2: derivations read the sanitized row (wrapped
        // minutes, 7-bit mask) — a corrupt stored row prices a sane
        // window instead of crashing or spanning negative time.
        val r = routine.sanitized()
        var d = r.endMinutes - r.startMinutes
        if (d <= 0) d += DAY_MINUTES
        // Routines rebuild: the clamp unifies at 30..1080 with
        // adjustRoutineTime's editor clamp (Sten §1.9) — printed
        // duration ≡ raw span for every storable routine, and
        // DAY BLOCKS' 630 minutes sits mid-range. Was 15..720.
        return d.coerceIn(30, 1080)
    }

    private fun bitFor(calendar: java.util.Calendar): Int =
        (calendar.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7

    /** Audit R5 #259 (R5-0266): a window's end is a WALL-CLOCK time,
     *  never start + elapsed duration. A routine stores wall-clock
     *  start/end minutes; pricing the end as startMillis + durMs
     *  made overnight windows end an hour late on spring-forward
     *  nights and an hour early on fall-back nights. The end is the
     *  start's local date-time plus the duration on the LOCAL
     *  timeline, resolved in the system zone — identical to the old
     *  arithmetic on every non-transition day. */
    private fun wallEndMillis(startMillis: Long, durationMinutes: Int): Long {
        val zone = java.time.ZoneId.systemDefault()
        val startLocal = java.time.LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(startMillis), zone
        )
        return startLocal.plusMinutes(durationMinutes.toLong())
            .atZone(zone).toInstant().toEpochMilli()
    }

    private fun atMinutes(day: java.util.Calendar, minutes: Int): Long {
        // Audit R6 #341 F29: java.time resolution, the same rules
        // MonolithWeek and DrawerDay already live by — this was the
        // one schedule surface still on lenient Calendar, which
        // silently shifted gap starts and dropped the second
        // occurrence of repeated-hour windows without a word.
        // Stated policy: a start inside a spring-forward gap fires
        // at the first valid instant after the gap (atZone's forward
        // shift); a start inside a fall-back repeated hour fires at
        // its FIRST occurrence (atZone's earlier-offset choice) and
        // the window's elapsed duration runs from there.
        val zone = java.time.ZoneId.of(day.timeZone.id)
        val date = java.time.LocalDate.of(
            day.get(java.util.Calendar.YEAR),
            day.get(java.util.Calendar.MONTH) + 1,
            day.get(java.util.Calendar.DAY_OF_MONTH)
        )
        return date.atTime(minutes / 60, minutes % 60).atZone(zone)
            .toInstant().toEpochMilli()
    }

    /** Next window start strictly after [nowMillis]; null if no days selected. */
    fun nextStartMillis(routine: RoutineEntity, nowMillis: Long): Long? {
        val r = routine.sanitized()
        if (r.daysMask == 0) return null
        val day = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        repeat(8) {
            if (r.daysMask and (1 shl bitFor(day)) != 0) {
                val start = atMinutes(day, r.startMinutes)
                if (start > nowMillis) return start
            }
            day.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        return null
    }

    /**
     * The pause-aware next start (routines rebuild §3.6): the
     * search origin is max(now, pausedUntilMillis), fed into
     * [nextStartMillis] - whose strictly-after contract means the
     * origin is passed as pausedUntilMillis - 1 when the pause is
     * the binding origin, so a window starting exactly as the
     * pause ends still counts. Windows the pause covers never
     * fire, so they are never the next start. This one helper is
     * what the scheduler and every "next seal" reader (Deck
     * ribbon, widget) share, so they can never disagree.
     *
     * (Composed at the e1996c16 merge on the #152 branch: main's
     * §3.6 helper supersedes the pause-stepping version this
     * branch's fix-forward 2 added when no definition existed —
     * same contract, one shared implementation.)
     */
    fun effectiveNextStartMillis(routine: RoutineEntity, nowMillis: Long): Long? {
        val r = routine.sanitized()
        val origin = maxOf(nowMillis, r.pausedUntilMillis)
        return if (origin > nowMillis) {
            nextStartMillis(r, origin - 1L)
        } else {
            nextStartMillis(r, nowMillis)
        }
    }

    /**
     * Issue #155 (T9, re-close bench): the REFUSAL-AWARE next
     * start — [effectiveNextStartMillis] (pause-aware) walked past
     * every candidate window the machine itself would refuse to
     * materialise. The heartbeat refuses a window on an excused
     * date (ILL / REST / FAMILY day-word, filed under the window's
     * filing date) and a tombstoned window (an annulled proving);
     * a "next routine" claim derived without those two inputs
     * names seals that will never happen — JUDGE-1's Reason-6
     * premise ("stays true until a routine changes") falsified on
     * exactly this point. This walk consumes the SAME refusal set
     * the heartbeat's hand checks (RoutineHeartbeat: excused isos
     * via DrawerDay.filingDate + dateIso; tombstones via
     * SessionSealTransitions.isTombstoned), so a glance derived
     * here can never disagree with the funnel.
     *
     * Bounded: at most [MAX_REFUSAL_WALK] refused candidates are
     * stepped over; a routine whose every window inside that
     * horizon is refused yields null, and the caller falls silent
     * (WING-D C1: the line stands only where it names a real,
     * refusal-aware next seal).
     */
    fun nextStartMillisRefusalAware(
        routine: RoutineEntity,
        nowMillis: Long,
        excusedDateIsos: Set<String>,
        isTombstoned: (routineId: Int, windowStartMillis: Long) -> Boolean
    ): Long? {
        var candidate = effectiveNextStartMillis(routine, nowMillis) ?: return null
        repeat(MAX_REFUSAL_WALK) {
            val refused =
                DrawerDay.dateIso(DrawerDay.filingDate(candidate)) in excusedDateIsos ||
                    isTombstoned(routine.id, candidate)
            if (!refused) return candidate
            candidate = effectiveNextStartMillis(routine, candidate) ?: return null
        }
        return null
    }

    /** How many refused candidates [nextStartMillisRefusalAware]
     *  steps over before falling silent — a fortnight of daily
     *  windows, or ~4 months of weekly ones. */
    const val MAX_REFUSAL_WALK = 16

    /** End of the window containing [nowMillis], or of the next window if none contains it. */
    fun nextEndMillis(routine: RoutineEntity, nowMillis: Long): Long? {
        val r = routine.sanitized()
        if (r.daysMask == 0) return null
        val durMin = durationMinutes(r)
        val day = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        // A window that started yesterday may still be running (overnight).
        day.add(java.util.Calendar.DAY_OF_YEAR, -1)
        repeat(9) {
            if (r.daysMask and (1 shl bitFor(day)) != 0) {
                val start = atMinutes(day, r.startMinutes)
                val end = wallEndMillis(start, durMin)
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
        val r = routine.sanitized()
        if (r.daysMask == 0) return null
        val durMin = durationMinutes(r)
        val day = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        day.add(java.util.Calendar.DAY_OF_YEAR, -1)
        repeat(2) {
            if (r.daysMask and (1 shl bitFor(day)) != 0) {
                val start = atMinutes(day, r.startMinutes)
                val end = wallEndMillis(start, durMin)
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

    /**
     * Audit R5 #306 F2: is this routine ENFORCING at [nowMillis] —
     * enabled, not paused, and a window contains the instant. While
     * this holds the routine is forced-on: the editor's off-switch
     * and current-day removal are refused in MonolithViewModel, the
     * forced state StayFree's scheduled modes carry (its
     * focus_mode_forced_* strings exist to refuse the same tap).
     * A paused routine is NOT enforcing — the pause was already
     * bought at the pause's own price — so it stays editable, and a
     * disabled routine enforces nothing by definition.
     */
    fun isEnforcingAt(routine: RoutineEntity, nowMillis: Long): Boolean {
        val r = routine.sanitized()
        if (!r.enabled) return false
        if (r.pausedUntilMillis > nowMillis) return false
        return windowContaining(r, nowMillis) != null
    }

    /** The next window to start strictly after [nowMillis], as an instance. */
    fun nextWindow(routine: RoutineEntity, nowMillis: Long): WindowInstance? {
        val start = nextStartMillis(routine, nowMillis) ?: return null
        val end = wallEndMillis(start, durationMinutes(routine))
        return WindowInstance(
            startMillis = start,
            endMillis = end,
            remainderMinutes = ((end - nowMillis) / 60_000L).toInt(),
            driftMinutes = 0
        )
    }

    /** #153 F2/F3 — the pause horizon for "skip today": the end of
     *  the window containing [nowMillis], or of the next window if
     *  none contains it. Pausing until this instant forgives exactly
     *  that one window (judgement J6: a pause-covered window is
     *  never a miss) and no other day — the weekly mask is untouched. */
    fun skipWindowEndMillis(routine: RoutineEntity, nowMillis: Long): Long? =
        windowContaining(routine, nowMillis)?.endMillis
            ?: nextWindow(routine, nowMillis)?.endMillis

    /**
     * The most recent window whose end is at or before [nowMillis],
     * searching back up to 8 start-days. One routine's windows never
     * overlap (starts are 24h apart, spans ≤ 18h), so the first hit
     * scanning backwards is the latest closed window.
     */
    fun latestClosedWindow(routine: RoutineEntity, nowMillis: Long): WindowInstance? {
        val r = routine.sanitized()
        if (r.daysMask == 0) return null
        val durMin = durationMinutes(r)
        val day = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        repeat(9) {
            if (r.daysMask and (1 shl bitFor(day)) != 0) {
                val start = atMinutes(day, r.startMinutes)
                val end = wallEndMillis(start, durMin)
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
     * LIVE (UNA-09, Truth Car 3): a routine the scheduler will ever
     * fire — switched on AND carrying at least one day. This is the
     * ONE definition every reader shares: the chain-alarm derivation
     * (RoutineDerive.nextBoundary skips non-live routines) and
     * Shield's "n ARMED" header both read it, so the printed count
     * can never again include a daysMask == 0 routine the scheduler
     * silently skips.
     */
    fun isLive(routine: RoutineEntity): Boolean =
        routine.enabled && routine.sanitized().daysMask != 0

    /** How many of [routines] are LIVE — Shield's "n ARMED" number
     *  (R-6: ARMED, not ACTIVE — a live routine is armed to fire,
     *  it is not presently blocking). */
    fun liveCount(routines: List<RoutineEntity>): Int =
        routines.count { isLive(it) }

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
    val shortsReelsSniperEnabled: Boolean = true,
    // Batch 10 (12→13, Frist — truth-3-times): the peek/break
    // lengths become settings the lease and the break actually
    // read, replacing the hardcoded 2 / 10 minutes. Bounds live
    // in the setters (peek 0–30, break 0–60, clamped on write),
    // not the schema. peekMinutes = 0 means PEEK IS OFF.
    val peekMinutes: Int = 2,
    val breakMinutes: Int = 10,
    // Audit R6 #340 F68 (12→13): data consent for OTHER APPS'
    // notification bodies, kept SEPARATE from the listener grant.
    // The grant answers "may Monolith hold notifications"; this
    // answers "may it KEEP the bodies". Values: UNSET / DECLINED /
    // GRANTED. Anything but GRANTED stores sender-and-app lines
    // only — the body is blanked at capture, never written — so
    // the default posture is the private one and keeping bodies
    // is an explicit, revocable choice of the user's.
    val notificationBodyConsent: String = "UNSET"
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

