# ORSON CHECKS — the four decider checks, run

**Run:** Oct 29, 2026 (REGISTRY-MERGE, on Ren's commission) ·
**Frame:** plan/ORSON-DECIDER-BRIEF.md — the four checks it named,
run against the record on disk. **This report carries evidence
only. The decision on `invention/wild-parts/orson-two-note-road.md`
(registry: RECEIVED, UNVERIFIED) is Ren's; no verdict is stated
here in the checker's voice.**

---

## Check 1 — The F13 record

*Question: is Orson's fork F13 the two-note road, and does its
answer dispose the file?*

Evidence:

- Sweep-C §4 (plan/SWEEP-C.md:70) lists "Orson (F13)" among the
  morning forks that "WERE answered" — but states neither what
  F13 asked nor what answered it.
- The two on-disk uses of "F13" in the morning record are both
  about a different subject — the seal voice's call-site spend:
  Zug's plan audit (COMMONS.md:4830) charges "the seal voice left
  spent in ~13 places (F13)"; the Slice-E Siegel part's account
  (COMMONS.md:5119) enumerates "F13 boundary … 17 call sites —
  2 die … 14 survive for the separate sweep", answered by Slice
  E's bit-identical-seal governance (B-5). Neither text mentions
  the Road, a gate note, or any pitch.
- plan/RULINGS.md contains no F13 entry at all (searched: `F13`,
  `F-13` — zero hits).
- The Road's own answer-trail on the wall is separate: Fen's
  refusal (COMMONS.md:3023, and his letter
  mail/orson/read/0719__from-fen__two-note-road.md — refusal on
  room/address grounds, arithmetic conceded) and Wren's refusal
  in the same thread ("Merge rules; if it takes your note, it
  takes it over this refusal, on the record").

Reading both ways: for Sweep-B — F13's answer, whatever fork it
settles, is not creditable to the Road on anything the record
shows; B's "no ruling on the file itself verifiable" survives
this check. For Sweep-C — §4's list is real (most forks were
answered), but its "(F13)" beside Orson's name does not carry
the Road with it.

## Check 2 — The build-ready ratifications

*Question: does SLICE-D-E-BUILD-READY (or the cascade/seal build
records) ratify any of the Road's fingerprints — 115.2,
360 Hz at 0.68, 720 Hz at the catch, gate note 110 ms @ −12 dB,
grain shadows 454.7 / 617.1 Hz?*

Evidence:

- Constant search across plan/SLICE-D-E-BUILD-READY.md,
  plan/SLICE-E-BUILD-READY.md, plan/slice-de-parts/,
  plan/slice-e-parts/ — and, widened, all of plan/ + methods/:
  `115.2`, `454.7`, `617.1`, `gate note` — **zero hits** outside
  the Orson filings, the two sweeps, the merge list and the
  decider brief itself.
- What the build-ready DID ratify at the gate is a different
  mechanism: D2 (Luz) — a Schmitt latch whose "pitched feel" is
  `fireFeel(1L)` → `playRotaryDetent` at **620 Hz up / 340 Hz
  retreat**, a fixed detent pair replacing the old click
  (plan/SLICE-D-E-BUILD-READY.md:86–88; the latch's feel also at
  :30). No fraction function, no octave, no grain shadows.
- Orson's own Slice-D part records the exclusion in his voice:
  "Fen (refusal of the road on room/address grounds — **the road
  is not in this slice; nothing here pitches the damped zone**)"
  (plan/slice-de-parts/orson.md:452–454), and Wren's "two-note-
  road narrowing — approach audio is pre-t=0 and outside this
  joint … not ruled here" (:448–451).

Reading both ways: for Sweep-B — no ratification names the Road
or any of its constants; nothing in the build-ready disposes the
file. For Sweep-C — the gate's sound slot WAS disposed by the
build-ready, in favour of Luz's detent: the Road was overtaken
in its slot by a build decision that never names it. "Disposed
by the slice" is true of the slot, not of the file.

## Check 3 — The shipped engine

*Question: does TactileSoundEngine as shipped in batch 7
implement a pitch road over the damped zone?*

Evidence (trunk, `Focus-app/app/src/main/java/com/example/
service/TactileSoundEngine.kt` — the batch-7 state):

- `renderSealBuffer`: `val f0 = 360f` — fixed (:187); its
  comment records the "shipped body unmodified (360 Hz,
  partials 360/1080/1080/…)" (:171).
- `playHarmonicSeal(arming)`: `fundamentalHz = if (arming) 360f
  else 480f` (:681) — a fixed pair, not a function of drag
  fraction.
- `playRotaryDetent` (the gate's voice) takes a fixed frequency
  per call (default 680; the build-ready priced the gate calls
  at 620/340). `playSeatKnock`'s pitch is a per-call parameter
  for the block ladder, unrelated to slide fraction.
- `115` / `115.2` appears nowhere in the engine or the UI tree
  (searched; the only hits are comment references to an
  unrelated ruling letter, mail/marlowe/1115). No pitch is
  computed from slide fraction anywhere in the shipped code.
  The seal is a fixed 1,000 ms render fired from exactly one
  call site (SessionSealTransition; Sweep-B's own deleted-letter
  tally noted "Throw built, sole playSealThrow site, batch 7").

Reading both ways: for Sweep-B — the build did NOT take the
design; Sweep-C's "superseded by the shipped batches 4/6/7"
fails as a mechanism claim, since no batch contains the Road or
any road. For Sweep-C — the shipped arming-sound set is complete
without the Road (fixed detent + fixed seal, shipped and
proven): in fact, if not in record, the design was overtaken by
events.

## Check 4 — The sibling filing / a ruling on the pair

*Question: does any ruling name THE PLAYABLE TRACK
(invention/wild-parts/orson.md) or the pair together?*

Evidence:

- Marlowe's merge **does rule on both filings together** —
  invention/WILD-INVENTIONS.md, Fight 7 ("The sound fights —
  Orson ×2, Ivo's Assay, and Fen's address refusal"): "whether
  the arm may make sound in his house is Sarthak's life call,
  not craft — **both Orson filings AND Ivo's Assay trial are
  gated on his answer (Q6)**. If sound is allowed: **the
  Two-Note Road ranks above the Octave Gate** (cheaper; the
  octave is derived, not decorated…) The Octave Gate is
  preserved as standing alternative." Pitch Sag is killed
  conditionally in the same fight (Ivo wins).
- The merge document itself stands RULED in the record:
  BUILD-CHANGES.md [07:20 IST] enters the Wild return
  (Marlowe, 20/20, WILD-INVENTIONS.md) among the morning's ruled
  returns, "WAVE CLOSES 6/6", and lists the arming-sound
  question among "7 questions **HELD at Ren's gate** (… Orson+
  Ivo 03:30 arming sound …)".
- Marlowe's recovery note on the file itself ("Both filings are
  live candidates from one seat; the merge rules on both",
  invention/wild-parts/orson-two-note-road.md:10–11) is thus
  satisfied in form: the merge ruled on both — and its ruling
  is a **conditional hold**, not an adoption and not a kill.
- No answer to the arming-sound question appears in
  plan/RULINGS.md or BUILD-CHANGES.md after the 07:20 holding
  entry (searched: "arming sound", Q6, "two-note", "gate
  note"). Sarthak's verdicts at 07:22 settled the *separate*
  death-sound question (Wild Q5) as NO, recorded expressly as
  Ren's inference (BUILD-CHANGES.md:133) — the record does not
  extend it to Q6.

Reading both ways: for Sweep-B — "still held at Ren's gate" is
precisely what the merge record shows; B's restraint matches
the record's own words. For Sweep-C — its instinct that the
seat's later filings disposed the matter has a real object (the
merge ruled the Wild wave, this pair included), but the merge's
disposition of the pair is a gated, ranked hold — not the
supersession C claimed, and the slice-de-parts / batches leg of
C's claim finds no per-file support (checks 2–3).

---

## The evidence, laid out both ways (no verdict)

**The strongest form of Sweep-B's case:** the only ruling that
names the Road (WILD-INVENTIONS Fight 7) holds it — ranked
first among the two filings, expressly gated on a question that
the record never shows answered. No build-ready, batch entry,
or shipped line of code adopts, implements, or kills it. On the
registry's per-file evidence standard, nothing has disposed
this file.

**The strongest form of Sweep-C's case:** the file's world
moved on without it. Its seat's wave was merged and ruled; its
gate slot was rebuilt on Fen's and Luz's terms (a refusal of the
Road stands unreversed from both its referees); the shipped
engine's arming sounds are complete without it; and its own
author's Slice-D part writes it out of the build ("not in this
slice"). A design can be dead in fact while no record buries it.

**What neither case supplies:** a citable disposition of
`orson-two-note-road.md` itself — an adoption, a kill, or an
answer to the Q6 gate it hangs on. That is the gap the decision,
whichever way it goes, will close.

— REGISTRY-MERGE, Oct 29, 2026. Evidence only; the decision is
Ren's.
