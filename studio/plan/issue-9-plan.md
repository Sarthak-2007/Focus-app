# ISSUE #9 — PARK (main-app pass): cell plan + dispositions

*Cell lead: Hof (author of AUDIT-park.md). Branch: agent/issue-9.
Everything here is DECLARED — nobody in this cell compiles; the
verdict belongs to Ren's build on Sarthak's word.*

## The three items, as found and as left

### 1 · PARK never re-proves → the CHECK verb (audit P-1, merge §4.1) — FIXED
Found: sweeps fired on Deck entry, Deck resume, FIX return only.
Opening PARK fired `refreshSystemGuard()` — a diagnostic whose
result, in MainActivity's own words, "renders NOWHERE". The board
read whatever an earlier surface happened to measure; grants
decayed GRANTED → STALE in front of the reader with no verb.

Left:
- `MainActivity`: the parkOpen effect now ALSO calls
  `viewModel.runWatchSweep()` — the sweep the board actually
  reads. Every open path (rail cap, PARK re-tap, first-run
  auto-open) passes through `parkOpen`, so none bypasses it.
- `ParkDrawer`: a CHECK key — the board's second object, FIX's
  grammar (48dp, ink fill, radius 8, seat knock on press, 1dp
  dip). Present whenever the drawer is operable, including
  never-measured (where it is the only honest move on the
  surface); absent under the seal, where the drawer is print.
  Its CHECKING state is a derivation, never a timer: CHECKING
  until every permission item carries a measurement stamped at
  or after the press, whoever stamped it.
- A whole landing plays the firm click (the FIX return's voice
  for a repaired line). An unwhole landing prints a persistent
  verdict line under the key — `NOT PROVED` + the first unproved
  counted door, in the board's own row grammar — clearing on the
  next press, on close, or whenever the board reads whole by any
  road. (Designer's failed state, adopted with one correction:
  her draft wording blamed a door that "did not answer". Every
  door answers; MISSING is an answer. The line names the
  measured lack and claims nothing more.)

### 2 · Cold-open Guard line blank → `GUARD — NOT CHECKED` (P-6, §4.5) — FIXED
`GuardLine.kt`: both unmeasured paths (null snapshot; any
counted item UNMEASURED) now return the NOT CHECKED print
instead of null. The doc comment records why the silence died:
the old blank existed to prevent a fabricated count, but blank
read as FINE to a tired man — a blank is a claim too. The line
now names its own ignorance and still assumes nothing.
DECLARED tests: `GuardLinePrintTest` pins all four prints
(NOT CHECKED ×2, 4/4, named lack, named stale lack).

### 3 · `FIRED AS SET` on the plate (P-7 / W-11) — ALREADY STRUCK on main; verified
No code change in this cell, because the merged tree already
carries the cure: PLATE_BODY prints "ROUTINES: SET BY HAND ON
SHIELD" and the UNA-05 comment in ParkDrawer.kt documents the
strike and its return conditions (Wache's F1–F5 in the same
build AND a live fire re-verified). A repo-wide grep finds the
sentence only in that comment and in paper. Finisher 3 owns the
full grep census as a checklist. Nothing was "earned" back:
no F1–F5, no re-verified live fire — the sentence stays off.

## W-08 (dynamic PARK plate, batch 10) — inputs prepared, plate NOT built
Ren's plate lands in batch 10; this cell prepares only its inputs:
- `peekMinutes` (Designer's find, the plate's PEEK line): today
  the plate prints a hard-coded 2 minutes while the lease reads
  the `PEEK_DURATION_SECONDS = 120` constant — one fact, two
  sources, already a lie waiting for Frist's editable times
  (batch 10) to make it print a stale number. Input contract:
  ParkDrawer takes the same value the lease reads (post-Frist:
  the `guard_settings` flow PARK already consumes), null → the
  plate omits the number rather than guessing, 0 → OFF form,
  1 → singular.
- Live-state facts the plate may honestly draw on, all already
  in the WatchSnapshot the drawer receives: the warrant word
  (LIVE/OFF/UNPROVEN via `WatchStore.printState`), the counted
  warranted n/4, per-item prints + stamp ages (measuredMono /
  measuredWall), sweepGen, the engine heartbeat + protecting
  flag, LAST PROVED wall. Freshness rule for all of them: a
  fact older than the board's own 45s stale threshold prints
  as history (a time), never as a present-tense claim.

## Scope discipline (what this cell did NOT do)
- The Live-Fire Strip: parked (issue #21) — not designed, not
  built; CHECK's copy register bars its language.
- The wider §4 merge list (warrant line §4.2, headline count
  §4.3, age column §4.4, FIX staircase §4.6, tiers §4.7,
  colophon §4.8, plate cut §4.9, moves §4.10, padding §4.11,
  doorLocked gate §4.12) belongs to the pass that owns it —
  Finisher 5's checklist keeps the boundary explicit. This
  branch carries §4.1 + §4.5 + the P-7 verification only.
- `ParkDrawer`'s new `onCheckClick` parameter has exactly one
  host (MainActivity) and no default — a second host cannot
  silently seat the drawer without wiring the verb.

## Addendum — finish patches adopted (all DECLARED, same branch)
- **F-B2/F-B3 (Finisher 1):** the board's word ink is a
  three-tier enum (`ParkWordInk` QUIET/HALF/FULL) replacing the
  lack Boolean; STALE takes the half-ink (P-4's dress half —
  it rides with this issue because a check landing on STALE
  rows must read honestly; §4.4's age column stays with the
  wider pass). Every row derives word + ink from ONE
  `permPrintAt` per frame — no more boundary tearing.
- **Finisher 2:** dip animators on both keys (90 SEAT in,
  160 THROW back); FIX entry corrected to the audit's 280 SEAT;
  CHECK's dress ladder built (print dress on a fresh board,
  ink when STALE/UNMEASURED/in-flight — MISSING never promotes
  it); key-zone spacing on the {8,16,24} module; maxLines=1 and
  spoken names on both keys.
- **F-G4 (Finisher 4):** the Guard line is unblankable in TYPE,
  not just in fact — `guardLineFor` is total, the flow's initial
  value is the NOT CHECKED print (the cold-open blank window is
  dead), `SealFreeze.guardLine` is non-null, and the band's
  null gate is gone. No printed string changed.
- `ChromeWaveUnitTest`'s old unmeasured-prints-nothing test
  now asserts the NOT CHECKED print in all three shapes.

## Addendum — the Critic's charges, ruled (issue-9-critic.md)
- **C-1 (S1) — CURED in this branch.** CHECKING's first form
  read raw stamp times; the shield loop and an earlier sweep
  could answer a press. Now: `WatchSnapshot.lastCompletedSweepGen`
  + `WatchStore.currentSweepGen()` — a press is answered only
  by the completing set of the sweep it dispatched, in parity
  with LAST PROVED. DECLARED tests pin both halves.
- **C-2 (S1) — ANSWERED.** A query that never returns leaves
  the key at CHECKING (Finisher 2's law stands: no timer may
  declare anything) — but the verb is its own escape: a press
  while CHECKING re-dispatches and re-bases the generation,
  and closing the drawer abandons the key's claim while the
  sweep completes in the store regardless.
- **C-3 (S1) — CURED.** The Guard line's derivation now rides
  a 5s guard clock in the VM combine, so a warranted 4/4
  cannot outlive its 45s threshold on a still screen. The
  seal-freeze branch is untouched (its tense is the seal's).
- **C-4 (S2) — RULED AGAINST, on the record.** The freeze
  holding a seal-frame NOT CHECKED is the freeze's tense
  (Designer §2.3 + Finisher 4 §4 concur), and sealed
  read-only is a §7 acquittal. The compound concern — doors
  dying mid-session (P-8) — is real and its cure is the
  seal-time warning line (§4.12, wider pass), not a live key
  under the seal. The Innovator's "CHECK stays live while
  sealed" is NOT adopted; Finisher 2's absence ruling stands
  over the Designer's inert-print seat. Three papers, one
  ruling, recorded here so no builder inherits by accident.
- **C-5 (S1) — CURED.** The complete-set rule now requires
  coexistence: the set's span must fit the board's own 45s
  freshness law, and a sweep past its window is abandoned
  (late stamps remain ordinary measurements). LAST PROVED's
  print now carries its date when it is not today.
- **C-6 (S2) — STANDS, escalated.** Interceptor RUNNING has
  no stale form; `poke()` bypasses the derivation it claims
  to refresh; interceptor stamps are re-dated at publish.
  Not cured here: the derivation feeds the Deck warrant, and
  a derivation-law change needs its own verifier. Filed for
  Ren to route — it is P-2's machinery half.
- **C-7 (S2) — STANDS, pre-flagged.** Presence-proved BIND
  doors (the audit's own RESIDUE) and NOTIFICATIONS'
  never-captured RUNNING. The L2 cure is the warrant car's
  `provePipelineNow()` territory, not this branch.
- **C-8 (S2) — STANDS, and amends the Innovator.** The
  residency stamp precedes the own-package filter, so LIVE
  can rest on Monolith's own windows. M2 (provoked pull) is
  buildable honestly only with a SEPARATE pull stamp — the
  Innovator's recommendation carries this amendment.
- **C-9 (S2) — CURED.** A thrown query stamps nothing; the
  item keeps its old word and age; the set cannot complete
  on it. (This also corrects the record: "every door
  answers" is true only at a completion, by construction.)
- **C-10 (S2) — CURED in paper.** `FIRED AS SET` stood
  unmarked in MASTER-PLAN, CHROME-BUILD-READY, teen.md and
  two seat drafts; all five now carry the strike and the
  W-11 return conditions at the site. Item 3's closure rests
  on the app grep AND this paper census (Finisher 3).
- **C-11 (S3) — RULED deliberate.** Two wholes, named:
  CHECKING completes on the PROVED SET (six — LAST PROVED's
  own definition); the verdict click and the NOT PROVED name
  use the COUNTED FOUR (the Guard's definition — the doors
  the warrant gates on). The code comments now say which
  whole each site means.

## Cell roster
Lead: Hof. Wave 1: Designer, Innovator, Critic, Builder —
all delivered (issue-9-designer / -innovator / -critic /
-builder .md). Expansion (Sarthak's order via Ren):
Finishers 1–5 and Feel 1–5 — all delivered (issue-9-finisher-*
and issue-9-feel-* .md). Fourteen seats, fourteen filings.

— Hof, cell lead, issue #9
