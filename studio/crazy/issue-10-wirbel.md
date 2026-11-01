# WIRBEL — CRAZY IDEAS FOR THE FINISH RECEIPT (Issue #10)

**Seat:** Wirbel, crazy-ideas only. No implementation, no audit, no merge.
**Subject:** the finish slip Sarthak ❤️'d (Oct 29, batch-7 render) — totals,
true outcomes, tear rituals, the slip as an object.
**Ground read before dreaming:** issue #10 + its 4 comments, `AUDIT-receipt.md`,
the Receipt cell plan (`ISSUE-10-receipt-cell.md`), Innovator + Feel-2 (tear)
filings in `studio/plan/issue-10/`.

## The laws I dream INSIDE (never against)

These are proven keepers, not constraints to escape:

- Printed character: Anonymous Pro mono ledger (11sp, 1sp tracking), long-form
  durations (`longSpan`), Sora only for his intention title, Atkinson
  Hyperlegible nowhere the machine speaks. Never green. No AI images, no
  image assets at all — type, rules, deterministic paths only.
- Truth: listed-never-summed deflections; peeks are NEVER blocks (the A4
  outcome allowlist stands); a figure the machine cannot back is OMITTED,
  never faked (`-1` never prints); `RAN` comes from `sealedElapsedSeconds`,
  never wall-clock subtraction.
- Object: zero buttons / CTA on the paper, brightest object in the frame,
  stillness after arrival (no re-strike, no animated numbers, no auto-dismiss).
- Parked means parked: **TEAR-to-file (B1)** and **SLIP BOOK (B2)** are
  Sarthak/Ren decisions waiting in #21. Ideas touching them are marked
  **[PARKED — dream only]** and priced for the day the ruling lands; they
  are not proposals to build now.

**My one-sentence thesis:** the other seats made the slip *true and beautiful*.
Make it *kept*. A report is read once; an object is kept, folded, bound,
re-found. Every idea below pushes the slip from "screen at the end" toward
"artefact in his life" — using only print mechanics the app already owns.

---

## A. THE SLIP AS AN OBJECT

### A1. CARBON COPY — the slip prints its own twin
- **Idea:** Two layers, one act. The bright original stands as today; directly
  beneath it, offset 2dp down-right, a faint carbon twin in the same mono —
  visibly the same №, same lines, lower contrast. When he files/dismisses,
  the original lifts away and the carbon stays a beat longer before fading
  into the Drawer. Message with zero words: *you saw the original; the
  machine kept the carbon.* Kills the "dismissed = gone" anxiety without a
  single new surface.
- **Mechanism:** Render `ReceiptSheet` content twice in a `Box` — back copy
  at ~0.35 alpha, offset (2dp, 2dp), non-interactive; exit animation splits
  the layers (original fades up, carbon fades down, 180ms Curve A).
- **Cost:** S. `ReceiptSheet.kt` only (~30 lines), no state, no migration.
  Risk: legibility at night — carbon alpha must follow the night-ink law.

### A2. THE SLIP IS MONEY — denomination corner
- **Idea:** `RAN` stops being a line and becomes a *denomination*. Top-right
  of the head, Anonymous Pro Bold, large: the total set like the corner
  numeral on a banknote, with the № directly under it as the serial number.
  He did not "complete a session" — he minted hours nobody can take. The
  body still prints `RAN {longSpan}` in its ruled total section (truth in
  the ledger); the corner is the same figure, monumental. Same fact, two
  registers: bookkeeping below, value above.
- **Mechanism:** One extra `Text` in the head row bound to the same
  `sealedElapsedSeconds > 0` guard as A1's total; omitted whole when the
  total omits (stand-down slips carry no denomination — a slept window
  mints nothing, and the empty corner *says* that).
- **Cost:** XS. `ReceiptSheet.kt` head only (~10 lines). Fights to watch:
  head crowding on faulted slips (faulted branch prints no corner — the
  machine does not denominate its own failure).

### A3. FOLD LINE AT ONE-THIRD — a receipt designed for a pocket
- **Idea:** A faint printed fold mark (two short ticks at the edges, never a
  full line) at exactly 1/3 of the slip's height. Above the fold: №, his
  intention, ARMED, RAN — the identity third. In the (parked) Slip Book the
  slip renders *folded at its own mark* by default; unfolded on tap. Even
  before any book exists, the ticks teach the eye the slip's anatomy: head
  = who/when/how long, body = what happened, foot = the law.
- **Mechanism:** Two 6dp edge ticks positioned by fractional offset in the
  sheet's `Box`; folded rendering is a height clip + the same ticks in the
  book surface.
- **Cost:** XS for the ticks alone (~8 lines, pure decoration with a job).
  Folded rendering rides with B2's cost if the book is ever ruled in.
  **[SLIP-BOOK-adjacent: ticks buildable now, folding is PARKED]**

### A4. WATERMARK № — the number is in the paper, not on it
- **Idea:** The receipt №, huge and at ~6% ink, rotated -90° along the slip's
  left margin — a watermark woven into the stock, like banknote paper.
  Screenshot a slip and the № is un-croppable; re-read a slip in the Drawer
  and its identity is ambient, not a label. The slip stops *carrying* a
  number and starts *being* one.
- **Mechanism:** One rotated `Text` (Anonymous Pro Bold, ~44sp, alpha 0.06)
  in the sheet `Box`, text = the existing zero-padded `numberPrint`; purely
  presentational, excluded from semantics/accessibility tree.
- **Cost:** XS (~12 lines). Must never compete with body text — alpha and
  margin placement are the whole discipline; phone-proof it at fontScale max.

### A5. GUILLOTINE TICKET STUB — the head is a stub, literally
- **Idea:** A real perforation dialect already exists in the app
  (`PerforationRow`, Tasks pad). Print one perforation row *under the head*
  — head above it (№, intention, ARMED), body below. It does nothing. It is
  not tearable. It simply declares the object's grammar: *stub / body*,
  the way every real ticket he has ever held is built. If B1's tear is ever
  ruled in, this is where its sibling perforation at the foot rhymes.
- **Mechanism:** Reuse `PerforationRow`'s dash spec (3dp on / 3dp off, 1dp,
  ground colour) as a static divider in `ReceiptSheet` between head and body.
- **Cost:** XS (~10 lines + import). Danger, stated plainly: a perforation
  that cannot tear can read as a lie (Feel-2's law). Mitigation: this one
  sits mid-paper where no tear is physically plausible, as a *printed rule
  in dash form* — the ruling should still eyeball it on the phone proof.

---

## B. TOTALS & TRUE OUTCOMES — saying more by printing exactly what's true

### B1. THE SPLIT TOTAL — `RAN` with its anatomy
- **Idea:** The total line grows a lawful sub-line, only when the data is
  sealed truth: `RAN 3 H 20 MIN` over a rule, and under it, smaller mono:
  the anatomy — blocks + breaks as *listed parts*, e.g.
  `2 BLOCKS · 1 BREAK TAKEN 10 MIN`. Not a new sum (never sum what the
  ledger lists) — the span derivation's own parts, printed because
  `spanElapsedSeconds` is literally built from them. A total he can audit
  at a glance is a total he trusts at 1 AM.
- **Mechanism:** `ReceiptState` already carries the span; add the two
  already-stored parts (`breakTakenSeconds`, block count at seal) to the
  receipt state at assembly; sheet prints the sub-line only when every
  part is present, omits whole otherwise.
- **Cost:** S. `ReceiptState.kt` + assembly + sheet (~40 lines), no
  migration (parts already persist on the session row). Guard: break part
  prints only *taken* seconds (Truth Car 3), never the assumed 600.

### B2. QUIET CHAPTERS — the slip proves a negative
- **Idea:** (Sharpens Finisher 3's silence rules into a first-class voice.)
  A gap of ≥20 min between deflections is not empty space — it is the
  session's best evidence. Print it as a chapter rule:
  `—— 47 MIN QUIET ——` in the list, centred, same ink. A slip with zero
  deflections prints ONE line only in the body: `QUIET THROUGHOUT` over
  the total. The absence of temptation, typeset. This is the only
  "achievement" the slip may ever state, because it is a witnessed fact,
  not praise.
- **Mechanism:** At assembly, walk the already-filtered deflection list,
  compute gaps from stamped times; emit gap markers as list items (state
  carries a sealed `List<ReceiptLine>` union, or the sheet derives gaps
  from the deflection timestamps it already receives).
- **Cost:** S (~35 lines, assembly or sheet derivation; sheet derivation
  keeps state untouched — prefer it). Threshold 20 min is a tuning
  constant, named in one place.

### B3. PEEK, PRINTED HONESTLY — the let-in gets its own true line
- **Idea:** Today peeks correctly print *nothing* (A4). Wilder truth: a
  peek is a fact about the session too — it just is not a block. Give it
  ONE lawful line in its own dialect, placed after the deflections,
  never among them: `LET IN ONCE — 2 MIN PEEK, RETURNED` (only when the
  log proves the return; if return is unprovable, omit — omission law).
  The slip that admits its one mercy is more believable than the slip
  that pretends the mercy never happened. This *strengthens* the
  never-print-peeks-as-blocks law by giving the truth somewhere to go.
- **Mechanism:** Assembly already enumerates the outcome vocabulary;
  count `Used 2m Peek` rows separately into a nullable `peekLine` on
  `ReceiptState`; sheet prints it in the foot section, visually fenced
  by rules from the deflection list.
- **Cost:** S (~30 lines, no migration). **Needs Ren's ruling** — it
  changes what the slip *says*, not how it looks. If "RETURNED" cannot
  be proven from today's logs, ship the line without that word or not
  at all; do not invent the return.

### B4. THE WITNESS SENTENCE — locked-grammar testimony (B7, gone wilder)
- **Idea:** Audit B7's SENTENCE, pushed: one prose line, generated by a
  locked grammar from sealed facts only, set in Atkinson Hyperlegible —
  the *one* line on the slip in the human typeface, because it is the
  machine speaking *to* him, not logging. Example shape:
  *"You armed for deep work at 05:12. The shield turned away Instagram
  twice. You ran 3 hours 20 minutes."* Banned forever: praise words
  (great, proud, streak, crushed), grades, comparisons. Grammar slots
  only fill from provable fields; any unprovable slot kills the whole
  sentence (omission law at sentence scale).
- **Mechanism:** Pure function `witnessSentence(ReceiptState): String?`
  with a fixed template + slot guards; deterministic (same state = same
  sentence, restore-identical).
- **Cost:** M. Mostly wording discipline + tests over the slot guards
  (~80 lines + table tests). **[PARKED-adjacent: B7 is a Group B ruling
  in #21 — dream filed, ruling required]**

### B5. CROSS-MIDNIGHT DATE AS A SECOND STAMP
- **Idea:** Audit A11 puts the date on the ARMED line when the span
  crosses midnight. Wilder: a slip that crosses midnight gets *stamped
  twice* — a second, offset head-mark at the ENDED section:
  `№ 048 · CONTINUED — 31 OCT` like a ledger page carried over. The slip
  admits it lives in two days. Sessions that cross midnight are his
  hardest (SLEEP routines, late arms); the paper should honour the
  border crossing instead of hiding it in one small date.
- **Mechanism:** Sheet compares ARMED/ENDED calendar days (both stamps
  already on state); renders the continuation mark only when they differ.
- **Cost:** XS (~15 lines). Same-day slips byte-identical to today.

---

## C. TEAR RITUALS — [PARKED — dream only, B1 / issue #21]

*Everything in this section waits on the TEAR ruling. Feel-2 already
specced the tear's physics (0.55 follow, 9 stick-slip clusters, point of
no return at cluster 5, shipped `playTearPulse()` voice, torn stamp
before motion). I do not re-spec it — I dream what the tear could MEAN.*

### C1. TEAR = THE ONLY SIGNATURE — [PARKED]
- **Idea:** He never taps "done" on a slip. Tearing is his signature —
  the one act that says *I read it, it is true, file it*. Untorn slips
  stay standing (navigation does not file — Feel-2 §2.5.4's
  recommendation, endorsed and amplified). A week of untorn slips
  standing in the Drawer is not clutter; it is unread mail from himself.
- **Mechanism:** B1's torn stamp becomes the *only* dismissal fact;
  A9's dismissed-№ remains for faulted slips (no tear on faults, ever).
- **Cost:** Inside B1's M/M+. The exclusivity ruling is the real price —
  it changes dismissal law, not just a gesture.

### C2. THE STUB HE KEEPS — [PARKED]
- **Idea:** The torn-off tab (16dp, carrying the № microprint) does not
  vanish. It falls — and lands, miniaturised, in the Deck's corner as
  today's stub, stacking visibly through the day (max: today's stubs).
  At midnight the stubs clear. His day, measured in torn stubs on the
  desk. Tactile, countable, zero numbers, zero scores.
- **Mechanism:** On tear completion, spawn a stub chip (№ text, torn-edge
  shape from the same № seed) in a Deck overlay list persisted per-day
  (prefs, date-keyed № list — the receipt prefs pattern).
- **Cost:** M on top of B1. One prefs list + Deck rendering (~90 lines).
  Kill-switch: stubs are decoration with a memory, never a counter —
  no totals, no "3/5 today" text anywhere near them.

### C3. TEAR ECONOMY — a clean tear buys tomorrow's first arm — [PARKED]
- **Idea:** The wildest one in this file, filed honestly as a dream:
  tearing a slip *files it and banks the ritual* — tomorrow's first arm
  skips its dwell (the 2s receipt dwell's mirror) once. Filing today
  makes starting tomorrow frictionless by exactly one gesture. The
  receipt stops being the end of the loop and becomes the start of the
  next one.
- **Mechanism:** Torn stamp sets a one-shot prefs flag consumed at the
  next day's first arm (dwell waived once, flag cleared on use or at
  day rollover).
- **Cost:** S in code (~25 lines). **High ruling risk, stated plainly:**
  it couples a reward to filing, which can train tearing-without-reading.
  If Ren/Sarthak smell gamification, kill it — the tear's meaning
  (C1) is worth more than its economy.

### C4. DOUBLE TEAR FOR FAULTS THAT HEAL — [PARKED, rides B4]
- **Idea:** A faulted slip (overprinted solid perforation, untearable)
  that later proves `RELEASE CONFIRMED LATER` gets its perforation
  *re-punched* — dashes return — and only then can it be torn. The
  machine's fault, forgiven by evidence, becomes fileable by hand.
  Two-stage paper: solid → dashed → torn, each stage a fact.
- **Mechanism:** B4's corrected-duplicate stamp flips the perforation
  state on the standing slip; tear availability is a function of
  (faulted && corrected).
- **Cost:** S on top of B1+B4. Pure state-gating + the overprint toggle
  Feel-2 already specced.

---

## D. SLIP BOOK & THE LONG ARC — [PARKED — dream only, B2 / issue #21]

### D1. THE BOUND ROLL — a month is one continuous paper — [PARKED]
- **Idea:** The Slip Book does not list slips. It *unrolls* them: one
  continuous thermal roll, № after №, separated by stitching marks
  (the perforation dialect, horizontal). Scrolling the book is unrolling
  the month. At month end the roll prints its own binding line:
  `— OCTOBER · 61 SLIPS · RAN … —` where `RAN …` sums only sealed
  `sealedElapsedSeconds` (a sum of totals is bookkeeping, not a grade —
  and it lives in the book, never pushed at him as a score).
- **Mechanism:** Book surface renders stored receipt states sequentially
  in one scroll column; month binding is a derived header from the same
  states. №-addressability (RC-04) already makes every slip findable.
- **Cost:** L — this is B2's real size. Storage already persists № +
  pointer + outcome per audit item 13; full slip re-render needs the
  receipt state snapshot persisted per № (small blob per slip, prefs/
  Room — the ruling prices it).

### D2. SLIP № AS AN ADDRESS HE CAN SPEAK — [PARKED-adjacent]
- **Idea:** Anywhere in the app that mentions a past session (Drawer
  rows, routine judgements), the slip № prints as its citation —
  `SEE SLIP № 048` in mono — and tapping it (in the Drawer, never on
  the paper itself) opens that slip, settled, no arrival re-performance.
  The № stops being decoration and becomes the app's citation system:
  every claim the machine makes about the past carries its slip №.
- **Mechanism:** Drawer/judgement rows already know their session row;
  the row carries `receiptNumber` (RC-04). Render + navigate to the
  book's № anchor.
- **Cost:** S–M, but only meaningful with B2's book to land in.
  Citation text alone (no navigation) is XS and buildable now.

### D3. THE YEAR SLIP — one receipt for the year, printed once — [PARKED]
- **Idea:** On Dec 31 (or his NEET exam eve — his call, if ever), the
  machine prints ONE slip: the year's roll totalled, first № and last №,
  quiet chapters counted as chapters, in the identical mono character,
  longer than any screen — it feeds and feeds. Not a stats page. A
  receipt for a year of his life, in the same paper as a 20-minute arm.
  The format's dignity is that it does not scale its voice with the
  amount.
- **Mechanism:** Same sheet, fed an aggregate state assembled from the
  year's sealed rows; every figure held to the same provability law
  (unprovable sections omit).
- **Cost:** M once B2/D1 storage exists; impossible before it. Filed as
  the book's horizon, not a near-term build.

---

## E. SOUND, TOUCH & PAPER FEEL (inside the decided character)

### E1. THE PRINT HEAD'S LAST TICK — feed ends with a mechanical settle
- **Idea:** A7's feed (not fade) ends with ONE tiny haptic tick at the
  frame the paper lands — the print head parking. Not the completion
  pulse (that sounded at detention, for the session); this tick is for
  the *paper*. Two events, two voices, never confused — the same
  discipline Feel-2 specced for tear vs finish.
- **Mechanism:** Arrival animation completion callback fires a single
  5ms low-amplitude tick via the existing tactile engine (sibling of
  `playTearPulse`'s tick vocabulary); restored slips (settled render)
  get no tick — paper that was already there makes no sound.
- **Cost:** XS (~15 lines). Reduced-motion / sound-off: tick dies with
  its channel, nothing compensates.

### E2. INK DENSITY = AGE OF THE FACT — [careful, bounded]
- **Idea:** On a *restored* slip (hours old), the paper is identical —
  but in the Slip Book, slips older than 30 days render at 92% ink:
  thermal paper fades, and the book honours it. Never on the standing
  slip, never below legibility, never as a "staleness" judgement — just
  material honesty: old paper looks old. Re-printing (opening) a slip
  does NOT refresh its ink; what fades, fades.
- **Mechanism:** Book rendering scales ink alpha by a step function of
  slip age (1.0 ≤30d, 0.92 beyond) — one constant, no gradient math.
- **Cost:** XS in the book's renderer. **[PARKED with B2]**; flagged
  risky: any whiff of "your old effort decayed" kills it — it is a
  paper effect, not a message, and the copy must never mention it.

### E3. THE SLIP CASTS THE ONLY SHADOW
- **Idea:** Feel-5/§1 gave the slip a shadow lift. Wilder discipline:
  during the slip's standing, it is the ONLY surface in the app allowed
  a shadow. The Deck behind it (scrim 0.12) goes completely flat — every
  other elevation in the visible frame is suppressed while the receipt
  stands. One object casts a shadow in this world, and it is the paper.
- **Mechanism:** A standing-receipt flag in the host gates `shadow`
  modifiers on the Deck composition beneath (or simply: the Deck's
  surfaces are already flat by design and this law *keeps* them so —
  cost may be zero if audited flat).
- **Cost:** XS–S depending on what the Deck currently lifts. Mostly a
  law to write down and a grep to prove it.

---

## F. THE ANTI-RECEIPT — faults with dignity

### F1. UPSIDE-DOWN FAULT LINE
- **Idea:** On a faulted slip, the fault line alone prints rotated 180°.
  He must physically turn the phone to read the machine's confession.
  No colour, no icon, no bold — orientation is the entire alarm, and it
  is unmistakable from across the room while remaining pure type.
  The machine does not shout about its failure; it prints it the way
  a misfeed prints — wrong — and he corrects it with his hands.
- **Mechanism:** `Modifier.rotate(180f)` on the fault line composable
  in `ReceiptSheet`; text content unchanged (B4's plain wording +
  retry time ride inside it if ruled in).
- **Cost:** XS (~4 lines). Accessibility: semantics keep the line in
  reading order for TalkBack (rotation is visual only) — spec that
  explicitly or do not ship it.

### F2. THE SLIP THAT PRINTS ITSELF SHORT
- **Idea:** A faulted slip is physically *shorter*: it omits its foot
  section entirely (no footer law line — the machine does not recite
  its law on a slip where it broke it) and ends at the fault line with
  the torn/static edge closer than usual. Length becomes information:
  a full-length slip = a full account; a short slip = the machine
  stopped talking where it failed. (Footer omission must be ruled —
  item 13 acquitted the footer's function; this idea argues the
  exception, it does not assume it.)
- **Mechanism:** Faulted branch already exists in the sheet (header
  collapse, A9); extend it to skip the footer block.
- **Cost:** XS (~6 lines) + Ren's ruling on the footer exception.

---

## Ranking — if Ren buys only three

1. **B2 QUIET CHAPTERS** — cheapest truth-multiplier; makes zero-
   deflection slips (his best sessions) print their best line.
2. **A1 CARBON COPY** — turns dismissal from loss into filing, no new
   surface, pure object-feel.
3. **B3 PEEK, PRINTED HONESTLY** — completes the truth programme A4
   started; needs a ruling, worth the ask.

Wildest, kept on purpose: **C3 TEAR ECONOMY** [PARKED] and
**D3 THE YEAR SLIP** [PARKED] — both filed so the ruling in #21 is made
against real shapes, not slogans.

## What I deliberately did NOT propose

- No scores, streaks, badges, comparisons, or praise anywhere — the
  slip testifies; it never grades (blocker-not-tracker stands).
- No share/export/social anything — a kept object is private by nature.
- No colour, no green, no images, no AI art. Type and rules only.
- No redesign of the loved character — every idea above renders in the
  slip Sarthak already ❤️'d or it does not render at all.

— Wirbel (crazy-ideas, issue #10)
