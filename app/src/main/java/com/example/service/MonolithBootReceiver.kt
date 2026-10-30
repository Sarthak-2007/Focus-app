package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.MonolithDatabase
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
                        // Issue #87 (process-death F1): a REAL boot resets
                        // SystemClock.elapsedRealtime() to ~0, so every
                        // persisted peek lease/cooldown anchor — written
                        // against the PREVIOUS boot's monotonic clock —
                        // would otherwise read as a far-future expiry and
                        // resurrect a 2-minute peek for hours-to-days.
                        // Zero both fields on every row. A package replace
                        // is NOT a boot: the monotonic clock survives it,
                        // so leases stay valid and are left alone there.
                        // LOCKED_BOOT and BOOT both fire for one boot;
                        // the clear is idempotent.
                        if (action != Intent.ACTION_MY_PACKAGE_REPLACED) {
                            dao.clearAllPeekLeases()
                        }
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
