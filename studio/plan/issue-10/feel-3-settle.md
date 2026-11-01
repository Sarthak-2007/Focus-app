# Issue #10 — Receipt (audit fixes) · FEEL SPECIALIST 3 (settle & stillness)

**Seat:** Feel Specialist 3 — settle & stillness, Zettel's cell for GitHub
issue #10, "Main-app pass — Receipt (audit fixes)".
**Items owned:** `studio/plan/AUDIT-receipt.md` items **18–20** (the part of
18 after the feed lands), **19** (dismissal feel), and the stillness /
scrim / anchor acquittals that bind them.
**Code grounded in:** this clone (`agent/issue-10`):
`ui/components/ReceiptSheet.kt`, `MainActivity.kt` (host),
`ui/MonolithViewModel.kt` (publication stamp), `data/ReceiptState.kt`
(contract + Detention), `ui/theme/MonolithCurves.kt` (motion vocabulary).
All line citations are to that tree.
**Sibling specs composed with:** `work/feel-1-arrival.md` (feed =
280 ms · Curve A / SEAT · 56 dp · no alpha; restored = no arrival),
`work/designer.md` §3 (dismissed-№ persistence attaches to the dismissal
*event* this document defines), `work/feel-2-tear.md` (the TEAR is group
**B1**, undecided — **nothing in this document is, hints at, or pre-builds
a tear**).
**Standing constraints honoured:** stillness after arrival is **binding**
(no re-strike, no animated numbers, no auto-dismiss timer); scrim **KEEP
at 0.12** (the room stays visible); anchor = **contact** (0 padding at
bottom/sides, item 17); zero buttons / CTA on the paper; no new colour;
never green; no images. Nothing here compiles, builds, or tests — paper
only. Nobody messages Sarthak.

---

## 0. The spec in three lines

- **Settle:** the feed's own tail is the only settle there is — at
  `receiptPublishedMonoMs + 280 ms` (fresh slips) or on frame 0 (restored
  slips) the paper is at offset 0, flush on its anchor, and from that
  instant **no property of the slip ever animates again** while it stands.
- **Dwell feel:** *held by pass-through proof* — for 2 s the slip changes
  in no visible way at all, and every tap lands **through** it on its own
  target and does that target's work; the user trusts the hold because
  the slip demonstrably refuses to be swatted, not because anything
  counts the 2 s down for him.
- **Dismissal:** a real **DOWN**, post-dwell, and then a **cut — 0 ms**.
  Paper and scrim vanish together on the dismissal frame. No fade, no
  slide-out, no tear. The faulted slip cuts the same way, but only a
  DOWN on the paper itself can cut it.

---

## 1. What the ending feels like today (evidence)

1. **There is no settle, because there is no landing.** The sheet is one
   `AnimatedVisibility(visible = true)` crossfade —
   `ReceiptSheet.kt:60–64`, `enter = fadeIn(specC())` (280 ms, EasingC),
   `exit = fadeOut(specA())` (260 ms). Alpha has no "rest position" to
   settle into; the slip doesn't land, it condenses. Feel-1 kills the
   fade and installs the feed; this seat owns everything from the feed's
   last frame onward.
2. **The exit fade is dead code, so today dismissal is already a cut —
   an accidental one.** `visible` is hardcoded `true`; the host
   (`MainActivity.kt:776–785`) composes the sheet conditionally and
   unmounts it outright on dismissal / navigation / PARK. §4 makes the
   cut deliberate, timed to DOWN, and identical on every door.
3. **Dismissal today is an UP-swat that eats the tap.**
   `ReceiptSheet.kt:70` — the scrim Box is
   `.clickable(onClick = onDismiss)`: `clickable` fires on **UP**,
   consumes the gesture, and the tap's own target never fires. The
   file's own KDoc (`ReceiptSheet.kt:43–45`) already promises
   *"dismisses on the next DOWN anywhere"* — the code does the opposite.
   Audit item 19 charges exactly this.
4. **There is no dwell at all.** Nothing in `ReceiptSheet.kt` or the
   host reads `receiptPublishedMonoMs`. The stamp exists and is already
   sampled in exactly one place — the VM's `emitReceipt`
   (`MonolithViewModel.kt:1322–1330`: `receipt.copy(receiptPublishedMonoMs
   = SystemClock.elapsedRealtime())`) — and its only current consumer is
   the sled gate's separate 1000 ms deadline (L1331–1341) and the Deck's
   sled-return count (`TactileDeckScreen.kt:281–295`). The 2 s dwell is
   a new reader of the same stamp, not a new stamp.
5. **The slip floats instead of resting.** Host Box at
   `ReceiptSheet.kt:66–72`: scrim `Color.Black @ 0.12` (L69),
   `.padding(12.dp)` on **all** sides (L71), `Alignment.BottomCenter`
   (L72). Audit item 17: the nav pill peeks under the slip's foot
   because of that float. A thing that hovers 12 dp off the surface has
   not settled in any sense the eye accepts — §2's resting geometry
   assumes cure **A5**'s contact anchor (0 at bottom and sides).
6. **Restored slips re-perform and re-arm.** A rehydrated slip
   (`ReceiptState.restored`, contract `ReceiptState.kt:159–167`;
   installed by `runSliceERecovery`, `MonolithViewModel.kt:~1096–1103`,
   with `receiptPublishedMonoMs = 0` and the contract's *"never
   restored (C3); a restored slip carries 0 and no clock reads it"*)
   still runs the mount fade, and under a naive dwell implementation
   would either re-dwell on a dead stamp or never become dismissible.
   §2.4 and §3.5 close both.

---

## 2. The settle — final resting behaviour

### 2.1 The settle instant

| Branch | Motion before stillness | Still from |
|---|---|---|
| Fresh, witnessed (clean or faulted — identical, per feel-1 §6.3) | The one feed: 280 ms · Curve A · 56 dp · whole document · no alpha | `receiptPublishedMonoMs + 280 ms` |
| Restored (Z6 case b) | **None. Ever.** `EnterTransition.None`, offset 0 on frame 0 | First rendered frame |
| Branch-U / discovery (`publishReceipt` facade) | Feeds as feel-1 §3 leaves it (its flagged question is Zettel's, not reopened here) | Its feed's end, same arithmetic |

There is no second "settle animation" after the feed, and the builder
must not invent one. Feel-1 §2.3 already measured the feed's tail: in
the last 10 % (252–280 ms) the paper moves **0.2 dp — sub-pixel** — on
Curve A, which never overshoots. That tail *is* the settle. What this
seat adds is the law for everything after it.

### 2.2 The allowed micro-motion list — exhaustive

Before the settle instant, exactly these motions may exist in the
slip's frame:

1. **The feed's `translationY`** — the single animated property, on the
   paper only, per feel-1's numbers. Nothing rides with it: no alpha,
   scale, rotation, shadow/elevation, or scrim change is timed to the
   feed or to its end.
2. **The room moving behind the slip.** The Deck's sled return fires at
   `receiptPublishedMonoMs + 1050 ms` (`TactileDeckScreen.kt:281–295`)
   and the ground may step its night split — all visible *through* the
   0.12 scrim, all belonging to the room. The slip does not ride, lean,
   parallax, or react to any of it. Contact with the anchor is what
   makes this read correctly: the room settles underneath a paper that
   is already lying still.
3. **The user's own scroll of a tall slip** (`verticalScroll`,
   `ReceiptSheet.kt:82`). User-driven, never animated: no fling
   assist, no auto-scroll to the footer, no snap. At rest the scroll
   offset is **0** — the head of the document is what arrival presents.

Everything else is forbidden micro-motion, named so a reviewer can fail
it on sight: overshoot / bounce / spring-back of any size; a 1–2 dp
"press" dip when the paper is touched; a shadow that deepens as the
feed lands; a scrim that breathes, pulses, or steps at any timestamp;
per-line stagger completing after the document; any number, rule, or
№ resolving, ticking, or re-striking after frame 0 of the feed.
Driven paper is stiff and roller-held — it stops, it does not wobble
into place.

### 2.3 Resting state — the definition of "settled"

From the settle instant until dismissal, for as long as the slip
stands, **all** of the following hold continuously:

- **Geometry:** `translationY = 0 dp`, scale 1, rotation 0. The paper
  sits flush on the contact anchor — bottom edge and both side edges
  in contact (0 padding, item 17 / A5), covering the nav band; the top
  edge is the only free edge. (Until A5 lands, the same zero-offset law
  applies inside today's 12 dp float — the offset binds, the padding
  is A5's seat.)
- **Opacity:** paper at alpha 1.0; scrim at **exactly 0.12**, constant
  — it does not deepen while the slip is read, lighten as the dwell
  expires, or flicker on recomposition.
- **Content:** every printed figure at its issued value. Two frames
  sampled ≥500 ms apart while the slip stands are pixel-identical in
  the slip and the scrim (the room behind the scrim may differ — the
  sled, a clock — the slip may not).
- **State:** the animation holds nothing. Settled offset is a constant
  0, not an `Animatable` resting at 0 that a later recomposition,
  destination return, activity recreation, or re-emission could
  re-run. `emitReceipt`'s first-emission-stands guard
  (`MonolithViewModel.kt:1323–1327`) is the VM half of this law; the
  sheet's half is: a standing slip never re-enters an enter transition.
- **Lifetime:** no auto-dismiss timer, no idle fade, no "read and
  retire" — the slip stands until a lawful dismissal event (§4) or the
  next arm clears it (`armMonolithSession` nulls `_receiptState`).
  Stillness is not a countdown wearing quiet clothes.

### 2.4 Restored slips — born settled, stay settled

Gate on the flag, never on a clock (`ReceiptState.kt:159–167`): a
slip with `restored = true` renders at offset 0, alpha 1.0, scrim
0.12, on its first frame, and no code path — recomposition, Deck
return, recreation — may feed, fade, or settle it again. Settled is a
motion property only: content, scrim, and dismissal law are identical
to a fresh slip's (§3.5 for its dwell state).

---

## 3. The dwell — invisible, but trustworthy

### 3.1 The law (audit item 19, cure A8)

For **2000 ms from `receiptPublishedMonoMs`** (the stamp
`MonolithViewModel.kt:1328–1330` already samples at issuance):

- no tap dismisses the slip;
- a tap is **not consumed by the slip** — it passes to its own target
  and that target's action fires;
- the in-flight tap — a finger already DOWN, or a DOWN landing in the
  same instant the slip composes — cannot kill an unread slip.

After 2000 ms the gate opens (§4) and stays open. The dwell is an
input-routing fact. It is **not** a state the slip displays.

### 3.2 The feel problem, stated plainly

A dwell the user cannot sense reads as lag or as a dead screen; a
dwell the user *can* sense through a progress bar, a filling border,
a greying scrim, a "hold on…" shimmer, or a haptic at expiry reads as
a game mechanic stapled to a legal document — and every one of those
devices is either an animated property on a slip under a binding
stillness acquittal, or an auto-dismiss timer's cousin. Both
directions are closed. So the dwell must be felt **without being
shown**.

### 3.3 The solution — trust by consequence, in three proofs

The user learns the hold the way he learns that a table is solid: by
leaning on it and having it not move.

1. **Pass-through proof (the primary signal).** During the dwell, a
   tap on anything the scrim covers — a rail stop, the Guard line /
   PARK, a Deck control — **does that thing**, visibly, immediately.
   The tap is not swallowed, not queued, not answered with a ripple
   from the slip. Within the first second of the slip's life the user
   can watch his own input land *past* the paper, twice if he tries
   twice. A slip that cannot be hit cannot be swatted; that is felt
   in the hand, not read off a meter. (Where the passed-to target's
   own consequence is leaving the Deck or opening PARK, the slip is
   dismissed *by that act* under the host law,
   `MainActivity.kt:303–308` — dismissed by navigation, never by the
   tap striking the slip. The designer's persistence §3.3 records it
   as a dismissal through that door, identical in effect.)
2. **Flinchlessness (the negative signal).** A tap whose target is
   the paper itself, or bare room under the scrim, produces **nothing
   from the slip**: no ripple, no press dip, no nudge, no sound from
   the slip, no scrim step. Ordinary cards flinch when poked; this
   paper doesn't notice. Combined with proof 1 — *things I aim at
   work; the paper itself ignores me* — the hold is unmistakable
   without a single pixel changing.
3. **A sharp far edge (the retroactive signal).** The gate opens at
   exactly 2000 ms on every slip, every branch, faulted included in
   its own way (§4.3), measured from the one stamp — never from
   settle, never from first composition, never re-started. The first
   lawful DOWN after it dismisses instantly (§4). One consistent,
   crisp boundary teaches the duration after two or three receipts;
   a fuzzy or displayed boundary would teach him to watch the slip
   instead of trusting it. **Expiry itself is silent and invisible:**
   no tick, no haptic, no alpha step at 2000 ms — nothing changed
   visually, so nothing visual may announce it. Only input routing
   changed.

That is the whole feel design: **the slip proves it is held by
letting everything else work.** Any builder proposal that adds a
visible dwell state — bar, sweep, countdown, dimming curve, border
draw, pulsing № — fails this spec and the acquittal behind it.

### 3.4 Timing arithmetic (typeable)

- `dwellDeadlineMono = receipt.receiptPublishedMonoMs + 2000L`.
  The feed (280 ms) occupies the dwell's first 14 %; **1720 ms of
  protected stillness remain after settle**. The dwell does **not**
  restart at settle and is **not** extended by the feed.
- The 2000 ms dwell and the sled gate's 1000 ms deadline
  (`MonolithViewModel.kt:1331–1341`) share the stamp and nothing
  else; neither moves the other.
- **Which instant judges a tap: its DOWN timestamp**, sampled when
  the DOWN is dispatched, never its UP / click completion. A DOWN
  inside the dwell whose UP lands after 2000 ms is a dwell tap
  forever — it passes / is inert, and its later UP dismisses
  nothing. A gesture that *starts* inside the dwell (scroll, fling,
  drag on a room target) is a dwell gesture for its whole life; it
  cannot mature into a dismissal mid-flight. Dismissal needs a
  fresh DOWN after the deadline. Each finger's DOWN is judged
  independently at its own instant.
- **Implementation shape:** one boolean gate per slip
  (`remember(receipt.sessionId)`), initialised open iff
  `receipt.restored` (§3.5), flipped once by a single
  `LaunchedEffect` that delays `max(0, dwellDeadlineMono −
  SystemClock.elapsedRealtime())`. No per-frame clock reads, no
  polling, no progress state of any kind exists to render from.

### 3.5 Zones and the restored case

- **Paper zone:** the paper Column already swallows its own touches
  via `.clickable(enabled = false) {}` (`ReceiptSheet.kt:80`). That
  stays during the dwell — taps on the paper are inert (proof 2),
  *not* forwarded to whatever Deck control happens to lie beneath
  the paper; the room moving because you touched the document lying
  on it would be a lie of a different kind. Scrolling the paper is
  its own target and works during the dwell.
- **Room zone:** everywhere the scrim covers that is not paper, the
  scrim installs **no consuming pointer handler at all** during the
  dwell — no `clickable`, no `pointerInput` that consumes. Absence
  of a handler is the pass-through; a handler that "forwards" is a
  re-implementation that will eat an edge case.
- **Restored slips:** the dwell is already served — the slip has
  stood across a process death, and its stamp is 0 by contract
  (*"no clock reads it"*). Gate initialises **open**: a restored
  slip is dismissible by the first lawful DOWN (§4), with no fresh
  2 s on every cold start. Re-dwelling a restored slip would punish
  him for the app's death and would read the dead stamp to do it.

---

## 4. Dismissal motion — under current law (no tear)

### 4.1 The event (what counts)

A dismissal event is exactly one of:

1. **A real DOWN on the room/scrim zone, after the dwell**, on a
   non-faulted slip (§4.3 for the faulted gate). DOWN, not UP:
   replace the scrim's `.clickable(onClick = onDismiss)`
   (`ReceiptSheet.kt:70`) with a DOWN detector (`pointerInput` +
   `awaitFirstDown`, consuming that DOWN) installed **only while
   the gate is open** — before the gate opens the handler does not
   exist (§3.5); after it opens, the DOWN it consumes is spent on
   dismissal and does **not** also fire the room target beneath.
   The exclusivity flips exactly once, at the deadline: before it,
   taps belong to their targets; after it, a room DOWN belongs to
   dismissal.
2. **Navigation away from the Deck, or PARK's invocation** — the
   host's existing doors (`MainActivity.kt:303–308`, rail path
   L716ff). These are consequences of the target's own act (§3.3)
   and dismiss at whatever moment they fire, dwell or no dwell —
   the dwell restrains the slip's *own* tap handler, not the room's
   lawful acts.
3. **The next arm** clearing `_receiptState` — housekeeping, not a
   gesture, listed so no door is undocumented.

Dismissal hooks persistence at this event and no earlier raw tap
(designer §3.5): a dwell tap writes nothing, dismisses nothing.

### 4.2 The motion — a cut, specified

| Property | Value |
|---|---|
| Duration | **0 ms.** No transition is scheduled. |
| Form | Host unmount (`MainActivity.kt:776–785` gate closes): paper and scrim leave composition **on the same frame**, the frame the DOWN is dispatched on (≤1 frame of render latency). |
| Paper | No translation, no alpha ramp, no scale, no torn edge, no perforation, no pre-tear hint of any kind. It is standing at offset 0 / alpha 1.0 in its last frame and absent in the next. |
| Scrim | Lifts with the paper, same frame, from its constant 0.12 — never a step to 0 across frames, never lingering after the paper. |
| `AnimatedVisibility` exit | `ExitTransition.None` — feel-1 §5 already deletes the dead `fadeOut(specA())` (`ReceiptSheet.kt:63`); this seat confirms nothing replaces it. If a builder's restructure reintroduces any non-`None` exit, it fails here. |
| Sound / haptic from the motion | None specified by this seat; the slip is paper (feel-4's map governs all sound, and dismissal is silent there). The DOWN's own target feedback, where the dismissal rode in on navigation/PARK, belongs to that act, unchanged. |

Why a cut is the *felt* answer, not a missing animation: arrival is
the machine's act and it had to prove itself (detention, then a
280 ms feed). Departure is **his** act. A dismissal that takes
160–280 ms to leave makes his DOWN feel negotiated; a cut makes it
feel *obeyed* — finger down, paper gone, room back at full light in
one frame, no after-image of the thing he just dismissed crossing
the screen. The asymmetry (280 ms in, 0 ms out) is the feel: the
machine takes its time arriving with proof; it takes none leaving
on his word. A slide-out "to match the feed" was considered and
rejected — reverse-feeding is re-printing, it re-animates a slip
under the stillness law's spirit, and it delays the room target's
room for the sake of symmetry nobody asked for. The TEAR, when and
if Ren rules B1 in, will replace this section wholesale through
feel-2's proposal; until then there is no tear-shaped anything.

Navigation/PARK dismissals are also cuts, trivially: the slip is
simply not in the new room's first frame, and PARK's own 280 ms
SEAT rise (`MonolithKin.DRAWER_OPEN_MS`) is the only motion that
act contains.

### 4.3 The faulted slip — explicit act only

The faulted slip's news — the shield did not stop — is the one
receipt fact too urgent to be swattable (audit items 14/19; Muede
P-3). Its gate is higher in **place**, not in time:

- **Scrim/room DOWNs never dismiss it** — not during the dwell, not
  after it. The scrim handler in §4.1(1) is never installed for a
  faulted slip; room taps behave as in the dwell for its whole life
  (pass to their targets; navigation/PARK consequences per §4.1(2)
  still apply, because leaving the room is his act, not a swat —
  and on return to the Deck an undismissed faulted slip re-stands,
  settled, per §2.4's law applied through rehydration).
- **The explicit act is: a DOWN on the paper itself**, after the
  dwell (the dwell binds faulted slips identically — an in-flight
  finger cannot kill even this slip in its first 2 s). He must put
  his finger on the document that carries the bad news. That is
  deliberate, locatable, and impossible to do by accident while
  reaching for the room — and it adds **no button, no CTA, no
  printed instruction** to the paper (zero-CTA acquittal stands;
  the paper does not learn to beg to be dismissed).
- Implementation: on the faulted branch only, the paper Column's
  inert swallow (`ReceiptSheet.kt:80`) becomes, once the gate
  opens, a DOWN detector on the paper that calls the same
  `onDismiss`. Before the gate opens it stays the inert swallow.
- Once that act lands, dismissal is the §4.2 cut, and persistence
  treats it identically to any other door (designer B3.4).

### 4.4 No resurrection of the ending

A dismissed slip produces no exit ghost: no recomposition may flash
its last frame, no restored copy of a *dismissed* № re-stands
(designer §3 gates that), and an *undismissed* slip that re-stands
via rehydration arrives under §2.4 — settled, silent, already past
its dwell. The ending ends once.

---

## 5. Branch matrix (one slip, every world)

| Case | Settle | Dwell | Dismissal |
|---|---|---|---|
| Fresh clean / FLOW / routine-attended | Feed → still at +280 ms | 0–2000 ms pass-through | Room DOWN post-dwell → cut |
| Fresh faulted (issuance E1800) | Same feed, same +280 ms still | Same 2000 ms from its stamp | **Paper DOWN only**, post-dwell → cut |
| Fresh PARTIAL / death-booked | Same feed (no variant for a sadder slip) | Same | Same as clean |
| Restored, undismissed | Born settled, frame 0 | Already served — gate open | First lawful DOWN → cut (faulted restored: paper DOWN) |
| Restored, dismissed № | Never renders (designer §3) | — | — |
| Standing while PARK opens / navigation fires | Was settled; unmounted mid-stillness | Irrelevant — target's act | Cut; not in the new room's first frame |

---

## 6. Acceptance checks (paper checks for the builder's proof)

1. **Stillness:** fresh slip settles by +280 ms from its stamp; two
   screenshots 500 ms apart after settle are pixel-identical in
   slip + scrim; no frame after settle shows offset ≠ 0, alpha ≠ 1,
   or scrim ≠ 0.12.
2. **No forbidden micro-motion:** frame-step the feed's last 100 ms —
   no overshoot frame, no shadow step, no per-line landing after the
   document.
3. **Dwell pass-through:** at +500 ms, tap a rail stop — the app
   navigates (and the slip is gone by that act, not by the tap on
   it). Restart the case; at +500 ms tap a Deck control visible
   beside the paper — the control fires; the slip stands.
4. **Dwell flinchlessness:** at +500 ms, tap the paper and tap bare
   scrim over dead room — no ripple, dip, sound, or dismissal; slip
   pixel-identical after both taps.
5. **DOWN-judging:** press at +1900 ms, hold, release at +2100 ms —
   no dismissal. A fresh press at +2100 ms dismisses on its DOWN,
   before its UP.
6. **Cut:** dismissal frame-stepped — last frame standing (offset 0,
   scrim 0.12), next frame both gone; no intermediate alpha/offset
   frame exists. No `fadeOut` / non-`None` exit remains in the file.
7. **Faulted:** room DOWNs at +500 ms and +3000 ms never dismiss;
   paper DOWN at +1000 ms does not dismiss; paper DOWN at +2100 ms
   cuts it.
8. **Restored:** force-stop with a slip standing → relaunch — slip
   present settled on frame 0, no feed/fade, and the first room DOWN
   dismisses it immediately (no second dwell). Dismissed before the
   force-stop → it never renders.
9. **No gimmick audit:** grep the sheet for any dwell-driven UI state
   (progress, countdown text, alpha/border animation keyed to the
   deadline) — there is none to find; the only dwell artefact in
   code is the one boolean gate.

## 7. What this spec deliberately does not touch

- The feed's numbers, issuance, and detention arithmetic — feel-1 /
  the VM; read here, never moved. The sled gate's 1000 ms deadline
  is not the dwell and is not extended to meet it.
- Shape, radius, shadow value, and the contact anchor's padding
  edit itself — A5 / feel-2 §1; §2.3 states the resting law that
  composes with them.
- Print content, rhythm, outcome words, fault line wording — the
  print seats. The faulted slip's *dismissal gate* is here; its
  words are not.
- Sound and haptics — feel-4's map (which already holds the slip
  itself silent; nothing here adds a call).
- Dismissed-№ persistence storage — designer §3; this document
  defines the event it hooks, per its §3.5 sequencing note.
- THE TEAR, the torn edge, the SLIP BOOK — group B. If B1 is ruled
  in, §4 is the section it replaces; until then, no tear.

— Feel Specialist 3 (settle & stillness), Zettel's cell (issue #10).
Paper only; nothing compiled.
