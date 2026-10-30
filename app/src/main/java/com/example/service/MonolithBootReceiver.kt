package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.MonolithDatabase
import com.example.data.UndoDeleteStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
 */
class MonolithBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val appContext = context.applicationContext
        when (action) {
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON" -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val dao = MonolithDatabase.getInstance(appContext).monolithDao()
                        if (action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
                            action == "android.intent.action.QUICKBOOT_POWERON"
                        ) {
                            dao.stampLastBoot(System.currentTimeMillis())
                        }
                        // Issue #83: a boot-started process reaps a
                        // stale undo-delete copy BEFORE the tick —
                        // processes born through this door (incl.
                        // LOCKED_BOOT_COMPLETED and
                        // MY_PACKAGE_REPLACED) may be the only
                        // process that ever runs after the copy's
                        // window closed, and without this the
                        // deleted task's full copy outlived its
                        // 5-second undo window indefinitely.
                        // Guarded: in direct boot the copy's
                        // credential-protected prefs may be
                        // unreadable pre-unlock, and a failed reap
                        // must never cost the tick its run.
                        runCatching { UndoDeleteStore.expireIfStale(appContext) }
                        val source = if (action == Intent.ACTION_MY_PACKAGE_REPLACED) {
                            RoutineHeartbeat.Source.PACKAGE_REPLACED
                        } else {
                            RoutineHeartbeat.Source.BOOT
                        }
                        RoutineHeartbeat.tick(appContext, source, force = true)
                        // The tick may have finalised the session that
                        // was active; raise the shield only if one
                        // remains active afterwards.
                        val active = dao.getActiveSessionSnapshot()
                        if (active != null && active.isActive) {
                            MonolithShieldService.startShield(appContext)
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RoutineHeartbeat.tick(
                            appContext, RoutineHeartbeat.Source.TIME_CHANGED, force = true
                        )
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
