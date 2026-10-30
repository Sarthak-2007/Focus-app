# ISSUE #6 — FEEL-5 · RHYTHM / THE LIVED MINUTE
## Seat: FEEL-5, Rost's cell · Fri Oct 30, 2026
## Status: DECLARED, paper only. Nothing compiled. Nothing committed.

Territory: the tempo of using the Deck half-asleep — how long
each thing takes the hand and the eye, in what order, and what
it costs when the man is at his slowest. Source of record:
`studio/plan/AUDIT-deck.md` §6 (Muede's 1 AM walk — the standing
test record). Re-walked against the code at this commit:
branch `agent/issue-6` @ 8438f5a,
`TactileDeckScreen.kt` as it stands in this tree — 1,148 lines
(the audit walked 1,069; the file grew, the walk did not get
faster). F1 has not landed in this tree yet: `studyPreset`
still sits at line 100, the FLOW cap still writes it, the grace
chip still swaps. Everything below is the BEFORE state unless a
fix is named.

Counting rules for this file, used consistently:
- **Gesture** — a deliberate hand act (drag, tap, slide, scroll).
- **Decision** — a choice the man must make or verify before the
  next gesture can be chosen; remembering counts, algebra counts.
- **Read** — one eye-stop: the eye lands, parses, leaves.
  Three cells parsed in one stop are one read and three facts.
- **Dead time** — input refused, or the verb absent, while the
  man is trying to act.

---

## §1 · THE RE-WALK — THREE WALKS AT THIS COMMIT

### Walk A — Arming block 1 at 05:00

Scenario, Muede's: awake one minute, eyes at half aperture,
yesterday's dial state unknown, intent = one 50-minute block,
seal it. The sequence the code actually imposes:

| # | Act | Kind | What the code does |
|---|---|---|---|
| 1 | Land on the Deck; find the instrument | read R1 | Idle content is ~1.23 scroll-screens (audit §4); the card may need a scroll before the wheel is whole — **scroll, 0–1 gestures**, luck-priced |
| 2 | Read the eyebrow: `TOTAL` | read R1 (wasted) | 10sp, `AnonymousPro`, says nothing he can act on. Pure tax |
| 3 | Read the hero span, 72sp | read R2 | The true span, breaks priced in — but unexplained (audit §5.6) |
| 4 | Decide block length; drag the wheel | decision D1 + gesture | Wheel stops are 5-minute steps, 10–180 (`BlockitCoreViews.kt`: 9° per 5 min). The drag is honest work |
| 5 | Decide block count; tap the wheel hub | decision D2 + **hidden control** + 0–7 gestures | Hub press-and-release cycles 1→…→8→1 (`onCycleBlocksCount` in the Deck's wheel `update`). One-way only: from yesterday's 3 to today's 1 costs **6 taps**; overshoot costs up to 7 |
| 6 | Verify the count you just set | read R2 again + algebra | The count is printed **nowhere**. He must reverse the hero: span = n × dial + (n−1) × 10. At 05:00 that is a maths problem used as a confirmation dialog |
| 7 | Decide FLOW vs timed | decision D3 + 0–1 gestures | A second door to the same fact: the FLOW key-cap — and a destructive one (T1, §3) |
| 8 | Read the task line; decide "into what?" | read R3 + decision D4 | `NOW` + title, a secret tap on the title (sub-steps), a secret tap on `QUEUE n` (a door). Two unlabelled controls sit in this line |
| 9 | Read the proof strip; decide "will it hold?" | read R4 + decision D5 | BLOCK / APPS / TIME in one stop, three facts, plus the header Guard line above it (host, +1 read the eye cannot opt out of) |
| 10 | Slide | **the 1 gesture that matters** | Commit crossing at displayed ≈0.92 travel (`SlideGestureMath`) |
| 11 | Wait out the detention | **dead time** | Shot runs 0–1050ms: scroll disabled (`verticalScroll(enabled = !shotActive)`), task handlers removed by state, wheel locked (`isLockedSession = shotActive`), slide seated as ghost. **1.05s, code-exact** — and it is not the whole wait (T2, §3) |

**Tally, Walk A, BEFORE — the audit's numbers, confirmed and
sharpened:** 1 gesture that matters, buried under **5 decisions**
(D1 length, D2 count, D3 mode, D4 task, D5 proof), **4 reads**
(+1 host read he never asked for), **1 hidden control** (the hub;
plus 2 secret taps in the task line that are hazards, not help),
**4–11 total hand acts** depending on yesterday's residue, and
**1.05s dead** — minimum. Muede's verdict stands verbatim at
this commit: **operable at 1 AM, unverifiable at 1 AM — FAIL as
composed.** The unverifiable step is #6: the one setting he
cannot read back is the one he set by tapping a blank hub.

### Walk B — The grace window (the panic)

He sealed by accident, or sealed wrong, and wants out free.
The window is 10 seconds (`withinGraceWindow =
elapsedSeconds <= 10`).

- **t 0 → 1.05s: the verb does not exist.** During the shot the
  pinned zone still seats the slide ghost — the chip branch is
  gated on `!shotActive`. The first 10.5% of his free window is
  spent watching a ceremony.
- **t ≈1.05s: an unannounced chip appears** at the slide's
  address: `GRACE WINDOW ABORT`, badge already reading ~`9s`
  (the badge is whole-second, derived from the Int
  `elapsedSeconds` — up to a further second of display lag is
  inside that rounding). It is 58dp tall floating in the 72dp
  zone.
- **Find + read + decide + tap** must all fit in what remains:
  **≈8 usable seconds** — the audit's figure, and the mechanism
  is now pinned: 10 − 1.05 (shot) − ~0–1 (badge granularity and
  the composition that seats the chip) ≈ 8–8.9, before any human
  slowness is charged. Panic spends the first of those seconds
  just learning the chip exists.
- **t = 10s: the pixels become a different verb.** Same address,
  same size, new chip: `EMERGENCY BREAK VALVE (60S HOLD)`,
  badge `$tokens/1 WEEK`. The label describes the overlay's
  hold, not this chip's tap — and a tap that was aimed at free
  becomes a tap that spends his one weekly break, with no latch
  of the intent he formed at DOWN (T4, §3).

**Tally, Walk B, BEFORE:** 1 gesture available, ~8s to spend it
in, 2 reads before it can be spent (find the chip, parse the
badge), 1 silent verb-swap at the deadline. The window is not
10 seconds. It never was.

### Walk C — The break ("how long is my break?")

A block ends; the repository flips the session into a Safe
Break automatically (`safeBreakSecondsRemaining =
SAFE_BREAK_DURATION_SECONDS` = 600 — the break is always
10 minutes, a fact printed nowhere as a standing answer).

- The largest print on the card, 72sp, reads **`00:00` for the
  break's whole length.** The Deck's `displaySeconds` takes
  per-block `remainingSeconds`; at break entry the repository
  sets `elapsedSeconds = effectiveTarget`, so remaining is 0 and
  stays 0. The entity already carries the honest derivation —
  `spanRemainingSeconds`, which the strip's ENDS cell uses —
  the hero does not. Largest print answers "this block is
  over," in code, for ten minutes.
- The one asked question is the smallest print on the card:
  the eyebrow, 10sp, `BREAK · n MIN`, where n is
  **ceiling-rounded whole minutes** —
  `(safeBreakSecondsRemaining + 59) / 60`. At 9:01 left it
  prints `10 MIN`; for the final 59 seconds it prints `1 MIN`.
  He cannot tell a break that just began from one about to end
  without watching the minute flip.
- The break's verb, `END BREAK`, lives in the utilities room
  inside the **scroll column** (Section 3) — not in the pinned
  zone. If the column rests scrolled, reaching the verb costs a
  scroll, paid out of the break itself.
- The transition into the break announces itself at no pinned
  address: pinned zone identical, hero swaps to `00:00`,
  eyebrow swaps its line. A man resting his eyes through the
  block boundary can miss that the state changed at all.

**Tally, Walk C, BEFORE:** the break's question costs 2 reads
(hero — wrong answer; eyebrow — right answer, coarse) at a
7.2× size disadvantage, with seconds-level truth available in
the model and rendered nowhere. Muede's sentence stands and
sharpens: it is not merely the smallest print; the largest
print is actively answering a different question, wrongly
enough to read as FINISH.

---

## §2 · WHAT EACH LANDED FIX DOES TO THE WALK

Gesture / decision / read deltas, per fix. "Lands" = per Rost's
triage: the LAND NOW set on the current room, then F16+F18
together, F6 on F5's hero work.

### F4 — Recipe print (the eyebrow's interim cure) · S

Idle eyebrow: `TOTAL` dies; the line prints `3 × 50 + BREAKS`.

- **Decisions 5 → 4 in Walk A.** D2's verification half dies:
  the count and the per-block length are stated, not derived.
  Step 6's algebra read is deleted outright.
- **Reads: count unchanged (4), wasted reads 1 → 0.** R1 stops
  being tax and becomes the confirmation for the hub's hidden
  gesture — the hidden control gains a readback at the same
  card, though not yet at the hand.
- Tempo caveat, mine to file: the recipe still prints at 10sp.
  F4 buys cognition, not optics. At 05:00 the line that now
  carries the confirmation is still the card's smallest print.
  The full cure is the Fusion's address change (below), not a
  size bump here — a bigger eyebrow is a second hero competing.

### F2 — FLOW cap killed as a control · S (rides with F4's print)

- **Decisions −1 as a separate door** (D3 folds into D1: mode
  is where the wheel stops, printed, never tapped).
- **Gestures −0–1**, and the destructive stray-tap cost of T1
  (§3) is deleted with the cap. In Walk A tempo terms this fix
  is mostly insurance: it removes the one tap that could cost
  him a full re-dial at his slowest minute.

### F5 — Break hero · S

During a Safe Break the hero IS the break countdown;
eyebrow (interim) / foot line (Fusion form) prints
`BREAK — BACK AT HH:MM`.

- **Walk C reads 2 → 1.** The first and largest read now answers
  the question; the eyebrow/foot line becomes confirmation
  (clock time), not the sole source.
- **Print size for the answer: 10sp → 72sp (7.2×)**, and
  **granularity: ceiling minutes → seconds.** The "did it just
  start or is it ending" ambiguity — a full 10-minute-wide
  uncertainty at minute resolution — dies.
- **The false FINISH dies.** `00:00` held for ten minutes was
  a misread waiting for a tired man; the break countdown
  cannot be mistaken for an ending because it is visibly
  running down from 10:00.
- **Gestures: unchanged, and one residual stands** — F5 moves
  no verb. `END BREAK` still lives in the scroll column. If
  Rost wants the break walk fully cured, seating that chip in
  or beside the pinned zone is a separate, unpriced ask; I flag
  it, I do not fold it into F5's S.

### F6 — The STOP chair · M (on F5's hero work)

One chair at the slide's address, one verb whole session:
STOP — free and instant inside 10s; after, the same tap opens
the valve challenge and the chair's own words carry the price.
Grace countdown moves into the hero. Chips fill the full 72dp.

- **Walk B reads 2 → 1, and the 1 is pre-learned.** There is no
  chip to *find*: the verb is the same at second 3 and second
  30, at the address his hand already knows from arming. Search
  time ≈ 0 — not because the window grew, but because none of
  it is spent searching.
- **Usable panic seconds: ~8, but all 8 are now decision
  time.** The honest statement for the record: F6 does not add
  a single second to the free window (the shot still eats
  1.05s of it — the chair cannot compose during `shotActive`
  either, unless the builder seats it through the shot; see
  T2). What F6 returns is the find-and-parse spend, ~1–2s of
  the 8, and the whole of the certainty.
- **The verb-swap dies; the deadline stops being an ambush.**
  A tap after 10s is no longer a different gesture wearing the
  same pixels — it is the same verb at a printed price. The
  price must be legible *before* the tap at every second,
  which the chair's own words now guarantee.
- **Target size 58dp → 72dp (+24% height)** at the exact
  address, in the exact seconds, where hand precision is at
  its worst. This is the fix's quiet half and it is real.
- Acceptance note (T4): the price wording flipping at the 10s
  boundary, mid-reach, is the one tempo trap the new chair can
  still set. Latch the regime at DOWN, as the slide already
  latches its press — filed in §3 as F6's test, not a redesign.

### F18 — The Fusion (with F16, the pinned collapse) · M + L

Hero into the wheel hub; the dial seals in place as its own
clock; rim ink carries progress. F16 returns ~120dp of pinned
screen; the idle Deck fits one screen.

- **Walk A reads 4 → 3 (Fusion), → 2 in healthy health (F16).**
  Hero read and wheel read merge into one hub read *at the
  hand*: span, recipe remainder (`+10 BREAK`, the `3 × 50`
  pattern), and the dial are one eye-stop. In health the strip
  read (R4) retires with the drawer shut — BLOCK is silence —
  leaving hub + ghost title/imprint line.
- **The hidden control is declared.** The hub gesture survives
  (it is a good gesture); what dies is its secrecy — its result
  prints in the hub's own sub-line at the same eye-stop. Step 6
  of Walk A, the algebra readback, has no successor. D2 remains
  a decision (how many blocks?) but is now a decision with a
  printed state, which is what a decision at 05:00 can survive.
- **Gestures: scroll −1 in the common case.** F16's returned
  120dp is what lets the idle instrument fit whole; Walk A's
  luck-priced step 1 scroll dies. Hand acts for the standard
  arm fall to: wheel drag 1 (+ hub taps only when changing
  count, now self-printing) + slide 1.
- **Dead time: unchanged, and I say so plainly.** The Fusion
  moves print; F15 moves the vertigo to the ground so the dead
  1.05s no longer *blurs the instrument being sealed* — the
  second feels like a room settling instead of a machine
  seizing. But the detention in code is an input law
  (`enabled = !shotActive`, handlers removed by state), not a
  visual. Unless a lane re-opens that law, **the after tally
  keeps its 1.05s**. Anyone who reports the Fusion as curing
  the dead second is selling feel as time. It cures the feel.
  The time is T2's filing.

### Secondary rows (one line each, tempo only)

- **F7 ghost task line:** Walk A read R3 shrinks to a title;
  the 2 secret taps (ambush gestures) die; D4 leaves the Deck
  — the task is chosen in Tasks, the Deck only confirms it.
- **F3 true progress:** no gesture/read change; removes a
  mid-session re-read ("is that % lying?") that costs a
  settled man a second look every time the old floor showed.
- **F16 sentence-in-fault above the slide:** in fault the one
  read that matters moves to first position — fault reads
  1, taken before any gesture, which is the correct order.

### The after tally — Walk A, all of the above landed

| | Before | After |
|---|---|---|
| Gestures that matter | 1, buried | 1, the second hand act of two–three |
| Total hand acts (standard arm) | 4–11 | 2 (+ hub taps when changing count) |
| Decisions on the Deck | 5 | 3 (length, count, seal) — task and proof have left the room or been printed into state |
| Reads (Deck-owned) | 4 (+1 host) | 2 (hub, ghost/imprint line); +1 in fault, by design |
| Wasted reads | 1 (`TOTAL`) | 0 |
| Hidden controls on the Deck | 1 hub + 2 secret taps | 0 |
| Dead time after the slide | 1.05s + proving silence | 1.05s + proving silence — **uncured** (T2) |

Grace: 1 tap, 0 search, price printed before the tap at every
second. Break: 1 read, at 72sp, in seconds.

---

## §3 · TEMPO DEFECTS NOBODY HAS FILED YET

**T1 · The FLOW cap is a destructive gesture, not just
theatre.** The audit convicts it as a dead wire. The tempo
charge is separate: its `onClick` fires
`onSelectStudyPreset(STOPWATCH_FLOW)` **and**
`onDialMinutesChanged(5)` — one stray tap at 05:00 overwrites
a dialled 50 with 5, and there is no undo; recovery is a full
re-dial plus re-verification of a value he can no longer see
because he replaced it. F2's kill cures this by deletion; if
F2 ever slips a pass, this defect ships inside it. Price of
the cure: already paid (F2, S).

**T2 · The dead second is longer than the dead second:
proving silence precedes the shot.** The audit counts 1050ms.
The code shows a prior, unbounded interval: while
`armPhase` is `Proving`, the Deck renders its idle form, the
readout does not start, and the refusal slot is deliberately
`null` ("the machine is working" — the slot's own comment).
Nothing on the face says the slide landed. Proving's length
is set by the cascade outside this file and is printed
nowhere, so total post-slide unresponsiveness = proving
(unknown) + 1050ms (shot). A half-asleep man whose slide
appears to have done nothing will slide again. **Ask:** a
tempo print for proving at the slide's own address (the latch
word changes; no new furniture), and a measured proving
duration on device. Phase-2 class — an arm-path question,
Ren's call, same shelf as Muede's 05:00 door. Until then, no
tally in this cell should quote the detention as "1 second."

**T3 · The Deck runs two clocks and the hero reads the
wrong one.** Beyond Walk C's break case: in any multi-block
session the hero counts the *per-block* remaining and resets
at each boundary, while the strip's ENDS reads
`spanRemainingSeconds`. "How long left?" — the session
question — is answered by the small print; the hero answers
"how long in this block?" without ever saying so. The honest
derivation already exists on the entity and is already
trusted by the strip. Filed as a derivation defect, not a
print defect: F5 cures the break instance; the multi-block
running instance survives F5 and should be named in F18's
hub spec — the hub, sealing in place as "its own clock,"
must be specified as the *span* clock or the Fusion inherits
this defect at a better address.

**T4 · No intent latch at the grace boundary (F6
acceptance test).** Today the chip a tap lands on is whichever
chip is composed when the click resolves; a press begun at
9.8s under GRACE can resolve at 10.2s under VALVE. The slide
already knows the cure — Emil's press latch freezes the
press's facts at DOWN. F6's chair removes the target swap,
but its price wording still flips at the boundary, potentially
mid-reach. **Test for the builder's close:** the regime in
force at DOWN is the regime the tap is priced at, for any tap
that begins inside the window. Paper-checkable in the chair's
handler wiring; no device needed to file it, one needed to
prove it.

**T5 · The finish has a dead second too, and nobody
counted the way back.** At the receipt, the sled's return
waits out the same 1050ms from the receipt's publication
stamp before `animateReturnHome` runs — a second detention,
at the tired end of a session, between "done" and "can arm
again," that no walk in the audit prices. It is the seal
shot's mirror and probably its law (the same 1050,
deliberately), so I file it as a counting correction, not a
charge: a full session's tempo budget is **two** dead
seconds, one at each end, plus T2's proving silence at the
front. Future walks should carry all three.

---

## Verdict, FEEL-5

Muede's FAIL stands at this commit, with his numbers intact
and two of them sharpened (the break hero is `00:00`, not
merely small; the detention is proving + 1.05s, not 1s). The
fix list, landed whole, takes Walk A from 5 decisions / 4
reads / 1 hidden control to 3 / 2 / 0 and gives the panic and
the break back their seconds and their print size. What it
does not touch — proving's silence, the two dead seconds —
is filed above, named, and left where Ren can rule on it.
The Deck after the fixes is a machine a man can operate at
1 AM and *verify* at 1 AM. That was the test. It can pass it.

— FEEL-5 (RHYTHM / THE LIVED MINUTE), Rost's cell, issue #6.
Report to Rost. DECLARED only; nobody compiles.
