package com.example.ui.strongroom

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.AppContainer
import com.example.data.BlockedAppEntity
import com.example.data.LegacyCarry
import com.example.data.SetupStore
import com.example.data.StrongroomDayEntity
import com.example.data.StrongroomStore
import com.example.data.toSealSpan
import com.example.data.toSigningRecord
import com.example.domain.LiftingPlate
import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomRouting
import com.example.service.StrongroomEngineBridge
import com.example.service.StrongroomLift
import com.example.service.TactileSoundEngine
import com.example.ui.guard.GuardIntent
import com.example.ui.guard.GuardSurfaceScreen
import com.example.ui.guard.GuardSurfaceSnapshot
import com.example.ui.theme.InkGold
import com.example.ui.theme.PlateDarkRoom
import com.example.ui.theme.PlateInkGold
import com.example.ui.theme.RoomHeadingStyle
import com.example.ui.theme.RoomListStyle
import com.example.ui.theme.SigningHeadStyle
import com.example.ui.theme.SigningQuietStyle
import com.example.ui.theme.StrongroomGround
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * STAGE 15 — THE INTEGRATED PROTOTYPE'S HOST (BUILD-SR-21).
 *
 * One host walks the whole day through the composed surfaces,
 * every transition driven by P1/P2's standing state (the store's
 * day rows + the bridge's clocks) — no dead buttons, no orphan
 * screens: each surface below is reached from the place before
 * it, and each callback lands on a store/bridge act. The flow
 * law (which place, which intake shape) is [StrongroomFlowModel];
 * this file only reads the clocks, loads the facts, and renders
 * the model's decisions.
 *
 * CONTAINMENT: while a Strongroom seal stands, this host is the
 * whole app — MainActivity renders it INSTEAD of the legacy
 * destinations (the launcher-is-everything law, Nov 1; P2's
 * sealed surface set). The legacy substrate engine stays alive
 * beneath (its shield is the swing's own arm); what retires is
 * the legacy ROUTES the new flow replaces, named in the record.
 *
 * Device behaviour stands UNPROVEN-ON-DEVICE (Audit A G-7).
 */

// ----------------------------------------------------------------------
// The gate — what MainActivity asks before choosing its root
// ----------------------------------------------------------------------

/** Standing facts that can force the Strongroom root: a seal
 *  standing (containment) or a struck plate not yet presented
 *  (the lifting is owed its offering before the room). */
data class StrongroomGate(val forced: Boolean, val sealedStanding: Boolean)

/** Presented-strike marker: the lifting is presented once per
 *  strike (date + state), then the plate lives in the pile. A
 *  small prefs fact, in the SetupStore pattern — chrome, not
 *  day data; no schema moves for it. */
internal fun presentedStrikeKey(context: Context): String? =
    context.getSharedPreferences("strongroom_flow", Context.MODE_PRIVATE)
        .getString("presented_strike", null)

internal fun markStrikePresented(context: Context, key: String) {
    context.getSharedPreferences("strongroom_flow", Context.MODE_PRIVATE)
        .edit().putString("presented_strike", key).commit()
}

internal fun strikeKey(row: StrongroomDayEntity): String =
    "${row.filingDateIso}|${row.state}"

/** Poll the gate facts (the store is the truth; the poll only
 *  re-reads it — on composition, on resume ticks of 15 s). */
@Composable
fun rememberStrongroomGate(): StrongroomGate {
    val context = LocalContext.current
    var gate by remember { mutableStateOf(StrongroomGate(forced = false, sealedStanding = false)) }
    LaunchedEffect(Unit) {
        val dao = AppContainer.dao(context.applicationContext)
        while (true) {
            val days = withContext(Dispatchers.IO) { dao.getStrongroomDaysFlow().first() }
            val sealed = days.any { it.state == StrongroomDayEntity.STATE_SEALED }
            val presented = presentedStrikeKey(context.applicationContext)
            val unpresented = days
                .filter { it.state in StrongroomDayEntity.STRUCK_STATES }
                .maxByOrNull { it.filingDateIso }
                ?.let { strikeKey(it) != presented } == true
            gate = StrongroomGate(forced = sealed || unpresented, sealedStanding = sealed)
            delay(15_000)
        }
    }
    return gate
}

// ----------------------------------------------------------------------
// The host
// ----------------------------------------------------------------------

@Composable
fun StrongroomFlowHost(
    allApps: List<BlockedAppEntity>,
    onExit: () -> Unit,
    onSetupStamped: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()
    val dao = remember(appContext) { AppContainer.dao(appContext) }
    val store = remember(dao) { StrongroomStore(dao) }
    val setupStore = remember(appContext) { SetupStore(appContext) }
    val engine = remember(context) { TactileSoundEngine(context) }
    DisposableEffect(engine) { onDispose { engine.stopAllAudio() } }

    val days by dao.getStrongroomDaysFlow().collectAsState(initial = emptyList())

    // A slow tick re-derives the time facts (break running, span
    // lifted) from the span — alarms are hints, the span is the
    // truth (P2's law, applied to the host's own reading).
    var tick by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(15_000)
            tick++
        }
    }

    val nowWall = System.currentTimeMillis()
    val todayIso = remember(tick) { StrongroomDay.filingDateIso(nowWall) }
    val tomorrowIso = remember(todayIso) { LocalDate.parse(todayIso).plusDays(1).toString() }
    val sealedRow = days.firstOrNull { it.state == StrongroomDayEntity.STATE_SEALED }
    val todayRow = days.firstOrNull { it.filingDateIso == todayIso }
    val tomorrowRow = days.firstOrNull { it.filingDateIso == tomorrowIso }
    val struckRow = days
        .filter { it.state in StrongroomDayEntity.STRUCK_STATES }
        .maxByOrNull { it.filingDateIso }
    var presentedKey by remember(appContext) {
        mutableStateOf(presentedStrikeKey(appContext))
    }
    val strikeUnpresented = struckRow != null && strikeKey(struckRow) != presentedKey

    val opening = StrongroomFlowModel.openingPlace(
        setupCompleted = setupStore.isCompleted(),
        sealedStanding = sealedRow != null,
        strikeUnpresented = strikeUnpresented
    )
    var place by remember { mutableStateOf(opening) }
    var antechamberOpen by remember { mutableStateOf(false) }

    // Forced standings move the host, whenever they arrive: a
    // strike presents itself; a seal standing takes the world.
    LaunchedEffect(opening) {
        if (opening == StrongroomFlowModel.Place.LIFTING ||
            opening == StrongroomFlowModel.Place.SEALED_FACE
        ) {
            place = opening
        }
    }
    LaunchedEffect(sealedRow?.filingDateIso) {
        if (sealedRow == null) antechamberOpen = false
    }

    // The break's arrival + the lifting's arrival, re-derived
    // from the span at every tick: a lifted span whose plate is
    // not yet struck is struck here (the bridge's receiver is
    // the other striker; the store's struck-once law makes the
    // race harmless — whoever arrives second reads the plate as
    // struck and writes nothing).
    val sealedSigning = sealedRow?.toSigningRecord()
    val sealedSpan = sealedRow?.toSealSpan()
    val nowElapsed = SystemClock.elapsedRealtime()
    val breakRunning = if (sealedSigning != null && sealedSpan != null) {
        StrongroomDay.breakFiring(sealedSigning, sealedSpan)
            ?.isRunning(nowElapsed) == true &&
            StrongroomEngineBridge.bootCount(appContext) == sealedSpan.sealedBootCount
    } else false
    LaunchedEffect(tick, sealedRow?.filingDateIso) {
        val row = sealedRow ?: return@LaunchedEffect
        val span = row.toSealSpan() ?: return@LaunchedEffect
        if (span.isLifted(SystemClock.elapsedRealtime(), StrongroomEngineBridge.bootCount(appContext))) {
            withContext(Dispatchers.IO) {
                store.strikePlate(
                    row.filingDateIso,
                    SystemClock.elapsedRealtime(),
                    StrongroomEngineBridge.bootCount(appContext),
                    sealFailed = false
                )
            }
        }
    }

    val effectivePlace = if (
        (place == StrongroomFlowModel.Place.SEALED_FACE ||
            place == StrongroomFlowModel.Place.DESK_CLOCK) && sealedRow != null
    ) {
        StrongroomFlowModel.sealedPlace(breakRunning, antechamberOpen)
    } else if (place == StrongroomFlowModel.Place.ANTECHAMBER && sealedRow != null) {
        StrongroomFlowModel.Place.ANTECHAMBER
    } else {
        place
    }

    BackHandler(enabled = true) {
        when (effectivePlace) {
            StrongroomFlowModel.Place.ANTECHAMBER -> {
                antechamberOpen = false
                place = StrongroomFlowModel.Place.SEALED_FACE
            }
            StrongroomFlowModel.Place.DESK_CLOCK ->
                place = StrongroomFlowModel.Place.SEALED_FACE
            StrongroomFlowModel.Place.SEALED_FACE -> Unit // the door holds; Back is not an exit
            StrongroomFlowModel.Place.LIFTING -> Unit // the ceremony is not left by Back
            StrongroomFlowModel.Place.DOORWAY -> if (sealedRow == null) onExit()
            else -> place = StrongroomFlowModel.Place.DOORWAY
        }
    }

    Box(modifier = modifier.fillMaxSize().background(StrongroomGround)) {
        when (effectivePlace) {
            StrongroomFlowModel.Place.FIRST_RUN -> FirstRunPlace(
                store = store,
                dao = dao,
                todayIso = todayIso,
                tomorrowIso = tomorrowIso,
                onConfirm = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            standingOrder(store, dao, todayIso, allApps)?.let { order ->
                                store.composeDay(order, tomorrowIso)
                            }
                        }
                        setupStore.stampSetup(0)
                        onSetupStamped()
                        place = StrongroomFlowModel.Place.DOORWAY
                    }
                }
            )

            StrongroomFlowModel.Place.DOORWAY -> DoorwayPlace(
                todayRow = todayRow,
                tomorrowRow = tomorrowRow,
                sealedStanding = sealedRow != null,
                onSigning = { place = StrongroomFlowModel.Place.SIGNING },
                onSwing = {
                    scope.launch {
                        val span = withContext(Dispatchers.IO) {
                            if (todayRow == null) {
                                standingOrder(store, dao, todayIso, allApps)
                                    ?.let { store.composeDay(it, todayIso) }
                            }
                            StrongroomEngineBridge.activateSeal(appContext, todayIso)
                        }
                        if (span != null) {
                            // R-7 DISCHARGED (BUILD-SR-23, step 7):
                            // this host's SessionNotification
                            // beginSession is STRUCK — BUILD-SR-14's
                            // bridge wiring (PR #856, step 2) fires
                            // beginSession inside activateSeal
                            // itself, and it is the surviving home.
                            engine.playSealThrow()
                            place = StrongroomFlowModel.Place.SEALED_FACE
                        }
                    }
                },
                onPile = { place = StrongroomFlowModel.Place.ROOM_PILE },
                onDrawer = { place = StrongroomFlowModel.Place.ROOM_DRAWER },
                onGuard = { place = StrongroomFlowModel.Place.GUARD },
                onExit = onExit
            )

            StrongroomFlowModel.Place.SIGNING -> SigningPlace(
                store = store,
                dao = dao,
                tomorrowIso = tomorrowIso,
                tomorrowRow = tomorrowRow,
                allApps = allApps,
                onLeave = { place = StrongroomFlowModel.Place.DOORWAY },
                onSigned = { result ->
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            val order = standingOrder(store, dao, todayIso, allApps)
                                ?: orderFromSigning(result, tomorrowRow, allApps, store, nowWall)
                            store.amendDay(
                                order, tomorrowIso,
                                StrongroomComposition.SigningAmendment(
                                    lockedPackages = result.lockedPackages,
                                    liftingTime = LocalTime.parse(result.liftingHour),
                                    sentence = result.sentence,
                                    breakLengthMinutes = result.breakMinutes,
                                    taskLine = result.taskLine
                                )
                            )
                        }
                        place = StrongroomFlowModel.Place.DOORWAY
                    }
                }
            )

            StrongroomFlowModel.Place.SEALED_FACE -> {
                if (sealedSigning != null) {
                    SealedFaceScreen(
                        state = StrongroomFlowModel.sealedFace(sealedSigning),
                        onKnockReach = { engine.playKnock() },
                        onAntechamberRequest = {
                            antechamberOpen = true
                            place = StrongroomFlowModel.Place.ANTECHAMBER
                        }
                    )
                }
            }

            StrongroomFlowModel.Place.ANTECHAMBER -> {
                if (sealedRow != null && sealedSigning != null) {
                    AntechamberPlace(
                        store = store,
                        row = sealedRow,
                        signing = sealedSigning,
                        allApps = allApps,
                        engine = engine,
                        onLeave = {
                            antechamberOpen = false
                            place = StrongroomFlowModel.Place.SEALED_FACE
                        }
                    )
                }
            }

            StrongroomFlowModel.Place.DESK_CLOCK -> {
                if (sealedRow != null && sealedSigning != null && sealedSpan != null) {
                    var punches by remember(sealedRow.filingDateIso) {
                        mutableStateOf<List<StrongroomDay.Punch>>(emptyList())
                    }
                    LaunchedEffect(sealedRow.filingDateIso, tick) {
                        punches = withContext(Dispatchers.IO) {
                            store.punches(sealedRow.filingDateIso)
                        }
                    }
                    Column(Modifier.fillMaxSize().background(PlateDarkRoom)) {
                        DeskClockScreen(
                            spanDurationMillis = sealedSpan.spanDurationMillis,
                            sealedAtElapsedMillis = sealedSpan.sealedAtElapsedMillis,
                            punches = StrongroomFlowModel.deskClockPunches(punches),
                            gaps = StrongroomFlowModel.deskClockGaps(sealedSigning, sealedSpan),
                            modifier = Modifier.weight(1f)
                        )
                        if (!breakRunning) {
                            HostRow("The face", "strongroom_to_face") {
                                place = StrongroomFlowModel.Place.SEALED_FACE
                            }
                        }
                    }
                }
            }

            StrongroomFlowModel.Place.LIFTING -> {
                if (struckRow != null) {
                    LiftingPlace(
                        store = store,
                        row = struckRow,
                        engine = engine,
                        onPresented = {
                            val key = strikeKey(struckRow)
                            markStrikePresented(appContext, key)
                            presentedKey = key
                            place = StrongroomFlowModel.Place.ROOM_PILE
                        }
                    )
                }
            }

            StrongroomFlowModel.Place.ROOM_PILE -> Column(
                Modifier.fillMaxSize().background(PlateDarkRoom)
            ) {
                var pile by remember { mutableStateOf<LiftingPlate.PlatePile?>(null) }
                LaunchedEffect(days) {
                    pile = withContext(Dispatchers.IO) {
                        val plates = days
                            .filter { it.state in StrongroomDayEntity.STRUCK_STATES }
                            .sortedByDescending { it.filingDateIso }
                            .mapNotNull { row ->
                                val dayPlate = store.strikePlate(
                                    row.filingDateIso,
                                    SystemClock.elapsedRealtime(),
                                    StrongroomEngineBridge.bootCount(appContext),
                                    sealFailed = row.state ==
                                        StrongroomDay.SealState.BROKEN.name
                                ) ?: return@mapNotNull null
                                StrongroomFlowModel.struckPlate(
                                    dayPlate = dayPlate,
                                    spanDurationMillis = row.spanDurationMillis,
                                    standingHour = StrongroomFlowModel.hourText(
                                        row.sealedAtWallMillis
                                    ),
                                    fallenHour = StrongroomFlowModel.hourText(
                                        System.currentTimeMillis()
                                    )
                                )
                            }
                        StrongroomFlowModel.platePile(plates)
                    }
                }
                Box(Modifier.weight(1f)) {
                    pile?.let { PlatePileScreen(pile = it) }
                }
                RoomFooter(
                    onDoorway = { place = StrongroomFlowModel.Place.DOORWAY },
                    onDrawer = { place = StrongroomFlowModel.Place.ROOM_DRAWER },
                    onGuard = { place = StrongroomFlowModel.Place.GUARD }
                )
            }

            StrongroomFlowModel.Place.ROOM_DRAWER -> Column(
                Modifier.fillMaxSize().background(PlateDarkRoom)
            ) {
                var drawer by remember {
                    mutableStateOf<LiftingPlate.TaskDrawer?>(null)
                }
                LaunchedEffect(Unit) {
                    drawer = withContext(Dispatchers.IO) {
                        StrongroomFlowModel.taskDrawer(
                            store.drawerPage(0L, System.currentTimeMillis(), 200, 0)
                        )
                    }
                }
                Box(Modifier.weight(1f)) {
                    drawer?.let { StrongroomDrawerScreen(drawer = it) }
                }
                RoomFooter(
                    onDoorway = { place = StrongroomFlowModel.Place.DOORWAY },
                    onDrawer = { place = StrongroomFlowModel.Place.ROOM_PILE },
                    onGuard = { place = StrongroomFlowModel.Place.GUARD }
                )
            }

            StrongroomFlowModel.Place.GUARD -> Column(
                Modifier.fillMaxSize().background(PlateDarkRoom)
            ) {
                var snapshot by remember {
                    mutableStateOf<GuardSurfaceSnapshot?>(null)
                }
                LaunchedEffect(tick, days) {
                    snapshot = withContext(Dispatchers.IO) {
                        val now = System.currentTimeMillis()
                        val guard = store.guardState(now)
                        StrongroomFlowModel.guardSnapshot(
                            guard = guard,
                            nowMillis = now,
                            sealedBlockActive = sealedRow != null,
                            purseSpentToday = guard.purseSpentToday(
                                StrongroomDay.filingDateIso(now)
                            ),
                            crankMinuteSpent = store.crankWeek(now).sealBroken
                        )
                    }
                }
                Box(Modifier.weight(1f)) {
                    snapshot?.let { snap ->
                        GuardSurfaceScreen(
                            snapshot = snap,
                            onIntent = { intent ->
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        val now = System.currentTimeMillis()
                                        when (intent) {
                                            is GuardIntent.Tighten ->
                                                store.tightenGuard(now, intent.newAllowanceMinutes)
                                            is GuardIntent.RequestRaise ->
                                                store.requestGuardRaise(now, intent.newAllowanceMinutes)
                                            GuardIntent.RequestOff ->
                                                store.requestGuardOff(now, sealedRow != null)
                                            GuardIntent.TurnOn -> store.turnGuardOn(now)
                                            is GuardIntent.BeginStretch ->
                                                store.beginGuardStretch(now, intent.endMillis)
                                            is GuardIntent.SetRoutineEnabled -> Unit
                                        }
                                    }
                                    tick++
                                }
                            },
                            onDetentTick = { engine.playRotaryDetent() }
                        )
                    }
                }
                RoomFooter(
                    onDoorway = { place = StrongroomFlowModel.Place.DOORWAY },
                    onDrawer = { place = StrongroomFlowModel.Place.ROOM_PILE },
                    onGuard = { place = StrongroomFlowModel.Place.ROOM_DRAWER }
                )
            }
        }
    }
}

// ----------------------------------------------------------------------
// Places
// ----------------------------------------------------------------------

@Composable
private fun FirstRunPlace(
    store: StrongroomStore,
    dao: com.example.data.MonolithDao,
    todayIso: String,
    tomorrowIso: String,
    onConfirm: () -> Unit
) {
    var carriedNames by remember { mutableStateOf<List<String>>(emptyList()) }
    var carriedHour by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val apps = dao.getAllAppsSnapshot()
            val session = dao.getActiveSessionSnapshot()
            val carry = LegacyCarry.deriveSigning(
                filingDateIso = tomorrowIso,
                blockedApps = apps,
                guardSettings = dao.getGuardSettingsSnapshot(),
                activeSessionIntention = session?.intention,
                guardAllowanceMinutes = store.guardState(System.currentTimeMillis())
                    .allowanceMinutes,
                activeSessionWindowEnd = session?.windowEndMillis
                    ?.takeIf { it > 0L }
                    ?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime()
                    }
            )
            carriedNames = apps
                .filter { it.resolvedTier == com.example.data.AppBlockTier.LOCKED }
                .map { it.appName }
            carriedHour = carry?.liftingTime?.let { StrongroomFlowModel.hourText(it) }
        }
    }
    // UX-SR-5 §1: the run compresses to the doorway alone on an
    // evening install — the carried things are a morning's
    // furniture.
    val evening = LocalTime.now().hour >= 17
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlateDarkRoom)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp)
            .testTag("strongroom_first_run")
    ) {
        Text(text = "ULYSSES", style = RoomHeadingStyle, color = PlateInkGold)
        Spacer(Modifier.height(18.dp))
        if (!evening) {
            Text(
                text = "What your phone already holds, carried into the room:",
                style = RoomListStyle,
                color = PlateInkGold
            )
            Spacer(Modifier.height(10.dp))
            if (carriedNames.isEmpty()) {
                Text(
                    text = "Nothing stands locked yet — the first signing settles it.",
                    style = RoomListStyle,
                    color = PlateInkGold
                )
            } else {
                carriedNames.take(8).forEach { name ->
                    Text(
                        text = name,
                        style = RoomListStyle,
                        color = PlateInkGold,
                        modifier = Modifier.padding(start = 14.dp, top = 3.dp, bottom = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = carriedHour?.let {
                    "Tomorrow stands composed from them, lifting at $it. " +
                        "One confirmation — nothing else is asked in this run."
                } ?: "Tomorrow composes from them at your first signing. " +
                    "One confirmation — nothing else is asked in this run.",
                style = RoomListStyle,
                color = PlateInkGold
            )
            Spacer(Modifier.height(22.dp))
        }
        Text(
            text = "Stand the doorway",
            style = RoomHeadingStyle,
            color = PlateInkGold,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onConfirm
                )
                .padding(vertical = 10.dp)
                .testTag("firstrun_confirm")
        )
    }
}

@Composable
private fun DoorwayPlace(
    todayRow: StrongroomDayEntity?,
    tomorrowRow: StrongroomDayEntity?,
    sealedStanding: Boolean,
    onSigning: () -> Unit,
    onSwing: () -> Unit,
    onPile: () -> Unit,
    onDrawer: () -> Unit,
    onGuard: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlateDarkRoom)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp)
            .testTag("strongroom_doorway")
    ) {
        Text(text = "THE DOORWAY", style = SigningHeadStyle, color = InkGold)
        Spacer(Modifier.height(14.dp))
        Text(
            text = when {
                sealedStanding -> "The door stands sealed."
                todayRow == null ->
                    "No day stands composed for today yet."
                else -> "Today stands ${todayRow.state.lowercase()}, " +
                    "lifting at ${
                        StrongroomFlowModel.hourText(
                            LocalTime.of(todayRow.liftingHour, todayRow.liftingMinute)
                        )
                    }."
            },
            style = SigningQuietStyle,
            color = InkGold.copy(alpha = 0.85f)
        )
        Text(
            text = tomorrowRow?.let {
                "Tomorrow stands ${it.state.lowercase()} — the signing amends it."
            } ?: "Tomorrow is not composed yet — the signing composes it.",
            style = SigningQuietStyle,
            color = InkGold.copy(alpha = 0.85f)
        )
        Spacer(Modifier.height(26.dp))
        HostRow("The signing", "doorway_signing", onSigning)
        if (!sealedStanding &&
            (todayRow == null || todayRow.state == StrongroomDayEntity.STATE_COMPOSED ||
                todayRow.state == StrongroomDayEntity.STATE_SIGNED)
        ) {
            HostRow("Seal the day", "doorway_swing", onSwing)
        }
        HostRow("The plates", "doorway_pile", onPile)
        HostRow("The drawer", "doorway_drawer", onDrawer)
        HostRow("The guard", "doorway_guard", onGuard)
        Spacer(Modifier.weight(1f))
        HostRow("Leave the strongroom", "doorway_leave", onExit)
    }
}

@Composable
private fun SigningPlace(
    store: StrongroomStore,
    dao: com.example.data.MonolithDao,
    tomorrowIso: String,
    tomorrowRow: StrongroomDayEntity?,
    allApps: List<BlockedAppEntity>,
    onLeave: () -> Unit,
    onSigned: (SigningResult) -> Unit
) {
    var draft by remember(tomorrowIso) { mutableStateOf<CompositionDraft?>(null) }
    LaunchedEffect(tomorrowIso, tomorrowRow) {
        draft = withContext(Dispatchers.IO) {
            val order = standingOrder(store, dao, tomorrowIso, allApps)
            val composed = tomorrowRow?.toSigningRecord()
                ?: order?.let { store.composeDay(it, tomorrowIso).record }
            val options = allApps.map { SigningAppOption(it.packageName, it.appName) }
            if (composed != null && options.isNotEmpty()) {
                StrongroomFlowModel.signingDraft(
                    dayShapeName = tomorrowRow?.dayShape ?: "ORDINARY",
                    appOptions = options,
                    composedLocked = composed.lockedPackages,
                    liftingTime = composed.liftingTime,
                    sentence = composed.sentence,
                    breakLengthMinutes = composed.breakLengthMinutes,
                    spentDay = store.guardState(System.currentTimeMillis())
                        .purseSpentToday(tomorrowIso)
                )
            } else {
                null
            }
        }
    }
    val standing = draft
    if (standing != null) {
        SigningRoute(draft = standing, onLeave = onLeave, onSigned = onSigned)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PlateDarkRoom)
                .padding(horizontal = 24.dp, vertical = 28.dp)
                .testTag("signing_no_catalogue")
        ) {
            Text(
                text = "No apps stand in the catalogue yet — the signing " +
                    "waits for the catalogue's first sync.",
                style = RoomListStyle,
                color = PlateInkGold
            )
            Spacer(Modifier.height(18.dp))
            HostRow("Back to the doorway", "signing_back", onLeave)
        }
    }
}

@Composable
private fun AntechamberPlace(
    store: StrongroomStore,
    row: StrongroomDayEntity,
    signing: StrongroomDay.SigningRecord,
    allApps: List<BlockedAppEntity>,
    engine: TactileSoundEngine,
    onLeave: () -> Unit
) {
    val context = LocalContext.current
    val nameByPackage = remember(allApps) { allApps.associate { it.packageName to it.appName } }
    val packageByName = remember(allApps) { allApps.associate { it.appName to it.packageName } }
    val initial = remember(row.filingDateIso, row.allowanceSpentMinutes, allApps) {
        StrongroomFlowModel.antechamberState(
            signing = signing,
            purseRemainingMinutes = row.allowanceTotalMinutes - row.allowanceSpentMinutes,
            nameOf = { pkg -> nameByPackage[pkg] ?: pkg },
            cataloguePackages = allApps.map { it.packageName }
        )
    }
    AntechamberScreen(
        initialState = initial,
        readOnlyLines = StrongroomFlowModel.antechamberReadOnlyLines(signing),
        onCrankOutcome = { outcome ->
            when (outcome) {
                CrankOutcome.TURNED -> engine.playSeatKnock(16, 0.85f)
                CrankOutcome.HARD_STOP_SPENT -> engine.playSeatKnock(24, 1.0f)
                CrankOutcome.ONE_ALREADY_OPEN -> engine.playFirmClick()
                else -> Unit
            }
        },
        onSignedOpen = { name ->
            engine.playMechanicalLatch(engaged = true)
            packageByName[name]?.let { pkg ->
                context.packageManager.getLaunchIntentForPackage(pkg)?.let { intent ->
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    runCatching { context.startActivity(intent) }
                }
            }
        },
        onShowingOutcome = { outcome ->
            when (outcome) {
                ShowingOutcome.SHOWN -> engine.playMechanicalLatch(engaged = true)
                ShowingOutcome.CANNOT_SPEND -> engine.playSeatKnock(24, 1.0f)
                else -> Unit
            }
        },
        onTakeCall = { engine.playMechanicalLatch(engaged = true) },
        onReturned = { engine.playMechanicalLatch(engaged = false) },
        onLeave = onLeave
    )
}

@Composable
private fun LiftingPlace(
    store: StrongroomStore,
    row: StrongroomDayEntity,
    engine: TactileSoundEngine,
    onPresented: () -> Unit
) {
    val context = LocalContext.current
    var plate by remember(row.filingDateIso) {
        mutableStateOf<LiftingPlate.StruckPlate?>(null)
    }
    var settled by remember(row.filingDateIso) { mutableStateOf(false) }
    val failure = row.state != StrongroomDay.SealState.LIFTED.name
    // The failureFace supply (BUILD-SR-23, step 7 — the act
    // BUILD-SR-14 named as P9's composition act): on a fallen day
    // the lifting presents P3's seal-gone form, struck from the
    // day's own recorded facts — the signing's lifting hour and
    // sentence carried unchanged, the bracket's standing hour =
    // the seal's striking wall hour, its fallen hour = the hour
    // of this presenting check (the same two recorded instants
    // the plate supply below stands on; bounds from recorded
    // facts, never a check log).
    val failureFace = if (failure) {
        val signing = row.toSigningRecord()
        SealedFaceState.sealGone(
            liftingHour = StrongroomFlowModel.hourText(signing.liftingTime),
            sentence = signing.sentenceOrKeptPlace,
            standingHour = StrongroomFlowModel.hourText(row.sealedAtWallMillis),
            fallenHour = StrongroomFlowModel.hourText(System.currentTimeMillis())
        )
    } else null
    LaunchedEffect(row.filingDateIso) {
        // R-7 DISCHARGED (BUILD-SR-23, step 7): the host's
        // SessionNotification.cancel is STRUCK — the bridge's
        // onFiringWake cancels at the lifting (PR #856), the
        // surviving home (E-4 stands: the one line's life ends at
        // the lifting, fired by the bridge, not the host).
        plate = withContext(Dispatchers.IO) {
            val dayPlate = store.strikePlate(
                row.filingDateIso,
                SystemClock.elapsedRealtime(),
                StrongroomEngineBridge.bootCount(context.applicationContext),
                sealFailed = row.state == StrongroomDay.SealState.BROKEN.name
            ) ?: return@withContext null
            StrongroomFlowModel.struckPlate(
                dayPlate = dayPlate,
                spanDurationMillis = row.spanDurationMillis,
                standingHour = StrongroomFlowModel.hourText(row.sealedAtWallMillis),
                fallenHour = StrongroomFlowModel.hourText(System.currentTimeMillis())
            )
        }
    }
    Column(Modifier.fillMaxSize().background(PlateDarkRoom)) {
        Box(Modifier.weight(1f)) {
            LiftingScreen(
                plate = plate,
                handingBack = LiftingPlate.HandingBack.prepare(emptyList()),
                failure = failure,
                failureFace = failureFace,
                onHandedBack = {
                    // The handing-back precondition stands; its
                    // former SessionNotification.cancel arm is
                    // STRUCK under R-7 (the bridge's cancel at the
                    // lifting is the surviving home).
                },
                onBoltWithdrawn = { index ->
                    StrongroomLift.BOLT_VOICES.getOrNull(index)?.let { voice ->
                        engine.playSeatKnock(
                            durationMs = StrongroomLift.BOLT_DURATION_MS,
                            amplitude01 = voice.gain,
                            pitchHz = voice.pitchHz
                        )
                    }
                },
                onDoorSettled = {
                    settled = true
                    engine.playSeatKnock(32, StrongroomLift.DOOR_GAIN, 160f)
                },
                onPlateRead = { settled = true }
            )
        }
        if (failure || settled) {
            HostRow("The room", "lifting_to_room", onPresented)
        }
    }
}

// ----------------------------------------------------------------------
// Host chrome — plain rows in the room's hand (host copy; Slice 3
// owns final copy — the GuardCopy precedent)
// ----------------------------------------------------------------------

@Composable
private fun HostRow(text: String, tag: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = RoomListStyle,
        color = PlateInkGold,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp)
            .testTag(tag)
    )
}

@Composable
private fun RoomFooter(
    onDoorway: () -> Unit,
    onDrawer: () -> Unit,
    onGuard: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PlateDarkRoom)
            .padding(horizontal = 24.dp, vertical = 6.dp)
    ) {
        Box(Modifier.weight(1f)) { HostRow("The doorway", "room_to_doorway", onDoorway) }
        Box(Modifier.weight(1f)) { HostRow("Plates / drawer", "room_to_other", onDrawer) }
        Box(Modifier.weight(1f)) { HostRow("The guard", "room_to_guard", onGuard) }
    }
}

// ----------------------------------------------------------------------
// The standing order — born from his data or his act, never invented
// ----------------------------------------------------------------------

/**
 * The week's standing order, sourced in the only two lawful
 * ways: (1) a standing day's own record (his composed/signed
 * facts, carried forward whole), or (2) the LegacyCarry from
 * his current app's data — which itself refuses to invent a
 * lifting hour (CRITIC-SR-6's cure) and returns null without
 * one. When neither stands, there is NO order: compositions do
 * not happen until his first signing creates the day, and that
 * signing's result becomes the order (see [orderFromSigning]).
 */
private suspend fun standingOrder(
    store: StrongroomStore,
    dao: com.example.data.MonolithDao,
    anyDateIso: String,
    allApps: List<BlockedAppEntity>
): StrongroomComposition.WeekStandingOrder? {
    val standing = dao.getStrongroomDaysFlow().first()
        .maxByOrNull { it.filingDateIso }
        ?.toSigningRecord()
    val seed = standing ?: run {
        val session = dao.getActiveSessionSnapshot()
        LegacyCarry.deriveSigning(
            filingDateIso = anyDateIso,
            blockedApps = dao.getAllAppsSnapshot(),
            guardSettings = dao.getGuardSettingsSnapshot(),
            activeSessionIntention = session?.intention,
            guardAllowanceMinutes = store.guardState(System.currentTimeMillis())
                .allowanceMinutes,
            activeSessionWindowEnd = session?.windowEndMillis
                ?.takeIf { it > 0L }
                ?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime()
                }
        )
    } ?: return null
    return weekOrder(seed)
}

/** An order whose every weekday stands ORDINARY under one
 *  template — the seed week's honest first form (UX-SR-5's
 *  seed week); shape authoring is the week's own later act. */
private fun weekOrder(
    seed: StrongroomDay.SigningRecord
): StrongroomComposition.WeekStandingOrder {
    val template = StrongroomComposition.DayTemplate(
        lockedPackages = seed.lockedPackages,
        allowedPackages = seed.allowedPackages,
        liftingTime = seed.liftingTime,
        breakLengthMinutes = seed.breakLengthMinutes,
        breakFiresAtOffsetMillis = seed.breakFiresAtOffsetMillis,
        allowanceMinutes = seed.allowanceMinutes,
        gaps = seed.gaps,
        taskLine = seed.taskLine
    )
    return StrongroomComposition.WeekStandingOrder(
        shapeByWeekday = DayOfWeek.values().associateWith {
            StrongroomComposition.DayShape.ORDINARY
        },
        templateByShape = mapOf(StrongroomComposition.DayShape.ORDINARY to template)
    )
}

/** The order born from his signing act itself: the result's
 *  sets and hour, the standing day's break offset and gaps
 *  carried, the allowance as the Guard stands. */
private suspend fun orderFromSigning(
    result: SigningResult,
    tomorrowRow: StrongroomDayEntity?,
    allApps: List<BlockedAppEntity>,
    store: StrongroomStore,
    nowWall: Long
): StrongroomComposition.WeekStandingOrder {
    val standing = tomorrowRow?.toSigningRecord()
    val seed = StrongroomDay.SigningRecord(
        filingDateIso = tomorrowRow?.filingDateIso ?: "",
        lockedPackages = result.lockedPackages,
        allowedPackages = standing?.allowedPackages
            ?: allApps.filter {
                it.resolvedTier != com.example.data.AppBlockTier.LOCKED
            }.map { it.packageName },
        liftingTime = LocalTime.parse(result.liftingHour),
        sentence = result.sentence,
        breakLengthMinutes = result.breakMinutes,
        breakFiresAtOffsetMillis = standing?.breakFiresAtOffsetMillis ?: -1L,
        allowanceMinutes = store.guardState(nowWall).allowanceMinutes,
        gaps = standing?.gaps ?: emptyList(),
        taskLine = result.taskLine
    )
    return weekOrder(seed)
}
