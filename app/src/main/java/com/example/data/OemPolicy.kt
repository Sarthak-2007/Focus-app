package com.example.data

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * ===========================================================================
 * THE OEM POLICY (issue #94, audit R2 oem-battery-reality F1) — the layer
 * that was missing entirely: on Xiaomi / Oppo-family / Vivo / Huawei skins,
 * surviving a reboot or an app update is NOT decided by the AOSP battery
 * exemption. A separate per-app OEM AUTOSTART toggle gates whether
 * BOOT_COMPLETED / MY_PACKAGE_REPLACED receivers may start the app at all,
 * and `PowerManager.isIgnoringBatteryOptimizations` neither grants nor
 * measures it. Before this file the app had zero manufacturer handling and
 * printed BATTERY GRANTED from the AOSP read alone — false assurance on
 * exactly the skins where the boot receiver can be silently dead.
 *
 * Honesty rule: the OEM toggle CANNOT be read back (no platform or vendor
 * API reports it). So on a gating skin the battery fact is never GRANTED —
 * it is UNPROVED until the AOSP exemption is also missing (then it is
 * plainly MISSING). The FIX trip on a gating skin goes to the OEM
 * autostart pane first, falling back to the AOSP battery pane when the
 * vendor pane does not exist on the device.
 * ===========================================================================
 */
object OemPolicy {

    /** The skins whose autostart toggle gates receiver restarts. */
    enum class AutostartGate { NONE, XIAOMI, COLOROS, VIVO, HUAWEI }

    /** Pure mapping (unit-tested): manufacturer/brand strings, any case. */
    fun gateFor(manufacturer: String?, brand: String?): AutostartGate {
        // Locale.ROOT pins (issue #88's Turkish-fold lesson, applied here):
        // a default-locale lowercase() folds 'I' to dotless 'ı' in tr-TR,
        // so "VIVO"/"XIAOMI" would miss their needles and the gate — the
        // exact false-GRANTED failure this file exists to prevent.
        val m = (manufacturer ?: "").trim().lowercase(java.util.Locale.ROOT)
        val b = (brand ?: "").trim().lowercase(java.util.Locale.ROOT)
        fun hits(vararg needles: String) = needles.any { it in m || it in b }
        return when {
            hits("xiaomi", "redmi", "poco") -> AutostartGate.XIAOMI
            // ColorOS skins: Oppo proper plus its Realme / OnePlus family.
            hits("oppo", "realme", "oneplus") -> AutostartGate.COLOROS
            hits("vivo", "iqoo") -> AutostartGate.VIVO
            hits("huawei", "honor") -> AutostartGate.HUAWEI
            else -> AutostartGate.NONE
        }
    }

    /** This device's gate, from the build fingerprint. */
    fun currentGate(): AutostartGate = gateFor(Build.MANUFACTURER, Build.BRAND)

    /** True when an OEM autostart toggle stands between this app and a
     *  receiver restart, unreadable and ungrantable from inside the app. */
    fun gatesAutostart(): Boolean = currentGate() != AutostartGate.NONE

    /**
     * The battery print's honest form: on a gating skin, an AOSP-granted
     * read proves only half the survival fact — the autostart half is
     * UNPROVED, never GRANTED. Pure, so the rule is unit-testable without
     * a device. (A missing AOSP exemption stays MISSING upstream: that
     * half IS known.)
     */
    fun batteryGrantedIsProved(gate: AutostartGate): Boolean =
        batteryGrantedIsProved(gate, attested = false)

    /**
     * Audit R6 #341 F28 (the remainder #94 left): on Xiaomi the
     * per-app BATTERY SAVER is a third, separate gate — MIUI's
     * PowerKeeper holds Monolith at "Battery saver (recommended)"
     * unless the user sets "No restrictions", and no API reads it
     * back. The proof rule therefore gains the user's ATTESTATION
     * (Blockit's shape): with the AOSP exemption granted and the
     * user's own word on record that the Xiaomi steps are set,
     * the battery fact is proved; without the attestation it
     * stays UNPROVED — never GRANTED on the AOSP read alone.
     * Other gating skins keep the #94 rule unchanged (their
     * attestation step is not built here and is not implied).
     */
    fun batteryGrantedIsProved(gate: AutostartGate, attested: Boolean): Boolean =
        when (gate) {
            AutostartGate.NONE -> true
            AutostartGate.XIAOMI -> attested
            else -> false
        }

    /**
     * The explicit Xiaomi battery step (F28): MIUI PowerKeeper's
     * per-app battery list, where Monolith must be set to "No
     * restrictions". Vendor components are guesses that may not
     * exist on a given build — callers try/catch each and fall
     * through (this file's standing doctrine).
     */
    fun miuiBatterySaverIntents(packageName: String): List<Intent> {
        val list = Intent().apply {
            component = ComponentName(
                "com.miui.powerkeeper",
                "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("packageName", packageName)
        }
        val keeper = Intent().apply {
            component = ComponentName(
                "com.miui.powerkeeper",
                "com.miui.powerkeeper.ui.PowerKeeperMainActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return listOf(list, keeper)
    }

    /**
     * The OEM autostart panes for [gate], most specific first. Every one
     * is a guess at a vendor component that may not exist on a given
     * build — callers must try/catch each and fall through to
     * [batterySettingsIntent]. Components are the long-published vendor
     * autostart activities for each skin.
     */
    fun autostartIntents(gate: AutostartGate, packageName: String): List<Intent> {
        fun pane(pkg: String, cls: String, extra: Pair<String, String>? = null): Intent =
            Intent().apply {
                component = ComponentName(pkg, cls)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (extra != null) putExtra(extra.first, extra.second)
            }
        return when (gate) {
            AutostartGate.XIAOMI -> listOf(
                pane(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            )
            AutostartGate.COLOROS -> listOf(
                pane(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                ),
                pane(
                    "com.oppo.safe",
                    "com.oppo.safe.permission.startup.StartupAppListActivity"
                )
            )
            AutostartGate.VIVO -> listOf(
                pane(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                ),
                pane(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                )
            )
            AutostartGate.HUAWEI -> listOf(
                pane(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
            )
            AutostartGate.NONE -> emptyList()
        }
    }

    /** The AOSP battery pane: the direct per-app request, as shipped. */
    fun batterySettingsIntent(packageName: String): Intent =
        Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Uri.parse("package:$packageName")
        )
}
