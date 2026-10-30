# ISSUE #6 — FINISHER-4 SPEC: F19 · F20 · F21 (gallery standard)

FINISHER-4, Rost's cell, issue #6 (Deck audit fixes), branch
`agent/issue-6`. Paper-verified against the tree as read this shift:
`TactileDeckScreen.kt` (1,148 lines), `MainActivity.kt` (821),
`ReceiptSheet.kt`, `MonolithRailView.kt`,
`MonolithCustomComponents.kt` (strip + `endsAtText`),
`MonolithCurves.kt` / `MonolithKin`. Source rulings:
`studio/plan/AUDIT-deck.md` §5 (charges 5 + 8), §9 (F19/F20/F21);
cross-checked against `studio/plan/AUDIT-receipt.md` items 17–19
(Zettel's cell) — one tension found, flagged in §4, not silently
resolved. Nothing compiled, nothing committed — DECLARED only.

**Merge-order note (read first).** F19–F21 are independent of the
restructure and land on the current room (audit §9 sequencing).
Every replacement text below is true of the tree AS IT STANDS.
Where another F-item rewrites the same surface later (F2 kills the
FLOW cap, F6 the chips, F10 the catch ghost, F15 the vertigo, F16
the standing strip), that item's builder owns the final wording of
its own block; F21's texts are the floor those lanes start from,
never a text they must preserve past their own change.

---

## §1 · F19 — RECEIPT DISMISSAL: SLIP-SWIPE OR PARK/RAIL ONLY (S)

### 1.1 · The state of the code, charged

- `ReceiptSheet.kt`: the scrim `Box` carries
  `.clickable(onClick = onDismiss)` over the full frame — ANY tap
  that reaches the scrim dismisses, on UP. The slip `Column`
  carries `.clickable(enabled = false) {}` — a disabled clickable
  installs no gesture detector, so taps on the paper fall through
  to the scrim behind it. Net behaviour, exactly as Zettel's audit
  (item 19) charged: any tap anywhere swats the ending, and the
  tapped thing's own action never fires.
- Host (`MainActivity.kt`): dismissal state is
  `dismissedReceiptSessionId` (line ~263). The lawful doors that
  already exist and MUST keep working: the `LaunchedEffect(
  currentDestination, parkOpen)` beside it dismisses on any
  navigation away from the Deck and on PARK's invocation; the
  rail's `onTabSelected` and PARK's invocation run through that
  same effect. No timer dismisses. System Back has no handler on
  the Deck while the slip stands.
- The slip's content scrolls (`verticalScroll` on the slip
  `Column`) — the dismissal swipe has to coexist with that scroll;
  §1.3 rules how.

### 1.2 · The exact gesture set — exhaustive

| Input | Result |
|---|---|
| Downward swipe ON THE SLIP, released past threshold (§1.3) | **DISMISSES** — the slip's own door |
| Rail stop tap or drag-seat (any of the 4 stops) | **DISMISSES** — host door, via navigation effect |
| PARK invoked (rail zone, or Guard-line tap opening PARK) | **DISMISSES** — host door, via `parkOpen` effect |
| Tap on the scrim (DOWN+UP within touch slop) | **SHAKE** (§1.4) — never dismisses, at any count, at any age of the slip |
| Tap on the slip | Nothing. The paper is read, not pressed. No shake, no dismissal |
| Drag on the scrim (any direction) | Nothing — no shake, no dismissal |
| Long-press anywhere (scrim or slip) | Nothing |
| Upward / horizontal swipe on the slip | Nothing — content scrolls (upward) as today; the slip does not translate |
| System Back | NOT a door. Do not add a `BackHandler` for the slip; host default stands (the slip's cold-start persistence is AUDIT-receipt item 20, group B — a neighbour, not this item) |

No dwell, no grace period, no faulted-slip exception: the two
lawful doors work from the slip's first frame, restored slips
included (a restored slip is the same object under the same law).
The swipe IS the explicit act for the faulted slip — see §4.1 for
how this answers AUDIT-receipt A8.

### 1.3 · The slip-swipe, specced

- **Direction:** downward only — the slip leaves through the foot
  of the screen, the reverse of its feed.
- **Tracking:** a downward drag that begins on the slip while the
  slip's own scroll state is at its top (`scrollState.value == 0`)
  translates the whole slip 1:1 with the finger. The expected
  mechanism is a `NestedScrollConnection` on the slip that claims
  post-scroll downward deltas at the top (the scroll keeps first
  claim on every other delta — content scrolling is untouched).
  A downward drag while the content is scrolled translates
  nothing; it scrolls back toward the top as today, and only the
  overshoot past the top becomes slip travel.
- **Threshold:** release at ≥ **56dp** of slip travel dismisses.
  Release below 56dp springs the slip home: 260ms,
  `MonolithCurves.specA()` (DURATION_260 · EasingA — a seat).
  A downward fling released with ≥ 24dp of travel also dismisses
  (velocity judged by the nested-scroll fling hand-off, not a
  second tracker).
- **Exit:** from the release offset the slip translates down its
  full height and the scrim fades 0.12 → 0, both over **180ms on
  `MonolithKin.EasingThrow`** (DURATION_180 — THROW is the house's
  named dismissal motion: "a dismissal accelerates away").
  `onDismiss()` fires exactly once, at the animation's end; the
  host unmounts as today. From the frame the exit starts, all
  input on scrim and slip is dead — no double-fire, no shake
  during the exit.
- **The slip consumes its own surface:** taps and non-dismissal
  drags on the slip are consumed by the slip's pointer handling.
  Nothing falls through to the scrim, nothing behind the sheet is
  reachable — same containment as today, now by the slip's own
  hand rather than by fall-through accident.

### 1.4 · The shake, specced

A stray scrim tap is a wrong-door knock; the answer is the slip
shaking its head — the paper moves, the room does not.

- **Target:** the slip only. The scrim never moves, never flashes.
- **Form:** horizontal keyframe shake on the slip's
  `translationX`, one pass, **280ms** (`MonolithCurves`
  DURATION_280), keyframes (time → offset):
  0ms → 0dp · 55ms → −6dp · 115ms → +5dp · 175ms → −3dp ·
  230ms → +1.5dp · 280ms → 0dp. Decaying, asymmetric, ends at
  exact rest — no overshoot past 0, no second pass.
- **Retrigger:** a tap mid-shake restarts the envelope from 0
  (snap, then replay). Taps never queue, never stack amplitude.
- **Feel:** one `HapticFeedbackType.TextHandleMove` tick at shake
  start, via `LocalHapticFeedback` — no new host seam, no sound
  (the receipt room is silent print; TactileSoundEngine is not
  wired here and F19 does not wire it).
- **Scrim ripple dies:** the scrim's `clickable` takes
  `indication = null`. A ripple on a defocus scrim is a button
  costume on a non-button.
- **Reduced motion:** if the system animator duration scale is 0,
  the shake collapses to a single 2dp nudge over 90ms — the
  refusal still reads, the motion does not perform.
- **Semantics:** the shake changes no state a screen reader can
  see; the slip's dismissal action name for F22's pass is the
  swipe ("Dismiss receipt" as a custom action on the slip) —
  noted here so F22 does not invent a scrim-tap action.

### 1.5 · Comment corrections that ride with F19

These comments become false the moment F19 lands; they are part
of this item, not F21 scope creep.

**(a) `ReceiptSheet.kt` header KDoc** — current: "…the slip
settled and still: it never re-strikes, never animates its
numbers, and dismisses on the next DOWN anywhere or on
navigation." Replacement paragraph:

```
 * receipt branch. Viewport-hosted (F-B1: it composes over the
 * Deck at the root, never inside the rail's band), defocus scrim
 * at 0.12 (Linea's frame law — the digest's 0.65 is a different
 * room's law), the slip settled and still: it never re-strikes,
 * never animates its numbers. Dismissal (F19): a downward swipe
 * on the slip itself, or the host's PARK/rail doors — a tap on
 * the scrim shakes the slip and dismisses nothing.
```

**(b) `MainActivity.kt` lines 263–268** — current: "the slip
stands until the next DOWN anywhere, a navigation, or PARK's
invocation". Replacement:

```
    // The receipt sheet's dismissal is host state (F-B1): the
    // slip stands until it is swiped away on its own surface, a
    // navigation, or PARK's invocation — a scrim tap only shakes
    // it (F19). It is never dismissed by a timer. (The nav/PARK
    // effect itself sits below, beside the PARK state it reads.)
```

**(c) `MainActivity.kt` receipt hosting block, lines ~748–754** —
current: "dismissed by the next DOWN (its scrim), by navigation,
or by PARK". Replacement:

```
            // THE RECEIPT SLIP (Slice E, F-L1/F-B1): viewport-
            // hosted over the Deck's room, dismissed by a swipe
            // on the slip itself, by navigation, or by PARK
            // (F19) — the two sheets (this and the digest) never
            // stack: PARK and the digest live in their own
            // invocations, and opening either dismisses this one
            // first.
```

Untouched, verified true under F19: the rail comment in
`MainActivity.kt` ("A stop press while PARK is open is dismissal
path (a) — … the session receipt sheet goes with it (F-B1)").

### 1.6 · Acceptance (paper/render-harness assertions)

1. Scrim tap ×5 → slip still composed; `dismissedReceiptSessionId`
   unchanged; shake observed after each tap.
2. Slip tap → slip composed, no shake, no dismissal.
3. Slip drag down 40dp, release → slip returns to 0 offset;
   no dismissal.
4. Slip drag down 80dp, release → exit runs; `onDismiss` fired
   exactly once; host state carries the session id.
5. Slip content scrolled (value > 0): downward drag scrolls
   content first; no slip translation until the top is reached.
6. Rail stop tap with slip standing → dismissed. PARK invoked
   with slip standing → dismissed. (Host doors regression.)
7. Faulted slip: identical set — swipe dismisses, scrim shakes.
8. Scrim shows no ripple on tap.

---

## §2 · F20 — PRINT PARK ON THE RAIL'S UNNAMED ZONE (S)

File: `MonolithRailView.kt` → `onDraw` (PARK-zone furniture block,
~line 226) + the class KDoc. Host chrome, flagged already by the
builder map as outside the Deck file but inside this issue.

### 2.1 · The print spec

- **Word:** `PARK`. Nothing else — no count, no badge, no icon.
- **Voice:** the stops' own print voice, no new style: the same
  `paintMono` in its word-pass state — Anonymous Pro, 10sp,
  `letterSpacing = 0.2f`, `Align.CENTER`.
- **Seat:** centred in the zone — centre x =
  (parkStartX + trackRightX) / 2, baseline = the stops'
  `wordBaseline` (centreY + 10sp × 0.36). Same line, same height
  as DECK · SHIELD · TASKS · LEDGER: five words on one baseline,
  the fifth behind its score line.
- **Fit, by arithmetic:** the zone is a constant 48dp
  (`RailGeometry.PARK_ZONE_DP`, width-independent). PARK at 10sp
  Anonymous Pro (0.6em advance): 4 × 6.0dp = 24dp, plus 3 letter
  gaps × 0.2em × 10sp = 6dp → ≈30dp of print in 48dp of zone,
  ≈9dp clear on each side of the word before the score line and
  the end wall. It fits without touching the geometry. The
  builder confirms against `paintMono.measureText("PARK")` on the
  first render; if a future typeface swap breaks the fit, the
  word shrinks — the zone never grows.
- **Tone law** (mirrors the stop words' three tones, same tokens):
  | State | Tone |
  |---|---|
  | At rest, cap elsewhere | `tokTextSecondary()` — an unselected stop's ink |
  | Pressed (`pressedStop == PARK_INDEX`, not dragging) | `tokTextPrimary()` — the one-frame strike, same as a pressed stop |
  | Cap in the zone (PARK open, or cap settled/dragged to nearest stop 4) | `tokOnControlFill()` — the word under the cap takes the glyph tone, same as a selected stop |
  While PARK is open the word keeps the glyph tone for the
  sheet's whole life: PARK is where he is, and the band says so.
  (The stops' "no word is selected while PARK is open" rule is
  unchanged — PARK's word is not a stop's selection, it is the
  zone's name lighting under its cap.)

### 2.2 · Furniture: the notch grip dies

The two notch lines at the zone's centre (`notchCx ± 2dp`,
ink 0.35 + 0.35 × parkFraction) occupy the exact centre the word
now takes — word and grip cannot share the seat. **The notch is
deleted, with the `parkFraction` inking that fed it.** What
stays, unchanged: the score line at the zone's inner edge
(ink 0.18, 6dp insets) and the end-wall line at the track's end
(ink 0.35) — they become the word's frame, the pair of rules a
named end-station sits between.

### 2.3 · Traps, named for the builder

- Do NOT add `"PARK"` to `RailGeometry.WORDS`. That list is the
  four stops; the draw loop, `stopForX`, and travel math index it
  as stops 0..3. A fifth entry would print a phantom segment word
  and invite stop-math drift. PARK prints from its own draw call
  in the zone block.
- PARK gains no rest position, no selection state, no travel
  change: `selectedTabIndex` stays 0..3, `PARK_INDEX = 4` stays
  travel-math only, the cap's resistance stretch into the zone
  (0.55 / 0.85 factors) is untouched.
- Geometry is untouched: `PARK_ZONE_DP` stays 48, margins,
  insets, segment math all stand. This item is print only.
- F22 note: the word is print. The rail's accessibility semantics
  (the zone's action name) remain F22's bar debt, unchanged by
  this item.

### 2.4 · Comment correction that rides with F20

`MonolithRailView.kt` class KDoc — current: "…and, at the track's
end, the PARK zone — a flat 48dp stretch that prints NO word
ever, because PARK is where the machine's own state lives, not a
place he goes." Replacement:

```
 * A 52dp recessed band flush at the foot of the screen: four stops
 * printed ON the recess (DECK · SHIELD · TASKS · LEDGER) and, at the
 * track's end, the PARK zone — a flat 48dp stretch that prints PARK
 * in the stops' own voice (F20): it is a door he uses, and a door is
 * named. The zone is still not a stop — no rest position, no
 * selection; the cap enters it, the word names it, the sheet is the
 * machine's own state. One cap travels the stops under KIN-NAV.
```

(`RailGeometry`'s KDoc — "There is no fifth rest position: PARK
is a zone the cap enters, not a stop it selects" — is true today
and stays true under F20. Untouched.)

### 2.5 · Acceptance

1. Render at rest: PARK visible in the zone, secondary ink, on
   the stops' baseline; notch lines absent; score + end-wall
   lines present.
2. PARK open: PARK in on-control ink; the four stop words
   unselected, as today.
3. Press-and-hold the zone: PARK strikes to primary ink within
   the frame, same as a stop press.
4. Cap travel, resistance, seat behaviour pixel-identical to
   pre-F20 renders at every other position.
5. Zone width 48dp at 360 / 390 / 412dp screen widths; word
   never touches either framing line.

---

## §3 · F21 — COMMENT TRUTH PASS, `TactileDeckScreen.kt` (S)

Audit charges: §5.8 (comment rot — the header's "12dp universal
spacing" promise over a 24dp-radius card at 16/18 padding;
sections run 1, 2, 3, 5) and §5.5 (idle ENDS slides while the
comments swear it never does). Method: the whole file's comments
read against the code beneath them, this shift. Every false or
imprecise comment is listed below with its exact replacement
text. Comments verified true are listed at §3.5 — the pass is a
sweep, not a sample.

### 3.1 · Ruling: sections UNNUMBERED, not renumbered

The banners drop their numbers and keep their names. A number
promises a sequence the file does not maintain (the pinned stack
was "5" because Slice D counted rooms that live in other files);
the next inserted room would either break the sequence or force
a renumber of every banner below it. Names are stable; numbers
were the rot's vector. The four banners become: THE INSTRUMENT ·
THE TASK · ARMED · THE PINNED STACK.

### 3.2 · Idle ENDS labelled PLANNED — the print spec

The fix list rules the label (not the freeze): §5.5's alternative
"freeze the derivation" dies — a frozen idle end would print a
time computed from a stale now, a worse lie than a moving plan.

- **Print:** the idle TIME cell reads **`ENDS 14:30 · PLANNED`** —
  the tense mark as a suffix, in the house's own tense grammar
  (the imprint's `· AS SEALED` suffix, Hale W-24). Suffix, not
  prefix, for a print reason: the HH:mm then sits at the same
  offset in idle and running cells, so the eye comparing the plan
  against the running end reads the clock in one place.
- **Site:** struck at the idle derivation only —
  `idleTimeCell`'s else-branch in the Deck file:
  `endsAtText(nowFrame + totalSpanMinutes * 60_000L) + " · PLANNED"`,
  density logic (UNA2-03: PASS only beside a freshly LIVE wire,
  else UNKNOWN) untouched.
- **NOT in `endsAtText`:** the formatter stays a pure
  "ENDS HH:mm" — the running cell and the press latch's running
  form use it unmarked, and F5's break eyebrow (`BACK AT HH:MM`)
  derives from the same helper family. The mark belongs to the
  idle call site, nowhere else.
- **Inheritance, free:** the press latch captures
  `idleTimeCell.first`, so the latched press print carries
  `· PLANNED` with no latch change — the plan stays labelled
  while the finger is down, which is exactly when a man might
  mistake it for a promise.
- **Branches that do NOT take the mark:** running ENDS (a pure
  function of the stored end — a fact, unmarked); idle FLOW's
  `NO END` (no end is claimed); `INVALID` (no end is claimed).
- **Fit:** TIME is the widest strip cell (weight 1.3 of 3.25;
  ≈146dp at 390dp). `ENDS 14:30 · PLANNED` = 20 mono characters
  ≈ 132dp at the cell's 11sp — fits; at 360dp (≈134dp cell) it is
  within ~2dp of the edge, so the builder verifies on the 360dp
  render and, if it clips, the cell's word drops to
  `ENDS 14:30 · PLAN` — never a smaller type size, never a wrap.

### 3.3 · The ledger — every false comment, with its replacement

**C1 · File header KDoc (lines 85–92).** Current:

```
 * TAB 0: FOCUS COMMAND CONSOLE (DEVELOPER-GUIDE.pdf §1–§11)
 * - Full-bleed stacked sections with 12dp universal spacing.
 * - The Instrument: one raised panel holding the readout + RotaryWheelView (Ren).
 * - Pinned `SlideToStartView` commit bar below the scroll.
 * - When armed, transforms into the dark Armed Lockdown state with One-Way Ratchet enforcement.
```

False in three places: there is no 12dp universal spacing (the
scroll column pads 12 horizontal / 8 vertical and spaces sections
12; the card is radius 24, padded 16/18, spaced 10 inside; the
pinned stack pads 12/8 with 6dp and 8dp spacers); the pinned
stack is four objects, not a lone commit bar; and this screen
enforces no ratchet — enforcement lives in the service and the
VM, the screen renders and asks. The DEVELOPER-GUIDE cite is
unverifiable from the tree and dies with the rest. Replacement:

```
 * TAB 0 — THE DECK: set the session, see the wire can hold it,
 * seal it.
 * - Scroll column: 12dp horizontal / 8dp vertical padding,
 *   sections spaced 12dp. The hero card: 24dp radius, 16dp
 *   horizontal / 18dp vertical padding, 10dp internal spacing.
 *   There is no universal spacing constant on this screen;
 *   each surface carries its own numbers.
 * - The Instrument: one surfaceBright panel holding the readout
 *   + RotaryWheelView — readout and wheel are one object.
 * - Pinned beneath the scroll: the proof strip, the slide's
 *   72dp register, the reserved sentence slot, and the imprint.
 * - Running: the same composable renders the running form over
 *   the session-night split (hero at t=0, ground at t+400).
 *   Enforcement lives in the service and the VM; this screen
 *   renders and asks.
```

**C2 · Slice D banner (line 323).** Current first sentences:
"TIME is a pure function of now + a span; it is re-derived at
minute boundaries from a frame clock, never stored, never slid."
— the §5.5 lie in its banner form: the idle print IS fed a
now-frame that advances every minute, so ENDS 14:30 becomes
14:31. (The banner's second half, the imprint freeze, is true
and is carried into the replacement verbatim.) Replacement:

```
    // SLICE D — the strip's derivations (Uma/Rosa/Emil). Running
    // TIME is a pure function of the session's stored end — it
    // never slides. Idle TIME is a PLAN: now + the dialled span,
    // re-derived at minute boundaries from a frame clock, so the
    // printed end advances as the clock does; the cell confesses
    // it on its face ("· PLANNED", §3.2 of the F21 spec). The
    // imprint's count freezes at the seal (host-side, Emil's
    // −1 law): the first composition that sees a running session
    // captures the then-live count and the BLOCK verdict, keyed
    // by the session's id.
```

**C3 · UNA2-03 rider (line 358).** Current is honest as far as it
goes ("a fact about the plan") but predates the label; it gains
the label's sentence so the next reader does not "simplify" the
suffix away. Replacement:

```
        // UNA2-03 rider: "ENDS HH:mm" is a fact about the plan, but
        // its PASS tone is a claim the wire stands behind — beside
        // a warrant that is not freshly LIVE the end prints at
        // UNKNOWN density, never PASS beside BLOCK OFF. The idle
        // print carries its tense as a suffix — "· PLANNED" —
        // struck at this derivation only; the running cell, a
        // stored end, carries no mark.
```

**C4 · Section 1 banner (line 544).** Number dies (§3.1); the
closing clause "The preset tiles are gone: one value, one
control — the wheel" is false while the FLOW key-cap stands
below the wheel as a second control (it dies under F2, not
here). Replacement:

```
        // =====================================================================
        // THE INSTRUMENT (Ren — hero card and dial are ONE object:
        // the readout lives in the same panel as the wheel that sets it.)
        // =====================================================================
```

**C5 · ESR-13 hero-card comment (line 552).** Current calls the
card "the Deck's one raised surface" — nothing raises it: the
`import androidx.compose.ui.draw.shadow` (line 36) is never
applied, and the audit (§2) records the lift as tone alone.
F11 owns the shadow-or-silence decision; until it lands the
comment stops claiming lift. Replacement:

```
                // ESR-13 (chrome C6): the hero card is the Deck's one
                // surfaceBright panel, both modes — lift by tone
                // alone; no shadow is applied (the draw.shadow
                // import above stands dead until F11 rules one).
                // The ground token (surface) here was the leaf-paper
                // false close's twin: the panel painted in the
                // ground's own tone.
```

**C6 · Vertigo comment (line 559).** Current: "at the seal the
room recedes" — the transform is applied to the hero card
itself, not the room; the instrument is what blurs (Schein's §6
charge; F15 moves the transform to the ground). Replacement:

```
                // The vertigo (Zug-II §5.2): at the seal THIS CARD
                // scales, rises and blurs — scale and rise on the
                // transform easing, the blur on its own re-ease,
                // peaking mid-shot (6dp at t = 0.5) and gone at
                // Settle. At rest every term is identity. (F15
                // moves the transform to the ground; until it
                // lands, the instrument itself is what recedes.)
```

**C7 · Eyebrow eviction comment (line 603).** Current: "the app
count's only homes are the strip's APPS cell and the imprint —
one number, LOCKED+PEEK, one rendering per fact." Internally
false — it lists two homes and claims one rendering (audit §4:
the eviction "moved it from three homes to two and declared
victory"; §7's U-3 names this comment as the law enforced by
comment alone). Replacement:

```
                // The eyebrow's count is EVICTED (D9/A8): the app
                // count's homes are the strip's APPS cell and the
                // imprint — two renderings of one number (the
                // split, LOCKED+PEEK), both struck from the same
                // split. The eyebrow's copy was the third; it is
                // the one that died.
```

**C8 · Pia FINISHES comment (line 713).** Current calls TIME
"(a pure function…)" without the idle caveat — the §5.5 lie in
miniature, at the exact spot a future editor would trust.
Replacement:

```
                    // Pia: the FINISHES line dies here — TIME's only
                    // home is the strip's TIME cell (running: a pure
                    // function of the stored end; idle: of the minute
                    // frame — a plan, printed "· PLANNED"). This
                    // row's copy slid with every recomposition.
```

**C9 · Section 2 banner (line 758) / Section 3 banner
(line 874).** Numbers die; the words are true and stand.
Replacements are the same lines minus `SECTION 2: ` / `SECTION
3: `:

```
        // THE TASK — one quiet line (Ren). Sub-steps behind a tap.
        // ARMED — session utilities (desk clock, held notifications)
```

**C10 · Section 5 banner (line 920).** Number dies; the closing
clause "so the zone under the wheel never changes height by a
pixel" misplaces the zone (the pinned stack sits beneath the
scroll column, not under the wheel). The strip's 53dp and the
Δy = 0 claims are true today and stand (F16 rewrites this banner
when it collapses the stack). Replacement:

```
        // =====================================================================
        // THE PINNED STACK (Slice D) — outside the scroll: the proof
        // strip, the slide's 72dp register, the reserved sentence
        // slot, and the imprint. The strip keeps its 53dp address in
        // every state; the slide zone holds the grace / valve chips
        // at the same address while sealed (Δy = 0 across tenancies),
        // so the pinned zone never changes height by a pixel across
        // the seal.
        // =====================================================================
```

**C11 · Slide-address comment (line 951).** Current parenthetical:
"(F-Z3's room, read by Kato's fallback: the utilities keep the
Δy = 0 address the zone has always had)" — the utilities live in
the scroll column's Section 3; they have never held this zone.
The zone's other tenants are the grace/valve chips. Replacement:

```
                    // The slide holds this address through the seal
                    // (as the seated ghost), through the finish's
                    // detention, and through its own return — the
                    // grace / valve chips take the address only for
                    // a settled, receipt-free running session
                    // (F-Z3's room: the chips are the zone's only
                    // other tenants, at the same address — Δy = 0
                    // across tenancies).
```

### 3.4 · Annex A — one file over, the same lie (severable)

`MonolithCustomComponents.kt`, `endsAtText` KDoc (lines 424–426).
Current: "TIME's print (Uma): a pure function of a stored
end-milli — the shipped ever-sliding finish line dies here;
nothing about this text is remembered, and nothing here ticks."
False for the idle call, which feeds it `nowFrame + span` — no
stored end exists at idle. It is outside the Deck file; flagged
as an annex so Rost can sever it if the file boundary must stay
hard — but the pass that fixes C2 and leaves this standing has
done half its job. Replacement:

```
 *  TIME's print (Uma): a pure function of ONE end-milli — the
 *  formatter stores nothing and ticks nothing. What the milli
 *  IS differs by caller: running passes the session's stored
 *  end (a fact); idle passes the current frame plus the dialled
 *  span (a plan — the Deck marks that print "· PLANNED" at its
 *  own derivation; this formatter never marks). */
```

### 3.5 · Acquitted — read, checked, true (no change)

The seal-shot banner and its clock note (Slice E); the cascade
gate comment (proving renders idle form); the print-tense law
(UNA2-03/04) and the wire-word note; Emil's latch comments
(E-4/E-5) and the latch-retirement notes at the seal and at
annulment (E-13, J-2); the seal-freeze block including the
restore fallback's honesty (E-1/E-2/E-3) and the freeze-presence
guard note; the held-finish and sled-scheduler comments; the
ESR-08 stopwatch derivation comment; the held-frame readout
comment (F-L2); the total-span pricing comment (BEYOND W3 —
breaks priced in, audit-acquitted); the hero span-class and
press comment (D3 — two ghosts at ±1dp, true today; F10 amends
its ghost sentence when the catch ghost dies); the wheel
ghost-through-the-shot comment (Zug-II §4.1); the FLOW key-cap
comment (true of the cap while it stands; dies with F2); the
input-death comments (Zug-II §4.6, ×3); the DONE key-cap comment
(ESR-07); the two-digit sub-step comment (DRW-08); the E5b B3/B4
exit comments; the F-Z3 utilities note; both ST-02 comments
(param default + END BREAK chip); the Wrapper (Ren) pinning
comment; the slot-tenant phase comment (step 4); the Suki
UNSET-retry comment; the parameter-block Slice D/E comments
(host owns the wire / phase / freeze / struck record);
`SealShotState`'s KDoc (phase boundaries match `phaseName`);
`PressLatch`'s KDoc (E-5/E-6). Signature and wiring comments in
`MainActivity.kt`'s Deck call (Zug-II §5.1 raw-theme note) also
checked — true.

### 3.6 · Acceptance

1. Grep the Deck file for `SECTION [0-9]` → zero hits; for
   `12dp universal` → zero hits; for `never slid` outside the
   running-TIME sentence of C2's replacement → zero hits.
2. Idle render: TIME cell prints `ENDS HH:MM · PLANNED`;
   one minute later the HH:MM has advanced and the mark stands.
3. Running render: TIME cell prints `ENDS HH:MM`, no mark.
   Idle FLOW: `NO END`, no mark. Press latch at idle: the
   latched TIME print carries the mark.
4. Every replacement above pasted verbatim — a paraphrased
   "equivalent" comment is a new unverified claim, not this pass.

---

## §4 · FLAGS FOR ROST

**4.1 · F19 vs AUDIT-receipt A8 — supersession needs the merge's
word.** Zettel's receipt audit (item 19, group A8) ruled:
dismissal on a real DOWN gesture + a 2-second dwell from
`receiptPublishedMonoMs` during which taps pass through + the
faulted slip dismissing only on an explicit act. Rost's F19
(deck audit §9, later document, and my brief) rules a different
set: scrim tap shakes, dismissal by slip-swipe or PARK/rail
only. This spec follows F19 whole, on two stated grounds: (a)
the deck audit is the later ruling and this cell's commission;
(b) the dwell would make the LAWFUL doors feel broken for the
slip's first two seconds — a man swiping a slip that will not
move reads a jam, not a courtesy; the shake answers the stray
tap without gating the true doors. Muede's intent under A8
(a faulted slip must not die by accident) is preserved in
substance: a scrim tap cannot kill any slip, faulted included,
and the swipe is an unmistakable act. If Ren wants A8's dwell
back, it bolts onto §1.3 as one clause (doors inert for 2s from
the publication stamp; shake still live) — flagged, not built
into the spec. Zettel's cell should hear the supersession from
the merge, not discover it.

**4.2 · Not mine, not touched.** The receipt sheet's arrival
(A7: feed not fade; the dead exit `fadeOut`) — F19's exit is
internal to the sheet and does not depend on it, but the two
lanes edit adjacent lines of `ReceiptSheet.kt`; sequence A7
before F19 or expect a trivial conflict at the
`AnimatedVisibility` block. The dismissed-№ persistence
(AUDIT-receipt item 20 interim law) changes the host state F19
reads; compatible, separate lane.

**4.3 · F20 is host chrome.** As the builder map already flagged:
`MonolithRailView.kt` is outside the Deck file and adjacent to
issue #4's territory (top status strip — a different surface;
the rail is the foot band and §2 touches nothing above it).
Spec is written to land under this issue; Rost rules the lane
boundary.

## §5 · WHAT THIS SPEC DOES NOT DO

No code is written here and nothing is compiled or committed —
DECLARED only, per the build law. Prices stand as the audit's:
F19 S · F20 S · F21 S. Files the builders will touch: F19 →
`ReceiptSheet.kt` + comment-only edits in `MainActivity.kt`;
F20 → `MonolithRailView.kt`; F21 → `TactileDeckScreen.kt` (+
Annex A in `MonolithCustomComponents.kt` if Rost keeps it).

— FINISHER-4, Rost's cell, issue #6. Spec DECLARED, uncompiled,
uncommitted.
