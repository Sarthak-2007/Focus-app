# MASTER-PLAN §6 — CHROME · MECHANICS PART (Mikro draft for Sana's merge)

*Seat: Mikro — Designers, micro-interactions. This file is the mechanics
half of §6 only: how chrome moves, answers, seats, refuses. Razor's body
(geometry, tokens, header, Guard line form, drawer rows) is the other
half; where a number here is Razor's or the Register's, it is quoted,
not re-derived. Governing: MASTER-PLAN §0 (law + his verdicts),
plan/REGISTER.md (constants — drafted to its values throughout),
plan/GUARD-RULING-AND-DRAWER.md (Guard dissolved; PARK; the Drawer).
My source table: plan/designers-parts/mikro.md §§7–9, §11. Sealed rail:
IMPROVERS-PLAN ladder 15, Rung 3, as instructed. Disagreements are
flagged in §6.M5 and in my letter to Sana — none is forked, none is
silently resolved here.*

*Terminology, one disambiguation the merge must keep: two surfaces are
called "drawer" in the sources. The **PARK sheet** is the Guard
workshop (permissions / sound / theme / spec plate) that rises off the
band's end-detent. The **storage Drawer** is the record face the LEDGER
stop opens (finished tasks, stored permanently, browsed by month). They
share sheet discipline (two rest heights, rows live only at the seat)
and nothing else: different doors, different contents, different sealed
behaviour (§6.M2.4 vs §6.M3.3).*

---

## 6.M0 — The mechanics grammar (applies to every part below)

The table's six states, unchanged: **REST · DOWN · TRAVELLING ·
ARRIVED · REFUSED · ARMED**. Three laws ride on all of §6:

1. **Answer on DOWN.** Every chrome press is acknowledged the same
   frame (≤16ms), in-frame, by ink or tone — never by scale, never by
   ripple (dead), never after the finger lifts.
2. **Words flip on arrival, never mid-flight.** A travelling cap
   carries its old word at its old ink for the whole travel. The word,
   the state fill, and the seat feel land together on the ARRIVED
   frame — the first frame within max(1% travel, 0.5dp) of the target.
3. **One mover.** While the cap travels, nothing else in chrome moves
   in response to the same input. Content is not a mover: it starts
   on the seat frame (§6.M1.4), never mid-flight.

Timing pointers (Kin's names, not redefined): **KIN-NAV** — 180ms +
60ms per stop crossed, critically damped, **zero overshoot** past the
target stop (Register). **KIN-TAB-IN** — content behind the cap.
**KIN-DRAWER** — 280ms CurveA sheet travel. **KIN-SNAP / KIN-RETURN**
— sheet seats and returns. Haptic sizes (Ton's): 1ms detent crossed ·
4ms contact / minor commit · 8ms throw arrived — navigation is never
a throw, so **8ms is never spent in chrome**.

---

## 6.M1 — THE BAND SWITCH as a mechanism

**Stops (operative, Register): DECK · SHIELD · TASKS · LEDGER + PARK.**
Four stops + the utility end-detent (§6.M2). Segment **85.5dp**, cap
**77.5dp** wide × 44dp tall, radius 8dp, inside the 52dp rail's
342 × 44dp usable track (Razor's body figures, Register-confirmed —
but see FLAG F-A: the PARK subtraction does not close against these
numbers). Stop words are printed on the recess (Anonymous Pro 700
10sp, +2.0); the cap travels *under* the selected word and re-inks it.

### 6.M1.1 Touch / visual split — the cap is visual only

The **touch target is the full segment × the rail's full 52dp** for
every stop (85.5 × 52dp operative). The cap (77.5 × 44dp) is a visual
object inside that target; it is never the hit area. A press anywhere
in the segment — above the cap, below it, in the segment's corners —
is a press on that stop. This split is what answers the 48dp floor
for chrome: the target meets it, the cap doesn't have to. PARK's
touch is its own zone (§6.M2.1), never a stop's spill-over.

### 6.M1.2 DOWN — the word answers, the cap does not depart

- On DOWN on an **unselected** stop: that stop's word inks
  **ink-55 → ink-100 the same frame**. The press is answered by the
  word, in-frame, before the cap moves a pixel. No cap motion, no
  scale, no ripple.
- On DOWN on the **already-selected** stop: the word is already
  ink-100, so the answer is the cap's bottom band stepping one tone
  down for the press duration (the chrome equivalent of a dip — the
  cap cannot translate inside a flush rail without breaking the
  recess line). Release restores it. Nothing else fires: a press on
  the seated stop is a null press — no callback, no re-travel.
- The cap **departs on release (tap) or follows the finger (drag)**
  — never on DOWN. DOWN claims; it does not commit.

### 6.M1.3 Travel — tap and drag, one law

- **Tap travel (powered):** KIN-NAV, critically damped, zero
  overshoot: adjacent stop **240ms** (180 + 60 × 1); DECK → LEDGER
  end-to-end **360ms** (180 + 60 × 3). A cap that bounces past a
  printed word has lied about where it stopped — overshoot is a
  build error here, not a style (see FLAG F-D on Band's Rung-4
  overshoot).
- **Pass-through:** on a powered travel or a drag, each intermediate
  stop centre crossed fires one **1ms detent tick** — never doubled
  with the seat feel.
- **Drag:** DOWN on the cap (or anywhere in the current segment)
  grabs it with its offset preserved — the cap never re-centres
  under the finger. MOVE is **1:1**, no magnet, no rubber band
  inside the stop field (FLAG F-D). Detent tick 1ms at each stop
  centre crossed. On release the cap seats to the **nearest stop**
  under KIN-NAV scaled to the remaining distance; a drag ending
  between stops never leaves the cap parked between words.
- **Words during travel:** the departing word stays ink-100 until
  the cap has fully left its segment; the target word stays ink-55
  on the recess until the ARRIVED frame. There is no progress-midpoint
  ink anywhere in the rail.

### 6.M1.4 ARRIVED — word, content, feel land together

On the seat frame, together: (1) the target word re-inks to
cap-content tone at **ink-100**; (2) the origin word drops to
**ink-55** on the recess (it is printed on the rail again); (3)
**content starts — on this frame, never earlier**: KIN-TAB-IN,
outgoing fade 120ms linear, incoming translate 14dp in stop order
(direction = travel direction) + fade, 240ms CurveA (FLAG F-E on
Razor's single 120ms figure); (4) seat feel **4ms**. For the whole
travel before this frame, the old screen is simply still there.

### 6.M1.5 The LEDGER stop is an ordinary stop

The storage Drawer is reached by exactly this mechanism and no
other: DOWN inks, release/drag travels, seat frame starts the
Drawer content. It carries no badge, count, dot or summons (Guard
ruling, acceptance 7). Its specialness is entirely in its sealed
behaviour (§6.M3.3) and its contents — Razor's body and the ruling
own those; its *motion* is the band's.

---

## 6.M2 — PARK — the end-detent in full

PARK is a **utility end-detent, not a destination**: a position past
the last stop, holding no content of its own. What lives there is
the PARK sheet (Guard workshop). **Arrival IS invocation** — there
is no second tap, no handle tap, no confirm.

### 6.M2.1 Rest form

Inside the track, after LEDGER: a **44dp zone + 4dp notch**
(Register; flat **48dp** cost under every variant). The zone reads
as rail: recess fill, a 1dp end-wall line at the track's end, a 1dp
score line at the zone's inner edge (Razor's body), and the **4dp
notch** — a short vertical grip line centred in the zone, resting
at **ink-35**. No segment rhythm. (The notch is my §8 mark at the
Register's 4dp, superseding my filed 6dp. Whether a `PARK` word
also prints in the zone is FLAG F-B: Razor prints one, I print
none — unresolvable from my seat.)

### 6.M2.2 Approach — resistance and the notch's ink

Dragging the cap past LEDGER toward PARK, the same 1:1 law holds —
with one change of feel: **rising resistance in the last 12dp** of
the 48dp leg (Kin's resistance map, gentle class — the finger still
wins; the hand feels a detent *approach*, not another stop). As the
cap crosses the zone's inner edge (the score line), the **notch
inks ink-35 → ink-70** — approach is announced on the notch, the
only mark in the zone. One detent tick fires at zone entry: **1ms,
in a lower pitch family than stop ticks** — PARK is not a stop and
must not sound like one (the exact pitch is Ton's to assign; my
flag F6 to him stands open).

A tap is priced identically: DOWN on the PARK zone answers by the
notch stepping ink-35 → ink-70 the same frame (the zone's word-ink
equivalent — there is no word to ink); on release the cap runs the
48dp leg powered, under the §6.M1.3 formula counted with PARK as
one step, and invocation waits for the seat — a tap never opens
the sheet mid-glide.

### 6.M2.3 Seat — arrival IS invocation

The cap travels **48dp past LEDGER's centre** and seats against the
end-wall: seat feel **4ms, duller than a stop seat** (a parked feel,
not a selected feel). **On this seat frame the PARK sheet begins
its rise** (KIN-DRAWER, §6.M2.4). The cap **stays parked** in the
zone for as long as the sheet is open — the sheet is visibly the
cap's cargo. No stop word changes state: LEDGER does not ink, no
stop claims selection; position alone says where the cap is.

Other invocation routes, all ending at the same seat frame (the
seat is the invocation, whatever the input): a **tap on the chrome
Guard line while unsealed** sends the cap to PARK under the same
travel law; **first run** opens PARK directly (setup walks the
permissions there — no detent gesture is charged for it).

### 6.M2.4 The PARK sheet — two rest heights, rows live at the seat

- **Exactly two rest heights: closed and open.** No half-detents,
  no peek height. Invocation travel: the sheet rises under
  **KIN-DRAWER — 280ms CurveA**, inner rows visible from frame one,
  no stagger, no fade-in of rows.
- **Handle law:** DOWN on the open sheet's grab strip — the strip's
  centre grip line steps ink-55 → ink-100 and widens 24 → 28dp the
  same frame; the sheet's top 8dp band drops one tone step (the
  sheet takes weight where the hand is). No sheet movement on DOWN.
  Drag: the sheet follows **1:1** vertically, clipped. Release
  **above 40%** of open height seats **open**; **below 40%** seats
  **closed** — both under KIN-SNAP scaled to the remaining travel.
  (Grab-strip height: Razor's body builds 32dp; my table filed a
  24dp grab zone — FLAG F-F, body's dimension vs my zone.)
- **Open seat frame:** grip line back to 24dp / ink-70, top band
  tone restores, feel **4ms**. **The sheet's rows are operable only
  from this frame** — a row tapped mid-rise is not answered; its
  DOWN law starts at ARRIVED.
- **Closed seat frame:** the notch returns to ink-35, the cap is
  freed (it may already be travelling — §6.M2.5).

### 6.M2.5 Leaving PARK = dismissing the sheet — one gesture

There is no "close the drawer, then navigate" sequence. Exactly
three inputs, each a single gesture that does both:

1. **A stop press / cap drag out:** the sheet dismisses and the cap
   departs **on the same input** — sheet exit and cap travel may
   overlap; they are one mechanism (the cap leaving its detent is
   what lets the sheet fall). The cap seats on the chosen stop
   under §6.M1 law.
2. **Drag the sheet down past 40%** → it seats closed (KIN-RETURN
   if dragged); the cap returns to the stop it came from.
3. **Tap on the scrim / content above the sheet**, or **tap PARK
   again** → the sheet seats closed over a **160ms exit** (leaving
   is faster than arriving); the cap travels home to the current
   stop under the §6.M1.3 formula counted from PARK.

### 6.M2.6 PARK REFUSED

PARK does not open while sealed (§6.M3). There is no closed-sheet
tease state, no greyed zone: mid-session the zone is simply inert,
like the rest of the rail.

---

## 6.M3 — THE SEALED RAIL — chrome's HOLD mechanics

*(Improvers ladder 15, Rung 3 — Band's seal, stated as this
section's law per brief. Geometry of the pin rides Razor's cap.)*

### 6.M3.1 Engage — pin, dim, in place

On the session-seal event (t=0 is §2.6's frame; the pin is chrome's
answer to it, charged after it — the seal never taxes ARM):

- A **locking pin drops through the cap**: a 3dp-wide bar in
  primary ink, drawn over the cap across the rail, extending 14dp
  above and below the cap's centre line at the cap's centre — a
  bolt through a switch. Drop: **180ms CurveA**.
- Simultaneously the printed scale **dims**: the words and numerals
  of the **unselected stops drop to 45%**; the selected stop's word
  stays at full ink — where he is remains legible; where he could
  go stops advertising. No text is added. No colour is spent —
  **zero orange in the sealed rail, as in every rail state** (the
  orange armed lamp of my filed §7 and Razor's §0.4 is dead: Ren's
  §0 ruling refuses the R13 exception; armed reads by form — the
  pin is that form).
- The cap is at DECK when the seal engages (arming is a Deck act);
  the pin drops through it there. Chrome's frame does not move —
  geometry never jumps; the hold state is a content swap inside
  the same rail (Designers §8.4 invariance).

### 6.M3.2 Swallow — every press, one dull answer, nothing else

While sealed, **all touch on the rail is swallowed**:

- DOWN anywhere on the rail returns **exactly one dull
  acknowledgement — the 4L latch feel, throttled to once per
  press**, no repeat on hold. No ripple, no ink change, no cap
  movement, no drag, no callback, no navigation. TalkBack reads
  the state once: *"Navigation sealed — session running."*
- There is **no in-band exit**: no long-press, no secret gesture,
  no PARK (§6.M2.6), no drawer. A sealed band that can be talked
  out of sealing is not sealed. The emergency break lives on the
  Deck under the banked-hold law; chrome neither duplicates it
  nor advertises it.
- The chrome **Guard line is inert and frozen** while sealed: it
  answers nothing, and per his death verdicts (§0) it does not
  change if blocking dies mid-session — no knock, no word, no
  screen change; the finish receipt tells the truth once.

### 6.M3.3 The storage Drawer while sealed

The Drawer is **read-only furniture** while sealed (Guard ruling):
it can be read; search input, reopening and every edit are inert
until the session ends. (How he *reaches* it mid-session against
§6.M3.2's swallow is FLAG F-C — the sources collide; I do not
resolve it.)

### 6.M3.4 Release — only the session ends it

Release comes only on session end, never from the rail itself:
the pin **lifts 180ms CurveA**, the dimmed scale **restores over
260ms CurveA**, and §6.M1 law resumes from the next press. Chrome
unlocks on session end — not at receipt dismissal, not at any
gesture of his inside the session.

---

## 6.M4 — THE RECEIPT — what survives (one line, as briefed)

My §11 receipt motion is superseded as a record form by the
Drawer; what survives of its motion grammar into the Drawer/PARK
world is this: **seat-before-rise** (the presenting sheet never
races the cap — cap home and chrome unlocked before anything
rises), a **240ms CurveA rise with no overshoot**, **one-gesture
dismissal** (down-drag or tap, 160ms exit) that **changes no
chrome state**, and **once-only presentation keyed by sessionId,
unreplayable even by process death** — the PARK sheet inherits
the rise-and-dismiss discipline (at KIN-DRAWER's 280ms), the
storage Drawer inherits the once-told truth, and the receipt
itself keeps no copy and no longer carries the record anywhere.

---

## 6.M5 — FLAGS (carried to Sana's letter; nothing below is forked)

- **F-A · Rail arithmetic does not close.** Register: segment
  85.5dp / cap 77.5dp are operative *and* PARK costs a flat 48dp.
  On Razor's 342dp usable track, 4 × 85.5 = 342 leaves 0dp for
  PARK; Razor's subtraction gives segment 73.5dp / cap 65.5dp.
  I drafted to the Register's headline values as instructed; the
  subtraction conflict is Sana/Ren's to close.
- **F-B · PARK word.** Razor prints `PARK` (plate, 55%) in the
  zone; my §8 prints no word, notch only. Register names the notch
  (4dp — adopted over my 6dp) and is silent on the word.
- **F-C · Sealed navigation.** MASTER §§0/2.1/3.1 say navigation
  and the drawer are "read-only" while sealed; Band Rung 3 +
  my §7 + Razor §0.7 + Designers §8.4 say the rail locks and
  swallows. §6.M3 drafts the lock (per brief); if read-only
  travel is the law, §6.M3.2 and the Drawer's sealed reachability
  both change. Ren's wording call.
- **F-D · Drag dressing + overshoot.** Band Rung 3/4: magnetised
  grooves (×0.55 within 12dp), rubber-band ends (×0.25, ≤9dp),
  MechanicalOvershoot into the middle stop. My §7 + Razor +
  Register: 1:1, nearest-stop seat, zero overshoot. Drafted to
  the latter; Band's dressing is not in this draft.
- **F-E · Content timing.** Razor: one 120ms crossfade. My
  KIN-TAB-IN: outgoing 120ms + incoming 240ms/14dp. Drafted
  mine; the merge should print one figure.
- **F-F · Grab strip.** Razor's sheet grab strip is 32dp tall;
  my handle law was filed against a 24dp grab zone. Behaviour
  above is height-agnostic; one dimension must win in print.
- **F-G · Sealed acknowledgement.** My §7 REFUSED and Razor
  §0.7 give total stillness (no feel at all); Band Rung 3 gives
  one dull latch per press. §6.M3.2 drafts Band's, per brief —
  my own filed line is the loser and I record it as such.
- **F-H · KIN-SHEET (my old F1).** Still no Kin curve by that
  name. In the Drawer/PARK world the debt shrinks: the PARK
  sheet runs on the settled KIN-DRAWER; only the §5 receipt's
  240ms interim pointer still waits on Kin.

---

## 6.M6 — ACCEPTANCE CHECKS (mechanics only)

1. DOWN on any stop inks its word 55→100 on that frame, cap
   unmoved; screenshot at DOWN+16ms shows ink, no travel.
2. No content pixel of the incoming screen appears before the
   cap's seat frame, on tap, drag, or PARK-return travel.
3. Touch test: taps in the segment's corners (outside the drawn
   cap, inside the segment × 52dp) select the stop; taps never
   need to touch the cap.
4. Drag released exactly between two stops seats to the nearest;
   the cap is never observed parked between words in any frame.
5. PARK: resistance is felt only in the last 12dp of the 48dp
   leg; the notch is at ink-70 before the seat frame and ink-35
   at rest; the sheet's first rise frame is the cap's seat frame,
   never earlier; cap remains in the zone while the sheet is open.
6. The sheet has exactly two rest heights under every input
   sequence (drag, fling, scrim tap, stop press); a row tapped
   mid-rise receives no DOWN answer.
7. Leaving PARK by stop press: sheet exit and cap travel begin
   on the same input — no frame shows the sheet closed with the
   cap still parked, nor the cap travelling with the sheet open.
8. Sealed: ten presses on the rail produce ten dull latches,
   zero navigations, zero cap movement, zero ink changes; PARK
   cannot be invoked; the Guard line does not change when a
   permission dies mid-session.
9. Session end: pin lifts and scale restores on the §6.M3.4
   timings; the first post-session press behaves under §6.M1
   with no residue of the swallow.

— Mikro, Designers (micro-interactions), for Sana — mechanics
half of §6; Razor's body is the other half.
