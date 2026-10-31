# ISSUE #16 — THE PILL, GONE FERAL
## Crazy-ideas file — Glut (crazy-ideas seat, issue #16 cell)

**Commission:** crazy, original ideas for the pill only — the small persistent
object: peeks, breaks, a glanceable presence that reads instantly and never
nags. No implementation, no audit, no test runs (the re-run waits for Ren's
healthy-window call; nothing here touches it).

**Ground I built on (read, not re-litigated):** `PillFace.kt` (one pure
derivation → Absent / Session / Break / Peek, ceiling-rounded, dies exactly at
the stored end), `PillController.kt` (overlay window, touches the window only
when `renderKey` moves), `PillTheme.kt` (palette-as-data seam — looks redress
the face by writing a palette, never by surgery), `studio/plan/PILL-AND-TIMES.md`,
and Feel-5's instant-read grading (FAIL today: colon clock needs conversion,
no analog urgency channel, 9 sp label, touch verbs unsignalled).

**The one law every idea below obeys:** ideas consume the existing
`PillFace` states and the stored ends — **never a new clock, never a counted
tick.** The derivation stays the only truth-teller; everything here is skin,
physics, and theatre on top of it. Where an idea needs continuous motion it
says so and pays for it honestly, because continuous motion fights the
controller's change-only redraw model (§0, idea G-0 is the cheap truce).

**Studio law respected throughout:** never green; figures in Anonymous Pro,
labels in Atkinson Hyperlegible, any display word in Sora; no AI-generated
images — everything below is shape, motion, haptics, and type, all drawable
in code.

---

## §0 THE TRUCE — how motion affords itself

### G-0. Keyframe breathing, not animation
- **Idea:** All "alive" motion below is rendered as **discrete keyframes
  swapped at renderKey boundaries plus at most 4 extra beats per state** —
  the pill inhales by stepping through 3 drawn sizes on a slow cadence the
  controller already wakes for (its 2 s shield loop / 1 s face samples),
  never a 60 fps animator.
- **Mechanism:** Renderer picks frame = f(remainingSeconds) from a tiny
  lookup; motion is an illusion made of stills, like a flip-book.
- **Cost:** ~15 lines in the renderer; zero new threads, zero new wakeups.
  This is the enabling cheat for G-1, G-2, G-6 — without it they're battery
  crimes; with it they're nearly free.

---

## §1 SHAPE — the pill stops being a rectangle with news

### G-1. The Lung
- **Idea:** The pill breathes. Comfortable time = slow, deep swell
  (scale 1.00 → 1.04 over ~4 s). Under 30 s the breath shortens and shallows;
  under 10 s it holds its breath — perfectly still, which reads louder than
  any motion.
- **Mechanism:** Scale keyframes from G-0 keyed to remainingSeconds bands;
  stillness in the last band is just "no keyframe change."
- **Cost:** ~20 lines + the G-0 lookup. No new state. Risk: motion sickness
  of the soul — amplitude must stay ≤4%, this is a presence, not a jelly.

### G-2. The Melt
- **Idea:** Corner radius and posture decay with the fuse. At grant: crisp
  capsule, radius tight, body level. As seconds fall the corners fatten, the
  body sags a few px at the centre (drawn as asymmetric radii + a 1–2 px
  baseline droop), until at <10 s it's nearly a droplet. **Shape is the
  urgency band** Feel-5 found missing — readable in pure peripheral vision,
  no digits, no colour change, works over any app background.
- **Mechanism:** radius/droop = piecewise function of remaining fraction;
  recomputed only when renderKey moves (once a second at most).
- **Cost:** ~25 lines in the pill's draw code; zero layout change (bounding
  box fixed, only the silhouette inside it moves). Palette-agnostic — every
  look inherits it free through the existing shape path.

### G-3. The Hourglass Waist
- **Idea:** For the Session face: the pill pinches in the middle in
  proportion to block progress — wide at block start, waisted at the middle,
  and it *releases* (swells back) as the block nears its end, like something
  about to be let go. Block i of n also gets i subtle notches on its spine.
- **Mechanism:** Waist depth = sin(progress·π); notches = blockIndex dots
  drawn in the border hairline, no text.
- **Cost:** ~20 lines. Session face only; Peek/Break ignore it. Gives the
  session pill a body-language the peek pill doesn't have — the two objects
  stop being confusable at a glance (Feel-5's question 1).

### G-4. Break Weather
- **Idea:** The Break face isn't a countdown wearing the pill — it's a
  different climate. The break pill is *wider, lower, and softer*: text
  breathes out to the intention word ("BREAK" is a state, the remaining
  time is a footnote in smaller figures), and the whole capsule carries the
  G-1 breath at rest tempo for the full break, however long is left. A break
  should feel like weather clearing, not a shorter fuse.
- **Mechanism:** Break face gets its own layout constants (aspect + label
  hierarchy swap) in the renderer; breath cadence pinned to the calm band.
- **Cost:** ~15 lines of layout constants. Zero derivation change — Break
  already outranks Peek in the derivation, this just lets the face *look*
  like it outranks it.

---

## §2 PHYSICS — the pill has mass

### G-5. Sand Weight
- **Idea:** The pill fills with sand as a peek burns. Early peek: drags
  light, snaps to the edge eagerly, a flick sends it gliding. Late peek:
  drag lags a hair behind the finger, snap is reluctant, glide dies in
  centimetres. Nobody reads this consciously; everybody *feels* the fuse
  in their thumb.
- **Mechanism:** Drag/snap spring constants (damping, fling friction, snap
  velocity threshold) scaled by remaining fraction — the physics parameters
  Feel-1 (Weight) already specced get a time input.
- **Cost:** ~10 lines where the spring constants are set; one multiply.
  Session face: constant weight (a session isn't draining). Break face:
  weight *leaves* as the break ends — it gets lighter as you return, the
  inverse signature.

### G-6. The Recall
- **Idea:** A peek never just *vanishes*. At expiry the pill is recalled:
  180 ms shrink-and-fly to the screen edge it was granted from, gone.
  Early hand-back (✕) does the same flight, slower and level — *returned*,
  not *repossessed*. Two deaths, two meanings, zero text.
- **Mechanism:** One exit animation on the overlay window (translate+scale
  to the grant edge), chosen by end-cause (expired vs handed-back), both
  already distinguishable where the lease ends.
- **Cost:** ~40 lines, the most expensive idea in §1–2. One-shot animation
  only (not continuous), so the battery objection doesn't apply. Risk: the
  window teardown must wait for the flight — cap at 200 ms hard.

### G-7. The Ghost
- **Idea:** After a peek dies, its silhouette stays: a hairline outline of
  the pill, no fill, no figures, at the death spot, fading over the first
  ~20 s of cooldown. The closed door leaves a chalk mark. If he glances
  back at the app he was just thrown out of, the ghost is the reason,
  wordlessly.
- **Mechanism:** A second, inert overlay (no touch target, no text) with
  alpha keyed to cooldown-elapsed; reuses the pill's last bounds.
- **Cost:** ~30 lines + one extra window during cooldown only. Must respect
  the derivation's absence rules (keyguard / own-app / telecom → no ghost
  either) — it's a face, so it derives like one: ghost = a fifth PillFace
  case or a controller-side echo; the echo is cheaper and keeps the sealed
  derivation untouched. **I recommend the echo.**

---

## §3 READING — killing the colon clock

### G-8. The Honest Bar
- **Idea:** Under the figures, a fuse-line: a hairline that burns down
  from full width to nothing across the pill's belly, in the accent
  (#F47442 family via palette). At 0.5 s you don't read "1:23" — you see
  *most of a line* or *almost no line*. The digits stay for confirmation;
  the line is the message.
- **Mechanism:** One filled rect, width = remaining fraction of the granted
  span (lease end − grant instant — both already stored/derivable).
- **Cost:** ~10 lines. The single highest value-per-line idea in this file.
  For Session: the bar fills *up* with elapsed instead — same line, opposite
  direction of travel; direction itself encodes which face you're seeing.

### G-9. Digits That Spend Themselves
- **Idea:** The figures thin as time falls. Anonymous Pro at full weight
  for the comfortable band; the last 15 s render in the font's lightest
  cut / reduced alpha — the number is literally running out. Pairs with
  G-2: shape and type decay together, one signal in two channels.
- **Mechanism:** Alpha step (3 bands) on the figures paint — no font swap
  needed if the bundled TTF lacks weights; alpha is the honest cheap cut.
- **Cost:** ~5 lines. Subtle on purpose: it's a peripheral cue, not a
  headline.

### G-10. One Word, Never a Sentence
- **Idea:** The peek pill's label stops being the app name in 9 sp and
  becomes a **state word in Sora**: PEEK while comfortable — then, under
  30 s, the word is gone and only figures + fuse remain (less to read as
  less time to read it); at expiry the recall (G-6) speaks. Session pill:
  the intention word, always. Break pill: BREAK, always, calm. The pill
  never prints a sentence, never explains, never nags — vocabulary of
  ≤1 word + figures + one line.
- **Mechanism:** Label selection by face+band in the renderer; delete the
  app-name print from the peek face (the app name is known — he's *in* it).
- **Cost:** ~10 lines, mostly deletion. Directly answers Feel-5 IR-1/IR-2.

---

## §4 TOUCH — verbs you can predict before you touch (Feel-5, question 3)

### G-11. The ✕ Grows Teeth Only When It Must
- **Idea:** The hand-back ✕ sits dim and small while the peek is young —
  leaving early is unremarkable. It does **not** grow, pulse, or plead as
  time falls (that's nagging). Instead the *body* gains the affordance:
  a long-press on the body visibly dents the pill (scale 0.96, radius
  squash) building toward hand-back — press-and-hold to give the time
  back, with the dent as the progress bar. Release early: it springs back,
  nothing happened, nothing recorded.
- **Mechanism:** Long-press handler with keyframed squash (G-0 stills);
  commit at ~600 ms hold → existing early-end verb.
- **Cost:** ~35 lines. Replaces a discoverability problem (what does the
  body do?) with a physical metaphor (squeeze to return). ✕ stays for
  those who know it; the dent teaches everyone else.

### G-12. Throw It Away
- **Idea:** Flick the pill off the edge of the screen — a real throw, past
  a velocity threshold, off its snapped edge — and that's a hand-back too.
  The pill flies out with G-6's returned-flight. Dismissing a distraction
  timer by *throwing it* is the most honest gesture this object can learn.
- **Mechanism:** Fling detector on the drag path; threshold high enough
  that repositioning never triggers it; throw direction must be outward.
- **Cost:** ~20 lines on the existing drag handling. Risk: accidental
  throws end peeks "unfairly" — mitigate with the threshold + the throw
  only counts while a peek is live (session/break pills just snap back,
  teaching the difference for free).

---

## §5 PRESENCE — the pill across a day, never nagging

### G-13. The Pill Ages, The Session Doesn't Lecture
- **Idea:** Across a long session the pill's border hairline accrues the
  block notches of G-3 permanently for the session's life — a quiet tally
  worn on the body, not a score, not a total, never announced. In the
  Drawer afterwards (his quiet, unjudged drawer), nothing changes; this
  is worn only while it's happening, then the pill is born clean next
  session. Presence with amnesia — the opposite of a streak.
- **Mechanism:** Border notch count = blocks completed this session, from
  the session row the derivation already reads.
- **Cost:** ~10 lines. Zero persistence — deliberately. The moment this
  survives the session it becomes a score, and scores are banned physics
  in this app.

### G-14. First-Second Ceremony
- **Idea:** When a session starts, the pill is born with one deliberate
  beat: it fades in *already at its snapped post*, does a single slow
  breath (G-1's deepest keyframe cycle, once), then settles. No banner,
  no "session started", no sound. One breath = "I'm here, we're working."
  The same single breath is the break's opening. Beginnings get a ritual;
  middles get silence; only endings (G-6) get motion.
- **Mechanism:** One-shot keyframe sequence on face transitions
  Absent→Session / Session→Break, ~2.4 s, non-repeating.
- **Cost:** ~25 lines. Risk: ceremony fatigue — cap it at exactly one
  breath, and it never replays on face *re-derivations*, only on true
  transitions.

### G-15. The Pill Refuses Some Rooms (already law — make it visible once)
- **Idea:** The derivation already goes Absent for calls, keyguard, own
  app. Crazy extension: **in a call, the pill leaves a single dot** — one
  6 dp accent point at its post, no figures. Not information; a promise:
  " still counting, voice outranks me, I'll be back." One dot is the
  smallest possible presence and it never appears anywhere else, so it
  can't be misread.
- **Mechanism:** A sixth render shape for the telecom-foreground case
  instead of full absence — **flagged honestly: this amends a §2.4 ruling
  (Q4), so it's Ren's call, not a silent swap.** Default if unruled: keep
  full Absent; the dot ships only on a ruling.
- **Cost:** ~15 lines + one ruling. Listed because the *idea* is the
  smallest-presence vocabulary the object could ever need.

### G-16. Cooldown, Printed Once
- **Idea:** SIL-3's wound: a withheld peek gives no reason, and the price
  is never printed. The pill's answer, in its own grammar: when he tries
  the blocked app during cooldown, the pill appears **hollow** — outline
  only, fuse-line empty, one figure: the minutes until the door reopens,
  in the muted tier. No "cooldown!" text, no scolding. A hollow pill =
  a door that exists but is empty right now. It stays 3 s, once per
  attempt, then Absent.
- **Mechanism:** A render variant of Peek with remaining = cooldown
  remaining (derivation already computes cooldown anchors in the
  repository; the face needs a read of it — **this is the one idea that
  asks the derivation for a new input**, priced as such).
- **Cost:** ~30 lines + the derivation input (the real cost — a new
  derived state means new tests in the owed suite's family; Ren windows
  it or kills it). Everything else in this file rides existing states.

---

## §6 MY RANKING, IF REN MAKES ME CHOOSE

1. **G-8 The Honest Bar** — 10 lines, fixes the instant-read failure at
   its root. Ship this before anything else in the file.
2. **G-2 The Melt** — shape-as-urgency; the pill's first true identity.
3. **G-10 One Word** — mostly deletion; the pill stops explaining itself.
4. **G-6 The Recall** — endings with meaning; the one animation worth
   paying full price for.
5. **G-5 Sand Weight** — felt, not seen; cheapest soul in the file.
6. **G-1 + G-0 The Lung on the Truce** — presence without battery crime.
7. **G-11 The Dent** — the discoverable hand-back.
8. **G-7 / G-16** — the cooldown pair (ghost + hollow pill): powerful,
   but G-16 costs a derivation input; sequence them ghost-first.
9. **G-3 / G-13 / G-4 / G-14 / G-9 / G-12 / G-15** — the seasoning:
   each small, each safe, none blocking the eight above.

**What I deliberately did NOT propose:** colours that shift toward red
(shouting), any sound from the pill (its silence is its dignity; the seal
owns sound in this app), any streak/score/memory across sessions, any
text longer than one word, and anything that needs a new clock. The pill
never nags — every idea above *reduces* what it says as pressure rises.

— Glut (crazy-ideas, issue #16)
