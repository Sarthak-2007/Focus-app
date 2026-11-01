package com.example.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.MainActivity
import com.example.R
import com.example.data.FocusProfileEntity

/**
 * Issue #274 (Audit R5 — Platform execution), finding #14's
 * remainder: dynamic per-loadout launcher shortcuts.
 *
 * PR #492 landed the static half (one "open Monolith" shortcut in
 * res/xml/shortcuts.xml). The per-configuration half stood open:
 * no shortcut named a loadout. This file is that half — one
 * dynamic shortcut per loadout (profile), republished whenever
 * the profiles flow moves, so an edit or deletion in the Matrix
 * is reflected on the launcher without a second source of truth.
 *
 * What a shortcut tap may do — and no more: it carries the
 * loadout's id into MainActivity, which applies the loadout
 * through the app's own verb (MonolithViewModel
 * .selectAndApplyProfile — the same call an in-app tap makes,
 * ratchet guards included) and stands on the Deck. The arm
 * itself still happens in-app, by hand, through the proving
 * path. A shortcut is a door to the loadout's arm surface,
 * never a silent seal — the same law the QS tile lives under
 * (it may open Monolith; it may not run the session).
 */
object LoadoutShortcuts {

    /** The shortcut intent's action. Owned here so the protocol
     *  (publisher, intent, activity reader) has one home. */
    const val ACTION_OPEN_LOADOUT = "com.example.action.OPEN_LOADOUT"

    /** The intent extra carrying the loadout's profile id. */
    const val EXTRA_OPEN_LOADOUT_ID = "com.example.EXTRA_OPEN_LOADOUT_ID"

    /** res/xml/shortcuts.xml declares exactly one static
     *  shortcut ("open_monolith"); statics and dynamics share
     *  the platform's per-activity cap, so the dynamic budget
     *  is the cap minus this count. If a second static is ever
     *  added there, raise this — the specs test pins the rule,
     *  not the number. */
    const val STATIC_SHORTCUT_COUNT = 1

    const val SHORTCUT_ID_PREFIX = "loadout_"

    /** One shortcut to publish: everything the platform needs,
     *  derived purely from the profile so the derivation is
     *  unit-testable without a launcher. */
    data class Spec(
        val id: String,
        val profileId: Int,
        val shortLabel: String,
        val longLabel: String
    )

    /**
     * The specs for [profiles] under a dynamic budget of
     * [maxDynamic]. Deterministic: input order (the profiles
     * flow's own order), duplicate ids collapse to the first,
     * a blank name falls back to the code name, then to a plain
     * "Loadout" — a shortcut never publishes an empty label.
     * A budget at or below zero publishes nothing.
     */
    fun specsFor(profiles: List<FocusProfileEntity>, maxDynamic: Int): List<Spec> {
        if (maxDynamic <= 0) return emptyList()
        val seen = HashSet<Int>()
        return profiles.asSequence()
            .filter { seen.add(it.id) }
            .take(maxDynamic)
            .map { profile ->
                val label = profile.name.ifBlank { profile.codeName }
                    .ifBlank { "Loadout" }
                Spec(
                    id = SHORTCUT_ID_PREFIX + profile.id,
                    profileId = profile.id,
                    shortLabel = label,
                    longLabel = "Apply $label in Monolith"
                )
            }
            .toList()
    }

    /**
     * Replace the dynamic shortcut set with the loadouts'
     * current specs (setDynamicShortcuts semantics: the set is
     * the truth, so a deleted loadout's shortcut dies here).
     * An affordance must never crash the host: any platform
     * refusal (rate limit, host gone) is swallowed.
     */
    fun publish(context: Context, profiles: List<FocusProfileEntity>) {
        try {
            val budget = ShortcutManagerCompat
                .getMaxShortcutCountPerActivity(context) - STATIC_SHORTCUT_COUNT
            val infos = specsFor(profiles, budget).map { spec ->
                ShortcutInfoCompat.Builder(context, spec.id)
                    .setShortLabel(spec.shortLabel)
                    .setLongLabel(spec.longLabel)
                    .setIcon(
                        IconCompat.createWithResource(context, R.mipmap.ic_launcher)
                    )
                    .setIntent(
                        Intent(context, MainActivity::class.java).apply {
                            action = ACTION_OPEN_LOADOUT
                            putExtra(EXTRA_OPEN_LOADOUT_ID, spec.profileId)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                    )
                    .build()
            }
            ShortcutManagerCompat.setDynamicShortcuts(context, infos)
        } catch (_: Exception) {
            // No launcher surface (or a refused call): the app
            // loses an affordance, never its footing.
        }
    }
}
