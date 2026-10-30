package com.example

import com.example.data.TamperWitness
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Issue #81 (audit R2, tamper-routes): the witness laws are pure so
 * they are proven here, on the JVM, case by case — the platform
 * reads that feed them (keystore alias presence, BOOT_COUNT,
 * component enabled-state) live in the repository and are thin
 * enough to read at a glance.
 */
class TamperWitnessTest {

    // F1 — clear-data / `pm clear` provenance.

    @Test
    fun provenance_virginDbWithPreExistingKey_isResurrectedUnproven() {
        assertEquals(
            TamperWitness.InstallProvenance.RESURRECTED_UNPROVEN,
            TamperWitness.classifyProvenance(dbVirgin = true, keystoreKeyPreExisted = true)
        )
    }

    @Test
    fun provenance_virginDbWithoutKey_isFreshInstall() {
        assertEquals(
            TamperWitness.InstallProvenance.FRESH,
            TamperWitness.classifyProvenance(dbVirgin = true, keystoreKeyPreExisted = false)
        )
    }

    @Test
    fun provenance_intactDbWithKey_isOrdinaryRelaunch() {
        assertEquals(
            TamperWitness.InstallProvenance.FRESH,
            TamperWitness.classifyProvenance(dbVirgin = false, keystoreKeyPreExisted = true)
        )
    }

    @Test
    fun provenance_intactDbWithoutKey_isFresh() {
        // A database that exists but was never signed over (no
        // session ever ran) carries no resurrection evidence.
        assertEquals(
            TamperWitness.InstallProvenance.FRESH,
            TamperWitness.classifyProvenance(dbVirgin = false, keystoreKeyPreExisted = false)
        )
    }

    // F3 — Safe Mode / unwitnessed intermediate boots.

    @Test
    fun unwitnessedBoot_sameBoot_isWitnessed() {
        assertEquals(false, TamperWitness.unwitnessedBoot(anchoredBootCount = 7, currentBootCount = 7))
    }

    @Test
    fun unwitnessedBoot_oneReboot_isTheNormalWitnessedReboot() {
        assertEquals(false, TamperWitness.unwitnessedBoot(anchoredBootCount = 7, currentBootCount = 8))
    }

    @Test
    fun unwitnessedBoot_twoBoots_isUnwitnessed() {
        // Boot into Safe Mode (Monolith disabled, never re-anchors),
        // boot back to normal: the row's anchor is two boots stale.
        assertEquals(true, TamperWitness.unwitnessedBoot(anchoredBootCount = 7, currentBootCount = 9))
    }

    @Test
    fun unwitnessedBoot_manyBoots_isUnwitnessed() {
        assertEquals(true, TamperWitness.unwitnessedBoot(anchoredBootCount = 7, currentBootCount = 12))
    }

    @Test
    fun unwitnessedBoot_unanchoredRow_neverConvicts() {
        assertEquals(false, TamperWitness.unwitnessedBoot(anchoredBootCount = 0, currentBootCount = 9))
    }

    @Test
    fun unwitnessedBoot_counterMovedBackwards_neverConvicts() {
        assertEquals(false, TamperWitness.unwitnessedBoot(anchoredBootCount = 9, currentBootCount = 3))
    }

    // F5 — component enabled-state (PackageManager constants:
    // DEFAULT=0, ENABLED=1, DISABLED=2, DISABLED_USER=3,
    // DISABLED_UNTIL_USED=4).

    @Test
    fun componentDisabled_defaultAndEnabled_areNotDisabled() {
        assertEquals(false, TamperWitness.componentDisabled(0))
        assertEquals(false, TamperWitness.componentDisabled(1))
    }

    @Test
    fun componentDisabled_allDisabledStates_count() {
        assertEquals(true, TamperWitness.componentDisabled(2))
        assertEquals(true, TamperWitness.componentDisabled(3))
        assertEquals(true, TamperWitness.componentDisabled(4))
    }
}
