package com.example

import com.example.service.UsageAccess
import com.example.service.Warrant
import com.example.service.WarrantState
import com.example.ui.components.RefusalAction
import com.example.ui.components.endsAtText
import com.example.ui.components.imprintTenantText
import com.example.ui.components.refusalSentenceFor
import com.example.ui.components.seamMarksFor
import com.example.ui.views.SlideGestureMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Slice D (Marlowe's lane): Teo's assertion list, the unit-testable
 * core. A9 (the slide's arithmetic) is covered exhaustively here.
 * A4/A5 hold by construction (the cap words are compile-time
 * constants; the host latch is composition state). A6 stood
 * superseded while the Live Wire stamp had no age; Truth Car 3
 * (UNA2-03) un-supersedes it — the warrant carries its read instant
 * now, and the stale prints are asserted in TruthCar3InterceptTest.
 * A1/A2/A3/A7/A10 are render- and
 * device-class checks, except A10's freeze, which is covered by
 * construction in the Deck host and asserted here at the derivation
 * level (imprintTenantText's sealed branch reads only the frozen
 * count it is handed).
 */
class SlideGestureMathTest {

    @Test
    fun gearIsIdentityUpToTheGate() {
        assertEquals(0f, SlideGestureMath.gear(0f), 0.0001f)
        assertEquals(0.34f, SlideGestureMath.gear(0.34f), 0.0001f)
        assertEquals(0.68f, SlideGestureMath.gear(0.68f), 0.0001f)
    }

    @Test
    fun gearDampsBeyondTheGateSoTheFingerCommitsAt092() {
        // Vega: displayed 0.84 (the catch) sits at finger p = 0.92.
        assertEquals(0.84f, SlideGestureMath.gear(0.92f), 0.0001f)
        // A full-throw finger displays 0.68 + 0.32 * 2/3 = 0.89333.
        assertEquals(0.89333f, SlideGestureMath.gear(1f), 0.001f)
        // The gear is memoryless: same finger, same place, always.
        assertEquals(SlideGestureMath.gear(0.8f), SlideGestureMath.gear(0.8f), 0f)
    }

    @Test
    fun travelLimitsByWarrantState() {
        assertEquals(1f, SlideGestureMath.travelLimit(WarrantState.LIVE), 0f)
        assertEquals(0.84f, SlideGestureMath.travelLimit(WarrantState.UNPROVEN), 0f)
        assertEquals(0.65f, SlideGestureMath.travelLimit(WarrantState.OFF), 0f)
    }

    @Test
    fun commitNeedsCatchAndLiveWireAtRelease() {
        // Issue #125 F2 (Sarthak's ruling): the commit is judged
        // at RELEASE — displayed at or past the catch on a LIVE
        // release-frame read. The predicate has no contact term:
        // crossing the catch under contact seats nothing any
        // more, and drag-back below the catch before release
        // cancels (the view's endDrag owns both calls).
        assertTrue(SlideGestureMath.commitsOnRelease(0.84f, WarrantState.LIVE))
        assertTrue(SlideGestureMath.commitsOnRelease(0.90f, WarrantState.LIVE))
        assertFalse(SlideGestureMath.commitsOnRelease(0.839f, WarrantState.LIVE))
        assertFalse(SlideGestureMath.commitsOnRelease(0.90f, WarrantState.UNPROVEN))
        assertFalse(SlideGestureMath.commitsOnRelease(0.90f, WarrantState.OFF))
    }

    @Test
    fun brushFloorIsExclusiveAt90ms() {
        assertTrue(SlideGestureMath.isBrush(0L))
        assertTrue(SlideGestureMath.isBrush(89L))
        assertFalse(SlideGestureMath.isBrush(90L))
        assertFalse(SlideGestureMath.isBrush(200L))
    }

    @Test
    fun schmittLatchFiresOncePerCrossing() {
        // Up-cross at 0.680 fires +1 and arms.
        val (armed1, fired1) = SlideGestureMath.schmitt(false, 0.68f)
        assertTrue(armed1)
        assertEquals(1, fired1)
        // Hover inside the dead-band: no re-fire, stays armed.
        val (armed2, fired2) = SlideGestureMath.schmitt(true, 0.67f)
        assertTrue(armed2)
        assertEquals(0, fired2)
        // Retreat cross at 0.660 fires -1 and disarms.
        val (armed3, fired3) = SlideGestureMath.schmitt(true, 0.66f)
        assertFalse(armed3)
        assertEquals(-1, fired3)
        // Below the gate, unarmed: nothing.
        val (armed4, fired4) = SlideGestureMath.schmitt(false, 0.5f)
        assertFalse(armed4)
        assertEquals(0, fired4)
    }

    @Test
    fun meshCurveEndpointsAndMonotonicity() {
        assertEquals(0f, SlideGestureMath.meshCurve(0f), 0.0001f)
        assertEquals(1f, SlideGestureMath.meshCurve(1f), 0.0001f)
        var prev = 0f
        var u = 0.05f
        while (u <= 1.0f) {
            val y = SlideGestureMath.meshCurve(u)
            assertTrue("curve must be monotonic: $y after $prev", y >= prev)
            prev = y
            u += 0.05f
        }
        // Exit slope is exactly 0 by the curve's second control point:
        // the last 1% of time buys almost no travel.
        assertTrue(SlideGestureMath.meshCurve(0.99f) > 0.98f)
    }
}

class StripDerivationsTest {

    private fun warrant(
        state: WarrantState,
        bound: Boolean = true,
        stamped: Boolean = true,
        usage: UsageAccess = UsageAccess.ALLOWED,
        readMono: Long = 0L,
        residencyStartMono: Long = 0L
    ) = Warrant(
        state = state,
        a11yBound = bound,
        a11yStampedThisResidency = stamped,
        shieldProtecting = false,
        usage = usage,
        readMono = readMono,
        residencyStartMono = residencyStartMono
    )

    @Test
    fun stampGradesFollowRosasDefinitions() {
        assertEquals(
            "BIND HELD · THIS VISIT" to "PROOF: LIVE BIND",
            seamMarksFor(warrant(WarrantState.LIVE))
        )
        // Room (i): held, unstamped.
        assertEquals(
            "BIND HELD · NOT SEEN" to "PROOF: BIND ONLY",
            seamMarksFor(warrant(WarrantState.UNPROVEN, stamped = false))
        )
        // Held and stamped, a grant unproven: still BIND ONLY,
        // residency word THIS VISIT.
        assertEquals(
            "BIND HELD · THIS VISIT" to "PROOF: BIND ONLY",
            seamMarksFor(warrant(WarrantState.UNPROVEN, usage = UsageAccess.UNSET))
        )
        // Room (ii): unconfirmed — nothing proven either way.
        // NOTE (Rosa, review F1): this state is function-correct but
        // production-unreachable on the shipped substrate —
        // liveWarrant maps every !a11yBound to OFF, so the
        // process-start window prints OFF CONFIRMED on mere absence
        // until the substrate gains Rosa's tri-state bind leg.
        // Flagged to Ren with her evidence; the function is ready.
        assertEquals(
            "BIND UNCONFIRMED" to "PROOF: NONE",
            seamMarksFor(warrant(WarrantState.UNPROVEN, bound = false, stamped = false, usage = UsageAccess.UNSET))
        )
        // OFF: the bind fact is stated truthfully from the legs.
        assertEquals(
            "BIND NOT HELD" to "PROOF: OFF CONFIRMED",
            seamMarksFor(warrant(WarrantState.OFF, bound = false, stamped = false))
        )
        assertEquals(
            "BIND HELD · THIS VISIT" to "PROOF: OFF CONFIRMED",
            seamMarksFor(warrant(WarrantState.OFF, usage = UsageAccess.DENIED))
        )
        assertEquals(
            "BIND HELD · NOT SEEN" to "PROOF: OFF CONFIRMED",
            seamMarksFor(warrant(WarrantState.OFF, stamped = false, usage = UsageAccess.DENIED))
        )
        assertNull(seamMarksFor(null))
    }

    @Test
    fun causeTablePicksOneSentenceByCausalPriority() {
        // A11y death outranks everything. Timed overload with a
        // fresh readMono/nowMono pair (TECH-1 B-Blocker-1): the
        // no-clock overload reads SystemClock, a throwing stub in
        // plain JVM tests. This warrant carries no residency, so
        // no cold-open grace silence is owed and OFF stands at once.
        assertEquals(
            R.string.sentence_blocking_off,
            refusalSentenceFor(
                warrant(WarrantState.OFF, bound = false, stamped = false, usage = UsageAccess.DENIED, readMono = 10_000L),
                10_000L
            )?.textRes
        )
        // Bound but unstamped, usage ALLOWED, and NO residency
        // carried (this helper's hand-built warrant): the cured
        // table (issue #7) prints BLOCKING IS OFF at once.
        // WHY THE OLD EXPECTATION WAS THE BUG: this row used to
        // expect RefusalAction.RETRY_CHECK — the fog sentence that
        // printed "can't check, tap to retry" AT REST on a cold
        // open, forever, on a guard whose only fault was that no
        // a11y event had happened yet; the retry could never fix
        // it, because nothing was broken that a tap repairs. The
        // fog row is deleted, not reworded: an unstamped warrant
        // carrying no residency is age-unknown, never fresh, so no
        // grace silence is owed and bound-but-blind is off in fact.
        // (A carried, in-grace residency IS silent — that half of
        // the cured table is proven in ColdStartWarrantTest.)
        val unstampedNoResidency = refusalSentenceFor(
            warrant(WarrantState.UNPROVEN, stamped = false), 10_000L
        )
        assertEquals(R.string.sentence_blocking_off, unstampedNoResidency?.textRes)
        assertEquals(RefusalAction.FIX_ACCESSIBILITY, unstampedNoResidency?.action)
        // Usage positively denied names its own pane.
        val usageSentence = refusalSentenceFor(warrant(WarrantState.OFF, usage = UsageAccess.DENIED, readMono = 10_000L), 10_000L)
        assertEquals(R.string.sentence_usage_off, usageSentence?.textRes)
        assertEquals(RefusalAction.FIX_USAGE, usageSentence?.action)
        // Usage never granted (issue #6 C3): the sentence names
        // the Usage pane — the door its tap has always opened.
        val unsetSentence = refusalSentenceFor(warrant(WarrantState.UNPROVEN, usage = UsageAccess.UNSET, readMono = 10_000L), 10_000L)
        assertEquals(R.string.sentence_usage_off, unsetSentence?.textRes)
        assertEquals(RefusalAction.FIX_USAGE, unsetSentence?.action)
        // UNSET + unstamped + UNPROVEN (the C3 lie's other door): the
        // RETRY row must not claim this warrant — its tap opens the
        // Usage pane, so the Usage sentence is the only honest print.
        val unsetUnstamped = refusalSentenceFor(warrant(WarrantState.UNPROVEN, stamped = false, usage = UsageAccess.UNSET, readMono = 10_000L), 10_000L)
        assertEquals(R.string.sentence_usage_off, unsetUnstamped?.textRes)
        assertEquals(RefusalAction.FIX_USAGE, unsetUnstamped?.action)
        // Healthy wire: the slot stands empty.
        assertNull(refusalSentenceFor(warrant(WarrantState.LIVE, readMono = 10_000L), 10_000L))
        assertNull(refusalSentenceFor(null, 10_000L))
    }

    @Test
    fun imprintTenantNeverPrintsWillShutOverADeadWire() {
        // Truth Car 3 (UNA2-02/04; Hale W-23/W-24): the tenant
        // takes the SHUT/PEEK split in the Shield's nouns. The
        // sealed branch prints its frozen verdict over the frozen
        // state; over a wire that reads OFF at the print instant
        // the words stand but carry their tense — " · AS SEALED".
        assertEquals(
            "7 HELD · THIS PHONE ONLY",
            imprintTenantText(true, 7, 0, WarrantState.OFF)
        )
        assertEquals(
            "1 HELD · THIS PHONE ONLY",
            imprintTenantText(true, 1, 0, WarrantState.LIVE)
        )
        assertEquals(
            "7 HELD · THIS PHONE ONLY · AS SEALED",
            imprintTenantText(true, 7, 0, WarrantState.LIVE, wireNow = WarrantState.OFF)
        )
        assertNull(imprintTenantText(true, null, 0, WarrantState.LIVE))
        assertNull(imprintTenantText(true, 7, null, WarrantState.LIVE))
        assertEquals(
            "1 WILL SHUT",
            imprintTenantText(false, 1, 0, WarrantState.LIVE)
        )
        assertEquals(
            "12 WILL SHUT",
            imprintTenantText(false, 12, 0, WarrantState.LIVE)
        )
        assertNull(imprintTenantText(false, 12, 0, WarrantState.OFF))
        assertNull(imprintTenantText(false, 12, 0, WarrantState.UNPROVEN))
        assertNull(imprintTenantText(false, 12, 0, null))
        assertNull(imprintTenantText(false, null, 0, WarrantState.LIVE))
        assertNull(imprintTenantText(false, 12, null, WarrantState.LIVE))
    }

    @Test
    fun imprintTenantPrintsTheHonestShutPeekSplit() {
        // UNA2-02 (Pruefer's fixture): 3 SHUT + 2 PEEK describe
        // the same world in both tenses — and a PEEK app is never
        // counted under WILL SHUT or HELD.
        assertEquals(
            "3 WILL SHUT · 2 PEEK",
            imprintTenantText(false, 3, 2, WarrantState.LIVE)
        )
        assertEquals(
            "3 HELD · 2 PEEK · THIS PHONE ONLY",
            imprintTenantText(true, 3, 2, WarrantState.LIVE)
        )
        assertEquals(
            "7 WILL SHUT · 2 PEEK",
            imprintTenantText(false, 7, 2, WarrantState.LIVE)
        )
        // W-23 omission law: a zero segment never prints.
        assertEquals(
            "1 PEEK",
            imprintTenantText(false, 0, 1, WarrantState.LIVE)
        )
        assertEquals(
            "3 PEEK · THIS PHONE ONLY",
            imprintTenantText(true, 0, 3, WarrantState.LIVE)
        )
        assertNull(imprintTenantText(false, 0, 0, WarrantState.LIVE))
        assertNull(imprintTenantText(true, 0, 0, WarrantState.LIVE))
    }

    @Test
    fun endsAtIsAPureFunctionOfTheEndInstant() {
        val millis = 1_700_000_000_000L
        // #88: endsAtText takes the device's 12/24-hour choice as a
        // boolean, so both branches pin exactly (test JVM default
        // locale is en, matching the Locale.US expectations below).
        val expected24 = "ENDS " + SimpleDateFormat("HH:mm", Locale.US).format(Date(millis))
        assertEquals(expected24, endsAtText(true, millis))
        assertEquals(endsAtText(true, millis), endsAtText(true, millis))
        val expected12 = "ENDS " + SimpleDateFormat("h:mm a", Locale.US).format(Date(millis))
        assertEquals(expected12, endsAtText(false, millis))
    }
}
