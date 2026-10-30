# Day8 — Critics, retention — ARM audit part 3: the repetition tax and the routine path

Scope read in code, not in summaries: `ui/MonolithViewModel.kt`, `data/MonolithRepository.kt`,
`data/MonolithModels.kt`, `data/MonolithDao.kt`, `service/RoutineAlarmReceiver.kt`,
`service/RoutineScheduler.kt`, `service/MonolithBootReceiver.kt`, `ui/screens/TactileDeckScreen.kt`,
`ui/screens/ShieldMatrixScreen.kt`, `ui/screens/CurioVaultScreen.kt`, `MainActivity.kt`.
Standards applied: ARM ≤10s · HOLD absolute · TELL THE TRUTH once. Settled law applied: routines are
his (create/rename/delete/pause), reseeded to his day (05:00 fasted block daily, sleep 20:00–03:30),
no ghost records, hold banked.

Method for the tax: a "cold start" = ViewModel recreated (process death, swipe-kill, reboot, Android
reclaiming the app during a long sealed session). Everything in §1 lives in bare
`MutableStateFlow`s in the ViewModel. There is **no** SharedPreferences, DataStore, SavedStateHandle,
or Room write for any of it — grep-verified. Within one process lifetime the values survive between
arms; the moment the process dies, every one of them reverts to the hardcoded default below.

---

## (a) Resting-state tax — what the Deck rests at, and what re-specifying costs

The Deck's resting values, in declaration order (`MonolithViewModel.kt`):

- `_dialMinutes = 50`
- `_studyBlocksCount = 2`
- `_studyPreset = StudyModePreset.POMODORO_25_5` — i.e. **25 min / 2 blocks**, contradicting the
  dial's own 50 one line above. The state is internally inconsistent before he touches anything.
- `_intentionText = "Finish Chapter 4 Problem Set"` — seed fiction from someone else's coursework.
- `_selectedProfileName = "Study Loadout"`
- `_shortsReelsSniperEnabled = true` (memory-only; the matching browser-blocker flag *is* persisted
  via GuardSettings — proof persistence was possible and simply not done for the rest)
- `_weeklyEmergencyTokensRemaining = 1` (memory-only — see integrity row below)
- `_darkTheme = false`

Then `init{}` starts a collector on `activeTask` that **overwrites two of these the moment the task
queue emits**: `_intentionText = task.title` and, if no session is active,
`_dialMinutes = task.estimatedMinutes.coerceIn(5,180)`. The seeded ACTIVE task (and every task the
app itself creates by default — `addTaskToQueue` default 25, `addChecklistItem` hardcodes 25)
carries `estimatedMinutes = 25`. So in steady state the dial does not rest at 50 *or* at his last
setting: it rests at **25, imposed by the queue head**, and it is re-imposed on *every task advance*
(`completeTaskAndAdvance`, `promoteTaskToActive`, `editTask`, `undoDeleteTask` all feed the same
collector). The repetition tax is therefore not a once-a-day cold-start tax only — the app re-taxes
the dial mid-day, arm after arm, whenever the queue moves.

Two structural facts before the table:

1. **The Deck renders no intention field and no profile picker at all.** `TactileDeckScreen`
   receives `intentionText`, `selectedProfileName`, `profiles`, `onIntentionChanged`,
   `onProfileSelected` as parameters and never draws any of them. Whatever the ViewModel holds is
   stamped onto the session record; from the Deck he can neither see it nor change it. A wrong
   intention is not a few seconds of typing — it is a detour to the Tasks tab and a queue promotion,
   or a false record accepted silently.
2. **His unit of work is one 50-minute block, ~10 separate arms a day.** The resting
   `studyBlocksCount = 2` means one slide arms *two* blocks back-to-back with an automatic
   10-minute Safe Break between them (`updateSessionProgress` inserts `SAFE_BREAK_DURATION_SECONDS`
   and auto-starts block 2). The preset that looks like his — `DEEP_50_10` — sets blocks to **3**.
   There is no resting configuration of this Deck that equals "50 × 1".

| Item | Resting value | What his day needs | Cost per arm | Cost per year |
|---|---|---|---|---|
| Dial minutes | 50 on a bare cold start; **25 in practice**, overwritten by the active task's estimate on every queue emission/advance (app's own task default is 25 everywhere) | 50 | Wheel spin 25→50 + readback verify ≈ **3 s** on most arms (0 s only on the rare bare cold start with no queue) | ≈ 3 s × ~1,800 arms ≈ **1.5 h** of pure wheel-turning |
| Blocks count | **2**, never persisted; cycle-only control on the wheel hub (2→3→4→1) | **1** — each arm is a single 50-min block | **3 hub taps** to cycle 2→3→4→1 + verify the total readout ≈ **3 s** | ≈ **1.5 h** in taps; plus the failure mode below |
| Session total (dial × blocks, as displayed) | 25 × 2 = **"50 MIN TOTAL"** — the one number that *looks* right while being two 25s and a break | 50 min, one block, no break | A verification read he must do *against* a display designed to reassure him ≈ 1–2 s, and it must be done *after* fixing dial (fixing dial first re-displays 100 MIN TOTAL until blocks are also fixed — the two fixes are order-coupled) | That coupled check is part of every arm's ~8 s below |
| Intention text | `"Finish Chapter 4 Problem Set"`, then auto-overwritten by queue-head title; **no editor exists on the Deck** | The chapter he is actually opening | 0 s if the queue head happens to be the next chapter; **20–40 s detour** (Tasks tab → promote/edit → back) if it isn't — or 0 s paid and a false intention stamped on the record | Best case 0; any day the queue and the morning disagree, ~10 detours ≈ 3–7 min/day |
| Profile / loadout label | `"Study Loadout"`, memory-only, **no picker on the Deck**; Shield's loadout knob likewise rests at index 0 (`remember`, unsynced to the persisted app tiers it claims to show) | The loadout he chose last time, named truthfully on the record | 0 s if he accepts the default label; otherwise a Shield detour. The *record* cost is permanent: every cold-start arm is filed under a profile name he may not have chosen | Every cold-started session in ~1,800 carries a possibly-wrong profile stamp |
| Study preset | `POMODORO_25_5` (25/2) while dial says 50 — self-contradictory rest state | `DEEP_50_10` shape but with 1 block — a preset that does not exist | Selecting any preset makes it *worse* (25/2, 50/3, 90/4); the only correct path is ignoring presets entirely and hand-fixing dial + blocks | Priced into the dial/blocks rows; the preset row itself is a trap, not a shortcut |
| Shorts/Reels sniper | `true`, memory-only, resets every cold start | `true` (under Study) | 0 s — correct **by luck**, not by memory. The first day he turns it off for a reason, every cold start silently turns it back on | 0 s; one silent rule-change per cold start |
| Dark theme | `false`, memory-only | His last choice | One header toggle ≈ 1 s per cold start | ~1 cold start/day × 180 days ≈ 3 min — trivial in seconds, corrosive in feel: the app greets him as a stranger daily |
| Weekly emergency hold | **Refills to 1 on every cold start** (`_weeklyEmergencyTokensRemaining = 1`, never persisted) | Banked: one hold per *week*, spent holds stay spent | **Negative cost — a forgery, not a tax.** Restarting the app mints a fresh weekly break. HOLD's scarcity, the entire mechanism, is voidable by swipe-killing the app | Up to 7 holds/week instead of 1, every week he cares to take them |

**Per-arm total (typical arm, queue-driven day):** dial ~3 s + blocks ~3 s + coupled total-readout
verify ~2 s ≈ **8 s of re-specification before the slide**, against an ARM budget of 10 s *total*.
The slide, the pre-flight accessibility check (`requestArmWithPreFlightCheck`), and any intention
detour are still unpaid. **The resting state alone spends ~80% of the ARM standard on most arms,
and over 100% on any arm where the queue head isn't his next chapter.**

**Per-year total:** ~8 s × ~1,800 arms ≈ **14,400 s ≈ 4.0 hours** of re-specification — roughly
five 50-minute blocks of study, spent re-telling the app what it was told yesterday. Best case
(process survives all day, queue head always correct, blocks fixed once): ~1 h. The 4 h figure is
the honest planning number because the dial tax is re-imposed by the task collector, not only by
cold starts.

**The failure mode is worse than the tax.** If he pays the dial but forgets the blocks (the display
already told him "50 MIN TOTAL" once today — it lies in the reassuring direction), the session runs
block 1, parks him in a 10-minute Safe Break he didn't schedule, then seals block 2 on its own. His
day has hard edges the app doesn't know — gym at 08:00, meals squeezed, sleep at 20:00. At even a
1-in-20 forget rate that's ~90 overruns a year, each either colliding with the next commitment or
burned out of with his *one* weekly hold — of which there are only 52. The tax's real price isn't
4 hours; it's that the forget is priced in a currency (the banked hold) the app elsewhere pretends
is scarce while secretly refilling it on restart. Both directions are lies about the same token.

**Plan fix (one line per item):** persist dial + blocks + intention + profile + sniper + theme in
Room/DataStore and restore on init (the browser-blocker flag already proves the pattern in this
codebase); make the task collector *suggest*, never overwrite, a dial the user has touched; rest at
50 × 1 — his block — not 25 × 2; render the intention on the Deck as the queue head's title with no
seed fiction behind it; persist the hold token with its week stamp, banked means banked.

---

## (b) Routine truth table — the routine path as it actually is

The path, end to end: `RoutineScheduler` sets two inexact alarms per routine
(`setAndAllowWhileIdle` — Doze may land them minutes late) for the next START and END.
`RoutineAlarmReceiver` on START: if no session is active, it calls
`repository.applySmartLoadout(loadout)` — **rewriting every app's tier globally, with no snapshot
and no restore at window end** — then `repository.startSession(intention = routine.name,
profileName = "Routine: <name>", durationMinutes = window length, studyBlocksCount = 1)` and starts
the shield. **Zero gestures, zero presence check of any kind** — no unlock, no interaction, no
intercept, nothing in the receiver even looks. On END: if the active session's `profileName` matches
`"Routine: <name>"`, it calls `repository.disarmActiveSession(completed = true,
outcomeReason = "Routine Window Ended")` and stops the shield. `disarmActiveSession` banks whatever
elapsed the monotonic clock verifies — but the `completed` flag is **the caller's word, never
checked against the target**. That one line is the mint.

Two collision facts the table relies on: (i) `startSession` begins with
`dao.deactivateAllSessions()` — *any* manual arm silently deactivates whatever session is live,
routine or not, leaving its row `isActive = 0, completedSuccessfully = false`, elapsed frozen at
last tick; (ii) `compute7DayHistory` sums `elapsedSeconds` over **all** sessions that day — ghosts,
orphans, and honest sessions indistinguishable in the total.

| Scenario | What the code does | What the record claims | Verdict | Plan fix |
|---|---|---|---|---|
| He studies at home exactly as the window intends, never touches the phone | START arms the loadout + a single-block session for the full window; shield ticks it; END disarms it | A **completed** session, full window elapsed, intention = the routine's name | **GHOST by construction.** The record is *consistent* with studying and proves nothing — no attendance signal exists anywhere in the path. Even the good day is unevidenced | A routine may *propose*; only his gesture (or a verified interaction inside the first minutes) may *mint*. Unconfirmed windows write nothing |
| He's sick in bed; phone on the desk | Identical fire, identical disarm. SLEEP/STUDY loadout seals the phone of a sick man; reaching anyone costs the 60-s hold | A **completed** focus session for hours he spent ill | **GHOST + HOLD harm.** The lie and the injury are the same event: the seal is real, the study is fictional | Deviation must be free: no auto-arm, ever. A missed window is a blank, not a debt and not a credit |
| The phone is elsewhere — left home, in a bag, powered through the window untouched | Identical mint (alarms are `RTC_WAKEUP`; the shield runs regardless of where the owner is) | **Completed** session | **GHOST, purest form.** The app's finest record-keeping happens in an empty room | As above: no presence, no record. Silence is the only honest entry |
| He's already inside his own manual session at START | Receiver checks `active == null \|\| !active.isActive` and **skips** — no steamroll, no loadout stomp. At END, profile-name mismatch means his session is *not* disarmed | Nothing for the routine; his own session stands or falls on its own | **The one honest path — honest by abstention.** Note the asymmetry it exposes: the protection exists in this direction only | Keep the skip; extend the same respect to the reverse direction (next row) |
| He manually arms *during* a routine session | `startSession` → `deactivateAllSessions()` silently kills the routine session mid-window; the pending END later finds a profile mismatch and does nothing | A frozen, uncompleted routine row with partial elapsed — which `compute7DayHistory` **still counts** toward the day's minutes; his manual session claims the rest. The same wall-clock hour can be double-counted across the two rows | **HALF-GHOST.** Nobody lied on purpose; the ledger still inflates the day | `startSession` must close a displaced session truthfully (bank elapsed, mark displaced) or refuse while one is live — never orphan a row that totals still read |
| He burns his emergency hold to escape a routine session he never started | Disarm works (it is the active session); token decrements — in the in-memory counter that a restart refills anyway | Routine session marked not-completed — **the most truthful routine row in the system**, and it cost his one weekly hold | **TRUTH, bought at HOLD prices.** Escaping an unrequested seal should cost nothing; here it costs the scarcest token in the app | A routine-started session must be dismissible free, labelled "routine dismissed", never against the bank |
| START lands late (Doze) or the phone reboots mid-window | Late START still creates a **full-window-length** session, overrunning the true end; END (separately alarmed) disarms it early — with `completed = true` regardless of the shortfall. Reboot: boot receiver restarts the shield and re-anchors elapsed; END still stamps complete | **Completed**, even if 40 of 240 minutes were actually sealed | **TRUTH violation in the disarm call itself**, independent of attendance: `completed` is asserted, never derived | Derive completion: `completed = verifiedElapsed ≥ target − tolerance`. Anything less banks as a partial, in human words |
| He toggles the routine OFF mid-window | `persistRoutine` → `scheduleRoutine` cancels both pending alarms — **including the END that would have stood the session down**. The session survives on its target duration alone (target == window length, so it *happens* to self-complete near the right time) | Completed at target — right answer, reached by accident of `duration == window` | **FRAGILE.** Any future change that decouples session length from window length turns "off" into "sealed indefinitely until target" | Toggling a routine off must stand down a session it started, in the same transaction as cancelling its alarms |
| Both seeds enabled, an ordinary untouched week (see §3 for the seeds) | Morning 240 min + Night 510 min minted daily, Mon–Fri for Morning, daily for Night | **≈ 750 completed minutes/day; ≈ 87 h in a Mon–Sun week with five Morning windows** — more "focus" than his 12-h target day contains, recorded while he does literally nothing | **The system at rest manufactures a better student than he is.** This is the retention killer: the first time he notices — and he will — every honest number in the app becomes suspect | No ghost records means exactly this row is impossible: routines cue, they never conclude |

---

## 3. The seed routines vs his real day

Seeds are written by `ensureSeededAndSyncPackages()` **only when the routines table is empty**, both
`enabled = true` — they fire from install day, before he has ever opened the Shield tab, and (per §4)
he can never delete them, so "empty table" is the only state in which reseeding is even possible,
and it is unreachable from the UI. Opt-out, not opt-in, twice over.

| | Seed as shipped | His day (settled reseed) | Gap |
|---|---|---|---|
| Morning | **"Morning Deep Work"** — 09:00–13:00, Mon–Fri, STUDY, one 240-min block | Fasted block **05:00–08:00 daily**, then gym 08:00; the day's ~10 blocks are 50-min units | Starts *after* his fasted block and his gym are both over; seals 09:00–13:00 as one mega-block across what should be four separate arms with real breaks; silent on weekends |
| Night | **"Night Wind-Down"** — 22:30–07:00, all days, SLEEP, one 510-min block | Sleep **20:00–03:30**; wake 03:30, bath, meditation, first block 05:00 | Starts 2.5 h after he's asleep (sealing a sleeping man's phone), runs **3.5 h past his waking** — the SLEEP loadout, which locks everything but Phone/Clock/Maps, sits across his bath, meditation and the approach to his 05:00 block. His 05:00 arm then either collides with the live routine session (orphan row, §b) or is skipped in favour of a session whose intention reads "Night Wind-Down" over his fasted study |
| Names | Generic template names from a US coursework app ("Deep Work", "Wind-Down") | His words for his blocks | The record's intentions are written in a stranger's vocabulary |

**Saturday (test day), specifically:** Morning does **not** fire (mask = Mon–Fri, bits 0–4) — the
one mercy in the table, and it is an accident of a weekday default, not a test-day decision. Night
fires **twice around the test**: Friday 22:30 → Saturday 07:00 (started Friday, mask includes
Friday) seals the phone until 07:00 on test morning under the SLEEP loadout; Saturday 22:30 →
Sunday 07:00 fires again that night. If a test morning needs the phone before 07:00 — travel,
admit-card, a parent calling — the price is the 60-second hold from the bank. The settled reseed
(05:00 fasted block daily; sleep 20:00–03:30) fixes every row of this table *as schedule*; it does
not fix §b — a reseeded ghost is still a ghost.

---

## 4. Is there ANY create / rename / delete path for routines in the UI today?

**No. Exhaustively no.** Searched across ViewModel, Repository, DAO, and both routine-bearing
surfaces (Shield is the only one):

- **ViewModel** exposes exactly four routine operations: `toggleRoutine` (pause/resume ✓),
  `cycleRoutineLoadout` (cycles STUDY → WORK_SPRINT → SLEEP — the only three, no custom),
  `adjustRoutineTime` (±30-min steps only; span silently clamped — an adjustment that would take
  the window outside 30 min–12 h is **dropped with no feedback at all**), `toggleRoutineDay`.
  Behind them, a single private `persistRoutine` → `repository.updateRoutine` → `dao.updateRoutine`
  (`@Update`). That is the entire write surface.
- **Repository** has no create/delete for routines; its only insert is the two-row seed inside
  `ensureSeededAndSyncPackages`, guarded by "table is empty".
- **DAO** has `insertRoutines` (seed-only caller) and `updateRoutine` — **no `@Delete`, no
  single-row insert**. Deletion is not merely unexposed; it is unimplemented at every layer.
- **Shield UI** (`RoutineRow`, collapsed-by-default): ON/OFF cap; expanded = START −30/+30,
  END −30/+30, a loadout cycle chip, seven day chips. The routine's name is a plain `Text` —
  no field, no long-press, no menu. There is no "+" anywhere in the ROUTINES section.

Against settled law — *routines fully his (create/rename/delete/pause)* — the score is **1 of 4**:
pause exists; create, rename, and delete do not exist in code at any layer. Practical consequences:
the two seed routines are **permanent residents** — he can silence them, retime them in half-hour
lurches, and cycle their loadout among three presets, but he can never remove "Night Wind-Down"
from his phone, never name a routine in his own words, and never add the 05:00 fasted block as a
third routine beside them. A routine with every day toggled off becomes an inert zombie row —
unfireable, undeletable, still listed — a ghost record of the configuration kind, matching the
session kind in §b. The plan owes him: DAO delete + single insert, ViewModel create/rename/delete
(delete also cancels alarms *and* stands down any session that routine started), and Shield
affordances for all three — name as an editable field on the expanded row, a delete in the same
expanded row, one add row under the list. Small surface; it is the difference between *his* routines
and *tenancy*.

---

## LEDGER — RETENTION ECONOMICS

*(Addendum at Hale's request. Sarthak has reopened the Ledger decision — keep-rebuilt / demote /
kill are all live. This section prices the record itself, fact by fact, from the code that writes
and renders it: `data/MonolithRepository.kt`, `ui/MonolithViewModel.kt`,
`ui/screens/CurioVaultScreen.kt`. YPT's holdings are taken as briefed: hours, groups, and a streak
other people can see — study time, socially witnessed, already his.)*

### 1. What Monolith's record holds that YPT cannot — and what each fact is worth to a man chasing AIR 1

A fact earns its place in a record if a rested version of him, glancing at it, *does something
different* because of it. Priced that way:

| Fact (Monolith-only) | Where it lives / how it's rendered | Glance value | Verdict price |
|---|---|---|---|
| Sessions actually sealed here — start, length, intention, profile | Session rows; Vault shows the last 8 as receipt cards: intention, `+Nm`, profile, `TASKS n`, and the line's payoff position is occupied by **`HMAC•XXXXXXXX`** — a hash stub for an auditor who does not exist (he is user, subject, and auditor in one body) | The seal-events are real and YPT-blind. But the same hours are already counted by YPT with witnesses, and syllabus truth lives in GMC — Monolith's minutes are a *second, private, worse* counting of a fact he already owns twice. He never decides tomorrow from this number | **≈ Zero as rendered.** The one human line ("you sealed 50 on Physics, tasks done") is buried under telemetry grammar; the payoff slot is spent on a hash |
| Deflections — intercepts, per app, with the trigger tag (Habit / Stuck / Tired / Message) and the intention they attacked | Written faithfully (`recordIntercept`, `phantomReachesBlocked`) — then **never rendered**: `CurioVaultScreen` receives `interceptLogs` and uses it for nothing but a single aggregate counter, DEFLECTED | This is the only *behavioural* fact in either system: which app reaches for him, at what moment, in what state. A rested Sunday glance could tighten exactly one tier on Shield — a real decision, decidable from this fact alone | **Real value, unrealised: zero as-built.** The app collects its best fact and files it where no one reads it. Value exists only in a rendering that ends in an action |
| Peeks taken (2-min leases used, cooldowns) | Lease timestamps on the app row; an intercept outcome string "Used 2m Peek" — rendered nowhere | A pattern of peeks at the same hour is a schedule fact; a single peek is trivia. He acts on the pattern maybe twice a year | **Near zero.** Diagnostic garnish, not a decision input |
| Holds spent (emergency disarms, partial stamps) | `disarmActiveSession` writes a "Partial Stamp / Unlatched Early" intercept row; the token counter itself is the in-memory fiction from §a — it refills on restart, so even the *count of holds* is untrue at the source | A true holds ledger would be the record's conscience: what did this week cost to escape? As built it cannot even count | **Zero, shading negative** — the one fact that should audit all the others is itself forged by the refill bug |
| Routine attendance — kept vs ghost | **This fact does not exist.** The receiver (§b) mints `completed = true` with no attendance signal; kept and ghost are the same row | Would be the record's most honest line ("the 05:00 window: you were there 6 of 7") — the only routine fact worth keeping | **Negative.** A field that *claims* attendance and cannot know it is worse than no field: it teaches him, correctly, that this record lies when convenient — and one discovered lie devalues every true row beside it |
| Tasks completed per session | Stamped on each receipt (`TASKS n`) | GMC owns syllabus state with chapter granularity; a per-session task count is a coarser copy | **Zero marginal** |
| The totals themselves (FOCUSED MIN / SESSIONS / TOWER BRICKS) | ViewModel: `totalFocusedMinutes` is **`coerceAtLeast(35)`** with an initial state of **75**; Vault: sessions shown as `size.coerceAtLeast(4)`, bricks as `coerceAtLeast(16)`; on a fresh install the repository **seeds three fabricated completed sessions** (Organic Chemistry, Algorithms, Constitutional Law — 150 fake minutes) so the Vault is never empty | A total with a floor is not a total. Before he has sealed a single minute, the Ledger already congratulates him with a stranger's coursework | **Negative.** The rendering layer falsifies independently of the routine layer — floors, seeds, and ghosts are three separate mints feeding one counter |

Net: of everything Monolith's record uniquely holds, exactly **one** fact (deflections, with their
trigger tags) clears the "he would act on it" bar — and it is the one fact the Vault collects and
does not show. Everything else is either duplicated better elsewhere, unrendered, or untrue.

### 2. Payoff at completion vs a standing tab

A payoff is an **event**: one rendering, at the moment the work ends, in human language, then gone.
A tab is a **place**: it must be worth entering on the days nothing happened, so it fills itself with
things that are always there — counters, a tower, faders, an export button — and the payoff, demoted
to one row among telemetry, decays into a *checking habit*: open, decode, compare against yesterday,
close. Nothing in the Vault is decidable (its only actions are selecting a toy artifact and exporting
an encrypted backup). Under rested-decides / tired-executes, the Vault has the polarity exactly
backwards: it presents *interpretation work* to the tired user at the exact moment the standard
promises him a verdict.

The code states the current settlement plainly: session completion today produces **no payoff
surface at all** — `updateSessionProgress` flips `isActive`, plays the seal sound, the Deck returns
to its (forgetful) rest state. The finish line of a 50-minute block is a text row ending in an HMAC
stub, three tabs away, in a room he must choose to enter. Wave-1's finding that the Vault is the
app's only screenshot-worthy surface cuts *for* the payoff and *against* the tab: what he would
photograph is the moment of completion made beautiful — a trophy is taken at the finish, not stored
in a room he has to keep visiting. **A record surface does not earn a tab by being the payoff; being
the payoff is precisely what disqualifies it from needing one.** One true sentence on the Deck at
disarm — sealed time, intention kept, holds untouched — rendered once, beautifully, is worth more
retention than the entire standing room, because he receives it 1,800 times without ever deciding
to go and look.

### 3. Day-100 test: the worst week — four zero days

What the Ledger shows him today on that week, in order of discovery:

1. **It probably doesn't show four zero days at all.** With the two seed routines enabled, the week
   is pre-filled with ~750 ghost minutes a day (§b). His worst week renders as a *strong* week.
   Absence isn't softened here; it is **abolished on paper** — the cruellest version, because the
   man who lived those four zeros is told by his own tool that they didn't happen.
2. If the ghosts were gone: `DayFadersView` clamps every day's tube to a **minimum 12% fill**
   (`coerceIn(0.12f, 1f)`) and prints `0m` in calm mono beneath it. A zero drawn at 12% is a lie of
   exactly the size that lets him not-quite-see it — calm as anaesthesia. Plus the floors from §1:
   the week sits under a lifetime total that can never fall below its mints.
3. Nothing in the surface *says* anything. Four holes in a fader bank, no sentence, no distinction
   between "sick", "travelled", and "didn't". The data model cannot tell these apart either —
   there is no absence record, only missing rows.

Is there an honest rendering that helps rather than accuses? Yes, but its properties are strict:
**blank, not zero** (no tube, no numeral — the day keeps its shape and stays empty, the way a
missed page in a diary is empty rather than stamped 0); **one sentence, once** ("Four days sealed
nothing. Nothing is owed." — a fact, no arithmetic of debt, no streak-death theatre); and crucially
**delivered at a moment, not kept on a wall** — spoken at the next arm or the Sunday reckoning,
then not re-encounterable at 1 AM on a checking impulse. A standing tab fails this test structurally:
a surface that must fill itself every day will, on the empty days, either accuse (streak broken,
red zeros — the shame loop that makes droppers delete apps) or anaesthetise (12% tubes, floors,
ghosts — this app). Both are retention poison; this codebase, remarkably, ships the second while
arming the first's data. **Absence itself is the argument**: the correct rendering of a bad week is
mostly *not rendering* — and "mostly not rendering" is a thing a moment can do and a tab cannot.

### 4. My lean

**(b) Demote.** Kill the Vault as a tab; keep the record as **one rendering in two places** — a
completion receipt on the Deck at the moment of disarm (one human sentence: what was sealed, for
how long, intention kept, hold untouched; no HMAC, no counters, no floors) and a single honest
week-line at the Sunday reckoning (seven days, blanks left blank, deflections-with-triggers as the
one acted-on fact, offered as a Shield decision, not a monument). The session rows stay in the
database — they are cheap, and the shield's own bookkeeping reads them — but nothing stands open
for checking, nothing accumulates into a tower, and every number shown is a number that happened.
The strongest point against this lean, stated fairly: wave-1 found the Vault is the app's *only*
screenshot-worthy surface, and beauty is retention — a trophy room he is proud of may be the one
vanity loop that pulls tomorrow's first arm out of him at 05:00, and demotion throws away the
prettiest object in the app (the Tower) to win an argument about honesty that a 19-year-old at
3:30 AM may never have. If the Tower is what makes him open the app on the worst morning, my lean
costs the exact behaviour it claims to protect — which is why the demoted receipt must be *at least*
as beautiful as the room it replaces, or this paragraph wins.

---

## Worst finding

**Left entirely alone, Monolith manufactures ≈750 minutes of "completed" focus a day — some 87
hours in an untouched week — from two seed routines he never created, cannot rename, and cannot
delete, while charging every honest manual arm ~8 of its 10 allowed seconds to re-specify the dial,
blocks, and intention the app forgot.**
