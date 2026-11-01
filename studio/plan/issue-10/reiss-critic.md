# REISS — BRUTAL CRITIC, Issue #10 (Receipt audit fixes)
## Seat: Reiss, dedicated critic, flat under Ren. Scope unrestricted.
## Target: falsehood only. The slip's printed character is loved and protected — nothing below attacks it.
## Tree audited: branch agent/issue-10, HEAD f14f3b5 (wave-1 e39ae62 + wave-2 d126c07 + Klappe test repairs). Nothing compiled (standing law).

Every charge files with file + line in the CURRENT tree, or it does not file.

---

## PART 1 — CHARGES THAT STILL PRINT (live lies on the slip today)

### R-01 (HIGH) — RAN, the new total, overstates by design: the ceiling formatter was never cured
Wave-1 shipped A1 as `RAN {longSpan(sealedElapsedSeconds)}`
(ReceiptSheet.kt, `if (receipt.sealedElapsedSeconds > 0)` block).
`longSpan` in the same file ceilings: `val minutes = (seconds + 59) / 60`.
A 61-second span prints `RAN 2 MIN`; a 20-second FLOW finish prints
`RAN 1 MIN`. PLANNED is a target — ceiling is a dialect there. RAN is
the lived span, the one figure six audit seats demanded *because it
is what actually happened*, and it is systematically high by up to
59 seconds on every non-exact slip. Zettel's own cell critic filed
this as Attack 3; Zettel's plan (ISSUE-10-receipt-cell.md, Ruling 2)
preserved it as an "OPEN QUESTION for Ren" — and wave-2 then shipped
around it without mentioning RAN's rounding once. The total was the
audit's unanimous cure. It landed as a rounded-up claim.
Charge stands against: the audit (specced the formatter without
pricing exactness), the cell lead (parked the charge, shipped the
line), and wave-2 (silence).

### R-02 (HIGH) — PARTIAL still prints bare, and wave-2's report calls that "STATE-ONLY"
Audit item 7 ruled PARTIAL STATE-ONLY: it prints only with its
evidence (`PARTIAL · {attended} OF {window}`), because the word
alone spans 16 attended minutes to window-minus-16.
Current code: `MonolithRepository.receiptOutcomeWord` returns
`"PARTIAL"` for ROUTINE_PARTIAL, and ReceiptSheet.kt's outcome
segment suppresses only `"ENDED"` and `"ENDED · SHIELD GAP"` —
every other word prints. So a PARTIAL slip today prints
`ENDED HH:mm · PARTIAL` with no evidence anywhere on the paper.
ISSUE-10-WAVE2-REPORT.md §1 then states "PARTIAL stays STATE-ONLY
(its evidence cure is B5, parked)" — that sentence is false as a
description of the printed slip. It prints. Bare. Worse: on a
routine row `sealedElapsedSeconds = row.spanElapsedSeconds`
(MonolithRepository.kt assemblePendingReceipt), and after finalise
the row's `elapsedSeconds` IS the attended figure
(finaliseRoutineSession writes `elapsedSeconds = attended`) — so
the slip already carries PARTIAL's evidence, unnamed, one line
below, labelled RAN. Same seconds, two names, neither identified.

### R-03 (HIGH) — PLANNED still prints the tamper penalty as the plan (item 4 / C3, still live)
Assembly (MonolithRepository.kt): `plannedSeconds = ... row.spanTargetSeconds`.
`spanTargetSeconds` (MonolithModels.kt) is built from
`effectiveTargetSeconds = targetDurationSeconds + penaltySecondsAdded`.
A mid-session tamper penalty is therefore printed inside PLANNED —
the exact recompute lie audit item 4 charged. `sealedSpanTargetSeconds`
exists nowhere in the tree (grep: no hits). Wave-2 lists C3 as a
remainder — honest in the report, but the slip in hand still lies,
and no charge in wave-2's roster says so in those words.

### R-04 (HIGH) — WITHIN still re-derives live on hand-armed rows (item 8 / C2, still live)
Assembly's hand-armed branch calls `routineWindowLabelAt(row.startWallTimeMillis)`,
which reads the CURRENT routine table and its CURRENT `enabled`
flags (MonolithRepository.kt, routineWindowLabelAt). Disable or
rename a routine after the session and the restored slip rewrites
its WITHIN line — first printing and rehydration can disagree.
RC-03's freeze is still another lane's unlanded work. Same status
as R-03: remaindered in prose, live on paper.

### R-05 (MEDIUM-HIGH) — The faulted slip still asserts the present tense forever
ReceiptSheet.kt faulted line, verbatim, unchanged by wave-2:
`SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)`.
On a restored slip days later it re-asserts "STILL BLOCKED" as a
present fact with no wall time attached. Audit item 14 charged
exactly this (Una: the line never ages) and priced an interim cure
(carry the fault's wall time). Wave-2's Feel-Timing seat signed the
ageing half REMAINDER — correct — but A9 is then reported as the
faulted fix, and A9 only collapses the header row. The stale
present-tense assertion is the faulted slip's only sentence, and
it is still unaged, still engineer-speak, and the retry that IS
scheduled (faultWall + 30 000 in the VM) is still never printed.

### R-06 (MEDIUM) — A7's "feed not fade" still fades
Wave-2 §6 and the sheet's own header claim the crossfade was
killed for a slide-feed. ReceiptSheet.kt's enter transition is
`slideInVertically(...) + fadeIn(MonolithCurves.specC())`.
The fade is still in the transition, composed with the feed.
Either the fade is wanted (then the report and the code comment
both misdescribe the arrival) or it is not (then A7 is half-done).
As filed, the documentation lies about the motion.

### R-07 (MEDIUM) — A8's dwell comment promises pass-through the code does not do
ReceiptSheet.kt's A8 comment says the in-flight tap "passes to its
own target". The implementation is a `pointerInput` +
`detectTapGestures(onPress)` on the full-size scrim Box: during
the dwell the tap is consumed by the scrim and goes nowhere — it
neither dismisses nor reaches its target. Wave-2's report admits
this honestly in two places (Interaction-detail note; Remainders:
"a scrim tap is eaten, not passed"). The code comment in the sheet
itself claims the stronger, false version. A reader of the code —
the next builder — is lied to where a reader of the report is not.

### R-08 (MEDIUM) — The footer guard ignores the very variable the audit named: fontScale
Audit item 13 / Druck P-7: the footer must split "if it cannot fit
one line at his fontScale". ReceiptSheet.kt's guard is
`BoxWithConstraints { if (maxWidth < 320.dp) split else maxLines = 1 }`
— width only. At 360dp with fontScale 1.3 the 60-char cured sentence
no longer fits one line, `maxLines = 1` truncates it silently, and
no split ever triggers. The "~315dp" measurement in the code
comment is asserted, never rendered (wave-2: "Ren verifies at
first render" — i.e. nobody has). The guard cures the narrow-phone
case and leaves the large-text case — the audit's case — open.

---

## PART 2 — THE FILTER'S VOCABULARY IS STILL PART FICTION

### R-09 (MEDIUM) — "Blocked" has no production writer; "Split" can never reach the filter
ReceiptState.kt `InterceptOutcome.BLOCKED_OUTCOMES` =
{"Blocked", "Stayed Focused", "Split Into 10m Micro-Step"}, with a
header claiming the vocabulary was "enumerated from EVERY writer
in the tree". Verified writers (MonolithViewModel.kt):
"Used 2m Peek" (peek grant), "Stayed Focused" (transmute),
"Split Into 10m Micro-Step" (splitActiveTaskAndReturn).
- "Blocked" is written by NO production code — only test fixtures
  (ScreenshotHarnessTest seeds). Its own comment now admits
  "harness/test spelling". A test dialect in the production
  allowlist, presented as enumeration, is assumption wearing
  evidence's clothes. Zettel's Ruling 3 ordered it struck at first
  compile if no writer exists. No writer exists. It still ships.
- "Split Into 10m Micro-Step" is logged with
  `sessionIntention = microTask.title` (splitActiveTaskAndReturn),
  while assembly requires `log.sessionIntention == row.intention`.
  It can never match. Inert today; armed the day anyone "fixes"
  the intention mismatch — splits (a man redirecting his own task)
  would start printing as deflections by a classification nobody
  argued. The cell critic filed this (Attack 2). It survived both
  waves untouched.
The core cure is real and I acquit it: "Used 2m Peek" no longer
prints as a block, and the filter runs before dedupe
(MonolithRepository.kt assemblePendingReceipt). A peek printing
as a block was the slip's worst lie; it is dead. The vocabulary
around that cure is the charge.

### R-10 (HIGH) — The deflection list is still resolutions, not attempts — and the contract still says "attempt"
Intercept rows are written only at resolution (the three VM call
sites above). `triggerAirlockIntercept` writes nothing; the
accessibility service writes nothing. An attempt he abandons —
backs out, waits out — leaves no row and can never print.
ReceiptState.kt still defines ReceiptDeflection as "a blocked
attempt the machine turned away". Post-A4 the list is honest
about *which resolutions* it shows and silent about *which
attempts never became resolutions*. The audit verified the rows;
nobody — audit, wave-1, wave-2 — has corrected the contract's
definition. N3 from the cell critic stands unanswered.

### R-11 (MEDIUM) — Dedupe still keys sites by the browser's package
Assembly dedupe keys on `packageName` (30s, MonolithRepository.kt).
Site intercepts log under the browser's package with the domain in
the name slot (MonolithAccessibilityService.kt site path →
VM resolution). Two different blocked sites in one browser 20s
apart collapse to one printed line. The audit's acquittal —
"30s dedupe verified as contracted" — is true for apps and false
for half the list's population. Wave-2 built neither the site
distinction nor a dedupe-key fix, and says so; the acquittal in
AUDIT-receipt.md still stands uncorrected for the next reader.

---

## PART 3 — W-41: THE OMISSION IS HONEST; THE DEPENDENCY STORY IS NOT

### R-12 (MEDIUM-HIGH) — Acquittal first: the HELD line's omission is the one fully honest act in this issue
`heldLine` is null today, the summed line is dead in the sheet,
and an absent line replaced a false one. That is the slip's own
law, correctly applied. No charge on the omission itself.

### R-13 (HIGH) — "At seal" is a birth lie waiting to be reborn
The dependency is everywhere described as columns
`heldShutAtSeal`/`heldPeekAtSeal` stamped "at seal". The existing
held columns are written at BIRTH, in startSession's insert
transaction (MonolithRepository.kt: `countHeldApps(...)` +
`heldSites = dao.getAllSitesSnapshot().size`, written into the
inserted row) — under a comment claiming "the set the shield will
actually hold for this row". Meanwhile the enforcer reads tiers
LIVE and a session's rules can only tighten mid-session, so birth
counts can understate the held set at end. If batch-10's columns
are written by the same birth writer — the path of least
resistance — W-41 returns wearing seal names over birth facts.
The cell plan names the requirement ("stamped at seal, from
enforcement state, by a named writer") and then names no writer.
Until a writer is named, the dependency is a wish, not a spec.

### R-14 (MEDIUM) — The split the dependency waits for already exists — twice — and is thrown away
`MonolithViewModel.appsSplit` already counts LOCKED vs PEEK_2M,
and `SealFreeze` already captures shut/peek AT the seal instant
(MonolithViewModel.kt SealFreeze construction, shutCount/peekCount
from `resolvedTier == LOCKED / PEEK_2M`). The audit priced C1 as
future data work without noting the seal-instant split is computed
in-process today and discarded before the row is read. Persistence
for rehydration is genuinely missing — that part of the price is
real. The audit's silence on the existing freeze is not.

### R-15 (MEDIUM-HIGH) — The audit's sites mechanism was false, and AUDIT-receipt.md still teaches it
Audit item 9: an emptied list prints `0 SITES` "while the enforcer
silently holds the 8 factory domains". There is NO enforcer
factory fallback — MonolithAccessibilityService.kt says so on its
face (UNA-06 comment: "There is NO factory fallback — an emptied
list blocks no websites"). The real mechanism is uglier:
`ensureSeededAndSyncPackages` (MonolithRepository.kt) re-inserts
all default domains whenever the sites table is empty, at every
VM birth and refresh — his emptied list silently resurrects, with
no seeded-once flag for sites. Zettel ruled W-41 "verified wider
than filed" on a mechanism that does not exist. The cell critic
corrected it (N5); the wave-2 report's W-41 remainder still specs
an "enforcer-derived sites figure" against the wrong mechanism;
and AUDIT-receipt.md itself was never amended, so the next
builder who reads the audit alone inherits the false story.

---

## PART 4 — THE SAME BLINDNESS, STILL LIVE WHERE IT JUDGES (outranking every print cure)

### R-16 (HIGH) — N1 stands: a peek still counts as attendance
`computeAttendedSeconds` (MonolithRepository.kt) reads intercept
logs in the window with NO outcome filter and NO intention filter:
every log becomes a 5-minute coverage arc feeding ATTENDED /
PARTIAL / MISSED. A "Used 2m Peek" row — the machine letting him
in — books the same attendance evidence as a turn-away. The
receipt cell routed this to #12/#13 as an "outgoing charge".
Routing is not curing, and issue #10's closing narrative must not
imply the receipt's truth problem is print-deep: the slip's
PARTIAL word (R-02) is judged by this engine.

### R-17 (HIGH) — N2 stands: a let-in still prints live as an "urge stopped"
`recordIntercept` (MonolithRepository.kt) increments
`phantomReachesBlocked` for EVERY resolution, peeks included, and
MonolithShieldService.kt:144 prints that counter as
"{n} urges stopped" on the live shield — mid-session, on the
surface he actually watches. A4 cured one of three readers of the
same conflation. The other two (this counter; the VM's
totalInterceptions) are untouched by both waves.

---

## PART 5 — PROCESS AND PAPER HONESTY

### R-18 (MEDIUM) — ReceiptWave2Test pins a state production can never mint
ReceiptWave2Test.kt's fixture builds ReceiptState directly with
kind = ROUTINE_SLEEP_STANDDOWN for every test, then one test feeds
it outcomeWord "ENDED" — a combination `receiptOutcomeWord` can
never produce for that kind (stand-down always stamps STOOD DOWN).
The suite tests the sheet's rendering of hand-made states, never
assembly: no test touches the outcome filter at repository level,
RAN's rounding (the 61-second case), PARTIAL's bare print, or the
footer at a large fontScale. Four tests, all DECLARED and unrun.
They are evidence of intent, not of truth, and must not be cited
as verification when this issue closes.

### R-19 (LOW, process) — The audit's evidence lines were stale, and two filings inherited them
Critic N7 stands: audit line numbers drifted throughout
(countHeldApps cited :1255, actual in critic's tree :1318, today
in Repository at countHeldApps definition after further drift;
InterceptLogEntity cited :434 vs actual :451-460 in the critic's
tree), and the B-stamps priced "+DB v12" when the tree was already
AT v12 (MonolithDatabase.kt version = 12 today) — that work is
v13. Substance survived re-verification; navigation by the audit's
numbers lands in wrong functions. An audit that demands evidence
discipline of others owes its own readers a correction pass. None
was filed.

### R-20 (MEDIUM) — "Ren's batch-10 hand-work" is this issue's landfill
W-41's columns, RC-03, sealedSpanTargetSeconds, the fault pulse
ordering (completion pulse ~700ms vs fault deadline 1520ms — a
faulted finish still sounds clean first; wave-2's Sound seat
explicitly refused to sign it closed), the dwell pass-through,
the footer render check, R3's "Blocked" grep: every unresolved
truth in this issue is deferred to one unnamed batch in one
sentence each. No charge above is closed by being remaindered.
If batch-10 slips, issue #10 closes with R-01 through R-17
standing. The reports should say that in one breath; none does.

---

## ACQUITTALS (binding — the character and the real cures)

- The slip's character, intact in current code: Anonymous Pro
  11sp/1sp mono ledger, long-form durations, zero buttons/CTA,
  surfaceBright brightest object, scrim 0.12, listed-never-summed
  deflections, ENDED from stamped endWallMillis, detention before
  issue. Loved, proven, untouched by every charge above.
- A4's core: peeks no longer print as blocks; filter is
  assembly-local and runs before dedupe (the two placements that
  could have rewritten routine judgements were avoided).
- A1's existence: the total prints. (Its rounding is R-01.)
- A2's source fix: STOOD DOWN stamped at the source, legacy word
  normalised on rehydrated slips.
- A3's noun: HOURS is the true word; ParkDrawer already spoke it.
- BREAK BANKED's kill: a hold price no longer prints as banked
  minutes; the field stays as record.
- Persisted dismissal via prefs: no fake migration, interim law
  honoured, dismissed № survives process death.
- Wave-2's remainders section: unusually honest prose. The
  charges it buries there are charged above for being buried,
  not for being hidden.

## BOTTOM LINE
The slip today prints fewer lies than it did — the peek-as-block
lie and the HELD sum are dead, and that is real. What remains is
not residue: a total that rounds itself up, a grade (PARTIAL)
printed without its evidence while the report calls it
state-only, a plan that still absorbs the penalty, a fault line
frozen in the present tense, and a judgement engine underneath
that still counts being let in as having attended. Close #10
only with R-01…R-17 answered on code, not remaindered into
batch-10.

— Reiss, brutal critic, issue #10. Paper only; nothing compiled.
