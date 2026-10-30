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
}
