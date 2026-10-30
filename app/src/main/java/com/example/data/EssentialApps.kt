package com.example.data

/**
 * Essential apps, defined once by PACKAGE — never by substring luck
 * (issue #119, findings 4 + 5).
 *
 * The old SLEEP 'Full Lockdown' test asked whether the string
 * "packageName + appName" happened to contain dialer/phone/clock/
 * alarm/maps. A stock Camera (com.android.camera2, "Camera") and
 * Waze (com.waze, "Waze") contain none of those, so the overnight
 * loadout locked the two tools an emergency at 3 AM most needs.
 * And nothing anywhere stopped the dialler itself being classified
 * LOCKED, so placing a call could be gated behind the weekly valve.
 *
 * This object is the one definition every writer consumes:
 *  - [sleepLoadoutTierFor] decides the SLEEP loadout's OPEN set:
 *    real dialler / camera / navigation / clock packages, with the
 *    legacy substring test kept only as a superset fallback for
 *    vendor variants not in the sets.
 *  - [floorTier] is the dialler floor: the default dialler and the
 *    telephony stack can never be classified LOCKED by ANY tier
 *    writer (set, cycle, loadout, profile) — the floor is PEEK_2M,
 *    so placing a call is always reachable through the ordinary
 *    peek lane. The floor grants no OPEN and no free exit; it only
 *    removes the absurd state "you cannot dial for help".
 *
 * Pure Kotlin, no Android: the sets and both rules are unit-tested
 * directly.
 */
object EssentialApps {

    /** Default diallers + the telephony / in-call stack. */
    val DIALER_PACKAGES: Set<String> = setOf(
        "com.google.android.dialer",
        "com.android.dialer",
        "com.samsung.android.dialer",
        "com.android.incallui",
        "com.samsung.android.incallui",
        "com.google.android.incallui",
        "com.android.server.telecom",
        "com.android.phone"
    )

    /** Stock and major-vendor camera packages. */
    val CAMERA_PACKAGES: Set<String> = setOf(
        "com.android.camera2",
        "com.android.camera",
        "com.google.android.GoogleCamera",
        "com.sec.android.app.camera",
        "com.miui.camera",
        "com.oneplus.camera",
        "com.oppo.camera",
        "com.vivo.camera",
        "com.huawei.camera",
        "com.motorola.camera3",
        "com.motorola.camera2"
    )

    /** Navigation packages — Maps is not the only way to get home. */
    val NAVIGATION_PACKAGES: Set<String> = setOf(
        "com.google.android.apps.maps",
        "com.google.android.apps.mapslite",
        "com.waze",
        "com.here.app.maps",
        "com.tomtom.gplay.navapp",
        "com.sygic.aura",
        "org.osmand",
        "org.osmand.plus"
    )

    /** Clock / alarm packages. */
    val CLOCK_PACKAGES: Set<String> = setOf(
        "com.google.android.deskclock",
        "com.android.deskclock",
        "com.sec.android.app.clockpackage",
        "com.miui.clock"
    )

    fun isDialerPackage(packageName: String): Boolean {
        val pkg = packageName.trim().lowercase()
        return pkg in DIALER_PACKAGES ||
            pkg.endsWith(".dialer") ||
            pkg.endsWith(".incallui") ||
            pkg == "com.android.server.telecom" ||
            pkg == "com.android.phone"
    }

    fun isCameraPackage(packageName: String): Boolean {
        val pkg = packageName.trim()
        return pkg in CAMERA_PACKAGES ||
            pkg.lowercase().endsWith(".camera") ||
            pkg.lowercase().endsWith(".camera2") ||
            pkg.lowercase().endsWith(".googlecamera")
    }

    fun isNavigationPackage(packageName: String): Boolean {
        val pkg = packageName.trim().lowercase()
        return pkg in NAVIGATION_PACKAGES || pkg == "com.waze"
    }

    fun isClockPackage(packageName: String): Boolean {
        val pkg = packageName.trim().lowercase()
        return pkg in CLOCK_PACKAGES || pkg.endsWith(".deskclock")
    }

    /** The SLEEP loadout's essentials: dialler, camera, navigation, clock. */
    fun isEssentialPackage(packageName: String): Boolean =
        isDialerPackage(packageName) ||
            isCameraPackage(packageName) ||
            isNavigationPackage(packageName) ||
            isClockPackage(packageName)

    /**
     * The dialler floor (finding 5): a requested LOCKED for a dialler /
     * telephony package is raised to PEEK_2M. Every other request —
     * and every non-dialler package — passes through unchanged.
     */
    fun floorTier(packageName: String, requested: AppBlockTier): AppBlockTier =
        if (requested == AppBlockTier.LOCKED && isDialerPackage(packageName)) {
            AppBlockTier.PEEK_2M
        } else {
            requested
        }

    /**
     * The SLEEP 'Full Lockdown' tier for one app (finding 4): OPEN for
     * essential packages by package identity; the legacy substring
     * test survives only as a fallback for vendor variants outside
     * the sets, so nothing that was OPEN before this fix closes.
     * Everything else is LOCKED — the loadout's strictness is intact.
     */
    fun sleepLoadoutTierFor(packageName: String, appName: String): AppBlockTier {
        if (isEssentialPackage(packageName)) return AppBlockTier.OPEN
        val lower = "$packageName $appName".lowercase()
        return if (
            lower.contains("dialer") || lower.contains("phone") ||
            lower.contains("clock") || lower.contains("alarm") ||
            lower.contains("maps")
        ) {
            AppBlockTier.OPEN
        } else {
            AppBlockTier.LOCKED
        }
    }
}
