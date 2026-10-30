# Issue #57 — Wave 3 Report · Receipt Sheet (R1 slice `receipt-sheet`)

Lead: RAMPE (C8 Schwelle) · Bundle seat-runner: the #57 cell agent.
Branch: `agent/issue-57` · Base: `origin/main` e29acdf.
Sacred constraint honoured throughout: **the receipt is a loved
surface** (his ❤️, Oct 29) — its print character (24dp slip, mono
lines, footer sentence, HELD/deflection lines, fault lines) is
untouched. Every change below is a truth fix only; where a fix
changed print at all, it changed it only on the false/missing
fact itself.

## Dispositions — every finding, file + line evidence

Audit evidence: `studio/audits/AUDIT-R1.txt`, slice receipt-sheet
(lines 1769–1807). Verified against `origin/main` (e29acdf) AND
`origin/agent/issue-10` (Wave-2 Group A, commits d126c07/e484783).

### 1. [HIGH] Elapsed duration never printed — FIXED-IN-CODE (#57); also FIXED-ELSEWHERE (#10)
- On main: `ReceiptState.sealedElapsedSeconds` exists
  (`data/ReceiptState.kt:180`, populated `MonolithRepository.kt:1640`
  from `row.spanElapsedSeconds`) but `ReceiptSheet.kt` (211 lines)
  never referenced it — only ARMED/ENDED/PLANNED/HELD/BREAK printed.
  **Stands on main.**
- On #10: cured — `RAN {longSpan}` over a divider under ENDED
  (issue-10 `ReceiptSheet.kt:239–244`).
- On #57: cured independently and minimally — `RAN {longSpan}`
  line directly under ENDED when `sealedElapsedSeconds > 0`
  (this branch `ReceiptSheet.kt:181`); zero omitted, never
  `RAN 0 MIN`. No divider/shape change (character preserved);
  the merge train keeps either rendering — the fact prints in both.

### 2. [HIGH] BREAK BANKED false 0 MIN / truncation — FIXED-IN-CODE (#57, arithmetic); FIXED-ELSEWHERE (#10, kill)
- On main: `ReceiptSheet.kt:134–136` (audit :138): guard only
  `bankedBreakSeconds > 0`, body `bankedBreakSeconds / 60` —
  1–59s printed `BREAK BANKED 0 MIN`, 119s printed `1 MIN`.
  **Stands on main.**
- On #10: the line is killed as printed, field state-only
  (issue-10 `ReceiptSheet.kt:260–269`) on a *semantic* ground
  (the one live path booked a 60s valve-hold price as banked).
- On #57: the audit's charge is arithmetic, and it is cured
  arithmetically — the line now prints the exact span
  (`ReceiptSheet.kt:197`, via exact `longSpan`): 45s →
  `BREAK BANKED 45 SEC`, 119s → `BREAK BANKED 1 MIN 59 SEC`.
  Train note: if #10's semantic kill composes after this branch,
  the line dies there and this cure is moot; if this branch
  composes last, no false figure can print either way. The false
  print is dead on both paths — the train picks the epitaph.

### 3. [MEDIUM] PLANNED silently rounded up — FIXED-IN-CODE (#57); STANDS on #10 too
- On main: `longSpan` at `ReceiptSheet.kt:204–211`:
  `val minutes = (seconds + 59) / 60` — 61s → `PLANNED 2 MIN`,
  3601s → `PLANNED 1H 01M`. **Stands.**
- On #10: **stands identically** — issue-10 `longSpan`
  (`ReceiptSheet.kt:389–396`) keeps the same ceiling body; its
  new RAN line inherits the same inflation. This is the one
  finding no earlier wave cured.
- On #57: `longSpan` rewritten exact (`ReceiptSheet.kt:285`):
  whole minutes print byte-identically (`50 MIN`, `1H 00M` —
  the normal case, plans are set in minutes), sub-minute
  remainders are spoken: `45 SEC`, `1 MIN 01 SEC`,
  `1H 00M 01 SEC`. No rounding, no truncation, anywhere the
  receipt prints a span (PLANNED, RAN, BREAK BANKED share it).

### 4. [MEDIUM] ARMED/ENDED bare HH:mm, cross-midnight ambiguous — FIXED-IN-CODE (#57, both lines); PARTLY FIXED-ELSEWHERE (#10, ARMED only)
- On main: `hhmm` at `ReceiptSheet.kt:203` is `HH:mm` only;
  ARMED/ENDED at :118/:124 carry no date. **Stands.**
- On #10: A11 cures it partly — ARMED alone carries its day
  (`EEE d MMM`) when arm/end local dates differ (issue-10
  `ReceiptSheet.kt:186–201`); ENDED stays bare, and it uses
  `java.time` (minSdk is 24 — a desugaring exposure #10 owns).
- On #57: when local dates differ, **both** lines carry their
  own day (`ReceiptSheet.kt:151–163`, `dayLabel` :264,
  `sameLocalDay` :269 — `Calendar`-based, no `java.time`, safe
  at minSdk 24): `ARMED TUE 4 NOV 23:50` / `ENDED WED 5 NOV
  00:10`. Same-day slips are byte-identical to before.
- Residual, named honestly: both branches format wall instants
  in the device's *current* timezone; a timezone change between
  session and viewing shifts both times together (the span and
  the day-relationship stay true; the printed clock times are
  the viewing zone's). Curing that needs the zone persisted on
  `ReceiptState` — a state-contract change outside this sheet's
  surgical scope. STANDS-BLOCKED (residual only), priced: one
  field on `ReceiptState`/`PendingReceipt` + assembly plumbing
  in `MonolithRepository` + sheet formatting honours it.

### 5. [MEDIUM] Fake dismissal/animation — FIXED-IN-CODE (#57); FIXED-ELSEWHERE in substance (#10)
- On main: `ReceiptSheet.kt:60–63` `AnimatedVisibility(visible =
  true, …, exit = fadeOut)` — exit can never run (the host,
  `MainActivity.kt:776–785`, unmounts the slip the moment
  `dismissedReceiptSessionId` is set), an initially-true
  visibility never runs the enter fade either, and :70
  `.clickable(onClick = onDismiss)` fires on release (UP),
  contradicting the header's own "dismisses on the next DOWN".
  **Stands, all three parts.**
- On #10: DOWN cured (pointerInput `onPress` + 2s dwell,
  issue-10 :121–128), enter made a real feed (slideIn), dead
  exit deleted — but `visible = true` remains hardcoded (:95)
  with `ExitTransition.None`, i.e. the same honesty cure for the
  exit, taken by deletion.
- On #57: scrim dismissal is `detectTapGestures(onPress = …)`
  — the DOWN itself (`ReceiptSheet.kt:99`); enter runs for real
  via `MutableTransitionState(false → true)` (:83–90); exit is
  declared `ExitTransition.None` with the reason in the header
  — no animation is claimed that cannot play. No dwell and no
  faulted-lock were imported from #10: the header spec on main
  promises the *next DOWN*, and #57 keeps that promise literally;
  #10's dwell is a deliberate spec change the train may still adopt.

## Tests — `app/src/test/java/com/example/ReceiptSheetTruthTest.kt` (new, 11 tests)
Pure: `longSpan` whole-minute regression set + exact-remainder
set (45/59/61/119/3601/3661); `sameLocalDay`/`dayLabel`.
Compose (Robolectric, the `ReceiptWave2Test` pattern): RAN line
prints the sealed elapsed exactly; zero elapsed prints no RAN;
PLANNED 61s prints `1 MIN 01 SEC` and never `2 MIN`; BREAK
BANKED 45s → `45 SEC` (never `0 MIN`), 119s → `1 MIN 59 SEC`;
cross-midnight dates BOTH lines; same-day lines stay bare; a
press DOWN with no release dismisses (the old clickable could
not). CI is the compile court — verdict recorded in the closing
comment on the issue.

## The 20 seats

### 1. Crazy-ideas
Print the truth louder, not prettier: what if RAN were the
slip's biggest figure? Rejected — crazy loses to loved: the
slip's hierarchy is proven (his ❤️); #57 changes facts, not
the face. My one kept idea is in the code: a duration that
cannot be said exactly in the old dialect gets its seconds
*spoken* (`1 MIN 01 SEC`) instead of rounded — the dialect
grows a word instead of telling a lie.

### 2. Maximum-implementation-potential
Every finding here was sheet-local except the timezone
residual; I took the maximum that stays surgical: one exact
formatter now serves PLANNED, RAN and BREAK BANKED, so no
future line printed from this file can re-inherit the ceiling
or the `/60`. The seam is the win — three findings, one cure.

### 3. Brutal critic
The old sheet was a forgery kit: it omitted the one number
the session existed for, fabricated `0 MIN` from 59 real
seconds, inflated every non-round plan, and claimed an exit
animation physics forbade. Worse, #10 — which "fixed the
receipt" — kept the ceiling in `longSpan` and shipped it into
its brand-new RAN line. Nobody checked the formatter. I did:
finding 3 stands on #10 verbatim, and this report says so.

### 4. Improver
Before: subtract two clock times in your head, across
midnight, to guess your session. After: the total is printed,
exact, and every other figure on the slip means what it says.
Small diff, total change in what the document *is*: from a
vibe of a record to a record.

### 5. Designer
I touched nothing visual that was not lying. Same 24dp slip,
same mono, same footer, same inks. The new RAN line sits in
the existing rhythm (8dp spaced mono line under ENDED);
cross-day dates appear only on the rare slip that needs them.
A loved surface earns restraint.

### 6. Consumer
Would I trust this slip in a dispute with myself at 1 AM?
Before: no — `PLANNED 2 MIN` for a 61-second plan is the kind
of small lie that kills a logbook. After: every number can be
re-derived from the state. That is the whole product.

### 7. Student
Sarthak reads this slip after a 12-hour day. `RAN 1H 01M 01
SEC` tells him the session he actually lived; `ARMED TUE 4
NOV 23:50 · ENDED WED 5 NOV 00:10` tells him the night shift
was real. No arithmetic homework at the finish line — the
receipt does the maths, exactly.

### 8. User
I press the scrim to put the slip away and it used to wait
for my finger to *leave* before obeying. Now it goes on the
press. That single DOWN is the difference between a document
and a dialog box.

### 9. User-experience
The journey is unchanged — seal, slip, read, dismiss — except
each step now tells the truth: arrival actually animates
(the fade was previously stillborn), the read is complete
(RAN), and the exit is instant on DOWN. No dwell was added:
the spec said next-DOWN and accidental-dismiss risk is the
host's call, already answered by that spec.

### 10. Purpose-questioning
Why does a blocker print a receipt at all? To make an
invisible discipline visible *credibly*. A receipt that
rounds, truncates and omits is worse than none — it teaches
the user to distrust the machine's other truths (HELD,
deflections). #57 restores the premise: if it prints, it is exact.

### 11. Thoughtful
The BREAK BANKED divergence deserved care, not reflex:
#10 killed the line for a semantic sin (pricing a valve
hold); #57's audit charged only arithmetic. I cured the
charge in front of me and left the train a written choice
between two honest outcomes — rather than silently importing
another issue's ruling into a loved surface.

### 12. Deep-thinker
Three of five findings share one root: the sheet treated
durations as *display* (round them for neatness) instead of
*evidence* (print them as sealed). `longSpan`'s ceiling is
the tell — `(seconds + 59) / 60` is a UI habit living in a
record-keeping surface. The fix is philosophical before it is
arithmetical: on a receipt, formatting never edits the fact.

### 13. Enthusiastic designer
The seconds-spoken forms are genuinely lovely: `1 MIN 01 SEC`
in Anonymous Pro reads like a stopwatch card punched by the
machine itself — more mechanical, more Monolith, not less.
Truth gave the slip *more* character, and only where the old
character was a lie.

### 14. Wow-level designer
Wow here is a date appearing exactly once in a slip's life —
the night it crosses midnight — and never otherwise. Restraint
that precise is the wow. The RAN line under ENDED completes
the classic receipt silhouette: times, then the total.

### 15. Designer with 29 years of experience
I've seen this failure for decades: the summary that rounds
is the report that gets shredded in the first audit. Print
craft rule — set the total, set it exact, set it in the same
type as the lines it totals. Done that way here. And never
spec an exit animation your host's lifecycle cannot run;
 juniors write `fadeOut`, veterans check the unmount path.

### 16. Intentionality-focused designer
Every delta names its intent in a comment at the site: DOWN
(:99), dates (:151), RAN (:178), exact BREAK (:197), honest
animation (header). Nothing in the diff is decorative, nothing
is accidental — including what was *not* taken from #10
(dwell, faulted-lock, 3dp shape): those are #10's intentions,
filed there, not smuggled here.

### 17. Synchronous collaboration
This bundle ran against two moving truths — main e29acdf and
#10's unmerged branch — and wrote both into every disposition
so Konto's train composes with eyes open: convergent cures
(RAN, DOWN, exit-honesty) are flagged as convergent, the one
divergence (BREAK BANKED: exact-print vs kill) is flagged as
a decision, and the one gap in #10 (finding 3) is flagged as
covered here. No silent double-work, no rival claim.

### 18. Implementation-quality
One formatter, three call sites, zero new dependencies,
`Calendar` instead of `java.time` at minSdk 24, internal
(not private) seams so the formatters are unit-testable
without composition. Diff: 1 file changed in `main`, 1 test
file added, 102 insertions in the sheet — surgical as ordered.

### 19. Technical execution specialist
Verified the mechanics, not the vibes: host unmount path read
at `MainActivity.kt:776–785` before touching the animation;
`onPress` semantics (fires at press, before release) are the
documented `detectTapGestures` contract; `MutableTransition
State(false)` + target true is the documented way to run an
enter on first composition. Robolectric pins include a
DOWN-without-UP gesture — the exact gesture the old code failed.

### 20. Interaction-detail specialist
Details: the scrim keeps its 0.12 alpha and 12dp pad; the
slip's disabled-clickable shield is untouched, so presses *on
the paper* still don't dismiss — only the scrim's DOWN does,
as before in geography, now correct in timing. The enter fade
keeps `MonolithCurves.specC()` — same curve, finally played.
A `receiptSheetScrim` testTag (invisible) makes the gesture
contract testable forever.

## Blockers / residuals
- Timezone-retroactivity residual on finding 4 — named and
  priced above; everything else is fixed in code on this branch.
- CI verdict: see the closing comment on issue #57 (this report
  is committed before the push; the run URL lives there, per
  the no-procrastination closure rule).

---

## CLOSER ADDENDUM (Wave-3 closer bundle under RAMPE, Oct 30)

### CI verdict — final classification (supersedes the deferral above)

Run 40974267409 on branch tip f316eca (completed): steps —
Assemble debug **SUCCESS**; Unit tests the only failing step:
269 tests executed, 37 failed. Classification: **zero
#57-scope errors, proven against the baseline** — main
@73aaeae (the exact commit merged into this branch) ran
40973388591 with 258 tests / 37 failed, and the two failure
sets are byte-identical (diff empty). The +11 delta is exactly
ReceiptSheetTruthTest, all passing. The 37 are pre-existing
other-scope failures: BreakMigrationTest ×1,
CascadeDeckRenderTest ×5, ChromeWaveUnitTest ×1,
DrawerRenderTest ×7, ScreenshotHarnessTest ×15 (all
`FileNotFoundException` at ScreenshotHarnessTest.kt:150 — a
harness-environment failure, not rendering assertions),
ShortsSniperTruthTest ×1, SoundAndSocialTruthTest ×4,
TetradRenderTest ×1, TruthCar3Sweep(Robo)Test ×3.

### Print-character proof — external Receipt Print Finisher verdict (cited in full)

**VIOLATIONS: NONE. Nothing DECISION-NEEDED on print
character.** 16 fingerprint elements: 12 UNTOUCHED, 4
TRUTH-MINIMUM. UNTOUCHED includes: the typeface system (Sora
deliberately absent from the slip; Atkinson prose + Anonymous
Pro telemetry only, no new imports), title block, № treatment
(Anonymous Pro Bold 13sp; ReceiptState identical to main),
ReceiptMonoLine helper byte-identical, ink discipline (no
accent ink; Color/Theme/Type/Curves/ReceiptState all
byte-identical to origin/main), slip body/tear metaphor,
section rules (there are none — border-only edge, 8dp rhythm
preserved; the branch did NOT import #10's divider rendering),
spacing rhythm (12/20/8dp), the exact footer
string/size/tracking/ink byte-identical, prose warnings,
HELD/routine/deflection lines, clock print (same-day
ARMED/ENDED byte-identical). TRUTH-MINIMUM: (a) longSpan
exactness — whole-minute values byte-identical to base
(60→`1 MIN`, 3600→`1H 00M` verified); the new SEC class
appears only where the base lied (45→`45 SEC`, was `0 MIN`;
61→`1 MIN 01 SEC`, was `2 MIN`); the 0-input edge is
unreachable in print. (b) RAN total — same mono line, same
8dp cadence, no divider, omission law obeyed (>0 only).
(c) Cross-midnight dates — same mono line/face/size/ink;
same-day slips byte-identical. (d) BREAK BANKED — template
unchanged, figure source only. Scope proof: the branch
touches exactly 3 files (ReceiptSheet.kt,
ReceiptSheetTruthTest.kt, this report); every other
print-adjacent source is byte-identical to origin/main. The
closer's own independent fingerprint (style-token diff,
base-vs-branch) agrees: empty diff outside the cured lines.
**No downgrade landed.**

### What changes for the user (plain words)

- The receipt now prints how long the session actually ran —
  a `RAN …` line under ENDED, in the same mono voice as every
  other line. Before, that fact was never printed at all.
- `BREAK BANKED` now prints the true banked time to the
  second. Before, anything under a minute printed a false
  `0 MIN`, and every other figure was silently rounded down.
- `PLANNED` now prints the true planned length. Before, it
  was rounded up to the next minute (a 61-second plan printed
  `2 MIN`). Whole-minute plans — the normal case — print
  exactly as they always did.
- If a session crosses midnight, ARMED and ENDED now show
  the day (`TUE 4 NOV`) as well as the time. Before, `ENDED
  00:10` after `ARMED 23:50` read as if the session ended
  before it started. Same-day receipts look exactly as before.
- Tapping the dim area around the receipt dismisses it the
  instant your finger touches down. Before, it waited until
  you lifted your finger.
- The receipt's fade-in now actually plays; the code no
  longer claims a fade-out that could never happen.
- Nothing about how the receipt looks has changed — fonts,
  sizes, colours, spacing, the № line, the footer are all
  byte-identical (print finisher's verdict, above).

### COMPOSITION-HELD (labelled explicitly; do not force)

- **COMPOSITION-HELD — BREAK BANKED divergence.** This
  branch prints the exact span; issue #10's composition
  (batch-10) kills the line entirely on semantic grounds (it
  booked a price as banked time). Both cures cannot land.
  The merge train picks the epitaph.
- **COMPOSITION-HELD — dismissal dwell.** #10's composition
  carries a 2-second dwell dismissal; this branch implements
  the header's spec as written — dismissal on the next DOWN.
  The train adopts one or the other; not forced here.
- Merge state overall: batch-10 also carries a near-identical
  RAN cure, a rival dated-ARMED implementation, a
  still-ceiling longSpan, and the W-41/W-43 ReceiptState
  composition (ReceiptHeldFigures). Recommended order: land
  batch-10 first, then rebase this branch and keep only what
  batch-10 lacks (exact longSpan, full MutableTransitionState
  enter, and the BREAK BANKED ruling above).

### DECISION-NEEDED (labelled explicitly; NOT implemented)

- **DECISION-NEEDED — F4 timezone-retroactivity residual.**
  The cured date/time printing resolves wall instants in the
  device's *current* local timezone at print time. A sealed
  receipt re-rendered after the device changes timezone
  (travel) would print its ARMED/ENDED wall times shifted to
  the new zone — the day label would be consistently wrong
  with the times, never internally contradictory, but not the
  session's own wall clock. The priced cure: persist one zone
  field on ReceiptState/PendingReceipt at seal time and plumb
  it through MonolithRepository to the formatters — outside
  this issue's surgical scope. Awaiting Sarthak's ruling; the
  DECISION-NEEDED option is to take the priced cure as a
  follow-up issue.
