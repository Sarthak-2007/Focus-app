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
import android.os.SystemClock
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.data.MonolithDatabase
import com.example.data.MonolithRepository
import com.example.data.ReturnChitState
import com.example.data.chitVerdictFor
import com.example.data.SmartLoadout
import com.example.service.InterceptConsumptionStore
import com.example.service.InterceptNonce
import com.example.service.InterceptPath
import com.example.service.MonolithDeviceAdminReceiver
import com.example.service.MonolithShieldService
import com.example.service.PermPrint
import com.example.service.PermissionReturnService
import com.example.service.TactileSoundEngine
import com.example.service.WatchItemId
import com.example.service.WatchStore
import com.example.ui.GlanceRouting
import com.example.ui.MonolithDestination
import com.example.ui.ArmPhase
import com.example.ui.MonolithViewModel
import com.example.ui.BreakDimScrim
import com.example.ui.nightDesaturate
import com.example.ui.rememberBreakDimAlpha
import com.example.ui.components.AirlockInterceptOverlay
import com.example.ui.components.BlockitActionChip
import com.example.ui.components.AlwaysOnDeskClockOverlay
import com.example.ui.components.LauncherBlockScreen
import com.example.ui.components.PeekCountdownPill
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

        setContent {
            val viewModel = rootViewModel
            viewModelRef = viewModel
            interactionViewModel = viewModel
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
        // FIX-4: a repeat intercept against this live (singleTop)
        // activity consumes its extras HERE — first composition is
        // no longer the only reader, so a second intercept can
        // never land him on the previous app's stale door.
        viewModelRef?.let { handleIncomingInterceptIntent(intent, it) }
    }

    private fun handleIncomingInterceptIntent(intent: Intent?, viewModel: MonolithViewModel) {
        if (intent == null) return
        // Issue #155 (F5): a tap on a glance card lands on the
        // surface the card is ABOUT, per GlanceRouting's per-state
        // table — not the #312 single route to SHIELD, which the
        // seal law in navigateTo refuses mid-session (dead code)
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
                GlanceRouting.destinationFor(about)?.let { viewModel.navigateTo(it) }
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

// ------------------------------------------------------------------
// Issue #90 (complexity F3, hoist step): these four helpers used to
// be nested funs inside MonolithRootApp — three of them named in
// the audit finding itself. They are file-level now, with every
// captured value passed explicitly; bodies are verbatim, so
// behaviour is unchanged and each helper is testable in isolation.
// ------------------------------------------------------------------

/**
 * Issue #534 F2: how long the delivered intro must stand in front
 * of the user before a dismissal counts as "read". A reflexive
 * scrim tap lands inside a second or two; the intro is ~60 words
 * and cannot have been read in that window. A FIX trip spends the
 * bit immediately at any dwell — engaging with the board IS
 * having read enough to act.
 */
internal const val PARK_INTRO_READ_DWELL_MS = 5_000L

/**
 * Issue #534 F2: the intro-spend verdict for one dismissal, as
 * pure arithmetic so the acceptance cases table directly:
 *  - never delivered this open -> never spends;
 *  - a FIX trip was taken this open -> spends;
 *  - otherwise spends only once the read dwell has elapsed.
 */
internal fun parkIntroSpendsOnDismiss(
    deliveredThisOpen: Boolean,
    fixTripTakenThisOpen: Boolean,
    deliveredAtMonoMs: Long,
    nowMonoMs: Long
): Boolean =
    deliveredThisOpen &&
        (fixTripTakenThisOpen ||
            (deliveredAtMonoMs > 0L && nowMonoMs - deliveredAtMonoMs >= PARK_INTRO_READ_DWELL_MS))

private fun closePark(
    parkOpenState: MutableState<Boolean>,
    digestSheetVisible: Boolean,
    viewModel: MonolithViewModel,
    dismissReceipt: Boolean,
    spendParkIntro: Boolean = false
) {
    parkOpenState.value = false
    if (dismissReceipt && digestSheetVisible) {
        viewModel.toggleNotificationDigestSheet(false)
    }
    // Issue #534 F2 (supersedes issue-156 F3's spend-on-delivery):
    // the intro bit is spent HERE, at dismissal, and only when the
    // caller judged this dismissal a read (see
    // parkIntroSpendsOnDismiss: delivered this open AND either a
    // FIX trip was taken or the read dwell elapsed). A reflexive
    // scrim tap inside the dwell spends nothing, so the intro
    // rises again on the next launch. The rise itself never spends.
    if (spendParkIntro && viewModel.parkIntroPending()) {
        viewModel.markParkIntroDone()
    }
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
    // Issue #14 (#70-F2 remainder): the Reception Sheet's later
    // movement gates on the session history — finished sessions,
    // and finished sessions in which a Safe Break was taken.
    val sessionHistory by viewModel.sessionHistory.collectAsStateWithLifecycle()
    val guardSettings by viewModel.guardSettingsState.collectAsStateWithLifecycle()
    // Issue #324, finding 4 (build half, relabelled: primer
    // re-creation + binding + persisted step state): the
    // permission sequence's persisted step. The store holds the
    // in-flight FIX trip across process death; the standing step
    // itself is DERIVED below from the sweep's live grants plus
    // that in-flight record — never from a stored "done" bit.
    val permissionStepStore = remember { com.example.data.PermissionStepStore(context) }
    var permissionPrimerDismissed by remember { mutableStateOf(false) }
    val permissionGrants = watchSnapshot.items.mapValues { (_, measurement) ->
        measurement.perm == com.example.service.PermState.GRANTED
    }
    val permissionStep = com.example.data.PermissionStepLogic.currentStep(
        permissionGrants, permissionStepStore.inFlightItem()
    )
    // A landed in-flight grant closes its trip in the store.
    LaunchedEffect(watchSnapshot) {
        val inFlight = permissionStepStore.inFlightItem()
        if (inFlight != null && permissionGrants[inFlight] == true) {
            permissionStepStore.clearInFlight()
        }
    }
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
    val forgedArtifactIds by viewModel.forgedArtifactIds.collectAsStateWithLifecycle()
    val curioForgeRecords by viewModel.curioForgeRecords.collectAsStateWithLifecycle()
    val forgeStats by viewModel.forgeStats.collectAsStateWithLifecycle()
    val receiptIndex by viewModel.receiptIndex.collectAsStateWithLifecycle()
    val dayWords by viewModel.dayWords.collectAsStateWithLifecycle()
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
    // Issue #13: A-4's cause table, A-10(v)'s keep-awake +
    // surrender fade, E-1's veil + Landing — all VM-owned state.
    val deskClockEntryCause by viewModel.deskClockEntryCause.collectAsStateWithLifecycle()
    val deskClockExitIsFinish by viewModel.deskClockExitIsFinish.collectAsStateWithLifecycle()
    val deskClockSurrenderFade by viewModel.deskClockSurrenderFade.collectAsStateWithLifecycle()
    val deskClockKeepAwake by viewModel.deskClockKeepAwake.collectAsStateWithLifecycle()
    val deskClockDescentApplies by viewModel.deskClockDescentApplies.collectAsStateWithLifecycle()
    // Wave-3 (E-1 as one scrim): the night return's phase +
    // snap-entry fact, the yield's black, the finish ground's
    // release, the snap/yield exit kinds, the raise generation,
    // and the pre-decision black gate — all VM-owned.
    val nightReturnPhase by viewModel.nightReturnPhase.collectAsStateWithLifecycle()
    val nightReturnSnapEntry by viewModel.nightReturnSnapEntry.collectAsStateWithLifecycle()
    val deskClockYielded by viewModel.deskClockYielded.collectAsStateWithLifecycle()
    val deskClockGroundRelease by viewModel.deskClockGroundRelease.collectAsStateWithLifecycle()
    val deskClockExitIsYield by viewModel.deskClockExitIsYield.collectAsStateWithLifecycle()
    val deskClockExitSnap by viewModel.deskClockExitSnap.collectAsStateWithLifecycle()
    val deskClockRaiseGeneration by viewModel.deskClockRaiseGeneration.collectAsStateWithLifecycle()
    val deskClockEntryStartsAtFull by viewModel.deskClockEntryStartsAtFull.collectAsStateWithLifecycle()
    val deskClockColdGate by viewModel.deskClockColdGate.collectAsStateWithLifecycle()

    // A-10(v): keep-awake ownership is the VM's session
    // machinery; this host only APPLIES the flag it is handed.
    val activityWindow = (androidx.compose.ui.platform.LocalContext.current as? android.app.Activity)?.window
    androidx.compose.runtime.LaunchedEffect(deskClockKeepAwake) {
        if (deskClockKeepAwake) {
            activityWindow?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activityWindow?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    // E-1 AS ONE SCRIM (wave-3 — Daemmer DAE-6 / Hauch H-1 /
    // Ruhe R-2): ONE animated alpha over the VM's night-return
    // phase — VEILED 0.80 / LANDING 0.45 / OPEN 0 — every leg
    // tween(520, Curve C), except the entries the phase machine
    // marks as snaps (the veil's covered entry, a hand
    // dismissal's Landing entry, an expiry clear). The wave-1/2
    // form was two objects (a boolean 0.80 veil box + this
    // separate Landing animator starting from 0), whose sum
    // flashed full Deck light for ~300 ms at the unveil. One
    // value cannot overshoot: after the stand-down the light
    // never rises except at his unveil (to 0.45) and his verb.
    val nightScrimAlpha = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(nightReturnPhase) {
        val target = when (nightReturnPhase) {
            com.example.ui.NightReturnPhase.VEILED -> 0.80f
            com.example.ui.NightReturnPhase.LANDING -> 0.45f
            com.example.ui.NightReturnPhase.OPEN -> 0f
        }
        if (nightReturnSnapEntry) {
            nightScrimAlpha.snapTo(target)
        } else {
            nightScrimAlpha.animateTo(
                target,
                androidx.compose.animation.core.tween(
                    520, easing = com.example.ui.theme.MonolithCurves.EasingC
                )
            )
        }
    }
    // Ruhe R-4: while a night return is armed, the Deck holds
    // its NIGHT scheme — 0.55 is a weight only against a known
    // ground, and the stand-down path's evaluation has already
    // flipped to day by the time the return begins.
    val nightReturnArmed = nightReturnPhase != com.example.ui.NightReturnPhase.OPEN
    val lastReceiptSessionId by viewModel.lastReceiptSessionId.collectAsStateWithLifecycle()
    // Issue #143 F4: the DND gate's line for the Deck.
    val dndLine by viewModel.dndLine.collectAsStateWithLifecycle()
    // Issue #143 F1/F5/F6: the shield loop's published break-dim
    // phase — the descent into the break and the fade out of it.
    val breakDimState by MonolithShieldService.breakDim.collectAsStateWithLifecycle()
    val breakDimAlpha = rememberBreakDimAlpha(breakDimState)
    val burnInFigure8MinuteStep by viewModel.burnInFigure8MinuteStep.collectAsStateWithLifecycle()
    val showNotificationDigestSheet by viewModel.showNotificationDigestSheet.collectAsStateWithLifecycle()
    val capturedNotifications by viewModel.capturedNotifications.collectAsStateWithLifecycle()
    val totalFocusedMinutes by viewModel.totalFocusedMinutes.collectAsStateWithLifecycle()
    val totalInterceptions by viewModel.totalInterceptions.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val soundscapeEnabled by viewModel.soundscapeEnabled.collectAsStateWithLifecycle()
    val airlockState by viewModel.airlockState.collectAsStateWithLifecycle()
    val floatingPeekPill by viewModel.floatingPeekPill.collectAsStateWithLifecycle()
    val disarmChallenge by viewModel.disarmChallenge.collectAsStateWithLifecycle()
    val weeklyEmergencyTokensRemaining by viewModel.weeklyEmergencyTokensRemaining.collectAsStateWithLifecycle()
    val ratchetWarningMessage by viewModel.ratchetWarningMessage.collectAsStateWithLifecycle()
    // R-5 (issue #7): Shield's status sentence derivation.
    val shieldBlockingStatus by viewModel.shieldBlockingStatus.collectAsStateWithLifecycle()
    val routineConflictNotice by viewModel.routineConflictNotice.collectAsStateWithLifecycle()
    val tierChangeForUndo by viewModel.tierChangeForUndo.collectAsStateWithLifecycle()

    val sealed = activeSession?.isActive == true
    // Shield Rethink 2, Step 1: the receipt line for the Shield's
    // dated truth line, and the wedge's grace carve-out — the same
    // <=10s window the Deck's grace abort reads (elapsedSeconds).
    val shieldReceiptLine by viewModel.shieldReceiptLine.collectAsStateWithLifecycle()
    val shieldIntention by viewModel.shieldIntention.collectAsStateWithLifecycle()
    val shieldInGracePeriod = activeSession?.let { it.isActive && it.elapsedSeconds <= 10 } == true
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
    // Wave-3 (Ruhe R-4): an armed night return holds the night
    // scheme for the header/PARK/Shield neighbours too, until
    // the bloom hands the scheme back to the evaluation.
    val effectiveNight = darkTheme || sealed || nightReturnArmed
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
            // Issue #256: the nav bar follows the ground exactly
            // like the status bar — it was pinned dark in both
            // branches, guaranteeing an illegible bar by day.
            navigationBarStyle = if (groundNight) {
                SystemBarStyle.dark(
                    android.graphics.Color.TRANSPARENT
                )
            } else {
                SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
            }
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
        // Empfang gate (issue #14 graft): while the setup stands
        // unstamped the RECEPTION does the first-open introducing,
        // and PARK does not invite itself over it — the two
        // first-open surfaces never stack. Stamping discharges the
        // intro bit itself (stampSetupOnly), so a stamped run is
        // not re-invited by this effect either.
        if (!sealed && !viewModel.navigationLocked() &&
            viewModel.parkIntroPending() && setupStamped &&
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

    // F-B1: PARK's invocation dismisses the receipt sheet on the
    // same input, and so does any navigation away from the Deck.
    // Issue #10 (audit item 20, interim law): every dismissal
    // also persists the slip's № through the VM, so the dismissed
    // slip stays dismissed across process death — the remember
    // above alone re-ambushed at every cold start.
    LaunchedEffect(currentDestination, parkOpen) {
        if (currentDestination != MonolithDestination.DECK || parkOpen) {
            receiptState?.let {
                dismissedReceiptSessionId = it.sessionId
                viewModel.dismissReceipt(it.sessionId)
            }
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

    // Issue #534 F2: delivery is RECORDED here, never spent. The
    // intro counts as delivered this open once PARK stands open
    // over a measured board; the spend itself happens at
    // dismissal (closePark) or on the first FIX trip
    // (openFixPane), judged by parkIntroSpendsOnDismiss. All three
    // facts are transient (remember, not persisted): a fresh
    // process starts undelivered, so an intro that was risen and
    // reflexively dismissed rises again on the next launch.
    // Declared after the latch above so the latch reads the
    // pending bit first.
    var parkIntroDeliveredThisOpen by remember { mutableStateOf(false) }
    var parkIntroDeliveredAtMonoMs by remember { mutableStateOf(0L) }
    var parkIntroFixTripThisOpen by remember { mutableStateOf(false) }
    LaunchedEffect(parkOpen) {
        if (!parkOpen) {
            parkIntroDeliveredThisOpen = false
            parkIntroDeliveredAtMonoMs = 0L
            parkIntroFixTripThisOpen = false
        }
    }
    LaunchedEffect(parkOpen, watchSnapshot) {
        if (parkOpen && watchSnapshot.items.isNotEmpty() &&
            viewModel.parkIntroPending() && !parkIntroDeliveredThisOpen
        ) {
            parkIntroDeliveredThisOpen = true
            parkIntroDeliveredAtMonoMs = SystemClock.elapsedRealtime()
        }
    }
    // The dismissal verdict for THIS opening, computed at the
    // moment of dismissal by every closePark call site below.
    fun parkIntroSpendForDismissal(): Boolean = parkIntroSpendsOnDismiss(
        deliveredThisOpen = parkIntroDeliveredThisOpen,
        fixTripTakenThisOpen = parkIntroFixTripThisOpen,
        deliveredAtMonoMs = parkIntroDeliveredAtMonoMs,
        nowMonoMs = SystemClock.elapsedRealtime()
    )

    // Issue #280 (R5 supplement): POST_NOTIFICATIONS was declared
    // in the manifest but never asked, so the seal's own
    // notifications could be silently unpostable. It is asked
    // once, as a runtime permission, when the first-run board is
    // delivered — not as a Settings trip and never as a gate: the
    // board and the seal work the same whether it is granted.
    // Issue #534 F3: the answer is now RECORDED (persisted via the
    // VM) and printed on the board; the callback also restarts the
    // intro's read dwell, because the system dialog stood OVER the
    // intro text for its whole life — time under the dialog is not
    // reading time.
    var postNotificationsGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                viewModel.postNotificationsGranted()
            }
        )
    }
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.recordPostNotificationsResult(granted)
        postNotificationsGranted = granted
        if (parkIntroDeliveredThisOpen) {
            parkIntroDeliveredAtMonoMs = SystemClock.elapsedRealtime()
        }
    }
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
    // A re-ask trip (or a Settings trip) hands back through
    // ON_RESUME, which bumps parkReturnTick: re-read the live
    // system fact so PARK's note line never prints a stale answer.
    LaunchedEffect(parkReturnTick) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val live = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (live) postNotificationsGranted = true
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
        } else {
            // Issue #324 finding 4: primer dismissal is per
            // Park-open — closing and reopening the Park (or a
            // fresh process) raises the primer again at the
            // persisted step. That reopening IS the resume.
            permissionPrimerDismissed = false
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
                // Wave-3 (Ruhe R-5): the night return's
                // staleness re-check rides this same resume.
                viewModel.expireNightReturnIfStale()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (parkOpen) {
        BackHandler {
            closePark(parkOpenState, showNotificationDigestSheet, viewModel, dismissReceipt = false,
                spendParkIntro = parkIntroSpendForDismissal())
        }
    } else if (currentDestination != MonolithDestination.DECK) {
        BackHandler { viewModel.navigateTo(MonolithDestination.DECK) }
    }

    // The reception's BACK: leaving stamps nothing. Composed
    // after the park chain above so it wins while the sheet
    // stands, and before the #27 overlay handler below so the
    // safety surfaces (Airlock, disarm challenge) still own
    // Back over it — the sheet is hosted BELOW them. The sheet
    // re-offers at the next cold start and waits in PARK —
    // leaving is never a trap and never a finish.
    if (receptionOpen) {
        BackHandler { receptionOpen = false }
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

    // Issue #534 F3(d): the denied note line's re-ask. While the
    // system will still show its dialog, re-fire the runtime
    // request; once it will not (rationale gone after a recorded
    // denial), the only honest path is this app's own notification
    // settings pane — the house FIX-trip pattern.
    fun reaskPostNotifications() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            postNotificationsGranted = true
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

    // Issue #143 F6: the window's own brightness rides the same
    // dim progression — the palette was never the only lever, and
    // on an LCD the backlight is the light. While the dim is up,
    // this window's brightness steps down with it; at rest (and on
    // dispose) the override is released back to the system value
    // the user set. Scoped to Monolith's window only — nothing
    // here touches the device-wide brightness.
    val rootView = androidx.compose.ui.platform.LocalView.current
    androidx.compose.runtime.DisposableEffect(rootView) {
        onDispose {
            var ctx: android.content.Context? = rootView.context
            var activity: android.app.Activity? = null
            while (ctx is android.content.ContextWrapper) {
                if (ctx is android.app.Activity) { activity = ctx; break }
                ctx = ctx.baseContext
            }
            activity?.window?.let { window ->
                val attrs = window.attributes
                attrs.screenBrightness =
                    android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                window.attributes = attrs
            }
        }
    }
    androidx.compose.runtime.LaunchedEffect(breakDimAlpha, rootView) {
        var ctx: android.content.Context? = rootView.context
        var activity: android.app.Activity? = null
        while (ctx is android.content.ContextWrapper) {
            if (ctx is android.app.Activity) { activity = ctx; break }
            ctx = ctx.baseContext
        }
        activity?.window?.let { window ->
            val attrs = window.attributes
            attrs.screenBrightness = if (breakDimAlpha > 0.001f) {
                com.example.data.NightDimMath.windowBrightnessForDim(breakDimAlpha)
            } else {
                android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            }
            window.attributes = attrs
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
                // Issue #143 F5: while the break dim is up, the
                // whole room drains toward monochrome on the dim's
                // own curve — night/sealed surfaces only in effect,
                // because the dim itself only exists inside a
                // sealed session's break and its fade.
                .nightDesaturate(
                    com.example.data.NightDimMath.saturationForDim(breakDimAlpha)
                )
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
                            parkOpen -> closePark(
                                parkOpenState, showNotificationDigestSheet, viewModel,
                                dismissReceipt = false,
                                spendParkIntro = parkIntroSpendForDismissal()
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
                                    // Wave-3 (Ruhe R-4): plus the
                                    // night return's scheme hold —
                                    // while VEILED/LANDING is armed
                                    // the Deck composes in night,
                                    // whatever the evaluation says.
                                    isDarkTheme = darkTheme || nightReturnArmed,
                                    onDialMinutesChanged = viewModel::setDialMinutes,
                                    onSelectStudyPreset = viewModel::selectStudyPreset,
                                    onSetStudyBlocksCount = viewModel::setStudyBlocksCount,
                                    onCompleteActiveTask = viewModel::completeTaskAndAdvance,
                                    onToggleTaskSubStep = viewModel::toggleTaskSubStep,
                                    onOpenTaskSequencer = { viewModel.navigateTo(MonolithDestination.TASKS) },
                                    // Handover spender 1 (issue #14
                                    // graft): the slide ATTEMPT spends
                                    // the Armed Handover — a refused
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
                                    onStartSafeBreak = { viewModel.startSafeBreakMidBlock() },
                                    onExtendSession = viewModel::extendActiveSession,
                                    dndLine = dndLine,
                                    weeklyEmergencyTokensRemaining = weeklyEmergencyTokensRemaining,
                                    // Issue #103 F1: the repository
                                    // diverts a non-SLEEP routine
                                    // session to its judging funnel —
                                    // a deleted routine row judges
                                    // too (null loadout != SLEEP);
                                    // SLEEP rows fall through to the
                                    // real grace path.
                                    isActiveRoutineJudged = activeSession?.routineId?.let { rid ->
                                        routines.firstOrNull { it.id == rid }
                                            ?.loadoutName != SmartLoadout.SLEEP.name
                                    } ?: false,
                                    appsCellCount = appsCellCount,
                                    appsSplit = appsSplit,
                                    deckWarrant = deckWarrant,
                                    deckPrimerPending = viewModel.deckPrimerPending(),
                                    sealFreeze = sealFreeze,
                                    // Issue #4 B-C1: the Bolt fold's
                                    // shield-beat input, from the
                                    // WatchStore snapshot this host
                                    // already collects.
                                    shieldHeartbeatMono = watchSnapshot.engineHeartbeatMono,
                                    shieldProtecting = watchSnapshot.engineProtecting,
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
                                            context,
                                            pinnedSettingsIntent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
                                            "ACCESSIBILITY"
                                        )
                                    },
                                    onFixUsageAccess = {
                                        launchSettings(
                                            context,
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
                                    peekMinutes = guardSettings?.peekMinutes ?: 2,
                                    loadoutCapIndex = loadoutCapIndex,
                                    isSessionActive = sealed,
                                    blockingStatus = shieldBlockingStatus,
                                    receiptLine = shieldReceiptLine,
                                    intention = shieldIntention,
                                    onIntentionChanged = viewModel::setShieldIntention,
                                    isGracePeriod = shieldInGracePeriod,
                                    onApplySmartLoadout = viewModel::applySmartLoadout,
                                    ratchetWarningMessage = ratchetWarningMessage,
                                    routineConflictNotice = routineConflictNotice,
                                    onDismissRoutineConflictNotice = viewModel::dismissRoutineConflictNotice,
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
                                    onSetAppCategory = viewModel::setAppCategory,
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
                                    drawerSearchDayWords = drawerSearchDayWords,
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
                        // §4.12 (wave-2): PARK's gate composes the
                        // unified doorLocked — HB-1/HB-2 were fixed
                        // everywhere except inside PARK, which drank
                        // from raw `sealed` while the header band
                        // above it already used doorLocked.
                        isSealed = doorLocked,
                        // §4.2: the same warrant the Deck prints.
                        warrant = deckWarrant,
                        introPending = parkIntroForOpen,
                        isNight = effectiveNight,
                        soundEnabled = soundEnabled,
                        shortsReelsSniperEnabled = shortsReelsSniperEnabled,
                        soundEngine = viewModel.soundEngine,
                        themeReadout = themeEvaluation.readout,
                        parkReturnTick = parkReturnTick,
                        onDismiss = {
                            closePark(
                                parkOpenState, showNotificationDigestSheet, viewModel,
                                dismissReceipt = false,
                                spendParkIntro = parkIntroSpendForDismissal()
                            )
                        },
                        onFixClick = { item -> openFixPane(context, permissionStepStore, viewModel, parkIntroDeliveredThisOpen, { parkIntroFixTripThisOpen = true }, item) },
                        onCheckClick = { viewModel.runWatchSweep() },
                        onToggleSound = { viewModel.toggleSound() },
                        soundscapeEnabled = soundscapeEnabled,
                        onToggleSoundscape = { viewModel.toggleSoundscape() },
                        onToggleShortsReelsSniper = { viewModel.toggleShortsReelsSniper() },
                        // Truth Car 3 (UNA-02): the SOCIAL SITES
                        // board line's sled — reads and writes the
                        // same persisted browserBlockerEnabled fact
                        // the accessibility service enforces.
                        socialSitesEnabled = browserUrlBlockerEnabled,
                        onToggleSocialSites = { viewModel.toggleBrowserUrlBlocker() },
                        postNotificationsGranted = postNotificationsGranted,
                        onReaskPostNotifications = { reaskPostNotifications() },
                        onOpenReception = {
                            closePark(
                                parkOpenState, showNotificationDigestSheet, viewModel,
                                dismissReceipt = false,
                                spendParkIntro = parkIntroSpendForDismissal()
                            )
                            receptionOpen = true
                        },
                        setupStampMask = if (setupStamped) {
                            setupStore.doorsAtStampMask()
                        } else {
                            null
                        },
                        peekMinutes = guardSettings?.peekMinutes ?: 2,
                        onPeekMinutesChange = { viewModel.setPeekMinutes(it) },
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
                                dismissReceipt = true,
                                spendParkIntro = parkIntroSpendForDismissal()
                            )
                            val dest = when (idx) {
                                0 -> MonolithDestination.DECK
                                1 -> MonolithDestination.SHIELD
                                2 -> MonolithDestination.TASKS
                                else -> MonolithDestination.VAULT
                            }
                            viewModel.navigateTo(dest)
                            // Issue #128 finding 1: a refused
                            // navigation (e.g. any rail travel
                            // while sealed) changes no state, so
                            // no recomposition would ever re-seat
                            // the cap — settle it back onto the
                            // destination the phone is actually
                            // on, or the band lies.
                            if (viewModel.currentDestination.value != dest) {
                                val actualIdx = when (viewModel.currentDestination.value) {
                                    MonolithDestination.DECK -> 0
                                    MonolithDestination.SHIELD -> 1
                                    MonolithDestination.TASKS -> 2
                                    MonolithDestination.VAULT -> 3
                                }
                                view.settleCapTo(actualIdx)
                            }
                        }
                        view.onParkInvoked = {
                            if (parkOpen) {
                                closePark(
                                    parkOpenState, showNotificationDigestSheet, viewModel,
                                    dismissReceipt = false,
                                    spendParkIntro = parkIntroSpendForDismissal()
                                )
                            } else {
                                openPark(parkOpenState, sealed, viewModel)
                                // Issue #128 finding 4: the cap
                                // travelled into the PARK zone
                                // BEFORE this ask, and a refusal
                                // (sealed / navigation-locked)
                                // changes no state — no
                                // recomposition would ever re-seat
                                // it, so the band stood over the
                                // blank zone printing a selection
                                // that does not exist. On refusal:
                                // the refusal knock (the same
                                // voice as a refused navigation)
                                // and the cap settles back onto
                                // the committed stop.
                                if (!parkOpen) {
                                    viewModel.soundEngine.playSeatKnock(6, 0.2f, 320f)
                                    view.settleCapTo(view.selectedTabIndex)
                                }
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
            // Wave-3 (Schimmer FE-2, piece 2): the pre-decision
            // black gate — while a cold process has a live
            // pre-birth session and no settle decision yet, black
            // stands over the Deck, so the cold open's first
            // surface is black and SEEDED's cut is black → face.
            // Non-clickable: it takes no touch and names nothing.
            if (deskClockColdGate) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.Black)
                        .testTag("desk_clock_cold_gate")
                )
            }

            // E-1 AS ONE SCRIM (wave-3; Hauch H-2 / Glanz G-4):
            // composed BELOW the desk-clock overlay — while the
            // clock stands, its opaque ground covers the scrim
            // completely, so the veil's entry at E0 no longer
            // cuts the finish print's ruled 260 Curve A fade; the
            // scrim becomes visible exactly when the ground
            // lifts, already at its phase weight, over the Deck
            // alone. In LANDING and OPEN the scrim consumes NO
            // touch (every Deck control live under the dim); in
            // VEILED it takes the first touch on pointer DOWN
            // (Hauch H-4 — a click's UP was a completed-tap law
            // a half-awake hand could fail), with NO indication
            // (H-5: a raw pointerInput draws no ripple on the
            // black).
            if (nightScrimAlpha.value > 0f) {
                val veiled = nightReturnPhase == com.example.ui.NightReturnPhase.VEILED
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Color.Black.copy(
                                alpha = nightScrimAlpha.value
                            )
                        )
                        .then(
                            if (veiled) {
                                Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = { viewModel.unveilNightReturn() }
                                        )
                                    }
                                    .testTag("night_return_veil")
                            } else {
                                Modifier
                            }
                        )
                )
            }

            AlwaysOnDeskClockOverlay(
                visible = isAlwaysOnDeskClockVisible,
                activeSession = activeSession,
                activeTask = activeTask,
                // Glaette G-6: dialMinutes no longer threads
                // into the face (see the overlay's signature).
                burnInStep = burnInFigure8MinuteStep,
                entryCause = deskClockEntryCause,
                exitIsFinish = deskClockExitIsFinish,
                surrenderFade = deskClockSurrenderFade,
                descentApplies = deskClockDescentApplies,
                raiseGeneration = deskClockRaiseGeneration,
                entryStartsAtFull = deskClockEntryStartsAtFull,
                groundReleased = deskClockGroundRelease,
                exitIsYield = deskClockExitIsYield,
                exitSnap = deskClockExitSnap,
                dimAlpha = breakDimAlpha,
                onDismiss = { viewModel.toggleAlwaysOnDeskClock(false) }
            )

            // Wave-3 (Kante K-1 / Samt S-3): the yield's own
            // exit — while a yielded session lives, a plain
            // true-black surface stands above the Deck, so the
            // surrender flag clears INTO black and stays black
            // until his wake touch re-raises SEEDED (the VM's
            // wake path; this box is non-clickable and consumes
            // nothing — the Activity's onUserInteraction
            // receives the touch first).
            if (deskClockYielded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.Black)
                        .testTag("desk_clock_yielded_black")
                )
            }

            // The digest renders the FROZEN snapshot whenever a
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
                        viewModel.dismissReceipt(standingReceipt.sessionId)
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
                // #70-F2 remainder (issue #14): a Safe Break counts
                // only where one was actually taken — breakTakenSeconds
                // is the ledger column every break exit writes, and
                // it stays 0 for a session that never broke.
                completedSessions = sessionHistory.count { !it.isActive },
                sessionsWithSafeBreak = sessionHistory.count {
                    !it.isActive && it.breakTakenSeconds > 0
                },
                onShown = { viewModel.runWatchSweep() },
                onFixClick = { item -> openFixPane(context, permissionStepStore, viewModel, parkIntroDeliveredThisOpen, { parkIntroFixTripThisOpen = true }, item) },
                onCheckClick = { viewModel.runWatchSweep() },
                onOpenShield = {
                    // H-2: only the unstamped sheet's excursion
                    // records a departure; a stamped sheet's home
                    // is PARK and its return is PARK.
                    if (!setupStamped) shieldExcursion = true
                    receptionOpen = false
                    // Through a local, as everywhere on current
                    // main: the literal navigateTo(SHIELD) call
                    // shape is the #312 glance route, pinned out
                    // of this file by GlanceRoutingTest. This is
                    // the sheet's own SHIELD door, not a glance
                    // tap (glances route at GlanceRouting above).
                    val shieldDestination = MonolithDestination.SHIELD
                    viewModel.navigateTo(shieldDestination)
                },
                onStamp = { stampSetupOnly() },
                onDone = { stampSetupAndEnter() },
                chit = chitUi,
                onDeparture = { door, print ->
                    setupStore.writeDepartureChit(door, print)
                }
            )

            // Composition note (issue #14 landing, Oct 31): the
            // Slice D comment that rode with this graft ("the
            // ShieldPermissionPrimerModal binding stays REMOVED")
            // is superseded on current main — issue #324 re-created
            // the primer below WITHOUT the banned COMMIT & ARM
            // ANYWAY path, so the register reason for the removal
            // no longer holds. Both surfaces stand: the Reception
            // above, the primer below it in the overlay order.
            // Issue #324, finding 4 (build half, relabelled):
            // the ShieldPermissionPrimerModal is RE-CREATED and
            // bound here. This is NOT the Slice D modal restored:
            // its "COMMIT & ARM ANYWAY" path (register LR2-S1-18)
            // does not exist in the re-creation — the only action
            // is FIX, routed through openFixPane to the step's own
            // Settings pane, and arming still lives where Slice D
            // put it: the warrant slide reads the grants itself
            // and refuses in the open. The primer stands over an
            // open Park while a measured board shows an ungranted
            // step; the step derives from the live grants plus
            // PermissionStepStore's persisted in-flight record, so
            // an interrupted sequence resumes where it stopped.
            com.example.ui.components.ShieldPermissionPrimerModal(
                visible = parkOpen && watchSnapshot.items.isNotEmpty() &&
                    permissionStep != null && !permissionPrimerDismissed,
                currentStep = permissionStep,
                grants = permissionGrants,
                onFixClick = { item ->
                    permissionPrimerDismissed = true
                    openFixPane(context, permissionStepStore, viewModel, parkIntroDeliveredThisOpen, { parkIntroFixTripThisOpen = true }, item)
                },
                onDismiss = { permissionPrimerDismissed = true }
            )

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
                peekMinutes = guardSettings?.peekMinutes ?: 2,
                // FIX-8 / P-2: the honest exit routes to the
                // door-originated Disarm entry — the face stands
                // in front of the still-standing door.
                onEndSessionInstead = viewModel::requestDisarmChallengeFromDoor,
                // FIX-7: the overlay reports the arm instant; the
                // VM sounds the arm beat under its nonce guard.
                onOfferArmed = viewModel::onAirlockOfferArmed,
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
                onHoldBanked = viewModel::recordValveHoldBanked,
                onCompleteCleanSession = viewModel::completeStopwatchCompletionFromDisarmFace
            )

            // Issue #143 F1/F6: the break dim stands over the
            // whole room — topmost, and strictly visual (no
            // pointer handlers): the descent into the break and
            // the fade out at its end. Everything beneath it —
            // Deck, desk clock, the Yield sheet — stays tappable;
            // only the light is taken away.
            BreakDimScrim(alpha = breakDimAlpha)

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
