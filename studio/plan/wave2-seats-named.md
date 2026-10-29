# WAVE-2 NAMED SEATS — ISSUE #9 (PARK)

*Seat-runner: Wave-2 named-types runner, working under lead VOGT
(issue #9 cell, Hof's cell; branch `agent/issue-9`, clone
`builds/cells/issue-9`). Sources read in full: `studio/plan/AUDIT-park.md`,
`studio/plan/issue-9-plan.md`, current `ParkDrawer.kt` (1,065 lines) and
`GuardLine.kt`, plus the stray builder clone's `GuardLine.kt` and
`studio/plan/issue-9-builder.md` for the fold/kill verdict in §0.
Paper only — nothing compiled, no app code touched, no commits.
Critics unrestricted; every charge below carries evidence or is
marked an acquittal. Prices are estimates in lines / S-M-L, Ren's
build on Sarthak's word is the only proof.*

---

## §0 · THE STRAY CLONE VERDICT — `GUARD_NOT_CHECKED` (builder clone) vs the landed NOT CHECKED print (main cell)

**VERDICT: KILL. Do not fold.**

Evidence, diffed directly (`builds/cells/issue-9-builder/…/GuardLine.kt`
against `builds/cells/issue-9/…/GuardLine.kt`):

- The stray change is one thing only: a public top-level
  `val GUARD_NOT_CHECKED = GuardLinePrint("GUARD — NOT CHECKED")`
  returned from the two unmeasured paths. The printed string is
  byte-identical to what the main cell already lands — the main
  cell returns `GuardLinePrint("GUARD — NOT CHECKED")` literally at
  both paths (`GuardLine.kt:39,42`), pinned by two test files
  (`GuardLinePrintTest.kt:47,55`, `ChromeWaveUnitTest.kt:157–159`).
  On the print itself there is nothing to gain; it is already shipped
  in this branch.
- The vehicle is a regression. The stray clone keeps the OLD
  signature `fun guardLineFor(...): GuardLinePrint?` (nullable).
  The main cell's F-G4 patch (plan addendum) deliberately made
  `guardLineFor` **total** — non-null return, `guardLinePrint` flow
  typed `StateFlow<GuardLinePrint>` with the NOT CHECKED print as
  its initial value, `SealFreeze.guardLine` non-null with a NOT
  CHECKED default (`MonolithViewModel.kt:631–635,698`), and the
  header band's null gate removed. Folding the stray file re-opens
  the null pathway the cell just closed in type, re-breaks every
  `.text` call site the tests now use without `?.`, and re-imports
  the builder dossier's own R-6 problem (a NOT CHECKED seal freeze
  that silently fell through to live derivation — builder §3.3),
  which the main cell answered by making the freeze carry the line.
- The stray clone is stale in a second way: its §2 CHECK sketch
  proposes `onCheckClick: () -> Unit = {}` **defaulted**; the main
  cell's plan states the opposite as a law — the parameter "has
  exactly one host (MainActivity) and no default — a second host
  cannot silently seat the drawer without wiring the verb." The
  stray branch is a superseded draft of this issue, not a patch on it.
- The one real merit in the stray idea — the literal
  `"GUARD — NOT CHECKED"` now lives at four sites in the main cell
  (GuardLine.kt ×2, MonolithViewModel.kt initial value, SealFreeze
  default) — does not need the stray file. If Vogt wants it, it is a
  fresh 15-minute refactor **inside the main cell**: hoist a
  `val` (or const-constructed print) in GuardLine.kt, keep the
  non-null signature, point all four sites at it. That is a new
  change authored against current code, not a fold of a dead clone.

Price of the kill: zero lines in the main cell; the
`issue-9-builder` clone/branch is left untouched on disk for the
record (deletion is Ren's call, not this seat's).

— Wave-2 seat-runner, for Vogt

---

## 1 · Crazy-ideas

**NEW MECHANISM — "Two Witnesses" (cross-examination CHECK).**
Today every grant is believed from exactly one witness, and the
audit's own RESIDUE + Critic C-7/C-8 record where that witness lies:
BIND rows read `instance != null` (presence, laundered fresh every
2 s by the shield loop), and the residency stamp precedes the
own-package filter, so LIVE can rest on Monolith's own windows.
CHECK currently re-asks the *same* witness louder.

Spec: each counted door gets a second, independent read through a
different Android surface than the primary query —
ACCESSIBILITY: enabled-services list parsed from
`Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES` vs the live
`instance != null`; USAGE ACCESS: raw `AppOpsManager` OPSTR check
vs the repository's `usageAccess()`; DEVICE ADMIN: active-admins
list membership vs `isDeviceAdminActive()`; NOTIFICATION LISTENER:
enabled-listener flat names vs `instance != null`. CHECK runs both
reads per door; agreement prints as today; **disagreement prints a
new board word, `DISPUTED`, in FULL ink**, and a disputed door
cannot count toward the warranted n/4 or complete LAST PROVED's
set. No decoy is posted, no pipeline is rehearsed — this is
cross-examination of grants, not the Live-Fire Strip.

Price: M, ~150–200 lines (second-witness queries in
`MonolithRepository.watchGrantQuery`'s neighbourhood, one
`witnessAgree` field on `WatchMeasurement`, one print word + ink
mapping in ParkDrawer, tests per door for agree/disagree). No DB,
no new permission, no new surface. It shrinks C-7/C-8 from
"stands, escalated" to a printed word — the residue becomes visible
on the board instead of true in a filing cabinet.

Second idea, which touches the parked Live-Fire Strip and is
therefore **not proposed for build**: a long-press on CHECK that
escalates from re-measurement to rehearsal. Recorded under
DECISION-FOR-SARTHAK at the foot of this file; issue #21 owns it.

— Wave-2 seat: Crazy-ideas

## 2 · Maximum-implementation-potential

**NEW MECHANISM — "The Vigil": while PARK stands open, no row is
ever allowed to cross into STALE unwitnessed.** The machinery is
all landed: `STALE_AFTER_MS = 45_000`, the open drawer already runs
a 5 s `nowMono` beat (`ParkDrawer.kt`, `LaunchedEffect(visible)`),
and the host already re-sweeps on open. The Vigil closes the loop:
while the drawer is open and unsealed, compute the nearest stamp
expiry among GRANTED rows (`measuredMono + 45s − now`); when the
beat finds a row within one beat of expiring, dispatch
`runWatchSweep()` once. Watching the board then means the board
keeps itself proved — CHECK remains as the manual verb and the
cold-open cure; the Vigil is CHECK made continuous for exactly the
duration a man is looking at it, and it dies with the drawer
(close, seal, or dismissal all end it — the existing
`LaunchedEffect(visible)` scope is the whole implementation
surface, plus the host callback PARK already holds).

Spec/price: S, ~40 lines (one expiry computation + one dispatch in
the existing beat loop, one test: open drawer + ageing stamps →
sweep dispatched before any row prints STALE; sealed drawer → no
dispatch). No DB, no permission, no setting, no new object on the
surface. Battery cost is bounded by the drawer's open minutes —
this is not a background cadence and must never become one.
Maximum potential read of the rest of the branch: the generation
machinery (C-1's cure) is stronger than its current job — the same
`lastCompletedSweepGen` parity that releases CHECKING could stamp
the foot verdict line (§4.3 remainder) for free when that pass lands.

— Wave-2 seat: Maximum-implementation-potential

## 3 · Brutal critic

**Finding: this branch cures 2 of the audit's 12 merge items and
must not be allowed to wear the audit's verdict.** Evidence is the
cell's own plan ("This branch carries §4.1 + §4.5 + the P-7
verification only") read against the current file: the header
inside PARK still prints bare `GUARD` (no `{n}/4`, §4.3 unlanded);
there is still no warrant line, so the board still cannot say LIVE
(§4.2, P-2 — the audit's S1 spine, answered here only by escalation
C-6/C-7); bottom padding is still `bottom = 100.dp`
(ParkDrawer.kt:420) against §4.11's ruled cut to 24; the plate
still prints 8 body lines including the COLOUR line the audit cut
for contradicting the file's own "orange zero" law and the
`DATA: ON THIS PHONE ONLY` segment Fremd falsified (§4.9); the
FIX return verdict is still, in the code's own words, "no word
prints, ever" (§4.6 / P-5 unlanded). Hof's §8 bar — "with §4
landed, PARK becomes what its first half always claimed" — is not
met by this branch and the plan, to its credit, never claims it is.
The charge is against any *future* telling that lets "issue #9
done" blur into "PARK audit done": 8 S + 3 M of merge list remain,
priced in the audit, owned by nobody yet.

Price of the cure: the audit's own §4 total for the remaining
items; price of the dishonesty if it happens: the whole audit.

— Wave-2 seat: Brutal critic

## 4 · Improver

**Finding: one literal, four homes — dedupe the NOT CHECKED print
inside the main cell (the salvageable kernel of the stray clone,
done right).** Evidence: `"GUARD — NOT CHECKED"` is constructed at
GuardLine.kt:39 and :42, MonolithViewModel.kt:~635 (SealFreeze
default) and :~698 (flow initial value). Four constructions of the
app's most truth-critical string is three chances for a future edit
to fix one site and lie at another. Improvement: one
`val GUARD_NOT_CHECKED_PRINT` in GuardLine.kt, all four sites
reference it, signature stays non-null (§0's terms). Price: S,
~10 lines net, existing GuardLinePrintTest already pins the string
so the refactor is test-covered before it starts. Second, smaller:
GuardLine.kt's doc head says "Four states, no fifth" and then
enumerates three bullets (WHOLE / LACK / UNMEASURED — STALE is a
dress of LACK, not a fourth bullet); fix the doc to say what it
means before a future reader invents the missing state. Price: 2
lines of comment.

— Wave-2 seat: Improver

## 5 · Designer

**Finding: the board still has no hierarchy — the tier composition
(§4.7 / §3 of the audit) is wholly unlanded, and the spacing module
is honoured only around the new keys.** Evidence: the board renders
six permission rows flat, a single 16 dp whitespace break, then
three interceptor rows — the exact structure Liv's §1.5 killed
("the 16dp whitespace family break — KILL as the board's only
structure"). No tier eyebrows, no consequence sentences, OVERLAY +
BATTERY still sit as false citizens in the counted family's visual
block. Off-module spacers persist outside the key zone Finisher 2
cleaned: 6 dp under the GUARD header, 14 dp above the board, 32 dp
above the plate — against the ruled {8, 16, 24} module (§4.11).
What the branch did land, it landed in dress: `ParkWordInk`
QUIET/HALF/FULL is a real three-tier ink system and STALE's 0.60
half-ink is the audit's P-4 dress, correctly built. The complaint
is composition, not craft: dress without ranking still reads as
nine equal citizens, and the warrant derivation (§3) proves they
are not equal.

Price: §4.7 as audited — M (eyebrows, consequence line, OFF/NO
DOOR split, false citizens out); spacing sweep S (~15 lines).

— Wave-2 seat: Designer

## 6 · Consumer

**Finding: the plate still sells a number the app no longer owns.**
Evidence: `PLATE_BODY` prints `"PEEK: 2 MINUTES, THEN SHUT AGAIN"`
as a hard-coded string while the lease reads
`PEEK_DURATION_SECONDS = 120` — the plan's own W-08 section names
this "one fact, two sources, already a lie waiting for Frist's
editable times (batch 10) to make it print a stale number," and
prepares the input contract (same value the lease reads; null →
omit the number; 0 → OFF form; 1 → singular). The contract is
prepared; the plate is not built; the consumer-facing fact on the
furniture is still the hard-coded one. A buyer of this app's
premise — it tells the truth about itself — is being told a
constant dressed as a measurement. Acquittal on the neighbouring
charge: `FIRED AS SET` is genuinely gone from the plate (grep
finds it only in the UNA-05 comment and paper), and the surviving
`ROUTINES: SET BY HAND ON SHIELD` claims only what is true.

Price: batch-10's plate work as already contracted; interim
one-line honesty fix if batch 10 slips: none honest exists short
of the contract — do not paper it, schedule it.

— Wave-2 seat: Consumer

## 7 · Student

**Finding: a first-time reader cannot learn from this drawer what
"SELF-REPORT" means, and the audit's cure for that was never
landed.** Evidence: the LAST PROVED line prints
`{HH:mm} · SELF-REPORT`; AUDIT §1.3 ruled the qualifier stays but
"gets one glossary line where the reception reprints it" — no
glossary line exists in ParkDrawer.kt, and the phrase appears
nowhere else in the drawer a student could learn it from. The
distinction it carries is the drawer's whole lesson — this instant
is the app reporting on itself, not a witness — and it is printed
in the app's most compressed register to the reader least equipped
to decode it. Second, same reader: the drawer's header says
`GUARD`, the header band outside says `GUARD — NOT CHECKED` /
`GUARD {n}/4`, and nothing inside the drawer ever reconciles the
two prints (§4.3 remainder) — a student reasonably asks which one
is the count.

Price: glossary line S (~5 lines at the colophon when §4.8 lands;
standalone, one ruled line under LAST PROVED); header count is
§4.3's price, already counted by the Designer seat.

— Wave-2 seat: Student

## 8 · User

**Finding (the 1 AM test, Muede's hour): the one repair trip still
abandons him at the border and still answers in silence.** Evidence
from the current return effect in ParkDrawer.kt: on FIX return the
code waits 800 ms, reads the store live, and — repaired or not —
prints nothing; the comment stands verbatim: "no word prints,
ever." P-5's cure (escort card per door, printed FIXED / STILL OPEN
verdict, Healing Staircase walking all lacks in one trip) is
§4.6, unlanded; FIX still names one lack, still lands him on the
general Accessibility list with "find MONOLITH" said nowhere on
this surface. The CHECK key genuinely improves his hour — he can
now re-prove without leaving — but CHECK cannot fix; the moment a
row reads MISSING, he is back on the silent trip. Partial
acquittal: FIX now commits on release, not on DOWN (HEAD commit
`6237468`, Feel 1 T-1), so a scroll that starts on the key no
longer launches him into Settings — a real 1 AM mercy, landed.

Price: §4.6 as audited — M (~80 lines for the Staircase per Vega's
pricing in the audit, plus the escort card and the printed verdict).

— Wave-2 seat: User

## 9 · User-experience

**Finding: CHECK's success and FIX's success speak in two different
voices, and only one of them prints.** Evidence: a whole CHECK
landing plays the firm click AND clears/prints a verdict line
(`NOT PROVED` + first lack, in board grammar, persistent until the
board reads whole); a FIX return plays the same firm click but is
barred from print by design ("no word prints, ever"). Two repair
verbs, one surface, opposite feedback laws — the user must learn
that on this board a click sometimes means "read the new line" and
sometimes means "nothing will ever be written." Consistency cure
is not to silence CHECK (its print is the audit's P-1 cure working)
but to give FIX the print P-5 already ruled. Second: the
`NOT PROVED` verdict is rendered as a `ParkBoardRow` with name
`NOT PROVED` and the lack as its word — visually a tenth board row;
a scanning eye can file it as a door named NOT PROVED. It wants a
dress distinction (it is a verdict, not a citizen) when §4's
composition pass touches the board.

Price: FIX verdict print is inside §4.6's M (User seat);
NOT PROVED dress distinction S (~10 lines, spacing/rule variant)
whenever the board is next opened.

— Wave-2 seat: User-experience

## 10 · Purpose-questioning

**Finding: PARK's ruled job sentence is still only half obeyed —
the junk-room clause was struck in the audit and the junk is still
in the drawer.** Evidence: AUDIT §0 struck "parks everything that
has no other home" and ruled PARK is "GUARD's drawer, not the app's
junk room"; the current drawer still houses the Sound sled (§1.9
ruled MOVE → SHIELD), the Shorts + Reels sled, a Theme row (§1.10
ruled KILL as a row, survive as footnote), and the spec plate —
alongside the board that is its actual job. The Sound/Them moves
are §4.10, unlanded, so this is not a builder's disobedience; it is
a purpose debt the programme still carries: every surface that
keeps a second job teaches the user that no surface has one job,
which is the exact habit the Deck was corrected for an hour before
the audit. Question put to the lead, not answered here: if §4.10
keeps slipping behind newer work, is the job sentence still the
law, or has it been quietly re-struck?

Price: §4.10 as audited — M (sled to Shield, theme row to
footnote/LOOK slot); the question costs one ruling from Vogt/Hof.

— Wave-2 seat: Purpose-questioning

## 11 · Thoughtful

**Finding: the seal still blinds him to a door that dies at night,
and the cheap cure was priced, ruled, and not landed.** Evidence:
AUDIT P-8 — a door dying after the 20:00 SLEEP seal "cannot even
be viewed until morning — disclosed nowhere"; the cure's cheap
half (the seal prints any ungranted door at seal time, §4.12)
"SHIPS" per the audit, and Critic C-4's ruling in this cell's plan
leans on exactly that cure ("its cure is the seal-time warning
line (§4.12, wider pass)") to justify keeping CHECK dead under the
seal. But §4.12 is not in this branch: the ruling's premise is a
future pass. Until it lands, the compound state is real — sealed,
a door dies, PARK is print-only, the print was frozen at the seal
frame, and no warning was ever printed. The thoughtful detail:
this is the one failure in the drawer that happens *while he is
asleep and cannot act*, which is precisely when a truth-telling
app should have done the telling in advance.

Price: §4.12 as audited — S (seal-time warning line + PARK gate
composing `doorLocked`); until then, C-4's record should carry an
asterisk wherever it is cited.

— Wave-2 seat: Thoughtful

## 12 · Deep-thinker

**Finding: CHECK proves grants; its name lets a man believe it
proves blocking — and the branch's own critic file proves the gap
is structural, not rhetorical.** Evidence: Critic C-6 (interceptor
RUNNING has no stale form; `poke()` bypasses the derivation it
claims to refresh; interceptor stamps re-dated at publish), C-7
(presence-proved BIND doors; NOTIFICATIONS' RUNNING never
captured), C-8 (residency stamp precedes the own-package filter)
all STAND after this branch, escalated as "P-2's machinery half."
So a CHECK that lands whole, plays the firm click, and advances
LAST PROVED certifies a *complete set of permission measurements*
(plan C-11 is precise about this) while the three interceptor rows
below — the ones that say whether anything is actually being
blocked — rest on derivation and presence. The two wholes are
named honestly in the code comments (C-11); they are not named
anywhere the user reads. The deep problem is not that CHECK
overclaims in its copy — its copy claims almost nothing — it is
that a verb named CHECK beside rows named RUNNING invites the
inference the machinery cannot cash. The warrant line (§4.2) is
the honest bridge and it is the cheapest unlanded item on the
list.

Price: §4.2 warrant line — S, as audited; C-6/C-7/C-8 remain
machinery work for Ren to route, unpriced here by design (their
own verifier first, per the plan).

— Wave-2 seat: Deep-thinker

## 13 · Enthusiastic designer

**Finding: the CHECK key's dress ladder is the best new object the
drawer has grown — and it deserves its missing third state.**
Evidence: Finisher 2's ladder (print dress on a fresh board; ink
exactly when a row is STALE/UNMEASURED or a sweep is in flight;
MISSING never promotes it because MISSING's answer is FIX) is a
genuinely intelligent piece of interaction design — the key's ink
*is* its recommendation. What it lacks is a rest state that says
"proved just now": after a whole landing, the key returns to print
dress indistinguishable from a board that was never checked this
visit. Proposal, small and in-grammar: for the remainder of the
open, after a whole landing, CHECK carries the LAST PROVED time as
a quiet suffix in Anonymous Pro (`CHECK · 03:41`) — the key signs
its own last work, using the instant the board already computes.
No new colour, no new object, no animation beyond the existing dip.

Price: S, ~15 lines (one text composition from
`snapshot.lastProvedWallMillis`, one render test pinning the
suffix appears only post-landing and clears on close).

— Wave-2 seat: Enthusiastic designer

## 14 · Wow-level designer

**Finding: the drawer has no signature moment — its most dramatic
event, a board going whole, is celebrated with one click sound.**
Evidence: the whole-landing path (plan §1) plays `playFirmClick()`
and clears a lack line; nothing on the surface moves, aligns, or
resolves. Compare the drawer's own grammar elsewhere — the grip
grows under a finger, keys dip 1 dp and return, the sheet seats at
280 SEAT — this app believes in physical punctuation, and the
moment the machine proves itself whole is the one moment that gets
none. Proposal, inside the material law (print + the two keys are
the only objects; no colour, no new surface): on a whole CHECK
landing, the six board rows' hairlines resolve in sequence — each
rule redraws at full ink for one beat, top to bottom at the board's
existing 49 dp pitch rhythm, ~90 ms apart — a proof visibly
*travelling* down the board it just measured, ending at the LAST
PROVED line as its time advances. It is the receipt printing,
staged. Restraint clause: it fires only on a CHECK landing that
turns the board whole, never on open, never on a FIX return, never
twice in one open.

Price: M, ~60–80 lines (per-row rule alpha animation driven from
the existing landing effect; one test that it does not fire on
partial landings). Filed as a proposal — Wow does not outrank the
§4 remainders; it is priced so Vogt can sequence it honestly.

— Wave-2 seat: Wow-level designer

## 15 · Designer with 29 years of experience

**Finding: twenty-nine years says check the declaration order,
because nobody has compiled this branch — and it does not
compile as written.** Evidence: `ParkDrawer.kt:263–272` —
`val fixDip by animateDpAsState(targetValue = if (fixPressed) …)`
reads `fixPressed` (and the animation spec branches on it twice
more), but `var fixPressed by remember { mutableStateOf(false) }`
is not declared until **line 301**, below the dip animator block.
Kotlin forbids reading a local variable before its declaration in
the same scope; this is not a style wart, it is a first-compile
failure waiting for Ren's one permitted build to discover it —
on a branch whose entire status is DECLARED and whose plan
promises the build as the verdict. The fix is beneath contempt,
which is exactly why it survived fourteen seats: move the
`fixPressed` / `fixPressedItem` declarations (and their
reset effect, which references nothing above it) to above the dip
animator block. Zero behaviour change, zero design change.

Price: S, ~10 lines moved, no lines written. This is MUST-1 at the
foot of this file. (Filed with the veteran's hedge intact: if
Ren's compile proves the ordering legal after all, the move is
still free and the charge converts to a readability note — but no
one should bet a build slot on it.)

— Wave-2 seat: Designer with 29 years of experience

## 16 · Intentionality-focused designer

**Finding: every object in this drawer now has a stated intention
except the oldest one — the grab strip's dismissal law and the
sheet's two rest heights are inherited, and the branch never
re-asks whether PARK, now carrying a verb, still wants to be
dismissible by accident.** Evidence: dismissal has three inputs
(scrim tap, sheet drag past 40 %, PARK re-tap — header comment,
ParkDrawer.kt), and a CHECK in flight is abandoned silently by all
three (`LaunchedEffect(visible)` resets `checkPressGen = -1` on
close; the sweep completes in the store, but the *verdict* — the
firm click or the NOT PROVED line — is composed in the drawer and
dies with it: Critic C-2 blesses the sweep's survival, not the
verdict's). The intentionality question: a man who presses CHECK
and brushes the scrim has asked the app a question and closed the
drawer on the answer. Either that is fine (the board will show
fresh stamps on reopen — true, and arguably enough) or the next
open should print the landed verdict once. The branch chose the
first by default, in a comment about state reset, not by ruling.

Price: ruling costs Vogt one paragraph; implementing the
verdict-survives-reopen variant is S (~20 lines — hold
`lastCheckLackName` at host/store scope instead of drawer
`remember`). Recommendation of this seat: rule it, don't drift it.

— Wave-2 seat: Intentionality-focused designer

## 17 · Synchronous collaboration

**Finding: this issue ran two clones, two GuardLines, and two
CHECK designs in parallel, and the synchronisation between them
was a human reading both — the stray clone is the evidence, not
an embarrassment.** Evidence: `builds/cells/issue-9-builder/`
(branch `agent/issue-9-builder`, base trunk `3ea05bd`) implemented
P-6 with a nullable signature and a defaulted-CHECK sketch
(builder dossier §§2–3) while the main cell was landing the
opposite decisions (total function, no-default parameter) on the
same files; the builder dossier even flags its own open questions
for "Hof" (R-1, R-6, §4 scope note) in a document the main cell's
plan never cites. Both cells were right about the string and
diverged on everything structural. The collaboration mechanism
that eventually caught it is this seat-run, after the fact.
Proposal for every future multi-clone issue: one shared
decisions page per issue in `studio/plan/` (the plan file already
exists — make it the *live* one: signature-level decisions like
total-vs-nullable and defaulted-vs-required get one line there the
hour they are ruled, and any second clone's first act is reading
it). §0's kill is the cleanup; this is the prevention.

Price: process only — one standing paragraph in the cell-plan
template, zero code. The cost of not doing it is priced in this
file: one full seat (§0) spent un-merging a clone that never
needed to diverge.

— Wave-2 seat: Synchronous collaboration

## 18 · Implementation-quality

**Finding: the C-1 cure is the highest-quality implementation in
the branch, and its pattern should be named so it is reused.**
Evidence: CHECKING is released only by
`snapshot.lastCompletedSweepGen >= checkPressGen` — a press is
answered exclusively by the completing set of the sweep it
dispatched, in stated parity with LAST PROVED's complete-set rule;
the plan records the first form's failure mode (raw stamp times,
laundered by the shield loop's 2 s presence re-stamps) and the
DECLARED tests pin both halves. That is derivation-over-timer,
generation-over-timestamp, with the failure history written in the
code comment at the site. Contrast, same file, lower quality: the
FIX return verdict still runs on `delay(800L)` — a wall-clock
guess ("the host's sweep lands within ~300 ms; the stability beat
runs 500 ms past it") in a file that just proved it knows how to
wait on a generation instead. When §4.6 lands the printed FIX
verdict, it should wait on the same generation machinery CHECK
built, not on the 800 ms beat.

Price: reuse note, zero lines now; inside §4.6's M, swapping the
800 ms delay for a generation wait is ~15 lines and deletes a race.

— Wave-2 seat: Implementation-quality

## 19 · Technical execution specialist

**Finding: the branch's execution risk is concentrated exactly
where its author said it would be — the uncompiled surface — and
one defect is already visible on paper (see seat 15, MUST-1).**
Corroborating evidence from the technical seat: (a) two separate
5 s clocks now exist to defeat the same staleness-derivation lag —
PARK's `nowMono` beat inside ParkDrawer and the VM's `guardClock`
flow added for Critic C-3 — correct individually, but they are
independent timers drifting against each other, so the band and
the board can disagree about a STALE boundary for up to one beat;
a single shared tick is the tidy form (S, ~20 lines, one flow
consumed by both). (b) `guardLineFor`'s freshness now depends on
callers passing an honest `nowMono`; the VM passes
`SystemClock.elapsedRealtime()` at combine time — correct — but
the function's purity contract means every future caller inherits
the obligation silently; the doc comment states it, acceptably.
(c) Acquittal: the sweep-overlap race the builder dossier feared
(R-2) is answered in the landed design — completion is judged by
generation parity under `publish()`'s synchronization, and C-2
turns the double-press into the escape hatch rather than a fault.
That acquittal is on the code as read, not on a run: nothing here
has executed.

Price: (a) S as above; (b) zero — documented; MUST-1 carries the
compile risk.

— Wave-2 seat: Technical execution specialist

## 20 · Interaction-detail specialist

**Finding: the two keys part company on the smallest possible
detail — when the press commits — and only one of them is right
for its cost.** Evidence: FIX commits on RELEASE
(`tryAwaitRelease()` gates `onFixClick`; HEAD commit `6237468`)
because its consequence is a trip to Settings; CHECK commits on
DOWN (`onCheckClick()` fires before `tryAwaitRelease()` in its
`onPress`) because its sweep is idempotent and costs nothing — the
code comment argues this explicitly, and the argument is sound as
far as it goes. What it misses: CHECK sits directly below FIX in a
vertically scrolling sheet; a scroll gesture that begins on CHECK
dispatches a sweep the user never asked for, and — the detail that
matters — re-bases `checkPressGen`, so if a deliberate CHECK was
already in flight, a stray scroll-touch silently replaces its
generation and its verdict now rides on the accidental sweep.
Harmless to the store, unfaithful to the press. Cure in the FIX
grammar: dispatch on release for CHECK too; the dip and knock stay
on DOWN (they cost nothing), the sweep joins FIX in costing a
completed press. The idempotence argument survives — it argues the
sweep is cheap, not that accidental dispatch is honest.

Price: S, ~6 lines moved inside the existing `onPress`, plus one
line in the T-5-style render test (press-and-cancel dispatches
zero sweeps). Second detail, acquitted: both keys carry
`maxLines = 1` and spoken content descriptions ("Fix {name}",
"Check guard state") — the accessibility dress on the new objects
is complete as built.

— Wave-2 seat: Interaction-detail specialist

---

## MUST FIXES (before this branch's build is requested)

1. **MUST-1 — Declaration order, ParkDrawer.kt (seat 15).**
   `fixDip` (line 263) reads `fixPressed`, declared at line 301.
   Move the `fixPressed` / `fixPressedItem` declarations above the
   dip animator block (~10 lines moved, zero behaviour change) so
   Ren's single build is not spent discovering it.
2. **MUST-2 — Kill the stray clone's GuardLine change ( §0 ).**
   Do not fold `agent/issue-9-builder`'s `GUARD_NOT_CHECKED` version
   into this branch, in whole or in part: identical string, worse
   (nullable) signature, reverses F-G4's totality. If the constant
   is wanted, author it fresh in the main cell per §0/Improver.

## REMAINDERS (ruled, priced, owned by the wider §4 pass — not this branch, not forgotten)

- §4.2 Warrant line on the board (S) — Deep-thinker seat: the
  honest bridge over C-6/C-7/C-8, which STAND and stay escalated.
- §4.3 Headline `GUARD {n}/4` + Linea's signed foot line (S, ~30 lines).
- §4.4 Age column per row (S) — the dress half (STALE half-ink)
  landed here; the ages did not.
- §4.6 FIX escort card + printed FIXED / STILL OPEN verdict +
  Healing Staircase + placement (M) — User / UX /
  Implementation-quality seats; build the verdict wait on CHECK's
  generation machinery, not the 800 ms delay.
- §4.7 Tier composition: eyebrows, consequence sentences, OFF vs
  NO DOOR, false citizens out of the tier block (M) — Designer seat.
- §4.8 LAST PROVED → foot colophon (S), with the SELF-REPORT
  glossary line §1.3 promised (Student seat, ~5 lines).
- §4.9 Plate cut to 4 lines (S) — COLOUR line and
  `DATA: ON THIS PHONE ONLY` still print (Consumer / Brutal critic).
- §4.10 Sound sled → Shield; Theme row → footnote / LOOK row (M) —
  Purpose-questioning seat.
- §4.11 Bottom padding 100 → 24 dp + spacing module {8,16,24}
  throughout (S) — 6/14/32 dp spacers still off-module.
- §4.12 PARK gate composes `doorLocked`; seal-time warning line
  (S) — Thoughtful seat; C-4's ruling carries an asterisk until
  this lands.
- W-08 dynamic plate (batch 10, Ren's): the PEEK line still prints
  a hard-coded 2 minutes; the input contract is prepared in the
  plan, the plate is not built (Consumer seat).
- New proposals filed by this run, unowned until Vogt sequences
  them: Two Witnesses cross-examination CHECK (Crazy-ideas, M);
  the Vigil — open-drawer self-proving (Max-impl, S);
  CHECK · {time} suffix after a whole landing (Enthusiastic, S);
  hairline-resolve on a whole landing (Wow, M); NOT CHECKED literal
  dedupe (Improver, S); shared 5 s tick for band + board
  (Technical execution, S); CHECK commits on release
  (Interaction-detail, S); CHECK verdict surviving a close
  (Intentionality — ruling first, S if built).

## DECISION-FOR-SARTHAK

- **Live-Fire Strip territory (parked, issue #21) — not built, not
  designed here, per the parked decision:** Crazy-ideas' second
  proposal (seat 1) — a long-press on CHECK that escalates from
  grant re-measurement to a witnessed pipeline rehearsal
  (sacrificial decoy the shield must block, self-posted
  notification the listener must catch). It touches the parked
  strip, so it is recorded here for Sarthak and nowhere else; the
  strip itself, and Vega's original §5 filing, remain exactly as
  parked in issue #21. Nothing in this file's other proposals
  depends on it: Two Witnesses and the Vigil were both scoped
  specifically to stay outside it.
- No other item in this run requires Sarthak: every other finding
  is a ruling for Vogt/Hof or a remainder already priced in
  AUDIT-park §4.

*— Wave-2 named-seats runner, issue #9, under Vogt. Twenty seats,
twenty filings, none silent. Paper only; Ren compiles, on
Sarthak's word.*
