package com.example

import com.example.data.OemPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

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
}
