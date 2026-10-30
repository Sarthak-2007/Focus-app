package com.example.data

/**
 * Tamper-witness derivations (issue #81, audit R2 "tamper-routes").
 *
 * Pure functions only — every platform read happens in the caller
 * (MonolithRepository / CryptoIntegrityManager) and lands here as a
 * plain value, so each law is unit-testable on the JVM and no caller
 * can quietly re-derive a softer version of it.
 */
object TamperWitness {

    /** What a launch can honestly claim about this install's past. */
    enum class InstallProvenance {
        /** No evidence the app ever ran on this device before. */
        FRESH,

        /** The database is virgin, yet a residue that survives
         *  `pm clear` (the AndroidKeyStore HMAC key — system-held,
         *  per-UID, deleted on uninstall but NOT on clear-data)
         *  already exists: this install ran before and its data was
         *  wiped. Whatever session or tamper record existed is
         *  unrecoverable, so the install must report itself
         *  UNPROVEN — never as a clean first run. */
        RESURRECTED_UNPROVEN
    }

    /**
     * The provenance law: a virgin database PLUS a pre-existing
     * keystore key is a resurrection after a data wipe. Either fact
     * alone is a fresh install (no key yet) or a normal relaunch
     * (database intact).
     */
    fun classifyProvenance(
        dbVirgin: Boolean,
        keystoreKeyPreExisted: Boolean
    ): InstallProvenance =
        if (dbVirgin && keystoreKeyPreExisted) {
            InstallProvenance.RESURRECTED_UNPROVEN
        } else {
            InstallProvenance.FRESH
        }

    /**
     * The unwitnessed-boot law (issue #81 F3, Safe Mode): the
     * session row is anchored to the boot it last ticked in
     * ([anchoredBootCount], from Settings.Global.BOOT_COUNT, which
     * increments on EVERY boot, Safe Mode included). A normal
     * reboot is witnessed promptly — the boot receiver runs at
     * LOCKED_BOOT_COMPLETED and the shield's first tick re-anchors
     * the row — so the next measurement sees a delta of exactly 1.
     * A delta of 2 or more means at least one whole boot came and
     * went with no Monolith process ever re-anchoring: the Safe
     * Mode signature (downloaded apps cannot run there, Monolith
     * included, while preinstalled Chrome/YouTube still can), or a
     * force-stop spanning a reboot — both are unproven time and
     * neither may pass as a clean, clock-frozen pause.
     * An unanchored row (0) and a counter that moved backwards
     * (factory reset territory, where the DB is gone anyway) never
     * convict.
     */
    fun unwitnessedBoot(anchoredBootCount: Int, currentBootCount: Int): Boolean =
        anchoredBootCount > 0 && currentBootCount - anchoredBootCount >= 2
}
