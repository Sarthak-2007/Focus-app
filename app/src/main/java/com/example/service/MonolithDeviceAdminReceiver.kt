package com.example.service

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

/**
 * The disable-request warning for Monolith's device admin (issue #44).
 *
 * The truth, stated plainly: device admin here adds friction and a
 * warning only. It does NOT prevent deactivation and it does NOT
 * prevent uninstall — Android still allows the user to deactivate
 * this admin and uninstall Monolith. Nothing in this class blocks
 * either action; [onDisableRequested] only returns the warning text
 * Android shows on the deactivation screen.
 *
 * The text is session-aware through the accessibility service's
 * in-memory witness ([MonolithAccessibilityService.armedSessionId]):
 * it may say a session is active only when that witness currently
 * holds one. The witness is memory-only, so after a reboot or a
 * service rebind it reports no session even if one is persisted —
 * the message never claims a session it cannot witness.
 */
class MonolithDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return deviceAdminDisableMessage(
            sessionActive = MonolithAccessibilityService.armedSessionId != -1
        )
    }

    // Watch Store GRANT CHECK writers (chrome C3): the grant's own
    // lifecycle stamps the store's DEVICE ADMIN item.
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        WatchStore.onDeviceAdminChanged(true)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        WatchStore.onDeviceAdminChanged(false)
    }
}

/**
 * Pure disable-request warning text (issue #44) — the truth table is
 * unit-tested directly. Both branches state the same limit: Android
 * still allows deactivation, and device admin does not prevent
 * deactivation or uninstall; it only adds friction and this warning.
 */
internal fun deviceAdminDisableMessage(sessionActive: Boolean): String {
    return if (sessionActive) {
        "A Monolith focus session is active right now. Warning only: " +
            "Android still allows you to deactivate this device admin, " +
            "and device admin does not prevent deactivation or uninstall " +
            "— it only adds friction and this warning. If you continue, " +
            "you can deactivate and then uninstall Monolith even during " +
            "the session. Ending the focus block cleanly first is your " +
            "choice, not a lock this admin can enforce."
    } else {
        "No active Monolith focus session is running right now. " +
            "Warning only: Android still allows you to deactivate this " +
            "device admin, and device admin does not prevent " +
            "deactivation or uninstall — it only adds friction and " +
            "this warning."
    }
}
