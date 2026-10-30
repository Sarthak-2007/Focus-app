# DESIGN — THE PILL, MET KINDLY (issue #16)

Seat: **Glut (enthusiastic designer)**, dedicated-100, Pastille's
cell, GitHub issue #16, `Sarthak-2007/Focus-app`, branch
`agent/issue-16`. Secondary seat (primary: #14, the first open).
Lens, only mine: **energy and delight — the warm, steady kind.**
A small object that should feel good to meet.

Ground, read whole before writing: `service/PillFace.kt`,
`service/PillController.kt`, `service/PillTheme.kt` (cell clone,
presse package state), the issue thread (Pastille's verification
record, Wave-2 addenda), the cell's five feel files (weight,
light, timing, touch+sound, instant-read), and the crazy-ideas
seat's grammar (G-0 flip-book discipline, G-2 Melt, G-5 Sand
Weight, G-6 Recall, G-8 Honest Bar, G-10 shrinking vocabulary).

**Lane discipline, stated first.** The crazy-ideas seat owns
the pill's *urgency* — shape, fuse, weight, recall. I own its
*manners* — arrival, greeting, homecoming, departure. Nothing
below touches the derivation's priority order, the palette, or
the fuse channels. Where my proposals meet his (endings), I
adopt his mechanism and add only the beat he didn't spec.

**The law I work under:** the pill never editorialises. It is a
companion that keeps time beside him inside other apps — apps
he is, by design, not supposed to be lingering in. Its warmth
is therefore *steadiness*, never encouragement to stay: no
cheering, no commentary on the peeked app, no reward language.
Delight here means: it arrives politely, it speaks his own
words back to him, it leaves without a fuss.

---

## §0 · THE DIAGNOSIS — a true clock with no manners

The derivation is the most honest small machine in the app:
one clock, ends-derived, ceiling-rounded, dies exactly at the
end instant (Feel-5). Its *presentation* has three discourtesies,
all verified in `PillController.kt`:

1. **Birth is a teleport.** `buildWindow()` constructs the view
   and `addView`s it at full alpha in its final seat (right
   edge, 22% height, corrected by one `post{}`). The pill is
   never seen arriving. Feel-1 called the drag weightless; the
   birth is worse — it is *eventless*.
2. **Death is a cut.** `removeWindow()` removes the view in the
   same frame the face turns Absent. Session's end, break's end
   — the companion of hours vanishes mid-glance. (Peek's death
   is the crazy seat's G-6 Recall; §3 adopts it.)
3. **The Session face files him.** `render()` prints
   `SESSION · BLOCK n/m` while `PillFace.Session.intention` —
   carried through the derivation on both arms from the session
   row — is never read by any renderer. The pill holds his own
   stated purpose for the session and labels him with a block
   index instead. This is the single coldest line in the lane,
   and the cheapest to warm: the data is already there.

---

## §1 · THE INTENTION GREETING (Session face)

**Exact render spec** — `PillController.render`, Session arm only:

- `intention.isNotBlank()` → label = `intention.uppercase()`,
  truncated to **22 characters**, ellipsised (`…`) at the cap.
  Figures line unchanged (remaining, or `+elapsed` stopwatch).
- `intention.isBlank()` → today's labels, byte-identical:
  `SESSION · BLOCK ${blockIndex}/${blockCount}` /
  `SESSION · ELAPSED`.
- Type: the label stays in the figures typeface (Anonymous Pro,
  9sp, `mutedTextArgb`). His words in the machine's telemetry
  dress is the right register — the machine repeating him back,
  not the machine performing folksiness in a second font.
  Loading Atkinson into the overlay for one line is priced and
  declined (§5).
- **Why 22:** the pill is `WRAP_CONTENT`; an uncapped intention
  ("Crack NEET physics — rotational mechanics full chapter
  revision before…") would stretch the pill across the app
  beneath it and turn a greeting into a banner. 22 chars at
  9sp Anonymous Pro ≈ 118dp — inside the pill's natural width
  at its longest current label. The cap is the greeting's price;
  state it, pay it.

**Truth audit:** the intention is his own session-row text,
printed verbatim (case-folded to the label tier's standing
caps). It cannot go stale — it re-derives with the face every
tick, and dies with the session. No new derivation input, no
new stored state: **zero derivation change**, renderer only.

**Merge flag (Route B parity):** the Wave-2 addendum records
that Route B (the shield notification's peek face) does *not*
consume `PillDerivation` despite the one-derivation claim.
This greeting lives in Route A's renderer; if Route B ever
grows a Session face, it must print the same label rule or the
two mouths will greet differently. Flagged for the batch-10
composition, not silently assumed.

**Test note (for the verification package, not a run):** the
18 owed tests pin the derivation; this proposal changes no
derivation behaviour, so no owed verdict moves. One new
renderer assertion is owed *if* it lands: label selection
(intention / fallback / cap) as a pure function — extract
`sessionLabelFor(face)` beside `formatClock` so it is testable
without a window.

---

## §2 · THE ARRIVAL (birth choreography)

**Exact mechanics** — in `buildWindow()`, after the successful
`addView` and the existing right-edge `post{}` correction:

1. Initial state at add: `alpha = 0f`, `translationX = +14dp`
   (toward the edge it was born at — it slides *in from* its
   margin, never across the glass; the peeked/session app keeps
   the centre, the pill takes its seat like a person sitting
   down at the edge of a desk).
2. One `ViewPropertyAnimator`: alpha → 1, translationX → 0,
   **180ms**, interpolator
   `PathInterpolator(0.2f, 0f, 0f, 1f)` — `MonolithCurves.EasingA`
   carried into View-land verbatim, so the overlay lands on the
   house curve, not a platform default. (The snap animator
   already in this file uses the raw default; aligning it is
   Feel-1's file, not mine — flagged, not taken.)
3. **Once per window build, never per render.** The
   `renderKey` early-return already guarantees renders are
   change-only; the arrival hangs off `buildWindow`, which runs
   only on Absent→face transitions. A face *change* inside a
   standing window (Peek→Break, Break→Session) does **not**
   replay it — see §4 for the one exception, at half length.
4. **Drag supremacy:** `ACTION_DOWN` cancels the arrival
   animator alongside the snap animator (one line beside the
   existing `snapAnimator?.cancel()`). His hand always outranks
   my choreography.

**Battery discipline (G-0 honoured, priced exception stated):**
the crazy seat's truce — motion as flip-book stills at render
boundaries, never an animator — exists to stop a per-second
animator culture. This arrival is a **single 180ms animator,
once per birth**, in the same exception class his G-6 already
claimed for endings. Beginnings and endings are the pill's
only two animated moments; the long middle stays stills. If
the cell rules the truce absolute, the fallback is a 2-still
flip (alpha 0.45 at build, 1.0 at first render change) —
uglier, honest, and noted here so the ruling has somewhere
to land.

**Price:** ~15 lines in `buildWindow` + 1 in the drag listener.
No new permissions, no new state, no derivation touch.

---

## §3 · HOMECOMING (hand-back) AND DEPARTURES

**Hand-back (✕ on the Peek face).** Today: silent instant
`recordPeekEnded`, the window cuts to whatever derives next.
Adopting the crazy seat's G-6 Return flight for the peek's
exit, I add only the beat after it:

- The peek pill departs (his flight, slower arm — a return,
  not a repossession).
- The face that follows is, in session, the **Session face —
  and it arrives with §2's arrival at half length (120ms,
  +8dp)**. This is the one replay §2.3 promised: the felt
  sentence, with no text added, is *peek handed back, session
  kept*. The pill leaves as a fuse and comes back as a
  companion; the two arrivals must not feel identical or the
  distinction is wasted.
- **No sound.** Feel-4 established `PillController` has no
  engine reference and every overlay moment is silent by
  wiring. Wiring one for a homecoming chirp is priced
  (constructor + service construction + a night law) and
  **declined**: the pill's manners are visual. A silent object
  that arrives well beats a chatty one.

**Session / Break departures.** When the face turns Absent
because the session or break *ended* (not keyguard, not
telecom, not own-package — those three are courtesies to other
surfaces and stay instant cuts: a pill lingering over the lock
screen to finish a fade would be a discourtesy, not a delight):

- `removeWindow` is preceded by a **140ms fade** (alpha → 0,
  no translation — it doesn't go anywhere; it concludes),
  then removal. Implementation: the controller's tick already
  mediates every face change; the fade is one animator on the
  standing root, with removal posted at its end. If the face
  returns within the fade (a new session armed inside 140ms —
  rare, possible), the fade cancels and §2's arrival does
  *not* replay: the window never died, so nobody re-arrives.

**Price:** ~20 lines; one new private animator field beside
`snapAnimator`.

---

## §4 · THE BREAK FACE — manners for the rest minutes

The Break face is the pill's only *resting* state and it
currently wears the fuse's dress: label `BREAK`, figures
counting down, nothing distinguishing rest from deadline
except the word. Three small rulings, all renderer-only:

1. **Label stays `BREAK`.** Candidates audited and killed:
   `BREAK · DOORS OPEN` (overclaims — the shut doors stay shut;
   the sheet's BREAK beat says so), `BREAK — OFF THE CLOCK`
   (false — the clock runs through it), `BREAK · RESTING`
   (vibe, not fact). The honest word is the warm enough word
   when the *transitions* carry the tone: §2's arrival opens
   the break, §3's fade-and-return closes it — the exhale is
   in the motion, not the label.
2. **No ✕, ever — already true** (`closeText` GONE on Break):
   recorded as a delight, not an oversight. A rest you can
   accidentally end with a stray tap is not rest. (Ending a
   break early lives in Monolith, where it is a decision.)
3. **The last second is not special.** No final-seconds
   treatment on the Break face — no quickening, no emphasis.
   A break that starts behaving like a fuse in its final 15s
   spends its whole length being dreaded. The Session face's
   return (§3's 120ms arrival) is the only "break is over"
   signal, and it is a welcome, not an alarm.

---

## §5 · KILLS — recorded so they stay dead

- **Editorial greetings** (`BACK TO IT →`, the app name with
  punctuation, any verb addressed to him from the pill): the
  pill serves a man inside an app he chose to ration. It keeps
  his time; it does not coach, cheer, or comment. Killed by
  the lens itself — steady warmth, never enthusiasm *at* him.
- **Atkinson for the intention line:** a second typeface load
  in the overlay for one 9sp line; the telemetry dress is the
  house register and the words carry the warmth. Declined,
  priced above (§1).
- **Colour warmth** (a warmer background on the Session face,
  a softer border on Break): the palette is `PillTheme`'s —
  the looks' property, data by design. A seat that recolours
  one face forks every look. Killed on ownership, not taste.
- **A breathing idle** on the Session face (the crazy seat's
  G-1 lung, applied to session): his grammar assigns breath to
  the *fuse*. A session of hours breathing in his periphery
  for its whole length is a distraction engine wearing a
  kindness costume. If the lung ships, it ships for Peek only.
  Recorded as my one dissent in his file's direction.
- **Persisting the pill's seat as a "welcome back"** (returning
  it to his dragged position next session): position is
  residency-scoped by §2.1's priced ruling. A pill that
  remembers where he left it is a nice ghost and a reopened
  decision. Killed on the ruling, noted for the looks wave if
  they want to re-price it.

---

## NEXT WANTS (mine, unassigned)

- If the intention greeting lands, watch one width: a 22-char
  intention + `MM:SS` at the longest block label — Zoll-style
  paper measure can prove the pill stays inside ~55% of a
  360dp screen before any device does.
- The §2 arrival wants one number from the merge: if
  `buildWindow`'s right-edge `post{}` correction and the
  arrival animator fight on slow devices, sequence them
  (correction first, animator on the next frame) — named here
  so the builder doesn't have to discover it.

— Glut, enthusiastic designer, issue #16. DECLARED, uncompiled.
