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

enum class FrictionTier(
    val code: String,
    val title: String,
    val subtitle: String,
    val badgeLabel: String
) {
    MINDFUL_PAUSE(
        code = "Standard",
        title = "Standard Mode",
        subtitle = "A calm 5-second hold before ending a session early. Ideal for daily study blocks.",
        badgeLabel = "Standard • 5s Hold"
    ),
    KINETIC_CIPHER(
        code = "Deep Lock",
        title = "Deep Lock",
        subtitle = "Requires aligning dual harmonic sliders or typing a confirmation phrase to end early.",
        badgeLabel = "Deep Lock • Alignment"
    ),
    IRON_MONOLITH(
        code = "Exam Lock",
        title = "Exam Lock",
        subtitle = "Maximum commitment. Requires a continuous 8-second hold + confirmation to stop early.",
        badgeLabel = "Exam Lock • 8s Hold"
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

@Entity(tableName = "focus_tasks")
data class FocusTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val notesOrSubsteps: String = "",
    val estimatedMinutes: Int = 25,
    val actualSecondsSpent: Int = 0,
    val categoryTag: String = "Study",
    val queueOrder: Int = 0,
    val status: String = TaskQueueStatus.QUEUED.name,
    val completedAtMillis: Long = 0L,
    val completedInSessionId: Int = 0
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
    SOCIAL("Social Feeds", "Social"),
    GAMES("Games", "Games"),
    MESSAGING("Messaging", "Chat"),
    BROWSERS("Web & News", "Web"),
    SHOPPING("Utility & Docs", "Tools")
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
    val frictionTier: String,
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
    val frictionTier: String,
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
    val penaltySecondsAdded: Int = 0,
    val isActive: Boolean,
    val completedSuccessfully: Boolean,
    val phantomReachesBlocked: Int = 0,
    val tamperDetected: Boolean = false,
    val forgedArtifactId: String? = null,
    val completedTasksSummary: String = "",
    val tasksCompletedCount: Int = 0
) {
    val remainingSeconds: Int
        get() = (targetDurationSeconds - elapsedSeconds).coerceAtLeast(0)
}

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

enum class ArtifactGeometry {
    LISSAJOUS_CORE,
    TORSION_GYRO,
    HYPERBOLIC_LOOM,
    KEPLER_ASTROLABE,
    ACOUSTIC_PENDULUM,
    OBSIDIAN_TESSERACT
}

data class KineticArtifact(
    val id: String,
    val serialCode: String,
    val name: String,
    val subtitle: String,
    val lore: String,
    val geometry: ArtifactGeometry,
    val requiredFocusMinutes: Int,
    val requiredInterceptions: Int,
    val freqA: Float,
    val freqB: Float,
    val defaultPhase: Float
)

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
}

object CurioCatalog {
    val artifacts = listOf(
        KineticArtifact(
            id = "artifact_lissajous",
            serialCode = "Toy 01 • 25m",
            name = "Harmonic Loop",
            subtitle = "Interactive Sound Wave Spinner",
            lore = "Unlocked after your first 25-minute Pomodoro block. Drag across the screen to bend its wave pattern during a 5-minute break.",
            geometry = ArtifactGeometry.LISSAJOUS_CORE,
            requiredFocusMinutes = 25,
            requiredInterceptions = 1,
            freqA = 3f,
            freqB = 2f,
            defaultPhase = 0.5f
        ),
        KineticArtifact(
            id = "artifact_gyro",
            serialCode = "Toy 02 • 45m",
            name = "Brass Gyroscope",
            subtitle = "4-Ring Orbiting Desk Toy",
            lore = "Four concentric rings that tilt and spin under your thumb. Unlocked after 45 minutes of focus.",
            geometry = ArtifactGeometry.TORSION_GYRO,
            requiredFocusMinutes = 45,
            requiredInterceptions = 2,
            freqA = 4f,
            freqB = 3f,
            defaultPhase = 1.1f
        ),
        KineticArtifact(
            id = "artifact_loom",
            serialCode = "Toy 03 • 60m",
            name = "String Prism",
            subtitle = "Geometric Tension Web",
            lore = "Made entirely from straight lines that form a curved prism as you rotate it. Unlocked after 1 hour of total focus.",
            geometry = ArtifactGeometry.HYPERBOLIC_LOOM,
            requiredFocusMinutes = 60,
            requiredInterceptions = 3,
            freqA = 5f,
            freqB = 4f,
            defaultPhase = 0.8f
        ),
        KineticArtifact(
            id = "artifact_kepler",
            serialCode = "Toy 04 • 120m",
            name = "Pocket Planetarium",
            subtitle = "Solar Orbit Geometric Spirograph",
            lore = "Traces the geometric flower pattern formed by two planets in orbit. Unlocked after 2 hours of focused study.",
            geometry = ArtifactGeometry.KEPLER_ASTROLABE,
            requiredFocusMinutes = 120,
            requiredInterceptions = 5,
            freqA = 7f,
            freqB = 5f,
            defaultPhase = 1.57f
        ),
        KineticArtifact(
            id = "artifact_pendulum",
            serialCode = "Toy 05 • 200m",
            name = "Sand Pendulum",
            subtitle = "Soothing Spiral Harmonograph",
            lore = "Like a heavy brass pendulum swinging over sand on a desk. Unlocked after 200 minutes of focus.",
            geometry = ArtifactGeometry.ACOUSTIC_PENDULUM,
            requiredFocusMinutes = 200,
            requiredInterceptions = 8,
            freqA = 6f,
            freqB = 5f,
            defaultPhase = 2.1f
        ),
        KineticArtifact(
            id = "artifact_tesseract",
            serialCode = "Toy 06 • 300m",
            name = "The Monolith Cube",
            subtitle = "4D Folding Geometry",
            lore = "Awarded for 5 hours of deep focus. Drag to fold and rotate all 16 vertices across space.",
            geometry = ArtifactGeometry.OBSIDIAN_TESSERACT,
            requiredFocusMinutes = 300,
            requiredInterceptions = 12,
            freqA = 8f,
            freqB = 7f,
            defaultPhase = 3.14f
        )
    )
}

