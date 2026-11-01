package com.example.service

import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.data.breakRemainingAfter

/**
 * The pill's face — ONE derivation with two mouths (PILL-AND-TIMES
 * §2.1, §2.3): the overlay window (Route A) and the shield
 * notification's peek face (Route B) both print what this function
 * decides, so the glass and the shade can never tell two times.
 *
 * The derivation is a pure function of STORED ENDS and the current
 * monotonic instant — never a counted tick. It replaces the dead
 * contract's state (FloatingPeekPillState in the ViewModel counts
 * 1 Hz decrements in a process the lease does not live in; the two
 * clocks could disagree and nothing rendered either of them). Here
 * there is exactly one clock — the caller's `nowMono` — and every
 * number below is recomputed from the Room rows at that instant:
 * sample it at any moment and the face equals the stored ends at
 * that moment, by construction.
 */
sealed interface PillFace {

    /** A string that changes exactly when the printed face changes.
     *  The controller touches the window only when this moves. */
    val renderKey: String

    data object Absent : PillFace {
        override val renderKey: String = "absent"
    }

    /** Timed: [remainingSeconds] is the Now Frame's per-block
     *  remaining; stopwatch: [elapsedSeconds] is the elapsed count. */
    data class Session(
        val intention: String,
        val isStopwatch: Boolean,
        val remainingSeconds: Int,
        val elapsedSeconds: Int,
        val blockIndex: Int,
        val blockCount: Int
    ) : PillFace {
        override val renderKey: String =
            "session:$isStopwatch:$remainingSeconds:$elapsedSeconds:$blockIndex/$blockCount"
    }

    data class Break(val remainingSeconds: Int) : PillFace {
        override val renderKey: String = "break:$remainingSeconds"
    }

    data class Peek(
        val packageName: String,
        val appName: String,
        val remainingSeconds: Int
    ) : PillFace {
        override val renderKey: String = "peek:$packageName:$remainingSeconds"
    }
}

object PillDerivation {

    /** §2.4 Q4: while the in-call face is foreground the pill reads
     *  ABSENT — a human voice outranks a fuse. The telecom package
     *  family joins own-package in the foreground test. */
    val TELECOM_FOREGROUND_PACKAGES: Set<String> = setOf(
        "com.android.incallui",
        "com.google.android.dialer",
        "com.samsung.android.incallui",
        "com.samsung.android.dialer",
        "com.android.dialer"
    )

    /**
     * §2.1's derivation, in its priority order. [foregroundPkg] is
     * the caller's best read of the foreground package (null = no
     * read; the controller resolves that case before calling — see
     * PillController). [keyguardLocked] is §2.4 Q2: the pill draws
     * over apps, never over the system's lock face.
     */
    fun derive(
        session: FocusSessionEntity?,
        appRows: List<BlockedAppEntity>,
        nowMono: Long,
        foregroundPkg: String?,
        ownPackage: String,
        keyguardLocked: Boolean = false
    ): PillFace {
        if (session == null || !session.isActive) return PillFace.Absent
        if (keyguardLocked) return PillFace.Absent
        if (foregroundPkg != null &&
            (foregroundPkg == ownPackage || foregroundPkg in TELECOM_FOREGROUND_PACKAGES)
        ) {
            return PillFace.Absent
        }

        // BREAK outranks PEEK: during a break the PEEK_2M doors stand
        // open without any lease, so a lease countdown would print a
        // fuse on an open door — a false urgency. The break's clock
        // is the true one, read off its stored (remaining, lastTick)
        // pair by the house derivation the progress machine uses.
        if (session.isInSafeBreak) {
            return PillFace.Break(
                remainingSeconds = breakRemainingAfter(
                    storedRemaining = session.safeBreakSecondsRemaining,
                    lastTickMonoMillis = session.lastTickElapsedRealtimeMillis,
                    nowMonoMillis = nowMono
                )
            )
        }

        // One live lease is enforced at grant (startPeekLease zeroes
        // every other row), so at most one row can hold a future end;
        // maxBy stays as the belt over that braces.
        val liveLease = appRows
            .filter { it.activePeekUntilElapsedRealtime > nowMono }
            .maxByOrNull { it.activePeekUntilElapsedRealtime }
        if (liveLease != null) {
            // Ceiling, not floor: the fuse reads its last second as
            // 1 and dies at the end instant, never a beat early.
            val remainingMs = liveLease.activePeekUntilElapsedRealtime - nowMono
            return PillFace.Peek(
                packageName = liveLease.packageName,
                appName = liveLease.appName,
                remainingSeconds = ((remainingMs + 999L) / 1000L).toInt()
            )
        }

        // SESSION: elapsed read off the same stored (elapsed, lastTick)
        // pair the break uses — the value the progress machine's next
        // write will persist, recomputed at this instant instead of
        // waited for. A reboot signature (now behind the stored tick)
        // credits nothing, exactly as breakRemainingAfter rules.
        val elapsedNow = elapsedAt(session, nowMono)
        return if (session.isStopwatchMode) {
            PillFace.Session(
                intention = session.intention,
                isStopwatch = true,
                remainingSeconds = 0,
                elapsedSeconds = elapsedNow,
                blockIndex = session.currentBlockIndex,
                blockCount = session.totalStudyBlocks
            )
        } else {
            PillFace.Session(
                intention = session.intention,
                isStopwatch = false,
                remainingSeconds =
                    (session.effectiveTargetSeconds - elapsedNow).coerceAtLeast(0),
                elapsedSeconds = elapsedNow,
                blockIndex = session.currentBlockIndex,
                blockCount = session.totalStudyBlocks
            )
        }
    }

    /** Whole seconds accrued since the row's stored tick, credited
     *  onto the stored elapsed. Mirrors breakRemainingAfter's law:
     *  an un-stamped tick (0 — a row written before its first
     *  progress pass) credits nothing beyond the stored fact. */
    private fun elapsedAt(session: FocusSessionEntity, nowMono: Long): Int {
        val lastTick = session.lastTickElapsedRealtimeMillis
        if (lastTick <= 0L) return session.elapsedSeconds
        val deltaSeconds = ((nowMono - lastTick) / 1000L).coerceAtLeast(0L)
        return session.elapsedSeconds + deltaSeconds.toInt()
    }
}
