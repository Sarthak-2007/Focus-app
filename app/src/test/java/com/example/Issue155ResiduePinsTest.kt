package com.example

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Issue #155 — WING-F (BUILDER-5) residue pins.
 *
 * UNVERIFIED-BY-CI: these tests have never executed. CI on this
 * programme is gated by the fleet-wide KSP break, and no local
 * compiling is permitted — so this file is a claim, not a proof,
 * until a CI run on its own head compiles and executes it.
 *
 * What PR #540's first pins leave unpinned (verified by reading
 * both of its test files against the branch's production code at
 * f5f64c59):
 *
 *  - (3a) flip-before-post ORDERING. The existing
 *    `loopCardFollowsTheProtectingFlip` checks only that
 *    `if (!protecting) {` and the arming string exist SOMEWHERE in
 *    MonolithShieldService.kt. The card could move back above the
 *    flip, or the full card could leave the else branch, and that
 *    test would still pass. The tests below pin relative order.
 *  - (F3) the completion CALL SITE. The existing builder tests
 *    feed the (ongoing=false, autoCancel=true, timeout) triple
 *    into buildNotification themselves; if the "Session Complete"
 *    call site stopped passing that triple, they would still
 *    pass. The call site itself is pinned below.
 *  - (3b) summons specificity. The existing summons pin searches
 *    the whole file for `timeoutAfterMs = COMPLETION_TIMEOUT_MS`,
 *    which also matches the completion card's call site — the
 *    summons could lose its timeout and that test would still
 *    pass. Below, the timeout must appear inside the SESSION
 *    COMPLETE summons call itself, and the BREAK OVER summons
 *    (deliberately untimed in the cure) must stay untimed.
 *  - (2) widget WIRING. WidgetSealTruthTest pins the pure rule,
 *    but nothing pins MonolithWidgetProvider actually calling it
 *    with the shield's witness fields, or printing the honest
 *    line instead of SEALED when it returns false. If the
 *    provider reverted to printing SEALED from the row alone,
 *    every WidgetSealTruthTest would still pass.
 *  - (3c) refresh funnels. NO test in PR #540 mentions
 *    requestUpdate at all: routine add/update/delete and both
 *    session-extend verbs could lose their widget refresh and
 *    the whole #155 test estate would stay green. Each funnel
 *    is pinned below, including that the refresh happens AFTER
 *    the write it announces.
 *
 * These are source pins in the same style as PR #540's own
 * source pins — deliberately scoped (call-site windows and
 * function bodies, never whole-file contains) so a match cannot
 * be satisfied by the wrong site.
 */
class Issue155ResiduePinsTest {

    private fun sourceOf(relativePath: String): String = listOf(
        File("src/main/java/$relativePath"),
        File("app/src/main/java/$relativePath"),
        File("../app/src/main/java/$relativePath")
    ).firstOrNull { it.exists() }?.readText()
        ?: error("$relativePath not found from ${File(".").absolutePath}")

    private fun shieldSource(): String =
        sourceOf("com/example/service/MonolithShieldService.kt")

    private fun widgetSource(): String =
        sourceOf("com/example/widget/MonolithWidgetProvider.kt")

    private fun repositorySource(): String =
        sourceOf("com/example/data/MonolithRepository.kt")

    /** The body of one class-level function: from its declaration
     *  to the next class-level function declaration. */
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
    fun flipRunsBeforeTheCardThatFollowsIt() {
        // (3a) ordering: flip block -> witness set -> gate ->
        // arming post inside the gate -> full card only after it,
        // in the else branch.
        val source = shieldSource()
        val flipIdx = source.indexOf("if (!protecting && usageGranted) {")
        assertTrue("protecting flip block missing", flipIdx >= 0)
        val setIdx = source.indexOf("protecting = true", flipIdx)
        assertTrue("flip must set the protecting witness", setIdx > flipIdx)
        val gateIdx = source.indexOf("if (!protecting) {", setIdx)
        assertTrue(
            "card gate must come after the flip, not before it",
            gateIdx > setIdx
        )
        val armingIdx = source.indexOf(
            "updateNotification(\"Monolith\", \"Arming shield\\u2026\")",
            gateIdx
        )
        assertTrue("arming card must be posted from the gate", armingIdx > gateIdx)
        val gateWindow = source.substring(
            gateIdx, minOf(gateIdx + 400, source.length)
        )
        assertTrue(
            "full card must sit in the gate's else branch",
            gateWindow.contains("} else if")
        )
        val fullCardIdx = source.indexOf("title = \"Monolith", gateIdx)
        assertTrue(
            "full session card must post only after the arming branch",
            fullCardIdx > armingIdx
        )
    }

    @Test
    fun completionCallSitePostsTheExpiringTriple() {
        // (F3) the call site itself — not parameters this test
        // supplies — must post the completion card not-ongoing,
        // auto-cancelling, and expiring at the F3 timeout.
        val source = shieldSource()
        val callIdx = source.indexOf("\"Session Complete\"")
        assertTrue("completion call site missing", callIdx >= 0)
        val window = source.substring(callIdx, minOf(callIdx + 500, source.length))
        val ongoingIdx = window.indexOf("ongoing = false")
        val autoCancelIdx = window.indexOf("autoCancel = true")
        val timeoutIdx = window.indexOf("timeoutAfterMs = COMPLETION_TIMEOUT_MS")
        assertTrue("completion card must post ongoing = false", ongoingIdx >= 0)
        assertTrue("completion card must post autoCancel = true", autoCancelIdx > ongoingIdx)
        assertTrue(
            "completion card must post the F3 timeout",
            timeoutIdx > autoCancelIdx
        )
    }

    @Test
    fun summonsTimeoutLivesInsideTheCompletionSummonsCall() {
        // (3b) scoped to the SESSION COMPLETE summons call itself,
        // so the completion card's identical argument elsewhere in
        // the file cannot satisfy this pin.
        val source = shieldSource()
        val callIdx = source.indexOf("\"SESSION COMPLETE\"")
        assertTrue("completion summons call missing", callIdx >= 0)
        val window = source.substring(callIdx, minOf(callIdx + 900, source.length))
        assertTrue(
            "SESSION COMPLETE summons must carry the F3 timeout itself",
            window.contains("timeoutAfterMs = COMPLETION_TIMEOUT_MS")
        )
        // postSummons must apply the timeout it is given.
        val postIdx = source.indexOf("private fun postSummons")
        assertTrue("postSummons missing", postIdx >= 0)
        val postWindow = source.substring(postIdx, minOf(postIdx + 2500, source.length))
        assertTrue(
            "postSummons must apply a positive timeout",
            postWindow.contains("if (timeoutAfterMs > 0) setTimeoutAfter(timeoutAfterMs)")
        )
    }

    @Test
    fun breakOverSummonsStaysUntimed() {
        // The cure deliberately leaves BREAK OVER unchanged: only
        // the completion summons gained a timeout. If a future
        // edit times the break-over summons too, that is a
        // behaviour change this pin makes visible.
        val source = shieldSource()
        val callIdx = source.indexOf("\"BREAK OVER\"")
        assertTrue("break-over summons call missing", callIdx >= 0)
        val callEnd = source.indexOf(")", callIdx)
        assertTrue(callEnd > callIdx)
        val call = source.substring(callIdx, callEnd)
        assertTrue(
            "BREAK OVER summons must not gain a timeout",
            !call.contains("timeoutAfterMs")
        )
    }

    @Test
    fun widgetPrintsSealedOnlyThroughTheTruthRule() {
        // (2) wiring: the provider must consult WidgetSealTruth
        // with the shield's own witness fields for THIS session,
        // print the honest unproven line when it says no, and
        // reach a SEALED print only after that consultation.
        val source = widgetSource()
        val renderIdx = source.indexOf("private suspend fun readStatusLine")
        assertTrue("widget status reader missing", renderIdx >= 0)
        val render = source.substring(renderIdx)
        val ruleIdx = render.indexOf("WidgetSealTruth.sealedProven(")
        assertTrue("widget must consult WidgetSealTruth", ruleIdx >= 0)
        val ruleWindow = render.substring(ruleIdx, minOf(ruleIdx + 600, render.length))
        assertTrue(
            ruleWindow.contains("protecting = MonolithShieldService.protecting")
        )
        assertTrue(
            ruleWindow.contains(
                "protectingSessionId = MonolithShieldService.protectingSessionId"
            )
        )
        assertTrue(ruleWindow.contains("sessionId = session.id"))
        assertTrue(
            ruleWindow.contains("beatWallMillis = session.shieldLastBeatWallMillis")
        )
        val unprovenIdx = render.indexOf(
            "\"SESSION LIVE \\u2022 SHIELD UNPROVEN", ruleIdx
        )
        assertTrue(
            "unproven world must print the honest line",
            unprovenIdx > ruleIdx
        )
        val sealedIdx = render.indexOf("\"SEALED \\u2022", ruleIdx)
        assertTrue(
            "no SEALED print may precede the truth rule's verdict",
            sealedIdx > unprovenIdx
        )
    }

    @Test
    fun routineWritesRefreshTheWidgetAfterTheWrite() {
        // (3c) NEXT SEAL is derived from routines: every routine
        // write funnel must refresh the widget, after its write.
        val source = repositorySource()
        val funnels = listOf(
            "suspend fun updateRoutine" to "dao.updateRoutine(routine.sanitized())",
            "suspend fun addRoutine" to "dao.insertRoutines",
            "suspend fun deleteRoutine" to "dao.deleteRoutineById(id)"
        )
        for ((declaration, writeMarker) in funnels) {
            val body = funBody(source, declaration)
            val writeIdx = body.indexOf(writeMarker)
            val refreshIdx = body.indexOf("MonolithWidgetProvider.requestUpdate(context)")
            assertTrue("$declaration must perform its write", writeIdx >= 0)
            assertTrue(
                "$declaration must refresh the widget after its write",
                refreshIdx > writeIdx
            )
        }
    }

    @Test
    fun sessionExtendsRefreshTheWidgetAfterTheWrite() {
        // (3c) SEALED-until is derived from the session target:
        // both extend verbs must refresh the widget, after the
        // re-signed row is written.
        val source = repositorySource()
        val funnels = listOf(
            "suspend fun extendActiveSessionBySeconds" to "dao.updateSession(reSigned)",
            "suspend fun extendActiveSession(extraMinutes" to "dao.updateSession(updated)"
        )
        for ((declaration, writeMarker) in funnels) {
            val body = funBody(source, declaration)
            val writeIdx = body.indexOf(writeMarker)
            val refreshIdx = body.indexOf("MonolithWidgetProvider.requestUpdate(context)")
            assertTrue("$declaration must perform its write", writeIdx >= 0)
            assertTrue(
                "$declaration must refresh the widget after its write",
                refreshIdx > writeIdx
            )
        }
    }
}
