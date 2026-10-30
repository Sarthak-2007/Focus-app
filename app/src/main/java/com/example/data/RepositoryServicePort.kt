package com.example.data

import android.content.Context

/**
 * ISSUE-67 (Audit R1 · architecture, finding 2) — THE REPOSITORY'S
 * SERVICE PORT.
 *
 * Before this file, the data layer imported and drove CONCRETE
 * Android services: it read `MonolithAccessibilityService.instance`
 * and `MonolithShieldService.protecting` directly, called
 * `MonolithShieldService.startShield`, drove `RoutineScheduler`'s
 * chain, and lazily constructed a `TactileSoundEngine` — while the
 * service layer constructed the repository right back. That is the
 * circular data<->service edge the audit filed as CRITICAL.
 *
 * The pattern is the codebase's own: `ArmSessionPort` /
 * `RepositoryArmSessionPort` (ui layer) already proved that a
 * consumer holding only an interface, with the concrete-service
 * implementation living on the service side of the line, is how
 * this app severs such an edge without changing behaviour.
 *
 * This port is everything the repository needs FROM the living
 * services, stated as facts and verbs in the data layer. The
 * production implementation is
 * `com.example.service.LiveRepositoryServicePort`, which is the
 * one file allowed to touch the concrete services on the
 * repository's behalf; it is wired in by
 * `com.example.AppContainer`, the single construction point.
 *
 * What deliberately does NOT cross this port:
 *  - the WatchStore models (Warrant / UsageAccess / WatchItemId):
 *    the repository only *reads derived facts* through WatchStore's
 *    pure derivation, it does not drive a service there. Those
 *    types living in the service package is finding 4's
 *    junk-drawer debt, recorded in the issue-67 report — moving
 *    them is a separate, wider rename, not this seam.
 *  - `MonolithDeviceAdminReceiver::class.java`: a ComponentName
 *    identity for a platform query (isAdminActive), not a driven
 *    service; the receiver is never called, started, or read.
 */
interface RepositoryServicePort {

    /** True while the accessibility service instance is bound. */
    val accessibilityBound: Boolean

    /** True while the notification-listener instance is bound. */
    val notificationListenerBound: Boolean

    /** True once the shield's monitor loop is protecting a session. */
    val shieldProtecting: Boolean

    /** Ask the shield service to start (foreground) for a session. */
    fun startShield(context: Context)

    /** Cancel the shipped legacy per-routine alarms by id. */
    fun cancelLegacyRoutineAlarms(context: Context, routineIds: List<Int>)

    /** Cancel the single routine chain alarm. */
    fun cancelRoutineChain(context: Context)

    /** Rearm the routine chain at the next boundary of [routines]. */
    fun rearmRoutineChain(context: Context, routines: List<RoutineEntity>, nowMillis: Long)

    /** Sound the break-boundary latch on the sound engine.
     *  [unattended] carries #150 F1/F2's binding through the port:
     *  machinery-fired voices are spoken as unattended (the
     *  ringer's word and Do Not Disturb bind them in the engine). */
    fun playBreakLatch(context: Context, engaged: Boolean, unattended: Boolean = false)

    /** Sound the break-END summons (Audit R3b #146: the exit voice,
     *  distinct from the entry latch). */
    fun playBreakEndSummons(context: Context)

    /** Sound the T-minus-60 break warning knock (#146). */
    fun playBreakWarningKnock(context: Context)
}

/**
 * The unbound port: every fact false, every verb a no-op. This is
 * the repository's default so its many unit-test constructions —
 * which never ran a living service — keep exactly the semantics
 * they always had in a service-less process (the old concrete
 * calls were themselves try/catch "safe ignore in test
 * environments"). Production NEVER uses this: AppContainer always
 * wires the live port.
 */
object UnboundRepositoryServicePort : RepositoryServicePort {
    override val accessibilityBound: Boolean = false
    override val notificationListenerBound: Boolean = false
    override val shieldProtecting: Boolean = false
    override fun startShield(context: Context) = Unit
    override fun cancelLegacyRoutineAlarms(context: Context, routineIds: List<Int>) = Unit
    override fun cancelRoutineChain(context: Context) = Unit
    override fun rearmRoutineChain(context: Context, routines: List<RoutineEntity>, nowMillis: Long) = Unit
    override fun playBreakLatch(context: Context, engaged: Boolean, unattended: Boolean) = Unit
    override fun playBreakEndSummons(context: Context) = Unit
    override fun playBreakWarningKnock(context: Context) = Unit
}
