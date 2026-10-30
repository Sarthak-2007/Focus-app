package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Issue #309 F7 (Audit R5, AppBlock second pass): the catalogue
 * used to discover installs/uninstalls only when a UI-driven sync
 * happened to run — a newly installed app was invisible to
 * enforcement until then, and the shield's 2-second loop re-scans
 * the DAO, never the package manager. This receiver listens for
 * package added / removed / replaced broadcasts and reconciles
 * the catalogue at install time.
 *
 * The reconcile itself is the existing catalogue law
 * (AppCatalogReconcile, issue #34): a discovered package is born
 * with its remembered ruling or category default, and a removed
 * package is only NOTED on first absence — pruning still needs a
 * second corroborating absence, so this receiver can never wipe a
 * ruling on one broadcast's say-so. The sync is forced: a package
 * broadcast is a genuinely separate look at the phone, not a
 * repeat of the last UI sync inside the collapse window.
 *
 * Failure posture mirrors MonolithBootReceiver (issue #44): a
 * supervised scope, every failure contained, goAsync's pending
 * result finished exactly once — a broken sync must never crash
 * the process from inside a broadcast.
 */
class MonolithPackageReceiver : BroadcastReceiver() {

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        // Last line of defence only — see the class KDoc.
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != Intent.ACTION_PACKAGE_ADDED &&
            action != Intent.ACTION_PACKAGE_REMOVED &&
            action != Intent.ACTION_PACKAGE_REPLACED &&
            action != Intent.ACTION_PACKAGE_CHANGED
        ) {
            return
        }
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        scope.launch {
            try {
                val dao = MonolithDatabase.getInstance(appContext).monolithDao()
                MonolithRepository(appContext, dao)
                    .ensureSeededAndSyncPackages(forceCatalogSync = true)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Contained: the next UI-driven sync reconciles —
                // this broadcast is an accelerator, not the only road.
            } finally {
                pendingResult.finish()
            }
        }
    }
}
