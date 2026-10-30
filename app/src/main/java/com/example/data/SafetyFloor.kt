package com.example.data

/**
 * Issue #332 F5 (Audit R5 supplement — Flipd, second-pass): the
 * DOOR floor, and the floor's missing half.
 *
 * The tier WRITERS already have a dialler floor: EssentialApps
 * .floorTier (issue #119) stops any writer — loadout, profile,
 * cycle, set — from classifying the dialler / telephony stack
 * LOCKED (it may still hold PEEK_2M). What no writer-side floor
 * can do is govern the two enforcement DOORS, which read stored
 * tiers: a legacy row, or a PEEK row outside its lease, can still
 * put a wall in front of the in-call UI at the worst moment. And
 * two safety classes had no floor anywhere at all:
 *  - keyboards (the IME rendering inside an ALLOWED app — lock it
 *    and the allowed app becomes unusable, the phone untypeable);
 *  - the emergency surfaces themselves.
 *
 * This object is what the doors consult: a floor package is
 * never intercepted, by either door, whatever tier a row holds.
 * Dialer detection delegates to EssentialApps.isDialerPackage so
 * the studio keeps ONE definition of the telephony stack.
 *
 * Deliberately NOT floored: payments, ride-hailing, music,
 * messaging — Flipd exempts those by vendor decree (#329 F4);
 * here they stay the user's to lock. The floor is safety
 * machinery, never a content exemption.
 */
object SafetyFloor {

    private val imeExact = setOf(
        "com.samsung.android.honeyboard",
        "com.microsoft.swiftkey"
    )

    private val emergencyExact = setOf(
        "com.android.emergency",
        "com.google.android.apps.safetyhub"
    )

    fun isFloorPackage(packageName: String): Boolean {
        if (EssentialApps.isDialerPackage(packageName)) return true
        if (packageName in imeExact || packageName in emergencyExact) return true
        return packageName.contains(".telecom.") ||
            packageName.contains("inputmethod")
    }
}

/**
 * Issue #332 F1/F2 (Audit R5 supplement — Flipd, second-pass): the
 * shield door's foreground decision, as a pure function over the
 * UsageEvents stream.
 *
 * The pre-cure fallback read daily-aggregate `lastTimeUsed` with
 * an 8-second freshness cutoff: a blocked app opened and settled
 * into use went invisible to the fallback door for the rest of
 * its stay. Flipd's door reads the real event stream — typed
 * foreground events on a 20-second window. This is that decision,
 * minus the Android plumbing: given the window's events in any
 * order, the foreground app is the package of the LATEST
 * foreground event; it is reported only when it is blocked and
 * not exempt (safety floor / live-call surface). A later
 * foreground event for an allowed app clears an earlier blocked
 * one — the door answers "what is in front of the user NOW",
 * never "what was opened recently".
 */
object ForegroundDoor {

    /** UsageEvents.Event.ACTIVITY_RESUMED / MOVE_TO_FOREGROUND. */
    const val TYPE_FOREGROUND = 1

    /** The event window the shield door reads, in milliseconds. */
    const val WINDOW_MS = 20_000L

    data class FgEvent(val packageName: String, val type: Int, val timeStamp: Long)

    fun lastForegroundPackage(events: List<FgEvent>): String? =
        events.filter { it.type == TYPE_FOREGROUND }
            .maxByOrNull { it.timeStamp }
            ?.packageName

    fun foregroundBlocked(
        events: List<FgEvent>,
        blockedPackages: Set<String>,
        exemptPackages: Set<String> = emptySet()
    ): String? {
        val foreground = lastForegroundPackage(events) ?: return null
        if (foreground in exemptPackages) return null
        if (SafetyFloor.isFloorPackage(foreground)) return null
        return foreground.takeIf { it in blockedPackages }
    }
}
