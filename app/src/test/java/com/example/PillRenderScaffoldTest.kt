package com.example

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.os.Process
import android.os.SystemClock
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.PillController
import com.example.service.PillDerivation
import com.example.service.PillFace
import java.io.File
import java.io.FileOutputStream
import java.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowSystemClock

/**
 * PILL RENDER SCAFFOLD — issue #16, Builder seat 3 (T24).
 *
 * What this file is: the render-evidence scaffold for the pill lane
 * (PR #231, batch-10 train), written against the pill's POST-TRAIN
 * API as read from branch `batch-10`. It touches no existing file.
 * The intent spec it implements lives at
 * `studio/issue-16/render-intent-spec.md` — read it before judging
 * any capture this scaffold produces.
 *
 * The post-train truth this scaffold is built on:
 *  - `PillFace` has exactly four faces: Absent, Session, Break, Peek
 *    (service/PillFace.kt). The eight states in the #16 brief
 *    (unarmed, armed, holding, peek, break, spent, finished,
 *    can't-verify) are DERIVATION SCENARIOS, not faces: each maps to
 *    a fixture over (session row, app rows, nowMono, foreground,
 *    keyguard) and an expected face. Two states (unarmed, finished)
 *    and can't-verify all derive Absent — told apart by fixture and
 *    by WHY, never by the face. The spec's table is the mapping.
 *  - The pill is NOT a composable. `PillController` builds a
 *    WindowManager overlay LinearLayout privately; its public
 *    surface is the constructor + start()/stop(). There is no
 *    Compose node to screenshot. Tier B therefore drives the real
 *    controller and harvests the real window it built.
 *
 * TWO TIERS:
 *  - Tier A (a_*): derivation evidence. Pure, deterministic, no
 *    window. Per state: the exact face, its renderKey, and the
 *    literal printed strings PillController.render would show for
 *    that face (label / time / hand-back ✕ visibility), asserted as
 *    literals — the same literal-oracle discipline PillDerivationTest
 *    adopted in its wave-2 settling fixes (F1/F2/F5).
 *  - Tier B (b_*): window captures. The real PillController, a real
 *    in-memory Room DB seeded per state, the real ticker. The window
 *    view is read back from the controller instance (reflection on
 *    its own `rootView` field — app code, not a framework shadow),
 *    drawn to a PNG in the lane shots dir, and its TextViews are
 *    walked and asserted against the Tier A literals.
 *
 * TIER B SEAMS, NAMED BEFORE THEY BITE: the controller's foreground
 * read is `MonolithRepository.queryForegroundPackage()` — gated by
 * the GET_USAGE_STATS AppOp and fed by UsageStatsManager events;
 * the window is gated by `Settings.canDrawOverlays` (the
 * SYSTEM_ALERT_WINDOW AppOp). Neither gate is injectable in the
 * post-train API. This scaffold opens both gates through the
 * Robolectric shadows BY REFLECTION (probe-and-report: no compile
 * dependency on shadow internals, which shift between Robolectric
 * versions). If a gate cannot be opened, the Tier B test fails with
 * a SEAM-REPORT naming the gate and what the probe found — that
 * report is a deliverable of this scaffold, not an accident. The
 * priced cure (a @VisibleForTesting foreground/overlay seam on
 * PillController) belongs to the train owners (K1/W6); this branch
 * never edits their files.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PillRenderScaffoldTest {

    // ------------------------------------------------------------------
    // Fixtures — constructors copied verbatim from PillDerivationTest
    // (the train's own proven shapes for these entities).
    // ------------------------------------------------------------------

    private val own = "com.example"
    private val t0 = 1_000_000L

    private fun session(
        isActive: Boolean = true,
        isStopwatch: Boolean = false,
        isInSafeBreak: Boolean = false,
        elapsedSeconds: Int = 0,
        targetDurationSeconds: Int = 1500,
        penaltySecondsAdded: Int = 0,
        safeBreakSecondsRemaining: Int = 0,
        lastTickElapsedRealtimeMillis: Long = 0L,
        currentBlockIndex: Int = 1,
        totalStudyBlocks: Int = 1,
        completedSuccessfully: Boolean = false
    ) = FocusSessionEntity(
        intention = "Deep Study",
        profileName = "Default",
        targetDurationSeconds = targetDurationSeconds,
        elapsedSeconds = elapsedSeconds,
        startWallTimeMillis = 1_000_000L,
        startElapsedRealtimeMillis = 0L,
        isActive = isActive,
        completedSuccessfully = completedSuccessfully,
        isStopwatchMode = isStopwatch,
        isInSafeBreak = isInSafeBreak,
        safeBreakSecondsRemaining = safeBreakSecondsRemaining,
        lastTickElapsedRealtimeMillis = lastTickElapsedRealtimeMillis,
        penaltySecondsAdded = penaltySecondsAdded,
        currentBlockIndex = currentBlockIndex,
        totalStudyBlocks = totalStudyBlocks
    )

    private fun app(pkg: String, name: String, peekUntil: Long = 0L) = BlockedAppEntity(
        packageName = pkg,
        appName = name,
        category = "MESSAGING",
        isBlocked = true,
        activePeekUntilElapsedRealtime = peekUntil
    )

    private fun derive(
        session: FocusSessionEntity?,
        rows: List<BlockedAppEntity> = emptyList(),
        now: Long = t0,
        foreground: String? = "com.notes.app",
        keyguard: Boolean = false
    ) = PillDerivation.derive(session, rows, now, foreground, own, keyguard)

    // ------------------------------------------------------------------
    // The printed face — a literal mirror of PillController.render
    // (batch-10 PillController.kt, render()): Session label is
    // "SESSION · ELAPSED" (stopwatch) or "SESSION · BLOCK i/n",
    // time is "+mm:ss" elapsed (stopwatch) or "mm:ss" remaining;
    // Break label "BREAK"; Peek label is the app name UPPERCASED;
    // the hand-back ✕ is VISIBLE only on Peek. Mirroring is a drift
    // risk and is named as one in the intent spec §5: Tier B's
    // TextView walk is what pins the mirror to the real renderer.
    // ------------------------------------------------------------------

    private data class Print(val label: String, val time: String, val handBackVisible: Boolean)

    private fun formatClock(totalSeconds: Int): String {
        val clamped = totalSeconds.coerceAtLeast(0)
        return String.format("%02d:%02d", clamped / 60, clamped % 60)
    }

    private fun expectedPrint(face: PillFace): Print? = when (face) {
        PillFace.Absent -> null
        is PillFace.Session -> Print(
            label = if (face.isStopwatch) "SESSION · ELAPSED" else "SESSION · BLOCK ${face.blockIndex}/${face.blockCount}",
            time = if (face.isStopwatch) "+${formatClock(face.elapsedSeconds)}" else formatClock(face.remainingSeconds),
            handBackVisible = false
        )
        is PillFace.Break -> Print("BREAK", formatClock(face.remainingSeconds), false)
        is PillFace.Peek -> Print(face.appName.uppercase(), formatClock(face.remainingSeconds), true)
    }

    // ==================================================================
    // TIER A — one derivation-evidence test per brief state.
    // ==================================================================

    /** State 1 — UNARMED: no session row at all. The pill does not
     *  exist. Evidence = Absent + no print. A capture for this state
     *  must show NO pill (see Tier B b_unarmed). */
    @Test
    fun a_unarmed_noSession_absentNoPrint() {
        val face = derive(null)
        assertEquals(PillFace.Absent, face)
        assertEquals("absent", face.renderKey)
        assertNull(expectedPrint(face))
    }

    /** State 2 — ARMED: the session has just been armed — active,
     *  elapsed 0, full target remaining, first block — and he has
     *  stepped into a foreign app. The pill stands at full length.
     *  Evidence = Session face, remaining == target, print
     *  "SESSION · BLOCK 1/4" / "25:00" / no ✕. */
    @Test
    fun a_armed_freshSession_sessionFaceAtFullLength() {
        val face = derive(
            session(elapsedSeconds = 0, targetDurationSeconds = 1500, currentBlockIndex = 1, totalStudyBlocks = 4)
        )
        assertEquals(
            PillFace.Session("Deep Study", false, 1500, 0, 1, 4),
            face
        )
        assertEquals(Print("SESSION · BLOCK 1/4", "25:00", false), expectedPrint(face))
    }

    /** State 3 — HOLDING: mid-session, the block is holding over a
     *  foreign app. Elapsed is read off the stored (elapsed, lastTick)
     *  pair at the sample instant — 300 stored + 10 s = 310, so
     *  remaining is 1190. Evidence = Session print
     *  "SESSION · BLOCK 2/4" / "19:50" / no ✕. */
    @Test
    fun a_holding_midBlock_sessionFaceWithDerivedRemaining() {
        val s = session(
            elapsedSeconds = 300,
            targetDurationSeconds = 1500,
            lastTickElapsedRealtimeMillis = t0,
            currentBlockIndex = 2,
            totalStudyBlocks = 4
        )
        val face = derive(s, now = t0 + 10_000L)
        assertEquals(PillFace.Session("Deep Study", false, 1190, 310, 2, 4), face)
        assertEquals(Print("SESSION · BLOCK 2/4", "19:50", false), expectedPrint(face))
    }

    /** Bonus to holding — the stopwatch form of the same state:
     *  label switches to ELAPSED and the time gains its "+". */
    @Test
    fun a_holding_stopwatchVariant_elapsedPrint() {
        val s = session(isStopwatch = true, elapsedSeconds = 100, lastTickElapsedRealtimeMillis = t0)
        val face = derive(s, now = t0 + 5_000L)
        assertEquals(Print("SESSION · ELAPSED", "+01:45", false), expectedPrint(face))
    }

    /** State 4 — PEEK: a live lease over the peeked app. The face
     *  names the row (not the foreground), the fuse is the ceiling
     *  of end − now, and the hand-back ✕ stands — the only state
     *  that carries it. Evidence = Peek print "PEEKED" / "02:00" /
     *  ✕ VISIBLE. */
    @Test
    fun a_peek_liveLease_peekFaceWithHandBack() {
        val rows = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))
        val face = derive(session(), rows, now = t0, foreground = "com.peeked")
        assertEquals(PillFace.Peek("com.peeked", "Peeked", 120), face)
        assertEquals("peek:com.peeked:120", face.renderKey)
        assertEquals(Print("PEEKED", "02:00", true), expectedPrint(face))
    }

    /** State 5 — BREAK: the safe break is live. A live peek lease is
     *  seeded TOO, on purpose: BREAK outranks PEEK (during a break
     *  the doors stand open lease-free; a fuse on an open door would
     *  be a false urgency). Evidence = Break print "BREAK" / "09:30"
     *  (600 stored − 30 s) / no ✕, and explicitly NOT the peek. */
    @Test
    fun a_break_safeBreakLive_breakFaceOutranksPeek() {
        val s = session(
            isInSafeBreak = true,
            safeBreakSecondsRemaining = 600,
            lastTickElapsedRealtimeMillis = t0
        )
        val rows = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))
        val face = derive(s, rows, now = t0 + 30_000L, foreground = "com.peeked")
        assertEquals(PillFace.Break(570), face)
        assertEquals(Print("BREAK", "09:30", false), expectedPrint(face))
    }

    /** State 6 — SPENT: the peek's fuse has burned to its end
     *  instant. The lease is dead AT the end (ceiling law: the last
     *  partial second reads 1, the end instant reads nothing) and
     *  the face reverts to the session's own — values pinned, per
     *  the train test's F5 discipline. Evidence = Session print
     *  "SESSION · BLOCK 1/1" / "25:00" / NO ✕ and no peeked name
     *  anywhere: a spent peek leaves no residue on the glass. */
    @Test
    fun a_spent_leaseAtItsEndInstant_faceRevertsToSession() {
        val rows = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))
        val face = derive(session(), rows, now = t0 + 120_000L, foreground = "com.peeked")
        assertEquals(
            PillFace.Session("Deep Study", false, 1500, 0, 1, 1),
            face
        )
        assertEquals(Print("SESSION · BLOCK 1/1", "25:00", false), expectedPrint(face))
    }

    /** State 7 — FINISHED: the session row stands completed and
     *  inactive. Same Absent as UNARMED — the fixture is the only
     *  difference, and the intent spec says so out loud: a finished
     *  capture and an unarmed capture are the same picture (no
     *  pill), told apart by the seeded row in the test record,
     *  never by anything on screen. */
    @Test
    fun a_finished_completedInactiveSession_absentNoPrint() {
        val face = derive(session(isActive = false, completedSuccessfully = true, elapsedSeconds = 1500))
        assertEquals(PillFace.Absent, face)
        assertNull(expectedPrint(face))
    }

    /** State 8 — CAN'T-VERIFY: the machine cannot honestly print.
     *  Two derivation-level forms, both Absent DESPITE a live
     *  session and a live lease:
     *  (a) keyguard locked — the pill draws over apps, never over
     *      the system's lock face (§2.4 Q2);
     *  (b) telecom foreground — a human voice outranks a fuse
     *      (§2.4 Q4).
     *  The third form — overlay grant missing — is not a derivation
     *  input at all; it is Tier B's b_cantVerify test. Evidence for
     *  this state is an ABSENCE WITH A NAMED CAUSE: the test name
     *  and fixture carry the cause, the capture carries nothing. */
    @Test
    fun a_cantVerify_keyguardLocked_absentDespiteLiveLease() {
        val rows = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))
        val face = derive(session(), rows, now = t0, foreground = "com.peeked", keyguard = true)
        assertEquals(PillFace.Absent, face)
        assertNull(expectedPrint(face))
    }

    @Test
    fun a_cantVerify_telecomForeground_absentDespiteLiveLease() {
        val rows = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))
        for (pkg in PillDerivation.TELECOM_FOREGROUND_PACKAGES) {
            val face = derive(session(), rows, now = t0, foreground = pkg)
            assertEquals("telecom $pkg", PillFace.Absent, face)
        }
    }

    /** Bonus absence — inside Monolith itself (§2.4 Q1, hard rule):
     *  the session is armed and holding, but Monolith is in front,
     *  so Monolith speaks and the pill is sheathed. */
    @Test
    fun a_armedButHome_ownPackageForeground_absent() {
        val rows = listOf(app("com.peeked", "Peeked", peekUntil = t0 + 120_000L))
        val face = derive(session(), rows, now = t0, foreground = own)
        assertEquals(PillFace.Absent, face)
    }

    // ==================================================================
    // TIER B — real-controller window captures.
    // ==================================================================

    private lateinit var context: Context
    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository
    private var controller: PillController? = null
    private var controllerScope: CoroutineScope? = null

    // Lane render isolation (the harness pattern): a shots-redirect.txt
    // in the lane names its own shots dir; otherwise this scaffold's
    // own tmp dir. Captures never land in the shared shots folder.
    private val shotsDir: File = run {
        val redirect = listOf(
            File("shots-redirect.txt"),
            File("../shots-redirect.txt"),
            File("app/shots-redirect.txt")
        ).firstOrNull { it.isFile }
        val target = redirect?.readText()?.trim()?.takeIf { it.isNotEmpty() }
            ?: (System.getProperty("java.io.tmpdir") + "/monolith-shots-pill")
        File(target).apply { mkdirs() }
    }

    @Before
    fun setupTierB() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = PillRepositoryFactory.create(context, db)
    }

    @After
    fun tearDownTierB() {
        runCatching { controller?.stop() }
        controller = null
        ShadowLooper.idleMainLooper()
        controllerScope?.cancel()
        controllerScope = null
        if (::db.isInitialized) db.close()
    }

    /** Small indirection so the repository construction reads in one
     *  place (context + the DAO of the in-memory DB). */
    private object PillRepositoryFactory {
        fun create(context: Context, db: MonolithDatabase): MonolithRepository =
            MonolithRepository(context, db.monolithDao())
    }

    // ---- shadow gate probes (reflection only; see class header) ----

    private val seamReport = StringBuilder()

    private fun shadowOf(target: Any): Any? {
        val shadows = Class.forName("org.robolectric.Shadows")
        val method = shadows.declaredMethods.firstOrNull { m ->
            m.name == "shadowOf" && m.parameterTypes.size == 1 &&
                m.parameterTypes[0].isAssignableFrom(target.javaClass)
        } ?: shadows.declaredMethods.firstOrNull { m ->
            m.name == "shadowOf" && m.parameterTypes.size == 1 &&
                target.javaClass.interfaces.any { m.parameterTypes[0].isAssignableFrom(it) }
        }
        return runCatching { method?.invoke(null, target) }.getOrNull()
    }

    /** Set an AppOp mode through ShadowAppOpsManager. EVERY
     *  setMode signature shape is attempted (String-op and Int-op):
     *  CI run 41155367631 proved the String form alone lands in a
     *  store the AOSP readback does not consult for the overlay op
     *  (setMode(String) reported ok, usage access read ALLOWED, yet
     *  canDrawOverlays stayed false). Returns every outcome. */
    private fun probeSetAppOp(opStr: String, opInt: Int, mode: Int): String {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val shadow = shadowOf(appOps) ?: return "no-shadow"
        val uid = Process.myUid()
        val pkg = context.packageName
        val outcomes = mutableListOf<String>()
        for (m in shadow.javaClass.methods.filter { it.name == "setMode" }) {
            val params = m.parameterTypes
            val args: Array<Any?>? = when {
                params.size == 4 && params[0] == String::class.java ->
                    arrayOf(opStr, uid, pkg, mode)
                params.size == 4 && (params[0] == Int::class.javaPrimitiveType || params[0] == Integer::class.java) ->
                    arrayOf(opInt, uid, pkg, mode)
                else -> null
            }
            if (args != null) {
                val ok = runCatching { m.invoke(shadow, *args) }.isSuccess
                outcomes.add("setMode(${params[0].simpleName})=${if (ok) "ok" else "fail"}")
            }
        }
        return if (outcomes.isEmpty()) "setMode-unavailable" else outcomes.joinToString(",")
    }

    /** Second overlay route: the runtime permission itself.
     *  Settings.canDrawOverlays falls back to the SYSTEM_ALERT_WINDOW
     *  permission grant when the AppOp reads MODE_DEFAULT, and some
     *  Robolectric versions track the grant on the application
     *  shadow, not in AppOps at all. Probe grantPermissions on the
     *  application shadow by reflection; report what happened. */
    private fun probeGrantOverlayPermission(): String {
        val app = context.applicationContext
        val shadow = shadowOf(app) ?: return "no-app-shadow"
        for (m in shadow.javaClass.methods.filter { it.name == "grantPermissions" }) {
            val params = m.parameterTypes
            val args: Array<Any?>? = when {
                params.size == 1 && params[0] == Array<String>::class.java ->
                    arrayOf(arrayOf(android.Manifest.permission.SYSTEM_ALERT_WINDOW))
                params.size == 1 && params[0] == String::class.java ->
                    arrayOf(android.Manifest.permission.SYSTEM_ALERT_WINDOW)
                else -> null
            }
            if (args != null) {
                val ok = runCatching { m.invoke(shadow, *args) }.isSuccess
                if (ok) return "grantPermissions(${params[0].simpleName})-ok"
            }
        }
        return "grantPermissions-unavailable"
    }

    /** Seed a MOVE_TO_FOREGROUND usage event for [pkg] at wall-now,
     *  through ShadowUsageStatsManager, whichever addEvent shape
     *  this Robolectric carries. Returns what happened. */
    private fun probeSeedForegroundEvent(pkg: String): String {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return "no-usage-stats-service"
        val shadow = shadowOf(usm) ?: return "no-shadow"
        val wallNow = System.currentTimeMillis()
        val candidates = shadow.javaClass.methods.filter { it.name.startsWith("addEvent") }
        for (m in candidates) {
            val params = m.parameterTypes
            val args: Array<Any?>? = when {
                // addEvent(String packageName, long timestamp, int eventType)
                params.size == 3 && params[0] == String::class.java ->
                    arrayOf(pkg, wallNow, UsageEvents.Event.MOVE_TO_FOREGROUND)
                // addEvent(UsageEvents.Event)
                params.size == 1 && params[0] == UsageEvents.Event::class.java -> {
                    val event = UsageEvents.Event()
                    runCatching {
                        setEventField(event, pkg, wallNow, UsageEvents.Event.MOVE_TO_FOREGROUND)
                    }
                    arrayOf(event)
                }
                else -> null
            }
            if (args != null) {
                val ok = runCatching { m.invoke(shadow, *args) }.isSuccess
                if (ok) return "${m.name}/${params.size}-ok"
            }
        }
        return "addEvent-unavailable(${candidates.map { it.name + "/" + it.parameterTypes.size }})"
    }

    private fun setEventField(event: UsageEvents.Event, pkg: String, timeMillis: Long, type: Int) {
        fun setField(names: List<String>, value: Any) {
            for (name in names) {
                val field = runCatching {
                    UsageEvents.Event::class.java.getDeclaredField(name)
                }.getOrNull() ?: continue
                field.isAccessible = true
                runCatching { field.set(event, value) }
                return
            }
        }
        setField(listOf("mPackageName", "packageName"), pkg)
        setField(listOf("mTimeStamp", "timeStamp", "mTimestamp"), timeMillis)
        setField(listOf("mEventType", "eventType"), type)
    }

    /** Open both gates and seed the foreground, recording every
     *  probe outcome into the seam report. Foreground seeding is
     *  retried (up to 3 rounds): run 41155367631 read the seeded
     *  event back in one test and null in two others with identical
     *  seeding — the shadow event store's first-write timing is not
     *  deterministic, so the scaffold seeds until the production
     *  read itself confirms the foreground, and reports the rounds
     *  it took. The readback that matters is the repository's own
     *  queryForegroundPackage — the same call the ticker makes. */
    private fun openGatesAndSeedForeground(foreignPkg: String) {
        seamReport.clear()
        seamReport.append("usageOp=").append(
            probeSetAppOp(AppOpsManager.OPSTR_GET_USAGE_STATS, 43, AppOpsManager.MODE_ALLOWED)
        )
        seamReport.append(" overlayOp=").append(
            probeSetAppOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, 24, AppOpsManager.MODE_ALLOWED)
        )
        seamReport.append(" overlayGrant=").append(probeGrantOverlayPermission())
        seamReport.append(" canDrawOverlays=").append(Settings.canDrawOverlays(context))
        seamReport.append(" usageAccess=").append(repository.usageAccess())
        var readback: String? = null
        var rounds = 0
        while (rounds < 3) {
            rounds++
            val seeded = probeSeedForegroundEvent(foreignPkg)
            readback = repository.queryForegroundPackage()
            seamReport.append(" fg[$rounds]=").append(seeded).append("->").append(readback)
            if (readback == foreignPkg) break
            Thread.sleep(60)
        }
        seamReport.append(" foregroundRead=").append(readback)
    }

    // ---- controller drive + harvest ----

    private fun startController() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        controllerScope = scope
        controller = PillController(
            context = context,
            repository = repository,
            dao = db.monolithDao(),
            scope = scope,
            ownPackage = context.packageName
        )
        controller!!.start()
    }

    /** Advance the shadow clock + idle the main looper in slices so
     *  the controller's 1 s ticker fires several times and Room's
     *  background executors get real wall time to answer. */
    private fun pumpTicks(slices: Int = 10, sliceMillis: Long = 600) {
        repeat(slices) {
            ShadowSystemClock.advanceBy(Duration.ofMillis(sliceMillis))
            ShadowLooper.idleMainLooper()
            Thread.sleep(40)
            ShadowLooper.idleMainLooper()
        }
    }

    /** The window the controller built, read off its own field.
     *  Null = no window stands (the honest picture for Absent). */
    private fun harvestedRootView(): View? {
        val c = controller ?: return null
        val field = PillController::class.java.getDeclaredField("rootView")
        field.isAccessible = true
        return field.get(c) as? View
    }

    private fun textViewsOf(root: View): List<TextView> {
        val out = mutableListOf<TextView>()
        fun walk(v: View) {
            if (v is TextView) out.add(v)
            if (v is ViewGroup) for (i in 0 until v.childCount) walk(v.getChildAt(i))
        }
        walk(root)
        return out
    }

    /** Draw the harvested window to shotsDir/pill_<state>.png and
     *  return (label, time, handBackVisible) as actually printed. */
    private fun captureWindow(state: String, root: View): Print {
        root.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(2400, View.MeasureSpec.AT_MOST)
        )
        root.layout(0, 0, root.measuredWidth.coerceAtLeast(1), root.measuredHeight.coerceAtLeast(1))
        val bitmap = Bitmap.createBitmap(root.width, root.height, Bitmap.Config.ARGB_8888)
        root.draw(Canvas(bitmap))
        val out = File(shotsDir, "pill_$state.png")
        FileOutputStream(out).use { fos -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos) }
        bitmap.recycle()
        println("PILL-RENDER captured pill_$state.png size=${out.length()} dir=${shotsDir.absolutePath}")
        assertTrue("capture pill_$state.png must be a real PNG (>1 KB), was ${out.length()}", out.length() > 1_000)

        // PillController's window structure (buildWindow): a column
        // holding [label, time] and a sibling hand-back TextView "✕".
        val texts = textViewsOf(root)
        val handBack = texts.firstOrNull { it.text?.toString() == "✕" }
        val labelAndTime = texts.filter { it !== handBack }
        assertTrue("pill window must carry label+time TextViews, found ${texts.size}", labelAndTime.size >= 2)
        return Print(
            label = labelAndTime[0].text?.toString() ?: "",
            time = labelAndTime[1].text?.toString() ?: "",
            handBackVisible = handBack != null && handBack.visibility == View.VISIBLE
        )
    }

    private fun failSeam(state: String): Nothing {
        fail("SEAM-REPORT pill/$state: no window harvested. Gates: $seamReport")
        throw AssertionError("unreachable — fail() above always throws")
    }

    // ---- the captures ----

    /** Tier B — HOLDING: seeded active session (elapsed read live:
     *  fixtures are anchored to the CURRENT shadow elapsedRealtime,
     *  so the derived remaining sits in a named 4-second window —
     *  see intent spec §4 for why a window, not a point). */
    @Test
    fun b_holding_windowCapture_printsSessionFace() {
        val now = SystemClock.elapsedRealtime()
        runBlocking {
            db.monolithDao().insertSession(
                session(
                    elapsedSeconds = 300,
                    targetDurationSeconds = 1500,
                    lastTickElapsedRealtimeMillis = now,
                    currentBlockIndex = 2,
                    totalStudyBlocks = 4
                )
            )
        }
        openGatesAndSeedForeground("com.notes.app")
        startController()
        pumpTicks()
        val root = harvestedRootView() ?: failSeam("holding")
        val print = captureWindow("holding", root)
        assertEquals("SESSION · BLOCK 2/4", print.label)
        assertTrue(
            "holding time must be the derived remaining (1200 s at seed, minus pump time), was ${print.time}",
            print.time in setOf("20:00", "19:59", "19:58", "19:57", "19:56")
        )
        assertEquals(false, print.handBackVisible)
    }

    /** Tier B — PEEK: live lease anchored to now + 120 s. The window
     *  must name the peeked app and stand the ✕. */
    @Test
    fun b_peek_windowCapture_printsPeekFaceWithHandBack() {
        val now = SystemClock.elapsedRealtime()
        runBlocking {
            val dao = db.monolithDao()
            dao.insertSession(session())
            dao.insertApp(app("com.peeked", "Peeked", peekUntil = now + 120_000L))
        }
        openGatesAndSeedForeground("com.peeked")
        startController()
        pumpTicks()
        val root = harvestedRootView() ?: failSeam("peek")
        val print = captureWindow("peek", root)
        assertEquals("PEEKED", print.label)
        assertTrue(
            "peek fuse must read the lease's ceiling seconds (120 at grant, minus pump time), was ${print.time}",
            print.time in setOf("02:00", "01:59", "01:58", "01:57", "01:56")
        )
        assertEquals(true, print.handBackVisible)
    }

    /** Tier B — BREAK: break live (600 stored, tick anchored to
     *  now); a live lease is seeded too — the window must print the
     *  BREAK, not the fuse. */
    @Test
    fun b_break_windowCapture_printsBreakNotPeek() {
        val now = SystemClock.elapsedRealtime()
        runBlocking {
            val dao = db.monolithDao()
            dao.insertSession(
                session(
                    isInSafeBreak = true,
                    safeBreakSecondsRemaining = 600,
                    lastTickElapsedRealtimeMillis = now
                )
            )
            dao.insertApp(app("com.peeked", "Peeked", peekUntil = now + 120_000L))
        }
        openGatesAndSeedForeground("com.peeked")
        startController()
        pumpTicks()
        val root = harvestedRootView() ?: failSeam("break")
        val print = captureWindow("break", root)
        assertEquals("BREAK", print.label)
        assertTrue(
            "break time must be the stored 600 s minus pump time, was ${print.time}",
            print.time in setOf("10:00", "09:59", "09:58", "09:57", "09:56")
        )
        assertEquals(false, print.handBackVisible)
    }

    /** Tier B — UNARMED: no session seeded. Even with both gates
     *  open and a foreign foreground, no window may stand. The
     *  evidence is the absence itself — no PNG is manufactured. */
    @Test
    fun b_unarmed_noWindowStands() {
        openGatesAndSeedForeground("com.notes.app")
        startController()
        pumpTicks()
        assertNull(
            "unarmed must never raise a pill window (gates: $seamReport)",
            harvestedRootView()
        )
        assertEquals(PillFace.Absent, derive(null))
    }

    /** Tier B — CAN'T-VERIFY (overlay form): session + lease are
     *  live and the foreground is foreign, but the overlay grant is
     *  REVOKED. The pill must stay down — Route B (the shield
     *  notification) carries the time instead; a pill that drew
     *  anyway would be printing without a warrant. If the revoke
     *  probe itself cannot move canDrawOverlays, this test fails at
     *  the probe assertion and names that — the gate is then
     *  untestable from Robolectric and the spec says what that
     *  means (device-only evidence). */
    @Test
    fun b_cantVerify_overlayRevoked_windowStaysDown() {
        val now = SystemClock.elapsedRealtime()
        runBlocking {
            val dao = db.monolithDao()
            dao.insertSession(session())
            dao.insertApp(app("com.peeked", "Peeked", peekUntil = now + 120_000L))
        }
        openGatesAndSeedForeground("com.peeked")
        val revoke = probeSetAppOp(
            AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, 24, AppOpsManager.MODE_ERRORED
        )
        assertTrue(
            "overlay revoke probe must actually close the gate " +
                "(probe=$revoke, canDrawOverlays=${Settings.canDrawOverlays(context)}) — " +
                "if this fails, the can't-verify/overlay state is not Robolectric-testable " +
                "on this Robolectric version and needs device evidence (intent spec §6)",
            !Settings.canDrawOverlays(context)
        )
        startController()
        pumpTicks()
        assertNull(
            "with the overlay grant revoked, no pill window may stand",
            harvestedRootView()
        )
    }
}
