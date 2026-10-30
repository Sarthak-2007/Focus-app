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
}
