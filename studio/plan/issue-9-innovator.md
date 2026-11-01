# ISSUE #9 — INNOVATOR: WHAT A CHECK CAN HONESTLY PROVE

*Seat: Innovator, issue-9 cell (Hof, lead). Date: Oct 30, 2026.*
*Ground read in the cell clone (`agent/issue-9`): `AUDIT-park.md`
(P-1…P-8, §4 merge list, §5 escalations), `AUDIT-park-claims.md`
(Una, A-01…A-14), `TRUTH3-WORDING.md` (Hale, W-08 / W-11),
`service/WatchStore.kt`, `service/MonolithAccessibilityService.kt`,
`service/MonolithShieldService.kt`,
`service/MonolithNotificationListener.kt`,
`data/MonolithRepository.kt` (`standingWarrant`, `watchGrantQuery`,
`usageAccess`, `queryForegroundBlockedApp`, `getSystemGuardStatus`,
peek grant), `ui/components/ParkDrawer.kt`,
`ui/components/GuardLine.kt`, `ui/MonolithViewModel.kt`
(`runWatchSweep`, `refreshSystemGuard`), `MainActivity.kt`
(PARK on-open diagnostic, ON_RESUME sweep). Paper only — nobody
compiles.*

*Scope discipline, stated once: the Live-Fire Strip is PARKED in
issue #21. Nothing below rehearses a block, posts a sacrificial
notification, or builds on the Strip. Where a mechanism below
touches the same territory, the line is drawn in its own section.
Designing the W-08 plate is likewise out of scope — §8 only
prepares its inputs.*

---

## §0 · THE PROBLEM, IN ONE PARAGRAPH

PARK's board is a ledger of other moments' measurements. The sweep
fires on Deck entry, Deck resume, and FIX return
(`MonolithViewModel.runWatchSweep`); PARK's own on-open hook fires
`refreshSystemGuard()` — a clock/HMAC/boot diagnostic whose result,
in MainActivity's own words, "renders NOWHERE." A man opens the
drawer *because* he doubts the machine, and the drawer answers with
whatever the Deck happened to measure before he doubted. Worse,
the 45-second STALE constant (`WatchStore.STALE_AFTER_MS`) decays
healthy grants in front of him while he reads, and STALE offers no
verb. CHECK is that verb. The design question is not "how do we
re-run the sweep" — it is **what grade of proof a press can
honestly buy**, because the sweep alone buys the weakest grade
there is: the OS's self-report, again.

## §1 · THE PROOF LADDER (the vocabulary every mechanism uses)

Four grades exist in this machinery. CHECK's print must name the
grade it reached, never borrow the grade above it.

| Grade | Name | What it means | Reachable today? |
|---|---|---|---|
| L0 | UNMEASURED | No reading in this process. Cold open lives here. | — (the state CHECK exists to leave) |
| L1 | SELF-REPORT | The OS / service registry answers a query *now*: grant present, service instance exists. This is all `watchGrantQuery` and `standingWarrant` do. | Yes — the existing sweep |
| L2 | WITNESSED | The pipeline *did work* inside the check window: the accessibility service acquired a live window root and ran its node machinery after the press; the shield loop completed an iteration against the live session during the window. | Yes — with one new service method (M2) |
| L3 | END-TO-END | A block or capture actually fired. Provoked L3 is the Live-Fire Strip — **issue #21, not built here.** *Historical* L3 already exists for free: real intercepts and captures sit timestamped in Room (`InterceptLogEntity.timestampMillis`, `CapturedNotificationEntity.timestampMillis`). CHECK may *quote* history; it may not *stage* it. | Quote only |

The ladder is the whole invention surface: today's PARK prints L1
facts in the typography of certainty (`RUNNING` derived from
setting ∧ parent freshness, P-2). A CHECK that only re-runs the
sweep and prints "CHECKED" would repeat the sin with a fresher
timestamp. The mechanisms below are ordered by grade bought.

---

## §2 · MECHANISM M1 — THE COMPLETING SWEEP (re-derivation, with an ending)

**Sketch.** CHECK (and PARK-open, per audit §4.1) calls the existing
`WatchStore.runSweep { repository.watchGrantQuery(it) }`, but wraps
it in a *completion contract* the sweep currently lacks:

- Press stamps `checkGen = sweepGen + 1`, `checkStartMono/Wall`.
- The verb is not done when the queries are *dispatched* — it is
  done when all six items carry measurements with
  `measuredWall >= checkStartWall` (the store already tracks exactly
  this era logic for LAST PROVED via `pendingSweepStartWall`), or
  when a **2.5 s budget** expires.
- On completion: LAST PROVED advances by the store's existing
  complete-set rule (untouched — acquitted by all six seats), and
  the board foot prints `CHECKED {HH:mm:ss} · 6/6 · SELF-REPORT`.
- On timeout: print what landed — `CHECKED {HH:mm:ss} · 4/6 —
  {STRAGGLER} UNANSWERED` — and the straggler's row stays
  UNMEASURED-print, never backfilled from its previous value. A
  query that throws already degrades to `false` inside `runSweep`;
  CHECK must distinguish *answered-no* (MISSING, a fact) from
  *never answered* (a hole). That distinction needs the landing
  stamp, not the value — it is free in the measurement record.
- Side effect with teeth: a completed CHECK re-bases every row's
  age to ~0, so the P-4 spectacle — grants decaying to STALE while
  the drawer stands open — cannot start from a stale base. The
  5-second render beat in ParkDrawer keeps re-deriving; after a
  CHECK the decay he watches is real decay from a known instant.

**Proves:** all six grants, as the OS reports them, at a named
instant, as one set. **Cannot prove:** that any pipeline works —
an enabled-but-wedged accessibility service passes M1 cleanly
(instance exists, AppOps allows, admin active).

**Price: S.** ~60–80 lines: a check-state holder beside the store,
the completion watcher (a `snapshots` collector with timeout), one
print line, tests beside `WatchStoreRobolectricTest`. No DB, no
permission, no new surface. Risk: low — every primitive exists;
the only new logic is the timeout/straggler bookkeeping. This is
audit §4.1's cure, finished rather than merely triggered.

---

## §3 · MECHANISM M2 — THE PROVOKED ROOT PULL (pipeline handshake) ★

**The observation.** The accessibility pipeline already has a
genuine activity witness: `lastRootMono`, stamped in
`onAccessibilityEvent` *before* any early return, only when
`rootInActiveWindow` was actually acquired, and zeroed on unbind
so a rebound service inherits nothing. The warrant's residency law
(`stampedThisResidency`) already treats "a root stamp at or after
a start instant" as pipeline proof — for the Deck. But the stamp
is *ambient*: it lands when the foreground happens to change. A
man sitting in PARK generates no foreground change, so the witness
cannot be summoned on demand — today.

**Sketch.** Give the service the one method it is missing, and let
CHECK run a handshake:

1. CHECK records `pullStartMono` and starts its own residency in
   the store (a PARK sibling of `noteResidencyStart` —
   `noteCheckStart()`; do not reuse the Deck residency, the two
   proofs must not launder each other).
2. It calls a new member on the live instance,
   `MonolithAccessibilityService.provePipelineNow(): Boolean`:
   synchronously (on the caller's thread, bounded — one root
   acquisition + one node query) it reads its own
   `rootInActiveWindow`; if non-null it runs the *same query shape
   the sniper runs* — `findAccessibilityNodeInfosByViewId` for a
   view id that exists in Monolith's own foreground window — and
   on success stamps `lastRootMono = now` through the exact code
   path the event handler uses. Returns whether the root was
   acquired. No event is forged; the service exercises its real
   machinery against the real current window, on request instead
   of on ambience.
3. CHECK's a11y leg passes at L2 iff `instance != null` ∧
   `provePipelineNow()` ∧ `lastRootMono >= pullStartMono`.
   Failure degrades the leg to L1-or-UNPROVEN — **never to OFF**:
   a null root on an OEM-quiet window is absence of proof, not
   proof of absence (the event path's own law: "a failed root
   acquisition stamps nothing: the wire reads dead, which is the
   truth" — CHECK prints UNPROVEN, the warrant's existing word).
4. **Shield leg (only when a session is active):** CHECK reads
   `engineHeartbeatMono`, waits for it to *advance* — the shield
   loop publishes one `onShieldIteration` per ~2 s iteration — and
   requires `MonolithShieldService.protectingSessionId ==` the
   active session's id with `engineProtecting == true`. That is a
   loop that demonstrably ran, against this session, inside the
   window: L2 for the shield. With no session, the leg prints
   `NO SESSION — SHIELD NOT OWED` and does not count as failure:
   an idle machine has no shield to witness, and CHECK must not
   fail a healthy idle phone.
5. **Listener leg — deliberately L1, labelled so.** The listener
   cannot be provoked without posting a notification, and posting
   one is Live-Fire (#21). CHECK prints the binding (L1) plus the
   historical L3 quote from Room: `LAST CAPTURE {HH:mm}` from
   `CapturedNotificationEntity.timestampMillis`, or `NO CAPTURE
   ON RECORD`. History is quoted as history; the print's tense
   does the honesty work.

**Verdict print** (one line, grade named): the board foot gains
e.g. `CHECKED 21:14:03 · PIPELINE WITNESSED · SHIELD WITNESSED`
/ `CHECKED 21:14:03 · GRANTS ONLY — PIPELINE UNPROVEN`. The
warrant line (audit §1.7) then has something to be: when CHECK
has witnessed within the freshness window (§5), PARK may print
the warrant word derived from a *fresh* `standingWarrant()` whose
residency test the pull just satisfied for PARK's own residency —
LIVE stops being a word the board borrows from the Deck's visit.

**Proves:** the a11y service is bound, awake, able to acquire the
foreground root, and able to run its detection query shape *now*;
the shield loop is turning against the live session *now*.
**Cannot prove:** that a real Shorts feed or blocked app would be
intercepted — detection against Monolith's own window is not
detection against YouTube's node tree, and the intercept *act*
(BACK + airlock) is never exercised. That remainder is exactly
#21's territory and CHECK's print must not claim it.

**Price: M.** ~120–180 lines: one service method (~25), store
check-residency + verdict derivation (~50), ParkDrawer verb +
print (~40), Robolectric/unit tests for the degradation table
(bound+pull-ok → WITNESSED; bound+pull-null → UNPROVEN, never OFF;
unbound → sweep result stands). No DB, no permission. Risks:
(a) `rootInActiveWindow` from a non-event thread — the property
is the service's own and the event handler already reads it under
try/catch; the pull copies that discipline; (b) OEM windows where
Monolith's own root is transiently null during drawer animation —
mitigate by firing the pull after the drawer's 280 SEAT settles
(ParkDrawer's `settled` flag already exists) and allowing one
retry inside the budget before degrading; (c) the pull must never
run while sealed-and-session-active in a way that perturbs the
session — it is read-only against the window tree; it launches
nothing, backs out of nothing, intercepts nothing.

---

## §4 · MECHANISM M3 — CROSS-EXAMINATION (two independent reads or it prints SPLIT)

**Sketch.** Every counted item gets a *second, independent*
source, and CHECK passes the item only when both agree. The
machinery already owns both reads for the two tier-1 doors:

- **ACCESSIBILITY:** instance-bound (`watchGrantQuery`) ∧
  listed in `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES`
  (`MonolithRepository.isAccessibilityEnabled` — exists, used by
  the diagnostic, unused by the board). Agreement = GRANTED.
  Enabled-but-not-bound prints `ENABLED — NOT BOUND` (the crashed /
  not-yet-rebound service: his FIX is a re-toggle, not a grant
  trip); bound-but-not-listed is impossible enough to print as
  measured and log. This gives the board a real OFF vs NO DOOR
  split for the door that matters most, extending audit §1.6's
  word-mapping from the interceptors to tier 1.
- **USAGE ACCESS:** AppOps three-valued verdict (`usageAccess()`)
  ∧ a *functional* read: `UsageStatsManager` actually returns rows
  for a recent window (the pattern `queryForegroundBlockedApp`
  already runs — 15 s window, `INTERVAL_DAILY`). ALLOWED-but-empty
  is a real OEM failure mode (AppOps granted, stats withheld);
  today it sails through as GRANTED and the shield's foreground
  door silently never fires. Disagreement prints
  `ALLOWED — STATS DARK`.
- **DEVICE ADMIN:** `isDeviceAdminActive` ∧ the admin receiver's
  own enabled state via `DevicePolicyManager.getAdmins` /
  component check — one platform, two surfaces; weakest pair of
  the four, keep L1 if the second read proves redundant in test.
- **OVERLAY / BATTERY:** single-source platform reads
  (`Settings.canDrawOverlays`, `PowerManager.isIgnoring…`). No
  honest second source exists in-tree; they stay L1 and CHECK's
  summary counts them as self-report, not witnessed. Naming that
  asymmetry is part of the mechanism.

**Proves:** grant *and* the grant's functional surface agree, for
the two doors the warrant actually stands on. **Cannot prove:**
pipeline activity (that's M2's grade).

**Price: M.** ~120–150 lines: second-read queries in the
repository, a SPLIT print state per item (one new `PermPrint`
case or a qualifier string — builder's call), board word mapping,
tests. Risk: medium — UsageStats emptiness varies by OEM and by
whether anything was used in the window; the functional read must
use a window wide enough (15 s of *device* usage while he is
literally holding the phone in PARK — Monolith's own usage counts)
or `STATS DARK` becomes a false accusation. Pilot it as a
qualifier print for one batch before letting it fail a CHECK.

---

## §5 · MECHANISM M4 — THE DECAY LEDGER (freshness as a model, not a constant)

**The charge inside P-4.** One flat 45 s constant serves items
with three different writer cadences: BIND items re-stamped every
~2 s by the shield loop *while a session runs* (and per the audit
residue, that loop launders BIND freshness — a 40-minute-old grant
prints seconds-fresh because the loop re-reads `instance != null`
each iteration), grant-check items stamped only by sweeps, and the
warrant stamped per read. One TTL for three physics is why STALE
both cries wolf (healthy grants, no sweep for 46 s) and stays
silent (BIND rows that can effectively never stale mid-session).

**Sketch.**

- **Per-source TTLs, derived from the writer's own cadence:**
  sweep items — TTL 45 s stands (it prices sweep-to-sweep doubt);
  BIND items *under a live shield loop* — print the loop's truth
  instead: their freshness fact is `engineHeartbeatMono` age, and
  their grant fact is the last *sweep or lifecycle* stamp; render
  age from the older, honest stamp and let the heartbeat print as
  its own line (§8 input 10). BIND items with no loop — sweep TTL.
  Lifecycle stamps (bind/unbind/admin-change hooks) never decay
  to STALE on their own: they are event facts, and the next event
  is the only thing that can contradict them; they decay only
  when a later sweep disagrees.
- **CHECK leaves a ledger entry** (in-memory ring, last 8, in the
  store): `{wall, mono, gen, landed 6/6, grade reached, first
  failure}`. PARK's foot can then print not just the last verdict
  but the *decay since*: `LAST CHECK 21:14 · WITNESSED · 3H AGO`
  and — the new capability — a *regression print*: if a CHECK
  fails an item the previous CHECK passed, the row carries
  `SINCE {HH:mm}` (the Death Certificate's cheap, honest half,
  without the Readiness-Log migration the full version rides:
  in-memory, process-life only, and printed as such).
- **Persisted anchors for the cold process.** WatchStore is
  in-memory by design ("in-memory only — proof is never
  persisted"), so a cold CHECK starts at L0 — correct. But Room
  already persists two *facts* (not proofs) CHECK should read on
  open: the session row's `shieldLastBeatWallMillis` and
  `GuardSettings.lastBootAtMillis` / `lastRoutineTickMillis`.
  A shield beat that *predates the last boot* is a dead shield
  whatever any in-memory flag says; printing
  `SHIELD LAST BEAT 07:52 — BEFORE BOOT 08:01` on a cold board is
  the one cross-process truth available, and it costs a DAO read.

**Proves:** nothing new — it makes M1/M2's prints stay true *over
time*, which is where PARK currently lies. **Price: M** (~150
lines incl. the age column audit §4.4 already owes; TTL split ~40,
ledger ~60, persisted anchors ~30, tests ~the rest). Risk: the
TTL table is a truth surface of its own — every number in it must
trace to a writer cadence in code, or M4 becomes the next audit's
P-4. No DB change for the ledger (process-life, labelled); the
anchors are reads of existing columns.

---

## §6 · MECHANISM M5 — THE INTEGRITY LEG (the diagnostic that already fires, finally printed)

**Sketch.** PARK-open already runs `refreshSystemGuard()` →
`getSystemGuardStatus()`: wall-vs-monotonic drift (intact ≤ 90 s),
session HMAC verification (`CryptoIntegrityManager`), boot count,
launcher-app count. It renders nowhere. CHECK adopts it as a
fourth, non-counted leg: `CLOCK INTACT · DRIFT 3S` /
`SESSION SEAL INTACT` (HMAC, only while a session exists),
`BOOTS n`. A tampered clock or broken seal is a blocking-truth
fact — sessions are mono-anchored, and drift is how a session's
own arithmetic goes false — and it is perverse that the one
diagnostic PARK already pays for is the one it never prints.

**Proves:** the clocks the proofs are stamped with are sane, and
the live session's state has not been altered under the app.
**Cannot prove:** anything about grants or pipelines — it is a
leg, not a verdict; it must never gate the counted four, only
stand beside them. **Price: S** (~40 lines: the ViewModel already
holds `_systemGuardStatus`; this is plumbing an existing flow to
a print + a CHECK-time refresh). Risk: low; HMAC failure wording
must be accusation-free (`SEAL UNVERIFIED`) until a human reads
it — a false integrity alarm at 1 AM is worse than silence.

---

## §7 · RECOMMENDATION — CHECK = M1 + M2, on M4's clock, M5 riding

**Build M1 and M2 as one verb** (they share the press, the budget,
and the print line; M1 without M2 is a fresher self-report, M2
without M1 witnesses a pipeline whose grants were never
re-derived). **M4 lands in the same batch if room allows** — at
minimum its age print and the BIND/honest-stamp split, because
M2's WITNESSED verdict itself decays and needs a lawful way to
age. **M3 is phase 2**: pilot `STATS DARK` as a qualifier first.
**M5 rides free** on the same press.

The composed CHECK, as a state machine the builder can lift:

```
IDLE ──press──▶ SWEEPING   runSweep (gen g); pull armed for when drawer settled
SWEEPING ──6/6 landed──▶ PULLING   provePipelineNow(); shield-heartbeat watch opens
SWEEPING ──2.5s──▶ DONE-PARTIAL    print landed count + straggler, grade ≤ L1
PULLING ──root stamp ≥ checkStart ∧ (shield advanced ∨ no session)──▶ DONE-WITNESSED
PULLING ──budget out / root null after 1 retry──▶ DONE-GRANTS-ONLY (UNPROVEN, never OFF)
any state ──press again──▶ no-op (re-entrancy guard; the key is print while running: CHECKING…)
```

Print grammar (one foot line + the warrant line it feeds):
`CHECKED {HH:mm:ss} · {grade} · {first failure or —}` where grade
∈ `WITNESSED` / `GRANTS ONLY` / `PARTIAL {n}/6`. Under the seal:
CHECK is measurement, not control — it launches nothing, toggles
nothing, opens no pane — so I propose it **stays live while
sealed** (the Night Porter's cheap half already accepts seal-time
truth prints; a sealed drawer that cannot even re-measure is P-8
again). Flagged for Hof/Ren to rule; the fallback (sealed = CHECK
prints last verdict only) costs nothing to adopt instead.

Total for the recommended composition: **M, ~250–330 lines**
across WatchStore, MonolithAccessibilityService,
MonolithRepository (anchors only), ParkDrawer, ViewModel — plus
tests. No DB change, no new permission, no new surface, no new
object on the board (CHECK prints as a ruled line with a margin
word, FIX remains the sole object per §8.10). Nobody compiles;
Ren compiles on Sarthak's word.

**What CHECK must never print:** `BLOCKING PROVED` (end-to-end is
#21), `RUNNING` upgraded by a CHECK (the interceptor words stay
derivations; CHECK witnesses their *parents*), any verdict
without its instant, and any failure word stronger than the
evidence (`UNPROVEN` is the ceiling for a failed pull; `OFF` is
reserved for directly observed dead signals, per the warrant's
existing print law).

---

## §8 · W-08 INPUT LIST — what the dynamic plate (batch 10) may honestly show

Ren's W-08 plate stops being byte-static. Every line below is a
fact the machinery *already senses*, its source, and the rule
that keeps the print true. Lines whose gate is unmet are marked
STRUCK — the plate omits them, it does not soften them.

| # | Fact (plate line candidate) | Source in code | Freshness rule |
|---|---|---|---|
| 1 | PEEK lease minutes — today the value is **2, a constant, not a setting**: no peek-minutes field exists in `GuardSettingsEntity` in this tree | Lease granted as `nowMono + 120_000` in `MonolithRepository` (peek grant, ~:1190); plate literal in `ParkDrawer.PLATE_BODY` | Static-true only while the constant is the lease. The day an editable peek setting lands, the plate must read that setting's row in the same commit or fall back to Hale's filed line `PEEK: A SHORT LEASE, THEN SHUT AGAIN` (W-08). Never print a number not read from the lease's own source |
| 2 | HOLD seconds + bank interval | Valve in `ui/views/BlockitSuitcaseAndSwitchesViews.kt` (hold total; bank = total/4 — Una A-11) | 60 s is Sarthak's standing verdict, not a setting (W-07): print is static-true until he changes the verdict; if the hold ever becomes editable, this line joins rule #1 |
| 3 | Emergency breaks remaining this week | `data/MonolithWeek.kt` (Monday-anchored `(1 − spends)`); spends booked only in `completeEmergencyBreak` (`MonolithRepository`) | Recompute at render; stale the instant a break completes or the week rolls (Mon 00:00). Never cache across a session end |
| 4 | ROUTINES — setting home only: **"FIRED AS SET" STRUCK** (W-11) | Rows/scheduler: `RoutineDerive.nextBoundary`, `MonolithModels.RoutineSchedule.nextStartMillis` | The struck clause returns only when Wache F1–F5 are in the same build **and** a live fire is re-verified on the phone. Until then the line is `ROUTINES: SET BY HAND ON SHIELD` — already the print in this tree |
| 5 | Next routine boundary (the honest replacement for a firing promise): `NEXT: {NAME} {HH:mm}` or `NO ROUTINE SET` | `RoutineDerive.nextBoundary` over enabled ∧ `daysMask != 0` routines (UNA-09: the header's `enabled`-only count is a lie the plate must not inherit) | Derived at render from the live routines flow; a routine edited on Shield re-derives it. Prints WOULD, never WILL (Tomorrow Dry-Run's tense law) |
| 6 | Routine heartbeat health: last tick instant | `GuardSettingsEntity.lastRoutineTickMillis` (persisted, survives process death) | Tick cadence is ~30 s (`RoutineHeartbeat` via shield loop / opportunistic ticks). Age > ~5 min with routines set = print `HEARTBEAT STALE {HH:mm}`; the tick's existence is a fact, its silence is a fact — print either, never neither |
| 7 | SITES count — the enforced list's size | `dao.getAllSitesSnapshot().size` — post-UNA-06 the stored list *is* the enforced list (no factory fallback in `MonolithAccessibilityService`) | Room emission freshness; 0 is a true print (`0 SITES` = blocks no sites). Re-verify the no-fallback invariant at every merge that touches the service — if the fallback ever returns, this line becomes false at exactly 0 |
| 8 | APPS split: `{n} SHUT · {m} PEEK` | `MonolithViewModel.appsSplit` over `allAppsFlow` resolved tiers (UNA2-02) | Null = UNKNOWN (no Room emission yet) — the plate prints nothing for the line, never 0/0. Live flow; no staleness window needed |
| 9 | GUARD count + warrant word + LAST PROVED | `WatchStore.countedWarranted` / `deriveWarrant` via `MonolithRepository.standingWarrant()`; `WatchSnapshot.lastProvedWallMillis` | Print through `WatchStore.printState(warrant, nowMono)`: a stale LIVE degrades to UNPROVEN (UNA2-03). LAST PROVED advances only on a complete six-set (complete-set rule, acquitted). After §7 lands, a CHECK verdict line (grade + instant) joins this family — it decays by the same 45 s / §5 TTL rules |
| 10 | Engine/shield state | `WatchSnapshot.engineProtecting` + `engineHeartbeatMono/Wall`; persisted anchor: session row `shieldLastBeatWallMillis` (`MonolithModels`, ~:351) | In-memory heartbeat: age from mono stamp; > ~5 s old mid-session = loop not turning, print age not adjectives. Cold process: only the persisted beat may print, as `LAST BEAT {HH:mm}`, and a beat predating `lastBootAtMillis` prints as dead (§5) |
| 11 | SOUND on/off | `service/SoundEffectsSetting` (persisted prefs; every engine reads it — Truth Car 3 cure landed in this tree) | Persisted fact; no decay. Read at render from the setting, never from an engine instance's var |
| 12 | Shorts + Reels setting on/off | `GuardSettingsEntity.shortsReelsSniperEnabled` (DB v12 column; service reads it per event via `shortsSniperArmed`) | Persisted fact; default-true on a missing row is the truthful pre-v12 value and the only default the plate may assume |
| 13 | Browser/social sites setting on/off | `GuardSettingsEntity.browserBlockerEnabled`; service honours the stored value per event | As #12 |
| 14 | THEME readout | `MonolithViewModel.evaluateTheme` — the same evaluation that drives the colour scheme (Una A-05) | Evaluation freshness (session/routine boundaries); the plate prints the evaluation's string, never composes its own |
| 15 | DATA: ON THIS PHONE ONLY | Manifest: `android:allowBackup="false"` (verified in this tree, ~line 30) + no INTERNET permission + no network code | Not a runtime fact — a *build* fact. Rule: any manifest/backup-rules change re-opens UNA-04 and strikes the line until re-verified. Print only in builds where the check has run |
| 16 | STUDY HOURS: IN YPT | Standing division of record (W-13 replacement already in this tree's plate) | Static-true as long as Monolith keeps no hours ledger of its own; if session history ever becomes an hours record, the line re-opens. (Sessions/drawer facts are the Drawer's prints, not the plate's) |
| 17 | Last real intercept / last capture (historical L3, quotable per §1) | `InterceptLogEntity.timestampMillis`; `CapturedNotificationEntity.timestampMillis` (Room) | Wall-clock history: prints as `LAST STOP {HH:mm}` / `LAST CAPTURE {HH:mm}` with the date when not today. Never re-worded as present capability; absence prints `NONE ON RECORD`, not a blank |

**Excluded on purpose:** session elapsed/remaining (the AOD and
Deck own session time; the plate is furniture about the
*machine*, and a plate line that needs a session to be true is a
strip line wearing furniture's clothing); battery percentage and
other phone-telemetry (sensed nowhere in-tree for this purpose —
`BATTERY` on the board is the *exemption grant*, not a level);
anything from the Live-Fire Strip (#21).

---

## §9 · CLOSING — what I want to do next, unassigned

Sit in the critic round on the CHECK builder's degradation table
before it merges — the UNPROVEN-never-OFF boundary is one
careless `else` away from becoming the next P-2. And if M3's
`STATS DARK` pilot ships, I want the first week's false-positive
read: if the qualifier cries wolf on his phone, it dies by my
hand, not the audit's.

— Innovator, issue-9 cell (for Hof)
