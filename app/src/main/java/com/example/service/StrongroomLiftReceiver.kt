package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * P2 — the firing wake (BUILD-SR-3). Armed by
 * [StrongroomEngineBridge.armFirings] through ExactAlarm at the
 * WALL ESTIMATE of an elapsed firing. The receiver itself decides
 * nothing: it hands the wake to the bridge, which re-derives from
 * the elapsed span before acting — a moved wall clock can wake
 * this receiver early or late, never move a firing.
 *
 * Break start/end wakes are the surfaces' events (they re-derive
 * from the same span); the lift wake strikes the plate and stands
 * the shield down, in the bridge.
 */
class StrongroomLiftReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "StrongroomLift"
        const val ACTION_FIRING = "com.example.action.STRONGROOM_FIRING"
        const val EXTRA_DATE_ISO = "com.example.extra.STRONGROOM_DATE_ISO"
        const val EXTRA_FIRING_KIND = "com.example.extra.STRONGROOM_FIRING_KIND"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_FIRING) return
        val dateIso = intent.getStringExtra(EXTRA_DATE_ISO) ?: return
        val kind = intent.getStringExtra(EXTRA_FIRING_KIND) ?: return
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        scope.launch {
            try {
                StrongroomEngineBridge.onFiringWake(appContext, dateIso, kind)
            } catch (e: Exception) {
                // Contained, never silent: a failed firing wake
                // leaves a findable trace (the boot receiver's law).
                Log.e(TAG, "firing wake failed for $dateIso/$kind", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
