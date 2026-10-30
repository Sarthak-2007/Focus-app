package com.example.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Issue #336 (Audit R5 supplement — Freedom, Visual design):
 * Freedom ships five purpose-built icon fonts as ONE icon system;
 * Monolith had no icon system at all — zero Icon composables in
 * the UI, every glyph an improvised text character ("✕", "‹", "›")
 * or a one-off Canvas drawing inside a single component.
 *
 * This is Monolith's one icon vocabulary: a single curated vector
 * set on the shared Material 24-unit grid — one fill language, one
 * metaphor per meaning — and every glyph-like mark in the UI
 * routes through here. Screens never render punctuation as icons
 * again: if a surface needs a glyph, it takes it from this object
 * or the set is extended HERE, in one place, with the metaphor
 * named. No icon fonts, no emoji, no per-screen improvisation.
 */
object MonolithIcons {

    /** The shield — the block itself; Guard and seal surfaces. */
    val Shield: ImageVector = Icons.Filled.Shield

    /** The lock — a sealed app, the arm state. */
    val Lock: ImageVector = Icons.Filled.Lock

    /** The clock — session time, the dial, durations. */
    val Clock: ImageVector = Icons.Filled.Schedule

    /** The eye — peek, the allowed glance. */
    val Eye: ImageVector = Icons.Filled.Visibility

    /** The key — the emergency break, the one valve. */
    val Key: ImageVector = Icons.Filled.VpnKey

    /** The bell — held notifications, the digest. */
    val Bell: ImageVector = Icons.Filled.Notifications

    /** The check — a kept promise, a completed seal. */
    val Check: ImageVector = Icons.Filled.Check

    /** The close — dismiss, end early, leave a surface. */
    val Close: ImageVector = Icons.Filled.Close

    /** Forward — next month, drill in, the drawer rows. */
    val ChevronRight: ImageVector = Icons.Filled.ChevronRight

    /** Back — previous month, return. */
    val ChevronLeft: ImageVector = Icons.Filled.ChevronLeft

    /** The list — the drawer, the ledger rows. */
    val List: ImageVector = Icons.AutoMirrored.Filled.List

    /** The bolt — the sniper, the instant intercept. */
    val Bolt: ImageVector = Icons.Filled.FlashOn

    /** The moon — night, sleep routines, the dark hours. */
    val Moon: ImageVector = Icons.Filled.DarkMode

    /** Play — start, resume. */
    val Play: ImageVector = Icons.Filled.PlayArrow

    /** Pause — the Safe Break's held state. */
    val Pause: ImageVector = Icons.Filled.Pause

    /** Arrow back — return to the previous surface. */
    val ArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack

    /** Every icon in the system — the registry tests pin this:
     *  one vocabulary, no orphans, no duplicate names, one grid. */
    val All: List<ImageVector> = listOf(
        Shield, Lock, Clock, Eye, Key, Bell, Check, Close,
        ChevronRight, ChevronLeft, List, Bolt, Moon, Play, Pause, ArrowBack
    )
}
