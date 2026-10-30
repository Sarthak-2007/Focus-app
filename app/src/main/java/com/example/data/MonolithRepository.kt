package com.example.data

import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Process
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.os.PowerManager
import android.telecom.TelecomManager
import com.example.service.MonolithAccessibilityService
import com.example.service.MonolithBootReceiver
import com.example.service.MonolithDeviceAdminReceiver
import com.example.service.MonolithNotificationListener
import com.example.service.MonolithShieldService
import com.example.service.TimeCriticalAlertGate
import com.example.service.UsageAccess
import com.example.service.Warrant
import com.example.service.WatchItemId
import com.example.service.WatchStore
import androidx.annotation.WorkerThread
import androidx.room.withTransaction
import com.example.domain.FocusDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

data class SystemGuardStatus(
    val hasUsageStatsPermission: Boolean = false,
    val hasOverlayPermission: Boolean = false,
    val isAccessibilityServiceEnabled: Boolean = false,
    val isNotificationListenerEnabled: Boolean = false,
    val isDeviceAdminEnabled: Boolean = false,
    // Issue #28: unmeasured is NOT intact. The old defaults (true /
    // true / 1 / 12) let every surface print security facts that
    // were never checked. Unknown now reads as unknown.
    val isMonotonicClockIntact: Boolean = false,
    val isHmacSignatureValid: Boolean = false,
    val clockDriftSeconds: Long = 0L,
    val systemBootCount: Int = 0,
    val installedLauncherAppsCount: Int = 0,
    /** Issue #81 F1: this install was resurrected after a data
     *  wipe (virgin DB + pre-existing keystore key). The guard
     *  status must carry it: a wiped install is UNPROVEN, and no
     *  surface may read its all-green rows as a clean history. */
    val installProvenanceUnproven: Boolean = false
) {
    val readinessCount: Int
        get() = listOf(
            isAccessibilityServiceEnabled,
            hasUsageStatsPermission,
            isNotificationListenerEnabled,
            isDeviceAdminEnabled
        ).count { it }
}

/**
 * The three outcomes of a valve-completed emergency break (TOKEN-SPEC
 * §4.1) — exactly these, no fourth. [RefusedNoToken] leaves the session
 * sealed and writes nothing; [Completed] means the spend row and the
 * disarm committed in the same transaction.
 */
sealed interface BreakResult {
    object Completed : BreakResult
    object RefusedNoToken : BreakResult
    object NoActiveSession : BreakResult
}

/**
 * The prune's record (Ordner lane, Mikro M5): how many rows the last
 * unannounced prune(s) took off the app list, and whether any stored
 * ruling went with them. Accumulates in prefs until the Shield strip
 * has been shown and dismissed — the strip prints this record and
 * nothing beyond it.
 */
data class PruneNote(
    val appsRemoved: Int,
    val rulesRemoved: Boolean
)

/**
 * W3 #130 (browser-as-loophole, finding 5): the held-sites count a
 * receipt may claim — the stored list's size ONLY when the browser
 * blocker is actually on. With the blocker switched off the
 * session holds no sites at all, whatever the list holds; claiming
 * the list size printed held sites that were never once checked.
 * Pure, mirroring countHeldApps' one-derivation law.
 */
internal fun heldSitesCount(siteCount: Int, browserBlockerEnabled: Boolean): Int =
    if (browserBlockerEnabled) siteCount else 0

/**
 * W3 #130 finding 2 — the seed-upgrade for EXISTING installs
 * (verifier correction, DURCH-D): the v1 site seed is once-ever
 * (SEED_SITES_KEY), so an install seeded before "youtube.com"
 * joined [BlockedBrowserDomains.defaultDomains] would keep the
 * legacy list forever and never receive the host rule. This pure
 * decision is the delivery path: given the install's stored
 * domains, the domains the one-shot v2 upgrade must ADD.
 *
 * The trigger is hard evidence, not a guess: the legacy factory
 * entry "youtube.com/shorts" still stands in the list, proving
 * the factory seed is in force and unedited at that entry. The
 * seeded-ever law (Audit R2 #96) bounds it the other way — a
 * list WITHOUT the legacy entry is the user's own editing (he
 * deleted the Shorts rule, or emptied the list, or built his
 * own), and the upgrade adds NOTHING: a deletion is a ruling,
 * never resurrected by an upgrade. A list already carrying the
 * host is complete. Pure, so the matrix is unit-tested.
 */
internal fun youtubeHostSeedUpgrade(existingDomains: Collection<String>): List<String> =
    if ("youtube.com/shorts" in existingDomains && "youtube.com" !in existingDomains) {
        listOf("youtube.com")
    } else {
        emptyList()
    }

/** Issue #99 F1: how long after a peek lease's end its package still
 *  counts as "just expired" for eviction purposes. The shield polls
 *  every 2s, so this only needs to cover the first polls after
 *  expiry; 15 minutes is generous without resurrecting old leases. */
const val EXPIRED_PEEK_EVICTION_WINDOW_MILLIS = 15 * 60 * 1000L

/**
 * Issue #99 F1 (pure, unit-tested): the packages whose peek lease
 * has expired inside the eviction window — lease end recorded,
 * in the past, and not so far in the past that the row is history.
 * A lease end of 0 (never leased / already closed) never qualifies.
 */
internal fun expiredPeekPackages(
    apps: List<BlockedAppEntity>,
    nowMono: Long,
    windowMillis: Long = EXPIRED_PEEK_EVICTION_WINDOW_MILLIS
): Set<String> = apps
    .filter { app ->
        val end = app.activePeekUntilElapsedRealtime
        end > 0L && end <= nowMono && (nowMono - end) <= windowMillis
    }
    .map { it.packageName }
    .toSet()

/**
 * Issue #90 (complexity F5, classifier extraction): the ending
 * write's outcome classification in updateSessionProgress, as a
 * pure function of the row's own facts (extracted from the
 * composed #82/#95 logic, unit-tested in
 * SessionOutcomeClassifierTest). The gap test runs on the
 * monotonic-implied now — the session start advanced by the
 * VERIFIED (monotonic) elapsed — never on the raw wall clock, so
 * clock travel can neither forge ELAPSED_SHIELD_GAP nor launder
 * one into ELAPSED_CLEAN; the reference also never sits before
 * the current build landed (update downtime is not a gap), and a
 * beat of 0 is a pre-v10 row — no beat evidence exists, so the
 * beat test alone cannot convict it; only the latch can. The
 * usage term (#81 F4): a finish may certify ELAPSED_CLEAN only
 * if the shield can still SEE at the ending write, so a missing
 * usage-stats permission books the gap. The exposure term
 * (#129 F3): an unguarded distraction door used 30s+ inside the
 * session's own window books ELAPSED_OPEN_EXPOSURE — below the
 * gap, above clean — from the caller's usage-evidence read. A
 * session that has not completed keeps its standing outcome; a
 * routine row keeps its standing outcome too — its judgement is
 * routineOutcome, booked by the finalise funnel.
 */
internal fun classifyElapsedOutcome(
    session: FocusSessionEntity,
    allBlocksCompleted: Boolean,
    isRoutineSession: Boolean,
    verifiedElapsedSeconds: Long,
    appLastUpdateMillis: Long,
    usageStatsPermissionHeld: Boolean,
    openExposureSeen: Boolean
): String {
    if (!allBlocksCompleted) return session.outcome
    if (isRoutineSession) return session.outcome
    val impliedNowWall = session.startWallTimeMillis + verifiedElapsedSeconds * 1000L
    val beatRef = maxOf(
        session.shieldLastBeatWallMillis,
        session.startWallTimeMillis,
        appLastUpdateMillis
    )
    return if (session.shieldGapSeen ||
        (session.shieldLastBeatWallMillis > 0L &&
            impliedNowWall - beatRef > MonolithRepository.SHIELD_GAP_THRESHOLD_MILLIS) ||
        !usageStatsPermissionHeld
    ) {
        SessionOutcome.ELAPSED_SHIELD_GAP
    } else if (openExposureSeen) {
        SessionOutcome.ELAPSED_OPEN_EXPOSURE
    } else {
        SessionOutcome.ELAPSED_CLEAN
    }
}

/**
 * The repository. THREADING CONTRACT (issue #33, audit R1): every
 * `suspend` function in this class does its Room / SharedPreferences /
 * Settings / signing work on [Dispatchers.IO] or inside a Room
 * transaction (which runs on Room's own executor) — a caller may use
 * any dispatcher. Every NON-suspend function that touches the system
 * (usage stats, AppOps, Settings, SharedPreferences) is annotated
 * [WorkerThread]: it blocks on IPC and must never run on the main
 * thread. Pure in-memory reads are the only un-annotated sync calls.
 *
 * The Room database is reached ONLY through [databaseProvider]
 * (issue #33, audit R1): no function in this class calls
 * `MonolithDatabase.getInstance` directly, so a test can inject an
 * in-memory database and every transactional path follows the DAO
 * it was given.
 */
class MonolithRepository(
    private val context: Context,
    private val dao: MonolithDao,
    // ISSUE-67: everything this class needs from the living
    // services arrives through this port (data layer, this
    // package) — never through concrete service imports. The
    // default is the unbound port (service-less test processes);
    // production is wired live by AppContainer, the single
    // construction point.
    private val servicePort: RepositoryServicePort = UnboundRepositoryServicePort,
    // ISSUE-67: the database is INJECTED (AppContainer passes the
    // same instance the DAO came from) — the transaction sites
    // below used to re-fetch it via getInstance behind the DAO's
    // back. Null keeps direct test constructions working.
    private val database: MonolithDatabase? = null,
    // Main's provider seam, composed with the injection above:
    // the provider resolves the injected database when present,
    // else the singleton — one resolution path either way.
    private val databaseProvider: () -> MonolithDatabase = { database ?: MonolithDatabase.getInstance(context) }
) {
    private fun db(): MonolithDatabase = databaseProvider()

    val allAppsFlow: Flow<List<BlockedAppEntity>> = dao.getAllApps()
    val allProfilesFlow: Flow<List<FocusProfileEntity>> = dao.getAllProfiles()
    val activeSessionFlow: Flow<FocusSessionEntity?> = dao.getActiveSessionFlow()
    val sessionHistoryFlow: Flow<List<FocusSessionEntity>> = dao.getSessionHistoryFlow()
    val interceptLogsFlow: Flow<List<InterceptLogEntity>> = dao.getInterceptLogsFlow()
    val capturedNotificationsFlow: Flow<List<CapturedNotificationEntity>> = dao.getCapturedNotificationsFlow()
    val allTasksFlow: Flow<List<FocusTaskEntity>> = dao.getAllTasksFlow()
    val allSitesFlow: Flow<List<BlockedSiteEntity>> = dao.getAllSitesFlow()
    val allRoutinesFlow: Flow<List<RoutineEntity>> = dao.getAllRoutinesFlow()

    companion object {
        private const val TAG = "MonolithRepository"
        private val seedMutex = Mutex()
        /** Issue #124 (typing-intention): the intention is a short,
         *  sharp declaration the user authors — never a paragraph.
         *  Every arm path funnels through [startSession], which
         *  trims and caps the snapshot to this length; the draft
         *  writer (MonolithViewModel.setIntentionText) caps at the
         *  same bound so the box can never hold more than a session
         *  can store. */
        const val MAX_INTENTION_CHARS = 80
        const val PEEK_DURATION_SECONDS = 120 // 2-Minute Peek
        const val PEEK_DURATION_MILLIS = 120 * 1000L // 2-Minute Peek lease in ms
        const val PEEK_COOLDOWN_MILLIS = 20 * 60 * 1000L // 20-Minute Mandatory Cooldown
        const val STOPWATCH_MIN_FLOOR_SECONDS = 20 * 60 // 20-Minute Minimum Floor
        // Issue #290 F1: FLOW's terminal cap — a count-up session
        // auto-ends at 4 hours instead of running forever with no
        // terminal rule of its own (Forest's stopwatch auto-stops
        // at its cap). The ending books STOPWATCH_ENDED, the same
        // lawful FLOW finish as the past-floor door.
        const val STOPWATCH_MAX_CAP_SECONDS = 4 * 60 * 60 // 4-Hour FLOW Cap
        const val SAFE_BREAK_DURATION_SECONDS = 10 * 60 // 10-Minute Safe Break
        const val HMAC_TAMPER_PENALTY_SECONDS = 15 * 60 // 15-Minute Penalty Lock

        /** The outcome-reason constant of the emergency-break path
         *  (TOKEN-SPEC §4.5) — exactly this string, never free text.
         *  The interim "Grace Cancel" string-sniff in
         *  [disarmActiveSession] correctly returns false for it. */
        const val EMERGENCY_BREAK_REASON = "Emergency Break"

        /** What a completed valve hold pays, in the valve's own terms
         *  (TOKEN-SPEC §2 — stored on the spend row). */
        const val EMERGENCY_BREAK_HELD_SECONDS = 60

        /** W3 #134 (phone-call-mid-session): the telephony surface.
         *  The call bypass has always covered calls that already
         *  exist (AudioManager call modes); placing a call starts
         *  in MODE_NORMAL inside the dialer, so a LOCKED Phone app
         *  was intercepted like any distraction and the user could
         *  not dial out mid-session. The dialer package is exempt
         *  at every door, in every loadout: telephony is a safety
         *  surface, not a distraction category. Identification is
         *  the system's own default dialer where TelecomManager
         *  names one, plus the stock dialer packages as the
         *  fallback — package-scoped, never a name substring, so
         *  no other app can wear the exemption. */
        private val KNOWN_DIALER_PACKAGES = setOf(
            "com.google.android.dialer",
            "com.android.dialer",
            "com.samsung.android.dialer"
        )

        fun isDialerPackage(context: Context, packageName: String): Boolean {
            if (packageName in KNOWN_DIALER_PACKAGES) return true
            return try {
                val tm = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
                tm?.defaultDialerPackage == packageName
            } catch (_: Exception) {
                false
            }
        }

        /** The shipped routine-session recogniser (TOKEN-SPEC §4.1):
         *  a session started by a routine carries this profile prefix.
         *  Kept for recognising legacy pre-rebuild rows (arbitration
         *  A3); new rows are matched by routineId. */
        private const val ROUTINE_PROFILE_PREFIX = "Routine: "

        // Attendance evidence mask bits (Sten §1.6.1). E1 is set at
        // materialise; E2 on foreground return; E3 by finalise when a
        // blocked attempt landed inside the window; E4 when the 60s
        // valve hold starts.
        const val EVIDENCE_E1_START = 1
        const val EVIDENCE_E2_FOREGROUND = 2
        const val EVIDENCE_E3_INTERCEPT = 4
        const val EVIDENCE_E4_HOLD_START = 8

        /** One evidence instant covers this long in the finalise sum. */
        const val EVIDENCE_COVERAGE_MILLIS = 300_000L

        /** Attendance counts from fifteen verified minutes (J1). */
        const val ATTENDANCE_FLOOR_SECONDS = 900

        /** F-SCH-1: the Drawer capture's prefs file (DayWordStore's
         *  PREFS_NAME, drawer lane) — the drain's source of record
         *  until the words land in `day_notes`. */
        const val DAY_WORDS_PREFS_NAME = "monolith_day_words"

        /** Chrome's prefs file + PARK's first-run bit (chrome C4). */
        const val CHROME_PREFS_NAME = "monolith_chrome"
        const val PARK_INTRO_DONE_KEY = "park_intro_done"
        /** Issue #70 F3: the Deck primer's first-run bit. */
        const val DECK_PRIMER_DONE_KEY = "deck_primer_done"

        /** Audit R1 #41: the shared WallAccrual anchor's disk
         *  home (see seedAccrualFromDisk / persistAccrualAnchor). */
        const val ACCRUAL_PREFS_NAME = "monolith_wall_accrual"
        const val ACCRUAL_KEY_SESSION = "session_id"
        const val ACCRUAL_KEY_TASK = "task_id"
        const val ACCRUAL_KEY_MONO = "last_mono_millis"
        const val ACCRUAL_KEY_BOOT = "boot_count"

        /** The closed declared-word list (DayWordStore.WORDS).
         *  Never TEST: the drain refuses any other word, so no
         *  test-day row can ever reach `day_notes`. */
        val DAY_WORDS: Set<String> = setOf("REST", "ILL", "FAMILY")

        /** Slice E (Nocke §2.2): the shield-gap law — a beat older
         *  than this (5 missed 2s beats) classifies the session
         *  death-booked and sets the latch. */
        const val SHIELD_GAP_THRESHOLD_MILLIS = 10_000L

        /** The Drawer's page size (DRW-04): reads fetch one row
         *  past it as the has-earlier probe. */
        const val DRAWER_PAGE_SIZE = 50

        /** Slice E (F-K1): the seal marker's prefs home — written
         *  synchronously by SessionSealTransition inside the seal
         *  act, BEFORE the Throw sounds, so a process that dies
         *  mid-seal leaves proof the seal landed. Read by the
         *  recovery path (adopt silently, never re-Throw) and by
         *  the ESR-02 repository refusal below. */
        const val SEAL_PREFS_NAME = "monolith_seal"
        const val SEAL_MARKER_SESSION_KEY = "sealed_session_id"
        const val SEAL_MARKER_WALL_KEY = "sealed_wall_ms"

        /** The clock anchor's prefs home (issue #82): the persisted
         *  (wall, elapsedRealtime, bootCount) triple [ClockHonesty]
         *  reasons from. Prefs, not Room — the anchor is machinery
         *  state, not record, and needs no column. */
        const val CLOCK_PREFS_NAME = "monolith_clock_anchor"
        const val CLOCK_ANCHOR_WALL_KEY = "anchor_wall_ms"
        const val CLOCK_ANCHOR_MONO_KEY = "anchor_elapsed_realtime_ms"
        const val CLOCK_ANCHOR_BOOT_KEY = "anchor_boot_count"

        /** Issue #48: EVERY sealed session's id, as a string set.
         *  The single int above is one slot — sealing a second
         *  session used to erase the first session's only on-disk
         *  proof. The int stays as the latest-seal witness (ESR-02,
         *  older readers); this set is the complete record. */
        const val SEAL_MARKER_SESSIONS_KEY = "sealed_session_ids"

        /** The seeded-ever flags (Audit R2 #96): emptiness is not
         *  freshness. Each flag is set the first time its store
         *  carries content — seeded or the user's own — and from
         *  then on an empty store is the user's own emptying,
         *  which the seed must never resurrect. */
        const val SEED_PREFS_NAME = "monolith_seed"
        const val SEED_SITES_KEY = "seeded_sites_v1"
        const val SEED_TASKS_KEY = "seeded_tasks_v1"

        /** W3 #130 F2 (verifier correction): the one-shot flag for
         *  the youtube.com host seed-upgrade — set the first time
         *  the upgrade has been CONSIDERED on an install that ran
         *  the v1 seed, whether or not it added the host, so the
         *  consideration never repeats (see youtubeHostSeedUpgrade). */
        const val SEED_SITES_V2_KEY = "seeded_sites_v2_youtube_host"

        /** The prune note's prefs home (Ordner lane, Mikro M5): a
         *  record of what the catalog reconcile removed, kept until
         *  Shield has shown it once. Prefs, not Room — announcing a
         *  table change needs no column in the table. */
        const val APP_LIST_PREFS_NAME = "monolith_app_list"
        const val PROFILE_UNDO_KEY = "profile_undo_snapshot_v1"
        const val PROFILE_UNDO_DIAL_KEY = "profile_undo_dial_minutes_v1"
        const val PROFILE_UNDO_NAME_KEY = "profile_undo_name_v1"
        const val PRUNE_NOTE_COUNT_KEY = "prune_note_count"
        const val PRUNE_NOTE_RULES_KEY = "prune_note_rules_removed"

        /** Issue #129 F1: the unreviewed-auto-OPEN record — the
         *  packages the category guess birthed OPEN with no preset
         *  and no ruling from the man. Prefs, not Room (same
         *  reasoning as the prune note: a catalogue bookkeeping
         *  record needs no column in the table). Entries leave the
         *  set ONLY when a ruling lands on that package
         *  ([setAppBlockTier] / [cycleAppBlockTier]) or the row is
         *  pruned — never by dismissal, or the prompt would be
         *  silence with extra steps. */
        const val UNREVIEWED_OPEN_KEY = "unreviewed_open_packages"

        /** Issue #129 F3: the reserved intercept-log package that
         *  carries a session's open-exposure names (the exposed
         *  app's real package rides in triggerReason, its label in
         *  appName). Receipt assembly reads these rows back and
         *  excludes them from deflections. */
        const val EXPOSURE_LOG_PACKAGE = "com.monolith.exposure"
        const val EXPOSURE_LOG_OUTCOME = "Open exposure"


        /** Issue #81 F1: the resurrection witness's prefs home.
         *  Written once, at seed time, when a virgin database meets
         *  a pre-existing AndroidKeyStore HMAC key — the residue
         *  `pm clear` cannot wipe (see TamperWitness). The prefs
         *  file itself dies with the NEXT clear, which is fine:
         *  that clear resurrects the same way and re-stamps it. */
        const val PROVENANCE_PREFS_NAME = "monolith_provenance"
        const val PROVENANCE_UNPROVEN_KEY = "resurrected_unproven"
        const val PROVENANCE_DETECTED_WALL_KEY = "resurrected_detected_wall_ms"

        /** The catalog memory (Wave 3, issue #34): the two-strike
         *  strike set and the pruned-rule tombstones, in the same
         *  prefs file — the schema stays frozen at v12, and a
         *  ruling's survival needs no column in the table it left.
         *  ABSENT_ONCE: packages absent at the last sync (a prune
         *  needs a second consecutive absence). REMEMBERED_RULES:
         *  "pkg|TIER|limit" strings for pruned rows, restored
         *  ahead of factory presets if the package returns. */
        const val ABSENT_ONCE_KEY = "catalog_absent_once"
        const val REMEMBERED_RULES_KEY = "catalog_remembered_rules"

        /** When the last catalog sync COMPLETED (wall millis,
         *  fresh-eyes L3): the anchor of the collapse window —
         *  a non-forced sync inside
         *  [AppCatalogReconcile.SYNC_COLLAPSE_WINDOW_MILLIS] of
         *  this instant is the same look repeating itself and
         *  returns without touching the strike set. Stamped only
         *  after a reconcile actually ran (a failed/empty query
         *  is not a successful sync and starts no window). */
        const val LAST_CATALOG_SYNC_KEY = "catalog_last_sync_ms"
    }

    /** The honest now (issue #82): the wall clock cross-checked
     *  against the persisted monotonic anchor — see [ClockHonesty].
     *  Every verdict that used to read System.currentTimeMillis()
     *  raw (week anchor for the emergency token, routine edit
     *  stamps, the heartbeat's derive/finalise "now") reads this
     *  instead, so travelling the device clock can neither mint a
     *  fresh weekly token, nor future-stamp an edit, nor materialise
     *  a routine window that has not honestly arrived. */
    fun honestNowMillis(): Long {
        val prefs = context.getSharedPreferences(CLOCK_PREFS_NAME, Context.MODE_PRIVATE)
        val nowWall = System.currentTimeMillis()
        val nowMono = SystemClock.elapsedRealtime()
        val boot = querySystemBootCount()
        val decision = ClockHonesty.decide(
            anchorWallMillis = prefs.getLong(CLOCK_ANCHOR_WALL_KEY, 0L),
            anchorElapsedRealtimeMillis = prefs.getLong(CLOCK_ANCHOR_MONO_KEY, 0L),
            anchorBootCount = prefs.getInt(CLOCK_ANCHOR_BOOT_KEY, -1),
            nowWallMillis = nowWall,
            nowElapsedRealtimeMillis = nowMono,
            nowBootCount = boot
        )
        if (decision.reanchor) {
            prefs.edit()
                .putLong(CLOCK_ANCHOR_WALL_KEY, nowWall)
                .putLong(CLOCK_ANCHOR_MONO_KEY, nowMono)
                .putInt(CLOCK_ANCHOR_BOOT_KEY, boot)
                .apply()
        }
        return decision.honestNowMillis

    }

    /** The seal marker's session id, or -1 when no seal has ever
     *  landed in this install (Slice E, F-K1 / ESR-02). */
    fun sealedSessionIdFromMarker(): Int =
        // Issue #83 (STURM3-04): the marker lives in device-protected
        // storage (SessionSealTransition writes it there); reading the
        // credential-protected file here would read a stale split copy.
        DeviceProtectedPrefs.get(context, SEAL_PREFS_NAME)
            .getInt(SEAL_MARKER_SESSION_KEY, -1)

    /** Issue #81 F1: true when this install has reported itself
     *  resurrected-after-wipe (see [ensureSeededAndSyncPackages]).
     *  Sticky for the life of the (new) data: nothing that happens
     *  afterwards can retroactively prove the wiped past clean. */
    fun provenanceUnproven(): Boolean =
        context.getSharedPreferences(PROVENANCE_PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(PROVENANCE_UNPROVEN_KEY, false)

    /** The resurrection check, run inside the seed lock BEFORE any
     *  seeding write, so "virgin" is measured on the database as
     *  found. Must also run before anything can sign session state
     *  (signing generates the keystore key on first use — after
     *  that, the residue exists by our own hand and proves
     *  nothing). Seeding is the first repository act of every
     *  launch path that can later sign, so this is the one place
     *  the measurement is honest. */
    private suspend fun recordProvenanceIfResurrected() {
        val dbVirgin = dao.getAllSitesSnapshot().isEmpty() &&
            dao.getGuardSettingsSnapshot() == null &&
            dao.getAllRoutinesSnapshot().isEmpty() &&
            dao.getSessionHistorySnapshot().isEmpty()
        val provenance = TamperWitness.classifyProvenance(
            dbVirgin = dbVirgin,
            keystoreKeyPreExisted = CryptoIntegrityManager.hmacKeyExists()
        )
        if (provenance == TamperWitness.InstallProvenance.RESURRECTED_UNPROVEN) {
            context.getSharedPreferences(PROVENANCE_PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PROVENANCE_UNPROVEN_KEY, true)
                .putLong(PROVENANCE_DETECTED_WALL_KEY, System.currentTimeMillis())
                .apply()
        }
    }

    /** Every session the seal marker testifies for (issue #48):
     *  the persisted id set, unioned with the legacy single-slot
     *  int, so a seal landed by an older build — which wrote only
     *  the int — still testifies. */
    @WorkerThread
    fun sealedSessionIdsFromMarker(): Set<Int> {
        // Issue #83 (STURM3-04): device-protected seal file, as above.
        val prefs = DeviceProtectedPrefs.get(context, SEAL_PREFS_NAME)
        val fromSet = prefs.getStringSet(SEAL_MARKER_SESSIONS_KEY, emptySet())
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .toSet()
        val legacy = prefs.getInt(SEAL_MARKER_SESSION_KEY, -1)
        return if (legacy >= 0) fromSet + legacy else fromSet
    }

    suspend fun ensureSeededAndSyncPackages(
        forceCatalogSync: Boolean = false
    ) = withContext(Dispatchers.IO) {
        seedMutex.withLock {
        recordProvenanceIfResurrected()

        // Audit R2 #96: seed only a store that has never carried
        // content. Once content exists (seeded or the user's own)
        // the seeded-ever flag is set, and a later empty store is
        // the user's own emptying — honoured, never resurrected.
        val seedPrefs = context.getSharedPreferences(SEED_PREFS_NAME, Context.MODE_PRIVATE)
        val sitesNow = dao.getAllSitesSnapshot()
        if (!seedPrefs.getBoolean(SEED_SITES_KEY, false)) {
            if (sitesNow.isEmpty()) {
                dao.insertSites(BlockedBrowserDomains.defaultDomains.map { BlockedSiteEntity(it) })
            }
            seedPrefs.edit().putBoolean(SEED_SITES_KEY, true).apply()

        }
        // W3 #130 F2 seed-upgrade (verifier correction, DURCH-D):
        // an install that ran the v1 seed before "youtube.com"
        // joined the defaults never re-seeds, so the host rule
        // must be DELIVERED — once, and only where the legacy
        // factory entry proves the seed stands unedited (the pure
        // decision above; a user's deletion is never resurrected).
        // A fresh install reaches this line with the current
        // defaults just seeded, so the decision adds nothing and
        // the flag simply closes. Runs under the same seed lock.
        if (!seedPrefs.getBoolean(SEED_SITES_V2_KEY, false)) {
            if (seedPrefs.getBoolean(SEED_SITES_KEY, false)) {
                val upgradeAdds = youtubeHostSeedUpgrade(
                    dao.getAllSitesSnapshot().map { it.domain }
                )
                if (upgradeAdds.isNotEmpty()) {
                    dao.insertSites(upgradeAdds.map { BlockedSiteEntity(it) })
                }
            }
            seedPrefs.edit().putBoolean(SEED_SITES_V2_KEY, true).apply()
        }
        if (dao.getGuardSettingsSnapshot() == null) {
            dao.insertGuardSettings(GuardSettingsEntity())
        }
        // Routines: a fresh install seeds his real day directly and is
        // marked reseed-done (it never "migrates" and never sees the
        // reset note); an existing install runs the version-blind
        // reseed transform once (Sten §2).
        if (dao.getAllRoutinesSnapshot().isEmpty()) {
            // Audit R2 #96: the fresh seed stamps its edit horizon,
            // exactly as the reseed path does. With the default 0,
            // every window that ended before the install would be
            // judged MISSED — the derive law's own rule ("before
            // that horizon judgement is UNKNOWN, never an invented
            // MISSED") defeated by the seed itself.
            val seededAtMillis = System.currentTimeMillis()
            dao.insertRoutines(
                routineTargets().map { it.copy(scheduleEditedAtMillis = seededAtMillis) }
            )
            // R3b #161 (update-day): an empty routines table means
            // this install has never seeded — a first install, a
            // wiped phone and a new phone are indistinguishable from
            // inside, so the seed owes the user one honest sentence
            // at first open instead of a silent factory state.
            InstallNoteStore(context).markFreshStart()
            val fresh = dao.getGuardSettingsSnapshot()
            if (fresh != null && !fresh.routineReseedV1Done) {
                dao.insertGuardSettings(
                    fresh.copy(routineReseedV1Done = true, routineReseedNoteDismissed = true)
                )
            }
        } else {
            reseedRoutinesV1()
        }
        try {
            // THE COLLAPSE GUARD (fresh-eyes L3): the two-strike
            // prune is only as honest as the distance between the
            // strikes. The ViewModel's init calls this sync and
            // then its guard refresh calls it AGAIN seconds later
            // (PARK's open does the same) — identical
            // PackageManager state, so one transiently-filtered
            // cold-start query used to satisfy both strikes in a
            // single launch and prune. A non-forced call inside
            // the window after a completed sync therefore skips
            // the reconcile entirely: it touches no strikes, files
            // no tombstones, prunes nothing. The rest of this
            // seeding pass (sites, profiles, tasks, the day-word
            // drain) is idempotent and still runs. A forced call,
            // and any call after the window, is a genuinely
            // separate look and reconciles normally.
            val collapseRepeat = !forceCatalogSync &&
                AppCatalogReconcile.isCollapseRepeat(
                    lastSuccessfulSyncMillis = loadLastCatalogSyncMillis(),
                    nowMillis = System.currentTimeMillis()
                )
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolvedList = pm.queryIntentActivities(mainIntent, 0)
            // The main activity per package (its launch intent's
            // component), resolved once per package — the reconcile's
            // dedupe rule lets it win over shortcut activities.
            val mainActivityByPackage = mutableMapOf<String, String?>()
            val installedFromPm = resolvedList.mapNotNull { resolveInfo ->
                val pkg = resolveInfo.activityInfo?.packageName ?: return@mapNotNull null
                if (pkg == context.packageName || pkg.startsWith("com.android.settings")) {
                    return@mapNotNull null
                }
                val label = resolveInfo.loadLabel(pm)?.toString()
                    ?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val mainActivity = mainActivityByPackage.getOrPut(pkg) {
                    runCatching {
                        pm.getLaunchIntentForPackage(pkg)?.component?.className
                    }.getOrNull()
                }
                InstalledPackage(
                    pkg, label,
                    isMainActivity = mainActivity != null &&
                        mainActivity == resolveInfo.activityInfo?.name
                )
            }
            // Issue #80 finding 1 (partial): the PackageManager
            // query above only sees the CALLING profile. LauncherApps
            // additionally enumerates the other profiles this user
            // owns (work profile, dual-app/clone user), so a package
            // installed only there is at least DISCOVERED and not
            // pruned from the catalog. Per-profile rules remain
            // impossible from one install — see the #80 report.
            val profiled = installedFromPm.map {
                ProfiledInstalledPackage(
                    it.packageName, it.label, Process.myUserHandle().toString()
                )
            }.toMutableList()
            try {
                val launcherApps =
                    context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
                if (launcherApps != null) {
                    for (profile in launcherApps.profiles) {
                        val activities = try {
                            launcherApps.getActivityList(null, profile)
                        } catch (_: Exception) {
                            null
                        } ?: continue
                        for (info in activities) {
                            val pkg = info.componentName?.packageName ?: continue
                            if (pkg == context.packageName ||
                                pkg.startsWith("com.android.settings")
                            ) {
                                continue
                            }
                            val label = info.label?.toString()
                                ?.takeIf { it.isNotBlank() } ?: continue
                            profiled.add(
                                ProfiledInstalledPackage(pkg, label, profile.toString())
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                // Discovery stays exactly as complete as the old
                // single-profile query when LauncherApps refuses.
            }
            // STURM3-13 port note: the calling-profile entries keep
            // their isMainActivity flags (issue #34's dedupe rule
            // depends on them), so the merged catalog is the PM list
            // plus ONLY the packages other profiles add — the pure
            // mergeProfiledInstalls view decides which those are.
            val pmPackages = installedFromPm.map { it.packageName }.toSet()
            val installed = installedFromPm +
                AppCatalogReconcile.mergeProfiledInstalls(profiled)
                    .filter { it.packageName !in pmPackages }

            // THE CATALOG IS THE PHONE'S TRUTH (Ordner lane, Sarthak's
            // complaint Oct 29 22:40: the list carried apps he never
            // installed). There is no blind factory seed any more:
            // this sync reconciles the table to the installed
            // launchable set — rows for packages absent on TWO
            // consecutive syncs are PRUNED (with their rulings filed
            // in the catalog memory first, issue #34), present
            // packages are discovered without a cap, and the
            // factory presets survive only as birth rules applied to
            // packages that actually exist on this phone. An empty
            // query result can only mean the query failed (the
            // <queries> declaration in the manifest makes every
            // launcher app visible), so an empty result skips the
            // reconcile entirely rather than pruning his rules away.
            if (installed.isNotEmpty() && !collapseRepeat) {
                val existingRows = dao.getAllAppsSnapshot()
                val plan = AppCatalogReconcile.reconcile(
                    existing = existingRows,
                    installed = installed,
                    usageMinutes = queryRealTodayUsageMinutes(),
                    categorize = ::categorizePackage,
                    previouslyAbsent = loadAbsentOnce(),
                    rememberedRules = loadRememberedRules()
                )
                if (plan.toInsert.isNotEmpty()) {
                    dao.insertApps(plan.toInsert)
                    // A restored package's tombstone is spent.
                    forgetRememberedRules(plan.toInsert.map { it.packageName })
                }
                if (plan.unreviewedOpenPackages.isNotEmpty()) {
                    recordUnreviewedOpen(plan.unreviewedOpenPackages)
                }
                plan.usageUpdates.forEach { dao.updateApp(it) }
                if (plan.toPrune.isNotEmpty()) {
                    // M5 (Mikro): the prune announces itself once, so
                    // record what it took BEFORE the rows leave —
                    // how many, and whether any stored ruling went
                    // with them. The Shield strip prints exactly
                    // this record, once, and nothing it can't prove.
                    val prunedRows = existingRows.filter {
                        it.packageName in plan.toPrune
                    }
                    // Issue #34: file the rulings BEFORE the rows
                    // leave, so a returning package is reborn with
                    // its own ruling, never a silent OPEN.
                    rememberPrunedRules(prunedRows)
                    recordPruneNote(
                        appsRemoved = prunedRows.size,
                        rulesRemoved = prunedRows.any {
                            it.resolvedTier != AppBlockTier.OPEN
                        }
                    )
                    // A pruned app can no longer be reviewed; its
                    // unreviewed entry leaves with its row (#129 F1).
                    clearUnreviewedOpen(plan.toPrune)
                    dao.deleteAppsByPackage(plan.toPrune)
                }
                // The strike set for the next sync: only packages
                // absent for the FIRST time here. A package absent
                // twice has been pruned above; one present again
                // clears itself by not being absent.
                saveAbsentOnce(plan.pendingPrune.toSet())
                // The sync completed: this instant anchors the
                // collapse window (L3). Only a reconcile that
                // actually ran earns the stamp — an empty query
                // above is a failed look and starts no window.
                saveLastCatalogSyncMillis(System.currentTimeMillis())
            }
        } catch (e: Exception) {
            // Safe fallback if package querying is restricted — logged, not silent (#33).
            swallow("ensureSeededAndSyncPackages catalog sync", e)
        }

        val existingProfiles = dao.getAllProfilesSnapshot()
        if (existingProfiles.isEmpty()) {
            val defaultProfiles = listOf(
                FocusProfileEntity(
                    name = "Study Loadout",
                    codeName = "Study Mode",
                    description = "Locks Social, Games & short video (YouTube included), sets Messaging to 2m Peek, arms Shorts Sniper.",
                    defaultMinutes = 50,
                    blockedCategoriesCsv = "SHORT_VIDEO,SOCIAL,GAMES",
                    scheduleLabel = "09:00 – 13:00 • Weekdays",
                    isScheduleEnabled = false,
                    startHour = 9,
                    startMinute = 0,
                    endHour = 13,
                    endMinute = 0
                ),
                FocusProfileEntity(
                    name = "Exam Lockdown",
                    codeName = "Exam Mode",
                    description = "Full 90-minute exam shield across all feeds, games, and video apps.",
                    defaultMinutes = 90,
                    blockedCategoriesCsv = "SHORT_VIDEO,SOCIAL,GAMES,MESSAGING,BROWSERS",
                    scheduleLabel = "14:00 – 18:00 • Exam Prep",
                    isScheduleEnabled = false,
                    startHour = 14,
                    startMinute = 0,
                    endHour = 18,
                    endMinute = 0
                ),
                FocusProfileEntity(
                    name = "Sleep Loadout",
                    codeName = "Sleep Guard",
                    description = "Locks every app except Phone, Clock, and Maps so you sleep on time.",
                    defaultMinutes = 60,
                    blockedCategoriesCsv = "SHORT_VIDEO,SOCIAL,GAMES,SHOPPING,BROWSERS,MESSAGING",
                    scheduleLabel = "22:30 – 07:00 • Nightly",
                    isScheduleEnabled = false,
                    startHour = 22,
                    startMinute = 30,
                    endHour = 7,
                    endMinute = 0
                ),
                FocusProfileEntity(
                    name = "Quick Homework",
                    codeName = "25m Sprint",
                    description = "Blocks short-form video while keeping messaging on 2m Peek for study groups.",
                    defaultMinutes = 25,
                    blockedCategoriesCsv = "SHORT_VIDEO,GAMES",
                    scheduleLabel = "19:00 – 21:00 • Daily",
                    isScheduleEnabled = false,
                    startHour = 19,
                    startMinute = 0,
                    endHour = 21,
                    endMinute = 0
                )
            )
            dao.insertProfiles(defaultProfiles)
        }

        // Issue #139 truth patches for rows seeded by older builds:
        // the Sleep seed forgot MESSAGING (WhatsApp stayed open under
        // a profile that promises to lock everything but Phone,
        // Clock and Maps), the Study seed promised YouTube a 2m
        // Peek the category system cannot give it, and every seed
        // claimed an enabled schedule no engine has ever read.
        // Patches bite only on rows still carrying the exact old
        // seed values — a hand-edited profile is never rewritten.
        val seededNames = setOf("Study Loadout", "Exam Lockdown", "Sleep Loadout", "Quick Homework")
        existingProfiles.forEach { profile ->
            var patched = profile
            if (profile.name == "Sleep Loadout" &&
                profile.blockedCategoriesCsv == "SHORT_VIDEO,SOCIAL,GAMES,SHOPPING,BROWSERS"
            ) {
                patched = patched.copy(
                    blockedCategoriesCsv = "SHORT_VIDEO,SOCIAL,GAMES,SHOPPING,BROWSERS,MESSAGING"
                )
            }
            if (profile.name == "Study Loadout" &&
                profile.description == "Locks Social & Games, sets YouTube & Messaging to 2m Peek, arms Shorts Sniper."
            ) {
                patched = patched.copy(
                    description = "Locks Social, Games & short video (YouTube included), sets Messaging to 2m Peek, arms Shorts Sniper."
                )
            }
            if (profile.name in seededNames && profile.isScheduleEnabled) {
                patched = patched.copy(isScheduleEnabled = false)
            }
            if (patched != profile) dao.updateProfile(patched)
        }

        // Fabricated demo history STRUCK in this lane too (Lie
        // Register 3 F-3; caught by Ren's pre-merge S1 sweep,
        // Oct 29): this copy of the seed block still inserted
        // three invented sessions, invented intercept logs and
        // invented captured notifications on first run — the
        // frozen base predated trunk's scrub. Fresh installs
        // now start with an empty, true history.

        val existingTasks = dao.getAllTasksSnapshot()
        // Audit R2 #96: the same seeded-ever law as the sites —
        // an emptied queue is the user's own, never refilled.
        if (existingTasks.isEmpty() && !seedPrefs.getBoolean(SEED_TASKS_KEY, false)) {
            // Audit R2 #96: the starter slate is honest — QUEUED,
            // zero seconds spent, no substep pre-done. The previous
            // seed fabricated work history (an ACTIVE first task
            // with 420s spent and a substep already done) that a
            // fresh install never did.
            val defaultTasks = listOf(
                FocusTaskEntity(
                    title = "Finish Chapter 4 Problem Set (#1–6)",
                    notesOrSubsteps = FocusTaskEntity.serializeSubSteps(
                        listOf(
                            TaskSubStep(0, "Draw free-body diagrams for problems #1–3", false),
                            TaskSubStep(1, "Solve equilibrium equations for #1–3", false),
                            TaskSubStep(2, "Complete problems #4–6 & verify units", false)
                        )
                    ),
                    estimatedMinutes = 25,
                    actualSecondsSpent = 0,
                    categoryTag = "Study",
                    queueOrder = 0,
                    status = TaskQueueStatus.QUEUED.name
                ),
                FocusTaskEntity(
                    title = "Synthesize Lecture 9 Architecture Notes",
                    notesOrSubsteps = FocusTaskEntity.serializeSubSteps(
                        listOf(
                            TaskSubStep(0, "Extract L1/L2 cache hierarchy diagrams", false),
                            TaskSubStep(1, "Summarize virtual page table walkthrough", false),
                            TaskSubStep(2, "Write 3 self-test recall prompts", false)
                        )
                    ),
                    estimatedMinutes = 25,
                    actualSecondsSpent = 0,
                    categoryTag = "Writing",
                    queueOrder = 1,
                    status = TaskQueueStatus.QUEUED.name
                ),
                FocusTaskEntity(
                    title = "Debug Dynamic Programming Memo Table",
                    notesOrSubsteps = FocusTaskEntity.serializeSubSteps(
                        listOf(
                            TaskSubStep(0, "Trace base case indices on paper first", false),
                            TaskSubStep(1, "Fix off-by-one transition in memo[i][j]", false),
                            TaskSubStep(2, "Run edge-case unit test suite", false)
                        )
                    ),
                    estimatedMinutes = 45,
                    actualSecondsSpent = 0,
                    categoryTag = "Code",
                    queueOrder = 2,
                    status = TaskQueueStatus.QUEUED.name
                )
            )
            dao.insertTasks(defaultTasks)
            seedPrefs.edit().putBoolean(SEED_TASKS_KEY, true).apply()
        } else {
            // Content exists — seeded or the user's own. Record
            // seeded-ever (an install predating the flag counts as
            // seeded) so a later emptying is honoured as his own.
            if (!seedPrefs.getBoolean(SEED_TASKS_KEY, false)) {
                seedPrefs.edit().putBoolean(SEED_TASKS_KEY, true).apply()
            }
            // Deduplicate any duplicate tasks or sessions created by prior race conditions.
            // PERMANENCE GUARD: a COMPLETED task is a stored record (the
            // Drawer) — repeat-titled completions are the normal shape of
            // a year, and this pass must never delete one. Stored rows are
            // skipped entirely: not deleted, and not registered as a
            // dedup key against anything.
            val seenTaskTitles = mutableSetOf<String>()
            for (task in existingTasks.sortedBy { it.id }) {
                if (task.resolvedStatus == TaskQueueStatus.COMPLETED) continue
                val key = "${task.title.lowercase(java.util.Locale.ROOT)}|${task.status}"
                if (!seenTaskTitles.add(key)) {
                    dao.deleteTaskById(task.id)
                }
            }
            val allSessions = dao.getSessionHistorySnapshot()
            val seenSessionKeys = mutableSetOf<String>()
            for (sess in allSessions.sortedBy { it.id }) {
                if (!sess.isActive) {
                    val key = "${sess.intention}|${sess.targetDurationSeconds}|${sess.completedTasksSummary}"
                    if (!seenSessionKeys.add(key)) {
                        dao.deleteSessionById(sess.id)
                    }
                }
            }
        }
        drainDayWordsLocked()
        }
    }

    /**
     * F-SCH-1 drain: moves the Drawer's declared day words from
     * their prefs capture into the `day_notes` Room table — their
     * durable home from schema v9 (the table rides Migration 8→9).
     * The capture contract is the Drawer lane's DayWordStore:
     * SharedPreferences file [DAY_WORDS_PREFS_NAME], key = dateIso
     * (yyyy-MM-dd), value = the declared word. Both lanes build
     * this repository on the application context, so this reads
     * the very file the capture writes.
     *
     * Runs inside every seed-sync (the first seed-sync after the
     * 8→9 upgrade performs the move) and is idempotent: drained
     * keys are removed from prefs, so a later run finds nothing
     * to move. Only words in [DAY_WORDS] drain. Anything else in
     * the prefs file — an unknown word, a non-string value — is
     * left exactly where it lies: never silently dropped, and
     * never stored, so a TEST row in day_notes is impossible by
     * this refusal (the word list is closed; this app does not
     * know test days). A date already present in the table takes
     * the prefs value: until drained, prefs is the capture of
     * record.
     */
    suspend fun drainDayWordsFromPrefs() = withContext(Dispatchers.IO) {
        seedMutex.withLock { drainDayWordsLocked() }
    }

    // PARK's first-run fact (chrome C4, spec §5.7): whether the
    // store has EVER measured is a first-run-only condition — after
    // the first run, a cold start's unmeasured instant is a sweep
    // that hasn't landed yet, not an invitation. One persisted bit
    // keeps the two apart.
    fun parkIntroPending(): Boolean =
        context.getSharedPreferences(CHROME_PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(PARK_INTRO_DONE_KEY, false).not()

    // Issue #70 F3: the Deck primer's gate — the same one-bit
    // pattern as the PARK intro. Pending until the first session
    // has ever run; the Deck prints its three lines only while
    // this is pending (and no session is live and the warrant is
    // OFF), then never again.
    fun deckPrimerPending(): Boolean =
        context.getSharedPreferences(CHROME_PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(DECK_PRIMER_DONE_KEY, false).not()

    fun markDeckPrimerDone() {
        context.getSharedPreferences(CHROME_PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(DECK_PRIMER_DONE_KEY, true).apply()
    }

    // ------------------------------------------------------------------
    // THE PRUNE NOTE (Mikro M5): accumulate — read — clear.
    // ------------------------------------------------------------------

    private fun recordPruneNote(appsRemoved: Int, rulesRemoved: Boolean) {
        if (appsRemoved <= 0) return
        val prefs = context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(PRUNE_NOTE_COUNT_KEY, prefs.getInt(PRUNE_NOTE_COUNT_KEY, 0) + appsRemoved)
            .putBoolean(
                PRUNE_NOTE_RULES_KEY,
                rulesRemoved || prefs.getBoolean(PRUNE_NOTE_RULES_KEY, false)
            )
            .apply()
    }

    /** The pending prune record, or null when nothing was taken off
     *  the list since the last dismissal. */
    @WorkerThread
    fun pendingPruneNote(): PruneNote? {
        val prefs = context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt(PRUNE_NOTE_COUNT_KEY, 0)
        if (count <= 0) return null
        return PruneNote(
            appsRemoved = count,
            rulesRemoved = prefs.getBoolean(PRUNE_NOTE_RULES_KEY, false)
        )
    }

    fun clearPruneNote() {
        context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(PRUNE_NOTE_COUNT_KEY)
            .remove(PRUNE_NOTE_RULES_KEY)
            .apply()
    }

    // ------------------------------------------------------------------
    // THE UNREVIEWED-OPEN RECORD (issue #129 F1): record — read — clear
    // on ruling. An app the keyword guess birthed OPEN is not the man's
    // decision; this record keeps that fact alive until he rules on it.
    // ------------------------------------------------------------------

    private fun recordUnreviewedOpen(packages: List<String>) {
        if (packages.isEmpty()) return
        val prefs = context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getStringSet(UNREVIEWED_OPEN_KEY, emptySet()).orEmpty()
        prefs.edit().putStringSet(UNREVIEWED_OPEN_KEY, current + packages).apply()
    }

    /** The recorded unreviewed-auto-OPEN packages, as stored. */
    fun unreviewedOpenPackages(): Set<String> =
        context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .getStringSet(UNREVIEWED_OPEN_KEY, emptySet()).orEmpty().toSet()

    /** The unreviewed apps that still stand in the catalogue, in
     *  name order — the review prompt's content. Rows pruned since
     *  the record was written are not returned (and are dropped
     *  from the record by the prune path above). */
    suspend fun unreviewedOpenApps(): List<BlockedAppEntity> {
        val recorded = unreviewedOpenPackages()
        if (recorded.isEmpty()) return emptyList()
        return dao.getAllAppsSnapshot()
            .filter { it.packageName in recorded && it.resolvedTier == AppBlockTier.OPEN }
            .sortedWith(compareBy<BlockedAppEntity> { it.appName.lowercase() }.thenBy { it.packageName })
    }

    fun clearUnreviewedOpen(packages: Collection<String>) {
        if (packages.isEmpty()) return
        val prefs = context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getStringSet(UNREVIEWED_OPEN_KEY, emptySet()).orEmpty()
        val next = current - packages.toSet()
        if (next.size != current.size) {
            prefs.edit().putStringSet(UNREVIEWED_OPEN_KEY, next).apply()
        }
    }

    // ------------------------------------------------------------------
    // THE CATALOG MEMORY (issue #34): strike set + rule tombstones.
    // ------------------------------------------------------------------

    private fun loadLastCatalogSyncMillis(): Long =
        context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(LAST_CATALOG_SYNC_KEY, 0L)

    private fun saveLastCatalogSyncMillis(millis: Long) {
        context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putLong(LAST_CATALOG_SYNC_KEY, millis).apply()
    }

    private fun loadAbsentOnce(): Set<String> =
        context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .getStringSet(ABSENT_ONCE_KEY, emptySet())
            ?.toSet() ?: emptySet()

    private fun saveAbsentOnce(packages: Set<String>) {
        context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putStringSet(ABSENT_ONCE_KEY, packages).apply()
    }

    /** Remembered rulings, decoded: "pkg|TIER|limit" per entry.
     *  A malformed entry is skipped, never fatal. */
    private fun loadRememberedRules(): Map<String, AppRulePreset> {
        val raw = context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
            .getStringSet(REMEMBERED_RULES_KEY, emptySet()) ?: emptySet()
        return raw.mapNotNull { entry ->
            val parts = entry.split("|")
            if (parts.size != 3) return@mapNotNull null
            val tier = runCatching { AppBlockTier.valueOf(parts[1]) }.getOrNull()
                ?: return@mapNotNull null
            val limit = parts[2].toIntOrNull() ?: return@mapNotNull null
            parts[0] to AppRulePreset(tier, limit)
        }.toMap()
    }

    private fun rememberPrunedRules(rows: List<BlockedAppEntity>) {
        if (rows.isEmpty()) return
        val prefs = context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
        val kept = (prefs.getStringSet(REMEMBERED_RULES_KEY, emptySet()) ?: emptySet())
            .filter { entry -> rows.none { it.packageName == entry.substringBefore("|") } }
        val encoded = rows.map {
            "${it.packageName}|${it.resolvedTier.name}|${it.dailyLimitMinutes}"
        }
        prefs.edit()
            .putStringSet(REMEMBERED_RULES_KEY, (kept + encoded).toSet())
            .apply()
    }

    private fun forgetRememberedRules(packages: List<String>) {
        if (packages.isEmpty()) return
        val prefs = context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getStringSet(REMEMBERED_RULES_KEY, emptySet()) ?: return
        val kept = raw.filter { it.substringBefore("|") !in packages }
        if (kept.size != raw.size) {
            prefs.edit().putStringSet(REMEMBERED_RULES_KEY, kept.toSet()).apply()
        }
    }

    fun markParkIntroDone() {
        context.getSharedPreferences(CHROME_PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(PARK_INTRO_DONE_KEY, true).apply()
    }

    private suspend fun drainDayWordsLocked() {
        val prefs = context.getSharedPreferences(DAY_WORDS_PREFS_NAME, Context.MODE_PRIVATE)
        val captured = prefs.all
        if (captured.isEmpty()) return
        val drained = captured.mapNotNull { (dateIso, value) ->
            val word = value as? String ?: return@mapNotNull null
            if (word in DAY_WORDS) DayNoteEntity(dateIso = dateIso, word = word) else null
        }
        if (drained.isEmpty()) return
        dao.insertDayNotes(drained)
        val editor = prefs.edit()
        drained.forEach { editor.remove(it.dateIso) }
        editor.apply()
    }

    // Issue #132: classification lives in the pure AppCategorizer
    // (AppCatalogReconcile.kt) so it is unit-testable; this stays
    // as the reconcile's categorize seam. (Issue #107's top-level
    // categorizePackage in the same file delegates to it too —
    // one classifier, two seams, no drift.) Fresh-eyes L2's
    // whole-token matching lives in that same classifier — the
    // old substring matcher here called the Play Store a game.
    // Survivor safety, verified for L2: reconcile copies ONLY
    // this category (plus label and usage) onto an existing row;
    // a survivor's tier and limit are never re-priced by a sync,
    // so a corrected category can never move an existing ruling.
    // ISSUE-67 NOTE: this branch originally extracted the same
    // function into FocusDomain; main's #132 extraction landed
    // first and is a superset (MUSIC token category), so the
    // domain slice defers to it — one classifier, never two.
    private fun categorizePackage(pkg: String, label: String): AppCategory =
        AppCategorizer.categorize(pkg, label)

    /** Issue #33 (audit R1): a swallowed failure is LOGGED, never
     *  silent — the caller still gets its safe negative answer, but
     *  'the read failed' is now distinguishable from 'the answer is
     *  no' in logcat instead of being conflated with it. */
    private fun swallow(where: String, e: Exception) {
        Log.w(TAG, "$where failed; answering with the safe negative", e)
    }

    @WorkerThread
    fun queryRealTodayUsageMinutes(): Map<String, Int> {
        if (!hasUsageStatsPermission()) return emptyMap()
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return emptyMap()
            val end = System.currentTimeMillis()
            val start = end - (12 * 60 * 60 * 1000L)
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
            // Fix 4: buckets SUM per package (the pure
            // AppCatalogReconcile.aggregateUsageMinutes) — the
            // old associate{} kept only the last bucket and
            // under-reported any multi-bucket app.
            stats?.let { rows ->
                AppCatalogReconcile.aggregateUsageMinutes(
                    rows.map {
                        it.packageName to
                            ((it.totalTimeInForeground / 60_000L).toInt().coerceAtLeast(0))
                    }
                )
            } ?: emptyMap()
        } catch (e: Exception) {
            swallow("queryRealTodayUsageMinutes", e)
            emptyMap()
        }
    }

    /** W3 #134: the progress machine reads the call signal through
     *  this seam — the same test-seam shape as [breakBoundaryKnock]
     *  — so the call-accounting tests can hold a call open without
     *  a telephony stack. Production reads the AudioManager probe
     *  itself, always. */
    internal var callActiveProbe: () -> Boolean = { isPhoneOrVoipCallActive() }

    /** The dialer exemption's instance door (W3 #134): the
     *  companion holds the one identification; callers with a
     *  repository at hand (the shield's blocked-set filter, the
     *  loadouts) come through here. */
    fun isDialerPackage(packageName: String): Boolean =
        isDialerPackage(context, packageName)

    /**
     * Active Phone & VoIP Call Bypass:
     * Monitors AudioManager call states (MODE_RINGTONE, MODE_IN_CALL, MODE_IN_COMMUNICATION)
     * so incoming cellular, WhatsApp, or Telegram voice and video calls are never blocked mid-ring.
     * W3 #134: this same signal now also drives the session clock —
     * while it is active, [updateSessionProgress] excludes the
     * tick's seconds from the booked elapsed (call time is not
     * focus time), holds any finish or Safe Break flip until the
     * call ends, and pauses the Safe Break countdown. Outgoing
     * calls are covered separately: the dialer package itself is
     * exempt at every door (see [isDialerPackage]).
     */
    @WorkerThread
    fun isPhoneOrVoipCallActive(): Boolean {
        return try {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
            val mode = am.mode
            mode == AudioManager.MODE_RINGTONE ||
                mode == AudioManager.MODE_IN_CALL ||
                mode == AudioManager.MODE_IN_COMMUNICATION
        } catch (e: Exception) {
            swallow("isPhoneOrVoipCallActive", e)
            false
        }
    }

    /**
     * Issue #129 F3: per-package foreground milliseconds inside
     * one wall-clock window, from UsageStats EVENTS (resumed /
     * paused pairs), not the whole-day totals the sync reads.
     * This is the evidence the ending write judges "clean" by:
     * what was actually in front of the man while the session
     * ran. Empty when usage access is not granted — the caller
     * can only judge from evidence it can see, and the warrant
     * already prints that blindness elsewhere; it never invents
     * exposure it cannot prove.
     */
    fun queryForegroundMillisInWindow(startWallMillis: Long, endWallMillis: Long): Map<String, Long> {
        if (!hasUsageStatsPermission() || endWallMillis <= startWallMillis) return emptyMap()
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return emptyMap()
            val events = usm.queryEvents(startWallMillis, endWallMillis) ?: return emptyMap()
            val event = android.app.usage.UsageEvents.Event()
            val resumedAt = mutableMapOf<String, Long>()
            val totals = mutableMapOf<String, Long>()
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                val pkg = event.packageName ?: continue
                @Suppress("DEPRECATION")
                when (event.eventType) {
                    android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND ->
                        resumedAt[pkg] = event.timeStamp
                    android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                        val start = resumedAt.remove(pkg)
                        if (start != null) {
                            totals[pkg] = (totals[pkg] ?: 0L) + (event.timeStamp - start).coerceAtLeast(0L)
                        }
                    }
                }
            }
            // A package still in the foreground at the window's
            // end was used until the end — book the open span.
            for ((pkg, start) in resumedAt) {
                totals[pkg] = (totals[pkg] ?: 0L) + (endWallMillis - start).coerceAtLeast(0L)
            }
            totals
        } catch (_: Exception) {
            emptyMap()
        }
    }

    @WorkerThread
    fun queryForegroundBlockedApp(blockedPackages: Set<String>): String? =
        queryForegroundBlockedApp(blockedPackages, emptySet())

    /**
     * Issue #99 F1: packages whose peek lease has JUST expired. A
     * stay-put user inside the peeked app has a [lastTimeUsed] from
     * when the app was opened — minutes stale by expiry — so the
     * ordinary 8-second freshness filter is structurally blind to
     * exactly the user the expiry must evict. For a package in
     * [expiredPeekPackages] the freshness filter is waived and the
     * usage window widened: the package is treated as
     * foreground-blocked on this poll regardless of lastTimeUsed age.
     */
    @WorkerThread
    fun queryForegroundBlockedApp(
        blockedPackages: Set<String>,
        expiredPeekPackages: Set<String>
    ): String? {
        if (!hasUsageStatsPermission() || blockedPackages.isEmpty()) return null
        return try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return null
            val end = System.currentTimeMillis()

            // Issue #99 F1 (kept): the expired-peek waiver still
            // rides the aggregate stats — a stay-put user's event
            // stream holds no fresh foreground event to read.
            if (expiredPeekPackages.isNotEmpty()) {
                val stats = usm.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY, end - 15_000L, end
                )
                val expiredHit = stats
                    ?.filter {
                        it.packageName in expiredPeekPackages &&
                            it.packageName in blockedPackages &&
                            !SafetyFloor.isFloorPackage(it.packageName)
                    }
                    ?.maxByOrNull { it.lastTimeUsed }
                // Issue #121: the time-critical gate binds this
                // return too — an expired peek never re-covers a
                // screen whose alarm is ringing right now.
                if (expiredHit != null &&
                    !TimeCriticalAlertGate.isAlertLiveFor(context, expiredHit.packageName)
                ) {
                    return expiredHit.packageName
                }
            }

            // Issue #332 F1: the door now reads the real UsageEvents
            // stream on a 20-second window (the Flipd supplement's
            // benchmark) instead of daily-aggregate lastTimeUsed
            // with an 8-second cutoff, which went blind to a blocked
            // app the moment it settled into use.
            val usageEvents = usm.queryEvents(end - ForegroundDoor.WINDOW_MS, end)
                ?: return null
            // Issue #290 F4 disposition (merge-time truth): the
            // event-stream cure landed on main first via issue
            // #332 (ForegroundDoor, below) — this branch's own
            // ForegroundEventStream implementation is withdrawn
            // rather than shipping a second, competing fold.
            val events = mutableListOf<ForegroundDoor.FgEvent>()
            val event = android.app.usage.UsageEvents.Event()
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                val pkg = event.packageName ?: continue
                events.add(ForegroundDoor.FgEvent(pkg, event.eventType, event.timeStamp))
            }
            // Issue #332 F2: a live call no longer suspends this
            // door wholesale (the pre-cure early return made every
            // call a complete enforcement holiday, uninstall guard
            // included). The call's own surfaces are exempt —
            // the safety floor covers dialer / incallui / telecom —
            // and every other blocked app stays blocked mid-call,
            // which is exactly Flipd's per-package construction.
            val foreground = ForegroundDoor.foregroundBlocked(events, blockedPackages)
                ?: return null
            // Issue #121: this is the shield door's half of the
            // time-critical alert gate (the call bypass above can
            // never fire for an alarm — it plays on the ALARM
            // stream, not a call mode). A foreground package whose
            // alarm is ringing, or whose calendar reminder is live,
            // is NOT reported blocked, so the shield never covers
            // the ringing screen it cannot dismiss. The exemption
            // lasts exactly as long as the alert; the package is
            // reported again the moment it ends. The seal itself is
            // untouched: the package's tier never changes, and no
            // other package passes.
            if (TimeCriticalAlertGate.isAlertLiveFor(context, foreground)) null else foreground
        } catch (e: Exception) {
            swallow("queryForegroundBlockedApp", e)
            null
        }
    }

    /** Usage access as AppOps actually reports it: three-valued.
     *  The old Boolean collapsed UNSET (never asked, fresh device)
     *  into DENIED, which lied on first runs. */
    @WorkerThread
    fun usageAccess(): UsageAccess {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
                ?: return UsageAccess.UNSET
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            when (mode) {
                AppOpsManager.MODE_ALLOWED -> UsageAccess.ALLOWED
                AppOpsManager.MODE_DEFAULT -> UsageAccess.UNSET
                else -> UsageAccess.DENIED
            }
        } catch (e: Exception) {
            swallow("usageAccess", e)
            UsageAccess.UNSET
        }
    }

    @WorkerThread
    fun hasUsageStatsPermission(): Boolean = usageAccess() == UsageAccess.ALLOWED

    /**
     * THE STANDING WARRANT (chrome C3, spec §4.5): the wire verdict
     * re-based on the Watch Store. The inputs are the same living
     * facts the deleted liveWarrant() read — accessibility bound,
     * the residency stamp, usage access, shield-protecting — but the
     * state logic is the store's one derivation
     * ([WatchStore.deriveWarrant]); this function only gathers.
     * Synchronous, no coroutine, no deadline: the slide reads it at
     * DOWN and again at the gate crossing, and the verdict is never
     * older than the gesture.
     */
    @WorkerThread
    fun standingWarrant(): Warrant {
        // ISSUE-67: the living-service facts arrive via the port.
        val a11yBound = servicePort.accessibilityBound
        val stamped = a11yBound && WatchStore.stampedThisResidency()
        return WatchStore.deriveWarrant(
            a11yBound = a11yBound,
            stamped = stamped,
            usage = usageAccess(),
            shieldProtecting = servicePort.shieldProtecting
        )
    }

    /**
     * One grant-check query for the Watch Store's sweep (chrome C3):
     * the present truth of one permission item, read fresh. The two
     * BIND items answer from their services' existence; the four
     * GRANT CHECK items from their platform queries. Each sweep
     * query lands independently — this function is called once per
     * item, in parallel, by WatchStore.runSweep.
     */
    @WorkerThread
    fun watchGrantQuery(item: WatchItemId): Boolean = when (item) {
        // ISSUE-67: BIND items answer via the service port.
        WatchItemId.ACCESSIBILITY -> servicePort.accessibilityBound
        WatchItemId.USAGE_ACCESS -> usageAccess() == UsageAccess.ALLOWED
        WatchItemId.DEVICE_ADMIN -> isDeviceAdminActive()
        WatchItemId.NOTIFICATION_LISTENER -> servicePort.notificationListenerBound
        WatchItemId.OVERLAY -> hasOverlayPermission()
        WatchItemId.BATTERY -> {
            // Issue #122: the sweep's BATTERY query also reads the
            // battery ITSELF and publishes it, so PARK's line can
            // print level / saver / charging — the perm bit stays
            // the exemption (the FIX door's business), but it is no
            // longer the only battery fact the user can check.
            val snapshot = batterySnapshot()
            WatchStore.publishBattery(snapshot)
            snapshot.exemptionIgnored
        }
        else -> false
    }

    /** Issue #122: the one battery read (level, saver, charging,
     *  exemption) every surface shares — see BatteryState.kt. */
    fun batterySnapshot(): BatterySnapshot = readBatterySnapshot(context)

    /** Battery: whether the OS is told to leave Monolith alone.
     *  The read the dissolved Guard screen never had — its BATTERY &
     *  ADMIN row answered with the admin bit for both. */
    @WorkerThread
    fun isBatteryOptimizationIgnored(): Boolean {
        return try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                ?: return false
            pm.isIgnoringBatteryOptimizations(context.packageName)
        } catch (e: Exception) {
            swallow("permission probe", e)
            false
        }
    }

    /** The artifact ids actually forged by real sessions — the
     *  Drawer cabinet's forged state (chrome C10). */
    suspend fun forgedArtifactIds(): Set<String> =
        dao.getForgedArtifactIds().toSet()

    /** Audit R3b #145: per-artifact forge provenance for the
     *  cabinet — the forging sessions themselves, so a duplicate
     *  forge is a counted second entry with its own date, not a
     *  silent no-op against a DISTINCT set. */
    suspend fun curioForgeRecords(): Map<String, List<FocusSessionEntity>> =
        dao.getForgingSessions().groupBy { it.forgedArtifactId!! }

    /** Audit R3b #145: the forge draw, cured of the unweighted
     *  lottery. Un-forged artifacts draw first (a finish should
     *  EARN something new while anything new remains); the pick
     *  among them is seeded by the finishing session's own facts —
     *  sealed seconds, tasks completed, blocks run — so the draw
     *  is a function of the effort that earned it, not a shuffle.
     *  Once the whole catalogue stands forged, the same seeded
     *  pick runs over all six and the duplicate is recorded by
     *  the cabinet's provenance like any other forge. */
    internal suspend fun drawCurioId(session: FocusSessionEntity): String {
        val forged = dao.getForgedArtifactIds().toSet()
        val fresh = CurioCatalog.artifacts.filter { it.id !in forged }
        val pool = if (fresh.isNotEmpty()) fresh else CurioCatalog.artifacts
        val seed = session.elapsedSeconds / 60 + session.tasksCompletedCount * 7 +
            session.currentBlockIndex * 13 + session.totalStudyBlocks
        return pool[Math.floorMod(seed, pool.size)].id
    }

    /**
     * The cabinet's forge record per artifact (Issue #110,
     * finding 5): how many sessions forged each id, and the most
     * recent forge instant. Computed from the session rows the
     * app already stores — forgedArtifactId plus the row's end
     * stamp (falling back to its start for pre-v11 rows) — so no
     * new storage is needed; the old DISTINCT read collapsed all
     * of this away. The Partial Stamp's id is counted here too:
     * it is a real stamp on real sessions, and the cabinet now
     * carries a catalog entry for it.
     */
    suspend fun forgeStats(): Map<String, ForgeStat> {
        val stats = mutableMapOf<String, ForgeStat>()
        for (session in dao.getSessionHistorySnapshot()) {
            val artifactId = session.forgedArtifactId ?: continue
            val forgedAt = if (session.endWallMillis > 0L) {
                session.endWallMillis
            } else {
                session.startWallTimeMillis
            }
            val prev = stats[artifactId]
            stats[artifactId] = ForgeStat(
                count = (prev?.count ?: 0) + 1,
                lastForgedAtMillis = maxOf(prev?.lastForgedAtMillis ?: 0L, forgedAt)
            )
        }
        return stats
    }

    @WorkerThread
    fun hasOverlayPermission(): Boolean {
        return try {
            Settings.canDrawOverlays(context)
        } catch (e: Exception) {
            swallow("permission probe", e)
            false
        }
    }

    @WorkerThread
    fun isAccessibilityEnabled(): Boolean {
        return try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            enabledServices.contains(context.packageName, ignoreCase = true)
        } catch (e: Exception) {
            swallow("permission probe", e)
            false
        }
    }

    @WorkerThread
    fun isNotificationListenerEnabled(): Boolean {
        return try {
            val enabledListeners = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            ) ?: return false
            enabledListeners.contains(context.packageName, ignoreCase = true)
        } catch (e: Exception) {
            swallow("permission probe", e)
            false
        }
    }

    @WorkerThread
    fun isDeviceAdminActive(): Boolean {
        return try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
                ?: return false
            val adminComponent = ComponentName(context, MonolithDeviceAdminReceiver::class.java)
            dpm.isAdminActive(adminComponent)
        } catch (e: Exception) {
            swallow("permission probe", e)
            false
        }
    }


    /**
     * Issue #81 F5: Monolith's own critical components, by simple
     * name, whose PackageManager enabled-state is DISABLED —
     * the `adb shell pm disable` / component-surgery route, which
     * shows no Settings window for the accessibility text guard to
     * see and (before this check existed) was reported by no
     * surface at all. Read fresh on every call; callers are the
     * heartbeat reconcile (which latches the session's gap when a
     * component is down mid-session) and any future surface. A
     * component whose state cannot be read counts as enabled —
     * an unreadable state is no evidence of surgery.
     */
    fun disabledCriticalComponents(): List<String> {
        val pm = context.packageManager
        val critical = listOf(
            "MonolithShieldService" to ComponentName(context, MonolithShieldService::class.java),
            "MonolithAccessibilityService" to ComponentName(context, MonolithAccessibilityService::class.java),
            "MonolithNotificationListener" to ComponentName(context, MonolithNotificationListener::class.java),
            "MonolithBootReceiver" to ComponentName(context, MonolithBootReceiver::class.java)
        )
        return critical.mapNotNull { (name, component) ->
            val state = runCatching { pm.getComponentEnabledSetting(component) }
                .getOrDefault(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
            if (TamperWitness.componentDisabled(state)) name else null
        }
    }


    /** The system boot count, or null when the Settings read fails
     *  or the value does not exist (issue #33, audit R1). Every
     *  internal decision path uses THIS read: the old code fell back
     *  to a fabricated `1` on failure, and that invented value fed
     *  reboot/tamper detection — a failed read could manufacture a
     *  reboot verdict. Unknown now stays unknown. */
    @WorkerThread
    fun querySystemBootCountOrNull(): Int? {

        return try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT)
        } catch (e: Exception) {
            swallow("querySystemBootCount", e)
            null
        }
    }

    /** Legacy Int surface for display/test callers that need a
     *  number; decision logic must use [querySystemBootCountOrNull]. */
    @WorkerThread
    fun querySystemBootCount(): Int = querySystemBootCountOrNull() ?: 1

    /**
     * Tamper-Proof Monotonic Clock + Boot Count + Keystore HMAC Verification
     * (keys are requested from the AndroidKeyStore; hardware/TEE backing is
     * not verified — no StrongBox request, no KeyInfo security-level check —
     * and signing falls back to a built-in software key when the KeyStore is
     * unavailable; audit R1 #36 finding 4, substance parked as decision #21):
     * 1. Cross-checks wall-clock elapsed time against hardware `SystemClock.elapsedRealtime()` and `BOOT_COUNT`.
     *    Advancing the phone's system clock forward by +2 hours is ignored because elapsed time is anchored to
     *    `SystemClock.elapsedRealtime()`.
     * 2. Across device reboots (`LOCKED_BOOT_COMPLETED` where `currentBoot != session.bootCount` or
     *    `nowMono < session.lastTickElapsedRealtimeMillis`), preserves the elapsed banked before
     *    the reboot and credits NOTHING for the unobserved interval — neither the powered-off
     *    downtime nor the new boot's uptime before this first post-reboot tick. That time was
     *    never witnessed by the shield or the session clock, so booking it as focus was a
     *    fabrication (Audit R3 #113, finding 5). A reboot neither resets the timer nor
     *    inflates it: the count resumes from the banked figure and ticks from the re-anchor
     *    [updateSessionProgress] persists.
     * 3. Verifies the AndroidKeyStore `HmacSHA256` signature via `CryptoIntegrityManager.verifySessionState`.
     */
    @WorkerThread
    fun computeVerifiedSessionElapsed(session: FocusSessionEntity): Pair<Int, Boolean> {
        val nowWall = System.currentTimeMillis()
        val nowMono = SystemClock.elapsedRealtime()
        val currentBoot = querySystemBootCountOrNull()

        val wallElapsedSec = ((nowWall - session.startWallTimeMillis) / 1000L).coerceAtLeast(0L)
        val hmacValid = CryptoIntegrityManager.verifySessionState(session)

        val rebootOccurred = (currentBoot != null && session.bootCount > 0 && currentBoot != session.bootCount) ||
            (session.lastTickElapsedRealtimeMillis > 0L && nowMono < session.lastTickElapsedRealtimeMillis)

        return if (!rebootOccurred && session.startElapsedRealtimeMillis > 0L && nowMono >= session.startElapsedRealtimeMillis) {
            val monoElapsedSec = ((nowMono - session.startElapsedRealtimeMillis) / 1000L).coerceAtLeast(0L)
            // The drift test is SEGMENT-local: it compares wall against
            // mono since the pair of anchors was set — and every
            // re-anchor in this codebase (seal, block advance, reboot)
            // moves BOTH anchors to the same frame, so the comparison
            // never spans a boot or a block boundary.
            val drift = abs(wallElapsedSec - monoElapsedSec)
            val tamper = drift > 90L || !hmacValid
            Pair((session.accumulatedBeforeRebootSeconds + monoElapsedSec.toInt()), tamper)
        } else {
            // Device rebooted during an active lock (LOCKED_BOOT_COMPLETED):
            // Resume from accumulated elapsed seconds prior to reboot + seconds elapsed since current boot.
            // Issue #81 F3: the tamper term is no longer !hmacValid
            // alone. An unwitnessed intermediate boot (Safe Mode —
            // Monolith cannot run there, so it never re-anchors —
            // or a force-stop spanning a reboot) leaves a BOOT_COUNT
            // delta of 2+ against the row's anchor; that span is
            // unproven and convicts like any other tamper (the
            // caller's penalty ratchet applies). The elapsed figure
            // still credits only witnessed time — unproven hours
            // are neither credited toward completion nor silently
            // forgiven: they cost the tamper penalty instead of
            // passing as a free, invisible freeze.
            val priorElapsed = max(session.accumulatedBeforeRebootSeconds, session.elapsedSeconds)
            val sinceRebootSec = (nowMono / 1000L).toInt().coerceAtLeast(0)
            val unwitnessed = currentBoot != null && TamperWitness.unwitnessedBoot(session.bootCount, currentBoot)
            Pair(priorElapsed + sinceRebootSec, !hmacValid || unwitnessed)
        }
    }

    suspend fun getSystemGuardStatus(): SystemGuardStatus = withContext(Dispatchers.IO) {
        val active = dao.getActiveSessionSnapshot()
        // Issue #28: with no active session there is nothing to
        // measure against — intact stays false (unproven), never
        // a free pass printed as a fact.
        var clockIntact = false
        var hmacIntact = false
        var driftSeconds = 0L

        if (active != null) {
            val nowWall = System.currentTimeMillis()
            val nowMono = SystemClock.elapsedRealtime()
            val wallElapsed = (nowWall - active.startWallTimeMillis) / 1000L
            val monoElapsed = (nowMono - active.startElapsedRealtimeMillis) / 1000L
            driftSeconds = abs(wallElapsed - monoElapsed)
            clockIntact = driftSeconds <= 90L
            hmacIntact = CryptoIntegrityManager.verifySessionState(active)
        }

        val appsCount = dao.getAllAppsSnapshot().size
        SystemGuardStatus(
            hasUsageStatsPermission = hasUsageStatsPermission(),
            hasOverlayPermission = hasOverlayPermission(),
            isAccessibilityServiceEnabled = isAccessibilityEnabled(),
            isNotificationListenerEnabled = isNotificationListenerEnabled(),
            isDeviceAdminEnabled = isDeviceAdminActive(),
            isMonotonicClockIntact = clockIntact,
            isHmacSignatureValid = hmacIntact,
            clockDriftSeconds = driftSeconds,
            systemBootCount = querySystemBootCountOrNull() ?: 0,
            installedLauncherAppsCount = appsCount,
            installProvenanceUnproven = provenanceUnproven()

        )
    }

    /**
     * 3-Tier Per-App Rule Cycling (1 tap: Locked -> 2m Peek -> Open -> Locked).
     */
    suspend fun addBlockedSite(raw: String): Boolean {
        val domain = BlockedBrowserDomains.normalizeDomain(raw) ?: return false
        dao.insertSite(BlockedSiteEntity(domain))
        return true
    }

    suspend fun removeBlockedSite(domain: String) {
        // Issue #304 F1: removing a blocked site loosens the seal —
        // refused at the write boundary while a session is active
        // (the ViewModel ratchet at #151 already warns on the hand
        // path; this closes the verb itself for every caller).
        if (dao.getActiveSessionSnapshot()?.isActive == true) return
        dao.deleteSite(domain)
    }

    suspend fun setBrowserBlockerEnabled(enabled: Boolean) {
        // Slice E (§7.5): targeted UPDATE — the whole-row REPLACE
        // this replaces could clobber the receipt claim columns
        // from a stale read. The seed-if-absent stays for the
        // never-written singleton.
        if (dao.getGuardSettingsSnapshot() == null) {
            dao.insertGuardSettings(GuardSettingsEntity())
        }
        dao.updateBrowserBlockerEnabled(enabled)
    }

    suspend fun isBrowserBlockerEnabled(): Boolean =
        dao.getGuardSettingsSnapshot()?.browserBlockerEnabled ?: true

    // Truth Car 3 (UNA-01): the sniper switch, persisted with the
    // same discipline as the browser pair — seed-if-absent, then a
    // targeted UPDATE, never a whole-row REPLACE.
    suspend fun setShortsReelsSniperEnabled(enabled: Boolean) {
        if (dao.getGuardSettingsSnapshot() == null) {
            dao.insertGuardSettings(GuardSettingsEntity())
        }
        dao.updateShortsReelsSniperEnabled(enabled)
    }

    suspend fun isShortsReelsSniperEnabled(): Boolean =
        dao.getGuardSettingsSnapshot()?.shortsReelsSniperEnabled ?: true

    suspend fun updateRoutine(routine: RoutineEntity) {
        // Issue #32 F2: THE routine write funnel (ViewModel's
        // persistRoutine routes here) — sanitize at the boundary.
        dao.updateRoutine(routine.sanitized())
    }

    /** #153 F4: add a routine row (single persist funnel stamps
     *  the edit horizon, like every other routine write). */
    suspend fun addRoutine(routine: RoutineEntity) {
        val now = System.currentTimeMillis()
        dao.insertRoutines(listOf(routine.copy(scheduleEditedAtMillis = now)))
    }

    /** #153 F4: delete a routine row. Its sessions are not lost:
     *  derive() finalises orphaned routine sessions by their
     *  stored window snapshot (R5). */
    suspend fun deleteRoutine(id: Int) {
        dao.deleteRoutineById(id)
    }

    /** Guard settings as a flow (reset-note strip + tick stamps). */
    val guardSettingsFlow: Flow<GuardSettingsEntity?> = dao.getGuardSettingsFlow()

    /** The three routines of his real day (Sten §2.5 targets). */
    fun routineTargets(): List<RoutineEntity> = listOf(
        RoutineEntity(
            name = "FASTED BLOCK", startMinutes = 300, endMinutes = 480,
            daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.STUDY.name, enabled = true
        ),
        RoutineEntity(
            name = "DAY BLOCKS", startMinutes = 540, endMinutes = 1170,
            daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.STUDY.name, enabled = true
        ),
        RoutineEntity(
            name = "SLEEP", startMinutes = 1200, endMinutes = 210,
            daysMask = RoutineSchedule.ALL_DAYS, loadoutName = SmartLoadout.SLEEP.name, enabled = true
        )
    )

    /**
     * Version-blind reseed (Sten §2), run once under the seed lock from
     * [ensureSeededAndSyncPackages] when routines already exist. Exact-
     * match shipped seeds migrate in place (id preserved, enabled
     * carried); user-edited rows are never touched; missing targets are
     * inserted unless a row already carries the target's exact name.
     * The done flag is written LAST, after the alarm cleanup, so an
     * interrupted run simply redoes the (idempotent) transform.
     */
    internal suspend fun reseedRoutinesV1() {
        val settings = dao.getGuardSettingsSnapshot() ?: return
        if (settings.routineReseedV1Done) return
        val now = System.currentTimeMillis()

        fun matches(r: RoutineEntity, name: String, start: Int, end: Int, mask: Int, loadout: String) =
            r.name == name && r.startMinutes == start && r.endMinutes == end &&
                r.daysMask == mask && r.loadoutName == loadout

        // S1: shipped "Morning Deep Work" seed → DAY BLOCKS.
        dao.getAllRoutinesSnapshot().firstOrNull {
            matches(it, "Morning Deep Work", 540, 780, RoutineSchedule.MON_FRI, SmartLoadout.STUDY.name)
        }?.let { s1 ->
            dao.updateRoutine(
                s1.copy(
                    name = "DAY BLOCKS", startMinutes = 540, endMinutes = 1170,
                    daysMask = RoutineSchedule.ALL_DAYS,
                    loadoutName = SmartLoadout.STUDY.name,
                    scheduleEditedAtMillis = now
                )
            )
        }
        // S2: shipped "Night Wind-Down" seed → SLEEP.
        dao.getAllRoutinesSnapshot().firstOrNull {
            matches(it, "Night Wind-Down", 1350, 420, RoutineSchedule.ALL_DAYS, SmartLoadout.SLEEP.name)
        }?.let { s2 ->
            dao.updateRoutine(
                s2.copy(
                    name = "SLEEP", startMinutes = 1200, endMinutes = 210,
                    daysMask = RoutineSchedule.ALL_DAYS,
                    loadoutName = SmartLoadout.SLEEP.name,
                    scheduleEditedAtMillis = now
                )
            )
        }
        // Missing targets: insert, unless an exact match already exists
        // or any row carries the target's exact name (his own hand).
        for (target in routineTargets()) {
            val rows = dao.getAllRoutinesSnapshot()
            val exact = rows.any {
                matches(it, target.name, target.startMinutes, target.endMinutes, target.daysMask, target.loadoutName)
            }
            if (exact || rows.any { it.name == target.name }) continue
            dao.insertRoutines(listOf(target.copy(scheduleEditedAtMillis = now)))
        }
        // Legacy per-routine alarms die here; the chain rearms against
        // the post-transform snapshot.
        val post = dao.getAllRoutinesSnapshot()
        // ISSUE-67: the chain is driven via the service port.
        servicePort.cancelLegacyRoutineAlarms(context, post.map { it.id })
        servicePort.cancelRoutineChain(context)
        servicePort.rearmRoutineChain(context, post, now)
        // Flag last — targeted (§7.5), never a whole-row write.
        dao.markRoutineReseedV1Done()
    }

    // -- Install-day disclosures (R3b #161) ----------------------

    fun freshStartNotePending(): Boolean = InstallNoteStore(context).freshStartPending()

    fun dismissFreshStartNote() = InstallNoteStore(context).dismissFreshStart()

    /** Registers this open against the version this phone last
     *  opened; true when an update landed since. */
    fun registerInstallOpen(): Boolean {
        val version = runCatching {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
        }.getOrDefault(0L)
        return InstallNoteStore(context).registerOpen(version)
    }

    fun updateNotePending(): Boolean = InstallNoteStore(context).updatePending()

    fun dismissUpdateNote() = InstallNoteStore(context).dismissUpdate()

    /** The reset note is dismissed ONLY by the user's own tap on
     *  it — never by obeying it (R3b #161: an edit used to erase
     *  the only record that a reset happened). */
    suspend fun dismissRoutineResetNote() {
        val settings = dao.getGuardSettingsSnapshot() ?: return
        if (!settings.routineReseedNoteDismissed) {
            dao.markRoutineReseedNoteDismissed()
        }
    }

    /**
     * Stamps an attendance-evidence bit on the active routine session
     * (E2 foreground return, E4 valve hold start). first/last evidence
     * stamps track the earliest/latest E2/E4 instants. No-ops for
     * manual sessions and when nothing is active.
     */
    suspend fun recordAttendanceEvidence(kindBit: Int, atMillis: Long = System.currentTimeMillis()) {
        // Issue #33 (audit R1): the read-modify-write runs inside ONE
        // transaction and re-reads the row there — two evidence
        // stamps landing together can no longer lose each other's
        // bits to a stale whole-row write.
        databaseProvider().withTransaction {
            val active = dao.getActiveSessionSnapshot() ?: return@withTransaction
            if (active.routineId == null) return@withTransaction
            val first = if (active.firstEvidenceMillis == 0L) atMillis
                else minOf(active.firstEvidenceMillis, atMillis)
            val last = maxOf(active.lastEvidenceMillis, atMillis)
            if ((active.evidenceMask and kindBit) != 0 &&
                first == active.firstEvidenceMillis && last == active.lastEvidenceMillis
            ) return@withTransaction
            dao.updateSession(
                active.copy(
                    evidenceMask = active.evidenceMask or kindBit,
                    firstEvidenceMillis = first,
                    lastEvidenceMillis = last
                )
            )
        }
    }

    /**
     * Evidence-derived attended seconds (Sten §1.6.1/J1): the union of
     * five-minute coverage arcs around every evidence instant, clipped
     * to the session's window. Instants are blocked attempts inside
     * the session's live span, task completions stamped to this
     * session, and the E2/E4 first/last stamps. Session start is NOT
     * an instant — a window nobody showed up to books zero.
     * Returns (seconds, whether any intercept contributed).
     */
    private suspend fun computeAttendedSeconds(session: FocusSessionEntity): Pair<Int, Boolean> {
        val windowStart = session.windowStartMillis
        val windowEnd = session.windowEndMillis
        if (windowEnd <= windowStart) return 0 to false
        val instants = sortedSetOf<Long>()
        val liveFrom = maxOf(windowStart, session.startWallTimeMillis)
        val intercepts = dao.getInterceptLogsBetween(liveFrom, windowEnd)
        intercepts.forEach { instants.add(it.timestampMillis) }
        dao.getTasksCompletedInSession(session.id).forEach { task ->
            if (task.completedAtMillis > 0) instants.add(task.completedAtMillis)
        }
        if (session.firstEvidenceMillis > 0) instants.add(session.firstEvidenceMillis)
        if (session.lastEvidenceMillis > 0) instants.add(session.lastEvidenceMillis)
        if (instants.isEmpty()) return 0 to intercepts.isNotEmpty()
        var covered = 0L
        var curStart = -1L
        var curEnd = -1L
        for (t in instants) {
            val s = maxOf(t, windowStart)
            val e = minOf(t + EVIDENCE_COVERAGE_MILLIS, windowEnd)
            if (e <= s) continue
            if (curStart < 0L) {
                curStart = s; curEnd = e
            } else if (s <= curEnd) {
                if (e > curEnd) curEnd = e
            } else {
                covered += curEnd - curStart; curStart = s; curEnd = e
            }
        }
        if (curStart >= 0L) covered += curEnd - curStart
        val windowSec = (windowEnd - windowStart) / 1000L
        return (covered / 1000L).coerceAtMost(windowSec).toInt() to intercepts.isNotEmpty()
    }

    /**
     * The §5.4 finalise funnel — the ONLY writer of a routine session's
     * booked seconds and outcome. Idempotent: a stamped row is never
     * re-judged. Books attendedSeconds (evidence only, capped at the
     * window) in EVERY arm — coordinator arbitration A2. SLEEP rows
     * are never stamped (J2); they are simply stood down. Returns the
     * outcome name, or null for SLEEP / non-routine / already handled
     * without a stamp.
     */
    suspend fun finaliseRoutineSession(session: FocusSessionEntity, nowMillis: Long): String? =
        withContext(Dispatchers.IO) {
        if (session.routineId == null) return@withContext null
        if (session.routineOutcome != null) return@withContext session.routineOutcome
        val routine = dao.getRoutineById(session.routineId)
        if (routine?.loadoutName == SmartLoadout.SLEEP.name) {
            if (session.isActive) {
                dao.updateSession(
                    session.copy(
                        isActive = false,
                        // RC-01: a stood-down SLEEP row ended at
                        // its window's end — the frozen stamp is
                        // the exact end (first stamp wins).
                        endWallMillis = if (session.endWallMillis == 0L) {
                            session.windowEndMillis
                        } else {
                            session.endWallMillis
                        }
                    )
                )
            }
            return@withContext null
        }
        val (attended, usedIntercepts) = computeAttendedSeconds(session)
        val windowSec = ((session.windowEndMillis - session.windowStartMillis) / 1000L).coerceAtLeast(0L)
        val lateFire = session.startWallTimeMillis - session.windowStartMillis > 120_000L
        val remainderSec = ((session.windowEndMillis -
            maxOf(session.windowStartMillis, session.startWallTimeMillis)) / 1000L).coerceAtLeast(0L)
        val yardstickSec = if (lateFire) remainderSec else windowSec
        val outcome = when {
            attended < ATTENDANCE_FLOOR_SECONDS ->
                if (lateFire) WindowJudgement.LATE_FIRE else WindowJudgement.MISSED
            attended >= yardstickSec - ATTENDANCE_FLOOR_SECONDS -> WindowJudgement.ATTENDED
            else -> WindowJudgement.PARTIAL
        }
        dao.updateSession(
            session.copy(
                elapsedSeconds = attended,
                completedSuccessfully = outcome == WindowJudgement.ATTENDED,
                forgedArtifactId = if (outcome == WindowJudgement.ATTENDED) session.forgedArtifactId else null,
                routineOutcome = outcome.name,
                evidenceMask = session.evidenceMask or
                    (if (usedIntercepts) EVIDENCE_E3_INTERCEPT else 0),
                isActive = false,
                // RC-01: the funnel is the routine ending write —
                // its instant is the end it books (first stamp
                // wins; a re-judged row is never re-stamped, and
                // the funnel is idempotent by its own law above).
                endWallMillis = if (session.endWallMillis == 0L) {
                    nowMillis
                } else {
                    session.endWallMillis
                }
            )
        )
        outcome.name
    }

    /**
     * Stands a routine session down without judging it (SLEEP window
     * end). A plain isActive write — deliberately NOT the disarm path,
     * so no booking, stamp, or intercept log is produced.
     */
    suspend fun deactivateRoutineSession(session: FocusSessionEntity) {
        if (session.isActive) {
            dao.updateSession(
                session.copy(
                    isActive = false,
                    // RC-01: stand-down happens at the window's
                    // end; the frozen stamp is the exact end.
                    endWallMillis = if (session.endWallMillis == 0L) {
                        session.windowEndMillis
                    } else {
                        session.endWallMillis
                    }
                )
            )
        }
    }

    /**
     * Materialises a [DesiredSession] from the reconciler: applies the
     * loadout, starts the session with its window snapshot (E1 lands in
     * [startSession]), and raises the shield.
     */
    suspend fun materialiseRoutineSession(desired: DesiredSession): FocusSessionEntity {
        val loadout = runCatching { SmartLoadout.valueOf(desired.loadoutName) }
            .getOrDefault(SmartLoadout.STUDY)
        applySmartLoadout(loadout)
        val session = startSession(
            intention = desired.intention,
            profileName = desired.profileName,
            durationMinutes = desired.durationMinutes,
            studyBlocksCount = 1,
            routineId = desired.routineId,
            windowStartMillis = desired.windowStartMillis,
            windowEndMillis = desired.windowEndMillis
        )
        servicePort.startShield(context) // ISSUE-67: via the port
        return session
    }


    /** ISSUE-67: the apps snapshot as a repository read, so the
     *  shield loop (which already holds a repository) stops
     *  bypassing this layer for its per-iteration app reads. */
    suspend fun allAppsSnapshot(): List<BlockedAppEntity> = dao.getAllAppsSnapshot()

    suspend fun cycleAppBlockTier(app: BlockedAppEntity): AppBlockTier {
        val cycled = when (app.resolvedTier) {
            AppBlockTier.LOCKED -> AppBlockTier.PEEK_2M
            AppBlockTier.PEEK_2M -> AppBlockTier.OPEN
            AppBlockTier.OPEN -> AppBlockTier.LOCKED
        }
        // Issue #119, finding 5: the dialler floor — cycling a dialler
        // or telephony package can land on PEEK_2M, never on LOCKED,
        // so placing a call is always reachable.
        val nextTier = EssentialApps.floorTier(app.packageName, cycled)
        // Issue #304 F1: the direction gate at the write boundary —
        // while a session is sealed a cycle step that would loosen
        // (LOCKED -> PEEK, PEEK -> OPEN) is refused here, not only
        // in the ViewModel's ratchet, so no caller can weaken a
        // sealed rule through this verb. OPEN -> LOCKED tightens
        // and stays lawful mid-session.
        if (dao.getActiveSessionSnapshot()?.isActive == true &&
            SessionTierGate.loosens(app.resolvedTier, nextTier)
        ) {
            return app.resolvedTier
        }
        val isBlockedFlag = nextTier != AppBlockTier.OPEN
        dao.setAppTier(app.packageName, isBlockedFlag, nextTier.name)
        // A ruling landed (#129 F1): the guess no longer stands
        // unreviewed for this package, whatever tier he chose —
        // choosing OPEN himself IS a review.
        clearUnreviewedOpen(listOf(app.packageName))
        return nextTier
    }

    suspend fun setAppBlockTier(packageName: String, tier: AppBlockTier) {
        // Issue #304 F1: same gate — a direct tier write that would
        // loosen a sealed app's rule is refused at the repository,
        // whoever the caller is.
        if (dao.getActiveSessionSnapshot()?.isActive == true) {
            val current = dao.getAllAppsSnapshot()
                .firstOrNull { it.packageName == packageName }
            if (current != null && SessionTierGate.loosens(current.resolvedTier, tier)) {
                return
            }
        }
        // Issue #119, finding 5: the dialler floor — no caller may
        // classify the default dialler / telephony stack LOCKED.
        val effectiveTier = EssentialApps.floorTier(packageName, tier)
        val isBlockedFlag = effectiveTier != AppBlockTier.OPEN
        dao.setAppTier(packageName, isBlockedFlag, effectiveTier.name)
        // Issue #129 F1: a ruling from the man retires the
        // unreviewed record for this package.
        clearUnreviewedOpen(listOf(packageName))
    }

    /**
     * 1-Tap Smart Loadouts:
     * - Study Loadout: Locks Social & Games, sets YouTube & Messaging to 2m Peek, leaves Utility/Docs Open.
     * - Sleep Loadout: Locks everything except Phone, Clock, Maps, Camera, and navigation (EssentialApps, by package).
     */
    suspend fun applySmartLoadout(loadout: SmartLoadout) = withContext(Dispatchers.IO) {
        // Issue #129 F4/F2: resync FIRST, so the loadout derives
        // over the phone's whole catalogue — an app installed
        // after the last sync previously wasn't in this map at
        // all, so the loadout passed over it and it stayed
        // whatever (nothing) it was while the seal counted held.
        ensureSeededAndSyncPackages()
        val allApps = dao.getAllAppsSnapshot()
        // Issue #304 F1: while a session is sealed the loadout lands
        // tightening-only — each app keeps the stricter of its
        // current tier and the preset's tier, so a mid-session
        // loadout tap can never loosen every app at once (the exact
        // hole the AppBlock head-to-head named). Unsealed, and at
        // routine materialisation (which applies the loadout BEFORE
        // its session starts), the preset lands whole, as before.
        val sealed = dao.getActiveSessionSnapshot()?.isActive == true
        // Issue #121: the SLEEP loadout's structural time-critical
        // set (alarm / calendar class by capability and exact
        // identity — TimeCriticalApps), resolved once per
        // application and shared with the Shield preview and the
        // ratchet warning, so preview and commit cannot diverge.
        val timeCriticalPackages =
            if (loadout == SmartLoadout.SLEEP) {
                TimeCriticalApps.resolveTimeCriticalPackages(
                    context, allApps.map { it.packageName }
                )
            } else {
                emptySet()
            }
        val updated = allApps.map { app ->
            // Issue #107: the tier rule is LoadoutRules.tierFor —
            // the same pure function the Shield preview prints its
            // counts from, so preview and commit can never diverge
            // (its rules are the exact rules this function used to
            // inline). Issue #32 F4: set the tier ONLY; normalized()
            // derives isBlocked from it at the single derivation point.
            // Issue #119 F4/F5 ride inside LoadoutRules.tierFor itself
            // (SLEEP essentials by package + the dialler floor), so
            // preview and commit carry them identically. Issue #129
            // F4: tierFor also takes the current tier, so a loadout
            // never loosens LOCKED->OPEN on a category guess; the
            // sealed-session gate then clamps whatever remains.
            // W3 #134 (composition with #107 + #304 + #121): telephony is a
            // safety surface — the dialer's desired tier is OPEN at
            // this commit point (tierFor itself stays pure for the
            // preview, and its own #119 floor already caps the
            // dialler at PEEK); the sealed-session gate then clamps
            // the desired tier exactly as for every other app, so
            // #304's no-loosening law governs a sealed session.
            val desired = if (isDialerPackage(app.packageName)) {
                AppBlockTier.OPEN
            } else {
                LoadoutRules.tierFor(loadout, app.packageName, app.appName, app.category, app.resolvedTier, timeCriticalPackages)
            }
            val tier = SessionTierGate.clampWhileSealed(app.resolvedTier, desired, sealed)
            app.copy(blockTier = tier.name).normalized()
        }
        dao.insertApps(updated)
        // Apps this loadout actually ruled (any non-OPEN landing)
        // leave the unreviewed record (#129 F1): the loadout act
        // is a ruling on them. Apps it left OPEN — the unknowns
        // in SHOPPING above all — stay in the record, still
        // awaiting his word.
        clearUnreviewedOpen(
            updated.filter { it.resolvedTier != AppBlockTier.OPEN }.map { it.packageName }
        )
    }

    /**
     * Issue #107 (shield-setup-lived #3): the user's correction of
     * the category guess. The category is an existing column on the
     * app row — correcting it is a plain row update, no schema
     * change. The corrected category then drives birth tiers,
     * filters and loadouts like any other fact.
     */
    suspend fun setAppCategory(packageName: String, category: AppCategory) =
        withContext(Dispatchers.IO) {
            val app = dao.getAllAppsSnapshot()
                .firstOrNull { it.packageName == packageName } ?: return@withContext
            if (app.category != category.name) {
                dao.updateApp(app.copy(category = category.name))
            }
        }

    /**
     * Enforces the Mandatory 20-Minute Cooldown per package using `SystemClock.elapsedRealtime()`:
     * If `lastPeekEndElapsedRealtime > 0`, a new 2m Peek cannot be started until
     * `SystemClock.elapsedRealtime() >= lastPeekEndElapsedRealtime + PEEK_COOLDOWN_MILLIS`.
     * The cooldown is armed at the peek's END (stamped by
     * [recordPeekEnded]); the `max` with the live lease end is the
     * fail-safe for a lease that lapses unrecorded, so the window can
     * never be shortened by killing the app mid-peek.
     */
    fun getPeekCooldownRemainingSeconds(app: BlockedAppEntity): Int =
        // ISSUE-67: the cooldown arithmetic is domain logic
        // (FocusDomain, pure + JVM-tested); only the clock read
        // stays here, where Android belongs.
        FocusDomain.peekCooldownRemainingSeconds(
            lastPeekEndMono = app.lastPeekEndElapsedRealtime,
            activePeekUntilMono = app.activePeekUntilElapsedRealtime,
            nowMono = SystemClock.elapsedRealtime(),
            cooldownMillis = PEEK_COOLDOWN_MILLIS
        )

    /**
     * Issue #55: the cooldown's absolute deadline in the SAME
     * monotonic domain the lease machinery uses
     * (`SystemClock.elapsedRealtime()`), or 0 when no cooldown is
     * armed. The airlock overlay derives its remaining time from
     * this instant with ceiling math — never from a locally
     * reconstructed wall-clock deadline, which could unlock the
     * peek before this repository would grant it.
     */
    fun getPeekCooldownDeadlineMono(app: BlockedAppEntity): Long {
        val anchorEndMono = max(app.lastPeekEndElapsedRealtime, app.activePeekUntilElapsedRealtime)
        if (anchorEndMono <= 0L) return 0L
        return anchorEndMono + PEEK_COOLDOWN_MILLIS
    }

    suspend fun getPeekCooldownRemainingSeconds(packageName: String): Int {
        val app = dao.getAllAppsSnapshot().firstOrNull { it.packageName == packageName } ?: return 0
        return getPeekCooldownRemainingSeconds(app)
    }

    /**
     * Grants a 2-minute peek lease (`activePeekUntilElapsedRealtime = nowMono + 120_000L`).
     * The 20-minute cooldown is NOT armed here (issue #118, finding 4):
     * `lastPeekEndElapsedRealtime` keeps its previous value untouched
     * and is stamped only by [recordPeekEnded], when the peek actually
     * ends — so the cooldown runs from the peek's end, never from its
     * start, and the recorded end is the real end, not a prediction
     * written before the peek began. The anti-abuse limit is unchanged:
     * [getPeekCooldownRemainingSeconds] also anchors on the live lease
     * end, so a lease that lapses without a recorded end (process
     * death mid-peek) still cannot be re-peeked inside the window.
     */
    suspend fun startPeekLease(packageName: String) {
        val nowMono = SystemClock.elapsedRealtime()
        val leaseEndMono = nowMono + PEEK_DURATION_MILLIS
        val previousPeekEndMono = dao.getAllAppsSnapshot()
            .firstOrNull { it.packageName == packageName }
            ?.lastPeekEndElapsedRealtime ?: 0L
        dao.updatePeekLeaseAndCooldown(
            packageName = packageName,
            activeUntilMono = leaseEndMono,
            peekEndMono = previousPeekEndMono
        )
    }

    /**
     * Ends a 2-minute peek lease immediately (either when 120s expires or when the user taps ✕)
     * and anchors the mandatory 20-minute cooldown to `SystemClock.elapsedRealtime()`.
     */
    suspend fun recordPeekEnded(packageName: String) {
        val nowMono = SystemClock.elapsedRealtime()
        dao.updatePeekLeaseAndCooldown(
            packageName = packageName,
            activeUntilMono = 0L,
            peekEndMono = nowMono
        )
    }

    suspend fun toggleAppBlocked(app: BlockedAppEntity) {
        cycleAppBlockTier(app)
    }

    suspend fun updateAppDailyLimit(packageName: String, limitMinutes: Int) {
        dao.updateDailyLimit(packageName, limitMinutes)
    }

    suspend fun addCustomApp(appName: String, packageName: String, category: AppCategory) {
        val cleanPkg = packageName.trim().ifEmpty {
            "com.custom.${appName.lowercase(java.util.Locale.ROOT).replace(" ", "")}"
        }
        dao.insertApp(
            BlockedAppEntity(
                packageName = cleanPkg,
                appName = appName.trim(),
                category = category.name,
                isBlocked = true,
                blockTier = AppBlockTier.LOCKED.name,
                dailyLimitMinutes = 25,
                todayUsageMinutes = 10,
                interceptCount = 0
            )
        )
    }

    // Issue #139 F4: the pre-apply snapshot. Applying a profile
    // bulk-rewrites every app's tier; before it does, the current
    // hand-set tiers are persisted here so one undo restores them.
    private fun profileUndoPrefs() =
        context.getSharedPreferences(APP_LIST_PREFS_NAME, Context.MODE_PRIVATE)

    fun hasProfileUndoSnapshot(): Boolean =
        profileUndoPrefs().contains(PROFILE_UNDO_KEY)

    /** Issue #139 F4, process-death completion: the pre-tap dial
     *  minutes and profile name, persisted in the same prefs entry
     *  as the tiers snapshot, so an undo after process death
     *  restores all three — not just the tiers. Written by the
     *  view model at tap time; removed when the snapshot is
     *  consumed by [restoreProfileUndoSnapshot]. */
    fun saveProfileUndoContext(dialMinutes: Int, profileName: String) {
        profileUndoPrefs().edit()
            .putInt(PROFILE_UNDO_DIAL_KEY, dialMinutes)
            .putString(PROFILE_UNDO_NAME_KEY, profileName)
            .apply()
    }

    fun profileUndoDialMinutes(): Int? =
        profileUndoPrefs().takeIf { it.contains(PROFILE_UNDO_DIAL_KEY) }
            ?.getInt(PROFILE_UNDO_DIAL_KEY, 0)

    fun profileUndoName(): String? =
        profileUndoPrefs().getString(PROFILE_UNDO_NAME_KEY, null)

    suspend fun restoreProfileUndoSnapshot(): Boolean {
        val raw = profileUndoPrefs().getString(PROFILE_UNDO_KEY, null) ?: return false
        val saved = raw.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNull { line ->
                val parts = line.split("|")
                if (parts.size == 3) parts[0] to (parts[1] to (parts[2] == "1")) else null
            }
            .toMap()
        if (saved.isEmpty()) return false
        val restored = dao.getAllAppsSnapshot().map { app ->
            val (tier, blocked) = saved[app.packageName] ?: return@map app
            app.copy(blockTier = tier, isBlocked = blocked)
        }
        dao.insertApps(restored)
        profileUndoPrefs().edit()
            .remove(PROFILE_UNDO_KEY)
            .remove(PROFILE_UNDO_DIAL_KEY)
            .remove(PROFILE_UNDO_NAME_KEY)
            .apply()
        return true
    }

    /** The apps no profile may ever lock: the clock and maps are
     *  how he wakes and gets home. Sleep's own description names
     *  Phone, Clock, and Maps as its exceptions (issue #139 F2) —
     *  the category system cannot express the exception, so it
     *  lives here as an invariant. Membership is by EssentialApps'
     *  exact package sets (issue #119) — never substring luck.
     *  The dialer is NOT listed here on purpose: the dialler floor
     *  (EssentialApps.floorTier, applied to every row inside
     *  applyProfile) already guarantees no profile can lock the
     *  phone either. */
    private fun isProfileExempt(packageName: String): Boolean =
        EssentialApps.isClockPackage(packageName) ||
            EssentialApps.isNavigationPackage(packageName)

    suspend fun applyProfile(profile: FocusProfileEntity) = withContext(Dispatchers.IO) {
        val activeCategories = profile.blockedCategoriesCsv
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

        val currentApps = dao.getAllAppsSnapshot()
        // Snapshot BEFORE the overwrite (issue #139 F4).
        profileUndoPrefs().edit()
            .putString(
                PROFILE_UNDO_KEY,
                currentApps.joinToString("\n") { app ->
                    "${app.packageName}|${app.blockTier}|${if (app.isBlocked) "1" else "0"}"
                }
            )
            .apply()
        val updated = currentApps.map { app ->
            val shouldBlock = app.category in activeCategories
            val rawTier = when {
                // Issue #139 F2: clock + navigation are exempt — a
                // profile can never lock how he wakes or gets home.
                isProfileExempt(app.packageName) -> AppBlockTier.OPEN
                !shouldBlock -> AppBlockTier.OPEN
                app.category == AppCategory.MESSAGING.name -> AppBlockTier.PEEK_2M
                // Issue #120: a profile can no more bulk-LOCK
                // finance than it can lock the dialler (issue
                // #119's floor) — even a profile that lists FINANCE
                // applies it as PEEK_2M, so the mid-session OTP/UPI
                // door stays lawful instead of leaving only the
                // once-a-week emergency valve.
                app.category == AppCategory.FINANCE.name -> AppBlockTier.PEEK_2M
                else -> AppBlockTier.LOCKED
            }
            // Issue #32 F4: tier is the truth; isBlocked is derived
            // by normalized() — note shouldBlock (category match) and
            // tier != OPEN can differ for MESSAGING (PEEK), and the
            // tier is what enforcement reads, so the tier wins.
            // Issue #119, finding 5: the dialler floor binds bulk
            // profile writes too — a profile can never lock the
            // dialler, so the floor applies before the copy.
            val tier = EssentialApps.floorTier(app.packageName, rawTier)
            app.copy(blockTier = tier.name).normalized()
        }
        dao.insertApps(updated)
    }

    suspend fun toggleProfileSchedule(profile: FocusProfileEntity) {
        dao.updateProfile(profile.copy(isScheduleEnabled = !profile.isScheduleEnabled))
    }

    suspend fun updateProfileTimeWindow(profile: FocusProfileEntity, startHour: Int, endHour: Int) {
        val safeStart = startHour.coerceIn(0, 23)
        val safeEnd = endHour.coerceIn(1, 24)
        // Issue #139 F5: an hours-only edit must never destroy the
        // minutes the profile already carries — Sleep's 22:30 start
        // was silently rewritten to 22:00, and its "Nightly" label
        // replaced. Minutes and the label's own suffix survive; the
        // printed window shows the real minutes.
        val suffix = profile.scheduleLabel.substringAfter(" • ", "Active Window")
        val scheduleStr = String.format(
            Locale.US, "%02d:%02d – %02d:%02d • %s",
            safeStart, profile.startMinute, safeEnd, profile.endMinute, suffix
        )
        dao.updateProfile(
            profile.copy(
                startHour = safeStart,
                endHour = safeEnd,
                scheduleLabel = scheduleStr
            )
        )
    }

    suspend fun saveCustomProfile(
        name: String,
        minutes: Int,
        categories: List<AppCategory>,
        startHour: Int,
        endHour: Int
    ) {
        val csv = categories.joinToString(",") { it.name }
        val code = name.take(10).trim().ifEmpty { "Custom" }
        val scheduleStr = String.format("%02d:00 – %02d:00 • Custom", startHour, endHour)
        dao.insertProfile(
            FocusProfileEntity(
                name = name.trim(),
                codeName = code,
                description = "Custom ${minutes}m block shielding ${categories.size} categories.",
                defaultMinutes = minutes,
                blockedCategoriesCsv = csv,
                scheduleLabel = scheduleStr,
                // Issue #139 F1: no engine reads a profile schedule,
                // so a new profile must not be born claiming one is
                // enabled. The window is stored as a note only.
                isScheduleEnabled = false,
                startHour = startHour,
                startMinute = 0,
                endHour = endHour,
                endMinute = 0
            ).sanitized() // Issue #32 F2: write-boundary sanitize
        )
    }

    /**
     * Starts a focus session:
     * - Dialing below 10 minutes activates Count-Up Stopwatch Mode with a mandatory 20-minute minimum lock floor.
     * - Signs state with AndroidKeyStore HmacSHA256 (`CryptoIntegrityManager`).
     */
    /** The held-apps count (Fader): the apps the shield actually
     *  holds for a session born now — every app whose resolved
     *  tier is not OPEN. One derivation, shared by the birth
     *  capture and every surface that ever prints the figure. */
    fun countHeldApps(apps: List<BlockedAppEntity>): Int =
        // ISSUE-67: domain derivation (FocusDomain), fed tier names.
        FocusDomain.countHeldApps(apps.map { it.resolvedTier.name })

    /** W-41: the same held count, split by tier — SHUT (LOCKED)
     *  first, PEEK (PEEK_2M) second. The receipt's HELD line
     *  prints the two as separate counts: a shut app and a
     *  peek-lease app are different promises and never flatten
     *  into one figure on the slip. A tier string no code here
     *  writes resolves fail-closed to LOCKED (see resolvedTier),
     *  so it lands in the SHUT count and the split still sums to
     *  [countHeldApps]. */
    fun countHeldAppsSplit(apps: List<BlockedAppEntity>): Pair<Int, Int> =
        apps.count { it.resolvedTier == AppBlockTier.LOCKED } to
            apps.count { it.resolvedTier == AppBlockTier.PEEK_2M }

    suspend fun startSession(
        intention: String,
        profileName: String,
        durationMinutes: Int,
        studyBlocksCount: Int = 1,
        routineId: Int? = null,
        windowStartMillis: Long = 0L,
        windowEndMillis: Long = 0L
    ): FocusSessionEntity {
        // Issue #129 F2: the catalogue resyncs at EVERY session
        // birth, before the held counts are captured below. An app
        // installed after the last Monolith launch / sync — even a
        // factory-preset one that will be born LOCKED — previously
        // had no row, no tier and no enforcement for the whole
        // session, because both enforcement doors only read rows
        // that exist. Birth is the one moment every session passes
        // through, whichever door armed it (hand or routine).
        ensureSeededAndSyncPackages()
        val isStopwatch = durationMinutes < 10
        val targetSecs = if (isStopwatch) {
            STOPWATCH_MIN_FLOOR_SECONDS
        } else {
            (durationMinutes * 60).coerceAtLeast(60)
        }
        val wallNow = System.currentTimeMillis()
        val monoNow = SystemClock.elapsedRealtime()
        val bootCount = querySystemBootCountOrNull() ?: 0
        val hmac = CryptoIntegrityManager.signSessionState(
            startWallTimeMillis = wallNow,
            startElapsedRealtimeMillis = monoNow,
            bootCount = bootCount,
            targetDurationSeconds = targetSecs,
            isStopwatch = isStopwatch
        )

        val session = FocusSessionEntity(
            // Issue #124 (typing-intention): the intention is
            // frozen HERE, at birth, exactly as declared — trimmed
            // and capped, and a blank stays blank. The old ifBlank
            // substitution forged a stock phrase ("Deep
            // Uninterrupted Study") and read it back to the user
            // as their own declaration; a user who declares
            // nothing has declared nothing, and every surface
            // downstream (receipt title, airlock line, shield
            // notification) prints this row's words or nothing.
            intention = intention.trim().take(MAX_INTENTION_CHARS),
            profileName = profileName,
            targetDurationSeconds = targetSecs,
            elapsedSeconds = 0,
            startWallTimeMillis = wallNow,
            startElapsedRealtimeMillis = monoNow,
            bootCount = bootCount,
            accumulatedBeforeRebootSeconds = 0,
            lastTickElapsedRealtimeMillis = monoNow,
            hmacSignature = hmac,
            isStopwatchMode = isStopwatch,
            totalStudyBlocks = studyBlocksCount.coerceIn(1, 8),
            currentBlockIndex = 1,
            isInSafeBreak = false,
            safeBreakSecondsRemaining = 0,
            penaltySecondsAdded = 0,
            isActive = true,
            completedSuccessfully = false,
            phantomReachesBlocked = 0,
            tamperDetected = false,
            forgedArtifactId = null,
            routineId = routineId,
            windowStartMillis = windowStartMillis,
            windowEndMillis = windowEndMillis,
            // E1: the window is open and his kit is on (Sten §1.6.1).
            // first/last evidence stamps stay 0 until E2/E4 land.
            evidenceMask = if (routineId != null) EVIDENCE_E1_START else 0
        ).sanitized() // Issue #32 F2: write-boundary sanitize (startSession funnel)
        // Issue #152: witness the zone this arming is lived in,
        // keyed by the arm instant, so the receipt can print its
        // ARMED time truthfully after a landing. Written at this
        // funnel because it is the ONLY session birthplace — a
        // capture anywhere else would miss a door.
        armZoneStore.put(wallNow, java.time.ZoneId.systemDefault().id)
        // Slice E (Fader's writer law): the held counts are captured
        // INSIDE the birth transaction, from the same snapshot that
        // deactivates the predecessor — the set the shield will
        // actually hold for this row, provable later from the row
        // itself. Exactly one writer exists for these columns
        // (this insert); no DAO update for them will ever exist.
        val database = db() // ISSUE-67: injected database, no re-fetch
        return database.withTransaction {
            val snapshotApps = dao.getAllAppsSnapshot()
            val heldApps = countHeldApps(snapshotApps)
            // W-41: the tier split rides the same snapshot and
            // the same single writer — the split is a seal fact
            // stored on the row, never a receipt-time re-count
            // against tiers that may have changed since.
            val (heldShut, heldPeek) = countHeldAppsSplit(snapshotApps)
            // W3 #130 (recomposed at the DURCH-D landing): sites are
            // held only when the blocker that enforces them is on —
            // the same toggle the service reads per event (default
            // ON when no row exists).
            val heldSites = heldSitesCount(
                dao.getAllSitesSnapshot().size,
                dao.getGuardSettingsSnapshot()?.browserBlockerEnabled ?: true
            )
            dao.deactivateAllSessions()
            val id = dao.insertSession(
                session.copy(
                    heldAppsAtSeal = heldApps, heldSitesAtSeal = heldSites,
                    heldShutAtSeal = heldShut, heldPeekAtSeal = heldPeek
                )
            ).toInt()
            session.copy(
                id = id, heldAppsAtSeal = heldApps, heldSitesAtSeal = heldSites,
                heldShutAtSeal = heldShut, heldPeekAtSeal = heldPeek
            )
        }
    }

    /** The active session row as a one-shot read (the cascade's
     *  L1 snapshot witness reads through this). */
    suspend fun activeSessionSnapshot(): FocusSessionEntity? = dao.getActiveSessionSnapshot()

    /** All app rows as a one-shot read (Issue #78: the Airlock's
     *  intent revalidation reads the tier from the store, never
     *  from a possibly-stale flow or the intent's own extras). */
    suspend fun appsSnapshot(): List<BlockedAppEntity> = dao.getAllAppsSnapshot()

    /** Any session row by id (Slice E: the finish machine's and
     *  the seal transition's reads). */
    suspend fun sessionById(id: Int): FocusSessionEntity? = dao.getSessionById(id)

    /** Issue #135 F1: the most recent FINISHED session row (history
     *  is start-DESC, so the first inactive row is the latest), or
     *  null when no session has ever finished. The VM restores the
     *  arm setup from this row after a process death. */
    suspend fun lastFinishedSessionSnapshot(): FocusSessionEntity? =
        dao.getSessionHistorySnapshot().firstOrNull { !it.isActive }

    /**
     * THE SEAL (Slice D step 4, Opal §F2): at t=0 the session row
     * is re-anchored to the seal instant and re-signed. startSession
     * anchored the row at insert; without this the session would
     * count up to the whole proving window (≤500ms) as proven
     * session time it never was. The re-sign is REQUIRED, not
     * polish: computeVerifiedSessionElapsed verifies the HMAC over
     * exactly these anchors, so an un-signed re-anchor would read
     * as tamper and draw the penalty. Both anchors move to the
     * same frame (mono = t0, wall = the wall clock at that frame)
     * so the mono/wall cross-check stays consistent.
     */
    suspend fun sealSession(sessionId: Int, t0Mono: Long): Boolean {
        // Klinke §4.3.2 (Slice E): the Boolean IS the guard — the
        // transition may sound the Throw only on a true return, so
        // a row that died under the attempt (annulment won the
        // race, a superseding birth deactivated it) refuses here
        // and no seal ever sounds over an un-re-anchored row.
        // Read+write run in one transaction (his R-K3 mitigation):
        // a shield tick landing between them can no longer be
        // clobbered by the stale copy.
        val database = db() // ISSUE-67: injected database, no re-fetch
        return database.withTransaction {
            val row = dao.getActiveSessionSnapshot() ?: return@withTransaction false
            if (row.id != sessionId || !row.isActive) return@withTransaction false
            val wallNow = System.currentTimeMillis()
            val hmac = CryptoIntegrityManager.signSessionState(
                startWallTimeMillis = wallNow,
                startElapsedRealtimeMillis = t0Mono,
                bootCount = row.bootCount,
                targetDurationSeconds = row.targetDurationSeconds,
                isStopwatch = row.isStopwatchMode
            )
            dao.updateSession(
                row.copy(
                    startWallTimeMillis = wallNow,
                    startElapsedRealtimeMillis = t0Mono,
                    lastTickElapsedRealtimeMillis = t0Mono,
                    hmacSignature = hmac
                )
            )
            true
        }
    }

    /**
     * ANNULMENT (Slice D step 4, Opal §F4): a proving attempt that
     * never reached t=0 un-exists - the row is DELETED, not
     * deactivated. disarmActiveSession is forbidden here: it writes
     * an outcome and a history row for a session that, in law,
     * never was. Pre-t0 only; the controller is the only caller
     * and it annuls solely from inside a live proving attempt.
     *
     * Issue #126 F5 rider: the deletion law stands, but it no
     * longer gets to pretend the wait's enforcement didn't
     * happen — the shield ran from the latch frame, and any
     * intercept it logged lives in intercept_logs, a separate
     * table this delete does not touch. The user-facing trace
     * of the annulment is the controller's sentence, which now
     * states that any blocking from the wait is off. A durable
     * per-attempt receipt would break the no-history law above
     * and is NOT built here — it is a decision for Sarthak.
     */
    suspend fun annulProvingSession(sessionId: Int) {
        dao.deleteSessionById(sessionId)
    }

    // =====================================================================
    // SLICE E — THE RECEIPT (Nocke's claim, Zettel's contract,
    // Reihe's assembly). The claim transaction is the publish
    // latch: ONE withTransaction on the guard_settings singleton
    // decides, atomically, whether this session's receipt mints a
    // № or returns the one already standing. No second path mints.
    // =====================================================================

    /** The guard row as it stands (created with defaults if the
     *  singleton was never written). */
    private suspend fun guardSettingsRow(): GuardSettingsEntity =
        dao.getGuardSettingsSnapshot() ?: GuardSettingsEntity()

    /**
     * Nocke §3's claim, Zettel Z1's latch: if the finish composite
     * has already fired for [sessionId], or its receipt already
     * stands, returns the standing № with NO increment; otherwise
     * mints: receiptSequence += 1, all three pointers move
     * together, in this one transaction. Returns the № the slip
     * prints. Valve / grace / early endings never call this —
     * they publish nothing and leave no gap in the sequence.
     */
    suspend fun claimReceiptNumber(
        sessionId: Int,
        outcomeWord: String,
        faulted: Boolean = false,
        faultWallMillis: Long = 0L
    ): Int {
        val database = db() // ISSUE-67: injected database, no re-fetch
        return database.withTransaction {
            // Seed-if-absent INSIDE the transaction: the claim's
            // targeted UPDATE matches zero rows on a settings row
            // that was never written, and the № would exist only
            // in memory (the repository test caught exactly this
            // on a fresh sandbox). The claim mints the sequence —
            // it must own its row's existence.
            if (dao.getGuardSettingsSnapshot() == null) {
                dao.insertGuardSettings(GuardSettingsEntity())
            }
            val settings = guardSettingsRow()
            // Truth Car 2 (RC-04): the ROW is the latch. A session
            // that already claimed carries its № on its own row —
            // return it, mint nothing, whatever the settings slot
            // holds by now. (The old guard read only the slot: a
            // session re-published after a later session claimed
            // would have minted a second №, forbidden only by the
            // arithmetic coincidence of two span constants in two
            // files. The row ends the coincidence.)
            val row = dao.getSessionById(sessionId)
            if (row != null && row.receiptNumber > 0) {
                // Audit R1 #40: the release-fault stamp rides the
                // claim transaction even on the latch path — a
                // faulted publish must never leave a claimed
                // receipt whose fault record a crash could strand
                // (rehydration reads releaseFaultShield as the
                // receipt's faulted flag, forever).
                if (faulted && !row.releaseFaultShield) {
                    dao.stampReleaseFault(sessionId, faultWallMillis)
                }
                return@withTransaction row.receiptNumber
            }
            if (settings.lastReceiptSessionId == sessionId && settings.receiptSequence > 0) {
                if (faulted && row != null && !row.releaseFaultShield) {
                    dao.stampReleaseFault(sessionId, faultWallMillis)
                }
                return@withTransaction settings.receiptSequence
            }
            val next = settings.receiptSequence + 1
            // Targeted write (§7.5): the claim touches its four
            // columns and no other — a whole-row REPLACE here
            // would clobber settings written since the read.
            dao.writeReceiptClaim(
                sessionId = sessionId,
                outcome = outcomeWord,
                sequence = next,
                receiptSessionId = sessionId
            )
            // The № is stamped on the session row in the SAME
            // transaction (RC-04): claim and stamp commit or roll
            // back together, so a № can never exist without its
            // row wearing it.
            dao.stampReceiptNumber(sessionId, next)
            // Audit R1 #40: the fault stamp joins the same
            // transaction — claim, № stamp and fault record now
            // commit or roll back as ONE act. (publishReceipt used
            // to stamp the fault in a separate write after the
            // claim; a crash in the gap left a claimed receipt
            // whose faulted flag rehydrated as false forever.)
            if (faulted) {
                dao.stampReleaseFault(sessionId, faultWallMillis)
            }
            next
        }
    }

    /** The standing receipt's identity, for rehydration (Z6 case
     *  b): (№, sessionId, outcomeWord) when a receipt stands. */
    suspend fun standingReceipt(): Triple<Int, Int, String>? {
        val settings = dao.getGuardSettingsSnapshot() ?: return null
        if (settings.receiptSequence <= 0 || settings.lastReceiptSessionId <= 0) return null
        return Triple(settings.receiptSequence, settings.lastReceiptSessionId, settings.lastReceiptOutcome)
    }

    /** The WITHIN line's clock (#88): the device's own 12/24-hour
     *  setting via [ClockText], never a pinned 24-hour format. */
    private fun hhmm(wallMillis: Long): String =
        ClockText.timeOfDay(context, wallMillis)

    /** Issue #152: [hhmm] in an explicit zone — the WITHIN line's
     *  frozen window was lived in the arm zone, not in whatever
     *  zone the phone sits in when the slip is assembled. Composed
     *  with #88 on merge: the pattern follows the device's own
     *  12/24-hour setting and locale (ClockText's law), only the
     *  zone is explicit. */
    private fun hhmm(wallMillis: Long, zone: java.time.ZoneId): String {
        val pattern = if (android.text.format.DateFormat.is24HourFormat(context)) "HH:mm" else "h:mm a"
        val format = java.text.SimpleDateFormat(pattern, java.util.Locale.getDefault())
        format.timeZone = java.util.TimeZone.getTimeZone(zone)
        return format.format(java.util.Date(wallMillis))
    }

    /** Issue #152: the UTC offset in force for [zone] at
     *  [wallMillis], as printed ("+05:30"; UTC is "+00:00"). */
    private fun zoneOffsetLabel(wallMillis: Long, zone: java.time.ZoneId): String {
        val offset = zone.rules.getOffset(java.time.Instant.ofEpochMilli(wallMillis))
        return if (offset == java.time.ZoneOffset.UTC) "+00:00" else offset.id
    }

    private fun hhmm(minutesOfDay: Int): String {
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, (minutesOfDay / 60) % 24)
            set(java.util.Calendar.MINUTE, minutesOfDay % 60)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return ClockText.timeOfDay(context, cal.timeInMillis)
    }

    /**
     * Reihe §3's assembly: the pending receipt's facts, from pure
     * reads of stored rows only — the ended row's own fields, the
     * frozen routine stamps, the spend ledger, the intercept
     * window. [kind] is the finish machine's classification.
     * Returns null when the row is gone or not in a publishable
     * state (the claim gate re-checks under the transaction's
     * own read in [publishReceipt]).
     */
    suspend fun assemblePendingReceipt(
        sessionId: Int,
        kind: String,
        endedAtWallMs: Long
    ): PendingReceipt? {
        val row = dao.getSessionById(sessionId) ?: return null
        if (row.isActive) return null

        // Truth Car 2 (RC-01): the slip's ENDED reads the row's
        // own stamped end whenever the ending write recorded one —
        // on EVERY path (witnessed, discovered, rehydrated). The
        // caller's [endedAtWallMs] is only the fallback for rows
        // the stamp never reached (pre-v11 rows): on the branch-U
        // discovery path it is the discovery instant, and printing
        // it as the end was the lie this closes.
        val trueEndedAtWallMs = if (row.endWallMillis > 0L) row.endWallMillis else endedAtWallMs

        // The routine line (Z5 + F-Z5). Routine rows: frozen at
        // insert — the name stripped from profileName's "Routine:
        // <name>", the window from the row's frozen stamps, never
        // re-evaluated. Hand-armed rows: §5.7 honoured at assembly
        // (F-Z5, +8) — a routine whose window contains the arm
        // instant lends its line; the BY… form never prints.
        val routineLabel: String? = if (row.routineId != null) {
            val name = row.profileName.removePrefix(ROUTINE_PROFILE_PREFIX).trim()
            // Issue #152: the frozen window was lived in the arm
            // zone (witnessed at the session's birth — ArmZoneStore).
            // When the phone has since landed in another zone, the
            // WITHIN line prints the lived window in that zone with
            // its offset named, instead of silently re-rendering
            // the instants in the arrival zone. Same zone (the
            // overwhelming case) the print is exactly as before.
            val armZone = armZoneStore.zoneFor(row.startWallTimeMillis)
                ?.let { id -> runCatching { java.time.ZoneId.of(id) }.getOrNull() }
            val crossedZones = armZone != null && armZone != java.time.ZoneId.systemDefault()
            if (name.isNotEmpty() && row.windowEndMillis > row.windowStartMillis) {
                if (crossedZones) {
                    "WITHIN ${name.uppercase(java.util.Locale.US)} · " +
                        "${hhmm(row.windowStartMillis, armZone!!)}–${hhmm(row.windowEndMillis, armZone)} " +
                        "(${zoneOffsetLabel(row.windowStartMillis, armZone)})"
                } else {
                    "WITHIN ${name.uppercase(java.util.Locale.US)} · " +
                        "${hhmm(row.windowStartMillis)}–${hhmm(row.windowEndMillis)}"
                }
            } else {
                null
            }
        } else {
            routineWindowLabelAt(row.startWallTimeMillis)
        }

        // Banked break seconds: the spend ledger's held seconds
        // for this session. On every publishable receipt today the
        // honest figure is 0 — a session the valve broke publishes
        // nothing — and the field exists so the day a banked break
        // can lawfully close a session, the slip already carries
        // its price. Named truth, not a placeholder.
        val spends = dao.getSpendsForSession(sessionId)
        val banked = spends.sumOf { it.heldSeconds }

        // Deflections (Zettel): intercept instants inside the
        // session's wall window, intention-matched, BLOCKED
        // outcome only (A4 — peeks never print as blocks),
        // system rows excluded, break-window openings excluded,
        // 30s-per-package dedupe keeping the first. A list,
        // never a sum.
        val windowEnd = if (row.routineId != null && row.windowEndMillis > 0L) {
            maxOf(trueEndedAtWallMs, row.windowEndMillis)
        } else {
            trueEndedAtWallMs
        }
        // Nachdruck R4 (Finisher 3 §4): the SITE distinction is
        // derived here, at assembly — the blocked-site domains
        // read ONCE into a set, and a deflection whose appName
        // exactly matches one is a site reach (the enforcer
        // stamps the matched domain into appName for site
        // intercepts, MonolithAccessibilityService). Exact match
        // against the very list the enforcer matched — hard
        // evidence, not string shape: a stored domain can be a
        // path form (youtube.com/shorts) and an app label can
        // contain a dot, so shape-sniffing would mislabel rows.
        val siteDomains = dao.getAllSitesSnapshot().map { it.domain }.toSet()
        val breakWindows = spends
            .map { it.spentAtWallMillis..(it.spentAtWallMillis + 60_000L) }
        // Issue #129 F3: the exposure booking the ending write
        // left for this session — the unguarded apps actually
        // used in the window, by label. Read back here, and
        // excluded from deflections below: an exposure is not a
        // deflection, and must never inflate that list.
        val openExposureApps = if (row.outcome == SessionOutcome.ELAPSED_OPEN_EXPOSURE) {
            dao.getInterceptLogsBetween(row.startWallTimeMillis, windowEnd)
                .filter { log ->
                    log.packageName == EXPOSURE_LOG_PACKAGE &&
                        log.outcome == EXPOSURE_LOG_OUTCOME &&
                        log.sessionIntention == row.intention
                }
                .map { it.appName }
                .distinct()
        } else {
            emptyList()
        }

        // Issue #105 (F1): the finished tasks are the row's OWN
        // booked summary — completeTaskAndAdvance appends each
        // finished task to completedTasksSummary, and until now
        // assembly never read it, so the slip attested a session
        // while naming none of the work finished inside it.
        val completedTasks = row.completedTasksSummary
            .split(" • ")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        // Issue #105 (F2, assembly side): the deflection match
        // must survive the session's intention ever having been
        // re-titled. The write side is cured on main by #124's
        // total freeze (landed via PR #453): completeTaskAndAdvance
        // and promoteTaskToActive never rewrite a session's
        // intention at all — a superset of the seal guard #106's
        // F2 cure carries, and the two agree (no session write on
        // task finish/promotion). Assembly additionally accepts
        // logs stamped with the title of any task this session
        // booked as finished: those attempts happened under
        // that task's watch, inside this window — so deflections
        // booked under a title a pre-freeze build re-titled away
        // are not silently dropped from the slip either.
        val knownIntentions = buildSet {
            add(row.intention)
            completedTasks.forEach { entry -> add(entry.substringBefore(" (").trim()) }
        }.filter { it.isNotEmpty() }.toSet()

        val lastByPackage = mutableMapOf<String, Long>()
        val deflections = dao.getInterceptLogsBetween(row.startWallTimeMillis, windowEnd)
            .filter { log ->
                // A4 (issue #10, ported by WELLE-6 from
                // e39ae62): the outcome test sits in the FIRST
                // filter — before the dedupe below — so a peek
                // row the filter removes can never eat a real
                // block's dedupe slot. Only proven blocks print:
                // a peek (he was LET IN) is not a deflection.
                log.sessionIntention in knownIntentions &&
                    log.packageName != "com.monolith.system" &&
                    log.packageName != EXPOSURE_LOG_PACKAGE &&
                    isBlockedInterceptOutcome(log.outcome) &&
                    breakWindows.none { log.timestampMillis in it }
            }
            .filter { log ->
                val prev = lastByPackage[log.packageName]
                if (prev == null || log.timestampMillis - prev >= 30_000L) {
                    lastByPackage[log.packageName] = log.timestampMillis
                    true
                } else {
                    false
                }
            }
            .map {
                ReceiptDeflection(
                    it.appName,
                    it.packageName,
                    it.timestampMillis,
                    isSite = it.appName in siteDomains,
                    // Issue #105 (F3): the outcome rides — the
                    // slip prints which kind of block each
                    // admitted attempt was.
                    outcome = it.outcome
                )
            }

        return PendingReceipt(
            sessionId = sessionId,
            kind = kind,
            outcomeWord = receiptOutcomeWord(row, kind),
            title = row.intention,
            armWallMs = row.startWallTimeMillis,
            endedAtWallMs = trueEndedAtWallMs,
            // Truth Car 2 (RC-02): a FLOW session has no plan —
            // its stored target is the stopwatch's 20-minute
            // FLOOR, and running it through the block formula
            // printed PLANNED 20 MIN … 3H 50M for a span that was
            // never planned. FLOW slips carry no planned figure
            // at all (the HELD line's omission law, applied to
            // PLANNED): null, and the sheet omits the segment.
            plannedSeconds = if (kind == FinishKind.FLOW_FINISH) null else row.spanTargetSeconds,
            sealedElapsedSeconds = row.spanElapsedSeconds,
            heldAppsAtSeal = row.heldAppsAtSeal,
            heldSitesAtSeal = row.heldSitesAtSeal,
            routineLabelWithin = routineLabel,
            bankedBreakSeconds = banked,
            shieldWasOff = row.outcome == SessionOutcome.ELAPSED_SHIELD_GAP,
            deflections = deflections,
            penaltySecondsAdded = row.penaltySecondsAdded,
            openExposureApps = openExposureApps,
            // Audit R3b #145: the forged curio's name rides the
            // slip — read off the row's own stamp, never re-drawn.
            forgedArtifactName = row.forgedArtifactId?.let { forgedId ->
                CurioCatalog.artifacts.firstOrNull { it.id == forgedId }?.name
            },
            // W-41: the row's stored split, threaded through.
            heldShutAtSeal = row.heldShutAtSeal,
            heldPeekAtSeal = row.heldPeekAtSeal,
            // Issue #105 (F1/F4): the session's booked work and
            // its lived Safe Breaks, from the row's own ledgers.
            completedTasks = completedTasks,
            safeBreakSeconds = row.breakTakenSeconds
        )
    }

    /** F-Z5's assembly evaluation for a hand-armed row: the line
     *  of an enabled routine whose window contains the arm
     *  instant (today's or yesterday's instance, for windows that
     *  cross midnight). Null when no window contains it. */
    private suspend fun routineWindowLabelAt(armWallMs: Long): String? {
        val routines = dao.getAllRoutinesSnapshot().filter { it.enabled }
        if (routines.isEmpty()) return null
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = armWallMs }
        val minutesOfDay = cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 +
            cal.get(java.util.Calendar.MINUTE)
        val dayBit = (cal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
        val prevDayBit = (dayBit + 6) % 7
        for (routine in routines) {
            val wraps = routine.endMinutes <= routine.startMinutes
            val contains = when {
                wraps ->
                    (routine.daysMask and (1 shl dayBit) != 0 && minutesOfDay >= routine.startMinutes) ||
                        (routine.daysMask and (1 shl prevDayBit) != 0 && minutesOfDay < routine.endMinutes)
                else ->
                    routine.daysMask and (1 shl dayBit) != 0 &&
                        minutesOfDay >= routine.startMinutes && minutesOfDay < routine.endMinutes
            }
            if (contains) {
                return "WITHIN ${routine.name.uppercase(java.util.Locale.US)} · " +
                    "${hhmm(routine.startMinutes)}–${hhmm(routine.endMinutes)}"
            }
        }
        return null
    }

    /** The slip's outcome word: the long-form dialect (F-W1) —
     *  the row's classification in words a man reads, never the
     *  stored token. */
    fun receiptOutcomeWord(row: FocusSessionEntity, kind: String): String = when (kind) {
        FinishKind.ROUTINE_ATTENDED -> "ATTENDED"
        FinishKind.ROUTINE_PARTIAL -> "PARTIAL"
        FinishKind.ROUTINE_SLEEP_STANDDOWN -> "SLEPT THROUGH"
        FinishKind.FLOW_FINISH -> "FLOW ENDED"
        else -> when (row.outcome) {
            SessionOutcome.ELAPSED_SHIELD_GAP -> "ENDED · SHIELD GAP"
            // #129 F3: never "ENDED" plain for an exposed session.
            SessionOutcome.ELAPSED_OPEN_EXPOSURE -> "ENDED · OPEN APP USED"
            else -> "ENDED"
        }
    }

    /**
     * The publish: gate + claim + state, one call. The gate is
     * the terminal predicate re-evaluated on a FRESH row read —
     * a finish the predicate no longer admits (a stale emission,
     * a re-opened row) publishes nothing and returns null. The
     * claim mints or returns the standing №; the returned state
     * is whole. [faulted] is the detention's verdict (Draht): a
     * faulted publish also stamps the row's release-fault record
     * in the same act.
     */
    suspend fun publishReceipt(pending: PendingReceipt, faulted: Boolean): ReceiptState? {
        val row = dao.getSessionById(pending.sessionId) ?: return null
        val isSleep = row.routineId?.let { dao.getRoutineById(it)?.loadoutName } ==
            SmartLoadout.SLEEP.name
        // Audit R1 #40 (publish side): the stand-down was already
        // classified at E0; a wall-clock rollback in the seconds
        // since must not refuse its publish. Judge the stand-down
        // on the floored clock, max(now, windowEnd) — the same
        // floor rehydrateReceipt applies to claimed rows.
        val judgeNow = if (pending.kind == FinishKind.ROUTINE_SLEEP_STANDDOWN) {
            maxOf(System.currentTimeMillis(), row.windowEndMillis)
        } else {
            System.currentTimeMillis()
        }
        val kindNow = finishKindOf(row, isSleep, judgeNow) ?: return null
        if (kindNow != pending.kind) return null
        // The once-gate (Zettel Z1): a session whose receipt
        // already stands publishes nothing twice — the claim's
        // latch alone would hand back the standing № and let a
        // second slip be built over it.
        if (standingReceipt()?.second == pending.sessionId) return null
        // Audit R1 #40: the fault stamp is part of the claim
        // transaction now (see claimReceiptNumber) — no separate
        // post-claim write exists for a crash to strand.
        val number = claimReceiptNumber(
            pending.sessionId, pending.outcomeWord,
            faulted = faulted, faultWallMillis = System.currentTimeMillis()
        )
        return ReceiptState(
            sessionId = pending.sessionId,
            number = number,
            kind = pending.kind,
            outcomeWord = pending.outcomeWord,
            title = pending.title,
            armWallMs = pending.armWallMs,
            endedAtWallMs = pending.endedAtWallMs,
            plannedSeconds = pending.plannedSeconds,
            sealedElapsedSeconds = pending.sealedElapsedSeconds,
            heldAppsAtSeal = pending.heldAppsAtSeal,
            heldSitesAtSeal = pending.heldSitesAtSeal,
            routineLabelWithin = pending.routineLabelWithin,
            bankedBreakSeconds = pending.bankedBreakSeconds,
            shieldWasOff = pending.shieldWasOff,
            faulted = faulted,
            deflections = pending.deflections,
            openExposureApps = pending.openExposureApps,
            restored = false,
            receiptPublishedMonoMs = 0L,
            penaltySecondsAdded = pending.penaltySecondsAdded,
            forgedArtifactName = pending.forgedArtifactName,
            heldShutAtSeal = pending.heldShutAtSeal,
            heldPeekAtSeal = pending.heldPeekAtSeal,
            armZoneId = armZoneStore.zoneFor(pending.armWallMs),
            completedTasks = pending.completedTasks,
            safeBreakSeconds = pending.safeBreakSeconds
        )
    }

    /**
     * Rehydration (Z6 case b): rebuild the standing slip from the
     * stored № + the row, marked restored. Ended-at for routine
     * rows is the row's windowEndMillis exactly (Reihe §3 rule
     * 2 — never the observation time); for manual rows it is the
     * same-boot derivation startWall + (lastTickMono −
     * startMono), named here as the rehydration approximation.
     *
     * Truth Car 2 (RC-01): the derivation below is now only the
     * FALLBACK for rows whose ending write predates the stamp.
     * Where the row carries endWallMillis, assemblePendingReceipt
     * reads the stamp over any derived value — the true E0 wall
     * IS persisted now, and a restored slip prints the witnessed
     * end, not the approximation.
     */
    suspend fun rehydrateReceipt(): ReceiptState? {
        val (number, sessionId, _) = standingReceipt() ?: return null
        return rehydrateReceipt(sessionId, number)
    }

    /** Issue #135 F2's door, called by the ViewModel's
     *  publishReceipt: a session that already claimed its №
     *  rebuilds SETTLED from its row — it never re-claims and
     *  never re-enters the finish controller. The rebuild itself
     *  is #157's [rehydrateReceipt] by session id (which already
     *  returns null for a row that claimed nothing); this name
     *  is the one the #135 call site was written against — the
     *  merge train landed the call without its callee, leaving
     *  main uncompilable (CI run 41057949156); restored here on
     *  the #152 branch so the branch — and main on merge —
     *  compiles again. */
    suspend fun receiptForSession(sessionId: Int): ReceiptState? =
        rehydrateReceipt(sessionId)

    /** Issue #157: re-derive ANY claimed session's slip by its
     *  session id — the Vault receipts index's tap. The № is the
     *  one stamped on the session's own row at claim (RC-04); a
     *  row that claimed nothing re-derives nothing. */
    suspend fun rehydrateReceipt(sessionId: Int): ReceiptState? {
        val row = dao.getSessionById(sessionId) ?: return null
        if (row.receiptNumber <= 0) return null
        return rehydrateReceipt(sessionId, row.receiptNumber)
    }

    /** The Vault's receipts index (issue #157): every claimed
     *  session, newest № first, carrying only what its row
     *  proves. The outcome word is the slip's own dialect,
     *  derived from the row's finish kind exactly as assembly
     *  derives it; a row whose kind no longer classifies (its
     *  receipt was claimed, so one did) falls back to the bare
     *  ended word its outcome column still proves. */
    suspend fun claimedReceipts(): List<ReceiptIndexEntry> {
        return dao.getClaimedReceiptSessions().map { row ->
            val isSleep = row.routineId?.let { dao.getRoutineById(it)?.loadoutName } ==
                SmartLoadout.SLEEP.name
            val kind = finishKindOf(row, isSleep, System.currentTimeMillis())
            ReceiptIndexEntry(
                sessionId = row.id,
                number = row.receiptNumber,
                endedAtWallMs = if (row.endWallMillis > 0L) {
                    row.endWallMillis
                } else {
                    row.startWallTimeMillis
                },
                outcomeWord = if (kind != null) {
                    receiptOutcomeWord(row, kind)
                } else if (row.outcome == SessionOutcome.ELAPSED_SHIELD_GAP) {
                    "ENDED · SHIELD GAP"
                } else {
                    "ENDED"
                },
                releaseFaultShield = row.releaseFaultShield,
                shieldWasOff = row.outcome == SessionOutcome.ELAPSED_SHIELD_GAP
            )
        }
    }

    private suspend fun rehydrateReceipt(sessionId: Int, number: Int): ReceiptState? {
        val row = dao.getSessionById(sessionId) ?: return null
        if (row.isActive) return null
        val isSleep = row.routineId?.let { dao.getRoutineById(it)?.loadoutName } ==
            SmartLoadout.SLEEP.name
        // Audit R1 #40: a CLAIMED receipt (the row wears its №)
        // is durable proof the finish fired — a wall-clock
        // rollback must not un-finish it. For claimed rows the
        // predicate's clock is floored at the row's own window
        // end, so the SLEEP stand-down's `windowEndMillis <= now`
        // gate can never swing shut behind a rollback. Unclaimed
        // rows keep the raw wall clock: nothing is owed them.
        val judgeNow = if (row.receiptNumber > 0) {
            maxOf(System.currentTimeMillis(), row.windowEndMillis)
        } else {
            System.currentTimeMillis()
        }
        val kind = finishKindOf(row, isSleep, judgeNow) ?: return null
        val endedAt = if (row.routineId != null && row.windowEndMillis > 0L) {
            row.windowEndMillis
        } else {
            row.startWallTimeMillis +
                (row.lastTickElapsedRealtimeMillis - row.startElapsedRealtimeMillis)
                    .coerceAtLeast(0L)
        }
        val pending = assemblePendingReceipt(sessionId, kind, endedAt) ?: return null
        return ReceiptState(
            sessionId = sessionId,
            number = number,
            kind = pending.kind,
            outcomeWord = pending.outcomeWord,
            title = pending.title,
            armWallMs = pending.armWallMs,
            endedAtWallMs = pending.endedAtWallMs,
            plannedSeconds = pending.plannedSeconds,
            sealedElapsedSeconds = pending.sealedElapsedSeconds,
            heldAppsAtSeal = pending.heldAppsAtSeal,
            heldSitesAtSeal = pending.heldSitesAtSeal,
            routineLabelWithin = pending.routineLabelWithin,
            bankedBreakSeconds = pending.bankedBreakSeconds,
            shieldWasOff = pending.shieldWasOff,
            faulted = row.releaseFaultShield,
            deflections = pending.deflections,
            openExposureApps = pending.openExposureApps,
            restored = true,
            receiptPublishedMonoMs = 0L,
            penaltySecondsAdded = pending.penaltySecondsAdded,
            forgedArtifactName = pending.forgedArtifactName,
            heldShutAtSeal = pending.heldShutAtSeal,
            heldPeekAtSeal = pending.heldPeekAtSeal,
            armZoneId = armZoneStore.zoneFor(pending.armWallMs),
            completedTasks = pending.completedTasks,
            safeBreakSeconds = pending.safeBreakSeconds
        )
    }

    /** The gap latch's repository door (Nocke §2.2): set the
     *  latch when the active row's beat is stale past the law.
     *  Called from the VM ticker and the relaunch path; the
     *  latch, once set, is never cleared.
     *
     *  Issue #82 (clock-attacks F1): the staleness test runs on the
     *  monotonic-implied now ([ClockHonesty.impliedNowMillis]),
     *  never on the raw wall clock. An 11-second forward jump of
     *  the device clock can no longer forge a gap that never
     *  happened, and a rollback can no longer launder a real one:
     *  monotonic time moves neither way at the user's hand. */
    suspend fun latchShieldGapIfStale(nowElapsedRealtimeMillis: Long): Boolean {
        val active = dao.getActiveSessionSnapshot() ?: return false
        if (active.shieldGapSeen || active.outcome.isNotEmpty()) return false
        // Composed at the #82/#95 merge: #95's adoption + update
        // exemption survive, judged on #82's honest clock. A row
        // whose beat is 0 predates the beat column (pre-v10) —
        // there is no beat evidence to judge from. Adopt it: stamp
        // the beat now (a wall stamp of the current instant, not a
        // verdict) and judge from here.
        if (active.shieldLastBeatWallMillis == 0L) {
            dao.adoptShieldBeat(active.id, System.currentTimeMillis())
            return false
        }
        // The reference never sits before the current build landed
        // (#95: update downtime is not a gap) and never before the
        // session started; the judgement itself runs on the
        // monotonic-implied now (#82), so clock travel forges and
        // launders nothing.
        val beatRef = maxOf(
            active.shieldLastBeatWallMillis,
            active.startWallTimeMillis,
            appLastUpdateMillis()
        )
        val impliedNow = ClockHonesty.impliedNowMillis(
            startWallMillis = active.startWallTimeMillis,
            accumulatedBeforeRebootSeconds = active.accumulatedBeforeRebootSeconds,
            startElapsedRealtimeMillis = active.startElapsedRealtimeMillis,
            lastTickElapsedRealtimeMillis = active.lastTickElapsedRealtimeMillis,
            elapsedSeconds = active.elapsedSeconds,
            nowElapsedRealtimeMillis = nowElapsedRealtimeMillis
        )
        if (impliedNow - beatRef <= SHIELD_GAP_THRESHOLD_MILLIS) return false
        dao.latchShieldGap(active.id)
        return true
    }

    /** Issue #81 F4: latch the gap NOW, without the staleness law.
     *  For blindness observed directly, not inferred from a stale
     *  beat: the shield process is alive and beating, but its
     *  foreground sight (Usage Access) has been revoked mid-session,
     *  so the door is dead while the beat stays fresh and
     *  [latchShieldGapIfStale] can never fire. Same latch, same
     *  guards — an already-latched or already-classified row is
     *  left alone. The finish then classifies ELAPSED_SHIELD_GAP,
     *  never ELAPSED_CLEAN. */
    suspend fun latchShieldGapNow(): Boolean {
        val active = dao.getActiveSessionSnapshot() ?: return false
        if (active.shieldGapSeen || active.outcome.isNotEmpty()) return false
        dao.latchShieldGap(active.id)
        return true
    }
    /** Wall time the installed package last landed on this phone
     *  (the install time on a first install), or 0 when unreadable.
     *  The shield-gap law's update exemption (Audit R2 #95). */
    private fun appLastUpdateMillis(): Long = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
    }.getOrDefault(0L)

    /** ST-03 (Truth Car 3): the break boundaries' voice, fired AT
     *  the flips in this machinery — the block→break entry below
     *  and [advancePastBreak], the one exit both the timer and the
     *  hand cross. The boundary belongs to the machinery that
     *  crosses it (Una's verification brief): a voice hung on a UI
     *  observer would be silent-by-construction whenever the app
     *  is not alive mid-break, and this machine crosses breaks
     *  from the shield's tick as readily as from the screen's.
     *
     *  The voices: release (false) is the engine's latch, fired
     *  as the block lets go into the break; engage (true) — the
     *  break END, the one cue a user plans around — got its own
     *  dedicated voice in issue #150 (a low double strike, not
     *  the toggle latch's single click: the exit cue was being
     *  missed and block time ran while the user believed the
     *  break still held), and #146 later raised that voice to
     *  the full summons this seam now fires. Both are
     *  machinery-fired, so both are spoken as unattended: the
     *  ringer's word and Do Not Disturb bind them in the engine.
     *  The
     *  player is a seam for exactly one reason: the transition
     *  tests count knock REQUESTS at the flip (Pruefer's proof
     *  standard — call level, never an acoustic claim). In
     *  production the seam's default is the engine itself, whose
     *  own soundEnabled gate decides — the same gate the seal
     *  reads, never routed around. The engine is lazy and
     *  repository-held: it is born only in a process that actually
     *  crosses a break, born FROM the persisted sound fact
     *  (UNA-03's initializer reads it at construction) and
     *  registered for the toggle's live pushes, so an engine this
     *  door creates honours the setting exactly as the screen's
     *  engine does. */
    internal var breakBoundaryKnock: (engaged: Boolean) -> Unit = { engaged ->
        // Composition of #150 and #146 (merge of main 017d2d6 into
        // agent/issue-150): the break EXIT is a summons, not a tick —
        // the user is away from the phone by design at that moment,
        // so the exit speaks with #146's full summons voice (which
        // supersedes #150's double-strike cue for this call site;
        // playBreakEndCue survives in the engine). Both boundary
        // voices are machinery-fired, so both are spoken as
        // unattended: the ringer's word and Do Not Disturb bind
        // them in the engine (#150 F1/F2 — the summons and the
        // warning knock pass unattended = true inside the engine).
        // The entry (engaged = false) keeps the latch, unattended.
        // ISSUE-67: both voices live behind the service port — the
        // data layer no longer constructs a sound engine; the live
        // port's lazy engine keeps the born-at-first-crossing
        // semantics verbatim, and the port carries the unattended
        // flag through so #150's binding is preserved exactly.
        if (engaged) {
            servicePort.playBreakEndSummons(context)
        } else {
            servicePort.playBreakLatch(context, false, unattended = true)
        }
    }

    /** T-minus-60 warning seam (Audit R3b #146): fired once when the
     *  Safe Break countdown crosses 60 seconds remaining, so the
     *  walking user feels the deadline coming instead of meeting it
     *  as a fait accompli. Default is the engine's warning knock,
     *  via the ISSUE-67 service port. */
    internal var breakWarningKnock: () -> Unit = {
        servicePort.playBreakWarningKnock(context)
    }

    /** Audit R3b #148: the T-60 block warning — one latch knock,
     *  fired by the countdown branch at the 60-seconds-left
     *  crossing. Its own seam (tests and the summons work on the
     *  #146 branch can re-voice it without touching the boundary
     *  knock's contract). ISSUE-67 REBASE NOTE: the seam is kept
     *  verbatim; its default is routed through the service port
     *  (playBreakLatch engaged=true is the same mechanical latch)
     *  so the Repository holds no sound engine of its own. */
    internal var blockWarningKnock: () -> Unit = {
        servicePort.playBreakLatch(context, true)
    }

    /** The Safe Break length in force for [session]. Today the one
     *  named constant; when Truth Car 3's stamp lands (Rurik —
     *  breakSeconds written on the row at creation, PILL-AND-TIMES
     *  §3.2), this reader re-points at the row's own stamp and the
     *  constant survives only as the default's ancestor. Every
     *  break-length read in the progress machine and the break
     *  verbs goes through here, so the stamp drops in at exactly
     *  one place. */
    private fun breakLengthSeconds(session: FocusSessionEntity): Int =
        SAFE_BREAK_DURATION_SECONDS

    /** The one write that carries a session out of a Safe Break
     *  into its next block: fresh anchors, a fresh signature over
     *  them, the index advanced. The expiry flip in
     *  [updateSessionProgress] and the end-early verb below are the
     *  same transition and share this write, so the two exits can
     *  never drift apart; they differ only in what the taken
     *  ledger books ([breakTakenSeconds] — the full length at
     *  expiry, the wall-derived part at an early end). The shield
     *  beat is carried, never stamped: a beat written by anyone
     *  but the shield would be a forged witness (Slice E). */
    private fun advancePastBreak(
        session: FocusSessionEntity,
        nowMono: Long,
        currentBoot: Int,
        beatWall: Long,
        breakTakenSeconds: Int
    ): FocusSessionEntity {
        val wallNow = System.currentTimeMillis()
        val freshHmac = CryptoIntegrityManager.signSessionState(
            wallNow, nowMono, currentBoot, session.targetDurationSeconds, false
        )
        // ST-03: the exit speaks here, at the flip — the one
        // point every break exit crosses exactly once, so the
        // timer's end and the hand's end sound identically,
        // once, and a countdown tick (which never reaches this
        // function) stays silent.
        breakBoundaryKnock(true)
        return session.copy(
            currentBlockIndex = (session.currentBlockIndex + 1).coerceAtMost(session.totalStudyBlocks),
            isInSafeBreak = false,
            safeBreakSecondsRemaining = 0,
            breakTakenSeconds = breakTakenSeconds,
            // W3 #134: the call ledger is per-block, like the
            // anchors this write re-bases — the next block's
            // verified elapsed counts from the fresh anchor, so
            // the exclusion must restart with it or one block's
            // call would tax the next block's clock.
            callExcludedSeconds = 0,
            elapsedSeconds = 0,
            startWallTimeMillis = wallNow,
            startElapsedRealtimeMillis = nowMono,
            bootCount = currentBoot,
            accumulatedBeforeRebootSeconds = 0,
            lastTickElapsedRealtimeMillis = nowMono,
            hmacSignature = freshHmac,
            shieldLastBeatWallMillis = beatWall
        )
    }

    /** ST-02 (Truth Car 3): end the active session's Safe Break
     *  NOW — the verb the register found missing (a man ready at
     *  minute four waited out six dead minutes; the break ended
     *  only when its timer expired). Performs the identical
     *  transition the expiry flip performs, at the tap, and books
     *  the break's ACTUALLY taken seconds — the length in force
     *  minus the wall-derived remainder at this instant — into
     *  breakTakenSeconds, so the record the session leaves (the
     *  span derivation, the desk clock, the receipt) never claims
     *  the untaken remainder ran.
     *
     *  Reads the stored row fresh and refuses quietly (returns the
     *  row unchanged) when there is nothing to end: no active
     *  session, a different session, a session not in a break. A
     *  routine session's break ends the same way its expiry flip
     *  already ends it — advancing a block declares no success,
     *  so the routines fence is not engaged. */
    suspend fun endSafeBreakEarly(sessionId: Int): FocusSessionEntity? {
        val row = dao.getActiveSessionSnapshot() ?: return null
        if (row.id != sessionId || !row.isActive || !row.isInSafeBreak) return row
        val nowMono = SystemClock.elapsedRealtime()
        val remaining = breakRemainingAfter(
            storedRemaining = row.safeBreakSecondsRemaining,
            lastTickMonoMillis = row.lastTickElapsedRealtimeMillis,
            nowMonoMillis = nowMono
        )
        val taken = (breakLengthSeconds(row) - remaining)
            .coerceIn(0, breakLengthSeconds(row))
        val next = advancePastBreak(
            session = row,
            nowMono = nowMono,
            currentBoot = querySystemBootCountOrNull() ?: row.bootCount,
            beatWall = row.shieldLastBeatWallMillis,
            breakTakenSeconds = row.breakTakenSeconds + taken
        )
        dao.updateSession(next)
        return next
    }

    /**
     * Updates session progress, enforcing:
     * 1. Monotonic clock, reboot recovery & HMAC signature verification (+15m Penalty Lock on tamper)
     * 2. Count-Up Stopwatch Mode (counts upward past the 20m minimum lock floor)
     * 3. Multi-Block Study Cycles (1 to 4 blocks) with automatic 10-minute Safe Breaks in between
     * 4. Call accounting (W3 #134): while the call bypass signal is active, tick seconds are
     *    excluded from the booked elapsed (callExcludedSeconds), no finish or Safe Break flip
     *    fires, and a running Safe Break's countdown holds — call time is never focus time.
     */
    /**
     * Issue #283 (R5 supplement): the in-session extend verb the
     * original ships (+15 min) and Monolith lacked entirely — a
     * running session could only end or be broken, never lengthened.
     * Extends the ACTIVE session's target by [addSeconds]. The
     * target is HMAC-covered (see CryptoIntegrityManager
     * .signSessionState's payload), so the row is re-signed in the
     * same write; an unsigned extension would read as tampering on
     * the next verified tick. Capped the way the original caps it:
     * while an hour or more remains, no further extension is sold
     * (its 'still 60 min' refusal). Stopwatch sessions have no
     * target to extend and refuse.
     */
    suspend fun extendActiveSessionBySeconds(addSeconds: Int = 900): Boolean {
        val session = dao.getActiveSessionSnapshot() ?: return false
        if (!session.isActive || session.isStopwatchMode) return false
        val (verifiedElapsed, _) = computeVerifiedSessionElapsed(session)
        val effectiveTarget = session.targetDurationSeconds + session.penaltySecondsAdded
        if (effectiveTarget - verifiedElapsed >= 3600) return false
        val newTarget = session.targetDurationSeconds + addSeconds
        val reSigned = session.copy(
            targetDurationSeconds = newTarget,
            hmacSignature = CryptoIntegrityManager.signSessionState(
                session.startWallTimeMillis,
                session.startElapsedRealtimeMillis,
                session.bootCount,
                newTarget,
                session.isStopwatchMode
            )
        )
        dao.updateSession(reSigned)
        return true
    }

    suspend fun updateSessionProgress(
        session: FocusSessionEntity,
        stampShieldBeat: Boolean = false
    ): FocusSessionEntity = databaseProvider().withTransaction {
        // Issue #33 (audit R1): two tickers drive this function (the
        // ViewModel loop and the Shield service) and each used to
        // write a whole row computed from its caller's stale
        // snapshot — clobbering phantom-reach counts and evidence
        // stamps written since. The tick now runs in ONE transaction
        // and computes from the row AS IT STANDS in the database;
        // the caller's snapshot only names the session. A row that
        // was disarmed/finalised since the snapshot is returned
        // untouched — a tick never resurrects it.
        val fresh = dao.getSessionById(session.id)
        when {
            fresh == null -> session
            !fresh.isActive -> fresh
            else -> updateSessionProgressLocked(fresh, stampShieldBeat)
        }
    }

    private suspend fun updateSessionProgressLocked(
        session: FocusSessionEntity,
        stampShieldBeat: Boolean
    ): FocusSessionEntity {
        val nowMono = SystemClock.elapsedRealtime()
        val nowWall = System.currentTimeMillis()
        val currentBoot = querySystemBootCountOrNull()
        // Slice E (Nocke §2.2): the shield's own tick stamps its
        // beat into every copy this function persists — the beat
        // is the finish machine's gap evidence. Other callers
        // (the VM ticker) never stamp it: a beat written by
        // anyone but the shield would be a forged witness.
        val beatWall = if (stampShieldBeat) System.currentTimeMillis() else session.shieldLastBeatWallMillis
        val (verifiedElapsed, tamper) = computeVerifiedSessionElapsed(session)
        // Issue #82 (clock-attacks F5): the tamper penalty is no
        // longer a one-way ratchet for CLOCK drift. Session elapsed
        // is monotonic-anchored, so clock travel alone gains the
        // user nothing; a 91-second blip (or an honest manual time
        // correction) must not permanently extend the lock. The
        // drift penalty therefore applies only WHILE the drift
        // persists and unwinds on the first clean tick. Forged
        // state (HMAC failure) is the real conviction: that penalty
        // is set once and never unwinds.
        val hmacValid = CryptoIntegrityManager.verifySessionState(session)
        val penaltyAdd = when {
            !hmacValid ->
                if (session.penaltySecondsAdded == 0) HMAC_TAMPER_PENALTY_SECONDS
                else session.penaltySecondsAdded
            tamper -> HMAC_TAMPER_PENALTY_SECONDS
            else -> 0
        }
        val effectiveTarget = session.targetDurationSeconds + penaltyAdd

        // Re-anchor if device rebooted so subsequent ticks on the new boot stay monotonic.
        // BOTH anchors move to this frame — the seal's own law (sealSession:
        // "Both anchors move to the same frame so the mono/wall cross-check
        // stays consistent"), which this path used to break: it moved the
        // mono anchor and left the wall anchor at the session's start, so
        // every later tick compared wall-since-start against
        // mono-since-reboot, the drift read permanently > 90s, and an
        // honest reboot branded the user a tamperer and drew the 15-minute
        // penalty (Audit R3 #113, finding 4). The re-sign covers the NEW
        // anchor pair, exactly as the seal's re-sign does.
        val rebootDetected = (currentBoot != null && session.bootCount > 0 && currentBoot != session.bootCount) ||
            (session.lastTickElapsedRealtimeMillis > 0L && nowMono < session.lastTickElapsedRealtimeMillis)
        val newAccumulated = if (rebootDetected) verifiedElapsed else session.accumulatedBeforeRebootSeconds
        val newStartMono = if (rebootDetected) nowMono else session.startElapsedRealtimeMillis
        val newStartWall = if (rebootDetected) nowWall else session.startWallTimeMillis
        val newBootCount = if (rebootDetected) currentBoot ?: session.bootCount else session.bootCount
        val newHmac = if (rebootDetected) {
            CryptoIntegrityManager.signSessionState(
                newStartWall,
                newStartMono,
                newBootCount,
                session.targetDurationSeconds,
                session.isStopwatchMode
            )
        } else {
            session.hmacSignature
        }

        // Issue #122 (finding 4): battery death IS an ending. On
        // the reboot branch only — the battery-death signature,
        // where monotonic time cannot account for the downtime —
        // a non-routine timed session whose planned wall end
        // passed while the phone was off ends HERE, truthfully,
        // instead of re-anchoring and re-locking the user for
        // time that ran out hours ago. The ending is booked as
        // ELAPSED_SHIELD_GAP (the span ran out while the shield
        // was gone): no success bit, no forged artifact, no
        // pulse, elapsed stays at its last PROVEN value, and the
        // end stamp is the planned end — not the recharge
        // instant that discovered it. Routine rows are excluded:
        // the heartbeat's window finalise owns their endings.
        if (rebootDetected && session.routineId == null &&
            SessionWallClock.wallDeadlinePassed(session, System.currentTimeMillis())
        ) {
            val plannedEnd = SessionWallClock.plannedEndWallMillis(session)!!
            val ended = session.copy(
                isActive = false,
                completedSuccessfully = false,
                isInSafeBreak = false,
                safeBreakSecondsRemaining = 0,
                startElapsedRealtimeMillis = newStartMono,
                bootCount = newBootCount,
                accumulatedBeforeRebootSeconds = newAccumulated,
                lastTickElapsedRealtimeMillis = nowMono,
                hmacSignature = newHmac,
                penaltySecondsAdded = penaltyAdd,
                tamperDetected = tamper,
                shieldLastBeatWallMillis = beatWall,
                outcome = SessionOutcome.ELAPSED_SHIELD_GAP,
                endWallMillis = if (session.endWallMillis == 0L) plannedEnd else session.endWallMillis
            )
            dao.updateSession(ended)
            return ended
        }

        // W3 #134 (phone-call-mid-session): the call bypass signal
        // the doors already trust, read once per tick. While a
        // call is live, the tick's wall seconds join the block's
        // persisted exclusion ledger and every booking below runs
        // on focusElapsed — call time is not focus time, so it
        // cannot advance the block, complete the session, or be
        // certified ELAPSED_CLEAN. Inside a Safe Break the ledger
        // does not grow (break seconds are not focus seconds and
        // the next block re-bases it anyway); the break branch
        // below holds its countdown instead.
        val callActive = callActiveProbe()
        val callExcluded = if (callActive && !session.isInSafeBreak) {
            callExcludedAfter(
                storedExcludedSeconds = session.callExcludedSeconds,
                lastTickMonoMillis = session.lastTickElapsedRealtimeMillis,
                nowMonoMillis = nowMono
            )
        } else {
            session.callExcludedSeconds
        }
        val focusElapsed = (verifiedElapsed - callExcluded).coerceAtLeast(0)

        accrueActiveTaskSeconds(session, nowMono, accrue = !callActive)

        if (session.isStopwatchMode) {
            // Issue #290 F1: FLOW's own terminal law — at the cap
            // the session ends in this same tick (STOPWATCH_ENDED,
            // completed, end stamped once), instead of copying
            // elapsed into the row and running indefinitely until
            // the user happens to find the valve.
            if (verifiedElapsed >= STOPWATCH_MAX_CAP_SECONDS) {
                val capped = session.copy(
                    elapsedSeconds = STOPWATCH_MAX_CAP_SECONDS,
                    startElapsedRealtimeMillis = newStartMono,
                    bootCount = newBootCount,
                    accumulatedBeforeRebootSeconds = newAccumulated,
                    lastTickElapsedRealtimeMillis = nowMono,
                    hmacSignature = newHmac,
                    penaltySecondsAdded = penaltyAdd,
                    tamperDetected = tamper,
                    shieldLastBeatWallMillis = beatWall,
                    isActive = false,
                    completedSuccessfully = true,
                    outcome = SessionOutcome.STOPWATCH_ENDED,
                    endWallMillis = if (session.endWallMillis == 0L) {
                        System.currentTimeMillis()
                    } else {
                        session.endWallMillis
                    }
                )
                dao.updateSession(capped)
                return capped
            }
            val updated = session.copy(
                elapsedSeconds = focusElapsed,
                callExcludedSeconds = callExcluded,
                startWallTimeMillis = newStartWall,
                startElapsedRealtimeMillis = newStartMono,
                bootCount = newBootCount,
                accumulatedBeforeRebootSeconds = newAccumulated,
                lastTickElapsedRealtimeMillis = nowMono,
                hmacSignature = newHmac,
                penaltySecondsAdded = penaltyAdd,
                tamperDetected = tamper,
                shieldLastBeatWallMillis = beatWall
            )
            dao.updateSession(updated)
            return updated
        }

        // Handle active 10-minute Safe Break countdown between multi-block study cycles.
        // The remaining seconds are WALL-DERIVED from the stored
        // (remaining, lastTick) pair — never decremented per call:
        // two tickers drive this function, and a per-call decrement
        // drained a 600s break in ~400 wall seconds (Bus, 09:30 IST).
        if (session.isInSafeBreak) {
            // W3 #134: a call that straddles the break must not
            // spend it. The countdown is derived from the stored
            // (remaining, lastTick) pair, so holding is one write:
            // the remainder untouched, the tick anchor advanced —
            // the drain resumes from exactly this remainder when
            // the call ends.
            if (callActive) {
                val heldBreak = session.copy(
                    lastTickElapsedRealtimeMillis = nowMono,
                    shieldLastBeatWallMillis = beatWall
                )
                dao.updateSession(heldBreak)
                return heldBreak
            }
            // Audit R1 #41: the tick computes the (remaining,
            // anchor) PAIR and the pair is what persists — the
            // old code stored the truncated remaining beside the
            // FULL current instant, discarding the sub-second
            // remainder every tick (breaks overran by ~30 s).
            val (nextBreakRemaining, nextBreakAnchor) = breakTickAfter(
                storedRemaining = session.safeBreakSecondsRemaining,
                lastTickMonoMillis = session.lastTickElapsedRealtimeMillis,
                nowMonoMillis = nowMono
            )
            // Audit R3b #146: one warning knock as the countdown
            // crosses 60 seconds remaining — the stored remainder
            // above the line, the wall-derived remainder at/below
            // it. Crossing-based, so it fires exactly once.
            if (session.safeBreakSecondsRemaining > 60 && nextBreakRemaining in 1..60) {
                breakWarningKnock()
            }
            return if (nextBreakRemaining == 0) {
                // Expiry: the break ran its full length in force,
                // and the taken ledger books exactly that (ST-02's
                // honesty condition — see breakTakenSeconds).
                val nextBlockSession = advancePastBreak(
                    session = session,
                    nowMono = nowMono,
                    currentBoot = newBootCount,
                    beatWall = beatWall,
                    breakTakenSeconds = session.breakTakenSeconds +
                        breakLengthSeconds(session)
                )
                dao.updateSession(nextBlockSession)
                nextBlockSession
            } else {
                val updatedBreak = session.copy(
                    safeBreakSecondsRemaining = nextBreakRemaining,
                    lastTickElapsedRealtimeMillis = nextBreakAnchor,
                    shieldLastBeatWallMillis = beatWall
                )
                dao.updateSession(updatedBreak)
                updatedBreak
            }
        }

        // Audit R3b #148: the block's last minute announces
        // itself. The flip into the break was the first signal a
        // block was ending — this crossing (the row's own stamped
        // elapsed passing the 60-seconds-left mark, read against
        // this tick's verified elapsed) fires ONE warning knock,
        // once per crossing; a penalty extension that pushes the
        // deadline back out re-arms it, truthfully. The "+2 MIN"
        // extension verb the persona asked for cannot live here:
        // the target is inside the session's HMAC, and mutating it
        // mid-session would invalidate the seal — that piece is
        // priced and parked, not improvised.
        // W3 #134 (composition): the crossing is read on
        // focusElapsed, not the raw verified figure — call time
        // is not focus time, so a call cannot carry the block
        // into (or through) its last minute.
        val remainingNow = effectiveTarget - focusElapsed
        val remainingPrev = effectiveTarget - session.elapsedSeconds
        if (remainingPrev > 60 && remainingNow in 1..60) {
            blockWarningKnock()
        }

        val blockFinished = focusElapsed >= effectiveTarget
        // W3 #134: the target was already met when the call began
        // (focusElapsed crossed it in the cadence before the ring)
        // — neither the Safe Break flip nor the finish may fire
        // underneath the conversation. Book the frozen progress
        // and stay in the block, active: the flip, the seal's
        // release, the completion notification and the end stamp
        // all wait for the first tick after hang-up, when the
        // user can actually see the session change state. The
        // shield loop reads isActive and keeps protecting.
        if (callActive && blockFinished) {
            val heldFinish = session.copy(
                elapsedSeconds = focusElapsed.coerceAtMost(effectiveTarget),
                callExcludedSeconds = callExcluded,
                startElapsedRealtimeMillis = newStartMono,
                bootCount = newBootCount,
                accumulatedBeforeRebootSeconds = newAccumulated,
                lastTickElapsedRealtimeMillis = nowMono,
                hmacSignature = newHmac,
                penaltySecondsAdded = penaltyAdd,
                tamperDetected = tamper,
                shieldLastBeatWallMillis = beatWall
            )
            dao.updateSession(heldFinish)
            return heldFinish
        }
        if (blockFinished && session.currentBlockIndex < session.totalStudyBlocks) {
            val safeBreakSession = session.copy(
                elapsedSeconds = effectiveTarget,
                callExcludedSeconds = callExcluded,
                isInSafeBreak = true,
                safeBreakSecondsRemaining = SAFE_BREAK_DURATION_SECONDS,
                lastTickElapsedRealtimeMillis = nowMono,
                penaltySecondsAdded = penaltyAdd,
                tamperDetected = tamper,
                shieldLastBeatWallMillis = beatWall
            )
            dao.updateSession(safeBreakSession)
            // ST-03: the entry speaks here, at the flip — the
            // latch releases as the block lets go into the break.
            // This branch runs once per entry (the row it writes
            // routes every later tick to the countdown branch),
            // so the voice fires exactly once.
            breakBoundaryKnock(false)
            return safeBreakSession
        }

        val allBlocksCompleted = blockFinished && session.currentBlockIndex >= session.totalStudyBlocks
        // Routine-session fence (Sten §1.6.1): a routine session's own
        // timer may deactivate it, but may NEVER declare success or mint
        // an artifact — booked seconds and outcome come only from the
        // finalise funnel, which runs off evidence, not off the clock.
        val isRoutineSession = session.routineId != null
        val forgedId = if (allBlocksCompleted && !isRoutineSession) {
            drawCurioId(session)
        } else null

        // Slice E (Nocke §2.3): the ending write classifies the
        // outcome, once, from the row's own facts. The gap test
        // reads the INCOMING beat (this tick's own stamp must not
        // launder a dead shield's absence): gap ⇔ the latch was
        // ever set, or the last beat is older than the 10s law
        // (5 missed 2s beats) measured from the later of the beat
        // and the session's start. Routine rows keep '' — their
        // judgement is routineOutcome, booked by the funnel.
        // Issue #129 F3: at the ending write, "clean" must also
        // survive the usage evidence. The exposure test reads the
        // session's own window (UsageStats events) against the
        // catalogue as it stands at the end: an unguarded
        // distraction door used for 30s+ inside the window means
        // this session was NOT clean, whatever the shield's
        // heartbeat says. The exposed names are booked as
        // intercept-log rows under a system package the receipt's
        // deflection filter excludes, so the slip can name them
        // without ever counting them as deflections.
        val exposedAtEnd: List<ExposedApp> = if (allBlocksCompleted && !isRoutineSession) {
            val nowWall = System.currentTimeMillis()
            OpenExposure.exposedApps(
                foregroundMillisByPackage = queryForegroundMillisInWindow(
                    session.startWallTimeMillis, nowWall
                ),
                apps = dao.getAllAppsSnapshot(),
                unreviewedOpenPackages = unreviewedOpenPackages(),
                ownPackage = context.packageName
            )
        } else {
            emptyList()
        }

        val outcomeStamp = classifyElapsedOutcome(
            session = session,
            allBlocksCompleted = allBlocksCompleted,
            isRoutineSession = isRoutineSession,
            verifiedElapsedSeconds = verifiedElapsed.toLong(),
            appLastUpdateMillis = appLastUpdateMillis(),
            usageStatsPermissionHeld = hasUsageStatsPermission(),
            openExposureSeen = exposedAtEnd.isNotEmpty()
        )

        val updated = session.copy(
            elapsedSeconds = focusElapsed.coerceAtMost(effectiveTarget),
            callExcludedSeconds = callExcluded,
            startWallTimeMillis = newStartWall,
            startElapsedRealtimeMillis = newStartMono,
            bootCount = newBootCount,
            accumulatedBeforeRebootSeconds = newAccumulated,
            lastTickElapsedRealtimeMillis = nowMono,
            hmacSignature = newHmac,
            penaltySecondsAdded = penaltyAdd,
            isActive = !allBlocksCompleted,
            // #129 F3: an exposed session elapsed, but it did not
            // succeed — no completion, no forged artifact for time
            // spent in an unguarded app (TELL-THE-TRUTH).
            completedSuccessfully = allBlocksCompleted && !isRoutineSession &&
                outcomeStamp != SessionOutcome.ELAPSED_OPEN_EXPOSURE,
            tamperDetected = tamper,
            forgedArtifactId = if (isRoutineSession ||
                outcomeStamp == SessionOutcome.ELAPSED_OPEN_EXPOSURE
            ) {
                null
            } else {
                forgedId ?: session.forgedArtifactId
            },
            shieldLastBeatWallMillis = beatWall,
            outcome = outcomeStamp,
            // Truth Car 2 (RC-01): the true end, stamped in the
            // same write that classifies the outcome — first
            // stamp wins, like the outcome itself. This tick is
            // the ending write; its wall instant is the witnessed
            // end the receipt will print on every path.
            endWallMillis = if (allBlocksCompleted && session.endWallMillis == 0L) {
                System.currentTimeMillis()
            } else {
                session.endWallMillis
            }
        )
        dao.updateSession(updated)
        // #129 F3: book the exposure names at the stamping write
        // (first stamp only — a re-tick of an ended row must not
        // book them twice). The rows ride the intercept log under
        // a reserved package, the same trick the Partial Stamp
        // uses, so no schema change carries them; the receipt
        // assembly reads them back by that package and NEVER
        // counts them as deflections.
        if (outcomeStamp == SessionOutcome.ELAPSED_OPEN_EXPOSURE && session.outcome.isEmpty()) {
            for (exposed in exposedAtEnd) {
                dao.insertInterceptLog(
                    InterceptLogEntity(
                        packageName = EXPOSURE_LOG_PACKAGE,
                        appName = exposed.appName,
                        sessionIntention = session.intention,
                        outcome = EXPOSURE_LOG_OUTCOME,
                        triggerReason = exposed.packageName
                    )
                )
            }
        }
        return updated
    }

    suspend fun recordIntercept(
        packageName: String,
        appName: String,
        sessionIntention: String,
        outcome: String,
        triggerReason: String = "Habit",
        // R3b #162 F4: an uninstall attempt is not a "phantom reach"
        // for a distraction app. Callers on the UNINSTALL_GUARD path
        // pass false: the intercept is still logged (with its
        // truthful outcome), but neither the per-app reach count nor
        // the session's phantom-reach count is inflated by it.
        countAsReach: Boolean = true
    ) {
        // Issue #33 (audit R1): count + log + phantom-reach increment
        // are ONE transaction — a kill between them used to leave the
        // three permanently disagreeing, and the session row is
        // re-read inside the transaction so the +1 never lands on a
        // stale whole-row copy.
        // W-41/R-09 (BELEG): a granted peek is not a blocked
        // reach. The intercept is still LOGGED (below, always —
        // the row is the evidence the slip's outcome filter
        // reads), but neither counter may call a let-through a
        // stop: the shield's "N reaches blocked" counts only
        // reaches that were not let through.
        val countAsBlocked = countAsReach && outcome != InterceptOutcome.USED_PEEK
        databaseProvider().withTransaction {
            if (countAsBlocked) {
                dao.incrementInterceptCount(packageName)
            }
            dao.insertInterceptLog(
                InterceptLogEntity(
                    packageName = packageName,
                    appName = appName,
                    sessionIntention = sessionIntention,
                    outcome = outcome,
                    triggerReason = triggerReason
                )
            )
            if (countAsBlocked) {
                val active = dao.getActiveSessionSnapshot()
                if (active != null) {
                    dao.updateSession(active.copy(phantomReachesBlocked = active.phantomReachesBlocked + 1))
                }
            }
        }
    }

    suspend fun saveCapturedNotification(
        packageName: String,
        appName: String,
        senderTitle: String,
        messageBody: String
    ) {
        dao.insertCapturedNotification(
            CapturedNotificationEntity(
                packageName = packageName,
                appName = appName,
                senderTitle = senderTitle,
                messageBody = messageBody
            )
        )
    }

    /** One-shot snapshot for the FROZEN digest (see the DAO). */
    suspend fun capturedNotificationsSnapshot(): List<CapturedNotificationEntity> =
        dao.getCapturedNotificationsSnapshot()

    /**
     * THE SEALED GATE for digest acts. While a session is active,
     * the held world can be read but never acted on — so both
     * delete paths refuse here, at the repository, regardless of
     * what any surface asks. A hidden chip was never the
     * requirement; a dead chain is. Returns false (and touches
     * nothing) while sealed; runs and returns true otherwise.
     */
    suspend fun deleteCapturedNotification(id: Int): Boolean {
        if (dao.getActiveSessionSnapshot()?.isActive == true) return false
        dao.deleteCapturedNotification(id)
        return true
    }

    suspend fun clearAllCapturedNotifications(): Boolean {
        if (dao.getActiveSessionSnapshot()?.isActive == true) return false
        dao.clearAllCapturedNotifications()
        return true
    }

    /** Issue #83: erase the intercept behaviour log — the delete
     *  path intercept_logs never had. Called by the digest clear
     *  so a user erasure covers the log as well as the digests. */
    suspend fun clearInterceptLogs() {
        dao.deleteAllInterceptLogs()
    }

    // =========================================================================
    // WEEKLY EMERGENCY BREAK — THE TOKEN (TOKEN-SPEC §§2–5)
    // The token is a fact derived from the append-only spend ledger,
    // never a stored counter: remaining = 1 − spends booked against the
    // current week anchor (MonolithWeek). Enforcement lives HERE — the
    // repository refuses a break at zero regardless of what any UI
    // asks; every surface's number is a rendering of this truth.
    // =========================================================================

    /** Remaining breaks for the week containing [nowMillis]. */
    suspend fun emergencyBreaksRemaining(nowMillis: Long = System.currentTimeMillis()): Int {
        val anchor = MonolithWeek.weekAnchorMillis(nowMillis)
        return emergencyBreaksRemainingFromSpendCount(dao.getSpendsForWeek(anchor).size)
    }

    /**
     * The anchor of the current week, re-emitted when it changes.
     * Amendment C (TOKEN-SPEC §5): the flow must emit on a week-anchor
     * change WHILE COLLECTED — an overlay left open across Monday 00:00
     * must never render last week's count. The one-minute re-check also
     * picks up timezone changes on the same tick.
     */
    private fun weekAnchorTickFlow(): Flow<Long> = flow {
        var lastAnchor: Long? = null
        while (true) {
            // Issue #82 (clock-attacks F3): the displayed week is
            // the honest week — date travel must not refresh the
            // token on any surface either.
            val anchor = MonolithWeek.weekAnchorMillis(honestNowMillis())
            if (anchor != lastAnchor) {
                lastAnchor = anchor
                emit(anchor)
            }
            delay(60_000L)
        }
    }

    /** Live remaining-count: re-anchors on collection and on rollover. */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeEmergencyBreaksRemaining(): Flow<Int> =
        weekAnchorTickFlow()
            .flatMapLatest { anchor ->
                dao.observeSpendsForWeek(anchor).map { spends ->
                    emergencyBreaksRemainingFromSpendCount(spends.size)
                }
            }
            .distinctUntilChanged()

    /**
     * The ONLY write of a spend, and the only lawful break-class disarm
     * (TOKEN-SPEC §4.1). One transaction: check the week's spends,
     * insert the spend row, disarm — dispatched on the session's kind.
     * Atomicity is the point: no interleaving where the session ends
     * but the spend is lost, the spend lands but the session stays
     * sealed, or two rapid completion callbacks spend twice (the check
     * runs inside the transaction). A hold that completes across the
     * Monday boundary spends against the COMPLETION instant's week
     * (§4.2). Banks, slips and abandoned attempts never reach this
     * function and write nothing (§4.3); the grace-cancel path never
     * passes through here (§4.4).
     */
    suspend fun completeEmergencyBreak(): BreakResult {
        val database = db() // ISSUE-67: injected database, no re-fetch
        return database.withTransaction {
            val active = dao.getActiveSessionSnapshot()
                ?: return@withTransaction BreakResult.NoActiveSession
            // Issue #82 (clock-attacks F3): the week's anchor is
            // computed from the HONEST now, never the raw wall
            // clock — travelling the date into any other week used
            // to mint a fresh, unspent token; monotonic time does
            // not travel, so the anchor stays in the real week.
            val now = honestNowMillis()
            val anchor = MonolithWeek.weekAnchorMillis(now)
            if (dao.getSpendsForWeek(anchor).size >= 1) {
                return@withTransaction BreakResult.RefusedNoToken
            }
            dao.insertEmergencyBreakSpend(
                EmergencyBreakSpendEntity(
                    sessionId = active.id,
                    spentAtWallMillis = now,
                    weekAnchorMillis = anchor,
                    heldSeconds = EMERGENCY_BREAK_HELD_SECONDS
                )
            )
            if (active.routineId != null || active.profileName.startsWith(ROUTINE_PROFILE_PREFIX)) {
                disarmRoutineSessionByBreak(active)
            } else {
                disarmActiveSession(completed = false, outcomeReason = EMERGENCY_BREAK_REASON)
            }
            BreakResult.Completed
        }
    }

    /**
     * Routine branch of the break dispatch — inside the break
     * transaction, so the token spend and this booking commit or roll
     * back together (TOKEN-SPEC §8 item 9 / B3).
     *
     * Three shapes:
     * - Legacy row (routineId null, recognised by the profile prefix —
     *   coordinator arbitration A3): the interim body stands; it books
     *   tick-accrued elapsed only, never the generic path's wall-clock
     *   max, and writes no Partial Stamp.
     * - SLEEP routine (J2): never judged — the generic disarm runs, in
     *   this transaction, per Sten §4.1.
     * - Any other routine session: the real §5.4 finalise funnel —
     *   [finaliseRoutineSession] books evidence-derived seconds only.
     *   No Partial Stamp is written for a routine session, ever.
     */
    private suspend fun disarmRoutineSessionByBreak(active: FocusSessionEntity) {
        if (active.routineId == null) {
            val (_, tamper) = computeVerifiedSessionElapsed(active)
            // Issue #287 F5: the routine-break door stamps the
            // culprit too — same ending-write law as the manual door.
            val (causePackage, causeKind) = failureCauseFor(active, "EMERGENCY_VALVE")
            dao.updateSession(
                active.copy(
                    elapsedSeconds = active.elapsedSeconds,
                    isActive = false,
                    completedSuccessfully = false,
                    tamperDetected = tamper,
                    outcome = SessionOutcome.VALVE_BROKE,
                    failureCausePackage = causePackage,
                    failureCauseKind = causeKind,
                    // RC-01: an ending write (unpublishable, but
                    // the row's end is the row's end).
                    endWallMillis = if (active.endWallMillis == 0L) {
                        System.currentTimeMillis()
                    } else {
                        active.endWallMillis
                    }
                )
            )
            return
        }
        val routine = dao.getRoutineById(active.routineId)
        if (routine?.loadoutName == SmartLoadout.SLEEP.name) {
            disarmActiveSession(completed = false, outcomeReason = EMERGENCY_BREAK_REASON)
            return
        }
        finaliseRoutineSession(active, System.currentTimeMillis())
    }

    /**
     * Issue #287 F5: the failure-cause pair for a session that is
     * ending broken. The package is the LAST deflection this
     * session's record holds inside its own window — the blocked
     * app he was reaching for when the end came (Forest's
     * 'Your tree withers due to this app: %1$s', as a stored fact).
     * The system pseudo-package the disarm doors log for partial
     * stamps is never a culprit. No deflection on record → '' —
     * the row then says only the kind, never an invented app.
     */
    private suspend fun failureCauseFor(
        active: FocusSessionEntity,
        kind: String
    ): Pair<String, String> {
        val now = System.currentTimeMillis()
        val culprit = dao.getInterceptLogsBetween(active.startWallTimeMillis, now)
            .filter {
                it.sessionIntention == active.intention &&
                    it.packageName != "com.monolith.system"
            }
            .lastOrNull()
        return (culprit?.packageName ?: "") to kind
    }

    /**
     * Issue #287 F7: add time to the ACTIVE session — the session
     * verb Forest's extension timer provides and Monolith lacked
     * (the only extend verb was the task's overtime). The new
     * target is re-signed in the same write: the HMAC covers
     * targetDurationSeconds, so an unsigned extension would read
     * as tamper on the next tick. FLOW sessions refuse (their
     * stored target is a floor, not a plan — extending FLOW is
     * meaningless). Returns the updated row, or null when no
     * session is active.
     */
    suspend fun extendActiveSession(extraMinutes: Int): FocusSessionEntity? =
        withContext(Dispatchers.IO) {
            val active = dao.getActiveSessionSnapshot() ?: return@withContext null
            if (extraMinutes <= 0 || active.isStopwatchMode) return@withContext active
            val newTarget = active.targetDurationSeconds + extraMinutes * 60
            val hmac = CryptoIntegrityManager.signSessionState(
                startWallTimeMillis = active.startWallTimeMillis,
                startElapsedRealtimeMillis = active.startElapsedRealtimeMillis,
                bootCount = active.bootCount,
                targetDurationSeconds = newTarget,
                isStopwatch = active.isStopwatchMode
            )
            val updated = active.copy(
                targetDurationSeconds = newTarget,
                hmacSignature = hmac
            )
            dao.updateSession(updated)
            updated
        }

    /**
     * Issue #287 F8: store the user's one reflection line against
     * a finished session (Forest's Reflect, as a stored fact on
     * the row). Refuses active rows — a reflection belongs to a
     * session that has ended. Blank input clears nothing and
     * writes nothing.
     */
    suspend fun setSessionReflection(sessionId: Int, note: String): Boolean =
        withContext(Dispatchers.IO) {
            val trimmed = note.trim()
            if (trimmed.isEmpty()) return@withContext false
            val row = dao.getSessionById(sessionId) ?: return@withContext false
            if (row.isActive) return@withContext false
            dao.updateSession(row.copy(reflectionNote = trimmed))
            true
        }

    suspend fun disarmActiveSession(completed: Boolean, outcomeReason: String) {
        val active = dao.getActiveSessionSnapshot() ?: return
        // Routine sessions never take the generic path (Sten §4.1):
        // non-SLEEP routine rows are booked only by the finalise funnel
        // (this also seals a session whose routine row was deleted);
        // SLEEP rows fall through — never judged (J2).
        if (active.routineId != null) {
            val routine = dao.getRoutineById(active.routineId)
            if (routine?.loadoutName != SmartLoadout.SLEEP.name) {
                finaliseRoutineSession(active, System.currentTimeMillis())
                return
            }
        }
        val (verifiedElapsed, tamper) = computeVerifiedSessionElapsed(active)
        // Audit R3b #149: the 10-second law, enforced where the
        // booking happens — not only by one chip's visibility. The
        // grace classification requires the anchor-derived elapsed
        // (the same max the bank uses) to be inside the window; a
        // "Grace Cancel" reason arriving late books as an early
        // end instead of laundering itself through the string.
        val lawElapsed = max(verifiedElapsed, active.elapsedSeconds)
        val isGraceCancel = outcomeReason.contains("Grace Cancel", ignoreCase = true) &&
            lawElapsed <= 10
        // W3 #134: the disarm doors bank the same focus-only
        // elapsed the progress machine books — the raw verified
        // figure still contains any call minutes, and banking it
        // here would re-certify them at the ending write. (The
        // row's own elapsedSeconds is already focus-booked by the
        // progress machine, so the max stays focus-only.)
        val focusElapsed = (verifiedElapsed - active.callExcludedSeconds).coerceAtLeast(0)
        val bankedElapsed = if (isGraceCancel) 0 else max(focusElapsed, active.elapsedSeconds)
        val partialStampId = if (!completed && !isGraceCancel && bankedElapsed >= 60) {
            // Issue #110, finding 5: the id is the named constant
            // whose catalog entry (PARTIAL_STAMP_ARTIFACT) the
            // cabinet carries — the stamp matches a real row now.
            PARTIAL_STAMP_ID
        } else {
            active.forgedArtifactId
        }
        // Audit R3b #145: the FLOW door is a finish like any other —
        // a hand-completed stopwatch span forges, exactly as the
        // cabinet's printed rule promises. (The progress machine
        // early-returns stopwatch rows, so this door is the only
        // ending write a FLOW finish ever crosses.) Routine-born
        // rows stay fenced: no forge from any hand door while the
        // routines rethink owns their judgement.
        val flowForgedId = if (completed && active.isStopwatchMode &&
            active.routineId == null && active.forgedArtifactId == null
        ) {
            drawCurioId(active)
        } else {
            null
        }
        // Slice E (Nocke §2.3): the disarm doors classify the
        // outcome at this same ending write. The valve's string
        // is the constant, never free text; FLOW's past-floor
        // door is the only completed stopwatch ending.
        val outcomeStamp = when {
            outcomeReason == EMERGENCY_BREAK_REASON -> SessionOutcome.VALVE_BROKE
            completed && active.isStopwatchMode -> SessionOutcome.STOPWATCH_ENDED
            isGraceCancel -> SessionOutcome.GRACE_CANCEL
            // Issue #290 F1: a FLOW session stopped under its floor
            // books its own withered outcome, not generic early end.
            !completed && active.isStopwatchMode &&
                lawElapsed < STOPWATCH_MIN_FLOOR_SECONDS -> SessionOutcome.STOPWATCH_WITHERED
            else -> SessionOutcome.ENDED_EARLY
        }
        // Issue #287 F5: a broken session names its culprit in its
        // own record, stamped at this same ending write.
        val (causePackage, causeKind) = when (outcomeStamp) {
            SessionOutcome.VALVE_BROKE ->
                failureCauseFor(active, "EMERGENCY_VALVE")
            SessionOutcome.ENDED_EARLY ->
                failureCauseFor(active, "MANUAL_DISARM")
            else -> "" to ""
        }
        // Issue #33 (audit R1): the ending write and its intercept
        // log commit together or not at all — a kill between them
        // used to leave a disarmed session with no log, or a log
        // for a session still standing.
        val disarmed = active.copy(
            elapsedSeconds = bankedElapsed,
            isActive = false,
            completedSuccessfully = completed,
            tamperDetected = tamper,
            // Audit R3b #145: a hand-completed FLOW span forges at
            // this door (flowForgedId, computed above); every other
            // door keeps the partial-stamp rule untouched.
            forgedArtifactId = flowForgedId ?: partialStampId,
            outcome = outcomeStamp,
            failureCausePackage = causePackage,
            failureCauseKind = causeKind,
            // RC-01: the disarm doors are ending writes too —
            // the FLOW door's stamp is what its slip's ENDED
            // reads (first stamp wins).
            endWallMillis = if (active.endWallMillis == 0L) {
                System.currentTimeMillis()
            } else {
                active.endWallMillis
            }
        )
        databaseProvider().withTransaction {
            dao.updateSession(disarmed)
            if (!completed && !isGraceCancel) {
                dao.insertInterceptLog(
                    InterceptLogEntity(
                        packageName = "com.monolith.system",
                        appName = if (bankedElapsed >= 60) "Partial Stamp (${bankedElapsed / 60}m)" else "Unlatched Early",
                        sessionIntention = active.intention,
                        outcome = outcomeReason
                    )
                )
            }
        }
    }

    /**
     * Computes 7-Day Focus History broken down into daily minutes and 25-minute Focus Blocks.
     */
    suspend fun compute7DayHistory(): List<DailyFocusStat> = withContext(Dispatchers.IO) {
        // D7/A3 exclusions: an unfinalised routine row is worth zero in
        // every total, and a legacy pre-rebuild routine row (snapshot
        // window 0) never enters history at all.
        val sessions = dao.getSessionHistorySnapshot().filter { s ->
            when {
                s.routineId != null && s.routineOutcome == null -> false
                s.routineId == null && s.windowStartMillis == 0L &&
                    s.profileName.startsWith(ROUTINE_PROFILE_PREFIX) -> false
                else -> true
            }
        }
        val dayFmt = SimpleDateFormat("EEE", Locale.getDefault())
        val isoFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val result = mutableListOf<DailyFocusStat>()

        for (daysAgo in 6 downTo 0) {
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -daysAgo)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = cal.timeInMillis
            val endOfDay = startOfDay + 86_400_000L
            val daySessions = sessions.filter { it.startWallTimeMillis in startOfDay until endOfDay }
            val totalSecs = daySessions.sumOf { it.elapsedSeconds }
            val totalMins = totalSecs / 60
            val blocks25 = FocusDomain.blocks25For(totalMins) // ISSUE-67: domain derivation
            val intercepts = daySessions.sumOf { it.phantomReachesBlocked }

            result.add(
                DailyFocusStat(
                    dayLabel = dayFmt.format(Date(startOfDay)).uppercase(Locale.getDefault()),
                    dateIso = isoFmt.format(Date(startOfDay)),
                    totalMinutes = totalMins,
                    blocks25mCount = blocks25,
                    interceptionsCount = intercepts
                )
            )
        }
        result
    }

    // =========================================================================
    // TIMEBOXED TASK QUEUE (SOLAR QUEUE) OPERATIONS
    // =========================================================================

    // Audit R1 #41: the accrual is the process-wide shared
    // instance. A per-repository instance here silently undid
    // WallAccrual's dedup — MainActivity, the Shield service,
    // the routine heartbeat and the seal transition each build
    // their own repository, and every instance credited the
    // full delta independently.
    private val taskAccrual = WallAccrual.shared

    // The shared anchor, persisted (Audit R1 #41): the anchor
    // used to be volatile and consumed BEFORE the credit was
    // durably written — a process death between observe() and
    // the task write lost the interval twice over (anchor gone,
    // credit unwritten). The anchor now lives on disk too:
    // seeded into the shared instance before each observe,
    // rewritten after it. Same-boot only: the stored boot count
    // and a monotonic sanity check reject anchors from a
    // previous boot, whose elapsedRealtime base differs.
    private val accrualPrefs by lazy {
        context.getSharedPreferences(ACCRUAL_PREFS_NAME, android.content.Context.MODE_PRIVATE)
    }

    private fun seedAccrualFromDisk(nowMono: Long) {
        if (!accrualPrefs.contains(ACCRUAL_KEY_MONO)) return
        val boot = accrualPrefs.getInt(ACCRUAL_KEY_BOOT, -1)
        val mono = accrualPrefs.getLong(ACCRUAL_KEY_MONO, 0L)
        if (boot != querySystemBootCount() || mono > nowMono) return
        taskAccrual.seed(
            sessionId = accrualPrefs.getInt(ACCRUAL_KEY_SESSION, -1),
            taskId = accrualPrefs.getInt(ACCRUAL_KEY_TASK, -1),
            lastMonoMillis = mono
        )
    }

    private fun persistAccrualAnchor() {
        val (sessionId, taskId, mono) = taskAccrual.anchorSnapshot() ?: return
        accrualPrefs.edit()
            .putInt(ACCRUAL_KEY_SESSION, sessionId)
            .putInt(ACCRUAL_KEY_TASK, taskId)
            .putLong(ACCRUAL_KEY_MONO, mono)
            .putInt(ACCRUAL_KEY_BOOT, querySystemBootCount())
            .apply()
    }

    /**
     * Credits the active task with WALL time, never with tick calls.
     * The old increment added +1 per call of updateSessionProgress —
     * and two tickers call it (the ViewModel loop and the Shield
     * service), so stored "actual" seconds were a call count:
     * ≈90 credited per 60 wall seconds (Hale, LR2-S2-24). Now both
     * tickers observe monotonic deltas through [WallAccrual]; any
     * number of callers sums to the time that actually passed.
     * During a Safe Break the anchor advances without crediting —
     * break minutes never land in a task's stored seconds.
     */
    private suspend fun accrueActiveTaskSeconds(
        session: FocusSessionEntity,
        nowMono: Long,
        accrue: Boolean = true
    ) {
        seedAccrualFromDisk(nowMono)
        val tasks = dao.getAllTasksSnapshot()
        val activeTask = tasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
            ?: tasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.QUEUED }
        // W3 #134: a live call pauses the task's clock exactly as
        // a Safe Break does — the anchor advances without
        // crediting, so call minutes never land in a task's
        // stored seconds either.
        val credit = taskAccrual.observe(
            sessionId = session.id,
            accruingTaskId = if (!accrue || session.isInSafeBreak) null else activeTask?.id,
            nowMonoMillis = nowMono
        )
        persistAccrualAnchor()
        if (activeTask != null && accrue && !session.isInSafeBreak &&
            (credit > 0 || activeTask.resolvedStatus != TaskQueueStatus.ACTIVE)
        ) {
            dao.updateTask(
                activeTask.copy(
                    status = TaskQueueStatus.ACTIVE.name,
                    actualSecondsSpent = activeTask.actualSecondsSpent + credit
                )
            )
        }
    }

    suspend fun addTaskToQueue(
        title: String,
        // Issue #32 F1: default 0 = no estimate given (was 25, which
        // fabricated an estimate for every caller that omitted it).
        estimatedMinutes: Int = 0,
        categoryTag: String = "Study",
        notesOrSubsteps: String = "",
        promoteToActive: Boolean = false
    ): FocusTaskEntity = withContext(Dispatchers.IO) {
        val cleanTitle = title.trim().ifEmpty { "Focused Work Block" }
        // MINUTES DIE (Truth Car 2): 0 is a lawful value now —
        // it means no estimate was given, and it is stored as 0
        // so no surface can print an estimate the man never made.
        // A positive estimate (only pre-order rows and the stored
        // history carry one) keeps the historical clamp.
        val clampedMins = if (estimatedMinutes <= 0) 0 else estimatedMinutes.coerceIn(5, 180)
        val existing = dao.getAllTasksSnapshot()
        val hasActive = existing.any { it.resolvedStatus == TaskQueueStatus.ACTIVE }

        val shouldBeActive = promoteToActive || !hasActive
        if (shouldBeActive && hasActive) {
            existing.filter { it.resolvedStatus == TaskQueueStatus.ACTIVE }.forEach { prevActive ->
                dao.updateTask(
                    prevActive.copy(
                        status = TaskQueueStatus.QUEUED.name,
                        queueOrder = 0
                    )
                )
            }
        }

        val nextOrder = if (shouldBeActive) {
            0
        } else {
            (existing.filter { it.resolvedStatus == TaskQueueStatus.QUEUED }
                .maxOfOrNull { it.queueOrder } ?: 0) + 1
        }

        val newTask = FocusTaskEntity(
            title = cleanTitle,
            notesOrSubsteps = notesOrSubsteps.trim(),
            estimatedMinutes = clampedMins,
            actualSecondsSpent = 0,
            categoryTag = categoryTag.ifBlank { "Study" },
            queueOrder = nextOrder,
            status = if (shouldBeActive) TaskQueueStatus.ACTIVE.name else TaskQueueStatus.QUEUED.name
        )
        val id = dao.insertTask(newTask).toInt()
        newTask.copy(id = id)
    }

    suspend fun updateTaskDetails(
        taskId: Int,
        newTitle: String,
        newEstimatedMinutes: Int,
        newCategoryTag: String,
        newNotes: String
    ): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val existing = dao.getAllTasksSnapshot().firstOrNull { it.id == taskId } ?: return@withContext null
        val updated = existing.copy(
            title = newTitle.trim().ifEmpty { existing.title },
            estimatedMinutes = newEstimatedMinutes.coerceIn(5, 180),
            categoryTag = newCategoryTag.ifBlank { existing.categoryTag },
            notesOrSubsteps = newNotes.trim()
        )
        dao.updateTask(updated)
        updated
    }

    suspend fun extendTaskEstimate(taskId: Int, extraMinutes: Int = 5): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val existing = dao.getAllTasksSnapshot().firstOrNull { it.id == taskId } ?: return@withContext null
        val updated = existing.copy(
            estimatedMinutes = (existing.estimatedMinutes + extraMinutes).coerceIn(5, 240)
        )
        dao.updateTask(updated)
        updated
    }

    /**
     * Marks the target task as COMPLETED (or toggles back to QUEUED if already COMPLETED),
     * records its completion in the active FocusSessionEntity receipt summary if a session is running,
     * and automatically promotes the next QUEUED task to ACTIVE.
     */
    suspend fun completeTaskAndAdvance(taskId: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        val target = allTasks.firstOrNull { it.id == taskId } ?: return@withContext null

        if (target.resolvedStatus == TaskQueueStatus.COMPLETED) {
            // Completion is a LATCH, never a toggle (Lie Register 3
            // MO-1/MO-2): this path once un-completed a finished
            // task without retracting the session summary or count,
            // so a double fire resurrected the task under a record
            // that still counted it — and a re-tear booked it twice.
            // Completing an already-completed task writes nothing
            // and returns the standing active task. Undoing a
            // mistaken finish is a separate, explicit verb (the
            // Last-Finished REOPEN in the Drawer build), never a
            // side effect of completing.
            return@withContext allTasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        }

        var currentSession = dao.getActiveSessionSnapshot()
        val nowWall = System.currentTimeMillis()
        val completedTask = target.copy(
            status = TaskQueueStatus.COMPLETED.name,
            completedAtMillis = nowWall,
            completedInSessionId = currentSession?.id ?: 0
        )
        dao.updateTask(completedTask)

        // Append to active session's receipt summary
        if (currentSession != null) {
            val entry = completedTaskSummaryEntry(target)
            val updatedSummary = if (currentSession.completedTasksSummary.isBlank()) {
                entry
            } else {
                "${currentSession.completedTasksSummary} • $entry"
            }
            currentSession = currentSession.copy(
                completedTasksSummary = updatedSummary,
                tasksCompletedCount = currentSession.tasksCompletedCount + 1
            )
            dao.updateSession(currentSession)
        }

        // Promote next queued task if the completed task was ACTIVE (or if no ACTIVE task remains)
        val remaining = dao.getAllTasksSnapshot()
        val existingActive = remaining.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        if (existingActive != null) {
            return@withContext existingActive
        }

        val nextUp = remaining
            .filter { it.resolvedStatus == TaskQueueStatus.QUEUED }
            .minByOrNull { it.queueOrder }
            ?: remaining.filter { it.resolvedStatus == TaskQueueStatus.DEFERRED }.minByOrNull { it.queueOrder }

        if (nextUp != null) {
            val promoted = nextUp.copy(status = TaskQueueStatus.ACTIVE.name, queueOrder = 0)
            dao.updateTask(promoted)
            // Issue #124 (typing-intention) + ESR-02: the promotion
            // is queue bookkeeping ONLY. The running session's
            // intention was frozen at arm time and is never
            // rewritten here — the old copy(intention =
            // promoted.title) silently changed what the receipt and
            // later airlock lines claimed the session was armed
            // with, and it ran with NO seal check at all (ESR-02's
            // exact charge against this function).
            return@withContext promoted
        }
        null
    }

    /**
     * The session-summary entry for one completed task — written
     * by [completeTaskAndAdvance] into the session's receipt
     * summary and reconstructed EXACTLY by [reopenStoredTask]'s
     * repair, which strips by string match. One formatter for
     * both sites, so the pair can never drift apart. MINUTES DIE
     * (Truth Car 2): a task that carries no estimate (0) writes
     * no estimate segment — "(0m est …)" would book a figure the
     * man never gave.
     */
    private fun completedTaskSummaryEntry(task: FocusTaskEntity): String =
        // ISSUE-67: the one formatter lives in the domain slice —
        // the reopen repair strips by this exact string, so booking
        // and repair must share ONE implementation (FocusDomain).
        FocusDomain.completedTaskSummaryEntry(task.title, task.estimatedMinutes, task.actualSecondsSpent)

    /**
     * REOPEN — the Last-Finished Line's own verb (Register 3,
     * MO-1/MO-2). completeTaskAndAdvance is COMPLETION-ONLY — its
     * old un-complete toggle branch is deleted ground — so
     * un-completing lives here, explicitly:
     *
     * A COMPLETED task returns to the queue (ACTIVE if none is
     * active), its completion stamp and completing-session id are
     * cleared, and the RECEIPT REPAIR rides: if the completion was
     * booked into the still-active session's receipt, the exact
     * entry it wrote is stripped (last occurrence) and the count
     * decremented — a mistaken DONE, once undone, leaves no false
     * entry on the finish receipt, and an honest later re-finish
     * books exactly one entry. The entry string is reproducible
     * exactly because reopening never changes actualSecondsSpent.
     * A task that is not COMPLETED is left untouched. Returns the
     * task that should be ACTIVE afterwards, mirroring
     * completeTaskAndAdvance's contract with the ViewModel.
     */
    suspend fun reopenStoredTask(taskId: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        val target = allTasks.firstOrNull { it.id == taskId } ?: return@withContext null
        if (target.resolvedStatus != TaskQueueStatus.COMPLETED) {
            return@withContext allTasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        }

        val hasActive = allTasks.any { it.resolvedStatus == TaskQueueStatus.ACTIVE && it.id != taskId }
        val restoredStatus = if (hasActive) TaskQueueStatus.QUEUED else TaskQueueStatus.ACTIVE
        val restored = target.copy(
            status = restoredStatus.name,
            completedAtMillis = 0L,
            completedInSessionId = 0
        )
        dao.updateTask(restored)

        val session = dao.getActiveSessionSnapshot()
        if (session != null && target.completedInSessionId == session.id &&
            session.completedTasksSummary.isNotBlank()
        ) {
            val entry = completedTaskSummaryEntry(target)
            val entries = session.completedTasksSummary.split(" • ").toMutableList()
            val idx = entries.lastIndexOf(entry)
            if (idx >= 0) {
                entries.removeAt(idx)
                dao.updateSession(
                    session.copy(
                        completedTasksSummary = entries.joinToString(" • "),
                        tasksCompletedCount = (session.tasksCompletedCount - 1).coerceAtLeast(0)
                    )
                )
            }
        }

        if (restoredStatus == TaskQueueStatus.ACTIVE) restored
        else allTasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
    }

    suspend fun promoteTaskToActive(taskId: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        val target = allTasks.firstOrNull { it.id == taskId } ?: return@withContext null
        if (target.resolvedStatus == TaskQueueStatus.ACTIVE) return@withContext target

        allTasks.filter { it.resolvedStatus == TaskQueueStatus.ACTIVE }.forEachIndexed { idx, prev ->
            dao.updateTask(
                prev.copy(
                    status = TaskQueueStatus.QUEUED.name,
                    queueOrder = idx + 1
                )
            )
        }

        val promoted = target.copy(
            status = TaskQueueStatus.ACTIVE.name,
            queueOrder = 0
        )
        dao.updateTask(promoted)

        // Issue #124 (typing-intention): NO session write happens
        // here at all. ESR-02 refused the intention rewrite only
        // for the SEALED row; the freeze is now total — a session's
        // intention is what the user declared at arm time, for the
        // session's whole life, sealed or not, because it is the
        // receipt's title source and the airlock's quoted words.
        // The promotion above is the Tasks surface's own
        // bookkeeping and proceeds on its own.
        promoted
    }

    suspend fun moveTaskOrder(taskId: Int, moveUp: Boolean) = withContext(Dispatchers.IO) {
        val queued = dao.getAllTasksSnapshot()
            .filter { it.resolvedStatus == TaskQueueStatus.QUEUED }
            .sortedWith(compareBy<FocusTaskEntity> { it.queueOrder }.thenBy { it.id })
            .toMutableList()
        val index = queued.indexOfFirst { it.id == taskId }
        if (index == -1) return@withContext
        val targetIndex = if (moveUp) index - 1 else index + 1
        if (targetIndex < 0) {
            // Moving up from the very top of QUEUED promotes it to ACTIVE!
            promoteTaskToActive(taskId)
            return@withContext
        }
        if (targetIndex >= queued.size) return@withContext

        val item = queued.removeAt(index)
        queued.add(targetIndex, item)
        queued.forEachIndexed { orderIdx, task ->
            dao.updateTask(task.copy(queueOrder = orderIdx + 1))
        }
    }

    suspend fun deferTask(taskId: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        val target = allTasks.firstOrNull { it.id == taskId } ?: return@withContext null

        val newStatus = if (target.resolvedStatus == TaskQueueStatus.DEFERRED) {
            TaskQueueStatus.QUEUED
        } else {
            TaskQueueStatus.DEFERRED
        }
        val maxOrder = (allTasks.maxOfOrNull { it.queueOrder } ?: 0) + 1
        dao.updateTask(target.copy(status = newStatus.name, queueOrder = maxOrder))

        val remaining = dao.getAllTasksSnapshot()
        val active = remaining.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        if (active != null) return@withContext active

        val nextQueued = remaining.filter { it.resolvedStatus == TaskQueueStatus.QUEUED }.minByOrNull { it.queueOrder }
        if (nextQueued != null) {
            val promoted = nextQueued.copy(status = TaskQueueStatus.ACTIVE.name, queueOrder = 0)
            dao.updateTask(promoted)
            return@withContext promoted
        }
        null
    }

    suspend fun deleteTaskById(taskId: Int): Pair<FocusTaskEntity?, FocusTaskEntity?> = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        val target = allTasks.firstOrNull { it.id == taskId } ?: return@withContext Pair(null, null)

        // PERMANENCE GUARD: a COMPLETED task is a stored record (the
        // Drawer). This path is the Tasks queue's per-task
        // delete-with-undo for UNCOMPLETED rows only — it refuses a
        // stored row outright, so no present or future caller can
        // delete the record through it. (Reopening a stored task is
        // reopenStoredTask's verb, never a delete.)
        if (target.resolvedStatus == TaskQueueStatus.COMPLETED) {
            return@withContext Pair(null, allTasks.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE })
        }
        dao.deleteTaskById(taskId)

        val remaining = dao.getAllTasksSnapshot()
        var currentActive = remaining.firstOrNull { it.resolvedStatus == TaskQueueStatus.ACTIVE }
        if (currentActive == null) {
            val nextQueued = remaining.filter { it.resolvedStatus == TaskQueueStatus.QUEUED }.minByOrNull { it.queueOrder }
            if (nextQueued != null) {
                currentActive = nextQueued.copy(status = TaskQueueStatus.ACTIVE.name, queueOrder = 0)
                dao.updateTask(currentActive)
            }
        }
        Pair(target, currentActive)
    }

    suspend fun restoreDeletedTask(task: FocusTaskEntity): FocusTaskEntity = withContext(Dispatchers.IO) {
        val allTasks = dao.getAllTasksSnapshot()
        if (task.resolvedStatus == TaskQueueStatus.ACTIVE) {
            allTasks.filter { it.resolvedStatus == TaskQueueStatus.ACTIVE }.forEach { prev ->
                dao.updateTask(prev.copy(status = TaskQueueStatus.QUEUED.name, queueOrder = prev.queueOrder + 1))
            }
        }
        dao.insertTask(task)
        task
    }

    // =========================================================================
    // THE DRAWER — stored tasks are COMPLETED rows of focus_tasks.
    // Reads are status-filtered and paged; there is NO clear, delete
    // or prune for stored rows anywhere in this repository —
    // retention is permanent. (The shipped clear-completed chain
    // was deleted outright at this merge, not merely unrendered:
    // a live chain one chip away from resurfacing is a deletion
    // waiting for a UI.)
    // =========================================================================

    /** One page of stored rows plus the only lawful has-earlier
     *  signal (Truth Car 2, DRW-04): the read asks for PAGE_SIZE+1
     *  rows; a (PAGE_SIZE+1)th row IS an earlier record, and its
     *  absence is proof there is none. The old contract returned
     *  a bare list and the ViewModel inferred "earlier exists"
     *  from page.size == 50 — at an exact multiple of 50 the
     *  Drawer asserted records that did not exist. */
    data class StoredPage(
        val rows: List<FocusTaskEntity>,
        val hasEarlier: Boolean
    )

    suspend fun storedPage(year: Int, month: Int, offset: Int): StoredPage {
        val (from, to) = DrawerDay.monthBoundsMillis(year, month)
        val fetched = dao.getStoredPage(from, to, DRAWER_PAGE_SIZE + 1, offset)
        return StoredPage(
            rows = fetched.take(DRAWER_PAGE_SIZE),
            hasEarlier = fetched.size > DRAWER_PAGE_SIZE
        )
    }

    /** Task-months ∪ declared-word months, newest first — a month
     *  holding only a declared word is still an address in the
     *  index, or the word would be filed nowhere. Word months come
     *  from BOTH word stores (see allDayWords). */
    suspend fun storedMonths(): List<StoredMonth> {
        // Audit R1 #37: months are filed by the ONE filing function
        // (DrawerDay) from raw instants — the SQL twin that used to
        // compute this in SQLite disagreed with it on DST days.
        val fromTasks = dao.getStoredCompletionInstants()
            .map { instant ->
                val (year, month) = DrawerDay.filingYearMonth(instant)
                StoredMonth(year, month)
            }
            .distinct()
        val fromWords = allDayWords().keys.mapNotNull { iso ->
            runCatching {
                val date = java.time.LocalDate.parse(iso)
                StoredMonth(date.year, date.monthValue)
            }.getOrNull()
        }
        return (fromTasks + fromWords).distinct().sortedWith(
            compareByDescending<StoredMonth> { it.year }.thenByDescending { it.month }
        )
    }

    suspend fun searchStoredTasks(query: String, offset: Int): StoredPage {
        // Issue #30 finding 5: escape the LIKE wildcards in the
        // user's words — the DAO's ESCAPE '\' clause makes \%, \_
        // and \\ match literally, so searching "50%" finds "50%"
        // instead of everything containing "50".
        val escaped = query.trim()
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")
        val fetched = dao.searchStoredTasks(escaped, DRAWER_PAGE_SIZE + 1, offset)
        return StoredPage(
            rows = fetched.take(DRAWER_PAGE_SIZE),
            hasEarlier = fetched.size > DRAWER_PAGE_SIZE
        )
    }

    /** Word-aware Drawer search (#158 F2): the day words a query
     *  names. A query names a word when its trimmed, upper-cased
     *  form equals a word in the closed list (DayWordStore.WORDS /
     *  [DAY_WORDS]) or is a prefix of one ("IL" names ILL). Pure —
     *  the matching rule lives in one place, shared by the
     *  suspend read below and any caller already holding the
     *  words map. */
    fun dayWordsMatching(query: String, allWords: Map<String, String>): Map<String, String> {
        val q = query.trim().uppercase(Locale.US)
        if (q.isEmpty()) return emptyMap()
        val matched = DAY_WORDS.filter { word -> word == q || word.startsWith(q) }
        if (matched.isEmpty()) return emptyMap()
        return allWords.filterValues { it in matched }
    }

    /** Every declared date carrying a word [query] names, from
     *  [allDayWords] — prefs ∪ day_notes, so a word-only day (no
     *  stored task on it) is found too. Empty when the query names
     *  no day word. */
    suspend fun searchDayWords(query: String): Map<String, String> =
        dayWordsMatching(query, allDayWords())

    // Declared day words (REST / ILL / FAMILY) live in TWO stores
    // by design of the two builds that carry them: DayWordStore
    // (SharedPreferences — the Drawer's capture surface writes
    // there) and the day_notes table (the routines build's drain
    // moves prefs words into the table at seed-sync and removes
    // them from prefs). A reader of only one store would watch
    // words vanish at the drain. allDayWords() is the union —
    // prefs (freshest, undrained) wins over the table on conflict,
    // matching the drain's own precedence.
    private val dayWordStore by lazy { DayWordStore(context) }

    /** Issue #152: the arm-zone capture for receipts (see
     *  ArmZoneStore). Written at the one birth funnel below. */
    private val armZoneStore by lazy { ArmZoneStore(context) }

    fun dayWords(): Map<String, String> = dayWordStore.all()

    suspend fun allDayWords(): Map<String, String> {
        val fromTable = dao.getAllDayNotesSnapshot().associate { it.dateIso to it.word }
        return fromTable + dayWordStore.all()
    }

    /** Declare (or clear, with null) the word for [dateIso].
     *  Returns whether the prefs write reached disk (DayWordStore's
     *  commit verdict — Audit R1 #37: the result used to be
     *  discarded, so a failed write still displayed as declared).
     *
     *  A CLEAR reaches BOTH stores: before this fix it removed only
     *  the prefs key, so a word that had drained into day_notes at
     *  seed-sync resurrected on the very next union read and could
     *  never be undeclared. The table row is deleted only when the
     *  prefs removal committed — if that failed, nothing about the
     *  clear is presented as done. */
    suspend fun declareDayWord(dateIso: String, word: String?): Boolean {
        // Issue #152 (honest-travel-day F1): never rewrite a past
        // day across a landing. Filing dates are local, so after a
        // westbound landing "today" can BE a date that already
        // carries a word declared in the departure zone — that
        // date was a lived past day, and the Drawer's law is that
        // it files words, it does not rewrite past days. A change
        // to a DIFFERENT word for such a date, made from a
        // different zone than the standing declaration's witnessed
        // zone, is refused (false — the caller must not present it
        // as declared). Same-zone changes stay free all day, and
        // words with no zone witness (declared before the witness
        // existed) are not refused on a guess.
        if (word != null) {
            val existing = allDayWords()[dateIso]
            val witnessedZone = dayWordStore.declaredZone(dateIso)
            if (existing != null && existing != word && witnessedZone != null &&
                witnessedZone != java.time.ZoneId.systemDefault().id
            ) {
                return false
            }
        }
        val committed = dayWordStore.put(dateIso, word)
        if (committed && word == null) {
            dao.deleteDayNote(dateIso)
        }
        return committed
    }

    /**
     * Used from the Airlock "Stuck on a Problem" coaching flow:
     * Breaks the current intimidating task into a 10-minute micro-step and promotes it to #1 ACTIVE immediately.
     */
    suspend fun splitActiveTaskIntoMicroStep(microStepTitle: String): FocusTaskEntity = withContext(Dispatchers.IO) {
        val cleanStep = microStepTitle.trim().ifEmpty { "Outline first 3 bullet points (10m micro-step)" }
        addTaskToQueue(
            title = cleanStep,
            estimatedMinutes = 10,
            categoryTag = "Micro-Step",
            notesOrSubsteps = FocusTaskEntity.serializeSubSteps(
                listOf(
                    TaskSubStep(0, "Open notes & clear workspace (2m)", false),
                    TaskSubStep(1, cleanStep, false)
                )
            ),
            promoteToActive = true
        )
    }

    suspend fun toggleTaskSubStep(taskId: Int, stepIndex: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val target = dao.getTaskById(taskId)
            ?: dao.getAllTasksSnapshot().firstOrNull { it.id == taskId }
            ?: return@withContext null
        val steps = target.subSteps.toMutableList()
        if (stepIndex !in steps.indices) return@withContext target
        val current = steps[stepIndex]
        steps[stepIndex] = current.copy(isDone = !current.isDone)
        val updated = target.copy(notesOrSubsteps = FocusTaskEntity.serializeSubSteps(steps))
        dao.updateTask(updated)
        updated
    }

    suspend fun addTaskSubStep(taskId: Int, stepTitle: String): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val clean = stepTitle.trim()
        if (clean.isEmpty()) return@withContext null
        val target = dao.getTaskById(taskId)
            ?: dao.getAllTasksSnapshot().firstOrNull { it.id == taskId }
            ?: return@withContext null
        val steps = target.subSteps.toMutableList()
        steps.add(TaskSubStep(index = steps.size, title = clean, isDone = false))
        val updated = target.copy(notesOrSubsteps = FocusTaskEntity.serializeSubSteps(steps))
        dao.updateTask(updated)
        updated
    }

    suspend fun autoSplitTaskIntoMicroSteps(taskId: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val target = dao.getTaskById(taskId)
            ?: dao.getAllTasksSnapshot().firstOrNull { it.id == taskId }
            ?: return@withContext null
        val generated = FocusTaskEntity.defaultSubStepsForCategory(target.title, target.categoryTag)
        val updated = target.copy(notesOrSubsteps = FocusTaskEntity.serializeSubSteps(generated))
        dao.updateTask(updated)
        updated
    }

    suspend fun updateTaskDuration(taskId: Int, estimatedMinutes: Int): FocusTaskEntity? = withContext(Dispatchers.IO) {
        val target = dao.getTaskById(taskId)
            ?: dao.getAllTasksSnapshot().firstOrNull { it.id == taskId }
            ?: return@withContext null
        val clamped = estimatedMinutes.coerceIn(5, 180)
        val updated = target.copy(estimatedMinutes = clamped)
        dao.updateTask(updated)
        updated
    }
}

