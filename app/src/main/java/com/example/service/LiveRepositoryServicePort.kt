package com.example.service

import android.content.Context
import com.example.data.RepositoryServicePort
import com.example.data.RoutineEntity

/**
 * ISSUE-67 — the LIVE implementation of the repository's service
 * port. This file lives on the service side of the line and is the
 * one place allowed to touch the concrete services on the data
 * layer's behalf, in the `RepositoryArmSessionPort` precedent's
 * shape. Every body is a verbatim forwarding of the call the
 * repository used to make itself, so behaviour is unchanged —
 * only the direction of the compile-time dependency moved:
 * service -> data (the port), never data -> concrete service.
 *
 * The break-latch engine is lazy PER PORT INSTANCE, exactly as it
 * was lazy per repository: born only in a process that actually
 * crosses a break, born from the persisted sound fact (the
 * engine's own initializer reads it), and registered for the
 * toggle's live pushes.
 */
class LiveRepositoryServicePort : RepositoryServicePort {

    override val accessibilityBound: Boolean
        get() = MonolithAccessibilityService.instance != null

    override val notificationListenerBound: Boolean
        get() = MonolithNotificationListener.instance != null

    override val shieldProtecting: Boolean
        get() = MonolithShieldService.protecting

    override fun startShield(context: Context) =
        MonolithShieldService.startShield(context)

    override fun cancelLegacyRoutineAlarms(context: Context, routineIds: List<Int>) =
        RoutineScheduler.cancelLegacyRoutineAlarms(context, routineIds)

    override fun cancelRoutineChain(context: Context) =
        RoutineScheduler.cancelChain(context)

    override fun rearmRoutineChain(context: Context, routines: List<RoutineEntity>, nowMillis: Long) =
        RoutineScheduler.rearmChain(context, routines, nowMillis)

    private var breakSoundEngine: TactileSoundEngine? = null

    private fun engine(context: Context): TactileSoundEngine =
        breakSoundEngine ?: TactileSoundEngine(context.applicationContext)
            .also { breakSoundEngine = it }

    override fun playBreakLatch(context: Context, engaged: Boolean, unattended: Boolean) {
        engine(context).playMechanicalLatch(engaged, unattended)
    }

    override fun playBreakEndSummons(context: Context) {
        engine(context).playBreakEndSummons()
    }

    override fun playBreakWarningKnock(context: Context) {
        engine(context).playBreakWarningKnock()
    }
}
