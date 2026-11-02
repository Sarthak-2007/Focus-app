package com.example

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.MonolithInks
import com.example.ui.theme.MonolithType
import com.example.ui.theme.SoraFamily
import com.example.ui.theme.StrongroomGround
import com.example.ui.theme.StrongroomLeaf
import com.example.ui.theme.StrongroomLineWork
import com.example.ui.theme.StrongroomWarmth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TEST-SR-1 (T40) — P0 TOKEN / TYPE CONFORMANCE, the half the
 * lane's own FoundationP0Test does not already assert.
 *
 * Obligations: ledger C-6 (the type system WHOLE — scales
 * SCR/ANON/PRT/PLT/HLD/FRM, no surface invents a size) and F-1
 * (palette ONLY, seven inks, never green). The lane's tests pin
 * each step's exact size/weight/tracking and each ink's exact hex;
 * this file pins the CLOSURE of both systems:
 *
 *  - the scale is complete and closed — the set of styles that
 *    exist is exactly the filed set, every size drawn from the
 *    filed size union, every family's weights inside its filed
 *    weight law, across ALL steps (not only the sampled ones);
 *  - the palette is closed under a single closure predicate, and
 *    the predicate is proven non-vacuous on fixtures: a green hex
 *    placed in a fixture palette FAILS the suite's closure check,
 *    so a green hex ever reaching the real palette fails with it.
 *
 * Authored under METHODS-SR-4's method (top-level, own @Test,
 * package com.example). Reflection filters synthetic/$-fields.
 */
class FoundationTokenTypeConformanceTest {

    // ------------------------------------------------------------------
    // F-1 — palette closure
    // ------------------------------------------------------------------

    /** The seven filed inks, by hex (ledger F-1 / E-7 / E-8). */
    private val filedSeven: Set<Color> = setOf(
        Color(0xFFF04FCB), Color(0xFF493EA4), Color(0xFFE2BC39),
        Color(0xFFC84481), Color(0xFFE47E35), Color(0xFF377ED0),
        Color(0xFF34323B)
    )

    /** Green-dominance: the green channel beats BOTH other
     *  channels. (His colour rule, Oct 10 2026: never green or
     *  anything close to green.) */
    private fun isGreenDominant(c: Color): Boolean =
        c.green > c.red && c.green > c.blue

    /** THE closure predicate — one rule, applied identically to
     *  the production palette and to every fixture below. */
    private fun paletteIsClosed(colors: List<Color>): Boolean =
        colors.size == 7 &&
            colors.toSet().size == 7 &&
            colors.all { it in filedSeven } &&
            colors.none { isGreenDominant(it) }

    @Test
    fun palette_theProductionPaletteIsClosed() {
        assertTrue(paletteIsClosed(MonolithInks))
    }

    @Test
    fun palette_aGreenHexInAFixture_failsTheClosure() {
        // The charged fixture test: green entering a palette in
        // ANY position — added as an eighth, or swapped in for a
        // filed ink — fails the same predicate the production
        // palette passes. If a future change adds green to
        // MonolithInks, palette_theProductionPaletteIsClosed fails
        // by this identical rule.
        val greens = listOf(
            Color(0xFF00FF00), Color(0xFF008000), Color(0xFF00C853),
            Color(0xFF4CAF50), Color(0xFF2E7D32)
        )
        for (green in greens) {
            assertTrue("fixture green $green must read as green-dominant", isGreenDominant(green))
            assertFalse(
                "eighth-ink fixture with $green must fail closure",
                paletteIsClosed(MonolithInks + green)
            )
            assertFalse(
                "swapped-in fixture with $green must fail closure",
                paletteIsClosed(MonolithInks.drop(1) + green)
            )
        }
        // And the predicate is not trivially false: a fixture of
        // exactly the filed seven, in any order, passes.
        assertTrue(paletteIsClosed(filedSeven.toList().reversed()))
    }

    @Test
    fun palette_semanticRolesDrawOnlyFromTheSeven() {
        // The Strongroom roles (ground/leaf/line-work/warmth) may
        // name no colour outside the closed palette.
        for (role in listOf(StrongroomGround, StrongroomLeaf, StrongroomLineWork, StrongroomWarmth)) {
            assertTrue("role colour $role must be one of the seven inks", role in MonolithInks)
        }
    }

    // ------------------------------------------------------------------
    // C-6 — the type scale is complete, closed, and unincreasable
    // ------------------------------------------------------------------

    /** Every TextStyle MonolithType declares, by member name. */
    private fun declaredStyles(): Map<String, TextStyle> =
        MonolithType::class.java.declaredFields
            .filter { !it.name.startsWith("$") && it.type == TextStyle::class.java }
            .associate { field ->
                field.isAccessible = true
                field.name to (field.get(MonolithType) as TextStyle)
            }

    @Test
    fun typeScale_theStyleSetIsExactlyTheFiledSet() {
        // SCR-0..6 · ANON-0..4 · PRT-2..4 · ENG-1 · FRM-1 — nothing
        // missing, and nothing extra: an invented step added to
        // MonolithType fails this test by name. PRT-5 is NOT here:
        // on TYPE-SR-5's anchored cure D-01 (A-6.6, PR #842 head
        // 69fb176e) it survives only as ENG-1's size-class CITATION
        // — a StepDescriptor, never a consumable style; the seam
        // was agreed by letter with BUILD-SR-1 and this census
        // re-aimed the same act. It is censused with the
        // descriptors below.
        assertEquals(
            setOf(
                "Scr0", "Scr1", "Scr2", "Scr3", "Scr4", "Scr5", "Scr6",
                "Anon0", "Anon1", "Anon2", "Anon3", "Anon4",
                "Prt2", "Prt3", "Prt4", "Eng1", "Frm1"
            ),
            declaredStyles().keys
        )
    }

    @Test
    fun typeScale_noStyleInventsASize_everySizeIsAFiledStepSize() {
        // The filed size union, from the Stage 5 steps as the
        // lane's own tests pin them. A style set at any other size
        // — a surface inventing a size inside the system — fails.
        val filedSizes = setOf(12f, 13f, 14f, 15f, 16f, 17f, 18f, 20f, 24f, 28f, 32f, 48f)
        for ((name, style) in declaredStyles()) {
            assertTrue(
                "$name size ${style.fontSize.value}sp is not a filed step size",
                style.fontSize.value in filedSizes
            )
            // Leading never crowds its size on any step.
            assertTrue("$name lineHeight crowds its size", style.lineHeight.value >= style.fontSize.value)
        }
    }

    @Test
    fun typeScale_weightLawHoldsAcrossEveryStyle_notOnlyTheSampledOnes() {
        // Atkinson 400/700 only · Anonymous Pro 400/700 only ·
        // Sora 600/700 only — checked over the whole declared set,
        // so a step added tomorrow at a rogue weight fails here
        // even if no one writes it a named test.
        for ((name, style) in declaredStyles()) {
            when (style.fontFamily) {
                AtkinsonHyperlegibleFamily, AnonymousProFamily -> assertTrue(
                    "$name weight ${style.fontWeight} breaks the 400/700-only law",
                    style.fontWeight == FontWeight.Normal || style.fontWeight == FontWeight.Bold
                )
                SoraFamily -> assertTrue(
                    "$name weight ${style.fontWeight} breaks the Sora 600/700-only law",
                    style.fontWeight == FontWeight.SemiBold || style.fontWeight == FontWeight.Bold
                )
                else -> throw AssertionError("$name uses an unfiled family: ${style.fontFamily}")
            }
        }
    }

    @Test
    fun typeScale_descriptorSteps_carryTheirRelations() {
        // PLT/HLD are descriptor steps (relations, not fixed
        // styles): the plate steps are one hand (same face, same
        // weight), the bracket never lighter than the plate line,
        // and HLD-1 is Atkinson 400 with its ruled wording.
        assertEquals(MonolithType.Plt1.face, MonolithType.Plt2.face)
        assertEquals(MonolithType.Plt1.weight, MonolithType.Plt2.weight)
        assertEquals("Anonymous Pro", MonolithType.Plt2.face)
        assertEquals("Atkinson Hyperlegible", MonolithType.Hld1.face)
        assertEquals(400, MonolithType.Hld1.weight)
        assertEquals("Locked until the hour", deckString("copy_10_2_notification")) // RE-POINTED (BUILD-SR-23): Hld1Wording mirror retired; the ruled line is pinned at the single copy source
        // PRT-5 the citation (A-6.6): Anonymous Pro 700, the 48pt
        // size-class ENG-1 binds to — and ENG-1 itself stands as
        // the one consumable 48pt style.
        assertEquals("PRT-5", MonolithType.Prt5.step)
        assertEquals("Anonymous Pro", MonolithType.Prt5.face)
        assertEquals(700, MonolithType.Prt5.weight)
        assertEquals(48f, MonolithType.Eng1.fontSize.value)
        // The descriptor census is closed too: exactly PLT-1,
        // PLT-2, HLD-1, PRT-5 are carried as descriptors.
        val descriptors = MonolithType::class.java.declaredFields
            .filter { !it.name.startsWith("$") }
            .filter { it.type.simpleName == "StepDescriptor" }
            .map { it.name }
            .toSet()
        assertEquals(setOf("Plt1", "Plt2", "Hld1", "Prt5"), descriptors)
    }
}

// The single copy source, read as the file the app ships (BUILD-SR-23 re-point).
private fun deckString(key: String): String {
    val xml = java.io.File("src/main/res/values/strongroom_strings.xml").readText()
    val match = Regex("<string name=\"" + key + "\">(.*?)</string>", RegexOption.DOT_MATCHES_ALL)
        .find(xml) ?: error("deck key missing from the single source: $key")
    return match.groupValues[1]
}
