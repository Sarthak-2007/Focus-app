# FEEL — THE DRAWER UNDER THE THUMB (touch & haptics)

Feel Specialist 1 — Touch, issue #8 cell (LADE). Source of law:
`studio/plan/AUDIT-drawer.md` (the verdict) and
`studio/work/issue-8/designer-drawer.md` (the finished screen:
search entry line, filing zone, current-month rows, MONTHS shelf,
MONTH view, task DETAIL). This file specifies **feel only** — what
each surface does under the thumb, in the palm, in the dark. No
layout, no print, no colour beyond what press states already
spend (the ruled `#493EA4` violet wash at ~8%). Never green. No
imagery. No cabinet anything — that is issue #21, not here.

Nobody compiled anything. Nothing in this file is a Kotlin change.

Ground truth from the code, so the prices in §7 are honest:
`DrawerScreen.kt` today is stock Compose feel — plain
`.clickable {}` everywhere, default Material ripple, and **zero
haptic calls anywhere in the file**. The house engine
(`TactileSoundEngine`) has a real waveform vocabulary, but every
public voice pairs its haptic with a PCM sound transient, and its
named voices belong to other acts (the seal's Throw, completion,
the latch, the tear). The Drawer borrows the engine's *hands*,
never its *sentences* — see §5, forbidden voices.

---

## 0 · THE ONE LAW OF THIS ROOM'S FEEL

> **Weight is proportional to irreversibility, and rest is silent.**

Three grades of consequence exist on this screen, and the thumb
must be able to tell them apart with the eyes shut:

| Grade | Act | Feel |
|---|---|---|
| Reading | open a row, page EARLIER, go BACK | a **tick** — paper tapped |
| Crossing | pull a month off the shelf | a **detent** — a catch, then a seat |
| Filing | declare REST / ILL / FAMILY | a **thud-stamp** — once, forever |

Everything else — arriving, scrolling, typing, reading DETAIL,
an empty result, a retired line, the room at rest — is
**SILENCE**. An archive that buzzes at rest is grading by
vibration: if the Drawer hums when he merely looks at it, the
buzz becomes a score he can feel in his pocket of attention.
The audit killed every printed total on this screen; this spec
kills the vibrational ones. No haptic in this room ever means
"well done." Haptics here mean only: *your thumb landed, and
this is how much what you just did cannot be undone.*

Corollaries, non-negotiable:

- **One event, one haptic.** The haptic fires at the tap's
  commit moment, from the same call site as the state change.
  Nothing fires again when the destination finishes animating
  in — a landing buzz on arrival is a second, unearned event,
  and it is how navigation starts to feel like applause.
- **No haptic on touch-down for anything inside the scroll**
  (rows, shelf, filing words). A scroll drag begins with a
  touch-down too; down-fired haptics would tick every scroll
  that starts on a row. Inside the scroll, haptics fire on
  confirmed tap commit only (release inside the target, inside
  tap slop). The one exception is BACK — it lives in the fixed
  header, outside the scroll, and fires on down (§4.6).
- **No per-item haptics, ever.** Not per row passed under the
  finger while scrolling, not per result appearing, not per
  row appended by EARLIER (one tick for the tap, none for the
  cargo). A count you can feel is a count.
- **Cancel is silent.** Every press in this room can be
  abandoned — drag off, scroll away — and abandonment unwinds
  the visual press and fires nothing. A cancelled act must feel
  like it never happened, because it didn't.

---

## 1 · THE VOCABULARY — THREE VOICES AND SILENCE

All three voices are new haptic-only patterns on the engine's
existing `triggerWaveform` machinery (API 26+ waveform with
amplitude envelope; pre-26 fallback = the single one-shot in
parentheses). Amplitudes are `VibrationEffect` 0–255 scale, the
same scale the shipped voices use. **Haptic only**: unlike the
engine's public voices, these enqueue **no sound transient**
(§5). A drawer does not click out loud at 1 AM.

### 1.1 TICK — reading

The rotary detent's family, shaded down. A strike that settles:
short, dry, high in the hand.

- Waveform: timings `[0, 7, 5]` ms, amplitudes `[0, 150, 60]`
  (the shipped `playRotaryDetent` shape at 180/70, shaded —
  the Drawer's tick is the quietest version of the house tick
  because reading is the cheapest act here). Fallback: 1 ms
  one-shot (`HAPTIC_ROTARY_TICK_1MS`).
- Total felt length ~12 ms. One per tap commit. Never repeated,
  never doubled for emphasis.
- Earned by: StoredRow → DETAIL, EARLIER, BACK (BACK shaded
  further, §4.6).

### 1.2 DETENT — the drawer pull

Two beats with a true gap: a catch, a breath, a seat. This is
the shelf's voice and only the shelf's (§4.4).

- Waveform: timings `[0, 9, 22, 8]` ms, amplitudes
  `[0, 170, 0, 140]` — deliberately *not* the mechanical
  latch's pattern (215/150 engaging): the latch snaps a machine
  shut; the detent is wood and brass, a drawer coming off its
  runner's notch. Lower first strike, softer seat, same
  two-stage grammar so the hand recognises the family.
- Total felt length ~39 ms. Fires once, at pull commit.
- Earned by: a MONTHS shelf row opening MONTH. Nothing else in
  the Drawer earns a detent. (DETAIL → back is *leaving*, and
  leaving is a tick, §4.6 — the asymmetry is the point: pulling
  a month out costs more than putting it back.)

### 1.3 THUD-STAMP — filing

One strike, low and final. Not a pattern — a pattern can be
counted, echoed, undone in the mind. A stamp lands once.

- Waveform: timings `[0, 14, 6]` ms, amplitudes
  `[0, 235, 85]` — a hard 14 ms strike with a 6 ms paper
  settle, no gap, no second beat. Heavier than the tick and
  the detent's first beat, shorter than both patterns end to
  end: the hand reads *mass*, not rhythm. Fallback: 8 ms
  one-shot.
- It must be unmistakable against the tick in the dark, half
  asleep, first try: the difference is weight and pitch of
  feel, not duration — a sleepy thumb confuses two rhythms, it
  does not confuse a tap with a stamp.
- Earned by: a filing word filing its day. Exactly once per
  day-line, because the line then retires (§4.2) and can never
  earn it again.
- It says "filed, forever." It must never say "score": that is
  why it is a *single* strike and why §5 forbids borrowing the
  completion pulse's three softening ticks — three ticks that
  wind down is the machine congratulating him; one thud is a
  stamp hitting paper and the matter closing.

### 1.4 SILENCE — the default voice

Silence is a specified behaviour, not an absence of spec. The
full silence list is §5. If an interaction is not named in
§§1.1–1.3, it is silent. When in doubt, the Builder ships
silence — a missing tick is a shrug, a spurious buzz in the
archive is a lie.

---

## 2 · PRESS MECHANICS — SHARED PHYSICS

Stock `.clickable` gives an unbounded ripple and no press
character. Every tappable surface below replaces it with the
same shared physics, tuned per element in §4:

- **Bounded violet wash, never ripple.** The press state is the
  ruled `#493EA4` at ~8%, clipped to the row's own bounds
  between its hairlines. Ripple that bleeds past a hairline
  makes paper look like a button; the wash stays inside the
  furniture. No elevation, no shadow, anywhere — nothing in a
  drawer rises toward the thumb.
- **No scale, no translation on rows.** StoredRows and their
  hairlines are a ruled sheet; scaling a row breaks the rule
  lines against its neighbours. Rows answer with wash only.
  The two lawful exceptions, each priced in its section: the
  shelf row's 2 dp *give* (§4.4) and the filing word's 1 dp
  *sink* (§4.2) — both are the object's own metaphor (a drawer
  face moves; a stamp descends), never decoration.
- **Wash onset within one frame (~16 ms) of touch-down** on
  every surface. Press feedback that arrives after the finger
  lifts is worse than none — it reads as lag and trains the
  thumb to press twice, and a double press is the filing
  zone's worst enemy (§6).
- **Easing**: house curves only. Wash in on Curve A
  (`EasingA`, fast commitment); wash out on Curve A at the
  durations in §4. No overshoot, no spring, no bounce anywhere
  in this room — `MechanicalOvershoot` is forbidden in the
  Drawer. Finished work does not bounce.
- **Press-and-hold does nothing extra, anywhere.** No
  long-press menu, no long-press haptic, no repeat. A held
  thumb just holds the wash (or the stamp) down until it
  commits or cancels. SELECT is a dead verb here (§6 of the
  audit), and long-press is where select modes hide.

---

## 3 · (reserved — press specs live per element in §4)

---

## 4 · ELEMENT BY ELEMENT

### 4.1 The search entry line — SILENCE, and a line that barely moves

The search line is a question he asks the archive; asking
changes nothing stored, so it earns no haptic and almost no
motion.

- **Tap to focus: silence.** No tick. The answer is visual
  only: caret appears, the 1 dp bottom rule takes the violet
  at full strength, placeholder yields to his typing in the
  same Anonymous 12sp costume (the designer's cured defect).
  Press depth: **zero** — no wash (a wash would make the line
  a button), no dip, no sink. A ruled line deepens its rule;
  that is all.
- **Typing: silence from the app.** Per-key haptics are the
  keyboard's own system business; the app adds nothing — no
  tick per character, no tick when the result set changes, no
  difference in feel between a query that finds and a query
  that doesn't. Found and not-found must feel identical,
  because a search that *feels* successful or failed is grading
  his memory.
- **`NO STORED TASK UNDER "X"`: silence.** No error buzz, no
  sad double-tick. An empty result is a printed fact, delivered
  in print only.
- **Result rows** are StoredRows and feel exactly as §4.3,
  including the substep second line — the reason-line changes
  nothing about the touch.
- **Leaving search** is BACK (§4.6): one tick, the query
  clears. Clearing earns no haptic of its own — one press,
  one tick, even though two things happen (leave + clear).
- **Under seal**: search is enabled (audit §2) and feels
  exactly as above — which is to say, it makes no sound in the
  hand at all. If seal doctrine ever removes the line, there
  is nothing to feel; the painted-door ban is the designer's,
  and its feel corollary is: never a line that accepts a tap
  and answers with a refusal buzz. Refusal haptics do not exist
  in this room.

### 4.2 The filing zone — THE STAMP

Today's and tomorrow's lines: date left (print), REST · ILL ·
FAMILY right in violet, each word its own target (designer §4:
line ≥44 dp; this spec raises the *touch* spec in §6).

This is the only irreversible act on the screen and the only
thud. Its choreography is a stamp press, in three beats:

1. **Down — the stamp descends.** On touch-down on an
   undeclared word: the word sinks **1 dp** (translateY) and
   its violet deepens to full ink-violet over **90 ms**
   (`MonolithKin.PRESS_MS`, Curve A). No haptic yet — a stamp
   hovering over paper has not stamped. If the thumb holds,
   the stamp stays down, indefinitely, saying nothing. That
   held moment is the only confirmation dialog this act gets,
   and it is made of pressure, not furniture — the 2-tap law
   (Drawer → ILL → sleep) forbids a dialog, so the press
   itself must carry the weight *before* commit.
2. **Commit — the stamp lands.** On release inside the word:
   the **thud-stamp** (§1.3) fires at the commit call site,
   and the line retires *with* the thud, not after it: the two
   unchosen words fade out over 180 ms (Curve A), the declared
   word crosses violet → full ink `#34323B` over 260 ms,
   the sink returns to 0 as the word becomes print. One
   motion, one haptic, no overshoot, no flourish after — the
   retirement animation must be *finished and still* quickly,
   because a line that keeps moving after the thud reads as
   celebration.
3. **Cancel — the stamp lifts.** Drag off the word and release:
   sink and ink unwind over 150 ms (`PRESS_BACK` budget),
   **silence**, nothing filed. A stamp that buzzes when lifted
   unstamped would punish hesitation; hesitation before an
   irreversible word is correct behaviour and is never taxed.

- **Once filed, the line is dead to the touch — totally.**
  The retired line takes no press state, no wash, no haptic,
  no ripple: it is not `clickable` at all. Tapping the printed
  ILL at 1 AM, or a week later, feels exactly like tapping a
  day heading — like tapping paper. This deadness *is* the
  "forever": a toggle clicks both ways; a stamp's line never
  answers again. Equally: no error buzz for touching it.
  Print cannot be an error.
- **Idempotent under a shaky thumb.** The first commit retires
  the line in the same frame the thud fires, so a reflexive
  second tap lands on retired print and earns silence — it
  cannot un-file, re-file, or double-stamp. The stamp is
  physically incapable of firing twice for one day.
- **Neighbour safety.** The gaps between the three words are
  dead zones: a tap there is silence and nothing — see §6 for
  why the miss must fail to *nothing*, never to the nearest
  word. No nearest-word snapping, no expanded invisible
  targets overlapping the gaps. On this one surface, accuracy
  outranks speed, because the act cannot be taken back.
- **The two lines are independent.** Filing tomorrow does not
  touch today's line, its feel, or its availability; each line
  stamps, retires, and goes dead on its own.
- **Under seal**: per the designer's rule — either the words
  are live (and feel exactly as above) or the undeclared line
  renders as plain date print with no affordance (and feels
  like print, because it is not composed as a target at all).
  The forbidden middle — violet words that take a press and
  refuse — is forbidden in feel too: no press state, no buzz,
  no nothing-behind-the-glass.

### 4.3 Current-month StoredRows — the tick, on paper

- **Press**: bounded violet wash (§2) ramping in over **80 ms**
  (Curve A), no translation, no scale. The row is a line on a
  ruled sheet; the wash is a fingertip's shadow, nothing more.
- **Commit**: on release-inside, the **tick** (§1.1) fires with
  the navigation state change; the wash fades over 180 ms as
  DETAIL comes in on the house tab-in character (240 ms,
  `TAB_IN_MS` family) — the tick is timed to the *tap*, and
  DETAIL's arrival adds nothing (§0, one event one haptic).
- **Drag / scroll-through**: the wash cancels silently as the
  scroll takes the gesture. A browse down the month crossing
  ten rows fires ten nothings. This is the most-fired gesture
  on the screen and its feel is: the archive is inert under a
  travelling thumb, alive only to a deliberate tap.
- **Long-press**: nothing. No haptic at any hold duration, no
  menu, no selection, no reorder. If the stock long-press
  ripple behaviour survives anywhere it is a defect.
- **SPENT, the clock, the title** — all print, all inert: the
  whole row is one target with one feel; there are no
  sub-targets to hit or miss at 1 AM.

### 4.4 The MONTHS shelf — a drawer pull, NOT a button click

The distinction the brief demands, made physical. A button
click is instant, uniform, and weightless: down, click, done.
A drawer pull has *travel and resistance*: you take hold, the
face gives a little against the runner, the catch lets go, and
the drawer comes. The shelf row's feel is built from that
sequence:

- **Take hold (down)**: the wash does **not** snap in like a
  row's. It builds over **140 ms** to the same ~8% violet —
  slower, deeper-feeling, the visual equivalent of resistance —
  and the row's face **gives 2 dp** (translateY +2 dp toward
  the thumb's pull direction, i.e. down) over the same 140 ms,
  Curve A, and stops there. 2 dp is a give, not a slide: the
  drawer has moved on its runner by the width of its tolerance
  and no more. No haptic on down — the catch has not let go yet.
- **The pull commits (release-inside, after a real tap)**: the
  **detent** (§1.2) fires — catch, gap, seat — at the same
  moment MONTH is entered; the give snaps back as the view
  transitions in on the drawer's own kinematics (**280 ms
  SEAT**, `KIN-DRAWER DRAWER_OPEN_MS`, EasingSeat — the name
  predates this screen and finally means what it says). The
  felt story, end to end: *grip… resistance… the catch lets
  go… the drawer is out.* Roughly 180 ms of held press can
  precede it if he presses slowly; the detent never fires
  early, never fires on a schedule — it fires when *he* lets
  the pull go.
- **Abandoned pull**: drag off or scroll away mid-press — wash
  and give unwind together over 150 ms, silence, no detent,
  no navigation. A drawer pushed back in makes no sound worth
  recording. Scroll-past is the common case: the shelf lives
  inside the landing scroll, so nearly every touch on a shelf
  row is actually a scroll beginning; the pull must arm only
  on a tap that stays a tap (release inside, within tap slop,
  no scroll takeover). A detent fired by a scroll is the
  shelf's cardinal defect — it would tick off months as he
  passes them, counting by vibration again.
- **The MONTHS heading**: print. Not a target, no press, no
  feel. Collapsing the shelf is not a verb here.
- **The shelf row carries nothing else to press.** No chevron
  target, no long-press, no swipe on the row (no swipe
  anywhere in the Drawer — §4.7). The whole 48 dp row is the
  handle; a drawer handle you can miss is a broken handle.
- **Return**: MONTH's BACK (§4.6) lands the landing scrolled
  to this row (designer §3.6). The return itself is a tick,
  not a detent — putting a drawer back is lighter than pulling
  it out, and the asymmetry lets the thumb know which way it
  is travelling without looking.

### 4.5 MONTH view — same paper, honest scope

- **StoredRows in MONTH** feel *identical* to §4.3 — same wash,
  same tick, same cancel. One row, one feel, wherever it
  rests; a row that felt different in MONTH would imply MONTH
  is a different kind of place, and it is only an earlier shelf
  of the same drawer.
- **EARLIER**: the word is a small target with a small feel —
  ink deepens on press (no wash box; EARLIER is a word, not a
  row), and on commit the **tick** (§1.1) fires once as the
  next page of rows appends with a plain 260 ms (specA) fade —
  no stagger, no cascade down the new rows (a cascade is a
  little celebration), and no haptic per appended row. When
  EARLIER is tapped for the last page, the same tick fires and
  the word then retires from the foot — the retirement itself
  is silent. EARLIER never earns the detent: paging within a
  month is reading further, not crossing.
- **Scrolling MONTH**: silence, as §4.3.
- There is no pull gesture, no swipe-to-close, no edge drag in
  MONTH. BACK is the way back, and it feels like §4.6.

### 4.6 BACK — edge-flush, on down, the smallest tick

One costume (audit §2), one feel, every view — LANDING's,
MONTH's, SEARCH's, DETAIL's:

- **Target**: ~45 dp, **flush to the screen edge** — the hit
  rect extends to x = 0 with no page-padding dead strip before
  it. At 1 AM the thumb finds the edge of the phone, not a
  coordinate; the edge itself is the landmark, so the target
  must begin where the glass begins.
- **Fires on touch-DOWN**, the one down-fired haptic in the
  Drawer. Justification: BACK sits in the fixed header, never
  under a scroll gesture, so the §0 scroll-safety reason does
  not apply — and the benefit is real: the tick lands the
  instant the edge is touched, confirming receipt before the
  view has moved, which is exactly what a half-asleep thumb
  needs from the one control it uses blind.
- **The haptic**: the tick at its smallest — waveform
  `[0, 5, 4]` ms, amplitudes `[0, 120, 50]` (fallback 1 ms).
  Going back is the lightest act in the room, lighter than
  opening, and the hand should feel retreat cost less than
  advance.
- **Press visual**: no wash box (a boxed BACK would be a
  button standing in a records room). The glyph itself dips:
  ink to full strength and a 2 dp nudge toward the edge over
  90 ms, returning as the previous view seats (280 ms SEAT
  family for MONTH→LANDING; the tab-in character elsewhere).
  The view transition starts on down with the tick — BACK is
  the one surface where press, haptic, and navigation are a
  single instant, because BACK has no cancelled state worth
  choreographing: a press on BACK that slides off simply does
  not navigate and unwinds the nudge silently (the tick has
  already fired; a stray 9 ms tick on an abandoned edge press
  is an acceptable, specified cost — the alternative, firing
  on release, makes every return feel lagged).
- **System back** inside the Drawer (consumed one step per
  press, audit §2) **is** this BACK: same tick, same step. Two
  back doors with two feels would make one of them a stranger.
- **SEARCH's BACK** clears the query as it leaves: still one
  tick. The clearing is not a second event and earns nothing.

### 4.7 Task DETAIL — a page, read in silence

DETAIL is the purest surface in the room and its feel is its
proof: **nothing in DETAIL is tappable except BACK.**

- Title, provenance block (ESTIMATE with category), SPENT
  standing alone, substeps in one voice, FORGED at full ink —
  every element is print. Tapping any of it: no press state, no
  wash, no haptic, no ripple. Not "disabled-button" feel — the
  elements are not composed as targets at all. A page does not
  depress when read.
- Scrolling the page: silence.
- There are no gestures in DETAIL: no swipe-down to dismiss,
  no swipe between tasks, no pull. (Vega's Trawl is a priced
  proposal for the looks programme, explicitly not ruled; this
  spec adds no gesture layer, and if it is ever ruled in it
  returns here for a feel hearing first — a weighted archive
  gesture is precisely where per-day detents could turn mass
  into a count, and §0 forbids that conversion.)
- Leaving is BACK (§4.6) and only BACK, returning him to the
  row he was standing on (designer §3.6) with the one small
  tick. DETAIL entered: the tick was spent at the row (§4.3);
  DETAIL's own appearance adds silence.

### 4.8 The landing as a whole — arrival and rest

- **Entering the Drawer** (from wherever the deck sends him):
  silence. No arrival tick, no seat knock, no "you are in the
  archive" signal. Rooms do not announce themselves to the
  hand.
- **The STORED header, day headings, dates**: print, inert, no
  targets. Day headings in particular never take a press —
  they are not collapsible, not fileable, not anything.
- **At rest, forever: silence.** No idle haptic exists in this
  room — no heartbeat, no reminder pulse, no sealed-state hum.
  If the Drawer is open and untouched for an hour, it has made
  exactly as much vibration as a paper drawer: none.

---

## 5 · THE SILENCE LIST & FORBIDDEN VOICES

**Specified silence** (each is a decision, not an omission):
screen entry · all scrolling & flinging · row pass-under during
scroll · search focus · typing · results arriving · empty
result · day headings · MONTHS heading · DETAIL body (all of
it) · retired filing lines · filing cancel · shelf-pull cancel ·
EARLIER's retirement · sealed-state anything beyond the live
acts' own feel · long-press everywhere.

**Forbidden voices** — existing engine voices that must never
be spent in the Drawer, because each one *means* something
elsewhere and spending it here would import that meaning:

- `playCompletionPulse` — three softening ticks = the machine
  winding down pleased. In the archive that is a grade with a
  pulse. The filing stamp exists precisely so this is never
  borrowed for filing.
- `playHarmonicSeal` / `playSealThrow` — the seal is the app's
  gravest act; its voice anywhere in the Drawer would make
  filing feel like sealing and browsing feel armed. The Throw
  is bit-identical and single-purpose by law; untouched.
- `playMechanicalLatch` as-is — its amplitudes (215/150) snap
  machinery. The shelf detent is a *new, softer pattern in its
  family* (§1.2), not a call to it.
- `playTearPulse` — the counterfoil's voice; nothing tears here.
- **Any sound at all.** The three Drawer voices are haptic-only
  (§1). The engine's public voices pair sound by construction;
  the Drawer voices must be built on `triggerWaveform`
  directly, with no `enqueueTransient`. Feel only, per brief —
  and at 1 AM, a ticking drawer is a drawer that wakes the house.

**Degradation**: haptics off (system or the engine's
`hapticsEnabled` fact) degrades every spec above to its visual
half with timings unchanged — press depths, wash ramps, the
stamp's sink-and-retire, the shelf's give all still happen.
Feel is a layer, never a load-bearing wall: no information in
this room is carried by vibration alone except the blind
confirmation of §6, and even that has its visual twin (the
line visibly retired, the month visibly open).

---

## 6 · THE 1 AM CASE — half asleep, one thumb, no misses

The design case, walked as Muede walks it (audit §4): he is
ill, it is ~01:00, the errand is *Drawer → ILL → sleep*,
two taps, eyes barely open, right thumb only.

- **The filing words sit in the thumb's country.** The words
  are right-aligned on their line; a right thumb falling
  naturally onto the filing zone lands on words, not dates.
  Touch spec per word: **full line height (≥48 dp) and ≥60 dp
  wide**, hit rect spanning the whole line height so vertical
  aim can be sloppy by half a line and still land. Horizontal
  gaps between words stay dead (§4.2): a miss lands in a gap
  and the outcome is *nothing happened* — silence, words still
  violet, he can tap again. The failure mode of a miss is
  retry, never a wrong word filed forever. This is the one
  place the spec refuses generous snapping, deliberately.
- **Confirmation he can feel without looking.** If the tap
  lands: the thud-stamp is unlike anything else his thumb will
  meet that night (nothing else in the errand ticks that
  heavy), and unlike the tick of the Drawer destination tap
  that brought him here. He does not need to open his eyes to
  verify: *heavy = filed*. If he feels nothing, nothing was
  filed, and the words are still there. The haptic vocabulary
  is the verification UI.
- **The double-tap reflex cannot hurt him.** Half-asleep
  thumbs double-tap. First tap files and retires the line in
  the same frame; the second lands on dead print (§4.2). No
  toggle-off, no second stamp, no dialog asking what he meant.
- **The other 1 AM errand** (find last week's finished thing):
  shelf pull (§4.4) — a 48 dp full-width handle, committable
  by a tap anywhere on the row, cancellable by the scroll he
  will probably start first; then a row tick into DETAIL whose
  SPENT he can read or, just as lawfully, whose row he already
  read on the way. BACK home is an edge-flush target he finds
  by the glass's edge, confirmed on down (§4.6) — the one
  control he can operate by pure proprioception.
- **Nothing in the room can startle.** The loudest thing the
  Drawer can do to a sleeping-adjacent man is a 20 ms thud
  through the phone's motor when *he* files a word. No buzz
  greets him, no buzz scolds a miss, no buzz marks a result.
  Silence is also a sleep feature.

---

## 7 · PRICED DELTAS — beyond stock Compose

Stock today (measured, §0): ripple-only `.clickable`, no
haptics, no press states in `DrawerScreen.kt`. Everything
above that line is delta. Rough prices, builder-days:

| # | Delta | Rough size | Price |
|---|---|---|---|
| 1 | Three haptic-only engine voices (tick / detent / stamp) on existing `triggerWaveform`, no transient enqueued; pre-26 fallbacks wired | ~60–90 LOC in `TactileSoundEngine` + call-site enum/seam | ~0.5 d |
| 2 | Shared bounded-wash indication + `InteractionSource` press tracking replacing stock ripple, applied to rows / shelf / EARLIER / filing words (BACK keeps glyph-dip variant) | ~120–180 LOC | ~0.5–1 d |
| 3 | Filing choreography: 90 ms sink, commit/cancel on release-inside vs drag-off, retirement crossfade bound to the thud's call site, retire-in-same-frame idempotency guard | ~40–60 LOC beyond the ruled state change | ~0.25–0.5 d |
| 4 | Shelf pull: 140 ms wash ramp + 2 dp give, tap-vs-scroll disambiguation inside the landing `LazyColumn` (arm on tap, cancel on scroll takeover), give unwinding on cancel | ~80–120 LOC — the genuinely fiddly item; scroll/tap arbitration is where the days go | ~1–1.5 d |
| 5 | BACK: edge-flush 45 dp rect, down-fired shaded tick, glyph dip; system-back routed through the same call so both backs share the voice | ~20–30 LOC | in #2's day |
| 6 | Feel tests: call-site unit tests (one tap = one haptic, retired line = no call, scroll = no call) + device pass on the §8 checks | ~80–120 LOC tests | ~0.5 d |

**Total: ~400–600 LOC, ≈ 2.5–3.5 builder-days.** Zero-cost
(by subtraction, not addition): removing `clickable` from
retired lines / headings / DETAIL body; the silence list is
mostly the discipline of *not* wiring things.

**Cut line, if the wave is squeezed**: #4 can degrade to
detent-on-tap with the row's standard 80 ms wash (saves
~1 day) — the detent voice survives, the pull's *travel* does
not, and the shelf becomes a heavy button instead of a drawer.
Recorded as the acceptable cut; everything else is load-
bearing — cut #3 and filing feels like a toggle, cut #1 and
the room is stock ripple with good intentions, cut #6 and
nobody has proven the scroll doesn't tick.

**Explicitly not priced because not proposed**: any gesture
layer (Trawl), any sound design, any per-look re-voicing.
The Drawer has one feel in every look; a records room does
not change its acoustics with the wallpaper.

---

## 8 · ACCEPTANCE — how LADE checks the feel without a spec sheet

On device, eyes open once, then shut:

1. Scroll the landing top to bottom ten times, fast and slow:
   **zero** haptics, zero washes sticking.
2. Eyes shut: open a row, go back, open a shelf month, go
   back, file tomorrow ILL. Four events, three voices, in
   order tick → tick → detent → (back) tick → stamp, and the
   stamp is identifiable blind, first try, as *the heavy one*.
3. Press ILL, drag off, release: nothing filed, nothing felt,
   words still violet.
4. Tap the retired ILL line five times: five nothings. It is
   print now.
5. Type a search that finds nothing: no buzz. Type one that
   finds: same no buzz.
6. Hold any row for five seconds: nothing happens, at any
   point, including release.
7. Leave the Drawer open, phone in hand, one minute: the room
   at rest vibrates exactly never.

If all seven pass, the Drawer feels like what it is: a quiet
piece of furniture that answers a deliberate thumb in exact
proportion to what the thumb just did — and otherwise keeps
the stillness the work inside it has earned.

— Feel 1 — Touch (issue #8 cell)
