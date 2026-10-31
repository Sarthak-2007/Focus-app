package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Audit R6 #341 F01 — the RETURN side of the guest/secondary-user
 * escape. The platform half stands blocked and is named as such:
 * Monolith's processes do not run inside another user profile, so
 * nothing in this app can observe that profile or police it.
 *
 * What CAN be built truthfully is the return: USER_SWITCHED and
 * USER_BACKGROUND are delivered to the OWNER profile when the
 * phone leaves / returns from another user. On a return broadcast
 * while a session is active, the session's event log gains a
 * USER_GAP stamp (MonolithRepository.recordUserSwitchGap): the
 * unguarded interval is on the session's own record instead of
 * the session silently resuming as if no time had passed. With
 * no active session the receiver records nothing — an absence
 * invented into a record would be a fabrication.
 *
 * Both actions sit on the implicit-broadcast exemption list, so
 * this manifest declaration actually delivers.
 */
class MonolithUserReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_USER_SWITCHED &&
            intent.action != Intent.ACTION_USER_BACKGROUND
        ) {
            return
        }
        val appContext = context.applicationContext
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                AppContainer.repository(appContext).recordUserSwitchGap()
            } catch (_: Exception) {
                // A failed stamp must never crash the receiver —
                // the gap stays unstamped, which the event log
                // shows as an absence, not a false claim.
            } finally {
                pending.finish()
            }
        }
    }
}
