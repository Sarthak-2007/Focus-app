# FEEL — THE VEIL: WEIGHT, ARRIVAL, THE FIRST TOUCH

HAUCH, Feel Specialist 1, Wacht's cell — issue #13
(Sarthak-2007/Focus-app, branch `agent/issue-13`, clone
`builds/cells/issue-13`). Slice: THE VEIL alone — E-1 as
ratified (`studio/plan/AUDIT-session-room.md` §4 E-1;
`DESIGN-face-spec.md` §10): after a routine finish the Deck
composes under a black scrim at 0.80 until first touch
(night return); keep-awake already cleared, so the OS
timeout darkens the veiled Deck if untouched. Composed
grammar: machine return wears the veil → first touch
unveils onto the Landing → first verb blooms.

STATE OF THE GROUND. Judged on the CURRENT tree — the
Session Room merge PLUS the wave-2 cure pass (Amt,
`ISSUE-13-WAVE2-REPORT.md`), re-read fresh for this filing:
`app/src/main/java/com/example/ui/MonolithViewModel.kt`,
`app/src/main/java/com/example/MainActivity.kt`,
`app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt`
(the Deck itself carries no veil code — the veil is host
choreography, which is correct), `data/ReceiptState.kt`,
`ui/components/AirlockAndDisarmOverlays.kt` (the finish
exit the veil is born under). Cross-read: Daemmer's
FEEL-daemmer.md — DAE-4 (veil scope) and DAE-6 (unveil
flash) touch this slice; DAE-5 and DAE-7 are the veil's
other half and are judged here only where they change what
the veil's touch means. Cured items are verified, not
re-filed (§1). No code touched by this seat; no compile
run, per orders.

## THE VERDICT IN ONE BREATH

The veil now fires on the right KIND — Salz C-22's cure is
real and verified in the code — and 0.80 is the right
weight, defended in §2. But the veil as composed is still
two objects pretending to be one light: a boolean black
box that vanishes in a single frame, and a second scrim
that then climbs from zero to the Landing's level — so the
first touch of the morning, the exact touch this grammar
exists to protect, opens with a pulse of full Deck light
before the dim arrives (DAE-6, still standing, still the
slice's worst defect). Around that centre: the veil can
still be born stale and uncovered on the cold-discovery
path, it lands on the standing print at E0 and cuts the
finish fade it was supposed to sleep under, the Landing
still exists only behind the veil (a night hand-dismissal
lands on the full-light Deck), and the bloom still fires
on ANY interaction — a brush, a volume key — not on the
first true verb. The trigger was cured in wave-2. The
light was not. One scrim, one animated value, three
targets cures the centre and most of the ring.

---

## §1 · THE GATE — does the veil fire only where it belongs?

### C-22, verified cured (do not re-file)

Pre-wave-2, `onFinishE0` set `_nightVeil` for EVERY
routine-born kind, witnessed or not, at any hour — a
witnessed noon ROUTINE_ATTENDED finish blacked the Deck
at 0.80 in his hand (Salz C-22; Daemmer DAE-4(a)). The
current code gates on the stand-down kind alone:

```kotlin
// MonolithViewModel.kt:1572–1573, in onFinishE0
if (kind == com.example.data.FinishKind.ROUTINE_SLEEP_STANDDOWN) {
    _nightVeil.value = true
}
```

And that kind is narrow by construction
(`ReceiptState.kt:94–96`): inactive ∧ SLEEP-loadout
routine row ∧ `routineOutcome == null` ∧
`windowEndMillis in 1..nowWallMillis` ∧ outcome column
empty (a valve-broken SLEEP row is excluded). No manual
finish, no attended/partial routine finish, no noon study
span can wear the veil any more. The noon blanket is
dead. Acquitted and closed.

### What the kind-gate does NOT settle

**Night is implied by the kind, not checked.** The SLEEP
routine's window is his 20:00–03:30 today, but the window
is editable (30 min – 18 h spans are lawful in the routine
editor) and the loadout test reads only the routine's
`loadoutName`. A SLEEP-loadout window moved to daylight
would stand down — and veil — at noon, lawfully, under
the current gate. This is not C-22 re-filed: the study-
noon case is cured. It is the gate's one unexamined
assumption, named so Wacht can rule it cheaply: the veil
trusts that a stand-down is a night event because his
schedule says so. My recommendation is to LEAVE the gate
kind-only (a second clock discriminator is a second
chance to disagree with the stand-down itself) and record
the assumption here — if the SLEEP window ever moves, the
veil moves with the act it belongs to, which is arguably
the truthful behaviour. Priced-and-named, not a defect.

**Fresh and machine-return are NOT gated — see H-3.**
`_nightVeil` is set at `MonolithViewModel.kt:1573`
synchronously, BEFORE the witnessed / branch-U split
(the `!witnessed` return sits inside the launched job
below it). Every path that reaches `onFinishE0` with the
stand-down kind veils — including a branch-U discovery
hours cold. That remainder is DAE-4(b)/(c), narrowed by
wave-2 but not closed, and it is filed below as H-3 with
its one new consequence (the uncovered arrival) that
Daemmer's filing predates the merged composition for.

**Machine-return only — hand paths never veil.** Verified:
the veil has exactly one writer in the tree
(`MonolithViewModel.kt:1573`; grep-clean across
`app/src`). No hand dismissal, no chip, no navigation can
raise it. The veil is machinery's garment, never the
hand's. Acquitted.

---

## §2 · THE WEIGHT — is 0.80 right at 03:30?

**Yes. 0.80 is the hinge value, and it should not move in
either direction.** The reasoning is about what stands
under it. The Deck at night is the night scheme:
ground `#171412` (Color.kt:32, relative luminance
≈ 0.008), ink `NightColorPrimary` `#FFE4CF`
(L ≈ 0.81), accent `#F47442`. A black scrim composites
multiplicatively — veiled transmission is 20 % of the
Deck's own light:

- **The ground goes effectively out.** 0.20 × a near-black
  ground is OLED-adjacent black. The room the Deck stands
  in disappears; only print survives, at one fifth.
- **Print ghosts, it does not read.** Cream ink at
  ≈ 20 % transmission in a dark bedroom, to an eye that
  has been in the dark or in meditation: the large forms —
  the hero block, the sled's mass, the rail — are present
  as shapes; body text and figures fall below comfortable
  reading. That is exactly the veil's contract: *there is
  a Deck, it is covered, it is not yet yours to read.*
  He can orient (this is the app, not a dead phone, not a
  stranger's screen) without being handed a single
  decision before his first deliberate touch.
- **At 0.65 the veil fails as a veil.** Transmission
  35 % roughly doubles the print's absolute light; at
  that level the night Deck's 13–15 sp lines are legible
  at arm's length. He would read — and start deciding —
  THROUGH the veil, and the "first touch" would unveil a
  room he has already used. A dimmer is not a veil.
- **At 0.90+ the veil fails as a signal.** Transmission
  10 % kills the ghost; the surface becomes
  indistinguishable from screen-off, from Samt's yielded
  black, from a dead phone. A man surfacing at 03:30 who
  meets undifferentiated black will press the power key
  or raise-to-wake against a screen that is already on —
  the machine return's one message ("I turned the light
  down for you; touch when ready") is lost, replaced by
  a fault he has to diagnose half-awake.

0.80 is the only value in the grammar where
covered-but-present reads. It also composes correctly
with the Landing beneath it: 0.80 → 0.45 is a step of
0.35, large enough to feel as an unveiling, small enough
that the Landing still lands as *dim* — the two states
are distinguishable by feel alone, in the dark, without
looking. KEEP 0.80, named as a tuned constant, not a
round number that happened to ship.

One condition on the acquittal: 0.80 is right OVER THE
NIGHT DECK. It is calibrated to a dark scheme under it.
If a future look gives the Deck a light daytime ground,
the same 0.80 would leave a bright grey field standing
in the dark — the weight is a property of the pair
(scrim × scheme), and any look that changes the Deck's
night ground re-opens this number. Recorded for the
looks programme; no action in this merge.

**Keep-awake / OS timeout — acquitted.** The veil does
not hold the screen on: `_deskClockKeepAwake` is cleared
by the ticker once no session stands (the wave-2
visible-with-no-session fix), and the veil Box itself
carries no window flag. An untouched veiled Deck darkens
on the OS's own timeout, from the stand-down, exactly as
E-1 prices it. The veil asks for no extra minute of
light. Correct.

---

## §3 · THE ARRIVAL — is it free of flash and face?

V6 is STRUCK and stays struck: nothing in this slice may
be a face. Verified in the current code: the veil is a
flat, featureless black field — no gradient, no figure,
no differential motion, no fade-in character
(`MainActivity.kt:820–827`). Its choreography is light
levels only. Nothing to cure on the V6 axis, and every
cure proposed in this file is constrained to keep it so:
a single uniform alpha moving between states is a light
level, not a face; any cure that introduced a centred
element, a staged reveal, or a two-part motion would
breach the strike and is rejected in advance.

**The designed arrival is covered — and mostly works.**
On the witnessed night the sequence is: E0 sets the veil
(`VM:1573`) in the same frame the finish exit begins;
the desk clock's print fades 260 Curve A while its true-
black ground HOLDS, the ground itself fading 260 Curve A
at +1,200 ms (`AirlockAndDisarmOverlays.kt:262–283`).
Under that held black, the veil's own appearance is not
an event: black arrives over black, and what the ground
finally lifts off is a Deck already wearing 0.80. That
is the audit's farewell shape, and on the charger night,
clock standing, it is what the code does. Acquitted —
with the two exceptions below, which are the arrival's
only uncovered moments.

**H-2 (MEDIUM) — The veil is composed OVER the clock, so
at E0 it cuts the print it was meant to sleep under.**
Host z-order in `MainActivity.kt`: the desk-clock
overlay composes FIRST (its ground + print), the Landing
and veil Boxes compose AFTER it (MainActivity:813–827) —
later in the Box is higher. The veil therefore does not
sit on the Deck under the clock's ground; it sits on top
of the whole stack, including the dawned print. In the
frame `_nightVeil` flips true, every still-visible clock
pixel is multiplied by 0.20 in one step — the print's
ruled 260 ms Curve A fade runs underneath a scrim that
has already taken 80 % of its light in 0 ms. A man who
wakes at 03:29 and watches the dawn (Daemmer's audience:
the ramp's whole point is that this minute is watched)
sees the face snap five-sixths dark at E0, then fade
the remainder — a cut wearing a fade's clothes.
CURE (exact): compose the veil + Landing BELOW the
desk-clock overlay in the host Box — move the two Boxes
(MainActivity:813–827) above the
`AlwaysOnDeskClockOverlay(...)` call. No state, no
timing changes: while the clock stands, its opaque
ground covers the veil completely; the veil becomes
visible exactly when the ground lifts (+1,200 + 260),
already at 0.80, over the Deck alone — the designed
reveal, obtained by z-order instead of by luck. When
the clock is fully gone its overlay returns early
(alphas ≤ 0) and composes nothing, so the veil's touch
target is unobstructed in every state where it should
fire. ZERO–SMALL (one block move). Proof: §6(b).

**H-3 (MEDIUM) — DAE-4's remainder: a cold discovery
veils late, stale, and UNCOVERED.**
The controller's branch-U law (`VM:1505–1532`): a
stand-down row first sighted at any age reaches
`onFinishE0(row, kind, witnessed = false)` via the
`receiptCliffExempt` branch — filing at any age is
truthful for the RECEIPT, and A-10(iv) is not challenged
here. But the veil is set on that same call,
unconditionally (`VM:1573`, before the witnessed split).
Concretely: process dies in the night; he opens the app
at 05:40 after bath and meditation — Moench's realistic
first open, in morning light, Deck already composed and
being looked at — the history collector's first emission
discovers the stand-down and the veil snaps on OVER THE
LIVE DECK, in his hand, in daylight: an 80 % darkening
with no clock ground ever having covered its arrival,
for an act that ended two hours ago. Every protection
the designed arrival has (covered, at night, at the
act's own instant) is absent on this path, and this is
the path a killed process guarantees. Worse, the
in-memory veil is not persisted (`VM:330`, a bare
`MutableStateFlow(false)`), so the cold path is not an
edge case — it is the only way a restarted process can
veil at all, and it always veils late over a lit Deck.
CURE (exact): gate the veil on the return being a
return. In `onFinishE0`, set `_nightVeil` only when
`witnessed` is true (the machine performed the finish
in this process — the covered arrival exists), OR when
the discovery is fresh: `witnessed == false &&
nowWall − windowEndMillis ≤ 30 min`, in which case the
veil must be seeded BEFORE the Deck's first frame of
the new process (set it in the finish controller's
first-emission handling, which already runs before he
can have read the Deck as a morning surface — if
Wacht's merge finds that ordering cannot be guaranteed,
the fresh-cold case drops to no-veil rather than a
late veil: a missing veil costs a dimming; a late veil
costs a blackout in his hand). Past 30 min cold: no
veil. The record (the filed receipt) still files at any
age — A-10(iv) untouched; only the garment expires.
SMALL (one condition at VM:1573 + the seeding order).
Proof: §6(c).

---

## §4 · ONE SCRIM OR TWO? — the unveil

**Two. And their sum goes UP in light at the touch.
This is DAE-6, verified standing in the current code,
named a remainder (not cured) in the wave-2 report's
addendum, and it is this slice's headline defect.**

The composition today:

- The VEIL is a boolean Box — black at a literal 0.80,
  composed or gone, no animation of any kind
  (`MainActivity.kt:820–827`).
- The LANDING is a SEPARATE Box driven by
  `animateFloatAsState` — 0 → 0.45 over 520 Curve C
  (`MainActivity.kt:273–280`, composed at :813–819).

At the unveil tap, `unveilNightReturn` (`VM:380–385`)
flips both in the same frame: `_nightVeil = false`,
`_deckLandingDim = true`. The veil's 0.80 therefore
vanishes in ONE FRAME while the Landing's dim starts
at 0.00 and climbs the Curve C tween. Curve C
(0.3, 0, 0.8, 0.15) is back-loaded — its output stays
low through the early tween — so the composite
transmission runs:

```
t = −1 frame   0.20   (veiled: 1 − 0.80)
t = 0          1.00   (veil gone, Landing dim ≈ 0)   ← full Deck light
t ≈ +130 ms    ≈ 0.95 (dim ≈ 0.05)
t ≈ +260 ms    ≈ 0.80 (dim ≈ 0.20)
t = +520 ms    0.55   (Landing: 1 − 0.45)
```

For roughly the first 300 ms of the first touch of the
morning, the Deck stands at or near FULL light — a
pulse of brightness delivered to a dark-adapted eye at
03:30, by the very gesture whose entire purpose is to
not do that — before settling to the Landing's level.
The composed grammar (DESIGN §10) is one descent of
light: 0.80 → 0.45 → (verb) → 0. As merged it is
0.80 → ~0 → 0.45 → 0: a blink inside the farewell.
A fade that overshoots its own target is worse than no
fade; this one overshoots to the maximum the screen has.

**H-1 (HIGH) — The unveil flashes (DAE-6, still open).**
CURE (exact — Daemmer's, adopted whole, it was already
the right shape): ONE scrim, ONE animated value, THREE
targets. Delete the boolean veil Box and the separate
Landing animator; compose a single full-screen black
scrim whose alpha is one
`animateFloatAsState(tween(520, EasingC))` driven by a
VM night-return phase:

```kotlin
enum class NightReturn { OPEN, VEILED, LANDING }
// scrim alpha: VEILED → 0.80f · LANDING → 0.45f · OPEN → 0f
```

The veil's touch target rides the same Box while the
phase is VEILED (the Box consumes touch only in that
phase; in LANDING and OPEN it carries no pointer
modifier, so the Deck's controls stay live — see §5).
Every transition of the return then moves monotonically
in light: 0.80 → 0.45 over 520 Curve C at the unveil,
0.45 → 0 over 520 Curve C at the bloom, and the
invariant can be stated and tested in one line: **after
the stand-down, the light never goes up except at his
verb.** SMALL–MEDIUM (host recomposition + a VM enum
replacing two booleans; `unveilNightReturn` and the
bloom become phase writes). Proof: §6(a).

Note what the cure does NOT do: it does not fade the
veil IN (arrival stays a covered snap, §3), it does not
add any element, gradient or staging (V6, §3), and it
does not change either weight (§2) or either duration
(520 Curve C is the ratified bloom curve, §10 — the
unveil leg inherits it because the grammar has one
curve for light returning, and inventing a second would
make the two legs feel like two mechanisms).

---

## §5 · TOUCH BEHAVIOUR — what a touch on the veil IS

The veil's touch contract, as specified: first touch
unveils; the Landing's controls are live from that
touch; the first true verb blooms. The current code's
contract differs in four felt ways.

**H-4 (MEDIUM) — The unveil waits for a completed tap;
the spec's unit is a touch.**
The veil Box uses `.clickable { }`
(`MainActivity.kt:825`). A click fires on UP, after the
press completes inside the bounds. So the half-awake
first contact — thumb lands, rests a beat, lifts — holds
the veil through the entire press (80–200 ms of contact
that feels unanswered) and then unveils at release.
Worse, a first contact that MOVES (a thumb-drag, the
start of a scroll, a palm shift) never becomes a click
at all: the veil survives the touch that was supposed
to end it, and he must touch again, deliberately, at
the moment he is least deliberate. "Until first touch"
(E-1's words) means DOWN. CURE (exact): the single
scrim of H-1 takes its VEILED-phase input from
`pointerInput { detectTapGestures(onPress = { … }) }`
(or an equivalent down-handler): the phase flips to
LANDING on the first pointer DOWN, the animation starts
under his thumb, and the same press's release lands on
the already-unveiling Deck. Any down — tap, drag-start,
palm — unveils; there is no gesture vocabulary to learn
at 03:30. SMALL (folds into H-1's recomposition).

**H-5 (LOW) — The veil's tap carries a ripple.**
`.clickable` with no `indication` argument takes the
theme's default indication: on press, a light ripple
spreads from the fingertip across the black field — a
small pale bloom of light ON the veil, at the exact
touch whose job is to lower the light, visible for the
press's duration before the click fires. It is the only
light event in the whole return grammar that nobody
specified. CURE: `indication = null` on the veil's
clickable — or, under H-4's cure, no indication exists
to remove (a raw pointerInput draws nothing). ZERO,
folded into H-1/H-4 either way.

**H-6 (MEDIUM) — DAE-7, still open: the bloom fires on
ANY interaction, not on the first true verb.**
`noteUserInteraction` clears `_deckLandingDim`
unconditionally (`VM:358–362`, the clearing write at
:361), and the Activity calls it from
`onUserInteraction` (`MainActivity.kt:79–82`) — which
fires for ANY touch-down anywhere AND for key events.
After the unveil, the Landing's protection therefore
lasts exactly until the first contact of any kind: a
brush rolling over in bed, an accidental palm, the tail
of the unveil tap's own gesture train, a volume-key
press in the dark — each blooms the Deck to full light
over 520 Curve C with no verb ever chosen. The code
says so itself, honestly (VM:356–357: "the hook cannot
distinguish a true verb from any other touch") — wave-2
fixed the comment, not the behaviour. The Landing at
0.45 is the morning's eye-shade; spending it on a brush
is the Landing failing at its one job.
CURE (exact — Daemmer's, adopted): stop clearing the
dim in `noteUserInteraction` (the stamp and the wake
path stay); clear it from the verb handlers —
`navigateTo`, PARK's open, the arm/chip verbs — via one
`bloomNightLanding()` on the VM (under H-1: a phase
write LANDING → OPEN). The bloom then means what §10
says it means. SMALL. Proof: §6(d).

**H-7 (MEDIUM) — DAE-5, still open: the Landing exists
only behind the veil; a night hand-dismissal lands on
the full-light Deck.**
`_deckLandingDim` has exactly one writer:
`unveilNightReturn` (`VM:383`). `toggleAlwaysOnDeskClock(false)`
(`VM:1955` ff.) — the hand dismissal, the tap on the
clock's own face — sets no dim, at any hour. The man
who taps the room away at 02:00 gets the 180 Throw into
a Deck at full light: Fen's original charge (the tap's
object is the exit, not the light), cured for the
machine's return and left standing on the path he
drives himself. DESIGN §1.10 promises the Landing for
the night dismissal by name.
CURE (exact): in `toggleAlwaysOnDeskClock(false)`, when
the dismissal is a night dismissal — a session live, or
the dismissal falls in the same night the room was
standing for (the VM has the session and the settle's
stamps; if Wacht wants one discriminator for veil +
Landing, the SLEEP-span-live test is the honest one:
the Landing is the room's light law, and it applies
when he dismisses THE ROOM, i.e. while a session
stands) — set the night-return phase to LANDING (under
H-1) / `_deckLandingDim = true` (without it). The host
already blooms it away on the verb (H-6's cure) or, as
merged, on the next interaction. SMALL. Proof: §6(d).

**What touch gets right — acquittals.**
- The unveil tap does NOT bloom: `onUserInteraction`
  fires before the click handler, reads
  `_deckLandingDim == false` (the Landing does not exist
  yet), and clears nothing; the bloom is reserved for a
  LATER interaction. The two-touch grammar (touch 1
  unveils, touch 2 blooms) is correctly sequenced in the
  merged code — H-6 is about WHAT touch 2 may be, not
  about touch 1 leaking into it. Acquitted.
- `unveilNightReturn` is idempotent and guarded
  (`VM:381`): a double-tap cannot double-fire or skip
  the Landing. Acquitted.
- The Landing Box consumes NO touch (plain Box,
  `MainActivity.kt:813–819`): Deck controls are live
  under the dim, exactly as §10 rules ("every control
  live from the first touch"). H-1's cure preserves
  this: the single scrim consumes input in VEILED only.
  Acquitted — and named, because a naive single-scrim
  cure that left the scrim clickable in all phases
  would silently kill it.
- The veil swallows Deck input while it stands: no tap
  can reach a Deck control through 0.80 of black and
  fire a verb he has not seen. The first touch is spent
  on the unveil, never smuggled into an action.
  Acquitted.

---

## §6 · PROOF DEBTS THIS SLICE ADDS (to audit §7)

(a) **The unveil, photographed** (Daemmer §5(c), still
owed, now the merge's debt): frames at tap +0 / +130 /
+260 / +520 ms on the designed night (charger, SLEEP
stand-down, dark room). With H-1 cured, the Deck's
measured transmission must NEVER exceed the Landing
level (0.55) at any frame, and must be monotonic
non-increasing in scrim alpha across the sequence.
Any frame above 0.55 transmission fails the merge on
sight — that frame IS H-1.
(b) **The witnessed stand-down arrival**: frames at
E0 / +130 / +260 ms with the man awake at 03:29 (dawn
complete, print at standing alpha): the print's fade
must trace the 260 Curve A shape unaided — no frame in
which the print's luminance has dropped by more than
the fade's own value at that offset (H-2's test).
(c) **The cold morning**: process killed before 03:30,
first open at +2 h. The Deck's first frames must show
either a veil already standing at frame one (fresh-
cold seeding, if Wacht keeps it) or no veil at all —
never a veil arriving over a composed Deck (H-3's
test). Plus the boundary case at +20 min cold, same
rule.
(d) **The Landing's two doors**: (i) a night hand-
dismissal at 02:00 must land at 0.45, not at full light
(H-7); (ii) on the Landing, a non-verb contact (a drag
across empty Deck, a volume key) must leave the dim
standing, and the first verb (a rail stop, PARK, the
arm) must bloom it over 520 Curve C (H-6).
No veil test exists in the tree today (grep of
`app/src/test` for veil/unveil: zero hits) — debts (a)
and (c) should land as host-level frame captures, not
unit tests; the phase machine of H-1, once pure, takes
the monotonicity assertion as a unit test beside them.

---

## §7 · THE FEEL SPEC — the veil, whole (for the builder)

| Beat | Value | Timing / curve | Touch |
|---|---|---|---|
| VEILED (machine return) | scrim α **0.80**, transmission 0.20 | enters with NO animation — covered snap under the finish ground (H-2's z-order); stands indefinitely; keep-awake clear, OS timeout takes the screen dark if untouched | — |
| Gate | kind == ROUTINE_SLEEP_STANDDOWN only (C-22, verified) AND fresh: witnessed in-process, or cold discovery ≤ 30 min seeded pre-frame; never late over a lit Deck (H-3) | set at E0, under the ground | — |
| UNVEIL leg | 0.80 → **0.45** | **520 ms, Curve C** (0.3, 0, 0.8, 0.15), single animated value (H-1) | starts on pointer **DOWN**, any contact (H-4); no indication, no ripple (H-5); consumes the touch whole — no verb fires through the veil |
| LANDING | scrim α **0.45** (Deck at 0.55 light — the room's light law) | stands indefinitely; no timeout, no decay | consumes NOTHING — every Deck control live under the dim |
| BLOOM leg | 0.45 → **0** | **520 ms, Curve C** | first TRUE VERB only — navigate / PARK / arm / chip handlers via `bloomNightLanding()`; never `onUserInteraction`, never a brush or a key (H-6) |
| Hand-dismissal entry | night dismissal of the standing room enters at LANDING (0.45), never at full light (H-7); day dismissal enters OPEN, Nacht A-N3 untouched | the dismissal's own 180 Throw delivers him onto the dim | — |
| Invariant | after E0, composite light NEVER rises except on the bloom leg, and the bloom leg starts only at a verb | monotonic in scrim alpha across VEILED → LANDING | — |
| V6 guard | one uniform field, one alpha, no element, no gradient, no staging — choreography, never a face | — | — |

Weights: 0.80 / 0.45 / 0 — all three already ratified or
derived (0.45 = the complement of the room's 0.55 light
law); this spec changes no weight and no duration. It
changes the NUMBER OF OBJECTS (two → one), the gate's
freshness, the z-order, and which touches count.

## §8 · PRICE SUMMARY

H-1 SMALL–MEDIUM (one scrim + VM phase enum) · H-2
ZERO–SMALL (one block move in the host) · H-3 SMALL
(one condition + seeding order) · H-4 SMALL (folds into
H-1) · H-5 ZERO (folds into H-1/H-4) · H-6 SMALL (one VM
function + verb call sites) · H-7 SMALL (one phase
write on the dismissal path). No DB change, no new
surface, no new weight, no new duration, no verb added
to any face. Cured and NOT re-filed: C-22's kind-gate
(§1), the two-touch sequencing, the Landing's live
controls, the keep-awake surrender (§2). Named
remainders from Daemmer that this slice confirms still
stand, in my numbering: DAE-6 = H-1, DAE-5 = H-7,
DAE-7 = H-6, DAE-4(b/c) = H-3.

---

## CLOSING — THE ONE FEEL DECISION I DEFEND HARDEST

**The return is ONE scrim with three targets, and its
light is monotonic: after the stand-down, the light
never goes up except at his verb (H-1).** I will defend
this against the two-box composition even though each
box, read alone, matches its spec line — the veil IS
0.80, the Landing IS 0.45 at 520 Curve C — because the
man never meets the boxes; he meets their SUM, at his
first touch, with dark-adapted eyes, at the worst
possible second. A composition that delivers a ~300 ms
pulse of full Deck light inside the unveil has not
approximately implemented the grammar; it has
implemented its opposite at its centre and its letter
at its edges. Every other defect in this file is a
wrong door or a wrong trigger. This one is the veil
itself flashing the man it was hung to shade — and it
is one animated value away from being the best return
in the building: 0.80, a touch, 0.45, a verb, the day,
the light only ever moving the way he moves it. Hang
it as one piece of cloth or do not call it a veil.

— Hauch, Feel Specialist 1 (the veil), Wacht's cell,
Oct 30, 2026. No commit, no push, no post: Wacht carries
this to the repo. Next seat inherits H-1 as the cure the
whole slice hangs on — H-4/H-5 fold into it, and §6(a)
is its proof or its refutation.
