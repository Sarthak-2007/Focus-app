package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.MonolithDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Direct-Boot Persistence:
 * Listens for LOCKED_BOOT_COMPLETED as well as BOOT_COMPLETED so active focus locks are
 * enforced immediately upon device reboot before the user even unlocks their lock screen.
 *
 * Routines rebuild: boot actions stamp lastBootAtMillis ONCE per real
 * boot (LOCKED_BOOT and BOOT both fire for one boot — the stamp is
 * boot-scoped, so BOOT_COMPLETED does not overwrite it), then hand
 * everything to the heartbeat: the tick reconciles whatever the
 * downtime owed and rearms the chain (alarms die at reboot). Clock
 * changes route here too — a moved clock invalidates every armed
 * boundary, and only a full reconcile re-derives the truth.
 *
 * Issue #44 hardening: the async work runs on a supervised scope
 * with an exception handler, each action group is caught separately
 * (a failure in one group never cancels or skips the others), and
 * goAsync's pending result is finished exactly once, in a finally.
 * The shield is raised BEFORE the heartbeat tick when a session is
 * already active — the tick is the slow step, and a slow or failed
 * tick must not leave an active session unshielded — and again after
 * the tick for whatever session remains active then.
 */
class MonolithBootReceiver : BroadcastReceiver() {

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        // Last line of defence only: every action group below already
        // catches its own failures. An exception reaching this handler
        // must still never crash the process from inside goAsync's
        // window — the failure is contained here, silently, matching
        // the codebase's safe-ignore convention for receiver work.
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val appContext = context.applicationContext
        when (action) {
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON" -> {
                val pendingResult = goAsync()
                scope.launch {
                    try {
                        runBootActions(appContext, action)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
            Intent.ACTION_USER_UNLOCKED,
            "android.intent.action.REBOOT" -> {
                // Audit R6 #340 F53: unlock-moment resurrection.
                // Deliberately the CHEAP path only — read the
                // active-session snapshot and re-raise the shield
                // if one is live. No reconcile, no heartbeat: the
                // unlock is not the place for slow work, and a
                // session-less unlock costs one snapshot read.
                val pendingResult = goAsync()
                scope.launch {
                    try {
                        runGuardedBootAction {
                            val dao = MonolithDatabase.getInstance(appContext).monolithDao()
                            val active = dao.getActiveSessionSnapshot()
                            if (active != null && active.isActive) {
                                MonolithShieldService.startShield(appContext)
                            }
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                val pendingResult = goAsync()
                scope.launch {
                    try {
                        runGuardedBootAction {
                            RoutineHeartbeat.tick(
                                appContext, RoutineHeartbeat.Source.TIME_CHANGED, force = true
                            )
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    private suspend fun runBootActions(appContext: Context, action: String) {
        // The DAO is fetched inside each group, never before them:
        // even a database-open failure is then contained to its own
        // group instead of skipping every group below it.
        // Group 1 — the boot stamp, once per real boot.
        runGuardedBootAction {
            if (bootStampDue(action)) {
                MonolithDatabase.getInstance(appContext).monolithDao()
                    .stampLastBoot(System.currentTimeMillis())
            }
        }

        // Group 2 — shield FIRST, before the slow reconcile: if a
        // session is already active, it is shielded now, so a slow
        // or failed tick cannot lose the shield.
        runGuardedBootAction {
            val active = MonolithDatabase.getInstance(appContext).monolithDao()
                .getActiveSessionSnapshot()
            if (shouldRaiseBootShield(active != null, active?.isActive == true)) {
                MonolithShieldService.startShield(appContext)
            }
        }

        // Group 3 — the heartbeat reconcile itself.
        runGuardedBootAction {
            RoutineHeartbeat.tick(appContext, bootHeartbeatSource(action), force = true)
        }

        // Group 4 — shield AGAIN after the tick: the tick may have
        // finalised the session that was active, so raise the shield
        // only if one remains active afterwards.
        runGuardedBootAction {
            val active = MonolithDatabase.getInstance(appContext).monolithDao()
                .getActiveSessionSnapshot()
            if (shouldRaiseBootShield(active != null, active?.isActive == true)) {
                MonolithShieldService.startShield(appContext)
            }
        }
    }
}

/**
 * One guarded boot action group (issue #44): a failure is contained
 * to its own group — it never crashes the receiver, never cancels
 * the following groups, and never skips goAsync's finish. Genuine
 * cancellation is not a failure and is always rethrown.
 */
internal suspend fun runGuardedBootAction(block: suspend () -> Unit) {
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        // Contained: the remaining boot groups still run — see the
        // MonolithBootReceiver KDoc.
    }
}

/** Pure shield decision for the boot path (issue #44): the shield is
 *  raised exactly when a session snapshot exists AND is active. */
internal fun shouldRaiseBootShield(hasSessionSnapshot: Boolean, sessionIsActive: Boolean): Boolean =
    hasSessionSnapshot && sessionIsActive

/** Pure boot-stamp rule (issue #44): only LOCKED_BOOT_COMPLETED and
 *  QUICKBOOT_POWERON stamp — BOOT_COMPLETED fires for the same boot
 *  and must not overwrite the stamp. */
internal fun bootStampDue(action: String?): Boolean =
    action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
        action == "android.intent.action.QUICKBOOT_POWERON"

/** Pure heartbeat-source rule (issue #44): a package replacement is
 *  reported as its own source; every other boot action is a boot. */
internal fun bootHeartbeatSource(action: String?): RoutineHeartbeat.Source =
    if (action == Intent.ACTION_MY_PACKAGE_REPLACED) {
        RoutineHeartbeat.Source.PACKAGE_REPLACED
    } else {
        RoutineHeartbeat.Source.BOOT
    }
