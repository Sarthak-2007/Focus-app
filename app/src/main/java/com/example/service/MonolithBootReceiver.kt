package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.UndoDeleteStore
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

    companion object {
        const val TAG = "MonolithBoot"
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // Last line of defence only: every action group below already
        // catches its own failures. An exception reaching this handler
        // must still never crash the process from inside goAsync's
        // window — but containment is never silence (PR CHECK,
        // Tyler's review of the #44 landing): the failure is LOGGED,
        // with its stack, so a boot that failed to restore the shield
        // leaves a findable trace instead of a quiet lie.
        Log.e(TAG, "uncaught boot failure reached the receiver handler", throwable)
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
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                val pendingResult = goAsync()
                scope.launch {
                    try {
                        runGuardedBootAction("time-changed-tick") {
                            RoutineHeartbeat.tick(
                                appContext, RoutineHeartbeat.Source.TIME_CHANGED, force = true
                            )
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
            // Issue #81 F5: a package's component set changed —
            // reconcile; the pass re-reads Monolith's own critical
            // components' enabled states (RoutineHeartbeat step 4c)
            // and latches the live session's gap if one was
            // disabled out from under it.
            Intent.ACTION_PACKAGE_CHANGED -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RoutineHeartbeat.tick(
                            appContext, RoutineHeartbeat.Source.PACKAGE_REPLACED, force = true
                        )
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
        runGuardedBootAction("boot-stamp") {
            if (bootStampDue(action)) {
                com.example.AppContainer.dao(appContext) /* ISSUE-67: single construction point */
                    .stampLastBoot(System.currentTimeMillis())
            }
        }

        // Group 1b — issue #83: a boot-started process reaps a stale
        // undo-delete copy. A process born through this door (incl.
        // LOCKED_BOOT_COMPLETED and MY_PACKAGE_REPLACED) may be the
        // only process that ever runs after the copy's 5-second
        // window closed; without this the deleted task's full copy
        // outlived its window indefinitely in cleartext prefs.
        // Guarded as its own group: in direct boot the copy's
        // credential-protected prefs may be unreadable pre-unlock,
        // and a failed reap must never cost the tick its run.
        runGuardedBootAction {
            UndoDeleteStore.expireIfStale(appContext)
        }

        // Group 1c — Issue #87 (process-death F1): a REAL boot resets
        // SystemClock.elapsedRealtime() to ~0, so every persisted
        // peek lease/cooldown anchor — written against the PREVIOUS
        // boot's monotonic clock — would otherwise read as a
        // far-future expiry and resurrect a 2-minute peek for
        // hours-to-days. Zero both fields on every row, BEFORE the
        // shield groups read them. A package replace is NOT a boot:
        // the monotonic clock survives it, so leases stay valid and
        // are left alone there. LOCKED_BOOT and BOOT both fire for
        // one boot; the clear is idempotent.
        runGuardedBootAction {
            if (action != Intent.ACTION_MY_PACKAGE_REPLACED) {
                com.example.AppContainer.dao(appContext)
                    .clearAllPeekLeases()
            }
        }

        // Group 2 — shield FIRST, before the slow reconcile: if a
        // session is already active, it is shielded now, so a slow
        // or failed tick cannot lose the shield.
        runGuardedBootAction("shield-before-tick") {
            val active = com.example.AppContainer.dao(appContext) /* ISSUE-67: single construction point */
                .getActiveSessionSnapshot()
            if (shouldRaiseBootShield(active != null, active?.isActive == true)) {
                MonolithShieldService.startShield(appContext)
            }
        }

        // Group 3 — the heartbeat reconcile itself.
        runGuardedBootAction("heartbeat-tick") {
            RoutineHeartbeat.tick(appContext, bootHeartbeatSource(action), force = true)
        }

        // Group 4 — shield AGAIN after the tick: the tick may have
        // finalised the session that was active, so raise the shield
        // only if one remains active afterwards.
        runGuardedBootAction("shield-after-tick") {
            val active = com.example.AppContainer.dao(appContext) /* ISSUE-67: single construction point */
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
internal suspend fun runGuardedBootAction(
    name: String = "unnamed",
    reportFailure: (String, Exception) -> Unit = { failedName, failure ->
        Log.e(MonolithBootReceiver.TAG, "boot action '$failedName' failed", failure)
    },
    block: suspend () -> Unit
) {
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // Contained AND loud (PR CHECK, Tyler's review of the #44
        // landing): the remaining boot groups still run, but the
        // failure is reported — group name and stack — through
        // [reportFailure], whose production default is Log.e. A
        // boot group that failed must never read as a boot that
        // worked. (The seam exists so unit tests can prove the
        // report fires; the default is the loud one.)
        reportFailure(name, e)
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
