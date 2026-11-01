package com.example

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Binder
import android.os.Looper
import android.os.Process
import android.os.SystemClock
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.service.PillController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSettings
import org.robolectric.shadows.ShadowWindowManagerImpl

/**
 * PIN-scope: RENDER — [PillController]'s window labels, driven through
 * the controller's own public surface (construction + start()) against
 * a seeded session row, a granted overlay op and a foreign foreground
 * witness. Until this file existed, no test in the tree referenced
 * PillController at all: its render was uncovered code, which the
 * targeted gate's honesty step read correctly on PR #766.
 *
 * The two label assertions rest on `PillController.render`'s face
 * branches: a session in its Safe Break prints the BREAK label, and a
 * plain timed session prints the SESSION · BLOCK label — in both cases
 * the exact text the strings.xml seam resolves (`R.string.pill_label_*`
 * after the #64 literal migration; the rendered values are unchanged).
 *
 * The gate pre-assertions name their own failure: if the overlay grant
 * or the foreground witness cannot be established in the sandbox, the
 * test fails THERE, saying which door was shut — never downstream at
 * an empty window.
 *
 * World isolation (the session face's cure, Oct 31): the tick
 * derives from EVERY app row in the shared store
 * (`getAllAppsSnapshot`), and in PillDerivation a live peek lease
 * outranks the session face — while a Safe Break outranks the
 * lease, which is why the break face never saw this. Suites that
 * grant leases leave them standing in the store
 * (PeekMinutesSetterTest's leaseEndIsStampedFromTheSetLength
 * grants `com.peekset.lease` a five-minute lease and restores
 * only the minutes value), so in the full suite this file's
 * session-face tick printed the polluter's peek face —
 * [LEASE, 05:00, ✕] — instead of its own session: the failure
 * flagged merge-introduced by PR #773 on #245 (6545374876).
 * This suite seeds no app rows; it clears the app table before
 * starting the controller, so the derivation reads the world
 * this test actually made. The label assertions are unchanged.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PillControllerRenderTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private var controller: PillController? = null
    private var scope: CoroutineScope? = null
    private var sessionId: Long = -1L

    @After
    fun tearDown() {
        controller?.stop()
        controller = null
        scope?.cancel()
        scope = null
        if (sessionId > 0) {
            val dao = MonolithDatabase.getInstance(context).monolithDao()
            runBlocking { dao.deleteSessionById(sessionId.toInt()) }
            sessionId = -1L
        }
    }

    /** Grant the two doors render() and the foreground read stand
     *  behind, and witness a foreign app in front. Each grant is
     *  proven by its own assertion before the controller starts.
     *
     *  Two different mechanisms, because the two doors are two
     *  different mechanisms: in Robolectric, Settings.canDrawOverlays
     *  is SHADOWED — ShadowSettings holds its own flag and no AppOps
     *  grant moves it (five CI cycles on this file established that
     *  the hard way: the op read asserted MODE_ALLOWED while the door
     *  stayed shut) — while usageAccess() runs the real AppOps read,
     *  so GET_USAGE_STATS is granted through ShadowAppOpsManager
     *  under both uids (the sandbox does not promise the process uid
     *  and the Binder calling uid are the same number). */
    private fun openTheDoors(repository: MonolithRepository) {
        ShadowSettings.setCanDrawOverlays(true)
        assertTrue(
            "overlay door: Settings.canDrawOverlays must read true once the shadow flag is set",
            Settings.canDrawOverlays(context)
        )
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        for (uid in setOf(Process.myUid(), Binder.getCallingUid())) {
            shadowOf(appOps).setMode(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                uid,
                context.packageName,
                AppOpsManager.MODE_ALLOWED
            )
        }

        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        // Stamped one second in the past, deliberately: the shadow's
        // queryEvents reads a TreeMap sub-range whose upper bound is
        // EXCLUSIVE, so a witness stamped at the query's own
        // millisecond falls outside the window and the foreground
        // read comes back null (the second test in this class lost
        // exactly that race). One second old is still the current
        // foreground inside the read's fifteen-minute window.
        shadowOf(usm).addEvent(
            "com.foreign.witness",
            System.currentTimeMillis() - 1_000,
            UsageEvents.Event.MOVE_TO_FOREGROUND
        )
        assertEquals(
            "foreground door: the usage-stats witness must name the foreign app",
            "com.foreign.witness",
            repository.queryForegroundPackage()
        )
    }

    private fun seedSession(session: FocusSessionEntity) {
        val dao = MonolithDatabase.getInstance(context).monolithDao()
        sessionId = runBlocking { dao.insertSession(session) }
    }

    private fun sessionRow(inSafeBreak: Boolean) = FocusSessionEntity(
        intention = "pill render test",
        profileName = "TEST",
        targetDurationSeconds = 3600,
        elapsedSeconds = 0,
        startWallTimeMillis = System.currentTimeMillis(),
        startElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
        lastTickElapsedRealtimeMillis = SystemClock.elapsedRealtime(),
        isInSafeBreak = inSafeBreak,
        safeBreakSecondsRemaining = if (inSafeBreak) 600 else 0,
        isActive = true,
        completedSuccessfully = false
    )

    /** Start the controller and wait for its window, idling the main
     *  looper while Room's executor answers the tick's reads. Returns
     *  every text the window's view tree prints. */
    private fun startAndCollectWindowTexts(): List<String> {
        val db = MonolithDatabase.getInstance(context)
        val dao = db.monolithDao()
        val repository = MonolithRepository(context, dao)
        openTheDoors(repository)

        val driveScope = CoroutineScope(Dispatchers.Main + Job())
        scope = driveScope
        // World isolation (see the class doc): clear every app
        // row the shared store is holding, so no foreign peek
        // lease can outrank this suite's own faces in the
        // derivation. This suite seeds no app rows of its own.
        val strayApps = runBlocking { dao.getAllAppsSnapshot() }
        if (strayApps.isNotEmpty()) {
            runBlocking {
                dao.deleteAppsByPackage(strayApps.map { it.packageName })
            }
        }

        val pill = PillController(context, repository, dao, driveScope, context.packageName)
        controller = pill
        pill.start()

        val deadline = System.currentTimeMillis() + 10_000L
        var texts = windowTexts()
        while (texts.isEmpty() && System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(50)
            texts = windowTexts()
        }
        return texts
    }

    private fun windowTexts(): List<String> {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val out = mutableListOf<String>()
        fun walk(view: View) {
            if (view is TextView) out += view.text?.toString().orEmpty()
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) walk(view.getChildAt(i))
            }
        }
        (shadowOf(wm) as ShadowWindowManagerImpl).views.forEach { walk(it) }
        return out
    }

    @Test
    fun breakFace_windowPrintsTheBreakLabelFromTheSeam() {
        seedSession(sessionRow(inSafeBreak = true))
        val texts = startAndCollectWindowTexts()
        assertTrue(
            "break face: the pill window must print its label; window texts were $texts",
            texts.contains(context.getString(R.string.pill_label_break))
        )
        assertTrue(
            "break face: the pill window must print a live clock; window texts were $texts",
            texts.any { it.matches(Regex("\\d+:\\d{2}")) }
        )
    }

    @Test
    fun sessionFace_windowPrintsTheSessionBlockLabelFromTheSeam() {
        seedSession(sessionRow(inSafeBreak = false))
        val texts = startAndCollectWindowTexts()
        assertTrue(
            "session face: the pill window must print its label; window texts were $texts",
            texts.contains(context.getString(R.string.pill_label_session_block, 1, 1))
        )
    }
}
