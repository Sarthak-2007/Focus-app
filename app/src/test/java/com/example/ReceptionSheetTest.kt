package com.example

import android.content.Context
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.RoutineEntity
import com.example.data.SetupStore
import com.example.service.PermState
import com.example.service.WatchItemId
import com.example.service.WatchMeasurement
import com.example.service.WatchSnapshot
import com.example.ui.components.ReceptionSheet
import com.example.ui.components.receptionDoorWord
import com.example.ui.components.receptionFirstRoutineLine
import com.example.ui.components.receptionInitiallyOpen
import com.example.ui.components.receptionTotal
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper

/**
 * THE RECEPTION (Empfang lane, Oct 30) — proof set, written
 * unrun under the compile-once law; Ren's merged compile is the
 * first run.
 *
 * The brief's four proofs:
 *  - first-open shows: the host's initial state derives from
 *    receptionInitiallyOpen(store.isCompleted()) — unstamped
 *    opens, stamped never re-interrupts (pure derivation +
 *    store round-trip below);
 *  - each door's print follows its live stamped state: the
 *    sheet's words derive from WatchStore.permPrintAt over the
 *    snapshot — fabricated snapshots per state prove the words;
 *  - the total never prints over an unread door;
 *  - exit lands on Deck: DONE fires the host's stamp-and-enter
 *    callback exactly once (the host's half — stamp, navigate
 *    DECK — is MainActivity wiring, read-verified in the lane).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReceptionSheetTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    // ------------------------------------------------------------
    // Fixtures
    // ------------------------------------------------------------

    /** A snapshot whose counted four carry [states] (item ->
     *  perm), measured at [measuredMono]. Items absent from the
     *  map are unmeasured. */
    private fun snapshotWith(
        states: Map<WatchItemId, PermState>,
        measuredMono: Long
    ): WatchSnapshot {
        val wall = System.currentTimeMillis()
        return WatchSnapshot(
            items = states.mapValues { (item, perm) ->
                WatchMeasurement(item, perm = perm, measuredMono = measuredMono, measuredWall = wall)
            },
            engineHeartbeatMono = measuredMono,
            engineHeartbeatWall = wall,
            engineProtecting = false,
            lastProvedWallMillis = wall
        )
    }

    private fun allGranted(mono: Long): WatchSnapshot = snapshotWith(
        mapOf(
            WatchItemId.ACCESSIBILITY to PermState.GRANTED,
            WatchItemId.USAGE_ACCESS to PermState.GRANTED,
            WatchItemId.DEVICE_ADMIN to PermState.GRANTED,
            WatchItemId.NOTIFICATION_LISTENER to PermState.GRANTED
        ),
        mono
    )

    /** Same as [snapshotWith] but each item carries its own
     *  measurement instant — the fixture S-1's kind-branching
     *  proofs need (one door's proof aged, the others fresh). */
    private fun snapshotWithMonos(
        states: Map<WatchItemId, PermState>,
        monoFor: (WatchItemId) -> Long
    ): WatchSnapshot {
        val wall = System.currentTimeMillis()
        return WatchSnapshot(
            items = states.mapValues { (item, perm) ->
                WatchMeasurement(
                    item, perm = perm,
                    measuredMono = monoFor(item), measuredWall = wall
                )
            },
            engineHeartbeatMono = states.keys.maxOfOrNull { monoFor(it) } ?: 0L,
            engineHeartbeatWall = wall,
            engineProtecting = false,
            lastProvedWallMillis = wall
        )
    }

    // ------------------------------------------------------------
    // The stamp + the first-open gate
    // ------------------------------------------------------------

    @Test
    fun setupStoreRoundTripsAndPersistsAcrossInstances() {
        val store = SetupStore(context)
        assertFalse("a fresh store is unstamped", store.isCompleted())
        store.markCompleted()
        assertTrue("the stamp lands", store.isCompleted())
        assertTrue(
            "a second instance reads the same stamp (the cold-start read)",
            SetupStore(context).isCompleted()
        )
    }

    @Test
    fun firstOpenGateOpensOnlyWhileUnstamped() {
        assertTrue(receptionInitiallyOpen(setupCompleted = false))
        assertFalse(
            "completed never re-interrupts",
            receptionInitiallyOpen(setupCompleted = true)
        )
    }

    // ------------------------------------------------------------
    // Door words — each print follows its live stamped state
    // ------------------------------------------------------------

    @Test
    fun doorWordsReprintTheBoardGrammar() {
        assertEquals("SET", receptionDoorWord(com.example.service.PermPrint.GRANTED))
        assertEquals("NOT SET", receptionDoorWord(com.example.service.PermPrint.MISSING))
        assertEquals("UNPROVED", receptionDoorWord(com.example.service.PermPrint.STALE))
        assertEquals("NOT CHECKED", receptionDoorWord(com.example.service.PermPrint.UNMEASURED))
    }

    @Test
    fun totalCountsWholeWhenAllFourGranted() {
        val now = SystemClock.elapsedRealtime()
        val total = receptionTotal(allGranted(now), now)
        assertEquals(4, total.count)
        // S-9: the all-set branch claims the doors, full stop —
        // never that blocking stands whole.
        assertEquals("ALL FOUR DOORS SET.", total.sentence)
    }

    @Test
    fun totalNamesTheSealSentenceForAWitnessLack() {
        val now = SystemClock.elapsedRealtime()
        val snap = snapshotWith(
            mapOf(
                WatchItemId.ACCESSIBILITY to PermState.GRANTED,
                WatchItemId.USAGE_ACCESS to PermState.MISSING,
                WatchItemId.DEVICE_ADMIN to PermState.GRANTED,
                WatchItemId.NOTIFICATION_LISTENER to PermState.GRANTED
            ),
            now
        )
        val total = receptionTotal(snap, now)
        assertEquals(3, total.count)
        assertEquals(
            "WITHOUT USAGE ACCESS, BLOCKING CANNOT PROVE ITSELF — SESSIONS WILL NOT SEAL.",
            total.sentence
        )
    }

    @Test
    fun totalNamesTheAnchorAndMailRoomSentences() {
        val now = SystemClock.elapsedRealtime()
        val adminMissing = snapshotWith(
            mapOf(
                WatchItemId.ACCESSIBILITY to PermState.GRANTED,
                WatchItemId.USAGE_ACCESS to PermState.GRANTED,
                WatchItemId.DEVICE_ADMIN to PermState.MISSING,
                WatchItemId.NOTIFICATION_LISTENER to PermState.GRANTED
            ),
            now
        )
        assertEquals(
            // S-6: the admin branch drops "MID-SESSION".
            "WITHOUT DEVICE ADMIN, MONOLITH CAN BE UNINSTALLED.",
            receptionTotal(adminMissing, now).sentence
        )
        val listenerMissing = snapshotWith(
            mapOf(
                WatchItemId.ACCESSIBILITY to PermState.GRANTED,
                WatchItemId.USAGE_ACCESS to PermState.GRANTED,
                WatchItemId.DEVICE_ADMIN to PermState.GRANTED,
                WatchItemId.NOTIFICATION_LISTENER to PermState.MISSING
            ),
            now
        )
        assertEquals(
            "WITHOUT THE LISTENER, MESSAGES ARRIVE AS THEY COME — NOTHING IS HELD.",
            receptionTotal(listenerMissing, now).sentence
        )
    }

    @Test
    fun totalNeverPrintsOverAnUnreadDoor() {
        val now = SystemClock.elapsedRealtime()
        // No snapshot at all.
        assertNull(receptionTotal(null, now).count)
        // One counted door unmeasured (absent from the map).
        val partial = snapshotWith(
            mapOf(WatchItemId.ACCESSIBILITY to PermState.GRANTED),
            now
        )
        val total = receptionTotal(partial, now)
        assertNull(total.count)
        assertEquals("THE DOORS ARE STILL BEING READ.", total.sentence)
    }

    @Test
    fun staleGrantReadsAsAgedProofNotLackInTheTotal() {
        val now = SystemClock.elapsedRealtime()
        // S-1 flipped: this test used to codify the lie (a STALE
        // grant read as WITHOUT in the total). A granted door
        // whose proof aged past the 45s bound is priced as aged
        // proof, never as absence. The count still counts only
        // fresh proofs — the sentence is what was cured.
        val oneStale = snapshotWithMonos(
            mapOf(
                WatchItemId.ACCESSIBILITY to PermState.GRANTED,
                WatchItemId.USAGE_ACCESS to PermState.GRANTED,
                WatchItemId.DEVICE_ADMIN to PermState.GRANTED,
                WatchItemId.NOTIFICATION_LISTENER to PermState.GRANTED
            )
        ) { item -> if (item == WatchItemId.ACCESSIBILITY) now - 60_000L else now }
        val total = receptionTotal(oneStale, now)
        assertEquals(3, total.count)
        assertEquals(
            "THE LAST PROOF OF ACCESSIBILITY AGED OUT — CHECK RE-PROVES IT.",
            total.sentence
        )
        // All four granted but aged: no WITHOUT anywhere, one
        // aged-proof sentence per stale door, count of fresh
        // proofs still 0.
        val allStale = receptionTotal(allGranted(now - 60_000L), now)
        assertEquals(0, allStale.count)
        assertFalse(allStale.sentence.contains("WITHOUT"))
        WatchItemId.COUNTED.forEach { item ->
            assertTrue(allStale.sentence.contains(item.boardName))
        }
    }

    @Test
    fun totalPricesMixedLacksEachInItsOwnKindsWords() {
        // C-10: one revoked door + one aged door — the total
        // prices BOTH, in COUNTED order, neither erasing the
        // other; the aged door's sentence asserts no grant.
        val now = SystemClock.elapsedRealtime()
        val mixed = snapshotWithMonos(
            mapOf(
                WatchItemId.ACCESSIBILITY to PermState.MISSING,
                WatchItemId.USAGE_ACCESS to PermState.GRANTED,
                WatchItemId.DEVICE_ADMIN to PermState.GRANTED,
                WatchItemId.NOTIFICATION_LISTENER to PermState.GRANTED
            )
        ) { item -> if (item == WatchItemId.DEVICE_ADMIN) now - 60_000L else now }
        val total = receptionTotal(mixed, now)
        assertEquals(2, total.count)
        assertEquals(
            "WITHOUT ACCESSIBILITY, BLOCKING CANNOT PROVE ITSELF — " +
                "SESSIONS WILL NOT SEAL. " +
                "THE LAST PROOF OF DEVICE ADMIN AGED OUT — CHECK RE-PROVES IT.",
            total.sentence
        )
    }

    @Test
    fun totalPricesEveryMissingDoorOneSentenceEach() {
        val now = SystemClock.elapsedRealtime()
        // Blank item 6: a two-lack skipper hears both prices at
        // the decision moment, in board order.
        val snap = snapshotWith(
            mapOf(
                WatchItemId.ACCESSIBILITY to PermState.MISSING,
                WatchItemId.USAGE_ACCESS to PermState.GRANTED,
                WatchItemId.DEVICE_ADMIN to PermState.GRANTED,
                WatchItemId.NOTIFICATION_LISTENER to PermState.MISSING
            ),
            now
        )
        val total = receptionTotal(snap, now)
        assertEquals(2, total.count)
        assertEquals(
            "WITHOUT ACCESSIBILITY, BLOCKING CANNOT PROVE ITSELF — " +
                "SESSIONS WILL NOT SEAL. " +
                "WITHOUT THE LISTENER, MESSAGES ARRIVE AS THEY COME — NOTHING IS HELD.",
            total.sentence
        )
    }

    @Test
    fun totalPricesStaleAndMissingEachInItsOwnKindsWords() {
        val now = SystemClock.elapsedRealtime()
        // Wave-2 correction (Pforte, lead): this test previously
        // codified the pre-C-10 branch-wins rule — any MISSING
        // erased every STALE door from the total. That is the
        // exact erasure C-10 killed ("a MISSING branch must
        // never erase a STALE door from the total, nor the
        // reverse") and it contradicted both the implementation
        // and totalPricesMixedLacksEachInItsOwnKindsWords above:
        // as written it would have failed on first run. The law
        // is per-kind pricing: EVERY lack is priced, in COUNTED
        // order, each in its own kind's words — here the stale
        // door (ACCESSIBILITY, first counted) prices first.
        val snap = snapshotWithMonos(
            mapOf(
                WatchItemId.ACCESSIBILITY to PermState.GRANTED,
                WatchItemId.USAGE_ACCESS to PermState.MISSING,
                WatchItemId.DEVICE_ADMIN to PermState.GRANTED,
                WatchItemId.NOTIFICATION_LISTENER to PermState.GRANTED
            )
        ) { item -> if (item == WatchItemId.ACCESSIBILITY) now - 60_000L else now }
        val total = receptionTotal(snap, now)
        assertEquals(2, total.count)
        assertEquals(
            "THE LAST PROOF OF ACCESSIBILITY AGED OUT — CHECK RE-PROVES IT. " +
                "WITHOUT USAGE ACCESS, BLOCKING CANNOT PROVE ITSELF — SESSIONS WILL NOT SEAL.",
            total.sentence
        )
    }

    @Test
    fun totalNeverContradictsItselfAcrossAllDoorStateCombinations() {
        // Wave-2 (Synchronous-collaboration seat, applied by
        // Pforte): the buried-contradiction guard. S-1 was one
        // combination (one stale door) found by hand; this sweep
        // prices all 4^4 = 256 combinations of the counted doors'
        // prints (fresh-GRANTED / stale-GRANTED / MISSING /
        // UNMEASURED) and pins the total's contract in each:
        // a null count rides exactly the still-being-read
        // sentence, and a printed count equals the number of
        // fresh-GRANTED doors — never more, never fewer.
        val now = SystemClock.elapsedRealtime()
        val counted = WatchItemId.COUNTED
        for (code in 0 until 256) {
            var rest = code
            val states = mutableMapOf<WatchItemId, PermState>()
            val stale = mutableSetOf<WatchItemId>()
            var freshGranted = 0
            var anyUnmeasured = false
            for (item in counted) {
                when (rest % 4) {
                    0 -> { states[item] = PermState.GRANTED; freshGranted++ }
                    1 -> { states[item] = PermState.GRANTED; stale += item }
                    2 -> states[item] = PermState.MISSING
                    else -> anyUnmeasured = true // absent = unmeasured
                }
                rest /= 4
            }
            val snap = snapshotWithMonos(states) { item ->
                if (item in stale) now - 60_000L else now
            }
            val total = receptionTotal(snap, now)
            if (anyUnmeasured) {
                assertNull("combo $code: unread door, no count", total.count)
                assertEquals(
                    "combo $code: unread door, reading sentence",
                    "THE DOORS ARE STILL BEING READ.", total.sentence
                )
            } else {
                assertEquals("combo $code: fresh count", freshGranted, total.count)
                assertFalse(
                    "combo $code: priced total never claims still-reading",
                    total.sentence == "THE DOORS ARE STILL BEING READ."
                )
            }
        }
    }

    // ------------------------------------------------------------
    // The next routine line — from the rows, never invented
    // ------------------------------------------------------------

    @Test
    fun routineLineNamesTheNextLiveRoutineToStart() {
        fun routine(
            name: String,
            start: Int,
            enabled: Boolean = true,
            daysMask: Int = 127
        ) = RoutineEntity(
            name = name,
            startMinutes = start,
            endMinutes = start + 60,
            daysMask = daysMask,
            loadoutName = "STUDY",
            enabled = enabled
        )
        fun at(year: Int, month0: Int, day: Int, hour: Int): Long =
            java.util.Calendar.getInstance().apply {
                set(year, month0, day, hour, 0, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis
        // Monday 2026-11-02, 04:00 local.
        val monday0400 = at(2026, java.util.Calendar.NOVEMBER, 2, 4)
        assertNull(receptionFirstRoutineLine(emptyList(), monday0400))
        assertNull(
            receptionFirstRoutineLine(
                listOf(routine("SLEEP", 1200, enabled = false)), monday0400
            )
        )
        // S-2: a daysMask == 0 routine never fires and is never
        // named, however early its startMinutes.
        assertNull(
            receptionFirstRoutineLine(
                listOf(routine("GHOST", 60, daysMask = 0)), monday0400
            )
        )
        // Both daily: the soonest next start wins, printed with
        // its home — no firing claim (the struck clause is gone).
        assertEquals(
            "NEXT ROUTINE — FASTED BLOCK · 05:00 · SET ON SHIELD",
            receptionFirstRoutineLine(
                listOf(routine("SLEEP", 1200), routine("FASTED BLOCK", 300)),
                monday0400
            )
        )
        // Next START, not smallest startMinutes: on Tuesday
        // 2026-10-27 04:00 the Monday-only 05:00 routine's next
        // start is six days out, so the daily 09:00 routine is
        // named — the old derivation named the 05:00 row FIRST.
        val tuesday0400 = at(2026, java.util.Calendar.OCTOBER, 27, 4)
        assertEquals(
            "NEXT ROUTINE — LATE BLOCK · 09:00 · SET ON SHIELD",
            receptionFirstRoutineLine(
                listOf(
                    routine("MONDAY DAWN", 300, daysMask = 1),
                    routine("LATE BLOCK", 540)
                ),
                tuesday0400
            )
        )
    }

    // ------------------------------------------------------------
    // The sheet composed — words on the surface, keys wired
    // ------------------------------------------------------------

    private fun settle(millis: Long = 700) {
        ShadowLooper.idleMainLooper()
        Thread.sleep(millis)
        ShadowLooper.idleMainLooper()
        try {
            composeRule.waitForIdle()
        } catch (_: Throwable) {
            // The staleness ticker can keep the rule non-idle; proceed anyway.
        }
        ShadowLooper.idleMainLooper()
    }

    /** USAGE ACCESS missing, the other three granted fresh. */
    private fun setSheet(
        onFix: (WatchItemId) -> Unit = {},
        onDone: () -> Unit = {},
        onOpenShield: () -> Unit = {},
        isFirstOpen: Boolean = true,
        onStamp: () -> Unit = {}
    ) {
        val now = SystemClock.elapsedRealtime()
        val snap = snapshotWith(
            mapOf(
                WatchItemId.ACCESSIBILITY to PermState.GRANTED,
                WatchItemId.USAGE_ACCESS to PermState.MISSING,
                WatchItemId.DEVICE_ADMIN to PermState.GRANTED,
                WatchItemId.NOTIFICATION_LISTENER to PermState.GRANTED
            ),
            now
        )
        composeRule.setContent {
            MyApplicationTheme(darkTheme = false) {
                ReceptionSheet(
                    visible = true,
                    snapshot = snap,
                    isFirstOpen = isFirstOpen,
                    isNight = false,
                    returnTick = 0,
                    soundEngine = null,
                    firstRoutineLine = null,
                    onShown = {},
                    onFixClick = onFix,
                    onCheckClick = {},
                    onOpenShield = onOpenShield,
                    onStamp = onStamp,
                    onDone = onDone
                )
            }
        }
        settle(900)
    }

    @Test
    fun sheetPrintsEachDoorByItsLiveState() {
        setSheet()
        composeRule.onNodeWithTag("reception_sheet").assertExists()
        // Three doors SET, the witness NOT SET — the words on the
        // surface are the snapshot's own prints, reprinted.
        composeRule.onAllNodesWithText("SET").assertCountEquals(3)
        composeRule.onNodeWithText("NOT SET").assertExists()
        // The lacking door's own key stands; the total counts 3/4.
        composeRule.onNodeWithTag("reception_fix_USAGE_ACCESS").assertExists()
        composeRule.onNodeWithTag("reception_door_DEVICE_ADMIN").assertExists()
        composeRule.onNodeWithText("GUARD 3/4").assertExists()
        composeRule.onNodeWithText(
            "WITHOUT USAGE ACCESS, BLOCKING CANNOT PROVE ITSELF — SESSIONS WILL NOT SEAL."
        ).assertExists()
    }

    @Test
    fun sheetFixKeyRoutesTheLackingDoor() {
        var fixed: WatchItemId? = null
        setSheet(onFix = { fixed = it })
        composeRule.onNodeWithTag("reception_fix_USAGE_ACCESS")
            .performScrollTo()
            .performClick()
        assertEquals(WatchItemId.USAGE_ACCESS, fixed)
    }

    @Test
    fun sheetDoneFiresTheStampAndEnterCallback() {
        // Letzt §1.8 (Flag C rewrite): G-01's order — the stamp
        // books AT the press; the enter waits for the tear.
        var stamped = false
        var done = false
        setSheet(onStamp = { stamped = true }, onDone = { done = true })
        composeRule.onNodeWithTag("reception_done")
            .performScrollTo()
            .performClick()
        assertTrue("the stamp books at the press", stamped)
        assertFalse("the enter waits for the tear (260ms)", done)
        composeRule.mainClock.advanceTimeBy(300)
        assertTrue("DONE hands the exit to the host after the tear", done)
    }

    @Test
    fun sheetDoneOnAReprintDoesNotStampAndExitsAtOnce() {
        var stamped = false
        var done = false
        setSheet(
            isFirstOpen = false,
            onStamp = { stamped = true },
            onDone = { done = true }
        )
        composeRule.onNodeWithTag("reception_done")
            .performScrollTo()
            .performClick()
        assertFalse(stamped)
        assertTrue(done)
    }

    @Test
    fun sheetShieldDoorFiresItsCallback() {
        var opened = false
        setSheet(onOpenShield = { opened = true })
        composeRule.onNodeWithTag("reception_open_list")
            .performScrollTo()
            .performClick()
        assertTrue(opened)
    }
}
