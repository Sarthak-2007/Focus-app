# PIA — Beyond the Deck, from the plate side
## Wild, watches · Phase 3 · the handoff nobody had specced

**My surfaces at this pass:** the Rigid Minute Plate (AOD desk clock)
and the running Deck. **My assignment in this pass:** the Deck pass
begins one surface too late. Joss draws the primed Deck at 04:58 —
but at 04:58 the phone on the desk is showing *my* plate: dimmed,
on true black, under the 0.25 ceiling. The first thing he sees on
the most important morning of the app is not the Deck. It is the
plate *becoming* the Deck. That becoming is specced here, in full,
plus the plate's one lesson for Aldo's total, plus the horological
idea I held back in my seat file as too much.

**Locked foundations honoured, not re-argued:** AOD ground true
black `#000000` · AOD hero H1 = Sora 700 **104sp**, tracking −3.0,
the only 104 in the app · staged dimming Stage 0 lum 0.215 (0–20
min) → Stage 1 0.174 (20–50) → Stage 2 0.130 (50+), all under the
0.25 ceiling, transitions on a minute boundary · the 12-anchor
patrol (4×3 grid, ±36dp × −64/0/+64dp, ≤1 grid-step per minute, all
12 anchors before any repeat, ≥24dp from every edge) · Mara's
Frozen Specular on AOD (one static catch hairline, same 315° sun,
nothing travels) · S0 — absolute silence on AOD, audio and haptics
both · the desk clock keeps the screen on and shows elapsed +
remaining, shifting position against burn-in. My filed concession
stands verbatim on the running display and on the plate: during a
break the demoted line declares `BREAK · 9:41` — a break is never
rendered as focus, on either face, at any luminance in this file.

One correction to my own seat file, recorded so nobody builds from
it: my filed plate ran the hero at 132/112sp Anonymous Pro. Phase 2
charged those sizes as ramp violations and the Designers'
settlement replaced them — 104sp Sora is law, my 132/112 are dead.
Everything below is built on the settled plate, not my first draft
of it.

---

## 1. THE HANDOFF — STEP, not ramp: the plate climbs a staircase and hands the eye to the Deck

### The beauty case, in plain words

At 04:58 his eyes are the least adapted they will be all day. He
has sat in the dark for an hour. The plate is a candle across the
room. If the phone answers his touch by jumping to a lit Deck, the
first experience of Monolith every morning is a small act of
violence — and he will start flinching from the app before he has
ever decided anything about it.

A clock does not brighten by sliding. Nothing in a good instrument
sweeps. So the wake does what the minute does: **it steps.** The
light climbs a short staircase — five landings, each one held long
enough for the eye to arrive — and on the third landing the plate
lets go and the Deck is simply *there*, at the same light, in the
same room, lit by the same sun. He never sees two clocks. He never
sees a flash. He watches night become morning at the speed a pupil
can follow, in the only motion grammar his clock has ever used:
stillness, then a step, then stillness.

The reverse is the same courtesy at the other end of the block:
when the desk clock takes over, the light walks *down* the stairs,
faster, because descending light never hurt anyone — and the plate
is waiting at the bottom, already telling the truth.

### What state the plate is in at 04:58 — stated honestly

Two entry states exist, and this spec covers both with one ramp:

- **P1 — plate open, no session sealed.** The morning's actual
  case (Humans §3: nothing is sealed, nothing is counting at
  04:58). The plate stands in its no-session stance: time of day
  as its quiet fact, session readings absent — not zero, *absent*.
- **P2 — plate open, closing out a prior sealed block** (a
  meditation block, an early block). The plate carries that
  block's REMAINING (104sp hero) and ELAPSED (50sp), its seam, its
  demoted lines — including `BREAK · …` if the block is in a break.

The custody rule is identical in both, and it is the heart of this
item: **every reading on the plate dies at the handoff. Nothing
transforms into the Deck's total, and nothing persists beside it.**
The plate's remaining is a MEASURED fact about time already
committed; the Deck's `3H 00M` is a CHOSEN/COMPUTED fact about
time not yet begun (Aldo's classes). They are different facts with
different homes, and for the length of this handoff they are never
readable in the same frame. What survives is not a fact at all —
see "What carries over" below.

### The wake gesture — no new gesture is invented

- **Door A (plate in foreground, screen on, keep-screen-on
  active):** one tap anywhere on the black. There are no controls
  on the plate to miss, which is why a weak 04:58 hand cannot fail
  this. A tap = contact and lift; a brush that never lifts does
  not register. Pickup alone is **not** a wake: pickup earns only
  the filed 10s task-line transient, at plate luminance, ceiling
  intact.
- **Door B (device locked over the plate):** the system's own
  unlock — biometric or swipe, whatever he already uses.
  Monolith adds no gesture, no second swipe, no PIN of its own.
- A minute tick, a notification, a patrol step: never a wake.
  The plate does not brighten itself for any reason in this spec
  except Item 3's strike, which is capped far below wake light
  and is not this ramp.

### Technique — token STEP 1400 (forward), STEP-DOWN 700 (reverse)

I am arguing a new transition token to Wren by mail, and stating
the argument here so the file carries it: Wren's grammar has no
word for a *luminance-state* change. RACK 280 moves focus between
peer screens at equal light; VERTIGO 1050 seals a session in the
hand at full light; neither may govern a surface leaving the 0.25
ceiling — a RACK executed at AOD-to-Deck luminance delta *is* the
flash. **STEP** is proposed as the fourth token, used exactly
twice in the app's day (wake, desk-clock takeover), never for
navigation, never between peers. It does not replace RACK — it
*wraps* one: the middle 280ms of the forward STEP is a true RACK,
executed at the one plateau where the two surfaces' luminances
are close enough for defocus to read as focus instead of glare.

**Forward — STEP 1400.** One `Animatable<Float> stepT` drives
brightness plateaus, defocus, and composition removal, so the
shot cannot desync (Wren's one-flow rule, inherited).

Luminance plateaus — relative luminance of the brightest glyph,
held, then stepped. Steps, never a continuous ramp (a ramp is a
sweep; this surface does not sweep):

| Plateau | Window | Brightest-glyph lum | Window brightness |
|---|---|---|---|
| P0 | t 0–200 | L₀ = current stage (0.215 / 0.174 / 0.130) | 0.35 cap, as filed |
| P1 | t 200–400 | 0.28 | 0.45 |
| P2 | t 400–600 | 0.42 | 0.58 |
| P3 | t 600–1000 | 0.58 → Deck night values take over at P4 | 0.72 |
| P4 | t 800–1000 | 0.78 (Deck tokens at this brightness) | 0.88 |
| P5 | t 1000–1400 | full Deck night: glyph `#EDE3D8`, 14.48:1 on `#171412` | his own setting, restored |

Each step is ≤ ~1.5× the plateau before it; each landing is held
≥200ms. No plateau uses an interpolated hex (H-7): plate tones
are the plate's locked tones, Deck tones are the Deck's locked
tokens (Sana D1/D9) — it is the *window brightness* that steps,
never the paint. The ground makes this work: the plate's ground
stays true black until the plate is removed at t 800, and the
Deck's `#171412` first appears already dimmed by the P4
brightness plateau, so the ground's step is perceived as one
plateau among five, not as the room's lights coming on. The 0.25
ceiling binds until wake intent is confirmed at t 200; after
that the surface is by definition no longer AOD, and the ceiling
no longer applies to it. That sentence is the ceiling's own
logic, stated so no auditor has to guess.

**Who owns the eye, each 100ms** (forward):

| t (ms) | Owner | What is true in that window |
|---|---|---|
| 0–100 | Plate, alone | Tap registered. Nothing changes. Intent hold — an accidental brush must not be able to spend light. |
| 100–200 | Plate, alone | Still P0. The plate is given one beat to be woken *from*, at its own light. |
| 200–300 | Plate, alone | Step to P1. Plate steps up whole — hero, ELAPSED, seam together, internal geometry frozen as always. |
| 300–400 | Plate, alone | P1 held. Frozen Specular hairline still at its anchor-relative position; the light's source has not moved, only its strength. |
| 400–500 | Plate, yielding | Step to P2. The plate begins the RACK core: blur 0→6dp, alpha falling. **Its readings die here — blurred first, removed second, never readable alongside anything.** |
| 500–600 | Plate, as silhouette | Blur completes. The Deck composes *beneath*, at P2 brightness, still defocused. The eye is still held by the plate hero's larger, brighter silhouette — that is correct; it is the last thing the night owned. |
| 600–700 | **The transfer** | RACK core, first half: Deck primary plane (the readout — Joss's `3H 00M`, Sora 700 72sp, glyph tier) sharpens from 9dp blur at t 600. Crossover: at ≈ t 650 the Deck total becomes the brightest sharp object in the frame. From here the Deck owns the eye. |
| 700–800 | Deck | RACK core completes (≈ t 880 by Wren's internal clock; the core runs 600–880). Secondary plane (wheel, at rest, demoted) sharpens at +50ms, tertiary (NEXT line, task line) at +100ms, per Wren's offsets. **Plate composition removed at t 800** — not faded to a ghost, *removed*; there is no after-image of the old readings. Landing tick 6ms @ 0.35 at ≈ t 830, when incoming blur passes 1dp (Wren's tick, at Wren's moment, scaled into the core). It is the first haptic of the day and it belongs to the Deck — the AOD's own silence is unbroken, because by t 830 the AOD no longer exists. |
| 800–900 | Deck, alone | Step to P4 (0.78). **The slide is live from this frame.** Chrome hard-cuts in at P4 — nameplate and rails are body, never dissolved (Wren's body law); they appear at plateau brightness, engraved labels do not fade. |
| 900–1000 | Deck, alone | P4 held. Full primed frame readable: one number, one word-line, pips, wheel at rest, START. |
| 1000–1100 | Deck, alone | Step to P5: window brightness reaches his own setting; AOD's 0.35 cap and keep-screen-on state are released in the same frame, in the same dispose step that filed them — no leak in either direction. |
| 1100–1400 | Deck, alone | Settle: the RACK landing hunt (scale 1.0 → 0.9985 → 1.0) stretched across the final plateau, then absolute stillness. The primed frame at t 1400 is pixel-identical to the primed frame at t 4000 (Item 2's stillness test applies from here). |

**Sound:** none, at any point. S0 covers the plate absolutely;
the Deck's wake invents no sound to celebrate escaping it.
**Haptics:** exactly one — the RACK landing tick above.
**Anchor behaviour:** the plate is frozen at its current anchor
for the whole 1400ms. A patrol step that falls due inside the
ramp is *owed, not performed*: anchor index is a pure function of
the session's elapsed-minute count (index = minute count mapped
through the fixed 12-anchor order), so when the plate next exists
it arrives at the anchor that minute owns. No anchor is skipped
in the count and none repeats early; the patrol's law is
arithmetic, not a timer that can be interrupted. Moving the plate
during the ramp would smear the one object the eye is holding —
the plate dies where it stands, like everything else on it.

**What carries over** — the complete list, and it contains no
fact: (1) *the sun.* Mara's frozen hairline on the plate and the
Deck's chamfer arc (Sana D2, 250°→350° through 315°) are the same
fixed source at two strengths; the room does not change when the
surface does. (2) *The state underneath.* Session entity, seal
state, routine prime — untouched; STEP is a rendering event only.
(3) *Darkness, briefly.* True black remains the ground until
t 800, so the Deck is born out of the plate's element, not out of
a grey intermediate that belongs to neither surface.

**Reverse — STEP-DOWN 700** (running Deck → desk clock, after a
session is sealed and he sets the phone down / taps the filed
desk-clock entry on the running Deck):

Descending light cannot flash, so the reverse is shorter, and it
is *not* a mirror: 700ms, three plateaus, eye ownership —

| t (ms) | Owner | What is true |
|---|---|---|
| 0–100 | Running Deck | Tap registered; nothing changes. One beat against an accidental set-down brushing the surface into darkness. |
| 100–300 | Running Deck, yielding | Plateau 0.42. RACK-reverse core (280ms, from t 100): running readout defocuses; nothing else on the Deck moves — the wheel does not turn for a goodbye. |
| 300–400 | The transfer | Plateau Stage 0 (lum 0.215). The plate composes at the anchor the current minute owns (never anchor zero — the patrol does not restart for a homecoming), at Stage 0 regardless of how long the session has run: **descent always lands on Stage 0**, and the staged-dimming clock starts from this entry. |
| 400–700 | Plate, alone | Deck composition removed at t 400. Window brightness cap 0.35 and keep-screen-on are set in the same frame the plate lands. P0 held; first patrol step at the next minute boundary, not before. |

**Custody in reverse is the opposite of forward — and must be.**
Forward, the readings die because a *new* fact (the total) is
being born. Reverse, nothing is born: REMAINING and ELAPSED
**survive**, transferring home from the running Deck to the
plate, because they are the same session's same facts and my
seat's first law is that the plate shows no fact the Deck does
not own — and lets go of none that it does. The transfer obeys
the settled per-surface forms without apology: hero REMAINING
re-renders in the AOD's form (Sora 700 104sp, `H:MM`) from the
running Deck's form — format differs by surface, as Designers
§6.2 states as law, not drift — ELAPSED takes the 50sp second
reading, progress becomes the 120×3dp geometry, the demoted
`BREAK · …` declaration, if the session is in a break at that
moment, lands in the plate's demoted line *verbatim*. During the
100–400ms defocus window neither rendering is readable, so for
zero frames do two copies of remaining exist in legible form.
**Haptics: none. Sound: none.** The descent ends inside S0; the
last permitted sound of the session's waking life is whatever
preceded the tap. A landing tick on arrival would be the AOD
buzzing the desk in its first second — filed as a bug in my seat,
still a bug.

*Boundary case, named so it is not improvised later:* if the
session **finishes** while the plate is open, STEP-DOWN does not
apply at all. The filed finish governs: hero holds `00:00` a full
1000ms, the one 60s step up to Stage 0 tones (Designers §6.2),
plate dismisses itself, brightness and keep-screen-on restore,
and the Deck's single receipt is the record. A finish is an exit
to a receipt, not a takeover by a clock.

**Reduced motion:** Wren's collapse (RACK → CUT) applies to the
defocus core only, executed at P2–P3 where the luminance delta
between the two surfaces is smallest. The staircase itself is
*not* motion and is never collapsed — collapsing it would
reinstate the flash for exactly the users who asked for less.

### ARM cost — priced in the 04:58 sequence, honestly

The handoff precedes the arm, so it is on the clock:

| Step | Cost |
|---|---|
| Wake gesture (weak hand, dark room) | ~400ms, 1 gesture |
| STEP 1400 to full luminance | 1400ms — **but the slide is live at t 800**; the final 600ms is light, not a lock |
| Read the primed frame (one number, decided rested) | ~500ms, overlapped with the settle |
| Slide past the gate / tap-commit (Humans §3) | ~800–1200ms |
| **Total, cold plate → sealed** | **≈ 3.1–3.5s typical; ≤ 4.5s worst case (Stage 2 start, slow hand, waits for full settle before sliding)** |

**ARM verdict: PASS with > 5s of headroom**, and the honest
fraction is named: STEP adds 800ms of *blocking* time (to a live
slide) and 1400ms of *elapsed* time. I do not claim zero. I claim
it is the cheapest morning available that does not begin with a
flash — the filed alternative is priced below.

### HOLD / TRUTH check

**HOLD: PASS.** The wake seals nothing, mints nothing, records
nothing. If his hand never comes, nothing happened (Humans
Variant C holds through my spec untouched). STEP contains no
control, no prompt, no branch; the slide it delivers him to is
the primed frame's own single verb. **TRUTH: PASS, and it is the
item's reason to exist.** One fact, one home, enforced *across
time*: plate readings are illegible from t 400 and removed at
t 800; the Deck total is not composed until they are already
blur; the two numbers never share a legible frame. The brightness
staircase renders no fact at all — it is light, not data (the
rig's law: light carries no data, §1.4, honoured here — plateau
count encodes nothing, stage encodes nothing new; the staged
dimming underneath remains the plate's own filed behaviour).

### What it replaces, and why that loses

It replaces Designers §6.2's wake sentence — *"unlock lands on
Running/Deck with a night first frame and a 2.5s brightness ramp
from the AOD floor"* — and my own seat's bare "tap anywhere to
exit." The sentence loses on three counts: 2.5s is a duration
with no curve, no plateaus, and no owner — during those 2.5s
*nobody's* spec says which surface holds the eye or when the
slide goes live, which is how merged builds end up rendering
both clocks at once; a continuous ramp is a *sweep*, the one
motion this family of surfaces exists to refuse; and 2.5s of
unstructured ramp is *slower* than STEP's 1400ms while feeling
less deliberate. My filed tap-to-exit loses because it priced
the gesture and ignored the light entirely.

### Strongest case against my own proposal

The staircase's steps are a bet about perception. Five plateaus
at ≤1.5× could, on a panel with coarse brightness quantisation,
read not as landings but as *flicker* — five small flashes
instead of one big one, which is worse than the disease. If the
device trial shows stepping artefacts at P1–P2 (the dark end,
where panels are least linear), the honest fallback is a
two-plateau STEP (P0 → 0.42 → full, 1000ms) — still stepped,
still owned per-100ms — and if even that flickers, a continuous
ramp confined to the brightness channel (never the paint) at
1400ms beats my philosophy, because a smooth ramp he doesn't
flinch from is better art than a stepped one he does. The
removal trial decides, on device, at 04:58 light, not in this
file. Second charge, owned: 800ms of blocking is 800ms — on a
morning where he is late and furious, STEP is the app visibly
taking its time before his most urgent gesture. My answer is
that the slide is live before full light and impatience can
outrun the last two plateaus; if he still experiences it as a
wait, the plateaus compress to 150ms holds (1100ms total) before
any other element of this spec is touched.

---

## 2. THE PLATE AT THE BOUNDARY — one philosophy of the minute, two motions

### The beauty case, in plain words

Stand in front of both faces and you are standing in front of one
conviction: **time does not flow in this app; it lands.** The
plate is still for 59 seconds and then the minute arrives, all at
once, like a stone set down. The Deck's total should feel like
the same stone in a different hand — a number that is *placed*,
holds its place without trembling, breathing, or quietly
re-deciding itself, and changes only when something true happens.
A primed Deck that subtly pulses, re-renders, or counts down to
its own window is a screen asking for attention. A primed Deck
with the plate's stillness is an instrument that has already
been set by a rested man and is simply *ready* — and readiness
that doesn't fidget is what trust looks like at 04:58, on the
two-hundredth morning as much as the first.

**The shared philosophy, in the one sentence required:**
*On both faces, a number changes only when the fact it renders
changes, it changes all at once, and between changes the display
is perfectly, verifiably still — the minute is a place the
display arrives at and holds, never a river it swims in.*

### Technique — what the plate's discipline changes on the Deck

The total is Aldo's Still Witness: COMPUTED class, re-strike —
digits swap in one frame, one 140ms press-impression decay,
never a roll through intermediate values that were never true.
That law stands whole. The plate adds three things to it, all on
the **primed** Deck (the total's longest-lived state, and
currently its least specced):

1. **The primed total inherits cruise stillness, as a test.**
   Between the events that may lawfully change it — wheel detent
   settle, hub press, routine prime, a mid-window restatement
   (Humans Variant B) — the primed frame does not repaint. At
   all. The test is borrowed verbatim from my plate's cruise
   test: screenshot-diff two captures of the primed Deck 5s
   apart; the diff must be empty. This kills, by name: any
   colon pulse on the primed `H:MM` (the colon is a drawn glyph
   at 45% alpha, always — my running-display rule, now extended
   to the primed total), any specular travel across the total,
   any brightness breathing, and any countdown-to-window in any
   form (Humans already forbids the countdown as a demand; this
   files the deeper reason — a countdown would make the total a
   MEASURED number pretending to be a CHOSEN one, a class lie).
2. **Restatement is a jump, sized like the plate's.** When the
   total *does* change on the primed frame (prime lands at
   05:00 under him, or he arrives mid-window), it changes by
   Aldo's re-strike — one frame, impression decay 140ms — and
   then it is still again. The plate's own jump is also 140ms.
   That is not a coincidence to engineer but a rhyme to keep:
   every lawful change of a big number in this app, on either
   face, completes inside the same 140ms envelope, so his eye
   learns one duration for "a number just became true."
3. **The total gets the plate's seat.** The plate is *rigid* —
   internal geometry frozen, the whole object moving only as
   one, on patrol. The primed total takes the numeral-level
   version: its well, its baseline, its glyph slots do not move
   between values. `3H 00M` → `1H 50M` (mid-window) re-strikes
   *in the same seat*; the well does not resize, re-centre, or
   re-settle around the new string. A number that shifts its
   furniture when it changes is a number still deciding.

**Where the boundary is right — where they must differ.**
Stillness is shared; *motion and trigger are not, and must not
be.* The plate's numeral changes because time passed (MEASURED):
it may *jump* — glyph strip translating 140ms with its 2dp
overshoot — because its value genuinely traversed the minute
boundary. The total changes because he chose or arithmetic
concluded (CHOSEN/COMPUTED): it must **never** borrow the
plate's glyph translation, because a total that travels implies
intermediate totals existed. Importing the jump into the total
would be the plate teaching the Deck a lie — so the lesson is
custody of stillness, not choreography. Second difference, kept
deliberately: the plate as an *object* is rigid and tours; the
Deck as an object is neither — its wheel turns under his hand
and its slide travels. The plate is an instrument at rest; the
Deck is an instrument *in* the hand. One philosophy of the
minute, two correct bodies for it. If a future pass tries to
make the Deck rigid or the plate interactive in the name of
consistency, this paragraph is the refusal, filed in advance.

### ARM / HOLD / TRUTH check

**ARM: 0ms, 0 gestures.** Stillness costs nothing; the
screenshot test is a build gate, not a runtime tax. **HOLD:
PASS** — a primed frame that cannot pulse or count down also
cannot nag; it removes mid-decision pressure rather than adding
any. **TRUTH: PASS, strengthened** — the class lie (a CHOSEN
number rendered with MEASURED motion, or a countdown reclassing
the total as time-pressure) is now named and banned on the exact
frame where it was most likely to sneak in wearing the word
"alive."

### What it replaces, and why that loses

It replaces the primed frame's current underspecification —
Humans §3 says "nothing on this frame moves, glows, pulses, or
counts down" as a drawing instruction for one frame. That loses
as a *standing* law because it governs a drawing, not a lifetime:
the primed frame can sit for two hours mid-window, across
recompositions, theme checks, and resolver updates (Joss's Next
Line cross-fades at 120ms when its output changes — that fade is
on the NEXT line, a different fact's home, and stays lawful; the
*total's* stillness needed its own sentence or the first
well-meaning "subtle refresh" would land on the biggest number
in the app). A prohibition drawn once is a wish; a diff test is
a law.

### Strongest case against my own proposal

Absolute stillness has a failure mode: death looks exactly like
it. A frozen app and a disciplined primed Deck are
pixel-identical under my own test — if Monolith ever hangs on
the primed frame, this spec has removed every cue by which he
could tell. My honest answer is that the *interaction* is the
proof of life (the slide answers the thumb, the wheel the hand)
and a hung app fails those too, so no information is actually
lost — but I concede the edge: if a single lawful heartbeat is
ever demanded for the primed frame, it should be Wren's nav
detent behaviour or the hub's press light (D6), something under
his hand — never an idle animation on the total. If the room
rules even that insufficient, Item 2 should be cut back to the
colon and countdown bans, and the diff test dropped, rather
than a pulse added to the number. A living number is not worth
a lying one.

---

## 3. MY OWN BEYOND — THE SILENT STRIKE: the plate counts the hour in light, once, and never in arrears

### The beauty case, in plain words

A great clock in a house is not only read. It is *felt*, once an
hour, from the next room — the strike tells you the shape of the
day without asking your eyes for anything. The plate can do that
in the only voice it is allowed: light. When a sealed session
crosses a whole hour, the plate steps up exactly one stage of
brightness — one only, still under the ceiling, still nowhere
near wake light — and holds it for exactly one minute. Across a
dark room, without focusing, without reading a single digit, he
*feels* the hour turn over: the clock got a breath lighter, the
way a room does when a cloud moves. Then it steps back down and
the night continues. No sound — S0 is absolute and a sleeping
house is sacred. No tally, no numeral, no record. The strike is
not information. It is the session acquiring *joints* you can
feel from the doorway — the same reason my Retrograde Flyback
gives blocks a joint — at the scale of hours instead of blocks.

This is the idea I held back. My seat file's staged quieting
says, in my own words, the clock "never gets brighter on its
own." The strike amends my own law, in the open, by name —
because a clock that can only ever get quieter is a clock
running down, and a session in its third hour should not feel
like something expiring. It should feel like something keeping
hours.

### Technique

- **Trigger:** the live session entity's elapsed time crossing
  a whole-hour boundary (elapsed = 1:00:00, 2:00:00, …) while
  the plate is the foreground surface. Computed from the
  entity, on the minute wakeup that boundary already owns —
  hour boundaries are minute boundaries; the strike costs
  **zero extra wakeups**.
- **Form:** at that minute tick, instead of (or as) the ordinary
  patrol step, the plate's ink steps **up one stage**: Stage 2
  (0.130) → Stage 1 (0.174); Stage 1 → Stage 0 (0.215); Stage 0
  → a strike value of **0.245 — under the 0.25 ceiling, by
  design, with 0.005 of headroom named so no one "rounds" it
  over later.** All elements step together (hero, ELAPSED, seam,
  Frozen Specular hairline), internal geometry frozen, patrol
  position unchanged that minute (the strike spends the minute's
  one permitted change; the patrol step is owed and lands at the
  next minute, per Item 1's arithmetic anchor law).
- **Duration:** exactly one minute — the strike lasts the unit
  it marks. At the next minute tick the plate steps back down to
  the stage the staged-dimming schedule owns at that elapsed
  time. One step up, one minute, one step down. No decay curve,
  no fade: steps, as everything on this surface steps.
  Transition between stages uses the filed 2s crossfade on a
  minute boundary (Designers §6.2 dimming law) in both
  directions — at this scale and delta, the filed crossfade is
  the plate's own grammar for stage changes, and it is *not* the
  continuous sweep banned elsewhere: it is how one stillness
  becomes another, at 0.04–0.07 of luminance, over 2s.
- **Never in arrears:** if the plate was not foreground at the
  boundary (he was in the Deck, phone was locked, STEP was
  running), the strike is *missed*, forever. It is never
  replayed, queued, or made up on return. A clock that strikes
  late is lying about what time it is; a clock that strikes the
  hour only when it is there to be heard is an instrument.
- **Breaks:** if the hour boundary falls inside a safe break,
  the strike **still fires** — it marks elapsed session time,
  not focus — but it fires in the plate's break stance: the step
  applies to the demoted line and seam at their break values and
  the hero (break remaining, dimmed to 60% by filed law) steps
  by the same ratio. The strike never, at any luminance, makes
  a break look like focus: the demoted line still declares
  `BREAK · …`, at strike brightness, which is if anything
  *more* honest — the one minute the room brightens is a minute
  the break is declared most legibly.
- **Finish hour:** a boundary that coincides with session end
  is not struck. The filed finish (hold `00:00`, the one 60s
  step, dismissal) owns that minute entirely.
- **Type / material:** no new element, no new tier, no new
  token. The strike uses only the three filed stage values plus
  the one named 0.245. S0 untouched: no audio, no haptic — a
  buzz at the hour on a desk at 06:00 is a notification wearing
  a clock's clothes.

### ARM / HOLD / TRUTH check

**ARM: 0.** Post-seal, ambient, zero wakeups added. **HOLD:
PASS** — the strike offers nothing, asks nothing, opens nothing;
it cannot be tapped, chased, or farmed. **TRUTH: PASS, with
the seam stated.** Elapsed already has a rendering on the plate
(the 50sp reading). Is the strike a *second* rendering of
elapsed, violating one-fact-once? No — by the same distinction
my chosen form already lives on: the minute jump is not a second
rendering of the minute digits; it is the *event* of the
boundary, where digits are the *state*. The strike is the
hour-boundary rendered as event, once, at the only scale the
room can feel. It renders no value — there is no count of
strikes anywhere, no `HOUR 2` text, nothing to read — so there
is nothing for it to disagree with. The moment anyone proposes
numbering the strikes, this item's own TRUTH check kills it.

### What it replaces, and why that loses

It replaces one sentence of my own seat file: *"it never gets
brighter on its own,"* inside staged quieting. That sentence
loses narrowly and honourably: it was written to stop
notifications and wake-events from hijacking the plate's light,
and as an absolute it also bans the clock's own lawful voice —
it makes monotonic dimming the plate's entire emotional range,
which reads, by hour three, as dying. The replacement keeps the
sentence's intent as law — *nothing but the session's own hour
boundary may ever brighten the plate; no notification, no wake,
no system event* — and carves exactly one exception, computed
from the session entity, capped, and stepped back down by the
plate itself.

### Strongest case against my own proposal

It is real, and it is Muede's hour that makes it: a luminance
step in a dark room is, physiologically, an alert. One stage at
0.04–0.07 luminance is subtle to a waking eye across a room;
to a half-asleep man at his desk at 06:00, mid-block, it may
register as *something changed* — and "something changed" is
the exact itch that makes a man pick up his phone inside a
sealed session, which is the one thing HOLD exists to prevent.
The strike also spends the scarcest resource on this surface —
uneven pixel wear: one minute per hour at a higher stage is a
small but real deviation from the dimming discipline that keeps
the plate's wear even, and it concentrates on whichever anchor
the patrol happens to occupy at hour boundaries. If the device
trial or a single real morning shows the step pulling his eye
to the phone even once, the strike dies entirely — it is an
ornament on a cloister, and the cloister wins every tie. I file
it because a clock with no voice at all is only half a clock;
I file its executioner in the same paragraph.

---

## Summary for the merge

1. **STEP 1400 / STEP-DOWN 700** — the wake is a luminance
   staircase wrapping one true RACK 280; eye ownership specced
   per 100ms; plate readings die blurred at t 400 and are
   removed at t 800, never legible beside the Deck total;
   reverse descent lets REMAINING/ELAPSED survive because
   custody, not novelty, is the rule. ARM price: 800ms blocking,
   ≈3.1–3.5s plate-to-sealed at 04:58, inside ≤10s with >5s
   spare. Replaces Designers §6.2's 2.5s ramp sentence.
2. **One philosophy of the minute** — *"a number changes only
   when its fact changes, all at once, and between changes the
   display is perfectly still."* Total inherits cruise stillness
   (5s screenshot-diff = empty on the primed frame), the 140ms
   change envelope, and an unmoving seat; it does **not** inherit
   the plate's glyph jump — stillness is shared, motion is not.
3. **The Silent Strike** — one stage up (≤0.245, under ceiling),
   one minute, on the hour boundary, zero wakeups, never in
   arrears, never during finish, `BREAK · …` declared at strike
   brightness. Amends my own "never brighter" sentence by name;
   dies on device if it ever pulls his eye to the phone.

Mail sent with this file: Joss (your frame is my destination —
its first 800ms live under my staircase; build it at full token
values, the brightness channel does the dimming, never the
paint) and Wren (STEP argued as a fourth token; also a refusal,
with respect, of GATE WEAVE on my surface — mailed in full).

— Pia, Wild · watches
