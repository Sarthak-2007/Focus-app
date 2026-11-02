package com.example

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.DoorWordStore
import com.example.data.PermissionStepStore
import com.example.data.StrongroomDayEntity
import com.example.data.StrongroomHeldArrivalEntity
import com.example.data.StrongroomStore
import com.example.data.toPurse
import com.example.data.toSigningRecord
import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import com.example.service.FailureLog
import com.example.service.GlanceRouting
import com.example.service.InterceptConsumptionStore
import com.example.service.InterceptNonce
import com.example.service.InterceptPath
import com.example.service.LoadoutShortcuts
import com.example.service.MonolithDeviceAdminReceiver
import com.example.service.MonolithShieldService
import com.example.service.PermissionReturnService
import com.example.service.StrongroomEngineBridge
import com.example.service.WatchItemId
import com.example.service.WatchStore
import com.example.session.MonolithViewModel
import com.example.domain.LiftingPlate
import com.example.ui.doorway.Doorway
import com.example.ui.doorway.DoorwayState
import com.example.ui.doorway.plateTruthForDoorway
import com.example.ui.drawer.Drawer
import com.example.ui.drawer.DrawerActions
import com.example.ui.drawer.DrawerContents
import com.example.ui.drawer.DrawerFront
import com.example.ui.drawer.DrawerState
import com.example.ui.lifting.Lifting
import com.example.ui.lifting.LiftingState
import com.example.ui.lifting.PlatePileView
import com.example.ui.lifting.StruckPlateView
import com.example.ui.sealed.DoorWordSetter
import com.example.ui.sealed.SealedFace
import com.example.ui.sealed.SealedFaceActions
import com.example.ui.sealed.SealedFaceState
import com.example.ui.sealed.SwingAct
import com.example.ui.sealed.SwingActActions
import com.example.ui.sealed.SwingActState
import com.example.ui.shell.StrongroomShell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ==================================================================
// THE FRESH SHELL — gate step 5 (the deletion), BUILD-SR-32.
//
// This file is MainActivity's class identity (census C-11: the
// manifest, both activity-aliases, shortcuts.xml, the accessibility
// config and five engine callers name com.example.MainActivity —
// the class does not die) carrying ONLY the machinery the cured
// census filed as ENGINE-WIRING: the five onCreate machinery
// blocks, the intercept-intent consumption law, the FIX-trip
// machinery (openFixPane / launchSettings / pinnedSettingsIntent),
// and the POST_NOTIFICATIONS re-ask. The old composition —
// MonolithRootApp, the four screens, every overlay host — is
// DELETED with the rest of com/example/ui/** in this same act.
// What composes here is ShellHost below.
//
// BUILD-SR-34 (the shell slice): ShellHost now composes the
// Strongroom's own order (com.example.ui.shell.StrongroomShell)
// on the composed foundation (PR #868, merged).
// SCREENSHOT RESTRICTIONS REMOVED (Sarthak's order, Nov 3, 2026:
// "Remove screenshot restrictions from the app"): the drawer
// slice's FLAG_SECURE effect (the old R-6 re-earn) is deleted
// whole — no surface in this app sets FLAG_SECURE, and
// screenshots and Recents thumbnails are allowed everywhere,
// the drawer included.
// ==================================================================

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

    /** The composition's ViewModel, held so onNewIntent can route
     *  repeat intercepts through the same consumption path as the
     *  first composition (FIX-4). Set inside setContent. */
    private var viewModelRef: MonolithViewModel? = null

    // Issue #13: the VM the content is showing, for the
    // Activity-level interaction stamp (FLOW §2.1).
    private var interactionViewModel: MonolithViewModel? = null

    /** Every touch in the app stamps the VM's interaction clock:
     *  the settle counts from his hand, A-4's discriminator reads
     *  it, the battery-yield wake re-raises through it. */
    override fun onUserInteraction() {
        super.onUserInteraction()
        interactionViewModel?.noteUserInteraction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            // Issue #256: the birth pin matches the day ground for
            // BOTH bars (the ground effect below re-pins on night).
            // The nav bar was pinned dark here and in the effect —
            // light glyphs on the light day ground, illegible.
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        // Audit R6 #340 F67: POST_NOTIFICATIONS was declared in
        // the manifest but never requested at runtime — on
        // Android 13+ every summons/session notification was
        // silently undeliverable. Ask exactly once (prefs-guarded);
        // a refusal is respected, never re-nagged.
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            val askPrefs = getSharedPreferences("monolith_perm_asks", MODE_PRIVATE)
            if (!askPrefs.getBoolean("post_notifications_asked", false)) {
                askPrefs.edit().putBoolean("post_notifications_asked", true).apply()
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    34067
                )
            }
        }

        // Watch Store bind (issue #48): LAST PROVED restored before
        // any surface reads the store.
        WatchStore.bind(applicationContext)
        // Audit R6 #340 F61: the scrubbed failure log binds at the
        // same moment so repository/a11y funnels can record.
        com.example.service.FailureLog.bind(applicationContext)

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

        // Issue #274 (finding #14's remainder): the dynamic
        // per-loadout launcher shortcuts mirror the profiles
        // flow — publish on every emission while this activity
        // lives, so a loadout created, renamed or deleted in
        // the Matrix reaches the launcher without a second
        // source of truth. Publication is replace-all and
        // failure-swallowed (LoadoutShortcuts.publish).
        lifecycleScope.launch {
            rootViewModel.profiles.collect { profiles ->
                LoadoutShortcuts.publish(applicationContext, profiles)
            }
        }

        setContent {
            val viewModel = rootViewModel
            viewModelRef = viewModel
            interactionViewModel = viewModel

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

            ShellHost(viewModel = viewModel)
        }
    }

        override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // FIX-4: a repeat intercept against this live (singleTop)
        // activity consumes its extras HERE — first composition is
        // no longer the only reader, so a second intercept can
        // never land him on the previous app's stale door.
        viewModelRef?.let { handleIncomingInterceptIntent(intent, it) }
    }

    private fun handleIncomingInterceptIntent(intent: Intent?, viewModel: MonolithViewModel) {
        if (intent == null) return
        // Issue #274: a dynamic loadout shortcut carries exactly
        // one extra and no intercept payload — consume it FIRST,
        // so its id can never be mistaken for intercept state by
        // the readers below, and remove it so a later re-delivery
        // of the same intent cannot apply the loadout twice.
        val loadoutId = intent.getIntExtra(LoadoutShortcuts.EXTRA_OPEN_LOADOUT_ID, -1)
        if (loadoutId != -1) {
            intent.removeExtra(LoadoutShortcuts.EXTRA_OPEN_LOADOUT_ID)
            viewModel.openLoadoutFromShortcut(loadoutId)
        }
        // Issue #155 (F5): a tap on a glance card lands on the
        // surface the card is ABOUT, per GlanceRouting's per-state
        // table — not the #312 single route to SHIELD, which the
        // seal law in moveToPlace refuses mid-session (dead code)
        // and which, at completion, lands where the receipt is
        // not. The about extra is routing-only: it names a
        // destination, never a session action, so honouring it
        // from any delivery can move the user's eyes but can
        // never touch the seal. Intercept extras take precedence:
        // when a firing's package rides the same intent, the
        // intercept path below owns the delivery and these extras
        // are simply consumed.
        val fromShieldNotification = intent.getBooleanExtra(
            MonolithShieldService.EXTRA_FROM_SHIELD_NOTIFICATION, false
        )
        val glanceAbout = GlanceRouting.parseAbout(
            intent.getStringExtra(GlanceRouting.EXTRA_GLANCE_ABOUT)
        )
        if (fromShieldNotification) {
            intent.removeExtra(MonolithShieldService.EXTRA_FROM_SHIELD_NOTIFICATION)
        }
        intent.removeExtra(GlanceRouting.EXTRA_GLANCE_ABOUT)
        val pkg = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPTED_PACKAGE)
        if (pkg.isNullOrBlank()) {
            if (fromShieldNotification) {
                // Cards posted by the current shield build carry
                // only the boolean; infer their state from the
                // live session fact. Per-state writers (the
                // EXTRA_GLANCE_ABOUT extra) ride PR #540's lane.
                val about = glanceAbout ?: GlanceRouting.aboutForLegacyShieldTap(
                    sessionActive = viewModel.activeSession.value?.isActive == true
                )
                GlanceRouting.placeFor(about)?.let { viewModel.moveToPlace(it) }
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
            // Wave-2 cure (critic N-4, issue #11 — carried through
            // the #68/#78 merge): the old "Short-Form Feed" fallback
            // fabricated a hero name for a door whose app was never
            // identified. The name slot holds a fact or the package
            // verbatim — never an invention.
            ?: pkg
        val badge = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPT_BADGE)
            // FIX-6 (C-10, issue #11): the fallback badge word is
            // "Blocked" — this is the fourth of the four sites.
            ?: "Blocked"
        // WHICH door fired (Truth Car 3, UNA2-01): the peek
        // offer is computed per-path — an extra that is missing
        // or names no known door parses to UNKNOWN, and UNKNOWN
        // never offers a peek.
        val path = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPT_PATH)
            ?.let { runCatching { InterceptPath.valueOf(it) }.getOrNull() }
            ?: InterceptPath.UNKNOWN
        // The site door's sub-line (FIX-6 name slot): the
        // browser holds the name; the matched domain rides
        // here. Absent on every other door.
        val subLine = intent.getStringExtra(MonolithShieldService.EXTRA_INTERCEPTED_SUBLINE)
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
        intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPTED_SUBLINE)
        intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPT_ID)
        intent.removeExtra(MonolithShieldService.EXTRA_INTERCEPT_NONCE)
        if (InterceptConsumptionStore.isConsumed(this, interceptId)) return
        if (!InterceptConsumptionStore.markConsumed(this, interceptId)) return
        // The showing itself is revalidated in the VM against the
        // live session and the package's live tier/lease — a
        // first-delivered but long-delayed intent does not resurrect
        // an Airlock its facts no longer support.
        viewModel.triggerAirlockInterceptFromIntent(pkg, name, badge, path, subLine)
    }

    // Issue #27 (finding 2): there is deliberately NO onDestroy
    // audio stop here any more. The old override stopped the
    // Activity's own engine field — which WAS the retained
    // ViewModel's engine — on every rotation, killing all sound
    // until process death. The ViewModel stops its engine in
    // onCleared(), the one true end of its life.
}

/**
 * THE HOST — everything this composable is, stated plainly: it
 * carries the shell's lifecycle wiring (residency stamp, the
 * ON_RESUME watch sweep + return-service stop, the ON_STOP airlock
 * abandonment note) and the airlock-holds-Back law, and it composes
 * the Strongroom's own order (StrongroomShell) as the app's whole
 * visual structure. No window in this host is ever made secure:
 * screenshot restrictions were removed whole on Sarthak's order
 * (Nov 3, 2026) — see the file head.
 *
 * BUILD-SR-35 (composition, second act): the shell's DOORWAY
 * place-host carries the Doorway surface itself, its state read
 * from the standing engine facts by [loadDoorwayState] below —
 * the first leg's own ShellHost glue (a hardcoded AtRest doorway
 * composed outside any place) is superseded by the shell and
 * does not exist in this form.
 */
@Composable
private fun ShellHost(viewModel: MonolithViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val airlockState by viewModel.airlockState.collectAsStateWithLifecycle()

    // The Doorway's standing state (BUILD-SR-35 composition): read
    // from the engine facts at the shell's birth and re-read on
    // every resume (the tick below is bumped by the lifecycle
    // observer's ON_RESUME arm). Until the first read lands it is
    // AtRest — the derived empty form, standing only while no
    // composed day has been read yet.
    val doorwayRefreshTick = remember { mutableStateOf(0) }
    var doorwayState by remember { mutableStateOf(DoorwayState.AtRest) }
    val appContext = context.applicationContext
    LaunchedEffect(doorwayRefreshTick.value) {
        doorwayState = loadDoorwayState(appContext)
    }

    // BUILD-SR-37 (the Seal slice): the seal's standing facts, read
    // from the Strongroom store on the same tick as the doorway's —
    // the sealed face's printings snapshot (derived ONCE from the
    // sealed day's standing record + the door-word as it stands;
    // the face is never re-derived from live facts while the seal
    // stands) and whether today's standing day makes the swing
    // invocable at the first sitting. An unsigned day stands open
    // (GAP-COORD-04 as ruled): no seal stands, no face derives,
    // and the shell rests at the doorway.
    var sealedFaceState by remember { mutableStateOf<SealedFaceState?>(null) }
    var swingStanding by remember { mutableStateOf(false) }
    var swingActState by remember { mutableStateOf(SwingActState()) }
    val shellScope = androidx.compose.runtime.rememberCoroutineScope()
    LaunchedEffect(doorwayRefreshTick.value) {
        val facts = loadSealFacts(appContext)
        sealedFaceState = facts.first
        swingStanding = facts.second
        // The movement law reads the same one derivation
        // (BUILD-SR-38 merge composition): the second touch is
        // lawful exactly when the snapshot stands.
        viewModel.noteStrongroomSealStanding(facts.first != null)
    }
    // BUILD-SR-40 (the Lifting + Plate Strike slice): the
    // lifting's standing facts, read from the Strongroom store
    // on the same tick — the newest struck day (its plate, struck
    // once, re-read never re-struck) and the whole pile of struck
    // rows behind it. The ceremony's clock offset is read from
    // the finish's own elapsed zero when a finish stands; a
    // surface entered after the day lifted stands at the score's
    // end (the door open, the plate in the pile).
    var liftingState by remember { mutableStateOf<LiftingState?>(null) }
    val finishForLifting by viewModel.finishState.collectAsStateWithLifecycle()
    LaunchedEffect(doorwayRefreshTick.value) {
        liftingState = loadLiftingState(appContext, finishForLifting?.e0Mono)
    }
    val sealedFaceActions = SealedFaceActions(
        // The knock's sound/haptic substance rides the Stage 13/14
        // slices through this seam; platform-edge reaches are
        // already answered by the engine's knock voice via the
        // bridge. The face itself changes nothing in answer.
        onKnock = {},
        onGrilleOpen = {
            viewModel.moveToPlace(com.example.session.StrongroomPlace.ANTECHAMBER)
        }
    )

    // BUILD-SR-41 (the Drawer slice): the drawer's standing in
    // the doorway place. Shut, it is furniture under the signing
    // table (DrawerFront); his hand opens it and it stands in
    // the place's whole reach, its contents read from the store
    // at the opening ([loadDrawerState]) — re-derived on every
    // opening, never held stale across openings.
    var drawerOpen by remember { mutableStateOf(false) }
    var drawerState by remember { mutableStateOf(DrawerState()) }
    LaunchedEffect(drawerOpen) {
        if (drawerOpen) drawerState = loadDrawerState(appContext)
    }

    // Watch Store residency (census §2c, re-carried): the old
    // composition stamped residency on DECK entry; the host has
    // no DECK, so residency starts at the shell's own birth and
    // the grant-check sweep fires with it (the §4.4 trigger the
    // Deck entry used to carry). Named here as the one place the
    // re-home changes a trigger's shape, never its fact.
    LaunchedEffect(Unit) {
        WatchStore.noteResidencyStart()
        viewModel.runWatchSweep()
    }

    // The old composition's lifecycle observer, re-carried whole
    // but for its Park bookkeeping (parkReturnTick — the Park is
    // deleted; there is no note line left to re-read).
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
                // The Doorway's facts are re-read on the same
                // return (BUILD-SR-35 composition): a day composed,
                // a caller held, or a plate struck while the app
                // stood aside is standing at the doorway on return.
                doorwayRefreshTick.value++
                viewModel.runWatchSweep()
                viewModel.onAppResumed()
                viewModel.refreshDeckWarrant()
                // Wave-3 (Ruhe R-5): the night return's
                // staleness re-check rides this same resume.
                viewModel.expireNightReturnIfStale()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Issue #27 (finding 3), the surviving half of the old Back
    // chain: while an Airlock stands, Back is consumed — the
    // door's own RETURN/PEEK are the only ways out. Back is never
    // an exit from an intercept.
    BackHandler(enabled = airlockState.isVisible) {
        // The door holds; Back is not an exit.
    }

    StrongroomShell(
        viewModel = viewModel,
        // THE UNIFIED STANDING (BUILD-SR-38 merge composition):
        // the shell's place derivation consumes the seal's standing
        // from THE SAME derivation that produces the face's
        // snapshot — loadSealFacts, from the sealed day's standing
        // record. The legacy session's standing no longer derives
        // the face's place in parallel: one derivation, and both
        // hosts render its one snapshot.
        sealedStanding = sealedFaceState != null,
        doorwayContent = {
            // While a Strongroom seal stands, the doorway place
            // itself stands as the sealed face (BUILD-SR-37;
            // ledger D-7: while sealed, the launcher is the face
            // and there is no route into the normal app — the
            // signing table does not stand under a standing seal).
            // BUILD-SR-38 merge composition: this glue renders THE
            // ONE SNAPSHOT (loadSealFacts' derivation, shared with
            // the shell's SEALED_FACE host) — since the shell's
            // place derivation now consumes that same standing,
            // this branch stands as the birth-frame form, before
            // the shell's first unified derivation lands the place
            // at SEALED_FACE; it derives nothing of its own.
            val face = sealedFaceState
            if (face != null) {
                SealedFace(state = face, actions = sealedFaceActions)
            } else {
                // The signing place, composed (BUILD-SR-35 +
                // BUILD-SR-36 at merge): the Doorway's four
                // furniture pieces stand in the room's upper reach,
                // and the signing table — the evening's act — stands
                // at the open door below them. One place, not two
                // destinations (census C-21 + C-02; GAP-R01: no
                // navigation glue exists).
                // BUILD-SR-37: between them stands THE SWING ACT,
                // invocable as the morning's first sitting when a
                // day stands composed or signed — his hand swings
                // the door on the standing signing, and at frame
                // contact this host fires the seal through the
                // bridge's activateSeal (its fresh host, per the
                // Caller-Orphan Register: until this slice it had
                // no production caller).
                // BUILD-SR-41: THE DRAWER stands in this same
                // place as its furniture (census C-22, doorway
                // furniture per BUILD-SR-34's accounting). Shut,
                // its front stands under the signing table; open,
                // it stands in the place's whole reach — one
                // place, never a destination (GAP-R01). It exists
                // only in this unsealed branch: while a seal
                // stands, the face above owns the place whole
                // (ledger D-7).
                if (drawerOpen) {
                    Drawer(
                        state = drawerState,
                        actions = DrawerActions(
                            onHandingBack = {
                                // C-11 at the drawer's threshold:
                                // the waiting people are returned
                                // to his phone's own list — the
                                // delivery itself is the wiring
                                // layer's act (P6's seam: the lift's
                                // engine path returns them; the
                                // drawer keeps no list of its own)
                                // and this host confirms the standing
                                // the gate waits on. No row is
                                // resolved TAKEN or LEFT here: that
                                // choice is the held surface's, per
                                // call, never a browsing act.
                                drawerState = drawerState.copy(
                                    handingBackDelivered = true
                                )
                            },
                            onClose = { drawerOpen = false }
                        )
                    )
                } else {
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize()
                ) {
                // THE FACE (LEAD-DOORFIX, 2026-11-03): the
                // seven-ink print stands behind the whole doorway
                // place — the door IS this surface, edge to edge;
                // the Doorway's furniture, the swing act, the
                // signing table and the drawer's front all stand
                // on it. (Before this, the place stood on the bare
                // field and the Doorway's drawn leaf — 26% of the
                // room's width — read on Sarthak's phone as a tiny
                // door floating in a void.)
                com.example.ui.doorway.DoorFace()
                androidx.compose.foundation.layout.Column(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize()
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = androidx.compose.ui.Modifier.weight(1f)
                    ) {
                        Doorway(state = doorwayState)
                    }
                    if (swingStanding) {
                        SwingAct(
                            state = swingActState,
                            actions = SwingActActions(
                                onContact = {
                                    shellScope.launch {
                                        val todayIso = StrongroomDay
                                            .filingDateIso(System.currentTimeMillis())
                                        val span = withContext(Dispatchers.IO) {
                                            StrongroomEngineBridge.activateSeal(
                                                appContext, todayIso
                                            )
                                        }
                                        swingActState = if (span != null) {
                                            SwingActState(sealStruck = true)
                                        } else {
                                            SwingActState(sealFailed = true)
                                        }
                                    }
                                },
                                onCeremonyComplete = {
                                    swingActState = SwingActState()
                                    doorwayRefreshTick.value++
                                }
                            )
                        )
                    }
                    androidx.compose.foundation.layout.Box(
                        modifier = androidx.compose.ui.Modifier.weight(1.35f)
                    ) {
                        SigningHost(viewModel = viewModel)
                    }
                    // The drawer's shut front — the room's
                    // furniture at the foot of the signing place.
                    DrawerFront(onOpen = { drawerOpen = true })
                }
                }
                }
            }
        },
        antechamberContent = {
            // The room behind the grille (BUILD-SR-38): composed
            // only inside a standing seal — the shell derives the
            // ANTECHAMBER place from the seal + his second touch,
            // and the host below renders nothing when no sealed
            // day stands (no dead route, route-book Law R-2).
            AntechamberHost(viewModel = viewModel)
        },
        breakContent = {
            // The Break (BUILD-SR-39): the shell stands the BREAK
            // place only while the engine's break span runs — the
            // host below renders the firing from the standing
            // facts and nothing when no break stands. The surface
            // invokes nothing: there is no act in it that can
            // start, stop or extend a break (census C-16).
            BreakHost(viewModel = viewModel)
        },
        sealedFaceContent = {
            // The shell's own SEALED_FACE host: the face from THE
            // ONE SNAPSHOT (BUILD-SR-38 merge composition — the
            // sealed face's derivation is unified: the snapshot is
            // derived once from the sealed day's standing record
            // in loadSealFacts, the shell's place derivation below
            // consumes that same standing via sealedStanding, and
            // both hosts — this one and the doorway place's glue —
            // render that one snapshot; re-shown exactly as cut,
            // never recomposed, ledger B-10).
            sealedFaceState?.let { face ->
                SealedFace(state = face, actions = sealedFaceActions)
            }
        },
        liftingContent = {
            // The shell's LIFTING host (BUILD-SR-40): the lifting
            // ceremony + the plate strike + the pile, derived
            // whole from the store's struck rows. A fallen day
            // stands here as the failure face (C-18). When no
            // struck day stands, nothing stands (B-14).
            liftingState?.let { state -> Lifting(state = state) }
        }
    )
}

/**
 * BUILD-SR-38 composition glue — the Antechamber (census C-08,
 * with C-09 the drum · C-14 the held pile · C-15 calling out ·
 * C-04 the governor's crank) in the shell's ANTECHAMBER place.
 *
 * The room's facts are re-derived from the kept engine on every
 * entry and after every act (the composed main's own pattern):
 * the sealed day's row gives the signing exactly as signed and
 * the purse as the store stands it (row.toPurse() — the purse is
 * NEVER held in this host: spend persists in the store, and a
 * purse re-derived from anywhere else is the resurrection Slice
 * 1's K2 kill named); the crank week and the day's held rows come
 * from the same store. Every spend runs through the store's own
 * verbs (spendAllowance · spendCrank · resolveHeldArrival ·
 * punch), and a launch or a dial happens ONLY behind a standing
 * spend / a taken slip — at the hard stop nothing is spent and
 * nothing is launched, and the room re-derives with the shutter
 * closed.
 *
 * The governor's crank: this host persists the week's spend and
 * returns him to the sealed face. The door's crack for that
 * minute is the seal's own consumption and no kept verb carries
 * it — named GAP-COORD-CRANK-CONSUMPTION on the slice's PR, never
 * invented here.
 */
@Composable
private fun AntechamberHost(viewModel: MonolithViewModel) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val apps by viewModel.apps.collectAsStateWithLifecycle()

    var signing by remember {
        mutableStateOf<com.example.domain.StrongroomDay.SigningRecord?>(null)
    }
    var room by remember {
        mutableStateOf<com.example.ui.antechamber.AntechamberRoom?>(null)
    }
    var dateIso by remember { mutableStateOf("") }
    var revealed by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var refreshTick by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTick) {
        val facts = withContext(Dispatchers.IO) {
            val store = StrongroomStore(AppContainer.dao(appContext))
            val row = store.sealedDay() ?: return@withContext null
            val now = System.currentTimeMillis()
            Triple(
                row.toSigningRecord(),
                com.example.ui.antechamber.AntechamberModel.room(
                    signing = row.toSigningRecord(),
                    purse = row.toPurse(),
                    crank = store.crankWeek(now),
                    heldRows = store.heldArrivals(row.filingDateIso)
                ),
                row.filingDateIso
            )
        }
        signing = facts?.first
        room = facts?.second
        dateIso = facts?.third ?: ""
    }

    val standingSigning = signing ?: return
    val standingRoom = room ?: return
    val standingDate = dateIso

    com.example.ui.antechamber.Antechamber(
        state = com.example.ui.antechamber.AntechamberState(
            signing = standingSigning,
            room = standingRoom,
            tokens = apps.map {
                com.example.ui.antechamber.AntechamberToken(it.packageName, it.appName)
            },
            revealedCodeIds = revealed
        ),
        actions = com.example.ui.antechamber.AntechamberActions(
            onTakeTurned = { pkg ->
                scope.launch {
                    val spent = withContext(Dispatchers.IO) {
                        StrongroomStore(AppContainer.dao(appContext))
                            .spendAllowance(
                                standingDate,
                                com.example.ui.antechamber.RevolvingDrum.MINUTES_PER_TAKING
                            )
                    }
                    if (spent != null) {
                        // The minute stands spent in the store;
                        // only now does the app open — for its
                        // minute, through the phone's own launcher
                        // entry, exactly as a taken peek launches.
                        runCatching {
                            val launchIntent = appContext.packageManager
                                .getLaunchIntentForPackage(pkg)
                                ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            if (launchIntent != null) {
                                appContext.startActivity(launchIntent)
                            }
                        }
                    }
                    refreshTick++
                }
            },
            onShowCode = { slip ->
                scope.launch {
                    val shown = withContext(Dispatchers.IO) {
                        val store = StrongroomStore(AppContainer.dao(appContext))
                        val row = store.sealedDay() ?: return@withContext false
                        store.spendAllowance(
                            row.filingDateIso,
                            com.example.ui.antechamber.RevolvingDrum.MINUTES_PER_TAKING
                        ) ?: return@withContext false
                        // The showing is struck (TX-SHOW-12): a
                        // SHOWING hole with the hour — the digits
                        // themselves are never struck.
                        val offset = (android.os.SystemClock.elapsedRealtime() -
                            row.sealedAtElapsedMillis).coerceAtLeast(0L)
                        store.punch(
                            row.filingDateIso,
                            StrongroomDay.Punch(
                                StrongroomDay.PunchKind.SHOWING, offset, ""
                            )
                        )
                        true
                    }
                    if (shown) revealed = revealed + slip.id
                    refreshTick++
                }
            },
            onResolveHeld = { slip, taken ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        StrongroomStore(AppContainer.dao(appContext))
                            .resolveHeldArrival(slip.id, taken)
                    }
                    revealed = revealed - slip.id
                    refreshTick++
                }
            },
            onTakeCall = { slip ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        StrongroomStore(AppContainer.dao(appContext))
                            .resolveHeldArrival(slip.id, taken = true)
                    }
                    // Connects THIS call only: the phone's own
                    // dialler on the number as it arrived.
                    runCatching {
                        appContext.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${slip.line}"))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    refreshTick++
                }
            },
            onGovernorCrank = {
                scope.launch {
                    val spent = withContext(Dispatchers.IO) {
                        StrongroomStore(AppContainer.dao(appContext))
                            .spendCrank(System.currentTimeMillis())
                    }
                    if (spent != null) {
                        viewModel.moveToPlace(
                            com.example.session.StrongroomPlace.SEALED_FACE
                        )
                    }
                    refreshTick++
                }
            },
            onCallOut = {
                runCatching {
                    appContext.startActivity(
                        Intent(Intent.ACTION_DIAL)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            },
            onReturnToFace = {
                viewModel.moveToPlace(com.example.session.StrongroomPlace.SEALED_FACE)
            }
        )
    )
}

/**
 * BUILD-SR-39 composition glue — THE BREAK (census C-16) in the
 * shell's BREAK place.
 *
 * The host renders THE FIRING and invokes nothing. The break's
 * clock is re-derived from the engine's standing facts on every
 * engine tick: first the Strongroom day's own firing (the
 * signing's offset + priced length against the seal's elapsed
 * span, through the domain's breakFiring), else the kept
 * session's standing break facts (its length in force + its
 * remaining seconds — the same facts the shell's place
 * derivation stands on). Between ticks the surface interpolates
 * on the monotonic clock alone; the wall clock is never
 * consulted. When neither clock stands, the host renders
 * nothing — a break that is not firing has no surface, and a
 * legacy session break on a day no Strongroom signing stands
 * for is the old engine's business, not this room's.
 *
 * The interior's acts are the held room's own, through the same
 * store verbs the antechamber host uses: a within-reach launch
 * (no spend — the break was priced whole at signing), a code's
 * showing (one minute + a SHOWING punch, B-18), a held slip
 * resolved or a held call taken. There is deliberately NO act
 * here — and none in the surface — that starts, stops, extends
 * or shortens the break.
 */
@Composable
private fun BreakHost(viewModel: MonolithViewModel) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val apps by viewModel.apps.collectAsStateWithLifecycle()
    val session by viewModel.activeSession.collectAsStateWithLifecycle()

    var breakState by remember {
        mutableStateOf<com.example.ui.`break`.BreakState?>(null)
    }
    var revealed by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var refreshTick by remember { mutableStateOf(0) }
    var nowElapsed by remember {
        mutableStateOf(android.os.SystemClock.elapsedRealtime())
    }

    // The escapement's frame clock: monotonic readings only.
    LaunchedEffect(Unit) {
        while (true) {
            androidx.compose.runtime.withFrameMillis {
                nowElapsed = android.os.SystemClock.elapsedRealtime()
            }
        }
    }

    val sessionOnBreak = session?.isInSafeBreak == true
    val sessionRemaining = session?.safeBreakSecondsRemaining ?: 0
    LaunchedEffect(sessionOnBreak, sessionRemaining, refreshTick, apps) {
        val facts = withContext(Dispatchers.IO) {
            val store = StrongroomStore(AppContainer.dao(appContext))
            val row = store.sealedDay() ?: return@withContext null
            val nowE = android.os.SystemClock.elapsedRealtime()
            val firingClock = if (
                row.spanDurationMillis > 0L && row.sealedBootCount >= 0
            ) {
                val span = StrongroomDay.SealSpan(
                    row.sealedAtElapsedMillis,
                    row.sealedBootCount,
                    row.spanDurationMillis
                )
                StrongroomDay.breakFiring(row.toSigningRecord(), span)
                    ?.let { firing ->
                        com.example.ui.`break`.BreakClock.fromFiring(firing, nowE)
                    }
            } else null
            val sessionClock = session?.takeIf { it.isInSafeBreak }?.let { s ->
                com.example.ui.`break`.BreakClock.fromSession(
                    s.effectiveBreakSeconds, s.safeBreakSecondsRemaining, nowE
                )
            }
            val clock = firingClock ?: sessionClock ?: return@withContext null
            val heldRows = store.heldArrivals(row.filingDateIso)
            val waiting = com.example.ui.antechamber.AntechamberModel.waitingPile(heldRows)
            Triple(row.toSigningRecord(), clock, waiting)
        }
        breakState = facts?.let { (signing, clock, waiting) ->
            com.example.ui.`break`.BreakState(
                signing = signing,
                clock = clock,
                tokens = apps.map {
                    com.example.ui.antechamber.AntechamberToken(it.packageName, it.appName)
                },
                waiting = waiting,
                quietDot = com.example.ui.antechamber.AntechamberModel
                    .quietDotVisible(waiting),
                revealedCodeIds = revealed
            )
        }
    }

    val standing = breakState ?: return
    com.example.ui.`break`.Break(
        state = standing,
        nowElapsedMillis = nowElapsed,
        actions = com.example.ui.`break`.BreakActions(
            onLaunchAllowed = { pkg ->
                // Within reach is within reach: the phone's own
                // launcher entry, nothing spent, nothing struck.
                runCatching {
                    val launchIntent = appContext.packageManager
                        .getLaunchIntentForPackage(pkg)
                        ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    if (launchIntent != null) {
                        appContext.startActivity(launchIntent)
                    }
                }
            },
            onShowCode = { slip ->
                scope.launch {
                    val shown = withContext(Dispatchers.IO) {
                        val store = StrongroomStore(AppContainer.dao(appContext))
                        val row = store.sealedDay() ?: return@withContext false
                        store.spendAllowance(
                            row.filingDateIso,
                            com.example.ui.antechamber.RevolvingDrum.MINUTES_PER_TAKING
                        ) ?: return@withContext false
                        val offset = (android.os.SystemClock.elapsedRealtime() -
                            row.sealedAtElapsedMillis).coerceAtLeast(0L)
                        store.punch(
                            row.filingDateIso,
                            StrongroomDay.Punch(
                                StrongroomDay.PunchKind.SHOWING, offset, ""
                            )
                        )
                        true
                    }
                    if (shown) revealed = revealed + slip.id
                    refreshTick++
                }
            },
            onResolveHeld = { slip, taken ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        StrongroomStore(AppContainer.dao(appContext))
                            .resolveHeldArrival(slip.id, taken)
                    }
                    revealed = revealed - slip.id
                    refreshTick++
                }
            },
            onTakeCall = { slip ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        StrongroomStore(AppContainer.dao(appContext))
                            .resolveHeldArrival(slip.id, taken = true)
                    }
                    runCatching {
                        appContext.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${slip.line}"))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    refreshTick++
                }
            },
            onKnock = {}
        )
    )
}

/**
 * BUILD-SR-36 composition glue — the Signing (census C-02, a HERO)
 * in the doorway place, under the Doorway's furniture.
 *
 * The standing day is read from the Strongroom store exactly as
 * [loadDoorwayState] reads it (the composed main's own pattern):
 * today's filing-day row standing COMPOSED or SIGNED, re-expressed
 * as a [com.example.domain.StrongroomComposition.ComposedDay]. No
 * week standing order exists in the kept engine, so none is
 * invented: a day that does not stand in the store does not stand
 * at the table — the host renders nothing until it stands
 * (stillness, census GAP-R03), and the Doorway above stands AtRest.
 *
 * Amendments amend the standing composition in memory AND persist
 * through the store's own verb for a fully-formed record
 * ([StrongroomStore.sign], the first-signing flow's verb — it
 * marks the day SIGNED, which is the signing's own fact: the
 * evening's amendments are the signing). The signature itself
 * re-signs the record as it stands when the wheel's turnover
 * completes; it seals nothing — the swing (the store's `seal`) is
 * the first sitting's act, service-side, and is not this host's.
 * The allowance pawl's moves run through the store's Guard verbs
 * (tightenGuard / requestGuardRaise), which carry D-1's law in the
 * domain. Task and sentence also issue the session ViewModel's
 * commands for the facts the kept engine carries in its own right
 * (intention · shield intention).
 */
@Composable
private fun SigningHost(viewModel: MonolithViewModel) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val apps by viewModel.apps.collectAsStateWithLifecycle()

    var day by remember {
        mutableStateOf<com.example.domain.StrongroomComposition.ComposedDay?>(null)
    }
    var guard by remember {
        mutableStateOf(
            com.example.domain.StrongroomGuard.GuardState(
                allowanceMinutes = StrongroomStore.DEFAULT_ALLOWANCE_MINUTES
            )
        )
    }
    var firstSigning by remember { mutableStateOf(false) }
    var codesWait by remember { mutableStateOf(false) }
    var signed by remember { mutableStateOf(false) }
    // BUILD-SR-37: the door-word as it stands (its store is the
    // ruling's home — GAP-COORD-01 as ruled at PR #873: a single
    // word of his choosing, set at the signing table, changeable
    // only at a signing; unset = the kept place).
    var doorWord by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val facts = withContext(Dispatchers.IO) {
            val store = StrongroomStore(AppContainer.dao(appContext))
            val todayIso = com.example.domain.StrongroomDay.filingDateIso(
                System.currentTimeMillis()
            )
            val row = store.day(todayIso)?.takeIf {
                it.state == StrongroomDayEntity.STATE_COMPOSED ||
                    it.state == StrongroomDayEntity.STATE_SIGNED
            }
            Triple(
                row?.let {
                    com.example.domain.StrongroomComposition.ComposedDay(
                        filingDateIso = it.filingDateIso,
                        shape = runCatching {
                            com.example.domain.StrongroomComposition.DayShape
                                .valueOf(it.dayShape)
                        }.getOrDefault(
                            com.example.domain.StrongroomComposition.DayShape.ORDINARY
                        ),
                        record = it.toSigningRecord(),
                        amendedBySigning = it.amendedBySigning
                    )
                },
                store.guardState(System.currentTimeMillis()),
                Triple(
                    store.isFirstSigning(),
                    row?.let {
                        it.allowanceTotalMinutes > 0 &&
                            it.allowanceSpentMinutes >= it.allowanceTotalMinutes
                    } ?: false,
                    row?.state == StrongroomDayEntity.STATE_SIGNED
                )
            )
        }
        day = facts.first
        guard = facts.second
        firstSigning = facts.third.first
        codesWait = facts.third.second
        signed = facts.third.third
        doorWord = withContext(Dispatchers.IO) {
            DoorWordStore(appContext).doorWord()
        }
    }

    val standing = day ?: return

    // The signing table, with the door-word's scribe line joined
    // to its setters (BUILD-SR-37, per the coordinator's ruling at
    // PR #873: the setting control joins the signing table's
    // setters at the sealed-face slice's composition — the Signing
    // as delivered is not reopened for it).
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier.fillMaxSize()
    ) {
        com.example.ui.signing.Signing(
            state = com.example.ui.signing.SigningState(
                day = standing,
                tokens = apps.map {
                    com.example.ui.signing.SigningToken(it.packageName, it.appName)
                },
                guard = guard,
                firstSigning = firstSigning,
                codesWaitTonight = codesWait,
                signed = signed
            ),
            actions = com.example.ui.signing.SigningActions(
                onAmend = { amendment ->
                    val amended = com.example.domain.StrongroomComposition
                        .amend(standing, amendment)
                    day = amended
                    amendment.taskLine?.let { viewModel.setIntentionText(it) }
                    amendment.sentence?.let { viewModel.setShieldIntention(it) }
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            StrongroomStore(AppContainer.dao(appContext))
                                .sign(amended.record, amended = true)
                        }
                    }
                },
                onAllowanceMove = { target ->
                    scope.launch {
                        val now = System.currentTimeMillis()
                        val todayIso = com.example.domain.StrongroomDay
                            .filingDateIso(now)
                        val next = withContext(Dispatchers.IO) {
                            val store = StrongroomStore(AppContainer.dao(appContext))
                            val current = store.guardState(now)
                            when (
                                val move = com.example.ui.signing.AllowancePawl.move(
                                    current, target, todayIso,
                                    com.example.domain.StrongroomDay
                                        .nextDayStartMillis(now)
                                )
                            ) {
                                is com.example.ui.signing.AllowancePawl.Move.Tightened ->
                                    store.tightenGuard(now, move.state.allowanceMinutes)
                                is com.example.ui.signing.AllowancePawl.Move.RaiseWound ->
                                    store.requestGuardRaise(
                                        now, move.state.pendingAllowanceMinutes
                                    ).first
                                is com.example.ui.signing.AllowancePawl.Move.BeyondHardStop ->
                                    current
                            }
                        }
                        guard = next
                    }
                },
                onSignature = {
                    signed = true
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            StrongroomStore(AppContainer.dao(appContext))
                                .sign(standing.record, amended = true)
                        }
                    }
                }
            ),
            modifier = androidx.compose.ui.Modifier.weight(1f)
        )
        DoorWordSetter(
            word = doorWord,
            onSetWord = { raw ->
                val store = DoorWordStore(appContext)
                val ok = store.put(raw)
                if (ok) doorWord = store.doorWord()
                ok
            },
            modifier = androidx.compose.ui.Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

/**
 * The Doorway's facts, read from the standing engine (BUILD-SR-35
 * composition, second act) — the ONLY source of [DoorwayState] in
 * the composed app:
 *
 *  - **The composed day**: today's filing-day row in the
 *    Strongroom store, when it stands COMPOSED or SIGNED (a sealed
 *    or struck day is not standing at the signing place — the
 *    shell's own routing owns those hours). Re-expressed as a
 *    [StrongroomComposition.ComposedDay] exactly as the store's
 *    own composeDay re-reads a standing row (shape from the row,
 *    record from the row, the signing-amendment fact from the
 *    row), then mapped by the Doorway's own
 *    [com.example.ui.doorway.DoorwayComposition.fromComposedDay]
 *    inside [DoorwayState.fromFacts]. No week standing order
 *    exists in the kept engine, so none is invented here: a day
 *    that does not stand in the store does not stand at the door.
 *  - **The held-waiting fact**: any arrival still WAITING in
 *    today's held pile.
 *  - **Yesterday's plate**: the plate of the filing day before
 *    today's, when it stands struck — re-read as struck (B-14:
 *    never re-struck) from its stored held/bracket facts and its
 *    punches, then carried through
 *    [plateTruthForDoorway].
 *
 * When none of these stand, the result IS [DoorwayState.AtRest]:
 * the honest empty doorway, derived — never hardcoded.
 */
private suspend fun loadDoorwayState(context: Context): DoorwayState =
    withContext(Dispatchers.IO) {
        val store = StrongroomStore(AppContainer.dao(context.applicationContext))
        val todayIso = StrongroomDay.filingDateIso(System.currentTimeMillis())
        val yesterdayIso = java.time.LocalDate.parse(todayIso).minusDays(1).toString()

        val todayRow = store.day(todayIso)
        val composedDay = todayRow
            ?.takeIf {
                it.state == StrongroomDayEntity.STATE_COMPOSED ||
                    it.state == StrongroomDayEntity.STATE_SIGNED
            }
            ?.let { row ->
                StrongroomComposition.ComposedDay(
                    filingDateIso = row.filingDateIso,
                    shape = runCatching {
                        StrongroomComposition.DayShape.valueOf(row.dayShape)
                    }.getOrDefault(StrongroomComposition.DayShape.ORDINARY),
                    record = row.toSigningRecord(),
                    amendedBySigning = row.amendedBySigning
                )
            }

        val waiting = store.heldArrivals(todayIso).any {
            it.state == StrongroomHeldArrivalEntity.STATE_WAITING
        }

        val yesterdayRow = store.day(yesterdayIso)
        val plate = yesterdayRow
            ?.takeIf { it.state in StrongroomDayEntity.STRUCK_STATES }
            ?.let { row ->
                plateTruthForDoorway(
                    StrongroomDay.DayPlate(
                        filingDateIso = row.filingDateIso,
                        hoursHeldMillis = row.struckHeldMillis,
                        punches = store.punches(yesterdayIso),
                        sealState = runCatching {
                            StrongroomDay.SealState.valueOf(row.state)
                        }.getOrDefault(StrongroomDay.SealState.BROKEN),
                        crankSealBroken = row.crankSealBroken,
                        bracketGapMillis = row.struckBracketGapMillis,
                        taskLine = row.taskLine
                    )
                )
            }

        DoorwayState.fromFacts(
            composedDay = composedDay,
            waitingSlipPresent = waiting,
            plate = plate
        )
    }
/**
 * The drawer's standing (BUILD-SR-41) — the ONLY source of
 * [DrawerState] in the composed app, read at each opening:
 *
 *  - **The plates**: every Strongroom day row standing STRUCK
 *    (LIFTED / BROKEN / AJAR), re-read as struck (B-14: never
 *    re-struck) from its stored held/bracket facts and its
 *    punches — the same row the doorway's press reads for
 *    yesterday, here for the whole season. The bracket pair is
 *    NOT carried: the day row persists the gap's length but not
 *    its hours, and [DrawerContents]' mapping forbids inventing
 *    them (the doorway composition's named resolution, carried).
 *  - **The stored tasks**: the store's drawerPage rows (legacy
 *    COMPLETED tasks), paged until a short page — the drawer's
 *    older keeping, in his own words.
 *  - **The handing back**: every arrival still WAITING under a
 *    struck day, oldest day first, arrival order within the
 *    day, each person once — P6's preparation
 *    ([LiftingPlate.HandingBack.prepare]) over the store's rows.
 *    A WAITING row is the record that someone waited; delivery
 *    is confirmed at the threshold (see the DrawerActions glue),
 *    and no row is resolved by this read.
 */
private suspend fun loadDrawerState(context: Context): DrawerState =
    withContext(Dispatchers.IO) {
        val dao = AppContainer.dao(context.applicationContext)
        val store = StrongroomStore(dao)
        val days = dao.getAllStrongroomDaysSnapshot()
        val struckRows = days
            .filter { it.state in StrongroomDayEntity.STRUCK_STATES }
            .sortedBy { it.filingDateIso }
        val plates = struckRows.map { row ->
            StrongroomDay.DayPlate(
                filingDateIso = row.filingDateIso,
                hoursHeldMillis = row.struckHeldMillis,
                punches = store.punches(row.filingDateIso),
                sealState = runCatching {
                    StrongroomDay.SealState.valueOf(row.state)
                }.getOrDefault(StrongroomDay.SealState.BROKEN),
                crankSealBroken = row.crankSealBroken,
                bracketGapMillis = row.struckBracketGapMillis,
                taskLine = row.taskLine
            )
        }
        val stored = mutableListOf<StrongroomDay.DrawerEntry>()
        var offset = 0
        while (true) {
            val page = store.drawerPage(0L, Long.MAX_VALUE, 100, offset)
            stored += page
            if (page.size < 100) break
            offset += page.size
        }
        val waiting = struckRows.flatMap { row ->
            store.heldArrivals(row.filingDateIso)
                .filter { it.state == StrongroomHeldArrivalEntity.STATE_WAITING }
                .map { arrival ->
                    LiftingPlate.WaitingPerson(
                        displayName = arrival.line,
                        ownListRef = "held:${arrival.id}"
                    )
                }
        }
        DrawerState(
            contents = DrawerContents.derive(plates, stored),
            handingBack = LiftingPlate.HandingBack.prepare(waiting),
            handingBackDelivered = false
        )
    }

/**
 * The seal's standing facts (BUILD-SR-37) — the ONLY source of the
 * sealed face and the swing's invocability in the composed app:
 *
 *  - **The sealed face**: the Strongroom store's sealed day, when
 *    one stands, re-expressed as [SealedFaceState] from its
 *    standing signing record and the door-word as it stands in
 *    [DoorWordStore]. Derived here, once per read, as an immutable
 *    snapshot — the face paints whole from it (the 100 ms paint
 *    bar's ground) and is never re-derived from live facts (held
 *    arrivals, punches, the purse) while the seal stands.
 *  - **The swing's invocability**: today's filing-day row standing
 *    COMPOSED or SIGNED, with no seal already standing. The swing
 *    fires from the standing signing exactly as it stands (the
 *    app never checks whether the evening happened, B-2); a day
 *    that does not stand makes no swing invocable, and an unsigned
 *    day stands open (GAP-COORD-04 as ruled).
 */
private suspend fun loadSealFacts(context: Context): Pair<SealedFaceState?, Boolean> =
    withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        val store = StrongroomStore(AppContainer.dao(appContext))
        val sealedRow = store.sealedDay()
        val face = sealedRow?.let { row ->
            SealedFaceState.standing(
                sealedRecord = row.toSigningRecord(),
                doorWord = DoorWordStore(appContext).doorWord()
            )
        }
        val todayIso = StrongroomDay.filingDateIso(System.currentTimeMillis())
        val todayRow = store.day(todayIso)
        val swingStanding = face == null && todayRow != null &&
            (todayRow.state == StrongroomDayEntity.STATE_COMPOSED ||
                todayRow.state == StrongroomDayEntity.STATE_SIGNED)
        face to swingStanding
    }

/**
 * The lifting's standing facts (BUILD-SR-40) — the ONLY source
 * of [LiftingState] in the composed app:
 *
 *  - **The struck days**: every Strongroom day row standing in a
 *    struck state (LIFTED · BROKEN · AJAR), newest filing day
 *    first. Each is re-read AS STRUCK from its stored held /
 *    bracket facts and its punches (B-14: never re-struck), and
 *    carried through [StruckPlateView.fromStruckPlate] — the
 *    mapping that mints the struck forms the doorway's model
 *    carries unrendered: the hours-held figure (TX-SHOW-10)
 *    and the bracket pair (TX-SHOW-14), the pair's hours derived
 *    from the stored sealing instant + the stored held/gap
 *    lengths, and nothing else.
 *  - **The pile**: those same struck plates in stack order —
 *    the pile's facts are the store's struck rows (the same
 *    pile the Drawer keeps; two views, one truth).
 *  - **The ceremony offset**: read against the standing
 *    finish's elapsed zero ([finishE0Mono], SystemClock basis —
 *    the same clock the ceremony score runs on); with no finish
 *    standing, the surface stands at the score's end.
 *
 * No struck day → null: the lifting does not stand (B-14).
 */
private suspend fun loadLiftingState(
    context: Context,
    finishE0Mono: Long?
): LiftingState? = withContext(Dispatchers.IO) {
    val appContext = context.applicationContext
    val dao = AppContainer.dao(appContext)
    val store = StrongroomStore(dao)
    val struckRows = dao.getAllStrongroomDaysSnapshot()
        .filter { it.state in StrongroomDayEntity.STRUCK_STATES }
        .sortedByDescending { it.filingDateIso }
    if (struckRows.isEmpty()) return@withContext null
    val views = struckRows.map { row ->
        StruckPlateView.fromStruckPlate(
            plate = StrongroomDay.DayPlate(
                filingDateIso = row.filingDateIso,
                hoursHeldMillis = row.struckHeldMillis,
                punches = store.punches(row.filingDateIso),
                sealState = runCatching { StrongroomDay.SealState.valueOf(row.state) }
                    .getOrDefault(StrongroomDay.SealState.BROKEN),
                crankSealBroken = row.crankSealBroken,
                bracketGapMillis = row.struckBracketGapMillis,
                taskLine = row.taskLine
            ),
            sealedAtWallMillis = row.sealedAtWallMillis
        )
    }
    val latest = views.first()
    val offset = finishE0Mono?.let {
        (android.os.SystemClock.elapsedRealtime() - it).toInt()
    } ?: com.example.ui.lifting.LiftingCeremony.ScoreEndMs
    LiftingState(
        plate = latest,
        pile = PlatePileView.fromPlates(views),
        isFailure = latest.isFailure,
        ceremonyStartOffsetMs = offset.coerceIn(
            0, com.example.ui.lifting.LiftingCeremony.ScoreEndMs
        )
    )
}

// Issue #27 (finding 5): a FIX trip must never die silently.
// Every Settings launch goes through here: the exact pane
// first; if the OEM throws, the generic Settings home is the
// fallback; if even that fails, the user is TOLD, in words,
// instead of being stranded on a dead tap.
private fun launchSettings(context: Context, intent: Intent, what: String) {
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
private fun openFixPane(
    context: Context,
    permissionStepStore: com.example.data.PermissionStepStore,
    viewModel: MonolithViewModel,
    parkIntroDeliveredThisOpen: Boolean,
    onParkIntroFixTrip: () -> Unit,
    item: com.example.service.WatchItemId
) {
    // Issue #534 F2: a FIX trip is the intro's other spend —
    // the user engaged with the board, so the intro has done
    // its work even if the dwell has not elapsed. Spend at the
    // trip itself; the flag also lets the eventual dismissal
    // judge the same way.
    if (parkIntroDeliveredThisOpen && viewModel.parkIntroPending()) {
        onParkIntroFixTrip()
        viewModel.markParkIntroDone()
    }
    // Issue #324 finding 4: record the step BEFORE the trip —
    // the persisted in-flight step is what a process death
    // during the Settings stay resumes from.
    permissionStepStore.beginTrip(item)
    // Issue #308: arm the return trip BEFORE the pane opens —
    // the service polls this item's grant and brings the user
    // back the moment it lands, instead of stranding them in
    // Settings.
    PermissionReturnService.arm(context, item)
    when (item) {
        // Audit R6 #340 F44: AUTOSTART has no pane to be sent
        // to — the tap IS the oath. It records the attestation
        // in the Watch Store and returns; no Settings trip is
        // faked for a gate the OS refuses to report.
        com.example.service.WatchItemId.AUTOSTART -> {
            com.example.service.WatchStore.attest(item)
        }
        com.example.service.WatchItemId.ACCESSIBILITY ->
            launchSettings(context, pinnedSettingsIntent(Settings.ACTION_ACCESSIBILITY_SETTINGS), "ACCESSIBILITY")
        com.example.service.WatchItemId.USAGE_ACCESS ->
            launchSettings(context, pinnedSettingsIntent(Settings.ACTION_USAGE_ACCESS_SETTINGS), "USAGE ACCESS")
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
                    launchSettings(context, pinnedSettingsIntent(Settings.ACTION_SECURITY_SETTINGS), "DEVICE ADMIN")
                }
        }
        com.example.service.WatchItemId.NOTIFICATION_LISTENER ->
            launchSettings(
                context,
                pinnedSettingsIntent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"),
                "NOTIFICATION ACCESS"
            )
        com.example.service.WatchItemId.OVERLAY ->
            launchSettings(
                context,
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
            // Audit R6 #341 F28: on Xiaomi the explicit BATTERY
            // step comes first — MIUI's per-app battery saver
            // (PowerKeeper → Monolith → "No restrictions") is
            // the third gate the AOSP read cannot see, and the
            // step Blockit's grant flow names explicitly. Its
            // panes are guesses; fall through to autostart,
            // then to AOSP, exactly as before.
            val miuiBatteryOpened = gate ==
                com.example.data.OemPolicy.AutostartGate.XIAOMI &&
                com.example.data.OemPolicy
                    .miuiBatterySaverIntents(context.packageName)
                    .any { pane ->
                        runCatching { context.startActivity(pane) }
                            .isSuccess
                    }
            val autostartOpened = miuiBatteryOpened || (gate !=
                com.example.data.OemPolicy.AutostartGate.NONE &&
                com.example.data.OemPolicy
                    .autostartIntents(gate, context.packageName)
                    .any { pane ->
                        runCatching { context.startActivity(pane) }
                            .isSuccess
                    })
            if (!autostartOpened) {
                val direct = pinnedSettingsIntent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:${context.packageName}")
                )
                runCatching { context.startActivity(direct) }
                    .getOrElse {
                        launchSettings(
                            context,
                            pinnedSettingsIntent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS),
                            "BATTERY"
                        )
                    }
            }
        }
        else -> launchSettings(context, pinnedSettingsIntent(Settings.ACTION_SETTINGS), item.name)
    }
}

// Issue #534 F3(d): the denied note line's re-ask. While the
// system will still show its dialog, re-fire the runtime
// request; once it will not (rationale gone after a recorded
// denial), the only honest path is this app's own notification
// settings pane — the house FIX-trip pattern.
private fun reaskPostNotifications(
    context: android.content.Context,
    viewModel: MonolithViewModel,
    notifPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    onNotificationsGranted: () -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED
    ) {
        onNotificationsGranted()
        return
    }
    val activity = context as? android.app.Activity
    val rationaleGone = activity != null &&
        !androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.POST_NOTIFICATIONS
        )
    if (rationaleGone && viewModel.postNotificationsGranted() == false) {
        launchSettings(
            context,
            pinnedSettingsIntent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            },
            "NOTIFICATIONS"
        )
    } else {
        notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
