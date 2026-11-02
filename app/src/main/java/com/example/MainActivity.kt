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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.PermissionStepStore
import com.example.service.FailureLog
import com.example.service.GlanceRouting
import com.example.service.InterceptConsumptionStore
import com.example.service.InterceptNonce
import com.example.service.InterceptPath
import com.example.service.LoadoutShortcuts
import com.example.service.MonolithDeviceAdminReceiver
import com.example.service.MonolithShieldService
import com.example.service.PermissionReturnService
import com.example.service.WatchItemId
import com.example.service.WatchStore
import com.example.session.MonolithDestination
import com.example.session.MonolithViewModel
import kotlinx.coroutines.launch

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
// the POST_NOTIFICATIONS re-ask, and the archive FLAG_SECURE law
// (archiveSurfaceSecure). The old composition — MonolithRootApp,
// the four screens, every overlay host — is DELETED with the rest
// of com/example/ui/** in this same act. What composes here is
// ShellHost below: the MINIMUM HOST, not a designed surface. The
// foundation (PR #868) and the surface slices compose onto this
// host in their own acts.
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


/**
 * THE MINIMUM HOST — everything this composable is, stated plainly:
 * it carries the shell's lifecycle wiring (residency stamp, the
 * ON_RESUME watch sweep + return-service stop, the ON_STOP airlock
 * abandonment note), the archive FLAG_SECURE effect keyed on the
 * ViewModel's standing destination, and the airlock-holds-Back law.
 * Its entire visual content is one static word on the default
 * Material ground — a birth certificate, not a surface. No old
 * destination is composed here; no DECK/Shield/Sequencer/Drawer
 * surface exists anywhere in the tree from this act on.
 */
@Composable
private fun ShellHost(viewModel: MonolithViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val airlockState by viewModel.airlockState.collectAsStateWithLifecycle()

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

    // Issue #264 B-264-31: the archive FLAG_SECURE law, verbatim
    // from the old composition — while the standing destination
    // is the archive, the window is FLAG_SECURE; every other
    // destination clears it.
    val archiveWindow = (context as? android.app.Activity)?.window
    LaunchedEffect(currentDestination) {
        if (archiveSurfaceSecure(currentDestination)) {
            archiveWindow?.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            archiveWindow?.clearFlags(
                android.view.WindowManager.LayoutParams.FLAG_SECURE
            )
        }
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

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "ULYSSES")
        }
    }
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

/**
 * Issue #264 (B-264-31): which destinations carry the archive.
 * The Ledger (VAULT) holds every receipt, day word and finished
 * session — while it stands, the activity window is FLAG_SECURE
 * (no screenshot, no Recents thumbnail of the user's history).
 * Every other destination is a working surface, not the archive,
 * and stays capturable. Pure, so the matrix is unit-tested.
 */
internal fun archiveSurfaceSecure(destination: MonolithDestination): Boolean =
    destination == MonolithDestination.VAULT
