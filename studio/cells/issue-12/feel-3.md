# FEEL-3 — Motion — Issue #12: Tasks + F-1

**Issue:** Sarthak-2007/Focus-app #12 — Tasks + F-1
**Role:** Feel-3 · motion (issue #12 cell)
**Status:** DECLARED — motion spec only, uncompiled, no code written, no Gradle/tests run
**Sources:** `studio/plan/AUDIT-tasks.md` (Amt's Tasks audit, Weiss's motion
measurements as baseline) and `studio/plan/PLAN-issue-12-tasks-f1.md`
(Amt, cell lead) in the cell clone on branch `agent/issue-12`

— Feel-3 · motion (issue #12 cell)

---

## 0. Baseline: what Weiss measured, and why it is a lie in motion

The audit (item 13, fix F-7) convicted the tear as built, and the numbers
are this seat's floor — nothing below re-litigates them:

- The fade's curve A reaches **0.878 of its fade by 90 ms** — nearly the
  whole disappearance is spent in the first third of the gesture.
- The leaf drops **10 dp against a 12 dp inter-leaf gap** — it does not
  even clear one leaf-space before it is gone.
- The perforation dashes run a **260 ms settle inside a layer whose alpha
  reaches 0 at 180 ms** — the tear's second half is painted inside a
  layer that no longer exists. Invisible by construction.

In felt terms: the eye is shown a sheet *evaporating*, quickly, while
the one part that would have read as *tearing* — the dashes letting go
along the cut — happens after the sheet has already vanished. That is
not a bad tear. It is a fade that borrowed a tear's name, and a named
motion that performs a different motion is a lie of exactly the kind
this audit exists to kill.

Weiss's respin is ruled and adopted whole as this pass's tear (F-7,
~15 lines): **30 dp travel, one 300 ms clock, alpha held to 40%,
accelerating cubic, perforation lifted out of the fading layer, ghost
lifetime 260 → 300 ms.** What follows is not a second respin. It is the
felt character that respin must be tuned to — what the acceleration is
*of*, where the separation lands inside the 300 ms, how the ghost
leaves — plus the motion spec for everything else on this screen that
moves: the queue's micro-motions, the seal, and the calm the system can
ask for. Every motion below carries three things: a **duration**, a
**curve in plain words**, and a **never** — the thing it must not do
even if doing it looks smoother in a demo.

One law governs the whole file, stated once: **booking precedes motion,
always (G-01).** The tear books at the tap; the queue reorders at the
key; the seal writes at the seal instant. Motion is the *receipt* the
eye gets for a fact that has already happened — never the act itself,
never a gate on it. No motion in this spec may delay, confirm, or be
cancellable in a way that un-books the fact.

---

## 1. The tear — the screen's one dramatic motion

### 1.1 Character: cling, separation, leave

Real paper on a perforation does not leave evenly, and it does not
leave immediately. It *clings*, then it *goes*. The 300 ms runs in
three felt phases on Weiss's one clock — one clock, not three chained
animations, so no phase can drift against another:

| Phase | Window | What the eye sees |
|---|---|---|
| **Cling** | 0–~100 ms | The leaf holds almost still — at most the first 3–4 dp of the 30 dp. The perforation, now lifted out of the fading layer at the unified tone (`outlineVariant` @ 0.55, audit item 17), takes the strain: the dashes' settle finally plays *visibly*, because the paper around it is still at full strength. This is the half the old build threw away. |
| **Separation** | ~100–130 ms | The break. Travel passes the point of no return — roughly the first third of the distance spent in roughly the first third of the time, then the leaf is unmistakably *off* the pad. This is the moment the gesture must be *felt* at: not at the tap (the tap is intent) and not at the end (the end is absence). If a finisher tunes only one frame, it is this one: before it, he could still imagine the leaf staying; after it, the leaf is gone and only its leaving remains. |
| **Leave** | ~130–300 ms | The freed leaf accelerates away through the remaining travel — the last ~20 dp in the last ~170 ms — at full commitment. Alpha falls only now, and only to Weiss's **40% floor**: the ghost is still *there*, a sheet on its way out, when the clock ends. |

- **Duration:** 300 ms total (ghost lifetime, Weiss). No tail, no
  second fade appended after it.
- **Curve, in plain words:** *slow to let go, fast to leave* — an
  accelerating cubic. Near-zero speed at the tap, speed building
  through separation, fastest at the very last frame. The exact
  opposite of the convicted curve, which spent 0.878 of itself by
  90 ms: that curve was in a hurry to hide the leaf; this one is in
  no hurry to hide it and in a great hurry once it is free.
- **Direction:** straight down the pad's plane, along the queue's own
  axis. The ghost leaves the way the next leaf will arrive — the eye
  reads one continuous column of paper.

### 1.2 How the ghost leaf leaves

At 300 ms the ghost is removed — cut, not faded out over a second
clock. Its removal is invisible in practice because two things are
true at that frame: it has travelled the full 30 dp (two and a half
times the old drop, and — after F-8's gap change, 12 dp → 4 dp — well
past the neighbouring leaf's territory), and the successor has already
begun its rise (§1.3), so the eye's attention has been handed over.
A ghost that needs a fade to disappear was not given enough travel.

**The tear must never:**

- **Never fade first.** Alpha leading travel — the leaf going
  transparent while still sitting in its slot — is the convicted
  motion, whatever numbers it is re-tuned to. Travel leads; alpha
  follows, floored at 40%.
- **Never rotate, tilt, scale, or peel.** No corner lift, no crumple,
  no flutter. This pad is flat paper under a straight cut; rotation
  reads as cartoon and crumple reads as judgement on the task. The
  leaf leaves whole, face-on, the way it lived.
- **Never bounce, overshoot, or ease out at the end.** A freed sheet
  does not decelerate politely into its exit and it does not spring.
  Deceleration at the end would say the leaf changed its mind.
- **Never pull the pad with it.** The leaves below, the pad edge, the
  header — none of them jolts, dips, or recoils when the head tears.
  Paper tears *off* a pad; the pad does not flinch.
- **Never outlive its clock.** No ghost lingering at 40% past 300 ms
  as a translucent memorial. During the ghost window, `#NN` and the
  QUEUE header count derive from the displayed list, ghost included
  (audit item 8) — print and paper agree for exactly the window, then
  both step together.
- **Never make the tap wait.** Booking is at the tap (G-01). A second
  tap mid-tear on the *next* leaf must book normally — the ghost owns
  no input.

### 1.3 The successor's rise — the vacancy is part of the tear

When the head tears, the queue advances. That advance is a motion too,
and it must not steal the tear's stage:

- **Duration:** 180 ms, beginning at the separation moment (~110 ms
  into the tear's clock), so the new head is settled into the raised
  place just after the ghost's clock ends.
- **Curve, in plain words:** *a step up, taken calmly* — decelerating.
  Fast enough at first to close the vacancy, soft into the head's
  raised position with no overshoot. Every queued leaf below moves up
  exactly one slot (leaf height + the new 4 dp gap) on the same
  180 ms, as one column — no stagger, no cascade. A stagger would
  turn the advance into a wave and make the queue look liquid.
- **The head's honours transfer at arrival, not during travel:** the
  raise, the bold, the accent verb land when the leaf lands. Mid-rise
  the leaf is a queued leaf moving; on arrival it is the head. Two
  leaves must never hold the raise at once.
- **Must never:** never rise before the separation (the successor
  pushing the dying leaf out reads as eviction, not completion);
  never scroll the viewport to follow; never renumber `#NN` mid-rise —
  the numbers step at arrival, in the same frame as the raise.

### 1.4 ZIP-TEAR — the standard after this pass, motion noted, not built

The audit rules Vega's ZIP-TEAR the house standard for the *next* tear
build (~180 lines: finger traces the perforation in a 32 dp band,
dashes consume behind the finger, booking at 85% crossing in G-01
order, tap retained as the accessibility floor), and the cell plan
confirms this pass ships the F-7 respin only — with the threshold at
the audit's 85%, not 92%. Motion records one requirement now so the
next build inherits it: under the finger, dash consumption is
**1:1 with the finger — zero lag, zero follow-through, no spring
catching up after the finger stops.** A tear that trails the hand
feels like dragging through glue; a tear that leads it feels like the
machine guessing. The finger is the clock. On release past 85%, the
untraced remainder completes on §1.1's accelerating curve in ≤120 ms —
the machine finishing a break the hand already made.

---

## 2. The queue's micro-motions

The queue is paper at rest. Its micro-motions exist to answer one
question — *where did that leaf go / come from?* — and then to stop.
Any queue motion longer or louder than the answer is decoration on a
tool used half-asleep (see §4 for what that costs).

### 2.1 A new leaf landing at the foot (ADD)

The recomposition (audit item 2, fix F-2) makes the composer the pad's
own next blank leaf at its foot; ADD is its end-key. On the key:

- **What moves:** the written leaf alone. It settles *down* into its
  slot from 6 dp above it, and the pad edge's stack gains its leaf —
  the lines behind step one tone quieter where the cap of 3 allows
  (audit item 16). Nothing above the new leaf moves at all: appending
  at the foot disturbs no existing order, and the motion must say so.
- **Duration:** 200 ms.
- **Curve, in plain words:** *set down, not dropped* — decelerating
  throughout, most of the 6 dp spent early, the last dp slow. Paper
  arriving on a pad from just above it, under a hand, not from a
  height.
- **Arrival print:** the leaf's `#NN` and the header's `N TASKS` step
  in the same frame the leaf lands — not before (a count promising a
  leaf not yet there), not after (a leaf the count disowns).
- **Must never:** never bounce or compress the leaf below it; never
  auto-scroll the pad to chase the landing if the foot is off-screen
  — the write happened on the composer leaf he is already looking at,
  so his viewport is the incident, and the pad does not drag his eye
  anywhere; never flash, highlight-fade, or tint the new leaf to
  announce it. He wrote it. He knows. A highlight says the machine
  thinks he wasn't paying attention.

### 2.2 FIRST — a leaf sent to the head, without a teleport

This is the hard one, and the audit's own words set the bar: CUE's old
behaviour — promote *and teleport* — dies (item 14), and FIRST (item 2,
Muede's errand) must *show the jump*. A leaf that vanishes from the
foot and is simply *there* at the head teaches the eye nothing, and at
1 AM an unexplained rearrangement reads as a mistake he has to audit.

The pad shows the jump as a **lift, travel, and landing**, in three
cases by distance:

- **Short jump (the leaf passes ≤4 leaves):** the new leaf lifts to
  the head's raised level, travels up the pad's own column over the
  leaves it passes, and lands in the head slot. Every leaf it passes
  steps down exactly one slot to make the room, beginning its step as
  the travelling leaf reaches it — the room opens *ahead* of the
  traveller, so no two leaves ever occupy one slot on any frame.
  - **Duration:** 280 ms, whatever the distance within the case.
  - **Curve, in plain words:** *hurried in the middle, careful at
    both ends* — accelerates out of the foot, fastest mid-column,
    decelerates into the head slot. Like a sheet pulled from the
    bottom of a pad and laid on top by one hand: the pull is quick,
    the placing is deliberate.
- **Long jump (more than 4 leaves — the bad-week queue):** nobody
  watches a leaf commute the full pile. The leaf lifts exactly as
  above, fades to the 40% floor over the first 80 ms *while rising
  clear of the column*, the intervening leaves step down one slot
  together in a single 160 ms decelerating shift, and the traveller
  re-materialises from 40% as it descends into the head slot over the
  final 80 ms. Total **320 ms**, capped — never a millisecond longer
  for a longer queue.
  - This is the one sanctioned fade in queue motion, and it is honest
    in a way the old tear was not: it does not pretend the leaf
    travelled a distance it did not travel. It shows *leaving the
    column* and *arriving at the head* and declines to fake the
    middle. The tear's fade was a lie because the tear *is* the
    middle. Here the middle is transport, and transport may be elided
    if both ends are shown.
- **Landing:** the head's honours — raise, bold, `#01`, accent verb —
  transfer in the landing frame, and renumbering steps in that same
  frame, all leaves at once. Before the landing frame, the travelling
  leaf carries no number; a number en route would be a claim about a
  position it has not reached.
- **CUE is the same motion.** CUE (audit item 14: make this leaf the
  head, STAY on Tasks) promotes an existing leaf; it uses this exact
  lift-travel-land, from wherever the leaf sits, at the same durations
  by the same distance cases. One promotion motion in the app, learned
  once. FIRST is simply CUE's motion with the composer leaf as its
  subject.
- **Must never:** never an instant swap — not for any distance, not
  at 1 AM, not as a "performance optimisation"; the teleport is the
  convicted act. Never scroll the viewport to follow the traveller
  (if the head is off-screen, the travel happens off-screen too and
  he finds the result when he looks; the pad does not seize his
  scroll position at night). Never navigate anywhere — CUE/FIRST end
  on Tasks, on the pad, still (item 14). Never let the old head keep
  its raise while the traveller is in flight: the raise leaves the
  old head in the frame the traveller lifts, and the slot sits
  unraised — honestly vacant — until the landing. A pad with two
  raised leaves has two heads, and a queue with two heads is a lie
  about what is next.

### 2.3 The smaller motions, specced so they cannot grow

| Motion | Duration | Curve, in plain words | Must never |
|---|---|---|---|
| **Sub-step strike toggle** (audit item 10) | 120 ms | The strike rule draws across the step's words left-to-right, *at writing speed* — even, unhurried, no acceleration. Un-strike erases in the same 120 ms, right-to-left undone as it was done. | Never strike by fade, grey-flash, or check-pop — a fading step reads as the step *disappearing*, not being done. Never animate the steps below reflowing; a strike changes ink, not layout. |
| **DEL + undo banner** (audit items 3, 15) | Leaf removal: 140 ms collapse of its slot, decelerating. Banner: appears by cut. | The deleted leaf's neighbours close the gap *reluctantly* — slow at first, settled by 140 ms. | Never a tear-like travel for a deletion: DEL is removal, TEAR is completion, and sharing a motion would share a meaning. Never slide the banner in, count it down, or pulse UNDO — the 5,000 ms window is already short for a 1 AM thumb; the banner's motion must not spend or advertise it. |
| **HAND-SPLIT opening blank lines** (audit item 11, fix F-6) | 160 ms | Ruled lines open *downward from the split point*, decelerating, as if the leaf were being ruled further by hand. | Never pop the lines in at once, never push the leaf's verbs down with a bounce; the leaf grows, it is not re-laid-out. |
| **Composer leaf focus** (fix F-2) | 0 ms | A cut. The cursor is simply there on the blank leaf. | Never a scroll animation *to* the foot when he taps to write — if a reach aid is built (the ritual seat flags the long-pile reach as unruled), its motion is a separate ruling, not a default glide smuggled in here. |

---

## 3. The seal's motion — a session materialises

F-1 (plan): at the seal instant, the routine path reads the head
title, writes it as the session's `intention` snapshot, and
`materialiseRoutineSession` makes the session real — while he is
fasted, mid-morning, *not operating the app*. Motion's ruling for the
seal is mostly a ruling of stillness, because the seal's truth is that
**nothing of his moves**: sealing never consumes, completes, promotes,
or dequeues the head task, and writes the intention *string only* —
no `taskId`, no bond (plan rulings 6–7). The motion must be exactly as
honest as the machinery. A motion that shows something travelling,
being claimed, or being celebrated would depict the claim semantics
this cell killed.

### 3.1 On Tasks, if he happens to be looking at the seal instant

- **What moves:** almost nothing, once. The perforation's solid
  overprint — the seal's physical mark (audit item 12) — draws in by
  opacity alone over **150 ms, even and linear**: a stamp's ink
  arriving, not an object travelling. In the same window, nothing
  else on the screen changes position by a single dp.
- **What changes by cut, never by animation:** the leaf's verbs
  (DEL, CUE/START, SPLIT, sub-step toggles) are **absent** — they do
  not fade, shrink, slide, or grey their way out; between one frame
  and the next they are gone, exactly as the tear's absence already
  behaves (audit item 12). The QUEUE header gains its one word —
  `N TASKS · SEALED` (audit item 4) — by cut, in place, no slide-in,
  no typewriter, no colour sweep.
- **What must be perfectly still:** every leaf's position, the scroll
  offset, every title and sub-step (fully legible — the seal removes
  levers, never print), the head's raise, the count itself. If his
  thumb is resting on the pad at the seal instant, the screen under
  it must not shift by a pixel: a surface that rearranges itself
  under a resting thumb at 05:00 teaches him the app moves on its
  own, and an app that moves on its own gets watched, not trusted.
- **Must never:** never a sealing *ceremony* on this screen — no
  sweep, shutter, dimming, tint, or lock-clunk animation. The audit
  already refused the seal a silent darkening; a loud one is worse.
  He did not perform this seal — the routine did, on time — so there
  is no gesture for motion to answer. Theatre here would be the
  machine congratulating itself for keeping an appointment.

### 3.2 On the Deck, receiving the routine session

The Deck is where the materialised session is *met*. Two cases,
because he almost never watches it happen:

- **The ordinary case — he opens the Deck after the seal:** the
  session is simply *there*, at rest, intention already printed
  verbatim. **No arrival motion plays.** Zero ms. A seal he missed
  is never replayed for him on entry: a replayed arrival claims the
  session began when he looked, and the session's whole authority is
  that it began at its hour without him. Catch-up animation is
  a lie about time.
- **The rare case — the Deck is open at the seal instant:** one
  motion only. The session's intention line receives its words by a
  **single 200 ms settle: opacity from 60% to full with a 2 dp
  downward rest** — decelerating, like a printed line being pressed
  onto the page. That is the entire event.
  - **What must be perfectly still through it:** the Deck's time
    numerals — the arrival must not make a single digit jump, recount,
    or re-render as if time itself had been disturbed; the wheel and
    every control keep their exact geometry; nothing slides in from
    any edge, and in particular **nothing arrives from the Tasks
    direction**. The leaf does not fly to the Deck. The leaf is still
    on the pad, on Tasks, un-torn, exactly as F-1 leaves it — only a
    *string*, snapshotted at the seal instant, has been carried.
    Motion that depicts the leaf travelling would picture precisely
    the consumption the plan forbids.
  - **Must never:** never pulse, glow, ring, or badge the arrival;
    never animate the routine's name being *replaced* by the head
    title (no crossfade-morph between `FASTED BLOCK` and his words —
    the sealed session was never titled anything else, and a morph
    would stage a correction that never happened); never hold or
    delay any Deck control until the settle finishes — the settle is
    print drying, not a door opening.

### 3.3 The general seal law

Across both screens: **the seal changes what is *offered*, never
where anything *is*.** Verbs vanish, a word appears, an overprint
arrives, a line of print settles on the Deck — and no object on either
screen changes position by one dp as a consequence of a seal. Position
is the queue's truth (head-ness *is* position); a seal that moved
paper would be a seal that reached into the queue, and reaching into
the queue is exactly what F-1 refuses to do.

---

## 4. Reduced motion — when the system asks for calm

When the system asks for calm — the platform's reduced-motion /
animator-off setting — this screen obeys *completely*, and the
obedience is specified motion by motion so no builder has to guess
which parts were "essential." The principle: **reduced motion removes
travel and theatre; it never removes the fact, the feedback, or the
order of events.** State still changes in the same frame it always
did (booking at the tap, G-01 untouched); only the eye's receipt
changes, from movement to a brief, still acknowledgment.

| Motion | Under reduced motion |
|---|---|
| **Tear** | No travel, no acceleration. The leaf holds at full alpha for **120 ms** with the perforation at its full unified tone — the still image of the cling — then is removed by cut. No fade-out tail: a lone slow fade under reduced motion would resurrect the convicted fade-tear in its purest form. The successor steps into the head slot by cut in the removal frame, honours and `#NN` stepping with it. The tick/haptic (the tear's sound, audit item 10's register) is *not* motion and is not reduced: it remains the confirmation. |
| **Successor rise** | Cut. No 180 ms column shift — the whole queue steps up one slot in one frame. |
| **ADD landing** | The leaf is present in its slot by cut, or by a ≤60 ms opacity step if the platform requires a transition — no 6 dp settle, no descent. Count and `#NN` step in the same frame, as always. |
| **FIRST / CUE promotion** | No lift, no travel, no long-jump elision fade. The reorder happens by cut in one frame: traveller at head, passed leaves stepped down, honours and numbers already correct on the first frame shown. This is the one place a "teleport" is lawful — because the *system*, not the app, has asked that jumps not be staged, and the alternative (slowing a long travel down) is worse than honesty by cut. |
| **Seal (Tasks)** | Overprint appears by cut (no 150 ms draw-in). Verbs and the `SEALED` word were already cuts; unchanged. |
| **Seal (Deck)** | The intention line is simply printed. No 200 ms settle, no 2 dp rest. |
| **Strike / DEL / HAND-SPLIT** | Strike appears drawn in one frame (no 120 ms draw-across). Deleted slot collapses by cut. Split lines are present by cut. |

**Reduced motion must never:**

- **Never make anything slower.** Every reduced duration above is
  *shorter* than its full-motion counterpart. Calm is not slowness;
  a screen that answers a request for calm by lingering is punishing
  the setting.
- **Never drop the perforation's moment in the tear.** The 120 ms
  full-alpha hold is the tear's irreducible minimum — the eye must
  still be shown *which* leaf left and that its cut was the thing
  that let go. Removing the hold too would leave tap → absence, with
  no witnessed separation at all.
- **Never be partial.** If the system asks for calm, *all* of the
  above applies at once — a screen that calms its tear but still
  flies a FIRST promotion has not reduced motion, it has reduced
  *some* motions, and the surviving flight becomes the loudest thing
  on the screen by contrast.
- **Never leak into the facts.** Reduced motion changes no booking
  order, no ghost-window print rule, no seal behaviour. If any
  reduced-motion path alters *when* a fact lands, the path is a bug
  wearing an accessibility setting.

---

## 5. Consolidated motion table (for Builder / Finisher)

| # | Motion | Duration | Curve, in plain words | Hard never |
|---|---|---|---|---|
| M1 | Tear (F-7 respin, Weiss's numbers) | 300 ms, one clock | Slow to let go, fast to leave — accelerating cubic; alpha follows travel, floored at 40%; perforation outside the fading layer | Fade-first; rotate/scale; bounce; pad flinch; ghost past 300 ms |
| M2 | Successor rise after tear | 180 ms from separation | A calm step up — decelerating, whole column as one | Rising before separation; stagger; mid-rise renumbering |
| M3 | ADD landing at foot | 200 ms | Set down, not dropped — decelerating 6 dp settle | Bounce; auto-scroll chase; highlight/announce tint |
| M4 | FIRST / CUE, short jump (≤4) | 280 ms | Hurried middle, careful ends — accelerate out, decelerate in; passed leaves step ahead of traveller | Instant swap; viewport follow; navigation; two raised heads |
| M5 | FIRST / CUE, long jump (>4) | 320 ms cap | Leave the column (80 ms to 40%), collective shift (160 ms), arrive (80 ms from 40%) | Faking the full travel; exceeding the cap for longer queues |
| M6 | Sub-step strike | 120 ms | Drawing at writing speed — even, left-to-right | Fade/grey strike; layout reflow |
| M7 | DEL slot collapse / undo banner | 140 ms / cut | Neighbours close reluctantly — decelerating | Tear's motion reused; banner slide/countdown theatrics |
| M8 | HAND-SPLIT lines | 160 ms | Ruled downward by hand — decelerating growth | Pop-in; bounced re-layout |
| M9 | Seal on Tasks | Overprint 150 ms; all else cut | Ink arriving — even, opacity only | Verb fade-outs; dimming/tint; any positional shift; ceremony |
| M10 | Seal received on Deck | 0 ms if unobserved; else 200 ms | A line pressed onto the page — decelerating 2 dp settle from 60% | Replayed arrival; leaf-to-Deck flight; numeral disturbance; title morph from routine name |
| M11 | Reduced motion (all) | ≤120 ms holds; else cuts | Stillness with one witnessed beat where the fact needs one | Slower than full motion; partial calm; facts re-timed |
| M12 | ZIP-TEAR (next build, noted) | Finger is the clock; ≤120 ms completion past 85% | 1:1 with the finger — zero lag, zero lead | Spring catch-up; booking before 85% crossing |

Nothing in this file requires a schema change or a new surface. It
tunes F-7 as ruled, gives F-2's FIRST and F-4's CUE one shared
promotion motion, holds F-1/F-5's seal to stillness, and prices calm.
Where this spec and the plan disagree on machinery, the plan stands;
the curves above are written to be implementable inside it unchanged.

**DECLARED — motion complete, uncompiled, ready for Amt.**

— Feel-3 · motion (issue #12 cell)

---

## Ready-to-post comment for issue #12 (AMT posts this)

> **Motion — Feel-3, issue #12 cell**
>
> Weiss's baseline is my floor: the tear as built spends 0.878 of its fade by 90 ms, drops 10 dp against a 12 dp gap, and plays the dashes' 260 ms settle inside a layer whose alpha is 0 at 180 ms — a fade in a tear's name, its second half invisible by construction. Full spec: `feel-3.md` in the cell work dir. F-7's respin stands as ruled (30 dp, one 300 ms clock, alpha held to 40%, accelerating cubic, perforation out of the fading layer); what I add is the felt tuning and the rest of the screen's motion.
>
> **The tear: cling, separation, leave.** One 300 ms clock in three phases — 0–~100 ms the leaf clings (3–4 dp only) while the perforation's settle finally plays visibly at full strength; ~100–130 ms is the separation, the one frame that matters; after it, the leaf accelerates through the remaining travel, alpha falling only now and only to the 40% floor, removed by cut at 300 ms because the successor's rise (180 ms, decelerating, whole column, honours transferring at arrival) has already taken the eye. Curve in plain words: *slow to let go, fast to leave* — the convicted curve's exact opposite. Never: fade-first, rotation, overshoot, a flinching pad, a ghost past its clock. Booking stays at the tap throughout. ZIP-TEAR (next build) inherits one line: dash consumption 1:1 with the finger, zero lag, zero lead.
>
> **The queue must show its jumps.** ADD lands at the foot in a 200 ms decelerating 6 dp settle — set down, not dropped — nothing above it moving, no auto-scroll chase, no highlight. FIRST and CUE share one promotion motion (CUE's teleport dies, per the audit): short jumps (≤4 leaves) lift, travel the column in 280 ms — hurried middle, careful ends — with passed leaves stepping down ahead of the traveller so no slot is ever doubled; long jumps cap at 320 ms by honestly eliding the middle (out at 40%, collective shift, back in at the head) rather than faking a commute. Renumbering and the head's raise transfer only in the landing frame, and the old head's raise leaves the frame the traveller lifts — two raised leaves would be two heads, and two heads is a lie about what's next. Never an instant swap, never a viewport follow, never navigation.
>
> **The seal barely moves — that is its truth.** F-1 consumes nothing and carries a string, not the leaf, so the motion may depict nothing travelling or being claimed. On Tasks at the seal instant: the perforation's solid overprint draws in by opacity alone over 150 ms; verbs and the header's `SEALED` word change by cut, never by fade or grey; every leaf, title, and the scroll offset stay pixel-still. On the Deck: if he opens it after the seal, zero motion — a missed seal is never replayed, because its authority is that it began at its hour without him; if the Deck is open, one 200 ms settle as the intention line is pressed onto the page, numerals and geometry perfectly still, nothing arriving from the Tasks direction, and no morph from the routine name — the session was never titled anything else. Across both screens: a seal changes what is *offered*, never where anything *is*.
>
> **Reduced motion: travel dies, facts don't.** Tear becomes a 120 ms full-alpha hold on the perforation, then a cut — no lone fade, which would resurrect the convicted motion in pure form. Promotions reorder by cut (the one lawful teleport, because the system asked for it), landings and settles become cuts or ≤60 ms steps, the seal is already near-still and simply completes. Every reduced duration is shorter than its full-motion counterpart, the reduction is all-or-nothing across the screen, and no reduced path may re-time a booking, a ghost window, or a seal.
>
> **DECLARED** — motion only, uncompiled, nothing run.
>
> — Feel-3 · motion (issue #12 cell)
