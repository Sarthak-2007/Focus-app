package com.example.service

import com.example.session.StrongroomPlace

/**
 * Issue #155 F5 (WING-F, BUILDER-4), re-targeted by BUILD-SR-34:
 * what a tap on a glance card is ABOUT, and the one place that
 * tap may lawfully land on.
 *
 * The F5 kernel was routing, not decoration, and its per-state
 * table stood: every row landed on the Deck, because the Deck was
 * the one room each state lawfully shared. The Deck is dead. In
 * the Strongroom's own order the successor room is the DOORWAY —
 * the signing place, where the arm is composed and where the
 * plate is read after the lifting — so every row now lands there:
 *
 *  - [ARMING] — the card is about the signing in progress; the
 *    signing lives at the doorway.
 *  - [LIVE_SESSION] / [SAFE_BREAK] / [BREAK_OVER] — the tap names
 *    the doorway, and [com.example.session.MonolithViewModel.moveToPlace]'s
 *    seal law refuses the move while a session stands: the sealed
 *    face holds, exactly as the old seal veto held the Deck rows.
 *    The route is a request the order judges, never a promise.
 *  - [COMPLETION] — the card is about the finished day; its
 *    reading (the plate, the slip) stands at the doorway.
 *
 * The rows stay distinct in code so a future change edits ONE
 * row with its gate named — never the whole table by a global
 * replace. No glance tap may carry a session-control action:
 * routing moves the user's eyes, never the seal.
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

    /** The place a glance tap lands on. Null only when the tap is
     *  not a glance tap at all (no [about]) — the caller then
     *  performs no routing, exactly as before this law. */
    fun placeFor(about: GlanceAbout?): StrongroomPlace? = when (about) {
        // About the finished day; its reading stands at the
        // doorway, where the plate is struck and read.
        GlanceAbout.COMPLETION -> StrongroomPlace.DOORWAY
        // About the live session / its break / its next block /
        // the signing in progress: the doorway is the request;
        // while a seal stands, moveToPlace refuses it and the
        // sealed face holds — the tap moves eyes only where the
        // Strongroom's order allows eyes to go.
        GlanceAbout.ARMING,
        GlanceAbout.LIVE_SESSION,
        GlanceAbout.SAFE_BREAK,
        GlanceAbout.BREAK_OVER -> StrongroomPlace.DOORWAY
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
