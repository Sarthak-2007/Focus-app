# LIE REGISTER 3 — THE MERGED TRUNK

*Filed by Hale, Critics coordinator, Oct 29 2026, under Pipeline
Law 9 (WORK-QUEUE item 2). The ground was the merged trunk only —
~/workspace/clone-inspect/Focus-app/ as Ren's hand merge left it:
the token ledger (DB v8), the counterfoil tear, the Live Wire
substrate, and the desk-clock span fix. Lane reports were not
evidence; every charge below quotes the trunk's own files. Six
hunters, both squads: Maas (measure), Beleg (receipts), Zweifel
(doubt), Moss (motion), Falk (provenance), Una (consistency).
Full evidence in plan/lie-register-3-parts/ (2,462 lines).*

**Tally: 55 charges — 11 S1 · 21 S2 · 23 S3 — and 46 acquittals.**
*Severity is the seat's where filed; † marks a coordinator grade
where the seat filed ungraded.*

---

## THE VERDICT REN ASKED FOR: THE DESK-CLOCK SPAN FIX

Ren's 0941 letter claimed: at break start the repository stores
elapsedSeconds = effectiveTarget, per-block remaining was 0 for
the whole break, and his span derivations now count breaks as
session time so the clock no longer reads 00:00.

**The claim is TRUE — verified in the trunk, acquitted on the
evidence (Maas A-1/A-2, coordinator's own reading concurring).**
At P = 0 the span is exact: spanRemaining 4200 at break start,
3900 mid-break, continuous across block→break, degenerating
exactly for single-block sessions, never read for stopwatch.

**The fix is nevertheless half a verdict.** Its arithmetic turns
on the penalty it under-counts (M-1, M-2), and the break input
feeding it in the trunk is a per-call counter, not a clock
(M-3). "Fixed" is correct for the reading. The clock feeding the
reading still does not measure time.

---

## S1 — CHARGES THAT LIE TO HIS FACE OR BREAK THE RECORD

**LR3-S1-01 · F-1 (Falk)** — Database v8 registers exactly one
migration, creating exactly one table. The `blocked_sites`,
`routines` and `guard_settings` tables have no DDL anywhere in
the tree; the destructive fallback was deleted in this same
merge; exportSchema=false leaves no schema to audit; and
TokenMigrationTest's "v7" is a hand-made two-column table opened
through a raw callback that bypasses Room validation. Any
install older than exactly v7 has no lawful path to v8 and
throws on open — and no test can see it. *Verified by the
coordinator in MonolithDatabase.kt directly.* (falk.md)

**LR3-S1-02 · F-3 (Falk)** — The fabricated seed history still
lives in the merged trunk: three invented sessions with forged
artifact IDs, invented interception counts, invented task
summaries (MonolithRepository.kt, seed block, inserts at
L279/298/317). LR2-S1-2 was ruled dead and ordered scrubbed;
the scrub exists only in Ruth's unmerged drawer lane.
*Verified by the coordinator in the seed block directly.*
(falk.md; converged by Una's closing note.)

**LR3-S1-03 · MO-1 (Moss)** — Task completion is a TOGGLE with
no latch downstream: the repository's complete path
(MonolithRepository.kt:1441-1450) un-completes a COMPLETED task
back to QUEUED, and the un-complete branch never retracts the
session summary/count written at completion (L1464-1475). A
double fire does not duplicate the record — it resurrects the
task beneath a record that still counts it, and a re-tear books
it twice. The only guard is a `remember {}` inside a recyclable
list row. (moss.md; converged by U-6.)

**LR3-S1-04 · MO-2 (Moss)** — Order lie in the tear: completion
fires after `delay(180)` (FocusSequencerScreen.kt L366-371)
while the dash settle runs 260ms (L361-365). The last 80ms of
every tear is amputated; the dashes never reach half before the
leaf vanishes — against the ruled "ONLY THEN" and the file's own
KDoc (L325-327). (moss.md)

**LR3-S1-05 · MO-3 (Moss)** — The tear is losable: the pending
completion is a composition-scoped `LaunchedEffect` behind a
plain `remember(task.id)` (L355). LazyColumn recycling, a
mid-tear CUE tap, or a configuration change cancels it — pulse
spent, stub fallen, task never completed, no trace anywhere.
(moss.md)

**LR3-S1-06 · Z-1 (Zweifel)** — The Live Wire root stamp is
never cleared: `lastRootMono` has one writer and no reset in
onServiceConnected, onUnbind, or onDestroy
(MonolithAccessibilityService.kt:79; LiveWire.kt:80-83). A
rebound service inherits its dead predecessor's stamp against
the unchanged residency start and `liveWarrant()` can read LIVE
before the new binding processes a single event. (zweifel.md)

**LR3-S1-07 · Z-2 ≡ F-2 (Zweifel, Falk)** — The verdict has no
reader: in the merged trunk `liveWarrant()` has zero callers and
`LiveWire.snapshots` zero collectors (whole-tree grep, both
seats), while comments claim "the slide reads it at DOWN and
again at the gate crossing" (MonolithRepository.kt L567-570)
and "the strip and the chrome Guard line render this"
(LiveWire.kt L55-58). The arm path still reads the Settings
string the substrate was built to replace. Every poke updates a
flow nobody reads. This bounds S1-06 today — and expires the
moment the slide build connects. (zweifel.md, falk.md)

**LR3-S1-08 · M-1 (Maas)** — `spanTargetSeconds`
(MonolithModels.kt:296-298) counts `penaltySecondsAdded` once;
the repository enforces it per block
(MonolithRepository.kt:1012, 1083) and it carries across blocks
(the next-block `copy()` never writes it). Worked case n=3,
T=1500, P=900: true session 8400s, span target 6600s;
spanRemaining understates true remaining by (n−k)·P — 1800s in
block 1, 900s in block 2, exact only in the final block.
(maas.md; a second seam of the same root filed as F-15.)

**LR3-S1-09 · M-2 (Maas)** — spanElapsed is discontinuous at
the break→next-block transition: Δ = 1 − P exactly. At P=0 a
harmless +1s (the rem=0 row is never persisted); at P=900 the
desk clock's elapsed jumps BACKWARD 899 seconds and its progress
bar regresses ~13.6 points the instant the block after a
penalised block begins. (maas.md)

**LR3-S1-10 · U-3† (Una)** — Task "actual seconds" are
tick-count, not wall-clock: `actualSecondsSpent + 1` per
`updateSessionProgress` call (MonolithRepository.kt:1355),
driven by two trunk tickers (ViewModel 1000ms, ShieldService
2000ms) — roughly 90 booked seconds per 60 real seconds.
LR2-S2-24, charged against the plan, is alive in the merged
trunk, and it is the raw material of every "actual" the Drawer
will ever print. (una.md)

**LR3-S1-11 · U-8† (Una)** — `confirmDisarmSession`
(MonolithViewModel.kt:951) is an unauthenticated free disarm:
it checks nothing, defaults the reason to "Ended Early", and
spends no token. The grace window exists only as a Deck
rendering condition (elapsed ≤ 10), and the repository decides
grace by substring — `contains("Grace Cancel")`
(MonolithRepository.kt:1277). Today its only caller is the
grace chip; the function itself would disarm anything, any
time, for free. (una.md)

---

## S2 — CHARGES THAT LIE QUIETLY OR ARM A FUTURE LIE

**LR3-S2-01 · M-3 (Maas) ≡ F-4 (Falk) ≡ U-4 (Una)** — In the
trunk the Safe Break drains 1 second PER CALL
(MonolithRepository.kt break branch), called by two tickers at
1000ms and 2000ms: a 600-second break ends in ~400 wall
seconds, and stalled tickers freeze the break entirely while
block time is clock-derived. No `breakRemainingAfter()` exists
anywhere in the trunk — Ruth's wall-derived fix is unmerged.
(maas.md, falk.md, una.md — three seats, one seam.)

**LR3-S2-02 · M-4 (Maas)** — A tamper penalty first detected
DURING a break is computed (Repository L1011) and then discarded
by both break writes; the next-block write re-signs a fresh
HMAC over the laundered state. The penalty lock has a
break-shaped hole. (maas.md)

**LR3-S2-03 · B-1 (Beleg) ≡ U-1/U-2 (Una)** — The ledger flow
is born lying: `stateIn(…, 1)` at MonolithViewModel.kt:152,
directly beneath a comment declaring the old
MutableStateFlow(1) dead, plus a second hardcoded `1` as the
Deck composable's parameter default (TactileDeckScreen.kt:115).
On a spent week, a cold start renders the live valve and the
"1/1 WEEK" badge until the ledger's first emission lands; only
the repository's refusal tells the truth. (beleg.md, una.md)

**LR3-S2-04 · F-5 (Falk) ≡ B-4 (Beleg)** — A token COUNT
reaches the UI in numerals on the Deck chip that opens the
break: `"$weeklyEmergencyTokensRemaining/1 WEEK"`
(TactileDeckScreen.kt:522) — on the surface whose valve was
ruled number-free. (falk.md, beleg.md)

**LR3-S2-05 · F-6 (Falk)** — The old Boolean
`hasUsageStatsPermission` still drives the Guard switch and
readiness count, collapsing UNSET into DENIED on every rendered
surface, while the honest tri-state (`usageAccess()`) sits
beneath it unused by those surfaces. (falk.md; the tri-state
itself acquitted, Zweifel A / Falk A-9.)

**LR3-S2-06 · F-7 (Falk)** — "ONE derivation of session time"
is claimed in the models' KDoc and violated by two of three
surfaces: ShieldService re-derives remaining inline
(L101-102), the repository re-derives at L1012, and Deck and
desk clock run opposite-direction bars from different
quantities. (falk.md)

**LR3-S2-07 · MO-4 (Moss)** — The falling leaf's other controls
stay live and writable during the tear — sub-steps (L454),
SPLIT (L506), CUE (L515-518), DEL (L526) carry no `!tearing`
gate; DEL mid-fall makes the delayed completion fire on a
deleted task. (moss.md)

**LR3-S2-08 · MO-5 (Moss)** — The sealed leaf's tear is truly
absent (acquitted), but SPLIT/CUE/DEL/sub-steps remain enabled
during a sealed session, and SPLIT/CUE reach `playHarmonicSeal`
in the ViewModel (L722, L740) — the sealed screen is not
gestureless and not mute. (moss.md)

**LR3-S2-09 · MO-6 (Moss)** — A tearless second completion
entry is wired live: `toggleChecklistItem`
(MonolithViewModel.kt L521-523), bound at MainActivity.kt:317
and directly on the Deck at L319 — completion with no tear, no
pulse, no sealed check. (moss.md)

**LR3-S2-10 · U-6 (Una)** — Un-completing a task never retracts
its session summary entry or count; re-completing writes it
twice. Session record and task rows diverge permanently.
(una.md; the downstream half of MO-1.)

**LR3-S2-11 · U-9 (Una)** — `totalFocusedMinutes` is a
VM-invented derivation with a fabricated floor (35) and a
fabricated initial (75); the repository's derivation of the
same fact has neither. (una.md)

**LR3-S2-12 · U-10 (Una)** — `totalInterceptions` takes the
maximum of two different counters and seeds it at 6; the
repository derives interceptions a third way (phantom sums).
(una.md)

**LR3-S2-13 · U-11 (Una)** — Completed-task counts live in two
homes (VM `completedTasks.size` vs session
`tasksCompletedCount`) that provably diverge on no-session
completion, un-complete, clear, and re-complete — while
`completedTasksSummary`, the repository's own per-session
record, has zero references in the VM and is never surfaced.
(una.md)

**LR3-S2-14 · Z-3 (Zweifel)** — The snapshot's stamped leg is
unmasked (LiveWire.kt L85-89 uses `stampedThisResidency()`
bare) against the warrant's bound-masked form
(Repository L580): after service death the snapshot can hold
bound=false, stamped=true indefinitely. (zweifel.md)

**LR3-S2-15 · Z-4 (Zweifel)** — `residencyStartMono`
initialises to 0L (LiveWire.kt L71-72); with the `stamp >=
start` test, any stamp from before any residency note counts
as "this residency." (zweifel.md)

**LR3-S2-16 · Z-5 (Zweifel)** — LiveWire's KDoc claims the
residency start is "noted on app resume"; the only caller is
the Deck LaunchedEffect (MainActivity L150-154). The ON_RESUME
observer (L201-209) calls only `refreshSystemGuard()` — stamps
survive an app return unrefreshed. (zweifel.md)

**LR3-S2-17 · Z-6 (Zweifel)** — Deck re-entry invalidation
works by design, but the effect-scheduled note can land after
the new visit's first stamps, retro-invalidating them, with no
guaranteed re-stamp; the `>=` comparison on a millisecond clock
adds a 1ms false positive at every boundary. (zweifel.md)

**LR3-S2-18 · Z-7 (Zweifel)** — The Shield monitor loop
(ShieldService L72-150) has no try/catch: one exception kills
monitoring without stopSelf(), leaving `protecting` frozen true
forever — or never true if it dies before L118. Folded: the
"full monitor iteration" comment (L114-116) overstates a flag
set mid-iteration, before the foreground scan. (zweifel.md)

**LR3-S2-19 · M-5 (Maas)** — The AOD progress clamp fabricates
at both ends: the 0.04f floor shows 4% at elapsed 0, and the
stopwatch bar (elapsed/1200, clamped at 1f) sits permanently
full past 20 minutes while its numerals keep counting.
(maas.md; the Deck's twin literals filed as F-9.)

**LR3-S2-20 · U-5 (Una)** — The session summary floors a
task's actual to "1m": a task completed with zero accrued
seconds is written into the permanent record as one minute of
work. (una.md)

**LR3-S2-21 · U-7 (Una)** — A ticker tick silently promotes
the first QUEUED task to ACTIVE — the machine reorders his
queue as a side effect of counting. (una.md)

---

## S3 — SMALL LIES, DEAD LETTERS, LOOSE THREADS

- **B-2 (Beleg)** — `completeEmergencyBreak()` transacts on a
  re-fetched singleton (Repository:1225), not the database its
  DAO belongs to: atomicity by wiring convention, not
  construction. (beleg.md)
- **B-3 (Beleg)** — The one-per-week invariant has no schema
  teeth: no UNIQUE constraint backs the in-transaction check.
  (beleg.md)
- **B-5 (Beleg)** — The 60-second week-tick poll lets an open
  overlay render last week's count up to ~60s past Monday
  00:00, against its own KDoc's "never"; enforcement itself is
  never stale. (beleg.md)
- **F-8 (Falk) ≡ M-6 (Maas)** — The break duration is law in
  three forms: the named constant (Repository:95), three bare
  600s inside the span formulas (MonolithModels.kt:298/302/305,
  same package), and a bare ×10 in the Deck (L142). One fact,
  three sources. (falk.md, maas.md)
- **F-9 (Falk)** — Literal progress outside the desk clock: the
  Deck's idle dial divides by 175f; both stopwatch bars divide
  by a hard-coded 1200f despite the named constant. (falk.md)
- **F-10 (Falk)** — The killed AES-GCM export is dead as code
  and alive as commentary in the CurioVault screen, which still
  describes an export that does not exist. (falk.md)
- **F-11 (Falk)** — The AOD file's comments document both hero
  numerals as "Space Mono, 112sp" (Airlock L159/537); the code
  renders AnonymousPro at 96.sp. (falk.md)
- **F-12 (Falk)** — Repository KDoc caps study cycles at "1 to
  4 blocks"; the code coerces to (1, 8). (falk.md)
- **F-13 (Falk)** — Dead imports left by the merge in two of
  the four seam files (Airlock: Spacer/NightBlockBarSurface/
  NightColorPrimaryContainer; Repository: the org.json pair).
  (falk.md)
- **F-14 (Falk)** — The readiness count is implemented twice;
  one copy is dead code. (falk.md)
- **F-15 (Falk)** — The second seam of the S1-08 root: after a
  penalised first block the desk clock overstates remaining by
  the full penalty, because the span cycle excludes what the
  span target includes once. (falk.md)
- **MO-7 (Moss)** — The completion pulse is counted per
  tear-attempt (fires at t=0, Screen L377), not per completion;
  the counts diverge exactly where MO-1/MO-3 bite. (moss.md)
- **U-2 (Una)** — filed within S2-03 above. (una.md)
- **U-12 (Una)** — `totalQueueMinutes` floors an empty queue at
  a stale snapshot of the dial, read inside a `tasks` map.
  (una.md)
- **U-13 (Una)** — The checklist invents three tasks whenever
  the real queue is empty — placeholder work wearing the
  queue's clothes. (una.md)
- **Z-8 (Zweifel)** — `WarrantState.UNPROVEN`'s doc omits its
  usage-UNSET cause and mislabels the state as first-run-only.
  (zweifel.md)
- **Z-9 (Zweifel)** — All registry fields are @Volatile, but
  every composite read (`snapshot()`, `stampedThisResidency()`,
  `liveWarrant()`) is torn: a verdict can be assembled from two
  different moments. (zweifel.md)

---

## CONVERGENCES — WHERE INDEPENDENT SEATS STRUCK THE SAME SEAM

1. **Time is counted, not measured** — M-3 / F-4 / U-4 (break)
   and U-3 (tasks): three seats, two surfaces, one disease.
   The trunk's clocks tick by function call.
2. **The number-free token leaks its number back** — B-1 /
   U-1 / U-2 (a born-`1` flow and a born-`1` default) and
   F-5 / B-4 (the count printed on the Deck chip).
3. **Live Wire is merged, praised, and unconsumed** —
   Z-2 / F-2, whole-tree grep, twice.
4. **The span and the penalty disagree** — M-1 / M-2 / F-15:
   one root (penalty counted once, enforced per block), three
   filings.
5. **The toggle beneath the tear** — MO-1 / U-6: fired twice,
   the record corrupts from both ends.
6. **One fact, several sources** — F-8 / M-6 (the 600),
   F-7 / U-9 / U-10 / U-11 (session facts re-derived per
   surface, each with its own drift).

---

## ACQUITTALS — RECORDED WITH THE SAME CARE

**The token ledger's core (Beleg A-1…A-6; Falk A-3/A-6; Una
A-U1 architecture, A-U4, A-U5)** — Exactly one production insert
call site inside the tree's only `withTransaction`, ordered
session check → token check (`>= 1`) → insert → disarm
dispatch. RefusedNoToken returns before any write; the VM's
refusal branch is literally empty and the session stays sealed.
The anchor is computed from the completion instant inside the
transaction and stored on the row; remaining is always
`(1 − rows).coerceAtLeast(0)`; the DAO spend surface is one
bare @Insert and three SELECTs — no update or delete path
exists anywhere. Migration(7,8) matches the entity
column-for-column and is registered; `fallbackToDestructiveMigration`
survives only as its own obituary comment. Sunday 23:59 and
Monday 00:01 IST land in different weeks, anchors exactly
604800000ms apart (worked in full). At zero tokens the valve is
not composed at all — refusal is structural. The VM calls the
single repository transaction for the break and sounds only
after commit.

**The span at P = 0 (Maas A-1…A-6)** — Block→break is
continuous; Ren's stored-fields claim is true; during a break
spanRemaining equals break remainder plus all remaining blocks
and breaks; single-block sessions degenerate exactly onto the
per-block derivations; the span is never read for stopwatch
sessions on any AOD branch; spanRemaining cannot go negative
and spanElapsed never exceeds spanTarget in any reachable
stored state; the mid-break formula is shaped correctly.

**Live Wire's design spine (Zweifel A-1…A-10; Falk A-9)** —
The stamp block precedes all 12 returns in onAccessibilityEvent,
tabled path by path. The shield's `protecting` flag is truly
excluded from the pre-arm conjuncts and read only post-verdict.
OFF holds exactly when (no bind) ∨ (usage DENIED), full truth
table filed; bind-alive + usage UNSET → UNPROVEN is the honest
state. All five registry fields are @Volatile with
identity-guarded teardown; every registry write that exists is
followed by a poke(); process death forges nothing — both
statics reset together behind a `stamp > 0L` guard.
`usageAccess()` and `liveWarrant()` themselves are honestly
built.

**The tear's craft (Moss MO-A1…A8)** — Double-tap inside one
composition is triple-gated. The motion numbers as drawn are
exact (10dp drop, 180ms fade, dashes 3dp→1.5dp with the gap
untouched). Completion is keyed by task.id, never by list
position. The tear path itself writes no storage before the
delay expires. The sealed leaf composes no tear label at all —
no `enabled=false` ghost — over a solid overprint. The
ViewModel's seal sound on the completion path is genuinely
removed, as ruled (echoed by Una A-U2). The held blank carries
no tap target. The label is character-exact: "DONE — TEAR".

**The merge's hygiene (Falk A-1, A-2, A-4, A-5, A-7, A-8)** —
The fabricated 0.65f is dead on the desk clock (its only
survivor is a scrim alpha). "Laid Seal" language: zero hits.
The AES-GCM export code is genuinely dead; HMAC remains,
alone. No duplicate or wildcard imports, no same-simple-name
classes anywhere in the tree. The AOD revert left no orphaned
branch and no unused locals. The week anchor is single-sited
in MonolithWeek. The database code carries no v7-era TODO,
fallback, or counter-world language in the ViewModel (Una
A-U7) — the counter world survives only functionally, in
initial values, which is where S2-03/S2-11/S2-12 charge it.

---

## NOT HUNTED (outside this register's ground)

The routines rebuild and the Drawer build exist only in
unmerged lane trees at filing time; nothing here acquits or
charges them. The slide build (Marlowe's lane) was in flight;
its consumption of Live Wire is exactly where S1-06 and S1-07
stop being latent. Device behaviour — real haptics, real
blocking, real upgrade on his phone — remains, as ever,
unproven by any reading of code.

— Hale, Critics coordinator, for Maas, Beleg, Zweifel, Moss,
Falk and Una.
