package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Issue #129 (unlisted-distraction, CRITICAL): the install alarm.
 *
 * Before this receiver the manifest declared no PACKAGE_ADDED
 * receiver at all, and the catalogue sync ran only when the
 * ViewModel asked — so an app installed after the last Monolith
 * launch had no catalogue row, and no row meant no law at either
 * enforcement door for the entire session.
 *
 * Now the install itself asks for the sync: the new package is
 * born into its row immediately (LOCKED by the fail-closed birth
 * in AppCatalogReconcile), whether or not a session is running,
 * so by the time any session seals the book already covers it.
 * The doors do not depend on this receiver landing — the
 * accessibility door fails closed on an unlisted sighting and
 * the shield loop re-syncs on its 30 s cadence — this is the
 * fast path, not the only path.
 */
class PackageAddedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_PACKAGE_ADDED) return
        // A replaced app keeps its existing row and his ruling;
        // only a genuinely new package needs the birth sync.
        if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) return
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = MonolithDatabase.getInstance(appContext).monolithDao()
                MonolithRepository(appContext, dao).ensureSeededAndSyncPackages()
            } catch (e: Exception) {
                android.util.Log.w("PackageAddedReceiver", "catalogue birth sync failed", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
