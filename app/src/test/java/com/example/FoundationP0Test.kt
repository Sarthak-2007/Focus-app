package com.example

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.InkBlue
import com.example.ui.theme.InkGold
import com.example.ui.theme.InkGraphite
import com.example.ui.theme.InkMagenta
import com.example.ui.theme.InkOrange
import com.example.ui.theme.InkRose
import com.example.ui.theme.InkViolet
import com.example.ui.theme.MonolithInks
import com.example.ui.theme.MonolithMotion
import com.example.ui.theme.MonolithType
import com.example.ui.theme.SoraFamily
import com.example.ui.theme.StrongroomGround
import com.example.ui.theme.StrongroomLeaf
import com.example.ui.theme.StrongroomLineWork
import com.example.ui.theme.StrongroomWarmth
import com.example.ui.theme.Typography
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P0 FOUNDATION (BUILD-SR-1) — the design foundation as executable law:
 * the closed seven-ink palette, the Stage 5 type system (scale, weights,
 * tracking), the font BINARIES' own weight classes (the file wins — a
 * swapped font file fails here, not at Sarthak's eye), and the motion
 * primitives' filed character. Sources: Audit A §4 + Stage 5 type-system
 * §1/§5 (incl. A-6.2/A-6.5/A-6.6/A-6.7) + RELIGHT (RelightTest).
 */
class FoundationP0Test {

    // ---------- The palette ----------

    @Test
    fun `the palette is exactly the seven filed inks`() {
        assertEquals(
            listOf(
                Color(0xFFF04FCB), Color(0xFF493EA4), Color(0xFFE2BC39),
                Color(0xFFC84481), Color(0xFFE47E35), Color(0xFF377ED0),
                Color(0xFF34323B)
            ),
            MonolithInks
        )
        assertEquals(Color(0xFFF04FCB), InkMagenta)
        assertEquals(Color(0xFF493EA4), InkViolet)
        assertEquals(Color(0xFFE2BC39), InkGold)
        assertEquals(Color(0xFFC84481), InkRose)
        assertEquals(Color(0xFFE47E35), InkOrange)
        assertEquals(Color(0xFF377ED0), InkBlue)
        assertEquals(Color(0xFF34323B), InkGraphite)
    }

    @Test
    fun `no ink is green or green-dominant`() {
        for (ink in MonolithInks) {
            assertTrue(
                "Ink $ink must satisfy green < maxOf(red, blue) (never green)",
                ink.green < maxOf(ink.red, ink.blue)
            )
        }
    }

    @Test
    fun `semantic roles name the filed inks`() {
        assertEquals(InkViolet, StrongroomGround)
        assertEquals(InkGold, StrongroomLeaf)
        assertEquals(InkGraphite, StrongroomLineWork)
        assertEquals(InkOrange, StrongroomWarmth)
    }

    // ---------- The Stage 5 screen scale ----------

    private fun assertStyle(
        style: androidx.compose.ui.text.TextStyle,
        family: androidx.compose.ui.text.font.FontFamily,
        weight: FontWeight,
        sizeSp: Float,
        leadingSp: Float,
        trackingSp: Float
    ) {
        assertEquals(family, style.fontFamily)
        assertEquals(weight, style.fontWeight)
        assertEquals(sizeSp.sp, style.fontSize)
        assertEquals(leadingSp.sp, style.lineHeight)
        assertEquals(trackingSp.sp, style.letterSpacing)
    }

    @Test
    fun `screen steps SCR-0 to SCR-6 carry the filed sizes weights and tracking`() {
        assertStyle(MonolithType.Scr0, AtkinsonHyperlegibleFamily, FontWeight.Normal, 12f, 16f, 0.72f)
        assertStyle(MonolithType.Scr1, AtkinsonHyperlegibleFamily, FontWeight.Normal, 14f, 20f, 0.28f)
        assertStyle(MonolithType.Scr2, AtkinsonHyperlegibleFamily, FontWeight.Normal, 16f, 24f, 0.16f)
        assertStyle(MonolithType.Scr3, AtkinsonHyperlegibleFamily, FontWeight.Bold, 18f, 24f, 0f)
        assertStyle(MonolithType.Scr4, SoraFamily, FontWeight.SemiBold, 20f, 28f, -0.20f)
        assertStyle(MonolithType.Scr5, SoraFamily, FontWeight.SemiBold, 24f, 32f, -0.24f)
        assertStyle(MonolithType.Scr6, SoraFamily, FontWeight.Bold, 32f, 40f, -0.64f)
    }

    @Test
    fun `telemetry steps ANON-0 to ANON-4 carry the filed sizes weights and tracking`() {
        assertStyle(MonolithType.Anon0, AnonymousProFamily, FontWeight.Normal, 13f, 16f, 0.39f)
        assertStyle(MonolithType.Anon1, AnonymousProFamily, FontWeight.Normal, 15f, 20f, 0f)
        assertStyle(MonolithType.Anon2, AnonymousProFamily, FontWeight.Normal, 17f, 24f, 0.34f)
        assertStyle(MonolithType.Anon3, AnonymousProFamily, FontWeight.Normal, 20f, 28f, 0f)
        assertStyle(MonolithType.Anon4, AnonymousProFamily, FontWeight.Bold, 28f, 32f, 0.56f)
    }

    @Test
    fun `printed steps and the ring step carry the filed sizes and never-negative tracking`() {
        assertStyle(MonolithType.Prt2, SoraFamily, FontWeight.SemiBold, 18f, 25f, 1.08f)
        assertStyle(MonolithType.Prt3, SoraFamily, FontWeight.Bold, 24f, 30f, 1.20f)
        assertStyle(MonolithType.Prt4, SoraFamily, FontWeight.SemiBold, 32f, 40f, 1.28f)
        assertStyle(MonolithType.Prt5, AnonymousProFamily, FontWeight.Bold, 48f, 50f, 0.96f)
        // ENG-1 is the PRT-5 figure class, Anonymous Pro 700 (§1.3, A-6.6).
        assertEquals(MonolithType.Prt5, MonolithType.Eng1)
        for (style in listOf(MonolithType.Prt2, MonolithType.Prt3, MonolithType.Prt4, MonolithType.Prt5)) {
            assertTrue("printed tracking is never negative (§5.3)", style.letterSpacing.value > 0f)
        }
    }

    @Test
    fun `weight truth - Atkinson and Anonymous carry 400 and 700 only, Sora 600 and 700 only`() {
        val atkinsonWeights = listOf(
            MonolithType.Scr0, MonolithType.Scr1, MonolithType.Scr2, MonolithType.Scr3, MonolithType.Frm1
        ).map { it.fontWeight }
        assertTrue(atkinsonWeights.all { it == FontWeight.Normal || it == FontWeight.Bold })
        val anonWeights = listOf(
            MonolithType.Anon0, MonolithType.Anon1, MonolithType.Anon2,
            MonolithType.Anon3, MonolithType.Anon4, MonolithType.Prt5, MonolithType.Eng1
        ).map { it.fontWeight }
        assertTrue(anonWeights.all { it == FontWeight.Normal || it == FontWeight.Bold })
        val soraWeights = listOf(
            MonolithType.Scr4, MonolithType.Scr5, MonolithType.Scr6,
            MonolithType.Prt2, MonolithType.Prt3, MonolithType.Prt4
        ).map { it.fontWeight }
        assertTrue(soraWeights.all { it == FontWeight.SemiBold || it == FontWeight.Bold })
    }

    @Test
    fun `descriptor steps carry their relations, not invented sizes`() {
        // PLT-1 and PLT-2 are one hand: same face (§6 same-hand rule).
        assertEquals(MonolithType.Plt2.face, MonolithType.Plt1.face)
        assertEquals("Anonymous Pro", MonolithType.Plt1.face)
        assertEquals("Atkinson Hyperlegible", MonolithType.Hld1.face)
        assertEquals(400, MonolithType.Hld1.weight)
        // The notification's wording is RULED (type-system A-6.2).
        assertEquals("Locked until the hour", MonolithType.Hld1Wording)
        // FRM-1: Atkinson 400 at the SCR-2 size class, size equivalence only.
        assertEquals(AtkinsonHyperlegibleFamily, MonolithType.Frm1.fontFamily)
        assertEquals(FontWeight.Normal, MonolithType.Frm1.fontWeight)
        assertEquals(16.sp, MonolithType.Frm1.fontSize)
    }

    @Test
    fun `material typography slots draw only from the Stage 5 steps`() {
        assertEquals(MonolithType.Scr6, Typography.displayLarge)
        assertEquals(MonolithType.Scr5, Typography.displayMedium)
        assertEquals(MonolithType.Scr4, Typography.displaySmall)
        assertEquals(MonolithType.Scr2, Typography.bodyLarge)
        assertEquals(MonolithType.Scr1, Typography.bodyMedium)
        assertEquals(MonolithType.Scr0, Typography.bodySmall)
        assertEquals(MonolithType.Scr3, Typography.labelLarge)
        assertEquals(FontWeight.Normal, Typography.bodyLarge.fontWeight)
    }

    // ---------- The font binaries themselves (the file wins) ----------

    private fun fontDir(): File {
        var dir: File? = File(System.getProperty("user.dir"))
        repeat(6) {
            val d = dir ?: return@repeat
            for (candidate in listOf(File(d, "src/main/res/font"), File(d, "app/src/main/res/font"))) {
                if (candidate.isDirectory) return candidate
            }
            dir = d.parentFile
        }
        throw AssertionError("res/font not found upward from ${System.getProperty("user.dir")}")
    }

    /** usWeightClass from the OS/2 table, parsed raw — no font library. */
    private fun ttfWeightClass(file: File): Int {
        val b = file.readBytes()
        fun u16(off: Int) = ((b[off].toInt() and 0xFF) shl 8) or (b[off + 1].toInt() and 0xFF)
        fun u32(off: Int) = ((b[off].toInt() and 0xFF) shl 24) or ((b[off + 1].toInt() and 0xFF) shl 16) or
            ((b[off + 2].toInt() and 0xFF) shl 8) or (b[off + 3].toInt() and 0xFF)
        val numTables = u16(4)
        for (i in 0 until numTables) {
            val rec = 12 + i * 16
            if (String(b, rec, 4, Charsets.US_ASCII) == "OS/2") {
                return u16(u32(rec + 8) + 4)
            }
        }
        throw AssertionError("no OS/2 table in ${file.name}")
    }

    @Test
    fun `bundled font binaries carry exactly the filed weight classes`() {
        val dir = fontDir()
        assertEquals(600, ttfWeightClass(File(dir, "sora_semibold.ttf")))
        assertEquals(700, ttfWeightClass(File(dir, "sora_bold.ttf")))
        assertEquals(400, ttfWeightClass(File(dir, "atkinson_hyperlegible.ttf")))
        assertEquals(700, ttfWeightClass(File(dir, "atkinson_hyperlegible_bold.ttf")))
        assertEquals(400, ttfWeightClass(File(dir, "anonymous_pro.ttf")))
        assertEquals(700, ttfWeightClass(File(dir, "anonymous_pro_bold.ttf")))
    }

    // ---------- The motion primitives ----------

    @Test
    fun `motion primitives carry mass detent and settle character, not defaults`() {
        // The knock is a dead stop: critically damped, the stiffest spring.
        assertEquals(1.0f, MonolithMotion.KnockSpring.dampingRatio, 0.0f)
        assertTrue(MonolithMotion.KnockSpring.stiffness > MonolithMotion.BoltSpring.stiffness)
        // The bolt is near-critical; the door's weight is the softest mass.
        assertEquals(0.92f, MonolithMotion.BoltSpring.dampingRatio, 0.0f)
        assertTrue(MonolithMotion.DoorWeightSpring.stiffness < MonolithMotion.DrumSpring.stiffness)
        // The drum and wheel are under-damped: they are caught by detents.
        assertTrue(MonolithMotion.DrumSpring.dampingRatio < 1.0f)
        assertTrue(MonolithMotion.WheelSpring.dampingRatio < MonolithMotion.DrumSpring.dampingRatio)
    }

    @Test
    fun `the sealing is three descending clunks and the lifting mirrors it`() {
        val seal = MonolithMotion.SealClunks
        assertEquals(3, seal.size)
        assertTrue(seal.zipWithNext().all { (a, b) -> b.atMs > a.atMs && b.weight < a.weight })
        val lift = MonolithMotion.LiftClunks
        assertEquals(3, lift.size)
        assertTrue(lift.zipWithNext().all { (a, b) -> b.atMs > a.atMs && b.weight > a.weight })
        assertEquals(seal.map { it.atMs }, lift.map { it.atMs })
    }
}
