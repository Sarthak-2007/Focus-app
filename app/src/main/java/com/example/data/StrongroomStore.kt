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
    // Composition + signing (amendment) + sealing (the swing)
    // ------------------------------------------------------------------

    /**
     * Compose [dateIso] from the week's standing order (B-2): the
     * day stands whether or not he comes to the door. Idempotent —
     * a day already standing (composed, amended, or sealed) is
     * returned as it stands, never recomposed (the restart law,
     * B-10, is this same rule at the store's door).
     */
    suspend fun composeDay(
        order: com.example.domain.StrongroomComposition.WeekStandingOrder,
        dateIso: String
    ): com.example.domain.StrongroomComposition.ComposedDay {
        dao.getStrongroomDay(dateIso)?.let { row ->
            return com.example.domain.StrongroomComposition.ComposedDay(
                filingDateIso = dateIso,
                shape = runCatching {
                    com.example.domain.StrongroomComposition.DayShape.valueOf(row.dayShape)
                }.getOrDefault(com.example.domain.StrongroomComposition.DayShape.ORDINARY),
                record = row.toSigningRecord(),
                amendedBySigning = row.amendedBySigning
            )
        }
        val composed = com.example.domain.StrongroomComposition.compose(order, dateIso)
        dao.insertStrongroomDay(
            composed.record.toEntity(dayShape = composed.shape.name, amendedBySigning = false)
                .copy(state = StrongroomDayEntity.STATE_COMPOSED)
        )
        return composed
    }

    /**
     * The evening signing AMENDS a standing composition (B-2): the
     * named fields move, the rest stands as composed. When no
     * composition stands yet (the signing is the first touch of
     * the day), the amendment is applied over [order]'s composition
     * — the signing still never creates the day from nothing.
     * Returns the amended day, or null once the day is sealed (a
     * sealed day is amended by no one).
     */
    suspend fun amendDay(
        order: com.example.domain.StrongroomComposition.WeekStandingOrder,
        dateIso: String,
        amendment: com.example.domain.StrongroomComposition.SigningAmendment
    ): com.example.domain.StrongroomComposition.ComposedDay? {
        val composed = composeDay(order, dateIso)
        val row = dao.getStrongroomDay(dateIso) ?: return null
        if (row.state == StrongroomDayEntity.STATE_SEALED ||
            row.state in StrongroomDayEntity.STRUCK_STATES
        ) return null
        amendment.gaps?.let {
            require(StrongroomDay.gapsFitAnySpan(it)) {
                "in-span gaps must not overlap and must fit a day's span"
            }
        }
        val amended = com.example.domain.StrongroomComposition.amend(composed, amendment)
        dao.insertStrongroomDay(
            row.copy(
                lockedPackagesCsv = StrongroomEncoding.encodePackages(amended.record.lockedPackages),
                allowedPackagesCsv = StrongroomEncoding.encodeOrderedPackages(amended.record.allowedPackages),
                liftingHour = amended.record.liftingTime.hour,
                liftingMinute = amended.record.liftingTime.minute,
                sentence = amended.record.sentence,
                breakLengthMinutes = amended.record.breakLengthMinutes,
                breakFiresAtOffsetMillis = amended.record.breakFiresAtOffsetMillis,
                allowanceTotalMinutes = amended.record.allowanceMinutes,
                allowanceSpentMinutes = minOf(row.allowanceSpentMinutes, amended.record.allowanceMinutes),
                gapsEncoded = StrongroomEncoding.encodeGaps(amended.record.gaps),
                taskLine = amended.record.taskLine,
                amendedBySigning = true,
                state = StrongroomDayEntity.STATE_SIGNED
            )
        )
        return amended
    }

    /**
     * Persist a fully-formed effective record (the carry and the
     * first-signing flow build these). [amended] marks whether an
     * evening signing produced it; a composed record persists with
     * the composed state. Sealing never consults either fact.
     */
    suspend fun sign(signing: StrongroomDay.SigningRecord, amended: Boolean = true) {
        require(StrongroomDay.gapsFitAnySpan(signing.gaps)) {
            // The calm-hour surface (CRITIC-SR-6 cure): in-span gaps
            // that overlap, or that no span of a day could ever
            // contain, are refused HERE — at signing — never
            // discovered at the first sitting.
            "in-span gaps must not overlap and must fit a day's span"
        }
        dao.insertStrongroomDay(
            signing.toEntity(amendedBySigning = amended).copy(
                state = if (amended) StrongroomDayEntity.STATE_SIGNED
                else StrongroomDayEntity.STATE_COMPOSED
            )
        )
    }

    /**
     * THE SWING: seal a standing day at the first study sitting.
     * Strike the elapsed span from the honest wall AT SEALING (the
     * one wall consultation — [StrongroomDay]
     * .spanDurationAtSealing), freeze it, and mark the day SEALED.
     * The day may be COMPOSED (never signed) or SIGNED (amended):
     * the app never checks whether the evening happened (B-2), so
     * this function reads no signing fact. Returns the struck span,
     * or null only when no day stands — the swing itself never
     * refuses (see the gap note at the strike below).
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
        // The swing NEVER fails for a gap (CRITIC-SR-6 cure, with a
        // named divergence from the prescribed form): the span's
        // length is a function of the sealing instant, which no
        // signing can compute without inventing a swing hour — so
        // fit-at-signing is enforced only where it IS computable
        // (overlap + the 24h bound, in sign/amend). Here, a hold
        // made late seals what is left of the day (refit Beat 2):
        // an in-span gap the shortened span never reaches is an
        // unreached shape, inert by gapAt's own arithmetic — it
        // opens nothing, strikes nothing, and the seal stands.
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
     *
     * STRUCK ONCE, NEVER RE-STRUCK (B-14): a day whose plate
     * already stands returns the plate as struck — its stored held
     * / bracket facts and its state — and nothing is written. Late
     * standings are confessed as they stand, never backdated into
     * a second strike.
     */
    /** Whether a strike landed fresh or the plate already stood —
     *  the distinction a caller routes a late standing on (a fresh
     *  strike is the day's truth arriving; an already-standing
     *  plate confessed late is a confession, routed by the caller,
     *  BUILD-SR-4's pattern). [strikePlate] keeps the plain plate
     *  shape for its existing callers. */
    sealed interface StrikeOutcome {
        val plate: StrongroomDay.DayPlate
        data class FreshlyStruck(override val plate: StrongroomDay.DayPlate) : StrikeOutcome
        data class AlreadyStanding(override val plate: StrongroomDay.DayPlate) : StrikeOutcome
    }

    suspend fun strikePlate(
        dateIso: String,
        nowElapsedMillis: Long,
        nowBootCount: Int,
        sealFailed: Boolean,
        zone: ZoneId = ZoneId.systemDefault()
    ): StrongroomDay.DayPlate? = strikePlateWithStanding(
        dateIso, nowElapsedMillis, nowBootCount, sealFailed, zone
    )?.plate

    suspend fun strikePlateWithStanding(
        dateIso: String,
        nowElapsedMillis: Long,
        nowBootCount: Int,
        sealFailed: Boolean,
        zone: ZoneId = ZoneId.systemDefault()
    ): StrikeOutcome? {
        val row = dao.getStrongroomDay(dateIso) ?: return null
        if (row.state in StrongroomDayEntity.STRUCK_STATES) {
            // The plate as struck, re-read — never re-struck (B-14).
            return StrikeOutcome.AlreadyStanding(
                StrongroomDay.DayPlate(
                filingDateIso = dateIso,
                hoursHeldMillis = row.struckHeldMillis,
                punches = punches(dateIso),
                sealState = runCatching { StrongroomDay.SealState.valueOf(row.state) }
                    .getOrDefault(StrongroomDay.SealState.BROKEN),
                crankSealBroken = row.crankSealBroken,
                bracketGapMillis = row.struckBracketGapMillis,
                    taskLine = row.taskLine
                )
            )
        }
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
        return StrikeOutcome.FreshlyStruck(plate)
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
        // CH-1 (CRITIC-SR-5, riding to P2): the civil week anchor
        // can be clock-minted forward — a wall clock jumped ahead
        // must not mint a fresh minute against elapsed truth. The
        // substrate's own law for the legacy token is
        // TokenWeekGuard's: a week that SKIPS PAST a spent marker
        // is not believed until the interval is corroborated. Here,
        // with the crank rows as the marker: when the newest prior
        // crank week stands SPENT and the claimed week begins more
        // than one lawful week after it (a skip, not the adjacent
        // rollover), the evaluation floors at the marker — the
        // crank reads SPENT for the claimed week and no fresh row
        // is born. Adjacent rollover and skips past an UNSPENT
        // marker mint nothing and stand fresh, as TokenWeekGuard
        // rules for the token.
        // CURED AT COMPOSITION (BUILD-SR-13): the floor reads the
        // newest SPENT marker, not the newest prior row — a fresh
        // row born on an intervening week's read (this function
        // inserts one below) otherwise stands between the marker
        // and a minted week and the floor never fires (the CH-1
        // test's failure on PR #841 head 0f1dd0e6, run 41422275346:
        // the adjacent week's read birthed its unspent row, and
        // the +3-week mint read fresh past the spent marker).
        dao.getLatestSpentStrongroomCrankWeekBefore(anchor)?.let { prior ->
            val oneWeekMillis = 7L * 24 * 3_600_000L
            if (anchor > prior.weekAnchorMillis + oneWeekMillis) {
                return StrongroomDay.GovernorCrankWeek(
                    weekAnchorMillis = anchor,
                    spentSeconds = StrongroomDay.CRANK_SECONDS,
                    sealBroken = true,
                    spentAtWallMillis = prior.spentAtWallMillis
                )
            }
        }
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
            spentMinutesThatDay = dayRow?.allowanceSpentMinutes ?: 0,
            dayPurseTotalMinutes = dayRow?.allowanceTotalMinutes ?: -1
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
 *    stands. WHERE HIS DATA HOLDS NO LIFTING HOUR, THE CARRY
 *    WAITS FOR HIS SIGNING — it never invents the day's most
 *    important number (CRITIC-SR-6's cure: the first form of this
 *    carry defaulted to an invented 06:00, which would have lifted
 *    a carried day mid-block; struck, not patched over);
 *  - allowance ← the Guard's standing allowance.
 *
 * Pure over plain inputs; the caller (first-signing flow / P9
 * migration proof) feeds it DAO snapshots and persists the result
 * with [StrongroomDayEntity.carriedFromSessionId] as provenance.
 * Returns null when no lifting hour stands in his data: no
 * carried day is composed that day — the standing order's
 * composition (which carries its own composed hours) and his
 * signing remain the day's sources, in their own rights.
 */
object LegacyCarry {

    fun deriveSigning(
        filingDateIso: String,
        blockedApps: List<BlockedAppEntity>,
        guardSettings: GuardSettingsEntity?,
        activeSessionIntention: String?,
        guardAllowanceMinutes: Int,
        activeSessionWindowEnd: LocalTime?
    ): StrongroomDay.SigningRecord? {
        val lifting = activeSessionWindowEnd ?: return null
        val locked = blockedApps
            .filter { it.resolvedTier == AppBlockTier.LOCKED }
            .map { it.packageName }
            .toSet()
        // The signed allowed set, carried: every non-LOCKED row,
        // in the catalogue's own order (the DAO's A–Z) — his
        // loadout's order, not a complement computed at read time.
        val allowed = blockedApps
            .filter { it.resolvedTier != AppBlockTier.LOCKED }
            .map { it.packageName }
        return StrongroomDay.SigningRecord(
            filingDateIso = filingDateIso,
            lockedPackages = locked,
            allowedPackages = allowed,
            liftingTime = lifting,
            sentence = activeSessionIntention?.takeIf { it.isNotBlank() },
            breakLengthMinutes = guardSettings?.breakMinutes ?: 10,
            breakFiresAtOffsetMillis = -1L,
            allowanceMinutes = guardAllowanceMinutes,
            gaps = emptyList()
        )
    }
}
