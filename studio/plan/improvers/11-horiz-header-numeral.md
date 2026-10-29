# 11 — Header Numeral (Tasks) — Horiz, Improvers

**Element (new):** the queue total as a 44sp numeral at the Tasks header — heir of
`TaskHorizonBarView`, which is dead (SYNTHESIS item 29: a chart of the list that is
right there fails one-fact-one-rendering). The bar's autopsy is wave-1's
`improvers/11-horizon-bar.md`; this file inherits its facts and buries its form.
**Host:** `FocusSequencerScreen.kt` Section 1, today a `FullBleedSectionHeader`
at 11.5sp + an 8dp spacer + a 42dp `AndroidView`. PLAN ONLY — no code written.

---

## CURRENT — the header as it is, the bar as it dies

**The header today.** `FullBleedSectionHeader("HORIZON", "3 TASKS • ", "95m",
useAccentOnNumber = false)`: word and number both at **11.5sp**, the number in
Anonymous Pro via `rememberMixedTypographyText`, colour `onBackground` against
`onSurfaceVariant` words. The screen's single most important pre-START fact —
*how much have I committed to* — is set at label size, right-aligned, at the same
visual weight as the `DONE 1` furniture on the QUEUE header below it. In the
renders (`shots/03_tasks.png`, `shots/night/n03_tasks.png`) it reads as a
caption, not a total. It is also the *truthful* one of the two renderings: its
`computedTotalMins` sums raw `estimatedMinutes`.

**The bar as it dies.** 42dp tray, segments weighted by
`estimatedMinutes.coerceAtLeast(10)` — a different denominator from the header's,
so bar and header describe different queues whenever a task runs under 10m.
Staircase stops after step 2; the largest segment (45m, ~47% of the strip) reads
as an empty raised card by day (#FAF6F0, brighter than the page, unstroked) and
as unfilled track by night (#332A27 on #272320). No scale, no identity, no
`contentDescription`, no motion, no touch. Its death certificate is signed; what
follows is only about what its facts do next.

**The inheritance — three facts the bar touched, one survives:**

| Candidate fact | Verdict | Why |
|---|---|---|
| **Queue total** (Σ `estimatedMinutes`) | **THE numeral's fact** | Settled by synthesis item 29; it is the ARM question ("how much have I committed to") and it has no other hero rendering — rows show parts, never the sum. |
| Cumulative starts (0 / 25 / 50) | **Dies with the bar** | Each start offset is a per-task fact; the rows already carry order (`#01 · 25m`) and the Deck owns time once a session starts. A second rendering in the header would be the bar's disease in miniature. |
| Elapsed marker (`actualSecondsSpent` on the active task) | **Dies with the bar** | Elapsed belongs to the running session (Deck / AOD). On Tasks, pre-START, it would render session state on the planning surface — two homes for one fact. |

**The numeral's language — duration, never clock time.** The total prints as a
duration in the same units the rows speak: `45M` under an hour, `1H 35M` above
it, `2H` (never `2H 0M`) when the remainder is zero. The alternative — a
finish-by clock time ("done by 14:20") — was considered and rejected on TRUTH:
it is a *derived* fact that silently assumes start-now, no breaks, no routine
pre-emption; it needs a ticking `now` to stay honest, decays every minute it
isn't recomputed, and would make the header assert a promise the app cannot
keep. Duration is the sum; clock time is a forecast. The numeral prints the sum.
One fact, one rendering, human language.

**Voice and colour, fixed before the ladder:** digits in **Anonymous Pro Bold**
(real 700 file now shipped — no synthesized smear), unit letters (`H` / `M`) in
**Atkinson Hyperlegible Bold** at 16sp on the same baseline. Ink only: day
#252323 on page #E4DFDA; night numeral #D2C6BB (≈11:1 tier) on ground #171412,
label #9C8E84, zero-state #776A60. **No orange** — the Tasks accent budget is
already spent on START's text, and a total is not a verb. Never green. The
numeral is never a touch target at any rung: it owns no action, and a tappable
number would promise an editor the rows already are.

---

## LADDER — each rung independently shippable

### Rung 1 — PROMOTE: kill the bar, crown the sum

- **Mechanism:** delete the `AndroidView` + 8dp spacer from Section 1 and the
  `TaskHorizonBarView` class (single host — verified: only
  `FocusSequencerScreen.kt` references it; the stale "#F55314 on task #1" KDoc
  dies with it). Replace `FullBleedSectionHeader` for this section with a
  numeral block: label row `TASKS` (Plus Jakarta Sans Bold 10sp, tracked
  0.6sp, `onSurfaceVariant` / night #9C8E84) left, count `3 QUEUED` in the same
  tier right; below it the numeral, left-aligned: digits Anonymous Pro Bold
  **44sp**, line-height 44sp, letter-spacing 0 (mono — digit-count growth moves
  nothing else), unit `M` Atkinson Bold 16sp, baseline-aligned, 4dp after the
  digits. Format at this rung: raw minutes always (`95M`) — human H/M splitting
  is Rung 2. Block height fixed at 68dp (16dp section spacer + 14dp label +
  44dp numeral line, rounded) so queue content never shifts when the total
  changes digit count.
- **Data:** `computedTotalMins` — the header's existing raw sum. The bar's
  `coerceAtLeast(10)` denominator dies unmissed; from this rung on, numeral =
  Σ rows, checkable by eye.
- **Empty state:** numeral `0`, unit `M`, both in the muted tier (day
  `onSurfaceVariant`; night #776A60), label right reads `NOTHING QUEUED`. The
  block keeps its 68dp — the screen does not jump when the first task lands.
- **Motion:** none. The value re-strikes on recomposition, instantly. **Curve:**
  n/a. **Haptic:** none. **Sound:** silent — nothing has happened yet at this
  rung except a number getting its proper size; see Rung 2 for the full defence.
- **Depth/tokens:** flat print on the page ground. No card, no recess, no
  stroke — the numeral is type, not furniture. Tokens: existing `onBackground`
  (day ink) / night #D2C6BB override; label tokens as above.

### Rung 2 — HUMAN LANGUAGE + THE LETTERPRESS RE-STRIKE

- **Mechanism (format):** the formatter splits at 60: `<60` → `45M`; `≥60` →
  `1H 35M`, remainder-zero → `2H`; hours ≥10 keep the same shape (`10H 15M`).
  Digits stay 44sp Anonymous Pro Bold; `H` and `M` both Atkinson Bold 16sp,
  each 4dp after its digit group, groups separated by 6dp. Count label
  unchanged. This is the rung where the header stops speaking machine (`95m`)
  and starts speaking the language the rest of the screen uses.
- **Mechanism (change):** the numeral is a *computed* number — it may never
  roll, flip, or count through intermediates (95 → 120 never passed through
  96…119 glyphs; drawing them would be a lie in motion). On any value change:
  **instant glyph swap + letterpress settle** — the new glyphs draw at 88% ink
  alpha and settle to 100% over **120ms, Curve A** `cubic-bezier(0.2, 0.0, 0.0,
  1.0)`, no size change, no intermediate values, ever. Interruption: a second
  change inside the 120ms restarts the alpha settle from the current alpha
  with the newest glyphs. Animator scale 0 → full ink immediately.
- **Haptic: none — defended, and this defence is final for all rungs.** My
  wave-1 self defended the bar's silence because a readout isn't touched. The
  numeral inherits the question, and the answer survives being re-asked: every
  change to this total was caused by a control that already fired its feel —
  ADD's latch, DONE, DEL, the composer's keycaps. A buzz from the numeral
  would double-fire the same event and teach the hand that the confirmation
  channel also means "a number changed somewhere." Haptics mean *your hand
  did that*; the numeral's hand is never on it. **Sound: silent**, same
  argument one level up — the synth gives controls a mechanical voice; a
  passive total that clicked on recomposition would be a notification, and
  this screen has no notification semantics.
- **Depth/tokens:** unchanged from Rung 1. The 88→100% alpha settle is the
  only depth cue — ink arriving on paper.

### Rung 3 — ENGRAVED DEPTH, NIGHT STAIRCASE, THE SPOKEN TOTAL

- **Mechanism (depth):** the numeral is printed *into* the surface, not laid
  on it: glyphs drawn twice — a 1dp-offset under-print first (day: #FAF6F0 at
  50% alpha, offset +1dp y; night: #000000 at 60% alpha, offset +1dp y, with
  the main pass in #D2C6BB), main pass on top at the settled alpha from
  Rung 2. No blur, no shadow elevation — a letterpress bite, two draw passes,
  zero new tokens beyond the night staircase values already legislated
  (ground #171412, tiers #D2C6BB / #9C8E84 / #776A60).
- **Mechanism (scale discipline):** at totals ≥ 600 minutes (3-digit hours
  after splitting, e.g. `10H 15M` still fits — the step-down triggers at
  ≥ 100H, practically never) — withdrawn as over-engineering; instead the
  real edge is handled: the numeral block allows the digit group to grow
  rightward freely (left-anchored, mono) and the count label is right-aligned
  to the screen margin, so no realistic total (≤ 99H 59M) collides.
- **Mechanism (accessibility — part of the rung, not a garnish):**
  `contentDescription` on the block: *"Queue total: 1 hour 35 minutes, 3
  tasks queued."* — spoken form expands the units the print compresses, built
  from the same `computedTotalMins` and queue size the block draws. Empty:
  *"Queue total: nothing queued."*
- **Motion:** Rung 2's re-strike, unchanged; the under-print f
...[truncated 3072 chars]ades with the main pass
  (same alpha driver), so the bite never separates from the glyph. **Haptic:**
  none. **Sound:** silent (Rung 2 defence).

### Rung 4 — THE MASTHEAD (peak rung, specced in full under PEAK)

- **Mechanism:** the numeral block leaves the `LazyColumn` and becomes a fixed
  header above it — the total stays in view while the queue scrolls beneath
  it, because a sum you must scroll back to check has stopped answering at a
  glance. At scroll position 0 the block sits on the page ground, flush, no
  separation. Once the list scrolls under it, the block takes **one surface
  step up** (night #1F1B18 on ground #171412; day #D8D1CB-variant step on
  #E4DFDA) plus a 1dp hairline on its bottom edge — state as position: raised
  only when something is beneath it.
- **Motion:** surface step + hairline alpha cross-fade **180ms, Curve A**
  `cubic-bezier(0.2, 0.0, 0.0, 1.0)` on scroll-state change only; the glyphs
  themselves never move, scale, or fade with scroll. **Haptic:** none —
  scrolling already has its own feel and the block is not the scrolled thing.
  **Sound:** silent.
- **Depth/tokens:** one tonal step + hairline, per the depth law (tone steps
  and hairlines, never shadows). No elevation, no blur.

---

## ★ BEST-RATIO CHANGE — Rung 1, priced

**★ Promote and kill: delete `TaskHorizonBarView` + its `AndroidView` host,
set `computedTotalMins` at 44sp Anonymous Pro Bold in the header block.**

Price: one host edit in `FocusSequencerScreen.kt` (remove ~15 lines of
AndroidView/spacer/import, add a ~25-line Compose numeral block), one class
deletion (~72 lines, single host — verified), zero new tokens, zero new
dependencies, no ViewModel or data change. It simultaneously (a) executes the
settled kill, (b) gives the ARM question its 44sp answer, (c) buries the
`coerceAtLeast(10)` denominator lie — numeral and rows describe the same queue
for the first time — and (d) frees 50dp of vertical space to the queue.
Everything above Rung 1 is craft; Rung 1 is the repair and the coronation in
one commit.

---

## PEAK (≤8 lines)

The Tasks header is a masthead, not a caption: `TASKS · 3 QUEUED` in quiet
10sp, and beneath it the commitment itself — **1H 35M** — Anonymous Pro Bold
44sp, letterpress-bitten into the ground, ink only, never orange.
It is the raw sum of the rows below it, in the language a human plans a day
in; it re-strikes in place when the queue changes (120ms ink settle, no
intermediate glyphs, no haptic, no sound — other hands caused that change
and already spoke for it).
Pinned above the scrolling queue, it answers "how much have I committed to"
before the first START, from anywhere on the screen.
At zero it says so at full size and means it: `0M`, NOTHING QUEUED.

---

## STANDARDS CHECK

- **ARM ≤10s:** PASS — the numeral is pre-START information, zero interaction,
  zero animation blocking the path; it *shortens* the arm decision (total
  commitment readable in one glance, at 44sp, without summing rows mentally).
- **HOLD absolute:** PASS — untouched; the numeral renders queue state only
  and offers no mid-session decision (it is never tappable at any rung).
- **TRUTH once:** PASS — one fact (queue total), one rendering (the numeral),
  one source (`computedTotalMins` = Σ raw `estimatedMinutes` = Σ printed
  rows). Cumulative starts and elapsed were explicitly refused a second home.
  Duration chosen over clock-time precisely because clock time would assert
  an unverifiable forecast.
- **His picks:** Anonymous Pro (real Bold shipped) is the numeral's voice;
  Atkinson for units; warm palette; night staircase ground #171412, tiers
  #D2C6BB / #9C8E84 / #776A60; never green; no orange on the numeral (Tasks'
  one accent stays on START).

## CONFLICTS

- **FullBleedSectionHeader reuse:** Rung 1 replaces this section's header
  only; QUEUE keeps the shared component. If Ilya wants header consistency
  enforced app-wide, that's a chrome decision — flagged, not taken.
- **Zeile (rows):** contract exchanged by mail (her letter, thread
  header-total-truth; my replies, threads numeral-sum-contract +
  header-total-truth). Agreed: summed set is exactly `orderedQueue`; total
  re-strikes only when a row leaves the set (never on DONE press or during
  her strike + 120ms hold); mid-session the frozen queue freezes the total
  by construction. Format divergence — her rows keep raw `{minutes}m`, the
  numeral splits to H/M at ≥60 from Rung 2 — **CLOSED by her reply (05:30
  mail): accepted, no escalation** ("95m is machine talk; 1H 35M is the same
  number spoken by a person… reconciles to the minute"). The Rung 1
  raw-minutes fallback stays specced but is no longer expected to fire.
- **Routines:** no dependency taken. A "fits before next routine" line was
  considered and rejected — it would import routine data into the numeral
  and mint a second, derived fact. Routines stay his, elsewhere.
- **Vertical space:** killing the bar frees 50dp; this plan spends none of
  it (numeral block is 68dp fixed vs the old header + spacer + bar ≈ 85dp).
  The freed space belongs to the queue/composer owners to claim.
- **Mail sent:** Nadia (mandatory — heir disposition + ladder top step,
  thread header-numeral-heir) ✓ · Zeile (sum contract + format-divergence
  reply, threads numeral-sum-contract, header-total-truth) ✓ · Commons
  wall post ✓ · Inbox read before finishing ✓ (Zeile's letter, moved to
  read/).
