# Issue #10 — Receipt (audit fixes) · FINISHER 4 (footer & faulted sheet)

**Seat:** Finisher 4, Zettel's cell for GitHub issue #10, "Main-app pass — Receipt
(audit fixes)". The slip is a LOVED surface: everything below is a print finish
inside its protected character — mono ledger, brightest object in the frame,
zero buttons or CTA on the paper, omission law (absent over false). Nothing
here redesigns; nothing here compiles, builds, or tests — paper only.

**Source of truth:** `studio/plan/AUDIT-receipt.md`, items 13–14 (read in the
`wt-ci` clone at `~/workspace/clone-inspect/builds/wt-ci/studio/plan/AUDIT-receipt.md`).
**Code read:** this cell's clone, branch `agent/issue-10` —
`app/src/main/java/com/example/ui/components/ReceiptSheet.kt` (211 lines; all
line citations below are to this file unless another file is named), plus
`data/ReceiptState.kt`, `data/MonolithRepository.kt`, `data/MonolithDao.kt`,
`data/MonolithModels.kt`, `ui/MonolithViewModel.kt`,
`service/ReleaseRetryScheduler.kt`, `ui/theme/Color.kt`, `ui/theme/Theme.kt`.

## Scope

**In (this document):**
- (a) The FOOTER — cure A3 (audit item 13, W-43): noun cure, one-line guard,
  night ink.
- (b) The FAULTED sheet's print finish *within current law* — cure A9's head
  half (collapse the empty title row, audit item 14 bullet 2) and item 14's
  interim ageing cure (the restored fault line carries the fault's wall time).

**Out — pending-ruling, group B (registered in §3, NOT specced as decided):**
the `FOR: <intention>` amendment to F-W1 (B3); the corrected-duplicate stamp
`RELEASE CONFIRMED LATER`, the fault line's plain rewording, the printed
retry time, and the pulse-ordering cure (all B4).

**Out (other seats):** fault-statement voice/size (item 16 → A6), shape and
anchor (items 15/17 → A5), dismissed-№ persistence (item 20 interim → A9's
other half, specced by the Designer seat), dismissal gesture (A8).

---

## 1. The footer — cure A3 (audit item 13; W-43)

### 1.1 What is wrong, in evidence

The footer prints at `ReceiptSheet.kt:175–181`:

```kotlin
Text(
    text = "THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT",
    fontFamily = AnonymousProFamily,
    fontSize = 9.sp,
    letterSpacing = 0.25.sp,
    color = scheme.onSurfaceVariant
)
```

Three faults, all audit-verified:

1. **The noun is false (W-43, stands on clause 2 only).** Clause 1,
   `THE MACHINE KEEPS ITS OWN LOG`, is TRUE and acquitted — with the
   precision that the log excludes the slip itself (only № + pointer +
   outcome persist, `ReceiptState.kt:10–18`). Clause 2's `RECORD` claims
   exclusivity that is false: Monolith itself is a study record (session
   history, the Drawer of finished tasks), and `YPT` occurs in the whole
   Kotlin tree only in these prints — no integration exists. What lives in
   YPT is **hours**. The cure concurs Hale's RECORD→HOURS.
2. **No wrap discipline.** The wart comment at L166–174 records a
   measurement, not a law: at 0.25sp tracking the old 62-char sentence
   measures ~326dp against the standard card's ~348dp inner width, so it
   lands on one line *at that width and that fontScale*. Audit item 13:
   at 360dp widths the sentence wraps silently — mid-clause, orphaning
   words (the original wart orphaned `YPT` at 1sp tracking). Nothing in
   the `Text` (no `maxLines`, no guard) controls where a wrap may fall.
3. **Night ink is the mute tier.** The footer takes `onSurfaceVariant`
   (L180) — the slip's *dim* token (also the № at L108 and dim deflections
   via `ReceiptMonoLine`, L195). In the night scheme that token is
   `NightColorOnSurfaceVariant` #776A60, annotated in `Color.kt` itself
   as a 3.5:1 mute tier *over the night ground*; on the slip's raised
   night surface (`NightColorSurfaceBright` #362C27) it measures ≈2.6:1.
   Audit item 13: in night frame n08 the footer is near-illegible. The
   day scheme hides the fault — day `onSurfaceVariant` #5F5856 on
   #FAF6F0 measures ≈6.5:1 — which is why it survived until a night frame
   was shot.

### 1.2 Decisions

- **F1 — Exact string, one source.** The footer text is, verbatim:

  ```
  THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY HOURS LIVE IN YPT
  ```

  60 characters: clause 1 = `THE MACHINE KEEPS ITS OWN LOG` (29 chars),
  separator = ` · `, clause 2 = `YOUR STUDY HOURS LIVE IN YPT` (28 chars).
  Nothing else changes in the wording — clause 1 stands acquitted
  character-for-character, and no synonym, shortening, or "improvement"
  of clause 2 is licensed. In code the string is built from exactly two
  clause constants joined by the separator (e.g. `FOOTER_CLAUSE_1`,
  `FOOTER_SEPARATOR = " · "`, `FOOTER_CLAUSE_2`), because §F3's split is
  defined on those constants — the clause boundary must exist in the
  source, not be re-derived by searching the joined string at render.

- **F2 — Type is unchanged.** Anonymous Pro, 9sp, letterSpacing 0.25sp,
  regular weight, start-aligned, no underline, no link styling. The
  1sp-tracking restoration the L166–174 comment warns against stays
  forbidden. The cure is wording + guard + ink, never a size or tracking
  change: a smaller footer to force a fit is a falsified fit.

- **F3 — The one-line guard (the fit rule).** The footer prints on
  **one line if the whole 60-char sentence fits the slip's inner width
  at the current fontScale; if it does not fit, it prints on exactly
  two lines, split at its own clause boundary — never wrapped
  mid-clause, never truncated, never ellipsised, never shrunk.**
  Exact mechanism for the builder:
  1. One `TextStyle` value is constructed for the footer (family, 9sp,
     0.25sp tracking, lineHeight 11sp — see F4) and is passed *unchanged*
     to both the measurer and the `Text`. A style measured is a style
     printed; two hand-copied styles are a future drift.
  2. The footer sits in a `BoxWithConstraints(Modifier.fillMaxWidth())`
     in its existing slot (last element of the slip `Column`,
     L74–84 — after every other line, on every branch including
     faulted; the footer is unconditional today and stays unconditional).
     Available width = `constraints.maxWidth` in px at that slot
     (already net of the host Box's 12dp padding at L71 and the
     Column's 20dp padding at L81 — measure where it prints, not
     against a remembered card width).
  3. `rememberTextMeasurer().measure(clause1 + separator + clause2,
     style, Constraints(maxWidth = availablePx))` during composition.
     The decision is recomputed on every recomposition — a fontScale
     change, rotation, or width change re-decides it; nothing is
     remembered across configurations.
  4. **Fits** ⟺ measured width in px, rounded up, is *strictly less*
     than the available width in px (at least 1px of clearance — a
     measurement that exactly equals the box is a wrap waiting for a
     rounding difference between measurer and layout).
  5. If it fits: `Text(joined, maxLines = 1, softWrap = false)`.
     If it does not: `Text(clause1 + " ·" + "\n" + clause2,
     maxLines = 2, softWrap = false)` — i.e. line 1 is
     `THE MACHINE KEEPS ITS OWN LOG ·` (the middle dot stays at the end
     of line 1: it is the boundary mark, and keeping it means the two
     printed lines re-join to the exact F1 sentence by replacing the
     break with the separator's trailing space), line 2 is
     `YOUR STUDY HOURS LIVE IN YPT`. `softWrap = false` in both modes
     is the enforcement, not a preference: with the measurer and the
     layout consuming the same style and width, the chosen form
     provably fits, and `softWrap = false` makes a mid-clause wrap
     *impossible* rather than merely unobserved. No `TextOverflow`
     mode is set — overflow must be unreachable, not decorated.
  6. No flicker path: the decision is made in composition from the
     measurer, never from an `onTextLayout` overflow callback that
     first renders the wrapped form and then corrects it. The wrong
     form never reaches a frame.

- **F4 — Two-line form metrics.** The 11sp lineHeight in F3's style
  applies in *both* modes, so switching modes changes only the line
  count: the two-line footer is the one-line footer plus exactly one
  11sp line of height. No extra `Spacer`, no paragraph gap, no change
  to the Column's 8dp `spacedBy` above the footer (L83), no centring —
  line 2 starts at the same left edge as line 1. The footer's quietness
  is carried by its 9sp size; a clause-split footer at midnight is
  still the smallest voice on the paper.

- **F5 — Night ink follows the slip's ink law.** The footer's colour
  becomes `scheme.onBackground` — the slip's one primary ink: the
  token the title prints in (L96), both bold statements print in
  (L151, L163), and every non-dim ledger line prints in
  (`ReceiptMonoLine`, L195). No new token, no alpha, no night-only
  conditional branch: the law is scheme-relative, so the one
  expression resolves correctly in both schemes —
  day: `TokenTextColorPrimary` #2C2A29 on #FAF6F0 (≈13.3:1);
  night: `NightTextColorPrimary` #D2C6BB — annotated in `Color.kt`
  as the 11:1 tier — on the raised night surface (≈8.1:1 against
  #362C27), replacing the mute tier's ≈2.6:1. The hierarchy survives
  the promotion because it was never carried by colour alone: the
  footer remains 9sp against the ledger's 11sp and the title's 15sp.
  **Untouched by this cure:** the №'s ink (L108) and the deflection
  dim tier (L195's `dim` branch) — the one-ink print cure for
  deflections is A10's seat; F5 rules the footer only and must not be
  read as pre-deciding them.

- **F6 — The stale comment dies with the wart.** The L166–174 comment
  ("The footer is exact AND one line… 62-char sentence… Do not
  'restore' the 1sp") is rewritten in the same change to state the new
  law: the F1 string, the F3 fit rule, and the 1sp prohibition. A
  comment that still describes the RECORD sentence and an
  unconditional one line would be a false record sitting on true code.

### 1.3 Resulting prints

One line (fits — e.g. standard width at fontScale 1.0; the cured
sentence is 2 chars shorter than the ~326dp measurement the old
comment records, so the common case does not change height):

```
THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY HOURS LIVE IN YPT
```

Two lines (does not fit at his fontScale / narrow width):

```
THE MACHINE KEEPS ITS OWN LOG ·
YOUR STUDY HOURS LIVE IN YPT
```

There is no third form. A rendering that shows any other break —
`…ITS OWN` / `LOG · YOUR…`, `…LIVE IN` / `YPT`, a clipped `YP…` — is a
failed build, not a variant.

### 1.4 Acceptance checks (paper checks for the builder's proof)

| # | Condition | Required result |
|---|---|---|
| 1 | Standard width, fontScale 1.0, day | Footer = F1 string, one line, `onBackground`, 9sp/0.25sp |
| 2 | fontScale raised until the joined sentence exceeds inner width (test 1.3 and 1.5) | Exactly the §1.3 two-line form; break only after `LOG ·`; `YPT` whole on line 2 |
| 3 | 360dp-wide viewport, fontScale 1.0 | One line if the measurer says it fits, else the two-line form — never a silent mid-clause wrap |
| 4 | Night scheme (n08 conditions) | Footer legible at arm's length; colour token is `onBackground`; no other line's ink changed |
| 5 | Faulted slip | Same footer, same guard — the footer slot and its law do not vary by branch |
| 6 | Grep the tree | `STUDY RECORD` no longer prints anywhere; `STUDY HOURS` occurs only in the footer constants |

---

## 2. The faulted sheet — print finish within current law

### 2.1 What is wrong, in evidence

The faulted branch today (`ReceiptSheet.kt:85–110` head, `:154–165`
fault line):

- The head `Row` (L85–89, `SpaceBetween`) composes the title
  conditionally: clean slips get the Atkinson Bold 15sp `Text`
  (L91–98); faulted slips get an empty `Box(Modifier.weight(1f))`
  (L99–102, comment: "F-W1: the faulted sheet has no title"). The №
  (`№ 048`, Anonymous Pro Bold 13sp, L103–109) prints beside a
  title-shaped absence. Audit item 14 (Aug, faulted frame): the empty
  title row leaves the № floating over a title-height void — dead air
  at the head of the one slip whose news is most urgent.
- The fault line (L154–165) prints
  `SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)` (Atkinson Bold 12sp,
  `onBackground`) whenever `receipt.faulted` — identically on the
  first printing and on a *restored* slip (`ReceiptState.restored`,
  `ReceiptState.kt:185`; rehydration at `MonolithRepository.kt:1783`
  sets `faulted = row.releaseFaultShield`). Audit item 14 (Una):
  `STILL BLOCKED` never ages — on a restored slip the present tense
  re-asserts a stale fact as if the machine had checked just now.
- The fault's wall time exists and is persisted, but never reaches
  the slip: the row carries `releaseFaultedAtWallMillis` (Draht's
  release-fault record in `MonolithModels.kt`, beside
  `releaseFaultShield` / `releaseRetriesDone`), written once by
  `MonolithDao.stampReleaseFault(id, wallMillis)` (`MonolithDao.kt:
  107–110`) from `publishReceipt` (`MonolithRepository.kt:1713–1715`).
  `ReceiptState` (18 fields, `ReceiptState.kt:165–186`) carries
  `faulted: Boolean` (L182) and no fault instant; `rehydrateReceipt`
  reads the flag and drops the stamp.

### 2.2 Decisions — decided now (group A: A9 head half + item 14 interim)

- **S1 — Collapse the empty title row (A9).** On the faulted branch
  the head composes **no title child and no spacer**: the
  `Box(Modifier.weight(1f))` at L101 is deleted, and the faulted head
  is a №-only row — `Row(Modifier.fillMaxWidth(),
  horizontalArrangement = Arrangement.End)` containing exactly the
  existing № `Text` (L103–109, unchanged: `receipt.numberPrint`,
  Anonymous Pro Bold 13sp, `onSurfaceVariant`). The head's height on
  a faulted slip is the №'s own line height and nothing more; the
  Column's standing 8dp spacing (L83) then separates head from the
  `ARMED …` line as it does between every other pair of lines. The
  clean branch is byte-identical to today (title `Text` with
  `weight(1f)` + №, `SpaceBetween`). No placeholder text, no
  "faulted" eyebrow, no icon takes the title's place — F-W1 stands
  whole within current law (see §3): the faulted sheet has no title,
  and after this cure it also has no title's *ghost*.
- **S2 — State carries the fault instant (+1 field, no schema
  change).** `ReceiptState` gains `faultWallMs: Long = 0L`
  (0 = no fault stamp carried; the default keeps every existing
  constructor source-compatible). Wiring, exactly two sites:
  - *Publish* (`MonolithRepository.publishReceipt`, L1701ff): in the
    `if (faulted)` act (L1713–1715), sample
    `val faultWallMs = System.currentTimeMillis()` **once**, pass
    that value to `dao.stampReleaseFault(pending.sessionId,
    faultWallMs)`, and carry that same value into the constructed
    `ReceiptState` (beside `faulted = faulted`, L1731). One sample,
    two consumers — the printed time and the persisted stamp are the
    same instant by construction, never two samples that can straddle
    a minute boundary. A non-faulted publish carries `0L`.
  - *Rehydrate* (`MonolithRepository.rehydrateReceipt`, L1757ff):
    `faultWallMs = row.releaseFaultedAtWallMillis` beside
    `faulted = row.releaseFaultShield` (L1783). No migration: the
    column has existed since the 9→10 Draht record.
- **S3 — The restored fault line carries the fault's wall time
  (item 14 interim).** Print rule for the L154–165 fault line:
  - `faulted && !restored` — the line prints **exactly as today**:
    `SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)`. At its first
    printing the assertion is current; the present tense is true.
  - `faulted && restored && faultWallMs > 0` — the line prints:

    ```
    SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP) · 23:41
    ```

    i.e. today's string + ` · ` + `hhmm(receipt.faultWallMs)`, where
    `hhmm` is the sheet's existing formatter (`ReceiptSheet.kt:
    199–200`, `HH:mm`, the same dialect as ARMED / ENDED /
    deflections) — reused, never reimplemented. The time is the
    fault's own stamp (S2), the instant the finish machine recorded
    the shield's failure to stop — not the moment of restoration,
    not the moment of reading. The present tense is thereby anchored
    to a named instant: the line now *reports* what was true at
    23:41 instead of re-asserting it at whatever hour he reopens it.
  - `faulted && restored && faultWallMs == 0` — the line prints as
    today, with **no time segment**. The omission law governs: a
    stamp-less row (any row whose fault record predates or lacks the
    stamp) gets an absent segment, never a substituted one.
    `endedAtWallMs` and `armWallMs` are expressly forbidden as
    substitutes — they are different instants (session end, session
    arm), and printing either as the fault's time would be a
    derivation wearing a stamp's clothes, the same class of lie
    item 6 accepts only as a legacy fallback for ENDED and never
    extends by analogy.
  - Type is unchanged in all three cases: Atkinson Hyperlegible Bold
    12sp, `onBackground`, same slot (after the shield-off statement,
    before the footer). The statement's voice cure is item 16's
    (A6's seat); S3 changes content on the restored branch only and
    composes with whatever A6 rules for the statement's face.

### 2.3 Resulting faulted heads (illustrative)

First printing — head collapsed, fault line as today:

```
№ 048
ARMED 09:00 · PLANNED 50 MIN
ENDED 09:50 · ENDED
…
SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP)
THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY HOURS LIVE IN YPT
```

Restored (undismissed) faulted slip — settled, no arrival
re-performance (item 18), fault line aged by its stamp:

```
№ 048
ARMED 09:00 · PLANNED 50 MIN
ENDED 09:50 · ENDED
…
SHIELD — STILL BLOCKED (SERVICE DIDN'T STOP) · 09:52
THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY HOURS LIVE IN YPT
```

(Times illustrative of form only; the ENDED outcome word and HELD /
deflection lines are other seats' cures and are shown in today's
form where they appear at all.)

### 2.4 Acceptance checks (paper checks)

| # | Sequence | Required result |
|---|---|---|
| 1 | Faulted finish, first printing | Head = №-only row, no title-height void; fault line has **no** time segment |
| 2 | Faulted slip dismissed per A9 persistence, restart | Never renders again (Designer's §3 contract) — S3 never gets a restored printing to age |
| 3 | Faulted slip **not** dismissed, process death, relaunch | Slip re-presents settled; fault line ends `· {HH:mm}` equal to the row's `releaseFaultedAtWallMillis` formatted by `hhmm` |
| 4 | Restored faulted row with `releaseFaultedAtWallMillis = 0` | Fault line prints with no time segment; no other timestamp appears in its place |
| 5 | Clean slip, either printing | Head and every line byte-identical to today except the §1 footer (string, ink, guard) |
| 6 | Publish-path check | The `faultWallMs` in state and the row's stamped value are the same sampled instant (single-sample wiring, S2) |

---

## 3. Pending-ruling register — group B (NOT decided, NOT specced)

The following are recorded so no builder mistakes silence for a
decision. **Nothing in this section is a spec.** Until Ren rules,
current law — as implemented in §§1–2 — stands whole.

- **B3 — `FOR: <intention>` on faulted slips (Emil's charge on F-W1;
  audit item 14 bullet 1). PENDING REN'S RULING.** The charge: no
  title reads to a stranger as a rendering crash, and F-W1's law was
  written to keep `ENDED-FAULT` off the slip, not to erase his
  intention for the machine's fault. If Ren ratifies, faulted slips
  print `FOR: <intention>` in the mono body; if he does not, F-W1
  stands whole. **Interim law (this doc):** the faulted sheet
  carries no title and no intention substitute (S1), and S1's
  collapse deliberately builds **no slot, hook, or reserved space**
  for the amendment — if B3 is ever ruled in, the head reopens under
  that ruling in its own pass, not by anticipation planted here.
- **B4a — Corrected-duplicate stamp `RELEASE CONFIRMED LATER`
  (Emil P6; audit item 14 bullet 4). PENDING REN'S RULING.** It
  needs the fault flag to be clearable (+DB schema work per the
  audit) and it issues a *corrected duplicate* of an issued slip.
  Note the standing tension it must be ruled against:
  `ReleaseRetryScheduler.kt`'s own header law — a healed fault
  "NEVER amends the issued receipt: the slip printed the truth at
  its instant, and a receipt that rewrites itself afterwards is a
  lie with a timestamp." S3 is not this stamp and does not amend
  anything: it changes what a *restored viewing* prints from facts
  already persisted at the fault instant. The stamp itself is not
  designed, placed, worded beyond its audit name, or priced here.
- **B4b — Fault line plain rewording + printed retry time.
  PENDING REN'S RULING (rides with B4).** Audit item 14 bullet 3
  charges `(SERVICE DIDN'T STOP)` as engineer-speak and asks to
  print the retry fact. The retry exists today — the VM schedules
  it at faultWall + 30 000 (`MonolithViewModel.kt:1277–1281`) on a
  persisted cadence of +30s / +5min
  (`ReleaseRetryScheduler.CADENCE_OFFSETS_MS`) — but under current
  law the slip does not say so, and **the builder must not add a
  retry time or reword the fault line under this document**: the
  verbatim string in S3 stands until B4 is ruled. S3's wall time is
  the *fault's* instant, a different fact from the *retry's*
  instant; printing the former is the audit's interim cure, not a
  back door for the latter.
- **B4c — Pulse ordering (audit item 14 bullet 5). PENDING /
  RIDES WITH B4.** The completion pulse firing at ~700ms, before
  the fault verdict exists (deadline 1520ms), is a VM-side charge.
  No print consequence is specced here; a faulted finish must not
  sound the clean-finish pulse first *when that cure lands* — this
  doc neither implements nor blocks it.

---

## 4. Dependencies and what this finish deliberately does not touch

1. **A6 (item 16) — statement voice.** If A6 moves the two bold
   statements to Anonymous Pro Bold 13sp/1sp, S3's restored string
   (including its `· HH:mm` segment) inherits that face whole; the
   segment is part of the line, never set in a second style or size.
2. **A9 persistence (Designer's §3).** S3's restored printing only
   ever occurs for a faulted slip that was *not* dismissed — the
   settled contract (faulted slips dismiss only on their explicit
   act, and re-present until they do). The two cures compose:
   persistence decides *whether* a restored slip exists; S3 decides
   what its fault line says when it does.
3. **A5 (items 15/17) — shape/anchor.** The head collapse (S1) and
   footer guard (F3) are internal to the Column and independent of
   the card's radius/border/anchor cure; F3 measures at its own
   slot, so an A5 width change re-decides the fit automatically.
4. **Not touched:** the №'s greyer ink (item 2 is a MOVE charged to
   another seat — S1 keeps the № Text exactly as printed today);
   deflection ink and form (A10/A4); outcome words (A2); the RAN
   total (A1); TEAR and SLIP BOOK (parked, issue #21) — nothing in
   §2 pre-builds either, and the restored-slip time segment is not a
   book entry, a re-summon, or a corrected duplicate.

— Finisher 4, Zettel's cell (issue #10). Paper only; nothing compiled.
