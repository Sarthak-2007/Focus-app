# ARM AUDIT, PART 1 — THE COLD OPEN
**Prima — Critics, first-3-seconds lens · Coordinator: Hale · Plan wave (no code)**

Question this table answers: *of the 10-second ARM budget, how much is spent
before his thumb ever reaches the slide?* — and what the Pre-Flight path costs
when it fires.

Method: code trace only. Seconds marked **(code)** are constants in the source.
Seconds marked **(est.)** are honest estimates for OS / Compose / human time —
no device trace exists in this repo, and none is invented here. His morning for
scale: up 03:30, fasted study block 05:00–08:00, ~10 × 50-min blocks.

**Short answer up front:** gesture-wise the cold open is nearly clean — one
launcher tap, and the slide is pinned on the first screen, no scrolling. Time-wise
the *painted* slide arrives in an estimated ~0.9–2.5 s. But the painted slide is
lying (placeholder values, see steps 7–8), the truth behind it is queued behind a
full installed-apps scan that runs up to **three times** before the first arm
(steps 3, 5, 14), and the one permission the seal depends on is only checked
**after** the slide is spent (steps 10–13).

---

## Table A — Launch → Deck ready for the first gesture

| Step | What happens (file evidence) | Gestures | Seconds | Fix the plan must carry |
|---|---|---|---|---|
| 1. Launcher tap → process start | `MainActivity` is the single LAUNCHER activity, `launchMode="singleTop"`, theme `Theme.MyApplication` = `android:Theme.DeviceDefault.NoActionBar` (AndroidManifest.xml; res/values/themes.xml). **No splash screen, no starting-window branding** — the first thing he sees is the OS default window, then Compose. | 1 (the tap) | 0.3–1.0 (est., OS cold process) | Plan a real starting window in the app's own surface colour so second 0–1 is Monolith, not Android default. Nothing may animate or "intro" here — a still, correct-colour frame only. |
| 2. `MainActivity.onCreate` — pre-Compose work | `MainActivity.kt:72–83`: `enableEdgeToEdge(...)`, `MonolithDatabase.getInstance(applicationContext)` (Room builder only — DB file opens lazily on first query, MonolithDatabase.kt), `MonolithRepository(...)` construction, then **`TactileSoundEngine(applicationContext)` constructed before `setContent`**. Sound engine `init` (TactileSoundEngine.kt:74–80) immediately spawns a `Thread.MAX_PRIORITY` synth thread that builds an `AudioTrack` and calls `track.play()` — audio hardware work competing with the first frame for CPU, before a single pixel exists. | 0 | 0.05–0.3 (est.; synth-thread/AudioTrack cost unmeasured, unbounded in code) | Sound engine must be **lazy**: construct on first feel event, not in `onCreate`. Nothing that cannot change the first frame may be built before it. |
| 3. ViewModel init, coroutine 1 — the scan (the heavy one) | `MonolithViewModel.kt` `init`: `viewModelScope.launch { repository.ensureSeededAndSyncPackages(); RoutineScheduler.scheduleAll(...); … refreshSystemGuard(); compute7DayHistory() }`. `ensureSeededAndSyncPackages()` (MonolithRepository.kt:74 ff., `Dispatchers.IO`, behind a `Mutex`) does, in order: snapshot reads for sites / guard settings / routines / apps; **on first run seeds** default sites, guard settings, 2 routines, 12-app default catalog, 4 profiles, **3 fabricated past sessions, 2 intercept logs, 2 captured notifications, 4 tasks**; on *every* run: `pm.queryIntentActivities(ACTION_MAIN + CATEGORY_LAUNCHER)` over **every launcher app on the phone**, `loadLabel(pm)` per app, `queryRealTodayUsageMinutes()`, per-app usage updates, a second full apps snapshot; on later runs additionally full task + session **dedup scans that delete rows**. It does not block the first frame (IO dispatcher) — it blocks the *truth*: every Deck value that comes from Room waits behind this. | 0 | No code bound. Est. 0.3–2.0+ on a populated phone; first-ever run (all seeding writes) higher. Mutex-serialised, so repeats queue (see steps 5, 14) | **Split the function in the plan:** (a) `ensureSeeded` — cheap, idempotent, the only part allowed anywhere near startup; (b) `syncPackages` — deferred until *after* the first seal or to a once-a-day background moment, never in init. Package scan results must be cached, not re-derived per launch. Fabricated seed history (fake sessions/logs/notifications presented as his) is a TELL-THE-TRUTH violation on day one — seed *empty*, or seed visibly-labelled samples that never enter his totals. |
| 4. ViewModel init, coroutine 2 + ticker — the dial moves by itself | `init` also launches `activeTask.collect { … _intentionText.value = task.title; _dialMinutes.value = task.estimatedMinutes.coerceIn(5,180) }` and `startTickerLoop()` (1 s loop from birth). Consequence: when the task flow first emits — i.e. after step 3's DB work — **the duration and intention rewrite themselves under his thumb**. A slide begun at the painted 50 min can be re-priced to the active task's minutes mid-gesture. | 0 | Fires whenever step 3 finishes — inside or outside the 3-second window, unpredictably | First frame must be the *only* frame: Deck may not paint an actionable slide until dial / intention / profile have emitted real values once (a sub-second truthful "not ready yet" state beats a ready-looking lie), and once painted, values may never change except by his hand. |
| 5. `refreshSystemGuard()` inside init — the scan runs **again** | Same init coroutine calls `refreshSystemGuard()` (MonolithViewModel.kt), whose body is `viewModelScope.launch { repository.ensureSeededAndSyncPackages(); _systemGuardStatus.value = …; compute7DayHistory() }`. So the step-3 scan is invoked a **second time** in the same cold start, queueing on the same `seedMutex`. The guard *status* itself (four Settings reads) is cheap; it is welded to the expensive scan for no reason. | 0 | Adds a full second pass of step 3's est. cost behind the mutex | `refreshSystemGuard` must read status only. Any function named "refresh status" that secretly re-runs seeding + a whole-phone package scan is banned by the plan. |
| 6. `MonolithRootApp` first composition — everything, all at once | `MainActivity.kt`: `setContent` collects 2 flows for theme; `MonolithRootApp` then collects **43 more** `collectAsStateWithLifecycle()` values (counted) fed by **17 `stateIn(WhileSubscribed(5000))` flows** in the ViewModel — tasks, profiles, apps, sessions, history, logs, notifications, stats, backup payload, artifacts — whether or not the Deck renders them. Root also composes, on frame one: `Scaffold`, `MonolithPullDownHeader`, collapsed `UnifiedStatusGuardDrawer`, `AnimatedContent` viewport, an 80 dp `AndroidView` bottom nav (`MonolithBottomNavView`), and **five overlays** (desk clock, notification digest, streak tower, Pre-Flight modal, airlock, disarm — each an `AnimatedVisibility` wrapper, content correctly not composed while invisible). | 0 | Compose cost est. 0.2–0.6; no code-fixed delay | Plan a Deck-first composition budget: the Deck's first frame subscribes to Deck state only; Vault/Shield/Guard state subscribes when its destination is first opened. 45 lifecycle collectors to show one wheel and one slide is machinery tax on every cold open. |
| 7. Destination transition machinery | Start destination is correct and free: `_currentDestination = MutableStateFlow(MonolithDestination.DECK)` — he lands on the Deck, no routing gesture, no onboarding. **Good.** But the viewport is `AnimatedContent` with `fadeIn(specB()) togetherWith fadeOut(specA())` (MainActivity.kt) — specB = **420 ms**, specA = **260 ms** (MonolithCurves.kt). Every return to the Deck from any tab pays up to 420 ms of fade before the slide is touchable-looking again. | 0 | 0 first launch; **0.42 (code)** on every tab return | The Deck — the arm surface — must have a **zero-duration return**. Fades are decoration; the arm path is exempt from decoration. Other tabs may keep their curves. |
| 8. `TactileDeckScreen` first composition — painted ≠ true | TactileDeckScreen.kt: scroll column (instrument panel: 84 sp readout, `RotaryWheelView` `AndroidView` at 240 dp, FLOW key, finish time; task line) with the **`SlideToStartView` pinned below the scroll** (68 dp) — the slide is on screen in frame one, reachable with **zero scrolling. Good, keep this.** But the painted values are pre-emission defaults: `_dialMinutes = 50` while `_studyPreset = POMODORO_25_5` (**25 min** — dial and preset disagree on the same screen; caption reads "100 MIN TOTAL" from 50 × 2 blocks); `_intentionText = "Finish Chapter 4 Problem Set"` (hardcoded); checklist falls back to 3 hardcoded items; `totalFocusedMinutes` initialises at **75** and `totalInterceptions` at **6** (ViewModel `stateIn` initial values); apps initialise `emptyList()` → the instrument honestly-but-falsely reads **"SHUT 0 APPS"** until the flow lands. | 0 | Painted slide: est. cumulative **~0.9–2.5 s** from tap (steps 1+2+6+8 estimates + ≤0.42 code anim). **Truth-ready slide: unbounded in code** — whenever steps 3–5 finish; no ready signal, no timeout, no loading state exists | This is the first-3-seconds core defect, and the plan must name it: **a slide that is painted before it is true is not ready.** Either (a) one honest loading state for the sub-second truth window, slide inert, or (b) persist last-known Deck state (dial, profile, blocked count, intention) and paint *that* instantly — yesterday's truth beats today's placeholder. Reconcile dial/preset to a single source of truth so the first frame cannot contradict itself. |

**Table A totals — before his thumb reaches the slide:**
- **Gestures: 1** (launcher tap). Zero scrolls, zero taps inside the app. This part passes.
- **Seconds: ~0.9–2.5 s (est.) to a painted slide; 0.00–0.42 s of it code-fixed animation.**
  To a *truthful* slide: **not bounded by anything in the code** — gated on a
  whole-phone package scan that init runs, `refreshSystemGuard` re-runs, and
  ON_RESUME runs a third time (step 14). On a bad morning the majority of the
  10 s budget can be spent looking ready while not being ready.

---

## Table B — The Pre-Flight path (`requestArmWithPreFlightCheck`)

Entry point: `SlideToStartView.onTouchEvent` — drag to `dragFraction >= 0.82`,
release → view plays `playHarmonicSeal(true)` itself and invokes
`onSlideCommitted` → `viewModel::requestArmWithPreFlightCheck`
(MainActivity.kt wiring; BlockitCoreViews.kt:764–780).

| Step | What happens (file evidence) | Gestures | Seconds | Fix the plan must carry |
|---|---|---|---|---|
| 9. The check itself — every arm, cheap | `MonolithViewModel.requestArmWithPreFlightCheck()`: `if (!repository.isAccessibilityEnabled()) { playMechanicalLatch; _preFlightCheckVisible = true } else armMonolithSession()`. `isAccessibilityEnabled()` = one `Settings.Secure.getString(ENABLED_ACCESS
...[truncated 8439 chars]