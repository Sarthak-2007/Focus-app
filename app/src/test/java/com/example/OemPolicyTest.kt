package com.example

import com.example.data.OemPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Issue #94 (audit R2, oem-battery-reality F1): the OEM autostart gate
 * mapping and the honest battery rule — a granted AOSP battery read is
 * proved only where no unreadable OEM toggle stands behind it.
 */
class OemPolicyTest {

    @Test
    fun gateFor_mapsGatingSkins() {
        assertEquals(
            OemPolicy.AutostartGate.XIAOMI,
            OemPolicy.gateFor("Xiaomi", "Xiaomi")
        )
        assertEquals(
            OemPolicy.AutostartGate.XIAOMI,
            OemPolicy.gateFor("Xiaomi", "Redmi")
        )
        assertEquals(
            OemPolicy.AutostartGate.COLOROS,
            OemPolicy.gateFor("OPPO", "OPPO")
        )
        assertEquals(
            OemPolicy.AutostartGate.COLOROS,
            OemPolicy.gateFor("realme", "realme")
        )
        assertEquals(
            OemPolicy.AutostartGate.COLOROS,
            OemPolicy.gateFor("OnePlus", "OnePlus")
        )
        assertEquals(
            OemPolicy.AutostartGate.VIVO,
            OemPolicy.gateFor("vivo", "vivo")
        )
        assertEquals(
            OemPolicy.AutostartGate.HUAWEI,
            OemPolicy.gateFor("HUAWEI", "HUAWEI")
        )
    }

    @Test
    fun gateFor_nonGatingAndUnknownAreNone() {
        assertEquals(
            OemPolicy.AutostartGate.NONE,
            OemPolicy.gateFor("Google", "google")
        )
        assertEquals(
            OemPolicy.AutostartGate.NONE,
            OemPolicy.gateFor("samsung", "samsung")
        )
        assertEquals(OemPolicy.AutostartGate.NONE, OemPolicy.gateFor(null, null))
        assertEquals(OemPolicy.AutostartGate.NONE, OemPolicy.gateFor("", ""))
    }

    @Test
    fun batteryGrantedIsProved_onlyWhereNoGateStandsBehindIt() {
        assertTrue(
            OemPolicy.batteryGrantedIsProved(OemPolicy.AutostartGate.NONE)
        )
        for (gate in OemPolicy.AutostartGate.values()) {
            if (gate != OemPolicy.AutostartGate.NONE) {
                assertFalse(
                    "gate $gate must leave a granted battery read UNPROVED",
                    OemPolicy.batteryGrantedIsProved(gate)
                )
            }
        }
    }

    // PIN scope: SOURCE.
    // ---- Audit R6 #341 F28: the explicit Xiaomi battery step +
    // the attestation rule ----

    @Test
    fun xiaomiBatteryStep_presentInGrantFlowForMiui() {
        val intents = OemPolicy.miuiBatterySaverIntents("com.example")
        assertTrue(intents.isNotEmpty())
        // The step is MIUI's own per-app battery surface
        // (PowerKeeper) — not a generic settings pane.
        assertTrue(
            intents.any { it.component?.packageName == "com.miui.powerkeeper" }
        )
    }

    @Test
    fun batteryAttestation_userHasNotConfirmed_printsUnprovedNeverGranted() {
        // The #94 truth half, sharpened by F28: on Xiaomi, an
        // AOSP-granted read WITHOUT the user's attestation is
        // still not proof.
        assertFalse(
            OemPolicy.batteryGrantedIsProved(
                OemPolicy.AutostartGate.XIAOMI, attested = false
            )
        )
    }

    @Test
    fun batteryAttestation_provedRule_needsAttestationOnlyOnXiaomi() {
        assertTrue(
            OemPolicy.batteryGrantedIsProved(
                OemPolicy.AutostartGate.XIAOMI, attested = true
            )
        )
        // No gate: proved without any attestation (unchanged).
        assertTrue(
            OemPolicy.batteryGrantedIsProved(
                OemPolicy.AutostartGate.NONE, attested = false
            )
        )
        // Other gating skins: this build's attestation covers the
        // Xiaomi steps only — their prints stay UNPROVED rather
        // than borrowing Xiaomi's proof.
        assertFalse(
            OemPolicy.batteryGrantedIsProved(
                OemPolicy.AutostartGate.COLOROS, attested = true
            )
        )
    }
}

/**
 * Audit R6 #341 F28: the attestation RECORD — the user's word,
 * stored with its instant and the gate it was given under, and
 * the board print it changes (UNPROVED → GRANTED only with the
 * attestation on record for THIS device's gate).
 */
@RunWith(org.robolectric.RobolectricTestRunner::class)
@Config(sdk = [36])
class OemPolicyAttestationStoreTest {

    private val context: android.content.Context
        get() = androidx.test.core.app.ApplicationProvider.getApplicationContext()

    @Test
    fun batteryAttestation_userConfirms_recordsAttestationWithTimestamp() {
        // Stand on a Xiaomi gate for this case.
        org.robolectric.util.ReflectionHelpers.setStaticField(
            android.os.Build::class.java, "MANUFACTURER", "Xiaomi"
        )
        org.robolectric.util.ReflectionHelpers.setStaticField(
            android.os.Build::class.java, "BRAND", "Redmi"
        )
        com.example.service.WatchStore.bind(context)
        // Before the word: no attestation, and the print of a
        // granted-but-unproved battery read is UNPROVED.
        assertEquals(null, com.example.service.WatchStore.batteryAttestedGate())
        val nowMono = 50_000L
        val snapshot = com.example.service.WatchSnapshot(
            items = mapOf(
                com.example.service.WatchItemId.BATTERY to
                    com.example.service.WatchMeasurement(
                        item = com.example.service.WatchItemId.BATTERY,
                        perm = com.example.service.PermState.GRANTED,
                        measuredMono = nowMono,
                        measuredWall = 1_000L
                    )
            ),
            engineHeartbeatMono = nowMono,
            engineHeartbeatWall = 1_000L,
            engineProtecting = true,
            lastProvedWallMillis = null
        )
        assertEquals(
            com.example.service.PermPrint.UNPROVED,
            com.example.service.WatchStore.permPrintAt(
                snapshot, com.example.service.WatchItemId.BATTERY, nowMono
            )
        )
        // The user confirms the Xiaomi steps are set: the record
        // lands under THIS gate, with its instant…
        val before = System.currentTimeMillis()
        com.example.service.WatchStore.onBatteryAttested()
        assertEquals(
            OemPolicy.AutostartGate.XIAOMI.name,
            com.example.service.WatchStore.batteryAttestedGate()
        )
        val prefs = context.getSharedPreferences("monolith_watch", android.content.Context.MODE_PRIVATE)
        assertTrue(prefs.getLong("battery_attested_wall", 0L) >= before)
        // …and the print can finally say GRANTED.
        assertEquals(
            com.example.service.PermPrint.GRANTED,
            com.example.service.WatchStore.permPrintAt(
                snapshot, com.example.service.WatchItemId.BATTERY, nowMono
            )
        )
    }
}
