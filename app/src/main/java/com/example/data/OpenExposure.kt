package com.example.data

/**
 * Issue #129 F3 — THE OPEN-EXPOSURE TEST (persona: the future
 * employer reading the record at face value).
 *
 * Before this file, a manual session's ending was stamped
 * ELAPSED_CLEAN purely from the shield's heartbeat: whether the
 * shield process beat, never what the man actually used. An app
 * that was OPEN — or had no catalogue row at all — could be used
 * for the whole session and the record still said clean.
 *
 * This is the pure test the ending write now runs: given the
 * per-package foreground time inside the session's own window
 * (from UsageStats events, gathered by the repository), which
 * UNGUARDED apps were actually used long enough to matter?
 *
 * An app counts as exposure only when ALL of these hold:
 * - it was in the foreground for at least [MIN_FOREGROUND_MILLIS]
 *   inside the window (a stray tap-through is not exposure);
 * - it was NOT held: it has no catalogue row, or its row's tier
 *   is OPEN;
 * - and it is plausibly a distraction door: its guessed category
 *   is one of the distraction categories (SHORT_VIDEO / SOCIAL /
 *   GAMES / BROWSERS), or it stands in the unreviewed-auto-OPEN
 *   record (issue #129 F1 — the mis-filed unknown, e.g. a game
 *   the keyword guess filed as SHOPPING), or it has no row at all.
 *
 * System surfaces are never exposure: launchers, System UI, the
 * dialer / in-call UI, Settings and the package installer are how
 * a phone is operated, not how a session is spent. Monolith
 * itself is excluded by the caller-supplied own-package.
 *
 * Deliberately NOT exposure: an OPEN app the man ruled OPEN
 * himself in a utility category (Docs, Maps, a calculator) — the
 * record does not call a session unclean for using the tools he
 * deliberately left open. The gap the audit found was never
 * "OPEN apps exist"; it was "unguarded distraction doors were
 * used and the record said nothing".
 */
data class ExposedApp(
    val packageName: String,
    /** The catalogue label, or the package name when no row exists. */
    val appName: String,
    val foregroundMillis: Long
)

object OpenExposure {

    const val MIN_FOREGROUND_MILLIS = 30_000L

    private val DISTRACTION_CATEGORIES = setOf(
        AppCategory.SHORT_VIDEO.name,
        AppCategory.SOCIAL.name,
        AppCategory.GAMES.name,
        AppCategory.BROWSERS.name
    )

    /** Packages that are the phone's own furniture, never a
     *  session's content. Matched exactly, plus any package whose
     *  name contains "launcher" (every OEM launcher's shape). */
    private val SYSTEM_SURFACES = setOf(
        "com.android.systemui",
        "com.android.settings",
        "com.google.android.packageinstaller",
        "com.android.packageinstaller",
        "com.samsung.android.packageinstaller",
        "com.miui.packageinstaller",
        "com.google.android.dialer",
        "com.android.dialer",
        "com.samsung.android.dialer",
        "com.android.incallui",
        "com.android.server.telecom",
        "com.google.android.permissioncontroller",
        "android"
    )

    fun isSystemSurface(packageName: String): Boolean =
        packageName in SYSTEM_SURFACES || packageName.contains("launcher")

    /**
     * The exposed apps in [foregroundMillisByPackage], in
     * longest-use-first order (ties by package name, so the
     * derivation is total and testable).
     */
    fun exposedApps(
        foregroundMillisByPackage: Map<String, Long>,
        apps: List<BlockedAppEntity>,
        unreviewedOpenPackages: Set<String>,
        ownPackage: String
    ): List<ExposedApp> {
        val byPackage = apps.associateBy { it.packageName }
        return foregroundMillisByPackage.entries
            .filter { (pkg, millis) ->
                millis >= MIN_FOREGROUND_MILLIS &&
                    pkg != ownPackage &&
                    !isSystemSurface(pkg)
            }
            .filter { (pkg, _) ->
                val row = byPackage[pkg]
                when {
                    row == null -> true
                    row.resolvedTier != AppBlockTier.OPEN -> false
                    row.category in DISTRACTION_CATEGORIES -> true
                    pkg in unreviewedOpenPackages -> true
                    else -> false
                }
            }
            .map { (pkg, millis) ->
                ExposedApp(
                    packageName = pkg,
                    appName = byPackage[pkg]?.appName ?: pkg,
                    foregroundMillis = millis
                )
            }
            .sortedWith(
                compareByDescending<ExposedApp> { it.foregroundMillis }
                    .thenBy { it.packageName }
            )
    }
}
