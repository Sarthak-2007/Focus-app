package com.example.domain

/**
 * CONTAIN — THE SEAL'S CONTAINMENT VERDICT (the production half
 * FLOW-SEAT's whole-day walk found missing: the routing decided,
 * but no enforcement door ever asked it).
 *
 * While a Strongroom day stands sealed, the launcher is the whole
 * phone (his Nov 1 order, carried in [StrongroomRouting]): a reach
 * for a foreign package is judged here, purely, from the SEALED
 * DAY's signed record — never from a caller-supplied catalogue
 * (B-9, the same law [StrongroomEngineBridge.onLockedAppReach]
 * reads by):
 *
 *  - PASS — the reach is lawful: Ulysses itself (the sealed face
 *    IS the phone's face while sealed), a system-UI surface (the
 *    shade, recents, the keyguard — never an app), a safety-floor
 *    package (dialer / telecom / keyboard / emergency: a contact's
 *    call rings THROUGH the seal, and its surfaces are floor), or
 *    an app on the day's SIGNED allowed list (the antechamber set —
 *    allowed apps pass per the signed day).
 *  - KNOCK — everything else: the day's locked set AND every
 *    unlisted package (an unlisted app is born LOCKED — the
 *    substrate's fail-closed rule, and [StrongroomRouting
 *    .routeReach]'s own verdict). The enforcement doors feed the
 *    reach into the bridge, which strikes the REACH punch and
 *    fires the knock — the knock event and nothing else — and the
 *    door then holds the app: eject, and the sealed face comes
 *    forward. The reach opens nothing.
 *  - HOLD_HOME — a foreign LAUNCHER: reaching for another home is
 *    reaching for the phone itself, not for an app. It is held
 *    (eject + the sealed face forward) but never punched as a
 *    knock: no app was reached for, and a knock per home press
 *    would fill the day's record with the phone's own furniture.
 *
 * The doors (accessibility + shield) execute these verdicts; they
 * never invent their own. Device behaviour stands
 * UNPROVEN-ON-DEVICE (Audit A G-7), as with every Strongroom
 * wiring claim: this is the decision, proven in code.
 */
object StrongroomContainment {

    enum class Verdict {
        /** Lawful while sealed: let it stand. */
        PASS,

        /** A locked reach: the knock sounds, the app is held. */
        KNOCK,

        /** Another launcher: held silently, never a knock. */
        HOLD_HOME
    }

    /**
     * Judge one foreground reach under a standing seal.
     * [allowedPackages] is the sealed day's SIGNED allowed set,
     * exactly as signed (B-9). The exemption flags are facts the
     * calling door already holds (the platform's own
     * classifications), passed in so this verdict stays pure.
     */
    fun verdict(
        packageName: String,
        ownPackage: String,
        allowedPackages: Set<String>,
        isSafetyFloor: Boolean,
        isHomePackage: Boolean,
        isSystemUi: Boolean
    ): Verdict = when {
        packageName == ownPackage -> Verdict.PASS
        isSystemUi -> Verdict.PASS
        isSafetyFloor -> Verdict.PASS
        packageName in allowedPackages -> Verdict.PASS
        isHomePackage -> Verdict.HOLD_HOME
        else -> Verdict.KNOCK
    }
}
