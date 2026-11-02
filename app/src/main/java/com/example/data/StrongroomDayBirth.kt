package com.example.data

import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * THE DAY'S BIRTH — the production producer (DAYBIRTH, 2026-11-03).
 *
 * Until this file existed, NOTHING in production ever composed a
 * Strongroom day: [StrongroomStore.composeDay] had no production
 * caller, [LegacyCarry.deriveSigning] was exercised only by the
 * P9 proofs, and migration 25→26 creates the Strongroom tables
 * empty — so on the phone no day row could ever come to exist,
 * the signing host rendered nothing (`day ?: return`), and every
 * downstream surface (swing, sealed face, antechamber, break,
 * lifting, drawer) was unreachable (FLOW-SEAT's whole-day walk,
 * step 1: DEAD).
 *
 * This producer is the composition root's missing half. It runs
 * at the app's entry paths (the shell's doorway/seal fact loads
 * and the signing host's load — each ensures before it reads, so
 * the day stands whether or not he comes to the door, B-2). It
 * births, through the store's OWN compose path:
 *
 *  - TODAY's filing day — the day the morning's swing seals
 *    ([StrongroomDay.filingDateIso] is the one seam, 03:30); and
 *  - TOMORROW's filing day — because the signing is the EVENING's
 *    act for tomorrow's day: the page he settles tonight is the
 *    page that stands at tomorrow's first sitting.
 *
 * The standing order is built from HIS loadout, by the one lawful
 * derivation the engine already holds: [LegacyCarry.deriveSigning]
 * over the DAO's snapshots (blocked apps · guard settings · the
 * active session's intention and window end · the Guard's standing
 * allowance read through the store's own Guard verb). The derived
 * record becomes the week's ORDINARY template; every weekday
 * carries that shape until a real standing-order surface exists.
 *
 * LAWS HELD:
 *  - One authoritative state: the store. This object writes
 *    nothing except through [StrongroomStore.composeDay], whose
 *    idempotence is the restart law — a day already standing
 *    (composed, signed, sealed, struck) is returned as it stands,
 *    never recomposed. Re-ensuring is two reads and no writes.
 *  - No invented lifting hour: when his data holds no window end
 *    (no active session, no session window, no profile window),
 *    [LegacyCarry] returns null and NO day is born — the carry's
 *    own law (it never invents the day's most important number).
 *    The birth reports [Outcome.NotBorn] and the entry paths read
 *    the honest empty doorway until his data can carry a day.
 *  - No Guard shortcut: the allowance is read through
 *    [StrongroomStore.guardState], the Guard's own read verb —
 *    no verb is bypassed, tightened, or raised here.
 *  - A composed day is NOT a signing: composeDay persists the
 *    COMPOSED state with amendedBySigning = false, so the
 *    first-signing fact (and its teaching) survives the birth.
 */
object StrongroomDayBirth {

    /** What an ensure did — named so callers and proofs can read
     *  the birth's truth without re-reading the store. */
    sealed interface Outcome {
        /** Both days stand (born now, or already standing). */
        data class Standing(
            val today: StrongroomComposition.ComposedDay,
            val tomorrow: StrongroomComposition.ComposedDay
        ) : Outcome

        /** No day was born: his data holds no lifting hour for
         *  the carry to carry. Nothing was written. */
        data object NotBorn : Outcome
    }

    /**
     * Ensure today's and tomorrow's filing days stand composed.
     * [nowMillis] is the caller's wall reading — this function
     * reads no clock of its own, so the proof drives the same
     * birth the phone runs.
     */
    suspend fun ensureStandingDays(
        store: StrongroomStore,
        dao: MonolithDao,
        nowMillis: Long,
        zone: ZoneId = ZoneId.systemDefault()
    ): Outcome {
        val todayIso = StrongroomDay.filingDateIso(nowMillis, zone)
        val tomorrowIso = LocalDate.parse(todayIso).plusDays(1).toString()

        val todayRow = store.day(todayIso)
        val tomorrowRow = store.day(tomorrowIso)
        if (todayRow != null && tomorrowRow != null) {
            return Outcome.Standing(
                today = standingAsComposed(todayRow),
                tomorrow = standingAsComposed(tomorrowRow)
            )
        }

        val order = standingOrder(store, dao, todayIso, nowMillis, zone)
            ?: return Outcome.NotBorn

        // composeDay is idempotent per date: a day already standing
        // returns as it stands; only the missing one is born.
        val today = store.composeDay(order, todayIso)
        val tomorrow = store.composeDay(order, tomorrowIso)
        return Outcome.Standing(today = today, tomorrow = tomorrow)
    }

    /** A standing row re-expressed as its composed day — the same
     *  re-reading composeDay itself performs on a standing row. */
    private fun standingAsComposed(
        row: StrongroomDayEntity
    ): StrongroomComposition.ComposedDay = StrongroomComposition.ComposedDay(
        filingDateIso = row.filingDateIso,
        shape = runCatching {
            StrongroomComposition.DayShape.valueOf(row.dayShape)
        }.getOrDefault(StrongroomComposition.DayShape.ORDINARY),
        record = row.toSigningRecord(),
        amendedBySigning = row.amendedBySigning
    )

    /**
     * The week's standing order, from his loadout: the carry's
     * derived signing for [anyDateIso] becomes the ORDINARY
     * template every weekday carries. Null when the carry cannot
     * derive (no lifting hour in his data) — no order, no birth,
     * no invented hour.
     */
    private suspend fun standingOrder(
        store: StrongroomStore,
        dao: MonolithDao,
        anyDateIso: String,
        nowMillis: Long,
        zone: ZoneId
    ): StrongroomComposition.WeekStandingOrder? {
        val activeSession = dao.getActiveSessionSnapshot()
        val derived = LegacyCarry.deriveSigning(
            filingDateIso = anyDateIso,
            blockedApps = dao.getAllAppsSnapshot(),
            guardSettings = dao.getGuardSettingsSnapshot(),
            activeSessionIntention = activeSession?.intention,
            guardAllowanceMinutes = store.guardState(nowMillis).allowanceMinutes,
            activeSessionWindowEnd = activeWindowEnd(
                session = activeSession,
                profiles = dao.getAllProfilesSnapshot(),
                zone = zone
            )
        ) ?: return null

        val template = StrongroomComposition.DayTemplate(
            lockedPackages = derived.lockedPackages,
            allowedPackages = derived.allowedPackages,
            liftingTime = derived.liftingTime,
            breakLengthMinutes = derived.breakLengthMinutes,
            breakFiresAtOffsetMillis = derived.breakFiresAtOffsetMillis,
            allowanceMinutes = derived.allowanceMinutes,
            gaps = derived.gaps,
            taskLine = null // the composition carries no task line; the signing's word is the signing's
        )
        return StrongroomComposition.WeekStandingOrder(
            shapeByWeekday = DayOfWeek.values().associateWith {
                StrongroomComposition.DayShape.ORDINARY
            },
            templateByShape = mapOf(
                StrongroomComposition.DayShape.ORDINARY to template
            )
        )
    }

    /**
     * The active session's window end, from the session's own
     * frozen facts — the same sources the engine already trusts:
     * the routine window snapshot (windowEndMillis) when it
     * stands, else the session's planned span (start + target),
     * else the window of the profile the session runs under.
     * Null when none stands: the carry then refuses, by its law.
     */
    private fun activeWindowEnd(
        session: FocusSessionEntity?,
        profiles: List<FocusProfileEntity>,
        zone: ZoneId
    ): LocalTime? {
        session ?: return null
        val endMillis = when {
            session.windowEndMillis > session.windowStartMillis ->
                session.windowEndMillis
            session.startWallTimeMillis > 0L && session.targetDurationSeconds > 0 ->
                session.startWallTimeMillis + session.targetDurationSeconds * 1000L
            else -> 0L
        }
        if (endMillis > 0L) {
            return Instant.ofEpochMilli(endMillis).atZone(zone).toLocalTime()
        }
        val profile = profiles.firstOrNull { it.name == session.profileName }
            ?: return null
        // endHour may be 24 (the profile's own coercion): the
        // window ends at the seam of the next day — LocalTime 00:00.
        return LocalTime.of(profile.endHour % 24, profile.endMinute)
    }
}
