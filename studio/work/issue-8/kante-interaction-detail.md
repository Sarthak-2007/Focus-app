# KANTE — INTERACTION DETAIL — #8 DRAWER: THE MONTHS SHELF + THE CABINET'S DEPARTURE
## Kante, interaction detail (dedicated-100 seat). Upgrades feel-1/2/3 + finisher-3 where they are silent; where they speak, this file does not contradict them.
## Status: SPEC ONLY — no Kotlin edited, nothing compiled (Ren compiles, on Sarthak's word).

Grounding read before writing: AUDIT-drawer.md (Lade), designer-drawer.md,
feel-1-touch.md (TICK `[0,7,5]ms/[0,150,60]`, detent, thud-stamp — adopted, not
re-specified), feel-2-sound.md (silence + one filing sound — adopted),
feel-3-motion.md (bands: Ink 90–150ms, Settle 200–300ms, no overshoot — adopted),
finisher-3-search-shelf.md, and DrawerScreen.kt as built (stock `.clickable`,
zero haptics, shelf rows at :639-645 plain clickable, no custom fling).

The gaps this file closes: (1) nobody specified the SHELF'S SCROLL WEIGHT —
the one continuous physical act on this screen; (2) nobody specified shelf-row
PRESS TRAVEL / 3D treatment — rows are flat clickables today; (3) nobody
specified the CABINET'S DEPARTURE as an interaction (the row dies — how?).

---

## 1 · SHELF SCROLL WEIGHT — the drawer is heavy, the shelf is heaviest

The landing is one scroll (current month, then MONTHS shelf). One scroll must
not have one weight: the shelf section is older, deeper, less-visited paper.

- **Fling decay, landing list:** custom `FlingBehavior` on the landing
  LazyColumn — `exponentialDecay(frictionMultiplier = 1.45f)` (Compose default
  is 1.0f). Effect, testable: a 2,400 dp/s release travels ~38% less far than
  stock and stops without a glide tail. Current-month rows feel handled;
  nothing sails past a month he meant to read.
- **Fling decay, MONTH view:** `frictionMultiplier = 1.25f` — inside a month he
  is scanning days, slightly lighter than the shelf corridor, still heavier
  than stock. Two weights only; a third is a dialect.
- **Velocity floor:** flings below 180 dp/s are treated as drags (no decay
  phase) — a slow thumb nudge moves exactly what it pushed, 0dp of carry.
- **Overscroll:** 0dp stretch, 0dp glow, both edges, both lists. Resistance
  only: drag past the edge moves content at 0.25× finger speed for a maximum
  of 12dp, releasing returns it in 140ms on `CubicBezierEasing(0.2,0,0,1)`
  (feel-3's house decelerate). Paper in a drawer does not stretch; it resists
  and stops. No overscroll haptic at either edge — rest is silent (feel-1 §0).
- **Scroll stop:** no settle bounce, no 1-frame snap. When decay velocity
  falls below 12 dp/s the list is AT REST in that same frame — no easing-out
  of the last pixels (that tail is what makes lists feel like jelly).
- **Scrollbar:** none. No thumb, no track, no fade-out sliver. A scrollbar is
  a progress meter — a count by position — and this room prints no counts.

## 2 · SHELF ROW PRESS — a label you pull, not a button you push

A MONTHS shelf row (month + year, no counts — finisher-3 §4 stands) is a
drawer label holder, brass-and-card furniture. Its press is a PULL-PREVIEW,
not a button dip:

- **Travel: 0dp translation of the row itself.** The row never sinks, scales,
  or slides. What moves is light and the rule under it (below). A moving row
  would shift the shelf he is aiming at; furniture labels do not duck.
- **3D / light treatment (the press, in order):**
  1. t=0–90ms (Ink band): the ruled `#493EA4` violet wash (the press ink
     feel-1 already allows, ~8%) fades in over the row's full height (44dp
     min touch height stands), easing `CubicBezierEasing(0.2,0,0,1)`.
  2. t=0–90ms, same tween: the row's 1dp bottom rule darkens from its rest
     ink to full `onBackground` ink — the label holder's lower edge catching
     light as the finger's shadow falls. Top rule does not change. One edge
     only: two edges changing reads as a box, and a box reads as a button.
  3. Release/cancel: both reverse in 160ms, same curve. Cancel (drag-off,
     scroll-away) reverses identically and fires nothing (feel-1: cancel is
     silent — adopted).
- **Commit point + haptic:** on release-inside-target only, the feel-1 DETENT
  fires (already specified there — catch-then-seat; do not add a second
  voice). No haptic on touch-down: a scroll drag starts on these rows
  (feel-1 §0 corollary — adopted and binding here).
- **Opening the month (the pull completes):** MONTH view crossfades in place
  per feel-3 §0.4, with the ONE translation this file adds: the incoming
  month's content rises 8dp→0dp over 240ms (Settle band, house decelerate) —
  the direction of a pull is up-and-out of the shelf, and 8dp is the largest
  rise that still reads as orientation, not travel. The shelf itself does not
  slide away; it is crossfaded out under the rise. BACK reverses from the
  current interpolated state (feel-3 §0.7 — adopted).
- **Long-press on a shelf row:** nothing. No menu, no preview pop, no haptic.
  A shelf label has no second function; inventing one invents a summons.

## 3 · THE SEARCH ENTRY LINE — the one press feel-3/feel-1 left open

Finisher-3 finished its geometry (44dp, 1dp rule, one costume). Its feel:

- **Focus:** tapping the line fires NO haptic and NO sound (feel-2 event 6 —
  adopted). The only change is the caret (platform blink, unmodified) and the
  1dp rule shifting to full ink over 120ms (Ink band). The rule is the focus
  ring; there is no second outline, ever.
- **The CLEAR affordance** (DrawerScreen.kt:444, erases the query): press =
  wash only, 90ms in / 160ms out, 0dp travel, TICK on commit (it is a reading
  act — it returns him to the shelf). Erasing must never feel like deleting:
  no thud, no stamp voice, no red, no shake.

## 4 · THE CABINET'S DEPARTURE — an eviction, performed at furniture speed

The CURIOS row + CABINET leave the Drawer (audit §2A, destination is #21's,
parked — not proposed here). The departure itself is an interaction and must
not read as a loss, a feature-cut apology, or a ceremony:

- **No farewell animation exists.** When the build that evicts it ships, the
  row is simply ABSENT on first open after update. No collapse animation
  (there is no session in which he watches it leave — animating a removal he
  didn't perform is theatre about the app, not for him), no "moved" banner,
  no toast, no one-time notice line. 0ms, 0 frames, 0 print.
- **Reflow:** the landing's remaining sections occupy their final positions
  from the first frame of that build — no layout may be shipped that first
  renders the row and then removes it (no `LaunchedEffect` eviction, no
  async gap). The shelf rises into the freed space by never having left it.
- **Search honesty during departure:** curios were never search results in
  the Drawer (search finds tasks); if any interim build has curios reachable
  through Drawer search, the eviction build removes them from results in the
  SAME build — a result that opens a dead room is worse than an absent row.
- **If #21 later gives the cabinet a home:** the entry to it, wherever it
  lands, gets the shelf-row treatment of §2 verbatim (0dp travel, wash +
  lower-rule light, detent on commit). One furniture language for every
  room-change in this app's quiet half. That sentence is the whole handover.

## 5 · FILING WORDS — one delta on feel-1's thud-stamp

Adopted whole, with the press that precedes it (feel-1 specified the haptic,
not the travel): REST / ILL / FAMILY are the only words in the room that
WRITE, so they are the only targets with travel — **1dp down over 90ms**
(`MonolithKin.PRESS_MS` seat curve), hold while pressed, **160ms return** on
release, commit on release-inside (a drag-off must be able to un-file an
unreleased word — at 01:00, a mis-tap he can slide off is mercy). The thud-
stamp haptic + feel-2's one sound fire at commit, from the same call site as
the write (feel-1 §0 — adopted). Disabled-under-seal words: no wash, no
travel, no haptic — inert print, not a painted door (audit §2 law).

## 6 · ACCEPTANCE (phone-checkable, no numbers in the app itself)
1. Hard fling on landing stops ~⅓ sooner than stock Compose; slow nudge = 0 carry.
2. Shelf-row press: row visibly does not move; wash + lower rule only; detent on release only.
3. Pull a month: content rises exactly once, ≤8dp, ≤240ms; nothing loops at rest for 60s.
4. Post-eviction build: first open shows no cabinet trace, no notice, no layout jump.
5. Filing word slides off uncommitted = nothing filed, nothing felt, nothing heard.

— Kante, interaction detail
