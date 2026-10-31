package com.example

import android.content.Context
import android.os.SystemClock
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppBlockTier
import com.example.data.BlockedBrowserDomains
import com.example.data.CryptoIntegrityManager
import com.example.data.CurioCatalog
import com.example.data.FocusSessionEntity
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.SmartLoadout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
// PIN-SCOPE: PLUMBING — broad Robolectric repository/session suite (working-rules adoption, Oct 31 18:06 IST).
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var db: MonolithDatabase
    private lateinit var repository: MonolithRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, MonolithDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MonolithRepository(context, db.monolithDao())
        fakeInstalledApps()
    }

    /** The catalog is the phone's truth now (Ordner lane): no blind
     *  factory seed remains, so the apps these tests exercise must
     *  be installed on the fake phone — they are discovered at
     *  seed-sync with their factory presets as birth rules (TikTok
     *  born SHUT, YouTube and WhatsApp born PEEK). */
    private fun fakeInstalledApps() {
        val shadow = org.robolectric.Shadows.shadowOf(context.packageManager)
        val query = android.content.Intent(android.content.Intent.ACTION_MAIN, null)
            .addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        for ((pkg, label) in listOf(
            "com.whatsapp" to "WhatsApp",
            "com.zhiliaoapp.musically" to "TikTok",
            "com.google.android.youtube" to "YouTube"
        )) {
            val info = android.content.pm.ResolveInfo().apply {
                activityInfo = android.content.pm.ActivityInfo().apply {
                    packageName = pkg
                    name = "$pkg.MainActivity"
                    nonLocalizedLabel = label
                }
                nonLocalizedLabel = label
            }
            shadow.addResolveInfoForIntent(query, info)
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `verify app_name is Monolith`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Monolith", appName)
    }

    @Test
    fun `verify monotonic clock anti-tamper and HMAC signature verification`() {
        val nowWall = System.currentTimeMillis()
        val nowMono = SystemClock.elapsedRealtime()
        val bootCount = repository.querySystemBootCount()

        val validHmac = CryptoIntegrityManager.signSessionState(
            startWallTimeMillis = nowWall - 10_000L,
            startElapsedRealtimeMillis = (nowMono - 10_000L).coerceAtLeast(1L),
            bootCount = bootCount,
            targetDurationSeconds = 1800,
            isStopwatch = false
        )

        val honestSession = FocusSessionEntity(
            intention = "Deep Work",
            profileName = "Study Loadout",
            targetDurationSeconds = 1800,
            elapsedSeconds = 10,
            startWallTimeMillis = nowWall - 10_000L,
            startElapsedRealtimeMillis = (nowMono - 10_000L).coerceAtLeast(1L),
            bootCount = bootCount,
            hmacSignature = validHmac,
            isActive = true,
            completedSuccessfully = false
        )

        val (_, honestTamper) = repository.computeVerifiedSessionElapsed(honestSession)
        assertFalse("Honest clock and HMAC should not flag tamper", honestTamper)
        assertTrue("Honest HMAC should be valid", CryptoIntegrityManager.verifySessionState(honestSession))

        // Tampered session where local DB targetDurationSeconds was modified without updating HMAC
        val modifiedDbSession = honestSession.copy(targetDurationSeconds = 60)
        val (_, dbTamperFlagged) = repository.computeVerifiedSessionElapsed(modifiedDbSession)
        assertTrue("Local storage modification must flag tamper", dbTamperFlagged)
        assertFalse("Modified session state must fail HMAC verification", CryptoIntegrityManager.verifySessionState(modifiedDbSession))
    }

    @Test
    fun `verify 2m peek lease immediately persists 20m cooldown in Room to prevent infinite peek loophole`() = runBlocking {
        repository.ensureSeededAndSyncPackages()
        val whatsappPkg = "com.whatsapp"

        val beforeCooldown = repository.getPeekCooldownRemainingSeconds(whatsappPkg)
        assertEquals("Before peeking, cooldown should be 0", 0, beforeCooldown)

        // Start a 2-minute peek lease
        repository.startPeekLease(whatsappPkg)

        // Even if the user immediately force-closes WhatsApp or tries to re-open,
        // the 20-minute (1,200s) cooldown is already persisted on disk!
        val afterStartCooldown = repository.getPeekCooldownRemainingSeconds(whatsappPkg)
        assertTrue(
            "Cooldown must be enforced immediately in Room (>= 1190s)",
            afterStartCooldown >= 1190
        )

        // Ending the peek early resets the 20m cooldown from the moment they finish
        repository.recordPeekEnded(whatsappPkg)
        val afterEarlyEndCooldown = repository.getPeekCooldownRemainingSeconds(whatsappPkg)
        assertTrue(
            "Cooldown after early end must still be ~1200s",
            afterEarlyEndCooldown in 1190..1200
        )
    }

    @Test
    fun `verify 3-tier per-app cycling and 1-tap smart loadouts`() = runBlocking {
        repository.ensureSeededAndSyncPackages()
        val initialApps = db.monolithDao().getAllAppsSnapshot()
        assertTrue("Default apps should be seeded", initialApps.isNotEmpty())

        val tiktok = initialApps.first { it.packageName == "com.zhiliaoapp.musically" }
        assertEquals(AppBlockTier.LOCKED, tiktok.resolvedTier)

        // Cycle 1: LOCKED -> PEEK_2M
        repository.cycleAppBlockTier(tiktok)
        val afterFirstTap = db.monolithDao().getAllAppsSnapshot().first { it.packageName == tiktok.packageName }
        assertEquals(AppBlockTier.PEEK_2M, afterFirstTap.resolvedTier)

        // Cycle 2: PEEK_2M -> OPEN
        repository.cycleAppBlockTier(afterFirstTap)
        val afterSecondTap = db.monolithDao().getAllAppsSnapshot().first { it.packageName == tiktok.packageName }
        assertEquals(AppBlockTier.OPEN, afterSecondTap.resolvedTier)

        // Apply Sleep Loadout: social & video apps become LOCKED
        repository.applySmartLoadout(SmartLoadout.SLEEP)
        val afterSleepLoadout = db.monolithDao().getAllAppsSnapshot().first { it.packageName == tiktok.packageName }
        assertEquals(AppBlockTier.LOCKED, afterSleepLoadout.resolvedTier)

        // Apply Study Loadout. WhatsApp becomes PEEK_2M and TikTok
        // stays LOCKED — and YouTube stays LOCKED too, even though
        // issue #133's rule makes lectures OPEN under STUDY: issue
        // #129's clamp stands between them. SLEEP just locked
        // YouTube, and a loadout NEVER loosens LOCKED -> OPEN on a
        // category guess ("opening a shut app is HIS hand on the
        // catalogue sled, never a guess" — LoadoutRules). The old
        // assertion here expected OPEN and had been failing on the
        // clamp since the two laws composed (floor lane, census
        // #6, row 11: the verify-first tag discharges as KEEP —
        // the behaviour is live and covered end-to-end only here;
        // the expectation was stale, not the behaviour dead).
        repository.applySmartLoadout(SmartLoadout.STUDY)
        val studyApps = db.monolithDao().getAllAppsSnapshot()
        val youtube = studyApps.first { it.packageName == "com.google.android.youtube" }
        assertEquals(AppBlockTier.LOCKED, youtube.resolvedTier)
        val whatsappAfterStudy = studyApps.first { it.packageName == "com.whatsapp" }
        assertEquals(AppBlockTier.PEEK_2M, whatsappAfterStudy.resolvedTier)
        val tiktokAfterStudy = studyApps.first { it.packageName == "com.zhiliaoapp.musically" }
        assertEquals(AppBlockTier.LOCKED, tiktokAfterStudy.resolvedTier)

        // His hand opens it (the sled verb), and only then does
        // the STUDY tap seat issue #133's lecture rule: YouTube
        // OPEN for lectures — the old PEEK_2M ration made a
        // 50-minute lecture unusable; the Shorts sniper stays the
        // feed guard.
        repository.setAppBlockTier(youtube.packageName, AppBlockTier.PEEK_2M)
        repository.applySmartLoadout(SmartLoadout.STUDY)
        val restudied = db.monolithDao().getAllAppsSnapshot()
            .first { it.packageName == "com.google.android.youtube" }
        assertEquals(AppBlockTier.OPEN, restudied.resolvedTier)

        // Direct spatial gravity well drop: drag YouTube into the Inner Event Horizon (LOCKED)
        repository.setAppBlockTier(youtube.packageName, AppBlockTier.LOCKED)
        val afterSpatialDrop = db.monolithDao().getAllAppsSnapshot().first { it.packageName == youtube.packageName }
        assertEquals(AppBlockTier.LOCKED, afterSpatialDrop.resolvedTier)
    }

    @Test
    fun `verify browser URL blocker domains list`() {
        assertTrue(BlockedBrowserDomains.defaultDomains.contains("instagram.com"))
        assertTrue(BlockedBrowserDomains.defaultDomains.contains("youtube.com/shorts"))
        assertTrue(BlockedBrowserDomains.defaultDomains.contains("reddit.com"))
    }

    @Test
    fun `verify AES-GCM 256-bit encrypted backup and stopwatch session`() = runBlocking {
        repository.ensureSeededAndSyncPackages()

        // Dialing < 10 minutes starts Count-Up Stopwatch Mode
        val stopwatchSession = repository.startSession(
            intention = "Open-Ended Lab Research",
            profileName = "Study Loadout",
            durationMinutes = 5,
            studyBlocksCount = 2
        )
        assertTrue("Dialing < 10m should activate Stopwatch Mode", stopwatchSession.isStopwatchMode)
        assertEquals(2, stopwatchSession.totalStudyBlocks)

        // Save a silent notification to the digest
        val initialCount = db.monolithDao().getCapturedNotificationsFlow().first().size
        repository.saveCapturedNotification(
            packageName = "com.whatsapp",
            appName = "WhatsApp"
        )
        val notifications = db.monolithDao().getCapturedNotificationsFlow().first()
        assertEquals(initialCount + 1, notifications.size)

        assertEquals(6, CurioCatalog.artifacts.size)
    }

    @Test
    fun `verify 3-stop switch labels and partial-credit session banking`() = runBlocking {
        assertEquals("Shut", AppBlockTier.LOCKED.label)
        assertEquals("2m Peek", AppBlockTier.PEEK_2M.label)
        assertEquals("Open", AppBlockTier.OPEN.label)

        // Start a 25-minute session and simulate 5 minutes elapsed before unlatching early
        val session = repository.startSession(
            intention = "Read Architectural History Ch. 3",
            profileName = "Study + Chat",
            durationMinutes = 25,
            studyBlocksCount = 1
        )
        val fiveMinsAgoMono = (SystemClock.elapsedRealtime() - 300_000L).coerceAtLeast(1L)
        db.monolithDao().updateSession(
            session.copy(
                startWallTimeMillis = System.currentTimeMillis() - 300_000L,
                startElapsedRealtimeMillis = fiveMinsAgoMono,
                elapsedSeconds = 300
            )
        )

        repository.disarmActiveSession(completed = false, outcomeReason = "Unlatched Early")
        val history = db.monolithDao().getSessionHistorySnapshot()
        val unlatched = history.first { it.id == session.id }
        assertFalse(unlatched.isActive)
        assertTrue("Partial session >= 60s must bank elapsed seconds", unlatched.elapsedSeconds >= 60)
        assertEquals("partial_stamp", unlatched.forgedArtifactId)
    }

    @Test
    fun `verify Zero-Green token palette census`() {
        // Issue #63: the census runs over the REAL token lists. It used
        // to enumerate 24 lying aliases (BauhausCanary = orange, etc.)
        // that duplicated these same tokens.
        val allStudioColors =
            com.example.ui.theme.DaylightZeroGreenTokens +
                com.example.ui.theme.NightZeroGreenTokens

        allStudioColors.forEach { color ->
            assertTrue(
                "Color $color must never have a dominant green channel (strict Zero-Green rule)",
                color.green < maxOf(color.red, color.blue)
            )
        }

    }

    @Test
    fun `verify Solar Queue timeboxed task CRUD, auto-advance, session receipt summary, and undo restore`() = runBlocking {
        repository.ensureSeededAndSyncPackages()
        val initialTasks = db.monolithDao().getAllTasksSnapshot()
        // >= 3, amended Oct 29 (Drawer build): the shipped seed's
        // 4th task was a fabricated COMPLETED row ("Review
        // Spaced-Repetition Deck", completed an hour before
        // install) — deleted on Neu's ruling so a fresh install's
        // Drawer can be honestly empty. The three honest queue
        // seeds are what this line now counts.
        assertTrue("Default timeboxed tasks should be seeded in Room", initialTasks.size >= 3)

        // Audit R2 #96: the seed is an honest QUEUED slate now —
        // no fabricated ACTIVE row with spent seconds and a done
        // substep. This story promotes the first seed itself,
        // exactly as a user's tap would.
        val firstSeed = initialTasks.first { it.queueOrder == 0 }
        val firstActive = repository.promoteTaskToActive(firstSeed.id)!!
        assertEquals("Finish Chapter 4 Problem Set (#1–6)", firstActive.title)

        // Start an active session so completing tasks logs to the session receipt
        val activeSession = repository.startSession(
            intention = firstActive.title,
            profileName = "Study Loadout",
            durationMinutes = 25,
            studyBlocksCount = 1
        )

        // Add a new timeboxed task to the queue
        val addedTask = repository.addTaskToQueue(
            title = "Write Systems Lab Report Conclusion",
            estimatedMinutes = 45,
            categoryTag = "Writing",
            notesOrSubsteps = "Include latency percentile table",
            promoteToActive = false
        )
        assertEquals(com.example.data.TaskQueueStatus.QUEUED, addedTask.resolvedStatus)

        // Edit the task details inline
        val editedTask = repository.updateTaskDetails(
            taskId = addedTask.id,
            newTitle = "Write Systems Lab Report & Graphs",
            newEstimatedMinutes = 50,
            newCategoryTag = "Writing",
            newNotes = "Include p99 latency chart"
        )
        assertNotNull(editedTask)
        assertEquals("Write Systems Lab Report & Graphs", editedTask!!.title)
        assertEquals(50, editedTask.estimatedMinutes)

        // Complete the active task and verify auto-advance promotes the next queued task to ACTIVE
        val nextActive = repository.completeTaskAndAdvance(firstActive.id)
        assertNotNull("Completing active task must auto-advance the next queued task", nextActive)
        assertEquals(com.example.data.TaskQueueStatus.ACTIVE, nextActive!!.resolvedStatus)
        assertEquals("Synthesize Lecture 9 Architecture Notes", nextActive.title)

        // Verify the active session receipt recorded the completed task
        val updatedSession = db.monolithDao().getActiveSessionSnapshot()
        assertNotNull(updatedSession)
        assertEquals(1, updatedSession!!.tasksCompletedCount)
        assertTrue(updatedSession.completedTasksSummary.contains("Finish Chapter 4 Problem Set"))

        // Delete a task and restore it (5-second Undo workflow)
        val (deleted, _) = repository.deleteTaskById(addedTask.id)
        assertNotNull(deleted)
        val afterDelete = db.monolithDao().getAllTasksSnapshot().any { it.id == addedTask.id }
        assertFalse("Deleted task should be removed from Room", afterDelete)

        repository.restoreDeletedTask(deleted!!)
        val afterRestore = db.monolithDao().getAllTasksSnapshot().any { it.id == addedTask.id }
        assertTrue("Restored task must be back in Room after Undo", afterRestore)

        // Verify calling ensureSeededAndSyncPackages() repeatedly never duplicates tasks or session receipts
        val taskCountBeforeReSeed = db.monolithDao().getAllTasksSnapshot().size
        val sessionCountBeforeReSeed = db.monolithDao().getSessionHistorySnapshot().size
        repository.ensureSeededAndSyncPackages()
        repository.ensureSeededAndSyncPackages()
        assertEquals("Re-seeding must be strictly idempotent for tasks", taskCountBeforeReSeed, db.monolithDao().getAllTasksSnapshot().size)
        assertEquals("Re-seeding must be strictly idempotent for sessions", sessionCountBeforeReSeed, db.monolithDao().getSessionHistorySnapshot().size)

        // Verify interactive sub-step toggling and HAND-SPLIT
        // (F-6, issue #12: the auto-split generator is deleted —
        // this segment previously called it, and the generator
        // OVERWROTE the notes with its fabricated triple, which is
        // what made the count below exactly 3. Hand-split APPENDS:
        // the task's edited notes ("Include p99 latency chart")
        // already parse as one step, so two hand-written steps
        // bring the same total to the same 3 — by addition now,
        // never by overwrite)
        repository.addTaskSubStep(addedTask.id, "Draft results section")
        repository.addTaskSubStep(addedTask.id, "Verify units & captions")
        val splitTask = db.monolithDao().getTaskById(addedTask.id)
        assertNotNull(splitTask)
        assertEquals(3, splitTask!!.subSteps.size)
        repository.toggleTaskSubStep(addedTask.id, 0)
        val afterStepToggle = db.monolithDao().getTaskById(addedTask.id)
        assertTrue("First micro-step should be marked done after toggle", afterStepToggle!!.subSteps.first().isDone)
    }

    @Test
    fun `verify 19 Zero-Green daylight and night tokens and Float-PCM synth constants`() {
        assertEquals(19, com.example.ui.theme.DaylightZeroGreenTokens.size)
        assertEquals(19, com.example.ui.theme.NightZeroGreenTokens.size)

        (com.example.ui.theme.DaylightZeroGreenTokens + com.example.ui.theme.NightZeroGreenTokens).forEach { color ->
            assertTrue(
                "Zero-Green token $color must satisfy green < maxOf(red, blue)",
                color.green < maxOf(color.red, color.blue)
            )
        }

        // Verify Float-PCM Hardware Transient Synthesizer constants from DEVELOPER-GUIDE.pdf §7
        val weights = com.example.service.TactileSoundEngine.PARTIAL_WEIGHTS
        assertEquals(4, weights.size)
        assertEquals(0.6f, weights[0], 0.001f)
        assertEquals(0.28f, weights[1], 0.001f)
        assertEquals(0.37f, weights[2], 0.001f)
        assertEquals(2.2f, weights[3], 0.001f)
        assertTrue(com.example.service.TactileSoundEngine.BODY_DECAY_STEP in 0.98f..0.995f)
        assertTrue(com.example.service.TactileSoundEngine.TAIL_DECAY_STEP in 0.96f..0.975f)
    }
}

