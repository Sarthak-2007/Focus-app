# Issue #10 — Receipt (audit fixes) · FEEL SPECIALIST 1 (arrival & feed motion)

**Seat:** Feel Specialist 1, Zettel's cell for GitHub issue #10.
**Item owned:** `studio/plan/AUDIT-receipt.md` item 18 / cure **A7** — the slip's arrival.
**Ruling, verbatim in effect:** KILL the crossfade; the document FEEDS (short,
whole-document slide-feed, no number animates); the detention (E1200–E1800) has
already paid for the arrival; the exit `fadeOut` is dead code — delete it;
restored slips never re-perform the arrival, they render settled.
**Code grounded in:** this clone (`agent/issue-10`): `ReceiptSheet.kt`,
`MainActivity.kt` (host), `MonolithViewModel.kt` (issuance), `ReceiptState.kt`
(Detention), `MonolithCurves.kt` (motion vocabulary). Paper only — nothing
compiled, built, or tested.

---

## 0. The spec in one line

**Feed: 280 ms · Curve A / SEAT `CubicBezierEasing(0.2, 0, 0, 1)` · 56 dp
upward whole-document travel · no alpha anywhere · settle = the curve's own
tail (last 28 ms covers the final 0.2 dp, sub-pixel, zero overshoot).**

---

## 1. What arrival is today (evidence)

`ui/components/ReceiptSheet.kt:60–64` — the whole sheet, scrim and paper
together, is one `AnimatedVisibility`:

```kotlin
AnimatedVisibility(
    visible = true,
    enter = fadeIn(MonolithCurves.specC()),   // tween 280ms, EasingC (0.3, 0, 0.8, 0.15)
    exit  = fadeOut(MonolithCurves.specA())   // tween 260ms, EasingA
) {
    Box( /* scrim: Color.Black @ 0.12, L66–76 */
        Column( /* the paper, L77ff */ )
    )
}
```

Four facts the cure is built on:

1. **It is a fade, and it fades everything.** Alpha animates on the scrim
   *and* the paper in lockstep — a UI card materialising, exactly what item 18
   kills. There is no translation anywhere in the file.
2. **The exit is provably dead.** `visible` is hardcoded `true`; the sheet
   never transitions itself off. The host (`MainActivity.kt:776–785`, this
   clone's numbering) conditionally *composes* the sheet —
   `if (standingReceipt != null && !dismissed && destination == DECK)` — so
   dismissal, navigation, and PARK all remove it from composition outright.
   The `exit = fadeOut(...)` at `ReceiptSheet.kt:63` can never run. The real
   exit law is item 19's (A8's seat), not an animation here.
3. **Restored slips currently re-perform.** The sheet has no `restored`
   branch: a rehydrated slip (`ReceiptState.restored`, contract at
   `ReceiptState.kt:157–167`; installed settled by the VM at
   `MonolithViewModel.kt` `runSliceERecovery`, ~L1096–1103 — *"a published
   receipt re-renders settled (Z6 case b)"*, `receiptPublishedMonoMs = 0`,
   never passed through `emitReceipt`) still runs the 280 ms fade on first
   composition, because `AnimatedVisibility(visible = true)` plays its enter
   on mount. The contract says settled; the sheet says fade. Item 18 rules
   for the contract.
4. **No number animates today, and none may start.** The sheet reads the
   issued fields directly (`ReceiptSheet.kt:91–176`); there is no count-up,
   no per-line stagger, no typewriter. The feed below moves the *document*;
   every printed figure is at its final value from frame 0.

---

## 2. The feed — numbers an implementer can type

### 2.1 The motion

| Property | Value | Notes |
|---|---|---|
| What moves | The paper `Column` only — one `translationY`, whole document | Never per-line, never staggered. The slip is one sheet of paper; its lines cannot arrive separately. |
| Direction | Upward, into its bottom anchor | Starts displaced **down** toward the slot it fed from, travels up, seats flush. Composes with A5/item 17's contact anchor (0 dp at bottom and sides): at rest the bottom edge is in contact; during the feed it is the last thing to land (§2.3). If A5 has not landed yet, the same numbers apply unchanged inside today's 12 dp host padding. |
| Distance | **56 dp**, fixed | Independent of slip height — a 6-line slip and a 20-line deflection slip feed identically. 56 dp is travel you can *see* as feeding (≈3 ledger lines' worth) without becoming a journey: it is 4× the tab transition's 14 dp nudge (`MonolithKin.TAB_TRANSLATE_DP`) and deliberately far short of a full-height emergence, which at this duration would smear. |
| Duration | **280 ms** | From the standing vocabulary (`MonolithCurves.DURATION_280`; also `DRAWER_OPEN_MS` — PARK's rise, the house's one other bottom-anchored paper-adjacent surface, rises 280 SEAT; the slip feeds in the same time signature). ~17 frames at 60 Hz. Short because the detention already paid (§3): anything ≥420 ms (`specB`) reads as a *second* wait stacked on the first. |
| Curve | **Curve A — `MonolithCurves.EasingA` = SEAT, `CubicBezierEasing(0.2f, 0f, 0f, 1f)`** | Already named for a landing: *"travel lands, never overshoots"* (`MonolithKin.EasingSeat`). Its measured profile *is* a roller feed — fast haul, friction stop: 61% of travel by t=70 ms, 88% by t=140 ms, 97.6% by t=210 ms (table §2.3). EasingC (today's fade curve, a slow swell) is wrong for driven paper and leaves this surface entirely. |
| Alpha | **None. Paper at alpha 1.0 on every frame, including frame 0.** | This is the kill, stated positively: no `fadeIn` composed with the slide, no alpha ramp on any descendant, no shadow/elevation ramp (A5's lift-by-shadow is a constant for the whole feed, not an animation). |
| Scale | None, ever | Paper does not grow toward you. |

### 2.2 Reference implementation (Compose, typeable)

Restructure so the animation wraps the paper only — today the scrim lives
inside the animated container (`ReceiptSheet.kt:66–76`) and would be dragged
with it; a translating scrim tears a hard edge across the room. The scrim is
**not** part of the arrival: it is present at its full acquitted 0.12 from
frame 0, unanimated (item 17 keeps 0.12; an alpha-animated scrim is just the
crossfade wearing a trenchcoat).

```kotlin
// ReceiptSheet.kt — imports: delete fadeIn, fadeOut;
// add slideInVertically, EnterTransition/ExitTransition (or None).

val animateArrival = !receipt.restored          // §4 — the only gate
Box( /* scrim Box exactly as today, OUTSIDE the AnimatedVisibility,
        background Black @ 0.12 from frame 0, clickable as today */ ) {
    AnimatedVisibility(
        visible = true,
        enter = if (animateArrival) {
            slideInVertically(
                animationSpec = MonolithCurves.specA(280)  // tween(280, EasingA)
            ) { _ -> with(density) { 56.dp.roundToPx() } } // initialOffsetY = +56dp (below rest)
        } else EnterTransition.None,
        exit = ExitTransition.None               // §5: replaces the dead fadeOut
    ) {
        Column( /* the paper, unchanged */ )
    }
}
```

`slideInVertically`'s `initialOffsetY` is measured against the animated
content's own height in its lambda form — do **not** use the `{ it }` /
full-height form; the fixed 56 dp above is the spec (a density-converted
constant, e.g. hoisted as `val feedPx = with(LocalDensity.current) { 56.dp.roundToPx() }`
outside, is equally lawful). An `Animatable` + `graphicsLayer { translationY }`
implementation that hits the same numbers (280 / Curve A / 56 dp / alpha
untouched / restored = no run) is equally compliant — the numbers bind, the
API does not. If A8's dismissal work restructures this composable, it
inherits these numbers, not the fade's.

### 2.3 The last 10% — the settle

The settle is not a second animation bolted on; it is Curve A's tail, and it
is specified so a reviewer can measure it (progress = fraction of the 56 dp
covered, computed from the bezier):

| Feed time | Elapsed | Travel covered | Remaining |
|---|---|---|---|
| 25% | 70 ms | 34.0 dp (60.7%) | 22.0 dp |
| 50% | 140 ms | 49.2 dp (87.8%) | 6.8 dp |
| 75% | 210 ms | 54.6 dp (97.6%) | 1.4 dp |
| **90%** | **252 ms** | **55.8 dp (99.65%)** | **0.20 dp** |
| 100% | 280 ms | 56.0 dp | 0 dp — locked |

So in the **last 10% (252–280 ms) the paper moves 0.2 dp — sub-pixel.** What
that reads as, and what is required of it:

- **The bottom edge seats.** The visible event of the last 28 ms is not
  travel but *contact*: the slip's foot comes flush against its anchor and
  stops being an object in motion. Velocity reaches exactly 0 at t=280 ms.
- **Zero overshoot, zero bounce, zero rebound.** Curve A never crosses its
  target (it is the SEAT curve, not `MechanicalOvershoot`). Driven paper is
  stiff and roller-held: it cannot spring past its stop and wobble back.
  Any overshoot — even 1–2 dp — turns paper into jelly/card and fails this
  spec on sight.
- **Nothing else changes in the settle window.** No alpha, shadow, scale, or
  content change is timed to land at 90% or 100% — no "pop" on arrival, no
  number resolving, no rule drawing itself. For roughly the final 14 ms the
  frame is already indistinguishable from rest; that dead-still tail *is*
  the settle feel — the machine has finished feeding before you consciously
  register the stop.
- **After t=280 ms the slip is still**, per the binding acquittal
  ("stillness after arrival: no re-strike, no animated numbers, no
  auto-dismiss timer"). The animation does not hold state on the paper:
  settled offset is exactly 0 dp.

---

## 3. Composition with the detention (E1200–E1800)

The feed's clock does not start at E0. It starts at **issuance**, and
issuance is the detention's own formula (`ReceiptState.kt:193–228`,
`Detention`):

```
R0 (rack start)  = max(E1200, P)                       P = pawl-lift offset
receipt t=0      = min(max(E1480, R0 + 280), E1800)    faulted ⇒ E1800 exactly
```

The VM polls and calls `emitReceipt` at that offset (`MonolithViewModel.kt`
`onFinishE0` detention loop; `emitReceipt` samples `receiptPublishedMonoMs`
at the same instant). The sheet composes from that state emission, so:

- **Feed window, absolute:** typical branch (P ≤ E1200 ⇒ R0 = E1200 ⇒
  issuance E1480): feed runs **E1480 → E1760**. Worst lawful branch
  (faulted, E1800): feed runs **E1800 → E2080**. In every branch the slip is
  fully settled ≤280 ms after the machine issues it — the feed adds no time
  *before* issuance and must never be used to delay it.
- **No second detention.** The held frame (E0–E1200), the rack (R0 + 280),
  and the issuance floor (E1480) have already made him wait while the
  machine proved it stopped. The feed is the *result* of that wait arriving,
  not another one: 280 ms is the ceiling, not a target to savour. Do not
  add a pre-feed hold, a start delay (`delayMillis = 0`), or a slower
  "premium" variant for any branch — the faulted slip least of all: its news
  is urgent and it has already waited longest (E1800).
- **No overlap backwards.** The feed never starts before issuance: the
  composition does not exist before `_receiptState` is set, and no part of
  this spec asks the host or VM to pre-compose, pre-warm, or cross-blend
  the slip with the finish machine's held frame. The held frame ends; the
  paper feeds. One thing, then the next — the detention's rhythm, kept.
- **The 2 s dismissal dwell (item 19 / A8) is untouched.** Its clock starts
  at `receiptPublishedMonoMs` (issuance), so the 280 ms feed occupies the
  first 14% of the dwell, inside which taps already pass to their own
  targets. The feed neither extends the dwell nor restarts it at settle,
  and its motion is not an excuse to swallow a tap: input law is A8's,
  this animation intercepts nothing (the scrim's click handling stays
  exactly as A8 rules it).
- **The sled gate is untouched.** `emitReceipt` arms its 1000 ms deadline
  from the same issuance stamp; the feed does not interact with it.
- **Branch-U / discovery receipts** (unwitnessed: VM `onFinishE0`,
  `witnessed = false` — *"the receipt simply stands when he next looks"*,
  and the `publishReceipt(sessionId)` facade): these carry
  `restored = false`, so under §2.2's gate alone they would feed if the
  Deck happens to be composed at discovery. Flagged for the builder, not
  silently expanded: the lawful minimum for A7 is the `restored` gate
  (§4); if Zettel wants discovery receipts settled too, the VM must mark
  them (e.g. route them through the restored flag's semantics) — a VM-side
  change this seat does not smuggle into a sheet-side spec. Until ruled,
  builders implement the `restored` gate exactly and note the branch-U
  case in their proof.

---

## 4. Restored slips — settled, means settled

Gate: **`animateArrival = !receipt.restored`** (`ReceiptState.restored` —
Z6 case b, `ReceiptState.kt:160–167`).

A restored slip (cold start / process rebirth, installed by
`runSliceERecovery` without `emitReceipt`, `receiptPublishedMonoMs = 0`):

- renders at offset **0 dp, alpha 1.0, on its first frame** —
  `EnterTransition.None`; no feed, no fade, no exceptions for "it was only
  backgrounded briefly": the flag, not a stopwatch, decides;
- does not re-feed on recomposition, destination return, or activity
  recreation while it stands (it never passes through an enter transition
  again — §2.2's `visible = true` mount-transition fires once per
  composition birth, and the host gate in `MainActivity.kt:776–785` keeps
  the composition alive across Deck returns; a builder who restructures
  the host must preserve this: restored = born settled, every time);
- is otherwise pixel-identical to a fresh slip — same content, same scrim,
  same dismissal law. Settled is a *motion* property only.

Note the interim-persistence interplay (designer's §3 / A9): a *dismissed*
slip never re-renders at all; an *undismissed* restored slip re-renders —
settled. Both laws hold simultaneously through this gate.

---

## 5. The exit fadeOut — deletion list

Delete, in `ReceiptSheet.kt`:

1. `exit = fadeOut(MonolithCurves.specA())` (L63) — dead code, per §1.2:
   the host unmounts the composition (`MainActivity.kt:776–785`) on every
   dismissal path; `AnimatedVisibility`'s exit never executes. Replace with
   `ExitTransition.None` if the container survives (§2.2), or nothing if
   the builder's structure drops the container for the animated modifier.
2. `enter = fadeIn(MonolithCurves.specC())` (L62) — the killed crossfade.
3. The now-unused imports: `fadeIn` (L4), `fadeOut` (L5). `MonolithCurves`
   stays imported — the feed spends `specA(280)`.
4. The KDoc at L36–53 says the slip is *"settled and still"* — after this
   cure that sentence needs its one honest amendment: it *feeds, then* is
   settled and still; it never re-strikes. (Wording is the builder's;
   leaving the old sentence unamended would make the file lie about
   itself.)

What does **not** replace it: no exit animation of any kind is specified
here. How the slip *leaves* is item 19's law (A8: DOWN gesture, dwell,
faulted-slip explicit act) plus the host's unmount — instantaneous removal
at the dismissal event is lawful and expected. Do not invent a slide-out
to "match" the feed: paper you are done with is torn/filed (group-B
territory), not reverse-printed.

---

## 6. Acceptance checks (paper checks for the builder's proof)

1. **Fresh witnessed finish:** slip's first frame shows the paper whole,
   fully opaque, displaced +56 dp; measured settle by frame ~17 (280 ms);
   no frame shows the paper at partial alpha, and the scrim is at 0.12
   from frame 0 and never moves.
2. **Curve audit:** sampled offsets match §2.3's table within a frame
   (≥97% of travel by 210 ms; ≤0.5 dp remaining at 252 ms; exactly 0 at
   280 ms; no frame with negative offset — overshoot = fail).
3. **Faulted finish:** issuance at E1800, feed identical in every number
   (280 / Curve A / 56 dp). No slower, sadder variant.
4. **Restored slip:** force-stop with a slip standing → relaunch → the
   slip is present, settled, on the first rendered frame; stepping
   frame-by-frame shows no offset and no alpha ramp.
5. **Numbers:** every printed figure identical across all feed frames
   (screenshot frames 0 / mid / settled — same №, same RAN, same lines).
6. **Tall slip:** a long deflection list feeds with the same 56 dp / 280 ms
   — distance does not scale with height.
7. **Dead code gone:** no `fadeIn`/`fadeOut` reference remains in
   `ReceiptSheet.kt`.

## 7. What this spec deliberately does not touch

- Dismissal gesture, dwell mechanics, exit behaviour — A8 / item 19.
- Shape, radius, anchor padding (the feed composes with A5 either way,
  §2.1), rhythm/rules, print content — A5/A6 and the designer's seats.
- Any VM/host timing change: issuance, detention arithmetic, the sled
  gate, and the dwell clock are all read here, never moved. The one
  flagged question (§3, branch-U settled-ness) is Zettel's/Ren's call,
  not a builder improvisation.

— Feel Specialist 1 (arrival & feed motion), Zettel's cell (issue #10).
Paper only; nothing compiled.
