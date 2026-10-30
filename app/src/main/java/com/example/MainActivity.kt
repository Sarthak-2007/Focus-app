package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.ReturnChitState
import com.example.data.chitVerdictFor
import com.example.service.InterceptPath
import com.example.service.MonolithShieldService
import com.example.service.PermPrint
import com.example.service.TactileSoundEngine
import com.example.service.WatchItemId
import com.example.service.WatchStore
import com.example.ui.MonolithDestination
import com.example.ui.ArmPhase
import com.example.ui.MonolithViewModel
import com.example.ui.components.AirlockInterceptOverlay
import com.example.ui.components.AlwaysOnDeskClockOverlay
import com.example.ui.components.LauncherBlockScreen
import com.example.ui.components.LauncherPage
import com.example.ui.components.CapturedNotificationsDigestSheet
import com.example.ui.components.DisarmChallengeOverlay
import com.example.ui.components.GuardLinePrint
import com.example.ui.components.MonolithPullDownHeader
import com.example.ui.components.ParkDrawer
import com.example.ui.components.ReceptionSheet
import com.example.ui.components.doorsMaskAt
import com.example.ui.components.guardLineFor
import com.example.ui.components.receptionFirstRoutineLine
import com.example.ui.components.receptionTotal
import com.example.ui.screens.DrawerScreen
import com.example.ui.screens.FocusSequencerScreen
import com.example.ui.screens.ShieldMatrixScreen
import com.example.ui.screens.TactileDeckScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.views.MonolithRailView
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var soundEngine: TactileSoundEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )

        val database = MonolithDatabase.getInstance(applicationContext)
        val repository = MonolithRepository(applicationContext, database.monolithDao())
        soundEngine = TactileSoundEngine(applicationContext)

        setContent {
            val viewModel: MonolithViewModel = viewModel(
                factory = MonolithViewModel.Factory(
                    appContext = applicationContext,
                    repository = repository,
                    soundEngine = soundEngine
                )
            )
            val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
            val activeSessionForTheme by viewModel.activeSession.collectAsStateWithLifecycle()
            val shotGround by viewModel.shotGround.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                handleIncomingInterceptIntent(intent, viewModel)
            }

            MyApplicationTheme(
                darkTheme = darkTheme,
                dynamicColor = false,
                // Slice E (F-Z1 + Zug-II §5.1): the scheme pin —
                // session-night applies only once the ground has
                // STEPPED (t=0 + 400ms), never at the row's birth
                // and never during proving. Before this build the
                // theme flipped the frame the row appeared; the
                // pin and the Deck's ground surfaces now step
                // together, on the VM's one mirror.
                isFocusSessionActive = activeSessionForTheme?.isActive == true &&
                    shotGround == com.example.ui.MonolithViewModel.ShotGround.GROUNDED
            ) {
                MonolithRootApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun handleIncomingInterceptIntent(intent: Intent?, viewModel: MonolithViewModel) {
        if (intent == null) return
        val pkg = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPTED_PACKAGE)
        if (!pkg.isNullOrBlank()) {
            val name = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPTED_APP_NAME)
                ?: "Short-Form Feed"
            val badge = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPT_BADGE)
                ?: "Locked"
            // WHICH door fired (Truth Car 3, UNA2-01): the peek
            // offer is computed per-path — an extra that is missing
            // or names no known door parses to UNKNOWN, and UNKNOWN
            // never offers a peek.
            val path = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPT_PATH)
                ?.let { runCatching { InterceptPath.valueOf(it) }.getOrNull() }
                ?: InterceptPath.UNKNOWN
            viewModel.triggerAirlockIntercept(pkg, name, badge, path)
            intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPTED_PACKAGE)
            intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPT_PATH)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::soundEngine.isInitialized) {
            soundEngine.stopAllAudio()
        }
    }
}

@Composable
fun MonolithRootApp(viewModel: MonolithViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scheme = MaterialTheme.colorScheme

    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()

    // Watch Store residency (chrome C3): entering the Deck starts a
    // residency — the accessibility pipeline must stamp an event at
    // or after this instant before the wire can read LIVE — and
    // fires the grant-check sweep (Deck entry is a §4.4 trigger).
    LaunchedEffect(currentDestination) {
        if (currentDestination == MonolithDestination.DECK) {
            com.example.service.WatchStore.noteResidencyStart()
            viewModel.runWatchSweep()
            // Draht's third trigger (Slice E): a faulted release
            // whose retry cadence is due heals on Deck entry.
            viewModel.onDeckOpenRetry()
        }
    }

    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
    val themeEvaluation by viewModel.themeEvaluation.collectAsStateWithLifecycle()
    val guardLinePrint by viewModel.guardLinePrint.collectAsStateWithLifecycle()
    val watchSnapshot by viewModel.watchSnapshots.collectAsStateWithLifecycle()
    val dialMinutes by viewModel.dialMinutes.collectAsStateWithLifecycle()
    val studyBlocksCount by viewModel.studyBlocksCount.collectAsStateWithLifecycle()
    val studyPreset by viewModel.studyPreset.collectAsStateWithLifecycle()
    val checklist by viewModel.checklist.collectAsStateWithLifecycle()
    val activeTask by viewModel.activeTask.collectAsStateWithLifecycle()
    val queuedTasks by viewModel.queuedTasks.collectAsStateWithLifecycle()
    val completedTasks by viewModel.completedTasks.collectAsStateWithLifecycle()
    val deferredTasks by viewModel.deferredTasks.collectAsStateWithLifecycle()
    val deletedTaskForUndo by viewModel.deletedTaskForUndo.collectAsStateWithLifecycle()
    val totalQueueMinutes by viewModel.totalQueueMinutes.collectAsStateWithLifecycle()
    val selectedProfileName by viewModel.selectedProfileName.collectAsStateWithLifecycle()
    val intentionText by viewModel.intentionText.collectAsStateWithLifecycle()
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val allApps by viewModel.apps.collectAsStateWithLifecycle()
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val pruneNote by viewModel.pruneNote.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val shortsReelsSniperEnabled by viewModel.shortsReelsSniperEnabled.collectAsStateWithLifecycle()
    val browserUrlBlockerEnabled by viewModel.browserUrlBlockerEnabled.collectAsStateWithLifecycle()
    val blockedSites by viewModel.blockedSites.collectAsStateWithLifecycle()
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    // UNA-07: the LOADOUT cap's resting position — his last hand pick,
    // from the persisted store by way of the VM.
    val loadoutCapIndex by viewModel.loadoutCapIndex.collectAsStateWithLifecycle()
    val showRoutineResetNote by viewModel.showRoutineResetNote.collectAsStateWithLifecycle()
    val frozenDigest by viewModel.frozenDigest.collectAsStateWithLifecycle()
    val storedMonths by viewModel.storedMonths.collectAsStateWithLifecycle()
    val drawerMonth by viewModel.drawerMonth.collectAsStateWithLifecycle()
    val drawerRows by viewModel.drawerRows.collectAsStateWithLifecycle()
    val drawerHasEarlier by viewModel.drawerHasEarlier.collectAsStateWithLifecycle()
    val drawerQuery by viewModel.drawerQuery.collectAsStateWithLifecycle()
    val drawerResults by viewModel.drawerResults.collectAsStateWithLifecycle()
    val drawerSearchHasEarlier by viewModel.drawerSearchHasEarlier.collectAsStateWithLifecycle()
    val dayWords by viewModel.dayWords.collectAsStateWithLifecycle()
    val forgedArtifactIds by viewModel.forgedArtifactIds.collectAsStateWithLifecycle()
    val appsCellCount by viewModel.appsCellCount.collectAsStateWithLifecycle()
    val appsSplit by viewModel.appsSplit.collectAsStateWithLifecycle()
    val deckWarrant by viewModel.deckWarrant.collectAsStateWithLifecycle()
    val sealFreeze by viewModel.sealFreeze.collectAsStateWithLifecycle()
    val armPhase by viewModel.armPhase.collectAsStateWithLifecycle()
    val armSentence by viewModel.armSentence.collectAsStateWithLifecycle()
    val armAnnulCount by viewModel.armAnnulCount.collectAsStateWithLifecycle()
    val struckSessionId by viewModel.struckSessionId.collectAsStateWithLifecycle()
    // Slice E: the shot's ground mirror, the finish machine, and
    // the standing receipt.
    val shotGround by viewModel.shotGround.collectAsStateWithLifecycle()
    val finishState by viewModel.finishState.collectAsStateWithLifecycle()
    val receiptState by viewModel.receiptState.collectAsStateWithLifecycle()
    val isAlwaysOnDeskClockVisible by viewModel.isAlwaysOnDeskClockVisible.collectAsStateWithLifecycle()
    val burnInFigure8MinuteStep by viewModel.burnInFigure8MinuteStep.collectAsStateWithLifecycle()
    val showNotificationDigestSheet by viewModel.showNotificationDigestSheet.collectAsStateWithLifecycle()
    val capturedNotifications by viewModel.capturedNotifications.collectAsStateWithLifecycle()
    val totalFocusedMinutes by viewModel.totalFocusedMinutes.collectAsStateWithLifecycle()
    val totalInterceptions by viewModel.totalInterceptions.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val airlockState by viewModel.airlockState.collectAsStateWithLifecycle()
    val disarmChallenge by viewModel.disarmChallenge.collectAsStateWithLifecycle()
    val weeklyEmergencyTokensRemaining by viewModel.weeklyEmergencyTokensRemaining.collectAsStateWithLifecycle()
    val ratchetWarningMessage by viewModel.ratchetWarningMessage.collectAsStateWithLifecycle()

    val sealed = activeSession?.isActive == true
    // HB-1 / HB-2 (Truth Car 2; Band's header-band audit): ONE
    // predicate for the door. Until this car, the header's isSealed
    // drank from `sealed` alone while the VM's navigation law also
    // knew the finish machine's detention — during a finish's
    // detention the band could print "DOOR: OPEN" over a held
    // frame, and the Guard line's tap could open PARK over the
    // receipt/hold (Guard taps land here, not in the destination
    // switch the VM's law guards). Every consumer below — header
    // print, door affordance, openPark guard — reads this one
    // doorLocked now, so the print and the lock can never part
    // again.
    val doorLocked = sealed || viewModel.navigationLocked()
    val effectiveNight = darkTheme || sealed
    // Slice E (Zug-II §5.1): the GROUND night — the header band's
    // room and the rail step at t=400 with the scheme pin; the
    // Deck derives its own split internally and Shield / Tasks /
    // Vault keep the immediate session-night above.
    val groundNight = darkTheme ||
        shotGround == com.example.ui.MonolithViewModel.ShotGround.GROUNDED
    // The receipt sheet's dismissal is host state (F-B1): the
    // slip stands until the next DOWN anywhere, a navigation, or
    // PARK's invocation — it is never dismissed by a timer. (The
    // nav/PARK effect itself sits below, beside the PARK state
    // it reads.)
    var dismissedReceiptSessionId by remember { mutableStateOf(-1) }

    // ------------------------------------------------------------------
    // PARK state (chrome C4). The drawer is invoked by arrival (the
    // rail), by the Guard line's tap, and — on the first run only,
    // while the store has never measured — by itself at first
    // composition. While sealed it cannot be invoked at all.
    // ------------------------------------------------------------------
    var parkOpen by remember { mutableStateOf(false) }
    var parkReturnTick by remember { mutableStateOf(0) }

    // ------------------------------------------------------------------
    // THE RECEPTION (Empfang lane, Oct 30 — GRAFTED onto HEAD at
    // the issue-#14 merge, Stachel C-12: the lane's host file was
    // a stale base and is never merged whole): the first-open
    // setup + introduction sheet. The stamp lives in SetupStore
    // (its own prefs, committed). The sheet opens itself only
    // while unstamped; once stamped it never interrupts again,
    // and PARK's stub row re-opens it. Stamping also discharges
    // PARK's own first-run invitation below.
    // ------------------------------------------------------------------
    val setupStore = remember { com.example.data.SetupStore(context.applicationContext) }
    var setupStamped by remember { mutableStateOf(setupStore.isCompleted()) }
    var receptionOpen by remember {
        mutableStateOf(
            com.example.ui.components.receptionInitiallyOpen(setupStore.isCompleted())
        )
    }

    // The Return Chit's host mirror (audit §3 Proposal 1): the
    // store is the fact; this state is only its print. Cold
    // start initialises from the store, so a process death in
    // Settings loses nothing.
    var chitUi by remember { mutableStateOf(setupStore.chit()) }

    // The Armed Handover (§3 Proposal 3, as amended by Stachel
    // C-13): a one-shot Guard-line override handed over by DONE
    // — WHOLE CASE ONLY. At 4/4 the standing line names no lack,
    // so the handover displaces nothing; in every other case
    // the live line keeps its slot and the handover speaks only
    // on PARK's stub (its own seat). Held in `remember` only —
    // process death kills it lawfully. Auto-seal is killed by
    // name: the handover points at the slide, never performs it.
    var armedHandover by remember { mutableStateOf<GuardLinePrint?>(null) }

    // H-2's departure record: set only by the Shield excursion
    // of an UNSTAMPED sheet; the re-raise effect below is keyed
    // on the destination so every return path obeys it.
    var shieldExcursion by remember { mutableStateOf(false) }

    /** Book the setup stamp ONLY (Letzt §1.2 unbundling, G-01):
     *  DONE's stamping press dispatches this FIRST, before the
     *  tear's sound or motion — the write, the commit, the
     *  publish, in the house order. The sheet is still standing
     *  when this returns; dismissal is stampSetupAndEnter(),
     *  called by the sheet only after the tear has run. The
     *  Setup Ledger lands in the same act (write-once mask,
     *  fresh-GRANTED only by construction — doorsMaskAt reads
     *  permPrintAt, whose GRANTED is never stale, C-14), and
     *  the handover is armed from the same instant's snapshot
     *  (whole case only, C-13). Idempotent: the guard makes a
     *  second call a no-op, so the stamp can never book twice. */
    fun stampSetupOnly() {
        if (setupStamped) return
        val nowMono = SystemClock.elapsedRealtime()
        setupStore.stampSetup(doorsMaskAt(watchSnapshot, nowMono))
        setupStamped = true
        armedHandover = if (receptionTotal(watchSnapshot, nowMono).count == 4) {
            GuardLinePrint("GUARD 4/4 — ONE ACT LEFT: SLIDE.")
        } else {
            null
        }
        if (viewModel.parkIntroPending()) {
            viewModel.markParkIntroDone()
        }
    }

    /** Exit only — the stamp is stampSetupOnly()'s business,
     *  so the sheet's DONE predicate stays auditable (§1.1). */
    fun stampSetupAndEnter() {
        receptionOpen = false
        viewModel.navigateTo(MonolithDestination.DECK)
    }

    // Chit resolution: judged on the first measurement taken
    // AFTER departure, whenever it lands (return sweep, Deck
    // entry, cold start). N-1 overnight law: a chit older than
    // a day by the wall clock resolves DARK before any sweep is
    // consulted — a verdict about Tuesday's excursion never
    // prints on Friday.
    LaunchedEffect(parkReturnTick, watchSnapshot) {
        val c = setupStore.chit() ?: return@LaunchedEffect
        if (c.state != ReturnChitState.DEPARTED) {
            chitUi = c
            return@LaunchedEffect
        }
        if (System.currentTimeMillis() - c.departureWall > 24 * 60 * 60 * 1000L) {
            setupStore.resolveChit(ReturnChitState.RETURNED_DARK, null)
            chitUi = setupStore.chit()
            return@LaunchedEffect
        }
        val verdict = chitVerdictFor(
            c,
            watchSnapshot?.measurement(c.door),
            SystemClock.elapsedRealtime()
        ) ?: return@LaunchedEffect
        val next = watchSnapshot?.let { snap ->
            // Wave-2 cure (Pforte; Brutal-critic + Finisher-surface
            // charge): the next-door search previously matched
            // MISSING only, so a STALE or UNMEASURED sibling left
            // next == null and the chit printed "· ALL FOUR SET."
            // beside a total pricing that very door — S-1's disease
            // inside the mechanism built to cure it. The search is
            // now over every non-GRANTED print: the suffix claims
            // ALL FOUR SET only when no other door lacks, and a
            // stale or unread door is honestly named NEXT.
            WatchItemId.COUNTED.firstOrNull { other ->
                other != c.door &&
                    WatchStore.permPrintAt(
                        snap, other, SystemClock.elapsedRealtime()
                    ) != PermPrint.GRANTED
            }
        }
        setupStore.resolveChit(verdict, next)
        if (verdict == ReturnChitState.RETURNED_REPAIRED) {
            // Letzt A-5: the click is the second channel,
            // REPAIRED only — never a failure voice.
            viewModel.soundEngine.playFirmClick()
        }
        chitUi = setupStore.chit()
    }

    // The chit's deadline: the sweep gets a bounded window — a
    // named 3 s, not the old 800 ms guess. Expiry resolves
    // UNREAD (or DARK if the boot clock went backwards).
    LaunchedEffect(parkReturnTick) {
        if (parkReturnTick <= 0) return@LaunchedEffect
        val c = setupStore.chit() ?: return@LaunchedEffect
        if (c.state != ReturnChitState.DEPARTED) return@LaunchedEffect
        delay(3_000L)
        val still = setupStore.chit() ?: return@LaunchedEffect
        if (still.state == ReturnChitState.DEPARTED) {
            setupStore.resolveChit(
                if (SystemClock.elapsedRealtime() < still.departureMono) {
                    ReturnChitState.RETURNED_DARK
                } else {
                    ReturnChitState.RETURNED_UNREAD
                },
                null
            )
            chitUi = setupStore.chit()
        }
    }

    // The ledger's ever-set mask: a bit is noted the first
    // sweep that sees its door GRANTED after the stamp.
    LaunchedEffect(watchSnapshot, setupStamped) {
        if (!setupStamped) return@LaunchedEffect
        val mask = doorsMaskAt(watchSnapshot, SystemClock.elapsedRealtime())
        if (mask and setupStore.everSetMask().inv() != 0) {
            setupStore.noteEverSet(mask)
        }
    }

    // The chit prints for ONE printing of the sheet: closing
    // the sheet spends a judged chit (an unjudged one is never
    // silently spent — the store refuses from DEPARTED).
    LaunchedEffect(receptionOpen) {
        if (!receptionOpen) {
            setupStore.spendChit()
            chitUi = setupStore.chit()
        }
    }

    // Handover spenders 2–4 (spender 1, the slide attempt, is
    // wrapped at the Deck's arm call site): a session stands /
    // he leaves the Deck / PARK opens — first act wins.
    LaunchedEffect(activeSession?.isActive) {
        if (activeSession?.isActive == true) armedHandover = null
    }
    LaunchedEffect(currentDestination) {
        if (currentDestination != MonolithDestination.DECK) armedHandover = null
    }
    LaunchedEffect(parkOpen) {
        if (parkOpen) armedHandover = null
    }

    // H-2 re-raise: returning to the Deck unstamped and
    // unsealed raises the sheet for this sitting, once.
    LaunchedEffect(currentDestination, setupStamped, sealed) {
        if (shieldExcursion &&
            currentDestination == MonolithDestination.DECK &&
            !setupStamped && !sealed
        ) {
            shieldExcursion = false
            receptionOpen = true
        }
    }
    LaunchedEffect(sealed) {
        if (sealed) shieldExcursion = false
    }

    // THE LAUNCHER (Sarthak's ruling, Oct 30 04:29 IST): while the
    // phone is sealed, the block surface is a launcher. It opens
    // from the Guard line's tap (a dead tap while sealed until now —
    // PARK refuses under seal) and from the Airlock's two doors.
    var launcherOpen by remember { mutableStateOf(false) }
    var launcherPage by remember { mutableStateOf(LauncherPage.HOME) }

    fun closePark(dismissReceipt: Boolean) {
        parkOpen = false
        if (dismissReceipt && showNotificationDigestSheet) {
            viewModel.toggleNotificationDigestSheet(false)
        }
        if (viewModel.parkIntroPending()) {
            viewModel.markParkIntroDone()
        }
    }

    fun openPark() {
        // HB-2: the tap-time read of the SAME doorLocked predicate —
        // the synchronous backstop for a Guard tap landing inside
        // the finish machine's detention window before the state
        // collectors recompose. The predicate is evaluated HERE, at
        // the tap: navigationLocked() reads the VM's flows directly,
        // so the answer is true even while this composition's
        // doorLocked still holds its pre-detention value. (The
        // first form of this guard read the captured doorLocked
        // val — a print, not a lock; hb4DetentionGuardTap caught
        // it on the gate build's first full test run.)
        if (!sealed && !viewModel.navigationLocked()) parkOpen = true
    }

    // F-B1: PARK's invocation dismisses the receipt sheet on the
    // same input, and so does any navigation away from the Deck.
    LaunchedEffect(currentDestination, parkOpen) {
        if (currentDestination != MonolithDestination.DECK || parkOpen) {
            receiptState?.let { dismissedReceiptSessionId = it.sessionId }
        }
    }

    // First run (§5.7): the store has never measured — PARK opens
    // directly at first composition. The persisted bit keeps a cold
    // start's unmeasured instant from re-inviting on later runs.
    // Empfang gate (Oct 30): while the setup stands unstamped the
    // RECEPTION does the first-open introducing, and PARK does not
    // invite itself over it — the two first-open surfaces never
    // stack. Once stamped, this invitation behaves exactly as
    // before for any run whose park bit somehow survived.
    LaunchedEffect(Unit) {
        if (viewModel.parkIntroPending() && setupStamped) {
            parkOpen = true
        }
    }

    // PARK's on-open diagnostic (§3.3): opening PARK fires the old
    // refreshSystemGuard() sweep. Its result renders NOWHERE — the
    // board renders the Watch Store — unless a diagnostic line is
    // invoked; the sweep itself is the point (clock/HMAC/boot facts
    // stay warm for the day a diagnostic needs them).
    LaunchedEffect(parkOpen) {
        if (parkOpen) {
            viewModel.refreshSystemGuard()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Return is proving (chrome C4 §4.4): the sweep
                // re-reads every grant; a FIX trip's hand-back is
                // judged against these landings.
                viewModel.runWatchSweep()
                parkReturnTick++
                viewModel.onAppResumed()
                viewModel.refreshDeckWarrant()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (parkOpen) {
        BackHandler { closePark(dismissReceipt = false) }
    } else if (currentDestination != MonolithDestination.DECK) {
        BackHandler { viewModel.navigateTo(MonolithDestination.DECK) }
    }

    // The reception's BACK: leaving stamps nothing (composed
    // after the chain above so it wins while the sheet stands).
    // The sheet re-offers at the next cold start and waits in
    // PARK — leaving is never a trap and never a finish.
    if (receptionOpen) {
        BackHandler { receptionOpen = false }
    }

    val selectedTabIndex = when (currentDestination) {
        MonolithDestination.DECK -> 0
        MonolithDestination.SHIELD -> 1
        MonolithDestination.TASKS -> 2
        MonolithDestination.VAULT -> 3
    }

    // The FIX key's panes: the exact Settings pane per grant,
    // including the device-admin pane the app has never had.
    fun openFixPane(item: com.example.service.WatchItemId) {
        runCatching {
            when (item) {
                com.example.service.WatchItemId.ACCESSIBILITY ->
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                com.example.service.WatchItemId.USAGE_ACCESS ->
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                com.example.service.WatchItemId.DEVICE_ADMIN -> {
                    val direct = Intent().setComponent(
                        android.content.ComponentName(
                            "com.android.settings",
                            "com.android.settings.DeviceAdminSettings"
                        )
                    )
                    runCatching { context.startActivity(direct) }
                        .getOrElse {
                            context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                        }
                }
                com.example.service.WatchItemId.NOTIFICATION_LISTENER ->
                    context.startActivity(
                        Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                    )
                com.example.service.WatchItemId.OVERLAY ->
                    context.startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                    )
                com.example.service.WatchItemId.BATTERY -> {
                    val direct = Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:${context.packageName}")
                    )
                    runCatching { context.startActivity(direct) }
                        .getOrElse {
                            context.startActivity(
                                Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                            )
                        }
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // The header band (chrome C2): brand + Guard line.
                MonolithPullDownHeader(
                    // The handover masks the live line while it
                    // stands — whole case only (C-13), so it can
                    // never displace a named lack.
                    guardLine = armedHandover ?: guardLinePrint,
                    // HB-1 (Truth Car 2) SUPERSEDES the chrome C11
                    // vi split: the header's door print drinks from
                    // the same doorLocked predicate as the openPark
                    // guard and the VM's navigation law, so the band
                    // can never print DOOR: OPEN over a held frame
                    // again. The guard line still carries only the
                    // door's one word while detained — nothing else
                    // rides it.
                    isSealed = doorLocked,
                    onGuardLineTap = {
                        when {
                            parkOpen -> closePark(dismissReceipt = false)
                            // While sealed, the Guard line opens the
                            // launcher — the sealed phone's home.
                            sealed -> {
                                launcherPage = LauncherPage.HOME
                                launcherOpen = true
                            }
                            else -> openPark()
                        }
                    }
                )

                // Main Screen Viewport — KIN-TAB-IN lives here and
                // nowhere else (chrome C1 §6.6): the outgoing screen
                // fades 120 linear; the incoming translates 14dp in
                // stop order and fades in over 240 SEAT.
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = currentDestination,
                        transitionSpec = {
                            val dir = if (
                                stopIndexOf(targetState) >= stopIndexOf(initialState)
                            ) 1 else -1
                            (fadeIn(
                                androidx.compose.animation.core.tween(
                                    com.example.ui.theme.MonolithKin.TAB_IN_MS,
                                    easing = com.example.ui.theme.MonolithKin.EasingSeat
                                )
                            ) + slideInHorizontally(
                                androidx.compose.animation.core.tween(
                                    com.example.ui.theme.MonolithKin.TAB_IN_MS,
                                    easing = com.example.ui.theme.MonolithKin.EasingSeat
                                )
                            ) { fullWidth -> dir * fullWidth / 28 }) togetherWith
                                fadeOut(
                                    androidx.compose.animation.core.tween(
                                        com.example.ui.theme.MonolithKin.TAB_FADE_OUT_MS,
                                        easing = androidx.compose.animation.core.LinearEasing
                                    )
                                )
                        },
                        label = "monolith_tab_transition"
                    ) { destination ->
                        when (destination) {
                            MonolithDestination.DECK -> {
                                TactileDeckScreen(
                                    activeSession = activeSession,
                                    dialMinutes = dialMinutes,
                                    studyBlocksCount = studyBlocksCount,
                                    studyPreset = studyPreset,
                                    checklist = checklist,
                                    activeTask = activeTask,
                                    queuedTasks = queuedTasks,
                                    completedTasksCount = completedTasks.size,
                                    totalQueueMinutes = totalQueueMinutes,
                                    selectedProfileName = selectedProfileName,
                                    intentionText = intentionText,
                                    totalFocusedMinutes = totalFocusedMinutes,
                                    totalInterceptions = totalInterceptions,
                                    profiles = profiles,
                                    apps = allApps,
                                    shortsReelsSniperEnabled = shortsReelsSniperEnabled,
                                    browserUrlBlockerEnabled = browserUrlBlockerEnabled,
                                    capturedNotificationsCount = capturedNotifications.size,
                                    soundEngine = viewModel.soundEngine,
                                    // Zug-II §5.1: the Deck receives
                                    // the RAW system theme — its
                                    // session-night is its own split
                                    // (hero at t=0, ground at t=400),
                                    // never the host's immediate one.
                                    isDarkTheme = darkTheme,
                                    onDialMinutesChanged = viewModel::setDialMinutes,
                                    onSelectStudyPreset = viewModel::selectStudyPreset,
                                    onSetStudyBlocksCount = viewModel::setStudyBlocksCount,
                                    onToggleChecklistItem = viewModel::toggleChecklistItem,
                                    onAddChecklistItem = viewModel::addChecklistItem,
                                    onCompleteActiveTask = viewModel::completeTaskAndAdvance,
                                    onDeferActiveTask = viewModel::deferTask,
                                    onExtendActiveTaskOvertime = { viewModel.extendActiveTaskOvertime(5) },
                                    onAddQuickTaskToQueue = { title, mins ->
                                        viewModel.addTaskToQueue(title, mins, "Study", "", false)
                                    },
                                    onToggleTaskSubStep = viewModel::toggleTaskSubStep,
                                    onOpenTaskSequencer = { viewModel.navigateTo(MonolithDestination.TASKS) },
                                    onOpenCurioVault = { viewModel.navigateTo(MonolithDestination.VAULT) },
                                    onIntentionChanged = viewModel::setIntentionText,
                                    onProfileSelected = viewModel::selectAndApplyProfile,
                                    // Handover spender 1: the slide
                                    // ATTEMPT spends it — a refused
                                    // slide has still spent the act.
                                    onArmSession = {
                                        armedHandover = null
                                        viewModel.armMonolithSession()
                                    },
                                    onRequestDisarm = viewModel::requestDisarmChallenge,
                                    onCancelWithinGraceWindow = {
                                        viewModel.confirmDisarmSession("Accidental Start Grace Cancel")
                                    },
                                    onOpenAlwaysOnDeskClock = { viewModel.toggleAlwaysOnDeskClock(true) },
                                    onOpenNotificationDigest = { viewModel.toggleNotificationDigestSheet(true) },
                                    onEndSafeBreak = { viewModel.endSafeBreakEarly() },
                                    weeklyEmergencyTokensRemaining = weeklyEmergencyTokensRemaining,
                                    appsCellCount = appsCellCount,
                                    appsSplit = appsSplit,
                                    deckWarrant = deckWarrant,
                                    sealFreeze = sealFreeze,
                                    armPhase = armPhase,
                                    armSentence = armSentence,
                                    armResetKey = armAnnulCount,
                                    struckSessionId = struckSessionId,
                                    onSessionStruck = viewModel::markSessionStruck,
                                    sealT0MonoMs = (armPhase as? ArmPhase.SealedRunning)?.t0Mono,
                                    finishState = finishState,
                                    receiptState = receiptState,
                                    onWitnessSledReturn = viewModel::witnessSledReturn,
                                    onTryStartSledReturn = viewModel::tryStartSledReturn,
                                    sledInitialFraction = {
                                        viewModel.sledInitialFraction(sealed)
                                    },
                                    warrantProvider = viewModel::armWarrantNow,
                                    onFixBlockingAccess = {
                                        runCatching { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
                                    },
                                    onFixUsageAccess = {
                                        runCatching { context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
                                    },
                                    onRetryBlockingCheck = viewModel::refreshDeckWarrant,
                                    onHandBackTick = viewModel::playHandBackTick,
                                )
                            }

                            MonolithDestination.SHIELD -> {
                                ShieldMatrixScreen(
                                    allApps = allApps,
                                    filteredApps = filteredApps,
                                    searchQuery = searchQuery,
                                    selectedCategory = selectedCategory,
                                    shortsReelsSniperEnabled = shortsReelsSniperEnabled,
                                    browserUrlBlockerEnabled = browserUrlBlockerEnabled,
                                    profiles = profiles,
                                    selectedProfileName = selectedProfileName,
                                    loadoutCapIndex = loadoutCapIndex,
                                    isSessionActive = sealed,
                                    ratchetWarningMessage = ratchetWarningMessage,
                                    soundEngine = viewModel.soundEngine,
                                    isDarkTheme = effectiveNight,
                                    onSearchQueryChanged = viewModel::updateSearchQuery,
                                    onSelectCategory = viewModel::selectCategoryFilter,
                                    onSetAppTier = { pkg, tier ->
                                        val app = allApps.find { it.packageName == pkg }
                                        if (app != null) {
                                            viewModel.setAppBlockTier(app, tier)
                                        }
                                    },
                                    onApplySmartLoadout = viewModel::applySmartLoadout,
                                    onSelectProfile = viewModel::selectAndApplyProfile,
                                    onToggleShortsReelsSniper = viewModel::toggleShortsReelsSniper,
                                    onToggleBrowserUrlBlocker = viewModel::toggleBrowserUrlBlocker,
                                    routines = routines,
                                    showResetNote = showRoutineResetNote,
                                    onToggleRoutine = viewModel::toggleRoutine,
                                    onCycleRoutineLoadout = viewModel::cycleRoutineLoadout,
                                    onAdjustRoutineTime = viewModel::adjustRoutineTime,
                                    onToggleRoutineDay = viewModel::toggleRoutineDay,
                                    blockedSites = blockedSites,
                                    onAddSite = viewModel::addBlockedSite,
                                    onRemoveSite = viewModel::removeBlockedSite,
                                    pruneNoteText = pruneNote?.let { note ->
                                        "${note.appsRemoved} APPS NOT ON THIS PHONE WERE TAKEN OFF THIS LIST" +
                                            if (note.rulesRemoved) " — AND THEIR RULES WITH THEM" else ""
                                    },
                                    onPruneNoteDismissed = viewModel::dismissPruneNote
                                )
                            }

                            MonolithDestination.TASKS -> {
                                FocusSequencerScreen(
                                    activeTask = activeTask,
                                    queuedTasks = queuedTasks,
                                    completedTasks = completedTasks,
                                    deferredTasks = deferredTasks,
                                    deletedTaskForUndo = deletedTaskForUndo,
                                    totalQueueMinutes = totalQueueMinutes,
                                    isSessionActive = sealed,
                                    soundEngine = viewModel.soundEngine,
                                    isDarkTheme = effectiveNight,
                                    onAddTask = viewModel::addTaskToQueue,
                                    onEditTask = viewModel::editTask,
                                    onUpdateTaskDuration = viewModel::updateTaskDuration,
                                    onCueTaskToFocus = viewModel::cueTaskAndNavigateToFocus,
                                    onCompleteTask = viewModel::completeTaskAndAdvance,
                                    onPromoteTask = viewModel::promoteTaskToActive,
                                    onMoveTaskOrder = viewModel::moveTaskOrder,
                                    onDeferTask = viewModel::deferTask,
                                    onDeleteTask = viewModel::deleteTaskWithUndo,
                                    onUndoDeleteTask = viewModel::undoDeleteTask,
                                    onDismissUndoBanner = viewModel::dismissUndoDeleteBanner,
                                    onReopenTask = viewModel::reopenTask,
                                    onToggleSubStep = viewModel::toggleTaskSubStep,
                                    onAddSubStep = viewModel::addTaskSubStep,
                                    onAutoSplitTask = viewModel::autoSplitTaskIntoMicroSteps,
                                    onNavigateToDeck = { viewModel.navigateTo(MonolithDestination.DECK) }
                                )
                            }

                            MonolithDestination.VAULT -> {
                                DrawerScreen(
                                    storedMonths = storedMonths,
                                    drawerMonth = drawerMonth,
                                    drawerRows = drawerRows,
                                    drawerHasEarlier = drawerHasEarlier,
                                    drawerQuery = drawerQuery,
                                    drawerResults = drawerResults,
                                    drawerSearchHasEarlier = drawerSearchHasEarlier,
                                    dayWords = dayWords,
                                    isSealed = sealed,
                                    isDarkTheme = effectiveNight,
                                    onSelectMonth = viewModel::selectDrawerMonth,
                                    onLoadEarlier = viewModel::loadEarlierDrawer,
                                    onQueryChange = viewModel::setDrawerQuery,
                                    onLoadEarlierSearch = viewModel::loadEarlierDrawerSearch,
                                    onDeclareTodayWord = viewModel::declareTodayWord,
                                    artifacts = com.example.data.CurioCatalog.artifacts,
                                    forgedArtifactIds = forgedArtifactIds
                                )
                            }
                        }
                    }

                    // PARK (chrome C4): hosted in the viewport, never
                    // the root — it rises over the current screen,
                    // under the header band, above the rail frame.
                    ParkDrawer(
                        visible = parkOpen,
                        snapshot = watchSnapshot,
                        isSealed = sealed,
                        isNight = effectiveNight,
                        soundEnabled = soundEnabled,
                        shortsReelsSniperEnabled = shortsReelsSniperEnabled,
                        soundEngine = viewModel.soundEngine,
                        themeReadout = themeEvaluation.readout,
                        parkReturnTick = parkReturnTick,
                        onDismiss = { closePark(dismissReceipt = false) },
                        onFixClick = { item -> openFixPane(item) },
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleShortsReelsSniper = { viewModel.toggleShortsReelsSniper() },
                        // Truth Car 3 (UNA-02): the SOCIAL SITES
                        // board line's sled — reads and writes the
                        // same persisted browserBlockerEnabled fact
                        // the accessibility service enforces.
                        socialSitesEnabled = browserUrlBlockerEnabled,
                        onToggleSocialSites = { viewModel.toggleBrowserUrlBlocker() },
                        onOpenReception = {
                            closePark(dismissReceipt = false)
                            receptionOpen = true
                        },
                        setupStampMask = if (setupStamped) {
                            setupStore.doorsAtStampMask()
                        } else {
                            null
                        },
                        handoverText = armedHandover?.text,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )

                    // The seam fade (chrome C9 F-04): a 24dp gradient
                    // directly above the rail — content dissolves
                    // into the ground before it reaches the band. It
                    // carries no content and takes no touch.
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(
                                        androidx.compose.ui.graphics.Color.Transparent,
                                        scheme.background
                                    )
                                )
                            )
                    )
                }

                // THE RAIL (chrome C1): the 52dp band. A stop press
                // while PARK is open is dismissal path (a) — the
                // sheet dismisses AND the cap departs on the same
                // input, and the session receipt sheet goes with it
                // (F-B1).
                AndroidView(
                    factory = { ctx ->
                        MonolithRailView(ctx).apply {
                            this.soundEngine = viewModel.soundEngine
                        }
                    },
                    update = { view ->
                        // The rail steps with the GROUND (t=400),
                        // not at the seal — Zug-II §5.1's table.
                        view.isNightMode = groundNight
                        view.soundEngine = viewModel.soundEngine
                        view.isSessionArmed = sealed
                        view.parkOpen = parkOpen
                        if (view.selectedTabIndex != selectedTabIndex) {
                            view.selectedTabIndex = selectedTabIndex
                        }
                        view.onTabSelected = { idx ->
                            if (parkOpen) closePark(dismissReceipt = true)
                            val dest = when (idx) {
                                0 -> MonolithDestination.DECK
                                1 -> MonolithDestination.SHIELD
                                2 -> MonolithDestination.TASKS
                                else -> MonolithDestination.VAULT
                            }
                            viewModel.navigateTo(dest)
                        }
                        view.onParkInvoked = {
                            if (parkOpen) {
                                closePark(dismissReceipt = false)
                            } else {
                                openPark()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("monolith_rail_view")
                )
            }

            // =================================================================
            // FULL-SCREEN OVERLAYS & MODALS
            // =================================================================
            AlwaysOnDeskClockOverlay(
                visible = isAlwaysOnDeskClockVisible,
                activeSession = activeSession,
                activeTask = activeTask,
                dialMinutes = dialMinutes,
                burnInStep = burnInFigure8MinuteStep,
                onDismiss = { viewModel.toggleAlwaysOnDeskClock(false) }
            )

            // The digest renders the FROZEN snapshot whenever a
            // freeze exists or a session is sealing the phone — the
            // live list is passed only when unsealed and unfrozen.
            val digestFrozen = frozenDigest != null || sealed
            CapturedNotificationsDigestSheet(
                visible = showNotificationDigestSheet,
                notifications = when {
                    frozenDigest != null -> frozenDigest!!.items
                    digestFrozen -> emptyList()
                    else -> capturedNotifications
                },
                frozenAtMillis = frozenDigest?.frozenAtMillis,
                frozenMode = digestFrozen,
                onClearAll = viewModel::clearAllCapturedNotifications,
                onDeleteItem = viewModel::deleteCapturedNotification,
                onDismiss = { viewModel.toggleNotificationDigestSheet(false) }
            )

            // THE RECEIPT SLIP (Slice E, F-L1/F-B1): viewport-
            // hosted over the Deck's room, dismissed by the next
            // DOWN (its scrim), by navigation, or by PARK — the
            // two sheets (this and the digest) never stack: PARK
            // and the digest live in their own invocations, and
            // opening either dismisses this one first.
            val standingReceipt = receiptState
            if (standingReceipt != null &&
                dismissedReceiptSessionId != standingReceipt.sessionId &&
                currentDestination == MonolithDestination.DECK
            ) {
                com.example.ui.components.ReceiptSheet(
                    receipt = standingReceipt,
                    onDismiss = { dismissedReceiptSessionId = standingReceipt.sessionId }
                )
            }

            // THE RECEPTION (Empfang lane, Oct 30 — grafted seat):
            // hosted in the overlay layer, BELOW the airlock and
            // the disarm challenge — safety surfaces always win,
            // and the sheet never renders its introduction over a
            // session sealing the phone (visible gated on !sealed).
            ReceptionSheet(
                visible = receptionOpen && !sealed,
                snapshot = watchSnapshot,
                isFirstOpen = !setupStamped,
                isNight = effectiveNight,
                returnTick = parkReturnTick,
                soundEngine = viewModel.soundEngine,
                firstRoutineLine = receptionFirstRoutineLine(
                    routines,
                    System.currentTimeMillis()
                ),
                onShown = { viewModel.runWatchSweep() },
                onFixClick = { item -> openFixPane(item) },
                onCheckClick = { viewModel.runWatchSweep() },
                onOpenShield = {
                    // H-2: only the unstamped sheet's excursion
                    // records a departure; a stamped sheet's home
                    // is PARK and its return is PARK.
                    if (!setupStamped) shieldExcursion = true
                    receptionOpen = false
                    viewModel.navigateTo(MonolithDestination.SHIELD)
                },
                onStamp = { stampSetupOnly() },
                onDone = { stampSetupAndEnter() },
                chit = chitUi,
                onDeparture = { door, print ->
                    setupStore.writeDepartureChit(door, print)
                }
            )

            // The ShieldPermissionPrimerModal binding stays REMOVED
            // (Slice D): its "COMMIT & ARM ANYWAY" path was the
            // register's LR2-S1-18 — the slide reads the warrant
            // itself now and refuses in the open, with the FIX
            // routes on the sentence itself.

            // THE LAUNCHER BLOCK SURFACE: hosted under the Airlock,
            // so a fresh intercept's door always stands above it.
            LauncherBlockScreen(
                visible = launcherOpen && sealed,
                session = activeSession,
                apps = allApps,
                initialPage = launcherPage,
                onClose = { launcherOpen = false },
                onLaunchApp = { app ->
                    launcherOpen = false
                    runCatching {
                        context.packageManager
                            .getLaunchIntentForPackage(app.packageName)
                            ?.let { launch ->
                                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(launch)
                            }
                    }
                },
                onPeekApp = { app ->
                    launcherOpen = false
                    viewModel.triggerAirlockIntercept(
                        app.packageName,
                        app.appName,
                        app.resolvedTier.badge,
                        InterceptPath.APP_TIER
                    )
                },
                onRequestEmergencyBreak = {
                    launcherOpen = false
                    viewModel.requestDisarmChallenge()
                }
            )

            AirlockInterceptOverlay(
                state = airlockState,
                onSelectReason = viewModel::selectDistractionReason,
                onTransmuteParticleTap = viewModel::incrementAirlockParticlePulse,
                onReturnToFocus = viewModel::transmuteImpulseAndReturn,
                onTake2MinutePeek = viewModel::start2MinutePeek,
                allowedAppsCount = allApps.count {
                    it.resolvedTier == com.example.data.AppBlockTier.OPEN
                },
                peekAppsCount = allApps.count {
                    it.resolvedTier == com.example.data.AppBlockTier.PEEK_2M
                },
                onOpenAllowedApps = {
                    launcherPage = LauncherPage.ALLOWED
                    launcherOpen = true
                },
                onOpenPeekApps = {
                    launcherPage = LauncherPage.PEEK
                    launcherOpen = true
                }
            )

            DisarmChallengeOverlay(
                state = disarmChallenge,
                tokensRemaining = weeklyEmergencyTokensRemaining,
                soundEngine = viewModel.soundEngine,
                onDismissKeepFocusing = viewModel::dismissDisarmChallenge,
                onCompleteDisarm = { viewModel.completeEmergencyBreakFromValve() },
                onHoldStarted = viewModel::recordValveHoldStarted
            )

        }
    }
}

/** Stop order for KIN-TAB-IN's direction: the incoming screen
 *  translates in the direction the stops are printed. */
private fun stopIndexOf(destination: MonolithDestination): Int = when (destination) {
    MonolithDestination.DECK -> 0
    MonolithDestination.SHIELD -> 1
    MonolithDestination.TASKS -> 2
    MonolithDestination.VAULT -> 3
}
