package com.example

import android.Manifest
import android.app.AlarmManager
import android.app.admin.DevicePolicyManager
import android.content.pm.PackageManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.service.InterceptConsumptionStore
import com.example.service.InterceptNonce
import com.example.service.InterceptPath
import com.example.service.MonolithDeviceAdminReceiver
import com.example.service.MonolithShieldService
import com.example.service.PermissionReturnService
import com.example.service.WatchStore
import com.example.ui.MonolithDestination
import com.example.ui.ArmPhase
import com.example.ui.MonolithViewModel
import com.example.ui.components.AirlockInterceptOverlay
import com.example.ui.components.BlockitActionChip
import com.example.ui.components.AlwaysOnDeskClockOverlay
import com.example.ui.components.LauncherBlockScreen
import com.example.ui.components.PeekCountdownPill
import com.example.ui.components.LauncherPage
import com.example.ui.components.CapturedNotificationsDigestSheet
import com.example.ui.components.DisarmChallengeOverlay
import com.example.ui.components.MonolithPullDownHeader
import com.example.ui.components.ParkDrawer
import com.example.ui.screens.DrawerScreen
import com.example.ui.screens.FocusSequencerScreen
import com.example.ui.screens.ShieldMatrixScreen
import com.example.ui.screens.TactileDeckScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.views.MonolithRailView

/** Issue #78 (Audit R2, ipc-intents): the system Settings app's
 *  package — the ONLY lawful home of every Settings pane this app
 *  navigates to. */
internal const val SYSTEM_SETTINGS_PACKAGE = "com.android.settings"

/**
 * A Settings intent PINNED to the system Settings app. An unpinned
 * implicit Settings intent enters system-wide resolution: any
 * installed app declaring a matching intent-filter can be picked
 * and present a fake Settings pane in place of the real one. The
 * FIX routes hand the user to permission panes for THIS app's own
 * protection grants — that hand-off is never offered to a resolver.
 */
internal fun pinnedSettingsIntent(action: String, data: Uri? = null): Intent {
    val intent = if (data != null) Intent(action, data) else Intent(action)
    return intent.setPackage(SYSTEM_SETTINGS_PACKAGE)
}

class MainActivity : ComponentActivity() {

    // Issue #27 (finding 1): intercept intents that arrive while
    // the Activity is alive (singleTop re-delivery lands in
    // onNewIntent) are bridged into composition through this
    // state. setIntent() alone is invisible to Compose, which is
    // how re-intercepts used to be silently dropped.
    private var pendingInterceptIntent by mutableStateOf<Intent?>(null)

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

        // Watch Store bind (issue #48): LAST PROVED restored before
        // any surface reads the store.
        WatchStore.bind(applicationContext)

        // ISSUE-67: the repository comes from the single
        // construction point (AppContainer: DAO + live service
        // port + the same database instance).
        val repository = AppContainer.repository(applicationContext)
        // Issue #27 (finding 2): the ViewModel is obtained through
        // the provider BEFORE any engine exists. On recreation the
        // provider returns the retained instance and the Factory's
        // create() never runs — no orphaned sound engine, and the
        // retained ViewModel's engine is never stopped by an
        // Activity's onDestroy (its death is onCleared's job).
        val rootViewModel = ViewModelProvider(
            this,
            MonolithViewModel.Factory(
                appContext = applicationContext,
                repository = repository
            )
        )[MonolithViewModel::class.java]
        pendingInterceptIntent = intent

        setContent {
            val viewModel = rootViewModel
            val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
            val activeSessionForTheme by viewModel.activeSession.collectAsStateWithLifecycle()
            val shotGround by viewModel.shotGround.collectAsStateWithLifecycle()

            // Issue #27 (finding 1): every pending intent — the
            // launch intent AND any onNewIntent re-delivery — is
            // handled exactly once, then cleared.
            LaunchedEffect(pendingInterceptIntent) {
                val pending = pendingInterceptIntent
                if (pending != null) {
                    handleIncomingInterceptIntent(pending, viewModel)
                    pendingInterceptIntent = null
                }
            }

            MyApplicationTheme(
                darkTheme = darkTheme,
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
        // Issue #27 (finding 1): bridge the re-delivered intent
        // into composition — without this, a re-intercept while
        // the Activity is alive never reaches the Airlock.
        pendingInterceptIntent = intent
    }

    private fun handleIncomingInterceptIntent(intent: Intent?, viewModel: MonolithViewModel) {
        if (intent == null) return
        // Issue #155 (F5, re-homed at the STURM-4 merge): a tap on
        // the shield notification is about the shield — land on
        // SHIELD, not the default Deck. Intercept extras take
        // precedence: when a firing's package rides the same intent,
        // the intercept path below owns the delivery and this extra
        // is simply consumed.
        val fromShieldNotification = intent.getBooleanExtra(
            MonolithShieldService.EXTRA_FROM_SHIELD_NOTIFICATION, false
        )
        if (fromShieldNotification) {
            intent.removeExtra(MonolithShieldService.EXTRA_FROM_SHIELD_NOTIFICATION)
        }
        val pkg = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPTED_PACKAGE)
        if (pkg.isNullOrBlank()) {
            if (fromShieldNotification) {
                viewModel.navigateTo(MonolithDestination.SHIELD)
            }
            return
        }
        // Issue #68 F5, Tier 2 — provenance before trust: this
        // activity is exported (LAUNCHER), so any installed app
        // can send these extras. Honour them only with the
        // single-use nonce an in-app service minted moments ago
        // (InterceptNonce); without one the extras are dropped
        // here and never reach the airlock.
        val nonce = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPT_NONCE)
        if (!InterceptNonce.consume(applicationContext, nonce)) {
            intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPTED_PACKAGE)
            intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPT_PATH)
            intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPT_NONCE)
            intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPT_ID)
            return
        }
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
        // Issue #78: consumption is PERSISTED, not just in-memory.
        // The platform retains the launching intent and redelivers
        // it intact after process death, so removeExtra on this
        // copy alone let the same stale Airlock fire again. The
        // firing's id is marked consumed (synchronous commit)
        // BEFORE the Airlock can show; an already-consumed
        // redelivery is dropped, and a delivery whose consumption
        // cannot be persisted is dropped too (fail closed — the
        // enforcement door fires a fresh intercept if the user is
        // actually standing in the blocked app). Id-less deliveries
        // are fingerprinted from their extras: the same stale copy
        // redelivered is the same fingerprint. (Merge note,
        // STURM3-15: the nonce gate above runs first — it is the
        // provenance proof; this store is the redelivery memory.
        // A lawful firing carries both, and must pass both.)
        val interceptId = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPT_ID)
            ?.takeIf { it.isNotBlank() }
            ?: InterceptConsumptionStore.fingerprint(pkg, name, badge, path.name)
        intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPTED_PACKAGE)
        intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPTED_APP_NAME)
        intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPT_BADGE)
        intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPT_PATH)
        intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPT_ID)
        intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPT_NONCE)
        if (InterceptConsumptionStore.isConsumed(this, interceptId)) return
        if (!InterceptConsumptionStore.markConsumed(this, interceptId)) return
        // The showing itself is revalidated in the VM against the
        // live session and the package's live tier/lease — a
        // first-delivered but long-delayed intent does not resurrect
        // an Airlock its facts no longer support.
        viewModel.triggerAirlockInterceptFromIntent(pkg, name, badge, path)
    }

    // Issue #27 (finding 2): there is deliberately NO onDestroy
    // audio stop here any more. The old override stopped the
    // Activity's own engine field — which WAS the retained
    // ViewModel's engine — on every rotation, killing all sound
    // until process death. The ViewModel stops its engine in
    // onCleared(), the one true end of its life.
}

// ------------------------------------------------------------------
// Issue #90 (complexity F3, hoist step): these four helpers used to
// be nested funs inside MonolithRootApp — three of them named in
// the audit finding itself. They are file-level now, with every
// captured value passed explicitly; bodies are verbatim, so
// behaviour is unchanged and each helper is testable in isolation.
// ------------------------------------------------------------------

private fun closePark(
    parkOpenState: MutableState<Boolean>,
    digestSheetVisible: Boolean,
    viewModel: MonolithViewModel,
    dismissReceipt: Boolean
) {
    parkOpenState.value = false
    if (dismissReceipt && digestSheetVisible) {
        viewModel.toggleNotificationDigestSheet(false)
    }
    // The intro bit is NOT spent here (issue-156, F3): a
    // dismissal — above all a reflexive scrim tap before the
    // sweep has printed a single word — spends nothing. The
    // bit is spent on delivery, by the effect in the root, once
    // the measured board is actually on screen.
}

private fun openPark(
    parkOpenState: MutableState<Boolean>,
    sealed: Boolean,
    viewModel: MonolithViewModel
) {
    // HB-2: the tap-time read of the SAME doorLocked predicate —
    // the synchronous backstop for a Guard tap landing inside
    // the finish machine's detention window before the state
    // collectors recompose. The predicate is evaluated HERE, at
    // the tap: navigationLocked() reads the VM's flows directly,
    // so the answer is true even while the composition's
    // doorLocked still holds its pre-detention value. (The
    // first form of this guard read the captured doorLocked
    // val — a print, not a lock; hb4DetentionGuardTap caught
    // it on the gate build's first full test run.)
    if (!sealed && !viewModel.navigationLocked()) parkOpenState.value = true
}

// Issue #27 (finding 5): a FIX trip must never die silently.
// Every Settings launch goes through here: the exact pane
// first; if the OEM throws, the generic Settings home is the
// fallback; if even that fails, the user is TOLD, in words,
// instead of being stranded on a dead tap.
@Composable
fun MonolithRootApp(viewModel: MonolithViewModel) {
    val context = LocalContext.current
    // Issue #339 (Second-pass F1): the Web Gate's persisted store
    // and its render-cache states (the store is the truth).
    val webGateStore = remember { com.example.data.WebGateStore(context) }
    var webBlockAllState by remember { mutableStateOf(webGateStore.blockAll()) }
    var allowedWebHostsState by remember { mutableStateOf(webGateStore.allowedHosts()) }
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
    val profileUndoAvailable by viewModel.profileUndoAvailable.collectAsStateWithLifecycle()
    val intentionText by viewModel.intentionText.collectAsStateWithLifecycle()
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()

    // Issue #70 F3: the Deck primer's bit flips the moment the
    // first session is live — the primer is a first-run-only
    // surface and never returns.
    LaunchedEffect(activeSession) {
        if (activeSession?.isActive == true && viewModel.deckPrimerPending()) {
            viewModel.markDeckPrimerDone()
        }
    }
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val allApps by viewModel.apps.collectAsStateWithLifecycle()
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val pruneNote by viewModel.pruneNote.collectAsStateWithLifecycle()
    val legacyOpenResetCount by viewModel.legacyOpenResetCount.collectAsStateWithLifecycle()
    val unreviewedOpenNames by viewModel.unreviewedOpenNames.collectAsStateWithLifecycle()
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
    val activeTaskFromYesterday by viewModel.activeTaskFromYesterday.collectAsStateWithLifecycle()
    val freshStartNoteVisible by viewModel.freshStartNoteVisible.collectAsStateWithLifecycle()
    val updateNoteVisible by viewModel.updateNoteVisible.collectAsStateWithLifecycle()
    val frozenDigest by viewModel.frozenDigest.collectAsStateWithLifecycle()
    val storedMonths by viewModel.storedMonths.collectAsStateWithLifecycle()
    val drawerMonth by viewModel.drawerMonth.collectAsStateWithLifecycle()
    val drawerRows by viewModel.drawerRows.collectAsStateWithLifecycle()
    val drawerHasEarlier by viewModel.drawerHasEarlier.collectAsStateWithLifecycle()
    val drawerQuery by viewModel.drawerQuery.collectAsStateWithLifecycle()
    val drawerResults by viewModel.drawerResults.collectAsStateWithLifecycle()
    val drawerSearchHasEarlier by viewModel.drawerSearchHasEarlier.collectAsStateWithLifecycle()
    val drawerLoading by viewModel.drawerLoading.collectAsStateWithLifecycle()
    val drawerError by viewModel.drawerError.collectAsStateWithLifecycle()
    val drawerSearchDayWords by viewModel.drawerSearchDayWords.collectAsStateWithLifecycle()
    val dayWords by viewModel.dayWords.collectAsStateWithLifecycle()
    val forgedArtifactIds by viewModel.forgedArtifactIds.collectAsStateWithLifecycle()
    val curioForgeRecords by viewModel.curioForgeRecords.collectAsStateWithLifecycle()
    val forgeStats by viewModel.forgeStats.collectAsStateWithLifecycle()
    val receiptIndex by viewModel.receiptIndex.collectAsStateWithLifecycle()
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
    val lastReceiptSessionId by viewModel.lastReceiptSessionId.collectAsStateWithLifecycle()
    val isAlwaysOnDeskClockVisible by viewModel.isAlwaysOnDeskClockVisible.collectAsStateWithLifecycle()
    val burnInFigure8MinuteStep by viewModel.burnInFigure8MinuteStep.collectAsStateWithLifecycle()
    val showNotificationDigestSheet by viewModel.showNotificationDigestSheet.collectAsStateWithLifecycle()
    val capturedNotifications by viewModel.capturedNotifications.collectAsStateWithLifecycle()
    val totalFocusedMinutes by viewModel.totalFocusedMinutes.collectAsStateWithLifecycle()
    val totalInterceptions by viewModel.totalInterceptions.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val airlockState by viewModel.airlockState.collectAsStateWithLifecycle()
    val floatingPeekPill by viewModel.floatingPeekPill.collectAsStateWithLifecycle()
    val disarmChallenge by viewModel.disarmChallenge.collectAsStateWithLifecycle()
    val weeklyEmergencyTokensRemaining by viewModel.weeklyEmergencyTokensRemaining.collectAsStateWithLifecycle()
    val ratchetWarningMessage by viewModel.ratchetWarningMessage.collectAsStateWithLifecycle()
    val tierChangeForUndo by viewModel.tierChangeForUndo.collectAsStateWithLifecycle()

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
    // Issue #143 (F5): the system bars follow the ground night.
    // onCreate pins a light status bar exactly once; without this,
    // dark status glyphs sat on the night ground for the whole
    // session. Re-applied whenever the ground steps, and the day
    // values are restored when it steps back. (This composable is
    // top-level, so the bars are set through the host Activity
    // from LocalContext, not the onCreate receiver.)
    val hostActivity = context as? androidx.activity.ComponentActivity
    LaunchedEffect(groundNight, hostActivity) {
        hostActivity?.enableEdgeToEdge(
            statusBarStyle = if (groundNight) {
                // SystemBarStyle.dark takes ONE scrim arg (the
                // two-arg form is light()'s) — the two-arg call
                // was PR #359's CI compile failure, fixed here.
                SystemBarStyle.dark(
                    android.graphics.Color.TRANSPARENT
                )
            } else {
                SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
            },
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )
    }
    // The receipt sheet's dismissal is host state (F-B1): the
    // slip stands until its own scrim takes the next DOWN — it
    // is never dismissed by a timer, by navigation, or by PARK.
    // Off the Deck it simply unmounts (its render gate below);
    // the flag stays untouched, so it re-stands on return.
    // (Issue #157: the navigation/PARK dismissal effect is
    // deleted — the tap to "Receipts" was the shredder.)
    // Issue #27 (finding 4): host state is rememberSaveable, not
    // plain remember — a rotation must not resurrect a dismissed
    // receipt, snap PARK shut mid-diagnosis, or restart the
    // return-tick diagnostic.
    // Audit R1 #40: the saveable is SEEDED from the persisted
    // dismissal (ReceiptDismissalStore) — rememberSaveable covers
    // rotation and state-restore, but a put-away receipt must
    // also survive a cold process death with no saved state to
    // restore; every dismissal below writes through to the store
    // (viewModel.persistReceiptDismissal).
    var dismissedReceiptSessionId by rememberSaveable {
        mutableStateOf(viewModel.dismissedReceiptSessionId())
    }

    // ------------------------------------------------------------------
    // PARK state (chrome C4). The drawer is invoked by arrival (the
    // rail), by the Guard line's tap, and — on the first run only —
    // by itself, once the first measured snapshot lands and the
    // door is open. While sealed it cannot be invoked at all.
    // ------------------------------------------------------------------
    // Issue #90 (complexity F3): the PARK open flag's state holder
    // is named so the hoisted file-level closePark/openPark below
    // can take it as an explicit parameter; every existing read and
    // write of parkOpen goes through this same holder, unchanged.
    val parkOpenState = rememberSaveable { mutableStateOf(false) }
    var parkOpen by parkOpenState
    var parkReturnTick by rememberSaveable { mutableStateOf(0) }

    // THE LAUNCHER (Sarthak's ruling, Oct 30 04:29 IST): while the
    // phone is sealed, the block surface is a launcher. It opens
    // from the Guard line's tap (a dead tap while sealed until now —
    // PARK refuses under seal) and from the Airlock's two doors.
    // D-07 (design-revamp salvage, 2dac73e): launcher state is
    // rememberSaveable, like the neighbouring host state above
    // (issue #27 pattern) — plain remember lost the open launcher
    // and its page to any Activity recreation / process death,
    // above all while the user was inside a launched allowed app.
    var launcherOpen by rememberSaveable { mutableStateOf(false) }
    var launcherPage by rememberSaveable { mutableStateOf(LauncherPage.HOME) }

    // closePark / openPark are file-level functions since the
    // issue-90 F3 hoist (they were nested inside this composable);
    // call sites pass parkOpenState, the digest-sheet visibility
    // and the seal explicitly.

    // Issue #157: there is deliberately NO effect here dismissing
    // the receipt slip on navigation or on PARK's invocation —
    // that effect (F-B1) was the shredder: the tap to "Receipts"
    // itself destroyed the standing slip. The slip's only
    // dismissal is its own scrim, and any claimed slip can be
    // re-presented from the Vault receipts index regardless.


    // First run (§5.7, issue-156 F1+F2): the auto-open obeys the
    // SAME door predicate as openPark() — a seal defers the intro,
    // never burns it — and it waits for the first measured
    // snapshot, so the board never rises blank: it arrives with
    // state words and, if a lack exists, the FIX key. Keyed on
    // doorLocked and the snapshot, so it re-fires the moment the
    // seal lifts or the first measurement lands while the intro
    // is still pending. The persisted bit keeps a cold start's
    // unmeasured instant from re-inviting on later runs.
    LaunchedEffect(doorLocked, watchSnapshot) {
        if (!sealed && !viewModel.navigationLocked() &&
            viewModel.parkIntroPending() &&
            watchSnapshot.items.isNotEmpty()
        ) {
            parkOpen = true
        }
    }

    // The intro header's latch for the current opening (issue-156,
    // F5): pending is read at the moment PARK opens, BEFORE the
    // delivery-spend below can land, and held for that whole
    // opening — the spend must not unprint the header mid-reading
    // on the next snapshot emission.
    var parkIntroForOpen by remember { mutableStateOf(false) }
    LaunchedEffect(parkOpen) {
        parkIntroForOpen = parkOpen && viewModel.parkIntroPending()
    }

    // The intro is spent on DELIVERY, not on dismissal (issue-156,
    // F3): once PARK stands open over a measured board, the intro
    // has been delivered — mark it done. Declared after the latch
    // above so the latch reads the pending bit first.
    LaunchedEffect(parkOpen, watchSnapshot) {
        if (parkOpen && watchSnapshot.items.isNotEmpty() &&
            viewModel.parkIntroPending()
        ) {
            viewModel.markParkIntroDone()
        }
    }

    // Issue #280 (R5 supplement): POST_NOTIFICATIONS was declared
    // in the manifest but never asked, so the seal's own
    // notifications could be silently unpostable. It is asked
    // once, as a runtime permission, when the first-run board is
    // delivered — not as a Settings trip and never as a gate: the
    // board and the seal work the same whether it is granted.
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    var notifAsked by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(parkIntroForOpen) {
        if (parkIntroForOpen && !notifAsked &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notifAsked = true
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Issue #109, F3: opening PARK fires the real Watch sweep.
    // Until now the board opened on whatever the last ON_RESUME /
    // Deck-entry sweep left behind, and its own GRANTED lines aged
    // into STALE in front of the reader with no re-measure anywhere
    // on the surface. The sweep is the board's re-measure; the
    // drawer's RE-CHECK key fires the same one on demand.
    // Issue #75 F2 (re-cut, STURM3-15): the old refreshSystemGuard()
    // diagnostic sweep that also fired here — its own comment
    // admitted its result rendered NOWHERE — is removed. The cut
    // shipped in PR #375's branch but was lost in that branch's
    // merge chain before it merged; the ViewModel's own init caller
    // of refreshSystemGuard is untouched.
    LaunchedEffect(parkOpen) {
        if (parkOpen) {
            viewModel.runWatchSweep()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // Issue #99 F3: stopping with an Airlock still standing
            // means the user left by Home/Recents — the VM logs it.
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.noteAirlockAbandonedIfStanding()
            }
            if (event == Lifecycle.Event.ON_RESUME) {
                // Return is proving (chrome C4 §4.4): the sweep
                // re-reads every grant; a FIX trip's hand-back is
                // judged against these landings.
                // Issue #308: the user is back (by the return
                // service or by hand) — any return watch ends here.
                PermissionReturnService.stop(context)
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
        BackHandler {
            closePark(parkOpenState, showNotificationDigestSheet, viewModel, dismissReceipt = false)
        }
    } else if (currentDestination != MonolithDestination.DECK) {
        BackHandler { viewModel.navigateTo(MonolithDestination.DECK) }
    }

    // Issue #27 (finding 3): full-screen overlays own Back. Before
    // this handler, Back on the DECK fell through to the default
    // dispatcher and FINISHED the Activity — including from the
    // Airlock, where Back was an escape hatch out of the
    // intercept. Priority: the Airlock holds (Back is consumed,
    // no exit — the door's own RETURN/PEEK are the only ways
    // out), then the disarm challenge (Back = keep focusing),
    // then the desk clock, the digest sheet, and the receipt.
    val receiptStandingForBack = receiptState != null &&
        dismissedReceiptSessionId != receiptState!!.sessionId &&
        currentDestination == MonolithDestination.DECK
    BackHandler(
        enabled = airlockState.isVisible || disarmChallenge.isVisible ||
            isAlwaysOnDeskClockVisible || showNotificationDigestSheet ||
            receiptStandingForBack
    ) {
        when {
            airlockState.isVisible -> Unit // the door holds; Back is not an exit
            disarmChallenge.isVisible -> viewModel.dismissDisarmChallenge()
            isAlwaysOnDeskClockVisible -> viewModel.toggleAlwaysOnDeskClock(false)
            showNotificationDigestSheet -> viewModel.toggleNotificationDigestSheet(false)
            receiptStandingForBack -> dismissedReceiptSessionId = receiptState!!.sessionId
        }
    }

    val selectedTabIndex = when (currentDestination) {
        MonolithDestination.DECK -> 0
        MonolithDestination.SHIELD -> 1
        MonolithDestination.TASKS -> 2
        MonolithDestination.VAULT -> 3
    }

    // Issue #27 (finding 5): a FIX trip must never die silently.
    // Every Settings launch goes through here: the exact pane
    // first; if the OEM throws, the generic Settings home is the
    // fallback; if even that fails, the user is TOLD, in words,
    // instead of being stranded on a dead tap.
    fun launchSettings(intent: Intent, what: String) {
        runCatching { context.startActivity(intent) }
            .onFailure {
                runCatching { context.startActivity(pinnedSettingsIntent(Settings.ACTION_SETTINGS)) }
                    .onFailure {
                        android.widget.Toast.makeText(
                            context,
                            "COULDN'T OPEN SETTINGS FOR $what — OPEN ANDROID SETTINGS AND GRANT IT FROM APPS",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }

            }
    }

    // The FIX key's panes: the exact Settings pane per grant; device
    // admin instead fires the official activation intent for this
    // app's own admin (issue #44), with Security Settings as fallback.
    fun openFixPane(item: com.example.service.WatchItemId) {
        // Issue #308: arm the return trip BEFORE the pane opens —
        // the service polls this item's grant and brings the user
        // back the moment it lands, instead of stranding them in
        // Settings.
        PermissionReturnService.arm(context, item)
        when (item) {
            com.example.service.WatchItemId.ACCESSIBILITY ->
                launchSettings(pinnedSettingsIntent(Settings.ACTION_ACCESSIBILITY_SETTINGS), "ACCESSIBILITY")
            com.example.service.WatchItemId.USAGE_ACCESS ->
                launchSettings(pinnedSettingsIntent(Settings.ACTION_USAGE_ACCESS_SETTINGS), "USAGE ACCESS")
            com.example.service.WatchItemId.DEVICE_ADMIN -> {
                // Issue #44: ask Android for THIS admin through the
                // official activation intent — the old hardcoded
                // com.android.settings component only opened a
                // generic list (and broke wherever that class name
                // differed) and never actually requested the admin.
                // Security Settings is only the fallback for when
                // the official intent itself cannot be fired.
                val requestAdmin = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(
                        DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                        ComponentName(context, MonolithDeviceAdminReceiver::class.java)
                    )
                    putExtra(
                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        context.getString(R.string.device_admin_add_explanation)
                    )
                }
                runCatching { context.startActivity(requestAdmin) }
                    .getOrElse {
                        launchSettings(pinnedSettingsIntent(Settings.ACTION_SECURITY_SETTINGS), "DEVICE ADMIN")
                    }
            }
            com.example.service.WatchItemId.NOTIFICATION_LISTENER ->
                launchSettings(
                    pinnedSettingsIntent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"),
                    "NOTIFICATION ACCESS"
                )
            com.example.service.WatchItemId.OVERLAY ->
                launchSettings(
                    pinnedSettingsIntent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    ),
                    "DISPLAY OVER OTHER APPS"
                )
            com.example.service.WatchItemId.BATTERY -> {
                // Issue #94 (F1): on an OEM-autostart-gated skin the
                // survival trip is the vendor's AUTOSTART pane first —
                // the AOSP battery pane alone leaves the boot receiver
                // dead. Each vendor component is a guess that may not
                // exist on this build; walk the list, then fall back
                // to the AOSP battery trip.
                // Issue #78 (merge, STURM3-15): the AOSP fallbacks
                // below go through pinnedSettingsIntent like every
                // other Settings route — main's #94 flow arrived with
                // them bare, which would have reopened the unpinned-
                // intent finding on this one pane.
                val gate = com.example.data.OemPolicy.currentGate()
                val autostartOpened = gate !=
                    com.example.data.OemPolicy.AutostartGate.NONE &&
                    com.example.data.OemPolicy
                        .autostartIntents(gate, context.packageName)
                        .any { pane ->
                            runCatching { context.startActivity(pane) }
                                .isSuccess
                        }
                if (!autostartOpened) {
                    val direct = pinnedSettingsIntent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:${context.packageName}")
                    )
                    runCatching { context.startActivity(direct) }
                        .getOrElse {
                            launchSettings(
                                pinnedSettingsIntent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS),
                                "BATTERY"
                            )
                        }
                }
            }
            else -> launchSettings(pinnedSettingsIntent(Settings.ACTION_SETTINGS), item.name)
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
                    guardLine = guardLinePrint,
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
                            parkOpen -> closePark(
                                parkOpenState, showNotificationDigestSheet, viewModel,
                                dismissReceipt = false
                            )
                            // While sealed, the Guard line opens the
                            // launcher — the sealed phone's home.
                            sealed -> {
                                launcherPage = LauncherPage.HOME
                                launcherOpen = true
                            }
                            else -> openPark(parkOpenState, sealed, viewModel)
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
                                    activeTask = activeTask,
                                    activeTaskFromYesterday = activeTaskFromYesterday,
                                    queuedTasks = queuedTasks,
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
                                    onCompleteActiveTask = viewModel::completeTaskAndAdvance,
                                    onToggleTaskSubStep = viewModel::toggleTaskSubStep,
                                    onOpenTaskSequencer = { viewModel.navigateTo(MonolithDestination.TASKS) },
                                    onArmSession = viewModel::armMonolithSession,
                                    onRequestDisarm = viewModel::requestDisarmChallenge,
                                    onCancelWithinGraceWindow = {
                                        viewModel.confirmDisarmSession("Accidental Start Grace Cancel")
                                    },
                                    onOpenAlwaysOnDeskClock = { viewModel.toggleAlwaysOnDeskClock(true) },
                                    onOpenNotificationDigest = { viewModel.toggleNotificationDigestSheet(true) },
                                    onEndSafeBreak = { viewModel.endSafeBreakEarly() },
                                    onExtendSession = viewModel::extendActiveSession,
                                    weeklyEmergencyTokensRemaining = weeklyEmergencyTokensRemaining,
                                    appsCellCount = appsCellCount,
                                    appsSplit = appsSplit,
                                    deckWarrant = deckWarrant,
                                    deckPrimerPending = viewModel.deckPrimerPending(),
                                    sealFreeze = sealFreeze,
                                    armPhase = armPhase,
                                    armSentence = armSentence,
                                    armResetKey = armAnnulCount,
                                    struckSessionId = struckSessionId,
                                    onSessionStruck = viewModel::markSessionStruck,
                                    sealT0MonoMs = (armPhase as? ArmPhase.SealedRunning)?.t0Mono,
                                    finishState = finishState,
                                    receiptState = receiptState,
                                    lastReceiptSessionId = lastReceiptSessionId,
                                    onShowLastReceipt = { sessionId ->
                                        // Un-dismiss first: this slip may
                                        // have been dismissed before the arm
                                        // that cleared it, and the host's
                                        // dismissed id would otherwise hide
                                        // the re-issued slip forever.
                                        dismissedReceiptSessionId = -1
                                        viewModel.publishReceipt(sessionId)
                                    },
                                    onWitnessSledReturn = viewModel::witnessSledReturn,
                                    onTryStartSledReturn = viewModel::tryStartSledReturn,
                                    sledInitialFraction = {
                                        viewModel.sledInitialFraction(sealed)
                                    },
                                    warrantProvider = viewModel::armWarrantNow,
                                    onFixBlockingAccess = {
                                        launchSettings(
                                            pinnedSettingsIntent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
                                            "ACCESSIBILITY"
                                        )
                                    },
                                    onFixUsageAccess = {
                                        launchSettings(
                                            pinnedSettingsIntent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                                            "USAGE ACCESS"
                                        )

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
                                    tierChangeForUndo = tierChangeForUndo,
                                    onUndoTierChange = viewModel::undoLastTierChange,
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
                                    onCycleDailyLimit = viewModel::cycleAppDailyLimit,
                                    onApplySmartLoadout = viewModel::applySmartLoadout,
                                    onSetAppCategory = viewModel::setAppCategory,
                                    onSelectProfile = viewModel::selectAndApplyProfile,
                                    profileUndoAvailable = profileUndoAvailable,
                                    onUndoProfileApply = viewModel::undoLastProfileApply,
                                    onToggleShortsReelsSniper = viewModel::toggleShortsReelsSniper,
                                    onToggleBrowserUrlBlocker = viewModel::toggleBrowserUrlBlocker,
                                    // #337 F1: shortcut binding state
                                    // read live; the chip deep-links
                                    // to the system's shortcut screen,
                                    // falling back to Accessibility
                                    // Settings where the ROM lacks it.
                                    accessibilityShortcutSet =
                                        com.example.service.AccessibilityShortcutSupport
                                            .isMonolithShortcutTarget(context),
                                    onOpenAccessibilityShortcutSettings = {
                                        runCatching {
                                            context.startActivity(
                                                com.example.service.AccessibilityShortcutSupport
                                                    .shortcutSettingsIntent()
                                            )
                                        }.getOrElse {
                                            runCatching {
                                                context.startActivity(
                                                    com.example.service.AccessibilityShortcutSupport
                                                        .accessibilitySettingsIntent()
                                                )
                                            }
                                        }
                                        Unit
                                    },
                                    // #339 F1: the Web Gate — state is
                                    // held here against the persisted
                                    // WebGateStore (the store is the
                                    // truth; these states are its
                                    // render cache).
                                    webBlockAll = webBlockAllState,
                                    onToggleWebBlockAll = {
                                        val next = !webGateStore.blockAll()
                                        webGateStore.setBlockAll(next)
                                        webBlockAllState = next
                                    },
                                    allowedWebHosts = allowedWebHostsState,
                                    onAddAllowedWebHost = { host ->
                                        if (webGateStore.addAllowedHost(host)) {
                                            allowedWebHostsState = webGateStore.allowedHosts()
                                        }
                                    },
                                    onRemoveAllowedWebHost = { host ->
                                        webGateStore.removeAllowedHost(host)
                                        allowedWebHostsState = webGateStore.allowedHosts()
                                    },
                                    routines = routines,
                                    showResetNote = showRoutineResetNote,
                                    onDismissResetNote = viewModel::dismissRoutineResetNote,
                                    showFreshStartNote = freshStartNoteVisible,
                                    showUpdateNote = updateNoteVisible,
                                    onDismissFreshStartNote = viewModel::dismissFreshStartNote,
                                    onDismissUpdateNote = viewModel::dismissUpdateNote,
                                    onToggleRoutine = { routine ->
                                        // Issue #280: enabling a
                                        // routine is the moment the
                                        // exact-alarm grant is asked
                                        // (the original gates its
                                        // routine sheet the same
                                        // way) — once, as an offer,
                                        // never a block: the chain
                                        // alarm and the heartbeat
                                        // run without it.
                                        if (!routine.enabled &&
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                                        ) {
                                            val am = context.getSystemService(
                                                android.content.Context.ALARM_SERVICE
                                            ) as? AlarmManager
                                            if (am != null && !am.canScheduleExactAlarms()) {
                                                runCatching {
                                                    context.startActivity(
                                                        Intent(
                                                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                                            Uri.parse("package:" + context.packageName)
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        viewModel.toggleRoutine(routine)
                                    },
                                    onCycleRoutineLoadout = viewModel::cycleRoutineLoadout,
                                    onAdjustRoutineTime = viewModel::adjustRoutineTime,
                                    onToggleRoutineDay = viewModel::toggleRoutineDay,
                                    onSkipRoutineToday = viewModel::skipRoutineToday,
                                    onResumeRoutine = viewModel::resumeRoutine,
                                    onAddRoutine = viewModel::addRoutine,
                                    onDeleteRoutine = viewModel::deleteRoutine,
                                    onRenameRoutine = viewModel::renameRoutine,
                                    onSetRoutineTime = { r, start, end -> viewModel.setRoutineTime(r, start, end) },
                                    blockedSites = blockedSites,
                                    onAddSite = viewModel::addBlockedSite,
                                    onRemoveSite = viewModel::removeBlockedSite,
                                    pruneNoteText = pruneNote?.let { note ->
                                        "${note.appsRemoved} APPS NOT ON THIS PHONE WERE TAKEN OFF THIS LIST" +
                                            if (note.rulesRemoved) " — AND THEIR RULES WITH THEM" else ""
                                    },
                                    onPruneNoteDismissed = viewModel::dismissPruneNote,
                                    legacyResetNoteText = legacyOpenResetCount?.let { count ->
                                        com.example.data.LegacyOpenReset.announcementLine(count)
                                    },
                                    onLegacyResetNoteDismissed = viewModel::dismissLegacyOpenResetNote,
                                    unreviewedOpenText = unreviewedOpenNames.takeIf { it.isNotEmpty() }?.let { names ->
                                        val shown = names.take(3).joinToString(", ") { it.uppercase() }
                                        val more = if (names.size > 3) " +${names.size - 3} MORE" else ""
                                        "${names.size} APPS ARE OPEN WITHOUT YOUR REVIEW: $shown$more — SET A RULE FOR EACH BELOW"
                                    }
                                )
                            }

                            MonolithDestination.TASKS -> {
                                FocusSequencerScreen(
                                    activeTask = activeTask,
                                    queuedTasks = queuedTasks,
                                    completedTasks = completedTasks,
                                                                        deletedTaskForUndo = deletedTaskForUndo,
                                                                        isSessionActive = sealed,
                                    soundEngine = viewModel.soundEngine,
                                    isDarkTheme = effectiveNight,
                                    onAddTask = viewModel::addTaskToQueue,
                                                                                                            onCueTaskToFocus = viewModel::cueTaskAndNavigateToFocus,
                                    onCompleteTask = viewModel::completeTaskAndAdvance,
                                                                                                                                                onDeleteTask = viewModel::deleteTaskWithUndo,
                                    onUndoDeleteTask = viewModel::undoDeleteTask,
                                    onDismissUndoBanner = viewModel::dismissUndoDeleteBanner,
                                    onReopenTask = viewModel::reopenTask,
                                    onToggleSubStep = viewModel::toggleTaskSubStep,
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
                                    drawerSearchDayWords = drawerSearchDayWords,
                                    dayWords = dayWords,
                                    isSealed = sealed,
                                    isDarkTheme = effectiveNight,
                                    onSelectMonth = viewModel::selectDrawerMonth,
                                    onLoadEarlier = viewModel::loadEarlierDrawer,
                                    onQueryChange = viewModel::setDrawerQuery,
                                    onLoadEarlierSearch = viewModel::loadEarlierDrawerSearch,
                                    onDeclareTodayWord = viewModel::declareTodayWord,
                                    onDeclareDayWord = viewModel::declareDayWordFor,
                                    artifacts = com.example.data.CurioCatalog.artifacts,
                                    forgedArtifactIds = forgedArtifactIds,
                                    curioForgeRecords = curioForgeRecords,
                                    drawerLoading = drawerLoading,
                                    drawerError = drawerError,
                                    forgeStats = forgeStats,
                                    receipts = receiptIndex,
                                    onReceiptClick = viewModel::presentReceipt
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
                        introPending = parkIntroForOpen,
                        isSealed = sealed,
                        isNight = effectiveNight,
                        soundEnabled = soundEnabled,
                        shortsReelsSniperEnabled = shortsReelsSniperEnabled,
                        soundEngine = viewModel.soundEngine,
                        themeReadout = themeEvaluation.readout,
                        parkReturnTick = parkReturnTick,
                        onDismiss = {
                            closePark(
                                parkOpenState, showNotificationDigestSheet, viewModel,
                                dismissReceipt = false
                            )
                        },
                        onFixClick = { item -> openFixPane(item) },
                        onRecheck = { viewModel.runWatchSweep() },
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleShortsReelsSniper = { viewModel.toggleShortsReelsSniper() },
                        // Truth Car 3 (UNA-02): the SOCIAL SITES
                        // board line's sled — reads and writes the
                        // same persisted browserBlockerEnabled fact
                        // the accessibility service enforces.
                        socialSitesEnabled = browserUrlBlockerEnabled,
                        onToggleSocialSites = { viewModel.toggleBrowserUrlBlocker() },
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

                // Issue #125 (armed-wrong F5): the grace abort does
                // not live only on the Deck anymore. For the whole
                // 10s window, every other tab carries the same chip
                // above the rail — leaving the Deck to check what
                // you armed wrong no longer burns the window
                // invisibly. Same words, same badge, same call as
                // the Deck's own chip.
                val graceSession = activeSession
                if (graceSession != null && graceSession.elapsedSeconds <= 10 &&
                    currentDestination != MonolithDestination.DECK
                ) {
                    BlockitActionChip(
                        wordLabel = "GRACE WINDOW ABORT",
                        monoBadge = "${10 - graceSession.elapsedSeconds}s",
                        isPrimaryInk = true,
                        onClick = {
                            viewModel.confirmDisarmSession("Accidental Start Grace Cancel")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .height(58.dp),
                        testTag = "grace_abort_button_off_deck"
                    )
                }

                // THE RAIL (chrome C1): the 52dp band. A stop press
                // while PARK is open is dismissal path (a) — the
                // sheet dismisses AND the cap departs on the same
                // input. The session receipt sheet is NOT dismissed
                // by navigation (issue #157): it unmounts off the
                // Deck and re-stands on return.
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
                            if (parkOpen) closePark(
                                parkOpenState, showNotificationDigestSheet, viewModel,
                                dismissReceipt = true
                            )
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
                                closePark(
                                    parkOpenState, showNotificationDigestSheet, viewModel,
                                    dismissReceipt = false
                                )
                            } else {
                                openPark(parkOpenState, sealed, viewModel)
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

            // The digest renders the sealed snapshot whenever a
            // freeze exists or a session is sealing the phone — the
            // snapshot refreshes on open and on new arrivals (issue
            // #118, finding 3) but renders in frozen MODE: read-only,
            // nothing opens. The live list is passed only when
            // unsealed and unfrozen.
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
            // hosted over the Deck's room, dismissed ONLY by the
            // next DOWN on its scrim (issue #157) — navigation and
            // PARK merely unmount it via the DECK gate below, and
            // it re-stands untouched on return. The two sheets
            // (this and the digest) never stack: PARK and the
            // digest live in their own invocations.
            val standingReceipt = receiptState
            if (standingReceipt != null &&
                dismissedReceiptSessionId != standingReceipt.sessionId &&
                currentDestination == MonolithDestination.DECK
            ) {
                com.example.ui.components.ReceiptSheet(
                    receipt = standingReceipt,
                    onDismiss = {
                        dismissedReceiptSessionId = standingReceipt.sessionId
                        viewModel.persistReceiptDismissal(standingReceipt.sessionId)
                    },
                    // Issue #135 F3: NEXT SESSION — dismiss the slip;
                    // the Deck underneath is already idle and ready
                    // to arm. The detention/sled ceremony is untouched.
                    onNextSession = { dismissedReceiptSessionId = standingReceipt.sessionId },
                    // Issue #135 F4: DONE WITH THIS TASK — only while
                    // the session's task is still open (the host's
                    // active task, not yet COMPLETED); completes it
                    // through the same path the Deck/Tasks use, then
                    // dismisses the slip.
                    onDoneWithTask = activeTask
                        ?.takeIf {
                            it.resolvedStatus != com.example.data.TaskQueueStatus.COMPLETED
                        }
                        ?.let { task ->
                            {
                                viewModel.completeTaskAndAdvance(task.id)
                                dismissedReceiptSessionId = standingReceipt.sessionId
                            }
                        }
                )
            }

            // A PAST SLIP, re-presented from the Vault's receipts
            // index (issue #157): the SAME sheet composable,
            // verbatim, over whatever screen invoked it. Its
            // dismissal is its own — it never touches the standing
            // receipt's flag above.
            val presentedReceipt by viewModel.presentedReceipt.collectAsStateWithLifecycle()
            presentedReceipt?.let { pastReceipt ->
                com.example.ui.components.ReceiptSheet(
                    receipt = pastReceipt,
                    onDismiss = { viewModel.dismissPresentedReceipt() }
                )
            }

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
                    // TAP-TIME LAUNCH GATE (design-revamp salvage,
                    // b027368): the entity handed in was composed when
                    // the grid rendered — re-read the VM's live rows
                    // at the tap and launch only what is still true
                    // NOW. On any refusal: no launch, the launcher
                    // stays open, nothing crashes.
                    val liveApp = viewModel.apps.value
                        .find { it.packageName == app.packageName }
                    val tapSealed =
                        viewModel.activeSession.value?.isActive == true
                    val launchAllowed = liveApp != null &&
                        liveApp.resolvedTier == com.example.data.AppBlockTier.OPEN &&
                        tapSealed &&
                        !viewModel.navigationLocked() &&
                        // Defence in depth, even if OPEN: the Play
                        // Store is an install vector, and browsers
                        // sit behind only a partial site blocker.
                        // BlockedAppEntity.category stores the
                        // AppCategory enum's name (see
                        // MonolithModels / repository writers).
                        liveApp.packageName != "com.android.vending" &&
                        liveApp.category != com.example.data.AppCategory.BROWSERS.name
                    if (launchAllowed && liveApp != null) {
                        // D-05 (2dac73e): close only once an intent
                        // has actually started — closing first made
                        // a failed launch a silent nothing.
                        val started = runCatching {
                            context.packageManager
                                .getLaunchIntentForPackage(liveApp.packageName)
                                ?.let { launch ->
                                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(launch)
                                    true
                                } ?: false
                        }.getOrDefault(false)
                        if (started) launcherOpen = false
                    }
                },
                onPeekApp = { app ->
                    // SAFE-BREAK PEEK (design-revamp salvage, b027368):
                    // during a Safe Break the active session already
                    // suspends the interceptors (WatchStore reads
                    // isInSafeBreak), so PEEK apps are already free —
                    // launch directly, no peek lease is started and
                    // no cooldown is armed. Read at tap time from
                    // the VM's activeSession flow.
                    val tapSession = viewModel.activeSession.value
                    // D-03 re-gate (2dac73e): same tap-time discipline
                    // as the OPEN gate — re-read the live entity; an
                    // app tightened to LOCKED mid-break must not
                    // launch off the stale composed row.
                    val livePeekApp = viewModel.apps.value.firstOrNull {
                        it.packageName == app.packageName
                    }
                    if (tapSession != null && tapSession.isActive && tapSession.isInSafeBreak &&
                        !viewModel.navigationLocked() && livePeekApp != null &&
                        livePeekApp.resolvedTier != com.example.data.AppBlockTier.LOCKED
                    ) {
                        val started = runCatching {
                            context.packageManager
                                .getLaunchIntentForPackage(livePeekApp.packageName)
                                ?.let { launch ->
                                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(launch)
                                    true
                                } ?: false
                        }.getOrDefault(false)
                        if (started) launcherOpen = false
                    } else {
                        // Outside a Safe Break (or refused by the
                        // re-gate) the existing Airlock route stands
                        // exactly as it was — the peek offer / lease
                        // machinery decides from there.
                        launcherOpen = false
                        viewModel.triggerAirlockIntercept(
                            app.packageName,
                            app.appName,
                            app.resolvedTier.badge,
                            InterceptPath.APP_TIER
                        )
                    }
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
                // Issue #114, finding 2: RETURN TO FOCUS now
                // returns. The ViewModel closes the sheet and books
                // the intercept; the host then sends Monolith's
                // whole task to the back, so the user lands on
                // whatever they were doing before the intercept
                // yanked them in — not stranded on the Deck having
                // to navigate back by hand for choosing the honest
                // door. Blocking itself is untouched: the session
                // stays sealed and the shield keeps enforcing.
                onReturnToFocus = {
                    viewModel.transmuteImpulseAndReturn()
                    (context as? android.app.Activity)?.moveTaskToBack(true)
                },
                onTake2MinutePeek = viewModel::start2MinutePeek,
                allowedAppsCount = allApps.count {
                    it.resolvedTier == com.example.data.AppBlockTier.OPEN &&
                        it.category != com.example.data.AppCategory.BROWSERS.name &&
                        it.packageName != "com.android.vending"
                },
                peekAppsCount = allApps.count {
                    it.resolvedTier == com.example.data.AppBlockTier.PEEK_2M
                },
                onOpenAllowedApps = {
                    viewModel.leaveAirlockViaLauncherDoors()
                    launcherPage = LauncherPage.ALLOWED
                    launcherOpen = true
                },
                onOpenPeekApps = {
                    viewModel.leaveAirlockViaLauncherDoors()
                    launcherPage = LauncherPage.PEEK
                    launcherOpen = true
                },
                onRequestEmergencyBreak = viewModel::requestDisarmFromAirlock,
                // Issue #318 F4: the wall breathes for the stored,
                // configured length — not a composable-local 5.
                breathSeconds = remember {
                    com.example.data.AirlockBreathStore(context).breathSeconds()
                }
            )

            // Issue #99 F2: the peek countdown pill — the one
            // rendering of floatingPeekPill (canonical; #101-F3
            // cross-ref). Shows while a peek lease runs in Monolith.
            PeekCountdownPill(
                state = floatingPeekPill,
                onEndEarly = viewModel::end2MinutePeekEarly
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
