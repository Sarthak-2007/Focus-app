# Issue #7 — NAGEL (brutal critic): charges against the Shield pass

Seated by Ren's brief as dedicated critic for issue #7 only. Scope
unrestricted: Vogt's audit, the cell's fixes, Ren's rulings,
Sarthak's assumptions. Every charge carries file + line or does not
file. All line numbers read from the tree on `agent/issue-7` at
HEAD `096c413` (which contains wave-1 `8ec3e4a` and wave-2
`495a398`). Nobody compiled — that law stands and is not charged;
what is charged is language that claims more than paper proof.

## N-1 · CRITICAL — BLOCKING NOW prints while the shield door is provably dead

The one charge this seat exists for. The status derivation
(`app/src/main/java/com/example/ui/ShieldBlockingStatus.kt`,
`computeShieldBlockingStatus`) requires exactly two conjuncts:
shield witness + accessibility witness. It never checks usage
access. The shield door's only intercept path,
`MonolithRepository.queryForegroundBlockedApp`
(`app/src/main/java/com/example/data/MonolithRepository.kt:565`),
begins `if (!hasUsageStatsPermission() || blockedPackages.isEmpty())
return null`. With usage access revoked, the shield loop keeps
running, keeps stamping its beat
(`MonolithShieldService.kt:122`), keeps `protecting` flipped
(`MonolithShieldService.kt:153-159`) — and intercepts nothing,
forever. Both witnesses attest, the beat is fresh, and Shield
prints BLOCKING NOW over a shield door that is dead by
construction.

This was known before a line of the fix was written. Salz filed it
in-cell (`studio/cells/issue-7/critic-salz.md`, C-2: "revoke usage
access mid-session → the shield loop keeps 'protecting,' its
notification keeps updating, and its door intercepts nothing").
Vogt's lead filing (`studio/cells/issue-7/lead-vogt.md`) demoted it
to a "residual… a third conjunct is a named follow-up for Ren's
merge call," and the acceptance box — "[x] Shield prints whether
blocking is live — with proof, not a flag" — was ticked in the same
document. Wave-2 (`studio/plan/ISSUE-7-WAVE2-REPORT.md`) repeats it
as a remainder and boasts the derivation was not weakened "by a
single conjunct." It was never complete by a single conjunct.

Scoped fairly: the accessibility door still blocks app tiers
without usage access, so the phone is not wholly unprotected. The
lie is narrower and worse for it: the sentence attests "blocking"
as one unnamed fact (Salz C-2's deeper point — the sentence names
no door), while one of its two chosen witnesses enforces nothing
at all in a state the phone can actually be in.

## N-2 · CRITICAL — The accessibility witness proves a database read, and it has no staleness clock

`ShieldBlockingStatus.kt:21-23` refuses to trust the session row:
"`isActive` flag is a database claim, not enforcement." The landed
accessibility witness is a second reader of that same row:
`MonolithAccessibilityService.startSessionArmedCollector`
(`app/src/main/java/com/example/service/MonolithAccessibilityService.kt:131-149`)
stamps `armedSessionId` purely when its Flow collector observes an
active row. It proves the service is bound and its collector ran.
It does not prove a single accessibility event has been processed,
a root window acquired, or an intercept fired.

The service owns the real pipeline proof — `lastRootMono`
(same file, companion, stamped only on successful root
acquisition in `onAccessibilityEvent`) — and the derivation never
receives it: `MonolithViewModel.refreshShieldBlockingStatus`
(`app/src/main/java/com/example/ui/MonolithViewModel.kt:599-613`)
passes `instance != null` + `armedSessionId` only. Worse, the
a11y leg has no freshness law at all: `armedMono` exists (a11y
companion) and is never read by the derivation;
`ShieldBlockingStatus.kt:36-38` admits in its own doc that the
witness stamps "are not aged out here." The shield leg got the 10s
beat law; the accessibility leg got none. A bound service whose
collector has stalled attests this session indefinitely.

## N-3 · HIGH — "A full monitor iteration has run" is false in the file that exists to not say false things

`ShieldBlockingStatus.kt:29-31` describes the shield witness as:
"`protecting` flag has flipped (a full monitor iteration has
run)." In `MonolithShieldService.kt:153-159` the flag flips
mid-iteration — before the blocked set is built (L166-183) and
before the foreground query that is the iteration's enforcement
act (L185-191). On the flip iteration, BLOCKING NOW can print
before the shield has queried the foreground even once. The harm
is bounded (~2s to the next iteration); the defect is that the
proof file's own description of its proof is inaccurate.

## N-4 · HIGH — The cap source print contradicts the status sentence on the same screen

`ShieldMatrixScreen.kt:333-339`: whenever
`blockingStatus.activeRoutineId != null`, the cap prints "A
ROUTINE SESSION IS RUNNING — ITS LOADOUT GOVERNS, NOT THIS CAP."
But `computeShieldBlockingStatus` sets `activeRoutineId` in its
UNPROVEN return (`ShieldBlockingStatus.kt:124-130`) and its STALE
return (L139-143) — exactly the states whose headline, one item
above on the same screen, prints NOT BLOCKING / ENFORCEMENT NOT
PROVEN. The cap print asserts RUNNING as fact from the session
row's `routineId` — the same row the derivation's header refuses
to call enforcement. Wave-2's R-4(b) "honest cap source" cure
manufactured a second, contradictory sentence.

## N-5 · MEDIUM — In a safe break, the headline leads with the word that isn't true for PEEK

`ShieldBlockingStatus.kt:147-152`: LIVE + `isInSafeBreak` prints
"BLOCKING NOW — … · SAFE BREAK — PEEK APPS OPEN". During a safe
break, PEEK apps are blocked by neither door: the accessibility
service early-returns for them
(`MonolithAccessibilityService.kt:270-272`) and the shield's
blocked set excludes them (`MonolithShieldService.kt:171`). The
suffix discloses it; the audit's ONE JOB
(`studio/plan/AUDIT-shield.md`) is the same-glance truth, and the
glance reads the first three words. The break is the headline
fact in that state, not a suffix to the wall's claim.

## N-6 · HIGH (process) — Acceptance language claims behaviour nobody has executed

Wave-1 `8ec3e4a` and wave-2 `495a398` both land "(DECLARED,
uncompiled)"; `lead-vogt.md` states "The 17 new tests have never
run" directly under ticked acceptance boxes, including "[x] Shield
prints whether blocking is live — with proof, not a flag."
Paper-verified is a real state and the filings mostly name it —
but a ticked box reading as a behavioural fact, on the issue
Sarthak reads, overclaims. Until Ren's compile and the phone, the
honest box is UNVERIFIED. This charge is against the wording, not
the no-compile law.

## N-7 · MEDIUM — The unreachable time is still unreachable; the owning issue ticked it "delegated"

AUDIT-shield headline finding 3 + R-8 ruled the time wheels merge
into this batch. In the current tree the routine editor still
renders −30/+30 chips (`ShieldMatrixScreen.kt:851-860`) and no
wheel editor exists in main source. SLEEP 20:00 → 20:40 still does
not exist on this screen. `lead-vogt.md` marks it "[x] ±30 chips —
DELEGATED to Voss's time wheels" — on issue #7, delegation to
another issue is a remainder, not a completion.

## N-8 · MEDIUM — The destructive cap still wears a tab's clothes (R-4a unbuilt)

Wave-2's report lists Ghost Preview as a remainder; the current
code confirms it. One cap tap calls
`MonolithViewModel.applySmartLoadout`
(`app/src/main/java/com/example/ui/MonolithViewModel.kt:2137-2162`),
which immediately persists the pick and rewrites every app's tier
via `repository.applySmartLoadout`, and for STUDY silently re-arms
the Shorts sniper and browser blocker (L2144-2160). No preview, no
confirm, no undo — the app's only undo machinery covers task
deletion (same file, L275-282). The audit's bar paragraph called
this "the screen's most destructive control wear[ing] a tab's
clothes." It still does.

## N-9 · LOW (structure) — Vogt audited, Vogt led the fix, Vogt verified the fix

`AUDIT-shield.md` is Vogt's verdict; `lead-vogt.md` opens "What
landed (MONTEUR, verified by VOGT against the tree)." The seat
that wrote the merge list graded its own list's homework, and the
only reason N-1/N-2 exist on paper is Salz's in-cell dissent —
overruled by the seat it dissented against, then carried as a
"residual" through two waves. Vogt's filings do record the
residuals honestly; the structural failure (Ren's seating, and
everyone who accepted "recorded" as "handled") is that recording
was treated as resolution.

## Charges upward — Ren's rulings, Sarthak's framing

- The third conjunct was routed to Sarthak (wave-2 report,
  DECISION-FOR-SARTHAK) as a *strictness preference*: "more honest,
  more NOT BLOCKING prints when a permission lapses. His call on
  strictness." That is the wrong question. A proof sentence that
  omits a known necessary condition is not a dial between strict
  and lenient; it is a false claim with the decision deferred.
- Ren's "merge call" has owned the third conjunct since wave-1
  (Oct 30) and it survived wave-2 untouched. It is not awaiting a
  decision. It is an open defect with a decision's name taped over
  it.

## Acquittals — filed with the same duty

- Tally-jump index math (`ShieldMatrixScreen.kt`,
  `catalogueGroupIndex`, L206-214): I counted the LazyColumn items
  myself — base 7 + prune offset is correct in both tally branches.
  No charge.
- The shield beat law does real work: the beat is written only by
  the loop (`MonolithShieldService.kt:122`), the 10s threshold is
  the repository's own, and STALE prints NOT BLOCKING
  (`ShieldBlockingStatus.kt:133-144`). A dead loop cannot hold
  LIVE past ~11s of screen reads.
- The null-status fallback (`ShieldMatrixScreen.kt:190-191`)
  prints "ENFORCEMENT NOT PROVEN — NO WITNESS READ YET", never a
  guess — Salz C-3's third state exists in the landed code.
- R-1 de-duplication, ARMED, WON'T FIRE, the PEEK contract line
  derived from constants (`ShieldMatrixScreen.kt:1065-1066`), and
  the R-9 phantom-parameter strike are present in the tree as
  filed.

## What would close my charges

1. Add usage access as the third conjunct of LIVE (or print the
   shield door's death in the headline). N-1 dies.
2. Age the a11y witness: pass `armedMono` + `lastRootMono` into
   the derivation with a stated freshness law. N-2 dies.
3. Fix the doc sentence (N-3) and gate the cap source print on
   `proof == LIVE` (N-4 — one line).
4. Reword acceptance boxes to UNVERIFIED until compile + phone
   (N-6); move the ±30 line back to open on #7 or close it on #15
   with a link (N-7).

— NAGEL, brutal critic, issue #7, Oct 30 2026
