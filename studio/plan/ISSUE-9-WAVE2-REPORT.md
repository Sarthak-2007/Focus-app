# ISSUE #9 — WAVE-2 REPORT (Park audit fixes)

Lead: VOGT, cluster C, flat under Ren. Branch `agent/issue-9`,
cell `builds/cells/issue-9`. Base wave-1 commits
`7387f21 / 7c8c6c6 / 6237468`.
**DECLARED throughout — nobody compiled, no tests run.**
Standards: ARM-fast · HOLD-absolutely · TELL-THE-TRUTH.

## Wave-1 state found
§4.1 CHECK verb + re-sweep on open, §4.5 `GUARD — NOT CHECKED`,
P-7 `FIRED AS SET` verified struck — all present as filed. The
rest of the §4 merge list (§4.2–§4.4, §4.6–§4.12) was explicitly
left to "the wider pass" by the wave-1 cell. Wave-2 is that pass.

## THE STRAY BUILDER CLONE — KILLED, with reason
`builds/cells/issue-9-builder` held uncommitted changes to
`GuardLine.kt` (+ a test): a `GUARD_NOT_CHECKED` constant whose
two return paths print exactly `GUARD — NOT CHECKED`. **Verdict:
KILL as superseded, do not fold.** The main cell landed the same
print in `7387f21` and then went past it (7c8c6c6's F-G4 made the
line unblankable in TYPE — total function, flow initial value,
non-null freeze field). Folding the stray would only rename a
constant and re-open settled code. Its full diff is preserved
at `studio/plan/issue-9-builder-stray.patch` on this branch; the
clone itself was moved to recoverable trash
(trash_id 20261029-223643-3ede02e67bdc4b499dd72de1e448b663,
restorable until 2026-11-28).

## FIXES LANDED (this branch, DECLARED)
1. **§4.2 warrant line (P-2 cheap half).** The board can now say
   LIVE: a `WARRANT` row under the header, its word derived ONLY
   through `WatchStore.printState` on a new host-supplied
   `warrant` parameter (MainActivity passes the same
   `deckWarrant` the Deck prints — one derivation, no fork).
   Null warrant prints `NOT CHECKED`, never silence.
2. **§4.3 headline + foot signature.** The header prints
   `GUARD {n}/4` (`countedWarranted`, the store's own count),
   and the board signs its work at the foot:
   `GUARD ···· {n}/4 · {HH:mm}` (testTag park_guard_footline).
3. **§4.8 LAST PROVED demoted** from the header to a foot
   colophon beside the signed line, date-when-not-today and
   `· SELF-REPORT` preserved.
4. **§4.4 age column.** Every permission row prints its own
   measurement's age (`NOW` / `45S AGO` / `12M AGO`, derived from
   the buried `measuredMono` stamps) beside its word; STALE's
   half-ink dress (wave-1) now has its print partner.
   DECLARED tests: `Wave2ParkPrintTest` (age boundaries).
5. **§4.7 tiers.** Eyebrows in board order — TIER 1 THE PROOF
   ITSELF (Accessibility + Usage), TIER 2 THE ANCHOR (Device
   Admin), TIER 3 THE QUIET (Notification Listener), and
   OVERLAY + BATTERY moved under `NOT COUNTED — THE REST`,
   out of the counted family's block.
6. **P-5 FIX, print half.** (a) Escort line above the key —
   for Accessibility: `FIX OPENS SETTINGS — FIND MONOLITH IN
   THE LIST, TURN IT ON, THEN COME BACK HERE`; generic form for
   the other doors. (b) The staircase label: `FIX 1 OF 3 — X`
   when several doors lack (the walk is sequential by
   construction — on return the key re-names the next lack).
   (c) The PRINTED return verdict Muede won over Dens's silent
   acquittal: `FIXED — X` / `STILL OPEN — X` under the key,
   persisting until the next press or close. The firm click
   (sound half) is unchanged.
7. **§4.9 plate cut.** Body is now HOLD + PEEK + one combined
   `SITES: KEPT WITH APPS ON SHIELD · STUDY HOURS: IN YPT`
   line. Cut: WHEEL, COLOUR (contradicted the orange-zero law),
   ROUTINES (its FIRED AS SET clause stays struck, W-11),
   SOUND/POWER, and the DATA clause. Lead ruling on the §1.11
   count: the ruled four lines would have deleted two truth
   facts this plate uniquely carries on this surface — kept as
   one combined fifth line, on the record in the code comment.
8. **§4.11 padding + module.** 100dp → 24dp at the drawer's
   foot; the off-module joints land on {8,16,24} (header 6→8,
   post-header 14→16, pre-plate 32→24).
9. **§4.12 gate half.** MainActivity now passes
   `isSealed = doorLocked` to ParkDrawer — PARK was the last
   surface drinking from raw `sealed` while its own header band
   already composed `doorLocked` (HB-1/HB-2's fork, closed).

## REMAINDERS (priced, not hidden)
- **P-2 whole answer / §5 Live-Fire Strip:** parked in issue
  #21 by Sarthak's order — NOT touched. Recorded as
  DECISION-FOR-SARTHAK below, not built.
- **§4.6 staircase, in-trip half:** FIX still makes one
  Settings trip per door; walking ALL lacks in one trip needs
  host-side trip chaining in MainActivity (~80 lines, Vega's
  Staircase) — the print half (escort + label + verdict)
  landed here.
- **§4.7 OFF vs NO DOOR split:** the interceptor derivation
  collapses both into OFF inside WatchStore; splitting it is a
  derivation-law change that feeds the Deck warrant too —
  needs its own verifier, not a Park-side print hack.
- **§4.10 moves:** Sound sled → Shield STRUCK by later truth —
  Shield audit R-9 rules PARK owns the switches (and wave-2 #7
  struck Shield's phantom switch params on exactly that
  ground). Theme row → footnote: the row stays until the LOOK
  picker exists to take its slot (#18, frozen).
- **§4.12 seal-time warning line** (an ungranted door printed
  at seal time): seal machinery in the VM, not this drawer —
  batch-10.
- **§4.4's residue + critic C-6/C-7/C-8** (interceptor RUNNING
  has no stale form; BIND presence-proof; residency stamp
  precedes the own-package filter): machinery, filed by
  wave-1 for Ren to route — unchanged by this pass.
- **W-08 dynamic PARK plate** (batch-10): wave-1 prepared its
  inputs (peekMinutes seam); the plate itself is #15's.
- Pre-existing test staleness (not wave-2's): several render
  tests in this cell call ParkDrawer without wave-1's required
  params — they were stale before this pass; my added `warrant`
  param has a default and adds no new breakage. Issue #2 owns
  test call-sites.
- Unproven until Ren's compile + the phone: everything above.

## SEAT SECTIONS
<!-- wave2-seats-finish-feel.md and wave2-seats-named.md are filed
     beside this report in studio/plan/ and are part of it. Any
     seat a runner left thin is covered by the lead below, marked
     LEAD-COVERED. -->

## SEAT-RUN NOTE
The seat files raced the lead's build pass; most of their §4
MUST list is recorded as FIXES above. On the stray builder
clone, the named-seats run independently reached the lead's
verdict: KILL the clone (its nullable signature reverses
F-G4's totality); a constant, if ever wanted, gets authored
fresh in the main cell. New mechanisms filed for Ren to
sequence (not built): Two Witnesses cross-examination CHECK
(M), the Vigil — open-drawer self-proving (S).

## DECISION-FOR-SARTHAK
- The Live-Fire Strip (PARK rehearsing its own pipeline) stays
  parked in #21 — wave-2 did not touch it. It moves only on
  his ruling. (A seat's long-press-CHECK escalation idea
  touches the strip and is likewise recorded, not built.)
- **Seal-time warning feel (seat-flagged):** when the §4.12
  seal-time warning lands (batch-10), should an ungranted door
  at the 20:00 seal print silently only, or also knock? His
  wind-down hour; his call.
- **Age column form (seat-flagged):** wave-2 shipped elapsed
  (`45S AGO`) DECLARED. If he prefers wall time (`02:47`,
  matching LAST PROVED), it is a one-line flip — his reading
  habit decides.

## Delivery state
- Commit `cc662da` on `agent/issue-9`, PUSHED to origin
  (6237468..cc662da, verified in the push output).
- Signed comment POSTED via `gh` CLI (the sanctioned route —
  no connector used):
  https://github.com/Sarthak-2007/Focus-app/issues/9#issuecomment-6510149323
  Text as posted:
> Wave-2 (Vogt, cluster C): Park's §4 list is worked to its
> priced remainders. Landed DECLARED on agent/issue-9: the
> warrant line (LIVE/OFF/UNPROVEN through the one
> WatchStore.printState derivation, NOT CHECKED when unread),
> GUARD n/4 in the headline + the signed foot line, LAST PROVED
> demoted to the foot colophon, the age column on every board
> row, tier eyebrows with OVERLAY+BATTERY out of the counted
> block, FIX's escort + FIX 1 OF N staircase label + the
> printed FIXED/STILL OPEN return verdict, the plate cut, the
> 100→24dp foot, and PARK's gate composing doorLocked at last.
> The stray issue-9-builder clone was killed as superseded
> (diff preserved on this branch). Remainders are priced in
> studio/plan/ISSUE-9-WAVE2-REPORT.md. Uncompiled — Ren
> compiles on Sarthak's word. — Vogt

Note on delivery route: per Sarthak's Oct 30 04:06 correction,
all GitHub writes this run went via `gh` CLI / plain git —
zero connector calls. (At seating, `gh` was not logged in;
by delivery time it was, and both the pushes and the comments
above went through.)

## Next wants (lead, unassigned capacity)
- The OFF vs NO DOOR derivation split, with its own verifier.
- The in-trip Healing Staircase in MainActivity.

— VOGT, wave-2 lead, issue #9
