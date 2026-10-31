# ISSUE #9 — ZAHN, BRUTAL CRITIC: FULL CHARGE FILE

*Critic seat: ZAHN, dedicated to issue #9 only, flat under Ren.
Scope: UNRESTRICTED — Hof's audit, the CHECK verb, the GUARD —
NOT CHECKED cure, the W-11 plate removal, Ren's rulings, Sarthak's
assumptions. Every charge carries file + line evidence or it is
not filed. Ground: cell clone `builds/cells/issue-9`, branch
`agent/issue-9` @ HEAD `2026c27` (wave-1 `7387f21 / 7c8c6c6 /
6237468`, wave-2 `cc662da / be8fc7f / 255ae52`), read Oct 30,
~05:25–06:00 IST. Paper only — nobody compiled, including me.*

*Severity: S1 = a printed claim that is false or unearned in a
reachable state. S2 = an unearned implication, a stale-proof
path, or a record that overstates what landed. S3 = a wording /
process fault that becomes S1/S2 if nobody touches it.*

*Charges Z-1…Z-4 were posted to issue #9 as signed comments the
moment each was proven (opening #issuecomment-6511607305,
Z-1 #issuecomment-6512043773, Z-2 #issuecomment-6512082393,
Z-3 #issuecomment-6512497724, Z-4 #issuecomment-6512531171).
They are restated here in one line each; Z-5…Z-12 are filed
in full below.*

---

## POSTED CHARGES (summary)

- **Z-1 (S1)** — CHECKING / LAST PROVED completion is a
  wall-clock ERA test in `WatchStore.publish()`
  (`WatchStore.kt:459–478`: `measuredWall >= sweepStart`, 45 s
  span), while three comments claim generation purity
  (`ParkDrawer.kt:208–220`, `WatchStore.kt:134–139`,
  `WatchStore.kt:269`). `WatchMeasurement.sweepGen` is written
  (`WatchStore.kt:415`) and read in NO production path. Gen-0
  shield-loop stamps (every ~2 s, presence only) and earlier
  sweeps' late landings still complete a press's "proof", and
  `publish()` then badges the set with the press's generation
  (`:471–475`). Wave-1 critic C-1 survives under a new label.
- **Z-2 (S1)** — Wave-2's PARK headline (`ParkDrawer.kt`
  header block, `countedWarranted` on a never-null snapshot —
  `MonolithViewModel.kt:480–482` exposes `WatchStore.snapshots`,
  initial `WatchSnapshot.EMPTY`) prints `GUARD 0/4` on cold
  open, and the foot signature (`ParkDrawer.kt:1011–1019`)
  prints `GUARD ···· 0/4 · {HH:mm}`. For a *partial* snapshot
  (3 granted, 1 unmeasured) the headline prints `GUARD 3/4`
  while the header correctly prints `GUARD — NOT CHECKED` —
  the two surfaces contradict each other in every unmeasured
  state, not just cold open. This is the fabricated count
  P-6 exists to prevent (`GuardLine.kt:19–27`). No test
  references `countedWarranted`, `park_guard_headline`, or
  `park_guard_footline` (grep, `app/src/test`: zero hits);
  `GuardLinePrintTest` / `ChromeWaveUnitTest:148–160` pin
  only the header derivation that PARK bypasses.
- **Z-3 (S2)** — The W-11 regression guard
  `TruthCar3SweepTest.una05_plateDoesNotClaimFiredAsSet`
  (`TruthCar3SweepTest.kt:336–354`) cannot compile (call sites
  omit required `ParkDrawer` params; wave-1 added a fifth,
  `onCheckClick`, `ParkDrawer.kt:156`, against builder
  dossier R-3's explicit warning) and wave-2's plate cut made
  its positive assertion false (`ROUTINES: SET BY HAND ON
  SHIELD` was deleted with the whole line, `ParkDrawer.kt:
  118–138`; the test still asserts it exists, `:351–352`).
  The strike itself is TRUE — grep-verified by me: `FIRED AS
  SET` in `app/src/main` survives only in the comment at
  `ParkDrawer.kt:125`. "Verified struck" (commit `7387f21`)
  is a grep verdict, not a test verdict. Say so.
- **Z-4 (S1)** — F-G4 made `SealFreeze.guardLine` non-null
  (`MonolithViewModel.kt:631–635`) and removed the live
  fall-through from the freeze branch (`:688–700`). The birth
  capture (`:852–868`) reads `WatchStore.snapshots.value` at
  the first `activeSession` emission, racing the UI-side
  sweeps (`MainActivity.kt:170,336,346`); the re-capture
  collector fires only on `ArmPhase.SealedRunning`
  transitions (`:886–913`), which a session restored after
  process death never re-makes. A restored sealed session can
  therefore hold a frozen `GUARD — NOT CHECKED` header for
  its whole life on a fully measured machine. Builder R-6
  priced the routine-born case; the restored-session race —
  the likelier overnight path — was never priced.

---

## Z-5 (S2) — THE STAIRCASE LABEL NEVER CLIMBS: it is hardcoded "FIX 1 OF n"

`ParkDrawer.kt:698–703`:

```kotlin
text = if (missingItems.size > 1) {
    "FIX 1 OF ${missingItems.size} — ${item.boardName}"
} else { "FIX — ${item.boardName}" }
```

The key always names `firstMissing`, so the numerator is a
constant: 1. Fix one of three doors and the key reads
`FIX 1 OF 2`, then `FIX — X`. The audited design (AUDIT-park
§2 P-5) was a key *morphing* `FIX 2 OF 3` — a walk you advance
through. What shipped is a queue that re-counts its remainder
while wearing a walk's grammar. The wave-2 report
(`ISSUE-9-WAVE2-REPORT.md` fix 6b) records "The staircase
label: FIX 1 OF 3" as landed and prices only the in-trip
chaining as remainder — it never states the numerator cannot
move. Either print the honest form (`FIX — X · 2 MORE OPEN`)
or track the walk's position. Do not let "1 OF n" impersonate
progress.

## Z-6 (S2) — CHECK HAS NO FAILURE FACE: a query that never answers leaves CHECKING forever, and the only escape is a secret

- `WatchStore.runSweep` (`WatchStore.kt:280–293`): a query
  that throws stamps nothing (C-9's cure). Correct in
  isolation — but then that item can never complete the set
  from the sweep itself; completion depends on other writers'
  era stamps (Z-1's laundering path is also the only rescue
  path) or never happens.
- `checking` (`ParkDrawer.kt:221–222`) has no deadline, no
  age print, no DID NOT ANSWER state. The Designer designed
  one (`issue-9-designer.md` §1.5: a FAILED state naming the
  door that did not answer); the plan renamed failure to
  completed-with-a-lack (wave-1 critic C-2's record) and the
  never-answers case fell out of the design entirely.
- The C-2 escape that did land — re-press while CHECKING
  re-dispatches (`ParkDrawer.kt` press block, ~L766–790;
  `pointerInput` is gated on `interactive` only) — is
  discoverable by nobody: the key reads CHECKING, and
  nothing on the surface says pressing it again does
  anything. Probability, stated honestly: the repository
  queries are local and several are internally try/caught
  (`MonolithRepository.kt:585–610, 659–667`), so a hang is
  unlikely. Unlikely is not never, and a truth surface whose
  verb can die silently has a hole exactly where its job is.

## Z-7 (S2) — CHECK RE-STAMPS THE USAGE LIE FRESH: UNSET collapses to MISSING, now with an age of NOW

`MonolithRepository.watchGrantQuery` (`MonolithRepository.kt:
645–647`): USAGE_ACCESS is granted iff `usageAccess() ==
ALLOWED`. `usageAccess()` (`:585–610`) returns `UNSET` both
for `MODE_DEFAULT` and from its catch block. UNSET therefore
stamps `PermState.MISSING` — a measured "no" nobody measured;
the audit's own RESIDUE names this "the repo's named lie"
(AUDIT-park §2). Pre-#9 it was a stale lie. Post-#9, every
CHECK and every PARK open re-stamps it with a fresh
mono/wall, and wave-2's age column (§4.4) certifies it as
seconds old. #9 did not create this lie, but its verb is now
the lie's freshest witness, and no #9 document mentions it.

## Z-8 (S3) — THE AGE COLUMN CERTIFIES PRESENCE AS FRESHNESS FOR THE TWO BIND DOORS

Wave-2's age column (§4.4) prints each permission row's
measurement age from `measuredMono` (`ParkDrawer.kt:520`).
For ACCESSIBILITY and NOTIFICATION_LISTENER during a
session, `onShieldIteration` (`WatchStore.kt` shield writer)
re-stamps every ~2 s from `instance != null` alone — so
their age reads `NOW` continuously, forever, without any
grant check occurring. The audit acquitted those stamps as
measurements; printing their age as if it were the age of a
*check* is a new claim wave-2 added on top of residue C-6.
At minimum the two BIND rows' ages mean a different thing
than the other four rows' ages, and the board does not say
so.

## Z-9 (S3) — THE PLATE IS FIVE LINES; THE AUDIT RULED FOUR; THE OVERRULE IS A CODE COMMENT BY A LATER LEAD

AUDIT-park §1.11 ruled the plate cut to 4 lines (head 2 +
HOLD + PEEK). What shipped is 5: PLATE_HEAD 2 + HOLD + PEEK
+ the combined `SITES: KEPT WITH APPS ON SHIELD · STUDY
HOURS: IN YPT` (`ParkDrawer.kt:118–138`). The deviation is
disclosed in the code comment ("against the literal count,
on the record — lead ruling") and in the wave-2 report
(fix 7). The substance is defensible; the process is not:
Hof's audit ruling was outranked by Vogt's wave-2 lead
ruling with no counter-sign from Hof and no ruling from
Sarthak anywhere in the record. If any later lead can
re-rule any audit line in a comment, "ruled" in an audit
means "until the next lead". File the overrule where
rulings live, with a name and a reason — or restore the
fourth line's cut.

## Z-10 (S2) — REN'S NO-COMPILE LAW CONCENTRATES ALL SYNTAX RISK INTO HIS ONE BUILD, AND THIS BRANCH PROVES IT

Everything on this branch is DECLARED, uncompiled — by
standing order, not by the cell's choice. The branch's own
history shows what that costs: commit `255ae52` ("wave-2
postscript") exists because `fixDip` read `fixPressed` ~50
lines before its declaration — a certain compile error that
sat in the tree until a seat caught it *by eye*. Four render
tests cannot compile (Z-3). Nobody knows what else is in
the tree, because the one instrument that would say so is
reserved for a single future build on Sarthak's word. I do
not charge the cell with Ren's law; I charge the record:
every "landed" in the wave reports should read "typed,
uncompiled, syntax unverified", and the merge plan should
budget Ren's first compile as a *debugging session*, not a
formality. The current wording ("DECLARED" in fine print,
"FIXES LANDED" in headlines) overstates certainty in
exactly the way this app exists to refuse.

## Z-11 (S3) — CHECK COMMITS ON DOWN; FIX WAS MOVED TO RELEASE FOR THE REASON THAT STILL APPLIES HALF-WAY

Commit `6237468` + the FIX press block (`ParkDrawer.kt:
663–690`): FIX commits on RELEASE because "a scroll that
merely starts on the key … must launch nothing". CHECK's
press (`ParkDrawer.kt` press block, ~L766–790) calls
`onCheckClick()` on DOWN, before `tryAwaitRelease()`. The
in-code defence — a sweep is idempotent and costs nothing —
is true of the sweep and false of the side effects: a
scroll that starts on CHECK still fires the seat knock,
starts CHECKING, and can clear a standing NOT PROVED
verdict line (`lastCheckLackName = null` on press) for a
gesture that meant "scroll down". Cheap, recoverable, and
inconsistent with the house's own freshly-written law.
Move the dispatch behind the release, keep the knock on
DOWN, and the inconsistency dies.

## Z-12 (S3) — TWO DIFFERENT "NOT CHECKED"S NOW LIVE ONE SCREEN APART

The header prints `GUARD — NOT CHECKED` = the counted four
are unmeasured (`GuardLine.kt:39–42`). PARK's warrant row
prints bare `NOT CHECKED` = no warrant read was supplied
(`ParkDrawer.kt:481`: `warrantPrint?.name ?: "NOT CHECKED"`).
Same words, different facts, adjacent surfaces — in an app
whose audit already carries a name-collision residue
(NOTIFICATION LISTENER vs NOTIFICATIONS). A reader cannot
tell which ignorance is being confessed. Rename the warrant
row's null word (`UNREAD`) or qualify it; do not let one
phrase serve two states.

---

## WHAT SURVIVES THE ATTACK (acquittals, so the record is honest)

- CHECK does dispatch six real platform queries through
  `MonolithRepository.watchGrantQuery` (`MonolithRepository
  .kt:645–654`) via the same sweep Deck entry uses; PARK
  open re-sweeps (`MainActivity.kt:336–338`). P-1's core
  demand — a verb that re-measures — is genuinely built.
  Z-1 attacks the completion *accounting*, not the verb.
- The header cure itself is sound and pinned:
  `guardLineFor` is total, `GUARD — NOT CHECKED` exact
  (em dash, single spaces), tests updated in
  `ChromeWaveUnitTest:148–160` and `GuardLinePrintTest`.
- The warrant line derives only through
  `WatchStore.printState` on the host's `deckWarrant`
  (`ParkDrawer.kt:476–484`) — no second derivation was
  forked. P-2's cheap half is real.
- `FIRED AS SET` is off every printed surface (Z-3 is
  about its guard test, not the strike).
- The wave-2 report discloses its remainders (staircase
  in-trip half, OFF/NO DOOR, §4.10, seal-time warning)
  instead of burying them. That honesty is why several
  charges above are S2/S3 and not S1.

## DEMANDS, IN ORDER

1. Z-1: make `publish()` test `sweepGen`, or strike the
   three generation-purity comments. (S1)
2. Z-2: route PARK headline + footline through the
   UNMEASURED gate; pin EMPTY and partial snapshots in a
   declared test. (S1)
3. Z-4: give a NOT CHECKED freeze a re-capture or
   fall-through path for restored/birth captures. (S1)
4. Z-3: repair the four render-test call sites, update
   `una05` to the wave-2 plate, and correct "verified" to
   "grep-verified" until the test runs. (S2)
5. Z-5, Z-6, Z-7: print honest forms (staircase remainder
   wording; a DID NOT ANSWER face; UNSET as its own word
   on the USAGE row). (S2)
6. Z-9, Z-10: file lead overrules where rulings live;
   word wave reports "typed, uncompiled" until Ren's
   build returns a verdict. (S2/S3)

— ZAHN, brutal critic, issue-9 cell. Oct 30, 2026.
