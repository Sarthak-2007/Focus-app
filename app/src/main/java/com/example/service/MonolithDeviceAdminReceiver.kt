package com.example.service

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

/**
 * Kernel-Level Uninstall Guard:
 * Uses Android's DeviceAdminReceiver to prevent uninstalling Monolith from the home screen
 * while a focus session is active.
 */
class MonolithDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return "Monolith Uninstall Guard is protecting your active study session. End your focus block cleanly before deactivating."
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
