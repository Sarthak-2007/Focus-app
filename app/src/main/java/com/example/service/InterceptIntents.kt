package com.example.service

import android.content.Context
import android.content.Intent

/**
 * ISSUE-67 (Audit R1 · architecture, finding 5) — THE INTERCEPT
 * INTENT SEAM.
 *
 * Before this file, two services imported `com.example.MainActivity`
 * — the UI root — just to build the intercept intent that brings
 * the blocked-app door to the front. A service depending on the
 * activity class is the layering inversion the audit filed: the
 * lowest layer naming the highest.
 *
 * The seam: the intercept intent is addressed by the activity's
 * CLASS NAME, resolved at runtime — the exact class the manifest
 * declares as `.MainActivity`. The services keep no compile-time
 * reference to the UI root; the intent's extras, flags and
 * behaviour are byte-for-byte the ones the direct intents carried.
 *
 * MAINTENANCE NOTE (finding 4's rename): when the code package is
 * finally renamed off `com.example`, [MAIN_ACTIVITY_CLASS] must be
 * updated in the same commit — it is the one place the activity's
 * fully-qualified name lives outside the manifest.
 */
object InterceptIntents {

    /** The launcher activity's fully-qualified class name
     *  (manifest: `android:name=".MainActivity"` in package
     *  com.example). */
    const val MAIN_ACTIVITY_CLASS = "com.example.MainActivity"

    /** An intent that brings the app's main activity to the front:
     *  NEW_TASK | SINGLE_TOP, as both intercept doors always sent. */
    fun mainActivityIntent(context: Context): Intent =
        Intent().apply {
            setClassName(context, MAIN_ACTIVITY_CLASS)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

    /** The permission-return intent (#308's
     *  PermissionReturnService, landed on main while this branch
     *  was in flight): NEW_TASK | REORDER_TO_FRONT | SINGLE_TOP —
     *  its own flag set, distinct from the intercept doors', kept
     *  byte-identical through the seam. */
    fun returnIntent(context: Context): Intent =
        Intent().apply {
            setClassName(context, MAIN_ACTIVITY_CLASS)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }

    /** The notification content intent: addressed to the same
     *  activity, with flags SET to SINGLE_TOP only — the exact
     *  semantics the shield's notification builder always used
     *  (distinct from the intercept doors' NEW_TASK | SINGLE_TOP). */
    fun notificationIntent(context: Context): Intent =
        Intent().apply {
            setClassName(context, MAIN_ACTIVITY_CLASS)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

    /** The full intercept door intent: main-activity intent plus
     *  the intercepted package / app name / badge / door-path
     *  extras, under the shield service's extra keys (the keys are
     *  protocol constants, not UI). REBASE COMPOSITION (#68 F5,
     *  landed on main while this branch was in flight): the intent
     *  also carries a freshly minted [InterceptNonce] — the
     *  activity drops intercept extras without one — minted here,
     *  at build time, exactly as both doors did inline before.
     *  REBASE COMPOSITION (#78, landed on main later): every
     *  firing also carries a unique intercept id, so the activity
     *  can persist its consumption and drop a process-death
     *  redelivery — minted here for the same reason. */
    fun interceptIntent(
        context: Context,
        packageName: String,
        appName: String,
        badge: String,
        path: InterceptPath
    ): Intent = mainActivityIntent(context).apply {
        putExtra(MonolithShieldService.EXTRA_INTERCEPTED_PACKAGE, packageName)
        putExtra(MonolithShieldService.EXTRA_INTERCEPTED_APP_NAME, appName)
        putExtra(MonolithShieldService.EXTRA_INTERCEPT_BADGE, badge)
        putExtra(MonolithShieldService.EXTRA_INTERCEPT_PATH, path.name)
        putExtra(
            MonolithShieldService.EXTRA_INTERCEPT_ID,
            java.util.UUID.randomUUID().toString()
        )
        putExtra(
            MonolithShieldService.EXTRA_INTERCEPT_NONCE,
            InterceptNonce.mint(context)
        )
    }
}
