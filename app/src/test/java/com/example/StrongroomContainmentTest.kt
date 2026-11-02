package com.example

import android.content.Context
import android.os.Build
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import com.example.data.MonolithDatabase
import com.example.data.StrongroomStore
import com.example.data.toSigningRecord
import com.example.domain.StrongroomContainment
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomRouting
import com.example.service.StrongroomEngineBridge
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.time.LocalTime

/**
 * CONTAIN — the seal's containment, pinned at every layer the two
 * enforcement doors execute:
 *
 *  1. THE VERDICT MATRIX (pure): StrongroomContainment over the
 *     sealed day's signed sets — and its agreement with
 *     StrongroomRouting.routeReach, so the doors and the bridge
 *     can never drift apart.
 *  2. THE ENFORCEMENT TRACE (Robolectric, production store): a
 *     signed day alone arms nothing; the SWING makes the seal
 *     stand in the store the doors read; a locked reach fed into
 *     the bridge — exactly what both doors do on a KNOCK verdict —
 *     returns KNOCK and strikes exactly one REACH punch; an
 *     allowed reach passes to the antechamber and punches nothing.
 *  3. THE HOME STANDING (manifest pin): MainActivity carries
 *     HOME + DEFAULT beside LAUNCHER, so the sealed face is a
 *     lawful home while a seal stands.
 *
 * Device behaviour (the eject, the face coming forward, the knock
 * heard on the phone) stands UNPROVEN-ON-DEVICE (Audit A G-7), as
 * with every Strongroom wiring claim.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StrongroomContainmentTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val storageContext: Context
        get() {
            val appCtx = context.applicationContext
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                appCtx.createDeviceProtectedStorageContext() ?: appCtx
            } else {
                appCtx
            }
        }

    @Before
    fun coldStart() {
        MonolithDatabase.closeAndResetForTests()
        storageContext.deleteDatabase("monolith_tactile_db")
        context.deleteDatabase("monolith_tactile_db")
    }

    @After
    fun teardown() {
        MonolithDatabase.closeAndResetForTests()
        storageContext.deleteDatabase("monolith_tactile_db")
        context.deleteDatabase("monolith_tactile_db")
    }

    private fun store(): StrongroomStore =
        StrongroomStore(com.example.AppContainer.dao(context))

    // ----------------------------------------------------------------
    // 1. The verdict matrix
    // ----------------------------------------------------------------

    private val allowed = setOf("com.notes.app", "com.peek.app")

    private fun verdict(
        pkg: String,
        floor: Boolean = false,
        home: Boolean = false,
        sysUi: Boolean = false
    ) = StrongroomContainment.verdict(
        packageName = pkg,
        ownPackage = "com.aistudio.monolith.kxtpzr",
        allowedPackages = allowed,
        isSafetyFloor = floor,
        isHomePackage = home,
        isSystemUi = sysUi
    )

    @Test
    fun verdict_ownApp_systemUi_safetyFloor_allowed_allPass() {
        assertEquals(
            StrongroomContainment.Verdict.PASS,
            verdict("com.aistudio.monolith.kxtpzr")
        )
        assertEquals(
            StrongroomContainment.Verdict.PASS,
            verdict("com.android.systemui", sysUi = true)
        )
        // A contact's call rings THROUGH: its surfaces are floor.
        assertEquals(
            StrongroomContainment.Verdict.PASS,
            verdict("com.android.incallui", floor = true)
        )
        assertEquals(
            StrongroomContainment.Verdict.PASS,
            verdict("com.notes.app")
        )
        assertEquals(
            StrongroomContainment.Verdict.PASS,
            verdict("com.peek.app")
        )
    }

    @Test
    fun verdict_lockedAndUnlisted_knock_foreignLauncher_holdsSilently() {
        assertEquals(
            StrongroomContainment.Verdict.KNOCK,
            verdict("com.video.app")
        )
        // Born locked: an app on neither list is a locked reach.
        assertEquals(
            StrongroomContainment.Verdict.KNOCK,
            verdict("com.never.signed.app")
        )
        assertEquals(
            StrongroomContainment.Verdict.HOLD_HOME,
            verdict("com.android.launcher3", home = true)
        )
        // The floor outranks the home fact: a package that is both
        // (never on a real phone) still passes — safety first.
        assertEquals(
            StrongroomContainment.Verdict.PASS,
            verdict("com.android.dialer", floor = true, home = true)
        )
    }

    @Test
    fun verdict_agreesWithRouting_forAppPackages() {
        val locked = setOf("com.video.app")
        fun routing(pkg: String) = StrongroomRouting.routeReach(
            sealed = true,
            packageName = pkg,
            lockedPackages = locked,
            allowedPackages = allowed
        )
        // Locked + unlisted: both layers knock.
        assertEquals(StrongroomRouting.ReachDecision.KNOCK, routing("com.video.app"))
        assertEquals(
            StrongroomContainment.Verdict.KNOCK, verdict("com.video.app")
        )
        assertEquals(StrongroomRouting.ReachDecision.KNOCK, routing("com.unlisted"))
        assertEquals(StrongroomContainment.Verdict.KNOCK, verdict("com.unlisted"))
        // Allowed: the routing sends it to the antechamber; the
        // containment passes it. Same law, two doors.
        assertEquals(
            StrongroomRouting.ReachDecision.ANTECHAMBER, routing("com.notes.app")
        )
        assertEquals(
            StrongroomContainment.Verdict.PASS, verdict("com.notes.app")
        )
    }

    // ----------------------------------------------------------------
    // 2. The enforcement trace over the production store
    // ----------------------------------------------------------------

    private suspend fun signDay(dateIso: String) {
        store().sign(
            StrongroomDay.SigningRecord(
                filingDateIso = dateIso,
                lockedPackages = setOf("com.video.app"),
                allowedPackages = listOf("com.notes.app"),
                liftingTime = LocalTime.of(23, 59),
                sentence = null,
                breakLengthMinutes = 0,
                breakFiresAtOffsetMillis = -1L,
                allowanceMinutes = 30,
                gaps = emptyList()
            )
        )
    }

    @Test
    fun trace_signedDayAlone_armsNothing_theSwingMakesTheSealStand() = runBlocking {
        signDay("2026-11-03")
        // Signed, not sealed: the store the doors read has no
        // sealed day — containment is inert, exactly as the legacy
        // path is untouched where no Strongroom day stands sealed.
        assertNull(store().sealedDay())

        val span = store().seal(
            "2026-11-03",
            sealedAtWallMillis = System.currentTimeMillis(),
            sealedAtElapsedMillis = SystemClock.elapsedRealtime(),
            bootCount = StrongroomEngineBridge.bootCount(context)
        )
        assertTrue(span != null)
        val standing = store().sealedDay()
        assertTrue(standing != null)
        // The standing row carries the SIGNED sets the doors judge
        // by (B-9) — read from the record, never supplied.
        val signing = standing!!.toSigningRecord()
        assertEquals(setOf("com.video.app"), signing.lockedPackages)
        assertEquals(listOf("com.notes.app"), signing.allowedPackages)
    }

    @Test
    fun trace_knockVerdict_fedToBridge_punchesExactlyOnce_allowedPunchesNothing() =
        runBlocking {
            signDay("2026-11-03")
            store().seal(
                "2026-11-03",
                sealedAtWallMillis = System.currentTimeMillis(),
                sealedAtElapsedMillis = SystemClock.elapsedRealtime(),
                bootCount = StrongroomEngineBridge.bootCount(context)
            )
            // The doors' KNOCK act, verbatim: verdict first, then
            // the reach fed into the bridge.
            val standing = store().sealedDay()!!
            val v = StrongroomContainment.verdict(
                packageName = "com.video.app",
                ownPackage = context.packageName,
                allowedPackages = standing.toSigningRecord().allowedPackages.toSet(),
                isSafetyFloor = false,
                isHomePackage = false,
                isSystemUi = false
            )
            assertEquals(StrongroomContainment.Verdict.KNOCK, v)
            assertEquals(
                StrongroomRouting.ReachDecision.KNOCK,
                StrongroomEngineBridge.onLockedAppReach(context, "com.video.app")
            )
            val punches = store().punches("2026-11-03")
            assertEquals(1, punches.size)
            assertEquals(StrongroomDay.PunchKind.REACH, punches.single().kind)
            assertEquals("com.video.app", punches.single().packageName)

            // An allowed reach: the bridge passes it to the
            // antechamber and the day's record gains nothing.
            assertEquals(
                StrongroomRouting.ReachDecision.ANTECHAMBER,
                StrongroomEngineBridge.onLockedAppReach(context, "com.notes.app")
            )
            assertEquals(1, store().punches("2026-11-03").size)
        }

    // ----------------------------------------------------------------
    // 3. The HOME standing
    // ----------------------------------------------------------------

    private fun manifest(): String {
        val candidates = listOf(
            File("src/main/AndroidManifest.xml"),
            File("app/src/main/AndroidManifest.xml")
        )
        val file = candidates.firstOrNull { it.exists() }
            ?: error("could not locate AndroidManifest.xml from ${File(".").absolutePath}")
        return file.readText()
    }

    @Test
    fun manifest_mainActivity_carriesHomeStandingBesideLauncher() {
        val m = manifest()
        val mainActivityBlock = m.substringAfter("android:name=\".MainActivity\"")
            .substringBefore("</activity>")
        assertTrue(mainActivityBlock.contains("android.intent.category.LAUNCHER"))
        assertTrue(mainActivityBlock.contains("android.intent.category.HOME"))
        assertTrue(mainActivityBlock.contains("android.intent.category.DEFAULT"))
    }
}
