package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomGuard

/**
 * P1 — THE STRONGROOM DAY, PERSISTED (BUILD-SR-3, DB 25→26).
 *
 * Four NEW tables beside the existing stores. MIGRATION_25_26 is
 * CREATE-only: no existing table is altered, so his loadouts,
 * rules, sessions and history carry by construction — the migration
 * proof in StrongroomMigrationP1Test seeds legacy rows and reads
 * them back untouched after 25→26.
 *
 * Rendering freedom for slices 2–8 is protected at this layer:
 * kinds and grades persist as NAMES (gap kind, punch kind), never
 * as render values (no pixels, no colours, no wording beyond his
 * own sentence, carried verbatim as data).
 *
 * CSV note: locked packages / gaps persist as delimited text, the
 * substrate's own habit (ProfileBundle's CSVs). Package names never
 * contain ',' or '|' — the encodings below rely on that platform
 * fact and round-trip exactly for every lawful package name.
 */

/** One signed day: the signing record + its seal + its strike.
 *  [state] is a [StrongroomDay.SealState] name once struck, or
 *  SIGNED (signed, not yet sealed) / SEALED while the day stands.
 *  Elapsed fields are monotonic-basis readings; wall fields are
 *  kept for the record and the plate, never for firing the bolts
 *  (the span is elapsed — see StrongroomDay.SealSpan). */
@Entity(tableName = "strongroom_days")
data class StrongroomDayEntity(
    @PrimaryKey val filingDateIso: String,
    val lockedPackagesCsv: String = "",
    val liftingHour: Int,
    val liftingMinute: Int,
    /** His sentence verbatim; null = the kept place stands. */
    val sentence: String? = null,
    val breakLengthMinutes: Int = 0,
    /** Offset into the sealed span; -1 = no break priced. */
    val breakFiresAtOffsetMillis: Long = -1L,
    val allowanceTotalMinutes: Int = 0,
    val allowanceSpentMinutes: Int = 0,
    /** kind|startOffset|durationMillis rows joined by ';'. */
    val gapsEncoded: String = "",
    val sealedAtWallMillis: Long = 0L,
    val sealedAtElapsedMillis: Long = 0L,
    val sealedBootCount: Int = -1,
    val spanDurationMillis: Long = 0L,
    val state: String = STATE_SIGNED,
    val struckHeldMillis: Long = 0L,
    val struckBracketGapMillis: Long = 0L,
    val crankSealBroken: Boolean = false,
    /** Provenance: the legacy session this day was carried from
     *  (LegacyCarry), -1 = signed fresh, never carried. */
    val carriedFromSessionId: Int = -1
) {
    companion object {
        const val STATE_SIGNED = "SIGNED"
        const val STATE_SEALED = "SEALED"
    }
}

/** One punched hole on a day's plate. Append-only in the store:
 *  insert + read, no update/delete path (a struck plate is a
 *  record, like the substrate's event logs). */
@Entity(
    tableName = "strongroom_punches",
    indices = [Index(value = ["filingDateIso"])]
)
data class StrongroomPunchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filingDateIso: String,
    /** A [StrongroomDay.PunchKind] name. */
    val kind: String,
    val atOffsetMillis: Long,
    val packageName: String = ""
)

/** The Guard's standing state — singleton row, id = 1, mirroring
 *  [StrongroomGuard.GuardState] field for field. The day-keyed
 *  purse fact (spentDateIso / spentMinutesThatDay) is fed from the
 *  day row by the store on every Guard read, so the hard-stop rule
 *  always reads the purse as it stands. */
@Entity(tableName = "strongroom_guard")
data class StrongroomGuardEntity(
    @PrimaryKey val id: Int = 1,
    val guardOn: Boolean = true,
    val allowanceMinutes: Int,
    val pendingAllowanceMinutes: Int = -1,
    val pendingAllowanceFromMillis: Long = 0L,
    val pendingOffFromMillis: Long = 0L,
    val stretchEndMillis: Long = 0L
)

/** The governor's crank, one row per week (MonolithWeek anchor).
 *  A legacy emergency-break spend against the same week makes this
 *  row born spent — see StrongroomStore.crankWeek, the carry. */
@Entity(tableName = "strongroom_crank_weeks")
data class StrongroomCrankWeekEntity(
    @PrimaryKey val weekAnchorMillis: Long,
    val spentSeconds: Int = 0,
    val sealBroken: Boolean = false,
    val spentAtWallMillis: Long = 0L,
    val carriedFromLegacySpend: Boolean = false
)

// ----------------------------------------------------------------------
// Encodings + mappers (pure; unit-tested with the model)
// ----------------------------------------------------------------------

object StrongroomEncoding {
    fun encodePackages(packages: Set<String>): String =
        packages.sorted().joinToString(",")

    fun decodePackages(csv: String): Set<String> =
        if (csv.isBlank()) emptySet() else csv.split(",").filter { it.isNotBlank() }.toSet()

    fun encodeGaps(gaps: List<StrongroomDay.ShapedGap>): String =
        gaps.joinToString(";") { "${it.kind.name}|${it.startOffsetMillis}|${it.durationMillis}" }

    fun decodeGaps(encoded: String): List<StrongroomDay.ShapedGap> {
        if (encoded.isBlank()) return emptyList()
        return encoded.split(";").mapNotNull { row ->
            val parts = row.split("|")
            if (parts.size != 3) return@mapNotNull null
            val kind = runCatching { StrongroomDay.GapKind.valueOf(parts[0]) }.getOrNull()
                ?: return@mapNotNull null
            val start = parts[1].toLongOrNull() ?: return@mapNotNull null
            val duration = parts[2].toLongOrNull() ?: return@mapNotNull null
            StrongroomDay.ShapedGap(kind, start, duration)
        }
    }
}

fun StrongroomDay.SigningRecord.toEntity(): StrongroomDayEntity = StrongroomDayEntity(
    filingDateIso = filingDateIso,
    lockedPackagesCsv = StrongroomEncoding.encodePackages(lockedPackages),
    liftingHour = liftingTime.hour,
    liftingMinute = liftingTime.minute,
    sentence = sentence,
    breakLengthMinutes = breakLengthMinutes,
    breakFiresAtOffsetMillis = breakFiresAtOffsetMillis,
    allowanceTotalMinutes = allowanceMinutes,
    gapsEncoded = StrongroomEncoding.encodeGaps(gaps)
)

fun StrongroomDayEntity.toSigningRecord(): StrongroomDay.SigningRecord = StrongroomDay.SigningRecord(
    filingDateIso = filingDateIso,
    lockedPackages = StrongroomEncoding.decodePackages(lockedPackagesCsv),
    liftingTime = java.time.LocalTime.of(liftingHour, liftingMinute),
    sentence = sentence,
    breakLengthMinutes = breakLengthMinutes,
    breakFiresAtOffsetMillis = breakFiresAtOffsetMillis,
    allowanceMinutes = allowanceTotalMinutes,
    gaps = StrongroomEncoding.decodeGaps(gapsEncoded)
)

/** The span of a sealed day row; null while the day is only signed. */
fun StrongroomDayEntity.toSealSpan(): StrongroomDay.SealSpan? =
    if (sealedBootCount < 0 || spanDurationMillis <= 0L) null
    else StrongroomDay.SealSpan(sealedAtElapsedMillis, sealedBootCount, spanDurationMillis)

fun StrongroomDayEntity.toPurse(): StrongroomDay.AllowancePurse =
    StrongroomDay.AllowancePurse(allowanceTotalMinutes, allowanceSpentMinutes)

fun StrongroomPunchEntity.toPunch(): StrongroomDay.Punch = StrongroomDay.Punch(
    kind = runCatching { StrongroomDay.PunchKind.valueOf(kind) }
        .getOrDefault(StrongroomDay.PunchKind.FLOOR),
    atOffsetMillis = atOffsetMillis,
    packageName = packageName
)

fun StrongroomGuardEntity.toGuardState(
    spentDateIso: String,
    spentMinutesThatDay: Int
): StrongroomGuard.GuardState = StrongroomGuard.GuardState(
    guardOn = guardOn,
    allowanceMinutes = allowanceMinutes,
    pendingAllowanceMinutes = pendingAllowanceMinutes,
    pendingAllowanceFromMillis = pendingAllowanceFromMillis,
    pendingOffFromMillis = pendingOffFromMillis,
    stretchEndMillis = stretchEndMillis,
    spentDateIso = spentDateIso,
    spentMinutesThatDay = spentMinutesThatDay
)

fun StrongroomGuard.GuardState.toEntity(): StrongroomGuardEntity = StrongroomGuardEntity(
    id = 1,
    guardOn = guardOn,
    allowanceMinutes = allowanceMinutes,
    pendingAllowanceMinutes = pendingAllowanceMinutes,
    pendingAllowanceFromMillis = pendingAllowanceFromMillis,
    pendingOffFromMillis = pendingOffFromMillis,
    stretchEndMillis = stretchEndMillis
)

fun StrongroomCrankWeekEntity.toCrankWeek(): StrongroomDay.GovernorCrankWeek =
    StrongroomDay.GovernorCrankWeek(
        weekAnchorMillis = weekAnchorMillis,
        spentSeconds = spentSeconds,
        sealBroken = sealBroken,
        spentAtWallMillis = spentAtWallMillis
    )
