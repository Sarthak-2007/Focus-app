package com.example.data

import com.example.domain.StrongroomDay
import com.example.domain.StrongroomGuard
import java.time.LocalTime
import java.time.ZoneId

/**
 * P1 — THE STRONGROOM STORE (BUILD-SR-3).
 *
 * The data-layer facade over the Strongroom tables + the legacy
 * stores. All day logic lives in the pure domain objects
 * ([StrongroomDay], [StrongroomGuard]); this store only persists
 * their facts and feeds them honest inputs (elapsed readings and
 * boot counts are PARAMETERS — the wiring layer reads the clocks,
 * so every store function is testable without a device).
 *
 * Clocks: sealing/striking take the caller's monotonic reading +
 * boot count. The store never reads SystemClock itself.
 */
class StrongroomStore(private val dao: MonolithDao) {

    // ------------------------------------------------------------------
    // Signing + sealing
    // ------------------------------------------------------------------

    suspend fun sign(signing: StrongroomDay.SigningRecord) {
        require(StrongroomDay.gapsAreLawful(signing.gaps, Long.MAX_VALUE)) {
            // Overlap is checked against the real span at sealing;
            // here only the shape law (positive, ordered) is cheap —
            // sealing re-checks against the struck span.
            "gaps must not overlap"
        }
        dao.insertStrongroomDay(signing.toEntity())
    }

    /**
     * Seal a signed day: strike the elapsed span from the honest
     * wall AT SEALING (the one wall consultation — [StrongroomDay]
     * .spanDurationAtSealing), freeze it, and mark the day SEALED.
     * Returns the struck span, or null when the day is unsigned or
     * its gaps do not fit the struck span (a signing error surfaces,
     * never a silently trimmed gap).
     */
    suspend fun seal(
        dateIso: String,
        sealedAtWallMillis: Long,
        sealedAtElapsedMillis: Long,
        bootCount: Int,
        zone: ZoneId = ZoneId.systemDefault()
    ): StrongroomDay.SealSpan? {
        val row = dao.getStrongroomDay(dateIso) ?: return null
        val signing = row.toSigningRecord()
        val duration = StrongroomDay.spanDurationAtSealing(sealedAtWallMillis, signing.liftingTime, zone)
        if (!StrongroomDay.gapsAreLawful(signing.gaps, duration)) return null
        dao.insertStrongroomDay(
            row.copy(
                sealedAtWallMillis = sealedAtWallMillis,
                sealedAtElapsedMillis = sealedAtElapsedMillis,
                sealedBootCount = bootCount,
                spanDurationMillis = duration,
                state = StrongroomDayEntity.STATE_SEALED
            )
        )
        return StrongroomDay.SealSpan(sealedAtElapsedMillis, bootCount, duration)
    }

    suspend fun day(dateIso: String): StrongroomDayEntity? = dao.getStrongroomDay(dateIso)

    suspend fun sealedDay(): StrongroomDayEntity? = dao.getSealedStrongroomDay()

    // ------------------------------------------------------------------
    // Punches + purse
    // ------------------------------------------------------------------

    suspend fun punch(dateIso: String, punch: StrongroomDay.Punch) {
        dao.insertStrongroomPunch(
            StrongroomPunchEntity(
                filingDateIso = dateIso,
                kind = punch.kind.name,
                atOffsetMillis = punch.atOffsetMillis,
                packageName = punch.packageName
            )
        )
    }

    suspend fun punches(dateIso: String): List<StrongroomDay.Punch> =
        dao.getStrongroomPunches(dateIso).map { it.toPunch() }

    /**
     * Spend from the day's purse. Returns the new purse, or null at
     * the hard stop (spend refused whole — the domain's rule, the
     * store persists only lawful spends).
     */
    suspend fun spendAllowance(dateIso: String, minutes: Int): StrongroomDay.AllowancePurse? {
        val row = dao.getStrongroomDay(dateIso) ?: return null
        val next = row.toPurse().spend(minutes) ?: return null
        if (next.spentMinutes != row.allowanceSpentMinutes) {
            dao.insertStrongroomDay(row.copy(allowanceSpentMinutes = next.spentMinutes))
        }
        return next
    }

    // ------------------------------------------------------------------
    // Striking the plate
    // ------------------------------------------------------------------

    /**
     * Strike the day's plate at lifting or failure: the domain
     * computes the truth (held, bracket gap, seal state); the store
     * persists the strike on the day row (state = the seal state's
     * name) and returns the plate. The crank's snapped seal is read
     * from the crank week containing the day.
     */
    suspend fun strikePlate(
        dateIso: String,
        nowElapsedMillis: Long,
        nowBootCount: Int,
        sealFailed: Boolean,
        zone: ZoneId = ZoneId.systemDefault()
    ): StrongroomDay.DayPlate? {
        val row = dao.getStrongroomDay(dateIso) ?: return null
        val span = row.toSealSpan() ?: return null
        val signing = row.toSigningRecord()
        val crank = crankWeek(row.sealedAtWallMillis, zone)
        val plate = StrongroomDay.strikePlate(
            signing = signing,
            seal = span,
            punches = punches(dateIso),
            crank = crank,
            nowElapsedMillis = nowElapsedMillis,
            nowBootCount = nowBootCount,
            sealFailed = sealFailed
        )
        dao.insertStrongroomDay(
            row.copy(
                state = plate.sealState.name,
                struckHeldMillis = plate.hoursHeldMillis,
                struckBracketGapMillis = plate.bracketGapMillis,
                crankSealBroken = plate.crankSealBroken
            )
        )
        return plate
    }

    // ------------------------------------------------------------------
    // The governor's crank — with the legacy carry
    // ------------------------------------------------------------------

    /**
     * The crank week containing [nowMillis]. CARRY: when no
     * Strongroom row exists yet for the week, the legacy token
     * ledger is consulted — an emergency-break spend booked against
     * this week (MonolithWeek's two-arm predicate, the substrate's
     * own truth) makes the crank born SPENT with its seal broken
     * and [StrongroomCrankWeekEntity.carriedFromLegacySpend] set.
     * His spent minute is never refunded by the redesign.
     */
    suspend fun crankWeek(
        nowMillis: Long,
        zone: ZoneId = ZoneId.systemDefault()
    ): StrongroomDay.GovernorCrankWeek {
        val anchor = MonolithWeek.weekAnchorMillis(nowMillis, zone)
        dao.getStrongroomCrankWeek(anchor)?.let { return it.toCrankWeek() }
        val nextAnchor = MonolithWeek.nextWeekAnchorMillis(nowMillis, zone)
        val legacySpend = dao.getSpendsForWeek(anchor, nextAnchor)
            .firstOrNull {
                MonolithWeek.spendCountsAgainstWeek(it, anchor, nextAnchor)
            }
        val row = if (legacySpend != null) {
            StrongroomCrankWeekEntity(
                weekAnchorMillis = anchor,
                spentSeconds = StrongroomDay.CRANK_SECONDS,
                sealBroken = true,
                spentAtWallMillis = legacySpend.spentAtWallMillis,
                carriedFromLegacySpend = true
            )
        } else {
            StrongroomCrankWeekEntity(weekAnchorMillis = anchor)
        }
        dao.insertStrongroomCrankWeek(row)
        return row.toCrankWeek()
    }

    /** Spend the week's minute; null when already spent. */
    suspend fun spendCrank(
        nowMillis: Long,
        zone: ZoneId = ZoneId.systemDefault()
    ): StrongroomDay.GovernorCrankWeek? {
        val week = crankWeek(nowMillis, zone)
        val spent = week.spend(nowMillis) ?: return null
        dao.insertStrongroomCrankWeek(
            StrongroomCrankWeekEntity(
                weekAnchorMillis = spent.weekAnchorMillis,
                spentSeconds = spent.spentSeconds,
                sealBroken = spent.sealBroken,
                spentAtWallMillis = spent.spentAtWallMillis,
                carriedFromLegacySpend = false
            )
        )
        return spent
    }

    // ------------------------------------------------------------------
    // The Guard — transitions persisted, pending applied on read
    // ------------------------------------------------------------------

    /**
     * The Guard state as it stands at [nowMillis]: pending changes
     * whose day-start has arrived are applied (and persisted) on
     * read; the purse fact is fed from today's day row so the
     * hard-stop rule reads the purse as it stands. A Guard row is
     * born on first read from the legacy settings: the allowance
     * seeds from the legacy peek minutes' purse shape — NO: the
     * allowance is its own fact; it seeds at [DEFAULT_ALLOWANCE_MINUTES]
     * when no row exists, and the legacy guard row's on/off posture
     * (browserBlockerEnabled is a different switch and is NOT read
     * as the Guard's — conflating them would invent a ruling).
     */
    suspend fun guardState(nowMillis: Long): StrongroomGuard.GuardState {
        val todayIso = StrongroomDay.filingDateIso(nowMillis)
        val dayRow = dao.getStrongroomDay(todayIso)
        var entity = dao.getStrongroomGuard()
        if (entity == null) {
            entity = StrongroomGuardEntity(allowanceMinutes = DEFAULT_ALLOWANCE_MINUTES)
            dao.insertStrongroomGuard(entity)
        }
        val fed = entity.toGuardState(
            spentDateIso = dayRow?.filingDateIso ?: "",
            spentMinutesThatDay = dayRow?.allowanceSpentMinutes ?: 0
        )
        val applied = StrongroomGuard.applyPending(fed, nowMillis)
        if (applied.toEntity() != entity) dao.insertStrongroomGuard(applied.toEntity())
        return applied
    }

    suspend fun tightenGuard(nowMillis: Long, newAllowanceMinutes: Int): StrongroomGuard.GuardState {
        val state = StrongroomGuard.tighten(guardState(nowMillis), newAllowanceMinutes)
        dao.insertStrongroomGuard(state.toEntity())
        return state
    }

    suspend fun requestGuardRaise(
        nowMillis: Long,
        newAllowanceMinutes: Int,
        zone: ZoneId = ZoneId.systemDefault()
    ): Pair<StrongroomGuard.GuardState, StrongroomGuard.RaiseRequest> {
        val (state, result) = StrongroomGuard.requestRaise(
            state = guardState(nowMillis),
            newAllowanceMinutes = newAllowanceMinutes,
            todayIso = StrongroomDay.filingDateIso(nowMillis, zone),
            nextDayStartMillis = StrongroomDay.nextDayStartMillis(nowMillis, zone)
        )
        dao.insertStrongroomGuard(state.toEntity())
        return state to result
    }

    suspend fun requestGuardOff(
        nowMillis: Long,
        sealedBlockActive: Boolean,
        zone: ZoneId = ZoneId.systemDefault()
    ): Pair<StrongroomGuard.GuardState, StrongroomGuard.OffRequest> {
        val (state, result) = StrongroomGuard.requestOff(
            state = guardState(nowMillis),
            nowMillis = nowMillis,
            sealedBlockActive = sealedBlockActive,
            nextDayStartMillis = StrongroomDay.nextDayStartMillis(nowMillis, zone)
        )
        dao.insertStrongroomGuard(state.toEntity())
        return state to result
    }

    suspend fun turnGuardOn(nowMillis: Long): StrongroomGuard.GuardState {
        val state = StrongroomGuard.turnOn(guardState(nowMillis))
        dao.insertStrongroomGuard(state.toEntity())
        return state
    }

    suspend fun beginGuardStretch(
        nowMillis: Long,
        endMillis: Long
    ): StrongroomGuard.GuardState {
        val state = StrongroomGuard.beginStretch(guardState(nowMillis), endMillis, nowMillis)
        dao.insertStrongroomGuard(state.toEntity())
        return state
    }

    // ------------------------------------------------------------------
    // The drawer — stored, unscored, untotalled
    // ------------------------------------------------------------------

    /**
     * The drawer's stored tasks for a page of history: the legacy
     * focus_tasks COMPLETED rows, re-expressed as [StrongroomDay
     * .DrawerEntry]. The mapping drops every numeric field the
     * legacy row carries (estimates, accruals) — the drawer entry
     * type has no field that could hold them.
     */
    suspend fun drawerPage(fromMillis: Long, toMillis: Long, limit: Int, offset: Int): List<StrongroomDay.DrawerEntry> =
        dao.getStoredPage(fromMillis, toMillis, limit, offset).map {
            StrongroomDay.DrawerEntry(taskId = it.id, title = it.title, completedAtMillis = it.completedAtMillis)
        }

    companion object {
        /** The allowance a fresh Guard row is born with, before any
         *  signing prices a day: the substrate's shipped default
         *  daily limit (BlockedAppEntity.dailyLimitMinutes = 30). */
        const val DEFAULT_ALLOWANCE_MINUTES: Int = 30
    }
}

/**
 * THE CARRY (P1 migration of MEANING, beside the schema migration):
 * derives a Strongroom signing from the legacy rows his current app
 * holds, so the first Strongroom day is built from HIS loadout —
 * never a blank slate, never an invented value:
 *
 *  - locked set ← blocked_apps rows whose resolved tier is LOCKED;
 *  - break length ← guard_settings.breakMinutes (the break length
 *    his current app actually runs);
 *  - sentence ← the active session's intention when one stands
 *    (his own words for a day, carried — otherwise the kept place);
 *  - lifting hour ← the active session's window end when one
 *    stands, else 06:00 (named here as the carry's stated default:
 *    his timetable's first sitting follows the 03:30 day-start and
 *    the morning shapes; a fresh signing replaces it in a minute);
 *  - allowance ← the Guard's standing allowance.
 *
 * Pure over plain inputs; the caller (first-signing flow / P9
 * migration proof) feeds it DAO snapshots and persists the result
 * with [StrongroomDayEntity.carriedFromSessionId] as provenance.
 */
object LegacyCarry {

    val DEFAULT_LIFTING_TIME: LocalTime = LocalTime.of(6, 0)

    fun deriveSigning(
        filingDateIso: String,
        blockedApps: List<BlockedAppEntity>,
        guardSettings: GuardSettingsEntity?,
        activeSession: FocusSessionEntity?,
        guardAllowanceMinutes: Int,
        activeSessionWindowEnd: LocalTime?
    ): StrongroomDay.SigningRecord {
        val locked = blockedApps
            .filter { it.resolvedTier == AppBlockTier.LOCKED }
            .map { it.packageName }
            .toSet()
        val sentence = activeSession?.intention?.takeIf { it.isNotBlank() }
        return StrongroomDay.SigningRecord(
            filingDateIso = filingDateIso,
            lockedPackages = locked,
            liftingTime = activeSessionWindowEnd ?: DEFAULT_LIFTING_TIME,
            sentence = sentence,
            breakLengthMinutes = guardSettings?.breakMinutes ?: 10,
            breakFiresAtOffsetMillis = -1L,
            allowanceMinutes = guardAllowanceMinutes,
            gaps = emptyList()
        )
    }
}
