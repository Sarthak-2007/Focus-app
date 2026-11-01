package com.example.ui

/**
 * Issue #155 F5 (WING-F, BUILDER-4): what a tap on a glance card is
 * ABOUT, and the one destination that tap may lawfully land on.
 *
 * The audit's F5 kernel was routing, not decoration: the shield
 * card's tap dumped the user on the arming Deck whatever the card
 * was saying. The first cure (PR #312) swung to the opposite
 * single destination — every shield tap called
 * `navigateTo(SHIELD)` — and that route is wrong twice over on
 * current main:
 *
 *  - LIVE: [MonolithViewModel.navigateTo] refuses every non-DECK
 *    destination while a session is active (the #128 seal law: a
 *    live session holds the phone on the Deck). The SHIELD call is
 *    dead code mid-session — the tap lands wherever the persisted
 *    destination happened to be, by accident, not by routing.
 *  - COMPLETION: the session is over, so the SHIELD call succeeds —
 *    and lands on the one surface where the receipt the card
 *    announces is NOT. The standing receipt slip renders only
 *    while `currentDestination == DECK` (MainActivity's DECK gate);
 *    off the Deck it unmounts.
 *
 * The lawful table is therefore per-state, and every row is DECK —
 * not because routing does not matter, but because two standing
 * gates leave exactly one lawful room per state:
 *
 *  - [ARMING] — the arming card is about the arm in progress; the
 *    arming surface is the Deck.
 *  - [LIVE_SESSION] / [SAFE_BREAK] — the session surface under a
 *    seal is the Deck (hero, glance caption, break state); the
 *    seal law vetoes every other destination anyway.
 *  - [BREAK_OVER] — the summons is about the next block of the
 *    live session: the Deck, under the same seal.
 *  - [COMPLETION] — the card/summons is about the receipt; the
 *    standing slip stands on the Deck and nowhere else.
 *
 * The rows stay distinct in code so a future destination change
 * (e.g. a lawful session surface off the Deck) edits ONE row with
 * its gate named — never the whole table by a global replace.
 * No row may ever name a destination the seal vetoes while its
 * state is live, and no glance tap may carry a session-control
 * action: routing moves the user's eyes, never the seal.
 */
enum class GlanceAbout {
    ARMING,
    LIVE_SESSION,
    SAFE_BREAK,
    BREAK_OVER,
    COMPLETION
}

object GlanceRouting {

    /** The intent extra carrying a glance tap's [GlanceAbout]
     *  name. Written by the shield service's notification builders
     *  (the per-state writers ride PR #540's lane — see the consult
     *  patch posted there); read here, in the activity's routing.
     *  The key is owned by this file so the protocol has one home. */
    const val EXTRA_GLANCE_ABOUT = "com.example.EXTRA_GLANCE_ABOUT"

    /** Parse the extra's value; an unknown or absent value is not
     *  a state — it parses to null and the caller falls back to
     *  the legacy inference in [aboutForLegacyShieldTap]. */
    fun parseAbout(raw: String?): GlanceAbout? =
        raw?.let { runCatching { GlanceAbout.valueOf(it) }.getOrNull() }

    /** The destination a glance tap lands on. Null only when the
     *  tap is not a glance tap at all (no [about]) — the caller
     *  then performs no routing, exactly as before this law. */
    fun destinationFor(about: GlanceAbout?): MonolithDestination? = when (about) {
        // About the receipt; the standing slip stands only on the
        // Deck (MainActivity's DECK gate) — SHIELD was the #312
        // route and the slip is not there.
        GlanceAbout.COMPLETION -> MonolithDestination.DECK
        // About the live session / its break / its next block /
        // the arm in progress: the Deck is the sealed session
        // surface, and the seal law in navigateTo vetoes every
        // other destination while the session is live — a route
        // naming SHIELD here would be a promise the VM refuses.
        GlanceAbout.ARMING,
        GlanceAbout.LIVE_SESSION,
        GlanceAbout.SAFE_BREAK,
        GlanceAbout.BREAK_OVER -> MonolithDestination.DECK
        null -> null
    }

    /** Legacy inference for shield taps that carry only the #312
     *  boolean extra (every card the current shield build posts):
     *  a tap arriving while a session is active is about that
     *  session; a tap arriving with no active session can only be
     *  the detached completion card, the one shield card that
     *  outlives its session. */
    fun aboutForLegacyShieldTap(sessionActive: Boolean): GlanceAbout =
        if (sessionActive) GlanceAbout.LIVE_SESSION else GlanceAbout.COMPLETION
}
