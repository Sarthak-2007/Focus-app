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
}
