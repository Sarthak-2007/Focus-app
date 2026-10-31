# FEEL 3 — MOTION — THE DRAWER (issue #8)

Feel 3 — Motion (issue #8 cell). Source of law: `studio/plan/AUDIT-drawer.md`
(Lade's verdict), `designer-drawer.md` (the floor — shelf, filing zone,
§3.6 scroll restoration), `innovator-drawer.md` (P2 Ghost Search,
conditional). This file specifies **only how the finished Drawer moves**.
No Kotlin edits. Nobody compiles. No cabinet home is proposed — issue
#21 owns it, parked.

---

## 0 · THE ONE SENTENCE

The Drawer is a heavy wooden drawer in a quiet room, not a stage.
You pull a shelf open, you take a slip out and read it, you put it
back where it was. Nothing in this room hurries you, surprises you,
or asks to be watched. Every duration below is argued against that
sentence. If a motion cannot be justified as *helping his eye stay
oriented while furniture moves*, it is cut — motion that shows off
is grading by theatre, and grading is dead in this room (audit §6).

### 0.1 Global motion laws — binding on every transition below

1. **No bounce. No overshoot. Ever.** No spring with
   `dampingRatio < 1.0`, no `OvershootInterpolator`, no elastic
   settle, no row that lands past its place and comes back. An
   archive does not bounce. Paper in a drawer either rests or it
   slides and stops. Any spring used at all is critically damped
   (`dampingRatio = 1.0`) and tuned to read as a tween.
2. **No pulse, no breathing, no shimmer, no attention loop.** Nothing
   on this screen animates at rest. Not the filing words, not the
   search caret beyond the platform's own blink, not the shelf,
   not EARLIER, not a sealed indicator. A looping animation in the
   rest room is a summons, and the Drawer never summons (audit §6).
   The only motion that exists is motion *he caused, once, that
   then stops*.
3. **Mass, then settle — one character for the whole screen.**
   Every transition below shares one physical character: low
   initial velocity, a long decelerating tail, a stop that is
   final — no second movement, no micro-settle, no fade that
   lingers after the content has arrived. In Compose terms the
   house curve is `FastOutSlowInEasing` /
   `CubicBezierEasing(0.2f, 0f, 0f, 1f)` (decelerate) for enters,
   and `LinearOutSlowInEasing` for exits that share the same mass.
   No per-screen curve inventions. Consistency *is* the feel: his
   hand learns one weight for this drawer and is never re-taught.
4. **Fade carries what translation must not.** This screen prefers
   opacity over movement. A view that can crossfade in place must
   not slide in from an edge, rise from the bottom, or fly across.
   Translation allowances are capped per transition below (8–16dp,
   always vertical, always in the direction of the pull). Horizontal
   slides are forbidden throughout the Drawer: a sideways slide
   reads as *navigation to a sibling app*, and MONTH / DETAIL are
   not siblings — they are the same drawer, deeper in.
5. **His scroll position is sacred.** No transition animates the
   scroll position for him, ever, except a scroll *he* is dragging.
   Restorations (§2) are placed before the first frame, not glided
   to. An animated glide to a restored position steals the exact
   orientation the restoration exists to preserve.
6. **Reduced motion is instant motion.** Honour the system animator
   duration scale. At scale 0, every band below collapses to 0ms —
   a cut, not a faster version of the same move. No transition in
   this file carries information that only exists in the animation;
   cutting it loses nothing. (This is also the test each motion
   had to pass to be specified at all.)
7. **Interruptibility.** Every transition is reversible mid-flight
   by its opposite gesture (BACK during an enter returns from the
   *current* interpolated state, not from the destination). Stock
   Compose `AnimatedContent` / `animate*AsState` behaviour; any
   custom work that breaks it is rejected at review.

### 0.2 Duration bands — why these numbers and no others

| Band | Range | Meaning in this room |
|---|---|---|
| Instant | 0ms | State was already true; printing it is not an event. |
| Ink | 90–150ms | A colour/opacity change on type already on screen. Below ~90ms it flickers; above ~150ms it reads as a deliberate fade he is being made to watch. |
| Settle | 200–300ms | A view replaces a view. Below ~200ms a full-screen change reads as a glitch/flash at 1 AM; above ~300ms he is waiting on furniture. |
| Never | >300ms | Ceremony. No Drawer transition may exceed 300ms for its primary movement. A motion that needs longer to "read" is a motion that should not exist here. |

Perceptual floor, stated once: ~100ms is the threshold under which
a response reads as *caused by his tap*; every press feedback below
starts inside it. The bands are not taste — they are the window
between "the app ignored me / glitched" and "the app is performing
for me." The Drawer lives in that window or it does not move.

### 0.3 What must NEVER animate — the standing list

Applies to every section below; a violation anywhere is a defect,
not a style choice.

- **Counts — because there are none.** No number on this screen may
  count up, tick, roll, or odometer into place. This binds SPENT,
  finish clocks, dates, and any future print. A stored fact appears
  whole, at full ink, or not at all. (If a count ever *did* exist
  here it would already be dead under audit §6; animating one would
  be grading with choreography.)
- **StoredRow content at rest.** Title, category, clock, SPENT never
  fade, shimmer, or highlight-cycle — not on landing load, not when
  a neighbour is tapped, not when returning from DETAIL.
- **Day headings and shelf rows in sequence.** No staggered cascade
  (row 1, then row 2, then row 3…) on any enter. A cascade is a
  countdown he has to sit through and it orders the room by
  animation order instead of by date. All elements of an entering
  view share one fade; nothing is sequenced.
- **The filing words as an ask.** No pulsing violet, no gentle
  scale-breathing, no periodic reminder movement on undeclared
  REST / ILL / FAMILY. See §4.
- **The search line.** No expanding capsule, no growing field, no
  animated underline sweep on focus beyond the 1dp rule taking the
  accent colour as an ink change (§6, Ink band). Focus is a state,
  not an entrance.
- **Hairlines.** Dividers never draw themselves in, wipe, or grow.
  A hairline is either present at full extent or absent.
- **EARLIER.** No bounce at the end of a page load, no spinner
  choreography, no "loading" pulse. See §7.
- **Seal furniture.** No lock, latch, shutter, or dimming sweep
  when the Drawer seals or unseals. See §8.

---

## 1 · LANDING → MONTH — the shelf row opens a month

**The errand.** He taps `SEPTEMBER 2026` on the MONTHS shelf
(Designer §3). What he must feel: *he pulled that month out of the
shelf he is standing in front of.* What he must not feel: that he
navigated to another screen — no page turn, no lateral travel, no
fresh room.

**Spec.**

- **Trigger frame (0–100ms, inside the tap):** the tapped shelf
  row alone takes its ruled press state — `#493EA4` at ~8% wash —
  as an Ink-band fade-in (100ms, decelerate). Nothing else on the
  landing moves or dims yet. This is the handle: the wash confirms
  *which* row his thumb is on before the room changes, and it is
  the visual anchor the next step grows from.
- **The pull (240–300ms, Settle band):** a crossfade with a capped
  vertical drift, in this exact composition:
  - The LANDING fades out fast — 120ms, linear-out — scroll frozen
    at its current offset (the landing does not scroll, slide, or
    shrink during its exit; a moving exit makes the shelf feel
    like it fell away rather than being opened).
  - The MONTH fades in over 240–300ms on the house decelerate
    curve, its content drifting **up by at most 12dp** as it
    arrives — the slip rising out of the shelf slot. 12dp is the
    ceiling, argued: at 0dp the change is a pure crossfade and the
    "pulled from" origin is lost; beyond ~16dp the incoming month
    reads as a sheet sliding up from below (a modal, a different
    gesture with a different meaning). 12dp is one shelf-row's
    worth of suggestion, not a journey.
  - The MONTH's own heading (the month name, under which its
    EARLIER is honestly scoped — audit §2) is present from the
    first frame of the fade at its final position. It does **not**
    morph from the tapped row, does not fly up to the header, and
    does not scale. A shared-element flight of the row's text into
    the heading was considered and **rejected**: it is the single
    most theatrical move available on this screen, it buys no
    orientation the wash + drift doesn't already give, and it
    prices like it (see Pricing). The continuity is carried by
    *name identity* — the same words he tapped are the words at
    the top — not by watching the words travel.
  - The tap wash does not carry into MONTH. The month arrives
    clean, at rest, no highlight residue.
- **What never animates here:** the other shelf rows (no dimming
  chorus while one is chosen), the current-month groups above the
  shelf (frozen under the exit fade), the header — STORED / BACK
  do not slide, swap with animation, or crossfade their labels;
  the header of MONTH is simply *there*, because it never left the
  room.

**Implementation character:** stock Compose `AnimatedContent`
(fadeIn + 12dp `slideInVertically` capped equivalent / fadeOut),
one tween, house curve. No custom layout, no shared-element
framework. **Stock work.**

---

## 2 · MONTH → LANDING — the return lands his eye where he left it

**The errand.** Designer §3.6, binding: returning from MONTH lands
the scroll **at the shelf row he tapped**, not at the top of the
landing — browsing two or three months in a row must not repay
each return with a re-scroll past the current month. Motion's job
here is narrower and harder than §1's: *the restoration itself is
not a motion at all.*

**Spec.**

- **Restoration is placement, not animation (0ms).** The landing's
  `LazyListState` (first visible item = the tapped shelf row, with
  its pre-tap scroll offset within the row) is restored **before
  the landing's first returned frame is composed**. There is no
  scroll glide, no ease-to-position, no "settle into place." A
  glide would show him the current month rushing past — the exact
  re-scroll §3.6 exists to abolish, now performed *at* him instead
  of *by* him. If the eye arrives and the row is already under the
  position his thumb left, orientation is total and free. This is
  the most important motion decision in this file and it is a
  decision to not move.
  - *Edge cases, ruled:* if EARLIER paging grew the landing list
    while he was away (it cannot — paging happens on the landing /
    in MONTH, not in the background), restore by **stable item key**
    (the month's identity), never by raw index — the Critic's S3
    charge stands: index-based restore points at a different row
    after any list change. If the tapped row somehow no longer
    exists (a month cannot empty while he reads it in this
    read-only room, but defensively): land at the shelf heading,
    instantly, no search-scroll.
- **The return fade (200–240ms, Settle band, shorter than §1):**
  returning is lighter work than arriving — he knows this room.
  MONTH fades out in 100ms; LANDING fades in over 200–240ms on the
  house curve with **no vertical drift** (0dp). The asymmetry with
  §1 is deliberate and argued: the 12dp rise *meant* "pulled out
  of the shelf"; on return the slip goes back into a slot he is
  already looking at — any drift would read as the landing itself
  moving, and the landing is the fixed reference frame for the
  whole visit. Stillness on return is what makes the restored
  position believable.
- **The tapped row, on return:** at full ink, no flash, no wash,
  no momentary highlight saying "you were here." A highlight would
  be a marker laid over his own memory; his eye lands on the row
  because it is *where he left it*, not because the app points.
  (If user testing ever shows the return losing him, the only
  permitted addition is holding the §1 tap wash for the duration
  of the return fade — 200ms, then gone. No standing marker.)
- **What never animates here:** the landing's scroll offset (see
  above), the shelf rows around the restored one, the rebuilt
  day groups — the landing returns whole or it does not return;
  no partial re-entry, no groups fading in in date order.

**Implementation character:** `rememberSaveable` / hoisted
`LazyListState` with stable keys for the shelf section; the fade
is stock `AnimatedContent`. The state mechanism is the Builder's
priced work under the audit's saveable-state ruling (Critic S3:
this mechanism does not exist in the current tree — no
`rememberLazyListState` anywhere in `DrawerScreen.kt`). Motion
adds nothing custom on top of it. **Stock animation; the
restoration plumbing is priced in §9.**

---

## 3 · LANDING → DETAIL and back — taking one slip out to read it

**The errand.** DETAIL is one stored task, read-only, BACK its
only verb (acquitted). Reached from LANDING (current month),
from MONTH, or from search results on the landing (Ghost Search,
§6) — the motion is identical from all three, because the act is
identical: he takes one slip out of the pile he is looking at.

**Spec.**

- **Enter (220–280ms, Settle band):** crossfade only, with the
  smallest drift in this file — the incoming DETAIL fades in over
  220–280ms (house decelerate) rising **at most 8dp**; the list
  behind fades out in 120ms, scroll frozen. 8dp, argued against
  §1's 12dp: a whole month is a shelf-slot of material and earns
  the larger suggestion of weight; one slip of paper earns less.
  The differential is how mass is told in this room — by two
  numbers, 12 and 8, never by speed tricks or scale.
  - The tapped StoredRow takes the same 100ms Ink-band press wash
    as §1's shelf row before the fade begins, and nothing else in
    the list reacts.
  - DETAIL's content — provenance block, SPENT standing alone,
    substeps in one voice (Designer §5) — arrives **as one fade**.
    No block-by-block build-up, no substeps typing in, no SPENT
    arriving after the title as if computed. It was always a
    finished record; it prints whole.
- **Back (180–220ms):** the reverse, shorter, **0dp drift**, and —
  the load-bearing rule, same as §2 — the list he returns to is at
  the **exact scroll offset he left**, placed before the first
  frame. From DETAIL-back-to-MONTH just as from DETAIL-back-to-
  LANDING: he returns to where he was standing (Designer §3.6 /
  §5). The row he read is not highlighted on return.
- **System back = header BACK.** One step per press, consumed
  inside the Drawer (audit §2; Critic M1) — and the motion for a
  system-back step is identical to the header-BACK motion for that
  step. Two gestures, one feel; a system back that *cut* where a
  header back *faded* would teach him the cheap door is the fast
  one.
- **What never animates here:** the ESTIMATE / SPENT facts (no
  counting, no slide-apart of the amended stacking — the layout
  Designer §5 rules is the layout at frame one), the substep
  numbering, FORGED print where it appears in any detail (full
  ink, first frame, no stamp-in, no press effect — a stamp
  animation on a forge fact would be the theatre this room exists
  to refuse).

**Implementation character:** stock `AnimatedContent` crossfade,
same rig as §1 with different constants. **Stock work.**

---

## 4 · THE FILING LINE RETIRES — a word is tapped

**The errand.** In the filing zone (Designer §4), he taps ILL on
tomorrow's line at 1 AM. Two taps total and he sleeps. The line
retires from *ask* to *print*: the chosen word stays forever
beside its date in full ink; the other two words leave. The audit's
law underneath: a question that stays standing after it is
answered is a summons — so the leaving must be complete — and
Dens's state-only ruling: what remains is pure print, forever,
read-only.

**Spec — animate the leaving, without ceremony (total ≤200ms).**

- **0–100ms:** the tapped word takes its press wash (accent at ~8%,
  Ink band) — confirmation inside the perceptual floor, because at
  1 AM the tap *is* the whole errand and its receipt cannot lag.
- **100–200ms, one movement, three simultaneous ink changes, no
  travel:**
  1. The **two unchosen words fade to 0** over 120–150ms,
     linear-out. They do not slide away, shrink, collapse toward
     the chosen word, or get struck through. They were an offer;
     the offer is withdrawn. Fading is the whole verb.
  2. The **chosen word's colour animates violet → full ink**
     `#34323B` over the same 120–150ms (colour tween, house curve).
     This is the semantic heart of the retirement and the only
     colour animation in the file that carries meaning: violet
     was *affordance*, ink is *record* (Designer §1: "the moment
     a thing stops being an action, it stops being violet"). His
     eye should be able to watch the word stop being a button and
     become a fact — in 150ms, once, and never back.
  3. The **date and the line itself do not move.** No re-centring
     of the remaining word, no height change of the line, no
     layout shift in the filing zone or the groups below it. The
     space the unchosen words occupied empties in place; if the
     chosen word sits left-of-where-centre was, it stays there.
     A line that tidies itself after filing is furniture fussing
     after he has already turned away to sleep.
- **After 200ms: absolute rest.** The retired line is print. It
  never animates again — not on scroll, not on re-entry to the
  Drawer, not when the *other* line (today/tomorrow) is filed.
  Entering the Drawer with a line already declared shows it
  declared at frame one, exactly as §8's rule for sealed states:
  *a state that was already true does not get an entrance.*
- **What never animates here:** no checkmark draw-on, no stamp, no
  haptic-celebration choreography paired with a visual flourish,
  no success colour (there is no success colour in this room's
  palette subset and green is banned outright), no expansion of
  the line to "show" the filing, no animation on the *other* day's
  line in sympathy. Filing tomorrow must not make today's line so
  much as flicker.
- **Undo:** there is none in the design (the word is filed), so no
  reverse motion is specified. If a correction flow is ever ruled,
  it re-enters as an ask at frame one — it does not reverse-play
  the retirement.

**Implementation character:** two `animateFloatAsState` (alphas) +
one `animateColorAsState`, stock. **Stock work** — but note the
Critic's M3: the tomorrow-filing *plumbing* (a date-carrying
callback through Drawer → MainActivity → ViewModel) does not exist
yet; that is Builder plumbing priced under the audit, not motion
work. Motion's cost here is the three tweens only.

---

## 5 · RESERVED — (no section)

*Numbering keeps Ghost Search at §6, matching the Innovator file's
P2 cross-references in the cell's working notes. This file has no
§5 motion: DETAIL's internal layout amendments (Designer §5) are
static print and were discharged in §3 above.*

---

## 6 · GHOST SEARCH — inking and receding *(only if P2 is adopted)*

**Conditional.** This section binds only if the cell adopts
Innovator P2 (Ghost Search): typing narrows the room he is
standing in — matching rows stand at full ink, non-matching rows
recede to ghosts at hairline strength (~14% ink, category / clock /
SPENT unprinted), the MONTHS shelf narrows the same way by
presence only. If P2 is not adopted and search stays the floor's
modeless list, this section is void and search's view change is a
plain §3-class crossfade (220–280ms, 8dp, no special law).

**The errand, in motion terms.** He is riffling. Each keystroke
re-lights the room. The motion must be *faster than his typing*
and must never move a single row by a single pixel — P2's own
binding constraint, adopted as motion law: **rows must not
re-flow; ink only.**

**Spec.**

- **Per-keystroke transition: 90–130ms, Ink band, opacity and
  colour only.** A row going lit → ghost tweens its title alpha
  100% → ~14% and its meta line (category / clock / SPENT) alpha
  → 0 over 90–130ms, house decelerate. A row going ghost → lit
  reverses over the same band. 90–130ms, argued: his keystrokes
  land every ~150–300ms even one-thumbed; the room must finish
  re-lighting *inside* one keystroke interval or it lags his
  typing and reads as sluggish search. Below 90ms the change
  flickers per keystroke and the room strobes while he types —
  the worst outcome available. The band is the slit between
  strobe and lag.
- **Synchronous, never staggered.** All rows begin their ink
  change on the same frame. A cascade down the list (lit rows
  resolving top-to-bottom) would add up to hundreds of ms of
  theatre per keystroke and would falsely imply the search is
  *proceeding* down the list. The result of a keystroke is a
  state of the room, arrived at all at once.
- **Zero layout motion.** Row heights, heading positions, the
  shelf's place in the scroll, and the scroll offset itself are
  bit-identical before and after every keystroke. Non-matching
  rows keep their full height as ghosts (P2, rejected-collapsing
  ruling, adopted). Nothing is `animateContentSize`'d, nothing is
  `AnimatedVisibility`'d, no item is inserted or removed from the
  lazy list in response to typing — the list's *contents* never
  change under search at all; only per-row ink state changes.
  This is also what makes the motion cheap and the scroll sacred
  (§0.1 law 5) at the same time.
- **The substep reason line** (audit §2: a substep hit prints the
  matched step as a second line on the lit row) is the one
  apparent exception, ruled thus: the reason line lives in a
  **reserved second-line slot** that exists (at meta-ink 0 alpha)
  on every row in the lit-capable rendering while a query is
  active, so its fade-in over the Ink band changes **no height**.
  If the Builder cannot hold the reservation (row internals are
  acquitted construction — Plan, Aug), the fallback is: the reason
  line appears **instantly (0ms)** with the row's lighting. What
  it may never do is push the rows below it down with an animated
  — or any — height change mid-typing.
- **Day headings and shelf rows** obey the same ink law: a
  hit-day's heading holds full ink, a pure-ghost day's heading
  recedes with its rows (title alpha only, heading never moves);
  a shelf month containing a hit holds full ink, one containing
  none recedes to ghost — **presence only, no counts, and the
  boolean arrives as ink, not as motion**: a shelf row does not
  flash when it first contains a hit.
- **Clearing the query** (erase, or BACK which clears — Designer
  §5) restores full ink everywhere over the same 90–130ms,
  scroll untouched, in one synchronous change. He never left the
  landing (P2), so there is no return transition to specify — and
  that absence is the proposal's whole point; motion confirms it
  by having nothing to do.
- **Typing itself:** no animation on the search line beyond the
  caret. No result "pop," no matched-text highlight sweep; if
  matched substrings are ever bolded, they are bold at frame one
  of the keystroke's ink change, not animated into boldness.

**What never animates here:** row positions, row heights, the
scroll, headings' positions, the shelf's structure, and — above
all — no row may *pulse* at the moment it becomes a hit. A hit
is not an achievement. It lights and rests.

**Implementation character:** per-row `animateFloatAsState` /
`animateColorAsState` driven by a lit/ghost boolean; lazy-list
composition unchanged. The shelf's per-month contains-hit boolean
is a new query (Critic M7 — it does not exist today and P2's
printed price understates it); motion's layer on top of it is
**stock**, ~half a day of tuning inside P2's own budget, not
additional to it.

---

## 7 · EARLIER — paging growth at his tap

**The errand.** EARLIER (landing's current month, and inside
MONTH scoped by its heading) grows the loaded set by one page at
his tap and retires at the archive's end (acquitted mechanism,
audit §2/§6). Growth only at his tap means: the room changes
*because he asked*, in the direction he asked, and the change
must not move what he is already reading.

**Spec.**

- **The tap (0–100ms):** EARLIER takes the standard Ink-band press
  wash. Nothing else. No spinner appears, no row dims, the list
  does not acknowledge "loading" as a state with furniture — a
  local page read is fast, and dressing it as a fetch is a lie
  about the machine.
- **The growth (0ms layout, optional ≤120ms ink):** the new page's
  day groups append **below** the current foot; EARLIER re-appears
  beneath them (or is absent, if the archive ended — its
  retirement is also instant, 0ms: a control that fades out
  wistfully at the end of the archive is emoting over a boundary).
  - **Viewport law:** the scroll offset does not change by one
    pixel as a result of the append. The rows he was reading stay
    exactly where they were; the growth happens entirely below
    the fold of his attention. Any implementation whose append
    nudges the viewport (a known lazy-list failure when items are
    keyed poorly) fails this spec even if every duration in it is
    honoured.
  - The appended groups may fade from 60% → 100% ink over at most
    120ms **if and only if** the page load was slow enough that a
    hard appearance would read as a pop — and if they do, they do
    it as one group fade, no stagger (§0.3). On a fast load the
    correct duration is 0ms: content that arrives instantly should
    print instantly. This fade is pop-insurance, not an entrance,
    and it is the first thing cut if it ever reads as one.
- **Repeated taps:** each tap is one page, one growth, same law.
  No acceleration, no "loading faster" feel, no queueing animation
  if he taps twice quickly — the second growth obeys the same
  viewport law from wherever the list then stands.
- **What never animates here:** no end-of-list bounce or overscroll
  glow choreography added by this pass (platform overscroll is the
  platform's, untouched and unextended), no EARLIER label morph
  ("EARLIER → MORE" or similar), no count of what was added — there
  is nothing to count *with*, by law.

**Implementation character:** stock lazy-list append with stable
keys (the same key discipline §2 requires). **Stock work.**

---

## 8 · SEALED-STATE TRANSITIONS — the room under seal

**The errand.** Under seal the Drawer is read-fully furniture:
reading is free, rows are read-only, search is *enabled* (a query
mutates nothing stored — audit §2, resolved), and the filing
words are either tappable or not composed as an affordance at
all — never a painted door (Designer §4's rule, the audit's
sealed-search law generalised). Motion's charge: the seal is a
*legal* state of the room, not a *physical event* in it. Nothing
locks, because nothing here was a door.

**Spec.**

- **Entering the Drawer already sealed (the normal case): 0ms.**
  The sealed Drawer composes sealed at frame one — search line in
  its sealed ruling, filing lines as print or as live words per
  the doctrine Ren/LADE sets, no affordances half-shown. There is
  no "sealing animation" on entry because he did not watch it
  seal; animating a past event at him on entry is theatre with a
  timestamp lie inside it. Same law as §4's already-declared line:
  *a state that was already true does not get an entrance.*
- **Seal engaging while he stands in the Drawer** (a span starts
  elsewhere / the seal state flips mid-visit): the affordance
  changes resolve as **one synchronous Ink-band change,
  120–150ms**:
  - Undeclared filing words, if seal doctrine retires them, fade
    violet → **absent-or-plain per Designer §4** over 120–150ms —
    they do not grey into a disabled costume and stay standing as
    painted doors; the end state is the ruled end state, reached
    by ink, with no height change to the filing line (the date
    does not move; §4's no-travel law holds under seal too).
  - The search line does not change at all if search is enabled
    under seal (the resolved ruling) — *no motion is the correct
    motion*, and any flicker of the field on seal would falsely
    print that something about finding changed.
  - **Stored rows, day headings, the shelf: untouched.** No
    dimming of the archive to "show" it is sealed, no overlay, no
    desaturation sweep. The work under seal reads at the same ink
    as the work unsealed — that is the acquitted promise (sealed
    read-only rows and full reading under seal, §6) and motion
    may not qualify it visually.
- **Unsealing mid-visit:** the exact reverse, same band, same
  synchrony — filing words (where the day is still fileable)
  arrive as violet affordance by ink fade-in over 120–150ms. They
  do not pop, scale, or draw attention beyond taking their colour;
  a man who unseals already knows what he came to do.
- **What never animates here:** no shutter, blind, latch, or lock
  glyph motion of any kind; no screen-wide dim/undim; no seal
  banner sliding in or out; no stored content participating in
  the transition at all. If a seal *indicator* print exists
  elsewhere in the app, it is that room's print — inside the
  Drawer the seal is expressed only by what the filing words and
  the search line truthfully are, changing by ink, once.

**Implementation character:** the same `animate*AsState` ink rig
as §4, driven by the sealed boolean. **Stock work.**

---

## 9 · PRICING — what is stock, what is not

Motion itself is almost entirely stock Compose, deliberately:
every custom millisecond in an archive is a liability, and this
spec was written to need nearly none. Rough prices, motion layer
only (the underlying mechanisms are priced in their own files):

| Piece | Class | Price |
|---|---|---|
| §1 LANDING → MONTH pull (wash + fade + 12dp drift) | Stock `AnimatedContent` | ~40–60 LOC, ½ day incl. tuning |
| §2 return fade (200–240ms, 0dp) | Stock, same rig | included in §1's rig |
| §2 scroll restoration | **Not motion — plumbing.** Hoisted/saveable `LazyListState` + stable shelf keys; does not exist today (Critic S3) | ~80–150 LOC, ½–1 day; belongs to the audit's saveable-state work, motion consumes it |
| §3 DETAIL enter/back | Stock, same rig, different constants | ~½ day with §1 |
| §4 filing retirement (2 alphas + 1 colour tween) | Stock `animate*AsState` | ~30 LOC, hours |
| §6 Ghost Search ink layer (per-row alpha/colour states, synchronous) | Stock animation over P2's rendering | ~½ day tuning **inside** P2's 1.5–2.5 day price (P2's shelf-boolean query is extra per Critic M7, not motion's) |
| §7 EARLIER growth (viewport-stable append) | Stock lazy list, stable keys shared with §2 | hours, with §2's key work |
| §8 sealed transitions | Stock, §4's rig on the sealed boolean | hours |
| **Rejected:** shared-element shelf-row → MONTH heading flight (§1) | Non-stock (`SharedTransitionLayout`, row/heading bounds tracking, interruption edge cases) | ~200–350 LOC, 2–3 days — **not bought**: pays days for theatre the wash + drift already replaces |
| **Rejected:** animated scroll-glide restorations (§2/§3) | Stock to build, priceless to get wrong | **not bought at any price** — it destroys the orientation it claims to deliver |
| **Rejected:** physics/spring system for the Drawer (custom "drawer weight" simulation) | Non-stock | **not bought**: the house decelerate tween at two drift distances (12dp / 8dp) tells all the mass this room has to tell |

**Total motion layer, if every adopted section ships: ~1.5–2
builder-days**, nearly all of it tuning constants against a real
device at 1 AM lighting, not writing animation code. Flag
plainly: the tuning pass is not optional padding — the difference
between 12dp reading as "pulled from the shelf" and reading as
"the screen slipped" is a device-in-hand judgement, and it is
where the days go.

---

## 10 · THE TEST — how LADE checks this spec was honoured

1. Record LANDING → MONTH at ¼ speed: the wash precedes the fade;
   the drift never exceeds 12dp; the heading never travels; the
   landing's scroll does not move during exit.
2. Open three months in a row via the shelf: every return lands
   with the tapped row already at its left position, zero scroll
   glide, zero highlight.
3. File ILL at arm's length, half-asleep standard: tap → wash
   inside a frame or two → by 200ms the line is silent print,
   and nothing anywhere else on the screen moved.
4. Type a 6-letter query under Ghost Search (if adopted): no row
   changes height, order, or position; the room re-lights inside
   each keystroke; clearing restores all ink in one change.
5. Tap EARLIER five times fast in a heavy month: the reading
   position never shifts; EARLIER's retirement at the end is
   instant and unmarked.
6. Flip the seal mid-visit both ways: only filing-word ink (and
   nothing else) changes, inside 150ms; stored rows are
   pixel-identical before and after.
7. Set the system animator scale to 0 and repeat 1–6: every
   errand still completes, every landing position still correct.
   Motion was orientation, never information.

— Feel 3 — Motion (issue #8 cell)
