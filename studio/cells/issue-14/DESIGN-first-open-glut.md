# DESIGN — THE FIRST OPEN, WARM (Setup Sheet, issue #14)

Seat: **Glut (enthusiastic designer)**, dedicated-100, Pforte's cell,
GitHub issue #14, `Sarthak-2007/Focus-app`, branch `agent/issue-14`.
Lens, only mine: **energy and delight — the warm, steady kind.**
The first-open ceremony should feel like being welcomed, not processed.

Ground, read whole before writing: `ReceptionSheet.kt` (1,296 lines,
current tree), `studio/plan/AUDIT-setup-sheet.md` (§1 inventory,
§2 S-1…S-15, §6 merge list), the cell's seat files (Fibel composition,
Funke mechanisms, Haut touch, Klang sound, Atem motion, Blank
first-ten-seconds, Halbwach 04:00 walk, Letzt tear, Fein type,
Zoll geometry, Kante edge table, Glatt park stub), and the issue
thread (Portal's One Reading, Rausch's ceremony pass, Persona ·
User 2's hesitations). Paper only — nothing compiled, nothing run.

**The law I work under, self-imposed and stated first:** every warm
word must pass Stachel's test. Warmth on this sheet is assembled
only from facts the sheet already proves elsewhere on its own face.
If a sentence would need the app to be nicer than it is, it dies
in §6 with the other kills.

---

## §0 · THE DIAGNOSIS — honest, and cold

The merge cured the sheet's lies. What remains is a temperature
problem, and it is measurable in the sheet's own print order:

1. The first three things he meets are a **model plate**
   (`MONOLITH ML-01`, PillBadge, onSurfaceVariant), a **form
   title** (`SETUP + INTRODUCTION`, Sora Bold 22), and a
   **definition** (the S-7 identity sentence). All three are
   about the machine. None is addressed to him.
2. The sheet's two kindest facts are printed at its **foot**,
   below the total, where Blank's census shows the first-open
   reader rarely arrives in his first pass: the DONE act-line
   (`IT STARTS NOTHING, SEALS NOTHING, AND MAY BE PRESSED WITH
   DOORS UNSET`) and the leave line (`LEAVE WHEN YOU LIKE — …`).
   The welcome exists. It is filed under the exit.
3. The sheet's one moment of earned joy — a door coming back
   **SET** after a FIX trip, chit testifying
   `WENT OUT … · CAME BACK SET.` — is spent in a single frame:
   F-6 collapses the card as a `when`-order, not a fade
   (Wave-2 finisher-motion, verified in code: the GRANTED early
   return at `ReceptionSheet.kt` ~:979). Persona · User 2 asked
   to *watch* the sheet shrink and to be *told* the FIX worked.
   Today both happen faster than reading.
4. The sheet's climax count, `GUARD 4/4`, prints in the same
   dress and beside the same act-line as `GUARD 0/4`. The
   difference between "nothing stands" and "everything stands"
   is one digit. Rausch killed confetti at 4/4 and he was right;
   but the opposite of confetti is not *nothing* — it is one
   true sentence changing. See §3.

None of this is a truth defect. It is why a truthful sheet can
still feel like processing. My four proposals fix temperature
only; every mechanism they touch belongs to another seat and is
named as such.

---

## §1 · THE WELCOME — one line, exact words, exact seat

**Print, in Movement 1, directly after the identity sentence
and before the boundary badge line:**

> `Nothing on this sheet starts anything. Take as long as you like.`

- **Style:** `BodyTextStyle` (Atkinson Hyperlegible 400 15/22),
  `onBackground` — the prose voice, at the identity sentence's
  weight class, not badge caps. A welcome whispered in telemetry
  dress would read as another plate. The boundary badge
  (`YOUR STUDY HOURS LIVE IN YPT. …`) follows it, so Movement 1
  reads: *what it is → you are safe here → the boundary.*
- **Truth audit, clause by clause.** "Nothing on this sheet
  starts anything" — the DONE act-line already prints
  `IT STARTS NOTHING, SEALS NOTHING`; OPEN THE LIST departs and
  returns (H-2 host law); FIX/CHECK depart and return; no key on
  the sheet arms, seals, or starts a session. "Take as long as
  you like" — the sheet has no timer, no expiry, and waits in
  PARK unfinished (stub note, F-12 seat); the leave line already
  grants the same in the other direction.
- **The bookend.** The leave line at the foot stays verbatim.
  Arrival and departure now speak the same promise in the same
  register — the sheet's first kindness is also its last, which
  is what makes it a welcome instead of a slogan.
- **Price:** +2 wrapped lines at Zoll's 320dp reference column
  ≈ **+44dp** in Movement 1, before any door. Repaid by F-6
  (−52dp per SET door, Zoll/Fibel figures) the first time any
  door is granted. No new colour, size, or motion. One string,
  one `Text`, one spacer adjustment (the existing 8dp below the
  identity becomes 8dp below the welcome).
- **What it is not:** not personalisation (no name is known and
  none is faked — Rausch's kill stands), not reassurance about
  privacy (the cards carry that, before the thumb moves, per
  Persona · User 2 — this line must never be asked to do the
  privacy lines' job), not a greeting to dismiss — it has no
  close button because it is not a banner; it is print, like
  everything else on the sheet.

**Coordination:** none. No other seat's item touches this seat;
F-2/F-3 already settled Movement 1's form.

---

## §2 · THE SET MOMENT — give the good news one breath

**Problem.** A repaired door's return is the sheet's only
earned reward, and it is currently spent instantly: the chit
line prints *beneath a card that has already collapsed*, under
a SET word whose ink emphasis (chitRepaired span, Funke §1.6)
most readers will never catch at collapse speed. Persona ·
User 2: *"Don't make me audit my own setup."* He cannot audit
what he cannot read in time.

**Proposal — the savour hold, exact mechanics:**

1. When a card's print flips to GRANTED **and** an unspent
   `RETURNED_REPAIRED` chit names that door (the exact
   `chitPrintsAgainst` condition already in code), the card
   does **not** take the F-6 early return immediately. It holds
   its expanded form — chit line under the job line, SET in ink
   per the existing chit exception — for **1,200ms**.
2. It then collapses to the F-6 ruled line over **260ms specA**
   (`MonolithKin.EasingSeat`, the house landing), height
   ~89dp → 37dp, the chit line fading with the body. One glide,
   no bounce, no overshoot — the vocabulary already on the
   sheet (Atem §0: no new curve is invented here).
3. **Sound: none added.** Klang's score already pays this
   moment — the firm click is the return verdict's second
   channel. One act, one voice (Haut's law). The hold is
   *visual* time: time to read `WENT OUT … · CAME BACK SET.`
   at 04:00, one-handed, at arm's length (~14 words at his
   reading pace ≈ 1.1s; 1,200ms is the honest figure, not a
   round one).
4. **Never on the 5s re-derive.** The hold fires only on the
   chit-bearing transition — a door discovered SET by a sweep
   or a CHECK collapses as today. Delight that repeats on a
   timer becomes a nag in under a minute; this one can only
   ever follow *his own errand*.
5. **Interrupt law:** any touch on the sheet during the hold
   ends it immediately (collapse completes in 160 THROW). His
   next act is never queued behind my celebration.

**Implementation shape (for Bauer/Portal):** the hold is a
per-card state keyed on (door, chit id); its trigger derives
from **Portal's single `SheetReading`** — the flip and the chit
both already live there. If the reading says the door was
GRANTED-with-chit at this swap and not at the previous, hold.
No second derivation, no snapshot access from the card.

**Price:** one `AnimatedContent`/height animation where today
there is a `when`; ~35 lines. Risk: low — worst failure is the
hold not firing, which degrades to today's exact behaviour.

---

## §3 · GUARD 4/4 — the act-line earns its second sentence

**Problem.** Under DONE, the act-line reads, at every count:

> `THIS FINISHES THE SETUP. IT STARTS NOTHING, SEALS NOTHING, AND MAY BE PRESSED WITH DOORS UNSET.`

At 0/4 that sentence is the sheet's mercy — it is what gets
Persona · User 2's thumb down. At 4/4 its last clause answers a
fear he no longer has, and nothing on the sheet marks that the
long scroll ended in a full house.

**Proposal — one swap, driven by the total itself:**

When `receptionTotal` (Portal: the reading's total) reports
count **4**, the act-line prints instead:

> `THIS FINISHES THE SETUP. IT STARTS NOTHING, SEALS NOTHING. EVERY DOOR STANDS SET.`

- Clause audit: the first two clauses are unchanged law. The
  third is the total's own S-9 branch (`ALL FOUR DOORS SET.`)
  carried to the one place his thumb is resting — not a new
  claim, a repetition of a proved one, at the decision point
  (the same one-fact-one-print discipline Gust's acquittal 7
  blessed for the Guard line, applied in reverse: here the
  datum is *under his hand*, so it prints there too).
- The unset-permission clause is *removed, not contradicted*:
  at 4/4 it is vacuous, and printing it would spend the
  moment's clarity on a case that does not exist (Kante's edge
  table: at count 4 there is exactly one combination).
- **No other 4/4 treatment.** No colour (ink stays DONE's
  alone), no sound (the tear pulse remains the sheet's loudest
  moment, 420ms, Letzt's), no tear-rule change. The celebration
  of 4/4 *is* the tear he is about to pull; §3 only clears the
  sentence out of its way.
- At every other count the line is byte-identical to today.
  STALE-at-4-granted prints count < 4 and gets today's line —
  correct: aged proofs have not earned the sentence yet, and
  CHECK re-proves them into it.

**Price:** one branch on the already-computed count; one
string; one test beside the existing act-line assertions.

---

## §4 · THE SMALL WARMTHS — three, each one line

1. **The stub note is a promise; print it like one.** The
   F-12-seated stub note (`THIS SHEET WAITS IN PARK — SETUP +
   INTRODUCTION.`) is the sheet telling him *unfinished is
   allowed to stay unfinished* — the textual twin of §1's
   welcome. It currently prints in the same badge dress as the
   PARK directions beneath it. Change **only its colour** from
   `onSurfaceVariant` to `onBackground`, keeping PillBadge
   style: the promise stands at prose weight, the directions
   (Glatt's wording, verbatim) stay dim beneath it. Price: one
   colour argument. Glatt's seat, his call — proposed, not taken.
2. **The rank line's fourth door.** `THE FOURTH MAKES IT QUIET.`
   is the sheet's most poetic clause and its only one about
   *his comfort*. Leave the words; they are acquitted. Noted
   here only to record that I tested replacing it with warmer
   copy and the original won — delight sometimes means
   defending the warm thing that already exists.
3. **First-routine line.** `NEXT ROUTINE — NAME · HH:MM · SET
   ON SHIELD` is the sheet's only sentence about *his actual
   day*. It is already the warmest datum on the page precisely
   because it is his. No change proposed; recorded so a future
   seat does not "improve" it into generic copy.

---

## §5 · WHAT I AM NOT TOUCHING (coordination ledger)

- **Portal's One Reading** — adopted as my substrate (§2, §3
  both consume it). If it lands differently than specced, my
  triggers re-point at whatever single reading exists; the
  proposals survive, the plumbing changes.
- **Rausch's Threshold / Prove First / Pull-Tear** — his
  ceremony, his file. §1 is the spoken half of his Handshake
  and nothing more; if his 900ms threshold ever lands, my
  welcome line is what the threshold should reveal *onto*
  (Movement 1 unchanged beneath it).
- **Atem's motion score** — §2 uses only his named vocabulary
  (260 SEAT / 160 THROW) and invents no curve. If he rules the
  collapse glide belongs to his tear file, I cede the glide and
  keep the hold: the hold is the delight; the glide is manners.
- **S-8 ROOM beat** — interim falsehood, composition gate with
  #13. Warmth never launders a false sentence: I propose
  nothing for that beat until it is true.

---

## §6 · KILLS — recorded so they stay dead

- **Applause sentences** (`GREAT JOB`, `WELL DONE`, `YOU DID
  IT` at any count): patronising at 04:00 to a man arming a
  lock against himself. Respect is this sheet's warmth;
  applause is a stranger's. Killed by the lens itself.
- **Setup percentages / progress bars:** a 75%-set blocker
  blocks nothing like 75%. The GUARD count is the only honest
  progress figure and it already exists. (Rausch's kill,
  seconded.)
- **Fake personalisation** (name, streak, "welcome back,
  warrior"): no source, no ship. (Rausch's kill, seconded.)
- **A warmer palette for first open only:** a face that changes
  its colours after setup teaches him the welcome was a costume.
  The welcome is words and time, never paint.
- **Staggered card entrances on the rise:** the sheet rises
  280 SEAT as one document (Atem, acquitted). Dealing the doors
  in like cards makes a ceremony of the *form*; the ceremony
  belongs to the *doors' verdicts* (§2), where it is earned.

---

## NEXT WANTS (mine, unassigned)

- On-device at the merge: read §1's line aloud at arm's length;
  if it lands as an instruction instead of a welcome, the
  fallback wording is `Take as long as you like — nothing on
  this sheet starts anything.` (clause order flipped, same facts).
- The §2 hold wants one number checked against the real chit:
  if the longest chit line wraps to 3 lines at 320dp, hold
  becomes 1,500ms. Zoll's tables can settle it on paper.

— Glut, enthusiastic designer, issue #14. DECLARED, uncompiled.
