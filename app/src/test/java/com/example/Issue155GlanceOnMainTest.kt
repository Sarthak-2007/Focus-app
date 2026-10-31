package com.example

import android.app.Notification
import com.example.data.RoutineEntity
import com.example.data.RoutineSchedule
import com.example.service.MonolithShieldService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.Calendar

/**
 * Issue #155 — WING-F BUILDER-5, the ON-MAIN verification set.
 *
 * PR #312 landed the F1/F3/F4/F5 cures on main with no executing
 * pin at all, and BUILDER-3 verified the widget's idle truth holds
 * on main (VERIFIED vs main 8625e7a, #155 comment 6536443799) with
 * the idle selection itself unpinned. This file pins what is
 * ALREADY on main, so a future merge train cannot silently
 * un-land it (F1 already broke once that way):
 *
 *  - F1 idle selection: the pure next-start math the widget's
 *    idle line is derived from (next-seal, the strictly-after
 *    contract, the pause origin-1 rule — the #512 bug class),
 *    the live filter, and the provider's scoped source wiring
 *    (NEXT SEAL / plain OPEN / fail-closed lines, in order).
 *  - F1 existence: the widget receiver + provider meta-data in
 *    the manifest and the 30-minute period in the widget info.
 *  - F1 refresh triggers that exist ON MAIN (shield side):
 *    widget refresh at the protecting flip, at completion, and
 *    on the STOP dispatch. (The repository routine/session
 *    funnels are NOT on main — they ride PR #540 and are pinned
 *    on the stacked branch agent/wingf-155-tests-residue-pins.)
 *  - F2 initial text: the foreground post starts from the honest
 *    arming line; the audit's lying strings are absent.
 *  - F3: the completion call site posts the expiring triple,
 *    and the builder plumbing lands it on the real Notification
 *    (adopted from PR #540's sound builder pins).
 *  - F4: the v2 channel shows the badge and deletes the legacy
 *    badge-off channel; the badge number is the held count with
 *    the urges-stopped fallback; the builder lands setNumber.
 *  - F5: the builder applies BigTextStyle, the system
 *    chronometer (when = session end), and the shield routing
 *    extra; MainActivity routes that extra to SHIELD.
 *
 * Deliberately NOT pinned here (cure not on main): the widget
 * SEALED truth rule and the card-flip ordering and the summons
 * expiry — those ride PR #540 (WidgetSealTruthTest,
 * Issue155CompletionExpiryTest) plus the stacked residue pins.
 * On main the widget still prints SEALED from the row alone and
 * the loop still posts the full card before the flip; no test
 * in this file may be read as covering either.
 *
 * UNVERIFIED-BY-CI until this PR's own head run compiles and
 * executes this class — the job log is the only proof.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Issue155GlanceOnMainTest {

    // ------------------------------------------------------------
    // F1 idle selection — the pure math (Mon 2026-10-26 week, the
    // same Calendar basis production and RoutineDeriveTest use).
    // ------------------------------------------------------------

    private fun at(dayOffsetFromMonday: Int, hour: Int, minute: Int = 0): Long {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.OCTOBER, 26, hour, minute, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_YEAR, dayOffsetFromMonday)
        return cal.timeInMillis
    }

    private fun routine(
        start: Int = 540,
        end: Int = 1170,
        mask: Int = RoutineSchedule.ALL_DAYS,
        enabled: Boolean = true,
        pausedUntil: Long = 0L
    ) = RoutineEntity(
        id = 1, name = "DAY BLOCKS", startMinutes = start, endMinutes = end,
        daysMask = mask, loadoutName = "STUDY", enabled = enabled,
        pausedUntilMillis = pausedUntil
    )

    @Test
    fun idleNextStart_pauseEndingExactlyAtWindowStart_stillCountsThatWindow() {
        // The origin-1 rule (#512 bug class): the search origin is
        // max(now, pausedUntil); fed strictly-after, a pause ending
        // exactly at a window start would skip that window for a
        // whole week if the origin were not passed as origin - 1.
        val r = routine(pausedUntil = at(0, 9))
        assertEquals(
            at(0, 9),
            RoutineSchedule.effectiveNextStartMillis(r, at(0, 8))
        )
    }

    @Test
    fun idleNextStart_pauseCoveringTheWindow_skipsToTheNextDay() {
        // A window the pause covers never fires, so it is never
        // the next seal the widget may print.
        val r = routine(pausedUntil = at(0, 10))
        assertEquals(
            at(1, 9),
            RoutineSchedule.effectiveNextStartMillis(r, at(0, 8))
        )
    }

    @Test
    fun idleNextStart_windowStartingExactlyNow_isNotTheNextStart() {
        // Strictly-after contract: a window starting now is the
        // containing window, not the NEXT one.
        assertEquals(
            at(1, 9),
            RoutineSchedule.effectiveNextStartMillis(routine(), at(0, 9))
        )
    }

    @Test
    fun idleNextStart_noDaysSelected_returnsNull() {
        // Null is what makes the widget fall through to plain OPEN.
        val r = routine(mask = 0)
        assertNull(RoutineSchedule.effectiveNextStartMillis(r, at(0, 8)))
        assertNull(RoutineSchedule.nextStartMillis(r, at(0, 8)))
    }

    @Test
    fun idleSelection_liveFilter_excludesDisabledAndMasklessRoutines() {
        // The widget filters isLive before it may print NEXT SEAL.
        assertTrue(RoutineSchedule.isLive(routine()))
        assertFalse(RoutineSchedule.isLive(routine(enabled = false)))
        assertFalse(RoutineSchedule.isLive(routine(mask = 0)))
    }

    // ------------------------------------------------------------
    // Source pins — scoped windows and function bodies only,
    // never whole-file contains that the wrong site could satisfy.
    // ------------------------------------------------------------

    private fun sourceOf(relativePath: String): String = listOf(
        File("src/main/$relativePath"),
        File("app/src/main/$relativePath"),
        File("../app/src/main/$relativePath")
    ).firstOrNull { it.exists() }?.readText()
        ?: error("$relativePath not found from ${File(".").absolutePath}")

    private fun shieldSource(): String =
        sourceOf("java/com/example/service/MonolithShieldService.kt")

    private fun widgetSource(): String =
        sourceOf("java/com/example/widget/MonolithWidgetProvider.kt")

    private fun funBody(source: String, declaration: String): String {
        val start = source.indexOf(declaration)
        assertTrue("declaration not found: $declaration", start >= 0)
        val nextStarts = listOf(
            "\n    suspend fun ",
            "\n    private fun ",
            "\n    override fun ",
            "\n    fun "
        ).map { source.indexOf(it, start + declaration.length) }
            .filter { it > start }
        val end = if (nextStarts.isEmpty()) source.length else nextStarts.min()
        return source.substring(start, end)
    }

    @Test
    fun widgetIdleSelection_printsNextSealThenOpenAndFailsClosed() {
        val body = funBody(widgetSource(), "private suspend fun readStatusLine")
        assertTrue(
            "idle search must read the active row first",
            body.indexOf("getActiveSessionSnapshot()") >= 0
        )
        assertTrue(
            "idle candidates must pass the live filter",
            body.contains(".filter { RoutineSchedule.isLive(it) }")
        )
        val nextIdx = body.indexOf("RoutineSchedule.effectiveNextStartMillis(routine, now)")
        assertTrue("idle line must derive from effectiveNextStartMillis", nextIdx >= 0)
        val sealIdx = body.indexOf("\"OPEN \\u2022 NEXT SEAL", nextIdx)
        assertTrue("next-seal line must print from that derivation", sealIdx > nextIdx)
        val openIdx = body.indexOf("\"OPEN\"", sealIdx)
        assertTrue("no live next start must fall through to plain OPEN", openIdx > sealIdx)
        assertTrue(
            "a failed read must print the fail-closed line, never a guess",
            body.contains("\"OPEN THE APP TO CHECK\"")
        )
    }

    @Test
    fun widgetIsDeclaredInTheManifestWithItsProviderInfo() {
        val manifest = sourceOf("AndroidManifest.xml")
        assertTrue(manifest.contains("android:name=\".widget.MonolithWidgetProvider\""))
        assertTrue(manifest.contains("android.appwidget.action.APPWIDGET_UPDATE"))
        assertTrue(manifest.contains("android:resource=\"@xml/monolith_widget_info\""))
        val info = sourceOf("res/xml/monolith_widget_info.xml")
        assertTrue(info.contains("android:updatePeriodMillis=\"1800000\""))
        assertTrue(info.contains("android:initialLayout=\"@layout/widget_monolith\""))
    }

    @Test
    fun shieldRefreshesTheWidgetAtFlipCompletionAndStop() {
        val source = shieldSource()
        val flipIdx = source.indexOf("if (!protecting && usageGranted) {")
        assertTrue("protecting flip block missing", flipIdx >= 0)
        val flipWindow = source.substring(flipIdx, minOf(flipIdx + 1200, source.length))
        assertTrue(
            "the protecting flip must refresh the widget",
            flipWindow.contains("MonolithWidgetProvider.requestUpdate(applicationContext)")
        )
        val completionIdx = source.indexOf("\"Session Complete\"")
        assertTrue("completion call site missing", completionIdx >= 0)
        val completionWindow =
            source.substring(completionIdx, minOf(completionIdx + 1500, source.length))
        assertTrue(
            "completion must refresh the widget",
            completionWindow.contains("MonolithWidgetProvider.requestUpdate(applicationContext)")
        )
        val stopIdx = source.indexOf("ShieldDispatch.STOP ->")
        assertTrue("STOP dispatch missing", stopIdx >= 0)
        val stopWindow = source.substring(stopIdx, minOf(stopIdx + 900, source.length))
        assertTrue(
            "the STOP dispatch must refresh the widget",
            stopWindow.contains("MonolithWidgetProvider.requestUpdate(applicationContext)")
        )
    }

    @Test
    fun firstForegroundPostIsTheHonestArmingText() {
        // F2: started is not protecting — the first post may only
        // claim arming, and the audit's lying strings stay absent.
        val source = shieldSource()
        assertTrue(source.contains("lastBaseSubtitle = \"Arming shield\\u2026\""))
        assertFalse(source.contains("Monolith Locked"))
        assertFalse(source.contains("Protecting your focus session"))
    }

    @Test
    fun completionCallSitePostsTheExpiringTriple() {
        // F3, adopted from the stacked residue pins (theirs was
        // sound): the call site itself, not parameters a test
        // supplies, must post the triple.
        val source = shieldSource()
        val callIdx = source.indexOf("\"Session Complete\"")
        assertTrue("completion call site missing", callIdx >= 0)
        val window = source.substring(callIdx, minOf(callIdx + 500, source.length))
        val ongoingIdx = window.indexOf("ongoing = false")
        val autoCancelIdx = window.indexOf("autoCancel = true")
        val timeoutIdx = window.indexOf("timeoutAfterMs = COMPLETION_TIMEOUT_MS")
        assertTrue("completion card must post ongoing = false", ongoingIdx >= 0)
        assertTrue("completion card must post autoCancel = true", autoCancelIdx > ongoingIdx)
        assertTrue("completion card must post the F3 timeout", timeoutIdx > autoCancelIdx)
    }

    @Test
    fun badgeChannelShowsTheBadgeAndDeletesTheLegacyChannel() {
        // F4: channels are immutable on-device, so the cure is a
        // v2 channel with the badge ON plus deletion of the legacy
        // badge-off id — both inside createNotificationChannel.
        val body = funBody(shieldSource(), "private fun createNotificationChannel()")
        assertTrue(body.contains("                setShowBadge(true)"))
        assertTrue(body.contains("deleteNotificationChannel(LEGACY_CHANNEL_ID)"))
        // Indented-statement needle on purpose: this function's own
        // comment mentions setShowBadge(false) in prose (the legacy
        // channel it deletes) — a bare contains would fail on the
        // correct code.
        assertFalse(
            "the v2 channel body must not switch the badge back off",
            body.contains("                setShowBadge(false)")
        )
    }

    @Test
    fun badgeNumberIsTheHeldCountWithTheUrgesFallback() {
        // F4: the badge's one number is the held (captured) count —
        // the same number the Deck's HELD chip prints — falling
        // back to this session's urges stopped if the read fails.
        val source = shieldSource()
        val heldIdx = source.indexOf("val heldCount")
        assertTrue("held-count read missing", heldIdx >= 0)
        val window = source.substring(heldIdx, minOf(heldIdx + 700, source.length))
        assertTrue(window.contains("dao.getCapturedNotificationsSnapshot().size"))
        assertTrue(window.contains("heldCount ?: updated.phantomReachesBlocked"))
    }

    @Test
    fun cardExpansionChronometerAndRoutingLiveInTheBuilder() {
        // F5: expanded text, the SYSTEM chronometer, and the
        // shield routing extra are all applied in buildNotification.
        val body = funBody(shieldSource(), "private fun buildNotification(")
        assertTrue(
            body.contains("builder.setStyle(NotificationCompat.BigTextStyle().bigText(bigText))")
        )
        assertTrue(body.contains("builder.setUsesChronometer(true)"))
        assertTrue(body.contains("builder.setChronometerCountDown(true)"))
        assertTrue(body.contains("putExtra(EXTRA_FROM_SHIELD_NOTIFICATION, true)"))
    }

    @Test
    fun shieldCardTapRoutesToTheShieldDestination() {
        // F5 routing, the MainActivity half: a launch carrying the
        // shield extra (and no intercept extra) lands on SHIELD,
        // not the default Deck.
        val main = sourceOf("java/com/example/MainActivity.kt")
        val extraIdx = main.indexOf("MonolithShieldService.EXTRA_FROM_SHIELD_NOTIFICATION, false")
        assertTrue("MainActivity must read the shield routing extra", extraIdx >= 0)
        val window = main.substring(extraIdx, minOf(extraIdx + 900, main.length))
        assertTrue(window.contains("navigateTo(MonolithDestination.SHIELD)"))
    }

    // ------------------------------------------------------------
    // Behavioural pins — the real builder, via the same reflective
    // seam PR #540's expiry pins use.
    // ------------------------------------------------------------

    private fun buildNotification(
        ongoing: Boolean = true,
        autoCancel: Boolean = false,
        timeoutAfterMs: Long = 0,
        bigText: String? = null,
        chronometerEndMillis: Long? = null,
        badgeNumber: Int? = null
    ): Notification {
        val service = Robolectric.buildService(MonolithShieldService::class.java)
            .create().get()
        val method = MonolithShieldService::class.java.getDeclaredMethod(
            "buildNotification",
            String::class.java,
            String::class.java,
            Boolean::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType,
            Long::class.javaPrimitiveType,
            String::class.java,
            Long::class.javaObjectType,
            Int::class.javaObjectType
        )
        method.isAccessible = true
        return method.invoke(
            service, "Monolith", "Standing card",
            ongoing, autoCancel, timeoutAfterMs, bigText, chronometerEndMillis, badgeNumber
        ) as Notification
    }

    @Test
    fun completionTimeoutIsFortyFiveMinutes() {
        assertEquals(45 * 60 * 1000L, MonolithShieldService.COMPLETION_TIMEOUT_MS)
    }

    @Test
    fun completionCardIsNotOngoingAutoCancelsAndExpires() {
        val n = buildNotification(
            ongoing = false,
            autoCancel = true,
            timeoutAfterMs = MonolithShieldService.COMPLETION_TIMEOUT_MS
        )
        assertTrue(n.flags and Notification.FLAG_ONGOING_EVENT == 0)
        assertTrue(n.flags and Notification.FLAG_AUTO_CANCEL != 0)
        assertEquals(MonolithShieldService.COMPLETION_TIMEOUT_MS, n.timeoutAfter)
    }

    @Test
    fun standingCardStaysOngoingWithoutExpiry() {
        val n = buildNotification(ongoing = true, autoCancel = false, timeoutAfterMs = 0L)
        assertTrue(n.flags and Notification.FLAG_ONGOING_EVENT != 0)
        assertTrue(n.flags and Notification.FLAG_AUTO_CANCEL == 0)
        assertEquals(0L, n.timeoutAfter)
    }

    @Test
    fun badgeNumberLandsOnTheNotificationAndAbsenceMeansNoNumber() {
        // F4 behavioural half: setNumber is what launcher badges
        // read. 7 is an arbitrary held count; null must stay 0
        // (Notification.number's no-number value).
        assertEquals(7, buildNotification(badgeNumber = 7).number)
        assertEquals(0, buildNotification(badgeNumber = null).number)
    }

    @Test
    fun chronometerCountsDownToTheSessionEnd() {
        // F5 behavioural half: the countdown is the system's —
        // when carries the end and the chronometer shows.
        val end = 1_700_000_000_000L
        val n = buildNotification(chronometerEndMillis = end)
        assertEquals(end, n.`when`)
        assertTrue(n.extras.getBoolean("android.showChronometer"))
        // With a big text supplied, SOME expanded text must be
        // present. (Under Robolectric the #337 repair path can
        // substitute its missing-grant warning as the big text —
        // presence is the invariant both paths honour.)
        val withBig = buildNotification(bigText = "Intention - 25:00 left", chronometerEndMillis = end)
        assertNotNull(withBig.extras.getCharSequence("android.bigText"))
    }
}
