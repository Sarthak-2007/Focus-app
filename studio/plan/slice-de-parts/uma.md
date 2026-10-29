# UMA — SLICE D JOINT: STRIP CELL 2 · APPS (build-ready)

**Seat:** Uma (14, Wild, museums — registries, counts, what a collection
claims about itself) · **Joint:** the middle cell of the standing strip
(BLOCK → APPS → TIME, causal order fixed, Register governs), above the
slide track, W4. **Filed rendering law (Rosa's grammar) stands:**
state by position, fill density (pass = recess flush · fault = cap-tone
fill · unknown = raised fill), word, edge; counts in Anonymous Pro 700
15sp. What this file changes is what the cell *counts*, where the count
*comes from*, and whether a data cell can be *at fault* at all.

A museum registry does one job: the number on the wall must be the
number in the store, derived the same way the store itself derives it,
or the label is fiction. The code today prints three different numbers
for this one fact, and all three are wrong in different ways (§2).
This file leaves the app with exactly one derivation.

---

## 1. VERDICT

**The APPS cell ships as a pure registry count — LOCKED + PEEK_2M,
derived from the DAO apps flow, never from a service, never from the
heavy sweep — with Rosa's `n OF n` ratio struck, fault density made
unreachable, and the strip/imprint reconciliation settled as: same
fact, same number, two tenses.**

1. **THE COUNT (ruling):** `n = |{ app : resolvedTier != OPEN }|` —
   LOCKED and PEEK_2M both count; OPEN never does. This is the set
   the seal will actually hold, because it is the set both enforcers
   actually enforce (§2). The current LOCKED-only count is a registry
   that omits a third of the collection on the seed data (7 printed,
   10 held). The PEEK question is the one genuine fork — it is filed
   for Ren in §7 with my recommendation (count PEEK), because BEYOND
   W4 already decided the imprint's HELD count includes PEEK, and two
   different numbers in the same tense on the same screen is the one
   configuration TRUTH-once cannot survive.
2. **THE SOURCE (ruling):** one derived `StateFlow<Int?>` in the
   ViewModel, mapped off `repository.allAppsFlow` (Room's
   `getAllApps()` Flow). Room's invalidation tracker is the push
   channel: the count recomputes **exactly when `blocked_apps`
   changes** — a tier write, a loadout apply, a profile apply — and
   at no other time. No cadence, no press-poll, no
   `getSystemGuardStatus()` (the heavy sweep: HMAC + DAO snapshot +
   four system queries per call, `MonolithRepository.kt:603–632` —
   and its apps figure is the *total* list size anyway, a third,
   different wrong number). APPS is not on Otis's ReadinessBus: the
   bus carries service facts the services maintain; this is a data
   fact the DAO maintains. Different source from Rosa's BLOCK cell,
   deliberately — BLOCK asks *can it block*, APPS asks *what is in
   the registry*.
3. **FAULT (ruling): APPS can never be at fault.** Its reachable
   densities are exactly two: **known** (any n, including 0) = recess
   flush, and **not-yet-known** (no Room emission yet) = raised fill,
   word `UNKNOWN`. Cap-tone fill and the doubled top hairline are
   unreachable for this cell — not by style discipline, but because
   no state in its derivation maps to them (§3). A number cannot be
   at fault; only a claim about a service can. `0` is a pass-density
   truth printed plainly: an empty loadout is a real state of the
   collection, not a failure of it.
4. **Rosa's `7 OF 7` / `6 OF 7` ratio is STRUCK for this cell**
   (amendment, §3). Its numerator — "enforced count, from the
   blocking service's own report" — does not exist before the arm:
   the Shield service `stopSelf()`s the moment no session is active
   (`MonolithShieldService.kt:69–70`), so pre-arm there is no service
   to report enforcement, and during a session the strip is frozen
   anyway. A ratio whose numerator is copied from its denominator
   (both read off the same DAO list) would stamp `10 OF 10` on the
   wall while claiming a verification that never happened — a
   registry certifying its own list against itself. The coverage
   question the ratio tried to ask (does enforcement match the
   loadout?) is real, and it belongs to BLOCK's press-poll and the
   Canary, which test the intercept path end-to-end. APPS does not
   re-ask it in arithmetic.
5. **Primed vs unprimed is an input switch on the same derivation,**
   named `appsCellCount` (§3d): unprimed it counts the DAO list as
   it stands; primed it counts the *projection* of the same list
   through the routine loadout's tier function — the identical
   `when` that `applySmartLoadout` writes with, extracted pure so
   the projection mutates nothing. The cell's word, fill, and form
   do not change when primed (W1: priming never moves or re-lights
   the frame; W4: the primed imprint string is *identical*). The
   routine's name is printed once, in the NEXT slot — APPS never
   repeats its provenance.

---

## 2. AGAINST THE REAL CODE (file:line)

All paths `Focus-app/app/src/main/java/com/example/`.

**The tier model — exactly three tiers, one resolver.**

| Fact | Evidence |
|---|---|
| `AppBlockTier` has exactly 3 values: `LOCKED` ("Shut"/"Blocked"), `PEEK_2M` ("2m Knock"/"2m Peek", 2-min lease + 20-min cooldown), `OPEN` ("Always accessible") | `data/MonolithModels.kt:13–37` (LOCKED :19, PEEK_2M :25, OPEN :31) |
| Every app's effective tier is `resolvedTier`: parse `blockTier`, falling back to `isBlocked → LOCKED else OPEN` on a corrupt string | `data/MonolithModels.kt:213–231` (resolver :226–231) |
| All writers keep `isBlocked == (tier != OPEN)`: `cycleAppBlockTier` / `setAppBlockTier`, `applySmartLoadout`, `applyProfile` | `data/MonolithRepository.kt` (tier cycling ~:660–675; loadout :681–722; profile apply, `applyProfile`) |

**Enforcement — the ground truth the count must match.**

| Fact | Evidence |
|---|---|
| Shield loop's enforced set: `tier == OPEN → false`, otherwise **true** — LOCKED *and* PEEK_2M — except PEEK during a Safe Break and PEEK inside an active peek lease | `service/MonolithShieldService.kt:108–120` |
| Accessibility intercept: identical rule — OPEN returns early; PEEK passes only inside Safe Break or a live lease; everything else intercepts | `service/MonolithAccessibilityService.kt:98–115` |
| The Shield service is **not alive pre-arm**: its loop `stopForeground` + `stopSelf()`s when no active session exists — Rosa's "blocking service's own report" has no reporter at strip time | `service/MonolithShieldService.kt:66–72` |
| The lease/break exceptions are *momentary states inside* the hold, re-read every 2s by the loop — if the count followed them, it would flicker mid-session, which the freeze law forbids. The count therefore names the **loadout fact** (what the seal subjects to blocking), not the instantaneous intercept set | `service/MonolithShieldService.kt:108–120` + loop `delay(2000L)` |

**The three wrong numbers the code prints today.**

| # | Rendering | Derivation | Why it's wrong |
|---|---|---|---|
| 1 | Deck eyebrow `WILL SHUT n APPS` / `SHUT n APPS`, 10sp | `apps.count { resolvedTier == LOCKED }`, computed in the composable | `ui/screens/TactileDeckScreen.kt:120`, printed :219 — **undercounts by every PEEK app** (seed: prints 7, seal holds 10) |
| 2 | Bottom-nav badge `blockedCountBadge` | the same LOCKED-only count, duplicated in the host | `MainActivity.kt:214–216`, consumed :482 — a second copy of the same wrong derivation |
| 3 | `SystemGuardStatus.installedLauncherAppsCount` | `dao.getAllAppsSnapshot().size` — the **total** registry size, OPEN included | `data/MonolithRepository.kt:619–631`, inside the heavy sweep :603–632 (HMAC verify :616, clock drift, 4+ system queries) — wrong number, at the highest price in the codebase, and Otis's law already forbids calling it on any cadence |

**The right source already exists — unwired for this purpose.**

| Fact | Evidence |
|---|---|
| DAO exposes the apps list as a Room `Flow` (push on any `blocked_apps` write) plus a snapshot | `data/MonolithDao.kt:11–16` |
| Repository passes it straight through as `allAppsFlow` | `data/MonolithRepository.kt:53` |
| VM already collects it as `apps: StateFlow<List<BlockedAppEntity>>` — with `emptyList()` as the initial value, so **cold start is indistinguishable from a true empty loadout** (the same cold-start lie Otis §0 flagged for guard status, one cell over) | `ui/MonolithViewModel.kt:247–249` |
| Routine loadouts are a closed enum the routine row names as a string: `RoutineEntity.loadoutName` ∈ `SmartLoadout` (STUDY / WORK_SPRINT / SLEEP) | `data/MonolithModels.kt:374–382`; cycled in `ui/MonolithViewModel.kt:430–435` |
| `applySmartLoadout`'s tier assignment is a pure per-app function of (loadout, category, package/name) *until* its final `dao.insertApps(updated)` — the projection in §3d is this `when`, minus the write | `data/MonolithRepository.kt:681–722` |
| **No prime state exists in code.** `RoutineSchedule.isActiveNow` is defined and never called; the current `RoutineAlarmReceiver` *applies* the loadout and *starts a session* at window start — the auto-fire model the settled plan (HPA-01: a routine is a WINDOW, not a firing) supersedes. The primed branch in §3d consumes the W1 prime function's output when it lands; see Risk 3 | `data/MonolithModels.kt:441` (unused); `service/RoutineAlarmReceiver.kt` BOUNDARY_START |
| Loadout editing lives only on Shield (tier sleds `onTierSelected`, `onSetAppTier`, `onApplySmartLoadout` wired to the SHIELD destination) while the Deck is a separate destination in the same `AnimatedContent` switch — the two are never composed together | `ui/screens/ShieldMatrixScreen.kt:278`; `MainActivity.kt:281` (DECK) vs :337 (SHIELD) |

---

## 3. AMENDED SPEC — EXACT

### 3a. The count and the tier table

```
n := apps.count { it.resolvedTier != AppBlockTier.OPEN }
```

| Tier | Counts? | What the count claims about it |
|---|---|---|
| `LOCKED` | **Yes** | Shut at seal; no in-session release except the weekly emergency break |
| `PEEK_2M` | **Yes** | Shut at seal; releasable only inside its own 2-minute lease (then 20-min cooldown) or a Safe Break — an exception *inside* the hold, not an absence of one. This is why the sealed tense is HELD, not SHUT (W4) |
| `OPEN` | **No** | Never intercepted; not part of the collection the seal holds |

- Count on `resolvedTier`, never on the `isBlocked` flag and never
  on the raw `blockTier` string: enforcement reads `resolvedTier`
  (`MonolithShieldService.kt:110`, `MonolithAccessibilityService.kt:99`),
  so the wall number and the intercept set are the same derivation
  by construction — they cannot drift apart.
- **Websites are not in this number.** `blocked_sites` is a separate
  registry (Guard/Shield's fact; my receipt line counts
  `14 apps · 3 sites` as two facts). APPS folding sites in would
  make the cell claim a mixed collection under one noun. Excluded,
  as a ruling.
- **Reconciliation with the imprint (W4, :526–528): SAME number.**
  Strip idle `n` and imprint `{n} APPS WILL SHUT` are one fact in
  two renderings (numeral-cell vs sentence); primed, both read the
  projection; at the seat frame both freeze on the press snapshot;
  sealed, the imprint's tense changes to `{n} APPS HELD` and the
  strip keeps the frozen numeral. Invariant, testable at build:
  *whenever strip and imprint are co-visible, their numbers are
  equal.* Any build where they differ has a second derivation —
  delete it.

### 3b. The source — one derived flow, no sweep, no cadence

In `MonolithViewModel` (new; §4–5):

```kotlin
// Manual input: the registry as it stands.
private val manualAppsCount: StateFlow<Int?> =
    repository.allAppsFlow
        .map { list -> list.count { it.resolvedTier != AppBlockTier.OPEN } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

// The cell's one input. Null = the registry has not answered yet.
val appsCellCount: StateFlow<Int?> =
    combine(manualAppsCount, primedLoadout) { manual, primed ->
        when {
            manual == null -> null            // projection needs the list too
            primed != null -> projectedCount(primed)   // §3d
            else -> manual
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)
```

- `Int?`, not `Int`: `null` is UNKNOWN. The existing `apps` flow's
  `emptyList()` initial value (`MonolithViewModel.kt:247–249`) must
  **not** be reused for this derivation — it would print a false `0`
  on every cold start. `appsCellCount` starts `null`, is
  `SharingStarted.Eagerly` (one small list, always collected by the
  Deck; Eagerly so a WhileSubscribed 5s lapse can never reset a
  known count back to UNKNOWN on re-entry).
- Recompute trigger is **Room invalidation alone**: any write to
  `blocked_apps` (`setAppTier`, loadout/profile applies, package
  sync inserts) re-emits `allAppsFlow` and the map re-runs. There is
  no timer, no re-poll, no Deck-entry refresh, and **no call to
  `getSystemGuardStatus()`** anywhere in this cell's path — its DAO
  snapshot is inside a suspend sweep that also pays HMAC +
  clock-drift + four system queries to return the wrong number
  (total size) for this purpose.
- The Shield heartbeat's enforced-set size (Otis's tap,
  `MonolithShieldService.kt:108–120` computed then discarded) is
  **not** this cell's source and must not be wired to it: the
  service is dead pre-arm, and in-session its set is lease/break-
  adjusted — a different, momentary fact. It remains available to
  BLOCK/Canary as corroboration, where a service fact belongs.
- **No STALE for APPS.** STALE is the decay of a polled measurement.
  A Room Flow value does not age: it is the current registry,
  held by subscription, replaced on the frame the registry changes.
  The strip's seam stamp keeps belonging to BLOCK's evidence
  (Otis's redefinition); APPS neither owns nor extends it.

### 3c. Rendering and fault semantics — exact

Cell 2 of 3, fixed middle address. Label row per Rosa's geometry:
`APPS`, Anonymous Pro 700 10sp +2.0 tracking, plate ink. State row:
**Anonymous Pro 700, 15sp, lh 20.**

| State | Word | Fill | Edge | Ink |
|---|---|---|---|---|
| Known, n ≥ 1 | `{n}` (e.g. `10`) | recess flush (pass density) | single hairline | secondary at rest, per Rosa's rest state |
| Known, n = 0 | `0` | recess flush (pass density) | single hairline | same |
| Not yet known | `UNKNOWN` | **raised** (`#362C27` night) | single hairline | secondary |
| Fault | — | — | — | **unreachable: no state in this derivation produces cap-tone fill (`#4A423C`), the doubled top hairline, or a fault word** |

- **The zero, worded:** n = 0 is the cell printing its truth
  plainly — *the registry answered, and the answer is nothing will
  shut.* It is pass-density because "pass" for a data cell means
  *the registry answered*, not *the registry is non-empty*. It gets
  **no sentence** under the track (Rosa already ruled APPS faults
  get no sentence; a non-fault gets one even less), **no advisory
  status**, and it does **not** trip BLOCK's interlock — whether a
  seal that shuts nothing should be refusable is the interlock
  joint's question (BLOCK's refusal is about a *broken* seal, and
  this seal is not broken, it is empty). The imprint at zero reads
  `0 APPS WILL SHUT` / sealed `0 APPS HELD` — grammatical, exact,
  unembarrassed. A museum with an empty gallery prints `0` in the
  register; it does not print FAULT.
- Flips: one frame, no easing, no stagger (Rosa) — silent, no
  haptic, no sound; the panel is read, not touched. The APPS cell
  is **never a tap target** in any state (BLOCK's pre-arm repair
  tap is BLOCK's; APPS has no repair surface — its remedy is the
  loadout on Shield, reached by navigation, not by the cell).
- While sealed: the cell holds the frozen press value (§3e) for the
  session's life, then returns to the live flow at session end.
  Mid-session loadout edits on Shield change live enforcement
  (the Shield loop re-reads every 2s) but never rewrite the frozen
  cell or the imprint — that is provenance, the same arm-time
  snapshot law my receipt is built on.

### 3d. Primed vs unprimed — the switch, named

- **Name:** `primedLoadout: StateFlow<SmartLoadout?>` — the Deck
  prime function's output (the same state that pre-sets the wheel
  and prints the loadout name in the NEXT slot, W1). APPS does not
  detect priming itself; it consumes that one state, exactly as
  TIME consumes the wheel/routine state. `null` = unprimed.
- **Unprimed:** `appsCellCount = manualAppsCount` — the DAO list
  as it stands.
- **Primed:** `appsCellCount = projectedCount(primedLoadout)` where

```kotlin
// In MonolithRepository: the when from applySmartLoadout (:685–716),
// extracted pure. applySmartLoadout calls it, then writes; the
// projection calls it and writes nothing.
internal fun tierFor(loadout: SmartLoadout, app: BlockedAppEntity): AppBlockTier
fun projectedShutCount(apps: List<BlockedAppEntity>, loadout: SmartLoadout): Int =
    apps.count { tierFor(loadout, it) != AppBlockTier.OPEN }
```

  One tier function, two callers — writer and projector can never
  disagree about what STUDY/SLEEP would do to the same list,
  because there is only one `when` left in the codebase.
- The cell's rendering under primed is **byte-identical** to
  unprimed — same word form, same fill law, no marker, no second
  provenance line. Priming changes the input, never the instrument
  (W1 stillness; W4: primed imprint string is *the identical
  string* against the routine's loadout).

### 3e. Mid-press guarantee (Teo's law) — in code terms

- At ACTION_DOWN on the cap, the strip captures
  `pressAppsCount = appsCellCount.value` into press-scoped state
  owned by the slide's press (the same capture the imprint freezes
  on, W4 :528–529: *no change mid-slide, in any condition*). While
  a press is live, the cell renders `pressAppsCount ?: UNKNOWN`;
  on release/cancel it returns to the live flow in one frame.
  If `appsCellCount.value` is `null` at DOWN, the cell is UNKNOWN
  for that press — it never updates mid-press when the first Room
  emission lands mid-slide.
- The guarantee is structural, not just snapshot-deep: **he cannot
  edit the loadout mid-press.** Every loadout mutation
  (`setAppBlockTier`, `cycleAppBlockTier`, `applySmartLoadout`,
  `applyProfile`) is wired only to the SHIELD destination
  (`MainActivity.kt:337–360`, `ShieldMatrixScreen.kt:278`); the
  Deck destination (`MainActivity.kt:281`) exposes no tier or
  loadout control, and the nav switch is an `AnimatedContent` swap
  — navigating away disposes the Deck composition and its press
  with it. A routine window boundary landing mid-press also cannot
  move the cell: under the settled prime model a boundary changes
  `primedLoadout` (an input), and the press renders the snapshot,
  not the input. The snapshot is therefore belt *and* braces: the
  braces alone would suffice, the belt is three lines.

---

## 4. CODE SITES

| Site | Change |
|---|---|
| `ui/MonolithViewModel.kt` (after :247–249) | **Add** `manualAppsCount` + `appsCellCount` derived flows (§3b); consume the prime function's `primedLoadout` state when W1 lands it in this VM |
| `data/MonolithRepository.kt:681–722` | **Extract** `tierFor(loadout, app)` pure from `applySmartLoadout`'s `when`; writer re-calls it. **Add** `projectedShutCount(apps, loadout)` (§3d) |
| `ui/screens/TactileDeckScreen.kt` (strip build, Rosa/strip joint's composable) | APPS cell renders `appsCellCount` (or the press snapshot while pressing) per §3c table: word, fill, edge mapping |
| `ui/screens/TactileDeckScreen.kt:120` + `:219` | **Delete** the composable-local LOCKED-only `blockedCount` and its eyebrow rendering — superseded by strip (standing) + imprint (sentence); keeping it would leave a second, wrong derivation on the same screen |
| `MainActivity.kt:214–216` + `:482` | Nav badge: re-point at `appsCellCount` **if the badge survives** Band/Otis's joint — its number must come from the one flow; its rendering is not mine to spec |
| — | **No changes:** `MonolithDao.kt` (the Flow already exists, :11–13 — deliberately no SQL `COUNT` query: a SQL count on the raw `blockTier` string would bypass `resolvedTier`'s fallback and could disagree with enforcement), `MonolithShieldService.kt`, `MonolithAccessibilityService.kt`, `getSystemGuardStatus()` (untouched, un-called by this cell), DB/schema (no migration) |

## 5. PRICE

| Item | Lines |
|---|---|
| VM derived flows (`manualAppsCount`, `appsCellCount`, combine switch) | ~26 |
| Repository: extract `tierFor` (net, incl. writer re-call) + `projectedShutCount` | ~14 |
| Deck: APPS cell word/fill/edge mapping + press-snapshot plumbing (3-line capture + render switch) | ~24 |
| Deck: delete :120 derivation + :219 eyebrow | −4 |
| MainActivity badge re-point (if it survives) | ~3 changed |
| **Total** | **≈ 63 lines net, 4 files touched, 0 new files, 0 migrations, 0 timers, 0 service changes** |

## 6. RISKS

1. **UNKNOWN flash on cold start.** First Room emission is fast but
   not instant; the cell honestly shows raised `UNKNOWN` for those
   frames instead of the current code's false `0`/false `7`. That
   is the Otis cold-start lie dying in public. If device trial
   shows the flash reads as breakage rather than honesty, the
   mitigation is holding the strip's *first* paint until first
   emission (Deck composition gate) — a rendering delay, never a
   fabricated initial value.
2. **PEEK is a judgement wearing a fact's clothes.** The
   enforcement code is unambiguous (PEEK intercepts), but the
   *word* HELD for an app he may open for 2 minutes is a claim
   about what "held" means. §7 puts it to Ren properly; if he
   rules LOCKED-only, §3a's table and the imprint diverge by
   design and must be re-named as two facts (see fork).
3. **The primed branch depends on a prime state that does not
   exist in code yet.** Today's `RoutineAlarmReceiver` auto-applies
   and auto-starts at window start, contradicting HPA-01. Under
   that regime the primed branch simply never fires — and the
   count stays truthful anyway, because the receiver's mutation
   lands in the DAO and the manual branch reads the DAO. The fork
   in the road is the routines joint's (Sten's), not this cell's;
   this cell is correct under both regimes, but WILL SHUT vs HELD
   tense under auto-start is not mine to rescue.
4. **Frozen vs live divergence mid-session is by design** — and
   will look like a bug to anyone who edits the loadout on Shield
   mid-session, watches enforcement change (2s loop), and sees the
   strip hold the seal-time number. It is provenance (the seal's
   registry entry, frozen at accession, like my receipt snapshot).
   Accepted under the freeze law; named here so the first bug
   report meets its answer already written.
5. **`resolvedTier` fallback hides data rot.** A corrupt
   `blockTier` string counts via the `isBlocked` fallback — the
   same way enforcement treats it, so wall and intercept can never
   disagree; but neither will ever announce the rot. Out of scope
   for this cell; flagged for whoever next touches the DAO.

## 7. FORK FOR REN (one, with recommendation)

**The PEEK-tier count.** Drafted for Ren to put to Sarthak in plain
words, if he chooses to ask at all — my recommendation is that this
is a studio-settleable reading of already-settled law (W4's HELD
includes PEEK), and the fork is filed because the brief names it
the likely one, not because the code leaves it open.

- **Option A — Count LOCKED + PEEK (RECOMMENDED).** The cell prints
  what the seal will hold: `10` on the seed registry. Strip and
  imprint carry the same number in every tense; the word for the
  sealed tense is HELD precisely because PEEK is held-with-a-lease.
  Matches both enforcers' code exactly.
- **Option B — Count LOCKED only.** The cell prints hard-shut apps
  only: `7` on the seed registry. Consequence, stated plainly:
  strip APPS and the imprint become *different facts* and must be
  honestly re-named (strip = "will shut outright", imprint =
  "held incl. peek") — two numbers for one session on one screen,
  each needing its own label, forever. It also keeps the current
  Deck derivation alive, which understates the seal he is about
  to enter by every messaging app he peeks.

**My recommendation: A, without hedging.** A registry that omits
the loaned works from its count has not made a conservative count;
it has made a wrong one. PEEK's lease is a door inside the held
room — the room is still held.

*Not a fork, a note for Marlowe:* whether an n = 0 seal should be
refusable belongs to the interlock joint (BLOCK/Fen), not to this
cell. My ruling covers only the rendering: APPS never faults on 0.

## 8. MAIL / WALL NOTE

Inbox at start: **empty** — no unread letters in `mail/uma/`; the
three letters in `read/` (Orson 0514, Mara 0517, citation-lift;
Moss 0531, sequence-number-and-verdict-line) were read in a prior
seating and stand filed. No re-seating letter from Marlowe was in
my inbox; this assignment arrived via the parent brief, and my wave
letter (0720, the Rotation) was read at Marlowe's own `read/` copy,
as the brief directed. One letter sent: to Marlowe
(`mail/marlowe/0818__from-uma__slice-de-apps-cell.md`) — verdict,
the struck ratio, and the PEEK fork with my recommendation. One
signed entry appended to the Commons wall: the registry rule in one
breath — *the wall number is the store's own derivation, or it is
fiction; APPS counts LOCKED + PEEK off the DAO flow, never faults,
and prints 0 plainly.*

— Uma, Wild · museums
